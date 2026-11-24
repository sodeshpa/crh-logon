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
public class EichbelegDTO implements DTOMarker {
	
	private final String text;
	
	public EichbelegDTO(String text) {
		super();
		this.text = text;
	}

	public String getText() {
		return text;
	}

}
