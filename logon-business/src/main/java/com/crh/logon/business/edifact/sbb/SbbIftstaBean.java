/*
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.sbb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.crh.logon.business.edifact.messages.Iftsta;

/**
 * @author AdNovum Informatik AG
 */
public class SbbIftstaBean extends Iftsta {

	public class Consignment {

		private String shipmentSequenceNo;
		private String consignmentNoteNo;
		private String departureRailwayStation;
		private String destinationRailwayStation;
		private String endRailwayStation;
			
		public class Status {
			private String statusCode;
			private String transportationOrderNo;
			private String carrierReferenceNo;
			private String senderReference;
			private Date statusChangeDate;
			private Date plannedArrivalDateEndRailwayStation;
			private String statusText;
			private String damageRemarks;
			private String trainNo;
			private String evuNo;
			private Date scheduledDepartureDate;
			private String currentRailwayStation;
			private Date departureDate;
			private Date arrivalDate;
			private String nextHandlingRailwayStation;
			
			public class Wagon {
				private String wagonNo;
				private boolean loaded;
				private String loadUnitNo;
				
				public String getWagonNo() {
					return wagonNo;
				}
				public void setWagonNo(String wagonNo) {
					this.wagonNo = wagonNo;
				}
				public boolean isLoaded() {
					return loaded;
				}
				public void setLoaded(boolean loaded) {
					this.loaded = loaded;
				}
				public String getLoadUnitNo() {
					return loadUnitNo;
				}
				public void setLoadUnitNo(String loadUnitNo) {
					this.loadUnitNo = loadUnitNo;
				}
			}
			private List<Wagon> wagonList = new ArrayList<Wagon>();
			
			public class Container {
				private String loadUnitNo;

				public String getLoadUnitNo() {
					return loadUnitNo;
				}
				public void setLoadUnitNo(String loadUnitNo) {
					this.loadUnitNo = loadUnitNo;
				}
			}
			private List<Container> containerList = new ArrayList<Container>();
			
			public String getStatusCode() {
				return statusCode;
			}
			public void setStatusCode(String statusCode) {
				this.statusCode = statusCode;
			}
			public String getTransportationOrderNo() {
				return transportationOrderNo;
			}
			public void setTransportationOrderNo(String transportationOrderNo) {
				this.transportationOrderNo = transportationOrderNo;
			}
			public String getCarrierReferenceNo() {
				return carrierReferenceNo;
			}
			public void setCarrierReferenceNo(String carrierReferenceNo) {
				this.carrierReferenceNo = carrierReferenceNo;
			}
			public String getSenderReference() {
				return senderReference;
			}
			public void setSenderReference(String senderReference) {
				this.senderReference = senderReference;
			}
			public Date getStatusChangeDate() {
				return statusChangeDate;
			}
			public void setStatusChangeDate(Date statusChangeDate) {
				this.statusChangeDate = statusChangeDate;
			}
			public Date getPlannedArrivalDateEndRailwayStation() {
				return plannedArrivalDateEndRailwayStation;
			}
			public void setPlannedArrivalDateEndRailwayStation(Date plannedArrivalDateEndRailwayStation) {
				this.plannedArrivalDateEndRailwayStation = plannedArrivalDateEndRailwayStation;
			}
			public String getStatusText() {
				return statusText;
			}
			public void setStatusText(String statusText) {
				this.statusText = statusText;
			}
			public String getDamageRemarks() {
				return damageRemarks;
			}
			public void setDamageRemarks(String damageRemarks) {
				this.damageRemarks = damageRemarks;
			}
			public String getTrainNo() {
				return trainNo;
			}
			public void setTrainNo(String trainNo) {
				this.trainNo = trainNo;
			}
			public String getEvuNo() {
				return evuNo;
			}
			public void setEvuNo(String evuNo) {
				this.evuNo = evuNo;
			}
			public Date getScheduledDepartureDate() {
				return scheduledDepartureDate;
			}
			public void setScheduledDepartureDate(Date scheduledDepartureDate) {
				this.scheduledDepartureDate = scheduledDepartureDate;
			}
			public String getCurrentRailwayStation() {
				return currentRailwayStation;
			}
			public void setCurrentRailwayStation(String currentRailwayStation) {
				this.currentRailwayStation = currentRailwayStation;
			}
			public Date getDepartureDate() {
				return departureDate;
			}
			public void setDepartureDate(Date departureDate) {
				this.departureDate = departureDate;
			}
			public Date getArrivalDate() {
				return arrivalDate;
			}
			public void setArrivalDate(Date arrivalDate) {
				this.arrivalDate = arrivalDate;
			}
			public String getNextHandlingRailwayStation() {
				return nextHandlingRailwayStation;
			}
			public void setNextHandlingRailwayStation(String nextHandlingRailwayStation) {
				this.nextHandlingRailwayStation = nextHandlingRailwayStation;
			}
			public List<Wagon> getWagonList() {
				return wagonList;
			}
			public List<Container> getContainerList() {
				return containerList;
			}
		}
		private List<Status> statusList = new ArrayList<Status>();
		
		public Consignment() {
		}
		
		public String getShipmentSequenceNo() {
			return shipmentSequenceNo;
		}
		public void setShipmentSequenceNo(String shipmentSequenceNo) {
			this.shipmentSequenceNo = shipmentSequenceNo;
		}
		public String getConsignmentNoteNo() {
			return consignmentNoteNo;
		}
		public void setConsignmentNoteNo(String consignmentNoteNo) {
			this.consignmentNoteNo = consignmentNoteNo;
		}
		public String getDestinationRailwayStation() {
			return destinationRailwayStation;
		}
		public void setDestinationRailwayStation(String destinationRailwayStation) {
			this.destinationRailwayStation = destinationRailwayStation;
		}
		public String getDepartureRailwayStation() {
			return departureRailwayStation;
		}
		public void setDepartureRailwayStation(String departureRailwayStation) {
			this.departureRailwayStation = departureRailwayStation;
		}
		public String getEndRailwayStation() {
			return endRailwayStation;
		}
		public void setEndRailwayStation(String endRailwayStation) {
			this.endRailwayStation = endRailwayStation;
		}
		public List<Status> getStatusList() {
			return statusList;
		}
	}
	private List<Consignment> consignmentList = new ArrayList<Consignment>();
	
	public SbbIftstaBean(String interchangeConrolReference, String messageReferenceNumber) {

		super(interchangeConrolReference, messageReferenceNumber);
	}

	public List<Consignment> getConsignmentList() {
		return consignmentList;
	}
}
