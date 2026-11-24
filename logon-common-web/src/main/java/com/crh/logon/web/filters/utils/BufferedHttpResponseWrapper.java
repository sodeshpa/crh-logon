/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.filters.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author AdNovum Informatik AG
 */
public class BufferedHttpResponseWrapper extends HttpServletResponseWrapper {
	
	private BufferedPrintWriter bufferedPrintWriter;
	private BufferedServletOutputStream bufferedServletOutputStream;
	private int status;

	public BufferedHttpResponseWrapper(HttpServletResponse origResponse) {
        super(origResponse);
    }

    public byte[] getBuffer( ) {

    	if (bufferedPrintWriter != null) {
    		String buffer = bufferedPrintWriter.getBuffer();
    		
    		return buffer != null ? buffer.getBytes() : null;
    	}
    	else if (bufferedServletOutputStream != null) {
    		
    		return bufferedServletOutputStream.getBuffer();
    	}
    	else {
    		
    		return null;
    	}
    }
    
    public void setStatus(int status) {
    	
    	this.status = status;
    	super.setStatus(status);
    }
    
    public int getStatus() {
    	
    	return status;
    }

    public PrintWriter getWriter() throws IOException {
        
    	bufferedPrintWriter = new BufferedPrintWriter(super.getWriter());
    	
    	return bufferedPrintWriter;
    }

    public ServletOutputStream getOutputStream( ) throws IOException {
        
    	bufferedServletOutputStream = new BufferedServletOutputStream(super.getOutputStream());
    	
    	return bufferedServletOutputStream;
    }
} 