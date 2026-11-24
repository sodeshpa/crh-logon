/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.util.Calendar;
import java.util.Date;

import com.crh.logon.utils.time.DateTimeUtils;

/**
 * Represents a from-to time interval. Represented using {@link Calendar} instances.
 * @author AdNovum Informatik AG
 */
public class Interval {

	private Calendar from;
	private Calendar to;

	public Interval(Calendar from, Calendar to) {
		this.from = from;
		this.to = to;
	}
	
	public Interval(Date from, Date to) {
		
		Calendar fromCal = Calendar.getInstance();
		Calendar toCal = Calendar.getInstance();
		
		fromCal.setTime(from);
		toCal.setTime(to);
		
		this.from = fromCal;
		this.to = toCal;
	}

	/**
	 * @return the from
	 */
	public Calendar getFrom() {
		return from;
	}

	/**
	 * @return the to
	 */
	public Calendar getTo() {
		return to;
	}

	/**
	 * Checks if the passed interval is inside the instance in a inclusive->exclusive manner: (...]
	 * <b>Please note this method only compares time!</b>
	 * @return true if the outerntInterval is inside the instance internval
	 */
	public boolean inside(Interval outerntInterval) {
		
		Calendar from = DateTimeUtils.getTimePart(this.from);
		Calendar pFrom = DateTimeUtils.getTimePart(outerntInterval.getFrom());
		
		Calendar to = DateTimeUtils.getTimePart(this.to);
		Calendar pTo = DateTimeUtils.getTimePart(outerntInterval.getTo());
		
		return (from.compareTo(pFrom) > 0 || from.equals(pFrom))
				&& to.compareTo(pTo) < 0;
	}
	
	/**
	 * Substracts the innerInterval from the instance interval.
	 * @return the residual before and the residual after intervals
	 */
	public Interval[] substract(Interval innerInterval) {

		if (!innerInterval.inside(this)) {
			throw new RuntimeException("Passed interval [" + innerInterval
					+ "] is not inside this interval [" + this + "]");
		}

		Interval beforeInterval = new Interval(from, innerInterval.from);
		Interval afterInterval = new Interval(innerInterval.to, to);

		return new Interval[] { beforeInterval, afterInterval };
	}

	/**
	 * Returns true if the instance interval is shorter than the given interval. Returns false if it is larger or equal. 
	 */
	public boolean shorterThan(Interval interval) {
		
		Calendar to = DateTimeUtils.getTimePart(this.to);
		Calendar pTo = DateTimeUtils.getTimePart(interval.to);
		
		Calendar from = DateTimeUtils.getTimePart(this.from);
		Calendar pFrom = DateTimeUtils.getTimePart(interval.from);

		long a = to.getTimeInMillis() - from.getTimeInMillis();
		long b = pTo.getTimeInMillis() - pFrom.getTimeInMillis();;
		
		return a < b;

	}

	/**
	 * Returns true if the instance interval starts before the given interval
	 */
	public boolean startsBefore(Calendar calendar) {
		
		Calendar from = DateTimeUtils.getTimePart(this.from);
		Calendar pCalendar = DateTimeUtils.getTimePart(calendar);
		
		return from.getTimeInMillis() < pCalendar.getTimeInMillis();
	}
	
	/**
	 * Returns true if the instance interval finishes before the given interval
	 */
	public boolean finishesBefore(Calendar calendar) {
		
		Calendar to = DateTimeUtils.getTimePart(this.to);
		Calendar pCalendar = DateTimeUtils.getTimePart(calendar);
		
		return to.getTimeInMillis() < pCalendar.getTimeInMillis();
	}
	
	/**
	 * Returns true if the instance interval finishes before the given interval
	 */
	boolean finishesAfter(Calendar calendar) {
		
		Calendar to = DateTimeUtils.getTimePart(this.to);
		Calendar pCalendar = DateTimeUtils.getTimePart(calendar);
		
		return to.getTimeInMillis() > pCalendar.getTimeInMillis();
	}
	
	/**
	 * Returns true if the instance interval starts after the given interval
	 */
	boolean startsAfter(Calendar interval) {
		
		Calendar from = DateTimeUtils.getTimePart(this.from);
		Calendar pCalendar = DateTimeUtils.getTimePart(interval);
		
		return from.getTimeInMillis() > pCalendar.getTimeInMillis(); 
	}
	
	/**
	 * Returns true if 
	 * the instance from is before given interval's from 
	 * and 
	 * the instance to is before given interval's to
	 * and
	 * the instance to is after given interval's from   
	 */
	public boolean intersectsBefore(Interval interval) {
		return (this.startsBefore(interval.from) || interval.from.equals(this.from)) && this.finishesBefore(interval.to) && this.finishesAfter(interval.from);
	}
	
	/**
	 * Returns true if 
	 * the instance from is after given interval's from 
	 * and 
	 * the instance to is after given interval's to
	 * and
	 * the instance from is before the given interval's to   
	 */
	public boolean intersectsAfter(Interval interval) {
		return this.startsAfter(interval.from) && this.finishesAfter(interval.to) && this.startsBefore(interval.to);
	}
	
	/**
	 * Returns true if the the given calendar is between the interval in a ( .. ] manner.
	 *  
	 */
	public boolean contains(Calendar calendar) {
		if (finishesAfter(calendar)) {
			Calendar from = DateTimeUtils.getTimePart(this.from);
			Calendar pCalendar = DateTimeUtils.getTimePart(calendar);
			return from.getTimeInMillis() <= pCalendar.getTimeInMillis();
		}
		return false;
	}
	

	/**
	 * Returns the lenght of the interval in milliseconds.
	 */
	public long lengthInMillis() {
		return to.getTimeInMillis() - from.getTimeInMillis();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interval other = (Interval) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		}
		else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		}
		else if (!to.equals(other.to))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Interval ["+AdjustTimesUtils.formatForError(from, to)+"]";
	}

	/**
	 * Calculate the number of weekend days between two date.
	 * 
	 * @return
	 */
	public int getCountOfWeekendDaysIncluded() {
		Calendar actualDay = (Calendar) from.clone();
		int countOfWeekendDaysIncluded = 0;
		while (!actualDay.after(to)) {
			int nameOfActualDay = actualDay.get(Calendar.DAY_OF_WEEK);
			if (nameOfActualDay == Calendar.SUNDAY
					|| nameOfActualDay == Calendar.SATURDAY) {
				++countOfWeekendDaysIncluded;
			}
			actualDay.add(Calendar.DAY_OF_MONTH, 1);
		}

		return countOfWeekendDaysIncluded;
	}

}