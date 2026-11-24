/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

/**
 * @author AdNovum Informatik AG
 */
public class MOA extends Tag {

	private DataElementMeta monetaryAmountTypeQualifier = new DataElementMeta(0, 0, true);
	private DataElementMeta monetaryAmount = new DataElementMeta(0, 1, false);
	private DataElementMeta currencyCode = new DataElementMeta(0, 2, false);
	private DataElementMeta[] dataElementMetas = { monetaryAmountTypeQualifier, monetaryAmount, currencyCode };
	
	public MOA() {
		
		super("MOA", "Monetary amount");
	}
	
	public String getMonetaryAmountTypeQualifier() {
		return monetaryAmountTypeQualifier.getValue();
	}
	
	public void setMonetaryAmountTypeQualifier(String monetaryAmountTypeQualifier) {
		this.monetaryAmountTypeQualifier.setValue(monetaryAmountTypeQualifier);
	}
	
	public String getMonetaryAmount() {
		return monetaryAmount.getValue();
	}
	
	public void setMonetaryAmount(String monetaryAmount) {
		this.monetaryAmount.setValue(monetaryAmount);
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
