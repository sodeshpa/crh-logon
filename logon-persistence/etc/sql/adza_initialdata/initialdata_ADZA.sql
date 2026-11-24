-- ADZA = Andorra Zouth Africa = EBM "Blueprint" Data.
--
-- Note, that much of this data is actually not from SAP, but already invented by AdNovum. If this is an issue: Move more of that into "adza_testdata".
--
-- 10.18.2012: ADZA/blueprint data is no longer perceived as "always load in all clusters", Crh no longer wants it in PROD system!
-- Therefore, make this an explicit call, to either load it, or not load it.

/**
 * Run this from the parent directory with the following command:
 *
 * @install_adnovum_testdata.sql
 */

prompt Spooling into file /temp/logon_create_db_20_initialdata_ADZA.log
spool /temp/logon_create_db_20_initialdata_ADZA.log

-- In a productive system, none of the blueprint data should be loaded, not even the companycode / purchasing org.
@@adza_initialdata/insert_companycode.sql
@@adza_initialdata/insert_purchasingorg.sql

-- plant before plant_track
@@adza_initialdata/tlgnr_plant.sql
-- plant_track before shippingpoint
@@adza_initialdata/tlgnr_plant_track.sql
-- shippingpoint before loadingpoint
@@adza_initialdata/insert_shippingpoint.sql
@@adza_initialdata/tlgnc_person_user_role.sql
@@adza_initialdata/tlgnc_pool.sql

-- sorg before sarea
@@adza_initialdata/tlgnr_sorg.sql
@@adza_initialdata/tlgnc_sarea.sql
call ulgn01.update_planning_mat_sarea();
--Do not create tlgnr_incoterm_deliv_type_bs for any productive sales area! what incoterm+delivery type combination is missing for a certain sales area that is forbidden in production as well.
--@@refdata/tlgnr_incoterm_deliv_type_bs.sql

@@adza_initialdata/tlgnc_plant_compcode.sql
@@adza_initialdata/tlgnc_plant_sarea.sql

@@adza_initialdata/tlgnr_soffice_bs.sql
@@adza_initialdata/tlgnr_sgroup_bs.sql
@@adza_initialdata/tlgnr_trsp_zone.sql

-- specific testdata
@@adza_initialdata/tlgnr_text.sql
@@adza_initialdata/tlgnr_deliv_note_sarea_text.sql
@@adza_initialdata/insert_pda_device.sql

-- Make Pickup Carrier accessible for all ADZA company codes
@@adza_initialdata/artificial_pickup_entries.sql

-- Incoterms for all Adnovum test sales areas
@@adza_initialdata/tlgnr_incoterm_deliv_type_bs.sql

spool off
