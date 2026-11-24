/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.sbb;

import java.util.ArrayList;
import java.util.List;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.edifact.tags.BGM;
import com.crh.logon.business.edifact.tags.FTX;
import com.crh.logon.business.edifact.tags.UNB;
import com.crh.logon.business.edifact.parser.Handler;
import com.crh.logon.business.edifact.tags.CNI;
import com.crh.logon.business.edifact.tags.DTM;
import com.crh.logon.business.edifact.tags.EQA;
import com.crh.logon.business.edifact.tags.EQD;
import com.crh.logon.business.edifact.tags.LOC;
import com.crh.logon.business.edifact.tags.RFF;
import com.crh.logon.business.edifact.tags.STS;
import com.crh.logon.business.edifact.tags.TDT;
import com.crh.logon.business.edifact.tags.Tag;
import com.crh.logon.business.edifact.tags.UNH;
import com.crh.logon.business.services.utils.EdifactException;

/**
 * @author AdNovum Informatik AG
 */
public class SbbIftstaHandler implements Handler {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SbbIftstaHandler.class);
	private boolean strict = true;

	// 0065 Message type
	private static final String MT_IFTSTA = "IFTSTA";
	// 1001 Document name code
	private static final String DNC_TRANSPORT_STATUS_REPORT = "44";
	// 1131 Code list identification code
	private static final String CLIC_RAILWAYS_NETWORKS = "36";
	// 1153 Reference function code qualifier
	private static final String RFCQ_GENERAL_CARGO_CONSIGNMENT_REFERENCE_NUMBER = "AKR";
	private static final String RFCQ_RAILWAY_CONSIGNMENT_NOTE_NUMBER = "RCN";
	private static final String RFCQ_CARRIERS_REFERENCE_NUMBER = "CN";
	private static final String RFCQ_UNIQUE_CONSIGNMENT_REFERENCE_NUMBER = "UCN";
	// 2005 Date/time/period function code qualifier
	private static final String DTPFCQ_MESSAGE_TIME = "137";
	private static final String DTPFCQ_ARRIVAL_TIME = "178";
	private static final String DTPFCQ_DEPARTURE_TIME = "189";
	private static final String DTPFCQ_STATUS_CHANGE_TIME = "334";
	private static final String DTPFCQ_EQUIPMENT_POSITIONING_TIME = "395";
	// 3055 Code list responsible agency code
	private static final String CLRAC_UIC = "12";
	// 3227 Location function code qualifier
	private static final String LFCQ_PLACE_OF_DEPARTURE = "5";
	private static final String LFCQ_PLACE_OF_DESTINATION = "8";
	private static final String LFCQ_PLACE_OF_ARRIVAL = "60";
	private static final String LFCQ_MARSHALLING_YARD = "139";
	private static final String LFCQ_REPORTING_LOCATION = "172";
	// 4451 Text subject code qualifier
	private static final String TSCQ_GENERAL_INFORMATION = "AAI";
	private static final String TSCQ_DAMAGE_REMARKS = "DAR";
	// 8051 Transport stage code qualifier
	private static final String TSCQ_MAIN_CARRIAGE_TRANSPORT = "20";
	// 8053 Equipment type code qualifier
	private static final String ETCQ_RAIL_CAR = "RR";
	private static final String ETCQ_CONTAINER = "CN";
	// 8169 Full/empty indicator, coded
	private static final String FEI_EMPTY = "4";
	private static final String FEI_FULL = "5";
	
	private String interchangeConrolReference;
	private List<SbbIftstaBean> sbbBeanList = new ArrayList<SbbIftstaBean>();
	private SbbIftstaBean sbbBean;
	private SbbIftstaBean.Consignment consignment;
	private SbbIftstaBean.Consignment.Status status;
	private SbbIftstaBean.Consignment.Status.Wagon wagon;
	private SbbIftstaBean.Consignment.Status.Container container;
	private int stsId = 0;
	
	private void handleUnexpectedData(Tag tag) {
		
		if (strict) {
		
			throw new EdifactException("Unexpected data processing " + tag);
		}
		LOG.info("Unexpected data processing " + tag);
	}
	
	private void isPropertySet(Object value) {
		
		if (value != null) {
			if (strict) {
				
				throw new EdifactException("Property already set");
			}
			LOG.info("Property already set");
		}
	}
	
	/**
	 * Parsing here is based on tag type + function code qualifier. The actual message structure is not taking into account.
	 */
	public void tag(Tag tag) {
		
		if (tag instanceof UNB) {
			UNB unb = (UNB)tag;
			interchangeConrolReference = unb.getInterchangeConrolReference(); 
		}
		else if (tag instanceof UNH) {
			UNH unh = (UNH)tag;
			if (!MT_IFTSTA.equals(unh.getType())) {
				
				throw new EdifactException("Unsupported message type: " + unh.getType() + ". Supported: " + MT_IFTSTA);
			}
			sbbBean = new SbbIftstaBean(interchangeConrolReference, unh.getMessageReferenceNumber());
			sbbBeanList.add(sbbBean);
			consignment = null;
			status = null;
			container = null;
		}
		else if (tag instanceof BGM) {
			BGM bgm = (BGM)tag;
			if (!DNC_TRANSPORT_STATUS_REPORT.equals(bgm.getDocumentNameCode())) {
				
				throw new EdifactException("Unsupported document name code " + bgm.getDocumentNameCode() + ". Supported: " + DNC_TRANSPORT_STATUS_REPORT);
			}
		}
		else if (tag instanceof CNI) {
			CNI cni = (CNI)tag;
			consignment = sbbBean.new Consignment();
			sbbBean.getConsignmentList().add(consignment);
			consignment.setShipmentSequenceNo(cni.getConsolidationItemNo());
			consignment.setConsignmentNoteNo(cni.getDocumentNo());
			status = null;
			container = null;
		}
		else if (tag instanceof DTM) {
			DTM dtm = (DTM)tag;
			if (DTPFCQ_MESSAGE_TIME.equals(dtm.getFunctionCodeQualifier())) {
				isPropertySet(sbbBean.getDateTimeOfMessage());
				sbbBean.setDateTimeOfMessage(dtm.getValueAsDate());
			}
			else if (DTPFCQ_STATUS_CHANGE_TIME.equals(dtm.getFunctionCodeQualifier())) {
				isPropertySet(status.getStatusChangeDate());
				status.setStatusChangeDate(dtm.getValueAsDate());
			}
			else if (DTPFCQ_EQUIPMENT_POSITIONING_TIME.equals(dtm.getFunctionCodeQualifier())) {
				isPropertySet(status.getPlannedArrivalDateEndRailwayStation());
				status.setPlannedArrivalDateEndRailwayStation(dtm.getValueAsDate());
			}
			else if (DTPFCQ_DEPARTURE_TIME.equals(dtm.getFunctionCodeQualifier())) {
				if (status.getScheduledDepartureDate() == null) {
					isPropertySet(status.getScheduledDepartureDate());
					status.setScheduledDepartureDate(dtm.getValueAsDate());
				}
				else {
					isPropertySet(status.getDepartureDate());
					status.setDepartureDate(dtm.getValueAsDate());
				}
			}
			else if (DTPFCQ_ARRIVAL_TIME.equals(dtm.getFunctionCodeQualifier())) {
				isPropertySet(status.getArrivalDate());
				status.setArrivalDate(dtm.getValueAsDate());
			}			
			else {
				handleUnexpectedData(tag);
			}
		}
		else if (tag instanceof LOC) {
			LOC loc = (LOC)tag;
			if (LFCQ_PLACE_OF_DEPARTURE.equals(loc.getFunctionCodeQualifier())) {
				isPropertySet(consignment.getDepartureRailwayStation());
				consignment.setDepartureRailwayStation(loc.getLocationNameCode());
			}
			else if (LFCQ_PLACE_OF_DESTINATION.equals(loc.getFunctionCodeQualifier())) {
				isPropertySet(consignment.getDestinationRailwayStation());
				consignment.setDestinationRailwayStation(loc.getLocationNameCode());
			}
			else if (LFCQ_PLACE_OF_ARRIVAL.equals(loc.getFunctionCodeQualifier())) {
				isPropertySet(consignment.getEndRailwayStation());
				consignment.setEndRailwayStation(loc.getLocationNameCode());
			}
			else if (LFCQ_REPORTING_LOCATION.equals(loc.getFunctionCodeQualifier())) {
				isPropertySet(status.getCurrentRailwayStation());
				status.setCurrentRailwayStation(loc.getLocationNameCode());
			}
			else if(LFCQ_MARSHALLING_YARD.equals(loc.getFunctionCodeQualifier())) {
				isPropertySet(status.getNextHandlingRailwayStation());
				status.setNextHandlingRailwayStation(loc.getLocationNameCode());	
			}
			else {
				handleUnexpectedData(tag);
			}
		}
		else if (tag instanceof STS) {
			STS sts = (STS)tag;
			LOG.debug("Creating new sts " + ++stsId);
			status = consignment.new Status();
			consignment.getStatusList().add(status);
			status.setStatusCode(sts.getStatusDescriptionCode());
		}
		else if (tag instanceof RFF) {
			RFF rff = (RFF)tag;
			if (RFCQ_UNIQUE_CONSIGNMENT_REFERENCE_NUMBER.equals(rff.getFunctionCodeQualifier())) {
				isPropertySet(status.getTransportationOrderNo());
				status.setTransportationOrderNo(rff.getReferenceId());
			}
			else if (RFCQ_CARRIERS_REFERENCE_NUMBER.equals(rff.getFunctionCodeQualifier())) {
				isPropertySet(status.getCarrierReferenceNo());
				status.setCarrierReferenceNo(rff.getReferenceId());
			}
			else if (RFCQ_GENERAL_CARGO_CONSIGNMENT_REFERENCE_NUMBER.equals(rff.getFunctionCodeQualifier())) {
				isPropertySet(status.getSenderReference());
				status.setSenderReference(rff.getReferenceId());
			}
			else if (RFCQ_RAILWAY_CONSIGNMENT_NOTE_NUMBER.equals(rff.getFunctionCodeQualifier())) {
				if (!rff.getReferenceId().equals(consignment.getConsignmentNoteNo())) {
					
					throw new EdifactException("Different consignment numbers found");
				}
			}
			else {
				handleUnexpectedData(tag);
			}
		}
		else if (tag instanceof FTX) {
			FTX ftx = (FTX)tag;
			if (TSCQ_GENERAL_INFORMATION.equals(ftx.getTextSubjectCodeQualifier())) {
				isPropertySet(status.getStatusText());
				status.setStatusText(ftx.getTextLiteral1());
				LOG.debug("Setting status text for sts " + stsId + " " + status.getStatusText() + " " + ftx.getTextLiteral1());
			}
			else if (TSCQ_DAMAGE_REMARKS.equals(ftx.getTextSubjectCodeQualifier())) {
				isPropertySet(status.getDamageRemarks());
				status.setDamageRemarks(ftx.getTextLiteral1());
			}
			else {
				handleUnexpectedData(tag);
			}
		}
		else if (tag instanceof TDT) {
			TDT tdt = (TDT)tag;
			if (TSCQ_MAIN_CARRIAGE_TRANSPORT.equals(tdt.getTransportStageCodeQualifier())) {
				isPropertySet(status.getTrainNo());
				status.setTrainNo(tdt.getConveyanceReferenceNo());
				if (CLIC_RAILWAYS_NETWORKS.equals(tdt.getCodeListIdCode()) && (CLRAC_UIC.equals(tdt.getCodeListResponsibleAgencyCode()))) {
					isPropertySet(status.getEvuNo());
					status.setEvuNo(tdt.getCarrierId());
				}
			}
			else {
				handleUnexpectedData(tag);
			}
		}
		else if (tag instanceof EQD) {
			EQD eqd = (EQD)tag;
			if (ETCQ_RAIL_CAR.equals(eqd.getEquipmentTypeCodeQualifier())) {
				wagon = status.new Wagon();
				status.getWagonList().add(wagon);
				wagon.setWagonNo(eqd.getEquipmentIdNo());
				if (FEI_EMPTY.equals(eqd.getFullEmptyIndicator())) {
					wagon.setLoaded(false);
				}
				else if (FEI_FULL.equals(eqd.getFullEmptyIndicator())) {
					wagon.setLoaded(true);
				}
			}
			else if (ETCQ_CONTAINER.equals(eqd.getEquipmentTypeCodeQualifier())) {
				container = status.new Container();
				status.getContainerList().add(container);
				container.setLoadUnitNo(eqd.getEquipmentIdNo());
			}
			else {
				handleUnexpectedData(tag);
			}
		}
		else if (tag instanceof EQA) {
			EQA eqa = (EQA)tag;
			if (ETCQ_CONTAINER.equals(eqa.getEquipmentCodeQualifier())) {
				isPropertySet(wagon.getLoadUnitNo());
				wagon.setLoadUnitNo(eqa.getEquipmentIdNo());
			}
			else {
				handleUnexpectedData(tag);
			}
		}
	}
		
	public void warning(EdifactException warning) {
		
		if (strict) {
			
			throw warning;
		}
		LOG.info("warning: " + warning.getMessage());
	}

	public List<SbbIftstaBean> getSbbBeanList() {
		
		return sbbBeanList;
	}
}
