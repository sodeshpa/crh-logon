/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.filters.utils;

import java.io.PrintWriter;

/**
 * @author AdNovum Informatik AG
 */
public class BufferedPrintWriter extends PrintWriter {

	private StringBuilder buffer;
	private String lineSeparator;
	
	public BufferedPrintWriter(PrintWriter printWriter) {

		super(printWriter);
		buffer = new StringBuilder();
		lineSeparator = System.getProperty("line.separator");
	}
	
	@Override
	public void write(int c) {
		buffer.append((char)c);
		super.write(c);
	}

	@Override
	public void write(char[] buf, int off, int len) {
		buffer.append(buf, off, len);
		super.write(buf, off, len);
	}

	@Override
	public void write(String s, int off, int len) {
		buffer.append(s, off, len);
		super.write(s, off, len);
	}
	
	@Override
	public void println() {
		buffer.append(lineSeparator);
		super.println();
	}

	public String getBuffer() {
		
		return buffer.toString();
	}
}
