/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispokarte;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.ordercreation.DeliveryOptionConfigurationDTO;
import com.crh.logon.business.ordercreation.DeliverySettingProductMap;
import com.crh.logon.business.ordercreation.DeliverySettingSalesAreaMap;
import com.crh.logon.business.ordercreation.dto.CommercialCarrierDTO;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO;
import com.crh.logon.business.ordercreation.dto.DeliverySettingDTO.DeliverySettingLangDepData;
import com.crh.logon.business.ordercreation.dto.ExecutingCarrierDTO;
import com.crh.logon.business.ordercreation.dto.LoadingPointDTO;
import com.crh.logon.business.ordercreation.dto.ShippingpointDTO;
import com.crh.logon.business.ordercreation.dto.SupplyPlantDTO;
import com.crh.logon.business.ordercreation.dto.TruckTypeWrapper;
import com.crh.logon.business.ordercreation.dto.TruckWagonTypeWrapper;
import com.crh.logon.business.ordercreation.dto.WagonTypeWrapper;
import com.crh.logon.business.services.interfaces.DeliveryDateService;
import com.crh.logon.business.services.interfaces.DeliverySettingService;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.interfaces.QuantityRestrictionService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.utils.DeliveryDistanceValidationUtils;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.QuantityRestrictionCalculation;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.business.services.utils.validation.ValidationErrorReason;
import com.crh.logon.constants.CallerContext;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.order.web.beans.ContractItemDataBean;
import com.crh.logon.order.web.beans.ContractItemOrderItemBean;
import com.crh.logon.dto.DistanceValidationEditGuiBean;
import com.crh.logon.order.web.forms.OrderRFIDPopupForm;
import com.crh.logon.order.web.forms.dispokarte.DispokarteForm;
import com.crh.logon.order.web.forms.dispokarte.SalesAreaBean;
import com.crh.logon.order.web.utils.DeliveryOptionConfigurationWrapper;
import com.crh.logon.order.web.utils.comparator.BilltoComparator;
import com.crh.logon.order.web.utils.comparator.DeliverySettingWrapperComparator;
import com.crh.logon.persistence.entities.common.FireqMaterialMixdesignEntity;
import com.crh.logon.persistence.entities.common.PersonEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoPersonEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrBtrulEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.ShiptoTruckTypeEntity;
import com.crh.logon.persistence.entities.common.ShiptoWagonTypeEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.ShiptoTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SignatureTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.TruckTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialDescriptionEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.ContractItemOrderItemListEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.UserProvider;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispokarte_DispokarteForm"
 *  path="/dispokarte/Dispokarte" scope="session"
 *  validate="true" input="DispokarteTile.def"
 *
 * @struts.action-forward name="input" path="DispokarteTile.def"
 * @struts.action-forward name="saveFailed" path="DispokarteTile.def"
 * @struts.action-forward name="initfailed" path="/dispokarte/DispokarteShipToEntry.do"
 * @struts.action-forward name="TabShipTo" path="DispokarteTabShipToTile.def"
 * @struts.action-forward name="TabSalesArea" path="DispokarteTabVkoTile.def"
 * @struts.action-forward name="TabLose" path="DispokarteListLoseTile.def"
 * @struts.action-forward name="TabSack" path="DispokarteListSackTile.def"
 * @struts.action-forward name="reloadMatFlow" path="MatFlowDropdown.def"
 * @struts.action-forward name="reloadPlant" path="PlantDropdown.def"
 * @struts.action-forward name="reloadRailChain" path="RailChainDropdown.def"
 * @struts.action-forward name="loadShiptoTypeRoad" path="ShiptoTypeRoad.def"
 * @struts.action-forward name="loadShiptoTypeRail" path="ShiptoTypeRail.def"
 * @struts.action-forward name="select" path="/dispokarte/DispokarteShipTo.do";
 * @struts.action-forward name="contractData" path="ContractData.def"
 * @struts.action-forward name="contractItemData" path="ContractItemData.def"
 */
public class DispokarteAction extends DispokarteShipToAction {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispokarteAction.class);
    
    private static final String LOAD_SHIPTO_TYPE_ROAD = "loadShiptoTypeRoad";
    private static final String LOAD_SHIPTO_TYPE_RAIL = "loadShiptoTypeRail";

    public static final String TAB_LOSE = "TabLose";
    
    private final static String CONTRACT_ITEM_ORDER_ITEM_DELETED_FLAG = "contractitemorderitem.deletedflag";

	private static final DeliverySettingDTO DELIVERY_SETTING_NOT_IN_LIST = null;

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
		    // Get the struts form.
			DispokarteForm helpForm = (DispokarteForm)form;
			String forwardName = "input";
			
			setSelectedTab("dispokarte", request);
			
			final SalesAreaBean currSalesAreaBean = helpForm.getCurrSalesAreaBean();
			if (request.getParameter("setTab") != null) {
				if (helpForm.shipToInitialized()){
					forwardName = helpForm.getTab();
					if (forwardName.startsWith(DispokarteForm.TAB_SALES_AREA)){
						forwardName = DispokarteForm.TAB_SALES_AREA;
						helpForm.setCurrSalesAreaIndex(Integer.parseInt(helpForm.getTab().substring(DispokarteForm.TAB_SALES_AREA.length())));
						validateOnEnterTabSarea(helpForm, request);
						loadBlockTypeForSoldToOnCurrentSalesArea(helpForm);
					}
					if (forwardName.startsWith(DispokarteForm.TAB_CONTRACT_DATA)){
						forwardName = DispokarteForm.TAB_CONTRACT_DATA;
					}
				}
				else {
					forwardName = "reloadwindow";
					setReloadWindowTarget(mapping.findForward("initfailed").getPath(), request);
				}
		    }
			else if (request.getParameter("setShiptoTypeTab") != null) {
				
				if (DispokarteForm.TAB_SHIPTO_TYPE_RAIL.equals(helpForm.getShiptoTypeTab())) {
					forwardName = LOAD_SHIPTO_TYPE_RAIL;
				}
				else {
					forwardName = LOAD_SHIPTO_TYPE_ROAD;
				}
			}
			else if (request.getParameter("setListTab") != null) {
				forwardName = TAB_LOSE;
		    }
			else if (request.getParameter("uomChanged") != null) {
				forwardName = TAB_LOSE;
		    }
			else if (request.getParameter("billtosChanged") != null) {
				//TODO:traverse complete restriction list (incl rules)
				//	  and match the billtos
				helpForm.populateForm();
			
				purgeRhythmRules( helpForm );
			
				forwardName = TAB_LOSE;
			}
			else if (request.getParameter("freeDeliveryChanged") != null) {
				forwardName = TAB_LOSE;
			}
			else if (request.getParameter("modifyMatChainPlant") != null) {
				String selectedPlant = request.getParameter("modifyMatChainPlant");
				helpForm.setMatChainPlantCd(selectedPlant);
				helpForm.setMatChainShippingpointId(Constant.INVALID_OBJECT_ID_STR);
				forwardName = "reloadMatFlow";
			}
			else if (request.getParameter("shiptoTypeChanged") != null) {
				String shiptoType = request.getParameter("shiptoType");
				helpForm.setSelectedShipToType((shiptoType == null || Constant.INVALID_OBJECT_ID_STR.equals(shiptoType)) ? Constant.INVALID_OBJECT_ID : Long.parseLong(shiptoType));
				boolean displayPlantSelection = helpForm.getPlantSelection();
				if (!displayPlantSelection) {
					helpForm.setPlantPlantCd(Constant.INVALID_OBJECT_ID_STR);
				}
				boolean displayMatChainSelection = helpForm.getDisplayMatChainSelection();
				if (!displayMatChainSelection) {
					helpForm.setMatChainPlantCd(Constant.INVALID_OBJECT_ID_STR);
					helpForm.setMatChainShippingpointId(Constant.INVALID_OBJECT_ID_STR);
				}
				forwardName = "reloadPlant";
				if (displayMatChainSelection) {
					forwardName = "reloadMatFlow";
				}
			}
			else if (request.getParameter("modifyRailChainPlant") != null) {
				String selectedPlant = request.getParameter("modifyRailChainPlant");
				helpForm.setRailChainPlantCd(selectedPlant);
				forwardName = "reloadRailChain";
			}
			else if (request.getParameter("addMaterial") != null) {
				forwardName = TAB_LOSE;
				final String compositeIdentifier = helpForm.getMaterialComboSelectedCompositeIdentifier();
				
				boolean isServiceOnly = false;
				if ("true".equals(request.getParameter("isServiceOnly"))) {
					isServiceOnly = true;
				}
				
				DeliverySettingDTO ds = createNewRestriction(currSalesAreaBean,
						compositeIdentifier, isServiceOnly);

				Comparator<DeliverySettingDTO> c = new DeliverySettingWrapperComparator();
				currSalesAreaBean.getNewDeliverySettingList().add(ds);
				Collections.sort(currSalesAreaBean.getRestrictionDeliverySettings(), c);

				helpForm.populateForm();
		    }
			else if (request.getParameter("deleteRestriction") != null) {
				Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_W);
				forwardName = TAB_LOSE;
				if (helpForm.getRestrToRemove() >= 0){
					final DeliverySettingDTO deliverySettingDTO = helpForm.getCurrSalesAreaBean().getRestrictionDeliverySettings().get(helpForm.getRestrToRemove());
					
					helpForm.getCurrSalesAreaBean().getSalesAreaMap().getServiceRestrictionDeliverySettings().getPossibleDeliverySettings().remove(deliverySettingDTO);
					helpForm.getCurrSalesAreaBean().getSalesAreaMap().getMaterialRestrictionDeliverySettings().getPossibleDeliverySettings().remove(deliverySettingDTO);
					helpForm.getCurrSalesAreaBean().getNewDeliverySettingList().remove(deliverySettingDTO);
					
					helpForm.getCurrSalesAreaBean().getShiptoSarea().getRestrictions().remove(deliverySettingDTO.getShiptoSareaRestr());
					helpForm.populateForm();
			
				}				
			}			
			else if (request.getParameter("loadContractItemData") != null) {
				loadContractItemData(request, helpForm);
				forwardName = "contractItemData";
			}
			else if (!helpForm.shipToInitialized()) {
		    	forwardName = "initfailed";
			} 
			else if (helpForm.getFormAction().equals("save")) {
				
				validateForSave(helpForm, request);
				
				if ( (MessageUtils.getActionWarnings(request) != null && MessageUtils.getActionWarnings(request).size() > 0)
						|| (MessageUtils.getActionErrors(request) != null && MessageUtils.getActionErrors(request).size() > 0)) {
					forwardName = "saveFailed";
					return result = mapping.findForward(forwardName);
			    }
				
				Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_W);
				// get the ShiptoTypeBsEntity for shiptoType
				
				if ( helpForm.getSelectedShipToType() > Constant.INVALID_OBJECT_ID ) {
					ShiptoTypeBsEntity stEntity = getServiceLocator().getRefDataGenericsService().findBsEntity(ShiptoTypeBsEntity.class, helpForm.getSelectedShipToType());
					helpForm.getShipTo().setShiptoType(stEntity);
				} 
			/*				else {
					helpForm.getShipTo().setShiptoType(null);
				}
				*/
				// get the signatureTypeBsEntity for shiptoType
				
				if ( helpForm.getSelectedSignatureTypeCd() > Constant.INVALID_OBJECT_ID ) {
					SignatureTypeBsEntity signEntity = getServiceLocator().getRefDataGenericsService().findBsEntity(SignatureTypeBsEntity.class,  helpForm.getSelectedSignatureTypeCd() );
					helpForm.getShipTo().setSignatureType(signEntity);
				} 
			/*				else {
					helpForm.getShipTo().setShiptoType(null);
				}*/
				
				// eliminate null checkbox values (setting to false)
				if ( helpForm.getShipTo().getCoordFixFlag() == null ) {
					helpForm.getShipTo().setCoordFixFlag(new Boolean(false));
				}
				if ( helpForm.getShipTo().getDailyOrderContactFlag() == null ) {
					helpForm.getShipTo().setDailyOrderContactFlag(new Boolean(false));
				}
				
				handleTruckWagonTypes(helpForm);
				
				List<ShiptoSareaEntity> salesAreaList = helpForm.getShiptoSalesAreas();
				if (salesAreaList != null) {
					for ( ShiptoSareaEntity sarea : salesAreaList) {
						if ( sarea.getFreeDeliveryFlag() == null ) {
							sarea.setFreeDeliveryFlag(new Boolean(false));
						}
						if ( sarea.getReleaseSapOptionalFlag() == null ) {
							sarea.setReleaseSapOptionalFlag(new Boolean(false));
						}
						
						handleBilltoList(sarea);
						
						List<ShiptoSareaRestrEntity> restrictionList = sarea.getRestrictions();
						if ( restrictionList == null) {
							continue;
						}
						
						for ( ShiptoSareaRestrEntity restr : restrictionList) {
							
							if ( restr.getRules() != null ) {
								Map<Long, SapBpEntity> sapBpMap = new HashMap<Long, SapBpEntity>();
								for ( ShiptoSareaRestrBtrulEntity rule : restr.getRules()) {
									Long sapBpId = rule.getBilltoSapBp().getSapBpId();
									if ( sapBpMap.containsKey(sapBpId)) {
										rule.setBilltoSapBp( sapBpMap.get(sapBpId) );
									}
									else {
										SapBpEntity sapBp = getServiceLocator().getDispokarteService().getBilltoSapBpById(sapBpId);
										rule.setBilltoSapBp(sapBp);
										sapBpMap.put(sapBpId, sapBp);
									}
								}
							}
							else {
								restr.setRules(new ArrayList<ShiptoSareaRestrBtrulEntity>());
							}

							if ( restr.getBilltoSapBp() != null ) {
								SapBpEntity sapBp = getServiceLocator().getDispokarteService().getBilltoSapBpById(restr.getBilltoSapBp().getSapBpId());
								restr.setBilltoSapBp(sapBp);
							}

							SapBpEntity payerSapBp = restr.getPayerSapBp();

							if (payerSapBp != null) {
								DispokarteService dispokarteService =
									getServiceLocator().getDispokarteService();
								Long payerSapBpId = payerSapBp.getSapBpId();
								payerSapBp = dispokarteService.getPayerSapBpById(payerSapBpId);
								restr.setPayerSapBp(payerSapBp);
							}
						}
					}
				}
				
				// contact person
				for(ShiptoPersonEntity shiptoPerson : helpForm.getShipTo().getShiptoPersonList()){
					PersonEntity person = shiptoPerson.getPerson();
					if (person.getPersonId() == null) {
						person.setActiveFlag(true);
						shiptoPerson.setPerson(getServiceLocator().getDispokarteService().persistPerson(person));
						shiptoPerson.setShipto(helpForm.getShipTo());
						ShiptoPersonEntity retrievedShipToPerson = getServiceLocator().getDispokarteService().persistShiptoPerson(shiptoPerson);
						person = retrievedShipToPerson.getPerson();
					}
					else {
						person = getServiceLocator().getDispokarteService().mergePerson(person);
					}
					if ( helpForm.getSelectedContactPersonId() == person.getPersonId().longValue() ) {
						helpForm.getShipTo().setContactPerson(person);
					}
				}
				
				// remove dummy shiptoSiloIds (freshly added silos have this)
				List<ShiptoSiloEntity> silos = helpForm.getShipTo().getSilos();
				if ( silos != null ) {
					for(ShiptoSiloEntity silo : silos) {
						if (silo.getShiptoSiloId() != null 
								&& silo.getShiptoSiloId().longValue() < 0
								&& silo.getShiptoSiloId().longValue() > Constant.SILO_TEST_DATA_UPPER_LIMIT) 
						{
							silo.setShiptoSiloId(null);
							
						}
					}
				}
				
				// set plant_cd
				if ( !StringUtils.empty(helpForm.getPlantPlantCd()) && !Constant.INVALID_OBJECT_ID_STR.equals(helpForm.getPlantPlantCd())
						&& helpForm.getSelectedShipToType() == Constant.SHIPTO_TYPE_PLANT) {
					PlantBsEntity plantBsEntity = getRefDataGenericsService().findBsEntity(PlantBsEntity.class, helpForm.getPlantPlantCd());
					helpForm.getShipTo().setPlant(plantBsEntity);
				}
				else {
					helpForm.getShipTo().setPlant(null);
				}
				
				// set matChainShippingpoint and railChainShippingpoint
				
				if ( !StringUtils.empty(helpForm.getMatChainShippingpointId()) && !Constant.INVALID_OBJECT_ID_STR.equals(helpForm.getMatChainShippingpointId()) ) {
					ShippingpointEntity matChainSP = getShippingPointService().loadShippingPoint(Long.parseLong(helpForm.getMatChainShippingpointId()));
					helpForm.getShipTo().setMatChainShippingpoint(matChainSP);
				}
				else {
					helpForm.getShipTo().setMatChainShippingpoint(null);
				}
				
				if ( !StringUtils.empty(helpForm.getRailChainShippingpointId()) && !Constant.INVALID_OBJECT_ID_STR.equals(helpForm.getRailChainShippingpointId()) ) {
					ShippingpointEntity railChainSP = getShippingPointService().loadShippingPoint(Long.parseLong(helpForm.getRailChainShippingpointId()));
					helpForm.getShipTo().setRailChainShippingpoint(railChainSP);
				}
				else {
					helpForm.getShipTo().setRailChainShippingpoint(null);
				}
				
				// 20100129 fix for ADN49874
				if ( helpForm.getShipTo().getRailHoldBackReOrderFlag() == null ) {
					helpForm.getShipTo().setRailHoldBackReOrderFlag(Boolean.FALSE);
				}
				
				try{
					getServiceLocator().getDispokarteService().mergeShipto(helpForm.getShipTo(), helpForm.getShiptoSalesAreas());
				    helpForm.setSoldToId(helpForm.getSoldTo().getSapId());
				    helpForm.setShipToId(helpForm.getShipTo().getShiptoSapBp().getSapId());
					MessageUtils.addActionMessage(request, "dispokarte.success.save");
				    helpForm.setSoldToId(helpForm.getSoldTo().getSapId());
				    helpForm.setShipToId(helpForm.getShipTo().getShiptoSapBp().getSapId());				
					OrderRFIDPopupForm.resetForm(request.getSession());
				    return super.execute(mapping, helpForm, request, response);
				}
				catch (RuntimeException re) {
					if (re instanceof LogonException) {
						// TODO (emoke Jun 3, 2009):this is not clear how it is thrown... sometimes EJB exception sometimes simple Logonexception
						Reason reason = ((LogonException)re).getReason();
						if (reason.getReason().equals(Reason.RES_RFID_OBJECT_LABEL_NOT_UNIQUE.getReason())){
							MessageUtils.addActionWarning(request, RFIDBean.getErrorMessage(Constant.RFIDOBJECT_TYPE_SHIPTO_SILO));
						}
						else {
							throw re;
						}
					}
					else if (re.getCause() instanceof LogonException) {
						LogonException le = (LogonException)re.getCause();
						Reason reason = le.getReason();
						if (reason.getReason().equals(Reason.RES_RFID_OBJECT_LABEL_NOT_UNIQUE.getReason())){
							MessageUtils.addActionWarning(request, RFIDBean.getErrorMessage(Constant.RFIDOBJECT_TYPE_SHIPTO_SILO));
						}
						else {
							throw le;
						}
					}
					else {
						throw re;
					}
				}
		    } 
			else if (helpForm.getFormAction().equals("reset")) {
			    helpForm.setSoldToId(helpForm.getSoldTo().getSapId());
			    helpForm.setShipToId(helpForm.getShipTo().getShiptoSapBp().getSapId());				
				OrderRFIDPopupForm.resetForm(request.getSession());
			    return super.execute(mapping, helpForm, request, response);
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

	/**
	 * Creates a delivery
	 * @param currentSalesArea
	 * @param sapMaterialIdToAdd
	 * @param contractItemId
	 * @return
	 */
	private DeliverySettingDTO createNewRestriction(final SalesAreaBean currentSalesArea,
			final String compositeIdentifier, boolean isServiceOnly) {
		// [[
		DeliverySettingDTO result = null;
		Object[][] params = {{"currSalesAreaBean", currentSalesArea},
							{"compositeIdentifier", compositeIdentifier}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createNewRestriction", params);
		}
		// ]]
		try {
			ShiptoSareaRestrEntity newRestriction = new ShiptoSareaRestrEntity();
			DeliverySettingSalesAreaMap currentSalesAreaMap = currentSalesArea.getSalesAreaMap();
			List<DeliverySettingDTO> currentBasicDeliverySettings = currentSalesAreaMap.getBasicDeliverySettings();
			DeliverySettingDTO deliverySettingByCompositeIdentifier = findDeliverySettingByCompositeIdentifier(
					compositeIdentifier, currentBasicDeliverySettings);
			
			PlantBsEntity productionPlant = deliverySettingByCompositeIdentifier.getProductionPlant();
			newRestriction.setProductionPlant(productionPlant);
			BigDecimal quantity = deliverySettingByCompositeIdentifier.getQuantity();
			newRestriction.setQuantity(quantity);
			SapMaterialEntity sapMaterial = deliverySettingByCompositeIdentifier.getSapMaterial();
			newRestriction.setSapMaterial(sapMaterial);
			ShiptoSareaEntity shiptoSarea = currentSalesArea.getShiptoSarea();
			newRestriction.setShiptoSarea(shiptoSarea);
			newRestriction.setSapMaterial(sapMaterial);
			SapBpEntity payerSapBusinessPartner = deliverySettingByCompositeIdentifier.getPayerSapBp();
			newRestriction.setPayerSapBp(payerSapBusinessPartner);
			SapBpEntity billtoSapBusinessPartner = deliverySettingByCompositeIdentifier.getBilltoSapBp();
			newRestriction.setBilltoSapBp(billtoSapBusinessPartner);
			UomBsEntity unitOfMeasure = deliverySettingByCompositeIdentifier.getUom();
			newRestriction.setUom(unitOfMeasure);
			SapContractItemEntity sapContractItem = deliverySettingByCompositeIdentifier.getSapContractItem();
			newRestriction.setSapContractItem(sapContractItem);

			newRestriction.setEnabledFlag(true);
			newRestriction.setReleaseSapFlag(false);
			newRestriction.setFreeDeliveryFlag(false);
			newRestriction.setExactCustorderQtyFlag(false);
			newRestriction.setExactSdorderQtyFlag(false);
			newRestriction.setOpenCustorderFlag(false);
			newRestriction.setSapSuppressMinChargeFlag(false);
			newRestriction.setOnHoldOrderAndLockedFlag(false);
			newRestriction.setAvailableInCustWebFlag(false);
			newRestriction.setAvailableInPickupAppFlag(false);
			newRestriction.setAutoCarrierAssignmentFlag(false);
			newRestriction.setAutoPoolAssignmentFlag(false);
			newRestriction.setAutoReleaseFlag(false);
			
			if (isServiceOnly) {
				ServiceLocator serviceLocator = getServiceLocator();
				RefDataGenericsService refDataGenericsService = serviceLocator.getRefDataGenericsService();
				DeliveryTypeBsEntity serviceDeliveryType = refDataGenericsService.findBsEntity(DeliveryTypeBsEntity.class, Constant.DELIVERY_TYPE_SERVICE);
				newRestriction.setDeliveryType(serviceDeliveryType);
			}
			
			List<ShiptoSareaRestrEntity> restrictions = shiptoSarea.getRestrictions();
			restrictions.add(newRestriction);
			
			TimeZone timeZone = UserProvider.getTimezone();
			
			final DeliverySettingDTO deliverySetting = new DeliverySettingDTO(newRestriction, timeZone);
			PlantBsEntity supplyPlant = deliverySettingByCompositeIdentifier.getSupplyPlant();
			deliverySetting.setSupplyPlant(supplyPlant);
			
			DeliverySettingLangDepData additionalData = deliverySetting.getAdditionalData();
			String materialPackageTxt = deliverySettingByCompositeIdentifier.getMaterialPackageTxt();
			additionalData.setMaterialPackageTxt(materialPackageTxt);
			String txt = deliverySettingByCompositeIdentifier.getTxt();
			additionalData.setTxt(txt);
			
			DeliveryOptionConfigurationDTO options = DeliveryOptionConfigurationWrapper.getDeliveryOptionConfiguration(currentSalesAreaMap, deliverySetting);
			
			if (isServiceOnly) {
				if (options.getDeliveryTypeList() != null) {
					Iterator<DeliveryTypeTxEntity> iter = options.getDeliveryTypeList().iterator();
					while (iter.hasNext()) {
						DeliveryTypeTxEntity actDeliveryTypeTx = iter.next();
						if (actDeliveryTypeTx.getDeliveryTypeTxPK().getDeliveryTypeBs().getDeliveryTypeCd() != Constant.DELIVERY_TYPE_SERVICE) {
							iter.remove();
						}
					}
				}
				
				// TODO szantaip these settings are not really needed as the DeliveryOptionConfigurationDTO sets empty lists by default anyway
				// just leaving them here as not sure why it was here in the first place
				options.setCommercialCarrierList(new ArrayList<CommercialCarrierDTO>());
				options.setExecutingCarrierList(new ArrayList<ExecutingCarrierDTO>());
				options.setDeliveryFreqEveryTimeUomMap(new HashMap<String, String>());
				options.setDeliveryTimeToleranceUomMap(new HashMap<String, String>());
				options.setLoadingpointList(new ArrayList<LoadingPointDTO>());
				options.setMaterialMixDesignList(new ArrayList<FireqMaterialMixdesignEntity>());
				options.setPoolList(new ArrayList<PoolTxEntity>());
				options.setShippingpoinList(new ArrayList<ShippingpointDTO>());
				options.setSiloList(new ArrayList<ShiptoSiloEntity>());
				options.setSupplyPlantList(new ArrayList<SupplyPlantDTO>());
				options.setVehicleTypeWrapperList(new ArrayList<TruckWagonTypeWrapper>());
			}
			
			deliverySetting.setDeliveryOptionConfiguration(options);
			return result = deliverySetting;
				
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public static DeliverySettingDTO findDeliverySettingByCompositeIdentifier(
			final String compositeIdentifier,
			List<DeliverySettingDTO> deliverySettings) {
		for (DeliverySettingDTO currentDeliverSetting : deliverySettings) {
			String currentCompositeIdentifier = currentDeliverSetting.getCompositeIdentifier();
			if (currentCompositeIdentifier.equals(compositeIdentifier)) {
				return currentDeliverSetting;
			}
		}
		return DELIVERY_SETTING_NOT_IN_LIST;
	}
	
	private void handleBilltoList (ShiptoSareaEntity sarea) {
		// [[
		Object[][] params = {{"sarea", sarea}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handleBilltoList", params);
		}
		// ]]
		try {
			if ( sarea.getBilltos() == null) {
				sarea.setBilltos(new ArrayList<ShiptoBilltoEntity>());
				return;
			}
			for (int i = 0; i < sarea.getBilltos().size(); i++) {
				ShiptoBilltoEntity shiptoBillto = (ShiptoBilltoEntity) sarea.getBilltos().get(i);
				if ( shiptoBillto.getShiptoBilltoId() != null ) {
					continue;
				}
				SapBpEntity billto = shiptoBillto.getBilltoSapBp();
				if ( billto.getCtlModTs() == null ) {
					SapBpEntity sapBp = getServiceLocator().getDispokarteService().getBilltoSapBpById(billto.getSapBpId());
					shiptoBillto.setBilltoSapBp(sapBp);
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/*
	 * review billto selection rhythm rules and remove the ones related to removed billtos
	 */
	private void purgeRhythmRules(DispokarteForm helpForm) {
		// [[
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "purgeRhythmRules", params);
		}
		// ]]
		try {
			
			List<ShiptoSareaRestrEntity> restrictionList = helpForm.getCurrSalesAreaBean().getShiptoSarea().getRestrictions();
			if ( restrictionList == null) {
				return;
			}
			
			Set<Long> billtoIdSet = new HashSet<Long>();
			List<ShiptoBilltoEntity> billtoList = helpForm.getCurrSalesAreaBean().getShiptoSarea().getBilltos();
			Collections.sort(billtoList, new BilltoComparator());
			
			if ( billtoList != null ) {
				for ( ShiptoBilltoEntity billto : billtoList ) {
					billtoIdSet.add(billto.getBilltoSapBp().getSapBpId());
				}
			}
						
			for (ShiptoSareaRestrEntity restr : restrictionList) {
				if (restr.getRules() == null) {
					continue;
				}
				boolean replaceRuleList = false;
				List<ShiptoSareaRestrBtrulEntity> newRuleList = new ArrayList<ShiptoSareaRestrBtrulEntity>();
				for(ShiptoSareaRestrBtrulEntity rule : restr.getRules() ){
					if ( billtoIdSet.contains(rule.getBilltoSapBp().getSapBpId()) ) {
						newRuleList.add(rule);
					}
					else {
						replaceRuleList = true;
					}
				}
				if (replaceRuleList) {
					restr.setRules(newRuleList);
				}
			}
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private void validateForSave(DispokarteForm helpForm, HttpServletRequest request) {
    	ShiptoEntity shipTo = helpForm.getShipTo();
    	SalesAreaBean currSalesAreaBean = helpForm.getCurrSalesAreaBean();  	
    	validateLeadSettings(request, currSalesAreaBean);
		if (shipTo != null){
    		try {
    			Date von1 = DateTimeUtils.parseHoursMinutes(helpForm.getOpen1From());
        		Date bis1 = DateTimeUtils.parseHoursMinutes(helpForm.getOpen1To());
        		Date von2 = DateTimeUtils.parseHoursMinutes(helpForm.getOpen2From());
        		Date bis2 = DateTimeUtils.parseHoursMinutes(helpForm.getOpen2To());
        		
        		if ((von1 == null) || (bis1 == null) || (von2 == null) || (bis2 == null)) {
        			MessageUtils.addActionWarning(request,"dispokarte.errors.opennotcomplete");
    	        }
        		else if (bis1.before(von1)){
        			MessageUtils.addActionWarning(request,"dispokarte.errors.openwrongorder");
    	        }
        		else if (von2.before(bis1)){
        			MessageUtils.addActionWarning(request,"dispokarte.errors.openwrongorder");
    	        }
        		else if (bis2.before(von2)){
        			MessageUtils.addActionWarning(request,"dispokarte.errors.openwrongorder");
    	        }
        		
        		/**
        		 * Do not allow dispocard saving, when available in terminal flag is ON,
        		 * but the restrictions are not valid for the Terminal Application.
        		 * 
        		 * Do not allow dispocard saving, when auto payer-determination flags are ON,
        		 * but the payer in NOT empty
        		 */
        		for (DeliverySettingDTO setting: currSalesAreaBean.getRestrictionDeliverySettings()) {
        			ShiptoSareaRestrEntity shiptoSareaRestr = setting.getShiptoSareaRestr();
					if (shiptoSareaRestr != null && shiptoSareaRestr.getAvailableInPickupAppFlag() &&
						!setting.isAvailableInPickupAppFlagEnabled()) {
        				MessageUtils.addActionWarning(request,"dispokarte.errors.invalid.AVAILABLE_IN_PICKUP_APP_FLAG");
        			}
					if (!setting.isAutoPayerSettingValid()) {
        				MessageUtils.addActionWarning(request,"dispokarte.errors.invalid.AUTOPAYER");
        			}
        		}
        		shipTo.setOpen1From(DateTimeUtils.getMinAfterMidnight(von1));
        		shipTo.setOpen1To(DateTimeUtils.getMinAfterMidnight(bis1));
        		shipTo.setOpen2From(DateTimeUtils.getMinAfterMidnight(von2));
        		shipTo.setOpen2To(DateTimeUtils.getMinAfterMidnight(bis2));
        		
        		if ( StringUtils.notEmpty(shipTo.getNoteDelivery()) && shipTo.getNoteDelivery().length() > ShiptoEntity.DBSIZE_NOTE_DELIVERY ) {
        			MessageUtils.addActionWarning(request,"dispokarte.errors.toolongNoteDelivery", Long.toString(ShiptoEntity.DBSIZE_NOTE_DELIVERY));
        		}
        		if ( StringUtils.notEmpty(shipTo.getNoteDispatching()) && shipTo.getNoteDispatching().length() > ShiptoEntity.DBSIZE_NOTE_DISPATCHING ) {
        			MessageUtils.addActionWarning(request,"dispokarte.errors.toolongNoteDispatching", Long.toString(ShiptoEntity.DBSIZE_NOTE_DISPATCHING));
        		}
        		if ( StringUtils.notEmpty(shipTo.getNoteOrderMgmt()) && shipTo.getNoteOrderMgmt().length() > ShiptoEntity.DBSIZE_NOTE_ORDER_MGMT ) {
        			MessageUtils.addActionWarning(request,"dispokarte.errors.toolongNoteOrderMgmt", Long.toString(ShiptoEntity.DBSIZE_NOTE_ORDER_MGMT));
        		}
        		if ( StringUtils.notEmpty(shipTo.getNoteRailwayDisp()) && shipTo.getNoteRailwayDisp().length() > ShiptoEntity.DBSIZE_NOTE_RAILWAY_DISP) {
        			MessageUtils.addActionWarning(request,"dispokarte.errors.toolongNoteRailwayDisp", Long.toString(ShiptoEntity.DBSIZE_NOTE_RAILWAY_DISP));
        		}
        		if ( StringUtils.notEmpty(shipTo.getNoteTruckDriver()) && shipTo.getNoteTruckDriver().length() > ShiptoEntity.DBSIZE_NOTE_TRUCK_DRIVER ) {
        			MessageUtils.addActionWarning(request,"dispokarte.errors.toolongNoteTruckDriver", Long.toString(ShiptoEntity.DBSIZE_NOTE_TRUCK_DRIVER));
        		}
        		if ( StringUtils.notEmpty(shipTo.getOrderConfEmailAddresses()) && shipTo.getOrderConfEmailAddresses().length() > ShiptoEntity.DBSIZE_ORDER_CONF_EMAIL_ADDRESSES ) {
        			MessageUtils.addActionWarning(request,"dispokarte.errors.toolongOrderConfEmail", Long.toString(ShiptoEntity.DBSIZE_ORDER_CONF_EMAIL_ADDRESSES));
        		}
        		if ( StringUtils.notEmpty(shipTo.getOrderConfFaxNumber()) && shipTo.getOrderConfFaxNumber().length() > ShiptoEntity.DBSIZE_ORDER_CONF_FAX_NUMBER ) {
        			MessageUtils.addActionWarning(request,"dispokarte.errors.toolongOrderFaxNumber", Long.toString(ShiptoEntity.DBSIZE_ORDER_CONF_FAX_NUMBER));
        		}
        		if ( StringUtils.notEmpty(shipTo.getOrderConfSmsNumber()) && shipTo.getOrderConfSmsNumber().length() > ShiptoEntity.DBSIZE_ORDER_CONF_SMS_NUMBER ) {
        			MessageUtils.addActionWarning(request,"dispokarte.errors.toolongOrderSmsNumber", Long.toString(ShiptoEntity.DBSIZE_ORDER_CONF_SMS_NUMBER));
        		}
        		
        		if (!LogonUtil.validSmsNumber(shipTo.getOrderConfSmsNumber())) {
       				MessageUtils.addActionWarning(request,"dispokarte.errors.invalidSMSnumber");
        		}
                if (!LogonUtil.validSmsNumber(shipTo.getOrderConfFaxNumber())) {
                    MessageUtils.addActionWarning(request,"dispokarte.errors.invalidFaxNumber");
                }
        		if (!LogonUtil.validEmail(shipTo.getOrderConfEmailAddresses())) {
        		    MessageUtils.addActionWarning(request,"dispokarte.errors.invalidEmailAddress");
        		}
    		}
    		catch (ParseException e) {
    			MessageUtils.addActionWarning(request,"dispokarte.errors.openbadformat");
			}
    	}
	}

	@AdnTrace
	private void validateLeadSettings(HttpServletRequest request, SalesAreaBean salesAreaBean) {
			Long applyNdInMinutes = null;
			Long applySdInMinutes = null;

			String applyNdS = salesAreaBean.getApplyNdLeadtimeChkUntilMi();
			String applySdS = salesAreaBean.getApplySdLeadtimeChkFromMi();

		/**
		 * Basic parsing check
		 */
		try {
				Date applyNdLeadtimeChkUntilMi = DateTimeUtils.parseHoursMinutes(applyNdS);
				applyNdInMinutes = DateTimeUtils.getMinAfterMidnight(applyNdLeadtimeChkUntilMi);
				
				Date applySdLeadtimeChkFromMi = DateTimeUtils.parseHoursMinutes(applySdS);
				applySdInMinutes = DateTimeUtils.getMinAfterMidnight(applySdLeadtimeChkFromMi);
			}
			catch (ParseException e) {
				StringBuffer error = new StringBuffer();
				error.append("ApplyNdLeadtimeChkUntilMi or ApplySdLeadtimeChkFromMi has wrong format (");
				error.append(applyNdS);
				error.append("; ");
				error.append(applySdS);
				error.append(")");
				throw new LogonException(error.toString(), Reason.RES_ERROR_IMPL);
			}

		/**
		 * sd and nd lead time sanity check
		 */
		DeliveryDateService deliveryDateService = getDeliveryDateService();
			Boolean sdltAndNdltActive = salesAreaBean.getShiptoSarea().getSdltAndNdltActive();
			Collection<ValidationErrorReason> reasons = new ArrayList<ValidationErrorReason>();
			if (Boolean.TRUE.equals(sdltAndNdltActive)) { // call validation only if flag active
				ValidationErrorReason dateErrorReason =
						deliveryDateService.validateShiptoSareaDateConstants(salesAreaBean.getShiptoSarea(),
								applyNdInMinutes, applySdInMinutes);
				if (dateErrorReason != null) {
					reasons.add(dateErrorReason);
				}
				else {
					salesAreaBean.getShiptoSarea().setApplyNdLeadtimeChkUntilMi(applyNdInMinutes);
					salesAreaBean.getShiptoSarea().setApplySdLeadtimeChkFromMi(applySdInMinutes);
				}
			}

		/**
		 * Distance based validation fields sanity check
		 */
		ShiptoSareaEntity shiptoSarea = salesAreaBean.getShiptoSarea();
		DistanceValidationEditGuiBean distanceValidationGuiBean = salesAreaBean.getDistanceValidationGuiBeanShipto();
		boolean areTheDistanceValuesValid = distanceValidationGuiBean.transmitValuesToShiptoSareaEntity(shiptoSarea, request);
		Boolean dbltActiveFlag = distanceValidationGuiBean.getDbltActiveFlag();
			if (areTheDistanceValuesValid && Boolean.TRUE.equals(dbltActiveFlag)) { // call validation only if flag active
				SareaEntity sarea = shiptoSarea.getSarea();
				DeliveryDistanceValidationUtils deliveryDistanceValidationUtils = new DeliveryDistanceValidationUtils();
				List<ValidationErrorReason> distanceErrorReasons =
						deliveryDistanceValidationUtils.sanityCheckShipToEdit(shiptoSarea, false, sarea);
				reasons.addAll(distanceErrorReasons);
			}


			processValidationErrorReasonsAndTrace(request, reasons, CallerContext.CALL_CENTER, false);

	}


	private void validateOnEnterTabSarea(DispokarteForm helpForm, HttpServletRequest request) {
		// [[
		Object[][] params = {{"helpForm", helpForm},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validateOnEnterTabSarea", params);
		}
		// ]]
		try {
			
			final DeliverySettingProductMap productMap = helpForm.getCurrSalesAreaBean().getSalesAreaMap().getMaterialRestrictionDeliverySettings();
			final DeliverySettingProductMap serviceMap = helpForm.getCurrSalesAreaBean().getSalesAreaMap().getServiceRestrictionDeliverySettings();
			Map<Object, List<ShiptoSalesAreaError>> invalidMap = new HashMap<Object, List<ShiptoSalesAreaError>>();
			
			invalidMap.putAll(productMap.getInvalidMap());
			invalidMap.putAll(serviceMap.getInvalidMap());
            
			for (Map.Entry<Object, List<ShiptoSalesAreaError>> entry : invalidMap.entrySet()) {
				List <ShiptoSalesAreaError> errorList = entry.getValue();
				if ( entry.getKey() instanceof DeliverySettingDTO ) {
					/**
					 * In case of restriction delivery settings show detailed warning which restriction is invalid, the reason will also be shown what the problem is with that!
					 * Later it could be solved much better: show the invalid value in the row of the invalid delivery setting.
					 */
					DeliverySettingDTO setting = (DeliverySettingDTO) entry.getKey();
					if ( setting.getShiptoSareaRestr() != null ) {
						if ( setting.getSapContractItem() != null ) {								
							MessageUtils.addActionWarning
							(request, "dispokarte.errors.invalid.parameters.sarea.material.contract", 
									setting.getSarea().getSareaDisplayLongname(),  (setting.getSapMaterial() != null ? setting.getSapMaterial().getSapMaterialNo() : null), 
									setting.getSapContractItem().getSapContract().getSapContractNo() + "(" + setting.getSapContractItem().getContractItemNo().toString() + ")");
						}
						else {
							MessageUtils.addActionWarning
							(request, "dispokarte.errors.invalid.parameters.sarea.material", 
									setting.getSarea().getSareaDisplayLongname(), setting.getSapMaterial().getSapMaterialNo());
							
						}
					}
				}
				for(ShiptoSalesAreaError e : errorList){				
					MessageUtils.addActionWarning(request, e.getCorrespondingCustorderError().getMessage(CallerContext.DISPOKARTE));
				}			
			}		
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	/**
	 * See twiki page
	 * https://aww.adnovum.ch/twiki/bin/view/CrhPegasus/RailWay#1_1_Priorities_of_Types_in_Shipt
	 * 
	 * persist/merge ShiptoWagonTypeEntities and ShiptoTruckWagonTypeEntities 
	 * @param DispokarteForm
	 */
	
	public void handleTruckWagonTypes(DispokarteForm helpForm) {
		// [[
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handleTruckWagonTypes", params);
		}
		// ]]
		try {
			List<ShiptoWagonTypeEntity> shiptoWagonToPersistList = new ArrayList<ShiptoWagonTypeEntity>();
			List<ShiptoWagonTypeEntity> shiptoWagonToMergeList = new ArrayList<ShiptoWagonTypeEntity>();
			List<ShiptoWagonTypeEntity> shiptoWagonToDeleteList = new ArrayList<ShiptoWagonTypeEntity>();
			List<ShiptoWagonTypeEntity> shiptoWagonNoChangeList = new ArrayList<ShiptoWagonTypeEntity>();
			
			List <ShiptoTruckTypeEntity> shiptoTruckToPersistList = new ArrayList<ShiptoTruckTypeEntity>();
			List <ShiptoTruckTypeEntity> shiptoTruckToMergeList = new ArrayList<ShiptoTruckTypeEntity>();
			List <ShiptoTruckTypeEntity> shiptoTruckToDeleteList = new ArrayList<ShiptoTruckTypeEntity>();
			List<ShiptoTruckTypeEntity> shiptoTruckNoChangeList = new ArrayList<ShiptoTruckTypeEntity>();
			
			for ( TruckTypeWrapper tw:  helpForm.getTruckTypeWrapperList() ) { 

				
				//for truck types the db contains the forbidden and the prioritized types
				//for truck types there IS a don't care situation in this case the priority is -1
				
				//it must be removed, as far it neither has priority information, nor is forbidden (0) from that point on
				if ( helpForm.getTruckTypesInDB().contains(tw.getTypeCd()) && tw.getPriority() < 0) {
					shiptoTruckToDeleteList.add(tw.getShiptoTruckTypeEntity());
				}
				//save truck type if it is forbidden (0) or has a priority set
				else if ( tw.getShiptoTruckTypeEntity() == null && tw.getPriority() >= 0) {
					ShiptoTruckTypeEntity stEntity = new ShiptoTruckTypeEntity();
					stEntity.setShipto(helpForm.getShipTo());
					TruckTypeBsEntity ttBs = ((TruckTypeTxEntity) helpForm.getShiptoMap().getGlobalTransportData().getTruckTypeTxMap().get(tw.getTypeCd())).getTruckTypeTxPK().getTruckTypeBs();
					stEntity.setTruckType(ttBs);
					stEntity.setEvaluationPriority(tw.getPriority());
					shiptoTruckToPersistList.add(stEntity);
					helpForm.getTruckTypesInDB().add(stEntity.getTruckType().getTruckTypeCd());
					continue;
				}
				else if ( tw.getShiptoTruckTypeEntity() != null ) {
					long originalPrio = ((ShiptoTruckTypeEntity) tw.getShiptoTruckTypeEntity()).getEvaluationPriority().longValue();  
					if ( (tw.getPriority() == originalPrio) && tw.getPriority() != -1  ) {
						shiptoTruckNoChangeList.add(tw.getShiptoTruckTypeEntity());
						continue;
					}
					(( ShiptoTruckTypeEntity ) tw.getShiptoTruckTypeEntity()).setEvaluationPriority(tw.getPriority());
					shiptoTruckToMergeList.add(tw.getShiptoTruckTypeEntity());
					continue;
				}
			}
			
			//for wagon types the db contains ONLY prioritized wagon types. If it is not in the DB, it counts as forbidden!
			//for wagon types there ISN'T don't care situation the type either has a priority, or it must be removed from the DB (counts as forbidden)
			for ( WagonTypeWrapper ww : helpForm.getWagonTypeWrapperList() ) { 
				
				//it must be removed (made forbidden), as far it does not have priority information anymore (0 counts as forbidden on the UI!)				
				if ( helpForm.getWagonTypesInDB().contains(ww.getTypeCd()) && ww.getPriority() < 1) {
					shiptoWagonToDeleteList.add(ww.getShiptoWagonTypeEntity());
				//save wagon only if it has a priority set					
				} else	if ( ww.getShiptoWagonTypeEntity() == null && ww.getPriority() > 0) {
					ShiptoWagonTypeEntity swEntity = new ShiptoWagonTypeEntity();
					swEntity.setShipto(helpForm.getShipTo());
					WagonTypeBsEntity wtBs = ((WagonTypeTxEntity) helpForm.getShiptoMap().getGlobalTransportData().getWagonTypeTxMap().get(ww.getTypeCd())).getWagonTypeTxPK().getWagonTypeBs();
					swEntity.setWagonType(wtBs);
					swEntity.setEvaluationPriority(ww.getPriority());
					shiptoWagonToPersistList.add(swEntity);
					helpForm.getWagonTypesInDB().add(swEntity.getWagonType().getWagonTypeCd());
					continue;
				}
				else if ( ww.getShiptoWagonTypeEntity() != null ) {
					long originalPrio = ((ShiptoWagonTypeEntity) ww.getShiptoWagonTypeEntity()).getEvaluationPriority().longValue();  
					if ( (ww.getPriority() == originalPrio) && ww.getPriority() != -1  ) {
						shiptoWagonNoChangeList.add(ww.getShiptoWagonTypeEntity());
						continue;
					}
					(( ShiptoWagonTypeEntity ) ww.getShiptoWagonTypeEntity()).setEvaluationPriority(ww.getPriority());
					shiptoWagonToMergeList.add(ww.getShiptoWagonTypeEntity());
					continue;
				}
			}
			
			getServiceLocator().getDispokarteService().handleWagonTypes( shiptoWagonToPersistList, shiptoWagonToMergeList, shiptoWagonToDeleteList);
			getServiceLocator().getDispokarteService().handleTruckTypes(shiptoTruckToPersistList, shiptoTruckToMergeList, shiptoTruckToDeleteList);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	public void loadContractItemData(HttpServletRequest request, DispokarteForm form) {
		// [[
		Object[][] params = {{"request", request},
							{"form", form}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadContractItemData", params);
		}
		// ]]
		try {
			ContractItemDataBean contractItemData = new ContractItemDataBean();
			
			// FIXME (romek Sept 23, 2011): error handling
			String sapContractItemIdStr = request.getParameter("sapContractItemId");
			Long sapContractItemId = Long.valueOf(sapContractItemIdStr);
			
			DispokarteService dispoService = getDispokarteService();
			SapContractItemEntity sapContractItem = dispoService.fetchSapContractItem(sapContractItemId);
			Locale locale = getLocale(request);

			QuantityRestrictionService qrService = getServiceLocator().getQuantityRestrictionService();
			
			SapContractItemEntity apGroupLeader = sapContractItem.getApGroupLeader(); 
			if(apGroupLeader != null) {
				contractItemData.setDataAvailable(true);
				contractItemData.setHasGroupLeader(true);
				
				QuantityRestrictionCalculation calculation = qrService.calculateOpenQuantityForQuantityRestrictedContractItem(sapContractItem);
				
				if (calculation != null) {
					String contractItemNo = String.valueOf(sapContractItem.getContractItemNo());
					contractItemData.setContractItemNo(contractItemNo);
					
					SapMaterialEntity sapMaterial = sapContractItem.getSapMaterial();
					Long sapMaterialId = sapMaterial.getSapMaterialId();
					
					SapMaterialDescriptionEntity materialTx = dispoService.fetchSapMaterialDescription(sapMaterialId, locale.getLanguage());
					
					String materialTxt = materialTx.getTxt();
					contractItemData.setMaterialTxt(materialTxt);
					
					UomBsEntity targetQuantityUom = sapContractItem.getTargetQuantityUom();
					String uomCd = targetQuantityUom.getUomCd();				
					UomTxEntity uomTx = dispoService.fetchUomTxEntity(uomCd, locale);
					contractItemData.setUomTxt(uomTx.getTxt());
					
					BigDecimal sumDeliveries = calculation.getSumDeliveries();
					sumDeliveries = sumDeliveries.setScale(2, BigDecimal.ROUND_HALF_UP);
					contractItemData.setSumDeliveries(sumDeliveries.toString());
					
					BigDecimal sumNonDeliveries = calculation.getSumNonDeliveries();
					sumNonDeliveries = sumNonDeliveries.setScale(2, BigDecimal.ROUND_HALF_UP);
					contractItemData.setSumNonDeliveries(sumNonDeliveries.toString());
					
					String deliveries = StringUtils.join(calculation.getDeliveries(), ", ");
					contractItemData.setDeliveries(deliveries);
					
					String nonDeliveries = StringUtils.join(calculation.getNonDeliveries(), ", ");
					contractItemData.setNonDeliveries(nonDeliveries);
					
					String conversionFailures = StringUtils.join(calculation.getConversionFailures(), ", ");
					contractItemData.setConversionFailures(conversionFailures);					
				}
				
				UomBsEntity targetQuantityUom = apGroupLeader.getTargetQuantityUom();
				String uomCd = targetQuantityUom.getUomCd();				
				UomTxEntity uomTx = dispoService.fetchUomTxEntity(uomCd, locale);
				contractItemData.setGroupLeaderUomTxt(uomTx.getTxt());

				BigDecimal targetQuantity = apGroupLeader.getTargetQuantity();
				targetQuantity = targetQuantity.setScale(2, BigDecimal.ROUND_HALF_UP);
				contractItemData.setTargetQuantity(targetQuantity.toString());
				
				BigDecimal totalSentQuantity = apGroupLeader.getTotalSentQuantity();
				totalSentQuantity = totalSentQuantity.setScale(2, BigDecimal.ROUND_HALF_UP);
				contractItemData.setTotalSentQuantity(totalSentQuantity.toString());
				
				BigDecimal sumItemQuantityOrdered = BigDecimal.ZERO;
				BigDecimal sumItemQuantityLoaded = BigDecimal.ZERO;
				BigDecimal sumItemQuantityDelivered = BigDecimal.ZERO;
				
				QuantityRestrictionCalculation apGroupItemCalc = qrService.calculateOpenQuantityForContractItem(apGroupLeader, null, true);
				if(apGroupItemCalc != null) {
					sumItemQuantityOrdered = sumItemQuantityOrdered.add(apGroupItemCalc.getSumNonDeliveries());
					sumItemQuantityLoaded = sumItemQuantityLoaded.add(apGroupItemCalc.getSumLoaded());
					sumItemQuantityDelivered = sumItemQuantityDelivered.add(apGroupItemCalc.getSumDeliveries());
				}

				sumItemQuantityOrdered = sumItemQuantityOrdered.setScale(2, BigDecimal.ROUND_HALF_UP);
				contractItemData.setSumItemQuantityOrdered(sumItemQuantityOrdered.toString());

				sumItemQuantityLoaded = sumItemQuantityLoaded.setScale(2, BigDecimal.ROUND_HALF_UP);
				contractItemData.setSumItemQuantityLoaded(sumItemQuantityLoaded.toString());

				sumItemQuantityDelivered = sumItemQuantityDelivered.setScale(2, BigDecimal.ROUND_HALF_UP);
				contractItemData.setSumItemQuantityDelivered(sumItemQuantityDelivered.toString());
				
				SapContractEntity sapContract = sapContractItem.getSapContract();
				if(sapContract != null && sapContract.getItemGroupingType() != null && sapContract.getItemGroupingType() != 1) {
					contractItemData.setContractQuantityRestricted(true);
					
					List<ContractItemOrderItemListEntity> contractItemOrderItemList = dispoService.fetchContractItemOrderItemList(sapContractItem.getSapContractItemId());
					List<ContractItemOrderItemBean> contractItemOrderItems = new ArrayList<ContractItemOrderItemBean>();
					if(contractItemOrderItemList != null) {
						for (ContractItemOrderItemListEntity cioi : contractItemOrderItemList) {
							String sentOrderItemNo = ObjectUtils.toString(cioi.getSentOrderItemNo());
							String orderItemNo = ObjectUtils.toString(cioi.getOrderItemNo());
							String orderNumber = ObjectUtils.toString(cioi.getOrderNumber());

							BigDecimal deliveredQuantity = cioi.getDeliveredQuantity();
							deliveredQuantity = deliveredQuantity.setScale(2, BigDecimal.ROUND_HALF_UP);
							String deliveredQuantityStr = deliveredQuantity.toString();
							
							String key = CONTRACT_ITEM_ORDER_ITEM_DELETED_FLAG + ((cioi.getDeletedFlag() != null && cioi.getDeletedFlag()) ? ".yes" : ".no");
							String deletedFlag = Message.lookup(locale, key);
							
							ContractItemOrderItemBean contractItemOrderItem = new ContractItemOrderItemBean(deliveredQuantityStr, sentOrderItemNo, deletedFlag, orderItemNo, orderNumber);
							contractItemOrderItems.add(contractItemOrderItem);							
						}
					}
					contractItemData.setContractItemOrderItemList(contractItemOrderItems);
				}
				
			} else {
				QuantityRestrictionCalculation calculation = qrService.calculateOpenQuantityForContractItem(sapContractItem, null);
				
				if (calculation != null) {
					contractItemData.setDataAvailable(true);
					
					String contractItemNo = String.valueOf(sapContractItem.getContractItemNo());
					contractItemData.setContractItemNo(contractItemNo);
					
					SapMaterialEntity sapMaterial = sapContractItem.getSapMaterial();
					Long sapMaterialId = sapMaterial.getSapMaterialId();
					
					SapMaterialDescriptionEntity materialTx = dispoService.fetchSapMaterialDescription(sapMaterialId, locale.getLanguage());
					
					String materialTxt = materialTx.getTxt();
					contractItemData.setMaterialTxt(materialTxt);
					
					UomBsEntity targetQuantityUom = sapContractItem.getTargetQuantityUom();
					String uomCd = targetQuantityUom.getUomCd();
					
					UomTxEntity uomTx = dispoService.fetchUomTxEntity(uomCd, locale);
					contractItemData.setUomTxt(uomTx.getTxt());
					
					BigDecimal sumDeliveries = calculation.getSumDeliveries();
					sumDeliveries = sumDeliveries.setScale(2, BigDecimal.ROUND_HALF_UP);
					contractItemData.setSumDeliveries(sumDeliveries.toString());
					
					BigDecimal sumNonDeliveries = calculation.getSumNonDeliveries();
					sumNonDeliveries = sumNonDeliveries.setScale(2, BigDecimal.ROUND_HALF_UP);
					contractItemData.setSumNonDeliveries(sumNonDeliveries.toString());
					
					String deliveries = StringUtils.join(calculation.getDeliveries(), ", ");
					contractItemData.setDeliveries(deliveries);
					
					String nonDeliveries = StringUtils.join(calculation.getNonDeliveries(), ", ");
					contractItemData.setNonDeliveries(nonDeliveries);
					
					String conversionFailures = StringUtils.join(calculation.getConversionFailures(), ", ");
					contractItemData.setConversionFailures(conversionFailures);
				}				
			}
			
			form.setContractItemData(contractItemData);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	private void loadBlockTypeForSoldToOnCurrentSalesArea(DispokarteForm helpForm) {
		// [[
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadCustomerBlockType", params);
		}
		// ]]
		try {
			final SalesAreaBean currSalesAreaBean = helpForm.getCurrSalesAreaBean();
			final SapBpEntity soldTo = helpForm.getSoldTo();
			
			boolean customerIsBlockedCentrally =  
					BooleanUtils.toBoolean(soldTo.getCentralDeliveryBlock()) ||
					BooleanUtils.toBoolean(soldTo.getCentralOrderBlock()) ||
					BooleanUtils.toBoolean(soldTo.getCentralPostingBlock());
				
			helpForm.setCustomerCentralBlocked(customerIsBlockedCentrally);
			if (!customerIsBlockedCentrally) {
				DeliverySettingService deliverySettingService = getServiceLocator().getDeliverySettingService();
				SapBpSareaEntity sapBpSarea = deliverySettingService.fetchSapBpSalesArea(soldTo.getSapBpId(),currSalesAreaBean.getShiptoSarea().getSarea().getSareaId());
			
				if (sapBpSarea != null) {
					helpForm.setSoldToSapBpSarea(sapBpSarea);
					boolean customerIsBlockedOnSalesArea = 
							BooleanUtils.toBoolean(sapBpSarea.getCustomerDeliveryBlock()) ||
							BooleanUtils.toBoolean(sapBpSarea.getCustomerOrderBlock()) ||
							BooleanUtils.toBoolean(sapBpSarea.getCustomerPostingBlock());
					helpForm.setCustomerSalesAreaBlocked(customerIsBlockedOnSalesArea);
				}
			}
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
}
