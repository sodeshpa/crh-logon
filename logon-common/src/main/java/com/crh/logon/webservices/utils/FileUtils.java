/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.MissingFormatArgumentException;
import java.util.function.Function;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.utils.ConfigBean;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class FileUtils {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(FileUtils.class);
	
	public static final Charset ENCODING = Charset.forName("UTF-8");
	private static final String DEFAULT_WEBSERVICE_NAME = "webservice";
	private static final String DEFAULT_BASE_FILENAME_FORMAT_STRING = "%1$s" + File.separator + "%2$tY-%2$tm-%2$td" + File.separator + "%2$tH_%2$tM_%2$tS_%2$tL.%3$s";
	private static final String CONFIG_PREFIX_SERVER = "wsserver";
	private static final String CONFIG_PREFIX_CLIENT = "wsclient";
	private static final String EXTENSION_REQUEST = "request";
	private static final String EXTENSION_RESPONSE = "response";

	private static void saveDumpFile(String filename, String content) {

		String filenameBegin;
		String filenameSeq = "";
		final String filenameEnd;
		final int idx = filename.lastIndexOf('.');
		if (idx >= 0) {
			filenameBegin = filename.substring(0, idx);
			filenameEnd = filename.substring(idx);
		}
		else {
			filenameBegin = filename;
			filenameEnd = "";
		}
		filenameBegin += ".tid" + Thread.currentThread().getId();
		
		int count = 0;
		filename = filenameBegin + filenameSeq + filenameEnd;
		File outputFile = new File(filename);
		while (outputFile.exists()) {
			filenameSeq = "-" + ++count;
			filename = filenameBegin + filenameSeq + filenameEnd;
			outputFile = new File(filename);
		}

		LOG.info("saving '" + outputFile.getAbsoluteFile().toString() + "'");
		OutputStreamWriter out = null;
		outputFile.getParentFile().mkdirs();
		try {
			OutputStream fout= new FileOutputStream(outputFile);
			OutputStream bout = new BufferedOutputStream(fout);
			out = new OutputStreamWriter(bout, ENCODING);
			out.write(content);
			out.flush();
		} catch (Exception e) {
			LOG.error("Error dumping file", e);
		}
		finally {
			if (out != null) {
				try {
					out.close();
				}
				catch (IOException e) {
					LOG.error("Error closing dump file", e);
				}
			}
		}
	}

	public static void dumpSoapRequestResponse(LogonOperationContext logonOperationContext, String requestString, String responseString) {
		dumpRequestResponse(logonOperationContext, DEFAULT_WEBSERVICE_NAME,
				ConfigBean::getWsserverSoapDumpDir, ConfigBean::getWsclientSoapDumpDir,
				CONFIG_PREFIX_SERVER, CONFIG_PREFIX_CLIENT, requestString, responseString);
	}

	public static void dumpRestRequestResponse(LogonOperationContext logonOperationContext, String requestString, String responseString) {
		dumpRequestResponse(logonOperationContext, "rest",
				bean -> {
					throw new IllegalArgumentException("Should never have been called");
				}, ConfigBean::getRestclientDumpDir,
				"restserver", "restclient", requestString, responseString);
	}

	private static void dumpRequestResponse(LogonOperationContext logonOperationContext, String defaultServiceName,
			Function<ConfigBean, String> serverDumpDir, Function<ConfigBean, String> clientDumpDir,
			String serverFilenameConfigPrefix, String clientFilenameConfigPrefix,
			String requestString, String responseString) {

		if ( (logonOperationContext == null) || (logonOperationContext.getConfigBean() == null) ) {
			LOG.error("config bean or logon operation context not found, dump disabled");

			return;
		}
		if (logonOperationContext.isSuppressDumping()) {
			LOG.debug("Dumping suppressed");

			return;
		}
		ConfigBean configBean = logonOperationContext.getConfigBean();
		String serviceName = StringUtils.defaultString(logonOperationContext.getServiceName(), defaultServiceName);
		String dumpDir;
		String defaultFilenameFormatString;
		if (logonOperationContext.isServerSide()) {
			dumpDir = serverDumpDir.apply(configBean);
			defaultFilenameFormatString = "incoming-" + DEFAULT_BASE_FILENAME_FORMAT_STRING;
		}
		else {
			dumpDir = clientDumpDir.apply(configBean);
			defaultFilenameFormatString = "outgoing-" + DEFAULT_BASE_FILENAME_FORMAT_STRING;
		}
		String configLabel = serviceName.toLowerCase();
		String filenameFormatString = configBean.getProperty(
				(logonOperationContext.isServerSide() ? serverFilenameConfigPrefix : clientFilenameConfigPrefix) + "."
						+ configLabel + ".dump.filename");

		Date date = logonOperationContext.getDate();
		dumpRequestResponse(serviceName, dumpDir, filenameFormatString, defaultFilenameFormatString, date,
				logonOperationContext.getSubstVariables(), requestString, responseString);
	}

	private static void dumpRequestResponse(String serviceName, String dumpDir, String filenameFormat,
			String defaultFilenameFormat, Date date, Object[] extraVars, String requestString, String responseString) {
		// request
		if (filenameFormat == null) {
			filenameFormat = defaultFilenameFormat;
		}
		if (requestString != null) {
			Object[] vars = new Object[] { serviceName, date, EXTENSION_REQUEST };
			if (extraVars != null) {
				vars = ArrayUtils.addAll(vars, extraVars);
			}
			String filename;
			try {
				filename = String.format(filenameFormat, vars);
			}
			catch (MissingFormatArgumentException e) {
				// If the xpath expression '*.dump.subst_xpath' did not evaluate
				// to the amount of values that's expected in the filename
				// format string, this exception will occur -> fallback to the
				// default format string which is not dependent on the xpath
				// variables.
				LOG.error("Error formatting filename " + filenameFormat);
				filenameFormat = defaultFilenameFormat;
				filename = String.format(defaultFilenameFormat, vars);
			}
			saveDumpFile(dumpDir + File.separator + filename, requestString);
		}
		// response
		if (responseString != null) {
			Object[] vars = new Object[] { serviceName, date, EXTENSION_RESPONSE };
			if (extraVars != null) {
				vars = ArrayUtils.addAll(vars, extraVars);
			}
			String filename = String.format(filenameFormat, vars);
			saveDumpFile(dumpDir + File.separator + filename, responseString);
		}
	}
}
