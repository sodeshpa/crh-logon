/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl.endload;

import java.io.Serializable;
import java.math.BigDecimal;

import com.crh.logon.persistence.entities.refdata.UomBsEntity;

/**
 * ReturnPallets data structure from the incoming endload request.
 * @author AdNovum Informatik AG
 */
public class ParsedPalletReturn implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final Long orderNo;
	private final String sapMaterialNo;
	private final BigDecimal quantity;
	private final UomBsEntity unitOfMeasurement;

	/**
	 * This variable stores the original position that this pallet return had in the request.
	 * Its only purpose is in error reporting: A faulty pallet return element can be pinpointed
	 * more easily this way.
	 */
	private final int originalPositionInRequest;

	public ParsedPalletReturn(
			Long orderNo, 
			String sapMaterialNo, 
			BigDecimal quantity, 
			UomBsEntity unitOfMeasurement,
			int originalPositionInRequest)
	{
		this.orderNo = orderNo;
		this.sapMaterialNo = sapMaterialNo;
		this.quantity = quantity;
		this.unitOfMeasurement = unitOfMeasurement;
		this.originalPositionInRequest = originalPositionInRequest;
	}

	public Long getOrderNo() {
		return orderNo;
	}

	public String getSapMaterialNo() {
		return sapMaterialNo;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public UomBsEntity getUnitOfMeasurement() {
		return unitOfMeasurement;
	}

	public int getOriginalPositionInRequest() {
		return originalPositionInRequest;
	}

}