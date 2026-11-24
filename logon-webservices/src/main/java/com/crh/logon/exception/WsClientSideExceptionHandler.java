/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.exception;

import java.net.SocketException;
import java.rmi.RemoteException;

import com.crh.logon.utils.ExceptionUtil;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class WsClientSideExceptionHandler {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WsClientSideExceptionHandler.class);

	/**
	 * @param throwable
	 * @param stubName
	 * @param operationName
	 * @return
	 */
	public static final String handle(Throwable throwable, String stubName,
			String operationName) {
		// [[
		String result = null;
		Object[][] params = {{"throwable", throwable},
							{"stubName", stubName},
							{"operationName", operationName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(WsClientSideExceptionHandler.class, "handle", params);
		}
		// ]]
		try {
			WsClientSideException resException = null;
			
			if (throwable instanceof Error) {
				resException = new WsClientSideException(
						WsServerSideException.TYPE_UNCHECKED_ERROR, throwable);
			} 
			else if (throwable instanceof RemoteException) {
				resException = new WsClientSideException(
						WsClientSideException.TYPE_AXIS2_ERROR, throwable);
			} 
			else if (throwable instanceof SocketException) {
				resException = new WsClientSideException(
						WsClientSideException.TYPE_COMMUNICATION_ERROR,
						throwable);
			} 
			else if (throwable instanceof WsClientSideException) {
				resException = (WsClientSideException) throwable;
			} 
			else {
				resException = new WsClientSideException(throwable);
			}
			
			ExceptionUtil.errException(LOG, resException, params);
			
			return result = resException.toString();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

}
