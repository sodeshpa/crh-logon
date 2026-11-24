-- select * from vlgns_soldship_flex_srch where composite_search_string like '%fvl%glattbrugg%';

-- working with "FVL Glattbrugg", soldto-shipto 800147 - 1866134
--
-- 1 sales area:
--   S101-10-10 (CH)
--
-- soldto_sap_bp_id = 101182 (careful - logon PK!)
-- SHIPTO_SAP_BP_ID = 109655 (careful - logon PK!)

-- select * from tlgns_sap_mat_cond where soldto_sap_bp_id = 101182 and SHIPTO_SAP_BP_ID is null;
-- Empty. => no "soldto" generic conditions.

-- select * from tlgns_sap_mat_cond where soldto_sap_bp_id = 101182 and SHIPTO_SAP_BP_ID = 109655;
-- Empty!
-- I.e., in LOGON 1.6, the whole Dispocard/OT is based on mat_cond defined for ALL SOLDTO, i.e. only mat-sorg defined!
-- This case does not exist in EBM anymore
-- Variant a) will be modelled with at least soldt-specific contracts (all shiptos of soldto=800147, Crh (Schweiz) AG Zementverkauf)
-- Variant b) will be modelled shipto specific.
-- Probably, a) will happen in reality. But this leads to a special case (so called soldto=shipto case in EBM)
-- => use b) for now in testdata!

-- materials in today's dispocard, search by SAP Shipto Number (reusable query for ANY shipto)
/*
select r.shipto_sarea_restr_id, r.shipto_sarea_id, r.enabled_flag, r.sap_material_id, m.sap_material_no from tlgnc_shipto_sarea_restr r
inner join tlgnc_shipto_sarea stsa on (stsa.shipto_sarea_id = r.shipto_sarea_id)
inner join tlgnc_shipto st on (st.shipto_id = stsa.shipto_id)
inner join tlgns_sap_bp sapst on (sapst.sap_bp_id = st.shipto_sap_bp_id)
left outer join tlgns_sap_material m on (r.sap_material_id = m.sap_material_id)
where sapst.sap_id = '1866134';
*/

/*
SHIPTO_SAREA_RESTR_ID SHIPTO_SAREA_ID ENABLED_FLAG SAP_MATERIAL_ID SAP_MATERIAL_NO
--------------------- --------------- ------------ --------------- ---------------
14541                 10907           0
16657                 10907           1            1054            1404860
16658                 10907           1            1040            1404839
16659                 10907           1            1068            1404863
*/

-- CREATING THE TEST CONTRACTS - INVENT DATA for this case!
-- - Validity: 1.1.2010 - 31.12.9999
-- - Contract Type: Use ZCQ (Agreement with customer with fixed prices) or ZCF (... without prices fixed)
-- - sarea: 1000 = S101-10-10
-- - Shipping cond for CHE: Only S1 = SBB relevant here. Mapping kdkg2_sap_trsp_mode_cd
-- - Incoterm: Use 'DDP', because it's a delivery (not pickup='EXW')

-- CREATING THE TEST CONTRACT ITEMS
-- - leave all fields "prod plant", "supply plant", value, etc. empty for now. They are not mandatory!
-- - 1 item for the 3 materials each.

-- CAREFUL: Note that the naming of the contract number (20101 = 201-01) must NOT be used by the application!
-- It is just that it's easier to give (static) numbers, i.e. managing the ID-range without sequences!
-- We are constructing TEST data here, therefore, humans should be able to generate/modify it easily
-- 
-- Possibly Optimization: Write PL/SQL "insert-or-update" scripts, which take
-- "business"-identifiers, to update/insert contracts + contract items.
-- But that seems to be too much logic at the wrong place (at least for now).

prompt Inserting into tlgns_sap_contract for SAP shipto 1866134 - FVL Glattbrugg
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-201, '1866134-01', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -101182, -109655, null, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'S1', 'Test AdN 201 -FVL Glatt- CHE rail', 1, 'CHF', 'DDP');

prompt Inserting into tlgns_sap_contract_item for SAP shipto 1866134 - FVL Glattbrugg
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-20101, -201, 10, -1040 /* Fluvio 4 */ , 0, 0); 
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-20102, -201, 20, -1054 /* Normo 4  */ , 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-20103, -201, 30, -1068 /* Normo 5R */ , 0, 0); 

commit;
