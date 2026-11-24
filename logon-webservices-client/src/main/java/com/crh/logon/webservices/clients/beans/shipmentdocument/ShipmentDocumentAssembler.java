/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.shipmentdocument;

import java.util.ArrayList;
import java.util.List;

import com.crh.logon.webservices.clients.util.WebserviceClientUtil;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.ADD01_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.ADD02_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.ADD03_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.ADD04_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.CITY1_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.COUNTRY1_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.DISTZ_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.DISTZ_type3;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.EXTI1_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.EXTI2_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.GESZTDA_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.GESZTD_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.GEWEI_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.IEDD_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.ISDD_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.LANGUAGE_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.LANGUA_ISO_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.LGORT_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.LIFEX_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.MATNR_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.MEDST_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.MEDST_type3;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.MESCOD_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.NAME1_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.NTANF_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.ORMNG_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.PARTNER_ID_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.PARTNER_Q_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.POSNR_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.POSTL_COD1_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.QUALF_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.REGION_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.RPANHAE_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.RPMOWA_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.SDABW_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.SHTYP_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.SNDPRN_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.SPART_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.STATUS_SD_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.STREET1_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.ShipmentDocumentCreation;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.Shipment_central_address;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.Shipment_control_data;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.Shipment_date;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.Shipment_delivery_header;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.Shipment_delivery_item;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.Shipment_document_creation;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.Shipment_general_text;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.Shipment_header;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.Shipment_header_addInfo;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.Shipment_stage;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.Shipment_text_header;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.Shipment_transport_additional_data;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.TDID_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.TDLINE_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.TEXT1_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.TEXT2_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.TEXT3_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.TEXT4_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.TKNUM_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.TPBEZ_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.VBELN_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.VKORG_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.VRKME_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.VSBED_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.VTWEG_type1;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.WARZTDA_type3;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.WERKS_type3;
import shipmentdocument.logisticdocument.net.crh.ea.DX1_Java_Instance_ShipmentDocument_OutStub.WARZTDA_type1;

import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class ShipmentDocumentAssembler {
	
	public static String getXml(ShipmentDocumentDTO shipmentDocument) {
		
		ShipmentDocumentCreation shipmentDocumentCreation = createShipmentDocumentCreation(shipmentDocument);
		
		return WebserviceClientUtil.getSerializedString(shipmentDocumentCreation, "ShipmentDocumentCreation");
	}

	public static ShipmentDocumentCreation createShipmentDocumentCreation(ShipmentDocumentDTO shipmentDocument) {

		ShipmentDocumentCreation shipmentDocumentCreation = new ShipmentDocumentCreation();
		Shipment_document_creation documentCreation = new Shipment_document_creation();

		Shipment_control_data shipmentControlData = new Shipment_control_data();
		if (shipmentDocument.getFunction() != null) {
			MESCOD_type1 mescod = new MESCOD_type1();
			mescod.setMESCOD_type0(shipmentDocument.getFunction());
			shipmentControlData.setMESCOD(mescod);
		}
		if (shipmentDocument.getPartnerNumberOfSender() != null) {
			SNDPRN_type1 sndPrn = new SNDPRN_type1();
			sndPrn.setSNDPRN_type0(shipmentDocument.getPartnerNumberOfSender());
			shipmentControlData.setSNDPRN(sndPrn);
		}
		documentCreation.setShipment_control_data(shipmentControlData);
		
		Shipment_header shipmentHeader = new Shipment_header();
		
		if (shipmentDocument.getShipmentNumber() != null) {
			TKNUM_type1 tknum = new TKNUM_type1();
			tknum.setTKNUM_type0(shipmentDocument.getShipmentNumber());
			shipmentHeader.setTKNUM(tknum);
		}
		if (shipmentDocument.getShipmentType() != null) {
			SHTYP_type1 shtyp = new SHTYP_type1();
			shtyp.setSHTYP_type0(shipmentDocument.getShipmentType());
			shipmentHeader.setSHTYP(shtyp);
		}
		if (shipmentDocument.getShippingConditions() != null) {
			VSBED_type1 vsbed = new VSBED_type1();
			vsbed.setVSBED_type0(shipmentDocument.getShippingConditions());
			shipmentHeader.setVSBED(vsbed);
		}
		if (shipmentDocument.getExternalIdentification1() != null) {
			EXTI1_type1 exti1 = new EXTI1_type1();
			exti1.setEXTI1_type0(shipmentDocument.getExternalIdentification1());
			shipmentHeader.setEXTI1(exti1);
		}
		if (shipmentDocument.getExternalIdentification2() != null) {
			EXTI2_type1 exti2 = new EXTI2_type1();
			exti2.setEXTI2_type0(shipmentDocument.getExternalIdentification2());
			shipmentHeader.setEXTI2(exti2);
		}
		if (shipmentDocument.getDistance() != null) {
			DISTZ_type1 distz = new DISTZ_type1();
			distz.setDISTZ_type0(shipmentDocument.getDistance());
			shipmentHeader.setDISTZ(distz);
		}
		if (shipmentDocument.getUnitOfMeasureForDistance() != null) {
			MEDST_type1 medst = new MEDST_type1();
			medst.setMEDST_type0(shipmentDocument.getUnitOfMeasureForDistance());
			shipmentHeader.setMEDST(medst);
		}
		if (shipmentDocument.getPlannedTotalTimeOfTransportationInDays() != null) {
			GESZTD_type1 gesztd = new GESZTD_type1();
			gesztd.setGESZTD_type0(shipmentDocument.getPlannedTotalTimeOfTransportationInDays());
			shipmentHeader.setGESZTD(gesztd);
		}
		if (shipmentDocument.getActualTotalTimeOfShipmentInDays() != null) {
			GESZTDA_type1 gesztda = new GESZTDA_type1();
			gesztda.setGESZTDA_type0(shipmentDocument.getActualTotalTimeOfShipmentInDays());
			shipmentHeader.setGESZTDA(gesztda);
		}
		if (shipmentDocument.getSpIndicator() != null) {
			SDABW_type1 spIndicator = new SDABW_type1();
			spIndicator.setSDABW_type0(shipmentDocument.getSpIndicator());
			shipmentHeader.setSDABW(spIndicator);
		}
		if (shipmentDocument.getWaitingTime() != null) {
			WARZTDA_type1 waitingTime = new WARZTDA_type1();
			waitingTime.setWARZTDA_type0(shipmentDocument.getWaitingTime());
			shipmentHeader.setWARZTDA(waitingTime);
		}
		if(shipmentDocument.getEtranReturnNumber() != null) {
			TPBEZ_type1 etranReturnNumber = new TPBEZ_type1();
			etranReturnNumber.setTPBEZ_type0(
					shipmentDocument.getEtranReturnNumber());
			shipmentHeader.setTPBEZ(etranReturnNumber);
		}

		Shipment_header_addInfo shipmentHeaderAddInfo = new Shipment_header_addInfo();
		if (shipmentDocument.getVersionNumberComponent() != null) {
			STATUS_SD_type1 statusSd = new STATUS_SD_type1();
			statusSd.setSTATUS_SD_type0(shipmentDocument.getVersionNumberComponent());
			shipmentHeaderAddInfo.setSTATUS_SD(statusSd);
		}
		if (shipmentDocument.getVehicle() != null) {
			RPMOWA_type1 rpmowa = new RPMOWA_type1();
			rpmowa.setRPMOWA_type0(shipmentDocument.getVehicle());
			shipmentHeaderAddInfo.setRPMOWA(rpmowa);
		}
		if (shipmentDocument.getTrailer() != null) {
			RPANHAE_type1 rpanhae = new RPANHAE_type1();
			rpanhae.setRPANHAE_type0(shipmentDocument.getTrailer());
			shipmentHeaderAddInfo.setRPANHAE(rpanhae);
		}
		shipmentHeader.addShipment_header_addInfo(shipmentHeaderAddInfo);

		List<Shipment_central_address> shipmentCentralAddressList = new ArrayList<Shipment_central_address>();
		List<ShipmentCentralAddressDTO> shipmentCentralAddressDTOList = shipmentDocument.getShipmentCentralAddressList();
		for (ShipmentCentralAddressDTO shipmentCentralAddressDTO : shipmentCentralAddressDTOList) {
			
			Shipment_central_address shipmentCentralAddress = new Shipment_central_address();
		
			if (shipmentCentralAddressDTO.getQualifierForPartnerFunction() != null) {
				PARTNER_Q_type1 partnerQ = new PARTNER_Q_type1();
				partnerQ.setPARTNER_Q_type0(shipmentCentralAddressDTO.getQualifierForPartnerFunction());
				shipmentCentralAddress.setPARTNER_Q(partnerQ);
			}
			if (shipmentCentralAddressDTO.getPartnerNoSap() != null) {
				PARTNER_ID_type1 partnerId = new PARTNER_ID_type1();
				partnerId.setPARTNER_ID_type0(shipmentCentralAddressDTO.getPartnerNoSap());
				shipmentCentralAddress.setPARTNER_ID(partnerId);
			}
			if (shipmentCentralAddressDTO.getLanguageKey() != null) {
				LANGUAGE_type1 language = new LANGUAGE_type1();
				language.setLANGUAGE_type0(shipmentCentralAddressDTO.getLanguageKey());
				shipmentCentralAddress.setLANGUAGE(language);
			}
			if (shipmentCentralAddressDTO.getNameOrAddressLine() != null) {
				NAME1_type1 name1 = new NAME1_type1();
				name1.setNAME1_type0(shipmentCentralAddressDTO.getNameOrAddressLine());
				shipmentCentralAddress.setNAME1(name1);
			}
			if (shipmentCentralAddressDTO.getHouseNumberAndStreet() != null) {
				STREET1_type1 street1 = new STREET1_type1();
				street1.setSTREET1_type0(shipmentCentralAddressDTO.getHouseNumberAndStreet());
				shipmentCentralAddress.setSTREET1(street1);
			}
			if (shipmentCentralAddressDTO.getPostalCode() != null) {
				POSTL_COD1_type1 postlCod1 = new POSTL_COD1_type1();
				postlCod1.setPOSTL_COD1_type0(shipmentCentralAddressDTO.getPostalCode());
				shipmentCentralAddress.setPOSTL_COD1(postlCod1);
			}
			if (shipmentCentralAddressDTO.getTownOrCity() != null) {
				CITY1_type1 city1 = new CITY1_type1();
				city1.setCITY1_type0(shipmentCentralAddressDTO.getTownOrCity());
				shipmentCentralAddress.setCITY1(city1);
			}
			if (shipmentCentralAddressDTO.getCountryIndicator() != null) {
				COUNTRY1_type1 country1 = new COUNTRY1_type1();
				country1.setCOUNTRY1_type0(shipmentCentralAddressDTO.getCountryIndicator());
				shipmentCentralAddress.setCOUNTRY1(country1);
			}
			if (shipmentCentralAddressDTO.getRegionState() != null) {
				REGION_type1 region = new REGION_type1();
				region.setREGION_type0(shipmentCentralAddressDTO.getRegionState());
				shipmentCentralAddress.setREGION(region);
			}
			
			shipmentCentralAddressList.add(shipmentCentralAddress);
		}
		shipmentHeader.setShipment_central_address(shipmentCentralAddressList.toArray(new Shipment_central_address[shipmentCentralAddressList.size()]));
		
		Shipment_date shipmentDate;
		boolean shouldSendShipmentDate;
		List<Shipment_date> shipmentDateList = new ArrayList<Shipment_date>();
		List<ShipmentDateDTO> shipmentDateDTOList = shipmentDocument.getShipmentDateList();
		for (ShipmentDateDTO shipmentDateDTO : shipmentDateDTOList) {
			shipmentDate = new Shipment_date();
			shouldSendShipmentDate = false;
			
			if (shipmentDateDTO.getIdocQualifierDateShipment() != null) {
				QUALF_type1 qualf = new QUALF_type1();
				qualf.setQUALF_type0(shipmentDateDTO.getIdocQualifierDateShipment());
				shipmentDate.setQUALF(qualf);
			}
			if (shipmentDateDTO.getConstraintForActivityStartBasic() != null) {
				NTANF_type1 ntanf = new NTANF_type1();
				ntanf.setNTANF_type0(shipmentDateDTO.getConstraintForActivityStartBasic());
				shipmentDate.setNTANF(ntanf);
				shouldSendShipmentDate = true;
			}
			if (shipmentDateDTO.getActualStartExecutionDate() != null) {
				ISDD_type1 isdd = new ISDD_type1();
				isdd.setISDD_type0(shipmentDateDTO.getActualStartExecutionDate());
				shipmentDate.setISDD(isdd);
				shouldSendShipmentDate = true;
			}
			if (shipmentDateDTO.getActualFinishExecutionDate() != null) {
				IEDD_type1 iedd = new IEDD_type1();
				iedd.setIEDD_type0(shipmentDateDTO.getActualFinishExecutionDate());
				shipmentDate.setIEDD(iedd);
				shouldSendShipmentDate = true;
			}
			
			if (shouldSendShipmentDate) {
				shipmentDateList.add(shipmentDate);
			}
		}
		
		shipmentHeader.setShipment_date(shipmentDateList.toArray(new Shipment_date[shipmentDateList.size()]));		
		
		List<ShipmentStageDTO> shipmentStageList = shipmentDocument.getShipmentStageList();
		Shipment_stage[] stages = new Shipment_stage[shipmentStageList.size()]; 
		for (int i = 0; i < shipmentStageList.size(); i++) {
			
			ShipmentStageDTO shipmentStageDTO = shipmentStageList.get(i);
			Shipment_stage stage = new Shipment_stage();
			
			// Distance
			if (shipmentStageDTO.getDistance() != null) {
				DISTZ_type3 distz = new DISTZ_type3();
				distz.setDISTZ_type2(shipmentStageDTO.getDistance());
				stage.setDISTZ(distz);
			}

			// Distance UOM
			if (shipmentStageDTO.getDistanceUom() != null) {
				MEDST_type3 medst = new MEDST_type3();
				medst.setMEDST_type2(shipmentStageDTO.getDistanceUom());
				stage.setMEDST(medst);
			}

			// Waiting time
			if (shipmentStageDTO.getShipmentWaitingTime() != null) {
				WARZTDA_type3 warztda = new WARZTDA_type3();
				warztda.setWARZTDA_type2(shipmentStageDTO.getShipmentWaitingTime());
				stage.setWARZTDA(warztda);
			}
			
			stages[i] = stage;
			
		}
		
		shipmentHeader.setShipment_stage(stages);
		
		List<Shipment_text_header> shipmentTextHeaderList = new ArrayList<Shipment_text_header>();
		List<ShipmentTextHeaderDTO> shipmentTextHeaderDTOList = shipmentDocument.getShipmentTextHeaderList();
		for (ShipmentTextHeaderDTO shipmentTextHeaderDTO : shipmentTextHeaderDTOList) {
			Shipment_text_header shipmentTextHeader = new Shipment_text_header();
			if (shipmentTextHeaderDTO.getTextID() != null) {
				TDID_type1 tdid = new TDID_type1();
				tdid.setTDID_type0(shipmentTextHeaderDTO.getTextID());
				shipmentTextHeader.setTDID(tdid);
			}
			if (shipmentTextHeaderDTO.getLanguageKey() != null) {
				LANGUA_ISO_type1 languaIso = new LANGUA_ISO_type1();
				languaIso.setLANGUA_ISO_type0(shipmentTextHeaderDTO.getLanguageKey());
				shipmentTextHeader.setLANGUA_ISO(languaIso);
			}
			
			List<Shipment_general_text> shipmentGeneralTextList = new ArrayList<Shipment_general_text>();
			List<ShipmentGeneralTextDTO> shipmentGeneralTextDTOList = shipmentTextHeaderDTO.getShipmentGeneralTextList();
			for (ShipmentGeneralTextDTO shipmentGeneralTextDTO : shipmentGeneralTextDTOList) {
				Shipment_general_text shipmentGeneralText = new Shipment_general_text();
				
				if (shipmentGeneralTextDTO.getTextLine() != null) {
					TDLINE_type1 tdline = new TDLINE_type1();
					tdline.setTDLINE_type0(shipmentGeneralTextDTO.getTextLine());
					shipmentGeneralText.setTDLINE(tdline);
				}
				
				shipmentGeneralTextList.add(shipmentGeneralText);
			}
			shipmentTextHeader.setShipment_general_text(shipmentGeneralTextList.toArray(new Shipment_general_text[shipmentGeneralTextList.size()]));
			
			shipmentTextHeaderList.add(shipmentTextHeader);
		}
		shipmentHeader.setShipment_text_header(shipmentTextHeaderList.toArray(new Shipment_text_header[shipmentTextHeaderList.size()]));

		Shipment_transport_additional_data shipmentTransportAdditionalData = new Shipment_transport_additional_data();
		if (shipmentDocument.getAdditionalText1() != null) {
			TEXT1_type1 text1 = new TEXT1_type1();
			text1.setTEXT1_type0(shipmentDocument.getAdditionalText1());
			shipmentTransportAdditionalData.setTEXT1(text1);
		}
		if (shipmentDocument.getAdditionalText2() != null) {
			TEXT2_type1 text2 = new TEXT2_type1();
			text2.setTEXT2_type0(shipmentDocument.getAdditionalText2());
			shipmentTransportAdditionalData.setTEXT2(text2);
		}
		String additionalText3 = shipmentDocument.getAdditionalText3();
		if (additionalText3 != null) {
			additionalText3 = StringUtils.removeLeadingZeros(additionalText3);
			TEXT3_type1 text3 = new TEXT3_type1();
			text3.setTEXT3_type0(additionalText3);
			shipmentTransportAdditionalData.setTEXT3(text3);
		}
		if (shipmentDocument.getAdditionalText4() != null) {
			TEXT4_type1 text4 = new TEXT4_type1();
			text4.setTEXT4_type0(shipmentDocument.getAdditionalText4());
			shipmentTransportAdditionalData.setTEXT4(text4);
		}
		
		if (shipmentDocument.getExtraCost1() != null) {
			ADD01_type1 add01 = new ADD01_type1();
			add01.setADD01_type0(shipmentDocument.getExtraCost1());
			shipmentTransportAdditionalData.setADD01(add01);
		}
		if (shipmentDocument.getExtraCost2() != null) {
			ADD02_type1 add02 = new ADD02_type1();
			add02.setADD02_type0(shipmentDocument.getExtraCost2());
			shipmentTransportAdditionalData.setADD02(add02);
		}
		if (shipmentDocument.getExtraCost3() != null) {
			ADD03_type1 add03 = new ADD03_type1();
			add03.setADD03_type0(shipmentDocument.getExtraCost3());
			shipmentTransportAdditionalData.setADD03(add03);
		}
		if (shipmentDocument.getExtraCost4() != null) {
			ADD03_type1 add04 = new ADD03_type1();
			add04.setADD03_type0(shipmentDocument.getExtraCost4());
			shipmentTransportAdditionalData.setADD03(add04);
		}
		
		shipmentHeader.setShipment_transport_additional_data(shipmentTransportAdditionalData);

		List<ShipmentDeliveryHeaderDTO> deliveryHeaderList = shipmentDocument.getDeliveryHeaderList();
		Shipment_delivery_header[] shipmentDeliveryHeader = new Shipment_delivery_header[deliveryHeaderList.size()];
		
		for (int i = 0; i < deliveryHeaderList.size(); i++) {
			
			ShipmentDeliveryHeaderDTO shipmentDeliveryHeaderDTO = deliveryHeaderList.get(i);
			
			shipmentDeliveryHeader[i] = new Shipment_delivery_header();
			if (shipmentDeliveryHeaderDTO.getSalesAndDistributionDocumentNumber() != null) {
				VBELN_type1 vbeln = new VBELN_type1();
				vbeln.setVBELN_type0(shipmentDeliveryHeaderDTO.getSalesAndDistributionDocumentNumber());
				shipmentDeliveryHeader[i].setVBELN(vbeln);
			}
			if (shipmentDeliveryHeaderDTO.getExternalIdentificationOfDeliveryNote() != null) {
				LIFEX_type1 lifex = new LIFEX_type1();
				lifex.setLIFEX_type0(shipmentDeliveryHeaderDTO.getExternalIdentificationOfDeliveryNote());
				shipmentDeliveryHeader[i].setLIFEX(lifex);
			}
			if (shipmentDeliveryHeaderDTO.getSalesOrganization() != null) {
				VKORG_type1 vkorg = new VKORG_type1();
				vkorg.setVKORG_type0(shipmentDeliveryHeaderDTO.getSalesOrganization());
				shipmentDeliveryHeader[i].setVKORG(vkorg);
			}
			
			List<Shipment_delivery_item> shipmentDeliveryItemList = new ArrayList<Shipment_delivery_item>();
			List<ShipmentDeliveryItemDTO> shipmentDeliveryItemDTOList = shipmentDeliveryHeaderDTO.getShipmentDeliveryItemList();
			
			for (ShipmentDeliveryItemDTO shipmentDeliveryItemDTO : shipmentDeliveryItemDTOList) {
				
				Shipment_delivery_item shipmentDeliveryItem = new Shipment_delivery_item();
				
				if (shipmentDeliveryItemDTO.getItemNumberOfTheSDDocument() != null) {
					POSNR_type1 posnr = new POSNR_type1();
					posnr.setPOSNR_type0(shipmentDeliveryItemDTO.getItemNumberOfTheSDDocument());
					shipmentDeliveryItem.setPOSNR(posnr);
				}
				if (shipmentDeliveryItemDTO.getMaterialNumber() != null) {
					MATNR_type1 matnr = new MATNR_type1();
					matnr.setMATNR_type0(shipmentDeliveryItemDTO.getMaterialNumber());
					shipmentDeliveryItem.setMATNR(matnr);
				}
				if (shipmentDeliveryItemDTO.getPlant() != null) {
					WERKS_type3 werks = new WERKS_type3();
					werks.setWERKS_type2(shipmentDeliveryItemDTO.getPlant());
					shipmentDeliveryItem.setWERKS(werks);
				}
				if (shipmentDeliveryItemDTO.getStorageLocation() != null) {
					LGORT_type1 lgort = new LGORT_type1();
					lgort.setLGORT_type0(shipmentDeliveryItemDTO.getStorageLocation());
					shipmentDeliveryItem.setLGORT(lgort);
				}
				if (shipmentDeliveryItemDTO.getSalesUom() != null) {
					VRKME_type1 vrkme = new VRKME_type1();
					vrkme.setVRKME_type0(shipmentDeliveryItemDTO.getSalesUom());
					shipmentDeliveryItem.setVRKME(vrkme);
				}
				if (shipmentDeliveryItemDTO.getWeightUnit() != null) {
					GEWEI_type1 gewei = new GEWEI_type1();
					gewei.setGEWEI_type0(shipmentDeliveryItemDTO.getWeightUnit());
					shipmentDeliveryItem.setGEWEI(gewei);
				}
				if (shipmentDeliveryItemDTO.getDeliveredQtyInSalesUom() != null) {
					ORMNG_type1 ormng = new ORMNG_type1();
					ormng.setORMNG_type0(shipmentDeliveryItemDTO.getDeliveredQtyInSalesUom());
					shipmentDeliveryItem.setORMNG(ormng);
				}
				if (shipmentDeliveryItemDTO.getDistributionChannel() != null) {
					VTWEG_type1 vtweg = new VTWEG_type1();
					vtweg.setVTWEG_type0(shipmentDeliveryItemDTO.getDistributionChannel());
					shipmentDeliveryItem.setVTWEG(vtweg);
				}
				if (shipmentDeliveryItemDTO.getDivision() != null) {
					SPART_type1 spart = new SPART_type1();
					spart.setSPART_type0(shipmentDeliveryItemDTO.getDivision());
					shipmentDeliveryItem.setSPART(spart);
				}
				
				shipmentDeliveryItemList.add(shipmentDeliveryItem);
			}
			
			shipmentDeliveryHeader[i].setShipment_delivery_item(shipmentDeliveryItemList.toArray(new Shipment_delivery_item[shipmentDeliveryItemList.size()]));
			
		}

		shipmentHeader.setShipment_delivery_header(shipmentDeliveryHeader);
		
		documentCreation.setShipment_header(shipmentHeader);
		shipmentDocumentCreation.setShipment_document_creation(documentCreation);

		return shipmentDocumentCreation;
		
	}
	
}