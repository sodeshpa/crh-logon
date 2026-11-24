/*
 * Author            : AdNovum Informatik AG
 * Version Number    : \$Revision$
 * Date of last edit : \$Date$
 */

package com.crh.logon.business.services.beans;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.LdpointMatRestrEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity;
import com.crh.logon.persistence.entities.views.ShippBimodalRoadRailEntity;



public final class OrderChangeLogic {
	 
	public static final int FUNCTIONALITY_UPDATE_TARA_MEASURE_DATA = 0;
	public static final int FUNCTIONALITY_UPDATE_NETTO_MEASURE_DATA = 1;
	public static final int FUNCTIONALITY_UPDATE_LOADINGPOINT_DATA = 2;
	public static final int FUNCTIONALITY_TRANSMIT_TO_WEIGH_SYSTEM = 3;
	public static final int FUNCTIONALITY_CANCEL_WEIGH_SYSTEM = 5;
	public static final int FUNCTIONALITY_UPDATE_ORDER_ITEM_LOADED_QUANTITY= 6;
	public static final int FUNCTIONALITY_LOAD_CONTAINER = 7;
	public static final int FUNCTIONALITY_CANCEL_LOADING = 8;
	public static final int FUNCTIONALITY_LDS_INFORMATION = 9;
	public static final int FUNCTIONALITY_LOAD_AS_PLANNED = 10;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderChangeLogic.class);

	/**
	 * Containes the next planned start of loading for the certain
	 * assignment for truck id-s.
	 */
	private final Map<Long, Date> truckNextMinPlannedStartMap;
	/**
	 * Contains all available loading points for a certain order (needed because parallel button 
	 * must be disabled/enabled accordingly).
	 */
	private final Map<Long, List<LdpointMatRestrEntity>> orderLoadingPointListMapping;
	/**
	 * Contains bimodal informations for a certain order.
	 */
	private final Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfo;

	/**
	 * Creates a new instance of the OrderChangeLogic class to decide if functionalities should be 
	 * disabled or not.
	 * 
	 * See com.crh.logon.order.web.forms.auftrag.AuftragBearbeitungForm variables for the parameters.
	 * 
	 * @param truckNextMinPlannedStartMap Containes the next planned start of loading for the certain 
	 * assignment for truck id-s.
	 * @param orderLoadingPointListMapping Contains all available loading points for a certain order
	 * (needed because parallel button must be disabled/enabled accordingly).
	 * @param bimodalRoadAndRelatedRailInfo Contains bimodal informations for a certain order.
	 */
	public OrderChangeLogic(Map<Long, Date> truckNextMinPlannedStartMap, 
			Map<Long, List<LdpointMatRestrEntity>> orderLoadingPointListMapping,
			Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfo) {
		
		this.truckNextMinPlannedStartMap = truckNextMinPlannedStartMap;
		this.orderLoadingPointListMapping = orderLoadingPointListMapping;
		this.bimodalRoadAndRelatedRailInfo = bimodalRoadAndRelatedRailInfo;
	}

	/**
	 * Determines if a certain functionality is NOT ALLOWED on this order. Returns true, if the functionality is not allowed for
	 * the user, and returns false, if no need to disable.
	 * 
	 * @param ordItemAssignmtLoad OrdItemAssignmtLoadEntity
	 * @param functionality OrderChangeLogic FUNCTIONALITY... 
	 * @return
	 */
	public boolean disableSimpleChangeFunctionality(OrdItemAssignmtLoadEntity ordItemAssignmtLoad, int functionality){
			// [[
			boolean result = false;
			Object[][] params = {{"order", ordItemAssignmtLoad},
								{"functionality", functionality + ""}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "disableSimpleChangeFunctionality", params);
			}
			// ]]
			try {
				if(LogonUtil.isShipPickupOrder(ordItemAssignmtLoad.getDeliveryTypeCd()) || LogonUtil.isBlockTrainPickupOrder(ordItemAssignmtLoad.getDeliveryTypeCd())){
					return result = disableSimpleChangeFunctionalityForShipOrBlocktrainPickup(ordItemAssignmtLoad, functionality);
				}
				else {
					return result = disableSimpleChangeFunctionalityForOtherVehicles(ordItemAssignmtLoad, functionality);
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
	 * Determines if a certain functionality is NOT ALLOWED on this order. Returns true, if the functionality is not allowed for
	 * the user, and returns false, if no need to disable.
	 * 
	 * @param ordItemAssignmtLoad OrdItemAssignmtLoadEntity
	 * @param functionality OrderChangeLogic FUNCTIONALITY...
	 * @return
	 */
	private boolean disableSimpleChangeFunctionalityForOtherVehicles(OrdItemAssignmtLoadEntity ordItemAssignmtLoad,
			int functionality) {
		
		boolean result = false;
		Long truckId = null;
		Date nextMinPlannedStart = null;
		Date plannedStart = null;

		if (ordItemAssignmtLoad.getTruckId() != null && ordItemAssignmtLoad.getLoadingPlannedStart() != null) {
			//it's not guaranteed that the order is assigned to a truck, either not guaranteed that the planned start is filled in, but if
			//it is, it must be checked, if that is the next order to be dispatched
			truckId = ordItemAssignmtLoad.getTruckId();
			plannedStart = ordItemAssignmtLoad.getLoadingMinPlannedStart();
			nextMinPlannedStart= (Date) truckNextMinPlannedStartMap.get(truckId);
		}
		if (ordItemAssignmtLoad.getLogonPackageTypeCd() == null) {
			//in case the package type is not known, disable everything!
			return result = true;
		}
		
		Long orderStatusCd = ordItemAssignmtLoad.getOrderStatusCd();
		Boolean isLandfill = ordItemAssignmtLoad.getIsLandfill();
		Boolean isReverseOrder = (isLandfill 
									|| LogonUtil.isReturnDeliveredOrder(ordItemAssignmtLoad.getDeliveryTypeCd())) 
								&& LogonStatusUtils.isBeforeDelivered(orderStatusCd);
			
		switch (functionality){
			case FUNCTIONALITY_UPDATE_LOADINGPOINT_DATA:
				if ( ordItemAssignmtLoad.getTruckId() != null && LogonUtil.isPlanningTruck(ordItemAssignmtLoad.getSapVehicleCharCd()) ){
					return result = true;
				}
				if (!isReverseOrder && ordItemAssignmtLoad.getShipppointTypeCd().equals(Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK)) {
					return result = true;
				}
				result = checkBasePreConditionsForTrucks(ordItemAssignmtLoad, truckId, nextMinPlannedStart, plannedStart, isReverseOrder);
				if (ordItemAssignmtLoad.getShipppointTypeCd().equals(Long.valueOf(Constant.SHIPPINGPOINT_TYPE_FVL_BIMODAL_TRUCK)) 
						&& LogonUtil.isBimodalTruckPartOrder(ordItemAssignmtLoad.getDeliveryTypeCd())) {
					result = true;
				}				
				if (ordItemAssignmtLoad.getLoadOrderId() != null){
					return result = true;
				}
				
				break;
			case FUNCTIONALITY_LOAD_CONTAINER:
				if ( ordItemAssignmtLoad.getTruckId() != null && LogonUtil.isPlanningTruck(ordItemAssignmtLoad.getSapVehicleCharCd()) ){
					return result = true;
				}
				if (!isReverseOrder && !ordItemAssignmtLoad.getShipppointTypeCd().equals(Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK) ) {
					return result = true;
				}
				if (ordItemAssignmtLoad.getLoadingTs() != null) {
					//only ONCE!
					return result = true;
				}
				result = checkBasePreConditionsForTrucks(ordItemAssignmtLoad, truckId, nextMinPlannedStart, plannedStart, isReverseOrder);
				
				break;
			case FUNCTIONALITY_UPDATE_TARA_MEASURE_DATA:
				if ( ordItemAssignmtLoad.getTruckId() != null && LogonUtil.isPlanningTruck(ordItemAssignmtLoad.getSapVehicleCharCd()) ){
					return result = true;
				}
				if	(ordItemAssignmtLoad.getLoadNetMeasure() == null && ordItemAssignmtLoad.getLoadOrderId() != null){
					//in case it is parallel, disable it anyway
					return result = true;
				}
				if (!isReverseOrder && ordItemAssignmtLoad.getShipppointTypeCd().equals(Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK)) {
					return result = true;
				}				
				if (ordItemAssignmtLoad.getLogonPackageTypeCd().equals(Constant.PACKAGE_TYPE_BBAG)) {
					//if it is bag, disable
					return result = true;
				}
				if (Constant.SAP_MATERIAL_NO_AGG_PLANNING.equals(ordItemAssignmtLoad.getMaterialNo())
						|| Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING.equals(ordItemAssignmtLoad.getMaterialNo())) {
					return result = true;
				}
				result = checkBasePreConditionsForTrucks(ordItemAssignmtLoad, truckId, nextMinPlannedStart, plannedStart, isReverseOrder);
				if (ordItemAssignmtLoad.getShipppointTypeCd().equals(Long.valueOf(Constant.SHIPPINGPOINT_TYPE_FVL_BIMODAL_TRUCK)) && LogonUtil.isBimodalTruckPartOrder(ordItemAssignmtLoad.getDeliveryTypeCd())) {
					result = result || checkBimodalRoadRequirements(ordItemAssignmtLoad);
				}
				
				break;
			case FUNCTIONALITY_UPDATE_NETTO_MEASURE_DATA:
				if ( ordItemAssignmtLoad.getTruckId() != null && LogonUtil.isPlanningTruck(ordItemAssignmtLoad.getSapVehicleCharCd()) ){
					return result = true;
				}
				if ( ordItemAssignmtLoad.getLogonPackageTypeCd().equals(Constant.PACKAGE_TYPE_BBAG)) {
					//if it is bag, disable there order item loaded data is set!
					return result = true;
				}
				if (Constant.SAP_MATERIAL_NO_AGG_PLANNING.equals(ordItemAssignmtLoad.getMaterialNo())
						|| Constant.SAP_MATERIAL_NO_LANDFILL_PLANNING.equals(ordItemAssignmtLoad.getMaterialNo())) {
					return result = true;
				}
				if (!isReverseOrder && ordItemAssignmtLoad.getShipppointTypeCd().equals(Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK)) {
					return result = true;
				}
				result = checkBasePreConditionsForTrucks(ordItemAssignmtLoad, truckId, nextMinPlannedStart, plannedStart, isReverseOrder);
				if (ordItemAssignmtLoad.getShipppointTypeCd().equals(Long.valueOf(Constant.SHIPPINGPOINT_TYPE_FVL_BIMODAL_TRUCK)) && LogonUtil.isBimodalTruckPartOrder(ordItemAssignmtLoad.getDeliveryTypeCd())) {
					result = result || checkBimodalRoadRequirements(ordItemAssignmtLoad);
				}
				if (ordItemAssignmtLoad.getLoadNetMeasure() == null && ordItemAssignmtLoad.getLoadOrderId() != null) {
					return result = true;
				}
				
				break;
			case FUNCTIONALITY_UPDATE_ORDER_ITEM_LOADED_QUANTITY:
				if ( ordItemAssignmtLoad.getTruckId() != null && LogonUtil.isPlanningTruck(ordItemAssignmtLoad.getSapVehicleCharCd()) ){
					return result = true;
				}
				if ( LogonPackageHandlingUtil.isBulk(ordItemAssignmtLoad.getLogonPackageTypeCd())) {
					//if it is bulk, disable there netto measure is set!
					return result = true;
				}
				if (!isReverseOrder && ordItemAssignmtLoad.getShipppointTypeCd().equals(Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK)) {
					return result = true;
				}
				result = checkBasePreConditionsForTrucks(ordItemAssignmtLoad, truckId, nextMinPlannedStart, plannedStart, isReverseOrder);
				if (ordItemAssignmtLoad.getShipppointTypeCd().equals(Long.valueOf(Constant.SHIPPINGPOINT_TYPE_FVL_BIMODAL_TRUCK)) && LogonUtil.isBimodalTruckPartOrder(ordItemAssignmtLoad.getDeliveryTypeCd())) {
					result = result || checkBimodalRoadRequirements(ordItemAssignmtLoad);
				}
				if (ordItemAssignmtLoad.getLoadNetMeasure() == null && ordItemAssignmtLoad.getLoadOrderId() != null) {
					return result = true;
				}
				
				break;
			case FUNCTIONALITY_TRANSMIT_TO_WEIGH_SYSTEM:
				
				Long weighSystemLpCd = ordItemAssignmtLoad.getWeighSystemLpCd();
				boolean pushModeOn = ordItemAssignmtLoad.getPushModeOn();
				
				LoadingpointEntity lp = getLoadingpointFromMap(ordItemAssignmtLoad);
				if (lp != null) {
					weighSystemLpCd = lp.getWeighSystemLp().getWeighSystemLpCd();
					pushModeOn = lp.getShippingpoint().getWeighSystemSp().getPushModeOn();
				}
				
				// Disable in case of 'manual LOGON' or 'manual PDA'
				if (weighSystemLpCd != null && !weighSystemLpCd.equals(Constant.WEIGH_SYSTEM_LP_WS)) {
					return result = true;
				}
				
				// Disable based on package type
				if (!pushModeOn) {
					return result = true;
				}
				
				// FIXME 26-Jul-2012/stich: Uncomment if required. As currently implemented, an existing loadOrderId would result in a MessageFunction.CHANGE
				if (ordItemAssignmtLoad.getLoadOrderId() != null) {
					return result = true;
				}
				
				if (!isReverseOrder && ordItemAssignmtLoad.getShipppointTypeCd().equals(Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK)) {
					return result = true;
				}
				result = checkBasePreConditionsForTrucks(ordItemAssignmtLoad, truckId, nextMinPlannedStart, plannedStart, isReverseOrder);
				if ( ordItemAssignmtLoad.getLoadId() != null && ordItemAssignmtLoad.getUnloadingTs() != null){
					//in fact this can not happen because to be the truck's next order it has to be null unloading_ts
					//in case it is not in the correct order status disable
					return result = true;					
				}
				if (ordItemAssignmtLoad.getOrderStatusCd() <= Constant.ORDER_STATUS_DISPATCHED){
					return result = true;
				}
				
				break;
//			case FUNCTIONALITY_SET_UOM:
//				result1 = checkBasePreConditions(order, truckId, nextMinPlannedStart, plannedStart);
//				if (order.getLoadNetMeasure() == null && order.getLoadOrderId() != null) {
//					return result1 = true;
//				}				
//				if (order.getLogonPackageTypeCd().equals(Constant.PACKAGE_TYPE_BULK)) {
//					return result1 = true;
//				}
//				break;
			case FUNCTIONALITY_CANCEL_WEIGH_SYSTEM:
				
				// Decision in LOGON-6329: cancel IS possible in the PULL mode (simply deletes the load order id)
				
				result = checkBasePreConditionsForTrucks(ordItemAssignmtLoad, truckId, nextMinPlannedStart, plannedStart, isReverseOrder);

				if ( ordItemAssignmtLoad.getLoadId() != null && ordItemAssignmtLoad.getUnloadingTs() != null){
					//in case it is not in the correct order status disable
					return result = true;					
				}
				if (ordItemAssignmtLoad.getOrderStatusCd() <= Constant.ORDER_STATUS_DISPATCHED){
					return result = true;
				}

				weighSystemLpCd = ordItemAssignmtLoad.getWeighSystemLpCd();
				pushModeOn = ordItemAssignmtLoad.getPushModeOn();
				
				lp = getLoadingpointFromMap(ordItemAssignmtLoad);
				if (lp != null) {
					weighSystemLpCd = lp.getWeighSystemLp().getWeighSystemLpCd();
					pushModeOn = lp.getShippingpoint().getWeighSystemSp().getPushModeOn();
				}
				
				// Disable in case of 'manual LOGON' or 'manual PDA'
				if (weighSystemLpCd != null && weighSystemLpCd != (int)Constant.WEIGH_SYSTEM_LP_WS) {
					return result = true;
				}
				
				// FIXME 26-Jul-2012/stich: as currently implemented, cancel wouldn't/cannot be sent if the loadOrderId is not set
				if (ordItemAssignmtLoad.getLoadOrderId() == null) {
					return result = true;
				}
				
				break;
			case FUNCTIONALITY_CANCEL_LOADING:
				/* LOGON-5629 LRQ 073.2 StartLoading does not Block Deallocation Functionality
				 */
				result = checkBasePreConditionsForTrucks(ordItemAssignmtLoad, truckId, nextMinPlannedStart, plannedStart, isReverseOrder);
				
				if ( ordItemAssignmtLoad.getLoadId() != null && ordItemAssignmtLoad.getUnloadingTs() != null){
					//in case it is not in the correct order status disable
					return result = true;					
				}
				if (ordItemAssignmtLoad.getOrderStatusCd() != Constant.ORDER_STATUS_LOADING) {
					return result = true;
				}
				
				
				break;
			case FUNCTIONALITY_LDS_INFORMATION:
				boolean enableUpdateNettoMeasureData = !disableSimpleChangeFunctionality(ordItemAssignmtLoad, 
						OrderChangeLogic.FUNCTIONALITY_UPDATE_NETTO_MEASURE_DATA);
				boolean enableUpdateOrderItemLoadedQuantity = !disableSimpleChangeFunctionality(ordItemAssignmtLoad, 
						OrderChangeLogic.FUNCTIONALITY_UPDATE_ORDER_ITEM_LOADED_QUANTITY);
				result = ordItemAssignmtLoad.getAssignmentId() == null && !(enableUpdateNettoMeasureData || enableUpdateOrderItemLoadedQuantity);
				break;
			case FUNCTIONALITY_LOAD_AS_PLANNED:
				if ( ordItemAssignmtLoad.getTruckId() != null && LogonUtil.isPlanningTruck(ordItemAssignmtLoad.getSapVehicleCharCd()) ){
					return result = true;
				}
				else {
					return ordItemAssignmtLoad.getLoadingRealStart() != null;
				}
		default:
			LOG.error("Unhandled functionality code");
			break;
		}
			return result;

	}

	/**
	 * Determines if a certain functionality is NOT ALLOWED on this order in case of simple vehicle. Returns true, if the functionality is not allowed for
	 * the user, and returns false, if no need to disable.
	 * 
	 * @param ordItemAssignmtLoad OrdItemAssignmtLoadEntity
	 * @param functionality OrderChangeLogic FUNCTIONALITY...
	 * @return
	 */
	private boolean disableSimpleChangeFunctionalityForShipOrBlocktrainPickup(
			OrdItemAssignmtLoadEntity ordItemAssignmtLoad, 
			int functionality){
		boolean result = false;
		
		if (ordItemAssignmtLoad.getLogonPackageTypeCd() == null) {
			//in case the package type is not known, disable everything!
			return result = true;
		}
			switch (functionality){
			case FUNCTIONALITY_UPDATE_LOADINGPOINT_DATA:
				if (ordItemAssignmtLoad.getShipppointTypeCd().equals(Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK)) {
					return true;
				}
				result = checkBasePreConditionsForShipAndBlocktrainPickup(ordItemAssignmtLoad);
				if (ordItemAssignmtLoad.getLoadOrderId() != null){
					return result = true;
				}
				break;
			case FUNCTIONALITY_LOAD_CONTAINER:
				if (!ordItemAssignmtLoad.getShipppointTypeCd().equals(Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK) ) {
					return result = true;
				}
				if (ordItemAssignmtLoad.getLoadingTs() != null) {
					return result = true;
				}
				result = checkBasePreConditionsForShipAndBlocktrainPickup(ordItemAssignmtLoad);
				break;
			case FUNCTIONALITY_UPDATE_TARA_MEASURE_DATA:
				if	(ordItemAssignmtLoad.getLoadNetMeasure() == null && ordItemAssignmtLoad.getLoadOrderId() != null){
					return result = true;
				}
				if (ordItemAssignmtLoad.getShipppointTypeCd().equals(Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK)) {
					return result = true;
				}				
				if (ordItemAssignmtLoad.getLogonPackageTypeCd().equals(Constant.PACKAGE_TYPE_BBAG)) {
					return result = true;
				}
				result = checkBasePreConditionsForShipAndBlocktrainPickup(ordItemAssignmtLoad);			
				break;
			case FUNCTIONALITY_UPDATE_NETTO_MEASURE_DATA:
				if ( ordItemAssignmtLoad.getLogonPackageTypeCd().equals(Constant.PACKAGE_TYPE_BBAG)) {
					return result = true;
				}
				if (ordItemAssignmtLoad.getShipppointTypeCd().equals(Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK)) {
					return result = true;
				}
				result = checkBasePreConditionsForShipAndBlocktrainPickup(ordItemAssignmtLoad);
				if (ordItemAssignmtLoad.getLoadNetMeasure() == null && ordItemAssignmtLoad.getLoadOrderId() != null) {
					return result = true;
				}
				break;
			case FUNCTIONALITY_UPDATE_ORDER_ITEM_LOADED_QUANTITY:
				if ( LogonPackageHandlingUtil.isBulk(ordItemAssignmtLoad.getLogonPackageTypeCd())) {
					//if it is bulk, disable there netto measure is set!
					return result = true;
				}
				if (ordItemAssignmtLoad.getShipppointTypeCd().equals(Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK)) {
					return result = true;
				}
				result = checkBasePreConditionsForShipAndBlocktrainPickup(ordItemAssignmtLoad);
				if (ordItemAssignmtLoad.getLoadNetMeasure() == null && ordItemAssignmtLoad.getLoadOrderId() != null) {
					return result = true;
				}
				break;				
			case FUNCTIONALITY_TRANSMIT_TO_WEIGH_SYSTEM:				
				Long weighSystemLpCd = ordItemAssignmtLoad.getWeighSystemLpCd();
				boolean pushModeOn = ordItemAssignmtLoad.getPushModeOn();
				
				LoadingpointEntity lp = getLoadingpointFromMap(ordItemAssignmtLoad);
				if (lp != null) {
					weighSystemLpCd = lp.getWeighSystemLp().getWeighSystemLpCd();
					pushModeOn = lp.getShippingpoint().getWeighSystemSp().getPushModeOn();
				}
				
				// Disable in case of 'manual LOGON' or 'manual PDA'
				if (weighSystemLpCd != null && !weighSystemLpCd.equals(Constant.WEIGH_SYSTEM_LP_WS)) {
					return result = true;
				}
				
				// Disable based on package type
				if (!pushModeOn) {
					return result = true;
				}
				
				// FIXME 26-Jul-2012/stich: Uncomment if required. As currently implemented, an existing loadOrderId would result in a MessageFunction.CHANGE
				if (ordItemAssignmtLoad.getLoadOrderId() != null) {
					return result = true;
				}
				
				if (ordItemAssignmtLoad.getShipppointTypeCd().equals(Constant.SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK)) {
					return result = true;
				}
				result = checkBasePreConditionsForShipAndBlocktrainPickup(ordItemAssignmtLoad);
				if ( ordItemAssignmtLoad.getLoadId() != null && ordItemAssignmtLoad.getUnloadingTs() != null){
					//in fact this can not happen because to be the truck's next order it has to be null unloading_ts
					//in case it is not in the correct order status disable
					return result = true;					
				}
				if (ordItemAssignmtLoad.getOrderStatusCd() <= Constant.ORDER_STATUS_DISPATCHED){
					return result = true;
				}				
				break;
			case FUNCTIONALITY_CANCEL_WEIGH_SYSTEM:
				// Decision in LOGON-6329: cancel IS possible in the PULL mode (simply deletes the load order id)
				
				result = checkBasePreConditionsForShipAndBlocktrainPickup(ordItemAssignmtLoad);

				if ( ordItemAssignmtLoad.getLoadId() != null && ordItemAssignmtLoad.getUnloadingTs() != null){
					//in case it is not in the correct order status disable
					return result = true;					
				}
				if (ordItemAssignmtLoad.getOrderStatusCd() <= Constant.ORDER_STATUS_DISPATCHED){
					return result = true;
				}

				weighSystemLpCd = ordItemAssignmtLoad.getWeighSystemLpCd();
				pushModeOn = ordItemAssignmtLoad.getPushModeOn();
				
				lp = getLoadingpointFromMap(ordItemAssignmtLoad);
				if (lp != null) {
					weighSystemLpCd = lp.getWeighSystemLp().getWeighSystemLpCd();
					pushModeOn = lp.getShippingpoint().getWeighSystemSp().getPushModeOn();
				}
				
				// Disable in case of 'manual LOGON' or 'manual PDA'
				if (weighSystemLpCd != null && weighSystemLpCd != (int)Constant.WEIGH_SYSTEM_LP_WS) {
					return result = true;
				}
				
				// FIXME 26-Jul-2012/stich: as currently implemented, cancel wouldn't/cannot be sent if the loadOrderId is not set
				if (ordItemAssignmtLoad.getLoadOrderId() == null) {
					return result = true;
				}
				
				break;
			case FUNCTIONALITY_CANCEL_LOADING:
				/* LOGON-5629 LRQ 073.2 StartLoading does not Block Deallocation Functionality
				 */
				result = checkBasePreConditionsForShipAndBlocktrainPickup(ordItemAssignmtLoad);
				
				if ( ordItemAssignmtLoad.getLoadId() != null && ordItemAssignmtLoad.getUnloadingTs() != null){
					//in case it is not in the correct order status disable
					return result = true;
				}
				if (ordItemAssignmtLoad.getOrderStatusCd() != Constant.ORDER_STATUS_LOADING) {
					return result = true;
				}
				
				break;
			default:
				LOG.error("Unhandled functionality code");
				break;
			}
			return result;
	}

	/**
	 * Returns true when the load has to be prevented as it is called from disableSimpleChangeFunctionality().
	 * @param ordItemAssignmtLoad
	 */
	private boolean checkBimodalRoadRequirements(OrdItemAssignmtLoadEntity ordItemAssignmtLoad) {
		boolean result = false;

		Long orderId = ordItemAssignmtLoad.getOrderId();
		ShippBimodalRoadRailEntity shippBimodalRoadRailEntity = bimodalRoadAndRelatedRailInfo.get(orderId);
		if (shippBimodalRoadRailEntity == null) {
			return result = false;
		}
		//if the related bimodal order is not in these statuses disable!
		Long bimodalRailOrderStatusCd = shippBimodalRoadRailEntity.getBimodalRailOrderStatusCd();
		if (!( bimodalRailOrderStatusCd.equals(Long.valueOf(Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT)) || 
				bimodalRailOrderStatusCd.equals(Long.valueOf(Constant.ORDER_STATUS_UNLOADING)) ||
				bimodalRailOrderStatusCd.equals(Long.valueOf(Constant.ORDER_STATUS_DELIVERED)) ||
				bimodalRailOrderStatusCd.equals(Long.valueOf(Constant.ORDER_STATUS_FINISHED)) )){
			return result = true;
		}
		return result;
	}

	/**
	 * These are the base conditions for a load to be editable.
	 * @param ordItemAssignmtLoad
	 * @param truckId
	 * @param nextLoadingMinPlannedStart
	 * @param loadingMinPlannedStart
	 * @param isReverseOrder
	 * @return
	 */
	private static boolean checkBasePreConditionsForTrucks(OrdItemAssignmtLoadEntity ordItemAssignmtLoad, Long truckId,
			Date nextLoadingMinPlannedStart, Date loadingMinPlannedStart, Boolean isReverseOrder) {
		boolean result = false;
		if ((  ordItemAssignmtLoad.getLoadId() != null && ordItemAssignmtLoad.getLoadingTs() != null && (ordItemAssignmtLoad.getOrderStatusCd() > Constant.ORDER_STATUS_DISPATCHED && ordItemAssignmtLoad.getOrderStatusCd() <= Constant.ORDER_STATUS_DELIVERED ))) {
			//in case it has already been started, but below delivered, let it edit!
			return result = false;
		}
		if ( !(ordItemAssignmtLoad.getOrderStatusCd() > Constant.ORDER_STATUS_DISPATCHED && ordItemAssignmtLoad.getOrderStatusCd() <= Constant.ORDER_STATUS_DELIVERED )) {
			return result = true;
		}
		
		if (nextLoadingMinPlannedStart == null){
			//it is not even present in the list --> not editable
			return result = true;
		}
		if (truckId != null && loadingMinPlannedStart != null && ! (loadingMinPlannedStart.compareTo(nextLoadingMinPlannedStart) == 0)){
			if (isArtificialPickupTruck(truckId) || isReverseOrder) {
				//in case of the artificial bulk/bag pickup trucks this rule should have no effect
				return result = false;
			}
			else{				
				//in case this is not the next possible plannedstart of the truck
				return result = true;
			}
		}				
		return result;
	}

	/**
	 * These are the base conditions for a load to be editable.
	 * @param ordItemAssignmtLoad
	 * @return
	 */
	private static boolean checkBasePreConditionsForShipAndBlocktrainPickup(OrdItemAssignmtLoadEntity ordItemAssignmtLoad) {
		boolean result = false;
		Long orderStatusCd = ordItemAssignmtLoad.getOrderStatusCd();
		Boolean isBeyondDispatchedAndDeliveredOrBefore = LogonStatusUtils.isBeyondDispatchedAndDeliveredOrBefore(orderStatusCd);
		if (ordItemAssignmtLoad.getLoadId() != null && ordItemAssignmtLoad.getLoadingTs() != null && isBeyondDispatchedAndDeliveredOrBefore) {
			//in case it has already been started, but below delivered, let it edit!
			return result = false;
		}
		if (!isBeyondDispatchedAndDeliveredOrBefore) {
			return result = true;
		}			
		return result;
	}
	
	/**
	 * Checks if the given truck is an artificial pickup truck.
	 * @param truckId ID (PK) of a truck entity.
	 * @return true if the truck is one of the artificial (technical) trucks used to represent
	 * pickup deliveries, false otherwise.
	 */
	private static boolean isArtificialPickupTruck(Long truckId)
	{
		return truckId.equals(Long.valueOf(Constant.ARTIFICIAL_PICKUP_BAG_TRUCK_ID)) || 
				truckId.equals(Long.valueOf(Constant.ARTIFICIAL_PICKUP_BULK_DMP_TRUCK_ID)) ||
				truckId.equals(Long.valueOf(Constant.ARTIFICIAL_PICKUP_BULK_PWDR_TRUCK_ID ));
	}

	/**
	 * Returns the loadingpoint from the map, which is assigned to the order. If not in the map, the order is not editable at all.
	 * @param ordItemAssignmtLoad
	 * @return
	 */
	private LoadingpointEntity getLoadingpointFromMap(OrdItemAssignmtLoadEntity ordItemAssignmtLoad) {
		LoadingpointEntity lp = null;
		Long id = ordItemAssignmtLoad.getLoadId();
		Long lpId = ordItemAssignmtLoad.getLoadingpointId();
		List<LdpointMatRestrEntity> lpList = orderLoadingPointListMapping.get(id);
		if (lpList != null && !lpList.isEmpty()) {
			Iterator<LdpointMatRestrEntity> it = lpList.iterator();
			while (it.hasNext()) {
				LdpointMatRestrEntity type = it.next();
				if(type.getLoadingpoint().getLoadingpointId().equals(lpId)){						
					lp = type.getLoadingpoint();
					break;
				}
			}			
		}
		return lp;
	}

}
