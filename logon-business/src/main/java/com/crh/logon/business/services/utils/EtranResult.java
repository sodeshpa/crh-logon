/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

/**
 * @author AdNovum Informatik AG
 */
public class EtranResult {

	public String etranReturn;
	public String etranOutbound;
	
	public EtranResult(String etranReturn, String etranOutbound) {
		this.etranReturn = etranReturn;
		this.etranOutbound = etranOutbound;
	}
	
	/**
	 * @return the etranReturn
	 */
	public String getEtranReturn() {
		return etranReturn;
	}
	/**
	 * @param etranReturn the etranReturn to set
	 */
	public void setEtranReturn(String etranReturn) {
		this.etranReturn = etranReturn;
	}
	/**
	 * @return the etranOutbound
	 */
	public String getEtranOutbound() {
		return etranOutbound;
	}
	/**
	 * @param etranOutbound the etranOutbound to set
	 */
	public void setEtranOutbound(String etranOutbound) {
		this.etranOutbound = etranOutbound;
	}	
}
