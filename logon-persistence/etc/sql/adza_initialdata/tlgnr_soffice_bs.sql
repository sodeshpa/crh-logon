-- based on ticket ADN71680

prompt '---------- adza_initialdata/tlgnr_soffice_bs.sql ----------'

prompt INSERTING into TLGNR_SOFFICE_BS

insert into tlgnr_soffice_bs (soffice_cd, soffice_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) 
values ('AD00', 'Sales office AD', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_soffice_bs (soffice_cd, soffice_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) 
values ('ZA00', 'Sales office ZA', 1, 1, systimestamp, 1, systimestamp);

commit;
