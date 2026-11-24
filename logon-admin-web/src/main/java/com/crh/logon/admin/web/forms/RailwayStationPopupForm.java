/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms;

import java.util.List;

import org.apache.struts.action.ActionForm;

/**
 * @author AdNovum Informatik AG
 * @struts.form name="admin_RailwayStationPopupForm" 
 */
public class RailwayStationPopupForm extends ActionForm {
	
public static final String SESSION_RAILWAYSTATION_NAME = "admin_RailwayStationPopupForm";
	
    private List rsList;
    private String railwayStationSearch;
    private String railwayStationId;

	public List getRsList() {
		return rsList;
	}

	public void setRsList(List rsList) {
		this.rsList = rsList;
	}

	public String getRailwayStationSearch() {
		return railwayStationSearch;
	}

	public void setRailwayStationSearch(String railwayStationSearch) {
		this.railwayStationSearch = railwayStationSearch;
	}

	public String getRailwayStationId() {
		return railwayStationId;
	}

	public void setRailwayStationId(String railwayStationId) {
		this.railwayStationId = railwayStationId;
	}
}
