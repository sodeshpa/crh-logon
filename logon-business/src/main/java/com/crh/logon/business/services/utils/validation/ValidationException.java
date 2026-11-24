/*
 * Author            : AdNovum Informatik AG

 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils.validation;

import java.util.Collection;
import java.util.LinkedList;

import javax.ejb.ApplicationException;

import org.apache.commons.lang.StringUtils;

import com.crh.logon.constants.CustorderError;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 *
 *	If this exception goes through transaction boundaries it will mark the transaction to
 *	be rolled back. 
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
@ApplicationException(rollback=true)
public final class ValidationException extends LogonException {
	
	private static final long serialVersionUID = 7443674667454609386L;
	
	private final Collection<ValidationErrorReason> validationErrorReasons = new LinkedList<ValidationErrorReason>();
	
	public ValidationException(ValidationErrorReason validationErrorReason) {
		super(Reason.RES_ERROR_UNSPECIFIED);
		this.validationErrorReasons.add(validationErrorReason);
    }
	
	public ValidationException(Collection<ValidationErrorReason> validationErrorReasons) {
		super(Reason.RES_ERROR_UNSPECIFIED);
		this.validationErrorReasons.addAll(validationErrorReasons);
    }
    
    /**
     * Returns true if there is an exception which had been suppressed when validating new 
     * entity. In this case ALL KIND OF EXCEPTIONS have to be thrown, new entities should not be
     * possible to be created if they have validation error.
     * 
     * @return
     */
    public boolean isRefersToNewEntity() {
    	for (ValidationErrorReason reason : validationErrorReasons) {
    		if (reason.isRefersToNewEntity()) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Returns true if there is an exception which had been suppressed when validating new 
     * entity. In this case ALL KIND OF EXCEPTIONS have to be thrown, new entities should not be
     * possible to be created if they have validation error.
     * 
     * @return
     */
    public boolean containsError(CustorderError error) {
    	for (ValidationErrorReason reason : validationErrorReasons) {
    		if (reason.getCustorderError().equals(error)) {
    			return true;
    		}
    	}
    	return false;
    }

	public Collection<ValidationErrorReason> getValidationErrorReasons() {
		return validationErrorReasons;
	}

	@Override
	public String getMessage() {
    	return StringUtils.join(validationErrorReasons, '\n');
	}
}
