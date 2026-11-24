prompt ************************************************************************
prompt *** Updating ADNOVUM INTERNAL TEST and INITIAL data - ADZA ONLY
prompt ************************************************************************
prompt

set define on
set linesize 120
set pages 1000

spool /temp/logon_update_adnovum_initial_and_testdata.log

prompt Update the ADZA initial/testdata for connected user:
prompt
show user
prompt

-- to prevent errors with the ampersand
set define off
-- Default Logon NLS settings
@@../../set_logon_session_settings.sql

prompt Time STARTED:
select systimestamp from dual;

prompt ************************************************************************

-- ^^^ Add new data updates here - copy the template below ^^^
-- BEGIN TEMPLATE
prompt ************ Start LOGON-xxxx: Summary  ***********
prompt Describe the change again, so that when you see an error in the logfiles you can find the place IMMEDIATELY.

-- ... update, inserts, modification - same statements you execute in SQL developer
commit;
prompt ************ End LOGON-xxxx ***********************
-- END TEMPLATE


prompt ************************************************************************
--- FINAL COMMIT ---
commit;
prompt Time FINISHED:
select systimestamp from dual;

spool off
