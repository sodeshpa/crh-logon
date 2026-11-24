/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.SapImportTestServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.SapImportTestServiceLocalInterface;
import com.crh.logon.persistence.entities.common.CronstatusEntity;
import com.crh.logon.persistence.entities.common.LogEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class SapImportTestServiceBean extends SapImportTestServiceDispatcher
	implements SapImportTestServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SapImportTestServiceBean.class);

	private static final String LOG_ERROR = "ERROR";

	/**
	 * This method is used to get last_run_number from tlgnc_cronstatus table
	 * 
	 * @param cronstatusName - Name of the cron job (unique). The same key is used as in application.xml.
	 * @adnlog.exclude return
	 */
	public long getLastRunNumber(String cronstatusName) {
		// [[
		Object[][] params = {{"cronstatusName", cronstatusName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLastRunNumber", params);
		}
		// ]]
		try {
			
			ReadWriteDao<CronstatusEntity> cronDao = getDaoProvider().getCronstatusEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(CronstatusEntity.ATTR_CRONSTATUS_NAME,
					cronstatusName));
			CronstatusEntity cronEntity = cronDao.findSingle(filter);
			if ((cronEntity != null) && (cronEntity.getLastRunNumber() != null)) {
				return cronEntity.getLastRunNumber().longValue();
			}
			
			return -1;
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
	 * This method is used to get the number of the errors from tlgnc_log table for given parameters
	 * 
	 * @param packageName - short name of the package (i.e. BP, COND, MAT, VEN)
	 * @param lastRunNumber - the last run number of the cron job
	 * @adnlog.exclude return
	 */
	public long getErrorsNumber(String packageName, long lastRunNumber) {
		// [[
		Object[][] params = {{"packageName", packageName},
							{"lastRunNumber", lastRunNumber + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getErrorsNumber", params);
		}
		// ]]
		try {
			
			ReadWriteDao<LogEntity> logDao = getDaoProvider().getLogEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(LogEntity.ATTR_RUN_NUMBER, lastRunNumber));
			filter.addExpression(Restrictions.ilike(LogEntity.ATTR_LOG_SOURCE, packageName));
			filter.addExpression(Restrictions.eq(LogEntity.ATTR_LOG_SEVERITY, LOG_ERROR));
			List logList = logDao.find(filter);
			
			if (logList != null) {
				return  logList.size();
			}
			return  -1;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

}
