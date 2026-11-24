/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.sirocco20;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author AdNovum Informatik AG
 */
public final class TextMessageBean implements Serializable {

	private final String principal;
	private final String credential;
	private final String serviceNumber;
	
	private final Collection<String> numbers;
	private final String message;

	/**
	 * Default constructor.
	 * 
	 * @param number
	 * @param message
	 */
	public TextMessageBean(String principal, String credential, String serviceNumber, Collection<String> numbers, String message) {
		this.principal = principal;
		this.credential = credential;
		this.serviceNumber = serviceNumber;
		this.numbers = numbers;
		this.message = message;
	}

	/**
	 * @return
	 */
	String getPrincipal() {
		return this.principal;
	}

	/**
	 * @return
	 */
	String getCredential() {
		return this.credential;
	}

	/**
	 * @return
	 */
	String getServiceNumber() {
		return this.serviceNumber;
	}
	
	/**
	 * @return
	 */
	String getMessage() {
		return this.message;
	}

	/**
	 * @return
	 */
	Collection<String> getNumbers() {
		return this.numbers;
	}
	
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("principal", principal);
		builder.append("credential", "*****");
		builder.append("numbers", numbers);
		builder.append("message", message);
		return builder.toString();
	}

}
