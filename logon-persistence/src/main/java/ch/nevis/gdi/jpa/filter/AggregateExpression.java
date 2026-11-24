/*
 * Author			 : AdNovum Informatik AG
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
final class AggregateExpression extends SelectExpression {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AggregateExpression.class);
	
	private AggregateFunction aggregateFunction = null;
	private boolean distinct;
	
	AggregateExpression(String propertyName, AggregateFunction aggregateFunction, boolean distinct) {
		super(propertyName);
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"propertyName", propertyName},
								{"aggregateFunction", aggregateFunction},
								{"distinct", distinct + ""}};
			LOG.entering(this, "AggregateExpression", params);
		}
		// ]]
		try {
			this.aggregateFunction = aggregateFunction;
			this.distinct = distinct;
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
			if (aggregateFunction != null) {
				sb.append(aggregateFunction.name().toLowerCase());
				sb.append("(");
				if (distinct) {
					sb.append("distinct ");
				}
				sb.append(getQualifiedPropertyName());
				sb.append(")");
			}
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

