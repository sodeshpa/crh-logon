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

import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.ShiptoSiloVerificationWrapper;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.ScanSiloRequest;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class ScanSiloRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ScanSiloRequestHandler.class);

    @EJB private OrderServiceLocalInterface orderService;
    
    protected void validate(LogonEventRequest eventBean) {
        
    	ScanSiloRequest bean = (ScanSiloRequest)eventBean;
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
            ScanSiloRequest bean = (ScanSiloRequest)eventBean;
            
            BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_SET_SILO, bean, true);
            AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());
            
            bizevent.setAssignment(part.getAssignment());
            bizevent.setShipto(part.getShipto());
            bizevent.setOrder(part.getOrder());
            
            final ShiptoSiloVerificationWrapper siloVerificationWrapper = getShiptoSiloWithVerificationType(bizevent, bean.getSiloRfidIdentity(), bean.getSiloLabel());
            if (siloVerificationWrapper != null && siloVerificationWrapper.getShiptoSilo() != null) {
            	final ShiptoSiloEntity silo = siloVerificationWrapper.getShiptoSilo();
            	bizevent.setShipto(silo.getShipto());
            }
            
            orderService.setSilo(bizevent, siloVerificationWrapper);
            
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
