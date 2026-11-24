package com.crh.logon.order.web.beans;

import com.crh.logon.persistence.entities.views.RailRepairEntity;

import java.io.Serializable;

/**
 * Wraps a RailRepairEntity to allow proper date/time conversions
 *
 * @author AdNovum Informatik AG
 */
public class RailRepairBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private RailRepairEntity entity;

	private String created;
	private String arrived;
	private String plantEnter;
	private String plantLeave;

	public RailRepairBean() { }

	public RailRepairBean(RailRepairEntity entity) {
		this.entity = entity;
	}

	public RailRepairEntity getEntity() {
		return entity;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getArrived() {
		return arrived;
	}

	public void setArrived(String arrived) {
		this.arrived = arrived;
	}

	public String getPlantEnter() {
		return plantEnter;
	}

	public void setPlantEnter(String plantEnter) {
		this.plantEnter = plantEnter;
	}

	public String getPlantLeave() {
		return plantLeave;
	}

	public void setPlantLeave(String plantLeave) {
		this.plantLeave = plantLeave;
	}
}
