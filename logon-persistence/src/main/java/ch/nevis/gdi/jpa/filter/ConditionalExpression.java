/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

import java.util.ArrayList;
import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
abstract class ConditionalExpression implements Expression {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ConditionalExpression.class);

	List<Expression> expressions = new ArrayList<Expression>();
	
	public ConditionalExpression(Expression expression1, Expression expression2, Expression... expressions) { // NOPMD
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"expression1", expression1},
								{"expression2", expression2}};
			LOG.entering(this, "Conjunction", params);
		}
		// ]]
		try {
			if (expression1 == null) {
				throw new NullPointerException("the first operand of a conditional expression must not be null!");
			}
			this.expressions.add(expression1);
			if (expression2 != null) {
				this.expressions.add(expression2);
			}
			if (expressions != null) {
				for (Expression expression : expressions) {
					if (expression == null) {
						continue;
					}
					this.expressions.add(expression);
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	List<Expression> getExpressions() {
		return expressions;
	}
	
}
