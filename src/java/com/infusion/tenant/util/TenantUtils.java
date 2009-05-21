package com.infusion.tenant.util;

import com.infusion.tenant.CurrentTenant;
import groovy.lang.Closure;

/**
 * Class that provides convenience methods for dealing with the multi-tenant plugin
 */
public class TenantUtils {
    private static CurrentTenant currentTenant;

    public static void doWithTenant(Integer tenantId, Closure closure) throws Throwable {
        Integer currentTenantId = currentTenant.get();
        currentTenant.set(tenantId);
        Throwable caught = null;
        try {
            closure.call();
        } catch (Throwable t) {
            caught = t;
        } finally {
            currentTenant.set(currentTenantId);
        }
        if (caught != null) {
            throw caught;
        }
    }


    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }
}
