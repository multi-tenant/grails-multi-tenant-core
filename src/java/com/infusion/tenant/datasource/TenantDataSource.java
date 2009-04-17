package com.infusion.tenant.datasource;

/**
 * Created by Felipe Cypriano
 * Date: 08/04/2009
 * Time: 08:30:29
 * 
 * Base interface to TentantDataSource. To be able to use it injected by Spring.
 * DataSource extension that allows for a custom url per tenant. 
 */
public interface TenantDataSource extends javax.sql.DataSource {
    /**
     * This tells spring to manage a datasource per tenant
     */
    public boolean multiTenant = true;

    public String getUrl();

    public void setDataSourceUrlResolver(DataSourceUrlResolver dataSourceUrlResolver);
}
