/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

import java.util.List;
import java.util.Map;

/**
 * An object used to create regular JPA query fragments programmatically
 * to avoid JPA Query Language statements showing up directly in the
 * business tier. In closer terms, {@link Filter} is responsible for
 * the WHERE and ORDER BY clause of a query and also for temporarily
 * storing input parameters until they are all passed to
 * <code>javax.persistence.Query</code>.
 * <br>
 * {@link Expression}s to be added to {@link Filter} can be created by
 * the {@link Restrictions} factory class and by the static methods of
 * the {@link Order} class.
 * <br>
 * <b>Note</b>: In most of the business cases, using a {@link Filter}
 * and the corresponding methods in the DAOs will just be enough.
 * 
 * @author AdNovum Informatik AG
 * 
 * @see Expression
 * @see Restrictions
 * @see Order
 * @see FilterImpl
 * @see JPAQuery
 */
public interface Filter<T> extends Expression {
	
	/**
	 * Adds filtering expressions and ordering instructions
	 * to the {@link Filter}.
	 * <br>
	 * Elements will be added to the constructed WHERE
	 * clause as conjunctions ("item1 AND item2 AND ...")
	 * except for {@link Order} expressions that are appended to
	 * the ORDER BY clause
	 * 
	 * @param expression Expression
	 * @throws NullPointerException if the expression is null
	 * @throws IllegalArgumentException if a {@link SelectExpression} is passed as expression
	 */
	void addExpression(Expression expression);

	/**
	 * The input parameters that get passed in with the <code>Expression</code>s are
	 * collected into a Map. An entry in the Map can be assigned to the resolved JPA query
	 * using the {@link Query#setParameter(String, Object)} method.
	 * @return A Map containing all input parameters, passed in with the expressions
	 */
	Map<String, Object> getInputParameters();
	
	List<Order> getOrderByExpressions();
	
}
