/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.dispo;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.DispoCarrierWrapper;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusTxEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.DropdownEntity;

/**
 * Used by the JSP which for Truck Scheduling.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_dispo_DispoForm"
 */
public class DispoForm extends AbstractDispoForm {
	
    private static final long serialVersionUID = -8910104051337644556L;

    public static final String FORM_NAME = "order_dispo_DispoForm";
    
    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(DispoForm.class);

    /* lists & dropdowns */
    private List<PoolTxEntity> pools;
    private List<PoolTxEntity> allPools;
	private List<DispoCarrierWrapper> carriers;
	private List<DropdownEntity> deliveringPlants;
    private List<OrderStatusTxEntity> orderStatusList;
    
    private String formAction;
    
    /* user data */ 
    private int selectedPoolIndex;
    /**
	 * Stores the selected carriers by the user for every pool.
	 * Map<index of the pool, set of selected carrier id's>
	 */
    private HashMap<Integer, Set<Long>> selectedCarriersCache;
    /** A set of IDs of the selected carriers within the selected pool. */
    private Set<Long> selectedCarriers;
    private int startHour;
    private int hourSpan; // Range
    private int timegrid;
    private float heightFactor;
    private int maxNrOfSelectableAssignments;
    private boolean showOnlyTrucksWithOrders; //FIXME: Remove this, since this is not used anymore!
    private boolean hidePlanningTrucks;
    private boolean hideRealTrucks;    
    private boolean hideTrucksWithoutDeliveries;
    private boolean hideScheduledCustorders;
    private boolean hideClosedCustorders;
    private boolean hideCustorderHeaders;
    private boolean hideInactiveTrucks;
    private boolean hideTrucksAndOrderOfPartners;
    private Set<String> selectedDeliveringPlants;
    private Set<Long> selectedOrderStatuses;
    private Long custorderNumber;
    private Long orderNumber;
    /** The selected filter value for Visual filter (0 - Material, 1 - Plant codes). */
    private int selectedFilterForVisual;
    
    private Long truckPriority;
    
	private boolean applyUserDefaultSettings = false;
    
	/**
     * Default constructor.
     */
    public DispoForm() {
		// [[
	
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "DispoForm");
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
     * fields on the Truck Scheduling screen.
     */
    protected void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			allPools = null;
			pools = null;
			carriers = null;
			selectedCarriers = new HashSet<Long>();
			selectedPoolIndex = 0;
			hourSpan = -1;
			timegrid = 4;
			startHour = -1;
			heightFactor = -1;
			selectedCarriersCache = new HashMap<Integer, Set<Long>>();
			maxNrOfSelectableAssignments = -1;
			showOnlyTrucksWithOrders = false;
			hidePlanningTrucks = false;
			hideRealTrucks = false;
		    hideTrucksWithoutDeliveries = false;
		    hideScheduledCustorders = false;
		    hideClosedCustorders = false;
		    hideCustorderHeaders = false;
		    hideInactiveTrucks = false;
			deliveringPlants = null;
			selectedDeliveringPlants = new HashSet<String>();
			orderStatusList = null;
			selectedOrderStatuses = new HashSet<Long>();
			custorderNumber = 0l;
			selectedFilterForVisual = 0;
			orderNumber = 0l;
			truckPriority = null;
			hideTrucksAndOrderOfPartners = false;
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
     * Clears out the variables from the struts form 
     * which stores the user preferences (defaults).
     */
    public void clearDefaults() {
    	formAction = null;
    	
		applyUserDefaultSettings = false;
		
		selectedCarriers = new HashSet<Long>();
		selectedPoolIndex = 0;
	    hourSpan = -1;
		timegrid = -1;
	    hidePlanningTrucks = false;
		hideRealTrucks = false;
	    hideTrucksWithoutDeliveries = false;
	    hideScheduledCustorders = false;
	    hideClosedCustorders = false;
	    hideCustorderHeaders = false;
	    hideInactiveTrucks = false;
	    hideTrucksAndOrderOfPartners = false;
	    selectedDeliveringPlants = new HashSet<String>();
	    selectedOrderStatuses = new HashSet<Long>();
	    selectedFilterForVisual = 0;
	    truckPriority = null;
    }

    /**
     * Override struts method.
     * Note: this is not associated with or invoked by the reset button on the UI
     * but rather is called on every request before the struts action
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
			if (request.getParameter("setCarrierrs") != null) {
				selectedCarriers.clear();
			}
			if (request.getParameter("setDisplay") != null) {
			    hidePlanningTrucks = false;
			    hideRealTrucks = false;
			    hideTrucksWithoutDeliveries = false;
			    hideScheduledCustorders = false;
			    hideClosedCustorders = false;
			    hideCustorderHeaders = false;
			    hideInactiveTrucks = false;
			    hideTrucksAndOrderOfPartners = false;
			}
			if (request.getParameter("setTrucks") != null) {
				showOnlyTrucksWithOrders = false;				
			}
			if (request.getParameter("setDeliveringPlants") != null) {
				selectedDeliveringPlants.clear();
			}
			if (request.getParameter("setOrderStatuses") != null) {
				selectedOrderStatuses.clear();
			}
			if (custorderNumber != null && custorderNumber.equals(0l)) {
				custorderNumber = null;
			}
			
			if (orderNumber != null && orderNumber.equals(0l)) {
				orderNumber = null;
			}
						
			formAction = null;
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
     * Perform form-specific validation of input parameters.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		// [[
		ActionErrors result = null;
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {
			
	        ActionErrors errors = super.validate(mapping, request);
			
	        /* no need to validate if 'delete' button was pressed */
	        if (request.getParameter("delete") != null) {
	            return result = null;
	        }
	        if (errors == null) {
	            errors = new ActionErrors();
	        }
			
	        return result = errors;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

	public List<PoolTxEntity> getPools() {
		return pools;
	}

	public void setPools(List<PoolTxEntity> pools) {
		this.pools = pools;
	}

	public List<DispoCarrierWrapper> getCarriers() {
		return carriers;
	}

	public void setCarriers(List<DispoCarrierWrapper> carriers) {
		this.carriers = carriers;
	}

	public Set<Long> getSelectedCarriers() {
		return selectedCarriers;
	}
	
	public String getSelectedCarriersString(){
		if (selectedCarriers.size() == 0){
			return "none";
		}
		else if (selectedCarriers.size() == carriers.size()){
			return "all";
		}
		else {
			StringBuilder res = new StringBuilder();
			for (DispoCarrierWrapper carrier : carriers) {
				if (selectedCarriers.contains(carrier.getCarrierId())){
					res.append(carrier.getCarrierName()).append(" ");
				}
			}
			return res.toString();
		}
	}

	public void setSelectedCarriers(Set<Long> selectedCarriers) {
		this.selectedCarriers = selectedCarriers;
	}
	
	public int getNumberOfSelectedCarriers(){
		return this.selectedCarriers != null ? this.selectedCarriers.size() : 0;
	}

	public boolean getSelectedCarriersCheckbox(int index){
		if (carriers != null){
			DispoCarrierWrapper carrier = carriers.get(index);
			return selectedCarriers.contains(carrier.getCarrierId());
		}
		else {
			return false;
		}
	}

	public void setSelectedCarriersCheckbox(int index, boolean value){
		if (carriers != null){
			DispoCarrierWrapper carrier = carriers.get(index);
			selectedCarriers.add(carrier.getCarrierId());
		}
	}
	
	/**
	 * Initializes the selected carriers cache for the selected pool.
	 * This method is called when the screen is accessed on the first time.
	 * 
	 * If the selectedCarriersCache is not empty (was initialized before), 
	 * it calls the restoreSelectedCarriersForSelectedPool() to restore prevously set carriers.
	 */
	public void initSelectedCarriersCache() {
		if (selectedCarriersCache.isEmpty()) {
			selectedCarriersCache.put(selectedPoolIndex, selectedCarriers);
		}
		else {
			restoreSelectedCarriersForSelectedPool();
		}
	}
	
	/**
	 * This method is called after selecting a new pool. 
	 * Restores the selected carriers for the newly selected pool if was any.
	 */
	public void restoreSelectedCarriersForSelectedPool() {
		Set<Long> nsc = selectedCarriersCache.get(selectedPoolIndex);
		if (nsc == null) {
			nsc = new HashSet<Long>();
			selectedCarriersCache.put(selectedPoolIndex, nsc);
			if ((carriers != null) && (carriers.size() < 3)) {
				Iterator<DispoCarrierWrapper> i = carriers.iterator();
				while (i.hasNext()) {
					final DispoCarrierWrapper carrier = i.next();
					nsc.add(carrier.getCarrierId());
				}
			}
		}
		selectedCarriers = nsc;
	}

	public int getSelectedPool() {
		return selectedPoolIndex;
	}

	public void setSelectedPool(int selectedPool) {
		this.selectedPoolIndex = selectedPool;
	}
	
	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getHourSpan() {
		return hourSpan;
	}

	public void setHourSpan(int hourSpan) {
		this.hourSpan = hourSpan;
	}
	
	public float getHeightFactorReal() {
		return heightFactor/2;
	}

	public void setHeightFactorReal(float heightFactor) {
		//dummy implementation
	}

	
	public float getHeightFactorPlanning() {
		return heightFactor/2;
	}

	public void setHeightFactorPlanning(float heightFactor) {
		//dummy implementation
	}

	public void setHeightFactor(float heightFactor) {
		this.heightFactor = heightFactor;
	}

	public List<PoolTxEntity> getAllPools() {
		return allPools;
	}

	public void setAllPools(List<PoolTxEntity> allPools) {
		this.allPools = allPools;
	}

	public int getMaxNrOfSelectableAssignments() {
		return maxNrOfSelectableAssignments;
	}

	public void setMaxNrOfSelectableAssignments(int maxNrOfSelectableAssignments) {
		this.maxNrOfSelectableAssignments = maxNrOfSelectableAssignments;
	}

	public boolean isShowOnlyTrucksWithOrders() {
		return showOnlyTrucksWithOrders;
	}

	public void setShowOnlyTrucksWithOrders(boolean showOnlyTrucksWithOrders) {
		this.showOnlyTrucksWithOrders = showOnlyTrucksWithOrders;
	}

	public List<DropdownEntity> getDeliveringPlants() {
		return deliveringPlants;
	}

	public void setDeliveringPlants(List<DropdownEntity> deliveringPlants) {
		this.deliveringPlants = deliveringPlants;
	}

	/**
	 * Returns the set of selected plant codes in the plant dropdown.
	 * @return A set of strings, each string is a delivery plant code. The content of the set
	 * is equal to the set of selected plants in the plants dropdown.
	 */
	public Set<String> getSelectedDeliveringPlants() {
		return selectedDeliveringPlants;
	}

	public String getSelectedDeliveringPlantsString() {
		if (selectedDeliveringPlants.size() == 0) {
			return "none";
		}
		else if (deliveringPlants.size() == selectedDeliveringPlants.size()) {
			return "all";
		}
		else {
			StringBuilder res = new StringBuilder();

			for (DropdownEntity deliveringPlant: deliveringPlants) {
				if (selectedDeliveringPlants.contains(deliveringPlant.getValue())) {					
					res.append(deliveringPlant.getName()).append(" ");
				}
			}

			return res.toString();
		}
	}

	public void setSelectedDeliveringPlants(Set<String> selectedDeliveringPlants) {
		this.selectedDeliveringPlants = selectedDeliveringPlants;
	}

	public int getNumberOfSelectedDeliveringPlants() {
		return this.selectedDeliveringPlants != null ? this.selectedDeliveringPlants.size() : 0;
	}

	public boolean getSelectedDeliveringPlantsCheckbox(int index) {
		if (deliveringPlants != null){
			DropdownEntity deliveringPlant = deliveringPlants.get(index);
			return selectedDeliveringPlants.contains(deliveringPlant.getValue());
		}
		else {
			return false;
		}
	}

	public void setSelectedDeliveringPlantsCheckbox(int index, boolean value) {
		if (deliveringPlants != null) {
			DropdownEntity deliveringPlant = deliveringPlants.get(index);
			selectedDeliveringPlants.add(deliveringPlant.getValue());
		}
	}

	public boolean isDeliveringPlantsSelectionEmpty() {
		return (selectedDeliveringPlants.size() == 0
				|| deliveringPlants.size() == selectedDeliveringPlants.size());
	}

	public Long getSelectedPoolEntityId() {
		return pools.get(selectedPoolIndex).getPoolTxPK().getPoolId();
	}

	public List<OrderStatusTxEntity> getOrderStatusList() {
		return orderStatusList;
	}

	public void setOrderStatusList(List<OrderStatusTxEntity> orderStatusList) {
		this.orderStatusList = orderStatusList;
	}

	public Set<Long> getSelectedOrderStatuses() {
		return selectedOrderStatuses;
	}
	
	public String getSelectedOrderStatusesString() {
		if (selectedOrderStatuses.isEmpty()) {
			return "none";
		}
		else if (orderStatusList.size() == selectedOrderStatuses.size()) {
			return "all";
		}
		else {
			StringBuilder res = new StringBuilder();

			for (OrderStatusTxEntity orderStatusTxEntity : orderStatusList) {
				Long orderStatusCd = orderStatusTxEntity.getOrderStatusTxPK().getOrderStatusBs().getOrderStatusCd();
				if (selectedOrderStatuses.contains(orderStatusCd)) {
					res.append(orderStatusTxEntity.getTxt()).append(" ");
				}
			}

			return res.toString();
		}
	}

	public void setSelectedOrderStatuses(Set<Long> selectedOrderStatuses) {
		this.selectedOrderStatuses = selectedOrderStatuses;
	}

	public int getNumberOfSelectedOrderStatuses() {
		return this.selectedOrderStatuses != null ? this.selectedOrderStatuses.size() : 0;
	}

	public boolean getSelectedOrderStatusesCheckbox(int index) {
		if (orderStatusList != null){
			OrderStatusTxEntity orderStatusTxEntity = orderStatusList.get(index);
			Long orderStatusCd = orderStatusTxEntity.getOrderStatusTxPK().getOrderStatusBs().getOrderStatusCd();
			return selectedOrderStatuses.contains(orderStatusCd);
		}
		else {
			return false;
		}
	}

	public void setSelectedOrderStatusesCheckbox(int index, boolean value) {
		if (orderStatusList != null) {
			OrderStatusTxEntity orderStatusTxEntity = orderStatusList.get(index);
			Long orderStatusCd = orderStatusTxEntity.getOrderStatusTxPK().getOrderStatusBs().getOrderStatusCd();
			selectedOrderStatuses.add(orderStatusCd);
		}
	}

	public boolean isOrderStatusesSelectionEmpty() {
		return (selectedOrderStatuses.isEmpty()
				|| orderStatusList.size() == selectedOrderStatuses.size());
	}

	public Long getCustorderNumber() {
		return custorderNumber;
	}

	public void setCustorderNumber(Long custorderNumber) {
		this.custorderNumber = custorderNumber;
	}
	
	/**
	 * @return the orderNumber
	 */
	public Long getOrderNumber() {
		return orderNumber;
	}

	/**
	 * @param orderNumber the orderNumber to set
	 */
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public boolean isHideTrucksWithoutDeliveries() {
		return hideTrucksWithoutDeliveries;
	}

	public void setHideTrucksWithoutDeliveries(boolean hideTrucksWithoutDeliveries) {
		this.hideTrucksWithoutDeliveries = hideTrucksWithoutDeliveries;
	}

	public boolean isHideScheduledCustorders() {
		return hideScheduledCustorders;
	}

	public void setHideScheduledCustorders(boolean hideScheduledCustorders) {
		this.hideScheduledCustorders = hideScheduledCustorders;
	}

	public boolean isHideClosedCustorders() {
		return hideClosedCustorders;
	}

	public void setHideClosedCustorders(boolean hideClosedCustorders) {
		this.hideClosedCustorders = hideClosedCustorders;
	}

	public boolean isHidePlanningTrucks() {
		return hidePlanningTrucks;
	}

	public void setHidePlanningTrucks(boolean hidePlanningTrucks) {
		this.hidePlanningTrucks = hidePlanningTrucks;
	}

	public boolean isHideRealTrucks() {
		return hideRealTrucks;
	}

	public void setHideRealTrucks(boolean hideRealTrucks) {
		this.hideRealTrucks = hideRealTrucks;
	}

	public boolean isHideCustorderHeaders() {
		return hideCustorderHeaders;
	}

	public void setHideCustorderHeaders(boolean hideCustorderHeaders) {
		this.hideCustorderHeaders = hideCustorderHeaders;
	}

	public int getSelectedFilterForVisual() {
		return selectedFilterForVisual;
	}

	public void setSelectedFilterForVisual(int selectedFilterForVisual) {
		this.selectedFilterForVisual = selectedFilterForVisual;
	}

	public boolean isHideInactiveTrucks() {
		return hideInactiveTrucks;
	}

	public void setHideInactiveTrucks(boolean hideInactiveTrucks) {
		this.hideInactiveTrucks = hideInactiveTrucks;
	}

	public int getTimegrid() {
		return timegrid;
	}

	public void setTimegrid(int timegrid) {
		this.timegrid = timegrid;
	}

	public boolean isApplyUserDefaultSettings() {
		return applyUserDefaultSettings;
	}

	public void setApplyUserDefaultSettings(boolean applyUserDefaultSettings) {
		this.applyUserDefaultSettings = applyUserDefaultSettings;
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	public long getCurrentMillisInUserTimeZone() {
		return userTimeZone.shiftToUTC(new Date()).getTime();
	}
	
    public Long getTruckPriority() {
		return truckPriority;
	}

	public void setTruckPriority(String truckPriority) {
		if (StringUtils.empty(truckPriority)) {
			this.truckPriority = null;
		}
		else {
			this.truckPriority = Long.valueOf(truckPriority);
		}
	}

	public boolean isHideTrucksAndOrderOfPartners() {
		return hideTrucksAndOrderOfPartners;
	}

	public void setHideTrucksAndOrderOfPartners(boolean hideExecutingOrdersAndTrucks) {
		this.hideTrucksAndOrderOfPartners = hideExecutingOrdersAndTrucks;
	}
}
