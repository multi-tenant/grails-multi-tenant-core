package com.infusion.tenant.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.Interceptor;
import org.hibernate.HibernateException;
import org.hibernate.StatelessSession;
import org.hibernate.event.EventListeners;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.stat.Statistics;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.classic.Session;

import javax.naming.Reference;
import javax.naming.NamingException;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;

/**
 * Wrapped "SessionFactory" class that returns custom TenantSession instances.
 */
public class TenantSessionFactory implements SessionFactory {
// ========================================================================================================================
//    Instance Fields
// ========================================================================================================================

    private SessionFactory wrapped;

// ========================================================================================================================
//    Constructors
// ========================================================================================================================

    public TenantSessionFactory() {
    }

    public TenantSessionFactory(SessionFactory wrapped) {
        this.wrapped = wrapped;
    }

// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    public void close() throws HibernateException {
        wrapped.close();
    }

    public void evict(Class aClass) throws HibernateException {
        wrapped.evict(aClass);
    }

    public void evict(Class aClass, Serializable serializable) throws HibernateException {
        wrapped.evict(aClass, serializable);
    }

    public void evictCollection(String s) throws HibernateException {
        wrapped.evictCollection(s);
    }

    public void evictCollection(String s, Serializable serializable) throws HibernateException {
        wrapped.evictCollection(s, serializable);
    }

    public void evictEntity(String s) throws HibernateException {
        wrapped.evictEntity(s);
    }

    public void evictEntity(String s, Serializable serializable) throws HibernateException {
        wrapped.evictEntity(s, serializable);
    }

    public void evictQueries() throws HibernateException {
        wrapped.evictQueries();
    }

    public void evictQueries(String s) throws HibernateException {
        wrapped.evictQueries(s);
    }

    public Map getAllClassMetadata() throws HibernateException {
        return wrapped.getAllClassMetadata();
    }

    public Map getAllCollectionMetadata() throws HibernateException {
        return wrapped.getAllCollectionMetadata();
    }

    public ClassMetadata getClassMetadata(Class aClass) throws HibernateException {
        return wrapped.getClassMetadata(aClass);
    }

    public ClassMetadata getClassMetadata(String s) throws HibernateException {
        return wrapped.getClassMetadata(s);
    }

    public CollectionMetadata getCollectionMetadata(String s) throws HibernateException {
        return wrapped.getCollectionMetadata(s);
    }

    public Session getCurrentSession() throws HibernateException {
        return wrapped.getCurrentSession();
    }

    public Set getDefinedFilterNames() {
        return wrapped.getDefinedFilterNames();
    }

    /**
     * This isn't an actual method from the SessionFactory interface, but we duck-type implemented it here
     * because most often the wrapped SessionFactory will actually be a SessionFactoryImpl (and places this
     * class is wired to will expect the method to exist)
     *
     */
    public EventListeners getEventListeners() {
        EventListeners rtn = null;
        if (wrapped instanceof SessionFactoryImpl) {
            rtn = ((SessionFactoryImpl) wrapped).getEventListeners();
        }
        return rtn;
    }

    public FilterDefinition getFilterDefinition(String s) throws HibernateException {
        return wrapped.getFilterDefinition(s);
    }

    public Reference getReference() throws NamingException {
        return wrapped.getReference();
    }

    public Statistics getStatistics() {
        return wrapped.getStatistics();
    }

    public SessionFactory getWrapped() {
        return wrapped;
    }

    public boolean isClosed() {
        return wrapped.isClosed();
    }

    public Session openSession() throws HibernateException {
        return new TenantSession(wrapped.openSession());
    }

    public Session openSession(Connection connection) {
        final Session session = wrapped.openSession(connection);
        return new TenantSession(session);
    }

    public Session openSession(Interceptor interceptor) throws HibernateException {
        final Session session = wrapped.openSession(interceptor);
        return new TenantSession(session);
    }

    public Session openSession(Connection connection, Interceptor interceptor) {
        final Session session = wrapped.openSession(connection, interceptor);
        return new TenantSession(session);
    }

    public StatelessSession openStatelessSession() {
        return wrapped.openStatelessSession();
    }

    public StatelessSession openStatelessSession(Connection connection) {
        return wrapped.openStatelessSession(connection);
    }

    public void setWrapped(SessionFactory wrapped) {
        this.wrapped = wrapped;
    }
}
