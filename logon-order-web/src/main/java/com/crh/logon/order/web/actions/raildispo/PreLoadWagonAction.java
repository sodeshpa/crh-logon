/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.raildispo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import com.crh.logon.beans.WagonPreLoadBean;
import com.crh.logon.business.services.interfaces.DispoService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.interfaces.UomConverterService;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialAltUomEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.persistence.entities.views.SapMaterialPlantDescEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * Handles the actions of the wagon preload popup.
 * 
 * @author AdNovum Informatik AG
 * 
 * @struts.action name="order_raildispo_PreLoadWagonForm"
 *  path="/raildispo/PreLoadWagon" scope="session"
 *  validate="false" input="RailDispoTile.def"
 *
 * @struts.action-forward name="success" path="PreLoadWagonPopupTile.def"
 * @struts.action-forward name="saveClose" path="ClosePreLoadWagonPopupTile.def"
 */
public class PreLoadWagonAction extends OrderActionSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PreLoadWagonAction.class);

	private final String ACTION_FORWARD_SUCCESS = "success";
	private final String ACTION_FORWARD_SAVECLOSE = "saveClose";

	private final String PARAM_SAVECLOSE = "saveClose";
	private final String PARAM_SELECT_MATERIAL = "selectMaterial";
	private final String PARAM_ADD_OTHER_PRODUCT = "addOtherProduct";
	private final String PARAM_REMOVE_PRODUCT = "removeProduct";

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
			
			if (request.getParameter(PARAM_SAVECLOSE) != null) {
				forwardName = savePreLoads(request, preLoadWagonForm);
			}
			else if (request.getParameter(PARAM_SELECT_MATERIAL) != null) {
				forwardName = selectMaterial(request, preLoadWagonForm);
			}
			else if (request.getParameter(PARAM_ADD_OTHER_PRODUCT) != null) {
				forwardName = addOtherProduct(request, preLoadWagonForm);
			}
			else if (request.getParameter(PARAM_REMOVE_PRODUCT) != null) {
				forwardName = removeProduct(request, preLoadWagonForm);
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
	
	private String selectMaterial(HttpServletRequest request, PreLoadWagonForm preLoadWagonForm) {
		String forwardName = ACTION_FORWARD_SUCCESS;
		
		final Map<Long, SapMaterialEntity> sapMaterialMap = preLoadWagonForm.getSapMaterialMap();
		
		final List<WagonPreLoadDTO> preLoadDTOs = preLoadWagonForm.getPreLoads();
		for (WagonPreLoadDTO wagonPreLoadDTO : preLoadDTOs) {
			final Long sapMatId = wagonPreLoadDTO.getSapMaterialId();
			final SapMaterialEntity sapMaterial = sapMaterialMap.get(sapMatId);
			
			if (sapMaterial == null) {
				continue;
			}
			
			final Boolean isBag = LogonPackageHandlingUtil.isBag(sapMaterial);
			final Boolean isBulk = LogonPackageHandlingUtil.isBulkDumpOrBulkPowder(sapMaterial);
			
			setTareWeightIfBag(wagonPreLoadDTO, preLoadWagonForm);
			
			if (isBag) {
				// Load alt UoM-s
				final UomConverterService uomConverterService = getServiceLocator().getUomConverterService();
				final List<SapMaterialAltUomEntity> sapMaterialAltUoms = 
						uomConverterService.fetchAltUom(sapMaterial);
				
				wagonPreLoadDTO.setSapMaterialAltUoms(sapMaterialAltUoms);
			}
			else if (isBulk) {
				// Load ton UoM
				if (preLoadWagonForm.getTonUomTx() == null) {
					final Locale locale = request.getLocale();
					final RefDataGenericsService refDataGenericsService = getRefDataGenericsService();
					final UomTxEntity tonUomTx = refDataGenericsService.
							findTxEntity(UomTxEntity.class, locale, Constant.UOM_TONS);
					
					preLoadWagonForm.setTonUomTx(tonUomTx);
				}
			}
			else {
				LOG.error("Selected material is not bag or bulk. " + sapMatId);
				MessageUtils.addActionError(request, "raildispo.preload.message.material.type.unsupported");
				return forwardName;
			}
			
			// Populate prod plant list
			final Map<Long, List<SapMaterialPlantDescEntity>> sapMatPlantDescMap = 
					preLoadWagonForm.getSapMatPlantDescMap();
			final List<SapMaterialPlantDescEntity> plantsForMaterial = sapMatPlantDescMap.get(sapMatId);
			wagonPreLoadDTO.setProdPlants(plantsForMaterial);
		}
		
		return forwardName;
	}
	
	private String addOtherProduct(HttpServletRequest request, PreLoadWagonForm preLoadWagonForm) {
		String forwardName = ACTION_FORWARD_SUCCESS;
		
		final List<WagonPreLoadDTO> preLoadDTOs = preLoadWagonForm.getPreLoads();
		for (WagonPreLoadDTO wagonPreLoadDTO : preLoadDTOs) {
			final Long sapMatId = wagonPreLoadDTO.getSapMaterialId();
			
			if (sapMatId == null || sapMatId == 0) {
				MessageUtils.addActionWarning(request,
						"raildispo.preloadpopup.message.selectmaterial");

				return forwardName;
			}
		}
		
		
		final WagonPreLoadDTO newPreLoadDTO = new WagonPreLoadDTO();
		
		setTareWeightIfBag(newPreLoadDTO, preLoadWagonForm);
		
		preLoadDTOs.add(newPreLoadDTO);
		
		return forwardName;
	}
	
	private WagonPreLoadDTO setTareWeightIfBag(WagonPreLoadDTO wagonPreLoadDTO, PreLoadWagonForm preLoadWagonForm) {
		final Boolean isBag = preLoadWagonForm.getIsBag();
		if (isBag) {
			final WagonEntity wagon = preLoadWagonForm.getWagon();
			final BigDecimal wagonTareWeightT = wagon.getTareWeightT();
			wagonPreLoadDTO.setTareWeightT(wagonTareWeightT);
		}
		return wagonPreLoadDTO;
	}
	
	private String removeProduct(HttpServletRequest request, PreLoadWagonForm preLoadWagonForm) {
		String forwardName = ACTION_FORWARD_SUCCESS;
		
		final String indexParamString = request.getParameter("index");
		final Integer indexToRemove = Integer.parseInt(indexParamString);
		
		final List<WagonPreLoadDTO> preLoadDTOs = preLoadWagonForm.getPreLoads();
		Integer i = 0;
		for (Iterator<WagonPreLoadDTO> it = preLoadDTOs.iterator(); it.hasNext();) {
			it.next();
			
			if (i == indexToRemove) {
				it.remove();
				break;
			}
		
			i++;
		}
		
		return forwardName;
	}
	
	private String savePreLoads(HttpServletRequest request, PreLoadWagonForm preLoadWagonForm) {
		String forwardName = ACTION_FORWARD_SUCCESS;
		
		final WagonEntity wagon = preLoadWagonForm.getWagon();
		final LoadingpointEntity loadingpoint = preLoadWagonForm.getLoadingpoint();

		final Boolean isBag = preLoadWagonForm.getIsBag();
		final Boolean isBulk = preLoadWagonForm.getIsBulk();

		final List<WagonPreLoadDTO> preLoadDTOs = preLoadWagonForm.getPreLoads();

		final List<WagonPreLoadBean> preLoads = new ArrayList<WagonPreLoadBean>();

		// Track the added materials (sapMaterialId, how many added).
		final Map<Long, Long> materialCountMap = new HashMap<Long, Long>();

		for (WagonPreLoadDTO preLoadDTO : preLoadDTOs) {
			final Long sapMaterialId = preLoadDTO.getSapMaterialId();

			if (sapMaterialId == null || sapMaterialId == 0) {
				MessageUtils.addActionWarning(request,
						"raildispo.preload.message.missing.selected.material");

				return forwardName;
			}
			Long count = materialCountMap.get(sapMaterialId);
			if (count != null) {
				MessageUtils.addActionWarning(request,
						"raildispo.preloadpopup.message.material.added.morethanonce");

				return forwardName;
			}
			else {
				materialCountMap.put(sapMaterialId, 1L);
			}

			final String prodPlantCd = preLoadDTO.getProdPlantCd();
			if (StringUtils.empty(prodPlantCd)) {
				MessageUtils.addActionWarning(request,
						"raildispo.preloadpopup.message.missing.prodplant");

				return forwardName;
			}

			final BigDecimal tareWeightT = preLoadDTO.getTareWeightT();
			if (tareWeightT == null || tareWeightT.compareTo(BigDecimal.ZERO) < 1) {
				MessageUtils.addActionWarning(request,
						"raildispo.preloadpopup.message.missing.tareweight");

				return forwardName;
			}

			final BigDecimal grossWeightT = preLoadDTO.getGrossWeightT();
			final BigDecimal netQuantity = preLoadDTO.getNetQuantity();
			final String netQuantityUomCd = preLoadDTO.getNetQuantityUomCd();

			if (isBulk) {
				if (grossWeightT == null || grossWeightT.compareTo(BigDecimal.ZERO) < 1) {
					MessageUtils.addActionWarning(request,
							"raildispo.preloadpopup.message.missing.grossweight");

					return forwardName;
				}
				if (grossWeightT.compareTo(tareWeightT) < 1) {
					MessageUtils.addActionWarning(request,
							"raildispo.preloadpopup.message.grossweightmustbegreater");

					return forwardName;
				}
			}
			else if (isBag) {
				if (netQuantity == null || netQuantity.compareTo(BigDecimal.ZERO) < 1) {
					MessageUtils.addActionWarning(request,
							"raildispo.preloadpopup.message.missing.netqty");

					return forwardName;
				}
				if (StringUtils.empty(netQuantityUomCd)) {
					MessageUtils.addActionWarning(request,
							"raildispo.preloadpopup.message.missing.uom");

					return forwardName;
				}
			}

			WagonPreLoadBean wagonPreLoadBean = null;
			if (isBulk) {
				wagonPreLoadBean = new WagonPreLoadBean(sapMaterialId, prodPlantCd, tareWeightT,
						grossWeightT);
			}
			else if (isBag) {
				wagonPreLoadBean = new WagonPreLoadBean(sapMaterialId, prodPlantCd, tareWeightT,
						netQuantity, netQuantityUomCd);
			}

			preLoads.add(wagonPreLoadBean);
		}

		final DispoService dispoService = getDispoService();
		dispoService.preLoad(wagon, loadingpoint, preLoads);

		MessageUtils.addActionMessage(request, "raildispo.preload.message.successful");

		callReloadWagonsJs(request);

		forwardName = ACTION_FORWARD_SAVECLOSE;
		
		return forwardName;
	}
	
	/**
	 * Sets the callReloadWagonsJs = true attribute in the request,
	 * which indicates that the wagon list need to be refreshed on the screen. 
	 */
	private HttpServletRequest callReloadWagonsJs(HttpServletRequest request) {
		request.setAttribute("callReloadWagonsJs", true);
		return request;
	}
}
