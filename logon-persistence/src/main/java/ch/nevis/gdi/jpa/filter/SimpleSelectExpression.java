/*
 * Author			 : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author AdNovum Informatik AG
 */
final class SimpleSelectExpression extends SelectExpression {

	SimpleSelectExpression() {
		super(ThreadLocalIdentificationVariable.IDENTIFICATION_VARIABLE_AS_PROPERTY_NAME);
	}
	
	SimpleSelectExpression(String propertyName) {
		super(propertyName);
	}

	public StringBuilder toJPAQL() {
		StringBuilder sb = new StringBuilder();
		sb.append(getQualifiedPropertyName());
		return sb;
	}
	
	@Override
	public String toString() {
		ToStringBuilder tbs = new ToStringBuilder(this);
        tbs.append(super.toString());
        tbs.append("jpaSnippet", toJPAQL().toString());
        return tbs.toString();
	}
	
}
