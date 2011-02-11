package grails.plugin.multitenant.core.util

import grails.plugin.multitenant.core.BaseDomainNameTenantResolver
import grails.plugin.multitenant.core.CurrentTenant
import grails.plugin.multitenant.core.groovy.compiler.MultiTenant

import java.lang.annotation.Annotation

import net.sf.ehcache.CacheManager
import net.sf.ehcache.Cache

/**
 * Class that provides convenience methods for dealing with the multi-tenant plugin.  It will allow to look up the
 * current tenant and retrieve information about that tenant or any of the registered tenants.  It will also allow
 * you to perform operations with a specific tenant.
 */
public class TenantUtils
{
  private static CurrentTenant currentTenant
  public static boolean ready = false

  /**
   * This method allows you to temporarily switch tenants to perform some operations.  Before
   * the method exits, it will set the tenantId back to what it was before.
   *
   * @param tenantId - The tenant Id you want to run the closure as.
   * @param closure - The closure you want to run as the new tenant.
   */
  public static void doWithTenant(Integer tenantId, Closure closure)
  {
    Integer currentTenantId = currentTenant.get()
    currentTenant.set(tenantId)
    try
    {
      closure.call()
    }
    finally
    {
      // Set the tenant id back to the original even if exception if thrown.
      currentTenant.set(currentTenantId)
    }
  }

  /**
   * This will allow the current tenant to be injected into the object.
   *
   * @param currentTenant - The current tenant resolver.
   */
  public void setCurrentTenant(CurrentTenant currentTenant)
  {
    this.currentTenant = currentTenant
  }

  /**
   * This will return the mapped tenant id of the currently mapped tenant.
   *
   * @return The mapped tenant id
   */
  public static Integer getCurrentTenant()
  {
    if (currentTenant != null)
    {
      return currentTenant.get()
    } else
    {
      return 0
    }
  }

  /**
   * This will return the name of the current tenant if that additional data is provided in the domain tenant object.  If
   * the name is not provided the mapped tenant id will be returned instead.
   *
   * @return - The name of the current tenant or the mapped tenant id if the name is not provided in the Domain
   *         Tenant Map domain object.
   */
  public static String getCurrentTenantName()
  {
    return getTenantName(getCurrentTenant())
  }

  /**
   * This will return the name of the tenant if that additional data is provided in the domain tenant object.  If
   * the name is not provided the mapped tenant id will be returned instead.
   *
   * @return - The name of the tenant or the mapped tenant id if the name is not provided in the Domain
   *         Tenant Map domain object.
   */
  public static String getTenantName(def inTenantId)
  {
    String tenantName = getCachedTenantData()?.get(inTenantId)?.name
    if (tenantName == null)
    {
      tenantName = inTenantId.toString()
    }
    return tenantName
  }

  /**
   * This will return a map of maps with the first map keyed by mapped tenant id for the data representing
   * the tenants that are active in the system.
   */
  public static getAllTenantData()
  {
    return getCachedTenantData()
  }

  /**
   * This will return a map of data representing the tenant for the mapped tenant id passed in.
   */
  public static getTenantData(def inTenantId)
  {
    return getCachedTenantData()?.get(inTenantId)
  }

  /**
   * Whether or not a particular class is annotated as MultiTenant
   *
   * @param aClass - The class to check for the multi tenant annotation.
   * @return - True if the class is annotated with the multi tenant annotation.
   */
  public static boolean isAnnotated(Class aClass)
  {
    boolean hasAnnotation = false
    if (aClass != null)
    {
      Annotation[] annotations = aClass.getAnnotations()
      for (Annotation annotation: annotations)
      {
        if (annotation instanceof MultiTenant)
        {
          hasAnnotation = true
          break
        }
      }
    }
    return hasAnnotation
  }

  /**
   * This will retrieve the cache of tenant data and return a map of that data keyed by mapped tenant id.
   */
  private static getCachedTenantData()
  {
    Cache tenantDataCache = CacheManager.getInstance().getCache(BaseDomainNameTenantResolver.TENANT_DATA_CACHE_NAME)
    if (tenantDataCache != null)
    {
      Map dataMap = tenantDataCache.getAllWithLoader(tenantDataCache.getKeys(), null)
      return dataMap
    } else
    {
      return null
    }
  }
}

