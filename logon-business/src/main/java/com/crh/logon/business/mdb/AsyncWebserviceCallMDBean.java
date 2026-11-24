/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.mdb;

import java.util.Iterator;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import ch.nevis.gdi.jpa.ThreadLocalUserInfo;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.persistence.dtos.LogonMessage;
import com.crh.logon.business.services.localinterfaces.MopEventServiceLocalInterface;
import com.crh.logon.business.services.impl.ServiceSupport;
import com.crh.logon.eventbeans.LogonMessageOutBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.LogonForceRollBackMDBException;

/**
 * Usage: AsyncWebserviceCallMDBean.callMdb(yourObject);
 * 
 * @author AdNovum Informatik AG
 */
@MessageDriven
(
		activationConfig =
		{
				@ActivationConfigProperty
				(
						propertyName="destinationType",
						propertyValue="javax.jms.Queue"
				),
				@ActivationConfigProperty
				(
						propertyName="destination",
						propertyValue="queue/MopQueue"
				),
				@ActivationConfigProperty(
						propertyName = "reconnectAttempts",
						propertyValue = "-1"	
				),
				@ActivationConfigProperty(
						propertyName = "setupAttempts", 
						propertyValue = "-1"
				)
		}
)
public class AsyncWebserviceCallMDBean extends ServiceSupport implements MessageListener {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AsyncWebserviceCallMDBean.class);
	
	@EJB private MopEventServiceLocalInterface mopEventService;
	
	public AsyncWebserviceCallMDBean() {
		LOG.debug("AsyncWebserviceCallMDBean created!");
	}
	
	public void onMessage(Message msg) {
		// [[
		Object[][] params = {{"msg", msg}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "onMessage", params);
		}
		// ]]
		try {
			ObjectMessage message = (ObjectMessage) msg;
			LogonMessage eventMsg = (LogonMessage)message.getObject();
			
			// setup thread info
			ThreadLocalUserInfo.setUserId(eventMsg.getUserId());
			ThreadLocalUserInfo.setAccessRestrictions(eventMsg.getAccessRestrictions());
			ThreadLocalUserInfo.setDisableNextAccessRestrictions(eventMsg.getDisableNextAccessRestrictions());
			for (Iterator it=eventMsg.getMessageBeans().iterator();it.hasNext();) {
				try{
					LogonMessageOutBean logonMessage = (LogonMessageOutBean)it.next();
					// send outgoing message
					mopEventService.sendMessage(logonMessage);
				}
				catch (RuntimeException exception) {
					if (exception.getCause() != null) {
						if (exception.getCause() instanceof LogonForceRollBackMDBException) {
							LOG.info("TODO: rollBackRequired at MDB AsyncWebserviceCallMDBean");
							// TODO Mar 11, 2015/timre: rollbackRequired 
						}
					}
				}
			}
		}
		catch (JMSException e) {
			throw new LogonException("Allowing JMS to retry request.", e);
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
