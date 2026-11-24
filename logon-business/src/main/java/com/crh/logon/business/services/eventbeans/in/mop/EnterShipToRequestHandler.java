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
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.LogonStatusUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.EnterShipToRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class EnterShipToRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EnterShipToRequestHandler.class);

	private static String FIELD_SHIP_TO_ID = "ShipToID";

    @EJB private OrderServiceLocalInterface orderService;
	@EJB private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;

	protected void validate(LogonEventRequest eventBean) {

		EnterShipToRequest bean = (EnterShipToRequest) eventBean;
		validateMandatory(FIELD_ASSIGNMENT_PART_ID, bean.getAssignmentPartId());
	}

	protected LogonEventResponse perform(LogonEventRequest eventBean) {
		// [[
		LogonEventResponse result = null;
		Object[][] params = { { "eventBean", eventBean } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "perform", params);
		}
		// ]]
		try {
			EnterShipToRequest bean = (EnterShipToRequest) eventBean;
			
			AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());
			AssignmentPartEntity unloadPart = null;
			AssignmentEntity assignment = part.getAssignment();
			if (AssignmentUtils.isUnloadPart(part)) {
				unloadPart = part;
			}
			else {
				unloadPart = AssignmentUtils.findAssignmentPartByType(assignment, Constant.ASSIGNMENT_PART_TYPE_UNLOAD);
			}
			OrderEntity order = unloadPart.getOrder();
			if (part.getAssignment().getAssignmentType().getAssignmentTypeCd().equals(Constant.ASSIGNMENT_TYPE_INTERNAL)) {
				if (order != null && LogonStatusUtils.isDeliveredOrBeyond(order.getOrderStatus().getOrderStatusCd())) {
					asyncMopEventOutService.sendCancelTourToPDA(order.getDeliveredTruck(), part.getAssignment());
					return createLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "Order is already delivered");
				}
			}

			final Long shiptoOrShippingpointId = bean.getShipToId();
			BizeventEntity bizevent = createBizevent(
					Constant.BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT, bean, true);

			if (shiptoOrShippingpointId != null) {
				if (order != null && LogonUtil.isOrderWithReversedDirection(order)) {
					ShippingpointEntity shippingpoint = getDaoProvider().getShippingpointEntityDao().findById(
							shiptoOrShippingpointId);
					if (shippingpoint == null) {
						throw new LogonEventException(createBasicLogonEventResponse(
								MopResponseCode.FIELD_UNKNOWN, FIELD_SHIP_TO_ID),
								shiptoOrShippingpointId);
					}
					bizevent.setShippingpoint(shippingpoint);
				}
				else {
					ShiptoEntity shipto = getDaoProvider().getShiptoEntityDao().findById(
							shiptoOrShippingpointId);
					if (shipto == null) {
						throw new LogonEventException(createBasicLogonEventResponse(
								MopResponseCode.FIELD_UNKNOWN, FIELD_SHIP_TO_ID),
								shiptoOrShippingpointId);
					}
					bizevent.setShipto(shipto);
				}
			}
			bizevent.setAssignment(assignment);
			bizevent.setOrder(order);

			orderService.enterUnloadingSite(bizevent, false);
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
