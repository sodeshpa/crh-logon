/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispo;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.DispoService;
import com.crh.logon.business.services.interfaces.ExternalMaterialService;
import com.crh.logon.business.services.interfaces.ExternalOrderService;
import com.crh.logon.business.services.interfaces.ExternalSiteService;
import com.crh.logon.business.services.interfaces.TrailerAdminService;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.dispo.ExternalOrderForm;
import com.crh.logon.persistence.entities.common.ExtOrderEntity;
import com.crh.logon.persistence.entities.common.ExtSiteEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.views.PoolCarrierCarrierEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_ExternalOrderForm"
 *  path="/dispo/ExternalOrderEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="DispoCreateExternalOrderPopup.def"
 */
public class ExternalOrderEntryAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(ExternalOrderEntryAction.class);

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
			
			ExternalOrderForm helpForm;
			String forwardName = "success";
			
			
		    // Get the struts form.
		    helpForm = (ExternalOrderForm) form;
		    
		    
			ExternalOrderService externalOrderService = getServiceLocator().getExternalOrderService();
			DispoService dispoService = getServiceLocator().getDispoService();
			ExternalMaterialService externalMaterialAdminService = getServiceLocator().getExternalMaterialService();
			ExternalSiteService externalSiteService = getServiceLocator().getExternalSiteService();
			TrailerAdminService trailerAdminService = getServiceLocator().getTrailerAdminService();
		    
		    
		    
		    if (request.getParameter("edit") != null) {
		    	helpForm.resetFields();
		    	helpForm.setEditOrder(Boolean.TRUE);
		    	Long extOrderId;
		    	try {
					extOrderId = Long.valueOf(request.getParameter("extorderid"));
				}
				catch (NumberFormatException e) {
					throw new LogonException( "extorderid parameter can't be converted to Long", Reason.RES_ERROR_UNSPECIFIED);
				}
		    		
				ExtOrderEntity extOrder = externalOrderService.fetchExternalOrder(extOrderId);
				if( extOrder != null ){
			
					//set pool list and selected pool
				    List pools = getServiceLocator().getPoolAdminService().fetchPoolTxEntities(request.getLocale());
				    helpForm.setPoolsForExternal(pools);
					Long poolId = extOrder.getPool().getPoolId();
					helpForm.setSelectedPoolForExternal(poolId);
					
			
					//set carrier list and selected carrier
					Set<Long> poolIds = new HashSet<Long>();
					poolIds.add(poolId);
					List<PoolCarrierCarrierEntity> carriers = getServiceLocator().getPoolAdminService().fetchPoolCarrierCarrierEntitysByPoolIds(poolIds, request.getLocale());
					
					helpForm.setCarriersForExternal(carriers);
					Long carrierId = extOrder.getCarrier().getCarrierId();
					helpForm.setSelectedCarrierForExternal(carrierId);
					
					// set sap id
					String sapId = trailerAdminService.fetchCarrier(carrierId).getSapVendor().getSapId();
					helpForm.setSapId(sapId);
					
					//set material list and selected material
					List materialList = externalMaterialAdminService.fetchMaterialsForCarrier(carrierId, true);
					helpForm.setMaterialsForExternal(materialList);
					Long materialId = extOrder.getExtMaterial().getExtMaterialId();
					helpForm.setSelectedMaterialForExternal(materialId);
					
					//set site list, selected shippingpoint and selected shipto 
					List siteList = externalSiteService.fetchSiteForCarrier(carrierId, true);
					helpForm.setSiteListForExternal(siteList);
					Long shippingPointId = extOrder.getLoadingExtSite().getExtSiteId();
					Long shipToId = extOrder.getUnloadingExtSite().getExtSiteId();
					ExtSiteEntity shippingPoint = externalSiteService.fetchExternalSite(shippingPointId);
					if (shippingPoint != null) {
						helpForm.setSelectedShippingPointForExternal(shippingPointId);
						helpForm.setShippingPointCoordLat(shippingPoint.getCoordLat());
						helpForm.setShippingPointCoordLon(shippingPoint.getCoordLon());
					}
					ExtSiteEntity shipto = externalSiteService.fetchExternalSite(shipToId);
					if (shipto != null) {
						helpForm.setSelectedShiptoForExternal(shipToId);
						helpForm.setShiptoCoordLat(shipto.getCoordLat());
						helpForm.setShiptoCoordLon(shipto.getCoordLon());
					}
					
					//set order uom list
					List uomList = getServiceLocator().getRefDataGenericsService().findTxEntityList(UomTxEntity.class, true, request.getLocale(), UomTxEntity.ATTR_TXT, true);
					helpForm.setUomListForExternal(uomList);
					
					//set selected ordered uom
					String uomCd = extOrder.getOrderedUom().getUomCd();
					helpForm.setUomCdForExternal(uomCd);
					
					//set ordered qty
					String orderQty = extOrder.getOrderedQuantity().toPlainString();
					helpForm.setOrderQuantityForExtarnal(orderQty);
					
					//set selected loaded oum 
					if( extOrder.getLoadedAndDeliveredUom() != null ){
						String uomCdLoadedAndDelivered = extOrder.getLoadedAndDeliveredUom().getUomCd();
						helpForm.setUomCdLoadedForExternal(uomCdLoadedAndDelivered);
					}
					
					//set loaded qty
					if( extOrder.getLoadedQuantity() != null ){
						String loadedQty = extOrder.getLoadedQuantity().toPlainString();
						helpForm.setLoadedQuantityForExtarnal(loadedQty);
					}
					
					//set delivered qty
					if( extOrder.getDeliveredQuantity() != null ){
						String deliveredQty = extOrder.getDeliveredQuantity().toPlainString();
						helpForm.setDeliveredQuantityForExtarnal(deliveredQty);
					}
					
					//set delivery from date and time
					Date deliveryFrom = extOrder.getDeliveryFromDat();
				    String deliveryFromDateString = getUserTimezone().fmtAsDayMonthYear(deliveryFrom);
				    String deliveryFromTimeString = getUserTimezone().fmtAsHourMinute(deliveryFrom);
				    helpForm.setDeliveryFromDate(deliveryFromDateString);
				    helpForm.setDeliveryFromTime(deliveryFromTimeString);
					
					//set delivery to date and time						
				    Date deliveryTo = extOrder.getDeliveryToDat();
				    String deliveryToDateString = getUserTimezone().fmtAsDayMonthYear(deliveryTo);
				    String deliveryToTimeString = getUserTimezone().fmtAsHourMinute(deliveryTo);
				    helpForm.setDeliveryToDate(deliveryToDateString);
				    helpForm.setDeliveryToTime(deliveryToTimeString);
				    
				    //set popoup window title
				    String orderNumber = ""+extOrder.getExtOrderNumber();
				    String title = Message.lookup(request.getLocale(), "extorder.title.edit", orderNumber );
				    helpForm.setPupupTitle(title);
				    
				    //set form extOrderId
				    helpForm.setExternalOrderId(extOrderId);
				    helpForm.setOrderStatusTransmittedToDriver(extOrder.getOrderStatus().getOrderStatusCd().longValue() >= Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER);
				    helpForm.setOrderStatusShipping(extOrder.getOrderStatus().getOrderStatusCd().longValue() >= Constant.ORDER_STATUS_SHIPPING);
				    helpForm.setNoteTruckDriver(extOrder.getNoteTruckDriver());
				}
				
				
				
			}
		    else
		    if (request.getParameter("createExternalOrder") != null) {	
			    helpForm.resetFields();
			    
			    if (helpForm.getPoolsForExternal() == null) {
				    List pools = getServiceLocator().getPoolAdminService().fetchPoolTxEntities(request.getLocale());
				    helpForm.setPoolsForExternal(pools);
			    }
			    String deliveryFromDateString = request.getParameter("dateString");

			    Date deliveryFromDate;
				try {
					deliveryFromDate = getUserTimezone().parseDayMonthYear4(deliveryFromDateString);
				}
				catch (Exception exc) {
					LOG.error(String.format("Parsing delivery date %s failed: %s",
											deliveryFromDateString, exc.toString()));
					deliveryFromDate = null;
				}

			    helpForm.setDispoDate(deliveryFromDate);
			    helpForm.setDefaultDeliveryFromTo(request);
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
