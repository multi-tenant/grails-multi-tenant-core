package com.infusion.tenant.hibernate;

import org.hibernate.*;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Expression;

import java.util.List;

import com.infusion.tenant.TenantUtils;

/**
 * Wrapped version of "Criteria" class, that injects a tenantId expression onto each query.  Provided by {@link TenantSession}
 */
public class TenantCriteria implements Criteria {
// ========================================================================================================================
//    Instance Fields
// ========================================================================================================================

    private final Criteria wrappedCriteria;

// ========================================================================================================================
//    Constructors
// ========================================================================================================================

    public TenantCriteria(Criteria wrappedCriteria) {
        final Integer tenant = TenantUtils.getCurrentTenant();
        if (tenant != null && tenant > 0) {
            wrappedCriteria.add(Expression.eq("tenantId", tenant));
        }
        this.wrappedCriteria = wrappedCriteria;
    }

// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    public Criteria add(Criterion criterion) {
        return wrappedCriteria.add(criterion);
    }

    public Criteria addOrder(Order order) {
        return wrappedCriteria.addOrder(order);
    }

    public Criteria createAlias(String s, String s1) throws HibernateException {
        return wrappedCriteria.createAlias(s, s1);
    }

    public Criteria createAlias(String s, String s1, int i) throws HibernateException {
        return wrappedCriteria.createAlias(s, s1, i);
    }

    public Criteria createCriteria(String s) throws HibernateException {
        return wrappedCriteria.createCriteria(s);
    }

    public Criteria createCriteria(String s, int i) throws HibernateException {
        return wrappedCriteria.createCriteria(s, i);
    }

    public Criteria createCriteria(String s, String s1) throws HibernateException {
        return wrappedCriteria.createCriteria(s, s1);
    }

    public Criteria createCriteria(String s, String s1, int i) throws HibernateException {
        return wrappedCriteria.createCriteria(s, s1, i);
    }

    public String getAlias() {
        return wrappedCriteria.getAlias();
    }

    public List list() throws HibernateException {
        return wrappedCriteria.list();
    }

    public ScrollableResults scroll() throws HibernateException {
        return wrappedCriteria.scroll();
    }

    public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
        return wrappedCriteria.scroll(scrollMode);
    }

    public Criteria setCacheMode(CacheMode cacheMode) {
        return wrappedCriteria.setCacheMode(cacheMode);
    }

    public Criteria setCacheRegion(String s) {
        return wrappedCriteria.setCacheRegion(s);
    }

    public Criteria setCacheable(boolean b) {
        return wrappedCriteria.setCacheable(b);
    }

    public Criteria setComment(String s) {
        return wrappedCriteria.setComment(s);
    }

    public Criteria setFetchMode(String s, FetchMode fetchMode) throws HibernateException {
        return wrappedCriteria.setFetchMode(s, fetchMode);
    }

    public Criteria setFetchSize(int i) {
        return wrappedCriteria.setFetchSize(i);
    }

    public Criteria setFirstResult(int i) {
        return wrappedCriteria.setFirstResult(i);
    }

    public Criteria setFlushMode(FlushMode flushMode) {
        return wrappedCriteria.setFlushMode(flushMode);
    }

    public Criteria setLockMode(LockMode lockMode) {
        return wrappedCriteria.setLockMode(lockMode);
    }

    public Criteria setLockMode(String s, LockMode lockMode) {
        return wrappedCriteria.setLockMode(s, lockMode);
    }

    public Criteria setMaxResults(int i) {
        return wrappedCriteria.setMaxResults(i);
    }

    public Criteria setProjection(Projection projection) {
        return wrappedCriteria.setProjection(projection);
    }

    public Criteria setResultTransformer(ResultTransformer resultTransformer) {
        return wrappedCriteria.setResultTransformer(resultTransformer);
    }

    public Criteria setTimeout(int i) {
        return wrappedCriteria.setTimeout(i);
    }

    public Object uniqueResult() throws HibernateException {
        return wrappedCriteria.uniqueResult();
    }
}
