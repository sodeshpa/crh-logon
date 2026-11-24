-- $Revision$
-- $Date$
--
-- Origin:
--   /share/doc/crh/pegasus/input/Bahn/RailcarMasterData_V0.5.5_20090807.xls
--   Tab "Track at Plant", includes also a picture of "Untervaz"
--
-- Explanation in E-Mail from Christoph, 24.9.2009:
--
-- In Untervaz gibt es Geleise welche für Normalspur und Schmalspur
-- gleichzeitig benutzt werden. Auch die Einfahrt und ausfahrt ins Werk hat 3
-- Geleise. Gewisse Geleise sind auch ausserhalb des Werks (Bahnhof) oder als
-- Spezialfall sogar in Buchs.
--
-- Die Geleise dem Werk (und nicht dem ShippingPoint oder Bahnhof) zuzuweisen
-- ist von mir aus gesehen flexibler (Jeder Shipping Point bestimmt ja das
-- Werk). Zusätzlich muss jeder Verladestelle noch genau ein Gleis zugeordnet
-- werden (damit bei der Bahnwagenanmeldung bekannt ist, auf welchem Geleise
-- der Bahnwagen steht).
-- Ein bisschen mehr Erklärung zum (kompliziertesten) Fall Untervaz zur Info:
-- - Insgesamt 3 Ladestellen (LS) für Losezement, davon
--   -> 2 LS (Gleis H4, H6) nur Normalspur (SBB) => Shipping Point "Rail UV SBB"
--   -> 1 LS (Gleis H2) mit Dreschienengleis (wird aber ausschliesslich nur
--      noch für RhB benutzt, da neue Wagen zu hoch) => Shipping Point "Rail UV RhB"
-- - Eine Rampe (Gleis H2) für Sackprodukte für Schmal- und Breitspur
--   -> Obwohl phyisch nur ein Ort, sind das in LOGON zwei Ladestrassen welche
--      an zwei verschiedenen ShippingPoints (SBB/RhB) hängen.
--   -> Beide Ladestrassen haben das gleiche Geleise zugeordnet
--   -> Kein Parallel (nur für Lose)
-- - 1 Durchfahrtsgeleis (H3)  (für Güterentlad hinten im Werk wie Braunkohle,
--   Steinkohle, GöS), wird teilweise als Abstellgeleise benutzt (nur Breitspur)
-- - 1 Rampe für Sackverlad (H1)
-- - 1 Stumpengeleise (H5) um Wagen abzustellen (z.B. vorgeladene Sackwagen
--   für Tessin, oder ausrangierte für Reparatur)
--
-- Zudem gibt es am Bahnhof ausserhalb des Werkes noch eine (unwichtige)
-- Aufteilung der Geleise Z4 und Z5. Hinter der Weiche (wechsel Z4<->Z5)
-- werden die Teilstücke nochmals umbenannt in Z14 (von Z4) und Z15 (von Z5).
--
-- June 2012: All these Tracks are probably still valid (because "reality")
-- However, adjusted all plant codes to the "new" EBM-Cluster1 plants.

prompt '---------- initialdata/tlgnr_plant_track.sql ----------'

prompt INSERTING into TLGNR_PLANT_TRACK_BS
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 1, 'C102', 'H1', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2, 'C102', 'H2', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 3, 'C102', 'S1', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 4, 'C101', 'H1', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 5, 'C101', 'H2', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 6, 'C103', 'H1', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 7, 'C103', 'H2', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 8, 'C103', 'H3', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 9, 'C103', 'H4', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'C103', 'H5', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'C103', 'H6', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'C103', 'Z1', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (13, 'C103', 'Z2', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (14, 'C103', 'Z3', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (15, 'C103', 'Z4', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (16, 'C103', 'Z5', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_bs (plant_track_cd, plant_cd, track_name, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (17, 'C103', 'B1', 1, 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - DE
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 1, 'de', 'SG - Losebelad'                , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2, 'de', 'SG - Sackbelad/Sigma'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 3, 'de', 'SG - Bahnhof'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 4, 'de', 'EC - Losebelad 1 / Sackbelad'  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 5, 'de', 'EC - Losebelad 2'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 6, 'de', 'UV - Pallets'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 7, 'de', 'UV - RbB Bulk'                 , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 8, 'de', 'UV - Abstellgleis'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 9, 'de', 'UV - Normalspur Bulk / Klinker', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'de', 'UV - Abstellgleis'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'de', 'UV - Normalspur Bulk'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'de', 'Zizers - Z1'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (13, 'de', 'Zizers - Z2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (14, 'de', 'Zizers - Z3 (Durchfahrt)'      , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (15, 'de', 'Zizers - Z4'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (16, 'de', 'Zizers - Z5'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (17, 'de', 'Buchs (alle Geleise)'          , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - EN (DEUTSCH COPIED)
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 1, 'en', 'SG - Losebelad'                , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2, 'en', 'SG - Sackbelad/Sigma'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 3, 'en', 'SG - Bahnhof'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 4, 'en', 'EC - Losebelad 1 / Sackbelad'  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 5, 'en', 'EC - Losebelad 2'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 6, 'en', 'UV - Pallets'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 7, 'en', 'UV - RbB Bulk'                 , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 8, 'en', 'UV - Abstellgleis'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 9, 'en', 'UV - Normalspur Bulk / Klinker', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'en', 'UV - Abstellgleis'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'en', 'UV - Normalspur Bulk'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'en', 'Zizers - Z1'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (13, 'en', 'Zizers - Z2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (14, 'en', 'Zizers - Z3 (Durchfahrt)'      , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (15, 'en', 'Zizers - Z4'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (16, 'en', 'Zizers - Z5'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (17, 'en', 'Buchs (alle Geleise)'          , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - FR (DEUTSCH COPIED)
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 1, 'fr', 'SG - Losebelad'                , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2, 'fr', 'SG - Sackbelad/Sigma'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 3, 'fr', 'SG - Bahnhof'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 4, 'fr', 'EC - Losebelad 1 / Sackbelad'  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 5, 'fr', 'EC - Losebelad 2'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 6, 'fr', 'UV - Pallets'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 7, 'fr', 'UV - RbB Bulk'                 , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 8, 'fr', 'UV - Abstellgleis'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 9, 'fr', 'UV - Normalspur Bulk / Klinker', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'fr', 'UV - Abstellgleis'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'fr', 'UV - Normalspur Bulk'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'fr', 'Zizers - Z1'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (13, 'fr', 'Zizers - Z2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (14, 'fr', 'Zizers - Z3 (Durchfahrt)'      , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (15, 'fr', 'Zizers - Z4'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (16, 'fr', 'Zizers - Z5'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (17, 'fr', 'Buchs (alle Geleise)'          , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - IT (DEUTSCH COPIED)
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 1, 'it', 'SG - Losebelad'                , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2, 'it', 'SG - Sackbelad/Sigma'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 3, 'it', 'SG - Bahnhof'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 4, 'it', 'EC - Losebelad 1 / Sackbelad'  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 5, 'it', 'EC - Losebelad 2'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 6, 'it', 'UV - Pallets'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 7, 'it', 'UV - RbB Bulk'                 , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 8, 'it', 'UV - Abstellgleis'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 9, 'it', 'UV - Normalspur Bulk / Klinker', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'it', 'UV - Abstellgleis'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'it', 'UV - Normalspur Bulk'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'it', 'Zizers - Z1'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (13, 'it', 'Zizers - Z2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (14, 'it', 'Zizers - Z3 (Durchfahrt)'      , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (15, 'it', 'Zizers - Z4'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (16, 'it', 'Zizers - Z5'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (17, 'it', 'Buchs (alle Geleise)'          , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_PLANT_TRACK_TX - ES (DEUTSCH COPIED)
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 1, 'es', 'SG - Losebelad'                , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 2, 'es', 'SG - Sackbelad/Sigma'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 3, 'es', 'SG - Bahnhof'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 4, 'es', 'EC - Losebelad 1 / Sackbelad'  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 5, 'es', 'EC - Losebelad 2'              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 6, 'es', 'UV - Pallets'                  , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 7, 'es', 'UV - RbB Bulk'                 , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 8, 'es', 'UV - Abstellgleis'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ( 9, 'es', 'UV - Normalspur Bulk / Klinker', 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'es', 'UV - Abstellgleis'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (11, 'es', 'UV - Normalspur Bulk'          , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (12, 'es', 'Zizers - Z1'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (13, 'es', 'Zizers - Z2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (14, 'es', 'Zizers - Z3 (Durchfahrt)'      , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (15, 'es', 'Zizers - Z4'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (16, 'es', 'Zizers - Z5'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_plant_track_tx (plant_track_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (17, 'es', 'Buchs (alle Geleise)'          , 1, systimestamp, 1, systimestamp);

commit;
