import grails.plugin.multitenant.core.CurrentTenantThreadLocal
import grails.plugin.multitenant.core.DomainNameDatabaseTenantResolver
import grails.plugin.multitenant.core.DomainNamePropertyTenantResolver
import grails.plugin.multitenant.core.datasource.PropertyDataSourceUrlResolver
import grails.plugin.multitenant.core.datasource.TenantDataSourcePostProcessor
import grails.plugin.multitenant.core.hibernate.TenantEventHandler
import grails.plugin.multitenant.core.spring.TenantBeanContainer
import grails.plugin.multitenant.core.MultiTenantFilter
import grails.plugin.multitenant.core.spring.TenantBeanFactoryPostProcessor
import grails.plugin.multitenant.core.util.TenantUtils
import com.infusion.util.domain.event.hibernate.CriteriaContext
import grails.plugin.multitenant.core.util.log.log.MultiTenantLogLayout
import org.apache.log4j.Appender
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.hibernate.Query
import org.hibernate.criterion.Expression
import org.hibernate.type.IntegerType
import grails.plugin.multitenant.core.datasource.DatabaseDatasourceUrlResolver
import util.ConfigHelper
import org.codehaus.groovy.grails.plugins.GrailsPlugin
import org.codehaus.groovy.grails.plugins.DefaultGrailsPluginManager
import grails.plugin.multitenant.core.DomainNameDatabaseTenantResolver
import grails.plugin.multitenant.core.DomainNamePropertyTenantResolver
import grails.plugin.multitenant.core.CurrentTenantThreadLocal

class MultiTenantCoreGrailsPlugin
{
  def version = "1.0.3"
  def grailsVersion = "1.3.0 > *"
  def dependsOn = [falconeUtil: "1.0"]
  def author = "Eric Martineau, Scott Ryan"
  def authorEmail = "ericm@infusionsoft.com, scryan@codehaus.org"
  def title = "Multi-Tenant Plugin(Core)"
  def description = ''' Allows for managing data for mutiple 'tenants' in a single database by using a tenantId column
                          for each domain object.  Also handles the proxying of spring beans for a multi-tenant environment. '''
  // URL to the plugin's documentation
  def documentation = "http://grails.org/multi-tenant-core"
  def doWithSpring = {
    def requestResolverType = ConfigHelper.get("config") {it.tenant.resolver.request.dns.type}
    //Utility class that contains tenant resolver
    tenantUtils(TenantUtils) {
      currentTenant = ref("currentTenant")
    }
    if (ConfigurationHolder.config.tenant.mode == "singleTenant")
    {

      //Put switching datasource here
      tenantDataSourcePostProcessor(TenantDataSourcePostProcessor)
      if (
        ConfigurationHolder.config.tenant.datasourceResolver.type == "config" ||
                ConfigurationHolder.config.tenant.datasourceResolver.type.size() == 0
      )
      {

        dataSourceUrlResolver(PropertyDataSourceUrlResolver)

      } else
      {

        dataSourceUrlResolver(DatabaseDatasourceUrlResolver) {
          eventBroker = ref("eventBroker")
        }
      }

    } else
    {

      //This registers hibernate events that force filtering on domain classes
      //In single tenant mode, the records are automatically filtered by different
      //data sources.
      tenantEventHandler(TenantEventHandler) {
        sessionFactory = ref("sessionFactory")
        currentTenant = ref("currentTenant")
      }
    }
    //Bean container for all multi-tenant beans
    tenantBeanContainer(TenantBeanContainer) {
      currentTenant = ref("currentTenant")
    }

    //The post-processor does bean modification for multi-tenant beans
    tenantBeanFactoryPostProcessor(TenantBeanFactoryPostProcessor)

    def resolverType = ConfigHelper.get("request") {it.tenant.resolver.type}
    if (resolverType == "request")
    {
      //This implementation
      currentTenant(CurrentTenantThreadLocal) {
        eventBroker = ref("eventBroker")
      }


      if (requestResolverType == "config")
      {
        //Default tenant resolver is a property file.  This can be easily overridden
        tenantResolver(DomainNamePropertyTenantResolver)
      } else if (ConfigurationHolder.config.tenant.resolver.request.dns.type == "db")
      {
        tenantResolver(DomainNameDatabaseTenantResolver) {
          eventBroker = ref("eventBroker")
        }
      }
    }

    //This bean adds the current tenantId to all logs
    multiTenantLogLayout(MultiTenantLogLayout) {
    }
  }

  def doWithEvents = {
    ctx ->
    if (ConfigurationHolder.config.tenant.mode != "singleTenant")
    {

      //Listen for criteria created events
      hibernate.criteriaCreated("tenantFilter") {
        CriteriaContext context ->
        boolean hasAnnotation = TenantUtils.isAnnotated(context.entityClass)
        if (context.entityClass == null || hasAnnotation)
        {
          final Integer tenant = ctx.currentTenant.get();
          context.criteria.add(Expression.eq("tenantId", tenant));
        }
      }

      //Listen for query created events
      hibernate.queryCreated("tenantFilter") {
        Query query ->
        for (String param: query.getNamedParameters())
        {
          if ("tenantId".equals(param))
          {
            query.setParameter("tenantId", ctx.currentTenant.get(), new IntegerType());
          }
        }
      }
    }
  }

  def doWithApplicationContext = {GrailsApplicationContext applicationContext ->
    def logValue = ConfigHelper.get(true) {it.tenant.log}
    if (logValue)
    {
      log.info "Setting up multi-tenant logging format"
      Enumeration<Appender> appenders = Logger.getRootLogger().getAllAppenders()
      if (appenders != null)
      {
        while (appenders.hasMoreElements())
        {
          appenders.nextElement().setLayout(applicationContext.multiTenantLogLayout)
        }
      }
    }
  }

  def doWithWebDescriptor = {xml ->
    def resolverFromConfig = ConfigHelper.get("request") {it.tenant.resolver.type}
    if (resolverFromConfig == "request")
    {

      //Add filter definition to web.xml
      def filterElements = xml.'filter'[0]
      filterElements + {
        'filter' {
          'filter-name'("MultiTenantFilter")
          'filter-class'(MultiTenantFilter.class.getName())
        }
      }

      //This is what we'll be adding filter mappings to
      def filterMappingElements = xml.'filter-mapping'[0]

      //Look in plugins for urls patterns
      DefaultGrailsPluginManager grailsManager = (DefaultGrailsPluginManager) manager;
      grailsManager.getAllPlugins().each {
        GrailsPlugin plugin ->
        if (plugin.getInstance().getProperties().containsKey("multiTenantFilterUrls"))
        {
          List urls = plugin.getInstance().multiTenantFilterUrls
          urls.each {url ->
            log.info "Adding ${url} to multitenant request filter mappings from ${plugin}"
            filterMappingElements + {
              'filter-mapping' {
                'filter-name'("MultiTenantFilter")
                'url-pattern'("${url}")
              }
            }
          }
        }
      }

      def urls = ConfigHelper.get(null) {it.tenant.filterUrls}
      urls?.each {url ->
        log.info "Adding ${url} to multitenant request mappings from Config.groovy"
        filterMappingElements + {
          'filter-mapping' {
            'filter-name'("MultiTenantFilter")
            'url-pattern'("${url}")
          }
        }
      }

      log.info "Finished mapping filter urls"
    }
  }

  def doWithDynamicMethods = {ctx ->

    if (ConfigurationHolder.config.tenant.mode != "singleTenant")
    {
      //Add a nullable contraint for tenantId.
      application.domainClasses.each {DefaultGrailsDomainClass domainClass ->
        domainClass.constraints?.get("tenantId")?.applyConstraint(ConstrainedProperty.NULLABLE_CONSTRAINT, true);
        domainClass.clazz.metaClass.beforeInsert = {
          if (delegate.respondsTo("tenantId") && tenantId == null)
          { tenantId = 0 }
        }
      }
    }

    TenantUtils.ready = true
  }


}
