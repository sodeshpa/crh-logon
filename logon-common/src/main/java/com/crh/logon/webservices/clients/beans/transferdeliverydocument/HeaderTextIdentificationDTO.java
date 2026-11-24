/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.transferdeliverydocument;

import java.io.Serializable;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public class HeaderTextIdentificationDTO implements Serializable {

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / header_text_identification (E1TXTH8) / TDID
	 */
	private String textID;
	public static final String TEXT_ID_ZS01 = "ZS01";
	
	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / header_text_identification (E1TXTH8) / LANGUA_ISO
	 */
	private String languageKey;
	public static final String LANGUAGE_KEY_GERMAN = "Z1";

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / header_text_identification (E1TXTH8) / header_texts (E1TXTP8)
	 */
	private List<HeaderTextsDTO> headerTextsList;

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

	public List<HeaderTextsDTO> getHeaderTextsList() {
		return headerTextsList;
	}

	public void setHeaderTextsList(List<HeaderTextsDTO> headerTextsList) {
		this.headerTextsList = headerTextsList;
	}
}
