/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.exception;

import com.crh.logon.utils.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class WebservicesException extends RuntimeException {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(WebservicesException.class);

	public static final int TYPE_UNKNOWN = -1;

	public static final int TYPE_UNCHECKED_ERROR = 0;

	public static final int TYPE_DB_INCONSISTENCY = 1;

	public static final int TYPE_INVALID_INPUT = 2;

	public static final int TYPE_SERVICE_CALL = 3;

	public static final int TYPE_NO_PERMISSION = 4;

	public static final int TYPE_BUSINESS_CONSTRAINT = 5;

	private String errorId = null;

	private int exceptionType = TYPE_UNKNOWN;

	/**
	 * @param message
	 */
	public WebservicesException(String message) {
		super(message);
		this.errorId = generateErrorId();
		this.exceptionType = WebservicesException.TYPE_UNKNOWN;
	}

	/**
	 * @param throwable
	 */
	public WebservicesException(Throwable throwable) {
		super(throwable.getMessage(), throwable);
		this.errorId = generateErrorId();
		this.exceptionType = WebservicesException.TYPE_UNKNOWN;
	}

	/**
	 * @param type
	 * @param message
	 */
	public WebservicesException(int type, String message) {
		super(message);
		this.errorId = generateErrorId();
		this.exceptionType = type;
	}

	/**
	 * @param type
	 * @param throwable
	 */
	public WebservicesException(int type, Throwable throwable) {
		super(throwable.getMessage(), throwable);
		this.errorId = generateErrorId();
		this.exceptionType = type;
	}

	/**
	 * @param type
	 * @param message
	 * @param throwable
	 */
	public WebservicesException(int type, String message, Throwable throwable) {
		super(message, throwable);
		this.errorId = generateErrorId();
		this.exceptionType = type;
	}

	/**
	 * @param type
	 * @param callingMethod
	 * @param callingClass
	 * @param message
	 * @throws WebservicesException
	 */
	public static final void raise(int type, String callingMethod,
			String callingClass, String message) throws WebservicesException {
		LOG.error("WebservicesException raised - " + callingClass + "."
				+ callingMethod + ": " + message);
		throw new WebservicesException(type, message);
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		StringBuffer message = new StringBuffer("ERROR-ID = ");

		message.append(errorId);
		message.append(";\n ");
		message.append(getMessageLabelByType());
		message.append(": ");
		message.append(super.getMessage());

		return message.toString();
	}

	/**
	 * @return
	 */
	public int getType() {
		return exceptionType;
	}

	/**
	 * @return
	 */
	private String generateErrorId() {
		String eid = StringUtils.generateErrorId();

		LOG.info("WebservicesException created: " + eid);

		return eid;
	}

	/**
	 * @return
	 */
	private String getMessageLabelByType() {
		switch (exceptionType) {
		case WebservicesException.TYPE_UNCHECKED_ERROR:
			return "Error in runtime environment";
		case WebservicesException.TYPE_DB_INCONSISTENCY:
			return "DB-Inconsistency";
		case WebservicesException.TYPE_INVALID_INPUT:
			return "Invalid input";
		case WebservicesException.TYPE_SERVICE_CALL:
			return "Error in SAPService";
		case WebservicesException.TYPE_NO_PERMISSION:
			return "No permission";
		case WebservicesException.TYPE_BUSINESS_CONSTRAINT:
			return "Violation of business constraints";
		default:
			return "Unknown";
		}
	}

}