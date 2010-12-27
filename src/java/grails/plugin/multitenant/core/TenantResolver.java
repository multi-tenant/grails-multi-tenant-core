package grails.plugin.multitenant.core;

import javax.servlet.http.HttpServletRequest;

/**
 * Class that knows how to identify a tenantId given an httpServletRequest
 */
public interface TenantResolver
{
    /**
     * Given a request, returns the associated tenantId
     *
     * @param inRequest - The Http request to use to resolve a tenant id.
     * @return - The tenant id that matches the request.
     */
    public Integer getTenantFromRequest(HttpServletRequest inRequest);
}
