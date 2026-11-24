/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.MonitoringTablesServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.BizeventEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.monitoring.ContainerPositionEntity;
import com.crh.logon.persistence.entities.monitoring.TrailerPositionEntity;
import com.crh.logon.persistence.entities.monitoring.TruckPositionEntity;
import com.crh.logon.persistence.entities.monitoring.WagonPositionEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.LogonException;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class MonitoringTablesServiceBean extends MonitoringTablesServiceDispatcher implements MonitoringTablesServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(MonitoringTablesServiceBean.class);

	@EJB private MonitoringTablesServiceLocalInterface monitoringTablesService;

	public Map<Long, Date> findBizeventTimesByAssignmentIds(Set<Long> assignmentIds, long bizEventType) {
			// [[
			Map<Long,Date> result = null;
			Object[][] params = {{"assignmentIds", assignmentIds}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "findBizeventTimesByAssignmentIds", params);
			}
			// ]]
			try {

				if (assignmentIds == null || assignmentIds.isEmpty()) {
					return Collections.<Long, Date>emptyMap();
				}

				Map<Long,Date> events = new HashMap<Long, Date>();
				
				DaoProvider daoProvider = getDaoProvider();
				
				ReadWriteDao<BizeventEntity> dao = daoProvider.getBizeventEntityDao();
				
				List<Long> assignmentIdList = new ArrayList<Long>(assignmentIds);
				
				Filter<BizeventEntity> filter = new FilterImpl<BizeventEntity>();
				filter.addExpression(Restrictions.eq(BizeventEntity.ATTR_BIZEVENT_TYPE_CD, bizEventType));
				filter.addExpression(DbUtils.createSplittedInRestrictionOfIds(BizeventEntity.ATTR_ASSIGNMENT_ID, assignmentIdList));
				
				List<BizeventEntity> bizEvents = dao.find(filter);
				for (BizeventEntity event : bizEvents) {
					Date date = event.getEventTs();
					if (date != null) {
						AssignmentEntity assignment = event.getAssignment();
						events.put(assignment.getAssignmentId(), date);
					}
				}
				
				return result = events;
				
			}
			finally {
			// [[
				if (logDebugEnabled) {
					LOG.exiting(result);
				}
			// ]]
			}
	}
	

	public BizeventEntity processBizevent(BizeventEntity bizevent) {
		// [[
		BizeventEntity result = null;
		Object[][] params = {{"bizevent", bizevent}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "processBizevent", params);
		}
		// ]]
		try {
			
			final ReadWriteDao<BizeventEntity> dao = getDaoProvider().getBizeventEntityDao();
			bizevent = dao.persist(bizevent, true);

			if (isContainerPositionEntityNeeded(bizevent) || isTrailerPositionEntityNeeded(bizevent) ||
					isTruckPositionEntityNeeded(bizevent) || isWagonPositionEntityNeeded(bizevent)) {
				
				try {
					monitoringTablesService._processBizevent(bizevent);
				}
				catch (Exception e) {
					try {
						monitoringTablesService._processBizevent(bizevent);
					}
					catch (Exception ex) {
						throw new LogonException("processBizevent 2nd try failed!", ex);
					}
				}
			}
			
			return result = bizevent;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void _processBizevent(BizeventEntity bizevent) {
		// [[
		BizeventEntity result = null;
		Object[][] params = {{"bizevent", bizevent}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "_processBizevent", params);
		}
		// ]]
		try {
			if (isContainerPositionEntityNeeded(bizevent)) {
				doContainerPositionEntity(bizevent);
			}
			if (isTrailerPositionEntityNeeded(bizevent)) {
				doTrailerPositionEntity(bizevent);
			}
			if (isTruckPositionEntityNeeded(bizevent)) {
				doTruckPositionEntity(bizevent);
			}
			if (isWagonPositionEntityNeeded(bizevent)) {
				doWagonPositionEntity(bizevent);
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}	
	}
	
	private final static boolean isContainerPositionEntityNeeded(BizeventEntity bizevent) {
		return ((bizevent.getCoordLat() != null) && (bizevent.getCoordLon() != null) && (bizevent.getContainer() != null));
	}
	
	private final void doContainerPositionEntity(BizeventEntity bizevent) {
		// [[
		Object[][] params = {{"bizevent", bizevent}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doContainerPositionEntity", params);
		}
		// ]]
		try {			
			final ContainerEntity container = bizevent.getContainer();
			final ReadWriteDao<ContainerPositionEntity> dao = getDaoProvider().getContainerPositionEntityDao();
			final Filter<ContainerPositionEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(ContainerPositionEntity.ATTR_CONTAINER, container));
			
			ContainerPositionEntity containerPosition = dao.findSingle(filter);
			final boolean isMerge = (containerPosition != null);
			if (!isMerge) {
				containerPosition = new ContainerPositionEntity();
			}
			
			containerPosition.setContainer(container);
			containerPosition.setCoordLat(bizevent.getCoordLat());
			containerPosition.setCoordLon(bizevent.getCoordLon());
			containerPosition.setBizeventId(bizevent.getBizeventId());
			containerPosition.setBizeventType(bizevent.getBizeventType());
			containerPosition.setEventTs(bizevent.getEventTs());
			
			if (isMerge) {
				dao.merge(containerPosition, true);
			}
			else {
				dao.persist(containerPosition, true);
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

	private final static boolean isTrailerPositionEntityNeeded(BizeventEntity bizevent) {
		return ((bizevent.getCoordLat() != null) && (bizevent.getCoordLon() != null) && (bizevent.getTrailer() != null));
	}
	
	private final void doTrailerPositionEntity(BizeventEntity bizevent) {
		// [[
		Object[][] params = {{"bizevent", bizevent}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doTrailerPositionEntity", params);
		}
		// ]]
		try {			
			final TrailerEntity trailer = bizevent.getTrailer();
			final ReadWriteDao<TrailerPositionEntity> dao = getDaoProvider().getTrailerPositionEntityDao();
			final Filter<TrailerPositionEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(TrailerPositionEntity.ATTR_TRAILER, trailer));
			
			TrailerPositionEntity trailerPosition = dao.findSingle(filter);
			final boolean isMerge = (trailerPosition != null);
			if (!isMerge) {
				trailerPosition = new TrailerPositionEntity();
			}
			
			trailerPosition.setTrailer(trailer);
			trailerPosition.setCoordLat(bizevent.getCoordLat());
			trailerPosition.setCoordLon(bizevent.getCoordLon());
			trailerPosition.setBizeventId(bizevent.getBizeventId());
			trailerPosition.setBizeventType(bizevent.getBizeventType());
			trailerPosition.setEventTs(bizevent.getEventTs());
			
			if (isMerge) {
				dao.merge(trailerPosition, true);
			}
			else {
				dao.persist(trailerPosition, true);
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

	private final static boolean isTruckPositionEntityNeeded(BizeventEntity bizevent) {
		return ((bizevent.getCoordLat() != null) && (bizevent.getCoordLon() != null) && (bizevent.getTruck() != null));
	}
	
	private final void doTruckPositionEntity(BizeventEntity bizevent) {
		// [[
		Object[][] params = {{"bizevent", bizevent}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doTruckPositionEntity", params);
		}
		// ]]
		try {			
			final TruckEntity truck = bizevent.getTruck();
			final ReadWriteDao<TruckPositionEntity> dao = getDaoProvider().getTruckPositionEntityDao();
			final Filter<TruckPositionEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(TruckPositionEntity.ATTR_TRUCK, truck));
			
			TruckPositionEntity truckPosition = dao.findSingle(filter);
			final boolean isMerge = (truckPosition != null);
			if (!isMerge) {
				truckPosition = new TruckPositionEntity();
			}
			
			truckPosition.setTruck(truck);
			truckPosition.setCoordLat(bizevent.getCoordLat());
			truckPosition.setCoordLon(bizevent.getCoordLon());
			truckPosition.setBizeventId(bizevent.getBizeventId());
			truckPosition.setBizeventType(bizevent.getBizeventType());
			truckPosition.setEventTs(bizevent.getEventTs());
			
			if (isMerge) {
				dao.merge(truckPosition, true);
			}
			else {
				dao.persist(truckPosition, true);
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

	private final static boolean isWagonPositionEntityNeeded(BizeventEntity bizevent) {
		return ((bizevent.getCoordLat() != null) && (bizevent.getCoordLon() != null) && (bizevent.getWagon() != null));
	}
	
	private final void doWagonPositionEntity(BizeventEntity bizevent) {
		// [[
		Object[][] params = {{"bizevent", bizevent}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doWagonPositionEntity", params);
		}
		// ]]
		try {			
			final WagonEntity wagon = bizevent.getWagon();
			final ReadWriteDao<WagonPositionEntity> dao = getDaoProvider().getWagonPositionEntityDao();
			final Filter<WagonPositionEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(WagonPositionEntity.ATTR_WAGON, wagon));
			
			WagonPositionEntity wagonPosition = dao.findSingle(filter);
			final boolean isMerge = (wagonPosition != null);
			if (!isMerge) {
				wagonPosition = new WagonPositionEntity();
			}
			
			wagonPosition.setWagon(wagon);
			wagonPosition.setCoordLat(bizevent.getCoordLat());
			wagonPosition.setCoordLon(bizevent.getCoordLon());
			wagonPosition.setBizeventId(bizevent.getBizeventId());
			wagonPosition.setBizeventType(bizevent.getBizeventType());
			wagonPosition.setEventTs(bizevent.getEventTs());
			
			if (isMerge) {
				dao.merge(wagonPosition, true);
			}
			else {
				dao.persist(wagonPosition, true);
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