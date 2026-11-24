/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.crh.logon.arquillian.ArquillianHelper;
import com.crh.logon.arquillian.ArquillianTestData;
import com.crh.logon.business.services.interfaces.SalesDocumentTestDataService;
import com.crh.logon.webservices.clients.beans.deliverydocument.SalesDocumentDTO;

/**
 * @author AdNovum Informatik AG
 */
@RunWith(Arquillian.class)
public class SapOutgoingDataServiceIntegrationTest extends BaseServiceIntegrationTest<CustorderServiceBean> {
	@Inject
	SalesDocumentTestDataService salesDocumentTestDataService;
	
	@Deployment
	@TargetsContainer("jbossas_remote")
	public static Archive createDeployment() {
		SapOutgoingDataServiceIntegrationTest instance = new SapOutgoingDataServiceIntegrationTest();
		ArquillianTestData testData = instance.prepareTestData();
		WebArchive webArchive = ArquillianHelper.createWebArchive(testData);
		return webArchive;
	}

	@Test
	public void testBagOrderSapDocumentOutput() {
		List<SalesDocumentDTO> salesDocuments = 
				salesDocumentTestDataService.getBagOrderSalesDocumentSampleData();
		
		for (SalesDocumentDTO salesDocument : salesDocuments) {
			assertThat("Bag order is missing sap field (loadingDate)",
					salesDocument.getLoadingDate(), notNullValue());
		}
	}
}