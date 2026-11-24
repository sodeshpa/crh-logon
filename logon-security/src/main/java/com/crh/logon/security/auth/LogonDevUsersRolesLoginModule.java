/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.security.auth;

import java.security.Principal;
import java.security.acl.Group;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import com.crh.logon.security.srv.ServiceManagerFactory;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.DatabaseServerLoginModule;

import com.crh.logon.constants.Constant;
import com.crh.logon.utils.PasswordUtil;


/**
 * @author AdNovum Informatik AG
 */
public class LogonDevUsersRolesLoginModule extends DatabaseServerLoginModule {
    
    private Principal identity;
    private char credential[];
    protected String extloginQuery;
    protected String roleGroupDenominator;
    protected String authorizedUserDenominator;
    protected String callerPrincipalDenominator;
    private String principalKey;
    private String credentialKey;
    protected String jnpHostname;
    
    public LogonDevUsersRolesLoginModule() {
        extloginQuery = "SELECT u.USER_ID FROM TLGNU_USER u INNER JOIN TLGNC_PERSON p ON u.PERSON_ID = p.PERSON_ID WHERE UPPER(u.EXT_LOGIN) = UPPER(?) and u.EXT_LOGIN_ACTIVE_FLAG=1 AND p.ACTIVE_FLAG=1";
        principalsQuery = "SELECT u.EXT_LOGIN_PWD FROM TLGNU_USER u WHERE UPPER(u.EXT_LOGIN) = UPPER(?) and u.EXT_LOGIN_ACTIVE_FLAG=1";
        rolesQuery = "SELECT f.FUNC_ID, 'Roles' FROM TLGNU_USER_ROLE ur, TLGNU_ROLE_FUNC rf, TLGNU_FUNC f " +
                "WHERE ur.USER_ID=? AND ur.ROLE_ID=rf.ROLE_ID AND rf.FUNC_ID=f.FUNC_ID";
        roleGroupDenominator = "Roles";
        authorizedUserDenominator = "Users";
        callerPrincipalDenominator = "CallerPrincipal";
        principalKey = "javax.security.auth.login.name";
        credentialKey = "javax.security.auth.login.password";
        jnpHostname = "jnp://localhost:1099";
    }
    
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        
        Object tmp = options.get("dsJndiName");
        if(tmp != null) {
            dsJndiName = tmp.toString();
        }
        tmp = options.get("extloginQuery");
        if(tmp != null) {
            extloginQuery = tmp.toString();
        }
        tmp = options.get("rolesQuery");
        if(tmp != null) {
            rolesQuery = tmp.toString();
        }
        tmp = options.get("roleGroupDenominator");
        if(tmp != null) {
            roleGroupDenominator = tmp.toString();
        }
        tmp = options.get("authorizedUserDenominator");
        if(tmp != null) {
            authorizedUserDenominator = tmp.toString();
        }
        tmp = options.get("callerPrincipalDenominator");
        if(tmp != null) {
            callerPrincipalDenominator = tmp.toString();
        }
        tmp = options.get("principalKey");
        if(tmp != null) {
            principalKey = tmp.toString();
        }
        tmp = options.get("credentialKey");
        if(tmp != null) {
            credentialKey = tmp.toString();
        }
        tmp = options.get("jnpHostname");
        if(tmp != null) {
            jnpHostname = tmp.toString();
        }
        
        if(log.isDebugEnabled()) {
            log.debug("Debug mode enabled for LogonDevUsersRolesLoginModule:");
            log.debug("\tdsJndiName=" + dsJndiName);
            log.debug("\textloginQuery=" + extloginQuery);
            log.debug("\trolesQuery=" + rolesQuery);
            log.debug("\troleGroupDenominator=" + roleGroupDenominator);
            log.debug("\tauthorizedUserDenominator=" + authorizedUserDenominator);
            log.debug("\tcallerPrincipalDenominator=" + callerPrincipalDenominator);
            log.debug("\tprincipalKey=" + principalKey);
            log.debug("\tcredentialKey=" + credentialKey);
            log.debug("\tjnpHostname=" + jnpHostname);
        }
    }
    
    /**
     * Retrieves the provided username and password via callback, validates the password, 
     * looks up the internal userid and prepares the userid as a principal
     */
    public boolean login() throws LoginException {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("LogonDevUsersRolesLoginModule login() called");
            logIdentity();
            logPrincipals();
        }
        
        // Check if a principal is already set from a previous module
        Object username = sharedState.get(principalKey);
        if (username != null) {
            if(username instanceof Principal) {
                identity = (Principal)username;
            } 
            else {
                String name = username.toString();
                try {
                    identity = createIdentity(name);
                }
                catch(Exception e) {
                    String msg = "Failed to create principal from name provided by a previous login module";
                    log.debug(msg, e);
                    LoginException le = new LoginException(msg);
                    le.initCause(e);
                    throw le;
                }
            }
            Object password = sharedState.get(credentialKey);
            if(password instanceof char[]) {
                credential = (char[])(char[])password;
            }
            else if (password != null) {
                String tmp = password.toString();
                credential = tmp.toCharArray();
            }
            else {
                String msg = "Failed to create password from password provided by a previous login module (password is null)";
                LoginException le = new LoginException(msg);
                throw le;
            }
            
            if (logDebugEnabled) {
                log.debug("A previous login module has already set a user name '"+identity.getName()+"' and a password.");
                log.debug("Validating these parameters in database");
            }
        }
        else {
            // Use callback to retrieve username and password
            if (logDebugEnabled) {
                log.debug("No username set, using callback handler to retrieve information");
            }
            super.loginOk = false;
            String info[] = getUsernameAndPassword();
            if (logDebugEnabled) {
                log.debug("Retrieved username: " + info[0]);
                if (info[1] != null) {
                    log.debug("Retrieved a non-null password");
                }
                else {
                    log.debug("Retrieved password is null");
                }
            }
            String callbackUsername = info[0];
            String callbackPassword = info[1];
            if (callbackUsername == null) {
                String msg = "username provided for login is null";
                LoginException le = new LoginException(msg);
                log.debug(msg, le);
                throw le;
            }
            
            try {
                identity = createIdentity(callbackUsername);
            }
            catch(Exception e) {
                String msg = "Failed to create principal from name '"+callbackUsername+"' provided by callback handler";
                log.debug(msg, e);
                LoginException le = new LoginException(msg);
                le.initCause(e);
                throw le;
            }
            if (callbackPassword != null) {
                credential = callbackPassword.toCharArray();
            }
            else {
                String msg = "Failed to create password from password provided by a previous login module (password is null)";
                LoginException le = new LoginException(msg);
                throw le;
            }
        }
        
        // Validate password
        if (logDebugEnabled) {
            log.debug("validating password for identity '"+identity+"'");
        }
        
        String expectedPassword = getUsersPassword();

		// prepare service managers
		final ServiceManagerFactory smf = new ServiceManagerFactory(dsJndiName);
		
		String inPwd = new String(credential);
		if(!PasswordUtil.validatePassword(inPwd, expectedPassword)) {
        	recordExtLogInAttempt(smf, identity.getName(), false);
            Throwable ex = getValidateError();
            FailedLoginException fle = new FailedLoginException("Password Incorrect/Password Required");
            if(ex != null) {
                log.debug("Password validation failed, bad password for username="+username, ex);
                fle.initCause(ex);
            } else {
                log.debug("Password validation failed, bad password for username="+username);
            }
            smf.cleanupConnection("login()");
            throw fle;
        }
        else {
        	if (isExtLoginPwdLocked(smf, identity.getName())) {
        		log.debug("external login password locked, extLogin: " + identity.getName());
        		recordExtLogInAttempt(smf, identity.getName(), false);
        		FailedLoginException fle = new FailedLoginException("external login password locked, extLogin: " + identity.getName());
        		throw fle;
        	}
        	else {
        		recordExtLogInAttempt(smf, identity.getName(), true);
        		LoginModuleUtil.setSessionLoginType(Constant.LOGIN_TYPE_EXTERNAL);
        	}
        }
        
        smf.cleanupConnection("login()");
        
        if (logDebugEnabled) {
        	log.debug("Password validation successful");
        }
        
        // Replacing external login id with user id 
        Principal internalUser = getUserFromExtLogin(identity);
        if (logDebugEnabled) {
            log.debug("externalUser: "+identity);
            log.debug("internalUser: "+internalUser);
        }
        identity = internalUser;
        
    
        if (logDebugEnabled) {
            log.debug("Putting username and credential in sharedState for later use");
        }
        sharedState.put(principalKey, identity.getName());
        sharedState.put(credentialKey, new String(credential));
        
        super.loginOk = true;
        
        if (logDebugEnabled) {
            logPrincipals();
            logSharedState();
            log.debug("LogonDevUsersRolesLoginModule login() returns true");
        }
        
        return true;
    }
    
	private boolean isExtLoginPwdLocked(final ServiceManagerFactory smf, final String identityName)
			throws LoginException {
		return smf.transaction(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return smf.getUserServiceManager().isExtLoginPwdLocked(identityName);
			}
		});
    }
	
	private void recordExtLogInAttempt(final ServiceManagerFactory smf, final String identityName,
			final boolean isSuccessful) throws LoginException {
		smf.transaction(new Callable<Void>() {
			public Void call() throws Exception {
				smf.getUserServiceManager().recordExtLogInAttempt(identityName, isSuccessful);
				return null;
			}
		});
	}
    
    /**
     * Returns the userid for the user with the corresponding external login
     * @param extlogin
     * @return
     * @throws LoginException
     */
    protected Principal getUserFromExtLogin(Principal extlogin) throws LoginException {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("getUserFromExtLogin() called with Principal '"+extlogin+"'");
        }

        if (extlogin == null) {
            String msg = "Parameter extlogin is null, lookup not possible";
            FailedLoginException fle = new FailedLoginException(msg);
            log.error(msg, fle);
            throw fle;
        }
        
        // Check in DB if a user exists for this extlogin and create a corresponding Identity
        Principal user = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String userid = null;
        try {
            if (logDebugEnabled) {
                log.debug("Setting up database parameters:");
                log.debug("Looking up datasource with dsJndiName '"+dsJndiName+"'");
            }
            
            InitialContext ctx = new InitialContext();
            DataSource ds = null;
            
            ds = (DataSource)ctx.lookup(dsJndiName);
            
            if (ds == null) {
                Object obj = ctx.lookup(dsJndiName);
                FailedLoginException fle = new FailedLoginException("datasource lookup failed, could not find configured datasource '"+obj+"'");
                log.error("Login failed, datasource lookup failed", fle);
                throw fle;
            }
            else if (logDebugEnabled) {
                log.debug("datasource lookup successful");
            }
            
            conn = ds.getConnection();
            
            if (logDebugEnabled) {
                log.debug("Excuting query: '"+extloginQuery+"'");
                log.debug("Used extlogin: '"+extlogin.getName()+"'");
            }
            
            ps = conn.prepareStatement(extloginQuery);
            ps.setString(1, extlogin.getName());
            rs = ps.executeQuery();
            if(!rs.next()) {
                FailedLoginException fle = new FailedLoginException("No matching username found in db for extlogin '"+extlogin.getName()+"'");
                log.error("Login failed, extlogin lookup returned no matches from db", fle);
                throw fle;
            }
            
            if (logDebugEnabled) {
                log.debug("Query returned with at least one element");
            }
            
            userid = rs.getString(1);
            
            if (logDebugEnabled) {
                log.debug("Obtained userid '"+userid+"'");
            }
            
            user = createIdentity(userid);
            
            if (logDebugEnabled) {
                log.debug("Identity user successfully created: '"+user+"'");
            }
        }
        catch (NamingException ne) {
            LoginException le = new LoginException("datasource lookup failed, could not find configured datasource '"+dsJndiName+"'");
            le.initCause(ne);
            log.error("", le);
            throw le;
        }
        catch(SQLException ex) {
            LoginException le = new LoginException("Query failed");
            le.initCause(ex);
            throw le;
        }
        catch (Exception ex) {
            LoginException le = new LoginException("Could not create principal from userid '"+userid+"'");
            le.initCause(ex);
            throw le;
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch(SQLException e) { log.error(e); }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch(SQLException e) { log.error(e); }
            }
            if (conn != null) {
                try {
                    conn.close();
                }
                catch(SQLException ex) { log.error(ex); }
            }
        }
        if (logDebugEnabled) {
            log.debug("Found user '"+user+"' for external login '"+extlogin.getName()+"'");
        }
        return user;
    }
    
    /**
     * Returns the role sets for the current identity and attaches the 'authorizedUserDenominator'
     * as a role. The authorizedUserDenominator is a required role for every user but is not stored
     * in the database
     */
    protected Group[] getRoleSets() throws LoginException {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("getRoleSets() called, using username '"+getUsername()+"'");
        }
        
        String username = getUsername();
        if (username == null) {
            FailedLoginException fle = new FailedLoginException("Parameter username is null, role lookup not possible");
            log.error("Login failed, parameter username is null", fle);
            throw fle;
        }
        
        // Check in DB if a user exists for this username and lookup its roles
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map roleSetsMap = new HashMap(); 
        
        try {
            if (logDebugEnabled) {
                log.debug("Setting up database parameters:");
                log.debug("Looking up DataSource with dsJndiName '"+dsJndiName+"'");
            }

            InitialContext ctx = new InitialContext();
            DataSource ds = null;
            
            ds = (DataSource)ctx.lookup(dsJndiName);
            
            if (ds == null) {
                Object obj = ctx.lookup(dsJndiName);
                FailedLoginException fle = new FailedLoginException("datasource lookup failed, could not find configured datasource '"+obj+"'");
                log.error("Login failed, datasource lookup failed", fle);
                throw fle;
            }
            else if (logDebugEnabled) {
                log.debug("datasource lookup successful");
            }
            
            conn = ds.getConnection();
            
            if (logDebugEnabled) {
                log.debug("Excuting query: '"+rolesQuery+"'");
                log.debug("Used username: '"+username+"'");
            }
            
            ps = conn.prepareStatement(rolesQuery);
            ps.setString(1, username);
            rs = ps.executeQuery();
            
            if(!rs.next()) {
                FailedLoginException fle = new FailedLoginException("No matching functions or roles found in db for username '"+username+"'");
                log.error("Login failed, functions lookup returned no matches from db", fle);
                throw fle;
            }

            if (logDebugEnabled) {
                log.debug("Query returned with at least one element");
            }
            
            // For each found role(s) entry,  add the role(s) to its role group
            do {
                String name = null;
                String groupName = null;
                try {
                    name = rs.getString(1);
                    if (logDebugEnabled) {
                        log.debug("Found name '"+name+"'");
                    }
                    groupName = rs.getString(2);
                    if (logDebugEnabled) {
                        log.debug("Found groupName '"+groupName+"'");
                    }
                }
                catch (Exception e) {
                    String msg = "Your query returned a malformatted result. Please make sure your query returns a correctly formatted result for this login module";
                    log.error(msg, e);
                    FailedLoginException fle = new FailedLoginException(msg);
                    throw fle; 
                }
                
                if (groupName == null || groupName.length() == 0) {
                    groupName = roleGroupDenominator;
                }
                Group group = (Group)roleSetsMap.get(groupName);
                if (group == null) {
                    if (logDebugEnabled) {
                        log.debug("No group found for groupName '"+groupName+"', creating new one.");
                    }
                    group = new SimpleGroup(groupName);
                    roleSetsMap.put(groupName, group);
                }
                try {
                    // Create a principal for this role
                    Principal p = createIdentity(name);
                    // Add this role to the role group
                    group.addMember(p);
                }
                catch (Exception e) {
                    log.error("Failed to create principal '"+name+"'", e);
                }
            }
            while (rs.next());
            
        }
        catch (NamingException ne) {
            LoginException le = new LoginException("datasource lookup failed, could not find configured datasource '"+dsJndiName+"'");
            le.initCause(ne);
            log.error("", le);
            throw le;
        }
        catch (SQLException ex) {
            log.error("SQLException: "+ex.getMessage());
            LoginException le = new LoginException("Query failed");
            le.initCause(ex);
            throw le;
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch(SQLException e) { log.error(e); }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch(SQLException e) { log.error(e);}
            }
            if (conn != null) {
                try {
                    conn.close();
                }
                catch(SQLException ex) { log.error(ex); }
            }
        }

        if (logDebugEnabled) {
            log.debug("RoleSetsMap successfully created with '"+roleSetsMap.size()+"' role groups.");
        }
        
        // Filter for 'Roles' set, do not consider the others
        Group roleSet = (Group) roleSetsMap.get(roleGroupDenominator);
        
        // Add authorizedUserDenominator to assure that this user is generally authorized to access the app
        Principal authUserDenominator = new SimplePrincipal(authorizedUserDenominator);
        roleSet.addMember(authUserDenominator);
        
        Group roleSets[] = new Group[roleSetsMap.size()];
        roleSetsMap.values().toArray(roleSets);
        
        if (logDebugEnabled) {
            log.debug("Group[] roleSets created with length '"+roleSetsMap.size()+"'");
        }
        
        return roleSets;
    }
    
    /**
     * Getter for the private identity variable
     */
    protected Principal getIdentity() {
        return identity;
    }
    

    /**
     * Dumps all principals contained in the subject to log.debug()
     */
    protected void logPrincipals() {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("logPrincipals:");
            try {
                java.util.Set<Principal> principals = subject.getPrincipals();
                log.debug("\tPrincipal has '"+principals.size()+"' elements: ");
                for (Principal p : principals) {
                    log.debug("\t\tPrincipal is: "+p+"' and has type '"+p.getClass().getName()+"'");
                }
            }
            catch (Exception e) {
                log.error("An exception occurred while dumping the current principals: "+e.getMessage());
            }
        }
    }
    
    /**
     * Dumps all entry sets from sharedState to log.debug()
     */
    protected void logSharedState() {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("logSharedState:");
            try {
                log.debug("Dumping sharedState containing '"+sharedState.size()+"' elements: ");
                for (Iterator it = sharedState.entrySet().iterator(); it.hasNext();) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (entry.getKey().equals(credentialKey)) {
                        log.debug("\thas a protected value (credential)");
                    }
                    else {
                        log.debug("\thas value: "+entry.getValue().toString());
                    }
                }
            }
            catch (Exception e) {
                log.error("An exception occurred while dumping the current shared state: "+e.getMessage());
            }
        }
    }
    
    /**
     * Dumps the currently stored principalKey to log.debug()
     */
    protected void logAuthLoginName() {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("logAuthLoginName:");
            Object usernameObj = sharedState.get(principalKey);
            if(usernameObj instanceof Principal) {
                Principal usernamePrinc = (Principal)usernameObj;
                log.debug("\tFound identity as Principal from sharedState.get("+principalKey+"): '"+usernamePrinc+"'");
            } 
            else {
                String name = usernameObj.toString();
                try {
                    identity = createIdentity(name);
                    log.debug("\tFound identity as String from sharedState.get("+principalKey+"): '"+identity+"'");
                }
                catch(Exception e) {
                    log.debug("\tFailed to create principal with the name '"+name+"'", e);
                }
            }
        }
    }
    
    
    /**
     * Dumps the length of the currently stored credentialKey to log.debug()
     */
    protected void logAuthLoginPassword() {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("logAuthLoginPassword:");
            Object password = sharedState.get(credentialKey);
            if(password instanceof char[]) {
                credential = (char[])(char[])password;
                log.debug("\tCredential is char[] and has length "+credential.length);
            }
            else if(password != null) {
                String tmp = password.toString();
                credential = tmp.toCharArray();
                log.debug("\tCredential is an object and has length "+credential.length);
            }
            else {
                log.debug("\tNo credential found or credential is null");
            }
        }
    }
    
    
    /**
     * Dumps the currently stored identity to log.debug()
     */
    protected void logIdentity() {
        boolean logDebugEnabled = log.isDebugEnabled();         
        if (logDebugEnabled) {
            log.debug("logIdentity:");
            log.debug("\tCurrently stored identity: "+identity);
        }
    }
    
}
