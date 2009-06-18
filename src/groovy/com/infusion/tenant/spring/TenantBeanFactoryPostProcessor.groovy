package com.infusion.tenant.spring;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.core.Ordered;
import org.codehaus.groovy.grails.commons.spring.RuntimeSpringConfiguration;
import org.codehaus.groovy.grails.commons.spring.DefaultRuntimeSpringConfiguration;
import org.codehaus.groovy.grails.commons.spring.BeanConfiguration;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.apache.log4j.Logger;
import com.infusion.util.CollectionUtil
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

/**
 * Examines all spring beans after all definitions have been loaded by the container, and converts
 * multiTenant beans to use the spring aop proxies.  This code executes before ANY beans are initialized.
 */
public class TenantBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

  Logger log = Logger.getLogger(TenantBeanFactoryPostProcessor.class.getName());

  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    //This cast exposes you bean registration capabilities
    DefaultListableBeanFactory applicationContext = (DefaultListableBeanFactory) beanFactory;

    //Locate and proxy multi-tenant beans
    RuntimeSpringConfiguration springConfig = new DefaultRuntimeSpringConfiguration();

    List uniquePerTenantBeans = ConfigurationHolder.config.tenant.uniquePerTenantBeans ?: [];

    for (String beanName: applicationContext.getBeanDefinitionNames()) {
      BeanDefinition oldBeanDef = applicationContext.getBeanDefinition(beanName);

      if (oldBeanDef.getBeanClassName() == null) {
        continue;
      }
      //Attempt to load the class so I can tell whether or not to proxy this bean.  Load
      //the class because we need to examine a static property.
      Class clazz = null;
      try {
        clazz = TenantBeanFactoryPostProcessor.class.getClassLoader().loadClass(oldBeanDef.getBeanClassName());
      } catch (Exception e) {
        log.error("Unabled to load class for " + oldBeanDef.getBeanClassName());
        continue;
        //throw new FatalBeanException("Error running postprocessor", e);
      }


      boolean staticMultiTenantSet = (Boolean) GrailsClassUtils.getStaticPropertyValue(clazz, "multiTenant") ?: false;
      boolean staticUniquePerTenantSet = (Boolean) GrailsClassUtils.getStaticPropertyValue(clazz, "uniquePerTenant") ?: false;
      if (staticMultiTenantSet || staticUniquePerTenantSet || uniquePerTenantBeans.contains(beanName)) {

        //Convert the original bean definition from singleton to prototype (because we'll
        //need to create one bean for each tenant)
        BeanDefinition prototypeBeanDefinition = new GenericBeanDefinition(oldBeanDef);
        prototypeBeanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        applicationContext.registerBeanDefinition("__" + beanName, prototypeBeanDefinition);

        //Just moves the original bean definition to a new location
        applicationContext.registerBeanDefinition("_" + beanName, oldBeanDef);

        //Create advice for this bean
        BeanConfiguration adviceBean = springConfig.createSingletonBean(TenantMethodInterceptor.class);
        adviceBean.addProperty("oldBeanName", "__" + beanName);
        adviceBean.addProperty("tenantBeanContainer", new RuntimeBeanReference("tenantBeanContainer"));
        applicationContext.registerBeanDefinition(beanName + "_advice", adviceBean.getBeanDefinition());

        //Create the actual proxied bean, with the same name as the original bean
        BeanConfiguration proxiedBean = springConfig.createSingletonBean(ProxyFactoryBean.class);
        proxiedBean.addProperty("target", new RuntimeBeanReference("_" + beanName));
        proxiedBean.addProperty("interceptorNames", CollectionUtil.QuickList(beanName + "_advice"));
        applicationContext.registerBeanDefinition(beanName, proxiedBean.getBeanDefinition());
      }
    }
  }

  public int getOrder() {
    return 10;
  }
}
