
package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.beans.LoadedQuantityData;
import com.crh.logon.business.services.beans.LoadingDataOrderItemBean;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.impl.endload.ParsedEndLoadingPart;
import com.crh.logon.business.services.impl.endload.ParsedPalletReturn;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.EndLoadingServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingPartData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingPartData.AffectedOrders;
import com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingRequest.ReturnPallets;
import com.holcim.logon.eventbeans.in.logon_lds_interface.EndLoadingResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.FirstWeighingData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LoadedMaterialData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SecondWeighingData;
import org.apache.commons.lang.StringUtils;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class EndLoadingRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EndLoadingRequestHandler.class);

	private static final String FIELD_VEHICLE = "vehicle";
	private static final String FIELD_END_LOADING_PART = "endLoadingPart";

	private static final String FIELD_RP_LOGON_ORDER_NO = "returnPallets[%d].logonOrderNo";
	private static final String FIELD_RP_MATERIAL_NO = "returnPallets[%d].materialNo";
	private static final String FIELD_RP_QUANTITY = "returnPallets[%d].quantity";
	private static final String FIELD_RP_QUANTITY_UOM = "returnPallets[%d].quantityUom";

	private static final String FIELD_ELPD = "endLoadingPartData[%d]";
	private static final String FIELD_ELPD_LOAD_ORDER_ID = FIELD_ELPD + ".loadOrderId";
	private static final String FIELD_ELPD_LOAD_ORDER_ITEM_ID = FIELD_ELPD + ".loadOrderItemId";
	private static final String FIELD_ELPD_AO_LOGON_ORDER_NO = FIELD_ELPD + ".affectedOrders[%d].logonOrderNo";
	private static final String FIELD_ELPD_AO_LOGON_ORDER_ITEM_NUMBER = FIELD_ELPD + ".affectedOrders[%d].logonOrderItemNumber";
	private static final String FIELD_ELPD_AO_NET_WEIGHT = FIELD_ELPD + ".affectedOrders[%d].netWeight";
	private static final String FIELD_ELPD_AO_NET_WEIGHT_UOM = FIELD_ELPD + ".affectedOrders[%d].netWeightUom";
	private static final String FIELD_ELPD_MATERIAL = FIELD_ELPD + ".material";
	private static final String FIELD_ELPD_SECOND_WEIGHING = FIELD_ELPD + ".secondWeighing";
	private static final String FIELD_ELPD_SECOND_WEIGHING_W2UOM = FIELD_ELPD + ".secondWeighing.w2UoM";
	private static final String FIELD_ELPD_NET_WEIGHT = FIELD_ELPD + ".netWeight";
	private static final String FIELD_ELPD_NET_WEIGHT_UOM = FIELD_ELPD + ".netWeightUom";

	private static final String FIELD_ELPD_FIRST_WEIGHTING_W1WEIGHING_POINT_NO = "endLoadingPartData[%d].fisrtWeighingData.w1WeighingPointNo";
	private static final String FIELD_ELPD_SECOND_WEIGHTING_W2WEIGHING_POINT_NO = "endLoadingPartData[%d].secondWeighingData.w2WeighingPointNo";

	@EJB private EndLoadingServiceLocalInterface endLoadingService;

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final EndLoadingRequest request = (EndLoadingRequest) eventBean;

		validateMandatory(FIELD_VEHICLE, request.getVehicle());
		validateMandatory(FIELD_END_LOADING_PART, request.getEndLoadingPart());

		final List<EndLoadingPartData> parts = request.getEndLoadingPart();

		if (parts.isEmpty()) {
			throw newMandatoryFieldEmpty(FIELD_END_LOADING_PART, request.getEndLoadingPart());
		}

		final EndLoadingPartData firstPart = parts.get(0);
		final String loadOrderId = firstPart.getLoadOrderId();
		final AssignmentEntity assignment = getAssignment(loadOrderId,
				String.format(FIELD_ELPD_LOAD_ORDER_ID, 0));

		verifyNoPlanning(assignment);

		int i = 0;
		for (EndLoadingPartData endLoadingPartData : request.getEndLoadingPart()) {

			validateMandatory(String.format(FIELD_ELPD_LOAD_ORDER_ID, i), endLoadingPartData.getLoadOrderId());
			validateMandatory(String.format(FIELD_ELPD_LOAD_ORDER_ITEM_ID, i), endLoadingPartData.getLoadOrderItemId());
			
			int j = 0;
			for (AffectedOrders affectedOrders : endLoadingPartData.getAffectedOrders()) {

				validateMandatory(String.format(FIELD_ELPD_AO_LOGON_ORDER_NO, i, j), affectedOrders.getLogonOrderNo());
				validateMandatory(String.format(FIELD_ELPD_AO_LOGON_ORDER_ITEM_NUMBER, i, j), affectedOrders.getLogonOrderItemNumber());
				validateMandatory(String.format(FIELD_ELPD_AO_NET_WEIGHT, i, j), affectedOrders.getNetWeight());
				validateMandatory(String.format(FIELD_ELPD_AO_NET_WEIGHT_UOM, i, j), affectedOrders.getNetWeightUoM());
				j++;
			}
			validateMandatory(String.format(FIELD_ELPD_MATERIAL, i), endLoadingPartData.getMaterial());
			validateMandatory(String.format(FIELD_ELPD_NET_WEIGHT, i), endLoadingPartData.getNetWeight());
			validateMandatory(String.format(FIELD_ELPD_NET_WEIGHT_UOM, i), endLoadingPartData.getNetWeightUoM());
			i++;
		}
		
		i = 0;
		for (ReturnPallets returnPallets : request.getReturnPallets()) { 
			validateMandatory(String.format(FIELD_RP_LOGON_ORDER_NO, i), returnPallets.getLogonOrderNo());
			validateMandatory(String.format(FIELD_RP_MATERIAL_NO, i), returnPallets.getMaterialNo());
			validateMandatory(String.format(FIELD_RP_QUANTITY, i), returnPallets.getQuantity());
			validateMandatory(String.format(FIELD_RP_QUANTITY_UOM, i), returnPallets.getQuantityUom());
			i++;
		}
	}
	
	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {
		
		final EndLoadingRequest request = (EndLoadingRequest) eventBean;
		final EndLoadingResponse response = createLogonOKEventResponse(EndLoadingResponse.class);

		final Date eventTime = request.getEventTime();
		final List<EndLoadingPartData> parts = request.getEndLoadingPart();
		
		final List<ParsedPalletReturn> palletReturns = parsePalletReturns(request.getReturnPallets());
		
		final EndLoadingPartData firstPart = parts.get(0);
		final String loadOrderId = firstPart.getLoadOrderId();
		final AssignmentEntity assignment = getAssignment(loadOrderId,
				String.format(FIELD_ELPD_LOAD_ORDER_ID, 0));
		final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);

		/* A set to keep track of all orders for which a pallet return was processed. This is
		 * used in the validation of the pallet returns: After this for loop, we go again
		 * through the list of pallet returns and make sure every return item could be processed.
		 */
		final Set<Long> returnProcessedOrderNumbers = new HashSet<>();
		for (OrderEntity oe : orders) {

			final List<OrderItemEntity> items = oe.getOrderItems();
			for (OrderItemEntity item : items) {
				// Check if there is a pallet return.
				/* Bad algorithm to loop here through all returnPallet objects, but let's
				 * assume that the number of total returned pallet entries is very small (0 or 1).
				 * If it becomes an issue, create a hashmap from the returnPallet data and look
				 * up any return by orderNo and sapMaterialNo.
				 */
				for (ParsedPalletReturn palletReturn : palletReturns)
				{
					final Long returnOrderNo = palletReturn.getOrderNo();
					final String returnSapMatNo = palletReturn.getSapMaterialNo();
					
					if (item.getOrder().getOrderNumber().equals(returnOrderNo)
							&& item.getSapMaterial().getSapMaterialIfcNo().equals(returnSapMatNo))
					{
						// Mark this order as "return-processed"
						returnProcessedOrderNumbers.add(palletReturn.getOrderNo());
					}
				}
			}
		}

		// Validation of returns: Make sure there are no pallet returns for which there
		// was no order.
		final List<ParsedPalletReturn> unprocessedReturns;
		unprocessedReturns = unprocessedPalletReturns(palletReturns, returnProcessedOrderNumbers);
		
		if (!unprocessedReturns.isEmpty())
		{
			final String errorMsg = unprocessedReturnErrorMessage(unprocessedReturns);
			throw newApplicationError(errorMsg);
		}
		
		final OrderEntity firstOrder = orders.get(0);
		boolean reverseOrder = LogonUtil.isOrderWithReversedDirection(firstOrder);
		final ShippingpointEntity sp = firstOrder.getShippingpoint();
		
		List<ParsedEndLoadingPart> endLoadingParts = new LinkedList<>();
		
		// order number -> lds delivery note number
		Map<Long, String> ldsDeliveryNoteNumberMap = new HashMap<>();

		List<LoadingDataOrderItemBean> allLoadingDataBeans = new ArrayList<>();

		int i = 0;
		for (EndLoadingPartData endLoadingPartData : request.getEndLoadingPart()) {
			
			endLoadingPartData = recalculateWeighingData(request, endLoadingPartData, reverseOrder);
			final FirstWeighingData firstWeighingData = endLoadingPartData.getFirstWeighing();
			final SecondWeighingData secondWeighingData = endLoadingPartData.getSecondWeighing();
			
			final AssignmentPartEntity ape = getAssignmentPartForLoading(endLoadingPartData.getLoadOrderId(),
					String.format(FIELD_ELPD_LOAD_ORDER_ID, i),
					endLoadingPartData.getLoadOrderItemId(),
					String.format(FIELD_ELPD_LOAD_ORDER_ITEM_ID, i));
			
			final LoadEntity load = ape.getLoad();
			LoadingpointEntity lp = getLoadingpointForLoad(load, sp, secondWeighingData, firstWeighingData, 
					FIELD_ELPD_SECOND_WEIGHTING_W2WEIGHING_POINT_NO, FIELD_ELPD_FIRST_WEIGHTING_W1WEIGHING_POINT_NO);
			
			final Date loadingEndTime;
			if (secondWeighingData != null && secondWeighingData.getLoadingEndTime() != null) {
				loadingEndTime = secondWeighingData.getLoadingEndTime();
			}
			else {
				loadingEndTime = eventTime;
			}
			
			BigDecimal grossWeightT = null;
			if (secondWeighingData != null) {
				final BigDecimal grossWeight = secondWeighingData.getW2Quantity();
				if (StringUtils.isNotEmpty(secondWeighingData.getW2UoM()) && (grossWeight != null)) {
					final UomBsEntity grossWeightUom = getUom(secondWeighingData.getW2UoM(), String.format(FIELD_ELPD_SECOND_WEIGHING_W2UOM, i));
					grossWeightT = getAsT(grossWeight, grossWeightUom);
				}
			}
			
			final List<OrderItemEntity> orderItems = orderService.getOrderItems(load);
			if (orderItems == null || orderItems.isEmpty()) {
				throw newApplicationError("There is no order item referring to this load. This can only happen in pre-loading, which is not supported.");
			}
			
			Map<Long, LoadedQuantityData> loadingData = new HashMap<>();
			final List<AffectedOrders> affectedOrders = endLoadingPartData.getAffectedOrders();
			
			if (!affectedOrders.isEmpty()) {
				final Map<Long, Map<Long, LoadedQuantityData>> parsedAffectedOrders = 
						new HashMap<>();
				
				int j = 0;
				for (AffectedOrders affectedOrder : affectedOrders) {
					final Long orderNo = Long.valueOf(affectedOrder.getLogonOrderNo());
					final String ldsDeliveryNoteNo = StringUtils.stripToNull(affectedOrder.getLdsDeliveryNoteNo());
					final Long orderItemNumber = affectedOrder.getLogonOrderItemNumber().longValue();
					final BigDecimal netQuantity = affectedOrder.getNetWeight();
					final UomBsEntity netQuantityUom = getUom(affectedOrder.getNetWeightUoM(), String.format(FIELD_ELPD_AO_NET_WEIGHT_UOM, i, j));
					
					// retrieve LDS delivery note number
					if (ldsDeliveryNoteNo != null && !ldsDeliveryNoteNumberMap.containsKey(orderNo)) {
						ldsDeliveryNoteNumberMap.put(orderNo, ldsDeliveryNoteNo);
					}
					
					final LoadedQuantityData loadedQuantityData = 
							new LoadedQuantityData(netQuantity, netQuantityUom.getUomCd());
					
					if (!parsedAffectedOrders.containsKey(orderNo)) {
						parsedAffectedOrders.put(orderNo, new HashMap<Long, LoadedQuantityData>());
					}
					
					parsedAffectedOrders.get(orderNo).put(orderItemNumber, loadedQuantityData);
					
					j++;
				}
				
				for (OrderItemEntity orderItem : orderItems) {
					final Long orderItemId = orderItem.getOrderItemId();
					final Long orderNo = orderItem.getOrder().getOrderNumber();
					final Long orderItemNumber = orderItem.getOrderItemNo();
					
					LoadedQuantityData loadedQuantityData = null;
					final Map<Long, LoadedQuantityData> perOrder = parsedAffectedOrders.get(orderNo);
					if (perOrder != null) {
						loadedQuantityData = perOrder.get(orderItemNumber);
					}
					
					if (loadedQuantityData == null) {
						throw newApplicationError(String.format(
								"Affected orders missing for order %d in in %s",
								orderNo,
								String.format(FIELD_ELPD, i)));
					}
					
					loadingData.put(orderItemId, loadedQuantityData);

					allLoadingDataBeans.add(new LoadingDataOrderItemBean(loadedQuantityData, orderItem));

				}
			}
			else if (orderItems.size() == 1) {
				final BigDecimal netQuantity = endLoadingPartData.getNetWeight();
				final UomBsEntity netQuantityUom = getUom(endLoadingPartData.getNetWeightUoM(), String.format(FIELD_ELPD_NET_WEIGHT_UOM, i));
				final OrderItemEntity orderItem = orderItems.get(0);
				
				final LoadedQuantityData loadedQuantityData = new LoadedQuantityData(netQuantity, netQuantityUom.getUomCd());
				loadingData.put(orderItem.getOrderItemId(), loadedQuantityData);

				allLoadingDataBeans.add(new LoadingDataOrderItemBean(loadedQuantityData, orderItem));

			}
			else {
				throw newApplicationError(String.format(
						"Affected orders missing in %s", String.format(FIELD_ELPD, i)));
			}
			
			final LoadedMaterialData loadedMaterial = endLoadingPartData.getMaterial();
			final String aggRmxCode = loadedMaterial.getAggRmxCode();
			final String mixDesignCd = loadedMaterial.getMixDesignID();
			final FireqMaterialMixdesignEntity mixDesign;
			if ((aggRmxCode != null) && (mixDesignCd != null)) {
				mixDesign = getMixDesign(mixDesignCd, String.format(FIELD_ELPD_MATERIAL, i) + ".mixDesignID");
			}
			else {
				mixDesign = null;
			}
			
			ParsedEndLoadingPart endLoadingPart = new ParsedEndLoadingPart(
					endLoadingPartData,
					ape.getAssignmentPartId(),
					lp,
					loadingEndTime,
					grossWeightT,
					loadingData,
					(mixDesign != null) ? mixDesign.getMixDesignId() : null);
			endLoadingParts.add(endLoadingPart);
			
			i++;
		}

		checkForInvalidLoadedQtys(allLoadingDataBeans);

		// fall-back for LDS delivery note number: use from request level if not specified otherwise
		if (orders.size() == 1) {
			String ldsDeliveryNoteNo = StringUtils.stripToNull(request.getLdsDeliveryNoteNo());
			Long orderNumber = firstOrder.getOrderNumber();
			if (ldsDeliveryNoteNo != null 
					&& !ldsDeliveryNoteNumberMap.containsKey(orderNumber)) {
				ldsDeliveryNoteNumberMap.put(orderNumber, ldsDeliveryNoteNo);
			}
		}
		
		endLoadingService.endLoading(
				assignment,
				sp,
				request,
				endLoadingParts,
				palletReturns,
				ldsDeliveryNoteNumberMap);
		
		/*
		 * The order entities are updated in another transaction, hence we must refresh them
		 * here in order to return up-to-date information to the LDS.
		 */
		final AssignmentEntity updatedAssignment = 
				getDaoProvider().getAssignmentEntityDao().findById(assignment.getAssignmentId());
		final List<OrderEntity> updatedOrders = 
				AssignmentUtils.getOrdersFromAssignment(updatedAssignment);
		final List<OrderData> responseOrders = response.getOrder();
		for (OrderEntity oe : updatedOrders) {
			final OrderData orderData = ldsService.createOrderData(oe);
			responseOrders.add(orderData);
		}
		
		return response;
	}
	
	/**
	 * Throws LogonEventException if there are any invalid loaded quantities. 
	 * Rule:
	 * if BULK:
	 *  loaded quantity should be positive
	 * if BAG
	 *   loaded quantity should not be negative for any order item
	 *   there should be at least one positive loaded quantity (others can be >= 0)
	 */
	private void checkForInvalidLoadedQtys(List<LoadingDataOrderItemBean> allLoadingDataBeans) {

		// Map<OrderNumber, List<LoadingDataBean>>
		Map<Long, List<LoadingDataOrderItemBean>> perOrderQuantity = new HashMap<>();

		// Group LoadingDataBean objects by order number
		for (LoadingDataOrderItemBean loadingDataBean : allLoadingDataBeans) {

			OrderEntity order = loadingDataBean.getOrder();
			Long orderNumber = order.getOrderNumber();

			List<LoadingDataOrderItemBean> loadedQuantitiesForOrder = perOrderQuantity.get(orderNumber);
			if (loadedQuantitiesForOrder == null) {
				loadedQuantitiesForOrder = new ArrayList<>();
			}

			loadedQuantitiesForOrder.add(loadingDataBean);

			perOrderQuantity.put(orderNumber, loadedQuantitiesForOrder);

		}

		for (Long orderNumber : perOrderQuantity.keySet()) {

			boolean orderHasValidLoadedQuantity = false;

			List<LoadingDataOrderItemBean> list = perOrderQuantity.get(orderNumber);
			for (LoadingDataOrderItemBean loadingDataBean : list) {

				OrderEntity order = loadingDataBean.getOrder();

				// Bulk Validation
				if (LogonUtil.isBulkOrder(order)) {
					LoadedQuantityData loadedQuantityData = loadingDataBean.getLoadedQuantityData();
					BigDecimal loadedQuantity = loadedQuantityData.getLoadedQuantity();
					if (loadedQuantity.compareTo(BigDecimal.ZERO) <= 0) {
						OrderItemEntity orderItem = loadingDataBean.getOrderItem();
						SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
						Long sapMaterialId = sapMaterial.getSapMaterialId();
						String errorMessage = String.format("Loaded quantity should be greater than zero! sapMaterialId=%d quantity=%f", sapMaterialId, loadedQuantity);
						throw newApplicationError(errorMessage);
					}
					orderHasValidLoadedQuantity = true;
				}
				else if (LogonUtil.isBagOrder(order)) {
					LoadedQuantityData loadedQuantityData = loadingDataBean.getLoadedQuantityData();
					BigDecimal loadedQuantity = loadedQuantityData.getLoadedQuantity();
					if (loadedQuantity.compareTo(BigDecimal.ZERO) < 0) {
						OrderItemEntity orderItem = loadingDataBean.getOrderItem();
						SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
						Long sapMaterialId = sapMaterial.getSapMaterialId();
						String errorMessage = String.format("Loaded quantity should be greater than or equal to zero! sapMaterialId=%d quantity=%f", sapMaterialId, loadedQuantity);
						throw newApplicationError(errorMessage);
					}
					else if (loadedQuantity.compareTo(BigDecimal.ZERO) > 0) {
						orderHasValidLoadedQuantity = true;
					}
				}

			}

			if (!orderHasValidLoadedQuantity) {
				String errorMessage = String.format("Order has no positive loaded quantity! orderNumber=%d", orderNumber);
				throw newApplicationError(errorMessage);
			}

		}

	}

	/**
	 * Validates first and secondWeighing (depends on reverse or not).
	 * Changes first and second weighing data in the request in these cases:
	 * - if global weighing data is received, only that one can be used further instead of 
	 *   the weighing data in endloadingparts
	 * - in case of reverse order (like landfill): changing order of weighing data
	 * 
	 * @param request
	 * @param endLoadingPartData
	 * @param reverseOrder
	 * @return endLoadingPartData
	 */
	// TODO (orsika Aug 14, 2013): resetting values in the request is an ugly solution. 
	// but its values are used in further phases of this request call, and this logic shouldn't 
	// run more times...
	private EndLoadingPartData recalculateWeighingData(EndLoadingRequest request, EndLoadingPartData endLoadingPartData, 
			boolean reverseOrder) {
		// [[
		EndLoadingPartData result = null;
		Object[][] params = {{"request", request},
							{"endLoadingPartData", endLoadingPartData},
							{"reverseOrder", reverseOrder + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "recalculateWeighingData", params);
		}
		// ]]
		try {
			if (reverseOrder && request.getFirstWeighing() == null && endLoadingPartData.getFirstWeighing() == null) {
				throw newApplicationError("FirstWeighing field mustn't be empty for reverse orders");
			}
			else if (!reverseOrder && request.getSecondWeighing() == null && endLoadingPartData.getSecondWeighing() == null) {
				throw newApplicationError("SecondWeighing field mustn't be empty for orders (non-reverse case)");
			}
			
			final FirstWeighingData globalFirstWeighingData = request.getFirstWeighing();
			final SecondWeighingData globalSecondWeighingData = request.getSecondWeighing();
			boolean isGlobalWeighingDataReceived = false;
			if (globalFirstWeighingData != null || globalSecondWeighingData != null) {
				isGlobalWeighingDataReceived = true;
			}
			
			if (isGlobalWeighingDataReceived) {
				endLoadingPartData.setFirstWeighing(globalFirstWeighingData);
				endLoadingPartData.setSecondWeighing(globalSecondWeighingData);
			}
			if (reverseOrder) {
				FirstWeighingData firstWeighingDataReversed = createFirstWeighingDataFromSecondWeighing(endLoadingPartData.getSecondWeighing());
				SecondWeighingData secondWeighingDataReversed = createSecondWeighingDataFromFirstWeighing(endLoadingPartData.getFirstWeighing());
				endLoadingPartData.setFirstWeighing(firstWeighingDataReversed);
				endLoadingPartData.setSecondWeighing(secondWeighingDataReversed);
			}
			result = endLoadingPartData;
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * It creates a SecondWeighingData object based on the parameter. Typical used
	 * for reverse directed orders.
	 * @param firstWeighing
	 * @return SecondWeighingData
	 */
	private SecondWeighingData createSecondWeighingDataFromFirstWeighing(
			FirstWeighingData firstWeighing) {
		// [[
		SecondWeighingData result = null;
		Object[][] params = {{"firstWeighing", firstWeighing}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createSecondWeighingDataFromFirstWeighing", params);
		}
		// ]]
		try {
			SecondWeighingData secondWeighing = new SecondWeighingData();
			secondWeighing.setLdsW2BalanceId(firstWeighing.getLdsW1BalanceId());
			secondWeighing.setLdsW2RecordId(firstWeighing.getLdsW1RecordId());
			secondWeighing.setLdsW2Time(firstWeighing.getLdsW1Time());
			secondWeighing.setLoadingEndTime(firstWeighing.getLoadingStartTime());
			secondWeighing.setW2Quantity(firstWeighing.getW1Quantity());
			secondWeighing.setW2UoM(firstWeighing.getW1UoM());
			secondWeighing.setW2WeighingPointNo(firstWeighing.getW1WeighingPointNo());
			result = secondWeighing;
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * It creates a firstWeighingData object based on the parameter. Typical used
	 * for reverse directed orders.
	 * @param secondWeighing
	 * @return FirstWeighing
	 */
	private FirstWeighingData createFirstWeighingDataFromSecondWeighing(
			SecondWeighingData secondWeighing) {
		// [[
		FirstWeighingData result = null;
		Object[][] params = {{"secondWeighing", secondWeighing}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createFirstWeighingDataFromSecondWeighing", params);
		}
		// ]]
		try {
			if (secondWeighing != null) {
				FirstWeighingData firstWeighing = new FirstWeighingData();
				firstWeighing.setLdsW1BalanceId(secondWeighing.getLdsW2BalanceId());
				firstWeighing.setLdsW1RecordId(secondWeighing.getLdsW2RecordId());
				firstWeighing.setLdsW1Time(secondWeighing.getLdsW2Time());
				firstWeighing.setLoadingStartTime(secondWeighing.getLoadingEndTime());
				firstWeighing.setW1Quantity(secondWeighing.getW2Quantity());
				firstWeighing.setW1UoM(secondWeighing.getW2UoM());
				firstWeighing.setW1WeighingPointNo(secondWeighing.getW2WeighingPointNo());
				result = firstWeighing;
			}
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	
	/**
	 * Returns an error message string for unprocessed pallet returns. This method returns a
	 * human-readable, user-friendly message that explains that some of the returns could not be
	 * processed, and lists the order numbers of these returns.
	 * @param unprocessedReturns The list of unprocessed pallet returns.
	 * @return The formatted error message, which can be returned as error message to the caller.
	 */
	private String unprocessedReturnErrorMessage(final List<ParsedPalletReturn> unprocessedReturns)
	{
		final StringBuilder errorMsg = new StringBuilder();
		
		errorMsg.append("The request contained pallet returns for orders which did not have ")
		.append("a corresponding loading part. The problematic returns are for the following ")
		.append("orders:");
		
		for (Iterator<ParsedPalletReturn> it = unprocessedReturns.iterator(); it.hasNext();)
		{
			ParsedPalletReturn ret = it.next();
			errorMsg.append(String.format(FIELD_RP_LOGON_ORDER_NO, ret.getOriginalPositionInRequest()));
			errorMsg.append(" = ");
			errorMsg.append(ret.getOrderNo());
			if (it.hasNext()) {
				errorMsg.append(", ");
			}
		}
		return errorMsg.toString();
	}

	/**
	 * Returns the list of pallets which have not been processed by the request.
	 * @param returns The original (input) list of pallet returns.
	 * @param processedOrderNos The order numbers of all those orders for which a
	 * pallet return has been processed.
	 * @return The list of pallet returns that have not been processed, i.e., which are wrongly
	 * present in the request.  See also LOGON-6054.
	 */
	private List<ParsedPalletReturn> unprocessedPalletReturns(
			final List<ParsedPalletReturn> returns,
			final Collection<Long> processedOrderNos) 
	{
		final List<ParsedPalletReturn> unprocessedReturns;
		unprocessedReturns = new LinkedList<>();
		for (ParsedPalletReturn palletReturn : returns)
		{
			if (!processedOrderNos.contains(palletReturn.getOrderNo())) {
				unprocessedReturns.add(palletReturn);
			}
		}
		return unprocessedReturns;
	}
	
	/**
	 * Parses the list of return pallet information from the request by converting to numeric
	 * data types where appropriate. A parsed pallet return entity doesn't mean that the order
	 * exists, it is just to make sure the order number could be parsed as a number,
	 * and that the return UOM is an existing UOM.
	 * @param returnPallets Return pallets part from the end loading request
	 * @return A list of corresponding ParsedPalledReturn objects.
	 */
	private List<ParsedPalletReturn> parsePalletReturns(List<ReturnPallets> returnPallets)
	{
		final List<ParsedPalletReturn> parsedPalletReturns;
		parsedPalletReturns = new LinkedList<>();
		
		int i = 0;
		for (ReturnPallets rawReturnPallet : returnPallets)
		{
			final Long orderNo;
			try {
				orderNo = Long.valueOf(rawReturnPallet.getLogonOrderNo());
			}
			catch (NumberFormatException e)
			{
				throw newApplicationError(String.format(FIELD_RP_LOGON_ORDER_NO +
						" contains an invalid order number %s", i, rawReturnPallet.getLogonOrderNo()));
			}
			
			final String returnedSapMatNo = rawReturnPallet.getMaterialNo();
			final BigDecimal returnedQty = rawReturnPallet.getQuantity();
			final String rawRetUom = rawReturnPallet.getQuantityUom();
			final UomBsEntity returnedUom;
			returnedUom = getUom(rawRetUom, String.format(FIELD_RP_QUANTITY_UOM, i));
			
			parsedPalletReturns.add(
					new ParsedPalletReturn(orderNo, returnedSapMatNo, returnedQty, returnedUom, i));
			
			i++;
		}
		
		return parsedPalletReturns;
	}

}
