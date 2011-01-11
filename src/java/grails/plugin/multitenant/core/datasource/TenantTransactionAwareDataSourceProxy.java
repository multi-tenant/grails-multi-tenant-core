package grails.plugin.multitenant.core.datasource;

import grails.plugin.multitenant.core.CurrentTenant;
import grails.plugin.multitenant.core.InvalidTenantException;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * This class will intercept any calls by the grails infrastructure for a data source and return the proper
 * data source for the current tenant.  If a transaction is already open and you make a tenant change the
 * data source will not change.  If you want to change tenants then you need to close all open transactions and then
 * change tenant and open the transaction.  Also you should flush and clear the hibernate session first level cache so
 * you don't mix tenant data in the cache.  This class only supports JNDI based data sources at the current time.
 */
public class TenantTransactionAwareDataSourceProxy extends TransactionAwareDataSourceProxy
{
    /**
     * This is the data source url resolver to be used to determine the data source url.
     */
    private DataSourceUrlResolver dataSourceUrlResolver;
    /**
     * This is the thread local tenant that is active at the time of the interation with the class.
     */
    private CurrentTenant currentTenant;
    /**
     * This allows the TenantBeanFactoryPostProcessor to assign a proxy to this object in spring since there is one of
     * these for each client.
     */
    public static final boolean multiTenant = true;
    /**
     * This is a logger for logging status or errors
     */
    private static Logger log = Logger.getLogger(TenantTransactionAwareDataSourceProxy.class);

    /**
     * This is the default constructor.
     *
     * @param targetDataSource The target data source for this class.
     */
    TenantTransactionAwareDataSourceProxy(DataSource targetDataSource)
    {
        super(targetDataSource);
    }

    /**
     * This method will return the actual data source for the active tenant.  If no tenant can be resolve then the
     * default data source is returned and assumed to connect to the configuration database as tenant 0.   If the
     * tenant is null we throw an exception.  If the tenant is 0 we return the original default which should be the
     * default data source.  If the tenant is non zero we look up the jndi entry and return it if it is not null.  If it
     * is null we throw an InvalidTenantException.
     *
     * @return The data source for the current thread local tenant.
     */
    @Override
    public DataSource getTargetDataSource()
    {
        DataSource ds = super.getTargetDataSource();
        Integer tenant = currentTenant.get();
        if (tenant == null)
        {
            throw new InvalidTenantException("No tenant (null) returned to locate the JNDI Name for.");
        } else if (tenant != 0)
        {
            // If not tenant 0 then look up the jndi name from the tenant.
            final String jndiNameForTenant = dataSourceUrlResolver.getDataSourceUrl(tenant);
            if (jndiNameForTenant == null)
            {
                throw new InvalidTenantException("No JNDI Name returned for tenant " + tenant);
            }
            if (log.isDebugEnabled())
            {
                log.debug("Returning the jndi dataSource " + jndiNameForTenant + " for tenant " + tenant);
            }
            try
            {
                // TODO Support other than JNDI Data Sources
                // TODO Either cache the context lookup or cache the data source for performance
                Context ctx = new InitialContext();
                ds = (DataSource) ctx.lookup(jndiNameForTenant);
            }
            catch (Exception ex)
            {
                log.fatal("Exception in Multi-tenant data source provider", ex);
            }
        }
        if (log.isDebugEnabled())
        {
            log.debug("Returning data source for tenant 0 as" + ds);
        }
        return ds;
    }

    /**
     * This will set the resolver type for this data source object.
     *
     * @param inDataSourceUrlResolver The resolver to use to determine the data source for the tenant.  The types could
     *                                be database or properties file.
     */
    public void setDataSourceUrlResolver(DataSourceUrlResolver inDataSourceUrlResolver)
    {
        this.dataSourceUrlResolver = inDataSourceUrlResolver;
    }

    /**
     * Since this object is cached this allows you to resolve which tenant this resource applies to.
     *
     * @param inCurrentTenant The tenant id this data source is associated with.
     */
    public void setCurrentTenant(CurrentTenant inCurrentTenant)
    {
        this.currentTenant = inCurrentTenant;
    }
}