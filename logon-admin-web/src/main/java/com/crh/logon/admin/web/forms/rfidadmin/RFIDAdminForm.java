/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.rfidadmin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.RfidtagEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;

/**
 * Form of page BaustellensiloTile,jsp.
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_RFIDAdminForm" 
 */
public class RFIDAdminForm extends ActionForm {
	
	
	private RfidobjectTypeBsEntity currentRefid;
	private List rfidList;
	private Map rfidObjectTypeMap;
	private String inputAction;
	//search mask
	private String rfidObjectTypeFilter;
	private String rfidIdentityFilter;
	private String nameFilter;
	private String objectLabelFilter;
	private boolean activeFilter;
	//details (add/edit)
	private String rfidIdentity;
	private String name;
	private boolean active;

	private RfidtagEntity rfidTag;
	
	private String rfidtagId;
	
	private static final long serialVersionUID = -8910104051337644556L;

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(RFIDAdminForm.class);

    /**
     * Default constructor.
     */
    public RFIDAdminForm() {
		
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "RFIDAdminForm");
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

    public void clear(){
    	init();
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
				rfidIdentity ="";
				name = "";
				active = false;
				currentRefid = new RfidobjectTypeBsEntity();
				rfidList = new ArrayList();
				rfidTag = null;
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
		// [[
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "reset", params);
		}
		// ]]
		try {
			activeFilter = false;
			active = false;
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
			
			if (request.getParameter("save") != null) {
				
				if (rfidIdentity.length() > RfidtagEntity.DBSIZE_RFID_IDENTITY) {
					errors.add(ActionMessages.GLOBAL_MESSAGE,
							new ActionMessage("rfidadmin.errors.fieldInputTooLong", new String [] {"Rfid Identity.", Long.toString(RfidtagEntity.DBSIZE_RFID_IDENTITY)}));
				}
				if (name.length() > RfidtagEntity.DBSIZE_NAME) {
					errors.add(ActionMessages.GLOBAL_MESSAGE,
							new ActionMessage("rfidadmin.errors.fieldInputTooLong", new String [] {"Name.", Long.toString(RfidtagEntity.DBSIZE_NAME)}));
				}
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


	public RfidobjectTypeBsEntity getCurrentRefid() {
		return currentRefid;
	}


	public void setCurrentRefid(RfidobjectTypeBsEntity currentRefid) {
		this.currentRefid = currentRefid;
	}


	public List getRfidList() {
		return rfidList;
	}


	public void setRfidList(List rfidList) {
		this.rfidList = rfidList;
	}

	public Map getRfidObjectTypeMap() {
		return rfidObjectTypeMap;
	}


	public void setRfidObjectTypeMap(Map rfidObjectTypeMap) {
		this.rfidObjectTypeMap = rfidObjectTypeMap;
	}



	public String getInputAction() {
		return inputAction;
	}


	public void setInputAction(String inputAction) {
		this.inputAction = inputAction;
	}
	
	public String getRfidObjectTypeMapEntry(String key) {
		// [[
		String result = null;
		Object[][] params = {{"key", key}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getRfidObjectTypeMapEntry", params);
		}
		// ]]
		try {
			Long keyLong = Long.valueOf(key);
			
			String typeStr = (String) rfidObjectTypeMap.get(keyLong);
			
			return result = typeStr;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public String getRfidObjectTypeFilter() {
		return rfidObjectTypeFilter;
	}


	public void setRfidObjectTypeFilter(String rfidObjectTypeFilter) {
		this.rfidObjectTypeFilter = rfidObjectTypeFilter;
	}


	public String getRfidIdentityFilter() {
		return rfidIdentityFilter;
	}


	public void setRfidIdentityFilter(String rfidIdentityFilter) {
		this.rfidIdentityFilter = rfidIdentityFilter;
	}


	public String getNameFilter() {
		return nameFilter;
	}


	public void setNameFilter(String nameFilter) {
		this.nameFilter = nameFilter;
	}


	public String getObjectLabelFilter() {
		return objectLabelFilter;
	}


	public void setObjectLabelFilter(String objectLabelFilter) {
		this.objectLabelFilter = objectLabelFilter;
	}


	public boolean isActiveFilter() {
		return activeFilter;
	}


	public void setActiveFilter(boolean activeFilter) {
		this.activeFilter = activeFilter;
	}


	public String getRfidIdentity() {
		return rfidIdentity;
	}


	public void setRfidIdentity(String rfidIdentity) {
		this.rfidIdentity = rfidIdentity;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}


	public RfidtagEntity getRfidTag() {
		return rfidTag;
	}


	public void setRfidTag(RfidtagEntity rfidTag) {
		this.rfidTag = rfidTag;
	}


	public String getRfidtagId() {
		return rfidtagId;
	}


	public void setRfidtagId(String rfidtagId) {
		this.rfidtagId = rfidtagId;
	}

	
}
