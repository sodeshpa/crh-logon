/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
final class SimpleExpression extends ParameteredExpression {

    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SimpleExpression.class);

	private ComparisonOperator op;
	
	SimpleExpression(String propertyName, Object value, ComparisonOperator op) {
		super(propertyName, value);
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"propertyName", propertyName},
								{"value", value},
								{"op", op}};
			LOG.entering(this, "SimpleExpression", params);
		}
		// ]]
		try {
			this.op = op;
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
			sb.append(getQualifiedPropertyName());
			sb.append(" ").append(op).append(" :").append(getInputParameter());
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
