/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

import java.io.Serializable;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;


/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 * 
 * NOTE: LogonProperties are defined in the properties files 'logon.properties'
 * and 'buildtime.properties'. It is filled up by spring (from the web tier)
 * and then passed into the ejb tier at application start up.
 * 
 * it contains properties which are either defined at build time or can not
 * be defined in the database, such as which 'instance' the application is
 * running as.
 * 
 * All other configuration parameters are retrieved from the DB and are 
 * accessible through ConfigBean (accessible through ConfigService)
 * 
 * How to add properties:
 * 
 *   1. add new member variable
 *   2. generate/write the getters/setters for the new property
 *   3. add the following tag to the method javadoc 
 *         @adnbus.property propertyName = "name.of.property.in.property.file"
 *   4. 'adnant all' -> the PropertyService Interface will be regenerated, 
 *      making the new property available to the rest of the application.
 */
public class LogonProperties implements Serializable {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(LogonProperties.class);
    
    public final static String ZONE_INT = "int";
    public final static String ZONE_DMZ = "dmz";
    public final static String ZONE_EXT = "ext";

    private String buildVersion = "undef";
    private String instance = null;
    private String providerurl = "jnp://localhost:1099";
    private boolean devmode = false;
    private String zone = ZONE_INT;
    private boolean accessRestrictions = true;
    private boolean execAuthorizeInsurance = false;
    private boolean useHttpOnlySessionIdFilter = false;
    private boolean useHttpOnlySecureSessionIdFilter = true;

	public LogonProperties() {
		LOG.info("initializing properties...");
	}
	
	/**
	 * @adnbus.property propertyName = "build.version"
	 * @adnlog.notrace
	 */
    public void setBuildVersion(String buildVersion) {
		LOG.info("setBuildVersion(): '" + buildVersion + "'");
		/*
		 * optrace initialization...
		 * must be done at the earliest possible moment because optrace
		 * initializes itself in a static block within a Filter where
		 * it reads these system properties
		 */
		System.setProperty("jcan.Op.pkgName", "logon");
		System.setProperty("jcan.Op.pkgVersion", buildVersion);
		this.buildVersion = buildVersion;
    }
    
    public String getBuildVersion() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getBuildVersion(): '" + buildVersion + "'");
		}
		// ]]
		return buildVersion;
    }

    /**
	 * @adnbus.property propertyName = "instance"
	 * @adnlog.notrace
	 */
	public void setInstance(String instanceFormat) {
		final String instance = String.format(instanceFormat, SystemUtils.USER_NAME);		
		LOG.info("setInstance(): '" + instance + "'");
		/*
		 * optrace initialization...
		 */
		System.setProperty("jcan.Op.instanceName", instance);
		this.instance = instance;
	}

	public String getInstance() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getInstance(): '" + instance + "'");
		}
		// ]]
		return instance;
	}

    /**
	 * @adnbus.property propertyName = "providerurl"
	 * @adnlog.notrace
	 */
	public void setProviderurl(String providerurl) {
		LOG.info("setProviderurl(): '" + providerurl + "'");
		this.providerurl = providerurl;
	}

	public String getProviderurl() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getProviderurl(): '" + providerurl + "'");
		}
		// ]]
		return providerurl;
	}
	
    /**
	 * @adnbus.property propertyName = "devmode"
	 * @adnlog.notrace
	 */
	public void setDevmode(boolean devmode) {
		LOG.info("setDevmode(): '" + devmode + "'");
		this.devmode = devmode;
	}

	public boolean getDevmode() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getDevmode(): '" + devmode + "'");
		}
		// ]]
		return devmode;
	}

	/**
	 * either 'int', 'ext' or 'dmz'
	 * 
	 * @adnbus.property propertyName = "zone"
	 * @adnlog.notrace
	 */
	public void setZone(String customer) {
		LOG.info("setZone(): '" + customer + "'");
		this.zone = customer;
	}

	public String getZone() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getZone(): '" + zone + "'");
		}
		// ]]
		return zone;
	}


	/**
	 * @adnbus.property propertyName = "access_restrictions"
	 * @adnlog.notrace
	 */
	public void setAccessRestrictions(boolean accessRestrictions) {
		LOG.info("setAccessRestrictions(): '" + accessRestrictions + "'");
		this.accessRestrictions = accessRestrictions;
	}

	public boolean getAccessRestrictions() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getAccessRestrictions(): '" + accessRestrictions + "'");
		}
		// ]]
		return accessRestrictions;
	}
	
	public boolean getExecAuthorizeInsurance() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getExecAuthorizeInsurance(): '" + execAuthorizeInsurance + "'");
		}
		// ]]
		return execAuthorizeInsurance;
	}

	/**
	 * @adnbus.property propertyName = "exec_authorize_insurance"
	 * @adnlog.notrace
	 */
	public void setExecAuthorizeInsurance(boolean execAuthorizeInsurance) {
		LOG.info("setExecAuthorizeInsurance(): '" + execAuthorizeInsurance + "'");
		this.execAuthorizeInsurance = execAuthorizeInsurance;
	}
	
	/**
	 * @adnbus.property propertyName = "use_HttpOnly_SessionIdFilter"
	 * @adnlog.notrace
	 */
	public void setUseHttpOnlySessionIdFilter(boolean useHttpOnlySessionIdFilter) {
		LOG.info("setuUseHttpOnlySessionIdFilter(): '" + useHttpOnlySessionIdFilter + "'");
		this.useHttpOnlySessionIdFilter = useHttpOnlySessionIdFilter;
	}
	
	public boolean getUseHttpOnlySessionIdFilter() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getUseHttpOnlySessionIdFilter(): '" + useHttpOnlySessionIdFilter + "'");
		}
		// ]]
		return useHttpOnlySessionIdFilter;
	}
	
	/**
	 * @adnbus.property propertyName = "use_HttpOnly_Secure_SessionIdFilter"
	 * @adnlog.notrace
	 */
	public void setuUseHttpOnlySecureSessionIdFilter(boolean useHttpOnlySecureSessionIdFilter) {
		LOG.info("setuUseHttpOnlySecureSessionIdFilter(): '" + useHttpOnlySecureSessionIdFilter + "'");
		this.useHttpOnlySecureSessionIdFilter = useHttpOnlySecureSessionIdFilter;
	}
	
	public boolean getUseHttpOnlySecureSessionIdFilter() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getUseHttpOnlySecureSessionIdFilter(): '" + useHttpOnlySecureSessionIdFilter + "'");
		}
		// ]]
		return useHttpOnlySecureSessionIdFilter;
	}
	
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, new String[0]);
    }
}
