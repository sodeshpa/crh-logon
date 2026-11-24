/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.dispo;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.TreeMap;

import javax.persistence.OptimisticLockException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.order.web.forms.dispo.AbstractDispoForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.DispoService;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.ReroutingOrderWrapper;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.forms.dispo.AssignmentEditForm;
import com.crh.logon.order.web.forms.dispo.DispoForm;
import com.crh.logon.order.web.utils.TourPart;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.ExtOrderEntity;
import com.crh.logon.persistence.entities.common.LoadEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.AssignPartStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentPartTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentPartTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_AssignmentEditForm" 
 *  path="/dispo/AssignmentEdit" scope="session"
 *  validate="false" input="DispoAssignmentEditTile.def"
 *
 * @struts.action-forward name="close" path="DispoAssignmentCloseTile.def"
 * @struts.action-forward name="refresh" path="DispoAssignmentEditTableTile.def"
 * @struts.action-forward name="error" path="DispoAssignmentEditTableTile.def"
 * @struts.action-forward name="showOrderDetail" path="DispoAssignmentEditTableLoadOrderIdTile.def"
 * @struts.action-forward name="showExternalOrder" path="DispoAssignmentEditTableLoadExtOrderIdTile.def"
 * @struts.action-forward name="reload" path="DispoAssignmentEditTile.def"
 * @struts.action-forward name="reroute" path="DispoReroutePopupTile.def"
 */
public class AssignmentEditAction extends AssignmentActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(AssignmentEditAction.class);
    
	private static final int LS_NEW = 0;
	private static final int LS_LOADING = 1;
	private static final int LS_LOADED = 2;
	private static final int LS_UNLOADED = 3;
	
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
			
		    String forwardName = "close";
		    if ( request.getParameter("setSelectedPart") != null ) {
		    	if ( helpForm.getSelectedPartId() != -1 ) {
		    		// store changes
		    		if ( !populateAssignmentPartEntity(helpForm, request)) {
		    			forwardName = "error";
		    			return result = mapping.findForward(forwardName);
		    		}
		    	}
		    	helpForm.setSelectedPartId( Integer.parseInt(request.getParameter("setSelectedPart")) );
		    	helpForm.populateFormFromSelectedPart(Integer.parseInt(request.getParameter("setSelectedPart")) );
		    	forwardName = "refresh";
		    }
		    else if (request.getParameter("save") != null) {
		    	try {
			    	if ( !populateAssignmentPartEntity(helpForm, request)) {
		    			forwardName = "error";
		    			return result = mapping.findForward(forwardName);
		    		}
			    	if (!validateLocations(helpForm)) {
			    		MessageUtils.addActionWarning(request, "editassignment.error.locationmissing");
			    		forwardName = "refresh";					
			    	}
			    	else if ( !validateTiming(helpForm) ) {
			    		MessageUtils.addActionWarning(request, "editassignment.error.timenotascending");
			    		forwardName = "refresh";
			    	}
			    	else if (!validateMessages(helpForm, request)) {
			    		forwardName = "refresh";
			    	}
			    	else {
				    	HashMap osmap = new HashMap();
				    	String error = setStates(helpForm, osmap);
				    	if (error != null) {
				    		MessageUtils.addActionWarning(request, error);
				    		forwardName = "refresh";					
				    	}
				    	else {
				    		
				    		/*
							 * TODO (marius Dec 5, 2012):
							 * Also include fields for planned/real arrival date
							 */
				    		
				    		String oldPlannedArrival = null;
				    		String oldPlannedDeparture = null;
				    		String oldRealArrival = null;
				    		String oldRealDeparture = null;
				    		
				    		String newPlannedArrival = helpForm.getAssignmentPartPlannedArrivalTime();
				    		String newPlannedDeparture = helpForm.getAssignmentPartPlannedDepartureTime();
				    		String newRealArrival = helpForm.getAssignmentPartRealArrivalTime();
				    		String newRealDeparture = helpForm.getAssignmentPartRealDepartureTime();
				    		
				    		
				    		AssignmentEntity assignment = null;
				    		assignment = getServiceLocator().getDispoService().fetchAssignmentForEdit(
				    				helpForm.getAssignment().getAssignmentId());
				    		
				    		
				    		List assParts = assignment.getAssignmentParts();
							ListIterator assPartsIt = assParts.listIterator();
							while (assPartsIt.hasNext()) {
								AssignmentPartEntity assp = (AssignmentPartEntity) assPartsIt.next();
								
								if ( assp.getAssignmentPartType().getAssignmentPartTypeCd() == Constant.ASSIGNMENT_PART_TYPE_LOAD ){
									oldPlannedArrival = DateTimeUtils.formatHoursMinutes( assp.getPlannedStart() );
									oldRealArrival = DateTimeUtils.formatHoursMinutes( assp.getRealStart() );
								}
								
								if ( assp.getAssignmentPartType().getAssignmentPartTypeCd() == Constant.ASSIGNMENT_PART_TYPE_TO ){
									oldPlannedDeparture = DateTimeUtils.formatHoursMinutes( assp.getPlannedStart() );
									oldRealDeparture = DateTimeUtils.formatHoursMinutes( assp.getRealStart() );
								}
							}
				    		if ( ((!StringUtils.empty(newPlannedArrival)) && (!newPlannedArrival.equals(oldPlannedArrival))) ||
									((!StringUtils.empty(newPlannedDeparture)) && (!newPlannedDeparture.equals(oldPlannedDeparture))) ||
									((!StringUtils.empty(newRealArrival)) && (!newRealArrival.equals(oldRealArrival))) ||
									((!StringUtils.empty(newRealDeparture)) && (!newRealDeparture.equals(oldRealDeparture))) ) {
				    			Authorization.authorize(Authorization.DISPATCHING_INT_ORD_W);
				    		}				    		
				    		
				    		List assignmentPartList = getAssignmentPartsFromTourPartList(helpForm.getTourPartList(), helpForm);
				    		updateAssignmentPartDistancesFromTourParts(helpForm.getTourPartList(), assignmentPartList);
				    		helpForm.getAssignment().setAssignmentParts(assignmentPartList);
					    	getServiceLocator().getDispoService().saveAssignmentAndOrders(helpForm.getAssignment(), osmap);
					    	MessageUtils.addActionMessage(request,"dispokarte.success.save");
					    	if (request.getParameter("resubmit") != null) {					    	
					    		getServiceLocator().getDispoService().transmitToDriver(helpForm.getAssignment().getAssignmentId(), Constant.WEIGH_SYSTEM_SP_NONE, Constant.WEIGH_SYSTEM_LP_LOGON);
					    	}
				    	}
			    	}
		    	}
		    	catch (RuntimeException e){
		    		if ( (e.getCause() != null) && (e.getCause() instanceof OptimisticLockException) ) {
		    			LOG.info("Exception caught: " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage());
		    			MessageUtils.addActionError(request, "optimisticlockerror.msg");
			    		forwardName = "refresh";
		    		}
		    		else {
		    			
		    			throw e;
		    		}
		    	}
		    }
		    else if (request.getParameter("delete") != null) {
		    	getServiceLocator().getDispoService().removeExteralAssignment(helpForm.getAssignment().getAssignmentId(), null);
		    }
		    else if ( request.getParameter("moveAssignmentPart") != null ) {
		    	if ( !populateAssignmentPartEntity(helpForm, request)) {
	    			forwardName = "error";
	    			return result = mapping.findForward(forwardName);
	    		}
		    	int partIdx = Integer.parseInt(request.getParameter("partIdx"));
		    	if ("up".equals(request.getParameter("moveAssignmentPart"))){
		    		//is similar to moving down the part above
		    		partIdx--;
		    	}
		    	TourPart down = (TourPart) helpForm.getTourPartList().get(partIdx);
		    	TourPart up = (TourPart) helpForm.getTourPartList().get(partIdx + 1);
		    	
		    	long durationDown = down.getDurationMin();
		    	Date arrDown = down.getPlannedArrival();
		    	long durationUp = up.getDurationMin();
		    	Date arrUp  = up.getPlannedArrival();
		    	
		    	if (down.getType() == TourPart.TYPE_LOAD) {
			    	down.setDepartureAp(up.getDepartureAp());
			    	up.setDepartureAp(down.getArrivalAp());
		        }
		    
		    	down.setPlannedArrival(arrUp);
		    	up.setPlannedArrival(arrDown);
		    	up.setPlannedDeparture(new Date(up.getPlannedArrival().getTime() + durationUp * 60000));
		    	down.setPlannedDeparture(new Date(down.getPlannedArrival().getTime() + durationDown * 60000));
		    	up.setSeqNum(up.getSeqNum() - 1);
		    	down.setSeqNum(down.getSeqNum() + 1);
		    	
		    	if (down.getType() != TourPart.TYPE_LOAD && up.getDepartureAp().getAssignmentPartType().getAssignmentPartTypeCd().longValue() != Constant.ASSIGNMENT_PART_TYPE_TO) {
		    		AssignmentPartTypeBsEntity tmp = up.getDepartureAp().getAssignmentPartType();
		    		up.getDepartureAp().setAssignmentPartType(down.getDepartureAp().getAssignmentPartType());
		    		down.getDepartureAp().setAssignmentPartType(tmp);
		    	}
		    	
		    	helpForm.getTourPartList().set(down.getSeqNum(), down);
		    	helpForm.getTourPartList().set(up.getSeqNum(), up);
		    	
		    	if (helpForm.getSelectedPartId() == up.getSeqNum()) {
		    		helpForm.setSelectedPartId(down.getSeqNum());
		    	}
		    	else if (helpForm.getSelectedPartId() == down.getSeqNum()) {
		    		helpForm.setSelectedPartId(up.getSeqNum());
		    	}
 
		    	helpForm.populateFormFromSelectedPart(helpForm.getSelectedPartId());
		    	
		    	helpForm.setRecalculateIdx(new Integer(up.getSeqNum() - 1));
		    	
		    	forwardName = "refresh";
		    }
		    else if ( request.getParameter("createAssignmentPart") != null ) {
		    	TourPart newTourPart;
		    	
		    	if ( "pre".equals(request.getParameter("createAssignmentPart") )) {
		    		AssignmentPartEntity newPart = new AssignmentPartEntity();
		    		newPart.setAssignmentPartSeqNo(new Long(0));
			    	newPart.setAssignmentPartType( ((AssignmentPartTypeTxEntity)helpForm.getAssignmentPartTypeTxMap().get(Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD)).getAssignmentPartTypeTxPK().getAssignmentPartTypeBs());
			    	newPart.setAssignPartStatus(getRefDataGenericsService().findBsEntity(AssignPartStatusBsEntity.class, Constant.ASSIGN_PART_STATUS_NEW));
			    	newPart.setAssignment(helpForm.getAssignment());
			    	newPart.setHomebaseFlag(Boolean.TRUE);
			    	newPart.setLnfShiptoFlag(Boolean.FALSE);
			    	newTourPart = createTourPart(null, newPart, null);
			    	newTourPart.setSeqNum(0);
			    	Iterator i = helpForm.getTourPartList().iterator();
			    	while (i.hasNext()){
			    		((TourPart)i.next()).incSeqNum();
			    	}
			    	helpForm.getTourPartList().add(0, newTourPart);
					helpForm.initTourPartDateTimeFormattingFlags();
			    	helpForm.initTimeModifierList(helpForm.getTourPartList().size());
			    	helpForm.setSelectedPartId(0);
			    	helpForm.populateFormFromSelectedPart(0);
		    	}
		    	else if ( "return".equals(request.getParameter("createAssignmentPart") )) {
		    		List tpl = helpForm.getTourPartList();
		    		TourPart lastTourPart = (TourPart)tpl.get(tpl.size() - 1);
		    		AssignmentPartEntity fromPart = lastTourPart.getDepartureAp();
		    		AssignmentPartEntity finishedPart = new AssignmentPartEntity();
		    		fromPart.setAssignmentPartType(((AssignmentPartTypeTxEntity)helpForm.getAssignmentPartTypeTxMap().get(Constant.ASSIGNMENT_PART_TYPE_FROM)).getAssignmentPartTypeTxPK().getAssignmentPartTypeBs());
			    	fromPart.setAssignPartStatus(getRefDataGenericsService().findBsEntity(AssignPartStatusBsEntity.class, Constant.ASSIGN_PART_STATUS_NEW));
			    	
			    	finishedPart.setAssignment(helpForm.getAssignment());
			    	finishedPart.setHomebaseFlag(Boolean.FALSE);
			    	finishedPart.setAssignmentPartType(((AssignmentPartTypeTxEntity)helpForm.getAssignmentPartTypeTxMap().get(Constant.ASSIGNMENT_PART_TYPE_FINISHED)).getAssignmentPartTypeTxPK().getAssignmentPartTypeBs());
			    	finishedPart.setAssignPartStatus(getRefDataGenericsService().findBsEntity(AssignPartStatusBsEntity.class, Constant.ASSIGN_PART_STATUS_NEW));
			    	
			    	lastTourPart.setDepartureAp(fromPart);
			    	newTourPart = createTourPart(finishedPart, null, null);
			    	newTourPart.setSeqNum(tpl.size());
			    	helpForm.getTourPartList().add(newTourPart);
					helpForm.initTourPartDateTimeFormattingFlags();
			    	helpForm.initTimeModifierList(helpForm.getTourPartList().size());
			    	helpForm.setSelectedPartId(helpForm.getTourPartList().size() - 1);
			    	helpForm.populateFormFromSelectedPart(helpForm.getTourPartList().size() - 1);
		    	}
		    	
		    	forwardName = "refresh";
		    }
		    else if ( request.getParameter("deleteAssignmentPart") != null) {
		    	int idxToDelete = Integer.parseInt( request.getParameter("deleteAssignmentPart"));
		    	TourPart rtp = (TourPart)helpForm.getTourPartList().remove(idxToDelete);
		    	if (rtp.getType() == TourPart.TYPE_RETURN){
		    		TourPart tp = (TourPart)helpForm.getTourPartList().get(helpForm.getTourPartList().size() -1);
		    		tp.getDepartureAp().setAssignmentPartType(((AssignmentPartTypeTxEntity)helpForm.getAssignmentPartTypeTxMap().get(Constant.ASSIGNMENT_PART_TYPE_FINISHED)).getAssignmentPartTypeTxPK().getAssignmentPartTypeBs());
		    	}
				helpForm.initTourPartDateTimeFormattingFlags();
		    	helpForm.initTimeModifierList(helpForm.getTourPartList().size());
		    	if (helpForm.getSelectedPartId() >= helpForm.getTourPartList().size()){
		    		helpForm.setSelectedPartId(helpForm.getTourPartList().size() - 1);
		    	}
		    	helpForm.populateFormFromSelectedPart(helpForm.getSelectedPartId());
		    	forwardName = "refresh";
		    }
		    else if ( request.getParameter("calculateTime") != null ) {
	    		if ( !populateAssignmentPartEntity(helpForm, request)) {
	    			forwardName = "error";
	    			return result = mapping.findForward(forwardName);
	    		}
		    	int baseId = Integer.parseInt(request.getParameter("baseId"));
		    	boolean up = Boolean.parseBoolean(request.getParameter("calcUp"));
		    	boolean down = Boolean.parseBoolean(request.getParameter("calcDown"));
		    	calculateTimes(helpForm, baseId, down, up);
	    		helpForm.populateFormFromSelectedPart(helpForm.getSelectedPartId());
	    		helpForm.setRecalculateIdx(null);
		    	forwardName = "refresh";
		    }
		    else if ( request.getParameter("showOrderDetail") != null ) {
		    	forwardName = "showOrderDetail";
		    	OrderEntity order = getDispoService().fetchOrder(helpForm.getOrderId());
		    	helpForm.setOderNumber(order.getOrderNumber());
		    }
		    else if ( request.getParameter("showExternalOrder") != null ) {
		    	forwardName = "showExternalOrder";
		    	ExtOrderEntity order = getExternalOrderService().fetchExternalOrder(helpForm.getExtOrderId());    
		    	helpForm.setOderNumber(order.getExtOrderNumber());
		    }
		    else if ( request.getParameter("reload") != null ) {
		    	OrderEntity order = getDispoService().fetchOrderForEdit(helpForm.getOderNumber());
		    	if ((order != null) && (order.getOrderStatus().getOrderStatusCd().longValue() != Constant.ORDER_STATUS_DELETED)){
			    	Iterator i = helpForm.getAssignment().getAssignmentParts().iterator();
			    	while (i.hasNext()){
			    		AssignmentPartEntity ap = (AssignmentPartEntity)i.next();
			    		if ((ap.getOrder() != null) && ap.getOrder().getOrderId().equals(order.getOrderId())){
			    			ap.setOrder(order);
			    		}
			    	}
			    	forwardName = "reload";
		    	}
		    }
			else if ( request.getParameter("reroute") != null ) {
				Long assignmentId = Long.valueOf(request.getParameter("reroute"));
				
				String orderList = request.getParameter("orders");
				String[] orderStrings = orderList.split("_");
				ArrayList<Long> filteredOrders = new ArrayList<Long>(orderStrings.length);
				if (orderStrings != null && orderStrings.length > 0) {
					Long orderId;
					for (int i = 0; i < orderStrings.length; i++) {
						try {
							orderId = Long.parseLong(orderStrings[i]);
							filteredOrders.add(orderId);
						}
						catch (NumberFormatException e) {
							LOG.error("the received orderId has wrong format (should be a valid long): " + orderStrings[i], e);
						}
					}
				}
				
				DispoService dispoService = getDispoService();
				Locale locale = getLocale(request);
				List<ReroutingOrderWrapper> reroutingOrderWrapperList = dispoService.fetchAlternativeOrdersForRerouting(assignmentId, filteredOrders, locale.getLanguage());
				helpForm.setReroutingOrderWrapperList(reroutingOrderWrapperList);
				forwardName = "reroute";
			}
			
		    if (forwardName.equals("close")) {
		    	
		    	AbstractDispoForm dispoForm = getDispoForm(request, helpForm.getAssignment());
		    	if (dispoForm instanceof DispoForm) { 
		    		request.setAttribute("refreshAssignments", true);
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
	
	/**
	 * Updates the assignment parts distances based on the given tourparts.
	 * @param tourPartList
	 * @param assignmentPartList
	 */
	static void updateAssignmentPartDistancesFromTourParts(List<TourPart> tourPartList,
			List<AssignmentPartEntity> assignmentPartList) {
		
		List<Long> distances = new ArrayList<Long>();
		ListIterator<TourPart> tpIt = tourPartList.listIterator();
		
		// First we collect the possible distances
		while (tpIt.hasNext()) {
			TourPart tp = (TourPart) tpIt.next();
			switch (tp.getType()) {
				case TourPart.TYPE_PRELOAD :
					distances.add(tp.getTransportDistanceM());
					break;	
				case TourPart.TYPE_DELIVERY :
					distances.add(tp.getTransportDistanceM());
					break;
				case TourPart.TYPE_RETURN :
					distances.add(tp.getTransportDistanceM());
					break;
				default : 
					break;
			}
			
		}
		
		int i = 0;
		
		for (AssignmentPartEntity entity : assignmentPartList) {
			AssignmentPartTypeBsEntity assignmentPartType = entity.getAssignmentPartType();
			if (assignmentPartType.getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD)) {
				entity.setTransportDistanceM(distances.get(i++));
			}
			else if (assignmentPartType.getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_TO)) {
				entity.setTransportDistanceM(distances.get(i++));
			}
			else if (assignmentPartType.getAssignmentPartTypeCd().equals(Constant.ASSIGNMENT_PART_TYPE_FROM)) {
				entity.setTransportDistanceM(distances.get(i++));
			}
		}
		
	}

	private boolean validateMessages(AssignmentEditForm helpForm, HttpServletRequest request) {
		boolean result = true;
		if ((helpForm.getAssignment().getCarrierDispatchingNote() != null) && 
				(helpForm.getAssignment().getCarrierDispatchingNote().length() > 250)) {
    		MessageUtils.addActionWarning(request, "editassignment.error.poolcarriertoolong");
    		result = false;
		}
		if ((helpForm.getAssignment().getCarrierTruckDriverNote() != null) && 
				(helpForm.getAssignment().getCarrierTruckDriverNote().length() > 250)) {
    		MessageUtils.addActionWarning(request, "editassignment.error.drivertoolong");
    		result = false;
		}
		return result;
	}

	private String setStates(AssignmentEditForm helpForm, HashMap newOrderStatusMap ) {
		Iterator i =  getAssignmentPartsFromTourPartList(helpForm.getTourPartList(),helpForm).iterator() ;
		AssignmentPartEntity lastAssStartedAp = null;
		HashMap loadApMap = new HashMap();
		TreeMap currentOrderApMap = null;
		AssignmentPartEntity preAp = null;
		HashMap orderApMap = new HashMap();
		while (i.hasNext()) {
			AssignmentPartEntity ap = (AssignmentPartEntity)i.next();
			if (ap != null){
				if (ap.getRealStart() != null){
					lastAssStartedAp = ap;
				}
				if (ap.getLoad() != null){
					loadApMap.put(ap.getLoad().getLoadId(), ap);
				}
				if (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD) {
					preAp = ap;
				}
				if (ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_TO) {
					currentOrderApMap = new TreeMap();
					if (preAp != null){
						currentOrderApMap.put(preAp.getAssignmentPartSeqNo(), preAp);
					}
					currentOrderApMap.put(ap.getAssignmentPartSeqNo(), ap);
				}
				if (ap.getOrder() != null){
					orderApMap.put(ap.getOrder(), currentOrderApMap);
					currentOrderApMap.put(ap.getAssignmentPartSeqNo(), ap);
					List orderItems = (List)LogonPackageHandlingUtil.getMaterialItems(ap.getOrder());
					Iterator j = orderItems.iterator();
					while (j.hasNext()) {
						OrderItemEntity oi = (OrderItemEntity)j.next();
						AssignmentPartEntity lap = (AssignmentPartEntity)loadApMap.get(oi.getLoad().getLoadId());
						currentOrderApMap.put(lap.getAssignmentPartSeqNo(), lap);
					}
				}
				if ((ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_FROM) ||
						(ap.getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_FINISHED)){
					Iterator j = orderApMap.values().iterator();
					while (j.hasNext()){
						((TreeMap)j.next()).put(ap.getAssignmentPartSeqNo(), ap);
					}
				}
			}
		}
		final long newAssignmentState;
		if (lastAssStartedAp != null) {
			final Long lastStartedAssignmentPartType = lastAssStartedAp.getAssignmentPartType().getAssignmentPartTypeCd();

			if (helpForm.getAssignment().getSimpleVehicle() != null) {
				// LOGON-6170: Special method to determine new assignment status for simple vehicles
				newAssignmentState = newAssignmentStateSimpleVehicle(lastStartedAssignmentPartType);
			}
			else {
				newAssignmentState = newAssignmentStateDefault(lastStartedAssignmentPartType);
			}
		}
		else {
			newAssignmentState = Constant.ASSIGNMENT_STATUS_NEW;
		}
		
		if (helpForm.getAssignment().getAssignStatus().getAssignStatusCd().longValue() == Constant.ASSIGNMENT_STATUS_FINISHED) {
			if (newAssignmentState != Constant.ASSIGNMENT_STATUS_FINISHED) {
				return "editassignment.error.assignmentfinished";
			}
		}
		
		i = orderApMap.keySet().iterator();
		while (i.hasNext()) {
			OrderEntity o = (OrderEntity)i.next();
			Iterator j = ((TreeMap)orderApMap.get(o)).values().iterator();
			AssignmentPartEntity lastOrdStartedAp = null;
			AssignmentPartEntity nextAp = null;
			long currentOrderStatus = o.getOrderStatus().getOrderStatusCd().longValue(); 
			long newOrderStatus = Constant.ORDER_STATUS_DISPATCHED;
			int minLoadState = LS_UNLOADED;
			int maxLoadState = LS_NEW;
			AssignmentPartEntity unloadAp = null;
			while (j.hasNext()){
				AssignmentPartEntity ap = (AssignmentPartEntity)j.next();
				if (ap.getOrder() != null){
					unloadAp = ap;
				}
				if (ap.getRealStart() == null) {
					if (nextAp == null){
						nextAp = ap;
					}
				}
				else {
					lastOrdStartedAp = ap;
				}
				if (ap.getLoad() != null){
					LoadEntity load = ap.getLoad();
					int currentLoadState = LS_UNLOADED;
					if (load.getUnloadingTs() == null){
						if (load.getLoadingTs() == null) {
							currentLoadState = LS_NEW;
						}
						else if (load.getNetQuantity() == null){
							currentLoadState = LS_LOADING;
						}
						else {
							currentLoadState = LS_LOADED;
						}
					}
					if (currentLoadState < minLoadState) {
						minLoadState = currentLoadState;
					}
					if (currentLoadState > maxLoadState) {
						maxLoadState = currentLoadState;
					}
					if (currentLoadState == LS_NEW) {
						if (ap.getRealStart() != null){
							return "editassignment.error.realstartonnewload";
						}
					}
					else {
						if (ap.getRealStart() == null){
							return "editassignment.error.norealstartonloadedload";
						}						
					}
				}
			}
			if (lastOrdStartedAp != null) {
				switch (lastOrdStartedAp.getAssignmentPartType().getAssignmentPartTypeCd().intValue()) {
				case (int)Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD:
					if (maxLoadState != LS_NEW) {
						return "editassignment.error.impossiblestatusforloadedload";
					}
					if (currentOrderStatus == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER) {
						newOrderStatus = currentOrderStatus;
					}
					else {
						newOrderStatus = Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK;
					}
					break;
				case (int)Constant.ASSIGNMENT_PART_TYPE_LOAD:
					if (maxLoadState == LS_UNLOADED) {
						return "editassignment.error.impossiblestatusforunloadedload";
					}
					if (minLoadState == LS_LOADED){
						if (newAssignmentState == Constant.ASSIGNMENT_STATUS_SHIPPING) {
							newOrderStatus = Constant.ORDER_STATUS_SHIPPING;							
						}
						else {
							newOrderStatus = Constant.ORDER_STATUS_LOADED;
						}
					}
					else if (maxLoadState == LS_NEW) {
						newOrderStatus = Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT;
					}
					else {
						newOrderStatus = Constant.ORDER_STATUS_LOADING;						
					}
					break;
				case (int)Constant.ASSIGNMENT_PART_TYPE_TO:
					if (maxLoadState == LS_UNLOADED) {
						return "editassignment.error.impossiblestatusforunloadedload";
					}
					if (minLoadState < LS_LOADED) {
						return "editassignment.error.impossiblestatusforloadingload";
					}
					newOrderStatus = Constant.ORDER_STATUS_SHIPPING;
					break;
				case (int)Constant.ASSIGNMENT_PART_TYPE_UNLOAD:
					if (maxLoadState == LS_UNLOADED) {
						return "editassignment.error.impossiblestatusforunloadedload";
					}
					if (minLoadState < LS_LOADED) {
						return "editassignment.error.impossiblestatusforloadingload";
					}
					if (lastAssStartedAp.getAssignmentPartSeqNo().equals(lastOrdStartedAp.getAssignmentPartSeqNo())){
						if (currentOrderStatus == Constant.ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT) {
							newOrderStatus = currentOrderStatus;
						}
						else {
							newOrderStatus = Constant.ORDER_STATUS_UNLOADING;
						}
						break;
					}
					//fallthrough if further in tour than unloading of current order (same state as on ap from/finished)
				case (int)Constant.ASSIGNMENT_PART_TYPE_FROM: //fallthrough
				case (int)Constant.ASSIGNMENT_PART_TYPE_FINISHED:
					if (minLoadState < LS_LOADED) {
						return "editassignment.error.impossiblestatusforloadingload";
					}
					if (currentOrderStatus == Constant.ORDER_STATUS_FINISHED) {
						newOrderStatus = currentOrderStatus;
					}
					else {
						newOrderStatus = Constant.ORDER_STATUS_DELIVERED;
					}
					break;
				default:
					break;
				}
			}
			else {
				if (maxLoadState != LS_NEW) {
					return "editassignment.error.impossiblestatusforloadedload";
				}
				if ((currentOrderStatus == Constant.ORDER_STATUS_DISPATCHED) || 
						(currentOrderStatus == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER)) {
					newOrderStatus = currentOrderStatus;
				}
				else {
					newOrderStatus = Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK;
				}
			}
			if ((currentOrderStatus == Constant.ORDER_STATUS_DELIVERED) ||
					(currentOrderStatus == Constant.ORDER_STATUS_FINISHED)) {
				if ((newOrderStatus != Constant.ORDER_STATUS_DELIVERED) &&
						(newOrderStatus != Constant.ORDER_STATUS_FINISHED)) {
					return "editassignment.error.orderalreadyfinished";
				}
			}
			newOrderStatusMap.put(unloadAp, newOrderStatus);
			
		}
		
		helpForm.getAssignment().setAssignStatus(getRefDataGenericsService().findBsEntity(AssignStatusBsEntity.class, newAssignmentState));
			
		return null;
	}

	/**
	 * Computes the new state that an assignment should have, based on the type of the assignment
	 * part that most recently started. For example, if the most recent assignment part that started
	 * (i.e., has a real start time set) is a LOAD part, then the new assignment status is
	 * determined to be "LOADING".
	 * <p>
	 * Note that this method is for truck assignments only (about rail I am not sure).
	 * For simple vehicles you must use <code>newAssignmentStateSimpleVehicle()</code> instead.
	 * @param lastStartedAssignmentPartType The type of the assignment part that was most recently
	 * started (must not be null).
	 * @return The new state for the assignment.
	 */
	private long newAssignmentStateDefault(final Long lastStartedAssignmentPartType) {
		final long newAssignmentState;
		switch (lastStartedAssignmentPartType.intValue()) {
		case (int)Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD:
			newAssignmentState = Constant.ASSIGNMENT_STATUS_PRE_LOAD;
			break;
		case (int)Constant.ASSIGNMENT_PART_TYPE_LOAD:
			newAssignmentState = Constant.ASSIGNMENT_STATUS_LOADING;
			break;
		case (int)Constant.ASSIGNMENT_PART_TYPE_TO:
			newAssignmentState = Constant.ASSIGNMENT_STATUS_SHIPPING;
			break;
		case (int)Constant.ASSIGNMENT_PART_TYPE_UNLOAD:
			newAssignmentState = Constant.ASSIGNMENT_STATUS_UNLOADING;
			break;
		case (int)Constant.ASSIGNMENT_PART_TYPE_FROM:
			newAssignmentState = Constant.ASSIGNMENT_STATUS_RETURNING;
			break;
		case (int)Constant.ASSIGNMENT_PART_TYPE_FINISHED:
			newAssignmentState = Constant.ASSIGNMENT_STATUS_FINISHED;
			break;
		default:
			 newAssignmentState = Constant.ASSIGNMENT_STATUS_NEW;
			break;
		}
		return newAssignmentState;
	}

	/**
	 * Computes the new state that an assignment should have, for <em>Simple Vehicles</em>!
	 * It is based on the type of the assignment part that most recently started. For example,
	 * if the most recent assignment part that started
	 * (i.e., has a real start time set) is a LOAD part, then the new assignment status is
	 * determined to be "SHIPPING".
	 * <p>
	 * Note that this method is for simple vehicle assignments only.
	 * For trucks (and rail?) you must use <code>newAssignmentStateDefault()</code> instead.
	 * @param lastStartedAssignmentPartType The type of the assignment part that was most recently
	 * started (must not be null).
	 * @return The new state for the assignment.
	 */
	private long newAssignmentStateSimpleVehicle(final Long lastStartedAssignmentPartType) {
		final long newAssignmentState;
		switch (lastStartedAssignmentPartType.intValue()) {
		case (int)Constant.ASSIGNMENT_PART_TYPE_PRE_LOAD:
		case (int)Constant.ASSIGNMENT_PART_TYPE_LOAD:
		case (int)Constant.ASSIGNMENT_PART_TYPE_TO:
			newAssignmentState = Constant.ASSIGNMENT_STATUS_SHIPPING;
			break;
		case (int)Constant.ASSIGNMENT_PART_TYPE_UNLOAD:
		case (int)Constant.ASSIGNMENT_PART_TYPE_FROM:
			newAssignmentState = Constant.ASSIGNMENT_STATUS_RETURNING;
			break;
		case (int)Constant.ASSIGNMENT_PART_TYPE_FINISHED:
			newAssignmentState = Constant.ASSIGNMENT_STATUS_FINISHED;
			break;
		default:
			 newAssignmentState = Constant.ASSIGNMENT_STATUS_NEW;
			break;
		}
		return newAssignmentState;
	}

	private boolean populateAssignmentPartEntity(AssignmentEditForm helpForm, HttpServletRequest request) {
		// [[
		boolean result1 = false;
		Object[][] params = {{"helpForm", helpForm},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "populateAssignmentPartEntity", params);
		}
		// ]]
		try {
			boolean result = true;
			TourPart tourPart = helpForm.getSelectedTourPart();
			
			tourPart.setSiteName(null);
			tourPart.setSiteLoadUnload(null);
			tourPart.setSiteContactPersonTxt(null);
			tourPart.setSiteStreetHouseNo(null);
			tourPart.setSiteCountry(null);
			tourPart.setSitePostalCode(null);
			tourPart.setSiteCity(null);
			
			if ( "true".equals(helpForm.getAssignmentPartChanged())) {
				// if anything other than real time changes, the assignment part's status is switched back to new 
				// (and the tour will need to be transmitted to the driver again)
				tourPart.setAssignPartStatus( getRefDataGenericsService().findBsEntity(AssignPartStatusBsEntity.class, AssignmentEditForm.ASSIGNMENT_PART_STATUS_NEW));
			} 
			
			Boolean lnfShiptoFlag = tourPart.getLnfShiptoFlag();
			if ( AssignmentEditForm.POSITION_HOMEBASE.equals(helpForm.getLocationType()) ) {
				tourPart.setShippingpoint(null);
				tourPart.setShipto(null);
				tourPart.setCoordLat(null);
				tourPart.setCoordLon(null);
				tourPart.setHomebaseFlag(Boolean.TRUE);
			}
			else if ( AssignmentEditForm.POSITION_COORDS.equals(helpForm.getLocationType()) ) {
				try {
					if ((helpForm.getAssignmentPartCoordLat() != null) &&
							!helpForm.getAssignmentPartCoordLat().trim().isEmpty() &&
							(helpForm.getAssignmentPartCoordLon() != null) &&
							!helpForm.getAssignmentPartCoordLon().trim().isEmpty()) {
						tourPart.setCoordLat(new Double(helpForm.getAssignmentPartCoordLat()));
						tourPart.setCoordLon(new Double(helpForm.getAssignmentPartCoordLon()));
					}
					else {
						tourPart.setCoordLat(null);
						tourPart.setCoordLon(null);
					}
				}
				catch (NumberFormatException e) {
					MessageUtils.addActionWarning(request, "editassignment.error.selectscoordinates");
					result = false;
					return result1 = result;
				}
				tourPart.setShippingpoint(null);
				tourPart.setShipto(null);
				tourPart.setHomebaseFlag(Boolean.FALSE);
				tourPart.setSiteName(helpForm.getSiteName());
				tourPart.setSiteLoadUnload(helpForm.getSiteLoadUnload());
				tourPart.setSiteContactPersonTxt(helpForm.getSiteContactPersonTxt());
				tourPart.setSiteStreetHouseNo(helpForm.getSiteStreetHouseNo());
				tourPart.setSiteCountry(helpForm.getSiteCountry());
				tourPart.setSitePostalCode(helpForm.getSitePostalCode());
				tourPart.setSiteCity(helpForm.getSiteCity());
			} 
			else if ( AssignmentEditForm.POSITION_NONE.equals(helpForm.getLocationType()) ) {
				tourPart.setShippingpoint(null);
				tourPart.setShipto(null);
				tourPart.setHomebaseFlag(Boolean.FALSE);
				tourPart.setCoordLat(null);
				tourPart.setCoordLon(null);
			} 
			else if ( AssignmentEditForm.POSITION_SHIPPINGPOINT.equals(helpForm.getLocationType()) ) {
				ShippingpointEntity sp = null;
				if ( StringUtils.notEmpty( helpForm.getAssignmentPartShippingpointId()) ) {
					sp = getServiceLocator().getDispoService().fetchShippingpoint(Long.parseLong(helpForm.getAssignmentPartShippingpointId()));
				}
				tourPart.setShippingpoint(sp);
				tourPart.setShipto(null);
				tourPart.setHomebaseFlag(Boolean.FALSE);
				tourPart.setCoordLat(null);
				tourPart.setCoordLon(null);
			}
			else if ( AssignmentEditForm.POSITION_SHIPTO.equals(helpForm.getLocationType()) ) {
				ShiptoEntity shipto = null;
				if ( StringUtils.notEmpty( helpForm.getAssignmentPartShiptoSapNr() ) ) {
					shipto = getServiceLocator().getDispokarteService().fetchShiptoBySapId( helpForm.getAssignmentPartShiptoSapNr());
				}
				tourPart.setShippingpoint(null);
				tourPart.setShipto(shipto);
				tourPart.setHomebaseFlag(Boolean.FALSE);
				if (lnfShiptoFlag != null && lnfShiptoFlag){
					try {
						if ((helpForm.getAssignmentPartCoordLat() != null) &&
								!helpForm.getAssignmentPartCoordLat().trim().isEmpty() &&
								(helpForm.getAssignmentPartCoordLon() != null) &&
								!helpForm.getAssignmentPartCoordLon().trim().isEmpty()) {
							tourPart.setCoordLat(new Double(helpForm.getAssignmentPartCoordLat()));
							tourPart.setCoordLon(new Double(helpForm.getAssignmentPartCoordLon()));
						}
						else {
							tourPart.setCoordLat(null);
							tourPart.setCoordLon(null);
						}
					}
					catch (NumberFormatException e) {
						MessageUtils.addActionWarning(request, "editassignment.error.selectscoordinates");
						result = false;
						return result1 = result;
					}
					tourPart.setSiteName(helpForm.getSiteName());
					tourPart.setSiteLoadUnload(helpForm.getSiteLoadUnload());
					tourPart.setSiteContactPersonTxt(helpForm.getSiteContactPersonTxt());
					tourPart.setSiteStreetHouseNo(helpForm.getSiteStreetHouseNo());
					tourPart.setSiteCountry(helpForm.getSiteCountry());
					tourPart.setSitePostalCode(helpForm.getSitePostalCode());
					tourPart.setSiteCity(helpForm.getSiteCity());					
				}
				else {
					tourPart.setCoordLat(null);
					tourPart.setCoordLon(null);
				}
			}
			
			AbstractDispoForm dispoForm = getDispoForm(request, helpForm.getAssignment());
			Date dispoDate = dispoForm.getDate();
			
			if ( StringUtils.notEmpty(helpForm.getAssignmentPartPlannedArrivalTime()) ) {
				try{
					
					Date originalDate = getSchedulingDatesOrFormDate(dispoDate, tourPart.getPlannedArrival());
					String formTime = helpForm.getAssignmentPartPlannedArrivalTime();
					String formDate = helpForm.getAssignmentPartPlannedArrivalDate();
					Calendar newCal = calculateNewTime(tourPart.getTimeZone(), originalDate, formTime, formDate, null);
					
					tourPart.setPlannedArrival(newCal.getTime());
					
				}
				catch (ParseException e) {
					// display warning
				}
			}
			if ( StringUtils.notEmpty(helpForm.getAssignmentPartRealArrivalTime()) ) {
				try{
					
					Date originalDate = tourPart.getRealArrival();
					
					if (originalDate == null) {
						originalDate = tourPart.getPlannedArrival();
					}
					
					originalDate = getSchedulingDatesOrFormDate(dispoDate, originalDate);
					
					
					String formTime = helpForm.getAssignmentPartRealArrivalTime();
					String formDate = helpForm.getAssignmentPartRealArrivalDate();
					Calendar newCal = calculateNewTime(tourPart.getTimeZone(), originalDate, formTime, formDate, helpForm.getMostRecentRealDepartureDate());
					
					tourPart.setRealArrival(newCal.getTime());
					
				}
				catch (ParseException e) {
					// display warning
				}
			}
			else {
				tourPart.setRealArrival(null);
			}
			if ( StringUtils.notEmpty(helpForm.getAssignmentPartPlannedDepartureTime()) ) {
				try{
					
					Date originalDate = getSchedulingDatesOrFormDate(dispoDate, tourPart.getPlannedDeparture());
					String formTime = helpForm.getAssignmentPartPlannedDepartureTime();
					String formDate = helpForm.getAssignmentPartPlannedDepartureDate();
					Calendar newCal = calculateNewTime(tourPart.getTimeZone(), originalDate, formTime, formDate, null);
					
					tourPart.setPlannedDeparture(newCal.getTime());
					
				}
				catch (ParseException e) {
					// display warning
				}
			}
			if ( StringUtils.notEmpty(helpForm.getAssignmentPartRealDepartureTime()) ) {
				try{
					
					Date originalDate = tourPart.getRealDeparture();
					if (originalDate == null) {
						originalDate = tourPart.getPlannedDeparture();
					}
					
					originalDate = getSchedulingDatesOrFormDate(dispoDate, originalDate);
					
					String formTime = helpForm.getAssignmentPartRealDepartureTime();
					String formDate = helpForm.getAssignmentPartRealDepartureDate();
					Calendar newCal = calculateNewTime(tourPart.getTimeZone(), originalDate, formTime, formDate, helpForm.getMostRecentRealDepartureDate());
					
					tourPart.setRealDeparture(newCal.getTime());
					
				}
				catch (ParseException e) {
					// display warning
				}
			}
			else {
				tourPart.setRealDeparture(null);
			}
			String assignmentPartDistance = helpForm.getAssignmentPartDistance();
			if ( StringUtils.notEmpty(assignmentPartDistance) ) {
				try{
					BigDecimal distance = new BigDecimal(assignmentPartDistance);
					tourPart.setDistance(distance);
				}
				catch (NumberFormatException e) {
					// display warning
				}
			}
			if ( helpForm.isExternalOrder() ) {
				tourPart.setExternalMaterial(helpForm.getExternalMaterial());
				tourPart.setExternalUom(StringUtils.empty(helpForm.getExternalUom()) || "-1".equals(helpForm.getExternalUom())  ? null : ((UomTxEntity) helpForm.getUomMap().get(helpForm.getExternalUom())).getUomTxPK().getUomBs());
				try {
					tourPart.setExternalQuantity(helpForm.getExternalQuantity());
				}
				catch (NumberFormatException e) {
					MessageUtils.addActionWarning(request, "editassignment.error.quantity");
					result = false;
					return result1 = result;
				}
			}
			//If tour part type = load set position information for all other load parts to the same values
			//(all parts must be loaded at the same place)
			if (tourPart.getType() == TourPart.TYPE_LOAD){
				Iterator i = helpForm.getTourPartList().iterator();
				while (i.hasNext()) {
					TourPart otp = (TourPart)i.next();
					if (!otp.equals(tourPart) && (otp.getType() == TourPart.TYPE_LOAD)) {
						otp.setCoordLat(tourPart.getCoordLat());
						otp.setCoordLon(tourPart.getCoordLon());
						otp.setHomebaseFlag(tourPart.getHomebaseFlag());
						otp.setShippingpoint(tourPart.getShippingpoint());
						otp.setShipto(tourPart.getShipto());
						otp.setLnfShiptoFlag(lnfShiptoFlag);
						otp.setSiteCity(tourPart.getSiteCity());
						otp.setSiteContactPersonTxt(tourPart.getSiteContactPersonTxt());
						otp.setSiteCountry(tourPart.getSiteCountry());
						otp.setSiteLoadUnload(tourPart.getSiteLoadUnload());
						otp.setSiteName(tourPart.getSiteName());
						otp.setSitePostalCode(tourPart.getSitePostalCode());
						otp.setSiteStreetHouseNo(tourPart.getSiteStreetHouseNo());
					}
				}
			}
			
			return result1 = true;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
		// ]]
		}
	}

	/**
	 * Returns the schedulingDate in preference or the dispoDate if former not available 
	 * @param dispoDate
	 * @param schedulingDate
	 * @return selected date
	 */
	private Date getSchedulingDatesOrFormDate(Date dispoDate, Date schedulingDate) {
		// [[
		Date result = null;
		Object[][] params = {{"dispoDate", dispoDate},
							{"schedulingDate", schedulingDate}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSchedulingDatesOrFormDate", params);
		}
		// ]]
		try {
			
			if (schedulingDate == null) {
				result = dispoDate;
			} 
			else {
				result = schedulingDate;
			}
			
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
	 * Example: 
	 * formTime = '11:25'
	 * originalDate = '2012.11.26'
	 * => output: 2012.11.26 11:25
	 * @param originalDate
	 * @param formTime
	 * @param formDate The date, this can be null (e.g. for the truck scheduling case
	 * where we don't show a date input field, and the assignment day is implicitly assumed) 
	 * @return calculated Calendar
	 * @throws ParseException
	 */
	private Calendar calculateNewTime(TimeZone timeZone, Date originalDate, String formTime, String formDate, Date previousAssignmentDate) throws ParseException {
		// [[
		Calendar result = null;
		Object[][] params = {{"originalDate", originalDate},
							{"formTime", formTime},
							{"formDate", formDate}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "calculateNewTime", params);
		}
		// ]]
		try {
			if (timeZone == null) {
				timeZone = getTimeZoneService().createDefault(getUserLocale());
			}
			if (!StringUtils.empty(formDate)) {
				final Date newDateTime = timeZone.parseDayMonthYearHourMinute(formDate, formTime);
				Calendar newDateCal = timeZone.asCalendar();
				newDateCal.setTime(newDateTime);
				// LOGON-9202 Feb 27, 2015/fszabo: in case the form date was before the previous assignment part's date, we need to set the date to that.
				if (previousAssignmentDate != null && newDateTime.before(previousAssignmentDate)) {
					Calendar prevCalendar = timeZone.asCalendar(previousAssignmentDate);
					newDateCal.set(Calendar.YEAR, prevCalendar.get(Calendar.YEAR));
					newDateCal.set(Calendar.MONTH, prevCalendar.get(Calendar.MONTH));
					newDateCal.set(Calendar.DAY_OF_MONTH, prevCalendar.get(Calendar.DAY_OF_MONTH));
				}
				return result = newDateCal;
			} else {
				Calendar origCal = timeZone.asCalendar();
				origCal.setTime(originalDate);
				return result = origCal;
			}
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
	 * Returns the the DispoForm or the SimpleDispoForm depending on which
	 * is available.
	 * @param request
	 * @return the found {@link AbstractDispoForm} 
	 */
	private AbstractDispoForm getDispoForm(HttpServletRequest request, AssignmentEntity assignment) {
		HttpSession session = request.getSession();

		Object dispoForm;

		if (assignment.getSimpleVehicle() == null) {
			dispoForm = session.getAttribute("order_dispo_DispoForm");
		}
		else {
			dispoForm = session.getAttribute("order_simpledispo_SimpleDispoForm");
		}

		((AbstractDispoForm) dispoForm).setUserTimeZone(getUserTimezone());

		return (AbstractDispoForm) dispoForm;
	}
	
	
	
	
	private boolean validateLocations(AssignmentEditForm helpForm) {
		// [[
		boolean result = false;
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validateLocations", params);
		}
		// ]]
		try {
			TourPart prevPart = null;
			ListIterator partIt = helpForm.getTourPartList().listIterator();
			while ( partIt.hasNext() ) {
				TourPart part = (TourPart) partIt.next();
				 if (AssignmentEditForm.POSITION_NONE.equals(part.getLocationType())){
					 return result = false;
				 }
			}
			return result = true;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}	
	
	private boolean validateTiming(AssignmentEditForm helpForm) {
		// [[
		boolean result = false;
		Object[][] params = {{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validateTiming", params);
		}
		// ]]
		try {
			TourPart prevPart = null;
			ListIterator partIt = helpForm.getTourPartList().listIterator();
			while ( partIt.hasNext() ) {
				TourPart part = (TourPart) partIt.next();
				Date plannedDeparture = part.getPlannedDeparture();
				if ((part.getType() != TourPart.TYPE_RETURN) &&  plannedDeparture == null ) {
					return result = false;
				}
				Date plannedArrival = part.getPlannedArrival();
				if ((part.getType() != TourPart.TYPE_PRELOAD) &&  plannedArrival == null ) {
					return result = false;
				}
				if ((plannedDeparture != null) && (plannedArrival != null)
						&& plannedDeparture.before(plannedArrival)) {
					return result = false;
				}
				Date realDeparture = part.getRealDeparture();
				Date realArrival = part.getRealArrival();
				if ((realDeparture != null) && (part.getType() != TourPart.TYPE_PRELOAD)) {
					if (realArrival == null) {
						return result = false;
					}
					if (realDeparture.before(realArrival)) {
						return result = false;
					}
				}
						
				if (prevPart != null) {
					if (plannedArrival.before(prevPart.getPlannedDeparture())) {
						return result = false;
					}
					if (realArrival != null) {
						if (prevPart.getRealDeparture() == null) {
							return result = false;
						}
						
						if (realArrival.before(prevPart.getRealDeparture())){
							return result = false;
						}
					}
				}
				prevPart = part;
			}
			return result = true;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private List getAssignmentPartsFromTourPartList(List tourPartList, AssignmentEditForm helpForm) {
		// [[
		List result = null;
		Object[][] params = {{"tourPartList", tourPartList},
							{"helpForm", helpForm}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAssignmentPartsFromTourPartList", params);
		}
		// ]]
		try {
			List<AssignmentPartEntity> assignmentPartList = new ArrayList<AssignmentPartEntity>();			
			// FIXME: initialize with max seqno + 1
			long seqId = helpForm.getMaxSeqno() + 1;
			ListIterator tpIt = tourPartList.listIterator();
			while (tpIt.hasNext()) {
				TourPart tp = (TourPart) tpIt.next();
				switch (tp.getType()) {
				case TourPart.TYPE_PRELOAD :
					tp.getDepartureAp().setAssignmentPartSeqNo(new Long(seqId++));
					assignmentPartList.add(tp.getDepartureAp());
					break;	
				case TourPart.TYPE_RETURN :
					tp.getArrivalAp().setAssignmentPartSeqNo(new Long(seqId++));
					assignmentPartList.add(tp.getArrivalAp());
					break;
				case TourPart.TYPE_LOAD :
					tp.getArrivalAp().setAssignmentPartSeqNo(new Long(seqId++));
					assignmentPartList.add(tp.getArrivalAp());
					if (tp.getDepartureAp().getAssignmentPartType().getAssignmentPartTypeCd().longValue() == Constant.ASSIGNMENT_PART_TYPE_TO) {
						tp.getDepartureAp().setAssignmentPartSeqNo(new Long(seqId++));
						assignmentPartList.add(tp.getDepartureAp());						
					}
					break;
				case TourPart.TYPE_DELIVERY :
					tp.getArrivalAp().setAssignmentPartSeqNo(new Long(seqId++));
					assignmentPartList.add(tp.getArrivalAp());
					tp.getDepartureAp().setAssignmentPartSeqNo(new Long(seqId++));
					assignmentPartList.add(tp.getDepartureAp());
					break;
				default : 
					break;
				}
				
			}
			return result = assignmentPartList;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private void calculateTimes(AssignmentEditForm helpForm, int baseId, boolean down, boolean up) {
		// [[
		Object[][] params = {{"helpForm", helpForm},
							{"baseId", baseId + ""},
							{"down", down + ""},
							{"up", up + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "calculateTimes", params);
		}
		// ]]
		try {
			TourPart basePart = (TourPart ) helpForm.getTourPartList().get(baseId);
					
			Date baseTime = basePart.getPlannedArrival();
			if (down && (baseTime != null)) {
				Calendar calculatedCal = new GregorianCalendar();
				calculatedCal.setTime(baseTime);
				for (int backwardId = baseId - 1; backwardId > -1; backwardId--) {
					TourPart backTourPart = (TourPart ) helpForm.getTourPartList().get(backwardId);
					if ( backTourPart.getRealDeparture() != null) {
						// fix part reached
						break;
					}
					long diff = correctDuration(backTourPart, (TourPart ) helpForm.getTourPartList().get(backwardId + 1), 
							new BigDecimal((String) helpForm.getTimeModifierList().get(backwardId)).doubleValue());
					long duration = backTourPart.getDurationMin();
					
					calculatedCal.add(Calendar.MINUTE, -(int)diff);
					backTourPart.setPlannedDeparture(calculatedCal.getTime());
					calculatedCal.add(Calendar.MINUTE, -(int)duration);
					backTourPart.setPlannedArrival(calculatedCal.getTime());
				}
			}
			
			baseTime = basePart.getPlannedDeparture();
			
			if (up && (baseTime != null)) {
				Calendar calculatedCal = new GregorianCalendar();
				calculatedCal.setTime(baseTime);
			
				for (int forwardId = baseId + 1; forwardId < helpForm.getTourPartList().size(); forwardId++) {
					TourPart forwardTourPart = (TourPart ) helpForm.getTourPartList().get(forwardId);
					if ( forwardTourPart.getRealArrival() != null) {
						// fix part reached
						break;
					}
					long diff = correctDuration((TourPart ) helpForm.getTourPartList().get(forwardId - 1), 
							forwardTourPart, new BigDecimal((String) helpForm.getTimeModifierList().get(forwardId)).doubleValue());
					long duration = forwardTourPart.getDurationMin(); 
					
					calculatedCal.add(Calendar.MINUTE, (int)diff);
					forwardTourPart.setPlannedArrival(calculatedCal.getTime());
					calculatedCal.add(Calendar.MINUTE, (int)duration);
					forwardTourPart.setPlannedDeparture(calculatedCal.getTime());
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
	

	private long correctDuration(TourPart start, TourPart destination, double rawDuration) {
		double correctionFactor = 1;
		if (destination.getLocationType().equals(AssignmentEditForm.POSITION_SHIPTO)) {
			if (start.getLocationType().equals(AssignmentEditForm.POSITION_SHIPPINGPOINT)) {
				correctionFactor = getServiceLocator().getOrderService().getDurationCorrectionFactor(
						(start.getDepartureAp().getShippingpoint() == null)?null:start.getDepartureAp().getShippingpoint().getShippingpointId(), 
						(destination.getArrivalAp().getShipto() == null)?null:destination.getArrivalAp().getShipto().getShiptoId(),
								start.getOrder());
			}
			else if (start.getLocationType().equals(AssignmentEditForm.POSITION_SHIPTO)) {
				correctionFactor = getServiceLocator().getOrderService().getDurationCorrectionFactor(
						(start.getDepartureAp().getShipto() == null)?null:start.getDepartureAp().getShipto().getShiptoId(), 
						(destination.getArrivalAp().getShipto() == null)?null:destination.getArrivalAp().getShipto().getShiptoId(),
								start.getOrder());
			}
			else {
				correctionFactor = getServiceLocator().getOrderService().getDurationCorrectionFactor(
						null, null, start.getOrder());
			}
		}
		else {
			correctionFactor = getServiceLocator().getOrderService().getDurationCorrectionFactor(
					null, null, start.getOrder());			
		}
		return Math.round(rawDuration * correctionFactor);
	}
}
