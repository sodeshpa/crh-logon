/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.web.utils.UserProvider;
import org.apache.commons.beanutils.BeanUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.PoolAssignmentService;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.beans.ManualPoolAssignmentBean;
import com.crh.logon.persistence.entities.views.ManPoolAssignmentListEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.DwrMessageBean;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="ManualPoolAssignmentAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.beans.ManualPoolAssignmentBean"
 * @adnbus.dwr.signature method="saveOrders" parameters="List<ManualPoolAssignmentBean> manualPoolAssignmentBeans, HttpSession session" imports="java.util.List, com.crh.logon.order.web.beans.ManualPoolAssignmentBean, javax.servlet.http.HttpSession"
 */
public class ManualPoolAssignmentAjaxService extends DwrServiceSupport {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ManualPoolAssignmentAjaxService.class);
    
    private static final String ATTR_POOL_ID = "poolId";
    
    public ListRange searchPoolAssignments (
			int skip, 
			int count, 
			String sort,
			Map<String, String> query,
			HttpServletRequest request,
			HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"count", count + ""},
							{"sort", sort},
							{"query", query},
							{"request", request},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchPoolAssignments", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.POOL_ASSIGNMENT);
			
			PoolAssignmentService poolAssignmentService = getServiceLocator().getPoolAssignmentService();
			String sortBy = ManPoolAssignmentListEntity.ATTR_SOLDTO_NAME1;
			boolean sortAsc = true;
			if (!StringUtils.empty(sort)) {
				String[] sortArr = sort.split(" ");
				sortBy = sortArr[0];
				sortAsc = "ASC".equals(sortArr[1]);
			}
			query.put(ManPoolAssignmentListEntity.ATTR_LANG_CD, request.getLocale().getLanguage());
			ListRange listRange = poolAssignmentService.searchManPoolAssignments(skip, count, sortBy, sortAsc, query);
			
			if ( (listRange != null) && (listRange.getData() != null) ) {
				Map<Long, TimeZone> shiptoTZCache = new HashMap<Long, TimeZone>();

				Object[] entityData = listRange.getData();
				Object[] beanData = new Object[entityData.length];
				LOG.debug("copying " + entityData.length + " elements");
				for (int i=0;i<entityData.length;i++) {
					ManPoolAssignmentListEntity entity = (ManPoolAssignmentListEntity)entityData[i];
					ManualPoolAssignmentBean bean = new ManualPoolAssignmentBean();
								
					try {
						BeanUtils.copyProperties(bean, entity);
					}
					catch (Exception e) {
						throw new LogonException(e);
					}

					long orderStatus = entity.getOrderStatusCd().longValue();
					if (orderStatus == Constant.ORDER_STATUS_NEW) {
						// No checkbox checked -> state "new"
						bean.setAutoPoolAssignmentFlag(false);
						bean.setAutoCarrierAssignmentFlag(false);
					}
					else if ( (orderStatus == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL) || (orderStatus == Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK) ) {
						// Pool checked -> state "transmitted to pool"
						bean.setAutoPoolAssignmentFlag(true);
					}
					else {
						bean.setAutoPoolAssignmentFlag(true);
						bean.setAutoCarrierAssignmentFlag(true);
					}
					if (bean.getPoolId() == null) {
						bean.setAutoPoolAssignmentFlag(false);
						bean.setAutoCarrierAssignmentFlag(false);
					}
					if (bean.getCommCarrierId() == null) {
						bean.setAutoCarrierAssignmentFlag(false);
					}

					// --- Pull in shipto timezones for date conversions
					{
						TimeZone shiptoTZ = shiptoTZCache.get(entity.getSapBpId());
						if (shiptoTZ == null) {
							ShiptoEntity shipto = getOrderDetailService().loadShipto(entity.getSapBpId());
							shiptoTZ = getTimeZoneService().loadForShipTo(shipto, getUserLocale());
							shiptoTZCache.put(entity.getSapBpId(), shiptoTZ);
						}
						bean.setDeliveryFrom(
								shiptoTZ.fmtAsDayMonthYearHourMinuteWithOffset(
										entity.getDeliveryFromDat(),
										UserProvider.getTimezone()));

						bean.setDeliveryTo(
								shiptoTZ.fmtAsDayMonthYearHourMinuteWithOffset(
										entity.getDeliveryToDat(),
										UserProvider.getTimezone()));
					}

					beanData[i] = bean;
				}
				LOG.debug("returning " + beanData.length + " elements");
				
				listRange.setData(beanData);
			}		
			
			return result = listRange;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
    
    public ListRange fetchAssignablePoolList(
    		int skip, 
			int count, 
			String query,
			HttpServletRequest request,
			HttpSession session) {
	   // [[
	   ListRange result = null;
	   Object[][] params = {{"skip", skip + ""},
						   {"count", count + ""},
						   {"query", query},
						   {"request", request},
						   {"session", session}};
	   boolean logDebugEnabled = LOG.isDebugEnabled();
	   if (logDebugEnabled) {
		   LOG.entering(this, "fetchAssignablePoolList", params);
	   }
	   // ]]
	   try {
		   Authorization.authorize(Authorization.POOL_ASSIGNMENT);
			
		   PoolAssignmentService poolAssignmentService = getServiceLocator().getPoolAssignmentService();
		   ListRange listRange = poolAssignmentService.fetchAssignablePoolList(skip, count, query, request.getLocale());
		   
		   return result = listRange;
	   }
	   finally {
	   // [[
		   if (logDebugEnabled) {
			   LOG.exiting(result);
		   }
	   // ]]
	   }
   }
    
    public ListRange fetchAssignableCarrierList(
    		int skip, 
			int count,
			String sort,
			Map<String, String> query,
			HttpServletRequest request,
			HttpSession session) {
	   // [[
	   ListRange result = null;
	   Object[][] params = {{"skip", skip + ""},
						   {"count", count + ""},
						   {"sort", sort},
						   {"query", query},
						   {"request", request},
						   {"session", session}};
	   boolean logDebugEnabled = LOG.isDebugEnabled();
	   if (logDebugEnabled) {
		   LOG.entering(this, "fetchAssignableCarrierList", params);
	   }
	   // ]]
	   try {
		   Authorization.authorize(Authorization.POOL_ASSIGNMENT);
		
		   Long poolId = null;
		   if (query.containsKey(ATTR_POOL_ID)) {
			   poolId = Long.parseLong((String)query.get(ATTR_POOL_ID));
		   }
		   PoolAssignmentService poolAssignmentService = getServiceLocator().getPoolAssignmentService();
		   ListRange listRange = poolAssignmentService.fetchAssignableCarrierList(skip, count, poolId);
		   
		   return result = listRange;
	   }
	   finally {
	   // [[
		   if (logDebugEnabled) {
			   LOG.exiting(result);
		   }
	   // ]]
	   }
   }
    
    
    public DwrMessageBean[] saveOrders(List<ManualPoolAssignmentBean> manualPoolAssignmentBeans,
			HttpSession session) {
		// [[
		DwrMessageBean[] result = null;
		Object[][] params = {{"manualPoolAssignmentBeans", manualPoolAssignmentBeans},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveOrders", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.POOL_ASSIGNMENT);
			
			// OrderService orderService = getServiceLocator().getOrderService();
			PoolAssignmentService poolAssignmentService = getServiceLocator().getPoolAssignmentService();
			
			// OrderStatusBsEntity toPool = getServiceLocator().getRefDataGenericsService().findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_TRANSMITTED_TO_POOL);
			// OrderStatusBsEntity toCarrier = getServiceLocator().getRefDataGenericsService().findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER);
			if (manualPoolAssignmentBeans != null) {
				for (ManualPoolAssignmentBean bean : manualPoolAssignmentBeans) {
					// OrderEntity orderEntity = orderService.findOrderById(bean.getOrderId());
			
					if ( (bean.getAutoPoolAssignmentFlag()) && (!bean.getAutoCarrierAssignmentFlag()) ) {
						// Pool checked -> state "transmitted to pool"
						poolAssignmentService.setOrderPool(bean.getOrderNumber(), bean.getPoolId(), null);
					}
					else if ( (bean.getAutoPoolAssignmentFlag()) && (bean.getAutoCarrierAssignmentFlag()) ) {
						// Both checked -> state "transmitted to carrier"
						poolAssignmentService.setOrderPool(bean.getOrderNumber(), bean.getPoolId(), null);
						poolAssignmentService.setOrderCarrier(bean.getOrderNumber(), bean.getCommCarrierId(), null);
					}
					else if (bean.getAutoCarrierAssignmentFlag()) {
						// Carrier checked -> not possible 
						LOG.error("Invalid status for " + bean.getOrderId() + ". Only autoCarrierAssignmentFlag is set.");
					}
				}
			}
			
			result = new DwrMessageBean[1];
			result[0] = new DwrMessageBean(DwrMessageBean.MsgTypes.SUCCESS,
					"", "", "Orders: " + manualPoolAssignmentBeans.size() + " processed.");
			
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
