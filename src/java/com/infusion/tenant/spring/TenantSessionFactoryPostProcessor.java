package com.infusion.tenant.spring;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.BeansException;
import com.infusion.tenant.hibernate.TenantConfigurableSessionFactoryBean;

/**
 * Created by IntelliJ IDEA.
 * User: eric
 * Date: Apr 1, 2009
 * Time: 10:52:27 PM
 */
public class TenantSessionFactoryPostProcessor implements BeanFactoryPostProcessor {
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //Handle SessionFactory
        final BeanDefinition sessionFactoryBeanDefinition = beanFactory.getBeanDefinition("sessionFactory");
        sessionFactoryBeanDefinition.setBeanClassName(TenantConfigurableSessionFactoryBean.class.getName());

    }

}
