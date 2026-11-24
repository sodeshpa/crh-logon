/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.order.web.utils.pdfgenerator;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;

import com.crh.logon.business.factories.PDFPageFactory;
import com.crh.logon.business.services.beans.pdf.PdfReport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

public class PdfGenerator {
	/**
	 * It's a null which is needed for resulting the pdf through struts. It has to be returned otherwise
	 * struts will try to write on the OutputStream which has been closed by the generatePdf method because 
	 * the pdf has been sent through
	 */
	public static final ActionForward DO_NOT_FORWARD = null;
	
	private static final JcanLogger LOG =
			JcanLoggerFactory.getLogger(PdfGenerator.class);

	
	/**
	 * It calls the pdf generator servlet and produce the pdf itself on the output stream
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param reportDataSource The dataSource of the report which will be transmitted to the template renderer 
	 */
	public static void generateDownloadPdf(HttpServletRequest request, HttpServletResponse response, PdfReport reportDataSource) {
		try {
			request.setAttribute(PDFPageFactory.REPORT_DATASOURCE, reportDataSource);
			request.getRequestDispatcher(PDFPageFactory.REPORT_SERVLET_URL).forward(request, response);
		} catch (ServletException servletException) {
			LOG.warn(servletException.getMessage());
		} catch (IOException ioException) {
			LOG.warn(ioException.getMessage());			
		} finally {
			try {
				OutputStream pdfOutputStream = response.getOutputStream();
				pdfOutputStream.close();
				pdfOutputStream.flush();
			} catch (IOException ioException) {
				LOG.warn(ioException.getMessage());
			}
		}
	}
}