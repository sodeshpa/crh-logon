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
public class PartnerInformationDTO implements Serializable {

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_partner_information (E1EDKA1) / PARVW
	 */
	private String partnerFunction;
	public static final String PARTNER_FUNCTION_SOLD_TO = "AG";
	public static final String PARTNER_FUNCTION_SHIP_TO = "WE";
	public static final String PARTNER_FUNCTION_BILL_TO = "RE";
	public static final String PARTNER_FUNCTION_PAYER = "RG";
	public static final String PARTNER_FUNCTION_CARRIER = "ZC";
	public static final String PARTNER_FUNCTION_FORWARDING_AGENT = "SP";
	
	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_partner_information (E1EDKA1) / PARTN
	 */
	private String partnerNumber;

	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_partner_information (E1EDKA1) / NAME1
	 */
	private String name1;
	public static final int NAME1_MAX_LENGTH = 35;
	
	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_partner_information (E1EDKA1) / NAME2
	 */
	private String name2;
	public static final int NAME2_MAX_LENGTH = 35;
	
	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_partner_information (E1EDKA1) / NAME3
	 */
	private String name3;
	
	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_partner_information (E1EDKA1) / STRAS
	 */
	private String streetAndHouseNumber1;
	public static final int STREET_AND_HOUSE_NUMBER1_MAX_LENGTH = 35;
	
	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_partner_information (E1EDKA1) / STRS2
	 */
	private String streetAndHouseNumber2;
	
	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_partner_information (E1EDKA1) / ORT01
	 */
	private String city;
	
	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_partner_information (E1EDKA1) / PSTLZ
	 */
	private String postalCode;
	
	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_partner_information (E1EDKA1) / LAND1
	 */
	private String countryKey;
	
	/**
	 * WS field to SAP: SalesDocumentCreation / sales_document_notification / document_header_partner_information (E1EDKA1) / ORGTX
	 */
	private String transportationZone;
	
	/**
	 * WS field to SAP: SalesDocumentCreation / document_header_partner_information / document_header_partner_information_additional_data / STCEG
	 */
	private String oneTimeSoldtoVatNo;

	public String getPartnerFunction() {
		return partnerFunction;
	}

	public void setPartnerFunction(String partnerFunction) {
		this.partnerFunction = partnerFunction;
	}

	public String getPartnerNumber() {
		return partnerNumber;
	}

	public void setPartnerNumber(String partnerNumber) {
		this.partnerNumber = partnerNumber;
	}

	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public String getName3() {
		return name3;
	}

	public void setName3(String name3) {
		this.name3 = name3;
	}

	public String getStreetAndHouseNumber1() {
		return streetAndHouseNumber1;
	}

	public void setStreetAndHouseNumber1(String streetAndHouseNumber1) {
		this.streetAndHouseNumber1 = streetAndHouseNumber1;
	}

	public String getStreetAndHouseNumber2() {
		return streetAndHouseNumber2;
	}

	public void setStreetAndHouseNumber2(String streetAndHouseNumber2) {
		this.streetAndHouseNumber2 = streetAndHouseNumber2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountryKey() {
		return countryKey;
	}

	public void setCountryKey(String countryKey) {
		this.countryKey = countryKey;
	}

	public String getTransportationZone() {
		return transportationZone;
	}

	public void setTransportationZone(String transportationZone) {
		this.transportationZone = transportationZone;
	}

	public String getOneTimeSoldtoVatNo() {
		return oneTimeSoldtoVatNo;
	}

	public void setOneTimeSoldtoVatNo(String oneTimeSoldtoVatNo) {
		this.oneTimeSoldtoVatNo = oneTimeSoldtoVatNo;
	}
}
