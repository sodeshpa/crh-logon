/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;



/**
 * @author AdNovum Informatik AG
 */
public class UNT extends Tag {
	
	private DataElementMeta numberOfSegments = new DataElementMeta(0, 0, true);
	private DataElementMeta messageReferenceNumber = new DataElementMeta(1, 0, true);
	private DataElementMeta[] dataElementMetas = { numberOfSegments, messageReferenceNumber };
	
	public UNT() {
		
		super("UNT", "Message trailer");
	}

	public String getNumberOfSegments() {
		return numberOfSegments.getValue();
	}

	public void setNumberOfSegments(String numberOfSegments) {
		this.numberOfSegments.setValue(numberOfSegments);
	}

	public String getMessageReferenceNumber() {
		return messageReferenceNumber.getValue();
	}

	public void setMessageReferenceNumber(String messageReferenceNumber) {
		this.messageReferenceNumber.setValue(messageReferenceNumber);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}
}
