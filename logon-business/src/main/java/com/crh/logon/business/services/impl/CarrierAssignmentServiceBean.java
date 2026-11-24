/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.List;

import javax.ejb.Stateless;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.CarrierAssignmentServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.CarrierAssignmentServiceLocalInterface;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.views.PoolCarrierCarrierEntity;
import com.crh.logon.persistence.entities.views.PoolShiptoMatSoldtoEntity;
/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class CarrierAssignmentServiceBean extends CarrierAssignmentServiceDispatcher
	implements CarrierAssignmentServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CarrierAssignmentServiceBean.class);

	/**
	 * fetch the PoolEntities (for the current user - this is handled in the DB)
	 * 
	 * @return list of PoolEntities
	 * @adnlog.exclude return
	 */
	public List fetchPoolEntityList() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolEntityList");
		}
		// ]]
		try {
			final ReadOnlyDao<PoolEntity> dao = getDaoProvider().getPoolEntityDao();			
			return dao.find((Filter<PoolEntity>) null);
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
	 * 
	 * 
	 * @param list of poolIds 
	 * @param language
	 * @return list of PoolShiptoMatSoldtoEntity
	 * @adnlog.exclude return
	 */
	public List fetchPoolShiptoMatSoldtoList(List poolList, String lang) {
		// [[
		Object[][] params = {{"poolList", poolList},
							{"lang", lang}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolShiptoMatSoldtoList", params);
		}
		// ]]
		try {
			return fetchPoolShiptoMatSoldtoList(poolList, null, null, null, lang);
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
	 * fetch a list of PoolShiptoMatSoldtoEntity filtered by pool, soldto, shipto, product and language
	 * 
	 * @param list of poolIds 
	 * @param soldToIdFilter
	 * @param shipToNrFilter 
	 * @param productNrFilter
	 * @param language
	 * @return list of UserPoolAccEntities
	 * @adnlog.exclude return
	 */
	public List fetchPoolShiptoMatSoldtoList(List poolList, Long soldToIdFilter, String shipToNrFilter, String productNrFilter, String lang) {
		// [[
		List<PoolShiptoMatSoldtoEntity> result = null;
		Object[][] params = {{"poolList", poolList},
							{"soldToIdFilter", soldToIdFilter},
							{"shipToNrFilter", shipToNrFilter},
							{"productNrFilter", productNrFilter},
							{"lang", lang}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolShiptoMatSoldtoList", params);
		}
		// ]]
		try {
			final ReadOnlyDao<PoolShiptoMatSoldtoEntity> dao = getDaoProvider().getPoolShiptoMatSoldtoEntityDao();
			final Filter<PoolShiptoMatSoldtoEntity> filter = dao.createFilter();
			
			if ((poolList != null) && (!poolList.isEmpty())) {
				filter.addExpression(Restrictions.in(PoolShiptoMatSoldtoEntity.ATTR_POOL_ID, poolList));
			}
			filter.addExpression(Restrictions.eq(PoolShiptoMatSoldtoEntity.ATTR_MATERIAL_DESC_LANG_CD, lang));
			if ( soldToIdFilter != null ) {
				filter.addExpression(Restrictions.eq(PoolShiptoMatSoldtoEntity.ATTR_SOLDTO_ID, soldToIdFilter));
			}
			if ( shipToNrFilter != null ) {
				filter.addExpression(Restrictions.eq(PoolShiptoMatSoldtoEntity.ATTR_SHIPTO_NR, shipToNrFilter));
			}
			if ( productNrFilter != null ) {
				filter.addExpression(Restrictions.eq(PoolShiptoMatSoldtoEntity.ATTR_MATERIAL_NR, productNrFilter));
			}
			filter.addExpression(Restrictions.isNotNull(PoolShiptoMatSoldtoEntity.ATTR_SOLDTO_ID));
			filter.addExpression(Restrictions.isNotNull(PoolShiptoMatSoldtoEntity.ATTR_POOL_ID));
			filter.addExpression(Restrictions.isNotNull(PoolShiptoMatSoldtoEntity.ATTR_SHIPTO_NR));
			filter.addExpression(Order.asc(PoolShiptoMatSoldtoEntity.ATTR_POOL_NAME));
			filter.addExpression(Order.asc(PoolShiptoMatSoldtoEntity.ATTR_SOLDTO_NAME));
			filter.addExpression(Order.asc(PoolShiptoMatSoldtoEntity.ATTR_SHIPTO_NAME));
			filter.addExpression(Order.asc(PoolShiptoMatSoldtoEntity.ATTR_MATERIAL_DESCRIPTION));
			
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
	 * Fetch the PoolCarrierCarrierEntities which belong to a list of PoolIds
	 *  
	 * @param list of poolIds 
	 * @return list of PoolCarrierCarrierEntities
	 */
    public List fetchCarriersForPoolIdList(List poolIdList) {
			// [[
			List<PoolCarrierCarrierEntity> result = null;
			Object[][] params = {{"poolIdList", poolIdList}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "fetchCarriersForPoolIdList", params);
			}
			// ]]
			try {
				final ReadOnlyDao<PoolCarrierCarrierEntity> dao = getDaoProvider().getPoolCarrierCarrierEntityDao();
				final Filter<PoolCarrierCarrierEntity> filter = dao.createFilter();
				if ((poolIdList != null) && (!poolIdList.isEmpty())) {
					filter.addExpression(Restrictions.in(PoolCarrierCarrierEntity.ATTR_POOL_ID, poolIdList));
					filter.addExpression(Order.asc(PoolCarrierCarrierEntity.ATTR_CARRIER_NAME));
				}
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
	 * Fetch a ShiptoSareaRestrEntity by id 
	 *  
	 * @param ShiptoSareaRestrId  
	 * @return ShiptoSareaRestrEntity
	 */
    public ShiptoSareaRestrEntity fetchRestricitonById( Long id) {
		// [[
		ShiptoSareaRestrEntity result = null;
		Object[][] params = {{"id", id}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchRestricitonById", params);
		}
		// ]]
		try {
			final ReadOnlyDao<ShiptoSareaRestrEntity> dao = getDaoProvider().getShiptoSareaRestrEntityDao();
			final Filter<ShiptoSareaRestrEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(ShiptoSareaRestrEntity.ATTR_SHIPTO_SAREA_RESTR_ID, id));
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
    
    /**
	 * Merge a ShiptoSareaRestrEntity 
	 *  
	 * @param ShiptoSareaRestrId  
	 * @return ShiptoSareaRestrEntity
	 */
    public ShiptoSareaRestrEntity mergeRestriciton( ShiptoSareaRestrEntity restrEntity) {
		// [[
		ShiptoSareaRestrEntity result = null;
		Object[][] params = {{"restrEntity", restrEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "mergeRestriciton", params);
		}
		// ]]
		try {
			final ReadWriteDao<ShiptoSareaRestrEntity> dao = getDaoProvider().getShiptoSareaRestrEntityDao();
			return result = dao.merge(restrEntity);
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
