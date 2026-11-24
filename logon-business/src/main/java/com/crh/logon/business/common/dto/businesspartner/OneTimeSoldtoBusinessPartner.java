/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.common.dto.businesspartner;

import com.crh.logon.business.common.generics.BusinessPartner;
import com.crh.logon.persistence.entities.common.OrderEntity;

/**
 * @author AdNovum Informatik AG
 */
public class OneTimeSoldtoBusinessPartner extends BusinessPartner{

	/**
	 * Initializes a {@link BusinessPartner} from the  order parameter.
	 * @param {@link OrderEntity} order
	 */
	public OneTimeSoldtoBusinessPartner(final OrderEntity order){

		super(order.getSoldtoSapBp());

		this.firstName = order.getOneTimeSoldtoName();
		this.lastName = null;	
		this.street = null;
		this.houseNumber = null;
		this.streetHouseNumber = order.getOneTimeSoldtoStreetHouseno();
		this.sapCountryCd = order.getOneTimeSoldtoCountry();
		this.postalCode = order.getOneTimeSoldtoPostalCode();
		this.city = order.getOneTimeSoldtoCity();
		this.vatRegistrationNumber = order.getOneTimeSoldtoVatNo();
	}

}
