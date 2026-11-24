/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.converters;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang.StringUtils;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * @author AdNovum Informatik AG
 * BeanUtils converter which converts the empty string or null to null.
 */
public class Base64ByteArrayConverter implements org.apache.commons.beanutils.Converter {
	
	public static byte[] convertDefault(Object value) {
		
		String strValue = value != null ? value.toString().trim() : null;
        if (StringUtils.isEmpty(strValue)) {
            
        	return null;
        }

        if (value instanceof byte[]) {
            
        	return (byte[])value;
        }
        
        return Base64.decode(strValue);
	}

	public Object convert(Class type, Object value) {

		if (byte[].class.equals(type)) {
		
			return convertDefault(value);
		}
		
		throw new ConversionException("Cannot convert to type " + type);
    }
}
