/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * @author AdNovum Informatik AG
 */
public final class BigDecimalHandler extends AbstractHandler {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BigDecimalHandler.class);	
	
	private static final PropertyUtilsBean pub = BeanUtilsBean.getInstance().getPropertyUtils();
	private static final Field bdStringCache;
	
	static {
		try {
			bdStringCache = BigDecimal.class.getDeclaredField("stringCache");
			bdStringCache.setAccessible(true);
		}
		catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.axis2.engine.Handler#invoke(org.apache.axis2.context.MessageContext)
	 */
	public InvocationResponse invoke(MessageContext mc) throws AxisFault {

		if ((mc != null) && (mc.getEnvelope() != null) && (mc.getEnvelope().getBody() != null)) {

			long start = 0;
			if (LOG.isDebugEnabled()) {
				start = System.currentTimeMillis();
				LOG.debug("Visitng mc.getEnvelope().getBody().getFirstElement().");
			}
			try {
				visit(mc.getEnvelope().getBody().getFirstElement());
			}
			catch (Exception e) {
				LOG.error("Visit mc.getEnvelope().getBody().getFirstElement() failed!", e);
			}
			finally {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Visiting took " + (System.currentTimeMillis() - start) + " ms");
				}				
			}
		}								
		
		return InvocationResponse.CONTINUE;
	}

	private void visit(OMElement element) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {		
		if (element != null) {
			final boolean isEnteringExitingEnabled = LOG.isEnteringExitingEnabled();
			if (isEnteringExitingEnabled) {
				LOG.entering(this, "visit", new Object[][]{{"element.QName", element.getQName()}});
			}
			try {
				if (element instanceof OMSourcedElement) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("OMElement is an OMSourcedElement, getting dataSource.");
					}
					final OMDataSource ds = ((OMSourcedElement) element).getDataSource();
					if (ds instanceof OMDataSourceExt) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("OMDataSource is an OMDataSourceExt, getting backing object; traversing via backing objects from now on.");
						}
						visit(((OMDataSourceExt) ds).getObject());
						return;
					}
				}
				
				@SuppressWarnings("unchecked")
				final Iterator<OMElement> iter = element.getChildElements();
				while (iter.hasNext()) {
					visit(iter.next());
				}
			}
			finally {
				if (isEnteringExitingEnabled) {
					LOG.exiting();
				}
			}		
		}
	}
	
	private void visit(Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		visit(object.getClass().getPackage(), object);		
	}

	private void visit(Package pkg, Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object[][] params = { { "pkg", pkg.getName()}, { "object",  ObjectUtils.identityToString(object)} };
		final boolean isEnteringExitingEnabled = LOG.isEnteringExitingEnabled();
		if (isEnteringExitingEnabled) {
			LOG.entering(this, "visit", params);
		}
		try {
			if (object != null) {
				final Class<?> clazz = object.getClass();
				
				if (!clazz.isPrimitive()) {
					if (clazz.isArray()) {
						final Object[] array = (Object[]) object;
						if (LOG.isDebugEnabled()) {
							LOG.debug(String.format("visit (array with size=%d) {", array.length));
						}
						try {
							for (Object element : array) {
								visit(pkg, element);
							}
						}
						finally {
							if (LOG.isDebugEnabled()) {
								LOG.debug("}");
							}
						}
					}
					else if (Collection.class.isAssignableFrom(clazz)) {
						final Collection<?> collection = (Collection<?>) object;
						if (LOG.isDebugEnabled()) {
							LOG.debug(String.format("visit (collection with size=%d) {", collection.size()));
						}
						try {
							for (Object element : collection) {
								visit(pkg, element);
							}
						}
						finally {
							if (LOG.isDebugEnabled()) {
								LOG.debug("}");
							}
						}
					}
					else if (BigDecimal.class.equals(clazz)) {
						fix((BigDecimal) object);
					}
					else if (ObjectUtils.equals(pkg, clazz.getPackage())) {
						if (LOG.isDebugEnabled()) {
							LOG.debug(String.format("visit (properties of %s) {", ObjectUtils.identityToString(object)));
						}
						try {
							final PropertyDescriptor[] pds = pub.getPropertyDescriptors(clazz);
							Method readMethod;
							for (PropertyDescriptor pd : pds) {
								if (StringUtils.equals(pd.getName(), "class")) {
									continue;
								}
								readMethod = pd.getReadMethod();
								if (readMethod != null) {
									Object property = readMethod.invoke(object, new Object[0]);
									if (LOG.isDebugEnabled()) {
										LOG.debug(String.format(
												"visit %s (%s).", 
												pd.getName(),
												ObjectUtils.identityToString(property)));
									}
									visit(pkg, property);
								}
							}
						}
						finally {
							if (LOG.isDebugEnabled()) {
								LOG.debug("}");
							}
						}
					}				
				}
			}
		}
		finally {
			if (isEnteringExitingEnabled) {
				LOG.exiting();
			}
		}
	}
		
	private void fix(BigDecimal bd) throws IllegalArgumentException, IllegalAccessException {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format(
					"Fixing BigDecimal from %s to %s.", 
					bd.toString(), 
					bd.toPlainString()));
		}
		bdStringCache.set(bd, bd.toPlainString());
	}
}
