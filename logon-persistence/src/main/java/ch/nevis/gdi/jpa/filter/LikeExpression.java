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
final class LikeExpression extends ParameteredExpression {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LikeExpression.class);
    
	private boolean caseSensitive = true;
	private boolean useEscape = false;
	private char escapeChar = '\\';
	
	
	/**
	 * Constructor which allows you to contruct a like expression that can contain wildcards
	 * that are escaped. Example:
	 * 
	 * You want to search a database field for the value '%isi3_%'
	 * 
	 * whereas the '_' should not mean a single char (as it would be in SQL by default when
	 * having a LIKE expression), but you want to really search for the "_" character. To 
	 * do this, you have to excape it, e.g. the value get's something like this:
	 * 
	 * '%isi3\_%'
	 * 
	 * But now the database engine needs to know about this, e.g. it needs to know that this
	 * LIKE expression uses a wildcard char, namely the backslash '\'.
	 * 
	 * @see http://www.jroller.com/cmaki/entry/jpql_s_crazy_escape_sequence
	 * @see http://www.manning-sandbox.com/thread.jspa?threadID=15023
	 * 
	 * @param propertyName
	 * @param value
	 * @param caseSensitive
	 * @param useEscape        - true when you want to use escape characters
	 * @param escapeCharacter  - the escape character to use when useEscape is true
	 */
	protected LikeExpression(String propertyName, String value, boolean caseSensitive, boolean useEscape, char escapeCharacter) {
		super(propertyName, value);
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"propertyName", propertyName},
								{"value", value},
								{"caseSensitive", caseSensitive + ""}};
			LOG.entering(this, "LikeExpression", params);
		}
		// ]]
		try {
			this.caseSensitive = caseSensitive;
			this.useEscape = useEscape;
			this.escapeChar = escapeCharacter;
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
			if (caseSensitive) {
				sb.append(getQualifiedPropertyName());
			}
			else {
				sb.append("upper(");
				sb.append(getQualifiedPropertyName());
				sb.append(")");
			}
			sb.append(" like ");
			sb.append(":").append(getInputParameter());
			
			if (this.useEscape) {
				sb.append(" ");
				sb.append("ESCAPE '");
				sb.append(this.escapeChar);
				sb.append("'");
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
