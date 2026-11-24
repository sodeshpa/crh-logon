/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.transferdeliverydocument;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class HeaderTextsDTO implements Serializable {

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / header_text_identification (E1TXTH8) / header_texts (E1TXTP8) / TDLINE
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
