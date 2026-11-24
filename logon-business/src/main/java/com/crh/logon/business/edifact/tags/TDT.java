/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;


/**
 * @author AdNovum Informatik AG
 */
public class TDT extends Tag {
	
	private DataElementMeta transportStageCodeQualifier = new DataElementMeta(0, 0, true);
	private DataElementMeta conveyanceReferenceNo = new DataElementMeta(1, 0, false);
	private DataElementMeta transportModeNameCode = new DataElementMeta(2, 0, false);
	private DataElementMeta carrierId = new DataElementMeta(4, 0, false);
	private DataElementMeta codeListIdCode = new DataElementMeta(4, 1, false);
	private DataElementMeta codeListResponsibleAgencyCode = new DataElementMeta(4, 2, false);
	private DataElementMeta transportIdMeans = new DataElementMeta(7, 0, false);
	private DataElementMeta transportIdCodeListQualifier = new DataElementMeta(7, 1, false);
	private DataElementMeta transportIdCodeListResponsibleAgent = new DataElementMeta(7, 2, false);
	private DataElementMeta[] dataElementMetas = { transportStageCodeQualifier, conveyanceReferenceNo, transportModeNameCode, carrierId, codeListIdCode, codeListResponsibleAgencyCode, transportIdMeans, transportIdCodeListQualifier, transportIdCodeListResponsibleAgent };
	
	public TDT() {
		
		super("TDT", "Transport information");
	}

	public String getTransportStageCodeQualifier() {
		return transportStageCodeQualifier.getValue();
	}

	public void setTransportStageCodeQualifier(String transportStageCodeQualifier) {
		this.transportStageCodeQualifier.setValue(transportStageCodeQualifier);
	}

	public String getConveyanceReferenceNo() {
		return conveyanceReferenceNo.getValue();
	}

	public void setConveyanceReferenceNo(String conveyanceReferenceNo) {
		this.conveyanceReferenceNo.setValue(conveyanceReferenceNo);
	}

	public String getTransportModeNameCode() {
		return transportModeNameCode.getValue();
	}

	public void setTransportModeNameCode(String transportModeNameCode) {
		this.transportModeNameCode.setValue(transportModeNameCode);
	}

	public String getCarrierId() {
		return carrierId.getValue();
	}

	public void setCarrierId(String carrierId) {
		this.carrierId.setValue(carrierId);
	}

	public String getCodeListIdCode() {
		return codeListIdCode.getValue();
	}

	public void setCodeListIdCode(String codeListIdCode) {
		this.codeListIdCode.setValue(codeListIdCode);
	}

	public String getCodeListResponsibleAgencyCode() {
		return codeListResponsibleAgencyCode.getValue();
	}

	public void setCodeListResponsibleAgencyCode(String codeListResponsibleAgencyCode) {
		this.codeListResponsibleAgencyCode.setValue(codeListResponsibleAgencyCode);
	}

	public String getTransportIdMeans() {
		return transportIdMeans.getValue();
	}

	public void setTransportIdMeans(String transportIdMeans) {
		this.transportIdMeans.setValue(transportIdMeans);
	}

	public String getTransportIdCodeListQualifier() {
		return transportIdCodeListQualifier.getValue();
	}

	public void setTransportIdCodeListQualifier(String transportIdCodeListQualifier) {
		this.transportIdCodeListQualifier.setValue(transportIdCodeListQualifier);
	}

	public String getTransportIdCodeListResponsibleAgent() {
		return transportIdCodeListResponsibleAgent.getValue();
	}

	public void setTransportIdCodeListResponsibleAgent(
			String transportIdCodeListResponsibleAgent) {
		this.transportIdCodeListResponsibleAgent.setValue(transportIdCodeListResponsibleAgent);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}
}