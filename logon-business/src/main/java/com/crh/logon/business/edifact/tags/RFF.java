/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;


/**
 * @author AdNovum Informatik AG
 */
public class RFF extends Tag {

	private DataElementMeta functionCodeQualifier = new DataElementMeta(0, 0, true);
	private DataElementMeta referenceId = new DataElementMeta(0, 1, true);
	private DataElementMeta[] dataElementMetas = { functionCodeQualifier, referenceId };
	
	public RFF() {
		
		super("RFF","Reference");
	}

	public String getFunctionCodeQualifier() {
		return functionCodeQualifier.getValue();
	}

	public void setFunctionCodeQualifier(String functionCodeQualifier) {
		this.functionCodeQualifier.setValue(functionCodeQualifier);
	}

	public String getReferenceId() {
		return referenceId.getValue();
	}

	public void setReferenceId(String referenceId) {
		this.referenceId.setValue(referenceId);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}
}
