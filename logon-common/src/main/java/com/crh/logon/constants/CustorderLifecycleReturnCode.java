/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.constants;


/**
 * Groups various error codes related to customer order lifecycle handling. For now only used in
 * Order Taking (Call Center).
 * 
 * @author AdNovum Informatik AG
 */
public enum CustorderLifecycleReturnCode {

	SUCCESS("Operation sucessfull."),
	ERR_OPEN_TO_FINISHED_NOT_OPEN("The Custorder is not open (unable to close Custorder)!"),
	ERR_OPEN_TO_FINISHED_NOT_ALL_SDOS_CLOSED("Not all of the Custorder single-delivery orders are closed (unable to close Custorder)!");
	/*
	This removed for LOGON-4978, based on agreement with Roland, no qty check anymore
	ERR_OPEN_TO_FINISHED_SUM_QUANTITY_NOT_OK("Quantity mismatch (unable to close Custorder)!");
	*/
	

	private final String msg;
	private CustorderLifecycleReturnCode(String msg) {
		this.msg = msg;
	}
	
	public boolean isSuccess() {
		return this == SUCCESS;
	}
	
	public boolean isError() {
		return !isSuccess();
	}
	
	@Override
	public String toString() {
		return msg;
	}
}
