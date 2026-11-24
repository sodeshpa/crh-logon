/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.security.auth;

import java.security.Principal;
import java.security.acl.Group;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;

import com.crh.logon.business.services.interfaces.DmzService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.constants.Constant;


/**
 * @author AdNovum Informatik AG
 */
public class LogonDmzUsersRolesLoginModule extends UsernamePasswordLoginModule {
    
    private Principal identity;
    private char credential[];
    protected String roleGroupDenominator;
    protected String authorizedUserDenominator;
    protected String callerPrincipalDenominator;
    protected String jnpHostname;
    private String principalKey;
    private String credentialKey;
    
    private String terminalMode;
    
    public LogonDmzUsersRolesLoginModule() {
        roleGroupDenominator = "Roles";
        authorizedUserDenominator = "Users";
        callerPrincipalDenominator = "CallerPrincipal";
        jnpHostname = "jnp://localhost:1099";
        principalKey = "javax.security.auth.login.name";
        credentialKey = "javax.security.auth.login.password";
        //true in case of pickup terminal call
        terminalMode = "false";
    }
    
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        
        Object tmp = options.get("roleGroupDenominator");
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
        tmp = options.get("jnpHostname");
        if(tmp != null) {
            jnpHostname = tmp.toString();
        }
        tmp = options.get("principalKey");
        if(tmp != null) {
            principalKey = tmp.toString();
        }
        tmp = options.get("credentialKey");
        if(tmp != null) {
            credentialKey = tmp.toString();
        }
        tmp = options.get("terminalMode");
        if(tmp != null) {
            terminalMode = tmp.toString();
        }        
        
        
        if(log.isDebugEnabled()) {
            log.debug("Debug mode enabled for LogonDmzUsersRolesLoginModule:");
            log.debug("\troleGroupDenominator=" + roleGroupDenominator);
            log.debug("\tauthorizedUserDenominator=" + authorizedUserDenominator);
            log.debug("\tcallerPrincipalDenominator=" + callerPrincipalDenominator);
            log.debug("\tjnpHostname=" + jnpHostname);
            log.debug("\tprincipalKey=" + principalKey);
            log.debug("\tcredentialKey=" + credentialKey);
            log.debug("\tterminalMode=" + terminalMode);
        }
    }
    
    /**
     * Retrieves the provided username and password via callback, validates the password, 
     * looks up the internal userid and prepares the userid as a principal
     */
    public boolean login() throws LoginException {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("LogonDmzUsersRolesLoginModule login() called");
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
            else if(password != null) {
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
                log.debug("Retrieved username: "+info[0]);
                if (info[1] != null) {
                    log.debug("Retrieved a non-null password");
                }
                else {
                    log.debug("Retrieved password is null");
                }
            }
            String callbackUsername = info[0];
            String callbackPassword = info[1];
            if(callbackUsername == null) {
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
        Long userId = null;
        // find internal user id given the provided user name
        if ( terminalMode.equals("false") ) {        	
        	userId = getUserFromExtLogin(identity.getName());
        	if (userId == null) {
        		String msg = "User " + identity.getName() + " not found";
        		LoginException le = new LoginException(msg);
        		throw le;        	
        	}
        }
        //in terminal mode the user name is the pda number, the password is the pickup_barcode_passcode of the user
        else if ( terminalMode.equals("true") ) {
        	userId = getUserFromPdaNumber(identity.getName());
        	if (userId == null) {
        		String msg = "User " + identity.getName() + " not found";
        		LoginException le = new LoginException(msg);
        		throw le;        	
        	}        	
        }
        if (logDebugEnabled) {
            log.debug("externalUser: "+identity);
            log.debug("internalUser: "+userId);
        }
        
        // Validate password
        if (logDebugEnabled) {
            log.debug("validating password for identity '"+identity+"'");
        }
        boolean validate = validateUsersPassword(userId, new String(credential));
        if(!validate) {
            Throwable ex = getValidateError();
            FailedLoginException fle = new FailedLoginException("Password Incorrect/Password Required");
            if(ex != null) {
                log.debug("Password validation failed, bad password for username="+identity.getName(), ex);
                fle.initCause(ex);
            } else {
                log.debug("Password validation failed, bad password for username="+identity.getName());
            }
            throw fle;
        }
        if (logDebugEnabled) {
        	log.debug("Password validation successful");
        }
        
        // Replacing external login id with user id 
        Principal internalPrinc = null;
        try {
            internalPrinc = createIdentity(userId.toString());
        }
        catch(Exception e) {
            String msg = "Failed to create principal from name provided by service";
            log.debug(msg, e);
            LoginException le = new LoginException(msg);
            le.initCause(e);
            throw le;
        }
        identity = internalPrinc;
        
    
        if (logDebugEnabled) {
            log.debug("Putting username and credential in sharedState for later use");
        }
        sharedState.put(principalKey, identity.getName());
        sharedState.put(credentialKey, new String(credential));
        
        super.loginOk = true;
        
        if (logDebugEnabled) {
            logPrincipals();
            logSharedState();
            log.debug("LogonDmzUsersRolesLoginModule login() returns true");
        }
        
        return true;
    }
    
    
    
    @Override
    protected String getUsersPassword() throws LoginException {
        String msg = "LogonDmzUsersRolesLoginModule does not use getUsersPassword(), but checks the password via validateUsersPassword(extLogin,extPassword)";
        throw new LoginException(msg);
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
        
        // Check if a user exists for this username and lookup its roles
        
        Set roleSet = getRoleSet(username); 
        
        if (logDebugEnabled) {
            log.debug("RoleSet successfully created with '"+roleSet.size()+"' roles.");
        }
        
        // Filter for 'Roles' set, do not consider the others
        Group roleGroup = new SimpleGroup(roleGroupDenominator);
        
        // Add authorizedUserDenominator to assure that this user is generally authorized to access the app
        Principal authUserDenominator = new SimplePrincipal(authorizedUserDenominator);
        roleGroup.addMember(authUserDenominator);
        for (Iterator it = roleSet.iterator(); it.hasNext();) {
            String s = (String) it.next();
            if (s != null) {
                Principal princ = new SimplePrincipal(s);
                roleGroup.addMember(princ);
            }
        }
        
        Group roleSets[] = new Group[1];
        roleSets[0] = roleGroup;
        
        if (logDebugEnabled) {
            log.debug("Group[] roleSets created with length 1");
        }
        
        return roleSets;
    }
    
    private boolean validateUsersPassword(Long userid, String password) {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("calling dmzService.validateUsersPassword() for user '"+userid+"'");
        }
        ServiceLocator locator = new ServiceLocator(jnpHostname, false);
        DmzService dmzService = locator.getDmzService();
        boolean result = false;
        if ( terminalMode.equals("false") ) {        	
        	result = dmzService.validateUsersPassword(userid,password);
        	if (result) {
        		LoginModuleUtil.setSessionLoginType(Constant.LOGIN_TYPE_EXTERNAL);
        	}
        }
        else if ( terminalMode.equals("true") ) {
        	//in terminal mode the user name is the pda number, the password is the pickup_barcode_passcode of the user
        	result = dmzService.validatePickupBarcodePasscode(userid,password);
        	if (result) {
        		LoginModuleUtil.setSessionLoginType(Constant.LOGIN_TYPE_TERMINAL);
        	}
        }
        
        if (logDebugEnabled) {
            log.debug("dmzService.validateUsersPassword() returned '"+result+"' for user '"+userid+"'");
        }
        return result;
    }
    
    private Long getUserFromExtLogin(String extLogin) {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("calling dmzService.getUserFromExtLogin() for user '"+extLogin+"'");
        }
        ServiceLocator locator = new ServiceLocator(jnpHostname, false);
        DmzService dmzService = locator.getDmzService();
        Long result = dmzService.getUserFromExtLogin(extLogin);
        if (logDebugEnabled) {
            log.debug("dmzService.getUserFromExtLogin() returned '"+result+"' for user '"+extLogin+"'");
        }
        return result;
    }
    
    private Long getUserFromPdaNumber(String pdaNumber) {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("calling dmzService.getUserFromPdaNumber() for user '"+pdaNumber+"'");
        }
        ServiceLocator locator = new ServiceLocator(jnpHostname, false);
        DmzService dmzService = locator.getDmzService();
        Long result = dmzService.getUserFromPdaNumber(pdaNumber);
        if (logDebugEnabled) {
            log.debug("dmzService.getUserFromPdaNumber() returned '"+result+"' for user '"+pdaNumber+"'");
        }
        return result;
    }
    
    private Set getRoleSet(String userid) {
        boolean logDebugEnabled = log.isDebugEnabled();
        if (logDebugEnabled) {
            log.debug("calling dmzService.getRoleSet() for user '"+userid+"'");
        }
        ServiceLocator locator = new ServiceLocator(jnpHostname, false);
        DmzService dmzService = locator.getDmzService();
        Set result = new HashSet();
        // in normal mode the external login flag must be true
        if ( terminalMode.equals("false") ) {
        	result = dmzService.getRoleSets(userid, true);
        }
        //in terminal mode external login flag must NOT be true
        else if ( terminalMode.equals("true") ) {        	
        	result = dmzService.getRoleSets(userid, false);
        }
        if (logDebugEnabled) {
            log.debug("dmzService.getRoleSet() returned the following set of roles:");
            for (Iterator it = result.iterator(); it.hasNext();) {
                String s = (String) it.next();
                log.debug("Role '"+s+"'");
            }
        }
        return result;
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
                    log.debug("\tEntry: "+entry.getKey().toString());
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
