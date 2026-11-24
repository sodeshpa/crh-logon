/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.store;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.GenericBlobServiceLocalInterface;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteData;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteLabel;
import com.crh.logon.business.services.pdftools.layout.EichbelegDTO;
import com.crh.logon.business.services.pdftools.layout.MaterialTotalsTextDTO;
import com.crh.logon.business.services.pdftools.layout.OrderItemDTO;
import com.crh.logon.business.services.reporting.PDFUtils;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.constants.Constant;
import com.crh.logon.pdf.templating.DataProvider;
import com.crh.logon.pdf.templating.DocumentValue;
import com.crh.logon.pdf.templating.ImageValue;
import com.crh.logon.pdf.templating.ParsingException;
import com.crh.logon.pdf.templating.Path;
import com.crh.logon.pdf.templating.TextValue;
import com.crh.logon.pdf.templating.Value;
import com.crh.logon.persistence.dto.BlobDTO;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.sap.SapMatPlantNormEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryNotePdfDataProvider implements DataProvider {

	private static final char PDF_NEWLINE = '\r';
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DeliveryNotePdfDataProvider.class);
	
	protected static abstract class TagData {
		abstract Value toValue(DeliveryNotePdfDataProvider provider);
	}

	protected static class ImageData extends TagData {
		private final byte[] image;

		protected ImageData(byte[] image) {
			this.image = image;
		}

		Value toValue(DeliveryNotePdfDataProvider provider) {
			if (image != null && image.length > 0) {
				return new ImageValue(image);
			}
			return null;
		}
	}

	protected static class NoteData extends TagData {
		private final DeliveryNoteData key;

		protected NoteData(DeliveryNoteData key) {
			this.key = key;
		}

		Value toValue(DeliveryNotePdfDataProvider provider) {
			return new TextValue(provider.store.getData(key));
		}
	}

	protected static class LabelData extends TagData {
		private final DeliveryNoteLabel key;

		protected LabelData(DeliveryNoteLabel key) {
			this.key = key;
		}

		Value toValue(DeliveryNotePdfDataProvider provider) {
			return new TextValue(provider.store.getLabel(key));
		}
	}

	private static final Map<String, Object> GLOBAL_KEYS;

	static {
		Map<String, Object> globalKeys = new HashMap<String, Object>();

		//--- AddressPart ---//
		globalKeys.put("SorgTxEntity.ADDRESS_NAME", new NoteData(DeliveryNoteData.DEST_ADDRESS1));
		globalKeys.put("SorgBsEntity.ADDRESS_STREET", new NoteData(DeliveryNoteData.DEST_ADDRESS2));
		globalKeys.put("SorgBsEntity.ADDRESS_COUNTRYCITY", new NoteData(DeliveryNoteData.DEST_ADDRESS3));
		globalKeys.put("SorgTxEntity.LINE_PHONE", new NoteData(DeliveryNoteData.DEST_TEL));
		globalKeys.put("SorgTxEntity.LINE_FAX", new NoteData(DeliveryNoteData.DEST_FAX));
		globalKeys.put("SorgTxEntity.LINE_TAX", new NoteData(DeliveryNoteData.DEST_TAX));
		globalKeys.put("SorgTxEntity.LINE_EMAIL", new NoteData(DeliveryNoteData.DEST_EMAIL));

		//--- TitlePart ---//
		globalKeys.put("DeliveryNoteLabel", new LabelData(DeliveryNoteLabel.ORDER_NUMBER));
		globalKeys.put("DeliveryNoteOrderNo", new NoteData(DeliveryNoteData.ORDER_NR));
		globalKeys.put("DeliveryPlantNameLabel", new LabelData(DeliveryNoteLabel.PLANT));
		globalKeys.put("DeliveryPlantName", new NoteData(DeliveryNoteData.PLANT));
		globalKeys.put("ShippingpointNameLabel", new LabelData(DeliveryNoteLabel.SHIPPING_POINT));
		globalKeys.put("ShippingpointName", new NoteData(DeliveryNoteData.SHIPPING_POINT));
		globalKeys.put("OutputdocEntity.CTL_CRE_TS", new NoteData(DeliveryNoteData.DELIVERY_NOTE_DATE));
		globalKeys.put("DeliveryNoteNo[1]", new NoteData(DeliveryNoteData.ORDER_NR_ALT));
		globalKeys.put("DeliveryNoteNo[2]", new NoteData(DeliveryNoteData.ORDER_NR_ALT2));
		globalKeys.put("IssueLocationLabel", new LabelData(DeliveryNoteLabel.EXPORT_INVOICE_ISSUE_LOCATION));
		globalKeys.put("IssueLocation", new NoteData(DeliveryNoteData.EXPORT_INVOICE_ISSUE_LOCATION));

		//--- DeliveryNoteShippingPart.NumberParts ---//
		globalKeys.put("CommercialDistanceKmLabel", new LabelData(DeliveryNoteLabel.DISTANCE));
		globalKeys.put("CommercialDistanceKm", new NoteData(DeliveryNoteData.DISTANCE));
		globalKeys.put("ExportInvoiceControllerNoLabel", new LabelData(DeliveryNoteLabel.EXPORT_INVOICE_CONTROLLER_NUMBER));
		globalKeys.put("SapContractEntity.CONTROLLER_NO", new NoteData(DeliveryNoteData.EXPORT_INVOICE_CONTROLLER_NUMBER));
		globalKeys.put("DeliveryWindowLabel", new LabelData(DeliveryNoteLabel.DELIVERY_WINDOW));
		globalKeys.put("DeliveryFromDate", new NoteData(DeliveryNoteData.DELIVERY_WINDOW_FROM));
		globalKeys.put("DeliveryToDate", new NoteData(DeliveryNoteData.DELIVERY_WINDOW_TO));
		globalKeys.put("CustRefContractNoLabel", new NoteData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_TILE));
		globalKeys.put("CustRefContractNo", new NoteData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_DATA));
		globalKeys.put("SapContractNoLabel", new LabelData(DeliveryNoteLabel.SAP_CONTRACT_NO));
		globalKeys.put("SAP_CONTRACT_NO", new NoteData(DeliveryNoteData.SAP_CONTRACT_NO));
		globalKeys.put("AdvancePaymentInvoiceNoLabel", new LabelData(DeliveryNoteLabel.EXPORT_INVOICE_ADVANCE_PAYMENT_INVOICE_NUMBER));
		globalKeys.put("AdvanceInvoiceNumbersConcat", new NoteData(DeliveryNoteData.EXPORT_INVOICE_ADVANCE_PAYMENT_INVOICE_NUMBER));
		globalKeys.put("LdsSealNumbersLabel", new LabelData(DeliveryNoteLabel.SEALS_NUMBERS));
		globalKeys.put("AssignmentEntity.LDS_SEAL_NUMBERS", new NoteData(DeliveryNoteData.SEALS_NUMBERS));
		globalKeys.put("CarrierCrmLabel", new LabelData(DeliveryNoteLabel.CARRIER_CRM));
		globalKeys.put("AssignmentEntity.LDS_CARRIER_CRM", new NoteData(DeliveryNoteData.CARRIER_CRM));

		//--- DeliveryNoteShippingPart.LoadDelTimePart ---//
		globalKeys.put("DeliverySiloLabel", new LabelData(DeliveryNoteLabel.DELIVERY_SILO));
		globalKeys.put("LoadEntity.WS_SILO_NO", new NoteData(DeliveryNoteData.DELIVERY_SILO));
		globalKeys.put("PlantEntryLabel", new LabelData(DeliveryNoteLabel.PLANT_ENTRY));
		globalKeys.put("LoadEntity.WS_ENTER_SHIPPINGPOINT_TIME", new NoteData(DeliveryNoteData.PLANT_ENTRY));
		globalKeys.put("PlantExitLabel", new LabelData(DeliveryNoteLabel.PLANT_EXIT));
		globalKeys.put("LoadEntity.WS_LEAVE_SHIPPINGPOINT_TIME", new NoteData(DeliveryNoteData.PLANT_EXIT));
		globalKeys.put("EndloadDateLabel", new LabelData(DeliveryNoteLabel.ENDLOAD_DATE));
		globalKeys.put("LatestLoadingTime", new NoteData(DeliveryNoteData.LOAD_DATE));
		globalKeys.put("DeliveryTimeLabel", new LabelData(DeliveryNoteLabel.DELIVERY_TIME));
		globalKeys.put("DeliveryTime", new NoteData(DeliveryNoteData.DELIVERY_DATE));
		globalKeys.put("IncotermLabel", new LabelData(DeliveryNoteLabel.INCOTERM));
		globalKeys.put("SapContractEntity.INCOTERMS_CD", new NoteData(DeliveryNoteData.INCOTERM));
		globalKeys.put("DeliveryTypeTxt", new NoteData(DeliveryNoteData.DELIVERY_TYPE));
		globalKeys.put("ReasonOfTransportLabel", new LabelData(DeliveryNoteLabel.REASON_OF_TRANSPORT));
		globalKeys.put("ReasonOfTransport", new NoteData(DeliveryNoteData.REASON_OF_TRANSPORT));

		//--- Address Information ---//
		globalKeys.put("SapBpEntity[0].Address", new LabelData(DeliveryNoteLabel.ADDRESS_SHIPTO));
		globalKeys.put("SapBpEntity[0].SAP_ID", new NoteData(DeliveryNoteData.SHIPTO_SAP));
		globalKeys.put("SapBpEntity[0].line1", new NoteData(DeliveryNoteData.SHIPTO_ADDRESS1));
		globalKeys.put("SapBpEntity[0].line2", new NoteData(DeliveryNoteData.SHIPTO_ADDRESS2));
		globalKeys.put("SapBpEntity[0].line3", new NoteData(DeliveryNoteData.SHIPTO_ADDRESS3));
		globalKeys.put("SapBpEntity[0].line4", new NoteData(DeliveryNoteData.SHIPTO_VAT));

		globalKeys.put("SapBpEntity[1].Address", new LabelData(DeliveryNoteLabel.ADDRESS_BILLTO));
		globalKeys.put("SapBpEntity[1].SAP_ID", new NoteData(DeliveryNoteData.BILLTO_SAP));
		globalKeys.put("SapBpEntity[1].line1", new NoteData(DeliveryNoteData.BILLTO_ADDRESS1));
		globalKeys.put("SapBpEntity[1].line2", new NoteData(DeliveryNoteData.BILLTO_ADDRESS2));
		globalKeys.put("SapBpEntity[1].line3", new NoteData(DeliveryNoteData.BILLTO_ADDRESS3));

		globalKeys.put("SapBpEntity[2].Address", new LabelData(DeliveryNoteLabel.ADDRESS_PAYER));
		globalKeys.put("SapBpEntity[2].SAP_ID", new NoteData(DeliveryNoteData.PAYER_SAP));
		globalKeys.put("SapBpEntity[2].line1", new NoteData(DeliveryNoteData.PAYER_ADDRESS1));
		globalKeys.put("SapBpEntity[2].line2", new NoteData(DeliveryNoteData.PAYER_ADDRESS2));
		globalKeys.put("SapBpEntity[2].line3", new NoteData(DeliveryNoteData.PAYER_ADDRESS3));

		globalKeys.put("SapBpEntity[3].Address", new LabelData(DeliveryNoteLabel.ADDRESS_SOLDTO));
		globalKeys.put("SapBpEntity[3].SAP_ID", new NoteData(DeliveryNoteData.SOLDTO_SAP));
		globalKeys.put("SapBpEntity[3].line1", new NoteData(DeliveryNoteData.SOLDTO_ADDRESS1));
		globalKeys.put("SapBpEntity[3].line2", new NoteData(DeliveryNoteData.SOLDTO_ADDRESS2));
		globalKeys.put("SapBpEntity[3].line3", new NoteData(DeliveryNoteData.SOLDTO_ADDRESS3));
		globalKeys.put("SapBpEntity[3].line4", new NoteData(DeliveryNoteData.SOLDTO_PHONE));
		globalKeys.put("SapBpEntity[3].line5", new NoteData(DeliveryNoteData.SOLDTO_VATREG));
		globalKeys.put("SapBpEntity[3].line6", new NoteData(DeliveryNoteData.CASH_SALES_TAX_NUMBER1));

		globalKeys.put("CarrierEntity[0].Address", new LabelData(DeliveryNoteLabel.ADDRESS_COMM_CARRIER));
		globalKeys.put("CarrierEntity[0].SAP_ID", new NoteData(DeliveryNoteData.COMM_CARRIER_SAP));
		globalKeys.put("CarrierEntity[0].line1", new NoteData(DeliveryNoteData.COMM_CARRIER_ADDRESS1));
		globalKeys.put("CarrierEntity[0].line2", new NoteData(DeliveryNoteData.COMM_CARRIER_ADDRESS2));
		globalKeys.put("CarrierEntity[0].line3", new NoteData(DeliveryNoteData.COMM_CARRIER_ADDRESS3));
		globalKeys.put("CarrierEntity[0].line4", new NoteData(DeliveryNoteData.COMM_CARRIER_PHONE));
		globalKeys.put("CarrierEntity[0].line5", new NoteData(DeliveryNoteData.COMM_CARRIER_PIVA_IA));

		globalKeys.put("CarrierEntity[1].Address", new LabelData(DeliveryNoteLabel.ADDRESS_EXEC_CARRIER));
		globalKeys.put("CarrierEntity[1].SAP_ID", new NoteData(DeliveryNoteData.EXEC_CARRIER_SAP));
		globalKeys.put("CarrierEntity[1].line1", new NoteData(DeliveryNoteData.EXEC_CARRIER_ADDRESS1));
		globalKeys.put("CarrierEntity[1].line2", new NoteData(DeliveryNoteData.EXEC_CARRIER_ADDRESS2));
		globalKeys.put("CarrierEntity[1].line3", new NoteData(DeliveryNoteData.EXEC_CARRIER_ADDRESS3));
		globalKeys.put("CarrierEntity[1].line4", new NoteData(DeliveryNoteData.EXEC_CARRIER_PHONE));
		globalKeys.put("CarrierEntity[1].line5", new NoteData(DeliveryNoteData.EXEC_CARRIER_PIVA_IA));

		//--- Cash Sales Delivery Note ---//
		globalKeys.put("CashSalesMaterialHeader", new NoteData(DeliveryNoteData.CASH_SALES_FIXED_TEXT));
		globalKeys.put("PdfDeliverynoteCrossboundary", new NoteData(DeliveryNoteData.CROSSBOUNDARY));
		globalKeys.put("PdfDeliverynoteCrossboundaryLine1", new NoteData(DeliveryNoteData.CROSSBOUNDARY_EXPORT_INVOICE_LINE1));
		globalKeys.put("PdfDeliverynoteCrossboundaryLine2", new NoteData(DeliveryNoteData.CROSSBOUNDARY_EXPORT_INVOICE_LINE2));
		globalKeys.put("NHMCodeLabel", new LabelData(DeliveryNoteLabel.NHM_CODE));
		globalKeys.put("SapMaterialPlantEntity.NHM_CODE", new NoteData(DeliveryNoteData.NHM));
		globalKeys.put("StationTitlePar", new NoteData(DeliveryNoteData.STATION_TITLE_PAR));

		// Total text
		globalKeys.put("ExportInvoiceTotalText", new NoteData(DeliveryNoteData.EXPORT_INVOICE_TOTAL_TEXT));

		//-- Legal Text Entries --//
		globalKeys.put("LegalText1", new NoteData(DeliveryNoteData.EXPORT_INVOICE_LEGAL_TEXT_1));
		globalKeys.put("LegalText2", new NoteData(DeliveryNoteData.EXPORT_INVOICE_LEGAL_TEXT_2));

		//-- Remark ---//
		globalKeys.put("FieldsRemarks", new LabelData(DeliveryNoteLabel.FIELDS_REMARKS));
		globalKeys.put("DeliveryNoteComment", new NoteData(DeliveryNoteData.COMMENT));
		globalKeys.put("DeliveryNoteFixRemark", new NoteData(DeliveryNoteData.FIX_REMARK_TXT));

		//--- Driver/Dispatcher/Customer Info ---//
		globalKeys.put("CrhLabel", new LabelData(DeliveryNoteLabel.CRH));
		globalKeys.put("AssignmentEntity.LDS_DISPATCHER_NAME", new NoteData(DeliveryNoteData.DISPATCHER_NAME));
		globalKeys.put("AssignmentEntity.LDS_DISPATCHERN_TEL", new NoteData(DeliveryNoteData.DISPATCHER_TEL));
		globalKeys.put("AssignmentEntity.LDS_DISPATCHER_ID", new NoteData(DeliveryNoteData.DISPATCHER_ID));

		globalKeys.put("AssignmentEntity.LDS_DRIVER_NAME", new NoteData(DeliveryNoteData.DRIVER_NAME));
		globalKeys.put("AssignmentEntity.LDS_DRIVER_CARD_NO", new NoteData(DeliveryNoteData.DRIVER_CARD_NO));
		globalKeys.put("AssignmentEntity.LDS_DRIVER_ID", new NoteData(DeliveryNoteData.DRIVER_ID));

		//--- Footer ---//
		globalKeys.put("FooterTitle", new NoteData(DeliveryNoteData.FOOTER_TITLE));
		globalKeys.put("FooterText", new NoteData(DeliveryNoteData.FOOTER_TEXT));
		globalKeys.put("CalibrationRecordIntro", new NoteData(DeliveryNoteData.EICHBELEG_TEXT));

		GLOBAL_KEYS = Collections.unmodifiableMap(globalKeys);
	}

	protected final DeliveryNotePdfDataStore store;
	protected final GenericBlobServiceLocalInterface blobService;
	protected final Map<String, Object> keys;

	public DeliveryNotePdfDataProvider(DeliveryNotePdfDataStore store, GenericBlobServiceLocalInterface blobService, SareaEntity sarea) {
		this.store = store;
		this.blobService = blobService;
		this.keys = new HashMap<String, Object>(GLOBAL_KEYS);

		addLabelToStore(DeliveryNoteLabel.FIELDS_REMARKS);
		addLabelToStore(DeliveryNoteLabel.SAP_CONTRACT_NO);

		initVisibilitySettings(sarea);
		initImageLogoSettings();

		initDeliveryNoteDependentSettings();
	}

	public DocumentValue getInclude(Path path) {
		try {
			InputStream stream = getClass().getClassLoader().getResourceAsStream(path.toString());
			return DocumentValue.Load(stream);
		}
		catch (IOException exc) {
			throw new LogonException(exc, Reason.RES_ERROR_IMPL);
		}
	}

	public Value getValue(Path path) {
		String key = path.toString();

		Object mappedKey = keys.get(key);
		if (mappedKey == null) {
			return null;
		}

		if (mappedKey instanceof TagData) {
			return ((TagData) mappedKey).toValue(this);
		}
		return new TextValue(mappedKey.toString());
	}

	public int count(Path path) {
		int max = 0;
		for (Map.Entry<String, Object> entry : keys.entrySet()) {
			Path key = parsePath(entry.getKey());
			if (path.isRootOf(key, false) && key.item(path.size() - 1).hasIndex()) {
				max = Math.max(key.item(path.size() - 1).getIndex() + 1, max);
			}
		}

		return max;
	}

	protected void initVisibilitySettings(SareaEntity sarea) {
		keys.put("SareaEntity.EXP_SHOW_ISSUE_DAT_LOC_FLAG", sarea.getExpShowIssueDatLocFlag());
		keys.put("SareaEntity.DN_SHOW_TRAILER_FLAG", sarea.getDnShowTrailerFlag());
		keys.put("SareaEntity.DOCUMENT_DISTANCE_FLAG", sarea.getDocumentDistanceFlag());
		keys.put("SareaEntity.DOCUMENT_DEL_TIME_WINDOW_FLAG", sarea.getDocumentDelTimeWindowFlag());
		keys.put("SareaEntity.DN_SHOW_SEALS_INFO_FLAG", sarea.getDnShowSealsInfoFlag());
		keys.put("SareaEntity.DN_SHOW_CARRIER_CRM_FLAG", sarea.getDnShowCarrierCrmFlag());
		keys.put("SareaEntity.DN_SHOW_SILO_NO_FLAG", sarea.getDnShowSiloNoFlag());
		keys.put("SareaEntity.DN_SHOW_PLANT_CYCLE_FLAG", sarea.getDnShowPlantCycleFlag());
		keys.put("SareaEntity.DN_SHOW_DISPATCHER_DRIVER_FLAG", sarea.getDnShowDispatcherDriverFlag());
	}

	protected void initImageLogoSettings() {
		// NOTE, some of these images are not really order dependent, but it made the code a bit easier
		// if we loaded them dynamically here - let's hope this is not a performance killer or anything,
		// but that would mean that they are constantly generating delivery notes

		byte[] image;

		//--- base images ---//
		image = loadImageBytes(store.getData(DeliveryNoteData.CRH_LOGO));
		keys.put("SorgLogo", new ImageData(image));

		if (StringUtils.isEmpty(store.getData(DeliveryNoteData.CROSSBOUNDARY))
				&& StringUtils.isEmpty(store.getData(DeliveryNoteData.NHM))
				&& StringUtils.isNotEmpty(store.getData(DeliveryNoteData.BANNER_IMG_NAME))
				&& !Boolean.valueOf(store.getData(DeliveryNoteData.IS_CASH_SALES))) {

			image = loadImageBytes(store.getData(DeliveryNoteData.BANNER_IMG_NAME));
			keys.put("BannerImage", new ImageData(image));
		}

		image = store.getByteStream(DeliveryNoteData.DRIVER_SIG_BYTE_STREAM);
		keys.put("DriverSignatureImage", new ImageData(image));
		keys.put("DriverSignatureLabel", new LabelData(DeliveryNoteLabel.SIGNATURE_DRIVER));

		image = store.getByteStream(DeliveryNoteData.CUST_SIG_BYTE_STREAM);
		keys.put("CustomerSignatureImage", new ImageData(image));
		keys.put("CustomerSignatureLabel", new LabelData(DeliveryNoteLabel.SIGNATURE_CUSTOMER));
	}

	protected void initDeliveryNoteDependentSettings() {
		boolean isExportInvoice = Boolean.valueOf(store.getData(DeliveryNoteData.IS_EXPORT_INVOICE));

		initCalibrationSettings();
		initOrderItemSettings();
		initAdvanceInvoiceSettings(isExportInvoice);
		initMaterialImageSettings();
		initCarrierSettings();
		
		keys.put("dnType.IS_EXPORT_INVOICE", isExportInvoice);
		keys.put("dnType.IS_CASH_SALES", Boolean.valueOf(store.getData(DeliveryNoteData.IS_CASH_SALES)));
		
		if (isExportInvoice) {
			keys.put("VersionOrIssueDateLabel", new LabelData(DeliveryNoteLabel.EXPORT_INVOICE_ISSUE_DATE));
			keys.put("IssueLocationLabel", new LabelData(DeliveryNoteLabel.EXPORT_INVOICE_ISSUE_LOCATION));
			keys.put("dnType.IS_CROSSBOUNDARY", 
					!StringUtils.isEmpty(store.getData(DeliveryNoteData.CROSSBOUNDARY_EXPORT_INVOICE_LINE1)) || !StringUtils.isEmpty(store.getData(DeliveryNoteData.CROSSBOUNDARY_EXPORT_INVOICE_LINE2)));
		}
		else {
			keys.put("dnType.IS_CROSSBOUNDARY", !StringUtils.isEmpty(store.getData(DeliveryNoteData.CROSSBOUNDARY)));
			keys.put("VersionOrIssueDateLabel", new LabelData(DeliveryNoteLabel.VERSION));
		}
	}

	private void initCalibrationSettings() {
		List<EichbelegDTO> eichbelegDTOs = store.getDTOList(EichbelegDTO.class);
		if (eichbelegDTOs.size() > 0) {
			keys.put("CalibrationRecordTare", eichbelegDTOs.get(0).getText());
			if (eichbelegDTOs.size() > 1) {
				keys.put("CalibrationRecordGross", eichbelegDTOs.get(1).getText());
			}
		}
	}

	private void initAdvanceInvoiceSettings(boolean isExportInvoice) {
		String advanceInvoiceNumbers = store.getData(DeliveryNoteData.EXPORT_INVOICE_ADVANCE_PAYMENT_INVOICE_NUMBER);
		if (isExportInvoice && !advanceInvoiceNumbers.isEmpty()) {
			keys.put("dnType.IS_ADVANCE_INVOICE", Boolean.TRUE);

			// NOTE: this code is identical to what is done in DeliveryNoteShippingPart - we copy it here
			// to avoid changing the main code right before the release
			advanceInvoiceNumbers = StringUtils.abbreviate(advanceInvoiceNumbers,
														   Constant.EXP_INV_ADVANCE_INVOICE_NUMBER_MAX_WIDTH);
			store.addData(DeliveryNoteData.EXPORT_INVOICE_ADVANCE_PAYMENT_INVOICE_NUMBER, advanceInvoiceNumbers);
		}
		else {
			keys.put("dnType.IS_ADVANCE_INVOICE", Boolean.FALSE);
		}
	}

	private void initOrderItemSettings() {
		Boolean hasReturn = Boolean.valueOf(store.getData(DeliveryNoteData.RETURN_DATA_FLAG));
		Boolean isBulk = Boolean.valueOf(store.getData(DeliveryNoteData.IS_BULK));
		Boolean isBulkAndService = Boolean.valueOf(store.getData(DeliveryNoteData.IS_BULK_AND_SERVICE));

		keys.put("dnIsBulkOrder", isBulk || isBulkAndService);

		//--- initialize labels ---//
		addLabelKeyAndStore("MaterialsLabel", DeliveryNoteLabel.MATERIAL);
		addLabelKeyAndStore("NetWLabel", DeliveryNoteLabel.WEIGHTS_NET);

		if (isBulk || isBulkAndService) {
			addLabelKeyAndStore("GrossWLabel", DeliveryNoteLabel.WEIGHTS_GROSS);
			addLabelKeyAndStore("TareWLabel", DeliveryNoteLabel.WEIGHTS_TARE);
			addLabelKeyAndStore("ReturnWLabel", DeliveryNoteLabel.WEIGHTS_RETURN);
		}
		else {
			addLabelKeyAndStore("OrderedQtyLabel", DeliveryNoteLabel.QUANTITY_ORDERED);
			addLabelKeyAndStore("DeliveredQtyLabel", DeliveryNoteLabel.QUANTITY_DELIVERED);
		}

		//--- initialize data ---//

		boolean isBulkItemsHaveReturnQty = false;

		// the Cash Sales record is "massaged" in the DeliveryNotePdfDataStore to look like an
		// OrderItemDTO object. However, it is stored in another element in the store, so we
		// iterate over our objects list and then "append" the cash sales record at the end
		// (i.e. treating it as if it were part of the original list by using the incremented
		//  counter value)
		int i = 0;

		List<OrderItemDTO> orderItemDTOs = store.getDTOList(OrderItemDTO.class);
		for (OrderItemDTO dto: orderItemDTOs) {
			if (dto.isAdditionalMaterialInfo()) {
				// for Aspose the additional material information is stored as part of
				// the original order item dto (i.e. mix design and production plant)
				//
				// the Aspose code here concats the additional material information
				// into the correct format since we have problems with the Aspose
				// parsing template library not properly outputting our carriage returns
				continue;
			}

			keys.put(String.format("OrderItem[%s].MaterialDescriptionInfos", i), getMaterialDescriptionInfos(dto));
			keys.put(String.format("OrderItem[%s].MaterialHierarchyInfos", i), getMaterialHierarchyInfos(dto));

			if (isBulkPkgOrderItem(dto)) {
				isBulkItemsHaveReturnQty = addBulkItemSpecificData(dto, i, hasReturn) || isBulkItemsHaveReturnQty;
			}
			else if (isBagPkgOrderItem(dto)) {
				addBagItemSpecificData(dto, i);
			}
			// else: we are dealing with a Cash Sales bean record which has not weight information

			i++;
		}

		// addBulkItemSpecificData() will only return true if the item in question has
		// a valid return quantity. this means that if *any* item has a return quantity
		// we need to use the correct layout with the return quantity column - all
		// other items without a return quantity simply display "-". if no bulk item
		// has a return quantity, then select the table without the return column
		// (see logon-pdf-generator/etc/templates/BulkItems.docx)
		if ((isBulk || isBulkAndService) && isBulkItemsHaveReturnQty) {
			keys.put("BulkItemsHaveReturnQty", isBulkItemsHaveReturnQty);
		}

		Object cashSalesTotalRecord = store.getObjectData(DeliveryNoteData.CASH_SALES_TOTAL_RECORD);
		if (cashSalesTotalRecord != null) {
			// cash sales total information does not display mix-design infos, so we directly
			// access the required values (i.e. getMaterialName, getMaterialDescription)
			MaterialTotalsTextDTO dto = (MaterialTotalsTextDTO) cashSalesTotalRecord;
			keys.put("TotalPriceNet", dto.getNetPrice());
			keys.put("TotalPriceVat", dto.getVat());
			keys.put("TotalPriceGross", dto.getGrossPrice());
		}
	}

	private String getMaterialDescriptionInfos(OrderItemDTO dto) {
		StringBuilder buf = new StringBuilder(dto.getMaterialName());
		if (StringUtils.isNotEmpty(dto.getSapNr())) {
			buf.append(PDF_NEWLINE);
			buf.append(dto.getSapNr());
		}

		return buf.toString();
	}

	private String getMaterialHierarchyInfos(OrderItemDTO dto) {
		StringBuilder buf = new StringBuilder(dto.getMaterialDescription());

		char separator = PDF_NEWLINE;
		if (StringUtils.isNotEmpty(dto.getMaterialMixDesignPlantName())) {
			buf.append(separator);
			buf.append(dto.getMaterialMixDesignPlantName());
			separator = ' ';
		}

		if (StringUtils.isNotEmpty(dto.getMaterialMixDesign())) {
			buf.append(separator);
			buf.append(dto.getMaterialMixDesign());
		}

		return buf.toString();
	}

	private boolean isBulkPkgOrderItem(OrderItemDTO dto) {
		Long pkgType = dto.getPkgType();
		return pkgType != null &&
				(Constant.PACKAGE_TYPE_BULK_DUMPED == pkgType ||
						Constant.PACKAGE_TYPE_BULK_POWDER == pkgType);
	}

	private boolean isBagPkgOrderItem(OrderItemDTO dto) {
		Long pkgType = dto.getPkgType();
		return pkgType != null && Constant.PACKAGE_TYPE_BBAG == pkgType;
	}

	private boolean addBulkItemSpecificData(OrderItemDTO dto, int i, boolean hasReturn) {
		boolean isValidReturnPresent = false;

		keys.put(String.format("OrderItem[%s].grossW", i),
				 OrderItemDTO.getValueOrEmptyStringWeight(dto.getGrossWeight(), Constant.UOM_TONS_TXT));

		keys.put(String.format("OrderItem[%s].tareW", i),
				 OrderItemDTO.getValueOrEmptyStringWeight(dto.getTareWeight(), Constant.UOM_TONS_TXT));

		String netW = OrderItemDTO.getValueOrEmptyStringWeight(dto.getNetWeight(), dto.getBulkUom());
		keys.put(String.format("OrderItem[%s].netW", i), netW);

		String returnW = "-";
		if (hasReturn && dto.getReturn() != null) {
			isValidReturnPresent = true;
			returnW = OrderItemDTO.getValueOrEmptyStringWeight(dto.getReturn(), dto.getBulkUom());
		}

		keys.put(String.format("OrderItem[%s].returnW", i), returnW);

		return isValidReturnPresent;
	}

	private void addBagItemSpecificData(OrderItemDTO dto, int i) {
		keys.put(String.format("OrderItem[%s].orderedQuantity", i),
				 OrderItemDTO.getValueOrEmptyStringWeight(dto.getOrdered(), dto.getOrderedUom()));

		keys.put(String.format("OrderItem[%s].deliveredQuantity", i),
				 OrderItemDTO.getValueOrEmptyStringWeight(dto.getDelivered(), dto.getDeliveredUom()));

		keys.put(String.format("OrderItem[%s].netW", i),
				 OrderItemDTO.getValueOrEmptyStringWeight(dto.getNetBag(), dto.getNetBagUom()));
	}

	private void initMaterialImageSettings() {
		// the logic for this code was taken from LogoPrintSection

		MatPlantEntity matPlantEntity = store.getMatPlantEntity();

		//--- some sane defaults ---//
		keys.put("Show_MatLogoLayout1", Boolean.FALSE);
		keys.put("Show_MatLogoLayout2", Boolean.FALSE);
		keys.put("Show_MatLogoLayout3", Boolean.FALSE);

		//--- determine the main layout to show ---//
		if (matPlantEntity == null) {
			// do not display any material logos
			return;
		}

		if (matPlantEntity.getPrioOverSapMatNormFlag()) {
			setMaterialLogoImagesForLayout1(matPlantEntity);
		}
		else {
			setMaterialLogoImagesForLayouts2Or3(matPlantEntity);
		}
	}

	private void setMaterialLogoImagesForLayout1(MatPlantEntity matPlantEntity) {
		keys.put("Show_MatLogoLayout1", Boolean.TRUE);

		int loadedImagesCnt = 0;

		// CE logo
		if (setMateriaLogoImage(matPlantEntity.getLogo1(),
								1,
								matPlantEntity.getLogo1Z2(),
								matPlantEntity.getLogo1Z1(),
								matPlantEntity.getNationalNorm())) {
			loadedImagesCnt++;
		}

		// Generic
		if (setGenericMaterialLogoImage(matPlantEntity)) {
			loadedImagesCnt++;
		}

		// CE logo
		if (setMateriaLogoImage(matPlantEntity.getLogo2(),
								2,
								matPlantEntity.getLogo2Z1(),
								matPlantEntity.getLogo2Z2(),
								matPlantEntity.getLogo2Z3())) {
			loadedImagesCnt++;
		}

		// U logo
		if (setMateriaLogoImage(matPlantEntity.getLogo3(),
								3,
								matPlantEntity.getLogo3Z1(),
								matPlantEntity.getLogo3Z2(),
								matPlantEntity.getLogo3Z3())) {
			loadedImagesCnt++;
		}

		// VDZ logo
		if (setMateriaLogoImage(matPlantEntity.getLogo4(),
								4,
								matPlantEntity.getLogo4Z1(),
								matPlantEntity.getLogo4Z2(),
								matPlantEntity.getLogo4Z3())) {
			loadedImagesCnt++;
		}

		// MPA Stuttgart
		if (setMateriaLogoImage(matPlantEntity.getLogo5(),
								5,
								matPlantEntity.getLogo5Z1(),
								matPlantEntity.getLogo5Z2(),
								matPlantEntity.getLogo5Z3())) {
			loadedImagesCnt++;
		}

		// MPA NRW
		if (setMateriaLogoImage(matPlantEntity.getLogo6(),
								6,
								matPlantEntity.getLogo6Z1(),
								matPlantEntity.getLogo6Z2(),
								matPlantEntity.getLogo6Z3())) {
			loadedImagesCnt++;
		}

		if (loadedImagesCnt > 0) {
			keys.put("Show_MatLogoLayout1", Boolean.TRUE);
		}
	}

	private void setMaterialLogoImagesForLayouts2Or3(MatPlantEntity matPlantEntity) {
		List<SapMatPlantNormEntity> sapMatPlantNormList = store.getMatPlantNormList();

		if (sapMatPlantNormList != null && !sapMatPlantNormList.isEmpty()) {
			boolean isGenericImageSet = false;
			boolean expShowZcode12Flag =
					Boolean.valueOf(store.getData(DeliveryNoteData.EXPORT_INVOICE_SHOW_ZCODE1_2_FLAG));

			int loadedImagesCnt = 0;

			for (int i = 0, imagePosition = 1; i < sapMatPlantNormList.size(); imagePosition++) {
				if (imagePosition == 2 && setGenericMaterialLogoImage(matPlantEntity)) {
					// the generic logo goes in the second position
					isGenericImageSet = true;
					continue;
				}
				else {
					SapMatPlantNormEntity normEntity = sapMatPlantNormList.get(i);
					String logoName = normEntity.getDeliveryNoteLogoImageName();

					String text1, text2, text3;
					if (expShowZcode12Flag) {
						text1 = normEntity.getZcode1() + " " + normEntity.getZcode2();
						text2 = normEntity.getText1();
						text3 = normEntity.getText2();
					}
					else {
						text1 = normEntity.getText1();
						text2 = normEntity.getText2();
						text3 = normEntity.getText3();
					}

					if (setMateriaLogoImage(logoName,
											imagePosition,
											text1,
											text2,
											text3)) {
						loadedImagesCnt++;
					}

					i++;
				}
			}

			if (loadedImagesCnt > 0) {
				if (isGenericImageSet) {
					keys.put("Show_MatLogoLayout2", Boolean.TRUE);
				}
				else {
					keys.put("Show_MatLogoLayout3", Boolean.TRUE);
				}
			}
		}
	}
	
	private boolean setMateriaLogoImage(String logoName, int position, String line1, String line2, String line3) {
		if (StringUtils.isNotEmpty(logoName)) {
			byte[] image = loadImageBytes(logoName);
			if (image != null && image.length > 0) {
				String indexedLogoName = String.format("MatLogoImage%s", position);
				keys.put(indexedLogoName, new ImageData(image));
				keys.put(String.format("%s_Line1", indexedLogoName), line1);
				keys.put(String.format("%s_Line2", indexedLogoName), line2);
				keys.put(String.format("%s_Line3", indexedLogoName), line3);
				return true;
			}
		}
		return false;
	}

	private boolean setGenericMaterialLogoImage(MatPlantEntity matPlantEntity) {
		if (matPlantEntity.getLogoGeneric() != null) {
			byte[] image = loadImageBytes(matPlantEntity.getLogoGeneric());
			if (image != null && image.length > 0) {
				keys.put("MatLogoImageGeneric", new ImageData(image));
				return true;
			}
		}
		return false;
	}

	private byte[] loadImageBytes(String logoImageName) {
		byte[] image = null;

		if (StringUtils.isNotEmpty(logoImageName)) {
			BlobDTO imageDTO = blobService.fetchBlobWithStringKeyValue("ImageEntity", "imageName", logoImageName, true);
			if (imageDTO != null) {
				PDFUtils.checkImageIntegrity(imageDTO, logoImageName);
				image = imageDTO.getStream();
			}
		}

		return image;
	}

	private Path parsePath(String path) {
		try {
			return Path.parse(path);
		}
		catch (ParsingException exc) {
			throw new IllegalArgumentException(exc);
		}
	}

	private void addLabelKeyAndStore(String key, DeliveryNoteLabel label) {
		addLabelToStore(label);
		keys.put(key, new LabelData(label));
	}

	private void addLabelToStore(DeliveryNoteLabel label) {
		store.addLabel(label, Message.lookup(store.getLocale(), label.getMessageId()));
	}
	
	private void initCarrierSettings() {
		// [[
		Object[][] params = {};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initCarrierSettings", params);
		}
		// ]]
		try {

			String plateNr = store.getData(DeliveryNoteData.TRUCK_PLATE_NR);
			if (!plateNr.isEmpty()) {
				keys.put("WagonOrTruckPlateNoLabel",
						new LabelData(DeliveryNoteLabel.TRUCK_PLATE_NO));
				keys.put("WagonOrTruckPlateNoSeparator", new LabelData(
						DeliveryNoteLabel.SEPARATOR_WAGON_OR_TRUCK_PLATE_NO));
				keys.put("WagonOrTruckPlateNo", new NoteData(DeliveryNoteData.TRUCK_PLATE_NR));
			}

			String trailerPlateNr = store.getData(DeliveryNoteData.TRAILER_PLATE_NR);
			if (!trailerPlateNr.isEmpty()) {
				keys.put("ContainerOrTrailerPlateNoLabel", new LabelData(
						DeliveryNoteLabel.TRAILER_PLATE_NO));
				keys.put("ContainerOrTrailerPlateNoSeparator", new LabelData(
						DeliveryNoteLabel.SEPARATOR_CONTAINER_OR_TRAILER_PLATE_NO));
				keys.put("ContainerOrTrailerPlateNo", new NoteData(
						DeliveryNoteData.TRAILER_PLATE_NR));
			}

			String wagonNr = store.getData(DeliveryNoteData.WAGON_NR);
			if (!wagonNr.isEmpty()) {
				keys.put("WagonOrTruckPlateNoLabel", new LabelData(DeliveryNoteLabel.WAGON_NO));
				keys.put("WagonOrTruckPlateNoSeparator", new LabelData(
						DeliveryNoteLabel.SEPARATOR_WAGON_OR_TRUCK_PLATE_NO));
				keys.put("WagonOrTruckPlateNo", new NoteData(DeliveryNoteData.WAGON_NR));
			}

			String containerNr = store.getData(DeliveryNoteData.CONTAINER_NR);
			if (!containerNr.isEmpty()) {
				keys.put("ContainerOrTrailerPlateNoLabel", new LabelData(
						DeliveryNoteLabel.CONTAINER_NO));
				keys.put("ContainerOrTrailerPlateNoSeparator", new LabelData(
						DeliveryNoteLabel.SEPARATOR_CONTAINER_OR_TRAILER_PLATE_NO));
				keys.put("ContainerOrTrailerPlateNo", new NoteData(DeliveryNoteData.CONTAINER_NR));
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
	}
