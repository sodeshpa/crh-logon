/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.deliverydocument;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class TextsDTO implements Serializable {

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_text_identification (E1EDKT1) / document_header_texts (E1EDKT2) / TDLINE
	 */
	private String textLine;
	public static final int TEXT_LINE_MAX_LENGTH = 70;
	
	public String getTextLine() {
		return textLine;
	}

	public void setTextLine(String textLine) {
		this.textLine = textLine;
	}
}
