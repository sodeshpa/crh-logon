package com.crh.logon.business.services.utils;

import javax.ejb.ApplicationException;

import com.crh.logon.utils.LogonException;

/**
 * This one occurs in cases when order creation or order saving is not permitted due to invalid
 * route to the shipto (e.g. if it is on a different continent than the shipping point). Pickup
 * delivery orders are an exception, because they can be created without valid calculated route.
 * 
 * @author AdNovum Informatik AG
 * 
 */
@ApplicationException(rollback = true)
public class LogonInvalidDurationException extends LogonException {
}
