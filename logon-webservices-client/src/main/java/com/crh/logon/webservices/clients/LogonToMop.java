/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

import org.datacontract.schemas._2004._07.comsvr.ArrayOfLOGONEvent;
import org.datacontract.schemas._2004._07.comsvr.LOGONEvent;
import org.datacontract.schemas._2004._07.comsvr.LOGONEventStatus;
import org.datacontract.schemas._2004._07.comsvr.LOGONMessage;

import com.crh.logon.business.services.eventbeans.utils.LogonEventOutBeanToWebservice;
import com.crh.logon.constants.Constant;
import com.crh.logon.eventbeans.LogonEventOutBean;
import com.crh.logon.eventbeans.LogonMessageOutBean;
import com.crh.logon.to.mop.Consume;
import com.crh.logon.to.mop.ConsumeResponse;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.webservices.clients.util.ClientConfig;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public final class LogonToMop {
	private final static JcanLogger LOG = JcanLoggerFactory.getLogger(LogonToMop.class);
	private final static String CONFIG_LABEL = "mop";
	
	// artificial response to avoid arrayoutofbound exception
	private static LOGONEventStatus EVENT_NO_RESPONSE = new LOGONEventStatus();
	static {
	    EVENT_NO_RESPONSE.setCode(Constant.STATUS_CODE_LOGON_EVENT_TECHNICAL_ERROR_CD);
	    EVENT_NO_RESPONSE.setTxt("No response arrived from ComSrv");
	}	
	
	private static LOGONEventStatus getLOGONEventStatus(ConsumeResponse response, Long eventId) {
		
		if ((eventId == null) || 
				(response == null) || 
				(response.getConsumeResult() == null) || 
				(response.getConsumeResult().getLogonEventStatusList() == null) || 
				(response.getConsumeResult().getLogonEventStatusList().getLOGONEventStatus() == null)) {			
			return EVENT_NO_RESPONSE;
		}
		
		for (LOGONEventStatus eventStatus: response.getConsumeResult().getLogonEventStatusList().getLOGONEventStatus()) {
			if (eventStatus.getEventID() == eventId.intValue()) {				
				return eventStatus;
			}
		}
		
		return EVENT_NO_RESPONSE;
	}
	
		
	/**
	 * Sets up the environment for the call to the mop 
	 * @param bean
	 * @param configBean
	 * @return
	 */
	public static LogonMessageOutBean sendToMop(LogonMessageOutBean bean, ConfigBean configBean) {
		// [[
		LogonMessageOutBean result = null;
		Object[][] params = {{"bean", bean},
							{"configBean", configBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToMop.class, "sendToMop", params);
		}
		// ]]
		try {
			final Consume requestObject = new Consume();
			final LOGONMessage message = createOutgoingMessage(bean);
			requestObject.setLOGONMessage(message);
			
			final ClientConfig clientConfig = new ClientConfig(configBean, CONFIG_LABEL, false, true);
			final EnhancedLOGONMessagePortStub stub = new EnhancedLOGONMessagePortStub(clientConfig);
			final ConsumeResponse response = stub.consume(requestObject);
			
			bean.setMessageStatus(response.getConsumeResult().getMessageStatus());

			@SuppressWarnings("unchecked")
			final List<LogonEventOutBean> logonEventBeans = bean.getLogonEventBeans();
			if (bean.getLogonEventBeans() != null) {
				for (LogonEventOutBean eventBean : logonEventBeans) {
					LOGONEventStatus eventStatus = getLOGONEventStatus(response, eventBean.getEventID());
					eventBean.setStatusCode(BigInteger.valueOf((long) eventStatus.getCode()));	
				    eventBean.setStatusTxt(eventStatus.getTxt());
				    eventBean.setStatusContextValue(eventStatus.getContextValue());
				}
			}
			
			return result = bean;
		}
		catch (Exception e) {
			if (e instanceof LogonException) {
				throw (LogonException) e;
			}
			else {
				throw new LogonException(e, Reason.RES_ERROR_WSCLIENT);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * creates the 
	 * @param bean
	 * @return
	 */
	private static LOGONMessage createOutgoingMessage(LogonMessageOutBean bean) {
		// [[
		LOGONMessage result = null;
		Object[][] params = {{"bean", bean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToMop.class, "createOutgoingMessage", params);
		}
		// ]]
		try {
			// FIXME (nina Mar 18, 2009): add an exception. 
			//Howto handle exceptions for missing values in eventbeans?
		    result = new LOGONMessage();
			
			//set header values
			// TODO (nina Mar 19, 2009): get PDAId for this session
		    result.setTargetPDAID(bean.getSenderPDAId());
		    result.setSendersTime(Calendar.getInstance());
		    result.setSessionID(bean.getSessionId());

			@SuppressWarnings("unchecked")
			final List<LogonEventOutBean> logonEventBeans = bean.getLogonEventBeans();
			final ArrayOfLOGONEvent events = new ArrayOfLOGONEvent();
		    LOGONEvent logonEvent;
		    
		    for (LogonEventOutBean eventBean : logonEventBeans) {
		    	logonEvent = new LOGONEvent();
                LogonEventOutBeanToWebservice.convert(eventBean, logonEvent);                
                events.addLOGONEvent(logonEvent);
		    }
		    result.setLogonEventList(events);
		    
			return result;
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
