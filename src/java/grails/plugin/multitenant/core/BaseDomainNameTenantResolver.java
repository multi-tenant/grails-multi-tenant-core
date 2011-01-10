package grails.plugin.multitenant.core;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Base class used for mapping sub-domains to tenantIds.  Subclasses can implement an "initialize" method
 * that loads the data into the "hosts" map, and this class will take care of the rest.  There is also a cache
 * that can be used to store all the domain information including the name and other information.
 */
public abstract class BaseDomainNameTenantResolver implements TenantResolver
{
    /**
     * This is a logger for logging status and error messages.
     */
    private static Logger log = Logger.getLogger(BaseDomainNameTenantResolver.class);
    /**
     * This is the name of the shared cache of tenant data.  This is a shared cache where domain objects are stored
     * with information about the existing tenants.
     */
    public static String TENANT_DATA_CACHE_NAME = "MultiTenantDomainData";
    /**
     * The map of hosts server names to tenant id used to look up tenant id.
     */
    protected Map<String, Integer> hosts = new HashMap<String, Integer>();
    // This is set to true if the data has been loaded and is current
    protected boolean loaded;

    /**
     * This will decode the tenantId based on the server name in the request.  It will return the mapped tenant id
     * based on the decoded server name or null if there is no match.  If there is no match the server name will be
     * output as a fatal error.  Debugging is available to output the tenant Id and matching server name.
     *
     * @param inRequest - The http servlet request to decode the tenant from.
     * @return - The tenant id based on the complete server name in the request.  Null if no match.
     */
    public Integer getTenantFromRequest(HttpServletRequest inRequest)
    {
        if (!loaded)
        {
            initialize();
            loaded = true;
        }
        Integer tenantId = hosts.get(inRequest.getServerName());
        if (tenantId == null)
        {
            log.fatal("Could not decode valid tenant id from request server " + inRequest.getServerName());
            throw new InvalidTenantException("Could not decode mapped tenant id from request server name " + inRequest.getServerName());
        }
        if (log.isDebugEnabled())
        {
            log.debug("Decoded tenant id " + tenantId + " from http request URL " + inRequest.getServerName());
        }
        return tenantId;
    }

    /**
     * Loads up all the mappings into the protected "hosts" field.  This can do other additional operations in the
     * super class.
     */
    public abstract void initialize();
}