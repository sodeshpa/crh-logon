/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.tags;

/**
 * @author AdNovum Informatik AG
 */
public class GID extends Tag {

	private DataElementMeta goodsItemNumber = new DataElementMeta(0, 0, false);
	private DataElementMeta[] dataElementMetas = { goodsItemNumber };
	
	public GID() {
		
		super("GID", "Goods item details");
	}

	public String getGoodsItemNumber() {
		return goodsItemNumber.getValue();
	}

	public void setGoodsItemNumber(String goodsItemNumber) {
		this.goodsItemNumber.setValue(goodsItemNumber);
	}

	@Override
	protected DataElementMeta[] getDataElementMetas() {
		return dataElementMetas;
	}
}
