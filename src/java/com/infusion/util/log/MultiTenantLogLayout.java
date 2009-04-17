package com.infusion.util.log;

import com.infusion.tenant.CurrentTenant;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternParser;

/**
 * Created by IntelliJ IDEA.
 * User: eric
 * Date: Mar 30, 2009
 * Time: 3:40:58 PM
 */
public class MultiTenantLogLayout extends PatternLayout {
// ========================================================================================================================
//    Static Fields
// ========================================================================================================================

    /**
     * Inserts a %T after the $t (thread logging) to display the current tenant.
     */
    public static final String DEFAULT_TENANT_CONVERSION_PATTERN = "%d [%t] [tenant %T] %-5p %c{2} %x - %m%n";

// ========================================================================================================================
//    Instance Fields
// ========================================================================================================================

    /**
     * This class knows how to get the current tenant
     */
    private CurrentTenant currentTenant;

// ========================================================================================================================
//    Constructors
// ========================================================================================================================

    public MultiTenantLogLayout() {
        this(DEFAULT_TENANT_CONVERSION_PATTERN);
    }

    public MultiTenantLogLayout(String pattern) {
        super(pattern);
    }

// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    public PatternParser createPatternParser(String pattern) {
        return new MultiTenantPatternParser(
                pattern == null ? DEFAULT_TENANT_CONVERSION_PATTERN : pattern, currentTenant);
    }

    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }
}
