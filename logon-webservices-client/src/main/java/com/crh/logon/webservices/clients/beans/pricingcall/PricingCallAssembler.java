/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.pricingcall;

import java.util.List;

import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.CITY_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.COUNTRY_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.CURRENCY_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.CURRENCY_type7;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.CUST_GROUP_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.DISTR_CHAN_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.DIVISION_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.DIVISION_type3;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.Date;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.GenericDate;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.INCOTERMS1_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ITM_NUMBER_type5;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.MATERIAL_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.MAT_PR_GRP_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.NAME_2_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.NAME_3_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.NAME_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ORD_REASON_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.PARTN_NUMB_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.PARTN_ROLE_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.PLANT_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.SALES_OFF_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.SALES_ORG_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.SHIP_COND_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.SHIP_POINT_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.STREET_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.TARGET_QTY_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.TARGET_QU_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ZFSD_PRICE_SIMULATION;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ZSSD_PS_HEADER;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ZSSD_PS_ITEMS;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ZSSD_PS_PARTNERS;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ZTSD_PS_ITEMS_type0;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ZTSD_PS_ITEMS_type1;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ZTSD_PS_PARTNERS_type0;
import pricesimulation.sales.net.crh.ea.DX1_Java_Instance_PriceSimulation_OutStub.ZTSD_PS_PARTNERS_type1;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.time.DateTimeFormat;
import com.crh.logon.webservices.clients.util.WebserviceClientUtil;

/**
 * @author AdNovum Informatik AG
 */
public class PricingCallAssembler {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PricingCallAssembler.class);

	private static GenericDate getGenericDate(java.util.Date date) {
		GenericDate gd = new GenericDate();
		gd.setGenericDate(DateTimeFormat.forSap(date));
		return gd;
	}

	public static String getXml(PricingCallDTO pricingCallDto) {

		ZFSD_PRICE_SIMULATION goodsMovement = createPricingCall(pricingCallDto);

		return WebserviceClientUtil.getSerializedString(goodsMovement, "ZFSD_PRICE_SIMULATION");

	}

	/**
	 * Assembles a {@code ZFSD_PRICE_SIMULATION} object using the fields of the LOGON domain DTO. 
	 * 
	 * @param pricingCallDto the given DTO for the available logon fields for the service
	 * @return {@code ZFSD_PRICE_SIMULATION} object corresponding to the WSDL
	 */
	public static ZFSD_PRICE_SIMULATION createPricingCall(PricingCallDTO pricingCallDto) {

		ZFSD_PRICE_SIMULATION priceSimulation = new ZFSD_PRICE_SIMULATION();

		// ========== Header section ============

		ZSSD_PS_HEADER header = new ZSSD_PS_HEADER();

		PricingCallDTOHeader dtoHeader = pricingCallDto.getHeader();

		if (dtoHeader.getSalesOrg() != null) {
			SALES_ORG_type1 param = new SALES_ORG_type1();
			param.setSALES_ORG_type0(dtoHeader.getSalesOrg());
			header.setSALES_ORG(param);
		}

		if (dtoHeader.getDistributionChannel() != null) {
			DISTR_CHAN_type1 param = new DISTR_CHAN_type1();
			param.setDISTR_CHAN_type0(dtoHeader.getDistributionChannel());
			header.setDISTR_CHAN(param);
		}

		if (dtoHeader.getDivision() != null) {
			DIVISION_type3 param = new DIVISION_type3();
			param.setDIVISION_type2(dtoHeader.getDivision());
			header.setDIVISION(param);
		}

		if (dtoHeader.getSalesOffice() != null) {
			SALES_OFF_type1 param = new SALES_OFF_type1();
			param.setSALES_OFF_type0(dtoHeader.getSalesOffice());
			header.setSALES_OFF(param);
		}

		if (dtoHeader.getCustomerGroup() != null) {
			CUST_GROUP_type1 param = new CUST_GROUP_type1();
			param.setCUST_GROUP_type0(dtoHeader.getCustomerGroup());
			header.setCUST_GROUP(param);
		}

		if (dtoHeader.getOrderReason() != null) {
			ORD_REASON_type1 param = new ORD_REASON_type1();
			param.setORD_REASON_type0(dtoHeader.getOrderReason());
			header.setORD_REASON(param);
		}

		if (dtoHeader.getPriceDate() != null) {
			Date param = new Date();
			param.setObject(getGenericDate(dtoHeader.getPriceDate()));
			header.setPRICE_DATE(param);
		}

		if (dtoHeader.getShippingConditions() != null) {
			SHIP_COND_type1 param = new SHIP_COND_type1();
			param.setSHIP_COND_type0(dtoHeader.getShippingConditions());
			header.setSHIP_COND(param);
		}

		if (dtoHeader.getCurrency() != null) {
			CURRENCY_type7 param = new CURRENCY_type7();
			param.setCURRENCY_type6(dtoHeader.getCurrency());
			header.setCURRENCY(param);
		}

		// ========== Items section ============

		ZTSD_PS_ITEMS_type1 items = new ZTSD_PS_ITEMS_type1();

		PricingCallDTOItems dtoItemObj = pricingCallDto.getItems();
		List<PricingCallDTOItem> dtoItems = dtoItemObj.getItems();

		for (PricingCallDTOItem dto : dtoItems) {

			ZSSD_PS_ITEMS item = new ZSSD_PS_ITEMS();

			if (dto.getItemNumber() != null) {
				ITM_NUMBER_type5 pItmNumber = new ITM_NUMBER_type5();
				pItmNumber.setITM_NUMBER_type4(dto.getItemNumber());
				item.setITM_NUMBER(pItmNumber);
			}

			if (dto.getMaterial() != null) {
				MATERIAL_type1 pMaterial = new MATERIAL_type1();
				pMaterial.setMATERIAL_type0(dto.getMaterial());
				item.setMATERIAL(pMaterial);
			}

			if (dto.getPlant() != null) {
				PLANT_type1 pPlant = new PLANT_type1();
				pPlant.setPLANT_type0(dto.getPlant());
				item.setPLANT(pPlant);
			}

			if (dto.getTargetQty() != null) {
				TARGET_QTY_type1 pTargetQty = new TARGET_QTY_type1();
				pTargetQty.setTARGET_QTY_type0(dto.getTargetQty());
				item.setTARGET_QTY(pTargetQty);
			}

			if (dto.getTargetQuantityUom() != null) {
				TARGET_QU_type1 pTargetQu = new TARGET_QU_type1();
				pTargetQu.setTARGET_QU_type0(dto.getTargetQuantityUom());
				item.setTARGET_QU(pTargetQu);
			}

			if (dto.getIncoterms() != null) {
				INCOTERMS1_type1 pIncoterms = new INCOTERMS1_type1();
				pIncoterms.setINCOTERMS1_type0(dto.getIncoterms());
				item.setINCOTERMS1(pIncoterms);
			}

			if (dto.getDivision() != null) {
				DIVISION_type1 pDivision = new DIVISION_type1();
				pDivision.setDIVISION_type0(dto.getDivision());
				item.setDIVISION(pDivision);
			}

			if (dto.getShippingPoint() != null) {
				SHIP_POINT_type1 pShippingPoint = new SHIP_POINT_type1();
				pShippingPoint.setSHIP_POINT_type0(dto.getShippingPoint());
				item.setSHIP_POINT(pShippingPoint);
			}

			if (dto.getMaterialPricingGroup() != null) {
				MAT_PR_GRP_type1 pMaterialPricingGroup = new MAT_PR_GRP_type1();
				pMaterialPricingGroup.setMAT_PR_GRP_type0(dto.getMaterialPricingGroup());
				item.setMAT_PR_GRP(pMaterialPricingGroup);
			}

			items.addItem(item);

		}

		// ========== Partners section ============

		ZTSD_PS_PARTNERS_type1 partners = new ZTSD_PS_PARTNERS_type1();

		PricingCallDTOPartners dtoPartners = pricingCallDto.getPartners();
		List<PricingCallDTOPartner> dtoPartnerList = dtoPartners.getItems();

		for (PricingCallDTOPartner dto : dtoPartnerList) {

			ZSSD_PS_PARTNERS item = new ZSSD_PS_PARTNERS();

			if (dto.getPartnerRole() != null) {
				PARTN_ROLE_type1 pPartnerRole = new PARTN_ROLE_type1();
				pPartnerRole.setPARTN_ROLE_type0(dto.getPartnerRole());
				item.setPARTN_ROLE(pPartnerRole);
			}

			if (dto.getPartnerNumber() != null) {
				PARTN_NUMB_type1 pPartnerNumber = new PARTN_NUMB_type1();
				pPartnerNumber.setPARTN_NUMB_type0(dto.getPartnerNumber());
				item.setPARTN_NUMB(pPartnerNumber);
			}

			if (dto.getName1() != null) {
				NAME_type1 pName = new NAME_type1();
				pName.setNAME_type0(dto.getName1());
				item.setNAME(pName);
			}

			if (dto.getName2() != null) {
				NAME_2_type1 pName2 = new NAME_2_type1();
				pName2.setNAME_2_type0(dto.getName2());
				item.setNAME_2(pName2);
			}

			if (dto.getName3() != null) {
				NAME_3_type1 pName3 = new NAME_3_type1();
				pName3.setNAME_3_type0(dto.getName3());
				item.setNAME_3(pName3);
			}

			if (dto.getStreet() != null) {
				STREET_type1 pStreet = new STREET_type1();
				pStreet.setSTREET_type0(dto.getStreet());
				item.setSTREET(pStreet);
			}

			if (dto.getCountry() != null) {
				COUNTRY_type1 pCountry = new COUNTRY_type1();
				pCountry.setCOUNTRY_type0(dto.getCountry());
				item.setCOUNTRY(pCountry);
			}

			if (dto.getCity() != null) {
				CITY_type1 pCity = new CITY_type1();
				pCity.setCITY_type0(dto.getCity());
				item.setCITY(pCity);
			}

			partners.addItem(item);

		}

		// ========== Use sub elements ============

		priceSimulation.setZSSD_PS_HEADER(header);
		priceSimulation.setZTSD_PS_ITEMS(items);
		priceSimulation.setZTSD_PS_PARTNERS(partners);

		return priceSimulation;

	}

}