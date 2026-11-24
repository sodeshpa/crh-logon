/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import javax.servlet.ServletRequest;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;

import com.crh.logon.utils.ConfigBean;
import com.crh.logon.webservices.utils.AbstractAxis2LogHandler;
import com.crh.logon.webservices.utils.LogonOperationContext;

/**
 * @author AdNovum Informatik AG
 * 
 * This handler creates the 'LogonOperationContext' and sets it as an attribute 
 * of the servlet request. The actual logging is performaed based on this 
 * LogonOperationContext in the servlet filter WebserviceRequestInfoFilter. This
 * handler should be registered as soon as possible after the predispatch 
 * phase for the IN and IN_FAULT flows only.
 * Actual logging is done in the servlet filter so non-XML and non-schema 
 * complient request are logged as well. 
 */
public class Axis2ServerSideLogHandler extends AbstractAxis2LogHandler {
	
	private ConfigBean configBean;
	
	@Override
	protected String getServiceName(MessageContext mc) {
		
		return mc.getAxisService().getName();
	}

	@Override
	protected ConfigBean getConfigBean(MessageContext mc) {
		
		if (configBean == null) {
			configBean = ServiceLocatorProvider.getServiceLocator().getConfigService().getConfigBean();
		}
		
		return configBean;
	}
	
	public InvocationResponse invoke(MessageContext mc) throws AxisFault {
		
		if ( (mc != null) && (mc.getEnvelope() != null) ) {
			ConfigBean configBean = getConfigBean(mc);
			if ( (configBean != null) && (configBean.getWsserverSoapDumpEnable()) ) {
				ServletRequest sr = (ServletRequest)mc.getProperty("transport.http.servletRequest");
				if (sr != null) {
					LogonOperationContext logonOperationContext = createLogonOperationContext(mc);
					sr.setAttribute(LogonOperationContext.class.getName(), logonOperationContext);
				}
			}
		}
		
		return InvocationResponse.CONTINUE;
	}
}
