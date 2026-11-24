/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;

import com.crh.logon.constants.Constant;

public class DbUtils {

	private DbUtils() { }

	public static List<Order> convertSortOrdersToOrders(List<SortOrder> sortOrders)
	{
		List<Order> orderByExps = new LinkedList<Order>();
		
		if (sortOrders != null) {
			for (SortOrder sortOrder : sortOrders) {
				orderByExps.add(sortOrder.getOrder());
			}
		}
		
		return orderByExps;
	}

	/**
	 * Deprecated. Use directly {@link Restrictions#in(String, Set)}, {@link Restrictions#in(String, List)} 
	 * or {@link Restrictions#in(String, Object[])} instead.
	 *
	 * Creates an equivalent Expression to 'Restrictions.in(attributeID,unlimitedSizeListOfIds)'
	 * which won't violate Oracle's limit for the count of elements listed in one IN() expression.
	 * 
	 * First it splits the long list of elements into limited size sublists, then 
	 * creates IN() expressions from the sublists. Finally it OR-es them together.
	 * 
	 * If you have a non-Long restriction list at hand, you should use the generic
	 * {@link #createSplittedInRestriction(String, List)} instead and specify the type parameter.
	 * 
	 * @param attributeID
	 * @param unlimitedSizeListOfIds
	 * @return Expression consisting of limited size IN()s OR-ed together. 
	 * @Deprecated
	 */
	public static Expression createSplittedInRestrictionOfIds(String attributeID,
			List<Long> unlimitedSizeListOfIds) {
		return DbUtils.<Long> createSplittedInRestriction(attributeID, unlimitedSizeListOfIds);
	}

	/**
	 * Deprecated. Use directly {@link Restrictions#in(String, Set)}, {@link Restrictions#in(String, List)} 
	 * or {@link Restrictions#in(String, Object[])} instead.
	 * Generic version of {@link #createSplittedInRestrictionOfIds(String, List)}.
	 * Use this when the restriction list does not consist of Long elements.
	 * 
	 * @param attributeID
	 * @param unlimitedSizeListOfIds
	 * @return Expression consisting of limited size IN()s OR-ed together. 
	 * @Deprecated
	 */
	public static <ElementType> Expression createSplittedInRestriction(String attributeID,
			List<ElementType> unlimitedSizeListOfIds) {
		/* Avoid extra work if it is not necessary. */
		if (unlimitedSizeListOfIds.size() <= Constant.SQL_IN_STATEMENT_MAX_NUM_OF_LIST_ELEMENTS) {
			return Restrictions.in(attributeID, unlimitedSizeListOfIds);
		}
		int limit = Constant.SQL_IN_STATEMENT_MAX_NUM_OF_LIST_ELEMENTS;
		final List<Expression> listOfLimitedSizeInExpressions = new ArrayList<Expression>();
		int totalSize = unlimitedSizeListOfIds.size();
		for (int currentPosition = 0; currentPosition < totalSize; currentPosition += limit) {
			List<ElementType> subList = unlimitedSizeListOfIds.subList(currentPosition,
					Math.min(currentPosition + limit, totalSize));
			final Expression limitedSizeInExpression = Restrictions.in(attributeID, subList);
			listOfLimitedSizeInExpressions.add(limitedSizeInExpression);
		}

		return Restrictions.or(listOfLimitedSizeInExpressions);
	}
}