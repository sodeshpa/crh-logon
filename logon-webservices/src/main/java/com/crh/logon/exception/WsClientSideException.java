/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.exception;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class WsClientSideException extends RuntimeException {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WsClientSideException.class);

	public static final int TYPE_UNKNOWN = -1;
	public static final int TYPE_UNCHECKED_ERROR = 0;
	public static final int TYPE_AXIS2_ERROR = 1;
	public static final int TYPE_COMMUNICATION_ERROR = 2;
	public static final int TYPE_SERVICE_ERROR = 3;

	private int exceptionType = TYPE_UNKNOWN;

	/**
	 * @param message
	 */
	public WsClientSideException(String message) {
		super(message);
		this.exceptionType = WsClientSideException.TYPE_UNKNOWN;
	}

	/**
	 * @param throwable
	 */
	public WsClientSideException(Throwable throwable) {
		super(throwable.getMessage(), throwable);
		this.exceptionType = WsClientSideException.TYPE_UNKNOWN;
	}

	/**
	 * @param type
	 * @param message
	 */
	public WsClientSideException(int type, String message) {
		super(message);
		this.exceptionType = type;
	}

	/**
	 * @param type
	 * @param throwable
	 */
	public WsClientSideException(int type, Throwable throwable) {
		super(throwable.getMessage(), throwable);
		this.exceptionType = type;
	}

	/**
	 * @param type
	 * @param message
	 * @param throwable
	 */
	public WsClientSideException(int type, String message, Throwable throwable) {
		super(message, throwable);
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
			String callingClass, String message) throws WsClientSideException {
		LOG.error("WsClientSideException raised - " + callingClass + "."
				+ callingMethod + ": " + message);
		throw new WsClientSideException(type, message);
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		StringBuffer message = new StringBuffer("");

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
	private String getMessageLabelByType() {
		switch (exceptionType) {
		case WsClientSideException.TYPE_UNCHECKED_ERROR:
			return "Error in runtime environment";
		case WsClientSideException.TYPE_AXIS2_ERROR:
			return "Error in webservice-host (AXIS2)";
		case WsClientSideException.TYPE_COMMUNICATION_ERROR:
			return "Error in communication with webservice-host (AppServer)";
		case WsClientSideException.TYPE_SERVICE_ERROR:
			return "Error in the webservice (server-side)";
		default:
			return "Unknown";
		}
	}

}