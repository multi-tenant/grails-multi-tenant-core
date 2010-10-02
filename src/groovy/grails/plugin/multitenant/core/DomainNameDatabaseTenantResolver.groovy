package grails.plugin.multitenant.core;

import com.infusion.util.event.groovy.GroovyEventBroker
import com.infusion.util.domain.event.HibernateEvent
import grails.plugin.multitenant.core.util.TenantUtils
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.apache.log4j.Logger

/**
 * Implementation that looks up tenantIds from a local table DomainTenantMap that stores domain name to tenantId mappings
 * in support of the single or multi tenant mode.  The data will either be looked up from a configuration database or from the default
 * datasource as defined in the datasource config file.  It is not a good idea to store this data in every client database.
 */
public class DomainNameDatabaseTenantResolver extends BaseDomainNameTenantResolver implements ApplicationContextAware {
  /** This is a logger for logging status and error messages.            */
  private static Logger log = Logger.getLogger(getClass());

  /**
   * Used for listening to save events for DomainTenantMap domain class
   */
  GroovyEventBroker eventBroker
  /**
   * This is the injected context that is used to look up the DomainTenantMap Bean.
   */
  ApplicationContext applicationContext
  /**
   * This will initialize the data for the application by loading the domain name tenant map data.  This data is loaded
   * from tenant 0 which could either be a separate configuration database or the default datasource as defined
   * in the datasources config file.  This insures we don't end up with cyclic dependencies and that we don't have to store
   * the configuration data with each client.
   */
  void initialize() {

    // If this is single tenant per database then we need to hard code to tenant 0 and use a configuration
    // database or the default datasouce.
    if (ConfigurationHolder.config.tenant.mode == "singleTenant" &&
            ConfigurationHolder.config.tenant.configDB == true) {
      log.info "Initializing Domain Name Map for Single Tenant with Config DB support"
      TenantUtils tenantUtils = applicationContext.getBean("tenantUtils")
      tenantUtils.doWithTenant(0) {
        loadDomainTenantMap()
      }
    }
    else {
      log.info "Initializing Domain Name Map for Multi Tenant support or single tenant without config DB support"
      loadDomainTenantMap()
    }
  }
  /**
   * This will load the hosts data which contains the map between domain and tenant.
   */
  void loadDomainTenantMap() {
    hosts.clear()
    log.info "Loading Domain Name to Tenant information from the database object DomainTenantMap"
    // Load the tenant information for the domain to tenant id from the database.
    def list = applicationContext.getBean("tenant.DomainTenantMap").findAll("from tenant.DomainTenantMap");
    list.each {map ->
      if (log.isDebugEnabled()) log.debug "Domain->Tenant: ${map.domainName}->${map.mappedTenantId}"
      hosts.put(map.domainName, map.mappedTenantId)
    }
  }
  /**
   * The event broker listens for every time a record is saved, then calls a refresh on the
   * list of hosts whenever the DomainTenantMap object is changed via a save or update.
   */
  void setEventBroker(GroovyEventBroker inEventBroker) {
    if (inEventBroker != null) {
      inEventBroker.subscribe("hibernate.${HibernateEvent.saveOrUpdate}.DomainTenantMap") {
        event, broker ->
        log.info "DomainTenantMap was changed via a save or update.  Reloading the Domain to Tenant information from the database."
        this.initialize();
      }
    }
  }
}
