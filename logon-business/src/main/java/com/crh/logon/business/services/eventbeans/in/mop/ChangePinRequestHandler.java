/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;
import java.util.Calendar;

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
import com.crh.logon.business.services.utils.LogonEventException;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.ChangePinRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.utils.PasswordUtil;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class ChangePinRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ChangePinRequestHandler.class);

    private static String FIELD_PDA_USER_NO = "PdaUserNo";
    private static String FIELD_OLD_PIN = "OldPIN";
    private static String FIELD_NEW_PIN = "NewPIN";
    
    protected void validate(LogonEventRequest eventBean) {
    	
    	ChangePinRequest bean = (ChangePinRequest)eventBean;
    	validateMandatory(FIELD_PDA_USER_NO, bean.getPdaUserNo());
    	validateMandatory(FIELD_OLD_PIN, bean.getOldPin());
    	validateMandatory(FIELD_NEW_PIN, bean.getNewPin());
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
			ChangePinRequest bean = (ChangePinRequest)eventBean;
            
            Filter filter = new FilterImpl();
            filter.addExpression(Restrictions.eq(UserEntity.ATTR_PDA_USER_NO, Long.valueOf(bean.getPdaUserNo())));
            UserEntity user = getDaoProvider().getUserEntityDao().findSingle(filter);
			
            if (user == null) {
            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.UNKNOWN_USER));
            }
			
            // check PIN validity
			String pinInput = bean.getOldPin();
			if (!PasswordUtil.validatePassword(pinInput, user.getPdaPin())) {
				throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.WRONG_PIN));
			}
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, getConfigBean().getAppPolicyPINLifeTimeD());
			user.setPdaPin(PasswordUtil.createHash(bean.getNewPin()));
			user.setPdaPinValidToDat(calendar.getTime());
			
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
