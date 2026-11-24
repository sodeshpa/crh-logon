/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.WagonNumberUtils;

/**
 * Purpose: printing out the entity's wagonNo in the standard way,
 * or print out the String in the wagon number displaying standard way.
 * @author AdNovum Informatik AG
 */
public class WagonNumberFormatTag extends TagSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WagonNumberFormatTag.class);
	private static final String C_NAME = 
		WagonNumberFormatTag.class.getName();

	private String wagonNumber;
	private WagonEntity wagon;


	public int doStartTag() throws JspException {
		String wagonNo = "";
		if (wagon != null) {
			 wagonNo = wagon.getWagonNo();
		}
		else if (!StringUtils.empty(wagonNumber) ){
			wagonNo = wagonNumber;
		}
		try {
			pageContext.getOut().write(WagonNumberUtils.toDisplayNo(wagonNo));
		}
		catch (IOException e) {
			LOG.error("Error when trying to display formatted wagon number", e);
		}
		return SKIP_BODY;
	}


	public String getWagonNumber() {
		return wagonNumber;
	}


	public void setWagonNumber(String wagonNumber) {
		this.wagonNumber = wagonNumber;
	}


	public WagonEntity getWagon() {
		return wagon;
	}


	public void setWagon(WagonEntity wagon) {
		this.wagon = wagon;
	}
	


}
