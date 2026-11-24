/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.auftrag;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.OrderDetailService;
import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.auftrag.OrderDetailForm;
import com.crh.logon.order.web.forms.auftrag.OrderItemBean;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class ReturnReasonPopupBase extends OrderActionSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReturnReasonPopupBase.class);
	
	protected OrderItemBean getSelectedOrderItemBean(OrderDetailForm ordDetForm, Long selectedOrderItemId) {
		// [[
		OrderItemBean result = null;
		Object[][] params = {{"ordDetForm", ordDetForm},
							{"selectedOrderItemId", selectedOrderItemId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSelectedOrderItemBean", params);
		}
		// ]]
		try {
			for (OrderItemBean orderItemBean : ordDetForm.getMaterialBean().getOrderItemList()) {
				if (orderItemBean.getOrderItemId().equals(selectedOrderItemId)) {
					return result = orderItemBean;
				}
			}
			
			return result = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
		
	protected Map<Long, ShippingpointEntity> getShippingpointMap(String plantCd, OrderDetailService orderDetailService) {
		// [[
		Map<Long,ShippingpointEntity> result = null;
		Object[][] params = {{"plantCd", plantCd},
				{"orderDetailService", orderDetailService}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getShippingpointMap", params);
		}
		// ]]
		try {
			
			if (StringUtils.empty(plantCd)) {
				return result = Collections.EMPTY_MAP;
			}
			
			List<ShippingpointEntity> shpointList = orderDetailService.loadShippingPointList(plantCd);
			
			if (shpointList == null || shpointList.isEmpty()) {
				return result = Collections.EMPTY_MAP;
			}
			
			result = new HashMap<Long, ShippingpointEntity>();
			for (Iterator<ShippingpointEntity> iter = shpointList.iterator(); iter.hasNext();) {
				ShippingpointEntity entity = iter.next();
				result.put(entity.getShippingpointId(), entity);
			}
			
			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
}
