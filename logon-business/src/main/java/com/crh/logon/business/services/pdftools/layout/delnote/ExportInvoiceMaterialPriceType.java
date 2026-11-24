/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.delnote;

/**
 * This enum defines the possible values which controls 
 * the displaying of the material price type rows for 
 * the export invoice document.
 * 
 * @author AdNovum Informatik AG
 */
public enum ExportInvoiceMaterialPriceType {
	
	DO_NOT_DISPLAY(0),
	DISPLAY(1),
	DISPLAY_AND_EXTRA_LINE_IF(2);

	private final int materialPriceTypeCode;

	private ExportInvoiceMaterialPriceType(int materialPriceTypeCode) {
		this.materialPriceTypeCode = materialPriceTypeCode;
	}

	public int getMaterialPriceTypeCode() {
		return materialPriceTypeCode;
	}	
}
