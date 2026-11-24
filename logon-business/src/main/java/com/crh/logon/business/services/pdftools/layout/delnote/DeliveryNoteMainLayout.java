/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.delnote;

import java.util.ArrayList;
import java.util.List;

import com.crh.logon.business.services.localinterfaces.GenericBlobServiceLocalInterface;
import com.crh.logon.business.services.pdftools.layout.utils.DeliveryNoteLinesSectionUtil;
import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import ch.nevis.jcan.print.fonts.Font;

import com.crh.logon.business.services.pdftools.layout.AddressPart;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteData;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteLabel;
import com.crh.logon.business.services.pdftools.layout.EichbelegDTO;
import com.crh.logon.business.services.pdftools.layout.GlyphWidthProvider;
import com.crh.logon.business.services.pdftools.layout.JcanPdfRenderer;
import com.crh.logon.business.services.pdftools.layout.LineBreaker;
import com.crh.logon.business.services.pdftools.layout.LogoPrintSection;
import com.crh.logon.business.services.pdftools.layout.MaterialTotalsTextDTO;
import com.crh.logon.business.services.pdftools.layout.OrderItemDTO;
import com.crh.logon.business.services.pdftools.layout.PdfLayoutGenerationTool;
import com.crh.logon.business.services.pdftools.layout.PdfTableUtil;
import com.crh.logon.business.services.pdftools.layout.TextListPart;
import com.crh.logon.business.services.pdftools.layout.TitlePart;
import com.crh.logon.pdftool.PdfDocumentWrapper;
import com.crh.logon.pdftool.objects.PdfObject;
import com.crh.logon.pdftool.objects.PdfRowBuilder;
import com.crh.logon.pdftool.objects.PdfRowBuilder.PdfRow;
import com.crh.logon.pdftool.objects.PdfTable;
import com.crh.logon.pdftool.objects.SimplePdfText;
import com.crh.logon.pdftool.renderer.AbstractPdfRenderer;
import com.crh.logon.pdftool.renderer.PdfFont;
import com.crh.logon.pdftool.renderer.PdfFontStore;
import com.crh.logon.pdftool.store.AbstractPdfDataStore;

/**
 * Main layout for Delivery note.
 * @author AdNovum Informatik AG
 */
public class DeliveryNoteMainLayout {
	
	/**
	 * Extra space below the address part
	 */
	private static final int EXTRA_SPACE_BELOW_ADDRESS_PART_IN_POINTS = 4;

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(DeliveryNoteMainLayout.class);
	
	private static String COLON_SPACE = PdfLayoutGenerationTool.COLON_SPACE;
	
	private static final int STATION_TITLE_ROW_LENGTH = 110;
	private static final int STATION_TITLE_MAX_ROWS = 3;
	
	// column withs
	private static final int MATERIAL_WIDTH = 180;
	private static final int WEIGHTS_GROSS_WIDTH = 40;
	private static final int WEIGHTS_TARE_WIDTH = 40;
	private static final int WEIGHTS_RETURN_WIDTH = 40;
	private static final int WEIGHTS_NET_BULK_WIDTH = 40;
	private static final int WEIGHTS_NET_BAG_WIDTH = 55;
	private static final int QUANTITY_ORDERED_WIDTH = 55;
	private static final int QUANTITY_DELIVERED_WIDTH = 55;
	private static final int TOTAL_LINE_NET_GROSS_WIDTH = 180;
	private static final int TOTAL_LINE_VAT_WIDTH = 125;
	private static final int ROW_HEIGHT_11 = 11;
	
	/**
	 * Constant for height of the 12pt font
	 */
	private static final int FONT_SIZE_12PT = 12;
	
	//With MATERIAL_HIERARCHY_BULK_LONG_WIDTH (215), there is enough (120px) space for WEIGHTS_GROSS_WIDTH(40) + WEIGHTS_TARE_WIDTH(40) + WEIGHTS_NET_BULK_WIDTH(40)
	private static final int MATERIAL_HIERARCHY_LONG_WIDTH = 215;
	//Extra column WEIGHTS_RETURN_WIDTH(40), space is decreased by this amount
	private static final int MATERIAL_HIERARCHY_SHORT_WIDTH = MATERIAL_HIERARCHY_LONG_WIDTH - WEIGHTS_NET_BULK_WIDTH;

	// default fonts
	private static final PdfFont helvetica_10 = new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_10);
	private static final PdfFont helvetica_9 = new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_9);
	private static final PdfFont helvetica_7 = new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_7);

	/**
	 * Returns the highest value from the list of values
	 * @param values list of double values
	 * @return the maximum of the provided values
	 */
	private static double max(double... values) {
		double result = Double.MIN_VALUE;
		for (double v: values) {
			result = Math.max(result, v);
		}
		return result;
	}
	
	public static boolean generateDeliveryNotePDF(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store, AbstractPdfRenderer renderer, GenericBlobServiceLocalInterface blobService) {
		// [[
		boolean result = false;
		Object[][] params = {{"store", store},
							{"renderer", renderer},
							{"blobService", blobService}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DeliveryNoteMainLayout.class, "generateDeliveryNotePDF", params);
		}
		// ]]
		try {
			PdfDocumentWrapper document = new PdfDocumentWrapper();
			List<PdfObject> content = document.getContent();
			
			// lines under addres block header, material header, remarks header
			DeliveryNoteLinePrintSection underLines = new DeliveryNoteLinePrintSection(document);
			// add underlines
			renderer.addCustomSection(underLines);
			
			// add logo custom section to renderer
			LogoPrintSection logoSection = new LogoPrintSection(document, store);
			// add logo section
			renderer.addCustomSection(logoSection);
			
			// add Crh logo image to header
			String logoImageName = store.getData(DeliveryNoteData.CRH_LOGO);
			if (!logoImageName.isEmpty()) {
				PdfLayoutGenerationTool.showImage(blobService, (JcanPdfRenderer) renderer, logoImageName, PdfLayoutGenerationTool.LOGO_PADDING_LEFT, document.getHeight() - PdfLayoutGenerationTool.LOGO_PADDING_TOP, PdfLayoutGenerationTool.LOGO_WIDTH, PdfLayoutGenerationTool.LOGO_HEIGHT);
			}
			
			final boolean isExportInvoice = Boolean.valueOf(store.getData(DeliveryNoteData.IS_EXPORT_INVOICE));
			
			// address part
			AddressPart addressPart = new AddressPart(store);
			// add address part 
			int addressPartHeight = addressPart.getHeightInPoints() + EXTRA_SPACE_BELOW_ADDRESS_PART_IN_POINTS;
			
			content.add(new PdfRowBuilder(1, addressPartHeight)
					.addElement(addressPart)
					.build());
			// title part
			int heightOfTitlePart;
			if (isExportInvoice) {
				heightOfTitlePart = 35;
			}
			else {
				heightOfTitlePart = 26;
			}
			content.add(new PdfRowBuilder(1, heightOfTitlePart)
					.addElement(new TitlePart(store, 10, heightOfTitlePart)).build());
			// shipping part
			DeliveryNoteShippingPart shippingPart = new DeliveryNoteShippingPart(store);
			content.add(new PdfRowBuilder(1, shippingPart.getNumOfRows() * shippingPart.defaultRowHeight)
					.addElement(shippingPart).build());
			// add the six addresses				
			ArrayList<String> soldToAddresses = new ArrayList<String>();
			ArrayList<String> shiptoAddresses = new ArrayList<String>();
			ArrayList<String> commCarrierAddresses = new ArrayList<String>();
			ArrayList<String> execCarrierAddresses = new ArrayList<String>();
			ArrayList<String> payerAddresses = new ArrayList<String>();
			ArrayList<String> billToAddresses = new ArrayList<String>();
			
			// sold to address list
			PdfLayoutGenerationTool.addToListIfNotEmpty(soldToAddresses, 
					store.getData(DeliveryNoteData.SOLDTO_ADDRESS1), 
					store.getData(DeliveryNoteData.SOLDTO_ADDRESS2), 
					store.getData(DeliveryNoteData.SOLDTO_ADDRESS3), 
					store.getData(DeliveryNoteData.SOLDTO_PHONE), 
					store.getData(DeliveryNoteData.SOLDTO_VATREG));
			
			// Cash Sales Invoice Form
			final String taxNumber1 = store.getData(DeliveryNoteData.CASH_SALES_TAX_NUMBER1);
			if (!StringUtils.isEmpty(taxNumber1)) {
				soldToAddresses.add(taxNumber1);
			}
			
			// ship to address list
			PdfLayoutGenerationTool.addToListIfNotEmpty(
					shiptoAddresses, 
					store.getData(DeliveryNoteData.SHIPTO_ADDRESS1), 
					store.getData(DeliveryNoteData.SHIPTO_ADDRESS2), 
					store.getData(DeliveryNoteData.SHIPTO_ADDRESS3));
			
			// ShipToVAT contains the ShipTo Contact information (if available), otherwise it contains
			// the VAT number or could be empty
			String shipToVAT = store.getData(DeliveryNoteData.SHIPTO_VAT);
			
			if (shipToVAT != null && shipToVAT.length() > 0) {
			
				final Font font = JcanPdfRenderer.getFont(helvetica_9);
				
				// ShipTo Contact information could be too long to fit into one line, break it to multiple
				// lines
				List<String> lines = LineBreaker.fitToWidth(160, shipToVAT, null, new GlyphWidthProvider() {
					
					@Override
					public double glyphWidth(char glyph) {
						
						// return the width of the specified glyph (character)
						return font.getWidth(glyph);
					}
				});
			
				// add the VAT/ShipTo lines to the list
				shiptoAddresses.addAll(lines);
			}
			
			// commercial carrier address list
			PdfLayoutGenerationTool.addToListIfNotEmpty(
					commCarrierAddresses, 
					store.getData(DeliveryNoteData.COMM_CARRIER_ADDRESS1), 
					store.getData(DeliveryNoteData.COMM_CARRIER_ADDRESS2), 
					store.getData(DeliveryNoteData.COMM_CARRIER_ADDRESS3), 
					store.getData(DeliveryNoteData.COMM_CARRIER_PHONE), 
					store.getData(DeliveryNoteData.COMM_CARRIER_PIVA_IA));
			
			// executing carrier address list
			PdfLayoutGenerationTool.addToListIfNotEmpty(execCarrierAddresses, 
					store.getData(DeliveryNoteData.EXEC_CARRIER_ADDRESS1), 
					store.getData(DeliveryNoteData.EXEC_CARRIER_ADDRESS2), 
					store.getData(DeliveryNoteData.EXEC_CARRIER_ADDRESS3), 
					store.getData(DeliveryNoteData.EXEC_CARRIER_PHONE), 
					store.getData(DeliveryNoteData.EXEC_CARRIER_PIVA_IA));
			
			// payer address list
			PdfLayoutGenerationTool.addToListIfNotEmpty(payerAddresses, 
					store.getData(DeliveryNoteData.PAYER_ADDRESS1), 
					store.getData(DeliveryNoteData.PAYER_ADDRESS2), 
					store.getData(DeliveryNoteData.PAYER_ADDRESS3));
			
			// bill to address list
			PdfLayoutGenerationTool.addToListIfNotEmpty(billToAddresses, 
					store.getData(DeliveryNoteData.BILLTO_ADDRESS1), 
					store.getData(DeliveryNoteData.BILLTO_ADDRESS2), 
					store.getData(DeliveryNoteData.BILLTO_ADDRESS3));
			
			boolean showPayer = !store.getData(DeliveryNoteData.BILLTO_SAP).equals(store.getData(DeliveryNoteData.PAYER_SAP));
			boolean showCommCarrier = !Boolean.valueOf(store.getData(DeliveryNoteData.HIDE_COMM_CARRIER));
			boolean showExecCarrier = !Boolean.valueOf(store.getData(DeliveryNoteData.HIDE_EXEC_CARRIER));
			
			// Payer TextList
			TextListPart payerTextList = new TextListPart();
			if (showPayer) {
				payerTextList = new TextListPart(160, ROW_HEIGHT_11, helvetica_9,
						store.getLabel(DeliveryNoteLabel.ADDRESS_PAYER) + COLON_SPACE, 
						store.getData(DeliveryNoteData.PAYER_SAP), 
						payerAddresses);
			}
			
			// Commercial Carrier TextList
			TextListPart commercialCarrierTextList = new TextListPart();
			if (showCommCarrier) {
				commercialCarrierTextList = new TextListPart(160, ROW_HEIGHT_11, helvetica_9,
						store.getLabel(DeliveryNoteLabel.ADDRESS_COMM_CARRIER) + COLON_SPACE, 
						store.getData(DeliveryNoteData.COMM_CARRIER_SAP), 
						commCarrierAddresses);
			}
			
			// Executing Carrier TextList
			TextListPart executingCarrierTextList = new TextListPart();
			if (showExecCarrier) {
				executingCarrierTextList = new TextListPart(160, ROW_HEIGHT_11, helvetica_9,
						store.getLabel(DeliveryNoteLabel.ADDRESS_EXEC_CARRIER) + COLON_SPACE, 
						store.getData(DeliveryNoteData.EXEC_CARRIER_SAP), 
						execCarrierAddresses);
			}
			
			// ship to TextList
			TextListPart shipToTextList = new TextListPart(160, ROW_HEIGHT_11, helvetica_9,
					store.getLabel(DeliveryNoteLabel.ADDRESS_SHIPTO) + COLON_SPACE,
					store.getData(DeliveryNoteData.SHIPTO_SAP), 
					shiptoAddresses);
			
			// bill To TextList
			TextListPart billToTextList = new TextListPart(160, ROW_HEIGHT_11, helvetica_9,
					store.getLabel(DeliveryNoteLabel.ADDRESS_BILLTO) + COLON_SPACE,
					store.getData(DeliveryNoteData.BILLTO_SAP), 
					billToAddresses);
			
			// Sold To TextList
			TextListPart soldToTextList = new TextListPart(160, ROW_HEIGHT_11, helvetica_9,
					store.getLabel(DeliveryNoteLabel.ADDRESS_SOLDTO) + COLON_SPACE,
					store.getData(DeliveryNoteData.SOLDTO_SAP), soldToAddresses);
			
			// calculate the height of row1
			double shipToHeight = shipToTextList.getListHeight();
			double billToHeight = billToTextList.getListHeight();
			double payerHeight = payerTextList.getListHeight();
			int row1Height = (int) Math.round(max(shipToHeight, billToHeight, payerHeight));
			
			// row1
			PdfRow row1 = new PdfRowBuilder(3, row1Height, 10, 0)
					// ship to
					.addElement(shipToTextList)
					// bill to
					.addElement(billToTextList)
					// payer
					.addElement(payerTextList)
					.build();
			
			// calculate the height of row2
			double soldToHeight = soldToTextList.getListHeight();
			double commercialCarrierHeight = commercialCarrierTextList.getListHeight();
			double executingCarrierHeight = executingCarrierTextList.getListHeight();
			int row2Height = (int) Math.round(max(soldToHeight, commercialCarrierHeight, executingCarrierHeight));
			
			// row2
			PdfRow row2 = new PdfRowBuilder(3, row2Height, 10, 0)
					.addElement(soldToTextList)
					.addElement(commercialCarrierTextList)
					.addElement(executingCarrierTextList)
					.build();
			
			
			// record position of the first address block's top 
			underLines.saveAddressBlock1YCoordinate(row1);
			
			// insert rows
			content.add(row1);
			
			// add 4 point of vertical space
			DeliveryNoteLinesSectionUtil.addSpace(content, 4);
			
			// save second block's Y position
			underLines.saveAddressBlock2YCoordinate(row2);
			
			// add second row
			content.add(row2);
			
			// a little space above the crossboundary part
			DeliveryNoteLinesSectionUtil.addSpace(content, ROW_HEIGHT_11);
			
			// NHM code
			boolean showNHMCode = false;
			if (!store.getData(DeliveryNoteData.NHM).isEmpty()) {
				showNHMCode = true;
				String nhmCode = store.getLabel(DeliveryNoteLabel.NHM_CODE) + COLON_SPACE + store.getData(DeliveryNoteData.NHM);
				content.add(new PdfRowBuilder(1, 12, 10, 0).addElement(
						SimplePdfText.create(nhmCode).setFont(new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_10))).build());
			}
			
			// Crossboundary code
			int crossBoundaryHeight = 0;
			String crossBoundaryText = store.getData(DeliveryNoteData.CROSSBOUNDARY);
			String crossBoundaryExportInvoiceLine1 = store.getData(DeliveryNoteData.CROSSBOUNDARY_EXPORT_INVOICE_LINE1);
			String crossBoundaryExportInvoiceLine2 = store.getData(DeliveryNoteData.CROSSBOUNDARY_EXPORT_INVOICE_LINE2);
			
			if (StringUtils.isNotEmpty(crossBoundaryText)) {
				crossBoundaryHeight = 48;
			
				content.add(new PdfRowBuilder(1, crossBoundaryHeight, 10, 0).addElement(
						SimplePdfText.create(crossBoundaryText).setFont(helvetica_10)).build());
			}
			else if (StringUtils.isNotEmpty(crossBoundaryExportInvoiceLine1) || StringUtils.isNotEmpty(crossBoundaryExportInvoiceLine2)) {
				
				int heightOfOneCrossBoundaryLine = 24;
				
				if (StringUtils.isNotEmpty(crossBoundaryExportInvoiceLine1)) {
					content.add(new PdfRowBuilder(1, heightOfOneCrossBoundaryLine, 10, 0).addElement(
							SimplePdfText.create(crossBoundaryExportInvoiceLine1).setFont(helvetica_10)).build());
					crossBoundaryHeight += heightOfOneCrossBoundaryLine;
				}
				
				if (StringUtils.isNotEmpty(crossBoundaryExportInvoiceLine2)) {
					content.add(new PdfRowBuilder(1, heightOfOneCrossBoundaryLine, 10, 0).addElement(
							SimplePdfText.create(crossBoundaryExportInvoiceLine2).setFont(helvetica_10)).build());
					crossBoundaryHeight += heightOfOneCrossBoundaryLine;
				}
			}
				
			// banner
			if (store.getData(DeliveryNoteData.CROSSBOUNDARY).isEmpty() && store.getData(DeliveryNoteData.NHM).isEmpty() && !store.getData(DeliveryNoteData.BANNER_IMG_NAME).isEmpty() && !Boolean.valueOf(store.getData(DeliveryNoteData.IS_CASH_SALES))) {
				String bannerImageName = store.getData(DeliveryNoteData.BANNER_IMG_NAME);
				PdfLayoutGenerationTool.showImage(blobService, (JcanPdfRenderer) renderer, bannerImageName, 50, 452, PdfLayoutGenerationTool.BANNER_WIDTH, PdfLayoutGenerationTool.BANNER_HEIGHT);
			
				// add space
				DeliveryNoteLinesSectionUtil.addSpace(content, (int)PdfLayoutGenerationTool.BANNER_HEIGHT + 5);
			
			}
			
			// add fixed text before Material lines
			final String fixedText = store.getData(DeliveryNoteData.CASH_SALES_FIXED_TEXT);
			if (!StringUtils.isEmpty(fixedText)) {
			
				// add space
				DeliveryNoteLinesSectionUtil.addSpace(content, 11);
				
				final SimplePdfText fixedTextPdfText = new SimplePdfText(fixedText, 440, 22);
				content.add(new PdfRowBuilder(1, 22, 10, 0).addElement(fixedTextPdfText).build());
			}
			
			// save materials table position
			underLines.saveMaterialBlockYCoordinate();
			
			// add 4pt of space 
			DeliveryNoteLinesSectionUtil.addSpace(content, 4);
			
			// generate the material table and add it to content
			createMaterialTable(store, content);
			
			Object cashSalesTotalRecordObj = store.getObjectData(DeliveryNoteData.CASH_SALES_TOTAL_RECORD);
			
			// If the cashSalesTotalRecord is not null need to display the total record in a new table
			// Need new table to have some space between the records and the total record
			boolean totalRecordPresent = cashSalesTotalRecordObj != null;
			if (totalRecordPresent) {
				
				MaterialTotalsTextDTO totalRecord = (MaterialTotalsTextDTO) cashSalesTotalRecordObj;
				
				// add space after material table
				DeliveryNoteLinesSectionUtil.addSpace(content, 4);
				
				SimplePdfText net = new SimplePdfText(totalRecord.getNetPrice());
				SimplePdfText vat = new SimplePdfText(totalRecord.getVat());
				SimplePdfText gross = new SimplePdfText(totalRecord.getGrossPrice());
				net.setWidth(TOTAL_LINE_NET_GROSS_WIDTH);
				vat.setWidth(TOTAL_LINE_VAT_WIDTH);
				gross.setWidth(TOTAL_LINE_NET_GROSS_WIDTH);			
				JcanPdfRenderer.cutTextToWidth(net, TOTAL_LINE_NET_GROSS_WIDTH);
				JcanPdfRenderer.cutTextToWidth(vat, TOTAL_LINE_VAT_WIDTH);
				JcanPdfRenderer.cutTextToWidth(gross, TOTAL_LINE_NET_GROSS_WIDTH);
				
				PdfRow totalsRow = new PdfRowBuilder(3, ROW_HEIGHT_11, 10, 0)
						.addElement(net)
						.addElement(vat)
						.addElement(gross)
					.build();
				
				content.add(totalsRow);
			}
			
			// not to confuse with total AS text!
			// example for this: "Ukupna vrednost facture u RSD"
			final String exportTotalText = store.getData(DeliveryNoteData.EXPORT_INVOICE_TOTAL_TEXT);
			boolean exportTotalTextPresent = !StringUtils.isEmpty(exportTotalText);
			if (exportTotalTextPresent) {
				final SimplePdfText totalTextPdfText = new SimplePdfText(exportTotalText, 200, ROW_HEIGHT_11);
				content.add(new PdfRowBuilder(1, ROW_HEIGHT_11, 10, 0).addElement(totalTextPdfText).build());
			}		
			
			// Show the total as text
			final Object cashSalesTotalAsTextObj = store.getObjectData(DeliveryNoteData.CASH_SALES_TOTAL_AS_TEXT);
			boolean printTotalAsTextToo = cashSalesTotalAsTextObj != null;
			if (printTotalAsTextToo) {
				final String cashSalesTotalAsText = (String) cashSalesTotalAsTextObj;
				final SimplePdfText cashSalesTotalAsTextPdfText = new SimplePdfText(cashSalesTotalAsText, 500, ROW_HEIGHT_11);
				content.add(new PdfRowBuilder(1, ROW_HEIGHT_11, 10, 0).addElement(cashSalesTotalAsTextPdfText).build());
			}
			
			if (totalRecordPresent || printTotalAsTextToo || exportTotalTextPresent){
				
				// add lines custom section to the renderer
				int availableSpace = PdfLayoutGenerationTool.getAvailableSpace(document);;
				DeliveryNoteTotalLinesPrintSection linePrintSection = new DeliveryNoteTotalLinesPrintSection(
									totalRecordPresent,
									printTotalAsTextToo,
									exportTotalTextPresent,
									availableSpace);
				renderer.addCustomSection(linePrintSection);
			}
			
			// add space after total material table
			DeliveryNoteLinesSectionUtil.addSpace(content, 4);
			
			//returned pallets
			String returnedPallets = store.getData(DeliveryNoteData.RETURNED_PALLETS);
			if (!returnedPallets.isEmpty()) {
				content.add(new PdfRowBuilder(1, 12, 10, 0).addElement(SimplePdfText.create(
						store.getLabel(DeliveryNoteLabel.RETURNED_PALLETS) + COLON_SPACE + store.getData(DeliveryNoteData.RETURNED_PALLETS))).build());
			}
			// general delivery type info
			//		content.add(new PdfRowBuilder(1, 11, 10, 0).addElement(SimplePdfText.create(store.getData(DeliveryNoteData.STATION_TITLE_PAR))).build());
			String deliveryTypeInfo = store.getData(DeliveryNoteData.STATION_TITLE_PAR);
			addDeliveryTypeInfo(content, deliveryTypeInfo);
			
			
			// save start y coordinate of logo section
			logoSection.setLogoSectionStartYCoordinate();
			
			// add space
			DeliveryNoteLinesSectionUtil.addSpace(content, PdfLayoutGenerationTool.getAvailableSpace(document) - 119);
			
			// save end Y coordinate of logo section
			logoSection.setLogoSectionEndYCoordinate(12);
			
			// transport responsibility information
			content.add(new PdfRowBuilder(1, 9, 10, 0).addElement(SimplePdfText.create(store.getData(DeliveryNoteData.TRANSPORT_RESPONSIBILITY_INFORMATION)).setFont(helvetica_7)).build());
				

			
			// chromat reduction comment
			content.add(new PdfRowBuilder(1, 9, 10, 0).addElement(SimplePdfText.create(store.getData(DeliveryNoteData.CHROMAT_REDUCTION_COMM)).setFont(helvetica_7)).build());
			
			// export invoice - legal text 1
			final String legalText1 = store.getData(DeliveryNoteData.EXPORT_INVOICE_LEGAL_TEXT_1);
			if (!StringUtils.isEmpty(legalText1)) {
				final int height = 22; // 2 rows
				final SimplePdfText legalText1PdfText = new SimplePdfText(legalText1, 500, height);
				content.add(new PdfRowBuilder(1, height, 10, 0).addElement(legalText1PdfText).build());
			}
			
			// export invoice - legal text 2
			final String legalText2 = store.getData(DeliveryNoteData.EXPORT_INVOICE_LEGAL_TEXT_2);
			if (!StringUtils.isEmpty(legalText2)) {
				final int height = 22; // 2 rows
				final SimplePdfText legalText2PdfText = new SimplePdfText(legalText2, 500, height);
				content.add(new PdfRowBuilder(1, height, 10, 0).addElement(legalText2PdfText).build());
			}
			
			underLines.saveRemarkBlockYCoordinate();
			
			// add 4pt space below the line
			DeliveryNoteLinesSectionUtil.addSpace(content, 4);
			
			// comment. can be multiline. calculate space based on number of lines
			SimplePdfText remarkText = SimplePdfText.create(
					store.getLabel(DeliveryNoteLabel.COMMENT) + COLON_SPACE).
					setFont(helvetica_9);
			
			PdfRow remarkRow = new PdfRowBuilder(1, 12, 10, 0).addElement(remarkText).build();
			
			content.add(remarkRow);
			
			int spaceAfterComments = 61;
			String commentData = store.getData(DeliveryNoteData.COMMENT);
			String[] strings = commentData.split("\\n");
			for (String string : strings) {
				if (spaceAfterComments < 11) {
					break;
				}
				content.add(new PdfRowBuilder(1, 11, 10, 0).addElement(SimplePdfText.create(string).setFont(new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_8))).build());
				spaceAfterComments -= 11;
			}
			
			// adding fix remark text
			content.add(new PdfRowBuilder(1, 12, 10, 0).addElement(SimplePdfText.create(store.getData(DeliveryNoteData.FIX_REMARK_TXT)).setFont(helvetica_9)).build());
					
			// driver, dispatcher and customer signature part
			createDriverDispatcherCustomerPart(store, content, spaceAfterComments, renderer);
			
			// footer
			content.add(new PdfRowBuilder(1, 10, 10, 0).addElement(SimplePdfText.create(store.getData(DeliveryNoteData.FOOTER_TITLE)).setFont(helvetica_7)).build());
			content.add(new PdfRowBuilder(1, 20, 10, 0).addElement(SimplePdfText.create(store.getData(DeliveryNoteData.FOOTER_TEXT)).setFont(helvetica_7)).build());
			
			// eichbeleg list
			List<EichbelegDTO> eichbelegDTOs = store.getDTOList(EichbelegDTO.class);
			if (!eichbelegDTOs.isEmpty()) {	
				content.add(new PdfRowBuilder(1, 9, 10, 0).addElement(SimplePdfText.create(store.getData(DeliveryNoteData.EICHBELEG_TEXT)).setFont(helvetica_7)).build());
				int numberOfLines = 0;
				for (EichbelegDTO eichbelegDTO : eichbelegDTOs) {
					// don't print more than 2 lines
					if (numberOfLines > 2) {
						break;
					}
					String line = eichbelegDTO.getText(); 
					SimplePdfText txt = SimplePdfText.create(line).setFont(helvetica_7);
					content.add(new PdfRowBuilder(1, 10, 10, 0).addElement(txt).build());
					numberOfLines++;
				}
			}
			
			
			
			// render the document
			renderer.initializeDocument(document);
			renderer.renderPage(document);
			renderer.close();
			
			return result = true;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Creates the material table.
	 * @param store the {@link AbstractPdfDataStore}
	 * @param content the Delivery Note document
	 */
	private static void createMaterialTable(
			AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store, List<PdfObject> content) {
		Boolean hasReturn = Boolean.valueOf(store.getData(DeliveryNoteData.RETURN_DATA_FLAG));
		Boolean isBag = Boolean.valueOf(store.getData(DeliveryNoteData.IS_BAG));
		Boolean isBulk = Boolean.valueOf(store.getData(DeliveryNoteData.IS_BULK));
		Boolean isServiceOnly = Boolean.valueOf(store.getData(DeliveryNoteData.IS_SERVICE_ONLY));
		Boolean isBulkAndService = Boolean.valueOf(store.getData(DeliveryNoteData.IS_BULK_AND_SERVICE));
		Boolean isBagAndService = Boolean.valueOf(store.getData(DeliveryNoteData.IS_BAG_AND_SERVICE));
		
		final int materialTableFontHeight = FONT_SIZE_12PT;
		List<SimplePdfText> headers = new ArrayList<SimplePdfText>();
		/* 
		 * LOGON-6236 Pallet item not printed in the DN. Only 8 lines allowed.
		 * Material name longer than 28 characters would break into new row 
		 * resulting some material missing at the end of the table.
		 * Hence the width of the material name column is increased from 150 to 180.
		 */
		SimplePdfText materialLabelSimplePdfText = new SimplePdfText(store.getLabel(DeliveryNoteLabel.MATERIAL) 
				+ COLON_SPACE, MATERIAL_WIDTH, materialTableFontHeight);
		headers.add(materialLabelSimplePdfText);
		if (isBulk || isBulkAndService) {
			if (hasReturn) {
				// empty header for material hierarchy
				headers.add(new SimplePdfText("", MATERIAL_HIERARCHY_SHORT_WIDTH, materialTableFontHeight)); 
			}
			else {
				// empty header for material hierarchy
				headers.add(new SimplePdfText("", MATERIAL_HIERARCHY_LONG_WIDTH, materialTableFontHeight)); 
			}
			headers.add(getRightAlignedHeaderText(store, DeliveryNoteLabel.WEIGHTS_GROSS));
			headers.add(getRightAlignedHeaderText(store, DeliveryNoteLabel.WEIGHTS_TARE));
			if (hasReturn) {
				headers.add(getRightAlignedHeaderText(store, DeliveryNoteLabel.WEIGHTS_RETURN));
			}
			SimplePdfText netWeightHeader = new SimplePdfText(store.getLabel(DeliveryNoteLabel.WEIGHTS_NET) 
					+ COLON_SPACE, WEIGHTS_NET_BULK_WIDTH, materialTableFontHeight);
			headers.add(PdfLayoutGenerationTool.rightAlignText(netWeightHeader));
		}
		else if (isBag || isServiceOnly || isBagAndService){
			// empty header for material hierarchy
			headers.add(new SimplePdfText("", MATERIAL_HIERARCHY_SHORT_WIDTH, materialTableFontHeight)); 
			headers.add(getRightAlignedHeaderText(store, DeliveryNoteLabel.QUANTITY_ORDERED));
			headers.add(getRightAlignedHeaderText(store, DeliveryNoteLabel.QUANTITY_DELIVERED));
			SimplePdfText netWeightHeader = new SimplePdfText(store.getLabel(DeliveryNoteLabel.WEIGHTS_NET) 
					+ COLON_SPACE, WEIGHTS_NET_BAG_WIDTH, materialTableFontHeight);
			headers.add(PdfLayoutGenerationTool.rightAlignText(netWeightHeader));
		}
		
		PdfTable materialTable = new PdfTable(headers, materialTableFontHeight);
		materialTable.setLabelProvider(new DeliveryNoteOrderItemLabelProvider(hasReturn, isBag, isServiceOnly));
		materialTable.setInput(store.getDTOList(OrderItemDTO.class));
		
		int calculatedTableHeight = PdfTableUtil.getHeightInPoints(materialTable);
		content.add(new PdfRowBuilder(1, calculatedTableHeight, 10, 0).addElement(materialTable).build());
	}

	/** 
	 * Creates the driver / dispatcher area + customer signature in the footer
	 * @param store
	 * @param content
	 * @param spaceAfterComments
	 * @param renderer
	 */
	private static void createDriverDispatcherCustomerPart(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store, List<PdfObject> content, int spaceAfterComments, AbstractPdfRenderer renderer) {

		// add space
		DeliveryNoteLinesSectionUtil.addSpace(content, spaceAfterComments);
		
		int numOfRowsForDriver = 0; // determine the number of rows needed for the driver part
		int numOfRowsForDispatcher = 0; // determine the number of rows needed for the dispatcher part
		String customerSigText = "(" + store.getData(DeliveryNoteData.CUSTOMER) + store.getLabel(DeliveryNoteLabel.SIGNATURE_CUSTOMER) + ")";
		SimplePdfText crhSig = new SimplePdfText("(" + store.getLabel(DeliveryNoteLabel.CRH) + ")", 230, 14).setFont(helvetica_10);
		SimplePdfText driverSig = new SimplePdfText("(" + store.getData(DeliveryNoteData.DRIVER_NAME) + store.getLabel(DeliveryNoteLabel.SIGNATURE_DRIVER) + ")", 180, 14).setFont(helvetica_10);
		SimplePdfText customerSig  = new SimplePdfText(customerSigText, 160, 14).setFont(helvetica_10);
		
		List<String> dispatcherList = new ArrayList<String>();
		List<String> driverList = new ArrayList<String>();
		List<String> customerSigList = new ArrayList<String>();
				
		String driverName = store.getData(DeliveryNoteData.DRIVER_NAME);
		if (StringUtils.isNotEmpty(driverName)){ 
			driverList.add(driverName);
			numOfRowsForDriver++;
		}
		String driverCardNo = store.getData(DeliveryNoteData.DRIVER_CARD_NO);
		if (StringUtils.isNotEmpty(driverCardNo)){
			driverList.add(driverCardNo);
			numOfRowsForDriver++;
		}
		String driverId = store.getData(DeliveryNoteData.DRIVER_ID);
		if (StringUtils.isNotEmpty(driverId)){
			driverList.add(driverId);
			numOfRowsForDriver++;
		}
		String dispatcherName = store.getData(DeliveryNoteData.DISPATCHER_NAME);
		if (StringUtils.isNotEmpty(dispatcherName)){
			dispatcherList.add(dispatcherName);
			numOfRowsForDispatcher++;
		}
		String dispatcherTel = store.getData(DeliveryNoteData.DISPATCHER_TEL);
		if (StringUtils.isNotEmpty(dispatcherTel)){
			dispatcherList.add(dispatcherTel);
			numOfRowsForDispatcher++;
		}
		String dispatcherId = store.getData(DeliveryNoteData.DISPATCHER_ID);
		if (StringUtils.isNotEmpty(dispatcherId)){
			dispatcherList.add(dispatcherId);
			numOfRowsForDispatcher++;
		}

		// this list will ensure that the "(Customer)" text will be in the bottom row (instead of the top one)
		// it's needed because of a bug in PDFRowBuilder - it renders everything in one column if the added 
		//  elements have different types (e.g.: SimpePDFText and TextListPart).
		int dispDriverTableNumOfRows = Math.max(numOfRowsForDispatcher, numOfRowsForDriver);
		for(int i=0; i<dispDriverTableNumOfRows-1; i++){
			customerSigList.add(" ");
		}
		customerSigList.add(customerSigText);
		
		TextListPart dispatcherArea = new TextListPart(160, 14, helvetica_10, dispatcherList);
		TextListPart driverArea = new TextListPart(160, 14, helvetica_10, driverList);
		TextListPart customerSigArea = new TextListPart(160, 14, helvetica_10, customerSigList);
				
		if(dispDriverTableNumOfRows == 0){ // if there is no data regarding the dispatcher/driver, display the signatures only
			content.add(new PdfRowBuilder(3, 14, 10, 0)
					.addElement(crhSig)
					.addElement(driverSig)
					.addElement(customerSig)
					.build());
			showDriverSignature(store, renderer);
			
		} else{
			content.add(new PdfRowBuilder(3, dispDriverTableNumOfRows * 14, 10, 0)
					.addElement(dispatcherArea)
					.addElement(driverArea)
					.addElement(customerSigArea)
					.build());
		}
		
		showCustomerSignature(store, renderer);
		
	}

	private static void showDriverSignature(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store, AbstractPdfRenderer renderer) {
		// driver's signature
		byte[] data = store.getByteStream(DeliveryNoteData.DRIVER_SIG_BYTE_STREAM);
		if (data.length > 0) {
			PdfLayoutGenerationTool.showImage((JcanPdfRenderer)renderer, 
					                           data, 
					                           260, 
					                           104, 
					                           PdfLayoutGenerationTool.SIGNATURE_WIDTH, 
					                           PdfLayoutGenerationTool.SIGNATURE_HEIGHT);
		}
	}

	private static void showCustomerSignature(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store, AbstractPdfRenderer renderer) {
		byte[] data;
		//customer's signature
		data = store.getByteStream(DeliveryNoteData.CUST_SIG_BYTE_STREAM);
		if (data.length > 0) {
			PdfLayoutGenerationTool.showImage((JcanPdfRenderer)renderer, 
					                           data, 
					                           420, 
					                           104, 
					                           PdfLayoutGenerationTool.SIGNATURE_WIDTH, 
					                           PdfLayoutGenerationTool.SIGNATURE_HEIGHT);
		}
	}
	
	private static SimplePdfText getHeaderText(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store, DeliveryNoteLabel label) {
		SimplePdfText simplePdfText = new SimplePdfText(store.getLabel(label) + COLON_SPACE, getMaterialTableColumnWidth(label), 12);
		return simplePdfText;
	}
	
	private static SimplePdfText getRightAlignedHeaderText(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store, DeliveryNoteLabel label) {
		SimplePdfText simplePdfText = getHeaderText(store, label);
		PdfLayoutGenerationTool.rightAlignText(simplePdfText);
		return simplePdfText;
	}

	private static int getMaterialTableColumnWidth(DeliveryNoteLabel label) {
		if (DeliveryNoteLabel.WEIGHTS_GROSS.equals(label)) {
			return WEIGHTS_GROSS_WIDTH;		
		}
		else if (DeliveryNoteLabel.WEIGHTS_TARE.equals(label)) {
			return WEIGHTS_TARE_WIDTH;
		}
		else if (DeliveryNoteLabel.WEIGHTS_RETURN.equals(label)) {
			return WEIGHTS_RETURN_WIDTH;
		}
		else if (DeliveryNoteLabel.QUANTITY_ORDERED.equals(label)) {
			return QUANTITY_ORDERED_WIDTH;
		}
		else if (DeliveryNoteLabel.QUANTITY_DELIVERED.equals(label)) {
			return QUANTITY_DELIVERED_WIDTH;
		}
		else if (DeliveryNoteLabel.MATERIAL.equals(label)) {
			return MATERIAL_WIDTH;
		}
		else {
			return 0;
		}
	}
		
	private static void addDeliveryTypeInfo(List<PdfObject> content, String title) {
		if (title.length() <= STATION_TITLE_ROW_LENGTH) {
			content.add(new PdfRowBuilder(1, 11, 10, 0).addElement(SimplePdfText.create(title)).build());
		}
		else {
			List<String> splittedTitle = com.crh.logon.utils.StringUtils.wrapText(title, STATION_TITLE_ROW_LENGTH);
			if (splittedTitle != null) {
				int i = 0;
				for (String titleRow : splittedTitle) {
					if (i < STATION_TITLE_MAX_ROWS) {
						content.add(new PdfRowBuilder(1, 11, 10, 0).addElement(SimplePdfText.create(titleRow)).build());
						i++;
					}
				}
			}
		}
	}
}
