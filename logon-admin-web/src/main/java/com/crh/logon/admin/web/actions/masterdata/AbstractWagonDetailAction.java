/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;

import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.beans.WagonDetailBuildBean;
import com.crh.logon.admin.web.beans.WagonDetailControlBean;
import com.crh.logon.admin.web.beans.WagonDetailCurrentOrderBean;
import com.crh.logon.admin.web.beans.WagonDetailLoadBean;
import com.crh.logon.admin.web.beans.WagonDetailMainBean;
import com.crh.logon.admin.web.beans.WagonDetailMaintenanceBean;
import com.crh.logon.admin.web.beans.WagonDetailTypeBean;
import com.crh.logon.admin.web.beans.WagonDetailWheelsetBean;
import com.crh.logon.admin.web.forms.masterdata.WagonDetailForm;
import com.crh.logon.business.services.interfaces.WagonDetailService;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.WagonInactiveReasonTxEntity;
import com.crh.logon.persistence.entities.refdata.WagonTrspCatTxEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeBsEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.persistence.entities.views.WagonDetailAssignmentEntity;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.WagonNumberUtils;

/**
 * @author AdNovum Informatik AG
 */
public abstract class AbstractWagonDetailAction extends AdminActionSupport {
	
	protected void initWagonDetailForm(WagonDetailForm helpForm, WagonEntity wagon, Locale locale) {
		
		WagonDetailService wagonDetailService = getServiceLocator().getWagonDetailService();
		
		WagonDetailCurrentOrderBean orderBean = new WagonDetailCurrentOrderBean();
		
		// 1:1 mappings
		WagonDetailMainBean wagonDetailMain = new WagonDetailMainBean();
		wagonDetailMain.setTimeZone(getUserTimezone());
		try {
			BeanUtils.copyProperties(wagonDetailMain, wagon);
		} catch (Exception e) {
			throw new LogonException(e);
		}
		// last known station
		if (wagon.getLastKnownRailwayStation() != null) {
			wagonDetailMain.setLastKnownRailwayStationId(wagon.getLastKnownRailwayStation().getRailwayStationId());
			List plantTracks = getServiceLocator().getWagonDetailService().fetchTrackListForRailwayStation(wagon.getLastKnownRailwayStation().getRailwayStationId(), locale.getLanguage());
			wagonDetailMain.setPlantTracks(plantTracks);
		}
		else{
			wagonDetailMain.setPlantTracks(new ArrayList());
		}
		
		if (wagon.getCurrentPlantTrack() != null){
			wagonDetailMain.setCurrentPlantTrackCd(wagon.getCurrentPlantTrack().getPlantTrackCd().toString());
		}
		
		WagonDetailLoadBean wagonDetailLoad = new WagonDetailLoadBean();
		try {
			BeanUtils.copyProperties(wagonDetailLoad, wagon);
		} catch (Exception e) {
			throw new LogonException(e);
		}
		WagonDetailControlBean wagonDetailControl = new WagonDetailControlBean();
		try {
			BeanUtils.copyProperties(wagonDetailControl, wagon);
		} catch (Exception e) {
			throw new LogonException(e);
		}
		WagonDetailTypeBean wagonDetailType = new WagonDetailTypeBean();
		WagonTypeBsEntity wagonType = wagon.getWagonType();
		try {
			BeanUtils.copyProperties(wagonDetailType, wagonType);
		} catch (Exception e) {
			throw new LogonException(e);
		}
		WagonDetailBuildBean wagonDetailBuild = new WagonDetailBuildBean();
		try {
			BeanUtils.copyProperties(wagonDetailBuild, wagon);
		} catch (Exception e) {
			throw new LogonException(e);
		}
		WagonDetailWheelsetBean wagonDetailWheelset = new WagonDetailWheelsetBean();
		try {
			BeanUtils.copyProperties(wagonDetailWheelset, wagon);
		} catch (Exception e) {
			throw new LogonException(e);
		}
		
		if ( helpForm.getWagonDetailMaintenance() == null ) {
			WagonDetailMaintenanceBean wagonMaintenance = new WagonDetailMaintenanceBean();
			wagonMaintenance.setTimeZone(getUserTimezone());
			wagonMaintenance.setSelectedMaintRhythmTypeCd(wagon.getMaintRhythmType() == null ? null : wagon.getMaintRhythmType().getMaintRhythmTypeCd().toString());
			helpForm.setWagonDetailMaintenance(wagonMaintenance);
		}
		
		
		// number
		String wagonNoDisplay = WagonNumberUtils.toDisplayNo(wagon.getWagonNo());
		wagonDetailMain.setWagonNo(wagonNoDisplay);
		wagonDetailControl.setWagonNo(wagonNoDisplay);
		// type
		wagonDetailType.setWagonTypeBsList(getServiceLocator().getRefDataGenericsService().findBsEntityList(WagonTypeBsEntity.class));
		if (wagonType != null) {
			initWagonTypeData(locale, wagonDetailService, wagonDetailMain, wagonDetailType,
					wagonType);
		}
		// inactive reasaon
		wagonDetailControl.setWagonInactiveReasonTxList(getRefDataGenericsService().findTxEntityList(WagonInactiveReasonTxEntity.class, locale));
		if ( (!wagon.getActiveFlag().booleanValue()) && (wagon.getWagonInactiveReason() != null) ) {
			wagonDetailControl.setWagonInactiveReasonCd(wagon.getWagonInactiveReason().getWagonInactiveReasonCd());
		}
		// keeper
		if (wagon.getWagonKeeper() != null) {
			wagonDetailMain.setWagonKeeperName(wagon.getWagonKeeper().getWagonKeeperName());
			wagonDetailControl.setWagonKeeperId(wagon.getWagonKeeper().getWagonKeeperId());
		}
		wagonDetailControl.setWagonKeeperList(wagonDetailService.getWagonKeeperList());
		// shippingpoint
		if (wagon.getReturnShiptoSapBp() != null) {
			wagonDetailMain.setReturnShipToName(wagon.getReturnShiptoSapBp().getName1());
			wagonDetailControl.setReturnShiptoSapBpId(wagon.getReturnShiptoSapBp().getSapBpId());
		}
		wagonDetailControl.setShiptoSapBpList(wagonDetailService.getShiptoSapBpList());
		// load
		if (wagon.getMaxGrossWeightT() != null) {
			wagonDetailControl.setFromDefaultMaxLoadT(Boolean.FALSE);
		}
		else {
			wagonDetailControl.setFromDefaultMaxLoadT(Boolean.TRUE);
			wagonDetailControl.setMaxGrossWeightT(wagonType.getMaxGrossWeightT() != null ? wagonType.getMaxGrossWeightT().toString() : null);
		}
		if (wagon.getVolumeM3() != null) {
			wagonDetailControl.setFromDefaultVolumeM3(Boolean.FALSE);
		}
		else {
			wagonDetailControl.setFromDefaultVolumeM3(Boolean.TRUE);
			wagonDetailControl.setVolumeM3(wagonType.getVolumeM3() != null ? wagonType.getVolumeM3().toString() : null);
		}
		if (wagon.getLimitCreditNoteT() != null) {
			wagonDetailControl.setFromDefaultLimitCreditNoteT(Boolean.FALSE);
		}
		else {
			wagonDetailControl.setFromDefaultLimitCreditNoteT(Boolean.TRUE);
			wagonDetailControl.setLimitCreditNoteT(wagonType.getLimitCreditNoteT() != null ? wagonType.getLimitCreditNoteT().toString() : null);
		}
		helpForm.setWagonId(wagon.getWagonId());
		helpForm.setOrderBean(orderBean);
		helpForm.setWagonDetailMain(wagonDetailMain);
		helpForm.setWagonDetailLoad(wagonDetailLoad);
		helpForm.setWagonDetailControl(wagonDetailControl);
		helpForm.setWagonDetailType(wagonDetailType);
		helpForm.setWagonDetailBuild(wagonDetailBuild);
		helpForm.setWagonDetailWheelset(wagonDetailWheelset);
		
	}

	/**
	 * @param locale
	 * @param wagonDetailService
	 * @param wagonDetailMain
	 * @param wagonDetailType
	 * @param wagonType
	 */
	protected void initWagonTypeData(Locale locale, WagonDetailService wagonDetailService,
			WagonDetailMainBean wagonDetailMain, WagonDetailTypeBean wagonDetailType,
			WagonTypeBsEntity wagonType) {
		wagonDetailMain.setWagonTypeName(wagonType.getWagonTypeName());
		wagonDetailType.setWagonTypeCd(wagonType.getWagonTypeCd());
		WagonTrspCatTxEntity transport = getServiceLocator().getRefDataGenericsService().findTxEntity(WagonTrspCatTxEntity.class, locale, wagonType.getWagonTrspCat().getWagonTrspCatCd());
		wagonDetailType.setWagonTrspCatTx(transport);
		
		initNhmData(locale, wagonDetailService, wagonDetailType, wagonType);
	}

	/**
	 * @param locale
	 * @param wagonDetailService
	 * @param wagonDetailType
	 * @param wagonType
	 */
	protected void initNhmData(Locale locale, WagonDetailService wagonDetailService,
			WagonDetailTypeBean wagonDetailType, WagonTypeBsEntity wagonType) {
		SapMaterialDescEntity emptyMaterialDesc = wagonDetailService.getSapMaterialDescEntityForSapNo(wagonType.getEmptySapMaterialNo(), locale.getLanguage());
		wagonDetailType.setNhmEmptyMat(emptyMaterialDesc);
		
		if (emptyMaterialDesc != null) {
			String nhmEmpty = wagonDetailService.getNhmForSapMaterialId(emptyMaterialDesc.getSapMaterialId());
			wagonDetailType.setNhmEmpty(nhmEmpty);
		}
		SapMaterialDescEntity repairMaterialDesc = wagonDetailService.getSapMaterialDescEntityForSapNo(wagonType.getRepairSapMaterialNo(), locale.getLanguage());
		wagonDetailType.setNhmRepairMat(repairMaterialDesc);
		
		if (repairMaterialDesc != null) {
			String nhmRepair = wagonDetailService.getNhmForSapMaterialId(repairMaterialDesc.getSapMaterialId());
			wagonDetailType.setNhmEmpty(nhmRepair);
		}
		SapMaterialDescEntity maintenanceMaterialDesc = wagonDetailService.getSapMaterialDescEntityForSapNo(wagonType.getMaintenanceSapMaterialNo(), locale.getLanguage());
		wagonDetailType.setNhmMaintenanceMat(maintenanceMaterialDesc);
		if (maintenanceMaterialDesc != null) {
			String nhmMaintenance = wagonDetailService.getNhmForSapMaterialId(maintenanceMaterialDesc.getSapMaterialId());
			wagonDetailType.setNhmEmpty(nhmMaintenance);
		}
	}
	
	/**
	 * @param request
	 * @param helpForm
	 * @param wagonDetailService
	 * @param wagon
	 */
	protected void initAssignmentDependentData(HttpServletRequest request, WagonDetailForm helpForm,
			WagonDetailService wagonDetailService, WagonEntity wagon) {
		
		//find the assignment part load --> this will resolve the current assignment
		List assignments = wagonDetailService.getCurrentAssignmentView(wagon.getWagonId(), request.getLocale().getLanguage());
		if (assignments != null && !assignments.isEmpty()){
			
			WagonDetailAssignmentEntity assignment = (WagonDetailAssignmentEntity) assignments.get(0);
			
			
			//station from is resolved from the assignment part load where it was loaded
			ShippingpointEntity shippingpointEntity = getShippingPointService().loadShippingPoint(assignment.getShippingpointId());
			if (shippingpointEntity != null) {				
				helpForm.getWagonDetailMain().setStationFrom(shippingpointEntity.getRailwayStation());
			}
			
			helpForm.getWagonDetailMain().setPlannedDepartureDate(
					getUserTimezone().fmtAsDayMonthYear4HourMinute(assignment.getDrivetoPlannedStart()));
			helpForm.getWagonDetailMain().setDepartureDate(
					getUserTimezone().fmtAsDayMonthYear4HourMinute(assignment.getDrivetoRealStart()));
			
			Long shiptoId = assignment.getShiptoId();
			if (shiptoId != null) {
				ShiptoEntity shipto = getDispokarteService().fetchShipto(shiptoId);
				RailwayStationEntity stationTo = shipto.getShiptoSapBp().getRailwayStation();
				helpForm.getWagonDetailMain().setStationTo(stationTo);
			}
			
			helpForm.getWagonDetailMain().setPlannedArrivalDate(
					getUserTimezone().fmtAsDayMonthYear4HourMinute(assignment.getUnloadPlannedStart()));
			helpForm.getWagonDetailMain().setArrivalDate(
					getUserTimezone().fmtAsDayMonthYear4HourMinute(assignment.getUnloadRealStart()));
					
			helpForm.getOrderBean().setOrderStatus(assignment.getStatusTxt());
			helpForm.getOrderBean().setOrderStatusSince(
					getUserTimezone().fmtAsDayMonthYear4HourMinute(assignment.getCtlModTs()));
			helpForm.getOrderBean().setRailHoldBackReOrderFlag(assignment.getRailHoldBackReOrderFlag());
			Date now = new Date();
			helpForm.getOrderBean().setOrderStatusSinceTimeString(
					assignment.getCtlModTs() != null
							? DateTimeUtils.getFormattedTimeLengthFromMillis(now.getTime() - assignment.getCtlModTs().getTime())
							: null);
			helpForm.getOrderBean().setOrderNumber(assignment.getOrderNumber() != null ? assignment.getOrderNumber().toString() : "");
			helpForm.getOrderBean().setOrderId(assignment.getOrderId());
			helpForm.getOrderBean().setOrderStatusCd(assignment.getOrderStatusCd());
			helpForm.getOrderBean().setDistributionDestCd(assignment.getDistributionDestCd());
			helpForm.getOrderBean().setDeliveryTypeCd(assignment.getDeliveryTypeCd());
			
		}
		List loadList = getServiceLocator().getWagonDetailService().getLoadList(wagon.getWagonId(),request.getLocale().getLanguage());
		helpForm.getWagonDetailMain().setLoadList(loadList);
	}
}
