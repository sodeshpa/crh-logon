/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

------------------------------------------------------------------------
-- Update planning material references for sales areas                --
-- * inserts for for all sarea (sorg_cd, distribution_channel pair)   --
-- ** one landfill and one aggregate planning material association    --
------------------------------------------------------------------------
create or replace procedure update_planning_mat_sarea is
    pragma autonomous_transaction;
begin
  insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) 
  select SLGNS_SAP_MATERIAL_SAREA.nextval id, planning_material_id, '0', null, sorg_cd, distribution_channel, 0 from
  (
    select max(sap_material_id) planning_material_id, sarea.sorg_cd sorg_cd, sarea.distribution_channel distribution_channel
    from tlgnc_sarea sarea
    join tlgns_sap_material on sap_material_no='LOGON00001'
    where (sarea.sorg_cd, sarea.distribution_channel) not in
    (
      select matsarea.sorg_cd, matsarea.distribution_channel 
      from tlgns_sap_material_sarea matsarea
      join tlgns_sap_material mat on mat.sap_material_id = matsarea.sap_material_id
      where mat.sap_material_no='LOGON00001'
    )
    group by sarea.sorg_cd, sarea.distribution_channel
    order by sarea.sorg_cd, sarea.distribution_channel
  );
    
  insert into tlgns_sap_material_sarea (sap_material_sarea_id, sap_material_id, marked_for_deletion, sales_uom_cd, sorg_cd, distribution_channel, is_generic_flag) 
  select SLGNS_SAP_MATERIAL_SAREA.nextval id, planning_material_id, '0', null, sorg_cd, distribution_channel, 0 from
  (
    select max(sap_material_id) planning_material_id, sarea.sorg_cd sorg_cd, sarea.distribution_channel distribution_channel
    from tlgnc_sarea sarea
    join tlgns_sap_material on sap_material_no='LOGON00002'
    where (sarea.sorg_cd, sarea.distribution_channel) not in
    (
      select matsarea.sorg_cd, matsarea.distribution_channel 
      from tlgns_sap_material_sarea matsarea
      join tlgns_sap_material mat on mat.sap_material_id = matsarea.sap_material_id
      where mat.sap_material_no='LOGON00002'
    )
    group by sarea.sorg_cd, sarea.distribution_channel
    order by sarea.sorg_cd, sarea.distribution_channel
  );
  commit;
end;
/

show errors;
