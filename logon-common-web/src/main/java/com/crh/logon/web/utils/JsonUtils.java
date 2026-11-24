/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;

import flexjson.JSONSerializer;

/**
 * Common utility class for JSON data generation.
 * 
 * @author AdNovum Informatik AG
 */
public class JsonUtils {

	public static JSONSerializer getJsonSerializer() {
		return new JSONSerializer()
			.exclude(
					"*.class",
					"*.ctlActCd",
					"*.ctlCreTs",
					"*.ctlCreUid",
					"*.ctlModTs",
					"*.ctlModUid");
	}
	
	/**
	 * Returns the JSON serializer for a {@link DeliverySettingDTO} object. This object
	 * contains a lot of lists inside the {@link DeliveryOptionConfigurationDTO} object.
	 * @return
	 */
	public static JSONSerializer getDeliverySettingJSONSerializer() {
		final JSONSerializer jsonSerializer = JsonUtils.getJsonSerializer();
		
		jsonSerializer.include("vehicleTypeCds");
		
		jsonSerializer.exclude(DeliverySettingDTO.excludeEntities);

		jsonSerializer.include("deliveryOptionConfiguration.prodPlantList.plantCd");
		jsonSerializer.include("deliveryOptionConfiguration.prodPlantList.plantName");
		jsonSerializer.include("deliveryOptionConfiguration.prodPlantList.productionPlant");
		jsonSerializer.include("deliveryOptionConfiguration.materialMixDesignList.mixDesignId");
		jsonSerializer.include("deliveryOptionConfiguration.materialMixDesignList.aggRmxCode");
		jsonSerializer.include("deliveryOptionConfiguration.materialMixDesignList.mixDesignCd");
		jsonSerializer.include("deliveryOptionConfiguration.deliveryTypeList.deliveryTypeTxPK.deliveryTypeBs.deliveryTypeCd");
		jsonSerializer.include("deliveryOptionConfiguration.deliveryTypeList.txt");
		jsonSerializer.include("deliveryOptionConfiguration.siloList.siloName");
		jsonSerializer.include("deliveryOptionConfiguration.siloList.shiptoSiloId");
		jsonSerializer.include("deliveryOptionConfiguration.uomList.uomTxPK.uomBs.uomCd");
		jsonSerializer.include("deliveryOptionConfiguration.uomList.txt");
		jsonSerializer.include("deliveryOptionConfiguration.supplyPlantList.plantCd");
		jsonSerializer.include("deliveryOptionConfiguration.supplyPlantList.plantName");
		jsonSerializer.include("deliveryOptionConfiguration.supplyPlantList.productionPlant");
		jsonSerializer.include("deliveryOptionConfiguration.shippingpoinList.shippingpointId");
		jsonSerializer.include("deliveryOptionConfiguration.shippingpoinList.shippingpointName");
		jsonSerializer.include("deliveryOptionConfiguration.shippingpoinList.coordLat");
		jsonSerializer.include("deliveryOptionConfiguration.shippingpoinList.coordLon");
		jsonSerializer.include("deliveryOptionConfiguration.shippingpoinList.plantCd");
		jsonSerializer.include("deliveryOptionConfiguration.shippingpoinList.railwayStationId");
		jsonSerializer.include("deliveryOptionConfiguration.loadingpointList.loadingpointId");
		jsonSerializer.include("deliveryOptionConfiguration.loadingpointList.loadingpointName");
		jsonSerializer.include("deliveryOptionConfiguration.poolList.poolTxPK.poolId");
		jsonSerializer.include("deliveryOptionConfiguration.poolList.name");
		jsonSerializer.include("deliveryOptionConfiguration.commercialCarrierList.carrierId");
		jsonSerializer.include("deliveryOptionConfiguration.commercialCarrierList.displayName");
		jsonSerializer.include("deliveryOptionConfiguration.commercialCarrierList.carrierRequiresPlanningFlag");//probably not needed, but it's outside on the jsp?
		jsonSerializer.include("deliveryOptionConfiguration.executingCarrierList.carrierId");
		jsonSerializer.include("deliveryOptionConfiguration.executingCarrierList.displayName");
		jsonSerializer.include("deliveryOptionConfiguration.billtoList.billtoSapBp.name1");
		jsonSerializer.include("deliveryOptionConfiguration.billtoList.billtoSapBp.name2");
		jsonSerializer.include("deliveryOptionConfiguration.billtoList.billtoSapBp.sapBpId");
		jsonSerializer.include("deliveryOptionConfiguration.billtoList.billtoSapBp.sapId");
		jsonSerializer.include("deliveryOptionConfiguration.payerList.name1");
		jsonSerializer.include("deliveryOptionConfiguration.payerList.name2");
		jsonSerializer.include("deliveryOptionConfiguration.payerList.sapBpId");
		jsonSerializer.include("deliveryOptionConfiguration.payerList.sapId");
		jsonSerializer.include("deliveryOptionConfiguration.vehicleTypeWrapperList");
		jsonSerializer.include("deliveryOptionConfiguration.deliveryFreqEveryTimeUomMap");
		jsonSerializer.include("deliveryOptionConfiguration.deliveryTimeToleranceUomMap");
		jsonSerializer.include("deliveryOptionConfiguration.serviceProcessIndicatorList");

		jsonSerializer.exclude("deliveryOptionConfiguration.commercialCarrierList.*");
		jsonSerializer.exclude("deliveryOptionConfiguration.executingCarrierList.*");
		jsonSerializer.exclude("deliveryOptionConfiguration.billtoList.*");
		jsonSerializer.exclude("deliveryOptionConfiguration.prodPlantList.*");
		jsonSerializer.exclude("deliveryOptionConfiguration.materialMixDesignList.*");
		jsonSerializer.exclude("deliveryOptionConfiguration.deliveryTypeList.*");
		jsonSerializer.exclude("deliveryOptionConfiguration.payerList.*");
		jsonSerializer.exclude("deliveryOptionConfiguration.siloList.*");
		jsonSerializer.exclude("deliveryOptionConfiguration.uomList.*");
		jsonSerializer.exclude("deliveryOptionConfiguration.supplyPlantList.*");			
		jsonSerializer.exclude("deliveryOptionConfiguration.shippingpoinList.*");
		jsonSerializer.exclude("deliveryOptionConfiguration.loadingpointList.*");
		jsonSerializer.exclude("deliveryOptionConfiguration.poolList.*");

		return jsonSerializer;
	}

}
