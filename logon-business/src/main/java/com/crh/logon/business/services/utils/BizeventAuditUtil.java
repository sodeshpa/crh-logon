/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.BizeventAuditEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.refdata.AuditBizeventConfigBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderSpiTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnOrderReasonTxEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.BizeventAuditDisplayEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public class BizeventAuditUtil {
		
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BizeventAuditUtil.class);
	
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_ORDER_STATUS_CD = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_SHIPTO_SAP_BP_ID = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_SOLDTO_SAP_BP_ID = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_BILLTO_SAP_BP_ID = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_POOL_ID = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_EXEC_CARRIER_ID = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_SHIPPINGPOINT_ID = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_DELIVERY_TYPE_CD = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_DELIVERY_FROM_DAT = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_DELIVERY_TO_DAT = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_RELEASE_SAP_FLAG = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_NO_PDA_OR_RAIL_EVENT_FLAG = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_FREE_DELIVERY_FLAG = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_NOTE_ORDER_MGMT = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_NOTE_DELIVERY = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_NOTE_DISPATCHING = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_NOTE_TRUCK_DRIVER = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_NOTE_RAILWAY_DISP = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_CARRIER_DELIVERY_NOTE = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_CARRIER_TRUCK_DRIVER_NOTE = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_CUST_DELIVERY_NOTE_COMMENT = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_ON_HOLD_ORDER_AND_LOCKED_FLAG = new AuditBizeventConfigBsEntity();

	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_PRODUCTION_PLANT_CD = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_ORDERED_UOM_CD = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_ORDERED_QUANTITY = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_LOADED_UOM_CD = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_LOADED_QUANTITY = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_RETURNED_UOM_CD = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_RETURNED_QUANTITY = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_RETURNED_REASON_CD = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_DELIVERED_UOM_CD = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_DELIVERED_QUANTITY = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_LOADINGPOINT_ID = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_SHIPTO_SILO_ID = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_ITEM_NOTE_ORDER_MGMT = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_ITEM_NOTE_DELIVERY = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_ITEM_NOTE_TRUCK_DRIVER = new AuditBizeventConfigBsEntity();
	public static final AuditBizeventConfigBsEntity AUDIT_BIZEVENT_CONFIG_ITEM_NOTE_RAILWAY = new AuditBizeventConfigBsEntity();
	
	
	static {

		AUDIT_BIZEVENT_CONFIG_ORDER_STATUS_CD.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_ORDER_STATUS_CD);
		AUDIT_BIZEVENT_CONFIG_SHIPTO_SAP_BP_ID.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_SHIPTO_SAP_BP_ID);
		AUDIT_BIZEVENT_CONFIG_SOLDTO_SAP_BP_ID.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_SOLDTO_SAP_BP_ID);
		AUDIT_BIZEVENT_CONFIG_BILLTO_SAP_BP_ID.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_BILLTO_SAP_BP_ID);
		AUDIT_BIZEVENT_CONFIG_POOL_ID.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_POOL_ID);
		AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID);
		AUDIT_BIZEVENT_CONFIG_EXEC_CARRIER_ID.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_EXEC_CARRIER_ID);
		AUDIT_BIZEVENT_CONFIG_SHIPPINGPOINT_ID.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_SHIPPINGPOINT_ID);
		AUDIT_BIZEVENT_CONFIG_DELIVERY_TYPE_CD.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERY_TYPE_CD);
		AUDIT_BIZEVENT_CONFIG_DELIVERY_FROM_DAT.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERY_FROM_DAT);
		AUDIT_BIZEVENT_CONFIG_DELIVERY_TO_DAT.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERY_TO_DAT);
		AUDIT_BIZEVENT_CONFIG_RELEASE_SAP_FLAG.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_RELEASE_SAP_FLAG);
		AUDIT_BIZEVENT_CONFIG_NO_PDA_OR_RAIL_EVENT_FLAG.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_NO_PDA_OR_RAIL_EVENT_FLAG);
		AUDIT_BIZEVENT_CONFIG_FREE_DELIVERY_FLAG.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_FREE_DELIVERY_FLAG);
		AUDIT_BIZEVENT_CONFIG_NOTE_ORDER_MGMT.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_ORDER_MGMT);
		AUDIT_BIZEVENT_CONFIG_NOTE_DELIVERY.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_DELIVERY);
		AUDIT_BIZEVENT_CONFIG_NOTE_DISPATCHING.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_DISPATCHING);
		AUDIT_BIZEVENT_CONFIG_NOTE_TRUCK_DRIVER.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_TRUCK_DRIVER);
		AUDIT_BIZEVENT_CONFIG_NOTE_RAILWAY_DISP.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_RAILWAY_DISP);
		AUDIT_BIZEVENT_CONFIG_CARRIER_DELIVERY_NOTE.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_CARRIER_DELIVERY_NOTE);
		AUDIT_BIZEVENT_CONFIG_CARRIER_TRUCK_DRIVER_NOTE.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_CARRIER_TRUCK_DRIVER_NOTE);
		AUDIT_BIZEVENT_CONFIG_CUST_DELIVERY_NOTE_COMMENT.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_CUST_DELIVERY_NOTE_COMMENT);
		AUDIT_BIZEVENT_CONFIG_ON_HOLD_ORDER_AND_LOCKED_FLAG.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_ON_HOLD_ORDER_AND_LOCKED_FLAG);

		AUDIT_BIZEVENT_CONFIG_PRODUCTION_PLANT_CD.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_PRODUCTION_PLANT_CD);
		AUDIT_BIZEVENT_CONFIG_ORDERED_UOM_CD.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_ORDERED_UOM_CD);
		AUDIT_BIZEVENT_CONFIG_ORDERED_QUANTITY.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_ORDERED_QUANTITY);
		AUDIT_BIZEVENT_CONFIG_LOADED_UOM_CD.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_LOADED_UOM_CD);
		AUDIT_BIZEVENT_CONFIG_LOADED_QUANTITY.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_LOADED_QUANTITY);
		AUDIT_BIZEVENT_CONFIG_RETURNED_UOM_CD.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_UOM_CD);
		AUDIT_BIZEVENT_CONFIG_RETURNED_QUANTITY.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_QUANTITY);
		AUDIT_BIZEVENT_CONFIG_RETURNED_REASON_CD.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_REASON_CD);
		AUDIT_BIZEVENT_CONFIG_DELIVERED_UOM_CD.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERED_UOM_CD);
		AUDIT_BIZEVENT_CONFIG_DELIVERED_QUANTITY.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERED_QUANTITY);
		AUDIT_BIZEVENT_CONFIG_LOADINGPOINT_ID.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_LOADINGPOINT_ID);
		AUDIT_BIZEVENT_CONFIG_SHIPTO_SILO_ID.setAuditBizeventConfigCd(Constant.AUDIT_BIZEVENT_CONFIG_SHIPTO_SILO_ID);
		
	}

	
	/**
	 * Returns true in case for the auditBizeventConfigCd value must be saved into the _ID postfic (type Long) field.
	 * @param auditBizeventConfigCd
	 * @return
	 */
	public static boolean isAuditBizeventStoringId(final Long auditBizeventConfigCd) {
		final Collection<BizeventAuditFieldConfiguration> fieldConfigurations = BizeventAuditConfiguration.fieldConfigurations;
		
		for (BizeventAuditFieldConfiguration audit : fieldConfigurations) {
			if (audit.getAuditBizeventConfigId() == auditBizeventConfigCd.longValue()) {
				if (audit.getFieldType() == BizeventAuditConfiguration.FIELD_TYPE_ID) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns true in case for the auditBizeventConfigCd value must be saved into the _STR postfic (type String) field.
	 * 
	 * @param auditBizeventConfigCd
	 * @return
	 */
	public static boolean isAuditBizeventStoringString(final Long auditBizeventConfigCd) {
		
		final Collection<BizeventAuditFieldConfiguration> fieldConfigurations = BizeventAuditConfiguration.fieldConfigurations;
		
		for (BizeventAuditFieldConfiguration audit : fieldConfigurations) {
			if ( audit.getAuditBizeventConfigId() == auditBizeventConfigCd.longValue()) {
				if ( audit.getFieldType() == BizeventAuditConfiguration.FIELD_TYPE_STRING || audit.getFieldType() == BizeventAuditConfiguration.FIELD_TYPE_CD) {
					return true;
				}
			}
		}
		return false;
	} 	
	
	/**
	 * Returns true in case for the auditBizeventConfigCd value must be saved into the _Dat postfic (type Date) field.
	 * 
	 * @param auditBizeventConfigCd
	 * @return
	 */
	public static boolean isAuditBizeventStoringDat(final Long auditBizeventConfigCd) {
		final Collection<BizeventAuditFieldConfiguration> fieldConfigurations = BizeventAuditConfiguration.fieldConfigurations;
		
		for (BizeventAuditFieldConfiguration audit : fieldConfigurations) {
			if ( audit.getAuditBizeventConfigId() == auditBizeventConfigCd.longValue()) {
				if ( audit.getFieldType() == BizeventAuditConfiguration.FIELD_TYPE_DATE) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns the field code for the bizevent which is supposed to be stored even if it has not changed at all.
	 * 
	 * For certain bizevent this is kind of an information audit about the current state of a field.
	 * 
	 * For example: for transmit to carrier bizevent it is important to know what was the carrier at the point
	 * the bizevent was created even if it has not changed. 
	 * 
	 * @param auditBizeventConfigCd
	 * @return
	 */
	public static Long getStoredFieldCode(final Long bizeventTypeCd) {
		final List <BizeventAuditConfiguration.SAVE_FIELD_STATUS_FOR_BIZEVENT> values = Arrays.asList(BizeventAuditConfiguration.SAVE_FIELD_STATUS_FOR_BIZEVENT.values());
		for (BizeventAuditConfiguration.SAVE_FIELD_STATUS_FOR_BIZEVENT audit : values) {
			if ( audit.getBizeventTypeCd().longValue() == bizeventTypeCd.longValue()) {
					return audit.getAuditCode();
			}
		}
		return null;
	}
	
	/**
	 * Returns true in case the field in is part of an audited group.
	 * @param auditBizeventConfigCd
	 * @return
	 */
	public static boolean isAuditBizeventGrouped(final Long auditBizeventConfigCd) {
		final BizeventAuditConfiguration.AUDIT_GROUP[] values = BizeventAuditConfiguration.AUDIT_GROUP.values();
		for ( BizeventAuditConfiguration.AUDIT_GROUP group : values ) {
			if (group.getAuditCodes().contains(auditBizeventConfigCd)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true in case a field is ment to be resolved by langauge.
	 * 
	 * @param auditConfigCd
	 * @return
	 */
	public static boolean isAuditBizeventValueImplicit(Long auditBizeventConfigCd) {
		
		final Collection<BizeventAuditFieldConfiguration> fieldConfigurations = BizeventAuditConfiguration.fieldConfigurations;
		
		for (BizeventAuditFieldConfiguration audit : fieldConfigurations) {
			if ( audit.getAuditBizeventConfigId() == auditBizeventConfigCd.longValue()) {
				return audit.isLanguageResolutionNeeded();
			}
		}
		return false;
	}

	/**
	 * Returns a {@link BizeventAuditEntity} for the specified {@link AuditBizeventConfigBsEntity}. 
	 * This function is supposed to take care of storing the value in a field corresponding to the type of the 
	 * audited field, and to make sure that values stored here fit into that field.
	 * 
	 * @param bs
	 * @param o
	 * @param to
	 * @return
	 */
	public static BizeventAuditEntity createAuditBizevent(AuditBizeventConfigBsEntity bs,
			Object o, Object to) {
		BizeventAuditEntity bizeventAudit = new BizeventAuditEntity();
		bizeventAudit.setAuditBizeventConfig(bs);
		final Long auditBizeventConfigCd = bs.getAuditBizeventConfigCd();
		
		if (  isAuditBizeventStoringId(auditBizeventConfigCd)) {
			
			bizeventAudit.setOldValueId((Long) o);
			bizeventAudit.setNewValueId((Long) to);					
		}
		else if ( isAuditBizeventStoringString(auditBizeventConfigCd) ) {
			
			if ( o != null ) {
				int maxLength = Long.valueOf(BizeventAuditEntity.DBSIZE_OLD_VALUE_STR).intValue();
				String string = o.toString();
				String trimmed = getValueTrimmed(maxLength, string);
				bizeventAudit.setOldValueStr(trimmed);				
			}
			if ( to != null ) {
				int maxLength = Long.valueOf(BizeventAuditEntity.DBSIZE_NEW_VALUE_STR).intValue();
				String string = to.toString();
				String trimmed = getValueTrimmed(maxLength, string);
				bizeventAudit.setNewValueStr(trimmed);				
			}
		}
		else if ( isAuditBizeventStoringDat(auditBizeventConfigCd) ) {
			bizeventAudit.setOldValueDat((Date) o);
			bizeventAudit.setNewValueDat((Date) to);
		}
		
		return bizeventAudit;
	}
	
	

	/**
	 * Trims the value to the specified maxLength, and replaces the last three
	 * possible character with ...
	 * 
	 * @param maxLength
	 * @param string
	 * @return
	 */
	private static String getValueTrimmed(int maxLength, String string) {

		int originalLength = string.length();
		String value = string;

		if ( originalLength > maxLength ) {
			String concat = "...";
			String  s = StringUtils.remove(string,'\r');
			s = StringUtils.remove(s,'\n');
			int whiteSpaceLength = originalLength - s.length();
			int newLength = maxLength - concat.length() - whiteSpaceLength;
			String remainingInfo = "(+" + Integer.toString(originalLength - newLength) + ")";
			int remainingInfoLength = remainingInfo.length();
			value = string.substring(0, newLength - remainingInfoLength);
			value += concat + remainingInfo;

		}

		return value;
	}
	
	
	/**
	 * Returns the field value from the order according to the audit config code specified.
	 * 
	 * @param auditConfigCd
	 * @param order
	 * @return
	 */
	private static Object getFieldValueForAuditConfigCode(Long auditConfigCd, OrderEntity order) {

	
		if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERY_TO_DAT)){
			return order.getDeliveryToDat();	
		}
		
		if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERY_FROM_DAT)){
			return order.getDeliveryFromDat();	
		}

		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_RELEASE_SAP_FLAG)){			
			return order.getReleaseSapFlag();
		}

		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_NO_PDA_OR_RAIL_EVENT_FLAG)){
			return order.getNoPdaOrRailEventsFlag();
		}

		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_FREE_DELIVERY_FLAG)){
			
			return order.getFreeDeliveryFlag();
		}
		
		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_ORDER_MGMT)){
			
			return order.getNoteOrderMgmt();
		}

		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_DELIVERY)){			
			return order.getNoteDelivery();
		}

		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_DISPATCHING)){
			
			return order.getNoteDispatching();
		}
	
		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_TRUCK_DRIVER)){			
			return order.getNoteTruckDriver();
		}
		
		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_RAILWAY_DISP)){
			return order.getNoteRailwayDisp();			
		}
		
		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_CARRIER_DELIVERY_NOTE)){

			return order.getCarrierDeliveryNote();	
		}
		
		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_CARRIER_TRUCK_DRIVER_NOTE)){	
			return order.getCarrierTruckDriverNote();
		}
		
		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_CUST_DELIVERY_NOTE_COMMENT)){			
			return order.getCustDeliveryNoteComment();
		}

		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_ON_HOLD_ORDER_AND_LOCKED_FLAG)){
			
			return order.getOnHoldOrderAndLockedFlag();
		}		
		
		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SHIPTO_SAP_BP_ID)){
			
			return ( order.getShiptoSapBp() != null ? order.getShiptoSapBp().getSapBpId() : null );
		}
		
		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SOLDTO_SAP_BP_ID)){
			return ( order.getSoldtoSapBp() != null ? order.getSoldtoSapBp().getSapBpId() : null );			
		}

		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_BILLTO_SAP_BP_ID)){
			return ( order.getBilltoSapBp() != null ? order.getBilltoSapBp().getSapBpId() : null );			
		}

		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_POOL_ID)){
			return ( order.getPool() != null ? order.getPool().getPoolId() : null );			
		}
		
		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID)){
			return ( order.getCommCarrier() != null ? order.getCommCarrier().getCarrierId() : null );
			
		}

		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_EXEC_CARRIER_ID)){
			return ( order.getExecCarrier() != null ? order.getExecCarrier().getCarrierId() : null );			
		}
		
		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SHIPPINGPOINT_ID)){
			return ( order.getShippingpoint() != null ? order.getShippingpoint().getShippingpointId() : null );			
		}

		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERY_TYPE_CD)){
			return ( order.getDeliveryType() != null ? order.getDeliveryType().getDeliveryTypeCd() : null );			
		}
		
		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_ORDER_STATUS_CD)){
			return ( order.getOrderStatus() != null ? order.getOrderStatus().getOrderStatusCd() : null );			
		}
		
		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SPI)){
			return ( order.getSpi() != null ? order.getSpi().getOrderSpiCd() : null );			
		}
		else if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_ITRACK_STATUS_CD)){
			return ( order.getItrackStatus() != null ? order.getItrackStatus().getItrackStatusCd() : null );
		}
		throw new LogonException("Unknown auditConfigCd specified for order level changes.[auditConfigCd=" + auditConfigCd + ";orderId=" + order.getOrderId() + "]", Reason.RES_ERROR_IMPL);
	}
	
	/**
	 * Returns the field value from the order item according to the audit config code specified.
	 * 
	 * @param auditConfigCd
	 * @param orderItem
	 * @return
	 */
	private static Object getFieldValueForAuditConfigCode(Long auditConfigCd, OrderItemEntity orderItem) {
		
		if ( auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_ORDERED_QUANTITY)) {
			return orderItem.getOrderedQuantity();

		}	
		
		else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_LOADED_QUANTITY)) {
			return orderItem.getLoadedQuantity();
			
		}
		
		else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_QUANTITY)) {
			return orderItem.getReturnedQuantity();

		}
		
		else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_REASON_CD)) {
			return 	( orderItem.getReturnOrderReason() != null ? orderItem.getReturnOrderReason().getReturnOrderReasonCd() : null );
		}
		
		else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERED_QUANTITY) ){
			return orderItem.getDeliveredQuantity();
		}
			
		else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_PRODUCTION_PLANT_CD)) {
			return ( orderItem.getProductionPlant() != null ? orderItem.getProductionPlant().getPlantCd() : null );
		}
		
		else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_ORDERED_UOM_CD)) {
			return ( orderItem.getOrderedUom() != null ? orderItem.getOrderedUom().getUomCd() : null );
		}
		
		else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_LOADED_UOM_CD)) {
			return ( orderItem.getLoadedUom() != null ? orderItem.getLoadedUom().getUomCd() : null );			
		}
		
		else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_UOM_CD)) {
			return ( orderItem.getReturnedUom() != null ? orderItem.getReturnedUom().getUomCd() : null );
		}
		
		else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERED_UOM_CD)) {
			return 	( orderItem.getDeliveredUom() != null ? orderItem.getDeliveredUom().getUomCd() : null );
		}
				
		else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_LOADINGPOINT_ID)) {
			return ( orderItem.getLoadingpoint() != null ? orderItem.getLoadingpoint().getLoadingpointId() : null );
		}
		
		else if (auditConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SHIPTO_SILO_ID)){
			return ( orderItem.getShiptoSilo() != null ? orderItem.getShiptoSilo().getShiptoSiloId() : null );
		}
		throw new LogonException("Unknown auditConfigCd specified for order item level changes.auditConfigCd=" + auditConfigCd + ";orderItemId=" + orderItem.getOrderItemId() + "]", Reason.RES_ERROR_IMPL);
	}


	/**
	 * Returns true in case comparing paramter o with to signs that there is a change in their value.
	 * 
	 * @param o
	 * @param to
	 * @return
	 */
	private static boolean checkChange(Object o, Object to) {

		boolean changed = false;

		if ( o instanceof Date || to instanceof Date ) {
			// TODO (emoke Mar 28, 2012):Check why this happens that for delivery from and to it depends, this should not be checked like that
			if ( o instanceof Timestamp ) {				
				changed = (o != null && to != null) && (!(((Timestamp)o).compareTo((Date) to) == 0 ));	
			}
			else if ( o instanceof Timestamp ) {
				changed = (o != null && to != null) && (!(((Date)o).compareTo((Timestamp) to) == 0 ));
			}	
		}
		else if (o instanceof String || to instanceof String ) {
			String strO = (String) o;
			strO = StringUtils.deleteWhitespace(strO);
			if ( StringUtils.isEmpty( strO ) ) {
				strO = null;
			}
			String strTo = (String) to;
			strTo = StringUtils.deleteWhitespace(strTo);
			if ( StringUtils.isEmpty( strTo ) ) {
				strTo = null;
			}
			changed = (strO != null && strTo != null) && (!strO.toString().equals( strTo.toString() ));
			return (( strO != null && strTo == null ) || (strO == null && strTo !=  null )) || changed;
		}
		else {
			changed = (o != null && to != null) && (!o.equals( to ));			
		}
		return (( o != null && to == null ) || (o == null && to !=  null )) || changed;

	}
	
	/**
	 * Returns a list of values from the predefined source where the bizevent audit type used to store the value.
	 * 
	 * Used for bulk retrieving these objects.
	 * 
	 * @param audits
	 * @return
	 */
	public static Set<Object> getValueListToResolve(final List<BizeventAuditDisplayEntity> audits, Long auditConfigCd) {
		
		if (isAuditBizeventValueImplicit(auditConfigCd)) {
			
			final Set<Object> valueSet = new HashSet<Object>();
			if (isAuditBizeventStoringId(auditConfigCd)) {
				for (BizeventAuditDisplayEntity audit : audits) {
					valueSet.add(audit.getNewValueId());
					valueSet.add(audit.getOldValueId());
				}
			}
			else if (isAuditBizeventStoringString(auditConfigCd)) {
				for (BizeventAuditDisplayEntity audit : audits) {
					valueSet.add(audit.getNewValueStr());
					valueSet.add(audit.getOldValueStr());
				}
			}
			valueSet.remove(null);
			return valueSet;
		}
		/**
		 * For others there is no need to resolve anything.
		 */
		return null;
	}
	
	/**
	 * @param valueDisplay
	 * @return
	 */
	public static String getDisplayText(final Object valueDisplay, Long auditConfigBsCd) {
		if ( valueDisplay == null ) {
			return null;
		}
		if (auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_ORDER_STATUS_CD)) {
			OrderStatusTxEntity tx = (OrderStatusTxEntity) valueDisplay;
			return tx.getTxt();
		}
		else if ( auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SHIPTO_SAP_BP_ID) ||
				auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SOLDTO_SAP_BP_ID) ||
				auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_BILLTO_SAP_BP_ID)) {
			SapBpEntity tx = (SapBpEntity) valueDisplay;
			return tx.getName1();
		}
		else if (auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_POOL_ID)) {
			PoolTxEntity tx = (PoolTxEntity) valueDisplay;
			return tx.getName();
		}
		else if ( auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID) ||
				auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_EXEC_CARRIER_ID )) {
			CarrierEntity tx = (CarrierEntity) valueDisplay;
			return tx.getDisplayName();
		}
		else if (auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SHIPPINGPOINT_ID)) {
			ShippingpointEntity tx = (ShippingpointEntity) valueDisplay;
			return tx.getShippingpointName();
		}
		else if (auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERY_TYPE_CD)) {
			
			DeliveryTypeTxEntity tx = (DeliveryTypeTxEntity) valueDisplay;
			return tx.getTxt();
		}
		else if (auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_PRODUCTION_PLANT_CD)) {
			
			PlantBsEntity tx = (PlantBsEntity) valueDisplay;
			return tx.getPlantName();
		}
		else if ( auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_ORDERED_UOM_CD) ||
				auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_LOADED_UOM_CD) ||
				auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_UOM_CD) ||
				auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERED_UOM_CD) ) {
			
			UomTxEntity tx = (UomTxEntity) valueDisplay;
			return tx.getTxt();
		}
		else if (auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_LOADINGPOINT_ID)) {
			LoadingpointEntity tx = (LoadingpointEntity) valueDisplay;
			return tx.getLoadingpointName();
		}
		else if (auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SHIPTO_SILO_ID)) {
			ShiptoSiloEntity tx = (ShiptoSiloEntity) valueDisplay;
			return tx.getSiloName();
		}
		
		else if (auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_REASON_CD)) {
			ReturnOrderReasonTxEntity tx = (ReturnOrderReasonTxEntity) valueDisplay;
			return tx.getTxt();
		}
		
		else if (auditConfigBsCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_SPI)) {
			OrderSpiTxEntity tx = (OrderSpiTxEntity) valueDisplay;
			return tx.getTxt();
		}
		return null;
	}
	/**
	 * Returns the group of config codes, which are ment to be handled grouped.
	 * @param auditConfigBsCd
	 * @return
	 */
	public static BizeventAuditConfiguration.AUDIT_GROUP getGroupOfAuditConfigCd(Long auditConfigBsCd) {
		
		for (BizeventAuditConfiguration.AUDIT_GROUP groupedAudit : BizeventAuditConfiguration.AUDIT_GROUP.values()) {
			
			if ( groupedAudit.getAuditCodes().contains(auditConfigBsCd) ) {
				return groupedAudit;
			}
		}
		return null;
	}
	

	/**
	 * Creates a new BizeventAuditEntity in case the values have changed.
	 * @param bizeventAuditList
	 * @param o will be used as old value
	 * @param to will be used as new value
	 * @param findAuditBizeventConfigBsEntity  
	 */
	private static BizeventAuditEntity createNewAuditBizeventIfChanged(
			final Object o, final Object to, AuditBizeventConfigBsEntity findAuditBizeventConfigBsEntity) {
		// [[
		BizeventAuditEntity result = null;
		Object[][] params = {{"o", o},
							{"to", to},
							{"findAuditBizeventConfigBsEntity", findAuditBizeventConfigBsEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(BizeventAuditUtil.class.getName(), "createNewAuditBizeventIfChanged", params);
		}
		// ]]
		try {
			if ( o == null && to == null ) {
				return result = null;
			}
			
			final boolean createAudit = checkChange(o, to);
			
			if ( createAudit ){
				BizeventAuditEntity bizeventAudit = createAuditBizevent(findAuditBizeventConfigBsEntity, o, to);
				return result = bizeventAudit;
			}
			return result = null;
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
	 * Returns bizevent audits for the order and its entities. Currently there is no reference to either
	 * of the order or the item which causes the audit, as it was not a requirement.
	 * 
	 * @param order
	 * @param dborder
	 * @return
	 */
	public static List<BizeventAuditEntity> getOrderAudits(final OrderEntity order, OrderEntity dborder) {
		final List<BizeventAuditEntity>bizeventAuditList = new ArrayList<BizeventAuditEntity>();
		final List<BizeventAuditEntity> bizeventAuditOrderList = getAuditBizeventsForOrder(order, dborder);
		bizeventAuditList.addAll(bizeventAuditOrderList);
		
		final Map<Long, OrderItemEntity> dbOrderItemsById = new HashMap<Long, OrderItemEntity>();
		
		List<OrderItemEntity> dbOrderItems = dborder.getOrderItems();
		for (OrderItemEntity dbOrderItem : dbOrderItems) {
			Long dbOrderItemId = dbOrderItem.getOrderItemId();
			dbOrderItemsById.put(dbOrderItemId, dbOrderItem);
		}
		
		for (OrderItemEntity orderItem : order.getOrderItems()) {
			Long orderItemId = orderItem.getOrderItemId();
			final OrderItemEntity dbOrderItem = dbOrderItemsById.get(orderItemId);
			if (dbOrderItem != null) {
				final List<BizeventAuditEntity> bizeventAuditOrderItemList = getAuditBizeventsForItem(
						orderItem, dbOrderItem);
				bizeventAuditList.addAll(bizeventAuditOrderItemList);
			}
		}
		
		return bizeventAuditList;
	}
	

	/**
     * Returns a list of {@link BizeventAuditEntity} created according to the changes
	 * in the {@link OrderEntity}
	 * 
	 * @param order the entitys new state which needs to be compared to the db state
	 * @param dbOrder the entity state in the db
	 * @return
	 */
	private static List <BizeventAuditEntity> getAuditBizeventsForOrder(final OrderEntity order,	final OrderEntity dbOrder) {
		final List<BizeventAuditEntity> bizeventAuditList = new ArrayList<BizeventAuditEntity>();
		
		for (BizeventAuditConfiguration.ORDER_AUDITS auditConfig : BizeventAuditConfiguration.ORDER_AUDITS.values()) {
			Long auditConfigCd = auditConfig.getAuditBizeventConfigId();
			
			final Object o = getFieldValueForAuditConfigCode(auditConfigCd, dbOrder);
			final Object to = getFieldValueForAuditConfigCode(auditConfigCd, order);
			
			AuditBizeventConfigBsEntity bs = new AuditBizeventConfigBsEntity();
			bs.setAuditBizeventConfigCd(auditConfigCd);
			
			final BizeventAuditEntity newAuditBizeventIfChanged = createNewAuditBizeventIfChanged( o, to, bs);
			if ( newAuditBizeventIfChanged != null ) {						
				bizeventAuditList.add(newAuditBizeventIfChanged);
			}
		}
		
		
		/**
		 * Go through all audited fields, check if the value has changed or not, and create
		 * an BizeventAuditEntity if there was a change, or member of audited groups have changed.
		 * 
		 */

		final Set<Long> alreadyAddedAuditCodes = new HashSet<Long>();
		for (BizeventAuditEntity bae : bizeventAuditList) {
			alreadyAddedAuditCodes.add(bae.getAuditBizeventConfig().getAuditBizeventConfigCd());
		}
		
		for (Long auditConfigCd : alreadyAddedAuditCodes) {
			
			/**
			 * Auditing of certain fields alone is meaningless, they are considered to be audited as groups. If a part of
			 * the group changes, then values of all the fields have to be stored.
			 * 
			 * Example:
			 * 
			 * Quantity changed from 1 to 2 in the ordered quantity field, but it is not known what was the value
			 * of the ordered unit of measure at the time of the change.
			 *  
			 * Reconstructing the value from the bizevent audit is not possible, as not all changes to the ordered uom, ordered quantity are
			 * audited, the reconstruction might fail, or mislead the user -> the value must be stored in order to be able to know exactly 
			 * the currentl value later on.
			 * 
			 */
			final BizeventAuditConfiguration.AUDIT_GROUP groupOfAuditConfigCd = getGroupOfAuditConfigCd(auditConfigCd);
			
			if ( groupOfAuditConfigCd != null ) {
				
				for ( Long boundCode : groupOfAuditConfigCd.getAuditCodes() ) {
					
					if ( !alreadyAddedAuditCodes.contains(boundCode) ) {
						
						BizeventAuditEntity bizeventAudit = createNewBizeventAudit(order,
								dbOrder, boundCode);
						bizeventAuditList.add(bizeventAudit);
					}
				}
			}
		}
		return bizeventAuditList;
	}

	/**
     * Returns a list of {@link BizeventAuditEntity} created according to the changes
	 * in the {@link OrderItemEntity}
	 * 
	 * @param item the entitys new state which needs to be compared to the db state
	 * @param dbItem the entity state in the db
	 * @return
	 */
	private static List<BizeventAuditEntity> getAuditBizeventsForItem(final OrderItemEntity item, final OrderItemEntity dbItem) {
		
		final List <BizeventAuditEntity> bizeventAuditList = new ArrayList<BizeventAuditEntity>();

		for (BizeventAuditConfiguration.ITEM_AUDITS auditConfig : BizeventAuditConfiguration.ITEM_AUDITS.values()) {
			Long auditConfigCd = auditConfig.getAuditBizeventConfigId();			
			
			final Object o = getFieldValueForAuditConfigCode(auditConfigCd, dbItem);
			final Object to = getFieldValueForAuditConfigCode(auditConfigCd, item);
						
			AuditBizeventConfigBsEntity bs = new AuditBizeventConfigBsEntity();
			bs.setAuditBizeventConfigCd(auditConfigCd);
			
			final BizeventAuditEntity newAuditBizeventIfChanged = createNewAuditBizeventIfChanged( o, to, bs);
			if ( newAuditBizeventIfChanged != null ) {						
				bizeventAuditList.add(newAuditBizeventIfChanged);
			}
		}
		
		/**
		 * Go through all audited fields, check if the value has changed or not, and create
		 * an BizeventAuditEntity if there was a change, or member of audited groups have changed.
		 * 
		 */
		final Set<Long> alreadyAddedAuditCodes = new HashSet<Long>();
		for (BizeventAuditEntity bae : bizeventAuditList) {
			alreadyAddedAuditCodes.add(bae.getAuditBizeventConfig().getAuditBizeventConfigCd());
		}		
		
		for (Long auditConfigCd : alreadyAddedAuditCodes) {
			
			/**
			 * Auditing of certain fields alone is meaningless, they are considered to be audited as groups. If a part of
			 * the group changes, then values of all the fields have to be stored.
			 * 
			 * Example:
			 * 
			 * Quantity changed from 1 to 2 in the ordered quantity field, but it is not known what was the value
			 * of the ordered unit of measure at the time of the change.
			 *  
			 * Reconstructing the value from the bizevent audit is not possible, as not all changes to the ordered uom, ordered quantity are
			 * audited, the reconstruction might fail, or mislead the user -> the value must be stored in order to be able to know exactly 
			 * the currentl value later on.
			 * 
			 */
			final BizeventAuditConfiguration.AUDIT_GROUP groupOfAuditConfigCd = getGroupOfAuditConfigCd(auditConfigCd);
			
			if ( groupOfAuditConfigCd != null ) {
				
				for ( Long boundCode : groupOfAuditConfigCd.getAuditCodes() ) {
					
					if ( !alreadyAddedAuditCodes.contains(boundCode) ) {
						
						BizeventAuditEntity bizeventAudit = createNewBizeventAudit(item,
								dbItem, boundCode);
						bizeventAuditList.add(bizeventAudit);
					}
				}
			}
		
		}
		return bizeventAuditList;
	}

	/**
	 * @param order
	 * @param dbOrder
	 * @param auditConfigCode
	 * @return
	 */
	private static BizeventAuditEntity createNewBizeventAudit(OrderEntity order, OrderEntity dbOrder,
			Long auditConfigCode) {
		// [[
		BizeventAuditEntity result = null;
		Object[][] params = {{"object", order},
							{"dbObj", dbOrder},
							{"auditConfigCode", auditConfigCode}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(BizeventAuditUtil.class.getName(), "createNewBizeventAudit", params);
		}
		// ]]
		try {
			final Object o = getFieldValueForAuditConfigCode(auditConfigCode, dbOrder);
			final Object to = getFieldValueForAuditConfigCode(auditConfigCode, order);
			final AuditBizeventConfigBsEntity bs = new AuditBizeventConfigBsEntity();
			bs.setAuditBizeventConfigCd(auditConfigCode);
			
			return result = createAuditBizevent(bs, o, to);
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
	 * @param item
	 * @param dbItem
	 * @param auditConfigCode
	 * @return
	 */
	private static BizeventAuditEntity createNewBizeventAudit(OrderItemEntity item, OrderItemEntity dbItem,
			Long auditConfigCode) {
		// [[
		BizeventAuditEntity result = null;
		Object[][] params = {{"object", item},
							{"dbObj", dbItem},
							{"auditConfigCode", auditConfigCode}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(BizeventAuditUtil.class.getName(), "createNewBizeventAudit", params);
		}
		// ]]
		try {
			final Object o = getFieldValueForAuditConfigCode(auditConfigCode, dbItem);
			final Object to = getFieldValueForAuditConfigCode(auditConfigCode, item);
			final AuditBizeventConfigBsEntity bs = new AuditBizeventConfigBsEntity();
			bs.setAuditBizeventConfigCd(auditConfigCode);
			
			return result = createAuditBizevent(bs, o, to);
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
	 * Adds the mandatory bizevent audit if it is not already present amongst the audit bizevents.
	 * Currently mandatory audits belong to order.
	 * 
	 * @param bizeventTypeCd
	 * @param order
	 * @param dborder
	 * @param audits
	 */
	public static void addMandatoryBizeventAudit(Long bizeventTypeCd, OrderEntity order, OrderEntity dborder, List<BizeventAuditEntity> audits) {
		
		final Long saveFieldValueForBizevent = BizeventAuditUtil.getStoredFieldCode(bizeventTypeCd);
		
		if (saveFieldValueForBizevent != null) {

			//these are the audits which are there because they were changed, or were supposed to be stored because they are ment to be saved together with a changed value
			final Map<Long, BizeventAuditEntity> auditsToSave = new HashMap<Long, BizeventAuditEntity> ();
			for (BizeventAuditEntity audit : audits) {
				auditsToSave.put(audit.getAuditBizeventConfig().getAuditBizeventConfigCd(), audit);
			}
			
			if (!auditsToSave.containsKey(saveFieldValueForBizevent)) {

				final BizeventAuditEntity mandatoryAuditBizevent = createNewBizeventAudit(order, dborder, saveFieldValueForBizevent);
				audits.add(mandatoryAuditBizevent);

			}
		}
	}
	
}
