/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;



/**
 * @author AdNovum Informatik AG
 */
public class UNH extends Tag {
	
	private DataElementMeta messageReferenceNumber = new DataElementMeta(0, 0, true);
	private DataElementMeta type = new DataElementMeta(1, 0, true);
	private DataElementMeta version = new DataElementMeta(1, 1, true);
	private DataElementMeta release = new DataElementMeta(1, 2, true);
	private DataElementMeta agency = new DataElementMeta(1, 3, true);
	private DataElementMeta associationAssignedCode = new DataElementMeta(1, 4, false);
	private DataElementMeta[] dataElementMetas = { messageReferenceNumber, type, version, release, agency, associationAssignedCode };
	
	public UNH() {
		
		super("UNH", "Message header");
	}
	
	public String getMessageReferenceNumber() {
		return messageReferenceNumber.getValue();
	}

	public void setMessageReferenceNumber(String messageReferenceNumber) {
		this.messageReferenceNumber.setValue(messageReferenceNumber);
	}
	
	public String getType() {
		return type.getValue();
	}

	public void setType(String type) {
		this.type.setValue(type);
	}

	public String getVersion() {
		return version.getValue();
	}

	public void setVersion(String version) {
		this.version.setValue(version);
	}
	
	public String getRelease() {
		return release.getValue();
	}
	
	public void setRelease(String release) {
		this.release.setValue(release);
	}

	public String getAgency() {
		return agency.getValue();
	}

	public void setAgency(String agency) {
		this.agency.setValue(agency);
	}

	public String getAssociationAssignedCode() {
		return associationAssignedCode.getValue();
	}
	
	public void setAssociationAssignedCode(String associationAssignedCode) {
		this.associationAssignedCode.setValue(associationAssignedCode);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}
}
