/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;

import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.EndLoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.AssignmentUtils;
import com.crh.logon.business.services.utils.ConfSeqLock;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.EventSource;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.EndLoadMopRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class EndLoadMopRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(EndLoadMopRequestHandler.class);

	private static final ConfSeqLock WITHOUT_LOCK = null;

	private static final boolean VALIDATE_COMPOSITION = true;

	private static String FIELD_ACCURATE_WEIGHTS = "accurateWeights";
	private static String FIELD_LOADED_QUANTITY_IN_PIECES = "loadedQuantityInPieces";
	
	@EJB private EndLoadingServiceLocalInterface endLoadingService;
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private LoadingServiceLocalInterface loadingService;
    
    protected void validate(LogonEventRequest eventBean) {
    	
    	EndLoadMopRequest bean = (EndLoadMopRequest)eventBean;
    	validateMandatory(FIELD_ASSIGNMENT_ID, bean.getAssignmentId());
    	validateMandatory(FIELD_TARE_WEIGHT, bean.getTareWeightKg());
    	validateMandatory(FIELD_TOTAL_WEIGHT, bean.getTotalWeightKg());
    	validateMandatory(FIELD_NET_WEIGHT, bean.getNetWeightKg());
    	validateMandatory(FIELD_ACCURATE_WEIGHTS, bean.isAccurateWeights());
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
			final EndLoadMopRequest bean = (EndLoadMopRequest)eventBean;
			
			// do not persist yet, persist and process just before calling endLoad
			final BizeventEntity bizevent = createBizevent(Constant.BIZEVENT_TYPE_LOADED, bean, true, false);
			
			final AssignmentPartEntity part = getAssignmentPart(bean.getAssignmentPartId());
			if (part == null) {
				throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "Loading based on assignmentID only is not supported"));
			}
			AssignmentEntity assignment = part.getAssignment();
			bizevent.setAssignment(assignment);
			
			List<OrderEntity> orders = AssignmentUtils.getOrdersFromAssignment(assignment);
			verifyNoPlanningMaterial(orders);
			
			ShippingpointEntity shippingPoint = getShippingpoint(orders, part);
			bizevent.setShippingpoint(shippingPoint);
			
			final LoadingpointEntity loadingpointEntity = getLoadingpoint(bizevent, 
					bean.getLoadingpointRfidIdentity(), bean.getLoadingpointLabel());
			if (loadingpointEntity != null) {
				bizevent.setShippingpoint(loadingpointEntity.getShippingpoint());
			}
			
			OrderEntity firstOrder = orders.isEmpty() ? null : orders.get(0);
			BigDecimal loadedQuantityInPieces = null;
			if (LogonUtil.isReturnPalletOnly(firstOrder)) {
				loadedQuantityInPieces = bean.getLoadedQuantityInPieces();
				validateMandatory(FIELD_LOADED_QUANTITY_IN_PIECES, bean.getLoadedQuantityInPieces());
			}
			final BigDecimal tareWeight = getKgAsTons(bean.getTareWeightKg());
			final BigDecimal netWeight = getKgAsTons(bean.getNetWeightKg());
			final BigDecimal totalWeight;
			
			final BigDecimal totalWeightReceived = getKgAsTons(bean.getTotalWeightKg());
			if ( ( tareWeight != null && tareWeight.compareTo(BigDecimal.ZERO ) == 0) &&
					( totalWeightReceived != null && netWeight != null && totalWeightReceived.compareTo(netWeight) == 0)) {
				
				totalWeight = null;
			}
			else {
				totalWeight = totalWeightReceived;
			}
			
			final boolean accurateWeights =  bean.isAccurateWeights();
			
			final LoadEntity load = part.getLoad();
			
			if (load != null) {
				try {
					endLoadingService.endLoadMop(bizevent, load.getLoadId(), loadingpointEntity, tareWeight, netWeight,
							totalWeight, loadedQuantityInPieces, accurateWeights);
				}
				catch (LogonException e) {
					if (e.getReason().equals(Reason.RES_ERROR_INVALID_MEASUREMENT)) {
						throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.INVALID_MEASUREMENT));
					}
					else {
						throw e;
					}
				}
				for (OrderEntity order : loadingService.findOrdersForLoadId(load.getLoadId())) {
					kafkaPublisherService.publishMOPEvent(order, bean);
				}
			}
			// special external orders case
			else {
				orderService.endLoadAndSendGoodsMovements(bizevent, load, loadingpointEntity, 
						tareWeight, totalWeight, netWeight, accurateWeights, 
						VALIDATE_COMPOSITION,
						EventSource.MOP, WITHOUT_LOCK);
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
