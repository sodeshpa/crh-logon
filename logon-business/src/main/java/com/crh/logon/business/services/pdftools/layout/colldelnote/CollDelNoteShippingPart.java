/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.colldelnote;

import com.crh.logon.business.services.pdftools.layout.DeliveryNoteData;
import com.crh.logon.business.services.pdftools.layout.DeliveryNoteLabel;
import com.crh.logon.pdftool.layout.PdfBorderLayout;
import com.crh.logon.pdftool.objects.AbstractPdfObject;
import com.crh.logon.pdftool.objects.PdfRowBuilder;
import com.crh.logon.pdftool.objects.SimplePdfText;
import com.crh.logon.pdftool.renderer.PdfFont;
import com.crh.logon.pdftool.renderer.PdfFontStore;
import com.crh.logon.pdftool.store.AbstractPdfDataStore;

/**
 * Shipping part layout element for collective delivery note. Distance and Delivery time window
 * are displayed if corresponding control flags are set to <code>true</code>
 * @author AdNovum Informatik AG
 */
public class CollDelNoteShippingPart extends AbstractPdfObject {
	
	/**
	 * A colon and a space character
	 */
	private static final String COLON_SPACE = ": ";
	
	/**
	 * Constructor
	 * @param store {@link AbstractPdfDataStore}
	 */
	public CollDelNoteShippingPart(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store) {
		setLayout(new PdfBorderLayout(2));
		PdfFont font = new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_9);
		
		if (!Boolean.valueOf(store.getData(DeliveryNoteData.HIDE_DEL_WINDOW))) {			
			// delivery window
			SimplePdfText deliveryWindow = SimplePdfText.create("").setFont(font);
			if (!store.getData(DeliveryNoteData.DELIVERY_WINDOW).isEmpty()) {
				deliveryWindow.setText(store.getLabel(DeliveryNoteLabel.DELIVERY_WINDOW)+ COLON_SPACE + store.getData(DeliveryNoteData.DELIVERY_WINDOW));
			} 
			getChildren().add(new PdfRowBuilder(1, 12, 10, 0).addElement(deliveryWindow).build());
		}
		if (!Boolean.valueOf(store.getData(DeliveryNoteData.HIDE_DISTANCE))) {			
			// distance
			SimplePdfText distanceLabel = new SimplePdfText("", 90, 12).setFont(font);
			SimplePdfText distanceData = new SimplePdfText("", 140, 12).setFont(font);
			if (!store.getData(DeliveryNoteData.DISTANCE).isEmpty()) {				
				distanceLabel.setText(store.getLabel(DeliveryNoteLabel.DISTANCE) + COLON_SPACE);
				distanceData.setText(store.getData(DeliveryNoteData.DISTANCE) + " " + store.getLabel(DeliveryNoteLabel.KM));
			}
			getChildren().add(new PdfRowBuilder(2, 12, 10, 0).addElement(distanceLabel).addElement(distanceData).build());
		}
		
		// contract
		SimplePdfText contract = SimplePdfText.create(store.getData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_TILE) + " " + store.getData(DeliveryNoteData.CUST_REF_CONTRACT_NUMBER_DATA)).setFont(font);
			getChildren().add(new PdfRowBuilder(1, 38, 10, 0)
									.addElement(contract)
									.build());
			
		// incoterm and delivery type
		SimplePdfText incotermLabel = new SimplePdfText(store.getLabel(DeliveryNoteLabel.INCOTERM) + COLON_SPACE, 90, 12).setFont(font);
		SimplePdfText incotermData = new SimplePdfText(store.getData(DeliveryNoteData.INCOTERM) + " / " + store.getData(DeliveryNoteData.DELIVERY_TYPE), 140, 12).setFont(font);
			getChildren().add(new PdfRowBuilder(2, 38, 10, 0)
									.addElement(incotermLabel)
									.addElement(incotermData)
									.build());
		
	}

}
