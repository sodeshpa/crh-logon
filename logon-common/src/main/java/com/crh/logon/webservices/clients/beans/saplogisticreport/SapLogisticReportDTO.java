/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.webservices.clients.beans.saplogisticreport;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

/**
 * @author AdNovum Informatik AG
 * 
 * Data Transfer Object from business tier to the webservice client 
 * tier. This DTO has to 
 */
public class SapLogisticReportDTO implements Serializable {

	private static final long serialVersionUID = -7068333851625147963L;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDOC_1 
	 */
	private BigInteger orderNumber;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDOC_2
	 */
	private String lifexNumber;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LFLAG_7
	 */
	private Boolean ontimeDeliveryInd;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDUR_1
	 */
	private BigInteger customerDeliveryTimeWindow;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LG_1
	 */
	private Calendar deliveryToDate;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LG_2
	 */
	private Calendar deliveryFromDate;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LORT_2
	 */
	private BigInteger orderCreationTypeId;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDIST_1
	 */
	private float transportDistance;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDUR_2
	 */
	private BigInteger drivingTime;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LCYTM_1
	 */
	private Long cycleTimeAtShippingPoint;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LG_3
	 */
	private Calendar enterShippingPointBiz;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LG_6
	 */
	private Calendar leaveShippingPointBiz;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDUR_4
	 */
	private BigInteger loadingTimeMinBiz;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LG_4
	 */
	private Calendar startLoadingBiz;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LG_5
	 */
	private Calendar endLoadingBiz;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LG_7
	 */
	private Calendar transmissionToRailwayCompanyBiz;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDUR_3
	 */
	private BigInteger cycleTimeSTBiz;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LG_8
	 */
	private Calendar enterShiptoBiz;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LG_11
	 */
	private Calendar leaveShiptoBiz;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDUR_5
	 */
	private BigInteger unloadingTimeBiz;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LG_9
	 */
	private Calendar startUnloadBiz;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LG_10
	 */
	private Calendar endUnloadBiz;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LFLAG_1
	 */
	private BigInteger deviationInDeliveryType;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LTYP_3
	 */
	private BigInteger deliveryTypeId;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LFLAG_5
	 */
	private int shorttermOrderInd;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LFLAG_9
	 */
	private int pdaOrRailInd;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LTYP_2
	 */
	private BigInteger shippingPointTypeId;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LSHI_1
	 */
	private BigInteger shippingPointRailwayStationCountryCd;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LSHI_2
	 */
	private BigInteger shippingPointRailwayStationNumber;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LSHI_3
	 */
	private String shippingPointRailwayStationName;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LSPCO_1
	 */
	private String shippingPointCoordinatesLat;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LSPCO_2
	 */
	private String shippingPointCoordinatesLon;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LTYP_1
	 */
	private BigInteger shiptoTypeId;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LSHCO_1
	 */
	private String shiptoCoordinatesLat;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LSHCO_2
	 */
	private String shiptoCoordinatesLon;


	/**
	 * WS field to SAP: LogisticReportCreation / header / LWAIT_1
	 */
	private String waitingTimeReasonShipto;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LPOOl_1
	 */
	private String poolId;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDRV_1
	 */
	private String truckDriverID;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDRV_2
	 */
	private String truckDriverName;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LTYP_4
	 */
	private String wagonTypeId;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LCRE_1
	 */
	private String deliveryCreatedBy;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LCRE_2
	 */
	private Calendar deliveryCreatedOn;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDUM_1
	 */
	private String dummyString;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDUM_2
	 */
	private String dummyString2;

	/*
	 * WS field to SAP: LogisticReportCreation / header / LDUM_4
	 */
	private int dummyInt;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDUM_5
	 */
	private float dummyFloat;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDUM_7
	 */
	private String dummyCalendar;

	/**
	 * WS field to SAP: LogisticReportCreation / header / LDUM_8
	 */
	private String dummyCalendar2;

	/**
	 * WS field to SAP: LogisticReportCreation / header / ?????
	 */
	private List<SapLogisticReportItemDTO> reportItemList;

	/**
	 * @return the orderItemList
	 */
	public List<SapLogisticReportItemDTO> getReportItemList() {
		return reportItemList;
	}

	/**
	 * @param orderItemList the orderItemList to set
	 */
	public void setReportItemList(List<SapLogisticReportItemDTO> reportItemList) {
		this.reportItemList = reportItemList;
	}

	/**
	 * @return the shippingPointCoordinatesLat
	 */
	public String getShippingPointCoordinatesLat() {
		return shippingPointCoordinatesLat;
	}

	/**
	 * @param shippingPointCoordinatesLat the shippingPointCoordinatesLat to set
	 */
	public void setShippingPointCoordinatesLat(String shippingPointCoordinatesLat) {
		this.shippingPointCoordinatesLat = shippingPointCoordinatesLat;
	}

	/**
	 * @return the shippingPointCoordinatesLon
	 */
	public String getShippingPointCoordinatesLon() {
		return shippingPointCoordinatesLon;
	}

	/**
	 * @param shippingPointCoordinatesLon the shippingPointCoordinatesLon to set
	 */
	public void setShippingPointCoordinatesLon(String shippingPointCoordinatesLon) {
		this.shippingPointCoordinatesLon = shippingPointCoordinatesLon;
	}

	/**
	 * @return the shiptoCoordinatesLat
	 */
	public String getShiptoCoordinatesLat() {
		return shiptoCoordinatesLat;
	}

	/**
	 * @param shiptoCoordinatesLat the shiptoCoordinatesLat to set
	 */
	public void setShiptoCoordinatesLat(String shiptoCoordinatesLat) {
		this.shiptoCoordinatesLat = shiptoCoordinatesLat;
	}

	/**
	 * @return the shiptoCoordinateLon
	 */
	public String getShiptoCoordinatesLon() {
		return shiptoCoordinatesLon;
	}

	/**
	 * @param shiptoCoordinateLon the shiptoCoordinateLon to set
	 */
	public void setShiptoCoordinatesLon(String shiptoCoordinateLon) {
		this.shiptoCoordinatesLon = shiptoCoordinateLon;
	}

	/**
	 * @return the waitingTimeReasonShipto
	 */
	public String getWaitingTimeReasonShipto() {
		return waitingTimeReasonShipto;
	}

	/**
	 * @param waitingTimeReasonShipto the waitingTimeReasonShipto to set
	 */
	public void setWaitingTimeReasonShipto(String waitingTimeReasonShipto) {
		this.waitingTimeReasonShipto = waitingTimeReasonShipto;
	}

	/**
	 * @return the poolId
	 */
	public String getPoolId() {
		return poolId;
	}

	/**
	 * @param poolId the poolId to set
	 */
	public void setPoolId(String poolId) {
		this.poolId = poolId;
	}

	/**
	 * @return the shippingPointTypeId
	 */
	public BigInteger getShippingPointTypeId() {
		return shippingPointTypeId;
	}

	/**
	 * @param shippingPointTypeId the shippingPointTypeId to set
	 */
	public void setShippingPointTypeId(BigInteger shippingPointTypeId) {
		this.shippingPointTypeId = shippingPointTypeId;
	}

	/**
	 * @return the deliveryTypeId
	 */
	public BigInteger getDeliveryTypeId() {
		return deliveryTypeId;
	}

	/**
	 * @param deliveryTypeId the deliveryTypeId to set
	 */
	public void setDeliveryTypeId(BigInteger deliveryTypeId) {
		this.deliveryTypeId = deliveryTypeId;
	}

	/**
	 * @return the truckDriverID
	 */
	public String getTruckDriverID() {
		return truckDriverID;
	}

	/**
	 * @param truckDriverID the truckDriverID to set
	 */
	public void setTruckDriverID(String truckDriverID) {
		this.truckDriverID = truckDriverID;
	}

	/**
	 * @return the truckDriverName
	 */
	public String getTruckDriverName() {
		return truckDriverName;
	}

	/**
	 * @param truckDriverName the truckDriverName to set
	 */
	public void setTruckDriverName(String truckDriverName) {
		this.truckDriverName = truckDriverName;
	}

	/**
	 * @return the deliveryCreatedBy
	 */
	public String getDeliveryCreatedBy() {
		return deliveryCreatedBy;
	}

	/**
	 * @param deliveryCreatedBy the deliveryCreatedBy to set
	 */
	public void setDeliveryCreatedBy(String deliveryCreatedBy) {
		this.deliveryCreatedBy = deliveryCreatedBy;
	}

	/**
	 * @return the deliveryCreatedOn
	 */
	public Calendar getDeliveryCreatedOn() {
		return deliveryCreatedOn;
	}

	/**
	 * @param deliveryCreatedOn the deliveryCreatedOn to set
	 */
	public void setDeliveryCreatedOn(Calendar deliveryCreatedOn) {
		this.deliveryCreatedOn = deliveryCreatedOn;
	}

	/**
	 * @return the shiptoTypeId
	 */
	public BigInteger getShiptoTypeId() {
		return shiptoTypeId;
	}

	/**
	 * @param shiptoTypeId the shiptoTypeId to set
	 */
	public void setShiptoTypeId(BigInteger shiptoTypeId) {
		this.shiptoTypeId = shiptoTypeId;
	}

	/**
	 * @return the wagonTypeId
	 */
	public String getWagonTypeId() {
		return wagonTypeId;
	}

	/**
	 * @param wagonTypeId the wagonTypeId to set
	 */
	public void setWagonTypeId(String wagonTypeId) {
		this.wagonTypeId = wagonTypeId;
	}

	/**
	 * @return the shippingPointRailwayStationCountryCd
	 */
	public BigInteger getShippingPointRailwayStationCountryCd() {
		return shippingPointRailwayStationCountryCd;
	}

	/**
	 * @param shippingPointRailwayStationCountryCd the shippingPointRailwayStationCountryCd to set
	 */
	public void setShippingPointRailwayStationCountryCd(BigInteger shippingPointRailwayStationCountryCd) {
		this.shippingPointRailwayStationCountryCd = shippingPointRailwayStationCountryCd;
	}

	/**
	 * @return the shippingPointRailwayStationNumber
	 */
	public BigInteger getShippingPointRailwayStationNumber() {
		return shippingPointRailwayStationNumber;
	}

	/**
	 * @param shippingPointRailwayStationNumber the shippingPointRailwayStationNumber to set
	 */
	public void setShippingPointRailwayStationNumber(BigInteger shippingPointRailwayStationNumber) {
		this.shippingPointRailwayStationNumber = shippingPointRailwayStationNumber;
	}

	/**
	 * @return the shippingPointRailwayStationName
	 */
	public String getShippingPointRailwayStationName() {
		return shippingPointRailwayStationName;
	}

	/**
	 * @param shippingPointRailwayStationName the shippingPointRailwayStationName to set
	 */
	public void setShippingPointRailwayStationName(String shippingPointRailwayStationName) {
		this.shippingPointRailwayStationName = shippingPointRailwayStationName;
	}

	/**
	 * @return the deliveryToDate
	 */
	public Calendar getDeliveryToDate() {
		return deliveryToDate;
	}

	/**
	 * @param deliveryToDate the deliveryToDate to set
	 */
	public void setDeliveryToDate(Calendar deliveryToDate) {
		this.deliveryToDate = deliveryToDate;
	}

	/**
	 * @return the deliveryFromDate
	 */
	public Calendar getDeliveryFromDate() {
		return deliveryFromDate;
	}

	/**
	 * @param deliveryFromDate the deliveryFromDate to set
	 */
	public void setDeliveryFromDate(Calendar deliveryFromDate) {
		this.deliveryFromDate = deliveryFromDate;
	}

	/**
	 * @return the enterShippingPointBiz
	 */
	public Calendar getEnterShippingPointBiz() {
		return enterShippingPointBiz;
	}

	/**
	 * @param enterShippingPointBiz the enterShippingPointBiz to set
	 */
	public void setEnterShippingPointBiz(Calendar enterShippingPointBiz) {
		this.enterShippingPointBiz = enterShippingPointBiz;
	}

	/**
	 * @return the leaveShippingPointBiz
	 */
	public Calendar getLeaveShippingPointBiz() {
		return leaveShippingPointBiz;
	}

	/**
	 * @param leaveShippingPointBiz the leaveShippingPointBiz to set
	 */
	public void setLeaveShippingPointBiz(Calendar leaveShippingPointBiz) {
		this.leaveShippingPointBiz = leaveShippingPointBiz;
	}

	/**
	 * @return the startLoadingBiz
	 */
	public Calendar getStartLoadingBiz() {
		return startLoadingBiz;
	}

	/**
	 * @param startLoadingBiz the startLoadingBiz to set
	 */
	public void setStartLoadingBiz(Calendar startLoadingBiz) {
		this.startLoadingBiz = startLoadingBiz;
	}

	/**
	 * @return the endLoadingBiz
	 */
	public Calendar getEndLoadingBiz() {
		return endLoadingBiz;
	}

	/**
	 * @param endLoadingBiz the endLoadingBiz to set
	 */
	public void setEndLoadingBiz(Calendar endLoadingBiz) {
		this.endLoadingBiz = endLoadingBiz;
	}

	/**
	 * @return the transmissionToRailwayCompanyBiz
	 */
	public Calendar getTransmissionToRailwayCompanyBiz() {
		return transmissionToRailwayCompanyBiz;
	}

	/**
	 * @param transmissionToRailwayCompanyBiz the transmissionToRailwayCompanyBiz to set
	 */
	public void setTransmissionToRailwayCompanyBiz(Calendar transmissionToRailwayCompanyBiz) {
		this.transmissionToRailwayCompanyBiz = transmissionToRailwayCompanyBiz;
	}

	/**
	 * @return the enterShiptoBiz
	 */
	public Calendar getEnterShiptoBiz() {
		return enterShiptoBiz;
	}

	/**
	 * @param enterShiptoBiz the enterShiptoBiz to set
	 */
	public void setEnterShiptoBiz(Calendar enterShiptoBiz) {
		this.enterShiptoBiz = enterShiptoBiz;
	}

	/**
	 * @return the startUnloadBiz
	 */
	public Calendar getStartUnloadBiz() {
		return startUnloadBiz;
	}

	/**
	 * @param startUnloadBiz the startUnloadBiz to set
	 */
	public void setStartUnloadBiz(Calendar startUnloadBiz) {
		this.startUnloadBiz = startUnloadBiz;
	}

	/**
	 * @return the endUnloadBiz
	 */
	public Calendar getEndUnloadBiz() {
		return endUnloadBiz;
	}

	/**
	 * @param endUnloadBiz the endUnloadBiz to set
	 */
	public void setEndUnloadBiz(Calendar endUnloadBiz) {
		this.endUnloadBiz = endUnloadBiz;
	}

	/**
	 * @return the leaveShiptoBiz
	 */
	public Calendar getLeaveShiptoBiz() {
		return leaveShiptoBiz;
	}

	/**
	 * @param leaveShiptoBiz the leaveShiptoBiz to set
	 */
	public void setLeaveShiptoBiz(Calendar leaveShiptoBiz) {
		this.leaveShiptoBiz = leaveShiptoBiz;
	}

	/**
	 * @return the orderNumber
	 */
	public BigInteger getOrderNumber() {
		return orderNumber;
	}

	/**
	 * @param orderNumber the orderNumber to set
	 */
	public void setOrderNumber(BigInteger orderNumber) {
		this.orderNumber = orderNumber;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SapLogisticReportDTO [orderNumber=" + orderNumber + "]";
	}

	/**
	 * @return the lifexNumber
	 */
	public String getLifexNumber() {
		return lifexNumber;
	}

	/**
	 * @param lifexNumber the lifexNumber to set
	 */
	public void setLifexNumber(String lifexNumber) {
		this.lifexNumber = lifexNumber;
	}

	/**
	 * @return the ontimeDeliveryInd
	 */
	public Boolean getOntimeDeliveryInd() {
		return ontimeDeliveryInd;
	}

	/**
	 * @param ontimeDeliveryInd the ontimeDeliveryInd to set
	 */
	public void setOntimeDeliveryInd(Boolean ontimeDeliveryInd) {
		this.ontimeDeliveryInd = ontimeDeliveryInd;
	}

	/**
	 * @return the customerDeliveryTimeWindow
	 */
	public BigInteger getCustomerDeliveryTimeWindow() {
		return customerDeliveryTimeWindow;
	}

	/**
	 * @param customerDeliveryTimeWindow the customerDeliveryTimeWindow to set
	 */
	public void setCustomerDeliveryTimeWindow(BigInteger customerDeliveryTimeWindow) {
		this.customerDeliveryTimeWindow = customerDeliveryTimeWindow;
	}

	/**
	 * @return the orderCreationTypeId
	 */
	public BigInteger getOrderCreationTypeId() {
		return orderCreationTypeId;
	}

	/**
	 * @param orderCreationTypeId the orderCreationTypeId to set
	 */
	public void setOrderCreationTypeId(BigInteger orderCreationTypeId) {
		this.orderCreationTypeId = orderCreationTypeId;
	}

	/**
	 * @return the transportDistance
	 */
	public float getTransportDistance() {
		return transportDistance;
	}

	/**
	 * @param transportDistance the transportDistance to set
	 */
	public void setTransportDistance(float transportDistance) {
		this.transportDistance = transportDistance;
	}

	/**
	 * @return the drivingTime
	 */
	public BigInteger getDrivingTime() {
		return drivingTime;
	}

	/**
	 * @param drivingTime the drivingTime to set
	 */
	public void setDrivingTime(BigInteger drivingTime) {
		this.drivingTime = drivingTime;
	}

	/**
	 * @return the cycleTime
	 */
	public Long getCycleTimeAtShippingPoint() {
		return cycleTimeAtShippingPoint;
	}

	/**
	 * @param cycleTime the cycleTime to set
	 */
	public void setCycleTimeAtShippingPoint(Long cycleTime) {
		this.cycleTimeAtShippingPoint = cycleTime;
	}

	/**
	 * @return the loadingTimeMinBiz
	 */
	public BigInteger getLoadingTimeMinBiz() {
		return loadingTimeMinBiz;
	}

	/**
	 * @param loadingTimeMinBiz the loadingTimeMinBiz to set
	 */
	public void setLoadingTimeMinBiz(BigInteger loadingTimeMinBiz) {
		this.loadingTimeMinBiz = loadingTimeMinBiz;
	}

	/**
	 * @return the cycleTimeBiz
	 */
	public BigInteger getCycleTimeSTBiz() {
		return cycleTimeSTBiz;
	}

	/**
	 * @param cycleTimeBiz the cycleTimeBiz to set
	 */
	public void setCycleTimeSTBiz(BigInteger cycleTimeBiz) {
		this.cycleTimeSTBiz = cycleTimeBiz;
	}

	/**
	 * @return the unloadingTimeBiz
	 */
	public BigInteger getUnloadingTimeBiz() {
		return unloadingTimeBiz;
	}

	/**
	 * @param unloadingTimeBiz the unloadingTimeBiz to set
	 */
	public void setUnloadingTimeBiz(BigInteger unloadingTimeBiz) {
		this.unloadingTimeBiz = unloadingTimeBiz;
	}

	/**
	 * @return the deviationInDeliveryType
	 */
	public BigInteger getDeviationInDeliveryType() {
		return deviationInDeliveryType;
	}

	/**
	 * @param deviationInDeliveryType the deviationInDeliveryType to set
	 */
	public void setDeviationInDeliveryType(BigInteger deviationInDeliveryType) {
		this.deviationInDeliveryType = deviationInDeliveryType;
	}

	/**
	 * @return the shorttermOrderInd
	 */
	public int getShorttermOrderInd() {
		return shorttermOrderInd;
	}

	/**
	 * @param shorttermOrderInd the shorttermOrderInd to set
	 */
	public void setShorttermOrderInd(int shorttermOrderInd) {
		this.shorttermOrderInd = shorttermOrderInd;
	}

	/**
	 * @return the pdaOrRailInd
	 */
	public int getPdaOrRailInd() {
		return pdaOrRailInd;
	}

	/**
	 * @param pdaOrRailInd the pdaOrRailInd to set
	 */
	public void setPdaOrRailInd(int pdaOrRailInd) {
		this.pdaOrRailInd = pdaOrRailInd;
	}

	/**
	 * @return the dummyString
	 */
	public String getDummyString() {
		return dummyString;
	}

	/**
	 * @param dummyString the dummyString to set
	 */
	public void setDummyString(String dummyString) {
		this.dummyString = dummyString;
	}

	/**
	 * @return the dummyString2
	 */
	public String getDummyString2() {
		return dummyString2;
	}

	/**
	 * @param dummyString2 the dummyString2 to set
	 */
	public void setDummyString2(String dummyString2) {
		this.dummyString2 = dummyString2;
	}

	/**
	 * @return the dummyInt
	 */
	public int getDummyInt() {
		return dummyInt;
	}

	/**
	 * @param dummyInt the dummyInt to set
	 */
	public void setDummyInt(int dummyInt) {
		this.dummyInt = dummyInt;
	}

	/**
	 * @return the dummyFloat
	 */
	public float getDummyFloat() {
		return dummyFloat;
	}

	/**
	 * @param dummyFloat the dummyFloat to set
	 */
	public void setDummyFloat(float dummyFloat) {
		this.dummyFloat = dummyFloat;
	}

	/**
	 * @return the dummyCalendar
	 */
	public String getDummyCalendar() {
		return dummyCalendar;
	}

	/**
	 * @param dummyCalendar the dummyCalendar to set
	 */
	public void setDummyCalendar(String dummyCalendar) {
		this.dummyCalendar = dummyCalendar;
	}

	/**
	 * @return the dummyCalendar2
	 */
	public String getDummyCalendar2() {
		return dummyCalendar2;
	}

	/**
	 * @param dummyCalendar2 the dummyCalendar2 to set
	 */
	public void setDummyCalendar2(String dummyCalendar2) {
		this.dummyCalendar2 = dummyCalendar2;
	}
	
}
