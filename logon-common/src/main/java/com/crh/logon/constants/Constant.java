/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.constants;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Description: Constant definitions.
 * 
 * ATTENTION: DO NOT USE ARITHMETIC OPERATORS in the context of these constants,
 * even if it seems to be useful. For the example of statuses, there can always
 * be new ones introduced between existing ones, which will lead logon to
 * unexpected behaviour.Please always check for every constant in particular.
 */
public class Constant {
	
	static 
	{
		/* LOGON-4893: Map containing SAP materials for which a special quantity
		 * must be set to SBB.
		 */
		SPECIAL_MATERIAL_QTY_FOR_SBB = initialSpecialMaterialQtyForSbbMap();
	}
	
	/**
	 * Special fixed quantities to be sent to SBB for certain materials.
	 * For certain materials (see LOGON-4893), a special fixed value (depending on the material)
	 * must be sent to SBB rather than 
	 */
	public static final Map<String, BigDecimal> SPECIAL_MATERIAL_QTY_FOR_SBB;
	
	public static final String LANGUAGE_CODE_ENGLISH = "en";
	public static final String LANGUAGE_CODE_GERMAN = "de";
	public static final String LANGUAGE_CODE_FRENCH = "fr";
	public static final String LANGUAGE_CODE_ITALIAN = "it";
	public static final String LANGUAGE_CODE_SPANISH = "es";
	public static final String LANGUAGE_CODE_CZECH = "cs";
	public static final String LANGUAGE_CODE_SLOVAK = "sk";
	public static final String LANGUAGE_CODE_HUNGARIAN = "hu";
	public static final String LANGUAGE_CODE_ROMANIAN = "ro";
	public static final String LANGUAGE_CODE_BULGARIAN = "bg";
	public static final String LANGUAGE_CODE_RUSSIAN = "ru";
	public static final String LANGUAGE_CODE_POLISH = "pl";
	public static final String LANGUAGE_CODE_AZERBAIJANI = "az";
	public static final String LANGUAGE_CODE_CROATIAN = "hr";
	public static final String LANGUAGE_CODE_SERBIAN = "sr";
	public static final String LANGUAGE_CODE_TURKISH = "tr";
	public static final String LANGUAGE_CODE_DUTCH = "nl";
	
	
	/**
	 * Name of the Random Number Generator (RNG) algorithm which is used to generate passwords.
	 * See Java Cryptography Architecture API Specification & Reference for information about standard RNG algorithm names.
	 */
	public static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
	
	
	/** Default rounding precision for operations on big decimals
	 * that represent money amounts (example: computing the VAT amount)
	 */
	public static final int MONEY_SCALE = 2;
	
	/**
	 * Default rounding mode for operations on big decimals
	 * that represent money amounts (example: computing the VAT amount)
	 */
	public static final int MONEY_ROUNDING_MODE = BigDecimal.ROUND_HALF_EVEN;
	
	// FIXME 08-Nov-2012/stich: [LOGON-5868] This is a temporary workaround. Provide a better solution, such as air distance * correction factor
	public static final Long TMP_TRANSPORT_DURATION_MIN = 60L;
	
	public static final String  NOT_AVAILABLE = "N/A";
	public static final String  ERROR = "Error";

	public static final String DEFAULT_ENCODING = "UTF-8";
	
	// Generic scale to use in BigDecimal operations (divisions, toString, ...)
	public static final int SCALE = 4;
	
	// Generic rounding mode for BigDecimal operations (division, setScale, toString)
	public static final int LOGON_ROUNDING_MODE = BigDecimal.ROUND_HALF_EVEN;
	
	public static final int SQL_IN_STATEMENT_MAX_NUM_OF_LIST_ELEMENTS = 999;
	public static final String SCHEMA_USER = "ulgn01";
	public static final String SCHEMA_USER_PREPEND = SCHEMA_USER + ".";
	
	public static final BigDecimal TON_TO_KG = new BigDecimal(1000);
	
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	
	public static final int DROPDOWN_NOT_SELECTED = -1;
	
	public static final long SYSTEM_USER_ID = 1;
	
	public static final long INVALID_KEY = -1;

	public static final long PACKAGE_TYPE_UNKNOWN = 0;
	public static final long PACKAGE_TYPE_BULK_POWDER = 1;
	public static final long PACKAGE_TYPE_BBAG = 2;
	public static final long PACKAGE_TYPE_SERVICE = 3;
	public static final long PACKAGE_TYPE_BULK_DUMPED = 4;
	// mixed orders
	public static final long MIXED_ORDER_BAG_AND_SERVICE = 5;
	public static final long MIXED_ORDER_BULK_AND_SERVICE = 6;

	
	public static final String PACKAGE_TYPE_UNKNOWN_STR = "unknown";
	public static final String PACKAGE_TYPE_BULK_POWDER_STR = "bulk powder";
	public static final String PACKAGE_TYPE_BULK_DUMPED_STR = "bulk dumped";
	public static final String PACKAGE_TYPE_BULK_GENERAL = "bulk";
	public static final String PACKAGE_TYPE_BBAG_STR = "bag";
	public static final String PACKAGE_TYPE_SERVICE_STR = "service";
	
	//is used for the empty selection in the GUI Drop Down Boxes
	public static final long INVALID_OBJECT_ID = -1;
	public static final String INVALID_OBJECT_ID_STR = "-1";
	
	//is used for the special selection in the GUI Drop Down Boxes
	public static final long SPECIAL_OBJECT_ONE_ID = -2;
	public static final String SPECIAL_OBJECT_ONE_ID_STR = "-2";
	public static final long SPECIAL_OBJECT_TWO_ID = -3;
	public static final String SPECIAL_OBJECT_TWO_ID_STR = "-3";
	public static final long SPECIAL_OBJECT_THREE_ID = -4;
	public static final String SPECIAL_OBJECT_THREE_ID_STR = "-4";
		
	//Order creation types
	public static final long ORDER_CREATION_TYPE_CALL_CENTER = 1;
	public static final long ORDER_CREATION_TYPE_WEB_ORDER = 2;
	public static final long ORDER_CREATION_TYPE_CREATED_BY_LOGON = 3;
	public static final long ORDER_CREATION_TYPE_PICKUP = 5;
	public static final long ORDER_CREATION_TYPE_EXTERNAL_SYSTEM = 6;
	public static final long ORDER_CREATION_TYPE_OC_INTERFACE = 8;
	public static final long ORDER_CREATION_TYPE_OC_CUSTOMER_PORTAL = 7;
	
	// Order status codes
	// ATTENTION: DO NOT USE ARITHMETIC OPERATORS (see header)
	public static final long ORDER_STATUS_NEW = 1;
	public static final long ORDER_STATUS_TRANSMITTED_TO_POOL = 2;
	public static final long ORDER_STATUS_TRANSMITTED_TO_POOL_ACK = 3;
	public static final long ORDER_STATUS_TRANSMITTED_TO_CARRIER = 4;
	public static final long ORDER_STATUS_TRANSMITTED_TO_CARRIER_ACK = 5;
	public static final long ORDER_STATUS_DISPATCHED = 6; // new biz: SCHEDULED
	public static final long ORDER_STATUS_TRANSMITTED_TO_DRIVER = 7; // new biz: RELEASED_FOR_EXECUTION
	public static final long ORDER_STATUS_TRANSMITTED_TO_RAIL = 18; // biz = 18 equals to 7
	public static final long ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK = 8;	// new biz: CONFIRMED_BY_DRIVER
	public static final long ORDER_STATUS_ARRIVED_AT_LOADINGPOINT = 9;
	public static final long ORDER_STATUS_LOADING = 10;
	public static final long ORDER_STATUS_LOADED = 11;
	public static final long ORDER_STATUS_SHIPPING = 12;
	public static final long ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT = 13;
	public static final long ORDER_STATUS_UNLOADING = 14;
	public static final long ORDER_STATUS_DELIVERED = 15;
	public static final long ORDER_STATUS_FINISHED = 16;
	public static final long ORDER_STATUS_DELETED = 17;
	public static final long ORDER_STATUS_DELIVERED_PENDING_LDS_DATA = 21;

	public static final List<Long> orderStatusAllowRegularization = Collections.unmodifiableList(Arrays.asList(
		ORDER_STATUS_TRANSMITTED_TO_DRIVER,
		ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK,
		ORDER_STATUS_ARRIVED_AT_LOADINGPOINT,
		ORDER_STATUS_LOADING,
		ORDER_STATUS_LOADED,
		ORDER_STATUS_SHIPPING,
		ORDER_STATUS_ARRIVED_AT_UNLOADING_POINT,
		ORDER_STATUS_UNLOADING,
		ORDER_STATUS_DELIVERED_PENDING_LDS_DATA
	));




	public static final long ORDER_STATUS_COMPLETED = 16;
	
	//StartLoad and EndLoad statuses, missing from LogonEventRequestFactory.MopFactory
	public static final String EVENT_LOGON_STARTLOAD = "startLoad";
	public static final String EVENT_LOGON_ENDLOAD = "endLoad";
	
	// Customer Order status codes
	public static final long CUSTORDER_STATUS_NEW = 1;
	public static final long CUSTORDER_STATUS_OPEN = 2;
	public static final long CUSTORDER_STATUS_FINISHED = 3;	// TODO 13032013/lajos: rename it to Completed as described in LRQ 070.2
	
	public static final long DAILY_ORDER_STATUS_NOT_CONTACTED_YET = 1;
	public static final long DAILY_ORDER_STATUS_NOT_ACCESSIBLE = 2;
	public static final long DAILY_ORDER_STATUS_NO_ORDER = 3;
	public static final long DAILY_ORDER_STATUS_ALREADY_ORDERED = 4;
	
	public static final int USER_TYPE_CRH = 0;
	public static final int USER_TYPE_CUSTOMER = 1;
	public static final int USER_TYPE_VENDOR = 2;
	public static final int USER_TYPE_DRIVER = 3;
	public static final int USER_TYPE_ADMIN = 4;
	
	public static final long ASSIGNMENT_PART_TYPE_PRE_LOAD = 0;
	public static final long ASSIGNMENT_PART_TYPE_LOAD = 1;
	public static final long ASSIGNMENT_PART_TYPE_TO = 2;
	public static final long ASSIGNMENT_PART_TYPE_UNLOAD = 3;
	public static final long ASSIGNMENT_PART_TYPE_FROM = 4;
	public static final long ASSIGNMENT_PART_TYPE_FINISHED = 5;
	
	public static final long ASSIGN_PART_STATUS_NEW = 0;
	public static final long ASSIGN_PART_STATUS_TRANSMITTED_ACK = 2;
	
	public static final int REROUTING_SUCCESSFUL = 0;
	public static final int REROUTING_UNSUCCESSFUL_NOMATCHING = 1;
	public static final int REROUTING_ORDERS_NOMATCHING_IN_TOUR = 2;
	public static final int REROUTING_UNSUCCESSFUL_UNKNOWN = 3;
	public static final int REROUTING_ORDERITEMS_MATCHING = 4;
	public static final int REROUTING_ORDERITEMS_NOMATCHING_WEIGHT_LESS_IN_LOAD = 5;
	public static final int REROUTING_ORDERITEMS_NOMATCHING_WEIGHT_MORE_IN_LOAD = 6;
	public static final int REROUTING_ORDERITEMS_NOMATCHING_MATERIAL_DIFFERENT = 7;
	public static final int REROUTING_ORDERITEMS_NOMATCHING_PRODPLANT_DIFFERENT = 8;
	public static final int REROUTING_ORDERITEMS_NOMATCHING_STRUCTURE_DIFFERENT = 9;
	
	public static final long RFIDOBJECT_TYPE_PDA_USER = 1;
	public static final long RFIDOBJECT_TYPE_LOADINGPOINT_SILO = 2;
	public static final long RFIDOBJECT_TYPE_PARALLEL_TERMINAL = 3;	
	public static final long RFIDOBJECT_TYPE_TRUCK = 4;
	public static final long RFIDOBJECT_TYPE_TRAILER = 5;
	public static final long RFIDOBJECT_TYPE_CONTAINER = 6;
	public static final long RFIDOBJECT_TYPE_WAGON = 7;
	public static final long RFIDOBJECT_TYPE_SHIPTO_SILO = 8;
	public static final long RFIDOBJECT_TYPE_FORKLIFT= 9;
	
	public static final long SILO_VERIFICATION_TYPE_NOT_VERIFIED = 1;
	public static final long SILO_VERIFICATION_TYPE_RFID_SCAN = 2;
	public static final long SILO_VERIFICATION_TYPE_RFID_NAME = 3;
	public static final long SILO_VERIFICATION_TYPE_RFID_LABEL = 4;
	
	public static final long TKATEGORIE_SILO = 1;
	public static final long TKATEGORIE_CONTAINER = 2;
	public static final long TKATEGORIE_TRAILER = 3;
	public static final long TKATEGORIE_BAG = 4;
	

	public static final long TRAILER_TKATEGORIE_BULK_POWDER = 1;
	public static final long TRAILER_TKATEGORIE_CONTAINER = 2;
	public static final long TRAILER_TKATEGORIE_BAG = 4;
	public static final long TRAILER_TKATEGORIE_BULK_DUMPED = 5;
	public static final long TRAILER_TKATEGORIE_CONCRETE = 6;
	public static final long TRAILER_TKATEGORIE_ALL_MAT_PACKAGE_TYPES = 7;
	
	public static final long TRAILER_TYPE_HORIZONTAL = 1;
	public static final long TRAILER_TYPE_TILTABLE = 2;
	
	public static final long WAGON_TKATEGORIE_SILO = 1;
	public static final long WAGON_TKATEGORIE_CONTAINER = 2;
	public static final long WAGON_TKATEGORIE_BAG = 4;

	public static final long WAGON_STATUS_AT_SHIPPINGPOINT = 0;
	public static final long WAGON_STATUS_SUBMITTED = 1;
	public static final long WAGON_STATUS_INCOMING = 2;
	public static final long WAGON_STATUS_LOADED = 3;	

	// 'Delivery Note Print Type' codes
	public static final long DELIV_NOTE_PRINT_TYPE_LOGON = 1;
	public static final long DELIV_NOTE_PRINT_TYPE_WEIGH_SYSTEM = 2;
	
	public static final String UOM_KG = "KG";
	public static final String UOM_TONS = "TO";
	public static final String UOM_DIMENSION_TONS = "TO";
	public static final String UOM_METER = "M";
	public static final String UOM_LKW = "LKW";
	public static final String UOM_CONTAINER = "CON";
	public static final String UOM_WAGON = "BW";
	public static final String UOM_PCE = "PCE";
	public static final String UOM_BAG_P40 = "P40";
	public static final String UOM_BAG_P48 = "P48";
	public static final String UOM_EMPTY_WAGON_54T = "54T";
	public static final String UOM_EMPTY_WAGON_27T = "27T";
	public static final String UOM_EMPTY_WAGON_36T = "36T";
	public static final String UOM_EMPTY_WAGON_15T = "15T";
	public static final String UOM_MTQ = "MTQ";
	public static final String UOM_BAG = "BAG";
	public static final String UOM_PAL = "PAL";
	public static final String UOM_HRE = "HRE";
	public static final String UOM_MIN = "MIN";
	
	public static final String[] LOGON_AID_UOMS = {
		UOM_LKW, UOM_CONTAINER, UOM_WAGON
	};
	
	public static final String UOM_KG_TXT = "kg";
	public static final String UOM_TONS_TXT = "t";
	public static final String UOM_MTQ_TXT = "m\u00B3"; // m3
	
	public static final long DELIVERY_TYPE_ROAD_DIRECT = 1;
	public static final long DELIVERY_TYPE_RAIL_DIRECT = 2;
	public static final long DELIVERY_TYPE_BIMODAL_RAIL = 3;
	public static final long DELIVERY_TYPE_BIMODAL_ROAD = 4;
	public static final long DELIVERY_TYPE_PICKUP = 5;
	public static final long DELIVERY_TYPE_BLOCK_TRAIN = 6;
	public static final long DELIVERY_TYPE_SHIP = 7;
	public static final long DELIVERY_TYPE_SERVICE = 8;
	public static final long DELIVERY_TYPE_RETURN_PICKUP = 9;
	public static final long DELIVERY_TYPE_RETURN_DELIVERED = 10;
	public static final long DELIVERY_TYPE_SHIP_PICKUP = 11;
	public static final long DELIVERY_TYPE_BLOCKTRAIN_PICKUP = 12;
	
	// Shipping Point types
	public static final long SHIPPINGPOINT_TYPE_ROAD = 0;
	public static final long SHIPPINGPOINT_TYPE_RAIL= 1;
	public static final long SHIPPINGPOINT_TYPE_CONTAINER= 2;
	public static final long SHIPPINGPOINT_TYPE_FVL_BIMODAL_TRUCK= 3;
	public static final long SHIPPINGPOINT_TYPE_RAIL_SUPPLIER= 4;
	public static final long SHIPPINGPOINT_TYPE_RAIL_REPAIR= 5;
	public static final long SHIPPINGPOINT_TYPE_RAIL_EXTERNAL_JUST_RETURN= 6;
	public static final long SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_RAIL= 7;
	public static final long SHIPPINGPOINT_TYPE_CONTAINER_TERMINAL_TRUCK= 2;
	public static final long SHIPPINGPOINT_TYPE_SIMPLE_VEHICLE= 8;

	// Shipping condition types
	public static final String SHIPPINGPOINT_CONDITION_TRUCK = "01";
	
	// Weighing system types on shipping point level
	public static final long WEIGH_SYSTEM_SP_NONE = 0;
	public static final long WEIGH_SYSTEM_SP_PARALLEL_TRUCK = 1;
	public static final long WEIGH_SYSTEM_SP_CONTAINER_LOADING = 4;
	public static final long WEIGH_SYSTEM_SP_PARALLEL_RAIL = 5;
	public static final long WEIGH_SYSTEM_SP_WEWAS = 6;
	public static final long WEIGH_SYSTEM_SP_FENICS = 7;
	public static final long WEIGH_SYSTEM_SP_DORNER = 8;
	public static final long WEIGH_SYSTEM_SP_KDTRUCK = 9;
	public static final long WEIGH_SYSTEM_SP_D800 = 10;
	
	// Weighing system types on loading point level
	public static final long WEIGH_SYSTEM_LP_WS = 0;
	public static final long WEIGH_SYSTEM_LP_LOGON = 2;
	public static final long WEIGH_SYSTEM_LP_PDA = 3;
	
	public static final long SHIPTO_TYPE_STANDARD = 0;
	public static final long SHIPTO_TYPE_TRANSPORTEURLAGER = 1;
	public static final long SHIPTO_TYPE_LOCATION_NOT_FIXED = 2;
	public static final long SHIPTO_TYPE_FVL = 3;
	public static final long SHIPTO_TYPE_CONTAINER_TERMINAL = 4;
	public static final long SHIPTO_TYPE_MAINTENANCE = 5;
	public static final long SHIPTO_TYPE_PLANT = 6;
	public static final long SHIPTO_TYPE_SUPPLIER = 7;
	
	public static final long SIGNATURE_TYPE_MANDATORY = 0;
	public static final long SIGNATURE_TYPE_OPTIONAL = 1;
	public static final long SIGNATURE_TYPE_NO_SIGNATURE = 2;
	
	public static final long AUDIT_ACTION_TABLEBROWSER_INSERT= 0;
	public static final long AUDIT_ACTION_TABLEBROWSER_UPDATE = 1;
	public static final long AUDIT_ACTION_TABLEBROWSER_DELETE = 2;
	public static final long AUDIT_ACTION_USERADMIN_NEW_USER = 10;
	public static final long AUDIT_ACTION_USERADMIN_PASSWORD_CHANGE = 11;
	public static final long AUDIT_ACTION_USERADMIN_INACTIVATE_USER = 12;
	public static final long AUDIT_ACTION_USERADMIN_ACTIVATE_USER = 13;
	
	public static final long BIZEVENT_TYPE_NEW = 101;
	public static final long BIZEVENT_TYPE_TRANSMITTED_TO_POOL = 102;
	public static final long BIZEVENT_TYPE_TRANSMITTED_TO_POOL_ACK = 103;
	public static final long BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER = 104;
	public static final long BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER_ACK = 105;
	public static final long BIZEVENT_TYPE_DISPATCHED = 106;
	public static final long BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK = 107;
	public static final long BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK_ACK = 108;
	public static final long BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT = 109;
	public static final long BIZEVENT_TYPE_LOADING = 110;
	public static final long BIZEVENT_TYPE_LOADED = 111;
	public static final long BIZEVENT_TYPE_SHIPPING = 112;
	public static final long BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT = 113;
	public static final long BIZEVENT_TYPE_UNLOADING = 114;
	public static final long BIZEVENT_TYPE_DELIVERED = 115;
	public static final long BIZEVENT_TYPE_FINISHED = 116;
	public static final long BIZEVENT_TYPE_DELETED = 117;
	public static final long BIZEVENT_TYPE_POSITION = 118;
	public static final long BIZEVENT_TYPE_TRANSMITTED_TO_RAIL = 119;
	public static final long BIZEVENT_TYPE_SET_COMM_CARRIER = 120;
	public static final long BIZEVENT_TYPE_SET_POOL = 121;	
	public static final long BIZEVENT_TYPE_POOL_ACK = 122;
	public static final long BIZEVENT_TYPE_COMM_CARRIER_ACK = 123;
	public static final long BIZEVENT_TYPE_CANCEL_ON_WEIGH_SYSTEM = 124;
	public static final long BIZEVENT_TYPE_SET_EXEC_CARRIER = 125;
	public static final long BIZEVENT_TYPE_EXEC_CARRIER_REFUSED = 126;
	public static final long BIZEVENT_TYPE_COMM_CARRIER_REFUSED = 127;
	public static final long BIZEVENT_TYPE_LOADING_CANCELLED = 130;
	public static final long BIZEVENT_TYPE_PRE_LOAD_TRANSMITTED_TO_TRUCK = 150;
	public static final long BIZEVENT_TYPE_PRE_LOAD_TRANSMITTED_TO_TRUCK_ACK = 151;
	public static final long BIZEVENT_TYPE_RETURN_TRANSMITTED_TO_TRUCK = 152;
	public static final long BIZEVENT_TYPE_RETURN_TRANSMITTED_TO_TRUCK_ACK = 153;
	public static final long BIZEVENT_TYPE_PRE_LOAD_CANCELLED = 154;
	public static final long BIZEVENT_TYPE_RETURN_CANCELLED = 155;
	public static final long BIZEVENT_TYPE_CANCEL_ASSIGNMENT = 160;
	public static final long BIZEVENT_TYPE_CREATE_EXTERNAL_ASSIGNMENT = 161;
	public static final long BIZEVENT_TYPE_EDIT_ASSIGNMENT = 162;
	public static final long BIZEVENT_TYPE_REMOVE_ASSIGNMENT = 163;
	public static final long BIZEVENT_TYPE_RETURNED = 165;
	public static final long BIZEVENT_TYPE_RE_ALLOCATE = 166;
	public static final long BIZEVENT_TYPE_DELIVERY_NOTE = 170;
	public static final long BIZEVENT_TYPE_SET_SILO = 171;
	public static final long BIZEVENT_TYPE_LEAVE_SHIPTO = 172;
	public static final long BIZEVENT_TYPE_SCAN_WAGON = 173;
	public static final long BIZEVENT_TYPE_SCAN_CONTAINER = 174;
	public static final long BIZEVENT_TYPE_MODIFY_FINISHED = 175;
	public static final long BIZEVENT_TYPE_TRUCK_DRIVER_CHANGED = 201;
	public static final long BIZEVENT_TYPE_TRUCK_COMPOSITION_CHANGED = 202;
	public static final long BIZEVENT_TYPE_TRUCK_COMPOSITION_CONFIRMED = 203;
    public static final long BIZEVENT_TYPE_TRAFFIC_JAM_START = 210;
    public static final long BIZEVENT_TYPE_TRAFFIC_JAM_END = 211;
    public static final long BIZEVENT_TYPE_EDIT_FIELD = 220;
    public static final long BIZEVENT_TYPE_FORCE_TO_SEND_TO_SAP = 221;
    public static final long BIZEVENT_TYPE_DELIVERYDOC_SUCCESS = 300;
    public static final long BIZEVENT_TYPE_SHIPMENTDOC_SUCCESS = 301;
    public static final long BIZEVENT_TYPE_TRANSFERDOC_SUCCESS = 302;
    public static final long BIZEVENT_TYPE_CN_DELIVERYDOC_SUCCESS = 303;
    public static final long BIZEVENT_TYPE_CN_SHIPMENTDOC_SUCCESS = 304;
    public static final long BIZEVENT_TYPE_DELIVERYDOC_FAIL = 310;
    public static final long BIZEVENT_TYPE_SHIPMENTDOC_FAIL = 311;
    public static final long BIZEVENT_TYPE_TRANSFERDOC_FAIL = 312;
    public static final long BIZEVENT_TYPE_CN_DELIVERYDOC_FAIL = 313;
    public static final long BIZEVENT_TYPE_CN_SHIPMENTDOC_FAIL = 314;
    public static final long BIZEVENT_TYPE_UPDATED_SCHEDULING = 315;
    public static final long BIZEVENT_TYPE_REGULARIZATION = 316;
    
	public static final List<Long> BIZEVENTS_WITH_TS_FOR_ORDER_FINAL = Arrays.asList
		(
			BIZEVENT_TYPE_NEW,
			BIZEVENT_TYPE_TRANSMITTED_TO_POOL,
			BIZEVENT_TYPE_POOL_ACK,
			BIZEVENT_TYPE_TRANSMITTED_TO_COMM_CARRIER,
			BIZEVENT_TYPE_COMM_CARRIER_ACK,
			BIZEVENT_TYPE_SET_EXEC_CARRIER,
			BIZEVENT_TYPE_DISPATCHED,
			BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK,
			BIZEVENT_TYPE_TRANSMITTED_TO_TRUCK_ACK,
			BIZEVENT_TYPE_TRANSMITTED_TO_RAIL,
			BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT,
			BIZEVENT_TYPE_LOADING,
			BIZEVENT_TYPE_LOADED,
			BIZEVENT_TYPE_SHIPPING,
			BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT,
			BIZEVENT_TYPE_UNLOADING,
			BIZEVENT_TYPE_DELIVERED,
			BIZEVENT_TYPE_LEAVE_SHIPTO
		);
	
	public static final List<Long> BIZEVENTS_FIRST_TS_NEEDED_FOR_ORDER_FINAL = Arrays.asList
		(
			BIZEVENT_TYPE_ARRIVED_AT_LOADINGPOINT,
			BIZEVENT_TYPE_LOADING,
			BIZEVENT_TYPE_ARRIVED_AT_DELIVERY_POINT,
			BIZEVENT_TYPE_UNLOADING
		);

    public static final long FUNCTION_GROUP_PDA_MIN = 14000;
    public static final long FUNCTION_GROUP_PDA_MAX = 14999;
    
    public static final long MIME_TYPE_GIF = 1;
    public static final long MIME_TYPE_JPEG = 2;
    public static final long MIME_TYPE_PNG = 3;
    public static final long MIME_TYPE_PDF = 4;
    public static final long MIME_TYPE_PS = 5;
    public static final long MIME_TYPE_CSV = 7;
    public static final long MIME_TYPE_XLS = 8;
    public static final long MIME_TYPE_DOC = 9;
    public static final long MIME_TYPE_TXT = 10;
    
    public static final long ORDER_FIRST_ITEM_CD = 10;
    public static final String DISTRIBUTION_DEST_OUTBOUND = "OUT";
    public static final String DISTRIBUTION_DEST_REPAIR = "REP";
    public static final String DISTRIBUTION_DEST_INTERUNIT_DIST = "IUD";
    public static final String DISTRIBUTION_DEST_INTERUNIT_PROD = "IUP";
    public static final String DISTRIBUTION_DEST_INBOUND = "INB";
    public static final String DISTRIBUTION_DEST_OUTBOUND_RETURN = "OUTR";
    public static final String DISTRIBUTION_DEST_INTERUNIT_DIST_RETURN = "IUDR";
    public static final String DISTRIBUTION_DEST_INTERUNIT_PROD_RETURN = "IUPR";
    public static final String DISTRIBUTION_DEST_INBOUND_RETURN = "INBR";
    
    public static final String[] DISTRIBUTION_DEST_SPECIAL = {
    	DISTRIBUTION_DEST_REPAIR, DISTRIBUTION_DEST_OUTBOUND_RETURN, DISTRIBUTION_DEST_INTERUNIT_DIST_RETURN,
    	DISTRIBUTION_DEST_INTERUNIT_PROD_RETURN, DISTRIBUTION_DEST_INBOUND_RETURN
    };
    
    public static final String[] DISTRIBUTION_DEST_RETURN = {
    	DISTRIBUTION_DEST_OUTBOUND_RETURN, DISTRIBUTION_DEST_INTERUNIT_DIST_RETURN,
    	DISTRIBUTION_DEST_INTERUNIT_PROD_RETURN, DISTRIBUTION_DEST_INBOUND_RETURN
    };    

	public static final long ASSIGNMENT_STATUS_NEW = 0;
	public static final long ASSIGNMENT_STATUS_PRE_LOAD = 1;
	public static final long ASSIGNMENT_STATUS_RELEASED_BY_PRESCHEDULER = 8;	
	public static final long ASSIGNMENT_STATUS_LOADING = 2;
	public static final long ASSIGNMENT_STATUS_SHIPPING = 3;
	public static final long ASSIGNMENT_STATUS_UNLOADING = 4;
	public static final long ASSIGNMENT_STATUS_RETURNING = 5;
	public static final long ASSIGNMENT_STATUS_FINISHED = 6;
	public static final long ASSIGNMENT_STATUS_CANCELLED = 7;
	
	public static final long RETURN_QUANT_HANDLING_NO_ADJUSTMENT = 0;
	public static final long RETURN_QUANT_HANDLING_ADJUSMENT_NEEDED = 1;
	public static final long RETURN_QUANT_HANDLING_ADJUSTED = 2;
	public static final long RETURN_QUANT_HANDLING_CREDIT_NOTE_NEEDED = 3;
	public static final long RETURN_QUANT_HANDLING_CREDIT_NOTE_CREATED = 4;
	public static final long RETURN_QUANT_HANDLING_NOT_SET = -1;
	
	public static final long PDA_SESSION_STATUS_INITIALIZING = 1;
    public static final long PDA_SESSION_STATUS_READY = 2;
    public static final long PDA_SESSION_STATUS_TERMINATED = 3;
    public static final long PDA_SESSION_STATUS_EVENTS_PENDING = 4;
    public static final long PDA_SESSION_STATUS_TIMED_OUT = 5;
    
    public static final long PDA_MESSAGE_TYPE_TO_DRIVER = 1;
    public static final long PDA_MESSAGE_TYPE_TO_DISPATCHER = 2;
    public static final long PDA_MESSAGE_TYPE_ERROR = 3;
    public static final long PDA_MESSAGE_TYPE_MARKET_INFO = 4;

    public static final long SILO_TEST_DATA_UPPER_LIMIT = -11;
    
    public static final String COUNTRY_CODE_CH = "CH";
    public static final String COUNTRY_CODE_IT = "IT";

	public static Date INFINITE_DATE;
	//refresh dynamic objects every 20 seconds
	public static final long LIVE_MAP_REFRESH_TIME = 20000;

	public static final long ASSIGNMENT_TYPE_INTERNAL = 0;
	public static final long ASSIGNMENT_TYPE_EXTERNAL = 1;
	
	public static final long WAGON_EVENT_STATUS_NEW = 1;
	public static final long WAGON_EVENT_STATUS_SUCCESS = 2;
	public static final long WAGON_EVENT_STATUS_FAILURE = 3;
	public static final long WAGON_EVENT_STATUS_IGNORED = 4;
	
	public static final String TRSP_COND_FIXED = "TRf";
	public static final String TRSP_COND_VARIABLE = "TRv";
	public static final String TRSP_COND_TRSP_COST = "TC";
	
	// transportation modes (TLGNR_SAP_TRSP_MODE_BS)
	public static final String TRANSPORTATION_MODE_TRUCK = "10";
	public static final String TRANSPORTATION_MODE_TRAIN = "50";
	public static final String TRANSPORTATION_MODE_PICKUP = "00";
	public static final String TRANSPORTATION_MODE_BLOCK_TRAIN = "20";
	public static final String TRANSPORTATION_MODE_SHIP = "30";

	// loading types (sent to PDA)
	public static final String LOADING_TYPE_BULK = "bulk";
	public static final String LOADING_TYPE_BAG = "bag";
	public static final String LOADING_TYPE_EXTERNAL = "external";
	public static final String LOADING_TYPE_CONTAINER = "container";
	public static final String LOADING_TYPE_WAGON = "wagon";
	public static final String LOADING_TYPE_ACKNOWLEDGE_FULL_TRUCK = "ackFullTruck";
	public static final String LOADING_TYPE_RETURN_PALLET_ONLY = "returnPalletOnly";
	
	// site types (sent to PDA)
	public static final String SITE_TYPE_LOADING_SITE = "loadingSite";
	public static final String SITE_TYPE_UNLOADING_SITE = "unloadingSite";
	
	// weighing types (sent to PDA)
    public static final String WEIGHING_TYPE_PARALLEL = "parallel";
    public static final String WEIGHING_TYPE_PDA = "pda";
    public static final String WEIGHING_TYPE_LOGON = "logon";
    
    // delivery types (sent to PDA) (NOT the same as TLGNR_DELIVERY_TYPE)
    public static final String PDA_DELIVERY_TYPE_CONTAINER = "container";
    
    // type of loadOrder message (sent to PDA)
	public enum LoadOrderType {
		LOADING_AT_CRH_SITE,
		ACKNOWLEDGE_FULL_TRUCK,
		RETURN_PALLET_ONLY,
		MEASURING_BEFORE_UNLOAD;
		}
	
    public static final String MATERIAL_PACKAGE_NULL = "NULL";
    public static final String MATERIAL_PACKAGE_BULK_POWDER = "S001";
    public static final String MATERIAL_PACKAGE_BAG = "S002";
    public static final String MATERIAL_PACKAGE_PALET_EMPTY = "S003";
    public static final String MATERIAL_PACKAGE_SILO = "S004";
    public static final String MATERIAL_PACKAGE_SERVICE = "S005";
    public static final String MATERIAL_PACKAGE_BULK_DUMPED = "S006";
    
    
	public static final long INTERFACE_TYPE_MOP_11 = 2;
	public static final long INTERFACE_TYPE_PWS_10 = 3;
	public static final long INTERFACE_TYPE_LOGON_LDS_INTERFACE_10 = 6;
	public static final long INTERFACE_TYPE_ORDER_CREATION_INTERFACE = 7;
	
	//Truck trsp categories 
	public static final long TRUCK_TRSP_CAT_BULK_POWDER = 1;
	public static final long TRUCK_TRSP_CAT_CONTAINER = 2;
	public static final long TRUCK_TRSP_CAT_TRAILER = 3;
	public static final long TRUCK_TRSP_CAT_GENERAL_FREIGHT = 4;
	public static final long TRUCK_TRSP_CAT_BULK_DUMPED = 5;
	public static final long TRUCK_TRSP_CAT_CONCRETE = 6;
	public static final long TRUCK_TRSP_CAT_ALL_MAT_PACKAGE_TYPES = 7;
	
	
	
	//Truck priority
	public static final long TRUCK_PRIORITY_MAX = 1;
	public static final long TRUCK_PRIORITY_MIN = 99;
	/** This priority value indicates that the truck is inactive. */
	public static final long TRUCK_PRIORITY_TRUCK_INACTIVE = 100;
	
	//Simple Vehicle priority
	public static final long SIMPLE_VEHICLE_PRIORITY_MAX = 0;
	public static final long SIMPLE_VEHICLE_PRIORITY_MIN = 99;

	// Simple vehicle 
	public static final long SIMPLE_VEHICLE_UNIT_COUNT_MIN = 0;
	public static final long SIMPLE_VEHICLE_UNIT_COUNT_MAX = 99;
	public static final long SIMPLE_VEHICLE_NR_CHAMBERS_MIN = 0;
	public static final long SIMPLE_VEHICLE_NR_CHAMBERS_MAX = 99;
	
	//prod palnt
    public static final String PROD_PLANT_SIGGENTHAL = "C102";
	
    //sequence names
    public static final String SEQUENCE_LOGON_EVENTID = "SLGNC_LOGON_EVENTID";
    
    // Status of OUTGOING events/messages
	public static final int STATUS_CODE_LOGON_MESSAGE_TECHNICAL_ERROR_CD = 20101;
	public static final int STATUS_CODE_LOGON_MESSAGE_PDA_OFFLINE_CD = 20301;
	public static final int STATUS_CODE_LOGON_MESSAGE_PDA_INTERRUPTED_CD = 20303;
	public static final int STATUS_CODE_LOGON_EVENT_TECHNICAL_ERROR_CD = 40101;

	public static final String PARALLEL_REMARK_LEVEL_WARNING = "WARNING";
	public static final String PARALLEL_REMARK_LEVEL_INFO = "INFO";
	
	public static final String VEHICLE_TYPE_TRUCK = "truck";
	public static final String VEHICLE_TYPE_WAGON = "wagon";
	public static final String VEHICLE_TYPE_SIMPLE_VEHICLE = "simve";
	
	public static final long RFID_SOURCE_LOGON = 1;
	public static final long RFID_SOURCE_SYNC = 2;
	
	public static final int WAGON_MAINTENANCE_DEFAULT_ACTIVATION_PERIOD_D = 14;
	
	public static final Map<Long, String> DEL_TYPE_TRSP_MODE_MAP = new HashMap<Long, String>();
	
	public static final long ASSIGN_STATUS_TR_NOT_TRANSMITTED = 0;
	public static final long ASSIGN_STATUS_TR_TRANSMITTED = 1;
	
	public static final int CHART_TRANS_CAT_TRUCK = 1;
	public static final int CHART_TRANS_CAT_RAIL = 2;
	
	public static final double MAP_CENTER_LAT = 46.878968;
	public static final double MAP_CENTER_LNG = 8.432007;
	public static final int MAP_CENTER_ZOOM_LEVEL = 8;
	
	public static final String CURRENCY_CODE_CHF = "CHF";
	public static final String CURRENCY_CODE_EUR = "EUR";
	
	//See LOGON-4148 currently only CHE uses rail return orders.
	public static final String CH_90_SGROUP = "CH90";
	public static final String DIVISON_CD_CEMENT = "10";

	public static final Set<Long> PLANNING_MATERIAL_IDS = new HashSet<Long>(Arrays.asList(1L, 2L));
	
	
	static {
		DEL_TYPE_TRSP_MODE_MAP.put(DELIVERY_TYPE_SHIP, TRANSPORTATION_MODE_SHIP);
		DEL_TYPE_TRSP_MODE_MAP.put(DELIVERY_TYPE_BLOCK_TRAIN, TRANSPORTATION_MODE_BLOCK_TRAIN);
		DEL_TYPE_TRSP_MODE_MAP.put(DELIVERY_TYPE_ROAD_DIRECT, TRANSPORTATION_MODE_TRUCK);
		DEL_TYPE_TRSP_MODE_MAP.put(DELIVERY_TYPE_RAIL_DIRECT, TRANSPORTATION_MODE_TRAIN);
		DEL_TYPE_TRSP_MODE_MAP.put(DELIVERY_TYPE_BIMODAL_RAIL, TRANSPORTATION_MODE_TRAIN);
		DEL_TYPE_TRSP_MODE_MAP.put(DELIVERY_TYPE_BIMODAL_ROAD, TRANSPORTATION_MODE_TRUCK);
		DEL_TYPE_TRSP_MODE_MAP.put(DELIVERY_TYPE_PICKUP, TRANSPORTATION_MODE_PICKUP);
		
		final Calendar c = Calendar.getInstance();
		c.clear();
		c.set(9999, 11, 31);
		INFINITE_DATE = c.getTime();
	}
	
	// The DB entries for artificial pickup and generic carrier cases
	// If change is requied for these values please also update the following sql: 
	// /logon-persistence/etc/sql/refdata/artificial_pickup_entries.sql
	
	public static final Long ARTIFICIAL_PICKUP_CARRIER_ID = 1L;
	public static final Long ARTIFICIAL_PICKUP_POOL_ID = 10L;
	public static final Long ARTIFICIAL_PICKUP_BAG_TRUCK_ID = 1L;
	public static final Long ARTIFICIAL_PICKUP_BULK_PWDR_TRUCK_ID = 2L;
	public static final Long ARTIFICIAL_PICKUP_BULK_DMP_TRUCK_ID = 5L;

	public static final Long ARTIFICIAL_SHIP_PICKUP_POOL_ID = 11L;
	public static final Long ARTIFICIAL_BLOCKTRAIN_PICKUP_POOL_ID = 12L;
	
	public static final Long ARTIFICIAL_GENERIC_CARRIER_ID = 2L;
	public static final Long ARTIFICIAL_GENERIC_CARRIER_BAG_TRUCK_ID = 3L;
	public static final Long ARTIFICIAL_GENERIC_CARRIER_BULK_TRUCK_ID = 4L;
	
	public static final String SAP_EXCEPTIONAL_SHIPPINGPOINT_CODE = "C122";

	public static final String CRONSTATUS_SAPIMPORT_BP = "sapimport_bp";
	public static final String CRONSTATUS_SAPIMPORT_MAT = "sapimport_mat";
	public static final String CRONSTATUS_SAPIMPORT_VENDOR = "sapimport_vendor";
	public static final String CRONSTATUS_SAPIMPORT_NETP = "sapimport_netp";
	public static final String CRONSTATUS_SAPIMPORT_CLEAN_LOG = "sapimport_clean_log";
	public static final String CRONSTATUS_PDA_SESSION_REAPER = "pda_session_reaper";
	public static final String CRONSTATUS_RFID_OBJECT_LABEL_CLEANUP = "rfid_object_label_cleanup";
	public static final String CRONSTATUS_UPDATE_ORDERS = "update_orders";
	public static final String CRONSTATUS_EDIFACT_IMPORT = "edifact_import";
	public static final String CRONSTATUS_WAGON_EVENT_PROC = "wagon_event_proc";
	public static final String CRONSTATUS_SEND_DAILY_REPORTS = "send_daily_reports";
	public static final String CRONSTATUS_DELIVNOTE_CLEANUP = "delivnote_cleanup";
	public static final String CRONSTATUS_SAPIMPORT_BPN = "sapimport_bpn";
	public static final String CRONSTATUS_SAPIMPORT_MN = "sapimport_mn";
	public static final String CRONSTATUS_SAPIMPORT_MNN = "sapimport_mnn";
	public static final String CRONSTATUS_SAPIMPORT_SDN = "sapimport_sdn";
	public static final String CRONSTATUS_SAPIMPORT_VEHN = "sapimport_vehn";
	public static final String CRONSTATUS_SAPIMPORT_VN = "sapimport_vn";
	public static final String CRONSTATUS_ARCHIVE_ORDERS = "archive_orders";
	public static final String CRONSTATUS_POSTPROCESS_SAPIMPORT_VEHN = "postprocess_sapimport_vehn";
	public static final String CRONSTATUS_SEND_STAGING_AREA_REPORT_BPN = "send_staging_area_report_bpn";
	public static final String CRONSTATUS_SEND_STAGING_AREA_REPORT_VN = "send_staging_area_report_vn";
	public static final String CRONSTATUS_SEND_STAGING_AREA_REPORT_MN = "send_staging_area_report_mn";
	public static final String CRONSTATUS_UPDATE_PLANNING_MAT_SAREA = "update_planning_mat_sarea";
	public static final String CRONSTATUS_ORDER_NOTIFICATION = "order_notification";
	
	public static final Long ORDER_DELIVERY_TYPE_DO_NOT_EXPORT = 0l;
	public static final Long ORDER_DELIVERY_TYPE_LOADED = 11L;
	public static final Long ORDER_DELIVERY_TYPE_DELIVERED = 15L;
		
	public static final String KEY_PASSWORD_CHANGE_ORIGINAL_URI = "original_url";
	
	// TODO boon, 20101229: This authorization function is checked in the business tier.
	// But the Authorization class is only available in the web tier.
	public static final Long REPORT_LOAD_SHIPPING_STATS = 7510L; // Load Shipping Stats
	public static final String REPORT_CD_LOAD_SHIPPING_STATS = "20";

	public static final String REPORT_CD_STAGING_AREA_ERRORS_BPN = "22";
	public static final String REPORT_CD_STAGING_AREA_ERRORS_VN = "23";
	public static final String REPORT_CD_STAGING_AREA_ERRORS_MN = "24";
	public static final String REPORT_CD_GOODSMOVEMENT = "25";
	public static final String REPORT_CD_TRANSPLUS = "27";
	
	public static final String SALES_DOC_CD_INCOMPLETE_LOAD = "X";

	public static final Long ORDER_REP_ATTACHMENT_TYPE_OFFER = 1L;
	public static final Long ORDER_REP_ATTACHMENT_TYPE_DOCUMENTATION = 2L;
	public static final Long ORDER_REP_ATTACHMENT_TYPE_INVOICE = 3L;
	public static final Long ORDER_REP_ATTACHMENT_TYPE_EMAIL = 4L;

	// Wagon Maintenance - status flags
	public static final Long RAIL_REP_STATUS_OPEN = 0L;
	public static final Long RAIL_REP_STATUS_FINISHED = 1L;
	public static final Long RAIL_REP_STATUS_FINISHED_COMMERCIAL = 2L;	
	
	// Dispo - Filter options for generic truck
	public static final int FILTER_FOR_TRUCK_ALL = 0;
	public static final int FILTER_FOR_TRUCK_PLANNING_ONLY = 1;
	public static final int FILTER_FOR_TRUCK_REAL_ONLY = 2;
	
	// Dispo - Filter options for rail
	public static final int FILTER_FOR_RAIL_WAGON_NR_LENGTH = 12;
	
	// Incoterms on tlgns_sap_contracts
	public static final String INCOTERMS_EXW = "EXW";		// Ex Works (Pickup)
	public static final String INCOTERMS_FCA = "FCA";		// Free Carrier
	public static final String INCOTERMS_FAS = "FAS";		// Free Alongside Ship
	public static final String INCOTERMS_FOB = "FOB";		// Free on bord
	public static final String INCOTERMS_CFR = "CFR";		// Cost and Freight
	public static final String INCOTERMS_CIF = "CIF";		// Cost, insurance and freight
	public static final String INCOTERMS_CPT = "CPT";		// Carriage Paid to
	public static final String INCOTERMS_CIP = "CIP";		// Carriage and insurance Paid to
	public static final String INCOTERMS_DAF = "DAF";		// Delivered at frontier
	public static final String INCOTERMS_DES = "DES";		// Delivered Ex Ship
	public static final String INCOTERMS_DEQ = "DEQ";		// Delivered Ex Quay
	public static final String INCOTERMS_DDU = "DDU";		// Delivered Duty Unpaid
	public static final String INCOTERMS_DDP = "DDP";		// Delivered Duty Paid
	public static final String INCOTERMS_DAP = "DAP";		// Delivered at Place
	public static final String INCOTERMS_DAT = "DAT";		// Delivered at Terminal

	// Return incoterms used by LDS
	public static final String INCOTERMS_RETURN_EXW = "REXW";		// return pickup
	public static final String INCOTERMS_RETURN_DDP = "RDDP";		// return delivered
	
	public static final String GENERIC_MATERIAL_GROUPING_CRITERIA_DIVISION = "1";
	public static final String GENERIC_MATERIAL_GROUPING_CRITERIA_PRICING_GROUP = "2";

    public final static String SAP_CONTRACT_TYPE_FIXED_PRICE = "ZCQ"; // Agreement with Customer with fixed prices 
    public final static String SAP_CONTRACT_TYPE_CRH_COMPANIES= "ZICO"; // Agreement between crh companies
	/**
	 * One time customers has the generic contract type. They requiring the cash sales invoice, 
	 * if TLGNC_SAREA.CS_ENABLE_ONE_TIME_CUST_FLAG=TRUE. 
	 */
    public final static String SAP_CONTRACT_TYPE_GENERIC_CONTRACT = "ZONC"; 
    public final static String SAP_CONTRACT_TYPE_FREE_OF_CHARGE = "ZFCH";
    public final static String SAP_CONTRACT_TYPE_ADVANCE_PAYMENT = "ZADV";

    public final static String SAP_CONTRACT_TYPE_QUANTITY_CONTRACT_PRICE = "ZCP";
	
	public static final long SAP_CONTRACT_ITEM_GROUPING_TYPE_UNKNOWN = 0;
	public static final long SAP_CONTRACT_ITEM_GROUPING_TYPE_NO_GROUPING = 1;
	public static final long SAP_CONTRACT_ITEM_GROUPING_TYPE_QUANTITY_RESTRICTED = 2;
	public static final long SAP_CONTRACT_ITEM_GROUPING_TYPE_ADVANCE_PAYMENT = 3;
	
	public static final String SAP_MATERIAL_NO_LANDFILL_PLANNING = "LOGON00001";
	public static final String SAP_MATERIAL_NO_AGG_PLANNING = "LOGON00002";
	
	/**
	 * Do not use these constants for any logic, always check the material itself!
	 */
	public static final Long PLANNING_LANDFILL_CONTRACT_ITEM_NUMBER = -2L;
	public static final Long PLANNING_AGGREGATE_CONTRACT_ITEM_NUMBER = -3L;

    public final static String MARKED_FOR_DELETION = "1";
    public final static String MARKED_FOR_DELETION_FALSE = "0";
    
    public static final long VEHICLE_LOGON_TYPE_TRUCK = 0;
    public static final long VEHICLE_LOGON_TYPE_TRAILER = 1;
    
    // Simple Vehicle
    public static final long SIMPLE_VEHICLE_SHIP_TYPE_CD = 1;
    public static final long SIMPLE_VEHICLE_BLOCK_TRAIN_TYPE_CD = 2;
    
    public static final long SIMPLE_VEHICLE_SHIP_PICKUP_CD = 1;
    public static final long SIMPLE_VEHICLE_BLOCKTRAIN_PICKUP_CD = 2;
    
    public static final long VEHICLE_CHARACTERISTIC_INDIVIDUAL = 0;
    public static final long VEHICLE_CHARACTERISTIC_GENERIC = 1;
    public static final long VEHICLE_CHARACTERISTIC_PLANNING = 2;
    
    //vehicle postprocessing vars
	public static final BigDecimal VP_MAX_NET_WEIGHT_CHAMBER1_T_SILO = new BigDecimal(28);
	public static final BigDecimal VP_MAX_NET_WEIGHT_CHAMBER1_T_OTHER = new BigDecimal(0);
	public static final BigDecimal VP_MAX_NET_WEIGHT_CHAMBER2 = new BigDecimal(0);
	
	//return order reason vars
	public static final String RETURN_ORDER_REASON_RAIL_UNLOADING_PROBLEM = "Z08";
	public static final String RETURN_ORDER_REASON_VOLUNTARY_RETURN = "Z11";
	
	//audit condig codes
	public static final Long AUDIT_BIZEVENT_CONFIG_ORDER_STATUS_CD = 1000L;
	public static final Long AUDIT_BIZEVENT_CONFIG_SHIPTO_SAP_BP_ID = 1001L;
	public static final Long AUDIT_BIZEVENT_CONFIG_SOLDTO_SAP_BP_ID = 1002L;
	public static final Long AUDIT_BIZEVENT_CONFIG_BILLTO_SAP_BP_ID = 1003L;
	public static final Long AUDIT_BIZEVENT_CONFIG_POOL_ID = 1004L;
	public static final Long AUDIT_BIZEVENT_CONFIG_COMM_CARRIER_ID = 1005L;
	public static final Long AUDIT_BIZEVENT_CONFIG_SHIPPINGPOINT_ID = 1006L;
	public static final Long AUDIT_BIZEVENT_CONFIG_DELIVERY_TYPE_CD = 1007L;
	public static final Long AUDIT_BIZEVENT_CONFIG_DELIVERY_FROM_DAT = 1008L;
	public static final Long AUDIT_BIZEVENT_CONFIG_DELIVERY_TO_DAT = 1009L;
	public static final Long AUDIT_BIZEVENT_CONFIG_RELEASE_SAP_FLAG = 1010L;
	public static final Long AUDIT_BIZEVENT_CONFIG_FREE_DELIVERY_FLAG = 1011L;
	public static final Long AUDIT_BIZEVENT_CONFIG_NO_PDA_OR_RAIL_EVENT_FLAG = 1012L;
	public static final Long AUDIT_BIZEVENT_CONFIG_NOTE_ORDER_MGMT = 1019L;
	public static final Long AUDIT_BIZEVENT_CONFIG_NOTE_DELIVERY = 1020L;
	public static final Long AUDIT_BIZEVENT_CONFIG_NOTE_DISPATCHING = 1021L;
	public static final Long AUDIT_BIZEVENT_CONFIG_NOTE_TRUCK_DRIVER = 1022L;
	public static final Long AUDIT_BIZEVENT_CONFIG_NOTE_RAILWAY_DISP = 1023L;
	public static final Long AUDIT_BIZEVENT_CONFIG_CARRIER_DELIVERY_NOTE = 1024L;
	public static final Long AUDIT_BIZEVENT_CONFIG_CARRIER_TRUCK_DRIVER_NOTE = 1025L;
	public static final Long AUDIT_BIZEVENT_CONFIG_CUST_DELIVERY_NOTE_COMMENT = 1026L;
	public static final Long AUDIT_BIZEVENT_CONFIG_ON_HOLD_ORDER_AND_LOCKED_FLAG = 1027L;
	
	public static final Long AUDIT_BIZEVENT_CONFIG_PRODUCTION_PLANT_CD = 1028L;
	public static final Long AUDIT_BIZEVENT_CONFIG_ORDERED_UOM_CD = 1029L;
	public static final Long AUDIT_BIZEVENT_CONFIG_ORDERED_QUANTITY = 1030L;
	public static final Long AUDIT_BIZEVENT_CONFIG_LOADED_UOM_CD = 1031L;
	public static final Long AUDIT_BIZEVENT_CONFIG_LOADED_QUANTITY = 1032L;
	public static final Long AUDIT_BIZEVENT_CONFIG_RETURNED_UOM_CD = 1033L;
	public static final Long AUDIT_BIZEVENT_CONFIG_RETURNED_QUANTITY = 1034L;
	public static final Long AUDIT_BIZEVENT_CONFIG_RETURNED_REASON_CD = 1044L;
	public static final Long AUDIT_BIZEVENT_CONFIG_DELIVERED_UOM_CD = 1035L;
	public static final Long AUDIT_BIZEVENT_CONFIG_DELIVERED_QUANTITY = 1036L;
	public static final Long AUDIT_BIZEVENT_CONFIG_LOADINGPOINT_ID = 1037L;
	public static final Long AUDIT_BIZEVENT_CONFIG_SHIPTO_SILO_ID = 1038L;
	
	public static final Long AUDIT_BIZEVENT_CONFIG_EXEC_CARRIER_ID = 1043L;
	
	public static final Long AUDIT_BIZEVENT_CONFIG_SPI = 1045L;
	public static final Long AUDIT_BIZEVENT_CONFIG_ITRACK_STATUS_CD = 1046L;

	
	// enable flag options
	public static final int ENABLED_FLAG_NO = 0;
	public static final int ENABLED_FLAG_YES = 1;
	
	// policy context
	public static final String POLICY_CONTEXT_REQUEST_KEY = "javax.servlet.http.HttpServletRequest";
	
	// session attribute keys
	public static final String USER_FILTER_USER_SESSION_ATTR_KEY		= "user_filter_user_session_attr_key";
	public static final String USER_FILTER_ROLE_SESSION_ATTR_KEY		= "user_filter_role_session_attr_key";
	public static final String USER_FILTER_GROUPS_SESSION_ATTR_KEY		= "user_filter_groups_session_attr_key";
	public static final String USER_FILTER_TIMEZONE_SESSION_ATTR_KEY	= "user_filter_timezone_session_attr_key";
	public static final String LOGON_LOGIN_TYPE_SESSION_ATTR_KEY		= "logon_login_type_session_attr_key";
	public static final String LOGON_LOGIN_IP_ADDRESS					= "logon_login_ip_address";

	// login types
	public static final String LOGIN_TYPE_LOGGED_OUT	= "login_type_logged_out";
	public static final String LOGIN_TYPE_INTERNAL	= "login_type_internal";
	public static final String LOGIN_TYPE_EXTERNAL	= "login_type_external";
	public static final String LOGIN_TYPE_TERMINAL	= "login_type_terminal";
	public static final String LOGIN_TYPE_PDA			= "login_type_pda";
	
	public static final Long WAGON_MAINT_ATTACHMENT_TYPE_EMAIL = 4L;
	
	public static final int MAX_ORDER_EXTRA_COST_COUNT = 4;
	
	public static final String ORDER_ITEM_NUM_DENOM_TYPE_ORDERED	= "orderItemOrderedNumeratorAndDenominator";
	public static final String ORDER_ITEM_NUM_DENOM_TYPE_LR 		= "orderItemLrNumeratorAndDenominator";
	
	public static final Long DEFAULT_SEQ_USAGE_CD = 0L;
	public static final Long CASH_SALES_SEQ_USAGE_CD = 1L;
	public static final Long EXPORT_INVOICE_SEQ_USAGE_CD = 2L;
	
	/**
	 * Chart constants
	 */
     // Delivery types
    public static final int CHART_DELIVERY_TYPE_DELIVERED = 1;
    public static final int CHART_DELIVERY_TYPE_PICKUP = 2;
    // Product types
    public static final int PRODUCT_TYPE_BAG = 1;
    public static final int PRODUCT_TYPE_BULK_LANDFILL = 2;
    public static final int PRODUCT_TYPE_BULK_AGG = 3;
    public static final int PRODUCT_TYPE_BULK_CEM = 4;
    // General bulk type
	public static final int PRODUCT_TYPE_BULK = 5;
    
    // Chart type
    public static final int CHART_TYPE_NR_OF_TRUCKS = 1;
    public static final int CHART_TYPE_NR_OF_LOADINGS = 2;
    
    public static final int DROP_DOWN_MAX_SIZE = 200;
    
    // transport category
    public static final long TRUCK_TRANSPORT_CAT_CD_BAG = 4;
    
    /**
     * Call Center Performance Order Modes
     */
    
    public static final String PERFORMANCE_ORDER_CALCULATE_QUANTITY 	= "performance_order_calculate_quantity";
    public static final String PERFORMANCE_ORDER_CALCULATE_DELIVERYTO	= "performance_order_calculate_deliveryto";
    
    /**
     * Customer Order and Customer Order Split
     */
    public static final String CUSTORDER_ORDERED_QTY_MAX_VALUE_STR = "999999.999";
    public static final String CUSTORDER_ORDERED_QTY_MIN_VALUE_STR = "0.001";
    public static final String CUSTORDER_QTY_PER_SDO_MAX_VALUE_STR = "999999.999";
    public static final String CUSTORDER_QTY_PER_SDO_MIN_VALUE_STR = "0.001";
    public static final String CUSTORDER_DELIVERY_TIME_TOLERANCE_MAX_VALUE_STR = "9999.99";
    public static final String CUSTORDER_DELIVERY_TIME_TOLERANCE_MIN_VALUE_STR = "0.01";
    public static final String CUSTORDER_DELIVERY_FQY_EVERY_TIME_MAX_VALUE_STR = "9999.99";
    public static final String CUSTORDER_DELIVERY_FQY_EVERY_TIME_MIN_VALUE_STR = "0.01";
    public static final String CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MAX_VALUE_STR = "9999.99";
    public static final String CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MIN_VALUE_STR = "0.01";
    public static final BigDecimal CUSTORDER_ORDERED_QTY_MAX_VALUE = new BigDecimal(CUSTORDER_ORDERED_QTY_MAX_VALUE_STR);
    public static final BigDecimal CUSTORDER_ORDERED_QTY_MIN_VALUE = new BigDecimal(CUSTORDER_ORDERED_QTY_MIN_VALUE_STR);
    public static final BigDecimal CUSTORDER_QTY_PER_SDO_MAX_VALUE = new BigDecimal(CUSTORDER_QTY_PER_SDO_MAX_VALUE_STR);
    public static final BigDecimal CUSTORDER_QTY_PER_SDO_MIN_VALUE = new BigDecimal(CUSTORDER_QTY_PER_SDO_MIN_VALUE_STR);
    public static final Double CUSTORDER_DELIVERY_TIME_TOLERANCE_MAX_VALUE = new Double(CUSTORDER_DELIVERY_TIME_TOLERANCE_MAX_VALUE_STR);
    public static final Double CUSTORDER_DELIVERY_TIME_TOLERANCE_MIN_VALUE = new Double(CUSTORDER_DELIVERY_TIME_TOLERANCE_MIN_VALUE_STR);
    public static final Double CUSTORDER_DELIVERY_FQY_EVERY_TIME_MAX_VALUE = new Double(CUSTORDER_DELIVERY_FQY_EVERY_TIME_MAX_VALUE_STR);
    public static final Double CUSTORDER_DELIVERY_FQY_EVERY_TIME_MIN_VALUE = new Double(CUSTORDER_DELIVERY_FQY_EVERY_TIME_MIN_VALUE_STR);
    public static final BigDecimal CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MAX_VALUE = new BigDecimal(CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MAX_VALUE_STR);
    public static final BigDecimal CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MIN_VALUE = new BigDecimal(CUSTORDER_DELIVERY_FQY_QTY_PER_HRE_MIN_VALUE_STR);

    // return material usage 
    public static final Long RETURN_MATERIAL_USAGE_REFILLING_VEHICLE_CD = 0L;
    public static final Long RETURN_MATERIAL_USAGE_MIXED_QUALITIES_CD = 1L;
    public static final Long RETURN_MATERIAL_USAGE_RUBISH_CD = 2L;
    
    // Names of generated PDF documents
	public static final String DOC_NAME_DELNOTE = "Delivery Note";
	public static final String DOC_NAME_EXPINV = "Export Invoice";
	public static final String DOC_NAME_CASHSALES = "Cash Sales";
	public static final String DOC_NAME_COLL_DELNOTE = "Collective Delivery Note";

    // Used at order detail. Defines the delivery types, where needs to be showed the carrier info 
    public static final Set<Long> ORD_DET_CARRIER_INFO_SET;    
    static {
    	ORD_DET_CARRIER_INFO_SET = new HashSet<Long>();
    	ORD_DET_CARRIER_INFO_SET.add(DELIVERY_TYPE_ROAD_DIRECT);
    	ORD_DET_CARRIER_INFO_SET.add(DELIVERY_TYPE_PICKUP);
    	ORD_DET_CARRIER_INFO_SET.add(DELIVERY_TYPE_BIMODAL_ROAD);
    	ORD_DET_CARRIER_INFO_SET.add(DELIVERY_TYPE_SHIP);
    	ORD_DET_CARRIER_INFO_SET.add(DELIVERY_TYPE_BLOCK_TRAIN);
    }
    
    // Used on saving loading and removing user preferences. 
    public static final String USER_PREFERENCE_SCREEN_ORDER_SEARCH = "SEARCH";
    public static final String USER_PREFERENCE_SCREEN_MONITORING_LIST = "MONITORING";
    public static final String USER_PREFERENCE_SCREEN_EXTERNAL_ORDER_SEARCH = "EXTSEARCH";
    public static final String USER_PREFERENCE_SCREEN_TRUCK_SCHEDULING = "DISPO";
    public static final String USER_PREFERENCE_SCREEN_RAIL_SCHEDULING = "RAILDISPO";
    public static final String USER_PREFERENCE_SCREEN_ORDER_SHIPTO = "ORDER_SHIPTO";

    /*
     * Google Maps Distance Matrix API - Response status code (Top-level Status Code)
     * (See: https://developers.google.com/maps/documentation/distancematrix/?hl=en#StatusCodes)
     */
    public static final String GMAPS_DISTANCE_MATRIX_RESPONSE_STATUS_OK = "OK";
    
	public static final String GMAPS_API_CRH_PRIVATE_KEY = "AIzaSyDARjL-LS6OvKXeGGz0k5t-QdvyUL5DgFM";
	
	public static final int TRUCK_ROUTE_DATA_EXPIRES_AFTER_DAYS = 30;
	
	public static final String CASH_SALES_INVOICE_MATERIAL_HEADER_KEY = "pdf.delivnote.cashsales.materialheader";
	public static final String EXPORT_INVOICE_TOTAL_TEXT_KEY = "pdf.delivnote.export.totaltext";
	public static final String EXPORT_INVOICE_LEGAL_TEXT_1_KEY = "pdf.delivnote.export.legaltext1";
	public static final String EXPORT_INVOICE_LEGAL_TEXT_2_KEY = "pdf.delivnote.export.legaltext2";
	/** 
	 * This constant field defines the number of characters which fits into the space for advance invoice number.
	 */
	public static final int EXP_INV_ADVANCE_INVOICE_NUMBER_MAX_WIDTH = 59;
	
	public static final BigDecimal BASE_CURRENCY_EXCHANGE_RATE = BigDecimal.valueOf(1);
	
	public static final List<Long> bagPackageTypeCds = Arrays.asList
			(Constant.PACKAGE_TYPE_BBAG);

	public static final List<String> bagOrderMaterialPackageCds = Arrays.asList
		(
			Constant.MATERIAL_PACKAGE_BAG,
			Constant.MATERIAL_PACKAGE_PALET_EMPTY
		);

	/**
	 * Bulk orders can be identified by the material package
	 */
	public static final List<String> bulkOrderMaterialPackageCds = Arrays.asList
		(
			Constant.MATERIAL_PACKAGE_BULK_DUMPED, 
			Constant.MATERIAL_PACKAGE_BULK_POWDER
		);
	
	public static final List<Long> bulkPakcageTypeCds = Arrays.asList
		(
			Constant.PACKAGE_TYPE_BULK_DUMPED, 
			Constant.PACKAGE_TYPE_BULK_POWDER
		);

	public static final String DEFAULT_LANGUAGE = "en";

	public static final Double DEFAULT_COORD_LON = 0D;
	public static final Double DEFAULT_COORD_LAT = 0D;

	// TODO lajos/15082013: please double check with BA whether this restriction is really needed or not!
//    /**
//     * Allowed incoterms of contracts for materials used in the Terminal Application
//     */
//	public static List<String> allowedTerminalApplicationIncoterms = Arrays.asList
//		(
//			Constant.INCOTERMS_EXW
//		);
	
	public static List<Long> deliveryTypesAvailableInTerminal = Arrays.asList
		(
			Constant.DELIVERY_TYPE_PICKUP,
			Constant.DELIVERY_TYPE_RETURN_PICKUP
		);
	
	/**
	 * Hardcoded map of special fixed rail loading quantity for certain SAP materials
	 * according to LOGON-4893.
	 * @return A map whose keys are SAP material no.s and whose values are the
	 * loading quantity to be sent to SBB for the respective material.
	 */
	private static final Map<String, BigDecimal> initialSpecialMaterialQtyForSbbMap()
	{
		final Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		
		// Hardcoded SAP material IDs according to LOGON-4893
		//final String LEERWAGEN_MIT_PALETTEN_P01 = "1903246";
		//final String LEERWAGEN_MIT_PALETTEN_QR5 = "60001714";
		final String LEERWAGEN_MIT_PALETTEN_PR1 = "60001563";
		
		// Add here all the materials with their special weight
		map.put(LEERWAGEN_MIT_PALETTEN_PR1, BigDecimal.valueOf(1L) /* one ton */);
		
		return Collections.unmodifiableMap(new HashMap<String, BigDecimal>());
	}

	public static final long _23_HOURS_IN_MINUTES = 1380L;

	public static final long _5_HOURS_IN_MINUTES = 300L;

	public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy.MM.dd";

	public static final boolean ON = true;

	public static final boolean OFF = false;

	public static final String NO_DATA = "";
	
	public static final String N_A = "N/A";

	public static final Boolean NO = false;

	public static final Boolean YES = true;

	public static final boolean CONTAINS = true;

	public static final boolean DOES_NOT_CONTAIN = false;

	public static final Object NOT_SPECIFIED = null;
	
	public static final int SMS_MAX_LENGTH_IN_CHARS = 160;

	public static final List<Locale> AVAILABLE_LOCALES = Arrays.asList(
		Locale.GERMAN, Locale.ENGLISH, Locale.FRENCH, Locale.ITALIAN,
		new Locale(LANGUAGE_CODE_SPANISH),
		new Locale(LANGUAGE_CODE_SLOVAK),
		new Locale(LANGUAGE_CODE_HUNGARIAN),
		new Locale(LANGUAGE_CODE_ROMANIAN),
		new Locale(LANGUAGE_CODE_BULGARIAN),
		new Locale(LANGUAGE_CODE_CROATIAN),
		new Locale(LANGUAGE_CODE_SERBIAN),
		new Locale(LANGUAGE_CODE_RUSSIAN),
		new Locale(LANGUAGE_CODE_POLISH),
		new Locale(LANGUAGE_CODE_AZERBAIJANI)
	);

	public static final int TOTAL_DISTRIBUTION_DESTINATION_CODES_IN_LOGON = 9;

	public static final String NEW_LINE = System.getProperty("line.separator");
	
	//LOGON-9267: LRQ 090.1 Refine archiving flag subject
	public static final Long ARCHIVING_STATUS_NEW = 1L;
	public static final Long ARCHIVING_STATUS_ARCHIVING_FAILED = 2L;
	public static final Long ARCHIVING_STATUS_DELETABLE = 3L;
	public static final Long ARCHIVING_STATUS_DELETION_FAILED = 4L;
	
	public static final String TIMEZONE_EUROPE_PARIS = "Europe/Paris";

	public static final String EVENT_STARTLOADING_FR = "Début de chargement";
}

