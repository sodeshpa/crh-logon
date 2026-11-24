/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.pages;

import org.junit.Ignore;

/**
 * @author AdNovum Informatik AG
 */
@Ignore
public abstract class TestProperties {

	/**
	 * The enumeration test properties key for shipping point page
	 */
	public enum ShippingPointKey implements PropertyKey {
		PLANT_NAME("plantName"),
		SEARCH_SHIPPING_POINT_NAME("searchShippingPointName"),
		SHIPPING_POINT_SAP_NO("shippingPointSapNo"),
		SHIPPING_POINT_NAME("shippingPointName"),
		SHIPPING_POINT_COMMENT("shippingPointComment"),
		COORD_LON("coordLon"),
		COORD_LAT("coordLat"),
		SHIPPING_POINT_TYPE("shippingPointType"),
		SHIPPING_POINT_CONDITION("shippingPointCondition"),
		WEIGHT_SYSTEM_SP_CD_LABEL("weightSystemSpCdLabel"),
		LDS_SERVER("ldsServer"),
		GEOFENCE_RADIUS("geofenceRadius"),
		LOADING_POINT_NAME("loadingPointName"),
		LOADING_TIME("loadingTime"),
		LOADING_POINT_COMMENT("loadingPointComment"),
		LOADING_POINT_OBJECT_LABEL("loadingPointObjectLabel"),
		DELIVERY_NOTE_PRINTER_LABEL("deliveryNotePrinterLabel"),
		PRINTER_LABEL("printerLabel"),
		DELIVERY_NOTE_COPIES("deliveryNoteCopies"),
		PACKAGE_TYPE_CD_LABEL("packageTypeCdLabel"),
		WEIGHT_SYSTEM_LP_CD_LABEL("weightSystemLpCdLabel"),
		RAIL_ARRIVAL_WARNING_MIN("railArrivalWarningMin");

		private String keyString;

		private ShippingPointKey(String keyString) {
			this.keyString = keyString;
		}

		/* (non-Javadoc)
		 * @see PropertyKey#getKeyString()
		 */
		@Override
		public String getKeyString() {
			return keyString;
		}
	}

	/**
	 * The enumeration test properties key for truck page
	 */
	public enum TruckPageKey implements PropertyKey {
		CARRIER("carrier"),
		CHARACTERISTIC("characteristic"),
		SAP_NUMBER("sapNumber"),
		PLATE_NUMBER("plateNumber"),
		TRUCK_NAME("truckName"),
		TARE_WEIGHT("tareWeight"),
		PRIORITY("priority"),
		CATEGORY("category"),
		TYPE("type"),
		HAS_PDA("hasPDA"),
		MAX_GROSS_WEIGHT("maxGrossWeight"),
		POOL("pool");

		String keyString;

		TruckPageKey(String keyString) {
			this.keyString = keyString;
		}

		public String getKeyString() {
			return this.keyString;
		}
	}

	/**
	 * The enumeration test properties key for order page
	 */
	public enum OrderPageKey implements PropertyKey {
		// for input
		SHIPTO("shipto"),
		SAP_CONTRACT_NUMBER("sapContractNumber"),
		BILLTO("billto"),
		BILLTO_NAME("billtoName"),
		SOLDTO("soldto"),
		PAYER("payer"),
		SAREA_NAME("sareaText"),
		SAP_CONTRACT_ITEM_NUMBER("sapContractItemNo"),
		MATERIAL_NAME("materialName"),
		DELIVERY_NAME("deliveryTypeName"),
		PRODUCTION_PLANT_CD("productionPlantCd"),
		SUPPLY_PLANT_CD("supplyPlantCd"),
		QUANTITY("quantity"),
		UOM_TXT("uomTxt"),
		SHIPPINGPOINT_NAME("shippingpointName"),
		LOADINGPOINT_NAME("loadingpointName"),
		POOL_ID("poolId"),
		POOL_NAME("pool"),
		CARRIER_ID("carrier"),
		CARRIER_NAME("carrierName"), // for Commercial Carrier
		EXECUTING_CARRIER_NAME("executingCarrierName"), // for Executing Carrier
		DELIVERY_FROM_TIME("deliveryFromTime"),
		DELIVERY_TO_TIME("deliveryToTime"),
		IS_ON_HOLD_ORDER("isOnHoldOrder"),

		// for output
		CUST_ORDER_NO("custOrderNo"),
		ORDER_NO("orderNo"),
		DELIVERY_FROM_DATE("deliveryFromDate"),
		DELIVERY_TO_DATE("deliveryToDate"),
		AUTOMATIC_RELEASE("automaticRelease");

		String keyString;

		OrderPageKey(String keyString) {
			this.keyString = keyString;
		}

		public String getKeyString() {
			return this.keyString;
		}
	}

	/**
	 * The enumeration test properties key for scheduling page
	 */
	public enum SchedulePageKey implements PropertyKey {
		POOL_NAME("pool"),
		CARRIER_NAME("carrierName"),
		CARRIER_NAME2("carrierName2"),
		SCHEDULING_CARRIER_NAME_1("schedulingCarrierName1"),
		TRUCK_ID_FOR_CARRIER_NAME_1("truckIdForCarrierName1"),
		TRUCK_ID_FOR_CARRIER_NAME_2("truckIdForCarrierName2"),
		SCHEDULING_CARRIER_NAME_2("schedulingCarrierName2"),
		DECLINE_REASON("declineReason"),
		TRUCK_ID1("truckId1"),
		TRUCK_ID2("truckId2"),
		DAY("day"),
		HOUR_SPAN("hourSpan"),
		DISPLAY("display"),
		TIME_GRID("timeGrid");

		String keyString;

		SchedulePageKey(String keyString) {
			this.keyString = keyString;
		}

		public String getKeyString() {
			return this.keyString;
		}
	}

	/**
	 * The enumeration test properties key for external order form
	 */
	public enum ExternalOrderKey implements PropertyKey {
		POOL_NAME("pool"),
		CARRIER_ID("carrierId"),
		SHIPPING_POINT_ID("shippingpointId"),
		MATERIAL("material"),
		SHIPTO("shipto"),
		QUANTITY("quantity"),
		UOM("uomTxt"),
		FROM_DATE("fromDate"),
		TRANSMITTED("transmitted"),
		ORDER_NO("orderNr"),
		EDIT_QUANTITY("editQuantity"),
		LOADED_QUANTITY("loadedQuantity");

		String keyString;

		private ExternalOrderKey(String keyString) {
			this.keyString = keyString;
		}

		public String getKeyString() {
			return this.keyString;
		}
	}

	/**
	 * The enumeration test properties key for scheduling page
	 */
	public enum LoadingPageKey implements PropertyKey {
		LOADING_POINT("loadingPoint"),
		TARE_WEIGHT("tareWeight"),
		NET_QUANTITY("netQuantity"),
		UNIT_OF_MEASURE("unitOfMeasure");

		String keyString;

		LoadingPageKey(String keyString) {
			this.keyString = keyString;
		}

		public String getKeyString() {
			return this.keyString;
		}
	}

}
