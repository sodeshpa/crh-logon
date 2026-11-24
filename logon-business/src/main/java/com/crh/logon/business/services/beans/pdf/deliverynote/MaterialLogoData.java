/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public class MaterialLogoData implements Serializable {
	private static final long serialVersionUID = -5925876260104808626L;
	
	private LogoLayout logoLayout;

	List<LogoData> logoList;
	
	public boolean renderLogos() {
		return (logoList != null && !logoList.isEmpty());
	}

	public List<LogoData> getLogoList() {
		return logoList;
	}

	public void setLogoList(List<LogoData> logoList) {
		this.logoList = logoList;
	}

	public LogoLayout getLogoLayout() {
		return logoLayout;
	}

	public void setLogoLayout(LogoLayout logoLayout) {
		this.logoLayout = logoLayout;
	}
}
