package com.infusion.tenant.spring;

import com.infusion.tenant.CurrentTenant;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Container that holds an instance of a bean for each tenant in your system.
 */
public class TenantBeanContainer implements ApplicationContextAware {
// ========================================================================================================================
//    Instance Fields
// ========================================================================================================================

    /**
     * The spring application context, used to create new beans when needed
     */
    private ApplicationContext applicationContext;

    /**
     * The store for all beans, by beanName->tenantId
     */
    private Map<String, Map<Integer, Object>> beans = new HashMap<String, Map<Integer, Object>>();

    /**
     * This class knows what the current tenant is
     */
    private CurrentTenant currentTenant;

// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    /**
     * Clears out all beans for all tenants
     */
    public void clear() {
        beans.clear();
    }

    /**
     * Returns the bean with the given name, for the current tenant
     *
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        final Object rtn;
        Integer currentTenantId = currentTenant.get();

        final Map<Integer, Object> beansByTenant = getBeansByName(beanName);

        if (!beansByTenant.containsKey(currentTenantId)) {
            rtn = applicationContext.getBean(beanName);
            beansByTenant.put(currentTenantId, rtn);
        } else {
            rtn = beansByTenant.get(currentTenantId);
        }

        return rtn;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setCurrentTenant(CurrentTenant currentTenant) {
        this.currentTenant = currentTenant;
    }

// ========================================================================================================================
//    Non-Public Instance Methods
// ========================================================================================================================

    private Map<Integer, Object> getBeansByName(String beanName) {
        if (!beans.containsKey(beanName)) {
            beans.put(beanName, new HashMap<Integer, Object>());
        }
        return beans.get(beanName);
    }
}
