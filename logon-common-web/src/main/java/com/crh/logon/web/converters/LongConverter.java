/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.converters;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 * BeanUtils converter which converts the empty string or null to null.
 */
public class LongConverter implements org.apache.commons.beanutils.Converter {
	
	public static Long convertDefault(Object value) {
		
		String strValue = value != null ? value.toString().trim() : null;
        if (StringUtils.isEmpty(strValue)) {
            
        	return null;
        }

        if (value instanceof Long) {
            
        	return (Long)value;
        }
        if (value instanceof Number) {
        	
        	return ((Number)value).longValue();
        }

        try {
            return new Long(strValue);
        } catch (NumberFormatException e) {
            throw new ConversionException(e);
        }
	}

	public Object convert(Class type, Object value) {

		if (Long.class.equals(type)) {
		
			return convertDefault(value);
		}
		
		throw new ConversionException("Cannot convert to type " + type);
    }
}
