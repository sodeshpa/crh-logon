/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import com.crh.logon.business.services.beans.pdf.PdfGeneratingResult;
import com.crh.logon.business.services.beans.pdf.deliverynote.DeliveryNotePdfDataSource;
import com.crh.logon.business.services.localinterfaces.DeliveryNoteDataCollectorServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.Pd4mlRenderingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.stores.dictionaries.OutputDocumentTypeStoreInterface;
import org.junit.Before;
import org.junit.Test;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderOutputdocEntity;
import com.crh.logon.persistence.entities.common.OutputdocEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.refdata.LangBsEntity;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OutputDocumentTypeBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
public class PDFGeneratorServiceBeanTest {
	private final Long SAMPLE_ENTITY_ID = Long.valueOf(1);
	
	protected MockPDFGeneratorServiceBean pdfGeneratorService;
	
	protected DeliveryNoteDataCollectorServiceLocalInterface deliveryNoteDataCollectorService;
	protected Pd4mlRenderingServiceLocalInterface pd4mlRenderingService;
	protected OrderServiceLocalInterface orderService;
	protected RefDataGenericsServiceLocalInterface referentialDataService;
	protected OutputDocumentTypeStoreInterface outputDocumentTypeStore;
	
	OrderEntity orderEntity;
	SapBpEntity sapBpEntity;
	SapContractEntity sapContractEntity;
	LangBsEntity langBsEntity;
	SareaEntity sareaEntity;
	MimeTypeBsEntity mimeTypeBsEntity;
	OutputDocumentTypeBsEntity outputDocumentTypeBsEntity;
	

	@Before
	public void setup() throws NoSuchMethodException, SecurityException {
		orderEntity = new OrderEntity();
		orderEntity.setOrderId(SAMPLE_ENTITY_ID);
		orderEntity.setOrderNumber(SAMPLE_ENTITY_ID);
		
		sapBpEntity = new SapBpEntity();
		sapBpEntity.setSapBpId(SAMPLE_ENTITY_ID);
		
		sapContractEntity = new SapContractEntity();
		sapContractEntity.setSapContractId(SAMPLE_ENTITY_ID);
		
		langBsEntity = new LangBsEntity();
		langBsEntity.setLangCd("az");
		
		sareaEntity = new SareaEntity();
		orderEntity.setOrderId(Long.valueOf(SAMPLE_ENTITY_ID));
		
		mimeTypeBsEntity = new MimeTypeBsEntity();
		mimeTypeBsEntity.setMimeTypeCd(SAMPLE_ENTITY_ID);
		
		sapBpEntity.setLang(langBsEntity);
		
		orderEntity.setShiptoSapBp(sapBpEntity);
		
		outputDocumentTypeBsEntity = new OutputDocumentTypeBsEntity();
		
		MockPDFGeneratorServiceBean spyPDFGeneratorServiceBean = new MockPDFGeneratorServiceBean();
		pdfGeneratorService  = spy(spyPDFGeneratorServiceBean);
		
		createMockServices();
	}
	
	@Test
	public void testGeneratePDFWithAzeriLanguageAndRequiresExportInvoice() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		when(orderService.isExportInvoice(any(SapContractEntity.class), any(SareaEntity.class))).thenReturn(true);
		when(orderService.isCashSales(any(SapContractEntity.class), any(SareaEntity.class))).thenReturn(false);
		
		setMockServices();
		
		PdfGeneratingResult generatePDF = pdfGeneratorService.generatePDF(SAMPLE_ENTITY_ID);
		
		assertFalse(generatePDF.isSuccess());
		assertTrue(generatePDF.getMessages().contains(Reason.RES_ERROR_DELIVERY_NOTE_NOT_SUPPORT_INVOICE_GENERATE_PDF_FOR_AZERI_LANGUAGE.getReason()));
		assertFalse(generatePDF.getMessages().contains(Reason.RES_ERROR_DELIVERY_NOTE_NOT_SUPPORT_CASH_SALES_TYPE_GENERATE_PDF_FOR_AZERI_LANGUAGE.getReason()));
	}
	
	@Test
	public void testGeneratePDFWithAzeriLanguageAndRequiresExportInvoiceAndCashSales() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		when(orderService.isExportInvoice(any(SapContractEntity.class), any(SareaEntity.class))).thenReturn(true);
		when(orderService.isCashSales(any(SapContractEntity.class), any(SareaEntity.class))).thenReturn(true);
		
		setMockServices();
		
		PdfGeneratingResult generatePDF = pdfGeneratorService.generatePDF(SAMPLE_ENTITY_ID);
		
		assertFalse(generatePDF.isSuccess());
		assertTrue(generatePDF.getMessages().contains(Reason.RES_ERROR_DELIVERY_NOTE_NOT_SUPPORT_INVOICE_GENERATE_PDF_FOR_AZERI_LANGUAGE.getReason()));
		assertTrue(generatePDF.getMessages().contains(Reason.RES_ERROR_DELIVERY_NOTE_NOT_SUPPORT_CASH_SALES_TYPE_GENERATE_PDF_FOR_AZERI_LANGUAGE.getReason()));
	}
	
	@Test
	public void testGeneratePDFWithAzeriLanguageAndCashSales() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		when(orderService.isExportInvoice(any(SapContractEntity.class), any(SareaEntity.class))).thenReturn(false);
		when(orderService.isCashSales(any(SapContractEntity.class), any(SareaEntity.class))).thenReturn(true);
		
		setMockServices();
		
		PdfGeneratingResult generatePDF = pdfGeneratorService.generatePDF(SAMPLE_ENTITY_ID);
		
		assertFalse(generatePDF.isSuccess());
		assertFalse(generatePDF.getMessages().contains(Reason.RES_ERROR_DELIVERY_NOTE_NOT_SUPPORT_INVOICE_GENERATE_PDF_FOR_AZERI_LANGUAGE.getReason()));
		assertTrue(generatePDF.getMessages().contains(Reason.RES_ERROR_DELIVERY_NOTE_NOT_SUPPORT_CASH_SALES_TYPE_GENERATE_PDF_FOR_AZERI_LANGUAGE.getReason()));
	}
	
	private class MockPDFGeneratorServiceBean extends PDFGeneratorServiceBean {
		public void setOrderService(OrderServiceLocalInterface orderService){
			this.orderService = orderService;
		}
		
		public void setDeliveryNoteDataCollectorService(DeliveryNoteDataCollectorServiceLocalInterface deliveryNoteDataCollectorService){
			this.deliveryNoteDataCollectorService = deliveryNoteDataCollectorService;
		}
		
		public void setPd4mlRenderingService(Pd4mlRenderingServiceLocalInterface pd4mlRenderingService){
			this.pd4mlRenderingService = pd4mlRenderingService;
		}
		
		public void setReferentialDataService(RefDataGenericsServiceLocalInterface referentialDataService){
			this.referentialDataService = referentialDataService;
		}
		
		public void setOutputDocumentTypeStore(OutputDocumentTypeStoreInterface outputDocumentTypeStore){
			this.outputDocumentTypeStore = outputDocumentTypeStore;
		}
		
		@SuppressWarnings("unchecked")
		public DaoProvider getDaoProvider() {
			DaoProvider daoProvider = mock(DaoProvider.class);
			ReadWriteDao<OrderEntity> orderDao = (ReadWriteDao<OrderEntity>)mock(ReadWriteDao.class);
			ReadWriteDao<OutputdocEntity> outDocDao = (ReadWriteDao<OutputdocEntity>)mock(ReadWriteDao.class);
			ReadWriteDao<OrderOutputdocEntity> orderOutDocDao = (ReadWriteDao<OrderOutputdocEntity>)mock(ReadWriteDao.class);
			
			when(daoProvider.getOrderEntityDao()).thenReturn(orderDao);
			when(daoProvider.getOutputdocEntityDao()).thenReturn(outDocDao);
			
			Filter<OrderEntity> filter = new FilterImpl<OrderEntity>();
			
			when(orderDao.createFilter()).thenReturn(filter);
			when(orderDao.findSingle(filter)).thenReturn(orderEntity);
			when(outDocDao.persist(new OutputdocEntity())).thenReturn(new OutputdocEntity());
			when(orderOutDocDao.persist(new OrderOutputdocEntity())).thenReturn(new OrderOutputdocEntity());
			
			return daoProvider;
		}
		
		protected ConfigBean getConfigBean() {
			return new ConfigBean();
		}
	}
	
	private void createMockServices() {
		orderService = mock(OrderServiceLocalInterface.class);
		deliveryNoteDataCollectorService = mock(DeliveryNoteDataCollectorServiceLocalInterface.class);
		pd4mlRenderingService = mock(Pd4mlRenderingServiceLocalInterface.class);
		referentialDataService = mock(RefDataGenericsServiceLocalInterface.class);
		outputDocumentTypeStore = mock(OutputDocumentTypeStoreInterface.class);
		
		when(orderService.isExportInvoice(any(SapContractEntity.class), any(SareaEntity.class))).thenReturn(true);
		when(orderService.isCashSales(any(SapContractEntity.class), any(SareaEntity.class))).thenReturn(false);
		
		when(referentialDataService.findBsEntity(LangBsEntity.class, Locale.ENGLISH.getLanguage())).thenReturn(langBsEntity);
		when(referentialDataService.findBsEntity(MimeTypeBsEntity.class, Constant.MIME_TYPE_PDF)).thenReturn(mimeTypeBsEntity);
		
		when(deliveryNoteDataCollectorService.collectData(Locale.ENGLISH, orderEntity)).thenReturn(new DeliveryNotePdfDataSource());
		when(pd4mlRenderingService.renderPdf(new DeliveryNotePdfDataSource())).thenReturn(new ByteArrayOutputStream());		
	}
	
	private void setMockServices() {
		pdfGeneratorService.setOrderService(orderService);
		pdfGeneratorService.setDeliveryNoteDataCollectorService(deliveryNoteDataCollectorService);
		pdfGeneratorService.setPd4mlRenderingService(pd4mlRenderingService);
		pdfGeneratorService.setReferentialDataService(referentialDataService);
		pdfGeneratorService.setOutputDocumentTypeStore(outputDocumentTypeStore);
	}
}