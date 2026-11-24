/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.crh.logon.to.ffax.FFaxStub.SendFaxFile;
import com.crh.logon.to.ffax.FFaxStub.SendFaxText;
import com.crh.logon.to.fsms.FSmsStub.SendSms;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.webservices.clients.LogonToFFax;
import com.crh.logon.webservices.clients.LogonToFSms;
import com.crh.logon.webservices.clients.beans.ferrari.FileMessageBean;
import com.crh.logon.webservices.clients.beans.ferrari.ResponseBean;
import com.crh.logon.webservices.clients.beans.ferrari.TextMessageBean;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


/**
 * @author AdNovum Informatik AG
 */
public class TestClient {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(TestClient.class);

	private static final String ARG_HELP = "help";
	private static final String ARG_PROPERTIES = "properties";
	private static final String ARG_TESTSERVICE = "testService";
	private static final String ARG_TESTOPERATION = "testOperation";
	private static final String FILENAME_OF_PROPERTIES = "etc/TestClient.properties";
	private static final String PROPERTY_AUTH_USER_POSTFIX = ".auth.user";
	private static final String PROPERTY_AUTH_PWD_POSTFIX = ".auth.pwd";
	private static final String PROPERTY_BASE_URL_POSTFIX = ".url.base";
	private static final String PROPERTY_POSTFIX = ".property";
	private static final String PROPERTY_TESTDATA_FOLDER_POSTFIX = ".testdata.folder";

	protected static final String SERVICE_LOGON_TO_FSMS = "LogonToFSms";
	protected static final String SERVICE_LOGON_TO_FSMS_BASE_URL = "http://localhost:8090/mockFSmsSoap";
	protected static final String SERVICE_LOGON_TO_FSMS_TESTDFOLDER = "testdata/logontofsms";
	protected static final String SERVICE_LOGON_TO_FSMS_AUTH_USER_PROP = ConfigBean.CFG_WSCLIENTFSMSUSER;
	protected static final String SERVICE_LOGON_TO_FSMS_AUTH_PWD_PROP = ConfigBean.CFG_WSCLIENTFSMSPASSWORD;
	protected static final String SERVICE_LOGON_TO_FSMS_HOST_URL_PROP = ConfigBean.CFG_WSCLIENTFSMSHOSTURL;
	protected static final String SERVICE_LOGON_TO_FSMS_HELLOWORLD = "HelloWorld";
	protected static final String SERVICE_LOGON_TO_FSMS_SENDSMS = "SendSms";

	protected static final Map<String, String> SERVICE_LOGON_TO_FSMS_OPERATIONS = new HashMap<String, String>();
	static {
		SERVICE_LOGON_TO_FSMS_OPERATIONS.put(SERVICE_LOGON_TO_FSMS_HELLOWORLD,
				"");
		SERVICE_LOGON_TO_FSMS_OPERATIONS.put(SERVICE_LOGON_TO_FSMS_SENDSMS,
				"testdata/logontofsms/sendsms");
	}

	protected static final String SERVICE_LOGON_TO_FFAX = "LogonToFFax";
	protected static final String SERVICE_LOGON_TO_FFAX_BASE_URL = "http://localhost:8091/mockFFaxSoap";
	protected static final String SERVICE_LOGON_TO_FFAX_TESTDFOLDER = "testdata/logontoffax";
	protected static final String SERVICE_LOGON_TO_FFAX_AUTH_USER_PROP = ConfigBean.CFG_WSCLIENTFFAXUSER;
	protected static final String SERVICE_LOGON_TO_FFAX_AUTH_PWD_PROP = ConfigBean.CFG_WSCLIENTFFAXPASSWORD;
	protected static final String SERVICE_LOGON_TO_FFAX_HOST_URL_PROP = ConfigBean.CFG_WSCLIENTFFAXHOSTURL;
	protected static final String SERVICE_LOGON_TO_FFAX_HELLOWORLD = "HelloWorld";
	protected static final String SERVICE_LOGON_TO_FFAX_SENDFAXFILE = "SendFaxFile";
	protected static final String SERVICE_LOGON_TO_FFAX_SENDFAXTEXT = "SendFaxText";
	protected static final String SERVICE_LOGON_TO_FFAX_FILEATTACH_PROP = "LogonToFFax.SendFaxFile.file.attachment";
	protected static final String SERVICE_LOGON_TO_FFAX_FILEATTACH_DEFAULT = "testdata/logontoffax/testdoc.pdf";
	protected static final String SERVICE_LOGON_TO_FFAX_FILEATTACH_MIMETYPE_PROP = "LogonToFFax.SendFaxFile.file.mimetype";
	protected static final String SERVICE_LOGON_TO_FFAX_FILEATTACH_MIMETYPE_DEFAULT = "application/pdf";

	protected static final Map<String, String> SERVICE_LOGON_TO_FFAX_OPERATIONS = new HashMap<String, String>();
	static {
		SERVICE_LOGON_TO_FFAX_OPERATIONS.put(SERVICE_LOGON_TO_FFAX_HELLOWORLD,
				"");
		SERVICE_LOGON_TO_FFAX_OPERATIONS.put(SERVICE_LOGON_TO_FFAX_SENDFAXFILE,
				"testdata/logontoffax/sendfaxfile");
		SERVICE_LOGON_TO_FFAX_OPERATIONS.put(SERVICE_LOGON_TO_FFAX_SENDFAXTEXT,
				"testdata/logontoffax/sendfaxtext");
	}

	protected static final Map<String, TestClientConfig> SUPPORTED_SERVICENAMES = new HashMap<String, TestClientConfig>();
	static {
		SUPPORTED_SERVICENAMES.put(SERVICE_LOGON_TO_FSMS,
				TestClientConfig.LOGONTOFSMS);
		SUPPORTED_SERVICENAMES.put(SERVICE_LOGON_TO_FFAX,
				TestClientConfig.LOGONTOFFAX);
	}

	private List<TestClientConfig> servicesToTest = null;

	private Properties testProperties = null;

	private boolean validArguments = true;

	/**
	 * Default constructor.
	 * @param argsString
	 */
	private TestClient(String argsString) {
		// [[
		Object[][] params = {{"argsString", argsString}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "TestClient", params);
		}
		// ]]
		try {
			List<String> serviceNames = new ArrayList<String>();
			String propertiesFileName = FILENAME_OF_PROPERTIES;
			boolean displayUsageInfo = false;
			
			this.servicesToTest = new ArrayList<TestClientConfig>();
			
			if (StringUtils.notEmpty(argsString)) {
				StringTokenizer tokenizer = new StringTokenizer(argsString,
						"-", false);
			
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					String[] subTokens = token.split("[ =]{1}");
					String option = subTokens[0];
			
					/*
					// tracing arguments
					System.out.println("Token: " + token);
					System.out.println("subTokens: " + subTokens);
					System.out.println("subTokens: ");
			
					for (int index = 0; (index < subTokens.length); index++) {
						System.out.println(index + ". subToken: "
								+ subTokens[index]);
					}*/
			
					if (ARG_HELP.equals(option)) {
						displayUsageInfo = true;
			
						break;
					}
					else if (ARG_PROPERTIES.equals(option)
							&& (subTokens.length == 2)) {
						propertiesFileName = subTokens[1];
			
						continue;
					}
					else if (ARG_TESTSERVICE.equals(option)
							&& (subTokens.length > 1)) {
						boolean validOptionValue = true;
			
						for (int index = 1; (index < subTokens.length)
								&& validOptionValue; index++) {
							String serviceToTest = subTokens[index];
			
							validOptionValue = SUPPORTED_SERVICENAMES
									.containsKey(serviceToTest);
			
							if (validOptionValue) {
								TestClientConfig tcConfig = (TestClientConfig) SUPPORTED_SERVICENAMES
										.get(serviceToTest);
			
								serviceNames.add(serviceToTest);
			
								if (!this.servicesToTest.contains(tcConfig)) {
									this.servicesToTest.add(tcConfig);
								}
							}
						}
			
						if (validOptionValue) {
							continue;
						}
					}
					else if (ARG_TESTOPERATION.equals(option)
							&& (subTokens.length > 1)
							&& (serviceNames.size() > 0)) {
						String serviceToTest = (String) serviceNames
								.get(serviceNames.size() - 1);
						TestClientConfig tcConfig = getTestClientConfig(serviceToTest);
						boolean validOptionValue = true;
			
						if (tcConfig != null) {
							Set availableOperationNames = tcConfig
									.getAvailableOperationNames();
			
							validOptionValue = (availableOperationNames != null);
			
							if (validOptionValue) {
								for (int index = 1; (index < subTokens.length)
										&& validOptionValue; index++) {
									String operationToTest = subTokens[index];
			
									validOptionValue = availableOperationNames
											.contains(operationToTest);
			
									if (validOptionValue) {
										Map<String, String> currentOperations = tcConfig
												.getCurrentOperations();
										Set<String> currentOperationNames = (currentOperations == null ? null
												: currentOperations.keySet());
			
										if ((currentOperationNames != null)
												&& (!currentOperationNames
														.contains(operationToTest))) {
											currentOperations.put(
													operationToTest, "");
										}
									}
								}
							}
						}
			
						if (validOptionValue) {
							continue;
						}
					}
			
					this.validArguments = false;
				}
			}
			
			if (!this.validArguments) {
				displayInvalidArguments(argsString);
			
				return;
			}
			
			if (displayUsageInfo) {
				displayUsageInfo();
			
				this.validArguments = false;
			
				return;
			}
			
			this.testProperties = loadProperties(propertiesFileName);
			
			initTestClient();
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
	 * @param argsString
	 */
	private final void displayInvalidArguments(String argsString) {
		// [[
		Object[][] params = {{"argsString", argsString}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "displayInvalidArguments", params);
		}
		// ]]
		try {
			LOG.error("TestClient has been called with invalid arguments: "
					+ argsString);
			
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
			LOG.info("\t-" + ARG_PROPERTIES
					+ "=<file-path>"
					+ " -> use this property-file instead the default value("
					+ FILENAME_OF_PROPERTIES + ")");
			LOG.info("\t-" + ARG_TESTSERVICE
					+ "=<webservice-name> -> test webservice");
			LOG.info("\t-" + ARG_TESTOPERATION
					+ "=<operation-name> -> test operation of the previously defined webservice");
			LOG.info("\t-" + ARG_HELP + " -> display this info.");
			
			LOG.info("Available webservices - operations: ");
			
			Iterator<String> sIterator = SUPPORTED_SERVICENAMES.keySet().iterator();
			
			while (sIterator.hasNext()) {
				String serviceName = (String) sIterator.next();
				StringBuffer buffer = new StringBuffer();
			
				if (StringUtils.notEmpty(serviceName)) {
					TestClientConfig tcConfig = (TestClientConfig) SUPPORTED_SERVICENAMES
							.get(serviceName);
					Set availableOperationNames = (tcConfig == null ? null
							: tcConfig.getAvailableOperationNames());
			
					buffer.append(serviceName + " - ");
			
					if (availableOperationNames != null) {
						Iterator oIterator = availableOperationNames.iterator();
			
						while (oIterator.hasNext()) {
							buffer.append((String) oIterator.next());
			
							if (oIterator.hasNext()) {
								buffer.append(" ");
							}
						}
					}
				}
			
				LOG.info("\t" + buffer);
			}
			
			LOG.info("Example:");
			LOG.info("-" + ARG_PROPERTIES + " etc/test.properties -"
					+ " -" + ARG_TESTSERVICE + " " + SERVICE_LOGON_TO_FSMS + " -"
					+ ARG_TESTOPERATION + " " + SERVICE_LOGON_TO_FSMS_HELLOWORLD + " "
					+ SERVICE_LOGON_TO_FSMS_SENDSMS);
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
	 * @param serviceName
	 * @return
	 */
	private final TestClientConfig getTestClientConfig(String serviceName) {
		// [[
		TestClientConfig result = null;
		Object[][] params = {{"serviceName", serviceName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getTestClientConfig", params);
		}
		// ]]
		try {
			if (StringUtils.notEmpty(serviceName)) {
				int nbrOfServicesToTest = (this.servicesToTest == null ? 0
						: this.servicesToTest.size());
			
				for (int index = 0; index < nbrOfServicesToTest; index++) {
					TestClientConfig tcConfig = (TestClientConfig) this.servicesToTest
							.get(index);
			
					if ((tcConfig != null)
							&& (serviceName.equals(tcConfig.getServiceName()))) {
						result = tcConfig;
			
						break;
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
	 * 
	 */
	private final void initTestClient() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initTestClient");
		}
		// ]]
		try {
			if (this.servicesToTest.isEmpty()) {
				Iterator<String> sIterator = SUPPORTED_SERVICENAMES.keySet().iterator();
			
				while (sIterator.hasNext()) {
					this.servicesToTest.add(SUPPORTED_SERVICENAMES
							.get(sIterator.next()));
				}
			}
			
			for (int index = 0; index < this.servicesToTest.size(); index++) {
				TestClientConfig tcConfig = (TestClientConfig) this.servicesToTest
						.get(index);
			
				if (tcConfig != null) {
					String serviceName = tcConfig.getServiceName();
					String baseUrl = tcConfig.getServiceUrl();
					Map<String, String> operations = tcConfig.getCurrentOperations();
					String sysPropUrl = tcConfig.getSysPropServiceUrl();
					String sysPropAuthPwd = tcConfig.getSysPropAuthPwd();
					String sysPropAuthUser = tcConfig.getSysPropAuthUser();
					Set<String> operationNames = null;
					String propertyValue = null;
			
					// folders of testdata
					if ((operations == null) || (operations.isEmpty())) {
						tcConfig.setAvailableOpsToCurrentOps();
			
						operations = tcConfig.getCurrentOperations();
					}
			
					operationNames = operations.keySet();
			
					if (operationNames != null) {
						Map defaultOperations = tcConfig
								.getAvailableOperations();
						Iterator<String> oIterator = operationNames.iterator();
			
						while (oIterator.hasNext()) {
							String operationName = (String) oIterator.next();
							String testdataFolderKey = serviceName + "."
									+ operationName
									+ PROPERTY_TESTDATA_FOLDER_POSTFIX;
							String defaultValue = (defaultOperations == null ? ""
									: (String) defaultOperations
											.get(operationName));
							String testdataFolder = getTestProperty(
									testdataFolderKey, defaultValue);
			
							operations.put(operationName, testdataFolder);
						}
					}
			
					// service-URL
					propertyValue = setSystemProperty(serviceName,
							PROPERTY_BASE_URL_POSTFIX, sysPropUrl, baseUrl);
					tcConfig.setServiceUrl(propertyValue);
			
					// auth-user
					propertyValue = setSystemProperty(serviceName,
							PROPERTY_AUTH_USER_POSTFIX, sysPropAuthUser, null);
					tcConfig.setAuthUser(propertyValue);
			
					// auth-pwd
					propertyValue = setSystemProperty(serviceName,
							PROPERTY_AUTH_PWD_POSTFIX, sysPropAuthPwd, null);
					tcConfig.setAuthPwd(propertyValue);
				}
			}
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
			LOG.info("Property file '" + propertiesFileName
					+ "' not found, using defaults.");
		}
		catch (IOException ioe) {
			LOG.info("Cannot read property file '" + propertiesFileName
					+ "', using defaults.");
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
			if (this.validArguments) {
				for (int index = 0; index < this.servicesToTest.size(); index++) {
					TestClientConfig tcConfig = (TestClientConfig) this.servicesToTest
							.get(index);
			
					if (tcConfig != null) {
						String serviceName = tcConfig.getServiceName();
			
						if (SERVICE_LOGON_TO_FSMS.equals(serviceName)) {
							LogonToFSmsClient.testOperations(tcConfig);
						}
						else if (SERVICE_LOGON_TO_FFAX.equals(serviceName)) {
							LogonToFFaxClient.testOperations(this, tcConfig);
						}
					}
				}
			}
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
	 * @param serviceName
	 * @param propertyPostfix
	 * @param defaultSysPropName
	 * @param defaultPropertyValue
	 * @return
	 */
	private String setSystemProperty(String serviceName, String propertyPostfix,
			String defaultSysPropName, String defaultPropertyValue) {
		// [[
		String result = null;
		Object[][] params = {{"serviceName", serviceName},
							{"propertyPostfix", propertyPostfix},
							{"defaultSysPropName", defaultSysPropName},
							{"defaultPropertyValue", defaultPropertyValue}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setSystemProperty", params);
		}
		// ]]
		try {
			String propertyValue = getTestProperty(serviceName + propertyPostfix,
					defaultPropertyValue);
			
			if (StringUtils.notEmpty(propertyValue)) {
				String systemPropertyName = getTestProperty(serviceName
						+ propertyPostfix + PROPERTY_POSTFIX,
						defaultSysPropName);
			
				if (StringUtils.notEmpty(systemPropertyName)) {
					System.setProperty(systemPropertyName, propertyValue);
				}
			
				result = propertyValue;
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
	protected final String getTestProperty(String key, String defaultValue) {
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
			
			if (this.testProperties != null) {
				propertyValue = this.testProperties.getProperty(key);
			
				if (StringUtils.empty(propertyValue)) {
					propertyValue = defaultValue;
				}
			}
			
			return (result = propertyValue);
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
			TestClient client = null;
			String argsString = null;
			
			// retrieving arguments for the client 
			if (args != null) {
				StringBuilder sBuilder = new StringBuilder();
			
				for (int index = 0; (index < args.length); index++) {
					if (index > 0) {
						sBuilder.append(" ");
					}
			
					sBuilder.append(args[index]);
				}
			
				argsString = sBuilder.toString();
			}
			
			client = new TestClient(argsString);
			
			//LOG.info(client.toString());
			
			// performing the required tests
			client.performTests();
		}
		catch (Exception exception) {
			ExceptionUtil.errException(LOG, exception, null);
		} 
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);

		builder.append("servicesToTest", this.servicesToTest);
		builder.append("testProperties", this.testProperties);
		builder.append("validArguments", this.validArguments);

		return builder.toString();
	}

	private static final class LogonToFSmsClient {
		private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonToFSmsClient.class);

		/**
		 * @param config
		 */
		public static final void testOperations(TestClientConfig tcConfig) {
			// [[
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "tcConfig", tcConfig } };
				LOG.entering(LogonToFSmsClient.class, "testOperations", params);
			}
			// ]]
			try {
				LOG.info("Testing operations of service: ");

				if (tcConfig != null) {
					LOG.info("\t - " + tcConfig.getServiceName() + " at "
							+ tcConfig.getServiceUrl());

					Map<String, String> currentOperations = tcConfig.getCurrentOperations();
					ConfigBean configBean = new ConfigBean();

					if (logDebugEnabled) {
						configBean.log();
					}

					if ((currentOperations != null)
							&& (!currentOperations.isEmpty())) {
						Iterator<String> oIterator = currentOperations.keySet()
								.iterator();

						while (oIterator.hasNext()) {
							String operationName = (String) oIterator.next();
							String testdataFolder = (String) currentOperations
									.get(operationName);

							if (SERVICE_LOGON_TO_FSMS_HELLOWORLD.equals(operationName)) {
								LogonToFSmsClient.HelloWorld(configBean);
							}
							else if (SERVICE_LOGON_TO_FSMS_SENDSMS
									.equals(operationName)) {
								LogonToFSmsClient.SendSms(
										testdataFolder, configBean);
							}
						}
					}
				}
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
		 * @param configBean
		 */
		private static final void HelloWorld(ConfigBean configBean) {
			// [[
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "configBean", configBean } };
				LOG.entering(LogonToFSmsClient.class, "HelloWorld", params);
			}
			// ]]
			try {
				String helloWorld = LogonToFSms.helloWorld(configBean);

				LOG.info("\t - HelloWorld = " + helloWorld);
			}
			catch (Exception exception) {
				LOG.error("Calling operation HelloWorld FAILED.");
				ExceptionUtil.errException(LOG, exception, null);
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
		 * @param testdataFolder
		 * @param configBean
		 */
		private static final void SendSms(String testdataFolder,
				ConfigBean configBean) {
			// [[
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "testdataFolder", testdataFolder },
						{ "configBean", configBean } };
				LOG.entering(LogonToFSmsClient.class, "SendSms", params);
			}
			// ]]
			try {
				File[] testFiles = null;

				if (StringUtils.empty(testdataFolder)) {
					LOG.info("No testdata has been provided for SendSms.");

					return;
				}

				testFiles = TestHelper.getListOfFiles(testdataFolder);

				if (testFiles == null) {
					LOG.info("There are no testfiles to process in " + testdataFolder);

					return;
				}

				for (int index = 0; index < testFiles.length; index++) {
					File testFile = testFiles[index];

					if (testFile != null) {
						LOG.info("\t- processing testfile: "
								+ testFile.getName());

						SendSms document = (SendSms) TestHelper.parseTestFile(
								SendSms.class, testFile);
						TextMessageBean smsBean = new TextMessageBean(document
								.getArgsNumber(), document.getArgsText());
						ResponseBean response = LogonToFSms.sendSms(smsBean,
								configBean);

						TestHelper.processFerrariResponse(
								SERVICE_LOGON_TO_FSMS,
								SERVICE_LOGON_TO_FSMS_SENDSMS, response);
					}
				}
			} 
			catch (Exception exception) {
				LOG.error("Calling operation SendSms FAILED.");

				ExceptionUtil.errException(LOG, exception, null);
			}
			finally {
				// [[
				if (logDebugEnabled) {
					LOG.exiting();
				}
				// ]]
			}
		}
	}

	private static final class LogonToFFaxClient {

		private static final JcanLogger LOG = JcanLoggerFactory
				.getLogger(LogonToFFaxClient.class);

		/**
		 * @param testClient
		 * @param tcConfig
		 */
		public static final void testOperations(TestClient testClient,
				TestClientConfig tcConfig) {
			// [[
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "testClient", testClient },
						{ "tcConfig", tcConfig } };
				LOG.entering(LogonToFFaxClient.class, "testOperations", params);
			}
			// ]]
			try {
				LOG.info("Testing operations of service: ");

				if (tcConfig != null) {
					LOG.info("\t - " + tcConfig.getServiceName() + " at "
							+ tcConfig.getServiceUrl());

					Map<String, String> currentOperations = tcConfig.getCurrentOperations();
					ConfigBean configBean = new ConfigBean();

					if (logDebugEnabled) {
						configBean.log();
					}

					if ((currentOperations != null)
							&& (!currentOperations.isEmpty())) {
						Iterator<String> oIterator = currentOperations.keySet()
								.iterator();

						while (oIterator.hasNext()) {
							String operationName = (String) oIterator.next();
							String testdataFolder = (String) currentOperations
									.get(operationName);

							if (SERVICE_LOGON_TO_FFAX_HELLOWORLD
									.equals(operationName)) {
								LogonToFFaxClient.HelloWorld(configBean);
							}
							else if (SERVICE_LOGON_TO_FFAX_SENDFAXFILE
									.equals(operationName)) {
								LogonToFFaxClient.SendFaxFile(testdataFolder,
										configBean, testClient);
							}
							else if (SERVICE_LOGON_TO_FFAX_SENDFAXTEXT
									.equals(operationName)) {
								LogonToFFaxClient.SendFaxText(testdataFolder,
										configBean);
							}
						}
					}
				}
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
		 * @param configBean
		 */
		private static final void HelloWorld(ConfigBean configBean) {
			// [[
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "configBean", configBean } };
				LOG.entering(LogonToFFaxClient.class, "HelloWorld", params);
			}
			// ]]
			try {
				String helloWorld = LogonToFFax.helloWorld(configBean);

				LOG.info("\t - HelloWorld = " + helloWorld);
			}
			catch (Exception exception) {
				LOG.error("Calling operation HelloWorld FAILED.");
				ExceptionUtil.errException(LOG, exception, null);
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
		 * @param testdataFolder
		 * @param configBean
		 * @param testClient
		 */
		private static final void SendFaxFile(String testdataFolder,
				ConfigBean configBean, TestClient testClient) {
			// [[
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "testdataFolder", testdataFolder },
						{ "configBean", configBean } };
				LOG.entering(LogonToFFaxClient.class, "SendFaxFile", params);
			}
			// ]]
			try {
				File[] testFiles = null;
				byte[] attachment = null;
				File attachmentFile = null;
				String attachmentPath = null;
				String attachmentMimeType = null;

				if (StringUtils.empty(testdataFolder)) {
					LOG.info("No testdata has been provided for SendFaxFile.");

					return;
				}

				testFiles = TestHelper.getListOfFiles(testdataFolder);

				if (testFiles == null) {
					LOG.info("There are no testfiles to process in "
							+ testdataFolder);

					return;
				}

				attachmentPath = testClient.getTestProperty(
						SERVICE_LOGON_TO_FFAX_FILEATTACH_PROP,
						SERVICE_LOGON_TO_FFAX_FILEATTACH_DEFAULT);

				attachmentMimeType = testClient.getTestProperty(
						SERVICE_LOGON_TO_FFAX_FILEATTACH_MIMETYPE_PROP,
						SERVICE_LOGON_TO_FFAX_FILEATTACH_MIMETYPE_DEFAULT);

				attachmentFile = new File(attachmentPath);

				try {
					attachment = TestHelper.getFileContent(attachmentFile);
				}
				catch (IOException ioException) {
					LOG.error("Unable to open and retrieve the content of the test-attachment: " 
							+ attachmentPath + "!");

					throw ioException;
				}

				for (int index = 0; index < testFiles.length; index++) {
					File testFile = testFiles[index];

					if (testFile != null) {
						LOG.info("\t- processing testfile: "
								+ testFile.getName());

						SendFaxFile document = (SendFaxFile) TestHelper
								.parseTestFile(SendFaxFile.class, testFile);
						FileMessageBean bean = new FileMessageBean(document
								.getArgsNumber(), attachmentMimeType,
								attachmentFile.getName(), attachment);
						ResponseBean response = LogonToFFax.sendFaxFile(bean,
								configBean);

						TestHelper.processFerrariResponse(
								SERVICE_LOGON_TO_FFAX,
								SERVICE_LOGON_TO_FFAX_SENDFAXFILE, response);
					}
				}
			}
			catch (Exception exception) {
				LOG.error("Calling operation SendFaxFile FAILED.");

				ExceptionUtil.errException(LOG, exception, null);
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
		 * @param testdataFolder
		 * @param configBean
		 */
		private static final void SendFaxText(String testdataFolder,
				ConfigBean configBean) {
			// [[
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "testdataFolder", testdataFolder },
						{ "configBean", configBean } };
				LOG.entering(LogonToFFaxClient.class, "SendFaxText", params);
			}
			// ]]
			try {
				File[] testFiles = null;

				if (StringUtils.empty(testdataFolder)) {
					LOG.info("No testdata has been provided for SendFaxText.");

					return;
				}

				testFiles = TestHelper.getListOfFiles(testdataFolder);

				if (testFiles == null) {
					LOG.info("There are no testfiles to process in "
							+ testdataFolder);

					return;
				}

				for (int index = 0; index < testFiles.length; index++) {
					File testFile = testFiles[index];

					if (testFile != null) {
						LOG.info("\t- processing testfile: "
								+ testFile.getName());

						SendFaxText document = (SendFaxText) TestHelper
								.parseTestFile(SendFaxText.class, testFile);
						TextMessageBean bean = new TextMessageBean(document
								.getArgsNumber(), document.getArgsText());
						ResponseBean response = LogonToFFax.sendFaxText(bean,
								configBean);

						TestHelper.processFerrariResponse(
								SERVICE_LOGON_TO_FFAX,
								SERVICE_LOGON_TO_FFAX_SENDFAXTEXT, response);
					}
				}
			}
			catch (Exception exception) {
				LOG.error("Calling operation SendFaxText FAILED.");

				ExceptionUtil.errException(LOG, exception, null);
			}
			finally {
				// [[
				if (logDebugEnabled) {
					LOG.exiting();
				}
				// ]]
			}
		}
	}

	private static final class TestHelper {

		private static final JcanLogger LOG = JcanLoggerFactory
				.getLogger(TestHelper.class);

		private static final String MEMBER_DOCUMENT_FACTORY = "Factory";

		private static final String METHOD_PARSE = "parse";

		/**
		 * @param object
		 * @param methodName
		 * @param parameters
		 * @param parameterTypes
		 * @return
		 * @throws Exception
		 */
		private static final Object callMethod(Object object,
				String methodName, Object[] parameters, Class[] parameterTypes)
				throws Exception {
			// [[
			Object result = null;
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "object", object },
						{ "methodName", methodName },
						{ "parameters", parameters },
						{ "parameterTypes", parameterTypes } };
				LOG.entering(TestHelper.class, "callMethod", params);
			}
			// ]]
			try {
				if ((object != null) && (StringUtils.notEmpty(methodName))) {
					Class clazz = (object instanceof Class ? (Class) object
							: object.getClass());
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

		/**
		 * @param requestClass
		 * @param xmlReader
		 * @return
		 * @throws Exception
		 */
		private static final Object parseWebserviceRequestObject(
				Class requestClass, XMLStreamReader xmlReader) throws Exception {
			// [[
			Object result = null;
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "requestClass", requestClass },
						{ "xmlReader", xmlReader } };
				LOG.entering(TestHelper.class, "parseWebserviceRequestObject",
						params);
			}
			// ]]
			try {
				Object requestDocument = null;

				if ((requestClass != null) && (xmlReader != null)) {
					for (Class innerClass : requestClass.getClasses()) {
						String innerClassName = innerClass.getSimpleName();

						if (StringUtils.notEmpty(innerClassName)
								&& innerClassName
										.endsWith(MEMBER_DOCUMENT_FACTORY)) {
							requestDocument = callMethod(innerClass,
									METHOD_PARSE, new Object[] { xmlReader },
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
		 * @param attachmentFile
		 * @return
		 * @throws IOException
		 */
		protected static final byte[] getFileContent(File attachmentFile)
				throws IOException {
			// [[
			byte[] result = null;
			BufferedInputStream biStream = null;
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "attachmentFile", attachmentFile } };
				LOG.entering(TestHelper.class, "getFileContent", params);
			}
			// ]]
			try {
				final int blockSize = 2048;

				FileInputStream fiStream = new FileInputStream(attachmentFile);
				List content = new ArrayList();
				byte[] buffer = new byte[blockSize];
				int totalNbrOfBytesRead = 0;
				int nbrOfBytesRead = 0;

				biStream = new BufferedInputStream(fiStream);

				while ((nbrOfBytesRead = biStream.read(buffer)) != -1) {
					content.add(buffer);

					totalNbrOfBytesRead += nbrOfBytesRead;
				}

				if (totalNbrOfBytesRead > 0) {
					result = new byte[totalNbrOfBytesRead];

					for (int pIndex = 0; pIndex < content.size(); pIndex++) {
						byte[] block = (byte[]) content.get(pIndex);

						if (block != null) {
							int blockLength = (pIndex == (content.size() - 1) ? (totalNbrOfBytesRead % blockSize)
									: blockSize);

							for (int bIndex = 0; bIndex < blockLength; bIndex++) {
								result[pIndex * blockSize + bIndex] = block[bIndex];
							}
						}
					}
				}

				return result;
			}
			finally {
				if (biStream != null) {
					biStream.close();
				}
				// [[
				if (logDebugEnabled) {
					LOG.exiting(result);
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
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "path", path } };
				LOG.entering(TestHelper.class, "getListOfFiles", params);
			}
			// ]]
			try {
				if (StringUtils.notEmpty(path)) {
					File directory = new File(path);

					if (directory.isDirectory()) {
						List<File> tempList = new ArrayList<File>();

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
		 * @param clazz
		 * @param testFile
		 * @return
		 * @throws Exception
		 */
		protected static Object parseTestFile(Class clazz, File testFile)
				throws Exception {
			// [[
			Object result = null;
			BufferedReader fileReader = null;
			XMLStreamReader xmlReader = null;
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "clazz", clazz }, { "testFile", testFile } };
				LOG.entering(TestHelper.class, "parseTestFile", params);
			}
			// ]]
			try {
				XMLInputFactory factory = XMLInputFactory.newInstance();

				fileReader = new BufferedReader(new FileReader(testFile));
				xmlReader = factory.createXMLStreamReader(fileReader);

				return (result = parseWebserviceRequestObject(clazz, xmlReader));
			} 
			finally {
				// [[
				if (logDebugEnabled) {
					LOG.exiting(result);
				}
				if (fileReader != null) {
					fileReader.close();
				}
				if (xmlReader != null) {
					xmlReader.close();
				}
				// ]]
			}
		}

		/**
		 * @param serviceName
		 * @param operationName
		 * @param response
		 */
		protected static void processFerrariResponse(String serviceName,
				String operationName, ResponseBean response) {
			// [[
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "serviceName", serviceName },
						{ "operationName", operationName },
						{ "response", response } };
				LOG.entering(TestHelper.class, "processFerrariResponse",
								params);
			}
			// ]]
			try {
				if ((response == null) || (!response.isSuccessful())) {
					LOG.error("\t Error during the call for operation: "
							+ operationName + " of service: " + serviceName);
				}

				if (response != null) {
					LOG.info("\t  Successful: " + response.isSuccessful()
							+ "; JobId: " + response.getJobId());
				}
			}
			finally {
				// [[
				if (logDebugEnabled) {
					LOG.exiting();
				}
				// ]]
			}
		}
	}

	private static enum TestClientConfig {

		LOGONTOFSMS(
				SERVICE_LOGON_TO_FSMS, SERVICE_LOGON_TO_FSMS_BASE_URL,
				SERVICE_LOGON_TO_FSMS_OPERATIONS,
				SERVICE_LOGON_TO_FSMS_HOST_URL_PROP,
				SERVICE_LOGON_TO_FSMS_AUTH_PWD_PROP,
				SERVICE_LOGON_TO_FSMS_AUTH_USER_PROP),
		LOGONTOFFAX(
				SERVICE_LOGON_TO_FFAX, SERVICE_LOGON_TO_FFAX_BASE_URL,
				SERVICE_LOGON_TO_FFAX_OPERATIONS,
				SERVICE_LOGON_TO_FFAX_HOST_URL_PROP,
				SERVICE_LOGON_TO_FFAX_AUTH_PWD_PROP,
				SERVICE_LOGON_TO_FFAX_AUTH_USER_PROP);

		private static JcanLogger LOG = JcanLoggerFactory
				.getLogger(TestClientConfig.class);

		private String serviceName = null;

		private String serviceUrl = null;

		private String authUser = null;

		private String authPwd = null;

		private Map availableOperations = null;

		private Map<String, String> currentOperations = null;

		private String sysPropServiceUrl = null;

		private String sysPropAuthPwd = null;

		private String sysPropAuthUser = null;

		/**
		 * Default constructor.
		 * 
		 * @param serviceName
		 * @param serviceUrl
		 * @param availableOperations
		 * @param sysPropServiceUrl
		 * @param sysPropAuthPwd
		 * @param sysPropAuthUser
		 */
		private TestClientConfig(String serviceName, String serviceUrl,
				Map availableOperations, String sysPropServiceUrl,
				String sysPropAuthPwd, String sysPropAuthUser) {
			init(serviceName, serviceUrl, availableOperations,
					sysPropServiceUrl, sysPropAuthPwd, sysPropAuthUser);
		}

		/**
		 * @param serviceName
		 * @param serviceUrl
		 * @param availableOperations
		 * @param sysPropServiceUrl
		 * @param sysPropAuthPwd
		 * @param sysPropAuthUser
		 */
		private void init(String serviceName, String serviceUrl,
				Map availableOperations, String sysPropServiceUrl,
				String sysPropAuthPwd, String sysPropAuthUser) {
			LOG = JcanLoggerFactory.getLogger(TestClientConfig.class);
			// [[
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				Object[][] params = { { "serviceName", serviceName },
						{ "serviceUrl", serviceUrl },
						{ "availableOperations", availableOperations },
						{ "sysPropServiceUrl", sysPropServiceUrl },
						{ "sysPropAuthPwd", sysPropAuthPwd },
						{ "sysPropAuthUser", sysPropAuthUser } };
				LOG.entering(this, "init", params);
			}
			// ]]
			try {
				this.serviceName = serviceName;
				this.serviceUrl = serviceUrl;
				this.availableOperations = availableOperations;
				this.currentOperations = new HashMap<String, String>();
				this.sysPropServiceUrl = sysPropServiceUrl;
				this.sysPropAuthPwd = sysPropAuthPwd;
				this.sysPropAuthUser = sysPropAuthUser;
			} 
			finally {
				// [[
				if (logDebugEnabled) {
					LOG.exiting();
				}
				// ]]
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		public String toString() {
			ToStringBuilder builder = new ToStringBuilder(this);

			builder.append("serviceName", this.serviceName);
			builder.append("serviceUrl", this.serviceUrl);
			builder.append("authUser", this.authUser);
			builder.append("authPwd", this.authPwd);
			builder.append("availableOperations", this.availableOperations);
			builder.append("currentOperations", this.currentOperations);
			builder.append("sysPropServiceUrl", this.sysPropServiceUrl);
			builder.append("sysPropAuthPwd", this.sysPropAuthPwd);
			builder.append("sysPropAuthUser", this.sysPropAuthUser);

			return builder.toString();
		}

		/**
		 * @return
		 */
		protected String getAuthPwd() {
			return this.authPwd;
		}

		/**
		 * @param authPwd
		 */
		protected void setAuthPwd(String authPwd) {
			this.authPwd = authPwd;
		}

		/**
		 * @return
		 */
		protected String getAuthUser() {
			return this.authUser;
		}

		/**
		 * @param authUser
		 */
		protected void setAuthUser(String authUser) {
			this.authUser = authUser;
		}

		/**
		 * @return
		 */
		protected Map getAvailableOperations() {
			return this.availableOperations;
		}

		/**
		 * @return
		 */
		protected Map<String, String> getCurrentOperations() {
			return this.currentOperations;
		}

		/**
		 * @return
		 */
		protected String getServiceName() {
			return this.serviceName;
		}

		/**
		 * @return
		 */
		protected String getServiceUrl() {
			return this.serviceUrl;
		}

		/**
		 * @param serviceUrl
		 */
		protected void setServiceUrl(String serviceUrl) {
			this.serviceUrl = serviceUrl;
		}

		/**
		 * @return
		 */
		protected Set getAvailableOperationNames() {
			// [[
			Set result = null;
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "getAvailableOperationNames");
			}
			// ]]
			try {
				if (this.availableOperations != null) {
					result = this.availableOperations.keySet();
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
		 * 
		 */
		protected void setAvailableOpsToCurrentOps() {
			this.currentOperations = this.availableOperations;
		}

		/**
		 * @return
		 */
		protected String getSysPropAuthPwd() {
			return this.sysPropAuthPwd;
		}

		/**
		 * @return
		 */
		protected String getSysPropAuthUser() {
			return this.sysPropAuthUser;
		}

		/**
		 * @return
		 */
		protected String getSysPropServiceUrl() {
			return this.sysPropServiceUrl;
		}
	}

}
