-- based on ticket ADN74269
-- TODO

prompt '---------- adza_initialdata/tlgnr_plant_track.sql ----------'

prompt INSERTING into TLGNR_PLANT_TRACK_BS
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, '8101', 'AD 8101 Track', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, '8104', 'AD 8104 Track', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, '8401', 'AD 8401 Track', 1, 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - EN
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'en', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'en', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'en', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - DE
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'de', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'de', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'de', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - FR
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'fr', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'fr', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'fr', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - IT
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'it', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'it', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'it', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - ES
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'es', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'es', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'es', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - CS
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'cs', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'cs', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'cs', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - SK
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'sk', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'sk', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'sk', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - HU
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'hu', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'hu', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'hu', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - RO
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'ro', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'ro', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'ro', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - BG
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'bg', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'bg', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'bg', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - RU
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'ru', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'ru', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'ru', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - PL
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'pl', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'pl', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'pl', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - AZ
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'az', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'az', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'az', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - HR
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'hr', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'hr', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'hr', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - SR
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'sr', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'sr', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'sr', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - TR
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -100, 'tr', 'AD 8101 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -101, 'tr', 'AD 8104 Track', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( -102, 'tr', 'AD 8401 Track', 1, systimestamp, 1, systimestamp);

commit;

