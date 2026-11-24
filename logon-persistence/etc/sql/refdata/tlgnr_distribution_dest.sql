-- $Revision$
-- $Date$
--
-- List based on spec /share/doc/crh/pegasus/input/SAP/ifc_spec_send/Sales_Order_and_Deliveries_Logon-SAP_V2.5_20090213.xls
-- 30.1.2011: Added flag "selectable_for_shipto_flag" to all, but now new codes. See LOGON-4365.

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_distribution_dest.sql ----------'

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_BS
insert into TLGNR_DISTRIBUTION_DEST_BS (distribution_dest_cd, selectable_for_shipto_flag, ctl_act_cd) values ('OUT',  1, 1);
insert into TLGNR_DISTRIBUTION_DEST_BS (distribution_dest_cd, selectable_for_shipto_flag, ctl_act_cd) values ('IUD',  1, 1);
insert into TLGNR_DISTRIBUTION_DEST_BS (distribution_dest_cd, selectable_for_shipto_flag, ctl_act_cd) values ('IUP',  1, 1);
insert into TLGNR_DISTRIBUTION_DEST_BS (distribution_dest_cd, selectable_for_shipto_flag, ctl_act_cd) values ('INB',  1, 1);
insert into TLGNR_DISTRIBUTION_DEST_BS (distribution_dest_cd, selectable_for_shipto_flag, ctl_act_cd) values ('REP',  1, 1);
insert into TLGNR_DISTRIBUTION_DEST_BS (distribution_dest_cd, selectable_for_shipto_flag, ctl_act_cd) values ('OUTR', 0, 1);
insert into TLGNR_DISTRIBUTION_DEST_BS (distribution_dest_cd, selectable_for_shipto_flag, ctl_act_cd) values ('IUDR', 0, 1);
insert into TLGNR_DISTRIBUTION_DEST_BS (distribution_dest_cd, selectable_for_shipto_flag, ctl_act_cd) values ('INBR', 0, 1);
insert into TLGNR_DISTRIBUTION_DEST_BS (distribution_dest_cd, selectable_for_shipto_flag, ctl_act_cd) values ('IUPR', 0, 1);

-- PLEASE: In case customer says 'value X is not desired in Logon' do NOT simply remove, but COMMENT it out, so the decision is recorded, and the value does not come back again...

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - EN
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'en', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'en', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'en', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'en', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'en', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'en', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'en', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'en', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'en', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - DE (ENGLISH COPIED)
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'de', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'de', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'de', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'de', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'de', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'de', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'de', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'de', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'de', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - FR (ENGLISH COPIED)
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'fr', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'fr', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'fr', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'fr', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'fr', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'fr', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'fr', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'fr', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'fr', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - IT (ENGLISH COPIED)
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'it', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'it', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'it', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'it', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'it', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'it', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'it', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'it', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'it', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - ES (ENGLISH COPIED)
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'es', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'es', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'es', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'es', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'es', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'es', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'es', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'es', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'es', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - CS (ENGLISH COPIED)
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'cs', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'cs', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'cs', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'cs', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'cs', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'cs', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'cs', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'cs', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'cs', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - SK (ENGLISH COPIED)
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'sk', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'sk', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'sk', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'sk', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'sk', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'sk', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'sk', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'sk', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'sk', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - HU (ENGLISH COPIED)
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'hu', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'hu', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'hu', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'hu', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'hu', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'hu', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'hu', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'hu', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'hu', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - RO (ENGLISH COPIED)
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'ro', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'ro', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'ro', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'ro', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'ro', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'ro', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'ro', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'ro', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'ro', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - BG (ENGLISH COPIED)
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'bg', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'bg', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'bg', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'bg', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'bg', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'bg', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'bg', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'bg', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'bg', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - RU (ENGLISH COPY)
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'ru', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'ru', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'ru', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'ru', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'ru', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'ru', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'ru', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'ru', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'ru', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - PL (ENGLISH COPY)
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'pl', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'pl', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'pl', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'pl', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'pl', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'pl', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'pl', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'pl', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'pl', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - AZ (ENGLISH COPY)
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'az', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'az', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'az', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'az', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'az', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'az', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'az', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'az', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'az', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - HR (ENGLISH COPY)
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'hr', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'hr', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'hr', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'hr', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'hr', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'hr', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'hr', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'hr', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'hr', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - SR (ENGLISH COPY)
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'sr', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'sr', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'sr', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'sr', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'sr', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'sr', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'sr', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'sr', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'sr', 'Inbound Return');

prompt INSERTING into TLGNR_DISTRIBUTION_DEST_TX - TR
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUT', 'tr', 'Outbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUD', 'tr', 'Inter-Unit Distribution');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUP', 'tr', 'Inter-Unit Production');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INB', 'tr', 'Inbound');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('REP', 'tr', 'Repair, Maintenance');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('OUTR', 'tr', 'Outbound Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUDR', 'tr', 'Inter-Unit Distribution Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('IUPR', 'tr', 'Inter-Unit Production Return');
insert into TLGNR_DISTRIBUTION_DEST_TX(DISTRIBUTION_DEST_CD, LANG_CD, TXT) values ('INBR', 'tr', 'Inbound Return');

commit;

