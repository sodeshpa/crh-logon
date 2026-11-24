-- $Revision$
-- $Date$
--
-- Values based on LOGON CHE, i.e. 2009: /share/doc/crh/pegasus/input/Sack/SAP_Transport_Zones_LOGON.xls
-- 4.6.2012: Included those as initialdata for cluster 1, "asis".

prompt '--------- cl1_initialdata/tlgnr_trsp_zone.sql ----------'

prompt INSERTING into TLGNR_TRSP_ZONE_BS
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMZENTD1', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMZENTD2', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMZENTD3', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMSHSGA1', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMSHSGA2', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMBERNHP', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMGLARE1', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMGLARE2', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMGLAREP', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMVALAH1', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMVALAHP', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMNEUEGP', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMAGBSBP', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMSARGC1', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMSARGC2', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMSARGC3', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMSARGCP', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMENGAF1', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('CCEMENGAF2', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('DE-A00', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('IT-A00', 1, 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TRSP_ZONE_TX
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD1', 'en', 'Zentral CH Zone D1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD2', 'en', 'Zentral CH Zone D2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD3', 'en', 'Zentral CH Zone D3' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSHSGA1', 'en', 'Z-SH-SG Zone A1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSHSGA2', 'en', 'Z-SH-SG Zone A2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMBERNHP', 'en', 'Bern Zone H' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLARE1', 'en', 'Glarus Zone E1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLARE2', 'en', 'Glarus Zone E2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLAREP', 'en', 'Glarus Zone E1 E2+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMVALAH1', 'en', 'Valais Zone H1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMVALAHP', 'en', 'Valais Zone H1+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMNEUEGP', 'en', 'Neuenburg Zone G' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMAGBSBP', 'en', 'AG BS Zone B' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC1', 'en', 'Sargans Zone C1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC2', 'en', 'Sargans Zone C2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC3', 'en', 'Sargans Zone C3' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGCP', 'en', 'Sargans Zone C1+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMENGAF1', 'en', 'Engadin Zone F1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMENGAF2', 'en', 'Engadin Zone F2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('DE-A00', 'en', 'NGE SGE Zone' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('IT-A00', 'en', 'ITA Zone' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD1', 'de', 'Zentral CH Zone D1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD2', 'de', 'Zentral CH Zone D2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD3', 'de', 'Zentral CH Zone D3' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSHSGA1', 'de', 'Z-SH-SG Zone A1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSHSGA2', 'de', 'Z-SH-SG Zone A2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMBERNHP', 'de', 'Bern Zone H' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLARE1', 'de', 'Glarus Zone E1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLARE2', 'de', 'Glarus Zone E2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLAREP', 'de', 'Glarus Zone E1 E2+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMVALAH1', 'de', 'Valais Zone H1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMVALAHP', 'de', 'Valais Zone H1+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMNEUEGP', 'de', 'Neuenburg Zone G' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMAGBSBP', 'de', 'AG BS Zone B' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC1', 'de', 'Sargans Zone C1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC2', 'de', 'Sargans Zone C2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC3', 'de', 'Sargans Zone C3' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGCP', 'de', 'Sargans Zone C1+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMENGAF1', 'de', 'Engadin Zone F1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMENGAF2', 'de', 'Engadin Zone F2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('DE-A00', 'de', 'NGE SGE Zone' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('IT-A00', 'de', 'ITA Zone' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD1', 'it', 'Zentral CH Zone D1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD2', 'it', 'Zentral CH Zone D2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD3', 'it', 'Zentral CH Zone D3' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSHSGA1', 'it', 'Z-SH-SG Zone A1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSHSGA2', 'it', 'Z-SH-SG Zone A2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMBERNHP', 'it', 'Bern Zone H' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLARE1', 'it', 'Glarus Zone E1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLARE2', 'it', 'Glarus Zone E2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLAREP', 'it', 'Glarus Zone E1 E2+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMVALAH1', 'it', 'Valais Zone H1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMVALAHP', 'it', 'Valais Zone H1+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMNEUEGP', 'it', 'Neuenburg Zone G' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMAGBSBP', 'it', 'AG BS Zone B' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC1', 'it', 'Sargans Zone C1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC2', 'it', 'Sargans Zone C2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC3', 'it', 'Sargans Zone C3' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGCP', 'it', 'Sargans Zone C1+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMENGAF1', 'it', 'Engadin Zone F1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMENGAF2', 'it', 'Engadin Zone F2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('DE-A00', 'it', 'NGE SGE Zone' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('IT-A00', 'it', 'ITA Zone' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD1', 'fr', 'Zentral CH Zone D1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD2', 'fr', 'Zentral CH Zone D2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD3', 'fr', 'Zentral CH Zone D3' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSHSGA1', 'fr', 'Z-SH-SG Zone A1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSHSGA2', 'fr', 'Z-SH-SG Zone A2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMBERNHP', 'fr', 'Bern Zone H' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLARE1', 'fr', 'Glarus Zone E1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLARE2', 'fr', 'Glarus Zone E2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLAREP', 'fr', 'Glarus Zone E1 E2+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMVALAH1', 'fr', 'Valais Zone H1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMVALAHP', 'fr', 'Valais Zone H1+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMNEUEGP', 'fr', 'Neuenburg Zone G' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMAGBSBP', 'fr', 'AG BS Zone B' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC1', 'fr', 'Sargans Zone C1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC2', 'fr', 'Sargans Zone C2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC3', 'fr', 'Sargans Zone C3' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGCP', 'fr', 'Sargans Zone C1+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMENGAF1', 'fr', 'Engadin Zone F1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMENGAF2', 'fr', 'Engadin Zone F2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('DE-A00', 'fr', 'NGE SGE Zone' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('IT-A00', 'fr', 'ITA Zone' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD1', 'es', 'Zentral CH Zone D1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD2', 'es', 'Zentral CH Zone D2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMZENTD3', 'es', 'Zentral CH Zone D3' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSHSGA1', 'es', 'Z-SH-SG Zone A1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSHSGA2', 'es', 'Z-SH-SG Zone A2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMBERNHP', 'es', 'Bern Zone H' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLARE1', 'es', 'Glarus Zone E1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLARE2', 'es', 'Glarus Zone E2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMGLAREP', 'es', 'Glarus Zone E1 E2+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMVALAH1', 'es', 'Valais Zone H1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMVALAHP', 'es', 'Valais Zone H1+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMNEUEGP', 'es', 'Neuenburg Zone G' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMAGBSBP', 'es', 'AG BS Zone B' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC1', 'es', 'Sargans Zone C1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC2', 'es', 'Sargans Zone C2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGC3', 'es', 'Sargans Zone C3' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMSARGCP', 'es', 'Sargans Zone C1+' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMENGAF1', 'es', 'Engadin Zone F1' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('CCEMENGAF2', 'es', 'Engadin Zone F2' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('DE-A00', 'es', 'NGE SGE Zone' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('IT-A00', 'es', 'ITA Zone' ,1, systimestamp, 1, systimestamp);


commit;