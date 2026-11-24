package com.crh.logon.business.services.localinterfaces;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.ejb.Local;
import javax.naming.NamingException;

import com.crh.logon.business.services.impl.cron.CronJobItem;

@Local
public interface CronJobServiceLocalInterface {
	
	/**
	 * It returns with the cronJobs list
	 * 
	 * @return List<CronJobItem>
	 */
	public List<CronJobItem> getCronJobs();
	
	/**
	 * It returns with the names of the cronjobs
	 * @return List<String>
	 */
	public List<String> getCronJobNames();
	
	/**
	 * It invokes a cronJob based on the cronJobName which has to be unique
	 * 
	 * @param cronJobName
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NamingException
	 */
	public void callCronJob(String cronJobName);
	
}
