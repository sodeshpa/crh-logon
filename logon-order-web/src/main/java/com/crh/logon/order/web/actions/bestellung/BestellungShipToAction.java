/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.bestellung;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJBException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.bestellung.BestellungForm;
import com.crh.logon.order.web.forms.truck.ChartForm;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.ordercreation.DeliverySettingShiptoMap;
import com.crh.logon.business.ordercreation.InvalidMapSupport;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.constants.CallerContext;
import com.crh.logon.constants.Constant;
import com.crh.logon.constants.CustorderError;
import com.crh.logon.constants.ShiptoSalesAreaError;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.TrspZonePolygonEntity;
import com.crh.logon.persistence.entities.refdata.DailyOrderStatusTxEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageTxEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageTxPK;
import com.crh.logon.persistence.entities.refdata.SapCountryTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.ShiptoSoldtoLnfDataEntity;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

import flexjson.DateTransformer;
import flexjson.HTMLEncoder;
import flexjson.JSONSerializer;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_bestellung_BestellungForm"
 *  path="/bestellung/BestellungShipTo" scope="session"
 *  validate="true" input="BestellungShipToTile.def"
 *
 * @struts.action-forward name="retry" path="BestellungShipToTile.def"
 * @struts.action-forward name="select" path="/bestellung/BestellungEntry.do"
 */
public class BestellungShipToAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(BestellungShipToAction.class);

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws Exception
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
			Authorization.authorize(Authorization.ORDER);
			
			BestellungForm helpForm = (BestellungForm) form;
		    DispokarteService dispoService = getServiceLocator().getDispokarteService();
		    RefDataGenericsService refData = getServiceLocator().getRefDataGenericsService();
		    Locale locale = request.getLocale();
		    
		    setSelectedTab("ccbestellung", request);
		    String forwardName = "select";
			
			helpForm.setLocale(locale);
			
		    ShiptoEntity shipto = null;
		    String soldtoSapId = helpForm.getSoldtoSapId();
		    String shiptoSapId = helpForm.getShiptoSapId();
		    
			if (StringUtils.isEmpty(soldtoSapId) || StringUtils.isEmpty(shiptoSapId)) {
		    	return result = mapping.findForward(forwardName);
		    }
		    
		    shipto = dispoService.fetchAndInitializeShiptoBySapId(shiptoSapId);
			if (shipto == null) {
				shipto = dispoService.createShipto(shiptoSapId, soldtoSapId);
		    }
			
			helpForm.setShipTo(shipto, createTimeZoneFor(shipto));
			SapBpEntity shiptoSapBp = shipto.getShiptoSapBp();
			
			SapBpEntity soldto = dispoService.fetchSoldtoSapBpBySapId(soldtoSapId);
			helpForm.setSoldTo(soldto);
			
			List<ShiptoSareaEntity> shiptoSalesAreas = dispoService.updateShiptoSalesAreas(shipto, soldto, false);
			
			DeliverySettingShiptoMap shiptoMap = getServiceLocator().getDeliverySettingService().initDeliverySettingShiptoMapForCallCenter(
					soldto, locale, shiptoSalesAreas, helpForm.getAllowFutureContracts());
			shiptoMap.validateSalesAreas();
			
			displaySalesAreaBlockWarning(request, shiptoMap);
			
    		helpForm.setShiptoMap(shiptoMap);
	    		
   			final Map<Object, List<ShiptoSalesAreaError>> invalidMap = shiptoMap.getInvalidMap();
			helpForm.setInvalidMap(invalidMap);
			
			// -----------------
    		// Initialize values which are global, 
    		// i.e. will apply to all edited order entries
    		// -----------------
		    helpForm.setShipTo(shipto, createTimeZoneFor(shipto));
			helpForm.setDeliveryTypeList(refData.findTxEntityList(DeliveryTypeTxEntity.class, false, locale,
					"deliveryTypeTxPK.deliveryTypeBs.deliveryTypeCd", true));
			helpForm.setDailyOrderStatusList(refData.findTxEntityList(DailyOrderStatusTxEntity.class, false, locale,
					"dailyOrderStatusTxPK.dailyOrderStatusBs.dailyOrderStatusCd", true));
			helpForm.setPoolTruckList(getPoolAdminService().fetchPoolTxEntitiesByDeliveryTypeNotAuthorized(locale,
					Constant.DELIVERY_TYPE_ROAD_DIRECT));
			helpForm.setPoolRailList(getPoolAdminService().fetchPoolTxEntitiesByDeliveryTypeNotAuthorized(locale,
					Constant.DELIVERY_TYPE_RAIL_DIRECT));
		
		    // --- Polygons (only for LNF) ---
			if (Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED == shipto.getShiptoType().getShiptoTypeCd()) {
			    List<TrspZonePolygonEntity> polygonList = getZoneMapService().loadTrspZonePolygons();
				for (TrspZonePolygonEntity polygon : polygonList) {
					//set all null zIndex values to -1 (form validation will reset them back)
					if (polygon.getZIndex() == null) {
						polygon.setZIndex(new Long(Constant.INVALID_OBJECT_ID));
					}
				}
				helpForm.setPolygonList(polygonList);
				List<ShiptoSoldtoLnfDataEntity> lnfAddresses = getBestellungService().fetchLatestLnfAddresses(
						shiptoSapBp.getSapBpId(), soldto.getSapBpId());
				helpForm.setLnfList(lnfAddresses);
		    }
		    
		    // ------------------
		    // Setup of the chart
		    // ------------------
			ChartForm chartForm = (ChartForm)request.getSession().getAttribute(ChartForm.FORM_NAME);
			ChartAction.initForm(chartForm, true, request, this, locale);
			
    		// -----------------------
    		// JSON for ExtJS widgets
    		// Shared by all edited new orders (i.e. independent from the selected product) 
    		// -----------------------
			HashMap<String, String> jsonData = new HashMap<String, String>();

			loadSiloMaterialDescriptions(helpForm, locale);
			
			loadMaterialPackageTxMap(helpForm, locale);
			
			loadSapCountryTxEntity(helpForm, request.getLocale(), soldto.getCountry().getSapCountryCd());
						
		    if (Constant.SHIPTO_TYPE_LOCATION_NOT_FIXED == shipto.getShiptoType().getShiptoTypeCd()) {
		    	jsonData.put("zonePolygonList", getJsonSerializer().serialize(helpForm.getPolygonList()));
		    	jsonData.put("zoneVertexList", getJsonSerializer().serialize(getZoneMapService().loadTrspZoneVertexs()));
		    }
		    jsonData.put("contactPersonList", getJsonSerializer().exclude("*.shipto").serialize(shipto.getShiptoPersonList()));
		    jsonData.put("siloList", new JSONSerializer()
		    		.include("shiptoSiloId")
		    		.include("siloName")
		    		.exclude("*")
		    		.transform(new HTMLEncoder(), "siloName")
		    		.serialize(helpForm.getShiptoActiveSilos()));
		    if ( helpForm.getLnfList() != null ) {
		    	String dateFormat = "dd/MM/yyyy HH:mm";
		    	jsonData.put("lnfList", new JSONSerializer().transform(new DateTransformer(dateFormat), "actualDeliveryDat").serialize(helpForm.getLnfList()));
		    }
    		helpForm.setJson(jsonData);
	    	
			return result = mapping.findForward(forwardName);
		}
		catch (EJBException ex) { 
			final Reason reason = ExceptionUtil.getLogonExceptionReason(ex, null);
			if (reason != null) {
				MessageUtils.addActionWarning(request, reason.getReason());
				return result = mapping.findForward("retry");
			}
			else {
				throw ex;
			}
		}
		catch (LogonException e) {
			if (Reason.RES_STRUTS_VALIDATION.equals(e.getReason())) {
				return result = mapping.findForward("retry");
			}
			else throw e;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private void loadMaterialPackageTxMap(BestellungForm helpForm, Locale locale) {
		Map<String, MaterialPackageTxEntity> materialPackageTxMap = helpForm.getMaterialPackageTxMap();
		if (materialPackageTxMap == null || materialPackageTxMap.isEmpty()) {
			List<MaterialPackageTxEntity> materialPackageLabels = getRefDataGenericsService().findTxEntityList(
					MaterialPackageTxEntity.class, false, locale, MaterialPackageTxEntity.ATTR_TXT,
					true);
			Map<String, MaterialPackageTxEntity> materialPackageLabelsByMaterialPackageCode = new HashMap<String, MaterialPackageTxEntity>();
			for (MaterialPackageTxEntity materialPackageLabel : materialPackageLabels) {
				MaterialPackageTxPK materialPackageKey = materialPackageLabel.getMaterialPackageTxPK();
				MaterialPackageBsEntity materialPackage = materialPackageKey.getMaterialPackageBs();
				String materialPackageCode = materialPackage.getMaterialPackageCd();
				materialPackageLabelsByMaterialPackageCode.put(materialPackageCode,
						materialPackageLabel);
			}
			helpForm.setMaterialPackageTxMap(materialPackageLabelsByMaterialPackageCode);
		}
	}
	
	/**
	 * Display Sales Area related Warning messages on Order Taking screen
	 * 
	 * @param request The {@link HttpServletRequest} to put the content of warning message.
	 * @param shiptoMap The DeliverySettinShiptoMap holds the related info about the warning message.
	 * 
	 */
	@AdnTrace
	public void displaySalesAreaBlockWarning(final HttpServletRequest request, final DeliverySettingShiptoMap shiptoMap){
		Map<Object, List<ShiptoSalesAreaError>> shiptoErrors = shiptoMap.getInvalidMap();
		boolean isContainsBusinessPartnerBlock = InvalidMapSupport.containsError(shiptoErrors,
				ShiptoSareaEntity.class, ShiptoSalesAreaError.BLOCK_BP);
		boolean isContainsAllSalesAreaBlock = InvalidMapSupport.containsError(shiptoErrors,
				ShiptoSareaEntity.class, ShiptoSalesAreaError.BLOCK_SOME_SALES_AREA);
		boolean isContainsSomeSalesAreaBlock = InvalidMapSupport.containsError(shiptoErrors,
				ShiptoSareaEntity.class, ShiptoSalesAreaError.BLOCK_ALL_SALES_AREA);

		if (isContainsBusinessPartnerBlock) {
			CustorderError blockBpError = ShiptoSalesAreaError.BLOCK_BP.getCorrespondingCustorderError();
			MessageUtils.addActionWarning(request,
					blockBpError.getMessage(CallerContext.CALL_CENTER));
		}
		else if (isContainsAllSalesAreaBlock || isContainsSomeSalesAreaBlock) {
			CustorderError blockSalesAreaError = ShiptoSalesAreaError.BLOCK_SOME_SALES_AREA.getCorrespondingCustorderError();
			MessageUtils.addActionWarning(request,
					blockSalesAreaError.getMessage(CallerContext.CALL_CENTER));
		}
	}
	
	/**
	 * Use the users locale as the source of language so the country given back will be something that the user can read.
	 * This is actually used for the google coordinates lookup, but to google maps api it scarecly matter in what language you give the country
	 */
	private void loadSapCountryTxEntity(BestellungForm helpForm, Locale locale, String sapBpCountryCd) {
		SapCountryTxEntity country = getRefDataGenericsService().findTxEntity(SapCountryTxEntity.class, locale, sapBpCountryCd);
		if (country != null) {
			helpForm.setCountryForShipto(country.getTxt());				
		} else {
			// if we don't find the country tx for the locale the fall back to the 2 char code. it's better than nothing.
			helpForm.setCountryForShipto(sapBpCountryCd);
		}

	}
}
