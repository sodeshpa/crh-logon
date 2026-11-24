/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

/**
 * @author AdNovum Informatik AG
 */
public class PIA extends Tag {

	private DataElementMeta productIdFunctionQualifier = new DataElementMeta(0, 0, true);
	private DataElementMeta itemNumber = new DataElementMeta(1, 0, true);
	private DataElementMeta itemNumberTypeCode = new DataElementMeta(1, 1, true);
	private DataElementMeta codeListResponsibleAgency = new DataElementMeta(1, 3, true);
	private DataElementMeta[] dataElementMetas = { productIdFunctionQualifier, itemNumber, itemNumberTypeCode, codeListResponsibleAgency };
	
	public PIA() {
		
		super("PIA", "Additional product id");
	}

	public String getProductIdFunctionQualifier() {
		return productIdFunctionQualifier.getValue();
	}

	public void setProductIdFunctionQualifier(String productIdFunctionQualifier) {
		this.productIdFunctionQualifier.setValue(productIdFunctionQualifier);
	}

	public String getItemNumber() {
		return itemNumber.getValue();
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber.setValue(itemNumber);
	}

	public String getItemNumberTypeCode() {
		return itemNumberTypeCode.getValue();
	}

	public void setItemNumberTypeCode(String itemNumberTypeCode) {
		this.itemNumberTypeCode.setValue(itemNumberTypeCode);
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
