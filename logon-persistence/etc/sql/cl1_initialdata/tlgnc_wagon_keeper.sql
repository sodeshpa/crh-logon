-- $Revision$
-- $Date$
--
-- The very original data for LOGON 1.3 came from this file
--   /share/doc/crh/pegasus/input/Bahn/RailcarMasterData_V0.5.5_20090807.xls
-- 
-- The current SQL script has been updated with data from the CHE production, DB dump July 2012. (e-mail addresses, active_flag, maintenance_email_flag)

prompt '---------- cl1_initialdata/tlgnc_wagon_keeper.sql ----------'

prompt INSERTING into TLGNC_WAGON_KEEPER

insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'EVS'              , 'jcr@ermewa.com, u.wehrli@ermewa.com, rail-ch@crh.com'                        , 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'FIXIT'            , null                                                                             , 0, 0, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Crh'           , 'rail-ch@crh.com'                                                             , 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Crh Hüntwangen', 'bahnwagen-ch@crh.com, rail-ch@crh.com, disposition-huentwangen@crh.com', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Hüntwangen'       , 'bahnwagen-ch@crh.com, rail-ch@crh.com, disposition-huentwangen@crh.com', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'NACCO'            , 'annabelle.antiphon@naccorail.com, rail-ch@crh.com'                           , 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'ÖBB'              , null                                                                             , 0, 0, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'RCA'              , null                                                                             , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'RhB'              , null                                                                             , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'SBB Cargo'        , null                                                                             , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Suter-Jörin'      , null                                                                             , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Transwaggon'      , 'sarigecili@transwaggon.ch, rail-ch@crh.com'                                  , 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Vigier'           , null                                                                             , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Wascosa'          , 'rail-ch@crh.com'                                                             , 1, 1, 1, systimestamp, 1, systimestamp);

commit;
