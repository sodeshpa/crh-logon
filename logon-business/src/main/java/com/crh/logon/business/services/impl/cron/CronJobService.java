/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.services.impl.cron;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.NamingException;

import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.CronJobServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LocalServices;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.FtpClientFactory;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;


@Lock(LockType.READ)
@Singleton
@Startup
@AdnTrace
public class CronJobService implements CronJobServiceLocalInterface {
	
	
	private static final boolean NOT_PERSISTENT = false;

	@Resource
	private TimerService timerService;
	  
	private static final String CLASS_WORD = ".class";
	private static final String BEAN_WORD = "Bean";
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CronJobService.class);
	private Map<String, CronJobItem> cronJobs;
	@EJB
	ConfigServiceLocalInterface configService;
	
	/**
	 * It collects the @ScheduledTask annotated methods during the startup 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostConstruct
	public void collectCronJobs() throws ClassNotFoundException, IOException {
		cronJobs = new HashMap<String, CronJobItem>();
		Iterable<Class> classes = getClasses("com.crh.logon.business");
		for (Class currentClass : classes) {
			String className = currentClass.getSimpleName();
			boolean isServiceBean = className.endsWith(BEAN_WORD);
			if (isServiceBean) {
				for (Method currentMethod : currentClass.getMethods()) {
					if (currentMethod.isAnnotationPresent(ScheduledTask.class)) {
						LOG.info(currentClass.getSimpleName() + "." + currentMethod.getName()
								+ " is annotated with " + ScheduledTask.class.getSimpleName());
						if (!currentMethod.isAnnotationPresent(Asynchronous.class)) {
							Class[] interfaces = currentClass.getInterfaces();
							ScheduledTask cronJobAnnotation = currentMethod.getAnnotation(ScheduledTask.class);
							String cronJobAnnotationName = cronJobAnnotation.name();

							for (Class currentInterface : interfaces) {
								if (currentInterface.isAnnotationPresent(Local.class)
										&& !cronJobs.containsKey(cronJobAnnotationName)) {
									CronJobItem cronJobItem = new CronJobItem(currentClass,
											currentInterface, currentMethod, cronJobAnnotation);
									cronJobs.put(cronJobAnnotationName, cronJobItem);

									TimerConfig timerConfiguration = new TimerConfig(cronJobItem,
											NOT_PERSISTENT);
									timerService.createCalendarTimer(
											cronJobItem.getScheduledExpression(),
											timerConfiguration);
								}
							}
						}
						else {
							LOG.warn(currentClass.getName() + "." + currentMethod.getName()
									+ ": methods with ScheduledTask annotation must NOT be annotated with @Asynchronous");
						}
					}
				}
			}
		}
		LOG.info(this.getClass().getSimpleName() + " started as a Singleton");
	}

	@Timeout
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void timerHandler(Timer timer) {
		CronJobItem cronJob = (CronJobItem) timer.getInfo();
		String cronJobName = cronJob.getCronJobName();
		String cronEnabledMethod = cronJob.getConfigBeanMethodName();
		if (cronJob.isSchedule()) {
			LOG.info("ScheduledTask called: " + cronJobName + " with " + cronEnabledMethod);
			if (configService.isInitialized()) {
				ConfigBean configuration = configService.getConfigBean();

				if (!configuration.getAppScheduledCronjobsEnabled()) {
					LOG.info("Scheduled jobs are disabled. Skipping " + cronJobName);
					return;
				}
				if ("".equals(cronEnabledMethod)) {
					callCronJob(cronJobName);
				}
				else {
					Boolean isCronEnabled = invokeMethod(configuration, cronEnabledMethod);
					LOG.debug("ScheduledTask called: " + cronJobName + " with " + cronEnabledMethod
							+ " isEnabled: " + isCronEnabled);
					if (isCronEnabled) {
						callCronJob(cronJobName);
					}
				}
			}
		}
	}
	
	
	private Boolean invokeMethod(ConfigBean configuration, String cronEnabledMethod) {
		try {
			@SuppressWarnings("rawtypes")
			Class[] emptyParameters = null;
			Method configMethod = ConfigBean.class.getDeclaredMethod(cronEnabledMethod, emptyParameters);
			configMethod.setAccessible(true);		
			Boolean value = (Boolean) configMethod.invoke(configuration);
			configMethod.setAccessible(false);
			return value;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private Iterable<Class> getClasses(String packageName) throws ClassNotFoundException,
			IOException {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = packageName.replace('.', '/');
		LOG.info("CronJobService Singleton is searching for " + path);
		Enumeration<URL> resources = classLoader.getResources(path);
		List<VirtualFile> dirs = new ArrayList<VirtualFile>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			VirtualFile virtualFile;
			try {
				virtualFile = VFS.getChild(resource.toURI());
				dirs.add(virtualFile);
			}
			catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<Class> classes = new ArrayList<Class>();
		for (VirtualFile directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}

		return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private List<Class> findClasses(VirtualFile directory, String packageName)
			throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		List<VirtualFile> files = directory.getChildren();
		for (VirtualFile file : files) {
			if (file.isDirectory()) {
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			}
			else if (file.getName().endsWith(CLASS_WORD)) {
				classes.add(Class.forName(packageName
						+ '.'
						+ file.getName().substring(0, file.getName().length() - CLASS_WORD.length())));
			}
		}
		return classes;
	}
	
	/**
	 * It returns with the cronJobs list
	 * 
	 * @return List<CronJobItem>
	 */
	public List<CronJobItem> getCronJobs() {
		return new ArrayList<CronJobItem>(cronJobs.values());
	}
	

	/**
	 * It returns with the names of the cronjobs
	 * @return List<String>
	 */
	public List<String> getCronJobNames() {
		List<String> cronJobNames = new ArrayList<String>(cronJobs.keySet());
		return cronJobNames;
	}
	
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void callCronJob(String cronJobName) {
		if (cronJobs.containsKey(cronJobName)) {
			CronJobItem cronJob = cronJobs.get(cronJobName);
			if (cronJob.isEnabled()) {
				
				try {
					cronJob.startJob();
				}
				catch (CronJobException e) {
					LOG.info("The Scheduled Job '" + cronJobName + "' is already running. Skip starting job.");
					return;
				}
				
				try {
					invoke(cronJob);					
				}
				catch (Exception e) {
					String errorMsg = "Exception while running '" + cronJobName + "' Scheduled Job";
					LOG.error(errorMsg, e);
					throw new LogonException(errorMsg, e); 
				}
				finally {
					cronJob.endJob();					
				}
			}
			else {
				throw new LogonException("Invalid call on a disabled scheduled task. It's id is : " + cronJobName, Reason.RES_IMPL_BUG);
			}
		} 
		else {
			throw new LogonException("Invalid scheduled task id : " + cronJobName, Reason.RES_IMPL_BUG);
		}
		
	}

	@SuppressWarnings("rawtypes")
	private void invoke(CronJobItem cronJob) {
		try {
			String sessionBeanName = cronJob.getSessionBeanClassName();
			String localInterfaceName = cronJob.getLocalInterfaceName();
			String annotatedMethodName = cronJob.getMethodName();
			Object localInterfaceInstance = LocalServices.getLocalInterfaceFor(sessionBeanName, localInterfaceName);
			Class[] emptyParameters = null;
			Method method = localInterfaceInstance.getClass().getDeclaredMethod(annotatedMethodName, emptyParameters);
			method.setAccessible(true);		
			method.invoke(localInterfaceInstance);
			method.setAccessible(false);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	public void preDestroy() {
		FtpClientFactory.shutdown();
	}
	
}
