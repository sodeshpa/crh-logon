prompt
prompt ************************************************************************
prompt *** Patching Logon SAP schema __FROM_VERSION____BUILD_SEQUENCE__ -> __TO_VERSION__ (patch in production)
prompt ************************************************************************
prompt
prompt This patch script is to be applied to Logon databases (LPx, LQx, LTx, LRx, INTx).
prompt It will upgrade the DB from version __FROM_VERSION____BUILD_SEQUENCE__ to version __TO_VERSION__.

-- This patch is for the sap schema

set linesize 120
set pages 1000

-- Patches are always run directly with the user who is currently connected,
-- which should be ulgnsap

column patch_file_name new_value patch_file_name noprint;
select '''logon_patch_sap____FROM_VERSION___to___TO_VERSION__''' as patch_file_name from dual;
@@../../patch_spool_on.sql
COLUMN patch_file_name CLEAR

-- to prevent errors with the ampersand
set define off

-- Default Logon NLS settings
@@../../set_logon_session_settings.sql

prompt Time STARTED:
select systimestamp from dual;

prompt ************************************************************************
prompt *** Check patch pre-condition
prompt ************************************************************************
-- Checks implemented:
-- - current DB user
-- - NLS_LANG setting

whenever sqlerror exit failure;

declare
  l_current_user VARCHAR2(16);
  l_nls_lang     VARCHAR2(32);
  e_incorrect_user       EXCEPTION;
  e_bad_nls_lang_setting EXCEPTION;
  pragma exception_init(e_incorrect_user,       -20034);
  pragma exception_init(e_bad_nls_lang_setting, -20035);
begin
  -- Checking the DB user
  select lower(user) into l_current_user from dual;
  if (l_current_user != 'ulgnsap') then
    raise_application_error(-20034, 'You must run this patch with "ulgnsap"! Patching aborted.');
  end if;

  -- Checking the NLS_LANG setting
  select lower(userenv('language')) into l_nls_lang from dual;
  if (l_nls_lang != 'american_america.al32utf8') then
    raise_application_error(-20035, 'You must set the NLS_LANG environment variable in the console to "AMERICAN_AMERICA.AL32UTF8" first! Patching aborted.');
  end if;
end;
/

whenever sqlerror continue;

-- ^^^ Add Patches HERE ^^^, COPY the "template" first from below. Be careful with dependencies amongst individual patches!

-- BEGIN TEMPLATE
----------------------------------------------------------------------------------------------------------------|
-- LOGON-XXXX                                                                                                   |
-- LRQ xyz.u : Name / Description                                                                               |
-- Summary of the things your are patching, etc,                                                                |
----------------------------------------------------------------------------------------------------------------|

-- commit;
-- END TEMPLATE

@@../../sapimport_grants.sql

commit;
prompt Time FINISHED:
select systimestamp from dual;

@@../../patch_spool_off.sql
