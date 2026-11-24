--This file contains contract for testing Terminal Application order creation.

-----------------------------------------
-- Terminal Application Test contracts --
-----------------------------------------

-- Pickup test contracts
insert into tlgns_sap_contract 
(sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id                                                                      , soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat                                     , valid_until_dat                                    , valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no , item_grouping_type, currency_cd, incoterms_cd) values
(-508           , 'EXW ADN41'    , 'UB'                , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD03-20-20'), -40             , -41             , -40             , null           , to_date('20000101 00:00:00', 'YYYYMMDD HH24:MI:SS'), to_date('20301231 23:59:59', 'YYYYMMDD HH24:MI:SS'), 0                      , 'S1'                , 'EXW Terminal App Test', 1                 , 'CHF'      , 'EXW'       );

insert into tlgns_sap_contract 
(sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id                                                                      , soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat                                    , valid_until_dat                                   , valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no , item_grouping_type, currency_cd, incoterms_cd) values 
(-509           , 'DDP ADN41'    , 'UB'                , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD03-20-20'), -40             , -41             , -40             , null           , to_date('20000101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('20301231 23:59:59','YYYYMMDD HH24:MI:SS'), 0                      , 'S1'                , 'DDP Terminal App Test', 1                 , 'CHF'      , 'DDP'       );

-- Bulk pickup test contract items
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id       , transport_cost_included_flag, is_ap_group_leader) values
(-50801              , -508           , -50801          , -1040 /* Fluvio 4 */  , 0                           , 0                 );

insert into tlgns_sap_contract_item 
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id     , transport_cost_included_flag, is_ap_group_leader) values 
(-50901              , -509           , -50901          , -1040 /* Fluvio 4 */, 0                           , 0                 );

-- Bag pickup test contract items
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id                    , transport_cost_included_flag, is_ap_group_leader) values
(-50802              , -508           , -50802          , -1065 /* ADN Normo 5 R Bag 25 kg */, 0                           , 0                 );

insert into tlgns_sap_contract_item 
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id                    , transport_cost_included_flag, is_ap_group_leader) values 
(-50902              , -509           , -50902          , -1065 /* ADN Normo 5 R Bag 25 kg */, 0                           , 0                 );

-- Service material test contract items
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id               , transport_cost_included_flag, is_ap_group_leader) values
(-50803              , -508           , -50803          , -6003 /* ADN Test Service 3 */, 0                           , 0                 );

insert into tlgns_sap_contract_item 
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id               , transport_cost_included_flag, is_ap_group_leader) values 
(-50903              , -509           , -50903          , -6003 /* ADN Test Service 3 */, 0                           , 0                 );

-- Palette material test contract items
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id           , transport_cost_included_flag, is_ap_group_leader) values
(-50804              , -508           , -50804          , -3066 /* Palette perdue */, 0                           , 0                 );

insert into tlgns_sap_contract_item 
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id           , transport_cost_included_flag, is_ap_group_leader) values 
(-50904              , -509           , -50904          , -3066 /* Palette perdue */, 0                           , 0                 );

