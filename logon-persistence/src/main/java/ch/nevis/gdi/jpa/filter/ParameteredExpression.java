/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

/**
 * @author AdNovum Informatik AG
 */
abstract class ParameteredExpression extends PropertyNameExpression {

	private String inputParameter;
	private Object value;
	
	ParameteredExpression(String propertyName, Object value) {
		super(propertyName);
		this.value = value;
	}

	void setInputParameter(String inputParameter) {
		this.inputParameter = inputParameter;
	}

	String getInputParameter() {
		return inputParameter;
	}

	Object getValue() {
		return value;
	}
	
}
