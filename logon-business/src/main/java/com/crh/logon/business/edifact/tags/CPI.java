/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

/**
 * @author AdNovum Informatik AG
 */
public class CPI extends Tag {

	private DataElementMeta chargeCategoryCode = new DataElementMeta(0, 0, false);
	private DataElementMeta codeListQualifier = new DataElementMeta(0, 1, false);
	private DataElementMeta codeListResponsibleAgency = new DataElementMeta(0, 2, false);
	private DataElementMeta methodOfPaymentTransportChargesCode = new DataElementMeta(1, 0, false);
	private DataElementMeta methodOfPaymentCodeListQualifier = new DataElementMeta(1, 1, false);
	private DataElementMeta methodOfPaymentCodeListResponsibleAgency = new DataElementMeta(1, 2, false);
	private DataElementMeta[] dataElementMetas = { chargeCategoryCode, codeListQualifier, codeListResponsibleAgency, methodOfPaymentTransportChargesCode, methodOfPaymentCodeListQualifier, methodOfPaymentCodeListResponsibleAgency };
	
	public CPI() {
		
		super("CPI", "Charge payment instructions");
	}

	public String getChargeCategoryCode() {
		return chargeCategoryCode.getValue();
	}

	public void setChargeCategoryCode(String chargeCategoryCode) {
		this.chargeCategoryCode.setValue(chargeCategoryCode);
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

	public String getMethodOfPaymentTransportChargesCode() {
		return methodOfPaymentTransportChargesCode.getValue();
	}

	public void setMethodOfPaymentTransportChargesCode(
			String methodOfPaymentTransportChargesCode) {
		this.methodOfPaymentTransportChargesCode.setValue(methodOfPaymentTransportChargesCode);
	}

	public String getMethodOfPaymentCodeListQualifier() {
		return methodOfPaymentCodeListQualifier.getValue();
	}

	public void setMethodOfPaymentCodeListQualifier(String methodOfPaymentCodeListQualifier) {
		this.methodOfPaymentCodeListQualifier.setValue(methodOfPaymentCodeListQualifier);
	}

	public String getMethodOfPaymentCodeListResponsibleAgency() {
		return methodOfPaymentCodeListResponsibleAgency.getValue();
	}

	public void setMethodOfPaymentCodeListResponsibleAgency(
			String methodOfPaymentCodeListResponsibleAgency) {
		this.methodOfPaymentCodeListResponsibleAgency.setValue(methodOfPaymentCodeListResponsibleAgency);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}
}
