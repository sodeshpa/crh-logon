-- Master SQL script to create all DB entities for the SAP Stating area of the Logon DB
-- This includes inserting of ref-data values.
column patch_file_name new_value patch_file_name noprint;
select '''logon_create_db_02_objs_sap''' as patch_file_name from dual;
@patch_spool_on.sql
COLUMN patch_file_name CLEAR

-- to prevent errors with the ampersand
set define off
-- Default Logon NLS settings
@set_logon_session_settings.sql

-- Creating the entities for the main schema
-- (all files in their natural order from logon-webservices/etc/entities)
@create_sap/10_create_basictables_pk_fk.sql
@create_sap/11_create_reftables.sql
@create_sap/20_create_constraints_pk.sql
@create_sap/24_create_constraints_fk.sql
@create_sap/26_create_constraints_fk_index.sql
@create_sap/40_create_comments.sql

-- Allow access for ulgn01 user
@sapimport_grants.sql

-- Initial reference data values
@refdata/tlgnr_sapimpstatus.sql

-- Testdata @testdata/... NOT to be called from objs.sql / objs_sap.sql!

@patch_spool_off.sql
