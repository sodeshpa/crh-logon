/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 */
public class BodyData implements Serializable {
	private static final long serialVersionUID = 3523703173940210530L;
	
	private MaterialLogoData materialLogo;
	private OrderItemData orderItem;
	private TransportData transport;
	
	public MaterialLogoData getMaterialLogo() {
		return materialLogo;
	}
	public void setMaterialLogo(MaterialLogoData materialLogo) {
		this.materialLogo = materialLogo;
	}
	public OrderItemData getOrderItem() {
		return orderItem;
	}
	public void setOrderItem(OrderItemData orderItem) {
		this.orderItem = orderItem;
	}
	public TransportData getTransport() {
		return transport;
	}
	public void setTransport(TransportData transport) {
		this.transport = transport;
	}
	
}
