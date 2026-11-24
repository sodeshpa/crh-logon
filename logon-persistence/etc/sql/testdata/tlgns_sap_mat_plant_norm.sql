-- Material norms for testing: LOGON-4585 LRQ 047.2 Interface: Material Norms (Implementing) (Block 3)
-- Production Plant: 	AD CEM Plant 8101 (8101)		8101
-- Material: 		ADN Flextremo 3R (ADN1405002)		-1125 

prompt '---------- testdata/tlgns_sap_mat_plant_norm.sql ----------'

prompt INSERTING into TLGNS_SAP_MAT_PLANT_NORM

Insert into TLGNS_SAP_MAT_PLANT_NORM (
  SAP_MAT_PLANT_NORM_ID, SAP_MATERIAL_ID, PLANT_CD,
  SAP_MATERIAL_NORM_CD, SAP_LANGUAGE_CD, SORG_CD, 
  SHOW_LOGO_FLAG, ZCODE1, ZCODE2, 
  TEXT1, TEXT2, TEXT3, 
  MARKED_FOR_DELETION, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN
) values (
  -1000, -1125, '8101', 
  'U', 'FR', 'AD02', 
  1, '7', 'B12', 
  'Exact1 - l1 ','Exact1 - l2', null,
  0, 1, systimestamp, 1, systimestamp, 0
);

Insert into TLGNS_SAP_MAT_PLANT_NORM (
  SAP_MAT_PLANT_NORM_ID, SAP_MATERIAL_ID, PLANT_CD,
  SAP_MATERIAL_NORM_CD, SAP_LANGUAGE_CD, SORG_CD, 
  SHOW_LOGO_FLAG, ZCODE1, ZCODE2, 
  TEXT1, TEXT2, TEXT3, 
  MARKED_FOR_DELETION, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN
) values (
  -1004, -1125, '8101', 
  'CE', 'FR', null, 
  1, '7', 'B12', 
  'Sorgnull1 - l1','Sorgnull1 - l2', null,
  0, 1, systimestamp, 1, systimestamp, 0
);

commit;