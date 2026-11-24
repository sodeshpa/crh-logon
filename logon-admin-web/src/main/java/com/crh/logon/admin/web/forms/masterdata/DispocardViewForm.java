/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.masterdata;

import java.util.Map;

import org.apache.struts.action.ActionForm;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_DispocardViewForm"
 */
public class DispocardViewForm extends ActionForm {
	private static final long serialVersionUID = 42L;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispocardViewForm.class);
    
	public static final String SESSION_DPVIEWFORM_NAME = "admin_DispocardViewForm";
	
	private Map json;
	
	public Map getJson() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getJson(): '" + json + "'");
		}
		// ]]
		return json;
	}

	public void setJson(Map json) {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("setJson(): '" + json + "'");
		}
		// ]]
		this.json = json;
	}
}
