package com.infusion.tenant.datasource;

import com.infusion.tenant.CurrentTenant;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: scottryan
 * Date: Aug 9, 2010
 * Time: 2:10:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class TenantTransactionAwareDataSourceProxy extends TransactionAwareDataSourceProxy
{
    /** This is the resolver to be used to determine the datasource. */
    private DataSourceUrlResolver dataSourceUrlResolver;
    /** This is the tenant this data source is associated with. */
    private CurrentTenant currentTenant;
    /**
     * This allows the TenantBeanFactoryPostProcessor to assign a proxy to this object in spring since there is one of
     * there for each client.
     */
    public static final boolean multiTenant=true;
    /** This is a logger for logging status or errors */
    private static Logger log=Logger.getLogger(TenantTransactionAwareDataSourceProxy.class);

    TenantTransactionAwareDataSourceProxy(DataSource targetDataSource)
    {
        super(targetDataSource);
    }

    @Override
    public DataSource getTargetDataSource()
    {
        DataSource ds=super.getTargetDataSource();
        if (currentTenant.get() != 0)
        {
            // If not tenant 0 then look up the jndi name from the tenant.
            final String jndiNameForTenant=dataSourceUrlResolver.getDataSourceUrl(currentTenant.get());
            try
            {
                // TODO Support other than JNDI Data Sources
                Context ctx=new InitialContext();
                ds=(DataSource) ctx.lookup(jndiNameForTenant);
            }
            catch (Exception ex)
            {
                log.fatal("Exception in Multi-tenant data source provider", ex);
            }
        }
        if (log.isDebugEnabled())
        {
            log.debug("Returning the dataSource " + ds.toString() + " for tenant " + currentTenant.get());
        }
        return ds;
    }

    /**
     * This will set the resolver type for this datasource object.
     *
     * @param inDataSourceUrlResolver The resolver to use to determine the datasource for the tenant.  The types could
     *                                be database or properties file.
     */

    public void setDataSourceUrlResolver(DataSourceUrlResolver inDataSourceUrlResolver)
    {
        this.dataSourceUrlResolver=inDataSourceUrlResolver;
    }

    /**
     * Since this object is cached this allows you to resolve which tenant this resource applies to.
     *
     * @param inCurrentTenant The tenant id this data source is associated with.
     */
    public void setCurrentTenant(CurrentTenant inCurrentTenant)
    {
        this.currentTenant=inCurrentTenant;
    }
}
