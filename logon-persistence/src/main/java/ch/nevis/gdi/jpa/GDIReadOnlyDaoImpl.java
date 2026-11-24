/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.GDIJPAImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.JPAQueryImpl;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.gdi.jpa.filter.ThreadLocalIdentificationVariable;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class GDIReadOnlyDaoImpl<T extends BaseEntity> implements ReadOnlyDao<T> {
    
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(GDIReadOnlyDaoImpl.class);
    
	protected EntityManager em;
	private Authorizer authorizer;
	
	protected Class<T> clazz;
	private String joinFetchEntityName;
	
	public GDIReadOnlyDaoImpl(EntityManager em, Authorizer authorizer, Class<T> clazz) {
		this.em = em;
		this.authorizer = authorizer;
		this.clazz = clazz;
	}

	private Filter<T> addAccessRestriction(Filter<T> filter) {
		Filter<T> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"filter", filter}};
			LOG.entering(this, "addAccessRestriction", params);
		}
		try {
			if (authorizer == null) {
				return result = filter;
			}
			// data access restriction
			Expression expr = authorizer.getRestriction(em, filter, clazz.getSimpleName(), ThreadLocalIdentificationVariable.getIdentificationVariable());
			if (expr != null) {
				if (filter == null) {
					filter = new FilterImpl<T>();
				}
				filter.addExpression(expr);
			}
			return result = filter;
		}
		finally {
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		}
	}
	
	private final Query prepareQuery(JPAQuery<T> jpaQuery) {
		Query result = null;
		Object[][] params = {{"jpaQuery", jpaQuery}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "prepareQuery", params);
		}
		try {
			StringBuilder query = new StringBuilder();
			query = jpaQuery.toJPAQL();
			if (logDebugEnabled) {
				LOG.debug("Preparing query: " + query);
			}
			Query queryObject = em.createQuery(query.toString());
			
			Filter<T> filter = jpaQuery.getFilter();
			if (filter != null) {
				Map<String, Object> inputParameters = filter.getInputParameters();
				for (Iterator<Entry<String, Object>> iterator = inputParameters.entrySet().iterator(); iterator.hasNext();) {
					Entry<String, Object> entry = iterator.next();
					queryObject.setParameter(entry.getKey(), entry.getValue());
				}
			}
			
			return result = queryObject;
		}
		finally {
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		}
	}
	
	public T findById(Object id) {
		return em.find(clazz, id);
	}
	
	public List<T> find(Filter<T> filter) {
		List<T> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"filter", filter}};
			LOG.entering(this, "find", params);
		}
		try {
			return result = findNRecs(0, 0, filter);
		}
		finally {
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		}
	}

	public List<T> findNRecs(int skip, int num, Filter<T> filter) {
		List<T> result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"filter", filter}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findNRecs", params);
		}
		String mth = "findNRecs";
		TimeTaker timeTaker = new TimeTaker();
		try {
			JPAQuery<T> jpaQuery = createJPAQuery();
			if (joinFetchEntityName != null) {
				((GDIJPAImpl<T>)jpaQuery).setJoinFetchEntityName(joinFetchEntityName);
			}
			// data access restriction
			filter = addAccessRestriction(filter);
			if (filter == null) {
				filter = createFilter();
			}
			jpaQuery.setFilter(filter);
			
			Query queryObject = prepareQuery(jpaQuery);
			queryObject.setFirstResult(skip);
			if (num > 0) {
				queryObject.setMaxResults(num);
			}
			List<T> resultList = queryObject.getResultList();
			if (resultList == null) {
				resultList = new ArrayList<T>();
			}
			return result = resultList;
		}
		catch (RuntimeException ex) {
			LOG.error("FAILED: " + DaoUtils.trcMeth(mth, params));
			throw ex;
		}
		finally {
			DaoUtils.reportQtime(clazz.getSimpleName(), timeTaker, mth, params);
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		}
	}

	public T findSingle(Filter<T> filter) {
		T result = null;
		Object[][] params = {{"filter", filter}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findSingle", params);
		}
		String mth = "findSingle";
		TimeTaker timeTaker = new TimeTaker();
		try {
			JPAQuery<T> jpaQuery = createJPAQuery();
			
			// data access restriction
			filter = addAccessRestriction(filter);
			if (filter == null) {
				filter = createFilter();
			}
			jpaQuery.setFilter(filter);
			
			Query queryObject = prepareQuery(jpaQuery);
			return result = (T) queryObject.getSingleResult();
		}
		catch (NoResultException ex) { // this is OK!
    		return result = null;
		}
		catch (RuntimeException ex) {
			LOG.error("FAILED: " + DaoUtils.trcMeth(mth, params));
			throw ex;
		}
		finally {
			DaoUtils.reportQtime(clazz.getSimpleName(), timeTaker, mth, params);
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		}
	}

	public Object findSingle(JPAQuery<T> jpaQuery) {
		Object result = null;
		Object[][] params = {{"jpaQuery", jpaQuery}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findSingle", params);
		}
		String mth = "findSingle";
		TimeTaker timeTaker = new TimeTaker();
		try {
			Filter<T> filter = jpaQuery.getFilter();
			
			// data access restriction
			filter = addAccessRestriction(filter);
			if (filter == null) {
				filter = createFilter();
			}
			jpaQuery.setFilter(filter);
			
			Query queryObject = prepareQuery(jpaQuery);
			return result = queryObject.getSingleResult();
		}
		catch (NoResultException ex) { // this is OK!
			return result = null;
		}
		catch (RuntimeException ex) {
			LOG.error("FAILED: " + DaoUtils.trcMeth(mth, params));
			throw ex;
		}
		finally {
			DaoUtils.reportQtime(clazz.getSimpleName(), timeTaker, mth, params);
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		}
	}
	
	public List find(JPAQuery<T> jpaQuery) {
		List result = null;
		Object[][] params = {{"jpaQuery", jpaQuery}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "find", params);
		}
		try {
			return result = findNRecs(0, 0, jpaQuery);
		}
		finally {
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		}
	}
	
	public List findNRecs(int skip, int num, JPAQuery<T> jpaQuery) {
		List result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"jpaQuery", jpaQuery}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findNRecs", params);
		}
		String mth = "findNRecs";
		TimeTaker timeTaker = new TimeTaker();
		try {
			Filter<T> filter = jpaQuery.getFilter();	
			
			// data access restriction
			filter = addAccessRestriction(filter);
			if (filter == null) {
				filter = createFilter();
			}
			jpaQuery.setFilter(filter);
			
			Query queryObject = prepareQuery(jpaQuery);
			queryObject.setFirstResult(skip);
			if (num > 0) {
				queryObject.setMaxResults(num);
			}
			List resultList = queryObject.getResultList();
			if (resultList == null) {
				resultList = new ArrayList();
			}
			return result = resultList;
		}
		catch (NoResultException ex) { // this is OK!
    		return result = null;
		}
		catch (RuntimeException ex) {
			LOG.error("FAILED: " + DaoUtils.trcMeth(mth, params));
			throw ex;
		}
		finally {
			DaoUtils.reportQtime(clazz.getSimpleName(), timeTaker, mth, params);
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		}
	}
	
	public long count(Filter<T> filter) {
		long result = 0L;
		Object[][] params = {{"filter", filter}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "count", params);
		}
		String mth = "count";
		TimeTaker timeTaker = new TimeTaker();
		try {
			JPAQuery<T> jpaQuery = createJPAQuery();
			jpaQuery.addSelect(SelectExpression.count());
			
			// data access restriction
			filter = addAccessRestriction(filter);
			if (filter == null) {
				filter = createFilter();
			}
			jpaQuery.setFilter(filter);
			
			Query queryObject = prepareQuery(jpaQuery); 
			return result = (Long) queryObject.getSingleResult();
		}
		catch (NoResultException ex) { // this is OK!
			return result = 0L;
		}
		catch (RuntimeException ex) {
			LOG.error("FAILED: " + DaoUtils.trcMeth(mth, params));
			throw ex;
		}
		finally {
			DaoUtils.reportQtime(clazz.getSimpleName(), timeTaker, mth, params);
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		}
	}

	public Filter<T> createFilter() {
		return new FilterImpl<T>();
	}

	public JPAQuery<T> createJPAQuery() {
		return new GDIJPAImpl<T>(clazz);
	}

	public String getJoinFetchEntityName() {
		return joinFetchEntityName;
	}

	public void setJoinFetchEntityName(String joinFetchEntityName) {
		this.joinFetchEntityName = joinFetchEntityName;
	}
	
}
