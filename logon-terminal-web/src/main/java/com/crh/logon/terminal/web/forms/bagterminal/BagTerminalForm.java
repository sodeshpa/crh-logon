/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.terminal.web.forms.bagterminal;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.terminal.web.forms.TerminalForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.terminal.web.forms.BagTerminalOrderDTO;
import com.crh.logon.web.utils.Authorization;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="terminal_BagTerminalForm"
 */
public class BagTerminalForm extends TerminalForm<BagTerminalOrderDTO> {

    private static final long serialVersionUID = 1;
    
    public static final String TRUCK_PLATE_NO_SESSION_KEY = "plateNo";
	
    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(BagTerminalForm.class);
    public static final String FORM_NAME = "terminal_BagTerminalForm";
    
    private String inputNewTruckId;
    private String inputAssignmentId;

    private List freeTruckList;
    
    private CarrierEntity carrier;
    private DriverEntity driver;
    private boolean isDriverOnline;

	/**
	 * Default constructor.
	 */
    public BagTerminalForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "BagTerminalForm");
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
			Authorization.authorize(Authorization.BAG_ORDER_TERMINAL_R);			
			this.carrier = null;
			this.driver = null;
			orderDTOList = null;
			inputNewTruckId = null;
			freeTruckList = null;
			inputAssignmentId = null;
			isDriverOnline = false;
			ordersLoadableTerminalFlag = null;
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

	public String getInputNewTruckId() {
		return inputNewTruckId;
	}

	public void setInputNewTruckId(String inputNewTruckId) {
		this.inputNewTruckId = inputNewTruckId;
	}

	public String getInputAssignmentId() {
		return inputAssignmentId;
	}

	public void setInputAssignmentId(String inputAssignmentId) {
		this.inputAssignmentId = inputAssignmentId;
	}

	public List getFreeTruckList() {
		return freeTruckList;
	}

	public void setFreeTruckList(List freeTruckList) {
		this.freeTruckList = freeTruckList;
	}

	public CarrierEntity getCarrier() {
		return carrier;
	}

	public DriverEntity getDriver() {
		return driver;
	}

	public void setCarrier(CarrierEntity carrier) {
		this.carrier = carrier;
	}

	public void setDriver(DriverEntity driver) {
		this.driver = driver;
	}

	public boolean isDriverOnline() {
		return isDriverOnline;
	}

	public void setDriverOnline(boolean isDriverOnline) {
		this.isDriverOnline = isDriverOnline;
	}

}
