/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.controllers.scheduling.containers;

import java.math.BigDecimal;

import com.crh.logon.beans.LoadedQuantityData;
import com.crh.logon.persistence.entities.common.LoadEntity;

/**
 * @author AdNovum Informatik AG
 * 
 * Contains loading data necessary to transfer load between orderitems
 */
public class LoadTransferContainer {

	private LoadedQuantityData oldOrderItemLoadQuantityData;
	private LoadEntity oldLoad;
	
	public LoadTransferContainer(BigDecimal loadedQuantity, String loadedUom, LoadEntity oldLoad) {
		LoadedQuantityData loadedQuantityData = new LoadedQuantityData(loadedQuantity, loadedUom);
		this.oldOrderItemLoadQuantityData = loadedQuantityData;
		this.oldLoad = oldLoad;
	}
	
	public LoadedQuantityData getOldOrderItemLoadQuantityData() {
		return oldOrderItemLoadQuantityData;
	}
	public void setOldOrderItemLoadQuantityData(LoadedQuantityData oldOrderItemLoadQuantityData) {
		this.oldOrderItemLoadQuantityData = oldOrderItemLoadQuantityData;
	}
	public LoadEntity getOldLoad() {
		return oldLoad;
	}
	public void setOldLoad(LoadEntity oldLoad) {
		this.oldLoad = oldLoad;
	}
}
