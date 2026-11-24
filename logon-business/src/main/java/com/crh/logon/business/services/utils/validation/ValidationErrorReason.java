/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils.validation;

import java.io.Serializable;
import java.util.Arrays;

import com.crh.logon.constants.CustorderError;
import com.crh.logon.persistence.entities.common.CustorderEntity;
import com.crh.logon.persistence.entities.common.CustorderItemEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;

/**
 * This class holds an enum {@link CustorderError} describing certain validation errors, and two additional
 * parameters for this kind of error which can help to find out what object caused the validation error.
 * If this reason is created for a complete new entity that means that it should cause failure if adding
 * to an already existing structure like order. If it is not new, that means that the validation errors are only
 * communicated to the user, but should not prevent saving the structure.
 * 
 * @author AdNovum Informatik AG
 */
public final class ValidationErrorReason implements Serializable {
	
	private static final long serialVersionUID = 709956211863680538L;

	public static final CustorderEntity DOES_NOT_REFER_TO_NEW_ENTITY = null;

	private final CustorderError custorderError;
	private final String[] additionalInfo;
	
	private final boolean refersToNewEntity;

	/**
	 * if TRUE show message as warning, otherwise error
	 */
	private Boolean showAsWarning = null;

	public static final String[] EMPTY_ADDITIONAL_INFO = new String[] {};

	public ValidationErrorReason(CustorderError custorderError, CustorderEntity custorder, String ... additionalInfo) {
		this(custorderError, custorder.getCustorderId() == null, null, additionalInfo);
	}
	
	public ValidationErrorReason(CustorderError custorderError, CustorderItemEntity custorderItem, String ... additionalInfo) {
		this(custorderError, custorderItem.getCustorderItemId() == null, null, additionalInfo);
	}

	public ValidationErrorReason(CustorderError custorderError, OrderEntity order, String ... additionalInfo) {
		this(custorderError, order.getOrderId() == null, null, additionalInfo);
	}
	
	public ValidationErrorReason(CustorderError custorderError, OrderItemEntity orderItem, String ... additionalInfo) {
		this(custorderError, orderItem.getOrderItemId() == null, null, additionalInfo);
	}
	
	public ValidationErrorReason(CustorderError custorderError, boolean referesToNewEntity,
			Boolean showAsWarning, String... additionalInfo) {
		this.custorderError = custorderError;
		this.additionalInfo = additionalInfo;
		this.refersToNewEntity = referesToNewEntity;
		this.showAsWarning = showAsWarning;
	}
	
	public CustorderError getCustorderError() {
		return custorderError;
	}

	public String[] getAdditionalInfo() {
		return additionalInfo;
	}

	public boolean isRefersToNewEntity() {
		return refersToNewEntity;
	}

	public Boolean getShowAsWarning() {
		return showAsWarning;
	}

	@Override
	public String toString() {
    	final StringBuilder sb = new StringBuilder();
    	
		sb.append("ValidationErrorReason: ");
		sb.append(custorderError);
		if ((additionalInfo != null) && (additionalInfo.length > 0)) {
			sb.append(", additionalInfo");
			sb.append(Arrays.toString(additionalInfo));
		}		
    	return sb.toString();
	}
}
