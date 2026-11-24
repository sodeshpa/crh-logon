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
import com.crh.logon.business.services.localinterfaces.AsyncMopEventOutServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.StartUnloadRequest;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class StartUnloadRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(StartUnloadRequestHandler.class);
    
    private static final String FIELD_UNLOAD_ORDER_ITEM_ID = "unloadOrderItemID";

    @EJB private OrderServiceLocalInterface orderService;
	@EJB private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;

	protected void validate(LogonEventRequest eventBean) {
        
    	StartUnloadRequest bean = (StartUnloadRequest)eventBean;
    	validateMandatory(FIELD_ASSIGNMENT_PART_ID, bean.getAssignmentPartId());
    	validateMandatory(FIELD_UNLOAD_ORDER_ITEM_ID, bean.getUnloadOrderItemId());
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
            StartUnloadRequest bean = (StartUnloadRequest)eventBean;
            
            BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_UNLOADING, bean, true);
            AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());

			if (part.getAssignment().getAssignmentType().getAssignmentTypeCd().equals(Constant.ASSIGNMENT_TYPE_INTERNAL)) {
				OrderItemEntity orderItem = getDaoProvider().getOrderItemEntityDao().findById(bean.getUnloadOrderItemId());
				if (orderItem != null && LogonStatusUtils.isDeliveredOrBeyond(orderItem.getOrder().getOrderStatus().getOrderStatusCd())) {
					asyncMopEventOutService.sendCancelTourToPDA(orderItem.getDeliveredLoadTruck(), part.getAssignment());
					return createLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "Order is already delivered");
				}
			}
            
            bizevent.setAssignment(part.getAssignment());
            bizevent.setOrder(part.getOrder());
            bizevent.setShipto(part.getShipto());
            ShiptoSiloEntity silo = getShiptoSilo(bizevent, bean.getShipToSiloRfidIdentity(), bean.getShipToSiloLabel());
            if (silo != null) {
               	bizevent.setShipto(silo.getShipto());
            }
            orderService.startUnload(bizevent);
			kafkaPublisherService.publishMOPEvent(bizevent.getOrder(), bean);
            
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
