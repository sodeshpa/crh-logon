
-- select * from vlgns_soldship_flex_srch where composite_search_string like '%fvl%altdorf%ur';

-- working with "FVL Altdorf", "UR", soldto-shipto 800147 - 1863947
--
-- 1 sales area:
--   S101-10-10 (CH)
--
-- soldto_sap_bp_id = 101182 (careful - logon PK!)
-- SHIPTO_SAP_BP_ID = 109672 (careful - logon PK!)

-- select * from tlgns_sap_mat_cond where soldto_sap_bp_id = 101182 and SHIPTO_SAP_BP_ID is null;
-- Empty. => no "soldto" generic conditions.

-- select * from tlgns_sap_mat_cond where soldto_sap_bp_id = 101182 and SHIPTO_SAP_BP_ID = 109672;
-- 4 conditions, explicitely maintained for this soldto-shipto BUT: all in the past!!
-- Do not model for EBM this way therefore, but similar to what already done for FVL Glattbrugg.

-- materials in today's dispocard, search by SAP Shipto Number (reusable query for ANY shipto)

/*
select r.shipto_sarea_restr_id, r.shipto_sarea_id, r.enabled_flag, r.sap_material_id, m.sap_material_no from tlgnc_shipto_sarea_restr r
inner join tlgnc_shipto_sarea stsa on (stsa.shipto_sarea_id = r.shipto_sarea_id)
inner join tlgnc_shipto st on (st.shipto_id = stsa.shipto_id)
inner join tlgns_sap_bp sapst on (sapst.sap_bp_id = st.shipto_sap_bp_id)
left outer join tlgns_sap_material m on (r.sap_material_id = m.sap_material_id)
where sapst.sap_id = '1863947';
*/

/*
SHIPTO_SAREA_RESTR_ID SHIPTO_SAREA_ID ENABLED_FLAG SAP_MATERIAL_ID SAP_MATERIAL_NO
--------------------- --------------- ------------ --------------- ---------------
16526                 12589           0
16528                 12589           1            1054            1404860
16529                 12589           1            1040            1404839
16530                 12589           1            1068            1404863
18606                 12589           1            1031            1404883
20421                 12589           1            1118            1406491
28497                 12589           1            1641            1411292
*/

-- CREATING THE TEST CONTRACTS - INVENT DATA for this case!
-- - Validity: 1.1.2010 - 31.12.9999
-- - Contract Type: Use ZCQ (Agreement with customer with fixed prices) or ZCF (... without prices fixed)
-- - sarea: 1000 = S101-10-10
-- - Shipping cond for CHE: Only S1 = SBB relevant here. Mapping kdkg2_sap_trsp_mode_cd
-- - Incoterm: Use 'DDP', because it's a delivery (not pickup='EXW')

-- CREATING THE TEST CONTRACT ITEMS
-- - leave all fields "prod plant", "supply plant", value, etc. empty for now. They are not mandatory!
-- - 1 item for the 6 materials each.

-- CAREFUL: Note that the naming of the contract number (30101 = 301-01) must NOT be used by the application!
-- It is just that it's easier to give (static) numbers, i.e. managing the ID-range without sequences!
-- We are constructing TEST data here, therefore, humans should be able to generate/modify it easily
--
-- Possibly Optimization: Write PL/SQL "insert-or-update" scripts, which take
-- "business"-identifiers, to update/insert contracts + contract items.
-- But that seems to be too much logic at the wrong place (at least for now).

prompt Inserting into tlgns_sap_contract for SAP shipto ADN1863947 - FVL Altdorf
insert into tlgns_sap_contract
(sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id                                                                    , soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat                                     , valid_until_dat                                    , valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no               , item_grouping_type, currency_cd, incoterms_cd) values
(-301           , '1863947-01'   , 'ZCQ'               , (select sarea_id from tlgnc_sarea where sarea_display_longname='AD02-10-10'), -101182         , -109672         , null            , null           , to_date('20100101 00:00:00', 'YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59', 'YYYYMMDD HH24:MI:SS'), 0                      , 1                   , 'Test AdN 301 -FVL Altdorf- CHE rail', 0                 , 'CHF'      , 'DDP'       );

prompt Inserting into tlgns_sap_contract_item for SAP shipto ADN1863947 - FVL Altdorf
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id          , transport_cost_included_flag, is_ap_group_leader) values
(-30101              , -301           , 10              , -1040 /* Fluvio 4 */     , 0                           , 0                 );
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id          , transport_cost_included_flag, is_ap_group_leader) values
(-30102              , -301           , 20              , -1054 /* Normo 4 */      , 0                           , 0                 );
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id          , transport_cost_included_flag, is_ap_group_leader) values
(-30103              , -301           , 30              , -1068 /* Normo 5R */     , 0                           , 0                 );
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id          , transport_cost_included_flag, is_ap_group_leader) values
(-30104              , -301           , 40              , -1031 /* Flextremo 4R */ , 0                           , 0                 );
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id          , transport_cost_included_flag, is_ap_group_leader) values
(-30105              , -301           , 50              , -1118 /* Bisolvo 3R */   , 0                           , 0                 );
insert into tlgns_sap_contract_item
(sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id          , transport_cost_included_flag, is_ap_group_leader) values
(-30106              , -301           , 60              , -1641 /* Optimo 4 */     , 0                           , 0                 );

commit;
