package com.infusion.tenant.datasource;

import com.infusion.tenant.CurrentTenant;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * DataSource extension that allows for a custom url per tenant.
 */
public class TenantDataSourceImpl extends DriverManagerDataSource implements TenantDataSource {
// ========================================================================================================================
//    Instance Fields
// ========================================================================================================================

    /**
     * This is the class that knows what the current tenant is
     * @return
     */
    CurrentTenant currentTenant;
    
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
        String dataSourceUrl = dataSourceUrlResolver.getDataSourceUrl(currentTenant.get());
        if (dataSourceUrl == null) {
            dataSourceUrl = super.getUrl();
        }
        return dataSourceUrl;
    }

    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }

    public void setDataSourceUrlResolver(DataSourceUrlResolver dataSourceUrlResolver) {
        this.dataSourceUrlResolver = dataSourceUrlResolver;
    }
}
