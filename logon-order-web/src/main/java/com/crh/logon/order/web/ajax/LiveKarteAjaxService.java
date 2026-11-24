/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.itrack.dto.PositionDTO;
import com.crh.logon.business.services.beans.VehicleCoordinatesBean;
import com.crh.logon.business.services.interfaces.LiveKarteService;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.order.web.beans.OrderFiltersBean;
import com.crh.logon.order.web.forms.auftrag.BizeventMapDetailBean;
import com.crh.logon.order.web.forms.auftrag.LiveKarteForm;
import com.crh.logon.persistence.entities.views.BizeventMapEntity;
import com.crh.logon.persistence.entities.views.OrderFiltersEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.struts.util.RequestUtils;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="LiveKarteAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.persistence.entities.views.OrderFiltersEntity"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.persistence.entities.views.BizeventMapEntity"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.beans.VehicleCoordinatesBean"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.beans.OrderFiltersBean"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.forms.auftrag.BizeventMapDetailBean"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.itrack.dto.PositionDTO"
 */
public class LiveKarteAjaxService extends DwrServiceSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LiveKarteAjaxService.class);

	/**
	 * @adnlog.exclude return
	 */
	public Map<Long, OrderFiltersBean> fetchStaticMapObjects(HttpServletRequest request) {
		// [[
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchStaticMapObjects", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.LIVE_MAP);
			final Map<Long, OrderFiltersBean> orderFilterMap = new HashMap<Long, OrderFiltersBean>();
			
			LiveKarteForm livekarteForm = (LiveKarteForm) request.getSession().getAttribute(
					LiveKarteForm.SESSION_LIVEKARTEFORM_NAME);

			Locale locale = RequestUtils.getUserLocale(request, null);

			@SuppressWarnings("unchecked")
			final List<OrderFiltersEntity> filtOrders = livekarteForm.getFiltOrderList();
			final List<OrderFiltersBean> filtBeans = convertToBeans(filtOrders, locale);

			// get order ids and create a map of orders
			for (OrderFiltersBean orderBean : filtBeans) {
				orderFilterMap.put(orderBean.getOrderId(), orderBean);
			}
			
			return orderFilterMap;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public List<VehicleCoordinatesBean> fetchAllDynamicMapObjects(int first, int pagesize, HttpServletRequest request) {
		// [[
		List<VehicleCoordinatesBean> result = null;
		Object[][] params = {{"first", first + ""},
							{"pagesize", pagesize + ""},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAllDynamicMapObjects", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.LIVE_MAP);
			
			LiveKarteForm livekarteForm = (LiveKarteForm) request.getSession().getAttribute(
					LiveKarteForm.SESSION_LIVEKARTEFORM_NAME);
			
			@SuppressWarnings("unchecked")
			final List<OrderFiltersEntity> filtOrders = livekarteForm.getFiltOrderList();
			
			List<VehicleCoordinatesBean> dynamicObjectList;
			//only when orders are selected
			if (filtOrders.size() > 0) {
				//get dynamic object for a selected list of orders
				LiveKarteService lkService = getServiceLocator().getLiveKarteService();
				dynamicObjectList = lkService.loadVehicleCoordinates(first, pagesize, filtOrders);
			}
			else {
				dynamicObjectList = new ArrayList<VehicleCoordinatesBean>();
			}
			
			return result = dynamicObjectList;
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
	 * Loads BizeventMapEntity by its id from vehicleCoordinatesBean.
	 * @adnlog.exclude return
	 */
	public BizeventMapEntity getBizevent(VehicleCoordinatesBean vehicle, HttpServletRequest request) {
		// [[
		BizeventMapEntity result = null;
		Object[][] params = {{"vehicle", vehicle},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getBizevent", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.LIVE_MAP);
			Locale locale = RequestUtils.getUserLocale(request, null);
			
			//get dynamic object for a given ordr id
			LiveKarteService lkService = getServiceLocator().getLiveKarteService();
			BizeventMapEntity bizeventMapEntity = lkService.loadBizevent(locale.getLanguage(), vehicle);
			
			return result = bizeventMapEntity;
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
	 * Loads BizeventMapEntitys by orderId from vehicleCoordinatesBean.
	 * @adnlog.exclude return
	 */
	public List<BizeventMapEntity> getBizeventsForOrder(int first, int pagesize, VehicleCoordinatesBean vehicle, HttpServletRequest request) {
		// [[
		List<BizeventMapEntity> result = null;
		Object[][] params = {{"vehicle", vehicle},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getBizeventsForOrder", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.LIVE_MAP);
			
			final List<Long> orderIds = new ArrayList<Long>();
			orderIds.add(vehicle.getOrderId());
			
			List<BizeventMapEntity> dynamicObjectList;
			//only when orders are selected
			if (orderIds.size() > 0) {
				Locale locale = RequestUtils.getUserLocale(request, null);
				//get dynamic object for a selected list of orders
				LiveKarteService lkService = getServiceLocator().getLiveKarteService();
				dynamicObjectList = lkService.loadBizevents( locale.getLanguage(), first, pagesize, orderIds);
			}
			else {
				dynamicObjectList = new ArrayList<BizeventMapEntity>();
			}
			
			return result = dynamicObjectList;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List<?> fetchAllMapObjects(String laodAllObjectsFlag, HttpServletRequest request) {
		// [[
		List<Object> result = null;
		Object[][] params = {{"laodAllObjectsFlag", laodAllObjectsFlag},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAllMapObjects", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.LIVE_MAP);
			
			LiveKarteForm livekarteForm = (LiveKarteForm) request.getSession().getAttribute(
					LiveKarteForm.SESSION_LIVEKARTEFORM_NAME);

			Locale locale = RequestUtils.getUserLocale(request, null);

			@SuppressWarnings("unchecked")
			final List<OrderFiltersEntity> filtOrders = livekarteForm.getFiltOrderList();
			final List<OrderFiltersBean> filtBeans = convertToBeans(filtOrders, locale);

			final List<Long> orderIds = new ArrayList<Long>();
			final Map<Long, OrderFiltersBean> orderFilterMap = new HashMap<Long, OrderFiltersBean>();
			
			//get order ids and create a map of orders 
			for (OrderFiltersBean orderBean : filtBeans) {
				orderIds.add(orderBean.getOrderId());
				orderFilterMap.put(orderBean.getOrderId(), orderBean);
			}			
			
			List<BizeventMapEntity> dynamicObjectList;
			//only when orders are selected
			if (orderIds.size() > 0) {
				//get dynamic object for a selected list of orders
				LiveKarteService lkService = getServiceLocator().getLiveKarteService();
				dynamicObjectList = lkService.loadBizevents(locale.getLanguage(), 0, 10000, orderIds);
			}
			else {
				dynamicObjectList = new ArrayList<BizeventMapEntity>();
			}
			
			// results to return
			final List<Object> dataList = new ArrayList<Object>();
			//map of filtered orders
			dataList.add(orderFilterMap);
			//list of biz events of trucks and trailers 
			dataList.add(dynamicObjectList);
			
			return result = dataList;
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
	 * Method is used in Order detail screen to get dynamic objects
	 * @adnlog.exclude return
	 */
	public List<BizeventMapEntity> fetchDynamicMapObjects(Long orderId, HttpServletRequest request) {
		Authorization.authorize(Authorization.LIVE_MAP);
	
		List<Long> orderIds = new ArrayList<Long>();
		orderIds.add(orderId);
		Locale locale = RequestUtils.getUserLocale(request, null);
		
		//get dynamic object for a given ordr id
		LiveKarteService lkService = getServiceLocator().getLiveKarteService();
		List<BizeventMapEntity> dynamicObjectList = lkService.loadBizevents(locale.getLanguage(), 0, 1000, orderIds);
		
		return dynamicObjectList;
		
	}

	private List<OrderFiltersBean> convertToBeans(
			List<OrderFiltersEntity> entities,
			Locale locale) {

		BeanUtilsBean beanUtils = getBeanUtilsBean();

		TimeZone defaultTz = getTimeZoneService().createDefault(locale);

		List<OrderFiltersBean> beans = new ArrayList<OrderFiltersBean>();
		for (OrderFiltersEntity entity: entities) {
			OrderFiltersBean bean = convertToBean(entity,
												  defaultTz,
												  locale,
												  beanUtils);
			beans.add(bean);
		}

		return beans;
	}

	private OrderFiltersBean convertToBean(OrderFiltersEntity entity,
										   TimeZone defaultTz,
										   Locale locale,
										   BeanUtilsBean beanUtils) {
		OrderFiltersBean bean = new OrderFiltersBean();
		try {
			beanUtils.copyProperties(bean, entity);
		}
		catch (Exception exc) {
			throw new LogonException(exc);
		}

		TimeZone shipToTz = defaultTz;
		if (StringUtils.notEmpty(entity.getShiptoTimezoneCd())) {
			shipToTz = new TimeZone(entity.getShiptoTimezoneCd(), locale);
		}

		bean.setDeliveryTo(shipToTz.fmtAsDayMonthYearHourMinuteWithOffset(
				entity.getDeliveryToDat(),
				getUserTimeZone()));

		return bean;
	}
	

    @AdnTrace
	public BizeventMapDetailBean getBizeventDetail(Long bizeventId, HttpServletRequest request){
		Authorization.authorize(Authorization.LIVE_MAP);
		TimeZone userTimeZone = getUserTimeZone();
		Locale locale = RequestUtils.getUserLocale(request, null);
		BizeventMapDetailBean bean = new BizeventMapDetailBean();

		LiveKarteService lkService = getServiceLocator().getLiveKarteService();
		BizeventMapEntity mapEntity = lkService.loadBizeventMapEntity(bizeventId, locale.getLanguage());
		String eventTimeInUserTimeZone = userTimeZone.fmtAsDayMonthYearHourMinute(mapEntity.getEventTs());
		bean.setBizeventMapEntity(mapEntity);
		bean.setEventTimeInUserTimeZone(eventTimeInUserTimeZone);
		return bean;
	}

	@AdnTrace
	public Long calculateArrivalDate(Long orderId, String time, Double lat, Double lon) {
		Authorization.authorize(Authorization.LIVE_MAP);

		if (lat == null || lon == null) {
			throw new IllegalArgumentException("lat or lon is null");
		}

		PositionDTO position = new PositionDTO();
		position.setLatitude(lat);
		position.setLongitude(lon);
		position.setTime(time != null ? OffsetDateTime.parse(time) : null);

		LiveKarteService lkService = getServiceLocator().getLiveKarteService();
		OffsetDateTime offsetDateTime = lkService.calculateArrivalDate(orderId, position);
		if (offsetDateTime == null) {
			return null;
		}
		// epoch seconds and ExtJS will format it correctly
		return offsetDateTime.toEpochSecond();
	}

}
