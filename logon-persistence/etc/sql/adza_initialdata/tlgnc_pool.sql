-- based on ticket ADN71680
-- /share/doc/crh/logon_2_0_ebm/input/initialdata/20110728\ Customizing\ master\ data\ for\ ITC1\ v3\ -\ RME.xlsx

prompt '---------- adza_initialdata/tlgnc_pool.sql ----------'

prompt INSERTING into TLGNC_POOL

-- 5 south Africa
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, '10', 1, 1, systimestamp, 1, systimestamp);

-- 3 Andorra
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, '10', 1, 1, systimestamp, 1, systimestamp);


insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-1001, -1001, 'ZA');
insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-1002, -1002, 'ZA');
insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-1003, -1003, 'ZA');
insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-1004, -1004, 'ZA');
insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-1005, -1005, 'ZA');

insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-1006, -1006, 'AD');
insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-1007, -1007, 'AD');
insert into tlgnc_region_pool (region_pool_id, pool_id, region_cd) values (-1008, -1008, 'AD');

prompt INSERTING into TLGNC_POOL_TX - EN
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'en', 'South Africa-1 [en]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'en', 'South Africa-2 [en]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'en', 'South Africa-3 [en]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'en', 'South Africa-4 [en]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'en', 'South Africa-5 [en]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'en', 'Andorra-1 [en]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'en', 'Andorra-2 [en]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'en', 'Andorra-3 [en]', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - DE
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'de', 'South Africa-1 [de]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'de', 'South Africa-2 [de]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'de', 'South Africa-3 [de]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'de', 'South Africa-4 [de]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'de', 'South Africa-5 [de]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'de', 'Andorra-1 [de]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'de', 'Andorra-2 [de]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'de', 'Andorra-3 [de]', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - FR
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'fr', 'South Africa-1 [fr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'fr', 'South Africa-2 [fr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'fr', 'South Africa-3 [fr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'fr', 'South Africa-4 [fr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'fr', 'South Africa-5 [fr]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'fr', 'Andorra-1 [fr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'fr', 'Andorra-2 [fr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'fr', 'Andorra-3 [fr]', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - IT
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'it', 'South Africa-1 [it]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'it', 'South Africa-2 [it]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'it', 'South Africa-3 [it]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'it', 'South Africa-4 [it]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'it', 'South Africa-5 [it]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'it', 'Andorra-1 [it]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'it', 'Andorra-2 [it]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'it', 'Andorra-3 [it]', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - ES
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'es', 'South Africa-1 [es]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'es', 'South Africa-2 [es]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'es', 'South Africa-3 [es]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'es', 'South Africa-4 [es]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'es', 'South Africa-5 [es]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'es', 'Andorra-1 [es]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'es', 'Andorra-2 [es]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'es', 'Andorra-3 [es]', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - CS
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'cs', 'South Africa-1 [cs]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'cs', 'South Africa-2 [cs]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'cs', 'South Africa-3 [cs]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'cs', 'South Africa-4 [cs]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'cs', 'South Africa-5 [cs]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'cs', 'Andorra-1 [cs]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'cs', 'Andorra-2 [cs]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'cs', 'Andorra-3 [cs]', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - SK
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'sk', 'South Africa-1 [sk]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'sk', 'South Africa-2 [sk]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'sk', 'South Africa-3 [sk]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'sk', 'South Africa-4 [sk]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'sk', 'South Africa-5 [sk]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'sk', 'Andorra-1 [sk]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'sk', 'Andorra-2 [sk]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'sk', 'Andorra-3 [sk]', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - HU
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'hu', 'South Africa-1 [hu]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'hu', 'South Africa-2 [hu]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'hu', 'South Africa-3 [hu]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'hu', 'South Africa-4 [hu]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'hu', 'South Africa-5 [hu]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'hu', 'Andorra-1 [hu]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'hu', 'Andorra-2 [hu]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'hu', 'Andorra-3 [hu]', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - RO
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'ro', 'South Africa-1 [ro]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'ro', 'South Africa-2 [ro]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'ro', 'South Africa-3 [ro]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'ro', 'South Africa-4 [ro]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'ro', 'South Africa-5 [ro]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'ro', 'Andorra-1 [ro]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'ro', 'Andorra-2 [ro]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'ro', 'Andorra-3 [ro]', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - BG
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'bg', 'South Africa-1 [bg]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'bg', 'South Africa-2 [bg]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'bg', 'South Africa-3 [bg]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'bg', 'South Africa-4 [bg]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'bg', 'South Africa-5 [bg]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'bg', 'Andorra-1 [bg]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'bg', 'Andorra-2 [bg]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'bg', 'Andorra-3 [bg]', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - RU (ENGLISH COPY)
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'ru', 'South Africa-1 [ru]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'ru', 'South Africa-2 [ru]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'ru', 'South Africa-3 [ru]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'ru', 'South Africa-4 [ru]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'ru', 'South Africa-5 [ru]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'ru', 'Andorra-1 [ru]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'ru', 'Andorra-2 [ru]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'ru', 'Andorra-3 [ru]', 1, systimestamp, 1, systimestamp);


prompt INSERTING into TLGNC_POOL_TX - PL (ENGLISH COPY)
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'pl', 'South Africa-1 [pl]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'pl', 'South Africa-2 [pl]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'pl', 'South Africa-3 [pl]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'pl', 'South Africa-4 [pl]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'pl', 'South Africa-5 [pl]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'pl', 'Andorra-1 [pl]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'pl', 'Andorra-2 [pl]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'pl', 'Andorra-3 [pl]', 1, systimestamp, 1, systimestamp);


prompt INSERTING into TLGNC_POOL_TX - AZ (ENGLSH COPY)
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'az', 'South Africa-1 [az]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'az', 'South Africa-2 [az]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'az', 'South Africa-3 [az]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'az', 'South Africa-4 [az]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'az', 'South Africa-5 [az]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'az', 'Andorra-1 [az]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'az', 'Andorra-2 [az]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'az', 'Andorra-3 [az]', 1, systimestamp, 1, systimestamp);


prompt INSERTING into TLGNC_POOL_TX - HR (ENGLISH COPY)
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'hr', 'South Africa-1 [hr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'hr', 'South Africa-2 [hr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'hr', 'South Africa-3 [hr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'hr', 'South Africa-4 [hr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'hr', 'South Africa-5 [hr]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'hr', 'Andorra-1 [hr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'hr', 'Andorra-2 [hr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'hr', 'Andorra-3 [hr]', 1, systimestamp, 1, systimestamp);


prompt INSERTING into TLGNC_POOL_TX - SR
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'sr', 'South Africa-1 [sr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'sr', 'South Africa-2 [sr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'sr', 'South Africa-3 [sr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'sr', 'South Africa-4 [sr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'sr', 'South Africa-5 [sr]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'sr', 'Andorra-1 [sr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'sr', 'Andorra-2 [sr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'sr', 'Andorra-3 [sr]', 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX - TR
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1001, 'tr', 'South Africa-1 [tr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1002, 'tr', 'South Africa-2 [tr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1003, 'tr', 'South Africa-3 [tr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1004, 'tr', 'South Africa-4 [tr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1005, 'tr', 'South Africa-5 [tr]', 1, systimestamp, 1, systimestamp);

insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1006, 'tr', 'Andorra-1 [tr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1007, 'tr', 'Andorra-2 [tr]', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (-1008, 'tr', 'Andorra-3 [tr]', 1, systimestamp, 1, systimestamp);

commit;
