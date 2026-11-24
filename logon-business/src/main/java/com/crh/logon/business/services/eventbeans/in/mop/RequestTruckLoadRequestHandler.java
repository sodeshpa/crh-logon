/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.mop;

import java.math.BigDecimal;

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
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.RequestTruckLoadRequest;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class RequestTruckLoadRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RequestTruckLoadRequestHandler.class);

    @EJB private OrderServiceLocalInterface orderService;
    @EJB private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;

	protected void validate(LogonEventRequest eventBean) {
        
		RequestTruckLoadRequest bean = (RequestTruckLoadRequest)eventBean;
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
			RequestTruckLoadRequest bean = (RequestTruckLoadRequest)eventBean;
			
			AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());
			LoadEntity load = part.getLoad();
			if (load == null) {
				
				throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "no load found for truck"));
			}
			
			LoadEntity wagonLoad = part.getLoad().getLoadedFromLoad();
			if (wagonLoad == null) {
				
				throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "no wagon load found for wagon"));
			}
			
			// check if we expect the wagon to be empty
			boolean plausible = false;
			BigDecimal remainingQuantityT = orderService.getWagonRemainingQuantityT(wagonLoad);
			// This message should be send after startLoad (so the truck composition is set on the load)
			BigDecimal orderedQuantityT = asyncMopEventOutService.getOrderedQuantityT(part);
			if (remainingQuantityT.add(new BigDecimal("" + getConfigBean().getAppDefaultTruckBimodalPlausibilityTolerance())).compareTo(orderedQuantityT) <= 0) {
				plausible = true;
			}			
			asyncMopEventOutService.sendTruckLoadToPDA(getPdaSession(), part, remainingQuantityT, null, plausible);
			
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
