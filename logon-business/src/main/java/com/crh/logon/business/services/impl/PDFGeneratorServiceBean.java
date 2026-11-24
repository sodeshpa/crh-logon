/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.interceptor.Interceptors;

import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.business.services.beans.pdf.PdfReport;
import com.crh.logon.business.services.dispatchers.PDFGeneratorServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.pdftools.layout.JcanPdfRenderer;
import com.crh.logon.business.services.pdftools.store.CollDeliveryNotePdfDataStore;
import com.crh.logon.business.services.pdftools.store.DeliveryNotePdfDataProvider;
import com.crh.logon.business.services.pdftools.store.DeliveryNotePdfDataStore;
import com.crh.logon.business.services.utils.TruckWagonTypeWrapperUtil;
import com.crh.logon.business.stores.dictionaries.OutputDocumentTypeStoreInterface;
import org.apache.commons.io.IOUtils;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.dto.TruckTypeWrapper;
import com.crh.logon.business.services.beans.pdf.PdfGeneratingResult;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DeliveryNoteDataCollectorServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.DispokarteServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.GenericBlobServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MaterialEnhancementsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PDFGeneratorServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.Pd4mlRenderingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.SapOutgoingDataServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TimeZoneServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TruckAdminServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.pdftools.layout.colldelnote.CollDelNoteMainLayout;
import com.crh.logon.business.services.pdftools.layout.delnote.DeliveryNoteMainLayout;
import com.crh.logon.business.services.pdftools.store.CollectiveDeliveryNotePdfDataProvider;
import com.crh.logon.constants.Constant;
import com.crh.logon.pdf.PdfGenerator;
import com.crh.logon.pdftool.renderer.AbstractPdfRenderer;
import com.crh.logon.persistence.dto.BlobDTO;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderOutputdocEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderOutputdocEntity;
import com.crh.logon.persistence.entities.common.OrderTruckTypeEntity;
import com.crh.logon.persistence.entities.common.OutputdocEntity;
import com.crh.logon.persistence.entities.refdata.DelivNoteSareaTextBsEntity;
import com.crh.logon.persistence.entities.refdata.DelivNoteSareaTextTxEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.LangBsEntity;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OutputDocumentTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SorgBsEntity;
import com.crh.logon.persistence.entities.refdata.TextTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.text.PdfTextTransliterator;
import com.crh.logon.utils.text.Transliterator;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@Interceptors(UserInfoInterceptor.class)
@Named
@AdnTrace
public class PDFGeneratorServiceBean extends PDFGeneratorServiceDispatcher implements PDFGeneratorServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PDFGeneratorServiceBean.class);

	private static final ByteArrayOutputStream NO_POSTSCRIPT_OUTPUT = null;
	public static final int ROUNDING_SCALE = 2;
	public static final double SIGNATURE_WIDTH = 135.0; // pixels
	public static final double SIGNATURE_HEIGHT = 75.0; // pixels
	public static final double LOGO_WIDTH = 100.0; // pixels
	public static final double LOGO_HEIGHT = 50.0; // pixels
	public static final double BANNER_WIDTH = 510.0; // pixels
	public static final double BANNER_HEIGHT = 100.0; // pixels

	public static final Long BULK_PWDR_PKG_CD = Constant.PACKAGE_TYPE_BULK_POWDER;
	public static final Long BULK_DMPD_PKG_CD = Constant.PACKAGE_TYPE_BULK_DUMPED;
	public static final Long BAG_PKG_CD = Constant.PACKAGE_TYPE_BBAG;
	public static final Long SERVICE_PKG_CD = Constant.PACKAGE_TYPE_SERVICE;

	@EJB SapOutgoingDataServiceLocalInterface sapOutgoingDataService;
	@EJB
	RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB
	GenericBlobServiceLocalInterface genericBlobService;
	@EJB PDFGeneratorServiceLocalInterface pdfGeneratorService;
	@EJB
	MaterialEnhancementsServiceLocalInterface materialEnhancementsService;
	@EJB OrderServiceLocalInterface orderService;
	@EJB
	DispokarteServiceLocalInterface dispokarteService;
	@EJB TimeZoneServiceLocalInterface timezoneService;
	@EJB TruckAdminServiceLocalInterface truckAdminService;
	@EJB DispoServiceLocalInterface dispoService;
	@EJB ConfigServiceLocalInterface configService;
	@EJB UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	@EJB
	OutputDocumentTypeStoreInterface outputDocumentTypeStore;
	
	@EJB DeliveryNoteDataCollectorServiceLocalInterface deliveryNoteDataCollectorService;
	@EJB Pd4mlRenderingServiceLocalInterface pd4mlRenderingService;
		
	/**
	 * generates pdf report upon given rules inserts images by calling
	 * GenericBlobService
	 *
	 * @param orderId
	 *            for which will be generated pdf
	 * @param loc
	 *            which language to use
	 * @return true if pdf was successfully generated and saved into db
	 */
	public PdfGeneratingResult generatePDF(Long orderId) {
		PdfGeneratingResult result = new PdfGeneratingResult(false);
		try {
			// select the order
			final ReadWriteDao<OrderEntity> orderDao = getDaoProvider().getOrderEntityDao();
			final Filter<OrderEntity> filter = orderDao.createFilter();
			filter.addExpression(Restrictions.eq(OrderEntity.ATTR_ORDER_ID, orderId));

			final OrderEntity order = orderDao.findSingle(filter);

			if ((order == null) || (order.getShiptoSapBp().getLang() == null)) {
				return new PdfGeneratingResult(false);
			}
			else {

				//Delivery note language is determined by the shipto language (TLGNS_SAP_BP -> LANG_CD)
				final Locale loc = new Locale(order.getShiptoSapBp().getLang().getLangCd());
				
				final boolean isCashSales = requiresCashSales(order);
				final boolean isRequiresExportInvoice = requiresExportInvoice(order);
				
				if (isRequiresExportInvoice) {
					/* LOGON-6197: Need both export invoice and delivery note for
					 * such contracts.
					 */
					PdfGeneratingResult result1 = generateDeliveryNote(order, loc, true, isCashSales);
					PdfGeneratingResult result2 = generateDeliveryNote(order, loc, false, isCashSales);
					result = result1.merge(result2);
				}
				else if (isCashSales){
					PdfGeneratingResult result1 = generateDeliveryNote(order, loc, false, true);
					PdfGeneratingResult result2 = generateDeliveryNote(order, loc, false, false);
					result = result1.merge(result2);
				}
				else {
					result = generateDeliveryNote(order, loc, false, false);
				}
			}
			return result;

		}
		catch (Exception e) {
			Object[][] params = { { "orderId", orderId } };
			ExceptionUtil.errException(LOG, e, params);
			return new PdfGeneratingResult(false);
		}
	}



	/**
	 * Returns true if the given order requires generation of an export invoice (as well as a
	 * delivery note). 
	 * @param order The order to check
	 * @return <code>true</code> if an export invoice must be created, <code>false</code>
	 * otherwise.
	 */
	private boolean requiresExportInvoice(final OrderEntity order) {
		return orderService.isExportInvoice(order.getSapContract(), order.getSarea());
	}

	/**
	 * Returns true if an order requires a cash sales invoice to generate as well.
	 * 
	 * @param order
	 * @return <code>true</code> if a cash sales invoice must be created, <code>false</code>
	 */
	private boolean requiresCashSales(final OrderEntity order) {
		return orderService.isCashSales(order.getSapContract(), order.getSarea());
	}
	
	/**
	 * Generates and persists the delivery note or the export invoice for an order.
	 * @param order The order for which to generate the documents
	 * @param locale The locale, which determines the document language.
	 * @param isExportInvoice Set to <code>true</code> to generate an export invoice instead of
	 * a delivery note.
	 * @return
	 */
	private PdfGeneratingResult generateDeliveryNote(final OrderEntity order,
										 Locale locale, final boolean isExportInvoice, final boolean isCashSales) {
		PdfGeneratingResult result = new PdfGeneratingResult(false);
		Transliterator transliterator = new PdfTextTransliterator(locale);

		DeliveryNotePdfDataStore store = null;

		if (usePd4mlBasedGeneration(locale)) {
			result = generateDeliveryNoteViaPd4ml(order, locale, isExportInvoice, isCashSales);
		}
		else if (useTemplateBasedGeneration(locale)) {
			store = DeliveryNotePdfDataStore.getDeliveryNotePdfDataStore(getDaoProvider(), locale,
					order, isExportInvoice, isCashSales, null);
			result = generateDeliveryNoteViaTemplate(store, order, locale, isExportInvoice,
					isCashSales);
		}
		else {
			store = DeliveryNotePdfDataStore.getDeliveryNotePdfDataStore(getDaoProvider(), locale,
					order, isExportInvoice, isCashSales, transliterator);
			result = generateDeliveryNoteViaJcan(store, order, locale, isExportInvoice, isCashSales);
		}

		LOG.info("Delivery note for order (" + order.getOrderNumber() + ") generated");
		return result;
	}

	/**
	 * @param order
	 * @param locale
	 * @param isExportInvoice
	 * @param isCashSales
	 * @return
	 */
	private PdfGeneratingResult generateDeliveryNoteViaPd4ml(OrderEntity order,
			Locale locale, boolean isExportInvoice, boolean isCashSales) {
		ByteArrayOutputStream pdfOutStream = null;
		PdfReport pdfReport = null;
		
		if (isExportInvoice) {
			// 29-Jan-2015/nhattan: LOGON-9091: Error message when generating Delivery note
			// Pd4ml is currently not supporting for export INVOICE
			LOG.error("Pd4ml is not supporting for generating invoice document");
			PdfGeneratingResult result = new PdfGeneratingResult(false);
			result.addMessage(Reason.RES_ERROR_DELIVERY_NOTE_NOT_SUPPORT_INVOICE_GENERATE_PDF_FOR_AZERI_LANGUAGE.getReason());
			return result;
		}
		else if (isCashSales) {
			LOG.error("Pd4ml is not supporting for generating document with cash sales type");
			PdfGeneratingResult result = new PdfGeneratingResult(false);
			result.addMessage(Reason.RES_ERROR_DELIVERY_NOTE_NOT_SUPPORT_CASH_SALES_TYPE_GENERATE_PDF_FOR_AZERI_LANGUAGE.getReason());
			return result;
		}
		else {
			pdfReport = deliveryNoteDataCollectorService.collectData(locale, order);
		}

		pdfOutStream = pd4mlRenderingService.renderPdf(pdfReport);

		if (pdfOutStream != null) {
			storeDeliveryNote(order, locale, isExportInvoice, isCashSales, pdfOutStream, NO_POSTSCRIPT_OUTPUT);
			return new PdfGeneratingResult(true);
		}
		
		return new PdfGeneratingResult(false);
	}

	/**
	 * @param locale
	 * @return
	 */
	private boolean usePd4mlBasedGeneration(Locale locale) {
		ConfigBean cfgBean = getConfigBean();
		boolean isPd4mlPrintingLanguage = cfgBean.isPd4mlPrintingLanguage(locale.getLanguage());
		return isPd4mlPrintingLanguage;
	}

	private boolean useTemplateBasedGeneration(Locale locale) {
		ConfigBean cfgBean = getConfigBean();
		return cfgBean.isAsposePrintingLanguage(locale.getLanguage());
	}

	private PdfGenerator createAsposePdfGenerator(DeliveryNotePdfDataProvider provider) {
		PdfGenerator pdfGenerator = new PdfGenerator(provider);

		String asposeLicense = getConfigBean().getPrintingAsposeLicense();
		if (StringUtils.notEmpty(asposeLicense)) {
			InputStream is = IOUtils.toInputStream(asposeLicense);
			pdfGenerator.setLicense(is);
		}
		else {
			LOG.error(String.format(
					"No Aspose license found on configuration bean. Check '%s' in TLGNR_CONFIG table!!",
					ConfigBean.CFG_PRINTINGASPOSELICENSE));
		}

		return pdfGenerator;
	}

	private PdfGeneratingResult generateDeliveryNoteViaJcan(final DeliveryNotePdfDataStore store,
												final OrderEntity order,
												final Locale locale, 
												final boolean isExportInvoice,
												final boolean isCashSales) {
		PdfGeneratingResult pdfGeneratingResult = new PdfGeneratingResult(false);
		// uncomment to display layout lines
		/*
		System.setProperty("ch.nevis.jcan.print.DebugCurPos", "true");
		System.setProperty("ch.nevis.jcan.print.DebugMargins", "true");
		System.setProperty("ch.nevis.jcan.print.layout.DebugBorder", "true");
		System.setProperty("ch.nevis.jcan.print.layout.DebugDisplacement", "true");
		*/

		final ByteArrayOutputStream pdfOutStream = new ByteArrayOutputStream();
		final ByteArrayOutputStream psOutStream = new ByteArrayOutputStream();
		final AbstractPdfRenderer renderer = new JcanPdfRenderer(pdfOutStream, psOutStream);

		pdfGeneratingResult.setSuccess(DeliveryNoteMainLayout.generateDeliveryNotePDF(store, renderer, genericBlobService));

		storeDeliveryNote(order, locale, isExportInvoice, isCashSales, pdfOutStream, psOutStream);
		return pdfGeneratingResult;
	}

	private PdfGeneratingResult generateDeliveryNoteViaTemplate(final DeliveryNotePdfDataStore store,
													final OrderEntity order,
													final Locale locale, 
													final boolean isExportInvoice,
													final boolean isCashSales) {
		boolean logDebugEnabled = LOG.isDebugEnabled();
		try {
			String templateName = getConfigBean().getPrintingAsposeTemplateDeliveryNote();
			if (logDebugEnabled) {
				LOG.debug("Loading template '" + templateName + "' for delivery note from classpath");
			}

			InputStream is = getClass().getClassLoader().getResourceAsStream(templateName);
			
			DeliveryNotePdfDataProvider provider =
					new DeliveryNotePdfDataProvider(store, genericBlobService, order.getSarea());
			
			PdfGenerator pdfGenerator = createAsposePdfGenerator(provider);
			PdfGenerator.Document document = pdfGenerator.generate(is);
			
			storeDeliveryNote(order, locale, isExportInvoice, isCashSales, document.toPDF(), document.toPS());
			return new PdfGeneratingResult(true);
		}
		catch (IOException exc) {
			throw new LogonException(exc, Reason.RES_ERROR_IMPL);
		}
	}

	private void storeDeliveryNote(final OrderEntity order,
								   final Locale locale,
								   final boolean isExportInvoice,
								   final boolean isCashSales,
								   final ByteArrayOutputStream pdfOutStream,
								   final ByteArrayOutputStream psOutStream) {
		final ReadWriteDao<OutputdocEntity> outDocDao = getDaoProvider().getOutputdocEntityDao();
		final LangBsEntity langBs = referentialDataService.findBsEntity(LangBsEntity.class,
				locale.getLanguage());

		// Determine file name
		final String outputdocName;

		OutputDocumentTypeBsEntity outputDocumentType;
		if (isExportInvoice) {
			outputdocName = Constant.DOC_NAME_EXPINV + " (" + order.getOrderNumber() + ")";
			outputDocumentType = outputDocumentTypeStore.getExportInvoiceType();
		}
		else if (isCashSales) {
			outputdocName = Constant.DOC_NAME_CASHSALES + " (" + order.getOrderNumber() + ")";
			outputDocumentType = outputDocumentTypeStore.getCashSalesType();
		}
		else {
			outputdocName = Constant.DOC_NAME_DELNOTE + " (" + order.getOrderNumber() + ")";
			outputDocumentType = outputDocumentTypeStore.getDeliveryNoteType();
		}

		OutputdocEntity outDocPdf = new OutputdocEntity();
		outDocPdf.setOutputdocName(outputdocName);
		outDocPdf.setOutputdocType(outputDocumentType);
		outDocPdf.setLang(langBs);

		final MimeTypeBsEntity dataMimeTypePdf = referentialDataService.findBsEntity(
				MimeTypeBsEntity.class, Constant.MIME_TYPE_PDF);
		outDocPdf.setDataMimeType(dataMimeTypePdf);
		outDocPdf.setData(pdfOutStream.toByteArray());
		outDocPdf = outDocDao.persist(outDocPdf);

		OutputdocEntity outDocPs = null;

		// store ps
		if (psOutStream != NO_POSTSCRIPT_OUTPUT) {
			outDocPs = new OutputdocEntity();
			
			outDocPs.setOutputdocName(outputdocName);
			outDocPs.setOutputdocType(outputDocumentType);
			outDocPs.setLang(langBs);
			
			final MimeTypeBsEntity dataMimeTypePs = referentialDataService.findBsEntity(
					MimeTypeBsEntity.class, Constant.MIME_TYPE_PS);
			outDocPs.setDataMimeType(dataMimeTypePs);
			outDocPs.setData(psOutStream.toByteArray());
			outDocPs = outDocDao.persist(outDocPs);
		}

		//store both docs
		final ReadWriteDao<OrderOutputdocEntity> orderOutDocDao = getDaoProvider().getOrderOutputdocEntityDao();
		OrderOutputdocEntity orderDoc = new OrderOutputdocEntity();
		orderDoc.setOrder(order);
		orderDoc.setGeneratedInOrderStatus(order.getOrderStatus());
		orderDoc.setPdfOutputdoc(outDocPdf);
		orderDoc.setPsOutputdoc(outDocPs);
		orderOutDocDao.persist(orderDoc);
	}



	/**
	 * Generates a collective delivery note for the input customer order
	 *
	 * @param custOrderId
	 * @return
	 */
	public boolean generateCollPdf(Long custOrderId) {
		boolean result = false;
		// select the order
		final ReadOnlyDao<CustorderEntity> orderDao = getDaoProvider().getCustorderEntityDao();
		final Filter<CustorderEntity> filter = orderDao.createFilter();
		filter.addExpression(Restrictions.eq(CustorderEntity.ATTR_CUSTORDER_ID, custOrderId));
		CustorderEntity order = orderDao.findSingle(filter);

		if ((order == null) || (order.getShiptoSapBp().getLang() == null)) {
			return false;
		}

		final Locale locale = new Locale(order.getShiptoSapBp().getLang().getLangCd());

		Transliterator transliterator = new PdfTextTransliterator(locale);

		if (useTemplateBasedGeneration(locale)) {
			CollDeliveryNotePdfDataStore store = CollDeliveryNotePdfDataStore.getCollDeliveryNotePdfDataStore(
					getDaoProvider(), locale, order, null);
			result = generateCollectiveDeliveryNoteViaTemplate(store, order, locale);
		}
		else {
			CollDeliveryNotePdfDataStore store = CollDeliveryNotePdfDataStore.getCollDeliveryNotePdfDataStore(
					getDaoProvider(), locale, order, transliterator);
			result = generateCollectiveDeliveryNoteViaJcan(store, order, locale);
		}

		LOG.info("Collective delivery note for customer order (" + order.getCustorderNumber()
				+ ") generated");
		return result;
	}

	private boolean generateCollectiveDeliveryNoteViaTemplate(
			final CollDeliveryNotePdfDataStore store,
			final CustorderEntity order,
			final Locale locale) {
		try {
			boolean logDebugEnabled = LOG.isDebugEnabled();
			String templateName = getConfigBean().getPrintingAsposeTemplateCollectiveDeliveryNote();
			if (logDebugEnabled) {
				LOG.debug("Loading template '" + templateName + "' for collective delivery note from classpath");
			}

			InputStream is = getClass().getClassLoader().getResourceAsStream(templateName);

			CollectiveDeliveryNotePdfDataProvider provider =
					new CollectiveDeliveryNotePdfDataProvider(store, genericBlobService, order.getSarea());

			PdfGenerator pdfGenerator = createAsposePdfGenerator(provider);
			PdfGenerator.Document document = pdfGenerator.generate(is);

			storeCollectiveDeliveryNote(order, locale, document.toPDF(), document.toPS());
			return true;
		}
		catch (IOException exc) {
			throw new LogonException(exc, Reason.RES_ERROR_IMPL);
		}
	}

	private boolean generateCollectiveDeliveryNoteViaJcan(
			final CollDeliveryNotePdfDataStore store,
			final CustorderEntity order,
			final Locale locale) {
		// uncomment to display layout lines
/*
			System.setProperty("ch.nevis.jcan.print.DebugCurPos", "true");
			System.setProperty("ch.nevis.jcan.print.DebugMargins", "true");
			System.setProperty("ch.nevis.jcan.print.layout.DebugBorder", "true");
			System.setProperty("ch.nevis.jcan.print.layout.DebugDisplacement", "true");
*/

		final ByteArrayOutputStream pdfOutStream = new ByteArrayOutputStream();
		final ByteArrayOutputStream psOutStream = new ByteArrayOutputStream();
		final AbstractPdfRenderer renderer = new JcanPdfRenderer(pdfOutStream, psOutStream);

		boolean result = CollDelNoteMainLayout.generateCollectiveDeliveryNote(store, renderer, genericBlobService);

		storeCollectiveDeliveryNote(order, locale, pdfOutStream, psOutStream);
		return result;
	}

	private void storeCollectiveDeliveryNote(final CustorderEntity order,
											 final Locale locale,
											 final ByteArrayOutputStream pdfOutStream,
											 final ByteArrayOutputStream psOutStream) {
		ReadWriteDao<OutputdocEntity> outDocDao = getDaoProvider().getOutputdocEntityDao();
		LangBsEntity langBs = referentialDataService.findBsEntity(LangBsEntity.class, locale.getLanguage());

		//store pdf
		OutputdocEntity outDocPdf = new OutputdocEntity();
		outDocPdf.setOutputdocName(Constant.DOC_NAME_COLL_DELNOTE + " (" + order.getCustorderNumber() + ")");
		outDocPdf.setLang(langBs);
		outDocPdf.setOutputdocType(outputDocumentTypeStore.getCollectiveDeliveryNoteType());

		MimeTypeBsEntity dataMimeTypePdf = referentialDataService.findBsEntity(
				MimeTypeBsEntity.class, Constant.MIME_TYPE_PDF);
		outDocPdf.setDataMimeType(dataMimeTypePdf);
		outDocPdf.setData(pdfOutStream.toByteArray());
		outDocPdf = outDocDao.persist(outDocPdf);

		//store ps
		OutputdocEntity outDocPs = new OutputdocEntity();
		outDocPs.setOutputdocName(Constant.DOC_NAME_COLL_DELNOTE + " (" + order.getCustorderNumber() + ")");
		outDocPs.setLang(langBs);
		outDocPs.setOutputdocType(outputDocumentTypeStore.getCollectiveDeliveryNoteType());

		MimeTypeBsEntity dataMimeTypePs = referentialDataService.findBsEntity(
				MimeTypeBsEntity.class, Constant.MIME_TYPE_PS);
		outDocPs.setDataMimeType(dataMimeTypePs);
		outDocPs.setData(psOutStream.toByteArray());
		outDocPs = outDocDao.persist(outDocPs);

		//store both docs
		ReadWriteDao<CustorderOutputdocEntity> orderOutDocDao = getDaoProvider().getCustorderOutputdocEntityDao();
		CustorderOutputdocEntity custOrderDoc = new CustorderOutputdocEntity();
		custOrderDoc.setCustorder(order);
		custOrderDoc.setGeneratedInCustordStat(order.getCustorderStatus());
		custOrderDoc.setPdfOutputdoc(outDocPdf);
		custOrderDoc.setPsOutputdoc(outDocPs);
		orderOutDocDao.persist(custOrderDoc);
	}

	boolean isBulkPkgType(final Long packageType) {
		return BULK_PWDR_PKG_CD.equals(packageType) ||
				BULK_DMPD_PKG_CD.equals(packageType);
	}

	public String formatText(String textCd, Locale locale) {
		return formatText(textCd, locale, null);
	}
	
	public String formatText(String textCd, Locale locale, Object[] messageArguments, DeliveryTypeBsEntity delType, SorgBsEntity sorg, 
			String distChannel, String divisionCd) {
		if (delType == null || sorg == null || distChannel == null || divisionCd == null) {
			return formatText(textCd, locale, messageArguments);
		}
		return formatTextWithDelNoteSareaLookup(textCd, locale, delType, sorg, 
				distChannel, divisionCd, messageArguments);
	}
	
	/**
	 * Returns the string for the given texCd. First it looks in the TLGNR_DELIV_NOTE_SAREA_TEXT_BS table.
	 * If it can't find a text entity for the given parameters, it falls back to {@link PDFGeneratorServiceBean#formatText(String, Locale, Object[])}
	 *
	 * @param textCd must not be null
	 * @param locale must not be null
	 * @param delType must not be null
	 * @param sorg must not be null
	 * @param distChannel must not be null
	 * @param divisionCd must not be null
	 * @param messageArguments must not be null
	 * @return
	 */
	public String formatTextWithDelNoteSareaLookup(String textCd, Locale locale, DeliveryTypeBsEntity delType, SorgBsEntity sorg,
			String distChannel, String divisionCd, Object[] messageArguments) {
		DelivNoteSareaTextBsEntity bs = null;

		final List<DelivNoteSareaTextBsEntity> deliveryTypeBsEntityList = referentialDataService.findActiveBsEntityList(DelivNoteSareaTextBsEntity.class);
		for (DelivNoteSareaTextBsEntity bsTemp : deliveryTypeBsEntityList) {
			if (bsTemp.getTextCd().equals(textCd)
					&& bsTemp.getDeliveryType().getDeliveryTypeCd().compareTo(
							delType.getDeliveryTypeCd()) == 0
					&& bsTemp.getDistributionChannel().equals(distChannel)
					&& bsTemp.getDivisionCd().equals(divisionCd)
					&& bsTemp.getSorg().getSorgCd().equals(sorg.getSorgCd())) {
				bs = bsTemp;
			}
		}
		if (bs == null) {
			// fallback
			return formatText(textCd, locale, messageArguments);
		}
		DelivNoteSareaTextTxEntity text = referentialDataService.findTxEntity(
				DelivNoteSareaTextTxEntity.class, locale, bs);
		if (text == null) {
			LOG.error("Could not find text in TextEntity for textCd = " + textCd + " and lang = "
					+ locale.getLanguage());
			return "";
		}
		return handleTextEntity(text.getTxt(), messageArguments, locale);
	}

	public String formatText(String textCd, Locale locale, Object[] messageArguments) {
		return formatText(textCd, locale, null, messageArguments);
	}
	
	public String formatText(String textCd, Locale locale, TimeZone timeZone, Object[] messageArguments) {
		TextTxEntity text = referentialDataService.findTxEntity(TextTxEntity.class, locale, textCd);
		if (text == null) {
			LOG.error("Could not find text in TextEntity for textCd = " + textCd + " and lang = "
					+ locale.getLanguage());
			return "";
		}
		return handleTextEntity(text.getTxt(), messageArguments, locale, timeZone);
	}

	public String lookUpTxText(String textCd, Locale locale){
		TextTxEntity txEntity = referentialDataService.findTxEntity(TextTxEntity.class, locale, textCd);
		if (txEntity != null){
			return txEntity.getTxt();
		} else{
			return null;
		}
	}

	private String handleTextEntity(String text, Object[] arguments, Locale locale) {
		return handleTextEntity(text, arguments, locale, null);
	}
	
	private String handleTextEntity(String text, Object[] arguments, Locale locale,
			TimeZone timeZone) {
		if ((arguments != null) && (arguments.length > 0)) {
			MessageFormat formatter = new MessageFormat(text);
			if (timeZone != null) {
				Object[] formats = formatter.getFormats();
				for (int i = 0; i < formats.length; i++) {
					if (formats[i] instanceof SimpleDateFormat) {
						((SimpleDateFormat) formats[i]).setTimeZone(timeZone);
					}
				}
			}
			formatter.setLocale(locale);
			return formatter.format(arguments);
		}
		else {
			return text;
		}
	}

	/**
	 * Returns those advance invoice numbers which are - not yet but - most probably will be
	 * associated with the given order item entity.
	 *
	 * @see SapOutgoingDataServiceBean.expandAdvancePaymentOrderItem()
	 *
	 * @return The list of Strings - advance invoice numbers.
	 */
	private BigDecimal calculateQuantityOnQuantityControlledItem(OrderItemEntity orderItem)
			throws LogonException {
		BigDecimal result = null;
		final SapContractItemEntity apContractItemGroupLeader = orderItem.getSapContractItem();

		BigDecimal deliveredQuantity = orderItem.getDeliveredQuantity();
		BigDecimal loadedQuantity = orderItem.getLoadedQuantity();
		BigDecimal orderedQuantity = orderItem.getOrderedQuantity();

		// Set up data for UoM convesion
		BigDecimal quantity = null;
		final TruckTypeBsEntity truckTypeBsEntity = getTruckTypeForOrderItem(orderItem);
		UomBsEntity fromUoM = null;
		final UomBsEntity toUoM = apContractItemGroupLeader.getTargetQuantityUom();

		if (deliveredQuantity != null) {
			fromUoM = orderItem.getDeliveredUom();
			quantity = deliveredQuantity;
		}
		else if (loadedQuantity != null) {
			fromUoM = orderItem.getLoadedUom();
			quantity = loadedQuantity;
		}
		else {
			fromUoM = orderItem.getOrderedUom();
			quantity = orderedQuantity;
		}
		final boolean allowFuzzy = fromUoM.getFuzzyQuantityFlag() || toUoM.getFuzzyQuantityFlag();

		// Perform the UoM conversion
		UomConversionResult conversion = null;
		try {
			conversion = unitOfMeasurementConverterService.convert(quantity, fromUoM, toUoM,
					apContractItemGroupLeader.getSapMaterial(), truckTypeBsEntity, null, allowFuzzy);
		}
		catch (LogonConversionException e) {
			String errorMsg = "Could not perform UoM conversion for getting advance invoice numbers.";
			LOG.error(errorMsg, e);
			new LogonException(errorMsg, e);
		}

		if (conversion == null || conversion.getQuantity() == null) {
			final String errorMsg = "The UoM conversion result is null while getting advance invoice numbers.";
			LOG.error(errorMsg);
			throw new LogonException(errorMsg, Reason.RES_ERROR_IMPL);
		}

		return conversion.getQuantity();
	}

	private TruckTypeBsEntity getTruckTypeForOrderItem(OrderItemEntity orderItem) {
		TruckTypeBsEntity truckType;
		if (orderItem.getDeliveredLoadTruck() != null) {
			truckType = orderItem.getDeliveredLoadTruck().getTruckType();
		}
		else {
			truckType = getProperTruckTypeForOrderItem(orderItem);
		}
		return truckType;
	}

	private TruckTypeBsEntity getProperTruckTypeForOrderItem(OrderItemEntity orderItem) {
		TruckTypeBsEntity result = null;
		final OrderEntity order = orderItem.getOrder();
		final List<OrderTruckTypeEntity> orderTruckTypeEntities = order.getOrderVehicleTypes();

		final Map<Long, OrderTruckTypeEntity> orderTruckTypeByTruckTypeCd = new HashMap<Long, OrderTruckTypeEntity>();

		final List<TruckWagonTypeWrapper> truckTypeWrappers = new ArrayList<TruckWagonTypeWrapper>();
		for (OrderTruckTypeEntity orderTruckType : orderTruckTypeEntities) {
			final Long truckTypeCd = orderTruckType.getTruckType().getTruckTypeCd();
			orderTruckTypeByTruckTypeCd.put(truckTypeCd, orderTruckType);

			final Long priority = orderTruckType.getEvaluationPriority();
			final TruckTypeBsEntity truckTypeBsEntity = orderTruckType.getTruckType();

			truckTypeWrappers.add(new TruckTypeWrapper(priority, truckTypeBsEntity, null));
		}

		final TruckWagonTypeWrapper properTruckTypeWrapper = TruckWagonTypeWrapperUtil.findProperTruck(truckTypeWrappers);
		final Long properTruckTypeCd = properTruckTypeWrapper.getTypeCd();
		final OrderTruckTypeEntity properOrderTruckType = orderTruckTypeByTruckTypeCd.get(properTruckTypeCd);
		if (properOrderTruckType != null) {
			result = properOrderTruckType.getTruckType();
		}
		else {
			result = null;
		}
		return result;
	}
	
	public BlobDTO fetchPdfLogo(Locale locale, String imageName) {
		String languagePostfix = (Constant.LANGUAGE_CODE_GERMAN.equals(locale.getLanguage())) ? ""
				: "_" + locale.getLanguage();
		
		BlobDTO image = genericBlobService.fetchPdfImage(imageName + languagePostfix);
		
		if (image == null || image.getStream() == null) {
			image = genericBlobService.fetchPdfImage(imageName);
		}
		
		return image;
	}

	/**
	 * Returns those advance invoice numbers which are (or probably will be)
	 * associated with the given order item entity.
	 *
	 * @return The list of Strings - advance invoice numbers.
	 */
	public List<String> getAdvanceInvoiceNumbers(OrderItemEntity orderItem) {
		List<String> result = new ArrayList<String>();

		List<OrderItemEntity> virtualItems = sapOutgoingDataService.mapQuantityControlledOrderItems(Collections.singletonList(orderItem));

		if (virtualItems.isEmpty()) {
			BigDecimal quantity = calculateQuantityOnQuantityControlledItem(orderItem);
			virtualItems = sapOutgoingDataService.distributeQuantityOnOrderItem(orderItem, quantity);
		}

		for (OrderItemEntity virtualItem : virtualItems) {
			SapContractItemEntity contractItem = virtualItem.getSapContractItem();
			if (contractItem != null) {
				String advanceInvoiceNumber = contractItem.getAdvanceInvoiceNo();
				result.add(advanceInvoiceNumber);
			}
		}
		return result;
	}
}