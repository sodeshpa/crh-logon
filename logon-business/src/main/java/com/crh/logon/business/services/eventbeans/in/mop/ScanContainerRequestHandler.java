/*
 * Author : AdNovum Informatik AG
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
import com.crh.logon.business.services.utils.UOMConverter;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.ScanContainerRequest;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class ScanContainerRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ScanContainerRequestHandler.class);

    @EJB private OrderServiceLocalInterface orderService;
    @EJB private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;

    protected void validate(LogonEventRequest eventBean) {
        
    	ScanContainerRequest bean = (ScanContainerRequest)eventBean;
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
            ScanContainerRequest bean = (ScanContainerRequest)eventBean;
            
            BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_SCAN_CONTAINER, bean, true);
            AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());
            bizevent.setAssignment(part.getAssignment());
            
            ContainerEntity container = getContainer(bean.getContainerRfidIdentity(), bean.getContainerLabel());
            if (container == null) {
            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_CONTAINER_LABEL), bean.getContainerLabel());
            }
            bizevent.setContainer(container);
			
            LoadEntity containerLoad = null;
            try {
            	containerLoad = orderService.updateTruckLoadFromContainerLoad(bizevent, part, container);
            } catch (LogonException le) {
            	if (le.getReason().equals(Reason.RES_ERROR_MATERIAL_MISMATCH)) {
            		
            		throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.MATERIAL_MISMATCH_WITH_CONTAINER, container.getContainerName()));
            	}
            	else if (le.getReason().equals(Reason.RES_ERROR_UNKNOWN_MATERIAL)) {
            		
            		throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.UNKNOWN_MATERIAL_IN_CONTAINER, container.getContainerName()));
            	}
            	
            	throw le;
            }
        	BigDecimal quantity = UOMConverter.getUomConvertedValue(containerLoad.getNetQuantityUom(), containerLoad.getNetQuantity(), false);
        	if (quantity == null) {
        		
        		throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "cannot determine load quantity"));
        	}
        	asyncMopEventOutService.sendTruckLoadToPDA(getPdaSession(), part, quantity, container, true);
        	
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
