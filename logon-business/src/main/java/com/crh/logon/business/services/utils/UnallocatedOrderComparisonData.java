/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;


import org.apache.commons.lang.Validate;

import com.crh.logon.persistence.entities.views.DispoExtOrderListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderListEntity;

/**
 * Data holder for comparing unallocated orders in scheduling screen
 */
public class UnallocatedOrderComparisonData implements Comparable<UnallocatedOrderComparisonData> {

	/**
	 * This value will be returned when wrong {@link OrderWrapper}s are passed to the {@link #compareTo(UnallocatedOrderComparisonData)} method
	 */
	private static final int SORTING_ORDER_IS_NOT_IMPORTANT_DUE_TO_WRONG_PARAMETERS = 0;

	/**
	 * Order Status code
	 */
	private Long orderStatusCode;

	/**
	 * Id of Commercial Carrier
	 */
	private Long commercialCarrierId;

	/**
	 * Id of Executing Carrier
	 */
	private Long executingCarrierId;

	/**
	 * True, if the order is {@link DispoOrderListEntity}, false
	 * if the order is {@link DispoExtOrderListEntity}.
	 */
	private boolean orderIsDispoOrderListEntity;

	/**
	 * Constructor.
	 * @param orderStatusCode status code of the order, <b>should not be null</b>.
	 * @param commercialCarrierId commercial carrier Id, could be null.
	 * @param executingCarrierId executing carrier id, could be null.
	 * @param orderIsDispoOrderListEntity <i>true</i> if the order is {@link DispoOrderListEntity}, <i>false</i> if the order is {@link DispoExtOrderListEntity}.
	 */
	public UnallocatedOrderComparisonData(Long orderStatusCode, Long commercialCarrierId,
			Long executingCarrierId, boolean orderIsDispoOrderListEntity) {
		super();
		
		Validate.notNull(orderStatusCode, "Order Status Code should not be null");
		
		this.orderStatusCode = orderStatusCode;
		this.commercialCarrierId = commercialCarrierId;
		this.executingCarrierId = executingCarrierId;
		this.orderIsDispoOrderListEntity = orderIsDispoOrderListEntity;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(UnallocatedOrderComparisonData otherUnallocatedOrderComparisonData) {

		if (otherUnallocatedOrderComparisonData == null
				|| !this.hasSameOrderType(otherUnallocatedOrderComparisonData)) {
			return SORTING_ORDER_IS_NOT_IMPORTANT_DUE_TO_WRONG_PARAMETERS;
		}

		int comparisonResult = LogonUtil.SORTING_ORDER_IS_EQUAL;

		// compare by order status code
		comparisonResult = LogonUtil.nullSafeCompare(this.getOrderStatusCode(),
				otherUnallocatedOrderComparisonData.getOrderStatusCode());
		if (comparisonResult != LogonUtil.SORTING_ORDER_IS_EQUAL) {
			return comparisonResult;
		}

		// compare by common carrier
		comparisonResult = LogonUtil.nullSafeCompare(this.getCommercialCarrierId(),
				otherUnallocatedOrderComparisonData.getCommercialCarrierId());
		if (comparisonResult != LogonUtil.SORTING_ORDER_IS_EQUAL) {
			return comparisonResult;
		}

		// compare by executing carrier
		comparisonResult = LogonUtil.nullSafeCompare(this.getExecutingCarrierId(),
				otherUnallocatedOrderComparisonData.getExecutingCarrierId());

		return comparisonResult;
	}

	/**
	 * @return the commercialCarrierId
	 */
	public Long getCommercialCarrierId() {
		return commercialCarrierId;
	}

	/**
	 * @return the executingCarrierId
	 */
	public Long getExecutingCarrierId() {
		return executingCarrierId;
	}

	/**
	 * @return the orderStatusCode
	 */
	public Long getOrderStatusCode() {
		return orderStatusCode;
	}

	/**
	 * Compares this {@link UnallocatedOrderComparisonData}'s order type to the <code>otherUnallocatedOrderComparisonData</code>'s type.
	 * Returns true, if the other order is not null and has the same type {@link DispoOrderListEntity} / {@link DispoExtOrderListEntity}, otherwise returns false.
	 * @param otherUnallocatedOrderComparisonData The other {@link UnallocatedOrderComparisonData} to compare against. 
	 * @return true, if the two orders are not null and have the same type, false otherwise.
	 */
	public boolean hasSameOrderType(
			UnallocatedOrderComparisonData otherUnallocatedOrderComparisonData) {
		if (otherUnallocatedOrderComparisonData != null) {
			boolean sameType = (this.isOrderIsDispoOrderListEntity() == otherUnallocatedOrderComparisonData.isOrderIsDispoOrderListEntity());
			return sameType;
		}
		return false;
	}

	/**
	 * @return the orderIsDispoOrderListEntity
	 */
	public boolean isOrderIsDispoOrderListEntity() {
		return orderIsDispoOrderListEntity;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UnallocatedOrderComparisonData [orderStatusCode=" + orderStatusCode
				+ ", commercialCarrierId=" + commercialCarrierId + ", executingCarrierId="
				+ executingCarrierId + ", orderIsDispoOrderListEntity="
				+ orderIsDispoOrderListEntity + "]";
	}
	
	

}
