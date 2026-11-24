
package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.BizEventUtil;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.AssignStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.PackageTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TrailerTrspCatBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTransportCatBsEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.LoadOrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.UpdateSchedulingRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.UpdateSchedulingResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.VehicleData;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class UpdateSchedulingRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UpdateSchedulingRequestHandler.class);

	private static final String FIELD_LOAD_ORDER_ID = "loadOrderId";

	private static final String FIELD_VEHICLE = "vehicle";

	private static final String FIELD_TRUCK_PLATE_NO = FIELD_VEHICLE + ".truckPlateNo";

	private static final String FIELD_TRAILER_PLATE_NO = FIELD_VEHICLE + ".trailerPlateNo";

	private static final String FIELD_COMMERCIAL_CARRIER = FIELD_VEHICLE + ".commercialCarrier";

	private static final String FIELD_COMMERCIAL_SAP_VENDOR_ID = FIELD_COMMERCIAL_CARRIER + ".sapVendorId";

	@EJB
	private DispoServiceLocalInterface dispoService;

	@EJB
	private MonitoringTablesServiceLocalInterface monitoringTablesService;


	@Override
	protected void validate(LogonEventRequest eventBean) {
		final UpdateSchedulingRequest request = (UpdateSchedulingRequest) eventBean;
		String inputLoadOrderId = request.getLoadOrderId();
		validateMandatory(FIELD_LOAD_ORDER_ID, inputLoadOrderId);
		VehicleData inputVehicle = request.getVehicle();
		validateMandatory(FIELD_VEHICLE, inputVehicle);
		String inputTruckPlateNumber = inputVehicle.getTruckPlateNo();
		validateMandatory(FIELD_TRUCK_PLATE_NO, inputTruckPlateNumber);
	}

	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {

		final UpdateSchedulingRequest request = (UpdateSchedulingRequest) eventBean;
		final UpdateSchedulingResponse response = createLogonOKEventResponse(UpdateSchedulingResponse.class);

		final VehicleData vehicle = request.getVehicle();

		TruckEntity newTruck;
		TrailerEntity trailer = null;

		// Validate truck
		newTruck = getTruck(vehicle, FIELD_VEHICLE, false);
		String truckPlateNumber = vehicle.getTruckPlateNo();
		if (newTruck == null) {
			throw createSchedulingError(String.format(
					"Cannot find an active truck with truckPlateNo = %s, updateScheduling not possible.",
					truckPlateNumber));
		}

		if (LogonUtil.isPlanningTruck(newTruck)) {
			throw createSchedulingError(String.format(
					"Received a planning truck (truckPlateNo = %s), updateScheduling not possible.",
					truckPlateNumber));
		}

		// Validate trailer
		String trailerPlateNumber = vehicle.getTrailerPlateNo();
		if (StringUtils.isNotEmpty(trailerPlateNumber)) {
			trailer = getTrailer(trailerPlateNumber, FIELD_TRAILER_PLATE_NO, false);
			if (trailer == null) {
				throw createSchedulingError(String.format(
						"Cannot find an active trailer with trailerPlateNo = %s, updateScheduling not possible.",
						trailerPlateNumber));
			}
			if (LogonUtil.isPlanningTrailer(trailer)) {
				throw createSchedulingError(String.format(
						"Received a planning trailer (trailerPlateNo = %s), updateScheduling not possible.",
						trailerPlateNumber));
			}
		}

		// Check composition and update truck-trailer link if required
		if (needsTrailer(newTruck)) {
			if (trailer != null) {
				final BizeventEntity bizevent =
						createBizevent(Constant.BIZEVENT_TYPE_TRUCK_COMPOSITION_CHANGED, request.getEventTime(), true);
				bizevent.setTruck(newTruck);
				bizevent.setTrailer(trailer);
				try {
					truckAdminService.updateTruck(newTruck, trailer, null, false);
				}
				catch (EJBException ejbException) {
					Exception cause = ejbException.getCausedByException();
					if (cause instanceof LogonException) {
						String message = cause.getMessage();
						throw createSchedulingError(message);
					}
					throw ejbException;
				}
				catch (LogonException logonException) {
					String message = logonException.getMessage();
					throw createSchedulingError(message);
				}
			}
			else {
				trailer = truckAdminService.fetchTrailerForTruck(newTruck.getTruckId(), true);
				if (trailer == null) {
					throw createSchedulingError(String.format("The truck (truckPlateNo = %s) needs a trailer, " +
									" but does not have one linked and none has been received.",
							truckPlateNumber));
				}
			}
		}
		else {
			if (trailer != null) {
				throw newApplicationError(String.format(
						"The truck (truckPlateNo = %s) cannot have a trailer, but trailer (trailerPlateNo = %s) has been "
								+ "received.",
						truckPlateNumber, trailerPlateNumber));
			}
		}

		String loadOrderId = request.getLoadOrderId();
		AssignmentEntity assignment = getAssignment(loadOrderId, FIELD_LOAD_ORDER_ID);
		Long assignmentId = assignment.getAssignmentId();

		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);

		OrderEntity firstOrder;
		OrderStatusBsEntity firstOrderStatus;
		if (!orders.isEmpty()) {
			firstOrder = orders.get(0);
			firstOrderStatus = firstOrder.getOrderStatus();
		}
		else {
			throw new LogonException("No orders found for assignment " + assignmentId, Reason.RES_ERROR_IMPL);
		}

		// Check composition against logon package type
		MaterialPackageBsEntity materialPackage = AssignmentUtils.getMaterialPackage(assignment);
		PackageTypeBsEntity logonPackageType = materialPackage.getLogonPackageType();
		Long packageTypeCode = logonPackageType.getPackageTypeCd();

		ContainerEntity container = fetchContainerForTruckOrTrailer(newTruck, trailer);

		if (!LogonUtil.isTruckCompositionValidForMaterialPackage(packageTypeCode, newTruck, trailer, container)) {
			throw newApplicationError(String.format("The composition (%s, %s) is invalid for material package '%s'",
					newTruck.getTruckPlateNo(),
					(trailer != null ? trailer.getTrailerPlateNo() : ""),
					materialPackage.getMaterialPackageCd()));
		}

		TruckEntity originalTruck = assignment.getTruck();
		CarrierEntity commercialCarrier = null;

		if (!ObjectUtils.equals(originalTruck, newTruck)) {

			if (!LogonUtil.isPlanningTruck(originalTruck)) {
				throw newApplicationError(String.format(
						"Assignment has a real truck (loadOrderId = %s).",
						assignment.getLoadOrderId()));
			}

			try {
				// determine the commercial carrier in case of generic carrier
				if (originalTruck.getCarrier().getCarrierId().equals(Constant.ARTIFICIAL_GENERIC_CARRIER_ID)) {
					if (vehicle.getCommercialCarrier() != null && vehicle.getCommercialCarrier().getSapVendorId() != null) {
						commercialCarrier = getCommercialCarrier(vehicle.getCommercialCarrier().getSapVendorId(),
								FIELD_COMMERCIAL_SAP_VENDOR_ID);
					}
					else {
						CarrierEntity executingCarrier = newTruck.getCarrier();
						if (Boolean.TRUE.equals(executingCarrier.getSapVendor().getVnIsCommCarrier())) {
							commercialCarrier = executingCarrier;
						}
						else {
							final String msg = String.format(
									"updateScheduling failed (commercial carrier not received and truck owner '%s' is not a "
											+ "commercial carrier)",
									executingCarrier.getDisplayName());
							LOG.error(msg);
							throw newApplicationError(msg);
						}
					}
				}

				/**
				 * The assignment can get into this status if someone deallocates on the Scheduling screen from the planning
				 * truck.
				 * The purpose of deallocating is to transmit to the executing carrier, who can set the real truck within Logon.
				 * In the meantime LDS can anytime call updateScheduling, and it has to activate the assignment belonging to the
				 * order.
				 */
				AssignStatusBsEntity assignmentStatus = assignment.getAssignStatus();
				Long assignStatusCode = assignmentStatus.getAssignStatusCd();
				if (assignStatusCode == Constant.ASSIGNMENT_STATUS_RELEASED_BY_PRESCHEDULER) {

					// does not send the update (loadOrderNotification) to the LDS
					if (orders.size() > 1) {
						throw new LogonException("LDS does not support tours [assignmentId=" + assignmentId + "]",
								Reason.RES_ERROR_IMPL);
					}

					if (LogonStatusUtils.isBeforeDispatched(firstOrderStatus)) {
						final BizeventEntity event = new BizeventEntity();
						event.setBizeventType(BizEventUtil.BIZEVENT_TYPE_DISPATCHED);
						event.setOrder(firstOrder);
						Date now = new Date();
						event.setEventTs(now);

						monitoringTablesService.processBizevent(event);

						orderService.changeOrderStatus(firstOrder, Constant.ORDER_STATUS_DISPATCHED);
					}
				}
				assignment = orderService.moveAssignment(newTruck.getTruckId(), assignmentId);
				assignment.setManuallyScheduled(Boolean.FALSE);


				// update commercial carrier in case of generic carrier
				if (originalTruck.getCarrier().getCarrierId().equals(Constant.ARTIFICIAL_GENERIC_CARRIER_ID)) {
					for (OrderEntity order : orders) {
						order.setCommCarrier(commercialCarrier);
					}
				}

				// TODO 25-Sep-2012/stich: Should be the correct source (the sending weighing system) instead of
				//  WEIGH_SYSTEM_SP_NONE
				//                          nevertheless, that argument is not used later one so fine for now
				dispoService.transmitToDriver(assignmentId,
						Constant.WEIGH_SYSTEM_SP_NONE,
						Constant.WEIGH_SYSTEM_LP_WS,
						BizEventUtil.BIZEVENT_TYPE_UPDATED_SCHEDULING);

				orderService.changeOrderStatus(firstOrder, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);
				createBizEventArrivedAtLoadingPointForOrders(orders, assignment, newTruck, eventBean.getEventTime());
			}
			catch (LogonException logonException) {
				final String message =
						String.format("UpdateScheduling failed ('%s', loadOrderId = %s).", logonException.getMessage(),
								assignment.getLoadOrderId());
				LOG.error(message, logonException);
				throw newApplicationError(message);
			}
		}
		else if (LogonStatusUtils.isBeyondDispatched(firstOrderStatus) && LogonStatusUtils
				.isBeforeArrivedAtLoadingPoint(firstOrderStatus)) {
			orderService.changeOrderStatus(firstOrder, Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);
			createBizEventArrivedAtLoadingPointForOrders(orders, assignment, newTruck, eventBean.getEventTime());
		}

		for (OrderEntity order : orders) {
			// publish all associated orders
			kafkaPublisherService.publishLDSEvent(order, request, "updateScheduling");
		}

		LoadOrderData loadOrderData = ldsService.createLoadOrderData(assignment);
		response.setLoadOrder(loadOrderData);

		return response;
	}

	private void createBizEventArrivedAtLoadingPointForOrders(List<OrderEntity> orders, AssignmentEntity assignment,
			TruckEntity truck, Date eventTime) {
		/* For most of the assignments this bizevent ArrivedAtLoadingpoint will be created by other means earlier.
		 * Mobile: reaching the geofence for the shippingpoint, Rail: Login from Parallel, Scheduling screen: enter
		 * shippingpoint button
		 * When the above ones doesn't happened, this far the StartLoadRequest created the bizevent with the same timestamp as
		 * the StartLoad.
		 *
		 * We want to make sure that the ArrivedAtLoadingpoint bizevent is created as earliest as possible, based on real events.
		 *
		 * At the point of order finalization we where the bizevents are really used for reporting purpose,
		 * we will take always the first among the many ArrivedAtLoadingpoint timestamps, so it's not really a problem if we
		 * create an extra one here
		 */
		for (OrderEntity order : orders) {
			BizeventEntity bizArrivedAtLoadingpoint = new BizeventEntity();
			bizArrivedAtLoadingpoint.setBizeventType(BizEventUtil.BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT);
			bizArrivedAtLoadingpoint.setTruck(truck);
			bizArrivedAtLoadingpoint.setAssignment(assignment);
			bizArrivedAtLoadingpoint.setOrder(order);
			bizArrivedAtLoadingpoint.setEventTs(eventTime);
			monitoringTablesService.processBizevent(bizArrivedAtLoadingpoint);
		}
	}

	private ContainerEntity fetchContainerForTruckOrTrailer(TruckEntity truck, TrailerEntity trailer) {
		ContainerEntity container = null;
		Long truckTrspCd = null;
		if (truck != null) {
			TruckTransportCatBsEntity truckTransportCat = truck.getTruckTransportCat();
			if (truckTransportCat != null) {
				truckTrspCd = truckTransportCat.getTruckTransportCatCd();
			}
		}
		Long trailerTrspCd = null;
		if (trailer != null) {
			TrailerTrspCatBsEntity trailerTrspCat = trailer.getTrailerTrspCat();
			if (trailerTrspCat != null) {
				trailerTrspCd = trailerTrspCat.getTrailerTrspCatCd();
			}
		}

		// take care of autoboxing
		if ((trailerTrspCd != null && trailerTrspCd == Constant.TRAILER_TKATEGORIE_CONTAINER) ||
				(truckTrspCd != null && truckTrspCd == Constant.TRUCK_TRSP_CAT_CONTAINER)) {
			container = truckAdminService.fetchContainerForTruckTrailerComposition(truck, trailer);
		}

		return container;
	}

	private static boolean needsTrailer(TruckEntity truck) {
		TruckTransportCatBsEntity transportCategory = truck.getTruckTransportCat();
		Long transportCategoryCode = transportCategory.getTruckTransportCatCd();
		return (ObjectUtils.equals(transportCategoryCode, Constant.TRUCK_TRSP_CAT_TRAILER));
	}
}
