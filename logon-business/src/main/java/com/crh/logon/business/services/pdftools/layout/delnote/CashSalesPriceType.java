/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.delnote;

/**
 * This enum defines the possible price type values
 * for the cash sale document.
 * 
 * @author AdNovum Informatik AG
 */
public enum CashSalesPriceType {

	STANDARD(0),
	BULGARIAN(1),
	CZECH(2);

	private final int priceTypeCode;

	private CashSalesPriceType(int priceTypeCode) {
		this.priceTypeCode = priceTypeCode;
	}

	public int getPriceTypeCode() {
		return priceTypeCode;
	}
}
