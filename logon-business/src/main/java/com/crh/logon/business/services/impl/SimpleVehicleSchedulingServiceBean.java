/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.beans.LoadedQuantityData;
import com.crh.logon.business.kafka.KafkaPublisherService;
import com.crh.logon.business.services.beans.SimpleVehicleAssignmentInfo;
import com.crh.logon.business.services.beans.SimpleVehicleAssignmentPart;
import com.crh.logon.business.services.beans.SimpleVehicleLoadableItem;
import com.crh.logon.business.services.beans.SimpleVehicleWeighingData;
import com.crh.logon.business.services.beans.UnallocatedOrderData;
import com.crh.logon.business.services.dispatchers.SimpleVehicleSchedulingServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ConfigurableSequencesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PoolAdminServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.SimpleVehicleSchedulingServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentStatusUtil;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.ConfSeqLock;
import com.crh.logon.business.services.utils.DispoCarrierWrapper;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.EventSource;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.refdata.AssignStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignTrStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.BizeventTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.views.CommExecCarrierPfEntity;
import com.crh.logon.persistence.entities.views.SimpleVehicleAssgEntity;
import com.crh.logon.persistence.entities.views.SimpvehLoadOrderItemEntity;
import com.crh.logon.persistence.entities.views.SimpvehLoadingpointEntity;
import com.crh.logon.persistence.entities.views.SimpvehOrdItemMatEntity;
import com.crh.logon.persistence.entities.views.SimpvehOrdVhcEntity;
import com.crh.logon.persistence.entities.views.SimpvehUnalcOrdEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * This service implements all methods required by the "Simple Vehicle Scheduling" Screen.
 * The main workflow supported is the following:
 * <ol>
 * 	<li>Allocate one or several orders to a simple vehicle.</li>
 * 	<li>Load the simple vehicle.</li>
 * 	<li>Unload the orders. (Open question: do they have to be unloaded in sequence
 * 	or can the unload order be arbitrary?)</li>
 * </ol>
 * <p>
 * See SDS LRQ 108.0 Managing Ship & Block-Train Logistics, Chapter 6.
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class SimpleVehicleSchedulingServiceBean extends SimpleVehicleSchedulingServiceDispatcher implements SimpleVehicleSchedulingServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SimpleVehicleSchedulingServiceBean.class);

	private static final BigDecimal NO_TARE_WEIGHT = null;

	private static final BigDecimal NO_GROSS_WEIGHT = null;

	private static final Date NO_LOADING_START_TIME = null;

	private static final Date NO_LOADING_END_TIME = null;
	
	@EJB private ConfigurableSequencesServiceLocalInterface sequenceService;
	@EJB private SimpleVehicleSchedulingServiceLocalInterface simpleVehicleService;
	@EJB private LoadingServiceLocalInterface loadingService;
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private PoolAdminServiceLocalInterface poolAdminService;
	@EJB private DispoServiceLocalInterface dispoService;
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private DispokarteServiceLocalInterface dispokarteService;
	@EJB private KafkaPublisherService kafkaPublisherService;

	
	private static Set<Long> activeSimpleVehicleAssignmentStatusSet = new HashSet<Long>();
	
	static {		
		final Long[] activeSimpleVehicleAssignmentStatuses = {
				AssignmentStatusUtil.ASSIGNMENT_STATUS_NEW.getAssignStatusCd(),
				AssignmentStatusUtil.ASSIGNMENT_STATUS_LOADING.getAssignStatusCd(),				
				AssignmentStatusUtil.ASSIGNMENT_STATUS_SHIPPING.getAssignStatusCd(),
		};

		activeSimpleVehicleAssignmentStatusSet.addAll(new HashSet<Long>(Arrays.asList(activeSimpleVehicleAssignmentStatuses)));
	}
		
	
	private static final class UnallocatedOrderComparator implements Comparator<UnallocatedOrderData>
	{
		/**
		 * Compare one UnallocatedOrderData object to another one.
		 * This is to implement the sorting defined for the list of unallocated orders
		 * in the SimpleVehicleSchedulingScreen. See SDS for LRQ 108.0 "Managing Ship & Block-Train Logistics",
		 * Section 6.4.3 "Un-allocated orders section".
		 * @param first an UnallocatedOrderData object
		 * @param other another UnallocatedOrderData object
		 * @return 1 if the first object is greater than (i.e. should come after)
		 * the other object; -1 if the first object is less than (i.e., should come before)
		 * the other object; 0 if they are equal.
		 */
		public int compare(UnallocatedOrderData first, UnallocatedOrderData other)
		{
			final Date firstDeliveryFrom = first.getOrder().getDeliveryFromDat();
			final Date otherDeliveryFrom = other.getOrder().getDeliveryFromDat();
			
			if (!firstDeliveryFrom.equals(otherDeliveryFrom)) {
				return firstDeliveryFrom.compareTo(otherDeliveryFrom);
			}
			else {
				/* Use order ID to have a deterministic ordering
				 * (assuming there are only distinct orders)
				 */
				return first.getOrder().getOrderId().compareTo(other.getOrder().getOrderId());
			}
		}
		
	}
	
	/**
	 * This comparator implements the sort order of the list of simple vehicles.
	 * See SDS LRQ 108.0 "Managing Ship & Block Train Logistics", Section 6.4.4,
	 * "Vehicle List / Allocated Orders Section".
	 * <p>
	 * According to the specification, the vehicle list is ordered ascending by
	 * <ul>
	 * 	<li>carrier name</li>
	 * 	<li>priority</li>
	 * 	<li>vehicle name</li>
	 * <ul>
	 * @author AdNovum Informatik AG
	 */
	private static final class VehicleListComparator implements Comparator<SimpleVehicleAssignmentInfo>
	{

		public int compare(SimpleVehicleAssignmentInfo o1, SimpleVehicleAssignmentInfo o2)
		{
			// First by carrier name
			if (!o1.getCarrierName().equals(o2.getCarrierName())) {
				return o1.getCarrierName().compareTo(o2.getCarrierName());
			}
			// Then by priority
			else if (!o1.getPriority().equals(o2.getPriority())) {
				return o1.getPriority().compareTo(o2.getPriority());
			}
			// Then by vehicle name
			else if (!o1.getVehicleName().equals(o2.getVehicleName())){
				return o1.getVehicleName().compareTo(o2.getVehicleName());
			}
			/* if everything else is equal, we use the vehicle id */
			else {
				return o1.getVehicleId().compareTo(o2.getVehicleId());
			}
		}
		
	}
	
	/**
	 * The minimum value of a simple vehicle's priority. See DB comment of
	 * tlgnc_simple_vehicle.priority for proper range.
	 */
	final private int VEHICLE_PRIO_MIN = 0;
	
	/**
	 * The maximum value of a simple vehicle's priority. See DB comment of
	 * tlgnc_simple_vehicle.priority for proper range.
	 */
	final private int VEHICLE_PRIO_MAX = 99;
	
	/**
	 * Returns the list of pools. This method returns all pools, taking
	 * into account the current user's authorization. 
	 * @return The list of all pools in Logon for which the current user
	 * is authorized.
	 */
	public Collection<PoolTxEntity> getPools(Locale locale)
	{
		// [[
		Collection<PoolTxEntity> result = null;
		Object[][] params = {{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPools", params);
		}
		// ]]
		try {
			return result = poolAdminService.fetchSimpleVehiclePoolTxEntities(locale, false);
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
	 * Returns all carriers for a given pool. This method returns
	 * all carriers for the specified pool ID, taking into account
	 * the current user's authorization.
	 * @param poolId ID of an existing pool whose carriers should be returned
	 * @return The list of carriers associated to the specified pool.
	 * @throws LogonException if the pool ID is invalid
	 */
	public Collection<DispoCarrierWrapper> getCarriers(Long poolId)
	throws LogonException
	{
		// [[
		Collection<DispoCarrierWrapper> result = null;
		Object[][] params = {{"poolId", poolId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCarriers", params);
		}
		// ]]
		try {
			return result = dispoService.fetchCarriersForPool(poolId);
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
	 * Loads all unallocated orders for the given pool. This method returns a map
	 * whose keys are the unallocated orders (one key for each line in the unallocated
	 * orders list in the bottom half of the simple vehicle scheduling screen), and
	 * for each unallocated order the value is a list of the simple vehicles to which the
	 * order can be assigned. This list corresponds to the dropdown entries next to each
	 * order.
	 * @param poolId The ID of an existing pool.
	 * @param carrierIds This set of carrier IDs is used to filter the list of vehicles
	 * to which an order can be assigned. Only vehicles belonging to one of the
	 * carriers in this list will be returned.
	 * @return A set of orders, and for each order a list of the vehicles to which the order
	 * can be assigned.
	 * @throws LogonException if the specified pool is invalid.
	 */
	public Collection<UnallocatedOrderData> getUnallocatedOrders(Long poolId, Set<Long> carrierIds, Locale locale)
	{
		// [[
		Collection<UnallocatedOrderData> result = null;
		Object[][] params = {{"poolId", poolId},
							{"carrierIds", carrierIds},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getUnallocatedOrders", params);
		}
		// ]]
		try {
			final List<SimpvehUnalcOrdEntity> orders = fetchOrdersFromView(poolId, locale.getLanguage());
			final List<SimpvehOrdVhcEntity> vehicles = fetchOrderVehiclesFromView(carrierIds);
			
			return result = makeUnallocatedOrderDataList(orders, vehicles);
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
	 * Returns all simple vehicles along with the info about assigned orders.
	 * @param carrierIds A set of valid carrier IDs
	 * @return The list of all simple vehicles that belong to one of the given carriers.
	 * @throws LogonException If any of the specified carrier IDs is invalid.
	 */
	public Collection<SimpleVehicleAssignmentInfo> getVehicleAssignmentData(Set<Long> carrierIds, Locale locale)
	throws LogonException
	{
		// [[
		Collection<SimpleVehicleAssignmentInfo> result = null;
		Object[][] params = {{"carrierIds", carrierIds},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getVehicleAssignmentData", params);
		}
		// ]]
		try {
			final List<SimpleVehicleAssgEntity> viewResults = fetchVehicleDataFromView(carrierIds,
					locale);
			
			return result = createSimpleVehicleAssgInfos(viewResults);
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
	 * Loads all necessary information to display the "loading" popup in the
	 * simple vehicle scheduling screen.
	 * @param vehicleId The vehicle to be loaded
	 * @return A list of loadable items and their material descriptions,
	 * and for each item the list of possible loading points.
	 * @throws LogonException if the simple vehicle doesn't exist.
	 */
	public Collection<SimpleVehicleLoadableItem> getLoadableItems(Long vehicleId, Locale locale)
	throws LogonException
	{
		// [[
		Collection<SimpleVehicleLoadableItem> result = null;
		Object[][] params = {{"vehicleId", vehicleId},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLoadableItems", params);
		}
		// ]]
		try {
			final ReadOnlyDao<SimpvehLoadOrderItemEntity> dao;
			dao = getDaoProvider().getSimpvehLoadOrderItemEntityDao();
			
			if (getDaoProvider().getSimpleVehicleEntityDao().findById(vehicleId) == null) {
				throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_VEHICLE_NOT_FOUND);
			}
			
			final Filter<SimpvehLoadOrderItemEntity> f = dao.createFilter();
			f.addExpression(Restrictions.eq(SimpvehLoadOrderItemEntity.ATTR_SIMPLE_VEHICLE_ID, vehicleId));
			f.addExpression(Restrictions.eq(SimpvehLoadOrderItemEntity.ATTR_LANG_CD, locale.getLanguage()));
			
			// Only orders beyond dispatched (at least released) and before delivered
			// This is to avoid showing orders that are already unloaded.
			f.addExpression(Restrictions.in(SimpvehLoadOrderItemEntity.ATTR_ORDER_STATUS_CD, LogonStatusUtils.getBeyondDispatched()));
			f.addExpression(Restrictions.in(SimpvehLoadOrderItemEntity.ATTR_ORDER_STATUS_CD, LogonStatusUtils.getBeforeDelivered()));
			
			final List<SimpvehLoadOrderItemEntity> loadOrderItems = dao.find(f);
			final Set<Long> loadOrderItemIds = extractLoadOrderItemIds(loadOrderItems);
			final List<SimpvehLoadingpointEntity> loadingPoints;
			if (!loadOrderItemIds.isEmpty()) {
				loadingPoints = fetchLoadingpointsForLoadOrderItems(loadOrderItemIds);
			}
			else {
				loadingPoints = new LinkedList<SimpvehLoadingpointEntity>();
			}
			return result = combineAsLoadableItemList(loadOrderItems, loadingPoints);
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
	 * Returns with the order items information for the initialization of the Simple Vehicle
	 * scheduling screen's unload popup, i.e., the order items that can be unloaded.
	 * @param orderId ID of the order for which to show the unloadable items.
	 */
	public List<SimpvehOrdItemMatEntity> getUnloadableItems(Long orderId, Locale locale) {
		// [[
		List<SimpvehOrdItemMatEntity> result = null;
		Object[][] params = {{"orderId", orderId},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getUnloadableItems", params);
		}
		// ]]
		try {
			final String langCd = locale.getLanguage();
			List<SimpvehOrdItemMatEntity> result1 = null;
			
			ReadOnlyDao<SimpvehOrdItemMatEntity> dao = getDaoProvider().getSimpvehOrdItemMatEntityDao();
			Filter<SimpvehOrdItemMatEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(SimpvehOrdItemMatEntity.ATTR_ORDER_ID, orderId));
			filter.addExpression(Restrictions.eq(SimpvehOrdItemMatEntity.ATTR_LANG_CD, langCd));
			result1 = dao.find(filter);
			
			return result = result1;
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
	 * Sets the priority of a given simple vehicle.
	 * @param simpleVehicleId ID of an existing simple vehicle.
	 * @param priority The priority to set. Range: 0-99
	 * @throws IllegalArgumentException if any of the arguments are null
	 * @throws LogonException if the priority is out of range, or if the specified vehicle doesn't exist
	 */
	public void setVehiclePriority(Long simpleVehicleId, Long priority)
	throws LogonException, IllegalArgumentException
	{
		// [[
		Object[][] params = {{"simpleVehicleId", simpleVehicleId},
							{"priority", priority}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setVehiclePriority", params);
		}
		// ]]
		try {
			if (simpleVehicleId ==  null) {
				throw new IllegalArgumentException("simpleVehicleId must not be null");
			}
			if (priority == null) {
				throw new IllegalArgumentException("priority must not be null");
			}
			if (priority < VEHICLE_PRIO_MIN || priority > VEHICLE_PRIO_MAX) {
				throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_PRIO_OOR);
			}
			
			final ReadWriteDao<SimpleVehicleEntity> dao = getDaoProvider().getSimpleVehicleEntityDao();
			final SimpleVehicleEntity vehicle = dao.findById(simpleVehicleId);
			
			if (vehicle == null) {
				throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_VEHICLE_NOT_FOUND);
			}
			
			vehicle.setPriority(priority);
			dao.persist(vehicle);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private List<OrderEntity> findOrdersByOrderItemIds(Set<Long> orderItemIds)
	{
		final ReadOnlyDao<OrderItemEntity> dao = getDaoProvider().getOrderItemEntityDao();
		final Filter<OrderItemEntity> f = dao.createFilter();
		f.addExpression(Restrictions.in(OrderItemEntity.ATTR_ORDER_ITEM_ID, orderItemIds));
		
		final JPAQuery<OrderItemEntity> q = dao.createJPAQuery();
		q.addSelect(SelectExpression.simpleSelect(OrderItemEntity.ATTR_ORDER));
		q.setDistinct(true);
		q.setFilter(f);
		
		@SuppressWarnings("unchecked")
		final List<OrderEntity> orders = dao.find(q);
		
		return orders;
	}
	
	public void releaseAssignment(Long assignmentId)
	{
		final AssignmentEntity assignment = 
				getDaoProvider().getAssignmentEntityDao().findById(assignmentId);
		
		final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
		for (OrderEntity order : orders) {
			orderService.changeOrderStatus(order, Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER);
		}
		
		assignment.setAssignTrStatus(referentialDataService.findBsEntity(
				AssignTrStatusBsEntity.class, Constant.ASSIGN_STATUS_TR_TRANSMITTED));
		
		orderService.transmitToWeighSystem(assignment);
	}
	
	/**
	 * Triggers the loading process of a simple vehicle.
	 * @param simpleVehicleId The ID of an existing simple vehicle that is not loaded.
	 * @param orderItemMap Loading information for each loaded material.
	 * @oaram isAutomaticEndload If it's true then an endload method will be called at
	 * the end of the loadSimpleVehicle
	 * @throws LogonException In any of the following cases:
	 * <ul>
	 * 	<li>if the simple vehicle does not exist</li>
	 *  <li>if the load info items refer to invalid order items</li>
	 *  <li>if the vehicle has already been loaded</li>
	 * </ul>
	 */
	public void loadSimpleVehicle(
			Long simpleVehicleId,
			Map<Long, SimpleVehicleWeighingData> orderItemMap)
	{
		// [[
		Object[][] params = {{"simpleVehicleId", simpleVehicleId},
							{"orderItemMap", orderItemMap}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadSimpleVehicle", params);
		}
		// ]]
		try {
			final Set<Long> orderItemIds = orderItemMap.keySet();
			final List<OrderEntity> orders = findOrdersByOrderItemIds(orderItemIds);
			
			ConfSeqLock lock = null;
			try {
				lock = sequenceService.acquireLockForOrders(orders);
				simpleVehicleService._loadSimpleVehicle(simpleVehicleId, orderItemMap, lock);
			}
			finally {
				try {
					sequenceService.releaseLock(lock);
				}
				catch (LogonException e) {
					LOG.error(e.getMessage());
				}
			}
			
			em.clear();
			final List<OrderEntity> updatedOrders = findOrdersByOrderItemIds(orderItemIds);
			for (OrderEntity order : updatedOrders) {
				if (LogonStatusUtils.isLoadedOrBeyond(order.getOrderStatus())) {
					orderService.generateAndSendPDFWithoutExplicitLoadingpoint(order, false, false, true);
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _loadSimpleVehicle(
			Long simpleVehicleId,
			Map<Long, SimpleVehicleWeighingData> orderItemMap,
			ConfSeqLock lock)
	{
		// [[
		Object[][] params = {{"simpleVehicleId", simpleVehicleId},
							{"orderItemMap", orderItemMap},
							{"lock", lock}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "_loadSimpleVehicle", params);
		}
		// ]]
		try {
			boolean isShipOrBlocktrainPickup = (simpleVehicleId == Constant.SIMPLE_VEHICLE_BLOCKTRAIN_PICKUP_CD 
									|| simpleVehicleId == Constant.SIMPLE_VEHICLE_SHIP_PICKUP_CD);
			// Validation: the vehicle ID must be valid			
			final SimpleVehicleEntity vehicle;
			vehicle = getDaoProvider().getSimpleVehicleEntityDao().findById(simpleVehicleId);
			if (vehicle == null) {
				LOG.error("Simple vehicle with ID {} not found.", new Object[] {simpleVehicleId});
				throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_VEHICLE_NOT_FOUND);
			}
			
			AssignmentEntity activeAssignment;
			Set<Long> orderItemIds = orderItemMap.keySet();
			Object[] orderItemIdList = orderItemIds.toArray();
			Long currentOrderItemId = (Long)orderItemIdList[0];
			
			if (isShipOrBlocktrainPickup) {
				 activeAssignment = findActiveAssignmentByOrderItemId(currentOrderItemId);
			}
			else{			
				 activeAssignment = findActiveAssignmentForVehicle(simpleVehicleId);
			}
			validateAssignmentStatus(vehicle, activeAssignment);
			
			// Validation of weighing data
			Collection<SimpleVehicleWeighingData> weighingData = orderItemMap.values();
			for (SimpleVehicleWeighingData wd : weighingData)
			{
				// Must not be negative
				if (wd.getLoadedQuantity().compareTo(BigDecimal.ZERO) < 0) {
					LOG.error("A negative loaded quantity {} was specified.", 
							new Object[] {wd.getLoadedQuantity()});
					throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_NEGATIVE_LOADED_QTY);
				}
			}
			
			// loadId -> orderItemId -> loadingData
			final Map<LoadEntity, Map<Long, LoadedQuantityData>> loadMap = 
					new HashMap<LoadEntity, Map<Long, LoadedQuantityData>>();
			final Map<LoadEntity, LoadingpointEntity> loadingpointsMap = 
					new HashMap<LoadEntity, LoadingpointEntity>();
			
			final Map<Long,OrderItemEntity> orderItems = findOrderItemsByOrderItemId(orderItemIds);
			for (Long orderItemId : orderItemIds)
			{
				final OrderItemEntity orderItem = orderItems.get(orderItemId);
				final SimpleVehicleWeighingData simpleVehicleWeighingData = orderItemMap.get(orderItemId);
				
				final LoadEntity load = orderItem.getLoad();
				final Long loadingpointId = simpleVehicleWeighingData.getLoadingPointId();
				final BigDecimal loadedQuantity = simpleVehicleWeighingData.getLoadedQuantity();
				final String loadUnitOfMeasurement = simpleVehicleWeighingData.isUnitOfMeasurementSpecified() ? 
					simpleVehicleWeighingData.getBaseUnitOfMeasurementCode() : appropriateLoadedUom(orderItem).getUomCd();
				
				/*
				 * loads should not be shared. should they be shared, only the first loading point of
				 * the order items sharing the load will be considered.
				 */
				if (!loadingpointsMap.containsKey(load)) {
					final LoadingpointEntity lp = 
							getDaoProvider().getLoadingpointEntityDao().findById(loadingpointId);
					loadingpointsMap.put(load, lp);
				}
				
				final LoadedQuantityData loadedQuantityData = new LoadedQuantityData(
						loadedQuantity, loadUnitOfMeasurement);

				final Map<Long, LoadedQuantityData> itemLoadedQuantityMap;
				if (loadMap.containsKey(load)) {
					itemLoadedQuantityMap = loadMap.get(load);
				}
				else {
					itemLoadedQuantityMap = new HashMap<Long, LoadedQuantityData>();
					loadMap.put(load, itemLoadedQuantityMap);
				}
				itemLoadedQuantityMap.put(orderItem.getOrderItemId(), loadedQuantityData);
			}
			
			for (LoadEntity load : loadMap.keySet()) {
				final LoadingpointEntity loadingpoint = loadingpointsMap.get(load);				
				Map<Long, LoadedQuantityData> loadedQuantityDataMap = loadMap.get(load);
				loadingService.updateSharedLoadAndSendGoodsMovements(load, loadingpoint, loadedQuantityDataMap,
						NO_TARE_WEIGHT, NO_GROSS_WEIGHT, NO_LOADING_START_TIME, NO_LOADING_END_TIME,
						EventSource.LOGON, lock);
			}
			
			if (!isShipOrBlocktrainPickup) {
				// Update order and assignment status
				updateStatusAfterLoading(activeAssignment, orderItems, weighingData);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * It validates the activeAssigment's status  
	 * 
	 * @param simpleVehicleId
	 * @param activeAssignment
	 */
	private void validateAssignmentStatus(SimpleVehicleEntity vehicle, 
									final AssignmentEntity activeAssignment) {
			// [[
			Object[][] params = {{"vehicle", vehicle},
								{"activeAssignment", activeAssignment}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "checkSimpleVehicleActiveAssignment", params);
			}
			// ]]
			try {
				Long simpleVehicleId = vehicle.getSimpleVehicleId();
				// Validation: there must be an assignment for the vehicle
				if (activeAssignment == null) {
					LOG.error("Could not find an active assignment for simple vehicle {} (ID: {})",
							new Object[] {vehicle.getVehicleName(), simpleVehicleId});
					throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_NO_ASSIGNMENT);
				}
				
				// Validation: The assignment has the proper status for loading.
				final Long assgStatusCd = activeAssignment.getAssignStatus().getAssignStatusCd();
				if (!assgStatusCd.equals(Constant.ASSIGNMENT_STATUS_NEW) &&
						!assgStatusCd.equals(Constant.ASSIGNMENT_STATUS_LOADING) &&
						!assgStatusCd.equals(Constant.ASSIGNMENT_STATUS_SHIPPING))
				{
					LOG.error("Can only load assignments in status new, loading, or shipping. " +
							"The specified assignment with ID {} failed this check.",
							new Object[] {activeAssignment.getAssignmentId()});
					throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_INVALID_ASSG_STATUS);
				}
			}
			finally {
			// [[
				if (logDebugEnabled) {
					LOG.exiting();
				}
			// ]]
			}
	}


	/**
	 * Deallocates orders from a simple vehicle.
	 * @param orderIds The list of orders to deallocate.
	 * @throws LogonException if not all the vehicles are allocated
	 * to the same simple vehicle, or if the vehicle has already
	 * been loaded.
	 * @throws IllegalArgumentException if the given set is empty
	 */
	public void deallocateOrders(Set<Long> orderIds)
	throws LogonException, IllegalArgumentException
	{
		// [[
		Object[][] params = {{"orderIds", orderIds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "deallocateOrders", params);
		}
		// ]]
		try {
			if (orderIds.isEmpty()) {
				throw new IllegalArgumentException("The set of order IDs must not be empty.");
			}
			
			final List<AssignmentPartEntity> parts = findAssignentPartsForOrders(orderIds);
			
			/* Check that there is exactly one assignment part per order ID
			 * This assumes that there is at most one active assignment per simple vehicle;
			 * if this check fails, you should also make sure that this assumption is not
			 * violated.
			 */
			if (parts.size() != orderIds.size()) {
				throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_ORDER_NOT_ALLOCATED);
			}
			
			// Check that the orders are all part of the same assignment
			final Set<Long> assignmentIds = new HashSet<Long>();
			for (AssignmentPartEntity part : parts) {
				assignmentIds.add(part.getAssignment().getAssignmentId());
			}
			if (assignmentIds.size() > 1) {
				// Some of the orders belong to different assignments/vehicles.
				throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_NOT_UNIQUE_ASSG);
			}
			
			// At this point we know that there is exactly one assignment.
			final AssignmentEntity assignment = parts.get(0).getAssignment();
			final Long assignmentId = assignment.getAssignmentId();
			
			// This method must only be called for orders assigned to simpe vehicles. 
			if (assignment.getSimpleVehicle() == null) {
				throw new IllegalArgumentException("This method is only for orders assigned to " +
						"simple vehicles. These orders are all part of the same assignment, but " +
								"not for a simple vehicle.");
			}
			
			// Load the order entities to pass to the order service below.
			final List<OrderEntity> orders = findOrdersByIds(orderIds);
			
			ConfSeqLock lock = null;
			try {
				lock = sequenceService.acquireLockForOrders(orders);
				simpleVehicleService._deallocateOrders(orderIds, assignmentId, lock);
			}
			finally{
				try {
					sequenceService.releaseLock(lock);
				}
				catch (LogonException e) {
					LOG.error(e.getMessage());
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}


	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _deallocateOrders(Set<Long> orderIds, Long assignmentId, ConfSeqLock lock)
	{
		final List<OrderEntity> orders = findOrdersByIds(orderIds);
		final AssignmentEntity assignment = 
				getDaoProvider().getAssignmentEntityDao().findById(assignmentId);
		final SimpleVehicleEntity vehicle = assignment.getSimpleVehicle();
		final Long simpleVehicleId = vehicle.getSimpleVehicleId();
		
		/* Only orders in status "dispatched" (= scheduled), "loaded", or "shipping" can be 
		 * deallocated.
		 * The use case for deallocating an order that has already been loaded is when
		 * the user has made a mistake and wants to restart the scheduling of a simple vehicle.
		 * However, if an order has already been loaded, the corresponding information has
		 * been sent to SAP and must be reverted if the order is to be deallocated again. 
		 */
		final List<OrderItemEntity> undoLoadingOrderItems = new LinkedList<OrderItemEntity>();
		final List<String> orderNumbers = new LinkedList<String>();
		for (OrderEntity o : orders)
		{
			final Long orderStatusCd = o.getOrderStatus().getOrderStatusCd();
			if (!(LogonStatusUtils.isDispatchedOrBeyond(orderStatusCd) &&
					LogonStatusUtils.isBeforeDelivered(orderStatusCd)))
			{
				LOG.error("Cannot deallocate order {}, it must be dispatched " +
						"but not yet delivered.", new Object[] {o.getOrderId()});
				throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_DEALLOCATE_WRONG_STATUS);
			}
			
			orderNumbers.add(o.getOrderNumber().toString());
			
			// Special treatment for already loaded orders
			if (LogonStatusUtils.isLoadedOrBeyond(orderStatusCd))
			{
				LOG.debug("Order {} has already been loaded, need to undo loading first.",
						new Object[] {o.getOrderId()});
				undoLoadingOrderItems.addAll(o.getOrderItems());
			}
		}
		
		/* Now revert the loading of all these order items retrieved above. This is achieved by
		 * loading the order items to be reverted with 0 quantity, while loading the other order
		 * items with their (already) loaded quantity. The latter is required because of possibly
		 * shared loads.
		 */
		if (!undoLoadingOrderItems.isEmpty()) {
			final Set<Long> loadIds = new HashSet<Long>();
			for (OrderItemEntity orderItem : undoLoadingOrderItems)
			{
				if (orderItem.getLoad() != null) {
					loadIds.add(orderItem.getLoad().getLoadId());
				}
			}
			
			// Find all the order items of all the loads
			final ReadOnlyDao<OrderItemEntity> dao = getDaoProvider().getOrderItemEntityDao();
			final Filter<OrderItemEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.in(OrderItemEntity.ATTR_LOAD_ID, loadIds));
			List<OrderItemEntity> loadedItems = dao.find(filter);
			
			final Map<Long, SimpleVehicleWeighingData> orderItemMap;
			orderItemMap = new HashMap<Long, SimpleVehicleWeighingData>();
			for (final OrderItemEntity item : loadedItems)
			{
				final Long itemId = item.getOrderItemId();
				/* Make sure that there is a loading point set in the load. If not, there is
				 * a bug somewhere.
				 */
				if (item.getLoad().getLoadingpoint() == null) {
					throw new RuntimeException("Oops, the load with ID " + item.getLoad().getLoadId() +
							" for order item " + itemId + " does not have a loading point set. " +
							"Please fix it in the DB.");
				}
				
				final Long loadingPtId = item.getLoad().getLoadingpoint().getLoadingpointId();
				final BigDecimal loadedQty;
				
				if (undoLoadingOrderItems.contains(item))
				{
					/* This item belongs to the order to be deallocated, so we reset the loaded
					 * quantity to zero.
					 */
					loadedQty = BigDecimal.ZERO;
				}
				// The default value for something that hasn't been loaded is zero.
				else if (item.getLoadedQuantity() != null)
				{
					loadedQty = item.getLoadedQuantity();
				}
				else {
					loadedQty = BigDecimal.ZERO;
				}
				
				orderItemMap.put(itemId, new SimpleVehicleWeighingData(itemId, loadingPtId, loadedQty));
			}
			
			// (Un)do the loading
			_loadSimpleVehicle(simpleVehicleId, orderItemMap, lock);
		}
		
		/* Finally, we are ready to actually deallocate the orders.
		 * There is already a service we can call that does the actual work.
		 */
		orderService.removeAssignment(assignment, orderNumbers, false);
		
		// Update order status and unset executing carrier.
		final OrderStatusBsEntity unassigned;
		unassigned = referentialDataService.findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK);
		for (OrderEntity o : orders) {
			o.setOrderStatus(unassigned);
			o.setExecCarrier(null);
			getDaoProvider().getOrderEntityDao().merge(o);
			kafkaPublisherService.publishOrderStatusChange(o);
		}
		
		// Update the assignment status (only has an effect if all others have been delivered)
		if (Constant.ASSIGNMENT_STATUS_CANCELLED != assignment.getAssignStatus().getAssignStatusCd()) {
			boolean allDelivered = true;
			final List<OrderEntity> remainingOrders = AssignmentUtils.getOrdersFromAssignment(assignment);
			for (OrderEntity order : remainingOrders) {
				if (LogonStatusUtils.isBeforeDelivered(order.getOrderStatus())) {
					allDelivered = false;
					break;
				}
			}
			
			if (allDelivered) {
				final AssignStatusBsEntity returningStatus;
				final long returningStatusCd = Constant.ASSIGNMENT_STATUS_RETURNING;
				returningStatus = referentialDataService.findBsEntity(
						AssignStatusBsEntity.class, returningStatusCd);
				assignment.setAssignStatus(returningStatus);
			}
		}
	}


	/**
	 * Triggers the unloading process of an order. The map <code>unloadedQuantities</code>
	 * contains the amount unloaded for each order item. 
	 * @param orderId The ID of an existing order, assigned to a loaded simple vehicle.
	 * @param unloadedQuantities A map whose keys are order item IDs and whose values
	 * are the unloaded quantities for each order item.
	 * @param finalizeToSap If this is <code>true</code>, the assignment corresponding to the
	 * order is assumed to be completely delivered and the delivered quantities are sent to SAP.
	 * @throws LogonException If the order ID is not valid, if
	 * <code>unloadedQuantities</code> does not contain a quantity for every item of the order,
	 * or if <code>unloadedQuantities</code> contains negative values.
	 */
	public void unloadOrder(
			Long orderId,
			Map<Long,BigDecimal> unloadedQuantities,
			Boolean finalizeToSap)
	{
		// [[
		Object[][] params = {{"orderId", orderId},
							{"unloadedQuantities", unloadedQuantities}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "unloadOrder", params);
		}
		// ]]
		try {
			// Validation: The order is valid
			final OrderEntity order = getDaoProvider().getOrderEntityDao().findById(orderId);
			if (order == null) {
				LOG.error("Could not find order with Id {}", new Object[] {orderId});
				throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_NO_SUCH_ORDER);
			}
			
			// Validation: The order has status "shipping".
			if (!order.getOrderStatus().getOrderStatusCd().equals(Constant.ORDER_STATUS_SHIPPING)) {
				LOG.error("Only orders in status shipping ({}) can be unloaded. " +
						"The specified order has status {}.",
						new Object[] {Constant.ORDER_STATUS_SHIPPING, order.getOrderStatus().getOrderStatusCd()});
				throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_ORDER_NOT_SHIPPING);
			}
			
			/* Validation: There is an unloaded quantity for every order item, and it is
			 * nonnegative.
			 */
			final Map<Long,OrderItemEntity> orderItems = findMaterialOrderItemsForOrder(orderId);
			for (Long itemId : orderItems.keySet())
			{
				final OrderItemEntity i = orderItems.get(itemId);
				if (!unloadedQuantities.containsKey(i.getOrderItemId())) {
					LOG.error("No unloaded quantity specified for order item {}",
							new Object[] {itemId});
					throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_MISSING_UNLOAD_QTY);
				}
				if (unloadedQuantities.get(itemId).compareTo(BigDecimal.ZERO) < 0) {
					LOG.error("Unloaded quantity specified was negative.");
					throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_UNLOAD_QTY_NEGATIVE);
				}
			}
			
			final AssignmentEntity assignment = findActiveAssignmentForOrder(orderId);
			// Validation: there is an assignment
			if (assignment == null) {
				LOG.error("No active assignment found for order with ID {}.",
						new Object[] {orderId});
				throw new LogonException(Reason.RES_ERROR_SIMPLEVEHICLE_NO_ASSIGNMENT);
			}
			
			/* TODO (marius Nov 19, 2012): Unloaded quantities cannot currently be set
			 * when unloading. Here'is today's summary from schuler:
			 *
			 *   Summary: the problem is that at the moment you can't specify the
			 *   returned quantity at the same time as the unloaded quantity.
			 *
			 *   Short Term Solution for next release:
			 *   remove the columns with the unloading information (unloading qty, and
			 *   unloading uom) => always unload the whole quantity
			 *   add a checkbox which sets the "release to SAP" flag to false in order
			 *   that the returned quantity can be added in order detail
			 *
			 * So for now, we don't do anything with the unloaded quantities.
			 */
			
			final BizeventEntity unloadEvent = createUnloadEvent(order, assignment);
			
			/* LOGON-6188:
			 * If it should be finalized, then it's the same case as if there is no restmenge
			 * ("restmenge" = "remaining quantity").
			 */
			final boolean restmengeFlag;
			if (finalizeToSap) {
				restmengeFlag = false;
			}
			else {
				// We "abuse" the restmenge flag to prevent sending to SAP if the checkbox
				// "Release order to SAP" is unchecked. 
				restmengeFlag = true;
			}
			orderService.setUnloaded(unloadEvent, null, restmengeFlag, Boolean.FALSE, false);
			
			// Note: the order and assignment status are updated by setUnloaded().
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();			// Assignment status is already updated
			}
		// ]]
		}
	}


	/**
	 * Assigns a given order to a simple vehicle.
	 * @param orderId The ID of an existing <em>unallocated</em> order.
	 * @param simpleVehicleId The ID of a vehicle to which the order should be assigned.
	 * @throws IllegalArgumentException if any of the input parameters are <code>null</code>
	 * @throws LogonException if any of the following applies:
	 * <ul>
	 * 	<li>no order or vehicle with the respective IDs exist</li>
	 * 	<li>the specified order is already assigned to a vehicle</li>
	 * 	<li>the vehicle does not belong to any of the executing carriers of the order's
	 * 	commercial carrier</li>
	 * 	<li>the vehicle has already been loaded</li>
	 * </ul>
	 */
	public void assignOrderToVehicle(Long orderId, Long simpleVehicleId)
	throws IllegalArgumentException, LogonException
	{
		// [[
		Object[][] params = {{"orderId", orderId + ""},
							 {"simpleVehicleId", simpleVehicleId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "assignOrderToVehicle", params);
		}
		// ]]
		try {
			
			if (orderId == null) {
				throw new IllegalArgumentException("The order item ID cannot be null.");
			}
			if (simpleVehicleId == null) {
				throw new IllegalArgumentException("The loading point ID cannot be null.");
			}
			
			final OrderEntity order = getDaoProvider().getOrderEntityDao().findById(orderId);
			if (order == null) {
				throw new LogonException("Cannot find order (id=" + orderId + ").", Reason.RES_ERROR_IMPL);
			}
			final SimpleVehicleEntity simpleVehicle = getDaoProvider().getSimpleVehicleEntityDao().findById(simpleVehicleId);
			if (simpleVehicle == null) {
				throw new LogonException("Cannot find simple vehicle (id=" + simpleVehicleId + ").", Reason.RES_ERROR_IMPL);
			}
			
			// Check whether the order is unassigned (there is only one unassigned status)
			if (!isUnassigned(order)) {
				throw new LogonException("Order " + order.getOrderNumber() + " is in status " + order.getOrderStatus().getOrderStatusCd() + ".", Reason.RES_ORDER_WRONG_STATUS);
			}
			
			// Check vehicle compatibility
			final CarrierEntity commercialCarrier = order.getCommCarrier();
			final CarrierEntity executingCarrier = simpleVehicle.getCarrier();
			if (!carriersMatch(commercialCarrier, executingCarrier)) {
				throw new LogonException("The simple vehicle's owner (" + executingCarrier.getDisplayName() +
						") is not a compatible executing carrier for this order's commercial carrier (" + commercialCarrier.getDisplayName() + ").",
						Reason.RES_ERROR_IMPL);
			}
			
			/* If there is an existing assignment for this vehicle,
			 * we must merge the assignments.
			 */
			orderService.assignOrderToSimpleVehicle(order, simpleVehicle);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}


	/**
	 * Determines the correct UOM to set as the "loaded UOM" of an order item,
	 * according to the specification.
	 * @param item An order item that is being loaded
	 * @return The UOM that should be set as "loaded UOM" in the order item.
	 */
	private static UomBsEntity appropriateLoadedUom(final OrderItemEntity item) {
		final UomBsEntity loadedUom;
		if (LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(item)) {
			loadedUom = item.getSapMaterial().getBaseUom();
		}
		else {
			loadedUom = item.getOrderedUom();
		}
		return loadedUom;
	}
	
	
	/**
	 * Checks if a an order is unassigned (in the context of
	 * simple vehicle orders). See LOGON-5901 for the lifecycle of
	 * simple vehicle orders: a new order initially has status
	 * "confirmed by commercial carrier", which means it is unassigned.
	 * As soon as the status changes to something else, the order
	 * no longer counts as unassigned.
	 * @param order the order to check
	 * @return true if the order has not yet been assigned, false otherwise.
	 */
	private static boolean isUnassigned(final OrderEntity order)
	{
		// [[
		boolean result = false;
		Object[][] params = {{"order", order}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SimpleVehicleSchedulingServiceBean.class, "isUnassigned", params);
		}
		// ]]
		try {
			// order_status_cd is not null on the DB.
			return result = order.getOrderStatus().getOrderStatusCd().equals(Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK);
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
	 * Checks if a commercial and an executing carrier match. This method checks
	 * whether the given executing carrier actually has an "executing" relationship
	 * with the commercial carrier. This can be used to check if a vehicle is allowed
	 * to ship an order, by passing the order's carrier (the commercial carrier)
	 * and the vehicle's carrier (the executing carrier).
	 * @param commercialCarrier
	 * @param executingCarrier
	 * @return true if the given executing carrier can execute orders for the given
	 * commercial carrier, false otherwise.
	 */
	private boolean carriersMatch(final CarrierEntity commercialCarrier,
			final CarrierEntity executingCarrier)
	{
		// [[
		boolean result = false;
		Object[][] params = {{"commercialCarrier", commercialCarrier},
							{"executingCarrier", executingCarrier}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "carriersMatch", params);
		}
		// ]]
		try {
			final ReadOnlyDao<CommExecCarrierPfEntity> pfDao = getDaoProvider().getCommExecCarrierPfEntityDao();
			final Filter<CommExecCarrierPfEntity> pfFilter = pfDao.createFilter();
			pfFilter.addExpression(Restrictions.eq(CommExecCarrierPfEntity.ATTR_COMM_CARRIER_ID, commercialCarrier.getCarrierId()));
			pfFilter.addExpression(Restrictions.eq(CommExecCarrierPfEntity.ATTR_EXEC_CARRIER_ID, executingCarrier.getCarrierId()));
			
			return result = (pfDao.findSingle(pfFilter) != null);
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
	 * Updates the status of orders and assignments after loading. This method updates the status
	 * of an assignment and its related orders after a loading operation has been performed.
	 * The rules for updating the status are defined in LOGON-5901.
	 * @param assignment The assignment for which loading has been done.
	 * @param orderItems The order items involved in the loading process
	 * @param weighingData The loading information.
	 */
	private void updateStatusAfterLoading(
			final AssignmentEntity assignment,
			Map<Long,OrderItemEntity> orderItems, 
			Collection<SimpleVehicleWeighingData> weighingData) {
		// [[
		Object[][] params = {{"assignment", assignment},
							{"weighingData", weighingData}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateStatusAfterLoading", params);
		}
		// ]]
		try {
			final AssignStatusBsEntity newAssgStatus;
			final Long newAssgStatusCd;

			// LOGON-5901:
			newAssgStatusCd = Constant.ASSIGNMENT_STATUS_SHIPPING;
			newAssgStatus = referentialDataService.findBsEntity(AssignStatusBsEntity.class, newAssgStatusCd);
			assignment.setAssignStatus(newAssgStatus);
			getDaoProvider().getAssignmentEntityDao().merge(assignment);
			
			updateOrderStatusAfterLoading(orderItems, weighingData);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}


	/**
	 * Updates the status of all involved orders according to LOGON-5901. If
	 * <code>weighingData</code> contains positive quantities for all of an order's order items,
	 * the order's status is set to Shipping, otherwise it is set to Loaded.
	 * @param orderItems The order items for which there is weighing data
	 * @param weighingData The weighing data
	 */
	private void updateOrderStatusAfterLoading(Map<Long, OrderItemEntity> orderItems,
			Collection<SimpleVehicleWeighingData> weighingData)
	{
		// Collect all involved orders
		final Map<Long,OrderEntity> orders = new HashMap<Long, OrderEntity>();
		for (Long itemId: orderItems.keySet())
		{
			final OrderItemEntity item = orderItems.get(itemId);
			final OrderEntity order = item.getOrder();
			orders.put(order.getOrderId(), order);
		}
		
		// Make a map of the weighing data
		final Map<Long,BigDecimal> loadedPerItem = new HashMap<Long, BigDecimal>();
		for (final SimpleVehicleWeighingData wd : weighingData)
		{
			loadedPerItem.put(wd.getOrderItemId(), wd.getLoadedQuantity());
		}
		
		// Check order by order
		for (Long orderId : orders.keySet())
		{
			final OrderEntity order = orders.get(orderId);
			final OrderStatusBsEntity orderStatus;
			orderStatus = orderStatusAfterLoading(order, loadedPerItem);
			order.setOrderStatus(orderStatus);
			getDaoProvider().getOrderEntityDao().merge(order);
			kafkaPublisherService.publishOrderStatusChange(order);
		}
	}


	/**
	 * Returns the status to which an order should be set after a loading operation.
	 * Depending on the loaded quantities, this method returns the appropriate status
	 * to which an order should be set according to LOGON-5901.
	 * @param order An order
	 * @param loadedPerItem A map containing the loaded quantities for all order items
	 * involved in the loading operation.
	 * @return The status to which the order should be set.
	 */
	private OrderStatusBsEntity orderStatusAfterLoading(
			OrderEntity order,
			Map<Long, BigDecimal> loadedPerItem)
	{
		final OrderStatusBsEntity loaded;
		final OrderStatusBsEntity shipping;

		loaded = referentialDataService.findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_LOADED);
		shipping = referentialDataService.findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_SHIPPING);
		
		final List<OrderItemEntity> items = order.getOrderItems();

		// Check if all the order's items have a positive loaded quantity.
		for (final OrderItemEntity item : items)
		{
			// Skip service materials
			if (LogonPackageHandlingUtil.isService(item)) {
				continue;
			}
			
			if (!loadedPerItem.containsKey(item.getOrderItemId())) {
				// Nothing has been loaded for this item, don't set to shipping yet.
				return loaded;
			}
			
			if (loadedPerItem.get(item.getOrderItemId()).compareTo(BigDecimal.ZERO) == 0) {
				// Loaded quantity was zero - don't send to shipping yet
				return loaded;
			}
		}
		
		// A positive quantity was loaded for all the order's items.
		return shipping;
	}


	/**
	 * Creates an unload event for the given order and assignment.
	 * @param order
	 * @param assignment
	 * @return
	 */
	private BizeventEntity createUnloadEvent(
			final OrderEntity order,
			final AssignmentEntity assignment)
	{
		final BizeventEntity unloadEvent = new BizeventEntity();
		final ShiptoEntity shipto;
		shipto = dispokarteService.fetchShiptoBySapId(order.getShiptoSapBp().getSapId());
		
		unloadEvent.setBizeventType(referentialDataService.findBsEntity(BizeventTypeBsEntity.class, Constant.BIZEVENT_TYPE_UNLOADING));
		unloadEvent.setDescription("Unloaded simple vehicle order with ID " + order.getOrderId());
		unloadEvent.setTruck(null);
		unloadEvent.setTrailer(null);
		unloadEvent.setContainer(null);
		unloadEvent.setWagon(null);
		unloadEvent.setSimpleVehicle(assignment.getSimpleVehicle());
		unloadEvent.setOrder(order);
		unloadEvent.setAssignment(assignment);
		unloadEvent.setCoordLat(null);
		unloadEvent.setCoordLon(null);
		unloadEvent.setRailwayStation(null);
		unloadEvent.setShippingpoint(null);
		unloadEvent.setShipto(shipto);
		unloadEvent.setCoordTruckHomebaseFlag(null);
		unloadEvent.setEventTs(new Date() /* now */);
		unloadEvent.setOriginalMopevent(null);
		return unloadEvent;
	}
	
	
	/**
	 * Converts a set of rows from the view <code>VLGNC_SIMPLE_VEHICLE_ASSG</code>
	 * to a list of corresponding SimpleVehicleAssignmentInfo objects.
	 * Essentially, this method converts the flat list returned by the
	 * view into an 1:N datastructure as implemented by 
	 * <code>SimpleVehicleAssignmentInfo</code>.
	 * @param viewResults Result lists obtained from the view
	 * @return A corresponding list of <code>SimpleVehicleAssignmentInfo</code> elements.
	 */
	static Collection<SimpleVehicleAssignmentInfo> createSimpleVehicleAssgInfos(List<SimpleVehicleAssgEntity> viewResults)
	{
		// [[
		Collection<SimpleVehicleAssignmentInfo> result = null;
		Object[][] params = {{"viewResults", viewResults}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SimpleVehicleSchedulingServiceBean.class, "createSimpleVehicleAssgInfos", params);
		}
		// ]]
		try {
			// The returned collection (need to treat it as List to make it sortable)
			final List<SimpleVehicleAssignmentInfo> vehicles = new LinkedList<SimpleVehicleAssignmentInfo>();
			
			// Maps vehicles to the list of assignments; used to group the view results by vehicle ID.
			final Map<Long,List<SimpleVehicleAssgEntity>> vehicleToAssgParts = new HashMap<Long,List<SimpleVehicleAssgEntity>>();
			
			// Group the view results by vehicle ID.
			for (SimpleVehicleAssgEntity r : viewResults) 
			{
				final Long vehicleId = r.getSimpleVehicleId();
				
				/* Initialize the map entry with an empty list if
				 * this is the first occurrence of this vehicle.
				 */
				if (!vehicleToAssgParts.containsKey(vehicleId)) {
					vehicleToAssgParts.put(vehicleId, new LinkedList<SimpleVehicleAssgEntity>());
				}
				vehicleToAssgParts.get(vehicleId).add(r);
			}
			
			for (Long vehicleId : vehicleToAssgParts.keySet())
			{
				final List<SimpleVehicleAssgEntity> parts = vehicleToAssgParts.get(vehicleId);
				
				vehicles.add(createSimpleVehicleAssgInfo(parts));
			}
			
			// Apply correct sort order (see SDS LRQ 108.0, Section 6.4.4)
			Collections.sort(vehicles, new VehicleListComparator());
			
			return result = vehicles;
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
	 * Converts a list of results of <code>VLGNC_SIMPLE_VEHICLE_ASSG</code> that
	 * all belong to the same simple vehicle into a single SimpleVehicleAssignmentInfo object.
	 * @param rows A list of view results; they must all correspond to the same
	 * vehicle, i.e., they must all have the same value in the attribute vehicleId. 
	 * @return An object where the 1:N relationship has been reconstructed.
	 * @throws IllegalArgumentException if the input is null or empty
	 */
	static SimpleVehicleAssignmentInfo createSimpleVehicleAssgInfo(List<SimpleVehicleAssgEntity> rows) 
	{
		// [[
		SimpleVehicleAssignmentInfo result = null;
		Object[][] params = {{"rows", rows}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SimpleVehicleSchedulingServiceBean.class, "createSimpleVehicleAssgInfo", params);
		}
		// ]]
		try {
			if (rows == null) {
				throw new IllegalArgumentException("parts must not be null");
			}
			if (rows.isEmpty()) {
				throw new IllegalArgumentException("the list of parts must not be empty");
			}
			
			final SimpleVehicleAssgEntity firstRow = rows.get(0);
			
			final Long vehicleId = firstRow.getSimpleVehicleId();
			final Long assignmentId = firstRow.getAssignmentId();
			
			final Long weighSystemSpCd = firstRow.getWeighSystemSpCd();
			boolean ldsDefined = false;
			if (weighSystemSpCd != null && weighSystemSpCd.longValue() != Constant.WEIGH_SYSTEM_SP_NONE) {
				ldsDefined = true;
			}
			
			final Collection<SimpleVehicleAssignmentPart> parts;
			boolean allReleased = true;
			if (assignmentId != null)
			{
				parts = new LinkedList<SimpleVehicleAssignmentPart>();
				for (SimpleVehicleAssgEntity row : rows)
				{
					final SimpleVehicleAssignmentPart part = new SimpleVehicleAssignmentPart(
							row.getOrderId(),
							row.getOrderNumber(),
							row.getOrderStatusCd(),
							row.getShipto(),
							row.getMaterialTxt(),
							row.getOrderStatusTxt()
							);
					
					parts.add(part);
					
					boolean curReleased = LogonStatusUtils.isBeyondDispatched(row.getOrderStatusCd());
					allReleased &= curReleased;
				}
			}
			else {
				// This vehicle does not have any assigned orders
				parts = new LinkedList<SimpleVehicleAssignmentPart>();
			}
			
			return result = new SimpleVehicleAssignmentInfo(
					vehicleId,
					firstRow.getVehicleName(),
					firstRow.getVehicleRegNo(),
					firstRow.getVehicleTypeTx(),
					firstRow.getPriority(),
					firstRow.getTareWeightT(),
					firstRow.getMaxGrossWeightT(),
					firstRow.getCarrierName(),
					firstRow.getAssStTxt(),
					assignmentId,
					allReleased,
					ldsDefined,
					parts);
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
	 * Fetches the active assignment to which an order belongs.
	 * @param orderId The ID of the order whose assignment should be fetched.
	 * @return The active assignment associated to the order, or null if no assignment was found.
	 */
	private AssignmentEntity findActiveAssignmentForOrder(Long orderId)
	{
		final ReadOnlyDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER_ID, orderId));
		filter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		
		/* If this fails with a NonUniqueResultException, there is a bug somewhere.
		 * There should be at most one assignment for a simple vehicle order.
		 */
		final AssignmentPartEntity ap = dao.findSingle(filter);
		if (ap == null) {
			// Nothing found
			return null;
		}
		
		return ap.getAssignment();
	}
	
	
	/**
	 * Fetches orders by a set of IDs. This method does almost the same as the DAO's
	 * <code>findById()</code> method, but accepts a <em>set</em> of IDs instead.
	 * @param orderIds A set of order IDs
	 * @return The set of order entities with the specified IDs, if found.
	 */
	private List<OrderEntity> findOrdersByIds(Set<Long> orderIds) {
		final ReadOnlyDao<OrderEntity> dao = getDaoProvider().getOrderEntityDao();
		final Filter<OrderEntity> orderFilter = dao.createFilter();
		orderFilter.addExpression(Restrictions.in(OrderEntity.ATTR_ORDER_ID, orderIds));
		final List<OrderEntity> orders = dao.find(orderFilter);
		return orders;
	}


	/**
	 * Loads a set of order item entities given their ids.
	 * @param orderItemIds The set of order item ids
	 * @return The set of order items for which the weighing data contains information.
	 * It is returned as a map whose key is the order item ID.
	 */
	private Map<Long, OrderItemEntity> findOrderItemsByOrderItemId(
			Set<Long> orderItemIds)
	{
		// Load the order items from the DB.
		final ReadOnlyDao<OrderItemEntity> dao = getDaoProvider().getOrderItemEntityDao();
		final Filter<OrderItemEntity> f = dao.createFilter();
		f.addExpression(Restrictions.in(OrderItemEntity.ATTR_ORDER_ITEM_ID, orderItemIds));
		final List<OrderItemEntity> items = dao.find(f);
		
		// Add the order items to the returned map.
		final Map<Long,OrderItemEntity> map = new HashMap<Long, OrderItemEntity>();
		for (OrderItemEntity i : items)
		{
			map.put(i.getOrderItemId(), i);
		}
		
		return map;
	}
	/**
	 * Loads the order entities of the order with a given order ID.
	 * @param orderId The ID of the order whose order items should be loaded.
	 * @return The set of order items of the given order. It is returned as a map 
	 * whose key is the order item ID.
	 */
	private Map<Long,OrderItemEntity> findMaterialOrderItemsForOrder(Long orderId)
	{
		final ReadOnlyDao<OrderItemEntity> dao = getDaoProvider().getOrderItemEntityDao();
		final Filter<OrderItemEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_ORDER_ID, orderId));
		
		final List<OrderItemEntity> items = dao.find(filter);
		final Map<Long,OrderItemEntity> map = new HashMap<Long, OrderItemEntity>();
		for (OrderItemEntity i : items) {
			// Skip service materials
			if (LogonPackageHandlingUtil.isService(i)) {
				continue;
			}
			map.put(i.getOrderItemId(), i);
		}
		
		return map;
	}
	
	
	/**
	 * Fetches the assignment part for each of the specified orders.
	 * @param orderIds A list of order IDs
	 * @return The set of assignment parts which refer to one of the given
	 * order IDs and which belong to an active simple vehicle assignment. 
	 */
	private List<AssignmentPartEntity> findAssignentPartsForOrders(Set<Long> orderIds)
	{
		final ReadOnlyDao<AssignmentPartEntity> dao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> assgFilter = dao.createFilter();
		assgFilter.addExpression(Restrictions.in(AssignmentPartEntity.ATTR_ORDER_ID, orderIds));
		assgFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		assgFilter.addExpression(Restrictions.isNotNull(AssignmentPartEntity.ATTR_ASSIGNMENT + "." + AssignmentEntity.ATTR_SIMPLE_VEHICLE_ID));
		final List<AssignmentPartEntity> parts = dao.find(assgFilter);
		return parts;
	}
	

	/**
	 * Finds an existing active assignment to a simple vehicle.
	 * The assumption here is that at most one active assignment exists
	 * for a simple vehicle at any given time.
	 * @param vehicle A simple vehicle
	 * @return The active assignment of the vehicle, or <code>null</code>
	 * if the vehicle doesn't have an active assignment.
	 */
	public AssignmentEntity findActiveAssignmentForVehicle(SimpleVehicleEntity vehicle) {
		final Long vehicleId = vehicle.getSimpleVehicleId();
		return findActiveAssignmentForVehicle(vehicleId);
	}
	
	/**
	 * Returns the active assignment (if any) for a simple vehicle
	 * @param vehicleId The ID of a simple vehicle
	 * @return The active assignment for the vehicle (there can be
	 * at most one) or <code>null</code> if the vehicle has no assignment.
	 */
	private AssignmentEntity findActiveAssignmentForVehicle(Long vehicleId)
	{
		final ReadOnlyDao<AssignmentEntity> dao = getDaoProvider().getAssignmentEntityDao();
		final Filter<AssignmentEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_SIMPLE_VEHICLE_ID, vehicleId));
		filter.addExpression(Restrictions.in(AssignmentEntity.ATTR_ASSIGN_STATUS_CD, activeSimpleVehicleAssignmentStatusSet));
		filter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		
		/* If this throws a NonUniqueResultException, there is
		 * something wrong with the assumption that at most one
		 * active assignment exists for a simple vehicle.
		 * Then you need to check the code that creates these assignments.
		 */		
		return dao.findSingle(filter);
	}

	/**
	 * Returns the active assignments (if any) for a simple vehicle
	 * @param vehicleId The ID of a simple vehicle
	 * @return The active assignment for the vehicle (there can be
	 * at most one) or <code>null</code> if the vehicle has no assignment.
	 */
	private AssignmentEntity findActiveAssignmentByOrderItemId(Long orderItemId)
	{		
		final ReadOnlyDao<OrderItemEntity> orderItemDao = getDaoProvider().getOrderItemEntityDao();
		final Filter<OrderItemEntity> orderItemfilter = orderItemDao.createFilter();
		orderItemfilter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_ORDER_ITEM_ID, orderItemId));
		OrderItemEntity orderItem = orderItemDao.findSingle(orderItemfilter);
		OrderEntity order = orderItem.getOrder();
		Long orderId = order.getOrderId();
		
		final ReadOnlyDao<AssignmentPartEntity> assignmentPartDao = getDaoProvider().getAssignmentPartEntityDao();
		final Filter<AssignmentPartEntity> assignmentPartFilter = assignmentPartDao.createFilter();
		assignmentPartFilter.addExpression(Restrictions.eq(AssignmentPartEntity.ATTR_ORDER_ID, orderId));
		AssignmentPartEntity assignmentPart = assignmentPartDao.findSingle(assignmentPartFilter);
		return assignmentPart.getAssignment();
	}
	
	/**
	 * Loads the vehicle data from the view VLGNC_SIMPLE_VEHICLE_ASSG, filtered
	 * by the specified carrier IDs and the language.
	 * @param carrierIds Only vehicles belonging to these carriers are returned.
	 * @param locale The locale is used to determine the language of the 
	 * reference data strings such as vehicle type or assignment status.
	 * @return The list of matching rows from the view
	 */
	private List<SimpleVehicleAssgEntity> fetchVehicleDataFromView(Set<Long> carrierIds,
			Locale locale) {
		final ReadOnlyDao<SimpleVehicleAssgEntity> dao = getDaoProvider().getSimpleVehicleAssgEntityDao();
		final Filter<SimpleVehicleAssgEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.in(SimpleVehicleAssgEntity.ATTR_CARRIER_ID, carrierIds));
		filter.addExpression(Restrictions.eq(SimpleVehicleAssgEntity.ATTR_LANG_CD, locale.getLanguage()));
		
		final List<SimpleVehicleAssgEntity> viewResults = dao.find(filter);
		return viewResults;
	}
	
	
	/**
	 * Fetches and returns all rows from VLGNC_SIMPVEH_UNALC_ORD that
	 * match the given poolId and language.
	 * @param poolId only orders assigned to this pool are returned
	 * @param langCd used to determine the lanugage for those columns taken from
	 * language-dependent reference data
	 * @return the list of matching rows from the view
	 */
	private List<SimpvehUnalcOrdEntity> fetchOrdersFromView(Long poolId, String langCd)
	{
		final ReadOnlyDao<SimpvehUnalcOrdEntity> orderDao = getDaoProvider().getSimpvehUnalcOrdEntityDao();
		final Filter<SimpvehUnalcOrdEntity> filter = orderDao.createFilter();
		filter.addExpression(Restrictions.eq(SimpvehUnalcOrdEntity.ATTR_POOL_ID, poolId));
		filter.addExpression(Restrictions.eq(SimpvehUnalcOrdEntity.ATTR_LANG_CD, langCd));
		
		return orderDao.find(filter);
	}
	
	
	/**
	 * Fetches and returns all rows from VLGNC_SIMPVEH_ORD_VHC_ENTITY whose
	 * carrier ID is in the given set.
	 * @param carrierIds Only rows with the carrier ID in this set are returned.
	 * @return the list of matching rows rom the view
	 */
	private List<SimpvehOrdVhcEntity> fetchOrderVehiclesFromView(Set<Long> carrierIds)
	{
		final ReadOnlyDao<SimpvehOrdVhcEntity> dao = getDaoProvider().getSimpvehOrdVhcEntityDao();
		final Filter<SimpvehOrdVhcEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.in(SimpvehOrdVhcEntity.ATTR_CARRIER_ID, carrierIds));
		List<SimpvehOrdVhcEntity> vehicles = dao.find(filter);
		return vehicles;
	}


	/**
	 * Combines two flat lists of SimpvehUnalcOrdEntity and SimpvehOrdVhcEntity
	 * objects into a list where vehicles for the same order are grouped together.
	 * @param orders A list of orders obtained from VLGNC_SIMPVEH_UNALC_ORD
	 * @param vehicles A list of vehicles obtained from VLGNC_SIMPVEH_ORD_VHC
	 * @return A list of UnallocatedOrderData objects, where each object's
	 * <code>order</code> attribute is a different order, and each object's
	 * <code>vehicles</code> list contains all vehicles to which the order
	 * can be assigned.
	 */
	static Collection<UnallocatedOrderData> makeUnallocatedOrderDataList(
			List<SimpvehUnalcOrdEntity> orders,
			List<SimpvehOrdVhcEntity> vehicles)
	{
		// [[
		Collection<UnallocatedOrderData> result = null;
		Object[][] params = {{"orders", orders},
							{"vehicles", vehicles}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(SimpleVehicleSchedulingServiceBean.class, "makeUnallocatedOrderDataList", params);
		}
		// ]]
		try {
			final Map<Long, List<SimpvehOrdVhcEntity>> vehiclesByOrder = new HashMap<Long, List<SimpvehOrdVhcEntity>>();
			
			// First group the vehicles by the order ID into the map vehiclesByOrder.
			for (final SimpvehOrdVhcEntity vhc : vehicles)
			{
				final Long vhcOrderId = vhc.getOrderId();
				
				/* Initialize list if this is the first
				 * occurrence of this order ID.
				 */
				if (!vehiclesByOrder.containsKey(vhc.getOrderId())) {
					vehiclesByOrder.put(vhcOrderId, new LinkedList<SimpvehOrdVhcEntity>());
				}
				
				vehiclesByOrder.get(vhcOrderId).add(vhc);
			}
			
			/* Now create the list of orders, taking the vehicles
			 * from the map for each order as we go.
			 */
			final List<UnallocatedOrderData> list = new LinkedList<UnallocatedOrderData>();
			for (final SimpvehUnalcOrdEntity order : orders)
			{
				final UnallocatedOrderData orderData;
				final List<SimpvehOrdVhcEntity> vehiclesForOrder;
				vehiclesForOrder = vehiclesByOrder.get(order.getOrderId());
				
				if (vehiclesForOrder == null) {
					// No assignable vehicles for this order, pass empty list
					orderData = new UnallocatedOrderData(order, new LinkedList<SimpvehOrdVhcEntity>());
				}
				else {
					orderData = new UnallocatedOrderData(order, vehiclesForOrder);
				}
				list.add(orderData);
			}
			
			/* Spec says the orders must be sorted.
			 * See comparator definition above for ordering rules.
			 */
			Collections.sort(list, new UnallocatedOrderComparator());
			
			return result = list;
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
	 * Extracts the set of distinct <code>simpvehLoadOrderItemId</code> from a list of load order
	 * item entities.
	 * @param loadOrderItems A collection of SimpvehLoadOrderItemEntity objects
	 * @return The set of IDs of thes entities
	 */
	private Set<Long> extractLoadOrderItemIds(Collection<SimpvehLoadOrderItemEntity> loadOrderItems)
	{
		final Set<Long> ids = new HashSet<Long>();
		
		for (SimpvehLoadOrderItemEntity item : loadOrderItems) {
			ids.add(item.getSimpvehLoadOrderItemId());
		}
		
		return ids;
	}


	/**
	 * Loads the {@link SimpvehLoadingpointEntity}s from the DB for a given set of IDs.
	 * @param ids A set of IDs
	 * @return All {@link SimpvehLoadingpointEntity} objects whose ID is in the given set.
	 * @throws IllegalArgumentException if <code>ids</code> is empty.
	 */
	private List<SimpvehLoadingpointEntity> fetchLoadingpointsForLoadOrderItems(Set<Long> ids)
	throws IllegalArgumentException
	{
		if (ids.isEmpty()) {
			throw new IllegalArgumentException("The list of ids must not be empty.");
		}
		
		final ReadOnlyDao<SimpvehLoadingpointEntity> dao;
		dao = getDaoProvider().getSimpvehLoadingpointEntityDao();
		
		final Filter<SimpvehLoadingpointEntity> f = dao.createFilter();
		f.addExpression(Restrictions.in(SimpvehLoadingpointEntity.ATTR_SIMPVEH_LOAD_ORDER_ITEM_ID, ids));
		
		return dao.find(f);
	}


	/**
	 * Combines a list of {@link SimpvehLoadingpointEntity} objects and a list of
	 * {@link SimpvehLoadingpointEntity} objects into a list of
	 * {@link SimpleVehicleLoadableItem} objects by grouping the objects according to
	 * the <code>simpvehLoadOrderItemId</code> attribute.
	 * @param loadOrderItems A list of simple vehicle load order items
	 * @param loadingPoints A list of simple vehicle loadingpoints
	 * @return A list of SimpleVehicleLoadableItem objects, where each load order item is
	 * combined with its associated loadingpoints.
	 */
	private Collection<SimpleVehicleLoadableItem> combineAsLoadableItemList(
			List<SimpvehLoadOrderItemEntity> loadOrderItems,
			List<SimpvehLoadingpointEntity> loadingPoints)
	{
		final List<SimpleVehicleLoadableItem> list = new LinkedList<SimpleVehicleLoadableItem>();
		final Map<Long, List<SimpvehLoadingpointEntity>> loadingpointsByOrderItem;
		loadingpointsByOrderItem = new HashMap<Long, List<SimpvehLoadingpointEntity>>();
		
		// Step 1: Group loadingpoints by order item ID into a map
		for (SimpvehLoadingpointEntity lp : loadingPoints)
		{
			final Long id = lp.getSimpvehLoadOrderItemId();
			if (!loadingpointsByOrderItem.containsKey(id)) {
				loadingpointsByOrderItem.put(id, new LinkedList<SimpvehLoadingpointEntity>());
			}
			
			loadingpointsByOrderItem.get(id).add(lp);
		}
		
		/* Step 2: Combine each order item with its associated loading points into a new
		 * SimpleVehicleLoadableItem object.
		 */
		for (SimpvehLoadOrderItemEntity item : loadOrderItems)
		{
			final Long id = item.getSimpvehLoadOrderItemId();
			final SimpleVehicleLoadableItem combinedItem;
			combinedItem = new SimpleVehicleLoadableItem(item, loadingpointsByOrderItem.get(id));
			
			list.add(combinedItem);
		}
		
		return list;
	}


}
