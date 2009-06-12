package com.infusion.tenant.hibernate

import com.infusion.tenant.CurrentTenant
import org.hibernate.SessionFactory
import org.hibernate.event.LoadEventListener.LoadType
import org.hibernate.tuple.StandardProperty
import util.hibernate.HibernateEventUtil
import org.hibernate.event.*
import com.infusion.tenant.util.TenantUtils

/**
 * Class to inject hibernate events that validate multi-tenancy.
 *
 * These event handler methods check the annotations of the domain classes
 * being persisted to know whether or not to force restrictions.
 */
public class TenantEventHandler implements LoadEventListener, PreDeleteEventListener, PreInsertEventListener,
PreUpdateEventListener {

  private Map<String, Class> reflectedCache = [:]

  public TenantEventHandler() {
  }

  CurrentTenant currentTenant


  public void setSessionFactory(SessionFactory factory) {
    //Adds this object (TenantEventHandler) to the event handler arrays using reflection
    ["load", "preDelete", "preInsert", "preUpdate"].each {
      HibernateEventUtil.addListener(factory, it, this)
    }
  }

  public boolean onPreInsert(PreInsertEvent preInsertEvent) {
    boolean shouldFail = false;
    if (TenantUtils.isAnnotated(preInsertEvent.getEntity().getClass())) {
      Integer setTenantId = preInsertEvent.getEntity().tenantId
      if (setTenantId == 0 || setTenantId == null) {
        int currentTenant = currentTenant.get()
        preInsertEvent.getEntity().tenantId = currentTenant;
        StandardProperty[] properties = preInsertEvent.getPersister().getEntityMetamodel().getProperties()
        int found = -1;
        int i = 0;
        properties.each {StandardProperty property ->
          if (property.getName() == "tenantId") {
            found = i;
          }
          i++;
        }
        if (found > -1) {
          preInsertEvent.getState()[found] = currentTenant;
        }
      } else {
        if (setTenantId != currentTenant.get()) {
          println "Failed Insert Because TenantId Doesn't Match"
          return true;
        }
      }
    }
    return shouldFail;
  }

  public boolean onPreUpdate(PreUpdateEvent preUpdateEvent) {
    boolean shouldFail = false;
    if (TenantUtils.isAnnotated(preUpdateEvent.getEntity().getClass())) {
      Integer setTenantId = preUpdateEvent.getEntity().tenantId
      if (setTenantId != currentTenant.get()) {
        println "Failed Update Because TenantId Doesn't Match"
        shouldFail = true;
      }
    }
    return shouldFail;
  }

  /**
   * This method caches the reflected class so it can be easily inspected
   * later
   */
  private Class getClassFromName(String className) {
    if (!reflectedCache.containsKey(className)) {
      Class aClass = this.class.classLoader.loadClass("${className}")
      reflectedCache.put(className, aClass);
    }
    return reflectedCache.get(className)
  }


  public void onLoad(LoadEvent event, LoadType loadType) {
    if (TenantUtils.isAnnotated(getClassFromName(event.getEntityClassName()))) {
      Object result = event.getResult()
      if (result != null) {
        Integer loaded = result.tenantId
        int currentTenant = currentTenant.get()
        if (loaded != currentTenant && !event.isAssociationFetch()) {
          println "Trying to load record from a different app (should be ${currentTenant} but was ${loaded})"
          event.setResult null
        }
      }
    }
  }

  /**
   * Checks before deleting a record that the record is for the current tenant.  THrows an exception otherwise
   */
  public boolean onPreDelete(PreDeleteEvent event) {
    boolean shouldFail = false;
    if (TenantUtils.isAnnotated(event.entity.class)) {
      Integer setTenantId = event.getEntity().tenantId
      if (setTenantId != currentTenant.get()) {
        println "Failed Delete Because TenantId Doesn't Match"
        shouldFail = true;
      }
    }
    return shouldFail;
  }


}