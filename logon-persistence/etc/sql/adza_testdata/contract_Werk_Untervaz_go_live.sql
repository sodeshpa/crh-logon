--This file contains contracts for the go-live scenario. That means, that the contact validity is starts in the future.
--(Therefore you will only see these materials in the Master Data, but not in the Call Center!)
--The source for this data is the stpo_based_on_dispocard_shipto_1209206_Werk_Untervaz_IUD.sql file

prompt Inserting into tlgns_sap_contract for SAP shipto ADN1209206
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-502, '1209206-02', 'UB', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -108393, -108394, null, null, to_date('20300101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'S1', 'Test AdN 502 -go live scenario', 1, 'CHF', 'DDP');

prompt Inserting BAG into tlgns_sap_contract_item for SAP shipto ADN1209206
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50201, -502, 10, -1027 /*Normo 4 Bag 25      */, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50202, -502, 20, -1065 /*Normo 5 R Bag 25 kg */, 0, 0); 
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50203, -502, 30, -2401 /*Albaro 5 R Sack 25  */, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50204, -502, 40, -2701 /*Optimo 4 Bag 25     */, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50205, -502, 50, -1049 /*Fortico 5R Sack 35  */, 0, 0);
prompt Inserting BULK into tlgns_sap_contract_item for SAP shipto ADN1209206
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50206, -502, 60, -1040 /* Fluvio 4 */ , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50207, -502, 70, -1054 /* Normo 4  */ , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50208, -502, 80, -1068 /* Normo 5R */ , 0, 0);

commit;
