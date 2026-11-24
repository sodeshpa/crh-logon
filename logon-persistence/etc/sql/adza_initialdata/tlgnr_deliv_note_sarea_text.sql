prompt '---------- adza_initialdata/tlgnr_deliv_note_sarea_text.sql ----------'

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_BS
insert into tlgnr_deliv_note_sarea_text_bs(deliv_note_sarea_text_cd, sorg_cd, distribution_channel, division_cd, delivery_type_cd, text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1000, 'AD02', '10', '10', 1, 'pdf.deliverynote.deliverytype', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_deliv_note_sarea_text_bs(deliv_note_sarea_text_cd, sorg_cd, distribution_channel, division_cd, delivery_type_cd, text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'AD02', '10', '10', 1, 'pdf.deliverynote.footer.title', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_deliv_note_sarea_text_bs(deliv_note_sarea_text_cd, sorg_cd, distribution_channel, division_cd, delivery_type_cd, text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'AD02', '10', '10', 1, 'pdf.deliverynote.footer.text', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_deliv_note_sarea_text_bs(deliv_note_sarea_text_cd, sorg_cd, distribution_channel, division_cd, delivery_type_cd, text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'AD02', '10', '10', 1, 'pdf.delivnote.eichbeleg.intro', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_deliv_note_sarea_text_bs(deliv_note_sarea_text_cd, sorg_cd, distribution_channel, division_cd, delivery_type_cd, text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'AD02', '10', '10', 1, 'pdf.delivnote.eichbeleg.tare', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_deliv_note_sarea_text_bs(deliv_note_sarea_text_cd, sorg_cd, distribution_channel, division_cd, delivery_type_cd, text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'AD02', '10', '10', 1, 'pdf.delivnote.eichbeleg.gross', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_deliv_note_sarea_text_bs(deliv_note_sarea_text_cd, sorg_cd, distribution_channel, division_cd, delivery_type_cd, text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'AD02', '10', '10', 5, 'pdf.deliverynote.bimodal_ord_wagon_inf', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_deliv_note_sarea_text_bs(deliv_note_sarea_text_cd, sorg_cd, distribution_channel, division_cd, delivery_type_cd, text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'AD02', '10', '10', 1, 'pdf.collective_deliverynote.footer.title', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_deliv_note_sarea_text_bs(deliv_note_sarea_text_cd, sorg_cd, distribution_channel, division_cd, delivery_type_cd, text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'AD02', '10', '10', 1, 'pdf.collective_deliverynote.footer.text', 1, 1, 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - EN
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'en', 'AD02-10-10 Bimodal Truck wagon inf (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'en', 'AD02-10-10 Truck delivery text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'en', 'AD02-10-10 Truck Eichbeleg gross (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'en', 'AD02-10-10 Truck Eichbeleg intro (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'en', 'AD02-10-10 Truck Eichbeleg tare (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'en', 'AD02-10-10 Truck Footer text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'en', 'AD02-10-10 Truck Footer text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'en', 'AD02-10-10 Truck Footer title (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'en', 'AD02-10-10 Truck Footer title (en)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - DE
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'de', 'AD02-10-10 Bimodal Truck wagon inf (de)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'de', 'AD02-10-10 Truck delivery text (de)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'de', 'AD02-10-10 Truck Eichbeleg gross (de)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'de', 'AD02-10-10 Truck Eichbeleg intro (de)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'de', 'AD02-10-10 Truck Eichbeleg tare (de)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'de', 'AD02-10-10 Truck Footer text (de)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'de', 'AD02-10-10 Truck Footer text (de)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'de', 'AD02-10-10 Truck Footer title (de)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'de', 'AD02-10-10 Truck Footer title (de)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - FR
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'fr', 'AD02-10-10 Bimodal Truck wagon inf (fr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'fr', 'AD02-10-10 Truck delivery text (fr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'fr', 'AD02-10-10 Truck Eichbeleg gross (fr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'fr', 'AD02-10-10 Truck Eichbeleg intro (fr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'fr', 'AD02-10-10 Truck Eichbeleg tare (fr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'fr', 'AD02-10-10 Truck Footer text (fr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'fr', 'AD02-10-10 Truck Footer text (fr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'fr', 'AD02-10-10 Truck Footer title (fr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'fr', 'AD02-10-10 Truck Footer title (fr)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - IT
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'it', 'AD02-10-10 Bimodal Truck wagon inf (it)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'it', 'AD02-10-10 Truck delivery text (it)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'it', 'AD02-10-10 Truck Eichbeleg gross (it)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'it', 'AD02-10-10 Truck Eichbeleg intro (it)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'it', 'AD02-10-10 Truck Eichbeleg tare (it)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'it', 'AD02-10-10 Truck Footer text (it)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'it', 'AD02-10-10 Truck Footer text (it)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'it', 'AD02-10-10 Truck Footer title (it)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'it', 'AD02-10-10 Truck Footer title (it)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - ES
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'es', 'AD02-10-10 Bimodal Truck wagon inf (es)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'es', 'AD02-10-10 Truck delivery text (es)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'es', 'AD02-10-10 Truck Eichbeleg gross (es)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'es', 'AD02-10-10 Truck Eichbeleg intro (es)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'es', 'AD02-10-10 Truck Eichbeleg tare (es)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'es', 'AD02-10-10 Truck Footer text (es)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'es', 'AD02-10-10 Truck Footer text (es)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'es', 'AD02-10-10 Truck Footer title (es)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'es', 'AD02-10-10 Truck Footer title (es)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - CS
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'cs', 'AD02-10-10 Bimodal Truck wagon inf (cs)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'cs', 'AD02-10-10 Truck delivery text (cs)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'cs', 'AD02-10-10 Truck Eichbeleg gross (cs)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'cs', 'AD02-10-10 Truck Eichbeleg intro (cs)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'cs', 'AD02-10-10 Truck Eichbeleg tare (cs)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'cs', 'AD02-10-10 Truck Footer text (cs)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'cs', 'AD02-10-10 Truck Footer text (cs)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'cs', 'AD02-10-10 Truck Footer title (cs)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'cs', 'AD02-10-10 Truck Footer title (cs)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - SK
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'sk', 'AD02-10-10 Bimodal Truck wagon inf (sk)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'sk', 'AD02-10-10 Truck delivery text (sk)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'sk', 'AD02-10-10 Truck Eichbeleg gross (sk)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'sk', 'AD02-10-10 Truck Eichbeleg intro (sk)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'sk', 'AD02-10-10 Truck Eichbeleg tare (sk)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'sk', 'AD02-10-10 Truck Footer text (sk)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'sk', 'AD02-10-10 Truck Footer text (sk)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'sk', 'AD02-10-10 Truck Footer title (sk)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'sk', 'AD02-10-10 Truck Footer title (sk)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - HU
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'hu', 'AD02-10-10 Bimodal Truck wagon inf (hu)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'hu', 'AD02-10-10 Truck delivery text (hu)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'hu', 'AD02-10-10 Truck Eichbeleg gross (hu)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'hu', 'AD02-10-10 Truck Eichbeleg intro (hu)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'hu', 'AD02-10-10 Truck Eichbeleg tare (hu)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'hu', 'AD02-10-10 Truck Footer text (hu)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'hu', 'AD02-10-10 Truck Footer text (hu)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'hu', 'AD02-10-10 Truck Footer title (hu)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'hu', 'AD02-10-10 Truck Footer title (hu)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - RO
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'ro', 'AD02-10-10 Bimodal Truck wagon inf (ro)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'ro', 'AD02-10-10 Truck delivery text (ro)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'ro', 'AD02-10-10 Truck Eichbeleg gross (ro)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'ro', 'AD02-10-10 Truck Eichbeleg intro (ro)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'ro', 'AD02-10-10 Truck Eichbeleg tare (ro)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'ro', 'AD02-10-10 Truck Footer text (ro)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'ro', 'AD02-10-10 Truck Footer text (ro)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'ro', 'AD02-10-10 Truck Footer title (ro)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'ro', 'AD02-10-10 Truck Footer title (ro)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - BG
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'bg', 'AD02-10-10 Bimodal Truck wagon inf (bg)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'bg', 'AD02-10-10 Truck delivery text (bg)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'bg', 'AD02-10-10 Truck Eichbeleg gross (bg)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'bg', 'AD02-10-10 Truck Eichbeleg intro (bg)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'bg', 'AD02-10-10 Truck Eichbeleg tare (bg)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'bg', 'AD02-10-10 Truck Footer text (bg)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'bg', 'AD02-10-10 Truck Footer text (bg)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'bg', 'AD02-10-10 Truck Footer title (bg)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'bg', 'AD02-10-10 Truck Footer title (bg)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - RU
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'ru', 'AD02-10-10 Bimodal Truck wagon inf (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'ru', 'AD02-10-10 Truck delivery text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'ru', 'AD02-10-10 Truck Eichbeleg gross (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'ru', 'AD02-10-10 Truck Eichbeleg intro (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'ru', 'AD02-10-10 Truck Eichbeleg tare (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'ru', 'AD02-10-10 Truck Footer text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'ru', 'AD02-10-10 Truck Footer text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'ru', 'AD02-10-10 Truck Footer title (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'ru', 'AD02-10-10 Truck Footer title (en)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - PL
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'pl', 'AD02-10-10 Bimodal Truck wagon inf (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'pl', 'AD02-10-10 Truck delivery text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'pl', 'AD02-10-10 Truck Eichbeleg gross (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'pl', 'AD02-10-10 Truck Eichbeleg intro (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'pl', 'AD02-10-10 Truck Eichbeleg tare (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'pl', 'AD02-10-10 Truck Footer text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'pl', 'AD02-10-10 Truck Footer text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'pl', 'AD02-10-10 Truck Footer title (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'pl', 'AD02-10-10 Truck Footer title (en)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - AZ
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'az', 'AD02-10-10 Bimodal Truck wagon inf (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'az', 'AD02-10-10 Truck delivery text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'az', 'AD02-10-10 Truck Eichbeleg gross (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'az', 'AD02-10-10 Truck Eichbeleg intro (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'az', 'AD02-10-10 Truck Eichbeleg tare (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'az', 'AD02-10-10 Truck Footer text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'az', 'AD02-10-10 Truck Footer text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'az', 'AD02-10-10 Truck Footer title (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'az', 'AD02-10-10 Truck Footer title (en)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - HR
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'hr', 'AD02-10-10 Bimodal Truck wagon inf (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'hr', 'AD02-10-10 Truck delivery text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'hr', 'AD02-10-10 Truck Eichbeleg gross (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'hr', 'AD02-10-10 Truck Eichbeleg intro (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'hr', 'AD02-10-10 Truck Eichbeleg tare (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'hr', 'AD02-10-10 Truck Footer text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'hr', 'AD02-10-10 Truck Footer text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'hr', 'AD02-10-10 Truck Footer title (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'hr', 'AD02-10-10 Truck Footer title (en)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - SR
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'sr', 'AD02-10-10 Bimodal Truck wagon inf (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'sr', 'AD02-10-10 Truck delivery text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'sr', 'AD02-10-10 Truck Eichbeleg gross (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'sr', 'AD02-10-10 Truck Eichbeleg intro (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'sr', 'AD02-10-10 Truck Eichbeleg tare (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'sr', 'AD02-10-10 Truck Footer text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'sr', 'AD02-10-10 Truck Footer text (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'sr', 'AD02-10-10 Truck Footer title (en)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'sr', 'AD02-10-10 Truck Footer title (en)');

prompt INSERTING into TLGNR_DELIV_NOTE_SAREA_TEXT_TX - TR
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1006, 'tr', 'AD02-10-10 Bimodal Truck wagon inf (tr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1000, 'tr', 'AD02-10-10 Truck delivery text (tr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1005, 'tr', 'AD02-10-10 Truck Eichbeleg gross (tr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1003, 'tr', 'AD02-10-10 Truck Eichbeleg intro (tr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1004, 'tr', 'AD02-10-10 Truck Eichbeleg tare (tr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1002, 'tr', 'AD02-10-10 Truck Footer text (tr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1008, 'tr', 'AD02-10-10 Truck Footer text (tr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1001, 'tr', 'AD02-10-10 Truck Footer title (tr)');
insert into tlgnr_deliv_note_sarea_text_tx(deliv_note_sarea_text_cd, lang_cd, txt) values (-1007, 'tr', 'AD02-10-10 Truck Footer title (tr)');

commit;

