/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.persistence.EntityExistsException;

import com.crh.logon.business.services.dispatchers.SapImportVehnPostprocessServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.VehnPostprocessException;
import com.crh.logon.utils.LogonException;
import org.apache.commons.lang.ArrayUtils;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.impl.cron.ScheduledTask;
import com.crh.logon.business.services.localinterfaces.SapImportVehnPostprocessServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TrailerAdminServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.PoolCarrierEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.sap.SapVehicleEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 * 
 * Responsible to convert SapVehicleEntity rows into TruckEntity or TrailerEntity. It is a required postprocessing 
 * after the "SAP Vehicle Import" PL/SQL package, runned with a CronJob.
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class SapImportVehnPostprocessServiceBean extends SapImportVehnPostprocessServiceDispatcher implements SapImportVehnPostprocessServiceLocalInterface{
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SapImportVehnPostprocessServiceBean.class);
	private final boolean DO_NOT_ALLOW_ACTIVE_ASSIGNMENTS = false;
	private final boolean ONLY_ACTIVE_FALSE = false;
	
	@EJB private SapImportVehnPostprocessServiceLocalInterface sapImportVehnPostprocessService; 
	@EJB private TrailerAdminServiceLocalInterface trailerAdminService;
	@EJB private RFIDServiceLocalInterface rfidService;
	@EJB private TruckAdminServiceLocalInterface truckAdminService;
	@EJB private ConfigServiceLocalInterface configService;
	
	/**
     * Main function for "Logon Truck/Trailer Create/Update Process" Sap Vehicle Import postprocessing
     */
	@ScheduledTask(name = "postprocess_sapimport_vehn", second = "0", minute = "*/5", hour = "*", configurationMethodName = "getSapimportVehnCronEnabled")	
	@TransactionAttribute(TransactionAttributeType.NEVER)
    public void updateVehicles(){
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateVehicles");
		}
		// ]]
		try {
			List<Long> sapVehicleIds = fetchNewSapVehicles();
			
			for (Long sapVehicleId : sapVehicleIds) {
				try {
					sapImportVehnPostprocessService._updateVehicle(sapVehicleId);
				}
				catch (LogonException e) {
					LOG.error("SAP vehicle postprocessing threw exception (sap_vehicle_id = " + sapVehicleId + "). " + e.getMessage());
				}
				catch (VehnPostprocessException e) {
					//This is currently unreachable, exception is packed into EJBException, see below
					LOG.error("SAP vehicle postprocessing threw exception (sap_vehicle_id = " + sapVehicleId + "). " + e.getMessage());
				}
				catch (EJBException e) {
					Exception causedBy = e.getCausedByException();
					if (causedBy instanceof VehnPostprocessException) {
						VehnPostprocessException vpe = (VehnPostprocessException) causedBy;
						LOG.error("SAP vehicle postprocessing threw exception (sap_vehicle_id = " + sapVehicleId + "). " + vpe.getMessage());
					}
					else {
						LOG.error("Unexpected EJBException.", e);
					}
				}
				catch (Exception e) {
					LOG.error("Unexpected error.", e);
				}
				finally {					
					sapImportVehnPostprocessService._saveVehiclePostprocessed(sapVehicleId);
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
     * Public function to process one sapVehicle
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void _updateVehicle(Long sapVehicleId) throws VehnPostprocessException {
		// [[
		Object[][] params = {{"sapVehicleId", sapVehicleId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "_updateVehicle", params);
		}
		// ]]
		try {
			ReadWriteDao<SapVehicleEntity> sapVehicleEntityDao = getDaoProvider().getSapVehicleEntityDao();
			SapVehicleEntity sapVehicle = sapVehicleEntityDao.findById(sapVehicleId);
			
			Long sapVehicleCharacteristicCd = sapVehicle.getSapVehicleChar().getSapVehicleCharCd();
			if (sapVehicleCharacteristicCd == Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL) {
				handleIndividualVehicle(sapVehicle);
			} 
			else if (sapVehicleCharacteristicCd == Constant.VEHICLE_CHARACTERISTIC_GENERIC) {
				handleGenericVehicle(sapVehicle);
			} 
			else {
				throw new VehnPostprocessException("sap_vehicle with sap_vehicle_id = " + sapVehicle.getSapVehicleId() + " has unknown characteristic", Reason.RES_ERROR_UNKNOWN_SAP_VEHICLE_CHARACTERISTIC);
			}
			
			//in case of successful import we prevent further imports by setting the isImported flag.
			
			sapVehicle.setSapVehicleIsImportedFlag(Boolean.TRUE);
			sapVehicleEntityDao.merge(sapVehicle);
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
     * Saves the processed status of the TLGNS_SAP_VEHICLE row.
     * @param sapVehicle
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void _saveVehiclePostprocessed(Long sapVehicleId) {	
		// [[
		Object[][] params = {{"sapVehicleId", sapVehicleId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "_saveVehiclePostprocessed", params);
		}
		// ]]
		try {
			ReadWriteDao<SapVehicleEntity> sapVehicleEntityDao = getDaoProvider().getSapVehicleEntityDao();
			SapVehicleEntity sapVehicle = sapVehicleEntityDao.findById(sapVehicleId);
			if (!sapVehicle.getSapVehicleIsProcessedFlag()) {
				sapVehicle.setSapVehicleIsProcessedFlag(Boolean.TRUE);
				sapVehicleEntityDao.merge(sapVehicle);
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
     * Fetches all unprocessed tlgns_sap_vehicle rows.
     */
    private List<Long> fetchNewSapVehicles(){
		// [[
    	List<Long> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchNewSapVehicles");
		}
		// ]]
		try {
			List<SapVehicleEntity> sapVehicles = null;
			
			ReadWriteDao<SapVehicleEntity> sapVehicleEntityDao = getDaoProvider().getSapVehicleEntityDao();
			Filter<SapVehicleEntity> filter = sapVehicleEntityDao.createFilter();
			filter.addExpression(Restrictions.eq(SapVehicleEntity.ATTR_SAP_VEHICLE_IS_IMPORTED_FLAG, Boolean.FALSE));
			filter.addExpression(Order.asc(SapVehicleEntity.ATTR_SAP_VEHICLE_ID));
			
			Calendar c = new GregorianCalendar();
			
			Integer dayLimit = getDayLimit();
			c.add(Calendar.DAY_OF_YEAR, -dayLimit);
			
			Expression processed = Restrictions.eq(SapVehicleEntity.ATTR_SAP_VEHICLE_IS_PROCESSED_FLAG, Boolean.TRUE);
			Expression notOld = Restrictions.ge(SapVehicleEntity.ATTR_CTL_MOD_TS, c.getTime());
			
			//was processed not longer then 5 days ago
			Expression notOldProcessed = Restrictions.and(processed, notOld);
			
			Expression notProcessed = Restrictions.eq(SapVehicleEntity.ATTR_SAP_VEHICLE_IS_PROCESSED_FLAG, Boolean.FALSE);
			
			filter.addExpression(Restrictions.or(notOldProcessed, notProcessed));
				
			sapVehicles = sapVehicleEntityDao.find(filter);
			
			List<Long> sapVehicleIds = new ArrayList<Long>(sapVehicles.size());
			
			for (SapVehicleEntity sapVehicleEntity : sapVehicles) {
				sapVehicleIds.add(sapVehicleEntity.getSapVehicleId());
			}
			
			return result = sapVehicleIds;
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
     * Handles the case of individual vehicle
     */
    private void handleIndividualVehicle(SapVehicleEntity sapVehicle) throws VehnPostprocessException{
		// [[
		Object[][] params = {{"sapVehicle", sapVehicle}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handleIndividualVehicle", params);
		}
		// ]]
		try {
			Long vehicleLogonTypeCd = sapVehicle.getSapVehicleType().getLogonVehicleType().getLogonVehicleTypeCd();
			
			if (vehicleLogonTypeCd == Constant.VEHICLE_LOGON_TYPE_TRAILER) {
				handleIndividualTrailer(sapVehicle);
			} 
			else if (vehicleLogonTypeCd == Constant.VEHICLE_LOGON_TYPE_TRUCK) {
				handleIndividualTruck(sapVehicle);
			}
			else {
				throw new VehnPostprocessException("sap_vehicle with sap_vehicle_id = " + sapVehicle.getSapVehicleId() + " is not a truck and neither a trailer.", Reason.RES_ERROR_UNKNOWN_SAP_VEHICLE_LOGON_TYPE);
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
     * Gets the truck based on sapVehicle property.
     * @param sapVehicleNo
     * @return null when truck does not exist
     */
	private TruckEntity getIndividualTruckBySapVehicleNo(String sapVehicleNo) {
		// [[
		TruckEntity result = null;
		Object[][] params = {{"sapVehicleNo", sapVehicleNo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getExistingIndividualTruck", params);
		}
		// ]]
		try {
			if (sapVehicleNo != null) {
				ReadWriteDao<TruckEntity> truckEntityDao = getDaoProvider().getTruckEntityDao();
				Filter<TruckEntity> filter = truckEntityDao.createFilter();
				
				filter.addExpression(Restrictions.eq(TruckEntity.ATTR_SAP_VEHICLE_NO, sapVehicleNo));
				filter.addExpression(Restrictions.eq(TruckEntity.ATTR_SAP_VEHICLE_CHAR_CD, Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL));
				return result = truckEntityDao.findSingle(filter);
			}
			else {
				return result = null;
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
	
	/**
	 * Gets the trailer based on sapVehicle property.
	 * @param sapVehicleNo
	 * @return null when trailer does not exist
	 */
	private TrailerEntity getIndividualTrailerBySapVehicleNo(String sapVehicleNo) {
		// [[
		TrailerEntity result = null;
		Object[][] params = {{"sapVehicleNo", sapVehicleNo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getExistingIndividualTrailer", params);
		}
		// ]]
		try {
			if (sapVehicleNo != null) {
				ReadWriteDao<TrailerEntity> trailerEntityDao = getDaoProvider().getTrailerEntityDao();
				Filter<TrailerEntity> filter = trailerEntityDao.createFilter();
				
				filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_SAP_VEHICLE_NO, sapVehicleNo));
				filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_SAP_VEHICLE_CHAR_CD, Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL));
				
				return result = trailerEntityDao.findSingle(filter);
			}
			else {
				return result = null;
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
    
	/**
	 * Creates or updates an individual truck.
	 * @param sapVehicleTruck
	 * @return true when successful
	 */
    private void handleIndividualTruck(SapVehicleEntity sapVehicleTruck) throws VehnPostprocessException {
		// [[
		Object[][] params = {{"sapVehicleTruck", sapVehicleTruck}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handleIndividualTruck", params);
		}
		// ]]
		try {
			
			TruckEntity truck = getIndividualTruckBySapVehicleNo(sapVehicleTruck.getSapVehicleNo());
			
			if (truck == null) {
				createIndividualTruck(sapVehicleTruck);
			}
			else {
				updateIndividualTruck(truck, sapVehicleTruck);
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
	 * Check for active assignments involving the truck's current load(s) and throw in case there is at least one.
	 * For trailer there is no need to check as we guarantee in the import that the trailer is either free, or
	 * is by the way attached to the same truck SAP sent.
	 * no such either.
	 * 
	 * @param truck
	 */
    private boolean checkActiveAssignment(TruckEntity truck) {
		// [[
		boolean result = false;
		Object[][] params = {{"truck", truck}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "truckHasAssignment", params);
		}
		// ]]
		try {
			List<LoadEntity> loads = truckAdminService.getLoadsOfTruck(truck);
			return result = truckAdminService.isActiveAssignmentPresent(loads);
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
	 * Creates a new truck.
	 * @param sapVehicle
	 * @return true when successful
	 */
	private void createIndividualTruck(SapVehicleEntity sapVehicle) throws VehnPostprocessException {
		// [[
		Object[][] params = {{"sapVehicle", sapVehicle}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createIndividualTruck", params);
		}
		// ]]
		try {
			ReadWriteDao<TruckEntity> truckEntityDao = getDaoProvider().getTruckEntityDao();    
			Filter<TruckEntity> truckFilter = truckEntityDao.createFilter();
			truckFilter.addExpression(Restrictions.eq(TruckEntity.ATTR_RFIDOBJECT + "." + RfidobjectEntity.ATTR_OBJECT_LABEL, sapVehicle.getSapVehiclePlateNo()));
			TruckEntity existingTruck = truckEntityDao.findSingle(truckFilter);
			
			if (existingTruck != null) {
				throw new VehnPostprocessException("A truck is already existing in 'TLGNC_TRUCK' with the same plate number (Plate number = " + sapVehicle.getSapVehiclePlateNo() + ", truck id = " + existingTruck.getTruckId() + ").", Reason.RES_ERROR_SAP_VEHICLE_PLATE_NO_INCOSISTENCY);	
			}
			
			TruckEntity truck = new TruckEntity();
						
			SapVendorEntity sapVendor = sapVehicle.getSapVendor();
			
			ReadWriteDao<CarrierEntity> carrierEntityDao = getDaoProvider().getCarrierEntityDao();
			Filter<CarrierEntity> carrierEntityFilter = carrierEntityDao.createFilter();
			carrierEntityFilter.addExpression(Restrictions.eq(CarrierEntity.ATTR_SAP_VENDOR_ID, sapVendor.getSapVendorId()));
			
			List<Long> poolIds = new ArrayList<Long>();
			
			ReadWriteDao<PoolCarrierEntity> poolCarrierEntityDao = getDaoProvider().getPoolCarrierEntityDao();
			
			CarrierEntity carrierEntity = carrierEntityDao.findSingle(carrierEntityFilter);
			
			String truckName = getVehicleName(carrierEntity.getDisplayName(), sapVehicle.getSapVehiclePlateNo(), (int)TruckEntity.DBSIZE_TRUCK_NAME);
			
			Filter<PoolCarrierEntity> poolCarrierEntityFilter = poolCarrierEntityDao.createFilter();
			poolCarrierEntityFilter.addExpression(Restrictions.eq(PoolCarrierEntity.ATTR_CARRIER_ID, carrierEntity.getCarrierId()));
			List<PoolCarrierEntity> poolCarrierEntityList = poolCarrierEntityDao.find(poolCarrierEntityFilter);
			
			for (PoolCarrierEntity poolCarrierEntity : poolCarrierEntityList) {
				poolIds.add(poolCarrierEntity.getPool().getPoolId());
			}
			
			truck = fillDefaultTruck(truck, sapVehicle);
			
			//Fill additional fields
			truck.setCarrier(carrierEntity);
			truck.setTruckName(truckName);
			truck.setTruckPlateNo(sapVehicle.getSapVehiclePlateNo());
			truck.setTareWeightT(sapVehicle.getSapVehcileTareWeightT());
						
			RfidobjectEntity newRfidobjectEntity = rfidService.newRfidobjectEntity(Constant.RFIDOBJECT_TYPE_TRUCK);
			truck.setRfidobject(newRfidobjectEntity);
			
			truckAdminService.createTruck(truck, ArrayUtils.toPrimitive(poolIds.toArray(new Long[0])));
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
	 * Creates an individual truck.
	 * @param sapVehicle
	 * @return
	 */
	private void updateIndividualTruck(TruckEntity truck, SapVehicleEntity sapVehicle) {
		// [[
		Object[][] params = {{"truck", truck},
							{"sapVehicle", sapVehicle}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateIndividualTruck", params);
		}
		// ]]
		try {
		
			SapVendorEntity sapVendor = sapVehicle.getSapVendor();
			
			ReadWriteDao<CarrierEntity> carrierEntityDao = getDaoProvider().getCarrierEntityDao();
			Filter<CarrierEntity> carrierEntityFilter = carrierEntityDao.createFilter();
			carrierEntityFilter.addExpression(Restrictions.eq(CarrierEntity.ATTR_SAP_VENDOR_ID, sapVendor.getSapVendorId()));
			CarrierEntity carrierEntity = carrierEntityDao.findSingle(carrierEntityFilter);
			
			String truckName = getVehicleName(carrierEntity.getDisplayName(), sapVehicle.getSapVehiclePlateNo(), (int)TruckEntity.DBSIZE_TRUCK_NAME);
			
			if (truck.getCarrier().getCarrierId() != carrierEntity.getCarrierId()) {
				// only update when carrierId is changing
				truck.setCarrier(carrierEntity);
				truck.setTruckName(truckName);
			}
			
			truck.setActiveFlag(sapVehicle.getSapVehicleActiveFlag());
			truck.setTruckPlateNo(sapVehicle.getSapVehiclePlateNo());
			truck.setTruckTransportCat(sapVehicle.getSapVehicleType().getTruckTransportCat());
			truck.setTruckType(sapVehicle.getSapVehicleType().getTruckType());
			truck.setAxisCount(sapVehicle.getSapVehicleAxisCount());
			truck.setMaxGrossWeightT(sapVehicle.getSapVehicleMaxGrossWeightT());
			truck.setTareWeightT(sapVehicle.getSapVehcileTareWeightT());
			truck.setVolumeM3(sapVehicle.getSapVehicleVolumeM3());
			truck.setSapVehicleIsInternalFlag(sapVehicle.getSapVehicleIsInternalFlag());
			truck.setSapVehicleType(sapVehicle.getSapVehicleType());

			refreshTruckComposition(truck);
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
	 * Simply does an update on the truck. Keeps the composition as-is, including 
	 * the trailer and the container it has. The point is to use the same update wht truck admin uses.
	 * 
	 * @param truck
	 * @param truckAdminService
	 */
	private void refreshTruckComposition(TruckEntity truck) {
		// [[
		Object[][] params = {{"truck", truck}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "refreshTruckComposition", params);
		}
		// ]]
		try {		
			//check if truck is used - in updateTruck it is only checked if there is a composition change, this here is stricter
			boolean truckHasActiveAssignment = checkActiveAssignment(truck);
			
			if (truckHasActiveAssignment) {
				LOG.error("Cannot change truck composition; truck's load has an active assignment (Truck plate number =" + truck.getTruckPlateNo() + ").");
			}
			else {
				//if there is a container and the trailer has it, it does not need to be sent in here as parameter, 
				//either of these below will be anyway null, but if it is a container
				//type truck there might be a container for the truck so we need to do this exactly like this here
				TrailerEntity trailerForTruck = truckAdminService.fetchTrailerForTruck(truck.getTruckId(), ONLY_ACTIVE_FALSE);
				ContainerEntity containerForTruck = truckAdminService.fetchContainerForTruck(truck.getTruckId(), ONLY_ACTIVE_FALSE);
				
				//use the normal merge function in order that later on if there is something to be checked there it will be checked too
				truckAdminService.updateTruck(truck, false, null, trailerForTruck, trailerForTruck, containerForTruck, containerForTruck, DO_NOT_ALLOW_ACTIVE_ASSIGNMENTS);
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
	 * Handles the case of individual trailer.
	 * @param sapVehicleTrailer
	 * @return true when successfully created/updated.
	 */
    private void handleIndividualTrailer(SapVehicleEntity sapVehicleTrailer) throws VehnPostprocessException {
		// [[
		Object[][] params = {{"sapVehicleTrailer", sapVehicleTrailer}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handleIndividualTrailer", params);
		}
		// ]]
		try {
			
			TrailerEntity trailer = getIndividualTrailerBySapVehicleNo(sapVehicleTrailer.getSapVehicleNo());
			TruckEntity truck = getIndividualTruckBySapVehicleNo(sapVehicleTrailer.getSapVehicleTruckEqpNo());

			if (truck == null && sapVehicleTrailer.getSapVehicleTruckEqpNo() != null) {
				//truck is not existing yet, wait for next attempt -> skip
				throw new VehnPostprocessException("Cannot create trailer as the incoming truck is not yet existing (sapVehicleTruckEqpNo = " + sapVehicleTrailer.getSapVehicleTruckEqpNo() + ").", Reason.RES_ERROR_TRAILER_NO_TRUCK);
			}
			
			if (trailer == null) {
				
				trailer = createIndividualTrailer(sapVehicleTrailer);
			}
			else {
				
				if (trailer.getCurrentTruck() != null) {
					if (truckAdminService.hasOpenLoad(trailer.getCurrentTruck())) {
						throw new VehnPostprocessException("Cannot update trailer (" +
								trailer.getTrailerName() + ", " +
								trailer.getTrailerPlateNo() + ") because its current truck has " +
								"open loads.",
								Reason.RES_ERROR_SAP_VEHICLE_PRECONDITIONS_NOT_MET);
					}
				}
				updateIndividualTrailer(sapVehicleTrailer, trailer);
			}
			
			TruckEntity curTruck = trailer.getCurrentTruck();
			// no update required (both null or both the same)
			if ((truck == null && curTruck == null) ||
					(truck != null && curTruck != null && curTruck.getTruckId().equals(truck.getTruckId()))) {
				return;
			}
			// detaching trailer
			if (truck == null || curTruck != null) {
				LOG.warn("Detaching trailer (" + trailer.getTrailerName() + ", " +
						trailer.getTrailerPlateNo() + ") in SAP import is not supported.");
				return;
			}
			TrailerEntity curTrailer = truckAdminService.fetchTrailerForTruck(truck.getTruckId(), false);
			// current truck has another trailer
			if (curTrailer != null) {
				LOG.warn("Detaching current trailer from target truck (" +
						truck.getTruckName() + ", " + truck.getTruckPlateNo() + ") in SAP " +
						"import is not supported.");
				return;
			}
			
			// in all other cases (update required, neither current truck nor trailer attached)
			try {
				truckAdminService.updateTruck(truck, trailer, null, DO_NOT_ALLOW_ACTIVE_ASSIGNMENTS);
			}
			catch (Throwable t) {
				LOG.warn("Unexpected exception during truck <-> trailer link update for " +
						"truck (" + truck.getTruckName() + ", " + truck.getTruckPlateNo() + ") " +
						"and trailer (" + trailer.getTrailerName() + ", " + trailer.getTrailerPlateNo() + "): " +
						t.getMessage());
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
     * Creates a new Individual trailer.
     * @param sapVehicleTrailer
     * @return true when successfully created
     */
	private TrailerEntity createIndividualTrailer(SapVehicleEntity sapVehicle) throws VehnPostprocessException {
		
		TrailerEntity trailer = new TrailerEntity();
		// [[
		TrailerEntity result = null;
		Object[][] params = {{"sapVehicle", sapVehicle}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createIndividualTrailer", params);
		}
		// ]]
		try {
			ReadWriteDao<TrailerEntity> truckEntityDao = getDaoProvider().getTrailerEntityDao();    
			Filter<TrailerEntity> truckFilter = truckEntityDao.createFilter();
			truckFilter.addExpression(Restrictions.eq(TrailerEntity.ATTR_RFIDOBJECT + "." + RfidobjectEntity.ATTR_OBJECT_LABEL, sapVehicle.getSapVehiclePlateNo()));
			TrailerEntity existingTrailer = truckEntityDao.findSingle(truckFilter);
			
			if (existingTrailer != null) {
				throw new VehnPostprocessException("A trailer is already existing in 'TLGNC_TRAILER' with the same plate number (Plate number = "
						+ sapVehicle.getSapVehiclePlateNo() + ", trailer id = " + existingTrailer.getTrailerId() + ").", Reason.RES_ERROR_SAP_VEHICLE_PLATE_NO_INCOSISTENCY);	
			}
			
			SapVendorEntity sapVendor = sapVehicle.getSapVendor();
			ReadWriteDao<CarrierEntity> carrierEntityDao = getDaoProvider().getCarrierEntityDao();
			Filter<CarrierEntity> carrierEntityFilter = carrierEntityDao.createFilter();
			carrierEntityFilter.addExpression(Restrictions.eq(CarrierEntity.ATTR_SAP_VENDOR_ID, sapVendor.getSapVendorId()));
			CarrierEntity carrierEntity = carrierEntityDao.findSingle(carrierEntityFilter);
			
			String trailerName = getVehicleName(carrierEntity.getDisplayName(), sapVehicle.getSapVehiclePlateNo(), (int)TrailerEntity.DBSIZE_TRAILER_NAME);
			
			
			//Fill default fields
			trailer = fillDefaultTrailer(trailer, sapVehicle);
			
			//Fill additional fields
			trailer.setCarrier(carrierEntity);
			trailer.setTrailerName(trailerName);
			trailer.setTrailerPlateNo(sapVehicle.getSapVehiclePlateNo());
			trailer.setTareWeightT(sapVehicle.getSapVehcileTareWeightT());			
						
			RfidobjectEntity newRfidobjectEntity = rfidService.newRfidobjectEntity(Constant.RFIDOBJECT_TYPE_TRAILER);
			trailer.setRfidobject(newRfidobjectEntity);
			
			try{
				return result = trailerAdminService.createTrailer(trailer);
			} catch (EntityExistsException e) {
				LOG.warn("A trailer with trailer name: " + trailer.getTrailerName() + " already exists.");
				return null;
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
	
	/**
	 * Updates the individual trailer.
	 * @param sapVehicleTrailer
	 * @param trailer
	 * @return true when successfully updated
	 */
	private void updateIndividualTrailer(SapVehicleEntity sapVehicleTrailer, TrailerEntity trailer) {
		// [[
		Object[][] params = {{"sapVehicleTrailer", sapVehicleTrailer},
							{"trailer", trailer}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateIndividualTrailer", params);
		}
		// ]]
		try {
			SapVendorEntity sapVendor = sapVehicleTrailer.getSapVendor();
			ReadWriteDao<CarrierEntity> carrierEntityDao = getDaoProvider().getCarrierEntityDao();
			Filter<CarrierEntity> carrierEntityFilter = carrierEntityDao.createFilter();
			carrierEntityFilter.addExpression(Restrictions.eq(CarrierEntity.ATTR_SAP_VENDOR_ID, sapVendor.getSapVendorId()));
			CarrierEntity carrierEntity = carrierEntityDao.findSingle(carrierEntityFilter);
			
			String trailerName = getVehicleName(carrierEntity.getDisplayName(), sapVehicleTrailer.getSapVehiclePlateNo(), (int)TrailerEntity.DBSIZE_TRAILER_NAME);
						
			long nrChambers = 0;
			
			if (isSiloType(sapVehicleTrailer)) {
				nrChambers = 1;
			}
			
			if (trailer.getCarrier().getCarrierId() != carrierEntity.getCarrierId()) {
				trailer.setCarrier(carrierEntity);
				trailer.setTrailerName(trailerName);
			}
			
			trailer.setActiveFlag(sapVehicleTrailer.getSapVehicleActiveFlag());
			trailer.setTrailerPlateNo(sapVehicleTrailer.getSapVehiclePlateNo());
			trailer.setTrailerTrspCat(sapVehicleTrailer.getSapVehicleType().getTrailerTrspCat());
			trailer.setTrailerType(sapVehicleTrailer.getSapVehicleType().getTrailerType());
			trailer.setMaxGrossWeightT(sapVehicleTrailer.getSapVehicleMaxGrossWeightT());
			trailer.setTareWeightT(sapVehicleTrailer.getSapVehcileTareWeightT());
			trailer.setAxisCount(sapVehicleTrailer.getSapVehicleAxisCount());
			trailer.setVolumeM3(sapVehicleTrailer.getSapVehicleVolumeM3());
			trailer.setNrChambers(nrChambers);
			trailer.setSapVehicleIsInternalFlag(sapVehicleTrailer.getSapVehicleIsInternalFlag());
			trailer.setSapVehicleType(sapVehicleTrailer.getSapVehicleType());
			
			//this is supposed to keep the container, but to use the same updaters as admin
			final ContainerEntity containerOfTrailer = trailerAdminService.fetchContainerForTrailer(trailer.getTrailerId(), ONLY_ACTIVE_FALSE);
			trailerAdminService.updateTrailer(trailer, containerOfTrailer, containerOfTrailer);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private String getVehicleName(String displayName, String plateNo, int dbSize) {
		
		String result = displayName;
		String suffix = plateNo != null ? " " + plateNo : "";
		int maxLengthDisplayName = dbSize - suffix.length();
		
		if (result.length() > maxLengthDisplayName) {
			result = displayName.substring(0, maxLengthDisplayName);
		}
		
		result = result + suffix;
		return result;
	}
	
	public boolean isSiloType(SapVehicleEntity sapVehicle) {
		// [[
		boolean result = false;
		Object[][] params = {{"sapVehicle", sapVehicle}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isSiloType", params);
		}
		// ]]
		try {
			Long vehicleLogonTypeCd = sapVehicle.getSapVehicleType().getLogonVehicleType().getLogonVehicleTypeCd();
			
			Long vehicleTransportCategory = null;
			
			if (vehicleLogonTypeCd == Constant.VEHICLE_LOGON_TYPE_TRUCK) {
				vehicleTransportCategory = sapVehicle.getSapVehicleType().getTruckTransportCat().getTruckTransportCatCd();
				return result = vehicleTransportCategory == Constant.TKATEGORIE_SILO;	
			}
			else {
				vehicleTransportCategory = sapVehicle.getSapVehicleType().getTrailerTrspCat().getTrailerTrspCatCd();
				return result = vehicleTransportCategory == Constant.TKATEGORIE_SILO;	
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
    
    /**
     * Updates the fields of the vehicle accordingly.
     * @param sapVehicle
     * @return true when successfully updated
     */
    private void handleGenericVehicle(SapVehicleEntity sapVehicle) throws VehnPostprocessException {
		// [[
		Object[][] params = {{"sapVehicle", sapVehicle}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handleGenericVehicle", params);
		}
		// ]]
		try {
			Long vehicleLogonTypeCd = sapVehicle.getSapVehicleType().getLogonVehicleType().getLogonVehicleTypeCd();
			
			if (vehicleLogonTypeCd == Constant.VEHICLE_LOGON_TYPE_TRAILER) {
				updateAssociatedGenericTrailers(sapVehicle);
			} 
			else if (vehicleLogonTypeCd == Constant.VEHICLE_LOGON_TYPE_TRUCK) {
				updateAssociatedGenericTrucks(sapVehicle);
			}
			else {
				throw new VehnPostprocessException("sap_vehicle with sap_vehicle_id = " + sapVehicle.getSapVehicleId() + " is not a truck and neither a trailer.", Reason.RES_ERROR_UNKNOWN_SAP_VEHICLE_LOGON_TYPE);
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
     * Updates the fields of the truck accordingly.
     * @param sapVehicle
     * @param currentSapVehicleNo
     * @return true when successfully updated
     */
    private void updateAssociatedGenericTrucks(SapVehicleEntity sapVehicle) {
		// [[
		Object[][] params = {{"sapVehicle", sapVehicle}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateAssociatedGenericTrucks", params);
		}
		// ]]
		try {
			ReadWriteDao<TruckEntity> truckEntityDao = getDaoProvider().getTruckEntityDao();
			Filter<TruckEntity> filter = truckEntityDao.createFilter();
			filter.addExpression(Restrictions.eq(TruckEntity.ATTR_SAP_VEHICLE_NO, sapVehicle.getSapVehicleNo()));
			filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_SAP_VEHICLE_CHAR_CD, Constant.VEHICLE_CHARACTERISTIC_GENERIC));
			
			List<TruckEntity> associatedTrucks = truckEntityDao.find(filter);
			
			for (TruckEntity truck : associatedTrucks) {
				truck.setActiveFlag(sapVehicle.getSapVehicleActiveFlag());
				truck.setSapVehicleIsInternalFlag(sapVehicle.getSapVehicleIsInternalFlag());
				truck.setSapVehicleType(sapVehicle.getSapVehicleType());
				truck = truckEntityDao.merge(truck);
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
     * Updates the fields of the trailer accordingly.
     * @param sapVehicle
     * @param currentSapVehicleNo
     * @return true when successfully updated
     */
    private void updateAssociatedGenericTrailers(SapVehicleEntity sapVehicle) {
		// [[
		Object[][] params = {{"sapVehicle", sapVehicle}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateAssociatedGenericTrailers", params);
		}
		// ]]
		try {
			ReadWriteDao<TrailerEntity> TrailerEntityDao = getDaoProvider().getTrailerEntityDao();
			Filter<TrailerEntity> filter = TrailerEntityDao.createFilter();
			filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_SAP_VEHICLE_NO, sapVehicle.getSapVehicleNo()));
			filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_SAP_VEHICLE_CHAR_CD, Constant.VEHICLE_CHARACTERISTIC_GENERIC));
			
			List<TrailerEntity> associatedTrailers = TrailerEntityDao.find(filter);
			
			for (TrailerEntity trailer : associatedTrailers) {
				trailer.setActiveFlag(sapVehicle.getSapVehicleActiveFlag());
				trailer.setSapVehicleIsInternalFlag(sapVehicle.getSapVehicleIsInternalFlag());
				trailer.setSapVehicleType(sapVehicle.getSapVehicleType());
				trailer = TrailerEntityDao.merge(trailer);
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
	 * Fetches the related SapVehicle for truck or trailer creation.
	 * @param sapNo
	 * @return
	 */
	public SapVehicleEntity fetchSapVehicleBySapNo(String sapNo) {
		// [[
		SapVehicleEntity result = null;
		Object[][] params = {{"sapNo", sapNo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapVehicleBySapNo", params);
		}
		// ]]
		try {
			ReadWriteDao<SapVehicleEntity> dao = getDaoProvider().getSapVehicleEntityDao();
			Filter<SapVehicleEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(SapVehicleEntity.ATTR_SAP_VEHICLE_NO, sapNo));
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
	 * This method fills the always commonly filled field values for truck. Also being used in
	 * the 'create truck from UI' case.
	 * @param sapVehicle
	 * @return
	 */
	public TruckEntity fillDefaultTruck(TruckEntity truck, SapVehicleEntity sapVehicle) {
		// [[
		TruckEntity result = null;
		Object[][] params = {{"sapVehicle", sapVehicle}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fillDefaultTruck", params);
		}
		// ]]
		try {
			long nrChambers = 0;
			BigDecimal maxNetWeightChamber1 = Constant.VP_MAX_NET_WEIGHT_CHAMBER1_T_OTHER;
			
			if (isSiloType(sapVehicle)) {
				nrChambers = 1;
				maxNetWeightChamber1 = Constant.VP_MAX_NET_WEIGHT_CHAMBER1_T_SILO;
			}
			
			truck.setDispatchingCarrier(null);
			truck.setActiveFlag(sapVehicle.getSapVehicleActiveFlag());
			truck.setITrackAvailableFlag(Boolean.FALSE);
			truck.setSapVehicleNo(sapVehicle.getSapVehicleNo());
			truck.setTruckTransportCat(sapVehicle.getSapVehicleType().getTruckTransportCat());
			truck.setTruckType(sapVehicle.getSapVehicleType().getTruckType());
			truck.setManufacturer(null);
			truck.setAxisCount(sapVehicle.getSapVehicleAxisCount());
			truck.setEuroNorm(null);
			truck.setMaxGrossWeightT(sapVehicle.getSapVehicleMaxGrossWeightT());
			truck.setHasDustfilterFlag(null);
			truck.setVolumeM3(sapVehicle.getSapVehicleVolumeM3());
			truck.setBrandingFlag(null);
			truck.setNrChambers(nrChambers);
			truck.setMaxNetWeightChamber1T(maxNetWeightChamber1);
			truck.setMaxNetWeightChamber2T(Constant.VP_MAX_NET_WEIGHT_CHAMBER2);
			truck.setTruckComment(null);
			truck.setPdaAvailableFlag(Boolean.FALSE);
			truck.setHomebaseCoordLat(null);
			truck.setHomebaseCoordLon(null);
			truck.setSapVehicleIsInternalFlag(sapVehicle.getSapVehicleIsInternalFlag());
			truck.setSapVehicleChar(sapVehicle.getSapVehicleChar());
			truck.setSapVehicleType(sapVehicle.getSapVehicleType());
			
			if (truck.getPriority() == null) {
				truck.setPriority(Constant.TRUCK_PRIORITY_MIN);
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
	 * This method fills the always commonly filled field values for trailer. Also being used in
	 * the 'create trailer from UI' case.
	 * @param sapVehicle
	 * @return
	 */
	public TrailerEntity fillDefaultTrailer(TrailerEntity trailer, SapVehicleEntity sapVehicle) {
		// [[
		TrailerEntity result = null;
		Object[][] params = {{"sapVehicle", sapVehicle}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fillDefaultTruck", params);
		}
		// ]]
		try {
			long nrChambers = 0;
			BigDecimal maxNetWeightChamber1 = Constant.VP_MAX_NET_WEIGHT_CHAMBER1_T_OTHER;
			
			if (isSiloType(sapVehicle)) {
				nrChambers = 1;
				maxNetWeightChamber1 = Constant.VP_MAX_NET_WEIGHT_CHAMBER1_T_SILO;
			}
			
			trailer.setActiveFlag(sapVehicle.getSapVehicleActiveFlag());
			trailer.setSapVehicleNo(sapVehicle.getSapVehicleNo());
			trailer.setTrailerTrspCat(sapVehicle.getSapVehicleType().getTrailerTrspCat());
			trailer.setTrailerType(sapVehicle.getSapVehicleType().getTrailerType());
			trailer.setTrailerComment(null);
			trailer.setMaxGrossWeightT(sapVehicle.getSapVehicleMaxGrossWeightT());
			trailer.setManufacturer(null);
			trailer.setAxisCount(sapVehicle.getSapVehicleAxisCount());
			trailer.setVolumeM3(sapVehicle.getSapVehicleVolumeM3());
			trailer.setBrandingFlag(null);
			trailer.setNrChambers(nrChambers);
			trailer.setMaxNetWeightChamber1T(maxNetWeightChamber1);
			trailer.setMaxNetWeightChamber2T(Constant.VP_MAX_NET_WEIGHT_CHAMBER2);
			trailer.setSapVehicleIsInternalFlag(sapVehicle.getSapVehicleIsInternalFlag());
			trailer.setSapVehicleChar(sapVehicle.getSapVehicleChar());
			trailer.setSapVehicleType(sapVehicle.getSapVehicleType());
			
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
	 * Function to get the day limit for the processing service.
	 * @return
	 */
	private Integer getDayLimit() {
		// [[
		Integer result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getDayLimit");
		}
		// ]]
		try {
			ConfigBean cfgBean = configService.getConfigBean();
			return result = cfgBean.getPostprocessSapimportVehnCronJobDays();
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

