/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.UserProvider;

public class OrderSearchExportUtil {

	public static OrderSearchExportMetaData createOrderSearchExportMetaData(Map<String, String> searchCriteria, Locale userLocale, TimeZone userTimeZone, ServiceLocator serviceLocator) {
		OrderSearchExportMetaData emd = new OrderSearchExportMetaData();
		emd.setLangCd(userLocale.getLanguage());
		if (!StringUtils.empty(searchCriteria.get("orderNumber"))) {
			String validatedOrderNo = searchCriteria.get("orderNumber");
			validatedOrderNo = validatedOrderNo.replaceAll("[^\\d]", "");
			emd.setOrderNumber(Long.valueOf(validatedOrderNo));
		}
		if (!StringUtils.empty(searchCriteria.get("soldtoSapBpId"))) {
			emd.setSoldtoSapBpId(Long.valueOf(searchCriteria.get("soldtoSapBpId")));
		}
		if (!StringUtils.empty(searchCriteria.get("shiptoSapBpId"))) {
			emd.setShiptoSapBpId(Long.valueOf(searchCriteria.get("shiptoSapBpId")));
		}
		if (!StringUtils.empty(searchCriteria.get("billtoSapBpId"))) {
			emd.setBilltoSapBpId(Long.valueOf(searchCriteria.get("billtoSapBpId")));
		}
		if (!StringUtils.empty(searchCriteria.get("sapMaterialId"))) {
			emd.setSapMaterialId(Long.valueOf(searchCriteria.get("sapMaterialId")));
		}
		
		if (!StringUtils.empty(searchCriteria.get("deliveryFromDat"))) {
			try {
				Date fromDate = DateTimeUtils.parseDayMonthYearInGMTZone(searchCriteria.get("deliveryFromDat"));
				fromDate = DateTimeUtils.getMorningOfDayInGMTZone(fromDate);
				emd.setDeliveryFromDat(fromDate);
			}
			catch (ParseException e) {
				throw new LogonException("Invalid date format " + searchCriteria.get("deliveryFromDat"), Reason.RES_ERROR_INVALID_DATE_FORMAT);
			}
			
		}
		if (!StringUtils.empty(searchCriteria.get("deliveryToDat"))) {
			try {
				Date toDate = DateTimeUtils.parseDayMonthYearInGMTZone(searchCriteria.get("deliveryToDat"));
				toDate = DateTimeUtils.getMidnightOfDayInGMTZone(toDate);
				emd.setDeliveryToDat(toDate);
			}
			catch (ParseException e) {
				throw new LogonException("Invalid date format " + searchCriteria.get("deliveryFromDat"), Reason.RES_ERROR_INVALID_DATE_FORMAT);
			}
		}
		if (!StringUtils.empty(searchCriteria.get("deliveryTypeCdList"))) {
			emd.setDeliveryTypeCdList(Arrays.asList(searchCriteria.get("deliveryTypeCdList").split(",")));
		}
		if (!StringUtils.empty(searchCriteria.get("destinationCdList"))) {
			emd.setDestinationCdList(Arrays.asList(searchCriteria.get("destinationCdList").split(",")));
		}
		if (!StringUtils.empty(searchCriteria.get("creationTypeList"))) {
			emd.setOrderCreationTypeCdList(Arrays.asList(searchCriteria.get("creationTypeList").split(",")));
		}				
		if (!StringUtils.empty(searchCriteria.get("shippingpointId"))) {
			emd.setShippingpointId(Long.valueOf(searchCriteria.get("shippingpointId")));
		}
		if (!StringUtils.empty(searchCriteria.get("poolId"))) {
			emd.setPoolId(Long.valueOf(searchCriteria.get("poolId")));
		}
		
		if (!StringUtils.empty(searchCriteria.get("execCarrierId"))) {
			emd.setExecCarrierId(Long.valueOf(searchCriteria.get("execCarrierId")));
		}
		
		if (!StringUtils.empty(searchCriteria.get("carrierIdList"))) {
			emd.setCarrierIdList(Arrays.asList(searchCriteria.get("carrierIdList").split(",")));
		}
		if (!StringUtils.empty(searchCriteria.get("statusList"))) {
			emd.setStatusList(Arrays.asList(searchCriteria.get("statusList").split(",")));
		}
		if (!StringUtils.empty(searchCriteria.get("sareaId"))) {
			emd.setSareaId(searchCriteria.get("sareaId"));
		}
		if (!StringUtils.empty(searchCriteria.get("shiptoSofficeCd"))) {
			emd.setShiptoSofficeCd(searchCriteria.get("shiptoSofficeCd"));
		}
		if (!StringUtils.empty(searchCriteria.get("shiptoSgroupCd"))) {
			emd.setShiptoSgroupCd(searchCriteria.get("shiptoSgroupCd"));
		}
		if (!StringUtils.empty(searchCriteria.get("plantCdList"))) {
			emd.setPlantCdList((Arrays.asList(searchCriteria.get("plantCdList").split(","))));
		}
		if (!StringUtils.empty(searchCriteria.get("ldsDeliveryNoteNo"))) {
			emd.setLdsDeliveryNoteNo(searchCriteria.get("ldsDeliveryNoteNo"));
		}
		if (!StringUtils.empty(searchCriteria.get("matPackagingType"))) {
			emd.setMaterialPackageCd(searchCriteria.get("matPackagingType"));
		}
		if ( (!StringUtils.empty(searchCriteria.get("vehicleType")) &&
				(!StringUtils.empty(searchCriteria.get("vehicleId")))) ) {
			if (searchCriteria.get("vehicleType").equals("truck")) {
				emd.setTruckId(Long.valueOf(searchCriteria.get("vehicleId")));
			}
			else if (searchCriteria.get("vehicleType").equals("wagon")) {
				emd.setWagonId(Long.valueOf(searchCriteria.get("vehicleId")));
			}
			else {				
				throw new LogonException("Unknown vehicle type " + searchCriteria.get("vehicleType"), Reason.RES_ERROR_IMPL);
			}
		}
		long userId = 0; 
		UserEntity user = UserProvider.getUser();
		if(user != null) {
			userId = user.getUserId();
		}
		emd.init(serviceLocator, userId);
		return emd;
	}
}
