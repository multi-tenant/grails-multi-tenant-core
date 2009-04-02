package com.infusion.tenant.datasource

import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Reads properties from Config.groovy at tenant.dataSourceTenantMap.
 */
public class PropertyDataSourceUrlResolver implements DataSourceUrlResolver {

  Map dataSources = [:]
  boolean loaded

  public String getDataSourceUrl(Integer tenantId) {
    if(!loaded) {
      init()
    }
    return dataSources.get("t" + tenantId);
  }

  void init() {
    dataSources.clear();
    dataSources.putAll(ConfigurationHolder.config.tenant.dataSourceTenantMap.flatten())
  }


}