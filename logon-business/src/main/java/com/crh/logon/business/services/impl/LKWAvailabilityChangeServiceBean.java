/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.LKWAvailabilityChangeServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.LKWAvailabilityChangeServiceLocalInterface;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.TruckUnavailabilityEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class LKWAvailabilityChangeServiceBean extends LKWAvailabilityChangeServiceDispatcher 
	implements LKWAvailabilityChangeServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LKWAvailabilityChangeServiceBean.class);

	
	/**
	 * Loads the data of the truck with the given Id. 
	 * @param truckId
	 * @return
	 */
	public TruckEntity fetchTruckDetail(long truckId){
		// [[
		TruckEntity result = null;
		Object[][] params = {{"truckId", truckId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchTruckDetail", params);
		}
		// ]]
		try {
			TruckEntity entity = getDaoProvider().getTruckEntityDao().findById(truckId);
			return result = entity;
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
	 * Loads a list when the truck is not available.  
	 * @param truckId
	 * @return
	 */
	public List fetchTruckUnavailabilities(long truckId){
		// [[
		List result1 = null;
		Object[][] params = {{"truckId", truckId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchTruckUnavailabilities", params);
		}
		// ]]
		try {
			ReadWriteDao<TruckUnavailabilityEntity> dao = getDaoProvider().getTruckUnavailabilityEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(TruckUnavailabilityEntity.ATTR_TRUCK_ID, truckId));
			filter.addExpression(Restrictions.eq(TruckUnavailabilityEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			
			//which did not end yet
			filter.addExpression(Restrictions.ge(TruckUnavailabilityEntity.ATTR_END_DAT, new Date()));
			filter.addExpression(Order.asc(TruckUnavailabilityEntity.ATTR_START_DAT));
			List list = dao.find(filter);
			return result1 = list;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}
	/**
	 * Adds a new entry into the table TLGNC_TRUCK_UNAVAILABILITY which contains the info about the breaks (abwesenheiten) of the related truck.  
	 * @param truckId
	 * @return
	 */
	public void storeTruckUnavailability(long truckId, String description, Date timeFrom, Date timeUntil){
		// [[
		Object[][] params = {{"truckId", truckId + ""},
							{"description", description},
							{"timeFrom", timeFrom},
							{"timeUntil", timeUntil}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "storeTruckUnavailability", params);
		}
		// ]]
		try {
			ReadWriteDao<TruckUnavailabilityEntity> dao = getDaoProvider().getTruckUnavailabilityEntityDao();
			TruckUnavailabilityEntity entity = new TruckUnavailabilityEntity();
			TruckEntity truck = fetchTruckDetail(truckId);
			entity.setTruck(truck);
			entity.setDescription(description);
			entity.setStartDat(timeFrom);
			entity.setEndDat(timeUntil);
			entity.setActiveFlag(Boolean.TRUE);
			dao.persist(entity);
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
	 * Updates an  entry in the table TLGNC_TRUCK_UNAVAILABILITY which contains the info about the breaks (abwesenheiten) of the related truck.  
	 * @param truckId
	 * @return
	 */
	public void updateTruckUnavailability(long entryId, String description, Date timeFrom, Date timeUntil){
		// [[
		Object[][] params = {{"entryId", entryId + ""},
							{"description", description},
							{"timeFrom", timeFrom},
							{"timeUntil", timeUntil}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateTruckUnavailability", params);
		}
		// ]]
		try {
			ReadWriteDao<TruckUnavailabilityEntity> dao = getDaoProvider().getTruckUnavailabilityEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(TruckUnavailabilityEntity.ATTR_TRUCK_UNAVAILABILITY_ID, entryId));
			TruckUnavailabilityEntity entity = dao.findSingle(filter);
			entity.setDescription(description);
			entity.setStartDat(timeFrom);
			entity.setEndDat(timeUntil);
			entity.setActiveFlag(Boolean.TRUE);
			dao.merge(entity);
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
	 * Deletes  entry in the table TLGNC_TRUCK_UNAVAILABILITY which contains the info about the breaks (abwesenheiten) of the related truck.  
	 * @param truckId
	 * @return
	 */
	public void deleteTruckUnavailability(long entryId){
		// [[
		Object[][] params = {{"entryId", entryId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "deleteTruckUnavailability", params);
		}
		// ]]
		try {
			ReadWriteDao<TruckUnavailabilityEntity> dao = getDaoProvider().getTruckUnavailabilityEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(TruckUnavailabilityEntity.ATTR_TRUCK_UNAVAILABILITY_ID, entryId));
			TruckUnavailabilityEntity entity = dao.findSingle(filter);
			entity.setActiveFlag(Boolean.FALSE);
			dao.merge(entity);
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
