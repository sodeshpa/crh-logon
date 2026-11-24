-- Based on ADN74269
-- All based on CHE prod DB, with "1:-1" mapping & renaming: Prefix with ADN, and multiply IDs with -1 (=> identical ID, but negative)

prompt '---------- adza_testdata/tlgnc_pickup_loadingpoint.sql ----------'

insert into tlgnc_pickup_loadingpoint (pickup_loadingpoint, client_identifier, loadingpoint_id, orders_loadable_terminal_flag, bulk_pickup_creation_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1020,'ADN_PICKUP', -1008, 1, 0, 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pickup_loadingpoint (pickup_loadingpoint, client_identifier, loadingpoint_id, orders_loadable_terminal_flag, bulk_pickup_creation_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1021,'ADN_PICKUP_FENICS', -9998, 0, 0, 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pickup_loadingpoint (pickup_loadingpoint, client_identifier, loadingpoint_id, orders_loadable_terminal_flag, bulk_pickup_creation_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1022,'ADN_PICKUP_PARALLEL', -1503, 1, 0, 1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_pickup_loadingpoint (pickup_loadingpoint, client_identifier, loadingpoint_id, orders_loadable_terminal_flag, bulk_pickup_creation_flag, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1023,'ADN_PICKUP_PARALLEL_NL', -1503, 0, 0, 1, systimestamp, 1, systimestamp, 0);

---------------------------------------------------
-- Terminal Application Test Pickup Loadingpoint --
---------------------------------------------------

insert into tlgnc_pickup_loadingpoint
(pickup_loadingpoint, client_identifier, loadingpoint_id, orders_loadable_terminal_flag, bulk_pickup_creation_flag, ctl_cre_uid, ctl_cre_ts  , ctl_mod_uid, ctl_mod_ts  , ctl_tcn) values
(-1026              , 'ADN_TEST_TA'    , -1016          , 1                            , 1                        , 1          , systimestamp, 1          , systimestamp, 0      );
