-- based on ticket LOGON-4598

prompt '---------- adza_initialdata/tlgnr_text.sql ----------'

prompt INSERTING into TLGNR_TEXT_BS

-- We are in ADZA, i.e CLUSTER SPECIFIC data, for "test"-"cluster" Andorra-Southafrica.
-- Any NEWLY introduced keys must be added to refdata/tlgnr_text.sql NOT here!

prompt INSERTING into TLGNR_TEXT_BS
insert into tlgnr_text_bs(text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_bs (text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_bs (text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_bs (text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_bs (text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_bs (text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 1, 1, 1, systimestamp, 1, systimestamp);

/* Careful such values would all be REFDATA, to be customized by Crh, but to be included for ALL dbs, not just for ADZA:

pdf.collective_deliverynote.footer.title: Collective del. note footer title
pdf.collective_deliverynote.footer.text:  Collective del. note footer text
pdf.deliverynote.deliverytype:            Delivery type fallback
*/

-- refdata!! insert into tlgnr_text_bs(text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.collective_deliverynote.footer.title', 1, 1, 1, systimestamp, 1, systimestamp);
-- refdata!! insert into tlgnr_text_bs(text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.collective_deliverynote.footer.text', 1, 1, 1, systimestamp, 1, systimestamp);
-- refdata!! insert into tlgnr_text_bs(text_cd, placeholder_count, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.deliverytype', 1, 1, 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - EN
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'en', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'en', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'en', 'AdNovum-TESTDATA: first line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'en', 'AdNovum-TESTDATA: first line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'en', 'AdNovum-TESTDATA: second line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'en', 'AdNovum-TESTDATA: second line of crossboundary (en)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - DE
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'de', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'de', 'AdNovum-TESTDATA: Grenz�berschreitende Lieferung ZA01-CH, siehe tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (de)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'de', 'AdNovum-TESTDATA: first line of crossboundary (de)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'de', 'AdNovum-TESTDATA: first line of crossboundary (de)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'de', 'AdNovum-TESTDATA: second line of crossboundary (de)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'de', 'AdNovum-TESTDATA: second line of crossboundary (de)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - FR
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'fr', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'fr', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (fr)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'fr', 'AdNovum-TESTDATA: first line of crossboundary (fr)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'fr', 'AdNovum-TESTDATA: first line of crossboundary (fr)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'fr', 'AdNovum-TESTDATA: second line of crossboundary (fr)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'fr', 'AdNovum-TESTDATA: second line of crossboundary (fr)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - IT
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'it', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'it', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (it)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'it', 'AdNovum-TESTDATA: first line of crossboundary (it)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'it', 'AdNovum-TESTDATA: first line of crossboundary (it)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'it', 'AdNovum-TESTDATA: second line of crossboundary (it)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'it', 'AdNovum-TESTDATA: second line of crossboundary (it)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - ES
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'es', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'es', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (es)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'es', 'AdNovum-TESTDATA: first line of crossboundary (es)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'es', 'AdNovum-TESTDATA: first line of crossboundary (es)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'es', 'AdNovum-TESTDATA: second line of crossboundary (es)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'es', 'AdNovum-TESTDATA: second line of crossboundary (es)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - CS
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'cs', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'cs', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (cs)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'cs', 'AdNovum-TESTDATA: first line of crossboundary (cs)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'cs', 'AdNovum-TESTDATA: first line of crossboundary (cs)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'cs', 'AdNovum-TESTDATA: second line of crossboundary (cs)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'cs', 'AdNovum-TESTDATA: second line of crossboundary (cs)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - SK
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'sk', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'sk', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (sk)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'sk', 'AdNovum-TESTDATA: first line of crossboundary (sk)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'sk', 'AdNovum-TESTDATA: first line of crossboundary (sk)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'sk', 'AdNovum-TESTDATA: second line of crossboundary (sk)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'sk', 'AdNovum-TESTDATA: second line of crossboundary (sk)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - HU
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'hu', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'hu', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (hu)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'hu', 'AdNovum-TESTDATA: first line of crossboundary (hu)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'hu', 'AdNovum-TESTDATA: first line of crossboundary (hu)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'hu', 'AdNovum-TESTDATA: second line of crossboundary (hu)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'hu', 'AdNovum-TESTDATA: second line of crossboundary (hu)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - RO
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'ro', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'ro', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (ro)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'ro', 'AdNovum-TESTDATA: first line of crossboundary (ro)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'ro', 'AdNovum-TESTDATA: first line of crossboundary (ro)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'ro', 'AdNovum-TESTDATA: second line of crossboundary (ro)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'ro', 'AdNovum-TESTDATA: second line of crossboundary (ro)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - BG
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'bg', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'bg', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (bg)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'bg', 'AdNovum-TESTDATA: first line of crossboundary (bg)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'bg', 'AdNovum-TESTDATA: first line of crossboundary (bg)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'bg', 'AdNovum-TESTDATA: second line of crossboundary (bg)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'bg', 'AdNovum-TESTDATA: second line of crossboundary (bg)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - RU
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'ru', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'ru', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'ru', 'AdNovum-TESTDATA: first line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'ru', 'AdNovum-TESTDATA: first line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'ru', 'AdNovum-TESTDATA: second line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'ru', 'AdNovum-TESTDATA: second line of crossboundary (en)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - PL
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'pl', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'pl', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'pl', 'AdNovum-TESTDATA: first line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'pl', 'AdNovum-TESTDATA: first line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'pl', 'AdNovum-TESTDATA: second line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'pl', 'AdNovum-TESTDATA: second line of crossboundary (en)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - AZ
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'az', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'az', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'az', 'AdNovum-TESTDATA: first line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'az', 'AdNovum-TESTDATA: first line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'az', 'AdNovum-TESTDATA: second line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'az', 'AdNovum-TESTDATA: second line of crossboundary (en)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - HR
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'hr', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'hr', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'hr', 'AdNovum-TESTDATA: first line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'hr', 'AdNovum-TESTDATA: first line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'hr', 'AdNovum-TESTDATA: second line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'hr', 'AdNovum-TESTDATA: second line of crossboundary (en)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - SR
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'sr', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'sr', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'sr', 'AdNovum-TESTDATA: first line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'sr', 'AdNovum-TESTDATA: first line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'sr', 'AdNovum-TESTDATA: second line of crossboundary (en)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'sr', 'AdNovum-TESTDATA: second line of crossboundary (en)', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TEXT_TX - TR
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.AD01ZA', 'tr', 'Periodische Sammelanmeldung nach ZG 42 Abs 1  Bes. c,  mit Antrag auf Prüferenzabfertigung. Der Ausführer der Waren, auf die sich dieses Handlespapier bezieht, erklärt, dass diese Waren soweit nicht anders angegeben, prüferenzbegünstigte CE Ursprungswaren sind.', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.deliverynote.crossboundary.ZA01CH', 'tr', 'AdNovum-TESTDATA: Cross-boundary Delivery ZA01-CH, see tlgnr_text_bs/tx, pdf.deliverynote.crossboundary.ZA01CH (tr)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.AD01ZA', 'tr', 'AdNovum-TESTDATA: first line of crossboundary (tr)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary1.ZA01CH', 'tr', 'AdNovum-TESTDATA: first line of crossboundary (tr)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.AD01ZA', 'tr', 'AdNovum-TESTDATA: second line of crossboundary (tr)', 1, systimestamp, 1, systimestamp);
insert into tlgnr_text_tx (text_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('pdf.delivnote.export.crossboundary2.ZA01CH', 'tr', 'AdNovum-TESTDATA: second line of crossboundary (tr)', 1, systimestamp, 1, systimestamp);

commit;

