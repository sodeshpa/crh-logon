/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.beans.UomConversionResult;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.DispoLoadtargetEntity;
import com.crh.logon.persistence.entities.views.DispoOrderItemListEntity;
import com.crh.logon.persistence.entities.views.DispoWagonListEntity;
import com.crh.logon.utils.LogonConversionException;
import com.crh.logon.utils.WagonNumberUtils;

/**
 * @author AdNovum Informatik AG
 */
public class WagonWrapper implements Serializable, Comparable<WagonWrapper> {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(WagonWrapper.class);
	private static final long serialVersionUID = 1L;
	
	private final UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	
	private final DispoWagonListEntity wagon;
	
	private RailAssignmentWrapper assignment;
	private List<ContainerWrapper> containers;
	private List<LoadWrapper> loads;
	
	private List<OrderPrioWrapper> possibleOrders;
	private Collection<String> possibleMaterialList;
	private Collection<DispoLoadtargetPrioWrapper> possiblePreloads;

	private Long wagonNo;
	private String wagonDisplayNo;
	private Set<Long> matComptaSet;
	private double tareWeightT;
	/** For the wagon itself. */
	private final BigDecimal netQuantityInTons;
	
	private boolean isReturning;
	private boolean preLoadEnabled;

	/**
	 * The determined status will only be set when the wrappers are fetched and
	 * the status components are set: loaded, submitted, returning, atShippingPoint
	 */
	private Long determinedStatus;

	private TimeZone timeZone;
	
	public DispoWagonListEntity getWagon() {
		return wagon;
	}

	public List<ContainerWrapper> getContainers() {
		return containers;
	}

	public WagonWrapper(DispoWagonListEntity wagon, boolean isReturning,
			UomConverterServiceLocalInterface unitOfMeasurementConverterService, TimeZone timeZone) {
		this.unitOfMeasurementConverterService = unitOfMeasurementConverterService;
		
		this.wagon = wagon;
		
		this.assignment = null;
		this.containers = new LinkedList<ContainerWrapper>();
		
		this.possibleOrders = new LinkedList<OrderPrioWrapper>();
		
		this.possibleMaterialList = new HashSet<String>();
		if (wagon.getMaterialName() != null) {
			possibleMaterialList.add(wagon.getMaterialName());
		}
		
		this.possiblePreloads = new LinkedList<DispoLoadtargetPrioWrapper>();

		if (wagon.getWagonNo() != null) {
			try {
				wagonNo = Long.parseLong(wagon.getWagonNo());
			}
			catch (NumberFormatException nfe) { }
		}

		this.wagonDisplayNo = WagonNumberUtils.toDisplayNo(wagon.getWagonNo());

		matComptaSet = null;
		
		if ((wagon.getWagonTrspCatCd().longValue() == Constant.WAGON_TKATEGORIE_SILO) && (wagon.getTareQuantity() != null)){
			tareWeightT = wagon.getTareQuantity().doubleValue(); // assuming UoM is tons for silo wagon
		}
		else {
			tareWeightT = 0;
		}
		
		netQuantityInTons = calculateNetQuantityInTons();
		
		this.isReturning = isReturning;
		this.preLoadEnabled = false;
		
		this.timeZone = timeZone;
	}

	public RailAssignmentWrapper getAssignment() {
		return assignment;
	}

	public void setAssignment(RailAssignmentWrapper assignment) {
		this.assignment = assignment;
	}

	/**
	 * Initialize the list of possible materials.
	 * @param assignment
	 */
	public void initPossibleMaterialList() {
		
		if ((this.assignment != null)) {
			this.possibleMaterialList = new HashSet<String>();
			for (AssignmentPartWrapper apw : assignment.getParts()) {
				List<DispoOrderItemListEntity> orderItems = apw.getOrderItems();
				if (orderItems != null) {
					int index = 0;
					for (DispoOrderItemListEntity doil : orderItems) {
						if (containers.size() < orderItems.size()) {
							if (!checkMatCompat(doil.getSapMaterialId(),
									doil.getProductionPlantCd(), doil.getOrderedQuantity(),
									doil.getUomCd(), doil.getFuzzyQuantityFlag())) {
								continue;
							}
							possibleMaterialList.add(doil.getMaterialName());
						}
						else {
							containers.get(index).addPossibleMaterial(doil.getMaterialName());
							index++;
						}
					}
				}
			}
		}
	}

	public void addContainer(ContainerWrapper container){
		containers.add(container);
	}

	public void setPossibleOrders(List<OrderPrioWrapper> possibleOrders) {
		if (assignment != null) {
			return;
		}
		this.possibleOrders = new ArrayList<OrderPrioWrapper>();
		possibleMaterialList = new HashSet<String>();
		for (OrderPrioWrapper opw : possibleOrders) {
			int index = 0;
			boolean add = true;
			for (DispoOrderItemListEntity doil : opw.getOrder().getMaterialItems()) {
				if (wagon.getWagonTrspCatCd().longValue() == Constant.WAGON_TKATEGORIE_SILO) {
					if (!checkMatCompat(doil.getSapMaterialId(), doil.getProductionPlantCd(),
							doil.getOrderedQuantity(), doil.getUomCd(),
							doil.getFuzzyQuantityFlag())) {
						add = false;
						continue;
					}
					possibleMaterialList.add(doil.getMaterialName());
				}
				else {
					if (index < containers.size()){
						ContainerWrapper container = containers.get(index);
						container.addPossibleMaterial(doil.getMaterialName());
					}
					index++;
				}
			}
			if (add) {
				this.possibleOrders.add(opw);
			}
		}
	}

	public List<OrderPrioWrapper> getPossibleOrders() {
		return possibleOrders;
	}

	public Collection<DispoLoadtargetPrioWrapper> getPossiblePreloads() {
		return possiblePreloads;
	}

	public void setPossiblePreloads(Collection<DispoLoadtargetPrioWrapper> possiblePreloads) {
		if (assignment != null){
			return;
		}
		if ((wagon.getNetQuantity() != null) && (wagon.getNetQuantity().longValue() != 0)){
			return;
		}
		this.possiblePreloads = new LinkedList<DispoLoadtargetPrioWrapper>();
		
		for (DispoLoadtargetPrioWrapper slpw : possiblePreloads) {
			DispoLoadtargetEntity slpwTarget = slpw.getTarget();
			
			if (containers.isEmpty()) {
				if (!slpwTarget.getLoadUomCd().equals(Constant.UOM_CONTAINER)) {
					// cosmin 28.01.2009: Check material compatibility matrix only if we have no containers
					// because it is not possible to find the tare weght of each single container on
					// a wagon
					if (!checkMatCompat(slpwTarget.getSapMaterialId(),
							slpwTarget.getProductionPlantCd(), slpwTarget.getLoadQuantity(),
							slpwTarget.getLoadUomCd(), slpwTarget.getFuzzyQuantityFlag())) {
						continue;
					}
					possibleMaterialList.add(slpwTarget.getMaterialName());
					this.possiblePreloads.add(slpw);
				}
			}
			else {
				if (!slpwTarget.getLoadUomCd().equals(Constant.UOM_WAGON)) {
					for (ContainerWrapper cw : containers) {
						if ((cw.getContainer().getNetQuantity()!= null) && (cw.getContainer().getNetQuantity().longValue() != 0)){
							return;
						}
						cw.addPossibleMaterial(slpwTarget.getMaterialName());
						this.possiblePreloads.add(slpw);
					}
				}
			}
		}
		
	}

	public Collection<String> getPossibleMaterialList() {
		return possibleMaterialList;
	}

	public String getWagonDisplayNo() {
		return wagonDisplayNo;
	}

	public boolean getIsReturning() {
		return isReturning;
	}

	public boolean isPreLoadEnabled() {
		return preLoadEnabled;
	}

	public void setPreLoadEnabled(boolean preLoadEnabled) {
		this.preLoadEnabled = preLoadEnabled;
	}

	public void setMatCompatSet(Set<Long> matCompatSet) {
		this.matComptaSet = matCompatSet;
		matCompatSet.add(wagon.getLastSapMaterialId());
	}

	public void setTareWeightT(double tareWeightT) {
		this.tareWeightT = tareWeightT;
	}


	private boolean checkMatCompat(Long orderMaterialId, String orderProdPlantCd,
			BigDecimal orderedQuantity, String orderedUomCd, Boolean fuzzyQuantityFlag) {
		// cosmin 28.01.2009: Check material compatibility matrix only if we have no containers
		// because it is not possible to find the tare weght of each single container on
		// a wagon
		
		if ((wagon.getWagonTrspCatCd() == Constant.WAGON_TKATEGORIE_SILO)
				&& wagon.getLastSapMaterialId() != null) {
			if (tareWeightT > 0) {
				BigDecimal wagonTareWeightT = wagon.getTareWeightT();
				double rcT = tareWeightT - wagonTareWeightT.doubleValue();
				double orderWeightT = 0;
				if (fuzzyQuantityFlag) {
					Double wagonMaxGrossWeightT = wagon.getMaxGrossWeightT();
					
					orderWeightT = wagonMaxGrossWeightT - wagonTareWeightT.doubleValue();
				}
				else {
					try {
						UomConversionResult ucr = unitOfMeasurementConverterService.convert(orderedQuantity,
								orderedUomCd, Constant.UOM_TONS, orderMaterialId, null, null,
								false);
						
						BigDecimal orderedQuantityT = ucr.getQuantity();
						orderWeightT = orderedQuantityT.doubleValue();
					}
					catch (LogonConversionException e) {
						LOG.warn("Cannot convert ordered quantity from UoM " + orderedUomCd
								+ " to tons (sapMaterialId = " + orderMaterialId + "): "
								+ e.getMessage());
						return false;
					}
				}
				// if there is no mat compat entry or if the remaining quantity is above the force identical threshold, check material for identity
				if ((wagon.getMatCompatId() == null) || (orderWeightT * wagon.getForceIdenticalThresholdPerc().doubleValue()/100 < rcT)){
					if ((orderMaterialId.longValue() != wagon.getLastSapMaterialId().longValue())
							|| !orderProdPlantCd.equals(wagon.getLastProductionPlantCd())) {
						return false;
					}
				} // if the remaining  quantity is between forceIdenticalThreshold and insignificantThreshold check compatibility List
				else if (orderWeightT * wagon.getInsignificantThresholdPerc().doubleValue()/100 < rcT) {
					if (!matComptaSet.contains(orderMaterialId)){
						return false;
					}
				}
			}
		}
		
		return true;
	}


	public boolean isLoaded(){
		if (isReturning){
			return false;
		}
		if ((containers == null) || (containers.isEmpty())) {
			if (wagon.getLoadId() != null) {
				if (wagon.getLogonPackageTypeCd() == null) {
					// We have a load with a non-SAP material.
					// TODO 29-Nov-2012/romek: This is no longer true, also clean up raildispo.js!
					return true;
				}
				if ((wagon.getNetQuantity() != null) && (wagon.getNetQuantity() != 0)) {
					// We have a load with a SAP material and there is a net quantity.
					return true;
				}
				if (wagon.getLoadingTs() != null) {
					return true;
				}
			}
			
			return false;
		}
		else {
			for (ContainerWrapper cw : containers) {
				if (!cw.isLoaded()){
					return false;
				}
			}
			return true;
		}
	}


	public boolean isTransmitted() {
		
		if (isReturning) {
			return false;
		}
		
		if ((assignment != null) && assignment.unloadParts().size() > 0){
			// rail assignments should have only one order
			AssignmentPartWrapper apw = assignment.unloadParts().get(0);
			final Long orderStatusCd = apw.getAssignmentPart().getOrderStatusCd();
			return LogonStatusUtils.isBeyondLoaded(orderStatusCd);
		}
		
		return false;
	}


	private <T extends Comparable<T>> int compareIncludingNull(T c1, T c2,
			boolean nullSmaller) {
		if (c1 != null){
			if (c2 != null) {
				return c1.compareTo(c2);
			}
			else {
				return nullSmaller?1:-1;
			}
		}
		if (c2 != null){
			return nullSmaller?-1:1;
		}
		return 0;
	}

	public int compareTo(WagonWrapper other) {
		int result = 0;
		
		result = compareIncludingNull(wagon.getTrackName(), other.wagon.getTrackName(), false);
		if (result != 0) {
			return result;
		}
		result = compareIncludingNull(wagon.getCurrentTrackPositionNo(), other.wagon.getCurrentTrackPositionNo(), false);
		if (result != 0) {
			return result;
		}
		
		result = compareIncludingNull(wagon.getLoadingTs(), other.wagon.getLoadingTs(), false);
		if (result != 0) {
			return result;
		}
		
		Date thisComapareTime = wagon.getArrivalLastKnownStationDat();
		if (this.isReturning){
			thisComapareTime = wagon.getPlannedStart();
		}
		Date otherCompareTime = other.wagon.getArrivalLastKnownStationDat();
		if (other.isReturning){
			otherCompareTime =other. wagon.getPlannedStart();
		}
		result = compareIncludingNull(thisComapareTime, otherCompareTime, false);
		if (result != 0) {
			return result;
		}
		result = wagon.getWagonTypeName().compareTo(other.wagon.getWagonTypeName());
		if (result != 0) {
			return result;
		}
		
		return wagon.getWagonNo().compareTo(other.wagon.getWagonNo());
	}

	public boolean equals(Object o){
		WagonWrapper other = (WagonWrapper) o;
		return wagon.getWagonNo().equals(other.wagon.getWagonNo());
	}

	public List<LoadWrapper> getLoads() {
		return loads;
	}

	public void setLoads(List<LoadWrapper> loads) {
		this.loads = loads;
	}
	
	public BigDecimal getNetQuantityInTons() {
		return netQuantityInTons;
	}

	// TODO 29-Nov-2012/romek: Similar calculations in WagonWrapper, ContainerWrapper, LoadWrapper.
	private BigDecimal calculateNetQuantityInTons() {
		Double netQuantityAsDouble = wagon.getNetQuantity();
		String netQuantityUomCd = wagon.getNetQuantityUomCd();
		
		BigDecimal netQuantityInTons = null;
		
		if (netQuantityAsDouble != null && netQuantityUomCd != null) {
			BigDecimal netQuantity = new BigDecimal(netQuantityAsDouble);
			Long sapMaterialId = wagon.getSapMaterialId(); // Not nullable.
			
			try {
				UomConversionResult ucr = unitOfMeasurementConverterService.convert(netQuantity,
						netQuantityUomCd, Constant.UOM_TONS, sapMaterialId, null, null, false);
				
				netQuantityInTons = ucr.getQuantity();
			}
			catch (LogonConversionException e) {
				LOG.warn("Cannot convert wagon net quantity from UoM " + netQuantityUomCd
						+ " to tons (sapMaterialId = " + sapMaterialId + "): " + e.getMessage());
			}
		}
		
		return netQuantityInTons;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public String getArrivalLastKnownStation() {
		if (wagon.getArrivalLastKnownStationDat() == null) {
			return " - ";
		}
		return timeZone.fmtAsDayMonthHourMinuteTrailingDot(wagon.getArrivalLastKnownStationDat());
	}

	public String getPlannedStartAsDayMonthHourMinute() {
		return timeZone.fmtAsDayMonthHourMinuteTrailingDot(wagon.getPlannedStart());
	}

	public String getPlannedStartAsDayMonthYear() {
		return timeZone.fmtAsDayMonthYear(wagon.getPlannedStart());
	}

	public String getLoadingTSAsDayMonthHourMinute() {
		if (wagon.getLoadingTs() == null) {
			return " - ";
		}
		return timeZone.fmtAsDayMonthHourMinuteTrailingDot(wagon.getLoadingTs());
	}

	/**
	 * @see WagonWrapper#determinedStatus
	 */
	public Long getDeterminedStatus() {
		return determinedStatus;
	}

	public void setDeterminedStatus(Long determinedStatus) {
		this.determinedStatus = determinedStatus;
	}

	public Long getWagonNo() {
		return wagonNo;
	}

}
