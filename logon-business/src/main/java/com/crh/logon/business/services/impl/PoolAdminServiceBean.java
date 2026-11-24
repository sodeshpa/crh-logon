/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.SelectableGeneralDisplayNameDto;
import com.crh.logon.business.services.dispatchers.PoolAdminServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.PoolAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.PoolCarrierRelationWrapper;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.PoolCarrierEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.views.PoolCarrierCarrierEntity;
import com.crh.logon.persistence.entities.views.PoolCommCarrierEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.LogonException;
 
/**
 * This service is taking care of calls mostly to pool related tables.
 * 
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class PoolAdminServiceBean extends PoolAdminServiceDispatcher implements PoolAdminServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PoolAdminServiceBean.class);
	
	@EJB private DispoServiceLocalInterface dispoService;
	
	/**
	 * Returns a poolTxEntity object with the given poolId. Its name attribut contains the
	 * name of the pool.  WARNING: DISABLES ACCESS RESTRICTIONS!
	 * @param poolId
	 * @param locale
	 * @return
	 */
	public PoolTxEntity fetchPoolTxEntityDisableAccessRestriction(Long poolId, Locale locale) {
		// [[
		PoolTxEntity result = null;
		Object[][] params = {{"poolId", poolId},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolTxEntityDisableAccessRestriction", params);
		}
		// ]]
		try {
			Authorizer.disableNextAccessRestriction();
			
			final ReadOnlyDao<PoolTxEntity> dao = getDaoProvider().getPoolTxEntityDao();
			final Filter<PoolTxEntity> filter = dao.createFilter();

			filter.addExpression(Restrictions.eq("poolTxPK.poolId", poolId));
			filter.addExpression(Restrictions.eq("poolTxPK.langCd", locale.getLanguage()));
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
	 * Returns a poolTxEntity object with the given poolId. Its name attribute contains the
	 * name of the pool. 
	 * @param poolId
	 * @param locale
	 * @return
	 */
	public PoolTxEntity fetchPoolTxEntity(Long poolId, Locale locale) {
		// [[
		PoolTxEntity result = null;
		Object[][] params = {{"poolId", poolId},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolTxEntity", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<PoolTxEntity> dao = getDaoProvider().getPoolTxEntityDao();
			final Filter<PoolTxEntity> filter = dao.createFilter();

			filter.addExpression(Restrictions.eq("poolTxPK.poolId", poolId));
			filter.addExpression(Restrictions.eq("poolTxPK.langCd", locale.getLanguage()));			
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
	 * Returns a list of PoolTxEntities, with all the available pools. Names are 
	 * retrieved according to locale. 
	 * @param locale
	 * @return
	 */
	public List<PoolTxEntity>  fetchPoolTxEntities(Locale locale) {
		// [[
		List<PoolTxEntity>  result = null;
		Object[][] params = {{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolTxEntities", params);
		}
		// ]]
		try {
			return result = fetchPoolTxEntitiesByDeliveryType(0, 0, locale, null);
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
     * Returns a list of PoolTxEntities, with all the available pools. Names are 
     * retrieved according to locale. 
     * @param locale
     * @return
     */
    public List<PoolTxEntity>  fetchPoolTxEntities(int skip, int num, Locale locale) {
		// [[
		List<PoolTxEntity>  result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolTxEntities", params);
		}
		// ]]
		try {
            return result = fetchPoolTxEntitiesByDeliveryType(skip, num, locale, null);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    public List<PoolTxEntity> fetchPoolTxEntitiesByDeliveryType(Locale locale, Long deliveryTypeCd) throws LogonException {
    	// [[
    	List<PoolTxEntity> result = null;
    	Object[][] params = {{"locale", locale},
    			{"deliveryTypeCd", deliveryTypeCd}};
    	boolean logDebugEnabled = LOG.isDebugEnabled();
    	if (logDebugEnabled) {
    		LOG.entering(this, "fetchPoolTxEntitiesByDeliveryType", params);
    	}
    	// ]]
    	try {
    		return result = fetchPoolTxEntitiesByDeliveryType(0, 0, locale, deliveryTypeCd);
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
     * Returns a list of PoolTxEntities, with all the available pools. Names are 
     * retrieved according to locale. 
     * @param locale
     * @return
     */
    public Long countPoolTxEntities(Locale locale) {
		// [[
		Long result = null;
		Object[][] params = {{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "countPoolTxEntities", params);
		}
		// ]]
		try {
            return result = countPoolTxEntitiesByDeliveryType(locale, null, false);
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
	 * Returns a list of PoolTxEntities, with all the available pools. Names are 
	 * retrieved according to locale. 
	 * 
	 * This function retrieves *all* pools, without restricting the pools
	 * to which the user actually has access too. (the full list is necessary
	 * in a few use cases, e.g. 'Order' and 'Dispokarte')
	 * 
	 * @param locale
	 * @return
	 */
	public List<PoolTxEntity> fetchAllPoolTxEntities(Locale locale) {
		// [[
		List<PoolTxEntity> result = null;
		Object[][] params = {{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAllPoolTxEntities", params);
		}
		// ]]
		try {
				final ReadOnlyDao<PoolTxEntity> txDao = getDaoProvider().getPoolTxEntityDao();
				final Filter<PoolTxEntity> txFilter = txDao.createFilter();

				txFilter.addExpression(Restrictions.jpaqlExpression("x.poolTxPK.poolId = any(select p.poolId from PoolEntity as p where p.ctlActCd = 1)", new HashMap()));
				txFilter.addExpression(Restrictions.eq("poolTxPK.langCd", locale.getLanguage()));
				txFilter.addExpression(Order.asc(PoolTxEntity.ATTR_NAME));
				// next dao call will retrieve unrestricted data
				Authorizer.disableNextAccessRestriction();
				return result = txDao.find(txFilter);
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
	 * Returns a list of PoolTxEntities, with all the available pools for the specified set of poolIds. Names are 
	 * retrieved according to locale. 
	 * 
	 * This function retrieves the pools specified without restricting the pools
	 * to which the user actually has access too. (the full list is necessary
	 * in a few use cases, e.g. 'Order' and 'Dispokarte')
	 * 
	 * @param locale
	 * @return
	 */
	@AdnTrace
	public Map<Long, PoolTxEntity> fetchAllPoolTxEntities(Locale locale, Set<Long> poolIds) {
	
		StringBuilder queryString = new StringBuilder("select x from PoolTxEntity x where poolTxPK.langCd = :language and poolTxPK.poolId in (");

		for (int j = 0;  j < poolIds.size();j++){
			queryString.append(":poolId_"+ j); 
			if(j < poolIds.size()-1){
				queryString.append(", ");
			}
		}
		
		queryString.append(") order by x.name asc");
		
		Query jpaQuery = em.createQuery(queryString.toString());
		jpaQuery.setParameter("language", locale.getLanguage());
		
		int j = 0;
		for (Long poolId: poolIds){
			jpaQuery.setParameter("poolId_" + j, poolId);
			j++;
		}

		List<PoolTxEntity> result = jpaQuery.getResultList();
		Map<Long, PoolTxEntity> poolTxMap = new HashMap<Long, PoolTxEntity>();
		for (PoolTxEntity entity : result) {
			poolTxMap.put(entity.getPoolTxPK().getPoolId(), entity);
		}
		
		return poolTxMap;
	}
		
	
	public List<PoolTxEntity> fetchPoolTxEntitiesByDeliveryTypeNotAuthorized(Locale locale, Long deliveryTypeCd) throws LogonException {
		// [[
		List<PoolTxEntity> result = null;
		Object[][] params = {{"locale", locale},
							{"deliveryTypeCd", deliveryTypeCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolTxEntitiesByDeliveryTypeNotAuthorized", params);
		}
		// ]]
		try {
			return result = fetchPoolTxEntitiesNotAuthorized(0, 0, locale, deliveryTypeCd);
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
	 * Returns all pools for the given transportation mode.
	 * @param locale The user's locale, used to select the language of the pool descriptions.
	 * @param transportationMode Only pools that have this SAP transportation mode are returned.
	 * @param disableAuthorization If this is <code>true</code>, then the pools are not filtered according to the
	 * user's authorization, but all pools are returned instead.
	 * @throws LogonException
	 */
	public List<PoolTxEntity> fetchPoolTxEntitiesByTransportationMode(Locale locale,String transportationMode, boolean disableAuthorization) throws LogonException 
	{
		// [[
		List<PoolTxEntity> result = null;
		Object[][] params = {{"locale", locale},
							{"transportationMode", transportationMode},
							{"disableAuthorization", disableAuthorization + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolTxEntitiesByTransportationMode", params);
		}
		// ]]
		try {
			final List<String> modes = new LinkedList<String>();
			modes.add(transportationMode);
			
			return result = fetchPoolTxEntitiesByTransportationModes(locale, modes, disableAuthorization);
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
	 * Returns all pools for simple vehicles. This method returns all pools whose SAP transportation
	 * type is one of "ship" or "block train".
	 * @param locale The user's locale, used to select the language of the pool descriptions.
	 * @param disableAuthorization If this is <code>true</code>, then the pools are not filtered according to the
	 * user's authorization, but all pools are returned instead.
	 * @throws LogonException
	 */
	public List<PoolTxEntity> fetchSimpleVehiclePoolTxEntities(Locale locale, boolean disableAuthorization)
	throws LogonException
	{
		// [[
		List<PoolTxEntity> result = null;
		Object[][] params = {{"locale", locale},
							{"disableAuthorization", disableAuthorization + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSimpleVehiclePoolTxEntities", params);
		}
		// ]]
		try {
			final List<String> simpleVehicleModes = new ArrayList<String>();
			simpleVehicleModes.add(Constant.TRANSPORTATION_MODE_SHIP);
			simpleVehicleModes.add(Constant.TRANSPORTATION_MODE_BLOCK_TRAIN);
			
			return result = fetchPoolTxEntitiesByTransportationModes(locale, simpleVehicleModes, disableAuthorization);
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
	 * Returns all pools for the given transportation mode.
	 * @param locale The user's locale, used to select the language of the pool descriptions.
	 * @param transportationModes Only return pools whose transport mode is in this list
	 * @param disableAuthorization If this is <code>true</code>, then the pools are not filtered according to the
	 * user's authorization, but all pools are returned instead.
	 * @throws LogonException
	 */
	public List<PoolTxEntity> fetchPoolTxEntitiesByTransportationModes(Locale locale,List<String> transportationModes, boolean disableAuthorization) throws LogonException {
		// [[
		List<PoolTxEntity> result = null;
		Object[][] params = {{"locale", locale},
							{"transportationMode", transportationModes},
							{"all", disableAuthorization + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolTxEntitiesByTransportationMode", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<PoolEntity> poolEntityDao = getDaoProvider().getPoolEntityDao();
			final Filter<PoolEntity> poolEntityFilter = poolEntityDao.createFilter();
			
	        poolEntityFilter.addExpression(Restrictions.eq(PoolEntity.ATTR_CTL_ACT_CD, Boolean.TRUE));
            poolEntityFilter.addExpression(Restrictions.in(PoolEntity.ATTR_SAP_TRSP_MODE_CD, transportationModes));

	        final List<Long> poolIdList = new ArrayList<Long>();
	        try {
		        if (disableAuthorization) {
		            // next dao call will retrieve unrestricted data
		            Authorizer.disableNextAccessRestriction();
		        }
	            final List<PoolEntity> findNRecs = poolEntityDao.findNRecs(0, 0, poolEntityFilter);
	            for (PoolEntity pe : findNRecs) {
	            	poolIdList.add(pe.getPoolId());
	            }
	        }
	        catch (Exception e) { // This Will never occur in practice, since the PoolEntity.ATTR_POOL_ID is a constant
	            throw new LogonException(e);
	        }

	        final ReadOnlyDao<PoolTxEntity> txDao = getDaoProvider().getPoolTxEntityDao();
	        final Filter<PoolTxEntity> txFilter = txDao.createFilter();
	        
	        txFilter.addExpression(Restrictions.in("poolTxPK.poolId", poolIdList));
	        txFilter.addExpression(Restrictions.eq("poolTxPK.langCd", locale.getLanguage()));
	        txFilter.addExpression(Order.asc(PoolTxEntity.ATTR_NAME));
	        if (disableAuthorization) {
	            // next dao call will retrieve unrestricted data
	            Authorizer.disableNextAccessRestriction();
	        }
	        return result = txDao.find(txFilter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private List<PoolTxEntity> fetchPoolTxEntitiesNotAuthorized(int skip, int num, Locale locale, Long deliveryTypeCd) throws LogonException {
		// [[
		List<PoolTxEntity> result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"locale", locale},
							{"deliveryTypeCd", deliveryTypeCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolTxEntitiesNotAuthorized", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<PoolTxEntity> txDao = getDaoProvider().getPoolTxEntityDao();
			final Filter<PoolTxEntity> txFilter = txDao.createFilter();
			
			final String jpaqlExpression = "x.poolTxPK.poolId = any(select p.poolId from PoolEntity as p where p.ctlActCd = 1 and p.sapTrspMode.sapTrspModeCd = :sapTrspModeCd)";
			
			final String sapTrspModeCd = Constant.DEL_TYPE_TRSP_MODE_MAP.get(deliveryTypeCd);
			final Map<String, Object> paramValues = new HashMap<String, Object>(); 
			paramValues.put("sapTrspModeCd", sapTrspModeCd);

			txFilter.addExpression(Restrictions.jpaqlExpression(jpaqlExpression, paramValues));
			txFilter.addExpression(Restrictions.eq("poolTxPK.langCd", locale.getLanguage()));
			txFilter.addExpression(Order.asc(PoolTxEntity.ATTR_NAME));
			// next dao call will retrieve unrestricted data
			Authorizer.disableNextAccessRestriction();
			return result = txDao.findNRecs(skip, num, txFilter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private List<PoolTxEntity> fetchPoolTxEntitiesByDeliveryType(int skip, int num, Locale locale, Long deliveryTypeCd) throws LogonException {
		// [[
		List<PoolTxEntity> result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"locale", locale},
							{"deliveryTypeCd", deliveryTypeCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolTxEntitiesByDeliveryType", params);
		}
		// ]]
		try {
			final ReadOnlyDao<PoolEntity> dao = getDaoProvider().getPoolEntityDao();
			final Filter<PoolEntity> filter = dao.createFilter();
			
	        filter.addExpression(Restrictions.eq(PoolEntity.ATTR_CTL_ACT_CD, Boolean.TRUE));
	        if (deliveryTypeCd != null) {
	            filter.addExpression(Restrictions.eq(PoolEntity.ATTR_SAP_TRSP_MODE_CD, Constant.DEL_TYPE_TRSP_MODE_MAP.get(deliveryTypeCd)));
	        }
			
	        final List<Long> poolIdList = new ArrayList<Long>();
	        try {
	            final List<PoolEntity> findNRecs = dao.findNRecs(skip, num, filter);
	            for (PoolEntity pe : findNRecs) {
	            	poolIdList.add(pe.getPoolId());
	            }
	        }
	        catch (Exception e) { // This Will never occur in practice, since the PoolEntity.ATTR_POOL_ID is a constant
	            throw new LogonException(e);
	        }
	        
			if (poolIdList.isEmpty()) {
				/* LOGON-6155: "Restrictions.in" throws an IllegalArgumentException if the
				 * poolIdList is empty, so we just return an empty list in that case.
				 */
				return Collections.<PoolTxEntity>emptyList();
			}
			else {
				final ReadOnlyDao<PoolTxEntity> txDao = getDaoProvider().getPoolTxEntityDao();
		        final Filter<PoolTxEntity> txFilter = txDao.createFilter();
		        
		        txFilter.addExpression(Restrictions.in("poolTxPK.poolId", poolIdList));
		        txFilter.addExpression(Restrictions.eq("poolTxPK.langCd", locale.getLanguage()));
		        txFilter.addExpression(Order.asc(PoolTxEntity.ATTR_NAME));
		        
		        return result = txDao.find(txFilter);
	        }
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

    private Long countPoolTxEntitiesByDeliveryType(Locale locale, Long deliveryTypeCd, boolean all) throws LogonException {
		// [[
		Long result = null;
		Object[][] params = {{"locale", locale},
							{"deliveryTypeCd", deliveryTypeCd},
							{"all", all + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "countPoolTxEntitiesByDeliveryType", params);
		}
		// ]]
		try {
            
			final ReadOnlyDao<PoolEntity> dao = getDaoProvider().getPoolEntityDao();
			final Filter<PoolEntity> filter = dao.createFilter();
            
            filter.addExpression(Restrictions.eq(PoolEntity.ATTR_CTL_ACT_CD, Boolean.TRUE));			
            if (deliveryTypeCd != null) {
                filter.addExpression(Restrictions.eq(PoolEntity.ATTR_SAP_TRSP_MODE_CD, Constant.DEL_TYPE_TRSP_MODE_MAP.get(deliveryTypeCd)));
            }
			
            if (all) {
                // next dao call will retrieve unrestricted data
                Authorizer.disableNextAccessRestriction();
            }
            return result = dao.count(filter);
            
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
	 * @param query
	 * @return
	 */
	public long countSapVendorList(String query) {
		// [[
		long result = 0L;
		Object[][] params = {{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "countSapVendorList", params);
		}
		// ]]
		try {
			final ReadOnlyDao<SapVendorEntity> dao = getDaoProvider().getSapVendorEntityDao();
			final Filter<SapVendorEntity> filter = dao.createFilter();
			
			return result = dao.count(filter);
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
	 * Returns SAP vendors which are not marked invalid in SAP.
	 * @param skip
	 * @param num
	 * @param orderBy
	 * @param query
	 * @return
	 */
	public List<SapVendorEntity> fetchSapVendorList(int skip, int num, String orderBy,
			String query) {
		// [[
		List<SapVendorEntity> result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"orderBy", orderBy},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapVendorList", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<SapVendorEntity> dao = getDaoProvider().getSapVendorEntityDao();
			final Filter <SapVendorEntity>filter = dao.createFilter();	
									
			filter.addExpression(Restrictions.neq(SapVendorEntity.ATTR_MARKED_FOR_DELETION, Constant.MARKED_FOR_DELETION));
			return result = dao.findNRecs(skip, num, filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
		
	public CarrierEntity fetchCarrierBySapVendorId(Long sapVendorId) {
		// [[
		CarrierEntity result = null;
		Object[][] params = {{"sapVendorId", sapVendorId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCarrierBySapVendorId", params);
		}
		// ]]
		try {

			final ReadOnlyDao<CarrierEntity> carrDao = getDaoProvider().getCarrierEntityDao();			
			final Filter<CarrierEntity> carrFilt = carrDao.createFilter();
			
			carrFilt.addExpression(Restrictions.eq(CarrierEntity.ATTR_SAP_VENDOR_ID, sapVendorId));
			carrFilt.addExpression(Restrictions.eq(CarrierEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			
			return result = carrDao.findSingle(carrFilt);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List<CarrierEntity> fetchCarriers() {
		// [[
		List<CarrierEntity> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCarriers");
		}
		// ]]
		try {

			final ReadOnlyDao<CarrierEntity> carrDao = getDaoProvider().getCarrierEntityDao();			
			final Filter<CarrierEntity> carrFilt = carrDao.createFilter();
			
			carrFilt.addExpression(Restrictions.eq(CarrierEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));	
			carrFilt.addExpression(Order.asc(CarrierEntity.ATTR_DISPLAY_NAME));
			return result =  carrDao.find(carrFilt);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List<PoolCarrierEntity> fetchPoolsForCarrier(CarrierEntity carrier) {
		// [[
		List<PoolCarrierEntity> result = null;
		Object[][] params = {{"carrier", carrier}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolsForCarrier", params);
		}
		// ]]
		try {

			final ReadOnlyDao<PoolCarrierEntity> poolCarrDao = getDaoProvider().getPoolCarrierEntityDao();
			final Filter<PoolCarrierEntity> poolCarrFilt = poolCarrDao.createFilter();
			
			poolCarrFilt.addExpression(Restrictions.eq(PoolCarrierEntity.ATTR_CARRIER, carrier));		
						
			return result = poolCarrDao.find(poolCarrFilt);			
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
	public List<PoolCommCarrierEntity> fetchPoolCommCarrierWithSapTrspModeCd() {
		// [[
		List<PoolCommCarrierEntity> result = null;
		final ReadOnlyDao<PoolCommCarrierEntity> poolCarrDao = getDaoProvider().getPoolCommCarrierEntityDao();
		final Filter<PoolCommCarrierEntity> poolCarrFilt = poolCarrDao.createFilter();
					
		return result = poolCarrDao.find(poolCarrFilt);			
	}
	
	public List<PoolCarrierEntity> fetchAllPoolsForCarriers() {
		// [[
		List<PoolCarrierEntity> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAllPoolsForCarriers");
		}
		// ]]
		try {

			final ReadOnlyDao<PoolCarrierEntity> poolCarrDao = getDaoProvider().getPoolCarrierEntityDao();
			final Filter<PoolCarrierEntity> poolCarrFilt = poolCarrDao.createFilter();
			
			return result = poolCarrDao.find(poolCarrFilt);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}	
	
	public List<Map<String, ?>> fetchPoolsAndCarriers(Locale locale) {
		
		final List<PoolTxEntity> pools = fetchPoolTxEntities(locale);
		final List<SapVendorEntity> sapVendors = fetchSapVendorList(0, -1, SapVendorEntity.ATTR_NAME1, null);
		final List<CarrierEntity> carriers = fetchCarriers();
		final List<PoolCarrierEntity> poolsPerCarrier = fetchAllPoolsForCarriers();
		
		final List<Map<String, ?>> resultList = new ArrayList<Map<String, ?>>();
		if ((sapVendors != null) && (!sapVendors.isEmpty())) {
			
			for (SapVendorEntity sapVendorEntity : sapVendors) {
				final Map<String, Object> entryMap = new HashMap<String, Object>();
				
				CarrierEntity carrierEntity = null;
				for (CarrierEntity carrier : carriers) {
					if (carrier.getSapVendor().getSapVendorId().equals(sapVendorEntity.getSapVendorId())) {
						carrierEntity = carrier;
						break;
					}
				}
				if ( carrierEntity == null ) {
					//there is no active carrier for this SAP vendor, do NOT show it!
					continue;
				}
				
				final List<PoolCarrierEntity> poolListForCarrier = new ArrayList<PoolCarrierEntity>();
				if (carrierEntity != null) {
					for (PoolCarrierEntity poolCarrier : poolsPerCarrier) {
						if (poolCarrier.getCarrier().getCarrierId().equals(carrierEntity.getCarrierId())) {
							poolListForCarrier.add(poolCarrier);
						}
					}
				}
				
				entryMap.put("sapVendorEntity", sapVendorEntity);
				entryMap.put("carrierEntity", carrierEntity);
				entryMap.put("pools", pools);
				entryMap.put("poolListForCarrier", poolListForCarrier);
				resultList.add(entryMap);
			}
		}
		
		return resultList;
	}
	
	/**
	 * Based on the given poolIds looks up the related {@code PoolCarrierCarrierEntity}s . 
	 * @param poolds - pools-to-carrier entities which the method should
	 * @return found entities
	 */
	public List<PoolCarrierCarrierEntity> fetchPoolCarrierCarrierEntitysByPoolIds(Set<Long> poolIds, Locale locale) {
		// [[
		List<PoolCarrierCarrierEntity> result = null;
		Object[][] params = {{"poolIds", poolIds},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolCarrierCarrierEntitysByPoolIds", params);
		}
		// ]]
		try {
			
			ReadOnlyDao<PoolCarrierCarrierEntity> poolCarrierCarrierEntityDao = getDaoProvider().getPoolCarrierCarrierEntityDao();
			
			Filter<PoolCarrierCarrierEntity> poolCarrierCarrierFilter = poolCarrierCarrierEntityDao.createFilter();
			poolCarrierCarrierFilter.addExpression(Restrictions.in(PoolCarrierCarrierEntity.ATTR_POOL_ID, poolIds));
			poolCarrierCarrierFilter.addExpression(Restrictions.eq(PoolCarrierCarrierEntity.ATTR_POOL_NAME_LANG_CD, locale.getLanguage()));
			poolCarrierCarrierFilter.addExpression(Order.asc(PoolCarrierCarrierEntity.ATTR_CARRIER_NAME));
			
			List<PoolCarrierCarrierEntity> resultList = poolCarrierCarrierEntityDao.find(poolCarrierCarrierFilter);
			
			return result = resultList;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public void saveCarriersAndAssignPools(Map<Long, CarrierEntity> carriersToSave, Map<Long, Map<Long, ?>> poolIdsToSaveForCarriers) {
		// [[
		Object[][] params = {{"carriersToSave", carriersToSave},
							{"poolIdsToSaveForCarriers", poolIdsToSaveForCarriers}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveCarriersAndAssignPools", params);
		}
		// ]]
		try {
			
			if (carriersToSave == null || carriersToSave.isEmpty()) {
				return;
			}
			
			final DaoProvider daoProvider = getDaoProvider();
			final ReadWriteDao<CarrierEntity> carrierEntityDao = daoProvider.getCarrierEntityDao();
			final ReadWriteDao<PoolCarrierEntity> poolCarrierEntityDao = daoProvider.getPoolCarrierEntityDao();
			final ReadWriteDao<PoolEntity> poolEntityDao = daoProvider.getPoolEntityDao();
			
			for (Map.Entry<Long, CarrierEntity> entry : carriersToSave.entrySet()) {
				
				final Long key = entry.getKey();
				final CarrierEntity carrier = entry.getValue();
				
				final Map<Long, ?> poolIdsForCarrierMap = poolIdsToSaveForCarriers.get(key);
			
				if (carrier.getCarrierId() == null) {
					//persist carrier and related pools
					carrierEntityDao.persist(carrier);
					
					for (Long poolId : poolIdsForCarrierMap.keySet()) {
						
						final PoolEntity assignedPool = poolEntityDao.findById(poolId);						
						PoolCarrierEntity poolCarrierEntityToSave = new PoolCarrierEntity();
						poolCarrierEntityToSave.setPool(assignedPool);
						poolCarrierEntityToSave.setCarrier(carrier);
						
						poolCarrierEntityDao.persist(poolCarrierEntityToSave);
					}
				}
				else {
					//merge carrier and related pools
					
					carrierEntityDao.merge(carrier); // would it be working ?
					
					Filter<PoolCarrierEntity> poolCarrierFilter = poolCarrierEntityDao.createFilter();
					poolCarrierFilter.addExpression(Restrictions.eq(PoolCarrierEntity.ATTR_CARRIER, carrier));
			
					final List<PoolCarrierEntity> storedPoolCarriers = poolCarrierEntityDao.find(poolCarrierFilter);
					
					//deleting what is not in list but stored
					for (PoolCarrierEntity storedPoolCarrier : storedPoolCarriers) {
						
						if (!poolIdsForCarrierMap.containsKey(storedPoolCarrier.getPool().getPoolId())) {
							poolCarrierEntityDao.remove(storedPoolCarrier);
						}
					}
					//adding what is in the list and not stored
					
					for (Long poolId : poolIdsForCarrierMap.keySet()) {
						poolCarrierFilter = poolCarrierEntityDao.createFilter();
						
						poolCarrierFilter.addExpression(Restrictions.eq(PoolCarrierEntity.ATTR_CARRIER, carrier));
						poolCarrierFilter.addExpression(Restrictions.eq(PoolCarrierEntity.ATTR_POOL_ID, poolId));
						
						final PoolCarrierEntity poolCarrierAlreadyStroed = poolCarrierEntityDao.findSingle(poolCarrierFilter);
						
						if (poolCarrierAlreadyStroed == null) {
							final PoolEntity assignedPool = poolEntityDao.findById(poolId);							
							PoolCarrierEntity poolCarrierEntityToSave = new PoolCarrierEntity();
							poolCarrierEntityToSave.setPool(assignedPool);
							poolCarrierEntityToSave.setCarrier(carrier);
							
							poolCarrierEntityDao.persist(poolCarrierEntityToSave);
						}
					}
				}
			}
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
	 * Collects a reusable constellation that describes relationship between pools and carriers respecting
	 * which carrier is in which pool. The returned constellation is based on the {@code SelectableGeneralDisplayNameDto} 
	 * class. It tries to represent a selectable hierarchy between the two domain model. It is possible to define preselected pools.
	 * Good use of this method can be GUIs where there check combobox controls based on a similar architechture.
	 * 
	 * @see #PoolCarrierRelationWrapper
	 * @see #chooseCarriersBasedOnPoolSelection(List, List, List, Map) for updating available carrier list after pool selection changed
	 * @param preSelectedPoolIds - if and only if left null all pools will be preselected!
	 * @param preSelectedCarriers - if and only if left null all carrier will be preselected!
	 * @return composed constellation
	 */
	public PoolCarrierRelationWrapper fetchTruckPoolCarrierConnectionsForLocale(Locale locale, Set<Long> preSelectedPoolIds, Set<Long> preSelectedCarriers) {
		// [[
		PoolCarrierRelationWrapper result = null;
		Object[][] params = {{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchTruckPoolCarrierConnectionsForLocale", params);
		}
		// ]]
		try {
			
			Set<Long> availablePoolIds = new HashSet<Long>();
			
			Set<Long> selectedPoolIds = new HashSet<Long>();
			Set<Long> selectedCarrierIds = new HashSet<Long>();
			
			List<SelectableGeneralDisplayNameDto> pools = new ArrayList<SelectableGeneralDisplayNameDto>();

			List<PoolTxEntity> poolTxEntities = fetchTruckPoolsWhereCarrierExists(locale);
			
			for (PoolTxEntity poolTxEntity : poolTxEntities) {

				String poolName = poolTxEntity.getName();
				Long poolId = poolTxEntity.getPoolTxPK().getPoolId();

				availablePoolIds.add(poolId);

				boolean poolPreSelected = preSelectedPoolIds == null || preSelectedPoolIds.contains(poolId);
				
				SelectableGeneralDisplayNameDto poolWrapper 
					= new SelectableGeneralDisplayNameDto(poolId, poolName, poolPreSelected);
				
				if (poolPreSelected) {
					selectedPoolIds.add(poolId);
				}

				pools.add(poolWrapper);

			}

			List<SelectableGeneralDisplayNameDto> carriers = new ArrayList<SelectableGeneralDisplayNameDto>();

			// This list contains all the pool-carrier n:m mapping results for the available pool carriers
			List<PoolCarrierCarrierEntity> poolCarrierCarrierEntities = 
					fetchPoolCarrierCarrierEntitysByPoolIds(availablePoolIds, locale);

			// Create a map like poolId => List of poolCarriers
			Map<Long, List<SelectableGeneralDisplayNameDto>> poolCarrierMap = new HashMap<Long, List<SelectableGeneralDisplayNameDto>>();

			for (PoolCarrierCarrierEntity poolCarrierCarrierEntity : poolCarrierCarrierEntities) {

				Long poolId = poolCarrierCarrierEntity.getPoolId();
				List<SelectableGeneralDisplayNameDto> list = poolCarrierMap.get(poolId);
				if (list == null) {
					list = new ArrayList<SelectableGeneralDisplayNameDto>();
				}

				// Attributes of the carrierWrapper
				String carrierName = poolCarrierCarrierEntity.getCarrierName();
				Long carrierId = poolCarrierCarrierEntity.getCarrierId();

				// We add it to the list of the poolCarrierMap
				SelectableGeneralDisplayNameDto carrierWrapper = new SelectableGeneralDisplayNameDto(carrierId, carrierName, false);
				list.add(carrierWrapper);
				poolCarrierMap.put(poolId, list);

				// And also to the list of the carrier dropdown if the pool and the carrier is selected on Scheduling Screen; 
				// we cannot (!) reuse the previous wrapper object to avoid selection problems
				boolean carrierPreselected = preSelectedCarriers == null || preSelectedCarriers.contains(carrierId);
				boolean poolPreSelected = preSelectedPoolIds == null || preSelectedPoolIds.contains(poolId);

				SelectableGeneralDisplayNameDto newCarrierEntry = new SelectableGeneralDisplayNameDto(carrierId, carrierName, carrierPreselected);

				if (poolPreSelected && !carriers.contains(newCarrierEntry)) {
					carriers.add(newCarrierEntry);
					if (carrierPreselected) {
						selectedCarrierIds.add(carrierId);
					}
				}

			}

			return result = new PoolCarrierRelationWrapper(pools, carriers, selectedPoolIds, selectedCarrierIds, poolCarrierMap);
			
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
	 * Will return a list of carriers which should be available for selection. New carriers introduced by selecting more pools will be 'selected'.
	 * @see #fetchTruckPoolCarrierConnectionsForLocale(Locale, Set, Set) to see how the pool carrier relation was built on the first place 
	 * @param newPoolSelectionIds
	 * @param carriers
	 * @param oldPoolSelectionIds
	 * @param poolCarrierMap
	 */
	public List<SelectableGeneralDisplayNameDto> chooseCarriersBasedOnPoolSelection(
			List<String> newPoolSelectionIds,
			List<Long> oldPoolSelectionIds,
			List<SelectableGeneralDisplayNameDto> carriers, 
			Map<Long, List<SelectableGeneralDisplayNameDto>> poolCarrierMap) {
		// [[
		List<SelectableGeneralDisplayNameDto> result = null;
		Object[][] params = {{"newPoolSelectionIds", newPoolSelectionIds},
							{"oldPoolSelectionIds", oldPoolSelectionIds},
							{"carriers", carriers},
							{"poolCarrierMap", poolCarrierMap}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "chooseCarriersBasedOnPoolSelection", params);
		}
		// ]]
		try {
			
			List<SelectableGeneralDisplayNameDto> newCarrierList = new ArrayList<SelectableGeneralDisplayNameDto>();
			List<SelectableGeneralDisplayNameDto> carriersToBeRemoved = new ArrayList<SelectableGeneralDisplayNameDto>();
			List<Long> poolsDeselected = new ArrayList<Long>();
			
			// Look for removed pools
			for (Long poolId : oldPoolSelectionIds) {
				if (!newPoolSelectionIds.contains(poolId.toString())) {
					
					List<SelectableGeneralDisplayNameDto> carriersForTheGivenPool = poolCarrierMap.get(poolId);
					
					carriersToBeRemoved.addAll(carriersForTheGivenPool);
					poolsDeselected.add(poolId);
					
				}
			}
			
			// Find carriers which can remain as they are also listed in a different pool
			Iterator<SelectableGeneralDisplayNameDto> iterator = carriersToBeRemoved.iterator();
			while (iterator.hasNext()) {
				SelectableGeneralDisplayNameDto carrierToBeRemoved = iterator.next();
				for (String poolIdStr : newPoolSelectionIds) {
					Long poolId = Long.valueOf(poolIdStr);
					List<SelectableGeneralDisplayNameDto> carriersForTheGivenPool = poolCarrierMap.get(poolId);
					if (carriersForTheGivenPool.contains(carrierToBeRemoved)) {
						iterator.remove();
						break;
					}
				}
			}
			carriers.removeAll(carriersToBeRemoved);
			
			// Look for newly added pools
			for (String poolIdStr : newPoolSelectionIds) {
				Long poolId = Long.valueOf(poolIdStr);
				if (!oldPoolSelectionIds.contains(poolId)) {
					List<SelectableGeneralDisplayNameDto> carriersForTheGivenPool = poolCarrierMap.get(poolId);
					newCarrierList.addAll(carriersForTheGivenPool);
				}
			}
			
			// Check by id based on equals()
			for (SelectableGeneralDisplayNameDto newCarrier : newCarrierList) {
				
				Long newCarrierId = newCarrier.getId();
			
				// If this carrier was not in the list already - note: it could be because a carrier could be belonging to multiple pools 
				SelectableGeneralDisplayNameDto carrier = findCarrierById(carriers, newCarrierId);
				if (carrier == null) {
					
					String carrierName = newCarrier.getName();
					// Create a copy of the carrier wrapper so changes are not affecting the entities in the poolCarrierMap
					carriers.add(new SelectableGeneralDisplayNameDto(newCarrierId, carrierName, true));
					
				}
			
			}

			return result = carriers;

		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private SelectableGeneralDisplayNameDto findCarrierById(List<SelectableGeneralDisplayNameDto> carriers,
			Long carrierId) {
		// [[
		SelectableGeneralDisplayNameDto result = null;
		Object[][] params = {{"carriers", carriers},
							{"carrierId", carrierId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findCarrierById", params);
		}
		// ]]
		try {
			try {
				for (SelectableGeneralDisplayNameDto carrier : carriers) {
					if (carrier.getId().equals(carrierId)) {
						result = carrier;
						break;
					}
				}
			}
			catch (Exception e) {
				LOG.error("Could not find carrier!", e);
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
	
	/**
	 * Returns with a list of {@code PoolTxEntity}s for truck transportation mode where for every pool there are carriers
	 * @param locale - user localse
	 * @return collected list
	 */
	public List<PoolTxEntity> fetchTruckPoolsWhereCarrierExists(Locale locale) {
		// [[
		List<PoolTxEntity> result = null;
		Object[][] params = {{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchTruckPoolsWhereCarrierExists", params);
		}
		// ]]
		try {

			List<PoolTxEntity> pools = fetchPoolTxEntitiesByTransportationMode(locale, Constant.TRANSPORTATION_MODE_TRUCK, false);

			if (pools.isEmpty()) {
				result = Collections.<PoolTxEntity>emptyList();
			} 
			else {

				List<Long> poolIds = new ArrayList<Long>(pools.size());
				for (PoolTxEntity poolTxEntity : pools) {
					Long poolId = poolTxEntity.getPoolTxPK().getPoolId();
					poolIds.add(poolId);
				}

				List<Long> restrictedPoolIds = dispoService.fetchRestrictedPoolIds(poolIds);
				// possible improvement: if both lists are sorted by pool ID, we can do a simultaneous iteration
				Set<Long> restrictedPools = new HashSet<Long>(pools.size()); // Map<poolId, numCarriers
				for (Long restrictedPoolId : restrictedPoolIds) {
					restrictedPools.add(restrictedPoolId);
				}

				for (Iterator<PoolTxEntity> it = pools.iterator(); it.hasNext();) {
					PoolTxEntity poolTxEntity = it.next();
					Long poolId = poolTxEntity.getPoolTxPK().getPoolId();
					if (!restrictedPools.contains(poolId)) {
						// The pool does not have any carriers set or the user does not have right to access any carriers of this pool, so we remove this pool. 
						it.remove();
					}
				}

				result = pools;

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

}
