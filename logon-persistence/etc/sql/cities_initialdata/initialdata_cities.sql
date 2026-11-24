prompt Spooling into file /temp/logon_create_db_30_initialdata_cities.log
spool /temp/logon_create_db_30_initialdata_cities.log

-- Execute cities cluster dependent csv and sql content

-- Default Logon NLS settings
@../set_logon_session_settings.sql

@@insert_user_person_driver_role.sql

@@insert_plant_plant_compcode.sql

@@insert_sorg_bs.sql
@@insert_sorg_tx.sql

@@insert_soffice_bs.sql
@@insert_sgroup_bs.sql

@@insert_rfidtag.sql

@@insert_railway_station.sql
@@insert_rail_distance.sql

@@tlgnc_maint_rhythm.sql

@@tlgnr_trsp_zone.sql
@@tlgnc_trsp_zone_polygon.sql

@@tlgnc_wagon_keeper.sql

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
create sequence slgnc_order_number     start with 40000000 maxvalue 49999999 nocache;
grant select on slgnc_order_number     to rl_lgn_user;

-- 18.10.2012: requested by Crh: Customer Order: start with 37... (and 17...., 27... for the other clusters)
drop sequence   slgnc_custorder_number;
create sequence slgnc_custorder_number start with 47000000 maxvalue 49999999 nocache;
grant select on slgnc_custorder_number to rl_lgn_user;

drop sequence   slgnc_load_order;
create sequence slgnc_load_order start with 4000000000 maxvalue 4999999999;
grant select on slgnc_load_order to rl_lgn_user;

drop sequence   slgnc_load_order_item;
create sequence slgnc_load_order_item start with 4000000000 maxvalue 4999999999;
grant select on slgnc_load_order_item to rl_lgn_user;

-- pda devices?
-- rfid tags?

spool off
