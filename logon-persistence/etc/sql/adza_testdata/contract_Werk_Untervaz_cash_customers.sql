--This file contains contract with type ZCSA, for modelling the case where free delivery is forbidden. - ADN75591	LRQ 014.0 - Free of charge orders
--The source for this data is the stpo_based_on_dispocard_shipto_1209206_Werk_Untervaz_IUD.sql file

prompt Inserting into tlgns_sap_contract for SAP shipto ADN1209206
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-504, '1209206-04', 'ZCSA', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -108393, -108394, null, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'S1', 'Test AdN 502 -go live scenario', 1, 'CHF', 'DDP');

prompt Inserting BAG into tlgns_sap_contract_item for SAP shipto ADN1209206
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, net_value, vat_percentage, target_quantity, target_quantity_uom_cd, transport_cost_included_flag, is_ap_group_leader) values (-50401, -504, 10, -1027 /*Normo 4 Bag 25      */, 6900, 19, 500, 'TO', 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, net_value, vat_percentage, target_quantity, target_quantity_uom_cd, transport_cost_included_flag, is_ap_group_leader) values (-50402, -504, 20, -1065 /*Normo 5 R Bag 25 kg */, 6900, 19, 500, 'TO', 0, 0); 
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, net_value, vat_percentage, target_quantity, target_quantity_uom_cd, transport_cost_included_flag, is_ap_group_leader) values (-50403, -504, 30, -2401 /*Albaro 5 R Sack 25  */, 6900, 19, 500, 'TO', 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, net_value, vat_percentage, target_quantity, target_quantity_uom_cd, transport_cost_included_flag, is_ap_group_leader) values (-50404, -504, 40, -2701 /*Optimo 4 Bag 25     */, 6900, 19, 500, 'TO', 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, net_value, vat_percentage, target_quantity, target_quantity_uom_cd, transport_cost_included_flag, is_ap_group_leader) values (-50405, -504, 50, -1049 /*Fortico 5R Sack 35  */, 5, 2, 5, 'BAG', 0, 0);

prompt Inserting BULK into tlgns_sap_contract_item for SAP shipto ADN1209206
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, net_value, vat_percentage, target_quantity, target_quantity_uom_cd, transport_cost_included_flag, is_ap_group_leader) values (-50406, -504, 60, -1040 /* Fluvio 4 */, 6900, 19, 500, 'TO' , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, net_value, vat_percentage, target_quantity, target_quantity_uom_cd, transport_cost_included_flag, is_ap_group_leader) values (-50407, -504, 70, -1054 /* Normo 4  */, 6900, 19, 500, 'TO' , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, net_value, vat_percentage, target_quantity, target_quantity_uom_cd, transport_cost_included_flag, is_ap_group_leader) values (-50408, -504, 80, -1068 /* Normo 5R */, 6900, 19, 500, 'TO' , 0, 0);

-- For rail orders using a material with a base UoM of Cubic meters.
-- TODO 28-Nov-2012/romek: -50410 was already reserved in the HU2 DB (which is IMO a bad practice!), currently using -50411.
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, net_value, vat_percentage, target_quantity, target_quantity_uom_cd, transport_cost_included_flag, is_ap_group_leader) values (-50411, -504, 90, -2702 /* ADN Bulk M3 Test*/, 6900, 19, 500, 'TO', 0, 0);

prompt Inserting PALLET into tlgns_sap_contract_item for SAP shipto ADN1209206
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, net_value, vat_percentage, target_quantity, target_quantity_uom_cd, transport_cost_included_flag, is_ap_group_leader) values (-50409, -504, 30, -3060 /*PALETTE PERDUE  */, 6700, 18, 400, 'PCE', 0, 0);

commit;
