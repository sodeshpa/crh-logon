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
public class IppTag {
    private final static byte MAJOR_VERSION = 0x01;
    private final static byte MINOR_VERSION = 0x01;

    private final static String ATTRIBUTES_CHARSET = "attributes-charset";
    private final static String ATTRIBUTES_NATURAL_LANGUAGE = "attributes-natural-language";

    private final static String ATTRIBUTES_CHARSET_VALUE = "utf-8";
    private final static String ATTRIBUTES_NATURAL_LANGUAGE_VALUE = "en-us";

    private final static byte OPERATION_ATTRIBUTES_TAG = 0x01;
    private final static byte END_OF_ATTRIBUTES_TAG = 0x03;
    private final static byte URI_TAG = 0x45;
    private final static byte CHARSET_TAG = 0x47;
    private final static byte NATURAL_LANGUAGE_TAG = 0x48;
    
    private static int _requestId = 0;


    /**
     * 
     * @param ippBuf
     * @param operation
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static ByteBuffer getOperation(ByteBuffer ippBuf,short operation) 
    throws UnsupportedEncodingException {
    	return getOperation(ippBuf,operation,null,null);
    } 
    /**
     * 
     * @param ippBuf
     * @param operation
     * @param charset
     * @param naturalLanguage
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static ByteBuffer getOperation(ByteBuffer ippBuf,short operation,String charset,
                    String naturalLanguage) throws UnsupportedEncodingException {
            if (ippBuf == null) {
                    return null;
            }
            if (charset == null) {
                    charset = ATTRIBUTES_CHARSET_VALUE;
            }
            if (naturalLanguage == null) {
                    naturalLanguage = ATTRIBUTES_NATURAL_LANGUAGE_VALUE;
            }
            ippBuf.put(MAJOR_VERSION);
            ippBuf.put(MINOR_VERSION);
            ippBuf.putShort(operation);
            ippBuf.putInt(++_requestId);
            ippBuf.put(OPERATION_ATTRIBUTES_TAG);

            ippBuf = getCharset(ippBuf,ATTRIBUTES_CHARSET,charset);
            ippBuf = getNaturalLanguage(ippBuf,ATTRIBUTES_NATURAL_LANGUAGE,naturalLanguage);
            return ippBuf;
    }

    /**
     * 
     * @param ippBuf
     * @param name
     * @param value
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static ByteBuffer getCharset(ByteBuffer ippBuf,String name,String value) 
    throws UnsupportedEncodingException {
    	if (ippBuf == null) {
    		return null;
        }
        if ((name == null) || (value == null)) {
        	return ippBuf;
        }
        ippBuf.put(CHARSET_TAG);
        ippBuf.putShort((short)name.length());
        ippBuf.put(IppUtil.toBytes(name));
        ippBuf.putShort((short)value.length());
        ippBuf.put(IppUtil.toBytes(value));
        return ippBuf;
    }

    /**
     * 
     * @param ippBuf
     * @param name
     * @param value
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static ByteBuffer getNaturalLanguage(ByteBuffer ippBuf,String name,String value) 
    throws UnsupportedEncodingException {
    	if (ippBuf == null) {
    		return null;
    	}
        if ((name == null) || (value == null)) {
        	return ippBuf;
        }
        ippBuf.put(NATURAL_LANGUAGE_TAG);
        ippBuf.putShort((short)name.length());
        ippBuf.put(IppUtil.toBytes(name));
        ippBuf.putShort((short)value.length());
        ippBuf.put(IppUtil.toBytes(value));
        return ippBuf;
    }

    /**
     * 
     * @param ippBuf
     * @param name
     * @param value
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static ByteBuffer getUri(ByteBuffer ippBuf, String name, URI value) 
    throws UnsupportedEncodingException {
    	if (ippBuf == null) {
    		return null;
        }
        if ((name == null) || (value == null)) {
        	return ippBuf;
        }
        ippBuf.put(URI_TAG);
        ippBuf.putShort((short)name.length());
        ippBuf.put(IppUtil.toBytes(name));
        ippBuf.putShort((short)value.toString().length());
        ippBuf.put(IppUtil.toBytes(value.toString()));
        return ippBuf;
    }

    /**
     * 
     * @param ippBuf
     * @return
     */
    public static ByteBuffer getEnd(ByteBuffer ippBuf) {
    	if (ippBuf == null) {
            return null;
    	}
    	ippBuf.put(END_OF_ATTRIBUTES_TAG);
    	return ippBuf;
    }
}
