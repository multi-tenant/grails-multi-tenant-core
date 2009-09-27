package com.infusion.tenant.cache;

import org.hibernate.cache.*;
import org.hibernate.util.StringHelper;
import org.hibernate.util.PropertiesHelper;

import java.util.Properties;

import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.CacheEntry;

/**
 *  Class that wraps OSCacheProvider and makes it multi-tenant compatible
 */
public class MultiTenantOSCacheProvider extends OSCacheProvider {
// ======================================================================================================================== 
//    Static Fields 
// ======================================================================================================================== 

	/**
	 * The <tt>OSCache</tt> cache capacity property suffix.
	 */
	public static final String OSCACHE_CAPACITY = "capacity";
	/**
	 * The <tt>OSCache</tt> CRON expression property suffix.
	 */
	public static final String OSCACHE_CRON = "cron";


	/**
	 * The <tt>OSCache</tt> refresh period property suffix.
	 */
	public static final String OSCACHE_REFRESH_PERIOD = "refresh.period";

	private static final Properties OSCACHE_PROPERTIES = new Config().getProperties();

// ======================================================================================================================== 
//    Public Instance Methods
// ======================================================================================================================== 

	/**
	 * Builds a new {@link Cache} instance, and gets it's properties from the OSCache {@link Config}
	 * which reads the properties file (<code>oscache.properties</code>) from the classpath.
	 * If the file cannot be found or loaded, an the defaults are used.
	 *
	 * @param region
	 * @param properties
	 * @return
	 * @throws CacheException
	 */
	public Cache buildCache(String region, Properties properties) throws CacheException {
		int refreshPeriod = PropertiesHelper.getInt(
			StringHelper.qualify(region, OSCACHE_REFRESH_PERIOD),
			OSCACHE_PROPERTIES,
			CacheEntry.INDEFINITE_EXPIRY
		);
		String cron = OSCACHE_PROPERTIES.getProperty( StringHelper.qualify(region, OSCACHE_CRON) );
        Integer capacity = PropertiesHelper.getInteger( StringHelper.qualify(region, OSCACHE_CAPACITY), OSCACHE_PROPERTIES );

		// construct the cache
		final Cache cache = new MultiTenantOSCache(refreshPeriod, cron, region, capacity);

		return cache;
	}

	public boolean isMinimalPutsEnabledByDefault() {
		return false;
	}

	public long nextTimestamp() {
		return Timestamper.next();
	}

	/**
	 * Callback to perform any necessary initialization of the underlying cache implementation
	 * during SessionFactory construction.
	 *
	 * @param properties current configuration settings.
	 */
	public void start(Properties properties) throws CacheException {
	}

	/**
	 * Callback to perform any necessary cleanup of the underlying cache implementation
	 * during SessionFactory.close().
	 */
	public void stop() {
	}
}
