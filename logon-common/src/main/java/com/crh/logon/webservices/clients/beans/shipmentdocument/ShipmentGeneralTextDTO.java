/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.shipmentdocument;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class ShipmentGeneralTextDTO implements Serializable {

	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_text_header (E1TXTH6) / shipment_general_text (E1TXTP6) / TDLINE
	 */
	private String textLine;
	public static final int TEXT_LINE_MAX_LENGTH = 132;
	
	public String getTextLine() {
		return textLine;
	}
	
	public void setTextLine(String textLine) {
		this.textLine = textLine;
	}
}
