/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;


/**
 * @author AdNovum Informatik AG
 */
public class FTX extends Tag {
	
	private DataElementMeta textSubjectCodeQualifier = new DataElementMeta(0, 0, true);
	private DataElementMeta freeTextFunctionCode = new DataElementMeta(1, 0, false);
	private DataElementMeta freeTextCoded = new DataElementMeta(2, 0, false);
	private DataElementMeta codeListResponsibleAgency = new DataElementMeta(2, 2, false);
	private DataElementMeta textLiteral1 = new DataElementMeta(3, 0, false);
	private DataElementMeta textLiteral2 = new DataElementMeta(3, 1, false);
	private DataElementMeta[] dataElementMetas = { textSubjectCodeQualifier, freeTextFunctionCode, freeTextCoded, codeListResponsibleAgency, textLiteral1, textLiteral2 };
		
	public FTX() {
		
		super("FTX", "Free Text");
	}

	public String getTextSubjectCodeQualifier() {
		return textSubjectCodeQualifier.getValue();
	}

	public void setTextSubjectCodeQualifier(String textSubjectCodeQualifier) {
		this.textSubjectCodeQualifier.setValue(textSubjectCodeQualifier);
	}

	public String getFreeTextFunctionCode() {
		return freeTextFunctionCode.getValue();
	}

	public void setFreeTextFunctionCode(String freeTextFunctionCode) {
		this.freeTextFunctionCode.setValue(freeTextFunctionCode);
	}

	public String getFreeTextCoded() {
		return freeTextCoded.getValue();
	}

	public void setFreeTextCoded(String freeTextCoded) {
		this.freeTextCoded.setValue(freeTextCoded);
	}

	public String getCodeListResponsibleAgency() {
		return codeListResponsibleAgency.getValue();
	}

	public void setCodeListResponsibleAgency(String codeListResponsibleAgency) {
		this.codeListResponsibleAgency.setValue(codeListResponsibleAgency);
	}

	public String getTextLiteral1() {
		return textLiteral1.getValue();
	}

	public void setTextLiteral1(String textLiteral1) {
		this.textLiteral1.setValue(textLiteral1);
	}

	public String getTextLiteral2() {
		return textLiteral2.getValue();
	}

	public void setTextLiteral2(String textLiteral2) {
		this.textLiteral2.setValue(textLiteral2);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}
}
