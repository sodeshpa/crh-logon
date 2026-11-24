/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.arquillian;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jboss.arquillian.protocol.servlet.arq514hack.descriptors.api.web.WebAppDescriptor;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.Alternatives;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.Audited;
import com.crh.logon.business.services.impl.ServiceSupport;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;

/**
 * @author AdNovum Informatik AG
 */
public class ArquillianHelper {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ArquillianHelper.class);

	private static final String BUSINESS_PACKAGE = "com.crh.logon.business";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

	static final String BEAN_POST_FIX = "Bean";
	static final String LOCAL_INTERFACE_POST_FIX = "LocalInterface";
	static final String SERVICE_BEAN_POST_FIX = "Service"+BEAN_POST_FIX;
	static final String TEST_CLASS_NAME_TAG = "Test";
	static final String TEST_SUITE_CLASS_NAME_TAG = TEST_CLASS_NAME_TAG + "Suite";

	private static final String DIRECTORY_NAME_LOGON_BUSINESS = "logon-business";
	private static final String DIRECTORY_NAME_LOGON_APPLICATION = "logon-application";
	
	private static final String FILE_NAME_BUSINESS_TEST_JAR = "logon-business-test.jar";
	private static final String FILE_NAME_EJB_JAR_XML = "ejb-jar.xml";
	private static final String FILE_NAME_BEANS_XML = "beans.xml";
	private static final String FILE_NAME_DEPLOYMENT_DESCRIPTOR = "test-jboss-deployment-structure.xml";
	private static final String FILE_NAME_PERSISTENCE_XML = "persistence.xml";

	private static final String DIRECTORY_PATH_MASTER_BUILD_DIR;
	private static final String DIRECTORY_PATH_LOGON_BUSINESS;
	private static final String DIRECTORY_PATH_LOGON_APPLICATION;
	private static final String DIRECTORY_PATH_BUSINESS_BUILD_DEP_LIBS;
	private static final String DIRECTORY_PATH_APPLICATION_LIBS;
	private static final String DIRECTORY_PATH_APPLICATION_AXIS2_LIBS;
	private static final String FILE_PATH_EJB_JAR_XML;
	private static final String FILE_PATH_DEPLOYMENT_DESCRIPTOR;
	private static final String FILE_PATH_PERSISTENCE_XML;

	static {
		String masterBuildDirProperty = System.getProperty("masterbuild.dir");
		if (masterBuildDirProperty == null) {
			masterBuildDirProperty = "..";
		}
		DIRECTORY_PATH_MASTER_BUILD_DIR = masterBuildDirProperty;
		DIRECTORY_PATH_LOGON_BUSINESS = DIRECTORY_PATH_MASTER_BUILD_DIR + "/" + DIRECTORY_NAME_LOGON_BUSINESS;
		DIRECTORY_PATH_LOGON_APPLICATION = DIRECTORY_PATH_MASTER_BUILD_DIR + "/" + DIRECTORY_NAME_LOGON_APPLICATION;
		DIRECTORY_PATH_BUSINESS_BUILD_DEP_LIBS = DIRECTORY_PATH_LOGON_BUSINESS + "/" + "build/dist/dep/lib";
		DIRECTORY_PATH_APPLICATION_LIBS = DIRECTORY_PATH_LOGON_APPLICATION + "/" + "build/stage_int/lib";
		DIRECTORY_PATH_APPLICATION_AXIS2_LIBS = DIRECTORY_PATH_LOGON_APPLICATION + "/" + "build/stage_int/lib-axis2";
		FILE_PATH_EJB_JAR_XML = DIRECTORY_PATH_LOGON_BUSINESS + "/" + "build/generated/gdi/ejb3/META-INF" + "/" + FILE_NAME_EJB_JAR_XML;
		FILE_PATH_DEPLOYMENT_DESCRIPTOR = DIRECTORY_PATH_LOGON_BUSINESS + "/" + "src/test/etc" + "/" + FILE_NAME_DEPLOYMENT_DESCRIPTOR;
		FILE_PATH_PERSISTENCE_XML = DIRECTORY_PATH_LOGON_BUSINESS + "/" + "src/main/resources/META-INF" + "/" + FILE_NAME_PERSISTENCE_XML;
	}

	static final Filter<ArchivePath> TEST_FILTER = new Filter<ArchivePath>() {
		@Override
		public boolean include(ArchivePath path) {
			String pathContext = path.get(); // i.e. "com/crh/logon/business/services/interfaces/TestService.class"
			int lastIndexOfSlash = pathContext.lastIndexOf(ArchivePath.SEPARATOR);
			String plainPathContext = pathContext.substring(lastIndexOfSlash + 1); // i.e "TestService.class"
			return !plainPathContext.matches(String.format(".+%s|.+%s.*", TEST_CLASS_NAME_TAG, TEST_SUITE_CLASS_NAME_TAG));
		}
	};

	/**
	 * Arquillian tests should be based on the .war created by this method.
	 * 3rd party Libs will be included.
	 * Bare minimum logon packages will be included under WEB-INF/lib
	 * Some specficic required logon classes will be included under WEB-INF/classes.
	 * 
	 * @param testClass - the JUnit test class 
	 * @param additionalClasses - test related additional classes i.e. the implementation and interface to be tested
	 * @return the created webarchive
	 */
	public static WebArchive createWebArchive(ArquillianTestData testData) {

		Date currentDate = new Date();

		String warFileName = String.format("arquillian_%s.war", sdf.format(currentDate));

		WebArchive webArchive = ShrinkWrap.create(WebArchive.class, warFileName);

		addPersistenceDescriptorToWebArchive(webArchive);
		addBeansDescriptor(webArchive, testData);
		addEjbDescriptorToWebArchive(webArchive);
		addTestDataDependentClassesToWar(webArchive, testData);
		addLogonRequiredClassesToWar(webArchive);
		addLogonBusinessPackageToWebArchive(webArchive);
		addDependenciesToWebArchive(webArchive, DIRECTORY_PATH_BUSINESS_BUILD_DEP_LIBS);
		addDependenciesToWebArchive(webArchive, DIRECTORY_PATH_APPLICATION_LIBS);
		addDependenciesToWebArchive(webArchive, DIRECTORY_PATH_APPLICATION_AXIS2_LIBS);
		addDeploymentDescriptorToWebArchive(webArchive);

		WebAppDescriptor webXml = Descriptors.create(WebAppDescriptor.class);
		webXml.version("3.0");
		webArchive.setWebXML(new StringAsset(webXml.exportAsString()));

		return webArchive;

	}

	private static void addTestDataDependentClassesToWar(WebArchive webArchive, ArquillianTestData testData) {

		Class testDataClass = testData.getTestDataClass();
		if (testDataClass != null) {
			webArchive.addClass(testDataClass);
		}

		Map<String, Class> additionalClassesMap = testData.getAdditionalClasses();
		Map<String, Class> referencedClassesMap = new HashMap<String, Class>();
		referencedClassesMap.putAll(additionalClassesMap);

		Collection<Class> additionalClasses = additionalClassesMap.values();
		Collection<Class> referencedClasses = referencedClassesMap.values();

		 webArchive.addClasses(additionalClasses.toArray(new Class[]{}));
		 webArchive.addClasses(referencedClasses.toArray(new Class[]{}));

	}

	/**
	 * Arquillian deployment needs it's own description of EJBs. 
	 * In Logon most EJB remote interfaces are not directly implemented by the Service Bean hence the EJB lookup would fail.
	 * In the core deployment this file is used also.
	 * @param webArchive where to add the ejb-jar descriptor
	 */
	private static void addEjbDescriptorToWebArchive(WebArchive webArchive) {
		webArchive.addAsWebInfResource(new File(FILE_PATH_EJB_JAR_XML), FILE_NAME_EJB_JAR_XML);
	}

	/**
	 * Adds a predefined deployment descriptor to the webArchive in order to exclude some
	 * to avoid clashes 
	 * @param webArchive where to add the deployment descriptor
	 */
	private static void addDeploymentDescriptorToWebArchive(WebArchive webArchive) {
		webArchive.addAsWebInfResource(new File(FILE_PATH_DEPLOYMENT_DESCRIPTOR));
	}

	/**
	 * Adds a predefined persistence descriptor to the webArchive. This descriptor is a bit different then the one used for core deployment. 
	 * @param webArchive where to add the persistence descriptor
	 */
	private static void addPersistenceDescriptorToWebArchive(WebArchive webArchive) {
		webArchive.addAsResource(new File(FILE_PATH_PERSISTENCE_XML), "META-INF" + "/" + FILE_NAME_PERSISTENCE_XML);
	}

	/**
	 * Adds a beans.xml created dynamically to the war file.
	 * What is fixed is the UserInfoInterceptor.java that will be added as an 'interceptor'.
	 * Other than that from the additionalClasses we add all the classes which are like 'XYServiceBean's. 
	 * @param webArchive - war to add the beans.xml
	 * @param testData - contains the required additional classes
	 */
	private static void addBeansDescriptor(WebArchive webArchive, ArquillianTestData testData) {

		Map<String, Class> additionalClassesMap = testData.getAdditionalClasses();
		Collection<Class> additionalClasses = additionalClassesMap.values();

		BeansDescriptor beans = Descriptors.create(BeansDescriptor.class);
		beans
			.createInterceptors()
			.clazz(UserInfoInterceptor.class.getCanonicalName());

		Alternatives<BeansDescriptor> alternatives = beans.createAlternatives();

		Class ejbClass = testData.getEjbClass();
		String className = ejbClass.getCanonicalName();
		alternatives.clazz(className);

		webArchive.addAsManifestResource(new StringAsset(beans.exportAsString()), FILE_NAME_BEANS_XML);
	}

	/**
	 * Logon business project contains some dependencies that we need.
	 * This methods looks them up as files and adds them to the webarchive lib folder.
	 * The source folder might also contain some logon artifacts which we should ignore. 
	 */
	private static void addDependenciesToWebArchive(WebArchive webArchive, String sourceFolder) {

		// Please note this is not working and that is why we look the files up one-by-one
		//   webArchive.addAsLibraries(new File(BUILD_DEP_LIBS));

		File[] listFiles = new File(sourceFolder).listFiles();
		for (File file : listFiles) {
			String libFileName = file.getName();
			File libFile = new File(sourceFolder + "/" + libFileName);
			webArchive.addAsLibrary(libFile, new BasicPath(libFileName));
		}

	}

	/**
	 * Without these classes we can't get away
	 * @param webArchive - the webArchive into which we add the classes
	 */
	private static void addLogonRequiredClassesToWar(WebArchive webArchive) {

		webArchive
			.addClass(Audited.class)
			.addClass(UserInfoInterceptor.class)
			.addClass(ServiceSupport.class);

	}

	/**
	 * Collects class packages of Logon into one jar which would be otherwise cumbersome to selectively
	 * include - i.e. adding only the required classes is too much work for the moment.
	 * Filtering is still in place for test cases though.
	 * @param webArchive - the webArchive into which we add the created JavaArchive lib 
	 */
	private static void addLogonBusinessPackageToWebArchive(WebArchive webArchive) {

		JavaArchive logonBasePackageJar = ShrinkWrap.create(JavaArchive.class, FILE_NAME_BUSINESS_TEST_JAR)
			.addPackages(true, TEST_FILTER, BUSINESS_PACKAGE);
		webArchive.addAsLibraries(logonBasePackageJar);

	}

}