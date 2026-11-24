-- $Revision$
-- $Date$
--
-- Initialdata for all of LOGON 2.1, Cluster 0 and Cluster1 commons
-- 
-- call sequence:
-- - objs_sap.sql      user ulgnsap
-- - objs.sql          user ugn01 + all (technical) refdata
--
-- - adza_initialdata: @intialdata_ADZA.sql              : Stuff from the "EBM blueprint", i.e. artificial countries Andorra/South-Africa.
--                                                         Should NOT be loaded in Prod env! -> explicit now.
--
-- - initialdata:      @initialdata_2.1_all_clusters.sql : Stuff to be truely loaded identical for BOTH clusters.
--
-- - cl0_initialdata:  @initialdata_FBNL                 : Stuff to be loaded ONLY for cluster 0 (Pools, real users etc). 
--                                                         CAREFUL: Even though plants/sareas etc are COMMON, they DIFFER per cluster, in their active flag!!
--
-- - cl1_initialdata:  @initialdata_cl1                  : Stuff to be loaded ONLY for cluster 1 (Pools, real users, pda devices, ...)
--                                                         CAREFUL: Even though plants/sareas etc are COMMON, they DIFFER per cluster, in their active flag!!
-- 
-- - ADNOVUM ONLY things like:
-- -    adza_testdata\testdata_adnovum_adza.sql  (formerly known as: minimal_contract_and_dependencies.sql)
-- -    testdata_load_prod_dump.sql : Test data which could be loaded on any productive dump + modifying passwords etc.
-- -    testdata/insert_config.sql  : Dev config

prompt Spooling into file /temp/logon_create_db_10_initialdata_2.2_all_clusters.log
spool /temp/logon_create_db_10_initialdata_2.2_all_clusters.log

-- Default Logon NLS settings
@../set_logon_session_settings.sql

-- Company Codes + Purchasing Org
-- 18.10.2012: All Codes from all clusters in both database, identical "active" flags. But, without the ADZA ones
@@insert_companycode.sql
@@insert_purchasingorg.sql

-- role, role_func as well as user. user_role initialdata: This is cluster specific data!
@@tlgnu_role.sql
@@tlgnu_role_func.sql

prompt Loading the common users: Currently (Oct 2012) the TESTERS, as well as support team Crh + AdNovum
-- real users of each cluster: Add to initialdata_cl0 and initialdata_cl1 !
@@insert_user_person_driver_role.sql

-- Configuration Table for Mapping Material Packaging Types
@@tlgns_matpkg_map.sql

-- prompt Plant, Shippingpoints, Loadingpoints
-- @@tlgnr_plant.sql - this is obsolete now, and removed. It has been replaced by a CSV based loader, together with tlgnc_plant_companycode
-- However, since October 2012, they are per-cluster data again, due to different "active" flags
--
-- plant-track: Considered "per cluster" initialdata, similar to shippingpoints/loadingpoints.
-- Careful: Loadingpoint has a dependency to plant_track!
-- @@tlgnr_plant_track.sql

-- Shippingpoint and Loadingpoints may be "for all clusters" soon as well, but for now: keep seperate.
-- Makes sense, due to weighingsystem configuration etc.
-- @@insert_shippingpoint.sql
-- @@insert_loadingpoint.sql

-- @@tlgnc_pool.sql -> This is considered cluster specific data.

-- sorg must be called before sarea, therefore: calling file from all relevant clusters would happen now:
-- However, since October 2012, they are per-cluster data again, due to different "active" flags -> moved to those independent scripts again..., together with sarea + plant_sarea
-- @@../fbnl_initialdata/tlgnr_sorg.sql - moved to initialdata_FBNL.sql again
-- @@../cl1_initialdata/tlgnr_sorg.sql  - moved to initialdata_cl1.sql  again
-- @@insert_sarea.sql
-- @@insert_plant_sarea.sql

-- Sales Office, Sales Group:
-- Originally, were in 2 seperate lists (C0, C1), but since October 2012 Ruben/Fernando want to load them in both Clusters.
-- On 18.10.2012 they unified it into ONE input file, with all active flags set, for all soffice/sgroup of both clusters.
-- We currently don't know if this will change any time, therefore, treat as "per cluster data" with identical input file for now.
-- @@../fbnl_initialdata/insert_soffice_bs.sql - moved to initialdata_FBNL.sql again
-- @@../fbnl_initialdata/insert_sgroup_bs.sql  - moved to initialdata_FBNL.sql again
-- @@../cl1_initialdata/insert_soffice_bs.sql  - moved to initialdata_cl1.sql  again
-- @@../cl1_initialdata/insert_sgroup_bs.sql   - moved to initialdata_cl1.sql  again

-- Transport Zone: Currently per cer cluster, but possibly in 1 list soon as well?
-- @@tlgnr_trsp_zone.sql

-- Add more data here which is
-- a) truely ALL (current) clusters.
--    If received in 2 lists: Put in respective directory, and call from here
--    (see sorg handling, at least as of Sept 2012... Things change quickly...)
-- b) received in only 1 merged list from Crh (it's really hard to split.... Keep in 1, if received in 1)

spool off
