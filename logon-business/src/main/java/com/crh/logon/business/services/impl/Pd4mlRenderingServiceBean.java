/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.pdf.PdfReport;
import com.crh.logon.business.services.dispatchers.Pd4mlRenderingServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.Pd4mlRenderingServiceLocalInterface;

/**
 * @author AdNovum Informatik AG
 */
@AdnTrace
@Alternative
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors(UserInfoInterceptor.class)
public class Pd4mlRenderingServiceBean extends Pd4mlRenderingServiceDispatcher
	implements Pd4mlRenderingServiceLocalInterface {
	
	@EJB
	ConfigServiceLocalInterface configService;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(Pd4mlRenderingServiceBean.class);
	
	public ByteArrayOutputStream renderPdf(PdfReport pdfReport) {
		ByteArrayOutputStream pdfOutStream = null;
		HttpURLConnection connection = null;
		
		if (pdfReport == null) {
			return null;
		}
		
		try {
			connection = initRenderingConnection();
			
			if (connection != null) {
				if (writeDataSourceToConnection(pdfReport, connection)) {
					pdfOutStream = readPdfFromConnection(connection);
					
					return pdfOutStream;
				}
			}
		}
		catch (Exception e) {
			LOG.error("Exception while rendering pdf", e);
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		
		return null;
	}
	
	private HttpURLConnection initRenderingConnection() {
		String generatorServletUrl = configService.getConfigBean().getPd4mlServletUrl();
		
		try {
			URL url = new URL(generatorServletUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			return connection;
		}
		catch (MalformedURLException e) {
			LOG.error("MalformedURLException", e);
		}
		catch (ProtocolException e) {
			LOG.error("ProtocolException", e);
		}
		catch (IOException e) {
			LOG.error("IOException", e);
		}
		
		return null;
	}
	
	private boolean writeDataSourceToConnection(PdfReport pdfReport, HttpURLConnection connection){
		try {
			OutputStream out = connection.getOutputStream();
			ObjectOutputStream objectOutput = new ObjectOutputStream(out);
			
			objectOutput.writeObject(pdfReport);
			objectOutput.close();
			return true;
		}
		catch (IOException e) {
			LOG.error("IOException", e);
		}
		
		return false;
	}
	
	private ByteArrayOutputStream readPdfFromConnection(HttpURLConnection connection) {
		try {
			InputStream in = connection.getInputStream();
			byte[] buffer = new byte[1024];
			int count;
			ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
			
			while ((count = in.read(buffer)) != -1) {
				byteOutput.write(buffer, 0, count);
			}
			
			byteOutput.flush();
			byteOutput.close();
			in.close();
			return byteOutput;
		}
		catch (IOException e) {
			LOG.error("IOException", e);
		}
		
		return null;
	}
}
