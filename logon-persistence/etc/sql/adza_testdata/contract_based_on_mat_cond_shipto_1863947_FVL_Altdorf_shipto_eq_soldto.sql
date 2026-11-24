--this contract is to modell the shipto equals soldto case
--the source of the data is the contract_based_on_mat_cond_shipto_1863947_FVL_Altdorf.sql file

prompt Inserting into tlgns_sap_contract for SAP shipto ADN1863947 - FVL Altdorf
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-302, '1863947-02', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -101182, -101182, null, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'S1', 'Test AdN 302 Altdorf soldto=shipto', 1, 'CHF', 'DDP');

prompt Inserting into tlgns_sap_contract_item for SAP shipto ADN1863947 - FVL Altdorf
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-30201, -302, 10, -1040 /* Fluvio 4     */ , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-30202, -302, 20, -1054 /* Normo 4      */ , 0, 0);

commit;
