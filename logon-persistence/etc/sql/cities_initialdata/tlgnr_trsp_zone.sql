-- $Revision$
-- $Date$
--
-- Values based on LOGON CHE, i.e. 2009: /share/doc/crh/pegasus/input/Sack/SAP_Transport_Zones_LOGON.xls
-- 4.6.2012: Included those as initialdata for cluster 1, "asis".

prompt '--------- cl1_initialdata/tlgnr_trsp_zone.sql ----------'

prompt INSERTING into TLGNR_TRSP_ZONE_BS
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('FR00000', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('RS-A01', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('SK-A01', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('PL-A01', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('HU-A01', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_bs(trsp_zone_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values('AT-A01', 1, 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_TRSP_ZONE_TX
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'en', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'en', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'en', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'en', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'en', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'en', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'de', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'de', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'de', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'de', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'de', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'de', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'fr', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'fr', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'fr', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'fr', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'fr', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'fr', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'it', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'it', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'it', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'it', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'it', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'it', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'es', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'es', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'es', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'es', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'es', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'es', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'sk', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'sk', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'sk', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'sk', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'sk', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'sk', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'cs', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'cs', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'cs', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'cs', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'cs', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'cs', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'hu', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'hu', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'hu', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'hu', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'hu', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'hu', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'ro', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'ro', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'ro', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'ro', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'ro', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'ro', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'bg', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'bg', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'bg', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'bg', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'bg', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'bg', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'hr', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'hr', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'hr', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'hr', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'hr', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'hr', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'sr', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'sr', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'sr', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'sr', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'sr', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'sr', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'ru', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'ru', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'ru', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'ru', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'ru', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'ru', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'pl', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'pl', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'pl', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'pl', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'pl', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'pl', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'az', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'az', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'az', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'az', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'az', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'az', 'Austria' ,1, systimestamp, 1, systimestamp);

insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('FR00000', 'tr', 'DEPART CENTRALE' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('RS-A01', 'tr', 'Serbia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('SK-A01', 'tr', 'Slovakia' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('PL-A01', 'tr', 'Poland' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('HU-A01', 'tr', 'Hungary' ,1, systimestamp, 1, systimestamp);
insert into tlgnr_trsp_zone_tx(trsp_zone_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('AT-A01', 'tr', 'Austria' ,1, systimestamp, 1, systimestamp);

commit;