/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.GeoPositionRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class GeoPositionRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(GeoPositionRequestHandler.class);

    protected void validate(LogonEventRequest eventBean) {
    	
    	GeoPositionRequest bean = (GeoPositionRequest)eventBean;
    	validateMandatory(FIELD_GEO_X, bean.getGeoX());
    	validateMandatory(FIELD_GEO_Y, bean.getGeoY());
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
			GeoPositionRequest bean = (GeoPositionRequest)eventBean;
            
            BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_POSITION, bean, true);
            AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());
            if (part != null) {
            	bizevent.setAssignment(part.getAssignment());
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
