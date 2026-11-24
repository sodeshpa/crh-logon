----------------------------------------------------------------------------------------------------------------|
-- CRHLOGON-180                                                                                                   |
----------------------------------------------------------------------------------------------------------------|

set autocommit off
whenever SQLERROR EXIT ROLLBACK
COLUMN spool_file_name NEW_VALUE spool_file_name NOPRINT

--log file name like [connected user]__[SID fullname]__anonymization__[timestamp].log
select  
'/temp/' 
    || user || '__' 
    ||  global_name || '__' 
    || 'anonymization'    || '__' 
    || to_char(sysdate, 'yyyymmddHH24MISS') || '.log'
  as spool_file_name from (select global_name from global_name), dual;

SPOOL &spool_file_name
prompt Patch log available at &&spool_file_name

-- Default Logon NLS settings
@@../../set_logon_session_settings.sql

--create the helper package
@@transform_data_pkg.sql

prompt Time STARTED:
select systimestamp from dual;

prompt ****************************************************************
prompt *** Patching Logon Database (anonymize all confidential data)
prompt ****************************************************************

prompt START anonymize confidential data

prompt START Block 1 - setting values
update ulgn01.TLGNC_ASSIGNMENT set LDS_DISPATCHER_TEL='+36 1 487 5000' where LDS_DISPATCHER_TEL is not null;
commit;
update ulgn01.TLGNC_CUSTORDER set ORDER_CONF_EMAIL_ADRESSES='crh-logon-test@adnovum.ch' where ORDER_CONF_EMAIL_ADRESSES is not null;
commit;
update ulgn01.TLGNC_CUSTORDER set ORDER_CONF_FAX_NUMBER='+36 1 487 5000' where ORDER_CONF_FAX_NUMBER is not null;
commit;
update ulgn01.TLGNC_CUSTORDER set ORDER_CONF_SMS_NUMBER='+36 1 487 5000' where ORDER_CONF_SMS_NUMBER is not null;
commit;
update ulgn01.TLGNC_CUSTORDER_ITEM set RATE=4 where RATE is not null;
commit;
update ulgn01.TLGNC_FREIGHT_COST set RATE=4 where RATE is not null;
commit;
update ulgn01.TLGNC_FUELING set PRICE=4 where PRICE is not null;
commit;
update ulgn01.TLGNC_IUD_COST set VAR_IUD_COST=4 where VAR_IUD_COST is not null;
commit;
update ulgn01.TLGNC_IUD_COST set FUTURE_VAR_IUD_COST=4 where FUTURE_VAR_IUD_COST is not null;
commit;
update ulgn01.TLGNC_MAT_PLANT set VARIABLE_PROD_COST_MAT=4 where VARIABLE_PROD_COST_MAT is not null;
commit;
update ulgn01.TLGNC_MAT_PLANT set FUTURE_VARIABLE_PROD_COST_MAT=4 where FUTURE_VARIABLE_PROD_COST_MAT is not null;
commit;
update ulgn01.TLGNC_MAT_SHIPPINGPOINT set VAR_HANDLING_COST_MAT=4 where VAR_HANDLING_COST_MAT is not null;
commit;
update ulgn01.TLGNC_MAT_SHIPPINGPOINT set FUTURE_VAR_HANDLING_COST_MAT=4 where FUTURE_VAR_HANDLING_COST_MAT is not null;
commit;
update ulgn01.TLGNC_ORDER set FREIGHT_COST=4 where FREIGHT_COST is not null;
commit;
update ulgn01.TLGNC_ORDER set ORDER_CONF_EMAIL_ADRESSES='crh-logon-test@adnovum.ch' where ORDER_CONF_EMAIL_ADRESSES is not null;
commit;
update ulgn01.TLGNC_ORDER set ORDER_CONF_FAX_NUMBER='+36 1 487 5000' where ORDER_CONF_FAX_NUMBER is not null;
commit;
update ulgn01.TLGNC_ORDER set ORDER_CONF_SMS_NUMBER='+36 1 487 5000' where ORDER_CONF_SMS_NUMBER is not null;
commit;
update ulgn01.TLGNC_ORDER set CUSTOMS_NOTIFICATION_NUMBER=1111 where CUSTOMS_NOTIFICATION_NUMBER is not null;
commit;
update ulgn01.TLGNC_ORDER set ORDER_VALIDATION_CODE='11' where ORDER_VALIDATION_CODE is not null;
commit;
update ulgn01.TLGNC_ORDER_ITEM set RATE=4 where RATE is not null;
commit;
update ulgn01.TLGNC_ORDER_ITEM set GROSS_PRICE_MAT=4 where GROSS_PRICE_MAT is not null;
commit;
update ulgn01.TLGNC_ORDER_ITEM set PROD_COST_MAT=4 where PROD_COST_MAT is not null;
commit;
update ulgn01.TLGNC_ORDER_ITEM set HANDLING_COST_MAT=4 where HANDLING_COST_MAT is not null;
commit;
update ulgn01.TLGNC_ORDER_ITEM set IUD_COST=4 where IUD_COST is not null;
commit;
update ulgn01.TLGNC_ORDER_ITEM set OUT_COST=4 where OUT_COST is not null;
commit;
update ulgn01.TLGNC_ORDER_ITEM set NET_VALUE=4 where NET_VALUE is not null;
commit;
update ulgn01.TLGNC_ORDER_ITEM set VAT_PERCENTAGE=4 where VAT_PERCENTAGE is not null;
commit;
update ulgn01.TLGNC_ORDER_PRESCHEDULE set FREIGHT_COST=4 where FREIGHT_COST is not null;
commit;
update ulgn01.TLGNC_ORDER_REP set EMAIL_TO_ADRESSES='crh-logon-test@adnovum.ch' where EMAIL_TO_ADRESSES is not null;
commit;
update ulgn01.TLGNC_ORDER_REP set EMAIL_CC_ADRESSES='crh-logon-test@adnovum.ch' where EMAIL_CC_ADRESSES is not null;
commit;
update ulgn01.TLGNC_ORDER_REP set EMAIL_BCC_ADRESSES='crh-logon-test@adnovum.ch' where EMAIL_BCC_ADRESSES is not null;
commit;
update ulgn01.TLGNC_ORDER_REP set OFFER_AMOUNT=4 where OFFER_AMOUNT is not null;
commit;
update ulgn01.TLGNC_ORDER_REP set INVOICE_AMOUNT=4 where INVOICE_AMOUNT is not null;
commit;
update ulgn01.TLGNC_ORDER_REP set INVOICE_NO=11111111 where INVOICE_NO is not null;
commit;
update ulgn01.TLGNC_PERSON set EMAIL='crh-logon-test@adnovum.ch' where EMAIL is not null;
commit;
update ulgn01.TLGNC_PRICING_CALL set NET_VALUE=4 where NET_VALUE is not null;
commit;
update ulgn01.TLGNC_PRICING_CALL set VAT_AMOUNT=4 where VAT_AMOUNT is not null;
commit;
update ulgn01.TLGNC_RAIL_TARIFF set TARIFF_NUMBER='11' where TARIFF_NUMBER is not null;
commit;
update ulgn01.TLGNC_SAREA set MAIL_FROM='crh-logon-test@adnovum.ch' where MAIL_FROM is not null;
commit;
update ulgn01.TLGNC_SAREA set MAIL_TEST_TO='crh-logon-test@adnovum.ch' where MAIL_TEST_TO is not null;
commit;
update ulgn01.TLGNC_SAREA set MAIL_BCC='crh-logon-test@adnovum.ch' where MAIL_BCC is not null;
commit;
update ulgn01.TLGNC_SAREA set MAIL_RAILADMIN='crh-logon-test@adnovum.ch' where MAIL_RAILADMIN is not null;
commit;
update ulgn01.TLGNC_SHIPPINGPOINT set VAR_HANDLING_COST_MAT=4 where VAR_HANDLING_COST_MAT is not null;
commit;
update ulgn01.TLGNC_SHIPPINGPOINT set FUTURE_VAR_HANDLING_COST_MAT=4 where FUTURE_VAR_HANDLING_COST_MAT is not null;
commit;
update ulgn01.TLGNC_SHIPTO set ORDER_CONF_EMAIL_ADDRESSES='crh-logon-test@adnovum.ch' where ORDER_CONF_EMAIL_ADDRESSES is not null;
commit;
update ulgn01.TLGNC_SHIPTO set ORDER_CONF_FAX_NUMBER='+36 1 487 5000' where ORDER_CONF_FAX_NUMBER is not null;
commit;
update ulgn01.TLGNC_SHIPTO set ORDER_CONF_SMS_NUMBER='+36 1 487 5000' where ORDER_CONF_SMS_NUMBER is not null;
commit;
update ulgn01.TLGNC_WAGON_KEEPER set EMAIL_ADDRESSES='crh-logon-test@adnovum.ch' where EMAIL_ADDRESSES is not null;
commit;
update ulgn01.TLGNC_WAGON_KEEPER set DESPATCH_CHARGE_PAYER='44445555' where DESPATCH_CHARGE_PAYER is not null;
commit;
update ulgn01.TLGNF_ORDER_FINAL set O_ORDER_CONF_EMAIL_ADRESSES='crh-logon-test@adnovum.ch' where O_ORDER_CONF_EMAIL_ADRESSES is not null;
commit;
update ulgn01.TLGNF_ORDER_FINAL set O_ORDER_CONF_FAX_NUMBER='+36 1 487 5000' where O_ORDER_CONF_FAX_NUMBER is not null;
commit;
update ulgn01.TLGNF_ORDER_FINAL set WE_SGROUP_EMAIL_ADDRESSES='crh-logon-test@adnovum.ch' where WE_SGROUP_EMAIL_ADDRESSES is not null;
commit;
update ulgn01.TLGNF_ORDER_FINAL set AG_SGROUP_EMAIL_ADDRESSES='crh-logon-test@adnovum.ch' where AG_SGROUP_EMAIL_ADDRESSES is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_RATE=4 where OI_RATE is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_GROSS_PRICE_MAT=4 where OI_GROSS_PRICE_MAT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_PROD_COST_MAT=4 where OI_PROD_COST_MAT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_HANDLING_COST_MAT=4 where OI_HANDLING_COST_MAT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_IUD_COST=4 where OI_IUD_COST is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_OUT_COST=4 where OI_OUT_COST is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_QUANTITY=4 where OI_MARGIN_QUANTITY is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_GROSS_PRICE=4 where OI_MARGIN_GROSS_PRICE is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_GROSS_PRICE_PT=4 where OI_MARGIN_GROSS_PRICE_PT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_DISCOUNTS=4 where OI_MARGIN_DISCOUNTS is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_DISCOUNTS_PT=4 where OI_MARGIN_DISCOUNTS_PT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_NET_PRICE=4 where OI_MARGIN_NET_PRICE is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_NET_PRICE_PT=4 where OI_MARGIN_NET_PRICE_PT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_REBATES=4 where OI_MARGIN_REBATES is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_REBATES_PT=4 where OI_MARGIN_REBATES_PT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_CASH_DISCOUNT=4 where OI_MARGIN_CASH_DISCOUNT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_CASH_DISCOUNT_PT=4 where OI_MARGIN_CASH_DISCOUNT_PT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_NET_SALES_EARNING=4 where OI_MARGIN_NET_SALES_EARNING is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_NET_SALES_EARNING_PT=4 where OI_MARGIN_NET_SALES_EARNING_PT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_PROD_COST=4 where OI_MARGIN_PROD_COST is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_PROD_COST_PT=4 where OI_MARGIN_PROD_COST_PT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_IUD_COST=4 where OI_MARGIN_IUD_COST is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_IUD_COST_PT=4 where OI_MARGIN_IUD_COST_PT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_HANDLING_COST=4 where OI_MARGIN_HANDLING_COST is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_HANDLING_COST_PT=4 where OI_MARGIN_HANDLING_COST_PT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_OUT_COST=4 where OI_MARGIN_OUT_COST is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_OUT_COST_PT=4 where OI_MARGIN_OUT_COST_PT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_TOTAL_DIST_COST=4 where OI_MARGIN_TOTAL_DIST_COST is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_TOTAL_DIST_COST_PT=4 where OI_MARGIN_TOTAL_DIST_COST_PT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_TOTAL_COST=4 where OI_MARGIN_TOTAL_COST is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_TOTAL_COST_PT=4 where OI_MARGIN_TOTAL_COST_PT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_CONTRIB_MARGIN=4 where OI_MARGIN_CONTRIB_MARGIN is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_MARGIN_CONTRIB_MARGIN_PT=4 where OI_MARGIN_CONTRIB_MARGIN_PT is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_REFUND_TRSP_CAPA_TKM=4 where OI_REFUND_TRSP_CAPA_TKM is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_REFUND_NET_WEIGHT_CUR_T=4 where OI_REFUND_NET_WEIGHT_CUR_T is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_REFUND_DISTANCE_CUR_KM=4 where OI_REFUND_DISTANCE_CUR_KM is not null;
commit;
update ulgn01.TLGNF_ORDER_ITEM_FINAL set OI_REFUND_TRSP_CAPA_CUR_TKM=4 where OI_REFUND_TRSP_CAPA_CUR_TKM is not null;
commit;
update ulgn01.TLGNR_SGROUP_BS set EMAIL_ADDRESSES='crh-logon-test@adnovum.ch' where EMAIL_ADDRESSES is not null;
commit;
update ulgn01.TLGNR_SIMCARD_BS set ISSUER_NUMBER=SIMCARD_CD where ISSUER_NUMBER is not null;
commit;
update ulgn01.TLGNR_SORG_TX set LINE_PHONE='+36 1 487 5000' where LINE_PHONE is not null;
commit;
update ulgn01.TLGNR_SORG_TX set LINE_FAX='+36 1 487 5000' where LINE_FAX is not null;
commit;
update ulgn01.TLGNR_SORG_TX set LINE_TAX='Ust_ID-Nr: DE142395541' where LINE_TAX is not null;
commit;
update ulgn01.TLGNR_SORG_TX set LINE_EMAIL='crh-logon-test@adnovum.ch' where LINE_EMAIL is not null;
commit;
update ulgn01.TLGNS_SAP_CONTRACT_ITEM set NET_VALUE=4 where NET_VALUE is not null;
commit;
update ulgn01.TLGNS_SAP_CONTRACT_ITEM set VAT_PERCENTAGE=4 where VAT_PERCENTAGE is not null;
commit;
update ulgn01.TLGNS_SAP_MATERIAL_PLANT set VAL_PRICE_CONTROL_INDICATOR='S' where VAL_PRICE_CONTROL_INDICATOR is not null;
commit;
update ulgn01.TLGNS_SAP_MATERIAL_PLANT set VAL_STANDARD_PRICE=4 where VAL_STANDARD_PRICE is not null;
commit;
update ulgn01.TLGNS_SAP_MATERIAL_PLANT set VAL_MOVING_AVG_PRICE=4 where VAL_MOVING_AVG_PRICE is not null;
commit;
update ulgn01.TLGNS_SAP_MATERIAL_PLANT set VAL_FUTURE_PRICE=4 where VAL_FUTURE_PRICE is not null;
commit;
update ulgn01.TLGNS_SAP_NETPRICE_ORDER_ITEM set NETPRICE_NET_VALUE=4 where NETPRICE_NET_VALUE is not null;
commit;
update ulgn01.TLGNS_SAP_NETPRICE_ORDER_ITEM set NETPRICE_REBATE_TOTAL=4 where NETPRICE_REBATE_TOTAL is not null;
commit;
update ulgn01.TLGNS_SAP_NETPRICE_ORDER_ITEM set NETPRICE_TAX=4 where NETPRICE_TAX is not null;
commit;
update ulgn01.TLGNS_SAP_NETPRICE_ORDER_ITEM set NETPRICE_CASH_DISCOUNT=4 where NETPRICE_CASH_DISCOUNT is not null;
commit;
update ulgn01.TLGNS_SAP_NETPRICE_ORDER_ITEM set NETPRICE_QUANTITY=4 where NETPRICE_QUANTITY is not null;
commit;
update ulgn01.TLGNS_SAP_VENDOR set PHONE_NUMBER='+36 1 487 5000' where PHONE_NUMBER is not null;
commit;
update ulgn01.TLGNU_EXT_LOGIN_PWD_HISTORY set EXT_LOGIN_PWD='xxxxxxxxxx' where EXT_LOGIN_PWD is not null;
commit;
update ulgn01.TLGNU_USER set EXT_LOGIN_PWD='xxxxxxxxxx' where EXT_LOGIN_PWD is not null;
commit;
update ulgn01.TLGNU_USER set PDA_USER_NO=USER_ID where PDA_USER_NO is not null;
commit;
update ulgn01.TLGNU_USER set PICKUP_BARCODE_PASSCODE='xxxxxxxxxx' where PICKUP_BARCODE_PASSCODE is not null;
commit;
update ulgn01.TLGNU_USER_ACTIVITY set IP_ADDRESS='111.111.111.111' where IP_ADDRESS is not null;
commit;
prompt END Block 1 - setting values


prompt START Block 2 - transforming values
call transform_data.transform('ulgn01.TLGNC_ASSIGNMENT','LDS_DRIVER_NAME','ASSIGNMENT_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_ASSIGNMENT','LDS_DRIVER_CARD_NO','ASSIGNMENT_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_ASSIGNMENT','LDS_DISPATCHER_NAME','ASSIGNMENT_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_ASSIGNMENT','LDS_ATTORNEY_NAME','ASSIGNMENT_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_ASSIGNMENT_PART','SITE_NAME','ASSIGNMENT_PART_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_ASSIGNMENT_PART','SITE_CONTACT_PERSON_TXT','ASSIGNMENT_PART_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_ASSIGNMENT_PART','SITE_STREET_HOUSE_NO','ASSIGNMENT_PART_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_CARRIER','DISPLAY_NAME','CARRIER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_CUSTORDER','CONTACT_PERSON_TXT','CUSTORDER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_CUSTORDER','LDS_CUSTORDER_NO','CUSTORDER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_CUSTORDER','CUSTORDER_CUSTREF_NO','CUSTORDER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_EXT_SITE','SITE_NAME','EXT_SITE_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_EXT_SITE','SITE_CONTACT_PERSON_TXT','EXT_SITE_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_EXT_SITE','SITE_STREET_HOUSE_NO','EXT_SITE_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_LOAD','WS_TRUCK_PLATE_NO','LOAD_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_LOAD','WS_TRAILER_PLATE_NO','LOAD_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_LOAD','WS_CARRIER','LOAD_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_LOAD','WS_CUSTOMER_ORDER_NO','LOAD_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_MOPEVENT_HEADER_IN','PDA_NO','MOPEVENT_HEADER_IN_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_MOPEVENT_HEADER_IN','TRUCK','MOPEVENT_HEADER_IN_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_MOPEVENT_HEADER_OUT','PDA_NO','MOPEVENT_HEADER_OUT_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_ORDER','LNF_SITE_STREET_HOUSE_NO','ORDER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_ORDER','LNF_SITE_VAT_NO','ORDER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_ORDER','ONE_TIME_SOLDTO_STREET_HOUSENO','ORDER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_ORDER','ONE_TIME_SOLDTO_VAT_NO','ORDER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_ORDER','CONTACT_PERSON_TXT','ORDER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_ORDER','ONE_TIME_SOLDTO_NAME','ORDER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_ORDER','ETRAN_RETURN','ORDER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_ORDER','ETRAN_OUTBOUND','ORDER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_PDA_DEVICE','PDA_DEVICE_NO','PDA_DEVICE_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_PERSON','NAME','PERSON_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_PERSON','FIRSTNAME','PERSON_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_PERSON','PHONE_NR','PERSON_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_PERSON','MOBILE_NR','PERSON_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_RFIDTAG','RFID_IDENTITY','RFIDTAG_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_SHIPTO','SBB_CUSTOMER_NUMBER','SHIPTO_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_SIMPLE_VEHICLE','VEHICLE_REG_NO','SIMPLE_VEHICLE_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_TRAILER','TRAILER_PLATE_NO','TRAILER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_TRAILER','TRAILER_NAME','TRAILER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_TRUCK','TRUCK_PLATE_NO','TRUCK_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_TRUCK','TRUCK_NAME','TRUCK_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_WAGON','WAGON_NO','WAGON_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_WAGON_EVENT','WAGON_NO','WAGON_EVENT_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNC_WAGON_KEEPER','WAGON_KEEPER_NAME','WAGON_KEEPER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','O_CONTACT_PERSON_TXT','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','O_CONTACT_PERSON_SIGNATURE_TXT','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','VEHICLE_REG_NO','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','DRIVER_NAME','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','DRIVER_FIRSTNAME','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','RE_NAME1','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','RE_NAME2','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','RE_STREET','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','RE_HOUSE_NUMBER','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','COMM_CARRIER_V_NAME1','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','COMM_CARRIER_V_NAME2','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','COMM_CARRIER_V_STREET','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','COMM_CARRIER_V_HOUSE_NUMBER','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','EXEC_CARRIER_V_NAME1','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','EXEC_CARRIER_V_NAME2','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','EXEC_CARRIER_V_STREET','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','EXEC_CARRIER_V_HOUSE_NUMBER','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','WE_NAME1','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','WE_NAME2','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','WE_STREET','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','WE_HOUSE_NUMBER','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','AG_NAME1','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','AG_NAME2','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','AG_STREET','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','AG_HOUSE_NUMBER','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','O_ONE_TIME_SOLDTO_NAME','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','O_ONE_TIME_SOLDTO_STREET_HNO','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','O_CTL_CRE_UID_NAME','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','O_CTL_MOD_UID_NAME','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','TRUCK_NAME','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','TRUCK_PLATE_NO','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','WAGON_NO','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','DRIVER_SAP_BP_NO','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','DRIVER_SAP_VENDOR_NO','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','RE_SAP_ID','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','RE_SBB_CUSTOMER_NUMBER','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','COMM_CARRIER_V_SAP_ID','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','EXEC_CARRIER_V_SAP_ID','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','WE_SAP_ID','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','WE_SBB_CUSTOMER_NUMBER','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','AG_SAP_ID','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','AG_SBB_CUSTOMER_NUMBER','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','O_LNF_SITE_NAME','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','O_LNF_SITE_STREET_HOUSE_NO','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','O_LNF_SITE_VAT_NO','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_FINAL','O_ONE_TIME_SOLDTO_VAT_NO','ORDER_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_ITEM_FINAL','TRAILER_PLATE_NO','ORDER_ITEM_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_ITEM_FINAL','WAGON_NO','ORDER_ITEM_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_ITEM_FINAL','OI_CTL_CRE_UID_NAME','ORDER_ITEM_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_ITEM_FINAL','OI_CTL_MOD_UID_NAME','ORDER_ITEM_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_ITEM_FINAL','LOAD_WS_TRAILER_PLATE_NO','ORDER_ITEM_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_ITEM_FINAL','LOAD_WS_TRUCK_PLATE_NO','ORDER_ITEM_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_ITEM_FINAL','TRAILER_NAME','ORDER_ITEM_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_ITEM_FINAL','TRUCK_NAME','ORDER_ITEM_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_ITEM_FINAL','TRUCK_PLATE_NO','ORDER_ITEM_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNF_ORDER_ITEM_FINAL','LOAD_WS_CUSTOMER_ORDER_NO','ORDER_ITEM_FINAL_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNR_SIMCARD_BS','SIMCARD_NUMBER','SIMCARD_CD'); 
commit;
call transform_data.transform('ulgn01.TLGNR_SIMCARD_BS','PHONE_NUMBER','SIMCARD_CD'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_BP','NAME1','SAP_BP_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_BP','NAME2','SAP_BP_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_BP','STREET','SAP_BP_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_BP','HOUSE_NUMBER','SAP_BP_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_BP','PHONE_NUMBER','SAP_BP_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_BP','VAT_REGISTRATION_NUMBER','SAP_BP_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_BP','TAX_NUMBER_1','SAP_BP_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_BP','CONTACT_NAME','SAP_BP_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_BP','CONTACT_PHONE','SAP_BP_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_BP','SAP_ID','SAP_BP_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_BP','SAP_IFC_ID','SAP_BP_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_CONTRACT','SAP_CONTRACT_NO','SAP_CONTRACT_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_CONTRACT','CREDIT_LIMIT_ACCOUNT_NO','SAP_CONTRACT_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_CONTRACT','VENDOR_SAP_NO','SAP_CONTRACT_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_CONTRACT','CONTROLLER_NO','SAP_CONTRACT_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_CONTRACT_ITEM','ADVANCE_INVOICE_NO','SAP_CONTRACT_ITEM_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_CONTRACT_ITEM','PARENT_OFFER_NO','SAP_CONTRACT_ITEM_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_VEHICLE','SAP_VEHICLE_PLATE_NO','SAP_VEHICLE_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_VENDOR','NAME1','SAP_VENDOR_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_VENDOR','NAME2','SAP_VENDOR_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_VENDOR','STREET','SAP_VENDOR_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_VENDOR','HOUSE_NUMBER','SAP_VENDOR_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_VENDOR','VAT_REGISTRATION_NUMBER','SAP_VENDOR_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_VENDOR','TAX_NUMBER3','SAP_VENDOR_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_VENDOR','SAP_ID','SAP_VENDOR_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNS_SAP_VENDOR','SAP_IFC_ID','SAP_VENDOR_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNU_USER','LDAP_HOLSPACE','USER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNU_USER','EXT_LOGIN','USER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNU_USER','PDA_PIN','USER_ID'); 
commit;
call transform_data.transform('ulgn01.TLGNU_USER','EXT_PWD_RESETTOKEN_HASH','USER_ID'); 
commit;

prompt END Block 2 - transforming values

prompt START Block 3 - deleting values

delete from ulgn01.TLGNC_ORDER_REP_ATT;
commit;
delete from ulgn01.TLGNC_WAGON_REP_ATT;
commit;
delete from ulgn01.TLGNR_CONFIG;
commit;

prompt END Block 3 - deleting values
----------------------------------------------------------------------------------------------------------------|
-- Custom part: foreign keys to to-deleted data                                                                                                   |
----------------------------------------------------------------------------------------------------------------|
prompt START Block 4 - custom anonimization
delete from ulgn01.TLGNT_OUTPUTDOCS_TO_DELETE;
commit;
delete from ulgn01.TLGNC_ORDER_OUTPUTDOC;
commit;
delete from ulgn01.TLGNC_CUSTORDER_OUTPUTDOC;
commit;
delete from ulgn01.TLGNC_OUTPUTDOC;
commit;
update ulgn01.TLGNC_ORDER SET DELIVERYNOTE_SIGNATURE_ID=null;
commit;
update ulgn01.TLGNU_USER SET SIGNATURE_ID=null;
commit;
delete from ulgn01.TLGNC_SIGNATURE;
commit;
prompt Start custom delete tlgnc_image: delete all which is referred from tlgnu_user
DECLARE
  CURSOR USER_WITH_PHOTO_IMAGE is select user_id, photo_image_id from tlgnu_user where photo_image_id is not null;
BEGIN
  FOR cur_user IN USER_WITH_PHOTO_IMAGE
  LOOP
	update tlgnu_user set photo_image_id=null where user_id=cur_user.user_id;
	delete from ulgn01.TLGNC_IMAGE where image_id=cur_user.photo_image_id;  
  END LOOP;
END;
/
commit;

prompt END Block 4 - custom anonimization
prompt END anonymize confidential data

DROP PACKAGE TRANSFORM_DATA;
commit;

prompt Time ENDED:
select systimestamp from dual;

spool off
COLUMN spool_file_name CLEAR
