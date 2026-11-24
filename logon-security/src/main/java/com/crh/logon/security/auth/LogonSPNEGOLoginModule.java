/*
 * JBoss, Home of Professional Open Source.
 * 
 * Copyright 2007, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.crh.logon.security.auth;

import static org.jboss.security.negotiation.Constants.KERBEROS_V5;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;
import org.jboss.security.negotiation.Constants;
import org.jboss.security.negotiation.NegotiationMessage;
import org.jboss.security.negotiation.common.NegotiationContext;
import org.jboss.security.negotiation.spnego.KerberosMessage;
import org.jboss.security.negotiation.spnego.SPNEGOLoginModule;
import org.jboss.security.negotiation.spnego.encoding.NegTokenInit;
import org.jboss.security.negotiation.spnego.encoding.NegTokenTarg;
import org.jboss.security.negotiation.spnego.encoding.SPNEGOMessage;

import com.crh.logon.utils.StringUtils;

public class LogonSPNEGOLoginModule extends SPNEGOLoginModule {

	private static final String SERVER_SECURITY_DOMAIN = "serverSecurityDomain";

	// Actual used Kerberos based security domain
	private String securityDomain;

	// Cached loginContext to actual used KerberosLoginModule
	private LoginContext loginContext = null;

	// Security domain list for failover handling
	private List<String> serverSecurityDomains = new ArrayList<String>();
	
	private static final Oid kerberos = KERBEROS_V5;

	private static final String SPNEGO = "SPNEGO";

	/* (non-Javadoc)
	 * @see org.jboss.security.negotiation.spnego.SPNEGOLoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
	 */
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState,
			Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);

		// Add new list
		String serverSecurityDomain = (String) options.get(SERVER_SECURITY_DOMAIN);

		if (!StringUtils.empty(serverSecurityDomain)) {
			serverSecurityDomain = serverSecurityDomain.trim();
			
			String[] domainArray = serverSecurityDomain.split(",");
			
			for (String domain : domainArray) {
				serverSecurityDomains.add(domain.trim());
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.jboss.security.negotiation.spnego.SPNEGOLoginModule#innerLogin()
	 */
	@Override
	protected Object innerLogin() throws LoginException {
		NegotiationContext negotiationContext = NegotiationContext.getCurrentNegotiationContext();
		if (negotiationContext == null) {
			return super.innerLogin();
		}
		else {
			// New failover method
			return spnegoLoginWithFailover(negotiationContext);
		}
	}

	private Object spnegoLoginWithFailover(NegotiationContext negotiationContext) throws LoginException {
		Object result = null;
		Iterator<String> it = serverSecurityDomains.iterator();
		while (it != null && it.hasNext()) {
			String securityDomain = (String) it.next();
			if (securityDomain != null) {
				try {
					// Set current securityDomain to getServerSubject method
					this.securityDomain = securityDomain;
					// Clear schemContext to current securityDomain
					negotiationContext.setSchemeContext(null);
					result = spnegoLogin(negotiationContext);
					if (result instanceof Exception) {
						log.error("Error in spnegoLogin with " + this.securityDomain, (Exception) result);
						continue;
					}
					return result;
				}
				catch (Exception e) {
					if (e instanceof LoginException) {
						throw (LoginException) e;
					}
					else {
						log.error("Error in spnegoLogin with " + this.securityDomain, e);
					}
				}
			}

		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.jboss.security.negotiation.spnego.SPNEGOLoginModule#getServerSubject()
	 */
	@Override
	protected Subject getServerSubject() throws LoginException {
		LoginContext lc = new LoginContext(securityDomain);
		lc.login();
		// Cache so we can log out.
		loginContext = lc;

		Subject serverSubject = loginContext.getSubject();
		if (log.isDebugEnabled()) {
			log.debug("Subject = " + serverSubject);
			log.debug("Logged in '" + securityDomain + "' LoginContext");
		}

		return serverSubject;
	}

	/**
	 * Overridden method 
	 * 
	 * @param negotiationContext
	 * @return
	 * @throws LoginException
	 */
	private Object spnegoLogin(NegotiationContext negotiationContext) throws LoginException {
		NegotiationMessage requestMessage = negotiationContext.getRequestMessage();
		if (requestMessage instanceof SPNEGOMessage == false
				&& requestMessage instanceof KerberosMessage == false) {
			String message = "Unsupported negotiation mechanism '" + requestMessage.getMessageType() + "'.";
			log.warn(message);
			throw new LoginException(message);
		}

		try {
			Subject server = getServerSubject();
			AcceptSecContext action = new AcceptSecContext(negotiationContext);
			Object result = Subject.doAs(server, action);

			return result;
		}
		finally {
			if (loginContext != null) {
				// TODO - We may not actually want to logout as if we use cache this may clear it,
				loginContext.logout();
			}
		}

	}

	
	/**
	 * Copied from {@link SPNEGOLoginModule}
	 */
	private class AcceptSecContext implements PrivilegedAction {

		private final NegotiationContext negotiationContext;

		public AcceptSecContext(final NegotiationContext negotiationContext) {
			this.negotiationContext = negotiationContext;
		}

		public Object run() {
			boolean DEBUG = log.isDebugEnabled();
			try {
				// The message type will have already been checked before this point so we know it is
				// a SPNEGO message.
				NegotiationMessage requestMessage = negotiationContext.getRequestMessage();

				// TODO - Ensure no way to fall through with gssToken still null.
				byte[] gssToken = null;
				if (requestMessage instanceof NegTokenInit) {
					NegTokenInit negTokenInit = (NegTokenInit) requestMessage;
					List<Oid> mechList = negTokenInit.getMechTypes();

					if (mechList.get(0).equals(kerberos)) {
						gssToken = negTokenInit.getMechToken();
					}
					else {
						boolean kerberosSupported = false;

						Iterator<Oid> it = mechList.iterator();
						while (it.hasNext() && kerberosSupported == false) {
							kerberosSupported = it.next().equals(kerberos);
						}

						NegTokenTarg negTokenTarg = new NegTokenTarg();

						if (kerberosSupported) {
							negTokenTarg.setNegResult(NegTokenTarg.ACCEPT_INCOMPLETE);
							negTokenTarg.setSupportedMech(kerberos);
						}
						else {
							negTokenTarg.setNegResult(NegTokenTarg.REJECTED);
						}
						negotiationContext.setResponseMessage(negTokenTarg);

						return Boolean.FALSE;
					}

				}
				else if (requestMessage instanceof NegTokenTarg) {
					NegTokenTarg negTokenTarg = (NegTokenTarg) requestMessage;

					gssToken = negTokenTarg.getResponseToken();
				}
				else if (requestMessage instanceof KerberosMessage) {
					KerberosMessage kerberosMessage = (KerberosMessage) requestMessage;

					gssToken = kerberosMessage.getToken();
				}

				Object schemeContext = negotiationContext.getSchemeContext();
				if (schemeContext != null && schemeContext instanceof GSSContext == false) {
					throw new IllegalStateException("The schemeContext is not a GSSContext");
				}

				GSSContext gssContext = (GSSContext) schemeContext;
				if (gssContext == null) {
					log.debug("Creating new GSSContext.");
					GSSManager manager = GSSManager.getInstance();
					gssContext = manager.createContext((GSSCredential) null);

					negotiationContext.setSchemeContext(gssContext);
				}

				if (gssContext.isEstablished()) {
					log.warn("Authentication was performed despite already being authenticated!");

					// TODO - Refactor to only do this once.
					setIdentity(new KerberosPrincipal(gssContext.getSrcName().toString()));

					if (DEBUG) {
						log.debug("context.getCredDelegState() = " + gssContext.getCredDelegState());
						log.debug("context.getMutualAuthState() = "
								+ gssContext.getMutualAuthState());
						log.debug("context.getSrcName() = " + gssContext.getSrcName().toString());
					}

					negotiationContext.setAuthenticationMethod(SPNEGO);
					negotiationContext.setAuthenticated(true);

					return Boolean.TRUE;
				}

				byte[] respToken = gssContext.acceptSecContext(gssToken, 0, gssToken.length);

				if (respToken != null) {
					NegotiationMessage response;
					if (requestMessage instanceof KerberosMessage) {
						response = new KerberosMessage(Constants.KERBEROS_V5, respToken);
					}
					else {
						NegTokenTarg negTokenTarg = new NegTokenTarg();
						negTokenTarg.setResponseToken(respToken);

						response = negTokenTarg;
					}

					negotiationContext.setResponseMessage(response);
				}

				if (gssContext.isEstablished() == false) {
					return Boolean.FALSE;
				}
				else {
					setIdentity(createIdentity(gssContext.getSrcName().toString()));

					if (DEBUG) {
						log.debug("context.getCredDelegState() = " + gssContext.getCredDelegState());
						log.debug("context.getMutualAuthState() = "
								+ gssContext.getMutualAuthState());
						log.debug("context.getSrcName() = " + gssContext.getSrcName().toString());
					}

					// TODO - Get these two in synch - maybe isAuthenticated based on an authentication method been set?
					negotiationContext.setAuthenticationMethod(SPNEGO);
					negotiationContext.setAuthenticated(true);
					return Boolean.TRUE;
				}

			}
			catch (Exception e) {
				return e;
			}

		}
	}

}