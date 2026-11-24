
package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.AssignmentServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.CancelLoadingRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.CancelLoadingResponse;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class CancelLoadingRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final String FIELD_LOAD_ORDER_ID = "loadOrderId";

	@EJB
	private AssignmentServiceLocalInterface assignmentService;


	@Override
	protected void validate(LogonEventRequest eventBean) {
		final CancelLoadingRequest request = (CancelLoadingRequest) eventBean;

		validateMandatory(FIELD_LOAD_ORDER_ID, request.getLoadOrderId());
	}

	@Override
	protected LogonEventResponse perform(LogonEventRequest eventBean) {

		final CancelLoadingRequest request = (CancelLoadingRequest) eventBean;
		final CancelLoadingResponse response = createLogonOKEventResponse(CancelLoadingResponse.class);

		final AssignmentEntity assignment = getAssignment(request.getLoadOrderId(), FIELD_LOAD_ORDER_ID);

		final BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_LOADING_CANCELLED, request.getEventTime(), true);
		bizevent.setTruck(assignment.getTruck());
		bizevent.setAssignment(assignment);

		assignmentService.cancelLoading(bizevent, assignment);

		return response;
	}
}
