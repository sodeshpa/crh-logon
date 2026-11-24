/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.SiloAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.dispatchers.SiloAdminServiceDispatcher;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.RfidtagEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.views.ShiptoSapBpEntity;

/**
 * 
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class SiloAdminServiceBean extends SiloAdminServiceDispatcher implements SiloAdminServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SiloAdminServiceBean.class);
	
	@EJB private RFIDServiceLocalInterface rfIdService;

	
	/**
	 * @param siloId the id of the silo which is going to be returned
	 * @adnlog.exclude return
	 */
	public ShiptoSiloEntity fetchSilo(long siloId) {
		// [[
		Object[][] params = {{"siloId", siloId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSilo", params);
		}
		// ]]
		try {
			ReadWriteDao<ShiptoSiloEntity> dao = getDaoProvider().getShiptoSiloEntityDao();
			ShiptoSiloEntity silo = dao.findById(siloId);
			if (silo != null && silo.getRfidobject() != null ) {
				silo.getRfidobject().initialize();
			}
			return silo;
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
	 * Loads all silos from TLGNC_SHIPTO_SILO, which are portable.
	 * @adnlog.exclude return
	 */
	public List<ShiptoSiloEntity> fetchSilos() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSilos");
		}
		// ]]
		try {
			ReadWriteDao<ShiptoSiloEntity> dao = getDaoProvider().getShiptoSiloEntityDao();
			Filter<ShiptoSiloEntity> filt = new FilterImpl<ShiptoSiloEntity>();
			filt.addExpression(Restrictions.eq(ShiptoSiloEntity.ATTR_PORTABLE_SILO_FLAG, true));
			List<ShiptoSiloEntity> result = dao.find(filt);
			return result;
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
	 * Updates an existing silo, saves rfids, and pool associations.
	 */
	public ShiptoSiloEntity updateSilo(ShiptoSiloEntity silo) {
		// [[
		ShiptoSiloEntity result = null;
		Object[][] params = {{"silo", silo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateSilo", params);
		}
		// ]]
		try {
			
			RfidobjectEntity rfidobject = silo.getRfidobject();
			long rfidobjectTypeSilo = Constant.RFIDOBJECT_TYPE_SHIPTO_SILO;
			
			RFIDBean.AbstractRFIDBean bean = RFIDBean.ShiptoSiloRFIDBean(silo);
			rfidobject = rfIdService.mergeRfIdObject(bean, rfidobjectTypeSilo);
			silo.setRfidobject(rfidobject);
			ReadWriteDao<ShiptoSiloEntity> dao = getDaoProvider().getShiptoSiloEntityDao();
			silo = dao.merge(silo);
			
			return result = silo;
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
	 * Creates a new silo, and saves its fields. 
	 */
	public ShiptoSiloEntity createSilo(ShiptoSiloEntity silo) {
		// [[
		ShiptoSiloEntity result = null;
		Object[][] params = {{"silo", silo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createSilo", params);
		}
		// ]]
		try {
			RfidobjectEntity rfObj = silo.getRfidobject();
			RFIDBean.AbstractRFIDBean bean = RFIDBean.ShiptoSiloRFIDBean(silo);
			rfObj = rfIdService.validateAndPersistRFIDObject(Constant.RFIDOBJECT_TYPE_SHIPTO_SILO, bean);
			silo.setRfidobject(rfObj);
			
			ReadWriteDao<ShiptoSiloEntity> dao = getDaoProvider().getShiptoSiloEntityDao();
			silo = dao.persist(silo);
			
			return result = silo;
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
	 * Loads a shipTo with the given id.
	 * @param id
	 * @return
	 */
	public ShiptoEntity fetchShipto(Long id) {
		// [[
		ShiptoEntity result1 = null;
		Object[][] params = {{"id", id}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShipto", params);
		}
		// ]]
		try {
			ReadWriteDao<ShiptoEntity> dao = getDaoProvider().getShiptoEntityDao();
			ShiptoEntity se = dao.findById(id);
			return result1 = se;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}
	
	
	/***
	 * Fetches the shiptos according to the query parameters
	 * @param start
	 * @param count
	 * @param orderBy
	 * @param query
	 * @return
	 */
	public List<ShiptoSapBpEntity> fetchShipto(int start, int count, String orderBy, String query) {
		// [[
		List<ShiptoSapBpEntity> result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShipto", params);
		}
		// ]]
		try {
			ReadOnlyDao<ShiptoSapBpEntity> dao = getDaoProvider().getShiptoSapBpEntityDao();
			List<ShiptoSapBpEntity> list = new ArrayList<ShiptoSapBpEntity>();
			
			Filter<ShiptoSapBpEntity> filter = new FilterImpl<ShiptoSapBpEntity>();
			filter.addExpression(Restrictions.or(Restrictions.ilike(ShiptoSapBpEntity.ATTR_NAME1, "%"+ query + "%"),
					Restrictions.ilike(ShiptoSapBpEntity.ATTR_CITY, "%" + query + "%"),
					Restrictions.ilike(ShiptoSapBpEntity.ATTR_POSTAL_CODE, "%" + query + "%"),
					Restrictions.ilike(ShiptoSapBpEntity.ATTR_SAP_ID, "%" + query + "%"))
			);
			list = dao.find(filter);
			return result = list;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public Long getDummySiloIdentifier(List<ShiptoSiloEntity> existingSilos) {
		if (existingSilos == null) {
			return new Long(-1);
		}
		long lowestIdentifier = 0;
		for (ShiptoSiloEntity silo : existingSilos) {
			long currentSiloIdentifier = silo.getShiptoSiloId();
			if (currentSiloIdentifier > Constant.SILO_TEST_DATA_UPPER_LIMIT
					&& currentSiloIdentifier < lowestIdentifier) {
				lowestIdentifier = currentSiloIdentifier;
			}
		}
		return new Long(lowestIdentifier - 1);
	}

	public void detachRFIDTagsFromRfidObject(RfidobjectEntity rfidObject) {
		if (rfidObject != null) {
			List<RfidtagEntity> rfids = rfidObject.getRfids();
			if (rfids != null) {
				for (RfidtagEntity rfidTag : rfids) {
					rfidTag.setRfidobject(null);
				}
			}
		}
	}

	public void detachSiloFromRestrictions(ShiptoSiloEntity selectedSilo,
			List<ShiptoSareaRestrEntity> restrictions) {
		if (restrictions == null) {
			return;
		}
		Long selectedSiloIdentifier = selectedSilo.getShiptoSiloId();
	
		for (ShiptoSareaRestrEntity currentRestriction : restrictions) {
			ShiptoSiloEntity currentSilo = currentRestriction.getShiptoSilo();
			if (currentSilo == null) {
				continue;
			}
			Long currentSiloIdentifier = currentSilo.getShiptoSiloId();
			if (currentSiloIdentifier.equals(selectedSiloIdentifier)) {
				currentRestriction.setShiptoSilo(null);
			}
		}
	}

	public void updateSelectedSilo(List<ShiptoSiloEntity> shiptosSilos,
			ShiptoSiloEntity selectedSilo) {
		Long selectedSiloIdentifier = selectedSilo.getShiptoSiloId();
		ListIterator<ShiptoSiloEntity> siloIterator = shiptosSilos.listIterator();
		while (siloIterator.hasNext()) {
			ShiptoSiloEntity currentSilo = siloIterator.next();
			Long currentSiloIdentifier = currentSilo.getShiptoSiloId();
			if (currentSiloIdentifier.equals(selectedSiloIdentifier)) {
				siloIterator.set(selectedSilo);
				break;
			}
		}
	}
	
}
