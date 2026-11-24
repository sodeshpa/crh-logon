/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.pdaadmin;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.utils.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.PdaDeviceEntity;

/**
 * @author AdNovum Informatik AG
 * 
 * @struts.form name="admin_PDAAdminForm" 
  */
public class PDAAdminForm extends ActionForm {

	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(PDAAdminForm.class);
	
	public static final int DEV_FUNC_CHECK_STATE_NOT_YET_VERIFIED = 0;
	public static final int DEV_FUNC_CHECK_STATE_READY_TO_VERIFY = 1;
	public static final int DEV_FUNC_CHECK_STATE_VERIFIED_READY = 2;
	public static final int DEV_FUNC_CHECK_STATE_VERIFIED_NOT_READY = 3;
	
	private PdaDeviceEntity pda;
	
	private String pdaDeviceId;
	private String pdaDeviceNo;
	private String carrierId;
	private String typeDescription;
	private String inventoryNumber;
	private String simcardCd;
	private String simcardNumber;
	private String phoneNumber;
	
	private String lastRegistrationDateStr;
	private String lastRegistrationTimeStr;
	private String softwareVersion;
	private boolean activeFlag;
	private HashMap json;
	private Locale locale;
	// values for the filtering dropdowns, key = id, value = display name
	
	private String inputAction;
	private int deviceFunctionalityCheckState;

	private TimeZone timezone;
	
	 /**
     * Default constructor.
     */
    public PDAAdminForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "PDAAdminForm");
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
    
    public void initDetailFields() {
		pdaDeviceId = null;
		pdaDeviceNo = null;
		typeDescription = null;
		inventoryNumber = null;
		lastRegistrationDateStr = null;
		lastRegistrationTimeStr = null;
		softwareVersion = null;
		activeFlag = false;
		carrierId = null;
		simcardCd = null;
		simcardNumber = null;
		phoneNumber = null;
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
			initDetailFields();
			pda = null;
			deviceFunctionalityCheckState = DEV_FUNC_CHECK_STATE_NOT_YET_VERIFIED;
			
			inputAction = null;
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
    	activeFlag = false;
    }

	public void setTimeZone(TimeZone timezone) {
		this.timezone = timezone;
	}

	public String getInputAction() {
		return inputAction;
	}

	public void setInputAction(String inputAction) {
		this.inputAction = inputAction;
	}

	public PdaDeviceEntity getPda() {
		return pda;
	}

	public void setPda(PdaDeviceEntity pda) {
		this.pda = pda;
	}

	public String getPdaDeviceNo() {
		return pdaDeviceNo;
	}

	public void setPdaDeviceNo(String pdaDeviceNo) {
		this.pdaDeviceNo = pdaDeviceNo;
	}

	public HashMap getJson() {
		return json;
	}

	public void setJson(HashMap json) {
		this.json = json;
	}

	public String getPdaDeviceId() {
		return pdaDeviceId;
	}

	public void setPdaDeviceId(String pdaDeviceId) {
		this.pdaDeviceId = pdaDeviceId;
	}

	public String getCarrierId() {
		return carrierId;
	}

	public void setCarrierId(String carrierId) {
		this.carrierId = carrierId;
	}

	public String getTypeDescription() {
		return typeDescription;
	}

	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}

	public String getInventoryNumber() {
		return inventoryNumber;
	}

	public void setInventoryNumber(String inventoryNumber) {
		this.inventoryNumber = inventoryNumber;
	}

	public String getSimcardCd() {
		return simcardCd;
	}

	public void setSimcardCd(String simcardCd) {
		this.simcardCd = simcardCd;
	}

	public String getSimcardNumber() {
		return simcardNumber;
	}

	public void setSimcardNumber(String simcardNumber) {
		this.simcardNumber = simcardNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getLastRegistrationDateStr() {
		return lastRegistrationDateStr;
	}

	public void setLastRegistrationDateStr(String lastRegistrationDateStr) {
		this.lastRegistrationDateStr = lastRegistrationDateStr;
	}

	public Date getLastRegistrationDat() throws ParseException {
		if (StringUtils.notEmpty(lastRegistrationTimeStr)) {
			return this.timezone.parseDayMonthYearHourMinute(lastRegistrationDateStr,
					lastRegistrationTimeStr);
		}
		return null;
	}

	public void setLastRegistrationDat(Date timestamp) {
		if (timestamp != null) {
			String f = this.timezone.fmtAsDetailedDateTime(timestamp);
			setLastRegistrationDateStr(f);

			f = this.timezone.fmtAsHourMinute(timestamp);
			setLastRegistrationTimeStr(f);
		}
	}

	public String getLastRegistrationTimeStr() {
		return lastRegistrationTimeStr;
	}

	public void setLastRegistrationTimeStr(String lastRegistrationTimeStr) {
		this.lastRegistrationTimeStr = lastRegistrationTimeStr;
	}

	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public int getDeviceFunctionalityCheckState() {
		return deviceFunctionalityCheckState;
	}

	public void setDeviceFunctionalityCheckState(int deviceFunctionalityCheckState) {
		this.deviceFunctionalityCheckState = deviceFunctionalityCheckState;
	}

	public boolean isActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
	
}
