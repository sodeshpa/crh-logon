/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.actions.AdminActionSupport;
import com.crh.logon.admin.web.beans.StorageLocationBean;
import com.crh.logon.admin.web.forms.AdminRFIDPopupForm;
import com.crh.logon.admin.web.forms.masterdata.LoadingPointBean;
import com.crh.logon.admin.web.forms.masterdata.ShippingPointForm;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.interfaces.ShippingPointService;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.MatShippingpointEntity;
import com.crh.logon.persistence.entities.common.RailwayStationEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.CurrencyBsEntity;
import com.crh.logon.persistence.entities.refdata.DelivNotePrintTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DelivNotePrintTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.PackageTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.PackageTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.PrinterBsEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SapShippingCondTxEntity;
import com.crh.logon.persistence.entities.refdata.ShipppointTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.TimezoneBsEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.WeighSystemLpBsEntity;
import com.crh.logon.persistence.entities.refdata.WeighSystemLpTxEntity;
import com.crh.logon.persistence.entities.refdata.WeighSystemSpBsEntity;
import com.crh.logon.persistence.entities.refdata.WeighSystemSpTxEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.SapMaterialPlantDescEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeParser;
import com.crh.logon.web.forms.RFIDPopupForm;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_ShippingPointForm" path="/ShippingPoint" scope="session"
 *  validate="true" input="ShippingPoint.def"
 *
 * @struts.action-forward name="shipPointEntry" path="/ShippingPointEntry.do"
 * @struts.action-forward name="shipPoint" path="ShippingPoint.def"
 * @struts.action-forward name="ldsServer" path="ShippingPointLdsServer.def"
 * @struts.action-forward name="loadPoint" path="LaodingPoint.def"
 * @struts.action-forward name="loadRfids" path="LaodingPointRfids.def"
 * @struts.action-forward name="loadMaterials" path="LaodingPointMaterials.def"
 * @struts.action-forward name="input" path="ShippingPointTile.def"
 * @struts.action-forward name="station" path="RailwayStation.def"
 */
public class ShippingPointAction extends AdminActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShippingPointAction.class);
	private static final String FORWARD_SHIPPING_POINT = "shipPoint";
	public static final String FORWARD_LOAD_LDS_SERVER = "ldsServer";
	private static final String FORWARD_LOADING_POINT = "loadPoint";
	private static final String FORWARD_LOAD_MATERIALS = "loadMaterials";
	private static final String FORWARD_STATION = "station";

	@Override
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
			Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_SHIPPINGPOINT);
			ShippingPointForm helpForm;
			String forwardName = "input";
			// Get the struts form.
			helpForm = (ShippingPointForm) form;
			
			// Check if cancel button clicked.
			if ("cancel".equals(helpForm.getFormAction())) {
				helpForm.setFormAction("");
				if (logDebugEnabled) {
					LOG.debug("Action: cancel button clicked!");
				}
			
				//clear all fields
				init(request, helpForm, helpForm.getShippingPoint().getShippingpointId());
				if (!helpForm.getLoadingPointWrapperList().isEmpty()) {
					updateLoadingpointSelection(0, request, helpForm);
				}
				forwardName = FORWARD_SHIPPING_POINT;
			}
			// Check if submit button clicked.
			else if ("save".equals(helpForm.getFormAction())) {
				helpForm.setFormAction("");
				if (logDebugEnabled) {
					LOG.debug("Action: submit(save) button clicked!");
				}
				helpForm.getShippingPoint().setRailwayStation(helpForm.getOperationStation());
			
				// ADN51108
				if ( helpForm.getShippingPoint().getShipppointType().getShipppointTypeCd() == Constant.SHIPPINGPOINT_TYPE_FVL_BIMODAL_TRUCK ) {
					helpForm.getShippingPoint().setRailArrivalWarningMin(Long.valueOf(helpForm.getRailArrivalWarningMin()));
				}
				else {
					helpForm.getShippingPoint().setRailArrivalWarningMin(null);
					helpForm.getShippingPoint().setFvlMixShiptoPerWagonFlag(false);
				}
				
				List<LoadingpointEntity> loadingPoints = unwrapLoadingPointList(helpForm);
				ShippingPointService shippingPointService = getServiceLocator().getShippingPointService();
				LoadingpointEntity badLp = shippingPointService.checkLoadingpointNameUnicity(helpForm.getShippingPoint(), loadingPoints);
				if (badLp != null){
					MessageUtils.addActionWarning(request, "shippingpoint.errors.loadingpointnameexists", badLp.getLoadingpointName());
					forwardName = FORWARD_SHIPPING_POINT;
					result = mapping.findForward(forwardName);
					return result;
				}
				List<LoadingpointEntity> collidingLps = shippingPointService.getCollidingLoadingpoints(helpForm.getShippingPoint(), loadingPoints);
				if (collidingLps != null && !collidingLps.isEmpty()) {
					for (LoadingpointEntity collidingLp : collidingLps) {
						MessageUtils.addActionWarning(request, "shippingpoint.errors.notUnique.weighSystemLpNo", collidingLp.getWeighSystemLoadingpointNo());
						forwardName = FORWARD_SHIPPING_POINT;
					}
					result = mapping.findForward(forwardName);
					return result;
				}
			
				//update shipping point and loading points
				try {
					ShippingpointEntity shippingPoint = helpForm.getShippingPoint();
					String sendSapSpShippingpointIdStr = helpForm.getSendSapSpShippingpointId();
					long sendSapSpShippingpointId = -1L;
					if (!StringUtils.empty(sendSapSpShippingpointIdStr)) {
						sendSapSpShippingpointId = parseSendSapSpShippingpointId(sendSapSpShippingpointIdStr);
					}
					if (sendSapSpShippingpointId == -1L) {
						shippingPoint.setSendSapShippingpoint(null);
						helpForm.setSendSapSpShippingpointName(null);
						helpForm.setSendSapSpShippingpointNo(null);
					}
					else {
						ShippingpointEntity sendSapSp = shippingPointService.loadShippingPoint(sendSapSpShippingpointId);
						shippingPoint.setSendSapShippingpoint(sendSapSp);
						helpForm.setSendSapSpShippingpointName(sendSapSp.getShippingpointName());
						helpForm.setSendSapSpShippingpointNo(sendSapSp.getSapShippingpointNo());
					}
					
					String varHandlingCostMat = helpForm.getVarHandlingCostMat();
					if (StringUtils.notEmpty(varHandlingCostMat)) {
						shippingPoint.setVarHandlingCostMat(new BigDecimal(varHandlingCostMat.replace(',', '.')));
					}
					else {
						shippingPoint.setVarHandlingCostMat(null);
					}

					String futureVarHandlingCostMat = helpForm.getFutureVarHandlingCostMat();
					if (StringUtils.notEmpty(futureVarHandlingCostMat)) {
						shippingPoint.setFutureVarHandlingCostMat(new BigDecimal(futureVarHandlingCostMat.replace(',', '.')));
					}
					else {
						shippingPoint.setFutureVarHandlingCostMat(null);
					}

					String futureValidFromDat = helpForm.getFutureValidFromDat();
					if (StringUtils.notEmpty(futureValidFromDat)) {
						try {
							shippingPoint.setFutureValidFromDat(DateTimeParser.parseLogonDayMonthYear(request.getLocale(), futureValidFromDat));
						}
						catch (ParseException e) {
							throw new LogonException(e);
						}
					}
					else {
						shippingPoint.setFutureValidFromDat(null);
					}

					String shippingPointCurrency = helpForm.getShippingPointCurrency();
					if (Constant.INVALID_OBJECT_ID_STR.equals(shippingPointCurrency)) {
						shippingPoint.setCurrency(null);
					}
					else {
						shippingPoint.setCurrency((CurrencyBsEntity)(helpForm.getCurrencyBsEntityMap().get(shippingPointCurrency)));
					}
					
					String shippingPointUom = helpForm.getShippingPointUom();
					if (Constant.INVALID_OBJECT_ID_STR.equals(shippingPointUom)) {
						shippingPoint.setUom(null);
					}
					else {
						shippingPoint.setUom((UomBsEntity)(helpForm.getUomBsEntityMap().get(shippingPointUom)));
					}
					
					Long weighSystemSpCd = helpForm.getWeighSystemSpCd();
					shippingPoint.setWeighSystemSp(getRefDataGenericsService().findBsEntity(WeighSystemSpBsEntity.class, weighSystemSpCd));
					
					List storageLocationBeanList = helpForm.getStorageLocationBeanList();
					for (Object o : storageLocationBeanList) {
						StorageLocationBean storageLocationBean = (StorageLocationBean) o;
						ShippingpointEntity shippingpoint = storageLocationBean.getShippingpoint();
						SapMaterialPlantDescEntity sapMaterialPlantDesc = storageLocationBean.getSapMaterialPlantDesc();
						MatShippingpointEntity matShippingpoint = storageLocationBean.getMatShippingpoint();

						boolean storageLocationIsDefault = storageLocationBean.getStorageLocationIsDefault();

						if (storageLocationIsDefault) {
							if (matShippingpoint != null && matShippingpoint.getVarHandlingCostMat() == null &&
									matShippingpoint.getFutureVarHandlingCostMat() == null &&
									matShippingpoint.getFutureValidFromDat() == null &&
									matShippingpoint.getCurrency() == null) {
								shippingPointService.deleteMatShippingpoint(matShippingpoint.getMatShippingpointId());
								storageLocationBean.setMatShippingpoint(null);
							}
						}
						else {
							String storageLocation = storageLocationBean.getStorageLocation();
							if (StringUtils.empty(storageLocation)) {
								storageLocation = null;
							}

							if (matShippingpoint != null) {
								matShippingpoint.setStorageLocation(storageLocation);
								matShippingpoint = shippingPointService.updateMatShippingpoint(matShippingpoint);
								storageLocationBean.setMatShippingpoint(matShippingpoint);
							}
							else {
								matShippingpoint = new MatShippingpointEntity();
								matShippingpoint.setStorageLocation(storageLocation);

								SapMaterialEntity sapMaterial =
										shippingPointService.loadSapMaterial(sapMaterialPlantDesc.getSapMaterialId());
								matShippingpoint.setSapMaterial(sapMaterial);

								matShippingpoint.setShippingpoint(shippingpoint);
								matShippingpoint = shippingPointService.createMatShippingpoint(matShippingpoint);

								storageLocationBean.setMatShippingpoint(matShippingpoint);
							}
						}
					}

					updateTimeZoneOnShippingpoint(shippingPoint, helpForm.getSelectedTimeZoneCd());
					
					shippingPointService.updateTruckRoutesToShippingPointCoords(shippingPoint);
					
					ShippingpointEntity shippingpointEntity = shippingPointService.updateShippingPoint(shippingPoint);
					loadingPoints = shippingPointService.updateLoadingPoints(loadingPoints);

					AdminRFIDPopupForm.resetForm(request.getSession());

					Long selectedLoadingpointId = null;
					if (!loadingPoints.isEmpty()) {
						selectedLoadingpointId = (loadingPoints.get(helpForm.getCurrLoadingPointIndex())).getLoadingpointId();
					}
					helpForm.setShippingPoint(shippingpointEntity);
					
					MessageUtils.addActionMessage(request, "message.successIns");
					initLoadingPoints(helpForm, helpForm.getShippingPoint().getShippingpointId(), selectedLoadingpointId, request);
					initPackageWeighTypes(request, helpForm);	
				}
				catch (LogonException re) {
					Reason reason = re.getReason();
					if (reason.getReason().equals(Reason.RES_RFID_OBJECT_LABEL_NOT_UNIQUE.getReason())){
						MessageUtils.addActionWarning(request, RFIDBean.getErrorMessage(Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO));
					}
					else {
						throw re;
					}
				}
				catch (RuntimeException re) {
					// TODO (emoke Jun 3, 2009):this is not clear how it is thrown... sometimes EJB exception sometimes simple Logonexception need to check
					 if (re.getCause() instanceof LogonException) {
						LogonException le = (LogonException)re.getCause();
						Reason reason = le.getReason();
						if (reason.getReason().equals(Reason.RES_RFID_OBJECT_LABEL_NOT_UNIQUE.getReason())){
							MessageUtils.addActionWarning(request, RFIDBean.getErrorMessage(Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO));
						}
						else {
							throw le;
						}
					}
					else {
						 throw re;
					}
				}
				forwardName = FORWARD_SHIPPING_POINT;
			}
			else if (FORWARD_LOAD_LDS_SERVER.equals(helpForm.getFormAction())) {
				String weighSystemSpCd = request.getParameter("ldsType");
				if (logDebugEnabled) {
					LOG.debug("Action: lds type dropdown selected!");
					LOG.debug("Lds type: " + weighSystemSpCd);
				}
				
				long lWeighSystemSpCd = parseLongId(weighSystemSpCd);
				WeighSystemSpBsEntity weighSystem = getRefDataGenericsService().findBsEntity(WeighSystemSpBsEntity.class, lWeighSystemSpCd);
				
				if (weighSystem.getConfigTableInfix() != null) {
					List ldsServerList = getShippingPointService().loadLdsServers(weighSystem.getConfigTableInfix());
					helpForm.setLdsServerList(ldsServerList);
					boolean ldsServerRequired = getShippingPointService().isLdsServerRequired(weighSystem.getConfigTableInfix());
					helpForm.setLdsServerRequired(ldsServerRequired);
				}
				else {
					helpForm.setLdsServerList(new ArrayList());
					helpForm.setLdsServerRequired(false);
				}
				
				forwardName = FORWARD_LOAD_LDS_SERVER;
			}
			else if (request.getParameter("select") != null) {
				String spid = request.getParameter("spid");
				if (logDebugEnabled) {
					LOG.debug("Action: shipping point DropDown is selected!");
					LOG.debug("Shipping point id: " + spid);
				}
			
				long lspid = parseLongId(spid);
				if (lspid != Constant.INVALID_OBJECT_ID) {
					init(request, helpForm, lspid);
			
					if (!helpForm.getLoadingPointWrapperList().isEmpty()) {
						updateLoadingpointSelection(0, request, helpForm);//set first tab
					}
				}
				forwardName = FORWARD_SHIPPING_POINT;
			}
			else if (request.getParameter("selecttab") != null) {
				String lpIndex = request.getParameter("lpindex");
				if (logDebugEnabled) {
					LOG.debug("Action: laoding point Tab (index: " + lpIndex + ") is selected!");
				}
			
				int ilpIndex = parseIntId(lpIndex);
				if (ilpIndex != (int)Constant.INVALID_OBJECT_ID && !helpForm.getLoadingPointWrapperList().isEmpty()) {
					updateLoadingpointSelection(ilpIndex, request, helpForm);
				}
			
				forwardName = FORWARD_LOADING_POINT;
			}
			else if (request.getParameter("addtab") != null && helpForm.isLoadingpointAddingAllowed()) {
				if (logDebugEnabled) {
					LOG.debug("Action: add laoding point!");
				}
				addTab(request, helpForm);
				forwardName = FORWARD_LOADING_POINT;
			}
			else if (request.getParameter("removetab") != null) {
				if (logDebugEnabled) {
					LOG.debug("Action: remove laoding point Tab (index: "
							+ helpForm.getCurrLoadingPointIndex() + ")!");
				}
				removeTab(helpForm, request);
				forwardName = FORWARD_LOADING_POINT;
			}
			else if (request.getParameter("selectStation") != null) {
				if (logDebugEnabled) {
					LOG.debug("Action: select station");
				}
				forwardName = FORWARD_STATION;
			}
			else if (request.getParameter("loadMaterials") != null) {
				String packTypeCd = request.getParameter("packTypeCd");
				if (logDebugEnabled) {
					LOG.debug("Action: load materials! Package type code: " + packTypeCd);
				}
			
				long packTypeCdL = parseLongId(packTypeCd);
			
				(helpForm.getLoadingPointWrapperList().get(helpForm.getCurrLoadingPointIndex()))
						.setPackageTypeCd(packTypeCdL);
			
				forwardName = FORWARD_LOAD_MATERIALS;
			}

			result = mapping.findForward(forwardName);
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

	private void init(HttpServletRequest request, ShippingPointForm helpForm, long spId) {
		// [[
		Object[][] params = {{"request", request},
							{"helpForm", helpForm},
							{"spId", spId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init", params);
		}
		// ]]
		try {
			
			//get shipping point
			ShippingpointEntity spEntity = getShippingPointService().loadShippingPoint(spId);
			helpForm.setShippingPoint(spEntity);

			ShippingpointEntity sendSapSp = spEntity.getSendSapShippingpoint();
			if (sendSapSp != null) {
				helpForm.setSendSapSpShippingpointId(sendSapSp.getShippingpointId().toString());
				helpForm.setSendSapSpShippingpointName(sendSapSp.getShippingpointName());
				helpForm.setSendSapSpShippingpointNo(sendSapSp.getSapShippingpointNo());
			}
			else {
				helpForm.setSendSapSpShippingpointId(null);
				helpForm.setSendSapSpShippingpointName(null);
				helpForm.setSendSapSpShippingpointNo(null);
			}

			BigDecimal varHandlingCostMat = spEntity.getVarHandlingCostMat();
			if (varHandlingCostMat == null) {
				helpForm.setVarHandlingCostMat(null);
			}
			else {
				helpForm.setVarHandlingCostMat(varHandlingCostMat.toString());
			}
			BigDecimal futureVarHandlingCostMat = spEntity.getFutureVarHandlingCostMat();
			if (futureVarHandlingCostMat == null) {
				helpForm.setFutureVarHandlingCostMat(null);
			}
			else {
				helpForm.setFutureVarHandlingCostMat(futureVarHandlingCostMat.toString());
			}
			Date futureValidFromDat = spEntity.getFutureValidFromDat();
			if (futureValidFromDat == null) {
				helpForm.setFutureValidFromDat(null);
			}
			else {
				helpForm.setFutureValidFromDat(helpForm.getTimeZone().fmtAsDayMonthYear(futureValidFromDat));
			}
			
			AdminRFIDPopupForm.resetForm(request.getSession());
			
			if (spEntity.getRailwayStation() != null) {
				RailwayStationEntity[] stations = getShippingPointService().loadOpAndCommRailwayStations(spEntity.getRailwayStation().getRailwayStationId());
				if ( stations != null) {
					helpForm.setOperationStation(stations[0]);
					helpForm.setCommStation(stations[1]);
				}
			}
			else {
				//reset
				helpForm.setCommStation(null);
				helpForm.setOperationStation(null);
			}
			
			helpForm.setWeighSystemSpCd(spEntity.getWeighSystemSp().getWeighSystemSpCd());
			
			//get ref data for all GUI lists 
			RefDataGenericsService refDataService = getRefDataGenericsService();
			Locale locale = getLocale(request);
			
			List spTypesList = refDataService.findTxEntityList(ShipppointTypeTxEntity.class, locale);
			helpForm.setSpTypeList(spTypesList);
			List sapShippingCondList = refDataService.findTxEntityList(SapShippingCondTxEntity.class, locale);
			helpForm.setSapShippingCondList(sapShippingCondList);
			List packageTypeList = refDataService.findTxEntityList(PackageTypeTxEntity.class, locale);
			helpForm.setPackageTypeList(packageTypeList);
			List delivNotePrintTypeList = refDataService.findTxEntityList(DelivNotePrintTypeTxEntity.class, locale);
			helpForm.setDelivNotePrintTypeList(delivNotePrintTypeList);

			List printerList = refDataService.findBsEntityList(PrinterBsEntity.class);
			helpForm.setPrinterList(printerList);
			
			List weighSystemSpList = refDataService.findTxEntityList(WeighSystemSpTxEntity.class, locale);
			helpForm.setWeighSystemSpList(weighSystemSpList);
			List weighSystemLpList = refDataService.findTxEntityList(WeighSystemLpTxEntity.class, locale);
			helpForm.setWeighSystemLpList(weighSystemLpList);
			if (spEntity.getWeighSystemSp().getConfigTableInfix() != null) {
				List ldsServerList = getShippingPointService().loadLdsServers(spEntity.getWeighSystemSp().getConfigTableInfix());
				helpForm.setLdsServerList(ldsServerList);
				boolean ldsServerRequired = getShippingPointService().isLdsServerRequired(spEntity.getWeighSystemSp().getConfigTableInfix());
				helpForm.setLdsServerRequired(ldsServerRequired);
			}
			else {
				helpForm.setLdsServerList(new ArrayList());
				helpForm.setLdsServerRequired(false);
			}
			
			List materialList = getShippingPointService().loadPlantMaterial(
					spEntity.getPlant().getPlantCd(), locale.getLanguage());
			helpForm.setMaterialList(materialList);
			
			List<MaterialPackageBsEntity> materialPackageBsEntityList = refDataService.findBsEntityList(MaterialPackageBsEntity.class, 
					true, MaterialPackageBsEntity.ATTR_LOGON_PACKAGE_TYPE_CD, true);
			List storageLocationBeanList = buildStorageLocationBeanList(spEntity, materialList, materialPackageBsEntityList);
			helpForm.setStorageLocationBeanList(storageLocationBeanList);
			
			initLoadingPoints(helpForm, spId, null, request);
			
			initPackageWeighTypes(request, helpForm);
			
			helpForm.setRailArrivalWarningMin(spEntity.getRailArrivalWarningMin() == null ? null : spEntity.getRailArrivalWarningMin().toString());
			
			List<CurrencyBsEntity> currencyBsEntityList = refDataService.findBsEntityList(CurrencyBsEntity.class);
			Map<String, CurrencyBsEntity> currencyBsEntityMap = new LinkedHashMap<>();
			for (CurrencyBsEntity bs : currencyBsEntityList) {
				currencyBsEntityMap.put(bs.getCurrencyCd(), bs);
			}
			helpForm.setCurrencyBsEntityMap(currencyBsEntityMap);
			if (spEntity.getCurrency() != null) {
				helpForm.setShippingPointCurrency(spEntity.getCurrency().getCurrencyCd());
			}
			else {
				helpForm.setShippingPointCurrency(null);
			}

			List<UomBsEntity> uomBsEntityList = refDataService.findBsEntityList(UomBsEntity.class, true, UomBsEntity.ATTR_UOM_CD, true);
			Map<String, UomBsEntity> uomBsEntityMap = new LinkedHashMap<>();
			for (UomBsEntity bs : uomBsEntityList) {
				uomBsEntityMap.put(bs.getUomCd(), bs);
			}
			helpForm.setUomBsEntityMap(uomBsEntityMap);
			if (spEntity.getUom() != null) {
				helpForm.setShippingPointUom(spEntity.getUom().getUomCd());
			}
			else {
				helpForm.setShippingPointUom(null);
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

	private void initLoadingPoints(ShippingPointForm helpForm, long spId, Long selectedLoadingpointId,
			HttpServletRequest request) {
		// [[
		Object[][] params = { { "helpForm", helpForm },
				{ "spId", spId + "" },
				{ "selectedLoadingpointId", selectedLoadingpointId },
				{ "request", request } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initLoadingPoints", params);
		}
		// ]]
		try {

			//get loading points 
			List loadingPointList = getShippingPointService().loadLoadingPoint(spId);
			RfidobjectTypeBsEntity bs = getRefDataGenericsService()
					.findBsEntity(RfidobjectTypeBsEntity.class, Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO);
			for (Object o : loadingPointList) {
				LoadingpointEntity loadingpointEntity = (LoadingpointEntity) o;
				if (loadingpointEntity.getRfidobject() == null) {
					RfidobjectEntity rfidobject = new RfidobjectEntity();
					rfidobject.setRfidobjectType(bs);
					loadingpointEntity.setRfidobject(rfidobject);
				}
			}

			//reset
			helpForm.setLoadingPointWrapperList(new ArrayList<>());

			//load material restrictions
			for (int i = 0; i < loadingPointList.size(); i++) {
				LoadingpointEntity lpe = (LoadingpointEntity) loadingPointList.get(i);

				LoadingPointBean lpBean = new LoadingPointBean();
				lpBean.setMaterialList(helpForm.getMaterialList());
				lpBean.setLpEntity(lpe);

				if (lpe.getGrossWeightLimitKg() != null) {
					double grossWeightLimitKg = lpe.getGrossWeightLimitKg().doubleValue() / 1000;
					lpBean.setGrossWeightLimitKg(grossWeightLimitKg + "");
				}

				if (lpe.getLoadingTime() != null) {
					lpBean.setLoadingTime(lpe.getLoadingTime() + "");
				}
				else {
					lpBean.setLoadingTimeFlag(true);
				}
				lpBean.setDelivNotePrintTypeCd(lpe.getDelivNotePrintType().getDelivNotePrintTypeCd());
				Long delivNoteCopiesLogon = lpe.getDelivNoteCopiesLogon();
				if (delivNoteCopiesLogon != null) {
					lpBean.setDelivNoteCopiesLogon(String.valueOf(lpe.getDelivNoteCopiesLogon()));
				}
				else {
					lpBean.setDelivNoteCopiesLogon(null);
				}
				lpBean.setPackageTypeCd(lpe.getPackageType().getPackageTypeCd());
				lpBean.setWeighSystemLpCd(lpe.getWeighSystemLp().getWeighSystemLpCd());

				if (lpe.getPrinter() != null) {
					lpBean.setPrinterCd(lpe.getPrinter().getPrinterCd());
				}

				helpForm.getLoadingPointWrapperList().add(lpBean);
				if (lpBean.getLpEntity().getLoadingpointId().equals(selectedLoadingpointId)) {
					updateLoadingpointSelection(i, request, helpForm);
				}

				if (lpe.getLeaveLpDelayMin() != null) {
					Long leaveLpDelay = lpe.getLeaveLpDelayMin();
					lpBean.setLeaveLpDelayMin(leaveLpDelay.toString());
				}
			}

			// After removing/adding a tabs, the currentIndex might be based on the wrapper list containing inactive loading
			// points, so the currentindex is validated and possibly reset here
			if ((helpForm.getLoadingPointWrapperList() != null) && (!helpForm.getLoadingPointWrapperList().isEmpty()) && (
					helpForm.getCurrLoadingPointIndex() >= helpForm.getLoadingPointWrapperList().size())) {
				updateLoadingpointSelection(0, request, helpForm);
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

	private void initPackageWeighTypes(HttpServletRequest request, ShippingPointForm helpForm) {
		// [[
		Object[][] params = { { "request", request },
				{ "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initPackageWeighTypes", params);
		}
		// ]]
		try {

			//reset lists
			helpForm.setPackageTypeSPList(new ArrayList());
			helpForm.setWeighSysTypeSPList(new ArrayList());

			//get ref data service 
			RefDataGenericsService refDataService = getRefDataGenericsService();
			Locale locale = getLocale(request);

			for (int i = 0; i < helpForm.getLoadingPointWrapperList().size(); i++) {
				LoadingPointBean lpBean = helpForm.getLoadingPointWrapperList().get(i);

				//only active loading points
				if (Boolean.TRUE.equals(lpBean.getLpEntity().getActiveFlag())) {

					Long pTypeCd = lpBean.getPackageTypeCd();
					PackageTypeTxEntity pTypeEnt = refDataService.findTxEntity(PackageTypeTxEntity.class, locale,
							pTypeCd);

					if (!helpForm.getPackageTypeSPList().contains(pTypeEnt.getTxt())) {
						helpForm.getPackageTypeSPList().add(pTypeEnt.getTxt());
					}

					Long wTypeCd = lpBean.getWeighSystemLpCd();
					WeighSystemLpTxEntity wTypeEnt = refDataService.findTxEntity(WeighSystemLpTxEntity.class,
							locale, wTypeCd);

					if (!helpForm.getWeighSysTypeSPList().contains(wTypeEnt.getTxt())) {
						helpForm.getWeighSysTypeSPList().add(wTypeEnt.getTxt());
					}
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

	private List<LoadingpointEntity> unwrapLoadingPointList(ShippingPointForm helpForm) {
		// [[
		List result = null;
		Object[][] params = { { "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "unwrapLoadingPointList", params);
		}
		// ]]
		try {
			List<LoadingpointEntity> loadingPointList = new ArrayList<>();

			for (int i = 0; i < helpForm.getLoadingPointWrapperList().size(); i++) {
				LoadingPointBean lpBean = helpForm.getLoadingPointWrapperList().get(i);

				// TODO (valdas Apr 8, 2009): BIG ONE! Don't fetch second time! We have these entities already! 
				//load package type 
				Long packageTypeCd = lpBean.getPackageTypeCd();
				PackageTypeBsEntity packageTypeBsEntity =
						getRefDataGenericsService().findBsEntity(PackageTypeBsEntity.class, packageTypeCd);
				lpBean.getLpEntity().setPackageType(packageTypeBsEntity);

				//load weighing system type
				Long weighSystemLpCd = lpBean.getWeighSystemLpCd();
				WeighSystemLpBsEntity weighSystemLpBsEntity =
						getRefDataGenericsService().findBsEntity(WeighSystemLpBsEntity.class, weighSystemLpCd);
				lpBean.getLpEntity().setWeighSystemLp(weighSystemLpBsEntity);

				//load delivery note print type
				Long delivNotePrintTypeCd = lpBean.getDelivNotePrintTypeCd();
				DelivNotePrintTypeBsEntity delivNotePrintTypeBsEntity =
						getRefDataGenericsService().findBsEntity(DelivNotePrintTypeBsEntity.class, delivNotePrintTypeCd);
				lpBean.getLpEntity().setDelivNotePrintType(delivNotePrintTypeBsEntity);

				//load printer
				Long printerCd = lpBean.getPrinterCd();
				if ((printerCd == null) || (printerCd == Constant.INVALID_OBJECT_ID)) {
					lpBean.getLpEntity().setPrinter(null);
				}
				else {
					PrinterBsEntity printerBsEntity = getRefDataGenericsService().findBsEntity(PrinterBsEntity.class, printerCd);
					lpBean.getLpEntity().setPrinter(printerBsEntity);
				}

				setDeliveryNoteCopies(lpBean, delivNotePrintTypeCd);

				loadingPointList.add(lpBean.getLpEntity());
			}

			result = loadingPointList;
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

	private void setDeliveryNoteCopies(LoadingPointBean lpBean, Long delivNotePrintTypeCd) {
		if (delivNotePrintTypeCd == Constant.DELIV_NOTE_PRINT_TYPE_LOGON) {
			String delivNoteCopiesLogonStr = lpBean.getDelivNoteCopiesLogon();
			Long delivNoteCopiesLogon = null;
			if (!StringUtils.empty(delivNoteCopiesLogonStr)) {
				try {
					delivNoteCopiesLogon = Long.valueOf(delivNoteCopiesLogonStr);
				}
				catch (NumberFormatException e) {
					LOG.warn("delivNoteCopiesLogonStr (" + delivNoteCopiesLogonStr + ") wasn't a number");
				}
			}
			lpBean.getLpEntity().setDelivNoteCopiesLogon(delivNoteCopiesLogon);
		}
		else {
			lpBean.getLpEntity().setDelivNoteCopiesLogon(null);
		}
	}

	private void addTab(HttpServletRequest request, ShippingPointForm helpForm) {
		// [[
		Object[][] params = { { "request", request },
				{ "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addTab", params);
		}
		// ]]
		try {

			if (helpForm.getLoadingPointWrapperList() != null) {
				LoadingpointEntity lpEntity = new LoadingpointEntity();
				lpEntity.setLoadingpointId(null);
				lpEntity.setShippingpoint(helpForm.getShippingPoint());
				lpEntity.setPackageType(new PackageTypeBsEntity());
				lpEntity.setWeighSystemLp(new WeighSystemLpBsEntity());
				lpEntity.setDelivNotePrintType(new DelivNotePrintTypeBsEntity());
				lpEntity.setMatRestrictions(new ArrayList<>());
				// FIXME (valdas Feb 23, 2009): what type to add? 
				lpEntity.setActiveFlag(true);

				Locale locale = getLocale(request);
				String lpName = MessageUtils.getMessage(request, locale, "shippingpoint.lpoint.field.name.value");
				lpEntity.setLoadingpointName(lpName);
				lpEntity.setLoadingpointObjectLabel(lpName);

				RfidobjectEntity rfidObj = new RfidobjectEntity();
				RefDataGenericsService refServ = getServiceLocator().getRefDataGenericsService();
				RfidobjectTypeBsEntity bs = refServ.findBsEntity(RfidobjectTypeBsEntity.class,
						Constant.RFIDOBJECT_TYPE_LOADINGPOINT_SILO);
				rfidObj.setRfidobjectType(bs);
				rfidObj.setRfidobjectId(null);
				rfidObj.setRfids(new ArrayList<>());
				lpEntity.setRfidobject(rfidObj);

				LoadingPointBean lpBean = new LoadingPointBean();
				lpBean.setLpEntity(lpEntity);
				lpBean.setMaterialList(helpForm.getMaterialList());
				lpBean.setLoadingTimeFlag(true);

				helpForm.getLoadingPointWrapperList().add(lpBean);
				updateLoadingpointSelection(helpForm.getLoadingPointWrapperList().size() - 1, request, helpForm);
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
	 * Updates the loadingpoint selection of the helpForm and sets the rfobject for rfidTile.
	 */
	private void updateLoadingpointSelection(int index, HttpServletRequest request, ShippingPointForm helpForm) {
		// [[
		Object[][] params = { { "index", index + "" },
				{ "request", request },
				{ "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateLoadingpointSelection", params);
		}
		// ]]
		try {
			helpForm.setCurrLoadingPointIndex(index);
			setRfObject(request, helpForm);
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
	 * Sets the rfObject which will be manipulated in case hinzufugen will be clicked on Loadingpoint screen.
	 */
	private void setRfObject(HttpServletRequest request, ShippingPointForm helpForm) {
		// [[
		Object[][] params = { { "request", request },
				{ "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setRfObject", params);
		}
		// ]]
		try {
			int ilpIndex = helpForm.getCurrLoadingPointIndex();
			if (helpForm.getLoadingPointWrapperList().size() <= ilpIndex) {
				return;
			}
			LoadingPointBean lpB = helpForm.getLoadingPointWrapperList().get(ilpIndex);
			LoadingpointEntity lpEntity = lpB.getLpEntity();
			RfidobjectEntity rfObj = lpEntity.getRfidobject();
			if (rfObj == null) {
				rfObj = new RfidobjectEntity();
				rfObj.setRfidobjectType(helpForm.getRfidType());
			}
			lpEntity.setRfidobject(rfObj);
			AbstractRFIDBean rfbean = RFIDBean.LoadingpointRFIDBean(lpEntity);
			RFIDPopupForm.initForm(request.getSession(), rfbean);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void removeTab(ShippingPointForm helpForm, HttpServletRequest request) {
		// [[
		Object[][] params = { { "helpForm", helpForm },
				{ "request", request } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "removeTab", params);
		}
		// ]]
		try {
			if (!helpForm.getLoadingPointWrapperList().isEmpty()) {
				int index = helpForm.getCurrLoadingPointIndex();
				LoadingPointBean lpBean = helpForm.getLoadingPointWrapperList().get(index);

				//id it is not save to db then just delete from the list
				if (lpBean.getLpEntity().getLoadingpointId() == null) {
					helpForm.getLoadingPointWrapperList().remove(index);
				}
				else {
					LoadingPointBean lpB = helpForm.getLoadingPointWrapperList().get(index);
					lpB.getLpEntity().setActiveFlag(false);
				}

				//set next active tab
				for (int i = 0; i < helpForm.getLoadingPointWrapperList().size(); i++) {
					LoadingPointBean lpB = helpForm.getLoadingPointWrapperList().get(i);

					if (Boolean.TRUE.equals(lpB.getLpEntity().getActiveFlag())) {
						updateLoadingpointSelection(i, request, helpForm);//set tab
						break;
					}
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

	private long parseSendSapSpShippingpointId(String sendSapSpShippingpointIdStr) {
		try {
			return Long.parseLong(sendSapSpShippingpointIdStr);
		}
		catch (NumberFormatException e) {
			LOG.error("The given shippingpointId (" + sendSapSpShippingpointIdStr + ") is not a number.");
			throw new LogonException(e);
		}
	}

	private long parseLongId(String in) {
		try {
			return Long.parseLong(in);
		}
		catch (NumberFormatException e) {
			LOG.error("user tampered with data, could not parse: '" + in + "'");
			return Constant.INVALID_OBJECT_ID;
		}
	}

	private int parseIntId(String in) {
		try {
			return Integer.parseInt(in);
		}
		catch (NumberFormatException e) {
			LOG.error("user tampered with data, could not parse: '" + in + "'");
			return (int)Constant.INVALID_OBJECT_ID;
		}
	}

	private List buildStorageLocationBeanList(ShippingpointEntity shippingpoint, List availableMaterialList,
			List<MaterialPackageBsEntity> materialPackageBsEntities) {
		List<StorageLocationBean> storageLocationBeanList = new ArrayList<>();

		Set<String> materialTypesWithStorageLocation = new HashSet<>();
		for (MaterialPackageBsEntity bs : materialPackageBsEntities) {
			if (Boolean.TRUE.equals(bs.getCanHaveStorageLocationFlag())) {
				materialTypesWithStorageLocation.add(bs.getMaterialPackageCd());
			}
		}

		for (Object o : availableMaterialList) {
			SapMaterialPlantDescEntity sapMaterialPlantDesc = (SapMaterialPlantDescEntity) o;
			if (materialTypesWithStorageLocation.contains(sapMaterialPlantDesc.getMaterialPackageCd())) {
				MatShippingpointEntity matShippingpoint =
						getShippingPointService().loadMatShippingpoint(shippingpoint.getShippingpointId(),
								sapMaterialPlantDesc.getSapMaterialId());

				StorageLocationBean storageLocationBean = new StorageLocationBean();
				storageLocationBean.setShippingpoint(shippingpoint);
				storageLocationBean.setSapMaterialPlantDesc(sapMaterialPlantDesc);
				storageLocationBean.setMatShippingpoint(matShippingpoint);
				storageLocationBeanList.add(storageLocationBean);
			}
		}

		return storageLocationBeanList;
	}

	private boolean updateTimeZoneOnShippingpoint(ShippingpointEntity shippingpoint, String selectedTimeZoneCd) {
		// [[
		boolean result = false;
		Object[][] params = {{"shippingpoint", shippingpoint},
							 {"selectedTimeZoneCd", selectedTimeZoneCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "updateTimeZoneOnUser", params);
		}
		// ]]
		try {
			String currentTimeZoneCd = shippingpoint.getTimezone() != null
					? shippingpoint.getTimezone().getTimezoneCd()
					: Constant.INVALID_OBJECT_ID_STR;

			boolean updateTimezone = !StringUtils.areEqual(currentTimeZoneCd, selectedTimeZoneCd);

			if (updateTimezone) {
				TimezoneBsEntity tzEntity = getServiceLocator().getRefDataGenericsService()
						.findBsEntity(TimezoneBsEntity.class, selectedTimeZoneCd);

				shippingpoint.setTimezone(tzEntity);
			}

			result = updateTimezone;
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
}
