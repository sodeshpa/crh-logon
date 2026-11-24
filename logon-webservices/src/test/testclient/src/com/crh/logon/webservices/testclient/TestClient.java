/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.testclient;

import java.io.File;
import java.io.FileInputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * This is the main class, which handles arguments,
 * and calculates what should be done exactly.
 * 
 * @author AdNovum Informatik AG
 */
@SuppressWarnings("static-access")
public class TestClient {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TestClient.class);

	private static final String DEFAULT_BASE_URL = "http://localhost:15080";
	private static final String DEFAULT_BASE_URL_CONTEXT = "/logon/webservices/services/";
	private static final String DEFAULT_TESTDATA_FOLDER_PREFIX = "testdata/";
	private static final String DEFAULT_MESSAGE_NAMESPACE = "http://logon.intranet-che/logon";
	private static final String DEFAULT_BASE_DIRECTORY = "./";
	
	private static final String TESTDATA_FOLDER_USECASE_INFIX = "usecases/";
	
	private static final String FILEEXTENSION_OF_REQUESTS = ".request";
	private static final String FILEEXTENSION_OF_REQUESTS_ONLY_BODY = ".requestbody";

	private static final String FILENAME_OF_PROPERTIES = "etc/TestClient.properties";
	private static final String FILENAME_OF_ORDEROFTESTS = "order.requests";

	private static final String PROPERTY_MESSAGE_NAMESPACE = "message.namespace";
	private static final String PROPERTY_BASE_URL = "webservices.url.base";
	private static final String PROPERTY_BASE_URL_CONTEXT = "webservices.url.context";
	private static final String PROPERTY_TESTDATA_FOLDER_POSTFIX = ".testdata.folder";
	private static final String PROPERTY_ORDEROFTESTS = "order.of.processing.testdata";

	private static final Options CL_OPTIONS;
	private static final String ARG_HELP = "help";
	private static final String ARG_ONLYBODY = "onlyBody";
	private static final String ARG_EXPLICITORDER = "explicitOrder";
	private static final String ARG_BASE_DIRECTORY = "baseDirectory";
	private static final String ARG_SERVICESTOTEST = "servicesToTest";
	private static final String ARG_REQUESTSFOLDER = "requestFolder";
	private static final String ARG_USECASE = "useCase";
	private static final String ARG_ALL_USE_CASES = "allUseCases";
			
	private static final Map<String, ServiceCaller> SUPPORTED_SERVICES;

		
	static {
		// our command line options
		CL_OPTIONS = new Options();

		CL_OPTIONS.addOption(new Option(ARG_HELP, "Display this info."));
		
		CL_OPTIONS.addOption(new Option(ARG_ONLYBODY, "The request files only contains the soap body part."));
		CL_OPTIONS.addOption(new Option(ARG_ALL_USE_CASES, "Perform all available usescases."));
		CL_OPTIONS.addOption(new Option(ARG_EXPLICITORDER, "The order of requests is explicit, only the requests specified in the order file will be sent."));
		
		CL_OPTIONS.addOption(OptionBuilder
						.withArgName("filePath")
						.hasArg()
						.withDescription("Setup custom base directory (instead of the current working directory)")
						.create(ARG_BASE_DIRECTORY));

		CL_OPTIONS.addOption(OptionBuilder
						.withArgName("folderName")
						.hasArg()
						.withDescription("Process SOAP-requests (files with extension 'request' or 'requestbody') from directory")
						.create(ARG_REQUESTSFOLDER));
		
		CL_OPTIONS.addOption(OptionBuilder
						.withArgName("webserviceName")
						.hasArgs()
						.withDescription("The names of the webservices to test.")
						.create(ARG_SERVICESTOTEST));
		
		CL_OPTIONS.addOption(OptionBuilder
						.withArgName("useCaseName")
						.hasArg()
						.withDescription("Process SOAP-requests (files with extension 'request' only) belonging to a specific use case")
						.create(ARG_USECASE));
		
		// a SortedMap implementation (so services will remain sorted by their importName) 
		SUPPORTED_SERVICES = new TreeMap<String, ServiceCaller>();

		final String[] serviceNames = new String[]{
				"BusinessPartnerNotification",
				"MaterialNotification",
				"MaterialNormNotification",
				"SalesDocumentNotification",
				"VehicleNotification",
				"VendorNotification"
		};
		
		// initialize all services (upon construction they will look up 
		// the generated axis2 classes using reflection such as the documents/stubs)
		for (String serviceName : serviceNames) {
			ServiceCaller serviceCaller;
			try {
				serviceCaller = new ServiceCaller(serviceName);
				SUPPORTED_SERVICES.put(serviceName, serviceCaller);
			}
			catch (Exception e) {
				throw new ExceptionInInitializerError(e);
			}
		}
	}
	
	/**
	 * Only parses the arguments, using apache-cli, and no help requested
	 * constructs TestClient with the options to do the work
	 * 
	 * @param args commandline arguments
	 */
	public static void main(String args[]) {
		// [[
		Object[][] params = {{"args", args}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(TestClient.class, "main", params);
		}
		// ]]
		boolean printHelp = false;
		try {
			
			final CommandLine line = new GnuParser().parse(CL_OPTIONS, args);
			
			if (line.hasOption(ARG_HELP)) {
				printHelp = true;
			}
			else {
				final TestClient client = new TestClient(line);
				if (line.hasOption(ARG_ALL_USE_CASES)) {
					client.performAllUseCases();
				}
				else {
					client.performTests();
				}
			}
		}
		catch (ParseException e) {
			// such as a non-existing switch passed as an argument
			LOG.error("Parsing failed.  Reason: " + e.getMessage());
			printHelp = true;
		}
		finally {
			if (printHelp) {
				printHelp();
			}
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	private static void printHelp() {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("adnant run-client", CL_OPTIONS);

		System.out.println("");
		System.out.println("Available webservices:");
		System.out.println(SUPPORTED_SERVICES.keySet());

		System.out.println("");
		System.out.println("Available usecases:");
		System.out.println(getAvailableUseCases(DEFAULT_BASE_DIRECTORY));
	}
		
	private Properties testProperties;
	private String serviceBaseUrl;

	private String baseDirectory;
	private String orderOfTests;
	private String messageNamespace;
	
	// calculated subset of the supported services
	private final Collection<ServiceCaller> servicesToTest;
	
	// if stated as an argument
	private final String dumpedRequestsFolder;

	private String useCaseParameter;
	private final boolean expectOnlyBody;
	private final boolean explicitOrder;
	
	private TestClient(CommandLine line) throws ParseException {
		// [[
		Object[][] params = {{"line", line}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "TestClient", params);
		}
		// ]]
		try {
			this.expectOnlyBody = line.hasOption(ARG_ONLYBODY);
			this.explicitOrder = line.hasOption(ARG_EXPLICITORDER);
			this.dumpedRequestsFolder = line.getOptionValue(ARG_REQUESTSFOLDER);
			this.useCaseParameter = line.getOptionValue(ARG_USECASE);
			
			if ((dumpedRequestsFolder != null) && expectOnlyBody) {
					throw new ParseException(
							String.format(
									"If the files are only containing the body, " +
									"and not a full soap request, you can not " +
									"specify the same folder for all services!"));				
			}
			
			this.baseDirectory = DEFAULT_BASE_DIRECTORY;
			if (line.hasOption(ARG_BASE_DIRECTORY)) {
				baseDirectory = line.getOptionValue(ARG_BASE_DIRECTORY);
			}
			
			loadPropertiesFromFile();

			this.servicesToTest = new TreeSet<ServiceCaller>();
			if (line.hasOption(ARG_SERVICESTOTEST)) {
				for (String serviceToTest : line.getOptionValues(ARG_SERVICESTOTEST)) {
					
					if (SUPPORTED_SERVICES.containsKey(serviceToTest)) {
						this.servicesToTest.add(SUPPORTED_SERVICES.get(serviceToTest));						
					}
					else {
						throw new ParseException(
								String.format(
										"Unknown webservice: %s", 
										serviceToTest));
					}
				}
			}
			
			if (this.servicesToTest.isEmpty()) {
				if (LOG.isInfoEnabled()) {
					LOG.info("No exact services was specified for testing, adding all services.");
				}
				this.servicesToTest.addAll(SUPPORTED_SERVICES.values());
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

	private void loadPropertiesFromFile() {
		this.testProperties = new Properties();
		String propertiesFileName = baseDirectory + FILENAME_OF_PROPERTIES;
		try {	
			this.testProperties.load(new FileInputStream(propertiesFileName));
			
			if (LOG.isInfoEnabled()) {
				LOG.info(String.format(
						"Properties successfully loaded from '%s'.",
						propertiesFileName));
			}
		}
		catch (Exception e) {
			LOG.error(String.format(
					"Exception while loading property file '%s' (%s).", 
					propertiesFileName, 
					e.getMessage()));
		}

		this.serviceBaseUrl = this.testProperties.getProperty(
										PROPERTY_BASE_URL, 
										DEFAULT_BASE_URL)
							+ this.testProperties.getProperty(
										PROPERTY_BASE_URL_CONTEXT, 
										DEFAULT_BASE_URL_CONTEXT);
		
		this.orderOfTests = this.testProperties.getProperty(
										PROPERTY_ORDEROFTESTS,
										FILENAME_OF_ORDEROFTESTS);

		this.messageNamespace = this.testProperties.getProperty(
										PROPERTY_MESSAGE_NAMESPACE,
										DEFAULT_MESSAGE_NAMESPACE);
	}

	/**
	 * this functions begins work, after everything is set in the constructor
	 */
	private final void performTests() {
		boolean infoEnabled = LOG.isInfoEnabled();
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "performTests");
		}
		// ]]
		try {

			if (infoEnabled) {
				LOG.info("Testing against service url: " + serviceBaseUrl);				
			}
			
			if (StringUtils.isNotEmpty(this.dumpedRequestsFolder)) {
				if (infoEnabled) {
					LOG.info(String.format(
							"Processing dumped requests from folder '%1$s'",
							this.dumpedRequestsFolder));
				}

				performDumpedRequests();
				
			}
			else if (StringUtils.isNotEmpty(this.useCaseParameter)) {
				if (infoEnabled) {
					LOG.info(String.format(
							"Processing requests for use case '%s'",
							this.useCaseParameter));
				}
				
				performUseCase(this.useCaseParameter);
			}
			else {
				if (infoEnabled) {
					LOG.info(String.format(
							"Processing service testing for '%1$s'",
							this.servicesToTest));
				}
				
				performServicesTest();
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
	 * if a dumpedRequestFolder is stated, then read all request files
	 * (*.request, which should be a full captured soap envelopes), and
	 * figure pair up request files with services by parsing them.
	 * After all request files are added to services do all of them.
	 */
	private void performDumpedRequests() {
		final boolean debugEnabled = LOG.isDebugEnabled();

		if (debugEnabled) {
			LOG.entering(this, "performDumpedRequests");
		}
		try {

			final Collection<File> allTestFiles = getTestFiles(this.dumpedRequestsFolder, null, false);			
			final Map<ServiceCaller, Collection<File>> testGroups = 
						RequestFetcher.mapRequestFilesToServices(SUPPORTED_SERVICES.values(), allTestFiles);

			for (Map.Entry<ServiceCaller, Collection<File>> testGroup : testGroups.entrySet()) {
	
				testGroup.getKey().callTests(
									this.messageNamespace,
									this.serviceBaseUrl, 
									testGroup.getValue(),
									this.expectOnlyBody);
			}

		}
		finally {
			if (debugEnabled) {
				LOG.exiting();
			}
		}
	}
	
	/**
	 * Read the test requests from the individual service directories.
	 */
	private void performServicesTest() {
		final boolean debugEnabled = LOG.isDebugEnabled();

		if (debugEnabled) {
			LOG.entering(this, "performServicesTest");
		}
		try {			

			String testFolderName;			
			Collection<File> testFiles;
			
			for (ServiceCaller serviceCaller : this.servicesToTest) {
				try {
					testFolderName = null;
					if (this.testProperties != null) {
						testFolderName = this.testProperties.getProperty(
								serviceCaller.getName() + PROPERTY_TESTDATA_FOLDER_POSTFIX);
					}
					if (StringUtils.isEmpty(testFolderName)) {
						testFolderName = DEFAULT_TESTDATA_FOLDER_PREFIX + serviceCaller.getName().toLowerCase();						
					}
					testFiles = getTestFiles(testFolderName, this.orderOfTests, this.explicitOrder);
					
					serviceCaller.callTests(
										this.messageNamespace,
										this.serviceBaseUrl, 
										testFiles,
										this.expectOnlyBody);
				} 
				catch (Exception e) {
					LOG.error(String.format(
							"Executing the test for service '%1$s' failed (%2$s), continuing.",
							serviceCaller.getName(), e.getMessage()));
				}
			}
			
		}
		finally {
			if (debugEnabled) {
				LOG.exiting();
			}
		}
	}

	public static File checkDirectoryPath(String path) {
		if (StringUtils.isEmpty(path)) {
			throw new IllegalArgumentException("Path is required for the directory!");
		}
		File directory = new File(path);
		if (!directory.exists()) {
			throw new InvalidParameterException("The file does not exist at path: '" + path
					+ "'");
		}
		if (!directory.isDirectory()) {
			throw new InvalidParameterException("The path does not point to directory: '" + path
					+ "'");
		}
		if (!directory.canRead()) {
			throw new InvalidParameterException("The folder cannot be read: '" + path
					+ "'");
		}
		return directory;
	}

	private static List<String> getAvailableUseCases(String baseDirectory) {
		String useCaseFolderPath = baseDirectory +
				DEFAULT_TESTDATA_FOLDER_PREFIX + TESTDATA_FOLDER_USECASE_INFIX;
		File useCaseFolder = checkDirectoryPath(useCaseFolderPath);
		String[] filesInDirectory = useCaseFolder.list();
		List<String> availableUseCases = Arrays.asList(filesInDirectory);
		Collections.sort(availableUseCases);
		return availableUseCases;
	}

	private void performAllUseCases() {
		boolean infoEnabled = LOG.isInfoEnabled();
		if (infoEnabled) {
			LOG.info("Send all '.request' files located within usecase/ to webservices.");
		}
		for (String useCase : getAvailableUseCases(baseDirectory)) {
			performUseCase(useCase);
		}
	}

	private void performUseCase(String useCase) {
		final boolean debugEnabled = LOG.isDebugEnabled();
		
		if (debugEnabled) {
			LOG.entering(this, "performUseCase");
		}
		try {
			
			String testFolderName;
			Collection<File> testFiles;
			
			String testFolderPrefix = baseDirectory + DEFAULT_TESTDATA_FOLDER_PREFIX
					+ TESTDATA_FOLDER_USECASE_INFIX + useCase;
			for (ServiceCaller serviceCaller : this.servicesToTest) {
				String serviceName = serviceCaller.getName();
				try {
					String serviceNameInLowerCase = serviceName.toLowerCase();
					testFolderName = testFolderPrefix + "/" + serviceNameInLowerCase;
					testFiles = getTestFiles(testFolderName, this.orderOfTests, this.explicitOrder);
					serviceCaller.callTests(
							this.messageNamespace,
							this.serviceBaseUrl, 
							testFiles,
							this.expectOnlyBody);
				}
				catch (IllegalArgumentException illegalArgumentException) {
					continue;
				}
				catch (Exception e) {
					LOG.error(String.format(
							"Executing the test for service '%1$s' failed (%2$s), continuing.",
							serviceName, e.getMessage()));
				}
			}
		}
		finally {
			if (debugEnabled) {
				LOG.exiting();
			}
		}
	}


	/**
	 * Read files from a specified directory and order them 
	 * 
	 * @param testFolderName folder to read from
	 * @param orderFileName  if this file exists, read the ordering stated within, and apply it on the test request files
	 * @param explicitOrder whether only explicitly ordered request files are included in the test
	 * @return
	 */
	private Collection<File> getTestFiles(String testFolderName, String orderFileName, boolean explicitOrder) {
		final boolean debugEnabled = LOG.isDebugEnabled();
		final Collection<File> result = null;
		if (debugEnabled) {
			Object[][] params = {
					{"testFolderName", testFolderName}, 
					{"orderFileName", orderFileName},
					{"explicitOrder", explicitOrder}};
			LOG.entering(this, "getTestFiles", params);
		}
		try {

			final RequestFetcher requestFetcher;
			File checkedDirectory = checkDirectoryPath(testFolderName);
			if (this.expectOnlyBody) {
				requestFetcher = new RequestFetcher(
						checkedDirectory,
						FILEEXTENSION_OF_REQUESTS_ONLY_BODY);
				
			}
			else {
				requestFetcher = new RequestFetcher(
						checkedDirectory,
						FILEEXTENSION_OF_REQUESTS);				
			}

			return requestFetcher.getAllRequestFiles(orderFileName, explicitOrder);
		}
		finally {
			if (debugEnabled) {
				LOG.exiting(result);
			}
		}
	}
}
