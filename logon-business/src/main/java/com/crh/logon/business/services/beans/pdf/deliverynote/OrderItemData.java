/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.beans.pdf.deliverynote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.crh.logon.business.services.utils.PdfUtil;

import org.apache.commons.lang.StringUtils;

/**
 * @author AdNovum Informatik AG
 */
public class OrderItemData implements Serializable {
	private static final long serialVersionUID = -6809621567985092307L;
	private static final int DELIVERY_INFO_MAX_LENGTH = 330;
	
	private boolean showReturn;
	
	private MaterialLayoutType materialLayoutType;
	private String numberOfReturnedPallets;
	private String deliveryTypeInfo;
	
	private List<BagItemData> bagItems = new ArrayList<BagItemData>();
	private List<ServiceItemData> serviceItems = new ArrayList<ServiceItemData>();
	private List<BulkItemData> bulkItems = new ArrayList<BulkItemData>();

	public boolean renderReturnedPallets() {
		return StringUtils.isNotEmpty(numberOfReturnedPallets);
	}
	
	public boolean renderDeliveryTypeInfo() {
		return StringUtils.isNotEmpty(deliveryTypeInfo);
	}
	
	public List<BagItemData> getBagItems() {
		return bagItems;
	}
	public void setBagItems(List<BagItemData> bagItems) {
		this.bagItems = bagItems;
	}
	public List<ServiceItemData> getServiceItems() {
		return serviceItems;
	}
	public void setServiceItems(List<ServiceItemData> serviceItems) {
		this.serviceItems = serviceItems;
	}
	public List<BulkItemData> getBulkItems() {
		return bulkItems;
	}
	public void setBulkItems(List<BulkItemData> bulkItems) {
		this.bulkItems = bulkItems;
	}
	public String getNumberOfReturnedPallets() {
		return numberOfReturnedPallets;
	}
	public void setNumberOfReturnedPallets(String numberOfReturnedPallets) {
		this.numberOfReturnedPallets = numberOfReturnedPallets;
	}
	public MaterialLayoutType getMaterialLayoutType() {
		return materialLayoutType;
	}
	public void setMaterialLayoutType(MaterialLayoutType materialLayoutType) {
		this.materialLayoutType = materialLayoutType;
	}
	public boolean isShowReturn() {
		return showReturn;
	}
	public void setShowReturn(boolean showReturn) {
		this.showReturn = showReturn;
	}
	public String getDeliveryTypeInfo() {
		return PdfUtil.getWtihMaxLength(deliveryTypeInfo, DELIVERY_INFO_MAX_LENGTH);
	}
	public void setDeliveryTypeInfo(String deliveryTypeInfo) {
		this.deliveryTypeInfo = deliveryTypeInfo;
	}
}
