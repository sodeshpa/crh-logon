/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.forms.dispo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.utils.LogonPackageHandlingUtil;
import com.crh.logon.business.services.utils.ReroutingOrderWrapper;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.utils.TourPart;
import com.crh.logon.persistence.entities.common.AssignmentEntity;
import com.crh.logon.persistence.entities.common.AssignmentPartEntity;
import com.crh.logon.persistence.entities.common.ExtOrderEntity;
import com.crh.logon.persistence.entities.common.ExtSiteEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.SimpleVehicleEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
import com.crh.logon.persistence.entities.refdata.AssignmentPartTypeTxEntity;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.utils.time.DateTimeUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
/**
 * Used by the JSP which provides both the input form for searches
 * and display of the search results.
 *
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.form name="order_dispo_AssignmentEditForm"
 */
public class AssignmentEditForm extends ActionForm {
	
	public static final String POSITION_HOMEBASE = "Homebase";
	public static final String POSITION_SHIPPINGPOINT = "Sp";
	public static final String POSITION_SHIPTO = "Shipto";
	public static final String POSITION_NONE = "None";
	public static final String POSITION_COORDS = "Coords";
	public static final String POSITION_EXTERNAL = "External";

    public static final Long ASSIGNMENT_PART_STATUS_NEW = new Long(0);
    public static final Long ASSIGNMENT_PART_STATUS_TRANSMITTED = new Long(1);
    public static final Long ASSIGNMENT_PART_STATUS_CONFIRMED = new Long(2);
    public static final Long ASSIGNMENT_PART_STATUS_CANCELLED = new Long(3);
    
	//TODO: get this from system defaults
	private static final long DEFAULT_LOADING_TIME = 20;

	private static final long serialVersionUID = 1L;

	private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(AssignmentEditForm.class);

	
	// ****************** needed for refactoring for tour
	
	private AssignmentEntity assignment;
	
//	private List assignmentPartList;
	
	private List<TourPart> tourPartList;
	
	private HashMap assignmentPartTypeTxMap;
	
	private int currentIdx;
	
	private HashMap materialDescriptionMap;
	
	private int selectedPartId;
	
	private String locationType;
	
	private String assignmentPartShippingpointId;
	
	private String assignmentPartShiptoSapNr;
	
	private String assignmentPartCoordLon;
	
	private String assignmentPartCoordLat;
	
	private String assignmentPartCalcStart;
	
	private String assignmentPartPlannedDepartureDate;
	
	private String assignmentPartPlannedDepartureTime;
	
	private String assignmentPartRealDepartureDate;
	private String assignmentPartRealDepartureTime;
	
	private String assignmentPartPlannedArrivalDate;
	private String assignmentPartPlannedArrivalTime;
	
	private String assignmentPartRealArrivalDate;
	private String assignmentPartRealArrivalTime;

	private String assignmentPartChanged;
	
	private String assignmentPartDistance;
	
	private List timeModifierList;
	
	private String externalMaterial;
	
	private String externalQuantity;
	
	private String externalUom;
	
	private String siteName;
	private String siteLoadUnload;
	private String siteContactPersonTxt;
	private String siteStreetHouseNo;
	private String siteCountry;
	private String sitePostalCode;
	private String siteCity;
	
    // Map of UomCd -> UomBSEntity
    private Map uomMap;
    
    private List uomList;
	// ******************
	
	
	
    // input from the user
    private long assignmentId;
    private long loadingTime;
    private long unloadingTime;
    
	//private AssignmentEntity assignment;
	private String materialQuantity;
	private String materialName;
	private String materialUomCd;

	private HashMap partMap;
	private HashSet calcStart;
	
	private Date baseDate;
	
	private List materialUoms;
	private HashMap materialUomMap;
	private HashMap materialUomNameMap;
	
	private long maxSeqno;

	String[] apLocationType;
	Long[] apShippingpointIds;
	Long[] apShiptoIds;
	private Integer recalculateIdx;
	
	// used for loading order detail dialog
	private long orderId;
	private long extOrderId;
	private Long orderNumber;
	
	/**
	 * LOGON-6664 tells if orders are crossing midnight
	 */
	private boolean acrossMidnight;
	
	private List<ReroutingOrderWrapper> reroutingOrderWrapperList;
	
	/**
     * Default constructor.
     */
    public AssignmentEditForm() {
		// [[
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "AssignmentEditForm");
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
			assignmentId = -1;
			assignment = null;
			partMap = new HashMap();
			loadingTime = DEFAULT_LOADING_TIME;
			unloadingTime = DEFAULT_LOADING_TIME;
			calcStart = new HashSet();
			initApLocationType();
			baseDate = null;
			apShippingpointIds = new Long[6];
			apShiptoIds = new Long[6];
			materialQuantity = null;
			materialName = null;
			materialUomCd = null;
			materialUoms = null;
			materialUomMap = new HashMap();
			materialUomNameMap = new HashMap();
			assignmentPartTypeTxMap = new HashMap();
			//			assignmentPartList = new ArrayList();
			tourPartList = new ArrayList();
			selectedPartId = 0;
			assignmentPartPlannedDepartureDate = null;
			assignmentPartPlannedDepartureTime = null;
			assignmentPartRealDepartureDate = null;
			assignmentPartRealDepartureTime = null;
			assignmentPartPlannedArrivalDate = null;
			assignmentPartPlannedArrivalTime = null;
			assignmentPartRealArrivalDate = null;
			assignmentPartRealArrivalTime = null;
			assignmentPartChanged = "false";
			assignmentPartDistance = null;
			timeModifierList = new ArrayList();
			recalculateIdx = null;
			siteName = null;
			siteLoadUnload = null;
			siteContactPersonTxt = null;
			siteStreetHouseNo = null;
			siteCountry = null;
			sitePostalCode = null;
			siteCity = null;
			reroutingOrderWrapperList = new ArrayList();
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
			initApLocationType();
			apShippingpointIds = new Long[6];
			apShiptoIds = new Long[6];
			assignmentId = -1;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }    
    
    private void copyApPosition(int fromPartType, int toPartType){
    	AssignmentPartEntity fromApe = (AssignmentPartEntity)partMap.get(new Long(fromPartType));
    	AssignmentPartEntity toApe = (AssignmentPartEntity)partMap.get(new Long(toPartType));
    	apShippingpointIds[toPartType] = apShippingpointIds[fromPartType];
    	toApe.setShippingpoint(fromApe.getShippingpoint());
		apShiptoIds[toPartType] = apShiptoIds[fromPartType];
		toApe.setShipto(fromApe.getShipto());
		toApe.setCoordLat(fromApe.getCoordLat());
		toApe.setCoordLon(fromApe.getCoordLon());
		toApe.setSiteCity(fromApe.getSiteCity());
		toApe.setSiteContactPersonTxt(fromApe.getSiteContactPersonTxt());
		toApe.setSiteCountry(fromApe.getSiteCountry());
		toApe.setSiteLoadUnload(fromApe.getSiteLoadUnload());
		toApe.setSiteName(fromApe.getSiteName());
		toApe.setSitePostalCode(fromApe.getSitePostalCode());
		toApe.setSiteStreetHouseNo(fromApe.getSiteStreetHouseNo());
		toApe.setLnfShiptoFlag(fromApe.getLnfShiptoFlag());
    }    
    
	public long getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(long assignmentId) {
		this.assignmentId = assignmentId;
	}

	public AssignmentEntity getAssignment() {
		return assignment;
	}

	private void initApLocationType(){
		String[] newApLocationType = {POSITION_NONE, POSITION_NONE, POSITION_NONE,
				POSITION_NONE, POSITION_NONE, POSITION_NONE};
		apLocationType = newApLocationType;
	}

	
	public void setAssignment(AssignmentEntity assingment, ShiptoEntity shipto) {
		HashMap newPartMap = new HashMap();
		this.baseDate = null;
		Iterator i = assingment.getAssignmentParts().iterator();
		loadingTime = 0;
		unloadingTime = 0;
		maxSeqno = 0;
		while (i.hasNext()){
			AssignmentPartEntity ape = (AssignmentPartEntity)i.next();
			newPartMap.put(ape.getAssignmentPartType().getAssignmentPartTypeCd(), ape);
			if ( (this.baseDate == null) && (ape.getPlannedStart() != null) ) {
				this.baseDate = DateTimeUtils.cleanTimeWithDefaultTimeZone(ape.getPlannedStart());
			}
			if (ape.getOrder() != null) {
				OrderEntity order = ape.getOrder();
				List orderItems = (List) LogonPackageHandlingUtil.getMaterialItems(order);
				OrderItemEntity orderItem = (OrderItemEntity) orderItems.get(0);

				loadingTime += orderItem.getPlannedLoadingTimeMin();
				unloadingTime += order.getPlannedUnloadingTimeMin();
			}
			else {
				// External order
				ExtOrderEntity extOrderEntity = ape.getExtOrder();				
				if(extOrderEntity != null && extOrderEntity.getLoadingExtSite() != null) {
					ExtSiteEntity extSiteEntity = extOrderEntity.getLoadingExtSite();
					loadingTime += extSiteEntity.getLoadUnloadTimeMin();
					unloadingTime += extSiteEntity.getLoadUnloadTimeMin();
				}
			}

			if (ape.getAssignmentPartSeqNo().longValue() > maxSeqno) {
				maxSeqno = ape.getAssignmentPartSeqNo().longValue();
			}
		}
		calcStart = new HashSet();
		partMap = newPartMap;
		this.assignment = assingment;
	}


	public HashMap getPartMap() {
		return partMap;
	}

	public long getLoadingTime() {
		return loadingTime;
	}

	public long getUnloadingTime() {
		return unloadingTime;
	}

	public Long[] getApShippingpointIds() {
		return apShippingpointIds;
	}

	public Long[] getApShiptoIds() {
		return apShiptoIds;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getMaterialUomCd() {
		return materialUomCd;
	}

	public void setMaterialUomCd(String materialUomCd) {
		this.materialUomCd = materialUomCd;
		assignment.setMaterialUom((UomBsEntity)materialUomMap.get(materialUomCd));
	}

	public String getMaterialQuantity() {
		return materialQuantity;
	}

	public void setMaterialQuantity(String materialQuantity) {
		this.materialQuantity = materialQuantity;
	}

	public List getMaterialUoms() {
		return materialUoms;
	}

	public void setMaterialUoms(List materialUoms, List materialUomBs) {
		this.materialUoms = materialUoms;
		HashMap map = new HashMap();
		if (materialUomBs != null){
			Iterator i = materialUomBs.iterator();
			while (i.hasNext()){
				UomBsEntity uom = (UomBsEntity)i.next();
				map.put(uom.getUomCd(), uom);
			}
		}
		materialUomMap = map;
		map = new HashMap();
		if (materialUoms != null){
			Iterator i = materialUoms.iterator();
			while (i.hasNext()){
				UomTxEntity uom = (UomTxEntity)i.next();
				map.put(uom.getUomTxPK().getUomBs().getUomCd(), uom.getTxt());
			}
		}
		materialUomNameMap = map;
		
	}
	
	public HashMap getAssignmentPartTypeTxMap() {
		return assignmentPartTypeTxMap;
	}
	
	public List getTourPartList() {
		return tourPartList;
	}

	public void setTourPartList(List<TourPart> tourPartList) {
		this.tourPartList = tourPartList;
	}

	public void initTourPartDateTimeFormattingFlags() {
		if (tourPartList != null) {
			boolean isAcrossMidnight = isAcrossMidnight();
			for (TourPart part: tourPartList) {
				part.setAcrossMidnight(isAcrossMidnight);
			}
		}
	}

	public void setAssignmentPartTypeTxMap(HashMap assignmentPartTypeTxMap) {
		this.assignmentPartTypeTxMap = assignmentPartTypeTxMap;
	}
	
	public void setAssignmentPartTypeTxMap(List assignmentPartTypeTxList) {
		HashMap map = new HashMap();
		ListIterator txIt = assignmentPartTypeTxList.listIterator();
		while (txIt.hasNext()){
			AssignmentPartTypeTxEntity txEntity = (AssignmentPartTypeTxEntity) txIt.next();
			map.put(txEntity.getAssignmentPartTypeTxPK().getAssignmentPartTypeBs().getAssignmentPartTypeCd(), txEntity);
		}
		this.assignmentPartTypeTxMap = map;
	}
	
	public Date getPlannedDepartureForCurrentIdx() {
		if ( tourPartList.size() > currentIdx) { 
			return ((TourPart) tourPartList.get(currentIdx)).getPlannedDeparture();
		}
		return null;
	}
	
	public Date getRealDepartureForCurrentIdx() {
		if ( tourPartList.size() > currentIdx) {
			return ((TourPart) tourPartList.get(currentIdx)).getRealDeparture();
		}
		return null;
	}

	public Date getPlannedArrivalForCurrentIdx() {
		if ( tourPartList.size() > currentIdx + 1) { 
			return ((TourPart) tourPartList.get(currentIdx)).getPlannedArrival();
		}
		return null;
	}
	
	public Date getRealArrivalForCurrentIdx() {
		if ( tourPartList.size() > currentIdx + 1) {
			return ((TourPart) tourPartList.get(currentIdx)).getRealArrival();
		}
		return null;
	}

	public int getCurrentIdx() {
		return currentIdx;
	}
	
	public void setCurrentIdx(int currentIdx) {
		this.currentIdx = currentIdx;
	}
	
	public List getMaterialsForCurrentIdx() {
		ArrayList result = new ArrayList();
		TourPart tourPart = ((TourPart) tourPartList.get(currentIdx));
		int type = tourPart.getType();
		if ( TourPart.TYPE_LOAD == type ) {
			String materialName = "";
			if ( tourPart.getArrivalAp().getLoad() != null &&  tourPart.getArrivalAp().getLoad().getSapMaterial() != null) {
					materialName = (String) materialDescriptionMap.get( tourPart.getArrivalAp().getLoad().getSapMaterial().getSapMaterialId() );
			}
			result.add(materialName);
			return result;
		} 
		else if ( TourPart.TYPE_DELIVERY == type ) {
			if ( tourPart.getArrivalAp().getOrder() == null 
					|| tourPart.getArrivalAp().getOrder().getOrderItems() == null 
					|| tourPart.getArrivalAp().getOrder().getOrderItems().size() == 0 ) {
				return result;
			}
			ListIterator ordItemIt = tourPart.getArrivalAp().getOrder().getOrderItems().listIterator();
			while ( ordItemIt.hasNext() ) {
				OrderItemEntity orderItem = (OrderItemEntity) ordItemIt.next();
				String materialName = (String) materialDescriptionMap.get( orderItem.getSapMaterial().getSapMaterialId() );
				String quantity = orderItem.getOrderedQuantity().toString();
				String uom = (String) materialUomNameMap.get(orderItem.getOrderedUom().getUomCd());
				result.add(materialName + " " + quantity + " " + uom);
			}
			return result;
		}
		return result;
	}

	public HashMap getMaterialDescriptionMap() {
		return materialDescriptionMap;
	}

	public void setMaterialDescriptionMap(HashMap materialDescriptionMap) {
		this.materialDescriptionMap = materialDescriptionMap;
	}
	
	public String getLocationForCurrentIdx() {
		return ((TourPart) tourPartList.get(currentIdx)).getLocationTx();
	}
	
	public boolean getHasPreLoading() {
		if ( tourPartList == null || tourPartList.isEmpty()) {
			return false;
		}
		ListIterator tpIt = tourPartList.listIterator();
		while (tpIt.hasNext()) {
			TourPart tp = (TourPart) tpIt.next(); 
			if ( TourPart.TYPE_PRELOAD == tp.getType() ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean getHasReturn() {
		if ( tourPartList == null || tourPartList.isEmpty()) {
			return false;
		}
		ListIterator tpIt = tourPartList.listIterator();
		while (tpIt.hasNext()) {
			TourPart tp = (TourPart) tpIt.next(); 
			if ( TourPart.TYPE_RETURN == tp.getType() ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean getCurrentIdxMovableUp() {
		TourPart tp = ((TourPart) tourPartList.get(currentIdx));
		if ( currentIdx == 0 ) {
			// the first row cannot be moved up
			return false;
		}
		if ( tp.getType() == TourPart.TYPE_PRELOAD  
				|| tp.getType() == TourPart.TYPE_RETURN ) {
			// these type of assignment parts cannot be moved
			return false;
		}
		if ((tp.getRealArrival() != null) || (tp.getRealDeparture() != null)){
			// Real times are set -> fixed
			return false;
		}
		TourPart previousPart = ((TourPart) tourPartList.get(currentIdx - 1));
		if ( tp.getType() != previousPart.getType() ) {
			// if previous part is not in the same group then this part cannot be moved up
			return false;
		}
		return true;
	}
	
	public boolean getCurrentIdxMovableDown() {
		TourPart tp = ((TourPart) tourPartList.get(currentIdx));
		if ( currentIdx == (tourPartList.size() - 1) ) {
			// the last row cannot be moved down
			return false;
		}
		if ( tp.getType() == TourPart.TYPE_PRELOAD  
				|| tp.getType() == TourPart.TYPE_RETURN ) {
			// these type of assignment parts cannot be moved
			return false;
		}
		if ((tp.getRealArrival() != null) || (tp.getRealDeparture() != null)){
			// Real times are set -> fixed
			return false;
		}
		TourPart nextPart = ((TourPart) tourPartList.get(currentIdx + 1));
		if ( tp.getType() != nextPart.getType() ) {
			// if next part is not in the same group then this part cannot be moved up
			return false;
		}
		return true;
	}

	public int getSelectedPartId() {
		return selectedPartId;
	}

	public void setSelectedPartId(int selectedPartId) {
		this.selectedPartId = selectedPartId;
	}
	
	public TourPart getSelectedTourPart() {
		if (selectedPartId == -1) {
			return null;
		}
		return (TourPart) tourPartList.get(selectedPartId);
	}

	public String getAssignmentPartShippingpointId() {
		return assignmentPartShippingpointId;
	}

	public void setAssignmentPartShippingpointId(String assignmentPartShippingpointId) {
		this.assignmentPartShippingpointId = assignmentPartShippingpointId;
	}

	public String getAssignmentPartShiptoSapNr() {
		return assignmentPartShiptoSapNr;
	}

	public void setAssignmentPartShiptoSapNr(String assignmentPartShiptoSapNr) {
		this.assignmentPartShiptoSapNr = assignmentPartShiptoSapNr;
	}

	public String getAssignmentPartCoordLon() {
		return assignmentPartCoordLon;
	}

	public void setAssignmentPartCoordLon(String assignmentPartCoordLon) {
		this.assignmentPartCoordLon = assignmentPartCoordLon;
	}

	public String getAssignmentPartCoordLat() {
		return assignmentPartCoordLat;
	}

	public void setAssignmentPartCoordLat(String assignmentPartCoordLat) {
		this.assignmentPartCoordLat = assignmentPartCoordLat;
	}

	public String getAssignmentPartCalcStart() {
		return assignmentPartCalcStart;
	}

	public void setAssignmentPartCalcStart(String assignmentPartCalcStart) {
		this.assignmentPartCalcStart = assignmentPartCalcStart;
	}

	public String getAssignmentPartPlannedDepartureDate() {
		return assignmentPartPlannedDepartureDate;
	}

	public void setAssignmentPartPlannedDepartureDate(String assignmentPartPlannedDepartureDate) {
		this.assignmentPartPlannedDepartureDate = assignmentPartPlannedDepartureDate;
	}

	public String getAssignmentPartPlannedDepartureTime() {
		return this.assignmentPartPlannedDepartureTime;
	}

	public void setAssignmentPartPlannedDepartureTime(String assignmentPartPlannedDeparture) {
		this.assignmentPartPlannedDepartureTime = assignmentPartPlannedDeparture;
	}

	public String getAssignmentPartRealDepartureDate() {
		return assignmentPartRealDepartureDate;
	}

	public void setAssignmentPartRealDepartureDate(String assignmentPartRealDepartureDate) {
		this.assignmentPartRealDepartureDate = assignmentPartRealDepartureDate;
	}

	public String getAssignmentPartRealDepartureTime() {
		return assignmentPartRealDepartureTime;
	}

	public void setAssignmentPartRealDepartureTime(String assignmentPartRealDeparture) {
		this.assignmentPartRealDepartureTime = assignmentPartRealDeparture;
	}
	
	public String getAssignmentPartPlannedArrivalDate() {
		return assignmentPartPlannedArrivalDate;
	}

	public void setAssignmentPartPlannedArrivalDate(String assignmentPartPlannedArrivalDate) {
		this.assignmentPartPlannedArrivalDate = assignmentPartPlannedArrivalDate;
	}

	public String getAssignmentPartPlannedArrivalTime() {
		return this.assignmentPartPlannedArrivalTime;
	}

	public void setAssignmentPartPlannedArrivalTime(String assignmentPartPlannedArrival) {
		this.assignmentPartPlannedArrivalTime = assignmentPartPlannedArrival;
	}

	public String getAssignmentPartRealArrivalDate() {
		return assignmentPartRealArrivalDate;
	}

	public void setAssignmentPartRealArrivalDate(String assignmentPartRealArrivalDate) {
		this.assignmentPartRealArrivalDate = assignmentPartRealArrivalDate;
	}

	public String getAssignmentPartRealArrivalTime() {
		return assignmentPartRealArrivalTime;
	}

	public void setAssignmentPartRealArrivalTime(String assignmentPartRealArrival) {
		this.assignmentPartRealArrivalTime = assignmentPartRealArrival;
	}
	
	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}
	
	public boolean getCurrentIdxTransmittedToDriver() {
		TourPart tp = (TourPart) tourPartList.get(selectedPartId);
		AssignmentPartEntity partEntity = tp.getArrivalAp();
		if (partEntity == null) {
			partEntity = tp.getDepartureAp();
		}
		if ( Long.valueOf("1").equals(partEntity.getAssignPartStatus().getAssignPartStatusCd())
				|| Long.valueOf("2").equals(partEntity.getAssignPartStatus().getAssignPartStatusCd())) {
			return true;
		}
		return false;
	}

	public String getAssignmentPartChanged() {
		return assignmentPartChanged;
	}

	public void setAssignmentPartChanged(String assignmentPartChanged) {
		this.assignmentPartChanged = assignmentPartChanged;
	}
	
	public String getAssignmentPartDistance() {
		return assignmentPartDistance;
	}

	public void setAssignmentPartDistance(String assignmentPartDistance) {
		this.assignmentPartDistance = assignmentPartDistance;
	}

	public boolean isExternalOrder() {
		return Constant.ASSIGNMENT_TYPE_EXTERNAL == assignment.getAssignmentType().getAssignmentTypeCd().intValue(); 
	}
	
	private void resetInputFields() {
		assignmentPartShippingpointId= null;
		assignmentPartShiptoSapNr = null; 
		assignmentPartCoordLon = null;
		assignmentPartCoordLat = null;
		assignmentPartCalcStart = null;
		assignmentPartPlannedDepartureDate = null;
		assignmentPartPlannedDepartureTime = null;
		assignmentPartRealDepartureDate = null;
		assignmentPartRealDepartureTime = null;
		assignmentPartPlannedArrivalDate = null;
		assignmentPartPlannedArrivalTime = null;
		assignmentPartRealArrivalDate = null;
		assignmentPartRealArrivalTime = null;
		assignmentPartChanged = null;
		assignmentPartDistance = null;
	}
	
	public void populateFormFromSelectedPart(int selectedPartIdx) {
		// [[
		Object[][] params = {{"selectedPartIdx", selectedPartIdx + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "populateFormFromSelectedPart", params);
		}
		// ]]
		try {
			// fill form attributes from selected part
			if ( getSelectedPartId() == -1) {
				return;
			}
			setSelectedPartId( selectedPartIdx);
			TourPart tourPart = getSelectedTourPart();
			resetInputFields();
			if (tourPart.getShippingpoint() == null) {
				setAssignmentPartShippingpointId(null);
			} else {
				setAssignmentPartShippingpointId( tourPart.getShippingpoint().getShippingpointId().toString());
			}
			if (tourPart.getShipto() == null) {
				setAssignmentPartShiptoSapNr(null);
			} else {
				setAssignmentPartShiptoSapNr(tourPart.getShipto().getShiptoSapBp().getSapId().toString());
			}
			if ( tourPart.getCoordLat() == null ) {
				setAssignmentPartCoordLat(null);
			}
			else {
				setAssignmentPartCoordLat( tourPart.getCoordLat().toString());
			}
			
			if ( tourPart.getCoordLon() == null ) {
				setAssignmentPartCoordLon(null);
			} 
			else {
				setAssignmentPartCoordLon( tourPart.getCoordLon().toString());
			}

			TimeZone timeZone = tourPart.getTimeZone();
			if (timeZone == null) {
				timeZone = TimeZone.getAppTimeZone();
			}

			if ( tourPart.getPlannedArrival() != null ) { 
				setAssignmentPartPlannedArrivalTime( timeZone.fmtAsHourMinute(tourPart.getPlannedArrival()));
				setAssignmentPartPlannedArrivalDate( timeZone.fmtAsDayMonthYear(tourPart.getPlannedArrival()));
			} 
			else {
				setAssignmentPartPlannedArrivalTime(null);
				setAssignmentPartPlannedArrivalDate(null);
			}
			if ( tourPart.getRealArrival() != null ) {
				setAssignmentPartRealArrivalTime( timeZone.fmtAsHourMinute(tourPart.getRealArrival()));
				setAssignmentPartRealArrivalDate( timeZone.fmtAsDayMonthYear(tourPart.getRealArrival()));
			}
			else {
				setAssignmentPartRealArrivalTime(null);
				setAssignmentPartRealArrivalDate(null);
			}
			if ( tourPart.getPlannedDeparture() != null ) { 
				setAssignmentPartPlannedDepartureTime( timeZone.fmtAsHourMinute(tourPart.getPlannedDeparture()));
				setAssignmentPartPlannedDepartureDate(timeZone.fmtAsDayMonthYear(tourPart.getPlannedDeparture()));
			} 
			else {
				setAssignmentPartPlannedDepartureTime(null);
				setAssignmentPartPlannedDepartureDate(null);
			}
			if ( tourPart.getRealDeparture() != null ) {
				setAssignmentPartRealDepartureTime( timeZone.fmtAsHourMinute(tourPart.getRealDeparture()));
				setAssignmentPartRealDepartureDate( timeZone.fmtAsDayMonthYear(tourPart.getRealDeparture()));
			}
			else {
				setAssignmentPartRealDepartureTime(null);
				setAssignmentPartRealDepartureDate(null);
			}
			if (tourPart.getLocationType() != POSITION_NONE) {
				setLocationType(tourPart.getLocationType());
			}
			
			BigDecimal distance = tourPart.getDistance();
			if ( distance != null ) {
				BigDecimal scaled = distance.setScale(1, RoundingMode.HALF_UP);
				String distanceStr = scaled.stripTrailingZeros().toString();
				setAssignmentPartDistance(distanceStr);
			}
			
			setExternalMaterial(tourPart.getExternalMaterial());
			setExternalQuantity(tourPart.getExternalQuantity() == null ? null : tourPart.getExternalQuantity().toString());
			setExternalUom(tourPart.getExternalUom() == null ? null : tourPart.getExternalUom().getUomCd());
			
			setSiteName(tourPart.getSiteName());
			setSiteLoadUnload(tourPart.getSiteLoadUnload());
			setSiteContactPersonTxt(tourPart.getSiteContactPersonTxt());
			setSiteStreetHouseNo(tourPart.getSiteStreetHouseNo());
			setSiteCountry(tourPart.getSiteCountry());
			setSitePostalCode(tourPart.getSitePostalCode());
			setSiteCity(tourPart.getSiteCity());
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}
	
	public Double getActiveCoordLon(int idx) {
		return ((TourPart) tourPartList.get(idx)).getActiveCoordLon();
	}
	
	public Double getActiveCoordLat(int idx) {
		return ((TourPart) tourPartList.get(idx)).getActiveCoordLat();
	}
		
	public String getActiveLocationType(int idx) {
		return ((TourPart) tourPartList.get(idx)).getLocationType();
	}
	
	public String getTimeModifier(int idx) {
		return (String) timeModifierList.get(idx);
	}
	
	public void setTimeModifier(int idx, String timeModifier) {
		this.timeModifierList.set(idx, StringUtils.empty(timeModifier) ? null : timeModifier);
	}

	public List getTimeModifierList() {
		return timeModifierList;
	}
	
	public void setTimeModifierList(List timeModifierList) {
		this.timeModifierList = timeModifierList;
	}

	public void initTimeModifierList(int size) {
		timeModifierList = new ArrayList(tourPartList.size());
		for (int i = 0; i < size; i ++ ) {
			timeModifierList.add("0");
		}
	}

	public String getExternalMaterial() {
		return externalMaterial;
	}

	public void setExternalMaterial(String externalMaterial) {
		this.externalMaterial = externalMaterial;
	}

	public String getExternalQuantity() {
		return externalQuantity;
	}

	public void setExternalQuantity(String externalQuantity) {
		if ((externalQuantity != null) && !externalQuantity.trim().isEmpty()) {
			this.externalQuantity = externalQuantity.trim();
		}
		else {
			this.externalQuantity = null;
		}
	}

	public String getExternalUom() {
		return externalUom;
	}

	public void setExternalUom(String externalUom) {
		this.externalUom = externalUom;
	}

	public Map getUomMap() {
		return uomMap;
	}

	public void setUomMap(Map uomMap) {
		this.uomMap = uomMap;
	}

	public List getUomList() {
		return uomList;
	}

	public void setUomList(List uomList) {
		this.uomList = uomList;
	}

	public long getMaxSeqno() {
		return maxSeqno;
	}

	public void setRecalculateIdx(Integer idx) {
		recalculateIdx = idx;
		if ((recalculateIdx != null) && (recalculateIdx.intValue() < 0)){
			recalculateIdx = new Integer(0);
		}
	}
	
	public Integer getRecalculateIdx(){
		return recalculateIdx;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteLoadUnload() {
		return siteLoadUnload;
	}

	public void setSiteLoadUnload(String siteLoadUnload) {
		this.siteLoadUnload = siteLoadUnload;
	}

	public String getSiteContactPersonTxt() {
		return siteContactPersonTxt;
	}

	public void setSiteContactPersonTxt(String siteContactPersonTxt) {
		this.siteContactPersonTxt = siteContactPersonTxt;
	}

	public String getSiteStreetHouseNo() {
		return siteStreetHouseNo;
	}

	public void setSiteStreetHouseNo(String siteStreetHouseNo) {
		this.siteStreetHouseNo = siteStreetHouseNo;
	}

	public String getSiteCountry() {
		return siteCountry;
	}

	public void setSiteCountry(String siteCountry) {
		this.siteCountry = siteCountry;
	}

	public String getSitePostalCode() {
		return sitePostalCode;
	}

	public void setSitePostalCode(String sitePostalCode) {
		this.sitePostalCode = sitePostalCode;
	}

	public String getSiteCity() {
		return siteCity;
	}

	public void setSiteCity(String siteCity) {
		this.siteCity = siteCity;
	}
	
	public String getTruckName() {
		TruckEntity truck = assignment.getTruck();
		if (truck != null) {
			return truck.getTruckName();
		}
		SimpleVehicleEntity simpleVehicle = assignment.getSimpleVehicle();
		if (simpleVehicle != null) {
			return simpleVehicle.getVehicleName();
		}
		return "";
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public long getExtOrderId() {
		return extOrderId;
	}

	public void setExtOrderId(long extOrderId) {
		this.extOrderId = extOrderId;
	}

	public void setOderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Long getOderNumber() {
		return orderNumber;
	}

    public List<ReroutingOrderWrapper> getReroutingOrderWrapperList() {
		return reroutingOrderWrapperList;
	}

	public void setReroutingOrderWrapperList(List<ReroutingOrderWrapper> reroutingOrderWrapperList) {
		this.reroutingOrderWrapperList = reroutingOrderWrapperList;
	}
	
	// LOGON-6664 Tell client interface whether to show full dates, 
	// not just times, for cross-midnight orders
	public boolean isAcrossMidnight() {
		boolean retVal = false;
		for(Object o: tourPartList) {
			TourPart part = (TourPart) o;

			TimeZone timeZone = part.getTimeZone();
			if (timeZone == null) {
				timeZone = TimeZone.getAppTimeZone();
			}

			if(part.getPlannedArrival() != null && part.getRealArrival() != null){
				Calendar a = timeZone.asCalendar();
				a.setTime(part.getPlannedArrival());
				Calendar b = timeZone.asCalendar();
				b.setTime(part.getRealArrival());
				if(a.get(Calendar.DAY_OF_YEAR) != b.get(Calendar.DAY_OF_YEAR)) {
					retVal = true;
					break;
				}
				
			}
			if(part.getPlannedDeparture() != null && part.getRealDeparture() != null){
				Calendar c = timeZone.asCalendar();
				c.setTime(part.getPlannedDeparture());
				Calendar d = timeZone.asCalendar();
				d.setTime(part.getRealDeparture());
				if(c.get(Calendar.DAY_OF_YEAR) != d.get(Calendar.DAY_OF_YEAR)) {
					retVal = true;
					break;
				}
			}
			
			
			
		}
		return retVal;
	}
	public void setAcrossMidnight(boolean acrossMidnight) {
		this.acrossMidnight = acrossMidnight;
	}
	
	/**
	 * From before SelectedTourPart
	 * 
	 * This method will return the real departure date from the 
	 * assignment part before the selected assignment part.
	 * If that is null, then it takes the date from the assignment part before that, and so on...
	 * @return
	 */
	public Date getMostRecentRealDepartureDate() {
		// [[
		Date result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getMostRecentRealDeparture");
		}
		// ]]
		try {
			TourPart selectedTourPart = getSelectedTourPart();
			if (tourPartList == null || selectedTourPart == null) {
				return result = null;
			}
			Iterator<TourPart> iterator = tourPartList.iterator();
			do {
				TourPart tourPart = iterator.next();
				if (selectedTourPart.equals(tourPart)) {
					return result;
				}
				Date departureDate = tourPart.getRealDeparture();
				result = departureDate == null ? result : departureDate;
			} while (iterator.hasNext());
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

}
