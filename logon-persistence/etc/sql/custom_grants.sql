PROMPT *****************************************************************
PROMPT Grant LOGON import packages to ulgnsap user                     *
PROMPT *****************************************************************

GRANT EXECUTE ON logon_log TO ulgnsap;

-- LOGON EBM 2.0 import scripts
GRANT EXECUTE ON sapimport_bpn TO ulgnsap;
GRANT EXECUTE ON sapimport_mn TO ulgnsap;
GRANT EXECUTE ON sapimport_mnn TO ulgnsap;
GRANT EXECUTE ON sapimport_sdn TO ulgnsap;
GRANT EXECUTE ON sapimport_vehn TO ulgnsap;
GRANT EXECUTE ON sapimport_vn TO ulgnsap;

PROMPT *****************************************************************
PROMPT Grant login related access to ulgn03 which is in rl_lgn_login   *
PROMPT *****************************************************************

grant select on TLGNC_PERSON to rl_lgn_login;
grant select on TLGNU_FUNC to rl_lgn_login;
grant select on TLGNU_ROLE to rl_lgn_login;
grant select on TLGNU_ROLE_FUNC to rl_lgn_login;
grant select on TLGNU_USER to rl_lgn_login;
grant select on TLGNU_USER_ROLE to rl_lgn_login;
grant select, insert, update, delete on TLGNU_USER to rl_lgn_login;
grant select on slgnu_user to rl_lgn_login;
grant select on TLGNC_PERSON to rl_lgn_login;
grant select, insert, update, delete on TLGNU_USER_ACTIVITY to rl_lgn_login;
grant select on slgnu_user_activity to rl_lgn_login;
grant select on VLGNU_USER_ACTIVITY_LOG to rl_lgn_login;
grant select on TLGNR_USER_ACTIVITY_ACTION_BS to rl_lgn_login;
grant select on TLGNR_USER_ACTIVITY_ACTION_TX to rl_lgn_login;
grant select on TLGNC_CONFIG_LOGIN_MODULE to rl_lgn_login;
grant select on TLGNR_TEXT_BS to rl_lgn_login;
grant select on TLGNR_TEXT_TX to rl_lgn_login;

PROMPT *****************************************************************
PROMPT Grant access to ulgn02 for manually created DB objects          *
PROMPT *****************************************************************
grant select on slgnc_order_number to rl_lgn_user;
grant select on slgnc_custorder_number to rl_lgn_user;

grant select on SLGNC_LOAD_ORDER to rl_lgn_user;
grant select on SLGNC_LOAD_ORDER_ITEM to rl_lgn_user;

grant select on slgnc_mopset_lang to rl_lgn_user;
grant select on slgnc_mopset_func to rl_lgn_user;
grant select on slgnc_mopset_truck_maintenance to rl_lgn_user;
grant select on slgnc_mopset_pda_error_message to rl_lgn_user;
grant select on slgnc_mopset_pda_market_info to rl_lgn_user;
grant select on slgnc_mopset_currency to rl_lgn_user;
grant select on slgnc_mopset_fuel_type to rl_lgn_user;
grant select on slgnc_mopset_wait_reason_sp to rl_lgn_user;
grant select on slgnc_mopset_wait_reason_st to rl_lgn_user;
grant select on slgnc_mopset_rfidobject_type to rl_lgn_user;

grant select on slgnc_logon_eventid to rl_lgn_user;

grant execute on findfirstfreepdauserno to rl_lgn_user;

grant select on SLGNC_LOGON_NGE_FLAT_FILE_ID to rl_lgn_user;
grant select on SLGNC_EXT_ORDER_NUMBER to rl_lgn_user;
grant select on SLGNC_CIBEF102_ICR to rl_lgn_user;
grant select on SLGNC_LOAD_NUMBER to rl_lgn_user;
grant select on SLGNC_ASSIGNMENT_NUMBER to rl_lgn_user;

grant execute on ulgn01.upsert_planning_contract_items to ulgn01,ulgn02,ulgn03,ulgnsap;
grant execute on ulgn01.update_planning_mat_sarea to ulgn01,ulgn02,ulgn03,ulgnsap;
grant execute on ulgn01.update_shipto_bp_meta to ulgn01,ulgn02,ulgn03,ulgnsap;

grant execute on is_existing_instance to rl_lgn_user;

grant execute on ulgn01.FILTER_TRUCK_IDS to ulgn01,ulgn02,ulgn03,ulgnsap;
grant execute on ulgn01.FETCH_TRUCK_CHANGES_FOR_DISPO to ulgn01,ulgn02,ulgn03,ulgnsap;
grant execute on auto_init_shipto_sarea_restr to ulgn01,ulgn02,ulgn03;
grant execute on delete_invalid_restrictions to ulgn01,ulgn02,ulgn03;