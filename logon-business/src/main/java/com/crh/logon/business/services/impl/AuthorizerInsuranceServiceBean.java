/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.gdi.jpa.BaseEntity;
import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ThreadLocalUserInfo;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.AuthorizerInsuranceServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.AuthorizerInsuranceServiceLocalInterface;
import com.crh.logon.business.services.utils.AuthorizerInsuranceUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@AdnTrace
public class AuthorizerInsuranceServiceBean extends AuthorizerInsuranceServiceDispatcher implements AuthorizerInsuranceServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AuthorizerInsuranceServiceBean.class);

	@EJB private AuthorizerInsuranceServiceLocalInterface authorizerInsuranceService;
	
	/**
	 * This test-method is called in production mode when the application is initialized.
	 * The application will NOT deploy if this test fails.
	 */
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List test() {
		LOG.info("Checking database integrity...");
		List<String> reportMessages = new ArrayList<String>();
		List<Exception> exceptions = new ArrayList<Exception>();
		
		for (Class entityClass: AuthorizerInsuranceUtil.ENTITIES) {
			String entityName = entityClass.getSimpleName();
			Long userId = ThreadLocalUserInfo.getUserId();
			try {
				test(entityClass);
				String reportMesssage = String.format(
					"%-35s OK!     (user=" + userId + ")",
					entityName
				);
				LOG.info(reportMesssage);
			}
			catch (Exception exception) {
				exceptions.add(exception);
				String reportMesssage = String.format(
					"%-35s FAILED! (user=" + userId + ")",
					entityName
				);
				reportMessages.add(reportMesssage);
			}
		}
		if (!exceptions.isEmpty()) {
			throw new LogonException(
				"One or more tables are not properly set up." + Constant.NEW_LINE
				+ " Report messages: " + reportMessages + Constant.NEW_LINE 
				+ " Exceptions: " + exceptions + Constant.NEW_LINE
				, Reason.RES_ERROR_INIT
			);
		}
		
		return reportMessages;
	}
	
	private void test(Class entityClass) {
    	
        Filter f = new FilterImpl();
        f.addExpression(Restrictions.falseExpression());
        Class daoProviderClass = getDaoProvider().getClass();
		ReadOnlyDao<BaseEntity> dao;
		try {
			Method method = daoProviderClass.getMethod("get" + entityClass.getSimpleName() + "Dao", (Class[])null);
			dao = (ReadOnlyDao<BaseEntity>)method.invoke(getDaoProvider(), (Object[])null);
		}
		catch (SecurityException e) {
			
			throw new LogonException("Error retrieving DAO", e);
		}
		catch (NoSuchMethodException e) {
			
			throw new LogonException("Error retrieving DAO", e);
		}
		catch (IllegalArgumentException e) {
			
			throw new LogonException("Error retrieving DAO", e);
		}
		catch (IllegalAccessException e) {
			
			throw new LogonException("Error retrieving DAO", e);
		}
		catch (InvocationTargetException e) {
			
			throw new LogonException("Error retrieving DAO", e);
		}
		if (entityClass.equals(SapBpEntity.class)) {
			// Calling the SapBpEntity DAO with a filter that doesn't contain 
			// BP_IS_* should throw an exception.
			boolean exceptionThrown = false;
			try {
				dao.find(f);
			}
			catch (LogonException e) {
				exceptionThrown = true;
				LOG.error("The EXCEPTION thrown above is EXPECTED (intentionaly called " +
						"SapBpEntityDao with a wrong filter) to make sure that is will block " +
						"acccess, and throw an exception.");
			}
			if (!exceptionThrown) {
				
				throw new LogonException("Filter check for SapBpEntity failed", Reason.RES_ERROR_IMPL);
			}
			f.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SOLDTO, Boolean.TRUE));
		}
		dao.find(f);
    }
}
