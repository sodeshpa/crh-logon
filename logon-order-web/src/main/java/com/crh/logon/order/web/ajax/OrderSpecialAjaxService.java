/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.order.web.forms.auftrag.OrderSpecialForm;
import org.apache.struts.util.RequestUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.OrderSpecialService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.RailTimetableEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.RailPlantEntity;
import com.crh.logon.persistence.entities.views.ShiptoSareaDetailEntity;
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
 * @adnbus.service name="OrderSpecialAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 *
 */
public class OrderSpecialAjaxService extends DwrServiceSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderSpecialAjaxService.class);

	/**
	 * Used by Order Special 
	 */
	public ListRange getPlantList(int start, int count, String orderBy, String query,
			HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPlantList", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
			
			final List<RailPlantEntity> plantList = getServiceLocator().getOrderSpecialService().fetchPlantList(query);
			
			int length = plantList.size();
			if ((start + count) < plantList.size()) {
				length = start + count;
			}
			
			ListRange searchRes = new ListRange();
			searchRes.setTotalSize(plantList.size());
			searchRes.setData(plantList.subList(start, length).toArray());
			
			return result = searchRes;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public ListRange getCarrierList(int start, int count, String orderBy, String query,
			HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCarrierList", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
			
			ServiceLocator serviceLocator = getServiceLocator();
			OrderSpecialService orderSpecialService = serviceLocator.getOrderSpecialService();

			List<CarrierEntity> carrierList = orderSpecialService.fetchRailCarriers(query);
			
			int length = carrierList.size();
			if ((start + count) < carrierList.size()) {
				length = start + count;
			}
			
			ListRange searchRes = new ListRange();
			searchRes.setTotalSize(carrierList.size());
			searchRes.setData(carrierList.subList(start, length).toArray());
			
			return result = searchRes;
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
	 * Returns the filtered (by query ComboBox) list of vlgns_sap_sold_ship_sarea_srch elements
	 * Used by the Ship-to search screen of UC 4012 Order Taking
	 * 
	 * @param start
	 * @param count
	 * @param orderBy
	 * @param query
	 * @param session
	 * @return
	 */
	public ListRange fetchSoldtoShiptoSareaList(int start, int count, String orderBy,
	//HashMap filters,
			String query, Boolean custWithoutOrderToday, HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"custWithoutOrderToday", custWithoutOrderToday},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSoldtoShiptoSareaList", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
			OrderSpecialForm form = (OrderSpecialForm)request.getSession().getAttribute(OrderSpecialForm.SESSION_ORDERSPECIALFORM_NAME);
			Boolean isRepairMaintenanceDeliveryType = false;
			if(form.getDistDestCd().equals(Constant.DISTRIBUTION_DEST_REPAIR)){
				isRepairMaintenanceDeliveryType = true;
			}
			return result = getServiceLocator().getOrderSpecialService().fetchSoldtoShiptoSareaList(
					start, count, query, custWithoutOrderToday, request.getLocale(), isRepairMaintenanceDeliveryType);
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
	 * Used by Order Special 
	 */
	public ListRange getShippingpointList(int start, int count, String orderBy, String query,
			HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getShippingpointList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
			ListRange list = getServiceLocator().getOrderSpecialService().fetchShippingpointList(
					start, count, query);
			return result = list;
			
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
	 * Used by Order Special 
	 */
	public Boolean checkTimetable(Long shiptoId, Long shippingPointId, HttpServletRequest request) {
		// [[
		Boolean result1 = null;
		Object[][] params = {{"shiptoId", shiptoId},
							{"shippingPointId", shippingPointId},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "checkTimetable", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
			
			if ((shippingPointId != null) && (shiptoId != null)) {
				
				ShippingpointEntity sp = getServiceLocator().getOrderSpecialService().fetchShippingpoint(shippingPointId);
				SapBpEntity shipto = getServiceLocator().getDispokarteService().getShiptoSapBpById(shiptoId);
				
				if ((sp.getRailwayStation() != null) && (shipto.getRailwayStation() != null)) {
					final List<RailTimetableEntity> railTimetableList = getServiceLocator().getOrderSpecialService().fetchRailTimetableList(
							sp.getRailwayStation().getRailwayStationId(), 
							shipto.getRailwayStation().getRailwayStationId());
					
					if ((railTimetableList == null) || (railTimetableList.isEmpty())){
						return result1 = false;
					}
				}
			}
			
			return result1 = true;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}
	
	/**
	 * Returns a {@link ListRange} with two objects and size of 2. An object can be 
	 * <code>null</code> if the corresponding entities couldn't been fetched.<br />
	 * This method is being used to get the necessary initial email to, and text
	 * fields for the special order creation popup 
	 * <li>The first element is a {@link ShiptoEntity} that only depends on the first
	 * parameter <code>sapBpId</code>
	 * <li>The second element is a  {@link ShiptoSareaRestrEntity}. The lookup
	 * depends on all the parameters.
	 * @param sapBpId
	 * @param sareaId
	 * @param sapMaterialId
	 * @param request
	 * @return
	 */
	public Object[] getMailDataForSpecOrder(Long sapBpId, Long sareaId, HttpServletRequest request) {
		// [[
		Object[] result = null;
		Object[][] params = {{"sapBpId", sapBpId},
							{"sareaId", sareaId},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getMailDataForSpecOrder", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
			
			Object[] objects = new Object[2];
			
			if (sapBpId != null) {
				ShiptoEntity shipto = getServiceLocator().getOrderSpecialService().loadShipto(sapBpId);
				objects[0] = shipto;
				ShiptoSareaEntity shiptoSarea = getServiceLocator().getBestellungService().getShiptoSarea(shipto.getShiptoId(), sareaId);
				final List<ShiptoSareaDetailEntity> sareaRestrList = getServiceLocator().getBestellungService().getShiptoSareaRestrprdDetailedList(
						shiptoSarea.getShiptoSareaId(), 
						request.getLocale());
				
				if (sareaRestrList != null && !sareaRestrList.isEmpty()) {
					objects[1] = sareaRestrList.get(0);
				} else {
					objects[1] = null;
				}
			}
			
			return result = objects;
			
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
	 * Used by Order Special 
	 */
	public ListRange getBilltoList(int start, int count, String orderBy, String query,
			Long shiptoSapBpId, Long sareaId, HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"shiptoSapBpId", shiptoSapBpId},
							{"sareaId", sareaId},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getBilltoList", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
			
			if ((shiptoSapBpId != null) && (sareaId != null)) {
				ListRange listRange = getServiceLocator().getOrderSpecialService().fetchBilltoList(
						start, count, shiptoSapBpId, sareaId);
				return result = listRange;
			}
			
			ListRange listRange = new ListRange();
			listRange.setTotalSize(0);
			listRange.setData(new Object[]{});
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
	 * Used by Order Special 
	 */
	public ListRange getMaterialList(int start, int count, String orderBy, String query,
			String prodPlantCd, HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"prodPlantCd", prodPlantCd},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getMaterialList", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
			
			if (StringUtils.notEmpty(prodPlantCd)) {
				ListRange list = getServiceLocator().getOrderSpecialService().fetchMaterialList(
						start, count, query, request.getLocale().getLanguage(), prodPlantCd);
				return result = list;
			}
			
			ListRange listRange = new ListRange();
			listRange.setTotalSize(0);
			listRange.setData(new Object[]{});
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
	 * Returns a list of damageCodeSearchEntities according to the query. Refdataservice not used because cannot be used 
	 * with paging. 
	 */
	public ListRange fetchDamageCodeList(int start, int count, String orderBy, String query, HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchDamageCodeList", params);
		}
		// ]]
		try {
			
			Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);

			Locale locale = RequestUtils.getUserLocale(request, null);

			return result = getServiceLocator().getOrderDetailService().loadDamageCodes(start, count, query, locale);
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
