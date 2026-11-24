/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

--------------------------------------
-- Upsert planning contract items --
--------------------------------------
-- Stored procedure for following the changes of tlgnc_sarea.allow_planning_material_flag 

create or replace procedure upsert_planning_contract_items(i_sales_area_id in number default null) is
  l_log_text tlgnc_log.log_text%type;
  l_inserted_contract_item_id number(12,0);
  c_aggregate_planning_item constant number := -3;
  c_landfill_planning_item constant number := -2;
  c_aggregate_planning_material constant number := 2;
  c_landfill_planning_material constant number := 1;
  c_sales_area_not_specified constant number := -1;
  c_no_transport_cost_included constant number := 0;
  c_not_ap_group_leader constant number := 0;
  c_not_marked_for_deletion constant char(1) := '0';
begin
  logon_log.g_min_loglevel := 'INFO';
  for sap_contract_to_update in (
    select 
      sales_area.allow_planning_material_flag,
      sap_contract.sap_contract_id, 
      aggregate_planning_item.sap_contract_item_id agg_planning_item_id,
      coalesce(aggregate_planning_item.marked_for_deletion,'0') agg_planning_item_deleted,
      landfill_planning_item.sap_contract_item_id landfill_planning_item_id, 
      coalesce(landfill_planning_item.marked_for_deletion,'0') landfill_planning_item_deleted
    from 
      tlgns_sap_contract sap_contract
    join 
      tlgnc_sarea sales_area
      on (sap_contract.sarea_id = sales_area.sarea_id)
    left outer join 
      tlgns_sap_contract_item landfill_planning_item
      on (landfill_planning_item.sap_contract_id = sap_contract.sap_contract_id
        and landfill_planning_item.contract_item_no = c_landfill_planning_item)
    left outer join 
      tlgns_sap_contract_item aggregate_planning_item 
      on (aggregate_planning_item.sap_contract_id = sap_contract.sap_contract_id
        and aggregate_planning_item.contract_item_no = c_aggregate_planning_item)
    where 
      (
        -- landfill or aggregate planning contract item is missing
        (
          sales_area.allow_planning_material_flag = 1 and
          (aggregate_planning_item.sap_contract_item_id is null or 
          landfill_planning_item.sap_contract_item_id is null)
        ) or  
        -- aggregate planning contract item has invalid marked for deletion flag
        (
          aggregate_planning_item.sap_contract_item_id is not null and  
          coalesce(aggregate_planning_item.marked_for_deletion, c_not_marked_for_deletion)
              = sales_area.allow_planning_material_flag
        ) or
        -- landfill planning contract item has invalid marked for deletion flag
        (
          landfill_planning_item.sap_contract_item_id is not null and
          coalesce(landfill_planning_item.marked_for_deletion, c_not_marked_for_deletion)
              = sales_area.allow_planning_material_flag
        )
      )
      and
      (coalesce(i_sales_area_id, c_sales_area_not_specified) in (sales_area.sarea_id, c_sales_area_not_specified))
  ) loop
    -- switch marked for deletion flag on aggregate planning contract item
    if sap_contract_to_update.agg_planning_item_id is not null and 
           sap_contract_to_update.allow_planning_material_flag = sap_contract_to_update.agg_planning_item_deleted then
        update tlgns_sap_contract_item 
        set 
          marked_for_deletion = 1 - sap_contract_to_update.allow_planning_material_flag,
          ctl_tcn=ctl_tcn + 1, 
          ctl_mod_uid = 1, 
          ctl_mod_ts = systimestamp 
        where 
          sap_contract_item_id = sap_contract_to_update.agg_planning_item_id;
      l_log_text := 'Contract id: ' || sap_contract_to_update.sap_contract_id;
      l_log_text := l_log_text || ' | ' || 
        'Contract item id: ' || sap_contract_to_update.agg_planning_item_id;
      l_log_text := l_log_text || ' | ' || 
        'Rows updated: ' || sql%rowcount;
      logon_log.info('upsert planning contract items / switch marked for deletion flag on aggregate planning contract item', l_log_text);
    end if;
    -- switch marked for deletion flag on landfill planning contract item
    if sap_contract_to_update.landfill_planning_item_id is not null and
        sap_contract_to_update.allow_planning_material_flag = sap_contract_to_update.landfill_planning_item_deleted then
      update tlgns_sap_contract_item 
      set 
        marked_for_deletion = 1 - sap_contract_to_update.allow_planning_material_flag,
        ctl_tcn=ctl_tcn + 1, 
        ctl_mod_uid = 1, 
        ctl_mod_ts = systimestamp 
      where sap_contract_item_id = sap_contract_to_update.landfill_planning_item_id;
      l_log_text := 'Contract id: ' || sap_contract_to_update.sap_contract_id;
      l_log_text := l_log_text || ' | ' || 
        'Contract item id: ' || sap_contract_to_update.landfill_planning_item_id;
      l_log_text := l_log_text || ' | ' ||
        'Rows updated: ' || sql%rowcount;
      logon_log.info('upsert planning contract items / switch marked for deletion flag on landfill planning contract item', l_log_text);
    end if;
    -- create aggregate planning contract item
    if sap_contract_to_update.agg_planning_item_id is null then
      insert into tlgns_sap_contract_item
      (sap_contract_item_id           , sap_contract_id                        , contract_item_no         , sap_material_id              , transport_cost_included_flag, is_ap_group_leader   , marked_for_deletion      ) values
      (slgns_sap_contract_item.nextval, sap_contract_to_update.sap_contract_id , c_aggregate_planning_item, c_aggregate_planning_material, c_no_transport_cost_included, c_not_ap_group_leader, c_not_marked_for_deletion);
      l_log_text := 'Contract id: ' || sap_contract_to_update.sap_contract_id;
      select slgns_sap_contract_item.currval
        into l_inserted_contract_item_id
      from dual;
      l_log_text := l_log_text || ' | ' || 
        'Contract item id: ' || l_inserted_contract_item_id;
      l_log_text := l_log_text || ' | ' || 
        'Rows inserted: ' || sql%rowcount;
      logon_log.info('upsert planning contract items / create aggregate planning contract item', l_log_text);
    end if;
    -- create landfill planning contract item
    if sap_contract_to_update.landfill_planning_item_id is null then
      insert into tlgns_sap_contract_item
      (sap_contract_item_id           , sap_contract_id                      , contract_item_no        , sap_material_id             , transport_cost_included_flag, is_ap_group_leader   , marked_for_deletion      ) values
      (slgns_sap_contract_item.nextval,sap_contract_to_update.sap_contract_id, c_landfill_planning_item, c_landfill_planning_material, c_no_transport_cost_included, c_not_ap_group_leader, c_not_marked_for_deletion);
      l_log_text := 'Contract id: ' || sap_contract_to_update.sap_contract_id;
      select slgns_sap_contract_item.currval
        into l_inserted_contract_item_id
      from dual;
      l_log_text := l_log_text || ' | ' || 
        'Contract item id: ' || l_inserted_contract_item_id;
      l_log_text := l_log_text || ' | ' || 
      'Rows inserted: ' || sql%rowcount;
      logon_log.info('upsert planning contract items / create landfill planning contract item', l_log_text);
    end if;
  end loop;
end;
/

show errors;
