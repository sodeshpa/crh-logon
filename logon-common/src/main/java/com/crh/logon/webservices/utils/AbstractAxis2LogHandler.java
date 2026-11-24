package com.crh.logon.webservices.utils;
/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
import java.util.Date;
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.jaxen.JaxenException;

import com.crh.logon.utils.ConfigBean;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 * 
 * Common code used by both the client and server side log handlers. This class 
 * could be extended to do the actual logging for the client and server side web 
 * services. However, for the server side web services, we want to log the exact 
 * message (including formatting), even if it's not XML. That's why the 
 * functionality is limited to setting up the LogonOperationContext.
 */
public abstract class AbstractAxis2LogHandler extends AbstractHandler {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AbstractAxis2LogHandler.class);
	
	private static final String CONFIG_PREFIX_SERVER = "wsserver";
    private static final String CONFIG_PREFIX_CLIENT = "wsclient";
	public static final String PROPERTY_SERVICE_NAME = "service_name";
	public static final String PROPERTY_CONFIG_BEAN = "config_bean";
	public static final String PROPERTY_LOGON_OPERATION_CONTEXT = "logon_operation_context";
	
	protected abstract String getServiceName(MessageContext mc);
	protected abstract ConfigBean getConfigBean(MessageContext mc);
	
	protected LogonOperationContext createLogonOperationContext(MessageContext mc) {
		
		ConfigBean configBean = getConfigBean(mc);
		String serviceName = getServiceName(mc);
		LogonOperationContext logonOperationContext = new LogonOperationContext();
		logonOperationContext.setServerSide(mc.isServerSide());
		logonOperationContext.setServiceName(serviceName);
		logonOperationContext.setConfigBean(configBean);
		logonOperationContext.setDate(new Date());
		String configLabel = logonOperationContext.getServiceName().toLowerCase();
		
		if (configBean != null) {
			String configPrefix = logonOperationContext.isServerSide()?CONFIG_PREFIX_SERVER:CONFIG_PREFIX_CLIENT;
			String suppressDumpingXpath = configBean.getProperty(configPrefix + "." + configLabel + ".dump.suppress_xpath");
			if (suppressDumpingXpath != null) {
				AXIOMXPath xpath = null;
				try {
					xpath = new AXIOMXPath(suppressDumpingXpath);
					xpath.addNamespaces(mc.getEnvelope());
					List omElements = xpath.selectNodes(mc.getEnvelope());
					if ( (omElements != null) && (omElements.size() == 1) ) {
						logonOperationContext.setSuppressDumping(true);
					}
				}
				catch (JaxenException e) {
					// This is not severe, so stacktrace is not necessary
					LOG.error("error executing xpath " + e);
				}
			}
			
			String substXpath = configBean.getProperty(configPrefix + "." + configLabel + ".dump.subst_xpath");
			if (substXpath != null) {
				AXIOMXPath xpath = null;
				try {
					xpath = new AXIOMXPath(substXpath);
					xpath.addNamespaces(mc.getEnvelope());
					List omElements = xpath.selectNodes(mc.getEnvelope());
					if (omElements != null) {
						Object[] substVariables = new Object[omElements.size()];
						for (int i=0;i<substVariables.length;i++) {
							Object element = omElements.get(i);
							if (element != null) {
								if (element instanceof OMElement) {
									substVariables[i] = ((OMElement)omElements.get(i)).getText();
								}
								else if (element instanceof OMText) {
									substVariables[i] = ((OMText)omElements.get(i)).getText();
								}
								else {
									substVariables[i] = element.toString();
								}
							}
						}
						logonOperationContext.setSubstVariables(substVariables);
					}
				}
				catch (JaxenException e) {
					// This is not severe, so stacktrace is not necessary
					LOG.error("error executing xpath " + e);
				}
			}
		}
		
		return logonOperationContext;
	}
}
