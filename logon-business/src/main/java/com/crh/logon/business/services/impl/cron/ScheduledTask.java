/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.services.impl.cron;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/** 
 * Schedule a timer for automatic creation with a timeout schedule 
 * based on a cron-like time expression. The annotated method is used 
 * as the timeout callback method. 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScheduledTask{
	/**
	 * It's the cronjob's name which must be unique 
	 * 
	 * [mandatory]
	 */
	String 	name();
	
	/**
	 * It defines that a cronjob is ignored (not active - false) or active (true). If it is active then it's invokable.
	 * 
	 * [optional]
	 */
	boolean ignore() default false;
	
	/**
	 * It defines that a cronjob is scheduled and the scheduler has to call it or not 
	 * 
	 * [optional]
	 */
	boolean schedule() default true;
	
	/**
	 * a particular calendar year
	 * Allowable values : a four-digit calendar year 
	 * 
	 * [optional]
	 */
    String year() default "*";
    
    /**
     * one or more months within a year
	 * Allowable values :
	 *     [1,12]
	 *     {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", Dec"}
	 * 
	 * [optional]
     */
    String month()  default "*";
	
    /**
     * one or more days within a week
     * Allowable values :
     *    [0,7]
     *    {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}
     *    
     * [optional]
     */    
    String dayOfWeek()  default "*";
	
	/**
	 *     [1,12]
	 *
	 * one or more days within a month
	 * Allowable values:
	 *    [1,31]
     *	  [-7, -1]
     *	  "Last"
     * 	  {"1st", "2nd", "3rd", "4th", "5th", "Last"} {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"} 
	 *
	 * "Last" means the last day of the month
	 *
	 * -x (where x is in the range [-7, -1]) means x day(s) before the last day of the month
	 * "1st","2nd", etc. applied to a day of the week identifies a single occurrence of that day within the month. 
	 * 
	 * [optional]
	 */
	String dayOfMonth()  default "*";
	
	/**
	 * one or more hours within a day
	 * Allowable values : [0,23]
	 * 
	 * [optional]
	 */
	String hour()  default "*";
	
	/**
	 * one or more minutes within an hour
	 * Allowable values : [0,59]
	 * 
	 * [optional] 	
	 */
	String minute()  default "*";
	
	/**
	 *  One or more seconds within a minute
	 *	Allowable values: [0,59]
	 *
	 *  [optional]
	 */
	String second()  default "*";
	
	/**
	 * It's the name of the configuration method inside the ConfigBean (getXY) which has to return with a boolean.
	 * If it is true then the scheduled task is callable otherwise it will be not called during the run of the scheduled job. 
	 * 
	 * [optional]
	 */
	String configurationMethodName() default "";
}
