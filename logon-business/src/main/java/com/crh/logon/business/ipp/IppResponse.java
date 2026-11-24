/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.ipp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 */
public class IppResponse {

	private String httpStatusCode;
	private List httpHeaders = new ArrayList();
	private int statusCode = -1;
	private int versionMajor = -1;
	private int versionMinor = -1;

	IppResponse() {
	}

	public List getHttpHeaders() {
		return httpHeaders;
	}

	public void setHttpHeaders(List httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getVersionMajor() {
		return versionMajor;
	}

	public void setVersionMajor(int versionMajor) {
		this.versionMajor = versionMajor;
	}

	public int getVersionMinor() {
		return versionMinor;
	}

	public void setVersionMinor(int versionMinor) {
		this.versionMinor = versionMinor;
	}
	
	public String getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(String httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	public boolean isSuccessful() {
		
		return ( (httpStatusCode != null) && (httpStatusCode.startsWith("2")) && ((statusCode >= 0x0000) && (statusCode <= 0x00FF)) );
	}
}
