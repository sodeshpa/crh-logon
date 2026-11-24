/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;


/**
 * @author AdNovum Informatik AG
 */
public class EQA extends Tag {
	
	private DataElementMeta equipmentCodeQualifier = new DataElementMeta(0, 0, true);
	private DataElementMeta equipmentIdNo = new DataElementMeta(1, 0, false);
	private DataElementMeta codeListQualifier = new DataElementMeta(1, 1, false);
	private DataElementMeta codeListResponsibleAgency = new DataElementMeta(1, 2, false);
	private DataElementMeta[] dataElementMetas = { equipmentCodeQualifier, equipmentIdNo, codeListQualifier, codeListResponsibleAgency };

	public EQA() {

		super("EQA", "Attached equipment");
	}

	public String getEquipmentCodeQualifier() {
		return equipmentCodeQualifier.getValue();
	}

	public void setEquipmentCodeQualifier(String equipmentCodeQualifier) {
		this.equipmentCodeQualifier.setValue(equipmentCodeQualifier);
	}

	public String getEquipmentIdNo() {
		return equipmentIdNo.getValue();
	}

	public void setEquipmentIdNo(String equipmentIdNo) {
		this.equipmentIdNo.setValue(equipmentIdNo);
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
