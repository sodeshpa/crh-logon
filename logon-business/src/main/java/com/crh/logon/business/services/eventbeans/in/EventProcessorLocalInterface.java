/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in;

import javax.ejb.Local;

import com.crh.logon.business.services.utils.LogonEventContextBean;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;

/**
 * @author AdNovum Informatik AG
 */
@Local
public interface EventProcessorLocalInterface extends RequestHandlerLocalInterface {

	public LogonEventResponse handle(LogonEventRequest eventBean, boolean directCall, LogonEventContextBean contextBean);
}
