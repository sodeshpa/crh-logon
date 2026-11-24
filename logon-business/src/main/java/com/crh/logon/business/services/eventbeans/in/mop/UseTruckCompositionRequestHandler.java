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

import com.crh.logon.business.services.localinterfaces.AsyncMopEventOutServiceLocalInterface;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.UseTruckCompositionRequest;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.PdaSessionEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.PdaSessionStatusBsEntity;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class UseTruckCompositionRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UseTruckCompositionRequestHandler.class);

	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;	
    @EJB private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;
  
	
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
            UseTruckCompositionRequest bean = (UseTruckCompositionRequest)eventBean;
            
            createBizevent(Constant.BIZEVENT_TYPE_TRUCK_COMPOSITION_CONFIRMED, bean, true);			
            TruckEntity newTruck = null;
            if (!StringUtils.empty(bean.getTruckLabel())) {
            	newTruck = getTruck(null, bean.getTruckLabel());
            	newTruck.setPdaAvailableFlag(Boolean.TRUE);
            }
                        
            PdaSessionEntity session = getActiveSession();
            if (session == null) {
        		
        		throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "no session"));
        	}
            DriverEntity driver = getCurrentDriver();
            if (driver == null) {
                
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "driver not found"));
            }		   
            
            TruckEntity currTruck = driver.getCurrentTruck();
            final String currTruckName = currTruck == null || currTruck.getRfidobject() == null ? "" : currTruck.getRfidobject().getObjectLabel();
            final String newTruckName = newTruck == null || newTruck.getRfidobject() == null ? "" : newTruck.getRfidobject().getObjectLabel();
            if (StringUtils.empty(newTruckName) && currTruck != null) {
                driver.setCurrentTruck(null);
            }
            if (StringUtils.notEmpty(newTruckName) && !newTruckName.equals(currTruckName)) {
			
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.INCOMPATIBLE_TRUCK_COMPOSITION, "Truck does not match with the selected one (" + newTruckName + " vs. " + currTruckName + ")."));
            }
            
            LOG.info("Driver (session: " + session.getPdaSessionId() + ") signed in with the following truck: " + newTruckName);
                        
            asyncMopEventOutService.sendOrderStatusToPDA(getPdaSession(), newTruck);
            if (session.getPdaSessionStatus().getPdaSessionStatusCd().longValue() == Constant.PDA_SESSION_STATUS_INITIALIZING) {
            	session.setPdaSessionStatus(referentialDataService.findBsEntity(PdaSessionStatusBsEntity.class, Constant.PDA_SESSION_STATUS_READY));
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
