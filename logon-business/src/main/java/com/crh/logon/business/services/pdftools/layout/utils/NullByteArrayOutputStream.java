/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.crh.logon.business.services.pdftools.layout.JcanPdfRenderer;
import org.apache.commons.io.output.NullOutputStream;

/**
 * {@link ByteArrayOutputStream} subclass, similar to {@link NullOutputStream} to be used as an argument for
 * {@link JcanPdfRenderer} constructor
 */
public class NullByteArrayOutputStream extends ByteArrayOutputStream {

	@Override
	public void close() throws IOException {
	}

	@Override
	public synchronized void reset() {
	}

	@Override
	public synchronized int size() {
		return 0;	
	}

	@Override
	public synchronized byte[] toByteArray() {
		return new byte[]{};
	}

	@Override
	public synchronized String toString() {
		return this.getClass().getName();
	}

	@Override
	public synchronized String toString(int hibyte) {
		return "";
	}

	@Override
	public synchronized String toString(String charsetName) throws UnsupportedEncodingException {
		return "";
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) {
	}

	@Override
	public synchronized void write(int b) {
	}

	@Override
	public synchronized void writeTo(OutputStream out) throws IOException {
	}

	@Override
	public void write(byte[] b) throws IOException {
	}

	@Override
	public void flush() throws IOException {
	}

}
