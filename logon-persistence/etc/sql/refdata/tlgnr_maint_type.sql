-- $Revision$
-- $Date$
--
-- List is based on European Guidelines for "Revisionsfristen und Instandhaltungsplaene"
-- See spec: /share/doc/crh/pegasus/input/Bahn/Concept_for_wagon_maintenance_rules_V1.0_20091215.xls

prompt '---------- refdata/tlgnr_maint_type.sql ----------'

prompt INSERTING into TLGNR_MAINT_TYPE_BS
insert into tlgnr_maint_type_bs (maint_type_cd, is_repair_flag, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_bs (maint_type_cd, is_repair_flag, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_bs (maint_type_cd, is_repair_flag, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_bs (maint_type_cd, is_repair_flag, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_bs (maint_type_cd, is_repair_flag, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_bs (maint_type_cd, is_repair_flag, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_bs (maint_type_cd, is_repair_flag, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_bs (maint_type_cd, is_repair_flag, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_bs (maint_type_cd, is_repair_flag, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   0, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_bs (maint_type_cd, is_repair_flag, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    0, 1, 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - EN (GERMAN COPY)
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'en', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'en', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'en', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'en', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'en', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'en', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'en', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'en', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'en', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'en', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - DE
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'de', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'de', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'de', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'de', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'de', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'de', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'de', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'de', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'de', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'de', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - FR (GERMAN COPY)
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'fr', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'fr', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'fr', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'fr', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'fr', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'fr', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'fr', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'fr', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'fr', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'fr', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - IT (GERMAN COPY)
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'it', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'it', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'it', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'it', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'it', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'it', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'it', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'it', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'it', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'it', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - ES (GERMAN COPY)
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'es', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'es', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'es', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'es', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'es', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'es', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'es', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'es', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'es', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'es', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - CS (GERMAN COPY)
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'cs', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'cs', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'cs', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'cs', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'cs', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'cs', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'cs', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'cs', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'cs', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'cs', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - SK (GERMAN COPY)
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'sk', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'sk', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'sk', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'sk', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'sk', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'sk', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'sk', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'sk', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'sk', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'sk', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - HU (GERMAN COPY)
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'hu', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'hu', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'hu', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'hu', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'hu', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'hu', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'hu', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'hu', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'hu', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'hu', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - RO (GERMAN COPY)
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'ro', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'ro', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'ro', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'ro', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'ro', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'ro', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'ro', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'ro', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'ro', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'ro', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - BG (GERMAN COPY)
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'bg', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'bg', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'bg', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'bg', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'bg', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'bg', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'bg', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'bg', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'bg', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'bg', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - RU (GERMAN COPY)
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'ru', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'ru', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'ru', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'ru', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'ru', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'ru', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'ru', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'ru', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'ru', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'ru', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - PL (GERMAN COPY)
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'pl', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'pl', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'pl', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'pl', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'pl', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'pl', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'pl', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'pl', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'pl', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'pl', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - AZ (GERMAN COPY)
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'az', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'az', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'az', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'az', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'az', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'az', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'az', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'az', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'az', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'az', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - HR (GERMAN COPY)
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'hr', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'hr', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'hr', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'hr', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'hr', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'hr', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'hr', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'hr', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'hr', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'hr', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - SR (GERMAN COPY)
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'sr', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'sr', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'sr', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'sr', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'sr', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'sr', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'sr', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M',  'sr', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5',   'sr', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C',    'sr', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_MAINT_TYPE_TX - TR
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G1.0', 'tr', 'Ausserpl. Instandsetzung'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G2.0', 'tr', 'Frist an Gueterwagen'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G3.0', 'tr', 'Ausserpl. mob. Instandsetzung'     , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.0', 'tr', 'Revision'                          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.2', 'tr', 'Vereinfachte Revision (6 Jahre)'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.3', 'tr', 'Vereinfachte Revision (1 Jahr)'    , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('G4.8', 'tr', 'Vereinfachte Revision (4/5 Jahre)' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('+3M', 'tr', 'Revisionsfristverlaengerung (3 Mt)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('P5', 'tr', 'Oberbau Revision'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_maint_type_tx (maint_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('C', 'tr', 'Reinigung'                         , 1, systimestamp, 1, systimestamp);

commit;

