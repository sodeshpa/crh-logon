/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
final class JpaQLExpression implements Expression {

    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(JpaQLExpression.class);

	private String jpaQuery = null;
	private Map<String, Object> values = new HashMap<String, Object>();
	
	JpaQLExpression(String jpaQuery, Map<String, Object> values) {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"jpaQuery", jpaQuery}};
			LOG.entering(this, "JpaQLExpression", params);
		}
		// ]]
		try {
			if (jpaQuery == null) {
				throw new NullPointerException("query fragment must never be null!");
			}
			this.jpaQuery = jpaQuery;
			if (values != null) {
				this.values = values;
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
			// use items from 
			sb.append(" (").append(jpaQuery).append(") ");
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
	
	Map<String, Object> getValues() {
		return values;
	}
	
}
