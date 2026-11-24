/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.OrderSpiBsEntity;

/**
 * The data model behind one row of the "Customer Order - Single Delivery Order Split" Popup Screen.
 * Keep in mind that currently customer orders may only contain one bulk material.
 * 
 * FIXME (romek Jul 25, 2011): Currently the data model is flat (not using the properties of the order
 * entity directly), because I could not find a way to send back the modified data back to the bean with
 * hierarchy preserved.
 * 
 * @author AdNovum Informatik AG
 */
public final class SplittedOrderData implements Serializable {

	private static final long serialVersionUID = -4912782694070993560L;

	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(SplittedOrderData.class);

	private OrderEntity order;
	private String deliveryFromDatStr;
	private String deliveryToDatStr;
	private String deliveryFromTime; 
	private String deliveryToTime;
	private boolean onTime;
	private ShiptoEntity shipTo; // needed for default date/time
	private List<SplittedOrderItemData> splittedOrderItemDatas = new ArrayList<SplittedOrderItemData>();
	private Boolean onHoldOrderAndLockedFlag;
	private Boolean exactCustorderQtyFlag;
	private int index;
	private DeliveryType deliveryType;
	private String serviceProcessIndicatorId;
	private TimeZone shiptoTimeZone = TimeZone.getAppTimeZone();
	
	public enum DeliveryType {
		BIMODAL, PICKUP, RAIL, ROAD, SERVICE, SHIPPICKUP, BLOCKTRAINPICKUP, RETURN_PICKUP, RETURN_DELIVERED;
	}

	/**
	 * Constructor for exclusive use by DWR.
	 */
	public SplittedOrderData() {
	}

	/**
	 * 
	 * @param order
	 * @param shipTo
	 * @param deliveryType
	 * @param exactCustorderQtyFlag
	 * @param index
	 * @param shiptoTimeZone used for date formatting and calculations
	 */
	public SplittedOrderData(OrderEntity order, ShiptoEntity shipTo, DeliveryType deliveryType, Boolean exactCustorderQtyFlag, int index, TimeZone shiptoTimeZone) {
		// [[
		Object[][] params = { { "order", order }, { "shipTo", shipTo },
				{ "deliveryType", deliveryType },
				{ "exactCustorderQtyFlag", exactCustorderQtyFlag }, { "index", index + "" },
				{ "shiptoTimeZone", shiptoTimeZone } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "SplittedOrderData", params);
		}
		// ]]
		try {
			this.shiptoTimeZone = shiptoTimeZone;
			this.order = order;
			this.shipTo = shipTo;
			this.deliveryType = deliveryType;
			this.exactCustorderQtyFlag = exactCustorderQtyFlag;
			this.index = index;
			initialize();
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	/**
	 * Initializer the details of this order.
	 * 
	 * TODO (romek Jul 21, 2011): Some parts are common with OrderActionSupport#initOrderDetails.
	 * 
	 * @param order
	 * @param shipTo
	 */
	public SplittedOrderData(OrderEntity order, ShiptoEntity shipTo, DeliveryType deliveryType, Boolean exactCustorderQtyFlag, int index) {
		// [[
		Object[][] params = {{"order", order},
							{"shipTo", shipTo},
							{"index", index + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "SplittedOrderData", params);
		}
		// ]]
		try {
			
			this.order = order;
			this.shipTo = shipTo;
			this.deliveryType = deliveryType;
			this.exactCustorderQtyFlag = exactCustorderQtyFlag;
			this.index = index;
			initialize();
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Configures the dates, and the quantities from the order.
	 * Can be called any time to synch if the order's data was changed.
	 */
	void initialize() {
		Date ordersDeliveryFromDat = order.getDeliveryFromDat();
		Date ordersDeliveryToDat = order.getDeliveryToDat();
		
		deliveryFromDatStr = shiptoTimeZone.fmtAsDayMonthYear(ordersDeliveryFromDat);
		deliveryToDatStr = shiptoTimeZone.fmtAsDayMonthYear(ordersDeliveryToDat);
		
		deliveryFromTime = shiptoTimeZone.fmtAsHourMinute(ordersDeliveryFromDat);
		deliveryToTime = shiptoTimeZone.fmtAsHourMinute(ordersDeliveryToDat);
		
		onHoldOrderAndLockedFlag = order.getOnHoldOrderAndLockedFlag();
		
	}

	public OrderEntity getOrder() {
		return order;
	}

	public void setOrder(OrderEntity order) {
		this.order = order;
	}

	public OrderItemEntity getOrderItem() {
		List<OrderItemEntity> orderItems = order.getOrderItems();
		return orderItems.get(0);
	}

	public ShiptoEntity getShipTo() {
		return shipTo;
	}

	public void setShipTo(ShiptoEntity shipTo) {
		this.shipTo = shipTo;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getDeliveryFromDatStr() {
		return deliveryFromDatStr;
	}

	public void setDeliveryFromDatStr(String deliveryFromDatStr) {
		this.deliveryFromDatStr = deliveryFromDatStr;
	}

	public String getDeliveryToDatStr() {
		return deliveryToDatStr;
	}

	public void setDeliveryToDatStr(String deliveryToDatStr) {
		this.deliveryToDatStr = deliveryToDatStr;
	}

	public String getDeliveryFromTime() {
		return deliveryFromTime;
	}

	public void setDeliveryFromTime(String deliveryFromTime) {
		this.deliveryFromTime = deliveryFromTime;
	}

	public String getDeliveryToTime() {
		return deliveryToTime;
	}

	public void setDeliveryToTime(String deliveryToTime) {
		this.deliveryToTime = deliveryToTime;
	}

	public boolean isOnTime() {
		return onTime;
	}

	public void setOnTime(boolean onTime) {
		this.onTime = onTime;
	}

	public Boolean getOnHoldOrderAndLockedFlag() {
		return onHoldOrderAndLockedFlag;
	}

	public void setOnHoldOrderAndLockedFlag(Boolean onHoldOrderAndLockedFlag) {
		this.onHoldOrderAndLockedFlag = onHoldOrderAndLockedFlag;
	}

	/**
	 * Updates values that could be changed on the UI.
	 */
	public void updateFrom(SplittedOrderData other, OrderSpiBsEntity serviceProcessIndicatorEntity ) {
		// [[
		Object[][] params = {{"other", other}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateFrom", params);
		}
		// ]]
		try {
			deliveryFromDatStr = other.getDeliveryFromDatStr();
			deliveryToDatStr = other.getDeliveryToDatStr();
			
			deliveryFromTime = other.getDeliveryFromTime();
			deliveryToTime = other.getDeliveryToTime();
			
			onTime = other.isOnTime();
			onHoldOrderAndLockedFlag = other.getOnHoldOrderAndLockedFlag();
			serviceProcessIndicatorId = other.getServiceProcessIndicatorId();
			
			Date deliveryFromDate = getDeliveryFrom().getTime();
			order.setDeliveryFromDat(deliveryFromDate);
			
			Date deliveryToDate = getDeliveryTo().getTime();
			order.setDeliveryToDat(deliveryToDate);
			
			order.setOnHoldOrderAndLockedFlag(onHoldOrderAndLockedFlag);
			
			order.setSpi(serviceProcessIndicatorEntity);
			
			List<SplittedOrderItemData> srcSplittedOrderItemDatas = other.getSplittedOrderItemDatas();
			for (SplittedOrderItemData srcSplittedOrderItemData : srcSplittedOrderItemDatas) {
				int index = srcSplittedOrderItemData.getIndex();
				SplittedOrderItemData dstSplittedOrderItemData = this.splittedOrderItemDatas.get(index);
				dstSplittedOrderItemData.updateFrom(srcSplittedOrderItemData);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	private Calendar getDeliveryFrom() {
		Long open1From = shipTo.getOpen1From();
		// TODO (romek Jul 21, 2011): read the default open1From value from config
		return OrderUtils.parseDeliveryDateTime(shiptoTimeZone, deliveryFromDatStr,
				deliveryFromTime, open1From, Constant._5_HOURS_IN_MINUTES);
	}

	private Calendar getDeliveryTo() {
		Long open2To = shipTo.getOpen2To();
		// TODO (romek Jul 21, 2011): read the default open2To value from config
		return OrderUtils.parseDeliveryDateTime(shiptoTimeZone, deliveryToDatStr, deliveryToTime,
				open2To, Constant._23_HOURS_IN_MINUTES);
	}

	public List<SplittedOrderItemData> getSplittedOrderItemDatas() {
		return splittedOrderItemDatas;
	}

	public void setSplittedOrderItemDatas(List<SplittedOrderItemData> splittedOrderItemDatas) {
		this.splittedOrderItemDatas = splittedOrderItemDatas;
	}
	
	public DeliveryType getDeliveryType() {
		return deliveryType;
	}
	
	public Boolean getExactCustorderQtyFlag() {
		return exactCustorderQtyFlag;
	}

	public String getServiceProcessIndicatorId() {
		return serviceProcessIndicatorId;
	}

	public void setServiceProcessIndicatorId(String serviceProcessIndicatorId) {
		this.serviceProcessIndicatorId = serviceProcessIndicatorId;
	}

	public TimeZone getShiptoTimeZone() {
		return shiptoTimeZone;
	}

	public void setShiptoTimeZone(TimeZone shiptoTimeZone) {
		this.shiptoTimeZone = shiptoTimeZone;
	}
	
}