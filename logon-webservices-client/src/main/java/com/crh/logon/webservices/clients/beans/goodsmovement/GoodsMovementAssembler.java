/*
 * Author : AdNovum Informatik AG
 * Version Number : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.goodsmovement;

import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.BLDAT_type1;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.BUDAT_type1;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.BWART_type1;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.Control_data;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.ERFME_type1;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.ERFMG_type1;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.GoodsMovements;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.Goods_movement_external_system_item;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.Goods_movements;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.Goods_movements_mobile_data_entry_header;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.LGORT_type1;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.MATNR_type1;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.SNDPRN_type1;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.TCODE_type1;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.UMLGO_type1;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.UMWRK_type1;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.WERKS_type1;
import goodsmovements.mm.net.crh.ea.XI_90E6B0A6Da7B3Afbb45604298Fccd288_ServiceStub.XBLNR_LONG_type1;

import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.webservices.clients.util.WebserviceClientUtil;

import deliverydocument.sales.net.crh.ea.XI_c2Dbac59De9D3A1C92653157E4A7Ab5B_ServiceStub.Document_item_general_data;

/**
 * @author AdNovum Informatik AG
 */
public class GoodsMovementAssembler {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(GoodsMovementAssembler.class);

	public static String getXml(GoodsMovementDTO goodsMovementDto) {

		GoodsMovements goodsMovement = createGoodsMovement(goodsMovementDto);

		return WebserviceClientUtil.getSerializedString(goodsMovement, "GoodsMovements");
	}

	/**
	 * GoodsMovement > Goods_movements >
	 * Goods_movements_mobile_data_entry_header >
	 * Goods_movements_external_system_item
	 * 
	 * @param goodsMovementDto
	 *            the given DTO for the available logon fields for the service
	 * @return GoodsMovements object corresponding to the WSLD
	 */
	public static GoodsMovements createGoodsMovement(GoodsMovementDTO goodsMovementDto) {
		// [[
		GoodsMovements result = null;
		Object[][] params = { { "goodsMovementDto", goodsMovementDto } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(GoodsMovementAssembler.class, "createGoodsMovement", params);
		}
		// ]]
		try {

			GoodsMovements goodsMovements = new GoodsMovements();
			Goods_movements goodsMovementsElement = new Goods_movements();

			Document_item_general_data test = new Document_item_general_data();

			Goods_movements_mobile_data_entry_header goodMovementsHeader = new Goods_movements_mobile_data_entry_header();

			if (goodsMovementDto.getDocumentDateInDocument() != null) {
				BLDAT_type1 bldat = new BLDAT_type1();
				bldat.setBLDAT_type0(goodsMovementDto.getDocumentDateInDocument());
				goodMovementsHeader.setBLDAT(bldat);
			}

			if (goodsMovementDto.getPostingDateInDocument() != null) {
				BUDAT_type1 budat = new BUDAT_type1();
				budat.setBUDAT_type0(goodsMovementDto.getPostingDateInDocument());
				goodMovementsHeader.setBUDAT(budat);
			}

			if (goodsMovementDto.getCurrentTransactionCode() != null) {
				TCODE_type1 tcode = new TCODE_type1();
				tcode.setTCODE_type0(goodsMovementDto.getCurrentTransactionCode());
				goodMovementsHeader.setTCODE(tcode);
			}

			if (goodsMovementDto.getReferenceDocumentNumber() != null) {
				XBLNR_LONG_type1 xblnr_long = new XBLNR_LONG_type1();
				xblnr_long.setXBLNR_LONG_type0(goodsMovementDto.getReferenceDocumentNumber());
				goodMovementsHeader.setXBLNR_LONG(xblnr_long);
			}

			List<GoodsMovementItemDTO> goodsMovementsItemDTOList = goodsMovementDto.getGoodsMovementItemList();
			for (GoodsMovementItemDTO goodsMovementsItemDTO : goodsMovementsItemDTOList) {
				Goods_movement_external_system_item GoodsMovementsItem = new Goods_movement_external_system_item();

				if (goodsMovementsItemDTO.getMaterialNumber() != null) {
					MATNR_type1 matnr = new MATNR_type1();
					matnr.setMATNR_type0(goodsMovementsItemDTO.getMaterialNumber());
					GoodsMovementsItem.setMATNR(matnr);
				}

				if (goodsMovementsItemDTO.getPlant() != null) {
					WERKS_type1 werks = new WERKS_type1();
					werks.setWERKS_type0(goodsMovementsItemDTO.getPlant());
					GoodsMovementsItem.setWERKS(werks);
				}

				if (goodsMovementsItemDTO.getStorageLocation() != null) {
					LGORT_type1 lgort = new LGORT_type1();
					lgort.setLGORT_type0(goodsMovementsItemDTO.getStorageLocation());
					GoodsMovementsItem.setLGORT(lgort);
				}

				if (goodsMovementsItemDTO.getMovementType() != null) {
					BWART_type1 bwart = new BWART_type1();
					bwart.setBWART_type0(goodsMovementsItemDTO.getMovementType());
					GoodsMovementsItem.setBWART(bwart);
				}

				if (goodsMovementsItemDTO.getQuantityInUnitOfEntry() != null) {
					ERFMG_type1 erfmg = new ERFMG_type1();
					erfmg.setERFMG_type0(goodsMovementsItemDTO.getQuantityInUnitOfEntry());
					GoodsMovementsItem.setERFMG(erfmg);
				}

				if (goodsMovementsItemDTO.getUnitOfEntry() != null) {
					ERFME_type1 erfme = new ERFME_type1();
					erfme.setERFME_type0(goodsMovementsItemDTO.getUnitOfEntry());
					GoodsMovementsItem.setERFME(erfme);
				}

				if (goodsMovementsItemDTO.getIssuingPlant() != null) {
					UMWRK_type1 umwrk = new UMWRK_type1();
					umwrk.setUMWRK_type0(goodsMovementsItemDTO.getIssuingPlant());
					GoodsMovementsItem.setUMWRK(umwrk);
				}

				if (goodsMovementsItemDTO.getIssuingStorageLocation() != null) {
					UMLGO_type1 umlgo = new UMLGO_type1();
					umlgo.setUMLGO_type0(goodsMovementsItemDTO.getIssuingStorageLocation());
					GoodsMovementsItem.setUMLGO(umlgo);
				}

				goodMovementsHeader.addGoods_movement_external_system_item(GoodsMovementsItem);
			}

			if (goodsMovementDto.getPartnerNumberOfSender() != null) {
				Control_data controlData = new Control_data();
				SNDPRN_type1 sndPrn = new SNDPRN_type1();
				sndPrn.setSNDPRN_type0(goodsMovementDto.getPartnerNumberOfSender());
				controlData.setSNDPRN(sndPrn);
				goodsMovementsElement.setControl_data(controlData);
			}

			goodsMovementsElement.setGoods_movements_mobile_data_entry_header(goodMovementsHeader);
			goodsMovements.setGoods_movements(goodsMovementsElement);

			return result = goodsMovements;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

}
