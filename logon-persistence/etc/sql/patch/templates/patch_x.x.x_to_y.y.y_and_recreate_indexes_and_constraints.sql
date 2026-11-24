prompt
prompt ************************************************************************
prompt *** Patching Logon Database __FROM_VERSION____BUILD_SEQUENCE__ -> __TO_VERSION__
prompt ************************************************************************
prompt
prompt This patch script is to be applied to Logon databases.
prompt It will upgrade the DB from version __FROM_VERSION____BUILD_SEQUENCE__ to version __TO_VERSION__.

set define   on
set linesize 1000
set pages    1000

-- Run this patch with the user 'ulgn01'.
-- Put 'ulgnsap' schema patches into 'patch_sap____FROM_VERSION___to___TO_VERSION__.sql'.

column patch_file_name new_value patch_file_name noprint;
select '''logon_patch____FROM_VERSION___to___TO_VERSION__''' as patch_file_name from dual;
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
  l_version_without_rc VARCHAR2(16);
  l_current_user       VARCHAR2(16);
  l_nls_lang           VARCHAR2(32);
  e_version_mismatch     EXCEPTION;
  e_incorrect_user       EXCEPTION;
  e_bad_nls_lang_setting EXCEPTION;
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

  -- Checking the DB version
  select
    regexp_replace(version, '(rc)?.(.)?$','') version_without_rc
  into
    l_version_without_rc
  from
    tlgnc_db_version_log
  where
    install_end_ts = (select max(install_end_ts) from tlgnc_db_version_log);

  -- TODO (generated): manually append additional allowed starting DB versions if necessary.
  -- Please syncronize the version check with https://aww.adnovum.ch/confluence/display/CrhPegasus/Releases
  if (l_version_without_rc not in ('__FROM_VERSION__')) then
    raise_application_error(-20036, 'The current DB version (' || l_version_without_rc || ') is not allowed as starting DB version! Patching aborted');
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

prompt ************************************************************************
prompt *** Data cleanups
prompt ************************************************************************
-- Add cleanups here which should used existing/old FK/INDEX, but are a requirement
-- for new FK or UQ constraints. Examples: Duplicates, invalid values for new FK, etc.

@@../clean_pks_based_on_custom_indexes.sql

-- ^^^ Add Cleanups ABOVE ^^^ Be careful with dependencies amongst individual patches ^^^

prompt
prompt ************************************************************************
prompt *** Drop FK Index, maybe also FK (can be recreated easily)
prompt ************************************************************************

-- Drop stuff from PREVIOUS delivered version - copied files from
--   /share/install/logon/(previous version)/logon/logon-persistence/build/generated/gdi/schema2ddlentities/sql/drop/
@@./60_gen_drop_constraints_fk_index.sql

prompt
prompt ************************************************************************
prompt *** Drop Unique constraints and Indexes (can be recreated easily)
prompt ************************************************************************
@@./64_gen_drop_constraints_unique.sql
@@./70_gen_drop_indexes.sql

prompt ************************************************************************
prompt *** Data cleanups
prompt ************************************************************************
-- Add cleanups here which should used existing old indexes and constraints

@@../clean_remaining_constraints_and_indexes.sql

-- Pros/Cons:
-- - Dropping them BEFORE the actual migration makes them LESS SECURE (patches
--   could corrupt data more easily) and makes heavy migration SLOWER (indexes
--   which cannot be used anymore)
-- - But dropping & recreating AFTER the actual migration adds a certain risk
--   for "BOGUS ERRORS", for example, when patch scripts completely remove a
--   table
-- - Some data migration may also require that a UQ constraints is temporarily
--   or permanently removed, and thus would have to do it manually, or would
--   fail.
--
-- => There is no global rule what is right or wrong. Decide for each patch,
--   what is appropriate.
--
-- LOGON - Decision:
-- - For current releases of MINI-patches, dropping them BEFORE is ok.
--
-- Reasons:
-- - The goal is 0 Errors in the log (quick verification!), with minimal effort
--   in writing correct patch scripts.
-- - Performance is irrelevant, Correctness is ensured by testing the patch
--   well enough (and the FK and UQ constraints are activated again, after all.
--   So, if something went wrong, the contraint fails, and data cleanup is
--   necessary, that's all.)
-- - If heavy data migration is needed (which would profit from active Indexes,
--   FK, UQ constraints: Simply move that from the schema-patch section towards
--   the end of the script, where Refdata and Metadata is added, etc.

prompt
prompt ************************************************************************
prompt *** Tables and Data migration, PK, Sequences, New/Modified FK
prompt ************************************************************************
prompt Time before the actual main migration:
select systimestamp from dual;

-- Patching the entities for the main schema
-- HINT: look at helper tool "logon-persistence/logon_alter_db.sh" !


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
-- => When updating/modifying data HERE, be aware, that Indexes, FK constraints
--    and UQ constraints may NOT be active, see "Pros/Cons" comments above;
--    they may have been dropped at the beginning of the patch!
-- => If HEAVY or dangerous data migration is needed, add that at the END of
--    the patch instead!
--    (In a DB < 1GB, no migration probably counts as heavy though....)

prompt Time after the actual main migration:
select systimestamp from dual;

prompt
prompt ************************************************************************
prompt *** Indexes
prompt ************************************************************************

prompt *** Recreating the indexes
@@../../create/15_gen_create_indexes.sql
@@../../custom_indexes.sql

prompt
prompt ************************************************************************
prompt *** Unique constraints
prompt ************************************************************************

prompt *** Recreating the unique constraints
@@../../create/22_gen_create_constraints_unique.sql

prompt
--     ************************************************************************
--     *** Foreign Keys
--     ************************************************************************
-- Variant 1:
-- drop all (at the beginning (1A) are here (1B), see comments for UQ
-- constraints, Indexes, FK Indexes) and recreate all:
-- prompt *** Recreating all Foreign keys
-- @@../../create/24_gen_create_constraints_fk.sql

-- Variant 2:
-- Simply add new ones. This is OK if the names have not been changed too much.
-- If new ones are needed, add manually above (NOT HERE), together with the
-- table and PK for better readibility! See above!

prompt
prompt ************************************************************************
prompt *** Recreating FK Indexes
prompt ************************************************************************
@@../../create/26_gen_create_constraints_fk_index.sql
@@../../custom_check_constraints.sql

prompt
prompt ************************************************************************
prompt *** Procedures, Custom Views, PL/SQL Packages
prompt ************************************************************************
-- Higher level order objects. They are all written so they can be reexecuted (create or replace).

prompt *** Stored Procedures (used in Views).
@@../../stored_procedures.sql

prompt *** Custom Views
@@../../custom_views.sql
@@../../custom_views_reporting.sql

-- Do not recreate sequences! @custom_sequences.sql

prompt *** PL/SQL Packages
@@../../logon_log_pkg.sql
@@../../logon_util_pkg.sql
-- @sapimport_grants.sql can not be called here, grants must be given by schema owner ulgnsap
@@../../sapimport_bpn_pkg.sql
@@../../sapimport_mn_pkg.sql
@@../../sapimport_mnn_pkg.sql
-- @@../../sapimport_netp_pkg.sql -- October 2012: not in use in LOGON EBM 2.1, probably obsolete
@@../../sapimport_pd_pkg.sql
@@../../sapimport_sdn_pkg.sql
@@../../sapimport_vehn_pkg.sql
@@../../sapimport_vn_pkg.sql
@@../../delete_invalid_restrictions_pkg.sql
@@../../auto_init_shipto_sarea_restr.sql

@@../../initialize_common_division_pkg.sql

prompt
prompt ************************************************************************
prompt *** Table and Column Comments
prompt ************************************************************************
-- Comments can be re-executed as well
@@../../create/40_gen_create_comments.sql

prompt
prompt ************************************************************************
prompt *** Privileges
prompt ************************************************************************
-- Grant "select", "insert", "update", "delete" on tables/custom views/sequences respectively
-- Grants can be re-executed.
@@../../create/45_gen_grant_privileges.sql
-- Grants to execute import packages / login user ulgn03. Can be reexecuted.
@@../../custom_grants.sql

prompt
prompt ************************************************************************
prompt *** Manual overrides to generated scripts
prompt ************************************************************************
-- Manual overrides / corrections / extensions to generated SQL which cannot (yet) be done in the mapping files
-- Check that the file is SAFE against multiple execution!
prompt *** Recreating the custom overrides - must be for each patch
@@../../custom_overrides.sql

-- do NOT call custom_create_... files - those are only for INITIAL DB creation.

prompt
prompt ************************************************************************
prompt *** Refdata
prompt ************************************************************************
-- Patching refdata :
-- - if your sql is not already in upsert format (insert of update if exists),
--   please rewrite it first based on current the examples in the refdata folder
-- - after that reference the upsert script here with @@ and relative path

prompt tlgnu_func: Generic Insert or Update (descriptions) over all
@@../../refdata/insert_func.sql

-- ^^^ Add new Refdata upserts here ^^^
-- Advantage of adding here (than together with the new/modifed tables):
-- Profit from FK, FK indexes and UQ constraints, which may be inactive in
-- the section above, but are always in place when thes section "Refdata"
-- is executed.

prompt
prompt ************************************************************************
prompt *** Generic Cleanup: Language for all refdata
prompt ************************************************************************
prompt Upsert default translations to all languages if missing:
-- Call after all language dependent data has been loaded (Refreshes all new and existing refdata tables)
@@../../refdata/insert_refdata_in_all_languages.sql

promp
prompt ************************************************************************
prompt *** Generic Cleanup: Tablebrowser config
prompt ************************************************************************
prompt Metadata for new tables: Drop all data, and load with generated/manual everytime.
delete from tlgnt_column_meta;
delete from tlgnt_table_meta;
commit;
@../../create/47_gen_insert_tablemeta.sql

-- Column references Table!
@../../refdata/tlgnt_table_meta.sql
@../../refdata/tlgnt_column_meta.sql

prompt ************************************************************************
prompt *** Generic (TODO) Cleanup: Config
prompt ************************************************************************

prompt Insert all new config values, and update min/max/description, but NEVER the values
@@../../refdata/insert_config.sql

--prompt ************************************************************************
--prompt *** New users
--prompt ************************************************************************

-- ^^^ Add new users here ^^^ (see LOGON-7819 source for example)

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
