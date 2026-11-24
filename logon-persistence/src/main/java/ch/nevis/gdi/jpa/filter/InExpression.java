/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
final class InExpression extends PropertyNameExpression implements Serializable{
	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(InExpression.class);

	private List<String> inputParameters = new ArrayList<String>();
	private List values = new ArrayList();
	
	InExpression(String propertyName, List values) {
		super(propertyName);
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"propertyName", propertyName},
								{"values", values}};
			LOG.entering(this, "InExpression", params);
		}
		// ]]
		try {
			// Enterprise JavaBeans 3.0, Final Release -> Query Language -> In Expressions:
			// "There must be at least one element in the comma separated list
			// that defines the set of values for the IN expression."
			// in_expression ::=
			// state_field_path_expression [NOT] IN ( in_item {, in_item}* | subquery)
			// in_item ::= literal | input_parameter
			if (values == null || values.isEmpty()) {
				throw new IllegalArgumentException("at least one element must be present in the value list!");
			}
			this.values = values;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	InExpression(String propertyName, Set values) {
		super(propertyName);
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"propertyName", propertyName},
					{"values", values}};
			LOG.entering(this, "InExpression", params);
		}
		// ]]
		try {
			if (values == null || values.isEmpty()) {
				throw new IllegalArgumentException("at least one element must be present in the value list!");
			}
			this.values = new ArrayList(values);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
	
	InExpression(String propertyName, Object[] values) {
		super(propertyName);
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"propertyName", propertyName},
					{"values", values}};
			LOG.entering(this, "InExpression", params);
		}
		// ]]
		try {
			if (values == null || values.length < 1) {
				throw new IllegalArgumentException("at least one element must be present in the value list!");
			}
			this.values = Arrays.asList(values);
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
			sb.append(" in (");
			for (Iterator iterator = inputParameters.iterator(); iterator.hasNext();) {
				String inputParameter = (String) iterator.next();
				sb.append(":").append(inputParameter);
				if (iterator.hasNext()) {
					sb.append(", ");
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

	List getValues() {
		return values;
	}

	void setInputParameters(List<String> inputParameters) {
		if (inputParameters == null) {
			return;
		}
		this.inputParameters = inputParameters;
	}
	
}
