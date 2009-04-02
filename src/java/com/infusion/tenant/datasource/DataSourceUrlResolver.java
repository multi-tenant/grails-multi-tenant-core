package com.infusion.tenant.datasource;

/**
 * This resolver takes in a tenantId and returns the datasource url
 * for that tenant.
 */
public interface DataSourceUrlResolver {
    public String getDataSourceUrl(Integer tenantId);
}
