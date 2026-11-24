/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.messages;

import java.util.Date;

public abstract class Iftsta extends Message {

	private Date dateTimeOfMessage;
		
	protected Iftsta(String interchangeConrolReference, String messageReferenceNo) {
		
		super(interchangeConrolReference, messageReferenceNo);
	}
	
	public Date getDateTimeOfMessage() {

		return dateTimeOfMessage;
	}

	public void setDateTimeOfMessage(Date dateTimeOfMessage) {

		this.dateTimeOfMessage = dateTimeOfMessage;
	}
}
