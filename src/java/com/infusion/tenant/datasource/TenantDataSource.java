package com.infusion.tenant.datasource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import com.infusion.tenant.TenantUtils;

/**
 * DataSource extension that allows for a custom url per tenant.
 */
public class TenantDataSource extends DriverManagerDataSource {
// ========================================================================================================================
//    Static Fields
// ========================================================================================================================

    /**
     * This tells spring to manage a datasource per tenant
     */
    public static final boolean multiTenant = true;

// ========================================================================================================================
//    Instance Fields
// ========================================================================================================================

    /**
     * This is the class that converts a tenantId to a datasource url.
     */
    DataSourceUrlResolver dataSourceUrlResolver;

// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    @Override
    public String getUrl() {
        if(dataSourceUrlResolver == null) {
            throw new IllegalArgumentException("Unable to load data source url resolver.  Make sure you" +
                    " have a spring bean that implements DataSourceUrlResolver, named dataSourceUrlResolver");
        }
        String dataSourceUrl = dataSourceUrlResolver.getDataSourceUrl(TenantUtils.getCurrentTenant());
        if (dataSourceUrl == null) {
            dataSourceUrl = super.getUrl();
        }
        return dataSourceUrl;
    }

    public void setDataSourceUrlResolver(DataSourceUrlResolver dataSourceUrlResolver) {
        this.dataSourceUrlResolver = dataSourceUrlResolver;
    }
}
