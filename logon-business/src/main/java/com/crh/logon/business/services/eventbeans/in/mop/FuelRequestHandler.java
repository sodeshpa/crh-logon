/*
 * Author : AdNovum Informatik AG
 */

package com.crh.logon.business.services.eventbeans.in.mop;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.utils.LogonEventException;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.mop.FuelRequest;
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.FuelingEntity;
import com.crh.logon.persistence.entities.refdata.CurrencyBsEntity;
import com.crh.logon.persistence.entities.refdata.FuelTypeBsEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class FuelRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(FuelRequestHandler.class);

    private static String FIELD_CURRENCY = "Currency";
    private static String FIELD_FUEL_TYPE = "FuelType";
    private static String FIELD_ODOMETER = "Odometer";
    private static String FIELD_PRICE = "Price";
    private static String FIELD_QUANTITY = "Quantity";
    
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;	
    
    protected void validate(LogonEventRequest eventBean) {
    	
    	FuelRequest bean = (FuelRequest)eventBean;
    	validateMandatory(FIELD_CURRENCY, bean.getCurrency());
    	validateMandatory(FIELD_FUEL_TYPE, bean.getFuelType());
    	validateMandatory(FIELD_ODOMETER, bean.getOdometer());
    	validateMandatory(FIELD_PRICE, bean.getPrice());
    	validateMandatory(FIELD_QUANTITY, bean.getQuantity());
    }


    protected LogonEventResponse perform(LogonEventRequest eventBean) {
		// [[
		LogonEventResponse result = null;
		Object[][] params = {{"eventBean", eventBean}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "perform", params);
		}
		// ]]
		try {
			FuelRequest bean = (FuelRequest)eventBean;
            
            DriverEntity driver = getCurrentDriver();
            if (driver == null) {
                
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "driver not found"));
            }
            
            FuelingEntity fueling = new FuelingEntity();
            fueling.setTruck(driver.getCurrentTruck());
            fueling.setUser(driver.getUser());
            CurrencyBsEntity currency = referentialDataService.findBsEntity(CurrencyBsEntity.class, bean.getCurrency());
            if (currency == null) {
			
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_CURRENCY), bean.getCurrency());
            }
            fueling.setCurrency(currency);
            FuelTypeBsEntity fuelType = referentialDataService.findBsEntity(FuelTypeBsEntity.class, new Long(bean.getFuelType()));
            if (fuelType == null) {
            	
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_FUEL_TYPE), bean.getFuelType());
            }
            fueling.setFuelType(fuelType);
            fueling.setOdometer(bean.getOdometer().longValue());
            fueling.setPrice(bean.getPrice().longValue());
            fueling.setQuantityL(bean.getQuantity().longValue());
            getDaoProvider().getFuelingEntityDao().persist(fueling);
            
            return result = createLogonEventResponse(MopResponseCode.OK);
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
