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
import com.holcim.logon.eventbeans.in.mop.EnterEndpointRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class EnterEndpointRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EnterEndpointRequestHandler.class);

    @EJB private OrderServiceLocalInterface orderService;

    protected void validate(LogonEventRequest eventBean) {
    	
    	EnterEndpointRequest bean = (EnterEndpointRequest)eventBean;
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
            EnterEndpointRequest bean = (EnterEndpointRequest)eventBean;
            
            BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_RETURNED, bean, true);
            AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());
            bizevent.setAssignment(part.getAssignment());
            orderService.setReturned(bizevent);
            
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
