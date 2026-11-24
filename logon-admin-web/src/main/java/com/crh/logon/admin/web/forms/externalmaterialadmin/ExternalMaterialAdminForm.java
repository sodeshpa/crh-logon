/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.externalmaterialadmin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * 
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_ExternalMaterialAdminForm"
 */
public class ExternalMaterialAdminForm extends ActionForm {



	public static final String formName = "admin_ExternalMaterialAdminForm";
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ExternalMaterialAdminForm.class);

	private String inputAction;
	private Map carrierMap;
	private Long carrierId;
	private String carrierName;
	private String carrierStreet;
	private String carrierCity;
	private Map materialMap;
	private Long materialId;
	private String materialName;
	private String inputComment;
	private boolean inputActive;
	
	public void setInputActive(boolean inputActive) {
		this.inputActive = inputActive;
	}

	public boolean isInputActive() {
		return inputActive;
	}

	public String getInputAction() {
		return inputAction;
	}

	public void setInputAction(String inputAction) {
		this.inputAction = inputAction;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getInputComment() {
		return inputComment;
	}

	public void setInputComment(String inputComment) {
		this.inputComment = inputComment;
	}

	public Map getMaterialMap() {
		return materialMap;
	}

	public void setMaterialMap(Map materialMap) {
		this.materialMap = materialMap;
	}

	public Long getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Long materialId) {
		this.materialId = materialId;
	}

	
	
	public Map getCarrierMap() {
		return carrierMap;
	}

	public void setCarrierMap(Map carrierMap) {
		this.carrierMap = carrierMap;
	}
	
	public void setCarrierId(Long carrierId) {
		this.carrierId = carrierId;
	}

	public Long getCarrierId() {
		return carrierId;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getCarrierName() {
		return carrierName;
	}
	
	public String getCarrierStreet() {
		return carrierStreet;
	}

	public void setCarrierStreet(String carrierStreet) {
		this.carrierStreet = carrierStreet;
	}

	public String getCarrierCity() {
		return carrierCity;
	}

	public void setCarrierCity(String carrierCity) {
		this.carrierCity = carrierCity;
	}

	/**
	 * Default constructor.
	 */
	public ExternalMaterialAdminForm() {
		
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ExternalMaterialAdminForm");
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
			
			inputActive = true;
			carrierMap = null;
			carrierId = null;
			carrierName = null;
			carrierCity = null;
			carrierStreet = null;
			materialMap = null;
			materialId = null;
			materialName = null;
			inputComment = null;
			
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
     * Note: is called on every request before the struts action
     * method gets control and, more importantly, before the request parameters
     * are automatically copied into the form. The primary use is to reset
     * checkboxes and struts multiboxes and the like for which the browser only
     * sends the form parameters when they are set.
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		inputActive = false;
	}
	
	public void resetFields() {
		inputActive = true;
		inputAction = null;
		materialId = null;
		materialName = null;
		inputComment = null;
	}



	
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



}
