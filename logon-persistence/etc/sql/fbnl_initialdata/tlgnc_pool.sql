-- based on ticket LOGON-6105

prompt '---------- fbnl_initialdata/tlgnc_pool.sql ----------'
prompt INSERTING into TLGNC_POOL
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2024, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2025, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2026, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2027, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2028, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2029, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2030, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2031, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2032, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2033, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2034, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2035, '10', 1, 1, systimestamp, 1, systimestamp);
-- 3 belgium pools
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2036, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2037, '10', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool (pool_id, sap_trsp_mode_cd, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2038, '30', 1, 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNC_POOL_TX 
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2024, 'de', 'FR - VRAC1_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2024, 'fr', 'FR - VRAC1_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2024, 'en', 'FR - VRAC1_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2024, 'es', 'FR - VRAC1_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2024, 'it', 'FR - VRAC1_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2025, 'de', 'FR - VRAC2_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2025, 'fr', 'FR - VRAC2_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2025, 'en', 'FR - VRAC2_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2025, 'es', 'FR - VRAC2_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2025, 'it', 'FR - VRAC2_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2026, 'de', 'FR - VRAC3_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2026, 'fr', 'FR - VRAC3_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2026, 'en', 'FR - VRAC3_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2026, 'es', 'FR - VRAC3_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2026, 'it', 'FR - VRAC3_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2027, 'de', 'FR - SAC_CRH_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2027, 'fr', 'FR - SAC_CRH_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2027, 'en', 'FR - SAC_CRH_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2027, 'es', 'FR - SAC_CRH_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2027, 'it', 'FR - SAC_CRH_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2028, 'de', 'FR - SAC_VDN_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2028, 'fr', 'FR - SAC_VDN_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2028, 'en', 'FR - SAC_VDN_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2028, 'es', 'FR - SAC_VDN_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2028, 'it', 'FR - SAC_VDN_EST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2029, 'de', 'FR - VRAC_IDF', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2029, 'fr', 'FR - VRAC_IDF', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2029, 'en', 'FR - VRAC_IDF', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2029, 'es', 'FR - VRAC_IDF', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2029, 'it', 'FR - VRAC_IDF', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2030, 'de', 'FR - VRAC_NORD', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2030, 'fr', 'FR - VRAC_NORD', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2030, 'en', 'FR - VRAC_NORD', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2030, 'es', 'FR - VRAC_NORD', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2030, 'it', 'FR - VRAC_NORD', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2031, 'de', 'FR - SAC_CRH_DVNO', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2031, 'fr', 'FR - SAC_CRH_DVNO', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2031, 'en', 'FR - SAC_CRH_DVNO', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2031, 'es', 'FR - SAC_CRH_DVNO', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2031, 'it', 'FR - SAC_CRH_DVNO', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2032, 'de', 'FR - SAC_VDN_DVNO', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2032, 'fr', 'FR - SAC_VDN_DVNO', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2032, 'en', 'FR - SAC_VDN_DVNO', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2032, 'es', 'FR - SAC_VDN_DVNO', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2032, 'it', 'FR - SAC_VDN_DVNO', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2033, 'de', 'FR - VRAC1_OUEST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2033, 'fr', 'FR - VRAC1_OUEST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2033, 'en', 'FR - VRAC1_OUEST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2033, 'es', 'FR - VRAC1_OUEST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2033, 'it', 'FR - VRAC1_OUEST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2034, 'de', 'FR - SAC_CRH_OUEST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2034, 'fr', 'FR - SAC_CRH_OUEST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2034, 'en', 'FR - SAC_CRH_OUEST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2034, 'es', 'FR - SAC_CRH_OUEST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2034, 'it', 'FR - SAC_CRH_OUEST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2035, 'de', 'FR - SAC_VDN_OUEST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2035, 'fr', 'FR - SAC_VDN_OUEST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2035, 'en', 'FR - SAC_VDN_OUEST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2035, 'es', 'FR - SAC_VDN_OUEST', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2035, 'it', 'FR - SAC_VDN_OUEST', 1, systimestamp, 1, systimestamp);

-- 3 belgium
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2036, 'de', 'BE - VRAC', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2036, 'fr', 'BE - VRAC', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2036, 'en', 'BE - VRAC', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2036, 'es', 'BE - VRAC', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2036, 'it', 'BE - VRAC', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2037, 'de', 'BE - SAC', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2037, 'fr', 'BE - SAC', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2037, 'en', 'BE - SAC', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2037, 'es', 'BE - SAC', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2037, 'it', 'BE - SAC', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2038, 'de', 'BE - SHIP', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2038, 'fr', 'BE - SHIP', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2038, 'en', 'BE - SHIP', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2038, 'es', 'BE - SHIP', 1, systimestamp, 1, systimestamp);
insert into tlgnc_pool_tx (pool_id, lang_cd, name, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2038, 'it', 'BE - SHIP', 1, systimestamp, 1, systimestamp);



commit;
