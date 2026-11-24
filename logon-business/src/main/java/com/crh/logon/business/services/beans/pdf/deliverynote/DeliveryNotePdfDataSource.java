/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import com.crh.logon.business.services.beans.pdf.PdfPageSettings;
import com.crh.logon.business.services.beans.pdf.PdfReport;

/**
 * @author AdNovum Informatik AG
 */
public class DeliveryNotePdfDataSource implements PdfReport {
	private static final long serialVersionUID = -6601031669821164205L;

	private PdfPageSettings pdfPageSettings;
	
	private HeaderData header;
	private BodyData body;
	private FooterData footer;
	
	public HeaderData getHeader() {
		return header;
	}


	public void setHeader(HeaderData header) {
		this.header = header;
	}


	public BodyData getBody() {
		return body;
	}


	public void setBody(BodyData body) {
		this.body = body;
	}


	public FooterData getFooter() {
		return footer;
	}


	public void setFooter(FooterData footer) {
		this.footer = footer;
	}


	@Override
	public PdfPageSettings getPageSettings() {
		return pdfPageSettings;
	}


	@Override
	public void setPageSettings(PdfPageSettings pdfPageSettings) {
		this.pdfPageSettings = pdfPageSettings;
	}

}
