/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.constants;

import java.io.Serializable;

public enum CustorderError implements Serializable {

	ACTUAL_DELIVERY_CALCULATION_FAILURE("ACTUAL_DELIVERY_CALCULATION_FAILURE"), // FIXME this is not known
	AUTORELEASE_NO_APPROPRIATE_TRUCK("No appropriate truck found for automatic assignment creation."),
	BILLTO("Billto is invalid"),
	CARRIER("Carrier is invalid"),
	CONTRACT_INVALID("Contract is invalid"),
	CURRENCY_MISSING("Currency is not defined for the order item"),
	CONTRACT_VALID_DATE_INVALID("Contract Validity date is out of the Delivery Time period"),
	CUSTOMER_CREDIT_BLOCK("Customer is blocked"),
	DELIVERY_FROM_UNKNOWN("The delivery from date of the Customer Order is unknown."),
	DELIVERY_TO_BEFORE_DELIVERY_FROM("Delivery from must be earlier than Delivery to"),
	DELIVERY_TO_IN_PAST("Delivery to must be in the future"),
	DELIVERY_TO_UNKNOWN("The delivery to date of the Customer Order is unknown and couldn't be calculated."),
	DELIVERY_TYPE("Delivery type is invalid"),
	HIGHEST_PRIO_TRUCK_NOT_FOUND("No highest priority truck type found"),
	INVALID_DEFAULT("Default is invalid"),
	INVALID_DELIVERY_TIME_TOLERANCE("Invalid delivery time tolerance"),
	INVALID_DEL_FQY("Invalid delivery frequency"),
	INVALID_DEL_FQY_DELIVERY_TYPE("Delivery type is invalid"),
	INVALID_DEL_FQY_PACKAGE_TYPE("Performance order is not supported for this material package type."),
	INVALID_DEL_FQY_SETTINGS_DTODAT_OQTY("Only one of the parameters should be defined at performance orders (deliveryToDat or orderedQuantity)"),
	INVALID_DEL_FQY_SETTINGS_ETIME_QTYPHRE("Only one of the delivery frequency settings should be defined (deliveryFreqEveryTime or deliveryFreqQtyperhre)"),
	INVALID_DEL_FQY_UOM_EVERYTIME("Invalid delivery frequency UoM (only MIN and HRE are allowed)."),
	INVALID_DEL_FQY_UOM_QTYPERHRE("Invalid delivery frequency UoM"),
	INVALID_DISTANCE("Distance is invalid."),
	INVALID_DURATION("Invalid duration."),
	INVALID_EXACT_CUSTORDER_QTY_FLAG("Exact customer order quantity flag is undefined (null)."),
	INVALID_EXACT_CUSTORDER_QTY_FLAG_BAG("The exactCustorderQtyFlag should be FALSE for bag products."),
	INVALID_EXACT_CUSTORDER_QTY_FLAG_FUZZY("The exactCustorderQtyFlag should be FALSE if ordered UoM is fuzzy."),
	INVALID_EXACT_CUSTORDER_QTY_FLAG_SETT("The exactCustorderQtyFlag settings are inconsistent (all custorder item shuld have the same value)."),
	INVALID_EXACT_SDORDER_QTY_FLAG("Exact single delivery order quantity flag is not set (null)."),
	INVALID_EXACT_SDORDER_QTY_FLAG_BAG("The exactSdorderQtyFlag should be TRUE for bag products."),
	INVALID_EXACT_SDORDER_QTY_FLAG_SETTING("The exactSdorderQtyFlag settings are inconsistent (all custorder item shuld have the same value)."),
	INVALID_ORDERED_QUANTITY("Invalid quantity"),
	INVALID_ORDERED_UOM_BAG_FUZZY("Invalid ordered unit of measure for bag product (please use an exact unit of measure)."),
	INVALID_ORDERED_UOM_SIPLVEH_FUZZY("Invalid ordered unit of measure for this delivery type (please use an exact unit of measure for ship and block-train)."),
	INVALID_PRODUCT("Invalid product selected"),
	INVALID_QUANTITY_PER_DELIVERY("Invalid quantity per delivery"),
	ITEM_NOT_IN_CONTRACT("Item added to the order is not present in the contract. Sap material: {0} Sap contract of the order: {1}"),
	LEADTIME_WEB_ORDER_INCONSISTENT_ALLOW_SETTING("Leadtime settings on master data for weborders are inconsistent for the allow constants"),
	LEADTIME_WEB_ORDER_ND_EARLY("web order leadtime is invalid for the first part of the next day (deliveryTo)"),
	LEADTIME_WEB_ORDER_SD_EARLY("web order leadtime is invalid for the first part of the same day (deliveryTo)"),
	LEADTIME_WEB_ORDER_SD_LATER("web order leadtime is invalid for the first part of the next day (deliveryTo)"),
	LEADTIME_WEB_ORDER_WEEKEND("The whole delivery time window is on weekend days"),

	LEADTIME_DISTANCE_SANITY_L1_LESS_THAN_L2("L1 must be less than or equal with L2."),
	LEADTIME_DISTANCE_SANITY_ALL_FIELDS_ARE_MANDATORY("Lead time validation fields are mandatory."),
	LEADTIME_DISTANCE_SANITY_DX_VALUES_INCREMENTAL("D1,D2,D3,D4,D5,D6 fields must be incremental."),
	LEADTIME_DISTANCE_SANITY_ONLY_ONE("SDND and DBLT do not works together!"),
	LEADTIME_DISTANCE_ERROR_DELIVERY_TO_BEFORE_DELIVERY_FROM("Delivery from must be before delivery to."),
	LEADTIME_DISTANCE_ERROR("Delivery from date has to be greater than earliest possible delivery date."),
	LEADTIME_DISTANCE_SANITY_GENERAL("Sanity cannot pass, cannot make order!"),

	LOADINGPOINT("Loadingpoint is invalid"),
	MATERIAL_NOT_IN_SAREA("Sales area  is invalid"),
	MATERIAL_NOT_UNIQUE("The same material is added multiple times to the Customer Order"),
	MAX_NUMBER_OF_ORDERS("Maximum number of orders reached"),
	MIXED_PACKAGE_COMBINATION("It is not possible to mix bag and bulk orders"),
	MORE_THAN_ONE_BULK_ITEM("Cannot add more than one bulk order item to the order!"),
	MULTI_ORDER_FLAGGED_ITEM_WITH_SERVICE("Cannot create order with service(s) if more than one order is supposed to be created."),
	OPEN_QUANTITY_CHECK_FAILURE("The open quantity is smaller than the ordered qty"),
	ORDERED_QUANTITY_TOO_LOW("Quantity to low. Less quantity provided than actually changable. (Generally from status loaded)"),
	PAYER("Payer is invalid"),
	PLANNING_MATERIAL_NOT_AVAILABLE_TO_SAREA("Saving not possible, Planning Material is not available to this SAREA. Please contact sysadm"),
	AUTOPAYER("Auto payer-determination disables having non-empty payer"),
	POOL("Pool is invalid"),
	PRODUCTION_PLANT("Production plant is invalid"),
	PRODUCTION_PLANT_MISSING("Production plant not specified and cannot be determined automatically.")	 ,
	QUANTITY_MISSING("No ordered quantity specified."),
	RAIL_ORDER_COMM_CARRIER_EMPTY("Not possible to save a rail order without commercial carrier"),
	RAIL_ORDER_DELIVERY_LATE("Not possible to create automatically the order for the selected delivery to date"),
	RAIL_ORDER_POOL_EMPTY("Not possible to save a rail order without pool"),
	SALESGROUP("Sales group is invalid"), 
	SAP_BP_SAREA("Sales area  is invalid") ,
	SAP_BP_SOFFICE("SalesOffice ist ungültig"),
	SAREA("Sales area  is invalid"),
	SAREA_SORG("Sales organization is invalid"),
	SERVICE_ONLY_ORDER("Cannot create order with only service item(s)!"),
	SHIPPINGPOINT("Shippingpoint is invalid"), 
	SHIPTO_BILLTO("Billto is invalid"),
	SHIPTO_BILLTO_IN_SAREA("Sales area  is invalid"),
	SILO("Silo is invalid"),
	SIMPLE_VEHICLE_ORDER_POOL_COMM_CARRIER("Carrier is invalid"),
	SUPPLY_PLANT("Supply plant is invalid"),
	TRANSPORT_CONDITION("Transport condition is missing"),
	UNKNOWN_LOGON_PACKAGE_TYPE("The packaging type is not known for this item"),
	UOM("Unit of measure is invalid"),
	WEB_BIMODAL_RAIL_LEADTIME_CHECK("Now is later than the latest web order placing time"), 
	AVAILABLE_IN_PICKUP_APP_FLAG("Dispocard should not be available on Terminal Application with this restrictions"),
	BLOCK_ALL_AREA("Customer is blocked for ALL Sales Areas"),
	BLOCK_SOME_AREA("Customer is blocked for SOME Sales Areas"),
	BLOCKED_BP("Customer is blocked.Order taking may not be possible!"),
	BLOCKED_SAREA("Customer is blocked. Order taking for some Sales Areas may not be possible!");
	
	private final String plainMessage;
	
	private CustorderError(String plainMessage) {
		this.plainMessage = plainMessage;
	}
		
	public String getPlainMessage() {
		return plainMessage;
	}
	
	/**
	 * Specify the message code when the error steps up in the context of the caller.
	 * 
	 * CURRRENTLY THE LITDICT ENTRIES ARE CHAOTIC. MOST LIKELY IN THE CASE OF WEB ORDER THE ONLY
	 * RETURNED REASON COULD BE:	return "application.errors.custordervalidation.HIDE_REASON_CONTACT_CRH". Purpose:
	 * the customer should not be informed what exactly is the problem, he should contact the crh hotline that they can
	 * fix the problem in the background.
	 * 
	 * @param context
	 * @return
	 */
	public String getMessage(CallerContext context) {
		if ((context == null) || (context == CallerContext.LDS)) {
			return getPlainMessage();
		}
		else {
			return getLitDictKey(context);
		}		
	}
	
	/**
	 * On order detail nothing should block.
	 * @param context
	 * @return
	 */
	public boolean shouldCauseFailure(CallerContext context) {
		switch (context) {
			case ORDER_DETAIL:
				return false;
			default:
				return true;
		}			
	}
	
	public String getLitDictKey(CallerContext context) {		
		switch (this) {
			case ITEM_NOT_IN_CONTRACT:
			case CURRENCY_MISSING:
			case CONTRACT_VALID_DATE_INVALID:
			case INVALID_PRODUCT:
			case MATERIAL_NOT_UNIQUE:
			case HIGHEST_PRIO_TRUCK_NOT_FOUND:
			case INVALID_DEFAULT:
			case LEADTIME_WEB_ORDER_INCONSISTENT_ALLOW_SETTING:
			case LEADTIME_WEB_ORDER_ND_EARLY:
			case LEADTIME_WEB_ORDER_SD_EARLY:
			case LEADTIME_WEB_ORDER_SD_LATER:
			case LEADTIME_WEB_ORDER_WEEKEND:
			case LEADTIME_DISTANCE_SANITY_L1_LESS_THAN_L2:
			case LEADTIME_DISTANCE_SANITY_ALL_FIELDS_ARE_MANDATORY:
			case LEADTIME_DISTANCE_SANITY_DX_VALUES_INCREMENTAL:
			case LEADTIME_DISTANCE_SANITY_ONLY_ONE:
			case LEADTIME_DISTANCE_ERROR_DELIVERY_TO_BEFORE_DELIVERY_FROM:
			case LEADTIME_DISTANCE_ERROR:
			case LEADTIME_DISTANCE_SANITY_GENERAL:
			case DELIVERY_FROM_UNKNOWN:
			case DELIVERY_TO_UNKNOWN:
			case INVALID_ORDERED_UOM_BAG_FUZZY:
			case INVALID_ORDERED_UOM_SIPLVEH_FUZZY:
			case INVALID_DEL_FQY_UOM_EVERYTIME:
			case INVALID_DEL_FQY_UOM_QTYPERHRE:
			case INVALID_DEL_FQY_SETTINGS_ETIME_QTYPHRE:
			case INVALID_DEL_FQY_SETTINGS_DTODAT_OQTY:
			case INVALID_DEL_FQY_PACKAGE_TYPE:
			case INVALID_DEL_FQY_DELIVERY_TYPE:
			case INVALID_EXACT_CUSTORDER_QTY_FLAG:
			case INVALID_EXACT_CUSTORDER_QTY_FLAG_SETT:
			case INVALID_EXACT_CUSTORDER_QTY_FLAG_BAG:
			case INVALID_EXACT_CUSTORDER_QTY_FLAG_FUZZY:
			case INVALID_EXACT_SDORDER_QTY_FLAG:
			case INVALID_EXACT_SDORDER_QTY_FLAG_SETTING:
			case INVALID_EXACT_SDORDER_QTY_FLAG_BAG:
			case PLANNING_MATERIAL_NOT_AVAILABLE_TO_SAREA:
			case PRODUCTION_PLANT_MISSING:
				return "application.errors.custordervalidation." + this.name();
				
			case UOM:
			case PRODUCTION_PLANT:
			case SUPPLY_PLANT:
			case SHIPPINGPOINT:	
			case LOADINGPOINT:
			case POOL:
			case CARRIER:
			case SILO:
			case BILLTO:	
			case SALESGROUP:
			case SAREA:
			case SAP_BP_SAREA:
			case SAP_BP_SOFFICE:
			case SAREA_SORG:
			case SHIPTO_BILLTO:
			case DELIVERY_TYPE:
			case TRANSPORT_CONDITION:
			case CUSTOMER_CREDIT_BLOCK:
			case CONTRACT_INVALID:
			case MATERIAL_NOT_IN_SAREA:
			case PAYER:
			case AUTOPAYER:
			case SHIPTO_BILLTO_IN_SAREA:
				switch (context) {
					case CALL_CENTER:
					case ORDER_DETAIL:
					case DISPOKARTE:
						return "dispokarte.errors.invalid." + this.name();					
					default:
						return "webbestellung.error.shipto.not.initialized";
				}
										
			case INVALID_DURATION:
				return "webbestellung.error.invalid.duration";
				
			case DELIVERY_TO_BEFORE_DELIVERY_FROM:
				return "webbestellung.error.invalid.delivery.earlier.later";
				
			case DELIVERY_TO_IN_PAST:
				return "webbestellung.error.invalid.delivery.future";
								
			case INVALID_DISTANCE:
				return "webbestellung.error.invalid.distance";
							
			case OPEN_QUANTITY_CHECK_FAILURE:
				switch (context) {
					case ORDER_DETAIL:
						return "orderdetail.message.error.quantity.restriction";
					case WEB_ORDER:
						return "webbestellung.error.quantity.restriction";
					case PICKUP_TERMINAL:
						return "pickup.errors.quantity.restriction";
					default:
						return "bestellung.errors.invalid.ERROR_CODE_QUANTITY_RESTRICTED";
				}
				
			case MIXED_PACKAGE_COMBINATION:
				return "webbestellung.error.combineddelivery";
				
			case MORE_THAN_ONE_BULK_ITEM:
				return "webbestellung.error.cannotaddmorebulk";
				
			case SERVICE_ONLY_ORDER:
				return "webbestellung.error.pure.service";
				
			case MULTI_ORDER_FLAGGED_ITEM_WITH_SERVICE:
				return "webbestellung.error.multiorder.with.service";
				
			case RAIL_ORDER_DELIVERY_LATE:
				return "webbestellung.error.railordernotpossibledelto";
						
			case RAIL_ORDER_COMM_CARRIER_EMPTY:
				switch (context) {
					case WEB_ORDER:
						return "webbestellung.error.rail.no.commcarrier";
					default:
						return "bestellung.error.rail.no.commcarrier";
				}
				
			case RAIL_ORDER_POOL_EMPTY:
				switch (context) {
					case WEB_ORDER:
						return "webbestellung.error.rail.no.pool";
					default:
						return "bestellung.error.rail.no.pool";
				}
				
			case SIMPLE_VEHICLE_ORDER_POOL_COMM_CARRIER:
				switch (context) {
					case WEB_ORDER:
						return "application.errors.custordervalidation.HIDE_REASON_CONTACT_CRH";
					default:
						return "application.errors.custordervalidation.SIMPLE_VEHICLE_ORDER_POOL_COMM_CARRIER";
				}	
				
			case WEB_BIMODAL_RAIL_LEADTIME_CHECK:
				return "webbestellung.error.weborderplacingnotpossible";
								
			case UNKNOWN_LOGON_PACKAGE_TYPE:
				return "webbestellung.error.no.logon.package.cd";
						
			case ACTUAL_DELIVERY_CALCULATION_FAILURE:
				return "webbestellung.error.no.logon.package.cd";
				
			case INVALID_ORDERED_QUANTITY:
				return "bestellung.error.invalid.quantity";
				
			case ORDERED_QUANTITY_TOO_LOW:
				return "res.error.custorder.qty.too.low";
					
			case INVALID_QUANTITY_PER_DELIVERY:
				return "bestellung.error.invalid.quantity.per.delivery";
				
			case INVALID_DEL_FQY:
				return "bestellung.error.invalid.delivery.frequency";

			case INVALID_DELIVERY_TIME_TOLERANCE:
				return "bestellung.error.invalid.delivery.time.tolerance";
				
			case MAX_NUMBER_OF_ORDERS:
				return "bestellung.error.max.num.order";

			case AUTORELEASE_NO_APPROPRIATE_TRUCK:
				return "res.order.assignment.autocreate.error.no.appropriate.truck.found";

			case AVAILABLE_IN_PICKUP_APP_FLAG:
				return "dispokarte.errors.invalid.AVAILABLE_IN_PICKUP_APP_FLAG";
			
			case BLOCKED_BP:
				switch (context) {
				case CALL_CENTER:
					return "bestellung.errors.customerSalesAreaBlockAll";
				default:
					return "dispokarte.errors.invalid.CUSTOMER_CREDIT_BLOCK";
				}
			
			case BLOCK_SOME_AREA:
				switch (context) {
					case DISPOKARTE:
						return "dispokarte.errors.customerSalesAreaBlockSome";
					default:
						return "bestellung.errors.customerSalesAreaBlockSome";
				}
			
			case BLOCK_ALL_AREA:
				switch (context) {
					case DISPOKARTE:
						return "dispokarte.errors.customerSalesAreaBlockAll";
					default:
						return "bestellung.errors.customerSalesAreaBlockSome";
				}
			default: 
				return "dispokarte.errors.invalid";
		}
	}

	@Override
	public String toString() {
		return String.format(
				"Custorder validation error %s(\"%s\")", 
				name(), 
				getPlainMessage());
	}
}