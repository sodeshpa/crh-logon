/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.factories;

import java.util.Calendar;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.ExtOrderEntity;
import com.crh.logon.persistence.entities.common.ExtSiteEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.AssignPartStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentPartTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.ShiptoTypeBsEntity;

public final class AssignmentPartFactory {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AssignmentPartFactory.class);
	private RefDataGenericsServiceLocalInterface referenceDataService;
	private AssignmentEntity assignment;
	
	public AssignmentPartFactory(AssignmentEntity assignment, RefDataGenericsServiceLocalInterface referenceDataService) {
		this.assignment = assignment;
		this.referenceDataService = referenceDataService;
	}

	/**
	 * It creates and initializes an assignmentPart with the related entities like assignmentPartType and assignmentPartStatus. 
	 * @param partType
	 * @param plannedStartTime
	 * @return AssignmentPartEntity
	 */
	private AssignmentPartEntity createAndInitAssignmentPart(Long partType, 
																Calendar plannedStartTime){
		// [[
		AssignmentPartEntity result = null;
		Object[][] params = {{"partType", partType},
							{"plannedStartTime", plannedStartTime}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createAndInitAssignmentPart", params);
		}
		// ]]
		try {
			
			final AssignPartStatusBsEntity assignmentPartStatus;
			final AssignmentPartTypeBsEntity assignmentPartType;
			assignmentPartStatus = referenceDataService.findBsEntity(AssignPartStatusBsEntity.class,
																		Constant.ASSIGN_PART_STATUS_NEW);
			assignmentPartType = referenceDataService.findBsEntity(AssignmentPartTypeBsEntity.class, 
																		partType);
			
			AssignmentPartEntity assignmentPart = new AssignmentPartEntity();
			assignmentPart.setAssignment(assignment);
			assignmentPart.setAssignPartStatus(assignmentPartStatus);
			assignmentPart.setAssignmentPartType(assignmentPartType);
			
			assignmentPart.setHomebaseFlag(Boolean.FALSE);
			assignmentPart.setPlannedStart(plannedStartTime.getTime());
			
			return result = assignmentPart;
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
	 * It creates a DRIVING_FROM assignmentPart. If the optionalSourceAssignmentPart is given then it
	 * will be used to initialize additional fields in the new assigmentPart. 
	 * @param plannedStartTime
	 * @param shipto
	 * @param optionalSourceAssignmentPart
	 * @return AssignmentPartEntity
	 */
	public AssignmentPartEntity createDrivingFrom(Calendar plannedStartTime,
														ShiptoEntity shipto,
														AssignmentPartEntity optionalSourceAssignmentPart) {
		// [[
		AssignmentPartEntity result = null;
		Object[][] params = {{"plannedStartTime", plannedStartTime},
							{"shipto", shipto},
							{"optionalSourceAssignmentPart", optionalSourceAssignmentPart}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createDrivingFrom", params);
		}
		// ]]
		try {
			AssignmentPartEntity assignmentPart = createAndInitAssignmentPart(Constant.ASSIGNMENT_PART_TYPE_FROM, 
																				plannedStartTime);
			assignmentPart.setShipto(shipto);
			
			if (optionalSourceAssignmentPart != null) {
				assignmentPart.setLnfShiptoFlag(optionalSourceAssignmentPart.getLnfShiptoFlag());
				
				assignmentPart.setSiteName(optionalSourceAssignmentPart.getSiteName());
				assignmentPart.setCoordLat(optionalSourceAssignmentPart.getCoordLat());
				assignmentPart.setCoordLon(optionalSourceAssignmentPart.getCoordLon());
				assignmentPart.setSiteCountry(optionalSourceAssignmentPart.getSiteCountry());
				assignmentPart.setSiteCity(optionalSourceAssignmentPart.getSiteCity());
				assignmentPart.setSitePostalCode(optionalSourceAssignmentPart.getSitePostalCode());
				assignmentPart.setSiteStreetHouseNo(optionalSourceAssignmentPart.getSiteStreetHouseNo());
			}
			
			return result = assignmentPart;
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
	 * It creates a DRIVING_FROM assignmentPart. If the optionalOrder is given then it
	 * will be used to initialize additional fields in the new assigmentPart. 
	 * @param plannedStartTime
	 * @param shipto
	 * @param order
	 * @return AssignmentPartEntity
	 */
	public AssignmentPartEntity createDrivingFromBasedOnOrder(Calendar plannedStartTime,
														ShiptoEntity shipto,
														OrderEntity order) {
		// [[
		AssignmentPartEntity result = null;
		Object[][] params = {{"plannedStartTime", plannedStartTime},
							{"shipto", shipto},
							{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createDrivingFromBasedOnOrder", params);
		}
		// ]]
		try {
			AssignmentPartEntity assignmentPart = createAndInitAssignmentPart(Constant.ASSIGNMENT_PART_TYPE_FROM, 
																				plannedStartTime);
			
			
			if (LogonUtil.isOrderWithReversedDirection(order) ){
				assignmentPart.setLnfShiptoFlag(Boolean.FALSE);
				assignmentPart.setShippingpoint(order.getShippingpoint());
			} 
			else {
				assignmentPart.setShipto(shipto);
				if (shipto != null) {
					ShiptoTypeBsEntity shiptoType = shipto.getShiptoType();
					Long shiptoTypeCd = shiptoType.getShiptoTypeCd();
					assignmentPart.setLnfShiptoFlag(shiptoTypeCd.equals(Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED));
				}
				else {
					assignmentPart.setLnfShiptoFlag(Boolean.FALSE);
				}
			}
			
			assignmentPart.setSiteName(order.getLnfSiteName());
			assignmentPart.setCoordLat(order.getLnfCoordLat());
			assignmentPart.setCoordLon(order.getLnfCoordLon());
			assignmentPart.setSiteCountry(order.getLnfSiteCountry());
			assignmentPart.setSiteCity(order.getLnfSiteCity());
			assignmentPart.setSitePostalCode(order.getLnfSitePostalCode());
			assignmentPart.setSiteStreetHouseNo(order.getLnfSiteStreetHouseNo());	
			
			
			return result = assignmentPart;
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
	 * It creates a LOADING assignmentPart. If the optionalExternalOrder is given then it will be used
	 * to initialize additional fields.
	 * @param plannedStartTime
	 * @param order
	 * @param optionalExternalOrder
	 * @return AssignmentPartEntity
	 */
	public AssignmentPartEntity createLoading(Calendar plannedStartTime, 
														OrderEntity order, 
														ExtOrderEntity optionalExternalOrder) {
		// [[
		AssignmentPartEntity result = null;
		Object[][] params = {{"plannedStartTime", plannedStartTime},
							{"order", order},
							{"optionalExternalOrder", optionalExternalOrder}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createLoading", params);
		}
		// ]]
		try {
			AssignmentPartEntity assignmentPart = createAndInitAssignmentPart(Constant.ASSIGNMENT_PART_TYPE_LOAD, 
																				plannedStartTime);
			assignmentPart.setLnfShiptoFlag(Boolean.FALSE);
			
			if (optionalExternalOrder != null) {
				final ExtSiteEntity loadSite = optionalExternalOrder.getLoadingExtSite();
				assignmentPart.setExtOrder(optionalExternalOrder);
				assignmentPart.setSiteCity(loadSite.getSiteCity());
				assignmentPart.setSiteContactPersonTxt(loadSite.getSiteContactPersonTxt());
				assignmentPart.setSiteCountry(loadSite.getSiteCountry());
				assignmentPart.setSiteLoadUnload(loadSite.getSiteLoadUnload());
				assignmentPart.setSiteName(loadSite.getSiteName());
				assignmentPart.setSitePostalCode(loadSite.getSitePostalCode());
				assignmentPart.setSiteStreetHouseNo(loadSite.getSiteStreetHouseNo());
				assignmentPart.setCoordLat(loadSite.getCoordLat());
				assignmentPart.setCoordLon(loadSite.getCoordLon());
			}
			assignmentPart.setOrder(null);
			
			return result = assignmentPart;
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
	 * It creates a DRIVING_TO assignmentPart.
	 * @param plannedStartTime
	 * @return AssignmentPartEntity
	 */
	public AssignmentPartEntity createDrivingTo(Calendar plannedStartTime) {
		// [[
		AssignmentPartEntity result = null;
		Object[][] params = {{"plannedStartTime", plannedStartTime}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createDrivingTo", params);
		}
		// ]]
		try {
			AssignmentPartEntity assignmentPart = createAndInitAssignmentPart(Constant.ASSIGNMENT_PART_TYPE_TO, 
																				plannedStartTime);
			assignmentPart.setLnfShiptoFlag(Boolean.FALSE);
			
			return result = assignmentPart;
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
	 * It creates an UNLOADING assignmentPart. If the order is given (not null) then it will be used 
	 * to initialize additional fields. The shipto will be used only if order is not null. Otherwise  
	 * if order is null and if externalOrder isn't then externalOrder will be used to initialize some fields.
	 * @param plannedStartTime
	 * @param order
	 * @param externalOrder
	 * @param shipto
	 * @return AssignmentPartEntity
	 */
	public AssignmentPartEntity createUnloading(Calendar plannedStartTime, 
													OrderEntity order, 
													ExtOrderEntity externalOrder, 
													ShiptoEntity shipto) {
		// [[
		AssignmentPartEntity result = null;
		Object[][] params = {{"plannedStartTime", plannedStartTime},
							{"order", order},
							{"externalOrder", externalOrder},
							{"shipto", shipto}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createUnloading", params);
		}
		// ]]
		try {
			AssignmentPartEntity assignmentPart = createAndInitAssignmentPart(Constant.ASSIGNMENT_PART_TYPE_UNLOAD, 
																				plannedStartTime);
			
			assignmentPart.setOrder(order);
			assignmentPart.setExtOrder(externalOrder);
			
			if (order != null) {
				if (LogonUtil.isOrderWithReversedDirection(order) ){
					assignmentPart.setShippingpoint(order.getShippingpoint());
					assignmentPart.setLnfShiptoFlag(Boolean.FALSE);
				} 
				else {
					assignmentPart.setShipto(shipto);				
					if (shipto != null) {
						ShiptoTypeBsEntity shiptoType = shipto.getShiptoType();
						Long shiptoTypeCd = shiptoType.getShiptoTypeCd();
						assignmentPart.setLnfShiptoFlag(shiptoTypeCd.equals(Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED));
					}
					else {
						assignmentPart.setLnfShiptoFlag(Boolean.FALSE);
					}
				}
				assignmentPart.setSiteName(order.getLnfSiteName());
				assignmentPart.setCoordLat(order.getLnfCoordLat());
				assignmentPart.setCoordLon(order.getLnfCoordLon());
				assignmentPart.setSiteCountry(order.getLnfSiteCountry());
				assignmentPart.setSiteCity(order.getLnfSiteCity());
				assignmentPart.setSitePostalCode(order.getLnfSitePostalCode());
				assignmentPart.setSiteStreetHouseNo(order.getLnfSiteStreetHouseNo());
			}
			else if (externalOrder != null) { // External order
				ExtSiteEntity unloadSite = externalOrder.getUnloadingExtSite();
				assignmentPart.setSiteCity(unloadSite.getSiteCity());
				assignmentPart.setSiteContactPersonTxt(unloadSite.getSiteContactPersonTxt());
				assignmentPart.setSiteCountry(unloadSite.getSiteCountry());
				assignmentPart.setSiteLoadUnload(unloadSite.getSiteLoadUnload());
				assignmentPart.setSiteName(unloadSite.getSiteName());
				assignmentPart.setSitePostalCode(unloadSite.getSitePostalCode());
				assignmentPart.setSiteStreetHouseNo(unloadSite.getSiteStreetHouseNo());
				assignmentPart.setCoordLat(unloadSite.getCoordLat());
				assignmentPart.setCoordLon(unloadSite.getCoordLon());
			}
			
			return result = assignmentPart;
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
	 * It creates a FINISHED assignmentPart. 
	 * @param plannedStartTime
	 * @return
	 */
	public AssignmentPartEntity createFinished(Calendar plannedStartTime) {
		// [[
		AssignmentPartEntity result = null;
		Object[][] params = {{"plannedStartTime", plannedStartTime}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createFinished", params);
		}
		// ]]
		try {
			AssignmentPartEntity assignmentPart = createAndInitAssignmentPart(Constant.ASSIGNMENT_PART_TYPE_FINISHED, plannedStartTime);
			assignmentPart.setLnfShiptoFlag(Boolean.FALSE);
			return result = assignmentPart;
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
	 * It creates a FINISHED assignmentPart and initialize additional fields regarding truck order creation -
	 * but just if the order is given (in this case the truck should be used).
	 * @param plannedStartTime
	 * @param truck
	 * @param order
	 * @param externalOrder
	 * @return AssignmentPartEntity
	 */
	public AssignmentPartEntity createFinishedForTruck(Calendar plannedStartTime,
																	TruckEntity truck,
																	OrderEntity order,
																	ExtOrderEntity externalOrder,
																	ShiptoEntity shipto) {
		// [[
		AssignmentPartEntity result = null;
		Object[][] params = {{"plannedStartTime", plannedStartTime},
							{"truck", truck},
							{"order", order},
							{"externalOrder", externalOrder}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createFinishedForTruck", params);
		}
		// ]]
		try {
			AssignmentPartEntity assignmentPart = createFinished(plannedStartTime);
			
			if (order != null) {
				if (!LogonUtil.isOrderWithReversedDirection(order) ){
					if (truck.getReturnShippingpoint() != null) {
						assignmentPart.setShippingpoint(truck.getReturnShippingpoint());
					}
					else {
						assignmentPart.setShippingpoint(order.getShippingpoint());
					}
				}
				else{
						assignmentPart.setShipto(shipto);
				}
			}
			else if (externalOrder != null) {
				ExtSiteEntity loadSite = externalOrder.getLoadingExtSite();
				assignmentPart.setSiteCity(loadSite.getSiteCity());
				assignmentPart.setSiteContactPersonTxt(loadSite.getSiteContactPersonTxt());
				assignmentPart.setSiteCountry(loadSite.getSiteCountry());
				assignmentPart.setSiteLoadUnload(loadSite.getSiteLoadUnload());
				assignmentPart.setSiteName(loadSite.getSiteName());
				assignmentPart.setSitePostalCode(loadSite.getSitePostalCode());
				assignmentPart.setSiteStreetHouseNo(loadSite.getSiteStreetHouseNo());
				assignmentPart.setCoordLat(loadSite.getCoordLat());
				assignmentPart.setCoordLon(loadSite.getCoordLon());			
			}
			
			return result = assignmentPart;
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
