/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.raildispo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.dto.WagonPreLoadDTO;
import com.crh.logon.order.web.forms.raildispo.PreLoadWagonForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.DispoService;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.LdpointMatRestrEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantTrackBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.persistence.entities.views.SapMaterialPlantDescEntity;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * Handles the loading of the wagon preload popup.
 * 
 * @author AdNovum Informatik AG
 * 
 * @struts.action name="order_raildispo_PreLoadWagonForm" path="/raildispo/PreLoadWagonEntry"
 *                scope="session" validate="false"
 * 
 * @struts.action-forward name="success" path="PreLoadWagonPopupTile.def"
 * @struts.action-forward name="close" path="ClosePreLoadWagonPopupTile.def"
 */
public class PreLoadWagonEntryAction extends OrderActionSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PreLoadWagonEntryAction.class);

	private final String ACTION_FORWARD_SUCCESS = "success";

	private final String ACTION_FORWARD_CLOSE = "close";

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
			Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
			
			final PreLoadWagonForm preLoadWagonForm = (PreLoadWagonForm) form;
			
			String forwardName = ACTION_FORWARD_SUCCESS;
			
			Long wagonId = null;
			final String wagonIdString = request.getParameter("wagonId");
			try {
				wagonId = Long.parseLong(wagonIdString);
			}
			catch (NumberFormatException ex) {
				MessageUtils.addActionError(request,
						"raildispo.preloadpopup.message.missing.wagonid");
			
				forwardName = ACTION_FORWARD_CLOSE;
				return result = mapping.findForward(forwardName);
			}
			
			// Clear values
			preLoadWagonForm.init();
			
			// Initialize the first row (WagonPreLoadDTO).
			final WagonPreLoadDTO firstWagonPreLoadDTO = new WagonPreLoadDTO();
			final List<WagonPreLoadDTO> preLoadDTOs = preLoadWagonForm.getPreLoads();
			preLoadDTOs.add(firstWagonPreLoadDTO);
			
			final DispoService dispoService = getDispoService();
			
			final WagonEntity wagon = dispoService.fetchWagon(wagonId);
			preLoadWagonForm.setWagon(wagon);
			
			Boolean wagonTypeIsBag = Boolean.FALSE;
			Boolean wagonTypeIsBulk = Boolean.FALSE;
			
			final Long wagonTrspCatCd = dispoService.getWagonTrspCatCd(wagon);
			wagonTypeIsBag = wagonTrspCatCd == Constant.WAGON_TKATEGORIE_BAG;
			wagonTypeIsBulk = wagonTrspCatCd == Constant.WAGON_TKATEGORIE_SILO;
			
			preLoadWagonForm.setIsBag(wagonTypeIsBag);
			preLoadWagonForm.setIsBulk(wagonTypeIsBulk);
			
			final PlantTrackBsEntity plantTrackBs = wagon.getCurrentPlantTrack();
			final PlantBsEntity plant = plantTrackBs.getPlant();
			preLoadWagonForm.setPlant(plant);
			
			final LoadingpointEntity loadingpoint = dispoService.getLoadingpoint(wagon);
			preLoadWagonForm.setLoadingpoint(loadingpoint);
			
			if (loadingpoint == null) {
				LOG.error("Could not fetch loadingpoint for the wagonId: " + wagonId);
				
				MessageUtils.addActionError(request, 
						"raildispo.preloadpopup.message.missing.loadingpoint");
				forwardName = ACTION_FORWARD_CLOSE;
				
				return result = mapping.findForward(forwardName);
			}
			
			final Long loadingpointId = loadingpoint.getLoadingpointId();
			final List<LdpointMatRestrEntity> ldPointMatRestrList = dispoService.fetchLdPointMatRestrs(loadingpointId);
			
			if (ldPointMatRestrList.isEmpty()) {
				MessageUtils.addActionWarning(request,
						"raildispo.preloadpopup.message.missing.material");
				forwardName = ACTION_FORWARD_CLOSE;
			
				return result = mapping.findForward(forwardName);
			}
			
			final Locale locale = request.getLocale();
			
			final List<SapMaterialEntity> sapMaterialList = new ArrayList<SapMaterialEntity>();
			final List<Long> sapMaterialIds = new ArrayList<Long>();
			for (LdpointMatRestrEntity ldPointMatRestr : ldPointMatRestrList) {
				final SapMaterialEntity sapMaterialEntity = ldPointMatRestr.getSapMaterial();
				
				final Boolean sapMaterialIsBag = LogonPackageHandlingUtil.isBag(sapMaterialEntity);
				final Boolean sapMaterialIsBulk = LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(sapMaterialEntity);
				
				if ((wagonTypeIsBag && sapMaterialIsBag) || (wagonTypeIsBulk && sapMaterialIsBulk)) {
					sapMaterialList.add(sapMaterialEntity);
					final Long sapMaterialId = sapMaterialEntity.getSapMaterialId();
					sapMaterialIds.add(sapMaterialId);
				}
			}
			
			if (sapMaterialIds.isEmpty()) {
				MessageUtils.addActionWarning(request, 
						"raildispo.preloadpopup.message.nomaterialforwagon");
				forwardName = ACTION_FORWARD_CLOSE;
			
				return result = mapping.findForward(forwardName);
			}
			
			final List<SapMaterialDescEntity> sapMaterialDescEntities = dispoService.fetchSapMaterialDescEntities(
					sapMaterialIds, locale.getLanguage());
			preLoadWagonForm.setMaterials(sapMaterialDescEntities);
			
			// Build the material map - needed to quickly access SapMaterialEntity-s later on.
			final Map<Long, SapMaterialEntity> sapMaterialMap = new HashMap<Long, SapMaterialEntity>();
			for (SapMaterialEntity sapMaterial : sapMaterialList) {
				final Long sapMatId = sapMaterial.getSapMaterialId();
				sapMaterialMap.put(sapMatId, sapMaterial);
			}
			preLoadWagonForm.setSapMaterialMap(sapMaterialMap);
			
			// Fetch and build a map of SapMaterialPlantDescEntity-s by material.
			final List<SapMaterialPlantDescEntity> sapMatPlantDescList = dispoService.fetchSapMaterialPlantDescEntitiesByMaterialIds(
					sapMaterialIds, locale.getLanguage());
			
			final Map<Long, List<SapMaterialPlantDescEntity>> sapMatPlantDescMap = new HashMap<Long, List<SapMaterialPlantDescEntity>>();
			
			for (SapMaterialPlantDescEntity sapMaterialPlantDesc : sapMatPlantDescList) {
			
				final Long sapMaterialId = sapMaterialPlantDesc.getSapMaterialId();
			
				List<SapMaterialPlantDescEntity> actSapMatPlantDescList = sapMatPlantDescMap.get(sapMaterialId);
				if (actSapMatPlantDescList == null) {
					actSapMatPlantDescList = new ArrayList<SapMaterialPlantDescEntity>();
					sapMatPlantDescMap.put(sapMaterialId, actSapMatPlantDescList);
				}
				actSapMatPlantDescList.add(sapMaterialPlantDesc);
			
			}
			preLoadWagonForm.setSapMatPlantDescMap(sapMatPlantDescMap);
			
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
