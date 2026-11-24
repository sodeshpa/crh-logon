/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;



/**
 * @author AdNovum Informatik AG
 */
public class STS extends Tag {
	
	private DataElementMeta statusCategory = new DataElementMeta(0, 0, false);
	private DataElementMeta statusDescriptionCode = new DataElementMeta(1, 0, false);
	private DataElementMeta[] dataElementMetas = { statusCategory, statusDescriptionCode };
	
	public STS() {
		
		super("STS", "Status");
	}
	
	public String getStatusCategory() {
		return statusCategory.getValue();
	}

	public void setStatusCategory(String statusCategory) {
		this.statusCategory.setValue(statusCategory);
	}

	public String getStatusDescriptionCode() {
		return statusDescriptionCode.getValue();
	}

	public void setStatusDescriptionCode(String statusDescriptionCode) {
		this.statusDescriptionCode.setValue(statusDescriptionCode);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}
}
