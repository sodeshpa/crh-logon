/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.common.http;

import java.io.IOException;
import java.util.Date;
import java.util.StringJoiner;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.common.base.ConfigReloadListener;
import com.crh.logon.business.services.interfaces.ConfigService;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.webservices.utils.FileUtils;
import com.crh.logon.webservices.utils.LogonOperationContext;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

@Singleton
@Lock(LockType.READ)
@LocalBean
public class RestHttpClientServiceBean implements ConfigReloadListener, HttpRequestInterceptor, HttpResponseInterceptor {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RestHttpClientServiceBean.class);

	private final CloseableHttpClient httpClient;

	private final ObjectMapper objectMapper;

	private volatile boolean dumpEnabled = false;

	@EJB
	private ConfigService configService;

	public RestHttpClientServiceBean() {
		httpClient = HttpClientBuilder.create()
				.useSystemProperties()
				.setDefaultRequestConfig(
						RequestConfig.custom()
								.setConnectTimeout(20 * 1000)
								.setConnectionRequestTimeout(20 * 1000)
								.setSocketTimeout(20 * 1000).build())
				.addInterceptorFirst((HttpRequestInterceptor) this)
				.addInterceptorLast((HttpResponseInterceptor) this)
				.build();
		objectMapper = JsonMapper.builder().addModule(new JavaTimeModule())
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
				.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
				.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.serializationInclusion(JsonInclude.Include.NON_EMPTY).build();
	}

	@Override
	@PostConstruct
	public void configReloaded() {
		ConfigBean configBean = configService.getConfigBean();
		dumpEnabled = configBean.getRestclientDumpEnable();
	}

	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	@Override
	public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
		httpContext.setAttribute("logon.time", new Date());
	}

	@Override
	public void process(HttpResponse response, HttpContext httpContext) throws IOException {
		if (dumpEnabled) {
			final HttpClientContext clientContext = HttpClientContext.adapt(httpContext);
			final HttpRequest request = clientContext.getRequest();

			StringJoiner sb = new StringJoiner(System.lineSeparator());
			sb.add(request.getRequestLine().toString());
			for (Header header : request.getAllHeaders()) {
				sb.add(header.toString());
			}
			if (request instanceof HttpEntityEnclosingRequest) {
				HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
				if (entity != null) {
					// add extra line break
					sb.add("");
					sb.add(EntityUtils.toString(entity));
				}
			}
			String requestStr = sb.toString();

			sb = new StringJoiner(System.lineSeparator());
			if (response != null) {
				sb.add(response.getStatusLine().toString());
				for (Header header : response.getAllHeaders()) {
					sb.add(header.toString());
				}
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					entity = new BufferedHttpEntity(entity);
					response.setEntity(entity);
					// add extra line break
					sb.add("");
					sb.add(EntityUtils.toString(entity));
				}
			} else {
				sb.add("No response");
			}

			String host = clientContext.getHttpRoute().getTargetHost().getHostName().replace('.','_');

			LogonOperationContext logonOperationContext = new LogonOperationContext();
			logonOperationContext.setConfigBean(configService.getConfigBean());
			logonOperationContext.setDate((Date) httpContext.getAttribute("logon.time"));
			logonOperationContext.setServerSide(false);
			logonOperationContext.setServiceName(host);

			FileUtils.dumpRestRequestResponse(logonOperationContext, requestStr, sb.toString());
		}
	}
}
