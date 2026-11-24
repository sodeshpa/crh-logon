/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import com.crh.logon.business.services.impl.TestServiceBean;

import javax.ejb.ApplicationException;

/**
 * This exception serves as a way for TestServiceBean calls to rollback any pending transactions in
 * order to avoid executing side-effects calls as part of the tests.
 *
 * Since it is not possible to have parameterized exceptions in Java, we make do with the additional
 * help method (getData) to return the data back in the appropriate form - assuming the caller is
 * using the correct type when assigning the value to a variable.
 *
 * @see TestServiceBean
 *
 * @author AdNovum Informatik AG
 */
@ApplicationException(rollback=true)
public class LogonTestSimulationException extends RuntimeException {

	private Object data;

	public LogonTestSimulationException(Object data) {
		this.data = data;
	}

	public <T> T getData() {
		return (T) data;
	}
}
