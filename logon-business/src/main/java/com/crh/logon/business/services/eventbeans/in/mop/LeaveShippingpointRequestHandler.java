/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.LeaveShippingpointRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
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
public class LeaveShippingpointRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LeaveShippingpointRequestHandler.class);
	private static String FIELD_SHIPPINGPOINT_ID = "ShippingPointId";
	
    @EJB private OrderServiceLocalInterface orderService;

	protected void validate(LogonEventRequest eventBean) {
		LeaveShippingpointRequest bean = (LeaveShippingpointRequest)eventBean;
		validateMandatory(FIELD_ASSIGNMENT_ID, bean.getAssignmentId());
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
			LeaveShippingpointRequest bean = (LeaveShippingpointRequest)eventBean;
            AssignmentEntity assignment = getAssignment(bean.getAssignmentId());
            final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
            OrderEntity firstOrder = null;
            if (orders.size() > 0) {
            	firstOrder = orders.get(0);
            }
            final Long shiptoOrShippingpointId = bean.getShippingpointId();

            BizeventEntity bizevent; 
            if (firstOrder != null && LogonUtil.isOrderWithReversedDirection(firstOrder)) {
				bizevent = createBizevent(Constant.BIZEVENT_TYPE_SHIPPING, bean, true);
				
				if (shiptoOrShippingpointId != null) {
					ShiptoEntity shipto = getDaoProvider().getShiptoEntityDao().findById(shiptoOrShippingpointId);
	            	if (shipto == null) {
	            		throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN,
	            				FIELD_SHIPPINGPOINT_ID), shiptoOrShippingpointId);
	            	}
	            	bizevent.setOrder(firstOrder);
	            	bizevent.setShipto(shipto);
	            }
            }
            else {
				bizevent = createBizevent(Constant.BIZEVENT_TYPE_SHIPPING, bean, true);
				
				if (shiptoOrShippingpointId != null) {
					ShippingpointEntity shippingpoint = getDaoProvider().getShippingpointEntityDao().findById(shiptoOrShippingpointId);
					
					if (shippingpoint == null) {
						throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN,
								FIELD_SHIPPINGPOINT_ID), shiptoOrShippingpointId);
					}
					
					bizevent.setShippingpoint(shippingpoint);
				}
            }
			bizevent.setAssignment(assignment);
            orderService.leaveShippingPoint(bizevent);
			for (OrderEntity order : orders) {
				// potentially all orders in the assignment can change, so publish all of them
				kafkaPublisherService.publishMOPEvent(order, bean);
			}
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
