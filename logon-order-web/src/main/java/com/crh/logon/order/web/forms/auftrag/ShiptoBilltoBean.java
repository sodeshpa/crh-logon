/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;

import com.crh.logon.persistence.entities.sap.SapBpEntity;

/**
 * @author AdNovum Informatik AG
 */
public class ShiptoBilltoBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private SapBpEntity billtoSapBpEntity;
	private boolean useAlteredName;
	private String alteredName;
	
	public ShiptoBilltoBean(SapBpEntity billtoEntity, boolean useAlteredName, String alteredName) {
		super();
		this.billtoSapBpEntity = billtoEntity;
		this.useAlteredName = useAlteredName;
		this.alteredName = alteredName;
	}
	
	public SapBpEntity getBilltoSapBpEntity() {
		return billtoSapBpEntity;
	}

	public void setBilltoSapBpEntity(SapBpEntity billtoSapBpEntity) {
		this.billtoSapBpEntity = billtoSapBpEntity;
	}

	public void setAlteredName(String alteredName) {
		this.alteredName = alteredName;
	}
	
	public void setUseAlteredName(boolean useAlteredName) {
		this.useAlteredName = useAlteredName;
	}
	
	public String getName() {
		if (useAlteredName) {
			return alteredName;			
		}
		else {
			return billtoSapBpEntity.getName1();
		}
			
	}
}
