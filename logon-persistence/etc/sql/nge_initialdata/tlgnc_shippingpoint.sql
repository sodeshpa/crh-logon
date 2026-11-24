-- Based on:
-- ticket 56040
--   /share/doc/crh/logon_nge/input/data/WEWAS_SAP_Werke_20100525.txt
-- and the fact that shippingpoints all have the same codes than the SAP code of their related plants for NGE
--
-- uma 19.8.2010: All coordinates are WRONG, they are actually Hamburg mainstation... ;-) But at least order taking is possible.
prompt INSERTING into TLGNC_SHIPPINGPOINT

insert into tlgnc_shippingpoint (shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, weigh_system_sp_cd, lds_server, coord_lat, coord_lon, coord_fix_flag, geofence_radius, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd) values (
  1000000, '15A0', '15A0', 1, 'Truck Lägerdorf',          0, 'E1', 6, null, 53.55326081, 10.00833035, 0, 50, 0, 0, TO_DATE('99991231', 'YYYYMMDD'), 'EUR');
insert into tlgnc_shippingpoint (shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, weigh_system_sp_cd, lds_server, coord_lat, coord_lon, coord_fix_flag, geofence_radius, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd) values (
  1000001, '15A2', '15A2', 1, 'Truck Rostock',            0, 'E1', 6, null, 53.55326081, 10.00833035, 0, 50, 0, 0, TO_DATE('99991231', 'YYYYMMDD'), 'EUR');
insert into tlgnc_shippingpoint (shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, weigh_system_sp_cd, lds_server, coord_lat, coord_lon, coord_fix_flag, geofence_radius, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd) values (
  1000002, '15A3', '15A3', 1, 'Truck Höver',              0, 'E1', 6, null, 53.55326081, 10.00833035, 0, 50, 0, 0, TO_DATE('99991231', 'YYYYMMDD'), 'EUR');
insert into tlgnc_shippingpoint (shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, weigh_system_sp_cd, lds_server, coord_lat, coord_lon, coord_fix_flag, geofence_radius, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd) values (
  1000003, '25A0', '25A0', 1, 'Truck Bremen',             0, 'E1', 6, null, 53.55326081, 10.00833035, 0, 50, 0, 0, TO_DATE('99991231', 'YYYYMMDD'), 'EUR');
insert into tlgnc_shippingpoint (shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, weigh_system_sp_cd, lds_server, coord_lat, coord_lon, coord_fix_flag, geofence_radius, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd) values (
  1000004, '15A9', '15A9', 1, 'Truck Lübeck',             0, 'E1', 6, null, 53.55326081, 10.00833035, 0, 50, 0, 0, TO_DATE('99991231', 'YYYYMMDD'), 'EUR');
insert into tlgnc_shippingpoint (shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, weigh_system_sp_cd, lds_server, coord_lat, coord_lon, coord_fix_flag, geofence_radius, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd) values (
  1000005, '15AA', '15AA', 1, 'Truck Ostermoor',          0, 'E1', 6, null, 53.55326081, 10.00833035, 0, 50, 0, 0, TO_DATE('99991231', 'YYYYMMDD'), 'EUR');
insert into tlgnc_shippingpoint (shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, weigh_system_sp_cd, lds_server, coord_lat, coord_lon, coord_fix_flag, geofence_radius, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd) values (
  1000006, '15AB', '15AB', 1, 'Truck Terminal Harmina',   0, 'E1', 6, null, 53.55326081, 10.00833035, 0, 50, 0, 0, TO_DATE('99991231', 'YYYYMMDD'), 'EUR');
insert into tlgnc_shippingpoint (shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, weigh_system_sp_cd, lds_server, coord_lat, coord_lon, coord_fix_flag, geofence_radius, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd) values (
  1000007, '25A1', '25A1', 1, 'Truck Salzgitter',         0, 'E1', 6, null, 53.55326081, 10.00833035, 0, 50, 0, 0, TO_DATE('99991231', 'YYYYMMDD'), 'EUR');  
insert into tlgnc_shippingpoint (shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, weigh_system_sp_cd, lds_server, coord_lat, coord_lon, coord_fix_flag, geofence_radius, var_handling_cost_mat, future_var_handling_cost_mat, future_valid_from_dat, currency_cd) values (
  1000008, '25A5', '25A5', 1, 'Truck Terminal Bremen',    0, 'E1', 6, null, 53.55326081, 10.00833035, 0, 50, 0, 0, TO_DATE('99991231', 'YYYYMMDD'), 'EUR');

-- Unfortunately the list WEWAS_SAP_Werke_20100525.txt is not complete, it only lists the WeWaS plants.
-- But there are others, NOT known to WeWaS, but just used in SAP, relevant for other use cases.

-- There was big discussion whether we should have plant '25A3' or not, see ticket 57242. The current (26.7.2010) approach is: Do NOT have it, but instead map 25A3 in condition import to 25A1.
-- FIXME uma, 26.7.2010: verify at end of release: If this changes, please also add a shippinpoint for it!

-- 25A4 is actually NOT a real shippingpoint, NGE only uses it as a plant for deliveries TO it, see Ticket 57242 / Mail from A.Horstmann, 22.7.2010.
--   Plant 25A4 is a technical in SAP (mill in Hoever), but without weighting system behind.
--   For us it's used as receiving point and for quantities we take from credit notes of our customer.
-- For Logon DB it is probably easier to have plant == shippingpoint for ALL plants in NGE.
-- They simply not be used in reality, but that's not a problem, and probably less confusing.
insert into tlgnc_shippingpoint (shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, weigh_system_sp_cd, lds_server, coord_lat, coord_lon, coord_fix_flag, geofence_radius) values (
  1000009, '25A4', '25A4', 1, 'Truck Höver (Mühle)',      0, 'E1', 6, null, 53.55326081, 10.00833035, 0, 50);

-- 25B4, Mail from Andreas Horstmann, 25.7.2010:
--   Plant 25B4 (power plant in Rostock) is not completely out of scope. It is one of the plants relevant for fly ash business. Therefore Logon will not
--   deliver any delivery note information in the WeWaS flatfile format used for cement deliveries. But there will be customer, ship to master data (sales
--   org. DE02) and freight condition records (ZKF2 out of table A694 and as well for sales org. DE02), to be able, to create Logon orders to which the
--   truck driver (as stated in some mail before) introduces manually (via handheld) the weight information he receives from the power plant weighting bridge.
insert into tlgnc_shippingpoint (shippingpoint_id, plant_cd, sap_shippingpoint_no, known_also_in_sap_flag, shippingpoint_name, shipppoint_type_cd, sap_shipping_cond_cd, weigh_system_sp_cd, lds_server, coord_lat, coord_lon, coord_fix_flag, geofence_radius) values (
  1000010, '25B4', '25B4', 1, 'Rostock (Kraftwerk)',      0, 'E1', 6, null, 53.55326081, 10.00833035, 0, 50);

commit;
