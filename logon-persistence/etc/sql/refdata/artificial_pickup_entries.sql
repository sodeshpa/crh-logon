-- $Revision$
-- $Date$
--
--Origin of this: Ticket ADN75373

-- The IDs must correspond to the ones used in JAVA, i.e.
--   ARTIFICIAL_PICKUP_CARRIER_ID
--   ARTIFICIAL_PICKUP_POOL_ID
--   ARTIFICIAL_PICKUP_BAG_TRUCK_ID
--   ARTIFICIAL_PICKUP_BULK_TRUCK_ID

-- Besides the "Pickup" carrier, we also have the "generic" carrier: ANY could take this order, update info comes from LDS interface. See ticket LOGON-5132

prompt INSERTING into TLGNS_SAP_VENDOR;
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number, vn_is_comm_carrier, vn_is_exec_carrier, vn_is_internal) 
values (1, 'LOGON00001', 'LOGON00001', null, '0', 'Pickup', null, null, null, null, null, null, null, null, 'en', null, 1, 1, 0);

prompt INSERTING into TLGNS_SAP_VENDOR;
insert into tlgns_sap_vendor (sap_vendor_id, sap_id, sap_ifc_id, account_group, marked_for_deletion, name1, name2, country_cd, postal_code, city, location_district, region, street, house_number, lang_cd, vat_registration_number, vn_is_comm_carrier, vn_is_exec_carrier, vn_is_internal) 
values (2, 'LOGON00002', 'LOGON00002', null, '0', 'Generic / Any', null, null, null, null, null, null, null, null, 'en', null, 1, 0 /* must NOT be an executing carrier */, 0);

prompt INSERTING into TLGNC_CARRIER
insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag,CARRIER_REQUIRES_PLANNING_FLAG) 
values (1, 'Pickup', 1, 1, null, 0, 0);

prompt INSERTING into TLGNC_CARRIER
insert into tlgnc_carrier (carrier_id, display_name, active_flag, sap_vendor_id, bag_stock_shippingpoint_id, transmit_to_carrier_flag,CARRIER_REQUIRES_PLANNING_FLAG) 
values (2, 'Generic / Any', 1, 2, null, 0, 0);

prompt INSERTING into TLGNC_POOL
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd) values (10, '00', 1);
insert into tlgnc_pool
(pool_id, sap_trsp_mode_cd, ctl_act_cd) values
(11     , '00'            , 1         );
insert into tlgnc_pool
(pool_id, sap_trsp_mode_cd, ctl_act_cd) values
(12     , '00'            , 1         );

-- For Generic carrier: No explicit pool assignment. Can be added to anything where wanted.

prompt INSERTING into TLGNC_POOL_CARRIER
insert into tlgnc_pool_carrier (pool_carrier_id, pool_id, carrier_id) values (slgnc_pool_carrier.nextval, 10, 1);
insert into tlgnc_pool_carrier
(pool_carrier_id           , pool_id, carrier_id, ctl_cre_uid, ctl_cre_ts  , ctl_mod_uid, ctl_mod_ts  ) values
(slgnc_pool_carrier.nextval, 11     , 1         , 1          , systimestamp, 1          , systimestamp);
insert into tlgnc_pool_carrier
(pool_carrier_id           , pool_id, carrier_id, ctl_cre_uid, ctl_cre_ts  , ctl_mod_uid, ctl_mod_ts  ) values
(slgnc_pool_carrier.nextval, 12     , 1         , 1          , systimestamp, 1          , systimestamp);

-- prompt INSERTING into TLGNC_RFIDOBJECT - Not nessary - will be created by an automated job
-- insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-10, 4, 'PICKUPBAG');
-- insert into tlgnc_rfidobject (rfidobject_id, rfidobject_type_cd, object_label) values (-9, 4, 'PICKUPBULK');

prompt INSERTING into TLGNC_SIMPLE_VEHICLE
insert into tlgnc_simple_vehicle
(simple_vehicle_id, carrier_id, active_flag, priority, vehicle_name  , vehicle_reg_no, sap_vehicle_no, simple_vehicle_type_cd, vehicle_unit_count, max_gross_weight_t, tare_weight_t, volume_m3, branding_flag, nr_chambers, sap_vehicle_is_internal_flag, vehicle_comment                                                                  , homebase_coord_lat, homebase_coord_lon) values
(1                , 1         , 1          , 1       , 'Pickup ship' , 'Pickup ship' , ''            , 1                     , 0                 , 5000              , 100          , 1000     , ''           , 1          , 0                           , 'Pickup artificial ship for logon pickup orders. Please do not modify its data!' , ''                , ''                );
insert into tlgnc_simple_vehicle
(simple_vehicle_id, carrier_id, active_flag, priority, vehicle_name        , vehicle_reg_no      , sap_vehicle_no, simple_vehicle_type_cd, vehicle_unit_count, max_gross_weight_t, tare_weight_t, volume_m3, branding_flag, nr_chambers, sap_vehicle_is_internal_flag, vehicle_comment                                                                        , homebase_coord_lat, homebase_coord_lon) values
(2                , 1         , 1          , 1       , 'Pickup Blocktrain' , 'Pickup Blocktrain' , ''            , 2                     , 0                 , 5000              , 100          , 1000     , ''           , 1          , 0                           , 'Pickup artificial blocktrain for logon pickup orders. Please do not modify its data!' , ''                , ''                );

prompt INSERTING into TLGNC_TRUCK
insert into tlgnc_truck (truck_id, carrier_id, active_flag, priority, truck_name, truck_plate_no, truck_transport_cat_cd, truck_type_cd, manufacturer, axis_count, euro_norm, max_gross_weight_t, tare_weight_t, has_dustfilter_flag, volume_m3, branding_flag, nr_chambers, max_net_weight_chamber1_t, max_net_weight_chamber2_t, truck_comment, pda_available_flag, homebase_coord_lat, homebase_coord_lon, rfidobject_id, dispatching_carrier_id, sap_vehicle_char_cd, sap_vehicle_no, sap_vehicle_is_internal_flag) 
values (1, 1, 1, 99, 'Pickup Bag Truck', 'PICKUP BAG', 4, 1, null, null, null, 100, 0, 0, 0, 0, 0, 0, 0, 'Pickup artificial truck for logon pickup orders. Please do not modify its data!', 0, null, null, /* -10 not needed */ null, null, 0, 'ADN_SAP_NO', 0);
insert into tlgnc_truck (truck_id, carrier_id, active_flag, priority, truck_name, truck_plate_no, truck_transport_cat_cd, truck_type_cd, manufacturer, axis_count, euro_norm, max_gross_weight_t, tare_weight_t, has_dustfilter_flag, volume_m3, branding_flag, nr_chambers, max_net_weight_chamber1_t, max_net_weight_chamber2_t, truck_comment, pda_available_flag, homebase_coord_lat, homebase_coord_lon, rfidobject_id, dispatching_carrier_id, sap_vehicle_char_cd, sap_vehicle_no, sap_vehicle_is_internal_flag) 
values (2, 1, 1, 99, 'Pickup Bulk Truck', 'PICKUP BULK', 1, 1, null, null, null, 100, 0, 0, 0, 0, 2, 50, 50, 'Pickup artificial truck for logon pickup orders. Please do not modify its data!', 0, null, null, /* -9 not needed */ null, null, 0, 'ADN_SAP_NO', 0);
--- The "bulk dumped" pickup truck was added later, hence it has ID 5 not to disturb the
--- original truck ID sequence. (The IDs of these pickup trucks are hardcoded in Constant.java.)
insert into tlgnc_truck (truck_id, carrier_id, active_flag, priority, truck_name, truck_plate_no, truck_transport_cat_cd, truck_type_cd, manufacturer, axis_count, euro_norm, max_gross_weight_t, tare_weight_t, has_dustfilter_flag, volume_m3, branding_flag, nr_chambers, max_net_weight_chamber1_t, max_net_weight_chamber2_t, truck_comment, pda_available_flag, homebase_coord_lat, homebase_coord_lon, rfidobject_id, dispatching_carrier_id, sap_vehicle_char_cd, sap_vehicle_no, sap_vehicle_is_internal_flag) 
values (5, 1, 1, 99, 'Pickup Bulk Dumped Truck', 'PICKUP BULK DUMPED', 5, 1, null, null, null, 100, 0, 0, 0, 0, 2, 50, 50, 'Pickup artificial truck for logon pickup orders. Please do not modify its data!', 0, null, null, /* -9 not needed */ null, null, 0, 'ADN_SAP_NO', 0);

prompt INSERTING into TLGNC_TRUCK
insert into tlgnc_truck (truck_id, carrier_id, active_flag, priority, truck_name, truck_plate_no, truck_transport_cat_cd, truck_type_cd, manufacturer, axis_count, euro_norm, max_gross_weight_t, tare_weight_t, has_dustfilter_flag, volume_m3, branding_flag, nr_chambers, max_net_weight_chamber1_t, max_net_weight_chamber2_t, truck_comment, pda_available_flag, homebase_coord_lat, homebase_coord_lon, rfidobject_id, dispatching_carrier_id, sap_vehicle_char_cd, sap_vehicle_no, sap_vehicle_is_internal_flag) 
values (3, 2, 1, 99, 'Generic Carrier Bag Truck', 'GEN TRUCK BAG', 4, 1, null, null, null, 100, 0, 0, 0, 0, 0, 0, 0, 'Artificial truck for GENERIC/ANY carrier. Please do not modify its data!', 0, null, null, /* -10 not needed */ null, null, 2, 'ADN_SAP_NO', 0);
insert into tlgnc_truck (truck_id, carrier_id, active_flag, priority, truck_name, truck_plate_no, truck_transport_cat_cd, truck_type_cd, manufacturer, axis_count, euro_norm, max_gross_weight_t, tare_weight_t, has_dustfilter_flag, volume_m3, branding_flag, nr_chambers, max_net_weight_chamber1_t, max_net_weight_chamber2_t, truck_comment, pda_available_flag, homebase_coord_lat, homebase_coord_lon, rfidobject_id, dispatching_carrier_id, sap_vehicle_char_cd, sap_vehicle_no, sap_vehicle_is_internal_flag) 
values (4, 2, 1, 99, 'Generic Carrier Bulk Truck', 'GEN TRUCK BULK', 1, 1, null, null, null, 100, 0, 0, 0, 0, 2, 50, 50, 'Artificial truck for GENERIC/ANY carrier. Please do not modify its data!', 0, null, null, /* -9 not needed */ null, null, 2, 'ADN_SAP_NO', 0);

-- Responsibility of each Cluster's initialdata: Add references to this carrier for the relevant Purchase Organisations! See:
-- - adza_initialdata/artificial_pickup_entries.sql
-- - fbnl_initialdata/artificial_pickup_entries.sql
-- - cl1_initialdata/artificial_pickup_entries.sql

prompt INSERTING into TLGNC_POOL_TX - EN
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'en', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'en', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'en', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - DE
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'de', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'de', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'de', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - FR
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'fr', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'fr', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'fr', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - IT
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'it', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'it', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'it', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - ES
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'es', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'es', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'es', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - CS
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'cs', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'cs', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'cs', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - SK
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'sk', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'sk', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'sk', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - HU
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'hu', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'hu', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'hu', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - RO
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'ro', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'ro', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'ro', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - BG
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'bg', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'bg', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'bg', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - RU
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'ru', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'ru', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'ru', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - PL
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'pl', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'pl', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'pl', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - AZ
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'az', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'az', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'az', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - HR
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'hr', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'hr', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'hr', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - SR
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'sr', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'sr', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'sr', '__BlocktrainPickup', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - TR
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'tr', '_Pickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'tr', '_ShipPickup', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'tr', '_BlocktrainPickup', 1, systimestamp, 1, systimestamp);

commit;

