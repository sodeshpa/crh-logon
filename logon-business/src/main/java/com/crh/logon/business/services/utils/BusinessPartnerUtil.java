/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import com.crh.logon.business.common.dto.businesspartner.OneTimeShiptoBusinessPartner;
import com.crh.logon.business.common.dto.businesspartner.OneTimeSoldtoBusinessPartner;
import com.crh.logon.business.common.dto.businesspartner.StandardBusinessPartner;
import com.crh.logon.business.common.generics.BusinessPartner;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;

public class BusinessPartnerUtil {


	/**
	 * 
	 * Returns a {@link BusinessPartner} object which contains all information of the shipto business partner
	 * 
	 * In standard cases for business partners data is coming from 
	 * {@link SapBpEntity} itself. In special cases  dedicated fields of the {@link OrderEntity} 
	 * can contain overridden business partner information.
	 * 
	 * 
	 * @param order
	 * @return
	 */
	public static BusinessPartner getShiptoBusinessPartner(OrderEntity order) {

		final SapBpEntity shiptoSapBp = order.getShiptoSapBp();
		final Boolean treatAsOnetimeFlag = shiptoSapBp.getTreatAsOnetimeFlag();

		if (treatAsOnetimeFlag.booleanValue()) {
			
			return new OneTimeShiptoBusinessPartner(order);
		}
		else {
			
			return new StandardBusinessPartner(shiptoSapBp);
		}

	}

	/**
	 * Returns a {@link BusinessPartner} object which contains all information of the shipto business partner
	 * 
	 * In standard cases for business partners data is coming from 
	 * {@link SapBpEntity} itself. In special cases  dedicated fields of the {@link OrderEntity} 
	 * can contain overridden business partner information.
	 * 
	 * @param order
	 * @return
	 */
	public static BusinessPartner getSoldtoBusinessPartner(OrderEntity order) {

		final SapBpEntity soldtoSapBp = order.getSoldtoSapBp();
		final Boolean treatAsOnetimeFlag = soldtoSapBp.getTreatAsOnetimeFlag();

		if (treatAsOnetimeFlag.booleanValue()) {
			
			return new OneTimeSoldtoBusinessPartner(order);
		}
		else {
			
			return new StandardBusinessPartner(soldtoSapBp);
		}
	}


	/**
	 * Returns a {@link BusinessPartner} object which contains all information of the payer business partner
	 * 
	 * In standard cases for business partners data is coming from 
	 * {@link SapBpEntity} itself. In special cases  dedicated fields of the {@link OrderEntity} 
	 * can contain overridden business partner information.
	 * 
	 * @param order
	 * @return
	 */
	public static BusinessPartner getPayerBusinessPartner(OrderEntity order) {


		final SapBpEntity payer = order.getPayerSapBp();
		final SapBpEntity soldto = order.getSoldtoSapBp();

		final Long payerSapBpId = payer.getSapBpId();
		final Long soldtoSapBpId = soldto.getSapBpId();

		if (payerSapBpId.equals(soldtoSapBpId) && soldto.getTreatAsOnetimeFlag()) {
			
			return new OneTimeSoldtoBusinessPartner(order);
		}
		else {
			
			return new StandardBusinessPartner(payer);
		}

	}

	/*** 
	 * Returns a {@link BusinessPartner} object which contains all information of the payer business partner
	 * 
	 * In standard cases for business partners data is coming from 
	 * {@link SapBpEntity} itself. In special cases  dedicated fields of the {@link OrderEntity} 
	 * can contain overridden business partner information.
	 * 
	 * @param order
	 * @return
	 */
	public static BusinessPartner getBilltoBusinessPartner(OrderEntity order) {


		final SapBpEntity billto = order.getBilltoSapBp();
		final SapBpEntity soldto = order.getSoldtoSapBp();

		final Long soldtoSapBpId = soldto.getSapBpId();
		final Long billtoSapBpId = billto.getSapBpId();

		if (billtoSapBpId.equals(soldtoSapBpId) && soldto.getTreatAsOnetimeFlag()) {
			
			return new OneTimeSoldtoBusinessPartner(order);
		}
		else {	
			
			return new StandardBusinessPartner(billto);
		}
	}
	
}
