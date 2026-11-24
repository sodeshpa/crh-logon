/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * Purpose: only evaluates the body in case the operation is NOT(!) allowed for user/order,
 * The buttons are present on wagon detail and/or order detail.
 * @author AdNovum Informatik AG
 */
public class OrderDetailButtonLogicTag extends TagSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderDetailButtonLogicTag.class);
	private static final String C_NAME = 
		OrderDetailButtonLogicTag.class.getName();

	public static final int DELETE_ORDER = 0;
	public static final int REPORT_WAGON_EMPTY = 1;
	public static final int SET_HOLD_BACK_RE_ORDER_FLAG = 2;
	public static final int DEALLOCATE_ORDER = 3;
	public static final int DEALLOCATE_AND_DELETE_ORDER = 4;
	public int buttonFunc;
	public String orderObjName;

	private OrderEntity order;

	public int doStartTag() throws JspException {
		//WARNING!!! the tag is used also on wagon detail, where the order entity is not loaded, see WagonDetailCurrentOrderBean orderEntity expect that attributes might not be initialized
		//not nullable fields might be null unless set separately
		Object obj = pageContext.getAttribute(getOrderObjName());
		if (obj instanceof OrderEntity ) {
			LOG.debug("orderentity");
			OrderEntity orderEntity = (OrderEntity) obj;
			order = orderEntity;
			
		}
		else {
			LOG.error("Unsupported  type in OrderDetailButtonLogicTag");
			return EVAL_BODY_INCLUDE;
		}
				
		if ( order.getOrderStatus().getOrderStatusCd() == null || order.getDeliveryType().getDeliveryTypeCd() == null ) {
			return EVAL_BODY_INCLUDE;
		}
		
		final boolean isPickup = LogonUtil.isAnyTypeOfPickupOrder(order.getDeliveryType().getDeliveryTypeCd());
		final boolean isRail = LogonUtil.isRailOrder(order.getDeliveryType().getDeliveryTypeCd());
		final boolean isTruck = LogonUtil.isTruckOrder(order.getDeliveryType().getDeliveryTypeCd());
		final boolean isTruckOnHoldUnlockedOrder = (isTruck && order.getOnHoldOrderAndLockedFlag().booleanValue() == false);

		switch (buttonFunc) {
		case DELETE_ORDER:
		{
			if (disableDeleteOrderButton(isPickup, isTruck, isTruckOnHoldUnlockedOrder)) {
				return EVAL_BODY_INCLUDE;	
			}

			/*
			 * 								<c:when test="${((order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_DELIVERED || order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_FINISHED ) && ORDER_DETAIL_DELETE_SPECIAL_DISABLED) ||
									(order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_DELETED) ||
									(order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_TRANSMITTED_TO_RAIL) ||
									((	(		(!isPickup && order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd > ORDER_STATUS_DISPATCHED) ||
												(isPickup && order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd > ORDER_STATUS_TRANSMITTED_TO_TRUCK) 
										) 
									&& (order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd < ORDER_STATUS_DELIVERED)) && ORDER_DETAIL_DELETE_SPECIAL_DISABLED)}" >
									class="ViewButtonStyleDisabled"
									onClick="return false;"
								</c:when>
			 */

			return SKIP_BODY;
		}
		case DEALLOCATE_ORDER: {
			if (disableDeallocatButton(isRail)) {
				return EVAL_BODY_INCLUDE;
			}
			return SKIP_BODY;	
		}
		case DEALLOCATE_AND_DELETE_ORDER: {
			if (disableDeallocatButton(isRail) && disableDeleteOrderButton(isPickup, isTruck, isTruckOnHoldUnlockedOrder)) {
				return EVAL_BODY_INCLUDE;
			}
			return SKIP_BODY;	
		}
		case REPORT_WAGON_EMPTY:
		{
			long orderStatusCd = order.getOrderStatus().getOrderStatusCd().longValue();
			/*<c:set var="isWagonEmptyReportDisabled">true</c:set>
	<crh-common:authorize function="<%= Authorization.RAILCARS_FREIMELDEN %>">
	<c:if test="${(order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_SHIPPING) ||
	            (order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT) ||
	            (order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_TRANSMITTED_TO_RAIL) ||
	            (order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_UNLOADING)}">
	    <c:set var="isWagonEmptyReportDisabled">false</c:set>
	</c:if>
	</crh-common:authorize>*/
			boolean orderDetailWagonEmptyReportDisabled = true;
			if ( ( Authorization.isAllowed(Authorization.RAILCARS_FREIMELDEN) ) && 
					(orderStatusCd == Constant.ORDER_STATUS_SHIPPING
							|| orderStatusCd == Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT ||
							orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL ||
							orderStatusCd == Constant.ORDER_STATUS_UNLOADING)) {
				orderDetailWagonEmptyReportDisabled = false;
			}
			if ( orderDetailWagonEmptyReportDisabled ) {				
				return EVAL_BODY_INCLUDE;
			}
			return SKIP_BODY;
		}
		case SET_HOLD_BACK_RE_ORDER_FLAG:
		{
			long orderStatusCd = order.getOrderStatus().getOrderStatusCd().longValue();
			boolean orderDetailWagonEmptyReportDisabled = true;
			if ( ( 	LogonUtil.isRailOrder(order.getDeliveryType().getDeliveryTypeCd()) && Authorization.isAllowed(Authorization.ORDER_DETAIL_W) ) && 
					(orderStatusCd < Constant.ORDER_STATUS_DELIVERED || orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL)) {
				orderDetailWagonEmptyReportDisabled = false;
			}
			if ( orderDetailWagonEmptyReportDisabled ) {				
				return EVAL_BODY_INCLUDE;
			}
			return SKIP_BODY;
		}		
		default:
			return EVAL_BODY_INCLUDE;
		}
	}


	public void setButtonFunc(int buttonFunc) {
		this.buttonFunc = buttonFunc;		
	}


	public String getOrderObjName() {
		return orderObjName;
	}


	public void setOrderObjName(String orderObjName) {
		this.orderObjName = orderObjName;
	}
	
	private boolean disableDeleteOrderButton(boolean isPickup, boolean isTruck, boolean isTruckOnHoldUnlockedOrder) {
		boolean orderDetailDeleteSpecialDisabled = true;
		long orderStatusCd = order.getOrderStatus().getOrderStatusCd().longValue();
		/*
		 * 		<c:set var="ORDER_DETAIL_DELETE_SPECIAL_DISABLED">true</c:set>
				<c:if test="${(order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_FINISHED || order_auftrag_OrderDetailForm.order.orderStatus.orderStatusCd == ORDER_STATUS_DELIVERED)}" >
				<crh-common:authorize function="<%= Authorization.ORDER_DETAIL_DELETE_SPECIAL%>">
					<c:set var="ORDER_DETAIL_DELETE_SPECIAL_DISABLED">false</c:set>
				</crh-common:authorize>
				</c:if>
		 */
		if (Authorization.isAllowed(Authorization.ORDER_DETAIL_W) && Authorization.isAllowed(Authorization.ORDER_DETAIL_DELETE_SPECIAL)) {
			orderDetailDeleteSpecialDisabled = false;
		}

		boolean result = false;	
		result |= (orderStatusCd == Constant.ORDER_STATUS_DISPATCHED && orderDetailDeleteSpecialDisabled && !isPickup && (!isTruck || isTruckOnHoldUnlockedOrder ));
		result |= (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER && !isPickup);
		result |= (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK && !isPickup);
		result |= (orderStatusCd == Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT && !isPickup);
		result |= (orderStatusCd == Constant.ORDER_STATUS_LOADING);
		result |= (orderStatusCd == Constant.ORDER_STATUS_LOADED);
		result |= (orderStatusCd == Constant.ORDER_STATUS_SHIPPING);
		result |= (orderStatusCd == Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT);
		result |= (orderStatusCd == Constant.ORDER_STATUS_UNLOADING);
		result |= (orderStatusCd == Constant.ORDER_STATUS_DELIVERED && orderDetailDeleteSpecialDisabled);
		result |= (orderStatusCd == Constant.ORDER_STATUS_FINISHED && orderDetailDeleteSpecialDisabled);
		result |= (orderStatusCd == Constant.ORDER_STATUS_DELETED);
		result |= (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL);

		return result;
	}
	
	private boolean disableDeallocatButton(boolean isRail) {
		long orderStatusCd = order.getOrderStatus().getOrderStatusCd().longValue();
		
		boolean allowedOrderStat = false;
		allowedOrderStat |= (orderStatusCd == Constant.ORDER_STATUS_SHIPPING);
		allowedOrderStat |= (orderStatusCd == Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT);
		allowedOrderStat |= (orderStatusCd == Constant.ORDER_STATUS_UNLOADING);
		allowedOrderStat |= (orderStatusCd == Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL);
		
		boolean result = true;
		result &= Authorization.isAllowed(Authorization.RAIL_DISPATCHING_SW);
		result &= isRail;
		result &= allowedOrderStat;
		
		return !result;
	}
}
