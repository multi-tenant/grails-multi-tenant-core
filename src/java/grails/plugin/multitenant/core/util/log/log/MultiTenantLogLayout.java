package grails.plugin.multitenant.core.util.log.log;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternParser;

/**
 * This class will set up a pattern to support inserting the tenant id or name into the beginning of any logging
 * pattern. It will either enhance an existing pattern or create a new pattern with the tenant id or tenant name
 * at the beginning of the patter.
 */
public class MultiTenantLogLayout extends PatternLayout
{
    /**
     * Inserts a %T after the $t (thread logging) to display the current tenant mapped id.
     */
    public static final String DEFAULT_TENANT_CONVERSION_PATTERN = "%d [%t] [tenant %T] %-5p %c{2} %x - %m%n";

    /**
     * This is a constructor that does not take a pattern and there fore uses the defined default pattern.
     */
    public MultiTenantLogLayout()
    {
        this(DEFAULT_TENANT_CONVERSION_PATTERN);
    }

    /**
     * This constructor allows for creation using a user defined pattern
     *
     * @param inPattern - The user defined logging pattern that can contain a %T where the tenant id will be inserted.
     */
    public MultiTenantLogLayout(String inPattern)
    {
        super(inPattern);
    }

    /**
     * This will create a pattern parser using the defined user pattern or the default pattern if one is not supplied.
     * This parser knows how to insert the current tenant id in to the proper location in the pattern.
     *
     * @param inPattern - The logging pattern to use during the parsing operation.
     * @return
     */
    public PatternParser createPatternParser(String inPattern)
    {
        return new MultiTenantPatternParser(
                inPattern == null ? DEFAULT_TENANT_CONVERSION_PATTERN : inPattern);
    }
}
