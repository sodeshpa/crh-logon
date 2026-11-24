/*
 * Copyright (c)     : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.common.WagonMaintEntity;
import com.crh.logon.persistence.entities.refdata.MaintTypeTxEntity;

/**
 * @author AdNovum Informatik AG
 */
public class WagonDetailMaintenanceBean implements Serializable {

	private static String MAINT_TYPE_G10 = "G1.0";
	private static String MAINT_TYPE_G20 = "G2.0";
	private static String MAINT_TYPE_G30 = "G3.0";
	private static String MAINT_TYPE_G40 = "G4.0";
	private static String MAINT_TYPE_G42 = "G4.2";
	private static String MAINT_TYPE_G43 = "G4.3";
	private static String MAINT_TYPE_G48 = "G4.8";
	private static String MAINT_TYPE_3M = "+3M";
	private static String MAINT_TYPE_P5 = "P5";
	private static String MAINT_TYPE_C = "C";
	
	private static List maintTypeOrderList; 
	
	private List maintTypeList;
	private List maintRhythmTypeList;
	private List repairPlantList;
	private String selectedMaintRhythmTypeCd;
	private Long damageCode;
	private String wagonNoteMaintenance;
	private Map wagonMaintMap;
	private Map maintTypeTxMap;
	private List repairShiptoList;
	private List damageCodeTxList;
	private List wagonDmgEntityList;
	private List emailAttachments;
	
	private WagonUploadAttachmentBean uploadBean;

	private TimeZone timezone;
	
	static {
		maintTypeOrderList = Arrays.asList(new String[]{"G1.0", "G2.0", "G3.0", "G4.0", "G4.2", "G4.3", "G4.8", "+3M", "P5", "C"});
	}
	
	public void clear() {
		selectedMaintRhythmTypeCd = "0";

		Iterator maintTypeIt = maintTypeOrderList.listIterator();
		
		while ( maintTypeIt.hasNext()) {
			String key = (String) maintTypeIt.next();
			
			MaintTypeTxEntity maintTypeTx = ((MaintTypeTxEntity) maintTypeTxMap.get(key));
			WagonMaintEntity newWagonMaint = new WagonMaintEntity();
			newWagonMaint.setActiveFlag(true);
			newWagonMaint.setMaintType( maintTypeTx.getMaintTypeTxPK().getMaintTypeBs() );
			MaintenanceBean mb = new MaintenanceBean(newWagonMaint,
													 newWagonMaint.getMaintType().getMaintTypeCd(),
													 maintTypeTx,
													 timezone );
			wagonMaintMap.put(key, mb);
		}
	}

	public void setTimeZone(TimeZone timezone) {
		this.timezone = timezone;
	}

	public List getMaintTypeList() {
		return maintTypeList;
	}

	public void setMaintTypeList(List maintTypeList) {
		this.maintTypeList = maintTypeList;
	}

	public List getMaintRhythmTypeList() {
		return maintRhythmTypeList;
	}

	public void setMaintRhythmTypeList(List maintRhythmTypeList) {
		this.maintRhythmTypeList = maintRhythmTypeList;
	}

	public List getRepairPlantList() {
		return repairPlantList;
	}

	public void setRepairPlantList(List repairPlantList) {
		this.repairPlantList = repairPlantList;
	}

	public String getSelectedMaintRhythmTypeCd() {
		return selectedMaintRhythmTypeCd;
	}

	public void setSelectedMaintRhythmTypeCd(String selectedMaintRhythmTypeCd) {
		this.selectedMaintRhythmTypeCd = selectedMaintRhythmTypeCd;
	}
	
	public Long getDamageCode() {
		return damageCode;
	}

	public void setDamageCode(Long damageCode) {
		this.damageCode = damageCode;
	}
	
	public String getWagonNoteMaintenance() {
		return wagonNoteMaintenance != null ? wagonNoteMaintenance : null;
	}
	
	public void setWagonNoteMaintenance(String wagonNoteMaintenance) {
		this.wagonNoteMaintenance = wagonNoteMaintenance;
	}
	
	public List getMaintTypeOrderList(){
		return WagonDetailMaintenanceBean.maintTypeOrderList;
	}

	public Map getWagonMaintMap() {
		return wagonMaintMap;
	}

	public void setWagonMaintMap(Map wagonMaintMap) {
		this.wagonMaintMap = wagonMaintMap;
	}

	public Map getMaintTypeTxMap() {
		return maintTypeTxMap;
	}

	public void setMaintTypeTxMap(Map maintTypeTxMap) {
		this.maintTypeTxMap = maintTypeTxMap;
	}

	public int getNumOfMaintTypes() {
		return maintTypeOrderList.size();
	}

	public List getRepairShiptoList() {
		return repairShiptoList;
	}

	public void setRepairShiptoList(List repairShiptoList) {
		this.repairShiptoList = repairShiptoList;
	}
	
	public List getDamageCodeTxList() {
		return damageCodeTxList;
	}

	public void setDamageCodeTxList(List damageCodeTxList) {
		this.damageCodeTxList = damageCodeTxList;
	}
	
	public List getWagonDmgEntityList() {
		return wagonDmgEntityList;
	}

	public void setWagonDmgEntityList(List wagonDmgEntityList) {
		this.wagonDmgEntityList = wagonDmgEntityList;
	}
	
	public List getEmailAttachments() {
		return emailAttachments;
	}
	
	public void setEmailAttachments(List emailAttachments) {
		this.emailAttachments = emailAttachments;
	}
	
	public WagonUploadAttachmentBean getUploadBean() {
		return uploadBean;
	}

	public void setUploadBean(WagonUploadAttachmentBean uploadBean) {
		this.uploadBean = uploadBean;
	}
	
	public MaintenanceBean getMaintenanceBeanG10() {
		if ( wagonMaintMap == null || wagonMaintMap.get(MAINT_TYPE_G10) == null ) {
			return null;
		}
		return (MaintenanceBean) wagonMaintMap.get(MAINT_TYPE_G10);
	}
	
	public MaintenanceBean getMaintenanceBeanG20() {
		if ( wagonMaintMap == null || wagonMaintMap.get(MAINT_TYPE_G20) == null ) {
			return null;
		}
		return (MaintenanceBean) wagonMaintMap.get(MAINT_TYPE_G20);
	}
	
	public MaintenanceBean getMaintenanceBeanG30() {
		if ( wagonMaintMap == null || wagonMaintMap.get(MAINT_TYPE_G30) == null ) {
			return null;
		}
		return (MaintenanceBean) wagonMaintMap.get(MAINT_TYPE_G30);
	}
	
	public MaintenanceBean getMaintenanceBeanG40() {
		if ( wagonMaintMap == null || wagonMaintMap.get(MAINT_TYPE_G40) == null ) {
			return null;
		}
		return (MaintenanceBean) wagonMaintMap.get(MAINT_TYPE_G40);
	}
	
	public MaintenanceBean getMaintenanceBeanG42() {
		if ( wagonMaintMap == null || wagonMaintMap.get(MAINT_TYPE_G42) == null ) {
			return null;
		}
		return (MaintenanceBean) wagonMaintMap.get(MAINT_TYPE_G42);
	}
	
	public MaintenanceBean getMaintenanceBeanG43() {
		if ( wagonMaintMap == null || wagonMaintMap.get(MAINT_TYPE_G43) == null ) {
			return null;
		}
		return (MaintenanceBean) wagonMaintMap.get(MAINT_TYPE_G43);
	}
	
	public MaintenanceBean getMaintenanceBeanG48() {
		if ( wagonMaintMap == null || wagonMaintMap.get(MAINT_TYPE_G48) == null ) {
			return null;
		}
		return (MaintenanceBean) wagonMaintMap.get(MAINT_TYPE_G48);
	}
	
	public MaintenanceBean getMaintenanceBean3M() {
		if ( wagonMaintMap == null || wagonMaintMap.get(MAINT_TYPE_3M) == null ) {
			return null;
		}
		return (MaintenanceBean) wagonMaintMap.get(MAINT_TYPE_3M);
	}
	
	public MaintenanceBean getMaintenanceBeanP5() {
		if ( wagonMaintMap == null || wagonMaintMap.get(MAINT_TYPE_P5) == null ) {
			return null;
		}
		return (MaintenanceBean) wagonMaintMap.get(MAINT_TYPE_P5);
	}
	
	public MaintenanceBean getMaintenanceBeanC() {
		if ( wagonMaintMap == null || wagonMaintMap.get(MAINT_TYPE_C) == null ) {
			return null;
		}
		return (MaintenanceBean) wagonMaintMap.get(MAINT_TYPE_C);
	}
	
	public MaintenanceBean getMaintenanceBean(String maintCd) {
		if ( wagonMaintMap == null || wagonMaintMap.get(maintCd) == null ) {
			return null;
		}
		return (MaintenanceBean) wagonMaintMap.get(maintCd);
	}
}
