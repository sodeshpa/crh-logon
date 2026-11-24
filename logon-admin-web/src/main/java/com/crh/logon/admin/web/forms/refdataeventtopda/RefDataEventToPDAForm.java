/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.refdataeventtopda;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Form of page BaustellensiloTile,jsp.
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_RefDataEventToPDAForm" 
 */
public class RefDataEventToPDAForm extends ActionForm {
	
	public static final int ENTITY_LANG = 0;
	public static final int ENTITY_FUNC = 1;
	public static final int ENTITY_TRUCK_MAINTENANCE = 2;
	public static final int ENTITY_PDA_ERROR_MESSAGE = 3;
	public static final int ENTITY_PDA_MARKET_INFO = 4;
	public static final int ENTITY_CURRENCY = 5;
	public static final int ENTITY_FUEL_TYPE = 6;
	public static final int ENTITY_WAITINGTIMEREASON_SHIPPINGPOINT = 7;
	public static final int ENTITY_WAITINGTIMEREASON_SHIPTO = 8;
	public static final int ENTITY_RFIDOBJECT_TYPE = 9;
	public static final int ENTITY_WAITING_TIME_AT_SHIPPINGPOINT = 10;
	public static final int ENTITY_WAITING_TIME_AT_SHIPTO = 11;
	
	private Locale locale;
	
	private List langs;
	private List functs;
	private List truckMaintenances;
	private List pdaMarketInfos;
	private List pdaErrorMessages;
	private List currencies;
	private List fuelTypes;
	private List waitingtimeReasonsShippingpoint;
	private List waitingtimeReasonsShipto;
	private List rfidobjectTypes;
	private String waitingTimeAtShippingpoint;
	private String waitingTimeAtShipTo;
	
	private Map entityNames;
	
	private int entityType;
	private String entityId;
	private String inputAction;
	
	private static final long serialVersionUID = -8910104051337644556L;

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(RefDataEventToPDAForm.class);

    /**
     * Default constructor.
     */
    public RefDataEventToPDAForm() {
		
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "RefDataEventToPDAForm");
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
			;//PMD
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
			;//PMD
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

	public List getLangs() {
		return langs;
	}

	public void setLangs(List langs) {
		this.langs = langs;
	}

	public List getFuncts() {
		return functs;
	}

	public void setFuncts(List functs) {
		this.functs = functs;
	}

	public List getTruckMaintenances() {
		return truckMaintenances;
	}

	public void setTruckMaintenances(List truckMaintenances) {
		this.truckMaintenances = truckMaintenances;
	}

	public List getPdaMarketInfos() {
		return pdaMarketInfos;
	}

	public void setPdaMarketInfos(List pdaMarketInfos) {
		this.pdaMarketInfos = pdaMarketInfos;
	}

	public List getCurrencies() {
		return currencies;
	}

	public void setCurrencies(List currencies) {
		this.currencies = currencies;
	}

	public List getFuelTypes() {
		return fuelTypes;
	}

	public void setFuelTypes(List fuelTypes) {
		this.fuelTypes = fuelTypes;
	}

	public List getWaitingtimeReasonsShippingpoint() {
		return waitingtimeReasonsShippingpoint;
	}

	public void setWaitingtimeReasonsShippingpoint(
			List waitingtimeReasonsShippingpoint) {
		this.waitingtimeReasonsShippingpoint = waitingtimeReasonsShippingpoint;
	}

	public List getWaitingtimeReasonsShipto() {
		return waitingtimeReasonsShipto;
	}

	public void setWaitingtimeReasonsShipto(List waitingtimeReasonsShipto) {
		this.waitingtimeReasonsShipto = waitingtimeReasonsShipto;
	}

	public List getRfidobjectTypes() {
		return rfidobjectTypes;
	}

	public void setRfidobjectTypes(List rfidobjectTypes) {
		this.rfidobjectTypes = rfidobjectTypes;
	}

	public Map getEntityNames() {
		return entityNames;
	}

	public void setEntityNames(Map entityNames) {
		this.entityNames = entityNames;
	}

	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getInputAction() {
		return inputAction;
	}

	public void setInputAction(String inputAction) {
		this.inputAction = inputAction;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public List getPdaErrorMessages() {
		return pdaErrorMessages;
	}

	public void setPdaErrorMessages(List pdaErrorMessages) {
		this.pdaErrorMessages = pdaErrorMessages;
	}

	public String getWaitingTimeAtShippingpoint() {
		return waitingTimeAtShippingpoint;
	}

	public void setWaitingTimeAtShippingpoint(String waitingTimeAtShippingpoint) {
		this.waitingTimeAtShippingpoint = waitingTimeAtShippingpoint;
	}

	public String getWaitingTimeAtShipTo() {
		return waitingTimeAtShipTo;
	}

	public void setWaitingTimeAtShipTo(String waitingTimeAtShipTo) {
		this.waitingTimeAtShipTo = waitingTimeAtShipTo;
	}
	
}
