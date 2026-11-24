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
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.DatabaseServerLoginModule;

import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.constants.Constant;


/**
 * @author AdNovum Informatik AG
 */
public class LogonLanUsersRolesLoginModule extends DatabaseServerLoginModule {
    
    private Principal identity;
    private char credential[];
    protected String holspaceQuery;
    protected String roleGroupDenominator;
    protected String callerPrincipalDenominator;
    protected String authorizedUserDenominator;
    private String rolesGroupKey;
    private String principalKey;
    private String credentialKey;
    protected String jnpHostname;
    
    public LogonLanUsersRolesLoginModule() {
        holspaceQuery = "SELECT TLGNU_USER.USER_ID FROM TLGNU_USER INNER JOIN TLGNC_PERSON ON TLGNU_USER.PERSON_ID = TLGNC_PERSON.PERSON_ID WHERE TLGNU_USER.LDAP_HOLSPACE=? AND TLGNC_PERSON.ACTIVE_FLAG=1";
        rolesQuery = "SELECT f.FUNC_ID, 'Roles' FROM TLGNU_USER_ROLE ur, TLGNU_ROLE_FUNC rf, TLGNU_FUNC f " +
        		"WHERE ur.USER_ID=? AND ur.ROLE_ID=rf.ROLE_ID AND rf.FUNC_ID=f.FUNC_ID";
	   
        roleGroupDenominator = "Roles";
        callerPrincipalDenominator = "CallerPrincipal";
        authorizedUserDenominator = "Users";
        rolesGroupKey = "com.crh.logon.security.auth.RolesGroup";
        principalKey = "com.crh.logon.security.auth.LogonLanPrincipal";
        credentialKey = "javax.security.auth.login.password";
        jnpHostname = "jnp://localhost:1099";
    }

    
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        
        Object tmp = options.get("extLoginQuery");
        if(tmp != null) {
            holspaceQuery = tmp.toString();
        }
        tmp = options.get("rolesQuery");
        if(tmp != null) {
            rolesQuery = tmp.toString();
        }
        tmp = options.get("roleGroupDenominator");
        if(tmp != null) {
            roleGroupDenominator = tmp.toString();
        }
        tmp = options.get("callerPrincipalDenominator");
        if(tmp != null) {
            callerPrincipalDenominator = tmp.toString();
        }
        tmp = options.get("authorizedUserDenominator");
        if(tmp != null) {
            authorizedUserDenominator = tmp.toString();
        }
        tmp = options.get("rolesGroupKey");
        if(tmp != null) {
            rolesGroupKey = tmp.toString();
        }
        tmp = options.get("principalKey");
        if(tmp != null) {
            principalKey = tmp.toString();
        }
        tmp = options.get("jnpHostname");
        if(tmp != null) {
            jnpHostname = tmp.toString();
        }
        
        if(log.isDebugEnabled()) {
            log.debug("Debug mode enabled for LogonLanUsersRolesLoginModule:");
            log.debug("\tholspaceQuery=" + holspaceQuery);
            log.debug("\trolesQuery=" + rolesQuery);
            log.debug("\troleGroupDenominator=" + roleGroupDenominator);
            log.debug("\tcallerPrincipalDenominator=" + callerPrincipalDenominator);
            log.debug("\tauthorizedUserDenominator=" + authorizedUserDenominator);
            log.debug("\trolesGroupKey=" + rolesGroupKey);
            log.debug("\tprincipalKey=" + principalKey);
            log.debug("\tjnpHostname=" + jnpHostname);
        }
    }
    
    public boolean login() throws LoginException {
        boolean logDebugEnabled = log.isDebugEnabled();
        
        if (logDebugEnabled) {
            logIdentity();
            logPrincipals();
        }
        
        // Check if a previous login succeeded
        if(super.login()) {
            if (logDebugEnabled) {
                log.debug("super.login() is true");
                logSharedState();
                logAuthLoginName();
                logAuthLoginPassword();
                logIdentity();
                logPrincipals();
            }
            
            // Fetch and store new user
            Principal kerberosPrinc = getSPNEGOUser();
            Principal user = getUserFromHolspace(kerberosPrinc);
            
            if (logDebugEnabled) {
                log.debug("Adding user '"+user+"' as principal");
            }
            // Store user in sharedState for later use in commit()
            sharedState.put(principalKey, user);
            identity = user;
            
            
            // Fetch and store attached roles
            if (logDebugEnabled) {
                log.debug("Trying to fetch and store attached roles for user '"+user+"'");
            }
            
            Group rolesGroup = getRolesFromUser(identity.getName());
            if (rolesGroup == null) {
                FailedLoginException fle = new FailedLoginException("No matching roles found in db for user  '"+identity.getName()+"'");
                log.error("Login failed, roles lookup returned no matches from db", fle);
                throw fle;
            }
            
            // Store rolesGroup in sharedState for later use in commit()
            sharedState.put(rolesGroupKey, rolesGroup);
            
            if (logDebugEnabled) {
                logPrincipals();
                logSharedState();
                log.debug("LogonLanUsersRolesLoginModule login() returns true");
            }
            
            // record internal login
            ServiceLocator locator = new ServiceLocator(jnpHostname, false);
            locator.getUserActivityService().recordIntLogInAttempt(kerberosPrinc.getName(), true);
            LoginModuleUtil.setSessionLoginType(Constant.LOGIN_TYPE_INTERNAL);
            
            return true;
            
        }
        
        if (logDebugEnabled) {
            log.debug("super.login() is false");
        }
        super.loginOk = false;
        
        FailedLoginException fle = new FailedLoginException("Kerberos did not deliver any username and credential, holspace validation cannot look up a user.");
        log.error("Login failed, no kerberos credential found", fle);
        throw fle;
    }
    
    protected Principal getUserFromHolspace(Principal holspace) throws LoginException {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("getUserFromHolspace() called with Principal '"+holspace+"'");
        }

        if (holspace == null) {
            FailedLoginException fle = new FailedLoginException("Parameter holspace is null, lookup not possible");
            log.error("Login failed, parameter holspace is null", fle);
            throw fle;
        }
        
        // Check in DB if a user exists for this holspace and create a corresponding Identity
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
                log.debug("Excuting query: '"+holspaceQuery+"'");
                log.debug("Used holspace: '"+holspace.getName()+"'");
            }
            
            ps = conn.prepareStatement(holspaceQuery);
            ps.setString(1, holspace.getName());
            rs = ps.executeQuery();
            if(!rs.next()) {
                FailedLoginException fle = new FailedLoginException("No matching username found in db for holspace '"+holspace.getName()+"'");
                log.error("Login failed, holspace lookup returned no matches from db", fle);
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
            if(rs != null) {
                try {
                    rs.close();
                }
                catch(SQLException e) { }
            }
            if(ps != null) {
                try {
                    ps.close();
                }
                catch(SQLException e) { }
            }
            if(conn != null) {
                try {
                    conn.close();
                }
                catch(SQLException ex) { }
            }
        }
        if (logDebugEnabled) {
            log.debug("Found user '"+user+"' for holspace '"+holspace.getName()+"'");
        }
        return user;
    }
    

    protected Group getRolesFromUser(String username) throws LoginException {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("getRolesFromUser() called with username '"+username+"'");
        }

        if (username == null) {
            FailedLoginException fle = new FailedLoginException("Parameter username is null, lookup not possible");
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
                
                if(groupName == null || groupName.length() == 0) {
                    groupName = roleGroupDenominator;
                }
                Group group = (Group)roleSetsMap.get(groupName);
                if(group == null) {
                    if (logDebugEnabled) {
                        log.debug("No group found for groupName '"+groupName+"', creating new one.");
                    }
                    group = new SimpleGroup(groupName);
                    roleSetsMap.put(groupName, group);
                }
                try {
                    // Create a principal for this role
                    java.security.Principal p = createIdentity(name);
                    // Add this role to the role group
                    group.addMember(p);
                }
                catch(Exception e) {
                    log.error("Failed to create principal '"+name+"'", e);
                }
            } while (rs.next());
            
        }
        catch(NamingException ne) {
            LoginException le = new LoginException("datasource lookup failed, could not find configured datasource '"+dsJndiName+"'");
            le.initCause(ne);
            log.error("", le);
            throw le;
        }
        catch(SQLException ex) {
            log.error("SQLException: "+ex.getMessage());
            LoginException le = new LoginException("Query failed");
            le.initCause(ex);
            throw le;
        }
        finally {
            if(rs != null) {
                try {
                    rs.close();
                }
                catch(SQLException e) { }
            }
            if(ps != null) {
                try {
                    ps.close();
                }
                catch(SQLException e) { }
            }
            if(conn != null) {
                try {
                    conn.close();
                }
                catch(SQLException ex) { }
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
        
        return roleSet;
    }
    
       
    /** Method to commit the authentication process (phase 2). If the login
    method completed successfully as indicated by loginOk == true, this
    method adds the getIdentity() value to the subject getPrincipals() Set.
    It also adds the members of each Group returned by getRoleSets()
    to the subject getPrincipals() Set.
    
    @see javax.security.auth.Subject;
    @see java.security.acl.Group;
    @return true always.
    */
    public boolean commit() throws LoginException {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("LogonLanUsersRolesLoginModule commit, loginOk="+loginOk);
            logPrincipals();
        }
        if(loginOk == false) {
            return false;
        }
        
        Set principals = subject.getPrincipals();
        boolean replaceCallerPrincipal = false;
        boolean replaceRoleGroup = false;
        boolean replaceKerberosPrincipal = false;
        
        // Fetch prepared data from login() call
        Principal preparedUser = null;
        Group preparedRoleGroup = null;
        try {
            preparedUser = (Principal)sharedState.get(principalKey);
            preparedRoleGroup = (Group)sharedState.get(rolesGroupKey);
            if (logDebugEnabled) {
                log.debug("Prepared user from login(): '"+preparedUser+"'");
                log.debug("Prepared roles from login(): '"+preparedRoleGroup+"'");
            }
        }
        catch (Exception e) {
            LoginException le = new LoginException("user or roles lookup failed, could not find prefetched data from login");
            le.initCause(e);
            log.error("", le);
            throw le;
        }
        
        for (Iterator it = principals.iterator(); it.hasNext();) {
            Principal princ = (Principal) it.next();
            
            if (logDebugEnabled) {
                log.debug("Principal: '"+princ+"' found with type '"+princ.getClass().getName()+"'");
            }
            
            if (princ instanceof SimpleGroup) {
                SimpleGroup sg = (SimpleGroup) princ;
                
                // Replace holspace with userid in CallerPrincipal
                if (sg.getName().equals(callerPrincipalDenominator)) {
                    it.remove();
                    replaceCallerPrincipal = true;
                    if (logDebugEnabled) {
                        log.debug("Removed Kerberos-CallerPrincipal, replace it with customized CallerPrincipal");
                    }
                    continue;
                }
                
                // Add own roles
                if (sg.getName().equals(roleGroupDenominator)) {
                    it.remove();
                    replaceRoleGroup = true;
                    if (logDebugEnabled) {
                        log.debug("Removed roles group, replace it with customized SimpleGroup");
                    }
                    continue;
                }
            }

            // Remove kerberos principal and replace with new SimplePrincipal 
            if (princ instanceof KerberosPrincipal) {                
                it.remove();
                replaceKerberosPrincipal = true;
                if (logDebugEnabled) {
                    log.debug("Removed kerberos principal, replace it with customized Principal");
                }
                continue;
            }
        }
        
        if (replaceCallerPrincipal) {
            Group callerPrincipal = new SimpleGroup(callerPrincipalDenominator);
            callerPrincipal.addMember(preparedUser);
            principals.add(callerPrincipal);
            if (logDebugEnabled) {
                log.debug("CallerPrincipal added to principals: "+callerPrincipal);
            }
        }
        
        if (replaceRoleGroup) {
            principals.add(preparedRoleGroup);
            if (logDebugEnabled) {
                log.debug("SimpleGroup with roles added to principals: "+preparedRoleGroup);
            }
        }
        
        if (replaceKerberosPrincipal) {
            principals.add(preparedUser);
            if (logDebugEnabled) {
                log.debug("Principal added to principals: "+preparedUser);
            }
        }
        
        logPrincipals();
        
        if (logDebugEnabled) {
            log.debug("LogonLanUsersRolesLoginModule commit() returns true");
        }
        return true;
    }
    
    protected Principal getIdentity() {
        return identity;
    }
    

    protected void logPrincipals() {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("logPrincipals:");
            try {
                java.util.Set<Principal> principals = subject.getPrincipals();
                log.debug("'\tPrincipal has '"+principals.size()+"' elements: ");
                for (Principal p : principals) {
                    log.debug("\t\tPrincipal is: "+p+"' and has type '"+p.getClass().getName()+"'");
                }
            }
            catch (Exception e) {
                log.error("An exception occurred while dumping the current principals: "+e.getMessage());
            }
        }
    }
    
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
    
    protected void logAuthLoginName() {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("logAuthLoginName:");
            Object usernameObj = sharedState.get("javax.security.auth.login.name");
            if(usernameObj instanceof Principal) {
                Principal usernamePrinc = (Principal)usernameObj;
                log.debug("\tFound identity as Principal from sharedState.get(javax.security.auth.login.name): '"+usernamePrinc+"'");
            } 
            else {
                String name = usernameObj.toString();
                try {
                    identity = createIdentity(name);
                    log.debug("\tFound identity as String from sharedState.get(javax.security.auth.login.name): '"+identity+"'");
                }
                catch(Exception e) {
                    log.debug("\tFailed to create principal with the name '"+name+"'", e);
                }
            }
        }
    }
    
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
    
    protected void logIdentity() {
        boolean logDebugEnabled = log.isDebugEnabled();         
        if (logDebugEnabled) {
            log.debug("logIdentity:");
            log.debug("\tCurrently stored identity: "+identity);
        }
    }
    
    protected Principal getSPNEGOUser() throws FailedLoginException {
        boolean logDebugEnabled = log.isDebugEnabled();         
        if (logDebugEnabled) {
            log.debug("Trying to retrieve SPNEGO Kerberos Principal");
        }
        Principal princ = null;
        
        Object kerberosPrinc = sharedState.get("javax.security.auth.login.name");
        if(kerberosPrinc instanceof Principal) {
            princ = (Principal)kerberosPrinc;
        }
        else {
            String name = kerberosPrinc.toString();
            try {
                princ = createIdentity(name);
            }
            catch(Exception e) {
                String msg = "Not able to retrieve SPNEGO Kerberos Principal, found name is '"+name+"'";
                FailedLoginException fle = new FailedLoginException(msg);
                fle.initCause(e);
                log.error(msg, fle);
                throw fle;
            }
        }
        if (logDebugEnabled) {
            log.debug("Found SPNEGO Kerberos Principal from sharedState.get(javax.security.auth.login.name): '"+princ+"'");
        }
        return princ;
    }
}
