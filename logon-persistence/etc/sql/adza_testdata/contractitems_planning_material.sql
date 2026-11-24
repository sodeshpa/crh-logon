prompt adding contract items to sales areas where allow_planning_material is true

insert into tlgns_sap_contract_item 
(                  sap_contract_item_id,          sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) 
(select slgns_sap_contract_item.nextval, contract.sap_contract_id,               -2,               1,                            0, 0
  from tlgns_sap_contract contract
  join tlgnc_sarea sarea on (sarea.sarea_id=contract.sarea_id
    and sarea.allow_planning_material_flag=1));

insert into tlgns_sap_contract_item 
(                  sap_contract_item_id,          sap_contract_id, contract_item_no, sap_material_id, transport_cost_included_flag, is_ap_group_leader) 
(select slgns_sap_contract_item.nextval, contract.sap_contract_id,               -3,               2,                            0, 0
  from tlgns_sap_contract contract
  join tlgnc_sarea sarea on (sarea.sarea_id=contract.sarea_id
    and sarea.allow_planning_material_flag=1));

commit;
