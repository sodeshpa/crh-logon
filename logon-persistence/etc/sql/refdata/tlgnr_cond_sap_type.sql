-- $Revision$
-- $Date$
--
-- Values based on LOGON_Interface_Price_conditions_V2.1_CDU_20090514.xls,
-- and in sync with workshop 22.6.2009 about transport conditions:
-- https://aww.adnovum.ch/twiki/bin/view/CrhPegasus/WorkshopCrh20090622
-- LOGON NGE is based on ticket 56442

prompt '---------- refdata/tlgnr_cond_sap_type.sql ----------'

prompt INSERTING into TLGNR_COND_SAP_TYPE_BS

-- Material Price Conditions
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'MP',   1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'MP',   2, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'MP',   3, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'MP',   4, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'MP',   5, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'MP',   6, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'MP',   7, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'MP',   8, 1, 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tables
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'MP',   11, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'MP',   12, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'MP',   13, 1, 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'TRf',  1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'TRv',  1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'TC',   1, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'TC',   2, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'TC',   3, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'TC',   4, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'TC',   5, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'TC',   6, 1, 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tables
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'TC',   11, 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_bs(cond_sap_type_cd, cond_logon_type_cd, evaluation_priority, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'TC',   13, 1, 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - EN

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'en', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'en', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'en', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'en', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'en', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'en', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'en', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'en', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'en', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'en', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'en', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'en', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'en', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'en', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'en', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'en', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'en', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'en', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'en', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'en', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'en', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - DE (ENGLISH COPY)

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'de', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'de', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'de', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'de', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'de', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'de', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'de', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'de', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'de', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'de', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'de', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'de', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'de', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'de', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'de', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'de', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'de', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'de', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'de', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'de', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'de', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - FR (ENGLISH COPY)

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'fr', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'fr', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'fr', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'fr', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'fr', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'fr', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'fr', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'fr', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'fr', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'fr', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'fr', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'fr', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'fr', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'fr', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'fr', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'fr', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'fr', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'fr', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'fr', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'fr', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'fr', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - IT (ENGLISH COPY)

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'it', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'it', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'it', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'it', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'it', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'it', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'it', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'it', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'it', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'it', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'it', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'it', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'it', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'it', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'it', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'it', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'it', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'it', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'it', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'it', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'it', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - ES (ENGLISH COPY)

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'es', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'es', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'es', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'es', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'es', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'es', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'es', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'es', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'es', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'es', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'es', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'es', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'es', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'es', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'es', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'es', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'es', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'es', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'es', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'es', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'es', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - CS (ENGLISH COPY)

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'cs', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'cs', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'cs', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'cs', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'cs', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'cs', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'cs', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'cs', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'cs', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'cs', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'cs', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'cs', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'cs', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'cs', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'cs', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'cs', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'cs', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'cs', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'cs', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'cs', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'cs', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - SK (ENGLISH COPY)

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'sk', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'sk', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'sk', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'sk', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'sk', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'sk', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'sk', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'sk', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'sk', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'sk', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'sk', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'sk', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'sk', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'sk', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'sk', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'sk', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'sk', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'sk', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'sk', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'sk', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'sk', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - HU (ENGLISH COPY)

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'hu', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'hu', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'hu', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'hu', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'hu', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'hu', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'hu', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'hu', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'hu', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'hu', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'hu', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'hu', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'hu', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'hu', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'hu', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'hu', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'hu', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'hu', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'hu', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'hu', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'hu', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - RO (ENGLISH COPY)

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'ro', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'ro', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'ro', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'ro', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'ro', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'ro', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'ro', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'ro', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'ro', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'ro', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'ro', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'ro', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'ro', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'ro', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'ro', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'ro', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'ro', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'ro', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'ro', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'ro', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'ro', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - BG (ENGLISH COPY)

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'bg', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'bg', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'bg', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'bg', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'bg', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'bg', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'bg', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'bg', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'bg', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'bg', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'bg', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'bg', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'bg', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'bg', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'bg', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'bg', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'bg', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'bg', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'bg', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'bg', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'bg', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - RU (ENGLISH COPY)

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'ru', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'ru', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'ru', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'ru', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'ru', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'ru', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'ru', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'ru', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'ru', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'ru', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'ru', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'ru', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'ru', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'ru', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'ru', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'ru', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'ru', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'ru', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'ru', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'ru', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'ru', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - PL (ENGLISH COPY)

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'pl', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'pl', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'pl', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'pl', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'pl', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'pl', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'pl', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'pl', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'pl', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'pl', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'pl', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'pl', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'pl', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'pl', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'pl', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'pl', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'pl', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'pl', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'pl', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'pl', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'pl', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - AZ (ENGLISH COPY)

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'az', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'az', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'az', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'az', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'az', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'az', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'az', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'az', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'az', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'az', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'az', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'az', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'az', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'az', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'az', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'az', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'az', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'az', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'az', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'az', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'az', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - HR (ENGLISH COPY)

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'hr', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'hr', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'hr', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'hr', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'hr', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'hr', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'hr', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'hr', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'hr', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'hr', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'hr', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'hr', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'hr', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'hr', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'hr', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'hr', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'hr', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'hr', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'hr', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'hr', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'hr', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - SR (ENGLISH COPY)

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'sr', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'sr', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'sr', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'sr', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'sr', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'sr', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'sr', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'sr', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'sr', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'sr', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'sr', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'sr', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'sr', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571',  'sr', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'sr', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503',  'sr', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527',  'sr', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'sr', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'sr', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'sr', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'sr', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

prompt INSERTING into TLGNR_COND_SAP_TYPE_TX - TR

-- Material Price Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A584', 'tr', 'MP: sorg - mat - condgrp2 - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A638', 'tr', 'MP: sorg - mat - (billto) - cust - shipto'   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A639', 'tr', 'MP: sorg - mat - (billto) - cust'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A640', 'tr', 'MP: sorg - mat - cust'                       , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A998', 'tr', 'MP: sorg - mat - condgrp2 - zone'            , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A936', 'tr', 'MP: sorg - mat - condgrp2'                   , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR6/A641', 'tr', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZP20/A641', 'tr', 'MP: sorg - mat'                              , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A624', 'tr', 'MP: sorg - mat - distchan - cust - plant' , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A005', 'tr', 'MP: sorg - mat - distchan - cust'                               , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZPR1/A004', 'tr', 'MP: sorg - mat - distchan'                                      , 1, systimestamp, 1, systimestamp);

-- Transport Cost Conditions
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE2/A526', 'tr', 'TRf: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE3/A526', 'tr', 'TRv: sp - sorg - mat'                        , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A571', 'tr', 'TC: sp - shipto - vendor - main item ref mat', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A811', 'tr', 'TC: sp - shipto - sorg - vendor'             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A503', 'tr', 'TC: sp - shipto - mat pricing group - vendor', 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTE/A527', 'tr', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZT2B/A527', 'tr', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZBF2/A527', 'tr', 'TC: sp - shipto'                             , 1, systimestamp, 1, systimestamp);

--Logon NGE SAP import needed tx entries
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZKF2/A694', 'tr', 'TC: sp (supply plant!) - sorg - distchan - shipto - vendor'                             , 1, systimestamp, 1, systimestamp);
insert into tlgnr_cond_sap_type_tx(cond_sap_type_cd, lang_cd, txt, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values ('ZTF0/A599', 'tr', 'TC: sp (supply plant!) - sorg - distchan - shipto - (mvgr2)'                            , 1, systimestamp, 1, systimestamp);

commit;

