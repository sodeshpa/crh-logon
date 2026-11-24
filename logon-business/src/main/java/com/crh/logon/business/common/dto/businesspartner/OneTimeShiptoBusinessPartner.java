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
public class OneTimeShiptoBusinessPartner extends BusinessPartner {

	/**
	 * Initializes a {@link BusinessPartner} from the  order parameter.
	 * @param {@link OrderEntity} order
	 */
	public OneTimeShiptoBusinessPartner(final OrderEntity order){
		
		super(order.getShiptoSapBp());
		
		this.firstName = order.getLnfSiteName();
		this.lastName = null;
		this.street = null;
		this.streetHouseNumber = order.getLnfSiteStreetHouseNo();
		this.houseNumber = null;
		this.sapCountryCd = order.getLnfSiteCountry();
		this.postalCode = order.getLnfSitePostalCode();
		this.city = order.getLnfSiteCity();
		this.vatRegistrationNumber = order.getLnfSiteVatNo();

	}

}
