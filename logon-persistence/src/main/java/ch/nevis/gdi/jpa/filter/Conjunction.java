/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

import java.util.Iterator;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
final class Conjunction extends ConditionalExpression {

    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(Conjunction.class);

	/**
	 * Calling {@link Expression#toJPAQL()} on an instance of this class will produce a
	 * JPA-QL snippet by joining the {@link Expression} parameters with the <b>AND</b>
	 * logical operand.
	 * 
	 * The first two {@link Expression} parameters are compulsory, otherwise using
	 * {@link Conjunction} would not make sense.
	 * 
	 * @param expression1 Compulsory parameter
	 * @param expression2 Compulsory parameter
	 * @param expressions Varargs parameter to append additional members to the conjunction
	 */
	Conjunction(Expression expression1, Expression expression2, Expression... expressions) { // NOPMD
		super(expression1, expression2, expressions);
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"expression1", expression1},
								{"expression2", expression2}};
			LOG.entering(this, "Conjunction", params);
		}
		// ]]
		try {
			if (expression2 == null) {
				throw new NullPointerException("the second operand of a conjunction must not be null!");
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
	
	public StringBuilder toJPAQL() {
		// [[
		StringBuilder result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "toJPAQL");
		}
		// ]]
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for (Iterator<Expression> iterator = expressions.iterator(); iterator.hasNext();) {
				Expression expression = iterator.next();
				sb.append(expression.toJPAQL());
				if (iterator.hasNext()) {
					sb.append(" and ");
				}
			}
			sb.append(")");
			return result = sb;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	@Override
	public String toString() {
		ToStringBuilder tbs = new ToStringBuilder(this);
        tbs.append(super.toString());
        tbs.append("jpaSnippet", toJPAQL().toString());
        return tbs.toString();
	}

}
