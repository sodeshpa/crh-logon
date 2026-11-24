/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.utils;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.utils.ConfigBean;
import org.apache.axis2.context.MessageContext;

/**
 * @author AdNovum Informatik AG
 * <p>
 * This handler creates the LogonOperationContext during the OUT/OUT_FAULT flows and dumps the request during any of the flows
 * based on this LogonOperationContext. Unlike the Axis2ServerSideLogHandler, this handler also takes care of the actual logging.
 * It should be registered as the last handler for the OUT/OUT_FAULT flows and the first handler for the IN/IN_FAULT flows.
 */
public class Axis2ClientSideLogHandler extends AbstractAxis2LogHandler {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(Axis2ClientSideLogHandler.class);

	@Override
	protected String getServiceName(MessageContext mc) {

		String serviceName = (String) mc.getConfigurationContext().getProperty(AbstractAxis2LogHandler.PROPERTY_SERVICE_NAME);
		if (serviceName == null) {
			serviceName = "outgoing-webservice";
		}

		return serviceName;
	}

	@Override
	protected ConfigBean getConfigBean(MessageContext mc) {

		return (ConfigBean) mc.getConfigurationContext().getProperty(AbstractAxis2LogHandler.PROPERTY_CONFIG_BEAN);
	}

	private boolean isRequest(MessageContext mc) {

		// For outgoing messages, the type is 'request' if LOGON acts as a
		// client.
		return ((mc.getFLOW() == MessageContext.OUT_FLOW) || (mc.getFLOW() == MessageContext.OUT_FAULT_FLOW));
	}

	private String prettyPrintXml(String xml) {

		String formattedXml = xml;
		try {
			StreamSource src = new StreamSource(new StringReader(xml));
			TransformerFactory transFactory = TransformerFactory.newInstance();
			transFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(src, result);
			formattedXml = result.getWriter().toString();
		}
		catch (Exception e) {
			LOG.debug("Error formatting xml", e);
		}

		return formattedXml;
	}

	public InvocationResponse invoke(MessageContext mc) {

		if ((mc != null) && (mc.getEnvelope() != null)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug((isRequest(mc) ? "request" : "response") + "(pretty printed): " +
						prettyPrintXml(mc.getEnvelope().toString()));
			}
			ConfigBean configBean = getConfigBean(mc);
			if ((configBean != null) && (configBean.getWsclientSoapDumpEnable())) {
				LogonOperationContext logonOperationContext = (LogonOperationContext) mc.getOperationContext()
						.getProperty(AbstractAxis2LogHandler.PROPERTY_LOGON_OPERATION_CONTEXT);
				if (logonOperationContext == null) {
					logonOperationContext = createLogonOperationContext(mc);
					mc.getOperationContext()
							.setProperty(AbstractAxis2LogHandler.PROPERTY_LOGON_OPERATION_CONTEXT, logonOperationContext);
				}
				dumpRequestResponse(logonOperationContext, mc);
			}
		}

		return InvocationResponse.CONTINUE;
	}

	private void dumpRequestResponse(LogonOperationContext logonOperationContext, MessageContext mc) {
		String requestString = null;
		String responseString = null;
		if (isRequest(mc)) {
			requestString = mc.getEnvelope().toString();
		}
		else {
			responseString = mc.getEnvelope().toString();
		}
		FileUtils.dumpSoapRequestResponse(logonOperationContext, requestString, responseString);
	}
}
