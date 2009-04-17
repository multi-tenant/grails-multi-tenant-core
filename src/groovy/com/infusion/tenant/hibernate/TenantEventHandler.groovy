package com.infusion.tenant.hibernate

import com.infusion.tenant.CurrentTenant
import org.hibernate.SessionFactory
import org.hibernate.event.LoadEventListener.LoadType
import org.hibernate.tuple.StandardProperty
import util.hibernate.HibernateEventUtil
import org.hibernate.event.*

/**
 * Class to inject hibernate events that validate multi-tenancy.
 */
public class TenantEventHandler implements LoadEventListener, PreDeleteEventListener, PreInsertEventListener,
PreUpdateEventListener {

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
    Integer setTenantId = preInsertEvent.getEntity().tenantId
    if (setTenantId == 0 || setTenantId == null) {
      int currentTenant = currentTenant.get()
      preInsertEvent.getEntity().tenantId = currentTenant;
      StandardProperty[] properties = preInsertEvent.getPersister().getEntityMetamodel().getProperties()
      int found=-1;
      int i = 0;
      properties.each {StandardProperty property ->
        if(property.getName() == "tenantId") {
          found = i;
        }
        i++;
      }
      if(found > -1) {
        preInsertEvent.getState()[found] = currentTenant;
      }
    } else {
      if (setTenantId != currentTenant.get()) {
        println "Failed Insert Because TenantId Doesn't Match"
        return true;
      }
    }
    return shouldFail;
  }

  public boolean onPreUpdate(PreUpdateEvent preUpdateEvent) {
    boolean shouldFail = false;
    Integer setTenantId = preUpdateEvent.getEntity().tenantId
    if (setTenantId != currentTenant.get()) {
      println "Failed Update Because TenantId Doesn't Match"
      shouldFail = true;
    }
    return shouldFail;
  }

  public void onLoad(LoadEvent event, LoadType loadType) {
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

  /**
   * Checks before deleting a record that the record is for the current tenant.  THrows an exception otherwise
   */
  public boolean onPreDelete(PreDeleteEvent event) {
    boolean shouldFail = false;
    Integer setTenantId = event.getEntity().tenantId
    if (setTenantId != currentTenant.get()) {
      println "Failed Delete Because TenantId Doesn't Match"
      shouldFail = true;
    }
    return shouldFail;
  }


}