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
public class BooleanConverter implements org.apache.commons.beanutils.Converter {

	public Boolean convertDefault(Object value) {

		String strValue = value != null ? value.toString().trim() : null;
        if (StringUtils.isEmpty(strValue)) {
            
        	return null;
        }

        if (value instanceof Boolean) {
            
        	return (Boolean)value;
        }

    	if (strValue.equalsIgnoreCase("yes") ||
			strValue.equalsIgnoreCase("y") ||
			strValue.equalsIgnoreCase("true") ||
			strValue.equalsIgnoreCase("on") ||
			strValue.equalsIgnoreCase("1")) {
           
    		return (Boolean.TRUE);
        } else if (strValue.equalsIgnoreCase("no") ||
    		strValue.equalsIgnoreCase("n") ||
    		strValue.equalsIgnoreCase("false") ||
    		strValue.equalsIgnoreCase("off") ||
    		strValue.equalsIgnoreCase("0")) {
            
        	return (Boolean.FALSE);
        } 
        else {
        	throw new ConversionException(strValue);
        }
    }
	
	public Object convert(Class type, Object value) {
		
		if (Boolean.class.equals(type)) {
			
			return convertDefault(value);
		}
		
		throw new ConversionException("Cannot convert to type " + type);
	}
}

