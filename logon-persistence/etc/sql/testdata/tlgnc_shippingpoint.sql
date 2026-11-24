prompt INSERTING into TLGNC_SHIPPINGPOINT
Insert into TLGNC_SHIPPINGPOINT (SHIPPINGPOINT_ID, PLANT_CD, SAP_SHIPPINGPOINT_NO, known_also_in_sap_flag, SHIPPINGPOINT_NAME, SHIPPPOINT_TYPE_CD, SAP_SHIPPING_COND_CD, WEIGH_SYSTEM_SP_CD, LDS_SERVER, RAILWAY_STATION_ID, SHIPPINGPOINT_COMMENT, COORD_LAT, COORD_LON, COORD_FIX_FLAG, GEOFENCE_RADIUS, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN)
  values (-21, '.SP6', '.SP6', 0, 'Return Bürglen', 6, 'S1', 0, null, 1621, 'Return shipping point for Bürglen', null, null, 0, 0, 1, systimestamp, 1, systimestamp, 1);
Insert into TLGNC_SHIPPINGPOINT (SHIPPINGPOINT_ID, PLANT_CD, SAP_SHIPPINGPOINT_NO, known_also_in_sap_flag, SHIPPINGPOINT_NAME, SHIPPPOINT_TYPE_CD, SAP_SHIPPING_COND_CD, WEIGH_SYSTEM_SP_CD, LDS_SERVER, RAILWAY_STATION_ID, SHIPPINGPOINT_COMMENT, COORD_LAT, COORD_LON, COORD_FIX_FLAG, GEOFENCE_RADIUS, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN)
  values (-22, '.SP6', '.SP7', 0, 'Return Tiger II', 6, 'S1', 0, null, 1291, 'Return shipping point for ARGE TIGER II', null, null, 0, 0, 1, systimestamp, 1, systimestamp, 1);
Insert into TLGNC_SHIPPINGPOINT (SHIPPINGPOINT_ID, PLANT_CD, SAP_SHIPPINGPOINT_NO, known_also_in_sap_flag, SHIPPINGPOINT_NAME, SHIPPPOINT_TYPE_CD, SAP_SHIPPING_COND_CD, WEIGH_SYSTEM_SP_CD, LDS_SERVER, RAILWAY_STATION_ID, SHIPPINGPOINT_COMMENT, COORD_LAT, COORD_LON, COORD_FIX_FLAG, GEOFENCE_RADIUS, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN)
  values (-23, '.SP6', '.SP8', 0, 'Return FVL Glattbrugg', 6, 'S1', 0, null, 1379, 'Return shipping point forFVL Glattbrugg', null, null, 0, 0, 1, systimestamp, 1, systimestamp, 1);

Insert into TLGNC_SHIPPINGPOINT (SHIPPINGPOINT_ID, PLANT_CD, SAP_SHIPPINGPOINT_NO, known_also_in_sap_flag, SHIPPINGPOINT_NAME, SHIPPPOINT_TYPE_CD, SAP_SHIPPING_COND_CD, WEIGH_SYSTEM_SP_CD, LDS_SERVER, RAILWAY_STATION_ID, SHIPPINGPOINT_COMMENT, COORD_LAT, COORD_LON, COORD_FIX_FLAG, GEOFENCE_RADIUS, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN)
  values (-30, '.SP5', '.SP9', 0, 'Return Repair Zwingen', 5, 'S1', 0, null, 1037, 'Return shipping point for Repair plant Zwingen', null, null, 0, 0, 1, systimestamp, 1, systimestamp, 1);
Insert into TLGNC_SHIPPINGPOINT (SHIPPINGPOINT_ID, PLANT_CD, SAP_SHIPPINGPOINT_NO, known_also_in_sap_flag, SHIPPINGPOINT_NAME, SHIPPPOINT_TYPE_CD, SAP_SHIPPING_COND_CD, WEIGH_SYSTEM_SP_CD, LDS_SERVER, RAILWAY_STATION_ID, SHIPPINGPOINT_COMMENT, COORD_LAT, COORD_LON, COORD_FIX_FLAG, GEOFENCE_RADIUS, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN)
  values (-31, '.SP5', '.SPA', 0, 'Return Repair Bauarena', 5, 'S1', 0, null, 1111, 'Return shipping point for Repair plant Bauarena', null, null, 0, 0, 1, systimestamp, 1, systimestamp, 1);


update tlgnc_shipto
set rail_chain_shippingpoint_id = -21
where shipto_sap_bp_id =
  (select sap_bp_id from tlgns_sap_bp where sap_id='1208422');

update tlgnc_shipto
set rail_chain_shippingpoint_id = -22
where shipto_sap_bp_id =
  (select sap_bp_id from tlgns_sap_bp where sap_id='1767913');

  update tlgnc_shipto
set rail_chain_shippingpoint_id = -23
where shipto_sap_bp_id =
  (select sap_bp_id from tlgns_sap_bp where sap_id='1866134');
  
  update tlgnc_shipto
set rail_chain_shippingpoint_id = -30
where shipto_sap_bp_id =
  (select sap_bp_id from tlgns_sap_bp where sap_id='1208355');

  update tlgnc_shipto
set rail_chain_shippingpoint_id = -31
where shipto_sap_bp_id =
  (select sap_bp_id from tlgns_sap_bp where sap_id='1626412');
  

commit;