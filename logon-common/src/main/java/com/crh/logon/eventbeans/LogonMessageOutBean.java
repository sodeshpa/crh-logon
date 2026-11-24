/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.eventbeans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @author AdNovum Informatik AG
 */
public class LogonMessageOutBean implements Serializable {

	private String senderPDAId;
	private Date sendersTime;
	private String sessionId;
	private List logonEventBeans;
	private int messageStatus;
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * Identifikation des Gerätes, entspricht Adressierung, welche für den gegenläufigen Call zu diesem Gerät benutzt werden
	 * muss
	 */
	public String getSenderPDAId() {
		return senderPDAId;
	}
	
	/**
	 * Identifikation des Gerätes, entspricht Adressierung, welche für den gegenläufigen Call zu diesem Gerät benutzt werden
	 * muss
	 */
	public void setSenderPDAId(String senderPDAId) {
		this.senderPDAId = senderPDAId;
	}
	
	/**
	 * Systemlokale Zeit des sendenden Gerätes
	 */
	public Date getSendersTime() {
		return sendersTime;
	}
	
	/**
	 * Systemlokale Zeit des sendenden Gerätes 
	 */
	public void setSendersTime(Date sendersTime) {
		this.sendersTime = sendersTime;
	}

	public int getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(int messageStatus) {
		this.messageStatus = messageStatus;
	}

	public List getLogonEventBeans() {
		return logonEventBeans;
	}

	public void setLogonEventBeans(List logonEventBeans) {
		this.logonEventBeans = logonEventBeans;
	}
}
