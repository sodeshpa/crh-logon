/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.auftrag;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.crh.logon.business.services.utils.CustorderSplitDiffWrapper;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.views.CustordItemSumQtyExtEntity;

/**
 * @author AdNovum Informatik AG
 */
public class CustOrderTabBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Map<Long, String> collDeliveryNoteMap;
	private Map<Long, CustordItemSumQtyExtEntity> materialOnlyMap;
	private Map<String, String> custorderItemsQtyBySapMatIdMap;
	
	private List<CustOrderTabOrderBean> orders = new LinkedList<CustOrderTabOrderBean>();
	
	private String custorderNumber;
	private String custorderStatus;
	private String deliveryFrom;
	private String deliveryTo;
	private boolean exactCustOrderQty;
	private boolean exactSingleOrderQty;
	private boolean exactCustOrderQtyEditable;
	private boolean exactSingleOrderQtyEditable;
	private CustorderEntity custorderEntity;
	private BigDecimal toDeleteQty;
	private String toDeleteQtyUom;
	private CustorderSplitDiffWrapper custorderSplitDiffWrapper;
	
	
	public Map<Long, String> getCollDeliveryNoteMap() {
		return collDeliveryNoteMap;
	}
	public void setCollDeliveryNoteMap(Map<Long, String> collDeliveryNoteMap) {
		this.collDeliveryNoteMap = collDeliveryNoteMap;
	}
	public Map<Long, CustordItemSumQtyExtEntity> getMaterialOnlyMap() {
		return materialOnlyMap;
	}
	public void setMaterialOnlyMap(Map<Long, CustordItemSumQtyExtEntity> materialOnlyMap) {
		this.materialOnlyMap = materialOnlyMap;
	}
	public List<CustOrderTabOrderBean> getOrders() {
		return orders;
	}
	public void setOrders(List<CustOrderTabOrderBean> orders) {
		this.orders = orders;
	}
	public boolean isExactCustOrderQty() {
		return exactCustOrderQty;
	}
	public void setExactCustOrderQty(boolean exactCustOrderQty) {
		this.exactCustOrderQty = exactCustOrderQty;
	}
	public boolean isExactSingleOrderQty() {
		return exactSingleOrderQty;
	}
	public void setExactSingleOrderQty(boolean exactSingleOrderQty) {
		this.exactSingleOrderQty = exactSingleOrderQty;
	}
	public String getDeliveryFrom() {
		return deliveryFrom;
	}
	public void setDeliveryFrom(String deliveryFrom) {
		this.deliveryFrom = deliveryFrom;
	}
	public String getDeliveryTo() {
		return deliveryTo;
	}
	public void setDeliveryTo(String deliveryTo) {
		this.deliveryTo = deliveryTo;
	}
	public CustorderEntity getCustorderEntity() {
		return custorderEntity;
	}
	public void setCustorderEntity(CustorderEntity custorderEntity) {
		this.custorderEntity = custorderEntity;
	}
	public BigDecimal getToDeleteQty() {
		return toDeleteQty;
	}
	public void setToDeleteQty(BigDecimal toDeleteQty) {
		this.toDeleteQty = toDeleteQty;
	}
	public String getToDeleteQtyUom() {
		return toDeleteQtyUom;
	}
	public void setToDeleteQtyUom(String toDeleteQtyUom) {
		this.toDeleteQtyUom = toDeleteQtyUom;
	}
	public CustorderSplitDiffWrapper getCustorderSplitDiffWrapper() {
		return custorderSplitDiffWrapper;
	}
	public void setCustorderSplitDiffWrapper(CustorderSplitDiffWrapper custorderSplitDiffWrapper) {
		this.custorderSplitDiffWrapper = custorderSplitDiffWrapper;
	}
	public String getCustorderNumber() {
		return custorderNumber;
	}
	public void setCustorderNumber(String custorderNumber) {
		this.custorderNumber = custorderNumber;
	}
	public String getCustorderStatus() {
		return custorderStatus;
	}
	public void setCustorderStatus(String custorderStatus) {
		this.custorderStatus = custorderStatus;
	}
	public boolean getExactCustOrderQtyEditable() {
		return exactCustOrderQtyEditable;
	}
	public void setExactCustOrderQtyEditable(boolean exactCustOrderQtyEditable) {
		this.exactCustOrderQtyEditable = exactCustOrderQtyEditable;
	}
	public boolean getExactSingleOrderQtyEditable() {
		return exactSingleOrderQtyEditable;
	}
	public void setExactSingleOrderQtyEditable(boolean exactSingleOrderQtyEditable) {
		this.exactSingleOrderQtyEditable = exactSingleOrderQtyEditable;
	}
	public Map<String, String> getCustorderItemsQtyBySapMatIdMap() {
		return custorderItemsQtyBySapMatIdMap;
	}
	public void setCustorderItemsQtyBySapMatIdMap(Map<String, String> custorderItemsQtyBySapMatIdMap) {
		this.custorderItemsQtyBySapMatIdMap = custorderItemsQtyBySapMatIdMap;
	}
	public String getCustorderItemsQtyBySapMatIdMap(String key) {
		return custorderItemsQtyBySapMatIdMap.get(key);
	}
	public void setCustorderItemsQtyBySapMatIdMap(String key, String value) {
		this.custorderItemsQtyBySapMatIdMap.put(key, value);
	}

}