/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.crh.logon.business.common.generics.BusinessPartner;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.refdata.SapCountryBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;

/**
 * Testing wrapper object containing display data of business partners
 * in different roles.
 * 
 * @author AdNovum Informatik AG
 */
public class BusinessPartnerUtilTest{



	
	@Before
	public void setUp() {
		;
	}
	
	@Test
	public void testShiptoBusinessPartner() {
				
		OrderEntity order = initStandardShiptoOrder();
		
		BusinessPartner bpData = BusinessPartnerUtil.getShiptoBusinessPartner(order);
		
		SapBpEntity shiptoSapBp = order.getShiptoSapBp();
		assertSame(bpData.getCity(), shiptoSapBp.getCity());
		assertSame(bpData.getHouseNumber(),shiptoSapBp.getHouseNumber());
		assertSame(bpData.getFirstName(), shiptoSapBp.getName1());
		assertSame(bpData.getLastName(), shiptoSapBp.getName2());
		assertSame(bpData.getSapCountryCd(), shiptoSapBp.getCountry().getSapCountryCd());
		assertSame(bpData.getStreet(), shiptoSapBp.getStreet());
		assertSame(bpData.getVatRegistrationNumber(),shiptoSapBp.getVatRegistrationNumber());
		String streetHouseNr = shiptoSapBp.getStreet() + (shiptoSapBp.getHouseNumber() != null ?  " " + shiptoSapBp.getHouseNumber() : "");
		assertEquals(bpData.getStreetHouseNumber(), streetHouseNr);
		assertSame(bpData.getPostalCode(), shiptoSapBp.getPostalCode());
	}



	protected OrderEntity initStandardShiptoOrder() {
		
		SapBpEntity bp = initSapBp();
		OrderEntity order =  new OrderEntity();
		
		order.setShiptoSapBp(bp);
		return order;
	}


	@Test
	public void testSoldtoBusinessPartner() {
		
		OrderEntity order = initStandardSoldtoOrder();

		BusinessPartner bpData = BusinessPartnerUtil.getSoldtoBusinessPartner(order);
		
		SapBpEntity soldtoSapBp = order.getSoldtoSapBp();
		assertSame(bpData.getCity(), soldtoSapBp.getCity());
		assertSame(bpData.getHouseNumber(),soldtoSapBp.getHouseNumber());
		assertSame(bpData.getFirstName(), soldtoSapBp.getName1());
		assertSame(bpData.getLastName(), soldtoSapBp.getName2());
		assertSame(bpData.getSapCountryCd(), soldtoSapBp.getCountry().getSapCountryCd());
		assertSame(bpData.getStreet(), soldtoSapBp.getStreet());
		assertSame(bpData.getVatRegistrationNumber(),soldtoSapBp.getVatRegistrationNumber());
		String streetHouseNr = soldtoSapBp.getStreet() + (soldtoSapBp.getHouseNumber() != null ?  " " + soldtoSapBp.getHouseNumber() : "");
		assertEquals(bpData.getStreetHouseNumber(), streetHouseNr);
		assertSame(bpData.getPostalCode(), soldtoSapBp.getPostalCode());
		
	}



	protected OrderEntity initStandardSoldtoOrder() {
		OrderEntity order = new OrderEntity();
		SapBpEntity bp = initSapBp();
		
		order.setSoldtoSapBp(bp);
		return order;
	}

	@Test
	public void testOneTimeSoldtoBusinessPartner() {
		
		OrderEntity order = initOneTimeSoldtoOrder();
		
		BusinessPartner bpData = BusinessPartnerUtil.getSoldtoBusinessPartner(order);

		assertOneTimeSoldto(bpData, order);
	}

	/**
	 * In the case of one time soldto the data returned must correspond with the 
	 * one time data.
	 * @param bpData
	 * @param order TODO
	 */
	private void assertOneTimeSoldto(BusinessPartner bpData, OrderEntity order) {
		assertSame(bpData.getCity(), order.getOneTimeSoldtoCity());
		assertSame(bpData.getStreet(),"");
		assertSame(bpData.getHouseNumber(),"");
		assertSame(bpData.getFirstName(), order.getOneTimeSoldtoName());
		assertSame(bpData.getLastName(), "");
		assertSame(bpData.getSapCountryCd(), order.getOneTimeSoldtoCountry());
		assertSame(bpData.getStreet(), "");
		assertSame(bpData.getStreetHouseNumber(),order.getOneTimeSoldtoStreetHouseno());
		assertSame(bpData.getVatRegistrationNumber(),order.getOneTimeSoldtoVatNo());
		assertSame(bpData.getPostalCode(), order.getOneTimeSoldtoPostalCode());
	}
	
	@Test
	public void testOneTimeShiptoBusinessPartner() {
		

		//make sure the onetimeshipto fields are initialized
		OrderEntity order = initOneTimeShiptoOrder();
		
		BusinessPartner bpData = BusinessPartnerUtil.getShiptoBusinessPartner(order);
		
		assertSame(bpData.getCity(), order.getLnfSiteCity());
		assertSame(bpData.getStreetHouseNumber(),order.getLnfSiteStreetHouseNo());
		assertSame(bpData.getHouseNumber(), "");
		assertSame(bpData.getFirstName(), order.getLnfSiteName());
		assertSame(bpData.getLastName(), "");
		assertSame(bpData.getSapCountryCd(), order.getLnfSiteCountry());
		assertSame(bpData.getStreet(), "");
		assertSame(bpData.getVatRegistrationNumber(),order.getLnfSiteVatNo());
		assertSame(bpData.getPostalCode(), order.getLnfSitePostalCode());
		
	}
	
	@Test
	public void testPayerBusinessPartner() {
		OrderEntity order = initStandardPayerOrder();
		
		BusinessPartner bpData = BusinessPartnerUtil.getPayerBusinessPartner(order);
		SapBpEntity payerSapBp = order.getPayerSapBp();
		assertSame(bpData.getCity(), payerSapBp.getCity());
		assertSame(bpData.getHouseNumber(),payerSapBp.getHouseNumber());
		assertSame(bpData.getFirstName(), payerSapBp.getName1());
		assertSame(bpData.getLastName(), payerSapBp.getName2());
		assertSame(bpData.getSapCountryCd(), payerSapBp.getCountry().getSapCountryCd());
		assertSame(bpData.getStreet(), payerSapBp.getStreet());
		String streethouseNr = payerSapBp.getStreet() + (payerSapBp.getHouseNumber() != null ?  " " +payerSapBp.getHouseNumber() : "");
		assertEquals(bpData.getStreetHouseNumber(), streethouseNr);
		assertSame(bpData.getVatRegistrationNumber(),payerSapBp.getVatRegistrationNumber());
		assertSame(bpData.getPostalCode(), payerSapBp.getPostalCode());
	}



	private OrderEntity initStandardPayerOrder() {
		OrderEntity order = new OrderEntity();
		SapBpEntity bp = initSapBp();
		
		order.setPayerSapBp(bp);
		order.setSoldtoSapBp(bp);
		return order;
	}
	
	@Test
	public void testBilltoBusinessPartner() {
		
		OrderEntity order = initStandardBilltoOrder();
		
		BusinessPartner bpData = BusinessPartnerUtil.getBilltoBusinessPartner(order);
		SapBpEntity billtoSapBp = order.getBilltoSapBp();
		assertSame(bpData.getCity(), billtoSapBp.getCity());
		assertSame(bpData.getHouseNumber(),billtoSapBp.getHouseNumber());
		assertSame(bpData.getFirstName(), billtoSapBp.getName1());
		assertSame(bpData.getLastName(), billtoSapBp.getName2());
		assertSame(bpData.getSapCountryCd(), billtoSapBp.getCountry().getSapCountryCd());
		assertSame(bpData.getStreet(), billtoSapBp.getStreet());
		String streetHouseNr = billtoSapBp.getStreet() + (billtoSapBp.getHouseNumber() != null ? " " + billtoSapBp.getHouseNumber() : "");
		assertEquals(bpData.getStreetHouseNumber(), streetHouseNr);
		assertSame(bpData.getVatRegistrationNumber(),billtoSapBp.getVatRegistrationNumber());
		assertSame(bpData.getPostalCode(), billtoSapBp.getPostalCode());
	}



	private OrderEntity initStandardBilltoOrder() {
		OrderEntity order = new OrderEntity();
		SapBpEntity bp = initSapBp();
		
		order.setBilltoSapBp(bp);
		order.setSoldtoSapBp(bp);
		return order;
	}
	
	@Test
	public void testBilltoEqualsOnetimeSoldtoPartner() {

		//LOGON-4576
		/**
		 * Case soldto is a one-time soldto, and the billto ID
		 * equals the soldto.
		 */
		OrderEntity order = initOneTimeSoldtoOrder();
		
		order.setBilltoSapBp(order.getSoldtoSapBp());
		
		BusinessPartner bpData = BusinessPartnerUtil.getBilltoBusinessPartner(order);
		
		assertOneTimeSoldto(bpData, order);
	}
	
	@Test
	public void testPayerEqualsOnetimeSoldtoPartner() {

		//LOGON-4576
		/**
		 * Case soldto is a one-time soldto, and the payer ID
		 * equals the soldto.
		 */
		OrderEntity order = initOneTimeSoldtoOrder();
		
		order.setPayerSapBp(order.getSoldtoSapBp());
		
		BusinessPartner bpData = BusinessPartnerUtil.getPayerBusinessPartner(order);
		
		assertOneTimeSoldto(bpData, order);
	}
	
	/**
	 * Initializes normal fields of a simple business partner.
	 * @return
	 */
	protected SapBpEntity initSapBp() {
		SapBpEntity bp = new SapBpEntity();
		
		bp.setTreatAsOnetimeFlag(Boolean.FALSE);
		bp.setSapBpId(new Long(-1));
		bp.setName1("setName1");
		bp.setName2("setName2");
		bp.setHouseNumber("setHouseNumber");
		bp.setPostalCode("setPostalCode");
		bp.setStreet("setStreet");
		bp.setCity("setCity");
		SapCountryBsEntity sapCountryBsEntity = new SapCountryBsEntity();
		sapCountryBsEntity.setSapCountryCd("sapCountryCd");
		bp.setCountry(sapCountryBsEntity);
		bp.setVatRegistrationNumber("setVatRegistrationNumber");
		return bp;
	}
	
	/**
	 * Initializes an order with one time soldto business partner data.
	 * @param order
	 */
	protected OrderEntity initOneTimeSoldtoOrder() {
		
		OrderEntity order = new OrderEntity();
		
		SapBpEntity bp = initSapBp();
		
		bp.setTreatAsOnetimeFlag(Boolean.TRUE);
		
		order.setOneTimeSoldtoName("oneTimeSoldtoName");
		order.setOneTimeSoldtoStreetHouseno("oneTimeSoldtoStreetHouseno");
		order.setOneTimeSoldtoCountry("oneTimeSoldtoCountry");
		order.setOneTimeSoldtoPostalCode("oneTimeSoldtoPostalCode");
		order.setOneTimeSoldtoVatNo("oneTimeSoldtoVatNo");
		order.setOneTimeSoldtoCity("oneTimeSoldtoCity");
		
		order.setSoldtoSapBp(bp);
		
		return order;
	}

	/**
	 * Initializes an order with one time shipto business partner data.
	 * @param order
	 */
	protected OrderEntity initOneTimeShiptoOrder() {
		OrderEntity order = new OrderEntity();
		
		SapBpEntity bp = initSapBp();
		
		bp.setTreatAsOnetimeFlag(Boolean.TRUE);
		
		order.setLnfSiteCity("lnfSiteCity");
		order.setLnfSiteCountry("lnfSiteCountry");
		order.setLnfSiteName("lnfSiteName");
		order.setLnfSitePostalCode("lnfSitePostalCode");
		order.setLnfSiteStreetHouseNo("lnfSiteStreetHouseNo");
		order.setLnfSiteVatNo("lnfSiteVatNo");
		
		order.setShiptoSapBp(bp);
		return order;
	}

}