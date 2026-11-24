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
public class WsServerSideException extends RuntimeException {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(WsServerSideException.class);

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
	public WsServerSideException(String message) {
		super(message);
		this.errorId = generateErrorId();
		this.exceptionType = WsServerSideException.TYPE_UNKNOWN;
	}

	/**
	 * @param throwable
	 */
	public WsServerSideException(Throwable throwable) {
		super(throwable.getMessage(), throwable);
		this.errorId = generateErrorId();
		this.exceptionType = WsServerSideException.TYPE_UNKNOWN;
	}

	/**
	 * @param type
	 * @param message
	 */
	public WsServerSideException(int type, String message) {
		super(message);
		this.errorId = generateErrorId();
		this.exceptionType = type;
	}

	/**
	 * @param type
	 * @param throwable
	 */
	public WsServerSideException(int type, Throwable throwable) {
		super(throwable.getMessage(), throwable);
		this.errorId = generateErrorId();
		this.exceptionType = type;
	}

	/**
	 * @param type
	 * @param message
	 * @param throwable
	 */
	public WsServerSideException(int type, String message, Throwable throwable) {
		super(message, throwable);
		this.errorId = generateErrorId();
		this.exceptionType = type;
	}

	/**
	 * @param type
	 * @param callingMethod
	 * @param callingClass
	 * @param message
	 * @throws WsServerSideException
	 */
	public static final void raise(int type, String callingMethod,
			String callingClass, String message) throws WsServerSideException {
		LOG.error("WsServerSideException raised - " + callingClass + "."
				+ callingMethod + ": " + message);
		throw new WsServerSideException(type, message);
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		StringBuffer message = new StringBuffer("ERROR-ID = ");

		message.append(errorId);
		message.append("; ");
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

		LOG.info("WsServerSideException created: " + eid);

		return eid;
	}

	/**
	 * @return
	 */
	private String getMessageLabelByType() {
		switch (exceptionType) {
		case WsServerSideException.TYPE_UNCHECKED_ERROR:
			return "Error in runtime environment";
		case WsServerSideException.TYPE_DB_INCONSISTENCY:
			return "DB-Inconsistency";
		case WsServerSideException.TYPE_INVALID_INPUT:
			return "Invalid input";
		case WsServerSideException.TYPE_SERVICE_CALL:
			return "Error in SAPService";
		case WsServerSideException.TYPE_NO_PERMISSION:
			return "No permission";
		case WsServerSideException.TYPE_BUSINESS_CONSTRAINT:
			return "Violation of business constraints";
		default:
			return "Unknown";
		}
	}

}