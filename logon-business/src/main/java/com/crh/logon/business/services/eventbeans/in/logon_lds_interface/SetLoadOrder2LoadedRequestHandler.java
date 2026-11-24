/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.EndLoadingServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.OrderUtils;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.FirstWeighingData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.OrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SecondWeighingData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SetLoadOrder2LoadedRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SetLoadOrder2LoadedResponse;
import org.apache.commons.lang.StringUtils;

/**
 * Request handler for a simplified endLoad, which uses the same loaded quantity as the ordered quantity. Runs
 * orderServiceBean.endLoad on the order determined by the loadOrderId given in the request.
 *
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class SetLoadOrder2LoadedRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SetLoadOrder2LoadedRequestHandler.class);

	private static final String FIELD_VEHICLE = "vehicle";

	private static final String FIELD_LOAD_ORDER_ID = "loadOrderId";

	@SuppressWarnings("unused")
	private static final String FIELD_FIRST_WEIGHING = "firstWeighing";

	private static final String FIELD_SECOND_WEIGHING = "secondWeighing";

	private static final String FIELD_SECOND_WEIGHING_W2UOM = "secondWeighingData.w2UoM";

	private static final String FIELD_FIRST_WEIGHING_W1WEIGHING_POINT_NO = "firstWeighingData.w1WeighingPointNo";

	private static final String FIELD_FIRST_WEIGHING_W1UOM = "firstWeighingData.w1UoM";

	private static final String FIELD_SECOND_WEIGHING_W2WEIGHING_POINT_NO = "secondWeighingData.w2WeighingPointNo";

	@EJB
	private EndLoadingServiceLocalInterface endLoadingService;

	@Override
	protected void validate(LogonEventRequest eventBean) {
		// [[
		Object[][] params = { { "eventBean", eventBean } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {
			final SetLoadOrder2LoadedRequest request = (SetLoadOrder2LoadedRequest) eventBean;

			validateMandatory(FIELD_VEHICLE, request.getVehicle());
			validateMandatory(FIELD_LOAD_ORDER_ID, request.getLoadOrderId());
			validateMandatory(FIELD_SECOND_WEIGHING, request.getSecondWeighing());

			final AssignmentEntity assignment = getAssignment(request.getLoadOrderId(),
					FIELD_LOAD_ORDER_ID);

			verifyNoPlanning(assignment);

			//in case of more kinds of material there is more than 1 loads to the assignment
			List<LoadEntity> loads = AssignmentUtils.getLoadsFromAssignment(assignment);
			for (LoadEntity load : loads) {
				//order must be a bag order
				//in case of tour there are more than 1 orders linked
				final Collection<OrderItemEntity> orderItems = orderService.getOrderItems(load);
				for (OrderItemEntity orderItem : orderItems) {
					OrderEntity order = orderItem.getOrder();
					if (!LogonUtil.isBagOrder(orderItem.getOrder())) {
						throw newApplicationError(String.format(
								"Order must be a bag order: %d.",
								order.getOrderNumber()));
					}
				}

				//order must be scheduled on a real truck (not planning truck) or is a pickup order
				if (LogonUtil.isPlanningTruck(load.getTruck())) {
					for (OrderItemEntity orderItem : orderItems) {
						OrderEntity order = orderItem.getOrder();
						if (!LogonUtil.isPickupOrder(order.getDeliveryType().getDeliveryTypeCd())) {
							throw newApplicationError(String.format(
									"Order must be scheduled on a real truck (not planning truck) or is a pickup order: %d.",
									order.getOrderNumber()));
						}
					}
				}

				//for the moment no tours are supported
				//the code is prepared for tours, only the validation should be removed, when it becomes supported
				Set<OrderEntity> orders = new HashSet<>();
				for (OrderItemEntity orderItem : orderItems) {
					OrderEntity order = orderItem.getOrder();
					orders.add(order);
				}

				//allowed order statuses: at least 'Released for execution', up to 'Arrived at unloading point'
				for (OrderEntity order : orders) {
					if (!OrderUtils.isInEndloadableState(order)) {
						throw newApplicationError(
								"Order must be at least in 'Released for execution' state, but up to 'Arrived at unloading point'");
					}
				}
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(null);
			}
			// ]]
		}
	}

	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {
		// [[
		LogonEventResponse result = null;
		Object[][] params = { { "eventBean", eventBean } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "perform", params);
		}
		// ]]
		try {
			final SetLoadOrder2LoadedRequest request = (SetLoadOrder2LoadedRequest) eventBean;
			final SetLoadOrder2LoadedResponse response = createLogonOKEventResponse(SetLoadOrder2LoadedResponse.class);

			final AssignmentEntity assignment = getAssignment(request.getLoadOrderId(),
					FIELD_LOAD_ORDER_ID);

			final FirstWeighingData firstWeighingData = request.getFirstWeighing();
			final SecondWeighingData secondWeighingData = request.getSecondWeighing();

			final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
			final List<LoadEntity> loads = AssignmentUtils.getLoadsFromAssignment(assignment);
			final OrderEntity firstOrder = orders.get(0);
			final ShippingpointEntity sp = firstOrder.getShippingpoint();

			final LoadingpointEntity startLoadingpoint;
			final LoadingpointEntity endLoadingpoint;

			// start loading point
			if (firstWeighingData != null
					&& StringUtils.isNotEmpty(firstWeighingData.getW1WeighingPointNo())) {
				startLoadingpoint = getLoadingpoint(sp.getPlant().getPlantCd(),
						firstWeighingData.getW1WeighingPointNo());
			}
			else {
				startLoadingpoint = null;
			}

			// end loading point
			if (secondWeighingData != null
					&& StringUtils.isNotEmpty(secondWeighingData.getW2WeighingPointNo())) {
				endLoadingpoint = getLoadingpoint(sp.getPlant().getPlantCd(),
						secondWeighingData.getW2WeighingPointNo());
			}
			else {
				endLoadingpoint = null;
			}

			// validation
			for (LoadEntity load : loads) {
				if (startLoadingpoint == null && endLoadingpoint == null
						&& load.getLoadingpoint() == null) {
					throw newApplicationError(String.format(
							"Loadingpoint cannot be identified (no StartLoading has been issued previously, and both %s and %s "
									+ "are empty).", FIELD_SECOND_WEIGHING_W2WEIGHING_POINT_NO,
							FIELD_FIRST_WEIGHING_W1WEIGHING_POINT_NO));
				}
			}

			// tare weight
			BigDecimal tareWeightT = null;
			if (firstWeighingData != null) {
				final BigDecimal tareWeight = firstWeighingData.getW1Quantity();
				if (StringUtils.isNotEmpty(firstWeighingData.getW1UoM()) && (tareWeight != null)) {
					final UomBsEntity tareWeightUom = getUom(firstWeighingData.getW1UoM(), FIELD_FIRST_WEIGHING_W1UOM);
					tareWeightT = getAsT(tareWeight, tareWeightUom);
				}
			}

			// gross weight
			BigDecimal grossWeightT = null;
			if (secondWeighingData != null) {
				final BigDecimal grossWeight = secondWeighingData.getW2Quantity();
				if (StringUtils.isNotEmpty(secondWeighingData.getW2UoM()) && (grossWeight != null)) {
					final UomBsEntity grossWeightUom = getUom(secondWeighingData.getW2UoM(), FIELD_SECOND_WEIGHING_W2UOM);
					grossWeightT = getAsT(grossWeight, grossWeightUom);
				}
			}

			endLoadingService.setLoadOrder2Loaded(assignment,
					startLoadingpoint,
					endLoadingpoint,
					tareWeightT,
					grossWeightT,
					request);

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

			result = response;
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
}
