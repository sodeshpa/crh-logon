/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.converters;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 * BeanUtils converter which converts the empty string or null to null.
 */
public class StringConverter implements org.apache.commons.beanutils.Converter {
	
	public static String convertDefault(Object value) {
		
		String strValue = value != null ? value.toString().trim() : null;
        if (StringUtils.isEmpty(strValue)) {
            
        	return null;
        }

        if (value instanceof String) {
            
        	return (String)value;
        }
        
        return value.toString();
	}

	public Object convert(Class type, Object value) {

		if (String.class.equals(type)) {
		
			return convertDefault(value);
		}
		
		throw new ConversionException("Cannot convert to type " + type);
    }
}
