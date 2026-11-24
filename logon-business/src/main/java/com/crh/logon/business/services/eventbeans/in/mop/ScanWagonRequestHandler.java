/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.ScanWagonRequest;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class ScanWagonRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ScanWagonRequestHandler.class);
    
    protected void validate(LogonEventRequest eventBean) {
        
    	ScanWagonRequest bean = (ScanWagonRequest)eventBean;
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
            ScanWagonRequest bean = (ScanWagonRequest)eventBean;
            
            AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());
            WagonEntity wagon = getWagon(bean.getWagonRfidIdentity(), bean.getWagonLabel());
            if (wagon == null) {
			
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_WAGON_LABEL), bean.getWagonLabel());
            }
            
            BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_SCAN_WAGON, bean, true);
            bizevent.setAssignment(part.getAssignment());
            bizevent.setWagon(wagon);
                        
            // Get wagon's load
            Filter filter = new FilterImpl();
            filter.addExpression(Restrictions.eq(LoadEntity.ATTR_WAGON, wagon));
            filter.addExpression(Restrictions.isNotNull(LoadEntity.ATTR_LOADING_TS));
            filter.addExpression(Restrictions.isNull(LoadEntity.ATTR_UNLOADING_TS));
            LoadEntity wagonLoad = getDaoProvider().getLoadEntityDao().findSingle(filter);
            if (wagonLoad == null) {
            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "no load found for wagon"));
            }
            // Get wagon's order item
            filter = new FilterImpl();
            filter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD, wagonLoad));
            // Note that only bulk orders are supported for bimodal orders, so 1 load = 1 order item
            OrderItemEntity wagonOrderItem = getDaoProvider().getOrderItemEntityDao().findSingle(filter);
            
            // Get truck's load
            LoadEntity truckLoad = part.getLoad();
            if (truckLoad == null) {
			
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "no load found for assignment part"));
            }
            // Get truck's order item 
            filter = new FilterImpl();
            filter.addExpression(Restrictions.eq(OrderItemEntity.ATTR_LOAD, truckLoad));
            OrderItemEntity truckOrderItem = getDaoProvider().getOrderItemEntityDao().findSingle(filter);
            if ( (truckOrderItem.getBimodalRailOrderItem() == null) || (!truckOrderItem.getBimodalRailOrderItem().getOrderItemId().equals(wagonOrderItem.getOrderItemId())) ) {
            	if ( (truckOrderItem.getBimodalRailOrderItem() != null) && (truckOrderItem.getBimodalRailOrderItem().getLoad().getWagon() != null) ) {
            		
            		throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "incorrect wagon, expected " + truckOrderItem.getBimodalRailOrderItem().getLoad().getWagon().getWagonNo()));	
            	}   
            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "incorrect wagon"));
            }   
            truckLoad.setLoadedFromLoad(wagonLoad);
            
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
