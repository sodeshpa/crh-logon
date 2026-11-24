--This file contains a simple contract for ADN One-Time Soldto.

prompt Inserting into tlgns_sap_contract for SAP shipto ADN22
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id                                                                      , soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat                                   , valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no        , item_grouping_type, currency_cd, incoterms_cd) values 
                               (-505           , '22-01'        , 'UB'                , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11'), -22             , -30             , -222            , null           , sysdate - 1   , to_date('20301231 23:59:59','YYYYMMDD HH24:MI:SS'), 0                      , 'S1'                , 'Test AdN 505-onetime soldto.', 1                 , 'CHF'      , 'DDP'       );
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id                                                                      , soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat                                    , valid_until_dat                                   , valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no        , item_grouping_type, currency_cd, incoterms_cd) values 
                               (-506           , '22-02'        , 'UB'                , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11'), -22             , -37             , null            , null           , sysdate - 1   , to_date('20301231 23:59:59','YYYYMMDD HH24:MI:SS'), 0                      , 'S1'                , 'Test AdN 505-onetime soldto.', 1                 , 'CHF'      , 'DDP'       );

prompt Inserting BAG into tlgns_sap_contract_item for SAP shipto ADN22
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id                , transport_cost_included_flag, is_ap_group_leader) values
(-50501              , -505           , 10              , -1027 /*Normo 4 Bag 25 */      , 0                           , 0                 );
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id                , transport_cost_included_flag, is_ap_group_leader) values
(-50601              , -506           , 10              , -1027 /*Normo 4 Bag 25 */      , 0                           , 0                 );
prompt Inserting BULK into tlgns_sap_contract_item for SAP shipto ADN22

insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id     , transport_cost_included_flag, is_ap_group_leader) values
(-50502              , -505           , 20              , -1040 /* Fluvio 4 */, 0                           , 0                 );
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id     , transport_cost_included_flag, is_ap_group_leader) values
(-50602              , -506           , 20              , -1040 /* Fluvio 4 */, 0                           , 0                 );


commit;
