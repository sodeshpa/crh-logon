/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigInteger;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.naming.NamingException;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.LocalServices;
import com.crh.logon.business.services.localinterfaces.LogonEventServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.stores.dictionaries.MopStatusStoreInterface;
import com.crh.logon.business.services.dispatchers.LogonEventServiceDispatcher;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.utils.LogonEventContextBean;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.crh.logon.persistence.entities.refdata.MopStatusTxEntity;

@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class LogonEventServiceBean extends LogonEventServiceDispatcher implements LogonEventServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonEventServiceBean.class);
	
	@EJB private MopStatusStoreInterface mopStatusStore;
	
	/**
	 * Returns a string containing the static text for the given error code,
	 * and the dynamic parameters received in the request.
	 * 
	 * @param locale
	 * @param code
	 * @param fmtParams
	 * @return
	 */
	public String getMopStatusTxt(Locale locale, BigInteger code, Object[] fmtParams) {
		
		MopStatusTxEntity statusRef = mopStatusStore.findText(code.toString(), locale.getLanguage());
		return LogonUtil.resolveMopStatusText(locale, fmtParams, statusRef);
	}
	
	public LogonEventResponse dispatchEventBean(LogonEventRequest request, long interfaceTypeCode) {
		// [[
		LogonEventResponse response = null;
		Object[][] params = {{"request", request},
							{"ifcType", interfaceTypeCode + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "_dispatchEventBean", params);
		}
		// ]]
		try {
			LogonEventContextBean contextBean = new LogonEventContextBean();
			contextBean.setInterfaceTypeCd(interfaceTypeCode);
			
			RequestHandlerLocalInterface handler = LocalServices.getLocalInterfaceForRequestHandler(request.getClass().getSimpleName());
			response = handler.handle(request, contextBean);
			
			return response;
		} catch (NamingException e) {
			LOG.error(e.getMessage());
			return null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(response);
			}
		// ]]
		}
	}
	
	
	/**
	 * Processes a LogonEventRequest and returns a LogonEventResponse.
	 * The returned LogonEventResponse will be of the subtype defined by the 
	 * specified interface. It is either:
	 * 1) A response returned by the eventhandler, which should always be of the correct sub type.
	 * 2) A response returned by a LogonEventException, which will be converted to the correct sub type. 
	 */
	public LogonEventResponse processEventBean(LogonEventRequest request, long ifcType) {
		return dispatchEventBean(request, ifcType);
	}
}
