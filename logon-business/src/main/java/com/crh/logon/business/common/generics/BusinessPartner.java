/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.common.generics;

import org.apache.commons.lang.StringUtils;

import com.crh.logon.persistence.entities.sap.SapBpEntity;

/**
 * Class containing business partner display data.
 * 
 * @author AdNovum Informatik AG
 */
public abstract class BusinessPartner {

	private final String sapIfcId;
	private final String sapId;
	private final boolean treatAsOneTime;
	private final String phoneNumber;

	protected String firstName;
	protected String lastName;
	protected String street;
	protected String houseNumber;
	protected String streetHouseNumber;
	protected String sapCountryCd;
	protected String postalCode;
	protected String city;
	protected String vatRegistrationNumber;
	
	public BusinessPartner(final SapBpEntity sapBp) {

		this.sapIfcId = sapBp.getSapIfcId();
		this.sapId = sapBp.getSapId();
		this.treatAsOneTime = sapBp.getTreatAsOnetimeFlag();
		this.phoneNumber = sapBp.getPhoneNumber();
		
	}
	
	public String getStreetHouseNumber() {
		return StringUtils.defaultString(streetHouseNumber);
	}
	
	public String getSapIfcId() {
		return StringUtils.defaultString(sapIfcId);
	}
	
	public String getSapId() {
		return StringUtils.defaultString(sapId);
	}

	public String getFirstName() {
		return StringUtils.defaultString(firstName);
	}
	
	public String getLastName() {
		return StringUtils.defaultString(lastName);
	}

	public String getStreet() {
		return StringUtils.defaultString(street);
	}

	public String getHouseNumber() {
		return StringUtils.defaultString(houseNumber);
	}

	public String getSapCountryCd() {
		return StringUtils.defaultString( sapCountryCd);
	}

	public String getPostalCode() {
		return StringUtils.defaultString(postalCode);
	}

	public String getCity() {
		return StringUtils.defaultString(city);
	}

	public String getVatRegistrationNumber() {
		return StringUtils.defaultString(vatRegistrationNumber);
	}

	public String getPhoneNumber() {
		return StringUtils.defaultString(phoneNumber);
	}
	
	public boolean getTreatAsOnetimeFlag() {
		return treatAsOneTime;
	}

	@Override
	public String toString() {
		return "BusinessPartner [sapIfcId=" + sapIfcId + ", sapId=" + sapId
				+ ", treatAsOneTime=" + treatAsOneTime + ", phoneNumber="
				+ phoneNumber + ", name1=" + firstName + ", name2=" + lastName
				+ ", street=" + street + ", houseNumber=" + houseNumber
				+ ", streetHouseNumber=" + streetHouseNumber
				+ ", sapCountryCd=" + sapCountryCd + ", postalCode="
				+ postalCode + ", city=" + city + ", vatRegistrationNumber="
				+ vatRegistrationNumber + "]";
	}	

}