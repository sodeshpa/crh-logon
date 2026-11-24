/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.exception;

import com.crh.logon.utils.ExceptionUtil;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public final class WebservicesExceptionHandler {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(WebservicesExceptionHandler.class);

	/**
	 * @param exception
	 * @param serviceName
	 * @param operationName
	 */
	protected static final void log(Exception exception, String serviceName,
			String operationName) {
		LOG.error("Service: " + serviceName + "; Operation: " + operationName
				+ "FAILED!; \\nDetails: " + exception);

		// Information about the caller of the 'handle' method.
		int delta = 2;
		StackTraceElement st = new Throwable().getStackTrace()[delta];
		String stackMsg = st.getClassName() + "." + st.getMethodName() + "("
				+ st.getFileName() + ":" + st.getLineNumber() + ")";
		LOG.error("Exception handled at " + stackMsg, exception);
	}

	/**
	 * @param throwable
	 * @param serviceName
	 * @param operationName
	 * @return
	 */
	public static final String handle(Throwable throwable, String serviceName,
			String operationName) {
		// [[
		String result = null;
		Object[][] params = {{"throwable", throwable},
							{"serviceName", serviceName},
							{"operationName", operationName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(WebservicesExceptionHandler.class, "handle", params);
		}
		// ]]
		try {
			WebservicesException resException = null;
			
			if (throwable instanceof Error) {
				resException = new WebservicesException(
						WebservicesException.TYPE_UNCHECKED_ERROR, throwable);
			} 
			else if (throwable instanceof WebservicesException) {
				resException = (WebservicesException) throwable;
			} 
			else {
				resException = new WebservicesException(throwable);
			}
			
			ExceptionUtil.errException(LOG, resException, params);
			
			// All the exceptions have to be suppressed in order to be able to
			// send a response to the client of the webservice. This response
			// informs the client about the error and it contains the details of
			// the exception.
			// throw resException;
			
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
