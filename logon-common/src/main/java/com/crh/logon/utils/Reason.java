/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

/**
 * @author AdNovum Informatik AG
 */
public enum Reason {

	RES_CRON_ALREADY_RUNNING("res.cron.already.running"),
	RES_IMPL_BUG("res.exception.implementation"),
	RES_ERROR_NOT_IMPL("res.exception.not_implemented"),
	RES_ERROR_INIT("res.error.init"),
	RES_ERROR_CONFIG_NOT_EXISTENT("res.error.not.existent"),
	RES_ERROR_CONFIG_NOT_VALID("res.error.not.valid"),
	RES_ERROR_INIT_SCV_LOOKUP("res.error.svc_lookup"),
	RES_ERROR_SECURITY_NOT_ALLOWED("res.error.security.not.allowed"),
	RES_ERROR_SECURITY_TAMPERING("res.error.security.tampering"),
	RES_ERROR_UNSPECIFIED("res.error.unspecified"),
	RES_ERROR_IMPL("res.error.impl"),
	RES_ERROR_WSCLIENT("res.error.wsclient"),
	RES_ERROR_WSSERVER_PARAMS("res.error.wsserver.params"),
	RES_ERROR_FETCH_BLOB("res.error.fetch.blob"),
	RES_ERROR_WRITE_BLOB("res.error.write.blob"),
	RES_ERROR_DELETE_BLOB("res.error.delete.blob"),
	RES_ERROR_GENERATE_PDF("res.error.generate.pdf"),
	RES_STRUTS_VALIDATION("res.struts.validation"),
	RES_ORDER_INSUFFICIENT_DATA("res.order.insufficient.data"),
	RES_ORDER_WRONG_STATUS("res.order.wrong.status"),
	RES_ORDER_ON_HOLD("res.order.on.hold"),
	RES_ORDER_ASSIGNMENT_AUTOCREATE_ERROR_GENERAL("res.order.assignment.autocreate.error.general"),
	RES_ORDER_ASSIGNMENT_AUTOCREATE_ERROR_NO_APPROPRIATE_TRUCK_FOUND("res.order.assignment.autocreate.error.no.appropriate.truck.found"),
	RES_ORDER_ASSIGNMENT_EXISTS_FOR_WAGON("res.order.assignment.exists.for.wagon"),
	RES_ORDER_PROCESSING_ERROR("res.order.processing.error"),
	RES_USER_LDAP_HOLSPACE_NOT_UNIQUE("res.user.ldap.holspace.not.unique"),
	RES_USER_PDA_USER_NUMBER_NOT_UNIQUE("res.user.pdausernumber.not.unique"),
	RES_RFID_OBJECT_LABEL_NOT_UNIQUE("res.rfidobject.label.not.unique"),	
	RES_ERROR_WSCLIENT_LOADINGPONT_UNKNOWN("res.error.wsclient.loadingpoint.unknown"),
	RES_RFIDTAG_IDENTITY_NOT_UNIQUE("res.rfidtag.identity.not.unique"),
	RES_TECH_ERROR_PARALLEL("res.tech.error.parallel"),
	RES_APP_ERROR_PARALLEL("res.app.error.parallel"),
	RES_ERROR_PDA("res.error.pda"),
	RES_MIME_UNSUPPORTED("res.error.mime.unsupported"),
	RES_IMAGE_NAME_NOT_UNIQUE("res.error.image.name.not.unique"),
	RES_ERROR_TRUCK_TRAILER_IS_ACTIVE("res.error.truck.trailer.is.active"),
	RES_ERROR_INVALID_TRUCK_COMPOSITION("res.error.invalid.truck.composition"),
	RES_ERROR_INVALID_MEASUREMENT("res.order.invalid.measurement"),
	RES_ERROR_FREEZING("res.error.freezing"),
	RES_ERROR_COST_CALCULATION("res.error.calculation"),
	RES_ERROR_UNKNOWN_MATERIAL("res.error.unknown_material"),
	RES_ERROR_MATERIAL_MISMATCH("res.error.material_mismatch"),
	RES_ERROR_INVALID_CARRIER("res.error.invalid.carrier"),
	RES_ERROR_CSV_IS_EMPTY("res.error.csv.is.empty"),
	
	RES_ERROR_DISPOKARTE_NO_SHIPTO_FOUND("dispokarte.errors.nores"),
	RES_ERROR_DISPOKARTE_NO_ACTIVE_SAREA_FOR_SHIPTO("dispokarte.errors.noactivesarea"),
	
	RES_ERROR_UNKNOWN_SAP_VEHICLE_CHARACTERISTIC("res.error.sap.vehicle.unknown_characteristic"),
	RES_ERROR_UNKNOWN_SAP_VEHICLE_LOGON_TYPE("res.error.sap.vehicle.unknown_vehicle_logon_type"),
	RES_ERROR_TRAILER_NO_TRUCK("res.error.sap.vehicle.trailer.before.truck"),
	RES_ERROR_TRAILER_HAS_TRUCK("res.error.sap.vehicle.trailer.has.truck"),
	RES_ERROR_SAP_VEHICLE_PRECONDITIONS_NOT_MET("res.error.sap.vehicle.precondition.failure"),
	RES_ERROR_SAP_VEHICLE_PLATE_NO_INCOSISTENCY("res.error.sap.vehicle.import.plateno.inconsistent"),
	RES_ERROR_SAP_SHIPPINGPOINT_MISCONFIGURED("res.error.sap.shippingpoint_misconfigured"),
	
	// custorder create/adjust
	RES_ERROR_CUSTORDER_ITEM_INCOSISTENCY("res.error.custorder.item.exactcustorderQtyFlag.inconsistent"),
	
	// DeliveryDocument OUT IFC
	RES_ERROR_ORDER_DIFFERENT_CURRENCIES("res.error.order.different.currencies"),
	
	//order cost calculation
	RES_ERROR_CCALC_DELIV_DATE_EXCH_RATE("res.error.ccalc.deliv.date.exch.rate"), //Order costs: Exchange rate missing.
	RES_ERROR_CCALC_DELIV_DATE_NOT_NULL("res.error.ccalc.deliv.date.not.null"), //Order costs: Actual delivery date entry in table tlgnc_order missing.
	RES_ERROR_CCALC_DELIV_Q_NOT_NULL("res.error.ccalc.deliv.q.not.null"), //Order costs: Delivery quantity entry in table tlgnc_item_order missing. 
	RES_ERROR_CCALC_DELIV_UOM_NOT_NULL("res.error.ccalc.deliv.uom.not.null"), //Order costs: Delivery uom code entry in table tlgnc_item_order missing.
	RES_ERROR_CCALC_SHIPPOINT_NOT_NULL("res.error.ccalc.shippoint.not.null"), //Order costs: Shipping point entry in table tlgnc_order missing.
	RES_ERROR_CCALC_WRONG_UOM("res.error.ccalc.wrong.uom"), //Order costs: Delivered unit of measure is not equal to base unit of measure of sap material.
	RES_ERROR_CCALC_HAN_EXCH_RATE("res.error.ccalc.han.exchange.rate"), //Handling costs: Exchange rate missing.
	RES_ERROR_CCALC_HAN_CUR_CODE("res.error.ccalc.han.currency.code"),  //Handling costs: Currency code entry in table tlgnc_shippingpoint missing.
	RES_ERROR_CCALC_HAN_CUR_CODE_MAT("res.error.ccalc.han.currency.code.mat"),  //Handling costs: Currency code entry in table tlgnc_mat_shippingpoint missing.
	RES_ERROR_CCALC_HAN_UOM_NOT_NULL("res.error.ccalc.han.uom.not.null"),  //Handling costs: Unit of measure entry in table tlgnc_shippingpoint missing.
	RES_ERROR_CCALC_HAN_VAR_PROD_COST("res.error.ccalc.han.var.prod.cost"), //Handling costs: (Future) Variable production costs of material entry in table tlgnc_shippingpoint missing.Handling costs: (Future) Variable production costs of material entry in table tlgnc_shippingpoint missing.
	RES_ERROR_CCALC_HAN_WRONG_UOM("res.error.ccalc.han.wrong.uom"), //Handling costs:  Delivered unit of measure is not equal to unit of measure of shipping point.
	RES_ERROR_CCALC_IUD_COST("res.error.ccalc.iud.cost"), //IUD costs: Cannot find needed row in table tlgnc_iud_cost.
	RES_ERROR_CCALC_IUD_EXCH_RATE("res.error.ccalc.iud.exchange.rate"), //IUD costs: Exchange rate missing.
	RES_ERROR_CCALC_IUD_CUR_CODE("res.error.ccalc.iud.currency.code"),  //IUD costs: Currency code entry in table tlgnc_iud_cost missing.
	RES_ERROR_CCALC_IUD_VAR_PROD_COST("res.error.ccalc.iud.var.prod.cost"), //IUD costs: (Future) Inter-unit distribution cost entry in table tlgnc_iud_cost missing.
	RES_ERROR_CCALC_PRO_MAT_PLANT("res.error.ccalc.pro.mat.plant"), //Production costs: Cannot find needed row in table tlgnc_mat_plant.
	RES_ERROR_CCALC_PRO_EXCH_RATE("res.error.ccalc.pro.exchange.rate"), //Production costs: Exchange rate missing.
	RES_ERROR_CCALC_PRO_CUR_CODE("res.error.ccalc.pro.currency.code"),  //Production costs: Currency code entry in table tlgnc_mat_plant missing.
	RES_ERROR_CCALC_PRO_VAR_PROD_COST("res.error.ccalc.pro.var.prod.cost"), //Production costs: (Future) Variable production costs of material entry in table tlgnc_mat_plant missing.
	RES_ERROR_CCALC_OUT_SHIPTO("res.error.ccalc.out.shipto"), //OUT costs: Cannot find needed row in table tlgnc_shipto.
	
	// The UOM of an order is "Truck", but no truck types are selected. 
	RES_ERROR_ORDER_TRUCK_UOM_NO_TRUCKS_SELECTED("res.error.order.uom.truck.without.vehicles.selected"),
	
	// Truck Revenue Planning report
	RES_ERROR_TRUCK_REVENUE_PLANNING_NO_DATA("res.error.truck.revenue.planning.no.data"), // No pre-scheduling data is available for truck revenue planning report generation.
	
	// Goods Movement
	RES_ERROR_GOODSMOVMENT_NO_ORDERITEM_NOR_LOAD_FOUND("res.error.goodsmovement.no.orderitem.nor.load.found"), // No order item or corresponding load found to the given order
	// DO NOT add the keys to language files (litDict, as these reasons are handled at business tier)
	RES_ERROR_GOODSMOVMENT_ORDERITEM_NOT_NULL("res.error.goodsmovement.orderitem.not.null"), // No order item initialized for the goods movement creation at return handling 
	RES_ERROR_GOODSMOVMENT_ORDERITEM_STATE_WRONG("res.error.goodsmovement.orderitem.state.wrong"), // The order item not contains the needed return values (reason, quantity, ...)
	RES_ERROR_GOODSMOVMENTMANAGER_STATE_WRONG("res.error.goodsmovementmanager.state.wrong"), // The goods movement manager initialization went wrong (i.e.: missing load entity, serviceLocator, ...)
	RES_ERROR_GOODSMOVMENTMANAGER_MISSING_FIELD("res.error.goodsmovementmanager.missing.field"), // The goods movement manager, goods movement entity creation is wrong ( missing fields)  
	RES_ERROR_GOODSMOVMENT_ORDER_NOT_NULL("res.error.goodsmovement.order.not.null"), // No order initialized for the goods movement creation at return handling 
	RES_ERROR_GOODSMOVMENT_ORDER_STATE_WRONG("res.error.goodsmovement.order.state.wrong"), // The order not contains the needed return values (reason, quantity, ...)
	
	// UoM conversion errors
	RES_ERROR_UOM_CONV_FUZZY_UOM("res.error.uom.conv.fuzzy.uom"),
	RES_ERROR_UOM_CONV_DIMENSION_UNKNOWN("res.error.uom.conv.dimension.unknown"),
	RES_ERROR_UOM_CONV_DIMENSION_MISMATCH("res.error.uom.conv.dimension.mismatch"),
	RES_ERROR_UOM_CONV_NUM_OR_DEN_ZERO("res.error.uom.conv.invalid.num.or.den"),
	RES_ERROR_UOM_CONV_COULD_NOT_FIND_ALT_UOM("res.error.uom.conv.could.not.find.alt.uom"),
	RES_ERROR_CANNOT_CONVERT_UOM("res.error.cannot.convert.uom"),
	
	// Simple vehicle scheduling
	RES_ERROR_SIMPLEVEHICLE_PRIO_OOR("res.error.simplevehicle.priority.outofrange"),
	RES_ERROR_SIMPLEVEHICLE_VEHICLE_NOT_FOUND("res.error.simplevehicle.vehiclenotfound"),
	RES_ERROR_SIMPLEVEHICLE_ORDER_NOT_ALLOCATED("res.error.simplevehicle.ordernotallocated"),
	RES_ERROR_SIMPLEVEHICLE_NOT_UNIQUE_ASSG("res.error.simplevehicle.notuniqueassignment"),
	RES_ERROR_SIMPLEVEHICLE_CANT_DEALLOCATE("res.error.simplevehicle.cantdeallocate"),
	RES_ERROR_SIMPLEVEHICLE_NO_ASSIGNMENT("res.error.simplevehicle.noassignment"),
	RES_ERROR_SIMPLEVEHICLE_ITEM_WITHOUT_LOAD("res.error.simplevehicle.itemwithoutload"),
	RES_ERROR_SIMPLEVEHICLE_NEGATIVE_LOADED_QTY("res.error.simplevehicle.negativeloadedqty"),
	RES_ERROR_SIMPLEVEHICLE_INVALID_ASSG_STATUS("res.error.simplevehicle.invalidassgstatus"),
	RES_ERROR_SIMPLEVEHICLE_NO_SUCH_ORDER("res.error.simplevehicle.nosuchorder"),
	RES_ERROR_SIMPLEVEHICLE_MISSING_UNLOAD_QTY("res.error.simplevehicle.missingunloadqty"),
	RES_ERROR_SIMPLEVEHICLE_UNLOAD_QTY_NEGATIVE("res.error.simplevehicle.unloadqtynegative"),
	RES_ERROR_SIMPLEVEHICLE_ORDER_NOT_SHIPPING("res.error.simplevehicle.ordernotshipping"),
	RES_ERROR_SIMPLEVEHICLE_ASSG_ALREADY_LOADING("res.error.simplevehicle.assignmentalreadyloading"),
	RES_ERROR_SIMPLEVEHICLE_DEALLOCATE_WRONG_STATUS("res.error.simplevehicle.deallocatewrongstatus"),
	RES_ERROR_SIMPLEVEHICLE_INVALID_LOADINGPOINT("res.error.simplevehicle.invalidloadingpoint"),
	
	// Loading
	RES_ERROR_LOADING_ASSIGNMENT_NOT_FOUND("res.error.loading.assignmentnotfound"),
	RES_ERROR_LOADING_NOT_YET_SHIPPING("res.error.loading.not.yet.shipping"),
	RES_ERROR_LOADING_NOT_YET_UNLOADING("res.error.loading.not.yet.unloading"),
	
	// User
	RES_ERROR_USER_NOT_FOUND("res.error.user.not.found"),
	RES_ERROR_USER_EXT_USER_NOT_FOUND("res.error.user.ext.user.not.found"),
	RES_ERROR_USER_EXT_NO_EMAIL_FOUND("res.error.user.ext.no.email.found"),
	RES_ERROR_USER_EXT_PWD_LOCKED("res.error.user.ext.pwd.locked"),
	RES_ERROR_USER_EXT_PWD_TOKEN_INVALID("res.error.user.ext.pwd.invalid.token"),
	RES_ERROR_USER_EXT_PWD_TOKEN_EXPIRED("res.error.user.ext.pwd.token.expired"),
	
	// configurable_sequences
	RES_ERROR_CONFIGURABLE_SEQUENCE_NOT_CONFIGURED("res.error.configurable.sequence.not.configured"),
	
	// External Login Password History
	RES_ERROR_EXTPWDHISTORY_PWD_NOT_HASHED("res.error.extpwdhistory.pwd.not.hashed"),
	
	// Naming Exception when lookup local ejb
	RES_ERROR_LOCALEJB_NOT_FOUND("res.exception.localejb.not.found"),
	
	RES_ERROR_GMAPS_DISTANCE_UNEXPECTED_RESPONSE("application.errors.gmaps.unexpected.response"),
	RES_ERROR_GMAPS_DISTANCE_CALC_FAILED_EXTERNAL_ORDER("application.errors.externalordercreation.distancecalculation.failed"),
	
	// User Activity
	RES_ERROR_USER_ACTIVITY_ACTION_NOT_FOUND("res.error.user.activity.action.not.found"),

	// Pricing call errors
	RES_ERROR_PRICING_CALL_BUSINESS_ERROR("res.error.pricingcall.business.error"),
	RES_ERROR_PRICING_CALL_GENERAL_ERROR("res.error.pricingcall.general.error"),

	// Logistic Report SAP BW errors
	RES_ERROR_LOGISTIC_REPORT_BUSINESS_ERROR("res.error.logisticreport.business.error"),
	RES_ERROR_LOGISTIC_REPORT_GENERAL_ERROR("res.error.logisticreport.general.error"),

	// Landfill errors
	RES_ERROR_LANDFILL_MULTIPLE_ORDERS("res.error.landfill.multiple.orders"),
	RES_ERROR_LANDFILL_MULTIPLE_ORDERITEMS("res.error.landfill.multiple.orderitems"),
	RES_ERROR_REVERSE_NOT_ALLOWED_STARTLOAD("res.error.reverse.not.allowed.startload"),
	
	RES_ERROR_BESTELLUNG_NO_ACTIVE_SAPMATERIALSAREA("bestellung.error.sapmaterialsarea"),
	
	// Automatic delivery note printing errors (after loading)
	RES_ERROR_DELIVERY_NOTE_PRINTING_INVALID_PRINTER_FOR_LOADINGPOINT("res.error.deliverynote.printing.invalid.printer.for.loadingpoint"), 
	RES_ERROR_DELIVERY_NOTE_NOT_SUPPORT_INVOICE_GENERATE_PDF_FOR_AZERI_LANGUAGE("res.error.deliverynote.error.generate.exportInvoice.azLanguage"),
	RES_ERROR_DELIVERY_NOTE_NOT_SUPPORT_CASH_SALES_TYPE_GENERATE_PDF_FOR_AZERI_LANGUAGE("res.error.deliverynote.error.generate.cashSales.azLanguage"),
	
	RES_ERROR_INVALID_DATE_FORMAT("auftragbearbeitung.errors.datebadformat"),

	RES_ERROR_DEMANDPLAN_INVALID_DATE("demandplan.error.invaliddate"),
	RES_ERROR_DEMANDPLAN_INVALID_WEIGHT("demandplan.error.invalidweight"),
	RES_ERROR_DEMANDPLAN_NO_SHIPTO("demandplan.error.noshipto"),
	RES_ERROR_DEMANDPLAN_NO_MAT("demandplan.error.nomat"),
	RES_ERROR_DEMANDPLAN_NO_PLAN("demandplan.error.noplan"),
	RES_ERROR_DEMANDPLAN_MAT_CANNOT_CHANGE("demandplan.error.matcannotchange"),
	RES_ERROR_DEMANDPLAN_MAT_NOT_UNIQUE("demandplan.error.matnotunique"),
	RES_ERROR_ITRACK_REPORTING_INVALID_DATE("itrack.error.invaliddate"),
	RES_ERROR_ITRACK_REPORTING_FILTER_REQUIRED("itrack.error.filter.required");

	private final String msgKey;
	
	private Reason(String msgKey) {
		this.msgKey = msgKey;
	}
	
	public String getReason() {
		return msgKey;
	}
	
	@Override
	public String toString() {
		return getReason();
	}
}
