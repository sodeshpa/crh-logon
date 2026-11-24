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
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MessageToLogonRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.PdaMessageEntity;
import com.crh.logon.persistence.entities.refdata.PdaMessageTypeBsEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class MessageToLogonRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MessageToLogonRequestHandler.class);
    
    private static final String FIELD_MESSAGE = "message";

	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;

    protected void validate(LogonEventRequest eventBean) {
    	
    	MessageToLogonRequest bean = (MessageToLogonRequest)eventBean;
    	validateMandatory(FIELD_MESSAGE, bean.getMessage());
    }


    public LogonEventResponse perform(LogonEventRequest eventBean) {
		// [[
		LogonEventResponse result = null;
		Object[][] params = {{"eventBean", eventBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "perform", params);
		}
		// ]]
		try {
            MessageToLogonRequest bean = (MessageToLogonRequest)eventBean;
			
            DriverEntity driver = getCurrentDriver();
            if (driver == null) {
                
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "driver not found"));
            }
            PdaMessageEntity msg = new PdaMessageEntity();
            msg.setPdaUser(driver.getUser());
            msg.setMessage(bean.getMessage());
            PdaMessageTypeBsEntity pdaMessageType = referentialDataService.findBsEntity(PdaMessageTypeBsEntity.class, Constant.PDA_MESSAGE_TYPE_TO_DISPATCHER);
            msg.setPdaMessageType(pdaMessageType);
            msg.setReceiverAckFlag(Boolean.FALSE);
            getDaoProvider().getPdaMessageEntityDao().persist(msg);
            
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
