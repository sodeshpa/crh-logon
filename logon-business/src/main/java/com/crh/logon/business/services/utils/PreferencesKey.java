/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import com.crh.logon.persistence.entities.admin.PreferencesEntity;

/**
 * Identifies possible keys for the <code>PREFERENCE_VALUE</code> column from
 * the <code>TLGNU_PREFERENCES</code> database table.
 *
 * @see PreferencesEntity
 *
 * @author AdNovum Informatik AG
 */
public enum PreferencesKey {

	/* -- Fields of Order Search screen -- */
	/** Content of Live field on Order Monitoring screen. Boolean. */
	SEARCH_LIVE_FLAG,
	/** Content of Delivery From field on Order Search screen. Value type: Date. */
	SEARCH_DELIVERY_FROM_DATE,
	/** Content of Delivery To field on Order Search screen. Value type: Date. */
	SEARCH_DELIVERY_TO_DATE,
	/** Content of Order status field on Order Search screen. Value type: String. */
	SEARCH_ORDER_STATUS_LIST,
	/** Content of Type field on Order Search screen. Value type: String. */
	SEARCH_DESTINATION_CD_LIST,
	/** Content of Mat. Packaging Type field. Value type: String. */
	SEARCH_MAT_PACKAGING_TYPE,
	/** Content of Material field on Order Search screen. Value type: Long. */
	SEARCH_SAP_MATERIAL_ID,
	/** Content of Sales Area field on Order Search screen. Value type: Long. */
	SEARCH_SAREA_ID,
	/** Content of Sales Office field on Order Search screen. String. */
	SEARCH_SHIPTO_SOFFICE_CD,
	/** Content of Sales Group field on Order Search screen. String. */
	SEARCH_SHIPTO_SGROUP_CD,
	/** Content of Sold To field on Order Search screen. Long. */
	SEARCH_SOLDTO_SAP_BP_ID,
	/** Content of Ship To field on Order Search screen. Long. */
	SEARCH_SHIPTO_SAP_BP_ID,
	/** Content of Bill To field on Order Search screen Long. */
	SEARCH_BILLTO_SAP_BP_ID,
	/** Content of Pool field on Order Search screen. Long. */
	SEARCH_POOL_ID,
	/** Content of Commercial Carrier field on Order Search screen. Long. */
	SEARCH_CARRIER_ID_LIST,
	/** Content of Executing Carrier field on Order Search screen. Long. */
	SEARCH_EXEC_CARRIER_ID,
	/** Content (key) of Vehicle field on Order Search screen. String. */
	SEARCH_VEHICLE_COMPOSITE_ID,
	/** Content (displayed) of Vehicle field on Order Search screen. String. */
	SEARCH_VEHICLE_NAME,
	/** Content of Delivery Plant field on Order Search screen. String. */
	SEARCH_PLANT_CD_LIST,
	/** Content of Shipping Point field on Order Search screen. Long. */
	SEARCH_SHIPPINGPOINT_ID,
	/** Content of Transportation Mode field on Order Search screen. String. */
	SEARCH_DELIVERY_TYPE_CD_LIST,
	/** Content of Order Creation Type field on Order Search screen. String. */
	SEARCH_ORDER_CREATION_TYPE,
	/** Content of LDS Delivery Note Number field on Order Search screen. String. */
	SEARCH_LDS_DELIVERY_NOTE_NO,
	/** Content of LDS Delivery Note Number field on Order Search screen. String. */
	SEARCH_NOTE_ORDER_MGMT,
	/** Content of LDS Delivery Note Number field on Order Search screen. String. */
	SEARCH_NOTE_DELIVERY,
	/** Content of LDS Delivery Note Number field on Order Search screen. String. */
	SEARCH_NOTE_DISPATCHING,
	/** Content of LDS Delivery Note Number field on Order Search screen. String. */
	SEARCH_NOTE_TRUCK_DRIVER,
	/** Content of LDS Delivery Note Number field on Order Search screen. String. */
	SEARCH_NOTE_RAILWAY_DISP,
	/** Content of LDS Delivery Note Number field on Order Search screen. String. */
	SEARCH_ORDER_NUMBER_FROM_SEQ_USAGE,
	/** Content of LDS Delivery Note Number field on Order Search screen. String. */
	SEARCH_SHIPTO_ADDRESS,
	/** Content of LDS Delivery Note Number field on Order Search screen. String. */
	SEARCH_LOADING_TS,

	/* -- Table columns widths of Order Search screen -- */

	/* Columns of the table Order Search screen. Value types for all of them: Long. */
	SEARCH_W_ORDER_NUMBER,
	SEARCH_W_DISTRIB_DEST_CD,
	SEARCH_W_SOLDTO_NAME,
	SEARCH_W_SHIPTO_NAME,
	SEARCH_W_SHIPTO_CITY,
	SEARCH_W_STATUS,
	SEARCH_W_MATERIAL_TXT,
	SEARCH_W_ORDERED_QUANTITY,
	SEARCH_W_ORDERED_UOM_TXT,
	SEARCH_W_LOADED_QUANTITY,
	SEARCH_W_LOADED_UOM_TXT,
	SEARCH_W_RETURNED_QUANTITY,
	SEARCH_W_RETURNED_UOM_TXT,
	SEARCH_W_DELIVERED_QUANTITY,
	SEARCH_W_DELIVERED_UOM_TXT,
	SEARCH_W_DELIVERY_TYPE_TXT,
	SEARCH_W_SHIPPINGPOINT_NAME,
	SEARCH_W_DELIVERY_FROM_DATE,
	SEARCH_W_DELIVERY_TO_DATE,
	SEARCH_W_PLANNED_DEL_DATE,
	SEARCH_W_POOL_NAME,
	SEARCH_W_COMM_CARRIER_NAME,
	SEARCH_W_EXEC_CARRIER_NAME,
	SEARCH_W_BILLTO_NAME,
	SEARCH_W_DRIVER_FULL_NAME,
	SEARCH_W_VEHICLE_NAME,
	SEARCH_W_ORDER_VALID_CODE,
	SEARCH_W_LDS_DELIVERY_NOTE_NO,
	SEARCH_W_NOTE_ORDER_MGMT,
	SEARCH_W_NOTE_DELIVERY,
	SEARCH_W_NOTE_DISPATCHING,
	SEARCH_W_NOTE_TRUCK_DRIVER,
	SEARCH_W_NOTE_RAILWAY_DISP,
	SEARCH_W_ORDER_NUMBER_FROM_SEQ_USAGE,
	SEARCH_W_SHIPTO_ADDRESS,
	SEARCH_W_LOADING_TS,
	SEARCH_W_ITRACK_STATUS_CD,
	
	/* Indexes Columns of the table Order Search screen. Value types for all of them: Long. */
	SEARCH_I_ORDER_NUMBER,
	SEARCH_I_DISTRIB_DEST_CD,
	SEARCH_I_SOLDTO_NAME,
	SEARCH_I_SHIPTO_NAME,
	SEARCH_I_SHIPTO_CITY,
	SEARCH_I_STATUS,
	SEARCH_I_MATERIAL_TXT,
	SEARCH_I_ORDERED_QUANTITY,
	SEARCH_I_ORDERED_UOM_TXT,
	SEARCH_I_LOADED_QUANTITY,
	SEARCH_I_LOADED_UOM_TXT,
	SEARCH_I_RETURNED_QUANTITY,
	SEARCH_I_RETURNED_UOM_TXT,
	SEARCH_I_DELIVERED_QUANTITY,
	SEARCH_I_DELIVERED_UOM_TXT,
	SEARCH_I_DELIVERY_TYPE_TXT,
	SEARCH_I_SHIPPINGPOINT_NAME,
	SEARCH_I_DELIVERY_FROM_DATE,
	SEARCH_I_DELIVERY_TO_DATE,
	SEARCH_I_PLANNED_DEL_DATE,
	SEARCH_I_POOL_NAME,
	SEARCH_I_COMM_CARRIER_NAME,
	SEARCH_I_EXEC_CARRIER_NAME,
	SEARCH_I_BILLTO_NAME,
	SEARCH_I_DRIVER_FULL_NAME,
	SEARCH_I_VEHICLE_NAME,
	SEARCH_I_ORDER_VALID_CODE,
	SEARCH_I_LDS_DELIVERY_NOTE_NO,
	SEARCH_I_NOTE_ORDER_MGMT,
	SEARCH_I_NOTE_DELIVERY,
	SEARCH_I_NOTE_DISPATCHING,
	SEARCH_I_NOTE_TRUCK_DRIVER,
	SEARCH_I_NOTE_RAILWAY_DISP,
	SEARCH_I_ORDER_NUMBER_FROM_SEQ_USAGE,
	SEARCH_I_SHIPTO_ADDRESS,
	SEARCH_I_LOADING_TS,
	SEARCH_I_ITRACK_STATUS_CD,

	/** Content of Order status field on Delivery Monitoring/Monitoring List screen. Value type: String. */
	MONITORING_ORDER_STATUS_LIST,
	/** Content of Type field on Delivery Monitoring/Monitoring List screen. Value type: String. */
	MONITORING_DESTINATION_CD_LIST,
	/** Content of Mat. Packaging Type field on Delivery Monitoring/Monitoring List screen. Value type: String. */
	MONITORING_MAT_PACKAGING_TYPE,
	/** Content of Material field on Delivery Monitoring/Monitoring List screen. Value type: Long. */
	MONITORING_SAP_MATERIAL_ID,
	/** Content of Sales Area field on Delivery Monitoring/Monitoring List screen. Value type: Long. */
	MONITORING_SAREA_ID,
	/** Content of Sales Office field on Delivery Monitoring/Monitoring List screen. String. */
	MONITORING_SHIPTO_SOFFICE_CD,
	/** Content of Sales Group field on Delivery Monitoring/Monitoring List screen. String. */
	MONITORING_SHIPTO_SGROUP_CD,
	/** Content of Sold To field on Delivery Monitoring/Monitoring List screen. Long. */
	MONITORING_SOLDTO_SAP_BP_ID,
	/** Content of Ship To field on Delivery Monitoring/Monitoring List screen. Long. */
	MONITORING_SHIPTO_SAP_BP_ID,
	/** Content of Bill To field on Delivery Monitoring/Monitoring List screen Long. */
	MONITORING_BILLTO_SAP_BP_ID,
	/** Content of Live field on Order Monitoring screen. Boolean. */
	MONITORING_LIVE_FLAG,
	/** Content of Pool field on Delivery Monitoring/Monitoring List screen. Long. */
	MONITORING_POOL_ID,
	/** Content of Commercial Carrier field on Delivery Monitoring/Monitoring List screen. Long. */
	MONITORING_CARRIER_ID_LIST,
	/** Content (key) of Vehicle field on Delivery Monitoring/Monitoring List screen. String. */
	MONITORING_VEHICLE_COMPOSITE_ID,
	/** Content (displayed) of Vehicle field on Delivery Monitoring/Monitoring List screen. String. */
	MONITORING_VEHICLE_NAME,
	/** Content of Delivery Plant field on Delivery Monitoring/Monitoring List screen. String. */
	MONITORING_PLANT_CD_LIST,
	/** Content of Shipping Point field on Delivery Monitoring/Monitoring List screen. Long. */
	MONITORING_SHIPPINGPOINT_ID,
	/** Content of Transportation Mode field on Delivery Monitoring/Monitoring List screen. String. */
	MONITORING_DELIVERY_TYPE_CD_LIST,

	/* -- Table columns widths of Delivery Monitoring/Monitoring List screen -- */

	/* Columns of the table Delivery Monitoring/Monitoring List screen. Value types for all of them: Long. */
	MONITORING_W_TIME_TO_REDLINE,
	MONITORING_W_ORDER_NUMBER,
	MONITORING_W_DISTRIB_DEST_CD,
	MONITORING_W_SOLDTO_NAME,
	MONITORING_W_SHIPTO_NAME,
	MONITORING_W_SHIPTO_CITY,
	MONITORING_W_STATUS,
	MONITORING_W_MATERIAL_TXT,
	MONITORING_W_ORDERED_QUANTITY,
	MONITORING_W_ORDERED_UOM_TXT,
	MONITORING_W_LOADED_QUANTITY,
	MONITORING_W_LOADED_UOM_TXT,
	MONITORING_W_RETURNED_QUANTITY,
	MONITORING_W_RETURNED_UOM_TXT,
	MONITORING_W_DELIVERED_QUANTITY,
	MONITORING_W_DELIVERED_UOM_TXT,
	MONITORING_W_DELIVERY_TYPE_TXT,
	MONITORING_W_SHIPPINGPOINT_NAME,
	MONITORING_W_DELIVERY_FROM_DATE,
	MONITORING_W_DELIVERY_TO_DATE,
	MONITORING_W_PLANNED_DEL_DATE,
	MONITORING_W_POOL_NAME,
	MONITORING_W_COMM_CARRIER_NAME,
	MONITORING_W_EXEC_CARRIER_NAME,
	MONITORING_W_BILLTO_NAME,
	MONITORING_W_DRIVER_FULL_NAME,
	MONITORING_W_VEHICLE_NAME,
	MONITORING_W_ORDER_VALID_CODE,

	/* Column indexes of monitoring list table */
	MONITORING_I_TIME_TO_REDLINE,
	MONITORING_I_ORDER_NUMBER,
	MONITORING_I_DISTRIB_DEST_CD,
	MONITORING_I_SOLDTO_NAME,
	MONITORING_I_SHIPTO_NAME,
	MONITORING_I_SHIPTO_CITY,
	MONITORING_I_STATUS,
	MONITORING_I_MATERIAL_TXT,
	MONITORING_I_ORDERED_QUANTITY,
	MONITORING_I_ORDERED_UOM_TXT,
	MONITORING_I_LOADED_QUANTITY,
	MONITORING_I_LOADED_UOM_TXT,
	MONITORING_I_RETURNED_QUANTITY,
	MONITORING_I_RETURNED_UOM_TXT,
	MONITORING_I_DELIVERED_QUANTITY,
	MONITORING_I_DELIVERED_UOM_TXT,
	MONITORING_I_DELIVERY_TYPE_TXT,
	MONITORING_I_SHIPPINGPOINT_NAME,
	MONITORING_I_DELIVERY_FROM_DATE,
	MONITORING_I_DELIVERY_TO_DATE,
	MONITORING_I_PLANNED_DEL_DATE,
	MONITORING_I_POOL_NAME,
	MONITORING_I_COMM_CARRIER_NAME,
	MONITORING_I_EXEC_CARRIER_NAME,
	MONITORING_I_BILLTO_NAME,
	MONITORING_I_DRIVER_FULL_NAME,
	MONITORING_I_VEHICLE_NAME,
	MONITORING_I_ORDER_VALID_CODE,

/* -- Fields of External Order Search screen -- */

	/** Content of Delivery From field on External Order Search screen. Value type: Date. */
	EXTSEARCH_DELIVERY_FROM_DATE,
	/** Content of Delivery To field on External Order Search screen. Value type: Date. */
	EXTSEARCH_DELIVERY_TO_DATE,
	/** Content of Order status field on External Order Search screen. Value type: String. */
	EXTSEARCH_ORDER_STATUS_LIST,
	/** Content (key) of Vehicle field on External Order Search screen. String. */
	EXTSEARCH_VEHICLE_COMPOSITE_ID,
	/** Content (displayed) of Vehicle field on External Order Search screen. String. */
	EXTSEARCH_VEHICLE_NAME,
	/** Content of Delivery Note Comment/Number field on External Order Search screen. String. */
	EXTSEARCH_DELIVERY_NOTE_COMMENT_NUMBER,

	/* -- Table columns widths of External Order Search screen -- */

	/* Columns of the table External Order Search screen. Value types for all of them: Long. */
	EXTSEARCH_W_EXT_ORDER_NUMBER,
	EXTSEARCH_W_EXT_DELIVERY_NOTE,
	EXTSEARCH_W_SHIPTO_NAME,
	EXTSEARCH_W_SHIPTO_LOAD_UNLOAD,
	EXTSEARCH_W_SHIPTO_CONTACT_PERSON_TXT,

	EXTSEARCH_W_SHIPTO_STREET_HOUSE_NO,
	EXTSEARCH_W_SHIPTO_COUNTRY,
	EXTSEARCH_W_SHIPTO_POSTAL_CODE,
	EXTSEARCH_W_SHIPTO_CITY,
	EXTSEARCH_W_STATUS_TXT,

	EXTSEARCH_W_MATERIAL_NAME,
	EXTSEARCH_W_ORDERED_QUANTITY,
	EXTSEARCH_W_ORDERED_UOM_NAME,
	EXTSEARCH_W_DELIVERED_QUANTITY,
	EXTSEARCH_W_DELIVERED_UOM_NAME,

	EXTSEARCH_W_SHIPPINGPOINT_NAME,
	EXTSEARCH_W_SP_LOAD_UNLOAD,
	EXTSEARCH_W_SP_CONTACT_PERSON_TXT,
	EXTSEARCH_W_SP_STREET_HOUSE_NO,
	EXTSEARCH_W_SP_COUNTRY,

	EXTSEARCH_W_SP_POSTAL_CODE,
	EXTSEARCH_W_SP_CITY,
	EXTSEARCH_W_DELIVERY_FROM,
	EXTSEARCH_W_DELIVERY_TO,
	EXTSEARCH_W_POOL_NAME,

	EXTSEARCH_W_CARRIER_NAME,
	EXTSEARCH_W_TRUCK_NAME,

	/*Column indexes for external search table */

	EXTSEARCH_I_EXT_ORDER_NUMBER,
	EXTSEARCH_I_EXT_DELIVERY_NOTE,
	EXTSEARCH_I_SHIPTO_NAME,
	EXTSEARCH_I_SHIPTO_LOAD_UNLOAD,
	EXTSEARCH_I_SHIPTO_CONTACT_PERSON_TXT,

	EXTSEARCH_I_SHIPTO_STREET_HOUSE_NO,
	EXTSEARCH_I_SHIPTO_COUNTRY,
	EXTSEARCH_I_SHIPTO_POSTAL_CODE,
	EXTSEARCH_I_SHIPTO_CITY,
	EXTSEARCH_I_STATUS_TXT,

	EXTSEARCH_I_MATERIAL_NAME,
	EXTSEARCH_I_ORDERED_QUANTITY,
	EXTSEARCH_I_ORDERED_UOM_NAME,
	EXTSEARCH_I_DELIVERED_QUANTITY,
	EXTSEARCH_I_DELIVERED_UOM_NAME,

	EXTSEARCH_I_SHIPPINGPOINT_NAME,
	EXTSEARCH_I_SP_LOAD_UNLOAD,
	EXTSEARCH_I_SP_CONTACT_PERSON_TXT,
	EXTSEARCH_I_SP_STREET_HOUSE_NO,
	EXTSEARCH_I_SP_COUNTRY,

	EXTSEARCH_I_SP_POSTAL_CODE,
	EXTSEARCH_I_SP_CITY,
	EXTSEARCH_I_DELIVERY_FROM,
	EXTSEARCH_I_DELIVERY_TO,
	EXTSEARCH_I_POOL_NAME,

	EXTSEARCH_I_CARRIER_NAME,
	EXTSEARCH_I_TRUCK_NAME,

	/* -- Fields of Truck Scheduling screen -- */

	/** The index of the selected pool from the Pools field on Truck Scheduling screen. Long. */
	DISPO_POOL_INDEX,
	/** Content of the Carriers field on Truck Scheduling screen. String. */
	DISPO_CARRIER_ID_LIST,
	/** Content of the Time grid field on Truck Scheduling screen. Long. */
	DISPO_TIMEGRID,
	/** Content of the Range field on Truck Scheduling screen. Long. */
	DISPO_RANGE,
	/** Content of the Plants field on Truck Scheduling screen. String. */
	DISPO_PLANTS_CD_LIST,
	/** Content of the Delivery Status field on Truck Scheduling screen. String. */
	DISPO_ORDER_STATUS_CD_LIST,
	/** Value of the Display -> Hide planning trucks field on Truck Scheduling screen. Boolean. */
	DISPO_HIDE_PLANNING_TRUCKS,
	/** Value of the Display -> Hide real trucks field on Truck Scheduling screen. Boolean. */
	DISPO_HIDE_REAL_TRUCKS,
	/** Value of the Display -> Hide inactive trucks field on Truck Scheduling screen. Boolean. */
	DISPO_HIDE_INACTIVE_TRUCKS,
	/** Value of the Display -> Hide trucks without deliveries field on Truck Scheduling screen. Boolean. */
	DISPO_HIDE_TRUCKS_WITHOUT_DELIVERIES,
	/** Value of the Display -> Hide scheduled customer orders field on Truck Scheduling screen. Boolean. */
	DISPO_HIDE_SCHEDULED_CUSTORDERS,
	/** Value of the Display -> Hide closed customer orders field on Truck Scheduling screen. Boolean. */
	DISPO_HIDE_CLOSED_CUSTORDERS,
	/** Value of the Display -> Hide customer order header line field on Truck Scheduling screen. Boolean. */
	DISPO_HIDE_CUSTORDER_HEADERS,
	/** Value of the Visual field on Truck Scheduling screen. Long. */
	DISPO_VISUAL_FILTER,
	/** Value of the Display -> Hide executing trucks and orders on Truck Scheduling screen. Boolean. */
	DISPO_HIDE_TRUCKS_AND_ORDER_OF_PARTNERS,
	/** Value of the truck priority filter on Truck Scheduling screen. Long*/
	DISPO_TRUCK_PRIORITY_FILTER,
	/* -- Fields of Rail Scheduling screen -- */

	/** The code of the selected plant on Rail Scheduling screen. String. */
	RAILDISPO_PLANT_CD,
	/** The list of IDs of the selected shippingpoints on Rail Scheduling screen. list of Long. */
	RAILDISPO_SHIPPINGPOINT_ID_LIST,
	/** The list of track codes of the selected tracks on Rail Scheduling screen. list of Long. */
	RAILDISPO_TRACKS_PLANTTRACK_CD_LIST,
	/** The value of the "no track" field on Rail Scheduling screen. Boolean. */
	RAILDISPO_SHOW_NO_TRACK,

	/* -- Fields of Order Ship To search screen -- */
	/** The value of the active AGG contract field on order shipto search screen. Boolean. */
	ORDER_SHIPTO_ACTIVE_AGG_CONTRACT,
	/** The value of the active CEM contract field on order shipto search screen. Boolean. */
	ORDER_SHIPTO_ACTIVE_CEM_CONTRACT
	;
}
