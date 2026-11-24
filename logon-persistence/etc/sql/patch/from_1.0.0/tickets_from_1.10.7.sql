prompt ************************************************************************
prompt *** BEGIN CRHLOGON-1361 Demand planning - Material Production plant
prompt ************************************************************************

prompt Updating tlgnc_mat_plant.default_prod_plant_flag

-- 0 out all previously set values as the patch in "tickets_from_1.10.4" set the flag for multiple plants for the same material
update ulgn01.tlgnc_mat_plant set default_prod_plant_flag = 0;

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

execute upsert_mat_plant_prod_flag('14003112', 'H133');
execute upsert_mat_plant_prod_flag('14003378', 'H133');
execute upsert_mat_plant_prod_flag('14003411', 'H133');
execute upsert_mat_plant_prod_flag('14003412', 'H133');
execute upsert_mat_plant_prod_flag('14001736', 'K102');
execute upsert_mat_plant_prod_flag('14001738', 'K102');
execute upsert_mat_plant_prod_flag('14001741', 'K102');
execute upsert_mat_plant_prod_flag('14001744', 'K102');
execute upsert_mat_plant_prod_flag('14001745', 'K102');
execute upsert_mat_plant_prod_flag('14001746', 'K102');
execute upsert_mat_plant_prod_flag('14001748', 'K102');
execute upsert_mat_plant_prod_flag('14001749', 'K102');
execute upsert_mat_plant_prod_flag('14001750', 'K102');
execute upsert_mat_plant_prod_flag('14002629', 'K102');
execute upsert_mat_plant_prod_flag('14002640', 'K102');
execute upsert_mat_plant_prod_flag('14003565', 'K102');
execute upsert_mat_plant_prod_flag('14003566', 'K102');
execute upsert_mat_plant_prod_flag('14003837', 'K102');
execute upsert_mat_plant_prod_flag('14003839', 'K102');
execute upsert_mat_plant_prod_flag('14003850', 'K102');
execute upsert_mat_plant_prod_flag('14001687', 'K101');
execute upsert_mat_plant_prod_flag('14001697', 'K101');
execute upsert_mat_plant_prod_flag('14001735', 'K101');
execute upsert_mat_plant_prod_flag('14001772', 'K101');
execute upsert_mat_plant_prod_flag('14001815', 'K101');
execute upsert_mat_plant_prod_flag('14001831', 'K101');
execute upsert_mat_plant_prod_flag('14001838', 'K101');
execute upsert_mat_plant_prod_flag('14001841', 'K101');
execute upsert_mat_plant_prod_flag('14001845', 'K101');
execute upsert_mat_plant_prod_flag('14001861', 'K101');
execute upsert_mat_plant_prod_flag('14001880', 'K101');
execute upsert_mat_plant_prod_flag('14002566', 'K101');
execute upsert_mat_plant_prod_flag('14002568', 'K101');
execute upsert_mat_plant_prod_flag('14002569', 'K101');
execute upsert_mat_plant_prod_flag('14002732', 'K101');
execute upsert_mat_plant_prod_flag('14003220', 'K101');
execute upsert_mat_plant_prod_flag('14003256', 'K101');
execute upsert_mat_plant_prod_flag('14003291', 'K101');
execute upsert_mat_plant_prod_flag('14003343', 'K101');
execute upsert_mat_plant_prod_flag('14003407', 'K101');
execute upsert_mat_plant_prod_flag('14003437', 'K101');
execute upsert_mat_plant_prod_flag('14003443', 'K101');
execute upsert_mat_plant_prod_flag('14003821', 'K101');
execute upsert_mat_plant_prod_flag('14003835', 'K101');

commit;

drop procedure upsert_mat_plant_prod_flag;

prompt ************************************************************************
prompt *** END CRHLOGON-1361 Demand planning - Material Production plant
prompt ************************************************************************

