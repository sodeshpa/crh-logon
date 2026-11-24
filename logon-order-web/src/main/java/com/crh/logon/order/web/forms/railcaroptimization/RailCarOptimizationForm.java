/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.railcaroptimization;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.order.web.actions.railcaroptimization.RailCarOptimizationAction;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.refdata.WagonTypeBsEntity;
import com.crh.logon.persistence.entities.views.RailPlantSpEntity;
import com.crh.logon.persistence.entities.views.RailcarOptOpenOrdersEntity;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_RailCarOptimizationForm"
 */
public class RailCarOptimizationForm extends ActionForm {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailCarOptimizationForm.class);
    private static final String DATE_FORMAT = "dd.MM.yyyy";

    private Date date; 
    
    private List plants;
    private List shippingpoints;
    private List wagonTypes;
    private Map wagonTypeMap;
    
    private HashMap selectedShippingpointsCache;
    private String selectedPlant;
	private Set selectedShippingpoints;
    private Set selectedWagonTypes;
    
    private RailCarTypeInfoBean typeInfoBean;
    
    
    private List wagonList;
    private List openOrders;
    private Map openOrdersPerWagonType;
    private Map<Long,Map> shiptoIdsForWagonTypes;

	private TimeZone timezone;

	/**
	 * Default constructor.
	 */
	public RailCarOptimizationForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "RailCarOptimizationForm");
		}
		// ]]
		try {
			init();
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
	 * Initializes the form. This has the effect of clearing all the input
	 * fields on the web form.
	 */
	private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			selectedShippingpoints = new HashSet();
			selectedPlant = null;
			selectedWagonTypes = null;
			plants = null;
			shippingpoints = null;
			wagonTypes = null;
			wagonTypeMap = new HashMap();
			date = new Date();
			selectedShippingpointsCache = new HashMap();
			typeInfoBean = null;
			openOrders = new LinkedList();
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
	 * Clears all the input fields on the web form, discards any existing
	 * search results, and resets any existing pages. Called in response to
	 * clicking the reset button on the web form.
	 */
	public void clear() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clear");
		}
		// ]]
		try {
			init();
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
     * Override struts method.
     * Note: this is not associated with or invoked by the reset button in the
     * ordermgr.web form, but rather is called on every request before the struts action
     * method gets control and, more importantly, before the request parameters
     * are automatically copied into the form. The primary use is to reset
     * checkboxes and struts multiboxes and the like for which the browser only
     * sends the form parameters when they are set.
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		// [[
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "reset", params);
		}
		// ]]
		try {
			if (request.getParameter(RailCarOptimizationAction.ACTION_SET_SHIPPINGPOINTS) != null){
				selectedShippingpoints.clear();
			}
			if (request.getParameter(RailCarOptimizationAction.ACTION_SET_WAGON_TYPES) != null){
				selectedWagonTypes.clear();
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
    //------------------------------------------------------------//
    //----------------------SHIPPINGPOINT-- ----------------------//
    //------------------------------------------------------------//
	public boolean getSelectedShippingpointCheckbox(int index){
		if (shippingpoints != null){
			RailPlantSpEntity sp = (RailPlantSpEntity)shippingpoints.get(index);
			return selectedShippingpoints.contains(sp.getShippingpointId());
		}
		else {
			return false;
		}
	}
    
	public void setSelectedShippingpointCheckbox(int index, boolean value){
		if (shippingpoints != null){
			RailPlantSpEntity sp = (RailPlantSpEntity)shippingpoints.get(index);
			selectedShippingpoints.add(sp.getShippingpointId());
		}
	}	
	
	public String getSelectedShippingpointsString(){
		if (selectedShippingpoints.size() == 0){
			return "none";
		}
		else if (selectedShippingpoints.size() == shippingpoints.size()){
			return "all";
		}
		else {
			Iterator i = shippingpoints.iterator();
			String res = "";
			while (i.hasNext()){
				RailPlantSpEntity rps = (RailPlantSpEntity)i.next();
				if (selectedShippingpoints.contains(rps.getShippingpointId())){
					res += rps.getShippingpointName() + " ";
				}
			}
			return res;
		}
	}	
	
	public Set getSelectedShippinpointsStationIds(){
		Set stations = new HashSet();
		Iterator i = shippingpoints.iterator();
		while (i.hasNext()) {
			RailPlantSpEntity rps = (RailPlantSpEntity) i.next();
			if (selectedShippingpoints.contains(rps.getShippingpointId())){
				if(rps.getRailwayStationId() != null) {
					stations.add(rps.getRailwayStationId());					
				}
			}
		}
		return stations;
	}
	
	/**
	 * @return the selectedShippingpointsCache
	 */
	public HashMap getSelectedShippingpointsCache() {
		return selectedShippingpointsCache;
	}
	
	/**
	 * @param selectedShippingpointsCache the selectedShippingpointsCache to set
	 */
	public void setSelectedShippingpointsCache(HashMap selectedShippingpointsCache) {
		this.selectedShippingpointsCache = selectedShippingpointsCache;
	}
	
	public void initSelectedShippingpoints(){
		HashSet nsc = (HashSet)selectedShippingpointsCache.get(selectedPlant);
		
		if (nsc == null) {
			nsc = new HashSet();
			selectedShippingpointsCache.put(selectedPlant, nsc);
			if (shippingpoints != null){
				Iterator i = shippingpoints.iterator();
				while (i.hasNext()){
					nsc.add(((RailPlantSpEntity)i.next()).getShippingpointId());
				}
			}
		}
		selectedShippingpoints = nsc;
	}
	
	/**
	 * @return the shippingpoints
	 */
	public List getShippingpoints() {
		return shippingpoints;
	}
	
	/**
	 * @param shippingpoints the shippingpoints to set
	 */
	public void setShippingpoints(List shippingpoints) {
		this.shippingpoints = shippingpoints;
	}
	
	public Set getSelectedShippingpoints() {
		return selectedShippingpoints;
	}
	
	public void setSelectedShippingpoints(Set selectedShippingpoints) {
		this.selectedShippingpoints = selectedShippingpoints;
	}
	
    //------------------------------------------------------------//
    //----------------------WAGON TYPES---------------------------//
    //------------------------------------------------------------//
	
	public boolean getSelectedWagonTypeCheckbox(int index){
		if (wagonTypes != null){
			WagonTypeBsEntity type = (WagonTypeBsEntity)wagonTypes.get(index);
			return selectedWagonTypes.contains(type.getWagonTypeCd());
		}
		else {
			return false;
		}
	}
    
	public void setSelectedWagonTypeCheckbox(int index, boolean value){
		if (wagonTypes != null){
			WagonTypeBsEntity type = (WagonTypeBsEntity)wagonTypes.get(index);
			selectedWagonTypes.add(type.getWagonTypeCd());
		}
	}	
	
	public String getSelectedWagonTypesString(){
		if (selectedWagonTypes.size() == 0){
			return "none";
		}
		else if (selectedWagonTypes.size() == wagonTypes.size()){
			return "all";
		}
		else {
			Iterator i = wagonTypes.iterator();
			String res = "";
			while (i.hasNext()){
				WagonTypeBsEntity rps = (WagonTypeBsEntity)i.next();
				if (selectedWagonTypes.contains(rps.getWagonTypeCd())){
					res += rps.getWagonTypeName() + " ";
				}
			}
			return res;
		}
	}	

	public void initSelectedWagonTypes(){
		HashSet	nsc = new HashSet();
			if (wagonTypes != null){
				Iterator i = wagonTypes.iterator();
				while (i.hasNext()){
					WagonTypeBsEntity type = (WagonTypeBsEntity)i.next();
					if (type.getOptimizationFlag()) {						
						nsc.add(type.getWagonTypeCd());
					}
					wagonTypeMap.put(type.getWagonTypeCd(), type);
				}
			}
		selectedWagonTypes = nsc;
	}
	
	/**
	 * @return the wagonTypes
	 */
	public List getWagonTypes() {
		return wagonTypes;
	}

	/**
	 * @param wagonTypes the wagonTypes to set
	 */
	public void setWagonTypes(List wagonTypes) {
		this.wagonTypes = wagonTypes;
	}


	/**
	 * @return the selectedWagonTypes
	 */
	public Set getSelectedWagonTypes() {
		return selectedWagonTypes;
	}

	/**
	 * @param selectedWagonTypes the selectedWagonTypes to set
	 */
	public void setSelectedWagonTypes(Set selectedWagonTypes) {
		this.selectedWagonTypes = selectedWagonTypes;
	}
	
	
    //------------------------------------------------------------//
    //------------------------------------------------------------//
    //------------------------------------------------------------//

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the plants
	 */
	public List getPlants() {
		return plants;
	}

	/**
	 * @param plants the plants to set
	 */
	public void setPlants(List plants) {
		this.plants = plants;
	}


    /**
	 * @return the selectedPlant
	 */
	public String getSelectedPlant() {
		return selectedPlant;
	}

	/**
	 * @param selectedPlant the selectedPlant to set
	 */
	public void setSelectedPlant(String selectedPlant) {
		this.selectedPlant = selectedPlant;
	}

    public String getStringDate() {
		return timezone.fmtAsDayMonthYear(date);
	}

	public void setStringDate(String text) throws ParseException {
		this.date = timezone.parseDayMonthYear(text);
	}

	/**
	 * @return the wagonList
	 */
	public List getWagonList() {
		return wagonList;
	}

	/**
	 * @param wagonList the wagonList to set
	 */
	public void setWagonList(List wagonList) {
		this.wagonList = wagonList;
	}

	/**
	 * @return the countBean
	 */
	public RailCarTypeInfoBean getTypeInfoBean() {
		return typeInfoBean;
	}

	/**
	 * @param typeInfoBean the countBean to set
	 */
	public void setTypeInfoBean(RailCarTypeInfoBean typeInfoBean) {
		this.typeInfoBean = typeInfoBean;
	}

	/**
	 * @return the wagonTypeMap
	 */
	public Map getWagonTypeMap() {
		return wagonTypeMap;
	}

	/**
	 * @return the openOrders
	 */
	public List getOpenOrders() {
		return openOrders;
	}

	/**
	 * @param openOrders the openOrders to set
	 */
	public void setOpenOrders(List openOrders) {
		this.openOrders = openOrders;
	}
	
	public int getOpenOrderCount(){
		return this.openOrders.size();
	}

	/**
	 * @return the openOrdersPerWagonType
	 */
	public Map getOpenOrdersPerWagonType() {
		return openOrdersPerWagonType;
	}

	/**
	 * @param openOrdersPerWagonType the openOrdersPerWagonType to set
	 */
	public void setOpenOrdersPerWagonType(Map openOrdersPerWagonType) {
		this.openOrdersPerWagonType = openOrdersPerWagonType;
	}

	public Set getShiptoIds() {
		Set shiptoIds = new HashSet();
		Iterator i = this.openOrders.iterator();
		while (i.hasNext()){
			RailcarOptOpenOrdersEntity current = (RailcarOptOpenOrdersEntity) i.next();
			shiptoIds.add(current.getShiptoId());
		}
		return shiptoIds;
	}

	/**
	 * @return the shiptoIdsForWagonTypes
	 */
	public Map<Long, Map> getShiptoIdsForWagonTypes() {
		return shiptoIdsForWagonTypes;
	}

	/**
	 * @param shiptoIdsForWagonTypes the shiptoIdsForWagonTypes to set
	 */
	public void setShiptoIdsForWagonTypes(Map<Long, Map> shiptoIdsForWagonTypes) {
		this.shiptoIdsForWagonTypes = shiptoIdsForWagonTypes;
	}

	public void setTimeZone(TimeZone timezone) {
		this.timezone = timezone;
	}
}

