/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.converters;

import java.math.BigDecimal;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 * BeanUtils converter which converts the empty string or null to null.
 */
public class BigDecimalConverter implements org.apache.commons.beanutils.Converter {

	public static BigDecimal convertDefault(Object value) {

		String strValue = value != null ? value.toString().trim() : null;
        if (StringUtils.isEmpty(strValue)) {
            
        	return null;
        }

        if (value instanceof BigDecimal) {
            
        	return (BigDecimal)value;
        }

        try {
            return new BigDecimal(strValue.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new ConversionException(e);
        }
    }
	
	public Object convert(Class type, Object value) {
		
		if (BigDecimal.class.equals(type)) {
		
			return convertDefault(value);
		}

		throw new ConversionException("Cannot convert to type " + type);
	}
}
