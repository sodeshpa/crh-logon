/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;

import org.jboss.security.SecurityContextAssociation;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class AuthUtils {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AuthUtils.class);
	private static ThreadLocal authorization = new ThreadLocal();
	public static final Long technical = 1L;
	
	public static Long getUserId() {
		String name = null;
		try {
			 Subject subject = SecurityContextAssociation.getSubject();
			if (subject == null) { // technical user
				return technical;
			}
			Set subjSet = subject.getPrincipals();
			for (Iterator iter = subjSet.iterator(); iter.hasNext();) {
				Principal principal = (Principal) iter.next();
				
				// Filter CallerPrincipal and Roles
				if (principal instanceof SimpleGroup) {
				    continue;
				}
				if (principal instanceof SimplePrincipal) {
					name = principal.getName();
					return Long.parseLong(name);
				}
			}
			return null;
		}
		catch (NumberFormatException e) {
        	String msg = "couldn't parse '" + name + "' as Long";
			LOG.error(msg);
        	throw new LogonException(msg, Reason.RES_ERROR_SECURITY_TAMPERING);
		}
	}
	
	public static boolean isTechnical() {
		LOG.debug("isTechnical(): " + (getUserId() == technical));
		return getUserId() == technical;
	}
	
	/**
	 * @return a hashmap with all function ids as keys (type Long)
	 */
	public static HashMap groups() {
		HashMap map = new HashMap();
		Subject subject = SecurityContextAssociation.getSubject();
		if (subject == null) { // technical user
        	throw new LogonException(Reason.RES_ERROR_SECURITY_TAMPERING);
		}
		Set subjSet = subject.getPrincipals();
		for (Iterator iter = subjSet.iterator(); iter.hasNext();) {
			Principal principal = (Principal) iter.next();
			if (principal instanceof SimpleGroup) {
				Group group = (Group)principal;
				for (Enumeration e = group.members() ; e.hasMoreElements() ;) {
					Principal p = (Principal)e.nextElement();
					try {
						Long funcId = Long.parseLong(p.getName());
						map.put(funcId, p);
					}
					catch (NumberFormatException nfe) {
						// this is ok, ignore
						LOG.debug(p.getName() + " skipped!");
					}
				}
			}
		}
		return map;
	}

	public static void setAuthorized(Boolean authorized) {
		LOG.debug("authorizing user for this request: " + authorized);
		authorization.set(authorized);
	}
	
	public static Boolean isAuthorized() {
		Boolean auth = (Boolean)authorization.get();
		if (auth != null && auth.booleanValue()) {
			LOG.debug("isAuthorized(): true");
			return true;
		}
		LOG.debug("isAuthorized(): false");
		return false;
	}
	
	public static void deauthorize() {
		authorization.set(Boolean.FALSE);
	}

}
