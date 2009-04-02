package com.infusion.tenant;

import javax.servlet.http.HttpServletRequest;

/**
 * Class that knows how to identify a tenantId given an httpServletRequest
 */
public interface TenantResolver {
// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    /**
     * Given a request, returns the associated tenantId
     */
    public Integer getTenantFromRequest(HttpServletRequest request);
}
