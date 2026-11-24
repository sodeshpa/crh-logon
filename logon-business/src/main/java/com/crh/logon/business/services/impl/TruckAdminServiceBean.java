/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.interceptor.Interceptors;
import javax.persistence.OptimisticLockException;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.dispatchers.TruckAdminServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TrailerAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.ContainerHistoryEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.PdaSessionEntity;
import com.crh.logon.persistence.entities.common.PoolCarrierEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.common.PoolTruckEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.TruckTrailerHistoryEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.monitoring.TruckPositionEntity;
import com.crh.logon.persistence.entities.refdata.SapVehicleCharBsEntity;
import com.crh.logon.persistence.entities.refdata.SapVehicleTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SapVehicleTypeTxEntity;
import com.crh.logon.persistence.entities.sap.SapVehicleEntity;
import com.crh.logon.persistence.entities.views.DriverWithoutSessionEntity;
import com.crh.logon.persistence.entities.views.PlanningTruckEntity;
import com.crh.logon.persistence.entities.views.TruckLastKnownCoordEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * 
 * @author AdNovum Informatik AG
 */
/**
 * @author AdNovum Informatik AG
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
/**
 * @author AdNovum Informatik AG
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
@Stateless
@Interceptors(UserInfoInterceptor.class)
@Named
public class TruckAdminServiceBean extends TruckAdminServiceDispatcher implements TruckAdminServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TruckAdminServiceBean.class);
	
	@EJB private TrailerAdminServiceLocalInterface trailerAdminService;
    @EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private RFIDServiceLocalInterface rfidService;


	static final Long[] pdaSessionCdOnline = {
			Constant.PDA_SESSION_STATUS_INITIALIZING,
			Constant.PDA_SESSION_STATUS_READY
	};

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
	 * Returns the trucks belonging to the given carrierId.
	 * @param showOnlyActiveTrucks if true, only trucks with active flags == true are returned
	 * @param carrierId the id of the carrier to which trucks will be queried
	 * @adnlog.exclude return
	 */
	@AdnTrace
	public List<TruckEntity> fetchTrucksForCarrier(long carrierId, boolean showOnlyActiveTrucks) {
		List<TruckEntity> list = new ArrayList<TruckEntity>();
		ReadWriteDao<TruckEntity> dao = getDaoProvider().getTruckEntityDao();
		Filter<TruckEntity> filter = new FilterImpl<TruckEntity>();

		filter.addExpression(Restrictions.eq(TruckEntity.ATTR_CARRIER_ID, carrierId));
		filter.addExpression(Order.asc(TruckEntity.ATTR_TRUCK_NAME));
		if (showOnlyActiveTrucks) {
			filter.addExpression(Restrictions.eq(TruckEntity.ATTR_ACTIVE_FLAG, true));
		}
		list = dao.find(filter);
		return list;
	}	
	
	/**
	 * @param truckId the id of the truck which is going to be returned
	 * @adnlog.exclude return
	 */
	public TruckEntity fetchTruck(long truckId) {
		// [[
		Object[][] params = {{"truckId", truckId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchTruck", params);
		}
		// ]]
		try {
			
			ReadWriteDao<TruckEntity> dao = getDaoProvider().getTruckEntityDao();
			TruckEntity truck = dao.findById(truckId);
			
			if (truck.getRfidobject() != null ) {
				truck.getRfidobject().initialize();
			}
			return truck;
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
	 * @param carrierId the id of the carrier which is going to be returned
	 * @adnlog.exclude return
	 */
	public PoolEntity fetchPool(long poolId) {
		// [[
		Object[][] params = {{"poolId", poolId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPool", params);
		}
		// ]]
		try {
			ReadWriteDao<PoolEntity> dao = getDaoProvider().getPoolEntityDao();
			PoolEntity pool = dao.findById(poolId);
			return pool;
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
     *Fetches all the pools for the given carrier, but returns pooltxentities, since we need the names.
     */
    public List fetchPools(Long carrierId, Locale locale){
			// [[
			List result = null;
			Object[][] params = {{"carrierId", carrierId},
								{"locale", locale}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "fetchPools", params);
			}
			// ]]
			try {
				ReadWriteDao<PoolCarrierEntity> dao = getDaoProvider().getPoolCarrierEntityDao();
				Filter filter = new FilterImpl();
				filter.addExpression(Restrictions.eq(PoolCarrierEntity.ATTR_CARRIER_ID, carrierId ));
				
				List poolCarrierList = dao.find(filter);
				List poolsList = new ArrayList();
				List poolTxList = new ArrayList();
				{
					Iterator it = poolCarrierList.iterator();
					while (it.hasNext()) {
						PoolCarrierEntity object = (PoolCarrierEntity) it.next();
						PoolEntity pool = object.getPool();
						if (pool.getCtlActCd()) {
	                        poolsList.add(pool.getPoolId());
						}
					}
				}
				
				if (poolsList != null && !poolsList.isEmpty())	{
					ReadWriteDao<PoolTxEntity> dao2 = getDaoProvider().getPoolTxEntityDao();
					Filter filt = new FilterImpl();
					filt.addExpression(Restrictions.and(Restrictions.in("poolTxPK.poolId", poolsList), Restrictions.eq("poolTxPK.langCd", locale.getLanguage())));
					poolTxList = dao2.find(filt);
				}
				return result = poolTxList;
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
	 * Loads the currently free drivers. Free in the meaning that they do not have active session, or if
	 * they have active session, but dont have assigned truck.
	 */
	public List fetchFreeDrivers(Long carrierId) {
		// [[
		List result = null;
		Object[][] params = {{"carrierId", carrierId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchFreeDrivers", params);
		}
		// ]]
		try {
			List freeDrivers = new ArrayList();
			ReadOnlyDao<DriverWithoutSessionEntity> dao = getDaoProvider().getDriverWithoutSessionEntityDao();
			Filter filt = new FilterImpl();
			// FIXME (emoke Jun 24, 2009):maximum?
			filt.addExpression(Order.asc(DriverWithoutSessionEntity.ATTR_NAME));
			filt.addExpression(Restrictions.eq(DriverWithoutSessionEntity.ATTR_CARRIER_ID, carrierId));
			freeDrivers = dao.findNRecs(0, 100, filt);
			
			return result = freeDrivers;
			
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
	 * Retrieves the driver for the truckId.
	 */
	public DriverEntity fetchDriver(long truckId) {
		// [[
		DriverEntity result = null;
		Object[][] params = {{"truckId", truckId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchDriver", params);
		}
		// ]]
		try {
			ReadWriteDao<DriverEntity> driverDao = getDaoProvider().getDriverEntityDao();
			Filter<DriverEntity> driverFilter = new FilterImpl<DriverEntity>();
			driverFilter.addExpression(Restrictions.eq(DriverEntity.ATTR_CURRENT_TRUCK_ID, truckId));
			result = driverDao.findSingle(driverFilter);
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
     * Retrieves the driverEntities for the driverIds.
     */
	public List fetchDriverEntities(List driverIds) {
		// [[
		List<DriverEntity> result = null;
		Object[][] params = {{"driverIds", driverIds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchDriverEntities", params);
		}
		// ]]
		try {
			final ReadOnlyDao<DriverEntity> dao = getDaoProvider().getDriverEntityDao();
			final Filter<DriverEntity> filt = dao.createFilter();
			if ((driverIds != null) && (!driverIds.isEmpty())) {
				filt.addExpression(Restrictions.in(DriverEntity.ATTR_DRIVER_ID, driverIds));
			}
            return result = dao.find(filt);
            
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
	 * Loads the selected pools for the given truck id and locale;
	 */
	public List fetchSelectedPoolsTxForTruck(long truckId, Locale locale) {
		// [[
		List result = null;
		Object[][] params = {{"truckId", truckId + ""},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSelectedPoolsTxForTruck", params);
		}
		// ]]
		try {
			ReadWriteDao<PoolTruckEntity> dao = getDaoProvider().getPoolTruckEntityDao();
			
			Filter filter = new FilterImpl();			
			filter.addExpression(Restrictions.eq(PoolTruckEntity.ATTR_TRUCK_ID, truckId ));
			
			List poolTruckList = dao.find(filter);
			List poolsList = new ArrayList();
			List poolTxList = new ArrayList();
			{
				Iterator it = poolTruckList.iterator();
				while (it.hasNext()) {
					PoolTruckEntity object = (PoolTruckEntity) it.next();
					PoolEntity pool = object.getPool();
					poolsList.add(pool.getPoolId());
				}
			}
			
			if (poolsList!= null && !poolsList.isEmpty())	{
				ReadWriteDao<PoolTxEntity> dao2 = getDaoProvider().getPoolTxEntityDao();
				Filter filt = new FilterImpl();
				filt.addExpression(Restrictions.and(Restrictions.in("poolTxPK.poolId", poolsList), Restrictions.eq("poolTxPK.langCd", locale.getLanguage())));
				poolTxList = dao2.find(filt);
			}
			return result = poolTxList;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List fetchSelectedPoolsForTruck(long truckId) {
		// [[
		List result = null;
		Object[][] params = {{"truckId", truckId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSelectedPoolsForTruck", params);
		}
		// ]]
		try {
			ReadWriteDao<PoolTruckEntity> dao = getDaoProvider().getPoolTruckEntityDao();
			
			Filter filter = new FilterImpl();			
			filter.addExpression(Restrictions.eq(PoolTruckEntity.ATTR_TRUCK_ID, truckId ));
			
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
	 * Fetches the trucks:
	 * - belonging to the given pool and carrier
	 * - having an allowed truck type code
	 * - ordered by priority ascending
	 * 
	 * @param poolId if null, no restriction will be applied for pools
	 * @param carrierId mandatory id for carrier restriction
	 * @param allowedTruckTypeCode non-empty list of truck type codes
	 */
	@AdnTrace
	public List<PlanningTruckEntity> findPlanningTrucksByPoolCarrierAndTruckType(Long poolId,
			Long carrierId,
			List<Long> allowedTruckTypeCodes) {
		Filter<PlanningTruckEntity> planningTruckFilter = new FilterImpl<PlanningTruckEntity>();
		if (poolId != null) {
			planningTruckFilter.addExpression(Restrictions.eq(PlanningTruckEntity.ATTR_POOL_ID,
					poolId));
		}
		planningTruckFilter.addExpression(Restrictions.eq(PlanningTruckEntity.ATTR_CARRIER_ID, carrierId));
		planningTruckFilter.addExpression(DbUtils.createSplittedInRestriction(
				PlanningTruckEntity.ATTR_TRUCK_TYPE_CD, allowedTruckTypeCodes));
		DaoProvider daoProvider = getDaoProvider();
		ReadOnlyDao<PlanningTruckEntity> planningTruckEntityDao = daoProvider.getPlanningTruckEntityDao();
		return planningTruckEntityDao.find(planningTruckFilter);
	}
	
	/**
	 * Updates the selected pools for this truck. 
	 * @param poolIds
	 * @param truckId
	 */
	private void updateSelectedPools(long[] poolIds, TruckEntity truck) {
		// [[
		Object[][] params = {{"poolIds", poolIds},
							{"truck", truck}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateSelectedPools", params);
		}
		// ]]
		try {
			// TODO (emoke Feb 19, 2009):check if this delete all remove new mechanism is welcome here?
			ReadWriteDao<PoolTruckEntity> dao = getDaoProvider().getPoolTruckEntityDao();		
			List list = fetchSelectedPoolsForTruck(truck.getTruckId());
			Iterator it = list.iterator();
			
			//remove all first
			while (it.hasNext()) {
				PoolTruckEntity ent = (PoolTruckEntity) it.next();
				dao.remove(ent);
			}
			
			//save new
			for ( int i = 0 ; i < poolIds.length ; i++ ) {
				PoolEntity poolEntity = getDaoProvider().getPoolEntityDao().findById(poolIds[i]);
				PoolTruckEntity newEnt = new PoolTruckEntity();
				newEnt.setPool(poolEntity);
				newEnt.setTruck(truck);
				dao.persist(newEnt);
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
	 * Assigns the driver to this truck, and updates the sooner driver (if there was any).
	 * @param allowPdaSession on the truck admin screen no update should be possible in case the driver has an active pda session in the case of pda calls it must always be possible to update the truck though
	 */
	public DriverEntity updateDriver(TruckEntity truck, DriverEntity oldDriver, DriverEntity newDriver, boolean allowPdaSession) {
		// [[
		DriverEntity result = null;
		Object[][] params = {{"truck", truck},
							{"oldDriver", oldDriver},
							{"newDriver", newDriver},
							{"allowPdaSession", allowPdaSession + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateDriver", params);
		}
		// ]]
		try {
			//find the current driver who drives this truck
			ReadWriteDao<DriverEntity> dao = getDaoProvider().getDriverEntityDao();
			DriverEntity lastDriver = null;
			if (truck.getTruckId() != null) {
				Filter filter = new FilterImpl();				
				filter.addExpression(Restrictions.eq(DriverEntity.ATTR_CURRENT_TRUCK_ID, truck.getTruckId()));
				lastDriver = dao.findSingle(filter);
			}
			if ((oldDriver != null && lastDriver == null) ||
			    (oldDriver == null && lastDriver != null)) {
			    throw new OptimisticLockException("last driver of truck (" + truck.getTruckId() + ") was changed in the meantime.");
			}
			if (oldDriver != null) {
				if (newDriver != null && oldDriver.getDriverId().equals(newDriver.getDriverId())) {
					// no need to update the driver, it remained so
					return result = oldDriver;
				}
				oldDriver.setCurrentTruck(null);
                // Flush to prevent problems with unique constraint
				dao.merge(oldDriver);
			}
			if (newDriver == null) {
				return result = null;
			}
			
			// This views returns all drivers WITHOUT an active PDA session
			DriverWithoutSessionEntity driverPda = null;
			if (!allowPdaSession) {
				Filter filt = new FilterImpl();
				filt.addExpression(Restrictions.eq(DriverWithoutSessionEntity.ATTR_DRIVER_ID, newDriver.getDriverId()));
				driverPda = getDaoProvider().getDriverWithoutSessionEntityDao().findSingle(filt);
			}
			if ( (allowPdaSession) || (driverPda != null) ) { //still available
				newDriver.setCurrentTruck(truck);
				dao.merge(newDriver);
				return result = newDriver;						
			}
			
			return result = null; // update failed
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
	 * Returns true in case the truck has an active session (the driver driving the truck has an active session).
	 * @param truck
	 * @return
	 */
	public boolean isTruckInSession (TruckEntity truck){
		// [[
		boolean result = false;
		Object[][] params = {{"truck", truck}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isTruckInSession", params);
		}
		// ]]
		try {
			if (truck.getTruckId() != null) {								
				DriverEntity driver = fetchDriver(truck.getTruckId());
				result = isDriverOnline(driver);
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
	 * @param driver
	 * @return
	 */
	public boolean isDriverOnline(DriverEntity driver) {
		if (driver == null) {
			return false;
		}
		boolean result;
		ReadWriteDao<PdaSessionEntity> sessionDao = getDaoProvider().getPdaSessionEntityDao();
		Filter filter = new FilterImpl();
		UserEntity user = driver.getUser();
		Long userId = user.getUserId();
		filter.addExpression(Restrictions.eq(PdaSessionEntity.ATTR_USER_ID, userId));
		filter.addExpression(Restrictions.in(PdaSessionEntity.ATTR_PDA_SESSION_STATUS_CD, pdaSessionCdOnline));
		List list = sessionDao.find(filter);
		if (list == null || list.isEmpty()){
			//if there is no initializing or ready session then it is not online
			result = false;
		}
		else{					
			result = true; 
		}
		return result;
	}
	
	/**
	 * Simply updates the truck entity itself, does not change the composition.
	 * This is used when a simple non-composition related attribute of the truck is updated (eg. priority, return_shippingpoint_id).
	 * 
	 * @param truck
	 * @param allowActiveAssignment
	 * @return
	 */
	public TruckEntity updateTruckSimple(TruckEntity truck, boolean allowActiveAssignment) {
		// [[
		TruckEntity result = null;
		Object[][] params = {{"truck", truck},
							{"allowActiveAssignment", allowActiveAssignment + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateTruckSimple", params);
		}
		// ]]
		try {
			TrailerEntity currtrailer = fetchTrailerForTruck(truck.getTruckId(), false);
			ContainerEntity currcontainer = fetchContainerForTruckTrailerComposition(truck, currtrailer);
			
			updateTruck(truck, false, null, currtrailer, currtrailer , currcontainer, currcontainer, allowActiveAssignment);
			return result = truck;
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
	 * Updated the truck composition. Releases the original trailer, container if there was one, and updates the composition.
	 * @param truck
	 * @param newTrailer
	 * @param newContainer
	 * @param allowActiveAssignment
	 * @return
	 */
    public TruckEntity updateTruck(TruckEntity truck, TrailerEntity newTrailer,  ContainerEntity newContainer, boolean allowActiveAssignment) {
		// [[
		TruckEntity result1 = null;
		Object[][] params = {{"truck", truck},
							{"newTrailer", newTrailer},
							{"newContainer", newContainer},
							{"allowActiveAssignment", allowActiveAssignment + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateTruck", params);
		}
		// ]]
		try {
			TrailerEntity currtrailer = fetchTrailerForTruck(truck.getTruckId(), false);
			ContainerEntity currcontainer = fetchContainerForTruckTrailerComposition(truck,
					currtrailer);
			
			updateTruck(truck, false, null, newTrailer, currtrailer , newContainer, currcontainer, allowActiveAssignment);
			return result1 = truck;
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
	 * Returns the container attached either to the truck or to the trailer. 
	 * In case the container is attached to the trailer, both current_truck_id and current_trailer_id is
	 * set for the container.
	 * @param truck
	 * @param trailer
	 * @return
	 */
	public ContainerEntity fetchContainerForTruckTrailerComposition(TruckEntity truck,
			TrailerEntity trailer) {
		// [[
		ContainerEntity result = null;
		Object[][] params = {{"truck", truck},
							{"trailer", trailer}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchContainerForTruckTrailerComposition", params);
		}
		// ]]
		try {
			ContainerEntity currcontainer = fetchContainerForTruck(truck.getTruckId(), false);
			if (currcontainer == null && trailer != null) {
				//primary is the container of the truck, if there is no container for the truck, there may be one for the trailer check that too
				currcontainer = trailerAdminService.fetchContainerForTrailer(trailer.getTrailerId(), false);
			}
			return result = currcontainer;
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
     * use rather updateTruck(TruckEntity truck, TrailerEntity newTrailer,  ContainerEntity newContainer, boolean allowActiveAssignment) which will retrieve 
     * the current composition, and pass here those.
     * 
     * Updates an existing truck, saves rfids, and pool associations.
     * Used BOTH in truck admin, and from different sources, where the composition has to be updated.
     * Originally it was desinged for truck admin, where it was quite realistic that the truck, the driver, the
     * trailer, container or anything related will be updated by pda events at the same time, and it was a requirement to 
     * be able to handle this case, and take certain steps in this case. This is the cause why the originaltrailer and other
     * original parameters are passed here, and are not retrieved from the db.
     * 
     * The passed composition elements will be updated as the following: 
     * original trailer: this will be released from the truck if the new trailer is null, or it is not the same as this one. current_truck_id is nulled 
     * but if the trailer had a container its container will also be updated as current_truck_id set to null.
     * new trailer:  this will be attached to the truck as current_truck_id set to the truck in question, and it's container also will get the current_truck_id
     * originalContainer: this will be detached from the truck
     * 
     * @param truck the truck to update
     * @param updatePools only set to true in case pool associations have to be updated
     * @param selectedPoolIds these are the pools to what the truck belongs to
     * @param newTrailer new trailer of the composition, this trailer is going to be attached to the truck
     * @param originalTrailer the original trailer of the truck, this trailer is going to be released (current_truck_id null)
     * @param newContainer	the new container, this container is going to be attached to the truck OR the trailer (see description of tlgnc_container current_truck and current_trailer_id)
     * @param originalContainer the original container, this is going to be released completely by setting both current_truck and current_trailer to null
     * @param allowActiveAssignment in case this is true, an exception is thrown if there is an active assignment for the composition
     *
     */
    public TruckEntity updateTruck(TruckEntity truck, boolean updatePools, long[] selectedPoolIds, TrailerEntity newTrailer, TrailerEntity originalTrailer,  ContainerEntity newContainer, ContainerEntity originalContainer, boolean allowActiveAssignment) {
		// [[
		TruckEntity result = null;
		Object[][] params = {{"truck", truck},
							{"updatePools", updatePools + ""},
							{"selectedPoolIds", selectedPoolIds},
							{"newTrailer", newTrailer},
							{"originalTrailer", originalTrailer},
							{"newContainer", newContainer},
							{"originalContainer", originalContainer},
							{"allowActiveAssignment", allowActiveAssignment + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateTruck", params);
		}
		// ]]
		try {
			
			final boolean isTrailerChanged =	
				(newTrailer == null && originalTrailer != null) || 
				(newTrailer != null && originalTrailer == null) ||
				(newTrailer != null && originalTrailer != null && !newTrailer.getTrailerId().equals(originalTrailer.getTrailerId()));
			final boolean isContainerChanged = 
				(newContainer != null && originalContainer == null) ||
				(newContainer == null && originalContainer != null) ||
				(newContainer != null && originalContainer != null && !newContainer.getContainerId().equals(originalContainer.getContainerId())); 
			final boolean isLoadCompositionUpdateNeeded = ( isTrailerChanged || isContainerChanged );
			
			if ( (isLoadCompositionUpdateNeeded) && (!allowActiveAssignment) ) {
				// check for active assignments involving the truck's current load(s) and prevent saving in case there is at least one
				List loads = getLoadsOfTruck(truck);
				boolean hasAssignment = isActiveAssignmentPresent(loads);
				if (hasAssignment == true) {
					
					throw new LogonException("Cannot change truck composition; truck's load has an active assignment", Reason.RES_ERROR_TRUCK_TRAILER_IS_ACTIVE);
				}
			}
			boolean isOriginalTrailerCompositionDetached = false;
			if (originalTrailer != null && (newTrailer == null || !newTrailer.getTrailerId().equals(originalTrailer.getTrailerId()))){
				//release the sooner trailer composition
				releaseTrailerCompositionFromTruck(originalTrailer, allowActiveAssignment);
				isOriginalTrailerCompositionDetached = true;
			}
			
            RfidobjectEntity rfidobject = truck.getRfidobject();
            long rfidobjectTypeTruck = Constant.RFIDOBJECT_TYPE_TRUCK;
            AbstractRFIDBean bean = RFIDBean.TruckRFIDBean(truck);
            rfidobject = rfidService.mergeRfIdObject(bean, rfidobjectTypeTruck);
            truck.setRfidobject(rfidobject);
            ReadWriteDao<TruckEntity> dao = getDaoProvider().getTruckEntityDao();
            truck = dao.merge(truck);
            
            boolean isnewTrailerContainerCompositionAttached = false;
            if (newTrailer != null ) {
            	ContainerEntity containerOfNewTrailer = trailerAdminService.fetchContainerForTrailer(newTrailer.getTrailerId(), false);
            	if (containerOfNewTrailer != null && (newContainer == null || (newContainer != null && !newContainer.getContainerId().equals(containerOfNewTrailer.getContainerId())))){					
            		//it must be detached from the previous composition first
            		releaseContainer(containerOfNewTrailer, allowActiveAssignment);
            	}
            	if (containerOfNewTrailer != null && (newContainer == null || (newContainer != null && newContainer.getContainerId().equals(containerOfNewTrailer.getContainerId())))) {
            		isnewTrailerContainerCompositionAttached = true;					
            	}
            	
            	newTrailer = attachTrailerCompositionToTruck(truck, newTrailer,
						allowActiveAssignment);
            	
            }
            
			if ( truck != null && newTrailer != null && truck.getTruckTransportCat().getTruckTransportCatCd().equals(Constant.TRUCK_TRSP_CAT_CONTAINER) ) {
				//in this case the container should only be attached to the truck, because it is not possible to have both
				LOG.error("Truck " + truck.getTruckId() + " is of transport cat container and the new composition contains also a trailer");
			}
			if ( originalContainer != null && (newContainer == null || !newContainer.getContainerId().equals(originalContainer.getContainerId()))) {
				if (originalTrailer == null || !isOriginalTrailerCompositionDetached) {
					//release if there is no trailer involved at all, or the release was not handled separately already where the trailer has been detached
					releaseContainer(originalContainer, allowActiveAssignment);
				}
			}
			
			if (newContainer != null && (newTrailer == null || !isnewTrailerContainerCompositionAttached) ) {					
				attachContainerToTruckTrailerComposition(newContainer, truck, newTrailer,
						allowActiveAssignment);
			}
            if (updatePools) {
            	updateSelectedPools(selectedPoolIds, truck);
            }
            
            return result = truck;
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
	 * Attaches the trailer composition to the givent truck entity. This means that the current_truck_id will be both set 
	 * for the trailer, and it's container - if there is one.
	 * @param truck
	 * @param newTrailer
	 * @param allowActiveAssignment
	 * @return
	 */
	private TrailerEntity attachTrailerCompositionToTruck(TruckEntity truck,
			TrailerEntity newTrailer, boolean allowActiveAssignment) {
		// [[
		TrailerEntity result = null;
		Object[][] params = {{"truck", truck},
							{"newTrailer", newTrailer},
							{"allowActiveAssignment", allowActiveAssignment + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "attachTrailerCompositionToTruck", params);
		}
		// ]]
		try {
			ContainerEntity newTrailersContainer = trailerAdminService.fetchContainerForTrailer(newTrailer.getTrailerId(), true);
			List loads = getLoadsForTrailerComposition(newTrailer,
					newTrailersContainer);
			
			if (!allowActiveAssignment) {
				boolean hasAssignment = isActiveAssignmentPresent(loads);
				if (hasAssignment == true) {
					
					throw new LogonException("Cannot change truck composition; trailer composition's (either attached trailer and/or attached trailers container) load has an active assignment", Reason.RES_ERROR_TRUCK_TRAILER_IS_ACTIVE);		
				}
			}
			newTrailer.setCurrentTruck(truck);
			newTrailer = getDaoProvider().getTrailerEntityDao().merge(newTrailer);
			storeTruckTrailerHistory(newTrailer);
			
			if (newTrailersContainer != null ) {
				newTrailersContainer.setCurrentTruck(truck);
				getDaoProvider().getContainerEntityDao().merge(newTrailersContainer);
			}
			
			// Attach the loads of this trailer (+container) to the truck
			if (loads != null && !loads.isEmpty()) {		
				for (Iterator it=loads.iterator();it.hasNext();) {
					LoadEntity load = (LoadEntity)it.next();
					load.setTruck(truck);
				}
			}
			return result = newTrailer;
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
	 * Releases a trailer and its container from a truck. If the trailer has a container, that is also release from the truck, but
	 * remains with the trailer.
	 * If the container has loads, the composition of the load also changes to the new composition will only belong to the trailer+container.
	 * @param originalTrailer
	 * @param allowActiveAssignment
	 */
	private void releaseTrailerCompositionFromTruck(TrailerEntity originalTrailer, boolean allowActiveAssignment) {
		//the original trailer is going to be detached to the truck, but the container must remain with the trailer!
		ContainerEntity originalTrailersContainer = trailerAdminService.fetchContainerForTrailer(originalTrailer.getTrailerId(), true);
		List loads = getLoadsForTrailerComposition(originalTrailer,
				originalTrailersContainer);
		
		if (!allowActiveAssignment) {
			boolean hasAssignment = isActiveAssignmentPresent(loads);
			if (hasAssignment == true) {

				throw new LogonException("Cannot change truck composition; trailer composition's (either detached trailer and/or detached trailers container) load has an active assignment", Reason.RES_ERROR_TRUCK_TRAILER_IS_ACTIVE);	
			}	
		}
		
		//composition was changed from one trailer to the other (but container stays on trailer)
		originalTrailer.setCurrentTruck(null);
		originalTrailer = getDaoProvider().getTrailerEntityDao().merge(originalTrailer);
		
		if (originalTrailersContainer != null) {					
			originalTrailersContainer.setCurrentTruck(null);
			getDaoProvider().getContainerEntityDao().merge(originalTrailersContainer);
		}
		
		// Detach the loads of this trailer (+container) from the truck
		if (loads != null && !loads.isEmpty()) {					
			for (Iterator it=loads.iterator();it.hasNext();) {
				LoadEntity load = (LoadEntity)it.next();
				load.setTruck(null);
			}
		}
	}

	/**
	 * @param originalTrailer
	 * @param originalTrailersContainer
	 * @return
	 */
	public List getLoadsForTrailerComposition(TrailerEntity originalTrailer,
			ContainerEntity originalTrailersContainer) {
		// [[
		List result = null;
		Object[][] params = {{"originalTrailer", originalTrailer},
							{"originalTrailersContainer", originalTrailersContainer}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLoadsForTrailerComposition", params);
		}
		// ]]
		try {
			Filter filter;
			List loads = null;
			filter = new FilterImpl();
			filter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_LOADING_TS));
			filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
			if (originalTrailersContainer != null) {
				filter.addExpression(Restrictions.or(Restrictions.eq(LoadEntity.ATTR_CONTAINER, originalTrailersContainer), Restrictions.eq(LoadEntity.ATTR_TRAILER, originalTrailer)));
			}
			else {
				filter.addExpression(Restrictions.eq(LoadEntity.ATTR_TRAILER, originalTrailer));
			}
			loads = getDaoProvider().getLoadEntityDao().find(filter);
			return result = loads;
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
	 * Checks whether there are loads on the truck which have not been unloaded, independent of
	 * whether they have been loaded or not. If there are such loads, truck / trailer attributes
	 * must not be changed.
	 * @param truck
	 * @return true if there are such loads, false otherwise.
	 */
	public boolean hasOpenLoad(TruckEntity truck) {
		// [[
		boolean result = false;
		Object[][] params = {{"truck", truck}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "hasOpenLoad", params);
		}
		// ]]
		try {
			ReadOnlyDao<LoadEntity> dao = getDaoProvider().getLoadEntityDao();
			Filter<LoadEntity> f = dao.createFilter();
			f.addExpression(Restrictions.eq(LoadEntity.ATTR_TRUCK_ID, truck.getTruckId()));
			f.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
			long count = dao.count(f);
			return result = (count > 0);
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
	 * The case of containers is somewhat different than the case of trailers and trucks as far they can be attached to truck compositions
	 * and wagons too. 
	 * The combined case is container terminal case, where the container is placed on a wagon, and is unloaded at the container terminal.
	 * At this point the wagon load is not yet unloaded (unloading_ts is NOT yet set) will oly be set when the container is going to be attached to
	 * a truck/trailer - but the wagon_id remains as it was, although the container is not anymore attached to the wagon. (Cause: history of the wagon
	 * is retrieved from the load table).
	 * Therefore the definition of active assignment is a bit refined than just checking active being of the assignment.
	 *
	 * Returns immediately with true in case there is an active assignment for any of the loads in the list.
	 * @param loads
	 * @return
	 */
	public boolean isActiveAssignmentPresentForContainer(List loads, ContainerEntity containerEntity) {
		// [[
		boolean result = false;
		Object[][] params = {{"loads", loads},
							{"containerEntity", containerEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isActiveAssignmentPresentForContainer", params);
		}
		// ]]
		try {
			TruckEntity truck = containerEntity.getCurrentTruck();
			WagonEntity wagon = containerEntity.getCurrentWagon();
			boolean hasAssignment = false;
			if (truck == null && wagon == null ) {
				hasAssignment = false;
			}
			if (loads != null && !loads.isEmpty()) {					
				for (Iterator it = loads.iterator();it.hasNext();) {
					LoadEntity load = (LoadEntity)it.next();
					Filter filter = new FilterImpl();
					filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
					filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_LOAD, load));
					if ( wagon != null ) {
						Restrictions.eq(AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_WAGON, wagon);						
					}
					if ( truck != null ) {
						Restrictions.eq(AssignmentPartEntity.ATTR_LOAD + "." + LoadEntity.ATTR_TRUCK, truck);					
					}
			
			
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
	 * Attach the container to the composition represented by the truck(+trailer). The loads of the container are going
	 * to be attached to the truck/trailer too. Only those loads, which are not related to wagon.
	 * @param newContainer
	 * @param truck
	 * @param trailer
	 * @param allowActiveAssignment
	 */
	public void attachContainerToTruckTrailerComposition(ContainerEntity newContainer,
			TruckEntity truck, TrailerEntity trailer, boolean allowActiveAssignment) {
		// [[
		Object[][] params = {{"newContainer", newContainer},
							{"truck", truck},
							{"trailer", trailer},
							{"allowActiveAssignment", allowActiveAssignment + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "attachContainerToTruckTrailerComposition", params);
		}
		// ]]
		try {
			List loads = getLoadsForContainer(newContainer);
			if (!allowActiveAssignment) {
				boolean hasAssignment = isActiveAssignmentPresentForContainer(loads, newContainer);
				if (hasAssignment) {
					
					throw new LogonException("Cannot change truck/trailer composition; attached container's load has an active assignment.", Reason.RES_ERROR_TRUCK_TRAILER_IS_ACTIVE);
				}
			}		
			
			List fromLoadIds = getLoadedFromLoadIds(loads);
			
			newContainer.setCurrentTruck(truck);
			newContainer.setCurrentTrailer(trailer);
			getDaoProvider().getContainerEntityDao().merge(newContainer);
			storeTruckContainerHistory(newContainer, truck, trailer);
			
			if (loads != null && !loads.isEmpty()) {							
				for (Iterator it=loads.iterator();it.hasNext();) {
					LoadEntity load = (LoadEntity)it.next();
					if ( !fromLoadIds.contains(load.getLoadId())) {
						load.setTruck(truck);
						load.setTrailer(trailer);						
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
	 * Returns a list of the load ids FROM which these loads were loaded.
	 * @param loads
	 * @return
	 */
	private List getLoadedFromLoadIds(List loads) {
		Iterator it1 = loads.iterator();
		List fromLoadIds = new ArrayList();
		while (it1.hasNext()) {
			LoadEntity load = (LoadEntity) it1.next();
			//the original loads in case of rail/road container terminal do not have to be updated with the new container composition! do not touch them!
			if (load.getLoadedFromLoad() != null && !fromLoadIds.contains(load.getLoadId())) {
				fromLoadIds.add(load.getLoadedFromLoad().getLoadId());
			}
		}
		return fromLoadIds;
	}

	/**
	 * Completely releases the container. The container, and it's loads are completely detached from the
	 * truck(+trailer).
	 * @param originalContainer
	 * @param allowActiveAssignment
	 */
	public void releaseContainer(ContainerEntity originalContainer, boolean allowActiveAssignment) {
		// [[
		Object[][] params = {{"originalContainer", originalContainer},
							{"allowActiveAssignment", allowActiveAssignment + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "releaseContainer", params);
		}
		// ]]
		try {
			List loads = getLoadsForContainer(originalContainer);
			if (!allowActiveAssignment) {
				boolean hasAssignment = isActiveAssignmentPresentForContainer(loads, originalContainer);
				if (hasAssignment == true) {
					throw new LogonException("Cannot change truck/truck composition; detached container's load has an active assignment", Reason.RES_ERROR_TRUCK_TRAILER_IS_ACTIVE);
				}
			}
			List fromLoadIds = getLoadedFromLoadIds(loads);
							
			// original container was changed, release it
			originalContainer.setCurrentTruck(null);
			originalContainer.setCurrentTrailer(null);
			getDaoProvider().getContainerEntityDao().merge(originalContainer);
			
			// Detach the loads of this container from the truck and the trailer
			if (loads != null && !loads.isEmpty()) {							
				for (Iterator it=loads.iterator();it.hasNext();) {
					LoadEntity load = (LoadEntity)it.next();
					if ( !fromLoadIds.contains(load.getLoadId())) {						
						load.setTruck(null);
						load.setTrailer(null);
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
	 * Returns the list of active loads for the container
	 * @param container
	 * @return
	 */
	public List getLoadsForContainer(ContainerEntity container) {
		// [[
		List result = null;
		Object[][] params = {{"container", container}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLoadsForContainer", params);
		}
		// ]]
		try {
			if ( container == null ) {
				return result = new ArrayList();
			}
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_LOADING_TS));
			filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
			filter.addExpression(Restrictions.eq(LoadEntity.ATTR_CONTAINER, container));
			List loads = getDaoProvider().getLoadEntityDao().find(filter);
			return result = loads;
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
     * Updates an existing truck and its composition(trailer and container). Records the composition change in 
     * TRUCK_TRAILER_HISTORY.
     */
    public TruckEntity updateTruckComposition(TruckEntity truck, TrailerEntity trailer) {
		// [[
		TruckEntity result = null;
		Object[][] params = {{"truck", truck},
							{"trailer", trailer}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateTruckComposition", params);
		}
		// ]]
		try {
			
            ReadWriteDao<TruckEntity> dao = getDaoProvider().getTruckEntityDao();
            truck = dao.merge(truck);
			
            if (trailer != null ) {
            	ReadWriteDao<TrailerEntity> daoT = getDaoProvider().getTrailerEntityDao();
            	trailer.setCurrentTruck(truck);
            	trailer = daoT.merge(trailer);
                storeTruckTrailerHistory(trailer);
            }
            
            return result = truck;
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
	 * Returns the active loads for the truck. (Active means loaded, but not yet unloaded).
	 * @param truck
	 * @return
	 */
	public List getLoadsOfTruck(TruckEntity truck) {
		// [[
		List result = null;
		Object[][] params = {{"truck", truck}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLoadsOfTruck", params);
		}
		// ]]
		try {
			ReadWriteDao<LoadEntity> dao = getDaoProvider().getLoadEntityDao();
			Filter unloadedFilter = new FilterImpl();
			unloadedFilter.addExpression(Restrictions.eq(LoadEntity.ATTR_TRUCK_ID, truck.getTruckId() ));
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
	 * Creates a new truck, and saves its fields. 
	 */
	public TruckEntity createTruck(TruckEntity truck, long[] selectedPoolIds) {
		// [[
		TruckEntity result = null;
		Object[][] params = {{"truck", truck},
							{"selectedPoolIds", selectedPoolIds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createTruck", params);
		}
		// ]]
		try {
			RfidobjectEntity rfidObject = truck.getRfidobject();
			AbstractRFIDBean bean = RFIDBean.TruckRFIDBean(truck);
			try {
				rfidObject = rfidService.validateAndPersistRFIDObject(Constant.RFIDOBJECT_TYPE_TRUCK, bean);
			}
			catch (LogonException logonException) {
				LOG.error("Could not create truck, because it's RFID object has the same {label, type code} as another one in the DB");
				Long truckId = truck.getTruckId();
				String truckName = truck.getTruckName();
				String truckPlateNumber = truck.getTruckPlateNo();
				LOG.error("Truck id: " + truckId + ", truck name: " + truckName
						+ ", truck plate number: " + truckPlateNumber);
				throw logonException;
			}
			truck.setRfidobject(rfidObject);
			
			ReadWriteDao<TruckEntity> dao = getDaoProvider().getTruckEntityDao();
			truck = dao.persist(truck);
			
			updateSelectedPools(selectedPoolIds, truck);
			
			return result = truck;
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
	 * Creates an entry for the truck+trailer assignment in truck_trailer_history.
	 * @param truck
	 */
	private void storeTruckTrailerHistory(TrailerEntity trailer) {
		// [[
		Object[][] params = {{"trailer", trailer}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "storeTruckTrailerHistory", params);
		}
		// ]]
		try {
			ReadWriteDao<TruckTrailerHistoryEntity> dao = getDaoProvider().getTruckTrailerHistoryEntityDao();
			TruckTrailerHistoryEntity entity = new TruckTrailerHistoryEntity();
			entity.setTruck(trailer.getCurrentTruck());
			entity.setTrailer(trailer);
			entity.setAssignmentTs(new Date());
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
	 * Creates an entry for the truck+trailer assignment in truck_trailer_history.
	 * @param truck
	 */
	private void storeTruckContainerHistory(ContainerEntity container, TruckEntity truck, TrailerEntity trailer ) {
		// [[
		Object[][] params = {{"container", container},
							{"truck", truck},
							{"trailer", trailer}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "storeTruckContainerHistory", params);
		}
		// ]]
		try {
			ReadWriteDao<ContainerHistoryEntity> dao = getDaoProvider().getContainerHistoryEntityDao();
			ContainerHistoryEntity entity = new ContainerHistoryEntity();
			entity.setTruck(truck);
			entity.setTrailer(trailer);
			entity.setContainer(container);
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
	 * Get last position of truck according to EVENT_TIMESTAMP. The truck's last position is defined so.
	 * 
	 * @param truckId
	 * @return double []
	 */
	public Double[] fetchLastPosition( long truckId) {
			// [[
			Double[] result = null;
			Object[][] params = {{"truckId", truckId + ""}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "fetchLastPosition", params);
			}
			// ]]
			try {
				Double [] coordinates = new Double [2];
				ReadWriteDao<TruckPositionEntity> truckPositionDao = getDaoProvider().getTruckPositionEntityDao();
				
				Filter filter = new FilterImpl();
				Map queryParams = new HashMap();
				Expression query = Restrictions.jpaqlExpression("x.eventTs = any(SELECT MAX(m.eventTs) FROM TruckPositionEntity as m WHERE m.truck.truckId = x.truck.truckId )", queryParams);
				filter.addExpression(query);
				filter.addExpression(Restrictions.eq(TruckPositionEntity.ATTR_TRUCK_ID, truckId));
				
				List<TruckPositionEntity> found = truckPositionDao.findNRecs(0, 1, filter);
				if (found != null && !found.isEmpty() ){
					TruckPositionEntity truckPosition = found.get(0);
					coordinates[0] = truckPosition.getCoordLat();
					coordinates[1] = truckPosition.getCoordLon();
				}
						
				return result = coordinates;
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
	 * Get last position of truck according to EVENT_TIMESTAMP. The truck's last position is defined so.
	 * 
	 * FIXME: test if this is faster than public Double[] fetchLastPosition( long truckId) {. if no remove fetchLastPositionList and TruckLastKnownCoordEntity
	 * @param List truckIdList
	 * @return List
	 */
	public List fetchLastPositionList(List truckIdList) {
		// [[
		List result = null;
		Object[][] params = {{"truckIdList", truckIdList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchLastPositionList", params);
		}
		// ]]
		try {
			ReadOnlyDao<TruckLastKnownCoordEntity> dao = getDaoProvider().getTruckLastKnownCoordEntityDao();
			Filter filter = new FilterImpl();
			if(truckIdList == null || truckIdList.isEmpty()){
				return result = new ArrayList();
			}
			filter.addExpression(Restrictions.in(TruckLastKnownCoordEntity.ATTR_TRUCK_ID, truckIdList));
			List found = dao.find(filter);			
			return result = found;
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
	 * Loads all available trailers for the carrier, which are yet available. If truckId is given the trailer
	 * which is assigned to this truck is also returned included in the list. Pass truckId null if ONLY all available trucks should
	 * be returned.
	 * @param carrierId
	 * @param truckId
	 * @return
	 */
	public List fetchAvailableTrailers(Long carrierId, Long truckId) {
		// [[
		List result = null;
		Object[][] params = {{"carrierId", carrierId},
							{"truckId", truckId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAvailableTrailers", params);
		}
		// ]]
		try {
			ReadWriteDao<TrailerEntity> trailerDao = getDaoProvider().getTrailerEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_CARRIER_ID, carrierId));
			filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_ACTIVE_FLAG, true));
			//only those trailers have to be returned, which are not assigned to any truck, or the one which is ALREADY ASSIGNED to this truck
			
			if (truckId != null){				
				filter.addExpression(Restrictions.or(Restrictions.isNull(TrailerEntity.ATTR_CURRENT_TRUCK_ID), Restrictions.eq(TrailerEntity.ATTR_CURRENT_TRUCK_ID, truckId)));
				
				//Filter for characteristic of truck
				ReadWriteDao<TruckEntity> truckEntityDao = getDaoProvider().getTruckEntityDao();
				TruckEntity truck = truckEntityDao.findById(truckId);
				filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_SAP_VEHICLE_CHAR_CD, truck.getSapVehicleChar().getSapVehicleCharCd()));
			}
			else{
				filter.addExpression(Restrictions.isNull(TrailerEntity.ATTR_CURRENT_TRUCK_ID));
			}
			filter.addExpression(Order.asc(TrailerEntity.ATTR_TRAILER_NAME));
			List list = trailerDao.find(filter);
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
	
	/**
	 * Loads all available containers, which are yet available. If truckId is given the container
	 * which is assigned to this truck is also returned included in the list. Pass truckId null if ONLY all available containers should
	 * be returned.
	 * @param truckId
	 * @return
	 */
	public List fetchAvailableContainers(Long truckId, Long trailerId) {
		// [[
		List result = null;
		Object[][] params = {{"truckId", truckId},
							{"trailerId", trailerId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAvailableContainers", params);
		}
		// ]]
		try {
			ReadWriteDao<ContainerEntity> containerDao = getDaoProvider().getContainerEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_ACTIVE_FLAG, true));
			//only those containers have to be returned, which are not assigned to any truck, or the one which is ALREADY ASSIGNED to this truck
			if (truckId != null){				
				filter.addExpression(Restrictions.or(
						Restrictions.and(
								Restrictions.isNull(ContainerEntity.ATTR_CURRENT_TRAILER_ID),
								Restrictions.isNull(ContainerEntity.ATTR_CURRENT_TRUCK_ID)),
						Restrictions.and(Restrictions.eq(ContainerEntity.ATTR_CURRENT_TRUCK_ID, truckId), Restrictions.isNull(ContainerEntity.ATTR_CURRENT_TRAILER_ID))));
			}
			else if(trailerId != null) {
				filter.addExpression(Restrictions.or(
								Restrictions.and(Restrictions.isNull(ContainerEntity.ATTR_CURRENT_TRAILER_ID),
								Restrictions.isNull(ContainerEntity.ATTR_CURRENT_TRAILER_ID)),
						Restrictions.eq(ContainerEntity.ATTR_CURRENT_TRAILER_ID, trailerId)));				
			}
			else{
				filter.addExpression(Restrictions.isNull(ContainerEntity.ATTR_CURRENT_TRUCK_ID));
				filter.addExpression(Restrictions.isNull(ContainerEntity.ATTR_CURRENT_TRAILER_ID));
			}
			filter.addExpression(Order.asc(ContainerEntity.ATTR_CONTAINER_NAME));
			List list = containerDao.find(filter);
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
	
	/**
	 * Fetches all selectable sap trucks for the create truck popup's SapVehicleNo dropdown.
	 * @param carrierId selected carrier
	 * @param skip find parameter
	 * @param num find parameter
	 * @param query input text
	 * @return
	 */
	public List<SapVehicleEntity> fetchGenericSapTrucksOfCarrier(Long carrierId, int skip, int num, String query) {
		// [[
		List result = null;
		Object[][] params = {{"carrierId", carrierId},
							{"skip", skip + ""},
							{"num", num + ""},
							{"query", query}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchGenericSapTrucksOfCarrier", params);
		}
		// ]]
		try {
			//SAP Vehicle Number | Sap Vehicle type | Sap Vehicle Desc
			
			//1. Get the vendor
			
			ReadWriteDao<CarrierEntity> carrierEntityDao = getDaoProvider().getCarrierEntityDao();			
			CarrierEntity carrier = carrierEntityDao.findById(carrierId);
			
			//2.) Get the unique set of sapVehicleTypeCd's for any matching description string
						
			FilterImpl txFilter = new FilterImpl();
			txFilter.addExpression(Restrictions.like(SapVehicleTypeTxEntity.ATTR_TXT, query + "%"));
			
			List<SapVehicleTypeTxEntity> sapVehicleTypeTxList = referentialDataService.findTxEntityFiltered(SapVehicleTypeTxEntity.class, txFilter);
			
			List<String> txIds = new ArrayList<String>();
			
			txIds.add("");
			
			for (SapVehicleTypeTxEntity sapVehicleTypeTxEntity : sapVehicleTypeTxList) {
				String sapVehicleTypeCd = sapVehicleTypeTxEntity.getSapVehicleTypeTxPK().getSapVehicleTypeBs().getSapVehicleTypeCd();
				if (!txIds.contains(sapVehicleTypeCd)) {
					txIds.add(sapVehicleTypeCd);
				}
			}
			
			txIds.add("");
			
			//3. Match the complete query
			
			ReadWriteDao<SapVehicleEntity> dao = getDaoProvider().getSapVehicleEntityDao();
			
			Filter<SapVehicleEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(SapVehicleEntity.ATTR_SAP_VEHICLE_ACTIVE_FLAG, Boolean.TRUE));
			filter.addExpression(Restrictions.eq(SapVehicleEntity.ATTR_SAP_VENDOR, carrier.getSapVendor()));
			
			filter.addExpression(Restrictions.eq(SapVehicleEntity.ATTR_SAP_VEHICLE_TYPE + "." + SapVehicleTypeBsEntity.ATTR_LOGON_VEHICLE_TYPE_CD, Constant.VEHICLE_LOGON_TYPE_TRUCK));
			filter.addExpression(Restrictions.eq(SapVehicleEntity.ATTR_SAP_VEHICLE_CHAR + "." + SapVehicleCharBsEntity.ATTR_SAP_VEHICLE_CHAR_CD ,Constant.VEHICLE_CHARACTERISTIC_GENERIC));
			
			Expression inputRestriction = null;
			
			if (0 < txIds.size()) {
				inputRestriction = Restrictions.or(
					Restrictions.like(SapVehicleEntity.ATTR_SAP_VEHICLE_NO, query + "%"),
					Restrictions.like(SapVehicleEntity.ATTR_SAP_VEHICLE_TYPE_CD, query + "%"),
					Restrictions.in(SapVehicleEntity.ATTR_SAP_VEHICLE_TYPE_CD, txIds)
				);
			}
			else {
				inputRestriction = Restrictions.or(
					Restrictions.like(SapVehicleEntity.ATTR_SAP_VEHICLE_NO, query + "%"),
					Restrictions.like(SapVehicleEntity.ATTR_SAP_VEHICLE_TYPE_CD, query + "%")
				);		
			}
			
			filter.addExpression(inputRestriction);
			
			List<SapVehicleEntity> trucks = dao.findNRecs(skip, num, filter);
			return result = trucks;
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
	 * Returns the trailer for the truck (if exists);
	 * @param truckId
	 * @return
	 */
	public TrailerEntity fetchTrailerForTruck(Long truckId, boolean onlyActive) {
		// [[
		TrailerEntity result = null;
		Object[][] params = {{"truckId", truckId},
							{"onlyActive", onlyActive + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchTrailerForTruck", params);
		}
		// ]]
		try {
			TrailerEntity trailer = null;
			ReadWriteDao<TrailerEntity> trailerDao = getDaoProvider().getTrailerEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_CURRENT_TRUCK_ID, truckId));
			if (onlyActive) {				
				filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			}
			trailer = trailerDao.findSingle(filter);
			return result = trailer;
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
	 * Returns the container for the truck (if exists). WARNING! Only returns the container if it is directly attached
	 * to the truck, that means it it not related to a trailer, but only to this truck! 
	 * @param truckId
	 * @param onlyActive TODO
	 * @return
	 */
	public ContainerEntity fetchContainerForTruck(Long truckId, boolean onlyActive) {
		// [[
		ContainerEntity result = null;
		Object[][] params = {{"truckId", truckId},
							{"onlyActive", onlyActive + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchContainerForTruck", params);
		}
		// ]]
		try {
			ContainerEntity container = null;
			ReadWriteDao<ContainerEntity> trailerDao = getDaoProvider().getContainerEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_TRUCK_ID, truckId));
			//only those count here, which are directly attached to the truck, and not those, which are attached to the trailer
			filter.addExpression(Restrictions.isNull(ContainerEntity.ATTR_CURRENT_TRAILER_ID));
			if (onlyActive) {				
				filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			}
			container = trailerDao.findSingle(filter);
			return result = container;
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
	 * Query for certain parameters of truck. Used for deterimining id the truck is unique.
	 * Returns true if there is another truck with the parameters provided. Returns false in case
	 * the truck is still saveable, and will not interfere with unique constraints.
	 * @param truckName
	 * @param plateNo
	 * @return
	 */
	public boolean findTruckWithUniqueParameters(Long truckId, String truckName, String plateNo){
		// [[
		boolean result = false;
		Object[][] params = {{"truckId", truckId},
							{"truckName", truckName},
							{"plateNo", plateNo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "findTruckWithUniqueParameters", params);
		}
		// ]]
		try {
			
			Filter filt = new FilterImpl();
			filt.addExpression(Restrictions.or(
					Restrictions.ilike(TruckEntity.ATTR_TRUCK_NAME, truckName),
					Restrictions.ilike(TruckEntity.ATTR_TRUCK_PLATE_NO, plateNo)
				));
			
			if (truckId != null) {				
				filt.addExpression(Restrictions.neq(TruckEntity.ATTR_TRUCK_ID, truckId));
			}
			List trucksWithParameter = getDaoProvider().getTruckEntityDao().find(filt);
			if (trucksWithParameter != null && !trucksWithParameter.isEmpty()) {
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
	
	
	public BigDecimal getTareWeightTComposition(TruckEntity truck) {
		
		// TARA
		BigDecimal decimal = truck.getTareWeightT();
		
		//TRAILER
		TrailerEntity trailer = fetchTrailerForTruck(truck.getTruckId(), false);
		if (trailer != null) {
			if (trailer.getTareWeightT() != null) {
				BigDecimal trailerTaraWeightT = trailer.getTareWeightT();
				if (decimal != null){						
					BigDecimal truckTrailerMeasure = decimal.add(trailerTaraWeightT);
					decimal = truckTrailerMeasure;
				}
				else{
					decimal = trailerTaraWeightT;
				}
			}
		}
	
		ContainerEntity currentContainer = fetchContainerForTruckTrailerComposition(truck, trailer);
		if ( currentContainer != null && currentContainer.getTareWeightT() != null){
			BigDecimal containerTaraWeightT = currentContainer.getTareWeightT();
			if (decimal != null){						
				BigDecimal truckTrailerContainerMeasure = decimal.add(containerTaraWeightT);
				decimal = truckTrailerContainerMeasure;
			}
			else{
				decimal = containerTaraWeightT;
			}
		}				
	
		return decimal;
	}
	
	
	public BigDecimal getTruckCompositionMaxNetWeightT(TruckEntity truck) {
    	
    	BigDecimal maxNetWeightT = null;
		BigDecimal tareWeightT = getTareWeightTComposition(truck);
		BigDecimal maxGrossWeightT = truck.getMaxGrossWeightT();
		TrailerEntity trailer = fetchTrailerForTruck(truck.getTruckId(), false);
		if (trailer != null) {
			if (trailer.getMaxGrossWeightT() != null) {
				maxGrossWeightT = trailer.getMaxGrossWeightT();
			}
		}
    	
		if ( (maxGrossWeightT != null) && (tareWeightT != null) ) {
			maxNetWeightT = maxGrossWeightT.subtract(tareWeightT);
		}
		// FIXME (emoke Apr 14, 2010): ask boon!
		ContainerEntity container = fetchContainerForTruckTrailerComposition(truck, fetchTrailerForTruck(truck.getTruckId(), false));
		if ( (container != null) && (container.getMaxNetWeightT() != null) ) {
			if ( (maxNetWeightT == null) || (maxNetWeightT.compareTo(container.getMaxNetWeightT()) > 0) ) {
				maxNetWeightT = container.getMaxNetWeightT();
			}
		}
		
		return maxNetWeightT;
    }
}
