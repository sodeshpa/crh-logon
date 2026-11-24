--file contains test contracts and additional materials for the ADN aggregate and landfill customer (ADNAGG)
prompt running contracts_adn_aggregate_and_landfill_customer.sql

--insert landfill contracts to sales area AD02-10-30
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-150, 'LF1 Bulk', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='30')), -32816, -39, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'LF contract 1'    , 1, 'CHF', 'DDP');

--insert aggregate contracts to sales area AD02-10-30
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-153, 'AGG1 Bulk', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='30')), -32816, -39, -32816, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'AGG contract 1'    , 1, 'CHF', 'DDP');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-154, 'AGG2 Bag', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='30')), -32816, -39, null, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'AGG contract 2'    , 1, 'CHF', 'DDP');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-155, 'AGG3 Svc', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='30')), -32816, -39, null, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'AGG contract 3'    , 1, 'CHF', 'DDP');

--insert aggregate contract items to sales area AD02-10-30, bulk contract (1 bulk, 2 services)
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11200, -153, 20, -4064, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11201, -153, 10, -6002, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11202, -153, 30, -6004, 0, 0);

--insert aggregate contract items to sales area AD02-10-30, bag contract (4 bag, 2 services)
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11203, -154, 10, -4060, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11204, -154, 20, -4061, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11205, -154, 30, -4062, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11206, -154, 40, -4063, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11207, -154, 50, -6003, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11208, -154, 60, -9992, 0, 0);

--insert aggregate contract items to sales area AD02-10-30, svc contract (2 services)
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11209, -155, 10, -6002, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11210, -155, 20, -6003, 0, 0);

--insert landfill contract items to sales area AD02-10-30, landfill (1 bulk lf, 1 svc)
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11211, -150, 10, -5010, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-11212, -150, 20, -6002, 0, 0);


------------------------------------------------INSERT INTO TLGNC_SHIPTO----------------------------------------
--this initialization could take place in Logon, but in that case has to make sure that pickup is possible

Insert into TLGNC_SHIPTO (SHIPTO_ID,SHIPTO_SAP_BP_ID,ACTIVE_FLAG,SHIPTO_TYPE_CD,OPEN1_FROM,OPEN1_TO,OPEN2_FROM,OPEN2_TO,CONTACT_PERSON_ID,SELF_SERVICE_FLAG,DAILY_ORDER_CONTACT_FLAG,DAILY_ORDER_LAST_CONTACT_DAT,DAILY_ORDER_STATUS_CD,SIGNATURE_TYPE_CD,COORD_LAT,COORD_LON,COORD_FIX_FLAG,GEOFENCE_RADIUS,UNLOADING_TIME_CEM_MIN,UNLOADING_TIME_AGG_MIN,LOADING_TIME_LF_MIN,RAIL_CHAIN_SHIPPINGPOINT_ID,MAT_CHAIN_SHIPPINGPOINT_ID,RAIL_HOLD_BACK_RE_ORDER_FLAG,PLANT_CD,PAPER_SINGLE_DELNOTE_FLAG_CEM,PAPER_SINGLE_DELNOTE_FLAG_AGG,PAPER_COLL_DELNOTE_FLAG_CEM,PAPER_COLL_DELNOTE_FLAG_AGG,EMAIL_DELIVNOTE_FLAG,FAX_DELIVNOTE_FLAG,ORDER_CONF_EMAIL_FLAG,ORDER_CONF_EMAIL_ADDRESSES,ORDER_CONF_FAX_FLAG,ORDER_CONF_FAX_NUMBER,ORDER_CONF_SMS_FLAG,ORDER_CONF_SMS_NUMBER,NOTE_ORDER_MGMT,NOTE_DELIVERY,NOTE_DISPATCHING,NOTE_TRUCK_DRIVER,NOTE_RAILWAY_DISP) values (-200,-39,1,0,420,720,780,1020,null,1,0,null,null,0,47.4782477,8.30985039999996,0,100,30,15,10,null,null,0,null,1,1,1,1,0,0,0,null,0,null,0,null,'Comment Order Management (Disopcard)','Comment Delivery Note (Disopcard)','Comment Scheduling (Disopcard)','Comment Driver (Disopcard)','Comment Weighingsystem (Disopcard)');

commit;
