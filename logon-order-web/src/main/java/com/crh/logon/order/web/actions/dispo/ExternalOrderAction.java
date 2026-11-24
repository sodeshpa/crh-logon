/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.dispo.ExternalOrderForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.GoogleMapsDistanceMatrixResultBean;
import com.crh.logon.business.services.interfaces.BestellungService;
import com.crh.logon.business.services.interfaces.DispoService;
import com.crh.logon.business.services.interfaces.ExternalMaterialService;
import com.crh.logon.business.services.interfaces.ExternalOrderService;
import com.crh.logon.business.services.interfaces.ExternalSiteService;
import com.crh.logon.business.services.interfaces.GoogleMapsService;
import com.crh.logon.business.services.interfaces.TrailerAdminService;
import com.crh.logon.business.services.interfaces.TruckAdminService;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.ExtMaterialEntity;
import com.crh.logon.persistence.entities.common.ExtOrderEntity;
import com.crh.logon.persistence.entities.common.ExtSiteEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.views.PoolCarrierCarrierEntity;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_ExternalOrderForm"
 *  path="/dispo/ExternalOrder" scope="session"
 *  validate="true" input="DispoTile2.def"
 *
 * @struts.action-forward name="succes" path="DispoCreateExternalOrderPopup.def"
 * @struts.action-forward name="delete" path="DispoCloseExternalOrderPopup.def"
 * @struts.action-forward name="saveClose" path="DispoCloseExternalOrderPopup.def"
 * 
 */
public class ExternalOrderAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(ExternalOrderAction.class);

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
			Authorization.authorize(Authorization.DISPATCHING_CREATE_EXT_ORD);
			
			ExternalOrderService externalOrderService = getServiceLocator().getExternalOrderService();
			DispoService dispoService = getServiceLocator().getDispoService();
			BestellungService orderService = getServiceLocator().getBestellungService();
			ExternalMaterialService externalMaterialAdminService = getServiceLocator().getExternalMaterialService();
			ExternalSiteService externalSiteService = getServiceLocator().getExternalSiteService();
			TrailerAdminService trailerAdminService = getServiceLocator().getTrailerAdminService();
			TruckAdminService truckAdminService = getServiceLocator().getTruckAdminService();
			
			
		    // Get the struts form.
			ExternalOrderForm helpForm = (ExternalOrderForm)form;
			String forwardName = "input";
			boolean reloadOrders = false;
			
			//setSelectedTab("dispo", request);
	
			if (request.getParameter("setPool") != null) {
			    
			    if ( ! Constant.INVALID_OBJECT_ID_STR.equals(helpForm.getSelectedPoolForExternal())) {
			    	
			    	helpForm.resetFieldsWithoutPools();
			    	Long poolId = helpForm.getSelectedPoolForExternal();
			    	
			    	Set<Long> poolIds = new HashSet<Long>();
					poolIds.add(poolId);
					List<PoolCarrierCarrierEntity> carrierList = getServiceLocator().getPoolAdminService().fetchPoolCarrierCarrierEntitysByPoolIds(poolIds, request.getLocale());
			    	
			    	helpForm.setCarriersForExternal(carrierList);
			    	
			    	if( carrierList.size() == 1 ){
						helpForm.setSelectedCarrierForExternal(((PoolCarrierCarrierEntity) carrierList.get(0)).getCarrierId());
						setCarrierFunction(helpForm, request, orderService, externalMaterialAdminService, externalSiteService, trailerAdminService);
					}
			    }
			}	
			
			if (request.getParameter("setCarrier") != null) {
			
				setCarrierFunction(helpForm, request, orderService, externalMaterialAdminService, externalSiteService, trailerAdminService);
			}
			
			if (request.getParameter("setSites") != null) {
				
				Long shippingPointId = helpForm.getSelectedShippingPointForExternal();
				Long shiptoId = helpForm.getSelectedShiptoForExternal();
				
				ExtSiteEntity shippingPoint = externalSiteService.fetchExternalSite(shippingPointId);
				ExtSiteEntity shipto = externalSiteService.fetchExternalSite(shiptoId);
				
				helpForm.setShippingPointCoordLat(shippingPoint.getCoordLat());
				helpForm.setShippingPointCoordLon(shippingPoint.getCoordLon());
				helpForm.setShiptoCoordLat(shipto.getCoordLat());
				helpForm.setShiptoCoordLon(shipto.getCoordLon());
				
				helpForm.setNoteTruckDriver(shipto.getCarrierTruckDriverNote());
			}
			
			if (request.getParameter("save") != null || request.getParameter("saveClose") != null) {
				
				Date deliveryFrom = helpForm.getDeliveryFrom();
				if(deliveryFrom == null){
					MessageUtils.addActionWarning(request, "extorder.error.wrong.format.deliveryFrom");
				}
				
				Date deliveryTo = helpForm.getDeliveryTo();
				if(deliveryTo == null){
					MessageUtils.addActionWarning(request, "extorder.error.wrong.format.deliveryTo");
				}
				
				
				Long poolId = helpForm.getSelectedPoolForExternal();
				Long carrierId = helpForm.getSelectedCarrierForExternal();
				Long shippingPointId = helpForm.getSelectedShippingPointForExternal();
				Long shiptoId = helpForm.getSelectedShiptoForExternal();
				Long materialId = helpForm.getSelectedMaterialForExternal();
				String orderQty = helpForm.getOrderQuantityForExtarnal();
				String loadedQty = helpForm.getLoadedQuantityForExtarnal();
				String deliveredQty = helpForm.getDeliveredQuantityForExtarnal();
				String orderedUomCd = helpForm.getUomCdForExternal();
				String loadedAndDeliveredUomCd = helpForm.getUomCdLoadedForExternal();
				
				if (!helpForm.isEditOrder() && deliveryFrom != null && deliveryTo != null){
					if ( deliveryFrom.after(deliveryTo) ) {
						MessageUtils.addActionWarning(request, "extorder.error.invalid.delivery.earlier.later");
					}
					if (deliveryFrom.before(new Date())) {
						MessageUtils.addActionWarning(request,"extorder.error.invalid.delivery.from");
					}
				}
				if (shippingPointId.equals(shiptoId) && !(shippingPointId == -1 || shippingPointId == 0 || shippingPointId == null)){
					MessageUtils.addActionWarning(request, "extorder.error.different.site");
				}
			
				if ( poolId == -1 || poolId == 0 || poolId == null){
					MessageUtils.addActionWarning(request, "extorder.errors.required.pool");
				}
			
				if ( carrierId == -1 || carrierId == 0 || carrierId == null){
					MessageUtils.addActionWarning(request, "extorder.errors.required.carrier");
				}
				
				if ( shippingPointId == -1 || shippingPointId == 0 || shippingPointId == null){
					MessageUtils.addActionWarning(request, "extorder.errors.required.shpoint");
				}
				
				if ( shiptoId == -1 || shiptoId == 0 || shiptoId == null){
					MessageUtils.addActionWarning(request, "extorder.errors.required.shipto");
				}
				
				if ( materialId == -1 || materialId == 0 || materialId == null){
					MessageUtils.addActionWarning(request, "extorder.errors.required.material");
				}
				
				//orderQty				
				BigDecimal orderQtyBigDecimal = null;
				if(!StringUtils.empty(orderQty)){
					try{
						orderQtyBigDecimal = new BigDecimal(orderQty);
					} catch (NumberFormatException e) {
						MessageUtils.addActionWarning(request, "extorder.error.wrong.format.orderQty");
					}
				}
				else{
					MessageUtils.addActionWarning(request, "extorder.errors.order.qty.zero");
				}
				
				//loadedQty
				BigDecimal loadedQtyBigDecimal = null;
				if (!StringUtils.empty(loadedQty)) {		
					try{
						loadedQtyBigDecimal = new BigDecimal(loadedQty);
					} catch (NumberFormatException e) {
						MessageUtils.addActionWarning(request, "extorder.error.wrong.format.loadedQty");
					}
				}
				
				//deliveredQty
				BigDecimal deliveredQtyBigDecimal = null;
				if (!StringUtils.empty(deliveredQty)) {
					try{
						deliveredQtyBigDecimal = new BigDecimal(deliveredQty);
					} catch (NumberFormatException e) {
						MessageUtils.addActionWarning(request, "extorder.error.wrong.format.deliveredQty");
					}
				}
				
				if ( StringUtils.notEmpty(helpForm.getNoteTruckDriver()) && helpForm.getNoteTruckDriver().length() > ExtOrderEntity.DBSIZE_NOTE_TRUCK_DRIVER ) {
        			MessageUtils.addActionWarning(request,"dispokarte.errors.toolongNoteTruckDriver", Long.toString(ExtOrderEntity.DBSIZE_NOTE_TRUCK_DRIVER));
        		}				
				checkFieldMaxValue(request, orderQty, ExtOrderEntity.DBSIZE_ORDERED_QUANTITY, ExtOrderEntity.DBSIZE_PRECISION_ORDERED_QUANTITY , "extorder.label.quantity");
				checkFieldMaxValue(request, loadedQty, ExtOrderEntity.DBSIZE_LOADED_QUANTITY, ExtOrderEntity.DBSIZE_PRECISION_LOADED_QUANTITY , "extorder.label.net.weight");
				checkFieldMaxValue(request, deliveredQty, ExtOrderEntity.DBSIZE_DELIVERED_QUANTITY, ExtOrderEntity.DBSIZE_PRECISION_DELIVERED_QUANTITY , "extorder.label.delivered.quantity");
				
				// Calculate distance and duration values
				Long distanceMeter = null;
				Long durationMin = null;

				final ExtSiteEntity shippingpointExtSite = externalSiteService.fetchExternalSite(shippingPointId);
				final ExtSiteEntity shiptoExtSite = externalSiteService.fetchExternalSite(shiptoId);
				try {
					final GoogleMapsService googleMapsService = getGoogleMapsService();
					final GoogleMapsDistanceMatrixResultBean distanceMatrixResult = googleMapsService.getDistanceMatrixResult(
							shippingpointExtSite, shiptoExtSite);

					final String status = distanceMatrixResult.getStatus();
					if (Constant.GMAPS_DISTANCE_MATRIX_RESPONSE_STATUS_OK.equals(status)) {

						distanceMeter = distanceMatrixResult.getDistance();
						durationMin = distanceMatrixResult.getDurationMin();

						if (LOG.isDebugEnabled()) {
							LOG.debug("## distanceMeter: '{}', durationMin: '{}'", new Object[] { distanceMeter,
									durationMin });
						}
					}
					else {
						MessageUtils.addActionWarning(request, "extorder.errors.no.route");
					}
				}
				catch (RuntimeException ex) {
					LOG.error(
							"Exception when calculating the distance and duration on saving external order.",
							ex);
					MessageUtils.addActionError(request,
							Reason.RES_ERROR_GMAPS_DISTANCE_CALC_FAILED_EXTERNAL_ORDER.getReason());
				}
				
				if (noErrorWarningPresent(request)) {
					int orderCount = 1;
					UomBsEntity orderedUom = getRefDataGenericsService().findBsEntity(UomBsEntity.class, orderedUomCd);
					if (orderedUom.getCreateMultiOrderFlag().booleanValue()) {
						orderCount = orderQtyBigDecimal.intValueExact();
					}
					ExtOrderEntity extOrder = null;
					for (int i=0;i<orderCount;i++) {
						if ((i == 0) && helpForm.getExternalOrderId() != null && helpForm.getExternalOrderId() != 0L) {
							extOrder = externalOrderService.fetchExternalOrder(helpForm.getExternalOrderId());
						}
						else {
							extOrder = new ExtOrderEntity();	
						}
						extOrder.setDeliveryFromDat(deliveryFrom);
						extOrder.setDeliveryToDat(deliveryTo);
						extOrder.setPool(truckAdminService.fetchPool(poolId));
						extOrder.setCarrier(trailerAdminService.fetchCarrier(carrierId));
						extOrder.setLoadingExtSite(shippingpointExtSite);
						extOrder.setUnloadingExtSite(shiptoExtSite);
						extOrder.setExtMaterial(externalMaterialAdminService.fetchExternalMaterial(materialId));
						extOrder.setOrderedUom(orderedUom);
						extOrder.setLoadedAndDeliveredUom(getRefDataGenericsService().findBsEntity(UomBsEntity.class, loadedAndDeliveredUomCd));
						if (orderedUom.getCreateMultiOrderFlag().booleanValue()) {
							extOrder.setOrderedQuantity(BigDecimal.ONE);
						}
						else {
							extOrder.setOrderedQuantity(orderQtyBigDecimal);
						}
						extOrder.setLoadedQuantity(loadedQtyBigDecimal);
						extOrder.setDeliveredQuantity(deliveredQtyBigDecimal);
						
						extOrder.setTransportDistanceM(distanceMeter);
						extOrder.setTransportDurationMin(durationMin);
						
						extOrder.setNoteTruckDriver(helpForm.getNoteTruckDriver());
						
						extOrder = externalOrderService.saveExtOrder(extOrder);
					}
					
					if ((extOrder != null) && (extOrder.getExtOrderId() != null)) {
						MessageUtils.addActionMessage(request, "extorder.message.success.save");

						helpForm.setExternalOrderId(extOrder.getExtOrderId());
						
						callReloadOrdersJs(request);
						
						if (request.getParameter("saveClose") != null) {
							forwardName = "saveClose";
							return result = mapping.findForward(forwardName);
						}
						else if (orderCount > 1) {
							resetForm(helpForm, request);
						}
					}
					
				}
				
			}
			if (request.getParameter("delete") != null) {
				externalOrderService.deleteExtOrder(helpForm.getExternalOrderId());
				
				resetForm(helpForm, request);							
			    
			    callReloadOrdersJs(request);
				forwardName = "delete";
				return result = mapping.findForward(forwardName);
			}
			
			forwardName = "succes";
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
	
	private void resetForm(ExternalOrderForm helpForm, HttpServletRequest request) {
		
		helpForm.resetFields();
	    
	    if (helpForm.getPoolsForExternal() == null) {
		    List pools = getServiceLocator().getPoolAdminService().fetchPoolTxEntities(request.getLocale());
		    helpForm.setPoolsForExternal(pools);
	    }
	    helpForm.setDefaultDeliveryFromTo(request);
	}
	
	private void callReloadOrdersJs(HttpServletRequest request) {
		request.setAttribute("callReloadOrdersJs", true);
	}
	
	private void setCarrierFunction(ExternalOrderForm helpForm, HttpServletRequest request, BestellungService orderService,
			ExternalMaterialService externalMaterialAdminService, ExternalSiteService externalSiteAdminService, TrailerAdminService trailerAdminService){
		
		
		helpForm.resetFieldsWithoutPoolsAndCarriers();
		long carrierId = helpForm.getSelectedCarrierForExternal();
		List materialList = externalMaterialAdminService.fetchMaterialsForCarrier(carrierId, true);
		List siteList = externalSiteAdminService.fetchSiteForCarrier(carrierId, true);
		List uomList = getServiceLocator().getRefDataGenericsService().findTxEntityList(UomTxEntity.class, true, request.getLocale(), UomTxEntity.ATTR_TXT, true);
		CarrierEntity carrier = trailerAdminService.fetchCarrier(carrierId);
		
		helpForm.setSapId(carrier.getSapVendor().getSapId());
		helpForm.setMaterialsForExternal(materialList);
		if( materialList.size() == 1 ){
			helpForm.setSelectedMaterialForExternal(((ExtMaterialEntity) materialList.get(0)).getExtMaterialId());
		}
						
		helpForm.setSiteListForExternal(siteList);
		if( siteList.size() == 1 ){
			Long extSiteId = ((ExtSiteEntity) siteList.get(0)).getExtSiteId();
			
			helpForm.setSelectedShippingPointForExternal(extSiteId);
			helpForm.setSelectedShiptoForExternal(extSiteId);
		}
						
		helpForm.setUomListForExternal(uomList);
		helpForm.setUomCdForExternal(Constant.UOM_TONS);
		helpForm.setUomCdLoadedForExternal(Constant.UOM_TONS);
	
	}
	
}
