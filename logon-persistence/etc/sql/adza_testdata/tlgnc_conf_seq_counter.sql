--------------------------------------------------------
--  Test Data for configurable sequence counter  
--------------------------------------------------------

-- Default usage type
insert into tlgnc_conf_seq_counter (conf_seq_counter_id, seq_usage_cd, companycode_cd, plant_cd, prefix, range_from, range_to, valid_from, last_value, locked, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-1, 0, 'AD01', '8101', 'M',   1,   9, to_date('20121101','YYYYMMDD'), null, 0, 1, systimestamp, 1, systimestamp, 1);
insert into tlgnc_conf_seq_counter (conf_seq_counter_id, seq_usage_cd, companycode_cd, plant_cd, prefix, range_from, range_to, valid_from, last_value, locked, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-2, 0, 'AD02', '8101', 'A',   1,   9, to_date('20121101','YYYYMMDD'), null, 0, 1, systimestamp, 1, systimestamp, 1);
insert into tlgnc_conf_seq_counter (conf_seq_counter_id, seq_usage_cd, companycode_cd, plant_cd, prefix, range_from, range_to, valid_from, last_value, locked, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-3, 0, 'AD03', '8101', 'W',   1,  99, to_date('20121101','YYYYMMDD'), null, 0, 1, systimestamp, 1, systimestamp, 1);
insert into tlgnc_conf_seq_counter (conf_seq_counter_id, seq_usage_cd, companycode_cd, plant_cd, prefix, range_from, range_to, valid_from, last_value, locked, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-4, 0, 'ZA01', '8101', 'O',   1,  99, to_date('20121101','YYYYMMDD'), null, 0, 1, systimestamp, 1, systimestamp, 1);

-- Cash sales usage type
insert into tlgnc_conf_seq_counter (conf_seq_counter_id, seq_usage_cd, companycode_cd, plant_cd, prefix, range_from, range_to, valid_from, last_value, locked, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-11, 1, 'AD01', '8101', 'M',   1,   9, to_date('20120101','YYYYMMDD'), null,0,  1, systimestamp, 1, systimestamp, 1);
insert into tlgnc_conf_seq_counter (conf_seq_counter_id, seq_usage_cd, companycode_cd, plant_cd, prefix, range_from, range_to, valid_from, last_value, locked, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-12, 1, 'AD02', '8101', 'A',   1,   9, to_date('20120101','YYYYMMDD'), null,0,  1, systimestamp, 1, systimestamp, 1);
insert into tlgnc_conf_seq_counter (conf_seq_counter_id, seq_usage_cd, companycode_cd, plant_cd, prefix, range_from, range_to, valid_from, last_value, locked, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-13, 1, 'AD03', '8101', 'W',   1,  99, to_date('20120101','YYYYMMDD'), null,0,  1, systimestamp, 1, systimestamp, 1);
insert into tlgnc_conf_seq_counter (conf_seq_counter_id, seq_usage_cd, companycode_cd, plant_cd, prefix, range_from, range_to, valid_from, last_value, locked, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-14, 1, 'ZA01', '8101', 'O',   1,  99, to_date('20120101','YYYYMMDD'), null,0,  1, systimestamp, 1, systimestamp, 1);

-- Export invoice usage type
insert into tlgnc_conf_seq_counter (conf_seq_counter_id, seq_usage_cd, companycode_cd, plant_cd, prefix, range_from, range_to, valid_from, last_value, locked, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-21, 2, 'AD01', '8101',null,3000,3500, to_date('20120701','YYYYMMDD'), null,0,  1, systimestamp, 1, systimestamp, 1);
insert into tlgnc_conf_seq_counter (conf_seq_counter_id, seq_usage_cd, companycode_cd, plant_cd, prefix, range_from, range_to, valid_from, last_value, locked, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-22, 2, 'AD02', '8101',null,3000,3500, to_date('20120701','YYYYMMDD'), null,0,  1, systimestamp, 1, systimestamp, 1);
insert into tlgnc_conf_seq_counter (conf_seq_counter_id, seq_usage_cd, companycode_cd, plant_cd, prefix, range_from, range_to, valid_from, last_value, locked, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-23, 2, 'AD03', '8101',null,3000,3500, to_date('20120701','YYYYMMDD'), null,0,  1, systimestamp, 1, systimestamp, 1);
insert into tlgnc_conf_seq_counter (conf_seq_counter_id, seq_usage_cd, companycode_cd, plant_cd, prefix, range_from, range_to, valid_from, last_value, locked, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (-24, 2, 'ZA01', '8101',null,3000,3500, to_date('20120701','YYYYMMDD'), null,0,  1, systimestamp, 1, systimestamp, 1);

commit;
