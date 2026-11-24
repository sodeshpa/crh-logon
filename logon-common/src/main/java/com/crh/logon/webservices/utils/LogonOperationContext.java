/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.utils;

import java.io.Serializable;
import java.util.Date;

import com.crh.logon.utils.ConfigBean;

/**
 * @author AdNovum Informatik AG
 */
public class LogonOperationContext implements Serializable {

	private ConfigBean configBean;
	private String serviceName;
	private boolean serverSide;
	private Date date;
	private boolean suppressDumping;
	private Object[] substVariables;

	public Object[] getSubstVariables() {
		return substVariables;
	}

	public void setSubstVariables(Object[] substVariables) {
		this.substVariables = substVariables;
	}

	public boolean isSuppressDumping() {
		return suppressDumping;
	}

	public void setSuppressDumping(boolean suppressDumping) {
		this.suppressDumping = suppressDumping;
	}

	public ConfigBean getConfigBean() {
		return configBean;
	}

	public void setConfigBean(ConfigBean configBean) {
		this.configBean = configBean;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public boolean isServerSide() {
		return serverSide;
	}

	public void setServerSide(boolean serverSide) {
		this.serverSide = serverSide;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
