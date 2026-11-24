/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.taglib;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.InvalidMapSupport;
import com.crh.logon.order.web.forms.bestellung.BestellungForm;
import com.crh.logon.order.web.forms.dispokarte.DispokarteForm;
import com.crh.logon.constants.ShiptoSalesAreaError;

/**
 * Used to determine whether functionality is available for a given order.
 * @author AdNovum Informatik AG
 */
public class DispokarteInvalidAttributeMarkerTag extends TagSupport { 
	
	private static final long serialVersionUID = 1L;

	public static final int DISPOKARTE = 0;
	public static final int BESTELLUNG = 1;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispokarteInvalidAttributeMarkerTag.class);

	protected Object attributeObject;
	private ShiptoSalesAreaError checkForErrorCode;
	private int screen;
	
	protected boolean invalid = false;
	
	public int doStartTag() throws JspException {
		// [[
		int result = 0;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doStartTag");
		}
		// ]]
		try {
			Map invalidMap = null;
			invalidMap = initInvalidMap();
			
			if(invalidMap == null) {
				return result = EVAL_BODY_INCLUDE;
			}
			
			if (checkForErrorCode != null) {
				invalid = InvalidMapSupport.containsError(invalidMap, attributeObject, checkForErrorCode);
			}
			else {
				//all type of errors must cause to mark the object invalid on screen
				invalid = InvalidMapSupport.containsError(invalidMap, attributeObject);
			}
			
			if (invalid) {
				printDelStart();
			}
						
			return result = EVAL_BODY_INCLUDE;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	protected Map initInvalidMap() {
		int result;
		Map invalidMap = null;
		switch (screen){
			case DISPOKARTE:{
				DispokarteForm form = (DispokarteForm) pageContext.getSession().getAttribute("order_dispokarte_DispokarteForm");
				if (form == null){			
					return null;
				}
				invalidMap = form.getInvalidObjectInformationMap();
				break;
			}
			case BESTELLUNG:{
				BestellungForm form = (BestellungForm) pageContext.getSession().getAttribute("order_bestellung_BestellungForm");
				if (form == null){			
					return null;
				}
				invalidMap = form.getInvalidMap();
				break;
			}
			default:{
				LOG.error("error");
			}
		}
		return invalidMap;
	}
	
	public int doEndTag(){
		int i = EVAL_PAGE;
		if (invalid) {
			printDelEnd();
		}
		return i;
	}

	private void printDelStart() {
		try {
			pageContext.getOut().write("<del>");
		}
		catch (IOException e) {
			LOG.error("Error when printing del for invalid attribute");
		}
	}

	private void printDelEnd() {
		try {
			pageContext.getOut().write("</del>");
		}
		catch (IOException e) {
			LOG.error("Error when printing del for invalid attribute");
		}
	}
	public Object getAttributeObject() {
		return attributeObject;
	}

	public void setAttributeObject(Object attributeObject) {
		this.attributeObject = attributeObject;
	}
	

	public ShiptoSalesAreaError getCheckForErrorCode() {
		return checkForErrorCode;
	}

	public void setCheckForErrorCode(ShiptoSalesAreaError checkForErrorCode) {
		this.checkForErrorCode = checkForErrorCode;
	}

	public void setScreen(int screen) {
		this.screen = screen;
	}


}
