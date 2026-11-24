/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.reporting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.actions.ByteArrayDownloadAction;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 */
public abstract class AbstractTruckRevenuePlanningDownloadAction extends ByteArrayDownloadAction {

	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(AbstractTruckRevenuePlanningDownloadAction.class);

	private static final String DEFAULT_FAIL_FORWARD_NAME = "failed";

	private final DateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	/**
	 * Name of the forward configuration that is used if a known error occurs for which an error message
	 * is displayed. (Default: {@link #DEFAULT_FAIL_FORWARD_NAME}.)
	 * 
	 * @return forward configuration for known failures
	 */
	public String getFailForwardName() {
		return DEFAULT_FAIL_FORWARD_NAME;
	}

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		// [[
		ActionForward result = null;
		Object[][] params = {{"mapping", mapping},
							{"form", form},
							{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			// TODO Auto-generated method stub
			return result = super.execute(mapping, form, request, response);
		}
		catch (Exception e) {
			// Handle known errors
			LogonException logonException = null;
			
			if (e instanceof LogonException) {
				logonException = (LogonException) e;
			}
			else if (e.getCause() instanceof LogonException) {
				logonException = (LogonException) e.getCause();
			}
			
			if (logonException != null) {
				Reason reason = logonException.getReason();
				
				if (Reason.RES_ERROR_TRUCK_REVENUE_PLANNING_NO_DATA.equals(reason)) {
					MessageUtils.addActionWarning(request, reason.getReason());
					
					String failForwardName = getFailForwardName();
					return result = mapping.findForward(failForwardName);
				}
			}
			
			// Handle unknown errors
			LOG.error("Error while downloading pdf");
			throw new LogonException(e);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	protected String getDateFileInFileNameFormat(Date date){
		return FILE_NAME_DATE_FORMAT.format(date);	
	}

}
