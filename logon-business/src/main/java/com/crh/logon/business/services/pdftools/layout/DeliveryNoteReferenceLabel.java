/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

/**
 * labels that only exist in reference tables 
 * and are not defined in the litdict file
 * 
 * @author AdNovum Informatik AG
 */
public enum DeliveryNoteReferenceLabel {
	
	LOGON_ORDER_NUMBER ("pdf.deliverynote.lt"),
	LDS_ORDER_NUMBER ("pdf.deliverynote.lds"),
	TRANSPORT_REASON_STPO("pdf.coll.reason_transport.stpo"),
	TRANSPORT_REASON_SALES("pdf.coll.reason_transport.sales"),
	CROSSBOUNDARY("pdf.deliverynote.crossboundary"),
	TRANSPORT_RESPONSIBLE_CRH("pdf.transport_responsible.crh"),
	TRANSPORT_RESPONSIBLE_CUSTOMER("pdf.transport_responsible.customer"),
	TRANSPORT_RESPONSIBLE_CARRIER("pdf.transport_responsible.carrier"),
	REMARKS_TEXT("pdf.deliverynote.remarks.text"),
	FOOTER_TITLE("pdf.deliverynote.footer.title"),
	FOOTER_TEXT("pdf.deliverynote.footer.text"),
	CALIBRATION_TEXT("pdf.delivnote.eichbeleg.intro"),
	CALIBRATION_TARE("pdf.delivnote.eichbeleg.tare"),
	CALIBRATION_GROSS("pdf.delivnote.eichbeleg.gross"),
	CUST_REF_NO("pdf.deliverynote.custrefno"),
	CONTRACT_NO("pdf.deliverynote.contractno"),
	BIMODAL_ORD_WAGON_INF("pdf.deliverynote.bimodal_ord_wagon_inf"),
	DELIVERY_TYPE("pdf.deliverynote.deliverytype");
	
	private final String messageId;
	
	DeliveryNoteReferenceLabel(String messageId) {
		this.messageId = messageId;
	}
	
	public String getMessageId() {
		return messageId;
	}
}
