
-- Loads ADDITIONAL AdNovum testdata on top of a productive DB (currently 1.1.x,)
--
-- Main difference to testdata_loadall.sql:
-- - ONLY data which is not contained in the Crh Prod DB!
-- - ONLY data which is really needed in addition in AdNovum environment, or to run special testcases.
--
-- Execute in:
-- - AdNovum DBs (for integration tests, or to have non-empty tables)
-- - Sometimes also useful in Crh test-environment
-- - NOT meant for productive database! -> such data to be placed in initialdata/ and called by patch scripts.
--
-- Note, that Errors (primary key constraint violated) are to be expected if executed twice.
-- Most of those files are simplistic or invented data, or needed in AdNovum Env.
-- Some of those files may subsequently be replaced by real (initial) data by Crh.
--
-- Keep this file compatible to the current production DB, process is:
-- - import new prod dump
-- - change passwords / maybe anonymize data (TODO later)
-- - testdata_load_prod_dump.sql

spool /temp/logon_testdata_load_prod_dump.log

-- to prevent errors with the ampersand
set define off
-- Default Logon NLS settings
@set_logon_session_settings.sql

-- Additional currencies and exchange rates, to test correct conversions (just EUR+CHF is not meaningful to test)
@testdata/tlgnr_currency.sql

-- SAP BP_... , SAP Material, SAP Mat/Trsp cond, Vendors:
-- No real need for testdata.
-- Add new BP/Mat/Cond/Vendors first in PROD, run import, and then export dump to AdNovum

-- Additional AdNovum vendors (ADN carriers and ADN pools/trucks are based on them)
@testdata/tlgns_sap_vendor.sql

-- Test SAP Vehicles for generic trucks and trailers
@testdata/tlgns_sap_vehicle.sql
@testdata/tlgns_sap_mat_plant_norm.sql

-- Additional users needed in AdNovum (only!).
@testdata/tlgnc_person.sql
@testdata/tlgnu_user.sql
@testdata/tlgnu_user_role.sql
-- Do not add tlgnc_driver here, because of reference to truck created in minimal_setup!

-- For all adnovum team members create the initial set of test users with different roles see https://aww.adnovum.ch/confluence/display/CrhPegasus/AdNovum+team+member+user+set
@testdata/insert_user_person_driver_role.sql

-- Handmade data access restriction data. Recommend to relate it to OUR OWN testdata/users, not real prod. data.
--Rather use the one above inser_user_person_driver_role.
@testdata/tlgnu_user_soldto_bp_acc.sql
@testdata/tlgnu_user_shipto_acc.sql
@testdata/tlgnu_user_sarea_acc.sql
@testdata/tlgnu_user_sgroup_acc.sql

prompt Enable all users with an existing PDA_USER_NO to be Pickup-Terminal users as well, pda_user_no = pickup_barcode_passcode for easy testing.
update tlgnu_user
set pickup_barcode_passcode = pda_user_no,
    ctl_mod_uid = 1,
    ctl_mod_ts=systimestamp
where pda_user_no is not null
and (pickup_barcode_passcode is null
     or pickup_barcode_passcode != pda_user_no || '');
commit;

-- Handmade initial (test)data for cost calculation - Will soon be replaced by real data.
@testdata/tlgnc_iud_cost.sql

-- A few AdNovum printers, similar to Crh's real ones.
@testdata/insert_printer_bs.sql

-- Handmade initial (test)data, mainly for AdNovum internal setup
-- Replace with real data from Crh when their data is stable enough,
-- but make sure there is a set of testdata on which automatic tests can rely on.
@testdata/minimal_setup.sql
@testdata/tlgnc_driver.sql
@testdata/tlgnc_simple_vehicle.sql

-- add missing self-referencing partner functions
@adza_testdata/insert_missing_partner_functions.sql

-- Not real testdata, but DEV-ONLY data
-- This file should NOT automatically be called together with testdata, because we do NOT want them by default in TLGN01/RLGN01 etc.
-- @testdata/insert_config.sql

-- Safety precaution: Only use dev mail server within AdNovum. Until 1.6, 'smtp.adnovum.ch' was default, therefore make sure it's corrected when loaded at AdNovum
-- uma/emoke 20110321: could be removed after 1.6 has been installed everywhere. But check also with redesign of config hierarchies, as described in ticket 68047.
update tlgnr_config set value='smtp-dev.adnovum.ch', ctl_mod_ts=systimestamp, ctl_mod_uid=1, description=description || ' Modified by testdata_load_prod_dump.sql for ADN internal.'
where key='mail.smtp.host' and instance is null and value='smtp.adnovum.ch';
commit;

spool off
