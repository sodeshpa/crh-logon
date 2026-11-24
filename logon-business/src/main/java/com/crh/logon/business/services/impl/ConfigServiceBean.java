/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.DirectJpaDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.common.base.ConfigReloadListener;
import com.crh.logon.business.services.dispatchers.ConfigServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.utils.ConfigInitializer;
import com.crh.logon.business.services.utils.ConfigReloadResult;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.persistence.entities.common.ConfigLoginModuleEntity;
import com.crh.logon.persistence.entities.refdata.ConfigEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.persistence.utils.RefDataUtil;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.LogonProperties;
import com.crh.logon.utils.Reason;

/**
 * @author AdNovum Informatik AG
 */
@Lock(LockType.READ)
@Singleton
@Interceptors(UserInfoInterceptor.class)
@Named
public class ConfigServiceBean extends ConfigServiceDispatcher implements ConfigServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ConfigServiceBean.class);

	private final static String CFG_OK = "ok";

	private final static String CFG_ERR_NULL = "err.number.null";

	private final static String CFG_ERR_NUMBERFORMAT = "err.number.format";

	private final static String CFG_ERR_MIN = "err.min.underflow";

	private final static String CFG_ERR_MAX = "err.max.overflow";

	@Inject
	@Any
	private Instance<ConfigReloadListener> configReloadListeners;

	@Override
	public String getProperty(String key) {
		return getProperty(key, null);
	}

	@Override
	public boolean isInitialized() {
		return ConfigInitializer.inst.isInitialized();
	}

	@Override
	public ConfigBean getConfigBean() {
		return ConfigInitializer.inst.getBean();
	}

	/**
	 * NOTE: logonProperties contains a instance field. The instance parameter is
	 * used to override whatever is in logonProperties. Note that logonProperties
	 * will NOT be changed. the configbean will have a getInstance() method that
	 * returns the instance to which the application was set (either from
	 * logonProperties or the overrided value).
	 *
	 * @param instance
	 * @param logonProperties
	 * @return
	 */
	@Override
	public ConfigBean initializeConfigBean(String instance, LogonProperties logonProperties) {

		if (!isExistingInstance(instance)) {
			String errorMsg = "Config requested for invalid instance: " + instance;
			LOG.error(errorMsg);
			throw new LogonException(errorMsg, Reason.RES_ERROR_CONFIG_NOT_EXISTENT);
		}

		ConfigBean configBean = ConfigInitializer.inst.initialize(this, instance, logonProperties);
		initializeLoginModuleConfiguration(configBean);

		return configBean;
	}

	@Override
	public ConfigBean initializeConfigBean(LogonProperties logonProperties) {

		ConfigBean configBean = ConfigInitializer.inst.initialize(this, null, logonProperties);
		initializeLoginModuleConfiguration(configBean);

		return configBean;
	}

	@Override
	public ConfigBean reloadConfigBean() {

		ConfigBean configBean = ConfigInitializer.inst.reload(this);
		initializeLoginModuleConfiguration(configBean);

		// notify the listeners
		configReloadListeners.forEach(ConfigReloadListener::configReloaded);

		return configBean;
	}

	private void initializeLoginModuleConfiguration(ConfigBean configBean) {

		/**
		 * The list of entries that will be copied are so far hard coded.
		 * That is because i had to call specific functions on the ConfigBean.
		 * (balakirev)
		 */

		String instance = configBean.getInstance();

		DaoProvider daoProvider = getDaoProvider();
		ReadWriteDao<ConfigLoginModuleEntity> configLoginModuleEntityDao = daoProvider.getConfigLoginModuleEntityDao();

		/**
		 * 1.) Finds the regarding entry.
		 * 2.) Updates it's value (or sets if the entry was not in the DB before)
		 * 3.) Merge (persist) to config login module table
		*/

		ConfigLoginModuleEntity mailBcc = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_MAILBCC);
		mailBcc.setValue(configBean.getMailBcc());
		configLoginModuleEntityDao.merge(mailBcc);

		ConfigLoginModuleEntity mailEncoding = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_MAILENCODING);
		mailEncoding.setValue(configBean.getMailEncoding());
		configLoginModuleEntityDao.merge(mailEncoding);

		ConfigLoginModuleEntity mailTestEnabled = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_MAILTESTENABLED);
		mailTestEnabled.setValue(Boolean.toString(configBean.getMailTestEnabled()));
		configLoginModuleEntityDao.merge(mailTestEnabled);

		ConfigLoginModuleEntity mailTestTo = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_MAILTESTTO);
		mailTestTo.setValue(configBean.getMailTestTo());
		configLoginModuleEntityDao.merge(mailTestTo);

		ConfigLoginModuleEntity mailSmptHost = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_MAILSMTPHOST);
		mailSmptHost.setValue(configBean.getMailSmtpHost());
		configLoginModuleEntityDao.merge(mailSmptHost);

		ConfigLoginModuleEntity mailSmptPort = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_MAILSMTPPORT);
		mailSmptPort.setValue(Integer.toString(configBean.getMailSmtpPort()));
		configLoginModuleEntityDao.merge(mailSmptPort);

		ConfigLoginModuleEntity mailFrom = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_MAILFROM);
		mailFrom.setValue(configBean.getMailFrom());
		configLoginModuleEntityDao.merge(mailFrom);

		ConfigLoginModuleEntity appPolicyPwdLengthMin = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_APPPOLICYPASSWORDLENGTHMINIMAL);
		appPolicyPwdLengthMin.setValue(Integer.toString(configBean.getAppPolicyPasswordLengthMinimal()));
		configLoginModuleEntityDao.merge(appPolicyPwdLengthMin);

		ConfigLoginModuleEntity appPolicyPwdLowCaseMin = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_APPPOLICYPASSWORDLOWERCASEMINIMAL);
		appPolicyPwdLowCaseMin.setValue(Integer.toString(configBean.getAppPolicyPasswordLowercaseMinimal()));
		configLoginModuleEntityDao.merge(appPolicyPwdLowCaseMin);

		ConfigLoginModuleEntity appPolicyPwdUpCaseMin = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_APPPOLICYPASSWORDUPPERCASEMINIMAL);
		appPolicyPwdUpCaseMin.setValue(Integer.toString(configBean.getAppPolicyPasswordUppercaseMinimal()));
		configLoginModuleEntityDao.merge(appPolicyPwdUpCaseMin);

		ConfigLoginModuleEntity appPolicyPwdNumMin = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_APPPOLICYPASSWORDNUMERICMINIMAL);
		appPolicyPwdNumMin.setValue(Integer.toString(configBean.getAppPolicyPasswordNumericMinimal()));
		configLoginModuleEntityDao.merge(appPolicyPwdNumMin);

		ConfigLoginModuleEntity appPolicyPwdSpecMin = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_APPPOLICYPASSWORDSPECIALMINIMAL);
		appPolicyPwdSpecMin.setValue(Integer.toString(configBean.getAppPolicyPasswordSpecialMinimal()));
		configLoginModuleEntityDao.merge(appPolicyPwdSpecMin);

		ConfigLoginModuleEntity appPolicyPwdRecovUrl = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_APPPOLICYPASSWORDRECOVERYURL);
		appPolicyPwdRecovUrl.setValue(configBean.getAppPolicyPasswordRecoveryUrl());
		configLoginModuleEntityDao.merge(appPolicyPwdRecovUrl);

		ConfigLoginModuleEntity appPolicyPwdGenLowCharset = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_APPPOLICYPASSWORDGENERATEDLOWERCASECHARSET);
		appPolicyPwdGenLowCharset.setValue(configBean.getAppPolicyPasswordGeneratedLowercaseCharset());
		configLoginModuleEntityDao.merge(appPolicyPwdGenLowCharset);

		ConfigLoginModuleEntity appPolicyPwdGenUpCharset = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_APPPOLICYPASSWORDGENERATEDUPPERCASECHARSET);
		appPolicyPwdGenUpCharset.setValue(configBean.getAppPolicyPasswordGeneratedUppercaseCharset());
		configLoginModuleEntityDao.merge(appPolicyPwdGenUpCharset);

		ConfigLoginModuleEntity appPolicyPwdGenNumCharset = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_APPPOLICYPASSWORDGENERATEDNUMERICCHARSET);
		appPolicyPwdGenNumCharset.setValue(configBean.getAppPolicyPasswordGeneratedNumericCharset());
		configLoginModuleEntityDao.merge(appPolicyPwdGenNumCharset);

		ConfigLoginModuleEntity appPolicyPwdSpecCharset = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_APPPOLICYPASSWORDSPECIALSETOFCHARS);
		appPolicyPwdSpecCharset.setValue(configBean.getAppPolicyPasswordSpecialSetOfChars());
		configLoginModuleEntityDao.merge(appPolicyPwdSpecCharset);

		ConfigLoginModuleEntity appPolicyPwdMaxLoginAttempt = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_APPPOLICYPASSWORDMAXLOGINATTEMPT);
		appPolicyPwdMaxLoginAttempt.setValue(Integer.toString(configBean.getAppPolicyPasswordMaxLoginAttempt()));
		configLoginModuleEntityDao.merge(appPolicyPwdMaxLoginAttempt);

		ConfigLoginModuleEntity appSystemName = findOrCreateNew(configLoginModuleEntityDao, instance, ConfigBean.CFG_APPSYSTEMNAME);
		appSystemName.setValue(Integer.toString(configBean.getAppPolicyPasswordMaxLoginAttempt()));
		configLoginModuleEntityDao.merge(appSystemName);

	}

	/**
	 * Checks if there is a {@link ConfigLoginModuleEntity} entry already in the database
	 * for the provided key. If there is none it returns a non managed POJO (new entry) with
	 * <b>key</b> and <b>instance</b> already filled.
	 * @param configLoginModuleEntityDao
	 * @param instance
	 * @param key
	 * @param value
	 * @return Managed entry if can be found - Non managed new POJO if not
	 */
	private ConfigLoginModuleEntity findOrCreateNew(
			ReadWriteDao<ConfigLoginModuleEntity> configLoginModuleEntityDao, String instance,
			String key) {

		// Entity that we return
		ConfigLoginModuleEntity entity;

		Filter<ConfigLoginModuleEntity> configLoginModuleFilter = new FilterImpl<ConfigLoginModuleEntity>();
		configLoginModuleFilter.addExpression(Restrictions.eq(ConfigLoginModuleEntity.ATTR_KEY, key));
		configLoginModuleFilter.addExpression(Restrictions.eq(
				ConfigLoginModuleEntity.ATTR_INSTANCE, instance));

		List<ConfigLoginModuleEntity> resultList = configLoginModuleEntityDao.find(configLoginModuleFilter);

		if (resultList.isEmpty()) {
			entity = new ConfigLoginModuleEntity();
			entity.setKey(key);
			entity.setInstance(instance);
		}
		else {
			// Pop the only result (unique constraint guarantees)
			entity = resultList.get(0);
		}

		return entity;

	}

	@Override
	public Collection<ConfigReloadResult> reloadConfigBean(boolean notify) {

		Collection<ConfigReloadResult> crrSet = new HashSet<ConfigReloadResult>();
		ConfigBean localConfigBean = reloadConfigBean();
		ConfigReloadResult crr = new ConfigReloadResult(null);
		crr.setConfigBean(localConfigBean);
		crrSet.add(crr);
		if ((notify) && (localConfigBean.getConfigNotificationUrls() != null)) {
			String[] urls = localConfigBean.getConfigNotificationUrls().split(",");
			for (String url : urls) {
				url = url.trim();
				crr = new ConfigReloadResult(url);
				crrSet.add(crr);
				try {
					ServiceLocator serviceLocator = new ServiceLocator(url, false);
					ConfigBean remoteConfigBean = serviceLocator.getConfigService().reloadConfigBean();
					crr.setConfigBean(remoteConfigBean);
				}
				catch (Exception e) {
					LOG.error("Config failed to reload on " + url, e);
					crr.setThrowable(e);
				}
			}
		}

		return crrSet;
	}

	@Override
	public String getProperty(String key, String instance) {
		return getProperty(key, instance, true);
	}

	/**
	 * @param key
	 * @param instance
	 * @param fail when true, fail if config param is not in DB
	 * @return
	 */
	@Override
	public String getProperty(String key, String instance, boolean fail) {
		String result = null;

		final ConfigEntity configEntity = fetchConfigEntity(key, instance, fail);
		if (configEntity != null) {
			result = configEntity.getValue();
		}

		return result;
	}

	/**
	 * fetches the ConfigEntity with given key. uses default instance (==null)
	 */
	@Override
	public ConfigEntity fetchConfigEntity(String key) {
		return fetchConfigEntity(key, null);
	}

	@Override
	public ConfigEntity fetchConfigEntity(String key, String instance) {
		return fetchConfigEntity(key, instance, true);
	}

	/**
	 * fetches the ConfigEntity with given key and instance. instance may be 'null'
	 * in which case the default config emtity is retreived.
	 *
	 * @param key
	 * @param instance
	 * @param fail when true, fail if config param is not in DB
	 * @return
	 */
	@Override
	public ConfigEntity fetchConfigEntity(String key, String instance, boolean fail) {
		// [[
		ConfigEntity result = null;
		final Object[][] params = { { "key", key }, { "instance", instance }, { "fail", fail + "" } };
		final boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchConfigEntity", params);
		}
		// ]]
		try {
			final Filter<ConfigEntity> filter = new FilterImpl<ConfigEntity>();
			filter.addExpression(Restrictions.eq(ConfigEntity.ATTR_KEY, key));

			final List<ConfigEntity> resultList;
			if (instance != null) {
				final List<String> instanceList = new ArrayList<String>();

				int position = instance.length();
				do {
					instanceList.add(instance.substring(0, position));
					position = instance.lastIndexOf('.', position - 1);
				} while (position > -1);

				filter.addExpression(Restrictions.or(
						Restrictions.isNull(ConfigEntity.ATTR_INSTANCE),
						Restrictions.in(ConfigEntity.ATTR_INSTANCE, instanceList),
						(Expression[]) null));
			}
			else {
				filter.addExpression(Restrictions.isNull(ConfigEntity.ATTR_INSTANCE));
			}

			resultList = RefDataUtil.findConfigEntityList(em, filter);

			ConfigEntity relevantEntity = null;
			for (ConfigEntity entity : resultList) {

				if ((relevantEntity == null)
						|| (relevantEntity.getInstance() == null)
						|| ((entity.getInstance() != null) && (entity.getInstance().length() > relevantEntity.getInstance().length()))) {

					relevantEntity = entity;
				}
			}

			if (relevantEntity == null) {
				if (fail) {
					throw new LogonException(Reason.RES_ERROR_CONFIG_NOT_EXISTENT, params);
				}
				else {
					LOG.error("non existent config '" + key + "' requested! " + "(check DB-table '"
							+ ConfigEntity.TABLE_NAME + "')");
				}
			}

			return result = relevantEntity;
		}
		finally {
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		}
	}

	@Override
	public void writeConfig(String key, String instance, String value) {
		// [[
		final Object[][] params = { { "key", key }, { "instance", instance }, { "value", value } };
		final boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "writeConfig", params);
		}
		// ]]
		try {
			throw new LogonException(Reason.RES_ERROR_NOT_IMPL);
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
	 * returns a hashmap with all available keys for the given instance and the
	 * values as strings.
	 */
	@Override
	public Map<String, String> fetchAllConfigs(String instance) {
		// [[
		Map<String, String> result = null;
		final Object[][] params = { { "instance", instance } };
		final boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAllConfigs", params);
		}
		// ]]
		try {
			final Map<String, String> resMap = new HashMap<String, String>();
			final Filter<ConfigEntity> filter = new FilterImpl<ConfigEntity>();

			final List<ConfigEntity> resultList;
			if (instance != null) {
				final List<String> instanceList = new ArrayList<String>();

				int position = instance.length();
				do {
					instanceList.add(instance.substring(0, position));
					position = instance.lastIndexOf('.', position - 1);
				} while (position > -1);

				filter.addExpression(Restrictions.or(
						Restrictions.isNull(ConfigEntity.ATTR_INSTANCE),
						Restrictions.in(ConfigEntity.ATTR_INSTANCE, instanceList),
						(Expression[]) null));
			}
			else {
				filter.addExpression(Restrictions.isNull(ConfigEntity.ATTR_INSTANCE));
			}

			resultList = RefDataUtil.findConfigEntityList(em, filter);

			ConfigEntity relevantEntity = null;
			final Map<String, ConfigEntity> relevantEntities = new HashMap<String, ConfigEntity>();
			for (ConfigEntity entity : resultList) {
				relevantEntity = relevantEntities.get(entity.getKey());

				if ((relevantEntity == null)
						|| (relevantEntity.getInstance() == null)
						|| ((entity.getInstance() != null) && (entity.getInstance().length() > relevantEntity.getInstance().length()))) {

					relevantEntities.put(entity.getKey(), entity);
				}
			}

			for (Map.Entry<String, ConfigEntity> entry : relevantEntities.entrySet()) {
				relevantEntity = entry.getValue();
				resMap.put(relevantEntity.getKey(), relevantEntity.getValue());
			}

			if (logDebugEnabled) {
				LOG.debug("size: " + resMap.size());
			}

			return result = resMap;
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
	 * validates all properties. returns a list of properties which violate rules.
	 * validation can only be done for properties with a min/max value.
	 */
	@Override
	public List<String> validateAll() {
		// [[
		List<String> result = null;
		final boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validateAll");
		}
		// ]]
		try {
			final DirectJpaDao dao = getDaoProvider().getDirectJpaDao();
			final List<?> all = dao.find("select x from " + ConfigEntity.class.getName() + " x");
			final List<String> res = new ArrayList<String>();

			String validationResult;
			String errorMessage;
			ConfigEntity entity;
			for (Iterator<?> iterator = all.iterator(); iterator.hasNext();) {
				entity = (ConfigEntity) iterator.next();

				validationResult = validate(entity);
				if (!validationResult.equals(CFG_OK)) {

					errorMessage = "NOT OK! " + entity.getKey() + " = " + entity.getValue() + " ("
							+ validationResult + ")";

					res.add(errorMessage);
					LOG.error(errorMessage);
				}
			}

			return result = res;
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
	 * validates one property. return 0 if the property is valid, otherwise
	 * a number representing the reason. (CFG_ERR_NUMBERFORMAT, CFG_ERR_MIN,
	 * CFG_ERR_MAX)
	 * validation can only be done for properties with either a min
	 * or max value.
	 * @return CFG_ERR_NUMBERFORMAT, CFG_ERR_MIN, CFG_ERR_MAX
	 */
	@Override
	public String validate(ConfigEntity configEntity) {
		final Long minValue = configEntity.getMinValue();
		final Long maxValue = configEntity.getMaxValue();

		if (minValue == null && maxValue == null) {
			return CFG_OK;
		}
		// either min or max is set... we have a config value containing a number...

		final String valueStr = configEntity.getValue();
		final double value;
		try {
			value = Double.parseDouble(valueStr);
		}
		catch (NumberFormatException e) {
			LOG.error("invalid config entry: '" + configEntity + "'");
			LOG.error("reason: " + e.getMessage());
			return CFG_ERR_NUMBERFORMAT;
		}
		catch (NullPointerException e) {
			LOG.error("invalid config entry: '" + configEntity + "'");
			LOG.error("reason: " + e.getMessage());
			return CFG_ERR_NULL;
		}

		if (minValue != null) {
			final long min = minValue.longValue();

			if (min > value) {
				LOG.error("invalid config entry: '" + configEntity + "'");
				LOG.error("reason: " + min + " > " + value + " (min underflow)");
				return CFG_ERR_MIN;
			}
		}
		if (maxValue != null) {
			final long max = maxValue.longValue();

			if (max < value) {
				LOG.error("invalid config entry: '" + configEntity + "'");
				LOG.error("reason: " + max + " < " + value + " (max overflow)");
				return CFG_ERR_MAX;
			}
		}
		return CFG_OK;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> listInstances() {
		// [[
		List<String> result = null;
		final boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "listInstances");
		}
		// ]]
		try {
			final DirectJpaDao dao = getDaoProvider().getDirectJpaDao();
			return result = dao.find("select distinct " + ConfigEntity.ATTR_INSTANCE + " from "
					+ ConfigEntity.class.getName() + " x");
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private boolean isExistingInstance(String instanceName) {
		String query = "select is_existing_instance(?1) from dual";
		BigDecimal isExisting = (BigDecimal) em.createNativeQuery(query)
												.setParameter(1, instanceName)
												.getSingleResult();
		return isExisting != BigDecimal.ZERO;
	}
}
