/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.crh.logon.order.web.forms.raildispo.DispoForm;
import org.apache.struts.util.RequestUtils;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LocalServices;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ShippingPointServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserServiceLocalInterface;
import com.crh.logon.business.services.interfaces.DispoService;
import com.crh.logon.business.services.utils.EtranResult;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.OrderWrapper;
import com.crh.logon.business.services.utils.WagonWrapper;
import com.crh.logon.order.web.utils.PosWrapper;
import com.crh.logon.order.web.utils.railexport.RailDataProvider;
import com.crh.logon.persistence.entities.views.DispoMaxWagonPosEntity;
import com.crh.logon.persistence.entities.views.DispoPlantTrackListEntity;
import com.crh.logon.persistence.entities.views.DispoWContainerListEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import javax.ejb.EJB;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="RailDispoAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.services.utils.*"
  */
public class RailDispoAjaxService extends DwrServiceSupport {
    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailDispoAjaxService.class);
 
    @EJB private UserServiceLocalInterface userService;
	@EJB private DispoServiceLocalInterface dispoService;
	@EJB private UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	@EJB private LoadingServiceLocalInterface loadingService;
	@EJB private ShippingPointServiceLocalInterface shippingPointService;
	@EJB private MonitoringTablesServiceLocalInterface monitoringService;
	
    public void setDispoDate(String date, HttpSession session) throws ParseException {
		// [[
		Object[][] params = {{"date", date},
							{"session", session}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setDispoDate", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_R);
			DispoForm dispoForm = (DispoForm) session.getAttribute("order_raildispo_DispoForm");
			dispoForm.setStringDate(date);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    private void initServiceBeans() {
    	try {
    		InitialContext initContext = new InitialContext();				    
	    	this.userService = (UserServiceLocalInterface) initContext.lookup(LocalServices.APPLICATION_NAME + "/UserServiceBean!com.crh.logon.business.services.localinterfaces.UserServiceLocalInterface");
	    	this.dispoService = (DispoServiceLocalInterface) initContext.lookup(LocalServices.APPLICATION_NAME + "/DispoServiceBean!com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface");
	    	this.unitOfMeasurementConverterService = (UomConverterServiceLocalInterface) initContext.lookup(LocalServices.APPLICATION_NAME + "/UomConverterServiceBean!com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface");
	    	this.loadingService = (LoadingServiceLocalInterface) initContext.lookup(LocalServices.APPLICATION_NAME + "/LoadingServiceBean!com.crh.logon.business.services.localinterfaces.LoadingServiceLocalInterface");
	    	this.shippingPointService = (ShippingPointServiceLocalInterface) initContext.lookup(LocalServices.APPLICATION_NAME + "/ShippingPointServiceBean!com.crh.logon.business.services.localinterfaces.ShippingPointServiceLocalInterface");
	    	this.monitoringService = (MonitoringTablesServiceLocalInterface) initContext.lookup(LocalServices.APPLICATION_NAME + "/MonitoringTablesServiceBean!com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface");
    	} 
		catch (NamingException e) {
			LOG.error(e.getMessage());
		}
    }
    
    public List<OrderWrapper> getOrderList(HttpServletRequest request) {
		// [[
		List<OrderWrapper> result = null;
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getOrderList", params);
		}
		// ]]
		try {
			initServiceBeans();
			Locale userLocale = RequestUtils.getUserLocale(request, null);
			DispoForm dispoForm = (DispoForm) request.getSession().getAttribute("order_raildispo_DispoForm");
			RailDataProvider railDataProvider = new RailDataProvider(userService, dispoService, unitOfMeasurementConverterService, 
					 												 loadingService, shippingPointService, monitoringService, 
					 												 getUserTimeZone(), dispoForm, userLocale);
			return result = railDataProvider.getOrderList();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
    public EtranResult saveEtranToWagon(Long wagonId, String etranReturn, String etranOutbound, HttpServletRequest request) {
		// [[
		Object[][] params = {{"wagonId", wagonId},
							{"etranReturn", etranReturn},
							{"etranOutbound", etranOutbound}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveEtranToWagon", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_R);
			
			Locale locale = request.getLocale();
			etranReturn = etranReturn.toUpperCase(locale);
			etranOutbound = etranOutbound.toUpperCase(locale);
			
			DispoService dispoService = getServiceLocator().getDispoService();
			dispoService.setWagonEtran(wagonId, etranReturn, etranOutbound);
			
			return new EtranResult(etranReturn, etranOutbound);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    public EtranResult saveEtranToOrder(Long orderNumber, String etranReturn, String etranOutbound, HttpServletRequest request) {
		// [[
		Object[][] params = {{"orderNumber", orderNumber},
							{"etranReturn", etranReturn},
							{"etranOutbound", etranOutbound}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "saveEtranToOrder", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_R);
			
			Locale locale = request.getLocale();
			etranReturn = etranReturn.toUpperCase(locale);
			etranOutbound = etranOutbound.toUpperCase(locale);
			
			DispoService dispoService = getServiceLocator().getDispoService();
			dispoService.setOrderEtran(orderNumber, etranReturn, etranOutbound);
			
			return new EtranResult(etranReturn, etranOutbound);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    public void createAssignment(Long wagonId, Long orderNr, Date wagonReturnDate, HttpServletRequest request) {
		// [[
		Object[][] params = {{"wagonId", wagonId},
							{"orderNr", orderNr},
							{"wagonReturnDate", wagonReturnDate},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createAssignment", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
			DispoForm dispoForm = (DispoForm) request.getSession().getAttribute("order_raildispo_DispoForm");
			if (wagonReturnDate == null){
				wagonReturnDate = dispoForm.getDate();
				if (wagonReturnDate.before(new Date())){
					wagonReturnDate = new Date();
				}
			}
			getServiceLocator().getOrderService().createAssignmentForRailWithOrderDispatchedOrLoaded(wagonId,  orderNr,
					wagonReturnDate, 0, 0, 0);
			
			
		}
	
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }    
    
    public List<WagonWrapper> getWagonList(HttpServletRequest request) {
		// [[
		List<WagonWrapper> result = null;
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getWagonList", params);
		}
		// ]]
		try {
			initServiceBeans();
			DispoForm dispoForm = (DispoForm) request.getSession().getAttribute("order_raildispo_DispoForm");
			Locale userLocale = RequestUtils.getUserLocale(request, null);
			RailDataProvider railDataProvider = new RailDataProvider(userService, dispoService, unitOfMeasurementConverterService, 
					 												loadingService, shippingPointService, monitoringService, 
					 												getUserTimeZone(), dispoForm, userLocale);
			return result = railDataProvider.getWagonList();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
    
	public void setLoadWeights(Map<String, String> tareWeights, Map<String, String> grossWeights,
			boolean includeRailTransmitted) {
		// [[
		Object[][] params = {{"tareWeights", tareWeights},
							{"grossWeights", grossWeights},
							{"includeRailTransmitted", includeRailTransmitted + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setLoadWeights", params);
		}
		// ]]
		try {
			if (includeRailTransmitted){
				Authorization.authorize(Authorization.RAIL_DISPATCHING_SW);
			}
			else {
				Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
			}
			getServiceLocator().getDispoService().updateRailLoad(tareWeights, grossWeights, 
					includeRailTransmitted);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
     }
    
    public void cancelAssignment(long[] assignmentIds, boolean includeRailTransmitted) {
		// [[
		Object[][] params = {{"assignmentIds", assignmentIds},
							{"includeRailTransmitted", includeRailTransmitted + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "cancelAssignment", params);
		}
		// ]]
		try {
			if (includeRailTransmitted){
				Authorization.authorize(Authorization.RAIL_DISPATCHING_SW);
			}
			else {
				Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
			}
			
			for (int i = 0; i < assignmentIds.length; i++) {
				getServiceLocator().getDispoService().cancelAssignment(assignmentIds[i], null, null, includeRailTransmitted);
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
    
    public void transmitToSAP(long[] assignmentIds, HttpServletRequest request) {
		// [[
		Object[][] params = {{"assignmentIds", assignmentIds},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "transmitToSAP", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
			
			for (int i = 0; i < assignmentIds.length; i++) {
				getServiceLocator().getDispoService().transmitToRailCompany(assignmentIds[i]);
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
    
    public double checkBimodalLoadBalanceAssignment(long assignmentId) {
		// [[
		double result = 0.0;
		Object[][] params = {{"assignmentId", assignmentId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "checkBimodalLoadBalanceAssignment", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
		   	
			return result = getServiceLocator().getDispoService().checkBimodalLoadBalanceAssignment(assignmentId);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
    
	public void setUnloaded(long assignmentId) {
		// [[
		Object[][] params = {{"assignmentId", assignmentId + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setUnloaded", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
   	
			getServiceLocator().getDispoService().setRailUnloaded(assignmentId);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
   			
	}
	
	public void setAtShipto(long[] assignmentIds) {
		// [[
		Object[][] params = {{"assignmentIds", assignmentIds}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setAtShipto", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
   	
			for (int i = 0; i < assignmentIds.length; i++) {
				getServiceLocator().getDispoService().enterUnloadingSite(assignmentIds[i]);
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

    public ListRange fetchContainerList(
    		int start, 
    		int count, 
    		String orderBy,
    		String query,
    		HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchContainerList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_R);
			
			List<DispoWContainerListEntity> list = getServiceLocator().getDispoService().fetchContainers(start, count, query, RequestUtils.getUserLocale(request, null).getLanguage());
			
			ListRange listRange = new ListRange();
			listRange.setData(list.toArray());
			listRange.setTotalSize(getServiceLocator().getDispoService().countContainers(query, RequestUtils.getUserLocale(request, null).getLanguage()));
			
			return result = listRange;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
    
    public List<DispoPlantTrackListEntity> fetchTrackList(String query, HttpServletRequest request) {
		// [[
		List<DispoPlantTrackListEntity> result = null;
		Object[][] params = {{"query", query},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchTrackList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
			DispoForm dispoForm = (DispoForm) request.getSession().getAttribute("order_raildispo_DispoForm");
			
			final List<DispoPlantTrackListEntity> tracks = new ArrayList<DispoPlantTrackListEntity>();
			DispoPlantTrackListEntity pt = new DispoPlantTrackListEntity();
			pt.setTrackName("-");
			pt.setTrackNameTx(Message.lookup(RequestUtils.getUserLocale(request, null), "raildispo.texte.NoTrack"));
			tracks.add(pt);
			
			
			tracks.addAll(dispoForm.getTracks());
			
			return result = tracks;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
   	
    }
    
    public List<PosWrapper> fetchPosList(String wagonNo, HttpServletRequest request) {
		// [[
		List<PosWrapper> result = null;
		Object[][] params = {{"wagonNo", wagonNo},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPosList", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
			
			DispoMaxWagonPosEntity pos = getServiceLocator().getDispoService().fetchMaxTrackPos(wagonNo);
			result = new ArrayList<PosWrapper>();
			result.add(new PosWrapper(null, "-"));
			if (pos.getCurrentPlantTrackCd() != null) {
				int i = 1;
				if (pos.getMaxTrackPositionNo() != null) {
					for (i = 1; i <= pos.getMaxTrackPositionNo().intValue(); i++){
						result.add(new PosWrapper(Long.valueOf(i), Integer.toString(i)));
					}
				}
				if (pos.getCurrentTrackPositionNo() == null) {
					result.add(new PosWrapper(Long.valueOf(i), Integer.toString(i)));
				}
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
    
    public void setWagonTrackPos(String wagonNo[], String spos, HttpServletRequest request) {
		// [[
		Object[][] params = {{"wagonNo", wagonNo},
							{"spos", spos},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setWagonTrackPos", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
			
			Long pos = null;
			try{
				pos = Long.parseLong(spos);
			}
			catch (NumberFormatException e) {
				// do nothing, pos = null
			}
			
			getServiceLocator().getDispoService().setWagonTrackPos(wagonNo, pos);
  	
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    public void setWagonTrack(String[] wagonNo, String sTrackCd, HttpServletRequest request) {
		// [[
		Object[][] params = {{"wagonNo", wagonNo},
							{"sTrackCd", sTrackCd},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "setWagonTrack", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
			
			Long trackCd = null;
			try{
				trackCd = Long.parseLong(sTrackCd);
			}
			catch (NumberFormatException e) {
				// do nothing, trackCd = null
			}
			
			getServiceLocator().getDispoService().setWagonTrack(wagonNo, trackCd);
  	
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }    
        
    public void adjustStellzeit(Long assignmentId, HttpServletRequest request) {
		// [[
		Object[][] params = {{"assignmentId", assignmentId},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "adjustStellzeit", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.RAIL_DISPATCHING_W);
			
			getServiceLocator().getOrderService().adjustStellzeit(assignmentId);
  	
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


