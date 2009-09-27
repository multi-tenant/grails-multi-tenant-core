package com.infusion.tenant.cache;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.OSCache;

import java.util.Map;
import java.util.HashMap;

import com.infusion.tenant.util.TenantUtils;

/**
 * Wraps an OSCache and makes it multi-tenant aware.
 */
public class MultiTenantOSCache implements Cache {
    int refreshPeriod;
    String region;
    String cron;

    Map<Integer, OSCache> tenantCache = new HashMap<Integer, OSCache>();
    private Integer cacheCapacity;

    public MultiTenantOSCache(int refreshPeriod, String cron, String region, Integer cacheCapacity) {
        this.refreshPeriod = refreshPeriod;
        this.region = region;
        this.cron = cron;
        this.cacheCapacity = cacheCapacity;
    }

    public void clear() throws CacheException {
        getWrapped().clear();
    }

    public void destroy() throws CacheException {
        getWrapped().destroy();
    }

    public Object get(Object key) throws CacheException {
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

    public void lock(Object key) throws CacheException {
        getWrapped().lock(key);
    }

    public long nextTimestamp() {
        return getWrapped().nextTimestamp();
    }

    public void put(Object key, Object value) throws CacheException {
        getWrapped().put(key, value);
    }

    public Object read(Object key) throws CacheException {
        return getWrapped().read(key);
    }

    public void remove(Object key) throws CacheException {
        getWrapped().remove(key);
    }

    public Map toMap() {
        return getWrapped().toMap();
    }

    public void unlock(Object key) throws CacheException {
        getWrapped().unlock(key);
    }

    public void update(Object key, Object value) throws CacheException {
        getWrapped().update(key, value);
    }

    public synchronized Cache getWrapped() {
        Integer currentTenant = TenantUtils.getCurrentTenant();
        if (currentTenant == null) {
            currentTenant = 0;
        }
        final OSCache rtn;
        if (tenantCache.containsKey(currentTenant)) {
            rtn = tenantCache.get(currentTenant);
        } else {
            rtn = new OSCache(refreshPeriod, cron, region);
            tenantCache.put(currentTenant, rtn);
        }
        if (cacheCapacity != null) {
            rtn.setCacheCapacity(cacheCapacity);
        }
        return rtn;
    }
}
