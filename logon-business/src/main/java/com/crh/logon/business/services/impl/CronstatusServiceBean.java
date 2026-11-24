/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.persistence.OptimisticLockException;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.CronstatusServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.CronstatusServiceLocalInterface;
import com.crh.logon.business.stores.sap.StagingAreaNewDataAvailableStoreInterface;
import com.crh.logon.persistence.entities.common.CronstatusEntity;
import com.crh.logon.persistence.entities.views.stagingarea.StagingAreaNewDataAvailableBaseEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class CronstatusServiceBean extends CronstatusServiceDispatcher implements CronstatusServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CronstatusServiceBean.class);	
	
		
	@EJB StagingAreaNewDataAvailableStoreInterface stagingAreaStore;
	
	/**
	 * @param cronstatusName
	 * @return
	 */
	public CronstatusEntity findCronstatusByName(String cronstatusName) {
		// [[
		CronstatusEntity result = null;
		Object[][] params = {{"cronstatusName", cronstatusName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findCronstatusByName", params);
		}
		// ]]
		try {
			if (cronstatusName == null) {				
				throw new LogonException("cronstatusName cannot be null", Reason.RES_IMPL_BUG);
			}
			final ReadOnlyDao<CronstatusEntity> dao = getDaoProvider().getCronstatusEntityDao();
			final Filter<CronstatusEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(CronstatusEntity.ATTR_CRONSTATUS_NAME, cronstatusName));
			result = dao.findSingle(filter);
			
			if (result == null) {				
				throw new LogonException("No CronStatusEntity has been found for the provided name. " + cronstatusName, Reason.RES_ERROR_IMPL, params);
			}
			
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
		
	private Date getCurrentTimestamp(CronstatusEntity cronstatus) {
		
		// "SELECT CURRENT_TIMESTAMP" is not enough, we must have a FROM clause. 
		return (Date)getDaoProvider().getDirectJpaDao().findSingle("SELECT CURRENT_TIMESTAMP FROM " + CronstatusEntity.class.getSimpleName() + " WHERE " + CronstatusEntity.ATTR_CRONSTATUS_NAME + " = ?1", cronstatus.getCronstatusName());
	}
	
	public boolean isNewDataAvailable(String cronstatusName) {
		// [[
		boolean result = false;
		Object[][] params = {{"cronstatusName", cronstatusName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isNewDataAvailable", params);
		}
		// ]]
		try {
			final CronstatusEntity cronstatus = findCronstatusByName(cronstatusName);
			
			final Date lastCalled = cronstatus.getLastStartTs();
			final Date lastExecuted = cronstatus.getLastEndTs();
			
			StagingAreaNewDataAvailableBaseEntity unprocessedRequest = stagingAreaStore.getLastNewDataAvailableForCronName(cronstatusName);
			if ( unprocessedRequest == null ) {
				LOG.info("There are no unprocessed requests for " + cronstatusName);
				return false;
			}

			return result = true;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public boolean isCronJobRunning(String cronstatusName) {
		// [[
		boolean result = false;
		Object[][] params = {{"cronstatusName", cronstatusName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isCronJobRunning", params);
		}
		// ]]
		try {
			final CronstatusEntity cronstatus = findCronstatusByName(cronstatusName);
			
			// get rid of the java.sql.timestamp
			final Date lastStartDate = new Date(cronstatus.getLastStartTs().getTime());
			final Date lastEndDate = new Date(cronstatus.getLastEndTs().getTime());
			return result = lastStartDate.after(lastEndDate);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
		
		@AdnTrace
		@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
		public void _startCronJob(String cronstatusName) {
			if (cronstatusName == null) {
				throw new LogonException("cronstatusName cannot be null", Reason.RES_IMPL_BUG);
			}

			final CronstatusEntity cronstatus = findCronstatusByName(cronstatusName);
			final Date currentTs = getCurrentTimestamp(cronstatus);
			cronstatus.setLastStartTs(currentTs);
		}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _finishCronJob(String cronstatusName) {
		// [[
		Object[][] params = {{"cronstatusName", cronstatusName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "_finishCronJob", params);
		}
		// ]]
		try {
			final CronstatusEntity cronstatus = findCronstatusByName(cronstatusName);
			final Date currentTs = getCurrentTimestamp(cronstatus);
			cronstatus.setLastEndTs(currentTs);
			em.flush();
		} catch (OptimisticLockException e) {
			LOG.error(String.format(
					"OptimisticLockException finishing cronjob %s.",
					cronstatusName));
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public long _increaseCronjobRunNumber(String cronstatusName) {
		// [[
		long result = 0L;
		Object[][] params = {{"cronstatusName", cronstatusName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "_increaseCronjobRunNumber", params);
		}
		// ]]
		try {
			Long newNumber = null;
			CronstatusEntity cronstatus;
			Long runNumber;
			int tries = 3;
			while (newNumber == null) {
				try {
					cronstatus = findCronstatusByName(cronstatusName);
					runNumber = cronstatus.getLastRunNumber();
					newNumber = (runNumber == null ? 1 : runNumber.longValue() + 1);					
					cronstatus.setLastRunNumber(newNumber);
					em.flush();
				} catch (OptimisticLockException e) {
					LOG.error(String.format(
							"OptimisticLockException increaseCronjobRunNumber for cronjob %s.",
							cronstatusName));
					newNumber = null;
					if (--tries <= 0) {
						throw e;
					}
				}
			}

			return result = newNumber;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
}
