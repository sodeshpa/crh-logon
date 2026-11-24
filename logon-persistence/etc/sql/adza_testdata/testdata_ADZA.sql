-- Purpose of this file:
-- - Fake CONTRACTS to allow for Order Taking independent of any SAP EBM data
-- - and many, many more objects to allow testing of various LOGON aspects without dependency to Crh
-- - For LOGON 2.0/2.1 ONLY, without any dependencies to SWISS (CHE) data!

/**
 * Run this from the parent directory with the following command:
 *
 * @install_adnovum_testdata.sql
 */

spool /temp/logon_create_db_21_testdata_ADZA.log

-- If mapping to LESS plants for ADZA:
-- cleanup the insert_tlgns_sap_material.sql to the actually necessary entries,
-- to avoid errors due to duplicate mat_plant entries that could result.

-- disabled from export set of tlgnc_mat_plant: S994 unknown, S999 Kraftwerk Walsum
-- even maintaining materials for som many plants is quite difficult.
-- For the rest: disable also entries in tlgns_sap_mat_plant

-- Note: On purpose negative IDs for most of the test-objects so we are out of the region of normal data (possibly created by using sequences).
-- Also, when data is well managed, this set of testdata could be loaded onto any database, never interfering with real data.
-- Do NOT use '-1', as this is reserved in the application for "empty selection" in Dropdowns etc. => Start with -11
--
-- The "random" looking negative IDs are for objects which are directly based on LOGON 1.6 CHE data (made independent of CHE, base on artificial ADZA)

@@adza_testdata/insert_tlgnc_railway_station.sql
@@adza_testdata/insert_tlgns_sap_bp.sql
@@adza_testdata/insert_tlgns_sap_bp_sarea.sql
@@adza_testdata/insert_tlgns_sap_bp_pf_shipto.sql
@@adza_testdata/insert_tlgns_sap_bp_pf_billto.sql
@@adza_testdata/insert_tlgns_sap_bp_pf_payer.sql
@@adza_testdata/insert_tlgns_sap_bp_companycode.sql
@@adza_testdata/insert_tlgns_sap_material.sql
@@adza_testdata/insert_tlgnc_mat_plant.sql
@@adza_testdata/contract_based_on_mat_cond_shipto_1208869_Regli_Zumdorf.sql

--WARNING!!!! Please make sure that @@adza_testdata/contract_based_on_mat_cond_shipto_1208869_Regli_Zumdorf.sql is called first this is using that shipto, but this does not belong anymore to swiss materials or anythin, this is completely just a test, not based on anything
--a special contract containing generic materials, and a service item for it's sales area, the shipto is zumdorf, but the service will be available for the whole sales area
@@adza_testdata/contract_shipto_1208869_Regli_Zumdorf_generic_materials.sql
--WARNING this below uses this above!
@@adza_testdata/contracts_test_contract_hierarchy.sql

@@adza_testdata/contract_based_on_mat_cond_shipto_1209037_Albula.sql
@@adza_testdata/contract_based_on_mat_cond_shipto_1863947_FVL_Altdorf.sql
@@adza_testdata/contract_based_on_mat_cond_shipto_1866134_FVL_Glattbrugg.sql
@@adza_testdata/stpo_based_on_dispocard_shipto_1209206_Werk_Untervaz_IUD.sql
@@adza_testdata/stpo_based_on_dispocard_shipto_1209206_Werk_Untervaz_IUD_Nr_2.sql
@@adza_testdata/contract_Werk_Untervaz_go_live.sql
@@adza_testdata/contract_Werk_Untervaz_outdated.sql
@@adza_testdata/contract_based_on_mat_cond_shipto_1863947_FVL_Altdorf_shipto_eq_soldto.sql
@@adza_testdata/contract_Werk_Untervaz_cash_customers.sql
@@adza_testdata/contract_OneTime_Soldto.sql
@@adza_testdata/contract_OneTime_Shipto.sql
@@adza_testdata/contract_shipto_1208869_Regli_Zumdorf_quantity_restriction.sql
@@adza_testdata/contracts_for_customer_order_block.sql
@@adza_testdata/contracts_adn_aggregate_and_landfill_customer.sql
@@adza_testdata/contractitems_planning_material.sql
@@adza_testdata/contract_terminal_application_test.sql

-- PRECONDITIONS for LOGIN to the application
-- tlgnc_person:    All ADNOVUM persons (for the users)
-- tlgnc_user:      All ADNOVUM users
-- tlgnc_user_role: All ADNOVUM user-roles


-- A set of initial shippingpoints, connected to railway stations as well.
@@adza_testdata/tlgnc_shippingpoint.sql

@@adza_testdata/tlgnc_loadingpoint.sql
--needed by NB tests otherwise material is not available on the loadingpoint, precondition insert_tlgns_sap_material.sql and tlgnc_loadingpoint.sql
@@adza_testdata/tlgnc_ldpoint_mat_restr.sql
@@adza_testdata/tlgnc_pickup_loadingpoint.sql

-- And initial railway data: timetable + distance for all stations in use.
@@adza_testdata/insert_rail_vendor_carrier_pool.sql

@@adza_testdata/tlgnc_rail_timetable.sql
@@adza_testdata/tlgnc_rail_distance.sql
@@adza_testdata/tlgnc_rail_tariff.sql

@@adza_testdata/tlgnc_wagon_keeper.sql
@@adza_testdata/insert_wagon.sql

-- Users logonadmin_ad01, logonadmin_ad02, logonadmin_za01
@@adza_testdata/insert_tlgnu_user.sql

--Handmade test data for testing external order functionality. Also used by selenium test.
@@adza_testdata/external_order_workflow_test_base.sql

--Test data for linking subcontractors
@@adza_testdata/linking_subcontractor_test_objects.sql

--advance payment test ship-to
@@adza_testdata/advance_payment_test_shipto.sql

--Test data for itrack tracking
@@adza_testdata/itrack_test_objects.sql

--Test data for freight cost calculation
@@adza_testdata/insert_tlgnc_freight_cost.sql


-- loadtest related testdata
@@adza_testdata/load_test_objects_for_ebm.sql

-- add missing self-referencing partner functions
@@adza_testdata/insert_missing_partner_functions.sql

@@adza_testdata/tlgnc_conf_seq_counter.sql

-- deciding cash sales / export invoice dn generation
@@adza_testdata/tlgnr_map_extra_dn_type_bs.sql


commit;

spool off
