/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ipp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Iterator;

import javax.print.Doc;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobListener;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class IppDocPrintJob implements DocPrintJob {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(IppDocPrintJob.class);
	
	private IppPrintService printService;
	private URI uri;
	private static final String IPP_MIME_TYPE = "application/ipp";
	private static final int IPP_PORT = 631;
	private static final String DEFAULT_URI_SCHEME = "http";
	
	IppDocPrintJob(IppPrintService printService, URI uri) {
		
		this.printService = printService;
		this.uri = uri;
	}

	public void addPrintJobAttributeListener(PrintJobAttributeListener listener,
			PrintJobAttributeSet attributes) {
		// TODO Auto-generated method stub
		
	}

	public void addPrintJobListener(PrintJobListener listener) {
		// TODO Auto-generated method stub
		
	}

	public PrintJobAttributeSet getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	public PrintService getPrintService() {
		
		return printService;
	}
	
	private void setHttpHeaders(HttpURLConnection conn, int length) throws PrintException {
		
		try {
			conn.setRequestMethod("POST");
		}
		catch (ProtocolException e) {
			throw new PrintException(e);
		}
		conn.setRequestProperty("Host", conn.getURL().getHost() + ":" + conn.getURL().getPort());
		conn.setRequestProperty("Date", new Date().toString());
		conn.setRequestProperty("Content-Type", IPP_MIME_TYPE);
		conn.setRequestProperty("Content-Length", "" + length);
		if (LOG.isDebugEnabled()) {
			
			LOG.debug("HTTP Headers: host: " + conn.getRequestProperty("Host") + " Content-Length: " + conn.getRequestProperty("Content-Length"));
		}
	}
	
	private IppResponse sendRequest(URI uri, byte[] ippBuf, byte[] docBuf) throws IOException, PrintException {
		
		if (ippBuf == null) {
			return null;
		}

		if (uri == null) {
			return null;
		}
		
		URI destUri;
		try {
			destUri = new URI((uri.getScheme().equals("ipp")?DEFAULT_URI_SCHEME:uri.getScheme()), uri.getUserInfo(), uri.getHost(), uri.getPort()==-1?IPP_PORT:uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
		}
		catch (URISyntaxException e) {
			throw new PrintException(e);
		}
		HttpURLConnection conn = null;
		IppResponse response = null;
		try {
			conn = (HttpURLConnection)destUri.toURL().openConnection();
			conn.setDoOutput(true);
			conn.setAllowUserInteraction(false);
			
			if (docBuf != null) {
				setHttpHeaders(conn, ippBuf.length + docBuf.length);
			}
			else {
				setHttpHeaders(conn, ippBuf.length);
			}
			
			conn.connect();
			OutputStream httpOut = conn.getOutputStream();
			httpOut.write(ippBuf);
			httpOut.write(docBuf);
			
			response = IppResponseHandler.getContent(conn);
		}
		finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		
		
		return response;

	}
	
	
	private void handlResponse(IppResponse response) throws PrintException {
		
		if (response == null) {
			
			throw new PrintException("Job failed: no response");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("HTTP status code: " + response.getHttpStatusCode());
			LOG.debug("HTTP headers:");
			for (Iterator it=response.getHttpHeaders().iterator();it.hasNext();) {
				String header = (String)it.next();
				LOG.debug(header);
			}
			LOG.debug("IPP info:");
			LOG.debug("Version major: " + response.getVersionMajor());
			LOG.debug("Version minor: " + response.getVersionMinor());
			LOG.debug("Status: " + response.getStatusCode() + " " + response.isSuccessful());
		}
		if (!response.isSuccessful()) {
			LOG.error("Job failed with HTTP statusline: " + (response.getHttpHeaders().size()>0?response.getHttpHeaders().get(0):"unknown") + " and IPP response code 0x" + Integer.toHexString(response.getStatusCode()));
			
			throw new PrintException("Job failed with HTTP statusline: " + (response.getHttpHeaders().size()>0?response.getHttpHeaders().get(0):"unknown") + " and IPP response code 0x" + Integer.toHexString(response.getStatusCode()));
		}
	}

	/**
	 * prints the doc. The attributes are ignored except for the Copies.
	 * @param doc
	 * @param attributes
	 */
	public void print(Doc doc, PrintRequestAttributeSet attributes) throws PrintException {

		IppResponse response = null;
		try {
			byte[] ippBuf = IppPrintJobOperation.getIppHeader(uri);
			byte[] docBuf = (byte[])doc.getPrintData();
			Copies copies = attributes == null ? null : (Copies)attributes.get(Copies.class);
			int numberOfCopies = copies == null ? 1 : copies.getValue();
			LOG.debug("Sending " + copies + " copies to printer");
			for (int i = 0; i < numberOfCopies; i++) {
			    response = sendRequest(uri, ippBuf, docBuf);
			    handlResponse(response);
            }
		} catch (Exception e) {
			if (e instanceof PrintException) {
				throw (PrintException)e;
			}
			throw new PrintException(e);
		}
	}

	public void removePrintJobAttributeListener(PrintJobAttributeListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void removePrintJobListener(PrintJobListener listener) {
		// TODO Auto-generated method stub
		
	}

}
