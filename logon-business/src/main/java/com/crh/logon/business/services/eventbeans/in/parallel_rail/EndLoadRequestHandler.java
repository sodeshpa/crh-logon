/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.parallel_rail;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.EndLoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.parallel_rail.EndLoadRequest;
import com.holcim.logon.eventbeans.in.parallel_rail.ParallelRailResponseCode;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class EndLoadRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	@EJB private EndLoadingServiceLocalInterface endLoadingService;
	@EJB private OrderServiceLocalInterface orderService;
	
	protected void validate(LogonEventRequest eventBean) {
    	
    	EndLoadRequest bean = (EndLoadRequest)eventBean;
    	validateMandatory(com.crh.logon.business.services.eventbeans.in.LogonEventRequestHandler.FIELD_VERLADEAUFTRAG_ID, bean.getVerladeauftragId());
    	validateMandatory(com.crh.logon.business.services.eventbeans.in.LogonEventRequestHandler.FIELD_TARE_WEIGHT, bean.getTareWeightKg());
    	validateMandatory(com.crh.logon.business.services.eventbeans.in.LogonEventRequestHandler.FIELD_TOTAL_WEIGHT, bean.getTotalWeightKg());
    	validateMandatory(com.crh.logon.business.services.eventbeans.in.LogonEventRequestHandler.FIELD_NET_WEIGHT, bean.getNetWeightKg());
    }
    
    
	protected LogonEventResponse perform(LogonEventRequest eventBean) {
		
		final EndLoadRequest bean = (EndLoadRequest)eventBean;
		
		final BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_LOADED, bean);
		final LoadEntity load = getLoadByVerladeauftragId(bean.getVerladeauftragId());
		if (load == null) {
			throw new LogonEventException(createBasicLogonEventResponse(
					ParallelRailResponseCode.APPLICATION_ERROR,
					"No load found with verladeAuftragID " + bean.getVerladeauftragId()));
		}
		
		bizevent.setContainer(load.getContainer());
		
		final OrderItemEntity orderItem = LogonUtil.getBulkDumpOrPowderOrderItem(orderService.getOrderItems(load));
		
		if (bean.getLogonOrderId() != null) {
			final OrderEntity order = orderService.findOrderByOrderNumber(bean.getLogonOrderId(), true);
			if (order == null) {
				throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.MANDATORY_FIELD_ERROR, com.crh.logon.business.services.eventbeans.in.LogonEventRequestHandler.FIELD_LOGON_ORDER_ID), bean.getLogonOrderId());
			}
			bizevent.setOrder(order);
		}
		else if (orderItem != null) {
			bizevent.setOrder(orderItem.getOrder());
		}
		
		AssignmentPartEntity loadAp = getAssignmentPart(load);
		if (loadAp != null) {
			bizevent.setAssignment(loadAp.getAssignment());
		}
		
		final LoadingpointEntity lp;
		if ((StringUtils.empty(bean.getWerkId())) && (orderItem != null)) {
			lp = getLoadingpoint(orderItem.getOrder().getShippingpoint().getPlant().getPlantCd(), bean.getVerladestrasseId());
		}
		else if (!StringUtils.empty(bean.getWerkId())) {
			lp = getLoadingpoint(bean.getWerkId(), bean.getVerladestrasseId());
		}
		else {
			lp = null;
		}
		
		if (lp != null) {
			bizevent.setShippingpoint(lp.getShippingpoint());
		}
		else if (orderItem != null) {
			bizevent.setShippingpoint(orderItem.getOrder().getShippingpoint());
		}
		
		final BigDecimal tareWeightT = getKgAsTons(bean.getTareWeightKg());
		final BigDecimal grossWeightT = getKgAsTons(bean.getTotalWeightKg());
		final BigDecimal netQuantityT = getKgAsTons(bean.getNetWeightKg());
		
		if (!endLoadingService.endLoadRail(bizevent, load.getLoadId(), lp,
				tareWeightT, grossWeightT, netQuantityT, bean)) {
			return createLogonEventResponse(ParallelRailResponseCode.LOADING_ABORTED, 
					bean.getVerladeauftragId().toString());
		}
		
		return createLogonEventResponse(ParallelRailResponseCode.OK);
	}
}
