/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.workflow;

import com.crh.logon.admin.web.pages.LogonSeleniumTestCaseBase;
import com.crh.logon.admin.web.util.PropertyManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.pages.PropertyKey;

/**
 * Loads properties from the specified @link {@link #inPropertiesPath} and makes sure that
 * when loading the test it is already in the @link {@link #outPropertiesPath}.
 * 
 * At the end of the test it saves everything added in the meantime using {@link #setOutProperty(PropertyKey, String)}
 * to the specified output properties.
 * 
 * @author AdNovum Informatik AG
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
@Ignore
public abstract class PropertyTransferTest extends LogonSeleniumTestCaseBase {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PropertyTransferTest.class);
	
	protected PropertyManager propertyManager;
	
	/**
	 * IN properties stored int eh path- these will be loaded before the test.
	 */
	private String inPropertiesPath = null;

	/**
	 * OUT properties to this path the properties loaded in the beginning, plus added while the test will
	 * be saved.
	 */
	private String outPropertiesPath = null;
	
	public PropertyTransferTest(String className, String inPropertiesPath, String outPropertiesPath) {	 
		this.propertyManager = new PropertyManager();
		this.inPropertiesPath = inPropertiesPath;
		this.outPropertiesPath = outPropertiesPath;
	}
	
	@Before
	public void loadProperties() throws Exception {
		if ( inPropertiesPath != null ) {			
			propertyManager.loadProperties(inPropertiesPath);
		}
		if ( outPropertiesPath != null ) {			
			propertyManager.copyProperties(inPropertiesPath, outPropertiesPath);
		}
	}
	
	@After
	public void storeNewObjects() throws Exception {
		// do not name this method setup, otherwise it will interfere with the junitrunner
		if ( outPropertiesPath == null ) {
			LOG.info("No output properties file path specified");
		}
		else {				
			propertyManager.storeTestRelatedProperties(outPropertiesPath, false);
		}
	}	
	
	public final String getOutProperty(PropertyKey key) {
		return propertyManager.getTestProperty(outPropertiesPath, key.getKeyString());
	}
	
	public final String getInProperty(PropertyKey key) {
		return propertyManager.getTestProperty(inPropertiesPath, key.getKeyString());
	}
	
	
	public final String setOutProperty(PropertyKey key, String value) {
		return propertyManager.addTestProperty(outPropertiesPath, key, value);
	}

	public String getInPropertiesPath() {
		return inPropertiesPath;
	}

	public String getOutPropertiesPath() {
		return outPropertiesPath;
	}

	public final String getOutProperty(String key) {
		return propertyManager.getTestProperty(outPropertiesPath, key);
	}
	
	public final String getInProperty(String key) {
		return propertyManager.getTestProperty(inPropertiesPath, key);
	}
	
	public final String getProperty(String path, String key){
		return propertyManager.getTestProperty(path, key);
	}
}
