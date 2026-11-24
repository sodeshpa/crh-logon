/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.services.utils.LogonEventContextBean;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;

/**
 * @author AdNovum Informatik AG
 */
@AdnTrace
public abstract class LogonEventProcessor extends LogonEventRequestHandler {

	public abstract LogonEventResponse processRequest(LogonEventRequest eventBean, boolean directCall);

	/**
	 * @param eventBean   : the eventbean for the request
	 * @param directCall  : set this to false if the handler is not called from a direct webservice call but indirectly from
	 *                    logon
	 * @param contextBean : if the call is indirect you need to specify a context for the call
	 * @return response to the eventbean
	 */
	public LogonEventResponse handle(LogonEventRequest eventBean, boolean directCall, LogonEventContextBean contextBean) {
		LogonEventResponse result;

		if (!directCall) {
			this.contextBean = contextBean;
		}

		locale = getLocale(eventBean);
		validate(eventBean);

		result = processRequest(eventBean, directCall);
		if (result == null) {
			throw newTechnicalError("Message not yet implemented!");
		}
		return result;
	}

	protected LogonEventResponse perform(LogonEventRequest eventBean) {
		return processRequest(eventBean, true);
	}
}
