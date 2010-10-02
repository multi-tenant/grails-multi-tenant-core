package grails.plugin.multitenant.core.cache

import grails.plugin.multitenant.core.util.TenantUtils

/**
 * $Rev: 1517 $:  Revision of last commit
 * $Author: sryan $:  Author of last commit
 * $Date: 2010-02-23 14:16:19 -0700 (Tue, 23 Feb 2010) $:  Date of last commit
 *
 * This class wraps an EhCache cache and makes it multi tenant aware.
 */
class MultiTenantEhCache implements org.hibernate.cache.Cache {

  /**
   * The name of the cache.  This name does not include the tenant id but just the unique name
   * of the cache.
   */
  String name;

  /** This will use the current cache manager to retrieve the current cache from the cache manager.
   * It supports multi-tenant capabilities by adding the tenant id to the name of the cache.
   */
  public MultiTenantEhCache(String inName, Properties inProperties) {
    name = inName;
  }

  public void clear() throws org.hibernate.cache.CacheException {
    getWrapped().clear();
  }

  public void destroy() throws org.hibernate.cache.CacheException {
    getWrapped().destroy();
  }

  public Object get(Object key) throws org.hibernate.cache.CacheException {
    return getWrapped().get(key);
  }

  public long getElementCountInMemory() {
    return getWrapped().getElementCountInMemory();
  }

  public long getElementCountOnDisk() {
    return getWrapped().getElementCountOnDisk();
  }

  public String getRegionName() {
    return getWrapped().getRegionName();
  }

  public long getSizeInMemory() {
    return getWrapped().getSizeInMemory();
  }

  public int getTimeout() {
    return getWrapped().getTimeout();
  }

  public void lock(Object key) throws org.hibernate.cache.CacheException {
    getWrapped().lock(key);
  }

  public long nextTimestamp() {
    return getWrapped().nextTimestamp();
  }

  public void put(Object key, Object value) throws org.hibernate.cache.CacheException {
    getWrapped().put(key, value);
  }

  public Object read(Object key) throws org.hibernate.cache.CacheException {
    return getWrapped().read(key);
  }

  public void remove(Object key) throws org.hibernate.cache.CacheException {
    getWrapped().remove(key);
  }

  public Map toMap() {
    return getWrapped().toMap();
  }

  public void unlock(Object key) throws org.hibernate.cache.CacheException {
    getWrapped().unlock(key);
  }

  public void update(Object key, Object value) throws org.hibernate.cache.CacheException {
    getWrapped().update(key, value);
  }

  public synchronized org.hibernate.cache.Cache getWrapped() {
    net.sf.ehcache.CacheManager singletonManager = net.sf.ehcache.CacheManager.create();
    // Get the current tenant id
    Integer currentTenant = TenantUtils.getCurrentTenant();
    if (currentTenant == null) {
      currentTenant = 0;
    }
    String newCacheName = currentTenant + "-" + name;
    net.sf.ehcache.Cache managedCache = singletonManager.getCache(newCacheName);
    // Now that we have the manager lets see if we have the cache
    if (managedCache == null) {
      // add to the manager
      // TODO How do we get specific configurations??
      singletonManager.addCache(newCacheName);
      managedCache = singletonManager.getCache(newCacheName);
    }
    org.hibernate.cache.Cache theCache;
    // Wrap the cache in the hibernate expected cache.
    theCache = new org.hibernate.cache.EhCache(managedCache);
    return theCache;
  }
}
