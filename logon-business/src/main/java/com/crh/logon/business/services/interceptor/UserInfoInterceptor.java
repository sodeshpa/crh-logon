/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.interceptor;

import javax.annotation.Resource;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import javax.transaction.Status;
import javax.transaction.TransactionSynchronizationRegistry;

import ch.nevis.gdi.jpa.ThreadLocalUserInfo;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.Audited;
import com.crh.logon.utils.AuthUtils;

/**
 * Responsible to inject user information to @ThreadLocalUserInfo.
 * Initializes ThreadLocal authorization defaults if they are not set beforehand.
 * If initialized by this @UserInfoInterceptor, these values are reset after to their default value
 * after the invocation to avoid leaking information to different threads through ThreadLocal variables.
 * disableNextAccessRestrictions is reset after the invocation regardless of where it was set,
 * as this is intended to be in effect for only one call.
 * 
 * @author AdNovum Informatik AG
 */
@Audited
@Interceptor
public class UserInfoInterceptor {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserInfoInterceptor.class);
	
	@PersistenceContext(unitName = "logon")
	private EntityManager em;
	
	@Resource(mappedName="java:comp/TransactionSynchronizationRegistry")
	private TransactionSynchronizationRegistry transactionRegistry;
	
	@AroundInvoke
	public Object injectUserInfo(InvocationContext invocationContext) throws Exception {
		boolean userInfoSetByThis = false;
		boolean accessRestrictionsSetByThis = false;
		
		try {
			userInfoSetByThis = initUserInfo();
			accessRestrictionsSetByThis = initAccessRestrictions();
			
			return invocationContext.proceed();
		}
		finally {
			clearUserInfo(userInfoSetByThis);
			clearAccessRestrictions(accessRestrictionsSetByThis);
		}
	}
	
	private boolean initAccessRestrictions() {
		boolean accessRestrictionsSetByThis = false;
		
		if (ThreadLocalUserInfo.getAccessRestrictions() == null) {
			ThreadLocalUserInfo.setAccessRestrictions(true);
			accessRestrictionsSetByThis = true;
		}
		if (ThreadLocalUserInfo.getDisableNextAccessRestrictions() == null) {
			ThreadLocalUserInfo.setDisableNextAccessRestrictions(false);
		}
		return accessRestrictionsSetByThis;
	}
	
	private boolean initUserInfo() {
		if (ThreadLocalUserInfo.getUserId() == null) {
			Long userId = AuthUtils.getUserId();
			ThreadLocalUserInfo.setUserId(userId);
			return true;
		}
		
		return false;
	}
	
	private void clearAccessRestrictions(boolean accessRestrictionsSetByThis) {
		if (accessRestrictionsSetByThis) {
			ThreadLocalUserInfo.setAccessRestrictions(null);
			accessRestrictionsSetByThis = false;
		}
		ThreadLocalUserInfo.setDisableNextAccessRestrictions(false);
	}
	
	private void clearUserInfo(boolean userInfoSetByThis) {
		if (userInfoSetByThis) {
			flushUserInfo();
			ThreadLocalUserInfo.setUserId(null);
		}
	}
	
	private void flushUserInfo() {
		int transactionStatus = -1;
		
		try {
			if (transactionRegistry != null) {
				transactionStatus = transactionRegistry.getTransactionStatus();
				if (transactionStatus == Status.STATUS_ACTIVE ||
					transactionStatus == Status.STATUS_PREPARED ||
					transactionStatus == Status.STATUS_PREPARING ||
					transactionStatus == Status.STATUS_UNKNOWN) {
					em.flush();
				}
			}
		}
		catch(TransactionRequiredException e) {
			LOG.error("Failed to flush user info, transaction status:" + transactionStatus, e);
		}
		catch(PersistenceException e) {
			LOG.error("Failed to flush user info, transaction status:" + transactionStatus);
			throw e;
		}
	}

}