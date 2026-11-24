/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.beans;

import java.io.Serializable;

/**
 * Bean used in the unlock external login process.
 * 
 * @author AdNovum Informatik AG
 */
public class UnlockExtLoginBean implements Serializable {

	private static final long serialVersionUID = 4432852093144457732L;
	public static final String UNLOCK_SUCCESSFUL = "application.unlock.ext.login.successful";
	public static final String UNLOCK_FAILED = "application.unlock.ext.login.unsuccessful";
	
	private String extLogin = null;
	private String lock = null;
	private String password = null;
	private String result = null;
	

	public String getExtLogin() {
		return extLogin;
	}

	public void setExtLogin(String extLogin) {
		this.extLogin = extLogin;
	}

	public String getLock() {
		return lock;
	}

	public void setLock(String lock) {
		this.lock = lock;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
