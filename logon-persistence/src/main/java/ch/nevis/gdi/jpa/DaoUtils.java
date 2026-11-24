/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public class DaoUtils {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DaoUtils.class);
    private static final JcanLogger LOG_PERFORMANCE = JcanLoggerFactory.getLogger(DaoUtils.class.getName() + ".dbperf");

    // time in ms after which a QTIME is traced at ERROR level
    private static final long MAX_ACCEPTABLE_QUERY_TIME = 30000; // 30 seconds
    // time in ms after which a QTIME is traced at INFO level
    // from productive logs we see that the vast majority should be below 100 ms.
    private static final long THRESHOLD_QUERY_INFO_TIME = 100;  // 100 ms


    public static void reportQtime(TimeTaker timeTaker, String mth, Object[][] params, Object... values) { // NOPMD
    	reportQtime(null, timeTaker, mth, params, values);
	}
    
    @SuppressWarnings("rawtypes")
	public static void reportQtime(String clazz, TimeTaker timeTaker, String mth, Object[][] params, Object... values) { // NOPMD
    	String clazzStr = "";
    	if (clazz != null) {
    		clazzStr = "(" + clazz + ") ";
    	}
        if (timeTaker.getElapsedTimeMillis() > MAX_ACCEPTABLE_QUERY_TIME) {
            LOG_PERFORMANCE.error("query time exceeded '" + MAX_ACCEPTABLE_QUERY_TIME + "ms'");
            LOG_PERFORMANCE.error(timeTaker.getPerformanceLogString() + clazzStr + trcMeth(mth, params, values));
        }
        else if (timeTaker.getElapsedTimeMillis() > THRESHOLD_QUERY_INFO_TIME) {
            LOG_PERFORMANCE.info(timeTaker.getPerformanceLogString() + clazzStr + trcMeth(mth, params, values));
        }
        else if (LOG_PERFORMANCE.isDebugEnabled()) {
            LOG_PERFORMANCE.debug(timeTaker.getPerformanceLogString() + clazzStr + trcMeth(mth, params, values));
        }
	}

	public static String trcMeth(String meth, Object[][] p) {
		// [[
		String result = null;
		Object[][] params = {{"meth", meth},
							{"p", p}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DaoUtils.class.getName(), "trcMeth", params);
		}
		// ]]
		try {
			return result = trcMeth(meth, p, (Object[]) null);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Varargs are used to provide full flexibility to log varying number
	 * of bind parameters in a JPA-QL query
	 */
	public static String trcMeth(String meth, Object[][] p, Object... values) { // NOPMD
		// [[
		String result = null;
		Object[][] params = {{"meth", meth},
							{"p", p}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DaoUtils.class.getName(), "trcMeth", params);
		}
		// ]]
		try {
			StringBuilder log = new StringBuilder();
			log.append(meth + "( params { ");
			logKeyVal(log, p);
			
			if (values != null) {
				log.append(", values { ");
				for (int i = 0; i < values.length; i++) {
					log.append(" " + values[i]);
				}
				log.append(" }");
			}
			log.append(" }");
			log.append(" )");
			
			return result = log.toString();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private static void logKeyVal(StringBuilder buf, Object params) {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] prm = {{"buf", buf},
								{"params", params}};
			LOG.entering(ReadOnlyDao.class, "logKeyVal", prm);
		}
		// ]]
		try {
			if (params == null) {
				buf.append("<null>");
			}
			else if (params instanceof String) {
				buf.append(params.toString());
			}
			else if (params instanceof Object[][]) {
				Object[][] paramsArrayArray = (Object[][]) params;
				int length = paramsArrayArray.length;
				for (int count = 0; count < length; count++) {
					String curParam = "" + paramsArrayArray[count][0];
					buf.append(curParam);
					buf.append(" = '");
					buf.append(p2str(paramsArrayArray[count][1]));
					buf.append("' ");
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private static String p2str(Object o) {
		// [[
		String result1 = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"o", o}};
			LOG.entering(ReadOnlyDao.class, "p2str", params);
		}
		// ]]
		try {
			if (o instanceof Object[]) {
				StringBuilder result = new StringBuilder();
				Object[] cur = (Object[])o;
				for (int i = 0; i < cur.length-1; i++) {
					result.append(cur[i] + ", ");
				}
				result.append(cur[cur.length-1]);
				return result1 = result.toString();
			}
			return result1 = o + "";
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}
}
