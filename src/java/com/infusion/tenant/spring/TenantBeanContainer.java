package com.infusion.tenant.spring;

import com.infusion.tenant.TenantUtils;

import java.util.Map;
import java.util.HashMap;

import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.BeansException;

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
        Integer currentTenant = TenantUtils.getCurrentTenant();

        final Map<Integer, Object> beansByTenant = getBeansByName(beanName);

        if (!beansByTenant.containsKey(currentTenant)) {
            rtn = applicationContext.getBean(beanName);
            beansByTenant.put(currentTenant, rtn);
        } else {
            rtn = beansByTenant.get(currentTenant);
        }

        return rtn;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
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
