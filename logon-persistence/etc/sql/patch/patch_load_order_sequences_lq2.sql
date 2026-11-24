prompt
prompt *******************************
prompt *** Patching Logon Database ***
prompt *******************************
prompt
prompt This patch script is to be applied to logon production database in LQ2

spool /temp/logon_patch_load_order_sequences_lq2.log

----------------------------------------------------------------------------------------------------------------|
-- LOGON-8698                                                                                                   |
-- Changing the IDs Counting Ranges in LOGON LQ2 and LR4                                                        |
----------------------------------------------------------------------------------------------------------------|
prompt *** START LOGON-8698 - Changing the IDs Counting Ranges in LOGON LQ2 and LR4

drop sequence  slgnc_order;
drop sequence  slgnc_order_number;
drop sequence  slgnc_load_order;
drop sequence  slgnc_load_order_item;

create sequence  slgnc_order           start with 183000  maxvalue    299999;
create sequence  slgnc_order_number    start with 31000000   maxvalue 31999999;
create sequence  slgnc_load_order      start with 3010000000 maxvalue 3019999999;
create sequence  slgnc_load_order_item start with 3001000000 maxvalue 3001999999;

grant select on slgnc_order to rl_lgn_user;
grant select on slgnc_order_number to rl_lgn_user;
grant select on slgnc_load_order to rl_lgn_user;
grant select on slgnc_load_order_item to rl_lgn_user;

prompt *** END LOGON-8698

spool off
