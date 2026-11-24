/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.WagonEventServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.WagonEventServiceLocalInterface;
import com.crh.logon.business.services.utils.BizEventUtil;
import com.crh.logon.business.services.impl.cron.ScheduledTask;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.common.WagonEventEntity;
import com.crh.logon.persistence.entities.refdata.WagonEventStatusBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class WagonEventServiceBean extends WagonEventServiceDispatcher implements WagonEventServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WagonEventServiceBean.class);
	@Resource private UserTransaction tx;
	private static final String STATUS_DEPARTED = "3";
	private static final String STATUS_ARRIVED = "8";
	
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private MonitoringTablesServiceLocalInterface monitoringTablesService;
	
	private WagonEntity findWagon(String wagonNo) {
		
		if (StringUtils.empty(wagonNo)) {
			
			return null;
		}
		final ReadWriteDao<WagonEntity> dao = getDaoProvider().getWagonEntityDao();
		final Filter<WagonEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(WagonEntity.ATTR_WAGON_NO, wagonNo));
		
		return dao.findSingle(filter);
	}
	
	
	private RailwayStationEntity findRailwayStation(String countryCode, String railwayStationNo) {
		
		if ( (countryCode == null) || (railwayStationNo == null) ) {
			
			return null;
		}

		final ReadWriteDao<RailwayStationEntity> dao = getDaoProvider().getRailwayStationEntityDao();
		final Filter<RailwayStationEntity> filter = dao.createFilter();
		
		filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_UIC_COUNTRY_CD, Long.valueOf(countryCode)));
		filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_RAILWAY_STATION_NO, railwayStationNo));
		filter.addExpression(Restrictions.eq(RailwayStationEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		
		return dao.findSingle(filter);
	}
	
	
	private RailwayStationEntity findRailwayStation(String fullCode) {
		
		if ( (fullCode == null) || (fullCode.length() < 3) ) {
			
			return null;
		}
		
		return findRailwayStation(fullCode.substring(0, 2), fullCode.substring(2));
	}
	
	
	private ShiptoEntity findShipto(SapBpEntity sapBp) {

		final ReadWriteDao<ShiptoEntity> dao = getDaoProvider().getShiptoEntityDao();
		final Filter<ShiptoEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP, sapBp));
			
		return dao.findSingle(filter);
	}
	
		
	private void processEvent(WagonEventEntity wagonEvent) {
		// [[
		Object[][] params = {{"wagonEvent", wagonEvent}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "processEvent", params);
		}
		// ]]
		try {
			
			WagonEntity wagon = null;
			if (wagonEvent.getWagonNo() != null) {
				wagon = findWagon(wagonEvent.getWagonNo());
				if (wagon == null) {
					LOG.info("Could not find wagon " + wagonEvent.getWagonNo());
					
					return;
				}
			}
			RailwayStationEntity railwayStation = null;
			RailwayStationEntity nextRailwayStation = null;
			if (wagonEvent.getCurRailwayStation() != null) {
				railwayStation = findRailwayStation(wagonEvent.getCurRailwayStation());
				if (railwayStation == null) {
					
					LOG.info("Could not find railway station " + wagonEvent.getCurRailwayStation());
				}
			}
			if (wagonEvent.getNextRailwayStation() != null) {
				nextRailwayStation = findRailwayStation(wagonEvent.getNextRailwayStation());
				if (nextRailwayStation == null) {
					
					LOG.info("Could not find next railway station " + wagonEvent.getNextRailwayStation());
				}
			}			
			if ( (!STATUS_DEPARTED.equals(wagonEvent.getEventType()) && (!STATUS_ARRIVED.equals(wagonEvent.getEventType()))) ) {
				LOG.debug("Ignoring event type " + wagonEvent.getEventType());
				
				return;
			}

			final ReadWriteDao<AssignmentEntity> assignmentDao = getDaoProvider().getAssignmentEntityDao();
			final Filter<AssignmentEntity> assignmentFilter = assignmentDao.createFilter();
			assignmentFilter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_WAGON, wagon));
			assignmentFilter.addExpression(Restrictions.eq(AssignmentEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			assignmentFilter.addExpression(Restrictions.neq(AssignmentEntity.ATTR_ASSIGN_STATUS_CD, Constant.ASSIGNMENT_STATUS_FINISHED));
			assignmentFilter.addExpression(Restrictions.neq(AssignmentEntity.ATTR_ASSIGN_STATUS_CD, Constant.ASSIGNMENT_STATUS_CANCELLED));
			
			final List<AssignmentEntity> assignmentList = assignmentDao.find(assignmentFilter);
			for (AssignmentEntity assignment : assignmentList) {
				
				final List<AssignmentPartEntity> assignmentPartList = assignment.getAssignmentParts();
				for (AssignmentPartEntity ap : assignmentPartList) {
					final OrderEntity order = ap.getOrder();
					if (order == null) {						
						continue;
					}
					if (order.getOrderStatus().getOrderStatusCd().longValue() <= Constant.ORDER_STATUS_LOADED) {						
						continue;
					}
					
					final BizeventEntity bizevent = new BizeventEntity();
					bizevent.setAssignment(assignment);
					bizevent.setEventTs(wagonEvent.getEventDat());
					bizevent.setOrder(order);
					bizevent.setRailwayStation(railwayStation);
					if ( (railwayStation != null) && (order.getShippingpoint().getRailwayStation() != null) && (order.getShippingpoint().getRailwayStation().getRailwayStationId().equals(railwayStation.getRailwayStationId())) ) {
						bizevent.setShippingpoint(order.getShippingpoint());
					}
					if ( (railwayStation != null) && (order.getShiptoSapBp().getRailwayStation() != null) && (order.getShiptoSapBp().getRailwayStation().getRailwayStationId().equals(railwayStation.getRailwayStationId())) ) {
						bizevent.setShipto(findShipto(order.getShiptoSapBp()));
					}
					bizevent.setWagon(wagon);
				
					if ( (STATUS_DEPARTED.equals(wagonEvent.getEventType())) &&
							((nextRailwayStation != null) && (order != null) && 
							(nextRailwayStation.getRailwayStationId().equals(order.getShiptoSapBp().getRailwayStation().getRailwayStationId()))) ) { 
						// Last departure message before arrival at destination
						bizevent.setShipto(findShipto(order.getShiptoSapBp()));
						bizevent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT);
						orderService.enterUnloadingSite(bizevent, false);
					}
					else {
						final ReadWriteDao<BizeventEntity> bizeventDao = getDaoProvider().getBizeventEntityDao();
						final Filter<BizeventEntity> bizeventFilter = bizeventDao.createFilter();
						bizeventFilter.addExpression(Restrictions.eq(BizeventEntity.ATTR_ASSIGNMENT, assignment));
						bizeventFilter.addExpression(Restrictions.eq(BizeventEntity.ATTR_WAGON, wagon));
						bizeventFilter.addExpression(Restrictions.eq(BizeventEntity.ATTR_BIZEVENT_TYPE, BizEventUtil.BIZEVENT_TYPE_SHIPPING));
						
						final List<BizeventEntity> bizeventList = bizeventDao.find(bizeventFilter);
						if (bizeventList.isEmpty()) {
							// The first message is used to indicate the order is shipping
							bizevent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_SHIPPING);
							orderService.leaveShippingPoint(bizevent);
						}
						else {
							// any other message is handled as GEO position
							bizevent.setBizeventType(BizEventUtil.BIZEVENT_TYPE_POSITION);
						}
					}
					monitoringTablesService.processBizevent(bizevent);
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
	
	@ScheduledTask(name = "wagon_event_proc", second = "35", minute = "3/10", hour = "*", configurationMethodName = "getWagonEventProcCronEnabled")	
	public void processEvents() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "processEvents");
		}
		// ]]
		try {
			final ReadWriteDao<WagonEventEntity> dao = getDaoProvider().getWagonEventEntityDao();
			final Filter<WagonEventEntity> filter = dao.createFilter();
			
			filter.addExpression(Restrictions.eq(WagonEventEntity.ATTR_WAGON_EVENT_STATUS_CD, Constant.WAGON_EVENT_STATUS_NEW));
			filter.addExpression(Order.asc(WagonEventEntity.ATTR_WAGON_EVENT_ID));
			
			final List<WagonEventEntity> wagonEventList = getDaoProvider().getWagonEventEntityDao().find(filter);
			final long maxAgeMs = getConfigBean().getWagonEventProcMaxEventAgeMin()*60*1000;
			for (WagonEventEntity wagonEvent : wagonEventList) {
				try {
					tx.begin();
					if ((new Date().getTime() - wagonEvent.getEventDat().getTime()) > maxAgeMs) {
						wagonEvent.setWagonEventStatus(referentialDataService.findBsEntity(WagonEventStatusBsEntity.class, Constant.WAGON_EVENT_STATUS_IGNORED));
					}
					else {
						processEvent(wagonEvent);
						wagonEvent.setWagonEventStatus(referentialDataService.findBsEntity(WagonEventStatusBsEntity.class, Constant.WAGON_EVENT_STATUS_SUCCESS));
					}
					em.merge(wagonEvent); // TODO boon, 20100421: Is this necessary?
					tx.commit();
				} catch (Exception e) {
					try {
						tx.rollback();
					} catch (SystemException se) {
						throw new LogonException(se);
					}
					LOG.error("Error processing event " + e.getMessage(), e);
					try {
						tx.begin();
						wagonEvent.setWagonEventStatus(referentialDataService.findBsEntity(WagonEventStatusBsEntity.class, Constant.WAGON_EVENT_STATUS_FAILURE));
						em.merge(wagonEvent);
						tx.commit();
					} catch (Exception f) {
						LOG.error("Error setting wagon event status");
						throw new LogonException(f);
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
}
