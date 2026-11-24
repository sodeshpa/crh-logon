/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.filters.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * @author AdNovum Informatik AG
 */
public class BufferedServletOutputStream extends ServletOutputStream {
    
	private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private ServletOutputStream servletOutputStream;
    
    public BufferedServletOutputStream(ServletOutputStream servletOutputStream) {
    	
    	this.servletOutputStream = servletOutputStream;
    }

	@Override
	public void write(int b) throws IOException {
		
		buffer.write(b);
		servletOutputStream.write(b);
	}
	
	public byte[] getBuffer() {
		
		return buffer.toByteArray();
	}
} 