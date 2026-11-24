/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.crh.logon.beans.ShippingOrderFetchListBean;

/**
 * Simple bean for holding the return objects for <code>OrderChangeService.loadShippingOrders()</code>.
 *  
 * @author AdNovum Informatik AG
 */
public class LoadShippingOrdersBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final ShippingOrderFetchListBean shippingOrderFetchListBean;
	private final List<ShippingOrderLoadBean> shipppingOrderLoadBeans;
	private final Map<Long, Date> plannedLoadByOrderId;
	private final Map<Long, Date> truckNextOrderMapping;

	public LoadShippingOrdersBean(ShippingOrderFetchListBean shippingOrderFetchListBean,
			List<ShippingOrderLoadBean> shipppingOrderLoadBeans,
			Map<Long, Date> plannedLoadByOrderId, 
			Map<Long, Date> truckNextOrderMapping) {
		
		this.shippingOrderFetchListBean = shippingOrderFetchListBean;
		this.shipppingOrderLoadBeans = shipppingOrderLoadBeans;
		this.plannedLoadByOrderId = plannedLoadByOrderId;
		this.truckNextOrderMapping = truckNextOrderMapping;
	}

	public ShippingOrderFetchListBean getShippingOrderFetchListBean() {
		return shippingOrderFetchListBean;
	}

	public List<ShippingOrderLoadBean> getShipppingOrderLoadBeans() {
		return shipppingOrderLoadBeans;
	}

	public Map<Long, Date> getPlannedLoadByOrderId() {
		return plannedLoadByOrderId;
	}

	public Map<Long, Date> getTruckNextOrderMapping() {
		return truckNextOrderMapping;
	}
}
