
package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.AdnTrace;
import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.ordercreation.DeliverySettingFilterLogic;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.dto.DeliverySetting;
import com.crh.logon.business.ordercreation.dto.OrderItemDeliverySetting;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.stores.sap.ShiptoSareaStoreInterface;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.UpdatePlanningMaterialRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.UpdatePlanningMaterialResponse;
import org.apache.commons.lang.StringUtils;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class UpdatePlanningMaterialRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final String FIELD_LOGON_ORDER_NO = "logonOrderNo";

	private static final String FIELD_PROD_PLANT_NO = "prodPlantNo";

	private static final String FIELD_MATERIAL_NO = "materialNo";

	private static final String FIELD_CONTRACT_ITEM_NUMBER = "contractItemNo";

	private static final String FIELD_UOM = "uom";

	private static final String FIELD_QTY = "qty";

	@EJB
	private ShiptoSareaStoreInterface shiptoSareaStore;

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final UpdatePlanningMaterialRequest request = (UpdatePlanningMaterialRequest) eventBean;

		getOrder(request.getLogonOrderNo(), FIELD_LOGON_ORDER_NO);
		getPlant(request.getProdPlantNo(), FIELD_PROD_PLANT_NO);
		getMaterial(request.getMaterialNo(), FIELD_MATERIAL_NO);
		parse(request.getContractItemNumber(), FIELD_CONTRACT_ITEM_NUMBER, false);
		if (!StringUtils.isEmpty(request.getUom())) {
			getUom(request.getUom(), FIELD_UOM);
		}
	}


	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {

		final UpdatePlanningMaterialRequest request = (UpdatePlanningMaterialRequest) eventBean;
		final UpdatePlanningMaterialResponse response = createLogonOKEventResponse(UpdatePlanningMaterialResponse.class);

		OrderEntity order = getOrder(request.getLogonOrderNo(), FIELD_LOGON_ORDER_NO);

		OrderItemEntity planningMaterialOrderItem = findPlanningMaterialOrderItem(order);
		boolean isMaterialEditable = LogonUtil.isStillReplacablePlanningMaterial(order,
				planningMaterialOrderItem.getSapMaterial());
		if (!isMaterialEditable) {
			throw newApplicationError("order is in a state where there's no possibility to replace the material");
		}

		SapContractItemEntity replacingContractItem = findReplacingContractItem(request.getContractItemNumber(),
				request.getMaterialNo(), planningMaterialOrderItem.getSapContractItem());
		PlantBsEntity productionPlant = findProductionPlant(request.getProdPlantNo(), replacingContractItem);

		//replace the material and contract item in the order
		order = materialEnhancementsService.replaceMaterialInItem(order, planningMaterialOrderItem.getOrderItemId(),
				replacingContractItem.getSapContractItemId());

		//uom check only after replacing the material in the orderItem, as it depends on the new material
		UomBsEntity replacingUom = findUom(request.getUom(), order, planningMaterialOrderItem);

		//replace the remaining attributes in the order
		order = materialEnhancementsService.updateMaterialAttributes(order, planningMaterialOrderItem.getOrderItemId(),
				productionPlant, replacingUom, request.getQty());

		//running algorithms with the new order attributes
		orderDetailService.updateOrder(order, false, false, null, true, null);

		return response;
	}

	//find the exactly 1 planning material orderItem
	private OrderItemEntity findPlanningMaterialOrderItem(OrderEntity order) {
		List<OrderItemEntity> orderItems = order.getOrderItems();
		OrderItemEntity planningMaterialOrderItem = LogonUtil.findPlanningMaterialOrderItem(orderItems);
		if (planningMaterialOrderItem == null) {
			throw newApplicationError("no planning material orderItem was found in the order");
		}
		return planningMaterialOrderItem;
	}

	//check for a matching contract item + material, which can replace the planning material (also checks for agg/landfill)
	private SapContractItemEntity findReplacingContractItem(String reqContractItemNumber, String reqMaterialNo,
			SapContractItemEntity planningMaterialContractItem) {
		Long contractItemNumber = parse(reqContractItemNumber, FIELD_CONTRACT_ITEM_NUMBER, false);

		SapContractItemEntity replacingContractItem = materialEnhancementsService.findMatchingContractItem(contractItemNumber,
				reqMaterialNo, planningMaterialContractItem);

		if (replacingContractItem == null) {
			throw newApplicationError("no such contractItemNumber and materialNo, which could replace the material");
		}

		SapContractEntity contract = replacingContractItem.getSapContract();
		boolean quantityControlledContract = LogonUtil.isQuantityControlledContract(contract);
		if (quantityControlledContract && !replacingContractItem.getIsApGroupLeader()) {
			replacingContractItem = replacingContractItem.getApGroupLeader();
		}

		return replacingContractItem;
	}

	//find production plant entity, if the material given in the contract item is available there
	private PlantBsEntity findProductionPlant(String prodPlantNo, SapContractItemEntity replacingContractItem) {
		SapMaterialEntity replacingMaterial = replacingContractItem.getSapMaterial();
		List<PlantBsEntity> prodPlantList =
				orderDetailService.loadSapMaterialPlantList(replacingMaterial.getSapMaterialId(), true);
		PlantBsEntity productionPlant = getPlant(prodPlantNo, FIELD_PROD_PLANT_NO);
		for (PlantBsEntity validPlantBs : prodPlantList) {
			if (validPlantBs.getPlantCd().equals(productionPlant.getPlantCd())) {
				return productionPlant;
			}
		}
		throw newApplicationError("prodPlantNo is not a possible production plant");
	}

	//uom check for the uom, already after replacing the material in the orderItem
	private UomBsEntity findUom(String reqUom, OrderEntity order, OrderItemEntity planningMaterialOrderItem) {

		UomBsEntity replacingUom = null;

		if (!StringUtils.isEmpty(reqUom)) {

			final UomBsEntity uom = getUom(reqUom, FIELD_UOM);

			List<UomBsEntity> validUomBsEntityList = findValidUomList(order, planningMaterialOrderItem);
			for (UomBsEntity validUomBs : validUomBsEntityList) {
				if (validUomBs.getUomCd().equals(uom.getUomCd())) {
					replacingUom = validUomBs;
					break;
				}
			}
			if (replacingUom == null) {
				throw newApplicationError("uom is not a possible uom for this material");
			}
		}
		return replacingUom;

	}


	@AdnTrace
	private List<UomBsEntity> findValidUomList(OrderEntity order, OrderItemEntity orderItem) {
		List<UomBsEntity> uomBsEntityList = new ArrayList<UomBsEntity>();

		final ShiptoSareaEntity shiptoSarea =
				shiptoSareaStore.findShiptoSareaWithActiveShipto(order.getShiptoSapBp(), order.getSarea());
		final DeliverySettingShiptoMap shiptoMap = deliverySettingService
				.initDeliverySettingShiptoMapForPlanningMaterialUpdate(order.getSoldtoSapBp(), Locale.ENGLISH, shiptoSarea,
						orderItem.getSapMaterial().getSapMaterialId());
		final DeliverySettingSalesAreaMap salesAreaMap = shiptoMap.getSalesAreaMapForSareaId(order.getSarea().getSareaId());

		DeliverySetting deliverySetting = new OrderItemDeliverySetting(order, orderItem);

		final DeliveryOptionConfigurationDTO deliveryOptionConfiguration =
				DeliverySettingFilterLogic.getDeliveryOptionConfiguration(deliverySetting, salesAreaMap);
		List<UomTxEntity> uomList = deliveryOptionConfiguration.getUomList();

		for (UomTxEntity uomTxEntity : uomList) {
			UomBsEntity validUomBs = uomTxEntity.getUomTxPK().getUomBs();
			uomBsEntityList.add(validUomBs);
		}
		return uomBsEntityList;
	}
}
