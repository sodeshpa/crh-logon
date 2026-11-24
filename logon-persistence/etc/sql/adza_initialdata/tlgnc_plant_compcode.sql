-- List of plant - compcode associations based on this sheet:
--   /share/doc/crh/logon_2_0_ebm/input/sap/LRQ001.0_Organisational_structure/EBM_template_Values_v8_20110323.pdf

/*
uma 4.8.2011:

On that graphic, AD02 seems to be the RELEVANT Company code => This is also the one that I set to be the owner of "8VAD"
In ZA, ZA01 seems to be more important though => set as owner of 9VZA

The plant 'QMPL', 'Virtual QMPlant' however does NOT seem to be with either ZA or AD company code => not assiated at all here.

Note, this is just a guess, and needs to be checked/confirmed during ITC tests with Crh.
*/

prompt '---------- adza_initialdata/tlgnc_plant_compcode.sql ----------'

prompt 'INSERTING into TLGNC_PLANT_COMPCODE'

insert into tlgnc_plant_compcode (plant_compcode_id, plant_cd, companycode_cd, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) values (slgnc_plant_compcode.nextval, '8101', 'AD02', 1,systimestamp,1,systimestamp,0);
insert into tlgnc_plant_compcode (plant_compcode_id, plant_cd, companycode_cd, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) values (slgnc_plant_compcode.nextval, '8102', 'AD01', 1,systimestamp,1,systimestamp,0);
insert into tlgnc_plant_compcode (plant_compcode_id, plant_cd, companycode_cd, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) values (slgnc_plant_compcode.nextval, '8103', 'AD02', 1,systimestamp,1,systimestamp,0);
insert into tlgnc_plant_compcode (plant_compcode_id, plant_cd, companycode_cd, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) values (slgnc_plant_compcode.nextval, '8104', 'AD03', 1,systimestamp,1,systimestamp,0);
insert into tlgnc_plant_compcode (plant_compcode_id, plant_cd, companycode_cd, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) values (slgnc_plant_compcode.nextval, '8201', 'AD03', 1,systimestamp,1,systimestamp,0);
insert into tlgnc_plant_compcode (plant_compcode_id, plant_cd, companycode_cd, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) values (slgnc_plant_compcode.nextval, '8301', 'AD02', 1,systimestamp,1,systimestamp,0);
insert into tlgnc_plant_compcode (plant_compcode_id, plant_cd, companycode_cd, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) values (slgnc_plant_compcode.nextval, '8401', 'AD02', 1,systimestamp,1,systimestamp,0);
insert into tlgnc_plant_compcode (plant_compcode_id, plant_cd, companycode_cd, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) values (slgnc_plant_compcode.nextval, '8VAD', 'AD02', 1,systimestamp,1,systimestamp,0);
insert into tlgnc_plant_compcode (plant_compcode_id, plant_cd, companycode_cd, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) values (slgnc_plant_compcode.nextval, '9101', 'ZA01', 1,systimestamp,1,systimestamp,0);
insert into tlgnc_plant_compcode (plant_compcode_id, plant_cd, companycode_cd, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) values (slgnc_plant_compcode.nextval, '9301', 'ZA01', 1,systimestamp,1,systimestamp,0);
insert into tlgnc_plant_compcode (plant_compcode_id, plant_cd, companycode_cd, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) values (slgnc_plant_compcode.nextval, '9401', 'ZA02', 1,systimestamp,1,systimestamp,0);
insert into tlgnc_plant_compcode (plant_compcode_id, plant_cd, companycode_cd, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) values (slgnc_plant_compcode.nextval, '9801', 'ZA03', 1,systimestamp,1,systimestamp,0);
insert into tlgnc_plant_compcode (plant_compcode_id, plant_cd, companycode_cd, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) values (slgnc_plant_compcode.nextval, '9VZA', 'ZA01', 1,systimestamp,1,systimestamp,0);

commit;
