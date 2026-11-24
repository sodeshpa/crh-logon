/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.BizEventUtil;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.WagonEmptyRequest;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class WagonEmptyRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WagonEmptyRequestHandler.class);

    @EJB private OrderServiceLocalInterface orderService;
    
    
    protected void validate(LogonEventRequest eventBean) {
        
    	WagonEmptyRequest bean = (WagonEmptyRequest)eventBean;
    	validateMandatory(FIELD_ASSIGNMENT_PART_ID, bean.getAssignmentPartId());
    	rfidMandatory(FIELD_WAGON_LABEL, bean.getWagonRfidIdentity(), bean.getWagonLabel());
    }
    

    protected LogonEventResponse perform(LogonEventRequest eventBean) {
		// [[
		LogonEventResponse result = null;
		Object[][] params = {{"eventBean", eventBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "perform", params);
		}
		// ]]
		try {
			final WagonEmptyRequest bean = (WagonEmptyRequest) eventBean;             
            final WagonEntity wagon = getWagon(bean.getWagonRfidIdentity(), bean.getWagonLabel());
            if (wagon == null) {            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_WAGON_LABEL), bean.getWagonLabel());
            }
            
            final ReadOnlyDao<AssignmentEntity> dao = getDaoProvider().getAssignmentEntityDao();
            final Filter<AssignmentEntity> filter = dao.createFilter();
            filter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_WAGON, wagon));
            filter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
            filter.addExpression(Restrictions.lt(AssignmentEntity.ATTR_ASSIGN_STATUS_CD, Constant.ASSIGNMENT_STATUS_FINISHED));
            
            final AssignmentEntity assignment = dao.findSingle(filter);
            if (assignment != null) {
            	final BizeventEntity bizevent = new BizeventEntity();
            	bizevent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_DELIVERED);
            	bizevent.setAssignment(assignment);
            	bizevent.setEventTs(eventBean.getEventTime());
            	bizevent.setWagon(wagon);
            	
            	@SuppressWarnings("unchecked")
				final List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();
            	for (AssignmentPartEntity ap : assignmentParts ) {
            		if (ap.getOrder() != null) {
            			bizevent.setOrder(ap.getOrder());
            			bizevent.setShipto(ap.getShipto());
            			break;
            		}
            	}
        		// This bizevent should not be persisted here (see OrderServiceBean._setUnloadedImpl)
            	// 
        		// bizevent = getMonitoringTablesService().processBizevent(bizevent);
            	orderService.setUnloaded(bizevent, null, Boolean.FALSE, Boolean.TRUE, true);
            }
            
            return result = createLogonEventResponse(MopResponseCode.OK);
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
