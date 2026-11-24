/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

/**
 * @author AdNovum Informatik AG
 */
public class CUX extends Tag {

	private DataElementMeta currencyDetailsQualifier = new DataElementMeta(0, 0, false);
	private DataElementMeta currencyCode = new DataElementMeta(0, 1, false);
	private DataElementMeta[] dataElementMetas = { currencyDetailsQualifier, currencyCode };
	
	public CUX() {
		
		super("CUX", "Currencies");
	}
	
	public String getCurrencyDetailsQualifier() {
		return currencyDetailsQualifier.getValue();
	}
	
	public void setCurrencyDetailsQualifier(String currencyDetailsQualifier) {
		this.currencyDetailsQualifier.setValue(currencyDetailsQualifier);
	}
	
	public String getCurrencyCode() {
		return currencyCode.getValue();
	}
	
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode.setValue(currencyCode);
	}
	
	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}	
}
