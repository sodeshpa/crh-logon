/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

/**
 * @author AdNovum Informatik AG
 */
public enum DeliveryNoteLabel {
	
	ORDER_NUMBER ("pdf.orderno"),
	COLL_ORDER_NUMBER("pdf.coll.orderno"),
	VERSION ("pdf.version"),
	ADDRESS_SOLDTO ("pdf.soldto.address"),
	ADDRESS_SHIPTO ("pdf.shipto.address"),
	/** 
	 * Label "Contact:" for Ship To Contact
	 */
	CONTACT_SHIPTO ("pdf.shipto.contact"),
	ADDRESS_COMM_CARRIER ("pdf.comm.carrier.address"),
	ADDRESS_EXEC_CARRIER("pdf.exec.carrier.address"),
	ADDRESS_BILLTO ("pdf.dealer.address"),
	ADDRESS_PAYER ("pdf.coll.payer"),
	NHM_CODE ("pdf.nhm.code"),
	TRUCK_PLATE_NO ("pdf.truck.plate.no"),
	WAGON_NO ("pdf.wagon.no"),
	CONTAINER_NO ("pdf.container.no"),
	DISTANCE ("pdf.distance"),
	KM ("pdf.km"),
	PLANT ("pdf.plant"),
	SHIPPING_POINT ("pdf.shipping.point"),
	ENDLOAD_DATE ("pdf.endload.date"),
	DELIVERY_TIME ("pdf.delivery.time"),
	DELIVERY_WINDOW ("pdf.delivery.window"),
	MATERIAL ("pdf.material"),
	WEIGHTS_GROSS ("pdf.weights.gross"),
	WEIGHTS_TARE ("pdf.weights.tare"),
	WEIGHTS_NET ("pdf.weights.net"),
	WEIGHTS_RETURN ("pdf.weights.return"),
	RETURNED_PALLETS ("pdf.weights.returned.pallets"),
	QUANTITY_ORDERED ("pdf.ordered.quantity"),
	QUANTITY_DELIVERED ("pdf.delivered.quantity"),
	COMMENT ("pdf.comment"),
	SIGNATURE_DRIVER ("pdf.signature.driver"),
	SIGNATURE_CUSTOMER ("pdf.signature.customer"),
	TEL ("pdf.tel"),
	FAX ("pdf.fax"),
	VEHICLE ("pdf.vehicle"),
	CHROMAT_MISSING ("pdf.chromat.missing"),
	
	// collective del note labels
	TOTAL ("pdf.coll.total"),
	RIDES ("pdf.coll.rides"),
	WAIT_TIME_TOTAL ("pdf.coll.waitingtime"),
	WAIT_TIME_HOURS ("pdf.coll.hours"),
	CRH ("pdf.coll.crh"),
	ORDERS_TABLE_DELNOTE ("pdf.coll.table.delnote"),
	ORDERS_TABLE_SAPNR ("pdf.coll.table.sapnr"),
	ORDERS_TABLE_MATERIAL ("pdf.coll.table.material"),
	ORDERS_TABLE_TRUCK ("pdf.coll.table.truck"),
	ORDERS_TABLE_LOAD ("pdf.coll.table.load"),
	ORDERS_TABLE_DELIVERY ("pdf.coll.table.delivery"),
	ORDERS_TABLE_NET ("pdf.coll.table.net"),
	TOTAL_TEXT ("pdf.coll.totaltext"), 
	INCOTERM ("pdf.coll.incoterm"),
	REASON_OF_TRANSPORT ("pdf.coll.reason_transport"),
	PHYSICAL_MAT ("pdf.coll.physicalmat"),
	SERVICES ("pdf.coll.services"), 
	NOT_AVAILABLE ("pdf.coll.na"), 
	
	// cash sales specific labels
	NET_PRICE ("pdf.cashsales.net"), 
	NET_PRICE_PER_BASEUOM ("pdf.cashsales.net.perbaseuom"),
	VAT ("pdf.cashsales.vat"), 
	VAT_RATE ("pdf.cashsales.vatrate"),
	GROSS_PRICE ("pdf.cashsales.gross"),
	TOTAL_PRICE("pdf.cashsales.total"),
	PAYMENT_METHOD("pdf.cashsales.payment"),
	CURRENCY("pdf.cashsales.currency"),
	TAX_NO("pdf.cashsales.taxno"),
	
	// Export Invoice specific labels
	EXPORT_INVOICE_ISSUE_DATE("pdf.exportinvoice.issueDate"),
	EXPORT_INVOICE_ISSUE_LOCATION("pdf.exportinvoice.issueLocation"),
	EXPORT_INVOICE_CONTROLLER_NUMBER("pdf.exportinvoice.controllerNo"),
	EXPORT_INVOICE_ADVANCE_PAYMENT_INVOICE_NUMBER("pdf.exportinvoice.advancePaymentInvoiceNo"),
	EXPORT_INVOICE_MAT_PRICE("pdf.exportinvoice.matPrice"),
	EXPORT_INVOICE_TOTAL_NET_PRICE("pdf.exportinvoice.totalNetPrice"),
	
	//Separators between important tags
	SEPARATOR_WAGON_OR_TRUCK_PLATE_NO("pdf.wagonortruckplateno.separator"),
	SEPARATOR_CONTAINER_OR_TRAILER_PLATE_NO("pdf.containerortrailerplateno.separator"),
	
	// Eastern European specific
	TRAILER_PLATE_NO ("pdf.trailer.plate.no"),
	DELIVERY_SILO ("pdf.delivery.silo"),
	PLANT_ENTRY ("pdf.plant.entry"),
	PLANT_EXIT ("pdf.plant.exit"),
	SEALS_NUMBERS ("pdf.seals.numbers"),
	CARRIER_CRM ("pdf.carrier.crm"),
	CONC_CERT ("pdf.conformity.cert"),

	SAP_CONTRACT_NO("bestellung.materialcontractcombo.deliverySettingBasedOnContract"),
	FIELDS_REMARKS("fields.remarks");
	
	
	private final String messageId;
	
	DeliveryNoteLabel(String messageId) {
		this.messageId = messageId;
	}
	
	public String getMessageId() {
		return messageId;
	}

}
