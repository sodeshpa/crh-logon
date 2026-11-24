/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;


/**
 * @author AdNovum Informatik AG
 */
public class LOC extends Tag {

	private DataElementMeta functionCodeQualifier = new DataElementMeta(0, 0, true);
	private DataElementMeta locationNameCode = new DataElementMeta(1, 0, false);
	private DataElementMeta codeListIdCode = new DataElementMeta(1, 1, false);
	private DataElementMeta codeListResponsibleAgency = new DataElementMeta(1, 2, false);
	private DataElementMeta locationName = new DataElementMeta(1, 3, false);
	private DataElementMeta related1LocationNameCode = new DataElementMeta(2, 0, false);
	private DataElementMeta related1CodeListIdCode = new DataElementMeta(2, 1, false);
	private DataElementMeta related1CodeListResponsibleAgency = new DataElementMeta(2, 2, false);
	private DataElementMeta related2LocationNameCode = new DataElementMeta(3, 0, false);
	private DataElementMeta related2CodeListIdCode = new DataElementMeta(3, 1, false);
	private DataElementMeta related2CodeListResponsibleAgency = new DataElementMeta(3, 2, false);
	private DataElementMeta[] dataElementMetas = { functionCodeQualifier, locationNameCode, codeListIdCode, codeListResponsibleAgency, locationName, related1LocationNameCode, related1CodeListIdCode, related1CodeListResponsibleAgency, related2LocationNameCode, related2CodeListIdCode, related2CodeListResponsibleAgency };
	
	public LOC() {
		
		super("LOC", "Place/location identification");		
	}

	public String getFunctionCodeQualifier() {
		return functionCodeQualifier.getValue();
	}

	public void setFunctionCodeQualifier(String functionCodeQualifier) {
		this.functionCodeQualifier.setValue(functionCodeQualifier);
	}

	public String getLocationNameCode() {
		return locationNameCode.getValue();
	}

	public void setLocationNameCode(String locationNameCode) {
		this.locationNameCode.setValue(locationNameCode);
	}

	public String getCodeListIdCode() {
		return codeListIdCode.getValue();
	}

	public void setCodeListIdCode(String codeListIdCode) {
		this.codeListIdCode.setValue(codeListIdCode);
	}

	public String getCodeListResponsibleAgency() {
		return codeListResponsibleAgency.getValue();
	}

	public void setCodeListResponsibleAgency(String codeListResponsibleAgency) {
		this.codeListResponsibleAgency.setValue(codeListResponsibleAgency);
	}

	public String getLocationName() {
		return locationName.getValue();
	}

	public void setLocationName(String locationName) {
		this.locationName.setValue(locationName);
	}

	public String getRelated1LocationNameCode() {
		return related1LocationNameCode.getValue();
	}

	public void setRelated1LocationNameCode(String related1LocationNameCode) {
		this.related1LocationNameCode.setValue(related1LocationNameCode);
	}

	public String getRelated1CodeListIdCode() {
		return related1CodeListIdCode.getValue();
	}

	public void setRelated1CodeListIdCode(String related1CodeListIdCode) {
		this.related1CodeListIdCode.setValue(related1CodeListIdCode);
	}

	public String getRelated1CodeListResponsibleAgency() {
		return related1CodeListResponsibleAgency.getValue();
	}

	public void setRelated1CodeListResponsibleAgency(String related1CodeListResponsibleAgency) {
		this.related1CodeListResponsibleAgency.setValue(related1CodeListResponsibleAgency);
	}

	public String getRelated2LocationNameCode() {
		return related2LocationNameCode.getValue();
	}

	public void setRelated2LocationNameCode(String related2LocationNameCode) {
		this.related2LocationNameCode.setValue(related2LocationNameCode);
	}

	public String getRelated2CodeListIdCode() {
		return related2CodeListIdCode.getValue();
	}

	public void setRelated2CodeListIdCode(String related2CodeListIdCode) {
		this.related2CodeListIdCode.setValue(related2CodeListIdCode);
	}

	public String getRelated2CodeListResponsibleAgency() {
		return related2CodeListResponsibleAgency.getValue();
	}

	public void setRelated2CodeListResponsibleAgency(String related2CodeListResponsibleAgency) {
		this.related2CodeListResponsibleAgency.setValue(related2CodeListResponsibleAgency);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}
}
