/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.exception;

import org.apache.axis2.AxisFault;

import com.crh.logon.utils.ExceptionUtil;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public final class WsServerSideExceptionHandler {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WsServerSideExceptionHandler.class);

	/**
	 * @param throwable
	 * @param serviceName
	 * @param operationName
	 * @throws AxisFault
	 */
	public static final void handle(Throwable throwable, String serviceName,
			String operationName) throws AxisFault {
		// [[
		Object[][] params = {{"throwable", throwable},
							{"serviceName", serviceName},
							{"operationName", operationName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(WsServerSideExceptionHandler.class, "handle", params);
		}
		// ]]
		try {
			WsServerSideException resException = null;
			
			if (throwable instanceof Error) {
				resException = new WsServerSideException(
						WsServerSideException.TYPE_UNCHECKED_ERROR, throwable);
			} 
			else if (throwable instanceof WsServerSideException) {
				resException = (WsServerSideException) throwable;
			} 
			else {
				resException = new WsServerSideException(throwable);
			}
			
			ExceptionUtil.errException(LOG, resException, params);
			
			// Exceptions are not suppressed any longer due to changes required by ADN40911.
			// Whenever an exception is encountered, it's translated to a standard 
			// SOAP-Fault using the built-in facilities provided by Axis2. 
			throw AxisFault.makeFault(resException);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

}
