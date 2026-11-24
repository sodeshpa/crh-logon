-- based on ticket ADN71680

prompt '---------- adza_initialdata/tlgnr_sgroup_bs.sql ----------'

prompt INSERTING into TLGNR_SGROUP_BS

insert into tlgnr_sgroup_bs (sgroup_cd, sgroup_name, ctl_act_cd, email_addresses, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) 
values ('AD0','Sales group AD', 1, null, 1, systimestamp, 1, systimestamp);
insert into tlgnr_sgroup_bs (sgroup_cd, sgroup_name, ctl_act_cd, email_addresses, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) 
values ('ZA0','Sales group ZA', 1, null, 1, systimestamp, 1, systimestamp);

commit;
