/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.PDAAdminServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.MopEventServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PDAAdminServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.eventbeans.LogonEventOutBean;
import com.crh.logon.eventbeans.LogonEventOutBeanFactory;
import com.crh.logon.eventbeans.LogonMessageOutBean;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.PdaDeviceEntity;
import com.crh.logon.persistence.entities.refdata.SimcardBsEntity;
import com.crh.logon.persistence.entities.views.PdaDeviceCarrierEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class PDAAdminServiceBean extends PDAAdminServiceDispatcher implements PDAAdminServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PDAAdminServiceBean.class);

    @EJB private RefDataGenericsServiceLocalInterface referentialDataService;
    @EJB private MopEventServiceLocalInterface mopEventService;
	
	/**
	 * fetch the carriers from db
	 * 
	 * @return list of CarrierEntities
	 */
	public List fetchPoolEntityList() {
		// [[
		List result1 = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolEntityList");
		}
		// ]]
		try {
			
			ReadWriteDao<CarrierEntity> dao = getDaoProvider().getCarrierEntityDao();
			Filter<CarrierEntity> filter = dao.createFilter();
			filter.addExpression(Order.asc(CarrierEntity.ATTR_DISPLAY_NAME));
			List result = dao.find(filter);
			
			return result1 = result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}
	
	public ListRange fetchPDADeviceCarrierEntityList(int skip, int num, String sort, boolean asc, HashMap searchCrit) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"sort", sort},
							{"asc", asc + ""},
							{"searchCrit", searchCrit}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPDADeviceCarrierEntityList", params);
		}
		// ]]
		try {
			ReadOnlyDao<PdaDeviceCarrierEntity> pdaDao = getDaoProvider().getPdaDeviceCarrierEntityDao();
			
			Filter filter = new FilterImpl();
			
			//			if (!StringUtils.empty((String)searchCrit.get(PdaDeviceEntity.ATTR_ACTIVE_FLAG))) {
			//				filter.addExpression(Restrictions.eq(PdaDeviceEntity.ATTR_ACTIVE_FLAG, Boolean.valueOf((String)searchCrit.get(PdaDeviceEntity.ATTR_ACTIVE_FLAG))));
			//			}
			
			if (!StringUtils.empty((String)searchCrit.get(PdaDeviceCarrierEntity.ATTR_CARRIER_ID))) {
				filter.addExpression(Restrictions.eq(PdaDeviceCarrierEntity.ATTR_CARRIER_ID, Long.valueOf((String)searchCrit.get(PdaDeviceCarrierEntity.ATTR_CARRIER_ID))));
			}
			
			filter.addExpression(asc ? Order.asc(sort) : Order.desc(sort));
			
			ListRange pdaCarrEntities = new ListRange();
			pdaCarrEntities.setData(pdaDao.findNRecs(skip, num, filter).toArray());
			pdaCarrEntities.setTotalSize(pdaDao.count(filter));
			
			return result = pdaCarrEntities;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public PdaDeviceEntity persistPDADevice(PdaDeviceEntity newPDA) {
		// [[
		PdaDeviceEntity result = null;
		Object[][] params = {{"newPDA", newPDA}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "persistPDADevice", params);
		}
		// ]]
		try {
			DaoProvider daoProvider = getDaoProvider();
			ReadWriteDao<PdaDeviceEntity> pdaDeviceDao = daoProvider.getPdaDeviceEntityDao();
			
			newPDA = pdaDeviceDao.persist(newPDA);
			
			return result = newPDA;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public PdaDeviceEntity mergePDADevice(PdaDeviceEntity pdaToModify) {
		// [[
		PdaDeviceEntity result = null;
		Object[][] params = {{"pdaToModify", pdaToModify}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "mergePDADevice", params);
		}
		// ]]
		try {
			DaoProvider daoProvider = getDaoProvider();
			ReadWriteDao<PdaDeviceEntity> pdaDeviceDao = daoProvider.getPdaDeviceEntityDao();
			
			pdaToModify = pdaDeviceDao.merge(pdaToModify);
			
			return result = pdaToModify;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public PdaDeviceEntity fetchPdaDeviceEntityById(long id) {
		// [[
		PdaDeviceEntity result = null;
		Object[][] params = {{"id", id + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPdaDeviceEntityById", params);
		}
		// ]]
		try {
			DaoProvider daoProvider = getDaoProvider();
			ReadWriteDao<PdaDeviceEntity> pdaDeviceDao = daoProvider.getPdaDeviceEntityDao();
			
			PdaDeviceEntity pda = pdaDeviceDao.findById(id);
			return result = pda;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	
	public List fetchSimCardBsEntityList(int skip, int num, String query, boolean bySimNr) {
				// [[
				List result = null;
				Object[][] params = {{"skip", skip + ""},
									{"num", num + ""},
									{"query", query},
									{"bySimNr", bySimNr + ""}};
				boolean logDebugEnabled = LOG.isDebugEnabled();
				if (logDebugEnabled) {
					LOG.entering(this, "fetchSimCardBsEntityList", params);
				}
				// ]]
				try {
					return result = fetchSimCardBsEntityListImpl(skip, num, query, bySimNr, false);
				}
				finally {
				// [[
					if (logDebugEnabled) {
						LOG.exiting(result);
					}
				// ]]
				}
	}
	
	public long countSimCardBsEntityList(String query, boolean bySimNr) {
			// [[
			long result = 0L;
			Object[][] params = {{"query", query},
								{"bySimNr", bySimNr + ""}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "countSimCardBsEntityList", params);
			}
			// ]]
			try {
				List list =  fetchSimCardBsEntityListImpl(0, 2, query, bySimNr, true);
				
				result = (Long) list.get(0);
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
	
	private List fetchSimCardBsEntityListImpl(int skip, int num, String query, boolean bySimNr, boolean count) {
			// [[
			List result = null;
			Object[][] params = {{"skip", skip + ""},
								{"num", num + ""},
								{"query", query},
								{"bySimNr", bySimNr + ""},
								{"count", count + ""}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(this, "fetchSimCardBsEntityListImpl", params);
			}
			// ]]
			try {
				List simCardList = new ArrayList();
				List resutltList = new ArrayList();
				
				Filter filter = new FilterImpl();
				
				/* First replace special characters with escape sequences 
				 * We use | as the escape character */
				query = query.replace("|", "||");
				query = query.replace("%", "|%");
				query = query.replace("_", "|_");
				/* now replace the query wildcard characlter '*'
				 * by the JPAQL escape character*/
				query = query.replace('*', '%');
				/* finally add a wildcard character at the end of the String*/
				query = query += '%';
				/* now convert to uppercase */
				query = query.toUpperCase();
				
				if (bySimNr) {
					filter.addExpression(Restrictions.like(SimcardBsEntity.ATTR_SIMCARD_NUMBER, '%' + query + '%'));
					filter.addExpression(Order.asc(SimcardBsEntity.ATTR_SIMCARD_NUMBER));
				}
				else {
					filter.addExpression(Restrictions.like(SimcardBsEntity.ATTR_PHONE_NUMBER, '%' + query + '%'));
					filter.addExpression(Order.asc(SimcardBsEntity.ATTR_PHONE_NUMBER));
				}
				simCardList = referentialDataService.findBsEntityFiltered(SimcardBsEntity.class, filter);
				
				if (count) {
					int cnt = simCardList.size();
					resutltList.add(Long.parseLong(Integer.toString(cnt)));
					return result =  resutltList;
				}
				return result = simCardList;
			}
			finally {
			// [[
				if (logDebugEnabled) {
					LOG.exiting(result);
				}
			// ]]
			}
	}
	
	
	public boolean testPDAReady(String pdaId) {
		// [[
		boolean result = false;
		Object[][] params = {{"pdaId", pdaId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "testPDAReady", params);
		}
		// ]]
		try {
			
			final String eventType = "readyForOperation";
			final String parameterName = "readyForOperation";
			final String testMessage = "PDA ready for operation check";
			
			HashMap logonData = new HashMap();
			logonData.put(parameterName, testMessage);
			
			LogonEventOutBean test = LogonEventOutBeanFactory.create(eventType, logonData);
			test.setEventType(eventType);
			
			LogonMessageOutBean message = new LogonMessageOutBean();
			message.setSenderPDAId(pdaId);
			message.setSendersTime(new Date());
			message.setSessionId("");
			List events = new ArrayList();
			events.add(test);
			message.setLogonEventBeans(events);
						
			try {
				mopEventService.sendMessage(message);
				
				return result = true;
			} catch (Exception e) {
				
				return result = false;
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
	
//	public SimcardBsEntity findSimcardBySimcardNumber(String simcardNumber) {
//		Filter simcardFilter = new FilterImpl();
//		simcardFilter.addExpression(Restrictions.eq(SimcardBsEntity.ATTR_SIMCARD_NUMBER, simcardNumber));
//		
//		List simcard = getRefDataGenericsService().findBsEntityFiltered(SimcardBsEntity.class, simcardFilter);
//		
//		return simcard
//	}
    
}
