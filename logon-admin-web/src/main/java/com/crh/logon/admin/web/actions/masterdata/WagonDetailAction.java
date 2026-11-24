/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.actions.masterdata;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.admin.web.beans.MaintenanceBean;
import com.crh.logon.admin.web.beans.WagonDetailBuildBean;
import com.crh.logon.admin.web.beans.WagonDetailControlBean;
import com.crh.logon.admin.web.beans.WagonDetailLoadBean;
import com.crh.logon.admin.web.beans.WagonDetailMainBean;
import com.crh.logon.admin.web.beans.WagonDetailMaintenanceBean;
import com.crh.logon.admin.web.beans.WagonDetailTypeBean;
import com.crh.logon.admin.web.beans.WagonDetailWheelsetBean;
import com.crh.logon.admin.web.beans.WagonUploadAttachmentBean;
import com.crh.logon.admin.web.forms.AdminRFIDPopupForm;
import com.crh.logon.admin.web.forms.masterdata.WagonDetailForm;
import com.crh.logon.business.services.interfaces.AssignmentService;
import com.crh.logon.business.services.interfaces.WagonDetailService;
import com.crh.logon.business.services.utils.MaintenanceUtil;
import com.crh.logon.business.services.utils.RFIDBean;
import com.crh.logon.business.services.utils.RFIDBean.AbstractRFIDBean;
import com.crh.logon.constants.Constant;
import com.crh.logon.converters.BigDecimalConverter;
import com.crh.logon.converters.BooleanConverter;
import com.crh.logon.converters.LongConverter;
import com.crh.logon.persistence.entities.common.MaintRhythmDependEntity;
import com.crh.logon.persistence.entities.common.MaintRhythmEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.WagonDmgcodeTypeEntity;
import com.crh.logon.persistence.entities.common.WagonEntity;
import com.crh.logon.persistence.entities.common.WagonMaintEntity;
import com.crh.logon.persistence.entities.common.WagonRepAttEntity;
import com.crh.logon.persistence.entities.refdata.DamagecodeBsEntity;
import com.crh.logon.persistence.entities.refdata.DamagecodeTxEntity;
import com.crh.logon.persistence.entities.refdata.MaintRhythmTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.MaintTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderRepAttTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.WagonTypeBsEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.forms.RFIDPopupForm;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="admin_WagonDetailForm" path="/WagonDetail" scope="session"
 *  validate="true" input="WagonDetailTile.def"
 *
 * @struts.action-forward name="loadLoadTab" path="WagonDetailLoadTab.def"
 * @struts.action-forward name="loadMapTab" path="WagonDetailMapTab.def"
 * @struts.action-forward name="loadControlTab" path="WagonDetailControlTab.def"
 * @struts.action-forward name="loadTypeTab" path="WagonDetailTypeTab.def"
 * @struts.action-forward name="loadBuildTab" path="WagonDetailBuildTab.def"
 * @struts.action-forward name="loadWheelsetTab" path="WagonDetailWheelsetTab.def"
 * @struts.action-forward name="loadMaintenanceTab" path="WagonDetailMaintenanceTab.def"
 * @struts.action-forward name="loadMaintenanceDmgCodeTile" path="WagonMaintenanceDmgCodeTile.def"
 * @struts.action-forward name="loadMaintTabAttachmentDiv" path="WagonMaintenanceDmgAttachmentTile.def"
 * @struts.action-forward name="loadWagonKeeperInfo" path="WagonDetailWagonKeeperInfo.def"
 * @struts.action-forward name="loadWagonTypeInfo" path="WagonDetailWagonTypeInfo.def"
 * @struts.action-forward name="loadRfidTab" path="WagonDetailRfidTab.def"
 * @struts.action-forward name="copypopup" path="WagonDetailTileCopy.def"
 * @struts.action-forward name="loadMaintenanceTable" path="WagonMaintenanceTable.def"
 * @struts.action-forward name="reset" path="/WagonDetailEntry.do"
 */
public class WagonDetailAction extends AbstractWagonDetailAction {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WagonDetailAction.class);
	public static final String FORM_ACTION_LOAD_TAB = "loadTab";
	public static final String FORWARD_LOAD_TAB = "loadLoadTab";
	public static final String FORWARD_MAP_TAB = "loadMapTab";
	public static final String FORWARD_CONTROL_TAB = "loadControlTab";
	public static final String FORWARD_TYPE_TAB = "loadTypeTab";
	public static final String FORWARD_BUILD_TAB = "loadBuildTab";
	public static final String FORWARD_WHEELSET_TAB = "loadWheelsetTab";
	public static final String FORWARD_MAINTENANCE_TAB = "loadMaintenanceTab";
	public static final String FORWARD_MAINTENANCE_TABLE = "loadMaintenanceTable";
	public static final String FORWARD_MAINTENANCE_DMG_CODE_TILE = "loadMaintenanceDmgCodeTile";
	public static final String FORM_ACTION_LOAD_INFO = "loadInfo";
	public static final String FORWARD_WAGON_KEEPER_INFO = "loadWagonKeeperInfo";
	public static final String FORWARD_WAGON_TYPE_INFO = "loadWagonTypeInfo";
	public static final String FORWARD_RFID_TAB = "loadRfidTab";
	public static final String FORWARD_SHOW_CREATE_SPECIAL_ORDER_POPUP = "showCreateSpecialOrderPopup";
	public static final String FORWARD_RESET = "reset";
	public static final String FORM_ACTION_SAVE = "save";
	public static final String FORM_ACTION_SAVE_NEW = "saveNew";
	public static final String FORM_ACTION_COPY = "copypopup";
	public static final String FORWARD_MAINT_TAB_ATTACHMENT_DIV = "loadMaintTabAttachmentDiv";
	public static final String FORM_ACTION_REPORT_AS_EMPTY = "emptyWagon";
	public static final String FORM_ACTION_DEALLOCATE_AND_DELETE_ORDER = "deallocateAndDeleteOrder";
	public static final String FORM_ACTION_DEALLOCATE_ORDER = "deallocateOrder";
	public static final String FORM_ACTION_HOLD_BACK_RETURN_ORDER = "holdBackRepairOrder";
	public static final String FORM_ACTION_ADD_DAMAGECODE = "addDamageCode";
	public static final String FORM_ACTION_REMOVE_DAMAGECODE = "removeDamageCode";
	public static final String FORM_ACTION_UPLOAD_WAGON_REP_ATT_FILE = "uploadWagonRepAttFile";
	public static final String FORM_ACTION_MAINT_TAB_RELOAD_ATTACHMENT_DIV = "maintTabReloadAttachmentDiv";
	public static final String FORM_ACTION_REMOVE_WAGON_REP_ATT_FILE = "removeWagonRepAttFile";
	
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
				
				Authorization.authorize(Authorization.RAILCAR_DETAIL_BASE_R);
				WagonDetailForm helpForm;
				String forwardName = null;
				// Get the struts form.
				helpForm = (WagonDetailForm)form;
				
				// Check if cancel button clicked.
				if (isCancelled(request)) {
					helpForm.clear();
				}
				else if (request.getParameter("lastDateChanged") != null) {
					Long maintRhythmTypeCd = Long.valueOf(helpForm.getWagonDetailMaintenance().getSelectedMaintRhythmTypeCd());
					Map rhythmMap = getServiceLocator().getWagonDetailService().getMaintRhythmMap(maintRhythmTypeCd);
					
					calculateAffectedDates(rhythmMap, request.getParameter("lastDateChanged"), helpForm, request.getLocale());
					forwardName = FORWARD_MAINTENANCE_TABLE;
				}
				else if (request.getParameter("recalculateDates") != null) {
					Long maintRhythmTypeCd = Long.valueOf(helpForm.getWagonDetailMaintenance().getSelectedMaintRhythmTypeCd());
					Map rhythmMap = getServiceLocator().getWagonDetailService().getMaintRhythmMap(maintRhythmTypeCd);
					
					recalculateMaintenanceDates(rhythmMap, helpForm, request.getLocale(), null);
					forwardName = FORWARD_MAINTENANCE_TABLE;
				}
				else if (FORM_ACTION_LOAD_TAB.equals(helpForm.getFormAction())) {
					helpForm.validateTab(helpForm.getActiveTab(), mapping, request);
					forwardName = helpForm.getSelectedTab();
				}
				else if (request.getParameter("formAction") != null && "reset".equals(request.getParameter("formAction"))) {
					helpForm.setResetToTab(helpForm.getSelectedTab());
					forwardName = FORWARD_RESET;
				}
				else if (FORM_ACTION_LOAD_INFO.equals(helpForm.getFormAction())) {
					WagonDetailService wagonDetailService = getServiceLocator().getWagonDetailService();
					if (helpForm.getWagonDetailType() != null) {
						List wagonTypes = helpForm.getWagonDetailType().getWagonTypeBsList();
						WagonTypeBsEntity wagonType = (WagonTypeBsEntity) wagonTypes.get(wagonTypes.indexOf(helpForm.getWagonDetailType().getWagonType()));
						//all type dependent fields must be refreshed
						try {
							BeanUtils.copyProperties(helpForm.getWagonDetailType(), wagonType);
						} catch (Exception e) {
							throw new LogonException(e);
						}
						initWagonTypeData(request.getLocale(), wagonDetailService, helpForm.getWagonDetailMain(),helpForm.getWagonDetailType(),
								wagonType);
					}
					forwardName = helpForm.getRequestedInfo(); 
				}
				else if (FORM_ACTION_REPORT_AS_EMPTY.equals(helpForm.getFormAction())){
					Authorization.authorize(Authorization.RAILCARS_FREIMELDEN);
					Long orderId = helpForm.getOrderBean().getOrderId();
					AssignmentService assignmentService = getServiceLocator().getAssignmentService();
					Long assignmentId = assignmentService.findActiveAssignmentIdForOrderId(orderId);
					getServiceLocator().getDispoService().setRailUnloaded(assignmentId);
					forwardName = FORWARD_RESET;
					result = mapping.findForward(forwardName);
					return result;
				}
				else if (FORM_ACTION_DEALLOCATE_AND_DELETE_ORDER.equals(helpForm.getFormAction())){
					Authorization.authorize(Authorization.ORDER_DETAIL_W);
					String reason = request.getParameter("reason");
					getOrderService().deleteOrder(helpForm.getOrderBean().getOrderId(), reason);
					forwardName = FORWARD_RESET;
					result = mapping.findForward(forwardName);
					return result;
				}
				else if (FORM_ACTION_DEALLOCATE_ORDER.equals(helpForm.getFormAction())){
					Authorization.authorize(Authorization.RAIL_DISPATCHING_SW);
					Long orderId = helpForm.getOrderBean().getOrderId();
					AssignmentService assignmentService = getServiceLocator().getAssignmentService();
					Long assignmentId = assignmentService.findActiveAssignmentIdForOrderId(orderId);
					getServiceLocator().getDispoService().cancelAssignment(assignmentId, null, null, true);
					forwardName = FORWARD_RESET;
					result = mapping.findForward(forwardName);
					return result;
				}
				else if (FORM_ACTION_HOLD_BACK_RETURN_ORDER.equals(helpForm.getFormAction())){
					Authorization.authorize(Authorization.ORDER_DETAIL_W);
					Boolean holdBackReOrderFlag = helpForm.getOrderBean().getRailHoldBackReOrderFlag();
					getOrderService().setOrderRailHoldBackReOrderFlag(helpForm.getOrderBean().getOrderId(), holdBackReOrderFlag);
					forwardName = FORWARD_RESET;
					result = mapping.findForward(forwardName);
					return result;
				}				
				else if (FORM_ACTION_COPY.equals(helpForm.getFormAction())) {
					//redirects to the popup where it is possible to give the wagon No
					helpForm.setInputWagonNo("");
					result = mapping.findForward(FORM_ACTION_COPY);
					return result;
				}
				else if (FORM_ACTION_SAVE.equals(helpForm.getFormAction()) || FORM_ACTION_SAVE_NEW.equals(helpForm.getFormAction())) {
					Authorization.authorize(Authorization.RAILCAR_DETAIL_BASE_W);
				
					WagonDetailService wagonDetailService = getServiceLocator().getWagonDetailService();
					WagonEntity wagon;

					if (FORM_ACTION_SAVE.equals(helpForm.getFormAction()) && helpForm.getWagonId() != null) {
						wagon = wagonDetailService.getWagon(helpForm.getWagonId());
						if (wagon == null) {
							LOG.error("No wagon found with ID " + helpForm.getWagonId());
							throw new LogonException();
						} else {
							wagon.setRfidobject(helpForm.getRfidObject());
						}
					}
					else if(FORM_ACTION_SAVE_NEW.equals(helpForm.getFormAction())){
						LOG.debug("CREATING NEW WAGON");
				
						wagon = new WagonEntity();
						helpForm.getWagonDetailMain().setWagonNo(helpForm.getInputWagonNo());
						helpForm.getWagonDetailControl().setWagonNo(helpForm.getInputWagonNo());
				
						RfidobjectEntity rfObj = new RfidobjectEntity();
						rfObj.setRfidobjectType(getRefDataGenericsService().findBsEntity(RfidobjectTypeBsEntity.class, Constant.RFIDOBJECT_TYPE_WAGON));
						wagon.setRfidobject(rfObj);
				
						helpForm.getWagonDetailBuild().resetNewWagonProperties();
						helpForm.setWagonDetailWheelset(new WagonDetailWheelsetBean());
						helpForm.getWagonDetailMain().resetNewWagonProperties();
						helpForm.getWagonDetailControl().resetNewWagonProperties();
						helpForm.getOrderBean().resetNewWagonProperties();
						helpForm.getWagonDetailMaintenance().clear();
					}
					else{
						LOG.error("Error when saving wagon. Action: " + helpForm.getFormAction());
						result = mapping.getInputForward();
						return result;
					}

					ActionMessages errorWarnings = helpForm.validateAll(mapping, request);
					if ( (errorWarnings == null) || (errorWarnings.isEmpty()) ) {
						request.setAttribute("reload", "reload");
						WagonDetailMainBean wagonDetailMain = helpForm.getWagonDetailMain();
						WagonDetailLoadBean wagonDetailLoad = helpForm.getWagonDetailLoad();
						WagonDetailControlBean wagonDetailControl = helpForm.getWagonDetailControl();
						WagonDetailTypeBean wagonDetailType = helpForm.getWagonDetailType();
						WagonDetailBuildBean wagonDetailBuild = helpForm.getWagonDetailBuild();
						WagonDetailWheelsetBean wagonDetailWheelset = helpForm.getWagonDetailWheelset();
						WagonDetailMaintenanceBean wagonDetailMaintenance = helpForm.getWagonDetailMaintenance();
				
						// copy default properties
						BeanUtilsBean beanUtilsBean = new BeanUtilsBean();
						ConvertUtilsBean convertUtilsBean = beanUtilsBean.getConvertUtils();
						convertUtilsBean.register(new BigDecimalConverter(), BigDecimal.class);
						convertUtilsBean.register(new LongConverter(), Long.class);
						convertUtilsBean.register(new BooleanConverter(), Boolean.class);
				
						// copy default properties
						try {
							beanUtilsBean.copyProperties(wagon, wagonDetailLoad);
							beanUtilsBean.copyProperties(wagon, wagonDetailControl);
							beanUtilsBean.copyProperties(wagon, wagonDetailType);
							beanUtilsBean.copyProperties(wagon, wagonDetailBuild);
							beanUtilsBean.copyProperties(wagon, wagonDetailMain);
							beanUtilsBean.copyProperties(wagon, wagonDetailWheelset);
							beanUtilsBean.copyProperties(wagon, wagonDetailMaintenance);
						}
						catch (Exception e) {
							throw new LogonException(e);
						} 
						
						// handle damage codes and attachments. There is no transition for a copy
						if (FORM_ACTION_SAVE.equals(helpForm.getFormAction())) {
							wagon.setDamageCdList(helpForm.getWagonDetailMaintenance().getWagonDmgEntityList());
							wagon.setWagonRepAttachments(helpForm.getWagonDetailMaintenance().getEmailAttachments());
						} else {
							List newWagonDmgEntityList = new ArrayList();
							List newWagonRepAttachmentList = new ArrayList();
							wagon.setDamageCdList(newWagonDmgEntityList);
							wagon.setWagonRepAttachments(newWagonRepAttachmentList);
						}

						if (Boolean.TRUE.equals(wagonDetailControl.getActiveFlag())) {
							wagon.setWagonInactiveReason(null);
						}
						if (Boolean.TRUE.equals(wagonDetailControl.getFromDefaultLimitCreditNoteT())) {
							wagon.setLimitCreditNoteT(null);
						}
						if (Boolean.TRUE.equals(wagonDetailControl.getFromDefaultMaxLoadT())) {
							wagon.setMaxGrossWeightT(null);
						}
						if (Boolean.TRUE.equals(wagonDetailControl.getFromDefaultVolumeM3())) {
							wagon.setVolumeM3(null);
						}
						StringBuilder sb = new StringBuilder();
						String wagonNo = wagonDetailControl.getWagonNo();
						for (int i=0;i<wagonNo.length();i++) {
							if (Character.isDigit(wagonNo.charAt(i))) {
								sb.append(wagonNo.charAt(i));
							}
						}
						if (sb.length() != WagonEntity.DBSIZE_WAGON_NO) {
							throw new LogonException("An error occurred parsing wagonNo " + wagonNo, Reason.RES_ERROR_IMPL);
						}
						wagon.setWagonNo(sb.toString());
				
						wagon.setLastKnownRailwayStation(wagonDetailMain.getLastKnownRailwayStation());
						if (wagon.getLastKnownRailwayStation() == null) {
							wagon.setCurrentPlantTrack(null);
						}
						else{
							wagon.setCurrentPlantTrack(wagonDetailMain.getCurrentPlantTrackBs());
						}
						if(wagon.getCurrentPlantTrack() == null) {
							wagon.setCurrentTrackPositionNo(null);
						}
						wagon.setArrivalLastKnownStationDat(wagonDetailMain.getArrivalLastKnownStationDat());
						wagon.setReturnShiptoSapBp(wagonDetailControl.getReturnShiptoSapBp());
						
						if ( StringUtils.notEmpty(helpForm.getWagonDetailMaintenance().getSelectedMaintRhythmTypeCd()) ) {
							wagon.setMaintRhythmType( getServiceLocator().getRefDataGenericsService().findBsEntity(MaintRhythmTypeBsEntity.class,
									Long.valueOf(helpForm.getWagonDetailMaintenance().getSelectedMaintRhythmTypeCd())) );
						}
						if ( wagon.getMaintRhythmType() == null) {
							wagon.setMaintRhythmType(getServiceLocator().getRefDataGenericsService().findBsEntity(MaintRhythmTypeBsEntity.class, Long.valueOf(0)));
						}
						
						wagon = wagonDetailService.storeWagon(wagon);
						
						if (helpForm.getWagonDetailMaintenance().getWagonMaintMap() != null) {
							Map wagonMaintMap = helpForm.getWagonDetailMaintenance().getWagonMaintMap();
							Set keySet = wagonMaintMap.keySet();
							Iterator keyIt = keySet.iterator();

							List<WagonMaintEntity> wagonMaintList = new ArrayList<>();
							while (keyIt.hasNext()) {
								MaintenanceBean mb = (MaintenanceBean) wagonMaintMap.get(keyIt.next());
								mb.getWagonMaintEntity().setWagon(wagon);
								wagonMaintList.add(mb.getWagonMaintEntity());
							}
							List updatedWagonMaintList = wagonDetailService.storeWagonMaintList(wagonMaintList);
							Map maintTypeTxMap = helpForm.getWagonDetailMaintenance().getMaintTypeTxMap();
							for (Object o : updatedWagonMaintList) {
								WagonMaintEntity wm = (WagonMaintEntity) o;
								MaintenanceBean mb = new MaintenanceBean(
										wm,
										wm.getMaintType().getMaintTypeCd(),
										(MaintTypeTxEntity) maintTypeTxMap.get(wm.getMaintType().getMaintTypeCd()),
										getUserTimezone());
								helpForm.getWagonDetailMaintenance().getWagonMaintMap().put(mb.getMaintTypeCd(), mb);
							}
							
							List wagonRepAttachments = wagon.getWagonRepAttachments();
							helpForm.getWagonDetailMaintenance().setEmailAttachments(wagonRepAttachments);
						}
						try {
							LOG.debug("result: " + BeanUtils.describe(wagon));
						} catch (Exception e) {
							LOG.debug("Couldn't describe wagon", e);
						}
						AdminRFIDPopupForm.resetForm(request.getSession());
						helpForm.setRfidObject(wagon.getRfidobject());
						AbstractRFIDBean bean = RFIDBean.WagonRFIDBean(wagon);
						RFIDPopupForm.initForm(request.getSession(),bean);
						MessageUtils.addActionMessage(request, "wagondetail.save_successful");
						initWagonDetailForm(helpForm, wagon, request.getLocale());
						initAssignmentDependentData(request, helpForm, wagonDetailService, wagon);
					}
					if (FORM_ACTION_SAVE_NEW.equals(helpForm.getFormAction())){
						forwardName = FORM_ACTION_COPY;
					} 
				} 
				else if (FORM_ACTION_ADD_DAMAGECODE.equals(helpForm.getFormAction())){
					WagonDetailService wagonDetailService = getServiceLocator().getWagonDetailService();
					WagonEntity wagon = wagonDetailService.getWagon(helpForm.getWagonId());
					Long dCd = helpForm.getWagonDetailMaintenance().getDamageCode();
					DamagecodeBsEntity bs = getRefDataGenericsService().findBsEntity(DamagecodeBsEntity.class, dCd);
					WagonDmgcodeTypeEntity damageCode = new WagonDmgcodeTypeEntity();
					damageCode.setDmgcodeType(bs);
					damageCode.setWagon(wagon);
					damageCode.setCtlActCd(Boolean.TRUE);
					helpForm.getWagonDetailMaintenance().getWagonDmgEntityList().add(damageCode);
					DamagecodeTxEntity txEnt = getRefDataGenericsService().findTxEntity(DamagecodeTxEntity.class, request.getLocale(), bs);
					helpForm.getWagonDetailMaintenance().getDamageCodeTxList().add(txEnt);
					forwardName = FORWARD_MAINTENANCE_DMG_CODE_TILE;
				} 
				else if (FORM_ACTION_REMOVE_DAMAGECODE.equals(helpForm.getFormAction())){
					Long dCd = helpForm.getWagonDetailMaintenance().getDamageCode();
					Iterator it = helpForm.getWagonDetailMaintenance().getWagonDmgEntityList().iterator();
					while (it.hasNext()) {
						WagonDmgcodeTypeEntity dmg = (WagonDmgcodeTypeEntity) it.next();
						if (dmg.getDmgcodeType().getDamagecodeCd().equals(dCd)) {
							it.remove();
							break;
						}
					}
					it = helpForm.getWagonDetailMaintenance().getDamageCodeTxList().iterator();
					while (it.hasNext()) {
						DamagecodeTxEntity dmg = (DamagecodeTxEntity) it.next();
						if (dmg.getDamagecodeTxPK().getDamagecodeBs().getDamagecodeCd().equals(dCd)) {
							it.remove();
							break;
						}
					}				
					forwardName = FORWARD_MAINTENANCE_DMG_CODE_TILE;
				}
				else if (FORM_ACTION_UPLOAD_WAGON_REP_ATT_FILE.equals(helpForm.getFormAction())) {
					Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
					WagonEntity wagon = getServiceLocator().getWagonDetailService().getWagon(helpForm.getWagonId());
					if (logDebugEnabled) {
						LOG.debug("Action: upload button is pressed");
					}
					WagonUploadAttachmentBean imgForm = helpForm.getWagonDetailMaintenance().getUploadBean();
					
					WagonRepAttEntity att = uploadImage(request, wagon, imgForm);
				
					if (att != null){
						List list = helpForm.getWagonDetailMaintenance().getEmailAttachments();
						list.add(att);
					} else {
						helpForm.setLastUploadFailed(true);
						helpForm.setLastUnsupportedFileExtension(imgForm.getFile().getContentType());
					}
					// return to avoid extra call
					result = null;
					return result;
				}
				else if (FORM_ACTION_REMOVE_WAGON_REP_ATT_FILE.equals(helpForm.getFormAction())){
					Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
					WagonUploadAttachmentBean imgForm = helpForm.getWagonDetailMaintenance().getUploadBean();
					List list = helpForm.getWagonDetailMaintenance().getEmailAttachments();					
					Iterator it = list.iterator();
					while (it.hasNext()) {
						WagonRepAttEntity repEnt = (WagonRepAttEntity) it.next();
						if (repEnt.getAttachmentId().equals(imgForm.getWagonRepAttId())) {
							it.remove();
						}
					}
					forwardName = FORWARD_MAINT_TAB_ATTACHMENT_DIV;
				}
				else if (FORM_ACTION_MAINT_TAB_RELOAD_ATTACHMENT_DIV.equals(helpForm.getFormAction())){
					/*
					 * if there were some problems during file upload (unsupported mime type)
					 * The error should be displayed here due to the double call (see the
					 * callback function in WagonMaintenanceDmgTile.jsp
					 */
					if (Boolean.TRUE.equals(helpForm.isLastUploadFailed())) {
						helpForm.setLastUploadFailed(false);
						MessageUtils.addActionWarning(request,"useradmin.errors.mimeunsupported", helpForm.getLastUnsupportedFileExtension());
					}
					forwardName = FORWARD_MAINT_TAB_ATTACHMENT_DIV;
				}

				result = (forwardName != null) ? mapping.findForward(forwardName) : mapping.getInputForward();
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
	
	/**
	 * Call to upload an image hold by the imgForm and attach it to the wagon repair
	 * entity.
	 * In case an error happened, actionerror is added to the request.
	 */
	private WagonRepAttEntity uploadImage(HttpServletRequest request, WagonEntity wagon,
			WagonUploadAttachmentBean imgForm) {
		// [[
		WagonRepAttEntity result = null;
		Object[][] params = { { "request", request },
				{ "wagon", wagon },
				{ "imgForm", imgForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "uploadImage", params);
		}
		// ]]
		try {
			WagonRepAttEntity attachment = new WagonRepAttEntity();
			attachment.setWagon(wagon);
			FormFile file = imgForm.getFile();
			attachment.setAttachmentName(file.getFileName());
			try {
				MimeTypeBsEntity dataMimeType =
						getServiceLocator().getUserAdminService().fetchMimeTypeByMimeTypeName(file.getContentType());
				attachment.setDataMimeType(dataMimeType);
				attachment.setData(file.getFileData());
			}
			catch (Exception e) {
				LOG.warn("Unsupported mime type: " + file.getContentType(), e);
				return null;
			}
			attachment.setAttachmentName(file.getFileName());

			OrderRepAttTypeBsEntity attachmentType = getRefDataGenericsService()
					.findBsEntity(OrderRepAttTypeBsEntity.class, Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL);
			attachment.setAttachmentType(attachmentType);

			result = attachment;
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

	private void recalculateMaintenanceDates(Map<String, MaintRhythmEntity> rhythmMap, WagonDetailForm helpForm, Locale locale,
			String rootMaintType) {
		// [[
		Object[][] params = { { "rhythmMap", rhythmMap },
				{ "helpForm", helpForm },
				{ "locale", locale },
				{ "rootMaintType", rootMaintType } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "recalculateMaintenanceDates", params);
		}
		// ]]
		try {

			List<String> maintTypeList = helpForm.getWagonDetailMaintenance().getMaintTypeOrderList();
			for (String typeCd : maintTypeList) {
				if (needToRecalculateMaintDate(rhythmMap, rootMaintType, typeCd)) {
					calculateMaintenanceDate(rhythmMap, typeCd, helpForm);
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

	private boolean needToRecalculateMaintDate(Map<String, MaintRhythmEntity> rhythmMap, String rootMaintType, String typeCd) {
		// if rootMainType is defined then recalculate only those maintenance types which are dependent on the
		// rootMainType
		if (StringUtils.notEmpty(rootMaintType)) {
			if (!rhythmMap.containsKey(typeCd)) {
				// there is no rhythm rule for this maintenance type -> next date is undetermined
				return false;
			}
			MaintRhythmEntity mrEntity = rhythmMap.get(typeCd);

			final List<MaintRhythmDependEntity> dependencies = mrEntity.getDependencies();
			for (MaintRhythmDependEntity dependency : dependencies) {
				if (rootMaintType.equals(dependency.getDependMaintType().getMaintTypeCd())) {
					return true;
				}
			}

			return false;
		}
		return true;
	}

	private void calculateAffectedDates(Map<String, MaintRhythmEntity> rhythmMap, String maintCd, WagonDetailForm helpForm,
			Locale locale) {
		// [[
		Object[][] params = { { "rhythmMap", rhythmMap },
				{ "maintCd", maintCd },
				{ "helpForm", helpForm },
				{ "locale", locale } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "calculateAffectedDates", params);
		}
		// ]]
		try {
			// recalculate the next date for the changed maintenance date
			calculateMaintenanceDate(rhythmMap, maintCd, helpForm);

			// recalculate affected maintenance types
			recalculateMaintenanceDates(rhythmMap, helpForm, locale, maintCd);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void calculateMaintenanceDate(Map<String, MaintRhythmEntity> rhythmMap, String maintCd, WagonDetailForm helpForm) {
		// [[
		Object[][] params = { { "rhythmMap", rhythmMap },
				{ "maintCd", maintCd },
				{ "helpForm", helpForm } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "calculateMaintenanceDate", params);
		}
		// ]]
		try {
			WagonDetailMaintenanceBean wdmb = helpForm.getWagonDetailMaintenance();
			MaintRhythmEntity mrEntity = rhythmMap.get(maintCd);
			if (mrEntity == null) {
				// there is no rhythm rule for this maintenance type -> next date is undetermined
				return;
			}

			String dependMaintCd;
			Date nextMaintDate = null;
			Date calculatedNextMaintDate;

			MaintenanceBean mb = wdmb.getMaintenanceBean(maintCd);
			final List<MaintRhythmDependEntity> dependencies = mrEntity.getDependencies();

			for (MaintRhythmDependEntity dependency : dependencies) {
				dependMaintCd = dependency.getDependMaintType().getMaintTypeCd();
				calculatedNextMaintDate = MaintenanceUtil.getNextMaintDate(
						wdmb.getMaintenanceBean(dependMaintCd).getWagonMaintEntity().getLastDat(),
						rhythmMap.get(mb.getMaintTypeCd()));

				if (calculatedNextMaintDate != null &&
						(nextMaintDate == null || nextMaintDate.after(calculatedNextMaintDate))) {
					nextMaintDate = calculatedNextMaintDate;
				}
			}

			mb.getWagonMaintEntity().setNextDat(nextMaintDate);
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
