/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.ajax;

import java.math.BigDecimal;
import java.util.Locale;

import com.crh.logon.business.services.interfaces.BestellungChartService;
import com.crh.logon.business.services.interfaces.BestellungService;
import com.crh.logon.business.services.interfaces.CarrierService;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.interfaces.OrderDetailService;
import com.crh.logon.business.services.interfaces.PlantSareaService;
import com.crh.logon.business.services.interfaces.PoolAdminService;
import com.crh.logon.business.services.interfaces.RailSchedulingService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.interfaces.SimpleVehicleSchedulingService;
import com.crh.logon.business.services.interfaces.TimeZoneService;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.converters.BigDecimalConverter;
import com.crh.logon.converters.BooleanConverter;
import com.crh.logon.converters.LongConverter;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.web.utils.UserProvider;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;

/**
 * @author AdNovum Informatik AG
 */
public class DwrServiceSupport {
	private ServiceLocator serviceLocator;

	public ServiceLocator getServiceLocator() {
		return serviceLocator;
	}

	public void setServiceLocator(ServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	public BestellungService getBestellungService() {
		return serviceLocator.getBestellungService();
	}

	public BestellungChartService getBestellungChartService() {
		return serviceLocator.getBestellungChartService();
	}

	public DispokarteService getDispokarteService() {
		return serviceLocator.getDispokarteService();
	}

	public CarrierService getCarrierService() {
		return serviceLocator.getCarrierService();
	}

	public PlantSareaService getPlantSareaService() {
		return serviceLocator.getPlantSareaService();
	}

	public PoolAdminService getPoolAdminService() {
		return serviceLocator.getPoolAdminService();
	}

	public OrderDetailService getOrderDetailService() {
		return serviceLocator.getOrderDetailService();
	}

	public RailSchedulingService getRailSchedulingService() {
		return serviceLocator.getRailSchedulingService();
	}

	public SimpleVehicleSchedulingService getSimpleVehicleSchedulingService() {
		return serviceLocator.getSimpleVehicleSchedulingService();
	}

	public TimeZoneService getTimeZoneService() {
		return serviceLocator.getTimeZoneService();
	}

	public ConfigBean getConfigBean() {
		return serviceLocator.getConfigService().getConfigBean();
	}
	
	public RefDataGenericsService getRefGenericsService() {
		return serviceLocator.getRefDataGenericsService();
	}

	public TimeZone getUserTimeZone() {
		return UserProvider.getTimezone();
	}

	public Locale getUserLocale() {
		return UserProvider.getLocale();
	}

	public BeanUtilsBean getBeanUtilsBean() {
		BeanUtilsBean beanUtilsBean = new BeanUtilsBean();
		ConvertUtilsBean convertUtilsBean = beanUtilsBean.getConvertUtils();
		convertUtilsBean.register(new BigDecimalConverter(), BigDecimal.class);
		convertUtilsBean.register(new LongConverter(), Long.class);
		convertUtilsBean.register(new BooleanConverter(), Boolean.class);

		return beanUtilsBean;
	}
	
}
