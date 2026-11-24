/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.dispo;

import com.crh.logon.business.services.utils.TimeZone;
import org.apache.struts.action.ActionForm;

import java.text.ParseException;
import java.util.Date;

/**
 * @author AdNovum Informatik AG
 */
public abstract class AbstractDispoForm extends ActionForm {

	protected Date date;
	protected TimeZone userTimeZone;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getStringDate() {
		return userTimeZone.fmtAsDayMonthYear4(date);
	}

	public void setStringDate(String text) throws ParseException {
		setDate(userTimeZone.parseDayMonthYear4(text));
	}

	public void setUserTimeZone(TimeZone userTimeZone) {
		this.userTimeZone = userTimeZone;
	}
}
