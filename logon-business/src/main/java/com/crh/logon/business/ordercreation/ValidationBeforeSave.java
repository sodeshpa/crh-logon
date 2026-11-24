/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ordercreation;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Validates order data that is ready to save (and no more changes are done to them).
 * 
 * TODO (romek Sept 26, 2011): currently I have not completely decided where to put this code, I will delay
 * this decision for later.
 * 
 * @author AdNovum Informatik AG
 */
public class ValidationBeforeSave extends InvalidMapSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ValidationBeforeSave.class);
//
//	private final OrderService orderService;
//	private final DeliverySettingService deliverySettingService;
//	private final OrderProcessHelper orderProcess;
//
//	public ValidationBeforeSave(OrderProcessHelper orderProcessHelper) {
//		this.orderProcess = orderProcessHelper;
//		this.orderService = orderProcess.getOrderService();
//		this.deliverySettingService = orderProcess.getDeliverySettingService();
//	}
//
//	/**
//	 * Validate everything that has to be done right before saving (especially things that can be changed
//	 * during order creation).
//	 * 
//	 * @param order
//	 */
//	public void validateOrder(OrderEntity order) {
//		// [[
//		Object[][] params = { { "order", order }, { "orderService", orderService } };
//		boolean logDebugEnabled = LOG.isDebugEnabled();
//		if (logDebugEnabled) {
//			LOG.entering(this, "validateOrder", params);
//		}
//		// ]]
//		try {
//			List<OrderEntity> orders = new ArrayList<OrderEntity>(1);
//			orders.add(order);
//			validateOrders(orders);
//		}
//		finally {
//			// [[
//			if (logDebugEnabled) {
//				LOG.exiting();
//			}
//			// ]]
//		}
//	}
//
//	/**
//	 * Validate everything that has to be done right before saving (especially things that can be changed
//	 * during order creation).
//	 * 
//	 * @param orders
//	 */
//	public void validateOrders(List<OrderEntity> orders) {
//		// [[
//		Object[][] params = { { "orders", orders }, { "orderService", orderService } };
//		boolean logDebugEnabled = LOG.isDebugEnabled();
//		if (logDebugEnabled) {
//			LOG.entering(this, "validateOrders", params);
//		}
//		// ]]
//		try {
//			for (OrderEntity order : orders) {
//				validateOrderItems(order);
//			}
//			
//			/*
//			 * The following check should only run if the consistency of the order data is assured,
//			 * that is, if no other validation errors occurred.
//			 */
//			if (getInvalidMap().isEmpty()) {
//				List<QuantityRestrictionError> quantityErrors = orderService.validateQuantities(orders);
//				
//				for (QuantityRestrictionError quantityError : quantityErrors) {
//					updateInvalidMap(quantityError, ShiptoSalesAreaError.QUANTIY_RESTRICTED);
//				}
//			}
//		}
//		finally {
//			// [[
//			if (logDebugEnabled) {
//				LOG.exiting();
//			}
//			// ]]
//		}
//	}
//
//	/**
//	 * Validates all the order items one by one against certain settings of the order.
//	 * @param orders
//	 */
//	private void validateOrderItems(OrderEntity order) {
//
//		/**
//		 * Phase 1: Checks regarding the combination of order items: like cannot mix bag and bulk orders, or
//		 * cannot mix free of charge orders
//		 */
//		final Set packageValidationErrors = LogonPackageHandlingUtil.checkLogonPackageCombination(order.getOrderItems());
//		if (!packageValidationErrors.isEmpty()) {
//			updateInvalidMap(order, ShiptoSalesAreaError.MATERIAL_PACKAGE_COMBINATION);
//		}
//
//		// TODO (emoke Oct 4, 2011): Check if the contract is common! This should basically be 
//		validateOrderItemDeliverySetting(order);
//
//	}
//
//	/**
//	 * Authorization is skipped for most of the calls here, because the saving should not fail because of that. 
//	 * In the meantime it is up to the order taking source not to let such attributes to be set, which are not supposed
//	 * to be available for the order taker.
//	 * 
//	 * @param order
//	 */
//	private void validateOrderItemDeliverySetting(OrderEntity order) {
//		
//		/**
//		 * For this material selection all materials which come from contracts are present, and all are resolved to generic materials.
//		 * The order items has to be present among these settings, otherwise it should not be present at all, and that signs that the
//		 * order item is invalid.
//		 * 
//		 * The validation is 2 phase:
//		 * Phase 1: Checking the qualification of the material + contract item presence in the order item
//		 * Phase 2: Checking the validity of the delivery setting: the material has to be available at the shippingpoint, must eb available for
//		 * road/rail etc. rules, the uom has to be active etc. 
//		 * If the delivery setting's attributes what is formed from the order item + order attributes (see OrderItemDeliverySetting) are all available
//		 * in the valid option values for each attribute, the order item is valid. 
//		 */
//		DeliverySettingSalesAreaMap salesAreaMap = orderProcess.initSareaMap(order);
//
//		for (Object item : order.getOrderItems()) {
//
//			final OrderItemEntity orderItem = (OrderItemEntity) item;
//			final OrderItemDeliverySetting itemDeliverySetting = new OrderItemDeliverySetting(order, orderItem);
//			final DeliverySettingDTO deliverySetting = salesAreaMap.getDeliverySettingByCompositeIdentifier(itemDeliverySetting.getCompositeIdentifier());
//			
//			/**
//			 * This here is supposed to check all meaningful attributes of the sales area, and fill out the
//			 * sales area map's invalid map. This will be later on used when validating the order item's delivery setting.
//			 */
//			salesAreaMap.validateSapBpSareaRelatedData(deliverySettingService);
//
//			/*
//			 * Phase 1: Check the material + contract item pair (if there is a contract item at all - for services there might be no contract item, but overall they can be present in an order) 
//			 */
//			if (deliverySetting == null) {
//				/**
//				 * In case in the sales area map this delivery setting is not present,
//				 * that means that the product is not available for the contract item 
//				 * the order item specifies ==> it is invalid! This should not happen from any of
//				 * the order taking sources. For most of the order taking sources this is guaranteed that on the UI
//				 * nothing is selectable what is not valid. But in case it does, this validation has to
//				 * make sure that it does not happen. For the order taking sources loading systems nothing
//				 * is guaranteed, as in a request anything can appear in any combination.
//				 */
//				updateInvalidMap(orderItem, ShiptoSalesAreaError.CONTRACT_INVALID);
//				continue;
//			}
//			
//			/*
//			 * Phase 2
//			 * Now check the delivery setting's validity. This can very well cause save failure. On all order taking scrrens the options are filtered for the
//			 * first item. The second item has poor pre-validation if it is valid against the order level settings.
//			 */
//			final DeliveryOptionConfigurationDTO validOptionConfiguration = DeliverySettingFilterLogic.getDeliveryOptionConfiguration(itemDeliverySetting, salesAreaMap);
//
//			salesAreaMap.validateDeliverySetting(itemDeliverySetting, validOptionConfiguration,
//					getInvalidMap());
//
//		}
//	}	
//
//	@Override
//	public Map<Object, List<ShiptoSalesAreaError>> getInvalidMap() {
//		return super.getInvalidMap();
//	}

}
