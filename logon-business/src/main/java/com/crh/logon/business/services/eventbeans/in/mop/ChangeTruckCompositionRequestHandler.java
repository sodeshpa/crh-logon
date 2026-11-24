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
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.ChangeTruckCompositionRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class ChangeTruckCompositionRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ChangeTruckCompositionRequestHandler.class);

    @EJB private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;
	@EJB private TruckAdminServiceLocalInterface truckAdminService;

    
    protected void validate(LogonEventRequest eventBean) {
    	
    	ChangeTruckCompositionRequest bean = (ChangeTruckCompositionRequest)eventBean;	
    	rfidMandatory(FIELD_TRUCK_LABEL, bean.getTruckRfidIdentity(), bean.getTruckLabel());
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
			ChangeTruckCompositionRequest bean = (ChangeTruckCompositionRequest)eventBean;
			
			// Get driver and truck composition
			DriverEntity driver = getCurrentDriver();
			if (driver == null) {
				throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "driver not found"));
			}
			
			TruckEntity truck = getTruck(bean.getTruckRfidIdentity(), bean.getTruckLabel());
			if (truck == null) {
				throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_TRUCK_LABEL), bean.getTruckLabel());
			}
			truck.setPdaAvailableFlag(Boolean.TRUE);
			DriverEntity lastTruckDriver = truckAdminService.fetchDriver(truck.getTruckId());
			
			TrailerEntity trailer = getTrailer(bean.getTrailerRfidIdentity(), bean.getTrailerLabel());
			ContainerEntity container = getContainer(bean.getContainerRfidIdentity(), bean.getContainerLabel());
			
			BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_TRUCK_COMPOSITION_CHANGED, 
					bean, false);
			bizevent.setTruck(truck);
			bizevent.setTrailer(trailer);
			bizevent.setContainer(container);
			// Update driver and truck composition
			driver = truckAdminService.updateDriver(truck, lastTruckDriver, driver, true);
			try {
				truckAdminService.updateTruck(truck, trailer,  container, false);
			}
			catch (LogonException e) {
				if (e.getReason().equals(Reason.RES_ERROR_TRUCK_TRAILER_IS_ACTIVE)) {
					throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, Message.lookup(getLocale(), "pda.error.truck_has_assignment")));
				}
				
				throw e;
			}
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
