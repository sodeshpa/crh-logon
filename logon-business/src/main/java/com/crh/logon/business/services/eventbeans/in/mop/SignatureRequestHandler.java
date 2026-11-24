/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;
import java.util.Arrays;

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
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.SignatureRequest;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.SignatureEntity;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.persistence.utils.DaoProvider;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class SignatureRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SignatureRequestHandler.class);

    private static String FIELD_SIGNATURE = "Signature";
    
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;	
       
    protected void validate(LogonEventRequest eventBean) {
        
    	SignatureRequest bean = (SignatureRequest)eventBean;
    	validateMandatory(FIELD_SIGNATURE, bean.getSignature());
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
            SignatureRequest bean = (SignatureRequest)eventBean;
			
            DaoProvider daoProvider = getDaoProvider();
            
            DriverEntity driver = getCurrentDriver();
            if (driver == null) {
                
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "driver not found"));
            }
            
            UserEntity user = driver.getUser();
            SignatureEntity signature = user.getSignature();
            if (signature == null) {
            	signature = new SignatureEntity();
            }            
            if ( (signature.getData() == null) || (!Arrays.equals(signature.getData(), bean.getSignature())) ) {
                signature.setData(bean.getSignature());
                signature.setDataMimeType(referentialDataService.findBsEntity(MimeTypeBsEntity.class, Constant.MIME_TYPE_PNG));
                signature = daoProvider.getSignatureEntityDao().persist(signature);
                user.setSignature(signature);
            }
            user.setSignatureValidFlag(Boolean.TRUE);
            
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
