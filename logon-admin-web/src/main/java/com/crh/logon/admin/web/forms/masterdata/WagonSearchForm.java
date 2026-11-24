/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.masterdata;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.crh.logon.admin.web.beans.WagonSearchResultBean;
import org.apache.struts.action.ActionForm;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.WagonSearchBean;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_WagonSearchForm"
 */
public class WagonSearchForm  extends ActionForm {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WagonSearchForm.class);
	
    public static final  String FORM_NAME = "admin_WagonSearchForm";
   
    private List<WagonSearchResultBean> wagonList;
    
    private HashMap <String,WagonSearchBean> searchBean;
    private int searchSkip;
    private int searchCount;
    
	private HashMap json;
	
	public WagonSearchForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "WagonSearchForm");
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
		    wagonList = null;
		    searchBean = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public HashMap getJson() {
		return json;
	}

	public void setJson(HashMap json) {
		this.json = json;
	}

	public void setWagonList(List<WagonSearchResultBean> wagonList) {
		this.wagonList = wagonList;
	}

	/**
	 * Returns the DISTINCT id - wagonNo pairs which are present in the search result list.
	 * Needed for wagon detail, where the involved wagons have to be visible on the left side,
	 * depending on the previous search filter. 
	 * @return
	 */
	public Map getWagonIdMap(){
		LinkedHashMap<Long, String> result = new LinkedHashMap();
		if (wagonList != null) {
			for (WagonSearchResultBean bean: wagonList) {
				if (!result.containsKey(bean.getWagonId())) {
					result.put(bean.getWagonId(), bean.getWagonNo());
				}
			}
		}
		return result;
	}


	public int getSearchSkip() {
		return searchSkip;
	}

	public void setSearchSkip(int searchSkip) {
		this.searchSkip = searchSkip;
	}

	public int getSearchCount() {
		return searchCount;
	}

	public void setSearchCount(int searchCount) {
		this.searchCount = searchCount;
	}

	public HashMap<String, WagonSearchBean> getSearchBean() {
		return searchBean;
	}

	public void setSearchBean(HashMap<String, WagonSearchBean> searchBean) {
		this.searchBean = searchBean;
	}


}
