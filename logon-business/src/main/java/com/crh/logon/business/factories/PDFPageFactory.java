package com.crh.logon.business.factories;

import java.util.Locale;

import com.crh.logon.business.services.beans.pdf.PdfPageOrientation;
import com.crh.logon.business.services.beans.pdf.PdfPageSettings;
import com.crh.logon.business.services.beans.pdf.PdfPaperSize;

public class PDFPageFactory {

	/**
	 * It's the name of the attribute to transmit datasources to the renderer
	 */
	public static final String REPORT_DATASOURCE = "PDF_REPORT_DATASOURCE";
	
	/**
	 * It's the URL of the report renderer servlet
	 */
	public static final String REPORT_SERVLET_URL = "/pdfGenerator.servlet";
	
	public static final String TEMPLATES_HOME = "/WEB-INF/jsp/pdftemplates/";
	
	/**
	 * It creates a A4 Landscaped report data based on the given templateFileName and outputFileName
	 * @param templateFileName It's the template filename like prescheduling.jsp which must be under /WEB-INF/jsp/pdftemplates/
	 * @param outputFileName It's the generated file name which will be showed for the customer
	 * @return PdfBaseReport
	 */
	public static PdfPageSettings createA4LandscapePage(String templateFileName, String outputFileName, Locale locale){
		return createPage(PdfPaperSize.A4, PdfPageOrientation.LANDSCAPE,  templateFileName,  outputFileName, locale);
	}
	
	public static PdfPageSettings createA4PortraitPage(String templateFileName, String outputFileName, Locale locale){
		return createPage(PdfPaperSize.A4, PdfPageOrientation.PORTRAIT,  templateFileName,  outputFileName, locale);
	}
	
	public static PdfPageSettings createPage(PdfPaperSize pdfPaperSize, PdfPageOrientation pdfPageOrientation,
			String templateFileName, String outputFileName, Locale locale) {
		PdfPageSettings pdfPageSettings = new PdfPageSettings();
		pdfPageSettings.setLocale(locale);
		
		pdfPageSettings.setPageOrientation(pdfPageOrientation);
		pdfPageSettings.setPaperSize(pdfPaperSize);
		
		pdfPageSettings.setTemplateFileName(templateFileName);
		pdfPageSettings.setOutputFileName(outputFileName);
		
		return pdfPageSettings;		
	}
}
