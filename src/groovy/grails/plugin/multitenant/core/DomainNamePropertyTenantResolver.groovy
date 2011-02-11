package grails.plugin.multitenant.core;


import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Simple class that loads tenants from a properties file.  The data in the file should be a map that contains at
 * least the following keys:
 * fullservername = mapped_tenant_id
 */
public class DomainNamePropertyTenantResolver extends BaseDomainNameTenantResolver
{
  /**
   * This will clear out the current list of tenants and load the most current copy form the data in the configuration
   * file.
   */
  public void initialize()
  {
    hosts.clear();
    Map domainTenants = ConfigurationHolder.config.tenant.domainTenantMap.flatten()
    hosts.putAll(domainTenants);
  }
}
