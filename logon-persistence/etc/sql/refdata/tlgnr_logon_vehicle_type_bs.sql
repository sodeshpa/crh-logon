-- $Revision$
-- $Date$
--
-- logon vehicle types, required by tlgnr_sap_vehicle_type in order to do the mapping to a) truck or b) trailer

prompt '---------- refdata/tlgnr_logon_vehicle_type_bs.sql ----------'

prompt INSERTING into TLGNR_LOGON_VEHICLE_TYPE_BS

--Truck
insert into tlgnr_logon_vehicle_type_bs (logon_vehicle_type_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 1, 1, sysdate, 1, sysdate);

--Trailer
insert into tlgnr_logon_vehicle_type_bs (logon_vehicle_type_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 1, 1, sysdate, 1, sysdate);

commit;
