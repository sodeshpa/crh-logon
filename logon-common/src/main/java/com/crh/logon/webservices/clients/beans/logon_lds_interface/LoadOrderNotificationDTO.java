/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.logon_lds_interface;

import com.holcim.logon.eventbeans.in.logon_lds_interface.LoadOrderData;
import com.holcim.logon.eventbeans.in.logon_lds_interface.Request;

/**
 * @author AdNovum Informatik AG
 */
public class LoadOrderNotificationDTO extends Request {

	public enum MessageFunction {
		NEW("new"),
		UPDATE("update");
		
		private final String value;
		
		MessageFunction(String v) {
			value = v;
		}
		
		public String value() {
			return value;
		}
	}

	private MessageFunction messageFunction;
	private LoadOrderData loadOrder;
	private String ldsType;

	public MessageFunction getMessageFunction() {
		return messageFunction;
	}
	public void setMessageFunction(MessageFunction messageFunction) {
		this.messageFunction = messageFunction;
	}
	public LoadOrderData getLoadOrder() {
		return loadOrder;
	}
	public void setLoadOrder(LoadOrderData loadOrder) {
		this.loadOrder = loadOrder;
	}
	public String getLdsType() {
		return ldsType;
	}
	public void setLdsType(String ldsType) {
		this.ldsType = ldsType;
	}
}
