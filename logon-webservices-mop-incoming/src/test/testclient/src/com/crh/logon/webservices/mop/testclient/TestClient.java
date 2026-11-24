package com.crh.logon.webservices.mop.testclient;


/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import com.crh.logon.mop.webservices.MOPEventConsumerStub;
import com.crh.logon.mop.webservices.MOPEventStatus_type0;
import com.crh.logon.mop.webservices.MOPMessage;
import com.crh.logon.mop.webservices.MOPMessageStatus;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class TestClient {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TestClient.class);
	private static final String ARG_HELP = "help";
	private static final String ARG_PROPERTIES = "properties";
	private static final String LOGONMOPEVENT_CONSUMER = "MOPEventConsumer";
	private static final String BASE_URL = "http://localhost:15080";
	private static final String BASE_URL_CONTEXT = "/logon/webservices-mop-incoming/services/";
	private static final String FILENAME_OF_PROPERTIES = "etc/TestClient.properties";
	private static final String PROPERTY_BASE_URL = "webservices.url.base";
	private static final String PROPERTY_BASE_URL_CONTEXT = "webservices.url.context";
	private static final String PROPERTY_TESTDATA_FOLDER = "testdata.folder";
	private static final String LOGONMOPEVENTDATA_TESTDFOLDER = "testdata";

	private String serviceBaseUrl = null;
	private Properties testProperties = null;
	private boolean validArguments = true;

	/**
	 * Default constructor.
	 * @param arguments
	 */
	private TestClient(String[] arguments) {
		// [[
		Object[][] params = {{"arguments", arguments}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "TestClient", params);
		}
		// ]]
		try {
			
			String propertiesFileName = FILENAME_OF_PROPERTIES;
			boolean displayUsageInfo = false;
			
			if (arguments != null) {
				for (int index = 0; (index < arguments.length) && validArguments; index++) {
					String argument = arguments[index];
					argument = "".equals(argument) ? null : argument;
					if (argument != null) {
			
						if (argument.equals("-" + ARG_HELP)) {
							displayUsageInfo = true;
			
							break;
						}
			
						if (argument.startsWith("-" + ARG_PROPERTIES + "=")) {
							propertiesFileName = argument.substring(ARG_PROPERTIES.length() + 2);
			
							continue;
						}
			
						this.validArguments = false;
					}
				}
			}
			
			if (!validArguments) {
				displayInvalidArguments(arguments);
			
				return;
			}
			
			if (displayUsageInfo) {
				displayUsageInfo();
			
				this.validArguments = false;
			
				return;
			}
			
			this.testProperties = loadProperties(propertiesFileName);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * @param arguments
	 */
	private final void displayInvalidArguments(String arguments[]) {
		// [[
		Object[][] params = {{"arguments", arguments}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "displayInvalidArguments", params);
		}
		// ]]
		try {
			
			StringBuffer buffer = new StringBuffer();
			
			for (int index = 0; index < arguments.length; index++) {
				if (index != 0) {
					buffer.append(" ");
				}
			
				buffer.append(arguments[index]);
			}
			
			LOG.error("TestClient has been called with invalid arguments: " + buffer);
			
			displayUsageInfo();
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * 
	 */
	private final void displayUsageInfo() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "displayUsageInfo");
		}
		// ]]
		try {
			
			LOG.info("Available options:");
			LOG.info("\t-" + ARG_PROPERTIES + "=<file-path>"
					+ " -> use this property-file instead the default value("
					+ FILENAME_OF_PROPERTIES + ")");
			LOG.info("\t-" + ARG_HELP + " -> display this info.");
			
			LOG.info("Available webservices: ");
			LOG.info("\t" + LOGONMOPEVENT_CONSUMER);
			LOG.info("Example:");
			LOG.info("-" + ARG_PROPERTIES + "=etc/test.properties");
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * @param path
	 * @return
	 */
	protected static File[] getListOfFiles(String path) {
		// [[
		File[] result = null;
		Object[][] params = {{"path", path}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TestClient.class, "getListOfFiles", params);
		}
		// ]]
		try {
			
			if (StringUtils.notEmpty(path)) {
				File directory = new File(path);
			
				if (directory.isDirectory()) {
					List tempList = new ArrayList();
			
					for (File dirItem : directory.listFiles()) {
						if (dirItem.isFile()) {
							tempList.add(dirItem);
						}
					}
			
					if (!tempList.isEmpty()) {
						result = new File[tempList.size()];
			
						for (int index = 0; index < tempList.size(); index++) {
							result[index] = (File) tempList.get(index);
						}
					}
				}
			}
			
			return result;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	private final String getTestProperty(String key, String defaultValue) {
		// [[
		String result = null;
		Object[][] params = {{"key", key},
							{"defaultValue", defaultValue}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getTestProperty", params);
		}
		// ]]
		try {
			String propertyValue = defaultValue;
			
			if (testProperties != null) {
				propertyValue = testProperties.getProperty(key);
				propertyValue = "".equals(propertyValue) ? null : propertyValue;
				if (propertyValue == null) {
					propertyValue = defaultValue;
				}
			}
			
			return result = propertyValue;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * @param propertiesFileName
	 * @return
	 */
	private final Properties loadProperties(String propertiesFileName) {
		// [[
		Properties result = null;
		Object[][] params = {{"propertiesFileName", propertiesFileName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadProperties", params);
		}
		// ]]
		try {
			
			Properties properties = new Properties();
			FileInputStream inFile = new FileInputStream(propertiesFileName);
			
			properties.load(inFile);
			
			result = properties;
			
		}
		catch (FileNotFoundException fnfe) {
			LOG.info("Property file '" + propertiesFileName + "' not found, using defaults.");
		}
		catch (IOException ioe) {
			LOG.info("Cannot read property file '" + propertiesFileName + "', using defaults.");
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
		return result;
	}

	/**
	 * 
	 */
	private final void performTests() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "performTests");
		}
		// ]]
		try {
			
			if (validArguments) {
			
				this.serviceBaseUrl = getTestProperty(PROPERTY_BASE_URL, BASE_URL)
						+ getTestProperty(PROPERTY_BASE_URL_CONTEXT, BASE_URL_CONTEXT);
				String url = this.serviceBaseUrl + LOGONMOPEVENT_CONSUMER;
			
				String testDir = (String) getTestProperty(PROPERTY_TESTDATA_FOLDER,
						LOGONMOPEVENTDATA_TESTDFOLDER);
			
				File[] testFiles = getListOfFiles(testDir);
			
				if (testFiles == null) {
					LOG.info("There are no testfiles to process in " + testDir);
			
					return;
				}
			
				MOPEventConsumerStub stub = new MOPEventConsumerStub(url);
			
				for (int index = 0; index < testFiles.length; index++) {
			
					XMLInputFactory factory = XMLInputFactory.newInstance();
					FileInputStream fis = new FileInputStream(testFiles[index]);
					XMLStreamReader reader = factory.createXMLStreamReader(fis);
			
					MOPMessage logonMOPMessage = (MOPMessage) TestHelper.parseWebserviceRequestObject(
					        MOPMessage.class, reader);
			
					LOG.info("call WS MopEventConsumer now");
					MOPMessageStatus status = stub.consume(logonMOPMessage);
					if (LOG.isInfoEnabled()) {
					    MOPEventStatus_type0 [] eventStatus = status.getMOPEventStatus();
			
					    for (int i = 0; i < eventStatus.length; i++) {
	                        LOG.info("Statuscode: " + eventStatus[i].getCode() + " " + eventStatus[i].getTxt() + eventStatus[i].getContextValue());
					    }
					}
				}
			}
		}
		catch (Exception e) {
			LOG.error("Calling MopEventConsumer FAILED.");
			ExceptionUtil.errException(LOG, e, null);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * @param args
	 */
	public static void main(java.lang.String args[]) {
		// [[
		Object[][] params = {{"args", args}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TestClient.class, "main", params);
		}
		// ]]
		try {
			
			TestClient client = new TestClient(args);
			
			client.performTests();
		}
		catch (Exception exception) {
			LOG.error("exception caught: ", exception);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	private static final class TestHelper {

		private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TestHelper.class);

		private static final String MEMBER_DOCUMENT_FACTORY = "Factory";

		private static final String METHOD_PARSE = "parse";

		/**
		 * @param requestClass
		 * @param fileReader
		 * @return
		 */
		public static final Object parseWebserviceRequestObject(Class requestClass,
				XMLStreamReader fileReader) throws InvocationTargetException, Exception {
			// [[
			Object result = null;
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "requestClass", requestClass },
						{ "fileReader", fileReader } };
				LOG.entering(TestHelper.class, "parseWebserviceRequestObject", params);
			}
			// ]]
			try {
				Object requestDocument = null;

				if ((requestClass != null) && (fileReader != null)) {
					for (Class innerClass : requestClass.getClasses()) {
						String innerClassName = innerClass.getSimpleName();

						if (StringUtils.notEmpty(innerClassName)
								&& innerClassName.endsWith(MEMBER_DOCUMENT_FACTORY)) {
							requestDocument = callMethod(innerClass, METHOD_PARSE,
									new Object[] { fileReader },
									new Class[] { XMLStreamReader.class });

							break;
						}
					}
				}

				return (result = requestDocument);
			}
			finally {
				// [[
				if (logDebugEnabled) {
					LOG.exiting(result);
				}
				// ]]
			}
		}

		/**
		 * @param object
		 * @param methodName
		 * @param parameters
		 * @param parameterTypes
		 * @return
		 */
		private static final Object callMethod(Object object, String methodName,
				Object[] parameters, Class[] parameterTypes) throws InvocationTargetException,
				Exception {
			// [[
			Object result = null;
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "object", object }, { "methodName", methodName },
						{ "parameters", parameters }, { "parameterTypes", parameterTypes } };
				LOG.entering(TestHelper.class, "callMethod", params);
			}
			// ]]
			try {
				if ((object != null) && (StringUtils.notEmpty(methodName))) {
					Class clazz = (object instanceof Class ? (Class) object : object.getClass());
					Method method = clazz.getMethod(methodName, parameterTypes);

					result = method.invoke(object, parameters);
				}

				return result;
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
}
