/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.auftrag;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.auftrag.OrderSpecialForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.auftrag.OrderDetailUploadAttachmentBean;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.CarrierEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.OrderRepAttEntity;
import com.crh.logon.persistence.entities.common.OrderRepDmgcodeTypeEntity;
import com.crh.logon.persistence.entities.common.OrderRepEntity;
import com.crh.logon.persistence.entities.common.PoolCarrierEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.refdata.ArchivingStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.CurrencyBsEntity;
import com.crh.logon.persistence.entities.refdata.DamagecodeBsEntity;
import com.crh.logon.persistence.entities.refdata.DamagecodeTxEntity;
import com.crh.logon.persistence.entities.refdata.DeliveryTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.DistributionDestBsEntity;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderCreationTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderRepAttTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.OrderStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_auftrag_OrderSpecialForm"
 *  path="/auftrag/OrderSpecial" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="fetchfirst" path="/auftrag/OrderSpecialEntry.do"
 * @struts.action-forward name="success" path="OrderSpecialTile.def"
 * @struts.action-forward name="saveOrder" path="OrderSpecialSave.def"
 * @struts.action-forward name="closeOrder" path="OrderSpecialPopupClose.def"
 * @struts.action-forward name="loadDamageCodeTile" path="OrderSpecialDamageCodeTile.def"
 * @struts.action-forward name="loadAttachmentTile" path="OrderSpecialRepAttachmentTile.def"
 * @struts.action-forward name="emptyMaterialRender" path="OrderSpecialEmptyMaterialOption.def"  
 */
public class OrderSpecialAction extends OrderActionSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(OrderSpecialAction.class);
	private static final String FORWARD_SUCCESS = "success";
	public static final String FORWARD_SAVE_ORDER = "saveOrder";
	public static final String FORWARD_SAVE_TRANSMIT_ORDER = "saveOrderTransmit";
	public static final String FORWARD_CLOSE_ORDER = "closeOrder";
	public static final String FORM_ACTION_UPLOAD_REP_ORDER_ATT_FILE = "uploadRepOrderAtt";
	public static final String FORM_ACTION_REMOVE_REP_ORDER_ATT_FILE = "removeRepOrderAtt";
	public static final String FORM_ACTION_SHOW_ATTACHMENTS = "show";
	public static final String FORM_ACTION_ADD_DAMAGECODE = "addDamageCode";
	public static final String FORM_ACTION_REMOVE_DAMAGECODE = "removeDamageCode";
	public static final String FORM_ACTION_CHANGE_EMPTY_MATERIAL = "emptyMaterial";
	public static final String FORWARD_DAMAGECODE_TILE = "loadDamageCodeTile";
	public static final String FORWARD_ATTACHMENT_TILE = "loadAttachmentTile";
	public static final String FORWARD_EMPTY_MATERIAL_RENDER = "emptyMaterialRender";

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
			Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
			
			OrderSpecialForm helpForm = (OrderSpecialForm) form;
			
			String forwardName = FORWARD_SUCCESS;
			
			if ((FORWARD_SAVE_ORDER.equals(helpForm.getFormAction())) || (FORWARD_SAVE_TRANSMIT_ORDER.equals(helpForm.getFormAction()))) {
				Authorization.authorize(Authorization.ORDER_DETAIL_W);
				if (logDebugEnabled) {
					LOG.debug("Action: save order!");
				}
				forwardName = FORWARD_SAVE_ORDER;
				
				// form validation is called from here because we have popup screen!
				helpForm.validateOrderSpecial(request);
				
				if (((MessageUtils.getActionErrors(request) == null) || (MessageUtils.getActionErrors(request).size() == 0)) &&
						((MessageUtils.getActionWarnings(request) == null) || (MessageUtils.getActionWarnings(request).size() == 0))) {
			
					OrderEntity order = initOrder(request, helpForm);
					
					if ((MessageUtils.getActionWarnings(request) == null) || (MessageUtils.getActionWarnings(request).size() == 0)) { // no new warnings from initOrder
						
						DistributionDestBsEntity dDest = getRefDataGenericsService().findBsEntity(DistributionDestBsEntity.class, helpForm.getDistDestCd());
						order.setDistributionDest(dDest);
						OrderRepEntity orderRep = null;
						
						// only order of type repair, maintenance 
						if (Constant.DISTRIBUTION_DEST_REPAIR.equals(helpForm.getDistDestCd())) {
							orderRep = helpForm.getOrderRep();
						}
					
						AssignmentEntity ass = null;
						try {
							ass = getOrderService().createSpecialRailAssignment(helpForm.getWagon(), order, order.getDeliveryFromDat(), orderRep);
						}
						catch (Exception e) {
							MessageUtils.addActionError(request, "orderspecial.message.not.success.save");
							
							//reset
							helpForm.setFormAction(null);
							return result = mapping.findForward(FORWARD_SAVE_ORDER);
						}
						
						
						if ((ass != null) && (!ass.getAssignmentParts().isEmpty())) {
							
							if (logDebugEnabled) {
								LOG.debug("Assignment sucessfully created. Assignment id: " + ass.getAssignmentId());
							}
							
							if (FORWARD_SAVE_TRANSMIT_ORDER.equals(helpForm.getFormAction())) {
								getDispoService().transmitToRailCompany(ass.getAssignmentId());
								
							}
							MessageUtils.addActionMessage(request, "orderspecial.message.success.save");
							forwardName = FORWARD_CLOSE_ORDER;
						}
						else {
							MessageUtils.addActionError(request, "orderspecial.message.not.success.save");
						}
					}
				}
			
				
			}
			else if (FORWARD_CLOSE_ORDER.equals(helpForm.getFormAction())) {
				if (logDebugEnabled) {
					LOG.debug("Action: close order!");
				}
				forwardName = FORWARD_CLOSE_ORDER;
			}
			else if (FORM_ACTION_UPLOAD_REP_ORDER_ATT_FILE.equals(helpForm.getFormAction())) {
				Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
				OrderRepEntity orderRepEnt = helpForm.getOrderRep();
				if (logDebugEnabled) {
					LOG.debug("Action: upload button is pressed");
				}
				OrderDetailUploadAttachmentBean imgForm = helpForm.getUploadBean();
				
				OrderRepAttEntity uploadImage = uploadImage(request, orderRepEnt, imgForm);
				if (uploadImage == null) {
					helpForm.setLastUploadFailed(true);
					helpForm.setLastUnsupportedFileExtension(imgForm.getFile().getContentType());
				}
				forwardName = null;
			}
			else if (FORM_ACTION_REMOVE_REP_ORDER_ATT_FILE.equals(helpForm.getFormAction())){
				Authorization.authorize(Authorization.WAGON_SPECIAL_ORDER_CREATE_EDIT);
				OrderDetailUploadAttachmentBean imgForm = helpForm.getUploadBean();
				
				if (imgForm.getIndex() != null ) {					
					OrderRepEntity orderRepEnt = helpForm.getOrderRep();
					orderRepEnt.getOrderRepAttachments().remove(imgForm.getIndex().intValue());
				}
				forwardName = FORWARD_ATTACHMENT_TILE;
			}
			else if (FORM_ACTION_ADD_DAMAGECODE.equals(helpForm.getFormAction())){
				Authorization.authorize(Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS);
				Long dCd = helpForm.getDamageCode();
				DamagecodeBsEntity bs = getRefDataGenericsService().findBsEntity(DamagecodeBsEntity.class, dCd);
				OrderRepDmgcodeTypeEntity damageCode = new OrderRepDmgcodeTypeEntity();
				damageCode.setDmgcodeType(bs);
				damageCode.setOrderRep(helpForm.getOrderRep());
				damageCode.setCtlActCd(Boolean.TRUE);
				helpForm.getOrderRep().getDamageCdList().add(damageCode);
				List list = new ArrayList();
				list.add(damageCode);
				List list2 = getOrderDetailService().getDamageCodeTxList(helpForm.getLocale(), list);
				DamagecodeTxEntity txEnt = (DamagecodeTxEntity)list2.get(0);
				helpForm.getDamageCodeTxList().add(txEnt);
				forwardName = FORWARD_DAMAGECODE_TILE;
			}
			else if (FORM_ACTION_REMOVE_DAMAGECODE.equals(helpForm.getFormAction())){
				Authorization.authorize(Authorization.ORDER_DETAIL_REPAIR_MAINT_FULL_ACCESS);
				Long dCd = helpForm.getDamageCode();
				Iterator it = helpForm.getOrderRep().getDamageCdList().iterator();
				while (it.hasNext()) {
					OrderRepDmgcodeTypeEntity dmg = (OrderRepDmgcodeTypeEntity) it.next();
					if (dmg.getDmgcodeType().getDamagecodeCd().equals(dCd)) {
						it.remove();
						break;
					}
				}
				it = helpForm.getDamageCodeTxList().iterator();
				while (it.hasNext()) {
					DamagecodeTxEntity dmg = (DamagecodeTxEntity) it.next();
					if (dmg.getDamagecodeTxPK().getDamagecodeBs().getDamagecodeCd().equals(dCd)) {
						it.remove();
						break;
					}
				}				
				forwardName = FORWARD_DAMAGECODE_TILE;
			}
			else if (FORM_ACTION_SHOW_ATTACHMENTS.equals(helpForm.getFormAction())){
				/*
				 * if there were some problems during file upload (unsupported mime type)
				 * The error should be displayed here due to the double call (see the
				 * callback function in WagonMaintenanceDmgTile.jsp
				 */
				if (helpForm.isLastUploadFailed()) {
					helpForm.setLastUploadFailed(false);
					MessageUtils.addActionWarning(request,"useradmin.errors.mimeunsupported", helpForm.getLastUnsupportedFileExtension());
				}
				forwardName = FORWARD_ATTACHMENT_TILE;
			}
			else if (FORM_ACTION_CHANGE_EMPTY_MATERIAL.equals(helpForm.getFormAction())){
				forwardName = FORWARD_EMPTY_MATERIAL_RENDER;
			}
			//reset
			helpForm.setFormAction(null);
			return result = mapping.findForward(forwardName);
		}
		catch (Exception e) {
			MessageUtils.addActionError(request, "fatalerror.fatalError");
			MessageUtils.addActionError(request, "fatalerror.helpmsg");
			LOG.error("Fatal error", e);
			return result = mapping.findForward(FORWARD_SAVE_ORDER);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private OrderEntity initOrder(HttpServletRequest request, OrderSpecialForm helpForm) {
		// [[
		OrderEntity result = null;
		Object[][] params = {{"request", request},
							{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "initOrder", params);
		}
		// ]]
		try {
			OrderEntity order = helpForm.getOrder();
			OrderItemEntity orderItem = helpForm.getOrderItem();
			
			final ShippingpointEntity sp = getOrderSpecialService().fetchShippingpoint(helpForm.getShippingpointId());
			if (sp == null) {
				MessageUtils.addActionWarning(request, "orderspecial.warning.invalid.shippingpoint");
			}
			else {
				order.setShippingpoint(sp);
			}
			
			SapBpEntity shipto = getDispokarteService().getShiptoSapBpById(helpForm.getOsShiptoSapBpId());
			order.setShiptoSapBp(shipto);
			SapBpEntity soldto = getDispokarteService().getSoldtoSapBpById(helpForm.getOsSoldtoSapBpId());
			order.setSoldtoSapBp(soldto);
			SapBpEntity billto = getDispokarteService().getBilltoSapBpById(helpForm.getOsBilltoSapBpId());
			order.setBilltoSapBp(billto);
			SareaEntity sarea = getSalesAreaService().findSalesAreaByIdentifier(helpForm.getOsSareaId());
			order.setSarea(sarea);
			
			PoolCarrierEntity poolCarrier = getOrderSpecialService().getDefaultRailPoolCarrier(helpForm.getCarrierId());
			if ( poolCarrier == null ) {
				MessageUtils.addActionWarning(request, "orderspecial.warning.missing.or.more.pool.carrier.connection");	
			}
			else {
				order.setPool(poolCarrier.getPool());
			}
			final CarrierEntity carrier = getOrderSpecialService().getCarrier(helpForm.getCarrierId());
			order.setCommCarrier(carrier);
			order.setExecCarrier(carrier);
			
			//check timetable information
			if ((sp.getRailwayStation() != null) && (shipto.getRailwayStation() != null)) {
				List railTimetableList = getOrderSpecialService().fetchRailTimetableList(sp.getRailwayStation().getRailwayStationId(), shipto.getRailwayStation().getRailwayStationId());
				
				if ((railTimetableList == null) || (railTimetableList.isEmpty())){
					MessageUtils.addActionWarning(request, "orderspecial.warning.no,timetable");
				}
			}
			
			
			if (helpForm.getMaterialLoad() == OrderSpecialForm.SELECTION_MATERIAL_LOAD_EMPTY) {
				
				PlantBsEntity prodPlant = getRefDataGenericsService().findBsEntity(PlantBsEntity.class, helpForm.getProdPlantCd());
				orderItem.setProductionPlant(prodPlant);
				
				
				SapMaterialEntity mat = getOrderSpecialService().fetchSapMaterial(helpForm.getEmptyWagonMaterialId());
				orderItem.setSapMaterial(mat);
				
				orderItem.setOrderedQuantity(new BigDecimal("1"));
				orderItem.setOrderedUom(orderItem.getSapMaterial().getBaseUom());
			}
			else if (helpForm.getMaterialLoad() == OrderSpecialForm.SELECTION_MATERIAL_LOAD_ANY) {
				PlantBsEntity prodPlant = getRefDataGenericsService().findBsEntity(PlantBsEntity.class, helpForm.getProdPlantCd());
				orderItem.setProductionPlant(prodPlant);
				
				SapMaterialEntity mat = getOrderSpecialService().fetchSapMaterial(helpForm.getSapMaterialId());
				orderItem.setSapMaterial(mat);
			}
			
			OrderStatusBsEntity orderStatus = getRefDataGenericsService().findBsEntity(OrderStatusBsEntity.class, Constant.ORDER_STATUS_NEW);
			order.setOrderStatus(orderStatus);
			
			OrderCreationTypeBsEntity orderCreationType = getRefDataGenericsService().findBsEntity(OrderCreationTypeBsEntity.class, Constant.ORDER_CREATION_TYPE_CALL_CENTER);
			order.setOrderCreationType(orderCreationType);
			
			DeliveryTypeBsEntity deliveryType = getRefDataGenericsService().findBsEntity(DeliveryTypeBsEntity.class, Constant.DELIVERY_TYPE_RAIL_DIRECT);
			order.setDeliveryType(deliveryType);
			
			ArchivingStatusBsEntity archivingStatusBsEntity = getRefDataGenericsService().findBsEntity(ArchivingStatusBsEntity.class, 
					Constant.ARCHIVING_STATUS_NEW);
			order.setArchivingStatus(archivingStatusBsEntity);
			
			order.setNoPdaOrRailEventsFlag(false);
			order.setReleaseSapFlag(true);
			order.setFreeDeliveryFlag(false);
			order.setOrderConfEmailFlag(false);
			order.setOrderConfFaxFlag(false);
			order.setOrderConfSmsFlag(false);
			order.setLoadConfEmailFlag(false);
			order.setLoadConfFaxFlag(false);
			order.setLoadConfSmsFlag(false);
			order.setDelivnoteEmailFlag(false);
			order.setDelivnoteFaxFlag(false);
			order.setDelivnoteSinglePaperFlag(false);
			order.setForceToSapFlag(false);
			order.setITrackAvailableFlag(false);
			
			// TODO (pascal Nov 10, 2012): Remove after Release 2.1.0.6 (Nov 26)
			// LOGON-5630: DeliveryNoteCollFlag is no longer saved on individual Order but on CustOrder
			//order.setDelivnoteCollPaperFlag(false);
			
			order.setOnHoldOrderAndLockedFlag(false); // TODO gergely (March 05 2010) verify if this is correct (ADN51440)
			
			// TODO (valdas Feb 25, 2010): Currency set to CHF
			orderItem.setCurrency(getRefDataGenericsService().findBsEntity(CurrencyBsEntity.class, Constant.CURRENCY_CODE_CHF));
			orderItem.setOrderItemNo(Constant.ORDER_FIRST_ITEM_CD);
			
			orderItem.setOrder(order);
			List oiList = new ArrayList();
			oiList.add(orderItem);
			order.setOrderItems(oiList);
			return result = order;
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
	 * Call to upload an image hold by the imgForm and attach it to the orderRepEnt order repair
	 * entity.
	 * In case an error happened, actionerror is added to the request.
	 * @param request
	 * @param orderRepEnt
	 * @param imgForm
	 * @return 
	 */
	protected OrderRepAttEntity uploadImage(HttpServletRequest request, OrderRepEntity orderRepEnt,
			OrderDetailUploadAttachmentBean imgForm) {
		// [[
		OrderRepAttEntity result = null;
		Object[][] params = {{"request", request},
							{"orderRepEnt", orderRepEnt},
							{"imgForm", imgForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "uploadImage", params);
		}
		// ]]
		try {
			OrderRepAttEntity attachment = new OrderRepAttEntity();
			attachment.setOrderRep(orderRepEnt);
			FormFile file = imgForm.getFile();
			attachment.setAttachmentName(file.getFileName());
			try {					
				MimeTypeBsEntity dataMimeType = getServiceLocator().getUserAdminService().fetchMimeTypeByMimeTypeName(file.getContentType());
				attachment.setDataMimeType(dataMimeType);
				attachment.setData(file.getFileData());
			}
			catch (Exception e) {
				LOG.warn("Unsupported mime type: " + file.getContentType());
				return result = null;
			}
			attachment.setAttachmentName(file.getFileName());
			
			String type = imgForm.getType();
			OrderRepAttTypeBsEntity attachmentType = getRefDataGenericsService().findBsEntity(OrderRepAttTypeBsEntity.class, Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL);
			attachment.setAttachmentType(attachmentType);
			orderRepEnt.getOrderRepAttachments().add(attachment);
			return result = attachment;
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
