-- select * from vlgns_soldship_flex_srch where composite_search_string like '%lbula%';

-- working with "Kieswerk Albula AG", soldto-shipto 73161 - 1209037
--
-- 3 sales areas:
--   S101-10-10 (CH) <-- Work mainly on this
--   S160-10-10 (D)           (no shipto/soldto spec conditions for this sorg)
--   S170-10-10 (Vorarlberg)  (no shipto/soldto spec conditions for this sorg)
--
-- soldto_sap_bp_id = 32816 (careful - logon PK!)
-- SHIPTO_SAP_BP_ID =  1631 (careful - logon PK!)

-- select * from tlgns_sap_mat_cond where soldto_sap_bp_id = 32816 and SHIPTO_SAP_BP_ID is null;
-- no "soldto" generic conditions. Only soldto AND shipto!

-- I.e., we have the case which corresponds to EBMS "standard case": 100% shipto specific contracts
-- select * from tlgns_sap_mat_cond where soldto_sap_bp_id = 32816 and SHIPTO_SAP_BP_ID = 1631
-- order by sap_material_id;
-- 11 conditions

-- select * from tlgns_sap_mat_cond where soldto_sap_bp_id = 32816 and SHIPTO_SAP_BP_ID = 1631
-- order by valid_from_dat, valid_to_dat, kdkg2_sap_trsp_mode_cd, sap_material_id;

-- 6 valid 1.1.2009 - 31.12.2010
-- 5 valid 1.1.2011 - 31.12.9999
-- ==> minimally TWO contracts needed
-- 2 different kdkg2_sap-trsp_mode_cd in each contract => in total FOUR contracts needed.

-- CREATING THE TEST CONTRACTS
-- - Contract Type: Use ZCQ (Agreement with customer with fixed prices) or ZCF (... without prices fixed)
-- - sarea: 1000 = S101-10-10
-- - Shipping cond for CHE: E1 = Truck, S1 = SBB (These may change in EBM). Mapping kdkg2_sap_trsp_mode_cd
--   10 --> E1
--   50 --> S1
--   00 --> E1 (Pickup is also considered as Truck)
-- - If shipping cond is '00', set to incoterm to 'EXW'. In all other cases, use 'DDP' for now.
-- ==> created the 4 contracts 101..104

-- CREATING THE TEST CONTRACT ITEMS
-- - leave all fields "prod plant", "supply plant", value, etc. empty for now. They are not mandatory!
-- Contract 1 and 2 - split by kdkg2_sap_trsp_mode_cd, into contract 101 and 102
--   1040 10    -> 1010001 10
--   1052 10    -> 1010002 20
--   1054 10    -> 1010003 30
--   1057 10    -> 1010004 40
--   1054    50 -> 1020001 10
--   1454 10    -> 1010005 50

-- Contract 1 and 2 - split by kdkg2_sap_trsp_mode_cd, into contract 101 and 102
--   1040 10
--   1052 10
--   1054 10
--   1057 10
--   1054    50

-- CAREFUL: Note that the naming of the contract number (10101 = 101-01) must NOT be used by the application!
-- It is just that it's easier to give (static) numbers, i.e. managing the ID-range without sequences!
-- We are constructing TEST data here, therefore, humans should be able to generate/modify it easily
-- 
-- Possibly Optimization: Write PL/SQL "insert-or-update" scripts, which take
-- "business"-identifiers, to update/insert contracts + contract items.
-- But that seems to be too much logic at the wrong place (at least for now).

prompt Inserting into tlgns_sap_contract for SAP shipto 1209037
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-101, '1209037-01', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -32816, -1631, null, null, to_date('20090101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('20101231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Test AdN 101 -Albula- CHE truck OLD', 1, 'CHF', 'DDP');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-102, '1209037-02', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -32816, -1631, null, null, to_date('20090101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('20101231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'S1', 'Test AdN 102 -Albula- CHE rail OLD' , 1, 'CHF', 'DDP');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-103, '1209037-03', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -32816, -1631, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Test AdN 103 -Albula- CHE truck'  , 1, 'CHF', 'DDP');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-104, '1209037-04', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -32816, -1631, null, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'S1', 'Test AdN 104 -Albula- CHE rail'     , 1, 'CHF', 'DDP');
-- contract for Albula in sales area AD02-20-11 (for testing authorization on sales area)
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-105, '1209037-05', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='11')), -32816, -1631, null, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Test AdN 105 -Albula- CHE truck'    , 1, 'CHF', 'DDP');
-- contract for Albula in sales area AD01-20-11 (for testing authorization on company code)
-- This is a shipto-soldto contract. But only usable in sales area AD01-20-11.
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-106, '1209037-06', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD01' and sa.distribution_channel='20' and sa.division_cd='11')), -32816, -32816, null, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Test AdN 106 -Albula- CHE truck'    , 1, 'CHF', 'DDP');
-- Contract for ADN Cement Suid-Afrika/ADN Ellis Park Rugby Stadium/Fifa
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-107, '0000021-01', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='ZA01' and sa.distribution_channel='10' and sa.division_cd='10')), -21, -20, null, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Test AdN 107 -ADN20- ZA01 truck'    , 1, 'EUR', 'DDP');

--Contract containing pallet dedicated to LRQ 018.0 autoadd pallet feature NB test
--The contract is not comimg from the original swiss data, it is fake
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-100, 'PALTEST001', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='11')), -32816, -32816, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Pallet test road direct'    , 1, 'CHF', 'DDP');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-99, 'PALTEST002', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='11')), -32816, -32816, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Pallet test pickup'    , 1, 'CHF', 'EXW');

--Contracts to test deleted materials are NOT available
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-156, 'MATDEL001', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='11')), -32816, -32816, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Material deleted in sarea'    , 1, 'CHF', 'DDP');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-157, 'MATDEL002', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='11')), -32816, -32816, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Mat deleted pickup'    , 1, 'CHF', 'EXW');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-160, 'MATDEL003', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='11')), -32816, -32816, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Mat deleted in sarea pickup'    , 1, 'CHF', 'EXW');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-161, 'MATDEL004', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='11')), -32816, -32816, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Mat deleted'    , 1, 'CHF', 'DDP');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-158, 'MATDEL005', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -32816, -32816, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Mat deleted in other sarea'    , 1, 'CHF', 'DDP');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-159, 'MATDEL006', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -32816, -32816, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Mat deleted pickup'    , 1, 'CHF', 'DDP');

prompt Inserting into tlgns_sap_contract_item for SAP shipto ADN1209037
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10101, -101, 10, -1040, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10102, -101, 20, -1052, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10103, -101, 30, -1054, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10104, -101, 40, -1057, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10105, -101, 50, -1454, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10201, -102, 10, -1054, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10301, -103, 10, -1040, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10302, -103, 20, -1052, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10303, -103, 30, -1054, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10304, -103, 40, -1057, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10401, -104, 10, -1054, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10501, -105, 10, -1040, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10601, -106, 10, -1040, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10701, -107, 10, -1040, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10702, -107, 20, -1052, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10703, -107, 30, -1054, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10704, -107, 40, -1057, 0, 0);

--LRQ 018.0 prepared contract
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10106, -100, 10, -1066, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10107, -100, 20, -1067, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10108, -100, 30, -3046, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10109, -99, 10, -1066, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10110, -99, 20, -1067, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10111, -99, 30, -3046, 0, 0);

--Contracts to test deleted materials are NOT available
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10112, -156, 10, -1067, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10113, -157, 10, -1067, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10114, -156, 20, -1070, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10115, -157, 20, -1069, 0, 0);

insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10116, -161, 10, -1067, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10117, -160, 10, -1067, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10118, -161, 20, -1069, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10119, -160, 20, -1070, 0, 0);

insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10120, -158, 10, -1067, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10121, -159, 10, -1067, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10122, -158, 20, -1070, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-10123, -159, 20, -1069, 0, 0);



commit;
