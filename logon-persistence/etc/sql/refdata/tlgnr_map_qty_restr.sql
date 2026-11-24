-- $Revision$
-- $Date$
--
prompt '---------- refdata/tlgnr_map_qty_restr.sql ----------'

INSERT INTO tlgnr_map_qty_restr_bs (sap_contract_type_cd, map_qty_restr_id, augru, quantity_control_flag, advance_payment_flag, ctl_act_cd) (SELECT sap_contract_type_cd, SLGNR_MAP_QTY_RESTR_BS.nextval, 'Z01', '1', '0', '1' FROM tlgnr_sap_contract_type_bs);
INSERT INTO tlgnr_map_qty_restr_bs (sap_contract_type_cd, map_qty_restr_id, augru, quantity_control_flag, advance_payment_flag, ctl_act_cd) (SELECT sap_contract_type_cd, SLGNR_MAP_QTY_RESTR_BS.nextval, '105', '1', '0', '1' FROM tlgnr_sap_contract_type_bs);
INSERT INTO tlgnr_map_qty_restr_bs (sap_contract_type_cd, map_qty_restr_id, augru, quantity_control_flag, advance_payment_flag, ctl_act_cd) (SELECT sap_contract_type_cd, SLGNR_MAP_QTY_RESTR_BS.nextval, '106', '1', '0', '1' FROM tlgnr_sap_contract_type_bs);
INSERT INTO tlgnr_map_qty_restr_bs (sap_contract_type_cd, map_qty_restr_id, augru, quantity_control_flag, advance_payment_flag, ctl_act_cd) VALUES ('ZADV', SLGNR_MAP_QTY_RESTR_BS.nextval, 'Z02', '1', '1', '1');
INSERT INTO tlgnr_map_qty_restr_bs (sap_contract_type_cd, map_qty_restr_id, augru, quantity_control_flag, advance_payment_flag, ctl_act_cd) VALUES ('ZADV', SLGNR_MAP_QTY_RESTR_BS.nextval, NULL, '1', '1', '1');

commit;