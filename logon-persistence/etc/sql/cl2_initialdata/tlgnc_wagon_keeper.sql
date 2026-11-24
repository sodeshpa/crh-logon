-- $Revision$
-- $Date$
--
-- The very original data for LOGON 1.3 came from this file
--   /share/doc/crh/pegasus/input/Bahn/RailcarMasterData_V0.5.5_20090807.xls
-- 
-- The current SQL script has been updated with data from the CHE production, DB dump July 2012. (e-mail addresses, active_flag, maintenance_email_flag)

prompt '---------- cl2_initialdata/tlgnc_wagon_keeper.sql ----------'

prompt INSERTING into TLGNC_WAGON_KEEPER

insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Bulgarian State Railways'   , null , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'DB Schencker Rail Romania'  , null , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Crh (Bulgaria)'          , null , 0, 0, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Crh (Romania)'           , null , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Crh (Slovakia)'          , null , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Crh (Wien)'              , null , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Pultrans KFT'               , null , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Raj-Wagon, Spol SRO'        , null , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Servtrans Invest'           , null , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Transplus (Slovenko) SRO'   , null , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Vest Trans Ploiesti'        , null , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Wagon Servis, Spol SRO'     , null , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Vigier'                     , null , 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon_keeper.nextval, 'Wascosa'                    , null , 0, 1, 1, systimestamp, 1, systimestamp);

commit;
