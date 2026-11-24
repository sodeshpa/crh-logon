/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.edifact.sbb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.crh.logon.business.services.utils.EdifactException;
import org.apache.commons.lang.StringUtils;

import com.crh.logon.business.edifact.tags.BGM;
import com.crh.logon.business.edifact.tags.CPI;
import com.crh.logon.business.edifact.tags.DIM;
import com.crh.logon.business.edifact.tags.DTM;
import com.crh.logon.business.edifact.tags.EQA;
import com.crh.logon.business.edifact.tags.EQD;
import com.crh.logon.business.edifact.tags.FTX;
import com.crh.logon.business.edifact.tags.GID;
import com.crh.logon.business.edifact.tags.LOC;
import com.crh.logon.business.edifact.tags.MEA;
import com.crh.logon.business.edifact.tags.NAD;
import com.crh.logon.business.edifact.tags.PIA;
import com.crh.logon.business.edifact.tags.RFF;
import com.crh.logon.business.edifact.tags.SGP;
import com.crh.logon.business.edifact.tags.TDT;
import com.crh.logon.business.edifact.tags.TSR;
import com.crh.logon.business.edifact.tags.Tag;
import com.crh.logon.business.edifact.tags.UNA;
import com.crh.logon.business.edifact.tags.UNB;
import com.crh.logon.business.edifact.tags.UNH;
import com.crh.logon.business.edifact.tags.UNT;
import com.crh.logon.business.edifact.tags.UNZ;
import com.crh.logon.constants.Constant;

/**
 * @author AdNovum Informatik AG
 */
public class Cibef102Converter {
	
	private static final String SYNTAX_IDENTIFIER = "UNOB";
	private static final String SYNTAX_VERSION_NO = "2";
	private static final String SENDER_ID = "HCB";
	private static final String SENDER_PARTNER_ID_CODE = "ZZ";
	private static final String RECIPIENT_ID = "SBB";
	private static final String RECIPIENT_ID_CODE_QUALIFIER = "UIC";
	
	private static final String MESSAGE_TYPE = "IFTMIN";
	private static final String MESSAGE_VERSION_NO = "D1";
	private static final String MESSAGE_RELEASE_NO = "95A";
	private static final String MESSAGE_CONTROLLING_AGENCY = "ER";
	private static final String ASSOCIATION_ASSIGNED_CODE = "SBB101";
	
	private static final String DNC_RAIL_CONSIGNMENT_NOTE = "720";
	private static final String DMN_DOCUMENT_TYPE = "CIBEF102";
	private static final DateFormat DMN_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final DateFormat DMN_TIME_FORMAT = new SimpleDateFormat("HHmmssSSS");  
	private static final String MFC_ORIGINAL = "9";
    
    private static final String TPCQ_SERVICE = "128";
    private static final String CODE_AGENCY_UIC = "12";
    private static final String CODE_AGENCY_SBB = "46";
    
    private static final String TSCQ_TRANSPORTATION_INFORMATION = "TRA";
    private static final String FT_CODE_TRANSIT_MODE = "VAR";
    private static final String TM_IMPORT = "5";
    private static final String TM_TRANSIT = "7";
    private static final String TM_BINNEN = "1";
    private static final String TM_EXPORT = "6";
    
    private static final String TSCQ_TRANSPORTATION_DETAILS_REMARKS = "TDT";
    private static final String FT_CODE_KURSNUMMER = "XKU";
    
    private static final String TSCQ_SPECIAL_SERVICE_REQUEST = "SSR";
    private static final String FT_CODE_VIA_RAILWAY_STATION = "XLU";
    private static final String VIA_RAILWAY_STATION_PROCESSING_CODE = "81";
    
    private static final String FT_CODE_TRANSPORTATION_MODE = "XTR";
    
    private static final String TSCQ_REQUESTED_TARIFF = "TRR";
    private static final String FT_CODE_REQUESTED_TARIFF = "XTA";
    private static final String TARIFF_UNIT_COUNT = "000";
    
    private static final String RCQ_CONSIGNORS_REFERENCE_NUMBER = "CU";
    private static final DateFormat CRN_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    
    private static final String RCQ_RAILWAY_CONSIGMENT_NOTE_NUMBER = "RCN";
    private static final String CONSIGMENT_NOTE_NUMBER = "000000000";
    
    private static final String TSCQ_MAIN_CARRIAGE_TRANSPORT = "20";
    private static final String TID_MEANS = "000000";
    private static final String TIDCQ_TRAIN_IDENTIFICATION = "142";
    
    private static final String LFCQ_PLACE_OF_DEPARTURE = "5";
	private static final String LFCQ_PLACE_OF_DESTINATION = "8";
	private static final String LFCQ_RAILWAY_STATION = "29";
	
	private static final String LOC_INVOICE_LOCATION = "0";
	private static final String LOC_EVU_NUMBER = "0000";
	private static final String LCLQ_RAILWAY_LOCATIONS = "37";
	
	private static final String DTPFCQ_DEPARTURE_DATE_TIME = "136";
	private static final String DTPFCQ_FORMAT_QUALIFIER = "203";
	
	private static final String PARTY_QUALIFIER_MESSAGE_FROM = "FR";
	private static final String PCQ_RAIL_CUSTOMERS = "38";
	private static final String COUNTRY_CODE_SWITZERLAND = "85";
	
	private static final String PARTY_QUALIFIER_CONSIGNOR = "CZ";
	
	private static final String CCCQ_METHODS_OF_PAYMENT = "153";
	private static final String CHARGE_METHOD_OF_PAYMENT_CQ = "ZZZ";
	
	private static final String PARTY_QUALIFIER_CONSIGNEE = "CN";
	private static final String PARTY_QUALIFIER_DESPATCH_CHARGE_PAYER = "DCP";
	
	private static final String PIFQC_PRODUCT_IDENTIFICATION = "5";
	private static final String PI_ITEM_NUMBER_TYPE_CODE = "HS";
	
	private static final String TSCQ_GOODS_DESCRIPTION = "AAA";
	
	private static final String MAQ_GROSS_WAGON = "GRO";
	private static final String MAQ_GROSS_CONTAINER = "GRC";
	private static final String MAQ_SPECIFICATION_VALUE = "SV";
	private static final String MAQ_TARE = "TAR";
	private static final String MUQ_KILOGRAM = "KGM";
	private static final String MUQ_FOOT = "FT";
	private static final String MUQ_MILLIMETER = "MM";
	
	private static final String EQC_RAILCAR = "RR";
	private static final String EQD_SIZE_AND_TYPE_ID = "000";
	private static final String EQC_CONTAINER = "CN";
	private static final String EQSC_YES = "Y";
	private static final String EQA_CODE_LIST_QUALIFIER = "ZZZ";
	
	private static final String DQC_GROSS_DIMENSIONS = "1";
	
	private static UNA getServiceStringAdvice() {
		
		return new UNA();
	}
	
	private static UNB getInterchangeHeader(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getDateTimeOfMessage() == null) {
			
			throw new EdifactException("Cannot create InterchangeHeader, DateTimeOfMessage is not set");
		}
		if (cibef102Bean.getInterchangeConrolReference() == null) {
			
			throw new EdifactException("Cannot create InterchangeHeader, InterchangeControlReference is not set");
		}
		UNB unb = new UNB();
		unb.setSyntaxId(SYNTAX_IDENTIFIER);
		unb.setSyntaxVersionNumber(SYNTAX_VERSION_NO);
		unb.setSenderId(SENDER_ID);
		unb.setSenderPartnerIdCodeQualifier(SENDER_PARTNER_ID_CODE);
		unb.setRecipientId(RECIPIENT_ID);
		unb.setRecipientPartnerIdCodeQualifier(RECIPIENT_ID_CODE_QUALIFIER);
		unb.setPrepDateTime(cibef102Bean.getDateTimeOfMessage());
		unb.setInterchangeConrolReference(cibef102Bean.getInterchangeConrolReference());
		unb.setTestInd(cibef102Bean.isTestIndicator()?"1":"0");
		
		return unb;
	}
	
	private static UNH getMessageHeader(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getMessageReferenceNo() == null) {
			
			throw new EdifactException("Cannot create MessageHeader, MessageReferenceNo is not set");
		}
		UNH unh = new UNH();
		unh.setMessageReferenceNumber(cibef102Bean.getMessageReferenceNo());
		unh.setType(MESSAGE_TYPE);
		unh.setVersion(MESSAGE_VERSION_NO);
		unh.setRelease(MESSAGE_RELEASE_NO);
		unh.setAgency(MESSAGE_CONTROLLING_AGENCY);
		unh.setAssociationAssignedCode(ASSOCIATION_ASSIGNED_CODE);
		
		return unh;
	}
	
	private static BGM getBeginningOfMessage(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getDateTimeOfMessage() == null) {
			
			throw new EdifactException("Cannot create BeginningOfMessage, DateTimeOfMessage is not set");
		}
		if (cibef102Bean.getSenderCompanyNo() == null) {
			
			throw new EdifactException("Cannot create BeginningOfMessage, SenderCompanyNo is not set");
		}
		if (cibef102Bean.getSenderCompanyLocNo() == null) {
			
			throw new EdifactException("Cannot create BeginningOfMessage, SenderCompanyLocNo is not set");
		}
		BGM bgm = new BGM();
		bgm.setDocumentNameCode(DNC_RAIL_CONSIGNMENT_NOTE);
		StringBuilder documentNo = new StringBuilder();
		documentNo.append(DMN_DOCUMENT_TYPE);
		documentNo.append(cibef102Bean.getSenderCompanyNo());
		documentNo.append(cibef102Bean.getSenderCompanyLocNo());
		synchronized (DMN_DATE_FORMAT) {
			documentNo.append(DMN_DATE_FORMAT.format(cibef102Bean.getDateTimeOfMessage()));
		}
		synchronized (DMN_TIME_FORMAT) {
			documentNo.append(DMN_TIME_FORMAT.format(cibef102Bean.getDateTimeOfMessage()));
			// Formatted time will return 'SSS' (even if 'SS' is specified it might, 'SS' means MINIMAL width of 2) 
			documentNo.deleteCharAt(documentNo.length()-1);
		}
		bgm.setDocumentNo(documentNo.toString());
		bgm.setMsgFuncCode(MFC_ORIGINAL);
		
		return bgm;
	}
	
	private static TSR getTransportationServiceMode(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getTransportPriority() == null) {
			
			throw new EdifactException("Cannot create BeginningOfMessage, TransportPriority is not set");
		}
		TSR tsr = new TSR(); // 5: Transportation service requirements
		tsr.setTransportPriorityCode(cibef102Bean.getTransportPriority());
		tsr.setCodeListQualifier(TPCQ_SERVICE);
		tsr.setCodeListResponsibleAgency(CODE_AGENCY_UIC);
		
		return tsr;
	}
	
	private static FTX getTransitMode(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getActualDepCountryCode() == null) {
			
			throw new EdifactException("Cannot create TransitMode, ActualDepCountryCode is not set");
		}
		if (cibef102Bean.getActualArrCountryCode() == null) {
			
			throw new EdifactException("Cannot create TransitMode, ActualArrCountryCode is not set");
		}
		FTX ftx = new FTX(); // 11: Transit mode
		ftx.setTextSubjectCodeQualifier(TSCQ_TRANSPORTATION_INFORMATION);
		ftx.setFreeTextCoded(FT_CODE_TRANSIT_MODE);
		ftx.setCodeListResponsibleAgency(CODE_AGENCY_UIC);
		if (COUNTRY_CODE_SWITZERLAND.equals(cibef102Bean.getActualDepCountryCode())) {
			if (COUNTRY_CODE_SWITZERLAND.equals(cibef102Bean.getActualArrCountryCode())) {
				ftx.setTextLiteral1(TM_BINNEN);
			}
			else {
				ftx.setTextLiteral1(TM_EXPORT);
			}
		}
		else {
			if (COUNTRY_CODE_SWITZERLAND.equals(cibef102Bean.getActualArrCountryCode())) {
				ftx.setTextLiteral1(TM_IMPORT);
			}
			else {
				ftx.setTextLiteral1(TM_TRANSIT);
			}
		}
		
		return ftx;
	}
	
	private static FTX getTransportationMode(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getTransportationMode() == null) {
			
			throw new EdifactException("Cannot create TransportationMode, TransportationMode is not set");
		}
		FTX ftx = new FTX(); // 12: Transportation mode
		ftx.setTextSubjectCodeQualifier(TSCQ_TRANSPORTATION_INFORMATION);
		ftx.setFreeTextCoded(FT_CODE_TRANSPORTATION_MODE);
		ftx.setCodeListResponsibleAgency(CODE_AGENCY_SBB);
		ftx.setTextLiteral1(cibef102Bean.getTransportationMode());
		
		return ftx;
	}
	
	private static FTX getTransportationRouteNumber(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getRouteNumber() == null) {
			
			return null;
		}
		FTX ftx = new FTX(); // 13: Kursnummer
		ftx.setTextSubjectCodeQualifier(TSCQ_TRANSPORTATION_DETAILS_REMARKS);
		ftx.setFreeTextCoded(FT_CODE_KURSNUMMER);
		ftx.setCodeListResponsibleAgency(CODE_AGENCY_SBB);
		ftx.setTextLiteral1(cibef102Bean.getRouteNumber());
		
		return ftx;
	}
	
	private static FTX getRequestedTariff(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getTariffNo() == null) {
			
			return null;
		}
		FTX ftx = new FTX(); // 18: Verlangter Tarif / Vertrag
		ftx.setTextSubjectCodeQualifier(TSCQ_REQUESTED_TARIFF);
		ftx.setFreeTextCoded(FT_CODE_REQUESTED_TARIFF);
		ftx.setCodeListResponsibleAgency(CODE_AGENCY_SBB);
		ftx.setTextLiteral1(cibef102Bean.getTariffNo());
		ftx.setTextLiteral2(TARIFF_UNIT_COUNT);
		
		return ftx;
	}
	
	private static FTX getViaRailwayStationServiceRequest(Cibef102Bean cibef102Bean) {
		
		if ( (cibef102Bean.getCommViaCountryCode() == null) || (cibef102Bean.getCommViaRailwayStationNo() == null) ) {
			
			return null;
		}
		FTX ftx = new FTX(); // 22: Ladebehandlungscode am Empfangsbahnhof
		ftx.setTextSubjectCodeQualifier(TSCQ_SPECIAL_SERVICE_REQUEST);
		ftx.setFreeTextCoded(FT_CODE_VIA_RAILWAY_STATION);
		ftx.setCodeListResponsibleAgency(CODE_AGENCY_SBB);
		ftx.setTextLiteral1(cibef102Bean.getCommViaCountryCode() + cibef102Bean.getCommViaRailwayStationNo());
		ftx.setTextLiteral2(VIA_RAILWAY_STATION_PROCESSING_CODE);
		
		return ftx;
	}
	
	private static RFF getTransportationOrderNo(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getOrderNo() == null) {
			
			throw new EdifactException("Cannot create TransportationOrderNo, OrderNo is not set");
		}
		if (cibef102Bean.getDateTimeOfMessage() == null) {
			
			throw new EdifactException("Cannot create TransportationOrderNo, DateTimeOfMessage is not set");
		}
		RFF rff = new RFF(); // 33: Beförderungsauftragsnummer und Erstellungsdatum der Sendung. Beförderungsauftragsnummer muss eindeutig sein.
		rff.setFunctionCodeQualifier(RCQ_CONSIGNORS_REFERENCE_NUMBER);
		synchronized (CRN_DATE_FORMAT) {
			rff.setReferenceId(StringUtils.leftPad(cibef102Bean.getOrderNo(), 9, '0') + CRN_DATE_FORMAT.format(cibef102Bean.getDateTimeOfMessage()));
		}
		
		return rff;
	}
	
	private static RFF getConsignmentNoteNo() {
		
		RFF rff = new RFF(); // 35: Frachtbriefnummer
		rff.setFunctionCodeQualifier(RCQ_RAILWAY_CONSIGMENT_NOTE_NUMBER);
		rff.setReferenceId(CONSIGMENT_NOTE_NUMBER);
		
		return rff;
	}
	
	private static DTM getDepartureDateTime(Cibef102Bean cibef102Bean) {
		
		DTM dtm = new DTM(); // 49: Versandbahnhof/Ladestelle und Datum/Zeit_Abgangs
		dtm.setFunctionCodeQualifier(DTPFCQ_DEPARTURE_DATE_TIME);
		dtm.setFormatCode(DTPFCQ_FORMAT_QUALIFIER);
		dtm.setValue(cibef102Bean.getDateTimeOfMessage());
		
		return dtm;
	}
	
	private static LOC getDepartureLocation(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getCommDepCountryCode() == null) {
			
			throw new EdifactException("Cannot create DepartureLocation, CommDepCountryCode is not set");
		}
		if (cibef102Bean.getCommDepRailwayStationNo() == null) {
			
			throw new EdifactException("Cannot create DepartureLocation, CommDepRailwayStationNo is not set");
		}
		if (cibef102Bean.getActualDepCountryCode() == null) {
			
			throw new EdifactException("Cannot create DepartureLocation, ActualDepCountryCode is not set");
		}
		if (cibef102Bean.getActualDepRailwayStationNo() == null) {
			
			throw new EdifactException("Cannot create DepartureLocation, ActualDepRailwayStationNo is not set");
		}
		LOC loc = new LOC(); // 48: Versandbahnhof/Ladestelle und Datum/Zeit_Abgangs
		loc.setFunctionCodeQualifier(LFCQ_PLACE_OF_DEPARTURE);
		loc.setLocationNameCode(cibef102Bean.getCommDepCountryCode() + cibef102Bean.getCommDepRailwayStationNo() + LOC_INVOICE_LOCATION + LOC_EVU_NUMBER);
		loc.setCodeListIdCode(LCLQ_RAILWAY_LOCATIONS);
		loc.setCodeListResponsibleAgency(CODE_AGENCY_UIC);
		loc.setRelated1LocationNameCode(cibef102Bean.getActualDepCountryCode() + cibef102Bean.getActualDepRailwayStationNo());
		loc.setRelated1CodeListIdCode(LCLQ_RAILWAY_LOCATIONS);
		loc.setRelated1CodeListResponsibleAgency(CODE_AGENCY_UIC);
		if (cibef102Bean.getActualDepRailwayStationLoadingPoint() != null) {
			loc.setRelated2LocationNameCode(StringUtils.leftPad(cibef102Bean.getActualDepRailwayStationLoadingPoint(), 4, '0'));
			loc.setRelated2CodeListIdCode(LCLQ_RAILWAY_LOCATIONS);
			loc.setRelated2CodeListResponsibleAgency(CODE_AGENCY_UIC);
		}
		Tag departureDateTime = getDepartureDateTime(cibef102Bean);
		if (departureDateTime != null) {
			loc.getTags().add(departureDateTime); // DTM(49)
		}
		
		return loc;
	}
	
	private static LOC getArrivalLocation(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getCommArrCountryCode() == null) {
			
			throw new EdifactException("Cannot create ArrivalLocation, CommArrCountryCode is not set");
		}
		if (cibef102Bean.getCommArrRailwayStationNo() == null) {
			
			throw new EdifactException("Cannot create ArrivalLocation, CommArrRailwayStationNo is not set");
		}
		if (cibef102Bean.getActualArrCountryCode() == null) {
			
			throw new EdifactException("Cannot create ArrivalLocation, ActualArrCountryCode is not set");
		}
		if (cibef102Bean.getActualArrRailwayStationNo() == null) {
			
			throw new EdifactException("Cannot create ArrivalLocation, ActualArrRailwayStationNo is not set");
		}
		LOC loc = new LOC(); // 48: Versandbahnhof/Ladestelle und Datum/Zeit_Abgangs
		loc.setFunctionCodeQualifier(LFCQ_PLACE_OF_DESTINATION);
		loc.setLocationNameCode(cibef102Bean.getCommArrCountryCode() + cibef102Bean.getCommArrRailwayStationNo() + LOC_INVOICE_LOCATION + LOC_EVU_NUMBER);
		loc.setCodeListIdCode(LCLQ_RAILWAY_LOCATIONS);
		loc.setCodeListResponsibleAgency(CODE_AGENCY_UIC);
		loc.setRelated1LocationNameCode(cibef102Bean.getActualArrCountryCode() + cibef102Bean.getActualArrRailwayStationNo());
		loc.setRelated1CodeListIdCode(LCLQ_RAILWAY_LOCATIONS);
		loc.setRelated1CodeListResponsibleAgency(CODE_AGENCY_UIC);
		if (cibef102Bean.getActualArrRailwayStationUnloadingPoint() != null) {
			loc.setRelated2LocationNameCode(StringUtils.leftPad(cibef102Bean.getActualArrRailwayStationUnloadingPoint(), 4, '0'));
			loc.setRelated2CodeListIdCode(LCLQ_RAILWAY_LOCATIONS);
			loc.setRelated2CodeListResponsibleAgency(CODE_AGENCY_UIC);
		}
		
		return loc;
	}
	
	private static LOC getViaLocation(Cibef102Bean cibef102Bean) {
		
		if ( (cibef102Bean.getCommViaCountryCode() == null) || (cibef102Bean.getCommViaCountryCode() == null) || (cibef102Bean.getActualViaCountryCode() == null) || (cibef102Bean.getActualViaRailwayStationNo() == null) ) {
			
			return null;
		}
		LOC loc = new LOC(); // 50: Unterwegsbahnhof
		loc.setFunctionCodeQualifier(LFCQ_RAILWAY_STATION);
		loc.setLocationNameCode(cibef102Bean.getCommViaCountryCode() + cibef102Bean.getCommViaRailwayStationNo() + LOC_INVOICE_LOCATION);
		loc.setCodeListIdCode(LCLQ_RAILWAY_LOCATIONS);
		loc.setCodeListResponsibleAgency(CODE_AGENCY_UIC);
		loc.setRelated1LocationNameCode(cibef102Bean.getActualViaCountryCode() + cibef102Bean.getActualViaRailwayStationNo());
		loc.setRelated1CodeListIdCode(LCLQ_RAILWAY_LOCATIONS);
		loc.setRelated1CodeListResponsibleAgency(CODE_AGENCY_UIC);
		
		return loc;
	}
	
	private static TDT getTransportationDetails(Cibef102Bean cibef102Bean) {
		
		TDT tdt = new TDT(); // 47: Zug/Block/Datum/Zeit_Abgangs, Zug/Datum/Bahnhof_Grenzankunft, Versand/Empfangsbahnhof, Leitweg
		tdt.setTransportStageCodeQualifier(TSCQ_MAIN_CARRIAGE_TRANSPORT);
		tdt.setTransportIdMeans(TID_MEANS);
		tdt.setTransportIdCodeListQualifier(TIDCQ_TRAIN_IDENTIFICATION);
		tdt.setTransportIdCodeListResponsibleAgent(CODE_AGENCY_UIC);
		tdt.getTags().add(getDepartureLocation(cibef102Bean)); // SG9: LOC(48)
		tdt.getTags().add(getArrivalLocation(cibef102Bean)); // SG9: LOC(48)
		Tag viaLocation = getViaLocation(cibef102Bean);
		if (viaLocation != null) {
			tdt.getTags().add(viaLocation); // SG9: LOC(50)	
		}
		
		return tdt;
	}
	
	private static NAD getMessageSender(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getSenderCompanyNo() == null) {
			
			throw new EdifactException("Cannot create MessageSender, SenderCompanyNo is not set");
		}
		if (cibef102Bean.getSenderCompanyLocNo() == null) {
			
			throw new EdifactException("Cannot create MessageSender, SenderCompanyLocNo is not set");
		}
		NAD nad = new NAD(); // 55: Meldungsabsender
		nad.setPartyQualifier(PARTY_QUALIFIER_MESSAGE_FROM);
		nad.setPartyId(cibef102Bean.getSenderCompanyNo() + cibef102Bean.getSenderCompanyLocNo());
		nad.setCodeListQualifier(PCQ_RAIL_CUSTOMERS);
		nad.setCodeListResponsibleAgency(CODE_AGENCY_UIC);
		nad.setCountry(COUNTRY_CODE_SWITZERLAND);
		
		return nad;
	}
	
	private static NAD getTransportSender(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getConsignorCustomerNo() == null) {
			
			throw new EdifactException("Cannot create TransportSender, ConsignorCustomerNo is not set");
		}
		if (cibef102Bean.getConsignorCustomerCountryCode() == null) {
			
			throw new EdifactException("Cannot create TransportSender, ConsignorCustomerCountryCode is not set");
		}
		NAD nad = new NAD(); // 56: Absender der Sendung, Beträge Franko, Absenderrreferenz, Frankatur			
		nad.setPartyQualifier(PARTY_QUALIFIER_CONSIGNOR);
		nad.setPartyId(StringUtils.leftPad(cibef102Bean.getConsignorCustomerNo(), 10, '0'));
		nad.setCodeListQualifier(PCQ_RAIL_CUSTOMERS);
		nad.setCodeListResponsibleAgency(CODE_AGENCY_UIC);
		nad.setPartyName(cibef102Bean.getConsignorName());
		nad.setCountry(cibef102Bean.getConsignorCustomerCountryCode());
		
		return nad;
	}
	
	private static CPI getPaymentInstructions(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getChargeCategory() == null) {
			
			throw new EdifactException("Cannot create PaymentInstructions, ChargeCategory is not set");
		}
		if (cibef102Bean.getChargeMethodOfPayment() == null) {
			
			throw new EdifactException("Cannot create PaymentInstructions, ChargeMethodOfPayment is not set");
		}
		CPI cpi = new CPI(); // 65: Frankatur / Incoterm
		cpi.setChargeCategoryCode(cibef102Bean.getChargeCategory());
		cpi.setCodeListQualifier(CCCQ_METHODS_OF_PAYMENT);
		cpi.setCodeListResponsibleAgency(CODE_AGENCY_SBB);
		cpi.setMethodOfPaymentTransportChargesCode(cibef102Bean.getChargeMethodOfPayment());
		cpi.setMethodOfPaymentCodeListQualifier(CHARGE_METHOD_OF_PAYMENT_CQ);
		cpi.setMethodOfPaymentCodeListResponsibleAgency(CODE_AGENCY_UIC);
		
		return cpi;
	}
	
	private static NAD getTransportRecipient(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getConsigneeCustomerNo() == null) {
			
			throw new EdifactException("Cannot create TransportRecipient, ConsigneeCustomerNo is not set");
		}
		if (cibef102Bean.getConsigneeCustomerCountryCode() == null) {
			
			throw new EdifactException("Cannot create TransportRecipient, ConsigneeCustomerCountryCode is not set");
		}
		NAD nad = new NAD(); // 71: Empfänger der Sendung, Beträge Unfranko
		nad.setPartyQualifier(PARTY_QUALIFIER_CONSIGNEE);
		nad.setPartyId(StringUtils.leftPad(cibef102Bean.getConsigneeCustomerNo(), 10, '0'));
		nad.setCodeListQualifier(PCQ_RAIL_CUSTOMERS);
		nad.setCodeListResponsibleAgency(CODE_AGENCY_UIC);
		nad.setPartyName(cibef102Bean.getConsigneeName());
		nad.setStreetAndHouseNo(cibef102Bean.getConsigneeStreetAndHouseNo());
		nad.setCity(cibef102Bean.getConsigneeCity());
		nad.setPostalCode(cibef102Bean.getConsigneePostalCode());
		nad.setCountry(cibef102Bean.getConsigneeCustomerCountryCode());
		
		return nad;
	}
	
	private static NAD getDespatchChargePayer(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getDespatchChargePayer() == null) {
			
			return null;
		}
		NAD nad = new NAD(); // 81: Frachtzahler Versand, Empfang, Transit
		nad.setPartyQualifier(PARTY_QUALIFIER_DESPATCH_CHARGE_PAYER);
		nad.setPartyId(StringUtils.leftPad(cibef102Bean.getDespatchChargePayer(), 10, '0'));
		nad.setCodeListQualifier(PCQ_RAIL_CUSTOMERS);
		nad.setCodeListResponsibleAgency(CODE_AGENCY_UIC);
		nad.setCountry(COUNTRY_CODE_SWITZERLAND);
		
		return nad;
	}
	
	private static PIA getProductId(Cibef102Bean.GoodsItem gi) {
		
		if (gi.getNhmCode() == null) {
			
			return null;
		}
		PIA pia = new PIA(); // 93: Ware, Ware auf dem Wagen oder im Container, Gewichte
		pia.setProductIdFunctionQualifier(PIFQC_PRODUCT_IDENTIFICATION);
		pia.setItemNumber(StringUtils.leftPad(gi.getNhmCode(), 8, '0'));
		pia.setItemNumberTypeCode(PI_ITEM_NUMBER_TYPE_CODE);
		pia.setCodeListResponsibleAgency(CODE_AGENCY_UIC);
		
		return pia;
	}
	
	private static FTX getProductDescription(Cibef102Bean.GoodsItem gi) {
		
		if (gi.getDescription() == null) {
			
			return null;
		}
		FTX ftx = new FTX(); // 94: Warenbezeichnung gemäss Kunde
		ftx.setTextSubjectCodeQualifier(TSCQ_GOODS_DESCRIPTION);
		ftx.setTextLiteral1(gi.getDescription());
				
		return ftx;
	}
	
	private static MEA getGoodsItemMeasurments(Cibef102Bean.GoodsItem gi, @SuppressWarnings("unused") Cibef102Bean.Wagon wagon) {
		
		if (gi.getNetWeightT() == null) {
			
			throw new EdifactException("Cannot create GoodsItemMeasurments, NetWeightT is not set");
		}
		MEA mea = new MEA(); // 101: Brutto/Frachtpflichtigesgewicht der Ware auf dem Wagen oder im Container
		mea.setMeasurementApplicationQualifier(MAQ_GROSS_WAGON);
		mea.setMeasureUnitQualifier(MUQ_KILOGRAM);
		mea.setMeasurementValue("" + gi.getNetWeightT().multiply(Constant.TON_TO_KG).longValue());
		
		return mea;
	}
	
	private static MEA getGoodsItemMeasurments(Cibef102Bean.GoodsItem gi, @SuppressWarnings("unused") Cibef102Bean.Container container) {
		
		if (gi.getNetWeightT() == null) {
			
			throw new EdifactException("Cannot create GoodsItemMeasurments, NetWeightT is not set");
		}
		MEA mea = new MEA(); // 101: Brutto/Frachtpflichtigesgewicht der Ware auf dem Wagen oder im Container
		mea.setMeasurementApplicationQualifier(MAQ_GROSS_CONTAINER);
		mea.setMeasureUnitQualifier(MUQ_KILOGRAM);
		mea.setMeasurementValue("" + gi.getNetWeightT().multiply(Constant.TON_TO_KG).longValue());
		
		return mea;
	}
	
	private static SGP getWagonOrContainerNumber(Cibef102Bean.GoodsItem gi, Cibef102Bean.Wagon wagon) {
		
		SGP sgp = new SGP(); // 100: Wagennummer oder Containernummer
		sgp.setEquipmentIdNumber(wagon.getWagonNo());
		sgp.setCodeListResponsibleAgency(CODE_AGENCY_UIC);
		sgp.getTags().add(getGoodsItemMeasurments(gi, wagon)); // MEA(101)
				
		return sgp;
	}
	
	private static SGP getWagonOrContainerNumber(Cibef102Bean.GoodsItem gi, Cibef102Bean.Container container) {
		
		SGP sgp = new SGP(); // 100: Wagennummer oder Containernummer
		sgp.setEquipmentIdNumber(container.getContainerNo());
		sgp.setCodeListResponsibleAgency(CODE_AGENCY_UIC);
		sgp.getTags().add(getGoodsItemMeasurments(gi, container)); // MEA(101)
				
		return sgp;
	}
	
	private static GID getGoodsItem(Cibef102Bean.GoodsItem gi, int goodsItemNo) {
		
		GID gid = new GID(); // 90: Ware, Ware auf dem Wagen oder im Container, Gewichte
		gid.setGoodsItemNumber(StringUtils.leftPad("" + goodsItemNo, 2, '0'));
		Tag productInformation = getProductId(gi);
		if (productInformation != null) {
			gid.getTags().add(productInformation); // PIA(93)
		}
		Tag productDescription = getProductDescription(gi);
		if (productDescription != null) {
			gid.getTags().add(productDescription); // FTX(94)
		}
		
		return gid;
	}
	
	private static GID getGoodsItem(Cibef102Bean.GoodsItem gi, int goodsItemNo, Cibef102Bean.Wagon wagon) {
		
		GID gid = getGoodsItem(gi, goodsItemNo);
		gid.getTags().add(getWagonOrContainerNumber(gi, wagon)); // SGP(100)
		
		return gid;
	}
	
	private static GID getGoodsItem(Cibef102Bean.GoodsItem gi, int goodsItemNo, Cibef102Bean.Container container) {
		
		GID gid = getGoodsItem(gi, goodsItemNo);
		gid.getTags().add(getWagonOrContainerNumber(gi, container)); // SGP(100)
		
		return gid;
	}
	
	private static List<GID> getGoodsItems(Cibef102Bean cibef102Bean) {
		
		List<GID> gidList = new ArrayList<GID>();
		int goodsItemNo = 1;
		// Add items directly on the wagon
		for (Cibef102Bean.GoodsItem gi: cibef102Bean.getWagon().getGoodsItems()) {
			gidList.add(getGoodsItem(gi, goodsItemNo++, cibef102Bean.getWagon()));
		}
		// Add items on containers
		for (Cibef102Bean.Container container: cibef102Bean.getWagon().getContainers()) {
			for (Cibef102Bean.GoodsItem gi: container.getGoodsItems()) {
				gidList.add(getGoodsItem(gi, goodsItemNo++, container));
			}	
		}
		
		return gidList;
	}
	
	private static EQA getContainerOnWagon(Cibef102Bean.Container container) {
		
		if (container.getContainerNo() == null) {
			
			return null;
		}
		EQA eqa = new EQA(); // 122: Container auf dem Wagen
		eqa.setEquipmentCodeQualifier(EQC_CONTAINER);
		eqa.setEquipmentIdNo(container.getContainerNo());
		eqa.setCodeListQualifier(EQA_CODE_LIST_QUALIFIER);
		eqa.setCodeListResponsibleAgency(CODE_AGENCY_SBB);
		
		return eqa;
	}
	
	private static EQD getWagon(Cibef102Bean cibef102Bean) {
		
		if (cibef102Bean.getWagon() == null) {
			
			return null;
		}
		EQD eqd = new EQD(); // 113: Wagennummer
		eqd.setEquipmentTypeCodeQualifier(EQC_RAILCAR);
		eqd.setEquipmentIdNo(cibef102Bean.getWagon().getWagonNo());
		eqd.setCodeListResponsibleAgency(CODE_AGENCY_UIC);
		eqd.setSizeAndTypeId(EQD_SIZE_AND_TYPE_ID);
		eqd.setSizeAndTypeCodeListResponsibleAgency(CODE_AGENCY_SBB);
		
		for (Cibef102Bean.Container container: cibef102Bean.getWagon().getContainers()) {
			Tag containerOnWagon = getContainerOnWagon(container);
			if (containerOnWagon != null) {
				eqd.getTags().add(containerOnWagon); // SG41: EQA(122)
			}
		}
		
		return eqd;
	}
	
	private static MEA getContainerUtiCode(Cibef102Bean.Container container) {
	
		if (container.getLengthCode() == null) {
			
			return null;
		}
		MEA mea = new MEA(); // 124: UTI Code und Längencode
		mea.setMeasurementApplicationQualifier(MAQ_SPECIFICATION_VALUE);
		mea.setMeasureUnitQualifier(MUQ_FOOT);
		mea.setMeasurementValue(StringUtils.leftPad(container.getLengthCode(), 7, '0'));
		
		return mea;
	}
	
	private static MEA getContainerTare(Cibef102Bean.Container container) {
		
		if (container.getTareWeightKg() == null) {
			
			return null;
		}
		MEA	mea = new MEA(); // 125: Tara des Containers
		mea.setMeasurementApplicationQualifier(MAQ_TARE);
		mea.setMeasureUnitQualifier(MUQ_KILOGRAM);
		mea.setMeasurementValue(container.getTareWeightKg().toPlainString());
		
		return mea;
	}
	
	private static DIM getContainerDimensions(Cibef102Bean.Container container) {
		
		if ( (container.getLengthMm() == null) && (container.getWidthMm() == null) && (container.getHeightMm() == null) ) {
			
			return null;
		}
		DIM dim = new DIM(); // 126: Länge, Höhe, Breite des Containers
		dim.setDimensionQualifier(DQC_GROSS_DIMENSIONS);
		dim.setMeasureUnitQualifier(MUQ_MILLIMETER);
		dim.setLengthDimension(container.getLengthMm() != null?"" + container.getLengthMm().longValue():null);
		dim.setWidthDimension(container.getWidthMm() != null?"" + container.getWidthMm().longValue():null);
		dim.setHeightDimension(container.getHeightMm() != null?"" + container.getHeightMm().longValue():null);

		return dim;
	}
	
	private static List<EQD> getContainers(Cibef102Bean cibef102Bean) {
		
		List<EQD> containerList = new ArrayList<EQD>();
		for (Cibef102Bean.Container container: cibef102Bean.getWagon().getContainers()) {
			if (container.getContainerNo() == null) {
				
				throw new EdifactException("Cannot create Container, ContainerNo is not set");
			}
			if (container.getNhmCode() == null) {
				
				throw new EdifactException("Cannot create Container, NhmCode is not set");
			}
			EQD eqd = new EQD(); // 123: Containernummer
			eqd.setEquipmentTypeCodeQualifier(EQC_CONTAINER);
			eqd.setEquipmentIdNo(container.getContainerNo());
			eqd.setCodeListResponsibleAgency(CODE_AGENCY_SBB);
			eqd.setSizeAndTypeId(container.getNhmCode()); // NHM container
			eqd.setSizeAndTypeCodeListResponsibleAgency(CODE_AGENCY_SBB);
			eqd.setEquipmentStatusCode(EQSC_YES);
			Tag containerUtiCode = getContainerUtiCode(container);
			if (containerUtiCode != null) {
				eqd.getTags().add(containerUtiCode); // MEA(124)
			}
			Tag containerTare = getContainerTare(container);
			if (containerTare != null) {
				eqd.getTags().add(containerTare); // MEA(125)
			}
			Tag containerDimensions = getContainerDimensions(container);
			if (containerDimensions != null) {
				eqd.getTags().add(containerDimensions); // DIM(126)
			}
			containerList.add(eqd);
		}
		
		return containerList;
	}
	
	private static int countTags(Tag t) {
		
		int count = 1;
		if ( (t.getTags() == null) || (t.getTags().isEmpty()) ) {
			
			return count;
		}
		for (Tag t2: t.getTags()) {
			
			count += countTags(t2);
		}
		
		return count;
	}
	
	private static UNT getMessageTrailer(UNH unh, List<Tag> interchange) {
		
		UNT unt = new UNT();
		int count = 1;
		for (Tag t: interchange) {
			if ( (t instanceof UNA) || (t instanceof UNB) ) {
				continue;
			}
			count += countTags(t);
		}
		unt.setNumberOfSegments("" + count);
		unt.setMessageReferenceNumber(unh.getMessageReferenceNumber());
		
		return unt;
	}
	
	private static UNZ getInterchangeTrailer(UNB unb) {
		
		UNZ unz = new UNZ();
		unz.setInterchangeControlCount("1");
		unz.setInterchangeControlReference(unb.getInterchangeConrolReference());
		
		return unz;
	}

	public static List<Tag> convert(Cibef102Bean cibef102Bean) {
		
		List<Tag> interchange = new ArrayList<Tag>();
		// Headers
		interchange.add(getServiceStringAdvice()); // UNA(1)
		UNB interchangeHeader = getInterchangeHeader(cibef102Bean);
		interchange.add(interchangeHeader); // UNB(2)
		UNH messageHeader = getMessageHeader(cibef102Bean);
		interchange.add(messageHeader); // UNH(3)
		BGM bgm = getBeginningOfMessage(cibef102Bean); // BGM(4)
		// End of headers
		bgm.getTags().add(getTransportationServiceMode(cibef102Bean)); // TSR(5)
		bgm.getTags().add(getTransitMode(cibef102Bean)); // FTX(11)
		bgm.getTags().add(getTransportationMode(cibef102Bean)); // FTX(12)
		Tag transportationRouteNumber = getTransportationRouteNumber(cibef102Bean); 
		if (transportationRouteNumber != null) {
			bgm.getTags().add(transportationRouteNumber); // FTX(13)
		}
		Tag requestedTariff = getRequestedTariff(cibef102Bean); // FTX(18)
		if (requestedTariff != null) {
			bgm.getTags().add(requestedTariff);
		}
		Tag viaRailwayStationServiceRequest = getViaRailwayStationServiceRequest(cibef102Bean);
		if (viaRailwayStationServiceRequest != null) {
			bgm.getTags().add(viaRailwayStationServiceRequest); // FTX(22)
		}
		bgm.getTags().add(getTransportationOrderNo(cibef102Bean)); // RFF(33)
		bgm.getTags().add(getConsignmentNoteNo()); // RFF(35)
		bgm.getTags().add(getTransportationDetails(cibef102Bean)); // SG8: TDT(47)
		bgm.getTags().add(getMessageSender(cibef102Bean)); // NAD(55)
		bgm.getTags().add(getTransportSender(cibef102Bean)); // NAD(56)
		bgm.getTags().add(getPaymentInstructions(cibef102Bean)); // CPI(65)
		bgm.getTags().add(getTransportRecipient(cibef102Bean)); // NAD(71)
		Tag despatchChargePayer = getDespatchChargePayer(cibef102Bean); // NAD(81)
		if (despatchChargePayer != null) {
			bgm.getTags().add(despatchChargePayer);
		}
		bgm.getTags().addAll(getGoodsItems(cibef102Bean)); // SG18: GID(90)
		Tag wagon = getWagon(cibef102Bean);
		if (wagon != null) {
			bgm.getTags().add(wagon); // SG37: EQD(113)
		}
		bgm.getTags().addAll(getContainers(cibef102Bean)); // SG37: EQD(123)
		interchange.add(bgm);		
		// Start of trailers
		interchange.add(getMessageTrailer(messageHeader, interchange));
		interchange.add(getInterchangeTrailer(interchangeHeader));
		
		return interchange;
	}
}
