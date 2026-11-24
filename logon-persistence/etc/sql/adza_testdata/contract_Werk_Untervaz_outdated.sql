--This file contains outdated contracts. That means, that the contact is not valid anymore.
--The source for this data is the stpo_based_on_dispocard_shipto_1209206_Werk_Untervaz_IUD.sql file

prompt Inserting into tlgns_sap_contract for SAP shipto ADN1209206
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-503, '1209206-03', 'UB', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -108393, -108394, null, null, to_date('20000101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('20091231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'S1', 'Test AdN 503-contr. outdated scen.', 1, 'CHF', 'DDP');

prompt Inserting BAG into tlgns_sap_contract_item for SAP shipto ADN1209206
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50301, -503, 10, -1027 /*Normo 4 Bag 25      */, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50302, -503, 20, -1065 /*Normo 5 R Bag 25 kg */, 0, 0); 
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50303, -503, 30, -2401 /*Albaro 5 R Sack 25  */, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50304, -503, 40, -2701 /*Optimo 4 Bag 25     */, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50305, -503, 50, -1049 /*Fortico 5R Sack 35  */, 0, 0);
prompt Inserting BULK into tlgns_sap_contract_item for SAP shipto ADN1209206
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50306, -503, 60, -1040 /* Fluvio 4 */ , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50307, -503, 70, -1054 /* Normo 4  */ , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50308, -503, 80, -1068 /* Normo 5R */ , 0, 0);

commit;
