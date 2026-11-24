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

import com.crh.logon.admin.web.forms.truckadmin.TruckAdminForm;
import com.crh.logon.admin.web.utils.TruckAdminChangeLogic;
import com.crh.logon.persistence.entities.common.TruckEntity;

/**
 * Used to determine whether functionality is available for a given a given user, and circumstances.
 * @author AdNovum Informatik AG
 */
public class TruckAdminChangeTag extends TagSupport { 
	
	private static final String C_NAME = 
		TruckAdminChangeTag.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);

	/**
	 * The context key for getting the value for {@link #lpObject}
	 */
	private String truckKey = null;
	
	private TruckEntity truck = null;
	
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
			TruckAdminForm form = (TruckAdminForm) pageContext.findAttribute(formKey);
			if (form == null) {
				return result = SKIP_BODY;
			}
			else{				
				return result = TruckAdminChangeLogic.disableChangeFunctionality(form, functionality) ?
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

	public String getTruckKey() {
		return truckKey;
	}

	public void setTruckKey(String truckKey) {
		this.truckKey = truckKey;
	}

	public TruckEntity getTruck() {
		return truck;
	}

	public void setTruck(TruckEntity truck) {
		this.truck = truck;
	}

	public String getFormKey() {
		return formKey;
	}

	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}


}
