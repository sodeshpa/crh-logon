/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.dispo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.web.utils.DropdownEntity;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_dispo_EditHomePlantForm"
 */
public class EditHomePlantForm extends ActionForm {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EditHomePlantForm.class);
	
	/* lists & dropdowns */
	/** The list of shippingpoints which belongs to the selected plant */
	private List<ShippingpointEntity> shippingpoints;
	/** The list of plants */
	private List<DropdownEntity> plants;
	
	/* user data */
	private Long selectedShippingpoint;
	private String selectedPlant;
	
	private TruckEntity truck;
	
	/** The list of shippingpoints for all the plants */
	private List<ShippingpointEntity> allShippingpoints;

	public EditHomePlantForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "EditHomePlantForm");
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
	 * fields on the form.
	 */
	public void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			shippingpoints = null;
			plants = null;
			selectedShippingpoint = null;
			selectedPlant = null;
			truck = null;
			allShippingpoints = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public void resetShippingpiointFields() {
		shippingpoints = null;
		selectedShippingpoint = null;
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

	public List<ShippingpointEntity> getShippingpoints() {
		return shippingpoints;
	}

	public void setShippingpoints(List<ShippingpointEntity> shippingpoints) {
		this.shippingpoints = shippingpoints;
	}

	public List<DropdownEntity> getPlants() {
		return plants;
	}

	public void setPlants(List<DropdownEntity> plants) {
		this.plants = plants;
	}

	public Long getSelectedShippingpoint() {
		return selectedShippingpoint;
	}

	public void setSelectedShippingpoint(Long selectedShippingpoint) {
		this.selectedShippingpoint = selectedShippingpoint;
	}

	public String getSelectedPlant() {
		return selectedPlant;
	}

	public void setSelectedPlant(String selectedPlant) {
		this.selectedPlant = selectedPlant;
	}

	public TruckEntity getTruck() {
		return truck;
	}

	public void setTruck(TruckEntity truck) {
		this.truck = truck;
	}

	public List<ShippingpointEntity> getAllShippingpoints() {
		return allShippingpoints;
	}

	public void setAllShippingpoints(List<ShippingpointEntity> allShippingpoints) {
		this.allShippingpoints = allShippingpoints;
	}
}
