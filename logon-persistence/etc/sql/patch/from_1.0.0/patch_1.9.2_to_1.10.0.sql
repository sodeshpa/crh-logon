prompt
prompt ************************************************************************
prompt *** Patching Logon Database 1.9.2 -> 1.10.0
prompt ************************************************************************
prompt
prompt This patch script is to be applied to Logon databases (LPx, LQx, LTx, LRx, INTx).
prompt It will upgrade the DB from version 1.9.2 to version 1.10.0.

set define   on
set linesize 1000
set pages    1000

-- Run this patch with the user 'ulgn01'.
-- Put 'ulgnsap' schema patches into 'patch_sap__1.9.2_to_1.10.0.sql'.

column patch_file_name new_value patch_file_name noprint;
select '''logon_patch__1.9.2_to_1.10.0''' as patch_file_name from dual;
@@../../patch_spool_on.sql
COLUMN patch_file_name CLEAR

-- To prevent errors with the ampersand
set define off

-- Default Logon NLS settings
@@../../set_logon_session_settings.sql

-- Output timestamp:
-- - at he beginning
-- - at the end
-- - at interesting places in the middle of the script to identify if certain
--   data migrations take too much time.
prompt Time STARTED:
select systimestamp from dual;

prompt ************************************************************************
prompt *** Check patch pre-condition
prompt ************************************************************************
-- Checks implemented:
-- - current DB user
-- - current DB version
-- - NLS_LANG setting

whenever sqlerror exit failure;

declare
  l_current_version VARCHAR2(16);           /** E.g. 2.3.0   */
  l_current_version_and_build VARCHAR2(16); /** E.g. 2.3.0.9 */
  l_current_user       VARCHAR2(16);
  l_nls_lang           VARCHAR2(32);
  e_version_mismatch      EXCEPTION;
  e_incorrect_user        EXCEPTION;
  e_bad_nls_lang_setting  EXCEPTION;
  pragma exception_init(e_incorrect_user      , -20034);
  pragma exception_init(e_bad_nls_lang_setting, -20035);
  pragma exception_init(e_version_mismatch    , -20036);
begin
  -- Checking the DB user
  select lower(user) into l_current_user from dual;
  if (l_current_user != 'ulgn01') then
    raise_application_error(-20034, 'You must run this patch with "ulgn01"! Patching aborted.');
  end if;
  -- Checking the NLS_LANG setting
  select lower(userenv('language')) into l_nls_lang from dual;
  if (l_nls_lang != 'american_america.al32utf8') then
    raise_application_error(-20035, 'You must set the NLS_LANG environment variable in the console to "AMERICAN_AMERICA.AL32UTF8" first! Patching aborted.');
  end if;

  -- Query the current (~latest installed) DB version
  begin
    select
      version into l_current_version_and_build
    from
      tlgnc_db_version_log
    where
      install_end_ts = (select max(install_end_ts) from tlgnc_db_version_log);
  exception
    when no_data_found then
      raise_application_error(-20036, 'DB version history is not maintained in this DB');
  end;


  l_current_version := regexp_replace(l_current_version_and_build, '\.[^.]+$','');


  -- TODO (generated): manually append additional allowed starting DB versions if necessary.
  -- Please syncronize the version check with https://aww.adnovum.ch/confluence/display/CrhPegasus/Releases
  if (l_current_version not in ('1.9.2')) then
    raise_application_error(-20036, 'The current DB version (' || l_current_version || ') is not allowed as starting DB version! Patching aborted');
  end if;

end;
/

whenever sqlerror continue;

prompt ************************************************************************
prompt *** Log start of patch into tlgnc_db_version_log
prompt ************************************************************************

insert into TLGNC_DB_VERSION_LOG (
  db_version_log_id,
  version, install_start_ts, install_type,
  client_ip, user_name,
  ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
)
values (
  slgnc_db_version_log.nextval,
  '@VERSION@', systimestamp, 'patch',
  sys_context('userenv', 'ip_address'), sys_context('userenv', 'os_user'),
  1, systimestamp, 1, systimestamp, 0
);

commit;

prompt
prompt ************************************************************************
prompt *** Ticket specific DB patch changes
prompt ************************************************************************


-- ^^^ Add Patches HERE ^^^, COPY the "template" first from below. Be careful with dependencies amongst individual patches!
-- HINT: look at helper tool "logon-persistence/logon_alter_db.sh" !

-- BEGIN TEMPLATE
----------------------------------------------------------------------------------------------------------------|
-- LOGON-XXXX                                                                                                   |
-- LRQ xyz.u : Name / Description                                                                               |
-- Summary of the things your are patching, etc,                                                                |
----------------------------------------------------------------------------------------------------------------|

-- commit;
-- END TEMPLATE
@@tickets_from_1.9.2.sql

--
-- Typically this includes
-- - create table
-- - alter table add
-- - alter table modify
-- - alter table drop colum: Careful with drop! Migration may fail -> data loss!!
-- - PK
-- - Sequences (add here, for better readibility)
-- - FK (add here as well, better readibility if things are close together)
--
-- Correct way to add non-nullable columns:
-- - Add new columns (with null - NO DEFAULT!)
-- - Update/Migration
-- - Change added columns to not null if needed.
-- => Do NOT work with db default in new columns! <=
--    (Reason: They cannot be removed completely anymore, only set to 'default null')
--
-- => Write update statements in a way that re-executing the same statement
--    would not corrupt the data, i.e. "idempotent".
--    This may be needed when delivering several times in a dev or test-
--    environment but the patch is actually written for a productive migration
--    Errors will occur (to be tolerated in this situation), but data loss or
--    corruption is unacceptable just because script is executed several times!

prompt Time after the actual main migration:
select systimestamp from dual;

prompt
prompt ************************************************************************
prompt *** Update SQL and PL/SQL code
prompt ************************************************************************
-- Only those which do not involve data changes:
-- - custom check constraints
-- - views
-- - stored procedures
-- - PL/SQL packages

prompt *** Custom check constraints
-- Does not update existing ones, only create if not exists!
@@../../custom_check_constraints.sql

prompt *** Stored Procedures (used in Views).
@@../../stored_procedures.sql
@@../../upsert_planning_contract_items.sql
@@../../update_planning_mat_sarea.sql

prompt *** Custom Views
@@../../custom_views.sql
@@../../custom_views_reporting.sql
@@../../custom_views_staging_area_info.sql

prompt *** PL/SQL Packages
@@../../logon_log_pkg.sql
@@../../logon_util_pkg.sql
@@../../sapimport_bpn_pkg.sql
@@../../sapimport_mn_pkg.sql
@@../../sapimport_mnn_pkg.sql
@@../../sapimport_sdn_pkg.sql
@@../../sapimport_vehn_pkg.sql
@@../../sapimport_vn_pkg.sql
@@../../initialize_common_division_pkg.sql
@@../../delete_invalid_restrictions_pkg.sql
@@../../auto_init_shipto_sarea_restr_pkg.sql

prompt
prompt ************************************************************************
prompt *** Update table and column comments
prompt ************************************************************************
-- Comments can be re-executed as well
@@../../create/40_gen_create_comments.sql

prompt
prompt ************************************************************************
prompt *** Update grants
prompt ************************************************************************
-- Grant "select", "insert", "update", "delete" on tables/custom views/sequences respectively
-- Grants can be re-executed.
@@../../create/45_gen_grant_privileges.sql
-- Grants to execute import packages / login user ulgn03. Can be reexecuted.
@@../../custom_grants.sql

prompt
prompt ************************************************************************
prompt *** Upsert default refdata translations
prompt ************************************************************************
prompt Upsert default redata translations to all languages if missing:
-- Call after all language dependent data has been loaded (Refreshes all new and existing refdata tables)
@@../../refdata/insert_refdata_in_all_languages.sql

prompt ************************************************************************
prompt *** Update tablebrowser config
prompt ************************************************************************
prompt Metadata for new tables: Drop all data, and load with generated/manual everytime.
delete from tlgnt_column_meta;
delete from tlgnt_table_meta;
commit;
@../../create/47_gen_insert_tablemeta.sql
@../../refdata/tlgnt_table_meta.sql
@../../refdata/tlgnt_column_meta.sql

prompt ************************************************************************
prompt *** Update config table (TLGNR_CONFIG)
prompt ************************************************************************

prompt Insert all new config values, and update min/max/description, but NEVER the values
@@../../refdata/insert_config.sql

prompt ************************************************************************
prompt *** Log end of patch into tlgnc_db_version_log
prompt ************************************************************************

update tlgnc_db_version_log
set
  install_end_ts = systimestamp,
  ctl_mod_uid = 1,
  ctl_mod_ts = systimestamp,
  ctl_tcn = ctl_tcn + 1
where install_start_ts = (select max(install_start_ts)
                          from tlgnc_db_version_log
                          where version = '@VERSION@');

-- ----------------------------------------------------------------------------
-- Do NOT add your patch statement simply at the end of the file!!!
--
-- READ the whole patch, READ the comments, UNDERSTAND the structure, ASK somebody.
-- There are markers and a template in the patch. Add your statements there.
-- ----------------------------------------------------------------------------

--- FINAL COMMIT ---
commit;
prompt Time FINISHED:
select systimestamp from dual;

@@../../patch_spool_off.sql
