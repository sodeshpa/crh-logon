/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.sirocco20;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.to.sirocco20.Sirocco20Stub.ArrayOfMTTextRequest;
import com.crh.logon.to.sirocco20.Sirocco20Stub.MTTextRequest;
import com.crh.logon.to.sirocco20.Sirocco20Stub.MTTextResponse;
import com.crh.logon.to.sirocco20.Sirocco20Stub.SubmitMTText;
import com.crh.logon.to.sirocco20.Sirocco20Stub.SubmitMTTextResponse;
import com.crh.logon.webservices.clients.util.WebserviceClientUtil;

/**
 * @author AdNovum Informatik AG
 */
public class LogonToSirocco20SmsAssembler {

	private static final JcanLogger LOG = JcanLoggerFactory
			.getLogger(LogonToSirocco20SmsAssembler.class);
		
	public static final SubmitMTText createSubmitMTText(TextMessageBean msg) {
		// [[
		SubmitMTText result = null;
		Object[][] params = {{"msg", msg}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToSirocco20SmsAssembler.class, "createSubmitMTText", params);
		}
		// ]]
		try {
			result = new SubmitMTText();
			
			result.setPrincipal(msg.getPrincipal());
			result.setCredential(msg.getCredential());
						
			MTTextRequest mTTextRequest;
			final ArrayOfMTTextRequest arrayOfMTTextRequest = new ArrayOfMTTextRequest();
			for (String number : msg.getNumbers()) {
				mTTextRequest = new MTTextRequest();
				mTTextRequest.setMessage(msg.getMessage());				
				mTTextRequest.setMobile(number);
				mTTextRequest.setServiceNumber(msg.getServiceNumber());
				mTTextRequest.setRate("0");
				arrayOfMTTextRequest.addItem(mTTextRequest);
			}			
			
			result.setItems(arrayOfMTTextRequest);
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public static String getSubmitMTTextXml(TextMessageBean msg) {		
		return WebserviceClientUtil.getSerializedString(createSubmitMTText(msg), "SubmitMTTextXml");
	}

	/**
	 * @param smsResponse
	 * @return
	 */
	public static final ResponseBean[] getResponseBean(SubmitMTTextResponse submitMTTextResponse) {
		// [[
		ResponseBean[] result = null;
		Object[][] params = {{"submitMTTextResponse", submitMTTextResponse}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(LogonToSirocco20SmsAssembler.class, "getResponseBean", params);
		}
		// ]]
		try {
			if ((submitMTTextResponse != null) &&
				(submitMTTextResponse.getSubmitMTTextReturn() != null) &&
				(submitMTTextResponse.getSubmitMTTextReturn().getItem() != null)) {				
				final MTTextResponse[] mTTextResponse = submitMTTextResponse.getSubmitMTTextReturn().getItem();
				
				result = new ResponseBean[mTTextResponse.length];
				for (int i=0; i<mTTextResponse.length; i++) {
					result[i] = new ResponseBean(
							!mTTextResponse[i].getError(),
							mTTextResponse[i].getAppId(),
							mTTextResponse[i].getGUID(),
							mTTextResponse[i].getErrorMessage());					
				}
			}
			else {
				result = new ResponseBean[0];
			}
			
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
}
