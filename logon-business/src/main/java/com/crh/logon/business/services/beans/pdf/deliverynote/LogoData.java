/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.crh.logon.persistence.dto.BlobDTO;

/**
 * @author AdNovum Informatik AG
 */
public class LogoData implements Serializable {
	private static final long serialVersionUID = -4776083527014366375L;
	private boolean isGenericLogo = false;
	
	private String text1;
	private String text2;
	private String text3;
	
	private String logoName;
	private BlobDTO logo;
	
	public boolean renderLogo() {
		return logo != null && logo.getStream() != null;
	}
	
	public boolean renderText1() {
		return StringUtils.isNotEmpty(text1);
	}
	
	public boolean renderText2() {
		return StringUtils.isNotEmpty(text2);
	}
	
	public boolean renderText3() {
		return StringUtils.isNotEmpty(text3);
	}
	
	public boolean renderText() {
		return renderText1() || renderText2() || renderText3();
	}

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
	}

	public String getText3() {
		return text3;
	}

	public void setText3(String text3) {
		this.text3 = text3;
	}

	public String getLogoName() {
		return logoName;
	}

	public void setLogoName(String logoName) {
		this.logoName = logoName;
	}

	public BlobDTO getLogo() {
		return logo;
	}

	public void setLogo(BlobDTO logo) {
		this.logo = logo;
	}

	public boolean isGenericLogo() {
		return isGenericLogo;
	}

	public void setGenericLogo(boolean isGenericLogo) {
		this.isGenericLogo = isGenericLogo;
	}
}
