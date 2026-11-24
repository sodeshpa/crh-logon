package com.crh.logon.order.web.beans;

import com.crh.logon.persistence.entities.views.OrderMonitoringEntity;
import com.crh.logon.persistence.entities.views.OrderSearchEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class OrderSearchResultBean implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String ATTR_DELIVERY_FROM = "deliveryFrom";
	public static final String ATTR_DELIVERY_TO = "deliveryTo";
	public static final String ATTR_LOADING = "loadingStr";
	public static final String ATTR_PLANNED_DELIVERY = "plannedDelivery";

	// Either a OrderSearchEntity or OrderMonitoringEntity
	private Object entity;

	private String deliveryFrom;
	private String deliveryTo;
	private String plannedDelivery;
	private String loadingStr;

	public OrderSearchResultBean(Object entity) {
		if (!(entity instanceof OrderSearchEntity) && !(entity instanceof OrderMonitoringEntity)) {
			throw new LogonException(
					"Given entity [" + entity + "] must be of type: OrderSearchEntity or OrderMonitoringEntity",
					Reason.RES_ERROR_IMPL);
		}

		this.entity = entity;
	}

	/**
	 * Returns either the search or monitoring entity set on this bean. This is only used
	 * when serializing for the ajax response so we don't need to be specific about the return type.
	 */
	public Object getEntity() {
		return entity;
	}

	public String getDeliveryFrom() {
		return deliveryFrom;
	}

	public void setDeliveryFrom(String deliveryFrom) {
		this.deliveryFrom = deliveryFrom;
	}

	public String getDeliveryTo() {
		return deliveryTo;
	}

	public void setDeliveryTo(String deliveryTo) {
		this.deliveryTo = deliveryTo;
	}

	public String getPlannedDelivery() {
		return plannedDelivery;
	}

	public void setPlannedDelivery(String plannedDelivery) {
		this.plannedDelivery = plannedDelivery;
	}

	public String getLoadingStr() {
		return loadingStr;
	}

	public void setLoadingStr(String loadingStr) {
		this.loadingStr = loadingStr;
	}
}
