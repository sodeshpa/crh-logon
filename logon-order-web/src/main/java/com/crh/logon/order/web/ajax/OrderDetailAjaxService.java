/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.crh.logon.order.web.forms.auftrag.OrderDetailForm;
import org.apache.struts.util.RequestUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.BizeventAuditService;
import com.crh.logon.business.services.interfaces.OrderDetailService;
import com.crh.logon.business.services.utils.BizeventAuditWrapper;
import com.crh.logon.business.services.utils.BindingsDTO;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.OrderDetailsUtil;
import com.crh.logon.business.services.utils.OrderLogEntryWrapper;
import com.crh.logon.business.services.utils.SortOrder;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.views.BizeventAuditDisplayEntity;
import com.crh.logon.persistence.entities.views.BizeventOrderLogEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="OrderDetailAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.ordercreation.dto.SapVendorDTO"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.utils.BindingsDTO"
 */
public class OrderDetailAjaxService extends DwrServiceSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderDetailAjaxService.class);

	/**
	 * Called when RFID popup is opened to fill RFID drop down 
	 */
	public ListRange fetchBizeventList(int start, int count, String orderBy, String query,
			HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = { { "start", start + "" }, { "count", count + "" },
				{ "orderBy", orderBy }, { "query", query }, { "request", request } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchBizeventList", params);
		}
		// ]]
		try {

			Authorization.authorize(Authorization.ORDER_DETAIL_ORDER_LOG);
			ListRange listRange = new ListRange();

			OrderDetailForm orderDetailForm = (OrderDetailForm) request.getSession().getAttribute(
					OrderDetailForm.SESSION_RFIDFORM_NAME);
			if ((orderDetailForm != null) && (orderDetailForm.getOrder() != null)) {
				Long orderId = orderDetailForm.getOrder().getOrderId();
				Locale locale = RequestUtils.getUserLocale(request, null);

				final OrderDetailService orderDetailService = getServiceLocator().getOrderDetailService();

				List<SortOrder> sortOrders = null;
				if (org.apache.commons.lang.StringUtils.isNotBlank(orderBy)) {
					SortOrder sortOrder = SortOrder.parse(orderBy);
					sortOrders = Collections.singletonList(sortOrder);
				}

				List<BizeventOrderLogEntity> bizeventList = orderDetailService.loadBizevents(start,
						count, sortOrders, orderId, locale.getLanguage());

				final List<OrderLogEntryWrapper> wrappedBizeventList = wrapBizeventList(
						bizeventList, orderDetailForm.getOrder(), request);

				listRange.setData(wrappedBizeventList.toArray());
				listRange.setTotalSize(orderDetailService.countBizevents(orderId,
						locale.getLanguage()));
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

	/**
	 * Wraps {@link BizeventLogEntity} entities into {@link OrderLogEntryWrapper} entities. Doing this because on the
	 * UI description field does not always show the description, but for different kind of bizevents different 
	 * kind of information is shown. Certain information needs language resolution according to the ids stored in audit table.
	 *  
	 * Updates the description field of bizevent type edit fields.
	 * @param bizeventList
	 * @param order 
	 * @param request
	 * @return
	 */
	private List<OrderLogEntryWrapper> wrapBizeventList(List<BizeventOrderLogEntity> bizeventList,
			OrderEntity order, HttpServletRequest request) {
		// [[
		Object[][] params = { { "bizeventList", bizeventList } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadBizevents", params);
		}
		// ]]
		try {
			if (bizeventList.isEmpty()) {
				return new ArrayList<OrderLogEntryWrapper>();
			}

			final List<Long> bizeventIds = new ArrayList<Long>();
			for (BizeventOrderLogEntity event : bizeventList) {
				bizeventIds.add(event.getBizeventId());
			}

			BizeventAuditService bizeventAuditService = getServiceLocator().getBizeventAuditService();
			Locale locale = request.getLocale();
			String language = locale.getLanguage();
			final List<BizeventAuditDisplayEntity> audits = bizeventAuditService.loadBizeventAudits(
					language, bizeventIds);

			//fill out language dependent information for rows where language resolution is needed
			final List<BizeventAuditWrapper> wrappedBizeventAudits = bizeventAuditService.wrapAuditViewEntities(
					audits, locale, getUserTimeZone());

			final Map<Long, List<BizeventAuditWrapper>> auditWrapperListMap = new HashMap<Long, List<BizeventAuditWrapper>>();
			for (BizeventAuditWrapper wrapper : wrappedBizeventAudits) {
				Long bizeventId = wrapper.getEntity().getBizeventId();
				List<BizeventAuditWrapper> list = auditWrapperListMap.get(bizeventId);
				if (list == null) {
					list = new ArrayList<BizeventAuditWrapper>();
					auditWrapperListMap.put(bizeventId, list);
				}
				list.add(wrapper);
			}

			final List<OrderLogEntryWrapper> logWrapperList = new ArrayList<OrderLogEntryWrapper>();

			//fill out the additional info field using the audit values and also using other attributes of the bizevent
			for (BizeventOrderLogEntity log : bizeventList) {
				OrderLogEntryWrapper logWrapper = new OrderLogEntryWrapper(log);
				Long bizeventTypeCd = log.getBizeventTypeCd();
				Long bizeventId = log.getBizeventId();
				String description = log.getDescription();
				if (auditWrapperListMap.containsKey(bizeventId)) {

					//for edit bizevent show the field change, and the changed field's description
					List<BizeventAuditWrapper> wrappers = auditWrapperListMap.get(bizeventId);
					for (BizeventAuditWrapper wrapper : wrappers) {
						BizeventAuditDisplayEntity bizeventAuditDisplayEntity = wrapper.getEntity();
						String newValueDisplay = wrapper.getNewValueDisplay();
						String oldValueDisplay = wrapper.getOldValueDisplay();
						if (bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_EDIT_FIELD)) {
							if ((newValueDisplay != null && oldValueDisplay != null)
									&& !newValueDisplay.equals(oldValueDisplay)
									|| (newValueDisplay == null && oldValueDisplay != null)
									|| (newValueDisplay != null && oldValueDisplay == null)) {
								String txt = bizeventAuditDisplayEntity.getTxt();
								String additionalInfo = logWrapper.getAdditionalInfo();
								logWrapper.setAdditionalInfo(additionalInfo != null ? (additionalInfo.concat("<br>"
										+ txt)) : txt);
							}
						}
						else {
							Long auditBizeventConfigCd = bizeventAuditDisplayEntity.getAuditBizeventConfigCd();
							if ((bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER) || bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_COMM_CARRIER_ACK))
									&& auditBizeventConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID)) {
								logWrapper.setAdditionalInfo(newValueDisplay);
							}
							else if ((bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_POOL)
									|| bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_POOL_ACK) || bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_SET_POOL))
									&& auditBizeventConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_POOL_ID)) {
								logWrapper.setAdditionalInfo(newValueDisplay);
							}
							else if (bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_SET_EXEC_CARRIER)
									&& auditBizeventConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_EXEC_CARRIER_ID)) {
								logWrapper.setAdditionalInfo(newValueDisplay);
							}

							else if (bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_SET_COMM_CARRIER)
									&& auditBizeventConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID)) {
								logWrapper.setAdditionalInfo(newValueDisplay);
							}
							else if (bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_COMM_CARRIER_REFUSED)
									&& auditBizeventConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID)) {
								logWrapper.setAdditionalInfo(oldValueDisplay);
								logWrapper.setDescription(description);
							}
							else if (bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_EXEC_CARRIER_REFUSED)
									&& auditBizeventConfigCd.equals(Constant.AUDIT_BIZEVENT_CONFIG_EXEC_CARRIER_ID)) {
								logWrapper.setAdditionalInfo(oldValueDisplay);
								logWrapper.setDescription(description);
							}
						}
					}
				}
				if (bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK)
						|| bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_UPDATED_SCHEDULING)) {
					String message = Message.lookup(locale, "orderdetail.log.prefix.eta");
					String additionalInfo = log.getTruckPlateNo();
					String eta = message + ": " + description;
					additionalInfo = StringUtils.appendWithNewLine(additionalInfo, eta);
					logWrapper.setAdditionalInfo(additionalInfo);
				}
				else if (bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_SCAN_WAGON)) {
					logWrapper.setAdditionalInfo(log.getWagonNo());
				}
				else if (bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_LOADING)) {
					String message = Message.lookup(locale, "orderdetail.log.prefix.tare");
					logWrapper.setAdditionalInfo(message + ": " + description);
				}
				else if (bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_LOADED)) {
					String message = Message.lookup(locale, "orderdetail.log.prefix.gross");
					logWrapper.setAdditionalInfo(message + ": " + description);
				}
				else if (bizeventTypeCd.equals(Constant.BIZEVENT_TYPE_SCAN_CONTAINER)) {
					String containerNo = log.getContainerNo();
					logWrapper.setAdditionalInfo(containerNo);
				}
				else {
					logWrapper.setDescription(description);
				}

				String initiator = determineInitiator(log, order);
				logWrapper.setInitiator(initiator);

				logWrapper.setEventTs(getUserTimeZone().fmtAsDayMonthYearHourMinute(
						log.getEventTs()));

				logWrapperList.add(logWrapper);
			}

			return logWrapperList;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}
	
	public ListRange fetchSapVendorList(int start, int count, String orderBy, String query,
			HttpServletRequest request, HttpSession session) {
		// [[
		ListRange result = null;
		Object[][] params = { 
				{ "start", start }, 
				{ "count", count }, 
				{ "orderBy", orderBy },
				{ "query", query }, 
				{ "request", request }, 
				{ "session", session } };

		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveVendorPf", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER_DETAIL_W);
			OrderDetailService orderDetailService = getServiceLocator().getOrderDetailService();

			result = orderDetailService.fetchSapVendorList(start, count, query, request.getLocale());

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

	
	/**
	 * Saves the PF-SAP Vendor assignment from the popup
	 * @param pfCode the PF code
	 * @param vendorId the SAP Vendor ID
	 */
	public void saveVendorPf(String pfCode, Long vendorId) {
		// [[

		Object[][] params = { { "pfCode", pfCode }, { "vendorId", vendorId } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveVendorPf", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER_DETAIL_W);
			getServiceLocator().getOrderDetailService().saveVendorPf(pfCode, vendorId);
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
	 * Determines the initiator field based on logEntity and orderEntity.
	 * 
	 * @param logEntity
	 * @param logWrapper 
	 * @return initiator
	 */
	private String determineInitiator(BizeventOrderLogEntity logEntity, OrderEntity orderEntity) {
		// [[
		String result = null;
		Object[][] params = { { "logEntity", logEntity }, { "orderEntity", orderEntity } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "determineInitiator", params);
		}
		// ]]
		try {
			String initiator = logEntity.getInitiator();
			Long originalMopEventId = logEntity.getOriginalMopeventId();

			if (orderEntity == null || !initiator.equals("system system")
					|| originalMopEventId != null) {
				return result = initiator;
			}

			Boolean noPdaOrRailEventsFlag = orderEntity.getNoPdaOrRailEventsFlag();

			if (noPdaOrRailEventsFlag) {
				return result = initiator;
			}
			if (orderEntity.getITrackAvailableFlag()) {
				return initiator;
			}

			DeliveryTypeBsEntity deliveryTypeBsEntity = orderEntity.getDeliveryType();
			Long deliveryTypeCd = deliveryTypeBsEntity.getDeliveryTypeCd();

			//If this is a truck order, display Initiator as "PDA or Weighing System" (base on the types in tlgnr_delivery_type_tx )
			if (deliveryTypeCd == Constant.DELIVERY_TYPE_ROAD_DIRECT
					|| deliveryTypeCd == Constant.DELIVERY_TYPE_BIMODAL_ROAD
					|| deliveryTypeCd == Constant.DELIVERY_TYPE_PICKUP) {
				return result = "PDA or Weighing System";
			}
			//If this is a rail order, display Initiator as "Rail event" (base on the types in tlgnr_delivery_type_tx )
			else if (deliveryTypeCd == Constant.DELIVERY_TYPE_RAIL_DIRECT
					|| deliveryTypeCd == Constant.DELIVERY_TYPE_BIMODAL_RAIL
					|| deliveryTypeCd == Constant.DELIVERY_TYPE_BLOCK_TRAIN
					|| deliveryTypeCd == Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP) {
				return result = "Rail Event";
			}
			else {
				return result = initiator;
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	/**
	 * Returns a list of damageCodeSearchEntities according to the query. Refdataservice not used because cannot be used 
	 * with paging. 
	 */
	public ListRange fetchDamageCodeList(int start, int count, String orderBy, String query,
			HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = { { "start", start + "" }, { "count", count + "" },
				{ "orderBy", orderBy }, { "query", query }, { "request", request } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchDamageCodeList", params);
		}
		// ]]
		try {

			Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);

			Locale locale = RequestUtils.getUserLocale(request, null);

			return result = getServiceLocator().getOrderDetailService().loadDamageCodes(start,
					count, query, locale);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public double checkBimodalLoadBalance(Long orderId, HttpServletRequest request) {
		// [[
		double result = 0.0;
		Object[][] params = { { "orderId", orderId }, { "request", request } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "checkBimodalLoadBalance", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAILCARS_FREIMELDEN);
			return result = getServiceLocator().getDispoService().checkBimodalLoadBalance(orderId);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	/**
	 * 
	 * @param plantCd
	 * @param request
	 * @param session
	 * @return
	 */
	public List<ShippingpointEntity> fetchShippingPointList(String plantCd,
			HttpServletRequest request, HttpSession session) {
		// [[
		List<ShippingpointEntity> result = new ArrayList<ShippingpointEntity>();

		Object[][] params = { { "plantCd", plantCd }, { "request", request },
				{ "session", session } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShippingPointList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER_DETAIL_R);

			return result = getOrderDetailService().loadShippingPointList(plantCd);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	/**
	 * Updates the list of bindings.
	 * @param tpSapVendorId the id of the TpSapVendorEntity to insert or remove from the list
	 * @param itemWasAdded if true, an item with the provided id was added to the list. If false, an item was removed from the list
	 * @param session the {@link HttpSession}
	 * @return the position where the new item was inserted, or -1 when an item was deleted.
	 */
	public int updateBindingsList(Long tpSapVendorId, Boolean itemWasAdded, HttpSession session) {
		// [[

		Object[][] params = {

		{ "tpSapVendorId", tpSapVendorId }, 
		{ "itemWasAdded", itemWasAdded },
		{ "session", session } 
		
		};
		
		int position = -1;
		
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateBindingsList", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.ORDER_DETAIL_W);

			OrderDetailForm orderDetailForm = (OrderDetailForm) session.getAttribute(OrderDetailForm.SESSION_RFIDFORM_NAME);
			if ((orderDetailForm != null) && (orderDetailForm.getOrder() != null)) {
				
				List<BindingsDTO> existingBindings = orderDetailForm.getExistingBindings();
				
				List<BindingsDTO> modifiedBindings = getOrderDetailService().updateExistingBindings(
						tpSapVendorId, itemWasAdded, existingBindings);

				if (itemWasAdded) {
					// find the position of the newly added item
					position = OrderDetailsUtil.findItemById(tpSapVendorId, modifiedBindings);
				}
				
				orderDetailForm.setExistingBindings(modifiedBindings);
				
				
			}
			// return the position where the new item should be inserted (or -1 if item was deleted)
		    return position;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(position);
			}
			// ]]
		}

	}

	/**
	 * Returns the list of available bindings
	 * @param start start interval
	 * @param count number of items
	 * @param searchFilter search filter
	 * @return ListRange containing List of {@link BindingsDTO}s
	 */
	public ListRange fetchAvailableBindings(int start, int count, String searchFilter) {
		// [[
		ListRange result = new ListRange();

		Object[][] params = { { "searchFilter", searchFilter }, { "start", start },
				{ "count", count } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getExistingBindings", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER_DETAIL_R);

			result = getOrderDetailService().fetchAvailableBindings(start, count, searchFilter);

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