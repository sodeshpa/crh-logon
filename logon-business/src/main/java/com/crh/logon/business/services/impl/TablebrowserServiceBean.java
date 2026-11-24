/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;
import javax.persistence.OptimisticLockException;

import com.crh.logon.business.services.dispatchers.TablebrowserServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.TablebrowserServiceLocalInterface;
import com.crh.logon.business.services.utils.TableBrowserSequenceWrapper;
import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;

import ch.nevis.gdi.jpa.DirectJpaDao;
import ch.nevis.gdi.jpa.DirectJpaDaoImpl;
import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.AuditEntity;
import com.crh.logon.persistence.entities.refdata.AuditActionBsEntity;
import com.crh.logon.persistence.entities.tablebrowser.TableMeta;
import com.crh.logon.persistence.entities.views.ColumnInfoEntity;
import com.crh.logon.persistence.entities.views.TableInfoEntity;

/**
 * the methods in this service are to be used by the tablebrowser *only*!
 * 
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class TablebrowserServiceBean extends TablebrowserServiceDispatcher implements TablebrowserServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TablebrowserServiceBean.class);
	
    @EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	
	/**
	 * This custom log4j filter will filter any message that belongs to the 
	 * contained logger names and threads. Before a custom table browser query 
	 * is made, the thread will be registered in this filter, supressing any 
	 * warnings/errors, after the query the thread will be de-registered from 
	 * this filter.
	 */
	private class CustomQueryLog4jFilter extends org.apache.log4j.spi.Filter {
		
		private Collection loggerNames;
		private Collection filteredThreads;
		private ThreadLocal suppressedEvents;
		
		public CustomQueryLog4jFilter() {
			filteredThreads = Collections.synchronizedSet(new HashSet());
			loggerNames = Collections.synchronizedSet(new HashSet());
			suppressedEvents = new ThreadLocal();
		}
		
		public String getSuppressedLogMessages() {
			
			StringBuilder sb = new StringBuilder();
			List eventList = (List)suppressedEvents.get();
			if  ( (eventList != null) && (!eventList.isEmpty()) ) {
				for (Iterator it=eventList.iterator();it.hasNext();) {
					LoggingEvent event = (LoggingEvent)it.next();
					sb.append(event.getLoggerName() + ": " + (event.getRenderedMessage()!=null?event.getRenderedMessage().replaceAll("\\r?\\n$", ""):"") + (it.hasNext()?System.getProperty("line.separator"):""));
				}
				
				return sb.toString();
			}
			
			return null;
		}
		
		public void addLoggerName(String loggerName) {
			
			loggerNames.add(loggerName);
		}
		
		public Collection getLoggerNames() {
			
			return loggerNames;
		}
		
		public void addThread(Thread t) {
			
			filteredThreads.add(t);
		}
		
		public void removeThread(Thread t) {
			
			filteredThreads.remove(t);
		}
		
		@Override
		public int decide(LoggingEvent event) {
			
			if ( (loggerNames.contains(event.getLoggerName())) &&
				(filteredThreads.contains(Thread.currentThread())) &&
				(event.getLevel().isGreaterOrEqual(Priority.WARN)) ) {
				List eventList = (List)suppressedEvents.get();
				if (eventList == null) {
					eventList = new ArrayList();
					suppressedEvents.set(eventList);
				}
				if (!eventList.contains(event)) {
					eventList.add(event);
				}
				
				return org.apache.log4j.spi.Filter.DENY;
			}

			return org.apache.log4j.spi.Filter.NEUTRAL;
		}
	}
	private static CustomQueryLog4jFilter log4jFilter; 
	
	
	/**
	 * generic data fetch (paged)
	 * 
	 * @adnlog.exclude return
	 */
	public List fetchGeneric(int skip, int num, String query, Object[] values) {
		// [[
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query},
							{"values", values}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchGeneric", params);
		}
		// ]]
		try {
			DirectJpaDao dao = getDaoProvider().getDirectJpaDao();
			List rows = dao.findNRecsNative(skip, num, query, values);
			return  rows;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	
	private void registerLog4jFilter() {
		
		for (Iterator it=log4jFilter.getLoggerNames().iterator();it.hasNext();) {
			String loggerName = (String)it.next();
			Category log4jLogger = Logger.getLogger(loggerName);
			while (log4jLogger != null) {
				Enumeration allAppenders = log4jLogger.getAllAppenders();
				// Only add the filter to the JDBCExceptionReporter appender. If none is defined, no filter will be added.
				while (allAppenders.hasMoreElements()) {
					Appender appender = (Appender)allAppenders.nextElement();
					boolean addFilter = true;
					org.apache.log4j.spi.Filter existingFilter = appender.getFilter();
					while (existingFilter != null) {
						if (existingFilter == log4jFilter) {
							addFilter = false;
						}
						existingFilter = existingFilter.next;
					}
					if (addFilter) {
						LOG.debug("Adding CustomQueryLog4jFilter to " + appender.getName());
						appender.addFilter(log4jFilter);
					}
				}
				log4jLogger = log4jLogger.getParent();
			}
		}
	}
	
	
	public List fetchGenericUserQuery(int skip, int num, String query, Object[] values) {
		// [[
		List result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"query", query},
							{"values", values}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchGenericUserQuery", params);
		}
		// ]]
		try {
			synchronized (TablebrowserServiceBean.class) {
				if (log4jFilter == null) {
					log4jFilter = new CustomQueryLog4jFilter();
					log4jFilter.addLoggerName(SqlExceptionHelper.class.getName());
					log4jFilter.addLoggerName(DirectJpaDaoImpl.class.getName());
				}
				try {
					registerLog4jFilter();
				}
				catch (Throwable t) {
					LOG.debug("Could not setup log4j filter " + t.getMessage());
				}
			}
			if ( (log4jFilter != null) && (query != null) ) {
				LOG.debug("Adding thread to log filter " + Thread.currentThread().getName());
				log4jFilter.addThread(Thread.currentThread());
			}
			DirectJpaDao dao = getDaoProvider().getDirectJpaDao();
			List rows = dao.findNRecsNative(skip, num, query, values);
			
			return result =  rows;
		}
		finally {
			if ( (log4jFilter != null) && (query != null) ) {
				LOG.debug("Removing thread to log filter " + Thread.currentThread().getName());
				log4jFilter.removeThread(Thread.currentThread());
				String msg = log4jFilter.getSuppressedLogMessages();
				if (msg != null) {
					LOG.info("Filtered messages: " + System.getProperty("line.separator") + msg);
				}
			}
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}


	/**
	 * generic data fetch
	 */
	public List fetchAllRowsGeneric(String query, Object[] values) {
		// [[
		List result = null;
		Object[][] params = {{"query", query},
							{"values", values}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAllRowsGeneric", params);
		}
		// ]]
		try {
			DirectJpaDao dao = getDaoProvider().getDirectJpaDao();
			List rows = dao.findNative(query, values);
			return result = rows;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * delete data from a table
	 */
	public void deleteGeneric(Object targetEntryId, String tableName, UserEntity user, String where, Object[] pkVals) {
		// [[
		Object[][] params = {{"targetEntryId", targetEntryId},
							{"tableName", tableName},
							{"user", user},
							{"where", where},
							{"pkVals", pkVals}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "deleteGeneric", params);
		}
		// ]]
		try {
			DirectJpaDao dao = getDaoProvider().getDirectJpaDao();
			dao.execNative("delete from " + Constant.SCHEMA_USER_PREPEND + tableName + " where " + where , pkVals);
			audit(targetEntryId, tableName, user, Constant.AUDIT_ACTION_TABLEBROWSER_DELETE);
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
	 * update existing data in a table
	 */
	public void updateGenericWithOL(Object targetEntryId, String tableName, UserEntity user, String set, Object[] setVals, String where,
			Object[] pkVals, BigDecimal tcn) {
		// [[
		Object[][] params = {{"targetEntryId", targetEntryId},
							{"tableName", tableName},
							{"user", user},
							{"set", set},
							{"setVals", setVals},
							{"where", where},
							{"pkVals", pkVals},
							{"tcn", tcn}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateGenericWithOL", params);
		}
		// ]]
		try {
			DirectJpaDao dao = getDaoProvider().getDirectJpaDao();
			
			Object[] pkValsAndTcn = new Object[pkVals.length + 1];
			System.arraycopy(pkVals, 0, pkValsAndTcn, 0, pkVals.length);
			pkValsAndTcn[pkValsAndTcn.length-1] = tcn;
			long tcnLong = 0;
			if (tcn != null) {
				tcnLong = tcn.longValue();
			}
			
			Object[] vals = new Object[setVals.length + pkVals.length + 4]; 
			System.arraycopy(pkVals, 0, vals, 0, pkVals.length);
			vals[pkVals.length] = tcn;
			System.arraycopy(setVals, 0, vals, pkVals.length + 1, setVals.length);
			
			int cnt = setVals.length + pkVals.length + 2;
			String [] configFieldsToAppend = new String [] {"CTL_MOD_UID", "CTL_MOD_TS", "CTL_TCN"};
			String separator = ", ";
			for (int i = 0; i < configFieldsToAppend.length; i++) {
				set += separator;
				set += configFieldsToAppend[i];
				set += " = ?";
				set += cnt;
				cnt ++;
			}
			vals[vals.length-3] = user.getUserId();
			vals[vals.length-2] = new java.util.Date();
			tcnLong++;
			vals[vals.length-1] = Long.valueOf(tcnLong); 
			List res = dao.findNative("select * from " + Constant.SCHEMA_USER_PREPEND + tableName + " where " + where, pkValsAndTcn);
			
			if (res == null || res.size() != 1) {
				String errMsg = "Concurrent modification detected!";
				
				LOG.error(errMsg + " in " + tableName);
				throw new OptimisticLockException("Concurrent modification detected!");
			}
			
			dao.execNative("update " + Constant.SCHEMA_USER_PREPEND + tableName +  " set " + set + " where " + where, vals);
			audit(targetEntryId, tableName, user, Constant.AUDIT_ACTION_TABLEBROWSER_UPDATE);
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
	 * update existing data in a table
	 */
	public void updateGeneric(Object targetEntryId, String tableName, UserEntity user, String set, Object[] setVals, String where,
			Object[] pkVals) {
		// [[
		Object[][] params = {{"targetEntryId", targetEntryId},
							{"tableName", tableName},
							{"user", user},
							{"set", set},
							{"setVals", setVals},
							{"where", where},
							{"pkVals", pkVals}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateGeneric", params);
		}
		// ]]
		try {
			DirectJpaDao dao = getDaoProvider().getDirectJpaDao();
			
			Object[] vals = new Object[setVals.length + pkVals.length + 2]; 
			System.arraycopy(pkVals, 0, vals, 0, pkVals.length);
			System.arraycopy(setVals, 0, vals, pkVals.length, setVals.length);
			
			int cnt = setVals.length + pkVals.length + 1;
			String [] configFieldsToAppend = new String [] {"CTL_MOD_UID", "CTL_MOD_TS"};
			String separator = ", ";
			for (int i = 0; i < configFieldsToAppend.length; i++) {
				set += separator;
				set += configFieldsToAppend[i];
				set += " = ?";
				set += cnt;
				cnt ++;
			}
			vals[vals.length-2] = user.getUserId();
			vals[vals.length-1] = new java.util.Date();
			dao.execNative("update " + Constant.SCHEMA_USER_PREPEND + tableName +  " set " + set + " where " + where, vals);
			audit(targetEntryId, tableName, user, Constant.AUDIT_ACTION_TABLEBROWSER_UPDATE);
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
	 * insert data generically into given table
	 */
	public void insertGeneric(Object targetEntryId, String tableName, UserEntity user, String fields, Object[] values, boolean hasTcnField) {
		// [[
		Object[][] params = {{"targetEntryId", targetEntryId},
							{"tableName", tableName},
							{"user", user},
							{"fields", fields},
							{"values", values},
							{"hasTcnField", hasTcnField + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "insertGeneric", params);
		}
		// ]]
		try {
			DirectJpaDao dao = getDaoProvider().getDirectJpaDao();
			StringBuffer s = new StringBuffer("insert into " + Constant.SCHEMA_USER_PREPEND);
			s.append(tableName);
			s.append("(");
			s.append(fields);
			if (hasTcnField) {
				s.append(", CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN");
			}
			else {
				s.append(", CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS");
			}
			s.append(") values(?1");
			
			int endIndex = hasTcnField ? 5 : 4;
			
			for (int i = 2; i <= values.length + endIndex; i++){
				s.append(", ?");
				s.append(i);
			}
			s.append(")");
						
			List valuesList = new ArrayList();
			valuesList.addAll(Arrays.asList(values));
			Long uID = user.getUserId();
			valuesList.add(uID);
			java.util.Date ts = new java.util.Date();
			valuesList.add(ts);
			valuesList.add(uID);
			valuesList.add(ts);
			if (hasTcnField) {
				valuesList.add(Long.valueOf(0));
			}
			
			values = valuesList.toArray();
			for (int i=0;i<values.length;i++) {
				if ( (values[i] != null) && (values[i] instanceof TableBrowserSequenceWrapper) ) {
					TableBrowserSequenceWrapper tbsw = (TableBrowserSequenceWrapper)values[i];
					values[i] = fetchSequenceNextVal(tbsw.getSequenceName());
					if (tbsw.isTargetEntryId()) {
						targetEntryId = values[i]; 
					}
				}
			}
				
			dao.execNative(s.toString(), values);
			audit(targetEntryId, tableName, user, Constant.AUDIT_ACTION_TABLEBROWSER_INSERT);
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
	 * @param tableName
	 * @param user
	 */
	private void audit(Object targetEntryId, String tableName, UserEntity user, Long actionKey) {
		// [[
		Object[][] params = {{"targetEntryId", targetEntryId},
							{"tableName", tableName},
							{"user", user},
							{"actionKey", actionKey}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "audit", params);
		}
		// ]]
		try {
			String targetEntryIdStr = targetEntryId.toString();
		    if (targetEntryIdStr != null && targetEntryIdStr.length() > 30) {
		        LOG.error("targetEntryIdStr is too long for TLGNC_AUDIT.TARGET_ENTRY_ID :" + targetEntryIdStr + ". It is truncated to fit to the column.");
		        targetEntryIdStr = targetEntryIdStr.substring(0, 30);
		    }
			
			AuditEntity auditEntity = new AuditEntity();
			AuditActionBsEntity auditAction = referentialDataService.findBsEntity(AuditActionBsEntity.class, actionKey);
			auditEntity.setAuditAction(auditAction);
			auditEntity.setChangeTs(new Date(System.currentTimeMillis()));
			auditEntity.setTargetEntryId(targetEntryIdStr);
			auditEntity.setTargetTableName(tableName);
			UserEntity userEntity = getDaoProvider().getUserEntityDao().findById(user.getUserId());
			auditEntity.setUser(userEntity);
			getDaoProvider().getAuditEntityDao().persist(auditEntity);
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
	 * fetch TableMeta of given table
	 */
	public TableMeta fetchTable(String name) {
		// [[
		TableMeta result = null;
		Object[][] params = {{"name", name}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchTable", params);
		}
		// ]]
		try {
			TableMeta res = null;
			ReadWriteDao<TableMeta> tableDao = getDaoProvider().getTableMetaDao();
			Filter criterion = new FilterImpl();
			criterion.addExpression(Restrictions.eq("name", name));
			res = tableDao.findSingle(criterion);
			return result = res;
		}
		catch (RuntimeException ex) {
			return result = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public ColumnInfoEntity loadColumnAdditionalInfo(String tableName, String columnName){
		// [[
		ColumnInfoEntity result = null;
		Object[][] params = {{"tableName", tableName},
							{"columnName", columnName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadColumnAdditionalInfo", params);
		}
		// ]]
		try {
			ReadOnlyDao<ColumnInfoEntity> columnInfoDao = getDaoProvider().getColumnInfoEntityDao();
			Filter filt = new FilterImpl();
			filt.addExpression(Restrictions.eq(ColumnInfoEntity.ATTR_ID, tableName + "_" + columnName));
			
			return result = columnInfoDao.findSingle(filt);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	
	public TableInfoEntity loadTableAdditionalInfo(String tableName) {
		// [[
		TableInfoEntity result = null;
		Object[][] params = {{"tableName", tableName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadTableAdditionalInfo", params);
		}
		// ]]
		try {
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(TableInfoEntity.ATTR_TABLE_NAME, tableName));
			
			return result = getDaoProvider().getTableInfoEntityDao().findSingle(filter);
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
