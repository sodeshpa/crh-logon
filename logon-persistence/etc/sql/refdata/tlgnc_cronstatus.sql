-- $Revision$
-- $Date$
--
-- Technical table. Must match the configured background jobs of the application.

prompt '---------- initialdata/tlgnc_cronstatus.sql ----------'

prompt INSERTING into TLGNC_CRONSTATUS

Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  ( SLGNC_CRONSTATUS.nextval, 'sapimport_clean_log', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);

Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  ( SLGNC_CRONSTATUS.nextval, 'pda_session_reaper', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);

Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  ( SLGNC_CRONSTATUS.nextval, 'rfid_object_label_cleanup', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);

Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  ( SLGNC_CRONSTATUS.nextval, 'edifact_import', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);

Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  ( SLGNC_CRONSTATUS.nextval, 'wagon_event_proc', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);

Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  ( SLGNC_CRONSTATUS.nextval, 'delivnote_cleanup', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);
  
Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  (SLGNC_CRONSTATUS.nextval, 'sapimport_bpn', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);
Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  (SLGNC_CRONSTATUS.nextval, 'sapimport_mn', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);
Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  (SLGNC_CRONSTATUS.nextval, 'sapimport_mnn', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);
Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  (SLGNC_CRONSTATUS.nextval, 'sapimport_sdn', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);
Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  (SLGNC_CRONSTATUS.nextval, 'sapimport_vehn', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);
Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  (SLGNC_CRONSTATUS.nextval, 'sapimport_vn', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);

Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  ( SLGNC_CRONSTATUS.nextval, 'update_orders', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);
  
Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  ( SLGNC_CRONSTATUS.nextval, 'send_daily_reports', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);

Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  ( SLGNC_CRONSTATUS.nextval, 'postprocess_sapimport_vehn', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);

Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  ( SLGNC_CRONSTATUS.nextval, 'send_staging_area_report_bpn', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);

Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  ( SLGNC_CRONSTATUS.nextval, 'send_staging_area_report_vn', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);

Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  ( SLGNC_CRONSTATUS.nextval, 'send_staging_area_report_mn', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);

Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  (SLGNC_CRONSTATUS.nextval, 'update_planning_mat_sarea', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);

Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  (SLGNC_CRONSTATUS.nextval, 'archive_orders', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);
  
Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  (SLGNC_CRONSTATUS.nextval, 'auto_init_shipto_sarea_restr', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);
  

commit;
