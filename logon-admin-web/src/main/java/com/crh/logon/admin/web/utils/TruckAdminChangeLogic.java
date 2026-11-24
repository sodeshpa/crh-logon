/*
 * Author            : AdNovum Informatik AG
 * Version Number    : \$Revision$
 * Date of last edit : \$Date$
 */

package com.crh.logon.admin.web.utils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.truckadmin.TruckAdminForm;
import com.crh.logon.constants.Constant;
import com.crh.logon.utils.StringUtils;



 public final class TruckAdminChangeLogic implements java.io.Serializable {

		public static final int FUNCTIONALITY_CHANGE_CONTAINER = 0;
		public static final int FUNCTIONALITY_CHANGE_TRAILER = 1;
		public static final int FUNCTIONALITY_CHANGE_TRUCK_TYPE = 3;
		public static final int FUNCTIONALITY_CHANGE_KATEGORIE = 4;
		public static final int FUNCTIONALITY_CHANGE_DUSTFILTER = 5;
		public static final int FUNCTIONALITY_CHANGE_VOLUMEN = 6;
		public static final int FUNCTIONALITY_SET_CHAMBERS = 7;
		public static final int FUNCTIONALITY_SET_CHAMBER_1_WEIGHT = 8;
		public static final int FUNCTIONALITY_SET_CHAMBER_2_WEIGHT = 9;
		public static final int FUNCTIONALITY_SET_PDA_FLAG = 10;
		public static final int FUNCTIONALITY_SET_PLATENO = 11;
		public static final int FUNCTIONALITY_SET_SAP_NO = 12;
		public static final int FUNCTIONALITY_SET_TARE_WEIGHT = 13;
		public static final int FUNCTIONALITY_SET_MAX_WEIGHT = 14;
		public static final int FUNCTIONALITY_SET_NUMAXLES = 15;
		
		private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TruckAdminChangeLogic.class);
		
		/**
		 * Determines if a certain functionality is NOT ALLOWED on this truck. Returns true, if the functionality is not allowed for
		 * the user, and returns false, if no need to disable.
		 * @param TruckAdminForm
		 * @param functionality OrderChangeLogic FUNCTIONALITY... 
		 * @return
		 */
		public static boolean disableChangeFunctionality(TruckAdminForm form, int functionality ){
			// [[
			boolean result1 = false;
			Object[][] params = {{"form", form},
								{"functionality", functionality + ""}};
			boolean logDebugEnabled = LOG.isDebugEnabled();
			if (logDebugEnabled) {
				LOG.entering(TruckAdminChangeLogic.class, "disableChangeFunctionality", params);
			}
			// ]]
			try {
				Long truckTransportCatCd = form.getInputTkategorie();
				Boolean pdaAvailableFlag = form.getInputPdaFlag();
				Long truckType = form.getInputLkwType();
				Long characteristic = form.getCharacteristic();
				final boolean loaded = form.isTruckLoaded();
				final boolean assigned = form.isTruckAssigned();
				final boolean isTruckInSession  = form.isTruckInSession();
				switch (functionality){
				case FUNCTIONALITY_CHANGE_KATEGORIE:
					LOG.debug("kategorie");
					if ( characteristic == Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL ) {
						return true;
					}
					if (loaded) {
						result1 = true;
					}
					break;
				case FUNCTIONALITY_CHANGE_CONTAINER:
					LOG.debug("container");
					if (pdaAvailableFlag == null || truckTransportCatCd == null ) {
						result1 = true;
					}
					else if ( pdaAvailableFlag || 
							truckTransportCatCd.equals(Constant.TKATEGORIE_SILO) ||
							truckTransportCatCd.equals(Constant.TKATEGORIE_TRAILER ) ){
						result1 = true;
					}
					if (assigned) {
						result1 = true;
					}
					break;
				case FUNCTIONALITY_CHANGE_DUSTFILTER:
					LOG.debug("dustfilter");
					if (truckTransportCatCd != null && truckTransportCatCd.equals(Constant.TKATEGORIE_TRAILER ) ){
						result1 = true;
						//it must be disabled, and cannot be selected
						form.setInputDustfilter(null);
					}
					break;
				case FUNCTIONALITY_CHANGE_VOLUMEN:
					LOG.debug("volumen");
					if ( characteristic == Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL ) {
						return true;
					}
					if (truckTransportCatCd != null && truckTransportCatCd.equals(Constant.TKATEGORIE_TRAILER ) ){
						result1 = true;
						//it must be disabled, and cannot be selected
						form.setInputVolumeM3(null);
					}
					break;					
				case FUNCTIONALITY_CHANGE_TRAILER:
					LOG.debug("trailer");
					if (pdaAvailableFlag == null || truckTransportCatCd == null ) {
						result1 = true;
					}
					else if ( pdaAvailableFlag || 
							truckTransportCatCd.equals(Constant.TKATEGORIE_SILO) ||
							truckTransportCatCd.equals(Constant.TKATEGORIE_CONTAINER ) ){
						result1 = true;
						if (truckTransportCatCd.equals(Constant.TKATEGORIE_SILO ) ||
							truckTransportCatCd.equals(Constant.TKATEGORIE_CONTAINER ) ){							
							//it must be disabled, and cannot be selected
							form.setInputTrailerSelect(null);
						}
					}
					if ( assigned ) {
						//must be disabled, but the value must remain
						result1 = true;
					}
					break;
				case FUNCTIONALITY_CHANGE_TRUCK_TYPE:
					LOG.debug("trucktype");
					if ( characteristic == Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL ) {
						return true;
					}
					break;
				case FUNCTIONALITY_SET_CHAMBERS:
					LOG.debug("chambers");
					if (truckTransportCatCd != null && truckTransportCatCd != Constant.TKATEGORIE_SILO) {
						form.setInputNrChambers("0");
						form.setInputMaxNetWeightChamber1T("0");
						form.setInputMaxNetWeightChamber2T("0");
						result1=true;
					}
					if ( loaded ) {
						//must be disabled, but the value must remain
						result1 = true;
					}					
					break;
				case FUNCTIONALITY_SET_CHAMBER_1_WEIGHT:
					LOG.debug("chambers1weight");
					if(StringUtils.notEmpty(form.getInputNrChambers())){						
						Long nrChambers = Long.valueOf(form.getInputNrChambers());
						if (nrChambers.compareTo(new Long(1)) < 0) {
							form.setInputMaxNetWeightChamber1T("0");
							result1=true;	
						}
						else if( nrChambers.compareTo(new Long(1)) == 0 && StringUtils.empty(form.getInputMaxNetWeightChamber1T()) || form.getInputMaxNetWeightChamber1T().equals("0")) {
							//default if not set: maximum weight - tara weight
							try{								
								form.setInputMaxNetWeightChamber1T(Long.toString(Long.valueOf(form.getInputMaxMeasure()) - Long.valueOf(form.getInputTaraMeasure())));
							}
							catch (NumberFormatException e) {
								form.setInputMaxNetWeightChamber1T("0");
							}
						}
					}
					break;					
				case FUNCTIONALITY_SET_CHAMBER_2_WEIGHT:
					LOG.debug("chambers2weight");
					if(StringUtils.notEmpty(form.getInputNrChambers())){						
						Long nrChambers = Long.valueOf(form.getInputNrChambers());
						if (nrChambers.compareTo(new Long(2)) < 0) {
							form.setInputMaxNetWeightChamber2T("0");
							result1=true;	
						}
					}
					break;
				case FUNCTIONALITY_SET_PDA_FLAG:
					LOG.debug("pdaFlag");
					if ( isTruckInSession ) {
						result1 = true;
					}	
					break;
				case FUNCTIONALITY_SET_PLATENO:
					LOG.debug("plateNo");
					if ( characteristic == Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL ) {
						result1 = true;
					}	
					break;
				case FUNCTIONALITY_SET_SAP_NO:
					LOG.debug("sapNo");
					if ( characteristic == Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL ) {
						result1 = true;
					}
					if ( characteristic == Constant.VEHICLE_CHARACTERISTIC_PLANNING) {
						result1 = true;
					}
					break;
				case FUNCTIONALITY_SET_TARE_WEIGHT:
					LOG.debug("tareWeight");
					if ( characteristic == Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL ) {
						result1 = true;
					}	
					break;
				case FUNCTIONALITY_SET_MAX_WEIGHT:
					LOG.debug("maxWeight");
					if ( characteristic == Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL ) {
						result1 = true;
					}	
					break;
				case FUNCTIONALITY_SET_NUMAXLES:
					LOG.debug("numAxles");
					if ( characteristic == Constant.VEHICLE_CHARACTERISTIC_INDIVIDUAL ) {
						result1 = true;
					}	
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
