/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.ExternalOrderServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ExternalOrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.OrderServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.stores.domain.AssignmentStoreInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.ExtOrderEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.views.ExtOrderSearchEntity;
import com.crh.logon.persistence.entities.views.OrderMonitoringEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class ExternalOrderServiceBean extends ExternalOrderServiceDispatcher implements ExternalOrderServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ExternalOrderServiceBean.class);
	
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private OrderServiceLocalInterface orderService;
	@EJB private AssignmentStoreInterface assignmentStore;

	
	public ExtOrderEntity fetchExternalOrder(Long extOrderId) {
		// [[
		ExtOrderEntity result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{ "extOrderId", extOrderId }};
			LOG.entering(this, "fetchExtOrder", params);
		}
		// ]]		
		try {
			final ReadOnlyDao<ExtOrderEntity> orderDao = getDaoProvider().getExtOrderEntityDao();
			return result = orderDao.findById(extOrderId);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public ExtOrderEntity saveExtOrder(ExtOrderEntity extOrder){
		// [[
		ExtOrderEntity result = null;
		Object[][] params = {{"extOrder", extOrder}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveExtOrder", params);
		}
		// ]]
		try {
			final ReadWriteDao<ExtOrderEntity> dao = getDaoProvider().getExtOrderEntityDao();

			//create new order
			if (extOrder.getExtOrderNumber() == null) {
				extOrder.setExtOrderNumber(fetchSequenceNextVal("SLGNC_EXT_ORDER_NUMBER")); 
				extOrder.setOrderStatus(referentialDataService.findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK));
				
				extOrder = dao.persist(extOrder);
			}
			//update existing order
			else {
				extOrder = dao.merge(extOrder);
			}
			
			return result = extOrder;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public ExtOrderEntity deleteExtOrder(Long externalOrderId){
		// [[
		ExtOrderEntity result = null;
		Object[][] params = {{"externalOrderId", externalOrderId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "deleteExtOrder", params);
		}
		// ]]
		try {
			ExtOrderEntity extOrder = fetchExternalOrder(externalOrderId);
			if(extOrder != null) {
				extOrder.setOrderStatus(referentialDataService.findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_DELETED));
				saveExtOrder(extOrder);
				AssignmentEntity ass = assignmentStore.getAssignmentForExtOrderId(externalOrderId);
				if(ass != null) {
					orderService.removeAssignment(ass, null, false);
				}
			}
			return result = extOrder;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}		
	}
	
    public ListRange searchExternalOrders(
			int skip, int num, String sort, boolean asc, HashMap searchCrit,
			Locale locale, TimeZone timezone) throws ParseException {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"sort", sort},
							{"asc", asc + ""},
							{"searchCrit", searchCrit},
							{"locale", locale},
							{"timezone", timezone}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "searchExternalOrders", params);
		}
		// ]]
		try {
			final ReadOnlyDao<ExtOrderSearchEntity> orderSearchDao = getDaoProvider().getExtOrderSearchEntityDao();		
			final Filter<ExtOrderSearchEntity> filter = orderSearchDao.createFilter();
			filter.addExpression(Restrictions.eq(ExtOrderSearchEntity.ATTR_LANG_CD, searchCrit.get(ExtOrderSearchEntity.ATTR_LANG_CD)));

			final String extOrderNumber = (String)searchCrit.get(ExtOrderSearchEntity.ATTR_EXT_ORDER_NUMBER);
			if (!StringUtils.empty(extOrderNumber)) {
				filter.addExpression(Restrictions.eq(ExtOrderSearchEntity.ATTR_EXT_ORDER_NUMBER, Long.valueOf(extOrderNumber)));
			}

			final String extDeliveryNote = (String)searchCrit.get(ExtOrderSearchEntity.ATTR_EXT_DELIVERY_NOTE);
			if (!StringUtils.empty(extDeliveryNote)) {
				filter.addExpression(Restrictions.ilike(ExtOrderSearchEntity.ATTR_EXT_DELIVERY_NOTE, "%" + extDeliveryNote + "%"));
			}

			// --- Delivery from ---
			String deliveryFromS = (String)searchCrit.get(ExtOrderSearchEntity.ATTR_DELIVERY_FROM_DAT);
			if (!StringUtils.empty(deliveryFromS)) {
				Date deliveryFromDate = DateTimeUtils.parseDayMonthYearInGMTZone(deliveryFromS);
				deliveryFromDate = DateTimeUtils.getMorningOfDayInGMTZone(deliveryFromDate);;
				filter.addExpression(Restrictions.ge(ExtOrderSearchEntity.ATTR_DELIVERY_FROM_DAT, deliveryFromDate));
			}
			
			// --- Delivery to ---
			String deliveryToS = (String)searchCrit.get(OrderMonitoringEntity.ATTR_DELIVERY_TO_DAT);
			if (!StringUtils.empty(deliveryToS)) {
				Date deliveryToDate = DateTimeUtils.parseDayMonthYearInGMTZone(deliveryToS);
				deliveryToDate = DateTimeUtils.getMidnightOfDayInGMTZone(deliveryToDate);
				filter.addExpression(Restrictions.le(OrderMonitoringEntity.ATTR_DELIVERY_TO_DAT, deliveryToDate));
			}

			// FIXME Oct 14, 2013/orsika: ExtOrderSearch view and this code also handles only trucks (but not wagons etc)
			final String truckId = (String) searchCrit.get("truckId");
			if (!StringUtils.empty(truckId)) {
				filter.addExpression(Restrictions.eq(ExtOrderSearchEntity.ATTR_TRUCK_ID, Long.valueOf(truckId)));
			}
			
			// --- Status list ---
			final String statusList = (String) searchCrit.get("statusList");
			if (!StringUtils.empty(statusList)) {
				final String[] statArr = statusList.split(",");
				final List<Long> statusCodeList = new ArrayList<Long>();
				for (String stat : statArr) {
					statusCodeList.add(Long.valueOf(stat));
				}
				long statusCdCount = Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL;
				if ( (statusCodeList.size() < statusCdCount) || (((Long)getDaoProvider().getDirectJpaDao().findSingle("SELECT COUNT(x) FROM " + OrderStatusBsEntity.class.getSimpleName() + " x")).longValue() != statusCodeList.size()) ) {
					filter.addExpression(Restrictions.in(ExtOrderSearchEntity.ATTR_ORDER_STATUS_CD, statusCodeList));
				}
			}
			
			filter.addExpression(asc ? Order.asc(sort) : Order.desc(sort));
			final List<ExtOrderSearchEntity> orderList = orderSearchDao.findNRecs(skip, num, filter);
			
			result = new ListRange();
			result.setTotalSize(orderSearchDao.count(filter));
			result.setData(orderList.toArray());
			
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
	
}
