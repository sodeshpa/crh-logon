--WARNING!!!! Please make sure that @contract_based_on_mat_cond_shipto_1208869_Regli_Zumdorf.sql is called first this is using that shipto, but this does not belong anymore to swiss materials or anythin, this is completely just a test, not based on anything
--WARNING!!!! Materials added here are also used in: logon-persistence/etc/sql/adza_testdata/contracts_test_contract_hierarchy.sql


--Contract for zumdorf containing generic materials and adding a test service

Insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MARKED_FOR_DELETION, MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-9990, 'ADN9999990','ADN000000009999990','ADN Gen. mat(pg 01)'  ,'TO','Gen. mat.(pgroup)'      ,'10',null, 'S002' ,0,1,0,0,1,systimestamp,1,systimestamp,0);
Insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MARKED_FOR_DELETION, MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-9991, 'ADN9999991','ADN000000009999991','ADN Gen. mat(div 20)'  ,'TO','Gen. mat.(div)'      ,'20',null, 'S002' ,0,1,0,0,1,systimestamp,1,systimestamp,0);
Insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MARKED_FOR_DELETION, MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-9993, 'ADN9999993','ADN000000009999993','ADN Mat (div 20)','TO','CEM I 52.5 N'      ,'20',null, 'S002' ,0,1,0,0,1,systimestamp,1,systimestamp,0);
Insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MARKED_FOR_DELETION, MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-9994, 'ADN9999994','ADN000000009999994','ADN M (div 20 pg 01)','TO','CEM I 52.5 N'      ,'20',null, 'S002' ,0,1,0,0,1,systimestamp,1,systimestamp,0);
Insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MARKED_FOR_DELETION, MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-9995, 'ADN9999995','ADN000000009999995','ADN M (pg 01)','TO','CEM I 52.5 N'      ,'10',null, 'S002' ,0,1,0,0,1,systimestamp,1,systimestamp,0);
--this will not be present in the contract, only in the sales area
Insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MARKED_FOR_DELETION, MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-9992, 'ADN9999992','ADN000000009999992','ADN Test Service 1'  ,'PCE','ADN Test service'      ,'10',null, 'S005' ,0,0,0,0,1,systimestamp,1,systimestamp,0);

--texts
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9990,'fr','ADN Test gen. mat.(pgroup 01)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9990,'it','ADN Test gen. mat.(pgroup 01)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9990,'de','ADN Test gen. mat.(pgroup 01)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9990,'en','ADN Test gen. mat.(pgroup 01)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9990,'es','ADN Test gen. mat.(pgroup 01)');

Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9991,'fr','ADN Test gen. mat.(div 20)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9991,'it','ADN Test gen. mat.(div 20)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9991,'de','ADN Test gen. mat.(div 20)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9991,'en','ADN Test gen. mat.(div 20)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9991,'es','ADN Test gen. mat.(div 20)');

Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9992,'fr','ADN Test Service 1 (fr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9992,'it','ADN Test Service 1 (it)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9992,'de','ADN Test Service 1 (de)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9992,'en','ADN Test Service 1 (en)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9992,'es','ADN Test Service 1 (es)');

Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9993,'fr','ADN Mat (div 20)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9993,'it','ADN Mat (div 20)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9993,'de','ADN Mat (div 20)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9993,'en','ADN Mat (div 20)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9993,'es','ADN Mat (div 20)');

Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9994,'fr','ADN M (div 20 pg 01)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9994,'it','ADN M (div 20 pg 01)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9994,'de','ADN M (div 20 pg 01)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9994,'en','ADN M (div 20 pg 01)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9994,'es','ADN M (div 20 pg 01)');

Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9995,'fr','ADN M (pg 01)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9995,'it','ADN M (pg 01)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9995,'de','ADN M (pg 01)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9995,'en','ADN M (pg 01)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-9995,'es','ADN M (pg 01)');

--for service add mat_plant, otherwise no production plant can be specified
Insert into tlgns_sap_material_plant (SAP_MATERIAL_PLANT_ID,SAP_MATERIAL_ID,PLANT_CD,MARKED_FOR_DELETION,NHM_CODE) values (-9991, -9992, '8101', '0', '25232900');
Insert into tlgns_sap_material_plant (SAP_MATERIAL_PLANT_ID,SAP_MATERIAL_ID,PLANT_CD,MARKED_FOR_DELETION,NHM_CODE) values (-9997, -9992, '8301', '0', '25232900');
Insert into tlgns_sap_material_plant (SAP_MATERIAL_PLANT_ID,SAP_MATERIAL_ID,PLANT_CD,MARKED_FOR_DELETION,NHM_CODE) values (-9993, -9993, '8101', '0', '25232900');
Insert into tlgns_sap_material_plant (SAP_MATERIAL_PLANT_ID,SAP_MATERIAL_ID,PLANT_CD,MARKED_FOR_DELETION,NHM_CODE) values (-9994, -9994, '8101', '0', '25232900');
Insert into tlgns_sap_material_plant (SAP_MATERIAL_PLANT_ID,SAP_MATERIAL_ID,PLANT_CD,MARKED_FOR_DELETION,NHM_CODE) values (-9995, -9995, '8101', '0', '25232900');

--for generic materials define the resolution attibute material_group4 also
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag, material_group4, pricing_group) values (-9990, -9990, '0', 'BAG', 'AD02', '10', 1,'2','01');
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag, material_group4, pricing_group) values (-9991, -9991, '0', 'BAG', 'AD02', '10', 1,'1', null);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag, material_group4, pricing_group) values (-9993, -9993, '0', 'BAG', 'AD02', '10', 0,null, null);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag, material_group4, pricing_group) values (-9994, -9994, '0', 'BAG', 'AD02', '10', 0,null,'01');
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag, material_group4, pricing_group) values (-9995, -9995, '0', 'BAG', 'AD02', '10', 0,null,'01');

--the service is simply in the sales are of the shipto - it will also be available for other shiptos
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-9992, -9992, '0', 'PCE', 'AD02', '10', 0);

--adding base_uom_cds as alternative uoms for all materials. This seems to be the standard SAP modelling, see LOGON-5552.
insert into tlgns_sap_material_alt_uom (sap_material_alt_uom_id, sap_material_id, uom_cd, numerator_for_conversion, denominator_for_conversion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-11761, -9995, 'TO', 1, 1, 1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (sap_material_alt_uom_id, sap_material_id, uom_cd, numerator_for_conversion, denominator_for_conversion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-11762, -9994, 'TO', 1, 1, 1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (sap_material_alt_uom_id, sap_material_id, uom_cd, numerator_for_conversion, denominator_for_conversion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-11763, -9993, 'TO', 1, 1, 1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (sap_material_alt_uom_id, sap_material_id, uom_cd, numerator_for_conversion, denominator_for_conversion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-11764, -9992, 'PCE',1, 1, 1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (sap_material_alt_uom_id, sap_material_id, uom_cd, numerator_for_conversion, denominator_for_conversion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-11765, -9991, 'TO', 1, 1, 1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (sap_material_alt_uom_id, sap_material_id, uom_cd, numerator_for_conversion, denominator_for_conversion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-11766, -9990, 'TO', 1, 1, 1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (sap_material_alt_uom_id, sap_material_id, uom_cd, numerator_for_conversion, denominator_for_conversion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-11800, -9990, 'BAG', 1, 40, 1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (sap_material_alt_uom_id, sap_material_id, uom_cd, numerator_for_conversion, denominator_for_conversion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-11801, -9991, 'BAG', 1, 40, 1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (sap_material_alt_uom_id, sap_material_id, uom_cd, numerator_for_conversion, denominator_for_conversion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-11802, -9993, 'BAG', 1, 40, 1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (sap_material_alt_uom_id, sap_material_id, uom_cd, numerator_for_conversion, denominator_for_conversion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-11803, -9994, 'BAG', 1, 40, 1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (sap_material_alt_uom_id, sap_material_id, uom_cd, numerator_for_conversion, denominator_for_conversion, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-11804, -9995, 'BAG', 1, 40, 1,systimestamp,1,systimestamp,0);


prompt Inserting into tlgns_sap_contract for SAP shipto ADN1208869
insert into tlgns_sap_contract (sap_contract_id, sap_contract_no, sap_contract_type_cd, sarea_id, soldto_sap_bp_id, shipto_sap_bp_id, billto_sap_bp_id, payer_sap_bp_id, valid_from_dat, valid_until_dat, valid_until_ignore_flag, sap_shipping_cond_cd, cust_purchase_order_no, item_grouping_type, currency_cd, incoterms_cd) values (-403, '1208869-03', 'ZCQ', (select sa.sarea_id from tlgnc_sarea sa where (sa.sorg_cd='AD02' and sa.distribution_channel='10' and sa.division_cd='10')), -31447, -1470, null, null, to_date('20100101 00:00:00','YYYYMMDD HH24:MI:SS'), to_date('99991231 23:59:59','YYYYMMDD HH24:MI:SS'), 0, 'S1', 'Test AdN 403 -regli zumdor generic'     , 1, 'CHF', 'DDP');

prompt Inserting into tlgns_sap_contract_item for SAP shipto ADN1208869 generic material contract
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40205, -403, 10, -9990, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40206, -403, 20, -9991, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40207, -403, 30, -9993, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40208, -403, 40, -9994, 0, 0);
insert into tlgns_sap_contract_item (sap_contract_item_id, sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) values (-40209, -403, 50, -9995, 0, 0);

commit;
