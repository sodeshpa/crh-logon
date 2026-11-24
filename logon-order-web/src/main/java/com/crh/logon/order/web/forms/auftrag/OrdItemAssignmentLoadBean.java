package com.crh.logon.order.web.forms.auftrag;

import com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class OrdItemAssignmentLoadBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private OrdItemAssignmtLoadEntity entity;
	private String loadingPlannedStart;
	private String loadingRealStart;

	public OrdItemAssignmentLoadBean(OrdItemAssignmtLoadEntity entity,
									 String loadingPlannedStart,
									 String loadingRealStart) {
		this.entity = entity;
		this.loadingPlannedStart = loadingPlannedStart;
		this.loadingRealStart = loadingRealStart;
	}

	public String getLoadingPlannedStart() {
		return loadingPlannedStart;
	}

	public String getLoadingRealStart() {
		return loadingRealStart;
	}
}
