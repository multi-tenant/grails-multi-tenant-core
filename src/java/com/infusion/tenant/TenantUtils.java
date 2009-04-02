package com.infusion.tenant;

/**
 * Class to store and locate the current tenant.  Stores in a threadlocal variable.
 */
public class TenantUtils {
// ========================================================================================================================
//    Static Fields
// ========================================================================================================================

    private static ThreadLocal<Integer> currentTenant = new ThreadLocal<Integer>();

// ========================================================================================================================
//    Static Methods
// ========================================================================================================================

    public static Integer getCurrentTenant() {
        Integer rtn = currentTenant.get();
        if(rtn == null) {
            currentTenant.set(0);
            rtn = 0;
        }
        return rtn;
    }

    public static void setCurrentTenant(Integer tenantId) {
        currentTenant.set(tenantId);
    }
}

