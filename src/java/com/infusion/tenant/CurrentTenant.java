package com.infusion.tenant;

/**
 * Interface used to store and retrieve the current tenant
 */
public interface CurrentTenant {
    public Integer get();
    public void set(Integer tenantId);
}
