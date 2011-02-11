package grails.plugin.multitenant.core

import com.infusion.util.event.groovy.GroovyEventBroker
import com.infusion.util.domain.event.HibernateEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.apache.log4j.Logger
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Cache
import net.sf.ehcache.Element
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Implementation that looks up tenantIds from a local table DomainTenantMap that stores domain name to tenantId mappings
 * in support of the single or multi-tenant mode.  The data will either be looked up from a configuration database on the
 * default datastore.
 */
public class DomainNameDatabaseTenantResolver extends BaseDomainNameTenantResolver implements ApplicationContextAware
{
  /** This is a logger for logging status and error messages.                             */
  private static Logger log = Logger.getLogger(DomainNameDatabaseTenantResolver.class)

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
   * from tenant 0 which could either be a separate configuration database or the default data source as defined
   * in the data sources config file.  This insures we don't end up with cyclic dependencies and that we don't have to store
   * the configuration data with each client.
   */
  void initialize()
  {
    log.info "Initializing Domain Name Map for Multi Tenant support"
    loadDomainTenantMap()
  }

  /**
   * This will load the hosts data which contains the map between domain and tenant. It will also load the database
   * objects in to another cache that can be accessed to retrieve additional data such as name etc.
   */
  void loadDomainTenantMap()
  {
    hosts.clear()
    Cache tenantDataCache = CacheManager.getInstance()?.getCache(TENANT_DATA_CACHE_NAME)
    if (tenantDataCache == null)
    {
      // This insures the cache has no limit and items are never removed from the cache.
      CacheManager.getInstance().addCache(new Cache(TENANT_DATA_CACHE_NAME, 1000, true, true, 120, 120))
      tenantDataCache = CacheManager.getInstance()?.getCache(TENANT_DATA_CACHE_NAME)
    }
    else
    {
      tenantDataCache.removeAll()
    }
    // See if there is a custom data source bean name
    def domainTenantBeanName = ConfigurationHolder.config.tenant.domainTenantBeanName
    if (domainTenantBeanName == null || domainTenantBeanName?.size() == 0)
    {
      domainTenantBeanName = "tenant.DomainTenantMap"
    }
    // Load the tenant information for the domain to tenant id from the database.
    def list = applicationContext.getBean(domainTenantBeanName).list()
    log.info "Loading " + list?.size() + " Domain Name to Mapped Tenant Id entries from the database object " + domainTenantBeanName
    list.each {map ->
      if (log.isDebugEnabled())
      {
        log.debug "Domain->Tenant: ${map.domainName}->${map.mappedTenantId}"
      }
      hosts.put(map.domainName, map.mappedTenantId)
      tenantDataCache.put(new Element(map.mappedTenantId, map))
    }
  }
  /**
   * The event broker listens for every time a record is saved, then calls a refresh on the
   * list of hosts whenever the DomainTenantMap object is changed via a save or update.
   */
  void setEventBroker(GroovyEventBroker inEventBroker)
  {
    if (inEventBroker != null)
    {
      inEventBroker.subscribe("hibernate.${HibernateEvent.saveOrUpdate}.DomainTenantMap") {
        event, broker ->
        log.info "DomainTenantMap was changed via a save or update.  Reloading the Domain to Tenant information from the database."
        this.initialize()
      }
    }
  }
}
