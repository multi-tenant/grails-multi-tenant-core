package com.infusion.tenant.hibernate;

import org.codehaus.groovy.grails.orm.hibernate.ConfigurableLocalSessionFactoryBean;
import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;

/**
 * Created by IntelliJ IDEA.
 * User: eric
 * Date: Mar 31, 2009
 * Time: 5:07:41 PM
 */
public class TenantConfigurableSessionFactoryBean extends ConfigurableLocalSessionFactoryBean {
    
    @Override
    protected SessionFactory newSessionFactory(Configuration configuration) throws HibernateException {
        return new TenantSessionFactory(super.newSessionFactory(configuration));
    }




}
