/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

import com.crh.logon.persistence.entities.views.BizeventOrderLogEntity;

public class OrderLogEntryWrapper implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public OrderLogEntryWrapper(BizeventOrderLogEntity entity) {
		super();
		this.entity = entity;
	}
	
	private BizeventOrderLogEntity entity;
	private String additionalInfo;
	private String initiator;
	
	private String description;
	private String eventTs;
	
	/**
	 * Value is dependening on the bizevent type. In certain cases information coming
	 * from the references of the bizevent is here, in other cases info can come from the audit
	 * entries.
	 * 
	 * @return
	 */
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public BizeventOrderLogEntity getEntity() {
		return entity;
	}
	public void setEntity(BizeventOrderLogEntity entity) {
		this.entity = entity;
	}
	public String getDescription() {
		return description;
	}
	public void setInitiator(String initiator){
		this.initiator = initiator;
	}
	public String getInitiator(){
		return initiator;
	}
	/**
	 * Always comes from the description, but in certain cases the original 
	 * description value is not supposed to be shown.
	 * 
	 * @return
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getEventTs() {
		return eventTs;
	}

	public void setEventTs(String eventTs) {
		this.eventTs = eventTs;
	}
}
