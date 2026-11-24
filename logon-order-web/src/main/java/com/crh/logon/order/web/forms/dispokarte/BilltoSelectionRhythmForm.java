/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.dispokarte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;

/**
 * @author AdNovum Informatik AG
 * 
 * @struts.form name="order_dispokarte_BilltoSelectionRhythmForm" * 
 */
public class BilltoSelectionRhythmForm extends ActionForm {

	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(BilltoSelectionRhythmForm.class);
    
	public static final int NUM_OF_MONTHS = 12;
	public static final int NUM_OF_DAYS_IN_WEEK = 7;
	public static final int NUM_OF_DAYS_IN_MONTH = 31;
	
	public static final int RULE_TYPE_MONTH = 0;
	public static final int RULE_TYPE_DAYS_OF_WEEK = 1;
	public static final int RULE_TYPE_DAYS_IN_MONTH = 2;
	
    public static final String TAB_MONTHLY = "TabMonthly";
    public static final String TAB_WEEKLY = "TabWeekly";
    public static final String TAB_DAYOFMONTH = "TabDayOfMonth";
    
    public static final String RULE_TYPE_MONTHLY = "Monthly";
    public static final String RULE_TYPE_WEEKLY = "Weekly";
    public static final String RULE_TYPE_DAYOFMONTH = "DayOfMonth";
    
    private String tab;
    private int id;
    private List monthCheckboxList;
    private List weekCheckboxList;
    private List dayOfMonthCheckboxList;
    private ShiptoSareaRestrEntity currentShiptoSareaRestr;
    private String selectedRuleType;
    
    private Map existingRulesMap;
    
    /**
     * Default constructor.
     */
    public BilltoSelectionRhythmForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "BilltoSelectionRhythmForm");
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
			existingRulesMap = new HashMap();
			currentShiptoSareaRestr = null;
			tab = "";
			selectedRuleType = "";
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    /*
     *  Initializes the monthCeckboxList, weekCheckboxList and dayOfMonthCheckboxList 
     *  for a specified number of billtos.
     */
    
    public void initLists (int numOfBilltos) {
    	monthCheckboxList = new ArrayList();
    	for (int i = 0; i < numOfBilltos*NUM_OF_MONTHS; i++) {
			monthCheckboxList.add("");
		}
		weekCheckboxList = new ArrayList();
		for (int i = 0; i < numOfBilltos*NUM_OF_DAYS_IN_WEEK; i++) {
			weekCheckboxList.add("");
		}
		dayOfMonthCheckboxList = new ArrayList();
		for (int i = 0; i < numOfBilltos*NUM_OF_DAYS_IN_MONTH; i++) {
			dayOfMonthCheckboxList.add("");
		}
    }
    
    /**
     * Clears all the input fields on the ordermgr.web form, discards any existing
     * search results, and resets any existing pages. Called in response to
     * clicking the reset button on the ordermgr.web form.
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
			int size;
			if ( TAB_MONTHLY.equals(tab) && monthCheckboxList != null) {
				size = monthCheckboxList.size();
				for (int i = 0; i < size; i++) {
				/*	if (request.getParameter("monthCheckbox[" + new Integer(i).toString() + "]") != null) {
						monthCheckboxList.set(i, "true");
					} else {*/
						monthCheckboxList.set(i, "");
			//				}
				}
			}
			else if ( TAB_WEEKLY.equals(tab) && weekCheckboxList != null ) {
				size = weekCheckboxList.size();
				for (int i = 0; i < size; i++) {
			/*			if (request.getParameter("weekCheckboxList[" + new Integer(i).toString() + "]") != null) {
						weekCheckboxList.set(i, "true");
					} else {*/
						weekCheckboxList.set(i, "");
			//				}
				}
			}
			else if ( TAB_DAYOFMONTH.equals(tab) && dayOfMonthCheckboxList != null ) {
				size = dayOfMonthCheckboxList.size();
				for (int i = 0; i < size; i++) {
			/*				if (request.getParameter("dayOfMonthCheckboxList[" + new Integer(i).toString() + "]") != null) {
						dayOfMonthCheckboxList.set(i, "true");
					} else {*/
						dayOfMonthCheckboxList.set(i, "");
			//				}
				}
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

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}
	
	public String getMonthCheckbox(int index) {
		return (String) monthCheckboxList.get(index);
	}
    
	public void setMonthCheckbox(int index, String value) {
		monthCheckboxList.set(index, value);
	}

	public List getMonthCheckboxList() {
		return monthCheckboxList;
	}

	public void setMonthCheckboxList(List monthCheckboxList) {
		this.monthCheckboxList = monthCheckboxList;
	}

	public List getWeekCheckboxList() {
		return weekCheckboxList;
	}

	public void setWeekCheckboxList(List weekCheckboxList) {
		this.weekCheckboxList = weekCheckboxList;
	}

	public List getDayOfMonthCheckboxList() {
		return dayOfMonthCheckboxList;
	}

	public void setDayOfMonthCheckboxList(List dayOfMonthCheckboxList) {
		this.dayOfMonthCheckboxList = dayOfMonthCheckboxList;
	}
	
	public String getWeekCheckbox(int index) {
		return (String) weekCheckboxList.get(index);
	}
    
	public void setWeekCheckbox(int index, String value) {
		weekCheckboxList.set(index, value);
	}
	
	public String getDayOfMonthCheckbox(int index) {
		return (String) dayOfMonthCheckboxList.get(index);
	}
    
	public void setDayOfMonthCheckbox(int index, String value) {
		dayOfMonthCheckboxList.set(index, value);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Map getExistingRulesMap() {
		return existingRulesMap;
	}

	public void setExistingRulesMap(Map existingRulesMap) {
		this.existingRulesMap = existingRulesMap;
	}

	public ShiptoSareaRestrEntity getCurrentShiptoSareaRestr() {
		return currentShiptoSareaRestr;
	}

	public void setCurrentShiptoSareaRestr(ShiptoSareaRestrEntity currentShiptoSareaRestr) {
		this.currentShiptoSareaRestr = currentShiptoSareaRestr;
	}

	public String getSelectedRuleType() {
		return selectedRuleType;
	}

	public void setSelectedRuleType(String selectedRuleType) {
		this.selectedRuleType = selectedRuleType;
	}
}
