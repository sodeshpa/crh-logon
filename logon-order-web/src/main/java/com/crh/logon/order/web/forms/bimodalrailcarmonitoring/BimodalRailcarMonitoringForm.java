/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.bimodalrailcarmonitoring;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.BimodalRailOrderWrapper;
import com.crh.logon.order.web.actions.bimodalrailcarmonitoring.BimodalRailcarMonitoringAction;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.views.BimodalRailOrderEntity;
import com.crh.logon.persistence.entities.views.FvlSapBpEntity;
import com.crh.logon.utils.StringUtils;

/**
 * Form belonging to Order Monitoring/Monitoring/Bimodal Monitoring.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_BimodalRailcarMonitoringForm"
 */
public class BimodalRailcarMonitoringForm extends ActionForm {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BimodalRailcarMonitoringForm.class);
 
    public static final String SESSION_FORM_NAME = "order_BimodalRailcarMonitoringForm";
    
    private List<PlantBsEntity> productionPlants;
    private List<FvlSapBpEntity> shiptos;
    private String companyCodeListJsOn;
    
    private String selectedCompanyCode;
    
    private Set<Long> selectedShiptos;
    
    private String selectedProductionPlantCd;
    private String selectedSapMatId;
    
    private List<BimodalRailOrderWrapper> resultList;
    
    private boolean sortAsc;
    private String sortByColumn;
    
	/**
	 * Default constructor.
	 */
	public BimodalRailcarMonitoringForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "BimodalRailcarMonitoringForm");
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
			selectedShiptos = new HashSet<Long>();
			selectedProductionPlantCd = null;
			selectedCompanyCode = null;
			selectedSapMatId = null;
			productionPlants = new ArrayList<PlantBsEntity>();
			companyCodeListJsOn = null;
			shiptos = null;
			resultList = null;
			sortAsc = true;
			sortByColumn = BimodalRailOrderEntity.ATTR_ORDER_STATUS_CD;
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
			if (request.getParameter(BimodalRailcarMonitoringAction.AJAX_ACTION_SET_SHIPTOS) != null){
				selectedShiptos.clear();
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

	public boolean getSelectedShiptoCheckbox(int index){
		if (shiptos != null){
			FvlSapBpEntity sp = (FvlSapBpEntity)shiptos.get(index);
			return selectedShiptos.contains(sp.getShiptoSapBpId());
		}
		else {
			return false;
		}
	}
    
	public void setSelectedShiptoCheckbox(int index, boolean value){
		if (shiptos != null){
			FvlSapBpEntity sp = (FvlSapBpEntity)shiptos.get(index);
			selectedShiptos.add(sp.getShiptoSapBpId());
		}
	}	
	
	public String getSelectedShiptosString(){
		if (selectedShiptos.size() == 0){
			return "none";
		}
		else if (selectedShiptos.size() == shiptos.size()){
			return "all";
		}
		else {
			Iterator i = shiptos.iterator();
			String res = "";
			while (i.hasNext()){
				FvlSapBpEntity rps = (FvlSapBpEntity)i.next();
				if (selectedShiptos.contains(rps.getShiptoSapBpId())){
					res += rps.getName1() + " ";
				}
			}
			return res;
		}
	}	
	

	public List getShiptos() {
		return shiptos;
	}

	public void setShiptos(List <FvlSapBpEntity>shiptos) {
		this.shiptos = shiptos;
	}

	public String getSelectedCompanyCode() {
		if ( !StringUtils.empty(selectedCompanyCode) ) {			
			return selectedCompanyCode;
		}
		else { 
			return null;
		}
	}
	
	public void setSelectedCompanyCode(String selectedCompanyCode) {
		this.selectedCompanyCode = selectedCompanyCode;
	}

	public Set<Long> getSelectedShiptos() {
		return selectedShiptos;
	}

	public void setSelectedShiptos(Set<Long> selectedShiptos) {
		this.selectedShiptos = selectedShiptos;
	}
	
	public List getSelectedShiptoIdList() {
		Set shiptoSapBpsSet = getSelectedShiptos();
		List shiptoSapBps = new ArrayList();
		shiptoSapBps.addAll(shiptoSapBpsSet);
		return shiptoSapBps;
	}

	public String getSelectedProductionPlantCd() {
		if ( !StringUtils.empty(selectedProductionPlantCd) ) {			
			return selectedProductionPlantCd;
		}
		else { 
			return null;
		}
	}

	public void setSelectedProductionPlantCd(String selectedProductionPlantCd) {
		this.selectedProductionPlantCd = selectedProductionPlantCd;
	}


	public List<PlantBsEntity> getProductionPlants() {
		return productionPlants;
	}

	public void setProductionPlants(List<PlantBsEntity> productionPlants) {
		this.productionPlants = productionPlants;
	}

	public String getCompanyCodeListJsOn() {
		return companyCodeListJsOn;
	}

	public void setCompanyCodeListJsOn(String companyCodeListJsOn) {
		this.companyCodeListJsOn = companyCodeListJsOn;
	}

	public List<BimodalRailOrderWrapper> getResultList() {
		return resultList;
	}

	public void setResultList(List<BimodalRailOrderWrapper> resultList) {
		this.resultList = resultList;
	}

	public String getSelectedSapMatId() {
		if ( !StringUtils.empty(selectedSapMatId) ) {			
			return selectedSapMatId;
		}
		else { 
			return null;
		}
	}

	public void setSelectedSapMatId(String selectedSapMatId) {
		this.selectedSapMatId = selectedSapMatId;
	}

	public boolean isSortAsc() {
		return sortAsc;
	}

	public void setSortAsc(boolean sortAsc) {
		this.sortAsc = sortAsc;
	}

	public String getSortByColumn() {
		return sortByColumn;
	}

	public void setSortByColumn(String sortByColumn) {
		this.sortByColumn = sortByColumn;
	}

}

