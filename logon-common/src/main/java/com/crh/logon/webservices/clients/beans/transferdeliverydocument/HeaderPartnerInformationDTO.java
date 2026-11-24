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
public class HeaderPartnerInformationDTO implements Serializable {

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / header_partner_information (E1ADRM1) / PARTNER_Q
	 */
	private String qualifierForPartnerFunction;
	public static final String QUALIFIER_FOR_PARTNER_FUNCTION_SOLD_TO = "AG";
	public static final String QUALIFIER_FOR_PARTNER_FUNCTION_SHIP_TO = "WE";
	public static final String QUALIFIER_FOR_PARTNER_FUNCTION_BILL_TO = "RE";
	public static final String QUALIFIER_FOR_PARTNER_FUNCTION_PAYER = "RG";
	public static final String QUALIFIER_FOR_PARTNER_FUNCTION_CARRIER = "ZC";
	public static final String QUALIFIER_FOR_PARTNER_FUNCTION_FORWARDING_AGENT = "SP";
	public static final String QUALIFIER_FOR_PARTNER_FUNCTION_VENDOR = "LF";

	/**
	 * WS field to SAP: TransferDeliveryCreation / transfer_delivery_creation / header_partner_information (E1ADRM1) / PARTNER_ID
	 */
	private String partnerNoSAP;

	public String getQualifierForPartnerFunction() {
		return qualifierForPartnerFunction;
	}

	public void setQualifierForPartnerFunction(String qualifierForPartnerFunction) {
		this.qualifierForPartnerFunction = qualifierForPartnerFunction;
	}

	public String getPartnerNoSAP() {
		return partnerNoSAP;
	}

	public void setPartnerNoSAP(String partnerNoSAP) {
		this.partnerNoSAP = partnerNoSAP;
	}
}
