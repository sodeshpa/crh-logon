/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.useractivity;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 * 
 * @struts.form name="admin_UserActivityForm"
 */
public class UserActivityForm extends ActionForm {

	private static final long serialVersionUID = 8104100381612457577L;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserActivityForm.class);
	
	private boolean adminView = false;
	private String filterFromDate;
	private String filterFromTime;
	private String filterToDate;
	private String filterToTime;
	private String filterUserId;
	private String filterExecutorId;
	private String filterUserName;
	private String filterUserFirstname;
	private String filterUserHolspace;
	private String filterUserExtlogin;
	private String filterUserPdauser;
	private String filterIpAddress;
	private String filterAction;
	private String filterDescription;
	private String lastLoginBefore;
	
	
	/**
     * Default constructor.
     */
	public UserActivityForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "UserActivityForm");
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
			clearSearchFilters();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    private void clearSearchFilters() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "clearSearchFilters");
		}
		// ]]
		try {
			setFilterFromDate(null);
			setFilterFromTime(null);
			setFilterToDate(null);
			setFilterToTime(null);
			setFilterUserId(null);
			setFilterExecutorId(null);
			setFilterUserName(null);
			setFilterUserFirstname(null);
			setFilterUserHolspace(null);
			setFilterUserExtlogin(null);
			setFilterUserPdauser(null);
			setFilterIpAddress(null);
			setFilterAction(null);
			setFilterDescription(null);
			setLastLoginBefore(null);
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
     * web form, but rather is called on every request before the struts action
     * method gets control and, more importantly, before the request parameters
     * are automatically copied into the form. The primary use is to reset
     * checkboxes and struts multiboxes and the like for which the browser only
     * sends the form parameters when they are set.
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
    	
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

	public String getFilterFromDate() {
		return filterFromDate;
	}

	public void setFilterFromDate(String filterFromDate) {
		this.filterFromDate = filterFromDate;
	}

	public String getFilterFromTime() {
		return filterFromTime;
	}

	public void setFilterFromTime(String filterFromTime) {
		this.filterFromTime = filterFromTime;
	}

	public String getFilterToDate() {
		return filterToDate;
	}

	public void setFilterToDate(String filterToDate) {
		this.filterToDate = filterToDate;
	}

	public String getFilterToTime() {
		return filterToTime;
	}

	public void setFilterToTime(String filterToTime) {
		this.filterToTime = filterToTime;
	}

	public String getFilterUserId() {
		return filterUserId;
	}

	public void setFilterUserId(String filterUserId) {
		this.filterUserId = filterUserId;
	}

	public String getFilterExecutorId() {
		return filterExecutorId;
	}

	public void setFilterExecutorId(String filterExecutorId) {
		this.filterExecutorId = filterExecutorId;
	}

	public String getFilterUserName() {
		return filterUserName;
	}

	public void setFilterUserName(String filterUserName) {
		this.filterUserName = filterUserName;
	}

	public String getFilterUserFirstname() {
		return filterUserFirstname;
	}

	public void setFilterUserFirstname(String filterUserFirstname) {
		this.filterUserFirstname = filterUserFirstname;
	}

	public String getFilterUserHolspace() {
		return filterUserHolspace;
	}

	public void setFilterUserHolspace(String filterUserHolspace) {
		this.filterUserHolspace = filterUserHolspace;
	}

	public String getFilterUserExtlogin() {
		return filterUserExtlogin;
	}

	public void setFilterUserExtlogin(String filterUserExtlogin) {
		this.filterUserExtlogin = filterUserExtlogin;
	}

	public String getFilterUserPdauser() {
		return filterUserPdauser;
	}

	public void setFilterUserPdauser(String filterUserPdauser) {
		this.filterUserPdauser = filterUserPdauser;
	}

	public String getFilterIpAddress() {
		return filterIpAddress;
	}

	public void setFilterIpAddress(String filterIpAddress) {
		this.filterIpAddress = filterIpAddress;
	}

	public String getFilterAction() {
		return filterAction;
	}

	public void setFilterAction(String filterAction) {
		this.filterAction = filterAction;
	}

	public String getFilterDescription() {
		return filterDescription;
	}

	public void setFilterDescription(String filterDescription) {
		this.filterDescription = filterDescription;
	}

	public boolean isAdminView() {
		return adminView;
	}

	public void setAdminView(boolean adminView) {
		this.adminView = adminView;
	}

	public String getLastLoginBefore() {
		return lastLoginBefore;
	}

	public void setLastLoginBefore(String lastLoginBefore) {
		this.lastLoginBefore = lastLoginBefore;
	}
}
