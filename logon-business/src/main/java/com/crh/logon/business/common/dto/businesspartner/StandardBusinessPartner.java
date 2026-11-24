/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.common.dto.businesspartner;

import com.crh.logon.business.common.generics.BusinessPartner;
import org.apache.commons.lang.StringUtils;

import com.crh.logon.persistence.entities.sap.SapBpEntity;

/**
 * @author AdNovum Informatik AG
 */
/**
 * Object storing sap business partner information data.
 */
public class StandardBusinessPartner extends BusinessPartner {
	
	/**
	 * Initializes a {@link BusinessPartner} which represents data
	 * of the sap business partner. Data is coming purely from the business partner
	 * entity.
	 * 
	 * @param sapBp
	 */
	public StandardBusinessPartner(final SapBpEntity sapBp) {

		super(sapBp);
		
		this.firstName = sapBp.getName1();
		this.lastName = sapBp.getName2();
		this.street = sapBp.getStreet();
		this.houseNumber = sapBp.getHouseNumber();
		this.streetHouseNumber =  sapBp.getStreet() + (!StringUtils.isEmpty(sapBp.getHouseNumber()) ? (' ' + sapBp.getHouseNumber()) : "");
		this.sapCountryCd = sapBp.getCountry().getSapCountryCd();
		this.postalCode = sapBp.getPostalCode();
		this.city = sapBp.getCity();
		this.vatRegistrationNumber = sapBp.getVatRegistrationNumber();
	}

}