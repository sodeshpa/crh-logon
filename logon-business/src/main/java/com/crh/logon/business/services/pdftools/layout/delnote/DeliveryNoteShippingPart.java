/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.delnote;

import com.crh.logon.business.services.pdftools.layout.DeliveryNoteData;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteLabel;
import com.crh.logon.business.services.pdftools.layout.JcanPdfRenderer;
import com.crh.logon.constants.Constant;
import com.crh.logon.pdftool.layout.PdfBorderData;
import com.crh.logon.pdftool.layout.PdfBorderData.HOrientation;
import com.crh.logon.pdftool.layout.PdfBorderLayout;
import com.crh.logon.pdftool.objects.AbstractPdfObject;
import com.crh.logon.pdftool.objects.PdfObject;
import com.crh.logon.pdftool.objects.PdfRowBuilder;
import com.crh.logon.pdftool.objects.PdfRowBuilder.PdfRow;
import com.crh.logon.pdftool.objects.SimplePdfText;
import com.crh.logon.pdftool.renderer.PdfFont;
import com.crh.logon.pdftool.renderer.PdfFontStore;
import com.crh.logon.pdftool.store.AbstractPdfDataStore;
import org.apache.commons.lang.StringUtils;

/**
 * The shipping part of a delivery note. It contains the truck/rail numbers,
 * contract number, distance and delivery time window on the left side load and 
 * delivery times on the right side. Displaying distance and delivery time window
 * depends on the control flags.
 * 
 * @author AdNovum Informatik AG
 */
public class DeliveryNoteShippingPart extends AbstractPdfObject {
	
	private static final String COLON_SPACE = ": ";
	public final int defaultRowHeight = 11;
	private static final PdfFont helvetica_9 = new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_9);
	private static final PdfFont helvetica_9_bold = new PdfFont(PdfFontStore.Helvetica_Bold, PdfFontStore.SIZE_9);
	
	NumberParts numberParts = null;
	LoadDelTimePart loadDelTimePart = null;
	
	private class NumberParts extends AbstractPdfObject {		
		
		public NumberParts(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store) {
			setLayout(new PdfBorderLayout(1));
			final boolean isExportInvoice = Boolean.valueOf(store.getData(DeliveryNoteData.IS_EXPORT_INVOICE));
									
			if (!store.getData(DeliveryNoteData.TRUCK_PLATE_NR).isEmpty()) {
				String truckPlate = store.getLabel(DeliveryNoteLabel.TRUCK_PLATE_NO) + COLON_SPACE + store.getData(DeliveryNoteData.TRUCK_PLATE_NR);
				getChildren().add(SimplePdfText.create(truckPlate).setFont(helvetica_9));
			}

			// the trailer plate and the controller number has to be in one line.
			PdfRow trailerPlateAndControllerNoRow = createTrailerPlateAndControllerNoRow(store, isExportInvoice);
			if (trailerPlateAndControllerNoRow != null){
				getChildren().add(trailerPlateAndControllerNoRow);
			}			
			
			if (!store.getData(DeliveryNoteData.WAGON_NR).isEmpty()) {
				String wagonNr = store.getLabel(DeliveryNoteLabel.WAGON_NO) + COLON_SPACE + store.getData(DeliveryNoteData.WAGON_NR);
				getChildren().add(SimplePdfText.create(wagonNr).setFont(helvetica_9));
			}
 			if (!store.getData(DeliveryNoteData.CONTAINER_NR).isEmpty()) {
				String containerNr = store.getLabel(DeliveryNoteLabel.CONTAINER_NO) + COLON_SPACE + store.getData(DeliveryNoteData.CONTAINER_NR);
				getChildren().add(SimplePdfText.create(containerNr).setFont(helvetica_9));
			}

			// show distance if exists. if any kind of nr data exists, append
			if (!Boolean.valueOf(store.getData(DeliveryNoteData.HIDE_DISTANCE))) {				
				if (!store.getData(DeliveryNoteData.DISTANCE).isEmpty()) {				
					String distanceString = store.getLabel(DeliveryNoteLabel.DISTANCE) 
							+ COLON_SPACE + store.getData(DeliveryNoteData.DISTANCE) + " " + store.getLabel(DeliveryNoteLabel.KM);
					SimplePdfText distance = SimplePdfText.create(distanceString).setFont(helvetica_9_bold);
					distance.setLayoutData(new PdfBorderData(HOrientation.RIGHT));
					if (getChildren().isEmpty()) {
						getChildren().add(distance);
					} else {
						PdfObject first = getChildren().get(0);
						first.setWidth(145);
						distance.setWidth(155);
						getChildren().remove(0);
						getChildren().add(0, new PdfRowBuilder(2, defaultRowHeight).addElement(first)
								.addElement(distance).build());
					}
				}
			}
			
			
			// delivery window
			if (!Boolean.valueOf(store.getData(DeliveryNoteData.HIDE_DEL_WINDOW))) {				
				String dwLabel = store.getLabel(DeliveryNoteLabel.DELIVERY_WINDOW);
				String dwData = store.getData(DeliveryNoteData.DELIVERY_WINDOW);
				if (!dwData.isEmpty()) {
					SimplePdfText deliveryWindow = null;
					deliveryWindow = SimplePdfText.create(dwLabel + COLON_SPACE	+ dwData);
					deliveryWindow.setFont(new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_7));
					
					getChildren()
						.add(new PdfRowBuilder(1, defaultRowHeight)
							.addElement(deliveryWindow)
							.build());
				}
			}
			
			// contract info
 			if (!store.getData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_TILE).isEmpty()) {
				SimplePdfText contract = SimplePdfText.create(store.getData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_TILE) + " " + store.getData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_DATA)).setFont(helvetica_9);
				getChildren().add(new PdfRowBuilder(1, defaultRowHeight)
										.addElement(contract)
										.build());
			}
			
			// Advance Payment Invoice numbers
			String advanceInvoiceNumbers = store.getData(DeliveryNoteData.EXPORT_INVOICE_ADVANCE_PAYMENT_INVOICE_NUMBER);
			if (isExportInvoice && !advanceInvoiceNumbers.isEmpty()) {
				advanceInvoiceNumbers = store.getLabel(DeliveryNoteLabel.EXPORT_INVOICE_ADVANCE_PAYMENT_INVOICE_NUMBER)
						+ COLON_SPACE + advanceInvoiceNumbers;
				// Shorten the advance invoice numbers in order to fit in (otherwise won't be displayed at all).
				advanceInvoiceNumbers = StringUtils.abbreviate(advanceInvoiceNumbers,
						Constant.EXP_INV_ADVANCE_INVOICE_NUMBER_MAX_WIDTH);
				final SimplePdfText advancePaymentIvoiceSimplePdfText = SimplePdfText.create(
						advanceInvoiceNumbers).setFont(
						helvetica_9);
				getChildren().add(
						new PdfRowBuilder(1, 11).addElement(advancePaymentIvoiceSimplePdfText).build());
			}
			
			if(!store.getData(DeliveryNoteData.CARRIER_CRM).isEmpty() || !store.getData(DeliveryNoteData.SEALS_NUMBERS).isEmpty()){
				getChildren().add(SimplePdfText.create("", defaultRowHeight));
				
				if(!store.getData(DeliveryNoteData.SEALS_NUMBERS).isEmpty()){
					String sealsNum = store.getLabel(DeliveryNoteLabel.SEALS_NUMBERS) + COLON_SPACE + store.getData(DeliveryNoteData.SEALS_NUMBERS);
					getChildren().add(SimplePdfText.create(sealsNum).setFont(helvetica_9));
				}
				if(!store.getData(DeliveryNoteData.CARRIER_CRM).isEmpty() && !store.getData(DeliveryNoteData.CROSSBOUNDARY).isEmpty()){
					String carrierCRM = store.getLabel(DeliveryNoteLabel.CARRIER_CRM) + COLON_SPACE + store.getData(DeliveryNoteData.CARRIER_CRM);
					getChildren().add(SimplePdfText.create(carrierCRM).setFont(helvetica_9));
				}
			}			
			
		}

		private PdfRow createTrailerPlateAndControllerNoRow(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store,
				                                     final boolean isExportInvoice) {
			
			String controllerNumberString = store.getData(DeliveryNoteData.EXPORT_INVOICE_CONTROLLER_NUMBER);
			String trailerPlateString = store.getData(DeliveryNoteData.TRAILER_PLATE_NR);
			SimplePdfText controllerNumber = null;
			SimplePdfText trailerPlate = null;
			final boolean trailerPlateExists = !trailerPlateString.isEmpty();
			final boolean printControllerNo = isExportInvoice && !controllerNumberString.isEmpty();
			
			if(trailerPlateExists){
				trailerPlateString = store.getLabel(DeliveryNoteLabel.TRAILER_PLATE_NO) 
						+ COLON_SPACE + trailerPlateString;
				trailerPlate = SimplePdfText.create(trailerPlateString);
				trailerPlate.setFont(helvetica_9);
			}						
			
			if (printControllerNo) {
				controllerNumberString = store.getLabel(DeliveryNoteLabel.EXPORT_INVOICE_CONTROLLER_NUMBER)
						+ COLON_SPACE + controllerNumberString;
				controllerNumber = SimplePdfText.create(controllerNumberString);
				controllerNumber.setFont(helvetica_9);	
				controllerNumber.setLayoutData(new PdfBorderData(HOrientation.RIGHT));
			}
			
			if (trailerPlateExists && printControllerNo){
				int trailerPlateWidth = 129;
				int controllerNoWidth = 118;
				trailerPlate.setWidth(trailerPlateWidth);
				controllerNumber.setWidth(controllerNoWidth);
				JcanPdfRenderer.cutTextToWidth(trailerPlate, trailerPlateWidth);
				JcanPdfRenderer.cutTextToWidth(controllerNumber, controllerNoWidth);	
				
				return new PdfRowBuilder(2, defaultRowHeight)
							.addElement(trailerPlate)
							.addElement(controllerNumber)
							.build();
			} 
			else {
				if (trailerPlateExists) {

					return new PdfRowBuilder(1, defaultRowHeight)
								.addElement(trailerPlate)
								.build();
				}
				else if (printControllerNo){

					return new PdfRowBuilder(1, defaultRowHeight)
								.addElement(controllerNumber)
								.build();
				}
			}
			
			return null;
		}
	}
	
	private class LoadDelTimePart extends AbstractPdfObject {
		
		public LoadDelTimePart(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store) {
			setLayout(new PdfBorderLayout(1));
			
			SimplePdfText deliverySiloLabel = new SimplePdfText(store.getLabel(DeliveryNoteLabel.DELIVERY_SILO) + COLON_SPACE, 90, defaultRowHeight);
			SimplePdfText deliverySiloData = new SimplePdfText(store.getData(DeliveryNoteData.DELIVERY_SILO), 140, defaultRowHeight);
			
			SimplePdfText plantEntryLabel = new SimplePdfText(store.getLabel(DeliveryNoteLabel.PLANT_ENTRY) + COLON_SPACE, 90, defaultRowHeight);
			SimplePdfText plantEntryData = new SimplePdfText(store.getData(DeliveryNoteData.PLANT_ENTRY), 140, defaultRowHeight);
			
			SimplePdfText plantExitLabel = new SimplePdfText(store.getLabel(DeliveryNoteLabel.PLANT_EXIT) + COLON_SPACE, 90, defaultRowHeight);
			SimplePdfText plantExitData = new SimplePdfText(store.getData(DeliveryNoteData.PLANT_EXIT), 140, defaultRowHeight);
			
			SimplePdfText loadDeliveryNoteLabel = new SimplePdfText(store.getLabel(DeliveryNoteLabel.ENDLOAD_DATE) + COLON_SPACE, 90, defaultRowHeight);
			SimplePdfText loadDeliveryNoteData = new SimplePdfText(store.getData(DeliveryNoteData.LOAD_DATE), 140, defaultRowHeight);
			
			SimplePdfText deliveryDeliveryNoteLabel = new SimplePdfText(store.getLabel(DeliveryNoteLabel.DELIVERY_TIME) + COLON_SPACE, 90, defaultRowHeight);
			SimplePdfText deliveryDeliveryNoteData = new SimplePdfText(store.getData(DeliveryNoteData.DELIVERY_DATE), 140, defaultRowHeight);
			
			if(Boolean.valueOf(store.getData(DeliveryNoteData.IS_BULK)) && StringUtils.isNotEmpty(store.getData(DeliveryNoteData.DELIVERY_SILO))){
				getChildren().add(new PdfRowBuilder(2, defaultRowHeight)
					.addElement(deliverySiloLabel)
					.addElement(deliverySiloData).build());
			}
			if (StringUtils.isNotEmpty(store.getData(DeliveryNoteData.PLANT_ENTRY))){
				getChildren().add(new PdfRowBuilder(2, defaultRowHeight)
					.addElement(plantEntryLabel)
					.addElement(plantEntryData).build());
			}
			getChildren().add(new PdfRowBuilder(2, defaultRowHeight)
				.addElement(loadDeliveryNoteLabel)
				.addElement(loadDeliveryNoteData).build());
			if (StringUtils.isNotEmpty(store.getData(DeliveryNoteData.PLANT_EXIT))){
				getChildren().add(new PdfRowBuilder(2, defaultRowHeight)
					.addElement(plantExitLabel)
					.addElement(plantExitData).build());
			}
			getChildren().add(new PdfRowBuilder(2, defaultRowHeight)
				.addElement(deliveryDeliveryNoteLabel)
				.addElement(deliveryDeliveryNoteData).build());
			
			// incoterm
			SimplePdfText incotermLabel = new SimplePdfText(store.getLabel(DeliveryNoteLabel.INCOTERM) + COLON_SPACE, 90, defaultRowHeight);
			SimplePdfText incotermData = new SimplePdfText(store.getData(DeliveryNoteData.INCOTERM) + " / " + store.getData(DeliveryNoteData.DELIVERY_TYPE), 140, defaultRowHeight);
				getChildren().add(new PdfRowBuilder(2, defaultRowHeight)
										.addElement(incotermLabel)
										.addElement(incotermData)
										.build());
			// reason of transport
			SimplePdfText reasonOfTransportLabel = new SimplePdfText(store.getLabel(DeliveryNoteLabel.REASON_OF_TRANSPORT) + COLON_SPACE, 90, defaultRowHeight);
			SimplePdfText reasonOfTransportData = new SimplePdfText(store.getData(DeliveryNoteData.REASON_OF_TRANSPORT), 140, defaultRowHeight);
				getChildren().add(new PdfRowBuilder(2, defaultRowHeight)
										.addElement(reasonOfTransportLabel)
										.addElement(reasonOfTransportData)
										.build());
				
			// Cash Sales Invoce Form - payment method
			final String paymentMethod = store.getData(DeliveryNoteData.CASH_SALES_PAYMENT_METHOD);
			if (paymentMethod != null && !paymentMethod.isEmpty()) {
				SimplePdfText paymentMethodLabel = new SimplePdfText(store.getLabel(DeliveryNoteLabel.PAYMENT_METHOD) + COLON_SPACE, 90, defaultRowHeight);
				SimplePdfText paymentMethodValue = new SimplePdfText(paymentMethod, 140, defaultRowHeight);
				getChildren().add(new PdfRowBuilder(2, defaultRowHeight)
						.addElement(paymentMethodLabel)
						.addElement(paymentMethodValue).build());
			}
		}
	}
	
	public DeliveryNoteShippingPart(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store) {
		numberParts = new NumberParts(store);
		loadDelTimePart = new LoadDelTimePart(store);
		setLayout(new PdfBorderLayout(1));
		
		getChildren()
			.add(new PdfRowBuilder(2, getNumOfRows() * defaultRowHeight, 10, 0)
				.addElement(numberParts)
				.addElement(loadDelTimePart)
				.build());
	}

	/**
	 * Returns the height of the whole DeliveryNoteShippingPart. 
	 * This number is max(LoadDelTimePart.height, NumberParts.height) in rows.
	 * @return height in rows
	 */
	public int getNumOfRows(){
		// the additional plus ones are necessary to make sure there is an empty line at the bottom
		return Math.max(loadDelTimePart.getChildren().size() + 1, numberParts.getChildren().size() + 1);
	}
}
