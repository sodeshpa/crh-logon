/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.containermonitoring;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.containermonitoring.ContainerMonitoringForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * @struts.action name="order_ContainerMonitoringForm" path="/containermonitoring/ContainerMonitoringEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="/containermonitoring/ContainerMonitoring.def"
 * @struts.action-forward name="failure" path="/containermonitoring/ContainerMonitoring.def"
 */
public class ContainerMonitoringEntryAction extends OrderActionSupport {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ContainerMonitoringEntryAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
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
					
					Authorization.authorize(Authorization.CONTAINER_MONITORING);
					String forwardName = "success";
					setSelectedTab("monitoring", request);
					setSelectedSub1Tab("monitoring", request);
					setSelectedSub2Tab("ContainerMonitoring", request);
					
					// Get the struts form.
					ContainerMonitoringForm helpForm = (ContainerMonitoringForm) form;
					helpForm.clear();

					final List <ShippingpointEntity> shippingPoints = getServiceLocator().getBestellungService().fetchContainerTerminalShippingPoints();
					
					if ( shippingPoints.size() == 1 ) {
						helpForm.setShippingPoints(shippingPoints);
					}
					
//		   		    final String companyJson = getJsonSerializer()
//		   		    		.include("companycodeCd")
//		   		    		.include("companycodeName")
//		   		    		.exclude("*").serialize(companyCodeList);
//		   		    	   		    
//		   		    helpForm.setCompanyCodeListJsOn(companyJson);


					return result = mapping.findForward(forwardName);
					
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
