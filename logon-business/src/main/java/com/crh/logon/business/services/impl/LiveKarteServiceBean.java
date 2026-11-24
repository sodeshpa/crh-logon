/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.heremap.RoutingServiceBean;
import com.crh.logon.business.heremap.RoutingServiceBean.Origin;
import com.crh.logon.business.heremap.RoutingServiceBean.Target;
import com.crh.logon.business.heremap.RoutingServiceBean.Via;
import com.crh.logon.business.itrack.dto.PositionDTO;
import com.crh.logon.business.services.beans.VehicleCoordinatesBean;
import com.crh.logon.business.services.dispatchers.LiveKarteServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LiveKarteServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.stores.domain.AssignmentStoreInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.monitoring.ContainerPositionEntity;
import com.crh.logon.persistence.entities.monitoring.TrailerPositionEntity;
import com.crh.logon.persistence.entities.monitoring.TruckPositionEntity;
import com.crh.logon.persistence.entities.monitoring.WagonPositionEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentPartTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.BizeventMapEntity;
import com.crh.logon.persistence.entities.views.OrderFiltersEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class LiveKarteServiceBean extends LiveKarteServiceDispatcher implements LiveKarteServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LiveKarteServiceBean.class);

	private static final Set<Long> ROUTING_RELEVANT_STATUS_CD_SET = Collections.unmodifiableSet(new HashSet<>(
			Arrays.asList(Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
					Constant.ORDER_STATUS_LOADING, Constant.ORDER_STATUS_LOADED, Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL,
					Constant.ORDER_STATUS_SHIPPING, Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
					Constant.ORDER_STATUS_UNLOADING)));

	@EJB
	private DispokarteServiceLocalInterface dispokarteService;

	@EJB
	private RoutingServiceBean routingServiceBean;

	@EJB
	private AssignmentStoreInterface assignmentStore;

	/**
	 * Loads truck, trailer, container, wagon coordinates from tlgnc_bizevent_map view for a given orders
	 * @param
	 * @adnlog.exclude return
	 */
	public List<BizeventMapEntity> loadBizevents(String lang, int first, int pagesize, List<Long> orderIds) {
		// [[
		Object[][] params = {{"lang", lang},
							{"first", first + ""},
							{"pagesize", pagesize + ""},
							{"orderIds", orderIds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadBizevents", params);
		}
		// ]]
		try {
			if ((orderIds == null) || (orderIds.isEmpty()) ) {				
				return new ArrayList<BizeventMapEntity>();
			}
			
			final ReadOnlyDao<BizeventMapEntity> bizDao = getDaoProvider().getBizeventMapEntityDao();
			final Filter<BizeventMapEntity> filter = bizDao.createFilter();
			filter.addExpression(Restrictions.in(BizeventMapEntity.ATTR_ORDER_ID, orderIds));
			filter.addExpression(Restrictions.eq(BizeventMapEntity.ATTR_LANG_CD, lang));
			//tourAck event should not be shown on the map (ADN50656)
			filter.addExpression(Restrictions.neq(BizeventMapEntity.ATTR_BIZEVENT_TYPE_CD, Constant.BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK_ACK));
			filter.addExpression(Order.asc(BizeventMapEntity.ATTR_ORDER_ID));
		    filter.addExpression(Order.asc(BizeventMapEntity.ATTR_BIZEVENT_CRE_TS));
			
			return bizDao.findNRecs(first, pagesize, filter);
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
	 * Loads BizeventMapEntity by its id from vehicleCoordinatesBean and language code.
	 * @param
	 * @adnlog.exclude return
	 */
	public BizeventMapEntity loadBizevent(String lang, VehicleCoordinatesBean vehicleCoordinatesBean) {
		// [[
		BizeventMapEntity result = null;
		Object[][] params = {{"lang", lang},
							{"vehicleCoordinatesBean", vehicleCoordinatesBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadBizevent", params);
		}
		// ]]
		try {
			Long bizeventId = vehicleCoordinatesBean.getBizeventId();
			final ReadOnlyDao<BizeventMapEntity> bizeventMapEntityDao = getDaoProvider().getBizeventMapEntityDao();
			final Filter<BizeventMapEntity> filter = bizeventMapEntityDao.createFilter();
			
			filter.addExpression(Restrictions.eq(BizeventMapEntity.ATTR_BIZEVENT_ID, bizeventId));
			filter.addExpression(Restrictions.eq(BizeventMapEntity.ATTR_LANG_CD, lang));
			filter.addExpression(Restrictions.neq(BizeventMapEntity.ATTR_BIZEVENT_TYPE_CD, Constant.BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK_ACK));
			
			List<BizeventMapEntity> bizeventMapEntityList = bizeventMapEntityDao.find(filter);
			if (!bizeventMapEntityList.isEmpty()) {
				result = bizeventMapEntityList.get(0);
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
	 * Loads BizeventMapEntitys by orderId from vehicleCoordinatesBean and language code.
	 * @param
	 * @adnlog.exclude return
	 */
	public List<BizeventMapEntity> loadBizeventsForOrder(String lang, VehicleCoordinatesBean vehicleCoordinatesBean) {
		// [[
		List<BizeventMapEntity> result = null;
		Object[][] params = {{"lang", lang},
				{"vehicleCoordinatesBean", vehicleCoordinatesBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadBizeventsForOrder", params);
		}
		// ]]
		try {
			final ReadOnlyDao<BizeventMapEntity> bizDao = getDaoProvider().getBizeventMapEntityDao();
			final Filter<BizeventMapEntity> filter = bizDao.createFilter();
			filter.addExpression(Restrictions.eq(BizeventMapEntity.ATTR_ORDER_ID, vehicleCoordinatesBean.getOrderId()));
			filter.addExpression(Restrictions.eq(BizeventMapEntity.ATTR_LANG_CD, lang));
			filter.addExpression(Restrictions.neq(BizeventMapEntity.ATTR_BIZEVENT_TYPE_CD, Constant.BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK_ACK));//=confirmed by driver?
			filter.addExpression(Order.asc(BizeventMapEntity.ATTR_BIZEVENT_CRE_TS));

			result = bizDao.find(filter);
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
	 * Loads truck, container, trailer, wagon coordinates from tlgnm_truck_position/
	 * tlgnm_container_position/tlgnm_trailer_position/tlgnm_wagon_position tables for the given orders
	 * @param
	 * @adnlog.exclude return
	 */
	public List<VehicleCoordinatesBean> loadVehicleCoordinates(int first, int pagesize, List<OrderFiltersEntity> filtOrders) {
		// [[
		List<VehicleCoordinatesBean> result = new ArrayList<VehicleCoordinatesBean>();
		Object[][] params = {{"first", first + ""},
							{"pagesize", pagesize + ""},
							{"filtOrders", filtOrders}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadVehicleCoordinates", params);
		}
		// ]]
		try {
			if (filtOrders == null || filtOrders.isEmpty() ) {
				return result;
			}
			
			VehicleCoordinatesBean vehicleCoordinatesBean;
			
			Map<Long, Long> truckOrderMapping = new HashMap<Long, Long>();
			Map<Long, Long> trailerOrderMapping = new HashMap<Long, Long>();
			Map<Long, Long> containerOrderMapping = new HashMap<Long, Long>();
			Map<Long, Long> wagonOrderMapping = new HashMap<Long, Long>();
			
			//creating vehicleCoordinatesBean objects for trucks
			for (OrderFiltersEntity orderFilter : filtOrders) {
				if (orderFilter.getTruckId() != null) {
					truckOrderMapping.put(orderFilter.getTruckId(), orderFilter.getOrderId());
				}
				if (orderFilter.getTrailerId() != null) {
					trailerOrderMapping.put(orderFilter.getTrailerId(), orderFilter.getOrderId());
				}
				if (orderFilter.getContainerId() != null) {
					containerOrderMapping.put(orderFilter.getContainerId(), orderFilter.getOrderId());
				}
				if (orderFilter.getWagonId() != null) {
					wagonOrderMapping.put(orderFilter.getWagonId(), orderFilter.getOrderId());
				}
			}
			
			List<Long> truckIds = new ArrayList<Long>(truckOrderMapping.keySet());
			
			if (truckIds.size() > 0){
				//loading trucks
				List<TruckPositionEntity> truckPositionEntities = loadTruckPositionEntity(first, pagesize, truckIds);
				
				for (TruckPositionEntity truckPositionEntity : truckPositionEntities) {
					vehicleCoordinatesBean = new VehicleCoordinatesBean();
					Long truckId = truckPositionEntity.getTruck().getTruckId();
					vehicleCoordinatesBean.setTruckId(truckId);
					vehicleCoordinatesBean.setCoordLat(truckPositionEntity.getCoordLat());
					vehicleCoordinatesBean.setCoordLon(truckPositionEntity.getCoordLon());
					vehicleCoordinatesBean.setBizeventTypeCd(truckPositionEntity.getBizeventType().getBizeventTypeCd());
					vehicleCoordinatesBean.setBizeventId(truckPositionEntity.getBizeventId());
					vehicleCoordinatesBean.setEventTs(truckPositionEntity.getEventTs());
					vehicleCoordinatesBean.setOrderId(truckOrderMapping.get(truckId));
					
					result.add(vehicleCoordinatesBean);
				}
			}
			
			List<Long> trailerIds = new ArrayList<Long>(trailerOrderMapping.keySet());
			
			if (trailerIds.size() > 0){
			
				//loading trailer
				List<TrailerPositionEntity> trailerPositionEntities = loadTrailerPositionEntity(first, pagesize, trailerIds);
				
				for (TrailerPositionEntity trailerPositionEntity : trailerPositionEntities) {
					vehicleCoordinatesBean = new VehicleCoordinatesBean();
					Long trailerId = trailerPositionEntity.getTrailer().getTrailerId();
					vehicleCoordinatesBean.setTrailerId(trailerId);
					vehicleCoordinatesBean.setCoordLat(trailerPositionEntity.getCoordLat());
					vehicleCoordinatesBean.setCoordLon(trailerPositionEntity.getCoordLon());
					vehicleCoordinatesBean.setBizeventTypeCd(trailerPositionEntity.getBizeventType().getBizeventTypeCd());
					vehicleCoordinatesBean.setBizeventId(trailerPositionEntity.getBizeventId());
					vehicleCoordinatesBean.setEventTs(trailerPositionEntity.getEventTs());
					vehicleCoordinatesBean.setOrderId(trailerOrderMapping.get(trailerId));
					
					result.add(vehicleCoordinatesBean);
				}
			}
			
			List<Long> containerIds = new ArrayList<Long>(containerOrderMapping.keySet());
			
			if (containerIds.size() > 0){

				// loading containers
				List<ContainerPositionEntity> containerPositionEntities = loadContainerPositionEntity(first, pagesize, containerIds);
				
				for (ContainerPositionEntity containerPositionEntity : containerPositionEntities) {
					vehicleCoordinatesBean = new VehicleCoordinatesBean();
					Long containerId = containerPositionEntity.getContainer().getContainerId();
					vehicleCoordinatesBean.setContainerId(containerId);
					vehicleCoordinatesBean.setCoordLat(containerPositionEntity.getCoordLat());
					vehicleCoordinatesBean.setCoordLon(containerPositionEntity.getCoordLon());
					vehicleCoordinatesBean.setBizeventTypeCd(containerPositionEntity.getBizeventType().getBizeventTypeCd());
					vehicleCoordinatesBean.setBizeventId(containerPositionEntity.getBizeventId());
					vehicleCoordinatesBean.setEventTs(containerPositionEntity.getEventTs());
					vehicleCoordinatesBean.setOrderId(containerOrderMapping.get(containerId));
					
					result.add(vehicleCoordinatesBean);
				}
			}
			
			List<Long> wagonIds = new ArrayList<Long>(wagonOrderMapping.keySet());
			
			if (wagonIds.size() > 0){
				//loading wagons
				List<WagonPositionEntity> wagonPositionEntities = loadWagonPositionEntity(first, pagesize, wagonIds);
				
				for (WagonPositionEntity wagonPositionEntity : wagonPositionEntities) {
					vehicleCoordinatesBean = new VehicleCoordinatesBean();
					Long wagonId = wagonPositionEntity.getWagon().getWagonId();
					vehicleCoordinatesBean.setWagonId(wagonId);
					vehicleCoordinatesBean.setCoordLat(wagonPositionEntity.getCoordLat());
					vehicleCoordinatesBean.setCoordLon(wagonPositionEntity.getCoordLon());
					vehicleCoordinatesBean.setBizeventTypeCd(wagonPositionEntity.getBizeventType().getBizeventTypeCd());
					vehicleCoordinatesBean.setBizeventId(wagonPositionEntity.getBizeventId());
					vehicleCoordinatesBean.setEventTs(wagonPositionEntity.getEventTs());
					vehicleCoordinatesBean.setOrderId(wagonOrderMapping.get(wagonId));
					
					result.add(vehicleCoordinatesBean);
				}
			}
			
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private List<TruckPositionEntity> loadTruckPositionEntity(int first, int pagesize, List<Long> truckIdList) {
		// [[
		Object[][] params = {{"first", first + ""},
							{"pagesize", pagesize + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadTruckPositionEntity", params);
		}
		// ]]
		try {
			final ReadOnlyDao<TruckPositionEntity> truckPositionDao = getDaoProvider().getTruckPositionEntityDao();
			final Filter<TruckPositionEntity> filter = truckPositionDao.createFilter();
			
			if (truckIdList != null && !truckIdList.isEmpty()) {
				filter.addExpression(Restrictions.in(TruckPositionEntity.ATTR_TRUCK_ID, truckIdList));
			}
			return truckPositionDao.findNRecs(first, pagesize, filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private List<TrailerPositionEntity> loadTrailerPositionEntity(int first, int pagesize, List<Long> trailerIdList) {
		// [[
		Object[][] params = {{"first", first + ""},
							{"pagesize", pagesize + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadTrailerPositionEntity", params);
		}
		// ]]
		try {
			final ReadOnlyDao<TrailerPositionEntity> trailerPositionDao = getDaoProvider().getTrailerPositionEntityDao();
			final Filter<TrailerPositionEntity> filter = trailerPositionDao.createFilter();
			
			if (trailerIdList != null && !trailerIdList.isEmpty()) {
				filter.addExpression(Restrictions.in(TrailerPositionEntity.ATTR_TRAILER_ID, trailerIdList));
			}
			return trailerPositionDao.findNRecs(first, pagesize, filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private List<ContainerPositionEntity> loadContainerPositionEntity(int first, int pagesize, List<Long> containerIdList) {
		// [[
		Object[][] params = {{"first", first + ""},
							{"pagesize", pagesize + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadContainerPositionEntity", params);
		}
		// ]]
		try {
			final ReadOnlyDao<ContainerPositionEntity> containerPositionDao = getDaoProvider().getContainerPositionEntityDao();
			final Filter<ContainerPositionEntity> filter = containerPositionDao.createFilter();
			
			if (containerIdList != null && !containerIdList.isEmpty()) {
				filter.addExpression(Restrictions.in(ContainerPositionEntity.ATTR_CONTAINER_ID, containerIdList));
			}
			return containerPositionDao.findNRecs(first, pagesize, filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private List<WagonPositionEntity> loadWagonPositionEntity(int first, int pagesize, List<Long> wagonIdList) {
		// [[
		Object[][] params = {{"first", first + ""},
							{"pagesize", pagesize + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadWagonPositionEntity", params);
		}
		// ]]
		try {
			final ReadOnlyDao<WagonPositionEntity> wagonPositionDao = getDaoProvider().getWagonPositionEntityDao();
			final Filter<WagonPositionEntity> filter = wagonPositionDao.createFilter();
			
			if (wagonIdList != null && !wagonIdList.isEmpty()) {
				filter.addExpression(Restrictions.in(WagonPositionEntity.ATTR_WAGON_ID, wagonIdList));
			}
			return wagonPositionDao.findNRecs(first, pagesize, filter);
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
	 * Loads order filters
	 * @param lang language code
	 * @adnlog.exclude return
	 */

	public List<OrderFiltersEntity> loadOrderFilters(String lang, List<Long> orderStatusCdList, Long orderNumber) {
		// [[
		Object[][] params = {{"lang", lang},
							{"orderStatusCdList", orderStatusCdList},
							{"orderNumber", orderNumber}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadOrderFilters", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<OrderFiltersEntity> orderFilterDao = getDaoProvider().getOrderFiltersEntityDao();
			final Filter<OrderFiltersEntity> filter = orderFilterDao.createFilter();
			if (orderNumber != null) {
				filter.addExpression(Restrictions.eq(OrderFiltersEntity.ATTR_ORDER_NUMBER, orderNumber));
			}
			if (lang != null) {
				filter.addExpression(Restrictions.eq(OrderFiltersEntity.ATTR_LANG_CD, lang));
			}
			if ((orderStatusCdList != null) && (!orderStatusCdList.isEmpty())) {
				filter.addExpression(Restrictions.in(OrderFiltersEntity.ATTR_ORDER_STATUS_CD, orderStatusCdList));
			}
			return orderFilterDao.findNRecs(0, 500, filter);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public BizeventMapEntity loadBizeventMapEntity(Long bizeventId, String lang) {
			final ReadOnlyDao<BizeventMapEntity> bizDao = getDaoProvider().getBizeventMapEntityDao();
			final Filter<BizeventMapEntity> filter = bizDao.createFilter();
			filter.addExpression(Restrictions.eq(BizeventMapEntity.ATTR_BIZEVENT_ID, bizeventId));
			filter.addExpression(Restrictions.eq(BizeventMapEntity.ATTR_LANG_CD, lang));
			return bizDao.findSingle(filter);
	}

	/**
	 * Calculate when the order could arrive to shipTo.
	 * @param orderId The order id
	 * @param position The information about the iTrack-equipped Truck
	 * @return The expected/calculated arrivale time to ShipTo
	 */
	public OffsetDateTime calculateArrivalDate(Long orderId, PositionDTO position) {
		ReadWriteDao<OrderEntity> dao = getDaoProvider().getOrderEntityDao();
		OrderEntity orderEntity = dao.findById(orderId);
		if (orderEntity == null) {
			LOG.debug("calculateNextArrivalDate: orderId " + orderId + " matches no order");
			return null;
		}

		OrderStatusBsEntity orderStatus = orderEntity.getOrderStatus();
		if (!ROUTING_RELEVANT_STATUS_CD_SET.contains(orderStatus.getOrderStatusCd())) {
			LOG.debug("calculateNextArrivalDate: order " + orderId + " is in the wrong status " + orderStatus.getOrderStatusCd());
			return null;
		}

		if (!Boolean.TRUE.equals(orderEntity.getITrackAvailableFlag())) {
			// Because we can only call the HereMap API for 250 different trucks per month, we have to limit them to itrack trucks
			LOG.debug("calculateNextArrivalDate: orderId " + orderId + " is not an itrack-equipped");
			return null;
		}

		if (orderEntity.getDeliveryType().getDeliveryTypeCd() != Constant.DELIVERY_TYPE_ROAD_DIRECT) {
			// We only calculate for road direct deliveries
			LOG.debug("calculateNextArrivalDate: orderId " + orderId + " is not a road-direct delivery");
			return null;
		}

		AssignmentEntity assignmentEntity = assignmentStore.findByOrderId(orderId);
		List<AssignmentPartEntity> assignmentParts = assignmentEntity.getAssignmentParts();
		assignmentParts.sort(Comparator.comparing(AssignmentPartEntity::getAssignmentPartSeqNo));

		ArrayList<Via> via = new ArrayList<>();
		AssignmentPartEntity temp = null;

		for (AssignmentPartEntity assignmentPart : assignmentParts) {
			// skip elements that were already started
			if (assignmentPart.getRealStart() != null) {
				continue;
			}

			// these are elements that we have to add to Via

			AssignmentPartTypeBsEntity assignmentPartType = assignmentPart.getAssignmentPartType();
			Long assignmentPartTypeCd = assignmentPartType.getAssignmentPartTypeCd();

			// we have a change, if temp wasn't null and...
			if (temp != null && (
					// the assignment part changed OR
					!Objects.equals(temp.getAssignmentPartType(), assignmentPartType) ||
							// the shippingpoint changed OR
							!Objects.equals(temp.getShippingpoint(), assignmentPart.getShippingpoint()) ||
							// the shipto changed
							!Objects.equals(temp.getShipto(), assignmentPart.getShipto()))) {

				if (Constant.ASSIGNMENT_PART_TYPE_LOAD == temp.getAssignmentPartType().getAssignmentPartTypeCd()) {
					via.add(createViaForLoading(temp, assignmentPart));
				} else
				if (Constant.ASSIGNMENT_PART_TYPE_UNLOAD == temp.getAssignmentPartType().getAssignmentPartTypeCd()) {
					via.add(createViaForUnloading(temp, assignmentPart));
				}
				temp = null;
			}

			if ((Constant.ASSIGNMENT_PART_TYPE_LOAD == assignmentPartTypeCd ||
					Constant.ASSIGNMENT_PART_TYPE_UNLOAD == assignmentPartTypeCd) && temp == null) {
				temp = assignmentPart;
			}

			if (assignmentPart.getOrder() != null && assignmentPart.getOrder().getOrderId().equals(orderId)) {
				// this is the element where it's supposed to arrive, break the loop
				break;
			}
		}

		SapBpEntity shiptoSapBp = orderEntity.getShiptoSapBp();
		ShiptoEntity shiptoEntity = dispokarteService.fetchShiptoBySapId(shiptoSapBp.getSapId());
		if (shiptoEntity == null) {
			LOG.debug("calculateNextArrivalDate: shiptoSapBp " + shiptoSapBp.getSapId() + " has no matching Shipto entity");
			return null;
		}
		if (shiptoEntity.getCoordLon() == null || shiptoEntity.getCoordLat() == null) {
			LOG.debug("calculateNextArrivalDate: shiptoSapBp " + shiptoEntity.getShiptoId() + " has no coords");
			return null;
		}

		return routingServiceBean.calculateArrivalTime(
				new Origin(position.getLatitude(), position.getLongitude(), position.getTime(), null),
				new Target(shiptoEntity.getCoordLat(), shiptoEntity.getCoordLon()),
				via, assignmentEntity, orderEntity);

	}

	private static Via createViaForLoading(AssignmentPartEntity first, AssignmentPartEntity next) {
		return new Via(first.getShippingpoint().getCoordLat(), first.getShippingpoint().getCoordLon(),
				(next.getPlannedStart().getTime() - first.getPlannedStart().getTime()) / 1000);
	}

	private static Via createViaForUnloading(AssignmentPartEntity first, AssignmentPartEntity next) {
		return new Via(first.getShipto().getCoordLat(), first.getShipto().getCoordLon(),
				(next.getPlannedStart().getTime() - first.getPlannedStart().getTime()) / 1000);
	}
}
