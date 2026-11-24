/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.forms;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import com.crh.logon.converters.BooleanConverter;
import com.crh.logon.converters.LongConverter;
import com.crh.logon.converters.URIConverter;
import com.crh.logon.utils.LogonException;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.ResourceKey;
import com.crh.logon.web.validators.RegexValidation;
import com.crh.logon.web.validators.Required;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.Message;
import com.crh.logon.converters.BigDecimalConverter;
import com.crh.logon.web.validators.StrutsValidator;
import com.crh.logon.web.validators.TypeValidation;

/**
 * @author AdNovum Informatik AG
 */
public abstract class AbstractActionForm extends ActionForm {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(AbstractActionForm.class);
	
	/**
	 * Validates a bean based on it's annotations from the com.crh.logon.web.validators package.
	 * 
	 * TODO: actually move these annotations to the fields instead of the getter methods, like hibernate validation (use hibernate for validation?)
	 * @param request
	 * @param bean
	 * @return
	 */
	protected ActionErrors validateBeanProperties(HttpServletRequest request, Object bean) {
		// [[
		ActionErrors result = null;
		Object[][] params = {{"request", request},
							{"bean", bean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validateBeanProperties", params);
		}
		// ]]
		try {
			
			ActionErrors actionErrors = null;
			if (bean == null) {
				
				return result = actionErrors;
			}
			LOG.debug("Type validation on " + bean.getClass().getSimpleName());
			try {
				PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(bean);
				for (PropertyDescriptor propertyDescriptor: propertyDescriptors) {
					Method readMethod = propertyDescriptor.getReadMethod();
					if (readMethod != null) {
						
						Object srcValue = BeanUtils.getProperty(bean, propertyDescriptor.getName());
						String fieldDesc = propertyDescriptor.getDisplayName();
						ResourceKey resourceKey = readMethod.getAnnotation(ResourceKey.class);
						if ( (resourceKey != null) && (!StringUtils.isEmpty(resourceKey.value())) ) {
							fieldDesc = Message.lookup(request.getLocale(), resourceKey.value());
						}
						
						Required required = readMethod.getAnnotation(Required.class);
						if (required != null) {
							if ( (srcValue == null) || (StringUtils.isEmpty(srcValue.toString())) ) {
								actionErrors = MessageUtils.addActionError(request, "error.field.required", fieldDesc);
							}
						}
						
						TypeValidation typeValidation = readMethod.getAnnotation(TypeValidation.class);
						if (typeValidation != null) {
							Class type = typeValidation.value();
							if ( (type.equals(BigDecimal.class)) || (type.equals(Long.class)) ) {
								try {
									if (type.equals(BigDecimal.class)) {
										new BigDecimalConverter().convert(type, srcValue);
									}
									else if (type.equals(Long.class)) {
										new LongConverter().convert(type, srcValue);
									}
								} catch (ConversionException e) {
									actionErrors = MessageUtils.addActionError(request, "error.field.typevalidation.number", fieldDesc);
								}
							}
							else if (type.equals(Boolean.class)) {
								try {
									new BooleanConverter().convert(type, srcValue);
								} catch (ConversionException e) {
									actionErrors = MessageUtils.addActionError(request, "error.field.typevalidation.boolean", fieldDesc);
								}
							}
							else if (type.equals(URI.class)) {
								try {
									new URIConverter().convert(type, srcValue);
								} catch (ConversionException e) {
									actionErrors = MessageUtils.addActionError(request, "error.field.typevalidation.uri", fieldDesc);
								}
							}
						}
						
						RegexValidation regexValidation = readMethod.getAnnotation(RegexValidation.class);
						if (regexValidation != null) {
							String regex = regexValidation.regex();
							String formatDesc = regexValidation.formatDesc();
							if (srcValue != null) {
								String strValue = srcValue.toString();
								if ( (!StringUtils.isEmpty(strValue)) && (!strValue.matches(regex)) ) {
									actionErrors = MessageUtils.addActionError(request, "error.field.regexvalidation", fieldDesc, formatDesc);	
								}
							}
						}
					}
				}		
			} catch (Exception e) {
				throw new LogonException(e);
			}
			
			return result = actionErrors;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	protected ActionErrors invokeBeanStrutsValidators(ActionMapping mapping, HttpServletRequest request, Object bean) {
		// [[
		ActionErrors result = null;
		Object[][] params = {{"mapping", mapping},
							{"request", request},
							{"bean", bean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "invokeBeanStrutsValidators", params);
		}
		// ]]
		try {
			
			ActionErrors errors = null;
			
			Method[] methods = bean.getClass().getMethods();
			for (Method m: methods) {
				if (m.getAnnotation(StrutsValidator.class) != null) {
					try {
						LOG.debug("Struts validator found: " + bean.getClass().getSimpleName() + "." + m.getName());
						errors = (ActionErrors)m.invoke(bean, mapping, request);
					}
					catch (Exception e) {
						throw new LogonException(e);
					}
				}
			}
			
			return result = errors;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	protected ActionErrors validateBean(ActionMapping mapping, HttpServletRequest request, Object bean) {
		
		ActionErrors errors = null;
		
		errors = validateBeanProperties(request, bean);
		errors = invokeBeanStrutsValidators(mapping, request, bean);
		
		return errors;
	}
}
