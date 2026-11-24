/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispokarte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.ordercreation.DeliverySettingProductMap;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.business.services.interfaces.BestellungService;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.constants.CallerContext;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.OrderRFIDPopupForm;
import com.crh.logon.order.web.forms.dispokarte.DispokarteForm;
import com.crh.logon.order.web.forms.dispokarte.SalesAreaBean;
import com.crh.logon.order.web.utils.comparator.PlantComparator;
import com.crh.logon.persistence.entities.common.PlantSareaEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.refdata.BilltoRuletypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.BpSareaSofficeSgroupEntity;
import com.crh.logon.persistence.entities.views.SapSoldBillSareaSrchEntity;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispokarte_DispokarteForm"
 *  path="/dispokarte/DispokarteEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="DispokarteTile.def"
 * @struts.action-forward name="initfailed" path="/dispokarte/DispokarteShipToEntry.do"
 */
public class DispokarteEntryAction extends OrderActionSupport {

	public static final String PACKAGE_TYPE_BULK = "1"; 
	public static final String PACKAGE_TYPE_BAG = "2";
	
	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(DispokarteEntryAction.class);

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
			Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
			setSelectedTab("dispokarte", request);
			
			DispokarteForm helpForm;
			String forwardName = "success";
			OrderRFIDPopupForm.resetForm(request.getSession());
			
		    // Get the struts form.
		    helpForm = (DispokarteForm) form;
		    
		    DispokarteService dispoService = getServiceLocator().getDispokarteService();
		    Locale locale = request.getLocale();
		    
		    if (!helpForm.shipToInitialized()) {
		        return result = mapping.findForward("initfailed");
		    }
		    
            // -----------------------------------------------------------
            // -- default refdata
            // -----------------------------------------------------------
		    // load default (monthly) RuleType
		    loadDefaultRuleType( helpForm);
			
			initShippingpointForPlantsMap(helpForm);
			
            // load all deliveryTypes
            helpForm.setAllDeliveryTypeList(getRefDataGenericsService().findTxEntityList(DeliveryTypeTxEntity.class, false, locale, "txt" , true));
			
            
            loadSiloMaterialDescriptions(helpForm, locale);
            
            // -----------------------------------------------------------
            // -- load shipto specific data
            // -----------------------------------------------------------
            ShiptoEntity shipTo = helpForm.getShipTo();
			
            // if opening times are empty -> set default from config table
            if (shipTo.getOpen1From() == null) {
                shipTo.setOpen1From(new Long(getServiceLocator().getConfigService().getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN1FROMMIN)));
            }
            if (shipTo.getOpen1To() == null) {
                shipTo.setOpen1To(new Long(getServiceLocator().getConfigService().getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN1TOMIN)));
            }
            if (shipTo.getOpen2From() == null) {
                shipTo.setOpen2From(new Long(getServiceLocator().getConfigService().getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN2FROMMIN)));
            }
            if (shipTo.getOpen2To() == null) {
                shipTo.setOpen2To(new Long(getServiceLocator().getConfigService().getProperty(ConfigBean.CFG_APPDEFAULTSHIPTOOPEN2TOMIN)));
            }
			
            // if geofence radius is empty -> set default from config table
            // TODO: read defaults form config
            if (shipTo.getGeofenceRadius() == null) {
                shipTo.setGeofenceRadius(50L);
            }
			
            // shipto person data
            if (shipTo.getContactPerson() != null) {
                helpForm.setSelectedContactPersonId(shipTo.getContactPerson().getPersonId());
            }
            
            shipTo.setShiptoPersonList( dispoService.fetchContactPersons(shipTo.getShiptoId()) );
            
            if (shipTo.getShiptoType() != null) {
                helpForm.setSelectedShipToType(shipTo.getShiptoType().getShiptoTypeCd());
            }
            
            if (shipTo.getSignatureType() != null) {
                helpForm.setSelectedSignatureTypeCd(shipTo.getSignatureType().getSignatureTypeCd());
            }
			
			List <SareaEntity> salesAreas = dispoService.fetchSalesAreas(shipTo, helpForm.getSoldTo().getSapBpId());
            helpForm.setSalesAreas(salesAreas);
            
            // load data from shipTo and salesAreas
            helpForm.populateForm();
			
            // -----------------------------------------------------------
            // -- load refdata for single saleasAreas
            // -----------------------------------------------------------            
            
			final Map<Object, List<ShiptoSalesAreaError>>  invalidObjectInformationMap = new HashMap<Object, List<ShiptoSalesAreaError>>();
			
            for (SareaEntity sarea : salesAreas) {
                
                SalesAreaBean sareaBean = helpForm.getSalesAreaBean(sarea);
                
                final DeliverySettingSalesAreaMap salesAreaMapForSareaId = helpForm.getShiptoMap().getSalesAreaMapForSareaId(sarea.getSareaId());
                
				final DeliverySettingProductMap productMap = salesAreaMapForSareaId.getMaterialRestrictionDeliverySettings();
				final DeliverySettingProductMap serviceMap = salesAreaMapForSareaId.getServiceRestrictionDeliverySettings();
                sareaBean.setSalesAreaMap(salesAreaMapForSareaId);
                
                invalidObjectInformationMap.putAll(productMap.getInvalidMap());
                invalidObjectInformationMap.putAll(serviceMap.getInvalidMap());
                invalidObjectInformationMap.putAll(salesAreaMapForSareaId.getInvalidMap());
                
                
                BpSareaSofficeSgroupEntity sapBpSarea = dispoService.fetchBpSareaSofficeSgroup( shipTo.getShiptoSapBp().getSapBpId() ,sarea.getSareaId() );
                sareaBean.setSapBpSarea(sapBpSarea);
                 
                sareaBean.resetBilltos();
                sareaBean.setNewDeliverySettingList(new ArrayList());
                
                @SuppressWarnings("unchecked")
				final List<SapSoldBillSareaSrchEntity> billtos = dispoService.fetchSapSoldBillSareas(sarea, helpForm.getSoldTo());
                for (SapSoldBillSareaSrchEntity ssbss : billtos) {
                    sareaBean.addBillto(ssbss);
                }
			
				// initialize payers
				@SuppressWarnings("unused")
				Long sareaId = sarea.getSareaId();
				SapBpEntity soldTo = helpForm.getSoldTo();
				@SuppressWarnings("unused")
				Long soldToSapBpId = soldTo.getSapBpId();
			
                if ( sareaBean.getShiptoSarea() == null  || sareaBean.getShiptoSarea().getRestrictions() == null) {
                    continue;
                }
                @SuppressWarnings("unchecked")
				List<ShiptoSareaRestrEntity> restrList = sareaBean.getShiptoSarea().getRestrictions();
                
                for (ShiptoSareaRestrEntity restr : restrList) {                    
                    if ( restr.getFreeDeliveryFlag() == null ) {
                        restr.setFreeDeliveryFlag(false);
                    }
                }
            }
            
            helpForm.handleTruckWagonTypes();
			
            // set railChainShippingpoint and matChainShippingpoint values for the form
            if (helpForm.getDisplayRailChainSelection() && shipTo.getRailChainShippingpoint() != null) {
                helpForm.setRailChainShippingpointId(shipTo.getRailChainShippingpoint().getShippingpointId().toString());
                helpForm.setRailChainPlantCd(shipTo.getRailChainShippingpoint().getPlant().getPlantCd());
            }
            else {
                helpForm.setRailChainShippingpointId(Constant.INVALID_OBJECT_ID_STR);
                helpForm.setRailChainPlantCd(Constant.INVALID_OBJECT_ID_STR);            	
            }
			
            if (helpForm.getDisplayMatChainSelection() && shipTo.getMatChainShippingpoint() != null) {
                helpForm.setMatChainShippingpointId(shipTo.getMatChainShippingpoint().getShippingpointId().toString());
                helpForm.setMatChainPlantCd(shipTo.getMatChainShippingpoint().getPlant().getPlantCd());
            }
            else {
                helpForm.setMatChainShippingpointId(Constant.INVALID_OBJECT_ID_STR);
                helpForm.setMatChainPlantCd(Constant.INVALID_OBJECT_ID_STR);            	
            }

            if (shipTo.getPlant() != null) {
                helpForm.setPlantPlantCd(shipTo.getPlant().getPlantCd());
            }
            
	    	helpForm.setInvalidObjectInformationMap(invalidObjectInformationMap);
	    	addActionMessagesFromErrorCodes(invalidObjectInformationMap, request);
	    	
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


	/**
	 * @param helpForm
	 */
	private void initShippingpointForPlantsMap(DispokarteForm helpForm) {
        // load all plants
        final List <PlantBsEntity>plants = collectSareaPlants(helpForm);
        
        
		helpForm.setPlantList(plants);
		
		Map shippingPointMap = new HashMap();
		Map shippingPointsForPlantsMap = new HashMap();
		BestellungService bestService = getServiceLocator().getBestellungService();
		List<ShippingpointDTO> shippingPointList = bestService.fetchShippingpointList();
		if ( shippingPointList != null ) {
			for (ShippingpointDTO shippingpointDTO : shippingPointList) {
		        if (shippingPointMap.get(shippingpointDTO.getPlantCd()) == null) {
		            shippingPointsForPlantsMap.put(shippingpointDTO.getPlantCd(), new ArrayList());
		            shippingPointMap.put(shippingpointDTO.getPlantCd(), new ArrayList());
		        }
		        ((List )shippingPointMap.get(shippingpointDTO.getPlantCd())).add(shippingpointDTO);
		        ((List )shippingPointsForPlantsMap.get(shippingpointDTO.getPlantCd())).add(shippingpointDTO);
		        
		    }
		}
		helpForm.setAllPlantShippingPointMap(shippingPointsForPlantsMap);
		
	}
	

	/**
	 * Collect all the plants related to any sales areas. Currently the rail and material chain dropdowns
	 * has to use only these plants.
	 * @param helpForm
	 * @return
	 */
	private List <PlantBsEntity> collectSareaPlants(DispokarteForm helpForm) {
		Set<PlantBsEntity> plantsS = new HashSet<PlantBsEntity>();
		for (Object shiptoSarea : helpForm.getShiptoSalesAreas()) {
			ShiptoSareaEntity sa = (ShiptoSareaEntity) shiptoSarea;
			for (Object psarea : sa.getSarea().getPlantSareaEntities() ) {
				PlantSareaEntity psa = (PlantSareaEntity) psarea;
				plantsS.add(psa.getPlant());
			}
		}
		List<PlantBsEntity> plants = new ArrayList<PlantBsEntity>(plantsS);
		Collections.sort(plants, new PlantComparator());
		return plants;
	}


	/**
	 * DispokarteInvalidAttributeMarkerTag marker (and the jsp ListLose) currently uses restrictions to mark a row invalid,
	 * and is iterating through the restrictions, therefore here adding the delivery settings rather in the invalid map as 
	 * restrictions.
	 * This should be changed later, in case we are iterating through restrictions.
	 * 
	 * 
	 * @param invalidMap
	 * @return
	 */
	public static ActionMessages addActionMessagesFromErrorCodes ( Map invalidMap, HttpServletRequest request ) {
		ActionMessages errors = new ActionMessages();
		if (invalidMap != null && !invalidMap.isEmpty()) {
			
			Map<Object,List<ShiptoSalesAreaError>> filteredInvalidMap = filterAndHandleSalesAreaBlockWarnings(invalidMap, request);
			Collection<List<ShiptoSalesAreaError>> values = filteredInvalidMap.values();
			
			for(List<ShiptoSalesAreaError> le : values){
				for(ShiptoSalesAreaError e : le){
					MessageUtils.addActionWarning(request, e.getCorrespondingCustorderError().getMessage(CallerContext.DISPOKARTE));
				}
			}
		}
		return errors;
	}	
	
	/**
	 * Filtering the existing invalidMap of the shipto regarding to the logic of warning handling
	 * which find in the LRQ 005.2 Customer Block at Sales Area Level.
	 * If central block is presented it will be display only. If there are every sales are is blocked
	 * in the shipto or some sales are are blocked the corresponmding warning message will be displayed.
	 * 
	 * @param errors The errors of the DeliverySettingShiptoMap. It contains every error message
	 * regarding to that shipto 
	 * @param request The request
	 * @return The reference of the updated shiptoMap
	 */
	private static Map<Object, List<ShiptoSalesAreaError>> filterAndHandleSalesAreaBlockWarnings(
			Map<Object, List<ShiptoSalesAreaError>> errors, HttpServletRequest request) {
		// [[
		Map<Object,List<ShiptoSalesAreaError>> result = null;
		Object[][] params = {{"errors", errors},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(DispokarteEntryAction.class, "filterAndHandleSalesAreaBlockWarnings", params);
		}
		// ]]
		try {
			result = new HashMap<Object, List<ShiptoSalesAreaError>>(errors);
			
			List<Object> blockAllWarning = new ArrayList<Object>();
			List<Object> blockSomeWarning = new ArrayList<Object>();
			List<Object> blockCentralWarning = new ArrayList<Object>();
			
			for(Map.Entry<Object, List<ShiptoSalesAreaError>> entry : result.entrySet()) {
				if(entry.getValue().contains(ShiptoSalesAreaError.BLOCK_BP)) {
					blockCentralWarning.add(entry.getKey());
					continue;
				}
				else if(entry.getValue().contains(ShiptoSalesAreaError.BLOCK_ALL_SALES_AREA)) {
					blockAllWarning.add(entry.getKey());
				}
				else if(entry.getValue().contains(ShiptoSalesAreaError.BLOCK_SOME_SALES_AREA)) {
					blockSomeWarning.add(entry.getKey());
				}
			}
			
			//If Block_BP presented we dont want to display any other error according to the blocking
			if(!blockCentralWarning.isEmpty()) {
				result.keySet().removeAll(blockCentralWarning);
				CustorderError blockBpError = ShiptoSalesAreaError.BLOCK_BP.getCorrespondingCustorderError();
				MessageUtils.addActionWarning(request, blockBpError.getMessage(CallerContext.DISPOKARTE));
				return result;
			}
			
			if(!blockAllWarning.isEmpty()) {
				result.keySet().removeAll(blockAllWarning);
				CustorderError blockAllError = ShiptoSalesAreaError.BLOCK_ALL_SALES_AREA.getCorrespondingCustorderError();
				MessageUtils.addActionWarning(request, blockAllError.getMessage(CallerContext.DISPOKARTE));
			}
			else if(!blockSomeWarning.isEmpty()) {
				result.keySet().removeAll(blockSomeWarning);
				CustorderError blockSomeError = ShiptoSalesAreaError.BLOCK_SOME_SALES_AREA.getCorrespondingCustorderError();
				MessageUtils.addActionWarning(request, blockSomeError.getMessage(CallerContext.DISPOKARTE));
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
	
	private void loadDefaultRuleType(DispokarteForm helpForm) {
		@SuppressWarnings("unchecked")
		List<BilltoRuletypeBsEntity> ruleTypeList = getServiceLocator().getRefDataGenericsService().findBsEntityList(BilltoRuletypeBsEntity.class);
	    BilltoRuletypeBsEntity defaultRuleType = null;
	    if (ruleTypeList != null) {
	    	for (BilltoRuletypeBsEntity ruleType : ruleTypeList) {
	    		if ( new Long(0).equals(ruleType.getBilltoRuletypeCd())) {
	    			defaultRuleType = ruleType;
	    			break;
	    		}
	    	}
	    }
	    helpForm.setDefaultRuleType(defaultRuleType);
	}
}