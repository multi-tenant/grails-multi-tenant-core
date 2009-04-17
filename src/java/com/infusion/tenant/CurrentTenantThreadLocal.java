package com.infusion.tenant;

/**
 * Basic implementation that stores the current tenant in a threadlocal variable.
 */
public class CurrentTenantThreadLocal implements CurrentTenant {
// ========================================================================================================================
//    Static Fields
// ========================================================================================================================

    private static ThreadLocal<Integer> currentTenant = new ThreadLocal<Integer>();

// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    public Integer get() {
        Integer rtn = currentTenant.get();
        if (rtn == null) {
            currentTenant.set(0);
            rtn = 0;
        }
        return rtn;
    }

    public void set(Integer tenantId) {
        currentTenant.set(tenantId);
    }
}
