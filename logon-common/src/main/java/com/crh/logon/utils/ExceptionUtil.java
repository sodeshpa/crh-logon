/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import ch.nevis.jcan.log.JcanLogger;

/**
 * NOTE: taken from jcan log. this class is not yet public, but traces so much
 * useful information on exception that I don't want to wait.
 * -> remove once the ExceptionUtil from jcan-log has been released
 *
 * @author AdNovum Informatik AG
 */
@SuppressWarnings("unchecked")
public class ExceptionUtil {
	public static final String PROP_FULL_PROPERTIES_TRC = "fullPropertyTracing";
	// NAMESPACE is used to identify which parts of the stacktrace come from our code.
	public static final String NAMESPACE = "com.crh.logon";
	
	private static String version = "undef";

	private static final String[] interestingProperties = {
		 "java.vendor",
		 "java.vm.version",
		 "os.name",
		 "os.version",
		 "file.encoding",
		 "user.name",
		 "user.dir"
	};

	private static HashMap interestingPropertiesMap;

	static {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream in = loader.getResourceAsStream ("buildtime.properties");
		Properties props = new Properties();
		try {
			if (in != null) {
				props.load(in);
				if (props.containsKey("build.version")) {
					version = props.getProperty("build.version");
				}
				else {
					version = "undef (not defined in buildtime.properties)";
				}
			}
			else {
				version = "undef (no buildtime.properties in classpath)";
			}
		}
		catch (IOException e) {
			version = "undef (" + e.getMessage() + ")";
		}
		
		interestingPropertiesMap = new HashMap();
		if (!fullProp()) {
			for (int i = 0; i < interestingProperties.length; i++) {
				interestingPropertiesMap.put(interestingProperties[i], new Object());
			}
		}
	}

    public static void errException(JcanLogger log, Throwable t, Object params) {
    	log.error(exceptionString(t, params));
    }

    public static String exceptionString(Throwable t, Object params) {
    	String eol = System.getProperty("line.separator");
    	return exceptionString(eol, t, params);
    }

    public static String exceptionString(String eol, Throwable t, Object params) {
    	List res = logExceptionIsilike(t, params);
    	StringBuilder builder = new StringBuilder(eol);
    	for (Iterator iterator = res.iterator(); iterator.hasNext();) {
			String line = (String) iterator.next();
			builder.append(line).append(eol);
		}
    	return builder.toString();
    }

    /**
     * terribly simple implementation of old isi-exceptions...
     */
	private static List logExceptionIsilike(Throwable t, Object params) {
    	List log = new ArrayList();
    	log.add("Exception context {");
    	log.add("   " + t.getClass().getName() + " {");
    	log.add("      message       : " + t.getMessage());
    	if (!equals(t.getMessage(), t.getLocalizedMessage())) {
    		log.add("      loc message   : " + t.getLocalizedMessage());
    	}
    	try {
    		throw new Throwable();
    	}
    	catch (Throwable temp) {
        	StackTraceElement[] stack = temp.getStackTrace();
        	/* NOTE! '3' assumes that we are called by xyzException() which
        	 *       in turn was called by the exception causing method!
        	 */
        	StackTraceElement caller = stack[3];
        	caller.getMethodName();
        	caller.getFileName();

        	log.add("      handled in    : " + caller.getFileName() + ":" + caller.getLineNumber());
        	log.add("      method        : " + caller.getMethodName() + "()");
    	}
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
    	String time = dateFormat.format(new Date());
    	log.add("      raise time    : " + time);
    	log.add("      build version : " + version);

		traceRuntime(log, 6, "runtime status");
		traceThread(log, 6, "thread");
		traceProperties(log, 6, "system properties");
		if (t instanceof LogonException) {
			LogonException le = (LogonException)t;
			traceParams(log, 6, le.getParams(), "LogonException parameters");
		}
		if (params != null) {
			traceParams(log, 6, params, "method parameters");
		}
    	traceException(log, 6, "stack trace", t);
    	if (isClassProblem(t)) {
    		traceClassPath(log, 6, "class path");
    	}
    	log.add("   }");
    	log.add("}");
    	return log;
    }

    private static void traceRuntime(List log, int intend, String msg) {
    	StringBuilder buf = indentation(intend);
		log.add(buf + msg + " {");
		Runtime runtime = Runtime.getRuntime();
		Object[][] mem = {
				{"availableProcessors", "" + runtime.availableProcessors()},
				{"totalMem", "" + runtime.totalMemory() + " bytes"},
				{"freeMem", "" + runtime.freeMemory() + " bytes"},
				{"maxMem", "" + runtime.maxMemory() + " bytes"},
				{"usedMem", "" + (runtime.totalMemory() - runtime.freeMemory()) + " bytes"}
		};
    	logKeyVal(log, 9, mem);
    	log.add(buf + "}");
    }

    private static void traceThread(List log, int intend, String msg) {
    	if (Thread.activeCount() == 1) {
    		return;
    	}
    	StringBuilder buf = indentation(intend);
		log.add(buf + msg + " {");

		Object[][] threadInfo = {
				{"activeCount", "" + Thread.activeCount()},
				{"thread", "" + Thread.currentThread().toString()},
		};
    	logKeyVal(log, 9, threadInfo);
    	log.add(buf + "}");
    }

	private static StringBuilder indentation(int intend) {
		StringBuilder buf = new StringBuilder();
		for (int i = intend; i > 0; i--) {
			buf.append(" ");
		}
		return buf;
	}

    private static void traceProperties(List log, int intend, String msg) {
    	StringBuilder buf = indentation(intend);
		log.add(buf + msg + " {");
		Properties props = System.getProperties();

		Object[][] jvm = null;
		if (!fullProp()) {
			int propNum = interestingProperties.length;
			jvm = new Object[propNum+1][2];
			int j = 0;
			for (; j < propNum; j++) {
				jvm[j][0] = interestingProperties[j];
				jvm[j][1] = props.get(interestingProperties[j]);
			}
			jvm[j][0] = "...";
			jvm[j][1] = "some properties omitted. enable full property trace with 'fullPropertyTracing' system property.";
		}
		else {
			int i = 0;
			Set keys = props.keySet();
			jvm = new Object[keys.size()][2];
			for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				if (fullProp() || interestingPropertiesMap.containsKey(key)) {
					jvm[i][0] = key;
					jvm[i][1] = props.get(key);
					i++;
				}
			}
		}

		logKeyVal(log, 9, jvm);
		log.add(buf + "}");
    }

    private static boolean fullProp() {
		return System.getProperty(PROP_FULL_PROPERTIES_TRC) != null;
    }

    private static void traceException(List log, int intend, String msg, Throwable t) {
    	StackTraceElement[] stack = t.getStackTrace();
    	StringBuilder buf = indentation(intend);
		log.add(buf + msg + " {");
		log.add(buf + "   description: " + t);
    	for (int i = 0; i < stack.length; i++) {
			StackTraceElement element = stack[i];
			String fn = element.getFileName();
			if (fn == null) {
				fn = "Unknown Source";
			}
			else {
				fn += ":" + element.getLineNumber();
			}
			if (element.getClassName().contains(NAMESPACE)) {
				log.add(
						buf + "!!! at " +  element.getClassName() + "." + element.getMethodName() + "(" + fn + ")");
			}
			else {
				log.add(
						buf + "    at " +  element.getClassName() + "." + element.getMethodName() + "(" + fn + ")");
			}
		}
    	if (t.getCause() != null) {
    		traceException(log, intend + 3, "caused by", t.getCause());
    	}
    	log.add(buf + "}");
    }

	/**
	 * will be used if there is a classpath-related problem
	 *
	 * NOTE: this is a very crude implementation. we should actually
	 * - check 'java.system.class.loader' property to find out the class loading
	 * - recursively go through loader.getParent() with delegation model
	 * (= depth first search)
	 *
	 * If you don't understand this, then you should probably not improve the
	 * implementation. :)
	 */
    private static void traceClassPath(List<String> log, int intend, String msg) {
    	StringBuilder buf = indentation(intend);
    	StringBuilder buf2 = indentation(intend+3);

    	HashMap<String, Object> m = new HashMap<String, Object>();
		log.add(buf + msg + " {");
		// JBoss7 does not return an URLClassLoader
		ClassLoader cl1 = ClassLoader.getSystemClassLoader();
		if (cl1 instanceof URLClassLoader) {
		    URLClassLoader sysloader = (URLClassLoader) cl1;
		    URL[] urls = sysloader.getURLs();
		    for (URL url : urls) {
		        m.put(url.getPath(), new Object());
		        log.add(buf2 + "@SYSTEM: " + url.getPath());
		    }
		}
		else {
		    log.add(buf2 + "@SYSTEM: " + cl1.toString());
		}

		ClassLoader cl2 = Thread.currentThread().getContextClassLoader();
		if (cl2 instanceof URLClassLoader && !cl2.equals(cl1)) {
		    URL[] urls = ((URLClassLoader)cl2).getURLs();
		    urls = ((URLClassLoader)Thread.currentThread().getContextClassLoader()).getURLs();
		    for (URL url : urls) {
		        if (!m.containsKey(url.getPath())) {
		            log.add(buf2 + "@CONTEXT: " + url.getPath());
		        }
		    }
		}
		else if (!cl2.equals(cl1)) {
            log.add(buf2 + "@CONTEXT: " + cl2.toString());
        }

		log.add(buf + "}");

		/* to find out from which jar a particular class was loaded
		Class cls = LOG.getBackendLogger().getClass();
		ProtectionDomain pDomain = cls.getProtectionDomain();
		CodeSource cSource = pDomain.getCodeSource();
		URL loc = cSource.getLocation();
		log.add("################ " + loc);
		*/
	}

    private static boolean isClassProblem(Throwable t) {
    	if (t instanceof ClassCastException ||
    			t instanceof ClassNotFoundException ||
    			t instanceof LinkageError) {
    		return true;
		}
    	if (t.getCause() != null) {
    		return isClassProblem(t.getCause());
    	}
    	return false;
    }

	private static void logKeyVal(List log, int intend, Object params) {
		StringBuilder buf = indentation(intend);

		if (params == null) {
			log.add(buf + "<null>");
		}
		else if (params instanceof String) {
			log.add(buf + params.toString());
		}
		else if (params instanceof Object[][]) {
			Object[][] paramsArrayArray = (Object[][]) params;
			int length = paramsArrayArray.length;
			int maxStrLength = 0;
			for (int count = 0; count < length; count++) {
				String curParam = "" + paramsArrayArray[count][0];
				if (curParam.length() > maxStrLength) {
					maxStrLength = curParam.length();
				}
			}
			for (int count = 0; count < length; count++) {
				StringBuilder result = new StringBuilder();
				String curParam = "" + paramsArrayArray[count][0];
				int padding = maxStrLength - curParam.length() + intend;
				for (; padding > 0; padding--) {
					result.append(" ");
				}

				result.append(curParam);
				result.append(" = '");
				result.append(p2str(paramsArrayArray[count][1]));
				result.append("'");
				log.add(result.toString());
			}
		}
	}

	private static void traceParams(List log, int intend, Object params, String msg) {
		StringBuilder buf = indentation(intend);
		log.add(buf + msg + " {");
		logKeyVal(log, intend + 3, params);
		log.add(buf + "}");
	}

	public static String p2str(Object o) {
		if (o instanceof Object[]) {
			StringBuilder result = new StringBuilder();
			Object[] cur = (Object[])o;
			if (cur.length > 0) {
				for (int i = 0; i < cur.length-1; i++) {
					result.append(cur[i] + ", ");
				}
				result.append(cur[cur.length-1]);
			}
			return result.toString();
		}
		return o + "";
	}

    private static boolean equals(Object a, Object b) {
    	if (a == null && b == null) {
    		return true;
    	}
    	if (a == null && b != null) {
    		return false;
    	}
    	if (a != null && b == null) {
    		return false;
    	}
    	return a.equals(b);
    }
    
    /**
     * Tries to dig out the Reason from a Throwable if the cause is LogonException.
     * 
     * @param throwable - the Throwable instance to examine.
     * @param defaultReason - the Reason which to return if no Reason was found. 
     * @return Reason - if the cause of the Throwable is LogonException, 
     *   returns the Reason from it, otherwise the given defaultReason. 
     */
    public static Reason getLogonExceptionReason(Throwable throwable, Reason defaultReason) {
    	Reason result = defaultReason;
    	
    	if (throwable != null && throwable.getCause() != null && 
    			throwable.getCause() instanceof LogonException) {
			LogonException logonException = (LogonException) throwable.getCause();
			if (logonException.getReason() != null) {
				result = logonException.getReason();
			}
		}
    	
    	return result;
    }
    
    /**
     * Tries to dig out the key of the Reason from a Throwable if the cause is LogonException.
     * 
     * @param throwable - the Throwable instance to examine.
     * @param defaultReasonKey - the Reason key which to return if no Reason key was found. 
     * @return Reason key String - if the cause of the Throwable is LogonException, 
     *   returns the Reason key from it, otherwise the given defaultReasonKey. 
     */
    public static String getLogonExceptionReasonKey(Throwable throwable, String defaultReasonKey) {
    	String result = defaultReasonKey;
    	
    	Reason reason = getLogonExceptionReason(throwable, null);
    	if (reason != null) {
    		result = reason.getReason();
    	}
    	
    	return result;
    }
}
