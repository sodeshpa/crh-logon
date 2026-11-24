/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.carrierassignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.forms.carrierassignment.CarrierAssignmentForm;
import com.crh.logon.business.services.interfaces.CarrierAssignmentService;
import com.crh.logon.business.services.interfaces.TrailerAdminService;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.views.PoolShiptoMatSoldtoEntity;
import com.crh.logon.web.actions.ActionSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author AdNovum Informatik AG
 *
 *  @struts.action name="admin_CarrierAssignmentForm"
 *  path="/carrierassignment/CarrierAssignment" scope="session"
 *  validate="false" input="/carrierassignment/CarrierAssignment.def"
 *
 * @struts.action-forward name="success" path="/carrierassignment/CarrierAssignment.def"
 * @struts.action-forward name="reloadTable" path="/carrierassignment/CarrierAssignmentTable.def"
 * @struts.action-forward name="reload" path="/carrierassignment/CarrierAssignmentEntry.do"
 */
public class CarrierAssignmentAction extends ActionSupport {
	 
	private static final JcanLogger LOG =
	        JcanLoggerFactory.getLogger(CarrierAssignmentAction.class);

		public ActionForward execute(
			ActionMapping mapping, ActionForm form,
			HttpServletRequest request,	HttpServletResponse response)
			throws IOException, ServletException
		{
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
				String forwardName = "success";
				Authorization.authorize(Authorization.MASTER_DATA_CARRIER_ASSIGNMT_SHIPTO_PROD);
				setSelectedTab("masterdata", request);
				setSelectedSub1Tab("carrier", request);
				CarrierAssignmentForm helpForm = (CarrierAssignmentForm) form;
				CarrierAssignmentService caServ = getServiceLocator().getCarrierAssignmentService();
				
				if (request.getParameter("filtersChanged") != null) {
					forwardName = "reloadTable";
					List poolIdFilterList = new ArrayList();
					Long soldToIdFilter = null; 
					String shipToNrFilter = null;
					String productNrFilter = null;
					
					// if '-' selected for pool filter then use all the pools which belong to the user
					if ( "-1".equals(helpForm.getSelectedPoolFilter()) ) {
						poolIdFilterList = helpForm.getUserPoolIdList();
					}
					else {
						poolIdFilterList.add(new Long(helpForm.getSelectedPoolFilter()));
					}
					
					if ( !"-1".equals(helpForm.getSelectedSoldtoFilter())) {
						soldToIdFilter = new Long (helpForm.getSelectedSoldtoFilter());
					}
					if ( !"-1".equals(helpForm.getSelectedShiptoFilter())) {
						shipToNrFilter = helpForm.getSelectedShiptoFilter();
					}
					if ( !"-1".equals(helpForm.getSelectedProductFilter())) {
						productNrFilter = helpForm.getSelectedProductFilter();
					}
				
					// load filtered table data ( pool-kunde-shipto-produkt combinations )
					List poolShiptoMaterialList = caServ.fetchPoolShiptoMatSoldtoList(poolIdFilterList, soldToIdFilter, shipToNrFilter, productNrFilter, request.getLocale().getLanguage());
					helpForm.setPoolShiptoMaterialList(poolShiptoMaterialList);
				
				}
				else if (helpForm.getFormAction().equals("save")) {
				 	
					// compare selected carriers and checkboxes with the original values to decide which entity has been changed
					ListIterator listIt = helpForm.getPoolShiptoMaterialList().listIterator();
					TrailerAdminService trailerServ = getServiceLocator().getTrailerAdminService();
					
					while ( listIt.hasNext() ) {
						PoolShiptoMatSoldtoEntity entity = (PoolShiptoMatSoldtoEntity) listIt.next();
						Boolean originalFlag = (Boolean) helpForm.getOriginalCheckBoxValueMap().get(entity.getShiptoSareaRestrId());
						Long originalCarrier = (Long) helpForm.getOriginalSelectedCarrierMap().get(entity.getShiptoSareaRestrId());
						
						if ( originalFlag.equals(entity.getAutoCarrierAssignmentFlag()) 
								&& (  (originalCarrier == null && entity.getCommCarrierId().equals(new Long("-1"))) 
										||   (originalCarrier != null && originalCarrier.equals(entity.getCommCarrierId())) 
									)
							) {
							continue;
						} 
						
						helpForm.getOriginalCheckBoxValueMap().put(entity.getShiptoSareaRestrId(), entity.getAutoCarrierAssignmentFlag());
						helpForm.getOriginalSelectedCarrierMap().put(entity.getShiptoSareaRestrId(), entity.getCommCarrierId());
						
						ShiptoSareaRestrEntity restriction = caServ.fetchRestricitonById( entity.getShiptoSareaRestrId());
						CarrierEntity carrier = null; 
						if ( !new Long("-1").equals( entity.getCommCarrierId() ) ) {
							carrier = trailerServ.fetchCarrier(entity.getCommCarrierId());
						}
						restriction.setAutoCarrierAssignmentFlag(entity.getAutoCarrierAssignmentFlag());
						restriction.setCommCarrier(carrier);
						caServ.mergeRestriciton(restriction);
					}
					
					MessageUtils.addActionMessage(request,"carrierassignment.message.saved");
					forwardName = "success";
				}
				else if (helpForm.getFormAction().equals("reset")) {
					forwardName = "reload";
				}
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
