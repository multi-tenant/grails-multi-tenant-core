package com.infusion.tenant;

/**
 * Interface used to store and retrieve the current tenant
 */
public interface CurrentTenant {
    public Integer get();
    public void set(Integer tenantId);

    /**
     * The 'loaded cache' contains all the tenants that this application has encountered.
     * Resetting the cache will erase that list, causing the app to reload data for tenants
     * as they are re-encountered.
     */
    public void resetLoadedCache();
}
