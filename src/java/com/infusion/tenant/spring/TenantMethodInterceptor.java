package com.infusion.tenant.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * Method interceptor used with Spring AOP to proxy beans that need to be tenant-aware
 */
public class TenantMethodInterceptor implements MethodInterceptor {
// ========================================================================================================================
//    Instance Fields
// ========================================================================================================================

    /**
     * The old bean name
     */
    String oldBeanName;

    /**
     * The container that contains the target bean per tenant.
     */
    TenantBeanContainer tenantBeanContainer;

// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        //Get the appropriate bean for the current tenant
        final Object beanFromContainer = tenantBeanContainer.getBean(oldBeanName);

        //Identify the corresponding method we are trying to call
        final Method method = beanFromContainer.getClass().getMethod(
                methodInvocation.getMethod().getName(),
                methodInvocation.getMethod().getParameterTypes());

        //Invoke the method and return the value
        return method.invoke(beanFromContainer, methodInvocation.getArguments());
    }

    public void setOldBeanName(String oldBeanName) {
        this.oldBeanName = oldBeanName;
    }

    public void setTenantBeanContainer(TenantBeanContainer tenantBeanContainer) {
        this.tenantBeanContainer = tenantBeanContainer;
    }
}
