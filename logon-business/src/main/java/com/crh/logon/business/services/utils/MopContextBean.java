/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import com.holcim.logon.eventbeans.in.mop.MopRequestMessage;

/**
 * @author AdNovum Informatik AG
 */
public class MopContextBean extends LogonEventContextBean {
	
	private Long mopeventHeaderInId;
	private MopRequestMessage mopRequestMessage;
	
	public MopContextBean() {
		super();
	}
	
	public Long getMopeventHeaderInId() {
		return mopeventHeaderInId;
	}

	public void setMopeventHeaderInId(Long mopeventHeaderInId) {
		this.mopeventHeaderInId = mopeventHeaderInId;
	}

	public MopRequestMessage getMopRequestMessage() {
		return mopRequestMessage;
	}

	public void setMopRequestMessage(MopRequestMessage mopRequestMessage) {
		this.mopRequestMessage = mopRequestMessage;
	}
}
