/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class DirectJpaDaoImpl implements DirectJpaDao {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DirectJpaDaoImpl.class);
	
	private EntityManager em;
	
	public DirectJpaDaoImpl(EntityManager em) {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"em", em}};
			LOG.entering(this, "DirectJpaDaoImpl", params);
		}
		// ]]
		try {
			this.em = em;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * Varargs are used to provide full flexibility for queries with varying number
	 * of bind parameters in the "WHERE" statement
	 * 
	 * @adnlog.exclude return
	 */
	public List find(String queryString, Object... values) { // NOPMD
		// [[
		Object[][] params = {{"queryString", queryString}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "find", params);
		}
		// ]]
		try {
			return findNRecs(0, 0, queryString, values);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	* Varargs are used to provide full flexibility for queries with varying number
	* of bind parameters in the "WHERE" statement
	*/
	public Object findSingle(String queryString, Object... values) { // NOPMD
		// [[
		Object result = null;
		Object[][] params = {{"queryString", queryString}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findSingle", params);
		}
		// ]]
		String mth = "findSingle";
		TimeTaker timeTaker = new TimeTaker();
		try {
			Query queryObject = em.createQuery(queryString);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					queryObject.setParameter(i + 1, values[i]);
				}
			}
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
			DaoUtils.reportQtime(timeTaker, mth, params);
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Varargs are used to provide full flexibility for queries with varying number
	 * of bind parameters in the "WHERE" statement
	 * 
	 * @adnlog.exclude return
	 */
	public List findNRecs(int skip, int num, String queryString, Object... values) { // NOPMD
		// [[
		Object[][] params = {{"skip", skip + ""},
				{"num", num + ""},
				{"queryString", queryString}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findNRecs", params);
		}
		// ]]
		String mth = "findNRecs";
		TimeTaker timeTaker = new TimeTaker();
		try {
			Query queryObject = em.createQuery(queryString);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					queryObject.setParameter(i + 1, values[i]);
				}
			}
			queryObject.setFirstResult(skip);
			if (num > 0) {
				queryObject.setMaxResults(num);
			}
			List result = queryObject.getResultList();
			if (logDebugEnabled) {
				LOG.debug("Size of the result list: " + result.size());
			}
			return result;
		}
		catch (RuntimeException ex) {
			LOG.error("FAILED: " + DaoUtils.trcMeth(mth, params));
			throw ex;
		}
		finally {
			DaoUtils.reportQtime(timeTaker, mth, params);
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * @adnlog.exclude return
	 */
	public List findNRecsNative(int skip, int num, String queryString, Object[] values) {
		// [[
		Object[][] params = {{"skip", skip + ""},
				{"num", num + ""},
				{"queryString", queryString},
				{"values", values}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findNRecsNative", params);
		}
		// ]]
		String mth = "findNRecsNative";
		TimeTaker timeTaker = new TimeTaker();
		try {
			Query queryObject = em.createNativeQuery(queryString);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					queryObject.setParameter(i + 1, values[i]);
				}
			}
			queryObject.setFirstResult(skip);
			if (num > 0) {
				queryObject.setMaxResults(num);
			}
			List result = queryObject.getResultList();
			if (logDebugEnabled) {
				LOG.debug("Size of the result list: " + result.size());
			}
			return result;
		}
		catch (RuntimeException ex) {
			LOG.error("FAILED: " + DaoUtils.trcMeth(mth, params));
			throw ex;
		}
		finally {
			DaoUtils.reportQtime(timeTaker, mth, params);
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * @adnlog.exclude return
	 */
	public List findNative(String queryString, Object[] values) {
		// [[
		Object[][] params = {{"queryString", queryString},
							{"values", values}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findNative", params);
		}
		// ]]
		try {
			return findNRecsNative(0, 0, queryString, values);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public int execNative(String queryString, Object[] values){
		// [[
		int result = 0;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		Object[][] params = {{"queryString", queryString},
				{"values", values}};
		if (logDebugEnabled) {
			LOG.entering(this, "execNative", params);
		}
		// ]]
		String mth = "execNative";
		TimeTaker timeTaker = new TimeTaker();
		try {
			Query queryObject = em.createNativeQuery(queryString);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					queryObject.setParameter(i + 1, values[i]);
				}
			}
			return result = queryObject.executeUpdate();
		}
		catch (RuntimeException ex) {
			LOG.error("FAILED: " + DaoUtils.trcMeth(mth, params));
			throw ex;
		}
		finally {
			DaoUtils.reportQtime(timeTaker, mth, params);
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public void flush() {
		this.em.flush();
	}

}
