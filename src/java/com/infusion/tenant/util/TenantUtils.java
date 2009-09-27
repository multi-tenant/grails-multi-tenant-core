package com.infusion.tenant.util;

import com.infusion.tenant.CurrentTenant;
import com.infusion.tenant.groovy.compiler.MultiTenant;
import groovy.lang.Closure;

import java.lang.annotation.Annotation;

import org.hibernate.SessionFactory;

/**
 * Class that provides convenience methods for dealing with the multi-tenant plugin
 */
public class TenantUtils {
    private static CurrentTenant currentTenant;
    private static SessionFactory sessionFactory;
    public static boolean ready = false;
    /**
     * This method allows you to temporarily switch tenants to perform some operations.  Before
     * the method exits, it will set the tenantId back to what it was before.
     *
     * @param tenantId
     * @param closure
     * @throws Throwable
     */
    public static void doWithTenant(Integer tenantId, Closure closure) throws Throwable {
        Integer currentTenantId = currentTenant.get();
        currentTenant.set(tenantId);
        Throwable caught = null;
        try {
            closure.call();
        } catch (Throwable t) {
            caught = t;
        } finally {
            currentTenant.set(currentTenantId);
        }
        if (caught != null) {
            throw caught;
        }
    }


    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        TenantUtils.sessionFactory = sessionFactory;
    }

    public static
    Integer getCurrentTenant() {
        if(currentTenant != null) {
            return currentTenant.get();
        } else {
            return 0;
        }
    }

    /**
     * Whether or not a particular class is annotated as MultiTenant
     *
     * @param aClass
     * @return
     */
    public static boolean isAnnotated(Class aClass) {
        boolean hasAnnotation = false;
        if (aClass != null) {
            Annotation[] annotations = aClass.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof MultiTenant) {
                    hasAnnotation = true;
                    break;
                }
            }
        }
        return hasAnnotation;
    }
}

