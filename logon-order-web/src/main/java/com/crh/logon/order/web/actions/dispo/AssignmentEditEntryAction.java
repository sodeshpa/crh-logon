/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.order.web.utils.AssignmentPartComparator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.RequestUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.dispo.AssignmentEditForm;
import com.crh.logon.order.web.utils.TourPart;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentPartTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_AssignmentEditForm" 
 *  path="/dispo/AssignmentEditEntry" scope="session"
 *  validate="true" input="DispoAssignmentEditTile.def"
 *
  * @struts.action-forward name="edit" path="DispoAssignmentEditTile.def"
 */
public class AssignmentEditEntryAction extends AssignmentActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(AssignmentEditEntryAction.class);
   
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
			Authorization.authorize(Authorization.DISPATCHING_W);
			AssignmentEditForm helpForm = (AssignmentEditForm) form;
			loadMaterialDescriptions(helpForm, getDispokarteService(), request.getLocale().getLanguage());
			
			String forwardName = "edit";
			
			loadUomMap(helpForm, request.getLocale());
			
			if (helpForm.getMaterialUoms() == null){
				helpForm.setMaterialUoms(
						getServiceLocator().getRefDataGenericsService().findTxEntityList(UomTxEntity.class, RequestUtils.getUserLocale(request, null)),
						getServiceLocator().getRefDataGenericsService().findBsEntityList(UomBsEntity.class));
			}
			 
			if (helpForm.getAssignmentPartTypeTxMap() == null || helpForm.getAssignmentPartTypeTxMap().isEmpty()){
				helpForm.setAssignmentPartTypeTxMap(getServiceLocator().getRefDataGenericsService().findTxEntityList(AssignmentPartTypeTxEntity.class, RequestUtils.getUserLocale(request, null)));
			}
			
			AssignmentEntity assignment = null;
			
			if (helpForm.getAssignmentId() >= 0) {
				assignment = getServiceLocator().getDispoService().fetchAssignmentForEdit(
						helpForm.getAssignmentId());
				initTourParts(assignment, helpForm);
			}
			else if (request.getParameter("truckId") != null) {
				Long truckId = Long.parseLong(request.getParameter("truckId"));
				Date date = new Date();
				if (request.getParameter("date") != null) {
					Long millis = Long.parseLong(request.getParameter("date"));
					date = new Date(millis);
				}
				assignment = getServiceLocator().getOrderService().createNewAssignment(truckId, date, TimeZone.getAppTimeZone(), 0, 0, 0);
				initTourParts(assignment, helpForm);
			}
			else {
				throw new LogonException("Either assignmentId or truckId must be given", Reason.RES_ERROR_UNSPECIFIED);
			}
			
			ShiptoEntity shipto = null;
			helpForm.setAssignment(assignment, shipto);
			helpForm.populateFormFromSelectedPart(0);
			
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
    
    private void loadMaterialDescriptions(AssignmentEditForm helpForm, DispokarteService dispoService, String localLangCd ) {
    	if ( helpForm.getMaterialDescriptionMap() != null && !helpForm.getMaterialDescriptionMap().isEmpty() ) {
			// the material descriptions are already loaded
			return;
    	}
    	List descriptionList = dispoService.fetchSapMaterialDescByLang(localLangCd);
		ListIterator descriptionIt = descriptionList.listIterator();
		HashMap materialDescriptionMap = new HashMap();
		
		while ( descriptionIt.hasNext() ) {
			SapMaterialDescEntity descEntity = (SapMaterialDescEntity) descriptionIt.next();
			materialDescriptionMap.put(descEntity.getSapMaterialId(), descEntity.getTxt());
		} 
		helpForm.setMaterialDescriptionMap(materialDescriptionMap);
	}
    
    private void initTourParts(AssignmentEntity assignment, AssignmentEditForm helpForm) {
    	Collections.sort(assignment.getAssignmentParts(), new AssignmentPartComparator());
    	
    	int tourPartSeq = 0;
    	TourPart tourPart = null;
    	Long lastToDistance = null;
    	
    	List<AssignmentPartEntity> parts = assignment.getAssignmentParts();
		List<TourPart> tourPartList = new ArrayList<TourPart>();
		
		for ( int i = 0; i < parts.size(); i ++) {
			AssignmentPartEntity ap = (AssignmentPartEntity) parts.get(i);
			long apType = ap.getAssignmentPartType().getAssignmentPartTypeCd();
			switch ((int)apType) {
				case (int)Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD:
					tourPart = createTourPart(null, ap, ap.getTransportDistanceM());
					break;
				case (int)Constant.ASSIGNMENT_PART_TYPE_LOAD:
					tourPart = createTourPart(ap, (AssignmentPartEntity) parts.get(i + 1), null);
					break;
				case (int)Constant.ASSIGNMENT_PART_TYPE_UNLOAD:
					tourPart = createTourPart(ap, (AssignmentPartEntity) parts.get(i + 1), lastToDistance);
					break;
				case (int)Constant.ASSIGNMENT_PART_TYPE_FROM:
					tourPart = createTourPart((AssignmentPartEntity) parts.get(i + 1), null, ap.getTransportDistanceM());
					break;
				case (int)Constant.ASSIGNMENT_PART_TYPE_TO:
					lastToDistance = ap.getTransportDistanceM();
					continue;
				default:
					continue;
			}

			tourPart.setSeqNum(tourPartSeq++);
			tourPartList.add(tourPart);
		}
		correctDeliveryPlantTimezone(tourPartList);
		helpForm.setTourPartList(tourPartList);
		helpForm.initTourPartDateTimeFormattingFlags();
		helpForm.initTimeModifierList(tourPartList.size());
    }
    
    /**
     * Delivery plant time zone in PRELOAD/RETURN parts should be the same as that in the LOAD
     */
    private void correctDeliveryPlantTimezone(List<TourPart> tourParts) {
    	if(tourParts != null) {
    		TimeZone deliveryPlantTimezone = null;
    		for(TourPart tourPart : tourParts) {
    			if(tourPart.getType() == TourPart.TYPE_LOAD) {
    				deliveryPlantTimezone = tourPart.getTimeZone();
    				break;
    			} 
    		}
    		for(TourPart tourPart : tourParts) {
    			if(tourPart.getType() == TourPart.TYPE_PRELOAD || tourPart.getType() == TourPart.TYPE_RETURN) {
    				tourPart.setDeliveryPlantTimeZone(deliveryPlantTimezone);
    			}
    		}
    	}
    }
    
    private void loadUomMap(AssignmentEditForm helpForm, Locale local) {
    	List uomList = getServiceLocator().getRefDataGenericsService().findTxEntityList(UomTxEntity.class, false, local, UomTxEntity.ATTR_TXT, true);
    	helpForm.setUomList(new ArrayList());
    	helpForm.setUomMap(new HashMap());
    	if ( uomList != null ) {
    		ListIterator uomIt = uomList.listIterator();
    		while ( uomIt.hasNext() ) {
    			UomTxEntity uom = (UomTxEntity) uomIt.next();
    			helpForm.getUomMap().put(uom.getUomTxPK().getUomBs().getUomCd(), uom);
    			helpForm.getUomList().add(uom);
    		}
    	}
	}
}
