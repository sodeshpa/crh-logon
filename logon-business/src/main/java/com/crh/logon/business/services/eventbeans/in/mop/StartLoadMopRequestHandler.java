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
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.EventSource;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.StartLoadMopRequest;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class StartLoadMopRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(StartLoadMopRequestHandler.class);

    @EJB private OrderServiceLocalInterface orderService;

	protected void validate(LogonEventRequest eventBean) {
		StartLoadMopRequest bean = (StartLoadMopRequest) eventBean;
		validateMandatory(FIELD_ASSIGNMENT_ID, bean.getAssignmentId());
		validateMandatory(FIELD_ASSIGNMENT_PART_ID, bean.getAssignmentPartId());
	}

	public LogonEventResponse perform(LogonEventRequest eventBean) {
		// [[
		LogonEventResponse result = null;
		Object[][] params = { { "eventBean", eventBean } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "perform", params);
		}
		// ]]
		try {
			StartLoadMopRequest bean = (StartLoadMopRequest) eventBean;

			BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_LOADING, bean, true);
			AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());
			if (part == null) {
				throw new LogonEventException(createBasicLogonEventResponse(
						MopResponseCode.APPLICATION_ERROR, "not supported"));
			}
			AssignmentEntity assignment = part.getAssignment();
			bizevent.setAssignment(assignment);
			
			List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
			verifyNoPlanningMaterial(orders);
			
			ShippingpointEntity shippingPoint = getShippingpoint(orders, part);
			bizevent.setShippingpoint(shippingPoint);
			
			LoadingpointEntity loadingpointEntity = getLoadingpoint(bizevent,
					bean.getLoadingpointRfidIdentity(), bean.getLoadingpointLabel());
			if (loadingpointEntity != null) {
				bizevent.setShippingpoint(loadingpointEntity.getShippingpoint());
			}
			orderService.startLoad(bizevent, part.getLoad(), loadingpointEntity, null,
					EventSource.MOP);
			for (OrderEntity order : orders) {
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
