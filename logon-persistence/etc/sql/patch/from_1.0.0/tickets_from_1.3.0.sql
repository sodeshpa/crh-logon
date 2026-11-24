prompt ************************************************************************
prompt *** BEGIN CRHLOGON-54: Various automatic maintenance issues
prompt ************************************************************************

Insert into TLGNC_CRONSTATUS (CRONSTATUS_ID, CRONSTATUS_NAME, LAST_START_TS, LAST_END_TS, LAST_RUN_NUMBER, CTL_CRE_UID, CTL_CRE_TS, CTL_MOD_UID, CTL_MOD_TS, CTL_TCN) values
  (SLGNC_CRONSTATUS.nextval, 'auto_init_shipto_sarea_restr', to_timestamp('19700101','YYYYMMDD'), to_timestamp('19700101','YYYYMMDD'), 0, 1, systimestamp, 1, systimestamp, 0);


prompt ************************************************************************
prompt *** END CRHLOGON-54: Various automatic maintenance issues
prompt ************************************************************************
*********************************************************************