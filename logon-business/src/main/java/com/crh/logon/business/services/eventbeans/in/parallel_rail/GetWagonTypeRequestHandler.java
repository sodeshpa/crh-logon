/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.eventbeans.in.parallel_rail;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.utils.BizEventUtil;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.LogonEventException;
import com.crh.logon.constants.Constant;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.parallel_rail.GetWagonTypeRequest;
import com.holcim.logon.eventbeans.in.parallel_rail.GetWagonTypeResponse;
import com.holcim.logon.eventbeans.in.parallel_rail.ParallelRailResponseCode;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class GetWagonTypeRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
	
	@EJB private DispoServiceLocalInterface dispoService;
	@EJB private OrderServiceLocalInterface orderService;
	
	protected void validate(LogonEventRequest eventBean) {
    	
		final GetWagonTypeRequest bean = (GetWagonTypeRequest) eventBean;
		validateMandatory(FIELD_WERK_ID, bean.getWerkId());
    	validateMandatory(FIELD_VERLADESTRASSE_ID, bean.getVerladestrasseId());
    	validateMandatory(FIELD_WAGON, bean.getWagon());
    }
	
	
	private void finishOrder(AssignmentPartEntity finishedAp, OrderEntity order) {
		
		final BizeventEntity bizevent = new BizeventEntity();
		bizevent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_DELIVERED);
		bizevent.setAssignment(finishedAp.getAssignment());
		bizevent.setShippingpoint(finishedAp.getShippingpoint());
		bizevent.setShipto(finishedAp.getShipto());
		bizevent.setCoordLat(finishedAp.getCoordLat());
		bizevent.setCoordLon(finishedAp.getCoordLon());
		bizevent.setRailwayStation(finishedAp.getShipto().getShiptoSapBp().getRailwayStation());
		bizevent.setOrder(order);
		bizevent.setEventTs(new Date());
		bizevent.setWagon(finishedAp.getAssignment().getWagon());
		// This bizevent should not be persisted here (see OrderServiceBean._setUnloadedImpl)
		// bizevent = monitoringTableService.processBizevent(bizevent);
		orderService.setUnloaded(bizevent, null, false, false, true);
	}


	protected LogonEventResponse perform(LogonEventRequest eventBean) {
		
		final GetWagonTypeRequest bean = (GetWagonTypeRequest) eventBean;
		final ReadOnlyDao<WagonEntity> wagonDao = getDaoProvider().getWagonEntityDao();		
		Filter<WagonEntity> wagonFilter = wagonDao.createFilter();
		wagonFilter.addExpression(Restrictions.eq(WagonEntity.ATTR_WAGON_NO, bean.getWagon()));
		
		final WagonEntity wagon = wagonDao.findSingle(wagonFilter);
		if (wagon == null) {			
			throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, Message.lookup(getLocale(), "parallel.error.unknown_wagon", bean.getWagon())));
		}
		if (!wagon.getActiveFlag().booleanValue()) {
			wagon.setActiveFlag(Boolean.TRUE);
		}
		
		final LoadingpointEntity lp = getLoadingpoint(bean.getWerkId(), bean.getVerladestrasseId());
		if (lp == null) {			
			throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.MANDATORY_FIELD_ERROR, FIELD_VERLADESTRASSE_ID), bean.getVerladestrasseId());
		}

		final ReadOnlyDao<AssignmentEntity> assignmentDao = getDaoProvider().getAssignmentEntityDao();		
		final Filter<AssignmentEntity> assignmentFilter = assignmentDao.createFilter();
		assignmentFilter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_WAGON, wagon));
		assignmentFilter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		assignmentFilter.addExpression(Restrictions.neq(AssignmentEntity.ATTR_ASSIGN_STATUS_CD, Constant.ASSIGNMENT_STATUS_FINISHED));
		assignmentFilter.addExpression(Restrictions.neq(AssignmentEntity.ATTR_ASSIGN_STATUS_CD, Constant.ASSIGNMENT_STATUS_CANCELLED));
		assignmentFilter.addExpression(Order.desc(AssignmentEntity.ATTR_ASSIGNMENT_DAT));
		
		final List<AssignmentEntity> assignmentList = assignmentDao.find(assignmentFilter);
		if (!assignmentList.isEmpty()) {
			for (AssignmentEntity assignment : assignmentList) {
				OrderEntity order = null;
				AssignmentPartEntity finishedAp = null;
				
				@SuppressWarnings("unchecked")
				final List<AssignmentPartEntity> assignmentParts = assignment.getAssignmentParts();
				for (AssignmentPartEntity ap : assignmentParts) {
					if ( (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_UNLOAD) && (ap.getOrder() != null) ) {
						order = ap.getOrder();
					}
					else if (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_FINISHED) {
						finishedAp = ap;
					}
				}
				if ( (order != null) && (order.getOrderStatus().getOrderStatusCd().longValue() > Constant.ORDER_STATUS_LOADED) ) {
					if ((order.getDistributionDest() != null) && (LogonUtil.isReturnDistributionDest(order.getDistributionDest().getDistributionDestCd()))) {
						if (order.getShiptoSapBp().getRailwayStation().getRailwayStationId().equals(lp.getShippingpoint().getRailwayStation().getRailwayStationId())) {
							// Return order's destination is the current plant, close the order
							finishOrder(finishedAp, order);
						}
						else {
							
							throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, Message.lookup(getLocale(), "parallel.error.wagon_assigned_to_return_order")));
						}
					}
					else {
						if ( (wagon.getReturnShiptoSapBp() != null) && (wagon.getReturnShiptoSapBp().getRailwayStation().getRailwayStationId().equals(lp.getShippingpoint().getRailwayStation().getRailwayStationId())) ) {
							if ( (finishedAp != null) && (finishedAp.getPlannedStart().compareTo(new Date()) < 0) ) {
								finishOrder(finishedAp, order);
							}
							else {
									
								throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, Message.lookup(getLocale(), "parallel.error.wagon_assigned_to_order")));
							}
						}
						else {
							
							throw new LogonEventException(createBasicLogonEventResponse(ParallelRailResponseCode.APPLICATION_ERROR, Message.lookup(getLocale(), "parallel.error.wagon_assigned_to_order_other_plant")));
						}
					}
				}
			}
		}

		final ReadOnlyDao<ContainerEntity> containerDao = getDaoProvider().getContainerEntityDao();		
		final Filter<ContainerEntity> containerFilter = containerDao.createFilter();
		// Check the loads for this wagon/containers
		containerFilter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CURRENT_WAGON, wagon));
		containerFilter.addExpression(Restrictions.eq(ContainerEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		final List<ContainerEntity> containerList = containerDao.find(containerFilter);
				
		// Set current railway station and track
		if (lp.getShippingpoint().getRailwayStation() != null) {
			final Date now = new Date();
			if (!lp.getShippingpoint().getRailwayStation().equals(wagon.getLastKnownRailwayStation()))  {
				wagon.setLastKnownRailwayStation(lp.getShippingpoint().getRailwayStation());
				wagon.setArrivalLastKnownStationDat(now);
			}
			else if ((wagon.getArrivalLastKnownStationDat() == null) || now.before(wagon.getArrivalLastKnownStationDat())){ // the == null case should never happen, as the field is coulpled with lastKnownRailwayStation
				wagon.setArrivalLastKnownStationDat(now);
			}
			if (lp.getPlantTrack() != null) {
				long targetTrackPos = 1;
				
				wagonFilter = wagonDao.createFilter();
				wagonFilter.addExpression(Restrictions.eq(WagonEntity.ATTR_CURRENT_PLANT_TRACK_CD, lp.getPlantTrack().getPlantTrackCd()));
				wagonFilter.addExpression(Order.desc(WagonEntity.ATTR_CURRENT_TRACK_POSITION_NO));
				final List<WagonEntity> res = wagonDao.find(wagonFilter);
				if (!res.isEmpty() && ((lp.getWagonLoadFirstPositionFlag() == null) || !lp.getWagonLoadFirstPositionFlag().booleanValue() )){
					final WagonEntity fw = res.get(0);
					if (fw.getCurrentTrackPositionNo() != null){
						targetTrackPos = fw.getCurrentTrackPositionNo().longValue() + 1;
					}
				}				
				String[] wagons = {wagon.getWagonNo()};
				dispoService.setWagonTrack(wagons, lp.getPlantTrack().getPlantTrackCd());
				dispoService.setWagonTrackPos(wagons, targetTrackPos);
			}

		}
		
		// Create the response
		final GetWagonTypeResponse response = (GetWagonTypeResponse)createLogonEventResponse(GetWagonTypeResponse.class, ParallelRailResponseCode.OK);
		response.setWagonType("" + wagon.getWagonType().getWagonTypeName());
		response.setMaxContainers(wagon.getWagonType().getNrContainers());
		response.setWagonComment(wagon.getWagonNoteRailwaySpedition());
		response.setTareWeightKg(wagon.getTareWeightT().movePointRight(3));
		if (!containerList.isEmpty()) {
			for (ContainerEntity container : containerList) {
				response.getContainerLabelList().add(container.getContainerName());
			}
		}
		
		return response;
	}
}
