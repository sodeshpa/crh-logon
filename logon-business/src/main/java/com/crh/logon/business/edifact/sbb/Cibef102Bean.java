/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.sbb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.crh.logon.business.edifact.messages.Message;

/**
 * @author AdNovum Informatik AG
 */
public class Cibef102Bean extends Message {

	public Cibef102Bean(String interchangeConrolReference, String messageReferenceNo) {
		
		super(interchangeConrolReference, messageReferenceNo);
	}

	private Date dateTimeOfMessage;
	private String senderCompanyNo;
	private String senderCompanyLocNo;
	
	private String tariffNo;
	private String tariffUnitCount;
	
	private String orderNo;
	
	private String transportPriority;
	
	private String actualDepCountryCode;
	private String actualDepRailwayStationNo;
	private String actualDepRailwayStationLoadingPoint;
	private String commDepCountryCode;
	private String commDepRailwayStationNo;
	
	private String actualArrCountryCode;
	private String actualArrRailwayStationNo;
	private String actualArrRailwayStationUnloadingPoint;
	private String commArrCountryCode;
	private String commArrRailwayStationNo;
	
	private String actualViaCountryCode;
	private String actualViaRailwayStationNo;
	private String commViaCountryCode;
	private String commViaRailwayStationNo;
	private String routeNumber;
	
	private String transportationMode;
	private String chargeCategory;
	private String chargeMethodOfPayment;
	
	private String consigneeCustomerNo;
	private String consigneeCustomerCountryCode;
	private String consigneeName;
	private String consigneeStreetAndHouseNo;
	private String consigneeCity;
	private String consigneePostalCode;
	
	private String consignorCustomerNo;
	private String consignorCustomerCountryCode;
	private String consignorName;
	
	private String despatchChargePayer;
	
	public class GoodsItem {
		
		private String itemNo;
		private String nhmCode;
		private String description;
		private BigDecimal netWeightT;
		
		public String getItemNo() {
			return itemNo;
		}

		public void setItemNo(String itemNo) {
			this.itemNo = itemNo;
		}

		public String getNhmCode() {
			return nhmCode;
		}

		public void setNhmCode(String nhmCode) {
			this.nhmCode = nhmCode;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public BigDecimal getNetWeightT() {
			return netWeightT;
		}

		public void setNetWeightT(BigDecimal netWeightT) {
			this.netWeightT = netWeightT;
		}
	}
	
	public class Wagon {
	
		private String wagonNo;
		private List<Container> containers;
		private List<GoodsItem> goodsItems;
		
		public String getWagonNo() {
			return wagonNo;
		}

		public void setWagonNo(String wagonNo) {
			this.wagonNo = wagonNo;
		}

		public List<Container> getContainers() {
			if (containers == null) {
				containers = new ArrayList<Container>();
			}
			return containers;
		}
		
		public List<GoodsItem> getGoodsItems() {
			if (goodsItems == null) {
				goodsItems = new ArrayList<GoodsItem>();
			}
			return goodsItems;
		}
	}
	private Wagon wagon;
	
	public class Container {
		
		private String containerNo;
		private String nhmCode;
		private String lengthCode;
		private BigDecimal tareWeightKg;
		private BigDecimal lengthMm;
		private BigDecimal widthMm;
		private BigDecimal heightMm;
		private List<GoodsItem> goodsItems;
		
		public String getContainerNo() {
			return containerNo;
		}

		public void setContainerNo(String containerNo) {
			this.containerNo = containerNo;
		}

		public String getNhmCode() {
			return nhmCode;
		}

		public void setNhmCode(String nhmCode) {
			this.nhmCode = nhmCode;
		}

		public String getLengthCode() {
			return lengthCode;
		}

		public void setLengthCode(String lengthCode) {
			this.lengthCode = lengthCode;
		}

		public BigDecimal getTareWeightKg() {
			return tareWeightKg;
		}

		public void setTareWeightKg(BigDecimal tareWeightKg) {
			this.tareWeightKg = tareWeightKg;
		}

		public BigDecimal getLengthMm() {
			return lengthMm;
		}

		public void setLengthMm(BigDecimal lengthMm) {
			this.lengthMm = lengthMm;
		}

		public BigDecimal getWidthMm() {
			return widthMm;
		}

		public void setWidthMm(BigDecimal widthMm) {
			this.widthMm = widthMm;
		}

		public BigDecimal getHeightMm() {
			return heightMm;
		}

		public void setHeightMm(BigDecimal heightMm) {
			this.heightMm = heightMm;
		}
		
		public List<GoodsItem> getGoodsItems() {
			if (goodsItems == null) {
				goodsItems = new ArrayList<GoodsItem>();
			}
			return goodsItems;
		}
	}

	public Date getDateTimeOfMessage() {
		return dateTimeOfMessage;
	}

	public void setDateTimeOfMessage(Date dateTimeOfMessage) {
		this.dateTimeOfMessage = dateTimeOfMessage;
	}

	public String getSenderCompanyNo() {
		return senderCompanyNo;
	}

	public void setSenderCompanyNo(String senderCompanyNo) {
		this.senderCompanyNo = senderCompanyNo;
	}

	public String getSenderCompanyLocNo() {
		return senderCompanyLocNo;
	}

	public void setSenderCompanyLocNo(String senderCompanyLocNo) {
		this.senderCompanyLocNo = senderCompanyLocNo;
	}

	public String getTariffNo() {
		return tariffNo;
	}

	public void setTariffNo(String tariffNo) {
		this.tariffNo = tariffNo;
	}

	public String getTariffUnitCount() {
		return tariffUnitCount;
	}

	public void setTariffUnitCount(String tariffUnitCount) {
		this.tariffUnitCount = tariffUnitCount;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTransportPriority() {
		return transportPriority;
	}

	public void setTransportPriority(String transportPriority) {
		this.transportPriority = transportPriority;
	}

	public String getActualDepCountryCode() {
		return actualDepCountryCode;
	}

	public void setActualDepCountryCode(String actualDepCountryCode) {
		this.actualDepCountryCode = actualDepCountryCode;
	}

	public String getActualDepRailwayStationNo() {
		return actualDepRailwayStationNo;
	}

	public void setActualDepRailwayStationNo(String actualDepRailwayStationNo) {
		this.actualDepRailwayStationNo = actualDepRailwayStationNo;
	}

	public String getActualDepRailwayStationLoadingPoint() {
		return actualDepRailwayStationLoadingPoint;
	}

	public void setActualDepRailwayStationLoadingPoint(String actualDepRailwayStationLoadingPoint) {
		this.actualDepRailwayStationLoadingPoint = actualDepRailwayStationLoadingPoint;
	}

	public String getCommDepCountryCode() {
		return commDepCountryCode;
	}

	public void setCommDepCountryCode(String commDepCountryCode) {
		this.commDepCountryCode = commDepCountryCode;
	}

	public String getCommDepRailwayStationNo() {
		return commDepRailwayStationNo;
	}

	public void setCommDepRailwayStationNo(String commDepRailwayStationNo) {
		this.commDepRailwayStationNo = commDepRailwayStationNo;
	}

	public String getActualArrCountryCode() {
		return actualArrCountryCode;
	}

	public void setActualArrCountryCode(String actualArrCountryCode) {
		this.actualArrCountryCode = actualArrCountryCode;
	}

	public String getActualArrRailwayStationNo() {
		return actualArrRailwayStationNo;
	}

	public void setActualArrRailwayStationNo(String actualArrRailwayStationNo) {
		this.actualArrRailwayStationNo = actualArrRailwayStationNo;
	}
	
	public String getActualArrRailwayStationUnloadingPoint() {
		return actualArrRailwayStationUnloadingPoint;
	}

	public void setActualArrRailwayStationUnloadingPoint(String actualArrRailwayStationUnloadingPoint) {
		this.actualArrRailwayStationUnloadingPoint = actualArrRailwayStationUnloadingPoint;
	}

	public String getCommArrCountryCode() {
		return commArrCountryCode;
	}

	public void setCommArrCountryCode(String commArrCountryCode) {
		this.commArrCountryCode = commArrCountryCode;
	}

	public String getCommArrRailwayStationNo() {
		return commArrRailwayStationNo;
	}

	public void setCommArrRailwayStationNo(String commArrRailwayStationNo) {
		this.commArrRailwayStationNo = commArrRailwayStationNo;
	}

	public String getActualViaCountryCode() {
		return actualViaCountryCode;
	}

	public void setActualViaCountryCode(String actualViaCountryCode) {
		this.actualViaCountryCode = actualViaCountryCode;
	}

	public String getActualViaRailwayStationNo() {
		return actualViaRailwayStationNo;
	}

	public void setActualViaRailwayStationNo(String actualViaRailwayStationNo) {
		this.actualViaRailwayStationNo = actualViaRailwayStationNo;
	}

	public String getCommViaCountryCode() {
		return commViaCountryCode;
	}

	public void setCommViaCountryCode(String commViaCountryCode) {
		this.commViaCountryCode = commViaCountryCode;
	}

	public String getCommViaRailwayStationNo() {
		return commViaRailwayStationNo;
	}

	public void setCommViaRailwayStationNo(String commViaRailwayStationNo) {
		this.commViaRailwayStationNo = commViaRailwayStationNo;
	}

	public String getRouteNumber() {
		return routeNumber;
	}

	public void setRouteNumber(String routeNumber) {
		this.routeNumber = routeNumber;
	}

	public String getTransportationMode() {
		return transportationMode;
	}

	public void setTransportationMode(String transportationMode) {
		this.transportationMode = transportationMode;
	}

	public String getChargeCategory() {
		return chargeCategory;
	}

	public void setChargeCategory(String chargeCategory) {
		this.chargeCategory = chargeCategory;
	}

	public String getChargeMethodOfPayment() {
		return chargeMethodOfPayment;
	}

	public void setChargeMethodOfPayment(String chargeMethodOfPayment) {
		this.chargeMethodOfPayment = chargeMethodOfPayment;
	}

	public String getConsigneeCustomerNo() {
		return consigneeCustomerNo;
	}

	public String getDespatchChargePayer() {
		return despatchChargePayer;
	}

	public void setDespatchChargePayer(String despatchChargePayer) {
		this.despatchChargePayer = despatchChargePayer;
	}

	public void setConsigneeCustomerNo(String consigneeCustomerNo) {
		this.consigneeCustomerNo = consigneeCustomerNo;
	}

	public String getConsigneeCustomerCountryCode() {
		return consigneeCustomerCountryCode;
	}

	public void setConsigneeCustomerCountryCode(String consigneeCustomerCountryCode) {
		this.consigneeCustomerCountryCode = consigneeCustomerCountryCode;
	}

	public String getConsigneeName() {
		return consigneeName;
	}

	public void setConsigneeName(String consigneeName) {
		this.consigneeName = consigneeName;
	}

	public String getConsigneeStreetAndHouseNo() {
		return consigneeStreetAndHouseNo;
	}

	public void setConsigneeStreetAndHouseNo(String consigneeStreetAndHouseNo) {
		this.consigneeStreetAndHouseNo = consigneeStreetAndHouseNo;
	}

	public String getConsigneeCity() {
		return consigneeCity;
	}

	public void setConsigneeCity(String consigneeCity) {
		this.consigneeCity = consigneeCity;
	}

	public String getConsigneePostalCode() {
		return consigneePostalCode;
	}

	public void setConsigneePostalCode(String consigneePostalCode) {
		this.consigneePostalCode = consigneePostalCode;
	}

	public String getConsignorCustomerNo() {
		return consignorCustomerNo;
	}

	public void setConsignorCustomerNo(String consignorCustomerNo) {
		this.consignorCustomerNo = consignorCustomerNo;
	}

	public String getConsignorCustomerCountryCode() {
		return consignorCustomerCountryCode;
	}

	public void setConsignorCustomerCountryCode(String consignorCustomerCountryCode) {
		this.consignorCustomerCountryCode = consignorCustomerCountryCode;
	}

	public String getConsignorName() {
		return consignorName;
	}

	public void setConsignorName(String consignorName) {
		this.consignorName = consignorName;
	}

	public Wagon getWagon() {
		return wagon;
	}

	public void setWagon(Wagon wagon) {
		this.wagon = wagon;
	}
}
