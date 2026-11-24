/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.converters;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class URIConverter implements org.apache.commons.beanutils.Converter {
	
	public static URI convertDefault(Object value) {
		
		String strValue = value != null ? value.toString().trim() : null;
        if (StringUtils.isEmpty(strValue)) {
            
        	return null;
        }

        if (value instanceof URI) {
            
        	return (URI)value;
        }
        
        try {
            return new URI(strValue);
        } catch (URISyntaxException e) {
            throw new ConversionException(e);
        }
	}

	public Object convert(Class type, Object value) {

		if (URI.class.equals(type)) {
		
			return convertDefault(value);
		}
		
		throw new ConversionException("Cannot convert to type " + type);
    }
}

