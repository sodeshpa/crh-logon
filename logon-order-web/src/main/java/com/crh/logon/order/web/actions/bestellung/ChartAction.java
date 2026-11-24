/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.bestellung;

 import java.io.IOException;
import java.util.ArrayList;
 import java.util.Collection;
import java.util.Date;
 import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 import com.crh.logon.business.services.utils.ServiceLocator;
 import com.crh.logon.business.services.utils.TimeZone;
 import com.crh.logon.order.web.actions.OrderActionSupport;
 import com.crh.logon.order.web.forms.truck.ChartForm;
 import com.crh.logon.order.web.utils.chart.ChartUtils;
 import com.crh.logon.utils.time.DateTimeParser;
 import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

 import com.crh.logon.business.services.interfaces.CarrierService;
import com.crh.logon.business.services.interfaces.PlantSareaService;
import com.crh.logon.business.services.interfaces.PoolAdminService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.utils.LogonUtil;
 import com.crh.logon.constants.Constant;
 import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.BestellungChartBean;
import com.crh.logon.web.utils.CheckboxDropdownEntity;
import com.crh.logon.web.utils.DropdownEntity;
import com.crh.logon.web.utils.JsonUtils;
import com.crh.logon.web.utils.PlantSareaUtils;

import flexjson.JSONSerializer;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_ChartForm" path="/bestellung/Chart" scope="session"
 *  				validate="false" 
 *
 * @struts.action-forward name="chart" path="ChartWrapperTile.def"
 */
public class ChartAction extends OrderActionSupport {
    
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ChartAction.class);
    
    private static final String ORDER_ENTRY_EMPTY_SELECTION_VALUE = "-1";

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws IOException, ServletException
	{
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
			Authorization.authorize(Authorization.PUBLIC_FUNCTION);

			ChartForm helpForm = (ChartForm) form;
			Locale locale = request.getLocale();
			
			//--------------------------------------------------------------------------------------
			// START --> Handle the parameters form 'Order Entry'
			//--------------------------------------------------------------------------------------
			if (!StringUtils.empty(request.getParameter("selectedDayDat"))) {
				Date date = DateTimeParser.parseLogonDayMonthYear4JS(locale, request.getParameter("selectedDayDat"));
				helpForm.setSelectedDayDat(date);
			}
			
			String ordEntryPoolId = request.getParameter("selectPool");
			if (!StringUtils.empty(ordEntryPoolId)){
				if (ORDER_ENTRY_EMPTY_SELECTION_VALUE.equals(ordEntryPoolId)) {
					ordEntryPoolId = ""; 
				}
				helpForm.setPoolIds(ordEntryPoolId);
			}
			
			String ordEntryCarrierId = request.getParameter("selectCarrier");
			if (!StringUtils.empty(ordEntryCarrierId)){
				if (ORDER_ENTRY_EMPTY_SELECTION_VALUE.equals(ordEntryCarrierId)) {
					ordEntryCarrierId = ""; 
				}
				helpForm.setCarrierIds(ordEntryCarrierId);
			}
			
			String selectDelTypeCd = request.getParameter("selectDelTypeCd");
			if (!StringUtils.empty(selectDelTypeCd)){
				int transCat = Constant.CHART_TRANS_CAT_TRUCK;
				if (LogonUtil.isRailOrder(Long.valueOf(selectDelTypeCd))) {
					transCat = Constant.CHART_TRANS_CAT_RAIL;	    		}

				helpForm.setTransCat(String.valueOf(transCat));
				
				final int chartDeliveryType = deliveryTypeForChartForm(Long.valueOf(selectDelTypeCd));
				if (chartDeliveryType != -1) {
					helpForm.setDeliveryTypes(String.valueOf(chartDeliveryType));
				}
			}
			
			/* If the chart is updated as a result of a material dropdown selection,
			 * the company code of the material entry has priority.
			 */
			final String companyCode = request.getParameter("companycode");
			final String companyCodeFromMaterial = request.getParameter("companycodeFromMaterial");
			if (companyCodeFromMaterial != null) {
				helpForm.setCompanycode(companyCodeFromMaterial);
			}
			else if (companyCode != null)
			{
				helpForm.setCompanycode(companyCode);
			}
			
			final String selectedDeliveryPlant = request.getParameter("selDeliveryPlantCd");
			if (selectedDeliveryPlant != null)
			{
				helpForm.setPlant(selectedDeliveryPlant);
			}
			
			final String selectedShippingPointId = request.getParameter("selShippingPointCd");
			if (selectedShippingPointId != null)
			{
				helpForm.setSelectedShippingPoint(selectedShippingPointId);
			}
			//--------------------------------------------------------------------------------------
			// END <--
			//--------------------------------------------------------------------------------------
			
			// fill all filter values into ChartBean
			BestellungChartBean chartBean = helpForm.getChartBean();

	    	chartBean.setPoolIds(helpForm.getSelectedPoolIds());
	    	chartBean.setCarrierIds(helpForm.getSelectedCarrierIds());
	    	chartBean.setTruckTypeIds(helpForm.getSelectedTruckTypes());
	    	chartBean.setDeliveryTypeIds(helpForm.getSelectedDeliveryTypes());
	    	chartBean.setTransCatId(helpForm.getSelectedTransCat());
	    	chartBean.setPlantId(helpForm.getSelectedPlant());
	    	chartBean.setShippingpointIds(helpForm.getSelectedShippingPoints());
	    	chartBean.setProductTypeIds(helpForm.getSelectedproductTypes());
	    	chartBean.setWagonTrspCatIds(helpForm.getSelectedWagonTrspCat());
	    	chartBean.setWagonTypeIds(helpForm.getSelectedWagonType());
	    	chartBean.setChartType(helpForm.getSelectedChartType());
	    	chartBean.setSelectedDayStr(helpForm.getSelectedDayDat());
	    	
			// --- Rail or road ---
			if (Constant.CHART_TRANS_CAT_RAIL ==  chartBean.getTransCatId()) {
				chartBean.setRailwayStationIds(getRailWayStationIds(chartBean.getPlantId()));
			}
	    	
	    	// --- Special case, when the pool or carrier is changed in the editor row and needs to override the selected values of chart area ---
	    	if (!StringUtils.empty(request.getParameter("filterPage"))) {
	    		helpForm.setFilterPage(request.getParameter("filterPage"));
	    	}
	    			
		    String forwardName = "chart";
		    
		    refreashFormInputFields(helpForm, locale);		    
		    
			return result = mapping.findForward(forwardName);
		}
		catch (Exception e) {
			LOG.error("An unknown error occurred during the chart rendering: ", e);
			return result = mapping.findForward("chart");
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
	 * In the chart form, the "delivery type" dropdown should be updated based on the
	 * delivery type of the order. The mapping is defined in this comment:
	 * https://aww.adnovum.ch/jira/browse/LOGON-5469?focusedCommentId=343132&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-343132
	 * This method implements the mapping.
	 * @return The delivery type constant (one of Constant.CHART_DELIVERY_TYPE_*) corresponding
	 * to the order delivery type, or -1 if the order delivery type was unknown or null.
	 */
	int deliveryTypeForChartForm(Long deliveryTypeCd)
	{
		final int UNKNOWN_TYPE = -1;
		
		// See comment in LOGON-5469 (link above in javadoc) for the specification of this method.
		if (deliveryTypeCd == null) {
			return UNKNOWN_TYPE;
		}
		
		switch ((int) deliveryTypeCd.longValue())
		{
		case (int) Constant.DELIVERY_TYPE_ROAD_DIRECT:
		case (int) Constant.DELIVERY_TYPE_RAIL_DIRECT:
		case (int) Constant.DELIVERY_TYPE_BIMODAL_RAIL:
		case (int) Constant.DELIVERY_TYPE_BIMODAL_ROAD:
		case (int) Constant.DELIVERY_TYPE_BLOCK_TRAIN:
		case (int) Constant.DELIVERY_TYPE_SHIP:
			return Constant.CHART_DELIVERY_TYPE_DELIVERED;
		case (int) Constant.DELIVERY_TYPE_PICKUP:
		case (int) Constant.DELIVERY_TYPE_SHIP_PICKUP:
		case (int) Constant.DELIVERY_TYPE_BLOCKTRAIN_PICKUP:
			return Constant.CHART_DELIVERY_TYPE_PICKUP;
		default:
			return UNKNOWN_TYPE;
		}
	}

	/**
	 * Refresh the form input fields ...
	 * 
	 * @param tpForm
	 * 			Chart Action Form
	 */
	private void refreashFormInputFields(ChartForm tpForm, Locale locale) {
		// [[
		Object[][] params = {{"tpForm", tpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "refreashFormInputFields", params);
		}
		// ]]
		try {			
			Map jsonData = tpForm.getJson();
			
			// set the checked pools
			List<Long> selectedPoolIds = tpForm.getSelectedPoolIds();
			List<CheckboxDropdownEntity> poolList = ChartUtils.fetchPoolList(tpForm.getTransCat(), locale, false, getPoolAdminService());
			refreshDropDown(selectedPoolIds, poolList, false);
			
			// refresh the carrier list
			List<Long> selectedCarrierIds = tpForm.getSelectedCarrierIds();
			List<CheckboxDropdownEntity> execCarrierList = ChartUtils.getCarrierDPEntities(selectedPoolIds, getCarrierService(), false, locale);
			refreshDropDown(selectedCarrierIds, execCarrierList, false);
			
			// -- truck type
			List<Long> selectedtruckTypes = tpForm.getSelectedTruckTypes();
			List<CheckboxDropdownEntity> truckTypeList = tpForm.getTruckTypeList();		
			refreshDropDown(selectedtruckTypes, truckTypeList, false);
			
			// -- delivery type
			List<Integer> selectedDeliveryTypes = tpForm.getSelectedDeliveryTypes();
			List<CheckboxDropdownEntity> deliveryTypeList = tpForm.getDeliveryTypeList();		
			refreshDropDown(selectedDeliveryTypes, deliveryTypeList, true);
			
			// -- plant
			String companycodeCd = tpForm.getCompanycode();
			List<DropdownEntity> plantBsList = null;
			if (!StringUtils.empty(companycodeCd)) {
				plantBsList = PlantSareaUtils.getPlantByCompanycodeCd(getPlantSareaService(), companycodeCd);
			}
			else {
				plantBsList = new ArrayList<DropdownEntity>();
			}
			
			// -- shipping point
			List<CheckboxDropdownEntity> shippingPointList = null;
			String plantCd = tpForm.getPlant();
			if (!StringUtils.empty(plantCd)) {
				List<Long> selectedShippingPoints = tpForm.getSelectedShippingPoints();
				shippingPointList = ChartUtils.getShippingPointList(getBestellungService(),
						plantCd, true, locale);
				refreshDropDown(selectedShippingPoints, shippingPointList, false);
			}
			else {
				shippingPointList = new ArrayList<CheckboxDropdownEntity>();
			}
			
			// -- product type
			List<Integer> selectedProductTypes = tpForm.getSelectedproductTypes();
			List<DropdownEntity> productTypeList = tpForm.getProductTypeList();			
			
			// -- wagon trsp category
			List<Long> selectedWagonTrspCat = tpForm.getSelectedWagonTrspCat();
			List wagonTrspCatList = tpForm.getWagonTrspCatList();				
			refreshDropDown(selectedWagonTrspCat, wagonTrspCatList, false);			
			
			// -- wagon type
			List<Long> selectedWagonType = tpForm.getSelectedWagonType();
			List<CheckboxDropdownEntity> wagonTypeList = tpForm.getWagonTypeList();				
			refreshDropDown(selectedWagonType, wagonTypeList, false);	
			
			JSONSerializer jsonSerializer = getJsonSerializer();
			jsonData.put("poolList", jsonSerializer.serialize(poolList));
			jsonData.put("execCarrierList", jsonSerializer.serialize(execCarrierList));
			jsonData.put("truckTypeList", jsonSerializer.serialize(truckTypeList));
			jsonData.put("deliveryTypeList", jsonSerializer.serialize(deliveryTypeList));
			jsonData.put("shippingPointList", jsonSerializer.serialize(shippingPointList));
			jsonData.put("productTypeList", jsonSerializer.serialize(productTypeList));
			jsonData.put("wagonTrspCatList", jsonSerializer.serialize(wagonTrspCatList));
			jsonData.put("wagonTypeList", jsonSerializer.serialize(wagonTypeList));
			jsonData.put("plantBsList", jsonSerializer.serialize(plantBsList));
			
			tpForm.setJson(jsonData);
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
	 * 
	 * @param selectedIds
	 * @param entityList
	 */
	private void refreshDropDown(List<? extends Number> selectedIds, Collection<CheckboxDropdownEntity> entityList, boolean isInt) {
		// [[
		Object[][] params = {{"selectedIds", selectedIds},
							{"entityList", entityList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "refreshDropDown", params);
		}
		// ]]
		try {
			if (entityList == null || entityList.isEmpty()) {
				return; // nothing todo
			}

			for (Iterator<CheckboxDropdownEntity> iter = entityList.iterator(); iter.hasNext();) {
				CheckboxDropdownEntity entity = iter.next();
				
				Number value = null;
				if (isInt) {
					value = Integer.valueOf(entity.getValue());
				}
				else {
					value = Long.valueOf(entity.getValue());					
				}
				boolean checked = (selectedIds != null && selectedIds.contains(value));
				entity.setChecked(checked);
			}	
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private Set<Long> getRailWayStationIds(String plantCds) {
		// [[
		Set<Long> result = null;
		Object[][] params = {{"plantCds", plantCds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getRailWayStationIds", params);
		}
		// ]]
		try {
			List<ShippingpointEntity> shippingPointList = getShippingPointService().loadShippingPoints(null, false, plantCds);
			
			if (shippingPointList == null) {
				return result = null;
			}
			
			result = new HashSet<Long>();
			
			for (ShippingpointEntity shippingPoint : shippingPointList) {
				Long raiwayStationId = shippingPoint.getRailwayStation() == null ? null
						: shippingPoint.getRailwayStation().getRailwayStationId();
				
				if (raiwayStationId != null) {
					result.add(raiwayStationId);
				}
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
	
	//----------------------------------------------------------------------------------------------
	// Static methods ...
	//----------------------------------------------------------------------------------------------
	
	/**
	 *
     * @param tpForm
     * @param actionSupport
     * @param locale
     */
	public static void initForm(ChartForm tpForm, boolean isOrderEntry, HttpServletRequest request,
			OrderActionSupport actionSupport, Locale locale) {
		// [[
		Object[][] params = {{"tpForm", tpForm},
							{"isOrderEntry", isOrderEntry + ""},
							{"request", request},
							{"serviceLocator", actionSupport},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(ChartAction.class, "initForm", params);
		}
		// ]]
		try {
			if (tpForm == null) {
				tpForm = new ChartForm();
				request.getSession().setAttribute(ChartForm.FORM_NAME, tpForm);
			}
			
			/*
			 * LOGON-6096 Truck Planning Screen - Filter and unscheduled order visualization
			 * Do not re-initialize the form if it was initialized already.
			 */
			if (tpForm.isInitialized()) {
				return;
			}
			
			tpForm.init();
			
			// -------------------------------------------------------------------------------------
			// init Services
			// -------------------------------------------------------------------------------------
            ServiceLocator serviceLocator = actionSupport.getServiceLocator();

			RefDataGenericsService refData = serviceLocator.getRefDataGenericsService();
			PoolAdminService poolAdminService = serviceLocator.getPoolAdminService();
			JSONSerializer jSONSerializer = JsonUtils.getJsonSerializer();
			CarrierService carrierService = serviceLocator.getCarrierService();
			PlantSareaService plantSareaService = serviceLocator.getPlantSareaService();
			
			// -------------------------------------------------------------------------------------
			// init chart bean
			// -------------------------------------------------------------------------------------
		    BestellungChartBean chartBean = new BestellungChartBean();
		    
		    TimeZone timezone = actionSupport.getUserTimezone();
            Date tomorrow = timezone.tomorrow();
		  
	    	chartBean.setDeliveryFrom(tomorrow);
	    	chartBean.setDeliveryTo(tomorrow);
	    	chartBean.setSelectedDayStr(timezone.fmtAsDayMonthYear(chartBean.getDeliveryFrom())); // Default day is the first day
	    	chartBean.setDayList(new ArrayList());
	    	chartBean.getDayList().add(chartBean.getSelectedDayStr());
	    	chartBean.setTransCatId(Constant.CHART_TRANS_CAT_TRUCK);
	    	chartBean.setChartType(Constant.CHART_TYPE_NR_OF_TRUCKS);
			chartBean.setUserTimeZone(timezone);
	    	tpForm.setChartBean(chartBean);
			
	    	tpForm.setSelectedDayDat(chartBean.getDeliveryFrom());
	    	
	    	// -------------------------------------------------------------------------------------
			// init json map
	    	// -------------------------------------------------------------------------------------
			Map jsonData = new HashMap();
			
			// -- load pool information
			List<CheckboxDropdownEntity> poolList = ChartUtils.fetchPoolList(String.valueOf(Constant.DELIVERY_TYPE_ROAD_DIRECT), locale, false, poolAdminService);			
			jsonData.put("poolList", jSONSerializer.serialize(poolList));
			
			// -- load carrier information
			List<CheckboxDropdownEntity> execCarrierList = ChartUtils.getCarrierDPEntities(null, carrierService, false, locale);
			jsonData.put("execCarrierList", jSONSerializer.serialize(execCarrierList));
			
			// -- transport category
			List<CheckboxDropdownEntity> transCatList = ChartUtils.getTransCatList(request);			
			jsonData.put("transCatList", jSONSerializer.serialize(transCatList));
			
			// -- truck type
			List<CheckboxDropdownEntity> truckTypeList = ChartUtils.getTruckTypeList(refData, locale);
			jsonData.put("truckTypeList", jSONSerializer.serialize(truckTypeList));			
			
			// -- delivery type
			List<CheckboxDropdownEntity> deliveryTypeList = ChartUtils.getDeliveryTypeList(request);			
			jsonData.put("deliveryTypeList", jSONSerializer.serialize(deliveryTypeList));
			
			// -- plant bs
			List<DropdownEntity> plantList = new ArrayList(); 
			jsonData.put("plantBsList", jSONSerializer.serialize(plantList));
			
			// -- shipping point
			List shippingPoint = new ArrayList();
			jsonData.put("shippingPointList", jSONSerializer.serialize(shippingPoint));
			
			// -- product type
			List<DropdownEntity> productTypeList = ChartUtils.getProductTypeList(request);			
			jsonData.put("productTypeList", jSONSerializer.serialize(productTypeList));
			
			// -- chart type
			List<CheckboxDropdownEntity> chartTypeList = ChartUtils.getChartTypeList(request);			
			jsonData.put("chartTypeList", jSONSerializer.serialize(chartTypeList));
			
			// -- wagon category
			List<CheckboxDropdownEntity> wagonTrspCatList = ChartUtils.getWagonTrspCatList(refData, locale);			
			jsonData.put("wagonTrspCatList", jSONSerializer.serialize(wagonTrspCatList));
			
			// -- wagon type
			List<CheckboxDropdownEntity> wagonTypeList = ChartUtils.getWagonTypeTxEntityList(refData, true, locale);			
			jsonData.put("wagonTypeList", jSONSerializer.serialize(wagonTypeList));
			
			// -- companycode
			List companycodeList = PlantSareaUtils.getCompanycodeBsEntityList(plantSareaService);
			jsonData.put("companycodeList", jSONSerializer.serialize(companycodeList));
			
			tpForm.setJson(jsonData);
			
			tpForm.setTruckTypeList(truckTypeList);
			tpForm.setCompanycodeList(companycodeList);
			tpForm.setDeliveryTypeList(deliveryTypeList);
			tpForm.setTransCatList(transCatList);
			tpForm.setWagonTrspCatList(wagonTrspCatList);
			tpForm.setWagonTypeList(wagonTypeList);
			tpForm.setPlantList(plantList);
			tpForm.setProductTypeList(productTypeList);
			tpForm.setChartType(String.valueOf(Constant.CHART_TYPE_NR_OF_TRUCKS));
			tpForm.setTransCat(String.valueOf(Constant.CHART_TRANS_CAT_TRUCK)); // the default should be the 'road'
			
			tpForm.initPreselectedValues();
			
			tpForm.setInitialized(true);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

}
