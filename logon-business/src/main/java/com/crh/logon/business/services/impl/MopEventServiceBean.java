/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.naming.NamingException;

import com.crh.logon.business.services.localinterfaces.LocalServices;
import com.crh.logon.business.services.utils.KeyBasedLockMap;
import org.apache.commons.lang.text.StrBuilder;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.MopEventServiceDispatcher;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.MopEventServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PdaSessionServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.MopContextBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.eventbeans.LogonDataFactory;
import com.crh.logon.eventbeans.LogonEventOutBean;
import com.crh.logon.eventbeans.LogonEventOutBeanFactory;
import com.crh.logon.eventbeans.LogonMessageOutBean;
import com.crh.logon.eventbeans.in.LogonEventRequestFactory;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopRequest;
import com.holcim.logon.eventbeans.in.mop.MopRequestMessage;
import com.holcim.logon.eventbeans.in.mop.MopResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseMessage;
import com.holcim.logon.eventbeans.in.mop.MopResponseMessageCode;
import com.crh.logon.eventbeans.mop.Message;
import com.crh.logon.eventbeans.mop.RefData;
import com.crh.logon.persistence.entities.admin.FuncEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.MopeventDataInEntity;
import com.crh.logon.persistence.entities.common.MopeventDataOutEntity;
import com.crh.logon.persistence.entities.common.MopeventHeaderInEntity;
import com.crh.logon.persistence.entities.common.MopeventHeaderOutEntity;
import com.crh.logon.persistence.entities.common.PdaMessageEntity;
import com.crh.logon.persistence.entities.common.PdaSessionEntity;
import com.crh.logon.persistence.entities.refdata.CurrencyTxEntity;
import com.crh.logon.persistence.entities.refdata.FuelTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.LangTxEntity;
import com.crh.logon.persistence.entities.refdata.MopStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.PdaErrorMessageTxEntity;
import com.crh.logon.persistence.entities.refdata.PdaMarketinfoTxEntity;
import com.crh.logon.persistence.entities.refdata.PdaSessionStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckMaintenanceTxEntity;
import com.crh.logon.persistence.entities.refdata.WaitingtimeReasonTxEntity;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.LogonForceRollBackMDBException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.webservices.clients.LogonToMop;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class MopEventServiceBean extends MopEventServiceDispatcher implements
		MopEventServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MopEventServiceBean.class);

	@EJB
	private MopEventServiceLocalInterface mopEventService;

	@EJB
	private RefDataGenericsServiceLocalInterface referentialDataService;

	@EJB
	private PdaSessionServiceLocalInterface pdaSessionService;

	private static final KeyBasedLockMap<String> MUTEX = new KeyBasedLockMap<String>();

	/**
	 * Stores the mopevent received by the webservice LogonMopEventConsuemr to the log table
	 * Modifies the bean to contain an eventID
	 * 
	 * @param mopRequest LogonEventBean
	 * @param mopData HashMap

	 */
	public Long insertMopEventIn(MopRequest mopRequest, HashMap mopData) {
		// [[
		Long result = null;
		Object[][] params = { { "bean", mopRequest }, { "mopData", mopData } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "_insertMopEventIn", params);
		}
		// ]]
		try {

			Long mopEventHeaderInId = null;
			if (mopRequest.getEventName() == null) {

				throw new LogonException("bean.getEventName() == null", Reason.RES_ERROR_IMPL);
			}

			ReadWriteDao<MopeventHeaderInEntity> mopeventHeaderDao = getDaoProvider().getMopeventHeaderInEntityDao();
			MopeventHeaderInEntity mopHeaderEntry = new MopeventHeaderInEntity();

			mopHeaderEntry.setEventName(mopRequest.getEventName());
			Double geoX = mopRequest.getGeoX() == null ? null : mopRequest.getGeoX().doubleValue();
			mopHeaderEntry.setGeoX(geoX);
			Double geoY = mopRequest.getGeoY() == null ? null : mopRequest.getGeoY().doubleValue();
			mopHeaderEntry.setGeoY(geoY);
			mopHeaderEntry.setPdaNo(mopRequest.getSenderPdaId());
			mopHeaderEntry.setSenderTime(mopRequest.getEventTime());
			mopHeaderEntry.setSessionNo(mopRequest.getSessionId() != null ? mopRequest.getSessionId().toString() : null);
			mopHeaderEntry.setSpeed(mopRequest.getSpeed());
			mopHeaderEntry.setCourse(mopRequest.getCourse());
			mopHeaderEntry.setGeoTime(mopRequest.getGeoTime());

			MopeventHeaderInEntity newMopEvent = mopeventHeaderDao.persist(mopHeaderEntry);
			mopEventHeaderInId = newMopEvent.getMopeventHeaderInId();

			ReadWriteDao<MopeventDataInEntity> mopeventDataDao = getDaoProvider().getMopeventDataInEntityDao();
			if (mopData != null) {
				for (Iterator it = mopData.keySet().iterator(); it.hasNext();) {
					String name = (String) it.next();
					if (StringUtils.notEmpty(name)) {
						Object value = mopData.get(name);
						if (value != null) {
							String strValue = value.toString();
							if (!strValue.isEmpty()) {
								MopeventDataInEntity mopDatatEntry = new MopeventDataInEntity();
								mopDatatEntry.setName(name);
								if (strValue.length() > 250) {
									strValue = strValue.substring(0, 247) + "...";
								}
								mopDatatEntry.setValue(strValue);
								mopDatatEntry.setMopeventHeaderIn(newMopEvent);
								mopeventDataDao.persist(mopDatatEntry);
							}
							if (name.equalsIgnoreCase("assignmentid")) {
								mopHeaderEntry.setAssignmentId(strValue);
							}
						}
					}
				}
			}
			em.flush();

			return result = mopEventHeaderInId;
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
	 * Update the Bean and Log Table with the return value of the WS call. 
	 * @param response
	 * 
	 */
	public MopResponse handleResponseMessage(MopRequestMessage mopRequestMessage,
			MopRequest mopRequest, LogonEventResponse response, Long mopEventHeaderInId,
			MopResponseMessage mopResponseMessage, LogonEventException logonEventException,
			boolean canSetMessageStatus, boolean loggedOut) {
		// [[
		Object[][] params = { { "mopRequestMessage", mopRequestMessage },
				{ "mopRequest", mopRequest }, { "bean", response },
				{ "mopEventHeaderInId", mopEventHeaderInId },
				{ "mopResponseMessage", mopResponseMessage } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateMopEventInStatus", params);
		}
		// ]]		
		try {
			// handle response from exception when it is occured
			MopResponse result = handleResponse((MopResponse) response, logonEventException, mopRequest);

			try {
				// Update the DB with the response code.
				// At this point, the event is processed, so we need to report the current status to the client. Any new exception is Logon's problem.			
				updateMopEventInStatus(mopEventHeaderInId, result);
			}
			catch (Exception e) {
				LOG.error("An error occured updating mop event status", e);
			}			

			// set response meaasge status when session is unknown
			if (canSetMessageStatus) {
				setMessageStatus(mopRequestMessage, mopResponseMessage, loggedOut);
			}
			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void updateMopEventInStatus(Long mopEventHeaderInId, MopResponse result) {
		ReadWriteDao<MopeventHeaderInEntity> mopeventHeaderDao = getDaoProvider().getMopeventHeaderInEntityDao();
		Filter filterHeader = new FilterImpl();
		filterHeader.addExpression(Restrictions.eq(
				MopeventHeaderInEntity.ATTR_MOPEVENT_HEADER_IN_ID, mopEventHeaderInId));
		MopeventHeaderInEntity mopHeaderEntry = mopeventHeaderDao.findSingle(filterHeader);

		mopHeaderEntry.setMopStatus(referentialDataService.findBsEntity(
				MopStatusBsEntity.class, String.valueOf(result.getCode())));
		mopHeaderEntry.setMopStatusContextValue(result.getContextValue());		
	}
	
	private MopResponse handleResponse(MopResponse response, LogonEventException logonEventException, MopRequest mopRequest) {
		MopResponse result = response;
		if (logonEventException != null) {
			LOG.info("Dispatching failed eventType=" + mopRequest.getEventName() + " eventID="
					+ mopRequest.getEventId() + " statusCode="
					+ logonEventException.getLogonEventResponse().getCode() + " contextValue="
					+ logonEventException.getLogonEventResponse().getContextValue());

			String requestClassName = mopRequest.getClass().getSimpleName();
			try {
				RequestHandlerLocalInterface handler = LocalServices.getLocalInterfaceForRequestHandler(requestClassName);
				String resolvedText = handler.getResponseMessage(mopRequest,
						logonEventException.getLogonEventResponse().getCode(),
						logonEventException.getFormatParameters());
				logonEventException.getLogonEventResponse().setTxt(resolvedText);
			}
			catch (NamingException e) {
				LOG.error("Could not retrieve handler for " + requestClassName);
			}

			result = (MopResponse) logonEventException.getLogonEventResponse();
		}		
		return result;
	}
	
	public void setMessageStatus(MopRequestMessage mopRequestMessage, MopResponseMessage mopResponseMessage, boolean loggedOut) {
		if (mopRequestMessage.getSessionId() != null) {
			PdaSessionEntity session = pdaSessionService.getSession(mopRequestMessage.getSessionId());
			if ((session == null)
					|| (session.getPdaSessionStatus().getPdaSessionStatusCd().longValue() == Constant.PDA_SESSION_STATUS_TIMED_OUT)
					|| ((session.getPdaSessionStatus().getPdaSessionStatusCd().longValue() == Constant.PDA_SESSION_STATUS_TERMINATED) && (!loggedOut))) {
				mopResponseMessage.setMessageStatus(MopResponseMessageCode.UNKNOWN_SESSION.value());
			}
		}
	}

	/**
	 * Only to be called from processEventBean
	 * @param messageBean The messageBean is optional and passed READ ONLY in the context (for eventNotSent)
	 * @param bean
	 * @param interfaceTypeCd
	 * @return
	 */
	public LogonEventResponse dispatchEventBean(MopRequestMessage mopRequestMessage,
			MopRequest mopRequest, Long mopEventHeaderInId) {
		// [[
		LogonEventResponse response = null;
		Object[][] params = { { "mopRequestMessage", mopRequestMessage }, { "bean", mopRequest },
				{ "mopEventHeaderInId", mopEventHeaderInId } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "_dispatchEventBean", params);
		}
		// ]]
		try {

			MopContextBean contextBean = new MopContextBean();
			contextBean.setInterfaceTypeCd(Constant.INTERFACE_TYPE_MOP_11);
			contextBean.setMopRequestMessage(mopRequestMessage);
			contextBean.setMopeventHeaderInId(mopEventHeaderInId);

			RequestHandlerLocalInterface handler = LocalServices.getLocalInterfaceForRequestHandler(mopRequest.getClass().getSimpleName());
			response = handler.handle(mopRequest, contextBean);
			em.flush();

			return response;
		}
		catch (NamingException e) {
			LOG.error(e.getMessage());
			return null;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(response);
			}
			// ]]
		}
	}

	/**
	 * Processes an event, 
	 * @param messageBean The messageBean is optional and passed in the context (for eventNotSent)
	 * @param bean
	 * @param interfaceTypeCd
	 * @return (possibly) updated bean on success, or the same bean with new status code on failure
	 */
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public MopResponse processEventBean(MopRequestMessage mopRequestMessage, MopRequest mopRequest,
			Long mopEventHeaderInId, MopResponseMessage mopResponseMessage,
			boolean canSetMessageStatus, boolean loggedOut) {
		// [[
		MopResponse result = null;
		Object[][] params = { { "messageBean", mopRequestMessage }, { "bean", mopRequest },
				{ "mopEventHeaderInId", mopEventHeaderInId },
				{ "mopResponseMessage", mopResponseMessage } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "processEventBean", params);
		}
		// ]]
		try {

			LogonEventException exception = null;
			try {
				result = (MopResponse) mopEventService.dispatchEventBean(mopRequestMessage,
						mopRequest, mopEventHeaderInId);
			}
			catch (LogonEventException logonEventException) {
				exception = logonEventException;
			}

			// handle Response message
			result = mopEventService.handleResponseMessage(mopRequestMessage, mopRequest,
					result, mopEventHeaderInId, mopResponseMessage, exception,
					canSetMessageStatus, loggedOut);

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

	/**
	 * Handles an incoming event. Includes locking on pdaid and logging to TLGNC_MOP_EVENT_*
	 * mopEventData: key=eventId, value=(HashMap)mopData
	 */
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public MopResponseMessage handleLogonMessage(MopRequestMessage requestMessage, Map mopEventData) {
		// [[
		MopResponseMessage result = null;
		Object[][] params = { { "requestMessage", requestMessage },
				{ "mopEventData", mopEventData } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handleLogonMessage", params);
		}
		// ]]
		try {
			try {
				LOG.info("Mutex lock (Incoming) with pdaid: " + requestMessage.getSenderPdaId());
				MUTEX.lock(requestMessage.getSenderPdaId(),
						getConfigBean().getWsclientMopReentrantkeyIncomingAcquistionTimeout());
			}
			catch (Exception e) {
				LOG.error("Error acquiring lock for incoming message", e);
				throw new LogonException(e);
			}
			MopResponseMessage mopResponseMessage = new MopResponseMessage();
			mopResponseMessage.setMessageStatus(MopResponseMessageCode.OK.value());

			boolean loggedOut = false;
			if (requestMessage.getMopRequests() != null) {
				int requestIndex = 0;
				int mopRequestSize = requestMessage.getMopRequests().size();
				
				for (MopRequest mopRequest : requestMessage.getMopRequests()) {
					boolean lastRequest = (mopRequestSize == ++requestIndex);
					if (mopRequest.getEventName().equalsIgnoreCase(
							LogonEventRequestFactory.MopFactory.EVENT_LOGOUT)) {
						loggedOut = true;
					}

					HashMap mopData = (HashMap) mopEventData.get(mopRequest.getEventId());
					// Insert request into DB 
					Long mopEventHeaderInId = mopEventService.insertMopEventIn(mopRequest, mopData);
					// Process request
					MopResponse mopResponse = processEventBean(requestMessage, mopRequest,
							mopEventHeaderInId, mopResponseMessage, lastRequest, loggedOut);
					mopResponse.setEventId(mopRequest.getEventId());
					mopResponseMessage.getMopEventStatusList().add(mopResponse);
				}
			}
			else {
				mopEventService.setMessageStatus(requestMessage, mopResponseMessage, loggedOut);
			}

			return result = mopResponseMessage;
		}
		finally {
			try {
				MUTEX.unlock(requestMessage.getSenderPdaId());
				LOG.info("Mutex unlock (Incoming) with pdaid: " + requestMessage.getSenderPdaId());
			}
			catch (Exception e) {
				LOG.error("Error releasing lock for incoming message", e);
			}
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	/**
	 * Stores the outgoing events sent to the mop to the log table
	 * 
	 * @param messageBean The outgoing eventBean.
	 * @return bean updated with the header ids
	 */
	private void insertMopMessageOut(LogonMessageOutBean messageBean, boolean needsResending) {
		// [[
		Object[][] params = { { "messageBean", messageBean },
				{ "needsResending", needsResending + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "insertMopMessageOut", params);
		}
		// ]]
		try {
			for (Iterator it = messageBean.getLogonEventBeans().iterator(); it.hasNext();) {
				LogonEventOutBean event = (LogonEventOutBean) it.next();
				ReadWriteDao<MopeventHeaderOutEntity> mopeventHeaderDao = getDaoProvider().getMopeventHeaderOutEntityDao();
				MopeventHeaderOutEntity mopHeaderEntry = new MopeventHeaderOutEntity();

				mopHeaderEntry.setSessionNo(messageBean.getSessionId());
				mopHeaderEntry.setPdaNo(messageBean.getSenderPDAId());
				mopHeaderEntry.setSenderTime(messageBean.getSendersTime());
				mopHeaderEntry.setEventId(event.getEventID());
				mopHeaderEntry.setEventName(event.getEventType());

				mopHeaderEntry.setMopStatus(referentialDataService.findBsEntity(
						MopStatusBsEntity.class, String.valueOf(event.getStatusCode())));
				mopHeaderEntry.setMopStatusContextValue(event.getStatusContextValue());
				mopHeaderEntry.setNeedsResendingFlag(Boolean.valueOf(needsResending));

				MopeventHeaderOutEntity newOutEvent = mopeventHeaderDao.persist(mopHeaderEntry);
				event.setId(newOutEvent.getMopeventHeaderOutId());

				ReadWriteDao<MopeventDataOutEntity> mopeventDataDao = getDaoProvider().getMopeventDataOutEntityDao();
				HashMap logonData = LogonDataFactory.create(event);
				if (logonData != null) {
					Iterator iter = logonData.entrySet().iterator();
					while (iter.hasNext()) {
						MopeventDataOutEntity mopDatatEntry = new MopeventDataOutEntity();
						Entry entry = (Entry) iter.next();
						mopDatatEntry.setName((String) entry.getKey());
						mopDatatEntry.setValue((String) entry.getValue());
						mopDatatEntry.setMopeventHeaderOut(newOutEvent);

						mopeventDataDao.persist(mopDatatEntry);
					}
				}
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

	/**
	 * Handles an outgoing event
	 * @param eventName
	 */
	@AdnTrace
	public void sendMessage(LogonMessageOutBean messageBean){
		Object[][] params = { { "messageBean", messageBean } };
		String pdaId = messageBean.getSenderPDAId();
		boolean isRolleBackRequired = true;
		try {
			if (pdaId != null) {
				try {
					LOG.info("Mutex lock (Outgoing) with pdaid: " + pdaId);
					MUTEX.lock(pdaId,
							getConfigBean().getWsclientMopReentrantkeyOutgoingAcquistionTimeout());
				}
				catch (Exception e) {
					LOG.error("Error acquiring lock for outgoing message", e);
					//Since it is critical that Logon sends the messages which were put into the queue, and at the moment it is
					// unclear if it is better/worse if Logon does not send the message in case the reentrant key timeout is reached, for now
					//keep the behavior as-is: continue sending and persisting the message. See CRHLOGONSH-307
					LOG.info("Outgoing message sending proceeds despite of lock acquistion error: " + messageBean.getLogonEventBeans().stream().map(o ->
							"eventType=" + ((LogonEventOutBean)o).getEventType() +
									"; pdaDeviceId=" + ((LogonEventOutBean)o).getSenderPDAId() +
									"; sessionId=" + ((LogonEventOutBean)o).getSessionId()).collect(Collectors.toList()));
				}
			}
			PdaSessionEntity session = null;
			for (Iterator it = messageBean.getLogonEventBeans().iterator(); it.hasNext();) {
				LogonEventOutBean event = (LogonEventOutBean) it.next();
				if (event.getEventID() == null) {
					event.setEventID(fetchSequenceNextVal(Constant.SEQUENCE_LOGON_EVENTID));
				}
			}

			
			if (!StringUtils.empty(messageBean.getSessionId())) {
				session = getDaoProvider().getPdaSessionEntityDao().findById(
						new Long(messageBean.getSessionId()));
			}

			boolean needsResending = false;
			if ((session == null)
					|| (session.getPdaSessionStatus().getPdaSessionStatusCd().longValue() != Constant.PDA_SESSION_STATUS_EVENTS_PENDING)) {
				try {
					LogonMessageOutBean responseMsg = LogonToMop.sendToMop(messageBean,
							getConfigBean());
					if (responseMsg != null) {
						messageBean = responseMsg;
					}
					else {
						for (Iterator it = messageBean.getLogonEventBeans().iterator(); it.hasNext();) {
							LogonEventOutBean event = (LogonEventOutBean) it.next();
							event.setStatusCode(new BigInteger(""
									+ Constant.STATUS_CODE_LOGON_MESSAGE_TECHNICAL_ERROR_CD));
							event.setStatusTxt(null);
							event.setStatusContextValue(null);
						}
					}
					isRolleBackRequired = false;
				}
				catch (Throwable throwable) {
					ExceptionUtil.errException(LOG, throwable, params);
					for (Iterator it = messageBean.getLogonEventBeans().iterator(); it.hasNext();) {
						LogonEventOutBean event = (LogonEventOutBean) it.next();
						event.setStatusCode(new BigInteger(""
								+ Constant.STATUS_CODE_LOGON_MESSAGE_TECHNICAL_ERROR_CD));
						event.setStatusTxt(null);
						event.setStatusContextValue(null);
					}
					
				}
				
				StrBuilder logMsg = null;
				if (LOG.isInfoEnabled()) {
					logMsg = new StrBuilder();
					if (messageBean != null) {
						logMsg.appendln("OUT: MOP Message PDA=" + messageBean.getSenderPDAId()
								+ ", session=" + messageBean.getSessionId() + ", status="
								+ messageBean.getMessageStatus());
						for (Iterator it = messageBean.getLogonEventBeans().iterator(); it.hasNext();) {
							LogonEventOutBean event = (LogonEventOutBean) it.next();
							logMsg.appendln("OUT: MOP Event PDA=" + event.getSenderPDAId()
									+ ", status=" + event.getStatusCode() + ", text="
									+ event.getStatusTxt() + ", contextValue="
									+ event.getStatusContextValue() + ", event="
									+ event.getEventID() + " (" + event.getEventType() + ")");
						}
						LOG.info(logMsg.toString());
					}
				}

				if (messageBean != null) {
					switch (messageBean.getMessageStatus()) {
					case Constant.STATUS_CODE_LOGON_MESSAGE_PDA_INTERRUPTED_CD:
					case Constant.STATUS_CODE_LOGON_MESSAGE_PDA_OFFLINE_CD:
						needsResending = true;
						break;
					default:
						needsResending = false;
						break;
					}
				}
			}
			else {
				LOG.info("Message to "
						+ messageBean.getSenderPDAId()
						+ " queued, session="
						+ (session != null ? (session.getPdaSessionId() + " " + session.getPdaSessionStatus().getPdaSessionStatusCd()) : "null"));
				needsResending = true;
			}

			if ((needsResending) && (session != null)) {
				session.setPdaSessionStatus(referentialDataService.findBsEntity(
						PdaSessionStatusBsEntity.class, Constant.PDA_SESSION_STATUS_EVENTS_PENDING));
			}

			insertMopMessageOut(messageBean, needsResending);
		}
		catch(RuntimeException exception){
			if(isRolleBackRequired){
				LOG.error("MopEventServiceBean MOP message sending failed",exception);
			}
			
			throw exception;
		}
		finally {
			if (pdaId != null) {
				try {
					MUTEX.unlock(pdaId);
					LOG.info("Mutex unlock (Outgoing) with pdaid: " + pdaId);
				}
				catch (Exception e) {
					LOG.error("Error releasing lock for outgoing message", e);
				}
			}
			if(isRolleBackRequired){
				throw new LogonForceRollBackMDBException();
			}
		}
	}

	/**
	 * Used by test page
	 * @param event
	 * @return
	 */
	public void sendEvent(LogonEventOutBean event) {
		// [[
		Object[][] params = { { "event", event } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendEvent", params);
		}
		// ]]
		try {
			// prepare outgoing message
			LogonMessageOutBean messageBean = new LogonMessageOutBean();
			messageBean.setSendersTime(new Date());
			List events = new ArrayList();
			events.add(event);
			messageBean.setLogonEventBeans(events);
			messageBean.setSenderPDAId(event.getSenderPDAId());
			messageBean.setSessionId(event.getSessionId());
			sendMessage(messageBean);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public void sendMessageToPDA(DriverEntity driver, PdaMessageEntity pme) {
		// [[
		Object[][] params = { { "driver", driver }, { "pme", pme } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendMessageToPDA", params);
		}
		// ]]
		try {
			if (driver == null || pme == null || driver.getCurrentTruck() == null) {
				throw new LogonException(Reason.RES_ORDER_INSUFFICIENT_DATA);
			}

			PdaSessionEntity session = pdaSessionService.getCurrentSession(driver.getCurrentTruck());
			if (session == null) {
				LOG.error("Could not find session for the selected Truck");
				throw new LogonException(Reason.RES_ORDER_WRONG_STATUS);
			}

			Message msg = new Message();
			msg.setEventType(LogonEventOutBeanFactory.EVENT_MESSAGE);
			pdaSessionService.initOutgoingEvent(msg, session);

			msg.setMessage(pme.getMessage());
			msg.setMessageID(String.valueOf(pme.getPdaMessageId()));
			msg.setSendersTime(pme.getCtlCreTs());

			sendEvent(msg);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	/**
	 * sends refdata to comServer
	 * 
	 * @param entity
	 * @param key
	 * @param langCd
	 * @param txt
	 * @param setNo
	 */
	private void sendRefDataToComServer(String entity, String key, String langCd, String txt,
			String setNo) {
		// [[
		Object[][] params = { { "entity", entity }, { "key", key }, { "langCd", langCd },
				{ "txt", txt }, { "setNo", setNo } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendRefDataToComServer", params);
		}
		// ]]
		try {
			RefData rd = new RefData();
			rd.setEventType(LogonEventOutBeanFactory.EVENT_REFDATA);
			rd.setEntity(entity);
			rd.setKey(key);
			rd.setLanguageCD(langCd);
			rd.setTxt(txt);
			rd.setSetNo(setNo);

			sendEvent(rd);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private final static String ENTITY_LANG = "lang";

	private final static String ENTITY_LANG_SEQ = "SLGNC_MOPSET_LANG";

	/**
	 * sends all lang refdata to the comServer
	 * 
	 */
	public void sendLangDataSetToComServer() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendLangDataSetToComServer");
		}
		// ]]
		try {

			final String setNo = fetchSequenceNextVal(ENTITY_LANG_SEQ).toString();

			List langs = referentialDataService.findLangTxEntityList();

			for (Iterator iterator = langs.iterator(); iterator.hasNext();) {
				LangTxEntity lang = (LangTxEntity) iterator.next();
				if (lang.getLangTxPK().getLangBs().getCtlActCd()) {
					sendRefDataToComServer(ENTITY_LANG, lang.getLangTxPK().getLangBs().getLangCd(),
							lang.getLangTxPK().getLang1Cd(), lang.getTxt(), setNo);
				}

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

	private final static String ENTITY_FUNC = "func";

	private final static String ENTITY_FUNC_SEQ = "SLGNC_MOPSET_FUNC";

	/**
	 * sends all func refdata to the comServer
	 * 
	 */
	public void sendFuncDataSetToComServer() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendFuncDataSetToComServer");
		}
		// ]]
		try {

			final String setNo = fetchSequenceNextVal(ENTITY_FUNC_SEQ).toString();

			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.ge(FuncEntity.ATTR_FUNC_ID,
					Constant.FUNCTION_GROUP_PDA_MIN));
			filter.addExpression(Restrictions.le(FuncEntity.ATTR_FUNC_ID,
					Constant.FUNCTION_GROUP_PDA_MAX));
			List funcs = getDaoProvider().getFuncEntityDao().find(filter);

			for (Iterator iterator = funcs.iterator(); iterator.hasNext();) {
				FuncEntity func = (FuncEntity) iterator.next();

				sendRefDataToComServer(ENTITY_FUNC, func.getFuncId().toString(), null,
						func.getFuncName(), setNo);
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

	private final static String ENTITY_TRUCK_MAINTENANCE = "truck_maintenance";

	private final static String ENTITY_TRUCK_MAINTENANCE_SEQ = "SLGNC_MOPSET_TRUCK_MAINTENANCE";

	/**
	 * sends all truck maintenance refdata to the comServer
	 * 
	 */
	public void sendTruckMaintenanceDataSetToComServer() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendTruckMaintenanceDataSetToComServer");
		}
		// ]]
		try {

			final String setNo = fetchSequenceNextVal(ENTITY_TRUCK_MAINTENANCE_SEQ).toString();

			List truckMaintenances = referentialDataService.findTxEntityFiltered(
					TruckMaintenanceTxEntity.class, (Filter) null);

			for (Iterator iterator = truckMaintenances.iterator(); iterator.hasNext();) {
				TruckMaintenanceTxEntity truckMaintenance = (TruckMaintenanceTxEntity) iterator.next();
				if (truckMaintenance.getTruckMaintenanceTxPK().getTruckMaintenanceBs().getCtlActCd()) {
					sendRefDataToComServer(
							ENTITY_TRUCK_MAINTENANCE,
							truckMaintenance.getTruckMaintenanceTxPK().getTruckMaintenanceBs().getTruckMaintenanceCd().toString(),
							truckMaintenance.getTruckMaintenanceTxPK().getLangCd(),
							truckMaintenance.getTxt(), setNo);
				}
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

	private final static String ENTITY_PDA_ERROR_MESSAGE = "pda_error_message";

	private final static String ENTITY_PDA_ERROR_MESSAGE_SEQ = "SLGNC_MOPSET_PDA_ERROR_MESSAGE";

	/**
	 * sends all pdaErrorMessages refdata to the comServer
	 * 
	 */
	public void sendPdaErrorMessageDataSetToComServer() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendPdaErrorMessageDataSetToComServer");
		}
		// ]]
		try {

			final String setNo = fetchSequenceNextVal(ENTITY_PDA_ERROR_MESSAGE_SEQ).toString();

			List truckMaintenances = referentialDataService.findTxEntityFiltered(
					PdaErrorMessageTxEntity.class, (Filter) null);

			for (Iterator iterator = truckMaintenances.iterator(); iterator.hasNext();) {
				PdaErrorMessageTxEntity pdaErrorMessage = (PdaErrorMessageTxEntity) iterator.next();
				if (pdaErrorMessage.getPdaErrorMessageTxPK().getPdaErrorMessageBs().getCtlActCd()) {
					sendRefDataToComServer(
							ENTITY_PDA_ERROR_MESSAGE,
							pdaErrorMessage.getPdaErrorMessageTxPK().getPdaErrorMessageBs().getPdaErrorMessageCd().toString(),
							pdaErrorMessage.getPdaErrorMessageTxPK().getLangCd(),
							pdaErrorMessage.getTxt(), setNo);
				}
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

	private final static String ENTITY_PDA_MARKET_INFO = "pda_market_info";

	private final static String ENTITY_PDA_MARKET_INFO_SEQ = "SLGNC_MOPSET_PDA_MARKET_INFO";

	/**
	 * sends all pdaMarketInfos refdata to the comServer
	 * 
	 */
	public void sendPdaMarketInfoDataSetToComServer() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendPdaMarketInfoDataSetToComServer");
		}
		// ]]
		try {

			final String setNo = fetchSequenceNextVal(ENTITY_PDA_MARKET_INFO_SEQ).toString();

			List pdaMarketInfos = referentialDataService.findTxEntityFiltered(
					PdaMarketinfoTxEntity.class, (Filter) null);

			for (Iterator iterator = pdaMarketInfos.iterator(); iterator.hasNext();) {
				PdaMarketinfoTxEntity pdaMarketInfo = (PdaMarketinfoTxEntity) iterator.next();
				if (pdaMarketInfo.getPdaMarketinfoTxPK().getPdaMarketinfoBs().getCtlActCd()) {
					sendRefDataToComServer(
							ENTITY_PDA_MARKET_INFO,
							pdaMarketInfo.getPdaMarketinfoTxPK().getPdaMarketinfoBs().getPdaMarketinfoCd().toString(),
							pdaMarketInfo.getPdaMarketinfoTxPK().getLangCd(),
							pdaMarketInfo.getTxt(), setNo);
				}
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

	private final static String ENTITY_CURRENCY = "currency";

	private final static String ENTITY_CURRENCY_SEQ = "SLGNC_MOPSET_CURRENCY";

	/**
	 * sends all currencies refdata to the comServer
	 * 
	 */
	public void sendCurrencyDataSetToComServer() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendCurrencyDataSetToComServer");
		}
		// ]]
		try {

			final String setNo = fetchSequenceNextVal(ENTITY_CURRENCY_SEQ).toString();

			List currencies = referentialDataService.findTxEntityFiltered(CurrencyTxEntity.class,
					(Filter) null);

			for (Iterator iterator = currencies.iterator(); iterator.hasNext();) {
				CurrencyTxEntity currency = (CurrencyTxEntity) iterator.next();
				if (currency.getCurrencyTxPK().getCurrencyBs().getCtlActCd()) {
					sendRefDataToComServer(ENTITY_CURRENCY,
							currency.getCurrencyTxPK().getCurrencyBs().getCurrencyCd().toString(),
							currency.getCurrencyTxPK().getLangCd(), currency.getTxt(), setNo);
				}
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

	private final static String ENTITY_FUEL_TYPE = "fuel_type";

	private final static String ENTITY_FUEL_TYPE_SEQ = "SLGNC_MOPSET_FUEL_TYPE";

	/**
	 * sends all fuelTypes refdata to the comServer
	 * 
	 */
	public void sendFuelTypeDataSetToComServer() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendFuelTypeDataSetToComServer");
		}
		// ]]
		try {

			final String setNo = fetchSequenceNextVal(ENTITY_FUEL_TYPE_SEQ).toString();

			List fuelTypes = referentialDataService.findTxEntityFiltered(FuelTypeTxEntity.class,
					(Filter) null);

			for (Iterator iterator = fuelTypes.iterator(); iterator.hasNext();) {
				FuelTypeTxEntity fuelType = (FuelTypeTxEntity) iterator.next();
				if (fuelType.getFuelTypeTxPK().getFuelTypeBs().getCtlActCd()) {
					sendRefDataToComServer(ENTITY_FUEL_TYPE,
							fuelType.getFuelTypeTxPK().getFuelTypeBs().getFuelTypeCd().toString(),
							fuelType.getFuelTypeTxPK().getLangCd(), fuelType.getTxt(), setNo);
				}
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

	private final static String ENTITY_WAITINGTIMEREASON_SHIPPINGPOINT = "waitingtimereason_shippingpoint";

	private final static String ENTITY_WAITINGTIMEREASON_SHIPTO = "waitingtimereason_shipto";

	private final static String ENTITY_WAITINGTIMEREASON_SHIPPINGPOINT_SEQ = "SLGNC_MOPSET_WAIT_REASON_SP";

	private final static String ENTITY_WAITINGTIMEREASON_SHIPTO_SEQ = "SLGNC_MOPSET_WAIT_REASON_ST";

	/**
	 * sends all waitingtimeReasons refdata to the comServer
	 * 
	 */
	private void sendWaitingtimeReasonDataSetToComServer(String entity, String sequence) {
		// [[
		Object[][] params = { { "entity", entity }, { "sequence", sequence } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendWaitingtimeReasonDataSetToComServer", params);
		}
		// ]]
		try {

			final String setNo = fetchSequenceNextVal(sequence).toString();

			List waitingtimeReasons = referentialDataService.findTxEntityFiltered(
					WaitingtimeReasonTxEntity.class, (Filter) null);

			for (Iterator iterator = waitingtimeReasons.iterator(); iterator.hasNext();) {
				WaitingtimeReasonTxEntity waitingtimeReason = (WaitingtimeReasonTxEntity) iterator.next();
				if (waitingtimeReason.getWaitingtimeReasonTxPK().getWaitingtimeReasonBs().getCtlActCd()) {
					if (((ENTITY_WAITINGTIMEREASON_SHIPPINGPOINT.equals(entity)) && (waitingtimeReason.getWaitingtimeReasonTxPK().getWaitingtimeReasonBs().getAtShippingpointFlag()))
							|| ((ENTITY_WAITINGTIMEREASON_SHIPTO.equals(entity)) && (waitingtimeReason.getWaitingtimeReasonTxPK().getWaitingtimeReasonBs().getAtShiptoFlag()))) {
						sendRefDataToComServer(
								entity,
								waitingtimeReason.getWaitingtimeReasonTxPK().getWaitingtimeReasonBs().getWaitingtimeReasonCd().toString(),
								waitingtimeReason.getWaitingtimeReasonTxPK().getLangCd(),
								waitingtimeReason.getTxt(), setNo);
					}
				}
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

	public void sendWaitingtimeReasonShippingpointDataSetToComServer() {

		sendWaitingtimeReasonDataSetToComServer(ENTITY_WAITINGTIMEREASON_SHIPPINGPOINT,
				ENTITY_WAITINGTIMEREASON_SHIPPINGPOINT_SEQ);
	}

	public void sendWaitingtimeReasonShiptoDataSetToComServer() {

		sendWaitingtimeReasonDataSetToComServer(ENTITY_WAITINGTIMEREASON_SHIPTO,
				ENTITY_WAITINGTIMEREASON_SHIPTO_SEQ);
	}

	private final static String ENTITY_RFIDOBJECT_TYPE = "rfidobject_type";

	private final static String ENTITY_RFIDOBJECT_TYPE_SEQ = "SLGNC_MOPSET_RFIDOBJECT_TYPE";

	/**
	 * sends all rfidobjectTypes refdata to the comServer
	 * 
	 */
	public void sendRfidobjectTypeDataSetToComServer() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendRfidobjectTypeDataSetToComServer");
		}
		// ]]
		try {

			final String setNo = fetchSequenceNextVal(ENTITY_RFIDOBJECT_TYPE_SEQ).toString();

			List rfidobjectTypes = referentialDataService.findTxEntityFiltered(
					RfidobjectTypeTxEntity.class, (Filter) null);

			for (Iterator iterator = rfidobjectTypes.iterator(); iterator.hasNext();) {
				RfidobjectTypeTxEntity rfidobjectType = (RfidobjectTypeTxEntity) iterator.next();
				if (rfidobjectType.getRfidobjectTypeTxPK().getRfidobjectTypeBs().getCtlActCd()) {
					sendRefDataToComServer(
							ENTITY_RFIDOBJECT_TYPE,
							rfidobjectType.getRfidobjectTypeTxPK().getRfidobjectTypeBs().getRfidobjectTypeCd().toString(),
							rfidobjectType.getRfidobjectTypeTxPK().getLangCd(),
							rfidobjectType.getTxt(), setNo);
				}
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

	private final static String ENTITY_CONFIG = "config";

	private final static String KEY_WAITINGTIME_AT_SHIPPINGPOINT = "waitingtime_at_shippingpoint";

	/**
	 * sends new/updated waitingtimeAtShippingpoint value to the comServer
	 * 
	 */
	public void sendWaitingtimeAtShippingpointToComServer() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendWaitingtimeAtShippingpointToComServer");
		}
		// ]]
		try {

			sendRefDataToComServer(ENTITY_CONFIG, KEY_WAITINGTIME_AT_SHIPPINGPOINT, null,
					getConfigBean().getWsclientMopWaitingTimeAtShippingpoint(), "1");
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private final static String KEY_WAITINGTIME_AT_SHIPTO = "waitingtime_at_shipto";

	/**
	 * sends new/updated waitingtimeAtShipto value to the comServer
	 * 
	 */
	public void sendWaitingtimeAtShiptoToComServer() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendWaitingtimeAtShiptoToComServer");
		}
		// ]]
		try {

			sendRefDataToComServer(ENTITY_CONFIG, KEY_WAITINGTIME_AT_SHIPTO, null,
					getConfigBean().getWsclientMopWaitingTimeAtShipTo(), "1");
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
