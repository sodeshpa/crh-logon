/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.railrepair;

import java.util.List;

import org.apache.struts.action.ActionForm;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_RailRepairForm"
 */
public class RailRepairForm  extends ActionForm {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailRepairForm.class);
	
    public static final  String FORM_NAME = "order_RailRepairForm";

    private List wagonKeeperList;
    private List repairShiptoList;
    private List combinedOrderStatusList;
    
	public RailRepairForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "RailRepairForm");
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
	
	public void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			
			clear();
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
			wagonKeeperList = null;
			repairShiptoList = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public List getWagonKeeperList() {
		return wagonKeeperList;
	}

	public void setWagonKeeperList(List wagonKeeperList) {
		this.wagonKeeperList = wagonKeeperList;
	}
	
	public List getRepairShiptoList() {
		return repairShiptoList;
	}

	public void setRepairShiptoList(List repairShiptoList) {
		this.repairShiptoList = repairShiptoList;
	}
	
	public List getCombinedOrderStatusList() {
		return combinedOrderStatusList;
	}

	public void setCombinedOrderStatusList(List combinedOrderStatusList) {
		this.combinedOrderStatusList = combinedOrderStatusList;
	}

}