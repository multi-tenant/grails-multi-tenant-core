package com.infusion.tenant.datasource;

import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import com.infusion.tenant.CurrentTenant;

import javax.naming.NamingException;

/**
 * This class allows you to have a switching jndi datasource per tenant with the multi-tenant plugin
 */
public class TenantJndiDataSource extends JndiObjectFactoryBean {
    private DataSourceUrlResolver dataSourceUrlResolver;
    private CurrentTenant currentTenant;
    public static final boolean multiTenant = true;

    @Override
    public String getJndiName() {
        final String jndiNameForTenant = dataSourceUrlResolver.getDataSourceUrl(currentTenant.get());
        if (jndiNameForTenant != null) {
            super.setJndiName(jndiNameForTenant);
        }
        return super.getJndiName();
    }

    public void setDataSourceUrlResolver(DataSourceUrlResolver dataSourceUrlResolver) {
        this.dataSourceUrlResolver = dataSourceUrlResolver;
    }

    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }
}
