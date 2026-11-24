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
import com.holcim.logon.eventbeans.in.mop.MopResponseCode;
import com.holcim.logon.eventbeans.in.mop.ReportPerDayRequest;
import com.crh.logon.persistence.entities.common.DailyReportEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.refdata.TruckMaintenanceBsEntity;

/**
 * @author AdNovum Informatik AG
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class ReportPerDayRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReportPerDayRequestHandler.class);

    private static String FIELD_INSPECTION = "Inspection";
    private static String FIELD_ODOMETER = "Odometer";
    private static String FIELD_MAINTENANCE = "Maintenance";
    private static String FIELD_WORKINGPERIOD = "WorkingPeriod";
    private static String FIELD_DRIVINGPERIOD = "DrivingPeriod";

	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;	
    
    protected void validate(LogonEventRequest eventBean) {
        
    	ReportPerDayRequest bean = (ReportPerDayRequest)eventBean;
    	validateMandatory(FIELD_INSPECTION, bean.isInspection());
    	validateMandatory(FIELD_ODOMETER, bean.getOdometer());
    	validateMandatory(FIELD_DRIVINGPERIOD, bean.getDrivingPeriod());
    	validateMandatory(FIELD_WORKINGPERIOD, bean.getWorkingPeriod());
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
            ReportPerDayRequest bean = (ReportPerDayRequest)eventBean;
            
            DailyReportEntity dailyReport = new DailyReportEntity();
            dailyReport.setInspection(bean.isInspection());
            dailyReport.setOdometer(bean.getOdometer().longValue());
            
            DriverEntity driver = getCurrentDriver();
            if (driver == null) {
                
            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.APPLICATION_ERROR, "driver not found"));
            }
            dailyReport.setUser(driver.getUser());
            dailyReport.setTruck(driver.getCurrentTruck());
            dailyReport.setWorkingtimeMin(new Long(bean.getWorkingPeriod()));
            dailyReport.setSteeringtimeMin(new Long(bean.getDrivingPeriod()));
			
            TruckMaintenanceBsEntity truckMaintenance = null;
            if (bean.getMaintenance() != null) {
            	truckMaintenance = referentialDataService.findBsEntity(TruckMaintenanceBsEntity.class, new Long(bean.getMaintenance()));
	            if (truckMaintenance == null) {
			
	            	throw new LogonEventException(createBasicLogonEventResponse(MopResponseCode.FIELD_UNKNOWN, FIELD_MAINTENANCE), bean.getMaintenance());
	            }
            }
            dailyReport.setTruckMaintenance(truckMaintenance);
            getDaoProvider().getDailyReportEntityDao().persist(dailyReport);
            
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
