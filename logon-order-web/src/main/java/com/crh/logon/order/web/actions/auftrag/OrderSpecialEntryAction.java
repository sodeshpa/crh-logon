/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.auftrag;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.persistence.entities.common.*;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.OrderSpecialService;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.auftrag.OrderSpecialForm;
import com.crh.logon.persistence.entities.refdata.DistributionDestTxEntity;
import com.crh.logon.persistence.entities.refdata.MaintTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.views.SapMatDescPackEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.WagonNumberUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

import flexjson.JSONSerializer;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_auftrag_OrderSpecialForm"
 *  path="/auftrag/OrderSpecialEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="OrderSpecialTile.def"
 */
public class OrderSpecialEntryAction extends OrderActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderSpecialEntryAction.class);
	
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
			Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
			String forwardName = "success";
			
			OrderSpecialForm helpForm = (OrderSpecialForm) form;
			helpForm.clear();
			Locale locale = getLocale(request);
			helpForm.setLocale(locale);
			helpForm.setDistDestCd(Constant.DISTRIBUTION_DEST_REPAIR);
			helpForm.setMaterialLoad(OrderSpecialForm.SELECTION_MATERIAL_LOAD_EMPTY);
			helpForm.setEmailSendFlag(true);

			// overwrite with shipto's timezone if we have one
			helpForm.setTimeZone(getUserTimezone());
			String wagonIdParam = request.getParameter("wagonid");
			
    		helpForm.setJsonDistDestData(new JSONSerializer()
	    		/*.include("distributionDestTxPK.distributionDestBs.distributionDestCd")
	    		.include("txt")
	    		.exclude("*")*/
	    		.serialize(
	    				getServiceLocator().getRefDataGenericsService().
	    				findTxEntityList(DistributionDestTxEntity.class,
						request.getLocale())));
    		
			if (wagonIdParam != null) {
				Long wagonId = Long.parseLong(wagonIdParam);
				//get order from DB
				OrderSpecialService orderSpecialService = getOrderSpecialService();
				WagonEntity wagon = orderSpecialService.loadWagon(wagonId);
				if (wagon!= null) {
					helpForm.setWagon(wagon);
					String wagonNo = WagonNumberUtils.toDisplayNo(wagon.getWagonNo());
					helpForm.setWagonNo(wagonNo);
					
					if (wagon.getReturnShiptoSapBp() != null){
						ShiptoEntity shipto = getDispokarteService()
								.fetchAndInitializeShiptoBySapId(
										wagon.getReturnShiptoSapBp().getSapId());

						TimeZone shiptoTZ = getTimeZoneService().loadForShipTo(shipto, locale);
						helpForm.setTimeZone(shiptoTZ);
					}

					{
						String emptySapMaterialNo = wagon.getWagonType().getEmptySapMaterialNo();
						SapMatDescPackEntity materialDesc = orderSpecialService.loadMaterialDesc(emptySapMaterialNo, locale.getLanguage());
						if (materialDesc != null) {
							helpForm.getEmptyMaterialMap().put(emptySapMaterialNo, materialDesc);
						}
					}
					
					
					{
						String emptySapMaterialNo = wagon.getWagonType().getRepairSapMaterialNo();
						SapMatDescPackEntity materialDesc = orderSpecialService.loadMaterialDesc(emptySapMaterialNo, locale.getLanguage());
						if (materialDesc != null) {
							helpForm.getEmptyMaterialMap().put(emptySapMaterialNo, materialDesc);
						}
					}
					
					{
						String emptySapMaterialNo = wagon.getWagonType().getMaintenanceSapMaterialNo();
						SapMatDescPackEntity materialDesc = orderSpecialService.loadMaterialDesc(emptySapMaterialNo, locale.getLanguage());
						if (materialDesc != null) {
							helpForm.getEmptyMaterialMap().put(emptySapMaterialNo, materialDesc);
						}
					}
					
					PlantBsEntity defaultServicePlant =
							orderSpecialService.getDefaultServicePlant();
					
					if (defaultServicePlant == null) {
						
						MessageUtils.addActionWarning(request,
								"wagondetail.error.createspecialorder.missingdefaultplant");
						
						forwardName = "fatalErrorPopup";
						return result = mapping.findForward(forwardName);
					}
					
					helpForm.setDefaultServicePlant(defaultServicePlant);
					
					// map of UomBsEntity
					List uomBsEntityList = getRefDataGenericsService().findBsEntityList(UomBsEntity.class);
					Map uomBsEntityMap = helpForm.getUomBsEntityMap();
					for (Iterator it = uomBsEntityList.iterator(); it.hasNext();) {
						UomBsEntity uomBs = (UomBsEntity) it.next();
						uomBsEntityMap.put(uomBs.getUomCd(), uomBs);
					}
					
					//set UOM of loaded quantity to Tons 
					helpForm.setQuantityUom(Constant.UOM_TONS);
					
					// map of MaintTypeTxEntity
					List maintTypeTxList = getOrderDetailService().getMaintTypeTxList(locale);
					Map MaintTypeTxMap = helpForm.getMaintTypeTxEntityMap();
					for (Iterator it = maintTypeTxList.iterator(); it.hasNext();) {
						MaintTypeTxEntity maintTx = (MaintTypeTxEntity) it.next();
						MaintTypeTxMap.put(maintTx.getMaintTypeTxPK().getMaintTypeBs().getMaintTypeCd(), maintTx);
						helpForm.getMaintTypeBsEntityList().add(maintTx.getMaintTypeTxPK().getMaintTypeBs());
						if (maintTx.getMaintTypeTxPK().getMaintTypeBs().getMaintTypeCd().equals(OrderSpecialForm.G1) ){
							helpForm.setMaintTypeBs(maintTypeTxList.indexOf(maintTx), true);
						}
					}
					
					
					//current load
					Map<String, Object>  currLoadMap = orderSpecialService.fetchCurrentLoad(
							wagonId, locale.getLanguage(), true);
					LoadEntity currLoad = (LoadEntity) currLoadMap.get(LoadEntity.class.getName());
					if (currLoad != null) {
						helpForm.setCurrLoad(currLoad);
						if (currLoad.getNetQuantity() != null) {
							helpForm.setCurrLoadQty(currLoad.getNetQuantity().toString());
						}
						if (currLoad.getGrossWeightT() != null) {
							//gross qty
							BigDecimal grossQty = currLoad.getGrossWeightT();
							//loaded qty
							helpForm.setQuantity(grossQty.toString());
						}
						
						helpForm.setCurrLoadPlantName(currLoad.getProductionPlant().getPlantName());
					}
					SapMaterialDescriptionEntity currLoadMatDesc= (SapMaterialDescriptionEntity) currLoadMap.get(SapMaterialDescriptionEntity.class.getName());
					if (currLoadMatDesc != null) {
						helpForm.setCurrLoadMatDesc(currLoadMatDesc.getTxt());
					}
					
					//previous load
					Map preLoadMap = orderSpecialService.fetchCurrentLoad(wagonId, locale.getLanguage(), false);
					LoadEntity preLoad = (LoadEntity)preLoadMap.get(LoadEntity.class.getName());
					if (preLoad != null) {
						helpForm.setPreLoad(preLoad);
						if ( preLoad.getNetQuantity() != null ) {
							helpForm.setPreLoadQty(preLoad.getNetQuantity().toString());
						}
						
						helpForm.setPreLoadPlantName(preLoad.getProductionPlant().getPlantName());
					}
					SapMaterialDescriptionEntity preLoadMatDesc= (SapMaterialDescriptionEntity) preLoadMap.get(SapMaterialDescriptionEntity.class.getName());
					if (preLoadMatDesc != null) {
						helpForm.setPreLoadMatDesc(preLoadMatDesc.getTxt());
					}
			
					if (wagon.getWagonKeeper().getMaintenanceEmailFlag()) {
						helpForm.setEmailCc(wagon.getWagonKeeper().getEmailAddresses());
					}
					
					helpForm.setEmailBcc(getConfigBean().getMailBcc());
					
					if (wagon.getLastKnownRailwayStation() != null) {
						ShippingpointEntity sp = orderSpecialService.fetchStationSp(wagon.getLastKnownRailwayStation().getRailwayStationId());
						if (sp != null){
							helpForm.setShippingpointId(sp.getShippingpointId());
							helpForm.setShippingpointName(sp.getShippingpointName());
							helpForm.setPlantCd(sp.getPlant().getPlantCd());
							helpForm.setPlantName(sp.getPlant().getPlantName());
							helpForm.setPlantRailwayStationCd(wagon.getLastKnownRailwayStation().getRailwayStationId().toString());
						}
					}
					
					CarrierEntity carrier = orderSpecialService.getDefaultRailCarrier();
					if (carrier != null) {
						helpForm.setCarrierId(carrier.getCarrierId());
						helpForm.setCarrierDisplayName(carrier.getDisplayName());
					}	
					
					OrderRepEntity or = helpForm.getOrderRep();
					
					// add predefined damage codes
					List orderRepDmgEntities = getOrderService().getOrderRepDmgEntitiesFromWagonDmgEntities(wagon.getDamageCdList());
					for (Object object : orderRepDmgEntities) {
						OrderRepDmgcodeTypeEntity entity = (OrderRepDmgcodeTypeEntity) object;
						entity.setOrderRep(or);
						or.getDamageCdList().add(entity);
					}
					List damageCodeTxList = getOrderDetailService().getDamageCodeTxList(locale, orderRepDmgEntities);
					helpForm.setDamageCodeTxList(damageCodeTxList);
					
					// add predefined email attachments
					List orderRepAttEntities = getOrderService().getOrderRepAttEntitiesFromWagonRepAttEntities(wagon.getWagonRepAttachments());
					for (Object object : orderRepAttEntities) {
						OrderRepAttEntity entity = (OrderRepAttEntity) object;
						entity.setOrderRep(or);
						or.getOrderRepAttachments().add(entity);
					}
					
					// add predefined email text
					String remarks = String.format("%n") + String.format("%n") + StringUtils.nvl(wagon.getWagonNoteMaintenance()) + String.format("%n");
					helpForm.setEmailText(remarks);
				}
				else {
					LOG.error("Cannot find wagon for a given wagon id (" + wagonIdParam + ")");
					
					MessageUtils.addActionWarning(request, "orderspecial.errors.notknownwagon");
					forwardName = "fatalErrorPopup";
				}
			
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
