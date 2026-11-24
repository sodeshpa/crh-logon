-- Based on ADN74269
-- Some additional Shippingpoints 
-- All based on CHE prod DB, with "1:-1" mapping & renaming: Prefix with ADN, and multiply IDs with -1 (=> identical ID, but negative)
-- (Easiest way to add more necessary data that automatically fits later on)

prompt '---------- adza_testdata/tlgnc_shippingpoint.sql ----------'

prompt INSERTING into TLGNC_SHIPPINGPOINT

prompt 'ADN Rail Siggenthal'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1001, '8101' /* was S101 in CHE*/, 'AD34' /*was S034 in CHE*/, 1, 'ADN Rail Siggenthal', 1, '02', 'F101',
  -1408, '46', 5, null, 'ADN Testdata, dimilar to Rail Siggenthal from CHE: S101/S034', 47.52087425, 8.240314722, 0, 50,
  0, null, 'TO', 0.39, 0.39, to_date('20200101','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Truck Siggenthal'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1002, '8101' /* was S101 in CHE*/, 'AD35' /*was S035 in CHE*/, 1, 'ADN Truck Siggenthal', 0, '01', 'F101',
  null, null, 1, 'siggenthal', 'ADN Testdata, similar to Truck Siggenthal from CHE: S101/S035', 47.52395346, 8.240432739, 1, 300,
  0, null, 'TO', 0.39, 0.39, to_date('20200101','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Container Sion'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1127, '8104' /*was S104 in CHE*/, 'AD78' /*was S278 in CHE*/, 1, 'ADN Container Sion', 1, 'Z5', 'F103',
  -1037, null, 4, null, 'ADN Testdata, similar to Containerterminal Sion from CHE: S104/S278', 47.53537612, 7.601440446, 0, 50,
  0, null, 'TO', 0.54, 0.54, to_date('20200101','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);
  
prompt 'ADN Rail Münchenstein'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-985, '8104' /*was S104 in CHE*/, 'AD58' /*was S058 in CHE*/, 1, 'ADN Rail Münchenstein', 1, '02', 'F103',
  -1037, null, 5, null, 'ADN Testdata, similar to Rail Münchenstein from CHE: S104/S058', 47.53537612, 7.601440446, 0, 50,
  0, null, 'TO', 0.54, 0.54, to_date('20200101','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Truck Münchenstein'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1003, '8104' /*was S104 in CHE*/, 'AD26' /* was S026 in CHE*/, 1, 'ADN Truck Münchenstein', 0, '01', 'F103',
  null, null, 1, 'siggenthal', 'ADN Testdata, similar to Truck Münchenstein from CHE: S104/S026', 47.511546, 7.615005, 1, 150,
  0, null, 'TO', 0.54, 0.54, to_date('20200101', 'YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);

prompt 'ADN Truck CEM Plant BIS'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1004, '8105', 'AD26' , 1, 'ADN Truck CEM Plant BIS', 0, '01', 'F103',
  null, null, 0, null, 'ADN Testdata CEM Plant BIS', 47.511546, 7.615005, 1, 150,
  0, null, 'TO', 0.54, 0.54, to_date('20200101', 'YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0)
  
prompt 'ADN Rail Untervaz (SBB)'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1017, '8401' /* was S130 in CHE */, 'AD43' /* was S043 in CHE */, 1, 'ADN Rail Untervaz (SBB)', 1, '02', 'F101',
  -1862, '13', 5, null, 'ADN Testdta, similar to Rail Untervaz (SBB) from CHE: S130/S043', 46.91814482, 9.556217194, 0, 50,
  0, null, 'TO', 0.15, 0.15, to_date('20200101','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);
  
-- For Rail Untervaz there is no RETURN shippingpoint needed, because it is a PRODUCING plant:
-- Wagons will get newly loaded there, never just returned to their origin. In a way, they ARE at their origin....
-- However, for Muenchenstein: That's just a TERMINAL, i.e., wagons only arrive there loaded,
-- are unloaded, and are returned empty => needs an artificial return shippingpoint!

prompt 'ADN Rail Return From Münchenstein'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1188, '.AZ6' /* was .SP6 in CHE*/, 'L156' /* was L156 in CHE'*/, 0, 'ADN RF Münchenstein (01)' ,6, '02', null,
  -1037, '1', 0, null, 'ADN Testdata, similar to Rail Return From Münchenstein (01) from CHE: .SP6/L156', 47.40020832, 8.447799683, 0, 100,
  0, null, 'TO', 0, 0, to_date('99991231','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);
  
--- Shippingpoints for FVL, including Rail Return Shippingpoints

prompt 'ADN FVL Glattbrugg'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1083, '8VAD' /* was S106 in CHE */, 'A234' /* was S234 in CHE */, 1, 'ADN FVL Glattbrugg', 3, 'Z5', null,
  null, null, 0, null, null, 47.43099047, 8.559207916, 0, 250,
  0, null, 'TO', 0, 0, to_date('99991231','YYYYMMDD'), 'CHF',
  30, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);

prompt 'ADN FVL Altdorf'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1095, '8VAD' /* was S106 in CHE */, 'A246' /* was S246 in CHE */, 1,'ADN FVL Altdorf', 3, 'Z5', null,
  null, null, 0, null, null, 46.900169, 8.62237, 1, 100,
  0, null, 'TO', 0, 0, to_date('99991231','YYYYMMDD'), 'CHF',
  30, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);

prompt 'ADN RF Altdorf (01)'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1134, '.AZ6' /* was .SP6 in CHE */, 'L102', 0, 'ADN RF Altdorf (01)', 6, '02', null,
  -1543, '1', 0, null, null, 47.40020832, 8.447799683, 0, 100,
  0, null, 'TO', 0, 0, to_date('99991231','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);

prompt 'ADN RF Glattbrugg (11)'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1173, '.AZ6' /* was .SP6 in CHE */, 'L141', 0, 'ADN RF Glattbrugg (11)', 6, '02', null,
  -1379, '11', 0, null, null, 47.40020832, 8.447799683, 0, 100,
  0, null, 'TO', 0, 0, to_date('99991231','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);

-- uma 18.8.2011: Do not know why Glattbrugg uses 2 different Return-From Shippingpoints. Must be something special... Don't care, use both for now.

prompt 'ADN RF Glattbrugg (51)'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1174, '.AZ6' /* was .SP6 in CHE */, 'L142' /* was L142 in CHE */, 0, 'ADN RF Glattbrugg (51)', 6, '02', null,
  -1379, '51', 0, null, null, 47.40020832, 8.447799683, 0, 100,
  0, null, 'TO', 0, 0, to_date('99991231','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);
  
prompt 'ADN REP ADZA SBB'

--Based on LGNDHU1's dump: REP Basel SBB RB II
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-986, '.AZ5' /* was .SP5 in CHE */, 'L513', 0, 'ADN REP ADZA SBB', 5, '02', null,
  -1970, '82', 0, null, null, 47, 8, 1, 100,
  0, null, 'TO', 0, 0, to_date('99991231','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);
  
--- Shippingpoints for Container Terminal, including Rail Return Shippingpoints

--Based on LGNDHU1's dump: Container Terminal Birrfeld Rail
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1024, '8VAD' /* was S106 in CHE */, 'S037', 1, 'Container Terminal Birrfeld Rail', 2, '01', null,
  -1860, null, 4, null, null, 47.43841, 8.217398, 1, 50,
  0, null, 'TO', 0, 0, to_date('99991231','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);
  
--Based on LGNDHU1's dump: Road Landquart
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-998, '8VAD' /* was S106 in CHE */, 'S036', 1, 'Container Terminal Landquart Road', 2, '02', null,
  null, null, 4, null, null, 46.969, 9.554, 1, 100,
  0, null, 'TO', 0, 0, to_date('99991231','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);
  
-- TODO, Ticket ADN74269. Unfortunately, all Container in CHE seem to be used by Plants in the WESTERN part of switzerland.
-- Good and very common seems to be: Rail Eclepens, with Fluvio 4 --> Containerterminal Sion.
-- TODO: Add first Plant + necessary BP + Contract to represent Rail Eclepens :-(

--Not based on any real data:
prompt 'ADN Simple Vehicle Siggenthal'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-9999, '8101' /* was S101 in CHE*/, 'AD99' /*was S034 in CHE*/, 0, 'ADN Simple Vehicle Siggenthal', 8, '02', 'F101',
  -1408, '46', 5, null, 'ADN Testdata, not based on real data', 47.52087425, 8.240314722, 0, 50,
  0, null, 'TO', 0.39, 0.39, to_date('20200101','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);
  
-- Additional Shippingpoints for ADNZA TestData (LOGON-5806)
-- Associated to Plant "AD CEM Plant 8101" (Plant Code '8101')
prompt 'ADN Truck SIG Fenics - Plant 8101'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1200, '8101' /* was S101 in CHE*/, 'ADN1' /*was S034 in CHE*/, 1, 'ADN Truck SIG Fenics', 0, '01', 'REAL',
  null, null, 7, null, 'ADN Testdata configured to use LDS Push Mode', 47.52395346, 8.240432739, 1, 300,
  0, null, 'TO', 0.39, 0.39, to_date('20200101','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);
  
prompt 'ADN Truck SIG Parallel Plant 8101'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1201, '8101' /* was S101 in CHE*/, 'ADN2' /*was S034 in CHE*/, 1, 'ADN Truck SIG Parallel', 0, '01', 'REA2',
  null, null, 1, 'CE', 'ADN Testdata configured to use LDS Pull Mode', 47.52395346, 8.240432739, 1, 300,
  0, null, 'TO', 0.39, 0.39, to_date('20200101','YYYYMMDD'), 'CHF',
  null, '44001166', 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);
  
-- Additional Shippingpoints for ADNZA TestData (LOGON-5806)
-- Associated to Plant "AD AGG Plant " (Plant Code '8301')
prompt 'ADN Truck Fenics - Plant 8301'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1202, '8301' /* was S101 in CHE*/, 'ADN3' /*was S034 in CHE*/, 1, 'ADN Truck Fenics', 0, '01', 'REA3',
  null, null, 7, null, 'ADN Testdata configured to use LDS Push Mode', 47.52395346, 8.240432739, 1, 300,
  0, null, 'TO', 0.39, 0.39, to_date('20200101','YYYYMMDD'), 'CHF',
  null, null, 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);
  
prompt 'ADN Truck Parallel - Plant 8301'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1203, '8301' /* was S101 in CHE*/, 'ADN4' /*was S034 in CHE*/, 1, 'ADN Truck Parallel', 0, '01', 'REA4',
  null, null, 1, 'CE', 'ADN Testdata configured to use LDS Pull Mode', 47.52395346, 8.240432739, 1, 300,
  0, null, 'TO', 0.39, 0.39, to_date('20200101','YYYYMMDD'), 'CHF',
  null, null, 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);
  
-- Shippingpoint for the ZA plant 9101, in order to use it for cross-boundery delivery (e.g. LOGON-4947).
prompt 'ADN ZA 9101 - Truck'
insert into tlgnc_shippingpoint (
  shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, storage_location,
  railway_station_id, railway_station_loading_point, weigh_system_sp_cd, lds_server, shippingpoint_comment, coord_lat, coord_lon, coord_fix_flag, geofence_radius,
  fvl_mix_shipto_per_wagon_flag, send_sap_shippingpoint_id, uom_cd, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd,
  rail_arrival_warning_min, sbb_customer_number, timezone_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
values (-1301, '9101', 'ZA01', 1, 'ADN ZA 9101 - Truck', 0, '01', 'REA5',
  null, null, 1, 'CE', 'ADN Testdata configured to use LDS Pull Mode', 47.52395346, 8.240432739, 1, 300,
  0, null, 'TO', 3.0, 3.0, to_date('20200101','YYYYMMDD'), 'ZAR' /* try to test currency conversion as well, but low prio. If there are issues, switch to CHF! */,
  null, null, 'Europe/Paris', 1, systimestamp, 1, systimestamp, 0);
  
commit;
