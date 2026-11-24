/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.beans;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;

import com.crh.logon.persistence.entities.refdata.WagonTrspCatTxEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeBsEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.web.utils.ResourceKey;
import com.crh.logon.web.validators.Required;

/**
 * @author AdNovum Informatik AG
 */
public class WagonDetailTypeBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private final PropertyChangeSupport PROPERTY_CHANGE_SUPPORT = new PropertyChangeSupport(this);
	private Long wagonTypeCd;
	private List wagonTypeBsList;
	private WagonTypeBsEntity wagonType;
	private WagonTrspCatTxEntity wagonTrspCatTx;
	
	private SapMaterialDescEntity nhmEmptyMat;
	private SapMaterialDescEntity nhmRepairMat;
	private SapMaterialDescEntity nhmMaintenanceMat;
	
	private String nhmEmpty;
	private String nhmRepair;
	private String nhmMaintenance;
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		
		PROPERTY_CHANGE_SUPPORT.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
    	
    	PROPERTY_CHANGE_SUPPORT.removePropertyChangeListener(listener);
    }

	@ResourceKey("wagon.wagontype")
	@Required
	public WagonTypeBsEntity getWagonType() {
		
		if (wagonTypeCd == null) {
			
			return null;
		}
		if ( (this.wagonType == null) || (!wagonTypeCd.equals(wagonType.getWagonTypeCd())) ) { 
			for (Iterator it=wagonTypeBsList.iterator();it.hasNext();) {
				WagonTypeBsEntity entity = (WagonTypeBsEntity)it.next();
				if (entity.getWagonTypeCd().equals(wagonTypeCd)) {
					this.wagonType = entity;
					break;
				}
			}
		}
		
		return this.wagonType;
	}
	
	public Long getWagonTypeCd() {
		return wagonTypeCd;
	}
	public void setWagonTypeCd(Long wagonTypeCd) {
		Long previousValue = this.wagonTypeCd;
		this.wagonTypeCd = wagonTypeCd;
		PROPERTY_CHANGE_SUPPORT.firePropertyChange("wagonTypeCd", previousValue, wagonTypeCd);
	}
	public List getWagonTypeBsList() {
		return wagonTypeBsList;
	}
	public void setWagonTypeBsList(List wagonTypeBsList) {
		this.wagonTypeBsList = wagonTypeBsList;
	}
	
	public String getWagonTypeWagonUicClassCd() {
		return (getWagonType() != null) ? getWagonType().getWagonUicClassCd() : null;
	}
	public String getWagonTypeAxisCount() {
		return (getWagonType() != null) ? ConvertUtils.convert(getWagonType().getAxisCount()) : null;
	}

	public String getWagonTypeMaxLoadT() {
		return (getWagonType() != null) ? ConvertUtils.convert(getWagonType().getMaxGrossWeightT()) : null;
	}
	public String getWagonTypeVolumeM3() {
		return (getWagonType() != null) ? ConvertUtils.convert(getWagonType().getVolumeM3()) : null;
	}
	public String getWagonTypeLimitCreditNoteT() {
		return (getWagonType() != null) ? ConvertUtils.convert(getWagonType().getLimitCreditNoteT()) : null;
	}

	public WagonTrspCatTxEntity getWagonTrspCatTx() {
		return wagonTrspCatTx;
	}

	public void setWagonTrspCatTx(WagonTrspCatTxEntity wagonTrspCatTx) {
		this.wagonTrspCatTx = wagonTrspCatTx;
	}

	public SapMaterialDescEntity getNhmEmptyMat() {
		return nhmEmptyMat;
	}

	public void setNhmEmptyMat(SapMaterialDescEntity nhmEmptyMat) {
		this.nhmEmptyMat = nhmEmptyMat;
	}

	public SapMaterialDescEntity getNhmRepairMat() {
		return nhmRepairMat;
	}

	public void setNhmRepairMat(SapMaterialDescEntity nhmRepairMat) {
		this.nhmRepairMat = nhmRepairMat;
	}

	public SapMaterialDescEntity getNhmMaintenanceMat() {
		return nhmMaintenanceMat;
	}

	public void setNhmMaintenanceMat(SapMaterialDescEntity nhmMaintenanceMat) {
		this.nhmMaintenanceMat = nhmMaintenanceMat;
	}

	public String getNhmEmpty() {
		return nhmEmpty;
	}

	public void setNhmEmpty(String nhmEmpty) {
		this.nhmEmpty = nhmEmpty;
	}

	public String getNhmRepair() {
		return nhmRepair;
	}

	public void setNhmRepair(String nhmRepair) {
		this.nhmRepair = nhmRepair;
	}

	public String getNhmMaintenance() {
		return nhmMaintenance;
	}

	public void setNhmMaintenance(String nhmMaintenance) {
		this.nhmMaintenance = nhmMaintenance;
	}	


}
