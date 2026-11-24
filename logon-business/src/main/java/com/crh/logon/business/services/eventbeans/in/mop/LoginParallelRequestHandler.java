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
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.LoginParallelRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class LoginParallelRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LoginParallelRequestHandler.class);

    private static String FIELD_TERMINAL_LABEL = "TerminalLabel";
    
    @EJB private OrderServiceLocalInterface orderService;
    
    protected void validate(LogonEventRequest eventBean) {
    	
    	LoginParallelRequest bean = (LoginParallelRequest)eventBean;
    	validateMandatory(FIELD_ASSIGNMENT_PART_ID, bean.getAssignmentPartId());
    	rfidMandatory(FIELD_TERMINAL_LABEL, bean.getTerminalRfidIdentity(), bean.getTerminalLabel());
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
            LoginParallelRequest bean = (LoginParallelRequest)eventBean;
            
            BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT, bean, true); 
            AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());
            
            bizevent.setAssignment(part.getAssignment());
            bizevent.setShippingpoint(part.getShippingpoint());
            LoadingpointEntity loadingpoint = getLoadingpoint(bizevent, bean.getTerminalRfidIdentity(), bean.getTerminalLabel());
            if (loadingpoint == null) {
			
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_TERMINAL_LABEL), bean.getTerminalLabel());
            }
            bizevent.setShippingpoint(loadingpoint.getShippingpoint());
            
            if (part.getLoad() != null) {
            	// There's only 1 order item for a bulk load
            	OrderItemEntity bulkOrderItem = LogonUtil.getBulkDumpOrPowderOrderItem(orderService.getOrderItems(part.getLoad()));
            	if (bulkOrderItem != null) {
            		bizevent.setOrder(bulkOrderItem.getOrder());
            	}
            }
            orderService.pdaLogin(bizevent, loadingpoint);
            
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
