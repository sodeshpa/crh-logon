/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.crh.logon.arquillian.ArquillianHelper;
import com.crh.logon.arquillian.ArquillianTestData;
import com.crh.logon.business.services.interfaces.CustorderService;
import com.crh.logon.persistence.entities.common.CustorderEntity;

/**
 * @author AdNovum Informatik AG
 */
@RunWith(Arquillian.class)
public class CustorderServiceIntegrationTest extends BaseServiceIntegrationTest<CustorderServiceBean> {

	// not necessarily exist in all databases; using for presentational purposes only and can be removed once this test is testing real functionality
	private static final long EXAMPLE_CUSTOMER_ORDER_ID = 1000L; 

	@EJB
	CustorderService custorderService;

	@Deployment
	@TargetsContainer("jbossas_remote")
	public static Archive createDeployment() {
		CustorderServiceIntegrationTest instance = new CustorderServiceIntegrationTest();
		ArquillianTestData testData = instance.prepareTestData();
		WebArchive webArchive = ArquillianHelper.createWebArchive(testData);
		return webArchive;
	}

	@Test
	public void testCustomerOrderExsists() {

		CustorderEntity customerOrder = custorderService.findCustorderById(EXAMPLE_CUSTOMER_ORDER_ID);
		assertThat(customerOrder, notNullValue());

	}

}