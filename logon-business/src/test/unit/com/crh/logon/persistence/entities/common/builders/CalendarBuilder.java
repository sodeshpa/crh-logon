/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.entities.common.builders;

import java.util.Calendar;
import java.util.Date;

public class CalendarBuilder {

	private Calendar cal;

	public CalendarBuilder() {
		this.cal = Calendar.getInstance();
		this.cal.setTimeInMillis(0); // to get the same value for the unset fields
	}

	public CalendarBuilder set(int field, int value) {
		cal.set(field, value);

		return this;
	}

	public CalendarBuilder year(int value) {
		cal.set(Calendar.YEAR, value);
		return this;
	}

	public CalendarBuilder month(int value) {
		cal.set(Calendar.MONTH, value);
		return this;
	}

	public CalendarBuilder day(int value) {
		cal.set(Calendar.DAY_OF_MONTH, value);
		return this;
	}

	public CalendarBuilder hour(int value) {
		cal.set(Calendar.HOUR_OF_DAY, value);
		return this;
	}

	public CalendarBuilder minute(int value) {
		cal.set(Calendar.MINUTE, value);
		return this;
	}

	public CalendarBuilder second(int value) {
		cal.set(Calendar.SECOND, value);
		return this;
	}

	public CalendarBuilder ms(int value) {
		cal.set(Calendar.MILLISECOND, value);
		return this;
	}

	public Calendar build() {
		return cal;
	}

	public Date buildDate() {
		return cal.getTime();
	}

	public void reset() {
		this.cal = Calendar.getInstance();
	}
}
