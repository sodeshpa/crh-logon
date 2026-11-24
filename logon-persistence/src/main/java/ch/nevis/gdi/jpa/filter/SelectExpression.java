/*
 * Author			 : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

/**
 * @author AdNovum Informatik AG
 * 
 * SelectExpression is everything before the from-keyword in a select
 * 
 * [select x, y, z] == SelectExpression
 * 
 */
public abstract class SelectExpression extends PropertyNameExpression {
	
	SelectExpression(String propertyName) {
		super(propertyName);
	}

	public static final SelectExpression simpleSelect() {
		return new SimpleSelectExpression();
	}
	
	public static final SelectExpression simpleSelect(String propertyName) {
		return new SimpleSelectExpression(propertyName);
	}
	
	public final static SelectExpression avg(String propertyName) {
		return new AggregateExpression(propertyName, AggregateFunction.AVG, false); 
	}

	public final static SelectExpression avgDistinct(String propertyName) {
		return new AggregateExpression(propertyName, AggregateFunction.AVG, true); 
	}
	
	public final static SelectExpression min(String propertyName) {
		return new AggregateExpression(propertyName, AggregateFunction.MIN, false); 
	}
	
	public final static SelectExpression minDistinct(String propertyName) {
		return new AggregateExpression(propertyName, AggregateFunction.MIN, true); 
	}
	
	public final static SelectExpression max(String propertyName) {
		return new AggregateExpression(propertyName, AggregateFunction.MAX, false); 
	}
	
	public final static SelectExpression maxDistinct(String propertyName) {
		return new AggregateExpression(propertyName, AggregateFunction.MAX, true); 
	}
	
	public final static SelectExpression sum(String propertyName) {
		return new AggregateExpression(propertyName, AggregateFunction.SUM, false); 
	}
	
	public final static SelectExpression sumDistinct(String propertyName) {
		return new AggregateExpression(propertyName, AggregateFunction.SUM, true); 
	}

	public final static SelectExpression count(String propertyName) {
		return new AggregateExpression(propertyName, AggregateFunction.COUNT, false); 
	}

	public final static SelectExpression countDistinct(String propertyName) {
		return new AggregateExpression(propertyName, AggregateFunction.COUNT, true); 
	}
	
	public final static SelectExpression count() {
		return count(ThreadLocalIdentificationVariable.IDENTIFICATION_VARIABLE_AS_PROPERTY_NAME);
	}
	
	public final static SelectExpression countDistinct() {
		return countDistinct(ThreadLocalIdentificationVariable.IDENTIFICATION_VARIABLE_AS_PROPERTY_NAME); 
	}

}
