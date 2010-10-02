package grails.plugin.multitenant.core;


import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Simple class that loads tenants from a properties file.
 */
public class DomainNamePropertyTenantResolver extends BaseDomainNameTenantResolver {

  public void initialize() {
    hosts.clear();
    Map domainTenants = ConfigurationHolder.config.tenant.domainTenantMap.flatten()
    hosts.putAll(domainTenants);
  }


}
