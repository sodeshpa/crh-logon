/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity;
import com.crh.logon.persistence.entities.views.ShippBimodalRoadRailEntity;

/**
 * @author AdNovum Informatik AG
 * 
 * This bean is used to represent the editability flags and some bimodal information of an order-load on the "Loading" tab.
 */
public class ShippingOrderLoadBean implements Serializable {

	private final OrdItemAssignmtLoadEntity loadRow;
	private boolean initForEdit = false;

	private static final long serialVersionUID = 1L;

	private boolean loadingOnCurrentDay;
	private boolean enableUpdateTareMeasureData;
	private boolean enableUpdateNettoMeasureData;
	private boolean enableUpdateLoadingPoint;
	private boolean enableTransmitToWeighSystem;
	private boolean enableLoadAsPlanned;
	private boolean enableCancelWeighSystem;
	private boolean enableUpdateOrderItemLoadedQuantity;
	private boolean enableLoadContainer;
	private String[][] unitsOfMeasurement;
	
	private String wagonNumber;
	private String availableSpaceInfoBimodal;
	

	private String loadingPlannedStart;
	private String loadingRealStart;
	
	private Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfoMap;
	private Map<Long, Date> truckNextMinPlannedStartMap;
	
	/**
	 * Init final attribute {@link #loadRow}
	 * 
	 * @param loadRow - scope {@link OrdItemAssignmtLoadEntity} mainly this is the base of the whole bean
	 * 
	 */
	public ShippingOrderLoadBean(OrdItemAssignmtLoadEntity loadRow) {
		 this.loadRow = loadRow;
	}

	public boolean isLoadingOnCurrentDay() {
		return loadingOnCurrentDay;
	}
	public void setLoadingOnCurrentDay(boolean loadingOnCurrentDay) {
		this.loadingOnCurrentDay = loadingOnCurrentDay;
	}

	public boolean isEnableUpdateTareMeasureData() {
		return enableUpdateTareMeasureData;
	}
	public void setEnableUpdateTareMeasureData(boolean enableUpdateTaraMeasureData) {
		this.enableUpdateTareMeasureData = enableUpdateTaraMeasureData;
	}
	public boolean isEnableUpdateNettoMeasureData() {
		return enableUpdateNettoMeasureData;
	}
	public void setEnableUpdateNettoMeasureData(boolean enableUpdateNettoMeasureData) {
		this.enableUpdateNettoMeasureData = enableUpdateNettoMeasureData;
	}
	public boolean isEnableUpdateLoadingPoint() {
		return enableUpdateLoadingPoint;
	}
	public void setEnableUpdateLoadingPoint(boolean enableUpdateLoadingPoint) {
		this.enableUpdateLoadingPoint = enableUpdateLoadingPoint;
	}
	public boolean isEnableTransmitToWeighSystem() {
		return enableTransmitToWeighSystem;
	}
	public void setEnableTransmitToWeighSystem(boolean enableTransmitToWeighSystem) {
		this.enableTransmitToWeighSystem = enableTransmitToWeighSystem;
	}
	public boolean isEnableCancelWeighSystem() {
		return enableCancelWeighSystem;
	}
	public void setEnableCancelWeighSystem(boolean enableCancelWeighSystem) {
		this.enableCancelWeighSystem = enableCancelWeighSystem;
	}
	public boolean isEnableUpdateOrderItemLoadedQuantity() {
		return enableUpdateOrderItemLoadedQuantity;
	}
	public void setEnableUpdateOrderItemLoadedQuantity(boolean enableUpdateOrderItemLoadedQuantity) {
		this.enableUpdateOrderItemLoadedQuantity = enableUpdateOrderItemLoadedQuantity;
	}
	public boolean isEnableLoadAsPlanned() {
		return enableLoadAsPlanned;
	}
	public void setEnableLoadAsPlanned(boolean enableLoadAsPlanned) {
		this.enableLoadAsPlanned = enableLoadAsPlanned;
	}
	public boolean isEnableLoadContainer() {
		return enableLoadContainer;
	}
	public void setEnableLoadContainer(boolean enableLoadContainer) {
		this.enableLoadContainer = enableLoadContainer;
	}
	public OrdItemAssignmtLoadEntity getLoadRow() {
		return loadRow;
	}

	public String getWagonNumber() {
		return wagonNumber;
	}
	public void setWagonNumber(String wagonNumber) {
		this.wagonNumber = wagonNumber;
	}
	public String getAvailableSpaceInfoBimodal() {
		return availableSpaceInfoBimodal;
	}
	public void setAvailableSpaceInfoBimodal(String availableSpaceInfoBimodal) {
		this.availableSpaceInfoBimodal = availableSpaceInfoBimodal;
	}

	public String getLoadingPlannedStart() {
		return loadingPlannedStart;
	}
	public void setLoadingPlannedStart(String loadingPlannedStart) {
		this.loadingPlannedStart = loadingPlannedStart;
	}

	public String getLoadingRealStart() {
		return loadingRealStart;
	}

	public void setLoadingRealStart(String loadingRealStart) {
		this.loadingRealStart = loadingRealStart;
	}

	public String[][] getUnitsOfMeasurement() {
		return unitsOfMeasurement;
	}
	public void setUnitsOfMeasurement(String[][] unitsOfMeasurement) {
		this.unitsOfMeasurement = unitsOfMeasurement;
	}
	public Map<Long, ShippBimodalRoadRailEntity> getBimodalRoadAndRelatedRailInfoMap() {
		return bimodalRoadAndRelatedRailInfoMap;
	}
	public void setBimodalRoadAndRelatedRailInfoMap(Map<Long, ShippBimodalRoadRailEntity> bimodalRoadAndRelatedRailInfoMap) {
		this.bimodalRoadAndRelatedRailInfoMap = bimodalRoadAndRelatedRailInfoMap;
	}
	public Map<Long, Date> getTruckNextMinPlannedStartMap() {
		return truckNextMinPlannedStartMap;
	}
	public void setTruckNextMinPlannedStartMap(Map<Long, Date> truckNextMinPlannedStartMap) {
		this.truckNextMinPlannedStartMap = truckNextMinPlannedStartMap;
	}
	
	/**
	 * Returns true in case initialization for edit mode took place on the
	 * bean.
	 * @return the initForEdit
	 */
	public boolean isInitForEdit() {
		return initForEdit;
	}
	
	/**
	 * Further initialization postprocessing takes place if the bean is going to be used in 
	 * edit mode.
	 */
	public void initializeForEdit() {
		
		this.initForEdit = true;
		initUomPreselection();
	}
	
	/**
	 * Define the preselected uom amongst the available UOMs.
	 *  
	 * In case loaded UOM is specified already -> that needs to be preselected.
	 * 
	 * Otherwise it depends on the available choices and the material package type.
	 * 
	 * See LOGON-6715
	 * 
	 * @param ordItemAssignmentLoad
	 */
	private void initUomPreselection() {
		
		final Long packageTypeCd = this.loadRow.getLogonPackageTypeCd();
		int selectIndex = 0;
		
		if ( LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(packageTypeCd)) {

			selectIndex = getPreselectedUomBulkDumporBulkPowder();
			
		}
		else if (LogonPackageHandlingUtil.isBag(packageTypeCd)) {
			
			selectIndex = getPreselectedUomBag();		
		}
		final String PRESELECT_UOM_TRUE = Boolean.TRUE.toString();
		this.unitsOfMeasurement[selectIndex][3] = PRESELECT_UOM_TRUE;					
		
	}
	/**
	 * Returns the preselected uom for a bag row.
	 * @return
	 */
	private int getPreselectedUomBag() {
		
		int selectIndex = 0;
		
		final int NO_PRIO = -1;
		final int PRIO_MAX = 2;
		final int PRIO_MIDDLE = 1;
		final int PRIO_MIN = 0;
		
		int currPrio = NO_PRIO;
		
		for (int i = 0; i < this.unitsOfMeasurement.length; i++ ) {
			
			String[] uomOption = this.unitsOfMeasurement[i];
			String uomCd = uomOption[0];
			
			//prio: loaded uom > PAL > base uom
			if (uomCd.equals(this.loadRow.getLoadedUomCd())) {					
				selectIndex = i;
				currPrio = PRIO_MAX;
			}
			else if (currPrio < PRIO_MAX && uomCd.equals(Constant.UOM_PAL) ) {
				selectIndex = i;
				currPrio = PRIO_MIDDLE;
			}
			else if (currPrio < PRIO_MIDDLE && uomCd.equals(this.loadRow.getBaseUomCd()) ) {
				selectIndex = i;
				currPrio = PRIO_MIN;
			}				
			
		}
		return selectIndex;
	}
	
	/**
	 * Returns the preselected uom for a bulk dumped/powder row.
	 * @param currPrio
	 */
	private int getPreselectedUomBulkDumporBulkPowder() {
		
		int selectIndex = 0;
		
		final int NO_PRIO = -1;
		
		int currPrio = NO_PRIO;
		final int PRIO_MAX = 1;
		final int PRIO_MIN = 0;
		
		for (int i = 0; i < this.unitsOfMeasurement.length; i++ ) {
			
			String[] uomOption = this.unitsOfMeasurement[i];
			String uomCd = uomOption[0];
			
			//prio: loaded uom > base uom
			if (uomCd.equals(this.loadRow.getLoadedUomCd()) ) {					
				selectIndex = i;
				currPrio = PRIO_MAX;
			}
			else if (currPrio < PRIO_MAX && uomCd.equals(this.loadRow.getBaseUomCd())) {
				selectIndex = i;
				currPrio = PRIO_MIN;
			}
		}
		return selectIndex;
	}
}