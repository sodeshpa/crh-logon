/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;

/**
 * Represents a part of an assignment for a simple vehicle
 * @author AdNovum Informatik AG
 */
public class SimpleVehicleAssignmentPart implements Serializable
{
	private static final long serialVersionUID = 1L;

	final private Long orderId;
	
	final private Long orderNumber;
	
	final private Long orderStatusCd;
	
	final private String shiptoName;
	
	final private String materialDescTxt;
	
	final private String orderStatusTxt;
	
	/**
	 * Creates a new simple vehicle assignment part.
	 * @param orderId The ID of the associated delivery order (FK tlgnc_order) or
	 * <code>null</code> if this assignment part is not associated to an order.
	 * @param orderNumber The human readable number of the order. Can be null if and only if
	 * <code>orderId</code> is null.
	 * @param orderStatusCd The status of the order (FK tlgnr_order_status_bs).
	 * Can be null if and only if there <code>orderId</code> is null.
	 * @param shiptoName The name of the order's shipto (destination). Can be null
	 * if and only if <code>orderId</code> is null.
	 * @throws IllegalArgumentException if any of the required arguments is null
	 */
	public SimpleVehicleAssignmentPart(
			Long orderId,
			Long orderNumber,
			Long orderStatusCd,
			String shiptoName,
			String materialDescTxt,
			String orderStatusTxt)
	{
		if (orderId == null) {
			throw new IllegalArgumentException("Order it must not be null. Could it be that there is" +
					" an assignment part of type \"unload\" in the DB that does not have an order ID set?");
		}
		if (orderNumber == null) {
			throw new IllegalArgumentException("orderNumber must not be null.");
		}
		if (orderStatusCd == null) {
			throw new IllegalArgumentException("orderStatusCd must not be null.");
		}
		if (shiptoName == null) {
			throw new IllegalArgumentException("shiptoName must not be null.");
		}
		if (materialDescTxt == null) {
			throw new IllegalArgumentException("materialDescTxt must not be null.");
		}
		if (orderStatusTxt == null) {
			throw new IllegalArgumentException("orderStatusTxt must not be null.");
		}
		
		this.orderId = orderId;
		this.orderNumber = orderNumber;
		this.orderStatusCd = orderStatusCd;
		this.shiptoName = shiptoName;
		this.materialDescTxt = materialDescTxt;
		this.orderStatusTxt = orderStatusTxt;
	}

	/**
	 * @return the orderId; not null
	 */
	public Long getOrderId() {
		return orderId;
	}

	/**
	 * @return the orderNumber; not null
	 */
	public Long getOrderNumber() {
		return orderNumber;
	}

	/**
	 * @return the orderStatusCd; not null
	 */
	public Long getOrderStatusCd() {
		return orderStatusCd;
	}

	/**
	 * @return the shiptoName; not null
	 */
	public String getShiptoName() {
		return shiptoName;
	}

	/**
	 * @return the materialDescTxt; not null
	 */
	public String getMaterialDescTxt() {
		return materialDescTxt;
	}

	/**
	 * @return the orderStatusTxt; not null
	 */
	public String getOrderStatusTxt() {
		return orderStatusTxt;
	}
	
}
