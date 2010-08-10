package com.infusion.tenant.cache
/**
 *  Class that wraps OSCacheProvider and makes it multi-tenant compatible
 */
class MultiTenantOSCacheProvider extends org.hibernate.cache.OSCacheProvider
{
// ========================================================================================================================
//    Static Fields
// ========================================================================================================================

    /**
     * The <tt>OSCache</tt> cache capacity property suffix.
     */
    public static final String OSCACHE_CAPACITY = "capacity"
    /**
     * The <tt>OSCache</tt> CRON expression property suffix.
     */
    public static final String OSCACHE_CRON = "cron"

    /**
     * The <tt>OSCache</tt> refresh period property suffix.
     */
    public static final String OSCACHE_REFRESH_PERIOD = "refresh.period"

    private static final Properties OSCACHE_PROPERTIES = new com.opensymphony.oscache.base.Config().getProperties()

// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    /**
     * Builds a new   {@link org.hibernate.cache.Cache}   instance, and gets it's properties from the OSCache   {@link com.opensymphony.oscache.base.Config}
     * which reads the properties file (<code>oscache.properties</code>) from the classpath.
     * If the file cannot be found or loaded, an the defaults are used.
     *
     * @param region
     * @param properties
     * @return
     * @throws org.hibernate.cache.CacheException
     */
    public org.hibernate.cache.Cache buildCache(String region, Properties properties) throws org.hibernate.cache.CacheException
    {
        int refreshPeriod = org.hibernate.util.PropertiesHelper.getInt(
                org.hibernate.util.StringHelper.qualify(region, OSCACHE_REFRESH_PERIOD),
                OSCACHE_PROPERTIES,
                com.opensymphony.oscache.base.CacheEntry.INDEFINITE_EXPIRY
        )
        String cron = OSCACHE_PROPERTIES.getProperty(org.hibernate.util.StringHelper.qualify(region, OSCACHE_CRON))
        Integer capacity = org.hibernate.util.PropertiesHelper.getInteger(org.hibernate.util.StringHelper.qualify(region, OSCACHE_CAPACITY), OSCACHE_PROPERTIES)

        // construct the cache
        final org.hibernate.cache.Cache cache = new MultiTenantOSCache(refreshPeriod, cron, region, capacity)

        return cache
    }

    public boolean isMinimalPutsEnabledByDefault()
    {
        return false
    }

    public long nextTimestamp()
    {
        return org.hibernate.cache.Timestamper.next()
    }

    /**
     * Callback to perform any necessary initialization of the underlying cache implementation
     * during SessionFactory construction.
     *
     * @param properties current configuration settings.
     */
    public void start(Properties properties) throws org.hibernate.cache.CacheException
    {
    }

    /**
     * Callback to perform any necessary cleanup of the underlying cache implementation
     * during SessionFactory.close().
     */
    public void stop()
    {
    }
}
