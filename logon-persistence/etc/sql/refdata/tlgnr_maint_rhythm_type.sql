-- $Revision$
-- $Date$
--
-- Initial rhythms are according to spec
-- /share/doc/crh/pegasus/input/Bahn/Concept_for_wagon_maintenance_rules_V1.0_20091215.xls

prompt '---------- refdata/tlgnr_maint_rhythm_type.sql ----------'

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_BS

insert into tlgnr_maint_rhythm_type_bs (maint_rhythm_type_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_rhythm_type_bs (maint_rhythm_type_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_rhythm_type_bs (maint_rhythm_type_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_rhythm_type_bs (maint_rhythm_type_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 1, 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_bs (maint_rhythm_type_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_rhythm_type_bs (maint_rhythm_type_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 1, 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_bs (maint_rhythm_type_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 1, 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_bs (maint_rhythm_type_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (7, 1, 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - DE
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'de', 'manuell'         , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'de', '12-4-4 Rhythmus' , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'de', '12-6 RhythmusC'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'de', '12-6 Rhythmus'   , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'de', 'IHP 400 4 C'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'de', 'IHP 300 C'   , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'de', 'IHP 200 Uacs'   , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (7, 'de', 'IHP 100 F+H'   , 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - EN
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'en', 'manual'          , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'en', '12-4-4 Rhythm'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'en', '12-6 RhythmC'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'en', '12-6 Rhythm'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'en', 'IHP 400 4 C'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'en', 'IHP 300 C'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'en', 'IHP 200 Uacs'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (7, 'en', 'IHP 100 F+H'     , 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - FR
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'fr', 'manuel'          , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'fr', '12-4-4 Rythme'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'fr', '12-6 RythmeC'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'fr', '12-6 Rythme'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'fr', 'IHP 400 4 C'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'fr', 'IHP 300 C'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'fr', 'IHP 200 Uacs'     , 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - IT
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'it', 'manuale'          , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'it', '12-4-4 Cadenza'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'it', '12-6 CadenzaC'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'it', '12-6 Cadenza'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'it', 'IHP 400 4 C'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'it', 'IHP 300 C'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'it', 'IHP 200 Uacs'     , 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - ES
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'es', 'manual'          , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'es', '12-4-4 Ritmo'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'es', '12-6 RitmoC'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'es', '12-6 Ritmo'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'es', 'IHP 400 4 C'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'es', 'IHP 300 C'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'es', 'IHP 200 Uacs'     , 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - CS
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'cs', 'manual'          , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'cs', '12-4-4 Rhythm'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'cs', '12-6 RhythmC'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'cs', '12-6 Rhythm'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'cs', 'IHP 400 4 C'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'cs', 'IHP 300 C'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'cs', 'IHP 200 Uacs'     , 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - SK
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'sk', 'manuál'          , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'sk', '12-4-4 Rytmus'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'sk', '12-6 RytmusC'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'sk', '12-6 Rytmus'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'sk', 'IHP 400 4 C'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'sk', 'IHP 300 C'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'sk', 'IHP 200 Uacs'     , 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - HU
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'hu', 'manuális'          , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'hu', '12-4-4 Ütemes'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'hu', '12-6 ÜtemesC'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'hu', '12-6 Ütemes'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'hu', 'IHP 400 4 C'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'hu', 'IHP 300 C'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'hu', 'IHP 200 Uacs'     , 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - RO
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'ro', 'manual'          , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'ro', '12-4-4 Ritm'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'ro', '12-6 RitmC'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'ro', '12-6 Ritm'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'ro', 'IHP 400 4 C'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'ro', 'IHP 300 C'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'ro', 'IHP 200 Uacs'     , 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - BG
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'bg', 'Наръчник'          , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'bg', '12-4-4 Ритъм'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'bg', '12-6 РитъмC'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'bg', '12-6 Ритъм'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'bg', 'IHP 400 4 C'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'bg', 'IHP 300 C'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'bg', 'IHP 200 Uacs'     , 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - RU
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'ru', 'manual'          , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'ru', '12-4-4 Rhythm'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'ru', '12-6 RhythmC'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'ru', '12-6 Rhythm'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'ru', 'IHP 400 4 C'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'ru', 'IHP 300 C'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'ru', 'IHP 200 Uacs'     , 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - PL
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'pl', 'ręczny'          , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'pl', '12-4-4 Rytm'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'pl', '12-6 RytmC'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'pl', '12-6 Rytm'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'pl', 'IHP 400 4 C'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'pl', 'IHP 300 C'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'pl', 'IHP 200 Uacs'     , 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - AZ
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'az', 'manual'          , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'az', '12-4-4 Rhythm'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'az', '12-6 RhythmC'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'az', '12-6 Rhythm'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'az', 'IHP 400 4 C'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'az', 'IHP 300 C'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'az', 'IHP 200 Uacs'     , 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - HR
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'hr', 'manual'          , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'hr', '12-4-4 Mjera'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'hr', '12-6 MjeraC'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'hr', '12-6 Mjera'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'hr', 'IHP 400 4 C'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'hr', 'IHP 300 C'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'hr', 'IHP 200 Uacs'     , 1001, systimestamp, 1001, systimestamp);

prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - SR
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'sr', 'ručni'          , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'sr', '12-4-4 Ritam'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'sr', '12-6 RitamC'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'sr', '12-6 Ritam'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'sr', 'IHP 400 4 C'     , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'sr', 'IHP 300 C'     , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'sr', 'IHP 200 Uacs'     , 1001, systimestamp, 1001, systimestamp);

-- fr/it: Are generated by the insert_refdata_in_all_languages.sql
prompt INSERTING into TLGNR_MAINT_RHYTHM_TYPE_TX - TR
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'tr', 'manuell'         , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'tr', '12-4-4 Rhythmus' , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'tr', '12-6 RhythmusC'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'tr', '12-6 Rhythmus'   , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'tr', 'IHP 400 4 C'   , 1, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'tr', 'IHP 300 C'   , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'tr', 'IHP 200 Uacs'   , 1001, systimestamp, 1001, systimestamp);
insert into tlgnr_maint_rhythm_type_tx (maint_rhythm_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (7, 'tr', 'IHP 100 F+H'   , 1001, systimestamp, 1001, systimestamp);

commit;

