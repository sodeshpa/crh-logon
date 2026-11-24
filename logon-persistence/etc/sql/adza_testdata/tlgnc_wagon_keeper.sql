-- Based on ADN74269
-- List based on values contained as "text" in column "keeper" in file
--   /share/doc/crh/pegasus/input/Bahn/RailcarMasterData_V0.5.5_20090807.xls
-- ADZA testdata wagons from -100


prompt '---------- adza_testdata/tlgnc_wagon_keeper.sql ----------'

prompt INSERTING into TLGNC_WAGON_KEEPER

insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-100, 'ADN Crh'           , null, 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_wagon_keeper (wagon_keeper_id, wagon_keeper_name, email_addresses, maintenance_email_flag, active_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-101, 'ADN Wascosa'          , null, 0, 1, 1, systimestamp, 1, systimestamp);

commit;
