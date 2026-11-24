/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.dispo;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.DriverBreakEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_dispo_DriverBreakForm"
 */
public class DriverBreakForm extends ActionForm {
	
	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(DriverBreakForm.class);

    // input from the user
	private Long driverBreakId;
	private Long driverId;
	private boolean isLunchBreak;
	private DriverBreakEntity driverBreak;
	private DriverEntity driver;
	private Date breakDay;
	private String startTime;
	private String endTime;
	

	/**
     * Default constructor.
     */
    public DriverBreakForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "DriverBreakForm");
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
     * fields on the order.web form.
     */
    private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			driverBreak = null;
			driver = null;
			driverBreakId = null;
			driverId = null;
			isLunchBreak = false;
			startTime = null;
			endTime = null;
			breakDay = null;
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
			driverBreakId = null;
			driverId = null;
			isLunchBreak = false;
			if (driverBreak != null){
				driverBreak.setIsLunchBreak(Boolean.FALSE);
			}
			startTime = null;
			endTime = null;
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
	        
	        if (request.getParameter("save") != null){
	        	Date dateStart = null;
				try {
					dateStart = DateTimeUtils.parseHoursMinutes(startTime, breakDay);
				}
				catch (ParseException e) {
					new ActionMessage("dispo.errors.driverbreak.startinvalid");					
				}
	        	
	        	Date dateEnd = null;
				try {
					dateEnd = DateTimeUtils.parseHoursMinutes(endTime, breakDay);
				}
				catch (ParseException e) {
					new ActionMessage("dispo.errors.driverbreak.endinvalid");
				}
	        	
				if ((dateStart != null) && (dateEnd != null)){
					if (dateEnd.after(dateStart)){
			        	driverBreak.setStartDat(dateStart);
			        	driverBreak.setEndDat(dateEnd);
					}
					else{
		                errors.add(ActionMessages.GLOBAL_MESSAGE,
		                        new ActionMessage("dispo.errors.driverbreak.startendseq"));
						
					}
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
	public Long getDriverBreakId() {
		return driverBreakId;
	}

	public void setDriverBreakId(Long driverBreakId) {
		this.driverBreakId = driverBreakId;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	public DriverBreakEntity getDriverBreak() {
		return driverBreak;
	}

	public void setDriverBreak(DriverBreakEntity driverBreak) {
		this.driverBreak = driverBreak;
		if (driverBreak != null){
			this.driver = driverBreak.getDriver();
		}
	}

    public DriverEntity getDriver() {
		return driver;
	}

	public void setDriver(DriverEntity driver) {
		this.driver = driver;
	}

	public boolean getIsLunchBreak() {
		return isLunchBreak;
	}

	public void setIsLunchBreak(boolean isLunchBreak) {
		this.isLunchBreak = isLunchBreak;
	}	
	
	public String getStartTime(){
		return DateTimeUtils.formatHoursMinutes(driverBreak.getStartDat());
	}
	
	public String getEndTime(){
		return DateTimeUtils.formatHoursMinutes(driverBreak.getEndDat());
	}
	
	public void setBreakDay(Date breakDay) {
		this.breakDay = breakDay;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
