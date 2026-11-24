-- Master SQL script to create all DB entities for the Logon DB, including inserting of ref-data values.
-- The files for schema ulgnsap are NOT to be called from here, but from objs_sap.sql

--	"So you can ignore drop index: specified index does not exist."
--	
--	We had cases - don't ask me on which DBs - where CREATE index failed,
--	because the the previes drop unique constraint failed:
--	
--	A)
--	- drop unique  constraints...
--	- create unique ... deferred : sometimes FAILS
--	
--	It _should_ not be necessary to drop both the constraint AND the constraint of the unique constraint, but this is what we had to do on certain oracle instances:
--	
--	B)
--	- drop unique constraint...
--	- drop index... <<------ should be redundant, included in above, but it's not always
--	- create unique constraint ... deferred.
--	
--	We had to chose:
--	A  risk to NOT have a unique constraint in maybe 20% of the cases?
--	B  risk to have an error message to ignor in 80% of the cases?
--	
--	
--	A would have been a sever problem, we rely on them for correctness.
--	
--	B is what you see => Ignore. 

column patch_file_name new_value patch_file_name noprint;
select '''logon_create_db_03_objs''' as patch_file_name from dual;
@patch_spool_on.sql
COLUMN patch_file_name CLEAR

-- to prevent errors with the ampersand
set define off
-- Default Logon NLS settings
@set_logon_session_settings.sql

prompt ************************************************************************
prompt *** Create temporary table for storing the exact start time of the
prompt *** installation. Insert systimestamp.
prompt ************************************************************************

create table tmp_start_ts (
    install_start_ts                 timestamp            not null
);

insert into tmp_start_ts (install_start_ts) values (systimestamp);

commit;

prompt ************************************************************************
prompt *** Call all create scripts
prompt ************************************************************************

-- Creating the entities for the main schema
-- (all files in their natural order from logon-persistence/build/generated/gdi/schema2ddlentities/sql/create)
@create/10_gen_create_tables.sql
@custom_create_move_blobs.sql
@create/15_gen_create_indexes.sql
@create/20_gen_create_constraints_pk.sql
@create/22_gen_create_constraints_unique.sql
@create/24_gen_create_constraints_fk.sql
@create/26_gen_create_constraints_fk_index.sql
@create/28_gen_create_sequences.sql

prompt ************************************************************************
prompt *** Log start of installation into tlgnc_db_version_log. Remove
prompt *** temporary table.
prompt ************************************************************************

insert into TLGNC_DB_VERSION_LOG (
  db_version_log_id,
  version, install_start_ts, install_type,
  client_ip, user_name,
  ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
)
values (
  slgnc_db_version_log.nextval,
  '@VERSION@', (select install_start_ts from tmp_start_ts), 'initial',
  sys_context('userenv', 'ip_address'), sys_context('userenv', 'os_user'),
  1, systimestamp, 1, systimestamp, 0
);

commit;

drop table tmp_start_ts;

-- Higher level order objects. When inserting new files, make sure order is compatible
@custom_check_constraints.sql
@stored_procedures.sql
@custom_views.sql
@custom_views_reporting.sql
@custom_views_staging_area_info.sql

@custom_sequences.sql
@custom_indexes.sql

@logon_log_pkg.sql
@logon_util_pkg.sql

@upsert_planning_contract_items.sql
@@update_planning_mat_sarea.sql
@update_shipto_bp_meta.sql

@@sapimport_bpn_pkg.sql
@@sapimport_mn_pkg.sql
@@sapimport_mnn_pkg.sql
@@sapimport_sdn_pkg.sql
@@sapimport_vehn_pkg.sql
@@sapimport_vn_pkg.sql

@@delete_invalid_restrictions_pkg.sql
@@auto_init_shipto_sarea_restr_pkg.sql

@@initialize_common_division_pkg.sql

-- Those generated view-comments apply to views created with custom_views.
-- Should we rename that file to 35_custom_views.sql maybe? Anyway, the idea
-- that those files with XY_gen can simply be called as they are generated
-- does not work, latest when going into patch mode.
@create/40_gen_create_comments.sql 

-- Grant "select", "insert", "update", "delete" on tables/custom views/sequences respectively
@create/45_gen_grant_privileges.sql 

-- Grants to execute import packages / login user ulgn03
@custom_grants.sql

-- Manual overrides / corrections / extensions to generated SQL which cannot (yet) be done in the mapping files
@custom_overrides.sql

-- Needed by tlgnr_image.sql
@refdata_prepare.sql

-- Initial reference data values - Refdata which are referred to by other refdata must come first
-- tlgnr_lang MUST be the first, because tx table have a FK to lang!
@refdata/tlgnr_lang.sql
@refdata/tlgnr_mime_type.sql
@refdata/tlgnr_logon_vehicle_type_bs.sql   -- tlgnr_sap_vehicle_type     depends on logon_vehicle_type
@refdata/insert_package_type_bs.sql        -- tlgnr_material_package.sql depends on package_type
@refdata/insert_package_type_tx.sql
@refdata/tlgnr_sap_trsp_mode.sql
@refdata/tlgnr_uom.sql                     -- tlgnr_map_sap_uom_bs.sql   depends on tlgnr_uom
@refdata/insert_trailer_trsp_cat_bs.sql    -- tlgnr_sap_vehicle_type.sql depends on trailer_transport_cat
@refdata/insert_trailer_trsp_cat_tx.sql
@refdata/insert_truck_transport_cat_bs.sql -- tlgnr_sap_vehicle_type.sql depends on truck_transport_cat
@refdata/insert_truck_transport_cat_tx.sql
@refdata/tlgnr_trailer_type.sql            -- tlgnr_sap_vehicle_type.sql depends on trailer_type
@refdata/insert_truck_type_bs.sql          -- tlgnr_sap_vehicle_type.sql depends on truck_type
@refdata/insert_truck_type_tx.sql
@refdata/insert_truck_type_trsp_cat_bs.sql
@refdata/tlgnr_itrack_status_bs.sql

-- scripts starting with "insert_..." are scripts generated by GDI out of csv input files:
-- - logon-persistence/etc/csv
-- - logon-persistence/etc/gdi/csv2sql/templates
-- - logon-persistence/build/generated/gdi/csv2sql

-- Rest of Files from logon-business/etc/sql/refdata in ALPHABETICAL order if possible, i.e. no dependencies to later tables.
@refdata/tlgnr_archiving_log_event_bs.sql
@refdata/tlgnr_archiving_status_bs.sql
@refdata/tlgnr_assign_part_status.sql
@refdata/tlgnr_assign_status.sql
@refdata/tlgnr_assign_tr_status.sql
@refdata/tlgnr_assignment_part_type.sql
@refdata/tlgnr_assignment_type.sql
@refdata/tlgnr_audit_action.sql
@refdata/tlgnr_audit_bizevent_config.sql
@refdata/tlgnr_billto_ruletype.sql
@refdata/tlgnr_bizevent_type.sql
@refdata/tlgnr_region_bs.sql
@refdata/tlgnr_companycode_bs.sql
@refdata/tlgnr_cond_logon_type.sql
@refdata/tlgnr_cond_sap_type.sql
@refdata/tlgnr_container_trsp_cat.sql
@refdata/tlgnr_container_type.sql
-- @refdata/tlgnr_country_region_tz_map.sql can not be called here. must be called after tlgnr_timezone.sql
@refdata/tlgnr_currency.sql
@refdata/tlgnr_custorder_status.sql
@refdata/tlgnr_damagecode.sql
@refdata/tlgnr_daily_order_status.sql
@refdata/tlgnr_deliv_note_print_type.sql
@refdata/tlgnr_delivery_type.sql
@refdata/tlgnr_distribution_dest.sql
@refdata/tlgnr_fuel_type.sql
@initialdata/tlgnc_outputdoc_type.sql
@refdata/tlgnr_image.sql
-- @refdata/tlgnr_lang.sql can not be called here, but must be first refdata
@refdata/tlgnr_maint_rhythm_type.sql
@refdata/tlgnr_maint_type.sql
@refdata/tlgnr_map_kvgr5_distribdest.sql
-- @refdata/tlgnr_map_qty_restr.sql needs to be present after sap_contract_type
@refdata/tlgnr_map_sap_uom_bs.sql
@refdata/insert_material_package_bs.sql
@refdata/insert_material_package_tx.sql
-- @refdata/tlgnr_mime_type.sql must be before image.sql
@refdata/tlgnr_mop_status.sql
@refdata/tlgnr_order_creation_type.sql
@refdata/tlgnr_order_spi.sql
@refdata/tlgnr_order_status.sql
@refdata/tlgnr_order_rep_att_type.sql
-- @refdata/tlgnr_package_type.sql
@refdata/tlgnr_pda_error_message.sql
@refdata/tlgnr_pda_marketinfo.sql
@refdata/tlgnr_pda_message_type.sql
@refdata/tlgnr_pda_session_status.sql
@refdata/tlgnr_return_order_reason.sql
@refdata/tlgnr_return_material_usage.sql
@refdata/tlgnr_return_quant_handling.sql
@refdata/tlgnr_rfidobject_type.sql
@refdata/tlgnr_division.sql
@refdata/tlgnr_sap_account_group.sql
@refdata/tlgnr_sap_incoterm.sql
-- incoterm is commented out because it should be called after tlngc_sarea's inserts
--@refdata/tlgnr_incoterm_deliv_type_bs.sql
@refdata/tlgnr_sap_contract_type.sql
@refdata/tlgnr_map_qty_restr.sql
@refdata/tlgnr_sap_country.sql
-- @refdata/tlgnr_sap_incoterm.sql must be before contract_type (although FK is not forced at the moment v2.1.4.0)
@refdata/tlgnr_sap_language.sql
@refdata/tlgnr_sap_material_norm_bs.sql
@refdata/tlgnr_sap_order_extra_cost.sql
@refdata/tlgnr_sap_payment_method.sql
@refdata/tlgnr_sap_shipping_cond.sql
-- @refdata/tlgnr_sap_trsp_mode.sql must be before delivery_type
-- @refdata/tlgnr_sapimpstatus.sql can not be called here, as it is ref data for ulgnsap!
@refdata/tlgnr_sap_vehicle_char.sql
@refdata/insert_sap_vehicle_type_bs.sql
@refdata/insert_sap_vehicle_type_tx.sql
@refdata/tlgnr_seq_usage_bs.sql
@refdata/tlgnr_shippingpoint_type.sql
@refdata/tlgnr_shipto_type.sql
@refdata/tlgnr_signature_type.sql
@refdata/tlgnr_silo_verif_type.sql
@refdata/tlgnr_simple_vehicle_type.sql
@refdata/tlgnr_soffice.sql
@refdata/tlgnr_text.sql
@refdata/tlgnr_timezone.sql
@refdata/tlgnr_sap_tp_sap_vendor_pf_bs.sql
@refdata/tlgnr_country_region_tz_map.sql
-- @refdata/tlgnr_trailer_type.sql --> dependency see top
-- @refdata/tlgnr_trailer_trsp_cat.sql --> dependency see top
@refdata/tlgnr_silo_type.sql
@refdata/tlgnr_truck_maintenance.sql
-- @refdata/tlgnr_truck_transport_cat.sql --> dependency see top
-- @refdata/tlgnr_truck_type.sql --> dependency see top
-- @refdata/tlgnr_truck_type_trsp_cat_bs.sql -- no longer an explicit file, but part of build/generated/gdi/csv2sql/refdata/insert_truck_type_bs.sql
@refdata/tlgnr_uic_country.sql
@refdata/tlgnr_user_activity_action.sql
@refdata/tlgnr_user_type.sql
@refdata/tlgnr_wagon_event_status.sql
@refdata/tlgnr_wagon_trsp_cat.sql
@refdata/tlgnr_wagon_type.sql
@refdata/tlgnr_wagon_inactive_reason.sql
@refdata/tlgnr_waitingtime_reason.sql
@refdata/tlgnr_weigh_system_sp.sql
@refdata/tlgnr_weigh_system_lp.sql
-- Do NOT add at the end, but please insert in ALPHABETICAL order. It's easier to verify if some are missing. Thanks!

-- creates the system user
@refdata/technical_user_person_driver_role.sql

-- 18.10.2012: Treat as initialdata per cluster now.
-- @refdata/insert_companycode.sql   - split into initialdata_ADZA + initialdata cl0 / cl1
-- @refdata/insert_purchasingorg.sql - split into initialdata_ADZA + initialdata cl0 / cl1

-- nemethze/20110719: may later become company (cluster) depending, i.e. moved from global "refdata" to cluster specific initialdata?
-- @refdata/insert_sgroup_bs.sql, Is already per cluster, see
-- - cl1_initialdata/sgroup_bs.csv
-- - fbnl_initialdata/sgroup_bs.csv

-- nemethze/20110719: may later become company (cluster) depending, i.e. moved from global "refdata" to cluster specific initialdata?
@refdata/insert_simcard_bs.sql

--@refdata/insert_printer_bs.sql uma: printers can be skipped for now

prompt Calling script to fix missing translations in refdata...
-- Call only after all language dependent data has been loaded
@refdata/insert_refdata_in_all_languages.sql

prompt Loading Config and Tablebrowser Config, Cronjobs...

-- Config is NOT planned to be country/cluster specific, will remain "global". Reason: Contains both cluster0 as well as cluster1 config.
@refdata/insert_config.sql

-- Table must be before column!
@create/47_gen_insert_tablemeta.sql
-- Manual overrides!
@refdata/tlgnt_table_meta.sql
@refdata/tlgnt_column_meta.sql

prompt Loading Authorization (functions, roles, role-functions)...
-- Authorization and initial user/role data. Func has clealy refdata character (code must be changed when func is updated!)
@refdata/insert_func.sql

-- Technically necessary data in tlgnC and tlgnS tables are also some kind of "refdata", without it, major parts of the application don't work
-- Artificial pickup entries: vendor, carrier, pool, pool_carrier, trucks.
@refdata/artificial_pickup_entries.sql
-- Artificial planning material entries: material, material_descriptions.
@refdata/artificial_planning_material_entries.sql

-- Background jobs
@refdata/tlgnc_cronstatus.sql 

-- Testdata @testdata/... NOT to be called from objs.sql! -> testdata_loadall

-- 18.10.2012: ADZA/blueprint data is no longer perceived as "always load in all clusters", Crh no longer wants it in PROD system!
-- Therefore, make this an explicit call, to either load it, or not load it.
-- @@adza_initialdata/initialdata_ADZA.sql

prompt ************************************************************************
prompt *** Log end of installation into tlgnc_db_version_log
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

commit;

@patch_spool_off.sql
