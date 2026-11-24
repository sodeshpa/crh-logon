/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.deliverydocument;

import java.util.ArrayList;
import java.util.List;

import com.crh.logon.utils.StringUtils;
import com.crh.logon.webservices.clients.util.WebserviceClientUtil;

import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ABTNR_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ARKTX_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.AUART_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.AUGRU_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.BELNRP_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.BELNR_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.BELNR_type3;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.BELNR_type5;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.BSARK_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.BSTNK_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.CURCY_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Document_header_date;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Document_header_general_data;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Document_header_organizational_data;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Document_header_partner_information;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Document_header_partner_information_additional_data;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Document_header_qualifier_extra_data;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Document_header_reference_data;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Document_header_terms_of_delivery;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Document_header_text_identification;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Document_header_texts;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Document_item_general_data;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Document_item_object_identification;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Document_item_reference_data;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.FBUDA_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Function_code;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.GEWEI_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.INCO1_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.LAND1_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.LGORT_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.LIFEX_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.MATNR_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.MENEE_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.MENGE_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.MESCOD_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.NAME1_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.NAME2_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.NAME3_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.NTGEW_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ORGTX_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ORT01_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Order_item_additional_data;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Order_item_transport_data;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.PARTN_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.PARVW_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.POSEX_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.POSNR_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.PREIS_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.PRSDT_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.PSTLZ_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Pricing_related_info;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.REFLIFEXPOS_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.REFLIFEX_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.REFLIFEX_type3;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.SNDPRN_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.SPART_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.STCEG_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.STRAS_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.STRS2_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.SalesDocumentCreation;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Sales_document_creation;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.TDID_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.TDLINE_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.TSSPRAS_ISO_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.VBELN_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.VKORG_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.VSART_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.VSTEL_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.VTWEG_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.WADAT_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.WAUHR_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.WERKS_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZBRGEW_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZCUSADD_BEZ_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZCUSADD_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZDELI_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZENTWG_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZEXTWG_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZFREECHR_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZGEWEI_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZINCLOAD_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZINVOICE_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZNTGEW_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZPTRATY_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZQUALZ_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZRETURN_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZROUTE_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZTRAID_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZTRATY_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZTRPLATE_type1;
import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.ZVSART_type1;

/**
 * @author AdNovum Informatik AG
 */
public class SalesDocumentAssembler {
	
	public static String getXml(SalesDocumentDTO salesDocument) {
		
		SalesDocumentCreation salesDocumentCreation = createSalesDocumentCreation(salesDocument);
		
		return WebserviceClientUtil.getSerializedString(salesDocumentCreation, "SalesDocumentCreation");
	}

	public static SalesDocumentCreation createSalesDocumentCreation(SalesDocumentDTO salesDocument) {

		SalesDocumentCreation salesDocumentCreation = new SalesDocumentCreation();
		Sales_document_creation salesDocumentNotification = new Sales_document_creation();

		Function_code functionCode = new Function_code();
		if (salesDocument.getFunction() != null) {
			MESCOD_type1 mescod = new MESCOD_type1();
			mescod.setMESCOD_type0(salesDocument.getFunction());
			functionCode.setMESCOD(mescod);
		}

		if (salesDocument.getPartnerNumberOfSender() != null) {
			SNDPRN_type1 sndPrn = new SNDPRN_type1();
			sndPrn.setSNDPRN_type0(salesDocument.getPartnerNumberOfSender());
			functionCode.setSNDPRN(sndPrn);
		}
		salesDocumentNotification.setFunction_code(functionCode);

		Document_header_general_data documentHeaderGeneralData = new Document_header_general_data();
		if (salesDocument.getCurrency() != null) {
			CURCY_type1 curcy = new CURCY_type1();
			curcy.setCURCY_type0(salesDocument.getCurrency());
			documentHeaderGeneralData.setCURCY(curcy);
		}
		if (salesDocument.getSalesDocumentNumber() != null) {
			BELNR_type1 belnrSalesDocumentNumber = new BELNR_type1();
			belnrSalesDocumentNumber.setBELNR_type0(salesDocument.getSalesDocumentNumber());
			documentHeaderGeneralData.setBELNR(belnrSalesDocumentNumber);
		}
		if (salesDocument.getSpIndicator() != null) {
			ABTNR_type1 spIndicator = new ABTNR_type1();
			spIndicator.setABTNR_type0(salesDocument.getSpIndicator());
			documentHeaderGeneralData.setABTNR(spIndicator);
		}
		if (salesDocument.getOrderReason() != null) {
			AUGRU_type1 augru = new AUGRU_type1();
			augru.setAUGRU_type0(salesDocument.getOrderReason());
			documentHeaderGeneralData.setAUGRU(augru);
		}
		
		final String shippingConditions = salesDocument.getShippingConditions();
		if (shippingConditions != null) {			
			VSART_type1 vsart = new VSART_type1();
			vsart.setVSART_type0(shippingConditions);
			documentHeaderGeneralData.setVSART(vsart);
		}
		
		final ZVSART_type1 zvsart;
		final String shippingType = salesDocument.getShippingType();
		if (shippingType != null) {
			zvsart = new ZVSART_type1();
			zvsart.setZVSART_type0(shippingType);
		}
		else {
			zvsart = null;
		}
		
		Pricing_related_info pricingRelatedInfo = new Pricing_related_info();
		if (salesDocument.getIncompleteLoading() != null) {
			ZINCLOAD_type1 zincload = new ZINCLOAD_type1();
			zincload.setZINCLOAD_type0(salesDocument.getIncompleteLoading());
			pricingRelatedInfo.setZINCLOAD(zincload);
		}
		if (salesDocument.getMeansOfTransportType() != null) {
			ZTRATY_type1 ztraty = new ZTRATY_type1();
			ztraty.setZTRATY_type0(salesDocument.getMeansOfTransportType());
			pricingRelatedInfo.setZTRATY(ztraty);
		}
		if (salesDocument.getRoute() != null) {
			ZROUTE_type1 zroute = new ZROUTE_type1();
			zroute.setZROUTE_type0(salesDocument.getRoute());
			pricingRelatedInfo.setZROUTE(zroute);
		}
		if (salesDocument.getAllItemsNetWeight() != null) {
			ZNTGEW_type1 zntgew = new ZNTGEW_type1();
			zntgew.setZNTGEW_type0(salesDocument.getAllItemsNetWeight());
			pricingRelatedInfo.setZNTGEW(zntgew);
		}

		if (salesDocument.getAllItemsGrossWeightInKg() != null) {
			ZBRGEW_type1 zbrgew = new ZBRGEW_type1();
			zbrgew.setZBRGEW_type0(salesDocument.getAllItemsGrossWeightInKg());
			pricingRelatedInfo.setZBRGEW(zbrgew);
		}
		if (salesDocument.getWeightUnit() != null) {
			ZGEWEI_type1 zgewei = new ZGEWEI_type1();
			zgewei.setZGEWEI_type0(salesDocument.getWeightUnit());
			pricingRelatedInfo.setZGEWEI(zgewei);
		}
		if (salesDocument.getEntryWeightOfTruck() != null) {
			ZENTWG_type1 zentwg = new ZENTWG_type1();
			zentwg.setZENTWG_type0(salesDocument.getEntryWeightOfTruck());
			pricingRelatedInfo.setZENTWG(zentwg);
		}
		if (salesDocument.getExitWeightOfTruck() != null) {
			ZEXTWG_type1 zextwg = new ZEXTWG_type1();
			zextwg.setZEXTWG_type0(salesDocument.getExitWeightOfTruck());
			pricingRelatedInfo.setZEXTWG(zextwg);
		}
		if (salesDocument.getSapVehicleNo() != null) {
			ZTRAID_type1 ztraid = new ZTRAID_type1();
			ztraid.setZTRAID_type0(salesDocument.getSapVehicleNo());
			pricingRelatedInfo.setZTRAID(ztraid);
		}
		String licensePlateNumber = salesDocument.getLicensePlateNumber();
		if (licensePlateNumber != null) {
			licensePlateNumber = StringUtils.removeLeadingZeros(licensePlateNumber);
			ZTRPLATE_type1 ztrplate = new ZTRPLATE_type1();
			ztrplate.setZTRPLATE_type0(licensePlateNumber);
			pricingRelatedInfo.setZTRPLATE(ztrplate);
		}
		if (zvsart != null) {
			pricingRelatedInfo.setZVSART(zvsart);
		}
		documentHeaderGeneralData.setPricing_related_info(pricingRelatedInfo);

		List<Document_header_qualifier_extra_data> headerQualifierExtraDataList = new ArrayList<Document_header_qualifier_extra_data>();
		List<QualifierExtraDataDTO> headerQualifierExtraDataDTOList = salesDocument.getQualifierExtraDataList();
		for (QualifierExtraDataDTO headerQualifierExtraDataDTO : headerQualifierExtraDataDTOList) {
			Document_header_qualifier_extra_data headerQualifierExtraData = new Document_header_qualifier_extra_data();
			
			if (headerQualifierExtraDataDTO.getQualifierType() != null) {
				ZQUALZ_type1 zqualz = new ZQUALZ_type1();
				zqualz.setZQUALZ_type0(headerQualifierExtraDataDTO.getQualifierType());
				headerQualifierExtraData.setZQUALZ(zqualz);
			}
			if (headerQualifierExtraDataDTO.getDataValue1() != null) {
				ZCUSADD_type1 zcusadd = new ZCUSADD_type1();
				zcusadd.setZCUSADD_type0(headerQualifierExtraDataDTO.getDataValue1());
				headerQualifierExtraData.setZCUSADD(zcusadd);
			}
			if (headerQualifierExtraDataDTO.getDataValue2() != null) {
				ZCUSADD_BEZ_type1 zcusadd = new ZCUSADD_BEZ_type1();
				zcusadd.setZCUSADD_BEZ_type0(headerQualifierExtraDataDTO.getDataValue2());
				headerQualifierExtraData.setZCUSADD_BEZ(zcusadd);
			}
			
			headerQualifierExtraDataList.add(headerQualifierExtraData);
		}
		documentHeaderGeneralData.setDocument_header_qualifier_extra_data(headerQualifierExtraDataList.toArray(new Document_header_qualifier_extra_data[headerQualifierExtraDataList.size()]));

		salesDocumentNotification.setDocument_header_general_data(documentHeaderGeneralData);

		Document_header_organizational_data documentHeaderOrganizationalData[] = new Document_header_organizational_data[1];
		documentHeaderOrganizationalData[0] = new Document_header_organizational_data();
		if (salesDocument.getSalesOrganization() != null) {
			VKORG_type1 vkorg = new VKORG_type1();
			vkorg.setVKORG_type0(salesDocument.getSalesOrganization());
			documentHeaderOrganizationalData[0].setVKORG(vkorg);
		}
		if (salesDocument.getDistributionChannel() != null) {
			VTWEG_type1 vtweg = new VTWEG_type1();
			vtweg.setVTWEG_type0(salesDocument.getDistributionChannel());
			documentHeaderOrganizationalData[0].setVTWEG(vtweg);
		}
		if (salesDocument.getDivision() != null) {
			SPART_type1 spart = new SPART_type1();
			spart.setSPART_type0(salesDocument.getDivision());
			documentHeaderOrganizationalData[0].setSPART(spart);
		}
		if (salesDocument.getSalesOrderType() != null) {
			AUART_type1 auart = new AUART_type1();
			auart.setAUART_type0(salesDocument.getSalesOrderType());
			documentHeaderOrganizationalData[0].setAUART(auart);
		}
		salesDocumentNotification.setDocument_header_organizational_data(documentHeaderOrganizationalData);

		Document_header_date documentHeaderDate[] = new Document_header_date[1];
		documentHeaderDate[0] = new Document_header_date();
		if (salesDocument.getDeliveryDate() != null) {
			WADAT_type1 wadat = new WADAT_type1();
			wadat.setWADAT_type0(salesDocument.getDeliveryDate());
			documentHeaderDate[0].setWADAT(wadat);
		}
		if (salesDocument.getLoadingDate() != null) {
			PRSDT_type1 prsdt = new PRSDT_type1();
			prsdt.setPRSDT_type0(salesDocument.getLoadingDate());
			documentHeaderDate[0].setPRSDT(prsdt);
		}
		if (salesDocument.getServiceRenderedDate() != null) {
			FBUDA_type1 fbuda = new FBUDA_type1();
			fbuda.setFBUDA_type0(salesDocument.getServiceRenderedDate());
			documentHeaderDate[0].setFBUDA(fbuda);
		}
		if (salesDocument.getRealDeliveryTime() != null) {
			WAUHR_type1 wauhr = new WAUHR_type1();
			wauhr.setWAUHR_type0(salesDocument.getRealDeliveryTime());
			documentHeaderDate[0].setWAUHR(wauhr);
		}
		salesDocumentNotification.setDocument_header_date(documentHeaderDate);

		List<Document_header_partner_information> documentHeaderPartnerInformationList = new ArrayList<Document_header_partner_information>();
		List<PartnerInformationDTO> partnerInformationList = salesDocument.getPartnerInformationList();
		for (PartnerInformationDTO partnerInformation : partnerInformationList) {
			Document_header_partner_information documentHeaderPartnerInformation = new Document_header_partner_information();
			if (partnerInformation.getPartnerFunction() != null) {
				PARVW_type1 parvw = new PARVW_type1();
				parvw.setPARVW_type0(partnerInformation.getPartnerFunction());
				documentHeaderPartnerInformation.setPARVW(parvw);
			}
			if (partnerInformation.getPartnerNumber() != null) {
				PARTN_type1 partn = new PARTN_type1();
				partn.setPARTN_type0(partnerInformation.getPartnerNumber());
				documentHeaderPartnerInformation.setPARTN(partn);
			}
			if (partnerInformation.getName1() != null) {
				NAME1_type1 name1 = new NAME1_type1();
				name1.setNAME1_type0(partnerInformation.getName1());
				documentHeaderPartnerInformation.setNAME1(name1);
			}
			if (partnerInformation.getName2() != null) {
				NAME2_type1 name2 = new NAME2_type1();
				name2.setNAME2_type0(partnerInformation.getName2());
				documentHeaderPartnerInformation.setNAME2(name2);
			}
			if (partnerInformation.getName3() != null) {
				NAME3_type1 name3 = new NAME3_type1();
				name3.setNAME3_type0(partnerInformation.getName3());
				documentHeaderPartnerInformation.setNAME3(name3);
			}
			if (partnerInformation.getStreetAndHouseNumber1() != null) {
				STRAS_type1 stras = new STRAS_type1();
				stras.setSTRAS_type0(partnerInformation.getStreetAndHouseNumber1());
				documentHeaderPartnerInformation.setSTRAS(stras);
			}
			if (partnerInformation.getStreetAndHouseNumber2() != null) {
				STRS2_type1 strs2 = new STRS2_type1();
				strs2.setSTRS2_type0(partnerInformation.getStreetAndHouseNumber2());
				documentHeaderPartnerInformation.setSTRS2(strs2);
			}
			if (partnerInformation.getCity() != null) {
				ORT01_type1 ort01 = new ORT01_type1();
				ort01.setORT01_type0(partnerInformation.getCity());
				documentHeaderPartnerInformation.setORT01(ort01);
			}
			if (partnerInformation.getPostalCode() != null) {
				PSTLZ_type1 pstlz = new PSTLZ_type1();
				pstlz.setPSTLZ_type0(partnerInformation.getPostalCode());
				documentHeaderPartnerInformation.setPSTLZ(pstlz);
			}
			if (partnerInformation.getCountryKey() != null) {
				LAND1_type1 land1 = new LAND1_type1();
				land1.setLAND1_type0(partnerInformation.getCountryKey());
				documentHeaderPartnerInformation.setLAND1(land1);
			}
			if (partnerInformation.getTransportationZone() != null) {
				ORGTX_type1 orgtx = new ORGTX_type1();
				orgtx.setORGTX_type0(partnerInformation.getTransportationZone());
				documentHeaderPartnerInformation.setORGTX(orgtx);
			}
			if (partnerInformation.getOneTimeSoldtoVatNo() != null) {
				Document_header_partner_information_additional_data document_header_partner_information_additional_data = new Document_header_partner_information_additional_data();
				STCEG_type1 stceg = new STCEG_type1();
				stceg.setSTCEG_type0(partnerInformation.getOneTimeSoldtoVatNo());				
				document_header_partner_information_additional_data.setSTCEG(stceg);
				documentHeaderPartnerInformation.setDocument_header_partner_information_additional_data(new Document_header_partner_information_additional_data[]{document_header_partner_information_additional_data});
			}
			documentHeaderPartnerInformationList.add(documentHeaderPartnerInformation);
		}
		salesDocumentNotification.setDocument_header_partner_information(documentHeaderPartnerInformationList.toArray(new Document_header_partner_information[documentHeaderPartnerInformationList.size()]));

		

		
		
		
		
		
		Document_header_reference_data documentHeaderReferenceData[] = new Document_header_reference_data[1];
		documentHeaderReferenceData[0] = new Document_header_reference_data();
		if (salesDocument.getSapContract() != null) {
			BELNR_type3 belnrSapContract = new BELNR_type3();
			belnrSapContract.setBELNR_type2(salesDocument.getSapContract());
			documentHeaderReferenceData[0].setBELNR(belnrSapContract);
		}
		if (salesDocument.getExternalDeliveryNumber() != null) {
			LIFEX_type1 lifex = new LIFEX_type1();
			lifex.setLIFEX_type0(salesDocument.getExternalDeliveryNumber());
			documentHeaderReferenceData[0].setLIFEX(lifex);
		}
		if (salesDocument.getDispatchDeliveryNumber() != null) {
			ZDELI_type1 zdeli = new ZDELI_type1();
			zdeli.setZDELI_type0(salesDocument.getDispatchDeliveryNumber());
			documentHeaderReferenceData[0].setZDELI(zdeli);
		}
		if (salesDocument.getCancelledDocument() != null) {
			REFLIFEX_type1 reflifexType1 = new REFLIFEX_type1();
			reflifexType1.setREFLIFEX_type0(salesDocument.getCancelledDocument());
			documentHeaderReferenceData[0].setREFLIFEX(reflifexType1);
		}
		if (salesDocument.getCustomerPurchaseOrder() != null) {
			BSTNK_type1 bstnk = new BSTNK_type1();
			bstnk.setBSTNK_type0(salesDocument.getCustomerPurchaseOrder());
			documentHeaderReferenceData[0].setBSTNK(bstnk);
		}
		if (salesDocument.getPurchaseOrderType() != null) {
			BSARK_type1 bsark = new BSARK_type1();
			bsark.setBSARK_type0(salesDocument.getPurchaseOrderType());
			documentHeaderReferenceData[0].setBSARK(bsark);
		}
		if (salesDocument.getLogisticExternalInvoiceNumber() != null) {
			ZINVOICE_type1 zinvoice = new ZINVOICE_type1();
			zinvoice.setZINVOICE_type0(salesDocument.getLogisticExternalInvoiceNumber());
			documentHeaderReferenceData[0].setZINVOICE(zinvoice);
		}
		if (salesDocument.getSapDelivery() != null) {
			VBELN_type1 vbeln = new VBELN_type1(); 
			vbeln.setVBELN_type0(salesDocument.getSapDelivery());
			documentHeaderReferenceData[0].setVBELN(vbeln);
		}
		salesDocumentNotification.setDocument_header_reference_data(documentHeaderReferenceData);

		Document_header_terms_of_delivery documentHeaderTermsOfDelivery[] = new Document_header_terms_of_delivery[1];
		documentHeaderTermsOfDelivery[0] = new Document_header_terms_of_delivery();
		if (salesDocument.getIncoterms() != null) {
			INCO1_type1 inco1 = new INCO1_type1();
			inco1.setINCO1_type0(salesDocument.getIncoterms());
			documentHeaderTermsOfDelivery[0].setINCO1(inco1);
		}
		salesDocumentNotification.setDocument_header_terms_of_delivery(documentHeaderTermsOfDelivery);

		List<Document_header_text_identification> documentHeaderTextIdentificationList = new ArrayList<Document_header_text_identification>();
		List<TextIdentificationDTO> textIdentificationList = salesDocument.getTextIdentificationList();
		for (TextIdentificationDTO textIdentification : textIdentificationList) {
			Document_header_text_identification documentHeaderTextIdentification = new Document_header_text_identification();
			if (textIdentification.getTextID() != null) {
				TDID_type1 tdid = new TDID_type1();
				tdid.setTDID_type0(textIdentification.getTextID());
				documentHeaderTextIdentification.setTDID(tdid);
			}
			if (textIdentification.getLanguageISO() != null) {
				TSSPRAS_ISO_type1 tssprasIso = new TSSPRAS_ISO_type1();
				tssprasIso.setTSSPRAS_ISO_type0(textIdentification.getLanguageISO());
				documentHeaderTextIdentification.setTSSPRAS_ISO(tssprasIso);
			}

			List<Document_header_texts> documentHeaderTextsList = new ArrayList<Document_header_texts>();
			List<TextsDTO> textsList = textIdentification.getTextsList();
			for (TextsDTO texts : textsList) {
				Document_header_texts documentHeaderTexts = new Document_header_texts();
				if (texts.getTextLine() != null) {
					TDLINE_type1 tdline = new TDLINE_type1();
					tdline.setTDLINE_type0(texts.getTextLine());
					documentHeaderTexts.setTDLINE(tdline);
				}
				documentHeaderTextsList.add(documentHeaderTexts);
			}
			documentHeaderTextIdentification.setDocument_header_texts(documentHeaderTextsList.toArray(new Document_header_texts[documentHeaderTextsList.size()]));

			documentHeaderTextIdentificationList.add(documentHeaderTextIdentification);
		}
		salesDocumentNotification.setDocument_header_text_identification(documentHeaderTextIdentificationList.toArray(new Document_header_text_identification[documentHeaderTextIdentificationList.size()]));

		List<Document_item_general_data> documentItemGeneralDataList = new ArrayList<Document_item_general_data>();
		List<SalesDocumentItemDTO> salesDocumentItemList = salesDocument.getSalesDocumentItemList(); 
		for (SalesDocumentItemDTO salesDocumentItem : salesDocumentItemList) {
			Document_item_general_data documentItemGeneralData = new Document_item_general_data();
			if (salesDocumentItem.getItemNumber() != null) {
				POSEX_type1 posex = new POSEX_type1();
				posex.setPOSEX_type0(salesDocumentItem.getItemNumber());
				documentItemGeneralData.setPOSEX(posex);
			}
			if (salesDocumentItem.getDeliveredQtyInSalesUom() != null) {
				MENGE_type1 menge = new MENGE_type1();
				menge.setMENGE_type0(salesDocumentItem.getDeliveredQtyInSalesUom());
				documentItemGeneralData.setMENGE(menge);
			}
			if (salesDocumentItem.getSalesUom() != null) {
				MENEE_type1 menee = new MENEE_type1();
				menee.setMENEE_type0(salesDocumentItem.getSalesUom());
				documentItemGeneralData.setMENEE(menee);
			}
			if (salesDocumentItem.getNetPrice() != null) {
				PREIS_type1 preis = new PREIS_type1();
				preis.setPREIS_type0(salesDocumentItem.getNetPrice());
				documentItemGeneralData.setPREIS(preis);
			}
			if (salesDocumentItem.getDeliveredQtyInTons() != null) {
				NTGEW_type1 ntgew = new NTGEW_type1();
				ntgew.setNTGEW_type0(salesDocumentItem.getDeliveredQtyInTons());
				documentItemGeneralData.setNTGEW(ntgew);
			}
			if (salesDocumentItem.getWeightUnit() != null) {
				GEWEI_type1 gewei = new GEWEI_type1();
				gewei.setGEWEI_type0(salesDocumentItem.getWeightUnit());
				documentItemGeneralData.setGEWEI(gewei);
			}
			if (salesDocumentItem.getPlant() != null) {
				WERKS_type1 werks = new WERKS_type1();
				werks.setWERKS_type0(salesDocumentItem.getPlant());
				documentItemGeneralData.setWERKS(werks);
			}
			if (salesDocumentItem.getStorageLocation() != null) {
				LGORT_type1 lgort = new LGORT_type1();
				lgort.setLGORT_type0(salesDocumentItem.getStorageLocation());
				documentItemGeneralData.setLGORT(lgort);
			}
			if (salesDocumentItem.getShippingPointReceivingPoint() != null) {
				VSTEL_type1 vstel = new VSTEL_type1();
				vstel.setVSTEL_type0(salesDocumentItem.getShippingPointReceivingPoint());
				documentItemGeneralData.setVSTEL(vstel);
			}

			Order_item_additional_data orderItemAdditionalData = new Order_item_additional_data();
			if (salesDocumentItem.getReturnIndicator() != null) {
				ZRETURN_type1 zreturn = new ZRETURN_type1();
				zreturn.setZRETURN_type0(salesDocumentItem.getReturnIndicator());
				orderItemAdditionalData.setZRETURN(zreturn);
			}
			if (salesDocumentItem.getFreeOfChargeIndicator() != null) {
				ZFREECHR_type1 zfreechr = new ZFREECHR_type1();
				zfreechr.setZFREECHR_type0(salesDocumentItem.getFreeOfChargeIndicator());
				orderItemAdditionalData.setZFREECHR(zfreechr);
			}
			documentItemGeneralData.setOrder_item_additional_data(orderItemAdditionalData);

			Order_item_transport_data orderItemTransportData = new Order_item_transport_data();
			if (salesDocumentItem.getMeansOfTransportType() != null) {
				ZPTRATY_type1 zptraty = new ZPTRATY_type1();
				zptraty.setZPTRATY_type0(salesDocumentItem.getMeansOfTransportType());
				orderItemTransportData.setZPTRATY(zptraty);
			}
			documentItemGeneralData.setOrder_item_transport_data(orderItemTransportData);

			Pricing_related_info pricing_related_info;
			
			Document_item_reference_data documentItemReferenceData[] = new Document_item_reference_data[1];
			documentItemReferenceData[0] = new Document_item_reference_data();
			if (salesDocumentItem.getContract() != null) {
				BELNR_type5 belnr = new BELNR_type5();
				belnr.setBELNR_type4(salesDocumentItem.getContract());
				documentItemReferenceData[0].setBELNR(belnr);
			}
			if (salesDocumentItem.getItemFromContract() != null) {
				BELNRP_type1 belnrp = new BELNRP_type1();
				belnrp.setBELNRP_type0(salesDocumentItem.getItemFromContract());
				documentItemReferenceData[0].setBELNRP(belnrp);
			}
			if (salesDocumentItem.getOriginalDocumentForReturns() != null) {
				REFLIFEX_type3 reflifex = new REFLIFEX_type3();
				reflifex.setREFLIFEX_type2(salesDocumentItem.getOriginalDocumentForReturns());
				documentItemReferenceData[0].setREFLIFEX(reflifex);
			}
			if (salesDocumentItem.getOriginalItem() != null) {
				REFLIFEXPOS_type1 reflifexpos = new REFLIFEXPOS_type1();
				reflifexpos.setREFLIFEXPOS_type0(salesDocumentItem.getOriginalItem());
				documentItemReferenceData[0].setREFLIFEXPOS(reflifexpos);
			}
			if (salesDocumentItem.getSapDeliveryItem() != null) {
				POSNR_type1 posnr = new POSNR_type1();
				posnr.setPOSNR_type0(salesDocumentItem.getSapDeliveryItem());
				documentItemReferenceData[0].setPOSNR(posnr);
			}
			documentItemGeneralData.setDocument_item_reference_data(documentItemReferenceData);

			Document_item_object_identification documentItemObjectIdentification[] = new Document_item_object_identification[1];
			documentItemObjectIdentification[0] = new Document_item_object_identification();
			if (salesDocumentItem.getMaterialNumber() != null) {
				MATNR_type1 matnr = new MATNR_type1();
				matnr.setMATNR_type0(salesDocumentItem.getMaterialNumber());
				documentItemObjectIdentification[0].setMATNR(matnr);
			}
			if (salesDocumentItem.getMaterialDescription() != null) {
				ARKTX_type1 arktx = new ARKTX_type1();
				arktx.setARKTX_type0(salesDocumentItem.getMaterialDescription());
				documentItemObjectIdentification[0].setARKTX(arktx);
			}
			documentItemGeneralData.setDocument_item_object_identification(documentItemObjectIdentification);

			documentItemGeneralDataList.add(documentItemGeneralData);
		}
		salesDocumentNotification.setDocument_item_general_data(documentItemGeneralDataList.toArray(new Document_item_general_data[documentItemGeneralDataList.size()]));

		salesDocumentCreation.setSales_document_creation(salesDocumentNotification);

		return salesDocumentCreation;
	}

}
