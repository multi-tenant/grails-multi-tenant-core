import com.infusion.tenant.hibernate.TenantEventHandler
import com.infusion.tenant.hibernate.TenantSessionFactory
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

class MultiTenantGrailsPlugin {
    def version = 0.1 
    def dependsOn = [falconeUtil:0.1]
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

    //Bean container for all multi-tenant beans
    tenantBeanContainer(TenantBeanContainer)

    //The post-processor does bean modification for multi-tenant beans
    tenantBeanFactoryPostProcessor(TenantBeanFactoryPostProcessor)

    //This registers hibernate events that force filtering on domain classes
    tenantEventHandler(TenantEventHandler) {
      sessionFactory = ref("sessionFactory")
    }

    //Default tenant resolver is a property file.  This can be easily overridden
    tenantResolver(DomainNamePropertyTenantResolver)
  }

  def doWithApplicationContext = {GrailsApplicationContext applicationContext ->
      //Register event listener on save for domain tenant map
  }

  def doWithWebDescriptor = {xml ->
  }

  def doWithDynamicMethods = {ctx ->
    //Add a nullable contraint for tenantId.
    application.domainClasses.each {DefaultGrailsDomainClass domainClass ->
      domainClass.constraints?.get("tenantId")?.applyConstraint(ConstrainedProperty.NULLABLE_CONSTRAINT, true);
      domainClass.clazz.metaClass.beforeInsert = {
        if(tenantId == null) tenantId = 0
      }
    }
  }

  def onChange = {event ->
  }

  def onConfigChange = {event ->
  }
}
