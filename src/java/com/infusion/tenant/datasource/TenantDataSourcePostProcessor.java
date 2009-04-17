package com.infusion.tenant.datasource;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.core.Ordered;

/**
 * Postprocessor that uses a tenant-aware implementation of datasource, that allows for datasource url switching.
 */
public class TenantDataSourcePostProcessor implements BeanFactoryPostProcessor, Ordered {
// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    public int getOrder() {
        return 5;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        final BeanDefinition beanDefinition = beanFactory.getBeanDefinition("dataSource");
        beanDefinition.setBeanClassName(TenantDataSourceImpl.class.getName());
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.getPropertyValues().addPropertyValue("dataSourceUrlResolver", new RuntimeBeanReference("dataSourceUrlResolver"));
    }
}
