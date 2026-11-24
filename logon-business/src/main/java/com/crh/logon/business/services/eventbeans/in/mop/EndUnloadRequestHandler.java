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
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.EndUnloadRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class EndUnloadRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EndUnloadRequestHandler.class);

    private static final String FIELD_REMAINING_QUANTITY_PRESENT = "remainingQuantityPresent";
    private static final String FIELD_UNLOAD_ORDER_ITEM_ID = "unloadOrderItemID";
    
    @EJB private OrderServiceLocalInterface orderService;
    @EJB private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;
    
    protected void validate(LogonEventRequest eventBean) {
    	
    	EndUnloadRequest bean = (EndUnloadRequest)eventBean;
    	validateMandatory(FIELD_ASSIGNMENT_PART_ID, bean.getAssignmentPartId());
    	validateMandatory(FIELD_REMAINING_QUANTITY_PRESENT, bean.isRemainingQuantityPresent());
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
			EndUnloadRequest bean = (EndUnloadRequest)eventBean;
			
			BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_DELIVERED, bean, true);

			AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());
			
			bizevent.setAssignment(part.getAssignment());
			bizevent.setOrder(part.getOrder());
			bizevent.setShipto(part.getShipto());
			
			ShiptoSiloEntity silo = getShiptoSilo(bizevent, bean.getShipToSiloRfidIdentity(), bean.getShipToSiloLabel());
			if (silo != null) {
				bizevent.setShipto(silo.getShipto());
			}
			OrderItemEntity orderItem = null;
			if (part.getAssignment().getAssignmentType().getAssignmentTypeCd().longValue() == Constant.ASSIGNMENT_TYPE_INTERNAL) {
				orderItem = getDaoProvider().getOrderItemEntityDao().findById(bean.getUnloadOrderItemId());
				if (orderItem == null) {
					
					throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_UNLOAD_ORDER_ITEM_ID), bean.getUnloadOrderItemId());
				}
				else if (LogonStatusUtils.isDeliveredOrBeyond(orderItem.getOrder().getOrderStatus().getOrderStatusCd())) {
					asyncMopEventOutService.sendCancelTourToPDA(orderItem.getDeliveredLoadTruck(), part.getAssignment());
					return createLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "Order is already delivered");
				}
			}

			orderService.setUnloaded(bizevent, orderItem, silo, bean.isRemainingQuantityPresent(),  bean.getReturnedQuantity(), true, false);
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
