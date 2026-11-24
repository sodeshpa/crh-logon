/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

import org.apache.commons.lang.StringUtils;

import com.crh.logon.pdftool.layout.PdfBorderData;
import com.crh.logon.pdftool.layout.PdfBorderLayout;
import com.crh.logon.pdftool.objects.AbstractPdfObject;
import com.crh.logon.pdftool.objects.PdfObject;
import com.crh.logon.pdftool.objects.PdfRowBuilder;
import com.crh.logon.pdftool.objects.SimplePdfText;
import com.crh.logon.pdftool.renderer.PdfFont;
import com.crh.logon.pdftool.renderer.PdfFontStore;
import com.crh.logon.pdftool.store.AbstractPdfDataStore;

/**
 * The title part consist of the order number + version on the left side,
 * production plant, shipping point on the right side.
 * 
 * @author AdNovum Informatik AG
 */
public class TitlePart extends AbstractPdfObject {
	
	private static final String COLON_SPACE = ": ";
	
	public class OrderNumberPart extends AbstractPdfObject {
		
		public OrderNumberPart(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store) {
			String titleText = store.getLabel(DeliveryNoteLabel.ORDER_NUMBER);
			PdfObject title = new SimplePdfText(titleText).setFont(new PdfFont(PdfFontStore.Helvetica_Bold, PdfFontStore.SIZE_14));
			String orderNrText = store.getData(DeliveryNoteData.ORDER_NR);
			PdfObject orderNr = new SimplePdfText(orderNrText).setFont(new PdfFont(PdfFontStore.Helvetica_Bold, PdfFontStore.SIZE_14));
			orderNr.setLayoutData(new PdfBorderData(PdfBorderData.HOrientation.RIGHT));
			
			// Export Invoice Changes for Eastern Europe - show "Issue Date" instead of version
			String versionDateText;
			final boolean isExportInvoice = Boolean.valueOf(store.getData(DeliveryNoteData.IS_EXPORT_INVOICE));
			String issueLocation = store.getData(DeliveryNoteData.EXPORT_INVOICE_ISSUE_LOCATION);
			// Only need to display "Issue Date" instead of "Version" when issueLocation is set.
			if (isExportInvoice && !StringUtils.isEmpty(issueLocation)) {
				versionDateText = store.getLabel(DeliveryNoteLabel.EXPORT_INVOICE_ISSUE_DATE)
						+ COLON_SPACE + store.getData(DeliveryNoteData.DELIVERY_NOTE_DATE);
			}
			else {
				versionDateText = store.getLabel(DeliveryNoteLabel.VERSION) + COLON_SPACE
						+ store.getData(DeliveryNoteData.DELIVERY_NOTE_DATE);
			}
			
			PdfObject version = new SimplePdfText(versionDateText).setFont(new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_7));
			String altOrderNumberText = store.getData(DeliveryNoteData.ORDER_NR_ALT) + " " + store.getData(DeliveryNoteData.ORDER_NR_ALT2);
			PdfObject altOrderNumber = new SimplePdfText(altOrderNumberText).setFont(new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_7));
			altOrderNumber.setLayoutData(new PdfBorderData(PdfBorderData.HOrientation.RIGHT));
			
			setLayout(new PdfBorderLayout(1));
			// note: the actual width seems to be the minimum of the two (probably because
			// the elements are connected through the PdfRowBuilder?), thus we use the same width
			// if we have the widest possible values (12 Ws) for both fields they overlap, but
			// that is not a realistic case (and still better than cutting off parts or not 
			// displaying the number)
			int width = 300;
			title.setWidth(width);
			orderNr.setWidth(width);
			getChildren().add(new PdfRowBuilder(2, 17).addElement(title).
					addElement(orderNr).build());
			getChildren().add(new PdfRowBuilder(2, 9).addElement(version).
					addElement(altOrderNumber).build());
			
			// Export Invoice Changes for Eastern Europe - show "Issue Location"
			if (isExportInvoice && !StringUtils.isEmpty(issueLocation)) {
				issueLocation = store.getLabel(DeliveryNoteLabel.EXPORT_INVOICE_ISSUE_LOCATION)
						+ COLON_SPACE + issueLocation;
				final PdfObject issueLocationPdfObject = new SimplePdfText(issueLocation).setFont(new PdfFont(
						PdfFontStore.Helvetica, PdfFontStore.SIZE_7)); // same font + size as "version".
				issueLocationPdfObject.setWidth(width);
				getChildren().add(
						new PdfRowBuilder(1, 9).addElement(issueLocationPdfObject).build());
			}
		}
	}
	
	public class ShippingPointPart extends AbstractPdfObject {
		
		public ShippingPointPart(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store) {
			setLayout(new PdfBorderLayout(1));
			getChildren().add(new PdfRowBuilder(2, 11)
					.addElement(new SimplePdfText(store.getLabel(DeliveryNoteLabel.PLANT) + COLON_SPACE, 90, 11))
					.addElement(adjustFont(new SimplePdfText(store.getData(DeliveryNoteData.PLANT), 140, 11)))
					.build());
			getChildren().add(new PdfRowBuilder(2, 11)
			.addElement(new SimplePdfText(store.getLabel(DeliveryNoteLabel.SHIPPING_POINT) + COLON_SPACE, 90, 11))
			.addElement(adjustFont(new SimplePdfText(store.getData(DeliveryNoteData.SHIPPING_POINT), 140, 11)))
			.build());
		}

		/**
		 * @param simplePdfText
		 * @return
		 */
		private PdfObject adjustFont(SimplePdfText simplePdfText) {
			return simplePdfText.getText().length() > 28 ? simplePdfText.setFont(new PdfFont(PdfFontStore.Helvetica, PdfFontStore.SIZE_8)) : simplePdfText;
		}
		
	}
	
	public TitlePart(AbstractPdfDataStore<DeliveryNoteLabel, DeliveryNoteData> store, int padding, int height) {
		setLayout(new PdfBorderLayout(1));
		getChildren().add(new PdfRowBuilder(2, height, padding, 0)
		.addElement(new OrderNumberPart(store))
		.addElement(new ShippingPointPart(store)).build());
	};

}
