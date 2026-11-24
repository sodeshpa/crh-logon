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
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.SwitchTruckRequest;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class SwitchTruckRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SwitchTruckRequestHandler.class);

    private static String FIELD_PDA_TRUCK_LABEL = "TruckLabel";
        
    @EJB private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;
	@EJB private TruckAdminServiceLocalInterface truckAdminService;

    
    protected void validate(LogonEventRequest eventBean) {
        
    	SwitchTruckRequest bean = (SwitchTruckRequest)eventBean;
    	rfidMandatory(FIELD_PDA_TRUCK_LABEL, bean.getTruckRfidIdentity(), bean.getTruckLabel());
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
            SwitchTruckRequest bean = (SwitchTruckRequest)eventBean;
            
            DriverEntity driver = getCurrentDriver();
            if (driver == null) {
                
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "driver not found"));
            }
            TruckEntity truck = getTruck(bean.getTruckRfidIdentity(), bean.getTruckLabel());
            if (truck == null) {
            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_PDA_TRUCK_LABEL), bean.getTruckLabel());
            }
            truck.setPdaAvailableFlag(Boolean.TRUE);
            DriverEntity lastTruckDriver = truckAdminService.fetchDriver(truck.getTruckId());
            BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_TRUCK_DRIVER_CHANGED, bean, false);
			bizevent.setTruck(truck);
			driver = truckAdminService.updateDriver(truck, lastTruckDriver, driver, true);
            asyncMopEventOutService.sendTruckCompositionToPDA(getPdaSession(), driver);
            
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
