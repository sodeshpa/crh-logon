/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.converters.Base64ByteArrayConverter;
import com.crh.logon.converters.BigDecimalConverter;
import com.crh.logon.converters.BooleanConverter;
import com.crh.logon.converters.DateConverter;
import com.crh.logon.converters.LongConverter;
import com.crh.logon.converters.StringConverter;

/**
 * @author AdNovum Informatik AG
 */
public class BeanUtilsBuilder {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BeanUtilsBuilder.class);

	private List<Converter> converters = new ArrayList<Converter>();
	
	/**
	 * Ads a {@link Converter} instance to the converter list of this
	 * builder. Will be used when building the instance
	 * @param converter
	 * @return
	 */
	public BeanUtilsBuilder addConverter(Converter converter) {
		// [[
		BeanUtilsBuilder result = null;
		Object[][] params = {{"converter", converter}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addConverter", params);
		}
		// ]]
		try {
			this.converters.add(converter);
			return result = this;
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
	 * Final command to instantiate the bean and add register all converters
	 * previously added to the builder.
	 * @return - created instance
	 */
	public  BeanUtilsBean createBean() {
		// [[
		BeanUtilsBean result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(BeanUtilsBuilder.class, "createBean");
		}
		// ]]
		try {
			
			BeanUtilsBean beanUtilsBean = new BeanUtilsBean();
			
			ConvertUtilsBean convertUtilsBean = beanUtilsBean.getConvertUtils();
			
			for (Converter converter : converters) {
				if (converter instanceof BigDecimalConverter) {
					convertUtilsBean.register(new BigDecimalConverter(), BigDecimal.class);
				}
				else if (converter instanceof LongConverter) {
					convertUtilsBean.register(new LongConverter(), Long.class);
				}
				else if (converter instanceof BooleanConverter) {
					convertUtilsBean.register(new BooleanConverter(), Boolean.class);
				}
				else if (converter instanceof DateConverter) {
					convertUtilsBean.register(new DateConverter(), Date.class);
				}
				else if (converter instanceof Base64ByteArrayConverter) {
					convertUtilsBean.register(new Base64ByteArrayConverter(), byte[].class);
				}
				else if (converter instanceof StringConverter) {
					convertUtilsBean.register(new StringConverter(), String.class);
				}
				else {
					throw new RuntimeException("Unsupported Converter type! Please add implementation!");
				}
			}
			
			return result = beanUtilsBean;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
}
