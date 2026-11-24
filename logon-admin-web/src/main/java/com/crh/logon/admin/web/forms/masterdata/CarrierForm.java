/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.forms.masterdata;

import org.apache.struts.action.ActionForm;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="admin_CarrierForm"
 */
public class CarrierForm extends ActionForm {
	private static final long serialVersionUID = 42L;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CarrierForm.class);
    
	public static final String SESSION_CARRIERFORM_NAME = "admin_CarrierForm";

}
