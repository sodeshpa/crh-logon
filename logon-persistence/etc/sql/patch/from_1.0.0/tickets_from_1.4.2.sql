prompt ************************************************************************
prompt *** BEGIN CRHLOGON-183: Rekey - regularization request
prompt ************************************************************************

ALTER TABLE TLGNC_LOAD ADD LDS_OFFLINE_MODE NUMBER(1);
UPDATE TLGNC_LOAD SET LDS_OFFLINE_MODE = 0;
ALTER TABLE TLGNC_LOAD MODIFY LDS_OFFLINE_MODE NUMBER(1) not null;

Insert into TLGNR_ORDER_STATUS_BS (ORDER_STATUS_CD,SORTING_NO,POSSIBLE_TRUCK_STATUS_FLAG,POSSIBLE_RAIL_STATUS_FLAG,CTL_ACT_CD,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,145,1,0,1,1,(select sysdate from dual),1,(select sysdate from dual));

Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'az','Delivered – Pending LDS Data (az)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'bg','Delivered – Pending LDS Data (bg)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'cs','Delivered – Pending LDS Data (cs)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'de','Delivered – Pending LDS Data (de)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'en','Delivered – Pending LDS Data','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'es','Delivered – Pending LDS Data (es)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'fr','Livré - Attente données FENICS','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'hr','Delivered – Pending LDS Data (hr)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'hu','Delivered – Pending LDS Data (hu)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'it','Delivered – Pending LDS Data (it)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'nl','Delivered – Pending LDS Data (nl)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'pl','Delivered – Pending LDS Data (pl)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'ro','Delivered – Pending LDS Data (ro)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'ru','Delivered – Pending LDS Data (ru)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'sk','Delivered – Pending LDS Data (sk)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'sr','Delivered – Pending LDS Data (sr)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));
Insert into TLGNR_ORDER_STATUS_TX (ORDER_STATUS_CD,LANG_CD,TXT,WARN_TXT,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS) values (21,'tr','Delivered – Pending LDS Data (tr)','LDS loading was skipped',1,(select sysdate from dual),1,(select sysdate from dual));

prompt ************************************************************************
prompt *** END CRHLOGON-183: Rekey - regularization request
prompt ************************************************************************

prompt ************************************************************************
prompt *** BEGIN CRHLOGON-182: Rekey - PDA changes
prompt ************************************************************************

ALTER TABLE TLGNR_WEIGH_SYSTEM_SP_BS ADD LDS_OFFLINE_MODE_ENABLE NUMBER(1);
UPDATE TLGNR_WEIGH_SYSTEM_SP_BS SET LDS_OFFLINE_MODE_ENABLE = 0;
ALTER TABLE TLGNR_WEIGH_SYSTEM_SP_BS MODIFY LDS_OFFLINE_MODE_ENABLE NUMBER(1) not null;

--enable for Fenics LDS offline mode
update tlgnr_weigh_system_sp_bs set LDS_OFFLINE_MODE_ENABLE=1 where config_table_infix='fenics';


prompt ************************************************************************
prompt *** END CRHLOGON-183: Rekey - regularization request
prompt ************************************************************************