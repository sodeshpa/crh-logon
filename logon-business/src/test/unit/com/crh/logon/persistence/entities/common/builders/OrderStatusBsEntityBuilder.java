/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.entities.common.builders;

import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.constants.Constant;

public class OrderStatusBsEntityBuilder {
	private Long orderStatusCd;

	private Long sortingNo;

	private Boolean possibleTruckStatusFlag;

	private Boolean possibleRailStatusFlag;

	private OrderStatusBsEntityBuilder() {
	}

	public static OrderStatusBsEntityBuilder get() {
		return new OrderStatusBsEntityBuilder();
	}

	public void reset() {
		orderStatusCd = null;
		sortingNo = null;
		possibleTruckStatusFlag = null;
		possibleRailStatusFlag = null;
	}

	public OrderStatusBsEntity build() {
		OrderStatusBsEntity orderStatus = new OrderStatusBsEntity();
		orderStatus.setOrderStatusCd(orderStatusCd);
		orderStatus.setSortingNo(sortingNo);
		orderStatus.setPossibleTruckStatusFlag(possibleTruckStatusFlag);
		orderStatus.setPossibleRailStatusFlag(possibleRailStatusFlag);
		return orderStatus;
	}

	/**
	{@linkplain Constant#ORDER_STATUS_NEW}
	{@linkplain Constant#ORDER_STATUS_TRANSMITTED_TO_POOL}
	{@linkplain Constant#ORDER_STATUS_TRANSMITTED_TO_POOL_ACK}
	{@linkplain Constant#ORDER_STATUS_TRANSMITTED_TO_CARRIER}
	{@linkplain Constant#ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK}
	{@linkplain Constant#ORDER_STATUS_DISPATCHED}
	{@linkplain Constant#ORDER_STATUS_TRANSMITTED_TO_DRIVER}
	{@linkplain Constant#ORDER_STATUS_TRANSMITTED_TO_RAIL}
	{@linkplain Constant#ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK}
	{@linkplain Constant#ORDER_STATUS_ARRIVED_AT_LOADINGPOINT}
	{@linkplain Constant#ORDER_STATUS_LOADING}
	{@linkplain Constant#ORDER_STATUS_LOADED}
	{@linkplain Constant#ORDER_STATUS_SHIPPING}
	{@linkplain Constant#ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT}
	{@linkplain Constant#ORDER_STATUS_UNLOADING}
	{@linkplain Constant#ORDER_STATUS_DELIVERED}
	{@linkplain Constant#ORDER_STATUS_FINISHED}
	{@linkplain Constant#ORDER_STATUS_DELETED}
	*/
	public OrderStatusBsEntityBuilder orderStatusCd(Long orderStatusCd) {
		this.orderStatusCd = orderStatusCd;
		return this;
	}

	public OrderStatusBsEntityBuilder sortingNo(Long sortingNo) {
		this.sortingNo = sortingNo;
		return this;
	}

	public OrderStatusBsEntityBuilder possibleTruckStatusFlag(Boolean possibleTruckStatusFlag) {
		this.possibleTruckStatusFlag = possibleTruckStatusFlag;
		return this;
	}

	public OrderStatusBsEntityBuilder possibleRailStatusFlag(Boolean possibleRailStatusFlag) {
		this.possibleRailStatusFlag = possibleRailStatusFlag;
		return this;
	}

	public static OrderStatusBsEntityBuilder ORDER_STATUS_NEW = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_NEW);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_TRANSMITTED_TO_POOL = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_TRANSMITTED_TO_POOL);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_TRANSMITTED_TO_POOL_ACK = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_TRANSMITTED_TO_CARRIER = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_DISPATCHED = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_DISPATCHED);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_TRANSMITTED_TO_DRIVER = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_TRANSMITTED_TO_RAIL = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_ARRIVED_AT_LOADINGPOINT = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_LOADING = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_LOADING);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_LOADED = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_LOADED);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_SHIPPING = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_SHIPPING);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_UNLOADING = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_UNLOADING);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_DELIVERED = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_DELIVERED);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_FINISHED = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_FINISHED);

	public static OrderStatusBsEntityBuilder ORDER_STATUS_DELETED = OrderStatusBsEntityBuilder.get().orderStatusCd(
			Constant.ORDER_STATUS_DELETED);

}
