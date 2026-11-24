/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.auftrag;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.auftrag.LiveKarteBean;
import com.crh.logon.order.web.forms.auftrag.LiveKarteForm;
import com.crh.logon.persistence.entities.views.OrderFiltersEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_auftrag_LiveKarteForm"
 *  path="/auftrag/LiveKarte" scope="session"
 *  validate="true"
 *
 * @struts.action-forward name="fetchfirst" path="/auftrag/LiveKarteEntry.do"
 * @struts.action-forward name="success" path="LiveKarteTile.def"
 * @struts.action-forward name="filters" path="LiveKarteFilters.def"
 */
public class LiveKarteAction extends OrderActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LiveKarteAction.class);
	private static final String FORWARD_SUCCESS = "success";
	private static final String FORWARD_FITLERS = "filters";
	public static final String FILTER_MAIN = "filter";
	public static final String FILTER_REFRESH = "refreshFilter";

	//icons
	public static final String LIVEMAP_ORDERMAP_PLANT = "livemap_ordermap_plant.png";
	public static final String LIVEMAP_ORDERMAP_SHIPPING_POINT = "livemap_ordermap_shipping_point.png";
	public static final String LIVEMAP_ORDERMAP_SHIPTO = "livemap_shipto.png";
	public static final String LIVEMAP_ORDERMAP_TRAIN_STATION = "livemap_ordermap_train_station.png";
	public static final String LIVEMAP_ROUTE = "livemap_route.png";
	public static final String LIVEMAP_EVENT = "livemap_event.png";
	public static final String LIVEMAP_NON_GEOEVENT = "livemap_non_geo_event.png";
	public static final String LIVEMAP_CONTAINER_UNLOADED = "livemap_container_unloaded.png";
	public static final String LIVEMAP_TRAILER_UNLOADED = "livemap_trailer_unloaded.png";
	public static final String LIVEMAP_SILO_UNLOADED = "livemap_silo_unloaded.png";
	public static final String LIVEMAP_RAILCAR_UNLOADED = "livemap_railcar_unloaded.png";
	public static final String LIVEMAP_TRUCK_UNLOADED_INSIDE_GEOFENCE = "livemap_truck_unloaded_inside_geofence.png";

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		
		// [[
		ActionForward result = null;
		Object[][] params = {{"mapping", mapping},
							{"form", form},
							{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.LIVE_MAP);
			LiveKarteForm helpForm = (LiveKarteForm) form;
			String forwardName = FORWARD_SUCCESS;
			
			String filterAction = request.getParameter("filterAction");
			String orderStatusSelectedList = request.getParameter("orderStatusSelectedList");
			if (logDebugEnabled) {
				LOG.debug("Filter Action: " + filterAction);
				LOG.debug("Order Status Selected List: " + orderStatusSelectedList);
			}
			
			if (filterAction != null) {
			
				if (FILTER_MAIN.equals(filterAction)) {
					if (logDebugEnabled) {
						LOG.debug("Action: user selected refrech button (orders will be reloaded from DB)");
					}
					
					//get order status code list
					List orderStatusCdList = new ArrayList();
					if (StringUtils.notEmpty(orderStatusSelectedList)) {
						// TODO: this should arrive by proper list marshaling
						String[] statArr = orderStatusSelectedList.split(",");
						
						for (int i=0; i<statArr.length; i++) {
							orderStatusCdList.add(Long.valueOf(statArr[i]));
						}
					}
					
					Long orderNumber = null;
					if (StringUtils.notEmpty(helpForm.getOrderNumber())) {
						try {
							orderNumber = Long.parseLong(helpForm.getOrderNumber());
						} 
						catch (NumberFormatException e) {
							MessageUtils.addActionError(request, "livemap.error.format.order.number");
						}
					}
					
					helpForm.initFlters();
					Locale locale = getLocale(request);
					initLiveKarte(helpForm, locale, orderStatusCdList, orderNumber);
					
				}
				else if (FILTER_REFRESH.equals(filterAction)) {
					if (logDebugEnabled) {
						LOG.debug("Action: user changed filter (order will NOT be reloaded from DB)");
					}
					loadFilters(helpForm);
				}
				forwardName = FORWARD_FITLERS;
			}
			
			return result = mapping.findForward(forwardName);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private void loadFilters(LiveKarteForm helpForm) {
		// [[
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadFilters", params);
		}
		// ]]
		try {
			
			// FIXME (valdas Apr 29, 2009): is it a right way to copy list?
			helpForm.setFiltOrderList((List) ((ArrayList) helpForm.getOrderList()).clone());
			ListIterator orderItr = helpForm.getOrderList().listIterator();
			
			//create filtered list
			while (orderItr.hasNext()) {
			
				OrderFiltersEntity orderFilter = (OrderFiltersEntity) orderItr.next();
				
				long orderId = orderFilter.getOrderId();
			
				if (orderFilter.getProductionPlantCd() != null) {
					if (helpForm.getPlantMap().containsKey(orderFilter.getProductionPlantCd())) {
						if (!((LiveKarteBean) helpForm.getPlantMap().get(
								orderFilter.getProductionPlantCd())).isChecked()) {
							helpForm.getFiltOrderList().remove(orderFilter);
							continue;
						}
					}
				}
			
				if (orderFilter.getShippingpointId() != null) {
					if (helpForm.getShPointMap().containsKey(orderFilter.getShippingpointId() + "")) {
						if (!((LiveKarteBean) helpForm.getShPointMap().get(
								orderFilter.getShippingpointId() + "")).isChecked()) {
							helpForm.getFiltOrderList().remove(orderFilter);
							continue;
						}
					}
				}
			
				if (orderFilter.getLoadingpointId() != null) {
					if (helpForm.getLdPointMap().containsKey(orderFilter.getLoadingpointId() + "")) {
						if (!((LiveKarteBean) helpForm.getLdPointMap().get(
								orderFilter.getLoadingpointId() + "")).isChecked()) {
							helpForm.getFiltOrderList().remove(orderFilter);
							continue;
						}
					}
				}
			
				if (orderFilter.getPoolId() != null) {
					if (helpForm.getPoolMap().containsKey(orderFilter.getPoolId() + "")) {
						if (!((LiveKarteBean) helpForm.getPoolMap().get(
								orderFilter.getPoolId() + "")).isChecked()) {
							helpForm.getFiltOrderList().remove(orderFilter);
							continue;
						}
					}
				}
			
				if (orderFilter.getCommCarrierId() != null) {
					if (helpForm.getCarrierMap().containsKey(orderFilter.getCommCarrierId() + "")) {
						if (!((LiveKarteBean) helpForm.getCarrierMap().get(
								orderFilter.getCommCarrierId() + "")).isChecked()) {
							helpForm.getFiltOrderList().remove(orderFilter);
							continue;
						}
					}
				}
			
				if (orderFilter.getSapMaterialId() != null) {
					if (helpForm.getMaterialMap().containsKey(orderFilter.getSapMaterialId() + "")) {
						if (!((LiveKarteBean) helpForm.getMaterialMap().get(
								orderFilter.getSapMaterialId() + "")).isChecked()) {
							helpForm.getFiltOrderList().remove(orderFilter);
							continue;
						}
					}
				}
			
				if (helpForm.getSareaMap().containsKey(orderFilter.getSareaId() + "")) {
					if (!((LiveKarteBean) helpForm.getSareaMap().get(orderFilter.getSareaId() + "")).isChecked()) {
						helpForm.getFiltOrderList().remove(orderFilter);
						continue;
					}
				}
			
				if (helpForm.getShiptoMap().containsKey(orderFilter.getShiptoSapBpId() + "")) {
					if (!((LiveKarteBean) helpForm.getShiptoMap().get(
							orderFilter.getShiptoSapBpId() + "")).isChecked()) {
						helpForm.getFiltOrderList().remove(orderFilter);
						continue;
					}
				}
			
				if (helpForm.getSoldtoMap().containsKey(orderFilter.getSoldtoSapBpId() + "")) {
					if (!((LiveKarteBean) helpForm.getSoldtoMap().get(
							orderFilter.getSoldtoSapBpId() + "")).isChecked()) {
						helpForm.getFiltOrderList().remove(orderFilter);
						continue;
					}
				}
			
				if (helpForm.getOrderNumMap().containsKey(orderFilter.getOrderNumber() + "")) {
					if (!((LiveKarteBean) helpForm.getOrderNumMap().get(
							orderFilter.getOrderNumber() + "")).isChecked()) {
						helpForm.getFiltOrderList().remove(orderFilter);
						continue;
					}
				}
			
				if (helpForm.getSgroupMap().containsKey(orderFilter.getSgroupCd())) {
					if (!((LiveKarteBean) helpForm.getSgroupMap().get(orderFilter.getSgroupCd())).isChecked()) {
						helpForm.getFiltOrderList().remove(orderFilter);
						continue;
					}
				}
			
				if (helpForm.getSofficeMap().containsKey(orderFilter.getSofficeCd())) {
					if (!((LiveKarteBean) helpForm.getSofficeMap().get(orderFilter.getSofficeCd())).isChecked()) {
						helpForm.getFiltOrderList().remove(orderFilter);
						continue;
					}
				}
			
				if (helpForm.getTruckMap().containsKey(orderFilter.getTruckId() + "")) {
					if (!((LiveKarteBean) helpForm.getTruckMap().get(orderFilter.getTruckId() + "")).isChecked()) {
						helpForm.getFiltOrderList().remove(orderFilter);
						continue;
					}
				}
			
				if (helpForm.getDriverMap().containsKey(orderFilter.getDriverId() + "")) {
					if (!((LiveKarteBean) helpForm.getDriverMap().get(
							orderFilter.getDriverId() + "")).isChecked()) {
						helpForm.getFiltOrderList().remove(orderFilter);
						continue;
					}
				}
				
			
			}
			
			//set all checked field invisible
			setFieldsInvisible(helpForm.getPlantMap().entrySet().iterator());
			setFieldsInvisible(helpForm.getShPointMap().entrySet().iterator());
			setFieldsInvisible(helpForm.getLdPointMap().entrySet().iterator());
			setFieldsInvisible(helpForm.getPoolMap().entrySet().iterator());
			setFieldsInvisible(helpForm.getCarrierMap().entrySet().iterator());
			setFieldsInvisible(helpForm.getTruckMap().entrySet().iterator());
			setFieldsInvisible(helpForm.getSareaMap().entrySet().iterator());
			setFieldsInvisible(helpForm.getDriverMap().entrySet().iterator());
			setFieldsInvisible(helpForm.getSofficeMap().entrySet().iterator());
			setFieldsInvisible(helpForm.getSgroupMap().entrySet().iterator());
			setFieldsInvisible(helpForm.getMaterialMap().entrySet().iterator());
			setFieldsInvisible(helpForm.getSoldtoMap().entrySet().iterator());
			setFieldsInvisible(helpForm.getShiptoMap().entrySet().iterator());
			setFieldsInvisible(helpForm.getOrderNumMap().entrySet().iterator());
			
			//set visible only those filters which are applicable  
			ListIterator tmpOrderItr = helpForm.getFiltOrderList().listIterator();
			
			while (tmpOrderItr.hasNext()) {
			
				OrderFiltersEntity orderFilter = (OrderFiltersEntity) tmpOrderItr.next();
				long orderId = orderFilter.getOrderId();
			
				//plant list
				if (orderFilter.getProductionPlantCd() != null) {
					if (helpForm.getPlantMap().containsKey(orderFilter.getProductionPlantCd())) {
						((LiveKarteBean) helpForm.getPlantMap().get(
								orderFilter.getProductionPlantCd())).setVisible(true);
					}
				}
			
				//shipping point list
				if (orderFilter.getShippingpointId() != null) {
					if (helpForm.getShPointMap().containsKey(orderFilter.getShippingpointId() + "")) {
						((LiveKarteBean) helpForm.getShPointMap().get(
								orderFilter.getShippingpointId() + "")).setVisible(true);
					}
				}
			
				//loading point list
				if (orderFilter.getLoadingpointId() != null) {
					if (helpForm.getLdPointMap().containsKey(orderFilter.getLoadingpointId() + "")) {
						((LiveKarteBean) helpForm.getLdPointMap().get(
								orderFilter.getLoadingpointId() + "")).setVisible(true);
					}
				}
			
				//carrier list
				if (orderFilter.getCommCarrierId() != null) {
					if (helpForm.getCarrierMap().containsKey(orderFilter.getCommCarrierId() + "")) {
						((LiveKarteBean) helpForm.getCarrierMap().get(
								orderFilter.getCommCarrierId() + "")).setVisible(true);
					}
				}
			
				//pool list
				if (orderFilter.getPoolId() != null) {
					if (helpForm.getPoolMap().containsKey(orderFilter.getPoolId() + "")) {
						((LiveKarteBean) helpForm.getPoolMap().get(orderFilter.getPoolId() + "")).setVisible(true);
					}
				}
			
				//sales area list
				if (helpForm.getSareaMap().containsKey(orderFilter.getSareaId() + "")) {
					((LiveKarteBean) helpForm.getSareaMap().get(orderFilter.getSareaId() + "")).setVisible(true);
				}
			
				//material list
				if (orderFilter.getSapMaterialId() != null) {
					if (helpForm.getMaterialMap().containsKey(orderFilter.getSapMaterialId() + "")) {
						((LiveKarteBean) helpForm.getMaterialMap().get(
								orderFilter.getSapMaterialId() + "")).setVisible(true);
					}
				}
			
				//soldto list
				if (helpForm.getSoldtoMap().containsKey(orderFilter.getSoldtoSapBpId() + "")) {
					((LiveKarteBean) helpForm.getSoldtoMap().get(
							orderFilter.getSoldtoSapBpId() + "")).setVisible(true);
				}
			
				//shipto list
				if (helpForm.getShiptoMap().containsKey(orderFilter.getShiptoSapBpId() + "")) {
					((LiveKarteBean) helpForm.getShiptoMap().get(
							orderFilter.getShiptoSapBpId() + "")).setVisible(true);
				}
			
				//order number list
				if (helpForm.getOrderNumMap().containsKey(orderFilter.getOrderNumber() + "")) {
					((LiveKarteBean) helpForm.getOrderNumMap().get(
							orderFilter.getOrderNumber() + "")).setVisible(true);
				}
			
				//Sales Office list
				if (orderFilter.getSofficeCd() != null) {
					if (helpForm.getSofficeMap().containsKey(orderFilter.getSofficeCd())) {
						((LiveKarteBean) helpForm.getSofficeMap().get(
								orderFilter.getSofficeCd() + "")).setVisible(true);
					}
				}
			
				//Sales Group list
				if (orderFilter.getSgroupCd() != null) {
					if (helpForm.getSgroupMap().containsKey(orderFilter.getSgroupCd())) {
						((LiveKarteBean) helpForm.getSgroupMap().get(orderFilter.getSgroupCd() + "")).setVisible(true);
					}
				}
			
				//Truck list
				if (orderFilter.getTruckId() != null) {
					if (helpForm.getTruckMap().containsKey(orderFilter.getTruckId() + "")) {
						((LiveKarteBean) helpForm.getTruckMap().get(orderFilter.getTruckId() + "")).setVisible(true);
					}
				}
			
				//Driver list
				if (orderFilter.getDriverId() != null) {
					if (helpForm.getDriverMap().containsKey(orderFilter.getDriverId() + "")) {
						((LiveKarteBean) helpForm.getDriverMap().get(orderFilter.getDriverId() + "")).setVisible(true);
					}
				}
			
			}
			
			//remove
			tmpOrderItr = null;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	private void setFieldsInvisible(Iterator it) {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("setFieldsInvisible(): '" + it + "'");
		}
		// ]]
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (((LiveKarteBean) entry.getValue()).isChecked()) {
				((LiveKarteBean) entry.getValue()).setVisible(false);
			}
		}
	}
}
