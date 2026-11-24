/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.ReleaseContainerRequest;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class ReleaseContainerRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReleaseContainerRequestHandler.class);
        
	@EJB private TruckAdminServiceLocalInterface truckAdminService;

    
    protected void validate(LogonEventRequest eventBean) {
    
    	ReleaseContainerRequest bean = (ReleaseContainerRequest)eventBean;
    	validateMandatory(FIELD_ASSIGNMENT_PART_ID, bean.getAssignmentPartId());
    	rfidMandatory(FIELD_CONTAINER_LABEL, bean.getContainerRfidIdentity(), bean.getContainerLabel());
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
            ReleaseContainerRequest bean = (ReleaseContainerRequest)eventBean;
            
            ContainerEntity container = getContainer(bean.getContainerRfidIdentity(), bean.getContainerLabel());
            if (container == null) {
			
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_CONTAINER_LABEL), bean.getContainerLabel());
            }
            DriverEntity driver = getCurrentDriver();
            if (driver == null) {
            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "driver not found"));	
            }			
            TruckEntity truck = driver.getCurrentTruck();
            Filter filter = new FilterImpl();
    		filter.addExpression(Restrictions.eq(TrailerEntity.ATTR_CURRENT_TRUCK_ID, truck.getTruckId()));
        	TrailerEntity trailer = getDaoProvider().getTrailerEntityDao().findSingle(filter);
        	truckAdminService.updateTruck(truck, trailer, null, true);
        	
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
