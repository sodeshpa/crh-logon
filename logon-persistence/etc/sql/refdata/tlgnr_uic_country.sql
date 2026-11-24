-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_uic_country.sql ----------'

prompt INSERTING into TLGNR_UIC_COUNTRY_BS
insert into TLGNR_UIC_COUNTRY_BS (UIC_COUNTRY_CD, UIC_COUNTRY_ALPHA_CD, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (80, 'D', 1, 1, systimestamp, 1, systimestamp);
insert into TLGNR_UIC_COUNTRY_BS (UIC_COUNTRY_CD, UIC_COUNTRY_ALPHA_CD, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (81, 'A', 1, 1, systimestamp, 1, systimestamp);
insert into TLGNR_UIC_COUNTRY_BS (UIC_COUNTRY_CD, UIC_COUNTRY_ALPHA_CD, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (83, 'IT', 1, 1, systimestamp, 1, systimestamp);
insert into TLGNR_UIC_COUNTRY_BS (UIC_COUNTRY_CD, UIC_COUNTRY_ALPHA_CD, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (85, 'CH', 1, 1, systimestamp, 1, systimestamp);
insert into TLGNR_UIC_COUNTRY_BS (UIC_COUNTRY_CD, UIC_COUNTRY_ALPHA_CD, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (87, 'F', 1, 1, systimestamp, 1, systimestamp);
insert into TLGNR_UIC_COUNTRY_BS (UIC_COUNTRY_CD, UIC_COUNTRY_ALPHA_CD, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (52, 'BG', 1, 1, systimestamp, 1, systimestamp);
insert into TLGNR_UIC_COUNTRY_BS (UIC_COUNTRY_CD, UIC_COUNTRY_ALPHA_CD, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (78, 'HR', 1, 1, systimestamp, 1, systimestamp);
insert into TLGNR_UIC_COUNTRY_BS (UIC_COUNTRY_CD, UIC_COUNTRY_ALPHA_CD, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (54, 'CZ', 1, 1, systimestamp, 1, systimestamp);
insert into TLGNR_UIC_COUNTRY_BS (UIC_COUNTRY_CD, UIC_COUNTRY_ALPHA_CD, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (55, 'HU', 1, 1, systimestamp, 1, systimestamp);
insert into TLGNR_UIC_COUNTRY_BS (UIC_COUNTRY_CD, UIC_COUNTRY_ALPHA_CD, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (53, 'RO', 1, 1, systimestamp, 1, systimestamp);
insert into TLGNR_UIC_COUNTRY_BS (UIC_COUNTRY_CD, UIC_COUNTRY_ALPHA_CD, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (72, 'RS', 1, 1, systimestamp, 1, systimestamp);
insert into TLGNR_UIC_COUNTRY_BS (UIC_COUNTRY_CD, UIC_COUNTRY_ALPHA_CD, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (56, 'SK', 1, 1, systimestamp, 1, systimestamp);
insert into TLGNR_UIC_COUNTRY_BS (UIC_COUNTRY_CD, UIC_COUNTRY_ALPHA_CD, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (22, 'UA', 1, 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - EN
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'en', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'en', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'en', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'en', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'en', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'en', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'en', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'en', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'en', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'en', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'en', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'en', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'en', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - DE
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'de', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'de', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'de', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'de', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'de', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'de', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'de', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'de', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'de', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'de', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'de', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'de', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'de', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - FR
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'fr', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'fr', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'fr', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'fr', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'fr', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'fr', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'fr', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'fr', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'fr', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'fr', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'fr', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'fr', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'fr', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - IT
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'it', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'it', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'it', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'it', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'it', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'it', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'it', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'it', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'it', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'it', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'it', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'it', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'it', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - ES
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'es', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'es', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'es', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'es', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'es', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'es', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'es', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'es', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'es', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'es', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'es', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'es', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'es', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - CS (ENGLISH COPY)
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'cs', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'cs', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'cs', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'cs', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'cs', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'cs', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'cs', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'cs', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'cs', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'cs', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'cs', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'cs', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'cs', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - SK (ENGLISH COPY)
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'sk', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'sk', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'sk', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'sk', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'sk', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'sk', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'sk', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'sk', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'sk', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'sk', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'sk', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'sk', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'sk', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - HU (ENGLISH COPY)
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'hu', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'hu', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'hu', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'hu', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'hu', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'hu', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'hu', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'hu', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'hu', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'hu', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'hu', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'hu', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'hu', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - RO (ENGLISH COPY)
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'ro', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'ro', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'ro', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'ro', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'ro', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'ro', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'ro', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'ro', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'ro', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'ro', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'ro', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'ro', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'ro', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - BG (ENGLISH COPY)
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'bg', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'bg', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'bg', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'bg', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'bg', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'bg', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'bg', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'bg', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'bg', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'bg', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'bg', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'bg', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'bg', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - RU (ENGLISH COPY)
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'ru', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'ru', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'ru', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'ru', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'ru', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'ru', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'ru', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'ru', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'ru', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'ru', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'ru', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'ru', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'ru', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - PL (ENGLISH COPY)
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'pl', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'pl', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'pl', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'pl', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'pl', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'pl', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'pl', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'pl', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'pl', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'pl', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'pl', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'pl', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'pl', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - AZ (ENGLISH COPY)
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'az', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'az', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'az', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'az', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'az', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'az', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'az', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'az', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'az', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'az', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'az', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'az', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'az', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - HR (ENGLISH COPY)
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'hr', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'hr', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'hr', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'hr', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'hr', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'hr', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'hr', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'hr', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'hr', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'hr', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'hr', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'hr', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'hr', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - SR (ENGLISH COPY)
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'sr', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'sr', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'sr', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'sr', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'sr', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'sr', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'sr', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'sr', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'sr', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'sr', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'sr', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'sr', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'sr', 'Ukraine');

prompt INSERTING into TLGNR_UIC_COUNTRY_TX - TR
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (80, 'tr', 'Germany');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (81, 'tr', 'Austria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (83, 'tr', 'Italy');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (85, 'tr', 'Switzerland');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (87, 'tr', 'France');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (52, 'tr', 'Bulgaria');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (78, 'tr', 'Croatia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (54, 'tr', 'Czech Republic');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (55, 'tr', 'Hungary');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (53, 'tr', 'Romania');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (72, 'tr', 'Serbia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (56, 'tr', 'Slovakia');
insert into TLGNR_UIC_COUNTRY_TX (UIC_COUNTRY_CD,LANG_CD,TXT) values (22, 'tr', 'Ukraine');

commit;

