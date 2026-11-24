/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author AdNovum Informatik AG
 */
public class PdfGeneratingResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean isSuccess;
	
	private Set<String> messages;
	
	public PdfGeneratingResult (boolean isSuccess) {
		this.isSuccess = isSuccess;
		messages = new HashSet<String> ();
	}
	
	public void addMessage(String message) {
		messages.add(message);
	}
	
	public PdfGeneratingResult merge (PdfGeneratingResult otherResult) {
		PdfGeneratingResult finalResult = new PdfGeneratingResult(this.isSuccess && otherResult.isSuccess);
		
		finalResult.getMessages().addAll(this.getMessages());
		finalResult.getMessages().addAll(otherResult.getMessages());
		
		return finalResult;
	}

	/**
	 * @return the isSuccess
	 */
	public boolean isSuccess() {
		return isSuccess;
	}

	/**
	 * @param isSuccess the isSuccess to set
	 */
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	/**
	 * @return the messages
	 */
	public Set<String> getMessages() {
		return messages;
	}

	/**
	 * @param messages the messages to set
	 */
	public void setMessages(Set<String> messages) {
		this.messages = messages;
	}
}