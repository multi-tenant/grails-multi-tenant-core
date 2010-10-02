package grails.plugin.multitenant.core.cache

/**
 * $Rev: 1517 $:  Revision of last commit
 * $Author: sryan $:  Author of last commit
 * $Date: 2010-02-23 14:16:19 -0700 (Tue, 23 Feb 2010) $:  Date of last commit
 *
 * This class provides a multi tenant aware cache provider for the EhCache caching
 * framework.
 */
class MultiTenantEhCacheProvider extends org.hibernate.cache.EhCacheProvider {
  /**
   * This will build a cache with the given name and return it.  The cache will
   * be a multi tenant enabled cache. Even though this method provides properties,
   * they are not used. Properties for EHCache are specified in the ehcache.xml file.
   * Configuration will be read from ehcache.xml for a cache declaration where
   * the name attribute matches the name parameter in this builder.
   * @param inName the name of the cache. Must match a cache configured in ehcache.xml
   * @param inProperties not currently used
   * @return A multi-tenante aware EHCache.
   * @throws org.hibernate.cache.CacheException Thrown if the cache cannot be successfully created.
   */
  public org.hibernate.cache.Cache buildCache(String inName, Properties inProperties) throws org.hibernate.cache.CacheException {
    // construct the cache
    final org.hibernate.cache.Cache cache = new MultiTenantEhCache(inName, inProperties);
    return cache;
  }

  /**
   * Determines if the minimal puts are enabled by default.
   * @return true if they are enabled and false if not.
   */
  public boolean isMinimalPutsEnabledByDefault() {
    return false;
  }

  /**
   * Returns the next timestamp.
   * @return The next available timestamp.
   */
  public long nextTimestamp() {
    return org.hibernate.cache.Timestamper.next();
  }

  /**
   * Callback to perform any necessary initialization of the underlying cache implementation
   * during SessionFactory construction.
   *
   * @param inProperties current configuration settings.
   */
  public void start(Properties inProperties) throws org.hibernate.cache.CacheException {
  }

  /**
   * Callback to perform any necessary cleanup of the underlying cache implementation
   * during SessionFactory.close().
   */
  public void stop() {
  }
}