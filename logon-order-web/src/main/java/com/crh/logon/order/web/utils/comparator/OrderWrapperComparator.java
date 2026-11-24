/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils.comparator;

import java.util.Comparator;

import com.crh.logon.business.services.utils.OrderWrapper;
import com.crh.logon.business.services.utils.UnallocatedOrderComparisonData;

/**
 * Comparator for {@link OrderWrapper}s for the Scheduling screen. The 
 * OrderWrappers are sorted based on
 * <ol>
 * <li>status code
 * <li>commercial carrier Id
 * <li>executing carrier Id
 * </ol>
 * Sorting is needed to have the correct grouping behaviour in the Open Orders list in the scheduling screen, since the
 * JavaScript code groups only adjacent sub-orders which are identical.
 */
public class OrderWrapperComparator implements Comparator<OrderWrapper> {
	
	/**
	 * This value will be returned when wrong {@link OrderWrapper}s are passed to the {@link #compare(OrderWrapper, OrderWrapper)} method
	 */
	private static final int SORTING_ORDER_IS_NOT_IMPORTANT_DUE_TO_WRONG_PARAMETERS = 0;
	
	@Override
	/**
	 * Compares two OrderWrapper. If any of them are null, or any of the orders are null, returns 0.
	 * It also returns 0, when the class of the order objects are different or
	 * not DispoOrderListEntity or DispoExtOrderListEntity. 
	 * @param orderWrapper1 first OrderWrapper 
	 * @param orderWrapper2 second OrderWrapper
	 * @return the integer result of comparison
	 */
	public int compare(OrderWrapper orderWrapper1, OrderWrapper orderWrapper2) {
		
		if (orderWrapper1 == null || 
		    orderWrapper2 == null || 
			orderWrapper1.getUnallocatedOrderComparisonData() == null || 
			orderWrapper2.getUnallocatedOrderComparisonData() == null) {
			return SORTING_ORDER_IS_NOT_IMPORTANT_DUE_TO_WRONG_PARAMETERS;
		}
		
		UnallocatedOrderComparisonData unallocatedOrder1ComparisonData = orderWrapper1.getUnallocatedOrderComparisonData();
		UnallocatedOrderComparisonData unallocatedOrder2ComparisonData = orderWrapper2.getUnallocatedOrderComparisonData();

		if (!unallocatedOrder1ComparisonData.hasSameOrderType(unallocatedOrder2ComparisonData)) {
				return SORTING_ORDER_IS_NOT_IMPORTANT_DUE_TO_WRONG_PARAMETERS;
		}

		return unallocatedOrder1ComparisonData.compareTo(unallocatedOrder2ComparisonData);
	}
}