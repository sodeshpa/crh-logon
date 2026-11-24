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
public class PayerSapBpBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private SapBpEntity sapBp;
	private boolean useAlteredName;
	private String alteredName;
	
	public PayerSapBpBean(SapBpEntity sapBp, boolean useAlteredName, String alteredName) {
		super();
		this.sapBp = sapBp;
		this.useAlteredName = useAlteredName;
		this.alteredName = alteredName;
	}
	
	public SapBpEntity getSapBp() {
		return sapBp;
	}
	
	public void setSapBp(SapBpEntity sapBp) {
		this.sapBp = sapBp;
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
			return sapBp.getName1();
		}
			
	}

}
