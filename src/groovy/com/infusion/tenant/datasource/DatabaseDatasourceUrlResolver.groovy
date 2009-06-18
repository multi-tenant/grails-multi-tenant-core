package com.infusion.tenant.datasource

import com.infusion.util.event.groovy.GroovyEventBroker
import org.springframework.context.ApplicationContext
import org.apache.log4j.Logger
import com.infusion.util.domain.event.HibernateEvent

/**
 * Created by IntelliJ IDEA.
 * User: eric
 * Date: Jun 17, 2009
 * Time: 8:24:31 PM
 * To change this template use File | Settings | File Templates.
 */

public class DatabaseDatasourceUrlResolver implements DataSourceUrlResolver {


  private static Logger log = Logger.getLogger(getClass());

  /**
   * Used for listening to save events for DomainTenantMap domain class
   */
  GroovyEventBroker eventBroker
  ApplicationContext applicationContext

  /**
   * Caches a map of tenantId to dataSource
   */
  Map<Integer, String> dataSources = [:]
  boolean loaded

  public synchronized String getDataSourceUrl(Integer tenantId) {
    if (!loaded) {
      init()
    }
    return dataSources.get(tenantId);
  }

  public synchronized void reset() {
    this.loaded = false;
  }

  void init() {
    log.info "Reloading datasource urls from the database"
    dataSources.clear();
    //This will load all domain tenants, regardless of which tenant they're for
    def list = applicationContext.getBean("tenant.DataSourceTenantMap").findAll("from tenant.DataSourceTenantMap");
    list.each {map ->
      log.debug "Tenant->DataSource: ${map.mappedTenantId}->${map.dataSource}"
      dataSources.put(map.mappedTenantId, map.dataSource)
    }
    this.loaded = true
  }

  /**
   * The event broker listens for every time a record is saved, then calls a refresh on the
   * list of hosts.
   */
  public void setEventBroker(GroovyEventBroker eventBroker) {
    if (eventBroker != null) {
      eventBroker.subscribe("hibernate.${HibernateEvent.saveOrUpdate}.DataSourceTenantMap") {
        event, broker ->
        log.info "DataSourceTenantMap Saved, mark cache as dirty."
        reset();
      }
    }
  }

}