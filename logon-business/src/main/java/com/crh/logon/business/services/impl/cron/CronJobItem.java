/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.services.impl.cron;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.ejb.ScheduleExpression;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.Reason;

public class CronJobItem implements Serializable{

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CronJobService.class);
	
	private final Boolean JOB_STARTED = Boolean.TRUE;
	private final Boolean JOB_STOPPED = Boolean.FALSE;
	
	private static final long serialVersionUID = 1L;
	private String sessionBeanClassName;
	private String localInterfaceName;
	private String methodName;
	private Boolean enabled;
	private Boolean schedule;
	private String cronJobName;

	private String year;
	private String month;
	private String dayOfWeek;
	private String dayOfMonth;
	private String hour;
	private String minute;
	private String second;
	
	private String configBeanMethodName;
	
	private Boolean running;
	
	@SuppressWarnings("rawtypes")
	public CronJobItem(Class sessionBeanClass, Class localInterface, Method method, ScheduledTask cronJobAnnotation) {
		this.sessionBeanClassName = sessionBeanClass.getSimpleName();
		this.localInterfaceName = localInterface.getName();
		this.methodName = method.getName();
		this.enabled = !cronJobAnnotation.ignore();
		this.year = cronJobAnnotation.year();
		this.month = cronJobAnnotation.month();
		this.dayOfMonth = cronJobAnnotation.dayOfMonth();
		this.dayOfWeek = cronJobAnnotation.dayOfWeek();
		this.hour = cronJobAnnotation.hour();
		this.minute = cronJobAnnotation.minute();
		this.second = cronJobAnnotation.second();
		this.configBeanMethodName = cronJobAnnotation.configurationMethodName();
		this.cronJobName = cronJobAnnotation.name();
		this.schedule = cronJobAnnotation.schedule();
		this.running = Boolean.FALSE;
	}
	
	public String getSessionBeanClassName() {
		return sessionBeanClassName;
	}
	public void setSessionBeanClassName(String sessionBeanClassName) {
		this.sessionBeanClassName = sessionBeanClassName;
	}
	public String getLocalInterfaceName() {
		return localInterfaceName;
	}
	public void setLocalInterfaceName(String localInterfaceName) {
		this.localInterfaceName = localInterfaceName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public ScheduleExpression getScheduledExpression() {
		ScheduleExpression expression = new ScheduleExpression()
											.year(year)
											.month(month)
											.dayOfMonth(dayOfMonth)
											.dayOfWeek(dayOfWeek)
											.hour(hour)
											.minute(minute)
											.second(second);
		return expression;
	}	
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}

	public String getConfigBeanMethodName() {
		return configBeanMethodName;
	}

	public void setConfigBeanMethodName(String configBeanMethodName) {
		this.configBeanMethodName = configBeanMethodName;
	}

	public String getCronJobName() {
		return cronJobName;
	}

	public void setCronJobName(String cronJobName) {
		this.cronJobName = cronJobName;
	}

	public Boolean isSchedule() {
		return schedule;
	}

	public void setSchedule(Boolean schedule) {
		this.schedule = schedule;
	}
	
	public Boolean isRunning() {
		return running;
	}

	/**
	 * Sets the cronjobItem as {@link #JOB_STARTED}. 
	 * Assumption is that it should not be set as started if it is already running. 
	 * @throws CronJobException
	 */
	public void startJob() throws CronJobException {
		if (this.running.equals(JOB_STARTED) ) {
			throw new CronJobException("The cronjob " + this.cronJobName + " is already running, any attempt to start it while running should fail.", Reason.RES_CRON_ALREADY_RUNNING);
		}
		LOG.info("STARTED '" + this.cronJobName + "' cron job.");
		this.running = JOB_STARTED;
	}
	
	/**
	 * Sets the cronjobItem as {@link #JOB_STOPPED}.  
	 */
	public void endJob() {
		LOG.info("STOPPED '" + this.cronJobName + "' cron job. Status before was " + (this.running.equals(JOB_STARTED) ? "'started'" : "'stopped'") );
		this.running = JOB_STOPPED;
	}
}
