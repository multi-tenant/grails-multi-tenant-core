package com.infusion.tenant.hibernate;

import org.hibernate.*;
import org.hibernate.event.EventListeners;
import org.hibernate.event.EventSource;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.jdbc.Batcher;
import org.hibernate.jdbc.JDBCContext;
import org.hibernate.jdbc.Work;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.*;
import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
import org.hibernate.type.Type;

import org.hibernate.classic.Session;
import org.hibernate.stat.SessionStatistics;

import java.sql.Connection;
import java.io.Serializable;
import java.util.*;

/**
 * Wrapped "Session" object - the only thing it does is return {@link com.infusion.tenant.hibernate.TenantQuery} and
 * {@link com.infusion.tenant.hibernate.TenantCriteria} instances instead of the defaults.
 */
public class TenantSession implements EventSource, Session {
// ========================================================================================================================
//    Instance Fields
// ========================================================================================================================

    private final EventSource eventSource;
    private final Session session;
    private final SessionImplementor sessionImplementor;

// ======================================================================================================================== 
//    Constructors
// ========================================================================================================================

    /**
     * The Session provided must also implement SessionImplementor and EventSource interface.
     * @param session
     */
    public TenantSession(Session session) {
        this.session = session;
        this.sessionImplementor = (SessionImplementor) session;
        this.eventSource = (EventSource) session;
    }

// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    public void afterScrollOperation() {
        sessionImplementor.afterScrollOperation();
    }

    public void afterTransactionCompletion(boolean b, Transaction transaction) {
        sessionImplementor.afterTransactionCompletion(b, transaction);
    }

    public void doWork(Work work) throws HibernateException {
        session.doWork(work);
    }

    public void beforeTransactionCompletion(Transaction transaction) {
        sessionImplementor.beforeTransactionCompletion(transaction);
    }

    public Transaction beginTransaction() throws HibernateException {
        return session.beginTransaction();
    }

    public String bestGuessEntityName(Object o) {
        return sessionImplementor.bestGuessEntityName(o);
    }

    public void cancelQuery() throws HibernateException {
        session.cancelQuery();
    }

    public void clear() {
        session.clear();
    }

    public Connection close() throws HibernateException {
        return session.close();
    }

    public Connection connection() throws HibernateException {
        return session.connection();
    }

    public boolean contains(Object o) {
        return session.contains(o);
    }

    public Criteria createCriteria(Class aClass) {
        return new TenantCriteria(session.createCriteria(aClass));
    }

    public Criteria createCriteria(String s) {
        return new TenantCriteria(session.createCriteria(s));
    }

    public Criteria createCriteria(Class aClass, String s) {
        return new TenantCriteria(session.createCriteria(aClass, s));
    }

    public Criteria createCriteria(String s, String s1) {
        return new TenantCriteria(session.createCriteria(s, s1));
    }

    public Query createFilter(Object o, String s) throws HibernateException {
        return new TenantQuery(session.createFilter(o, s));
    }

    public Query createQuery(String s) throws HibernateException {
        return new TenantQuery(session.createQuery(s));
    }

    public SQLQuery createSQLQuery(String s) throws HibernateException {
        return session.createSQLQuery(s);
    }

    public Query createSQLQuery(String s, String s1, Class aClass) {
        return session.createSQLQuery(s, s1, aClass);
    }

    public Query createSQLQuery(String s, String[] strings, Class[] classes) {
        return session.createSQLQuery(s, strings, classes);
    }

    public int delete(String s) throws HibernateException {
        return session.delete(s);
    }

    public void delete(Object o) throws HibernateException {
        session.delete(o);
    }

    public void delete(String s, Object o) throws HibernateException {
        session.delete(s, o);
    }

    public int delete(String s, Object o, Type type) throws HibernateException {
        return session.delete(s, o, type);
    }

    public int delete(String s, Object[] objects, Type[] types) throws HibernateException {
        return session.delete(s, objects, types);
    }

    public void delete(String s, Object o, boolean b, Set set) {
        eventSource.delete(s, o, b, set);
    }

    public void disableFilter(String s) {
        session.disableFilter(s);
    }

    public Connection disconnect() throws HibernateException {
        return session.disconnect();
    }

    public Filter enableFilter(String s) {
        return session.enableFilter(s);
    }

    public void evict(Object o) throws HibernateException {
        session.evict(o);
    }

    public int executeNativeUpdate(NativeSQLQuerySpecification nativeSQLQuerySpecification, QueryParameters queryParameters) throws HibernateException {
        return sessionImplementor.executeNativeUpdate(nativeSQLQuerySpecification, queryParameters);
    }

    public int executeUpdate(String s, QueryParameters queryParameters) throws HibernateException {
        return sessionImplementor.executeUpdate(s, queryParameters);
    }

    public Collection filter(Object o, String s) throws HibernateException {
        return session.filter(o, s);
    }

    public Collection filter(Object o, String s, Object o1, Type type) throws HibernateException {
        return session.filter(o, s, o1, type);
    }

    public Collection filter(Object o, String s, Object[] objects, Type[] types) throws HibernateException {
        return session.filter(o, s, objects, types);
    }

    public List find(String s) throws HibernateException {
        return session.find(s);
    }

    public List find(String s, Object o, Type type) throws HibernateException {
        return session.find(s, o, type);
    }

    public List find(String s, Object[] objects, Type[] types) throws HibernateException {
        return session.find(s, objects, types);
    }

    public void flush() throws HibernateException {
        session.flush();
    }

    public void forceFlush(EntityEntry entityEntry) throws HibernateException {
        eventSource.forceFlush(entityEntry);
    }

    public Object get(Class aClass, Serializable serializable) throws HibernateException {
        return session.get(aClass, serializable);
    }

    public Object get(String s, Serializable serializable) throws HibernateException {
        return session.get(s, serializable);
    }

    public Object get(Class aClass, Serializable serializable, LockMode lockMode) throws HibernateException {
        return session.get(aClass, serializable, lockMode);
    }

    public Object get(String s, Serializable serializable, LockMode lockMode) throws HibernateException {
        return session.get(s, serializable, lockMode);
    }

     /*
     * ###############################################################################
     * Event Source
     * ###############################################################################
     */

    public ActionQueue getActionQueue() {
        return eventSource.getActionQueue();
    }

    public Batcher getBatcher() {
        return sessionImplementor.getBatcher();
    }

    public CacheMode getCacheMode() {
        return session.getCacheMode();
    }

    public Serializable getContextEntityIdentifier(Object o) {
        return sessionImplementor.getContextEntityIdentifier(o);
    }

    public LockMode getCurrentLockMode(Object o) throws HibernateException {
        return session.getCurrentLockMode(o);
    }

    public int getDontFlushFromFind() {
        return sessionImplementor.getDontFlushFromFind();
    }

    public Filter getEnabledFilter(String s) {
        return session.getEnabledFilter(s);
    }

    public Map getEnabledFilters() {
        return sessionImplementor.getEnabledFilters();
    }

    public EntityMode getEntityMode() {
        return session.getEntityMode();
    }

    public String getEntityName(Object o) throws HibernateException {
        return session.getEntityName(o);
    }

    public EntityPersister getEntityPersister(String s, Object o) throws HibernateException {
        return sessionImplementor.getEntityPersister(s, o);
    }

    public Object getEntityUsingInterceptor(EntityKey entityKey) throws HibernateException {
        return sessionImplementor.getEntityUsingInterceptor(entityKey);
    }

    public SessionFactoryImplementor getFactory() {
        return sessionImplementor.getFactory();
    }

    public String getFetchProfile() {
        return sessionImplementor.getFetchProfile();
    }

    public Type getFilterParameterType(String s) {
        return sessionImplementor.getFilterParameterType(s);
    }

    public Object getFilterParameterValue(String s) {
        return sessionImplementor.getFilterParameterValue(s);
    }

    public FlushMode getFlushMode() {
        return session.getFlushMode();
    }

    public Serializable getIdentifier(Object o) throws HibernateException {
        return session.getIdentifier(o);
    }

    /*
     * ###############################################################################
     * Session Implementor
     * ###############################################################################
     */

    public Interceptor getInterceptor() {
        return sessionImplementor.getInterceptor();
    }

    public JDBCContext getJDBCContext() {
        return sessionImplementor.getJDBCContext();
    }

    public EventListeners getListeners() {
        return sessionImplementor.getListeners();
    }

    public Query getNamedQuery(String s) throws HibernateException {
        return new TenantQuery(session.getNamedQuery(s));
    }

    public Query getNamedSQLQuery(String s) {
        return sessionImplementor.getNamedSQLQuery(s);
    }

    public PersistenceContext getPersistenceContext() {
        return sessionImplementor.getPersistenceContext();
    }

    public org.hibernate.Session getSession(EntityMode entityMode) {
        return session.getSession(entityMode);
    }

    public SessionFactory getSessionFactory() {
        return session.getSessionFactory();
    }

    public SessionStatistics getStatistics() {
        return session.getStatistics();
    }

    public long getTimestamp() {
        return sessionImplementor.getTimestamp();
    }

    public Transaction getTransaction() {
        return session.getTransaction();
    }

    public String guessEntityName(Object o) throws HibernateException {
        return sessionImplementor.guessEntityName(o);
    }

    public Object immediateLoad(String s, Serializable serializable) throws HibernateException {
        return sessionImplementor.immediateLoad(s, serializable);
    }

    public void initializeCollection(PersistentCollection persistentCollection, boolean b) throws HibernateException {
        sessionImplementor.initializeCollection(persistentCollection, b);
    }

    public Object instantiate(String s, Serializable serializable) throws HibernateException {
        return sessionImplementor.instantiate(s, serializable);
    }

    public Object instantiate(EntityPersister entityPersister, Serializable serializable) throws HibernateException {
        return eventSource.instantiate(entityPersister, serializable);
    }

    public Object internalLoad(String s, Serializable serializable, boolean b, boolean b1) throws HibernateException {
        return sessionImplementor.internalLoad(s, serializable, b, b1);
    }

    public boolean isClosed() {
        return sessionImplementor.isClosed();
    }

    public boolean isConnected() {
        return session.isConnected();
    }

    public boolean isDirty() throws HibernateException {
        return session.isDirty();
    }

    public boolean isEventSource() {
        return sessionImplementor.isEventSource();
    }

    public boolean isOpen() {
        return session.isOpen();
    }

    public boolean isTransactionInProgress() {
        return sessionImplementor.isTransactionInProgress();
    }

    public Iterator iterate(String s) throws HibernateException {
        return session.iterate(s);
    }

    public Iterator iterate(String s, QueryParameters queryParameters) throws HibernateException {
        return sessionImplementor.iterate(s, queryParameters);
    }

    public Iterator iterate(String s, Object o, Type type) throws HibernateException {
        return session.iterate(s, o, type);
    }

    public Iterator iterate(String s, Object[] objects, Type[] types) throws HibernateException {
        return session.iterate(s, objects, types);
    }

    public Iterator iterateFilter(Object o, String s, QueryParameters queryParameters) throws HibernateException {
        return sessionImplementor.iterateFilter(o, s, queryParameters);
    }

    public List list(CriteriaImpl criteria) {
        return sessionImplementor.list(criteria);
    }

    public List list(String s, QueryParameters queryParameters) throws HibernateException {
        return sessionImplementor.list(s, queryParameters);
    }

    public List list(NativeSQLQuerySpecification nativeSQLQuerySpecification, QueryParameters queryParameters) throws HibernateException {
        return sessionImplementor.list(nativeSQLQuerySpecification, queryParameters);
    }

    public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
        return sessionImplementor.listCustomQuery(customQuery, queryParameters);
    }

    public List listFilter(Object o, String s, QueryParameters queryParameters) throws HibernateException {
        return sessionImplementor.listFilter(o, s, queryParameters);
    }

    public Object load(Class aClass, Serializable serializable) throws HibernateException {
        return session.load(aClass, serializable);
    }

    public Object load(String s, Serializable serializable) throws HibernateException {
        return session.load(s, serializable);
    }

    public void load(Object o, Serializable serializable) throws HibernateException {
        session.load(o, serializable);
    }

    public Object load(Class aClass, Serializable serializable, LockMode lockMode) throws HibernateException {
        return session.load(aClass, serializable, lockMode);
    }

    public Object load(String s, Serializable serializable, LockMode lockMode) throws HibernateException {
        return session.load(s, serializable, lockMode);
    }

    public void lock(Object o, LockMode lockMode) throws HibernateException {
        session.lock(o, lockMode);
    }

    public void lock(String s, Object o, LockMode lockMode) throws HibernateException {
        session.lock(s, o, lockMode);
    }

    public Object merge(Object o) throws HibernateException {
        return session.merge(o);
    }

    public Object merge(String s, Object o) throws HibernateException {
        return session.merge(s, o);
    }

    public void merge(String s, Object o, Map map) throws HibernateException {
        eventSource.merge(s, o, map);
    }

    public void persist(Object o) throws HibernateException {
        session.persist(o);
    }

    public void persist(String s, Object o) throws HibernateException {
        session.persist(s, o);
    }

    public void persist(String s, Object o, Map map) throws HibernateException {
        eventSource.persist(s, o, map);
    }

    public void persistOnFlush(String s, Object o, Map map) {
        eventSource.persistOnFlush(s, o, map);
    }

    public void reconnect() throws HibernateException {
        session.reconnect();
    }

    public void reconnect(Connection connection) throws HibernateException {
        session.reconnect(connection);
    }

    public void refresh(Object o) throws HibernateException {
        session.refresh(o);
    }

    public void refresh(Object o, LockMode lockMode) throws HibernateException {
        session.refresh(o, lockMode);
    }

    public void refresh(Object o, Map map) throws HibernateException {
        eventSource.refresh(o, map);
    }

    public void replicate(Object o, ReplicationMode replicationMode) throws HibernateException {
        session.replicate(o, replicationMode);
    }

    public void replicate(String s, Object o, ReplicationMode replicationMode) throws HibernateException {
        session.replicate(s, o, replicationMode);
    }

    public Serializable save(Object o) throws HibernateException {
        return session.save(o);
    }

    public void save(Object o, Serializable serializable) throws HibernateException {
        session.save(o, serializable);
    }

    public Serializable save(String s, Object o) throws HibernateException {
        return session.save(s, o);
    }

    public void save(String s, Object o, Serializable serializable) throws HibernateException {
        session.save(s, o, serializable);
    }

    public void saveOrUpdate(Object o) throws HibernateException {
        session.saveOrUpdate(o);
    }

    public void saveOrUpdate(String s, Object o) throws HibernateException {
        session.saveOrUpdate(s, o);
    }

    public Object saveOrUpdateCopy(Object o) throws HibernateException {
        return session.saveOrUpdateCopy(o);
    }

    public Object saveOrUpdateCopy(Object o, Serializable serializable) throws HibernateException {
        return session.saveOrUpdateCopy(o, serializable);
    }

    public Object saveOrUpdateCopy(String s, Object o) throws HibernateException {
        return session.saveOrUpdateCopy(s, o);
    }

    public Object saveOrUpdateCopy(String s, Object o, Serializable serializable) throws HibernateException {
        return session.saveOrUpdateCopy(s, o, serializable);
    }

    public void saveOrUpdateCopy(String s, Object o, Map map) throws HibernateException {
        eventSource.saveOrUpdateCopy(s, o, map);
    }

    public ScrollableResults scroll(String s, QueryParameters queryParameters) throws HibernateException {
        return sessionImplementor.scroll(s, queryParameters);
    }

    public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode) {
        return sessionImplementor.scroll(criteria, scrollMode);
    }

    public ScrollableResults scroll(NativeSQLQuerySpecification nativeSQLQuerySpecification, QueryParameters queryParameters) throws HibernateException {
        return sessionImplementor.scroll(nativeSQLQuerySpecification, queryParameters);
    }

    public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
        return sessionImplementor.scrollCustomQuery(customQuery, queryParameters);
    }

    public void setAutoClear(boolean b) {
        sessionImplementor.setAutoClear(b);
    }

    public void setCacheMode(CacheMode cacheMode) {
        session.setCacheMode(cacheMode);
    }

    public void setFetchProfile(String s) {
        sessionImplementor.setFetchProfile(s);
    }

    public void setFlushMode(FlushMode flushMode) {
        session.setFlushMode(flushMode);
    }

    public void setReadOnly(Object o, boolean b) {
        session.setReadOnly(o, b);
    }

    public void update(Object o) throws HibernateException {
        session.update(o);
    }

    public void update(Object o, Serializable serializable) throws HibernateException {
        session.update(o, serializable);
    }

    public void update(String s, Object o) throws HibernateException {
        session.update(s, o);
    }

    public void update(String s, Object o, Serializable serializable) throws HibernateException {
        session.update(s, o, serializable);
    }
}