/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.truckadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.admin.web.beans.ForwardingAgentBean;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Form of page LKWStammDatenTile,jsp.
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_PoolAdminForm"
 */
public class PoolAdminForm extends ActionForm {
	
	private String inputAction;
	private List poolList;
	private List sapVendorList;
	private Map fwasToStore;
	private Map fwaMap;
	private ForwardingAgentBean currentEditedForwardingAgentBean;
	
	private static final long serialVersionUID = -8910104051337644556L;

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(PoolAdminForm.class);

    /**
     * Default constructor.
     */
    public PoolAdminForm() {
		
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "PoolAdminForm");
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


	public String getInputAction() {
		return inputAction;
	}



	public void setInputAction(String inputAction) {
		this.inputAction = inputAction;
	}


	public List getPoolList() {
		return poolList;
	}



	public void setPoolList(List poolList) {
		this.poolList = poolList;
	}

    public List getSapVendorList() {
		return sapVendorList;
	}


	public void setSapVendorList(List sapVendorList) {
		this.sapVendorList = sapVendorList;
	}

    public Map getFwasToStore() {
		return fwasToStore;
	}


	public void setFwasToStore(Map fwasToStore) {
		this.fwasToStore = fwasToStore;
	}
	
	public ForwardingAgentBean getCurrentEditedForwardingAgentBean() {
		return currentEditedForwardingAgentBean;
	}


	public void setCurrentEditedForwardingAgentBean(ForwardingAgentBean currentEditedForwardingAgentBean) {
		this.currentEditedForwardingAgentBean = currentEditedForwardingAgentBean;
	}
	
	public Map getFwaMap() {
		return fwaMap;
	}


	public void setFwaMap(Map fwaMap) {
		this.fwaMap = fwaMap;
	}


	public ForwardingAgentBean getFwaIfAlreadyEdited(String key) {
		if(fwasToStore == null || fwasToStore.isEmpty()) return null;
		ForwardingAgentBean fwa = (ForwardingAgentBean) fwasToStore.get(key);
		return fwa;
	}
	
	public ForwardingAgentBean getFwaMapEntry(String key) {

		Long keyLong = Long.parseLong(key);
		
		return (ForwardingAgentBean) fwaMap.get(keyLong);
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
			this.inputAction = "";
			this.poolList = new ArrayList();
			this.sapVendorList = new ArrayList();
			this.fwaMap = new HashMap();
			this.fwasToStore = new HashMap();
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
     * 
     * Do not reset the flags, which are not sent in case they are not checked, and so they do not change their values.
     * This calls must only be called if the whole form is submitted. Otherwise they should not be reseted, since they may be
     * true, but not sent by AJAX call. 
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
			for (Iterator iterator = this.fwaMap.keySet().iterator(); iterator.hasNext();) {
				Long key = (Long) iterator.next();
				
				ForwardingAgentBean fwa = (ForwardingAgentBean) this.fwaMap.get(key);
				
				fwa.resetSelectedPools();
				fwa.setEdited(false);
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
    public void clear(){
    	init();
    }

}
