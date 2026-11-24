/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;



/**
 * @author AdNovum Informatik AG
 */
public class EQD extends Tag {
	
	private DataElementMeta equipmentTypeCodeQualifier = new DataElementMeta(0, 0, true);
	private DataElementMeta equipmentIdNo = new DataElementMeta(1, 0, false);
	private DataElementMeta codeListResponsibleAgency = new DataElementMeta(1, 2, false);
	private DataElementMeta sizeAndTypeId = new DataElementMeta(2, 0, false);
	private DataElementMeta sizeAndTypeCodeListResponsibleAgency = new DataElementMeta(2, 2, false);
	private DataElementMeta equipmentStatusCode = new DataElementMeta(4, 0, false);
	private DataElementMeta fullEmptyIndicator = new DataElementMeta(5, 0, false);
	private DataElementMeta[] dataElementMetas = { equipmentTypeCodeQualifier, equipmentIdNo, codeListResponsibleAgency, sizeAndTypeId, sizeAndTypeCodeListResponsibleAgency, equipmentStatusCode, fullEmptyIndicator };
	
	public EQD() {
		
		super("EQD", "Equipment details");
	}

	public String getEquipmentTypeCodeQualifier() {
		return equipmentTypeCodeQualifier.getValue();
	}

	public void setEquipmentTypeCodeQualifier(String equipmentTypeCodeQualifier) {
		this.equipmentTypeCodeQualifier.setValue(equipmentTypeCodeQualifier);
	}

	public String getEquipmentIdNo() {
		return equipmentIdNo.getValue();
	}

	public void setEquipmentIdNo(String equipmentIdNo) {
		this.equipmentIdNo.setValue(equipmentIdNo);
	}

	public String getCodeListResponsibleAgency() {
		return codeListResponsibleAgency.getValue();
	}

	public void setCodeListResponsibleAgency(String codeListResponsibleAgency) {
		this.codeListResponsibleAgency.setValue(codeListResponsibleAgency);
	}

	public String getSizeAndTypeId() {
		return sizeAndTypeId.getValue();
	}

	public void setSizeAndTypeId(String sizeAndTypeId) {
		this.sizeAndTypeId.setValue(sizeAndTypeId);
	}

	public String getSizeAndTypeCodeListResponsibleAgency() {
		return sizeAndTypeCodeListResponsibleAgency.getValue();
	}

	public void setSizeAndTypeCodeListResponsibleAgency(
			String sizeAndTypeCodeListResponsibleAgency) {
		this.sizeAndTypeCodeListResponsibleAgency.setValue(sizeAndTypeCodeListResponsibleAgency);
	}

	public String getEquipmentStatusCode() {
		return equipmentStatusCode.getValue();
	}

	public void setEquipmentStatusCode(String equipmentStatusCode) {
		this.equipmentStatusCode.setValue(equipmentStatusCode);
	}

	public String getFullEmptyIndicator() {
		return fullEmptyIndicator.getValue();
	}

	public void setFullEmptyIndicator(String fullEmptyIndicator) {
		this.fullEmptyIndicator.setValue(fullEmptyIndicator);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}
}
