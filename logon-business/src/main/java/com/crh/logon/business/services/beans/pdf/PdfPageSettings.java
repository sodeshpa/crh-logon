package com.crh.logon.business.services.beans.pdf;

import java.io.Serializable;
import java.util.Locale;

public class PdfPageSettings implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * This will be the generated file name
	 */
	private String outputFileName;
	
	private Locale locale;
	
	/**
	 * This is the template filename which will be used by the pdfGenerator 
	 */
	private String templateFileName;
	
	/**
	 * This is for the orientation of the exported page
	 */
	private PdfPageOrientation pageOrientation;
	
	/**
	 * It defines the paper size of the exported pdf
	 */
	private PdfPaperSize paperSize;
	
	private Locale pdfLanguage;
	
	
	/* (non-Javadoc)
	 * @see PdfReport#getTemplateFileName()
	 */
	public String getTemplateFileName() {
		return templateFileName;
	}

	/* (non-Javadoc)
	 * @see PdfReport#setTemplateFileName(java.lang.String)
	 */
	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
	}

	/* (non-Javadoc)
	 * @see PdfReport#getPageOrientation()
	 */
	public PdfPageOrientation getPageOrientation() {
		return pageOrientation;
	}

	/* (non-Javadoc)
	 * @see PdfReport#setPageOrientation(PdfPageOrientation)
	 */
	public void setPageOrientation(PdfPageOrientation pageOrientation) {
		this.pageOrientation = pageOrientation;
	}

	/* (non-Javadoc)
	 * @see PdfReport#getPaperSize()
	 */
	public PdfPaperSize getPaperSize() {
		return paperSize;
	}

	/* (non-Javadoc)
	 * @see PdfReport#setPaperSize(PdfPaperSize)
	 */
	public void setPaperSize(PdfPaperSize paperSize) {
		this.paperSize = paperSize;
	}

	/* (non-Javadoc)
	 * @see PdfReport#getOutputFileName()
	 */
	public String getOutputFileName() {
		return outputFileName;
	}

	/* (non-Javadoc)
	 * @see PdfReport#setOutputFileName(java.lang.String)
	 */
	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public Locale getPdfLanguage() {
		return pdfLanguage;
	}

	public void setPdfLanguage(Locale pdfLanguage) {
		this.pdfLanguage = pdfLanguage;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}	
}
