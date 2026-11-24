/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.colldelnote;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crh.logon.business.services.localinterfaces.GenericBlobServiceLocalInterface;
import com.crh.logon.business.services.pdftools.layout.AddressPart;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteData;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteLabel;
import com.crh.logon.business.services.pdftools.layout.JcanPdfRenderer;
import com.crh.logon.business.services.pdftools.layout.OrderItemDTO;
import com.crh.logon.business.services.pdftools.layout.PdfLayoutGenerationTool;
import com.crh.logon.business.services.pdftools.layout.TextListPart;
import com.crh.logon.business.services.pdftools.layout.TitlePart;
import com.crh.logon.business.services.pdftools.layout.utils.CollectiveDeliveryNoteGenerationResult;
import com.crh.logon.business.services.pdftools.layout.utils.CollectiveDeliveryNotesDTOs;
import com.crh.logon.business.services.pdftools.layout.utils.DeliveryNoteLinesSectionUtil;
import com.crh.logon.business.services.pdftools.layout.utils.GenerateCollectiveDeliveryNoteWithResultParameter;
import com.crh.logon.business.services.pdftools.layout.utils.NullByteArrayOutputStream;
import com.crh.logon.pdftool.PdfDocumentWrapper;
import com.crh.logon.pdftool.labelprovider.DTOMarker;
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
 * Main layout for collective delivery note
 * @author AdNovum Informatik AG
 */
public class CollDelNoteMainLayout {

	/**
	 * Footer text row height in Points
	 */
	private static final int FOOTER_TEXT_ROW_HEIGHT_PT = 20;

	/**
	 * Footer title row height in Points
	 */
	private static final int FOOTER_TITLE_ROW_HEIGHT_PT = 10;

	/**
	 * Crh,Driver,Customer signature row height in Points
	 */
	private static final int CRH_DRIVER_CUSTOMER_SIGNATURE_ROW_HEIGHT_PT = 14;

	/**
	 * Signature label row height in Points
	 */
	private static final int SIGNATURE_ROW_HEIGHT_PT = 50;

	/**
	 *  Remark text row height in Points
	 */
	private static final int REMARK_TEXT_ROW_HEIGHT_PT = 11;

	/**
	 * Remark label row height in Points
	 */
	private static final int REMARK_LABEL_ROW_HEIGHT_PT = 12;

	/**
	 * Flag to indicate that the collective delivery note should be rendered
	 */
	private static final boolean RENDER_DOCUMENT = false;

	/**
	 * Flag to indicate that the collective delivery note should not be rendered
	 */
	private static final boolean DO_NOT_RENDER_DOCUMENT = true;

	/**
	 * 12Pt Font Height
	 */
	private static final int FONT_HEIGHT_12PT = 12;
	
	/**
	 * Height of the shipping part in points
	 */
	private static final int SHIPPING_PART_HEIGHT_PT = 50;

	/**
	 * Height of the Title part in points
	 */
	private static final int TITLE_PART_HEIGHT_PT = 26;

	/**
	 * Height of the row in points
	 */
	private static final int TITLE_ROW_HEIGHT_PT = 30;

	/**
	 * Row height in the totals table
	 */
	private static final int TOTALS_TABLE_ROW_HEIGHT_PT = FONT_HEIGHT_12PT;

	/**
	 * Index for the first page of the collective delivery note
	 */
	private static final int FIRST_PAGE = 1;

	/**
	 * Empty {@link String}
	 */
	private static final String EMPTY_STRING = "";

	/**
	 * 85 Point height constant
	 */
	private static final int ENOUGH_SPACE_FOR_FOOTER_PT = 85;

	/**
	 * Font height of 11 Points
	 */
	private static final int FONT_HEIGHT_11PT = 11;

	/**
	 * A colon and a space character
	 */
	private static String COLON_SPACE = PdfLayoutGenerationTool.COLON_SPACE;

	/**
	 * Height of a cell for the order table. Since row height could duplicate when delivery time is displayed, it is multiplied by 2 (worst case)
	 */
	private static final int ORDER_TABLE_CELL_HEIGHT = FONT_HEIGHT_12PT * 2;

	/**
	 * Entry point for Collective Delivery Note generator.
	 * The generation is done in two run:
	 * <ul><li>In the first run the number of pages is calculated (otherwise it would be impossible to calculate the number of pages of the result document)
	 * <li>In the second run, the actual pdf is generated
	 * </ul>
	 * @param store {@link AbstractPdfDataStore}
	 * @param renderer {@link AbstractPdfRenderer}
	 * @param blobService {@link GenericBlobServiceLocalInterface}
	 * @return true if the Collective Delivery Note generation succeeded, false otherwise.
	 */
	public static boolean generateCollectiveDeliveryNote(
			AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store,
			AbstractPdfRenderer renderer, GenericBlobServiceLocalInterface blobService) {

		// first run: calculate number of pages and physical and service items
		final ByteArrayOutputStream pdfOutStream = new NullByteArrayOutputStream();
		final ByteArrayOutputStream psOutStream = new NullByteArrayOutputStream();
		final AbstractPdfRenderer mockRenderer = new JcanPdfRenderer(pdfOutStream, psOutStream);
		
		GenerateCollectiveDeliveryNoteWithResultParameter calculateNumberOfPagesParameterObject =
				new GenerateCollectiveDeliveryNoteWithResultParameter(store, mockRenderer, blobService, 0, null, DO_NOT_RENDER_DOCUMENT);
		
		CollectiveDeliveryNoteGenerationResult result = generateCollectiveDeliveryNoteWithResult(calculateNumberOfPagesParameterObject);

		if (result.isSuccess()) {

			// second run: generate the document with the previously calculated number of pages
			// the service and physical items also reused
			GenerateCollectiveDeliveryNoteWithResultParameter collectiveDeliveryNoteParameterObject = new GenerateCollectiveDeliveryNoteWithResultParameter(
					store, renderer, blobService, result.getNumberOfPages(),
					result.getPhysicalAndServiceItems(), RENDER_DOCUMENT);

			result = generateCollectiveDeliveryNoteWithResult(collectiveDeliveryNoteParameterObject);

			return result.isSuccess();
		}
		else {
			return false;
		}
	}

	/**
	 * Calculates the list of physical and service DTOs from the store's DTO list.
	 * @param parameter {@link GenerateCollectiveDeliveryNoteWithResultParameter}
	 * @return {@link CollectiveDeliveryNotesDTOs}
	 */
	private static CollectiveDeliveryNotesDTOs definePhysicalAndServiceDTOs(
			GenerateCollectiveDeliveryNoteWithResultParameter parameter) {
		ArrayList<DTOMarker> physicalDTOs = new ArrayList<DTOMarker>();
		ArrayList<DTOMarker> serviceDTOs = new ArrayList<DTOMarker>();
		List<OrderItemTotalDTO> totalItemDTOs = parameter.getStore().getDTOList(
				OrderItemTotalDTO.class);
		for (OrderItemTotalDTO orderItemTotalDTO : totalItemDTOs) {
			if (orderItemTotalDTO.isPhysical()) {
				physicalDTOs.add(orderItemTotalDTO);
			}
			else {
				serviceDTOs.add(orderItemTotalDTO);
			}
		}

		// add extra DTOs with summary and label information
		if (physicalDTOs.size() > 0) {
			Map<Integer, String> physicalLabelMap = new HashMap<Integer, String>();
			physicalLabelMap.put(0, parameter.getStore().getLabel(DeliveryNoteLabel.PHYSICAL_MAT)
					+ COLON_SPACE);
			physicalDTOs.add(0, new LabelDTO(physicalLabelMap));
		}
		Map<Integer, String> physicalTotalMap = new HashMap<Integer, String>();
		physicalTotalMap.put(1, parameter.getStore().getLabel(DeliveryNoteLabel.TOTAL));
		physicalTotalMap.put(2, parameter.getStore().getData(DeliveryNoteData.RIDES) + " "
				+ parameter.getStore().getLabel(DeliveryNoteLabel.RIDES));
		if (!parameter.getStore().getData(DeliveryNoteData.NET_TOTAL_UOM).isEmpty()) {
			physicalTotalMap.put(3, parameter.getStore().getData(DeliveryNoteData.NET_TOTAL_WEIGHT));
			physicalTotalMap.put(4, parameter.getStore().getData(DeliveryNoteData.NET_TOTAL_UOM));
		}
		physicalDTOs.add(new LabelDTO(physicalTotalMap, new PdfFont(PdfFontStore.Helvetica_Bold,
				PdfFontStore.SIZE_9)));

		if (serviceDTOs.size() > 0
				|| !parameter.getStore().getData(DeliveryNoteData.WAITING_TIME).isEmpty()) {
			Map<Integer, String> servicesLabelMap = new HashMap<Integer, String>();
			servicesLabelMap.put(0, parameter.getStore().getLabel(DeliveryNoteLabel.SERVICES)
					+ COLON_SPACE);
			serviceDTOs.add(0, new LabelDTO(servicesLabelMap));
			if (!parameter.getStore().getData(DeliveryNoteData.WAITING_TIME).isEmpty()) {
				Map<Integer, String> servicesWaitingTimeMap = new HashMap<Integer, String>();
				servicesWaitingTimeMap.put(2,
						parameter.getStore().getLabel(DeliveryNoteLabel.WAIT_TIME_TOTAL));
				servicesWaitingTimeMap.put(3,
						parameter.getStore().getData(DeliveryNoteData.WAITING_TIME));
				servicesWaitingTimeMap.put(4,
						parameter.getStore().getLabel(DeliveryNoteLabel.WAIT_TIME_HOURS));
				serviceDTOs.add(1, new LabelDTO(servicesWaitingTimeMap));
			}
		}
		return new CollectiveDeliveryNotesDTOs(physicalDTOs, serviceDTOs);

	}

	/**
	 * Generates the Collective Delivery Note.
	 * @param parameter {@link GenerateCollectiveDeliveryNoteWithResultParameter}
	 * @return {@link CollectiveDeliveryNoteGenerationResult}
	 */
	private static CollectiveDeliveryNoteGenerationResult generateCollectiveDeliveryNoteWithResult(
			GenerateCollectiveDeliveryNoteWithResultParameter parameter) {

		// page calculations
		AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store = parameter.getStore();
		List<OrderItemDTO> orderItemDTOs = store.getDTOList(OrderItemDTO.class);
		CollectiveDeliveryNoteGenerationResult result = new CollectiveDeliveryNoteGenerationResult(
				0, false, null);

		CollectiveDeliveryNoteGeneratorContext context = new CollectiveDeliveryNoteGeneratorContext();
		context.setTotalNumberOfPages(parameter.getNumberOfPages());

		if (!parameter.isPhysicalAndServiceItemsDefined()) {
			CollectiveDeliveryNotesDTOs physicalAndServiceDTOs = definePhysicalAndServiceDTOs(parameter);
			// save it to the results
			result.setPhysicalAndServiceItems(physicalAndServiceDTOs);
			context.copyPhysicalDTOs(physicalAndServiceDTOs.getPhysicalDTOs());
			context.copyServiceDTOs(physicalAndServiceDTOs.getServiceDTOs());
		}
		else {
			// coming from the method parameters
			context.copyPhysicalDTOs(parameter.getPhysicalAndServiceItems().getPhysicalDTOs());
			context.copyServiceDTOs(parameter.getPhysicalAndServiceItems().getServiceDTOs());
		}

		PdfDocumentWrapper document = null;

		CollectiveDeliveryNoteLinePrintSection linePrintSection = null;

		AbstractPdfRenderer renderer = parameter.getRenderer();

		// add new pages until the footer is shown
		while (!context.isFooterShown()) {
			// create new document for each new page
			document = new PdfDocumentWrapper();
			linePrintSection = new CollectiveDeliveryNoteLinePrintSection(document);
			renderer.addCustomSection(linePrintSection);

			// add header to every page
			CollDelNoteMainLayout.addCollectiveDeliveryNoteHeader(parameter, document);

			List<PdfObject> documentContent = document.getContent();
			if (context.getCurrentPage() == FIRST_PAGE) {
				// call initialization
				renderer.initializeDocument(document);
				addShippingPart(store, linePrintSection, documentContent);
			}

			// show orders table elements first
			addOrderItems(store, orderItemDTOs, document, linePrintSection);

			// show physical totals table. If the table hasn't been started yet and there is no place to display it
			// on the same page, start a new one
			addPhysicalTable(store, context, document);

			addServiceItems(store, context, document, linePrintSection);

			// add waiting time, signatures and footer if there is sufficient space remaining
			addFooter(store, context, document, linePrintSection);
			
			// render this page
			if (!parameter.isDoNotRenderDocument()) {
				// add page number if multipaged
				addPageNumber(context, renderer);
				renderer.renderPage(document);
			}
			context.setCurrentPage(context.getCurrentPage() + 1);
		}
		// close the renderer
		if (!parameter.isDoNotRenderDocument()) {
			renderer.close();
		}

		result.setSuccess(true);
		result.setNumberOfPages(context.getCurrentPage() - 1);

		return result;
	}

	/**
	 * Adds the page number
	 * @param context {@link CollectiveDeliveryNoteGeneratorContext}
	 * @param renderer {@link AbstractPdfRenderer}
	 */
	private static void addPageNumber(CollectiveDeliveryNoteGeneratorContext context, AbstractPdfRenderer renderer) {
		if (!context.isFooterShown() || context.getCurrentPage() != FIRST_PAGE) {
			((JcanPdfRenderer) renderer).setPageFooter(context.getCurrentPage(),
					context.getTotalNumberOfPages());
		}
	}

	/**
	 * Adds the footer part
	 * @param store {@link AbstractPdfDataStore}
	 * @param context {@link CollectiveDeliveryNoteGeneratorContext}
	 * @param document {@link PdfDocumentWrapper}
	 * @param linePrintSection {@link CollectiveDeliveryNoteLinePrintSection}
	 */
	private static void addFooter(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store,
			CollectiveDeliveryNoteGeneratorContext context, PdfDocumentWrapper document,
			CollectiveDeliveryNoteLinePrintSection linePrintSection) {
		if (PdfLayoutGenerationTool.getAvailableSpace(document) >= ENOUGH_SPACE_FOR_FOOTER_PT) {

			List<PdfObject> documentContent = document.getContent();
			
			// display footer at the bottom. fill paper with empty space
			if (PdfLayoutGenerationTool.getAvailableSpace(document) > ENOUGH_SPACE_FOR_FOOTER_PT) {
				int footerSpace = PdfLayoutGenerationTool.getAvailableSpace(document) - ENOUGH_SPACE_FOR_FOOTER_PT;
				// add space
				DeliveryNoteLinesSectionUtil.addSpace(documentContent, footerSpace);
				
			}

			linePrintSection.saveRemarkBlockYCoordinate();

			// add space 4pt
			DeliveryNoteLinesSectionUtil.addSpace(documentContent, 4);

			PdfRow remarksLabelRow = new PdfRowBuilder(1, REMARK_LABEL_ROW_HEIGHT_PT, 10, 0).addElement(
					SimplePdfText.create(store.getLabel(DeliveryNoteLabel.COMMENT) + COLON_SPACE).setFont(
							new PdfFont(PdfFontStore.Helvetica_Bold, PdfFontStore.SIZE_9))).build();
			documentContent.add(remarksLabelRow);
			PdfRow remarksRow = new PdfRowBuilder(1, REMARK_TEXT_ROW_HEIGHT_PT, 10, 0).addElement(
					SimplePdfText.create(store.getData(DeliveryNoteData.COMMENT)).setFont(
							new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_8))).build();
			documentContent.add(remarksRow);

			// space for signature, signature list
			documentContent.add(new PdfRowBuilder(1, SIGNATURE_ROW_HEIGHT_PT).addElement(
					SimplePdfText.create(EMPTY_STRING)).build());
			SimplePdfText driverSig = new SimplePdfText("("
					+ store.getLabel(DeliveryNoteLabel.SIGNATURE_DRIVER) + ")", 180, 14).setFont(new PdfFont(
					PdfFontStore.Helvetica, PdfFontStore.SIZE_10));
			SimplePdfText customerSig = new SimplePdfText("("
					+ store.getLabel(DeliveryNoteLabel.SIGNATURE_CUSTOMER) + ")", 180, 14).setFont(new PdfFont(
					PdfFontStore.Helvetica, PdfFontStore.SIZE_10));
			SimplePdfText crhSig = new SimplePdfText("("
					+ store.getLabel(DeliveryNoteLabel.CRH) + ")", 230, 14).setFont(new PdfFont(
					PdfFontStore.Helvetica, PdfFontStore.SIZE_10));
			documentContent.add(new PdfRowBuilder(3, CRH_DRIVER_CUSTOMER_SIGNATURE_ROW_HEIGHT_PT, 10, 0).addElement(crhSig).addElement(
					driverSig).addElement(customerSig).build());

			// footer
			documentContent.add(new PdfRowBuilder(1, FOOTER_TITLE_ROW_HEIGHT_PT, 10, 0).addElement(
					SimplePdfText.create(store.getData(DeliveryNoteData.FOOTER_TITLE)).setFont(
							new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_7))).build());
			documentContent.add(new PdfRowBuilder(1, FOOTER_TEXT_ROW_HEIGHT_PT, 10, 0).addElement(
					SimplePdfText.create(store.getData(DeliveryNoteData.FOOTER_TEXT)).setFont(
							new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_7))).build());

			context.setFooterShown(true);
		}
	}


	/**
	 * Adds the service items list
	 * @param store {@link AbstractPdfDataStore}
	 * @param context {@link CollectiveDeliveryNoteGeneratorContext}
	 * @param document {@link PdfDocumentWrapper}
	 * @param linePrintSection {@link CollectiveDeliveryNoteLinePrintSection}
	 */
	private static void addServiceItems(
			AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store, CollectiveDeliveryNoteGeneratorContext context,
			PdfDocumentWrapper document, CollectiveDeliveryNoteLinePrintSection linePrintSection) {
		if (!context.isServicesTableStarted() && context.getPhysicalDTOs().size() == 0) {
			// show services totals table. If the table hasn't been started yet and there is no place to display it
			// on the same page, start a new one
			if ((context.getServiceDTOs().size() + 3) * TOTALS_TABLE_ROW_HEIGHT_PT > PdfLayoutGenerationTool.getAvailableSpace(document)) {
				jumpToNewPage(document);
				context.setServicesTableStarted(true);
				context.setPrintSeparatorLine(false);
			}
		}

		if (context.getServiceDTOs().size() > 0) {
			int toShowOnThisPage = Math.min(PdfLayoutGenerationTool.getAvailableSpace(document)
					/ TOTALS_TABLE_ROW_HEIGHT_PT, context.getServiceDTOs().size());
			if (toShowOnThisPage > 0) {
				if (context.isPrintSeparatorLine()) {

					linePrintSection.saveServicesBlockYCoordinate();
					// add space 4pt
					DeliveryNoteLinesSectionUtil.addSpace(document.getContent(), 4);
					context.setPrintSeparatorLine(false);
				}
				List<DTOMarker> subList = new ArrayList<DTOMarker>(context.getServiceDTOs().subList(0,
						toShowOnThisPage));
				context.getServiceDTOs().removeAll(subList);
				addTotalsTable(store, document, subList);
				context.setServicesTableStarted(true);
			}
		}
	}

	/**
	 * Adds the table of the physical order items
	 * @param store {@link AbstractPdfDataStore}
	 * @param context {@link CollectiveDeliveryNoteGeneratorContext}
	 * @param document {@link PdfDocumentWrapper}
	 */
	private static void addPhysicalTable(
			AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store, CollectiveDeliveryNoteGeneratorContext context,
			PdfDocumentWrapper document) {
		if (!context.isPhysicalTableStarted()
				&& (context.getPhysicalDTOs().size() + 3) * TOTALS_TABLE_ROW_HEIGHT_PT > PdfLayoutGenerationTool.getAvailableSpace(document)) {
			jumpToNewPage(document);
			context.setPhysicalTableStarted(true);
		}
		if (context.getPhysicalDTOs().size() > 0) {
			int toShowOnThisPage = Math.min(PdfLayoutGenerationTool.getAvailableSpace(document)
					/ TOTALS_TABLE_ROW_HEIGHT_PT, context.getPhysicalDTOs().size());
			if (toShowOnThisPage > 0) {
				List<DTOMarker> subList = new ArrayList<DTOMarker>(context.getPhysicalDTOs().subList(0,
						toShowOnThisPage));
				context.getPhysicalDTOs().removeAll(subList);
				addTotalsTable(store, document, subList);
				context.setPhysicalTableStarted(true);
			}
		}
	}

	/**
	 * Adds the shipping part.
	 * @param store {@link AbstractPdfDataStore}
	 * @param linePrintSection {@link CollectiveDeliveryNoteLinePrintSection}
	 * @param documentContent List of {@link PdfObject}
	 */
	private static void addShippingPart(
			AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store,
			CollectiveDeliveryNoteLinePrintSection linePrintSection, List<PdfObject> documentContent) {

		// shipping part, first page only
		documentContent.add(new PdfRowBuilder(1, SHIPPING_PART_HEIGHT_PT).addElement(new CollDelNoteShippingPart(store)).build());

		// add the three addresses, first page only				
		ArrayList<String> shipToAddresses = new ArrayList<String>();
		ArrayList<String> soldToAddresses = new ArrayList<String>();
		ArrayList<String> billToAddresses = new ArrayList<String>();

		PdfLayoutGenerationTool.addToListIfNotEmpty(shipToAddresses,
				store.getData(DeliveryNoteData.SHIPTO_ADDRESS1),
				store.getData(DeliveryNoteData.SHIPTO_ADDRESS2),
				store.getData(DeliveryNoteData.SHIPTO_ADDRESS3));

		PdfLayoutGenerationTool.addToListIfNotEmpty(soldToAddresses,
				store.getData(DeliveryNoteData.SOLDTO_ADDRESS1),
				store.getData(DeliveryNoteData.SOLDTO_ADDRESS2),
				store.getData(DeliveryNoteData.SOLDTO_ADDRESS3),
				store.getData(DeliveryNoteData.SOLDTO_PHONE),
				store.getData(DeliveryNoteData.SOLDTO_VATREG));

		TextListPart billTo = null;
		TextListPart payer = null;
		PdfRow payerRow = null;
		PdfFont font = new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_9);

		// add billto
		PdfLayoutGenerationTool.addToListIfNotEmpty(billToAddresses,
				store.getData(DeliveryNoteData.BILLTO_ADDRESS1),
				store.getData(DeliveryNoteData.BILLTO_ADDRESS2),
				store.getData(DeliveryNoteData.BILLTO_ADDRESS3));

		billTo = new TextListPart(160, FONT_HEIGHT_11PT, font,
				store.getLabel(DeliveryNoteLabel.ADDRESS_BILLTO) + COLON_SPACE,
				store.getData(DeliveryNoteData.BILLTO_SAP), billToAddresses);

		if (!store.getData(DeliveryNoteData.PAYER_SAP).equals(
				store.getData(DeliveryNoteData.BILLTO_SAP))) {
			// add payer
			PdfLayoutGenerationTool.addToListIfNotEmpty(billToAddresses,
					store.getData(DeliveryNoteData.PAYER_ADDRESS1),
					store.getData(DeliveryNoteData.PAYER_ADDRESS2),
					store.getData(DeliveryNoteData.PAYER_ADDRESS3));

			payer = new TextListPart(160, FONT_HEIGHT_11PT, font,
					store.getLabel(DeliveryNoteLabel.ADDRESS_PAYER) + COLON_SPACE,
					store.getData(DeliveryNoteData.PAYER_SAP), billToAddresses);

			payerRow = new PdfRowBuilder(3, 90, 10, 0).addElement(
					new TextListPart(160, FONT_HEIGHT_11PT, font, EMPTY_STRING, EMPTY_STRING,
							new ArrayList<String>())).addElement(
					new TextListPart(160, FONT_HEIGHT_11PT, font, EMPTY_STRING, EMPTY_STRING,
							new ArrayList<String>())).addElement(payer).build();
		}

		PdfRow shipToSoldToBillToRow = new PdfRowBuilder(3, 90, 10, 0).addElement(
				new TextListPart(160, FONT_HEIGHT_11PT, font,
						store.getLabel(DeliveryNoteLabel.ADDRESS_SHIPTO) + COLON_SPACE,
						store.getData(DeliveryNoteData.SHIPTO_SAP), shipToAddresses)).addElement(
				new TextListPart(160, FONT_HEIGHT_11PT, font,
						store.getLabel(DeliveryNoteLabel.ADDRESS_SOLDTO) + COLON_SPACE,
						store.getData(DeliveryNoteData.SOLDTO_SAP), soldToAddresses)).addElement(
				billTo).build();

		linePrintSection.saveAddressBlock1YCoordinate(shipToSoldToBillToRow);

		documentContent.add(shipToSoldToBillToRow);

		if (payer != null) {
			linePrintSection.saveAddressBlock2YCoordinate(payerRow);
			documentContent.add(payerRow);
		}
	}

	/**
	 * Fills the current page to enforce the next elements to be placed on the next page.
	 * @param document the {@link PdfDocumentWrapper}
	 */
	private static void jumpToNewPage(PdfDocumentWrapper document) {
		document.getContent().add(
				new PdfRowBuilder(1, PdfLayoutGenerationTool.getAvailableSpace(document)).addElement(
						SimplePdfText.create(EMPTY_STRING)).build());
	}

	/**
	 * Adds the list of order items.
	 * @param store {@link AbstractPdfDataStore}
	 * @param orderItemDTOs List if {@link OrderItemDTO}
	 * @param document {@link PdfDocumentWrapper}
	 * @param linePrintSection {@link CollectiveDeliveryNoteLinePrintSection}
	 */
	private static void addOrderItems(
			AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store,
			List<OrderItemDTO> orderItemDTOs, PdfDocumentWrapper document,
			CollectiveDeliveryNoteLinePrintSection linePrintSection) {
		if (orderItemDTOs.size() > 0) {
			int toShowOnThisPage = Math.min(
					(PdfLayoutGenerationTool.getAvailableSpace(document) / ORDER_TABLE_CELL_HEIGHT) - 3,
					orderItemDTOs.size());
			if (toShowOnThisPage > 0) {

				linePrintSection.saveMaterialBlockYCoordinate();
				
				// add space 4pt
				DeliveryNoteLinesSectionUtil.addSpace(document.getContent(), 4);

				List<OrderItemDTO> subList = new ArrayList<OrderItemDTO>(orderItemDTOs.subList(0,
						toShowOnThisPage));
				orderItemDTOs.removeAll(subList);
				CollDelNoteMainLayout.addOrderTable(store, document, subList,
						ORDER_TABLE_CELL_HEIGHT, FONT_HEIGHT_12PT);
				// ad some empty lines if all the elements had been displayed and some space remains
				if (orderItemDTOs.size() == 0
						&& PdfLayoutGenerationTool.getAvailableSpace(document) > 20) {
					document.getContent().add(
							new PdfRowBuilder(1, 20).addElement(SimplePdfText.create(EMPTY_STRING)).build());
				} 
				
			}
		}
	}

	/**
	 * Inserts the material table.
	 * @param store the {@link AbstractPdfDataStore}
	 * @param document the {@link PdfDocumentWrapper}
	 * @param input the list of {@link OrderItemDTO}
	 * @param orderTableCellHeight cell height of the order table
	 * @param headerFontFize font size for the orders table
	 */
		public static void addOrderTable(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store,
				PdfDocumentWrapper document, List<OrderItemDTO> input, int orderTableCellHeight, int headerFontFize) {
			// orders table
			// material item table
			List<SimplePdfText> headers = new ArrayList<SimplePdfText>();
			PdfFont bold = new PdfFont(PdfFontStore.Helvetica_Bold, PdfFontStore.SIZE_9);
			headers.add(new SimplePdfText(store.getLabel(DeliveryNoteLabel.ORDERS_TABLE_DELNOTE), 60, 20).setFont(bold));
			headers.add(new SimplePdfText(store.getLabel(DeliveryNoteLabel.ORDERS_TABLE_SAPNR), 65, 20).setFont(bold));
			headers.add(new SimplePdfText(store.getLabel(DeliveryNoteLabel.ORDERS_TABLE_MATERIAL), 125, 20).setFont(bold));
			headers.add(new SimplePdfText(store.getLabel(DeliveryNoteLabel.ORDERS_TABLE_TRUCK), 55, 20).setFont(bold));
			headers.add(new SimplePdfText(store.getLabel(DeliveryNoteLabel.ORDERS_TABLE_LOAD), 65, 20).setFont(bold));
			headers.add(new SimplePdfText(store.getLabel(DeliveryNoteLabel.ORDERS_TABLE_DELIVERY), 77, 20).setFont(bold));
			SimplePdfText net = new SimplePdfText(store.getLabel(DeliveryNoteLabel.ORDERS_TABLE_NET), 80, 20).setFont(bold);
			PdfLayoutGenerationTool.rightAlignText(net);
			headers.add(net);
			PdfTable materialTable = new PdfTable(headers, headerFontFize);
			materialTable.setLabelProvider(new CollDelNoteOrderTableLabelProvider());
			materialTable.setInput(input);
			// determine table size based on size of input
			int tableHeight = 30;
			tableHeight += (input.size() * orderTableCellHeight);
			PdfRow materialTableRow = new PdfRowBuilder(1, tableHeight, 10, 0).addElement(materialTable).build();
			document.getContent().add(materialTableRow);
		}

	/**
	 * Adds the totals table
	 * @param store the {@link AbstractPdfDataStore}
	 * @param document the {@link PdfDocumentWrapper}
     * @param input the List of {@link DTOMarker}
	 */
	public static void addTotalsTable(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store,
		PdfDocumentWrapper document, List<DTOMarker> input) {
		// totals table
		PdfTable totalsTable = new PdfTable(4);
		CollDelNoteTotalsTableLabelProvider labelProvider = new CollDelNoteTotalsTableLabelProvider(store.getLabel(DeliveryNoteLabel.TOTAL_TEXT));
		totalsTable.setLabelProvider(labelProvider);
		totalsTable.setInput(input);
		int totalsTableHeight = (input.size() + 2)* TOTALS_TABLE_ROW_HEIGHT_PT;
		PdfRow row = new PdfRowBuilder(1, totalsTableHeight, 10, 0).addElement(totalsTable).build();
		document.getContent().add(row);
	}


	/**
	 * Adds the header part
	 * @param parameter {@link GenerateCollectiveDeliveryNoteWithResultParameter}
	 * @param document {@link PdfDocumentWrapper}
	 */
	public static void addCollectiveDeliveryNoteHeader(GenerateCollectiveDeliveryNoteWithResultParameter parameter, PdfDocumentWrapper document) {
		// add logo image
		AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store = parameter.getStore();
		String logoImageName = store.getData(DeliveryNoteData.CRH_LOGO);
		if (!logoImageName.isEmpty()) {
			AbstractPdfRenderer renderer = parameter.getRenderer();
			PdfLayoutGenerationTool.showImage(parameter.getBlobService(), (JcanPdfRenderer) renderer, logoImageName, PdfLayoutGenerationTool.LOGO_PADDING_LEFT, document.getHeight() - PdfLayoutGenerationTool.LOGO_PADDING_TOP, PdfLayoutGenerationTool.LOGO_WIDTH, PdfLayoutGenerationTool.LOGO_HEIGHT);
		}
		
		// address part
		AddressPart addressPart = new AddressPart(store);
		int addressPartHeight = addressPart.getHeightInPoints();
		PdfRow addressPartRow = new PdfRowBuilder(1, addressPartHeight)
			.addElement(addressPart)
			.build();
		document.getContent().add(addressPartRow);
		
		// title part
		TitlePart titlePart = new TitlePart(store, 10, TITLE_PART_HEIGHT_PT);
		PdfRow titleRow = new PdfRowBuilder(1, TITLE_ROW_HEIGHT_PT)
							.addElement(titlePart).build();
		document.getContent().add(titleRow);
	}

}
