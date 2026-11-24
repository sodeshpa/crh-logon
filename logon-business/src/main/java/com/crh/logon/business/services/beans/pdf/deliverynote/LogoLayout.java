/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

/**
 * @author AdNovum Informatik AG
 */
public enum LogoLayout {
	MATERIAL_PLANT("materialplant"),
	SAP_NORM_LIST("sapnormlist");

	private final String name;
	
	LogoLayout(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
