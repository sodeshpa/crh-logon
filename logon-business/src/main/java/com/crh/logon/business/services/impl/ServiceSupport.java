/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ch.nevis.gdi.jpa.DirectJpaDao;
import ch.nevis.gdi.jpa.ThreadLocalUserInfo;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.ConfigInitializer;
import com.crh.logon.business.services.utils.Services;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.persistence.utils.DaoProviderNew;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;

/**
 * @author AdNovum Informatik AG
 */
public class ServiceSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ServiceSupport.class);

	@PersistenceContext(unitName = "logon")
	protected EntityManager em;
	private DaoProvider daoProvider;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	protected Services getServices() {
		return new Services(em);
	}

	/*
	 * implementation for getting rid of Services and (after that) the manual creation of EJBs. -
	 * nemes 05.07.12 / stich 12.03.13
	 */

	private static final String DEFAULT_JNDI_PREFIX = "java:global/logon/logon-business//";
	private static final String DEFAULT_JNDI_SUFFIX = "Bean";

	private InitialContext ctx = null;
	private Map<Class<Object>, Object> EJBs = null;

	private DaoProviderNew daoProviderNew;

	protected final <T> T getServiceFor(Class<T> implClass) {
		return getServiceFor(implClass, null);
	}

	@SuppressWarnings("unchecked")
	protected final <T> T getServiceFor(Class<? extends T> implClass, T inst) {
		if (EJBs == null) {
			EJBs = new HashMap<Class<Object>, Object>();
		}
		if (inst == null) {
			inst = (T) EJBs.get(implClass);
			if (inst == null) {
				try {
					if (ctx == null) {
						ctx = new InitialContext();
					}
					inst = (T) ctx.lookup(DEFAULT_JNDI_PREFIX + implClass.getSimpleName() + DEFAULT_JNDI_SUFFIX + "!" + implClass.getName());
					EJBs.put((Class<Object>) implClass, inst);
				}
				catch (Exception e) {
					throw new LogonException(String.format("Lookup failed for EJB %s!",
							implClass.getName()), e);
				}
			}
		}
		else {
			EJBs.put((Class<Object>) implClass, inst);
		}
		return inst;
	}



	public Long getUserId() {
		Long userId = ThreadLocalUserInfo.getUserId();
		LOG.debug("getUserId(): '" + userId + "'");
		return userId;
	}

	/**
	 * @adnlog.notrace
	 */
	public DaoProvider getDaoProvider() {
		if (daoProvider == null) {
			daoProvider = new DaoProvider(em, new Authorizer());
		}
		else {
			daoProvider.refresh(em);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("getDaoProvider(): '" + daoProvider + "'");
		}
		return daoProvider;
	}

	public DaoProviderNew getDaoProviderNew() {
		if (daoProviderNew == null) {
			daoProviderNew = new DaoProviderNew(em, new Authorizer());
		}
		else {
			daoProviderNew.refresh(em);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("getDaoProvider(): '" + daoProvider + "'");
		}
		return daoProviderNew;
	}
	
	protected ConfigBean getConfigBean() {
		return ConfigInitializer.inst.getBean();
	}

	/**
	 * Utility method to return the next DB sequence value
	 * 
	 * @param seqName
	 * @return
	 */
	protected Long fetchSequenceNextVal(String seqName) {
		// [[
		Long result = null;
		Object[][] params = { { "seqName", seqName } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSequenceNextVal", params);
		}
		// ]]
		try {
			final DirectJpaDao jpaDao = getDaoProvider().getDirectJpaDao();
			@SuppressWarnings("unchecked")
			final List<BigDecimal> results = jpaDao.findNative("select " + Constant.SCHEMA_USER_PREPEND + seqName + ".nextval from dual", null);
			return result = results.iterator().next().longValue();
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
