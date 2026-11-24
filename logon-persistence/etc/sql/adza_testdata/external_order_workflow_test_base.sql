prompt Inserting test data for external order workflow tests

---- Test carrier for external workflow test. All these objects can be created from Logon, but the NB tests expect always the same truck carrier.
-- CreateExternalOrderTest.java + DispatchExternalOrderTest + ExternalPDA soapui test uses these!
prompt INSERTING into TLGNS_SAP_VENDOR
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (-111, 'ADNEXTERN',		'ADNEXTERN',	'ZNOR', 0, 'ADN External carrier',	null, 'CH', '8005', 'Zürich', 'ZH', null, 'Röntgenstrasse', 	'22', 'de' ,null,1,1,1);

prompt INSERTING into TLGNC_CARRIER
Insert into TLGNC_CARRIER (CARRIER_ID, DISPLAY_NAME, ACTIVE_FLAG, SAP_VENDOR_ID, transmit_to_carrier_flag,CARRIER_REQUIRES_PLANNING_FLAG) values (-111, 'ADN External carrier', 1, -111, 0, 0);

-- Test external pool
prompt INSERTING into tlgnc_pool
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-111, '10', 1, 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-111, 'de', 'ADN Pool External'       , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-111, 'en', 'ADN Pool External'        , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-111, 'fr', 'ADN Pool External (fr)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-111, 'it', 'ADN Pool External (it)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-111, 'es', 'ADN Pool External (es)'   , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_CARRIER
Insert into TLGNC_POOL_CARRIER (POOL_CARRIER_ID,POOL_ID,CARRIER_ID) values (-111, -111,-111);

--RFidobject for the truck needs to precede the truck itself
prompt INSERTING into TLGNc_RFIDOBJECT
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL)
	VALUES (-1001, 4 , 'ADN_EXTERNAL_TRUCK_1');
	
INSERT INTO TLGNC_RFIDOBJECT (RFIDOBJECT_ID, RFIDOBJECT_TYPE_CD, OBJECT_LABEL)
	VALUES (-1002, 4 , 'ADN_EXTERNAL_TRUCK_2');	
	
---- Test truck	
prompt INSERTING into TLGNC_TRUCK
insert into tlgnc_truck (truck_id, carrier_id, priority, truck_name, truck_plate_no, truck_transport_cat_cd, truck_type_cd, active_flag, truck_comment, pda_available_flag, homebase_coord_lat, homebase_coord_lon, max_gross_weight_t, tare_weight_t, rfidobject_id, nr_chambers, max_net_weight_chamber1_t, max_net_weight_chamber2_t, sap_vehicle_char_cd, sap_vehicle_no, sap_vehicle_is_internal_flag) values
  (-1001, -111, 99, 'ADN External BULK truck', 'ADN_EXTERNAL_TRUCK_1', 1, 1, 1, 'External truck used for testing external order functionality.',     1, null, null, 42, 15, -1001, 1, 27, 0, 0, 'ADN_SAP_NO', 0);

insert into tlgnc_truck (truck_id, carrier_id, priority, truck_name, truck_plate_no, truck_transport_cat_cd, truck_type_cd, active_flag, truck_comment, pda_available_flag, homebase_coord_lat, homebase_coord_lon, max_gross_weight_t, tare_weight_t, rfidobject_id, nr_chambers, max_net_weight_chamber1_t, max_net_weight_chamber2_t, sap_vehicle_char_cd, sap_vehicle_no, sap_vehicle_is_internal_flag) values
  (-1002, -111, 99, 'ADN External BAG truck', 'ADN_EXTERNAL_TRUCK_2',  4, 1, 1, 'External bag truck used for testing external order functionality.', 1, null, null, 42, 15, -1002, 1, 27, 0, 0, 'ADN_SAP_NO', 0);
  
prompt INSERTING into TLGNC_POOL_TRUCK
Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values ( -1111, -111, -1001);

Insert into TLGNC_POOL_TRUCK (POOL_TRUCK_ID,POOL_ID,TRUCK_ID) values ( -1112, -111, -1002);
---- Test external sites
prompt INSERTING into TLGNC_EXT_SITE  
Insert into TLGNC_EXT_SITE (EXT_SITE_ID,CARRIER_ID,ACTIVE_FLAG,SITE_NAME,SITE_LOAD_UNLOAD,SITE_CONTACT_PERSON_TXT,SITE_STREET_HOUSE_NO,SITE_COUNTRY,SITE_POSTAL_CODE,SITE_CITY,COORD_LAT,COORD_LON,GEOFENCE_RADIUS_M,LOAD_UNLOAD_TIME_MIN,CARRIER_TRUCK_DRIVER_NOTE) values (-1000,-111,1,'External shippingpoint site, Szombathely, Crh',15,'Minimal setup','Szombathely, Csaba str','HU','9700','Szombathely',47.224785,16.642805,30,15,'Minimal setup external shippingpoint comment, Szombathely Crh plant');
Insert into TLGNC_EXT_SITE (EXT_SITE_ID,CARRIER_ID,ACTIVE_FLAG,SITE_NAME,SITE_LOAD_UNLOAD,SITE_CONTACT_PERSON_TXT,SITE_STREET_HOUSE_NO,SITE_COUNTRY,SITE_POSTAL_CODE,SITE_CITY,COORD_LAT,COORD_LON,GEOFENCE_RADIUS_M,LOAD_UNLOAD_TIME_MIN,CARRIER_TRUCK_DRIVER_NOTE) values (-1001,-111,1,'External shipto site, Budapes, Adnovum',15,'Minimal setup','Budapest, Kapas str. 12','HU','1027','Budapest',47.508541,19.033318,30,15,'Minimal setup external shipto comment, Adnovum site');

---- Test external materials

prompt INSERTING into TLGNC_EXT_MATERIAL
Insert into TLGNC_EXT_MATERIAL (EXT_MATERIAL_ID,CARRIER_ID,ACTIVE_FLAG,MATERIAL_NAME,MATERIAL_COMMENT) values (-1000,-111,1,'Minimal setup external material','Minimal setup external material. Created by the minimal setup script run to create test data.');

commit;

