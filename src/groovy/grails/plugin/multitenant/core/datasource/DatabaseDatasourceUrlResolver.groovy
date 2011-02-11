package grails.plugin.multitenant.core.datasource

import com.infusion.util.event.groovy.GroovyEventBroker
import org.springframework.context.ApplicationContext
import org.apache.log4j.Logger
import com.infusion.util.domain.event.HibernateEvent
import org.springframework.context.ApplicationContextAware
import grails.plugin.multitenant.core.util.TenantUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder
/**
 *  This class supports the loading of data source information in support of the single tenant mode.  It supports
 * the mode where tenant specific data sources are stored in a database either in the config database as configured
 * in the config properties or the database defined by the default data source in the data sources config file.
 */
public class DatabaseDatasourceUrlResolver implements DataSourceUrlResolver, ApplicationContextAware
{

  /** This is a logger for logging status and errors.                      */
  private static Logger log = Logger.getLogger(DatabaseDatasourceUrlResolver.class);

  /**
   * Used for listening to save events for DomainTenantMap domain class.  If the DomainTenantMap object is saved
   * in the application the data is then reloaded by the plugin to pick up new entries and the bean cache is cleared.
   */
  GroovyEventBroker eventBroker
  /**
   * This is the application context that is used to load the data from the database.
   */
  ApplicationContext applicationContext
  /**
   * Caches a map of tenantId to dataSource entries
   */
  Map<Integer, String> dataSources = [:]
  /** This tracks the load status for the data.                      */
  Status status = Status.NotLoaded
  /**
   * This will return the data source for a given tenant id.
   * @param inTenantId The tenant id you wish to retrieve the data source for.
   * @return The data source name string for the input tenant.
   */
  public synchronized String getDataSourceUrl(Integer inTenantId)
  {
    switch (status)
    {
      case Status.Loading:
        return null;
        break;
      case Status.NotLoaded:
        init()
      case Status.Loaded:
        return dataSources.get(inTenantId)
        break;
    }
  }
  /** This method allows you to reset the data and forces the data to be reloaded from the database on access.                      */
  public synchronized void reset()
  {
    this.status = Status.NotLoaded;
  }
  /**
   * This will initialize this object by loading the initial data set from the database.   The tenant is set to 0 so that
   * we can force it to use a fixed tenant to load this data otherwise you end up in a circular reference.  The data
   * for the data sources should be stored in a configuration database as defined in the config database and not in each
   * clients database for obvious security reasons however the system does support loading that data from the
   * default data source for testing or other reasons.
   */
  void init()
  {
    log.info "Initializing Data Source Map for Multi Tenant support"
    // If this is single tenant we have to use tenant 0 to avoid circular reference.
    // and it will use the default data source.
    if (ConfigurationHolder.config.tenant.mode == "singleTenant")
    {
      TenantUtils tenantUtils = applicationContext.getBean("tenantUtils")
      // Do this with tenant 0 so we don't end up in a cyclic loop and can support a configuration database.  The Resolver
      // will deal with what to do with tenant 0
      tenantUtils.doWithTenant(0) {
        loadDataSourceTenantMap()
      }
    }
  }
  /**
   * This will load the data source tenant map that maps the tenant id to the data source from a database.
   *
   */
  void loadDataSourceTenantMap()
  {
    // See if there is a custom data source bean name
    def dataSourceBeanName = ConfigurationHolder.config.tenant.dataSourceBeanName
    if (dataSourceBeanName == null || dataSourceBeanName?.size() == 0)
    {
      dataSourceBeanName = "tenant.DataSourceTenantMap"
    }
    // If the data is already loaded don't reload it.
    if (status != Status.NotLoaded) return
    status = Status.Loading
    // Clear out the map of tenant to data source
    dataSources.clear();
    //This will load all domain tenants, regardless of which tenant they're for
    def list = applicationContext.getBean(dataSourceBeanName).list();
    log.info "Loading " + list?.size() + " Mapped Tenant Id to DataSource entries from the database object " + dataSourceBeanName
    list.each {map ->
      if (log.isDebugEnabled()) log.debug "Tenant->DataSource: ${map.mappedTenantId}->${map.dataSource}"
      dataSources.put(map.mappedTenantId, map.dataSource)
    }
    this.status = Status.Loaded
  }
  /**
   * The event broker listens for every time a record is saved, then calls a refresh on the
   * list of hosts if the DataSourceTenantMap has changed.
   * @param inEventBroker - The event broker that allows us to register to listen to Hibernate events.
   */
  public void setEventBroker(GroovyEventBroker inEventBroker)
  {
    if (inEventBroker != null)
    {
      // Subscribe to the save or update event for the DataSourceTenantMap object
      inEventBroker.subscribe("hibernate.${HibernateEvent.saveOrUpdate}.DataSourceTenantMap") {
        event, broker ->
        log.info "DataSourceTenantMap was changed via a save or update.  Reloading the Tenant to DataSource information from the database."
        reset();
      }
    }
  }

}