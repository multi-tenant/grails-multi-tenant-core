import com.infusion.tenant.hibernate.TenantEventHandler
import com.infusion.util.domain.event.hibernate.InterceptableSessionFactory
import com.infusion.tenant.spring.TenantBeanContainer
import com.infusion.tenant.spring.TenantMethodInterceptor
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.commons.spring.BeanConfiguration
import org.codehaus.groovy.grails.commons.spring.DefaultRuntimeSpringConfiguration
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext
import org.codehaus.groovy.grails.commons.spring.RuntimeSpringConfiguration
import org.hibernate.SessionFactory
import org.springframework.aop.framework.ProxyFactoryBean
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.codehaus.groovy.grails.validation.NullableConstraint
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import com.infusion.tenant.spring.TenantBeanFactoryPostProcessor
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import com.infusion.tenant.DomainNamePropertyTenantResolver
import com.infusion.util.event.spring.InterceptableSessionFactoryPostProcessor
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.infusion.tenant.datasource.TenantDataSourcePostProcessor
import com.infusion.tenant.datasource.PropertyDataSourceUrlResolver
import org.apache.log4j.Logger
import com.infusion.util.log.MultiTenantLogLayout
import org.apache.log4j.Appender
import org.hibernate.Criteria
import org.hibernate.Query
import org.hibernate.type.IntegerType
import org.hibernate.criterion.Expression
import com.infusion.tenant.CurrentTenantThreadLocal

class MultiTenantGrailsPlugin {
  def version = 0.5
  def dependsOn = [falconeUtil: 0.3]
  def author = "Eric Martineau"
  def authorEmail = "ericm@infusionsoft.com"
  def title = "Multi-Tenant Plugin"
  def description = '''\\
Allows for managing data for mutiple 'tenants' in a single database by using a tenantId column for each domain object.  Also handles
the proxying of spring beans for a multi-tenant environment.
'''
  // URL to the plugin's documentation
  def documentation = "http://grails.org/MultiTenant+Plugin"

  def doWithSpring = {

    currentTenant(CurrentTenantThreadLocal)

    if (ConfigurationHolder.config.tenant.mode == "singleTenant") {

      //Put switching datasource here
      tenantDataSourcePostProcessor(TenantDataSourcePostProcessor)

      //This is the default - can be overridden
      dataSourceUrlResolver(PropertyDataSourceUrlResolver)

    } else {

      //This registers hibernate events that force filtering on domain classes
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

    //Default tenant resolver is a property file.  This can be easily overridden
    tenantResolver(DomainNamePropertyTenantResolver)

    multiTenantLogLayout(MultiTenantLogLayout) {
      currentTenant = ref("currentTenant")
    }


  }

  def doWithEvents = {
    ctx ->
    if (ConfigurationHolder.config.tenant.mode != "singleTenant") {
      hibernate.criteriaCreated("tenantFilter") {
        Criteria criteria ->
        final Integer tenant = ctx.currentTenant.get();
        if (tenant != null && tenant > 0) {
          criteria.add(Expression.eq("tenantId", tenant));
        }
      }

      "hibernate.queryCreated"("tenantFilter") {
        Query query ->
        for (String param: query.getNamedParameters()) {
          if ("tenantId".equals(param)) {
            query.setParameter("tenantId", ctx.currentTenant.get(), new IntegerType());
          }
        }
      }
    }

  }

  def doWithApplicationContext = {GrailsApplicationContext applicationContext ->
    Appender appender = Logger.getRootLogger().getAppender("stdout")
    appender.setLayout(applicationContext.multiTenantLogLayout)
  }

  def doWithWebDescriptor = {xml ->
  }

  def doWithDynamicMethods = {ctx ->
    if (ConfigurationHolder.config.tenant.mode != "singleTenant") {
      //Add a nullable contraint for tenantId.
      application.domainClasses.each {DefaultGrailsDomainClass domainClass ->
        domainClass.constraints?.get("tenantId")?.applyConstraint(ConstrainedProperty.NULLABLE_CONSTRAINT, true);
        domainClass.clazz.metaClass.beforeInsert = {
          if (tenantId == null) tenantId = 0
        }
      }
    }
  }


}
