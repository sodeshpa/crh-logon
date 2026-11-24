-- select * from vlgns_soldship_flex_srch where composite_search_string like '%regli%zumdorf%';

-- working with "Kies und Beton Regli AG... Betonwerk Zumdorf", soldto-shipto 73031 - 1208869
--
-- 1 sales areas:
--   S101-10-10 (CH) <-- Work mainly on this
--
-- soldto_sap_bp_id = 31447 (careful - logon PK!)
-- SHIPTO_SAP_BP_ID =  1470 (careful - logon PK!)

-- select * from tlgns_sap_mat_cond where soldto_sap_bp_id = 31447 and SHIPTO_SAP_BP_ID is null;
-- no "soldto" generic conditions. Only soldto AND shipto!

-- I.e., we have the case which corresponds to EBMS "standard case": 100% shipto specific contracts
-- select * from tlgns_sap_mat_cond where soldto_sap_bp_id = 31447 and SHIPTO_SAP_BP_ID = 1470;
-- 18 rows. 9 from 2009-2010, 9 since 1.1.2011 - 31.12.9999

-- materials in today's dispocard, search by SAP Shipto Number (reusable query for ANY shipto)
/*
select r.shipto_sarea_restr_id, r.shipto_sarea_id, r.enabled_flag, r.sap_material_id, m.sap_material_no, md.txt from tlgnc_shipto_sarea_restr r
inner join tlgnc_shipto_sarea stsa on (stsa.shipto_sarea_id = r.shipto_sarea_id)
inner join tlgnc_shipto st on (st.shipto_id = stsa.shipto_id)
inner join tlgns_sap_bp sapst on (sapst.sap_bp_id = st.shipto_sap_bp_id)
left outer join tlgns_sap_material m on (r.sap_material_id = m.sap_material_id)
left outer join tlgns_sap_material_description md on (m.sap_material_id = md.sap_material_id and md.lang_cd = 'en')
where sapst.sap_id = '1208869';
*/
-- weird: The Dispocard shows row "Hydrolent" (sap mat no 1101870 / logon id 1029) as "cannot be inactivated".
-- But the condition does not differ from the others at all. ALL are soldto-shipto specific. Bug?
/*
select * from tlgns_sap_mat_cond
where soldto_sap_bp_id = 31447 and SHIPTO_SAP_BP_ID = 1470
and valid_to_dat >= to_date('9999', 'YYYY')
order by valid_from_dat, valid_to_dat, kdkg2_sap_trsp_mode_cd, sap_material_id;
*/
-- 9 valid 1.1.2009 - 31.12.2010 - ignore those here.
-- 9 valid 1.1.2011 - 31.12.9999
-- ==> minimally TWO contracts would be needed, we just model the "current"
-- 2 different kdkg2_sap-trsp_mode_cd in each contract

-- CREATING THE TEST CONTRACTS
-- - Contract Type: Use ZCQ (Agreement with customer with fixed prices) or ZCF (... without prices fixed)
-- - sarea: 1000 = S101-10-10
-- - Shipping cond for CHE: E1 = Truck, S1 = SBB (These may change in EBM). Mapping kdkg2_sap_trsp_mode_cd
--   10 --> E1
--   50 --> S1
--   00 --> E1 (Pickup is also considered as Truck) (does not occur in this example)
-- - If shipping cond is '00', set to incoterm to 'EXW'. In all other cases, use 'DDP' for now.
-- ==> created the 4 contracts 101..104

-- CREATING THE TEST CONTRACT ITEMS
-- - leave all fields "prod plant", "supply plant", value, etc. empty for now. They are not mandatory!
-- Contract 1 and 2 - split by kdkg2_sap_trsp_mode_cd, into contract 01 and 02 of test customer 4, i.e. 401xx and 402xx
--   1029 10    -> 40101 10
--   1031 10    -> 40102 20
--   1040 10    -> 40103 30
--   1054 10    -> 40104 40
--   1068 10    -> 40105 50
--   1125 10    -> 40106 60
--   1031    50 -> 40201 10
--   1054    50 -> 40202 20
--   1125    50 -> 40203 30

-- CAREFUL: Note that the naming of the contract number (10101 = 101-01) must NOT be used by the application!
-- It is just that it's easier to give (static) numbers, i.e. managing the ID-range without sequences!
-- We are constructing TEST data here, therefore, humans should be able to generate/modify it easily
-- 
-- Possibly Optimization: Write PL/SQL "insert-or-update" scripts, which take
-- "business"-identifiers, to update/insert contracts + contract items.
-- But that seems to be too much logic at the wrong place (at least for now).

prompt Inserting into tlgns_sap_contract for SAP shipto ADN1208869
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-401, '1208869-01', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -31447, -1470, null, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Test AdN 401 -regli zumdor-CHE truc'    , 1, 'CHF', 'DDP');
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-402, '1208869-02', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -31447, -1470, null, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'S1', 'Test AdN 402 -regli zumdor-CHE rail'     , 1, 'CHF', 'DDP');

-- Contract with EXW (Ex Works) incoterm for pickup
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-444, '1208869-12', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -31447, -1470, null, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'S1', 'Test AdN 444 -regli zumdor-CHE pick'     , 1, 'CHF', 'EXW');

-- Cubic meters loading related testdata, for 'ADN Bulk M3 Test', adding material for plant 8101.
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-2702, '1208869-11', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -31447, -1470, -31447, -31447, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'E1', 'Test AdN 401 -regli zumdor-CHE tM3', 1, 'CHF', 'DDP');

prompt Inserting into tlgns_sap_contract_item for SAP shipto ADN1208869
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40101, -401, 10, -1029, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40102, -401, 20, -1031, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40103, -401, 30, -1040, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40104, -401, 40, -1054, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40105, -401, 50, -1068, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40106, -401, 60, -1125, 0, 0);
--this service was not there originally for zumdorf
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40107, -401, 70, -6002, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40201, -402, 10, -1031, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40202, -402, 20, -1054, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40203, -402, 30, -1125, 0, 0);

insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40294, -402, 40, -3039, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40295, -402, 50, -3046, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40296, -402, 60, -3053, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40297, -402, 70, -3059, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40298, -402, 80, -3060, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40299, -402, 90, -1027, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40300, -402, 100, -1049, 0, 0);
--this service was not there originally for zumdorf
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40301, -402, 110, -6002, 0, 0);

-- contract items for sap contract with EXW(pickup) incoterm
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-44401, -444, 10, -1027, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-44402, -444, 20, -1049, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-44403, -444, 30, -3039, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-44404, -444, 40, -3059, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-44405, -444, 50, -3060, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-44406, -444, 60, -6002, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-44407, -444, 70, -2702, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-44408, -444, 70, -2703, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-44409, -444, -2, 1, 0, 0);

-- Cubic meters loading related testdata, for 'ADN Bulk M3 Test', adding material for plant 8101.
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-2702, -2702, 10, -2702, 0, 0);

commit;
