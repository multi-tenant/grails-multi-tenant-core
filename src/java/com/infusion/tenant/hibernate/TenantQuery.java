package com.infusion.tenant.hibernate;

import org.hibernate.*;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;
import org.hibernate.type.IntegerType;

import java.util.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.infusion.tenant.TenantUtils;

/**
 * Wrapped version of "Query" class, that injects the current tenant as a parameter onto each hql query (the parameter should be
 * named :tenantId).  If the query
 * doesn't have a tenantId parameter, the query will fail (we want this, it helps us force that people using hql are
 * making sure to include the tenantId).  Provided by {@link TenantSession}
 */
public class TenantQuery implements Query {
// ========================================================================================================================
//    Instance Fields
// ======================================================================================================================== 

    private final Query wrapped;

// ========================================================================================================================
//    Constructors
// ========================================================================================================================

    public TenantQuery(Query wrapped) {
        for (String param : wrapped.getNamedParameters()) {
            if ("tenantId".equals(param)) {
                wrapped.setParameter("tenantId", TenantUtils.getCurrentTenant(), new IntegerType());
            }
        }
        this.wrapped = wrapped;
    }

// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    public int executeUpdate() throws HibernateException {
        return wrapped.executeUpdate();
    }

    public String[] getNamedParameters() throws HibernateException {
        return wrapped.getNamedParameters();
    }

    public String getQueryString() {
        return wrapped.getQueryString();
    }

    public String[] getReturnAliases() throws HibernateException {
        return wrapped.getReturnAliases();
    }

    public Type[] getReturnTypes() throws HibernateException {
        return wrapped.getReturnTypes();
    }

    public Iterator iterate() throws HibernateException {
        return wrapped.iterate();
    }

    public List list() throws HibernateException {
        return wrapped.list();
    }

    public ScrollableResults scroll() throws HibernateException {
        return wrapped.scroll();
    }

    public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
        return wrapped.scroll(scrollMode);
    }

    public Query setBigDecimal(int i, BigDecimal bigDecimal) {
        return wrapped.setBigDecimal(i, bigDecimal);
    }

    public Query setBigDecimal(String s, BigDecimal bigDecimal) {
        return wrapped.setBigDecimal(s, bigDecimal);
    }

    public Query setBigInteger(int i, BigInteger bigInteger) {
        return wrapped.setBigInteger(i, bigInteger);
    }

    public Query setBigInteger(String s, BigInteger bigInteger) {
        return wrapped.setBigInteger(s, bigInteger);
    }

    public Query setBinary(int i, byte[] bytes) {
        return wrapped.setBinary(i, bytes);
    }

    public Query setBinary(String s, byte[] bytes) {
        return wrapped.setBinary(s, bytes);
    }

    public Query setBoolean(int i, boolean b) {
        return wrapped.setBoolean(i, b);
    }

    public Query setBoolean(String s, boolean b) {
        return wrapped.setBoolean(s, b);
    }

    public Query setByte(int i, byte b) {
        return wrapped.setByte(i, b);
    }

    public Query setByte(String s, byte b) {
        return wrapped.setByte(s, b);
    }

    public Query setCacheMode(CacheMode cacheMode) {
        return wrapped.setCacheMode(cacheMode);
    }

    public Query setCacheRegion(String s) {
        return wrapped.setCacheRegion(s);
    }

    public Query setCacheable(boolean b) {
        return wrapped.setCacheable(b);
    }

    public Query setCalendar(int i, Calendar calendar) {
        return wrapped.setCalendar(i, calendar);
    }

    public Query setCalendar(String s, Calendar calendar) {
        return wrapped.setCalendar(s, calendar);
    }

    public Query setCalendarDate(int i, Calendar calendar) {
        return wrapped.setCalendarDate(i, calendar);
    }

    public Query setCalendarDate(String s, Calendar calendar) {
        return wrapped.setCalendarDate(s, calendar);
    }

    public Query setCharacter(int i, char c) {
        return wrapped.setCharacter(i, c);
    }

    public Query setCharacter(String s, char c) {
        return wrapped.setCharacter(s, c);
    }

    public Query setComment(String s) {
        return wrapped.setComment(s);
    }

    public Query setDate(int i, Date date) {
        return wrapped.setDate(i, date);
    }

    public Query setDate(String s, Date date) {
        return wrapped.setDate(s, date);
    }

    public Query setDouble(int i, double v) {
        return wrapped.setDouble(i, v);
    }

    public Query setDouble(String s, double v) {
        return wrapped.setDouble(s, v);
    }

    public Query setEntity(int i, Object o) {
        return wrapped.setEntity(i, o);
    }

    public Query setEntity(String s, Object o) {
        return wrapped.setEntity(s, o);
    }

    public Query setFetchSize(int i) {
        return wrapped.setFetchSize(i);
    }

    public Query setFirstResult(int i) {
        return wrapped.setFirstResult(i);
    }

    public Query setFloat(int i, float v) {
        return wrapped.setFloat(i, v);
    }

    public Query setFloat(String s, float v) {
        return wrapped.setFloat(s, v);
    }

    public Query setFlushMode(FlushMode flushMode) {
        return wrapped.setFlushMode(flushMode);
    }

    public Query setInteger(int i, int i1) {
        return wrapped.setInteger(i, i1);
    }

    public Query setInteger(String s, int i) {
        return wrapped.setInteger(s, i);
    }

    public Query setLocale(int i, Locale locale) {
        return wrapped.setLocale(i, locale);
    }

    public Query setLocale(String s, Locale locale) {
        return wrapped.setLocale(s, locale);
    }

    public Query setLockMode(String s, LockMode lockMode) {
        return wrapped.setLockMode(s, lockMode);
    }

    public Query setLong(int i, long l) {
        return wrapped.setLong(i, l);
    }

    public Query setLong(String s, long l) {
        return wrapped.setLong(s, l);
    }

    public Query setMaxResults(int i) {
        return wrapped.setMaxResults(i);
    }

    public Query setParameter(int i, Object o) throws HibernateException {
        return wrapped.setParameter(i, o);
    }

    public Query setParameter(String s, Object o) throws HibernateException {
        return wrapped.setParameter(s, o);
    }

    public Query setParameter(int i, Object o, Type type) {
        return wrapped.setParameter(i, o, type);
    }

    public Query setParameter(String s, Object o, Type type) {
        return wrapped.setParameter(s, o, type);
    }

    public Query setParameterList(String s, Collection collection) throws HibernateException {
        return wrapped.setParameterList(s, collection);
    }

    public Query setParameterList(String s, Object[] objects) throws HibernateException {
        return wrapped.setParameterList(s, objects);
    }

    public Query setParameterList(String s, Collection collection, Type type) throws HibernateException {
        return wrapped.setParameterList(s, collection, type);
    }

    public Query setParameterList(String s, Object[] objects, Type type) throws HibernateException {
        return wrapped.setParameterList(s, objects, type);
    }

    public Query setParameters(Object[] objects, Type[] types) throws HibernateException {
        return wrapped.setParameters(objects, types);
    }

    public Query setProperties(Object o) throws HibernateException {
        return wrapped.setProperties(o);
    }

    public Query setProperties(Map map) throws HibernateException {
        return wrapped.setProperties(map);
    }

    public Query setReadOnly(boolean b) {
        return wrapped.setReadOnly(b);
    }

    public Query setResultTransformer(ResultTransformer resultTransformer) {
        return wrapped.setResultTransformer(resultTransformer);
    }

    public Query setSerializable(int i, Serializable serializable) {
        return wrapped.setSerializable(i, serializable);
    }

    public Query setSerializable(String s, Serializable serializable) {
        return wrapped.setSerializable(s, serializable);
    }

    public Query setShort(int i, short s) {
        return wrapped.setShort(i, s);
    }

    public Query setShort(String s, short i) {
        return wrapped.setShort(s, i);
    }

    public Query setString(int i, String s) {
        return wrapped.setString(i, s);
    }

    public Query setString(String s, String s1) {
        return wrapped.setString(s, s1);
    }

    public Query setText(int i, String s) {
        return wrapped.setText(i, s);
    }

    public Query setText(String s, String s1) {
        return wrapped.setText(s, s1);
    }

    public Query setTime(int i, Date date) {
        return wrapped.setTime(i, date);
    }

    public Query setTime(String s, Date date) {
        return wrapped.setTime(s, date);
    }

    public Query setTimeout(int i) {
        return wrapped.setTimeout(i);
    }

    public Query setTimestamp(int i, Date date) {
        return wrapped.setTimestamp(i, date);
    }

    public Query setTimestamp(String s, Date date) {
        return wrapped.setTimestamp(s, date);
    }

    public Object uniqueResult() throws HibernateException {
        return wrapped.uniqueResult();
    }
}
