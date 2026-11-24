/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.raildispo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.utils.OrderWrapper;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.order.web.utils.BooleanBuffer;
import com.crh.logon.persistence.entities.views.DispoPlantTrackListEntity;
import com.crh.logon.persistence.entities.views.RailPlantEntity;
import com.crh.logon.persistence.entities.views.RailPlantSpEntity;

/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_raildispo_DispoForm"
 */
public class DispoForm extends ActionForm {

    private static final long serialVersionUID = 1;

    private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DispoForm.class);

    public static final String FORM_NAME = "order_raildispo_DispoForm";

	private static final int MAX_SP_FOR_NORMAL_PLANT = 5;
    
    private List<RailPlantEntity> plants;
    private List<RailPlantSpEntity> shippingpoints;
    private List<DispoPlantTrackListEntity> tracks;
	private BooleanBuffer showNoTrack;
	private BooleanBuffer statusIncoming;
	private BooleanBuffer statusAtSp;
	private BooleanBuffer statusLoaded;
	private BooleanBuffer statusTransmitted;
    private Map<String, Set<Long>> selctedShippingpointsCache;
    private Map<String, Set<Long>> selctedTracksCache;
    private Map<String, BooleanBuffer> showNoTrackCache;
	private Map<String, BooleanBuffer> statusIncomingCache;
	private Map<String, BooleanBuffer> statusAtSpCache;
	private Map<String, BooleanBuffer> statusLoadedCache;
	private Map<String, BooleanBuffer> statusTransmittedCache;
	
	private String formAction;
    
    /* user data */ 
    /* list of the IDs of the selected shippingpoints */
    private Set<Long> selectedShippingpoints;
    private Set<Long> selectedTracks;
    private String selectedPlant;
    private Date date; 
    private float heightFactor;
    
    private Boolean dbIsEmpty;

	private List<OrderWrapper> orderList;
	
	private boolean applyUserDefaultSettings = false;

	private TimeZone userTimeZone;
	
	private String orderNumberFilter;
	private String wagonNumberFilter;
	private String productNameFilter;
    private Boolean railDispoFiltering;
	/**
     * Default constructor.
     */
    public DispoForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "DispoForm");
		}
		// ]]
		try {
	        init();
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    /**
     * Initializes the form. This has the effect of clearing all the input
     * fields on the order.web form.
     */
    private void init() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "init");
		}
		// ]]
		try {
			selectedShippingpoints = new HashSet<Long>();
			selectedPlant = null;
			plants = null;
			shippingpoints = null;
			tracks = null;
			date = new Date();
			heightFactor = -1;
			orderList = new LinkedList<OrderWrapper>();
			selctedShippingpointsCache = new HashMap<String, Set<Long>>();
			selctedTracksCache = new HashMap<String, Set<Long>>();
			showNoTrackCache = new HashMap<String, BooleanBuffer>();
			statusIncomingCache = new HashMap<String, BooleanBuffer>();
			statusAtSpCache = new HashMap<String, BooleanBuffer>();
			statusLoadedCache = new HashMap<String, BooleanBuffer>();
			statusTransmittedCache = new HashMap<String, BooleanBuffer>();
	
			showNoTrack = new BooleanBuffer(Boolean.TRUE);
			statusIncoming = new BooleanBuffer(Boolean.TRUE);
			statusAtSp = new BooleanBuffer(Boolean.TRUE);
			statusLoaded = new BooleanBuffer(Boolean.TRUE);
			statusTransmitted = new BooleanBuffer(Boolean.TRUE);	
			
			dbIsEmpty = new Boolean(Boolean.FALSE);
			
			orderNumberFilter = null;
			wagonNumberFilter = null;
			productNameFilter = null;
			railDispoFiltering = Boolean.valueOf(Boolean.FALSE);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
    
    /**
     * Clears out the variables from the struts form 
     * which stores the user preferences (defaults).
     */
    public void clearDefaults() {
    	formAction = null;
    	
		applyUserDefaultSettings = false;
		
		selectedPlant = null;
		selectedShippingpoints = new HashSet<Long>();
		selectedTracks = new HashSet<Long>();
		showNoTrack = new BooleanBuffer(Boolean.TRUE);
		
		orderNumberFilter = null;
		wagonNumberFilter = null;
		productNameFilter = null;
    }

    /**
     * Override struts method.
     * Note: this is not associated with or invoked by the reset button in the
     * ordermgr.web form, but rather is called on every request before the struts action
     * method gets control and, more importantly, before the request parameters
     * are automatically copied into the form. The primary use is to reset
     * checkboxes and struts multiboxes and the like for which the browser only
     * sends the form parameters when they are set.
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		// [[
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "reset", params);
		}
		// ]]
		try {
			if (request.getParameter("setShippingpoints") != null){
				selectedShippingpoints.clear();
			}
			if (request.getParameter("setTracks") != null){
				selectedTracks.clear();
			}
			if (request.getParameter("setShowNoTrack") != null){
				showNoTrack.setValue(Boolean.FALSE);
			}
			if (request.getParameter("setStatus") != null){
				statusIncoming.setValue(Boolean.FALSE);
				statusAtSp.setValue(Boolean.FALSE);
				statusLoaded.setValue(Boolean.FALSE);
				statusTransmitted.setValue(Boolean.FALSE);
			}
			
			formAction = null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }

    /**
     * Override struts method.
     * Perform form-specific validation of input parameters.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		// [[
		ActionErrors result = null;
		Object[][] params = {{"mapping", mapping},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "validate", params);
		}
		// ]]
		try {
			
	        ActionErrors errors = super.validate(mapping, request);
			
	        /* no need to validate if 'delete' button was pressed */
	        if (request.getParameter("delete") != null) {
	            return result = null;
	        }
	        if (errors == null) {
	            errors = new ActionErrors();
	        }
			
	        return result = errors;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
    }
	
    public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
    public String getStringDate() {
		return TimeZone.getAppTimeZone().fmtAsDayMonthYear4(date);
	}

	public void setStringDate(String date) throws ParseException {
		this.date = TimeZone.getAppTimeZone().parseDayMonthYear4(date);
	}

	public float getHeightFactor() {
		return heightFactor;
	}

	public void setHeightFactor(float heightFactor) {
		this.heightFactor = heightFactor;
	}

	public List<RailPlantEntity> getPlants() {
		return plants;
	}

	public void setPlants(List<RailPlantEntity> plants) {
		this.plants = plants;
	}

	public List<RailPlantSpEntity> getShippingpoints() {
		return shippingpoints;
	}

	public void setShippingpoints(List<RailPlantSpEntity> shippingpoints) {
		this.shippingpoints = shippingpoints;
	}

	public Set<Long> getSelectedShippingpoints() {
		return selectedShippingpoints;
	}

	public void setSelectedShippingpoints(Set<Long> selectedShippingpoints) {
		this.selectedShippingpoints = selectedShippingpoints;
	}
	
	/**
	 * Initializes the selected shipping points cache for the selected plant.
	 * This method is called when the screen is accessed on the first time.
	 * 
	 * If the selctedShippingpointsCache is not empty (was initialized before), 
	 * it calls the restoreSelectedShippingpointsForSelectedPool() to restore 
	 * previously set shipping points.
	 */
	public void initSelectedShippingpointsCache() {
		if (selctedShippingpointsCache.isEmpty()) {
			selctedShippingpointsCache.put(selectedPlant, selectedShippingpoints);
		}
		else {
			restoreSelectedShippingpointsForSelectedPool();
		}
	}

	public void restoreSelectedShippingpointsForSelectedPool() {
		Set<Long> nsc = selctedShippingpointsCache.get(selectedPlant);

		if (nsc == null) {
			nsc = new HashSet<Long>();
			selctedShippingpointsCache.put(selectedPlant, nsc);
			if (shippingpoints != null){
				for (RailPlantSpEntity rps : shippingpoints) {
					nsc.add(rps.getShippingpointId());
				}
			}
		}
		selectedShippingpoints = nsc;
	}
	
	/**
	 * Initializes the selected tracks cache for the selected plant.
	 * This method is called when the screen is accessed on the first time.
	 * 
	 * If the selctedTracksCache is not empty (was initialized before), 
	 * it calls the restoreSelectedTracksForSelectedPool() to restore 
	 * previously set tracks.
	 */
	public void initselectedTracksCache() {
		if (selctedTracksCache.isEmpty()) {
			selctedTracksCache.put(selectedPlant, selectedTracks);
		}
		else {
			restoreSelectedTracksForSelectedPool();
		}		
	}
	
	public void initShowNoTrackCache() {
		final boolean isEnteringExitingEnabled = LOG.isEnteringExitingEnabled();
		if (isEnteringExitingEnabled) {
			LOG.entering(this, "initShowNoTrackCache");
		}
		try {
			if(showNoTrackCache.isEmpty()) {
				showNoTrackCache.put(selectedPlant, showNoTrack);
			}
		}
		finally {
			if (isEnteringExitingEnabled) {
				LOG.exiting();
			}
		}
	}
	
	public void restoreSelectedTracksForSelectedPool(){
		Set<Long> nsc = selctedTracksCache.get(selectedPlant);

		if (nsc == null) {
			nsc = new HashSet<Long>();
			selctedTracksCache.put(selectedPlant, nsc);
		}
		
		showNoTrack = showNoTrackCache.get(selectedPlant);
		if (showNoTrack == null) {
			showNoTrack = new BooleanBuffer(Boolean.FALSE);
			showNoTrackCache.put(selectedPlant, showNoTrack);
		}		
		selectedTracks = nsc;
		
	}
	
	public void initSelectedStatus(){
		
		statusIncoming = statusIncomingCache.get(selectedPlant);
		if (statusIncoming == null) {
			statusIncoming = new BooleanBuffer(Boolean.FALSE);
			statusIncomingCache.put(selectedPlant, statusIncoming);
		}
		statusAtSp = statusAtSpCache.get(selectedPlant);
		if (statusAtSp == null) {
			statusAtSp = new BooleanBuffer(Boolean.TRUE);
			statusAtSpCache.put(selectedPlant, statusAtSp);
		}
		statusLoaded = statusLoadedCache.get(selectedPlant);
		if (statusLoaded == null) {
			statusLoaded = new BooleanBuffer(shippingpoints.size() <= MAX_SP_FOR_NORMAL_PLANT);
			statusLoadedCache.put(selectedPlant, statusLoaded);
		}
		statusTransmitted = statusTransmittedCache.get(selectedPlant);
		if (statusTransmitted == null) {
			statusTransmitted = new BooleanBuffer(Boolean.FALSE);
			statusTransmittedCache.put(selectedPlant, statusTransmitted);
		}
	}

	public boolean getSelectedShippingpointCheckbox(int index){
		if (shippingpoints != null){
			RailPlantSpEntity sp = shippingpoints.get(index);
			return selectedShippingpoints.contains(sp.getShippingpointId());
		}
		else {
			return false;
		}
	}

	public boolean getSelectedTrackCheckbox(int index) {
		if (tracks != null){
			DispoPlantTrackListEntity pt = (DispoPlantTrackListEntity)tracks.get(index);
			return selectedTracks.contains(pt.getPlantTrackCd());
		}
		else {
			return false;
		}
	}

	public void setSelectedShippingpointCheckbox(int index, boolean value){
		if (shippingpoints != null){
			RailPlantSpEntity sp = shippingpoints.get(index);
			selectedShippingpoints.add(sp.getShippingpointId());
		}
	}	

	public void setSelectedTrackCheckbox(int index, boolean value){
		if (tracks != null){
			DispoPlantTrackListEntity pt = (DispoPlantTrackListEntity)tracks.get(index);
			selectedTracks.add(pt.getPlantTrackCd());
		}
	}	

	public String getSelectedShippingpointsString(){
		if (selectedShippingpoints.size() == 0){
			return "none";
		}
		else if (selectedShippingpoints.size() == shippingpoints.size()){
			return "all";
		}
		else {
			String res = "";
			for (RailPlantSpEntity rps : shippingpoints) {
				if (selectedShippingpoints.contains(rps.getShippingpointId())){
					res += rps.getShippingpointName() + " ";
				}
			}
			return res;
		}
	}

	public String getSelectedTracksString() {
		if (selectedTracks.isEmpty()) {
			return "none";
		}
		else if (selectedTracks.size() == tracks.size()) {
			return "all";
		}
		else {
			final StringBuilder res = new StringBuilder();
			for (DispoPlantTrackListEntity pt : tracks) {
				if (selectedTracks.contains(pt.getPlantTrackCd())) {
					res.append(pt.getTrackName());
				}
			}
			return res.toString();
		}
	}
	
	public Set<Long> getSelectedShippinpointsStationIds() {
		Set<Long> stations = new HashSet<Long>();
		if (shippingpoints != null) {
			for (RailPlantSpEntity rps : shippingpoints) {
				if (selectedShippingpoints.contains(rps.getShippingpointId())) {
					stations.add(rps.getRailwayStationId());
				}
			}
		}
		return stations;
	}

	public List<RailPlantSpEntity> getSelectedShippinpointsStations() {
		List<RailPlantSpEntity> stations = new ArrayList<RailPlantSpEntity>();
		if (shippingpoints != null) {
			for (RailPlantSpEntity rps : shippingpoints) {
				if (selectedShippingpoints.contains(rps.getShippingpointId())) {
					stations.add(rps);
				}
			}
		}
		return stations;
	}

	public RailPlantEntity getSelectedRailPlant() {
		for (RailPlantEntity railPlantEntity : plants) {
			if (railPlantEntity.getPlantCd().equals(selectedPlant)) {
				return railPlantEntity;
			}
		}
		return null;
	}

	public List<DispoPlantTrackListEntity> getSelectedPlantTracks() {
		List<DispoPlantTrackListEntity> plantTracks = new ArrayList<DispoPlantTrackListEntity>();
		if (tracks != null) {
			for (DispoPlantTrackListEntity plantTrack : tracks) {
				if (selectedTracks.contains(plantTrack.getPlantTrackCd())) {
					plantTracks.add(plantTrack);
				}
			}
		}
		return plantTracks;
	}

	public String getSelectedPlant() {
		return selectedPlant;
	}

	public void setSelectedPlant(String selectedPlant) {
		this.selectedPlant = selectedPlant;
	}

	public void setOrderList(List<OrderWrapper> orderList) {
		this.orderList = orderList;
		Collections.sort(this.orderList);
	}

	public List<OrderWrapper> getOrderList() {
		return orderList;
	}

	public List<DispoPlantTrackListEntity> getTracks() {
		return tracks;
	}

	public void setTracks(List<DispoPlantTrackListEntity> tracks) {
		this.tracks = tracks;
	}

	public Set<Long> getSelectedTracks() {
		return selectedTracks;
	}

	public void setSelectedTracks(Set<Long> selectedTracks) {
		this.selectedTracks = selectedTracks;
	}

	public boolean getShowNoTrack() {
		return showNoTrack.getValue();
	}
	
	public void setShowNoTrack(boolean showNoTrack) {
		this.showNoTrack.setValue(showNoTrack);
	}

	public boolean getStatusIncoming() {
		return statusIncoming.getValue();
	}

	public void setStatusIncoming(boolean statusIncoming) {
		this.statusIncoming.setValue(statusIncoming);
	}

	public boolean getStatusAtSp() {
		return statusAtSp.getValue();
	}

	public void setStatusAtSp(boolean statusAtSp) {
		this.statusAtSp.setValue(statusAtSp);
	}

	public boolean getStatusLoaded() {
		return statusLoaded.getValue();
	}

	public void setStatusLoaded(boolean statusLoaded) {
		this.statusLoaded.setValue(statusLoaded);
	}

	public boolean getStatusTransmitted() {
		return statusTransmitted.getValue();
	}

	public void setStatusTransmitted(boolean statusTransmitted) {
		this.statusTransmitted.setValue(statusTransmitted);
	}

	public boolean isDbIsEmpty() {
		return dbIsEmpty;
	}

	public void setDbIsEmpty(boolean dbIsEmpty) {
		this.dbIsEmpty = dbIsEmpty;
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	public boolean isApplyUserDefaultSettings() {
		return applyUserDefaultSettings;
	}

	public void setApplyUserDefaultSettings(boolean applyUserDefaultSettings) {
		this.applyUserDefaultSettings = applyUserDefaultSettings;
	}

	public TimeZone getUserTimeZone() {
		return userTimeZone;
	}

	public void setUserTimeZone(TimeZone userTimeZone) {
		this.userTimeZone = userTimeZone;
	}

	public String getOrderNumberFilter() {
		return orderNumberFilter;
	}

	public void setOrderNumberFilter(String orderNumberFilter) {
		this.orderNumberFilter = orderNumberFilter;
	}

	public String getWagonNumberFilter() {
		return wagonNumberFilter;
	}

	public void setWagonNumberFilter(String wagonNumberFilter) {
		this.wagonNumberFilter = wagonNumberFilter;
	}

	public String getProductNameFilter() {
		return productNameFilter;
	}

	public void setProductNameFilter(String productNameFilter) {
		this.productNameFilter = productNameFilter;
	}

	public Boolean getRailDispoFiltering() {
		return railDispoFiltering;
	}

	public void setRailDispoFiltering(Boolean railDispoFiltering) {
		this.railDispoFiltering = railDispoFiltering;
	}
	
	
}
