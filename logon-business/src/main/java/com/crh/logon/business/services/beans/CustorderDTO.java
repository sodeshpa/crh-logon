/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;

import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.services.utils.CustorderSplitWrapper;
import com.crh.logon.persistence.entities.common.CustorderEntity;


public class CustorderDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private CustorderEntity custorderEntity;
	private DeliverySettingSalesAreaMap deliverySettingSalesAreaMap;
	CustorderSplitWrapper splitWrapper;

	/**
	 *
	 */
	public CustorderDTO() {
	}

	public CustorderDTO(CustorderEntity custorderEntity) {
		this(custorderEntity, null);
	}

	public CustorderDTO(CustorderEntity custorderEntity, DeliverySettingSalesAreaMap deliverySettingSalesAreaMap) {
		this.custorderEntity = custorderEntity;
		this.deliverySettingSalesAreaMap = deliverySettingSalesAreaMap;
	}

	public CustorderDTO(CustorderEntity custorderEntity, DeliverySettingSalesAreaMap deliverySettingSalesAreaMap,
			CustorderSplitWrapper splitWrapper) {
		this.custorderEntity = custorderEntity;
		this.deliverySettingSalesAreaMap = deliverySettingSalesAreaMap;
		this.splitWrapper = splitWrapper;
	}
	/**
	 * @return the custorderEntity
	 */
	public CustorderEntity getCustorderEntity() {
		return custorderEntity;
	}

	/**
	 * @return the deliverySettingSalesAreaMap
	 */
	public DeliverySettingSalesAreaMap getDeliverySettingSalesAreaMap() {
		return deliverySettingSalesAreaMap;
	}

	/**
	 * @param custorderEntity the custorderEntity to set
	 */
	public void setCustorderEntity(CustorderEntity custorderEntity) {
		this.custorderEntity = custorderEntity;
	}

	/**
	 * @param deliverySettingSalesAreaMap the deliverySettingSalesAreaMap to set
	 */
	public void setDeliverySettingSalesAreaMap(
			DeliverySettingSalesAreaMap deliverySettingSalesAreaMap) {
		this.deliverySettingSalesAreaMap = deliverySettingSalesAreaMap;
	}

	public CustorderSplitWrapper getSplitWrapper() {
		return splitWrapper;
	}

	public void setSplitWrapper(CustorderSplitWrapper splitWrapper) {
		this.splitWrapper = splitWrapper;
	}
}
