/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.util.Locale;

import com.crh.logon.business.services.beans.pdf.PdfPageSettings;
import com.crh.logon.business.factories.PDFPageFactory;

/**
 * @author AdNovum Informatik AG
 */
public class DeliveryNoteDataFactory {
	public static String DELIVERY_NOTE_TEMPLATE_NAME = "deliverynote.jsp";

	public static DeliveryNotePdfDataSource createDeliveryNoteBase(Locale locale) {
		DeliveryNotePdfDataSource deliveryNotePdfDataSource = new DeliveryNotePdfDataSource();

		PdfPageSettings pdfPageSettings = PDFPageFactory.createA4LandscapePage(DELIVERY_NOTE_TEMPLATE_NAME, "", locale);
		deliveryNotePdfDataSource.setPageSettings(pdfPageSettings);
		
		return deliveryNotePdfDataSource;
	}

}
