/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.bestellung;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.bestellung.BestellungSearchForm;
import com.crh.logon.order.web.forms.bestellung.MaterialPackageTypeBean;
import com.crh.logon.persistence.entities.refdata.DistributionDestTxEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageTxEntity;
import com.crh.logon.persistence.entities.refdata.OrderCreationTypeTxEntity;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_bestellung_BestellungSearchForm" path="/bestellung/BestellungMonitoringEntry" scope="session" validate="false"
 *
 * @struts.action-forward name="success" path="BestellungSearchTile.def"
 */
public class BestellungMonitoringEntryAction extends AbstractBestellungSearchEntryAction {
	
	Long[] STATUS_LIST_NEW = {	
			Constant.ORDER_STATUS_NEW
			};
	
	Long[] STATUS_LIST_POOL = {	
			Constant.ORDER_STATUS_TRANSMITTED_TO_POOL,
			Constant.ORDER_STATUS_TRANSMITTED_TO_POOL_ACK
			};
		
	Long[] STATUS_LIST_WORKFLOW = {
			Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER,
			Constant.ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK,			
			Constant.ORDER_STATUS_DISPATCHED,
			Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER,	
			Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK,	
			Constant.ORDER_STATUS_TRANSMITTED_TO_RAIL,			
			Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
			Constant.ORDER_STATUS_LOADING,
			Constant.ORDER_STATUS_LOADED,
			Constant.ORDER_STATUS_SHIPPING,
			Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
			Constant.ORDER_STATUS_UNLOADING
			};

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(BestellungMonitoringEntryAction.class);

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
			Authorization.authorize(Authorization.ORDER_VIEW_3_MONITORING);
			
			String forwardName;
			setSelectedTab("monitoring", request);
			setSelectedSub1Tab("monitoring", request);
			setSelectedSub2Tab("ordermonitoring", request);
			BestellungSearchForm searchForm = (BestellungSearchForm)form;
			
			String formAction = !StringUtils.isEmpty(searchForm.getFormAction()) ? searchForm.getFormAction() : "";
			if (ACTION_SAVE_PREFERENCES.equals(formAction)) {
				forwardName = handleSaveButtonClick(searchForm, Constant.USER_PREFERENCE_SCREEN_ORDER_SEARCH);
				
				addSuccessFullySavedMessageForAjaxResponse(request, response);
	    		
				return result = null;
			}
			else if (ACTION_RESET.equals(formAction)) {
				searchForm.clearDefaults();
				forwardName = "success";
				return result = mapping.findForward(forwardName);
			}
			
			List statusList = buildValidStatusSelectList(request.getLocale());
			searchForm.setOrderStatusList(statusList);
			
			List<OrderCreationTypeTxEntity> orderCreationTypeList = getRefDataGenericsService().findTxEntityList(OrderCreationTypeTxEntity.class, request.getLocale());
			searchForm.setOrderCreationTypeList(orderCreationTypeList);
			
			List<DistributionDestTxEntity> distributionDestCdList = getRefDataGenericsService().
					findTxEntityList(DistributionDestTxEntity.class, request.getLocale());
			searchForm.setDistributionDestCdList(distributionDestCdList);

			Collection<Object> packageTypes = new HashSet<Object>();
			packageTypes.add(Constant.MATERIAL_PACKAGE_BAG);
			packageTypes.add(Constant.MATERIAL_PACKAGE_BULK_POWDER);
			packageTypes.add(Constant.MATERIAL_PACKAGE_BULK_DUMPED);

			ArrayList<MaterialPackageTxEntity> materialPackageTxListAll = (ArrayList<MaterialPackageTxEntity>)getRefDataGenericsService().
					findTxEntityList(MaterialPackageTxEntity.class, false, request.getLocale(), MaterialPackageTxEntity.ATTR_TXT, true);
			
			ArrayList<MaterialPackageTypeBean> materialPackageTypeBeanList = new ArrayList<MaterialPackageTypeBean>();
			for (MaterialPackageTxEntity matPackageTx : materialPackageTxListAll) {
				String matPackageCd = matPackageTx.getMaterialPackageTxPK().getMaterialPackageBs().getMaterialPackageCd();
				if (packageTypes.contains(matPackageCd)) {
					MaterialPackageTypeBean materialPackageTypeBean = new MaterialPackageTypeBean();
					materialPackageTypeBean.setMaterialPackageCd(matPackageCd);
					materialPackageTypeBean.setMaterialPackageTxt(matPackageTx.getTxt());
					materialPackageTypeBeanList.add(materialPackageTypeBean);
				}
			}
			searchForm.setMaterialPackageTypeBean(materialPackageTypeBeanList);
			
			Map jsonData = getJsonData(request.getLocale());
			jsonData.put("orderStatusTxList", getJsonSerializer().serialize(getBestellungService().fetchOrderStatusTxList(request.getLocale())));
			searchForm.setJson(jsonData);
			
			populateForm(searchForm, request.getLocale(), null, statusList,
					Constant.USER_PREFERENCE_SCREEN_ORDER_SEARCH);
			
			forwardName = "success";
			
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
	 * @param refData
	 * @param locale
	 * @return
	 */
	private List buildValidStatusSelectList(Locale locale) {
		//visible anyway
		List requestStatuses = new ArrayList() ;
		requestStatuses.addAll(Arrays.asList(STATUS_LIST_WORKFLOW));
		
		if( Authorization.isAllowed(Authorization.ORDER_VIEW_3_MONITORING_AUFTRAGSANNAHME)) {			
			requestStatuses.addAll(Arrays.asList(STATUS_LIST_NEW));
		}

		if( Authorization.isAllowed(Authorization.ORDER_VIEW_3_MONITORING_FOR_POOL)) {			
			requestStatuses.addAll(Arrays.asList(STATUS_LIST_POOL));
		}
		
		return getBestellungService().getStatusList(requestStatuses, locale.getLanguage());
	}
	
}
