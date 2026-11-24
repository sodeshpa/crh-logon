/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.converters;

import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 * BeanUtils converter which converts the empty string or null to null.
 */
public class DateConverter implements org.apache.commons.beanutils.Converter {
	
	public static Date convertDefault(Object value) {
		
		String strValue = value != null ? value.toString().trim() : null;
        if (StringUtils.isEmpty(strValue)) {
            
        	return null;
        }

        if (value instanceof Date) {
            
        	return (Date)value;
        }
        
        return DatatypeConverter.parseDateTime(strValue).getTime();
	}

	public Object convert(Class type, Object value) {

		if (Date.class.equals(type)) {
		
			return convertDefault(value);
		}
		
		throw new ConversionException("Cannot convert to type " + type);
    }
}
