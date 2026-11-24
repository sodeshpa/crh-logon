/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.validators;
import java.util.ArrayList;
import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class ValidatorMessage {
	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(ValidatorMessage.class);
    private String resourceKey;
    private List additionalInfoList;
    
    public ValidatorMessage() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "ValidatorMessage");
		}
		// ]]
		try {
	        additionalInfoList = new ArrayList();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    public List getAdditionalInfoList() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getAdditionalInfoList(): '" + additionalInfoList + "'");
		}
		// ]]
		return additionalInfoList;
    }

    public void setAdditionalInfoList(List additionalInfoList) {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("setAdditionalInfoList(): '" + additionalInfoList + "'");
		}
		// ]]
		this.additionalInfoList = additionalInfoList;
    }
    
    public void addAdditionalInfo(String info) {
		// [[
		Object[][] params = {{"info", info}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addAdditionalInfo", params);
		}
		// ]]
		try {
            additionalInfoList.add(info);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    public String getResourceKey() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getResourceKey(): '" + resourceKey + "'");
		}
		// ]]
		return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("setResourceKey(): '" + resourceKey + "'");
		}
		// ]]
		this.resourceKey = resourceKey;
    } 
}
