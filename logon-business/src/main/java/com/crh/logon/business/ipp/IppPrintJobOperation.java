/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ipp;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 * @author AdNovum Informatik AG
 */
public class IppPrintJobOperation {
	private final static short PRINT_JOB = 0x0002;
	private final static int BUFFER_CAPACITY = 8*512;

	/**
	 * 
	 * @param printerUri
	 * @return ByteBuffer IPP header
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] getIppHeader(URI printerUri) throws UnsupportedEncodingException {
		
		ByteBuffer ippBuf = ByteBuffer.allocateDirect(BUFFER_CAPACITY);
		
		ippBuf = IppTag.getOperation(ippBuf,PRINT_JOB);		
		ippBuf = IppTag.getUri(ippBuf, "printer-uri", printerUri);
		ippBuf = IppTag.getEnd(ippBuf);
		ippBuf.flip();
		
		int i = 0;
		byte[] ippBytes = new byte[ippBuf.limit()];
		while (ippBuf.hasRemaining()) {
			ippBytes[i++] = ippBuf.get();
		}
		
		return ippBytes;
	}
	
}

