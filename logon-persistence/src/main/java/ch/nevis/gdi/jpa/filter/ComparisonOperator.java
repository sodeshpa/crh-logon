/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

/**
 * @author AdNovum Informatik AG
 */
enum ComparisonOperator {
	
	EQUALS("="),
	GREATER_THAN(">"),
	LOWER_THAN("<"),
	GREATER_THAN_OR_EQUAL(">="),
	LOWER_THAN_OR_EQUAL("<="),
	NOT_EQUALS("<>");
	
	private ComparisonOperator(String op) {
		this.op = op;
	}
	
	private String op;

	@Override
	public String toString() {
		return op;
	}

}
