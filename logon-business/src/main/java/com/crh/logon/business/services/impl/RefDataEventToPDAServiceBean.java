/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.RefDataEventToPDAServiceLocalInterface;
import com.crh.logon.business.services.dispatchers.RefDataEventToPDAServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.FuncEntity;

/**
 * This serviceBean is supposed to do be responsible for operations related to RFIDObjects, and RFidtag
 * objects.
 * 
 * @author AdNovum Informatik AG
 */
@Stateless
public class RefDataEventToPDAServiceBean extends RefDataEventToPDAServiceDispatcher 
	implements RefDataEventToPDAServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RefDataEventToPDAServiceBean.class);
	
	protected @EJB RFIDServiceLocalInterface rfidService;

	public List fetchFuncList() {
		// [[
		List result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchFuncList");
		}
		// ]]
		try {
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.ge(FuncEntity.ATTR_FUNC_ID, Constant.FUNCTION_GROUP_PDA_MIN));
			filter.addExpression(Restrictions.le(FuncEntity.ATTR_FUNC_ID, Constant.FUNCTION_GROUP_PDA_MAX));
			List funcs = getDaoProvider().getFuncEntityDao().find(filter);
			
			return result = funcs;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public FuncEntity fetchFuncById(Long funcId) {
		// [[
		FuncEntity result = null;
		Object[][] params = {{"funcId", funcId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchFuncById", params);
		}
		// ]]
		try {
			ReadWriteDao<FuncEntity> funcEntityDao = getDaoProvider().getFuncEntityDao();
			return result = funcEntityDao.findById(funcId);
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
