/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;


import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.DummyServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.DummyServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.persistence.entities.refdata.SignatureTypeTxEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * This is a sample service which shows the patterns to be used in this project.
 * 
 * Important to know:
 * 
 *   - DummyServiceDispatcher is generated
 *   - Write javadoc comments for public methods! They will end up in
 *     the generated service interface.
 *   - you can restrict deployment possibilities by either only implementing
 *     DummyServiceLocal or DummyServiceRemote. Note that they don't need
 *     any implemented methods,
 *   - trx hangling: this is all container provided, all trx are currently
 *     'readwrite'
 * 
 * @author AdNovum Informatik AG
 */
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class DummyServiceBean extends DummyServiceDispatcher implements DummyServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DummyServiceBean.class);

	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;

	
	/**
	 * This method takes a string as an argument and returns a new String
	 * which looks like this: 'received: string'
	 * 
	 * @param in some random String
	 * @throws LogonBusinessException if in was null
	 */
	public String hello(String in) {
		// [[
		String result = null;
		Object[][] params = {{"in", in}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "hello", params);
		}
		// ]]
		try {
			// this is how we tell our client that an error happened
			//
			if (in == null) {
				throw new LogonException(Reason.RES_ERROR_UNSPECIFIED);
			}
			return result = "received: " + in;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public String localizedHello(Locale locale, String key) {
		// [[
		String result = null;
		Object[][] params = {{"locale", locale},
							{"key", key}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "localizedHello", params);
		}
		// ]]
		try {
			referentialDataService.findTxEntityList(SignatureTypeTxEntity.class, locale);
			referentialDataService.findTxEntity(SignatureTypeTxEntity.class, locale, 0L);
			return result = Message.lookup(locale, key);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	// You can also use generics!
	public List<String> getSomeStrings(Map<String, List<String>> weirdMap)
	{
		return new LinkedList<String>();
	}
}
