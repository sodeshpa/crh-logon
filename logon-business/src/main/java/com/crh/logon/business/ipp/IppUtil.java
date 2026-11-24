/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ipp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

/**
 * @author AdNovum Informatik AG
 */
public class IppUtil {
	
	private final static String DEFAULT_CHARSET = "UTF-8";

	/**
	 * 
	 * @param a high byte
	 * @param b low byte
	 * @return short value
	 */
	static public short toShort(byte a, byte b)  {
		return (short)(((a & 0x00ff) << 8) + (b & 0x00ff));
	}

	/**
	 * 
	 * @param b byte
	 * @return String
	 */
	static public String toHex(byte b)  {
		String st = Integer.toHexString(b);
		return (st.length() == 1) ? new String("0" + st) : st;
	}

	/**
	 * 
	 * @param b byte
	 * @return String with Marker '0x' ahead
	 */
	static public String toHexWithMarker(byte b)  {
		StringBuffer sb = new StringBuffer();
		sb.append("0x").append(toHex(b));
		return sb.toString();
	}

	/**
	 * 
	 * @param dst array of byte
	 * @return String representation
	 */
	static String toString(byte[] dst) {
		StringBuffer sb = new StringBuffer();
		int l = dst.length;
		for (int i = 0; i < l; i++)  {
			int b = dst[i];
			int ival = ((int)b) & 0xff;
			char c = (char)ival;
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 *
	 * @param str String to encode
	 * @param encoding
	 * @return byte array
	 * @throws UnsupportedEncodingException 
	 */
	static public byte[] toBytes(String str, String encoding) 
	throws UnsupportedEncodingException {
		if (encoding == null) { encoding = DEFAULT_CHARSET; }
		return str.getBytes(encoding);
	}

	/**
	 * 
	 * Encode in default encoding ('utf-8')
	 * @see toBytes(str,null)
	 * @return array of byte
	 * @throws UnsupportedEncodingException 
	 */
	static public byte[] toBytes(String str) throws UnsupportedEncodingException {
		return toBytes(str,null);
	}

	/**
	 * see RFC 2579 for DateAndTime byte length and explanation of byte fields
	 * IPP datetime must have a length of eleven bytes
	 * @param dst byte array
	 * @return String representation of dateTime
	 */
	static String toDateTime(byte[] dst) {	
		StringBuffer sb = new StringBuffer();
		short year = toShort(dst[0],dst[1]);
		sb.append(year).append("-");
		byte month = dst[2];
		sb.append(month).append("-");
		byte day = dst[3];
		sb.append(day).append(",");
		byte hours = dst[4];
		sb.append(hours).append(":");
		byte min = dst[5];
		sb.append(min).append(":");
		byte sec = dst[6];
		sb.append(sec).append(".");
		byte decSec = dst[7];
		sb.append(decSec).append(",");
	
		int b = dst[8];
		int ival = ((int)b) & 0xff;
		char c = (char) ival;
		sb.append(c);
	
		hours = dst[9];
		sb.append(hours).append(":");
		min = dst[10];
		sb.append(min);
		return sb.toString();
	}

	/**
	 * See RFC2910, http://www.ietf.org/rfc/rfc2910
	 * IPP boolean is defined as SIGNED-BYTE
	 * where 0x00 is 'false' and 0x01 is 'true'
	 * @param b byte
	 * @return String representation of boolean: i.e. true, false
	 */
	static public String toBoolean(byte b) {
		return (b == 0) ? new String("false") : new String("true");
	}

	/**
	 * 
	 * @param ipAddress
	 * @return true or false
	 * @throws IOException 
	 */
	static public boolean isAlive(String ipAddress) throws IOException {
		return InetAddress.getByName(ipAddress).isReachable(2000);
	}
	
	/**
	*
	* @param str
	* @return
	* @throws CharacterCodingException
	*/
	static public String getTranslatedString(String str)
	throws CharacterCodingException {
		return getTranslatedString(str,null);
	}
	
	/**
	*
	* @param str
	* @param charsetName
	* @return
	* @throws CharacterCodingException
	*/
	static public String getTranslatedString(String str, String charsetName)
	throws CharacterCodingException {
		if (charsetName == null) {
			charsetName = DEFAULT_CHARSET;
		}
		Charset charset = Charset.forName(charsetName);
		CharsetDecoder decoder = charset.newDecoder();
		CharsetEncoder encoder = charset.newEncoder();
		decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
		encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
		// Convert a string to charsetName bytes in a ByteBuffer
		// The new ByteBuffer is ready to be read.
		ByteBuffer buf = encoder.encode(CharBuffer.wrap(str));
		// Convert charsetName bytes in a ByteBuffer to a character ByteBuffer
		// and then to a string. The new ByteBuffer is ready to be read.
		CharBuffer cbuf = decoder.decode(buf);
		return cbuf.toString();
	} 
}
