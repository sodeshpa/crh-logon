-- Based on https://aww.adnovum.ch/jira/browse/LOGON-4706

prompt '---------- adza_testdata/tlgnc_track_loadingpoint.sql ----------'

Insert into TLGNC_TRACK_LOADINGPOINT (TRACK_LOADINGPOINT_ID,PLANT_TRACK_CD,LOADINGPOINT_ID,CTL_CRE_UID,CTL_CRE_TS,CTL_MOD_UID,CTL_MOD_TS,CTL_TCN) values (-1001,-100,-1001,1,sysdate,1,sysdate,0);