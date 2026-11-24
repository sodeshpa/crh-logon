/*
Generated based on Prod CHE DB, all connections between the manually added railway stations, and then manually formatted / cleanup up, plus all IDs turned into NEGATIVE.
dep_railway_station_id in (1408, 1037, 1379, 1543, 1862) and arr_railway_station_id in (1408, 1037, 1379, 1543, 1862)
*/
prompt '---------- adza_testdata/tlgnc_raildistance.sql ----------'

insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values ( -4236,-1408,-1037,69000,1,1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values ( -3743,-1408,-1543,96000,1,1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values ( -4032,-1408,-1379,33000,1,1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values ( -5605,-1037,-1408,69000,1,1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values ( -5609,-1037,-1862, 1001,1,1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values ( -5824,-1408,-1862, 1001,1,1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values ( -5897,-1543,-1408,96000,1,1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values ( -5898,-1543,-1862, 1001,1,1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values ( -6062,-1862,-1408, 1001,1,1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values ( -6182,-1862,-1862, 1001,1,1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values (  -999,-1408,-1408, 1001,1,1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values ( -1700,-1861,-1860,10000,1,1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values ( -1701,-1860,-1861,10000,1,1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values ( -1702,-1861,-1861, 1001,1,1, systimestamp, 1, systimestamp, 0);
insert into tlgnc_rail_distance (rail_distance_id,dep_railway_station_id,arr_railway_station_id,tariff_distance_m,active_flag,ctl_cre_uid,ctl_cre_ts,ctl_mod_uid,ctl_mod_ts,ctl_tcn) values ( -1703,-1860,-1860, 1001,1,1, systimestamp, 1, systimestamp, 0);
commit;
