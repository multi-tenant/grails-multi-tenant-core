package com.infusion.tenant.event;


/**
 * This class is used whenever a tenant id is changed.
 */
public class TenantChangedEvent {
    public final Integer oldTenant;
    public final Integer newTenant;

    public TenantChangedEvent(Integer oldTenant, Integer newTenant) {
        this.oldTenant = oldTenant;
        this.newTenant = newTenant;
    }
}
