/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

final class CDIHelper {

	private final static String BEAN_MANAGER_JNDI_NAME = "java:comp/BeanManager";
	
	private Logger logger = Logger.getLogger(this.getClass());		
	private Context context;
	private BeanManager beanManager;
	
	/**
	 * 
	 */
	private CDIHelper() {
		try {
			context = new InitialContext();	
			if(context != null) {
				beanManager = getBeanManager();
			}
		}
		catch (Exception e) {
			logger.error("Error in beanManager init", e);
		}
	}
	
	public static CDIHelper getInstance() {
		return CDIHelperHolder.INSTANCE;
	}
	
	private static class CDIHelperHolder {
		private static volatile CDIHelper INSTANCE = new CDIHelper();
	}
	
	private BeanManager getBeanManager() {
		return getJNDIObject(BEAN_MANAGER_JNDI_NAME, BeanManager.class);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getJNDIObject(String jndiName, Class<T> clazz) {
		try {
			return (T) context.lookup(jndiName);
		}
		catch (NamingException e) {
			logger.error("Error in " + jndiName + " lookup", e);
			return null;
		}
	}
	
	public <T> T getBean(Class<T> type) {
		return resolveBean(beanManager.getBeans(type), type);
	}

	public <T> T getBean(String name, Class<T> type) {
		return resolveBean(beanManager.getBeans(name), type);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T resolveBean(Set<Bean<?>> beans, Class<T> type) {
		if (beans.isEmpty()) {
			return null;
		}
		Bean<?> bean = beanManager.resolve(beans);
		CreationalContext<?> context = beanManager.createCreationalContext(bean);
		return (T) beanManager.getReference(bean, bean.getBeanClass(), context);		
	}
	
}
