/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
 
package com.crh.logon.arquillian;
 
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
 
/**
 * @author AdNovum Informatik AG
 */
public class ArquillianTestData {

	private Class testClass;
	private Class ejbClass;

	private Map<String, Class> additionalClasses;

	private ArquillianTestData() {
		additionalClasses = new HashMap<String, Class>();
	}

	public static ArquillianTestData create() {
		return new ArquillianTestData();
	}

	public ArquillianTestData addTestClass(Class testClass) {
		this.testClass = testClass;
		return this;
	}

	public ArquillianTestData addEjbClass(Class ejbClass) {
		this.ejbClass = ejbClass;
		return this;
	}

	public ArquillianTestData addAdditionalClass(Class additionalClass) {
		String additionalClassName = additionalClass.getName();
		this.additionalClasses.put(additionalClassName, additionalClass);
		return this;
	}

	public Class getTestDataClass() {
		return testClass;
	}

	public Class getEjbClass() {
		return ejbClass;
	}

	/**
	 * @return unmodifiableMap view of the additionalClasses collected
	 */
	public Map<String, Class> getAdditionalClasses() {
		return Collections.unmodifiableMap(additionalClasses);
	}

}