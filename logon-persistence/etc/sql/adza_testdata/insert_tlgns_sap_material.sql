/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

------------------------------------------
-- Testdata inserts for:                --
--   * tlgns_sap_material               -- 
--   * tlgns_sap_material_alt_uom       --  
--   * tlgns_sap_material_sarea         --
--   * tlgns_sap_material_description   --
------------------------------------------

/*
select * from tlgns_sap_material m where m.sap_material_id in
  (select distinct ci.sap_material_id from tlgns_sap_contract_item ci)
order by m.sap_material_id;
*/

prompt inserting tlgns_sap_material: required materials for test-contracts based from CHE prod data
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1027,    'ADN1404854','ADN000000001404854','OPC Type I'    ,'TO','CEM I 42.5 N'      ,'10',null, 'S002',      0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1029,    'ADN1101870','ADN000000001101870','Fly Ash'       ,'TO','Others'            ,'10',null, 'S001',     0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1031,    'ADN1404883','ADN000000001404883','OPC Type II'   ,'TO','CEM II/A-M 42.5 R' ,'10',null, 'S001',     0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1040,    'ADN1404839','ADN000000001404839','OPC Type II'   ,'TO','CEM II/A-LL 42.5 N','10',null, 'S001',     0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1049,    'ADN1404878','ADN000000001404878','OPC Type II'   ,'TO','CEM II/A-D 52.5 R' ,'10',null, 'S002',      0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1052,    'ADN1404870','ADN000000001404870','OPC Type II'   ,'TO','CEM II/A-S 32.5 R' ,'10',null, 'S001',     0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1054,    'ADN1404860','ADN000000001404860','OPC Type I'    ,'TO','CEM I 42.5 N'      ,'10',null, 'S001',     0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1057,    'ADN1404849','ADN000000001404849','OPC Type III'  ,'TO','CEM III/B 32.5 N'  ,'10',null, 'S001',     0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1065,    'ADN1404856','ADN000000001404856','OPC Type I'    ,'TO','CEM I 52.5 R'      ,'10',null, 'S002',      0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1068,    'ADN1404863','ADN000000001404863','OPC Type I'    ,'TO','CEM I 52.5 R'      ,'10',null, 'S001',     0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1118,    'ADN1406491','ADN000000001406491','Other Cement'  ,'TO','CEM II/B-M 32.5 R' ,'10',null, 'S001',     0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1125,    'ADN1405002','ADN000000001405002','OPC Type II'   ,'TO','CEM II/B-M 32.5 R' ,'10',null, 'S001',     0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1454,    'ADN1404842','ADN000000001404842','OPC Type II'   ,'TO','CEM II/A-D 52.5 N' ,'10',null, 'S001',     0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1641,    'ADN1411292','ADN000000001411292','Other Cement'  ,'TO','CEM II/B-M 42.5 N' ,'10',null, 'S001',     0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2401,    'ADN1411749','ADN000000001411749','White Cement'  ,'TO','CEM I 52.5 N'      ,'10',null, 'S002',      0,1,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2701,    'ADN1412168','ADN000000001412168','OPC Type I'    ,'TO','CEM I 42.5 N'      ,'10',null, 'S002',      0,1,0,0,1,systimestamp,1,systimestamp,0);
-- Pallets for testing
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-3039,    'ADN7000003','ADN000000070000003','070301'        ,'PCE',null               ,'10',null, 'S003',  0,0,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-3046,    'ADN7000001','ADN000000070000001','070301'        ,'PCE',null               ,'10',null, 'S003',  0,0,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-3053,    'ADN7000002','ADN000000070000002','070301'        ,'PCE',null               ,'10',null, 'S003',  0,0,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-3059,    'ADN7000004','ADN000000070000004','070301'        ,'PCE',null               ,'10',null, 'S003',  0,0,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-3060,    'ADN7000011','ADN000000070000011','070301'        ,'PCE',null               ,'10',null, 'S003',  0,0,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-3066,    'ADN7000005','ADN000000070000005','070301'        ,'PCE',null               ,'10',null, 'S003',  0,0,0,0,1,systimestamp,1,systimestamp,0);

--2 Materials specifically set up by tlgnc_mat_plant entry to trigger LRQ 018.0 autoadd pallet feature
Insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1066,    'ADN1404857','ADN000000001404857','Add Autoadd A'    ,'TO','CEM I 52.5 R B'      ,'10',null, 'S002',      0,1,0,0,1,systimestamp,1,systimestamp,0);
Insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1067,    'ADN1404858','ADN000000001404858','Add Autoadd B'    ,'TO','CEM I 52.5 R C'      ,'10',null, 'S002',      0,1,0,0,1,systimestamp,1,systimestamp,0);

--Materials for testing that deleted ones should not appear
Insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1070,    'ADNDEL1404859','ADN0DELETEDSAREA00','Deleted SA'    ,'TO','CEM I 52.5 R C'      ,'10',null, 'S002',      0,1,0,0,1,systimestamp,1,systimestamp,0);
Insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1069,    'ADNDEL1404860','ADN000000DELETED00','Deleted'    ,'TO','CEM I 52.5 R C'      ,'10',null, 'S002',      0,1,0,1,1,systimestamp,1,systimestamp,0);

-- Test material for the following fields of tlgnr_wagon_type: empty_sap_material_no, maintenance_sap_material_no, repair_sap_material_no 
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1902632, 'ADN1902632','ADN000000001902632','ADN wagon mat.','TO', 'ADN wagon mat.'   ,'10',null, 'S005', 0,1,0,0,1,systimestamp,1,systimestamp,0);

-- Aggregate test material
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-4060,    'ADN7000012','ADN000000070000012','0111400'       ,'TO','ADN AGG Bag 1 mat'     ,'10',null, 'S002',     1,0,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-4061,    'ADN7000013','ADN000000070000013','0111400'       ,'TO','ADN AGG Bag 2 mat'     ,'10',null, 'S002',     1,0,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-4062,    'ADN7000014','ADN000000070000014','0111400'       ,'TO','ADN AGG Bag 3 mat'     ,'10',null, 'S002',     1,0,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-4063,    'ADN7000015','ADN000000070000015','0111400'       ,'TO','ADN AGG Bag 4 mat'     ,'10',null, 'S002',     1,0,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-4064,    'ADN7000016','ADN000000070000016','0111400'       ,'TO','ADN AGG Bulk mat.'     ,'10',null, 'S006',     1,0,0,'0',1,systimestamp,1,systimestamp,0);

-- Landfill test material
insert into tlgns_sap_material (SAP_MATERIAL_ID,SAP_MATERIAL_NO,SAP_MATERIAL_IFC_NO,MATERIAL_GROUP_DESC,BASE_UOM_CD,PRODUCT_HIERARCHY_DESC,DIVISION_CD,INDUSTRY_STANDARD_DESC,MATERIAL_PACKAGE_CD,MATERIAL_IS_AGG,MATERIAL_IS_CEM,MATERIAL_IS_LF,MARKED_FOR_DELETION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-5010,    'ADN7000014','ADN000000070000014','0111404'       ,'TO','ADN Landfill mat.' ,'10',null,'S006',    0,0,1,0,1,systimestamp,1,systimestamp,0);

-- Service Test materials 2-4 (1 is already defined in contract_shipto_1208869_Regli_Zumdorf_generic_materials.sql
insert into tlgns_sap_material (sap_material_id,sap_material_no,sap_material_ifc_no,update_blocked_by_hjid,material_group_desc,base_uom_cd,product_hierarchy_desc,division_cd,industry_standard_desc,material_package_cd,marked_for_deletion,prod_plant_cd,material_is_agg,material_is_cem,material_is_lf,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values (-6002,'ADN0006000','ADN000000000006000',null,'ADN Test Service 2','PCE',null,'10',null,'S005',0,null,0,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (sap_material_id,sap_material_no,sap_material_ifc_no,update_blocked_by_hjid,material_group_desc,base_uom_cd,product_hierarchy_desc,division_cd,industry_standard_desc,material_package_cd,marked_for_deletion,prod_plant_cd,material_is_agg,material_is_cem,material_is_lf,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values (-6003,'ADN0006001','ADN000000000006001',null,'ADN Test Service 3','PCE',null,'10',null,'S005',0,null,0,0,0,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material (sap_material_id,sap_material_no,sap_material_ifc_no,update_blocked_by_hjid,material_group_desc,base_uom_cd,product_hierarchy_desc,division_cd,industry_standard_desc,material_package_cd,marked_for_deletion,prod_plant_cd,material_is_agg,material_is_cem,material_is_lf,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values (-6004,'ADN0006002','ADN000000000006002',null,'ADN Test Service 4','HRE',null,'10',null,'S005',0,null,0,0,0,1,systimestamp,1,systimestamp,0);

-- Cubic meters loading related testdata, Material which has BASE UOM 'MTQ', referred as 'ADN Bulk M3 Test' from now on
insert into tlgns_sap_material (sap_material_id,sap_material_no,sap_material_ifc_no,material_group_desc,base_uom_cd,product_hierarchy_desc,division_cd,industry_standard_desc,material_package_cd,material_is_agg,material_is_cem,material_is_lf,marked_for_deletion,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values (-2702,    'ADN1412169','ADN000000001412169','OPC Type I',   'MTQ','CEM I 42.5 N',         '10', null, 'S001',    0,1,0,0,1,systimestamp,1,systimestamp,0);

--bulk dumped test
insert into tlgns_sap_material (sap_material_id,sap_material_no,sap_material_ifc_no,material_group_desc,base_uom_cd,product_hierarchy_desc,division_cd,industry_standard_desc,material_package_cd,material_is_agg,material_is_cem,material_is_lf,marked_for_deletion,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values (-2703,    'ADN1412170','ADN000000001412170','OPC Type I',   'MTQ','CEM I 42.5 N',         '10', null, 'S006',    0,1,1,0,1,systimestamp,1,systimestamp,0);

-- The update of wagon_types only for test wagon types
update TLGNR_WAGON_TYPE_BS set empty_sap_material_no = 'ADN1902632' where wagon_type_cd<0;
update TLGNR_WAGON_TYPE_BS set maintenance_sap_material_no = 'ADN1902632' where wagon_type_cd<0;
update TLGNR_WAGON_TYPE_BS set repair_sap_material_no = 'ADN1902632' where wagon_type_cd<0;

/*
select * from tlgns_sap_material_alt_uom mauom where mauom.sap_material_id in
  (select distinct ci.sap_material_id from tlgns_sap_contract_item ci)
order by mauom.sap_material_id;
*/

prompt inserting tlgns_sap_material_alt_uom: required materials for test-contracts based from CHE prod data
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1981,-1027,'BAG',1, 40,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1982,-1065,'BAG',8, 40,1,systimestamp,1,systimestamp,0);
Insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1983,-1066,'BAG',8, 40,1,systimestamp,1,systimestamp,0);
Insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1986,-1067,'BAG',8, 40,1,systimestamp,1,systimestamp,0);
Insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1987,-1070,'BAG',8, 40,1,systimestamp,1,systimestamp,0);
Insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1988,-1069,'BAG',8, 40,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1036,-1029,'TO' ,1, 1,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2209,-1031,'27T',27, 1,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2135,-1040,'15T',15, 1,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2203,-1049,'BAG',1,40,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2208,-1052,'15T',15, 1,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2128,-1054,'15T',15, 1,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2129,-1057,'27T',27, 1,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2133,-1065,'CKG',1, 10,1,systimestamp,1,systimestamp,0);
Insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1984,-1066,'CKG',1, 10,1,systimestamp,1,systimestamp,0);
Insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1980,-1067,'CKG',1, 10,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2130,-1068,'15T',15, 1,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2228,-1118,'15T',15, 1,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2252,-1125,'27T',27, 1,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2134,-1454,'15T',15, 1,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2301,-1641,'15T',15, 1,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2541,-2401,'BAG',1, 40,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2542,-2401,'PAL',48, 40,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2801,-2701,'BAG',1, 40,1,systimestamp,1,systimestamp,0);
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-2802,-2701,'PAL',14, 10,1,systimestamp,1,systimestamp,0);

--adding base_uom_cds as alternative uoms for all materials
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1040,-1040,'TO' ,1, 1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11760,-1902632,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11795,-1027,'TO',1,1,1,systimestamp,1,systimestamp,0);

insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11767,-6004,'HRE',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11768,-6003,'PCE',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11769,-6002,'PCE',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11770,-5010,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11771,-4064,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11772,-4063,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11773,-4062,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11774,-4061,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11775,-4060,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11776,-3066,'PCE',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11777,-3060,'PCE',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11778,-3059,'PCE',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11779,-3053,'PCE',1,1,1,systimestamp,1,systimestamp,0);
Insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11700,-1066,'TO',1,1,1,systimestamp,1,systimestamp,0);
Insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11701,-1067,'TO',1,1,1,systimestamp,1,systimestamp,0);
Insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11702,-1070,'TO',1,1,1,systimestamp,1,systimestamp,0);
Insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11703,-1069,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11780,-3046,'PCE',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11781,-3039,'PCE',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11782,-2701,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11783,-2401,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11784,-1641,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11785,-1454,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11786,-1125,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11787,-1118,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11788,-1068,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11789,-1065,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11790,-1057,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11791,-1054,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11792,-1052,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11793,-1049,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-11794,-1031,'TO',1,1,1,systimestamp,1,systimestamp,0);
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values ( -2703,-2703,'TO',4,1,1,systimestamp,1,systimestamp,0);

-- Cubic meters loading related testdata, alternative UOM for 'MTQ' material 'ADN Bulk M3 Test' in 'TO'. -> 4 'MTQ' = 1 'TO'
insert into TLGNS_SAP_MATERIAL_ALT_UOM (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values ( -2702,-2702,'TO',4,1,1,systimestamp,1,systimestamp,0);

-- Cubic meters loading related testdata, alternative UOM for 'TO' material 'Flextermo 4R Bulk' in 'MTQ'. -> 4 'MTQ' = 1 'TO'
insert into tlgns_sap_material_alt_uom (SAP_MATERIAL_ALT_UOM_ID,SAP_MATERIAL_ID,UOM_CD,NUMERATOR_FOR_CONVERSION,DENOMINATOR_FOR_CONVERSION,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values ( -9998,-1031,'MTQ',1, 4, 1,systimestamp,1,systimestamp,0);

-- CROSS-BOUNDARY: Make certain materials available at ZA plant 9101 as well (LOGON-4947).
-- uma, 19.12.2012: ADN_PLANT_S108_MAP_CD had been mapped to 9101, i.e., there are already a bunch of materials available there. No need to add new ones!

-- Test material for the following fields of tlgnr_wagon_type: empty_sap_material_no, maintenance_sap_material_no, repair_sap_material_no
/*
select msa.sap_material_sarea_id, msa.sap_material_id, msa.sales_uom_cd, msa.sorg_cd, msa.distribution_channel from tlgns_sap_material_sarea msa where msa.sap_material_id in
  (select distinct ci.sap_material_id from tlgns_sap_contract_item ci)
and msa.sorg_cd = 'S101' / * all test contracts are for S101 only * /
order by msa.sap_material_id;
*/

prompt inserting tlgns_sap_material_sarea: required materials for test-contracts based from CHE prod data

insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1031, -1027, '0', 'BAG', 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1036, -1029, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1040, -1031, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1060, -1040, '0', null, 'AD02', '10', 0);
--added this association below because of contract hierarchies, it was originally not present in the CHE database, the id is completely fake!
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-10601, -1040, '0', null, 'AD01', '20', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1078, -1049, '0', 'BAG', 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1084, -1052, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1089, -1054, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1096, -1057, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1114, -1065, '0', 'BAG', 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1104, -1066, '0', 'BAG', 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1103, -1067, '0', 'BAG', 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1105, -1070, '1', 'BAG', 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1106, -1069, '0', 'BAG', 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-904, -3046, '0', 'BAG', 'AD03', '20', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1120, -1068, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1198, -1118, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1210, -1125, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1888, -1454, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-1521, -1641, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-2301, -2401, '0', 'BAG', 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-2621, -2701, '0', 'BAG', 'AD02', '10', 0);

--Pallets
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-3001, -3039, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-3002, -3046, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-3003, -3053, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-3004, -3059, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-3005, -3060, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-3006, -3066, '0', null, 'AD02', '10', 1);

-- Aggregate
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-4001, -4060, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-4003, -4061, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-4004, -4062, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-4005, -4063, '0', null, 'AD02', '10', 0);
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-4006, -4064, '0', null, 'AD02', '10', 0);

-- Landfill
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-4002, -5010, '0', null, 'AD02', '10', 0);

-- Service 2-4
insert into TLGNS_SAP_MATERIAL_SAREA (SAP_MATERIAL_SAREA_ID,SAP_MATERIAL_ID,SORG_CD,DISTRIBUTION_CHANNEL,MARKED_FOR_DELETION,SALES_UOM_CD,PRICING_GROUP,MATERIAL_GROUP4,IS_GENERIC_FLAG) values (-6002,-6004,'AD02','10','0',null,null,null,0);
insert into TLGNS_SAP_MATERIAL_SAREA (SAP_MATERIAL_SAREA_ID,SAP_MATERIAL_ID,SORG_CD,DISTRIBUTION_CHANNEL,MARKED_FOR_DELETION,SALES_UOM_CD,PRICING_GROUP,MATERIAL_GROUP4,IS_GENERIC_FLAG) values (-6001,-6003,'AD02','10','0',null,null,null,0);
insert into TLGNS_SAP_MATERIAL_SAREA (SAP_MATERIAL_SAREA_ID,SAP_MATERIAL_ID,SORG_CD,DISTRIBUTION_CHANNEL,MARKED_FOR_DELETION,SALES_UOM_CD,PRICING_GROUP,MATERIAL_GROUP4,IS_GENERIC_FLAG) values (-6000,-6002,'AD02','10','0',null,null,null,0);

-- Cubic meters loading related testdata, for 'ADN Bulk M3 Test', adding material for sarea AD02.
insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-2702, -2702, '0', null, 'AD02', '10', 0);

insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) values (-2703, -2703, '0', null, 'AD02', '10', 0);

-- Auto-payer
insert into TLGNS_SAP_MATERIAL_SAREA
(SAP_MATERIAL_SAREA_ID           , SAP_MATERIAL_ID, SORG_CD, DISTRIBUTION_CHANNEL, MARKED_FOR_DELETION, SALES_UOM_CD, PRICING_GROUP, MATERIAL_GROUP4, IS_GENERIC_FLAG, CTL_CRE_UID, CTL_CRE_TS  , CTL_MOD_UID, CTL_MOD_TS  , CTL_TCN) values
(slgns_sap_material_sarea.nextval, -1040          , 'ZA01' , 10                  , '0'                , null        , null         , null           , 0              , 1          , systimestamp, 1          , systimestamp, 0      );

insert into TLGNS_SAP_MATERIAL_SAREA
(SAP_MATERIAL_SAREA_ID           , SAP_MATERIAL_ID, SORG_CD, DISTRIBUTION_CHANNEL, MARKED_FOR_DELETION, SALES_UOM_CD, PRICING_GROUP, MATERIAL_GROUP4, IS_GENERIC_FLAG, CTL_CRE_UID, CTL_CRE_TS  , CTL_MOD_UID, CTL_MOD_TS  , CTL_TCN) values
(slgns_sap_material_sarea.nextval, -1054          , 'ZA01' , 10                  , '0'                , null        , null         , null           , 0              , 1          , systimestamp, 1          , systimestamp, 0      );

insert into TLGNS_SAP_MATERIAL_SAREA
(SAP_MATERIAL_SAREA_ID           , SAP_MATERIAL_ID, SORG_CD, DISTRIBUTION_CHANNEL, MARKED_FOR_DELETION, SALES_UOM_CD, PRICING_GROUP, MATERIAL_GROUP4, IS_GENERIC_FLAG, CTL_CRE_UID, CTL_CRE_TS  , CTL_MOD_UID, CTL_MOD_TS  , CTL_TCN) values
(slgns_sap_material_sarea.nextval, -1052          , 'ZA01' , 10                  , '0'                , null        , null         , null           , 0              , 1          , systimestamp, 1          , systimestamp, 0      );

-------------------------------------------------
-- Terminal Application Test Material -> Sarea --
-------------------------------------------------

insert into TLGNS_SAP_MATERIAL_SAREA
(SAP_MATERIAL_SAREA_ID           , SAP_MATERIAL_ID, SORG_CD, DISTRIBUTION_CHANNEL, MARKED_FOR_DELETION, SALES_UOM_CD, PRICING_GROUP, MATERIAL_GROUP4, IS_GENERIC_FLAG, CTL_CRE_UID, CTL_CRE_TS  , CTL_MOD_UID, CTL_MOD_TS  , CTL_TCN) values
(slgns_sap_material_sarea.nextval, -1065          , 'AD03' , 20                  , '0'                , 'BAG'       , null         , null           , 0              , 1          , systimestamp, 1          , systimestamp, 0      );

insert into TLGNS_SAP_MATERIAL_SAREA
(SAP_MATERIAL_SAREA_ID           , SAP_MATERIAL_ID, SORG_CD, DISTRIBUTION_CHANNEL, MARKED_FOR_DELETION, SALES_UOM_CD, PRICING_GROUP, MATERIAL_GROUP4, IS_GENERIC_FLAG, CTL_CRE_UID, CTL_CRE_TS  , CTL_MOD_UID, CTL_MOD_TS  , CTL_TCN) values
(slgns_sap_material_sarea.nextval, -1040          , 'AD03' , 20                  , '0'                , null        , null         , null           , 0              , 1          , systimestamp, 1          , systimestamp, 0      );

insert into TLGNS_SAP_MATERIAL_SAREA
(SAP_MATERIAL_SAREA_ID           , SAP_MATERIAL_ID, SORG_CD, DISTRIBUTION_CHANNEL, MARKED_FOR_DELETION, SALES_UOM_CD, PRICING_GROUP, MATERIAL_GROUP4, IS_GENERIC_FLAG, CTL_CRE_UID, CTL_CRE_TS  , CTL_MOD_UID, CTL_MOD_TS  , CTL_TCN) values
(slgns_sap_material_sarea.nextval, -6003          , 'AD03' , 20                  , '0'                , null        , null         , null           , 0              , 1          , systimestamp, 1          , systimestamp, 0      );

insert into TLGNS_SAP_MATERIAL_SAREA
(SAP_MATERIAL_SAREA_ID           , SAP_MATERIAL_ID, SORG_CD, DISTRIBUTION_CHANNEL, MARKED_FOR_DELETION, SALES_UOM_CD, PRICING_GROUP, MATERIAL_GROUP4, IS_GENERIC_FLAG, CTL_CRE_UID, CTL_CRE_TS  , CTL_MOD_UID, CTL_MOD_TS  , CTL_TCN) values
(slgns_sap_material_sarea.nextval, -3066          , 'AD03' , 20                  , '0'                , null        , null         , null           , 0              , 1          , systimestamp, 1          , systimestamp, 0      );


-------------------------------------------------
-- Autoadd feature LRQ 019.0 Pallet sales area
-------------------------------------------------
insert into TLGNS_SAP_MATERIAL_SAREA
(SAP_MATERIAL_SAREA_ID           , SAP_MATERIAL_ID, SORG_CD, DISTRIBUTION_CHANNEL, MARKED_FOR_DELETION, SALES_UOM_CD, PRICING_GROUP, MATERIAL_GROUP4, IS_GENERIC_FLAG, CTL_CRE_UID, CTL_CRE_TS  , CTL_MOD_UID, CTL_MOD_TS  , CTL_TCN) values
(slgns_sap_material_sarea.nextval, -1066          , 'AD03' , 20                  , '0'                , 'BAG'       , null         , null           , 0              , 1          , systimestamp, 1          , systimestamp, 0      );

insert into TLGNS_SAP_MATERIAL_SAREA
(SAP_MATERIAL_SAREA_ID           , SAP_MATERIAL_ID, SORG_CD, DISTRIBUTION_CHANNEL, MARKED_FOR_DELETION, SALES_UOM_CD, PRICING_GROUP, MATERIAL_GROUP4, IS_GENERIC_FLAG, CTL_CRE_UID, CTL_CRE_TS  , CTL_MOD_UID, CTL_MOD_TS  , CTL_TCN) values
(slgns_sap_material_sarea.nextval, -1067          , 'AD03' , 20                  , '0'                , 'BAG'       , null         , null           , 0              , 1          , systimestamp, 1          , systimestamp, 0      );


/*
select sap_material_id, lang_cd, txt from tlgns_sap_material_description md where md.sap_material_id in
  (select distinct ci.sap_material_id from tlgns_sap_contract_item ci)
order by md.sap_material_id;
*/

prompt inserting tlgns_sap_material_description: required materials for test-contracts based from CHE prod data

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - EN (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'en', 'ADN Normo 4 Bag 25 (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'en', 'ADN Hydrolent (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'en', 'ADN Flextremo 4R (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'en', 'ADN Fluvio 4 (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'en', 'ADN Fortico 5R Sack 25 (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'en', 'ADN Provato 3R (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'en', 'ADN Normo 4 (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'en', 'ADN Modero 3B (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'en', 'ADN Normo 5 R Bag 25 kg (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'en', 'ADN Normo 5R (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'en', 'ADN Bisolvo 3R (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'en', 'ADN Flextremo 3R (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'en', 'ADN Fortico 5 (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'en', 'ADN Optimo 4 (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'en', 'ADN Wagon Mat. (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'en', 'ADN Albaro 5 R Sack 25 (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'en', 'ADN Optimo 4 Bag 25 (en)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'en', 'ADN Trigger Autoadd 1st Material (en)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'en', 'ADN Trigger Autoadd 2nd Material (en)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'en', 'ADN Mat. Deleted in AD02-10-11 (en)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'en', 'ADN Mat. Deleted! (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'en', 'ADN Bulk M3 Test (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'en', 'Bulk dumped test (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'en', 'Sac vide (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'en', 'Palette vendue (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'en', 'ELASTIQUES (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'en', 'HOUSSES (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'en', 'PALETTE PERDUE (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'en', 'Palette Perdue (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'en', 'ADN Agg Bag 1 mat. (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'en', 'ADN Agg Bag 2 mat. (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'en', 'ADN Agg Bag 3 mat. (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'en', 'ADN Agg Bag 4 mat. (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'en', 'ADN Agg Bulk 1 mat. (en)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'en', 'ADN landfill mat. (en)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'en', 'ADN Test Service 2 (en)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'en', 'ADN Test Service 3 (en)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'en', 'ADN Test Service 4 (en)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - DE (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'de', 'ADN Normo 4 Sack 25 (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'de', 'ADN Hydrolent (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'de', 'ADN Flextremo 4R (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'de', 'ADN Fluvio 4 (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'de', 'ADN Fortico 5R Sack 25 (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'de', 'ADN Provato 3R (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'de', 'ADN Normo 4 (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'de', 'ADN Modero 3B (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'de', 'ADN Normo 5 R Sack 25 (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'de', 'ADN Normo 5R (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'de', 'ADN Bisolvo 3R (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'de', 'ADN Flextremo 3R (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'de', 'ADN Fortico 5 (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'de', 'ADN Optimo 4 (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'de', 'ADN Wagon Mat. (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'de', 'ADN Albaro 5 R Sack 25 (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'de', 'ADN Optimo 4 Sack 25 (de)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'de', 'ADN Trigger Autoadd 1st Material (de)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'de', 'ADN Trigger Autoadd 2nd Material (de)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'de', 'ADN Mat. Deleted in AD02-10-11 (de)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'de', 'ADN Mat. Deleted! (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'de', 'ADN Bulk M3 Test (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'de', 'Bulk dumped test (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'de', 'Sac vide (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'de', 'Palette vendue (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'de', 'ELASTIQUES (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'de', 'HOUSSES (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'de', 'PALETTE PERDUE (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'de', 'Palette Perdue (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'de', 'ADN Agg Bag 1 mat. (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'de', 'ADN Agg Bag 2 mat. (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'de', 'ADN Agg Bag 3 mat. (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'de', 'ADN Agg Bag 4 mat. (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'de', 'ADN Agg Bulk 1 mat. (de)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'de', 'ADN landfill mat. (de)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'de', 'ADN Test Service 2 (de)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'de', 'ADN Test Service 3 (de)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'de', 'ADN Test Service 4 (de)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - FR (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'fr', 'ADN Normo 4 Sac 25 (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'fr', 'ADN Hydrolent (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'fr', 'ADN Flextremo 4R (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'fr', 'ADN Fluvio 4 (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'fr', 'ADN Fortico 5R Sac 25 (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'fr', 'ADN Provato 3R (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'fr', 'ADN Normo 4 (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'fr', 'ADN Modero 3B (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'fr', 'ADN Normo 5 R Sac 25 (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'fr', 'ADN Normo 5R (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'fr', 'ADN Bisolvo 3R (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'fr', 'ADN Flextremo 3R (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'fr', 'ADN Fortico 5 (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'fr', 'ADN Optimo 4 (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'fr', 'ADN Wagon Mat. (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'fr', 'ADN Albaro 5 R Sack 25 (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'fr', 'ADN Optimo 4 Sac 25 (fr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'fr', 'ADN Trigger Autoadd 1st Material (fr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'fr', 'ADN Trigger Autoadd 2nd Material (fr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'fr', 'ADN Mat. Deleted in AD02-10-11 (fr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'fr', 'ADN Mat. Deleted! (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'fr', 'ADN Bulk M3 Test (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'fr', 'Bulk dumped test (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'fr', 'Sac vide (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'fr', 'Palette vendue (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'fr', 'ELASTIQUES (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'fr', 'HOUSSES (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'fr', 'PALETTE PERDUE (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'fr', 'Palette Perdue (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'fr', 'ADN Agg Bag 1 mat. (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'fr', 'ADN Agg Bag 2 mat. (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'fr', 'ADN Agg Bag 3 mat. (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'fr', 'ADN Agg Bag 4 mat. (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'fr', 'ADN Agg Bulk 1 mat. (fr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'fr', 'ADN landfill mat. (fr)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'fr', 'ADN Test Service 2 (fr)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'fr', 'ADN Test Service 3 (fr)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'fr', 'ADN Test Service 4 (fr)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - IT (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'it', 'ADN Normo 4 Sac 25 (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'it', 'ADN Hydrolent (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'it', 'ADN Flextremo 4R (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'it', 'ADN Fluvio 4 (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'it', 'ADN Fortico 5R Sac 25 (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'it', 'ADN Provato 3R (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'it', 'ADN Normo 4 (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'it', 'ADN Modero 3B (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'it', 'ADN Normo 5 R Sac 25 (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'it', 'ADN Normo 5R (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'it', 'ADN Bisolvo 3R (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'it', 'ADN Flextremo 3R (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'it', 'ADN Fortico 5 (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'it', 'ADN Optimo 4 (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'it', 'ADN Wagon Mat. (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'it', 'ADN Albaro 5 R Sack 25 (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'it', 'ADN Optimo 4 Sac 25 (it)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'it', 'ADN Trigger Autoadd 1st Material (it)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'it', 'ADN Trigger Autoadd 2nd Material (it)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'it', 'ADN Mat. Deleted in AD02-10-11 (it)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'it', 'ADN Mat. Deleted! (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'it', 'ADN Bulk M3 Test (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'it', 'Bulk dumped test (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'it', 'Sac vide (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'it', 'Palette vendue (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'it', 'ELASTIQUES (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'it', 'HOUSSES (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'it', 'PALETTE PERDUE (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'it', 'Palette Perdue (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'it', 'ADN Agg Bag 1 mat. (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'it', 'ADN Agg Bag 2 mat. (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'it', 'ADN Agg Bag 3 mat. (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'it', 'ADN Agg Bag 4 mat. (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'it', 'ADN Agg Bulk 1 mat. (it)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'it', 'ADN landfill mat. (it)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'it', 'ADN Test Service 2 (it)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'it', 'ADN Test Service 3 (it)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'it', 'ADN Test Service 4 (it)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - ES (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'es', 'ADN Normo 4 Bag 25 (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'es', 'ADN Hydrolent (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'es', 'ADN Flextremo 4R (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'es', 'ADN Fluvio 4 (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'es', 'ADN Fortico 5R Sack 25 (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'es', 'ADN Provato 3R (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'es', 'ADN Normo 4 (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'es', 'ADN Modero 3B (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'es', 'ADN Normo 5 R Bag 25 kg (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'es', 'ADN Normo 5R (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'es', 'ADN Bisolvo 3R (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'es', 'ADN Flextremo 3R (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'es', 'ADN Fortico 5 (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'es', 'ADN Optimo 4 (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'es', 'ADN Wagon Mat. (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'es', 'ADN Albaro 5 R Sack 25 (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'es', 'ADN Optimo 4 Sack 25 (es)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'es', 'ADN Trigger Autoadd 1st Material (es)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'es', 'ADN Trigger Autoadd 2nd Material (es)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'es', 'ADN Mat. Deleted in AD02-10-11 (es)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'es', 'ADN Mat. Deleted! (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'es', 'ADN Bulk M3 Test (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'es', 'Bulk dumped test (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'es', 'Sac vide (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'es', 'Palette vendue (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'es', 'ELASTIQUES (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'es', 'HOUSSES (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'es', 'PALETTE PERDUE (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'es', 'Palette Perdue (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'es', 'ADN Agg Bag 1 mat. (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'es', 'ADN Agg Bag 2 mat. (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'es', 'ADN Agg Bag 3 mat. (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'es', 'ADN Agg Bag 4 mat. (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'es', 'ADN Agg Bulk 1 mat. (es)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'es', 'ADN landfill mat. (es)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'es', 'ADN Test Service 2 (es)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'es', 'ADN Test Service 3 (es)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'es', 'ADN Test Service 4 (es)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - CS (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'cs', 'ADN Normo 4 Bag 25 (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'cs', 'ADN Hydrolent (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'cs', 'ADN Flextremo 4R (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'cs', 'ADN Fluvio 4 (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'cs', 'ADN Fortico 5R Sack 25 (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'cs', 'ADN Provato 3R (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'cs', 'ADN Normo 4 (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'cs', 'ADN Modero 3B (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'cs', 'ADN Normo 5 R Bag 25 kg (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'cs', 'ADN Normo 5R (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'cs', 'ADN Bisolvo 3R (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'cs', 'ADN Flextremo 3R (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'cs', 'ADN Fortico 5 (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'cs', 'ADN Optimo 4 (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'cs', 'ADN Wagon Mat. (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'cs', 'ADN Albaro 5 R Sack 25 (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'cs', 'ADN Optimo 4 Bag 25 (cs)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'cs', 'ADN Trigger Autoadd 1st Material (cs)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'cs', 'ADN Trigger Autoadd 2nd Material (cs)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'cs', 'ADN Mat. Deleted in AD02-10-11 (cs)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'cs', 'ADN Mat. Deleted! (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'cs', 'ADN Bulk M3 Test (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'cs', 'Bulk dumped test (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'cs', 'Sac vide (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'cs', 'Palette vendue (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'cs', 'ELASTIQUES (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'cs', 'HOUSSES (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'cs', 'PALETTE PERDUE (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'cs', 'Palette Perdue (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'cs', 'ADN Agg Bag 1 mat. (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'cs', 'ADN Agg Bag 2 mat. (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'cs', 'ADN Agg Bag 3 mat. (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'cs', 'ADN Agg Bag 4 mat. (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'cs', 'ADN Agg Bulk 1 mat. (cs)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'cs', 'ADN landfill mat. (cs)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'cs', 'ADN Test Service 2 (cs)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'cs', 'ADN Test Service 3 (cs)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'cs', 'ADN Test Service 4 (cs)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - SK (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'sk', 'ADN Normo 4 Bag 25 (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'sk', 'ADN Hydrolent (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'sk', 'ADN Flextremo 4R (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'sk', 'ADN Fluvio 4 (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'sk', 'ADN Fortico 5R Sack 25 (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'sk', 'ADN Provato 3R (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'sk', 'ADN Normo 4 (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'sk', 'ADN Modero 3B (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'sk', 'ADN Normo 5 R Bag 25 kg (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'sk', 'ADN Normo 5R (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'sk', 'ADN Bisolvo 3R (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'sk', 'ADN Flextremo 3R (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'sk', 'ADN Fortico 5 (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'sk', 'ADN Optimo 4 (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'sk', 'ADN Wagon Mat. (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'sk', 'ADN Albaro 5 R Sack 25 (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'sk', 'ADN Optimo 4 Bag 25 (sk)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'sk', 'ADN Trigger Autoadd 1st Material (sk)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'sk', 'ADN Trigger Autoadd 2nd Material (sk)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'sk', 'ADN Mat. Deleted in AD02-10-11 (sk)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'sk', 'ADN Mat. Deleted! (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'sk', 'ADN Bulk M3 Test (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'sk', 'Bulk dumped test (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'sk', 'Sac vide (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'sk', 'Palette vendue (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'sk', 'ELASTIQUES (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'sk', 'HOUSSES (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'sk', 'PALETTE PERDUE (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'sk', 'Palette Perdue (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'sk', 'ADN Agg Bag 1 mat. (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'sk', 'ADN Agg Bag 2 mat. (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'sk', 'ADN Agg Bag 3 mat. (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'sk', 'ADN Agg Bag 4 mat. (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'sk', 'ADN Agg Bulk 1 mat. (sk)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'sk', 'ADN landfill mat. (sk)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'sk', 'ADN Test Service 2 (sk)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'sk', 'ADN Test Service 3 (sk)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'sk', 'ADN Test Service 4 (sk)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - HU (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'hu', 'ADN Normo 4 Bag 25 (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'hu', 'ADN Hydrolent (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'hu', 'ADN Flextremo 4R (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'hu', 'ADN Fluvio 4 (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'hu', 'ADN Fortico 5R Sack 25 (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'hu', 'ADN Provato 3R (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'hu', 'ADN Normo 4 (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'hu', 'ADN Modero 3B (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'hu', 'ADN Normo 5 R Bag 25 kg (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'hu', 'ADN Normo 5R (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'hu', 'ADN Bisolvo 3R (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'hu', 'ADN Flextremo 3R (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'hu', 'ADN Fortico 5 (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'hu', 'ADN Optimo 4 (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'hu', 'ADN Wagon Mat. (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'hu', 'ADN Albaro 5 R Sack 25 (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'hu', 'ADN Optimo 4 Bag 25 (hu)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'hu', 'ADN Trigger Autoadd 1st Material (hu)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'hu', 'ADN Trigger Autoadd 2nd Material (hu)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'hu', 'ADN Mat. Deleted in AD02-10-11 (hu)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'hu', 'ADN Mat. Deleted! (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'hu', 'ADN Bulk M3 Test (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'hu', 'Bulk dumped test (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'hu', 'Sac vide (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'hu', 'Palette vendue (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'hu', 'ELASTIQUES (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'hu', 'HOUSSES (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'hu', 'PALETTE PERDUE (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'hu', 'Palette Perdue (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'hu', 'ADN Agg Bag 1 mat. (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'hu', 'ADN Agg Bag 2 mat. (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'hu', 'ADN Agg Bag 3 mat. (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'hu', 'ADN Agg Bag 4 mat. (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'hu', 'ADN Agg Bulk 1 mat. (hu)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'hu', 'ADN landfill mat. (hu)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'hu', 'ADN Test Service 2 (hu)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'hu', 'ADN Test Service 3 (hu)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'hu', 'ADN Test Service 4 (hu)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - RO (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'ro', 'ADN Normo 4 Bag 25 (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'ro', 'ADN Hydrolent (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'ro', 'ADN Flextremo 4R (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'ro', 'ADN Fluvio 4 (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'ro', 'ADN Fortico 5R Sack 25 (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'ro', 'ADN Provato 3R (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'ro', 'ADN Normo 4 (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'ro', 'ADN Modero 3B (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'ro', 'ADN Normo 5 R Bag 25 kg (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'ro', 'ADN Normo 5R (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'ro', 'ADN Bisolvo 3R (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'ro', 'ADN Flextremo 3R (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'ro', 'ADN Fortico 5 (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'ro', 'ADN Optimo 4 (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'ro', 'ADN Wagon Mat. (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'ro', 'ADN Albaro 5 R Sack 25 (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'ro', 'ADN Optimo 4 Bag 25 (ro)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'ro', 'ADN Trigger Autoadd 1st Material (ro)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'ro', 'ADN Trigger Autoadd 2nd Material (ro)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'ro', 'ADN Mat. Deleted in AD02-10-11 (ro)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'ro', 'ADN Mat. Deleted! (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'ro', 'ADN Bulk M3 Test (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'ro', 'Bulk dumped test (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'ro', 'Sac vide (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'ro', 'Palette vendue (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'ro', 'ELASTIQUES (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'ro', 'HOUSSES (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'ro', 'PALETTE PERDUE (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'ro', 'Palette Perdue (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'ro', 'ADN Agg Bag 1 mat. (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'ro', 'ADN Agg Bag 2 mat. (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'ro', 'ADN Agg Bag 3 mat. (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'ro', 'ADN Agg Bag 4 mat. (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'ro', 'ADN Agg Bulk 1 mat. (ro)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'ro', 'ADN landfill mat. (ro)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'ro', 'ADN Test Service 2 (ro)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'ro', 'ADN Test Service 3 (ro)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'ro', 'ADN Test Service 4 (ro)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - BG (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'bg', 'ADN Normo 4 Bag 25 (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'bg', 'ADN Hydrolent (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'bg', 'ADN Flextremo 4R (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'bg', 'ADN Fluvio 4 (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'bg', 'ADN Fortico 5R Sack 25 (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'bg', 'ADN Provato 3R (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'bg', 'ADN Normo 4 (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'bg', 'ADN Modero 3B (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'bg', 'ADN Normo 5 R Bag 25 kg (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'bg', 'ADN Normo 5R (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'bg', 'ADN Bisolvo 3R (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'bg', 'ADN Flextremo 3R (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'bg', 'ADN Fortico 5 (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'bg', 'ADN Optimo 4 (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'bg', 'ADN Wagon Mat. (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'bg', 'ADN Albaro 5 R Sack 25 (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'bg', 'ADN Optimo 4 Bag 25 (bg)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'bg', 'ADN Trigger Autoadd 1st Material (bg)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'bg', 'ADN Trigger Autoadd 2nd Material (bg)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'bg', 'ADN Mat. Deleted in AD02-10-11 (bg)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'bg', 'ADN Mat. Deleted! (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'bg', 'ADN Bulk M3 Test (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'bg', 'Bulk dumped test (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'bg', 'Sac vide (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'bg', 'Palette vendue (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'bg', 'ELASTIQUES (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'bg', 'HOUSSES (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'bg', 'PALETTE PERDUE (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'bg', 'Palette Perdue (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'bg', 'ADN Agg Bag 1 mat. (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'bg', 'ADN Agg Bag 2 mat. (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'bg', 'ADN Agg Bag 3 mat. (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'bg', 'ADN Agg Bag 4 mat. (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'bg', 'ADN Agg Bulk 1 mat. (bg)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'bg', 'ADN landfill mat. (bg)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'bg', 'ADN Test Service 2 (bg)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'bg', 'ADN Test Service 3 (bg)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'bg', 'ADN Test Service 4 (bg)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - RU (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'ru', 'ADN Normo 4 Bag 25 (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'ru', 'ADN Hydrolent (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'ru', 'ADN Flextremo 4R (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'ru', 'ADN Fluvio 4 (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'ru', 'ADN Fortico 5R Sack 25 (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'ru', 'ADN Provato 3R (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'ru', 'ADN Normo 4 (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'ru', 'ADN Modero 3B (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'ru', 'ADN Normo 5 R Bag 25 kg (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'ru', 'ADN Normo 5R (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'ru', 'ADN Bisolvo 3R (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'ru', 'ADN Flextremo 3R (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'ru', 'ADN Fortico 5 (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'ru', 'ADN Optimo 4 (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'ru', 'ADN Albaro 5 R Sack 25 (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'ru', 'ADN Optimo 4 Bag 25 (ru)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'ru', 'ADN Trigger Autoadd 1st Material (ru)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'ru', 'ADN Trigger Autoadd 2nd Material (ru)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'ru', 'ADN Mat. Deleted in AD02-10-11 (ru)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'ru', 'ADN Mat. Deleted! (ru)');

--Pallet description
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'ru', 'Sac vide (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'ru', 'Palette vendue (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'ru', 'ELASTIQUES (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'ru', 'HOUSSES (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'ru', 'PALETTE PERDUE (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'ru', 'Palette Perdue (ru)');

insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'ru', 'ADN Wagon Mat. (ru)');

-- Aggregate
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'ru', 'ADN Agg Bag 1 mat. (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'ru', 'ADN Agg Bag 2 mat. (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'ru', 'ADN Agg Bag 3 mat. (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'ru', 'ADN Agg Bag 4 mat. (ru)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'ru', 'ADN Agg Bulk 1 mat. (ru)');

-- Landfill
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'ru', 'ADN landfill mat. (ru)');

-- Services 2-4
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'ru', 'ADN Test Service 2 (ru)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'ru', 'ADN Test Service 3 (ru)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'ru', 'ADN Test Service 4 (ru)');

-- Cubic meters loading related testdata, for 'ADN Bulk M3 Test'
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'ru', 'ADN Bulk M3 Test (ru)');

insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'ru', 'Bulk dumped test (ru)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - PL (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'pl', 'ADN Normo 4 Bag 25 (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'pl', 'ADN Hydrolent (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'pl', 'ADN Flextremo 4R (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'pl', 'ADN Fluvio 4 (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'pl', 'ADN Fortico 5R Sack 25 (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'pl', 'ADN Provato 3R (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'pl', 'ADN Normo 4 (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'pl', 'ADN Modero 3B (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'pl', 'ADN Normo 5 R Bag 25 kg (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'pl', 'ADN Normo 5R (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'pl', 'ADN Bisolvo 3R (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'pl', 'ADN Flextremo 3R (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'pl', 'ADN Fortico 5 (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'pl', 'ADN Optimo 4 (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'pl', 'ADN Albaro 5 R Sack 25 (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'pl', 'ADN Optimo 4 Bag 25 (pl)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'pl', 'ADN Trigger Autoadd 1st Material (pl)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'pl', 'ADN Trigger Autoadd 2nd Material (pl)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'pl', 'ADN Mat. Deleted in AD02-10-11 (pl)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'pl', 'ADN Mat. Deleted! (pl)');

--Pallet description
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'pl', 'Sac vide (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'pl', 'Palette vendue (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'pl', 'ELASTIQUES (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'pl', 'HOUSSES (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'pl', 'PALETTE PERDUE (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'pl', 'Palette Perdue (pl)');

insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'pl', 'ADN Wagon Mat. (pl)');

-- Aggregate
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'pl', 'ADN Agg Bag 1 mat. (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'pl', 'ADN Agg Bag 2 mat. (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'pl', 'ADN Agg Bag 3 mat. (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'pl', 'ADN Agg Bag 4 mat. (pl)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'pl', 'ADN Agg Bulk 1 mat. (pl)');

-- Landfill
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'pl', 'ADN landfill mat. (pl)');

-- Services 2-4
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'pl', 'ADN Test Service 2 (pl)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'pl', 'ADN Test Service 3 (pl)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'pl', 'ADN Test Service 4 (pl)');

-- Cubic meters loading related testdata, for 'ADN Bulk M3 Test'
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'pl', 'ADN Bulk M3 Test (pl)');

insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'pl', 'Bulk dumped test (pl)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - AZ (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'az', 'ADN Normo 4 Bag 25 (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'az', 'ADN Hydrolent (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'az', 'ADN Flextremo 4R (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'az', 'ADN Fluvio 4 (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'az', 'ADN Fortico 5R Sack 25 (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'az', 'ADN Provato 3R (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'az', 'ADN Normo 4 (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'az', 'ADN Modero 3B (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'az', 'ADN Normo 5 R Bag 25 kg (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'az', 'ADN Normo 5R (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'az', 'ADN Bisolvo 3R (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'az', 'ADN Flextremo 3R (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'az', 'ADN Fortico 5 (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'az', 'ADN Optimo 4 (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'az', 'ADN Albaro 5 R Sack 25 (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'az', 'ADN Optimo 4 Bag 25 (az)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'az', 'ADN Trigger Autoadd 1st Material (az)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'az', 'ADN Trigger Autoadd 2nd Material (az)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'az', 'ADN Mat. Deleted in AD02-10-11 (az)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'az', 'ADN Mat. Deleted! (az)');

--Pallet description
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'az', 'Sac vide (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'az', 'Palette vendue (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'az', 'ELASTIQUES (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'az', 'HOUSSES (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'az', 'PALETTE PERDUE (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'az', 'Palette Perdue (az)');

insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'az', 'ADN Wagon Mat. (az)');

-- Aggregate
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'az', 'ADN Agg Bag 1 mat. (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'az', 'ADN Agg Bag 2 mat. (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'az', 'ADN Agg Bag 3 mat. (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'az', 'ADN Agg Bag 4 mat. (az)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'az', 'ADN Agg Bulk 1 mat. (az)');

-- Landfill
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'az', 'ADN landfill mat. (az)');

-- Services 2-4
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'az', 'ADN Test Service 2 (az)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'az', 'ADN Test Service 3 (az)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'az', 'ADN Test Service 4 (az)');

-- Cubic meters loading related testdata, for 'ADN Bulk M3 Test'
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'az', 'ADN Bulk M3 Test (az)');

insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'az', 'Bulk dumped test (az)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - HR (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'hr', 'ADN Normo 4 Bag 25 (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'hr', 'ADN Hydrolent (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'hr', 'ADN Flextremo 4R (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'hr', 'ADN Fluvio 4 (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'hr', 'ADN Fortico 5R Sack 25 (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'hr', 'ADN Provato 3R (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'hr', 'ADN Normo 4 (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'hr', 'ADN Modero 3B (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'hr', 'ADN Normo 5 R Bag 25 kg (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'hr', 'ADN Normo 5R (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'hr', 'ADN Bisolvo 3R (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'hr', 'ADN Flextremo 3R (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'hr', 'ADN Fortico 5 (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'hr', 'ADN Optimo 4 (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'hr', 'ADN Albaro 5 R Sack 25 (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'hr', 'ADN Optimo 4 Bag 25 (hr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'hr', 'ADN Trigger Autoadd 1st Material (hr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'hr', 'ADN Trigger Autoadd 2nd Material (hr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'hr', 'ADN Mat. Deleted in AD02-10-11 (hr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'hr', 'ADN Mat. Deleted! (hr)');

--Pallet description
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'hr', 'Sac vide (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'hr', 'Palette vendue (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'hr', 'ELASTIQUES (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'hr', 'HOUSSES (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'hr', 'PALETTE PERDUE (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'hr', 'Palette Perdue (hr)');

insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'hr', 'ADN Wagon Mat. (hr)');

-- Aggregate
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'hr', 'ADN Agg Bag 1 mat. (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'hr', 'ADN Agg Bag 2 mat. (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'hr', 'ADN Agg Bag 3 mat. (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'hr', 'ADN Agg Bag 4 mat. (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'hr', 'ADN Agg Bulk 1 mat. (hr)');

-- Landfill
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'hr', 'ADN landfill mat. (hr)');

-- Services 2-4
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'hr', 'ADN Test Service 2 (hr)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'hr', 'ADN Test Service 3 (hr)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'hr', 'ADN Test Service 4 (hr)');

-- Cubic meters loading related testdata, for 'ADN Bulk M3 Test'
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'hr', 'ADN Bulk M3 Test (hr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'hr', 'Bulk dumped test (hr)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - SR (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'sr', 'ADN Normo 4 Bag 25 (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'sr', 'ADN Hydrolent (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'sr', 'ADN Flextremo 4R (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'sr', 'ADN Fluvio 4 (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'sr', 'ADN Fortico 5R Sack 25 (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'sr', 'ADN Provato 3R (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'sr', 'ADN Normo 4 (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'sr', 'ADN Modero 3B (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'sr', 'ADN Normo 5 R Bag 25 kg (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'sr', 'ADN Normo 5R (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'sr', 'ADN Bisolvo 3R (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'sr', 'ADN Flextremo 3R (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'sr', 'ADN Fortico 5 (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'sr', 'ADN Optimo 4 (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'sr', 'ADN Albaro 5 R Sack 25 (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'sr', 'ADN Optimo 4 Bag 25 (sr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'sr', 'ADN Trigger Autoadd 1st Material (sr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'sr', 'ADN Trigger Autoadd 2nd Material (sr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'sr', 'ADN Mat. Deleted in AD02-10-11 (sr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'sr', 'ADN Mat. Deleted! (sr)');

--Pallet description
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'sr', 'Sac vide (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'sr', 'Palette vendue (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'sr', 'ELASTIQUES (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'sr', 'HOUSSES (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'sr', 'PALETTE PERDUE (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'sr', 'Palette Perdue (sr)');

insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'sr', 'ADN Wagon Mat. (sr)');

-- Aggregate
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'sr', 'ADN Agg Bag 1 mat. (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'sr', 'ADN Agg Bag 2 mat. (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'sr', 'ADN Agg Bag 3 mat. (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'sr', 'ADN Agg Bag 4 mat. (sr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'sr', 'ADN Agg Bulk 1 mat. (sr)');

-- Landfill
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'sr', 'ADN landfill mat. (sr)');

-- Services 2-4
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'sr', 'ADN Test Service 2 (sr)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'sr', 'ADN Test Service 3 (sr)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'sr', 'ADN Test Service 4 (sr)');

-- Cubic meters loading related testdata, for 'ADN Bulk M3 Test'
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'sr', 'ADN Bulk M3 Test (sr)');

insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'sr', 'Bulk dumped test (sr)');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - TR (ENGLISH COPY)
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1027, 'tr', 'ADN Normo 4 Bag 25 (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1029, 'tr', 'ADN Hydrolent (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1031, 'tr', 'ADN Flextremo 4R (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1040, 'tr', 'ADN Fluvio 4 (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1049, 'tr', 'ADN Fortico 5R Sack 25 (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1052, 'tr', 'ADN Provato 3R (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1054, 'tr', 'ADN Normo 4 (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1057, 'tr', 'ADN Modero 3B (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1065, 'tr', 'ADN Normo 5 R Bag 25 kg (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1068, 'tr', 'ADN Normo 5R (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1118, 'tr', 'ADN Bisolvo 3R (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1125, 'tr', 'ADN Flextremo 3R (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1454, 'tr', 'ADN Fortico 5 (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1641, 'tr', 'ADN Optimo 4 (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1902632, 'tr', 'ADN Wagon Mat. (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2401, 'tr', 'ADN Albaro 5 R Sack 25 (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2701, 'tr', 'ADN Optimo 4 Bag 25 (tr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1066, 'tr', 'ADN Trigger Autoadd 1st Material (tr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1067, 'tr', 'ADN Trigger Autoadd 2nd Material (tr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1070, 'tr', 'ADN Mat. Deleted in AD02-10-11 (tr)');
Insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-1069, 'tr', 'ADN Mat. Deleted! (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2702, 'tr', 'ADN Bulk M3 Test (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-2703, 'tr', 'Bulk dumped test (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3039, 'tr', 'Sac vide (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3046, 'tr', 'Palette vendue (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3053, 'tr', 'ELASTIQUES (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3059, 'tr', 'HOUSSES (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3060, 'tr', 'PALETTE PERDUE (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-3066, 'tr', 'Palette Perdue (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4060, 'tr', 'ADN Agg Bag 1 mat. (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4061, 'tr', 'ADN Agg Bag 2 mat. (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4062, 'tr', 'ADN Agg Bag 3 mat. (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4063, 'tr', 'ADN Agg Bag 4 mat. (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-4064, 'tr', 'ADN Agg Bulk 1 mat. (tr)');
insert into tlgns_sap_material_description (SAP_MATERIAL_ID,LANG_CD,TXT) values (-5010, 'tr', 'ADN landfill mat. (tr)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6002, 'tr', 'ADN Test Service 2 (tr)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6003, 'tr', 'ADN Test Service 3 (tr)');
insert into TLGNS_SAP_MATERIAL_DESCRIPTION (SAP_MATERIAL_ID,LANG_CD,TXT) values (-6004, 'tr', 'ADN Test Service 4 (tr)');

commit;

