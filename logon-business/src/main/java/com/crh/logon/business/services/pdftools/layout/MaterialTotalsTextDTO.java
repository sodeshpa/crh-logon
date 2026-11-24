/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout;

import com.crh.logon.pdftool.labelprovider.DTOMarker;

/**
 * @author AdNovum Informatik AG
 */
public class MaterialTotalsTextDTO implements DTOMarker {

	private String netPrice;
	private String vat;
	private String grossPrice;
	
	public MaterialTotalsTextDTO(String netPrice, String vat, String grossPrice){
		this.netPrice = netPrice;
		this.vat = vat;
		this.grossPrice = grossPrice;
	}

	/**
	 * @return the netPrice
	 */
	public String getNetPrice() {
		return netPrice;
	}

	/**
	 * @return the vat
	 */
	public String getVat() {
		return vat;
	}

	/**
	 * @return the grossPrice
	 */
	public String getGrossPrice() {
		return grossPrice;
	}

}
