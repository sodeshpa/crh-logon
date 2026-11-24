/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.taglib;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.InvalidMapSupport;
import com.crh.logon.constants.ShiptoSalesAreaError;

/**
 * @author AdNovum Informatik AG
 */
public class DispokarteInvalidAttributeTrTag extends DispokarteInvalidAttributeMarkerTag {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispokarteInvalidAttributeTrTag.class);
	
	private String clazz;
	
	public int doStartTag() throws JspException {
		// [[
		int result = 0;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "doStartTag");
		}
		// ]]
		try {
			Map<Object, List<ShiptoSalesAreaError>> invalidMap = null;
			invalidMap = initInvalidMap();
			
			if(invalidMap == null) {
				return result = EVAL_BODY_INCLUDE;
			}
			
			
			//all type of errors must cause to mark the object invalid on screen
			invalid = InvalidMapSupport.containsError(invalidMap, attributeObject);

			if (invalid) {
				clazz = "alerted";
			}
			
			try {
				pageContext.getOut().write("<tr id=\"" + id + "\" class=\"restrictionRow " + clazz + "\">");
			}
			catch (IOException e) {
				LOG.error("Error when printing <tr> for dispokarte table attribute");
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
	
	@Override
	public int doEndTag() {
		try {
			pageContext.getOut().write("</tr>");
		}
		catch (IOException e) {
			LOG.error("Error when printing </tr> for dispokarte table attribute");
		}
		return EVAL_PAGE;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

}
