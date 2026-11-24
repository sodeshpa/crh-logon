/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.filters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Date;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.web.filters.utils.BufferedHttpResponseWrapper;
import com.crh.logon.web.filters.utils.InputStreamWrapper;
import com.crh.logon.web.utils.ServiceLocatorProvider;
import com.crh.logon.webservices.utils.FileUtils;
import com.crh.logon.webservices.utils.LogonOperationContext;
import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class WebservicesRequestInfoFilter implements Filter {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WebservicesRequestInfoFilter.class);

	public void init(FilterConfig config) {
		LOG.info("WebservicesRequestInfoFilter initialized!");
	}

	public void destroy() {
		LOG.info("WebservicesRequestInfoFilter destroyed!");
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		// [[
		Object[][] params = { { "req", req },
				{ "res", res },
				{ "chain", chain } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doFilter", params);
		}
		// ]]
		BufferedHttpResponseWrapper response = null;
		InputStreamWrapper request = null;
		ConfigBean configBean = ServiceLocatorProvider.getServiceLocator().getConfigService().getConfigBean();
		try {
			request = new InputStreamWrapper((HttpServletRequest) req);
			response = new BufferedHttpResponseWrapper((HttpServletResponse) res);
			chain.doFilter(request, response);

			if (HttpServletResponse.SC_INTERNAL_SERVER_ERROR == response.getStatus()) {
				LOG.error("Internal server error during processing the request.");
			}
		}
		catch (ServletException | IOException e) {
			ExceptionUtil.errException(LOG, e, params);
			throw e;
		}
		finally {
			handleReturn(configBean, request, response);
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void handleReturn(ConfigBean configBean, InputStreamWrapper request, BufferedHttpResponseWrapper response) {
		try {
			String responseString = null;
			if (((configBean.getWsserverSoapDumpEnable()) || (LOG.isDebugEnabled())) && (response != null)) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bos.write(response.getBuffer());
				responseString = new String(bos.toByteArray(),
						response.getCharacterEncoding() != null ? response.getCharacterEncoding()
								: Charset.defaultCharset().name());
			}
			if (configBean.getWsserverSoapDumpEnable()) {
				dumpRequestResponse(configBean, request, responseString);
			}
			if (LOG.isDebugEnabled()) {
				if (request != null) {
					LOG.debug("SOAP-request (pretty printed):" + System.getProperty("line.separator") + prettyPrintXml(
							request.getRequestBody()));
				}
				if (responseString != null) {
					LOG.debug("SOAP-response (pretty printed):" + System.getProperty("line.separator") + prettyPrintXml(
							responseString));
				}
			}
		}
		catch (Exception e) {
			// Catch any exception, if logging fails; too bad, but it can't
			// harm the actual processing of the message.
			LOG.error("Error dumping request/response", e);
		}
	}

	private String prettyPrintXml(String xml) {

		String formattedXml = xml;
		if (StringUtils.isNotEmpty(xml)) {
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
		}
		return formattedXml;
	}

	private void dumpRequestResponse(ConfigBean configBean, InputStreamWrapper request, String responseString) {

		LogonOperationContext logonOperationContext = null;
		String serviceName = "incoming-webservice";
		if (request != null) {
			logonOperationContext = (LogonOperationContext) request.getAttribute(LogonOperationContext.class.getName());
		}
		if (logonOperationContext == null) {
			logonOperationContext = new LogonOperationContext();
			logonOperationContext.setConfigBean(configBean);
		}
		if (logonOperationContext.getServiceName() == null) {
			// fallback; determine the service from the URI. If the handler is
			// not registered, try to get the service name the poor man's way.
			if (request != null && request.getRequestURI() != null) {
				serviceName = request.getRequestURI();
				serviceName = serviceName.substring(serviceName.lastIndexOf('/') + 1);
			}
			logonOperationContext.setServiceName(serviceName);
		}
		if (logonOperationContext.getDate() == null) {
			logonOperationContext.setDate(new Date());
		}
		if (request != null && StringUtils.isNotEmpty(request.getRequestBody())) {
			FileUtils.dumpSoapRequestResponse(logonOperationContext, request.getRequestBody(), responseString);
		}
		else if (request != null) {
			// Request for WSDL for instance
			LOG.info("Suppressed dumping of empty request for " + request.getRequestURL());
		}
	}

}
