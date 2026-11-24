/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.orderdetail.dto;

import java.math.BigDecimal;
import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author AdNovum Informatik AG
 */
public class BimodalTruckOrderItemDTO implements Serializable {

	private static final long serialVersionUID = -2816258420111092912L;

	private String truckName;
	private Long orderId;
	private Long orderNumber;
	private boolean authorized;
	private BigDecimal netQuantity;
	private String uomTxt;

	public BimodalTruckOrderItemDTO(String truckName, Long orderId, Long orderNumber,
			boolean authorized, BigDecimal netQuantity, String uomTxt) {
		this.truckName = truckName;
		this.orderId = orderId;
		this.orderNumber = orderNumber;
		this.authorized = authorized;
		this.netQuantity = netQuantity;
		this.uomTxt = uomTxt;
	}

	public String getTruckName() {
		return truckName;
	}

	public Long getOrderId() {
		return orderId;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public BigDecimal getNetQuantity() {
		return netQuantity;
	}

	public String getUomTxt() {
		return uomTxt;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append(super.toString());
		tsb.append("truckName", truckName);
		tsb.append("orderId", orderId);
		tsb.append("orderNumber", orderNumber);
		tsb.append("authorized", authorized);
		String netQuantityText = netQuantity == null ? "null" : netQuantity.toPlainString();
		tsb.append("netQuantity", netQuantityText);
		tsb.append("uomTxt", uomTxt);
		return tsb.toString();
	}

	/**
	 * Returns a {@code String} representation of the netQuantity and the uomTxt separated by a space
	 * @return the formatted text
	 */
	public String getWeightRepresentation() {
		if (netQuantity == null){
			return "";
		}
		return String.format("%s %s", netQuantity.toPlainString(), uomTxt);
	}

}
