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
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.EnterShippingpointRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class EnterShippingpointRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EnterShippingpointRequestHandler.class);

    private static String FIELD_SHIPPINGPOINT_ID = "ShippingPointId";
    
    @EJB private OrderServiceLocalInterface orderService;

    protected void validate(LogonEventRequest eventBean) {
    	
    	EnterShippingpointRequest bean = (EnterShippingpointRequest)eventBean;
    	validateMandatory(FIELD_ASSIGNMENT_ID, bean.getAssignmentId());
    	
        AssignmentEntity assignment = getAssignment(bean.getAssignmentId());
    	List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);

		for (OrderEntity oe : orders) {
			OrderStatusBsEntity orderStatus = oe.getOrderStatus();
			if (!LogonStatusUtils.isBeforeLoading(orderStatus.getOrderStatusCd())) {
				throw newApplicationError("Enter shippingpoint cannot be done with the current order status! Order status is " + orderStatus.getOrderStatusCd());
			}
		}
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
            EnterShippingpointRequest bean = (EnterShippingpointRequest)eventBean;
            AssignmentEntity assignment = getAssignment(bean.getAssignmentId());
            final List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
            OrderEntity firstOrder = null;
            if (orders.size() > 0) {
            	firstOrder = orders.get(0);
            }
            final Long shiptoOrShippingpointId = bean.getShippingpointId();

            BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT, bean, true);
			if (firstOrder != null && LogonUtil.isOrderWithReversedDirection(firstOrder)) {
				if (shiptoOrShippingpointId != null) {
					ShiptoEntity shipto = getDaoProvider().getShiptoEntityDao().findById(shiptoOrShippingpointId);
	            	if (shipto == null) {
	            		throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN,
	            				FIELD_SHIPPINGPOINT_ID), shiptoOrShippingpointId);
	            	}
	            	bizevent.setShipto(shipto);
	            }
            }
            else {
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
            
            orderService.enterLoadingSite(bizevent);
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
