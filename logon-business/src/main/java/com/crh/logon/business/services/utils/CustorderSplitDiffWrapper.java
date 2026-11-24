/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.ArrayList;
import java.util.List;

import com.crh.logon.persistence.entities.common.OrderEntity;

/**
 * This wrapper is used to have a passable result of the differences between two
 * custorders. The orders contained in this wrapper are distributed into the
 * different lists.
 * 
 * @author AdNovum Informatik AG
 */
public class CustorderSplitDiffWrapper {

	private List<OrderEntity> tobeCreatedOrders = new ArrayList<OrderEntity>();
	private List<OrderEntity> tobeDeletedOrders = new ArrayList<OrderEntity>();
	private List<OrderSplitDiffWrapper> tobeEditedOrders = new ArrayList<OrderSplitDiffWrapper>();
	
	public List<OrderEntity> getTobeCreatedOrders() {
		return tobeCreatedOrders;
	}
	public void setTobeCreatedOrders(List<OrderEntity> tobeCreatedOrders) {
		this.tobeCreatedOrders = tobeCreatedOrders;
	}
	public List<OrderEntity> getTobeDeletedOrders() {
		return tobeDeletedOrders;
	}
	public void setTobeDeletedOrders(List<OrderEntity> tobeDeletedOrders) {
		this.tobeDeletedOrders = tobeDeletedOrders;
	}
	public List<OrderSplitDiffWrapper> getTobeEditedOrders() {
		return tobeEditedOrders;
	}
	public void setTobeEditedOrders(List<OrderSplitDiffWrapper> tobeEditedOrders) {
		this.tobeEditedOrders = tobeEditedOrders;
	}
	
}
