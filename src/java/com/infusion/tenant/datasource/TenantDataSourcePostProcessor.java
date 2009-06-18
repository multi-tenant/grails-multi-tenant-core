package com.infusion.tenant.datasource;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.core.Ordered;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.apache.commons.dbcp.BasicDataSource;

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
        if (JndiObjectFactoryBean.class.getName().equals(beanDefinition.getBeanClassName())) {
            beanDefinition.setBeanClassName(TenantJndiDataSource.class.getName());
        } else if (BasicDataSource.class.getName().equals(beanDefinition.getBeanClassName())) {
            beanDefinition.setBeanClassName(TenantPooledDataSource.class.getName());
        } else if (DriverManagerDataSource.class.getName().equals(beanDefinition.getBeanClassName())) {
            beanDefinition.setBeanClassName(TenantDataSourceImpl.class.getName());
        } else {
            return;
        }

        beanDefinition.setAutowireCandidate(true);
        beanDefinition.getPropertyValues().addPropertyValue("dataSourceUrlResolver", new RuntimeBeanReference("dataSourceUrlResolver"));
        beanDefinition.getPropertyValues().addPropertyValue("currentTenant", new RuntimeBeanReference("currentTenant"));
    }
}
