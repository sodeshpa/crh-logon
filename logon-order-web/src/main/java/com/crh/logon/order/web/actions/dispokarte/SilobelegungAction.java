/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispokarte;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.bestellung.BestellungForm;
import com.crh.logon.order.web.forms.dispokarte.DispokarteForm;
import com.crh.logon.order.web.forms.dispokarte.SalesAreaBean;
import com.crh.logon.order.web.forms.dispokarte.SilobelegungEditForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.SiloAdminServiceLocalInterface;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaRestrEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.actions.RFIDPopupAction;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.ServiceLocatorProvider;

import flexjson.HTMLEncoder;
import flexjson.JSONSerializer;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispokarte_SilobelegungEditForm"
 *  path="/dispokarte/Silobelegung" scope="session"
 *  validate="false" input="DispokarteSilobelegungEditTile.def"
 *
 * @struts.action-forward name="list" path="DispokarteSilobelegungTile.def"
 * @struts.action-forward name="close" path="DispokarteSilobelegungCloseTile.def"
 * @struts.action-forward name="error" path="DispokarteSilobelegungEditTile.def"
 */
public class SilobelegungAction extends OrderActionSupport {
	
	private RFIDServiceLocalInterface rfidService;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SilobelegungAction.class);

	private static final ShiptoSiloEntity NO_SILO_SELECTED = null;

	private static SiloAdminServiceLocalInterface localSiloAdminService;

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
			// Get the struts form(s).
			Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_W);
			SilobelegungEditForm helpForm = (SilobelegungEditForm) form;
			String referrer = helpForm.getReferrer();
			DispokarteForm dispoForm = null;
			BestellungForm orderForm = null;
			ShiptoEntity shipto = null;
			initServices();
			
			// --- If called from Dispokarte... ---
			if (SilobelegungEditForm.REFERRER_DISPOKARTE.equals(referrer)) {
				HttpSession session = request.getSession();
				dispoForm = (DispokarteForm) session.getAttribute(DispokarteForm.FORM_NAME);
				shipto = dispoForm.getShipTo();
			}
			// --- ...or from Order taking ---
			else {
				Authorization.authorize(Authorization.ORDER);
				orderForm = BestellungForm.getSessionInstance(request);
				shipto = orderForm.getShipTo();
			}
				
			String forwardName = "list";

			if (request.getParameter("delete") != null) {
				deleteSilo(helpForm, dispoForm, shipto);
			}
			if (request.getParameter("save") != null) {
				ShiptoSiloEntity helpFormsSilo = helpForm.getSilo();
				if (helpFormsSilo != null) {
					// validation
					final String maxVolM3Entered = helpForm.getMaxVolumeMeter();
					final String maxVolTonsEntered = helpForm.getMaxVolumeTons();

					if (!StringUtils.empty(maxVolM3Entered)) {
						try {
							BigDecimal bigDecimalMaxVolM3Entered = StringUtils.toBigDecimal(maxVolM3Entered);
							helpFormsSilo.setMaxVolumeM3(bigDecimalMaxVolM3Entered);
						} catch (ParseException e) {
							MessageUtils.addActionWarning(
									request, "dispokarte.errors.maxvolmnotnumeric");
						}
					}

					if (!StringUtils.empty(maxVolTonsEntered)) {
						try {
							BigDecimal bigDecimalMaxVolTonsEntered = StringUtils.toBigDecimal(maxVolTonsEntered);
							helpFormsSilo.setMaxVolumeTons(bigDecimalMaxVolTonsEntered);
						}
						catch (ParseException e) {
							MessageUtils.addActionWarning(
									request, "dispokarte.errors.maxvoltnotnumeric");
						}
					}

					if (helpForm.isUnloadingTimeCheckbox()) {
						helpFormsSilo.setUnloadingTime(null);
					}

					String siloName = helpFormsSilo.getSiloName();
					if (StringUtils.empty(siloName)) {
						MessageUtils.addActionWarning(request, "dispokarte.errors.emptysiloname");
					}

					Map<String, ShiptoSiloEntity> siloNameMap = helpForm.getSiloNameMap();
					if (siloNameMap.containsKey(siloName)) {
						ShiptoSiloEntity siloFromMap = siloNameMap.get(siloName);
						if (!helpFormsSilo.equals(siloFromMap)) {
							MessageUtils.addActionWarning(
								request, "dispokarte.errors.notuniquesiloname");
						}
					}

					ActionMessages actionWarnings = MessageUtils.getActionWarnings(request);
					if (actionWarnings != null && actionWarnings.size() > 0) {
						return result = mapping.findForward("error");
					}

					// load actual SapMaterialEntity
					int materialIdx = helpForm.getMaterialIdx();
					if (materialIdx < 0) {
						helpFormsSilo.setSapMaterial(null);
					}
					else {
						List<SapMaterialDescEntity> materials = helpForm.getMaterials();
						SapMaterialDescEntity matDescEntity = materials.get(materialIdx);
						Long sapMaterialId = matDescEntity.getSapMaterialId();
						SapMaterialEntity sapMat =
								getBestellungService().getSapMaterialById(sapMaterialId);
						helpFormsSilo.setSapMaterial(sapMat);
						addSiloMaterialDescription(dispoForm, orderForm, matDescEntity);
					}

					if (helpForm.getAction().equals("add")) {
						addSilo(dispoForm, shipto, helpFormsSilo);
					}
				}

				AbstractRFIDBean bean = RFIDBean.ShiptoSiloRFIDBean(helpFormsSilo);
				RFIDPopupAction.validateRFIDObject(request, bean, rfidService);
				forwardName = "close";
			}

			// --- Only relevant of Order Taking ---
			if (SilobelegungEditForm.REFERRER_ORDER_TAKING.equals(referrer)
					&& (request.getParameter("save") != null || request.getParameter("delete") != null)) {

				// --- Immediate saving of new silo data (in contrast to the
				// "lazy" approach of Dispokarte) ---
				DispokarteService dispokarteService = getDispokarteService();
				ShiptoEntity mergedShipto = dispokarteService.mergeShipto(shipto, null);
				if (orderForm != null) {
					orderForm.setShipTo(mergedShipto, createTimeZoneFor(mergedShipto));

					// --- Re-serializing the content of the silo combobox ---
					JSONSerializer jsonSerializer = new JSONSerializer();
					List<ShiptoSiloEntity> shiptoActiveSilos = orderForm.getShiptoActiveSilos();
					jsonSerializer = jsonSerializer.include("shiptoSiloId");
					jsonSerializer = jsonSerializer.include("siloName");
					HTMLEncoder htmlEncoder = new HTMLEncoder();
					jsonSerializer = jsonSerializer.transform(htmlEncoder, "siloName");
					jsonSerializer = jsonSerializer.exclude("*");
					String serializedSiloList = jsonSerializer.serialize(shiptoActiveSilos);
					orderForm.getJson().put("siloList", serializedSiloList);
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

	public void initServices() {
		ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();
		rfidService = serviceLocator.getLocalRFIDService();
		localSiloAdminService = serviceLocator.getLocalSiloAdminService();
	}

	private static void deleteSilo(SilobelegungEditForm helpForm, DispokarteForm dispoForm,
			ShiptoEntity shipto) {
		int selectedSiloIdentifier = helpForm.getId();
		if (selectedSiloIdentifier < 0) {
			return;
		}
		List<ShiptoSiloEntity> shiptoSilos = shipto.getSilos();
		ShiptoSiloEntity selectedSilo = shiptoSilos.get(selectedSiloIdentifier);
		if (selectedSilo == NO_SILO_SELECTED) {
			return;
		}
		boolean portableSiloFlag = selectedSilo.getPortableSiloFlag();
		selectedSilo.setActiveFlag(portableSiloFlag);
		if (dispoForm != null) {
			List<SareaEntity> salesAreas = dispoForm.getSalesAreas();
			if (salesAreas != null) {
				for (SareaEntity salesArea : salesAreas) {
					SalesAreaBean currentSalesArea = dispoForm.getSalesAreaBean(salesArea);
					List<ShiptoSiloEntity> silosInCurrentSalesArea = currentSalesArea.getShiptoSilos();
					if (silosInCurrentSalesArea == null) {
						continue;
					}
					localSiloAdminService.updateSelectedSilo(silosInCurrentSalesArea, selectedSilo);
					currentSalesArea.addShiptoSilos(silosInCurrentSalesArea);
					ShiptoSareaEntity currentShiptoSalesArea = currentSalesArea.getShiptoSarea();

					List<ShiptoSareaRestrEntity> restrictions = currentShiptoSalesArea.getRestrictions();
					localSiloAdminService.detachSiloFromRestrictions(selectedSilo, restrictions);
				}
			}
		}

		localSiloAdminService.updateSelectedSilo(shiptoSilos, selectedSilo);

		RfidobjectEntity selectedRfidObject = selectedSilo.getRfidobject();
		localSiloAdminService.detachRFIDTagsFromRfidObject(selectedRfidObject);
	}

	private static void addSiloMaterialDescription(DispokarteForm dispoForm,
			BestellungForm orderForm, SapMaterialDescEntity description) {
		Long sapMaterialId = description.getSapMaterialId();
		if (dispoForm != null) {
			Map<Long, SapMaterialDescEntity> siloMaterialDescriptions = dispoForm.getSiloMaterialDescriptionMap();
			siloMaterialDescriptions.put(sapMaterialId, description);
		}
		else if (orderForm != null) {
			Map<Long, SapMaterialDescEntity> siloMaterialDescriptions = orderForm.getSiloMaterialDescriptionMap();
			siloMaterialDescriptions.put(sapMaterialId, description);
		}

	}

	private static void addSilo(DispokarteForm dispoForm, ShiptoEntity shipto,
			ShiptoSiloEntity helpFormsSilo) {
		List<ShiptoSiloEntity> silos = shipto.getSilos();
		Long dummySiloIdentifier = localSiloAdminService.getDummySiloIdentifier(silos);
		helpFormsSilo.setShiptoSiloId(dummySiloIdentifier);
		helpFormsSilo.setShipto(shipto);
		silos.add(helpFormsSilo);
		if (dispoForm != null) {
			List<SareaEntity> salesAreas = dispoForm.getSalesAreas();
			if (salesAreas != null) {
				for (SareaEntity salesArea : salesAreas) {
					SalesAreaBean salesAreaOfDispoForm = dispoForm.getSalesAreaBean(salesArea);
					List<ShiptoSiloEntity> shiptoSilos = salesAreaOfDispoForm.getShiptoSilos();
					shiptoSilos.add(helpFormsSilo);
					salesAreaOfDispoForm.addShiptoSilos(shiptoSilos);
				}
			}
		}
	}
}
