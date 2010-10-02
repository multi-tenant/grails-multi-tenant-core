package grails.plugin.multitenant.core.datasource;

import grails.plugin.multitenant.core.CurrentTenant;
import org.apache.log4j.Logger;
import org.springframework.jndi.JndiObjectFactoryBean;

/**
 * This class allows you to have a switching jndi datasource per tenant with the multi-tenant plugin
 */
public class TenantJndiDataSource extends JndiObjectFactoryBean {
    /**
     * This is the resolver to be used to determine the datasource.
     */
    private DataSourceUrlResolver dataSourceUrlResolver;
    /**
     * This is the tenant this data source is associated with.
     */
    private CurrentTenant currentTenant;
    /**
     * This allows the TenantBeanFactoryPostProcessor to assign a proxy to this object in spring since there is one of
     * there for each client.
     */
    public static final boolean multiTenant = true;
    /**
     * This is a logger for logging status or errors
     */
    private static Logger log = Logger.getLogger(TenantJndiDataSource.class);

    /**
     * This method will look up the datasoure URL for the given tenant and return that url.
     *
     * @return The data source URL for the tenant via the resolver.
     */
    @Override
    public String getJndiName() {
        if (log.isDebugEnabled())
            log.debug("Resolving JNDI datasource for tenant " + currentTenant.get() + " and resolver " + dataSourceUrlResolver);
        // If we are using tenent 0 then return the default datasource
        // TODO Can we handle a hard coded config datasource?
        if (currentTenant.get() == 0) {
            final String jndiNameForTenant = super.getJndiName();
        } else {
            // If not tenant 0 then look up the jndi name from the tenant.
            final String jndiNameForTenant = dataSourceUrlResolver.getDataSourceUrl(currentTenant.get());
            // If we found a jndi name we store it in the datasource otherwise we determine if it is an error or not.
            if (jndiNameForTenant != null) {
                super.setJndiName(jndiNameForTenant);
            } else {
                // TODO If this is single tenant we want to throw an exception to warn of a possible issue?
            }
        }
        if (log.isDebugEnabled())
            log.debug("Located JNDI datasource " + super.getJndiName() + " for tenant " + currentTenant.get());
        return super.getJndiName();
    }

    /**
     * This will set the resolver type for this datasource object.
     *
     * @param inDataSourceUrlResolver The resolver to use to determine the datasource for the tenant.  The types could
     *                                be database or properties file.
     */

    public void setDataSourceUrlResolver(DataSourceUrlResolver inDataSourceUrlResolver) {
        this.dataSourceUrlResolver = inDataSourceUrlResolver;
    }

    /**
     * Since this object is cached this allows you to resolve which tenant this resource applies to.
     *
     * @param inCurrentTenant The tenant id this data source is associated with.
     */
    public void setCurrentTenant(CurrentTenant inCurrentTenant) {
        this.currentTenant = inCurrentTenant;
    }
}
