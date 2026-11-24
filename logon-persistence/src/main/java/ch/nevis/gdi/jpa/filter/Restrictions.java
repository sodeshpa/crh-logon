/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crh.logon.constants.Constant;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


/**
 * A factory class for all expressions that can be used for filtering
 * query results.
 * 
 * @author AdNovum Informatik AG
 */
@AdnTrace
public abstract class Restrictions {

    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(Restrictions.class);

	/**
	 * Creates a filtering expression like <code>x.propertyName = :propertyName</code>, where
	 * <code>:propertyName</code> is an input parameter in the prepared statement created
	 * by the DAO and <code>value</code> will be assigned to it dynamically.
	 * 
	 * @param propertyName
	 * @param value
	 * @throws NullPointerException if propertyName was null
	 */
	public final static Expression eq(String propertyName, Object value) {
		if (value == null) {
			return new NullExpression(propertyName);
		}
		return new SimpleExpression(propertyName, value, ComparisonOperator.EQUALS);
	}
	
	/**
	 * Creates a filtering expression like <code>x.propertyName <> :propertyName</code>, where
	 * <code>:propertyName</code> is an input parameter in the prepared statement created
	 * by the DAO and <code>value</code> will be assigned to it dynamically.
	 * 
	 * @param propertyName
	 * @param value
	 * @throws NullPointerException if propertyName was null
	 */
	public final static Expression neq(String propertyName, Object value) {
		if (value == null) {
			return new NotNullExpression(propertyName);
		}
		return new SimpleExpression(propertyName, value, ComparisonOperator.NOT_EQUALS);
	}

	/**
	 * Creates a filtering expression like <code>x.propertyName >= :propertyName</code>, where
	 * <code>:propertyName</code> is an input parameter in the prepared statement created
	 * by the DAO and <code>value</code> will be assigned to it dynamically.
	 * 
	 * @param propertyName
	 * @param value
	 * @throws NullPointerException if propertyName was null
	 */
	public final static Expression ge(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, ComparisonOperator.GREATER_THAN_OR_EQUAL);
	}

	/**
	 * Creates a filtering expression like <code>x.propertyName <= :propertyName</code>, where
	 * <code>:propertyName</code> is an input parameter in the prepared statement created
	 * by the DAO and <code>value</code> will be assigned to it dynamically.
	 * 
	 * @param propertyName
	 * @param value
	 * @throws NullPointerException if propertyName was null
	 */
	public final static Expression le(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, ComparisonOperator.LOWER_THAN_OR_EQUAL);
	}
	
	/**
	 * Creates a filtering expression like <code>x.propertyName < :propertyName</code>, where
	 * <code>:propertyName</code> is an input parameter in the prepared statement created
	 * by the DAO and <code>value</code> will be assigned to it dynamically.
	 * 
	 * @param propertyName
	 * @param value
	 * @throws NullPointerException if propertyName was null
	 */
	public final static Expression lt(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, ComparisonOperator.LOWER_THAN);
	}
	
	/**
	 * Creates a filtering expression like <code>x.propertyName > :propertyName</code>, where
	 * <code>:propertyName</code> is an input parameter in the prepared statement created
	 * by the DAO and <code>value</code> will be assigned to it dynamically.
	 * 
	 * @param propertyName
	 * @param value
	 * @throws NullPointerException if propertyName was null
	 */
	public final static Expression gt(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, ComparisonOperator.GREATER_THAN);
	}
	
	/**
	 * Creates a filtering expression like <code>x.propertyName like :propertyName</code>
	 * (case sensitive), where <code>:propertyName</code> is an input parameter in
	 * the prepared statement created by the DAO and <code>value</code> will be assigned
	 * to it dynamically.
	 * 
	 * Please have a look at the method that takes escape characters as well!
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public final static Expression like(String propertyName, String value) {
		return like(propertyName, value, false, ' ');
	}
	
	/**
	 * @see Expression like(String propertyName, String value)
	 * 
	 * Additional, you can define escape characters here, example how to use it:
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
	 * @param propertyName
	 * @param value
	 * @param useEscape
	 * @param escapeCharacter
	 * @return
	 */
	public final static Expression like(String propertyName, String value, boolean useEscape, char escapeCharacter) {
		return new LikeExpression(propertyName, value, true, useEscape, escapeCharacter);
	}

	/**
	 * Creates a filtering expression like <code>x.propertyName like :propertyName</code>
	 * (case insensitive), where <code>:propertyName</code> is an input parameter in
	 * the prepared statement created by the DAO and <code>value</code> will be assigned
	 * to it dynamically.
	 * 
	 * Please have a look at the method that takes escape characters as well!
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public final static Expression ilike(String propertyName, String value) {
		return ilike(propertyName, value, false, ' ');
	}
	
	/**
	 * @see Expression ilike(String propertyName, String value)
	 * 
	 * Additional, you can define escape characters here, example how to use it:
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
	 * @param propertyName
	 * @param value
	 * @param useEscape
	 * @param escapeCharacter
	 * @return
	 */
	public final static Expression ilike(String propertyName, String value, boolean useEscape, char escapeCharacter) {
		if (value != null) {
			value = value.toUpperCase();
		}
		return new LikeExpression(propertyName, value, false, useEscape, escapeCharacter);
	}
	
	/**
	 * Creates a filtering expression like <code>x.propertyName in (:propertyName_1, :propertyName_2, ...)</code>, where
	 * <code>:propertyName_X</code> is an input parameter in the prepared statement created
	 * by the DAO and corresponding <code>value</code> will be assigned to it dynamically.
	 * 
	 * @param propertyName
	 * @param values
	 * @throws NullPointerException if propertyName was null
	 * @throws IllegalArgumentException if values is null or an empty list
	 */
	public final static Expression in(String propertyName, List values) {
		if (values == null) {
			throw new IllegalArgumentException("at least one element must be present in the value list!");
		}
		Set reducedValues = new HashSet(values);
		return Restrictions.in(propertyName, reducedValues);
	}
	
	/**
	 * Creates a filtering expression like <code>x.propertyName in (:propertyName_1, :propertyName_2, ...)</code>, where
	 * <code>:propertyName_X</code> is an input parameter in the prepared statement created
	 * by the DAO and corresponding <code>value</code> will be assigned to it dynamically.
	 * 
	 * @param propertyName
	 * @param values
	 * @throws NullPointerException if propertyName was null
	 * @throws IllegalArgumentException if values is null or an empty list
	 */
	public final static Expression in(String propertyName, Set values) {
		if (values.size() <= Constant.SQL_IN_STATEMENT_MAX_NUM_OF_LIST_ELEMENTS) {
			return new InExpression(propertyName, values);
		}

		int limit = Constant.SQL_IN_STATEMENT_MAX_NUM_OF_LIST_ELEMENTS;
		final List<Expression> listOfLimitedSizeInExpressions = new ArrayList<Expression>();
		int totalSize = values.size();
		List valuesAsList = new ArrayList(values);
		for (int currentPosition = 0; currentPosition < totalSize; currentPosition += limit) {
			List<ElementType> subList = valuesAsList.subList(currentPosition,
					Math.min(currentPosition + limit, totalSize));
			final Expression limitedSizeInExpression = Restrictions.in(propertyName, subList);
			listOfLimitedSizeInExpressions.add(limitedSizeInExpression);
		}
		return Restrictions.or(listOfLimitedSizeInExpressions);
	}
	
	/**
	 * Creates a filtering expression like <code>x.propertyName in (:propertyName_1, :propertyName_2, ...)</code>, where
	 * <code>:propertyName_X</code> is an input parameter in the prepared statement created
	 * by the DAO and corresponding <code>value</code> will be assigned to it dynamically.
	 * 
	 * @param propertyName
	 * @param values
	 * @throws NullPointerException if propertyName was null
	 * @throws IllegalArgumentException if values is null or an empty list
	 */
	public final static Expression in(String propertyName, Object[] values) {
		if (values == null) {
			throw new IllegalArgumentException("at least one element must be present in the value list!");
		}
		Set reducedValues = new HashSet(Arrays.asList(values));
		return Restrictions.in(propertyName, reducedValues);
	}	

	/**
	 * Creates a filtering expression like <code>x.propertyName is null</code>.
	 * 
	 * @param propertyName
	 * @throws NullPointerException if propertyName was null
	 */
	public final static Expression isNull(String propertyName) {
		return new NullExpression(propertyName);
	}

	/**
	 * Creates a filtering expression like <code>x.propertyName is not null</code>.
	 * 
	 * @param propertyName
	 * @throws NullPointerException if propertyName was null
	 */
	public final static Expression isNotNull(String propertyName) {
		return new NotNullExpression(propertyName);
	}
	
	/**
	 * Creates a filtering expression like <code>not(expression)</code>.
	 * 
	 * @param expression
	 * @throws NullPointerException if expression was null
	 */
	public final static Expression not(Expression expression) {
		return new NotExpression(expression);
	}
	
	/**
	 * Creates a filtering expression like <code>expression1 and expression2 and expression3 and ...</code>.
	 * 
	  * The first two {@link Expression} parameters are compulsory, otherwise using
	 * {@link Conjunction} would not make sense.
	 * 
	 * @param expression1 compulsory parameter
	 * @param expression2 compulsory parameter
	 * @param expressions varargs parameter to append additional members to the conjunction
	 * @throws NullPointerException if expression1 or expression2 was null
	 */
	public final static Expression and(
			Expression expression1,
			Expression expression2,
			Expression... expressions) // NOPMD - gg20081211: see explanation in Conjunction
	{
		return new Conjunction(expression1, expression2, expressions);
	}
	
	/**
	 * Creates a filtering expression like <code>expression1 or expression2 or expression3 or ...</code>.
	 * 
	 * The first two {@link Expression} parameters are compulsory, otherwise using
	 * {@link Disjunction} would not make sense.
	 * 
	 * @param expression1 compulsory parameter
	 * @param expression2 compulsory parameter
	 * @param expressions varargs parameter to append additional members to the disjunction
	 * @throws NullPointerException if expression1 or expression2 was null
	 */
	public final static Expression or(
			Expression expression1,
			Expression expression2,
			Expression... expressions) // NOPMD - gg20081211: see explanation in Disjunction
	{
		return new Disjunction(expression1, expression2, expressions);
	}

	public final static Expression or(List<Expression> expressions) {
		if (expressions == null) {
			throw new NullPointerException("a disjunction must consist of at least two not-null operands!");
		}
		if (expressions.isEmpty()) {
			throw new NullPointerException("a disjunction must consist of at least two not-null operands!");
		}
		if (expressions.size() == 1) {
			return expressions.get(0);
		}
		Expression expression1 = expressions.get(0);
		Expression expression2 = expressions.get(1);
		Expression[] expressionz = null;
		if (expressions.size() > 2) {
			List<Expression> subList = expressions.subList(2, expressions.size());
			if (subList != null) {
				expressionz = subList.toArray(new Expression[0]);
			}
		}
		return new Disjunction(expression1, expression2, expressionz);
	}
	
	public final static Expression and(List<Expression> expressions) {
		if (expressions == null) {
			throw new NullPointerException("a conjunction must consist of at least two not-null operands!");
		}
		if (expressions.isEmpty()) {
			throw new NullPointerException("a conjunction must consist of at least two not-null operands!");
		}
		if (expressions.size() == 1) {
			return expressions.get(0);
		}

		Expression expression1 = expressions.get(0);
		Expression expression2 = expressions.get(1);
		Expression[] expressionz = null;
		if (expressions.size() > 2) {
			List<Expression> subList = expressions.subList(2, expressions.size());
			if (subList != null) {
				expressionz = subList.toArray(new Expression[0]);
			}
		}
		return new Conjunction(expression1, expression2, expressionz);
	}
	
	/**
	 * Creates an {@link Expression} which resolves to the same JPA query as passed in the parameter.
	 * It is the responsibility of the developer that the query is syntactically correct and that it
	 * will fit into the context of the whole statement. Also, the compilation of a proper value map
	 * for any occurring input parameters is the task of the API user.
	 * @param jpaqlExpression a custom JPA QL query
	 * @param values a map, which contains all values to be assigned to the contained named parameters
	 * @return an {@link Expression} which resolves exactly to the passed in jpaqlExpression parameter
	 * @throws NullPointerException is jpaqlExpression was null
	 */
	public final static Expression jpaqlExpression(String jpaqlExpression,
			Map<String, Object> values) {
		return new JpaQLExpression(jpaqlExpression, values);
	}

	/**
	 * Creates an {@link Expression}, which resolves to the <code>1 = 1</code> tautology.
	 */
	public final static Expression trueExpression() {
		return TrueExpression.INSTANCE;
	}
	
	/**
	 * Creates an {@link Expression}, which resolves to the <code>1 = 0</code> contradiction.
	 */
	public final static Expression falseExpression() {
		return FalseExpression.INSTANCE;
	}
	
}
