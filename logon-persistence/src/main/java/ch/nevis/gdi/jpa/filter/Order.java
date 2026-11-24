/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package ch.nevis.gdi.jpa.filter;

import org.apache.commons.lang.builder.ToStringBuilder;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public final class Order extends PropertyNameExpression {

    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(Order.class);

    private boolean ascending;
    private boolean caseSensitive;
    
    private Order(String propertyName, boolean ascending, boolean caseSensitive) {
    	super(propertyName);
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"propertyName", propertyName},
								{"ascending", ascending + ""}};
			LOG.entering(this, "Order", params);
		}
		// ]]
		try {
			this.ascending = ascending;
			this.caseSensitive = caseSensitive;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    public static Order asc(String propertyName) {
		// [[
		Order result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"propertyName", propertyName}};
			LOG.entering(Order.class.getName(), "asc", params);
		}
		// ]]
		try {
			return result = new Order(propertyName, true, true);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    public static Order desc(String propertyName) {
		// [[
		Order result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"propertyName", propertyName}};
			LOG.entering(Order.class.getName(), "desc", params);
		}
		// ]]
		try {
			return result = new Order(propertyName, false, true);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    public static Order descCaseInsensitive(String propertyName) {
		// [[
		Order result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"propertyName", propertyName}};
			LOG.entering(Order.class.getName(), "descCaseInsensitive", params);
		}
		// ]]
		try {
    		if (propertyName == null) {
    			return result = null;
    		}
    		return result = new Order(propertyName, false, false);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

    public static Order ascCaseInsensitive(String propertyName) {
		// [[
		Order result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			Object[][] params = {{"propertyName", propertyName}};
			LOG.entering(Order.class.getName(), "ascCaseInsensitive", params);
		}
		// ]]
		try {
    		if (propertyName == null) {
    			return result = null;
    		}
    		return result = new Order(propertyName, true, false);
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
     * This is a convenience method which does a case insensitive ordering for
     * Strings and a normal ordering for any other data type.
     * @param entity
     * @param propertyName
     * @return
     */
    public static Order ascCaseInsensitive(Class entity, String propertyName) {
		// [[
		Order result = null;
		Object[][] params = {{"entity", entity},
							{"propertyName", propertyName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(Order.class.getName(), "ascCaseInsensitive", params);
		}
		// ]]
		try {
			Class clazz = entity.getClass().getField(propertyName).getClass();
			if ( (clazz != null) && (String.class.isAssignableFrom(clazz)) ) {
				return result = Order.ascCaseInsensitive(propertyName);
			}
			return result = Order.asc(propertyName);
		}
		catch (Exception e) {
			return result = Order.asc(propertyName);
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
     * This is a convenience method which does a case insensitive ordering for
     * Strings and a normal ordering for any other data type.
     * @param entity
     * @param propertyName
     * @return
     */
    public static Order descCaseInsensitive(Class entity, String propertyName) {
		// [[
		Order result = null;
		Object[][] params = {{"entity", entity},
							{"propertyName", propertyName}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(Order.class.getName(), "descCaseInsensitive", params);
		}
		// ]]
		try {
			Class clazz = entity.getClass().getField(propertyName).getClass();
			if ( (clazz != null) && (String.class.isAssignableFrom(clazz)) ) {
				return result = Order.descCaseInsensitive(propertyName);
			}
			return result = Order.desc(propertyName);
		}
		catch (Exception e) {
			return result = Order.desc(propertyName);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }

	public StringBuilder toJPAQL() {
		// [[
		StringBuilder result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "toJPAQL");
		}
		// ]]
		try {
			StringBuilder sb = new StringBuilder();
			if (!caseSensitive) {
				sb.append("upper(");
			}
			sb.append(getQualifiedPropertyName());
			if (!caseSensitive) {
				sb.append(")");
			}
			sb.append(ascending ? " asc" : " desc");
			return result = sb;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	@Override
	public String toString() {
		ToStringBuilder tbs = new ToStringBuilder(this);
        tbs.append(super.toString());
        tbs.append("jpaSnippet", toJPAQL().toString());
        return tbs.toString();
	}
	
}
