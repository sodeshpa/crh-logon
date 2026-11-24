/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispo;

import java.io.IOException;
import java.math.BigDecimal;
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

import com.crh.logon.business.services.interfaces.LKWAvailabilityChangeService;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.dispo.LKWPopupForm;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.TrailerTrspCatTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckTransportCatTxEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeTxEntity;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.TruckPriorityUtil;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_LKWPopupForm"
 *  path="/dispo/LKWPopupEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="LKWPopupTile.def"
 * @struts.action-forward name="failure" path="LKWPopupTile.def" 
 */
public class LKWPopupEntryAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(LKWPopupEntryAction.class);

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
			Authorization.authorize(Authorization.DISPATCHING_W);
			
			setSelectedTab("dispo", request);
			
		    // Get the struts form.
			LKWPopupForm helpForm = (LKWPopupForm) form;
			helpForm.clear();
			
			String forwardName = "success";
			
			setSelectedTab("dispokarte", request);
			
			long truckId = Long.valueOf(request.getParameter("truckId")).longValue();
			
		    LKWAvailabilityChangeService service = getServiceLocator().getLKWAvailabilityChangeService();
		    TruckEntity truck = service.fetchTruckDetail(truckId);
		    List unAvailabilities = service.fetchTruckUnavailabilities(truckId);
		    helpForm.setTruckUnavailabilities(unAvailabilities);
		    helpForm.setTruck(truck);
		    TruckTypeTxEntity type = getServiceLocator().getRefDataGenericsService().findTxEntity(TruckTypeTxEntity.class, request.getLocale(), truck.getTruckType());
		    helpForm.setType(type);
		    TruckTransportCatTxEntity catTx = getServiceLocator().getRefDataGenericsService().findTxEntity(TruckTransportCatTxEntity.class, request.getLocale(), truck.getTruckTransportCat());
		    helpForm.setTrspCat(catTx);
		    TrailerEntity trailer = getServiceLocator().getTruckAdminService().fetchTrailerForTruck(truckId, true);
		    helpForm.setTrailer(trailer);
		    
		    helpForm.setTruckPriority(truck.getPriority());
		    populateTruckPriorities(helpForm);
		    
		    if ( trailer != null ) {
		    	TrailerTrspCatTxEntity trailerTx = getServiceLocator().getRefDataGenericsService().findTxEntity(TrailerTrspCatTxEntity.class, request.getLocale(), trailer.getTrailerTrspCat());
		    	helpForm.setTrailerTrspCat(trailerTx);
		    }
		    ContainerEntity container = getServiceLocator().getTruckAdminService().fetchContainerForTruckTrailerComposition(truck, trailer);
		    helpForm.setContainer(container);
		    try {				
		    	BigDecimal sumTare = LogonUtil.sumTareWeights(trailer, container, truck, null, null);
		    	if ( sumTare != null ) {					
		    		helpForm.setCompositionTare(sumTare.toPlainString());
		    	}
		    }
		    catch (Exception e) {
		    	LOG.error("Could not determine tare of composition truck plate no " + truck.getTruckPlateNo() + " truck id " + truck.getTruckId() );
			}
		    try {
		    	Long truckTransportCatCd = truck.getTruckTransportCat().getTruckTransportCatCd();
		    	
		    	BigDecimal maxGross =  null;
		    	
		    	//same logic as-is for determination of max gross for LDS
				if (truckTransportCatCd.equals(Constant.TRUCK_TRSP_CAT_TRAILER) && trailer != null) {
					
						maxGross =  trailer.getMaxGrossWeightT();
				}
				else {
					
					maxGross =  truck.getMaxGrossWeightT();
				}
		    	
		    	if ( maxGross != null ) {					
		    		helpForm.setCompositionGross(maxGross.toPlainString());
		    	}
		    }
		    catch (Exception e) {
		    	LOG.error("Could not determine gross of composition truck plate no " + truck.getTruckPlateNo() + " truck id " + truck.getTruckId() );
			}
		    
		    // Set the truck + trailer nrChambers on the form
		    Long nrChambers = truck.getNrChambers();
		    if (trailer != null) {
		    	nrChambers += trailer.getNrChambers();
		    }
		    
		    helpForm.setCompositionNrChambers(nrChambers.toString());
			
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
	
	private void populateTruckPriorities(LKWPopupForm helpForm) {
		long[] truckPriorities = TruckPriorityUtil.createSimpleVehiclePriorityArray();

		helpForm.setTruckPriorities(truckPriorities);
	}
}
