/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ipp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class IppResponseHandler {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(IppResponseHandler.class);

	public static IppResponse getContent(HttpURLConnection conn) throws IOException {

		IppResponse ippResponse = new IppResponse();
		
		parseHttpHeaders(conn, ippResponse);		
		parseIppHeaders(conn, ippResponse);
				
		return ippResponse;
	}
	
	private static void parseHttpHeaders(HttpURLConnection conn, IppResponse ippResponse) {
		
		int headerCount = 0;
		String key = null, value = null;
		List httpHeaders = new ArrayList();
		do {
			key = conn.getHeaderFieldKey(headerCount);
			value = conn.getHeaderField(headerCount++);
			if ((key != null) || (value != null)) {
				httpHeaders.add((key!=null?key + ": ":"") + value);
			}
		}
		while ((key != null) || (value != null));
		
		ippResponse.setHttpHeaders(httpHeaders);
		try {
			ippResponse.setHttpStatusCode(conn.getResponseCode() + "");
		}
		catch (IOException e) {
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private static void parseIppHeaders(HttpURLConnection conn, IppResponse response) throws IOException {
		
		InputStream httpIn = conn.getInputStream();
		
		response.setVersionMajor(httpIn.read());
		response.setVersionMinor(httpIn.read());
		byte h = 0, l = 0;
		h = (byte)httpIn.read();
		l = (byte)httpIn.read();
		LOG.debug("Status code: " + h + " " + l);
		response.setStatusCode((h<<8) + l);
	}
}
