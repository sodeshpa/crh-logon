-- $Revision$
-- $Date$
--
-- 2011: based on ticket ADN71680 and ADN74059
-- 2012: Additional input / update in combination with cluster 1: LOGON-4620 / LOGON-4621

prompt Spooling into file /temp/logon_create_db_11_initialdata_CL2log
spool /temp/logon_create_db_11_initialdata_CL2.log

-- Default Logon NLS settings
@../set_logon_session_settings.sql

-- NOT calling REAL users here (only 'insert_user_person_driver_role_NOT_NEEDED_FOR_C2.sql' exists)
-- @@insert_user_person_driver_role.sql

prompt Plants, Shippingpoints, Loadingpoints

-- Plants: Since October 2012 contain data from BOTH clusters, but with different active flags.
@@insert_plant_plant_compcode.sql

-- In FBNL, no rail as in Switzerland, therefore, no plant_track data.
-- @@tlgnr_plant_track.sql

-- Shippingpoint and Loadingpoints may be "for all clusters" soon as well, but for now: keep seperate.
-- Makes sense, due to weighingsystem configuration etc.
@@insert_shippingpoint.sql
@@insert_loadingpoint.sql

-- Pool: Considered cluster specific data, therefore called here (and not in initialdata_2.1_all_clusters)
@@tlgnc_pool.sql

-- sorg and sarea:
-- Since October 2012 considered 'per cluster' again: Contain data from BOTH clusters, but with different active flags.
-- @@tlgnr_sorg.sql -- TODO uma - remove, once tested!
@@insert_sorg_bs.sql
@@insert_sorg_tx.sql
@@insert_sarea.sql
call ulgn01.update_planning_mat_sarea();
@@../refdata/tlgnr_incoterm_deliv_type_bs.sql
@@insert_plant_sarea.sql

-- Sales Office, Sales Group:
-- Originally, were in 2 seperate lists (C0, C1), with only the per-cluster data. Since October 2012: identical data, with data from both clusters in each cluster.
-- Files are actually identical in FBNL and CL1 currently (19.10.2012), but could change later, e.g. in their "active"-flag.
@@insert_soffice_bs.sql
@@insert_sgroup_bs.sql

-- Transport Zone: Currently empty, because not sure if FBNL will use zone-based pricing at all.
@@tlgnr_trsp_zone.sql

-- Make Pickup Carrier + Generic Carrier accessible for all Cluster 0 company codes
@@artificial_pickup_entries.sql

-- Adjusting Sequences specific for Cluster 1, see also LOGON-4621, C1 Data Migration Tasks.
--
-- 1) Range
-- Tried to update on 18.10.2012 on request Crh: 9 instead of 8 digits!
-- Cluster 0: 100'000'000 to 199'999'999 (-> see custom_sequences.sql, initialdata_fbnl.sql + if not overridden)
-- Cluster 1: 200'000'000 to 299'999'999 -> see c1_initialdata
-- Cluster 2: 300'000'000 to 399'999'999 -> c2_initialdata, once that folder/set of initialdata has been created, ca 2013
-- Cluster 3: 400'000'000 to 499'999'999 -> c3_initialdata, once that folder/set of initialdata has been created, ca 2013/2014
-- OPEN ISSUE however: Requires LDS ifc changes! therefore, 24.10.2012, going back to 8 digits again!! (manually at Crh LPx, and in CVS)
--
-- 2) 'nocache' option:
-- This could be a performance issue on a RAC environment. Default is caching to 20 (per node), i.e., less synchronization required.
-- Disadvantages of caching:
-- - numbers not guaranteed to be 'ordered' by creation time
-- - A gap is produced when DB or even a single app-server (db client) is restarted. With 5 app servers and 2 DB nodes, this can be significant!
-- Because these numbers have a limited range, AND are truely visible business numbers,
-- we decided that 'look nice' and 'consume only when needed' is more important than
-- performance for these 2 specific sequences.
-- All other sequences of course stay with the default of cache 20.
drop sequence   slgnc_order_number;
create sequence slgnc_order_number     start with 30000000 maxvalue 39999999 nocache;
grant select on slgnc_order_number     to rl_lgn_user;

-- 18.10.2012: requested by Crh: Customer Order: start with 37... (and 17...., 27... for the other clusters)
drop sequence   slgnc_custorder_number;
create sequence slgnc_custorder_number start with 37000000 maxvalue 39999999 nocache;
grant select on slgnc_custorder_number to rl_lgn_user;

drop sequence   slgnc_load_order;
create sequence slgnc_load_order start with 3000000000 maxvalue 3999999999;
grant select on slgnc_load_order to rl_lgn_user;

drop sequence   slgnc_load_order_item;
create sequence slgnc_load_order_item start with 3000000000 maxvalue 3999999999;
grant select on slgnc_load_order_item to rl_lgn_user;



-- pda devices?

-- rfid tags?

spool off
