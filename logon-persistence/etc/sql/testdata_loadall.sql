-- Loads all testdata in a correct order.
--
-- Execute in:
-- - AdNovum DBs (for integration tests, or to have non-empty tables)
-- - Sometimes also useful in Crh test-environment
-- - NOT meant for productive database! -> such data to be placed in initialdata_loadall.sql
--
-- Note, that Errors (primary key constraint violated) are to be expected if executed twice.
-- Most of those files are simplistic or invented data.
-- Many of those files will subsequently be replaced by real (initial) data by Crh.
--
-- NOTE (romek Aug 31, 2011): In Logon 1.6 (branch logon_1_6_0_1) we had material conditions and transport
-- conditions, which were added in tlgns_sap_mat_cond.sql and insert_sap_transport_cond.sql.
-- In case they are needed again, for more information you can also check our Twiki:
-- https://aww.adnovum.ch/twiki/bin/view/CrhPegasus/WorkshopCrh20090622#Minutes_1_Transport_Conditions

spool /temp/logon_testdata_loadall.log

-- to prevent errors with the ampersand
set define off
-- Default Logon NLS settings
@set_logon_session_settings.sql

-- Additional currencies and exchange rates, to test correct conversions (just EUR+CHF is not meaningful to test)
@testdata/tlgnr_currency.sql
@testdata/tlgnc_currency_exch_rate.sql

-- BP testdata is based on Excel extracts from Crh/SAP, and transformed into SQL INSERT scripts
-- Maybe some of those files will be added as initial data (instead of
-- testdata), to be independent of an initial SAP data load.
-- In this case, get the real data after a clean setup and sap test run from Crh test environment.
/* @testdata/tlgns_sap_bp.sql - Deleted the file, because too old (LOGON 1.0/2009) and no plans to reactivate/update */ 
@testdata/tlgns_sap_bp_sarea.sql
@testdata/tlgns_sap_bp_pf_shipto.sql
@testdata/tlgns_sap_bp_pf_billto.sql

-- Material testdata is based on early Excel extracts from Crh/SAP, and transformed into SQL INSERT Scripts
-- Maybe some of those files will be added as initial data (instead of
-- testdata), to be independent of an initial SAP data load.
-- In this case, get the real data after a clean setup and sap test run from Crh test environment.
@testdata/tlgns_sap_material.sql
@testdata/tlgns_sap_material_description.sql
@testdata/tlgns_sap_material_alt_uom.sql
@testdata/tlgns_sap_material_plant.sql
@testdata/tlgns_sap_material_sarea.sql

-- Only very few vendors, looks handmade
@testdata/tlgns_sap_vendor.sql

-- Additional users needed in AdNovum (only!). Do not mix initial data and testdata in security relevant parts!
@testdata/tlgnc_person.sql
-- No need to create additional roles just for testing
@testdata/tlgnu_user.sql
@testdata/tlgnu_user_role.sql
@testdata/tlgnc_driver.sql

-- Handmade data access restriction data
@testdata/tlgnu_user_soldto_bp_acc.sql
@testdata/tlgnu_user_shipto_acc.sql
@testdata/tlgnu_user_sarea_acc.sql
@testdata/tlgnu_user_sgroup_acc.sql

-- For all adnovum team members create the initial set of test users with different roles see https://aww.adnovum.ch/confluence/display/CrhPegasus/AdNovum+team+member+user+set
@testdata/insert_user_person_driver_role.sql

-- Handmade initial (test)data for cost calculation
@testdata/tlgnc_iud_cost.sql

-- Will finally become the initial data at Crh production environment.
-- However, in the first round (in initialdata), the data is not loaded yet, because there are not SAP materials in the DB.
-- Therefore, execute again, when at least we have our test materials in place.
@initialdata/insert_mat_plant.sql

-- Rail data: Material Compatibility (Product Change Matrix)
-- In the real 1.1 setup based on ulgnmig, this is real initialdata (see initialdata_load_1.1.sql)
-- For the setup without ulgnmig, must be considered testdata (because it's dependent on tlgns_sap_material.sql)
-- TODO: This, together with all SAP etc data should NOT be run on a DB which was created with the help of original Crh Prod DB!
@initialdata/insert_mat_compat.sql
@initialdata/insert_mat_compat_mat.sql

-- Statement moved into minimal_setup, because it depends on the 'initial shippingpoint'.
-- TODO: Add real data from Crh, for real shippingpoints, but then move to "initialdata"
-- @testdata/tlgnc_mat_shippingpoint.sql

-- A few AdNovum printers, similar to Crh's real ones.
@testdata/insert_printer_bs.sql

-- Handmade initial (test)data, mainly for AdNovum internal setup
-- Replace with real data from Crh when their data is stable enough,
-- but make sure there is a set of testdata on which automatic tests can rely on.
@testdata/minimal_setup.sql

-- Simple vehicles (ships and block trains)
@testdata/tlgnc_simple_vehicle.sql

-- Not real testdata, but DEV-ONLY data
-- This file should NOT automatically be called together with testdata, because we do NOT want them by default in TLGN01/RLGN01 etc.
-- @testdata/insert_config.sql

spool off
