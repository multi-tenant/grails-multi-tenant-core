package com.infusion.tenant.hibernate

import org.hibernate.event.PreDeleteEvent
import com.infusion.tenant.TenantUtils
import org.hibernate.event.PostLoadEvent
import org.hibernate.event.PreDeleteEvent
import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor
import org.hibernate.event.SaveOrUpdateEvent
import com.infusion.tenant.TenantUtils
import org.hibernate.event.PreLoadEvent
import org.hibernate.SessionFactory
import org.hibernate.event.EventListeners
import org.hibernate.event.LoadEventListener
import org.hibernate.event.PreLoadEventListener
import org.hibernate.event.PreDeleteEventListener
import org.hibernate.event.PreInsertEventListener
import org.hibernate.event.PreUpdateEventListener
import org.hibernate.event.PreInsertEvent
import org.hibernate.event.PreUpdateEvent
import org.hibernate.EntityMode
import org.hibernate.event.LoadEvent
import org.hibernate.event.LoadEventListener.LoadType
import org.hibernate.event.SaveOrUpdateEventListener
import org.hibernate.tuple.StandardProperty
import util.hibernate.HibernateEventUtil
import org.codehaus.groovy.grails.orm.hibernate.ConfigurableLocalSessionFactoryBean
import com.infusion.tenant.TenantUtils

/**
 * Class to inject hibernate events that validate multi-tenancy.
 */
public class TenantEventHandler implements LoadEventListener, PreDeleteEventListener, PreInsertEventListener,
PreUpdateEventListener {

  public TenantEventHandler() {
  }


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
      int currentTenant = TenantUtils.getCurrentTenant()
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
      if (setTenantId != TenantUtils.getCurrentTenant()) {
        println "Failed Insert Because TenantId Doesn't Match"
        return true;
      }
    }
    return shouldFail;
  }

  public boolean onPreUpdate(PreUpdateEvent preUpdateEvent) {
    boolean shouldFail = false;
    Integer setTenantId = preUpdateEvent.getEntity().tenantId
    if (setTenantId != TenantUtils.getCurrentTenant()) {
      println "Failed Update Because TenantId Doesn't Match"
      shouldFail = true;
    }
    return shouldFail;
  }

  public void onLoad(LoadEvent event, LoadType loadType) {
    Object result = event.getResult()
    if (result != null) {
      Integer loaded = result.tenantId
      int currentTenant = TenantUtils.getCurrentTenant()
      if (loaded != currentTenant && !event.isAssociationFetch()) {
        println "Trying to load record from a different app (should be ${TenantUtils.getCurrentTenant()} but was ${loaded})"
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
    if (setTenantId != TenantUtils.getCurrentTenant()) {
      println "Failed Delete Because TenantId Doesn't Match"
      shouldFail = true;
    }
    return shouldFail;
  }


}