/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.common.WagonKeeperEntity;
import com.crh.logon.persistence.entities.refdata.WagonInactiveReasonBsEntity;
import com.crh.logon.persistence.entities.refdata.WagonInactiveReasonTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.ResourceKey;
import com.crh.logon.web.validators.RegexValidation;
import com.crh.logon.web.validators.Required;
import com.crh.logon.web.validators.StrutsValidator;
import com.crh.logon.web.validators.TypeValidation;

/**
 * @author AdNovum Informatik AG
 */
public class WagonDetailControlBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String wagonNo;
    private Boolean activeFlag;
    private Long wagonInactiveReasonCd;
    private List wagonInactiveReasonTxList;
    private WagonInactiveReasonBsEntity wagonInactiveReason;
    private String tareWeightT;
	private String maxGrossWeightT;
    private String volumeM3;
    private String writtenTareWeightT;
    private String limitCreditNoteT;
    private String loadingLimitAT;
    private String loadingLimitBT;
    private String loadingLimitCT;
    private String loadingLimitDT;
    private String wheelbaseMm;
    private String wheelbaseBppMm;
    private String bufferLengthMm;
    private String undercarriageManufacturer;
    private String undercarriageConstYear;
    private String superstructureManufacturer;
    private String superstructureConstYear;
    private String unloadingSystem;
    private String drawingType;
    private String sapVehicleNo;
    
    private Long wagonKeeperId;
    private List wagonKeeperList;
    private WagonKeeperEntity wagonKeeper;
    
    private Boolean fromDefaultMaxLoadT = Boolean.TRUE;
    private Boolean fromDefaultVolumeM3 = Boolean.TRUE;
    private Boolean fromDefaultLimitCreditNoteT = Boolean.TRUE;
    
    private Long returnShiptoSapBpId;
    private List shiptoSapBpList;
    private SapBpEntity returnShiptoSapBp;
    
    @ResourceKey("wagon.wagonno")
	@Required
	@RegexValidation(formatDesc="xxxx xxxx xxx-x", regex="^(?:\\d{4}\\s\\d{4}\\s\\d{3}-\\d{1})|(?:\\d{12})$")
	public String getWagonNo() {
		return wagonNo;
	}
	public void setWagonNo(String wagonNo) {
		this.wagonNo = wagonNo;
	}
	public Boolean getActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
	
	public Long getWagonInactiveReasonCd() {
		return wagonInactiveReasonCd;
	}
	public void setWagonInactiveReasonCd(Long wagonInactiveReasonCd) {
		this.wagonInactiveReasonCd = wagonInactiveReasonCd;
	}
	
	public List getWagonInactiveReasonTxList() {
		return wagonInactiveReasonTxList;
	}
	public void setWagonInactiveReasonTxList(List wagonInactiveReasonTxList) {
		this.wagonInactiveReasonTxList = wagonInactiveReasonTxList;
	}
	public WagonInactiveReasonBsEntity getWagonInactiveReason() {
		
		if (wagonInactiveReasonCd == null) {
			
			return null;
		}
		if ( (this.wagonInactiveReason == null) || (!wagonInactiveReasonCd.equals(this.wagonInactiveReason.getWagonInactiveReasonCd())) ) {
			// find the entity in the list
			this.wagonInactiveReason = null;
			if ((wagonInactiveReasonCd != null) && (shiptoSapBpList != null)) {
				for (Iterator it=wagonInactiveReasonTxList.iterator(); it.hasNext();) {
					WagonInactiveReasonTxEntity wagonInactiveReasonTx = (WagonInactiveReasonTxEntity)it.next();
					if (wagonInactiveReasonTx.getWagonInactiveReasonTxPK().getWagonInactiveReasonBs().getWagonInactiveReasonCd().equals(wagonInactiveReasonCd)) {
						this.wagonInactiveReason = wagonInactiveReasonTx.getWagonInactiveReasonTxPK().getWagonInactiveReasonBs();
						break;
					}
				}
			}
		}
		
		return this.wagonInactiveReason; 
	}
	@ResourceKey("wagon.tare_weight")
	@Required
	public String getTareWeightT() {
		return tareWeightT;
	}
	
	public void setTareWeightT(String tareWeightT) {
		this.tareWeightT = tareWeightT;
	}
	
	@ResourceKey("wagon.maxload")
	public String getMaxGrossWeightT() {
		return maxGrossWeightT;
	}
	public void setMaxGrossWeightT(String maxGrossWeightT) {
		this.maxGrossWeightT = maxGrossWeightT;
	}
	
	@ResourceKey("wagon.volume")
	@TypeValidation(java.math.BigDecimal.class)
	public String getVolumeM3() {
		return volumeM3;
	}
	public void setVolumeM3(String volumeM3) {
		this.volumeM3 = volumeM3;
	}
	
	@ResourceKey("wagon.written_tare_weight")
	public String getWrittenTareWeightT() {
		return writtenTareWeightT;
	}
	public void setWrittenTareWeightT(String writtenTareWeightT) {
		this.writtenTareWeightT = writtenTareWeightT;
	}
	@ResourceKey("wagon.limit_credit_note")
	public String getLimitCreditNoteT() {
		return limitCreditNoteT;
	}
	public void setLimitCreditNoteT(String limitCreditNoteT) {
		this.limitCreditNoteT = limitCreditNoteT;
	}
	
	@ResourceKey("wagon.load_limit_a")
	public String getLoadingLimitAT() {
		return loadingLimitAT;
	}
	public void setLoadingLimitAT(String loadingLimitAT) {
		this.loadingLimitAT = loadingLimitAT;
	}
	
	@ResourceKey("wagon.load_limit_b")
	public String getLoadingLimitBT() {
		return loadingLimitBT;
	}
	public void setLoadingLimitBT(String loadingLimitBT) {
		this.loadingLimitBT = loadingLimitBT;
	}
	
	@ResourceKey("wagon.load_limit_c")
	public String getLoadingLimitCT() {
		return loadingLimitCT;
	}
	public void setLoadingLimitCT(String loadingLimitCT) {
		this.loadingLimitCT = loadingLimitCT;
	}
	
	@ResourceKey("wagon.load_limit_d")
	public String getLoadingLimitDT() {
		return loadingLimitDT;
	}
	public void setLoadingLimitDT(String loadingLimitDT) {
		this.loadingLimitDT = loadingLimitDT;
	}
	
	@ResourceKey("wagon.wheelbase")
	@TypeValidation(java.lang.Long.class)
	public String getWheelbaseMm() {
		return wheelbaseMm;
	}
	public void setWheelbaseMm(String wheelbaseMm) {
		this.wheelbaseMm = wheelbaseMm;
	}
	
	@ResourceKey("wagon.wheelbase_bpp")
	@TypeValidation(java.lang.Long.class)
	public String getWheelbaseBppMm() {
		return wheelbaseBppMm;
	}
	public void setWheelbaseBppMm(String wheelbaseBppMm) {
		this.wheelbaseBppMm = wheelbaseBppMm;
	}
	
	@ResourceKey("wagon.buffer_length")
	@TypeValidation(java.lang.Long.class)
	public String getBufferLengthMm() {
		return bufferLengthMm;
	}
	public void setBufferLengthMm(String bufferLengthMm) {
		this.bufferLengthMm = bufferLengthMm;
	}
	public String getUndercarriageManufacturer() {
		return undercarriageManufacturer;
	}
	public void setUndercarriageManufacturer(String undercarriageManufacturer) {
		this.undercarriageManufacturer = undercarriageManufacturer;
	}
	
	@TypeValidation(java.lang.Long.class)
	public String getUndercarriageConstYear() {
		return undercarriageConstYear;
	}
	public void setUndercarriageConstYear(String undercarriageConstYear) {
		this.undercarriageConstYear = undercarriageConstYear;
	}
	public String getSuperstructureManufacturer() {
		return superstructureManufacturer;
	}
	public void setSuperstructureManufacturer(String superstructureManufacturer) {
		this.superstructureManufacturer = superstructureManufacturer;
	}
	
	@TypeValidation(java.lang.Long.class)
	public String getSuperstructureConstYear() {
		return superstructureConstYear;
	}
	public void setSuperstructureConstYear(String superstructureConstYear) {
		this.superstructureConstYear = superstructureConstYear;
	}
	public String getUnloadingSystem() {
		return unloadingSystem;
	}
	public void setUnloadingSystem(String unloadingSystem) {
		this.unloadingSystem = unloadingSystem;
	}
	public String getDrawingType() {
		return drawingType;
	}
	public void setDrawingType(String drawingType) {
		this.drawingType = drawingType;
	}
	public Long getWagonKeeperId() {
		return wagonKeeperId;
	}
	public void setWagonKeeperId(Long wagonKeeperId) {
		this.wagonKeeperId = wagonKeeperId;
	}
	public List getWagonKeeperList() {
		return wagonKeeperList;
	}
	public void setWagonKeeperList(List wagonKeeperList) {
		this.wagonKeeperList = wagonKeeperList;
	}
	
	@ResourceKey("wagon.keeper_name")
	@Required
	public WagonKeeperEntity getWagonKeeper() {
		
		if (wagonKeeperId == null) {
			
			return null;
		}
		if ( (this.wagonKeeper == null) || (!wagonKeeperId.equals(this.wagonKeeper.getWagonKeeperId())) ) {
			// find the wagonkeeper entity in the list
			this.wagonKeeper = null;
			if ((wagonKeeperId != null) && (wagonKeeperList != null)) {
				for (Iterator it=wagonKeeperList.iterator(); it.hasNext();) {
					WagonKeeperEntity wagonKeeper = (WagonKeeperEntity)it.next();
					if (wagonKeeper.getWagonKeeperId().equals(wagonKeeperId)) {
						
						this.wagonKeeper = wagonKeeper;
						break;
					}
				}
			}
		}
		
		return this.wagonKeeper; 
	}
	
	public Boolean getWagonKeeperMaintenanceEmailFlag() {
		
		return (getWagonKeeper() != null) ? getWagonKeeper().getMaintenanceEmailFlag() : null;
	}
	
	public String getWagonKeeperEmailAddresses() {
		
		return (getWagonKeeper() != null) ? getWagonKeeper().getEmailAddresses() : null;
	}
	

	public Long getReturnShiptoSapBpId() {
		return returnShiptoSapBpId;
	}
	public void setReturnShiptoSapBpId(Long returnShiptoSapBpId) {
		this.returnShiptoSapBpId = returnShiptoSapBpId;
	}
	public List getShiptoSapBpList() {
		return shiptoSapBpList;
	}
	public void setShiptoSapBpList(List shiptoSapBbList) {
		this.shiptoSapBpList = shiptoSapBbList;
	}
	@ResourceKey("wagon.return_shipto")
	public SapBpEntity getReturnShiptoSapBp() {
		
		if (returnShiptoSapBpId == null) {
			
			return null;
		}
		if ( (this.returnShiptoSapBp == null) || (!returnShiptoSapBpId.equals(this.returnShiptoSapBp.getSapBpId())) ) {
			// find the entity in the list
			this.returnShiptoSapBp = null;
			if ((returnShiptoSapBpId != null) && (shiptoSapBpList != null)) {
				for (Iterator it=shiptoSapBpList.iterator(); it.hasNext();) {
					ShiptoSareaEntity shiptoSareaEntity = (ShiptoSareaEntity)it.next();
					ShiptoEntity shiptoEntity = shiptoSareaEntity.getShipto();
					if (shiptoEntity != null) {
						if (shiptoEntity.getShiptoSapBp().getSapBpId().equals(returnShiptoSapBpId)) {
							this.returnShiptoSapBp = shiptoSareaEntity.getShipto().getShiptoSapBp();
							break;
						}
					}
				}
			}
		}
		
		return this.returnShiptoSapBp; 
	}
	public Boolean getFromDefaultMaxLoadT() {
		return fromDefaultMaxLoadT;
	}
	public void setFromDefaultMaxLoadT(Boolean fromDefaultMaxLoadT) {
		this.fromDefaultMaxLoadT = fromDefaultMaxLoadT;
	}
	public Boolean getFromDefaultVolumeM3() {
		return fromDefaultVolumeM3;
	}
	public void setFromDefaultVolumeM3(Boolean fromDefaultVolumeM3) {
		this.fromDefaultVolumeM3 = fromDefaultVolumeM3;
	}
	public Boolean getFromDefaultLimitCreditNoteT() {
		return fromDefaultLimitCreditNoteT;
	}
	public void setFromDefaultLimitCreditNoteT(Boolean fromDefaultLimitCreditNoteT) {
		this.fromDefaultLimitCreditNoteT = fromDefaultLimitCreditNoteT;
	}
	
	@StrutsValidator
	public void validate(ActionMapping mapping, HttpServletRequest request) {
		ActionSupport.checkFieldMaxValue(request, getLimitCreditNoteT(), WagonEntity.DBSIZE_LIMIT_CREDIT_NOTE_T, WagonEntity.DBSIZE_PRECISION_LIMIT_CREDIT_NOTE_T, "wagon.limit_credit_note");
		ActionSupport.checkFieldMaxValue(request, getLoadingLimitAT(), WagonEntity.DBSIZE_LOADING_LIMIT_A_T, WagonEntity.DBSIZE_PRECISION_LOADING_LIMIT_A_T, "wagon.load_limit_a");
		ActionSupport.checkFieldMaxValue(request, getLoadingLimitBT(), WagonEntity.DBSIZE_LOADING_LIMIT_B_T, WagonEntity.DBSIZE_PRECISION_LOADING_LIMIT_B_T, "wagon.load_limit_b");
		ActionSupport.checkFieldMaxValue(request, getLoadingLimitCT(), WagonEntity.DBSIZE_LOADING_LIMIT_C_T, WagonEntity.DBSIZE_PRECISION_LOADING_LIMIT_C_T, "wagon.load_limit_c");
		ActionSupport.checkFieldMaxValue(request, getLoadingLimitDT(), WagonEntity.DBSIZE_LOADING_LIMIT_D_T, WagonEntity.DBSIZE_PRECISION_LOADING_LIMIT_D_T, "wagon.load_limit_d");
		ActionSupport.checkFieldMaxValue(request, getMaxGrossWeightT(), WagonEntity.DBSIZE_MAX_GROSS_WEIGHT_T, WagonEntity.DBSIZE_PRECISION_MAX_GROSS_WEIGHT_T, "wagon.maxload");
		ActionSupport.checkFieldMaxValue(request, getTareWeightT(), WagonEntity.DBSIZE_TARE_WEIGHT_T, WagonEntity.DBSIZE_PRECISION_TARE_WEIGHT_T, "wagon.tare_weight");
		ActionSupport.checkFieldMaxValue(request, getWrittenTareWeightT(), WagonEntity.DBSIZE_WRITTEN_TARE_WEIGHT_T, WagonEntity.DBSIZE_PRECISION_WRITTEN_TARE_WEIGHT_T, "wagon.written_tare_weight");
		if (activeFlag.equals(Boolean.FALSE) && wagonInactiveReason == null) {
			MessageUtils.addActionWarning(request, "wagondetail.error.inactivereason");
		}
	}


	public String getSapVehicleNo() {
		return sapVehicleNo;
	}


	public void setSapVehicleNo(String sapVehicleNo) {
		this.sapVehicleNo = sapVehicleNo;
	}


	/**
	 * Deletes the properties, which must be empty for a new wagon.
	 */
	public void resetNewWagonProperties() {
		sapVehicleNo = "";
	}

}