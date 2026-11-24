/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.crh.logon.constants.Constant;

/**
 * @author AdNovum Informatik AG
 */
public class BizeventAuditConfiguration {
	
	final static Collection <BizeventAuditFieldConfiguration> fieldConfigurations = new ArrayList<BizeventAuditFieldConfiguration>();
	
	public static final int FIELD_TYPE_ID = 0;
	public static final int FIELD_TYPE_STRING = 1;
	public static final int FIELD_TYPE_CD = 2;
	public static final int FIELD_TYPE_DATE = 3;
	
	/**
	 * Main properties of the audit configs.
	 */
	public static enum ORDER_AUDITS implements BizeventAuditFieldConfiguration,Serializable{
		
		AUDIT_BIZEVENT_CONFIG_ORDER_STATUS_CD(Constant.AUDIT_BIZEVENT_CONFIG_ORDER_STATUS_CD,  FIELD_TYPE_ID, true),
		AUDIT_BIZEVENT_CONFIG_SHIPTO_SAP_BP_ID(Constant.AUDIT_BIZEVENT_CONFIG_SHIPTO_SAP_BP_ID,  FIELD_TYPE_ID, true),
		AUDIT_BIZEVENT_CONFIG_SOLDTO_SAP_BP_ID(Constant.AUDIT_BIZEVENT_CONFIG_SOLDTO_SAP_BP_ID,  FIELD_TYPE_ID, true),
		AUDIT_BIZEVENT_CONFIG_BILLTO_SAP_BP_ID(Constant.AUDIT_BIZEVENT_CONFIG_BILLTO_SAP_BP_ID,  FIELD_TYPE_ID, true),
		AUDIT_BIZEVENT_CONFIG_POOL_ID(Constant.AUDIT_BIZEVENT_CONFIG_POOL_ID,  FIELD_TYPE_ID, true),
		AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID(Constant.AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID,  FIELD_TYPE_ID, true),
		AUDIT_BIZEVENT_CONFIG_EXEC_CARRIER_ID(Constant.AUDIT_BIZEVENT_CONFIG_EXEC_CARRIER_ID,FIELD_TYPE_ID, true),
		AUDIT_BIZEVENT_CONFIG_SHIPPINGPOINT_ID(Constant.AUDIT_BIZEVENT_CONFIG_SHIPPINGPOINT_ID,  FIELD_TYPE_ID, true),
		AUDIT_BIZEVENT_CONFIG_DELIVERY_TYPE_CD(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERY_TYPE_CD,  FIELD_TYPE_CD, true),
		AUDIT_BIZEVENT_CONFIG_DELIVERY_FROM_DAT(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERY_FROM_DAT,  FIELD_TYPE_DATE, false),
		AUDIT_BIZEVENT_CONFIG_DELIVERY_TO_DAT(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERY_TO_DAT,  FIELD_TYPE_DATE, false),
		AUDIT_BIZEVENT_CONFIG_RELEASE_SAP_FLAG(Constant.AUDIT_BIZEVENT_CONFIG_RELEASE_SAP_FLAG,  FIELD_TYPE_STRING, false),
		AUDIT_BIZEVENT_CONFIG_NO_PDA_OR_RAIL_EVENT_FLAG(Constant.AUDIT_BIZEVENT_CONFIG_NO_PDA_OR_RAIL_EVENT_FLAG,  FIELD_TYPE_STRING,	false),
		AUDIT_BIZEVENT_CONFIG_FREE_DELIVERY_FLAG(Constant.AUDIT_BIZEVENT_CONFIG_FREE_DELIVERY_FLAG,  FIELD_TYPE_STRING, false),
		AUDIT_BIZEVENT_CONFIG_NOTE_ORDER_MGMT(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_ORDER_MGMT,  FIELD_TYPE_STRING, false),
		AUDIT_BIZEVENT_CONFIG_NOTE_DELIVERY(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_DELIVERY,  FIELD_TYPE_STRING, false),
		AUDIT_BIZEVENT_CONFIG_NOTE_DISPATCHING(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_DISPATCHING,  FIELD_TYPE_STRING, false),
		AUDIT_BIZEVENT_CONFIG_NOTE_TRUCK_DRIVER(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_TRUCK_DRIVER,  FIELD_TYPE_STRING, false),
		AUDIT_BIZEVENT_CONFIG_NOTE_RAILWAY_DISP(Constant.AUDIT_BIZEVENT_CONFIG_NOTE_RAILWAY_DISP,  FIELD_TYPE_STRING, false),
		AUDIT_BIZEVENT_CONFIG_CARRIER_DELIVERY_NOTE(Constant.AUDIT_BIZEVENT_CONFIG_CARRIER_DELIVERY_NOTE,  FIELD_TYPE_STRING, false),
		AUDIT_BIZEVENT_CONFIG_CARRIER_TRUCK_DRIVER_NOTE(Constant.AUDIT_BIZEVENT_CONFIG_CARRIER_TRUCK_DRIVER_NOTE,  FIELD_TYPE_STRING, false),
		AUDIT_BIZEVENT_CONFIG_CUST_DELIVERY_NOTE_COMMENT(Constant.AUDIT_BIZEVENT_CONFIG_CUST_DELIVERY_NOTE_COMMENT,  FIELD_TYPE_STRING, false),
		AUDIT_BIZEVENT_CONFIG_ON_HOLD_ORDER_AND_LOCKED_FLAG(Constant.AUDIT_BIZEVENT_CONFIG_ON_HOLD_ORDER_AND_LOCKED_FLAG,  FIELD_TYPE_STRING, false),
		AUDIT_BIZEVENT_CONFIG_SPI(Constant.AUDIT_BIZEVENT_CONFIG_SPI,  FIELD_TYPE_STRING, true),
		AUDIT_BIZEVENT_CONFIG_ITRACK_STATUS_CD(Constant.AUDIT_BIZEVENT_CONFIG_ITRACK_STATUS_CD,  FIELD_TYPE_CD, false);
		
		ORDER_AUDITS(long auditBizeventConfigId,  int fieldType, boolean needsResolution) {
			this.auditBizeventConfigId = auditBizeventConfigId;
			this.needsLanguageResolution = needsResolution;
			this.fieldType = fieldType;
		}
		
		private long auditBizeventConfigId;
		private boolean needsLanguageResolution;
		private int fieldType;
		/**
		 * The config cd of the audit.
		 * @return
		 */
		public long getAuditBizeventConfigId() {
			return auditBizeventConfigId;
		}
		/**
		 * Returns true in case the value stored in the db is not ment to be displayed
		 * as is, but is ment to be resolved to a language dependent string.
		 * @return
		 */
		public boolean isLanguageResolutionNeeded() {
			return needsLanguageResolution;
		}
		
		/**
		 * Returns a code, which can be resolved to the field type.
		 * 
		 * @return
		 */
		public int getFieldType() {
			return fieldType;
		}
	
	}
	
	public static enum ITEM_AUDITS implements BizeventAuditFieldConfiguration, Serializable{
		
		AUDIT_BIZEVENT_CONFIG_PRODUCTION_PLANT_CD(Constant.AUDIT_BIZEVENT_CONFIG_PRODUCTION_PLANT_CD,  FIELD_TYPE_CD, true),
		AUDIT_BIZEVENT_CONFIG_ORDERED_UOM_CD(Constant.AUDIT_BIZEVENT_CONFIG_ORDERED_UOM_CD,  FIELD_TYPE_CD,  true),
		AUDIT_BIZEVENT_CONFIG_ORDERED_QUANTITY(Constant.AUDIT_BIZEVENT_CONFIG_ORDERED_QUANTITY,  FIELD_TYPE_STRING,false),
		AUDIT_BIZEVENT_CONFIG_LOADED_UOM_CD(Constant.AUDIT_BIZEVENT_CONFIG_LOADED_UOM_CD,  FIELD_TYPE_CD, true),
		AUDIT_BIZEVENT_CONFIG_LOADED_QUANTITY(Constant.AUDIT_BIZEVENT_CONFIG_LOADED_QUANTITY,  FIELD_TYPE_STRING,false),
		AUDIT_BIZEVENT_CONFIG_RETURNED_UOM_CD(Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_UOM_CD,  FIELD_TYPE_CD, true),
		AUDIT_BIZEVENT_CONFIG_RETURNED_QUANTITY(Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_QUANTITY,  FIELD_TYPE_STRING,false),
		AUDIT_BIZEVENT_CONFIG_RETURNED_REASON_CD(Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_REASON_CD,  FIELD_TYPE_CD, true),
		AUDIT_BIZEVENT_CONFIG_DELIVERED_UOM_CD(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERED_UOM_CD,  FIELD_TYPE_CD, true),
		AUDIT_BIZEVENT_CONFIG_DELIVERED_QUANTITY(Constant.AUDIT_BIZEVENT_CONFIG_DELIVERED_QUANTITY,  FIELD_TYPE_STRING, false),
		AUDIT_BIZEVENT_CONFIG_LOADINGPOINT_ID(Constant.AUDIT_BIZEVENT_CONFIG_LOADINGPOINT_ID,  FIELD_TYPE_ID, true),
		AUDIT_BIZEVENT_CONFIG_SHIPTO_SILO_ID(Constant.AUDIT_BIZEVENT_CONFIG_SHIPTO_SILO_ID,  FIELD_TYPE_ID, true);
		
		ITEM_AUDITS(long auditBizeventConfigId,  int fieldType, boolean needsResolution) {
			this.auditBizeventConfigId = auditBizeventConfigId;
			this.needsLanguageResolution = needsResolution;
			this.fieldType = fieldType;
		}
		
		private long auditBizeventConfigId;
		private boolean needsLanguageResolution;
		private int fieldType;
		
		/**
		 * The config cd of the audit.
		 * @return
		 */
		public long getAuditBizeventConfigId() {
			return auditBizeventConfigId;
		}
		/**
		 * Returns true in case the value stored in the db is not ment to be displayed
		 * as is, but is ment to be resolved to a language dependent string.
		 * @return
		 */
		public boolean isLanguageResolutionNeeded() {
			return needsLanguageResolution;
		}
		
		/**
		 * Returns a code, which can be resolved to the field type.
		 * 
		 * @return
		 */
		public int getFieldType() {
			return fieldType;
		}
		
	}
	
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
	public static enum AUDIT_GROUP implements Serializable{
		
		ORDERED(0,Constant.AUDIT_BIZEVENT_CONFIG_ORDERED_QUANTITY, Constant.AUDIT_BIZEVENT_CONFIG_ORDERED_UOM_CD),
		LOADED(1, Constant.AUDIT_BIZEVENT_CONFIG_LOADED_QUANTITY, Constant.AUDIT_BIZEVENT_CONFIG_LOADED_UOM_CD),
		DELIVERED(2, Constant.AUDIT_BIZEVENT_CONFIG_DELIVERED_QUANTITY, Constant.AUDIT_BIZEVENT_CONFIG_DELIVERED_UOM_CD),
		RETURNED(3,Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_QUANTITY, Constant.AUDIT_BIZEVENT_CONFIG_RETURNED_UOM_CD),
		TRANSPORTEUR(4,Constant.AUDIT_BIZEVENT_CONFIG_POOL_ID, Constant.AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID);
		
		AUDIT_GROUP(int nr, Long... auditCodes) {
			this.nr = nr;
			this.auditCodes = Arrays.asList(auditCodes);
		}

		
		private int nr;
		private List<Long> auditCodes;
		
		public int getNr() {
			return nr;
		}
		public List<Long> getAuditCodes() {
			return auditCodes;
		}
	};
	
	/**
	 * Certain bizevents need to store the state of a field even if it has not changed. This enum is supposed to store
	 * the static rule for which bizevent which field is supposed to be stored anyway.
	 */
	public static enum SAVE_FIELD_STATUS_FOR_BIZEVENT implements Serializable{
		
		BIZEVENT_TYPE_POOL_ACK(BizEventUtil.BIZEVENT_TYPE_POOL_ACK.getBizeventTypeCd(), Constant.AUDIT_BIZEVENT_CONFIG_POOL_ID),
		BIZEVENT_TYPE_SET_POOL(BizEventUtil.BIZEVENT_TYPE_SET_POOL.getBizeventTypeCd(), Constant.AUDIT_BIZEVENT_CONFIG_POOL_ID),
		
		BIZEVENT_TYPE_SET_COMM_CARRIER(BizEventUtil.BIZEVENT_TYPE_SET_COMM_CARRIER.getBizeventTypeCd(), Constant.AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID),
		BIZEVENT_TYPE_COMM_CARRIER_REJECTS(BizEventUtil.BIZEVENT_TYPE_COMM_CARRIER_REJECTS.getBizeventTypeCd(), Constant.AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID),
		BIZEVENT_TYPE_COMM_CARRIER_ACK(BizEventUtil.BIZEVENT_TYPE_COMM_CARRIER_ACK.getBizeventTypeCd(), Constant.AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID),
		BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER(BizEventUtil.BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER.getBizeventTypeCd(), Constant.AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID),
		
		BIZEVENT_TYPE_SET_EXEC_CARRIER(BizEventUtil.BIZEVENT_TYPE_SET_EXEC_CARRIER.getBizeventTypeCd(), Constant.AUDIT_BIZEVENT_CONFIG_EXEC_CARRIER_ID),
		BIZEVENT_TYPE_EXEC_CARRIER_REJECTS(BizEventUtil.BIZEVENT_TYPE_EXEC_CARRIER_REJECTS.getBizeventTypeCd(), Constant.AUDIT_BIZEVENT_CONFIG_EXEC_CARRIER_ID);
		
		private Long bizeventTypeCd;
		private Long auditCode;

		SAVE_FIELD_STATUS_FOR_BIZEVENT(Long bizeventTypeCd, Long auditCode) {
			this.bizeventTypeCd = bizeventTypeCd;
			this.auditCode = auditCode;
		}

		public Long getBizeventTypeCd() {
			return bizeventTypeCd;
		}

		public Long getAuditCode() {
			return auditCode;
		}

		
	};
	
	static {
		
		final List <ORDER_AUDITS> orderFieldConfigs = Arrays.asList(BizeventAuditConfiguration.ORDER_AUDITS.values());
		final List <ITEM_AUDITS> itemFieldConfigs = Arrays.asList(BizeventAuditConfiguration.ITEM_AUDITS.values());
		
		fieldConfigurations.addAll(orderFieldConfigs);
		fieldConfigurations.addAll(itemFieldConfigs);
	}
	
}
