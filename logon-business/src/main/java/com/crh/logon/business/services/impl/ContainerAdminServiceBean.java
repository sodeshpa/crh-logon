/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.ContainerAdminServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ContainerAdminServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.ContainerHistoryEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class ContainerAdminServiceBean extends ContainerAdminServiceDispatcher implements ContainerAdminServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ContainerAdminServiceBean.class);

	@EJB private RFIDServiceLocalInterface rfidService;
	/**
	 * Fetches all carriers.
	 * @adnlog.exclude return
	 */
	public List fetchCarriers() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCarriers");
		}
		// ]]
		try {
			ReadWriteDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(CarrierEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
			filter.addExpression(Order.asc(CarrierEntity.ATTR_DISPLAY_NAME));
			return dao.find(filter);
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
	 * Fetches history of the given container id
	 * @adnlog.exclude return
	 */
	public List fetchContainerHistory(Long containerId) {
		// [[
		Object[][] params = {{"containerId", containerId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchContainerHistory", params);
		}
		// ]]
		try {
			ReadWriteDao<ContainerHistoryEntity> dao = getDaoProvider().getContainerHistoryEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(ContainerHistoryEntity.ATTR_CONTAINER_ID, containerId));
			filter.addExpression(Order.asc(ContainerHistoryEntity.ATTR_CTL_CRE_TS));
			return dao.find(filter);
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
	 * @param carrierId the id of the carrier to which the containers will be queried
	 * @adnlog.exclude return
	 */
	@AdnTrace
	public List<ContainerEntity> fetchContainersForCarrier(long carrierId, boolean showOnlyActiveContainers) {

			ReadWriteDao<ContainerEntity> dao = getDaoProvider().getContainerEntityDao();
			
			Filter<ContainerEntity> filter = new FilterImpl<ContainerEntity>();
			filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CARRIER_ID, carrierId));
			filter.addExpression(Order.asc(ContainerEntity.ATTR_CONTAINER_NAME));
			if (showOnlyActiveContainers) {
				filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_ACTIVE_FLAG, true));
			}
			
			List<ContainerEntity> list = dao.find(filter);
			for (Iterator<ContainerEntity> it = list.iterator(); it.hasNext();) {
				ContainerEntity container = (ContainerEntity) it.next();
				if (container.getRfidobject() != null) { 
					container.getRfidobject().initialize();
				}
			}
			
			return list;
	}
	
	
	/**
	 * @param carrierId the id of the carrier to which the containers will be queried
	 * @adnlog.exclude return
	 */
	public ContainerEntity fetchContainer(long containerId) {
		// [[
		Object[][] params = {{"containerId", containerId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchContainer", params);
		}
		// ]]
		try {
			ReadWriteDao<ContainerEntity> dao = getDaoProvider().getContainerEntityDao();
			
			Filter filter = new FilterImpl();
			filter.addExpression(Order.asc(ContainerEntity.ATTR_CONTAINER_ID));
			ContainerEntity result = dao.findById(containerId);
			result.getRfidobject().initialize();
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

	/**
	 * returns true if values (name and no) are unique
	 * @param containerName check uniqueness
	 * @param containerNo check uniqueness
	 * @return
	 */
	public Map checkContainerUniqueness(Long containerId, String containerName, String containerNo) {
		// [[
		Map result = null;
		Object[][] params = {{"containerId", containerId},
							{"containerName", containerName},
							{"containerNo", containerNo}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "checkContainerUniqueness", params);
		}
		// ]]
		try {
			
			Map map = new HashMap();
			ReadWriteDao<ContainerEntity> dao = getDaoProvider().getContainerEntityDao();
			
			//check container name uniqueness
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CONTAINER_NAME, containerName));
			if (containerId != null) {
				filter.addExpression(Restrictions.neq(ContainerEntity.ATTR_CONTAINER_ID, containerId));
			}
			
			List nameList = dao.find(filter);
			if ((nameList == null) || (nameList.isEmpty())){
				map.put(ContainerEntity.ATTR_CONTAINER_NAME, Boolean.TRUE);
			} 
			else {
				map.put(ContainerEntity.ATTR_CONTAINER_NAME, Boolean.FALSE);
			}
			
			//check container no uniqueness
			filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(ContainerEntity.ATTR_CONTAINER_NO, containerNo));
			if (containerId != null) {
				filter.addExpression(Restrictions.neq(ContainerEntity.ATTR_CONTAINER_ID, containerId));
			}
			
			List noList = dao.find(filter);
			if ((noList == null) || (noList.isEmpty())){
				map.put(ContainerEntity.ATTR_CONTAINER_NO, Boolean.TRUE);
			} 
			else {
				map.put(ContainerEntity.ATTR_CONTAINER_NO, Boolean.FALSE);
			}
			
			return result = map;
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public ContainerEntity updateContainer(ContainerEntity container) {
		// [[
		ContainerEntity result = null;
		Object[][] params = {{"container", container}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateContainer", params);
		}
		// ]]
		try {
			ReadWriteDao<ContainerEntity> dao = getDaoProvider().getContainerEntityDao();
			
			//save rfid
			if (container.getRfidobject() != null) {
				AbstractRFIDBean bean = RFIDBean.ContainerRFIDBean(container);
				RfidobjectEntity rfidobject;
				
				if (container.getRfidobject().getRfidobjectId() == null) {
					rfidobject = rfidService.validateAndPersistRFIDObject(Constant.RFIDOBJECT_TYPE_CONTAINER, bean);
				}
				else {
					rfidobject = rfidService.mergeRfIdObject(bean, Constant.RFIDOBJECT_TYPE_CONTAINER);
				}
				container.setRfidobject(rfidobject);
			}
			
			//save container
			if (container.getContainerId() == null) {
				//new one
				container = dao.persist(container);
			}
			else {
				container = dao.merge(container);
			}
			
			return result = container;
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
