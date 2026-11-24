/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.pdftools.layout.colldelnote;

import java.math.BigDecimal;

import com.crh.logon.pdftool.labelprovider.DTOMarker;

/**
 * @author AdNovum Informatik AG
 */
public class OrderItemTotalDTO implements DTOMarker {
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -969673104377064414L;
	
	/**
	 * SAP Number
	 */
	private final String sapNr;
	
	/**
	 * Name of material
	 */
	private final String materialName;
	
	/**
	 * Unit of Measure
	 */
	private final String uom;
	
	/**
	 * true, if physical material, false if service
	 */
	private final boolean isPhysical;
	
	/**
	 * net weight
	 */
	private BigDecimal netWeight;
	
	/**
	 * Constructor
	 * @param sapNr SAP Number
	 * @param materialName name of material
	 * @param netWeight net weight
	 * @param uom Unit of Measure
	 * @param isPhysical true, if physical material, false if service
	 */
	public OrderItemTotalDTO(String sapNr, String materialName, BigDecimal netWeight, String uom, boolean isPhysical) {
		super();
		this.sapNr = sapNr;
		this.materialName = materialName;
		this.netWeight = netWeight;
		this.uom = uom;
		this.isPhysical = isPhysical;
	}
	/**
	 * @return the sapNr
	 */
	public String getSapNr() {
		return sapNr;
	}
	/**
	 * @return the materialName
	 */
	public String getMaterialName() {
		return materialName;
	}
	
	/**
	 * Sets the net weight
	 * @param netWeight the net weight
	 */
	public void setNetWeight(BigDecimal netWeight) {
		this.netWeight = netWeight;
	}
	/**
	 * @return the netWeight
	 */
	public BigDecimal getNetWeight() {
		return netWeight;
	}
	/**
	 * @return the uom
	 */
	public String getUom() {
		return uom;
	}
	/**
	 * @return the isPhysical
	 */
	public boolean isPhysical() {
		return isPhysical;
	}
	
}
