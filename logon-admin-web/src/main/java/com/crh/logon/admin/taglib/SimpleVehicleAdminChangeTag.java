/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.simplevehicleadmin.SimpleVehicleAdminForm;
import com.crh.logon.admin.web.utils.SimpleVehicleAdminChangeLogic;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;

/**
 * Used to determine whether functionality is available for a given a given user, and circumstances.
 * @author AdNovum Informatik AG
 */
public class SimpleVehicleAdminChangeTag extends TagSupport { 
	
	private static final String C_NAME = 
		SimpleVehicleAdminChangeTag.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);

	/**
	 * The context key for getting the value for {@link #lpObject}
	 */
	private String simpleVehicleKey = null;
	
	private SimpleVehicleEntity simpleVehicle = null;
	
	private String formKey = null;
	
	private int functionality = -1;

	public int getFunctionality() {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("getFunctionality(): '" + functionality + "'");
		}
		// ]]
		return functionality;
	}
	
	public void setFunctionality(int functionality) {
		// [[
		if (LOG.isDebugEnabled()) {
			LOG.debug("setFunctionality(): '" + functionality + "'");
		}
		// ]]
		this.functionality = functionality;
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
			SimpleVehicleAdminForm form = (SimpleVehicleAdminForm) pageContext.findAttribute(formKey);
			if (form == null) {
				return result = SKIP_BODY;
			}
			else{				
				return result = SimpleVehicleAdminChangeLogic.disableChangeFunctionality(form, functionality) ?
						EVAL_BODY_INCLUDE : SKIP_BODY ;
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public String getSimpleVehicleKey() {
		return simpleVehicleKey;
	}

	public void setSimpleVehicleKey(String simpleVehicleKey) {
		this.simpleVehicleKey = simpleVehicleKey;
	}

	public SimpleVehicleEntity getSimpleVehicle() {
		return simpleVehicle;
	}

	public void setSimpleVehicle(SimpleVehicleEntity simpleVehicle) {
		this.simpleVehicle = simpleVehicle;
	}

	public String getFormKey() {
		return formKey;
	}

	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}


}
