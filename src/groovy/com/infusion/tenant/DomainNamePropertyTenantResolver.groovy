package com.infusion.tenant;

import com.infusion.tenant.TenantResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

/**
 * Simple class that loads tenants from a properties file.
 */
public class DomainNamePropertyTenantResolver extends BaseDomainNameTenantResolver {

  public void initialize() {
    hosts.clear();
    Map domainTenants = ConfigurationHolder.config.tenant.mapping.flatten()
    hosts.putAll(domainTenants);
  }


}
