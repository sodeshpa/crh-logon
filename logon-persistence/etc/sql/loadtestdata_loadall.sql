-- Loads all testdata meant for LOAD and PERFORMANCE tests.
--
-- Execute in:
-- - AdNovum DBs for LOAD TEST and PERFORMANCE tests only.
-- - Sometimes maybe also useful in Crh test-environment (to verify behaviour in real environment)
-- - NOT meant for productive database! -> such data to be placed in initialdata_loadall.sql

spool /temp/logon_loadtestdata_loadall.log

-- to prevent errors with the ampersand
set define off
-- Default Logon NLS settings
@set_logon_session_settings.sql

-- Preconditions: testdata_loadall.sql MUST have been called before, i.e. all normal users, Vendors etc must be in the DB.

--load test carrier
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number,vn_is_comm_carrier,vn_is_exec_carrier,vn_is_internal)
values (-15, '800040',		'0000800040',	'ZNOR', 0, 'Adn Load Test Carrier 800040',	null, 'CH', '8005', 'Zürich', 'ZH', null, 'Luisenstrasse', 	'6', 'en', null,1,1,1);

Insert into TLGNS_SAP_VENDOR_PORG (SAP_VENDOR_PORG_ID,SAP_VENDOR_ID,PURCHASINGORG_CD,MARKED_FOR_DELETION) values (-15,-15,'AD01',0);
Insert into TLGNS_SAP_VN_PF_EXEC_CARRIER (SAP_VENDOR_PORG_ID,EXEC_SAP_VENDOR_PORG_ID,PURCHASINGORG_CD) values (-15,-15,'AD01');

--load test carrier
Insert into TLGNC_CARRIER (CARRIER_ID, DISPLAY_NAME, ACTIVE_FLAG, SAP_VENDOR_ID, transmit_to_carrier_flag,CARRIER_REQUIRES_PLANNING_FLAG) values (-15, 'load test carrier 800040', 1, -15, 0, 0);

--load test pool
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-15, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-15, 'de', 'Load Test Pool'       , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-15, 'en', 'Load Test Pool'       , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-15, 'fr', 'Load Test Pool (fr)'       , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-15, 'it', 'Load Test Pool (it)'       , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-15, 'es', 'Load Test Pool (es)'       , 1, systimestamp, 1, systimestamp);

insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-17, -15, 'AD');
Insert into TLGNC_POOL_CARRIER (POOL_CARRIER_ID,POOL_ID,CARRIER_ID) values (-15, -15,-15);

@loadtestdata/insert_pda_device.sql
@loadtestdata/insert_truck.sql
@loadtestdata/insert_user_person_driver_role.sql

spool off
