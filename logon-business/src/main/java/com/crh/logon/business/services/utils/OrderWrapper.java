/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.DispoExtOrderListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderItemListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderListEntity;
import com.crh.logon.persistence.entities.views.DispoPlanningEntity;
import com.crh.logon.persistence.entities.views.DispoTruckTypeListEntity;
import com.crh.logon.persistence.entities.views.DispoWagonTypeListEntity;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.utils.WagonNumberUtils;

/**
 * @author AdNovum Informatik AG
 */
public class OrderWrapper implements Serializable, Comparable<OrderWrapper> {
	
	private static final long serialVersionUID = 1L;
	
	private Object orderObj;
	private DispoOrderListEntity logonOrder;
	private DispoExtOrderListEntity extOrder;
	private List<DispoOrderItemListEntity> items;
	private List<DispoOrderItemListEntity> materialItems;
	private long loadingTime;
	private boolean isBag;
	private boolean isEmptyPallet;
	private boolean isLandfill;
	private List<DispoWagonTypeListEntity> wagonTypeList;
	/** Holds the possible truck type matches for the order. */
	private List<DispoTruckTypeListEntity> truckTypeList;
	/** The packagetype code of the first ordered item. */
	private Long logonPackageTypeCd;
	/** Can I put this order in a container? */
	private boolean isContainerType;
	/** Do I have to transport it in a container? */
	private boolean isContainerOnly;
	private boolean isRailSiloType;
	private Date firstSwitchingTimeDep;
	private Date firstSwitchingTimeArr;
	private Date lastSwitchingTimeDep;
	private Date lastSwitchingTimeArr;
	private Map<Long, String> wagonNumbers;
	private Set<String> distinctWagonNumbers;
	
	//prescheduling attributes - have effect on UI in case of truck
	private boolean isPreschedulingRequired;
	private boolean isPreschedulingDone;
	private Double freightCost;
	private String freightCurrency;
	private Long plannedTruckId;
	private Date plannerUnloadPlannedStart;

	private TimeZone userTimeZone;

	public OrderWrapper(Object order, List<DispoWagonTypeListEntity> wtlist, TimeZone userTimeZone) {
		this.userTimeZone = userTimeZone;

		if (order instanceof DispoOrderListEntity) {
			this.orderObj = order;
			this.logonOrder = (DispoOrderListEntity)order;
			initOrderWrapper(wtlist);
			calculateSwitchingTimes();
		}
		else {
			this.orderObj = order;
			this.extOrder = (DispoExtOrderListEntity)order;
			initOrderWrapper(wtlist);
		}
	}
	
	private void initOrderWrapper(List<DispoWagonTypeListEntity> wtlist){
		this.items = new LinkedList<DispoOrderItemListEntity>();
		this.materialItems = new LinkedList<DispoOrderItemListEntity>();
		this.loadingTime = 0;
		isBag = false;
		isLandfill = false;
		isEmptyPallet = false;
		wagonTypeList = wtlist;
		isContainerType = true;
		isContainerOnly = false;
		isRailSiloType = true;
		firstSwitchingTimeDep = null;
		firstSwitchingTimeArr = null;
		lastSwitchingTimeDep = null;
		lastSwitchingTimeArr = null;
		wagonNumbers = new HashMap<Long, String>();
		distinctWagonNumbers = new HashSet<String>();
		isPreschedulingRequired = false;
		isPreschedulingDone = false;
	}
	
	private void calculateSwitchingTimes() {
		DispoOrderListEntity order = (DispoOrderListEntity)orderObj; 
		if (order.getArrOffsetDays() == null){
			return;
		}

		TimeZone timeZone = TimeZone.getDBTimeZone();

		Calendar c = timeZone.asCalendar();
		c.setTime(timeZone.getDayStart(order.getDeliveryToDat()));
		c.add(Calendar.DAY_OF_YEAR, -order.getArrOffsetDays().intValue());
		int day = c.get(Calendar.DAY_OF_WEEK);
		switch (day) {
		case Calendar.SATURDAY:
			c.add(Calendar.DAY_OF_YEAR, -1);
			break;
		case Calendar.SUNDAY:
			c.add(Calendar.DAY_OF_YEAR, -2);
			break;
		default:
			// we don't have to do anything if it isn't a weekend day
			break;
		}
		
		c.add(Calendar.MINUTE, order.getDepSwitchingTimeMin().intValue());
		lastSwitchingTimeDep = c.getTime();

		c.setTime(timeZone.getDayStart(order.getDeliveryToDat()));
		c.add(Calendar.MINUTE, order.getArrSwitchingTimeMin().intValue());
		lastSwitchingTimeArr = c.getTime();

		c.setTime(timeZone.getDayStart(order.getDeliveryFromDat()));
		c.add(Calendar.DAY_OF_YEAR, -order.getArrOffsetDays().intValue());
		day = c.get(Calendar.DAY_OF_WEEK);
		switch (day) {
		case Calendar.SATURDAY:
			c.add(Calendar.DAY_OF_YEAR, -1);
			break;
		case Calendar.SUNDAY:
			c.add(Calendar.DAY_OF_YEAR, -2);
			break;
		default:
			// we don't have to do anything if it isn't a weekend day
			break;
		}
		
		c.add(Calendar.MINUTE, order.getDepSwitchingTimeMin().intValue());
		firstSwitchingTimeDep = c.getTime();

		c.setTime(timeZone.getDayStart(order.getDeliveryFromDat()));
		c.add(Calendar.MINUTE, order.getArrSwitchingTimeMin().intValue());
		firstSwitchingTimeArr = c.getTime();
	}

	public void addItem(DispoOrderItemListEntity item){
		items.add(item);
		if (item.getBrWagonNo() != null){
			String disp = WagonNumberUtils.toDisplayNo(item.getBrWagonNo());
			distinctWagonNumbers.add(disp);
			wagonNumbers.put(item.getOrderItemId(), disp);
		}
		if (!LogonPackageHandlingUtil.isMaterialType(item.getLogonPackageTypeCd())) {
			//if it is not a material item it should not define any of the order wrapper's attributes
			return;
		}
		materialItems.add(item);
		if (item.getPlannedLoadingTimeMin() != null){
			loadingTime += item.getPlannedLoadingTimeMin().longValue();
		}
		String materialPackageCd = item.getMaterialPackageCd();
		isBag =Constant.MATERIAL_PACKAGE_BAG.equals(materialPackageCd) 
				|| Constant.MATERIAL_PACKAGE_PALET_EMPTY.equals(materialPackageCd);
		isEmptyPallet = Constant.MATERIAL_PACKAGE_PALET_EMPTY.equals(materialPackageCd);
		isLandfill = item.getIslandfill();
		
		String uomCd = item.getUomCd();
		
		// FIXME 29-Nov-2012/romek: Are these still valid?
		if (uomCd.equals(Constant.UOM_CONTAINER)) {
			isContainerOnly = true;
		}
		else if (!uomCd.equals(Constant.UOM_TONS)) {
			isContainerType = false;
		}
		
		if (!uomCd.equals(Constant.UOM_WAGON) && !uomCd.equals(Constant.UOM_TONS)) {
			isRailSiloType = false;
		}
		
		if (items.size() == 1) {
			logonPackageTypeCd = item.getLogonPackageTypeCd();
		}
	}
	
	public boolean isExternalOrder() {
		return orderObj instanceof DispoExtOrderListEntity;
	}

	/* TODO gergely cleanup
	public DispoOrderListEntity getOrder() {
		return order;
	}
	*/

	public Object getOrder() {
		return orderObj;
	}
	
	/**
	 * Returns an {@link UnallocatedOrderComparisonData} from the {@link #orderObj} for sorting the unallocated orders in the Scheduling screen 
	 * @return {@link UnallocatedOrderComparisonData}
	 */
	public UnallocatedOrderComparisonData getUnallocatedOrderComparisonData() {

		Object orderObject = this.getOrder();

		if (orderObject == null) {
			return null;
		}

		boolean orderIsDispoOrderListEntity = true;

		Long orderStatusCode = null;
		Long commercialCarrierId = null;
		Long executingCarrierId = null;

		if (orderObject instanceof DispoOrderListEntity) {

			DispoOrderListEntity dispoOrderListEntity = (DispoOrderListEntity) orderObject;
			orderStatusCode = dispoOrderListEntity.getOrderStatusCd();
			commercialCarrierId = dispoOrderListEntity.getCommCarrierId();
			executingCarrierId = dispoOrderListEntity.getExecCarrierId();
			orderIsDispoOrderListEntity = true;

		}
		else if (orderObject instanceof DispoExtOrderListEntity) {

			DispoExtOrderListEntity dispoExtOrderListEntity = (DispoExtOrderListEntity) orderObject;
			orderStatusCode = dispoExtOrderListEntity.getOrderStatusCd();
			commercialCarrierId = dispoExtOrderListEntity.getCommCarrierId();
			executingCarrierId = dispoExtOrderListEntity.getExecCarrierId();
			orderIsDispoOrderListEntity = false;
		}

		UnallocatedOrderComparisonData result = new UnallocatedOrderComparisonData(
				orderStatusCode, commercialCarrierId, executingCarrierId, orderIsDispoOrderListEntity);

		return result;

	}

	public DispoOrderListEntity getLogonOrder() {
		return logonOrder;
	}

	public DispoExtOrderListEntity getExtOrder() {
		return extOrder;
	}

	public List<DispoOrderItemListEntity> getItems() {
		return items;
	}
	
	public List<DispoOrderItemListEntity> getMaterialItems() {
		return materialItems;
	}

	public long getLoadingTime() {
		return loadingTime;
	}

	public boolean getIsBag() {
		return isBag;
	}
	
	public Long getCommCarrierId() {
		return isExternalOrder() ? extOrder.getCommCarrierId() : logonOrder.getCommCarrierId();
	}
	
	public Long getExecCarrierId() {
		return isExternalOrder() ? extOrder.getExecCarrierId() : logonOrder.getExecCarrierId();
	}

	public List<DispoWagonTypeListEntity> getWagonTypeList() {
		return wagonTypeList;
	}

	public boolean getIsContainerType() {
		return isContainerType;
	}

	public boolean getIsRailSiloType() {
		return isRailSiloType;
	}

	public Date getLastSwitchingTimeDep() {
		return lastSwitchingTimeDep;
	}

	public void setLastSwitchingTimeDep(Date d) {
		lastSwitchingTimeDep = d;
	}

	public Date getLastSwitchingTimeArr() {
		return lastSwitchingTimeArr;
	}

	public void setLastSwitchingTimeArr(Date d) {
		lastSwitchingTimeArr = d;
	}

	public String getId() {
		
		return isExternalOrder() ? "ext_" + extOrder.getExtOrderId() : "int_" + logonOrder.getOrderId();
	}

	public int compareTo(OrderWrapper to) {
		int ret = 0;
		if (this.firstSwitchingTimeDep != null) {
			if (to.firstSwitchingTimeDep != null) {
				ret = this.firstSwitchingTimeDep.compareTo(to.firstSwitchingTimeDep);
				if (ret != 0) {
					return ret;
				}
			}
			else {
				return -1;
			}
		}
		else {
			if (to.firstSwitchingTimeDep != null) {
				return 1;
			}
		}
		
		Date thisDat = this.isExternalOrder()  ? this.extOrder.getSortToDat() : this.logonOrder.getSortToDat();
		Date toDat = to.isExternalOrder() ? to.extOrder.getSortToDat() : to.logonOrder.getSortToDat();
			
		ret = thisDat.compareTo(toDat);
		if (ret != 0){
			return ret;
		}
		DispoWagonTypeListEntity thisDwtl = null;
		if ((this.wagonTypeList != null) && !this.wagonTypeList.isEmpty()) {
			thisDwtl = this.wagonTypeList.get(0);
		}
		DispoWagonTypeListEntity toDwtl = null;
		if ((to.wagonTypeList != null) && !to.wagonTypeList.isEmpty()) {
			toDwtl = to.wagonTypeList.get(0);
		}
		if (thisDwtl != null) {
			if (toDwtl != null) {
				ret = thisDwtl.getEvaluationPriority().compareTo(toDwtl.getEvaluationPriority());
				if (ret != 0) {
					return ret;
				}
			}
			else {
				return -1;
			}
		}
		else {
			if (toDwtl != null) {
				return 1;
			}
		}
				
		Long thisNo = this.isExternalOrder() ? this.extOrder.getOrderNumber() : this.logonOrder.getOrderNumber();
		Long toNo = to.isExternalOrder() ? to.extOrder.getOrderNumber() : to.logonOrder.getOrderNumber();
		
		return thisNo.compareTo(toNo);
	}
	
	public boolean equals(Object o){
		OrderWrapper to = (OrderWrapper)o;
		if (isExternalOrder() != to.isExternalOrder()) {
			return false;
		}
		else if (isExternalOrder()) {
			return this.extOrder.getOrderNumber().equals(to.extOrder.getOrderNumber());
		}
		else {
			return this.logonOrder.getOrderNumber().equals(to.logonOrder.getOrderNumber());
		}
	}

	public Map<Long, String> getWagonNumbers() {
		return wagonNumbers;
	}

	public Set<String> getDistinctWagonNumbers() {
		return distinctWagonNumbers;
	}

	public Date getFirstSwitchingTimeDep() {
		return firstSwitchingTimeDep;
	}

	public void setFirstSwitchingTimeDep(Date d) {
		firstSwitchingTimeDep = d;
	}

	public Date getFirstSwitchingTimeArr() {
		return firstSwitchingTimeArr;
	}

	public void setFirstSwitchingTimeArr(Date d) {
		firstSwitchingTimeArr = d;
	}

	public String getFirstSwitchingTimeDeparture() {
		return userTimeZone.fmtAsDayMonthHourMinuteTrailingDot(firstSwitchingTimeDep);
	}

	public String getFirstSwitchingTimeArrival() {
		return userTimeZone.fmtAsDayMonthHourMinuteTrailingDot(firstSwitchingTimeArr);
	}

	public String getLastSwitchingTimeDeparture() {
		return userTimeZone.fmtAsDayMonthHourMinuteTrailingDot(lastSwitchingTimeDep);
	}

	public String getLastSwitchingTimeArrival() {
		return userTimeZone.fmtAsDayMonthHourMinuteTrailingDot(lastSwitchingTimeArr);
	}

	public String getDeliveryFrom() {
		return userTimeZone.fmtAsDayMonthTrailingDot(getDeliveryFromDate());
	}

	public String getDeliveryFromAsDayMonthYear4() {
		return userTimeZone.fmtAsDayMonthYear4(getDeliveryFromDate());
	}

	public Date getDeliveryFromDate() {
		if (logonOrder != null) {
			return logonOrder.getDeliveryFromDat();
		}
		if (extOrder != null) {
			return extOrder.getDeliveryFromDat();
		}
		return null;
	}

	public String getDeliveryTo() {
		return userTimeZone.fmtAsDayMonthTrailingDot(getDeliveryToDate());
	}

	public String getDeliveryToAsDayMonthYear4() {
		return userTimeZone.fmtAsDayMonthTrailingDot(getDeliveryToDate());
	}

	public Date getDeliveryToDate() {
		if (logonOrder != null) {
			return logonOrder.getDeliveryToDat();
		}
		if (extOrder != null) {
			return extOrder.getDeliveryToDat();
		}
		return null;
	}

	public boolean getIsContainerOnly() {
		return isContainerOnly;
	}

	public void initPlanningRevenueAttributes(DispoPlanningEntity revenuePlanning) {
		
		plannedTruckId = revenuePlanning.getTruckId();
		if ( revenuePlanning.getPlannedStart() != null && revenuePlanning.getOrderDeliveryDuration() != null ) {			
			plannerUnloadPlannedStart = new Date(revenuePlanning.getPlannedStart().getTime() + (revenuePlanning.getOrderDeliveryDuration().longValue() * 60 * 1000));
		}
		if ( this.logonOrder != null && this.logonOrder.getCarrierRequiresPlanningFlag() != null && this.logonOrder.getCarrierRequiresPlanningFlag() ) {
			
			isPreschedulingRequired = true;
			
			if (revenuePlanning.getPlannedStart() != null ) {
				//it is already planned by the prescheduler, this has to be shown differently on the UI!
				isPreschedulingDone = true;
			}		

			freightCost = revenuePlanning.getFreightCost() != null ? revenuePlanning.getFreightCost().doubleValue() : null;
			freightCurrency = revenuePlanning.getFreightCostCurrencyCd();		
		}
	}

	public boolean getIsPreschedulingRequired() {
		return isPreschedulingRequired;
	}

	public boolean getIsPreschedulingDone() {
		return isPreschedulingDone;
	}

	public Double getFreightCost() {
		return freightCost;
	}

	public String getFreightCurrency() {
		return freightCurrency;
	}

	public Long getPlannedTruckId() {
		return plannedTruckId;
	}
	
	public String getPlannerUnloadPlannedStart() {
		return (plannerUnloadPlannedStart != null) ? DateTimeUtils.formatHoursMinutes(plannerUnloadPlannedStart)  : "";
	}

	public List<DispoTruckTypeListEntity> getTruckTypeList() {
		return truckTypeList;
	}
	
	public void setTruckTypeList(List<DispoTruckTypeListEntity> truckTypeList) {
		this.truckTypeList = truckTypeList;
	}

	public Long getLogonPackageTypeCd() {
		return logonPackageTypeCd;
	}

	public void setLogonPackageTypeCd(Long logonPackageTypeCd) {
		this.logonPackageTypeCd = logonPackageTypeCd;
	}

	public boolean isEmptyPallet() {
		return isEmptyPallet;
	}

	public void setEmptyPallet(boolean isEmptyPallet) {
		this.isEmptyPallet = isEmptyPallet;
	}

	public boolean isLandfill() {
		return isLandfill;
	}

	public void setLandfill(boolean isLandfill) {
		this.isLandfill = isLandfill;
	}
}
