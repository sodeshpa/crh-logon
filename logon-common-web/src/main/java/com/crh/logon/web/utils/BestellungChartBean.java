/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.web.utils;

import com.crh.logon.business.services.utils.TimeZone;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * @author AdNovum Informatik AG
 */
public class BestellungChartBean implements Serializable {
	
	public static final String SESSION_KEY = "order_bestellung_BestellungChartBean";

	private List<Long> poolIds;
	private List<Long> carrierIds;
	private List<Long> truckTypeIds;
	private List<Integer> deliveryTypeIds;
	private int transCatId;
	private String plantId;
	private List<Long> shippingpointIds;
	private List<Integer> productTypeIds;
	private List<Long> wagonTrspCatIds;
	private List<Long> wagonTypeIds;
	private Set<Long> railwayStationIds;
	// nrOf loadings / nr of trucks
	private int chartType;
	
	private Date open1From;
	private Date open1To;
	private Date open2From;
	private Date open2To;
	private Date deliveryFrom;
	private Date deliveryTo;
	private String selectedDayStr;
	private List dayList;

	private TimeZone userTimeZone;

	public List getDayList() {
		return dayList;
	}

	public void setDayList(List dayList) {
		this.dayList = dayList;
	}

	public static BestellungChartBean getSessionInstance(HttpServletRequest request) {
		return (BestellungChartBean)request.getSession(true).getAttribute(SESSION_KEY);
	}
	
	public Date getDeliveryFrom() {
		return deliveryFrom;
	}
	public void setDeliveryFrom(Date deliveryFrom) {
		this.deliveryFrom = deliveryFrom;
	}
	public Date getDeliveryTo() {
		return deliveryTo;
	}
	public void setDeliveryTo(Date deliveryTo) {
		this.deliveryTo = deliveryTo;
	}
	
	public String getSelectedDayStr() {
		return selectedDayStr;
	}
	public void setSelectedDayStr(String selectedDayStr) {
		this.selectedDayStr = selectedDayStr;
	}

	public Date getOpen1From() {
		return open1From;
	}

	public void setOpen1From(Date open1From) {
		this.open1From = open1From;
	}

	public Date getOpen1To() {
		return open1To;
	}

	public void setOpen1To(Date open1To) {
		this.open1To = open1To;
	}

	public Date getOpen2From() {
		return open2From;
	}

	public void setOpen2From(Date open2From) {
		this.open2From = open2From;
	}

	public Date getOpen2To() {
		return open2To;
	}

	public void setOpen2To(Date open2To) {
		this.open2To = open2To;
	}

	public List<Long> getPoolIds() {
		return poolIds;
	}

	public void setPoolIds(List<Long> poolIds) {
		this.poolIds = poolIds;
	}

	public List<Long> getCarrierIds() {
		return carrierIds;
	}

	public void setCarrierIds(List<Long> carrierIds) {
		this.carrierIds = carrierIds;
	}

	public List<Long> getTruckTypeIds() {
		return truckTypeIds;
	}

	public void setTruckTypeIds(List<Long> truckTypeIds) {
		this.truckTypeIds = truckTypeIds;
	}

	public List<Integer> getDeliveryTypeIds() {
		return deliveryTypeIds;
	}

	public void setDeliveryTypeIds(List<Integer> deliveryTypeIds) {
		this.deliveryTypeIds = deliveryTypeIds;
	}

	public int getTransCatId() {
		return transCatId;
	}

	public void setTransCatId(int transCatId) {
		this.transCatId = transCatId;
	}

	public String getPlantId() {
		return plantId;
	}

	public void setPlantId(String plantId) {
		this.plantId = plantId;
	}

	public List<Long> getShippingpointIds() {
		return shippingpointIds;
	}

	public void setShippingpointIds(List<Long> shippingpointIds) {
		this.shippingpointIds = shippingpointIds;
	}

	public List<Integer> getProductTypeIds() {
		return productTypeIds;
	}

	public void setProductTypeIds(List<Integer> productTypeIds) {
		this.productTypeIds = productTypeIds;
	}

	public List<Long> getWagonTrspCatIds() {
		return wagonTrspCatIds;
	}

	public void setWagonTrspCatIds(List<Long> wagonTrspCatIds) {
		this.wagonTrspCatIds = wagonTrspCatIds;
	}

	public List<Long> getWagonTypeIds() {
		return wagonTypeIds;
	}

	public void setWagonTypeIds(List<Long> wagonTypeIds) {
		this.wagonTypeIds = wagonTypeIds;
	}

	public int getChartType() {
		return chartType;
	}

	public void setChartType(int chartType) {
		this.chartType = chartType;
	}

	public Set<Long> getRailwayStationIds() {
		return railwayStationIds;
	}

	public void setRailwayStationIds(Set<Long> railwayStationIds) {
		this.railwayStationIds = railwayStationIds;
	}

	public TimeZone getUserTimeZone() {
		return userTimeZone;
	}

	public void setUserTimeZone(TimeZone userTimeZone) {
		this.userTimeZone = userTimeZone;
	}
}
