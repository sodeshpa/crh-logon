/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.mop.webservices;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.crh.logon.utils.time.DateTimeFormat;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.lang.text.StrBuilder;

import com.crh.logon.converters.ConversionException;
import com.crh.logon.eventbeans.in.LogonEventRequestFactory;
import com.holcim.logon.eventbeans.in.mop.MopRequest;
import com.holcim.logon.eventbeans.in.mop.MopRequestMessage;
import com.holcim.logon.eventbeans.in.mop.MopResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.MopResponseMessage;
import com.holcim.logon.eventbeans.in.mop.MopResponseMessageCode;
import com.crh.logon.exception.WebservicesExceptionHandler;
import com.crh.logon.mop.util.EventBeansUtil;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.ServiceLocatorProvider;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class MOPEventConsumer implements MOPEventConsumerInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MOPEventConsumer.class);
	
	private static final String OPERATION_MOP_EVENT = "consume";
	
	public MOPEventConsumer() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "MOPEventConsumer");
		}
		// ]]
		try {
			if (logDebugEnabled) { // careful! the whole next block only prints information!
				MessageContext messageContext = MessageContext.getCurrentMessageContext();
				Iterator iter = messageContext.getPropertyNames();
				for (Iterator iterator = iter; iterator.hasNext();) {
					String key = (String) iterator.next();
					LOG.debug("1. " + key + ": " + messageContext.getProperty(key));
				}
				iter = messageContext.getOperationContext().getPropertyNames();
				for (Iterator iterator = iter; iterator.hasNext();) {
					String key = (String) iterator.next();
					LOG.debug("2. " + key + ": " + messageContext.getOperationContext().getProperty(key));
				}
			
				iter = messageContext.getConfigurationContext().getPropertyNames();
				for (Iterator iterator = iter; iterator.hasNext();) {
					String key = (String) iterator.next();
					LOG.debug("3. " + key + ": " + messageContext.getConfigurationContext().getProperty(key));
				}
			
				iter = messageContext.getServiceGroupContext().getPropertyNames();
				for (Iterator iterator = iter; iterator.hasNext();) {
					String key = (String) iterator.next();
					LOG.debug("4. " + key + ": " + messageContext.getServiceGroupContext().getProperty(key));
				}
			
				iter = messageContext.getServiceContext().getPropertyNames();
				for (Iterator iterator = iter; iterator.hasNext();) {
					String key = (String) iterator.next();
					LOG.debug("5. " + key + ": " + messageContext.getServiceContext().getProperty(key));
				}
			}
			else {
				MessageContext messageContext = MessageContext.getCurrentMessageContext();
				LOG.debug("REMOTE_ADDR: " + messageContext.getProperty(MessageContext.REMOTE_ADDR) + 
						 " CHARACTER_SET_ENCODING: " + messageContext.getProperty(MessageContext.CHARACTER_SET_ENCODING));
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	
	private MOPEventStatus_type0 getMOPEventStatus(MOPEventStatus_type0[] statuses, Long eventId) {
		
		if (eventId == null) {
			
			return null;
		}
		for (MOPEventStatus_type0 status: statuses) {
			if ( (eventId.longValue() == status.getEventID().longValue())) {
				
				return status;
			}
		}
		
		return null;
	}
	
	
	public MOPMessageStatus consume(MOPMessage logonMOPMessage) {
		// [[
		MOPMessageStatus result = null;
		Object[][] params = {{"logonMOPMessage", logonMOPMessage}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "consume", params);
		}
		// ]]
		result = new MOPMessageStatus();
		try {
			// Initialize the return structure
			MOPEvent_type0[] events = logonMOPMessage.getMOPEvent();
			MOPEventStatus_type0[] statuses = new MOPEventStatus_type0[events.length];
			for (int i=0;i<events.length;i++) {
				statuses[i] = new MOPEventStatus_type0();
				statuses[i].setEventID(events[i].getEventID());
			}
			result.setMOPEventStatus(statuses);
			String defaultStatusTxt = "";
			
			try {
				// Write mopEvent received from WS client into LOG File
				logMopEventConsumeObject(logonMOPMessage);
			
				// Create the message
				MopRequestMessage mopRequestMessage = new MopRequestMessage();
				mopRequestMessage.setSenderPdaId(logonMOPMessage.getSenderPDAID());
				if (!StringUtils.empty(logonMOPMessage.getSessionID())) {
					mopRequestMessage.setSessionId(Long.parseLong(logonMOPMessage.getSessionID()));
				}
				if (logonMOPMessage.getSendersTime() != null) {
					mopRequestMessage.setSendersTime(logonMOPMessage.getSendersTime().getTime());
				}
				// Create the event beans
				Map mopEventData = new HashMap();
				for (MOPEvent_type0 logonMOPEvent: events) {
					//create mopEvent bean
	                HashMap mopData = EventBeansUtil.data2Map(logonMOPEvent);
	                try {
	                	MopRequest mopRequest = createLogonEventBean(logonMOPMessage, logonMOPEvent, mopData);
	                	mopRequestMessage.getMopRequests().add(mopRequest);
		                mopEventData.put(mopRequest.getEventId(), mopData);
	                }
	                catch (ConversionException ce) {
	                	MOPEventStatus_type0 status = getMOPEventStatus(statuses, Long.valueOf(logonMOPEvent.getEventID().longValue()));
	                	status.setCode(MopResponseCode.MANDATORY_FIELD_ERROR.value());
	                	status.setContextValue(ce.getPropertyName());
	                	status.setTxt(ServiceLocatorProvider.getServiceLocator().getLogonEventService().getMopStatusTxt(Locale.getDefault(), MopResponseCode.MANDATORY_FIELD_ERROR.value(), new Object[] { ce.getPropertyName(), ce.getPropertyValue() }));
	                }
				}
				// Process the message
				MopResponseMessage mopResponseMessage = ServiceLocatorProvider.getServiceLocator().getMopEventService().handleLogonMessage(mopRequestMessage, mopEventData);
				
				// Set the return values
				for (Iterator it=mopResponseMessage.getMopEventStatusList().iterator();it.hasNext();) {
					MopResponse response = (MopResponse)it.next();
					MOPEventStatus_type0 status = getMOPEventStatus(statuses, response.getEventId());
					if (status != null) {
						status.setCode(response.getCode());
						status.setTxt(response.getTxt());
						status.setContextValue(response.getContextValue());
					}
				}
				result.setMessageStatus(mopResponseMessage.getMessageStatus());
			}
			catch (Throwable t) {
				ExceptionUtil.errException(LOG, t, params);
				result.setMessageStatus(MopResponseMessageCode.TECHNICAL_ERROR.value());
				if (t instanceof LogonException) {
					LogonException ex = (LogonException)t;
					defaultStatusTxt = ex.getMessage() + " (" + ex.getId() + ")";
				}
				else {
					defaultStatusTxt = WebservicesExceptionHandler.handle(t, getClass().getSimpleName(), OPERATION_MOP_EVENT);
				} 
			}
			
			StrBuilder logMsg = null;
			if (LOG.isInfoEnabled()) {
				logMsg = new StrBuilder();
				logMsg.appendln("IN: MOP Message PDA=" + logonMOPMessage.getSenderPDAID() + 
					", session=" + logonMOPMessage.getSessionID() + 
					", status=" + result.getMessageStatus());
			}
			// Set the mandatory status fields
			for (int i=0;i<statuses.length;i++) {
				MOPEventStatus_type0 status = statuses[i];
				MOPEvent_type0 event = events[i];
				if (status.getCode() == null) {
					status.setCode(MopResponseMessageCode.TECHNICAL_ERROR.value());
				}
				if (status.getTxt() == null) {
					status.setTxt(defaultStatusTxt);
				}
				if (status.getContextValue() == null) {
					status.setContextValue("");
				}
				if (logMsg != null) {
					logMsg.appendln("IN: MOP Event PDA=" + logonMOPMessage.getSenderPDAID() +
							", status=" + status.getCode() + 
							", text=" + status.getTxt() +
	                        ", contextValue=" + status.getContextValue() +
	                        ", event=" + status.getEventID() + 
	                        " (" + event.getEventName() + ")");
				}
			}
			if (logMsg != null) {
				LOG.info(logMsg.toString());
			}
            
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
	
	
	private void logMopEventConsumeObject(MOPMessage logonMOPMessage) {
		// [[
		Object[][] params = {{"logonMOPMessage", logonMOPMessage}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "logMopEventConsumeObject", params);
		}
		// ]]
		try {
            if (LOG.isInfoEnabled()) {
            	StrBuilder logMsg = new StrBuilder();
            	logMsg.append("MopMessageConsume-Object is: ");
            	logMsg.append("senderPDAID = \"" + logonMOPMessage.getSenderPDAID() + "\", ");
            	logMsg.append("sessionID = \"" + logonMOPMessage.getSessionID() + "\", ");
            	logMsg.append("sendersTime = \"" + ((logonMOPMessage.getSendersTime()!=null)? DateTimeFormat.asLogonDayMonthYear(logonMOPMessage.getSendersTime().getTime()):"") + "\". ");
			
                MOPEvent_type0 [] events = logonMOPMessage.getMOPEvent();
                for (int i = 0; i < events.length; i++) {
                    MOPEvent_type0 event = events[i];
                    logMsg.append("MopEvent is: ");
                    logMsg.append("eventID = \"" + event.getEventID() + "\", ");
                    logMsg.append("eventType = \"" + event.getEventName() + "\", ");
                    logMsg.append("geoX = \"" + event.getGeoX() + "\", ");
                    logMsg.append("geoY = \"" + event.getGeoY() + "\", ");
                    logMsg.append("speed = \"" + event.getSpeed() + "\", ");
                    logMsg.append("course = \"" + event.getCourse() + "\", ");
                    logMsg.append("geoTime = \"" + ((event.getGeoTime()!=null)? DateTimeFormat.asLogonDayMonthYear(event.getGeoTime().getTime()):"") + "\". ");
                
                    HashMap mopData = EventBeansUtil.data2Map(event);
				
                    logMsg.append("mopData are: ");
    				Iterator iter = mopData.entrySet().iterator();
    				while(iter.hasNext()){
    					Entry entry = (Entry) iter.next();
    					logMsg.append("(name=\"" + entry.getKey() + "\"" + ",value=\"" + entry.getValue() + "\") ");
    				}
                }
                LOG.info(logMsg.toString());
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	
	private MopRequest createLogonEventBean(MOPMessage logonMOPMessage, MOPEvent_type0 mopEvent, HashMap mopData) {
		// [[
		MopRequest result = null;
		Object[][] params = {{"logonMOPMessage", logonMOPMessage},
							{"mopEvent", mopEvent},
							{"mopData", mopData}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createLogonEventBean", params);
		}
		// ]]
		try {
			String event = mopEvent.getEventName();
			MopRequest bean = (MopRequest)LogonEventRequestFactory.MopFactory.create(event, mopData);
			bean.setEventId(new Long(mopEvent.getEventID().toString()));
			bean.setGeoX(mopEvent.getGeoX());
			bean.setGeoY(mopEvent.getGeoY());
			bean.setSenderInfo(logonMOPMessage.getSenderPDAID());
			bean.setSenderPdaId(logonMOPMessage.getSenderPDAID());
			if (!StringUtils.empty(logonMOPMessage.getSessionID())) {
				bean.setSessionId(Long.parseLong(logonMOPMessage.getSessionID()));
			}
			if (mopEvent.getSpeed() != null) {
				bean.setSpeed(mopEvent.getSpeed().longValue());
			}
			if (mopEvent.getCourse() != null) {
				bean.setCourse(mopEvent.getCourse().longValue());
			}
            if (mopEvent.getEventTime() != null) {
                bean.setEventTime(mopEvent.getEventTime().getTime());
            }
            if (mopEvent.getGeoTime() != null) {
				bean.setGeoTime(mopEvent.getGeoTime().getTime());
            }
            
			return result = bean;
		}
		catch (RuntimeException re) {
			throw re;
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
