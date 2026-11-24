
package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.eventbeans.in.StartLoadingRequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderChangeServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventSimulationException;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.EventSource;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.FirstWeighingData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.StartLoadingRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.StartLoadingResponse;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class StartLoadingRequestHandler extends LogonEventProcessor
		implements StartLoadingRequestHandlerLocalInterface, RequestHandlerLocalInterface {

	@EJB
	private OrderChangeServiceLocalInterface orderChangeService;

	@EJB
	private MonitoringTablesServiceLocalInterface monitoringTablesService;

	@SuppressWarnings("unused")
	private static final String FIELD_MATERIAL = "material";

	private static final String FIELD_LOAD_ORDER_ITEM_ID = "loadOrderItemId";

	private static final String FIELD_FIRST_WEIGHING = "firstWeighing";

	private static final String FIELD_FIRST_WEIGHING_W1QUANTITY = "firstWeighing.w1Quantity";

	private static final String FIELD_FIRST_WEIGHING_W1UOM = "firstWeighing.w1UoM";

	private static final String FIELD_LOCATION = "location";

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final StartLoadingRequest request = (StartLoadingRequest) eventBean;

		validateMandatory(FIELD_LOAD_ORDER_ITEM_ID, request.getLoadOrderItemId());
		// LOGON-5651 Issue 2)
		if (BooleanUtils.isFalse(request.isTareWarnIgnore())) {
			validateMandatory(FIELD_FIRST_WEIGHING, request.getFirstWeighing());
			validateMandatory(FIELD_FIRST_WEIGHING_W1QUANTITY, request.getFirstWeighing().getW1Quantity());
			validateMandatory(FIELD_FIRST_WEIGHING_W1UOM, request.getFirstWeighing().getW1UoM());
		}
		validateMandatory(FIELD_LOCATION, request.getLocation());
	}

	@Override
	public LogonEventResponse processRequest(LogonEventRequest eventBean, boolean directCall) {
		final StartLoadingRequest request = (StartLoadingRequest) eventBean;
		final StartLoadingResponse response = createLogonOKEventResponse(StartLoadingResponse.class);

		final AssignmentPartEntity ape = getAssignmentPartForLoading(request.getLoadOrderItemId(), FIELD_LOAD_ORDER_ITEM_ID);
		final LoadEntity load = ape.getLoad();
		final FirstWeighingData firstWeighingData = request.getFirstWeighing();

		final List<OrderItemEntity> orderItems = orderService.getOrderItems(load);
		if (orderItems.isEmpty()) {
			throw newApplicationError("No order item found with load id " + load.getLoadId() + "!");
		}

		// this one is for the BizEvent - only if unique
		OrderEntity uniqueOrder = null;
		if (orderItems.size() == 1) {
			uniqueOrder = orderItems.get(0).getOrder();
		}

		// anyone - to determine the SP
		final OrderEntity firstOrder = orderItems.get(0).getOrder();

		// simulation only?
		boolean simulateOnly = request.isSimulateOnly();

		if (!LogonUtil.isOrderWithReversedDirection(firstOrder) || !directCall) {
			// loadingpoitNo can still be null after this
			final String loadingpointNo = StringUtils.defaultIfEmpty(
					firstWeighingData.getW1WeighingPointNo(),
					request.getLocation().getLoadingpointNo());

			final ShippingpointEntity sp = firstOrder.getShippingpoint();
			final LoadingpointEntity lp = getLoadingpoint(sp.getPlant().getPlantCd(), loadingpointNo);

			final AssignmentEntity assignment = ape.getAssignment();
			verifyNoPlanning(assignment);

			verifyTruckIsEmpty(load, assignment);

			BizeventEntity bizevent;
			if (LogonUtil.isOrderWithReversedDirection(firstOrder)) {
				bizevent = createBizeventForUnloading(load,
						firstWeighingData.getLoadingStartTime(), sp, uniqueOrder, assignment,
						request.getEventTime());
			}
			else {
				bizevent = createBizeventForLoading(load,
						firstWeighingData.getLoadingStartTime(), sp, uniqueOrder, assignment,
						request.getEventTime());
			}

			setFirstWeighingValuesOnLoad(load, firstWeighingData, lp, request.isTareDeviationDriverACK());
			BigDecimal tareWeightT =
					calcTareWeight(load, firstWeighingData, FIELD_FIRST_WEIGHING_W1UOM, request.isTareWarnIgnore());

			orderService.startLoad(bizevent, load, lp, tareWeightT, EventSource.LDS);

			if (!simulateOnly) {
				monitoringTablesService.processBizevent(bizevent);

				kafkaPublisherService.publishLDSEvent(firstOrder, request, Constant.EVENT_STARTLOADING_FR);
			}
		}

		if (simulateOnly) {
			throw new LogonEventSimulationException(response);
		}

		return response;
	}

	private void verifyTruckIsEmpty(LoadEntity load, AssignmentEntity assignment) {
		final TruckEntity truckEntity = load.getTruck();
		if (!orderChangeService.preCheckTruckIsEmpty(truckEntity.getTruckId(), assignment.getAssignmentId())) {
			throw newApplicationError(
					"Can not load this order, the truck " + truckEntity.getTruckId() + " are still in loading by another order");
		}
	}
}
