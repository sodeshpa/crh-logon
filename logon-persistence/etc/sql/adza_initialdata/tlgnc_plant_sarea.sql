-- List of plant - sarea associations based on this sheet:
--   /share/doc/crh/logon_2_0_ebm/input/sap/LRQ001.0_Organisational_structure/EBM_template_Values_v8_20110323.pdf

/*
uma 4.8.2011:
Note, this is just an initial shot.
Possibly we have to update, but possibly Crh is updating the usage of plants per sales area in tablebrowser directly.

uma 19.12.2012:
It has become evident, that Crh in fact does NOT care about this ADZA anymore, not even in LQx testenvironments.
-> Please extend ADZA to suit AdNovum needs whenever it is useful!

But please do not modify existing data without carefully checking what sideeffects on testdata & testcases it has.
*/

prompt '---------- adza_initialdata/tlgnc_plant_sarea.sql ----------'

prompt 'INSERTING into TLGNC_PLANT_SAREA'

prompt Plant 8101: Should insert 10 entries: AD02-10-10, AD02-20-10, AD02-10-30, ...
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '8101', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='AD02'
);

prompt Plant 8102: Should insert 2 entries: AD01-10-11, AD01-20-11
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '8102', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='AD01'
);

prompt Plant 8103: Should insert 10 entries: AD02-10-10, AD02-20-10, AD02-10-30, ...
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '8103', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='AD02'
);

prompt Plant 8104: Should insert 4 entries: AD03-10-10, AD03-20-10, AD03-10-10, AD03-20-10.
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '8104', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='AD03'
);

prompt Plant 8105: Should insert 4 entries: AD03-10-10, AD03-20-10, AD03-10-10, AD03-20-10.
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '8105', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='AD03'
);

prompt Plant 8201: Should insert 4 entries: AD03-10-10, AD03-20-10, AD03-10-10, AD03-20-10.
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '8201', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='AD03'
);

prompt Plant 8301: Should insert 10 entries: AD02-10-10, AD02-20-10, AD02-10-30, ...
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '8301', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='AD02'
);

prompt Plant 8401: Should insert 10 entries: AD02-10-10, AD02-20-10, AD02-10-30, ...
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '8401', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='AD02'
);

prompt Plant 8VAD: Should insert 10 entries: AD02-10-10, AD02-20-10, AD02-10-30, ...
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '8VAD', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='AD02'
);

prompt "INSERTING into TLGNC_PLANT_SAREA - South Africa"

prompt Plant 9101: Should insert 4 entries: ZA01-10-10, ZA01-20-10, ZA01-10-30, ZA01-20-30.
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '9101', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='ZA01'
);

prompt Plant 9301: Should insert 4 entries: ZA01-10-10, ZA01-20-10, ZA01-10-30, ZA01-20-30.
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '9301', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='ZA01'
);

prompt Plant 9401: Should insert 2 entries: ZA02-10-40, ZA01-20-40.
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '9401', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='ZA02'
);

prompt Plant 9801: Should insert 2 entries: ZA03-10-80, ZA03-20-80.
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '9801', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='ZA03'
);

prompt Plant 9VZA: Should insert 4 entries: ZA01-10-10, ZA01-20-10, ZA01-10-30, ZA01-20-30.
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '9VZA', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='ZA01'
);

prompt UNCLEAR: There is a (virtual?) plant QMPL, where ownership is not clear. Maybe that should be useable by all AD+ZA sales areas? Do that for now.

prompt Plant QMPL: Insert in just ALL AD+ZA sales areas: make available globally.
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, 'QMPL', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd in ('AD01', 'AD02', 'AD03', 'ZA01', 'ZA02', 'ZA03')
);

prompt INSERTING into TLGNC_PLANT_SAREA - South Africa plant useable for AD sarea AD02 - to test cross-boundary (ignore real world geography...)

prompt Plant 9101: Should insert 10 entries: AD02-10-10, AD02-20-10, AD02-10-30, ...
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, '9101', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='AD02'
);

prompt Plant C102 for SArea CH90-10-10, CH90-10-99, CH90-20-10, CH90-20-99
insert into tlgnc_plant_sarea ( plant_sarea_id, plant_cd, sarea_id, ctl_cre_uid,ctl_cre_ts, ctl_mod_uid,ctl_mod_ts, ctl_tcn) (
  select slgnc_plant_sarea.nextval, 'C102', sa.sarea_id,
    1,systimestamp,1,systimestamp,0 from tlgnc_sarea sa
  where sa.sorg_cd='CH90'
);

commit;
