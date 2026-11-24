/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import com.crh.logon.persistence.entities.views.BizeventMapEntity;
import java.io.Serializable;
/**
 * @author AdNovum Informatik AG
 */
public class BizeventMapDetailBean implements Serializable{


	private static final long serialVersionUID = 1L;
	
	private BizeventMapEntity bizeventMapEntity;
	private String eventTimeInUserTimeZone;

	public String getEventTimeInUserTimeZone() {
		return eventTimeInUserTimeZone;
	}

	public void setEventTimeInUserTimeZone(String eventTimeInUserTimeZone) {
		this.eventTimeInUserTimeZone = eventTimeInUserTimeZone;
	}

	public BizeventMapEntity getBizeventMapEntity() {
		return bizeventMapEntity;
	}

	public void setBizeventMapEntity(BizeventMapEntity bizeventMapEntity) {
		this.bizeventMapEntity = bizeventMapEntity;
	}
	
}
