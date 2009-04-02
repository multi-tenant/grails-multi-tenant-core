package com.infusion.tenant;

import tenant.DomainTenantMap
import com.infusion.util.event.EventBroker
import com.infusion.util.event.groovy.GroovyEventBroker
import com.infusion.util.domain.event.HibernateEvent;

/**
 * Implementation that looks up tenantIds from a local table DomainTenantMap that stores domain name to tenantId mappings. 
 */
public class DomainNameDatabaseTenantResolver extends BaseDomainNameTenantResolver {

  /**
   * Used for listening to save events for DomainTenantMap domain class
   */
  GroovyEventBroker eventBroker

  public DomainNameDatabaseTenantResolver() {
  }

  /**
   * The event broker listens for every time a record is saved, then calls a refresh on the
   * list of hosts.
   */
  public void setEventBroker(GroovyEventBroker eventBroker) {
    if (eventBroker != null) {
      eventBroker.subscribe("hibernate.${HibernateEvent.saveOrUpdate}.${DomainTenantMap.class.getSimpleName()}") {
        event, broker ->
        this.initialize();
      }
    }
  }

  public void initialize() {
    hosts.clear()
    //This will load all domain tenants, regardless of where they came from
    Collection<DomainTenantMap> list = DomainTenantMap.findAll("from tenant.DomainTenantMap");
    list.each {DomainTenantMap map ->
      hosts.put(map.domainName, map.mappedTenantId)
    }
  }


}
