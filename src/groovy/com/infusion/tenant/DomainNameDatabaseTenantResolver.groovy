package com.infusion.tenant;


import com.infusion.util.event.EventBroker
import com.infusion.util.event.groovy.GroovyEventBroker
import com.infusion.util.domain.event.HibernateEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.apache.log4j.Logger;

/**
 * Implementation that looks up tenantIds from a local table DomainTenantMap that stores domain name to tenantId mappings. 
 */
public class DomainNameDatabaseTenantResolver extends BaseDomainNameTenantResolver implements ApplicationContextAware {

  private static Logger log = Logger.getLogger(getClass());

  /**
   * Used for listening to save events for DomainTenantMap domain class
   */
  GroovyEventBroker eventBroker
  ApplicationContext applicationContext


  public DomainNameDatabaseTenantResolver() {
  }

  /**
   * The event broker listens for every time a record is saved, then calls a refresh on the
   * list of hosts.
   */
  public void setEventBroker(GroovyEventBroker eventBroker) {
    if (eventBroker != null) {
      eventBroker.subscribe("hibernate.${HibernateEvent.saveOrUpdate}.DomainTenantMap") {
        event, broker ->
        log.info "DomainTenantMap was saved, reloading resolver"
        this.initialize();
      }
    }
  }

  public void initialize() {
    hosts.clear()
    log.info "Reloading resolver"
    //This will load all domain tenants, regardless of which tenant they're for
    def list = applicationContext.getBean("tenant.DomainTenantMap").findAll("from tenant.DomainTenantMap");
    list.each {map ->
      log.debug "Domain->Tenant: ${map.domainName}->${map.mappedTenantId}"
      hosts.put(map.domainName, map.mappedTenantId)
    }
  }

}
