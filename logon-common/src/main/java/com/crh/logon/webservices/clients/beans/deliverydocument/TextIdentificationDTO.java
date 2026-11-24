/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.deliverydocument;

import java.io.Serializable;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public class TextIdentificationDTO implements Serializable {

	private static final long serialVersionUID = 1167599526043127319L;
	
	/** Delivery note text */
	public static final String TEXT_ID_Z003 = "Z003";       
	/** Name of the Carrier */
	public static final String TEXT_ID_Z009 = "Z009";
	/** Truck license plate number */
	public static final String TEXT_ID_Z010 = "Z010";
	/** Driver's name */
	public static final String TEXT_ID_Z011 = "Z011";
	/** Attorney Name + Power of attorney's first day of validity */
	public static final String TEXT_ID_Z012 = "Z012";
	/** Etran number */
	public static final String TEXT_ID_Z013 = "Z013";
	
	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_text_identification (E1EDKT1) / TDID
	 */
	private String textID;
	
	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_text_identification (E1EDKT1) / TSSPRAS_ISO
	 */
	private String languageISO;
	public static final String LANGUAGE_ISO_GERMAN = "Z1";
	
	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_text_identification (E1EDKT1) / document_header_texts (E1EDKT2)
	 */
	private List<TextsDTO> textsList;
	
	public String getTextID() {
		return textID;
	}

	public void setTextID(String textID) {
		this.textID = textID;
	}

	public String getLanguageISO() {
		return languageISO;
	}

	public void setLanguageISO(String languageISO) {
		this.languageISO = languageISO;
	}

	public List<TextsDTO> getTextsList() {
		return textsList;
	}

	public void setTextsList(List<TextsDTO> textsList) {
		this.textsList = textsList;
	}
}
