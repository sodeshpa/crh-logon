/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.filters.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.ExceptionUtil;

/**
 * Class which is used to wrap a request in order that the wrapped request's 
 * input stream can be read once and later be read again by subsequent calls 
 * to getInputStream().
 * 
 * This is neccessary because: getInputStream/getReader may only be called once
 * per request and thus, tracing request bodies (e.g. for dwr requests) would 
 * disturb normal request processing.
 * 
 * @author AdNovum Informatik AG
 */
public class InputStreamWrapper extends HttpServletRequestWrapper {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(InputStreamWrapper.class);

	private static final int CHUNK_SIZE = 1024;
	private final String charSetName;
    private final byte[] requestBody;
    
    public InputStreamWrapper(HttpServletRequest request) throws IOException {
        super(request);
        
        InputStream inputStream = null;
        try {
        	byte[] buf;
        	/* initially, the buffer is allocated the number of bytes as 
        	   indicated by the content length. In normal circumstances this 
        	   means the buffer does not need to be extended. However, the 
        	   actual size is determined by the length of the stream. */ 
        	int estLength = request.getContentLength();
        	if (estLength > 0) {
        		buf = new byte[estLength];
        	}
        	else {
        		// content length could not be determined
        		buf = new byte[CHUNK_SIZE];
        	}
        	int bytesRead = 0;
        	int totalBytesRead = 0;
        	if (request.getInputStream() != null) {
        		inputStream = new BufferedInputStream(request.getInputStream());
        		do {
        			bytesRead = inputStream.read(buf, totalBytesRead, buf.length-totalBytesRead);
        			if (bytesRead > 0) {
        				totalBytesRead += bytesRead;
        				if (buf.length == totalBytesRead) {
        					// chunk read, check if end of file is reached
		            		int testByte = inputStream.read();
		            		if (testByte != -1) {
		            			// no end of file, extend buffer and continue
		            			buf = Arrays.copyOf(buf, buf.length + CHUNK_SIZE);
		            			buf[buf.length-CHUNK_SIZE] = (byte)testByte;
		            			totalBytesRead += 1;
		            		}
        				}
	            	}
        		} while (bytesRead > 0);
            }
        	if (buf.length == totalBytesRead) {
        		requestBody = buf;
        	}
        	else {
        		// trim the buffer to the 
        		requestBody = Arrays.copyOf(buf, totalBytesRead);
        	}
        	
        	charSetName = request.getCharacterEncoding();
        }
        finally {
            if (inputStream != null) {
                try {
                	inputStream.close();
                }
                catch (IOException iox) {
                	Object params = null;
        			ExceptionUtil.errException(LOG, iox, params);
                }
            }
        }
    }
 

    /**
     * Override of the getInputStream() method which returns an InputStream that reads from the
     * stored XML payload string instead of from the request's actual InputStream.
     */
    public ServletInputStream getInputStream ()
        throws IOException {
        
        final ByteArrayInputStream byteArrayInputStream = 
        	new ByteArrayInputStream(requestBody);
        
        ServletInputStream inputStream = new ServletInputStream() {
            public int read () 
                throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return inputStream;
    }


    public String getRequestBody() {
		
    	try {
			return new String(requestBody, charSetName!=null ? charSetName : Charset.defaultCharset().name());
		}
		catch (UnsupportedEncodingException e) {
			LOG.error("Unknown charset " + charSetName, e);
			
			return new String(requestBody);
		}
	}
}