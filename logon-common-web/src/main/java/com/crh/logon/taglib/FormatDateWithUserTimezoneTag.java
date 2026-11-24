/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.taglib;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.web.utils.UserProvider;

/**
 * @author AdNovum Informatik AG
 */
public class FormatDateWithUserTimezoneTag extends TagSupport{
	private static final long serialVersionUID = 8053980961340214494L;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(FormatDateWithUserTimezoneTag.class);
	
	private Date date;
	
	public int doStartTag() throws JspException {
		TimeZone timeZone = UserProvider.getTimezone();
		
		try {
			pageContext.getOut().write(timeZone.fmtAsDayMonthYearHourMinute(date));
		}
		catch (IOException e) {
			LOG.error("Error when trying to display date with user timezone", e);
		}
		return SKIP_BODY;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
}
