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
import com.holcim.logon.eventbeans.in.mop.LeaveShipToRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class LeaveShipToRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LeaveShipToRequestHandler.class);

    private static String FIELD_SHIP_TO_ID = "ShipToID";
    
    @EJB private OrderServiceLocalInterface orderService; 
    
    protected void validate(LogonEventRequest eventBean) {
    	
    	LeaveShipToRequest bean = (LeaveShipToRequest)eventBean;
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
            LeaveShipToRequest bean = (LeaveShipToRequest)eventBean;
            AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());
            AssignmentEntity assignment = part.getAssignment();
            OrderEntity order = part.getOrder();

            final Long shiptoOrShippingpointId = bean.getShipToId();
            BizeventEntity bizevent; 
            if (order != null && LogonUtil.isOrderWithReversedDirection(order)) {
            	bizevent = createBizevent(Constant.BIZEVENT_TYPE_LEAVE_SHIPTO, bean, true);
            	
				if (shiptoOrShippingpointId != null) {
					ShippingpointEntity shippingpoint =
					getDaoProvider().getShippingpointEntityDao().findById(shiptoOrShippingpointId);
					if (shippingpoint == null) {
							throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN,
									FIELD_SHIP_TO_ID), shiptoOrShippingpointId);
					}
					bizevent.setShippingpoint(shippingpoint);
	            }
            }
            else {
            	bizevent = createBizevent(Constant.BIZEVENT_TYPE_LEAVE_SHIPTO, bean, true);
				
				if (shiptoOrShippingpointId != null) {
					ShiptoEntity shipto = getDaoProvider().getShiptoEntityDao().findById(shiptoOrShippingpointId);
	            	if (shipto == null) {
	            		throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN,
	            				FIELD_SHIP_TO_ID), shiptoOrShippingpointId);
	            	}	
	            	bizevent.setShipto(shipto);
				}
            }
            bizevent.setAssignment(assignment);
            bizevent.setOrder(order);
            
            orderService.leaveShipto(bizevent);
            
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
