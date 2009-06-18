package com.infusion.tenant.datasource;

import com.infusion.util.event.groovy.GroovyEventBroker;

/**
 * This resolver takes in a tenantId and returns the datasource url
 * for that tenant.
 */
public interface DataSourceUrlResolver {
    public String getDataSourceUrl(Integer tenantId);
}
