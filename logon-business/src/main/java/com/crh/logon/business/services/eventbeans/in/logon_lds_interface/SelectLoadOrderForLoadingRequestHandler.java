
package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.BizEventUtil;
import org.apache.commons.lang.StringUtils;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SelectLoadOrderForLoadingRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SelectLoadOrderForLoadingRequest.OrderValidation;
import com.holcim.logon.eventbeans.in.logon_lds_interface.SelectLoadOrderForLoadingResponse;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class SelectLoadOrderForLoadingRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final String FIELD_LOAD_ORDER_ID = "loadOrderId";

	private static final String FIELD_OV_LOGON_ORDER_NO = "orderValidation[%d].logonOrderNo";

	private static final String FIELD_OV_ORDER_VALIDATION_CODE = "orderValidation[%d].orderValidationCode";

	@EJB
	private MonitoringTablesServiceLocalInterface monitoringTablesService;

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final SelectLoadOrderForLoadingRequest request = (SelectLoadOrderForLoadingRequest) eventBean;

		validateMandatory(FIELD_LOAD_ORDER_ID, request.getLoadOrderId());

		int i = 0;
		final List<OrderValidation> ovList = request.getOrderValidation();
		for (OrderValidation ov : ovList) {

			validateMandatory(String.format(FIELD_OV_LOGON_ORDER_NO, i), ov.getLogonOrderNo());
			validateMandatory(String.format(FIELD_OV_ORDER_VALIDATION_CODE, i), ov.getOrderValidationCode());

			i++;
		}
	}

	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {

		final SelectLoadOrderForLoadingRequest request = (SelectLoadOrderForLoadingRequest) eventBean;
		final SelectLoadOrderForLoadingResponse response = createLogonOKEventResponse(SelectLoadOrderForLoadingResponse.class);

		int i = 0;
		final List<OrderValidation> ovList = request.getOrderValidation();
		for (OrderValidation ov : ovList) {
			OrderEntity order = getOrder(ov.getLogonOrderNo(), String.format(FIELD_OV_LOGON_ORDER_NO, i));

			if (!StringUtils.equals(order.getOrderValidationCode(), ov.getOrderValidationCode())) {
				throw newApplicationError(String.format(
						"Order validation code %s invalid for order %d. ",
						ov.getOrderValidationCode(),
						order.getOrderNumber()));
			}

			i++;
		}

		final AssignmentEntity assignment = getAssignment(request.getLoadOrderId(),
				FIELD_LOAD_ORDER_ID);

		verifyNoPlanning(assignment);

		List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);

		/* For most of the assignments this bizevent ArrivedAtLoadingpoint will be created by other means earlier.
		 * Mobile: reaching the geofence for the shippingpoint, Rail: Login from Parallel, Scheduling screen: enter
		 * shippingpoint button
		 * When the above ones doesn't happen, this far the StartLoadRequest created the bizevent with the same timestamp as the
		 *  StartLoad.
		 *
		 * We want to make sure that the ArrivedAtLoadingpoint bizevent is created as earliest as possible, based on real events.
		 *
		 * At the point of order finalization we where the bizevents are really used for reporting purpose,
		 * we will take always the first among the many ArrivedAtLoadingpoint timestamps, so it's not really a problem if we
		 * create an extra one here.
		 */
		for (OrderEntity order : orders) {
			BizeventEntity bizArrivedAtLoadingpoint = new BizeventEntity();
			bizArrivedAtLoadingpoint.setBizeventType(BizEventUtil.BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT);
			bizArrivedAtLoadingpoint.setTruck(assignment.getTruck());
			bizArrivedAtLoadingpoint.setAssignment(assignment);
			bizArrivedAtLoadingpoint.setOrder(order);
			bizArrivedAtLoadingpoint.setEventTs(request.getEventTime());
			monitoringTablesService.processBizevent(bizArrivedAtLoadingpoint);
		}

		return response;
	}

}
