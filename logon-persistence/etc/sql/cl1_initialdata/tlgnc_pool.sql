-- $Revision$
-- $Date$
--
-- 2009: List of initial pool as listed in 20090326_Pegasus_-_LOGON_Benutzer_Interface_Spezifikation_(Excel)_Version_1.0.0.9.2.xls
--       List of initial pools of North Germany in ticket 57243
-- 2012, EBM, Cluster 1, 4.6.2012: Simply copied them over. It's good enough to start with. Crh can update / refine.
--
-- 18.20.2012: Received a completely new but clean list of pools by Andi Schmid:
--   /share/doc/crh/logon_2_1/input/Cluster1_refdata_initialdata/20121018_updates/LOGON_C1_Update Initial data_mail_ASC.xls
--   Advantage: Nice prefix so that grouped by countries.
--   Change to input file: Put IDs starting at 200x + moved Pickup pool (all clusters into global initialdata (see "artificial_pickup_entries.sql")

prompt '---------- initialdata/tlgnc_pool.sql ----------'

prompt INSERTING into TLGNC_POOL

-- to prevent errors with the ampersand
set define off

insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2002, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2003, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2004, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2005, '10', 0, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2006, '50', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2007, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2008, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2009, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2010, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2011, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2012, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2013, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2014, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2015, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2016, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2017, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2018, '10', 1, 1, systimestamp, 1, systimestamp);
-- LOGON-6104
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2019, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2020, '20', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2021, '30', 1, 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - de/en/es/fr/it per pool_id

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2002, 'de', 'CH-CEM-Bulk Ost'     , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2002, 'en', 'CH-CEM-Bulk East'    , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2002, 'es', 'CH-CEM-Bulk East'    , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2002, 'fr', 'CH-CEM-Bulk Est'     , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2002, 'it', 'CH-CEM-Bulk Est'     , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2003, 'de', 'CH-CEM-Bulk West'    , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2003, 'en', 'CH-CEM-Bulk West'    , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2003, 'es', 'CH-CEM-Bulk West'    , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2003, 'fr', 'CH-CEM-Bulk Ouest'   , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2003, 'it', 'CH-CEM-Bulk Ovest'   , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2004, 'de', 'CH-CEM-Bulk Zentral' , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2004, 'en', 'CH-CEM-Bulk Central' , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2004, 'es', 'CH-CEM-Bulk Central' , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2004, 'fr', 'CH-CEM-Bulk Central' , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2004, 'it', 'CH-CEM-Bulk Centrale', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2005, 'de', 'CH-CEM-Bulk Süd'     , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2005, 'en', 'CH-CEM-Bulk South'   , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2005, 'es', 'CH-CEM-Bulk South'   , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2005, 'fr', 'CH-CEM-Bulk Sud'     , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2005, 'it', 'CH-CEM-Bulk Sud'     , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2006, 'de', 'CH-CEM-Rail'         , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2006, 'en', 'CH-CEM-Rail'         , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2006, 'es', 'CH-CEM-Rail'         , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2006, 'fr', 'CH-CEM-Rail'         , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2006, 'it', 'CH-CEM-Rail'         , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2007, 'de', 'CH-CEM-Bag'          , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2007, 'en', 'CH-CEM-Bag'          , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2007, 'es', 'CH-CEM-Bag'          , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2007, 'fr', 'CH-CEM-Bag'          , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2007, 'it', 'CH-CEM-Bag'          , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2008, 'de', 'SG-CEM-Bulk Süd'     , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2008, 'en', 'SG-CEM-Bulk South'   , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2008, 'es', 'SG-CEM-Bulk South'   , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2008, 'fr', 'SG-CEM-Bulk Sud'     , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2008, 'it', 'SG-CEM-Bulk Sud'     , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2009, 'de', 'IT-CEM-Bulk'         , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2009, 'en', 'IT-CEM-Bulk'         , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2009, 'es', 'IT-CEM-Bulk'         , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2009, 'fr', 'IT-CEM-Bulk'         , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2009, 'it', 'IT-CEM-Bulk'         , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2010, 'de', 'IT-CEM-Bag'          , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2010, 'en', 'IT-CEM-Bag'          , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2010, 'es', 'IT-CEM-Bag'          , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2010, 'fr', 'IT-CEM-Bag'          , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2010, 'it', 'IT-CEM-Bag'          , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2011, 'de', 'IT-AGG-Bulk'         , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2011, 'en', 'IT-AGG-Bulk'         , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2011, 'es', 'IT-AGG-Bulk'         , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2011, 'fr', 'IT-AGG-Bulk'         , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2011, 'it', 'IT-AGG-Bulk'         , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2012, 'de', 'CEM Süd'             , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2012, 'en', 'CEM South'           , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2012, 'es', 'CEM Sud'             , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2012, 'fr', 'CEM Sud'             , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2012, 'it', 'CEM Sud'             , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2013, 'de', 'CEM Nord'            , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2013, 'en', 'CEM North'           , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2013, 'es', 'CEM North'           , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2013, 'fr', 'CEM Nord'            , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2013, 'it', 'CEM Nord'            , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2014, 'de', 'CEM BAG'             , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2014, 'en', 'CEM BAG'             , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2014, 'es', 'CEM BAG'             , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2014, 'fr', 'CEM BAG'             , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2014, 'it', 'CEM BAG'             , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2015, 'de', 'AGG Nord'            , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2015, 'en', 'AGG North'           , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2015, 'es', 'AGG Nord'            , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2015, 'fr', 'AGG Nord'            , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2015, 'it', 'AGG Nord'            , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2016, 'de', 'CEM West'            , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2016, 'en', 'CEM West'            , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2016, 'es', 'CEM West'            , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2016, 'fr', 'CEM West'            , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2016, 'it', 'CEM West'            , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2017, 'de', 'AGG Süd'             , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2017, 'en', 'AGG South'           , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2017, 'es', 'AGG Sud'             , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2017, 'fr', 'AGG Sud'             , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2017, 'it', 'AGG Sud'             , 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2018, 'de', 'AGG West'            , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2018, 'en', 'AGG West'            , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2018, 'es', 'AGG West'            , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2018, 'fr', 'AGG West'            , 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2018, 'it', 'AGG West'            , 1, systimestamp, 1, systimestamp);

-- LOGON-6104
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2019, 'de', 'DE-PICKING', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2019, 'en', 'DE-PICKING', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2019, 'es', 'DE-PICKING', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2019, 'fr', 'DE-PICKING', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2019, 'it', 'DE-PICKING', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2020, 'en', 'DE - Block Train', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2020, 'de', 'DE - Blockzug', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2020, 'fr', 'DE - Block Train', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2020, 'it', 'DE - Block Train', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2020, 'es', 'DE - Block Train', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2021, 'en', 'DE - Ship', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2021, 'de', 'DE - Schiff', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2021, 'fr', 'DE - Ship', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2021, 'it', 'DE - Ship', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2021, 'es', 'DE - Ship', 1, systimestamp, 1, systimestamp);

commit;
