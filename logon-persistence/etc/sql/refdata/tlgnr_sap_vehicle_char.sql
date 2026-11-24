-- $Revision$
-- $Date$
--
-- initial data from
-- file:///share/doc/crh/logon_2_0_ebm/spec/biz/CR_Group_1/CR_Group1_Vehicle_Master_Data_Adminsitration.pdf

prompt '---------- refdata/tlgnr_sap_vehicle_char.sql ----------'

prompt INSERTING into TLGNR_SAP_VEHICLE_CHAR_BS

-- individual
insert into tlgnr_sap_vehicle_char_bs (
	sap_vehicle_char_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
) values (
	0, 1, 1, sysdate, 1, sysdate
);

-- generic
insert into tlgnr_sap_vehicle_char_bs (
	sap_vehicle_char_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
) values (
	1, 1, 1, sysdate, 1, sysdate
);

-- real
insert into tlgnr_sap_vehicle_char_bs (
	sap_vehicle_char_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
) values (
	2, 1, 1, sysdate, 1, sysdate
);

commit;