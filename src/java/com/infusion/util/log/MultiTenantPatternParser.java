package com.infusion.util.log;

import com.infusion.tenant.CurrentTenant;
import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Class for parsing patterns from log formats and injecting the current tenant's id.  The pattern for
 * displaying the current tenantId is %T
 */
public class MultiTenantPatternParser extends PatternParser {
// ========================================================================================================================
//    Static Fields
// ========================================================================================================================

    public static final char TENANT_CONVERTER_CHAR = 'T';

// ========================================================================================================================
//    Instance Fields
// ========================================================================================================================

    final CurrentTenant currentTenant;

// ========================================================================================================================
//    Constructors
// ========================================================================================================================

    public MultiTenantPatternParser(String pattern, CurrentTenant currentTenant) {
        super(pattern);
        this.currentTenant = currentTenant;
    }

// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    public void finalizeConverter(char c) {
        if (c == TENANT_CONVERTER_CHAR) {
            addConverter(new MultiTenantPatternConverter(formattingInfo));
            currentLiteral.setLength(0);
        } else {
            super.finalizeConverter(c);
        }
    }

// ========================================================================================================================
//    Inner Classes
// ========================================================================================================================

    /**
     * This class will actually return the replacement string for the logger (just the integer of the current tenant)
     */
    private class MultiTenantPatternConverter extends PatternConverter {
// ========================================================================================================================
//    Constructors
// ========================================================================================================================

        MultiTenantPatternConverter(FormattingInfo formattingInfo) {
            super(formattingInfo);
        }

// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

        public String convert(LoggingEvent event) {
            final String rtn;
            if(currentTenant == null) {
                rtn = "N/A";
            } else {
                rtn = String.valueOf(currentTenant.get());
            }
            return rtn;
        }
    }
}
