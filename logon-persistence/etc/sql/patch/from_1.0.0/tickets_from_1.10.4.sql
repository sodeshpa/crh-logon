prompt ************************************************************************
prompt *** BEGIN CRHLOGON-1361 Demand planning - Material Production plant
prompt ************************************************************************

prompt Updating tlgnc_mat_plant

alter table ulgn01.tlgnc_mat_plant
    add default_prod_plant_flag number(1,0) default 0 not null;
comment on column ulgn01.tlgnc_mat_plant.default_prod_plant_flag is
    '1 = This is the default production factory for the material. 0 (standard behavior) = Not a (default) producing factory';

create index i_mat_plant_prod_plant_flag on ulgn01.tlgnc_mat_plant (default_prod_plant_flag) tablespace index_lgn_a;

prompt Updating tlgnc_mat_plant.default_prod_plant_flag

create or replace procedure upsert_mat_plant_prod_flag(i_sap_material_no IN VARCHAR2, i_plant_cd IN VARCHAR2)
is
	l_sap_material_id NUMBER;
begin
	begin
		select sap_material_id
		into l_sap_material_id
		from ulgn01.tlgns_sap_material
		where sap_material_no = i_sap_material_no;
	exception
		when NO_DATA_FOUND then
			return;
	end;

	update ulgn01.tlgnc_mat_plant set default_prod_plant_flag = 1
	where plant_cd = i_plant_cd and sap_material_id = l_sap_material_id;
end;
/

execute upsert_mat_plant_prod_flag('14001697', 'H133');
execute upsert_mat_plant_prod_flag('14001735', 'H133');
execute upsert_mat_plant_prod_flag('14001736', 'H133');
execute upsert_mat_plant_prod_flag('14001741', 'H133');
execute upsert_mat_plant_prod_flag('14001744', 'H133');
execute upsert_mat_plant_prod_flag('14001746', 'H133');
execute upsert_mat_plant_prod_flag('14001750', 'H133');
execute upsert_mat_plant_prod_flag('14001751', 'H133');
execute upsert_mat_plant_prod_flag('14001772', 'H133');
execute upsert_mat_plant_prod_flag('14001774', 'H133');
execute upsert_mat_plant_prod_flag('14001787', 'H133');
execute upsert_mat_plant_prod_flag('14001808', 'H133');
execute upsert_mat_plant_prod_flag('14001810', 'H133');
execute upsert_mat_plant_prod_flag('14001815', 'H133');
execute upsert_mat_plant_prod_flag('14001825', 'H133');
execute upsert_mat_plant_prod_flag('14001831', 'H133');
execute upsert_mat_plant_prod_flag('14001832', 'H133');
execute upsert_mat_plant_prod_flag('14001834', 'H133');
execute upsert_mat_plant_prod_flag('14001838', 'H133');
execute upsert_mat_plant_prod_flag('14001841', 'H133');
execute upsert_mat_plant_prod_flag('14001845', 'H133');
execute upsert_mat_plant_prod_flag('14001861', 'H133');
execute upsert_mat_plant_prod_flag('14001880', 'H133');
execute upsert_mat_plant_prod_flag('14002566', 'H133');
execute upsert_mat_plant_prod_flag('14002629', 'H133');
execute upsert_mat_plant_prod_flag('14003112', 'H133');
execute upsert_mat_plant_prod_flag('14003220', 'H133');
execute upsert_mat_plant_prod_flag('14003378', 'H133');
execute upsert_mat_plant_prod_flag('14003411', 'H133');
execute upsert_mat_plant_prod_flag('14003412', 'H133');
execute upsert_mat_plant_prod_flag('14003443', 'H133');
execute upsert_mat_plant_prod_flag('14003566', 'H133');
execute upsert_mat_plant_prod_flag('14001736', 'K102');
execute upsert_mat_plant_prod_flag('14001738', 'K102');
execute upsert_mat_plant_prod_flag('14001741', 'K102');
execute upsert_mat_plant_prod_flag('14001743', 'K102');
execute upsert_mat_plant_prod_flag('14001744', 'K102');
execute upsert_mat_plant_prod_flag('14001745', 'K102');
execute upsert_mat_plant_prod_flag('14001746', 'K102');
execute upsert_mat_plant_prod_flag('14001748', 'K102');
execute upsert_mat_plant_prod_flag('14001749', 'K102');
execute upsert_mat_plant_prod_flag('14001750', 'K102');
execute upsert_mat_plant_prod_flag('14001751', 'K102');
execute upsert_mat_plant_prod_flag('14002061', 'K102');
execute upsert_mat_plant_prod_flag('14002629', 'K102');
execute upsert_mat_plant_prod_flag('14002640', 'K102');
execute upsert_mat_plant_prod_flag('14003220', 'K102');
execute upsert_mat_plant_prod_flag('14003565', 'K102');
execute upsert_mat_plant_prod_flag('14003566', 'K102');
execute upsert_mat_plant_prod_flag('14003837', 'K102');
execute upsert_mat_plant_prod_flag('14003839', 'K102');
execute upsert_mat_plant_prod_flag('14003850', 'K102');
execute upsert_mat_plant_prod_flag('14001687', 'K101');
execute upsert_mat_plant_prod_flag('14001697', 'K101');
execute upsert_mat_plant_prod_flag('14001735', 'K101');
execute upsert_mat_plant_prod_flag('14001736', 'K101');
execute upsert_mat_plant_prod_flag('14001741', 'K101');
execute upsert_mat_plant_prod_flag('14001772', 'K101');
execute upsert_mat_plant_prod_flag('14001815', 'K101');
execute upsert_mat_plant_prod_flag('14001831', 'K101');
execute upsert_mat_plant_prod_flag('14001832', 'K101');
execute upsert_mat_plant_prod_flag('14001838', 'K101');
execute upsert_mat_plant_prod_flag('14001841', 'K101');
execute upsert_mat_plant_prod_flag('14001845', 'K101');
execute upsert_mat_plant_prod_flag('14001861', 'K101');
execute upsert_mat_plant_prod_flag('14001880', 'K101');
execute upsert_mat_plant_prod_flag('14002566', 'K101');
execute upsert_mat_plant_prod_flag('14002568', 'K101');
execute upsert_mat_plant_prod_flag('14002569', 'K101');
execute upsert_mat_plant_prod_flag('14002629', 'K101');
execute upsert_mat_plant_prod_flag('14002732', 'K101');
execute upsert_mat_plant_prod_flag('14003220', 'K101');
execute upsert_mat_plant_prod_flag('14003247', 'K101');
execute upsert_mat_plant_prod_flag('14003256', 'K101');
execute upsert_mat_plant_prod_flag('14003291', 'K101');
execute upsert_mat_plant_prod_flag('14003343', 'K101');
execute upsert_mat_plant_prod_flag('14003407', 'K101');
execute upsert_mat_plant_prod_flag('14003437', 'K101');
execute upsert_mat_plant_prod_flag('14003443', 'K101');
execute upsert_mat_plant_prod_flag('14003770', 'K101');
execute upsert_mat_plant_prod_flag('14003821', 'K101');
execute upsert_mat_plant_prod_flag('14003835', 'K101');
commit;

drop procedure upsert_mat_plant_prod_flag;

-- And view updates from the main script

prompt ************************************************************************
prompt *** END CRHLOGON-1361 Demand planning - Material Production plant
prompt ************************************************************************

