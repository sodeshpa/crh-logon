/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.carrierassignment;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.views.PoolShiptoMatSoldtoEntity;

/**
 * @author AdNovum Informatik AG
 * 
 * @struts.form name="admin_CarrierAssignmentForm" 
  */
public class CarrierAssignmentForm extends ActionForm {

	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(CarrierAssignmentForm.class);
	    
	private List poolShiptoMaterialList;
	
	// ID for all the pools which belong to the poolmanager
	private List userPoolIdList;
	
	// values for the filtering dropdowns, key = id, value = display name
	private Map poolMap;
	private Map soldtoMap;
	private Map shiptoMap;
	private Map productMap;
	
	private String selectedPoolFilter;
	private String selectedSoldtoFilter;
	private String selectedShiptoFilter;
	private String selectedProductFilter;

	// map of poolId -> ArrayList( poolCarrierCarrierEntities belonging to the given poolId )
	//private List poolCarrierList;
	private Map PoolCarrierMap;
	
	private String formAction;
	
	// maps to hold the original values from DB -> used at 'save' to decide which entries are changed
	// key = compositeId of PoolShiptoMatSoldtoEntity
	// value = original value
	private Map originalSelectedCarrierMap;
	private Map originalCheckBoxValueMap;
	
	 /**
     * Default constructor.
     */
    public CarrierAssignmentForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "CarrierAssignmentForm");
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
     * fields on the ordermgr.web form.
     */
    private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			poolShiptoMaterialList = null;
			poolMap = null;
			soldtoMap = null;
			shiptoMap = null;
			productMap = null;
			selectedPoolFilter = null;
			selectedSoldtoFilter = null;
			selectedShiptoFilter = null;
			selectedProductFilter = null;
			PoolCarrierMap = null;
			userPoolIdList = null;
			formAction = null;
			originalSelectedCarrierMap = null;
			originalCheckBoxValueMap = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    public void clean() {
    	init();
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
			if (poolShiptoMaterialList == null) {
				return;
			}
			ListIterator it = poolShiptoMaterialList.listIterator();
			
			while (it.hasNext()) {
				PoolShiptoMatSoldtoEntity entity = (PoolShiptoMatSoldtoEntity) it.next();
				entity.setAutoCarrierAssignmentFlag(new Boolean(false));
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

	public List getPoolShiptoMaterialList() {
		return poolShiptoMaterialList;
	}

	public void setPoolShiptoMaterialList(List poolShiptoMaterialList) {
		this.poolShiptoMaterialList = poolShiptoMaterialList;
	}

	public String getSelectedPoolFilter() {
		return selectedPoolFilter;
	}

	public void setSelectedPoolFilter(String selectedPoolFilter) {
		this.selectedPoolFilter = selectedPoolFilter;
	}

	public String getSelectedSoldtoFilter() {
		return selectedSoldtoFilter;
	}

	public void setSelectedSoldtoFilter(String selectedSoldtoFilter) {
		this.selectedSoldtoFilter = selectedSoldtoFilter;
	}

	public String getSelectedShiptoFilter() {
		return selectedShiptoFilter;
	}

	public void setSelectedShiptoFilter(String selectedShiptoFilter) {
		this.selectedShiptoFilter = selectedShiptoFilter;
	}

	public String getSelectedProductFilter() {
		return selectedProductFilter;
	}

	public void setSelectedProductFilter(String selectedProductFilter) {
		this.selectedProductFilter = selectedProductFilter;
	}

	public Map getPoolMap() {
		return poolMap;
	}

	public void setPoolMap(Map poolMap) {
		this.poolMap = poolMap;
	}

	public Map getSoldtoMap() {
		return soldtoMap;
	}

	public void setSoldtoMap(Map soldtoMap) {
		this.soldtoMap = soldtoMap;
	}

	public Map getShiptoMap() {
		return shiptoMap;
	}

	public void setShiptoMap(Map shiptoMap) {
		this.shiptoMap = shiptoMap;
	}

	public Map getProductMap() {
		return productMap;
	}

	public void setProductMap(Map productMap) {
		this.productMap = productMap;
	}

	public List getUserPoolIdList() {
		return userPoolIdList;
	}

	public void setUserPoolIdList(List userPoolIdList) {
		this.userPoolIdList = userPoolIdList;
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	public Map getOriginalSelectedCarrierMap() {
		return originalSelectedCarrierMap;
	}

	public void setOriginalSelectedCarrierMap(Map originalSelectedCarrierMap) {
		this.originalSelectedCarrierMap = originalSelectedCarrierMap;
	}

	public Map getOriginalCheckBoxValueMap() {
		return originalCheckBoxValueMap;
	}

	public void setOriginalCheckBoxValueMap(Map originalCheckBoxValueMap) {
		this.originalCheckBoxValueMap = originalCheckBoxValueMap;
	}

	public Map getPoolCarrierMap() {
		return PoolCarrierMap;
	}

	public void setPoolCarrierMap(Map poolCarrierMap) {
		PoolCarrierMap = poolCarrierMap;
	}
	
	
}
