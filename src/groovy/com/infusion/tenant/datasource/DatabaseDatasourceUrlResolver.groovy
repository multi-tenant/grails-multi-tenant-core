package com.infusion.tenant.datasource

import com.infusion.util.event.groovy.GroovyEventBroker
import org.springframework.context.ApplicationContext
import org.apache.log4j.Logger
import com.infusion.util.domain.event.HibernateEvent
import org.springframework.context.ApplicationContextAware
import com.infusion.tenant.util.TenantUtils

/**
 * Created by IntelliJ IDEA.
 * User: eric
 * Date: Jun 17, 2009
 * Time: 8:24:31 PM
 * To change this template use File | Settings | File Templates.
 */

public class DatabaseDatasourceUrlResolver implements DataSourceUrlResolver, ApplicationContextAware {


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

  Status status = Status.NotLoaded

  public synchronized String getDataSourceUrl(Integer tenantId) {
    if (!TenantUtils.ready) {
      return null;
    }
    switch (status) {
      case Status.Loading:
        return null;
        break;
      case Status.NotLoaded:
        init()
      case Status.Loaded:
        return dataSources.get(tenantId)
        break;
    }
  }

  public synchronized void reset() {
    this.status = Status.NotLoaded;
  }

  void init() {
    if (status != Status.NotLoaded) return
    status = Status.Loading
    log.info "Reloading datasource urls from the database"
    dataSources.clear();
    //This will load all domain tenants, regardless of which tenant they're for
    def list = applicationContext.getBean("tenant.DataSourceTenantMap").findAll("from tenant.DataSourceTenantMap");
    list.each {map ->
      log.debug "Tenant->DataSource: ${map.mappedTenantId}->${map.dataSource}"
      dataSources.put(map.mappedTenantId, map.dataSource)
    }
    this.status = Status.Loaded
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