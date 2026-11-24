/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa;

import javax.persistence.EntityManager;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class ReadWriteDaoImpl<T extends BaseEntity>
extends ReadOnlyDaoImpl<T>
implements ReadWriteDao<T>
{
    
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReadWriteDaoImpl.class);
    
	public ReadWriteDaoImpl(EntityManager em, Authorizer authorizer, Class<T> clazz) {
		super(em, authorizer, clazz);
	}

	public T persist(T entity) {
		return persist(entity, true);
	}

	public T persist(T entity, boolean doFlush) {
		T result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		Object[][] params = {{"entity", entity}, {"doFlush", doFlush}};
		if (logDebugEnabled) {
			LOG.entering(this, "persist", params);
		}
		String mth = "persist";
		TimeTaker timeTaker = new TimeTaker();
		try {
			em.persist(entity);
			if (doFlush) {
				em.flush();
			}
			return result = entity;
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
	
	public T merge(T entity) {
		return merge(entity, true);
	}	

	public T merge(T entity, boolean doFlush) {
		T result = null;
		Object[][] params = {{"entity", entity}, {"doFlush", doFlush}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "merge", params);
		}
		String mth = "merge";
		TimeTaker timeTaker = new TimeTaker();
		try {
			entity = em.merge(entity);
			if (doFlush) {
				em.flush();
			}
			return result = entity;
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
	
	public void remove(final T entity) {
		remove(entity, true);
	}
	
	public void remove(final T entity, boolean doFlush) {
		Object[][] params = {{"entity", entity}, {"doFlush", doFlush}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "remove", params);
		}
		String mth = "remove";
		TimeTaker timeTaker = new TimeTaker();
		try {
			em.remove(entity);
			if (doFlush) {
				em.flush();
			}
			return;
		}
		catch (RuntimeException ex) {
			LOG.error("FAILED: " + DaoUtils.trcMeth(mth, params));
			throw ex;
		}
		finally {
			DaoUtils.reportQtime(clazz.getSimpleName(), timeTaker, mth, params);
			if (logDebugEnabled) {
				LOG.exiting();
			}
		}
	}

}
