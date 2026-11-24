-- 800119 Crh (Schweiz) AG Würenlingen - 1209206 Werk Untervaz (Type IUD)
-- i.e., this would have STPO orders in EBM.
-- select * from vlgns_soldship_flex_srch where composite_search_string like '%800119%1209206%werk%untervaz%';

-- 3 sales areas, but we only care about S101-10-10
--   S101-10-10 (CH)
--
-- soldto_sap_bp_id = 108393 (careful - logon PK!) / SAP  800119
-- SHIPTO_SAP_BP_ID = 108394 (careful - logon PK!) / SAP 1209206

-- select * from tlgns_sap_mat_cond where soldto_sap_bp_id = 31447 and SHIPTO_SAP_BP_ID is null;
-- no "soldto" generic conditions. Only soldto AND shipto!

-- I.e., we have the case which corresponds to EBMS "standard case": 100% shipto specific contracts
-- select * from tlgns_sap_mat_cond where soldto_sap_bp_id = 108393 and SHIPTO_SAP_BP_ID = 108394;
-- 3 rows. all valid until 9999 (different starting dates ignored - 2009 or 2010 does not matter. valid NOW matters)

-- materials in today's dispocard, search by SAP Shipto Number (reusable query for ANY shipto)
/*
select r.shipto_sarea_restr_id, r.shipto_sarea_id, r.enabled_flag, r.sap_material_id, m.sap_material_no, md.txt from tlgnc_shipto_sarea_restr r
inner join tlgnc_shipto_sarea stsa on (stsa.shipto_sarea_id = r.shipto_sarea_id)
inner join tlgnc_shipto st on (st.shipto_id = stsa.shipto_id)
inner join tlgns_sap_bp sapst on (sapst.sap_bp_id = st.shipto_sap_bp_id)
left outer join tlgns_sap_material m on (r.sap_material_id = m.sap_material_id)
left outer join tlgns_sap_material_description md on (m.sap_material_id = md.sap_material_id and md.lang_cd = 'en')
where sapst.sap_id = '1209206' and stsa.sarea_id=1000
order by sap_material_id;
*/
-- 16 entries for all sales area, 11 for the S101-10-10. But the UI only shows 6 (+star) + warning "some of the referenced values are marked as deleted".

-- weird 1: The Dispocard shows row "Gottardo" (sap mat no 1404843 / logon id 1062) as "cannot be inactivated/deleted".
-- But the condition does not differ from the others at all. ALL are soldto-shipto specific. Bug probably, like for "Regli Zumdorf" as well.

/*
select * from tlgns_sap_mat_cond
where soldto_sap_bp_id = 108393 and SHIPTO_SAP_BP_ID = 108394
and valid_to_dat >= to_date('9999', 'YYYY')
order by valid_from_dat, valid_to_dat, kdkg2_sap_trsp_mode_cd, sap_material_id;
*/

-- weird 2: These soldto-shipto spec materials are all BULK, and SHIPTO specific,
-- but NOT contained in the dispocard for this tab!! That is against the concept
-- actually, and considered wrong :-(

-- So, instead we create an STPO for Rail direct for the Bag products as listed
-- in the dispocard instead.

-- CREATING THE TEST CONTRACTS
-- - Contract Type: Use "UB" = Stock Transfer Purchase Order
-- - sarea: 1000 = S101-10-10
-- - Shipping cond for CHE: S1 = SBB (This may change in EBM).
-- - If shipping cond is '00', set to incoterm to 'EXW'. In all other cases, use 'DDP' for now.
-- ==> created 1 contract 501

-- CREATING THE TEST CONTRACT ITEMS
-- - leave all fields "prod plant", "supply plant", value, etc. empty for now. They are not mandatory!
-- - Then, just took the Bag products which were visible also in the dispocard
--   (but which were actually NOT backed by shipto specific mat cond!)
-- + also added all the bulk products as done for the other contract testdata, to have a "complete" STPO as 1 test contract

-- CAREFUL: Note that the naming of the contract number (10101 = 101-01) must NOT be used by the application!
-- It is just that it's easier to give (static) numbers, i.e. managing the ID-range without sequences!
-- We are constructing TEST data here, therefore, humans should be able to generate/modify it easily
-- 
-- Possibly Optimization: Write PL/SQL "insert-or-update" scripts, which take
-- "business"-identifiers, to update/insert contracts + contract items.
-- But that seems to be too much logic at the wrong place (at least for now).

prompt Inserting into tlgns_sap_contract for SAP shipto ADN1209206
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-501, '1209206-01', 'UB', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -108393, -108394, null, null, to_date('20110101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'S1', 'Test AdN 501 -werk UV STPO-CHE rail', 1, 'CHF', 'DDP');

prompt Inserting BAG into tlgns_sap_contract_item for SAP shipto ADN1209206
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50101, -501, 10, -1027 /*Normo 4 Bag 25      */, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50102, -501, 20, -1065 /*Normo 5 R Bag 25 kg */, 0, 0); 
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50103, -501, 30, -2401 /*Albaro 5 R Sack 25  */, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50104, -501, 40, -2701 /*Optimo 4 Bag 25     */, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50105, -501, 50, -1049 /*Fortico 5R Sack 35  */, 0, 0);
prompt Inserting BULK into tlgns_sap_contract_item for SAP shipto ADN1209206
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50106, -501, 60, -1040 /* Fluvio 4 */ , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50107, -501, 70, -1054 /* Normo 4  */ , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-50108, -501, 80, -1068 /* Normo 5R */ , 0, 0);

commit;
