/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

import com.crh.logon.persistence.entities.views.ContainerMonitoringEntity;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
public class ContainerMonitoringWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ContainerMonitoringEntity entity;
	private String prodPlant;
	private String materialAndQty;
	private Long standingTimeInMins;
	private String loadingTime;
	private String arrivalTime;
	
	public ContainerMonitoringWrapper(ContainerMonitoringEntity entity){
		this.entity = entity;
	}
	
	public String getStandingTime(){
		return DateTimeUtils.getFormattedTimeLengthFromMillis(standingTimeInMins*60000L);	
	}
	

	/**
	 * @return the entity
	 */
	public ContainerMonitoringEntity getEntity() {
		return entity;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(ContainerMonitoringEntity entity) {
		this.entity = entity;
	}
	
	/**
	 * @return the prodPlant
	 */
	public String getProdPlant() {
		return prodPlant;
	}

	/**
	 * @param prodPlant the prodPlant to set
	 */
	public void setProdPlant(String prodPlant) {
		this.prodPlant = prodPlant;
	}

	/**
	 * @return the materialAndQty
	 */
	public String getMaterialAndQty() {
		return materialAndQty;
	}

	/**
	 * @param materialAndQty the materialAndQty to set
	 */
	public void setMaterialAndQty(String materialAndQty) {
		this.materialAndQty = materialAndQty;
	}


	/**
	 * @return the standingTimeInMins
	 */
	public Long getStandingTimeInMins() {
		return standingTimeInMins;
	}

	/**
	 * @param standingTimeInMins the standingTimeInMins to set
	 */
	public void setStandingTimeInMins(Long standingTimeInMins) {
		this.standingTimeInMins = standingTimeInMins;
	}

	public String getLoadingTime() {
		return loadingTime;
	}

	public void setLoadingTime(String loadingTime) {
		this.loadingTime = loadingTime;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
}
