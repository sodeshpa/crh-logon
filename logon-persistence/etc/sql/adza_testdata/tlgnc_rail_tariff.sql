-- Based on ADN74269

prompt '---------- adza_testdata/tlgnc_rail_tariff.sql ----------'

insert into tlgnc_rail_tariff
(rail_tariff_id, nhm_code, dep_railway_station_id, arr_railway_station_id, wagon_uic_class_cd, tariff_number, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
select -1, null, null, null, null, 'TEST', 1, systimestamp, 1, systimestamp, 0
from dual
where not exists (
	select *
	from tlgnc_rail_tariff
	where dep_railway_station_id is null
	and arr_railway_station_id is null
	and nhm_code is null
	and wagon_uic_class_cd is null
);