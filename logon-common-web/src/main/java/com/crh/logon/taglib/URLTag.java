/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.taglib;

import javax.servlet.jsp.JspException;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonProperties;
import com.crh.logon.web.utils.ConfigBeanProvider;

/**
 * @author AdNovum Informatik AG
 */
public class URLTag extends org.apache.taglibs.standard.tag.el.core.UrlTag {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(URLTag.class);
	private static final String VERSION_FILENAME_LABEL;
	private static final String VERSION_QUERY_LABEL;
	static {
		ConfigBean cb = ConfigBeanProvider.getConfigBean();
		LogonProperties logonProperties = cb.getLogonProperties();
		VERSION_FILENAME_LABEL = "_v" + logonProperties.getBuildVersion();
		VERSION_QUERY_LABEL = "logonversion=" + logonProperties.getBuildVersion();
	}
	private static final String TYPE_QUERYSTRING = "query";
	private static final String TYPE_FILENAME = "filename";
	private String _value;
	private String type;
	
	public URLTag() {
		
		init();
	}
	
	private void init() {
		
		_value = null;
		type = null;
	}
	
	private String insertInFilename(String originalUrl) {
		
		String versionedUrl = originalUrl;
		int fileNameLoc = originalUrl.lastIndexOf('/');
		if (fileNameLoc < 0) {
			fileNameLoc = 0;
		}
		int insertPoint = -1;
		int queryStringSep = originalUrl.indexOf('?', fileNameLoc);
		int extensionSep = originalUrl.indexOf('.', fileNameLoc);
		if ( ((queryStringSep >= 0) && (extensionSep >= 0) && (extensionSep < queryStringSep)) ||
			((queryStringSep < 0) && (extensionSep >= 0)) ) {
			insertPoint = extensionSep;
		}
		else if ((queryStringSep >= 0) && (extensionSep < 0)) {
			insertPoint = queryStringSep;
		}
		else if ((extensionSep < 0) && (queryStringSep < 0)) {
			insertPoint = originalUrl.length();
		}
		else {
			LOG.error("insertpoint could not be determined for " + originalUrl);
		}
		if (insertPoint > 0) {
			versionedUrl = originalUrl.substring(0, insertPoint) + VERSION_FILENAME_LABEL;
			if (insertPoint < originalUrl.length()-1) {
				versionedUrl += originalUrl.substring(insertPoint);
			}
		}
		
		return versionedUrl;
	}
	
	private String insertInQueryString(String originalUrl) {
		// [[
		String result = null;
		Object[][] params = {{"originalUrl", originalUrl}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "insertInQueryString", params);
		}
		// ]]
		try {
			
			String versionedUrl = originalUrl;
			String insertString = null;
			if (originalUrl.endsWith("/")) {
				LOG.error("cannot insert query string in path");
			}
			else {
				int queryStringSep = originalUrl.indexOf('?');
				if (queryStringSep >= 0) {
					insertString = "&" + VERSION_QUERY_LABEL;
				}
				else {
					insertString = "?" + VERSION_QUERY_LABEL;
				}
				versionedUrl += insertString;
			}
			
			return result = versionedUrl;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public int doStartTag() throws JspException {
		// [[
		int result = 0;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doStartTag");
		}
		// ]]
		try {
			String versionedUrl;
			if ( (type != null) && (type.equals(TYPE_FILENAME)) ) {
				versionedUrl = insertInFilename(_value);
			}
			else {
				versionedUrl = insertInQueryString(_value);
			}
			super.setValue(versionedUrl);
			
			return result = super.doStartTag();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public void release() {
		
		super.release();
		init();
	}

	public void setValue(String value) {
		this._value = value;
	}

	public void setType(String type) {
		this.type = type;
	}
}
