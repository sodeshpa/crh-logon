-- This shipto had been added as a 'clone' of 1209206 Werk Untervaz, but in had been adapted for the LRQ 279.2 Export invoice use case later on. The data
-- is absolutely not verified by a business analyst, so please do not expect it to be 100% valid. All what is made sure that there is a ZCEX contract for this use case
-- see also LOGON-4617
prompt Inserting into tlgns_sap_contract for SAP shipto ADN2209206
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-5011, '2209206-01', 'UB', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -208393, -208394, null, null, to_date('20110101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'S1', 'Test AdN5011 -werk UV STPO-CHE rail', 1, 'CHF', 'DDP');

prompt Inserting BAG into tlgns_sap_contract_item for SAP shipto ADN2209206
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-501011, -5011, 10, -1027 /*Normo 4 Bag 25      */, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-501021, -5011, 20, -1065 /*Normo 5 R Bag 25 kg */, 0, 0); 
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-501031, -5011, 30, -2401 /*Albaro 5 R Sack 25  */, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-501041, -5011, 40, -2701 /*Optimo 4 Bag 25     */, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-501051, -5011, 50, -1049 /*Fortico 5R Sack 35  */, 0, 0);

prompt Inserting BULK into tlgns_sap_contract_item for SAP shipto ADN2209206
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-501061, -5011, 60, -1040 /* Fluvio 4 */ , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-501071, -5011, 70, -1054 /* Normo 4  */ , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-501081, -5011, 80, -1068 /* Normo 5R */ , 0, 0);

prompt Inserting SERVICE into tlgns_sap_contract_item for SAP shipto ADN2209206
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-501082, -5011, 90, -9992 /* ADN Test service */ , 0, 0);

prompt Inserting LANDFILL into tlgns_sap_contract_item for SAP shipto ADN2209206
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-5001083, -5011, 100, -5010, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-5001084, -5011, 110, -5010, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-5001085, -5011, 120, -5010, 0, 0);

prompt Inserting AGGREGATE into tlgns_sap_contract_item for SAP shipto ADN2209206
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-5001086, -5011, 130, -4060, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-5001087, -5011, 140, -4060, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-5001088, -5011, 150, -4060, 0, 0);


insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-5012, 'ZCEX_1', 'ZCEX', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -208393, -208394, null, null, to_date('20110101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'S1', 'ZCEX contract', 1, 'CHF', 'DDP');

insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id,target_quantity, target_quantity_uom_cd, net_value, vat_percentage, transport_cost_included_flag, is_ap_group_leader) values (-501089, -5012, 10, -1027, 0.025, 'TO', 25, 8.0  /*Normo 4 Bag 25      */, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id,target_quantity, target_quantity_uom_cd, net_value, vat_percentage, transport_cost_included_flag, is_ap_group_leader) values (-501090, -5012, 20, -1065, 1, 'BAG', 10, 8.0 /*Normo 5 R Bag 25 kg */, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id,target_quantity, target_quantity_uom_cd, net_value, vat_percentage, transport_cost_included_flag, is_ap_group_leader) values (-501091, -5012, 30, -1040, 10, 'TO', 500, 8.0 /* Fluvio 4 */ , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id,target_quantity, target_quantity_uom_cd, net_value, vat_percentage, transport_cost_included_flag, is_ap_group_leader) values (-501092, -5012, 40, -5010, 1, 'TO', 14.5, 8.0 /* AdN Landfill Mat */ , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id,target_quantity, target_quantity_uom_cd, net_value, vat_percentage, transport_cost_included_flag, is_ap_group_leader) values (-501093, -5012, 50, -4060, 1, 'TO', 19.99, 8.0 /* AdN Agg Bag 1 Mat */, 0, 0);
-- LOGON-5692: having targetquantity, uom, price and vat set for a service material fails at opening the dispocard.  
-- insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id,target_quantity, target_quantity_uom_cd, net_value, vat_percentage, transport_cost_included_flag, is_ap_group_leader) values (-501094, -5012, 60, -9992, 1, 'PCE', 256.10, 8.0 /* ADN Test service */ , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id,target_quantity, target_quantity_uom_cd, net_value, vat_percentage, transport_cost_included_flag, is_ap_group_leader) values (-501094, -5012, 60, -9992, null, null, null, null /* ADN Test service */ , 0, 0);


commit;
