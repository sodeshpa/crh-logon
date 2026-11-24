/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.mop;

import java.util.List;
import java.util.ListIterator;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.AsyncMopEventOutServiceLocalInterface;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.MopEventUtils;
import com.crh.logon.business.stores.domain.MopeventDataOutStoreInterface;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.EventNotSentRequest;
import com.holcim.logon.eventbeans.in.mop.MopRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.MopeventHeaderOutEntity;
import com.crh.logon.persistence.entities.common.PdaDeviceEntity;
import com.crh.logon.persistence.entities.common.PdaSessionEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
@AdnTrace
public class EventNotSentRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EventNotSentRequestHandler.class);
	private static final String FIELD_FAILED_EVENT_ID = "failedEventID";
	
	@EJB private MopeventDataOutStoreInterface mopDataOutStore;
    @EJB private AsyncMopEventOutServiceLocalInterface asyncMopEventOutService;
	
	protected void validate(LogonEventRequest eventBean) {    	
    	final EventNotSentRequest bean = (EventNotSentRequest) eventBean;
    	
    	validateMandatory(FIELD_FAILED_EVENT_ID, bean.getFailedEventId());
    }
	
	private boolean deviceHasSessionWithEventsPending(String deviceNo) {
        Filter<PdaSessionEntity> filter = new FilterImpl<PdaSessionEntity>();
        
        filter.addExpression(Restrictions.eq(PdaSessionEntity.ATTR_PDA_SESSION_STATUS_CD, Constant.PDA_SESSION_STATUS_EVENTS_PENDING));
        filter.addExpression(Restrictions.eq(PdaSessionEntity.ATTR_PDA_DEVICE + "." + PdaDeviceEntity.ATTR_PDA_DEVICE_NO, deviceNo));  
        long sessionCount = getDaoProvider().getPdaSessionEntityDao().count(filter);
        
        return sessionCount > 0;
	}
	
	private MopeventHeaderOutEntity getHeader(EventNotSentRequest bean) {

		final ReadOnlyDao<MopeventHeaderOutEntity> dao = getDaoProvider().getMopeventHeaderOutEntityDao();
		final Filter<MopeventHeaderOutEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(MopeventHeaderOutEntity.ATTR_EVENT_ID, bean.getFailedEventId()));
		filter.addExpression(Order.desc(MopeventHeaderOutEntity.ATTR_MOPEVENT_HEADER_OUT_ID));
		
		final List<MopeventHeaderOutEntity> outevents = getDaoProvider().getMopeventHeaderOutEntityDao().findNRecs(0, 1, filter);
		if ((outevents == null) || (outevents.isEmpty())) {			
			throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_FAILED_EVENT_ID));
		}
		
		return (MopeventHeaderOutEntity) outevents.get(0);
	}
	
	private Long getAssignmentId(MopeventHeaderOutEntity header) {
		Long assignmentId = mopDataOutStore.getMopDataOutFieldLong(header, FIELD_ASSIGNMENT_ID);
		
		if (assignmentId == null) {
			throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, 
					"An error occurred retrieving tourdata for mopevent header " +
					header.getMopeventHeaderOutId()));
		}
		
		return assignmentId;
	}

	protected LogonEventResponse perform(LogonEventRequest eventBean) {
		EventNotSentRequest bean = (EventNotSentRequest)eventBean;
		MopeventHeaderOutEntity header = getHeader(bean);
		
		if (deviceHasSessionWithEventsPending(header.getPdaNo())) {
			header.setNeedsResendingFlag(true);
		}
		else {
			// TODO (armin Feb 13, 2015): I did not create this abomination but I should really destroy it..
			if (MopEventUtils.isTourData(header)) {
				// In case an event as part of a tour is being requested, the 
				// complete tour should be resent (in a single message).
				// It's possible that multiple events within a message request 
				// different parts of the same tour, in that case the tour 
				// should be resent only once.
				// Since incoming events within a message don't (didn't) have
				// any relation and were handled completely independent of each
				// other, I added the messageBean to the contextBean.
				// Now, this handler only handles the last event within a 
				// message for a certain assignment. In case we find other uses
				// of incoming events relying on other incoming events,
				// reconsider this approach (session context data?).
				Long assignmentId = getAssignmentId(header);
				final List<MopRequest> mopRequests = getContextBean().getMopRequestMessage().getMopRequests();
				final ListIterator<MopRequest> mopRequestsIter = mopRequests.listIterator(mopRequests.size());
				while (mopRequestsIter.hasPrevious()) {
					LogonEventRequest otherEventBean = mopRequestsIter.previous();
					if (otherEventBean instanceof EventNotSentRequest) {
						if (((EventNotSentRequest) otherEventBean).getEventId().equals(bean.getEventId())) {
							// There's no EventNotSent bean after this one for 
							// this assignment. The tour will be resent now.
							break;
						}
						MopeventHeaderOutEntity otherHeader = getHeader((EventNotSentRequest)otherEventBean);
						if (MopEventUtils.isTourData(otherHeader)) {
							Long otherAssignmentId = getAssignmentId(otherHeader);
							if (otherAssignmentId.equals(assignmentId)) {
								// The otherEventBean is a request for the same
								// tour. The tour will be send when 
								// otherEventBean is handled.
								return createLogonEventResponse(MopResponseCode.OK);
							}
						}
					}
				}
				
				AssignmentEntity assignment = getAssignment(assignmentId);
				asyncMopEventOutService.sendTourToPDA(assignment.getTruck(), assignment);
			}
			else {
				asyncMopEventOutService.resendEvent(header);
			}
		}
		
		return  createLogonEventResponse(MopResponseCode.OK);
	}

}
