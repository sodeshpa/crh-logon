/*
 * Author            : AdNovum Informatik AG
 * Version Number    : \$Revision$
 * Date of last edit : \$Date$
 */

package com.crh.logon.admin.web.utils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.simplevehicleadmin.SimpleVehicleAdminForm;


public final class SimpleVehicleAdminChangeLogic {

		public static final int FUNCTIONALITY_CHANGE_VEHICLE_TYPE = 3;
		public static final int FUNCTIONALITY_CHANGE_VOLUME = 6;
		public static final int FUNCTIONALITY_SET_UNIT_COUNT = 7;
		public static final int FUNCTIONALITY_SET_NR_CHAMBERS = 8;
		public static final int FUNCTIONALITY_SET_REG_NO = 11;
		public static final int FUNCTIONALITY_SET_SAP_NO = 12;
		public static final int FUNCTIONALITY_SET_SAP_TYPE = 13;
		public static final int FUNCTIONALITY_SET_TARE_WEIGHT = 14;
		public static final int FUNCTIONALITY_SET_MAX_WEIGHT = 15;
		public static final int FUNCTIONALITY_SET_COMPANYCODE = 16;
		
		private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SimpleVehicleAdminChangeLogic.class);
		
		/**
		 * Determines if a certain functionality is NOT ALLOWED on this truck. Returns true, if the functionality is not allowed for
		 * the user, and returns false, if no need to disable.
		 * @param TruckAdminForm
		 * @param functionality OrderChangeLogic FUNCTIONALITY... 
		 * @return
		 */
		public static boolean disableChangeFunctionality(SimpleVehicleAdminForm form, int functionality ){
			// [[
			boolean result1 = false;
			Object[][] params = {{"form", form},
								{"functionality", functionality + ""}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(SimpleVehicleAdminChangeLogic.class, "disableChangeFunctionality", params);
			}
			// ]]
			try {
				// 2012-Nov-18/awe: at the moment, there is no restriction, can be added here as needed
				result1 = false;
				switch (functionality){
				case FUNCTIONALITY_CHANGE_VOLUME:
					LOG.debug("volumen");
					break;					
				case FUNCTIONALITY_CHANGE_VEHICLE_TYPE:
					LOG.debug("vehicleType");
					break;
				case FUNCTIONALITY_SET_UNIT_COUNT:
					LOG.debug("unitCount");					
					break;			
				case FUNCTIONALITY_SET_NR_CHAMBERS:
					LOG.debug("chambers");					
					break;	
				case FUNCTIONALITY_SET_REG_NO:
					LOG.debug("regNo");	
					break;
				case FUNCTIONALITY_SET_SAP_NO:
					LOG.debug("sapNo");
					break;
				case FUNCTIONALITY_SET_SAP_TYPE:
					LOG.debug("sapType");
					break;
				case FUNCTIONALITY_SET_TARE_WEIGHT:
					LOG.debug("tareWeight");	
					break;
				case FUNCTIONALITY_SET_MAX_WEIGHT:
					LOG.debug("maxWeight");	
					break;
				case FUNCTIONALITY_SET_COMPANYCODE:
					LOG.debug("companycode");	
					break;
				default:
					LOG.error("Unhandled functionality code");
					break;
				}
				return result1;
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
