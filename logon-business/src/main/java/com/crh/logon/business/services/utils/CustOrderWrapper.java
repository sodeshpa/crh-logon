/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.crh.logon.persistence.entities.views.CustorderItemSumQtyEntity;
import com.crh.logon.persistence.entities.views.CustorderViewEntity;


/**
 * @author AdNovum Informatik AG
 */
public class CustOrderWrapper implements Serializable{

	private CustorderViewEntity custorderViewEntity;
	private List<OrderWrapper> orderWrappers;
//	private CustorderSumQuantitiesEntity sumQuantities;
	private CustorderItemSumQtyEntity sumQuantities;
	/** This is used on the UI as the class of the Customer Order. */
	private String custorderHeadClass;
	
	/**
	 * @param custorderViewEntity
	 * @param orderWrappers
	 */
	public CustOrderWrapper(CustorderViewEntity custorderViewEntity, CustorderItemSumQtyEntity custorderItemSumQtyEntity) {
		super();
		this.custorderViewEntity = custorderViewEntity;
		this.orderWrappers = new ArrayList<OrderWrapper>();
		this.sumQuantities = custorderItemSumQtyEntity;	
	}

	/**
	 * @return the orderWrappers
	 */
	public List<OrderWrapper> getOrderWrappers() {
		return orderWrappers;
	}

	/**
	 * @param orderWrappers the orderWrappers to set
	 */
	public void setOrderWrappers(List<OrderWrapper> orderWrappers) {
		this.orderWrappers = orderWrappers;
	}
	
	/**
	 * @param orderWrappers the orderWrappers to set
	 */
	public void addOrderWrapper(OrderWrapper orderWrapper) {
		this.orderWrappers.add(orderWrapper);
	}

	/**
	 * @return the custorderViewEntity
	 */
	public CustorderViewEntity getCustorderViewEntity() {
		return custorderViewEntity;
	}

	/**
	 * @param custorderViewEntity the custorderViewEntity to set
	 */
	public void setCustorderViewEntity(CustorderViewEntity custorderViewEntity) {
		this.custorderViewEntity = custorderViewEntity;
	}

	/**
	 * @return the sumQuantities
	 */
	public CustorderItemSumQtyEntity getSumQuantities() {
		return sumQuantities;
	}

	/**
	 * @param sumQuantities the sumQuantities to set
	 */
	public void setSumQuantities(CustorderItemSumQtyEntity sumQuantities) {
		this.sumQuantities = sumQuantities;
	}

	public String getCustorderHeadClass() {
		return custorderHeadClass;
	}

	public void setCustorderHeadClass(String custorderHeadClass) {
		this.custorderHeadClass = custorderHeadClass;
	}
	
}
