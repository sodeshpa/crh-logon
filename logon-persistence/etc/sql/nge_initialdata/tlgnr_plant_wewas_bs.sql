-- Creating table in order to store wewas codes for NGE plants
-- insert data according to:
-- /share/doc/crh/logon_nge/input/data/WEWAS_SAP_Werke_20100525.txt

prompt Adding entries to table TLGNR_PLANT_WEWAS_BS

insert into tlgnr_plant_wewas_bs (plant_wewas_cd, plant_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('100', '15A0', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_wewas_bs (plant_wewas_cd, plant_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('120', '15A2', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_wewas_bs (plant_wewas_cd, plant_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('200', '15A3', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_wewas_bs (plant_wewas_cd, plant_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('300', '15A9', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_wewas_bs (plant_wewas_cd, plant_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('310', '15AA', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_wewas_bs (plant_wewas_cd, plant_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('330', '15AB', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_wewas_bs (plant_wewas_cd, plant_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('110', '25A0', 1, 1, systimestamp, 1, systimestamp);

insert into tlgnr_plant_wewas_bs (plant_wewas_cd, plant_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('220', '25A1', 1, 1, systimestamp, 1, systimestamp);
-- insert into tlgnr_plant_wewas_bs (plant_wewas_cd, plant_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('220', '25A3', 1, 1, systimestamp, 1, systimestamp); /* cannot add this - duplicate WeWaS */
-- Note, that it would be 220 = (25A1, 25A3), see ticket 57242. The current (26.7.2010) approach is: Do NOT have it (keep the 1 : (0,1) mapping), but instead map 25A3 in condition import to 25A1.

-- Mail 26.7.2010, Guenther Laatsch: Hi Urs, the WeWaS Code for 25A4 is 230.
-- Note uma: This will never be used for LOADING, but only for delivery to it, i.e. it will be associated with a shipto.
insert into tlgnr_plant_wewas_bs (plant_wewas_cd, plant_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('230', '25A4', 1, 1, systimestamp, 1, systimestamp);

insert into tlgnr_plant_wewas_bs (plant_wewas_cd, plant_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('320', '25A5', 1, 1, systimestamp, 1, systimestamp);

-- The following do not have WeWaS codes (no loading with Weighing System but with PDA or manual)
-- FIXME uma 23.8.2010: However, we currently use the existance of a WeWaS code as the criteria to determine whether we should send to SAP the NGE or the CHE way! Obviously, this won't work and needs redesign. New type in plant table? "Send SAP type"?

-- 25B1 Fly-Ash Plant Wedel
-- 25B4 Fly-Ash Plant Rostock
-- 25B6 Fly-Ash Plant Neumünster
-- 25B7 Fly-Ash Plant Kiel
-- 25B9 Fly-Ash Plant Tiefstack
-- 25BB Fly-Ash Plant Büchsenschincken

commit;
