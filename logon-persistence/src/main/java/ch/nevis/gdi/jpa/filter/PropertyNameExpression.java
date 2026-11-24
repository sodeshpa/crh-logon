/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 * Use this class if you will have a property name used in your expression.
 * This class stores it and provides you with convenient access to it.
 */
abstract class PropertyNameExpression implements Expression {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PropertyNameExpression.class);

	private String propertyName;

	PropertyNameExpression(String propertyName) {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"propertyName", propertyName}};
			LOG.entering(this, "PropertyNameExpression", params);
		}
		// ]]
		try {
			if (propertyName == null) {
				throw new NullPointerException("property name must never be null!");
			}
			this.propertyName = propertyName;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	String getPropertyName() {
		return propertyName;
	}

	String getQualifiedPropertyName() {
		if (propertyName.equalsIgnoreCase(ThreadLocalIdentificationVariable.IDENTIFICATION_VARIABLE_AS_PROPERTY_NAME)) {
			return ThreadLocalIdentificationVariable.getIdentificationVariable();
		}
		return ThreadLocalIdentificationVariable.getIdentificationVariable() + "." + propertyName;
	}

}
