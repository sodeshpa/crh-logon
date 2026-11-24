/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.converters;

/**
 * @author AdNovum Informatik AG
 */
public class ConversionException extends RuntimeException {
	
	String propertyName;
	Object propertyValue;
	
	public ConversionException(Throwable cause, String propertyName, Object propertyValue) {
		
		super(cause);
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Object getPropertyValue() {
		return propertyValue;
	}
}
