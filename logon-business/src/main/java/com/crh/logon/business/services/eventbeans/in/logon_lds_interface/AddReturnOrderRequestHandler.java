/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ContributionCalcServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.CreditNoteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.FrozenTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.GoodsMovementServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnMaterialUsageBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnOrderReasonBsEntity;
import com.crh.logon.persistence.entities.refdata.ReturnQuantHandlingBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.AddReturnOrderRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.AddReturnOrderResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderItemReturnData;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class AddReturnOrderRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final String FIELD_LOGON_ORDER_NO = "logonOrderNo";

	private static final String FIELD_SM = "sapMaterial";

	private static final String FIELD_RETURNED_UOM = "returnedUom";

	private static final String FIELD_DELIVERED_QTY = "deliveredQuantity";

	private static final String FIELD_RETURNED_QTY = "returnedQuantity";

	private static final String FIELD_DATE_OF_RETURN = "dateOfReturn";

	private static final String FIELD_RETURN_ORDER_REASON = "returnOrderReason";

	@EJB
	private CreditNoteServiceLocalInterface creditNoteService;

	@EJB
	private GoodsMovementServiceLocalInterface goodsMovementService;

	@EJB
	private ContributionCalcServiceLocalInterface contributionCalcService;

	@EJB
	private FrozenTablesServiceLocalInterface frozenTablesService;

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final AddReturnOrderRequest request = (AddReturnOrderRequest) eventBean;
		validateMandatory(FIELD_LOGON_ORDER_NO, request.getLogonOrderNo());
		request.getOrderItemReturnData().stream().forEach(orderItemReturnData -> {
			validateMandatory(FIELD_SM, orderItemReturnData.getSapMaterial());
			validateMandatory(FIELD_RETURNED_UOM, orderItemReturnData.getReturnedUom());
			validateMandatory(FIELD_DELIVERED_QTY, orderItemReturnData.getDeliveredQuantity());
			validateMandatory(FIELD_RETURNED_QTY, orderItemReturnData.getReturnedQuantity());
			validateMandatory(FIELD_DATE_OF_RETURN, orderItemReturnData.getDateOfReturn());
			validateMandatory(FIELD_RETURN_ORDER_REASON, orderItemReturnData.getReturnOrderReason());
		});
	}

	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {

		final AddReturnOrderRequest request = (AddReturnOrderRequest) eventBean;
		final AddReturnOrderResponse response = createLogonOKEventResponse(AddReturnOrderResponse.class);

		final OrderEntity order = getOrder(request.getLogonOrderNo(), FIELD_LOGON_ORDER_NO);
		if (order == null) {
			throw new LogonException("No order found for order number " + request.getLogonOrderNo(), Reason.RES_ERROR_IMPL);
		}

		if (!order.getOrderStatus().getOrderStatusCd().equals(Constant.ORDER_STATUS_DELIVERED) &&
				!order.getOrderStatus().getOrderStatusCd().equals(Constant.ORDER_STATUS_FINISHED)) {
			throw new LogonException("Order status should be in delivered or finished state", Reason.RES_ERROR_IMPL);

		}
		request.getOrderItemReturnData().stream().forEach(returnData -> {
			if (!order.getOrderItems().stream().filter(item -> item.getSapMaterial().getSapMaterialIfcNo().equals(returnData.getSapMaterial())).findFirst().isPresent()) {
				throw new LogonException("Could not find order item for the selected material:" + returnData.getSapMaterial(),
						Reason.RES_ERROR_IMPL);
			}
		});
		order.getOrderItems().stream().forEach(orderItem -> {
			Optional<OrderItemReturnData> orderItemReturnData =
					request.getOrderItemReturnData().stream()
							.filter(data -> data.getSapMaterial().equals(orderItem.getSapMaterial().getSapMaterialIfcNo()))
							.findFirst();
			if (!orderItemReturnData.isPresent()) {
				return;
			}
			if (orderItem.getReturnedQuantity() != null) {
				throw new LogonException(
						"The returned quantity is already set for this order item. No change is allowed for LDS.",
						Reason.RES_ERROR_IMPL);
			}
			OrderItemReturnData itemReturnData = orderItemReturnData.get();
			ReturnOrderReasonBsEntity returnOrderReason = referentialDataService.findBsEntity(
					ReturnOrderReasonBsEntity.class, itemReturnData.getReturnOrderReason());
			if (returnOrderReason == null) {
				throw new LogonException("Returned Order Reason is invalid " + itemReturnData.getReturnedUom(),
						Reason.RES_ERROR_IMPL);
			}
			BigDecimal returnMaterialUsageCd = itemReturnData.getReturnMaterialUsageCd();
			if (returnMaterialUsageCd == null) {
				returnMaterialUsageCd = BigDecimal.ZERO;
			}
			ReturnMaterialUsageBsEntity returnMaterialUsage = referentialDataService.findBsEntity(
					ReturnMaterialUsageBsEntity.class, returnMaterialUsageCd.longValue());

			final List<OrderItemEntity> orderItems = order.getOrderItems();
			final ShippingpointEntity returnShippingPoint = order.getShippingpoint();
			final PlantBsEntity returnPlant = returnShippingPoint.getPlant();
			String materialNo = itemReturnData.getSapMaterial();
			UomBsEntity returnedUom = getUom(itemReturnData.getReturnedUom(), FIELD_RETURNED_UOM);

			orderItem.setReturnedQuantity(itemReturnData.getReturnedQuantity());
			orderItem.setDeliveredQuantity(itemReturnData.getDeliveredQuantity());
			orderItem.setReturnedUom(returnedUom);
			orderItem.setReturnOrderReason(returnOrderReason);
			orderItem.setReturnMaterialUsage(returnMaterialUsage);
			orderItem.setReturnShippingpoint(returnShippingPoint);
			orderItem.setReturnPlant(returnPlant);
			Long returnQuantHandlingCode = Constant.RETURN_QUANT_HANDLING_NO_ADJUSTMENT;
			ReturnQuantHandlingBsEntity rqhBsEntity = referentialDataService.findBsEntity(
					ReturnQuantHandlingBsEntity.class, returnQuantHandlingCode);
			orderItem.setReturnQuantHandling(rqhBsEntity);
			creditNoteService.handleCreditNoteQuantities(orderItem, Boolean.FALSE);

		});

		orderDetailService.validateOrderInSeparateTransaction(order);
		orderDetailService.updateOrder(order, false, false, null, false, null);
		if (order.getOrderStatus().getOrderStatusCd().equals(Constant.ORDER_STATUS_FINISHED)){
			contributionCalcService.updateCosts(order);
			em.flush(); // order final needs this to be persisted because it uses a view on this data
			frozenTablesService.freezeOrder(order);
		}

		OrderEntity orderResponse = getDaoProvider().getOrderEntityDao().findById(order.getOrderId());
		response.setOrder(ldsService.createOrderData(orderResponse));

		return response;
	}
}

