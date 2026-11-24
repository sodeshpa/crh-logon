/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.GoodsMovementEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.MatShippingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.webservices.clients.beans.goodsmovement.GoodsMovementItemDTO;

/**
 * @author AdNovum Informatik AG
 */
public class GoodsMovementUtils {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(GoodsMovementUtils.class);
	
	public final static String MOVEMENT_KEY = "orderdetail.material.goodsmovement.text.movement";
	public final static String END_LOAD_REASON_KEY = "orderdetail.material.goodsmovement.text.reason.endLoad";
	public final static String RETURNED_REASON_KEY = "orderdetail.material.goodsmovement.text.reason.returned";
	public final static String CREDIT_NOTE_REASON_KEY = "orderdetail.material.goodsmovement.text.reason.creditNote";
	public final static String PENDING_LABEL_KEY = "orderdetail.material.goodsmovement.text.pending";

	/**
	 * Map the BWART codes to their cancel BWART code (used to cancel goods movements).
	 */
	public static final Map<String, String> movementTypeCancelMap;
	static {
		movementTypeCancelMap = new HashMap<String, String>();
		movementTypeCancelMap.put(GoodsMovementItemDTO.TR_WITHIN_PLANT, GoodsMovementItemDTO.TR_SAME_PLANT_VIRT_TO_REAL_SLOC);
		movementTypeCancelMap.put(GoodsMovementItemDTO.TR_SAME_PLANT_VIRT_TO_REAL_SLOC, GoodsMovementItemDTO.TR_WITHIN_PLANT);
		movementTypeCancelMap.put(GoodsMovementItemDTO.TR_BETWEEN_PLANTS, GoodsMovementItemDTO.TR_BETWEEN_PLANTS);
		movementTypeCancelMap.put(GoodsMovementItemDTO.TR_RUBISH, GoodsMovementItemDTO.TR_CANCEL_RUBISH);
		movementTypeCancelMap.put(GoodsMovementItemDTO.TR_CANCEL_RUBISH, GoodsMovementItemDTO.TR_RUBISH);
	}
	
	/**
	 * 
	 * @param shippingpoint
	 * @param matShippingpoint
	 * @return
	 */
	public static String getStorageLocation(ShippingpointEntity shippingpoint, MatShippingpointEntity matShippingpoint) {
		// [[
		String result = null;
		Object[][] params = {{"shippingpoint", shippingpoint},
							{"matShippingpoint", matShippingpoint}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(GoodsMovementUtils.class, "getStorageLocation", params);
		}
		// ]]
		try {
			if (shippingpoint == null && matShippingpoint == null) {
				LOG.warn("The 'shippingpoint' and 'matShippingpoint' parameters are <NULL>");
				return result = null;
			}
			
			String storageLocation = null;
			if (matShippingpoint != null) {
				if (!StringUtils.isEmpty(matShippingpoint.getStorageLocation())) {
					storageLocation = matShippingpoint.getStorageLocation();
				}
			}
			else {
				storageLocation = shippingpoint.getStorageLocation();
			}
			
			return result = storageLocation;
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
	 * Get Storage location (in transit/SAP virtual) maintained on the SAP known delivery for the given
	 * <code>plant</code> based on parameter <code>sapMaterial</code>.
	 * 
	 * First identify the delivery plant point through send_sap_shippingpoint_id 
	 * (to use SAP known plant). If bag packaging type - use tlgnc_plant.storage_loc_bag_in_transit, 
	 * 													else (bulk) use storage_loc_bulk_in_transit.
	 * 
	 * @param plant 
	 * @param sapMaterial 
	 * @return String with issuingStorageLocation
	 */
	public static String getVirtualStorageLocation(PlantBsEntity plant, SapMaterialEntity sapMaterial) {
		// [[
		String result = null;
		Object[][] params = {{"plant", plant},
				{"sapMaterial", sapMaterial}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(GoodsMovementUtils.class, "getVirtualStorageLocation", params);
		}
		// ]]
		try {
			
			if (plant != null && sapMaterial != null) {
				// For BAG
				if (LogonPackageHandlingUtil.isBag(sapMaterial)) {
					return result = plant.getStorageLocBagInTransit();
				}
				// For BULK
				else if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(sapMaterial)){
					return result = plant.getStorageLocBulkInTransit();
				}
			}
			
			return null;
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
	 * Check if a Goods movement request needs to be created for the given load.
	 * The logic to be used at the check is described in BLOCK3_LRQ_041.5
	 * 
	 * @param load
	 * @return
	 */
	public static boolean isGMRequiredForLoadAndOrderItems(LoadEntity load, List<OrderItemEntity> orderItems) {
		// [[
		boolean result = false;
		Object[][] params = { { "load", load }, { "orderItems", orderItems } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(GoodsMovementUtils.class, "isGMRequiredForLoadAndOrderItems", params);
		}
		// ]]
		try {
			if (load == null) {
				String errorMsg = "The 'load' parameter is null!";
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			
			SapMaterialEntity matEntity = load.getSapMaterial();
			Long loadId = load.getLoadId();
			
			// check Material Package Type Distinction
			String materialPackageCd = matEntity.getMaterialPackage().getMaterialPackageCd();
			if (!LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(matEntity) && !LogonPackageHandlingUtil.isBag(matEntity)) {
				if (logDebugEnabled) {
					LOG.debug("The goods movement entity creation check is not fulfilled for material package type --> no Request will be sent for load id:'"
							+ loadId + "'");
					LOG.debug("The material package type is: '" + materialPackageCd + "'");
				}
				return result = false;
			}
			
			// check Material Type Distinction
			if (!matEntity.getMaterialIsCem() && !matEntity.getMaterialIsAgg()) {
				if (logDebugEnabled) {
					LOG.debug("The goods movement entity creation check is not fulfilled for material type --> no Request will be sent for load id:'"
							+ loadId + "'");
					LOG.debug("The material type is: landfill or 'NULL'");
				}
				return result = false;
				
			}

			boolean isScheduledPickupOrReverseOrder = (orderItems != null && !orderItems.isEmpty());
			
			if (isScheduledPickupOrReverseOrder) {
				for(OrderItemEntity orderItem : orderItems){
					OrderEntity order = orderItem.getOrder();
					if (! (LogonUtil.isScheduledPickupOrder(order)
							|| LogonUtil.isOrderWithReversedDirection(order) ) ){
						isScheduledPickupOrReverseOrder = false;
						break;
					}
				}
			}
			// Delivery Type Distinction
			if (LogonUtil.isLoadForPickupTruck(load.getTruck())
					|| isScheduledPickupOrReverseOrder
					|| LogonUtil.isShipOrBlocktrainPickup(load.getSimpleVehicle())) {
				if (logDebugEnabled) {
					LOG.debug("The goods movement entity creation check is not fulfilled for delivery type --> no Request will be sent for load id:'"
							+ loadId + "'");
					LOG.debug("The delivery type could be a 'pickup' as the load.truck_id is defined for load.getTruck().getTruckId():'"
							+ (load.getTruck() == null ? "<NULL>" : load.getTruck().getTruckId())
							+ "'");
					LOG.debug("The delivery type could be a 'scheduledPickup' as orderItems contain only RoadDirect with EXW or FOB incoterms:'"
							+ (isScheduledPickupOrReverseOrder ? " it's a scheduled pickup order" : "it's not a scheduled pickup order")
							+ "'");
				}
				return result = false;
			}
			
			return result = true;
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
	 * Specific formatter method for GoodsMovementDisplayBean, formatter for "Reason" field on GoodsMovementPopupTile.jsp
	 */
	public static String getGoodsMovementReason(GoodsMovementEntity goodsMovementEntity, String endLoadReason, String returnedReason, String creditNoteReason) {
		// [[
		String result1 = null;
		Object[][] params = {{"goodsMOvementEntity", goodsMovementEntity},
							{"endLoadReason", endLoadReason},
							{"returnedReason", returnedReason},
							{"creditNoteReason", creditNoteReason}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(GoodsMovementUtils.class, "getGoodsMovementReason", params);
		}
		// ]]
		try {
			
			String result = "";
			List<String> reasons = new ArrayList<String>(3);
			
			if (goodsMovementEntity.getIsEndloadGm()) {
				reasons.add(endLoadReason);
			}
			
			if (goodsMovementEntity.getIsReturnGm()) {
				reasons.add(returnedReason);
			}
			
			if (goodsMovementEntity.getIsCreditNoteGm()) {
				reasons.add(creditNoteReason);
			}
			
			for (String reason : reasons) {
				if (!StringUtils.isEmpty(result)) {
					result += ", ";
				}
				result += reason;
			}
			return result1 = result;
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
	 * Specific formatter method for GoodsMovementDisplayBean, formatter for "Send Date/Time" field on GoodsMovementPopupTile.jsp 
	 */
	public static String getGoodsMovementDate(GoodsMovementEntity goodsMovementEntity, String pending, Locale locale) {
		// [[
		String result1 = null;
		Object[][] params = {{"goodsMovementEntity", goodsMovementEntity},
							{"pending", pending},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(GoodsMovementUtils.class, "getGoodsMovementDate", params);
		}
		// ]]
		try {
			String result = "";
			if (goodsMovementEntity.getSentGoodsmoveToSapTs() != null){
				result = DateTimeUtils.formatDayMonthYear(goodsMovementEntity.getSentGoodsmoveToSapTs(), locale) 
					+ " " + DateTimeUtils.getHoursMinutesString(goodsMovementEntity.getSentGoodsmoveToSapTs());
			}
			else {
				if (goodsMovementEntity.getSentGoodsmoveToQueueTs() != null){
					result = pending;
				}
				else {
					result = "-";
				}
			}
			return result1 = result;
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
	 * Specific formatter method for GoodsMovementDisplayBean, formatter for "Delivery Plant" field. PLANT_NAME (PLANT_CODE)
	 */
	public static String getGoodsMovementDeliveryPlant(GoodsMovementEntity goodsMovementEntity) {
		// [[
		String result1 = null;
		Object[][] params = {{"getGoodsMovementDeliveryPlant", goodsMovementEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(GoodsMovementUtils.class, "getGoodsMovementDeliveryPlant", params);
		}
		// ]]
		try {
			String result = "";
			PlantBsEntity toPlant = goodsMovementEntity.getToPlant();
			if (toPlant != null) {
				result = toPlant.getPlantName() + " (" + toPlant.getPlantCd() + ")";
			}
			else {
				result = "-";
			}
			return result;
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
	 * Specific formatter method for GoodsMovementDisplayBean, formatter for "From Plant" field. PLANT_NAME (PLANT_CODE)
	 */
	public static String getGoodsMovementFromPlant(GoodsMovementEntity goodsMovementEntity) {
		// [[
		String result = null;
		Object[][] params = {{"goodsMovementEntity", goodsMovementEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(GoodsMovementUtils.class, "getGoodsMovementFromPlant", params);
		}
		// ]]
		try {
			String fromPlantName = "";
			PlantBsEntity fromPlant = goodsMovementEntity.getFromPlant();
			if (fromPlant != null) {
				fromPlantName = fromPlant.getPlantName() + " (" + fromPlant.getPlantCd() + ")";
			}
			else {
				fromPlantName = "-";
			}
			return result = fromPlantName;
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
	 * Create a new cancel {@link GoodsMovementEntity} for the given {@code entity}.
	 * 
	 * @param entity
	 * @return
	 */
	public static GoodsMovementEntity getCancelGoodsMovementEntity(GoodsMovementEntity entity) {
		// [[
		GoodsMovementEntity result = null;
		Object[][] params = {{"entity", entity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(GoodsMovementUtils.class, "getCancelGoodsMovementEntity", params);
		}
		// ]]
		try {
			if (entity == null) {
				LOG.warn("The given goods movement entity is <NULL>, cannot create the cancel movement!");
				return result = null;
			}

			GoodsMovementEntity stornoEntity = createGoodsMovementEntity(entity);
			
			// set the cancel type
			String cancelMovementType = movementTypeCancelMap.get(stornoEntity.getMovementType());
			stornoEntity.setMovementType(cancelMovementType);
			
			LOG.debug("The cancel type for movement type: '" + entity.getMovementType() + "', is :'" + cancelMovementType+ "'");
			
			/**
			 * when movement between plants (movement type is '301') exchange the 'FROM SLOC' with 'TO SLOC'  
 			 */
			if (GoodsMovementItemDTO.TR_BETWEEN_PLANTS.equals(cancelMovementType)) {
				stornoEntity.setFromPlant(entity.getToPlant());
				stornoEntity.setFromStorageLoc(entity.getToStorageLoc());
				stornoEntity.setToPlant(entity.getToPlant());
				stornoEntity.setToStorageLoc(entity.getFromStorageLoc());
			}
			
			return result = stornoEntity;
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
	 * Create a new {@link GoodsMovementEntity}, and set the values from the given {@code entity}.
	 * The control fields and the primary key (goods_movement_id) are not set. 
	 * 
	 * @param entity
	 * @return
	 */
	private static GoodsMovementEntity createGoodsMovementEntity(GoodsMovementEntity entity) {
		// [[
		GoodsMovementEntity result = null;
		Object[][] params = {{"entity", entity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(GoodsMovementUtils.class, "createGoodsMovementEntity", params);
		}
		// ]]
		try {
			if (entity == null) {
				LOG.warn("The given goods movement entity is <NULL>, cannot create the cancel movement!");
				return result = null;
			}
			
			GoodsMovementEntity newEntity = new GoodsMovementEntity();
			newEntity.setFromPlant(entity.getFromPlant());
			newEntity.setFromStorageLoc(entity.getFromStorageLoc());
			newEntity.setIsCreditNoteGm(entity.getIsCreditNoteGm());
			newEntity.setIsEndloadGm(entity.getIsEndloadGm());
			newEntity.setIsReturnGm(entity.getIsReturnGm());
			newEntity.setLoad(entity.getLoad());
			newEntity.setNetQuantityUom(entity.getNetQuantityUom());
			newEntity.setMovementType(entity.getMovementType());
			newEntity.setNetQuantity(entity.getNetQuantity());
			newEntity.setReferenceDocumentNumberSap(entity.getReferenceDocumentNumberSap());
			newEntity.setSapMaterial(entity.getSapMaterial());
			newEntity.setSentGoodsmoveToQueueTs(new Date());
			newEntity.setToPlant(entity.getToPlant());
			newEntity.setToStorageLoc(entity.getToStorageLoc());
			
			return result = newEntity;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public static void validateOrderItem(OrderItemEntity orderItem) {
		if (orderItem == null) {
			String errorMsg = "The Order Item Entity is not defined!";
			LOG.error(errorMsg);
			
			throw new LogonException(errorMsg, Reason.RES_ERROR_GOODSMOVMENT_ORDERITEM_NOT_NULL);
		}
		
		if (orderItem.getReturnedQuantity() == null) {
			String errorMsg = "The Order Item with id: '" + orderItem.getOrderItemId() + "', has no 'return quantity' set!";
			LOG.error(errorMsg);
			
			throw new LogonException(errorMsg, Reason.RES_ERROR_GOODSMOVMENT_ORDERITEM_STATE_WRONG);
		}
		
		if (orderItem.getReturnedUom() == null) {
			String errorMsg = "The Order Item with id: '" + orderItem.getOrderItemId() + "', has no 'return Uom' set!";
			LOG.error(errorMsg);
			
			throw new LogonException(errorMsg, Reason.RES_ERROR_GOODSMOVMENT_ORDERITEM_STATE_WRONG);
		}
		
		if (orderItem.getReturnMaterialUsage() == null) {
			String errorMsg = "The Order Item with id: '" + orderItem.getOrderItemId() + "', has no 'return material usage' set!";
			LOG.error(errorMsg);
			
			throw new LogonException(errorMsg, Reason.RES_ERROR_GOODSMOVMENT_ORDERITEM_STATE_WRONG);
		}
		
		if (orderItem.getReturnPlant() == null) {
			String errorMsg = "The Order Item with id: '" + orderItem.getOrderItemId() + "', has no 'return plant' set!";
			LOG.error(errorMsg);
			
			throw new LogonException(errorMsg, Reason.RES_ERROR_GOODSMOVMENT_ORDERITEM_STATE_WRONG);
		}
		
		if (orderItem.getReturnShippingpoint() == null) {
			String errorMsg = "The Order Item with id: '" + orderItem.getOrderItemId() + "', has no 'return shippingpoint' set!";
			LOG.error(errorMsg);
			
			throw new LogonException(errorMsg, Reason.RES_ERROR_GOODSMOVMENT_ORDERITEM_STATE_WRONG);
		}
	}
	
	public static boolean isRefillingReturn(OrderItemEntity orderItem) {
		return Constant.RETURN_MATERIAL_USAGE_REFILLING_VEHICLE_CD.equals(orderItem.getReturnMaterialUsage().getReturnMaterialUsageCd());
	}
	public static boolean isMixQualityReturn(OrderItemEntity orderItem) {
		return Constant.RETURN_MATERIAL_USAGE_MIXED_QUALITIES_CD.equals(orderItem.getReturnMaterialUsage().getReturnMaterialUsageCd());
	}
	public static boolean isRubishReturn(OrderItemEntity orderItem) {
		return Constant.RETURN_MATERIAL_USAGE_RUBISH_CD.equals(orderItem.getReturnMaterialUsage().getReturnMaterialUsageCd());
	}
}
