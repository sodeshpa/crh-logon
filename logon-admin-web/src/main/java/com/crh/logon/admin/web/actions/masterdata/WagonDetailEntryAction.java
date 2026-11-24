/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.admin.web.beans.WagonDetailBuildBean;
import com.crh.logon.admin.web.beans.WagonDetailTypeBean;
import com.crh.logon.admin.web.forms.AdminRFIDPopupForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.admin.web.beans.MaintenanceBean;
import com.crh.logon.admin.web.beans.WagonDetailControlBean;
import com.crh.logon.admin.web.beans.WagonDetailLoadBean;
import com.crh.logon.admin.web.beans.WagonDetailMainBean;
import com.crh.logon.admin.web.beans.WagonDetailWheelsetBean;
import com.crh.logon.admin.web.beans.WagonUploadAttachmentBean;
import com.crh.logon.admin.web.forms.masterdata.WagonDetailForm;
import com.crh.logon.admin.web.forms.masterdata.WagonSearchForm;
import com.crh.logon.business.services.interfaces.WagonDetailService;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.common.WagonMaintEntity;
import com.crh.logon.persistence.entities.refdata.MaintRhythmTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.MaintTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeBsEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_WagonDetailForm" path="/WagonDetailEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="WagonDetailTile.def"
 * @struts.action-forward name="loadLoadTab" path="WagonDetailLoadTab.def"
 * @struts.action-forward name="loadMapTab" path="WagonDetailMapTab.def"
 * @struts.action-forward name="loadControlTab" path="WagonDetailControlTab.def"
 * @struts.action-forward name="loadTypeTab" path="WagonDetailTypeTab.def"
 * @struts.action-forward name="loadBuildTab" path="WagonDetailBuildTab.def"
 * @struts.action-forward name="loadWheelsetTab" path="WagonDetailWheelsetTab.def"
 * @struts.action-forward name="loadMaintenanceTab" path="WagonDetailMaintenanceTab.def"
 */
public class WagonDetailEntryAction extends AbstractWagonDetailAction {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WagonDetailEntryAction.class);
	public static String FORM_ACTION_LOAD_TAB = "loadTab";
	public static String FORWARD_LOAD_TAB = "loadLoadTab";
	public static String FORWARD_MAP_TAB = "loadMapTab";
	public static String FORWARD_CONTROL_TAB = "loadControlTab";
	public static String FORWARD_TYPE_TAB = "loadTypeTab";
	public static String FORWARD_BUILD_TAB = "loadBuildTab";
	public static String FORWARD_WHEELSET_TAB = "loadWheelsetTab";
	public static String FORWARD_MAINTENANCE_TAB = "loadMaintenanceTab";
		
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
			AdminRFIDPopupForm.resetForm(request.getSession());
			Authorization.authorize(Authorization.RAILCAR_DETAIL_BASE_R);
			WagonDetailForm helpForm = (WagonDetailForm)form;
			String forwardName = "success";
			String wagonIdDesc = request.getParameter("wagonid");
			if ( StringUtils.empty(wagonIdDesc)) {
				wagonIdDesc = helpForm.getWagonId() == null ? null : helpForm.getWagonId().toString();
			}
			helpForm.clear();
			if ( !StringUtils.empty(helpForm.getResetToTab()) ) {
				helpForm.setSelectedTab(helpForm.getResetToTab());
				helpForm.setResetToTab(null);
				forwardName = helpForm.getSelectedTab();
			}
			WagonDetailService wagonDetailService = getServiceLocator().getWagonDetailService();
			if (StringUtils.notEmpty(wagonIdDesc)) {
				
				Long wagonId = Long.parseLong(wagonIdDesc);
				WagonEntity wagon = wagonDetailService.getWagon(wagonId);
				if (wagon == null) {
					LOG.error("Wagon with ID " + wagonId + " not found");
				}
				else {
					initWagonDetailForm(helpForm, wagon, request.getLocale());
					RfidobjectEntity rfObj = wagon.getRfidobject();
					if (rfObj == null) {
						rfObj = new RfidobjectEntity();
						rfObj.setRfidobjectType(getRefDataGenericsService().findBsEntity(RfidobjectTypeBsEntity.class, Constant.RFIDOBJECT_TYPE_WAGON));			
					}
					wagon.setRfidobject(rfObj);
					helpForm.setRfidObject(rfObj);
					AbstractRFIDBean bean = RFIDBean.WagonRFIDBean(wagon);
					AdminRFIDPopupForm.initForm(request.getSession(),bean);
					initAssignmentDependentData(request, helpForm, wagonDetailService, wagon);
				}
			}
			else {
				Authorization.authorize(Authorization.RAILCAR_DETAIL_BASE_W);
				helpForm.setWagonDetailMain(new WagonDetailMainBean());
				helpForm.setWagonDetailLoad(new WagonDetailLoadBean());
				WagonDetailControlBean wagonDetailControl = new WagonDetailControlBean();
				wagonDetailControl.setWagonKeeperList(wagonDetailService.getWagonKeeperList());
				wagonDetailControl.setShiptoSapBpList(wagonDetailService.getShiptoSapBpList());
				helpForm.setWagonDetailControl(wagonDetailControl);
				WagonDetailTypeBean wagonDetailType = new WagonDetailTypeBean();
				wagonDetailType.setWagonTypeBsList(getServiceLocator().getRefDataGenericsService().findBsEntityList(WagonTypeBsEntity.class));
				helpForm.setWagonDetailType(wagonDetailType);
				helpForm.setWagonDetailBuild(new WagonDetailBuildBean());
				helpForm.setWagonDetailWheelset(new WagonDetailWheelsetBean());
			}
			helpForm.getWagonDetailMaintenance().setMaintRhythmTypeList(getRefDataGenericsService().findTxEntityList(MaintRhythmTypeTxEntity.class, true, request.getLocale(), null, true));
			if ( request.getSession().getAttribute(WagonSearchForm.FORM_NAME) != null ) {
				WagonSearchForm searchForm = (WagonSearchForm) request.getSession().getAttribute(WagonSearchForm.FORM_NAME);
				helpForm.setWagonIdMap(searchForm.getWagonIdMap());
			}
			initMaintenanceTab(helpForm, wagonIdDesc, wagonDetailService, request); 
			
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
	
	private void initMaintenanceTab(WagonDetailForm helpForm, String wagonIdDesc, WagonDetailService wagonDetailService, HttpServletRequest request){ 
		// [[
		Object[][] params = {{"helpForm", helpForm},
							{"wagonIdDesc", wagonIdDesc},
							{"wagonDetailService", wagonDetailService},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initMaintenanceTab", params);
		}
		// ]]
		try {
			HashMap wagonMaintMap = new HashMap();
			
			List maintTypeTxList = getRefDataGenericsService().findTxEntityList(MaintTypeTxEntity.class, request.getLocale());
			helpForm.getWagonDetailMaintenance().setMaintTypeList(maintTypeTxList);
			HashMap maintTypeTxMap = new HashMap();
			ListIterator maintTypeIt = maintTypeTxList.listIterator();
			while ( maintTypeIt.hasNext() ) {
				MaintTypeTxEntity maintTypeTx = ((MaintTypeTxEntity) maintTypeIt.next());
				String key = maintTypeTx.getMaintTypeTxPK().getMaintTypeBs().getMaintTypeCd();
				maintTypeTxMap.put(key, maintTypeTx);
			}

			helpForm.getWagonDetailMaintenance().setMaintTypeTxMap(maintTypeTxMap);
			
			List wagonMaintList = StringUtils.empty(wagonIdDesc) ? null : wagonDetailService.findWagonMaintListByWagonId(Long.parseLong(wagonIdDesc));
			List repairShiptoList = wagonDetailService.fetchRepairShiptoList();
			
			List repairShiptoSapBpList = new ArrayList();
			
			ListIterator repairShiptoListIt = repairShiptoList.listIterator();
			
			while ( repairShiptoListIt.hasNext() ) {
				ShiptoSareaEntity shiptoSareaEntity = (ShiptoSareaEntity) repairShiptoListIt.next();
				ShiptoEntity shiptoEntity = shiptoSareaEntity.getShipto();
				if (shiptoEntity != null) {
					repairShiptoSapBpList.add(shiptoEntity.getShiptoSapBp());
				}
			}
			
			MaintenanceBean.setRepairShiptoList(repairShiptoSapBpList);
			helpForm.getWagonDetailMaintenance().setRepairShiptoList(repairShiptoSapBpList);
			
			// fill saved Maintenance Type info for this wagon
			if ( wagonMaintList != null ) {
				ListIterator wagonMaintIt = wagonMaintList.listIterator();
				while ( wagonMaintIt.hasNext() ) {
					WagonMaintEntity wm = (WagonMaintEntity) wagonMaintIt.next();
					MaintenanceBean mb = new MaintenanceBean(
							wm,
							wm.getMaintType().getMaintTypeCd(),
							(MaintTypeTxEntity) maintTypeTxMap.get(wm.getMaintType().getMaintTypeCd()),
							getUserTimezone());
					wagonMaintMap.put(wm.getMaintType().getMaintTypeCd(), mb);
				}
			}
			
			// create new maintenance type row for types which are not defined
			 
			maintTypeIt = helpForm.getWagonDetailMaintenance().getMaintTypeList().listIterator();
			
			while ( maintTypeIt.hasNext() ) {
				MaintTypeTxEntity maintTypeTx = ((MaintTypeTxEntity) maintTypeIt.next());
				String key = maintTypeTx.getMaintTypeTxPK().getMaintTypeBs().getMaintTypeCd();
				if ( !wagonMaintMap.containsKey(key)) {
					WagonMaintEntity newWagonMaint = new WagonMaintEntity();
					newWagonMaint.setActiveFlag(true);
					newWagonMaint.setMaintType( maintTypeTx.getMaintTypeTxPK().getMaintTypeBs() );
					MaintenanceBean mb = new MaintenanceBean(
							newWagonMaint,
							newWagonMaint.getMaintType().getMaintTypeCd(),
							maintTypeTx,
							getUserTimezone());
					wagonMaintMap.put(key, mb);
				}
			}
			
			helpForm.getWagonDetailMaintenance().setWagonMaintMap(wagonMaintMap);
			WagonEntity wagon = wagonDetailService.getWagon(helpForm.getWagonId());
			// set damage code tx list
			List dmgCodeList = getServiceLocator().getWagonDetailService().getDamageCodeTxList(request.getLocale(), wagon.getDamageCdList());
			helpForm.getWagonDetailMaintenance().setDamageCodeTxList(dmgCodeList);
			helpForm.getWagonDetailMaintenance().setWagonDmgEntityList(wagon.getDamageCdList());
			// set att attachment bean
			WagonUploadAttachmentBean uploadBean = new WagonUploadAttachmentBean();
			helpForm.getWagonDetailMaintenance().setUploadBean(uploadBean);
			helpForm.getWagonDetailMaintenance().setEmailAttachments(wagon.getWagonRepAttachments());
			// set wagon maintenance notes
			helpForm.getWagonDetailMaintenance().setWagonNoteMaintenance(wagon.getWagonNoteMaintenance());
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
