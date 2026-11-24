/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.factories.PDFPageFactory;
import com.crh.logon.business.services.beans.pdf.PdfPageSettings;
import com.crh.logon.business.services.beans.pdf.PdfReport;
import com.crh.logon.order.web.utils.WhiteListObjectInputStream;
import com.crh.logon.persistence.dto.BlobDTO;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.servlets.LogonServlet;


/**
 * @author AdNovum Informatik AG
 */
public class PdfGeneratorServlet extends LogonServlet {
    private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG =
			JcanLoggerFactory.getLogger(PdfGeneratorServlet.class);

	private static final String REPORT_ATTRIBUTE_IS_NULL = "PDF generator servlet got a message without report data source";
	private static final String UNDEFINED_TEMPLATE = "PDF generator servlet got a message with undefined template name";
	
	private static final String LOCALHOST = "127.0.0.1";
	private static Set<String> allowedRequestSources = new HashSet<String>();

	private static final Set<Package> allowedPackagesForDeserialization = Stream.of(PdfReport.class.getPackage(),
			BlobDTO.class.getPackage()
	).collect(Collectors.toSet());
	private static final Set<Class> allowedClassesForDeserialization = Stream.of(java.lang.Enum.class,
			java.util.ArrayList.class,
			Locale.class,
			byte[].class,
			int[].class,
			char[].class,
			long[].class,
			double[].class,
			float[].class,
			short[].class
	).collect(Collectors.toSet());
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (verifyRequestSource(request)) {
			transferDataToView(request, response);
		}
	}
	
	public void initLocalHostData() {
		allowedRequestSources.add(LOCALHOST);
		
	    try {
	    	InetAddress localhost = InetAddress.getLocalHost();
	    	if (localhost != null) {
	    		allowedRequestSources.add(localhost.getHostAddress());
	    	}
	    } catch (Exception e) {
	    	LOG.error("Could not look up localhost for pdf generator servlet", e);
	    }
	    	
	    try {
	    	InetAddress[] localLookups = InetAddress.getAllByName("localhost");
	    	
	    	if (localLookups != null) {
		        for (InetAddress inetAddress : localLookups) {
		        	allowedRequestSources.add(inetAddress.getHostAddress());
		        }
	    	}
	    } catch (Exception e) {
	    	LOG.error("Could not look up localhost for pdf generator servlet", e);
	    }
	}
	
	@Override
	public void init(ServletConfig servletconfig) throws ServletException {
		initLocalHostData();
	}
	
	private void transferDataToView(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		PdfReport reportDataSource = null;
		
		if (request.getAttribute(PDFPageFactory.REPORT_DATASOURCE) != null) {
			reportDataSource = (PdfReport) request.getAttribute(PDFPageFactory.REPORT_DATASOURCE);
		}
		else {
			InputStream in = request.getInputStream();
			ObjectInputStream objectInput = new WhiteListObjectInputStream(in,
					allowedClassesForDeserialization, allowedPackagesForDeserialization);

			try {
				reportDataSource = (PdfReport) objectInput.readObject();
			}
			catch (ClassNotFoundException e) {
				LOG.error("Failed to read pdf report from request", e);
			}
			
			request.setAttribute(PDFPageFactory.REPORT_DATASOURCE, reportDataSource);
		}
		
		if (reportDataSource != null) {
			PdfPageSettings pdfPageSettings = reportDataSource.getPageSettings();
			if (pdfPageSettings != null && !StringUtils.empty(pdfPageSettings.getTemplateFileName())) {
				String templateName = pdfPageSettings.getTemplateFileName();
				String templateURL = PDFPageFactory.TEMPLATES_HOME + templateName;

				//System.getProperties().put("pd4ml.extra.resource.loaders", "com.crh.logon.web.pdfgenerator.ImageClassLoader");
				
				Config.set(request.getSession(), Config.FMT_LOCALE, pdfPageSettings.getLocale());
				RequestDispatcher requestDispatcher = request.getRequestDispatcher(templateURL);
				requestDispatcher.forward(request, response);
			}
			else {
				LOG.warn(UNDEFINED_TEMPLATE);
			}
		}
		else {
			LOG.warn(REPORT_ATTRIBUTE_IS_NULL);
		}
	}
	
	private static boolean verifyRequestSource(HttpServletRequest request) {
		return allowedRequestSources.contains(request.getRemoteAddr());
	}
}