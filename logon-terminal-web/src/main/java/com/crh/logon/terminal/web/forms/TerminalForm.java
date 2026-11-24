/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.forms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.struts.action.ActionForm;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.web.utils.UserProvider;

public abstract class TerminalForm<TerminalOrderDTOType extends TerminalOrderDTO> extends
		ActionForm {

	private static final long serialVersionUID = 7285348489957616269L;

	protected Boolean ordersLoadableTerminalFlag;

	protected List<TerminalOrderDTOType> orderDTOList;

	private TimeZone shippingPointTimeZone;

	private static Long[] openStatuses = {
		Constant.ORDER_STATUS_NEW, 
		Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
		Constant.ORDER_STATUS_NEW,
		Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
		Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK,
		Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
		Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK,
		Constant.ORDER_STATUS_DISPATCHED,
		Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
		Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK ,
		Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
		Constant.ORDER_STATUS_LOADING
	};

	public static Set openStatusSet = new HashSet();

	public static final Long[] editableStatuses = {
		Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,
		Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK ,
		Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT
	};

	public static Set editableStatusSet = new HashSet();

	static {
		openStatusSet.addAll(Arrays.asList(TerminalForm.getOpenStatuses()));
		editableStatusSet.addAll(Arrays.asList(TerminalForm.editableStatuses));
	}

	public static Set getOpenStatusSet() {
		return openStatusSet;
	}

	public static Set getEditableStatusSet() {
		return editableStatusSet;
	}

	public static Long[] getEditableStatuses() {
		return editableStatuses;
	}

	public Boolean getOrdersLoadableTerminalFlag() {
		return ordersLoadableTerminalFlag;
	}

	public void setOrdersLoadableTerminalFlag(Boolean ordersLoadableTerminalFlag) {
		this.ordersLoadableTerminalFlag = ordersLoadableTerminalFlag;
	}

	public List<TerminalOrderDTOType> getOrderDTOList() {
		return orderDTOList;
	}

	public void setOrderDTOList(List orderList) {
		this.orderDTOList = orderList;
	}

	public static Long[] getOpenStatuses() {
		return openStatuses;
	}

	public static void setOpenStatuses(Long[] openStatuses) {
		TerminalForm.openStatuses = openStatuses;
	}

	public boolean isDisplayingShippingPointTimeZoneNeeded() {
		TimeZone tz = getShippingPointTimeZone();
		return !tz.isSame(UserProvider.getTimezone());
	}

	public TimeZone getShippingPointTimeZone() {
		return shippingPointTimeZone;
	}

	public void setShippingPointTimeZone(TimeZone shippingPointTimeZone) {
		this.shippingPointTimeZone = shippingPointTimeZone;
	}

}
