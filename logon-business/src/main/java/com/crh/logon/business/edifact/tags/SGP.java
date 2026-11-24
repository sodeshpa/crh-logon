/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

/**
 * @author AdNovum Informatik AG
 */
public class SGP extends Tag {
	
	private DataElementMeta equipmentIdNumber = new DataElementMeta(0, 0, false);
	private DataElementMeta codeListQualifier = new DataElementMeta(0, 1, false);
	private DataElementMeta codeListResponsibleAgency = new DataElementMeta(0, 2, false);
	private DataElementMeta[] dataElementMetas = { equipmentIdNumber, codeListQualifier, codeListResponsibleAgency };

	public SGP() {
		
		super("SGP", "Split goods placement");
	}

	public String getEquipmentIdNumber() {
		return equipmentIdNumber.getValue();
	}

	public void setEquipmentIdNumber(String equipmentIdNumber) {
		this.equipmentIdNumber.setValue(equipmentIdNumber);
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
