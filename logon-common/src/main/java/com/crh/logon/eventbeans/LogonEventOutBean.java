/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.eventbeans;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author AdNovum Informatik AG
 */
public class LogonEventOutBean implements Serializable {
	
	private Long id;
	private Long eventID;
	private String eventType;
	private String senderPDAId;
	private Date sendersTime;
	private BigInteger statusCode;
	private String statusContextValue;
	private String sessionId;
	private String statusTxt;
	private Object responseData;
	
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

	public String getStatusContextValue() {
		return statusContextValue;
	}

	public void setStatusContextValue(String statusContextValue) {
		this.statusContextValue = statusContextValue;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigInteger getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(BigInteger statusCode) {
		this.statusCode = statusCode;
	}

    public String getStatusTxt() {
		return statusTxt;
	}

	public void setStatusTxt(String statusTxt) {
		this.statusTxt = statusTxt;
	}

	public Object getResponseData() {
		return responseData;
	}

	public void setResponseData(Object responseData) {
		this.responseData = responseData;
	}
}
