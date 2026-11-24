/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.bimodalrailcarmonitoring;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.order.web.forms.bimodalrailcarmonitoring.BimodalRailcarMonitoringForm;
import com.crh.logon.persistence.entities.refdata.CompanycodeBsEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_BimodalRailcarMonitoringForm" path="/bimodalrailcarmonitoring/BimodalRailcarMonitoringEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="/bimodalrailcarmonitoring/BimodalRailcarMonitoring.def"
 * @struts.action-forward name="failure" path="/bimodalrailcarmonitoring/BimodalRailcarMonitoring.def"
 */
public class BimodalRailcarMonitoringEntryAction extends OrderActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BimodalRailcarMonitoringEntryAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		// [[
		ActionForward result1 = null;
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
			// [[
			ActionForward result = null;
			
				Authorization.authorize(Authorization.BIMODAL_MONITORING);
				BimodalRailcarMonitoringForm helpForm;
				String forwardName = "success";
				setSelectedTab("monitoring", request);
				setSelectedSub1Tab("monitoring", request);
				setSelectedSub2Tab("BimodalRailcarMonitoring", request);
				
				// Get the struts form.
				helpForm = (BimodalRailcarMonitoringForm) form;
				helpForm.clear();
				
				final List <CompanycodeBsEntity> companyCodeList = getServiceLocator().getBimodalRailcarMonitoringService().fetchCompanyCodes();
				
//				if ( companyCodeList.size() == 1 ) {
//					helpForm.setSelectedCompanyCode(companyCodeList.get(0).getCompanycodeCd());
//				}
				
	   		    final String companyJson = getJsonSerializer()
	   		    		.include("companycodeCd")
	   		    		.include("companycodeName")
	   		    		.exclude("*").serialize(companyCodeList);
	   		    	   		    
	   		    helpForm.setCompanyCodeListJsOn(companyJson);

				return result1 = result = mapping.findForward(forwardName);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}
	}
