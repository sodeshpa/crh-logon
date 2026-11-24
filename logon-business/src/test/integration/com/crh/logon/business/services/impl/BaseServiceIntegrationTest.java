/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.ejb.EJB;

import org.junit.Before;
import org.junit.Ignore;

import com.crh.logon.arquillian.ArquillianTestData;
import com.crh.logon.business.services.interfaces.ConfigService;
import com.crh.logon.business.services.utils.ConfigInitializer;
import com.crh.logon.utils.LogonProperties;


/**
 * Arquillian service tests should be subclasses of this class.
 * The generic type parameter should be the class of the EJB session bean under test.
 * @author AdNovum Informatik AG
 */
@Ignore
public abstract class BaseServiceIntegrationTest<T extends ServiceSupport> {
	@EJB
	ConfigService configService;
	
	/**
	 * Prepares the {@code ArquillianTestData} for Arquillian tests. 
	 * @return {@code ArquillianTestData} which has the testClass and the class of the EJB under test already added to the classpath
	 */
	ArquillianTestData prepareTestData() {

		Class<?> testClass = getClass();
		ParameterizedType parameterizedType = (ParameterizedType) testClass.getGenericSuperclass();
		Type typeArguments[] = parameterizedType.getActualTypeArguments();
		Class ejbClass = (Class<T>) typeArguments[0];

		ArquillianTestData testData = ArquillianTestData.create()
			.addTestClass(testClass)
			.addEjbClass(ejbClass)
			.addAdditionalClass(BaseServiceIntegrationTest.class)
			.addAdditionalClass(ArquillianTestData.class);

		return testData;

	}

	//TODO: replace this solution with the properly initialized real config
	@Before
	public void initTestEnvironment() {
		if (!ConfigInitializer.inst.isInitialized()) {
			LogonProperties logonProperties = new LogonProperties();
			logonProperties.setDevmode(true);
			
			configService.initializeConfigBean(logonProperties);
		}
	}
}
