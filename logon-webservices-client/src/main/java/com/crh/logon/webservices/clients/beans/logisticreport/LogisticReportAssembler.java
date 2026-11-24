/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.logisticreport;


import java.math.BigInteger;
import java.util.Calendar;

import com.crh.logon.webservices.clients.beans.saplogisticreport.SapLogisticReportDTO;
import com.crh.logon.webservices.clients.beans.saplogisticreport.SapLogisticReportItemDTO;
import com.crh.logon.webservices.clients.util.WebserviceClientUtil;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LCON_1_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LCRE_1_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LDOC_2_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LDRV_1_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LDRV_2_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LLPT_1_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LLPT_2_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LPLT_1_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LPOOL_1_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LSHCO_1_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LSHCO_2_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LSHI_3_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LSPCO_1_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LSPCO_2_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LTYP_4_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.LWAIT_1_type1;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.Logistics_Item_Information;
import logisticsreporting.qm.net.holcim.ea.DX1_Java_Instance_LogisticsReporting_OutStub.MT_LogisticsReporting_Out;

public class LogisticReportAssembler {

	public static DX1_Java_Instance_LogisticsReporting_OutStub.MT_LogisticsReporting_Out createLogisticReportClientDocument(
			SapLogisticReportDTO dto) {

		DX1_Java_Instance_LogisticsReporting_OutStub.MT_LogisticsReporting_Out reportMT = new DX1_Java_Instance_LogisticsReporting_OutStub.MT_LogisticsReporting_Out();
		DX1_Java_Instance_LogisticsReporting_OutStub.DT_LogisticsReporting_out reportDT = new DX1_Java_Instance_LogisticsReporting_OutStub.DT_LogisticsReporting_out();
		DX1_Java_Instance_LogisticsReporting_OutStub.Logistics_Data logisticData = new DX1_Java_Instance_LogisticsReporting_OutStub.Logistics_Data();
		DX1_Java_Instance_LogisticsReporting_OutStub.Logistics_Header_Information logisticHeader = new DX1_Java_Instance_LogisticsReporting_OutStub.Logistics_Header_Information();
		
		//SimpleDateFormat logisticDateFormat = new SimpleDateFormat("yyyy-MM-DDThh:mm:ss");
		
		//Header KPIs

		//Complex KPI

		//0,LDOC_1
		if (dto.getOrderNumber() != null) {
			BigInteger orderNumber = dto.getOrderNumber();
			logisticHeader.setLDOC_1(orderNumber);
		}
		
		//0.1,LDOC_2
		if (dto.getLifexNumber() != null) {
			LDOC_2_type1 lifexWrapper = new LDOC_2_type1();
			lifexWrapper.setLDOC_2_type0(dto.getLifexNumber());
			logisticHeader.setLDOC_2(lifexWrapper );
		}
		
		//1,LFLAG_7
		if(dto.getOntimeDeliveryInd() != null){
			BigInteger onTimeFlag= BigInteger.valueOf(dto.getOntimeDeliveryInd() ? 1 : 0);
			logisticHeader.setLFLAG_7(onTimeFlag);
		}

		//2,LORT_2
		if (dto.getOrderCreationTypeId() != null) {
			BigInteger creationTypeId = dto.getOrderCreationTypeId();
			logisticHeader.setLORT_2(creationTypeId);
		}
		
		//3,LCYTM_1 
		if (dto.getCycleTimeAtShippingPoint() != null) {
			BigInteger cycleTimeAtSP = BigInteger.valueOf(dto.getCycleTimeAtShippingPoint());
			logisticHeader.setLCYTM_1(cycleTimeAtSP);
		}
		
		//4,LDIST_1
		logisticHeader.setLDIST_1(dto.getTransportDistance());
		
		//5,LFLAG_9
		BigInteger pdaIndicator = BigInteger.valueOf(dto.getPdaOrRailInd());
		logisticHeader.setLFLAG_9(pdaIndicator);
		
		

		//9,LFLAG_5
		BigInteger shortTermInd = BigInteger.valueOf(dto.getShorttermOrderInd());
		logisticHeader.setLFLAG_5(shortTermInd);
		
		//10,LDUR_1
		if (dto.getCustomerDeliveryTimeWindow() != null) {
			logisticHeader.setLDUR_1(dto.getCustomerDeliveryTimeWindow());
		}
		
		//11,LDUR_3
		if (dto.getCycleTimeSTBiz() != null) {
			logisticHeader.setLDUR_3(dto.getCycleTimeSTBiz());
		}

		//12,LDUR_4
		logisticHeader.setLDUR_4(dto.getLoadingTimeMinBiz());

		//13,LDUR_5
		logisticHeader.setLDUR_5(dto.getUnloadingTimeBiz());
		
		//14,LFLAG_1
		logisticHeader.setLFLAG_1(dto.getDeviationInDeliveryType());
		
		//20, LDUR_2
		logisticHeader.setLDUR_2(dto.getDrivingTime());

		//Simple KPIs
		
		//26,LSPCO_1
		if (dto.getShippingPointCoordinatesLat() != null) {
			LSPCO_1_type1 shippingCoordsLAT = new LSPCO_1_type1();
			shippingCoordsLAT.setLSPCO_1_type0(dto.getShippingPointCoordinatesLat());
			logisticHeader.setLSPCO_1(shippingCoordsLAT);
		}
		
		//27,LSPCO_2
		if (dto.getShippingPointCoordinatesLon() != null) {
			LSPCO_2_type1 shippingCoordsLON = new LSPCO_2_type1();
			shippingCoordsLON.setLSPCO_2_type0(dto.getShippingPointCoordinatesLon());
			logisticHeader.setLSPCO_2(shippingCoordsLON);
		}
		
		//28,LSHCO_1
		if (dto.getShiptoCoordinatesLat() != null) {
			LSHCO_1_type1 shiptoCoordsLat = new LSHCO_1_type1();
			shiptoCoordsLat.setLSHCO_1_type0(dto.getShiptoCoordinatesLat());
			logisticHeader.setLSHCO_1(shiptoCoordsLat);
		}
		
		//29,LSHCO_2
		if (dto.getShiptoCoordinatesLat() != null) {
			LSHCO_2_type1 shiptoCoordsLon = new LSHCO_2_type1();
			shiptoCoordsLon.setLSHCO_2_type0(dto.getShiptoCoordinatesLon());
			logisticHeader.setLSHCO_2(shiptoCoordsLon);
		}
		
		//41,LWAIT_1
		// TODO Sep 24, 2014/hosszub: Perhaps the other KPIs should be refactored like this too, reducing from 2 to 1 the data access from the dto. Less possibility for mistakes, perhaps faster too.
		String waitingTimeReasonShipto = dto.getWaitingTimeReasonShipto();
		if (waitingTimeReasonShipto != null) {
			LWAIT_1_type1 waitingReason = new LWAIT_1_type1();
			waitingReason.setLWAIT_1_type0(waitingTimeReasonShipto);
			logisticHeader.setLWAIT_1(waitingReason);			
		}
		
		//44,LPOOL_1
		if (dto.getPoolId() != null) {
			LPOOL_1_type1 poolId = new LPOOL_1_type1();
			poolId.setLPOOL_1_type0(dto.getPoolId());
			logisticHeader.setLPOOL_1(poolId );
		}
		
		//45,LTYP_2
		if (dto.getShippingPointTypeId() != null) {
			BigInteger spTypeId = dto.getShippingPointTypeId();
			logisticHeader.setLTYP_2(spTypeId);
		}
		
		//46,LTYP_3
		if (dto.getDeliveryTypeId() != null) {
			BigInteger deliveryTypeId = dto.getDeliveryTypeId();
			logisticHeader.setLTYP_3(deliveryTypeId );
		}
		
		//47.0,LDRV_2
		if (dto.getTruckDriverName() != null) {
			LDRV_2_type1 trckDriverName = new LDRV_2_type1();
			trckDriverName.setLDRV_2_type0(dto.getTruckDriverName());
			logisticHeader.setLDRV_2(trckDriverName);
		}

		//47.1,LDRV_1
		if (dto.getTruckDriverID() != null) {
			LDRV_1_type1 truckDriverId = new LDRV_1_type1();
			truckDriverId.setLDRV_1_type0(dto.getTruckDriverID());
			logisticHeader.setLDRV_1(truckDriverId);
		}

		//48,LCRE_1
		if (dto.getDeliveryCreatedBy() != null) {
			LCRE_1_type1 deliveryCreatedBy = new LCRE_1_type1();
			deliveryCreatedBy.setLCRE_1_type0(dto.getDeliveryCreatedBy());
			logisticHeader.setLCRE_1(deliveryCreatedBy);
		}

		//49,LCRE_2, not null
		if (dto.getDeliveryCreatedOn() != null) {
			Calendar deliveryCreatedOn = dto.getDeliveryCreatedOn();
			logisticHeader.setLCRE_2(deliveryCreatedOn);
		}
		
		//50,LTYP_1
		if (dto.getShiptoTypeId() != null) {
			BigInteger stTypeId = dto.getShiptoTypeId();
			logisticHeader.setLTYP_1(stTypeId);
		}

		//60,LTYP_4
		if (dto.getWagonTypeId() != null) {
			LTYP_4_type1 wagonTypeId = new LTYP_4_type1();
			wagonTypeId.setLTYP_4_type0(dto.getWagonTypeId());
			logisticHeader.setLTYP_4(wagonTypeId);

		}

		//62,LSHI_1
		if (dto.getShippingPointRailwayStationCountryCd() != null) {
			BigInteger spCountryCd = dto.getShippingPointRailwayStationCountryCd();
			logisticHeader.setLSHI_1(spCountryCd);
		}

		//63,LSHI_2
		if (dto.getShippingPointRailwayStationNumber() != null) {
			BigInteger spStationNumber = dto.getShippingPointRailwayStationNumber();
			logisticHeader.setLSHI_2(spStationNumber);
		}

		//64,LSHI_3
		if (dto.getShippingPointRailwayStationName() != null) {
			LSHI_3_type1 spStationName = new LSHI_3_type1();
			spStationName.setLSHI_3_type0(dto.getShippingPointRailwayStationName());
			logisticHeader.setLSHI_3(spStationName);
		}

		//65,LG_1,not null
		if (dto.getDeliveryFromDate() != null) {
			Calendar enterSp = dto.getDeliveryFromDate();
			logisticHeader.setLG_1(enterSp);
		}
		
		//66,LG_2,not null
		if (dto.getDeliveryToDate() != null) {
			Calendar startLoad = dto.getDeliveryToDate();
			logisticHeader.setLG_2(startLoad);
		}
		
		//67,LG_3
		if (dto.getEnterShippingPointBiz() != null) {
			Calendar enterShippingPointBiz = dto.getEnterShippingPointBiz();
			logisticHeader.setLG_3(enterShippingPointBiz);
		}

		//68,LG_4
		if (dto.getStartLoadingBiz() != null) {
			Calendar startLoadingBiz = dto.getStartLoadingBiz();
			logisticHeader.setLG_4(startLoadingBiz);
		}

		//69,LG_5
		if (dto.getEndLoadingBiz() != null) {
			Calendar endLoadingBiz = dto.getEndLoadingBiz();
			logisticHeader.setLG_5(endLoadingBiz);
		}

		//70,LG_6
		if (dto.getLeaveShippingPointBiz() != null) {
			Calendar leaveShippingPointBiz = dto.getLeaveShippingPointBiz();
			logisticHeader.setLG_6(leaveShippingPointBiz);
		}

		//71,LG_7
		if (dto.getTransmissionToRailwayCompanyBiz() != null) {
			Calendar transmissionToRailwayCompanyBiz = dto.getTransmissionToRailwayCompanyBiz();
			logisticHeader.setLG_7(transmissionToRailwayCompanyBiz);
		}

		//72,LG_8
		if (dto.getEnterShiptoBiz() != null) {
			Calendar enterShiptoBiz = dto.getEnterShiptoBiz();
			logisticHeader.setLG_8(enterShiptoBiz);
		}

		//73,LG_9
		if (dto.getStartUnloadBiz() != null) {
			Calendar startUnloadBizString = dto.getStartUnloadBiz();
			logisticHeader.setLG_9(startUnloadBizString);
		}

		//74,LG_10
		if (dto.getEndUnloadBiz() != null) {
			Calendar endUnloadBiz = dto.getEndUnloadBiz();
			logisticHeader.setLG_10(endUnloadBiz);
		}

		//75,LG_11
		if (dto.getLeaveShiptoBiz() != null) {
			Calendar leaveShipto = dto.getLeaveShiptoBiz();
			logisticHeader.setLG_11(leaveShipto);
		}

		final int itemCount = dto.getReportItemList().size();
		Logistics_Item_Information[] logisticItems = new Logistics_Item_Information[itemCount];

		//Item KPIs

		for (int i = 0; i < itemCount; i++) {
			
			SapLogisticReportItemDTO itemDto = dto.getReportItemList().get(i);
			DX1_Java_Instance_LogisticsReporting_OutStub.Logistics_Item_Information logisticItem = new DX1_Java_Instance_LogisticsReporting_OutStub.Logistics_Item_Information();
			
			//Complex
			
			//6,LFLA_10
			//if (itemDto.getLoadingWithLdsInd() != null) {
			logisticItem.setLFLA_10(BigInteger.valueOf(itemDto.getLoadingWithLdsInd()));
			
			//7, LFLAG_2
			logisticItem.setLFLAG_2(BigInteger.valueOf(itemDto.getMaintainedDispocardIndicator()));
			
			//8,LFLAG_4
			//DTO should contain 1 instead of null based on specs.
			if (itemDto.getSiloVerificationTypeId() != null) {
				BigInteger siloVerifType = itemDto.getSiloVerificationTypeId();
				logisticItem.setLFLAG_4(siloVerifType);
			}
			
			//15,LFLAG_3
			logisticItem.setLFLAG_3(itemDto.getDeviationInOrderedQty());
			
			
			//Simple

			//25,LPLT_1, not null
			if (itemDto.getProductionPlantId() != null) {
				LPLT_1_type1 prodPlantId = new LPLT_1_type1();
				prodPlantId.setLPLT_1_type0(itemDto.getProductionPlantId());
				logisticItem.setLPLT_1(prodPlantId);
			}

			//42.0,LLPT_1
			if (itemDto.getLoadingPointId() != null) {
				LLPT_1_type1 loadingPointId = new LLPT_1_type1();
				loadingPointId.setLLPT_1_type0(itemDto.getLoadingPointId());
				logisticItem.setLLPT_1(loadingPointId);
			}

			//42.1,LLPT_2
			if (itemDto.getLoadingPointName() != null) {
				LLPT_2_type1 loadingPointName = new LLPT_2_type1();
				loadingPointName.setLLPT_2_type0(itemDto.getLoadingPointName());
				logisticItem.setLLPT_2(loadingPointName);
			}

			//61,LCON_1
			if (itemDto.getContainerNumber() != null) {
				LCON_1_type1 containerNumber = new LCON_1_type1();
				containerNumber.setLCON_1_type0(itemDto.getContainerNumber());
				logisticItem.setLCON_1(containerNumber);
			}

			//76,LITM_1,not null
			if (itemDto.getOrderItemNumber() != null) {
				BigInteger OrderItemNumber = itemDto.getOrderItemNumber();
				logisticItem.setLITM_1(OrderItemNumber);
			}

			logisticItems[i] = logisticItem;

		}

		logisticHeader.setItem(logisticItems);

		logisticData.setHeader(logisticHeader);
		reportDT.setDATA(logisticData);
		reportMT.setMT_LogisticsReporting_Out(reportDT);
		return reportMT;
	}

	public static String getXml(SapLogisticReportDTO sapLogisticReportDTO) {
		MT_LogisticsReporting_Out createLogisticReportClientDocument = createLogisticReportClientDocument(sapLogisticReportDTO);
		return WebserviceClientUtil.getSerializedString(createLogisticReportClientDocument, "MT_LogisticsReporting_Out");
	}

}
