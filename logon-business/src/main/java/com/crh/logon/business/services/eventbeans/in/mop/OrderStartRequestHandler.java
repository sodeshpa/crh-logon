/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.OrderStartRequest;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class OrderStartRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderStartRequestHandler.class);
    @EJB private OrderServiceLocalInterface orderService;
	
	protected void validate(LogonEventRequest eventBean) {
		OrderStartRequest bean = (OrderStartRequest)eventBean;
		validateMandatory(FIELD_ASSIGNMENT_PART_ID, bean.getAssignmentPartId());
	}

	protected LogonEventResponse perform(LogonEventRequest eventBean) {
		// [[
		LogonEventResponse result = null;
		Object[][] params = {{"eventBean", eventBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "perform", params);
		}
		// ]]
		try {
			OrderStartRequest bean = (OrderStartRequest)eventBean;
			
			AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());
			
			// TODO (07082013/lajos): at start order the load ap is sent by logon mobile.
			if (part.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_LOAD) {
	            BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK_ACK, bean, true);
	            bizevent.setAssignment(part.getAssignment());
	            bizevent.setOrder(part.getOrder());
			}
			
			orderService.driverAck(part);
			
			return result = createLogonEventResponse(MopResponseCode.OK);
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
