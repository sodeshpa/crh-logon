-- Purpose of this file:
-- - Mainly for fake testdata!
--
-- Not for those:
-- - Initial Data like users, roles, config, ... should rather be in refdata -> loaded also in Crh
-- - Initial Data specified by Crh should also rather be in refdata -> loaded also in Crh
-- - Large sets of testdata should be moved into its own file in testdata (see BP, material, etc.)

-- to prevent errors with the ampersand
set define off
-- Default Logon NLS settings
@set_logon_session_settings.sql

-- Note: On purpose negative IDs for most of the test-objects so we are out of the region of normal data (possibly created by using sequences).
-- Also, when data is well managed, this set of testdata could be loaded onto any database, never interfering with real data.
-- Do NOT use '-1', as this is reserved in the application for "empty selection" in Dropdowns etc. => Start with -11

-- Additional Roles we want to have @AdNovum: Not needed anymore (see initialdata/tlgnu_role.sql)

-- Carriers depend on SAP_Vendor! -> tlgns_sap_vendor.sql!
prompt INSERTING into TLGNC_CARRIER
Insert into TLGNC_CARRIER (CARRIER_ID, DISPLAY_NAME, ACTIVE_FLAG, SAP_VENDOR_ID, transmit_to_carrier_flag,CARRIER_REQUIRES_PLANNING_FLAG) values (-11, 'carrier -11', 1, -11, 0, 0);
Insert into TLGNC_CARRIER (CARRIER_ID, DISPLAY_NAME, ACTIVE_FLAG, SAP_VENDOR_ID, transmit_to_carrier_flag,CARRIER_REQUIRES_PLANNING_FLAG) values (-12, 'carrier -12', 1, -12, 0, 0);
Insert into TLGNC_CARRIER (CARRIER_ID, DISPLAY_NAME, ACTIVE_FLAG, SAP_VENDOR_ID, transmit_to_carrier_flag,CARRIER_REQUIRES_PLANNING_FLAG) values (-13, 'carrier -13', 1, -13, 0, 0);
-- An old carrier, based on same vendor, but marked as inactive. Should normally not appear! This is on purpose, to test if the application can handle this special 1:N case due to active/inactive. (2009 - 2012)
-- On October 2012, stich/uma decided that this is crap, it does not help anybody, it only caused so many problems and bogus error reports in the past.
-- Fact is: tlgnc_carrier are *always* created based on SAP import, and also *reactivation* is implemented. This 1:N does simply not happen in reality.
-- -- Inactivated Oct 2012: Insert into TLGNC_CARRIER (CARRIER_ID, DISPLAY_NAME, ACTIVE_FLAG, SAP_VENDOR_ID, transmit_to_carrier_flag,CARRIER_REQUIRES_PLANNING_FLAG) values (-14, 'carrier -14', 0, -11, 0, 0);

-- Persons, Users and Roles: See tlgnu_person/user/user_role in testdata and initialdata.
-- Drivers: tlgnc_driver (testdata and initialdata)

prompt INSERTING into TLGNC_POOL
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-11, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-12, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-13, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-14, '10', 1, 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-11, 'de', 'Pool Eins'       , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-12, 'de', 'Pool Zwei'       , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-13, 'de', 'Pool Drei'       , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-14, 'de', 'Pool Vier'       , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-11, 'en', 'Pool One'        , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-12, 'en', 'Pool Two'        , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-13, 'en', 'Pool Three'      , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-14, 'en', 'Pool Four'       , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-11, 'fr', 'Pool One (fr)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-12, 'fr', 'Pool Two (fr)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-13, 'fr', 'Pool Three (fr)' , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-14, 'fr', 'Pool Four (fr)'  , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-11, 'it', 'Pool One (it)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-12, 'it', 'Pool Two (it)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-13, 'it', 'Pool Three (it)' , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-14, 'it', 'Pool Four (it)'  , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-11, 'es', 'Pool One (es)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-12, 'es', 'Pool Two (es)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-13, 'es', 'Pool Three (es)' , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-14, 'es', 'Pool Four (es)'  , 1, systimestamp, 1, systimestamp);

insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-11, -11, 'AD');
insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-12, -12, 'ZA');

insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-13, -13, 'AD');
insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-15, -13, 'ZA');
insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-14, -14, 'AD');
insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-16, -14, 'ZA');

prompt INSERTING into TLGNC_POOL_CARRIER
Insert into TLGNC_POOL_CARRIER (POOL_CARRIER_ID,POOL_ID,CARRIER_ID) values (-11, -11,-11);
Insert into TLGNC_POOL_CARRIER (POOL_CARRIER_ID,POOL_ID,CARRIER_ID) values (-12, -12,-12);
Insert into TLGNC_POOL_CARRIER (POOL_CARRIER_ID,POOL_ID,CARRIER_ID) values (-13, -13,-11);
Insert into TLGNC_POOL_CARRIER (POOL_CARRIER_ID,POOL_ID,CARRIER_ID) values (-14, -13,-12);

prompt INSERTING into TLGNC_RFIDOBJECT
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL)
	VALUES (-100, 4 , 'ZH01');  
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL)
	VALUES (-101, 4 , 'ZH02');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL)
	VALUES (-102, 4 , 'ZH03');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL)
	VALUES (-103, 4 , 'ZH04');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL)
	VALUES (-104, 4 , 'ZH05');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL)
	VALUES (-105, 4 , 'ZH06');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL)
	VALUES (-110, 5 , 'ZH101');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL)
	VALUES (-111, 5 , 'ZH102');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL)
	VALUES (-112, 5 , 'ZH103');
-- LOGON-5806 Additional trucks/trailers for carrier-12
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL) VALUES (-201, 4 , 'mixed1');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL) VALUES (-202, 4 , 'tractorPow1');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL) VALUES (-203, 4 , 'truckMix1');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL) VALUES (-204, 4 , 'tractorDump1');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL) VALUES (-205, 4 , 'tractorCon1');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL) VALUES (-206, 4 , 'bulkpow1');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL) VALUES (-207, 4 , 'bulkdump1');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL) VALUES (-208, 5 , 'conc');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL) VALUES (-209, 5 , 'dump');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL) VALUES (-210, 5 , 'mixgood');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL) VALUES (-211, 5 , 'Powder');
-- LOGON-5695 Need unlinked Truck/Trailer to test CreateCompletedCustomerOrder
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL) VALUES (-212, 4 , 'tractorAlone');
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL) VALUES (-213, 5 , 'trailerAlone');
	

prompt INSERTING into TLGNC_TRUCK
Insert into TLGNC_TRUCK (TRUCK_ID,CARRIER_ID,PRIORITY,TRUCK_NAME,TRUCK_PLATE_NO,TRUCK_TRANSPORT_CAT_CD,TRUCK_TYPE_CD,ACTIVE_FLAG,TRUCK_COMMENT,PDA_AVAILABLE_FLAG,HOMEBASE_COORD_LAT,HOMEBASE_COORD_LON,TARE_WEIGHT_T,MAX_GROSS_WEIGHT_T,RFIDOBJECT_ID, NR_CHAMBERS, MAX_NET_WEIGHT_CHAMBER1_T, MAX_NET_WEIGHT_CHAMBER2_T, SAP_VEHICLE_CHAR_CD) values
  (-100,-11,99,'Truck 1 (C1)','ZH01',1,1,1,'Simple Silo Truck',1,null,null, 10 /* tare */ , 42 /* gross */,-100, 1, 27 /* net 1 */, 0, 0);
Insert into TLGNC_TRUCK (TRUCK_ID,CARRIER_ID,PRIORITY,TRUCK_NAME,TRUCK_PLATE_NO,TRUCK_TRANSPORT_CAT_CD,TRUCK_TYPE_CD,ACTIVE_FLAG,TRUCK_COMMENT,PDA_AVAILABLE_FLAG,HOMEBASE_COORD_LAT,HOMEBASE_COORD_LON,TARE_WEIGHT_T,MAX_GROSS_WEIGHT_T,RFIDOBJECT_ID, NR_CHAMBERS, MAX_NET_WEIGHT_CHAMBER1_T, MAX_NET_WEIGHT_CHAMBER2_T, SAP_VEHICLE_CHAR_CD) values
  (-101,-11,99,'Truck 2 (C1)','ZH02',3,5,1,'Truck with Silo Trailer',1,null,null, 10, 42,-101, 1, 27 /* net 1 */, 0, 0);
Insert into TLGNC_TRUCK (TRUCK_ID,CARRIER_ID,PRIORITY,TRUCK_NAME,TRUCK_PLATE_NO,TRUCK_TRANSPORT_CAT_CD,TRUCK_TYPE_CD,ACTIVE_FLAG,TRUCK_COMMENT,PDA_AVAILABLE_FLAG,HOMEBASE_COORD_LAT,HOMEBASE_COORD_LON,TARE_WEIGHT_T,MAX_GROSS_WEIGHT_T,RFIDOBJECT_ID, NR_CHAMBERS, MAX_NET_WEIGHT_CHAMBER1_T, MAX_NET_WEIGHT_CHAMBER2_T, SAP_VEHICLE_CHAR_CD) values
  (-102,-11,99,'Truck 3 (C1)','ZH03',3,5,1,'Truck with Silo Trailer',1,null,null, 10, 42,-102, 1, 27 /* net 1 */, 0, 0);
Insert into TLGNC_TRUCK (TRUCK_ID,CARRIER_ID,PRIORITY,TRUCK_NAME,TRUCK_PLATE_NO,TRUCK_TRANSPORT_CAT_CD,TRUCK_TYPE_CD,ACTIVE_FLAG,TRUCK_COMMENT,PDA_AVAILABLE_FLAG,HOMEBASE_COORD_LAT,HOMEBASE_COORD_LON,TARE_WEIGHT_T,MAX_GROSS_WEIGHT_T,RFIDOBJECT_ID, NR_CHAMBERS, MAX_NET_WEIGHT_CHAMBER1_T, MAX_NET_WEIGHT_CHAMBER2_T, SAP_VEHICLE_CHAR_CD) values
  (-201,-12,99,'Truck 2 (C2)','ZH04',3,5,1,'Truck with Silo Trailer',1,null,null, 10, 42,-103, 1, 27 /* net 1 */, 0, 0);
Insert into TLGNC_TRUCK (TRUCK_ID,CARRIER_ID,PRIORITY,TRUCK_NAME,TRUCK_PLATE_NO,TRUCK_TRANSPORT_CAT_CD,TRUCK_TYPE_CD,ACTIVE_FLAG,TRUCK_COMMENT,PDA_AVAILABLE_FLAG,HOMEBASE_COORD_LAT,HOMEBASE_COORD_LON,TARE_WEIGHT_T,MAX_GROSS_WEIGHT_T,RFIDOBJECT_ID, NR_CHAMBERS, MAX_NET_WEIGHT_CHAMBER1_T, MAX_NET_WEIGHT_CHAMBER2_T, SAP_VEHICLE_CHAR_CD) values
  (-200,-12,99,'Truck 1 (C2)','ZH05',1,1,1,'b',1,null,null, 10, 42,-104, 1, 27, 0, 0);
Insert into TLGNC_TRUCK (TRUCK_ID,CARRIER_ID,PRIORITY,TRUCK_NAME,TRUCK_PLATE_NO,TRUCK_TRANSPORT_CAT_CD,TRUCK_TYPE_CD,ACTIVE_FLAG,TRUCK_COMMENT,PDA_AVAILABLE_FLAG,HOMEBASE_COORD_LAT,HOMEBASE_COORD_LON,TARE_WEIGHT_T,MAX_GROSS_WEIGHT_T,RFIDOBJECT_ID, NR_CHAMBERS, MAX_NET_WEIGHT_CHAMBER1_T, MAX_NET_WEIGHT_CHAMBER2_T, SAP_VEHICLE_CHAR_CD) values
  (-202,-12,99,'Truck 3 (C2)','ZH06',1,2,1,'b',1,null,null, 10, 42,-105, 1, 27, 0, 0);
-- LOGON-5806 Additional vehicles/trucks/trailers for carrier-12
Insert into TLGNS_SAP_VEHICLE (SAP_VEHICLE_ID, SAP_VEHICLE_NO, SAP_VEHICLE_IFC_NO, SAP_VEHICLE_ACTIVE_FLAG, SAP_VEHICLE_NAME, SAP_VENDOR_ID, SAP_VEHICLE_PLATE_NO, SAP_VEHICLE_TYPE_CD, SAP_VEHICLE_AXIS_COUNT, SAP_VEHICLE_MAX_GROSS_WEIGHT_T, SAP_VEHCILE_TARE_WEIGHT_T, SAP_VEHICLE_VOLUME_M3, SAP_VEHICLE_TRAILER_EQP_NO, SAP_VEHICLE_TRUCK_EQP_NO, SAP_VEHICLE_CHAR_CD, SAP_VEHICLE_IS_INTERNAL_FLAG, COMPANYCODE_CD, SAP_VEHICLE_IS_PROCESSED_FLAG, SAP_VEHICLE_IS_IMPORTED_FLAG, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values (-500, 'bulkdump1sap', 'bulkdump1sap', 1, 'Bulk Dumped', -12, 'bulkdump1sap', 'Z002', '02', 44, 9.95, 0, null, null, 1, 1, 'AD02', 1, 1, 1, sysdate, 1, sysdate, 1);
Insert into TLGNS_SAP_VEHICLE (SAP_VEHICLE_ID, SAP_VEHICLE_NO, SAP_VEHICLE_IFC_NO, SAP_VEHICLE_ACTIVE_FLAG, SAP_VEHICLE_NAME, SAP_VENDOR_ID, SAP_VEHICLE_PLATE_NO, SAP_VEHICLE_TYPE_CD, SAP_VEHICLE_AXIS_COUNT, SAP_VEHICLE_MAX_GROSS_WEIGHT_T, SAP_VEHCILE_TARE_WEIGHT_T, SAP_VEHICLE_VOLUME_M3, SAP_VEHICLE_TRAILER_EQP_NO, SAP_VEHICLE_TRUCK_EQP_NO, SAP_VEHICLE_CHAR_CD, SAP_VEHICLE_IS_INTERNAL_FLAG, COMPANYCODE_CD, SAP_VEHICLE_IS_PROCESSED_FLAG, SAP_VEHICLE_IS_IMPORTED_FLAG, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values (-501, 'bulkpow1Sap', 'bulkpow1Sap', 1, 'Bulk Powder', -12, 'bulkpow1Sap', 'Z002', '02', 42, 12.01, 0, null, null, 1, 1, 'AD02', 1, 1, 1, sysdate, 1, sysdate, 1);
Insert into TLGNS_SAP_VEHICLE (SAP_VEHICLE_ID, SAP_VEHICLE_NO, SAP_VEHICLE_IFC_NO, SAP_VEHICLE_ACTIVE_FLAG, SAP_VEHICLE_NAME, SAP_VENDOR_ID, SAP_VEHICLE_PLATE_NO, SAP_VEHICLE_TYPE_CD, SAP_VEHICLE_AXIS_COUNT, SAP_VEHICLE_MAX_GROSS_WEIGHT_T, SAP_VEHCILE_TARE_WEIGHT_T, SAP_VEHICLE_VOLUME_M3, SAP_VEHICLE_TRAILER_EQP_NO, SAP_VEHICLE_TRUCK_EQP_NO, SAP_VEHICLE_CHAR_CD, SAP_VEHICLE_IS_INTERNAL_FLAG, COMPANYCODE_CD, SAP_VEHICLE_IS_PROCESSED_FLAG, SAP_VEHICLE_IS_IMPORTED_FLAG, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values (-502, 'mixed1Sap', 'mixed1Sap', 1, 'MixedGoods', -12, 'mixed1Sap', 'Z002', '02', 42, 8.4, 0, null, null, 1, 1, 'AD02', 1, 1, 1, sysdate, 1, sysdate, 1);
Insert into TLGNS_SAP_VEHICLE (SAP_VEHICLE_ID, SAP_VEHICLE_NO, SAP_VEHICLE_IFC_NO, SAP_VEHICLE_ACTIVE_FLAG, SAP_VEHICLE_NAME, SAP_VENDOR_ID, SAP_VEHICLE_PLATE_NO, SAP_VEHICLE_TYPE_CD, SAP_VEHICLE_AXIS_COUNT, SAP_VEHICLE_MAX_GROSS_WEIGHT_T, SAP_VEHCILE_TARE_WEIGHT_T, SAP_VEHICLE_VOLUME_M3, SAP_VEHICLE_TRAILER_EQP_NO, SAP_VEHICLE_TRUCK_EQP_NO, SAP_VEHICLE_CHAR_CD, SAP_VEHICLE_IS_INTERNAL_FLAG, COMPANYCODE_CD, SAP_VEHICLE_IS_PROCESSED_FLAG, SAP_VEHICLE_IS_IMPORTED_FLAG, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values (-503, 'tractorZ002', 'tractorZ002', 1, 'Tractor (Z002)', -12, 'tractorZ002', 'Z002', '02', 42, 11, 0, null, null, 1, 1, 'AD02', 1, 1, 1, sysdate, 1, sysdate, 1);

Insert into TLGNC_TRUCK (TRUCK_ID,CARRIER_ID,DISPATCHING_CARRIER_ID,ACTIVE_FLAG,PRIORITY,TRUCK_NAME,TRUCK_PLATE_NO,SAP_VEHICLE_NO,TRUCK_TRANSPORT_CAT_CD,TRUCK_TYPE_CD,MANUFACTURER,AXIS_COUNT,EURO_NORM,MAX_GROSS_WEIGHT_T,TARE_WEIGHT_T,HAS_DUSTFILTER_FLAG,VOLUME_M3,BRANDING_FLAG,NR_CHAMBERS,MAX_NET_WEIGHT_CHAMBER1_T,MAX_NET_WEIGHT_CHAMBER2_T,SAP_VEHICLE_IS_INTERNAL_FLAG,SAP_VEHICLE_CHAR_CD,SAP_VEHICLE_TYPE_CD,TRUCK_COMMENT,PDA_AVAILABLE_FLAG,SMS_SEND_FLAG,HOMEBASE_COORD_LAT,HOMEBASE_COORD_LON,RETURN_SHIPPINGPOINT_ID,RFIDOBJECT_ID) values (-500,-12,null,1,99,'Bulk Dumped','bulkdump1','bulkdump1sap',5,95,null,'02',null,44,9.95,null,0,null,0,0,0,1,1,'Z002','AdN TestData, please don''t change.',0,null,null,null,null,-207);
Insert into TLGNC_TRUCK (TRUCK_ID,CARRIER_ID,DISPATCHING_CARRIER_ID,ACTIVE_FLAG,PRIORITY,TRUCK_NAME,TRUCK_PLATE_NO,SAP_VEHICLE_NO,TRUCK_TRANSPORT_CAT_CD,TRUCK_TYPE_CD,MANUFACTURER,AXIS_COUNT,EURO_NORM,MAX_GROSS_WEIGHT_T,TARE_WEIGHT_T,HAS_DUSTFILTER_FLAG,VOLUME_M3,BRANDING_FLAG,NR_CHAMBERS,MAX_NET_WEIGHT_CHAMBER1_T,MAX_NET_WEIGHT_CHAMBER2_T,SAP_VEHICLE_IS_INTERNAL_FLAG,SAP_VEHICLE_CHAR_CD,SAP_VEHICLE_TYPE_CD,TRUCK_COMMENT,PDA_AVAILABLE_FLAG,SMS_SEND_FLAG,HOMEBASE_COORD_LAT,HOMEBASE_COORD_LON,RETURN_SHIPPINGPOINT_ID,RFIDOBJECT_ID) values (-501,-12,null,1,99,'Bulk Powder','bulkpow1','bulkpow1Sap',1,87,null,'02',null,42,12.01,null,0,null,0,0,0,1,1,'Z002','AdN TestData, please don''t change.',0,null,null,null,null,-206);
Insert into TLGNC_TRUCK (TRUCK_ID,CARRIER_ID,DISPATCHING_CARRIER_ID,ACTIVE_FLAG,PRIORITY,TRUCK_NAME,TRUCK_PLATE_NO,SAP_VEHICLE_NO,TRUCK_TRANSPORT_CAT_CD,TRUCK_TYPE_CD,MANUFACTURER,AXIS_COUNT,EURO_NORM,MAX_GROSS_WEIGHT_T,TARE_WEIGHT_T,HAS_DUSTFILTER_FLAG,VOLUME_M3,BRANDING_FLAG,NR_CHAMBERS,MAX_NET_WEIGHT_CHAMBER1_T,MAX_NET_WEIGHT_CHAMBER2_T,SAP_VEHICLE_IS_INTERNAL_FLAG,SAP_VEHICLE_CHAR_CD,SAP_VEHICLE_TYPE_CD,TRUCK_COMMENT,PDA_AVAILABLE_FLAG,SMS_SEND_FLAG,HOMEBASE_COORD_LAT,HOMEBASE_COORD_LON,RETURN_SHIPPINGPOINT_ID,RFIDOBJECT_ID) values (-502,-12,null,1,99,'MixedGoods','mixed1','mixed1Sap',4,79,null,'02',null,42,8.4,null,0,null,0,0,0,1,1,'Z002','AdN TestData, please don''t change.',0,null,null,null,null,-201);
Insert into TLGNC_TRUCK (TRUCK_ID,CARRIER_ID,DISPATCHING_CARRIER_ID,ACTIVE_FLAG,PRIORITY,TRUCK_NAME,TRUCK_PLATE_NO,SAP_VEHICLE_NO,TRUCK_TRANSPORT_CAT_CD,TRUCK_TYPE_CD,MANUFACTURER,AXIS_COUNT,EURO_NORM,MAX_GROSS_WEIGHT_T,TARE_WEIGHT_T,HAS_DUSTFILTER_FLAG,VOLUME_M3,BRANDING_FLAG,NR_CHAMBERS,MAX_NET_WEIGHT_CHAMBER1_T,MAX_NET_WEIGHT_CHAMBER2_T,SAP_VEHICLE_IS_INTERNAL_FLAG,SAP_VEHICLE_CHAR_CD,SAP_VEHICLE_TYPE_CD,TRUCK_COMMENT,PDA_AVAILABLE_FLAG,SMS_SEND_FLAG,HOMEBASE_COORD_LAT,HOMEBASE_COORD_LON,RETURN_SHIPPINGPOINT_ID,RFIDOBJECT_ID) values (-503,-12,null,1,99,'Tractor (w. Concrete Tr)','tractorCon1','tractorZ002',3,97,null,'02',null,42,11,null,null,null,0,0,0,1,1,'Z002','AdN TestData Truck&Trailer Composition, please don''t change.',0,null,null,null,null,-205);
Insert into TLGNC_TRUCK (TRUCK_ID,CARRIER_ID,DISPATCHING_CARRIER_ID,ACTIVE_FLAG,PRIORITY,TRUCK_NAME,TRUCK_PLATE_NO,SAP_VEHICLE_NO,TRUCK_TRANSPORT_CAT_CD,TRUCK_TYPE_CD,MANUFACTURER,AXIS_COUNT,EURO_NORM,MAX_GROSS_WEIGHT_T,TARE_WEIGHT_T,HAS_DUSTFILTER_FLAG,VOLUME_M3,BRANDING_FLAG,NR_CHAMBERS,MAX_NET_WEIGHT_CHAMBER1_T,MAX_NET_WEIGHT_CHAMBER2_T,SAP_VEHICLE_IS_INTERNAL_FLAG,SAP_VEHICLE_CHAR_CD,SAP_VEHICLE_TYPE_CD,TRUCK_COMMENT,PDA_AVAILABLE_FLAG,SMS_SEND_FLAG,HOMEBASE_COORD_LAT,HOMEBASE_COORD_LON,RETURN_SHIPPINGPOINT_ID,RFIDOBJECT_ID) values (-504,-12,null,1,99,'Tractor (w. Dumpable Tr)','tractorDump1','tractorZ002',3,97,null,'02',null,42,11,null,null,null,0,0,0,1,1,'Z002','AdN TestData Truck&Trailer Composition, please don''t change.',0,null,null,null,null,-204);
Insert into TLGNC_TRUCK (TRUCK_ID,CARRIER_ID,DISPATCHING_CARRIER_ID,ACTIVE_FLAG,PRIORITY,TRUCK_NAME,TRUCK_PLATE_NO,SAP_VEHICLE_NO,TRUCK_TRANSPORT_CAT_CD,TRUCK_TYPE_CD,MANUFACTURER,AXIS_COUNT,EURO_NORM,MAX_GROSS_WEIGHT_T,TARE_WEIGHT_T,HAS_DUSTFILTER_FLAG,VOLUME_M3,BRANDING_FLAG,NR_CHAMBERS,MAX_NET_WEIGHT_CHAMBER1_T,MAX_NET_WEIGHT_CHAMBER2_T,SAP_VEHICLE_IS_INTERNAL_FLAG,SAP_VEHICLE_CHAR_CD,SAP_VEHICLE_TYPE_CD,TRUCK_COMMENT,PDA_AVAILABLE_FLAG,SMS_SEND_FLAG,HOMEBASE_COORD_LAT,HOMEBASE_COORD_LON,RETURN_SHIPPINGPOINT_ID,RFIDOBJECT_ID) values (-505,-12,null,1,99,'Tractor (w. MixedGoods Tr)','truckMix1','tractorZ002',3,97,null,'02',null,42,11,null,null,null,0,0,0,1,1,'Z002','AdN TestData Truck&Trailer Composition, please don''t change.',0,null,null,null,null,-203);
Insert into TLGNC_TRUCK (TRUCK_ID,CARRIER_ID,DISPATCHING_CARRIER_ID,ACTIVE_FLAG,PRIORITY,TRUCK_NAME,TRUCK_PLATE_NO,SAP_VEHICLE_NO,TRUCK_TRANSPORT_CAT_CD,TRUCK_TYPE_CD,MANUFACTURER,AXIS_COUNT,EURO_NORM,MAX_GROSS_WEIGHT_T,TARE_WEIGHT_T,HAS_DUSTFILTER_FLAG,VOLUME_M3,BRANDING_FLAG,NR_CHAMBERS,MAX_NET_WEIGHT_CHAMBER1_T,MAX_NET_WEIGHT_CHAMBER2_T,SAP_VEHICLE_IS_INTERNAL_FLAG,SAP_VEHICLE_CHAR_CD,SAP_VEHICLE_TYPE_CD,TRUCK_COMMENT,PDA_AVAILABLE_FLAG,SMS_SEND_FLAG,HOMEBASE_COORD_LAT,HOMEBASE_COORD_LON,RETURN_SHIPPINGPOINT_ID,RFIDOBJECT_ID) values (-506,-12,null,1,99,'Tractor (w. Powder Tr)','tractorPow1','tractorZ002',3,97,null,'02',null,42,11,null,null,null,0,0,0,1,1,'Z002','AdN TestData Truck&Trailer Composition, please don''t change.',0,null,null,null,null,-202);
-- LOGON-5695 Need unlinked Truck/Trailer to test CreateCompletedCustomerOrder
Insert into TLGNS_SAP_VEHICLE (SAP_VEHICLE_ID, SAP_VEHICLE_NO, SAP_VEHICLE_IFC_NO, SAP_VEHICLE_ACTIVE_FLAG, SAP_VEHICLE_NAME, SAP_VENDOR_ID, SAP_VEHICLE_PLATE_NO, SAP_VEHICLE_TYPE_CD, SAP_VEHICLE_AXIS_COUNT, SAP_VEHICLE_MAX_GROSS_WEIGHT_T, SAP_VEHCILE_TARE_WEIGHT_T, SAP_VEHICLE_VOLUME_M3, SAP_VEHICLE_TRAILER_EQP_NO, SAP_VEHICLE_TRUCK_EQP_NO, SAP_VEHICLE_CHAR_CD, SAP_VEHICLE_IS_INTERNAL_FLAG, COMPANYCODE_CD, SAP_VEHICLE_IS_PROCESSED_FLAG, SAP_VEHICLE_IS_IMPORTED_FLAG, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values (-507, 'tractorZ019', 'tractorZ019', 1, 'Tractor (Z019)', -12, 'tractorZ019', 'Z019', '02', 42, 11, 0, null, null, 1, 1, 'AD02', 1, 1, 1, sysdate, 1, sysdate, 1);

Insert into TLGNC_TRUCK (TRUCK_ID,CARRIER_ID,DISPATCHING_CARRIER_ID,ACTIVE_FLAG,PRIORITY,TRUCK_NAME,TRUCK_PLATE_NO,SAP_VEHICLE_NO,TRUCK_TRANSPORT_CAT_CD,TRUCK_TYPE_CD,MANUFACTURER,AXIS_COUNT,EURO_NORM,MAX_GROSS_WEIGHT_T,TARE_WEIGHT_T,HAS_DUSTFILTER_FLAG,VOLUME_M3,BRANDING_FLAG,NR_CHAMBERS,MAX_NET_WEIGHT_CHAMBER1_T,MAX_NET_WEIGHT_CHAMBER2_T,SAP_VEHICLE_IS_INTERNAL_FLAG,SAP_VEHICLE_CHAR_CD,SAP_VEHICLE_TYPE_CD,TRUCK_COMMENT,PDA_AVAILABLE_FLAG,SMS_SEND_FLAG,HOMEBASE_COORD_LAT,HOMEBASE_COORD_LON,RETURN_SHIPPINGPOINT_ID,RFIDOBJECT_ID) values (-507,-12,null,1,99,'Tractor (w/o trailer)','tractorAlone','tractorZ019',3,97,null,'02',null,42,11,null,null,null,0,0,0,1,1,'Z019','AdN TestData Truck&Trailer Composition, please don''t change.',0,null,null,null,null,-212);

  
prompt INSERTING into TLGNC_TRAILER
INSERT INTO TLGNC_TRAILER (TRAILER_ID,CARRIER_ID,TRAILER_NAME,TRAILER_PLATE_NO,TRAILER_TYPE_CD,TRAILER_TRSP_CAT_CD,ACTIVE_FLAG,TRAILER_COMMENT,TARE_WEIGHT_T,RFIDOBJECT_ID, NR_CHAMBERS, MAX_NET_WEIGHT_CHAMBER1_T, MAX_NET_WEIGHT_CHAMBER2_T, SAP_VEHICLE_CHAR_CD, CURRENT_TRUCK_ID)
	VALUES (-100,-11,'Trailer 1', 'ZH101',1,1,1,'Silo Auflieger',7,-110, 1, 27, 0, 0, -101);
INSERT INTO TLGNC_TRAILER (TRAILER_ID,CARRIER_ID,TRAILER_NAME,TRAILER_PLATE_NO,TRAILER_TYPE_CD,TRAILER_TRSP_CAT_CD,ACTIVE_FLAG,TRAILER_COMMENT,TARE_WEIGHT_T,RFIDOBJECT_ID, NR_CHAMBERS, MAX_NET_WEIGHT_CHAMBER1_T, MAX_NET_WEIGHT_CHAMBER2_T, SAP_VEHICLE_CHAR_CD, CURRENT_TRUCK_ID)
	VALUES (-101,-11,'Trailer 2', 'ZH102',1,1,1,'Silo Auflieger',7,-111, 1, 27, 0, 0, -102);
INSERT INTO TLGNC_TRAILER (TRAILER_ID,CARRIER_ID,TRAILER_NAME,TRAILER_PLATE_NO,TRAILER_TYPE_CD,TRAILER_TRSP_CAT_CD,ACTIVE_FLAG,TRAILER_COMMENT,TARE_WEIGHT_T,RFIDOBJECT_ID, NR_CHAMBERS, MAX_NET_WEIGHT_CHAMBER1_T, MAX_NET_WEIGHT_CHAMBER2_T, SAP_VEHICLE_CHAR_CD)
	VALUES (-102,-12,'Trailer 3', 'ZH103',1,1,1,'Silo Auflieger',7,-112, 1, 27, 0, 0);
-- LOGON-5806 Additional trucks/trailers for carrier-12	
Insert into TLGNC_TRAILER (TRAILER_ID,CARRIER_ID,ACTIVE_FLAG,TRAILER_NAME,TRAILER_PLATE_NO,SAP_VEHICLE_NO,TRAILER_TRSP_CAT_CD,TRAILER_TYPE_CD,TRAILER_COMMENT,MAX_GROSS_WEIGHT_T,TARE_WEIGHT_T,MANUFACTURER,AXIS_COUNT,VOLUME_M3,BRANDING_FLAG,RFIDOBJECT_ID,CURRENT_TRUCK_ID,NR_CHAMBERS,MAX_NET_WEIGHT_CHAMBER1_T,MAX_NET_WEIGHT_CHAMBER2_T,SAP_VEHICLE_IS_INTERNAL_FLAG,SAP_VEHICLE_CHAR_CD,SAP_VEHICLE_TYPE_CD) values (-200,-12,1,'Concrete','conc','7111281',6,1,null,42,21,null,'02',0,0,-208,-503,0,0,0,1,1,'T010');
Insert into TLGNC_TRAILER (TRAILER_ID,CARRIER_ID,ACTIVE_FLAG,TRAILER_NAME,TRAILER_PLATE_NO,SAP_VEHICLE_NO,TRAILER_TRSP_CAT_CD,TRAILER_TYPE_CD,TRAILER_COMMENT,MAX_GROSS_WEIGHT_T,TARE_WEIGHT_T,MANUFACTURER,AXIS_COUNT,VOLUME_M3,BRANDING_FLAG,RFIDOBJECT_ID,CURRENT_TRUCK_ID,NR_CHAMBERS,MAX_NET_WEIGHT_CHAMBER1_T,MAX_NET_WEIGHT_CHAMBER2_T,SAP_VEHICLE_IS_INTERNAL_FLAG,SAP_VEHICLE_CHAR_CD,SAP_VEHICLE_TYPE_CD) values (-201,-12,1,'Dumpable','dump','7111281',5,1,null,44,11,null,'02',0,0,-209,-504,0,0,0,1,1,'T010');
Insert into TLGNC_TRAILER (TRAILER_ID,CARRIER_ID,ACTIVE_FLAG,TRAILER_NAME,TRAILER_PLATE_NO,SAP_VEHICLE_NO,TRAILER_TRSP_CAT_CD,TRAILER_TYPE_CD,TRAILER_COMMENT,MAX_GROSS_WEIGHT_T,TARE_WEIGHT_T,MANUFACTURER,AXIS_COUNT,VOLUME_M3,BRANDING_FLAG,RFIDOBJECT_ID,CURRENT_TRUCK_ID,NR_CHAMBERS,MAX_NET_WEIGHT_CHAMBER1_T,MAX_NET_WEIGHT_CHAMBER2_T,SAP_VEHICLE_IS_INTERNAL_FLAG,SAP_VEHICLE_CHAR_CD,SAP_VEHICLE_TYPE_CD) values (-202,-12,1,'MixGoods','mixgood','7111281',4,1,null,0,11,null,'02',0,null,-210,-505,0,0,0,1,1,'T010');
Insert into TLGNC_TRAILER (TRAILER_ID,CARRIER_ID,ACTIVE_FLAG,TRAILER_NAME,TRAILER_PLATE_NO,SAP_VEHICLE_NO,TRAILER_TRSP_CAT_CD,TRAILER_TYPE_CD,TRAILER_COMMENT,MAX_GROSS_WEIGHT_T,TARE_WEIGHT_T,MANUFACTURER,AXIS_COUNT,VOLUME_M3,BRANDING_FLAG,RFIDOBJECT_ID,CURRENT_TRUCK_ID,NR_CHAMBERS,MAX_NET_WEIGHT_CHAMBER1_T,MAX_NET_WEIGHT_CHAMBER2_T,SAP_VEHICLE_IS_INTERNAL_FLAG,SAP_VEHICLE_CHAR_CD,SAP_VEHICLE_TYPE_CD) values (-203,-12,1,'Powder','Powder','7111281',1,1,null,42,22,null,'02',0,0,-211,-506,0,0,0,1,1,'T010');
-- LOGON-5695 Need unlinked Truck/Trailer to test CreateCompletedCustomerOrder
Insert into TLGNC_TRAILER (TRAILER_ID,CARRIER_ID,ACTIVE_FLAG,TRAILER_NAME,TRAILER_PLATE_NO,SAP_VEHICLE_NO,TRAILER_TRSP_CAT_CD,TRAILER_TYPE_CD,TRAILER_COMMENT,MAX_GROSS_WEIGHT_T,TARE_WEIGHT_T,MANUFACTURER,AXIS_COUNT,VOLUME_M3,BRANDING_FLAG,RFIDOBJECT_ID,CURRENT_TRUCK_ID,NR_CHAMBERS,MAX_NET_WEIGHT_CHAMBER1_T,MAX_NET_WEIGHT_CHAMBER2_T,SAP_VEHICLE_IS_INTERNAL_FLAG,SAP_VEHICLE_CHAR_CD,SAP_VEHICLE_TYPE_CD) values (-204,-12,1,'PowderAlone','PowderAlone','7111281',1,1,'AdN Testdata, don''t delete',42,22,null,'02',0,0,-213,null,0,0,0,1,1,'T010');



prompt INSERTING into TLGNC_POOL_TRUCK
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values ( -11, -11, -100);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values ( -12, -11, -101);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values ( -13, -11, -102);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values ( -14, -12, -200);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values ( -15, -12, -201);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values ( -16, -12, -202);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values ( -17, -13, -100);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values ( -18, -13, -101);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values ( -19, -13, -102);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values ( -20, -13, -200);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values ( -21, -13, -202);
-- LOGON-5806 Additional trucks/trailers for carrier-12	
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values (-300,-13,-500);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values (-301,-13,-501);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values (-302,-13,-503);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values (-303,-13,-504);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values (-304,-13,-505);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values (-305,-13,-506);
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values (-306,-13,-502);
-- LOGON-5695 Need unlinked Truck/Trailer to test CreateCompletedCustomerOrder
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values (-307,-13,-507);


-- was sap_bp_id 33294 _in our testdata_! different when on top of migrated data...
--prompt INSERTING into TLGNC_SHIPTO
--INSERT INTO TLGNC_SHIPTO
--(SHIPTO_ID,SHIPTO_SAP_BP_ID,ACTIVE_FLAG,NOTE_ORDER_MGMT,NOTE_DELIVERY,NOTE_DISPATCHING,NOTE_TRUCK_DRIVER,NOTE_RAILWAY_DISP,OPEN1_FROM,OPEN1_TO,OPEN2_FROM,OPEN2_TO,PAPER_DELIVNOTE_FLAG,EMAIL_DELIVNOTE_FLAG,FAX_DELIVNOTE_FLAG,ORDER_CONF_EMAIL_FLAG,ORDER_CONF_EMAIL_ADDRESSES,ORDER_CONF_FAX_FLAG,ORDER_CONF_FAX_NUMBER,ORDER_CONF_SMS_FLAG,ORDER_CONF_SMS_NUMBER,CONTACT_PERSON_ID,SHIPTO_TYPE_CD,SELF_SERVICE_FLAG,DAILY_ORDER_CONTACT_FLAG,SIGNATURE_TYPE_CD,COORD_LAT,COORD_LON,COORD_FIX_FLAG,GEOFENCE_RADIUS,UNLOADING_TIME)
--(select 1,sapbp.sap_bp_id,1,'Order Management','delivery note','Dispatcher''s note','Truck driver''s note','Railway dispatcher''s note', 450,550,680,800,1,1,0,1, 'info@company.com',0,'fax',0,'sms',null,0,1,1,0,47.496792215201786,8.726577758789062,1,450,15 from tlgns_sap_bp sapbp where sap_id='1241133');

-- Initial Silo? (Depends on Material amongst others)

-- Initial Billto per Shipto?

/* TODO: Update for ADZA testdata 

-- Initial SP, LP, LP MAT Restr

prompt INSERTING into TLGNC_SHIPPINGPOINT
Insert into TLGNC_SHIPPINGPOINT (SHIPPINGPOINT_ID, PLANT_CD, SAP_SHIPPINGPOINT_NO, known_also_in_sap_flag, SHIPPINGPOINT_NAME, SHIPPPOINT_TYPE_CD, SAP_SHIPPING_COND_CD, PARALLEL_SPID, RAILWAY_STATION_ID, SHIPPINGPOINT_COMMENT, COORD_LAT, COORD_LON, COORD_FIX_FLAG, GEOFENCE_RADIUS, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN)
  values (-11, 'S104', 'ADN1', 0, 'Initial shipping point', 0, 'E1', '1', null, 'Initial shipping point for Muenchenstein (plant S104, shippingPointID -11, ADN1)', 47.383173005260005, 8.527994156102068, 1, 50, 1, systimestamp, 1, systimestamp, 1);

prompt INSERTING into TLGNC_LOADINGPOINT
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, package_type_cd, weigh_system_type_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, loadingpoint_comment, active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
  values (-1101, -11, 'LP1: Fluvio 4', 'LP1: Fluvio 4', 1, 2, 1, null, null, 'Test Loadingpoint with 1 bulk product', 1, null, 42000, null, 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, package_type_cd, weigh_system_type_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, loadingpoint_comment, active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
  values (-1102, -11, 'LP2: Normo 4', 'LP2: Normo 4', 1, 2, 1, null, null, 'Test Loadingpoint with 1 bulk product', 1, null, 42000, null, 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, package_type_cd, weigh_system_type_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, loadingpoint_comment, active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
  values (-1103, -11, 'LP3: Fluvio 4 & Normo 4 Manuell', 'LP3: Fluvio 4 & Normo 4 Manuell', 1, 2, 1, null, null, 'Test Loadingpoint with 2 bulk products Parallel', 1, null, 42000, null, 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, package_type_cd, weigh_system_type_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, loadingpoint_comment, active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
  values (-1104, -11, 'LP4: Fluvio 4 & Normo 4 Parallel', 'LP4: Fluvio 4 & Normo 4 Parallel', 1, 1, 1, null, null, 'Test Loadingpoint with 2 bulk products Parallel', 1, null, 42000, null, 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, package_type_cd, weigh_system_type_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, loadingpoint_comment, active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
  values (-1105, -11, 'LP5: Normo 4/Fluvio 4 Sack', 'LP5: Normo 4/Fluvio 4 Sack', 2, 2, 1, null, null, 'Test Loadingpoint with 2 bag products', 1, null, 42000, null, 1, systimestamp, 1, systimestamp, 0);

prompt INSERTING into TLGNC_LDPOINT_MAT_RESTR
-- Naming -110401: Shippinpoint -11, loadingpoint -5 of it (i.e. -1104), mat restriction 01
-- Note: Material ID is built on the fact that *currently* our ID are = to SAP No.
-- When adding/changing, check that it's consistent with the LP names!
Insert into TLGNC_LDPOINT_MAT_RESTR (LDPOINT_MAT_RESTR_ID, LOADINGPOINT_ID, SAP_MATERIAL_ID, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN)
  (select -110101, -1101, m.sap_material_id, 1, systimestamp, 1, systimestamp, 0 from tlgns_sap_material m where m.sap_material_no='1404839');
Insert into TLGNC_LDPOINT_MAT_RESTR (LDPOINT_MAT_RESTR_ID, LOADINGPOINT_ID, SAP_MATERIAL_ID, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN)
  (select -110201, -1102, m.sap_material_id, 1, systimestamp, 1, systimestamp, 0 from tlgns_sap_material m where m.sap_material_no='1404860');
Insert into TLGNC_LDPOINT_MAT_RESTR (LDPOINT_MAT_RESTR_ID, LOADINGPOINT_ID, SAP_MATERIAL_ID, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN)
  (select -110301, -1103, m.sap_material_id, 1, systimestamp, 1, systimestamp, 0 from tlgns_sap_material m where m.sap_material_no='1404860');
Insert into TLGNC_LDPOINT_MAT_RESTR (LDPOINT_MAT_RESTR_ID, LOADINGPOINT_ID, SAP_MATERIAL_ID, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN)
  (select -110302, -1103, m.sap_material_id, 1, systimestamp, 1, systimestamp, 0 from tlgns_sap_material m where m.sap_material_no='1404839');
Insert into TLGNC_LDPOINT_MAT_RESTR (LDPOINT_MAT_RESTR_ID, LOADINGPOINT_ID, SAP_MATERIAL_ID, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN)
  (select -110401, -1104, m.sap_material_id, 1, systimestamp, 1, systimestamp, 0 from tlgns_sap_material m where m.sap_material_no='1404860');
Insert into TLGNC_LDPOINT_MAT_RESTR (LDPOINT_MAT_RESTR_ID, LOADINGPOINT_ID, SAP_MATERIAL_ID, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN)
  (select -110501, -1105, m.sap_material_id, 1, systimestamp, 1, systimestamp, 0 from tlgns_sap_material m where m.sap_material_no='1404854');
Insert into TLGNC_LDPOINT_MAT_RESTR (LDPOINT_MAT_RESTR_ID, LOADINGPOINT_ID, SAP_MATERIAL_ID, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN)
  (select -110502, -1105, m.sap_material_id, 1, systimestamp, 1, systimestamp, 0 from tlgns_sap_material m where m.sap_material_no='1404838');



-- Some very basic initial cost element for this shippingpoint
prompt INSERTING into TLGNC_MAT_SHIPPINGPOINT

insert into tlgnc_mat_shippingpoint (mat_shippingpoint_id, sap_material_id, shippingpoint_id, storage_location, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd) 
(select -11, m.sap_material_id, -11, null, 33, 35, null, 'HUF'
  from tlgns_sap_material m
  where sap_material_no='1404839');

--this records will be used to test cost. Commercial tab (Order Details). pay attention to shipping point id (1003)!
insert into tlgnc_mat_shippingpoint (mat_shippingpoint_id, sap_material_id, shippingpoint_id, storage_location, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd)
(select -12, m.sap_material_id, 1003, null, 33, 35, null, 'CHF'
	  from tlgns_sap_material m
	  where sap_material_no='1404839');

TODO: Update for ADZA testdata
*/

--PDA for James Bond User used for integration workflow tests having pda user no 200012
Insert into TLGNC_PDA_DEVICE (PDA_DEVICE_ID,PDA_DEVICE_NO,LAST_USER_ID,CARRIER_ID,ACTIVE_FLAG) values (-12,'ADNJAMESBO',null,null,1);

--Personal PDAs used by AdNovum people for logon-mobile dev & tests
insert into tlgnc_pda_device (pda_device_id, pda_device_no, last_user_id, carrier_id, type_description, inventory_number, simcard_cd, last_registration_date, software_version, pda_device_comment, active_flag)
values (-13, 'umg_android_a86f4b68ca8db9c7', null, null, 'Samsung Galaxy S3', null, null, sysdate, null, 'AdNovum Device used by jelan', 1);
insert into tlgnc_pda_device (pda_device_id, pda_device_no, last_user_id, carrier_id, type_description, inventory_number, simcard_cd, last_registration_date, software_version, pda_device_comment, active_flag)
values (-14, 'umg_android_5e7917921637ee94', null, null, 'Samsung Galaxy S3', null, null, sysdate, null, 'Personal Device cwi (AdNovum)', 1);
insert into tlgnc_pda_device (pda_device_id, pda_device_no, last_user_id, carrier_id, type_description, inventory_number, simcard_cd, last_registration_date, software_version, pda_device_comment, active_flag)
values (-15, 'umg_android_6b463f10b7d16fc3', null, null, 'Samsung Galaxy S3', null, null, sysdate, null, 'Personal Device uma (AdNovum)', 1);
insert into tlgnc_pda_device (pda_device_id, pda_device_no, last_user_id, carrier_id, type_description, inventory_number, simcard_cd, last_registration_date, software_version, pda_device_comment, active_flag)
values (-16, 'umg_android_efef98f44249626', null, null,  'Samsung Galaxy S3', null, null, sysdate, null, 'Personal Device jelan (AdNovum)', 1);




prompt INSERTING into TLGNC_RFIDOBJECT
Insert into TLGNC_RFIDOBJECT (RFIDOBJECT_ID,RFIDOBJECT_TYPE_CD) values (-11, 1);

Insert into TLGNC_RFIDOBJECT (RFIDOBJECT_ID,RFIDOBJECT_TYPE_CD,OBJECT_LABEL) values (-12,1,'200012');
Update TLGNU_USER set PDA_USER_RFIDOBJECT_ID=-12 where USER_ID = -12;

prompt INSERTING into TLGNC_RFIDTAG
-- Assigned - not assignable without deselecting
Insert into TLGNC_RFIDTAG (RFIDTAG_ID,RFIDOBJECT_ID,NAME,ACTIVE_FLAG,RFID_IDENTITY) values (-11,-11,'ADN001',1,'1234567890ABCDEF');
Insert into TLGNC_RFIDTAG (RFIDTAG_ID,RFIDOBJECT_ID,NAME,ACTIVE_FLAG,RFID_IDENTITY) values (-12,-11,'ADN002',1,'1234567890ABCDEE');
-- All of those RFID are NOT yet assigned to an object, therefore RFIDOBJECT = null
Insert into TLGNC_RFIDTAG (RFIDTAG_ID,RFIDOBJECT_ID,NAME,ACTIVE_FLAG,RFID_IDENTITY) values (-13,null,'ADN003',1,'1234567890ABCDED');
Insert into TLGNC_RFIDTAG (RFIDTAG_ID,RFIDOBJECT_ID,NAME,ACTIVE_FLAG,RFID_IDENTITY) values (-14,null,'ADN004',1,'1234567890ABCDEC');
Insert into TLGNC_RFIDTAG (RFIDTAG_ID,RFIDOBJECT_ID,NAME,ACTIVE_FLAG,RFID_IDENTITY) values (-15,null,'ADN005',1,'1234567890ABCDEB');
Insert into TLGNC_RFIDTAG (RFIDTAG_ID,RFIDOBJECT_ID,NAME,ACTIVE_FLAG,RFID_IDENTITY) values (-16,null,'ADN006',1,'1234567890ABCDEA');


-- for testing PDF, only update sales org. (S101 used for Toggenburger AG) in order to have a test banner 'pdf_general_info_banner' which has a reference to tlgnc_image.image_name
UPDATE TLGNR_SORG_TX SET IMAGE_DEL_NOTE_GENERAL_INFO = 'pdf_general_info_banner' WHERE SORG_CD='S101';

-- uncomment the rollback for testing...
-- rollback;
commit;

