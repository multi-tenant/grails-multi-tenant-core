package com.infusion.util.log;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import com.infusion.tenant.TenantUtils;

/**
 * Created by IntelliJ IDEA.
 * User: eric
 * Date: Mar 30, 2009
 * Time: 3:40:58 PM
 */
public class MultiTenantLogLayout extends PatternLayout {
    @Override
    public String format(LoggingEvent loggingEvent) {
        Integer currentTenant = TenantUtils.getCurrentTenant();
        String log = super.format(loggingEvent);
        if(currentTenant != null && currentTenant > 0) {
            log = "[Tenant " + currentTenant + "] " + log;
        }
        return log;
    }
}
