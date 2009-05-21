package com.infusion.tenant;

import com.infusion.util.event.EventBroker;
import com.infusion.tenant.event.TenantChangedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic implementation that stores the current tenant in a threadlocal variable.
 */
public class CurrentTenantThreadLocal implements CurrentTenant {
// ========================================================================================================================
//    Static Fields
// ========================================================================================================================

    private ThreadLocal<Integer> currentTenant = new ThreadLocal<Integer>();

    private EventBroker eventBroker;
    private List<Integer> loaded = new ArrayList<Integer>();

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

    public void set(Integer newTenantId) {
        Integer oldTenantId = get();
        if (!oldTenantId.equals(newTenantId)) {
            currentTenant.set(newTenantId);
            final TenantChangedEvent changedEvent = new TenantChangedEvent(oldTenantId, newTenantId);
            if (eventBroker != null) {
                eventBroker.publish("tenantChanged", changedEvent);
                if (!loaded.contains(newTenantId)) {
                    loaded.add(newTenantId);
                    eventBroker.publish("newTenant", changedEvent);
                }
            }
        }
    }

    public void setEventBroker(EventBroker eventBroker) {
        this.eventBroker = eventBroker;
    }
}
