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
public class ShipmentCentralAddressDTO implements Serializable {

	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_central_address (E1ADRM4) / PARTNER_Q
	 */
	private String qualifierForPartnerFunction;
	public static final String QUALIFIER_FOR_PARTNER_FUNCTION_CARRIER = "ZC";
	public static final String QUALIFIER_FOR_PARTNER_FUNCTION_FORWARDING_AGENT = "SP";
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_central_address (E1ADRM4) / PARTNER_ID
	 */
	private String partnerNoSap;
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_central_address (E1ADRM4) / LANGUAGE
	 */
	private String languageKey;
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_central_address (E1ADRM4) / NAME1
	 */
	private String nameOrAddressLine;
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_central_address (E1ADRM4) / STREET1
	 */
	private String houseNumberAndStreet;
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_central_address (E1ADRM4) / POSTL_COD1
	 */
	private String postalCode;
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_central_address (E1ADRM4) / CITY1
	 */
	private String townOrCity;
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_central_address (E1ADRM4) / COUNTRY1
	 */
	private String countryIndicator;
	
	/**
	 * WS field to SAP: ShipmentDocumentCreation / shipment_document_creation / shipment_header (E1EDT20) / shipment_central_address (E1ADRM4) / REGION
	 */
	private String regionState;

	public String getQualifierForPartnerFunction() {
		return qualifierForPartnerFunction;
	}

	public void setQualifierForPartnerFunction(String qualifierForPartnerFunction) {
		this.qualifierForPartnerFunction = qualifierForPartnerFunction;
	}

	public String getPartnerNoSap() {
		return partnerNoSap;
	}

	public void setPartnerNoSap(String partnerNoSap) {
		this.partnerNoSap = partnerNoSap;
	}

	public String getLanguageKey() {
		return languageKey;
	}

	public void setLanguageKey(String languageKey) {
		this.languageKey = languageKey;
	}
	
	public String getNameOrAddressLine() {
		return nameOrAddressLine;
	}

	public void setNameOrAddressLine(String nameOrAddressLine) {
		this.nameOrAddressLine = nameOrAddressLine;
	}

	public String getHouseNumberAndStreet() {
		return houseNumberAndStreet;
	}

	public void setHouseNumberAndStreet(String houseNumberAndStreet) {
		this.houseNumberAndStreet = houseNumberAndStreet;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getTownOrCity() {
		return townOrCity;
	}

	public void setTownOrCity(String townOrCity) {
		this.townOrCity = townOrCity;
	}

	public String getCountryIndicator() {
		return countryIndicator;
	}

	public void setCountryIndicator(String countryIndicator) {
		this.countryIndicator = countryIndicator;
	}

	public String getRegionState() {
		return regionState;
	}

	public void setRegionState(String regionState) {
		this.regionState = regionState;
	}
}
