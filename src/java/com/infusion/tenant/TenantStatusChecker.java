package com.infusion.tenant;

/**
 * Interface that checks the status of a given tenant.  If the tenant status
 * returns false, it will be redirected to an 'Account Disabled' screen.
 */
public interface TenantStatusChecker {
    public boolean checkTenantStatus(Integer tenantId);
}
