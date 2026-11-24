/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.AddServiceMaterialsToOrderRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.AddServiceMaterialsToOrderResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.MaterialData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ServiceMaterialData;
import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class AddServiceMaterialsToOrderRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final String FIELD_LOGON_ORDER_NO = "logonOrderNo";

	private static final String FIELD_SM = "serviceMaterial";

	private static final String FIELD_SM_MAT = "serviceMaterial[%d].material";

	private static final String FIELD_SM_MAT_MATERIAL_NO = "serviceMaterial[%d].material.materialNo";

	private static final String FIELD_SM_MAT_QUANTITY_UOM = "serviceMaterial[%d].material.quantityUom";

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final AddServiceMaterialsToOrderRequest request = (AddServiceMaterialsToOrderRequest) eventBean;
		validateMandatory(FIELD_LOGON_ORDER_NO, request.getLogonOrderNo());
		validateMandatory(FIELD_SM, request.getServiceMaterial());

		int i = 0;
		for (ServiceMaterialData serviceMaterial : request.getServiceMaterial()) {

			validateMandatory(String.format(FIELD_SM_MAT, i), serviceMaterial.getMaterial());
			validateMandatory(String.format(FIELD_SM_MAT_MATERIAL_NO, i), serviceMaterial.getMaterial().getMaterialNo());
			validateMandatory(String.format(FIELD_SM_MAT_QUANTITY_UOM, i), serviceMaterial.getMaterial().getQuantityUom());
			i++;
		}
	}

	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {

		final AddServiceMaterialsToOrderRequest request = (AddServiceMaterialsToOrderRequest) eventBean;
		final AddServiceMaterialsToOrderResponse response =
				(AddServiceMaterialsToOrderResponse) createLogonOKEventResponse(AddServiceMaterialsToOrderResponse.class);

		final OrderEntity order = getOrder(request.getLogonOrderNo(), FIELD_LOGON_ORDER_NO);

		final List<OrderItemEntity> orderItems = order.getOrderItems();
		final List<ServiceMaterialData> serviceMaterials = request.getServiceMaterial();
		OrderItemEntity newOrderItem;
		SapMaterialEntity sapMaterial;
		MaterialData material;
		UomBsEntity quantityUom;

		int i = 0;
		for (ServiceMaterialData serviceMaterial : serviceMaterials) {
			material = serviceMaterial.getMaterial();
			newOrderItem = null;

			for (OrderItemEntity orderItem : orderItems) {
				if (StringUtils.equals(orderItem.getSapMaterial().getSapMaterialIfcNo(), material.getMaterialNo())) {
					newOrderItem = orderItem;
					break;
				}
			}

			sapMaterial = getMaterial(material.getMaterialNo(), String.format(FIELD_SM_MAT_MATERIAL_NO, i));
			quantityUom = getUom(material.getQuantityUom(), String.format(FIELD_SM_MAT_QUANTITY_UOM, i));

			SapContractItemEntity receivedSapContractItem = null;

			if (StringUtils.isNotEmpty(material.getContractItemNumber())) {
				receivedSapContractItem = getSapContractItemEntity(order.getSapContract(), material.getContractItemNumber(),
						"order.contractItemNumber");
			}

			BigDecimal quantity = material.getQuantity();
			if (newOrderItem == null) {

				newOrderItem = new OrderItemEntity();
				newOrderItem.setOrderItemNo(Long.valueOf((orderItems.size() + 1) * 10));

				newOrderItem
						.setProductionPlant(referentialDataService.findBsEntity(PlantBsEntity.class, material.getProdPlantNo()));
				newOrderItem.setSapMaterial(sapMaterial);
				newOrderItem.setDeliveredQuantity(quantity);
				newOrderItem.setDeliveredUom(quantityUom);
				newOrderItem.setOrderedQuantity(quantity);
				newOrderItem.setOrderedUom(quantityUom);
				newOrderItem.setPlannedLoadingTimeMin(0L);

				if (material.getNumerator() != null) {
					newOrderItem.setOrderedUomNumerator(material.getNumerator().longValueExact());
				}
				if (material.getDenominator() != null) {
					newOrderItem.setOrderedUomDenominator(material.getDenominator().longValueExact());
				}

				newOrderItem.setSapContractItem(receivedSapContractItem);

				final List<OrderItemEntity> newOrderItems = new ArrayList<OrderItemEntity>();
				newOrderItems.add(newOrderItem);
				newOrderItem.setOrder(order);
				orderItems.add(newOrderItem);

				orderService.initLaterAddedOrderItems(order);

				orderDetailService.validateOrderInSeparateTransaction(order);
			}
			else {

				orderService.adjustQuantities(newOrderItem, sapMaterial, receivedSapContractItem,
						quantity, quantityUom);
			}

			i++;
		}

		response.setOrder(ldsService.createOrderData(order));

		return response;
	}


}
