/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa;

import java.util.List;

import javax.persistence.NonUniqueResultException;

import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.JPAQueryImpl;
import ch.nevis.gdi.jpa.filter.SelectExpression;

/**
 * @author AdNovum Informatik AG
 */
public interface ReadOnlyDao<T extends BaseEntity> {
    
	/**
	 * Finds an entity by its identifier.
	 * @param id
	 * @return an entity of type <b>T</b> or <code>null</code>, if nothing found
	 */
	T findById(Object id);
	
	/**
	 * Same as {@link #findNRecs(int, int, Filter)}, only without
	 * limitations to result set size.
	 * @param filter
	 * @return List of entities of type <b>T</b>, never returns <code>null</code>
	 */
	List<T> find(Filter<T> filter);
	
	/**
	 * Used to retrieve results of simple queries that are expected to return entities
	 * filtered by the conditions in {@link Filter}.
	 * <br>
	 * The selection will be performed on the table or view represented by the
	 * entity of type <b>T</b> and the results are automatically cast to entities.
	 * 
	 * @param skip Set the first result to be retrieved.
	 * @param num Set a limit upon the number of objects to be retrieved.
	 * @param filter
	 * @return List of entities of type <b>T</b>, never returns <code>null</code>
	 */
	List<T> findNRecs(int skip, int num, Filter<T> filter);
	
	/**
	 * Performs a search filtered by the conditions in {@link Filter} on the
	 * table or view represented by the entity of type <b>T</b> and returns
	 * a single entity when found.
	 * @param filter
	 * @return an entity of type <b>T</b> or <code>null</code>, if nothing found
	 * @throws NonUniqueResultException if more than one result
	 */
	T findSingle(Filter<T> filter);

	/**
	 * Performs a search filtered by the conditions in {@link JPAQuery} on the
	 * table or view represented by the entity of type <b>T</b> and returns
	 * a single result (usually a number of type Long) when found.
	 * @param filter
	 * @return a simgle result of type Object or <code>null</code>, if nothing found
	 * @throws NonUniqueResultException if more than one result
	 */
	Object findSingle(JPAQuery<T> jpaQuery);
	
	/**
	 * Same as {@link #findNRecs(int, int, JPAQuery)}, only without
	 * limitations to result set size.
	 * @param jpaQuery
	 * @return List of Object or Object[] depending on the number of select
	 * expressions in the query. It never returns <code>null</code>.
	 */
	List find(JPAQuery<T> jpaQuery);
	
	/**
	 * Used to retrieve results of complex queries that are expected not
	 * only to return entities but a custom selection of fields and
	 * aggregations ({@link SelectExpression}) instead filtered by the
	 * conditions in {@link Filter}.
	 * <br>
	 * The selection will be performed on the table or view represented by the
	 * entity of type <b>T</b> but the results cannot be cast to an entity
	 * automatically and the return type does not use generics.
	 * <br>
	 * <br>
	 * The elements of the result of a Java Persistence query whose SELECT clause
	 * consists of more than one select expressions are of type Object[]. If the SELECT
	 * clause consists of only one select expression, the elements of the query result
	 * are of type Object.
	 * <br>
	 * 
	 * @param skip Set the first result to be retrieved.
	 * @param num Set a limit upon the number of objects to be retrieved.
	 * @param jpaQuery
	 * @return List of Object or Object[] depending on the number of select
	 * expressions in the query. It never returns <code>null</code>.
	 */
	List findNRecs(int skip, int num, JPAQuery<T> jpaQuery);
	
	/**
	 * Counts returned rows from the table represented by the entity of type <b>T</b>
	 * filtered by the conditions in {@link Filter}.
	 * @param filter
	 * @return number of returned rows
	 */
	long count(Filter<T> filter);
	
	/**
	 * Convenience method for creating an entity specific filter without using the constructor
	 * of {@link FilterImpl}.
	 * @return
	 */
	Filter<T> createFilter();

	/**
	 * Convenience method for creating an entity specific JPAQuery without using the constructor
	 * of {@link JPAQueryImpl}.
	 * @return
	 */
	JPAQuery<T> createJPAQuery();

	
}
