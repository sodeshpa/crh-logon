/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import com.crh.logon.utils.ConfigBean;

/**
 * store the current ConfigBean to get rid of many service calls.
 * 
 * ConfigBeanProvider.getConfigBean() can be used everywhere inside the webapp.
 * 
 * @author AdNovum Informatik AG
 */
@SuppressWarnings("unchecked")
public enum ConfigBeanProvider {
	inst;
	private ConfigBean configBean;
	
	public static ConfigBean getConfigBean() {
		return inst.configBean;
	}
	
	public static void setConfigBean(ConfigBean configBean) {
		inst.configBean = configBean;
	}
}
