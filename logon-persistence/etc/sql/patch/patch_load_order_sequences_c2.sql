prompt
prompt ****************************************************************
prompt *** Patching Logon Database (fix versions: 2.1.9.0 and 2.2.0.1)
prompt ****************************************************************
prompt
prompt This patch script is to be applied to logon production database in LP2

----------------------------------------------------------------------------------------------------------------|
-- LOGON-6721                                                                                                   |
-- LRQ 073.3 Cross Cluster LDS Connectivity                                                                     |
--                                                                                                              |
-- The goal is to have cluster specific load order IDs which we send to the LDS in the                          |
-- loadOrderNotification request.                                                                               |
----------------------------------------------------------------------------------------------------------------|
prompt *** START LOGON-6721 - LRQ 073.3 Cross Cluster LDS Connectivity

drop sequence   slgnc_load_order;
create sequence slgnc_load_order start with 3000000000 maxvalue 3999999999;
grant select on slgnc_load_order to rl_lgn_user;

drop sequence   slgnc_load_order_item;
create sequence slgnc_load_order_item start with 3000000000 maxvalue 3999999999;
grant select on slgnc_load_order_item to rl_lgn_user;

prompt *** END LOGON-6721
