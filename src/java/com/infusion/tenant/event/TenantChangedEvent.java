package com.infusion.tenant.event;


/**
 * Created by IntelliJ IDEA.
 * User: eric
 * Date: Mar 21, 2009
 * Time: 7:36:50 PM
 */
public class TenantChangedEvent {
    public final Integer oldTenant;
    public final Integer newTenant;

    public TenantChangedEvent(Integer oldTenant, Integer newTenant) {
        this.oldTenant = oldTenant;
        this.newTenant = newTenant;
    }
}
