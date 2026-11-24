package com.crh.logon.business.services.eventbeans.in;

import java.math.BigInteger;

import javax.ejb.Local;

import com.crh.logon.business.services.utils.LogonEventContextBean;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;

@Local
public interface RequestHandlerLocalInterface {

	public LogonEventResponse handle(LogonEventRequest eventBean, LogonEventContextBean contextBean);
	public String getResponseMessage(LogonEventRequest request, BigInteger code, Object[] fmtParams);
}
