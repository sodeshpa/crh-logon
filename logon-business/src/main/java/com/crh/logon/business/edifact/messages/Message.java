/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.messages;

/**
 * @author AdNovum Informatik AG
 */
public abstract class Message {

	private String interchangeConrolReference;
	private String messageReferenceNo;
	private boolean testIndicator;
	
	protected Message(String interchangeConrolReference, String messageReferenceNo) {
		
		this.interchangeConrolReference = interchangeConrolReference;
		this.messageReferenceNo = messageReferenceNo;
	}

	public String getInterchangeConrolReference() {
		return interchangeConrolReference;
	}

	public String getMessageReferenceNo() {
		return messageReferenceNo;
	}

	public boolean isTestIndicator() {
		return testIndicator;
	}

	public void setTestIndicator(boolean testIndicator) {
		this.testIndicator = testIndicator;
	}
}
