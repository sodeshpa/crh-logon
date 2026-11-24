/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;

import com.crh.logon.utils.ConfigBean;

/**
 * @author AdNovum Informatik AG
 */
public class ConfigReloadResult implements Serializable {

	private String source;
	private ConfigBean configBean;
	private Throwable throwable;
	
	public ConfigReloadResult(String source) {
		
		this.source = source;
	}
	
	public String getSource() {
		return source;
	}
		
	public ConfigBean getConfigBean() {
		return configBean;
	}
	
	public void setConfigBean(ConfigBean configBean) {
		this.configBean = configBean;
	}
	
	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
}
