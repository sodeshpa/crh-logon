--This file contains a simple contract for ADN One-Time Shipto (LNF).
prompt Inserting into tlgns_sap_contract for SAP shipto ADN23
insert into tlgns_sap_contract
(sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id                                                                      , soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat                                    , valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no        , item_grouping_type, currency_cd, incoterms_cd) values
(-507           , '23-01'        , 'UB'                , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11'), -32816          , -23             , -32816          , null           , sysdate - 1   , to_date('20301231 23:59:59', 'YYYYMMDD HH24:MI:SS'), 0                      , 'S1'                , 'Test AdN 507-onetime shipto.', 1                 , 'CHF'      , 'DDP'       );

prompt Inserting BAG into tlgns_sap_contract_item for SAP shipto ADN23
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id                , transport_cost_included_flag, is_ap_group_leader) values
(-50701              , -507           , 10              , -1027 /*Normo 4 Bag 25 */      , 0                           , 0                 );

prompt Inserting BULK into tlgns_sap_contract_item for SAP shipto ADN23
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id      , transport_cost_included_flag, is_ap_group_leader) values
(-50702              , -507           , 20              , -1040 /* Fluvio 4 */ , 0                           , 0                 );

--insert landfill contracts to sales area AD02-10-30
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-127, 'LNF_LF1', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='30')), -32816, -23, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'LF contract 1'    , 1, 'CHF', 'DDP');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-128, 'LNF_LF2', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='30')), -32816, -23, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'LF contract 2'    , 1, 'CHF', 'DDP');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-129, 'LNF_LF3', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='30')), -32816, -23, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'LF contract 3'    , 1, 'CHF', 'DDP');

--insert aggregate contracts to sales area AD02-10-30
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-130, 'LNF_AGG1', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='30')), -32816, -23, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'AGG contract 1'    , 1, 'CHF', 'DDP');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-131, 'LNF_AGG2', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='30')), -32816, -23, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'AGG contract 2'    , 1, 'CHF', 'DDP');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-132, 'LNF_AGG3', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='30')), -32816, -23, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'AGG contract 3'    , 1, 'CHF', 'DDP');

--insert landfill contract items to sales area AD02-10-30
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11164, -127, 10, -5010, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11165, -128, 10, -5010, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11166, -129, 10, -5010, 0, 0);

--insert aggregate contract items to sales area AD02-10-30
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11167, -130, 10, -4060, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11168, -131, 10, -4060, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11169, -132, 10, -4060, 0, 0);

commit;
