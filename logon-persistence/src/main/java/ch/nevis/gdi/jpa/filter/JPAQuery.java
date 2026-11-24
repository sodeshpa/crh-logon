/*
 * Author			 : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

/**
 * An object used to create regular JPA queries (and JDBC prepared
 * statements, in the DAOs) programmatically to avoid JPA Query Language
 * statements showing up directly in the business tier.
 * <br>
 * {@link JPAQuery} was written to support analytic query compilation,
 * like counting averages, sums, and selecting custom fields of an entity
 * next to them.
 * <br>
 * <b>Note</b>: In those business case where there is no need to select any
 * additional or special values, using a {@link Filter} and the corresponding
 * methods in the DAOs will just be enough.
 * <br>
 * By default, calling {@link JPAQuery#toJPAQL()} on a new instance of
 * {@link JPAQuery} will return the following JPA query:
 * <code>select x from T x</code>.
 * The API user can extend this very simple query by adding other
 * select expressions, the corresponding group-bys and setting a
 * self-compiled {@link Filter}.<br>
 * 
 * @author AdNovum Informatik AG
 * 
 * @see JPAQueryImpl
 * @see SelectExpression
 * @see Filter
 * @see FilterImpl
 */
public interface JPAQuery<T> extends Expression {
	
	/**
	 * Adds a {@link SelectExpression} to the query to be resolved as:
	 * <code>select expression1, expression2, expression3, ...</code>.
	 * @param expression
	 */
	void addSelect(SelectExpression expression);
	
	/**
	 * Adds a {@link GroupBy} to the query to be resolved as:
	 * <code>... group by expression1, expression2, expression3, ...</code>.<br>
	 * {@link GroupBy} can be used together with {@link AggregateExpression},
	 * making it the API user's responsibility to create a regular JPA query.
	 * @param propertyName, any valid field of an entity as free text but 
	 * using the "ATTR_..." constants of the entites instead is more preferred.
	 */
	void addGroupBy(String propertyName);

	/**
	 * Gets the {@link Filter} optionally set on the query.
	 * @return {@link Filter} if set, otherwise <code>null</code>.
	 */
	Filter<T> getFilter();
	
	/**
	 * Sets the {@link Filter} on the query to be resolved as:
	 * <code>... where expression1 and expression2 and expression3 ...</code>.<br>
	 * Setting no {@link Filter} or <code>null</code> will result in
	 * not generating a where-block in the query.
	 * @param filter
	 */
	void setFilter(Filter<T> filter);
	
	/**
	 * Determines whether the query results should be distinct or not. The default is <code>false</code>.
	 * @param distinct
	 */
	void setDistinct(boolean distinct);
	
}
