prompt
prompt ************************************************************************
prompt *** Patching Logon Database 2.6.2 -> 2.6.3
prompt ************************************************************************
prompt
prompt This patch script is to be applied to Logon databases (LPx, LQx, LTx, LRx, INTx).
prompt It will upgrade the DB from version 2.6.2 to version 2.6.3.

set define   on
set linesize 1000
set pages    1000

-- Run this patch with the user 'ulgn01'.
-- Put 'ulgnsap' schema patches into 'patch_sap__2.6.2_to_2.6.3.sql'.

spool /temp/logon_patch_2.6.2_to_2.6.3.log

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

  -- Checking the DB version
  if regexp_like(l_current_version_and_build, '^2\.[012][.0-9]+') then
    /** The old version scheme was used between 2.0.* - 2.2.* */
    l_current_version := regexp_replace(l_current_version_and_build, '(rc)?.(.)?$','');
  elsif regexp_like(l_current_version_and_build, '^[2-9]\.[.0-9]+$') then
    /** The new version scheme is used from 2.3.*  */
    l_current_version := regexp_replace(l_current_version_and_build, '\.[^.]+$','');
  else
    dbms_output.put_line('ERROR: Unknown version scheme was used: ' || l_current_version_and_build);
  end if;

  -- TODO (generated): manually append additional allowed starting DB versions if necessary.
  -- Please syncronize the version check with https://aww.adnovum.ch/confluence/display/CrhPegasus/Releases
  if (l_current_version not in ('2.6.2')) then
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

-- START ticket specific patches
@@tickets.sql
-- END ticket specific patches

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

spool off
