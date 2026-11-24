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
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.WaitingOnShipToRequest;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.DriverWaitingreasonEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.WaitingtimeReasonBsEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class WaitingOnShipToRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface{
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WaitingOnShipToRequestHandler.class);
    
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;	

    private static String FIELD_SHIPTO_ID = "ShiptoID";
    private static String FIELD_REASON = "Reason";   
    
    protected void validate(LogonEventRequest eventBean) {
        
    	WaitingOnShipToRequest bean = (WaitingOnShipToRequest)eventBean;
    	validateMandatory(FIELD_ASSIGNMENT_PART_ID, bean.getAssignmentPartId());
    	validateMandatory(FIELD_SHIPTO_ID, bean.getShipToId());
    	validateMandatory(FIELD_REASON, bean.getReason());
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
            WaitingOnShipToRequest bean = (WaitingOnShipToRequest)eventBean;
            
            DriverEntity driver = getCurrentDriver();
            if (driver == null) {
                
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "driver not found"));
            }
            DriverWaitingreasonEntity waitingReason = new DriverWaitingreasonEntity();
            waitingReason.setDriver(driver);
            ShiptoEntity shipto = getDaoProvider().getShiptoEntityDao().findById(bean.getShipToId());
            if (shipto == null) {
            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_SHIPTO_ID), bean.getShipToId());
            }
            waitingReason.setShipto(shipto);
            WaitingtimeReasonBsEntity waitingtimeReason = referentialDataService.findBsEntity(WaitingtimeReasonBsEntity.class, bean.getReason());
            if (waitingtimeReason == null) {
			
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_REASON), bean.getReason());
            }
            waitingReason.setWaitingtimeReason(waitingtimeReason);
            waitingReason.setWaittimeMin(new Long(getConfigBean().getWsclientMopWaitingTimeAtShipTo()));
            waitingReason.setEventTs(eventBean.getEventTime());
            getDaoProvider().getDriverWaitingreasonEntityDao().persist(waitingReason);
            
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
