-- Based on ADN74269
-- Some additional Loadingpoints
-- All based on CHE prod DB, with "1:-1" mapping & renaming: Prefix with ADN, and multiply IDs with -1 (=> identical ID, but negative)
-- (Easiest way to add more necessary data that automatically fits later on)

prompt '---------- adza_testdata/tlgnc_loadingpoint.sql ----------'


--Urs: null to 'rfidobject_id'
--prompt INSERTING into TLGNC_RFIDOBJECT

--insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-1041, 2, 'ADN Bahnverlad 1');
--insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-1042, 2, 'ADN Bahnverlad 2');
--insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-3801, 2, 'ADN Sackverlad Bahn');
--insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-1045, 2, 'ADN Silo 1');
--insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-1046, 2, 'ADN Silo 2');
--insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-1047, 2, 'ADN Sack 1');
--insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-1048, 2, 'ADN Sack 2');
--insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-1049, 2, 'ADN Bahnwagen');
--insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-8781, 2, 'ADN Container Sion');
--insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-11462, 2, 'ADN Rail Münchenstein');
--insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-1055, 2, 'ADN Autoverlad Münchenstein');
--insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-1054, 2, 'ADN Bahnverlad Münchenstein');
--insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-1093, 2, 'ADN Rail REP ADZA');

prompt INSERTING into TLGNC_LOADINGPOINT

prompt 'ADN Bahnverlad 1'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1001, -1001 /*was 1001 in CHE*/, 'ADN Bahnverlad 1', 'ADN Bahnverlad 1', 
1, 0, 3, '12', null, null, null, 
1, null, 90000, null, null, null, 
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bahnverlad 2'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1002, -1001 /*was 1001 in CHE*/, 'ADN Bahnverlad 2', 'ADN Bahnverlad 2',
1, 0, 3, null, null, null, null, 
1, null, 41200, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Sackverlad Bahn'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1261, -1001 /*was 1001 in CHE*/, 'ADN Sackverlad Bahn', 'ADN Sackverlad Bahn',
2, 2, 3, null, null, null, null,
1, null, 41000, null, null, null,
1, systimestamp, 1, systimestamp, 0);

--inserting rfidobject for ADN Silo 1 - needed for NB tests to have an rfidobject and a tag
prompt inserting rfidobject
Insert into TLGNC_RFIDOBJECT (RFIDOBJECT_ID,RFIDOBJECT_TYPE_CD,OBJECT_LABEL,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-200,2,'8101_AD35_ADNSILO1',1,systimestamp,1,systimestamp,0);
prompt inserting rfid tag ADSILO
Insert into TLGNC_RFIDTAG (RFIDTAG_ID,RFIDOBJECT_ID,NAME,RFID_IDENTITY,ACTIVE_FLAG,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-200,-200,'ADSILO','ADN_SILO_LOADING',1,1,systimestamp,1,systimestamp,1);

prompt 'ADN Silo 1'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1005, -1002 /*was 1002 in CHE*/, 'ADN Silo 1', 'ADN Silo 1',
1, 0, 3, 'SILO1', null, null, 'Please do not change anything for this loadingpoint, Nightly Build is using this, and expects it as-is.', 
1, null, null, -200, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Silo 2'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1006, -1002 /*was 1002 in CHE*/, 'ADN Silo 2', 'ADN Silo 2',
1, 3, 3, null, null, null, null,
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Silo 3'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label,
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon,
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1009, -1002 /*was 1002 in CHE*/, 'ADN Silo 3', 'ADN Silo 3',
1, 0, 3, 'SILO3', null, null, 'AdN Nightlybuid Testdata, DO NOT CHANGE',
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Sack 1'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1007, -1002 /*was 1002 in CHE*/, 'ADN Sack 1', 'ADN Sack 1',
2, 2, 3, null, null, null, null,
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Sack 2'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1008, -1002 /*was 1002 in CHE*/, 'ADN Sack 2', 'ADN Sack 2',
2, 3, 3, null, null, null, null, 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bahnwagen'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1481, -1002 /*was 1002 in CHE*/, 'ADN Bahnwagen', 'ADN Bahnwagen',
1, 3, 3, null, null, null, null,
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Container Sion'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1281, -1127 /*was 1127 in CHE*/, 'ADN Container Sion', 'ADN Container Sion',
1, 0, 3, null, null, null, null, 
1, null, 41000, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Rail Münchenstein'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1361, -985 /*was 985 in CHE*/, 'ADN Rail Münchenstein', 'ADN Rail Münchenstein',
1, 2, 3, null, null, null, null, 
1, null, 90000, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Autoverlad Münchenstein'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1015, -1003 /*was 1003 in CHE*/, 'ADN Autoverlad Münchenstein', 'ADN Autoverlad Münch.',
1, 0, 1, '32', null, null, null, 
1, null, 41200, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bahnverlad Münchenstein'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1014, -1003 /*was 1003 in CHE*/, 'ADN Bahnverlad Münchenstein', 'ADN Bahnverlad Münch.',
1, 0, 1, '31', null, null, null, 
1, null, 41200, null, null, null,
1, systimestamp, 1, systimestamp, 0);

-- TODO nemethze 2011.09.06 FIX This loadingpoint
--prompt 'ADN Truck Load CEM Plant BIS'
--insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
--package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
--active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
--ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
--values (-1016, -1004 , 'ADN Truck Load CEM Plant BIS', 'ADN Truck Load CEM Plant BIS',
--1, 3, 3, '31', null, null, null, 
--1, null, 41200, null, null, null,
--1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Rail REP ADZA'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1033, -986 /*was 986 in CHE*/, 'ADN Rail REP ADZA', 'ADN Rail REP ADZA.',
1, 0, 3, null, null, null, null, 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'Container Terminal Aarau'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1057, -1024 /*was 1024 in CHE*/, 'ADN Container Terminal Aarau', 'ADN Container Terminal Aarau.',
1, 0, 3, null, null, null, null, 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bahnverlad LQ'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1241, -998 /*was 986 in CHE*/, 'ADN Bahnverlad LQ', 'ADN Bahnverlad LQ',
1, 2, 3, null, null, null, null, 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bahnverlad Container'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1058, -1001 /*was 1001 in CHE*/, 'ADN Bahnverlad Container', 'ADN Bahnverlad Container', 
1, 0, 3, '11', null, null, null, 
1, null, 90000, null, null, null, 
1, systimestamp, 1, systimestamp, 0);

-- 'ADN Truck SIG Fenics - Plant 8101'; ID = -1200
-- one for each packaging type: Bag / Bulk dumped / Bulk powder 
prompt 'ADN Bag / ADN Truck SIG Fenics - Plant 8101'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1500, -1200, 'ADN Bag', 'ADN Bag',
2, 0, 3, '20', null, null, 'ADN TestData, Please do not change anything for this loadingpoint.', 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bulk Dumped / ADN Truck SIG Fenics - Plant 8101'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1501, -1200, 'ADN Bulk Dumped', 'ADN Bulk Dumped',
4, 0, 3, '21', null, null, 'ADN TestData, Please do not change anything for this loadingpoint.', 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bulk Powder / ADN Truck SIG Fenics - Plant 8101'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1502, -1200, 'ADN Bulk Powder', 'ADN Bulk Powder',
1, 0, 3, '22', null, null, 'ADN TestData, Please do not change anything for this loadingpoint.', 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bulk Powder 2 / ADN Truck SIG Fenics - Plant 8101'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-9999, -1200 /*there was no Fenics loadingpoint in CH*/, 'ADN Bulk Powder 2', 'ADN Bulk Powder 2',
1, 0, 2, 'FENICS', null, null, 'ADN TestData, Please do not change anything for this loadingpoint.',
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bag 2 / ADN Truck SIG Fenics - Plant 8101'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-9998, -1200 /*there was no Fenics loadingpoint in CH*/, 'ADN Bag 2', 'ADN Bag 2',
2,0,3, 'BAGFENICS', null, null, 'ADN TestData, Please do not change anything for this loadingpoint.',
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);


-- Loadingpoints for shippingpoint 'ADN Truck SIG Parallel - Plant 8101: ID = -1201'
-- one for each packaging type: Bag / Bulk dumped / Bulk powder

prompt 'ADN Bag / ADN Truck SIG Parallel - Plant 8101'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1503, -1201, 'ADN Bag', 'ADN Bag',
2, 0, 3, '23', null, null, 'ADN TestData, Please do not change anything for this loadingpoint.', 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bulk Dumped / ADN Truck SIG Parallel - Plant 8101'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1504, -1201, 'ADN Bulk Dumped', 'ADN Bulk Dumped',
4, 0, 3, '24', null, null, 'ADN TestData, Please do not change anything for this loadingpoint.', 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bulk Powder / ADN Truck SIG Parallel - Plant 8101'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1505, -1201, 'ADN Bulk Powder', 'ADN Bulk Powder',
1, 0, 3, '25', null, null, 'ADN TestData, Please do not change anything for this loadingpoint.', 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

-- Loadingpoints for shippingpoint 'ADN Truck Fenics - Plant 8301: ID = -1202'
-- one for each packaging type: Bag / Bulk dumped / Bulk powder

prompt 'ADN Bag / ADN Truck Fenics - Plant 8301'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1506, -1202, 'ADN Bag', 'ADN Bag',
2, 0, 3, '26', null, null, 'ADN TestData, Please do not change anything for this loadingpoint.', 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bulk Dumped / ADN Truck Fenics - Plant 8301'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1507, -1202, 'ADN Bulk Dumped', 'ADN Bulk Dumped',
4, 0, 3, '27', null, null, 'ADN TestData, Please do not change anything for this loadingpoint.', 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bulk Powder / ADN Truck Fenics - Plant 8301'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1508, -1202, 'ADN Bulk Powder', 'ADN Bulk Powder',
1, 0, 3, '28', null, null, 'ADN TestData, Please do not change anything for this loadingpoint.', 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

-- Loadingpoints for shippingpoint 'ADN Truck Parallel - Plant 8301: ID = -1203'
-- one for each packaging type: Bag / Bulk dumped / Bulk powder
prompt 'ADN Bag / ADN Truck Parallel - Plant 8301'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1509, -1203, 'ADN Bag', 'ADN Bag',
2, 0, 3, '29', null, null, 'ADN TestData, Please do not change anything for this loadingpoint.', 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bulk Dumped  / ADN Truck Parallel - Plant 8301'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1510, -1203, 'ADN Bulk Dumped', 'ADN Bulk Dumped',
4, 0, 3, '30', null, null, 'ADN TestData, Please do not change anything for this loadingpoint.', 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Bulk Powder / ADN Truck Parallel - Plant 8301'
insert into tlgnc_loadingpoint (loadingpoint_id, shippingpoint_id, loadingpoint_name, loadingpoint_object_label, 
package_type_cd, weigh_system_lp_cd, deliv_note_print_type_cd, weigh_system_loadingpoint_no, printer_cd, plant_track_cd, loadingpoint_comment,
active_flag, loading_time, gross_weight_limit_kg, rfidobject_id, wagon_load_first_position_flag, deliv_note_copies_logon, 
ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) 
values (-1511, -1203, 'ADN Bulk Powder', 'ADN Bulk Powder',
1, 0, 3, '31', null, null, 'ADN TestData, Please do not change anything for this loadingpoint.', 
1, null, null, null, null, null,
1, systimestamp, 1, systimestamp, 0);

--------------------------------------------
-- Terminal Application Test Loadingpoint --
--------------------------------------------

prompt 'ADN Terminal App Test LP'
insert into TLGNC_LOADINGPOINT
(LOADINGPOINT_ID, SHIPPINGPOINT_ID, LOADINGPOINT_NAME         , LOADINGPOINT_OBJECT_LABEL , PACKAGE_TYPE_CD, WEIGH_SYSTEM_LP_CD, DELIV_NOTE_PRINT_TYPE_CD, DELIV_NOTE_COPIES_LOGON, WEIGH_SYSTEM_LOADINGPOINT_NO, PRINTER_CD, PLANT_TRACK_CD, WAGON_LOAD_FIRST_POSITION_FLAG, LOADINGPOINT_COMMENT, ACTIVE_FLAG, LOADING_TIME, GROSS_WEIGHT_LIMIT_KG, RFIDOBJECT_ID, CTL_CRE_UID, CTL_CRE_TS  , CTL_MOD_UID, CTL_MOD_TS  , CTL_TCN) values
(-1016          , -1003           , 'ADN Terminal App Test LP', 'ADN Terminal App Test LP', 2              , 3                 , 1                       , 2                      , null                        , null      , null          , null                          , null                , 1          , null        , null                 , null         , 1          , systimestamp, 1          , systimestamp, 0      ); 

prompt 'Add material restrictions to "ADN Terminal App Test LP"'
insert into tlgnc_ldpoint_mat_restr m
(m.ldpoint_mat_restr_id                , m.loadingpoint_id, m.sap_material_id )
(select slgnc_ldpoint_mat_restr.nextval, -1016            , sm.sap_material_id
	from tlgns_sap_material_plant smp
	join tlgns_sap_material sm
		on (sm.sap_material_id = smp.sap_material_id)
	where smp.plant_cd = '8104'
		and sm.material_package_cd in ('S002','S003')
);
