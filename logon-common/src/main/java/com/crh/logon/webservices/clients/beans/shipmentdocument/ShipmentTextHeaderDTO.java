/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.shipmentdocument;

import java.io.Serializable;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public class ShipmentTextHeaderDTO implements Serializable {
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_text_header (E1TXTH6) / TDID
	 */
	private String textID;
	public static final String TEXT_ID_0001 = "0001";
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_text_header (E1TXTH6) / LANGUA_ISO
	 */
	private String languageKey;
	public static final String LANGUAGE_KEY_GERMAN = "Z1";
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_text_header (E1TXTH6) / shipment_general_text (E1TXTP6)
	 */
	private List<ShipmentGeneralTextDTO> shipmentGeneralTextList;

	public String getTextID() {
		return textID;
	}

	public void setTextID(String textID) {
		this.textID = textID;
	}

	public String getLanguageKey() {
		return languageKey;
	}

	public void setLanguageKey(String languageKey) {
		this.languageKey = languageKey;
	}

	public List<ShipmentGeneralTextDTO> getShipmentGeneralTextList() {
		return shipmentGeneralTextList;
	}

	public void setShipmentGeneralTextList(List<ShipmentGeneralTextDTO> shipmentGeneralTextList) {
		this.shipmentGeneralTextList = shipmentGeneralTextList;
	}
}
