/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.containermonitoring;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.ContainerMonitoringWrapper;
import com.crh.logon.order.web.actions.containermonitoring.ContainerMonitoringAction;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.utils.StringUtils;


/**
 * @author AdNovum Informatik AG
 * 
 * @struts.form name="order_ContainerMonitoringForm"
 */
public class ContainerMonitoringForm extends ActionForm{

	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ContainerMonitoringForm.class);

    public static final String SESSION_FORM_NAME = "order_ContainerMonitoringForm";
    
    private List<PlantBsEntity> productionPlants;
    private List<ShippingpointEntity> shippingPoints;
    
    private String selectedProductionPlantCd;
    private String selectedSapMatId;
    private Set<Long> selectedShippingPoints;
    
    private boolean sortAsc;
    private String sortByColumn;
        
    private List<ContainerMonitoringWrapper> resultList;
    
    
    public ContainerMonitoringForm(){
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ContainerMonitoringForm");
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
    
    public void init(){
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {			
			selectedShippingPoints = new HashSet<Long>();
			selectedProductionPlantCd = null;
			selectedSapMatId = null;
			productionPlants = new ArrayList<PlantBsEntity>();
			shippingPoints = null;
			resultList = null;
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
			String actionParameter = request.getParameter(ContainerMonitoringAction.ACTION);
			if (ContainerMonitoringAction.AJAX_ACTION_SET_SHIPPINGPOINTS.equals(actionParameter)){
				selectedShippingPoints.clear();
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
    
	/**
	 * @return the sortAsc
	 */
	public boolean isSortAsc() {
		return sortAsc;
	}

	/**
	 * @param sortAsc the sortAsc to set
	 */
	public void setSortAsc(boolean sortAsc) {
		this.sortAsc = sortAsc;
	}

	/**
	 * @return the sortByColumn
	 */
	public String getSortByColumn() {
		return sortByColumn;
	}

	/**
	 * @param sortByColumn the sortByColumn to set
	 */
	public void setSortByColumn(String sortByColumn) {
		this.sortByColumn = sortByColumn;
	}

	public List<Long> getSelectedShippingPointIdList() {
		return new ArrayList<Long>(getSelectedShippingPoints());
	}	

	public String getSelectedShippingPointsString(){
		if (selectedShippingPoints.size() == 0){
			return "none";
		}
		else if (selectedShippingPoints.size() == shippingPoints.size()){
			return "all";
		}
		else {
			
			StringBuilder sb = new StringBuilder();
			for (ShippingpointEntity e : shippingPoints){
				if (selectedShippingPoints.contains(e.getShippingpointId())){
					sb.append(e.getShippingpointName());
					sb.append(" ");
				}
			}
			return sb.toString();
		}
	}	
	
	public boolean getSelectedShippingPointCheckbox(int index){
		if (shippingPoints != null){
			ShippingpointEntity sp = (ShippingpointEntity)shippingPoints.get(index);
			return selectedShippingPoints.contains(sp.getShippingpointId());
		}
		else {
			return false;
		}
	}
    
	public void setSelectedShippingPointCheckbox(int index, boolean value){
		if (shippingPoints != null){
			ShippingpointEntity sp = (ShippingpointEntity)shippingPoints.get(index);
			if(value){
				selectedShippingPoints.add(sp.getShippingpointId());				
			} else{
				selectedShippingPoints.remove(sp.getShippingpointId());				
			}
		}
	}	
    
	/**
	 * @return the productionPlants
	 */
	public List<PlantBsEntity> getProductionPlants() {
		return productionPlants;
	}

	/**
	 * @param productionPlants the productionPlants to set
	 */
	public void setProductionPlants(List<PlantBsEntity> productionPlants) {
		this.productionPlants = productionPlants;
	}

	/**
	 * @return the selectedProductionPlantCd
	 */
	public String getSelectedProductionPlantCd() {
		return selectedProductionPlantCd;
	}

	/**
	 * @param selectedProductionPlantCd the selectedProductionPlantCd to set
	 */
	public void setSelectedProductionPlantCd(String selectedProductionPlantCd) {
		this.selectedProductionPlantCd = selectedProductionPlantCd;
	}

	/**
	 * @return the selectedSapMatId
	 */
	public String getSelectedSapMatId() {
		if ( !StringUtils.empty(selectedSapMatId) ) {			
			return selectedSapMatId;
		}
		else { 
			return null;
		}
	}

	/**
	 * @param selectedSapMatId the selectedSapMatId to set
	 */
	public void setSelectedSapMatId(String selectedSapMatId) {
		this.selectedSapMatId = selectedSapMatId;
	}

	/**
	 * @return the resultList
	 */
	public List<ContainerMonitoringWrapper> getResultList() {
		return resultList;
	}

	/**
	 * @param resultList the resultList to set
	 */
	public void setResultList(List<ContainerMonitoringWrapper> resultList) {
		this.resultList = resultList;
	}

	/**
	 * @return the shippingPoints
	 */
	public List<ShippingpointEntity> getShippingPoints() {
		return shippingPoints;
	}

	/**
	 * @param shippingPoints the shippingPoints to set
	 */
	public void setShippingPoints(List<ShippingpointEntity> shippingPoints) {
		this.shippingPoints = shippingPoints;
	}

	/**
	 * @return the selectedShippingpoints
	 */
	public Set<Long> getSelectedShippingPoints() {
		return selectedShippingPoints;
	}

	/**
	 * @param selectedShippingpoints the selectedShippingpoints to set
	 */
	public void setSelectedShippingPoints(Set<Long> selectedShippingpoints) {
		this.selectedShippingPoints = selectedShippingpoints;
	}
    
    
}
