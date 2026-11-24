
package com.crh.logon.xjc.hyperjaxb3;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jvnet.hyperjaxb3.ejb.strategy.outline.ProcessOutline;
import org.jvnet.hyperjaxb3.ejb.strategy.outline.base.naming.DefaultNaming;
import org.springframework.core.io.Resource;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

public final class NamingExt extends DefaultNaming {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(NamingExt.class);
	
	private final Map<String, String> prefixes;

	private final Properties sapAbbrev;
	
	public NamingExt() {
		super();
		
		if (LOG.isInfoEnabled()) {
			LOG.info("--------------------------------------------------------");
			LOG.info("Extended naming strategy class for hyperjaxb3 initiated.");
			LOG.info("--------------------------------------------------------");
		}
		
		this.prefixes = new HashMap<String, String>();
		this.sapAbbrev = new Properties();
	}

	public void setSapAbbrevResource(Resource sapAbbrevResource) throws IOException {
		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("Using file '%s'.", sapAbbrevResource));			
		}
		
		this.sapAbbrev.clear();
		this.sapAbbrev.load(sapAbbrevResource.getURL().openStream());
		
		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("Using properties %s.", this.sapAbbrev));
		}
	}
	
	public String getEntityTableName(ProcessOutline context, FieldOutline fieldOutline, Options options) {
		final boolean debugEnabled = LOG.isDebugEnabled();
		String result = null;
		if (debugEnabled) {
			Object[][] params = { 
					{ "context", context},
					{ "fieldOutline", fieldOutline},
					{ "options", options}};
			LOG.entering(this, "getEntityTableName", params);
		}
		try {

			return result = getEntityTableName(fieldOutline.parent().target);
			
		}
		finally {
			if (debugEnabled) {
				LOG.exiting(result);
			}
		}
		
	}

	public String getEntityTableName(ProcessOutline context, ClassOutline classOutline, Options options) {
		final boolean debugEnabled = LOG.isDebugEnabled();
		String result = null;
		if (debugEnabled) {
			Object[][] params = { 
					{ "context", context},
					{ "classOutline", classOutline},
					{ "options", options}};
			LOG.entering(this, "getEntityTableName", params);
		}
		try {

			return result = getEntityTableName(classOutline.target);

		}
		finally {
			if (debugEnabled) {
				LOG.exiting(result);
			}
		}		
	}
	
	private String getEntityTableName(CClassInfo childClassInfo) {
		final boolean debugEnabled = LOG.isDebugEnabled();
		String result = null;
		
		if (debugEnabled) {
			Object[][] params = { 
					{ "childClassInfo", childClassInfo}};
			LOG.entering(this, "appendPrefix", params);
		}
		try {
			final String name = childClassInfo.getSqueezedName();
			
			CClassInfoParent parent;
			CClassInfo locatorHolder = childClassInfo;
			while (locatorHolder.getLocator() == null) {
				parent = locatorHolder.parent();
				if (parent == null) {		
					throw new IllegalStateException("Parent hierarchy exhausted, unable to get locator!");					
				}
				else if (!(parent instanceof CClassInfo)) {
					throw new IllegalStateException("Parent is not a class, unable to get locator!");
				}
				else {
					locatorHolder = (CClassInfo) parent;
					if (debugEnabled) {
						LOG.debug(String.format(
								"-> hierarchy [%s]",
								locatorHolder));
					}
				}
			}
			final String origin = locatorHolder.getLocator().getSystemId();
			final String prefix;
			
			if (!prefixes.containsKey(origin)) {
				
				try {
					final URL originURL = new URL(origin);
					final File originFile = new File(originURL.getFile());
					final String originName = originFile.getName();
					final String key =  originName.replaceAll("(\\.[^.]*)?$", "");
					
					final String value = this.sapAbbrev.getProperty(key);
					if (value == null) {			
						throw new IllegalArgumentException(String.format(
								"Our properties file does not contain the key '%s' (origin: '%s')!",
								key, origin));
					}
					else {
						prefix = value.toUpperCase();
						prefixes.put(origin, prefix);
						
						if (LOG.isInfoEnabled()) {
							LOG.info(String.format(
									"Calculated '%2$s' for '%1$s'.",
									origin, prefix));
						}
					}
				}
				catch (Exception e) {				
					throw new IllegalArgumentException(String.format(
							"Could not parse the origin ('%s') to a meaningful key!",
							origin));
				}
			}
			else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Getting cached result.");
				}
				
				prefix = prefixes.get(origin);
			}

			result = super.getName(prefix + "_" + name);
			
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("'%s' -> '%s'", name, result));
			}
			
			return result; 

		}
		finally {
			if (debugEnabled) {
				LOG.exiting(result);
			}
		}
	}
	
	public String getName(String draftName) {
		final String name = super.getName(draftName);
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Mapping long name '%s' to '%s'", draftName, name));
		}
		
		return name;
	}
}