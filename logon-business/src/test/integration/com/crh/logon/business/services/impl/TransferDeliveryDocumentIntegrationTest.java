/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import com.crh.logon.business.services.interfaces.OrderTestDataService;
import com.crh.logon.business.services.interfaces.SapOutgoingDataService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.crh.logon.arquillian.ArquillianHelper;
import com.crh.logon.arquillian.ArquillianTestData;
import com.crh.logon.persistence.entities.common.CreditNoteEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.webservices.clients.beans.transferdeliverydocument.HeaderQualifierExtraDataDTO;
import com.crh.logon.webservices.clients.beans.transferdeliverydocument.TransferDeliveryDocumentDTO;

/**
 * @author AdNovum Informatik AG
 */
@RunWith(Arquillian.class)
public class TransferDeliveryDocumentIntegrationTest extends
		BaseServiceIntegrationTest<SapOutgoingDataServiceBean> {

	private OrderEntity testOrder;
	private CreditNoteEntity testCreditNote;

	@EJB
	SapOutgoingDataService sapOutgoingDataService;

	@Inject
	OrderTestDataService orderTestDataService;

	@Deployment
	@TargetsContainer("jbossas_remote")
	public static WebArchive createDeployment() {
		TransferDeliveryDocumentIntegrationTest instance = new TransferDeliveryDocumentIntegrationTest();
		ArquillianTestData testData = instance.prepareTestData();
		WebArchive webArchive = ArquillianHelper.createWebArchive(testData);
		return webArchive;
	}

	@Before
	public void setUp() {
		testOrder = orderTestDataService.createOrderForTransferDeliveryDocumentGenerationTesting();
		testCreditNote = new CreditNoteEntity();
		List<OrderItemEntity> orderItems = testOrder.getOrderItems();
		OrderItemEntity firstOrderItem = orderItems.get(0);
		testCreditNote.setOrderItem(firstOrderItem);
	}

	@Test
	public void testQualifierExtraData() {
		TransferDeliveryDocumentDTO transferDeliveryDocumentInbound = sapOutgoingDataService.getTransferDeliveryDocumentInbound(
				testOrder, testCreditNote);
		TransferDeliveryDocumentDTO transferDeliveryDocumentOutbound = sapOutgoingDataService.getTransferDeliveryDocumentOutbound(
				testOrder, testCreditNote);
		validateTransferDocument(transferDeliveryDocumentInbound);
		validateTransferDocument(transferDeliveryDocumentOutbound);
	}

	/**
	 * One document can contain multiple qualifier extra data, each should be validated independently
	 * @param transferDeliveryDocument
	 */
	private void validateTransferDocument(TransferDeliveryDocumentDTO transferDeliveryDocument) {
		TrailerEntity deliveredTrailer = testOrder.getDeliveredTrailer();
		String externalDeliveryNumber = transferDeliveryDocument.getExternalDeliveryNumber();
		String trailerPlateNo = (deliveredTrailer == null) ? null
				: deliveredTrailer.getTrailerPlateNo();
		List<HeaderQualifierExtraDataDTO> headerQualifierExtraDatas = transferDeliveryDocument.getHeaderQualifierExtraDataList();
		for (HeaderQualifierExtraDataDTO currentHeaderQualifierExtraData : headerQualifierExtraDatas) {
			String ZCUSADD = currentHeaderQualifierExtraData.getDataValue1();
			boolean isTransferReturnInfo = isTransferReturnInfo(currentHeaderQualifierExtraData);
			boolean isTrailerInfo = isTrailerInfo(currentHeaderQualifierExtraData);
			Assert.assertNotSame(isTransferReturnInfo, isTrailerInfo);
			if (isTransferReturnInfo) {
				Assert.assertEquals(externalDeliveryNumber, ZCUSADD);
			}
			if (isTrailerInfo) {
				Assert.assertEquals(trailerPlateNo, ZCUSADD);
			}
		}
	}

	private boolean isTrailerInfo(HeaderQualifierExtraDataDTO headerQualifierExtraData) {
		String qualifierType = headerQualifierExtraData.getQualifierType();
		return qualifierType.equalsIgnoreCase(HeaderQualifierExtraDataDTO.QUALIFIER_TYPE_TRAILER_INFORMATION);
	}

	private boolean isTransferReturnInfo(HeaderQualifierExtraDataDTO headerQualifierExtraData) {
		String qualifierType = headerQualifierExtraData.getQualifierType();
		return qualifierType.equalsIgnoreCase(HeaderQualifierExtraDataDTO.QUALIFIER_TYPE_RETURNED_ORDER_NO);
	}

}