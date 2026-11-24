/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

/**
 * @author AdNovum Informatik AG
 */
public class TSR extends Tag {

	private DataElementMeta transportPriorityCode = new DataElementMeta(2, 0 , false);
	private DataElementMeta codeListQualifier = new DataElementMeta(2, 1, false);
	private DataElementMeta codeListResponsibleAgency = new DataElementMeta(2, 2, false);
	private DataElementMeta[] dataElementMetas = { transportPriorityCode, codeListQualifier, codeListResponsibleAgency };
	
	public TSR() {
		super("TSR", "Transport service requirements");
	}
	
	public String getTransportPriorityCode() {
		return transportPriorityCode.getValue();
	}
	
	public void setTransportPriorityCode(String transportPriorityCode) {
		this.transportPriorityCode.setValue(transportPriorityCode);
	}
	
	public String getCodeListQualifier() {
		return codeListQualifier.getValue();
	}
	
	public void setCodeListQualifier(String codeListQualifier) {
		this.codeListQualifier.setValue(codeListQualifier);
	}
	
	public String getCodeListResponsibleAgency() {
		return codeListResponsibleAgency.getValue();
	}
	
	public void setCodeListResponsibleAgency(String codeListResponsibleAgency) {
		this.codeListResponsibleAgency.setValue(codeListResponsibleAgency);
	}
	
	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}
}
