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
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.ExternalSiteServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.ExternalSiteServiceLocalInterface;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ExtSiteEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class ExternalSiteServiceBean extends ExternalSiteServiceDispatcher 
	implements ExternalSiteServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ExternalSiteServiceBean.class);

	
	/**
	 * Fetches all carriers.
	 * @adnlog.exclude return
	 */
	public List fetchCarriers() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCarriers");
		}
		// ]]
		try {
			ReadWriteDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(CarrierEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			filter.addExpression(Order.asc(CarrierEntity.ATTR_DISPLAY_NAME));
			return dao.find(filter);
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
	 * Fetch a list of site entries mapped to the give carrierId.
	 * @param carrierId
	 * @param excludeInactive - if true the result list will <b>not</b> contain the non-active elements
	 * (they will be excluded) ! 
	 * @return - the mapped sites for the given carrier 
	 */
	public List fetchSiteForCarrier(long carrierId, boolean excludeInactive) {
		// [[
		List result = null;
		Object[][] params = { { "carrierId", carrierId + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSiteForCarrier", params);
		}
		// ]]
		try {
			ReadWriteDao<ExtSiteEntity> dao = getDaoProvider().getExtSiteEntityDao();

			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(ExtSiteEntity.ATTR_CARRIER_ID, carrierId));
			if (excludeInactive) {
				filter.addExpression(Restrictions.eq(ExtSiteEntity.ATTR_ACTIVE_FLAG, true));
			}
			filter.addExpression(Order.asc(ExtSiteEntity.ATTR_SITE_NAME));

			return result = dao.find(filter);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	/**
	 * Fetch a list of site entries mapped to the give carrierId.
	 * <b>Please note that the returned result set will contain non-active elements as well!</b>
	 * @param carrierId
	 * @return - the mapped sites for the given carrier 
	 */
	public List fetchSiteForCarrier(long carrierId) {
		// [[
		List result = null;
		Object[][] params = {{"carrierId", carrierId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSiteForCarrier", params);
		}
		// ]]
		try {
			return fetchSiteForCarrier(carrierId, false);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public ExtSiteEntity saveSite(ExtSiteEntity extSite) {
		// [[
		ExtSiteEntity result1 = null;
		Object[][] params = {{"extSite", extSite}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveSite", params);
		}
		// ]]
		try {
			ReadWriteDao<ExtSiteEntity> dao = getDaoProvider().getExtSiteEntityDao();
			
			if (extSite.getExtSiteId() == null) {
				extSite = dao.persist(extSite);
			} else {
				extSite = dao.merge(extSite);
			}
			
			return result1 = extSite;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}
	
	public ExtSiteEntity fetchExternalSite(Long extSiteId) {
		// [[
		ExtSiteEntity result = null;
		Object[][] params = {{"extSiteId", extSiteId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchExternalSite", params);
		}
		// ]]
		try {
			ReadWriteDao<ExtSiteEntity> dao = getDaoProvider().getExtSiteEntityDao();
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(ExtSiteEntity.ATTR_EXT_SITE_ID, extSiteId));
			
			return result = dao.findSingle(filter);
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




