/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.beans.LoadedQuantityData;
import com.crh.logon.business.controllers.scheduling.containers.LoadTransferContainer;
import com.crh.logon.business.controllers.scheduling.containers.OrderItemLoadTransferContainer;
import com.crh.logon.business.services.interfaces.OrderService;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public class ReroutingAssignmentUtil {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReroutingAssignmentUtil.class);

	/**
	 * All orders are returned, which have a matching order among the filterOrders for at least
	 * one of the orders of the assignment.
	 * @param byWeight TODO
	 * 
	 * @return set of orderEntities
	 */
	public static Set<OrderEntity> matchingOrdersWithAssignment(List<OrderEntity> ordersOfAssignment,
			Set<OrderEntity> filterOrders, boolean byWeight) {
		// [[
		HashSet<OrderEntity> result = new HashSet<OrderEntity>();
		Object[][] params = { { "ordersOfAssignment", ordersOfAssignment },
				{ "filterOrders", filterOrders } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "matchingOrdersWithAssignment", params);
		}
		// ]]
		try {
			Iterator<OrderEntity> orderInAssignmentIterator = ordersOfAssignment.iterator();
			while (orderInAssignmentIterator.hasNext()) {
				OrderEntity orderInAssignment = orderInAssignmentIterator.next();
				Set<OrderEntity> matchingOrders = matchingOrder(orderInAssignment, filterOrders, false);
				if (!matchingOrders.isEmpty()) {
					result.addAll(matchingOrders);
				}
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
	 * If there is at least one order in orderEntities, which matches the order by material,
	 * then one of them is returned. Otherwise empty Set is returned.
	 * 
	 * @param order
	 * @param orderEntities
	 * @param byWeight Check also weight constraints when determining order item matches
	 * @return set of orderEntities. empty, if no matching order found. NEVER returns null.
	 */
	private static Set<OrderEntity> matchingOrder(OrderEntity order, Set<OrderEntity> orderEntities, 
			boolean byWeight) {
		// [[
		Set<OrderEntity> result = null;
		Object[][] params = {{"order", order},
							{"orderEntities", orderEntities},
							{"byWeight", byWeight + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "matchingOrder", params);
		}
		// ]]
		try {
			final Set<OrderEntity> matchingOrders = new HashSet<OrderEntity>();
			boolean filterOrderMatches;
			String deliveryPlantCd = (order.getShippingpoint() == null ? null : order.getShippingpoint().getPlant().getPlantCd());
			for (OrderEntity filterOrder : orderEntities) {
				filterOrderMatches = (Constant.DELIVERY_TYPE_ROAD_DIRECT == order.getDeliveryType().getDeliveryTypeCd());
				
				String filtDeliveryPlantCd = (filterOrder.getShippingpoint() == null ? null : filterOrder.getShippingpoint().getPlant().getPlantCd());
				filterOrderMatches &= (deliveryPlantCd == null && filtDeliveryPlantCd == null)
						|| deliveryPlantCd.equals(filtDeliveryPlantCd);
				
				filterOrderMatches &= (matchingOrderItems(order.getOrderItems(),
						filterOrder.getOrderItems(), byWeight) == Constant.REROUTING_ORDERITEMS_MATCHING);
			
				if (filterOrderMatches) {
					matchingOrders.add(filterOrder);
				}
			}
			return result = matchingOrders;
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
	 * Orders from the first list are mapped to the orders from the second list, if they
	 * are considered to be matching.
	 * 
	 * @param oldOrders
	 * @param newOrders
	 * @param isTour
	 * @return map of orders
	 */
	public static Map<OrderEntity, OrderEntity> matchingOrderCouples(List<OrderEntity> oldOrders, 
			List<OrderEntity> newOrders, boolean isTour) {
		// [[
		Map<OrderEntity, OrderEntity> result = new HashMap<OrderEntity, OrderEntity>();
		Object[][] params = { { "oldOrders", oldOrders }, { "newOrders", newOrders } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "matchingOrderCouples", params);
		}
		// ]]
		try {
			final Map<OrderEntity, OrderEntity> couples = new HashMap<OrderEntity, OrderEntity>();
			/**
			 * Contains the new orders which aren't paired yet to old orders.
			 * At the beginning it containes all the new orders.
			 */
			final List<OrderEntity> notPairedNewOrders = new ArrayList<OrderEntity>(newOrders);
			for (OrderEntity oldOrder : oldOrders) {
				for (OrderEntity newOrder : newOrders) {
					if (!notPairedNewOrders.contains(newOrder)) {
						// This order is already paired before to another oldOrder
						continue;
					}
					final Set<OrderEntity> newOrderSet = new HashSet<OrderEntity>();
					newOrderSet.add(newOrder);
					if (!matchingOrder(oldOrder, newOrderSet, isTour).isEmpty()) {
						couples.put(oldOrder, newOrder);
						// In order not to pair this newOrder with any other oldOrders as well
						notPairedNewOrders.remove(newOrder);
						// We already found a match for the old order we swith to the new old order
						break;
					}
				}
			}
			return result = couples;
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
	 * An order residing in an assignment can be rerouted, if it is in delivered state,
	 * and it has 'road direct' delivery type.
	 * */
	public static boolean isReroutableOrder(OrderEntity order) {
		// [[
		boolean result = true;
		Object[][] params = { { "order", order }};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "isReroutableOrder",
					params);
		}
		// ]]
		try {
			if (Constant.ORDER_STATUS_DELIVERED != order.getOrderStatus().getOrderStatusCd()
					|| Constant.DELIVERY_TYPE_ROAD_DIRECT != order.getDeliveryType().getDeliveryTypeCd()) {
				return result = false;
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
	 * Returns a <strong>new</strong> list consisting of all but the order items which are services
	 * with 0 ordered quantity.
	 * @param orderItems
	 * @return filtered list of order items
	 */
	private static List<OrderItemEntity> getOrderItemsWithoutZeroQtyServices(List<OrderItemEntity> orderItems)
	{
		// [[
		List<OrderItemEntity> result = null;
		Object[][] params = {{"orderItems", orderItems}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "getOrderItemsWithoutZeroQtyServices", params);
		}
		// ]]
		try {
			final List<OrderItemEntity> filteredItems = new ArrayList<OrderItemEntity>();
			
			for (OrderItemEntity orderItem : orderItems) {
				if (!isOrderItemServiceWithZeroQty(orderItem)) {
					filteredItems.add(orderItem);
				}
			}
			
			return result = filteredItems;
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
	 * Checks if the OrderItem has service material and has zero ordered quantity.
	 * These orderitems should be disregarded when collecting matching orders for rerouting.
	 */
	public static boolean isOrderItemServiceWithZeroQty(OrderItemEntity orderItem) {
		// [[
		boolean result = false;
		Object[][] params = {{"orderItem", orderItem}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "isOrderItemServiceWithZeroQty", params);
		}
		// ]]
		try {
			if (orderItem == null) {
				return result = false;
			}
			else {
				return result = LogonPackageHandlingUtil.isService(orderItem)
						&& orderItem.getOrderedQuantity() != null
						&& orderItem.getOrderedQuantity().equals(BigDecimal.ZERO);
			}
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
	 * Compares 2 sets of orderItems by structure (number of items), sapMaterialId, 
	 * production plant and optionally weight data. Returns true, if every orderItem in 
	 * the list has its couple in the other list (1-1). The order of orderItems1 and 
	 * orderItems2 can be important, if weight matching is also applied: in bulk case, 
	 * ordered quantity can be less than loaded quantity (and it's considered to be 
	 * matching).
	 * 
	 * @param orderItems1 - belongs to the assignment (loaded quantity)
	 * @param orderItems2 - belongs to the new order (ordered quantity)
	 * @param byWeight - performs comparing also by weight, if this flag is true.
	 * @return errorCode
	 */
	private static int matchingOrderItems(List<OrderItemEntity> orderItems1,
			List<OrderItemEntity> orderItems2, boolean byWeight) {
		// [[
		int result = Constant.REROUTING_ORDERITEMS_MATCHING;
		Object[][] params = { { "orderItems1", orderItems1 },
				{ "orderItems2", orderItems2 },
				{ "byWeight", byWeight }};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "matchingOrderItems",
					params);
		}
		// ]]
		try {
			if (orderItems1 == null || orderItems2 == null) {
				LOG.error("orderItems1: " + orderItems1 + "; orderItems2: " + orderItems2);
				throw new LogonException("orderItem list shouldn't be null",
						Reason.RES_ERROR_IMPL);
			}
			
			// We should not consider sevice material order item with zero ordered quantity.
			final List<OrderItemEntity> filteredItems1 = getOrderItemsWithoutZeroQtyServices(orderItems1);
			final List<OrderItemEntity> filteredItems2 = getOrderItemsWithoutZeroQtyServices(orderItems2);
			
			if (filteredItems1.size() != filteredItems2.size()) {
				return result = Constant.REROUTING_ORDERITEMS_NOMATCHING_STRUCTURE_DIFFERENT;
			}
			
			Collections.sort(filteredItems1, new OrderItemMaterialLoadedWeightComparator());
			Collections.sort(filteredItems2, new OrderItemMaterialOrderedWeightComparator());
			
			// Collect all SAP materials from both order item lists to compare later on
			final List<SapMaterialEntity> materials1 = new ArrayList<SapMaterialEntity>();
			final List<SapMaterialEntity> materials2 = new ArrayList<SapMaterialEntity>();
			
			final Iterator<OrderItemEntity> it1 = filteredItems1.iterator();
			final Iterator<OrderItemEntity> it2 = filteredItems2.iterator();
			// Loop through all order items, even if some does not have pair here
			while (it1.hasNext() || it2.hasNext()) {
				final OrderItemEntity oi1 = it1.hasNext() ? it1.next() : null;
				final OrderItemEntity oi2 = it2.hasNext() ? it2.next() : null;
				
				// We should exclude sevice material order item with zero ordered quantity.
				if (oi1 != null && !isOrderItemServiceWithZeroQty(oi1)) {
					materials1.add(oi1.getSapMaterial());
				}
				if (oi2 != null && !isOrderItemServiceWithZeroQty(oi2)) {
					materials2.add(oi2.getSapMaterial());
				}
				
				if (oi1 != null && oi2 != null) {
					if (!oi1.getProductionPlant().getPlantCd().equals(oi2.getProductionPlant().getPlantCd())) {
						return result = Constant.REROUTING_ORDERITEMS_NOMATCHING_PRODPLANT_DIFFERENT;
					}
					if (byWeight) {
						if (LogonPackageHandlingUtil.isBag(oi1)) {
							float loadedWeight = getLoadedBaseUomWeight(oi1);
							float orderedweight = getOrderedBaseUomWeight(oi2);
							if (loadedWeight < orderedweight) {
								return result = Constant.REROUTING_ORDERITEMS_NOMATCHING_WEIGHT_LESS_IN_LOAD;
							}
							else if (loadedWeight > orderedweight) {
								return result = Constant.REROUTING_ORDERITEMS_NOMATCHING_WEIGHT_MORE_IN_LOAD;
							}
						}
						else if (LogonPackageHandlingUtil.isBulkPowder(oi1) || LogonPackageHandlingUtil.isBulkDumped(oi1)) {
							float loadedWeight = getLoadedBaseUomWeight(oi1);
							float orderedWeight = getOrderedBaseUomWeight(oi2);
							
							//orderitem1 is still on the truck (load), orderitem2 belongs to the new order (ordered quantity)
							if (loadedWeight < orderedWeight) {
								return result = Constant.REROUTING_ORDERITEMS_NOMATCHING_WEIGHT_LESS_IN_LOAD;
							}
						}
					}
				}
			}
			
			// Check SAP materials here (already excluded service materials with zero quantity)
			if (materials1.size() != materials2.size()) {
				return result = Constant.REROUTING_ORDERITEMS_NOMATCHING_STRUCTURE_DIFFERENT;
			}
			for (int i = 0; i < materials1.size(); i++) {
				final SapMaterialEntity material1 = materials1.get(i);
				final SapMaterialEntity material2 = materials2.get(i);
				
				if (material1.getSapMaterialId() != material2.getSapMaterialId()) {
					return result = Constant.REROUTING_ORDERITEMS_NOMATCHING_MATERIAL_DIFFERENT;
				}
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


	public static int validateOldAndNewOrders(List<OrderEntity> oldOrders,
			List<OrderEntity> newOrders, boolean isTour)
	{
		if (newOrders == null || oldOrders == null || oldOrders.size() == 0 || newOrders.size() == 0) {
			int oldSize = (oldOrders == null ? 0 : oldOrders.size());
			int newSize = (newOrders == null ? 0 : newOrders.size());
			throw new LogonException("The number of old orders and new order must be the same "
					+  "and cannot be 0. old: " + oldSize + "; new: " + newSize, Reason.RES_ERROR_IMPL);
		}
		
		if (oldOrders.size() != newOrders.size()) {
			if (isTour) {
				return Constant.REROUTING_ORDERS_NOMATCHING_IN_TOUR;
			}
			else {
				return Constant.REROUTING_UNSUCCESSFUL_NOMATCHING;
			}
		}
		
		return Constant.REROUTING_SUCCESSFUL;
	}


	/**
	 * Compares the given order lists by orderitem structure and orderitem matching.
	 * Returns an error/success code.
	 * @param orderMapping
	 * @param oldOrdersSize
	 * @param isTour
	 * @return
	 */
	public static int matchingOrders(Map<OrderEntity, OrderEntity> orderMapping, int oldOrdersSize, 
			boolean isTour) {
		// [[
		int result = 0;
		Object[][] params = {{"orderMapping", orderMapping},
							{"oldOrdersSize", oldOrdersSize + ""},
							{"isTour", isTour + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "matchingOrders", params);
		}
		// ]]
		try {
			//check: every order in the oldOrders list has its couple from the newOrders list?
			if (orderMapping.size() != oldOrdersSize) {
				if (isTour) {
					return result = Constant.REROUTING_ORDERS_NOMATCHING_IN_TOUR;
				}
				else {
					return result = Constant.REROUTING_UNSUCCESSFUL_NOMATCHING;
				}
			}
			else {
				OrderEntity newOrder;
				for (OrderEntity oldOrder : orderMapping.keySet()) {
					newOrder = orderMapping.get(oldOrder);
					result = matchingOrderItems(oldOrder.getOrderItems(), newOrder.getOrderItems(), isTour);
					if (result != Constant.REROUTING_ORDERITEMS_MATCHING) {
						return result;
					}
				}
			}
			
			return result = Constant.REROUTING_ORDERITEMS_MATCHING;
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
	 * If the 2 lists of orderItems are matching for rerouting (same structure, same material,
	 * same production plant, same quantities for bag orders), then it returns a map containing 
	 * the couples.
	 * Returns empty list, if the lists are not matching.
	 * 
	 * @param originalOrderItems
	 * @param replacementOrderItems
	 * @param isTour
	 * @return never returns null
	 */
	public static Map<OrderItemEntity, OrderItemEntity> getMatchingOrderItemCouples(
			List<OrderItemEntity> originalOrderItems, List<OrderItemEntity> replacementOrderItems,
			boolean isTour) {
		// [[
		Map<OrderItemEntity, OrderItemEntity> result = null;
		Object[][] params = { { "originalOrderItems", originalOrderItems },
				{ "replacementOrderItems", replacementOrderItems }, { "isTour", isTour + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "matchingOrderItemsCouples", params);
		}
		// ]]
		try {
			final List<OrderItemEntity> originalOrderItemsFiltered = getOrderItemsWithoutZeroQtyServices(originalOrderItems);
			final List<OrderItemEntity> replacementOrderItemsFiltered = getOrderItemsWithoutZeroQtyServices(replacementOrderItems);
			
			Collections.sort(originalOrderItemsFiltered, new OrderItemMaterialLoadedWeightComparator());
			Collections.sort(replacementOrderItemsFiltered, new OrderItemMaterialOrderedWeightComparator());
			
			Iterator<OrderItemEntity> originalOrderItemsFilteredIterator = originalOrderItemsFiltered.iterator();
			Iterator<OrderItemEntity> replacementOrderItemsFilteredIterator = replacementOrderItemsFiltered.iterator();
			result = new HashMap<OrderItemEntity, OrderItemEntity>();
			while (originalOrderItemsFilteredIterator.hasNext() && replacementOrderItemsFilteredIterator.hasNext()) {
				OrderItemEntity originalOrderItem = originalOrderItemsFilteredIterator.next();
				OrderItemEntity replacementOrderItem = replacementOrderItemsFilteredIterator.next();
				result.put(originalOrderItem, replacementOrderItem);
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

	public static List<AssignmentEntity> createAssignment(
			OrderServiceLocalInterface orderService, Map<OrderEntity, OrderEntity> orderMapping, Long truckId, Date startTime, TimeZone timeZone) {
		// [[
		List<AssignmentEntity> result = new ArrayList<AssignmentEntity>();
		Object[][] params = {{"os", orderService},
				{"orderMapping", orderMapping},
				{"truckId", truckId},
				{"startTime", startTime},
				{"timeZone", timeZone}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "createAssignment", params);
		}
		// ]]
		try {
			OrderEntity newOrder;
			int loadingTime = 0;
			int transportDurationMin = 0;
			int plannedUnloadingTimeMin = 0;
			for (OrderEntity oldOrder : orderMapping.keySet()) {
				//loadingTime value would be applied only for external orders, but here we don't have external orders
				loadingTime = 0;
				transportDurationMin = (oldOrder.getTransportDurationMin() == null ? 0 : oldOrder.getTransportDurationMin().intValue());
				plannedUnloadingTimeMin = (oldOrder.getPlannedUnloadingTimeMin() == null ? 0 : oldOrder.getPlannedUnloadingTimeMin().intValue());
				newOrder = orderMapping.get(oldOrder);
				
				AssignmentEntity assignment = orderService.createAssignmentForTruckWithOrderDispatched(truckId,
																							 newOrder,
																							 startTime,
																							 timeZone,
																							 loadingTime,
																							 transportDurationMin,
																							 plannedUnloadingTimeMin);
				
				result.add(assignment);
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
	
	private static void copyAssignmentFields(AssignmentEntity oldAssignment, AssignmentEntity newAssignment) {
		if(oldAssignment == null || newAssignment == null) {
			return;
		}
		
		newAssignment.setLdsAttorneyName(oldAssignment.getLdsAttorneyName());
		newAssignment.setLdsAttorneyStart(oldAssignment.getLdsAttorneyStart());
		newAssignment.setLdsCarrierCrm(oldAssignment.getLdsCarrierCrm());
		newAssignment.setLdsDispatcherId(oldAssignment.getLdsDispatcherId());
		newAssignment.setLdsDispatcherName(oldAssignment.getLdsDispatcherName());
		newAssignment.setLdsDispatcherTel(oldAssignment.getLdsDispatcherTel());
		newAssignment.setLdsDriverCardNo(oldAssignment.getLdsDriverCardNo());
		newAssignment.setLdsDriverId(oldAssignment.getLdsDriverId());
		newAssignment.setLdsDriverName(oldAssignment.getLdsDriverName());
		newAssignment.setLdsSealNumbers(oldAssignment.getLdsSealNumbers());
		
	}
				
	
	public static AssignmentEntity mergeAssignments(OrderServiceLocalInterface orderServiceLocalInterface, List<AssignmentEntity> newAssignments, AssignmentEntity oldAssignment) {
		// [[
		AssignmentEntity result = null;
		Object[][] params = {{"orderServiceLocalInterface", orderServiceLocalInterface},
				{"newAssignments", newAssignments}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "mergeAssignments", params);
		}
		// ]]
		try {
			if (newAssignments.size() == 1) {
				result = newAssignments.get(0);
				copyAssignmentFields(oldAssignment, result);
			}
			else if (newAssignments.size() > 1) {
				//assignment[0] is the target assignment: all the other assignments are "dragged" into this one
				Long targetAssignmentId = newAssignments.get(0).getAssignmentId();
				Long toBeMergedAssignmentId;
				//dragging assignment[1]..assignment[n] into the first assignment
				for (int i = 1; i < newAssignments.size(); i++) {
					toBeMergedAssignmentId = newAssignments.get(i).getAssignmentId();
					result = orderServiceLocalInterface.mergeAssignments(toBeMergedAssignmentId, targetAssignmentId, null);
					copyAssignmentFields(oldAssignment, result);
				}
			}
			else {
				throw new LogonException("no assignment received", Reason.RES_ERROR_IMPL);
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
	 * @param orderItemLoadTransfers
	 * @return map of loading data for each orderitem
	 */
	@AdnTrace
	public static Map<Long, LoadedQuantityData> getLoadedQuantityDataMap (
			List<OrderItemLoadTransferContainer> orderItemLoadTransfers) {
		Map<Long, LoadedQuantityData> loadedQuantityDataMap = new HashMap<Long, LoadedQuantityData>();
		
		for (OrderItemLoadTransferContainer orderItemLoadTransfer : orderItemLoadTransfers) {
			loadedQuantityDataMap.put(
					orderItemLoadTransfer.getTargetOrderItem().getOrderItemId(),
					orderItemLoadTransfer.getLoadTransferSource().getOldOrderItemLoadQuantityData());
		}
		
		return loadedQuantityDataMap;
	}

	/**
	 * @param orderMapping
	 * @param isTour
	 * @return the new orderitems belonging to the new assignment we are rerouting to,
	 *  paired with the old load data that will be transfered to them
	 */
	public static List<OrderItemLoadTransferContainer> getOrderItemLoadTransfers(
			Map<OrderEntity, OrderEntity> orderMapping, boolean isTour)
	{
		// [[
		List<OrderItemLoadTransferContainer> result = null;
		Object[][] params = {{"orderMapping", orderMapping},
							{"isTour", isTour + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "getOrderItemMap", params);
		}
		// ]]
		try {
			final List<OrderItemLoadTransferContainer> allOrderItemLoadTransfers = 
					new ArrayList<OrderItemLoadTransferContainer>();
			
			for (final OrderEntity oldOrder : orderMapping.keySet())
			{
				final OrderEntity newOrder = orderMapping.get(oldOrder);
				
				List<OrderItemEntity> oldOrderItems = oldOrder.getOrderItems();
				List<OrderItemEntity> newOrderItems = newOrder.getOrderItems();
				Map<OrderItemEntity, OrderItemEntity> itemMapping = 
						getMatchingOrderItemCouples(oldOrderItems, newOrderItems, 
								isTour);
				for (final OrderItemEntity oldItem : itemMapping.keySet())
				{
					final OrderItemEntity newItem = itemMapping.get(oldItem);
					final LoadTransferContainer loadTransfer = new LoadTransferContainer(oldItem.getLoadedQuantity(), 
							oldItem.getLoadedUom().getUomCd(), oldItem.getLoad());
					
					final OrderItemLoadTransferContainer orderItemLoadTransfer = 
							new OrderItemLoadTransferContainer(newItem, loadTransfer);

					allOrderItemLoadTransfers.add(orderItemLoadTransfer);
				}
			}
			
			return result = allOrderItemLoadTransfers;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}


	public static int copyOrderItemWeightData(OrderService orderService, Map<OrderEntity, OrderEntity> orderMapping, boolean isTour) {
		// [[
		int result = 0;
		Object[][] params = {{"orderService", orderService},
							{"orderMapping", orderMapping},
							{"isTour", isTour + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "copyOrderItemWeightData", params);
		}
		// ]]
		try {
			for (OrderEntity oldOrder : orderMapping.keySet()) {
				OrderEntity newOrder = orderMapping.get(oldOrder);
				
				List<OrderItemEntity> oldOrderItems = oldOrder.getOrderItems();
				List<OrderItemEntity> newOrderItems = newOrder.getOrderItems();
				Map<OrderItemEntity, OrderItemEntity> orderItemMapping = getMatchingOrderItemCouples(oldOrderItems, newOrderItems, isTour);
				Iterator<OrderItemEntity> orderItemIterator = orderItemMapping.keySet().iterator();
				while (orderItemIterator.hasNext()) {
					OrderItemEntity oldOrderItem = orderItemIterator.next();
					OrderItemEntity newOrderItem = orderItemMapping.get(oldOrderItem);
					
					newOrderItem.setLoadedQuantity(oldOrderItem.getLoadedQuantity());
					newOrderItem.setLoadedUom(oldOrderItem.getLoadedUom());
					orderService.updateOrderItemNumeratorAndDenominator(newOrderItem, Constant.ORDER_ITEM_NUM_DENOM_TYPE_LR);
				}
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
	 * Compares orderItems by sapMaterialId. 
	 */
	public static class OrderItemMaterialComparator implements Comparator<OrderItemEntity> {
		public int compare(OrderItemEntity oi1, OrderItemEntity oi2) {
			int materialDiff = oi1.getSapMaterial().getSapMaterialId().compareTo(oi2.getSapMaterial().getSapMaterialId());
			return materialDiff;
		}
	}
	
	/**
	 * Compares orderItems by ordered weight. 
	 */
	public static class OrderItemMaterialOrderedWeightComparator implements Comparator<OrderItemEntity> {
		public int compare(OrderItemEntity oi1, OrderItemEntity oi2) {
			int materialDiff = oi1.getSapMaterial().getSapMaterialId().compareTo(oi2.getSapMaterial().getSapMaterialId());
			if (materialDiff == 0) {
				Float oi1BaseUomWeight = getOrderedBaseUomWeight(oi1);
				Float oi2BaseUomWeight = getOrderedBaseUomWeight(oi2);
				return oi1BaseUomWeight.compareTo(oi2BaseUomWeight);
			}
			return materialDiff;
		}
	}
	
	/**
	 * Compares orderItems by loaded weight. 
	 */
	public static class OrderItemMaterialLoadedWeightComparator implements Comparator<OrderItemEntity> {
		public int compare(OrderItemEntity oi1, OrderItemEntity oi2) {
			int materialDiff = oi1.getSapMaterial().getSapMaterialId().compareTo(oi2.getSapMaterial().getSapMaterialId());
			if (materialDiff == 0) {
				Float oi1BaseUomWeight = getLoadedBaseUomWeight(oi1);
				Float oi2BaseUomWeight = getLoadedBaseUomWeight(oi2);
				return oi1BaseUomWeight.compareTo(oi2BaseUomWeight);
			}
			return materialDiff;
		}
	}
	
	private static float getOrderedBaseUomWeight(OrderItemEntity orderItem) {
		// [[
		float result = 0F;
		Object[][] params = { { "orderItem", orderItem }};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "getOrderedBaseUomWeight",
					params);
		}
		// ]]
		try {
			Float f = (orderItem.getOrderedQuantity().longValue() + 0F) * orderItem.getOrderedUomNumerator() / orderItem.getOrderedUomDenominator();
			return result = f;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
	
	private static float getLoadedBaseUomWeight(OrderItemEntity orderItem) {
		// [[
		float result = 0F;
		Object[][] params = { { "orderItem", orderItem }};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ReroutingAssignmentUtil.class, "getLoadedBaseUomWeight",
					params);
		}
		// ]]
		try {
			Float f = (orderItem.getLoadedQuantity().longValue() + 0F) * orderItem.getLrUomNumerator() / orderItem.getLrUomDenominator();
			return result = f;
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
