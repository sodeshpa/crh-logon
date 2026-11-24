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

import com.crh.logon.business.services.dispatchers.ExternalMaterialServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.ExternalMaterialServiceLocalInterface;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ExtMaterialEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class ExternalMaterialServiceBean extends ExternalMaterialServiceDispatcher
	implements ExternalMaterialServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ExternalMaterialServiceBean.class);

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
	 * Fetch a list of material entries mapped to the give carrierId.
	 * @param carrierId
	 * @param excludeInactive - if true the result list will <b>not</b> contain the non-active elements
	 * (they will be excluded) ! 
	 * @return - the mapped materials for the given carrier 
	 */
	public List fetchMaterialsForCarrier(long carrierId, boolean excludeInactive) {
		// [[
		Object[][] params = {{"carrierId", carrierId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchMaterialsForCarrier", params);
		}
		// ]]
		try {
			ReadWriteDao<ExtMaterialEntity> dao = getDaoProvider().getExtMaterialEntityDao();
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(ExtMaterialEntity.ATTR_CARRIER_ID, carrierId));
			if (excludeInactive) {
				filter.addExpression(Restrictions.eq(ExtMaterialEntity.ATTR_ACTIVE_FLAG, true));
			}
			filter.addExpression(Order.asc(ExtMaterialEntity.ATTR_MATERIAL_NAME));
			
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
	 * Fetch a list of material entries mapped to the give carrierId.
	 * <b>Please note that the returned result set will contain non-active elements as well!</b>
	 * @param carrierId
	 * @return - the mapped materials for the given carrier 
	 */
	public List fetchMaterialsForCarrier(long carrierId) {
		// [[
		Object[][] params = {{"carrierId", carrierId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchMaterialsForCarrier", params);
		}
		// ]]
		try {
			return fetchMaterialsForCarrier(carrierId, false);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public ExtMaterialEntity saveMaterial(ExtMaterialEntity extMat){
		// [[
		ExtMaterialEntity result1 = null;
		Object[][] params = {{"extMat", extMat}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveMaterial", params);
		}
		// ]]
		try {
			ReadWriteDao<ExtMaterialEntity> dao = getDaoProvider().getExtMaterialEntityDao();
			
			if (extMat.getExtMaterialId() == null) {			 //add new material
				extMat = dao.persist(extMat);
			} else {
				extMat = dao.merge(extMat);						//update material
			}
			
			return result1 = extMat;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}

	
	public boolean isNameExist(String materialName, Long carrierId){
		
		ReadWriteDao<ExtMaterialEntity> dao = getDaoProvider().getExtMaterialEntityDao();
		Filter filter = new FilterImpl();
		filter.addExpression(Restrictions.eq(ExtMaterialEntity.ATTR_MATERIAL_NAME, materialName));
		filter.addExpression(Restrictions.eq(ExtMaterialEntity.ATTR_CARRIER_ID, carrierId));
		List nameList = dao.find(filter);
		if ( nameList.isEmpty() ){
			return false;
		}
		return true;
	}
	
	public Long getMaterialIdByName(String materialName, Long carrierId){
		
		ReadWriteDao<ExtMaterialEntity> dao = getDaoProvider().getExtMaterialEntityDao();
		Filter filter = new FilterImpl();
		filter.addExpression(Restrictions.eq(ExtMaterialEntity.ATTR_MATERIAL_NAME, materialName));
		filter.addExpression(Restrictions.eq(ExtMaterialEntity.ATTR_CARRIER_ID, carrierId));
		ExtMaterialEntity material = dao.findSingle(filter);
	
		return material.getExtMaterialId();
	}	
	
	public ExtMaterialEntity fetchExternalMaterial(long materialId) {
		// [[
		ExtMaterialEntity result = null;
		Object[][] params = {{"materialId", materialId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchExternalMaterial", params);
		}
		// ]]
		try {
			ReadWriteDao<ExtMaterialEntity> dao = getDaoProvider().getExtMaterialEntityDao();
				
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(ExtMaterialEntity.ATTR_EXT_MATERIAL_ID, materialId));
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
