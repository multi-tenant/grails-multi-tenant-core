package com.infusion.tenant;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;

/**
 * Base class used for mapping subdomains to tenantIds.  Subclasses can implement an "initialize" method
 * that loads the data into the "hosts" map, and this class will take care of the rest.
 */
public abstract class BaseDomainNameTenantResolver implements TenantResolver {
// ========================================================================================================================
//    Instance Fields
// ========================================================================================================================

    /**
     * The map of hosts used to look up tenant id
     */
    protected Map<String, Integer> hosts = new HashMap<String, Integer>();
    protected boolean loaded;

// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    public Integer getTenantFromRequest(HttpServletRequest request) {
        if(!loaded) {
            initialize();
            loaded = true;
        }
        return hosts.get(request.getServerName());
    }

// ========================================================================================================================
//    Abstract Methods
// ======================================================================================================================== 

    /**
     * Loads up all the mappings into the protected "hosts" field.
     */
    public abstract void initialize();
}
