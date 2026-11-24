/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.SimpleVehicleAdminServiceLocalInterface;
import com.crh.logon.business.services.dispatchers.SimpleVehicleAdminServiceDispatcher;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;

/**
 * 
 * @author AdNovum Informatik AG
 */
@Stateless
public class SimpleVehicleAdminServiceBean extends SimpleVehicleAdminServiceDispatcher 
	implements SimpleVehicleAdminServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SimpleVehicleAdminServiceBean.class);
	
	/**
	 * @param skip from pager
	 * @param num from pager
	 * @param query user input, will be transformed and passed as search value on sevaral fields
	 * @adnlog.exclude return
	 */
	public List fetchCarriers(int skip, int num, String query) {
		// [[
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCarriers", params);
		}
		// ]]
		try {
			List list = new ArrayList();
			ReadWriteDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
			
			Filter filter =  new FilterImpl();
			long id = Constant.INVALID_KEY;
			try{				
				 id = Long.valueOf(query);
			}
			catch (NumberFormatException e){
				id = Constant.INVALID_KEY;
			}
			
			if ( id != Constant.INVALID_KEY ){
				filter.addExpression(Restrictions.eq(CarrierEntity.ATTR_CARRIER_ID, id));
			}
			list = dao.find(filter);
			return list;
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
	 * @param carrierId the id of the carrier to which tre simpleVehicles will be queried
	 * @adnlog.exclude return
	 */
	public List fetchSimpleVehiclesForCarrier(long carrierId) {
		// [[
		Object[][] params = {{"carrierId", carrierId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSimpleVehiclesForCarrier", params);
		}
		// ]]
		try {
			List list = new ArrayList();
			ReadWriteDao<SimpleVehicleEntity> dao = getDaoProvider().getSimpleVehicleEntityDao();
			Filter filter =  new FilterImpl();
			
			filter.addExpression(Restrictions.eq(SimpleVehicleEntity.ATTR_CARRIER_ID, carrierId));
			filter.addExpression(Order.asc(SimpleVehicleEntity.ATTR_VEHICLE_NAME));
			list = dao.find(filter);
			return list;
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
	 * @param simpleVehicleId the id of the simpleVehicle which is going to be returned
	 * @adnlog.exclude return
	 */
	public SimpleVehicleEntity fetchSimpleVehicle(long simpleVehicleId) {
		// [[
		Object[][] params = {{"simpleVehicleId", simpleVehicleId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSimpleVehicle", params);
		}
		// ]]
		try {
			
			ReadWriteDao<SimpleVehicleEntity> dao = getDaoProvider().getSimpleVehicleEntityDao();
			SimpleVehicleEntity simpleVehicle = dao.findById(simpleVehicleId);
			
			return simpleVehicle;
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
	 * @param carrierId the id of the carrier which is going to be returned
	 * @adnlog.exclude return
	 */
	public CarrierEntity fetchCarrier(long carrierId) {
		// [[
		Object[][] params = {{"carrierId", carrierId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCarrier", params);
		}
		// ]]
		try {
			
			ReadWriteDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
			CarrierEntity carrier = dao.findById(carrierId);
			return carrier;
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
	 * Returns true in case the simpleVehicle has an active session (the driver driving the simpleVehicle has an active session).
	 * @param simpleVehicle
	 * @return
	 */
	public boolean isSimpleVehicleInSession (SimpleVehicleEntity simpleVehicle){
		// [[
		boolean result = false;
		Object[][] params = {{"simpleVehicle", simpleVehicle}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isSimpleVehicleInSession", params);
		}
		// ]]
		try {
			// 2012-Nov-05/awe: implement (no driver for simple vehicle)
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
     * If you do not need to have optimisticlock exception for all participant elements (trailer, container), then
     * use rather updateSimpleVehicle(SimpleVehicleEntity simpleVehicle, TrailerEntity newTrailer,  ContainerEntity newContainer, boolean allowActiveAssignment) which will retrieve 
     * the current composition, and pass here those.
     * 
     * Updates an existing simpleVehicle, saves rfids, and pool associations.
     * Used BOTH in simpleVehicle admin, and from different sources, where the composition has to be updated.
     * Originally it was desinged for simpleVehicle admin, where it was quite realistic that the simpleVehicle, the driver, the
     * trailer, container or anything related will be updated by pda events at the same time, and it was a requirement to 
     * be able to handle this case, and take certain steps in this case. This is the cause why the originaltrailer and other
     * original parameters are passed here, and are not retrieved from the db.
     * 
     * The passed composition elements will be updated as the following: 
     * original trailer: this will be released from the simpleVehicle if the new trailer is null, or it is not the same as this one. current_simpleVehicle_id is nulled 
     * but if the trailer had a container its container will also be updated as current_simpleVehicle_id set to null.
     * new trailer:  this will be attached to the simpleVehicle as current_simpleVehicle_id set to the simpleVehicle in question, and it's container also will get the current_simpleVehicle_id
     * originalContainer: this will be detached from the simpleVehicle
     * 
     * @param simpleVehicle the simpleVehicle to update
     * @param updatePools only set to true in case pool associations have to be updated
     * @param selectedPoolIds these are the pools to what the simpleVehicle belongs to
     * @param newTrailer new trailer of the composition, this trailer is going to be attached to the simpleVehicle
     * @param originalTrailer the original trailer of the simpleVehicle, this trailer is going to be released (current_simpleVehicle_id null)
     * @param newContainer	the new container, this container is going to be attached to the simpleVehicle OR the trailer (see description of tlgnc_container current_simpleVehicle and current_trailer_id)
     * @param originalContainer the original container, this is going to be released completely by setting both current_simpleVehicle and current_trailer to null
     * @param allowActiveAssignment in case this is true, an exception is thrown if there is an active assignment for the composition
     *
     */
    public SimpleVehicleEntity updateSimpleVehicle(SimpleVehicleEntity simpleVehicle, boolean allowActiveAssignment) {
		// [[
		SimpleVehicleEntity result = null;
		Object[][] params = {{"simpleVehicle", simpleVehicle},
							{"allowActiveAssignment", allowActiveAssignment + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateSimpleVehicle", params);
		}
		// ]]
		try {		
            ReadWriteDao<SimpleVehicleEntity> dao = getDaoProvider().getSimpleVehicleEntityDao();
            simpleVehicle = dao.merge(simpleVehicle);
			
            return result = simpleVehicle;
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
	 * Returns immediately with true in case there is an active assignment for any of the loads in the list.
	 * @param loads
	 * @return
	 */
	public boolean isActiveAssignmentPresent(List loads) {
		// [[
		boolean result = false;
		Object[][] params = {{"loads", loads}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isActiveAssignmentPresent", params);
		}
		// ]]
		try {
			boolean hasAssignment = false;
			if (loads != null && !loads.isEmpty()) {					
				for (Iterator it=loads.iterator();it.hasNext();) {
					LoadEntity load = (LoadEntity)it.next();
					Filter filter = new FilterImpl();
					filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
					filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD, load));
					long count = getDaoProvider().getAssignmentPartEntityDao().count(filter);
					if (count > 0) {
						hasAssignment = true;
						break;
					}
			}
			}
			return result = hasAssignment;
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
	 * Returns the active loads for the simpleVehicle. (Active means loaded, but not yet unloaded).
	 * @param simpleVehicle
	 * @return
	 */
	public List getLoadsOfSimpleVehicle(SimpleVehicleEntity simpleVehicle) {
		// [[
		List result = null;
		Object[][] params = {{"simpleVehicle", simpleVehicle}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLoadsOfSimpleVehicle", params);
		}
		// ]]
		try {
			ReadWriteDao<LoadEntity> dao = getDaoProvider().getLoadEntityDao();
			Filter unloadedFilter = new FilterImpl();
			unloadedFilter.addExpression(Restrictions.eq(LoadEntity.ATTR_SIMPLE_VEHICLE_ID, simpleVehicle.getSimpleVehicleId() ));
			unloadedFilter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_LOADING_TS));
			unloadedFilter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
			List unloadedEntries = dao.find(unloadedFilter);
			return result = unloadedEntries;
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
	 * Creates a new simpleVehicle, and saves its fields. 
	 */
	public SimpleVehicleEntity createSimpleVehicle(SimpleVehicleEntity simpleVehicle) {
		// [[
		SimpleVehicleEntity result = null;
		Object[][] params = {{"simpleVehicle", simpleVehicle}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createSimpleVehicle", params);
		}
		// ]]
		try {
			ReadWriteDao<SimpleVehicleEntity> dao = getDaoProvider().getSimpleVehicleEntityDao();
			simpleVehicle = dao.persist(simpleVehicle);
						
			return result = simpleVehicle;
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
	 * Get last position of simpleVehicle according to EVENT_TIMESTAMP. The simpleVehicle's last position is defined so.
	 * 
	 * @param simpleVehicleId
	 * @return double []
	 */
	public Double[] fetchLastPosition( long simpleVehicleId) {
		// [[
		Double[] result3 = null;
		Object[][] params = {{"simpleVehicleId", simpleVehicleId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchLastPosition", params);
		}
		// ]]
		try {
			Double [] coordinates = new Double [2];
			ReadWriteDao<BizeventEntity> dao = getDaoProvider().getBizeventEntityDao();
			Filter filter = new FilterImpl();
			
			filter.addExpression(Restrictions.isNotNull(BizeventEntity.ATTR_COORD_LAT));
			filter.addExpression(Restrictions.isNotNull(BizeventEntity.ATTR_COORD_LON));
			filter.addExpression(Restrictions.eq(BizeventEntity.ATTR_SIMPLE_VEHICLE_ID, simpleVehicleId));
			filter.addExpression(Order.desc(BizeventEntity.ATTR_CTL_CRE_TS));
			
			List found = dao.findNRecs(0, 1, filter);
			if (found != null && !found.isEmpty() ){
				BizeventEntity event = (BizeventEntity) found.get(0);
				coordinates[0] = event.getCoordLat();
				coordinates[1] = event.getCoordLon();
			}
					
			return result3 = coordinates;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result3);
			}
		// ]]
		}
	}

	/**
	 * Query for certain parameters of simpleVehicle. Used for deterimining id the simpleVehicle is unique.
	 * Returns true if there is another simpleVehicle with the parameters provided. Returns false in case
	 * the simpleVehicle is still saveable, and will not interfere with unique constraints.
	 * @param simpleVehicleName
	 * @param regNo
	 * @return
	 */
	public boolean findSimpleVehicleWithUniqueParameters(Long simpleVehicleId, String simpleVehicleName, String regNo){
		// [[
		boolean result = false;
		Object[][] params = {{"simpleVehicleId", simpleVehicleId},
							{"simpleVehicleName", simpleVehicleName},
							{"regNo", regNo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findSimpleVehicleWithUniqueParameters", params);
		}
		// ]]
		try {
			
			Filter filt = new FilterImpl();
			filt.addExpression(Restrictions.or(
					Restrictions.ilike(SimpleVehicleEntity.ATTR_VEHICLE_NAME, simpleVehicleName),
					Restrictions.ilike(SimpleVehicleEntity.ATTR_VEHICLE_REG_NO, regNo)
				));
			
			if (simpleVehicleId != null) {				
				filt.addExpression(Restrictions.neq(SimpleVehicleEntity.ATTR_SIMPLE_VEHICLE_ID, simpleVehicleId));
			}
			List simpleVehiclesWithParameter = getDaoProvider().getSimpleVehicleEntityDao().find(filt);
			if (simpleVehiclesWithParameter != null && !simpleVehiclesWithParameter.isEmpty()) {
				return result = true;
			}
			else{
				return result = false;
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
	
	
	public BigDecimal getTareWeightTComposition(SimpleVehicleEntity simpleVehicle) {
		
		// TARA
		BigDecimal decimal = simpleVehicle.getTareWeightT();			
	
		return decimal;
	}
	
	
	public BigDecimal getSimpleVehicleCompositionMaxNetWeightT(SimpleVehicleEntity simpleVehicle) {
    	
    	BigDecimal maxNetWeightT = null;
		BigDecimal tareWeightT = getTareWeightTComposition(simpleVehicle);
		BigDecimal maxGrossWeightT = simpleVehicle.getMaxGrossWeightT();
    	
		if ( (maxGrossWeightT != null) && (tareWeightT != null) ) {
			maxNetWeightT = maxGrossWeightT.subtract(tareWeightT);
		}
		return maxNetWeightT;
    }
}
