
package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.AssignmentServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.CarrierServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.PackageTypeTrspCatMapping;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderTruckTypeEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.PackageTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TrailerTrspCatBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTransportCatBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ScheduleAssignmentRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ScheduleAssignmentResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.VendorData;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class ScheduleAssignmentRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ScheduleAssignmentRequestHandler.class);

	private static final String PARTFIELD_SAP_VENDOR_ID = ".sapVendorId";

	private static final String FIELD_PLANNED_LOADING = "plannedLoading";

	private static final String FIELD_VEHICLE = "vehicle";

	private static final String FIELD_VEHICLE_COMMCARRIER = FIELD_VEHICLE + ".commercialCarrier";

	private static final String FIELD_VEHICLE_COMMCARRIER_SAP_VENDOR_ID = FIELD_VEHICLE_COMMCARRIER + PARTFIELD_SAP_VENDOR_ID;

	private static final String FIELD_VEHICLE_EXECCARRIER = FIELD_VEHICLE + ".executingCarrier";

	private static final String FIELD_VEHICLE_EXECCARRIER_SAP_VENDOR_ID = FIELD_VEHICLE_EXECCARRIER + PARTFIELD_SAP_VENDOR_ID;

	private static final String FIELD_VEHICLE_TRUCK_PLATE_NO = FIELD_VEHICLE + ".truckPlateNo";

	private static final String FIELD_LOGON_ORDER_NO = "logonOrderNo";

	@EJB
	private CarrierServiceLocalInterface carrierService;

	@EJB
	private DispoServiceLocalInterface dispoService;

	@EJB
	private AssignmentServiceLocalInterface assignmentService;

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final ScheduleAssignmentRequest request = (ScheduleAssignmentRequest) eventBean;

		validateMandatory(FIELD_CLIENT_TYPE, request.getClientType());
		validateMandatory(FIELD_PLANNED_LOADING, request.getPlannedLoading());
		validateMandatory(FIELD_VEHICLE, request.getVehicle());
		validateMandatory(FIELD_VEHICLE_COMMCARRIER, request.getVehicle().getCommercialCarrier());
		validateMandatory(FIELD_VEHICLE_COMMCARRIER_SAP_VENDOR_ID, request.getVehicle().getCommercialCarrier().getSapVendorId());

		if (request.getVehicle().getExecutingCarrier() != null) {
			validateMandatory(FIELD_VEHICLE_EXECCARRIER_SAP_VENDOR_ID,
					request.getVehicle().getExecutingCarrier().getSapVendorId());
		}

		validateMandatory(FIELD_VEHICLE_TRUCK_PLATE_NO, request.getVehicle().getTruckPlateNo());
		validateMandatory(FIELD_LOGON_ORDER_NO, request.getLogonOrderNo());
	}

	@Override
	@AdnTrace
	protected LogonEventResponse perform(LogonEventRequest eventBean) {

		final ScheduleAssignmentRequest request = (ScheduleAssignmentRequest) eventBean;
		final ScheduleAssignmentResponse response = createLogonOKEventResponse(ScheduleAssignmentResponse.class);

		LOG.debug("Fetching order by order number {}", request.getLogonOrderNo());
		// this method makes sure that the order exists
		final OrderEntity order = getOrder(request.getLogonOrderNo(), FIELD_LOGON_ORDER_NO);
		Long orderId = order.getOrderId();

		// make sure the order is in the correct status
		OrderStatusBsEntity orderStatus = order.getOrderStatus();
		final Long orderStatusCd = orderStatus.getOrderStatusCd();
		if (!LogonStatusUtils.isBeforeDispatched(orderStatusCd)) {
			LOG.info("Order with id {} is in status {}, cannot be scheduled", orderId, orderStatusCd);
			throw newApplicationError("Order is in status " + orderStatusCd + ", cannot be scheduled");
		}

		// fetch existing assignment of the order
		Long existingAssignmentId = assignmentService.findActiveAssignmentIdForOrderId(order.getOrderId());
		if (existingAssignmentId != null) {
			LOG.info("Order with id {} already has an assignment with id {}, cannot be scheduled", orderId,
					existingAssignmentId);
			throw newApplicationError("Order already has an assignment, cannot be scheduled");
		}

		// Order to truck assignment has to be checked before the order is changed
		LOG.debug("Fetching truck by vehicle plate number {} and vehicle sap number {}", request.getVehicle().getTruckPlateNo(),
				request.getVehicle().getSapTruckNo());
		final TruckEntity truck = getTruck(request.getVehicle(), FIELD_VEHICLE, true);

		final CarrierEntity commCarrier = checkAndUpdateCommCarrier(order, request.getVehicle().getCommercialCarrier());
		checkAndUpdateExecCarrier(order, request.getVehicle().getExecutingCarrier(), commCarrier, truck);

		// calculate the loading time
		LOG.debug("Calculating load duration for order id {}", orderId);
		final Long loadDuration = order.getOrderItems().stream().mapToLong(OrderItemEntity::getPlannedLoadingTimeMin).sum();

		final Date startTime = calculateStartTime(request.getPlannedLoading(), loadDuration, order);

		assertOrderCanGoOnTruck(order, truck);

		LOG.debug("Creating assignment for order id {} and truck id {}", orderId, truck.getTruckId());
		orderService.createAssignmentForTruckWithOrderDispatched(truck.getTruckId(),
				order, startTime, TimeZone.getDBTimeZone(),
				loadDuration.intValue(), order.getTransportDurationMin().intValue(),
				order.getPlannedUnloadingTimeMin().intValue());

		// This is a really, really ugly hack, but we need it.
		// This seemingly get method updates the assignment, because it's travel distance is not calculated right after
		// assignment
		// And because this is the only place where this logic exists and highly tied to some view and wrapper objects,
		// I've decided to just call the same method that the UI would call, just with more restricted parameters
		LOG.debug("Fetching and updating assignment for order id {} and truck id {}", orderId, truck.getTruckId());
		dispoService.getAssignmentList(Locale.ENGLISH.getLanguage(), Collections.singletonList(commCarrier.getCarrierId()),
				Collections.singletonList(truck.getTruckId()), null, null, null, startTime, Locale.ENGLISH,
				TimeZone.getDBTimeZone(), order.getOrderNumber(), true);

		return response;
	}

	private static Date calculateStartTime(OffsetDateTime plannedLoading, Long loadDuration, OrderEntity order) {
		// We manually convert the incoming date with offset to the DB's timezone, because we cannot convert just an offset to
		// a zone id, so we manually convert the incoming date to DB's timezone
		final ZoneId dbZoneId = TimeZone.getDBTimeZone().getZoneId();
		final ZonedDateTime plannedLoadingInDBTime = plannedLoading.atZoneSameInstant(dbZoneId);
		Date startTime = Date.from(plannedLoadingInDBTime.toInstant());
		LOG.debug("Converted incoming date {} to timezone {}, resulted in {}", plannedLoading, dbZoneId, startTime);

		// make sure the planned loading start time + loading time + transport duration is between the order delivery start and
		// order delivery end
		final long loadPlusDeliveryTime = loadDuration + order.getTransportDurationMin();
		final Date deliveryStartTime = Date.from(plannedLoadingInDBTime.plusMinutes(loadPlusDeliveryTime).toInstant());
		Date adjustedDeliveryStartTime = null;
		if (deliveryStartTime.before(order.getDeliveryFromDat())) {
			adjustedDeliveryStartTime = order.getDeliveryFromDat();
		}
		else if (deliveryStartTime.after(order.getDeliveryToDat())) {
			adjustedDeliveryStartTime = order.getDeliveryToDat();
		}

		if (adjustedDeliveryStartTime != null) {
			Date adjustedStartTime = Date.from(ZonedDateTime.ofInstant(adjustedDeliveryStartTime.toInstant(), dbZoneId)
					.minusMinutes(loadPlusDeliveryTime).toInstant());
			LOG.info("Adjusted incoming loading date from {} to {} for order with id {}, because the calculated delivery date "
					+ "didn't fit the order's delivery windows", startTime, adjustedStartTime, order.getOrderId());
			startTime = adjustedStartTime;
		}
		return startTime;
	}

	private CarrierEntity checkAndUpdateCommCarrier(OrderEntity order, VendorData commCarrierData) {
		// Update the carriers before the assignment, so that the executing carrier is set correctly, if it wasn't before.
		final String commCarrierSapVendorId = commCarrierData.getSapVendorId();
		LOG.debug("Fetching commercial carrier with SapVendorId {}", commCarrierSapVendorId);
		final CarrierEntity commCarrier = getCommercialCarrier(commCarrierSapVendorId, FIELD_VEHICLE_COMMCARRIER_SAP_VENDOR_ID);
		assertCarrierInPool(order, commCarrier, false);

		LOG.debug("Updating order with id {} to commercial carrier with id {}", order.getOrderId(), commCarrier.getCarrierId());
		dispoService.setOrderCommCarrier(order, commCarrier);

		return commCarrier;
	}

	private CarrierEntity checkAndUpdateExecCarrier(OrderEntity order, VendorData execCarrierData, CarrierEntity commCarrier,
			TruckEntity truck) {
		final CarrierEntity execCarrier;

		if (execCarrierData != null) {
			final String execCarrierSapVendorId = execCarrierData.getSapVendorId();
			LOG.debug("Fetching executing carrier with SapVendorId {}", execCarrierSapVendorId);
			execCarrier = getExecutingCarrier(execCarrierSapVendorId, FIELD_VEHICLE_EXECCARRIER_SAP_VENDOR_ID);
		}
		else if (!LogonUtil.isPlanningTruck(truck)) {
			// real truck, use the truck's carrier as exec carrier
			execCarrier = truck.getCarrier();
		}
		else {
			// it's a planning truck, exec carrier is null
			execCarrier = null;
		}

		if (execCarrier != null) {
			assertCarrierInPool(order, execCarrier, true);

			// make sure the commercial carrier and executing carrier have a connection
			if (!carrierService.checkIfCommCarrierAndExecCarrierArePartner(commCarrier.getCarrierId(),
					execCarrier.getCarrierId())) {
				LOG.info("In order with id {} the selected commercial carrier with id {} and executing carrier with id {} are "
						+ "not partners", order.getOrderId(), commCarrier.getCarrierId(), execCarrier.getCarrierId());
				throw newApplicationError("The selected commercial carrier and executing carrier are not partners");
			}
		}

		LOG.debug("Updating order with id {} to executing carrier with id {}", order.getOrderId(),
				execCarrier != null ? execCarrier.getCarrierId() : null);
		dispoService.setOrderExecCarrier(order, execCarrier);

		return execCarrier;
	}

	private void assertOrderCanGoOnTruck(OrderEntity order, TruckEntity truck) {
		// in dispo2.js there are different rules about which order can go on which truck
		// but only a subset of the rules are implemented here since this WS can only be called once for an order

		if (order.getCommCarrier() != null && order.getExecCarrier() == null &&
				!equals(order.getCommCarrier(), truck.getCarrier()) &&
				(truck.getDispatchingCarrier() == null || !equals(truck.getDispatchingCarrier(), order.getCommCarrier()))) {
			// this has to be a planning truck, otherwise exec carrier would be set to the truck's carrier

			// Rule 4: there is no planning yet crh planner has to plan first on planning trucks of the commercial carrier
			// only those trucks are allowed for which the carrier equals with the commercial carrier id,
			// the only case when it can differ is if the truck is lent to another carrier (dispatching carrier is set for
			// the
			// truck and corresponds with the order's carrier)
			LOG.info("In order with id {} the truck doesn't belong to the comm carrier with id {}", order.getOrderId(),
					order.getCommCarrier().getCarrierId());
			throw newApplicationError("Commercial carrier doesn't match the selected planning truck's carrier");
		}
		else if (order.getCommCarrier() != null && order.getExecCarrier() != null &&
				!equals(order.getExecCarrier(), truck.getCarrier()) && (truck.getDispatchingCarrier() == null ||
				!equals(truck.getDispatchingCarrier(), order.getExecCarrier()))) {
			// Rule 6: if executing carrier attribute is set, only those trucks are allowed for which the carrier equals with the
			// executing carrier id, the only case when it can differ is if the truck is lent to another carrier (dispatching
			// carrier is set for the truck and corresponds with the order's executing carrier)
			LOG.info("In order with id {} the truck doesn't belong to the exec carrier with id {}", order.getOrderId(),
					order.getExecCarrier().getCarrierId());
			throw newApplicationError("Executing carrier doesn't match the selected truck's carrier");
		}

		// Rule 7: If the truck transport category is trailer, then it must have a trailer attached.
		Long transportCatCd =
				Optional.of(truck).map(TruckEntity::getTruckTransportCat).map(TruckTransportCatBsEntity::getTruckTransportCatCd)
						.orElseThrow(() -> newApplicationError("Truck has no transport category"));

		TrailerEntity trailer = null;
		LOG.debug("Truck with id {} has transportCatCd {}", truck.getTruckId(), transportCatCd);
		if (transportCatCd == Constant.TRUCK_TRSP_CAT_TRAILER) {
			// this truck has a trailer, fetch the trailer
			trailer = truckAdminService.fetchTrailerForTruck(truck.getTruckId(), true);
			if (trailer == null) {
				throw newApplicationError("Truck needs a trailer, but has no trailer assigned to it");
			}
			transportCatCd =
					Optional.of(trailer).map(TrailerEntity::getTrailerTrspCat).map(TrailerTrspCatBsEntity::getTrailerTrspCatCd)
							.orElseThrow(() -> newApplicationError("Trailer has no transport category"));
			LOG.debug("Trailer with id {} has transportCatCd {}", trailer.getTrailerId(), transportCatCd);
		}

		// Rule 8: For container orders the truck/trailer transport category must be 2 (Container).
		if (transportCatCd != Constant.TRUCK_TRSP_CAT_CONTAINER) {
			boolean isContainerOnly =
					order.getOrderItems().stream().map(OrderItemEntity::getOrderedUom).map(UomBsEntity::getUomCd).reduce(
							Boolean.FALSE, (actual, it) -> {
								// logic from OrderWrapper, order of the items matter!!!!!
								if (Constant.UOM_CONTAINER.equals(it)) {
									return Boolean.TRUE;
								}
								else if (!Constant.UOM_TONS.equals(it)) {
									return Boolean.FALSE;
								}
								return actual;
							}, (actual, it) -> it);
			LOG.debug("Order's isContainerOnly is {}", isContainerOnly);
			if (isContainerOnly) {
				throw newApplicationError("Order has only container items, but the transport is not a container");
			}
		}

		// LOGON-4983 Scheduling: Additional Validation Logic based on order truck type restriction Disable those
		// trucks which truck type is not "compatible" with the dragged order. LOGON-5992 Scheduling screen - drag&drop an
		// order doesn't respect restrictions This check applied for unallocated orders and assignments as well, because
		// not yet released assignment can be dragged to another truck.
		List<OrderTruckTypeEntity> orderVehicleTypes = order.getOrderVehicleTypes();
		if (orderVehicleTypes.stream()
				.map(it -> it.getTruckType().getTruckTypeCd())
				.noneMatch(it -> it.equals(truck.getTruckType().getTruckTypeCd()))) {
			throw newApplicationError("The order cannot be scheduled for the given truck");
		}

		// LOGON-5043/LOGON-5558: Package type code must match transport code LOGON-5992 Scheduling screen - drag&drop an
		// order doesn't respect restrictions This check applied for unallocated orders and assignments as well, because
		// not yet released assignment can be dragged to another truck.
		Long logonPackageType = order.getOrderItems().stream()
				.map(OrderItemEntity::getSapMaterial).filter(Objects::nonNull)
				.map(SapMaterialEntity::getMaterialPackage).filter(Objects::nonNull)
				.map(MaterialPackageBsEntity::getLogonPackageType).filter(Objects::nonNull)
				.map(PackageTypeBsEntity::getPackageTypeCd)
				.findFirst().orElse(null);
		Map<Long, Set<Long>> packageTypeMap =
				trailer != null ? PackageTypeTrspCatMapping.getTrailerTrspCatForAllPackageTypes() :
						PackageTypeTrspCatMapping.getTruckTrspCatForAllPackageTypes();
		Set<Long> transportCatCds = packageTypeMap.get(logonPackageType);
		if (transportCatCds == null || !transportCatCds.contains(transportCatCd)) {
			throw newApplicationError("Package type code must match transport code.");
		}
	}

	private void assertCarrierInPool(OrderEntity order, CarrierEntity carrier, boolean isExecCarrier) {
		final PoolEntity pool = order.getPool();
		if (carrier.getPoolEntityList().stream().noneMatch(it -> it.getPoolId().equals(pool.getPoolId()))) {
			throw newApplicationError("The selected " + (isExecCarrier ? "executing" : "commercial") +
					" carrier is not in the order's own pool");
		}
	}

	private static <T, R> boolean equals(T o1, T o2, Function<T, R> idFunction) {
		if (o1 == null) {
			return o2 == null;
		}
		if (o2 == null) {
			return false;
		}
		return Objects.equals(idFunction.apply(o1), idFunction.apply(o2));
	}

	private static boolean equals(CarrierEntity o1, CarrierEntity o2) {
		return equals(o1, o2, CarrierEntity::getCarrierId);
	}
}
