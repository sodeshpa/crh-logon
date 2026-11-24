/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


/**
 * The default implementation of the {@link Filter} interface.
 * <br>
 * <b>Note</b>: The constructor of the class is <b>public</b>,
 * however, it is encouraged to use the corresponding method
 * of the DAOs to create a new instance of {@link FilterImpl}.
 * 
 * @author AdNovum Informatik AG
 * 
 * @see Filter
 * @see Expression
 * @see Order
 * @see JpaQLExpression
 * @see InExpression
 * @see ParameteredExpression
 */
public class FilterImpl<T> implements Filter<T>, Serializable {

	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(FilterImpl.class);
	
	private List<Expression> expressions = null;
    private List<Order> orders = null;
    
    private Map<String, Object> inputParameters = null;
    
    public FilterImpl() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "FilterImpl");
		}
		// ]]
		try {
			expressions = new ArrayList<Expression>();
			orders = new ArrayList<Order>();
			inputParameters = new HashMap<String, Object>();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    public void addExpression(Expression expression) {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"expression", expression}};
			LOG.entering(this, "addExpression", params);
		}
		// ]]
		try {
			if (expression == null) {
				throw new NullPointerException("null expression not allowed!");
			}
			
			if (expression instanceof SelectExpression) {
				throw new IllegalArgumentException("invalid expression type in filter: "
						+ expression.getClass().getSimpleName());
			}
			
			if (expression instanceof Order) {
				orders.add((Order) expression);
			}
			else {
				expressions.add(expression);
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
			// always re-initialize input parameters map
			inputParameters = new HashMap<String, Object>();
			preProcessExpressions();
			
			StringBuilder sb = new StringBuilder();
			
			if (!expressions.isEmpty()) {
				sb.append(" where ");
			}
			for (Iterator it = expressions.iterator(); it.hasNext();) {
				Expression expression = (Expression) it.next();
				sb.append(expression.toJPAQL());
				if (it.hasNext()) {
					sb.append(" and ");
				}
			}
			
			// NOTE! "ORDER BY" moved to JPAQueryImpl, it has to be after "GROUP BY"
			// see ADN53339 for more information
			
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

	private void preProcessExpressions() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "preProcessExpressions");
		}
		// ]]
		try {
			if (expressions.isEmpty()) {
				return;
			}
			
			for (Expression expression : expressions) {
				preProcessExpression(expression);
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

	private void preProcessExpression(Expression expression) {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "preProcessExpression");
		}
		// ]]
		try {
			if (expression == null) {
				return;
			}
			
			// Step #1: handle ConditionalExpression (recursive calls)
			if (expression instanceof ConditionalExpression) {
				ConditionalExpression conditionalExpression = (ConditionalExpression) expression;
				List<Expression> joinedExpressions = conditionalExpression.getExpressions();
				for (Expression expression2 : joinedExpressions) {
					preProcessExpression(expression2);
				}
				return;
			}
			
			// Step #2: add the input parameters of the JpaQLExpressions first, as their
			// identifiers are determined by the API user
			if (expression instanceof JpaQLExpression) {
				JpaQLExpression jpaQLExpression = (JpaQLExpression) expression;
				Map<String, Object> map = jpaQLExpression.getValues();
				if (map == null) {
					return;
				}
				for (Entry<String, Object> entry : map.entrySet()) {
					String inputParameterName = replaceDots(entry.getKey());
					Object inputParameterValue = inputParameters.get(inputParameterName);
					Object value = entry.getValue();
					if (inputParameterValue != null) {
						if (!inputParameterValue.equals(value)) {
							throw new IllegalArgumentException("Multiple values found for the same parameter, name=" + inputParameterName + ", value1=" + inputParameterValue + ", value2=" + value);
						}
						continue;
					}
					inputParameters.put(inputParameterName, value);
				}
				return;
			}
			
			// Step #3: handle InExpression
			if (expression instanceof InExpression) {
				InExpression inExpression = (InExpression) expression;
				String propertyName = inExpression.getPropertyName();
				List<String> inExprInputParams = new ArrayList<String>();
				List list = inExpression.getValues();
				String inputParameterName = replaceDots(propertyName);
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					Object value = iterator.next();
					int inputParameterNumericLabel = 0;
					String key = inputParameterName;
					while (inputParameters.get(key) != null) {
						key = inputParameterName + "_" + inputParameterNumericLabel++;
					}
					inExprInputParams.add(key);
					inputParameters.put(key, value);
				}
				inExpression.setInputParameters(inExprInputParams);
				return;
			}
			
			// Step #4: handle ParameteredExpression (skip JpaQLExpression, InExpression)
			if (expression instanceof ParameteredExpression) {
				ParameteredExpression parameteredExpression = (ParameteredExpression) expression;
				String propertyName = parameteredExpression.getPropertyName();
				Object value = parameteredExpression.getValue();
				String inputParameterName = replaceDots(propertyName);
				String key = inputParameterName;
				int inputParameterNumericLabel = 0;
				while (inputParameters.get(key) != null) {
					key = inputParameterName + "_" + inputParameterNumericLabel++;
				}
				parameteredExpression.setInputParameter(key);
				inputParameters.put(key, value);
				return;
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

	/**
	 * Replaces "." characters in input parameter names to "_". It became necessary
	 * due to a known bug in the Hibernate implementation.
	 * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-3715">HHH-3715</a>
	 */
	private String replaceDots(String key) {
		if (key == null) {
			return key;
		}
		return key.replaceAll("\\.", "_");
	}
	
	public Map<String, Object> getInputParameters() {
		return inputParameters;
	}
	
	@Override
	public String toString() {
		ToStringBuilder tbs = new ToStringBuilder(this);
        tbs.append(super.toString());
        tbs.append("jpaSnippet", toJPAQL().toString());
        return tbs.toString();
	}

	public List<Order> getOrderByExpressions() {
		return orders;
	}
	
}
