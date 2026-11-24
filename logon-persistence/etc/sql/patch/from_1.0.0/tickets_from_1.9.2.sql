prompt ************************************************************************
prompt *** BEGIN CRHLOGON-1340 Demand planning
prompt ************************************************************************

prompt *** Setup Roles and Functions

-- Demand Planning roles
create or replace procedure upsert_func (
	v_func_id                     in number,
	v_func_name                   varchar2,
	v_func_descr                  varchar2
) is
	v_existing                      number; -- number of existing records of that key (0 or 1)
begin
	dbms_output.enable(100000);
	-- A record is identified by v_func_id directly
	select count(func_id) into v_existing
	from tlgnu_func f
	where f.func_id = v_func_id;

	if (v_existing > 0) then
		-- update
		update tlgnu_func set
			/* func_id : is the ID, do not update */
							  func_name   = v_func_name,
							  func_descr  = v_func_descr,
							  ctl_mod_uid = 1,
							  ctl_mod_ts  = systimestamp,
							  ctl_tcn     = ctl_tcn+1
		where
				func_id = v_func_id and ( 1 != 1
			or func_name  != v_func_name
			or func_descr != v_func_descr
			);
		dbms_output.put_line ('tlgnu_func: UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnu_func with id=' || v_func_id || '.');

	else
		insert into tlgnu_func (
			func_id,
			func_name,
			func_descr,
			ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
		) values (
					 v_func_id,
					 v_func_name,
					 v_func_descr,
					 1, systimestamp, 1, systimestamp, 0
				 );
		dbms_output.put_line ('tlgnu_func: INSERTED ' || SQL%ROWCOUNT ||' tlgnu_func with id=' || v_func_id || '; name=' || v_func_name || '.');

	end if;
end upsert_func;
/
show errors;

prompt Insert or Update tlgnu_func 25000: 'Demand Planning'                            , 'Access to the demand planning tab.'
execute upsert_func (25000, 'Demand Planning', 'Access to the demand planning tab.');
prompt Insert or Update tlgnu_func 25001: 'Demand Planning Report Expected Demand'     , 'Access to the Report expected demand tab'
execute upsert_func (25001, 'Demand Planning Report Expected Demand', 'Access to the Report expected demand tab');
prompt Insert or Update tlgnu_func 25002: 'Demand Planning Report Plan vs. Real', 'Access to the Report Planned Demand vs. Realized Orders tab'
execute upsert_func (25002, 'Demand Planning Report Plan vs. Real', 'Access to the Report Planned Demand vs. Realized Orders tab');
prompt Insert or Update tlgnu_func 25010: 'Demand planning admin rights'               , 'Placing demand plans for next week without any limitation. Users not having this right must obey the sales area rules for placing demand plans. See also tlgnc_sarea.demand_plan_latest_until_h.'
execute upsert_func (25010, 'Demand planning admin rights', 'Placing demand plans for next week without any limitation. Users not having this right must obey the sales area rules for placing demand plans. See also tlgnc_sarea.demand_plan_latest_until_h.');
drop procedure upsert_func;
commit;


-- func 25000 = Demand Planning
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  25000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Order Entry         */,  25000);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  12 /* Kunden (Web Order) */,  25000);

-- func 25001 = Demand Planning Expected Demand
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  25001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Order Entry         */,  25001);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  12 /* Kunden (Web Order) */,  25001);

-- func 25002 = Demand Planning Expected vs Realized
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  25002);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Order Entry         */,  25002);

-- func 25010 = Demand Planning Unlimited
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  1 /* LOGON Admin         */,  25010);
insert into tlgnu_role_func (role_func_id, role_id, func_id) values (slgnu_role_func.nextval,  6 /* Order Entry         */,  25010);
commit;

prompt *** Setup Table changes

-- alter table tlgnc_demand_plan add (
create table tlgnc_demand_plan (
	demand_plan_id                   number(12)           not null,
	shipto_id                        number(12)           not null,
	sap_material_id                  number(12)           not null,
	planned_weight_t                 number(12,4)         not null,
	start_date                       date                 not null,
	end_date                         date                 not null,
	ctl_cre_uid                      number(12)           default 1 not null,
	ctl_cre_ts                       timestamp            default systimestamp not null,
	ctl_mod_uid                      number(12)           default 1 not null,
	ctl_mod_ts                       timestamp            default systimestamp not null,
	ctl_tcn                          number(12)           default 0 not null
);

alter table tlgnc_sarea add (
	enable_demand_planning_flag number(1) null,
	demand_plan_latest_until_h number(4) null
);

update tlgnc_sarea set enable_demand_planning_flag = 0;
alter table tlgnc_sarea modify enable_demand_planning_flag number(1) not null;

alter table tlgnc_demand_plan add primary key (demand_plan_id) using index tablespace index_lgn_a;
alter table tlgnc_demand_plan add constraint fk_tlgnc_demand_plan_shipto foreign key (shipto_id) references tlgnc_shipto (shipto_id);
alter table tlgnc_demand_plan add constraint fk_tlgnc_demand_plan_mat foreign key (sap_material_id) references tlgns_sap_material (sap_material_id);
create index i_demand_plan on tlgnc_demand_plan (shipto_id) tablespace index_lgn_a;
create index i_demand_plan_1 on tlgnc_demand_plan (sap_material_id) tablespace index_lgn_a;
create sequence slgnc_demand_plan start with 1000 maxvalue 88888888888;
create sequence sq_tlgnc_demand_plan start with 1000000000 maxvalue 88888888888;

comment on table tlgnc_demand_plan is 'Demand plans for the future';
comment on column tlgnc_demand_plan.demand_plan_id is 'PK';
comment on column tlgnc_demand_plan.shipto_id is 'FK tlgnc_shipto.';
comment on column tlgnc_demand_plan.sap_material_id is 'FK tlgns_sap_material.';
comment on column tlgnc_demand_plan.planned_weight_t is 'The planned demand in the given date-range for the set shipto and material.';
comment on column tlgnc_demand_plan.start_date is 'Start of the planned demand range.';
comment on column tlgnc_demand_plan.end_date is 'Start of the planned demand range.';
comment on column tlgnc_demand_plan.ctl_cre_uid is 'Technical field. The principal name (corresponding field of javax.security.Principal) of the user who created the record.';
comment on column tlgnc_demand_plan.ctl_cre_ts is 'Technical field. The time when the record got created.';
comment on column tlgnc_demand_plan.ctl_mod_uid is 'Technical field. The principal name (corresponding field of javax.security.Principal) of the user who last modified the record.';
comment on column tlgnc_demand_plan.ctl_mod_ts is 'Technical field. The time when the record was last modified.';
comment on column tlgnc_demand_plan.ctl_tcn is 'Technical field. For supporting optimistic locking.';
comment on column tlgnc_sarea.enable_demand_planning_flag is 'Tells whether demand planning functionality is enabled for the sales area.';
comment on column tlgnc_sarea.demand_plan_latest_until_h is 'Tells how many hours before Monday 00:00 on week X can customers issue forecast to that week (e.g. 58 hours in case forecast should be closed by Friday 14:00 week X-1).';

grant select, insert, update, delete on tlgnc_demand_plan to rl_lgn_user;
grant select on slgnc_demand_plan to rl_lgn_user;

insert into tlgnt_table_meta (table_meta_id, table_name, insert_allowed, update_allowed, delete_allowed, referenced_field, use_dropdown, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn) values (slgnt_table_meta.nextval, 'TLGNC_DEMAND_PLAN', 1, 1, 1, null, 0, 1, systimestamp, 1, systimestamp, 0);

prompt ************************************************************************
prompt *** END CRHLOGON-1340 Demand planning
prompt ************************************************************************