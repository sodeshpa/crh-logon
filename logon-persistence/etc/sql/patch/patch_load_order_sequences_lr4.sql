prompt
prompt *******************************
prompt *** Patching Logon Database ***
prompt *******************************
prompt
prompt This patch script is to be applied to logon production database in LR4

spool /temp/logon_patch_load_order_sequences_lr4.log

----------------------------------------------------------------------------------------------------------------|
-- LOGON-8698                                                                                                   |
-- Changing the IDs Counting Ranges in LOGON LQ2 and LR4                                                        |
----------------------------------------------------------------------------------------------------------------|
prompt *** START LOGON-8698 - Changing the IDs Counting Ranges in LOGON LQ2 and LR4

drop sequence  slgnc_order;
drop sequence  slgnc_order_number;
drop sequence  slgnc_load_order;
drop sequence  slgnc_load_order_item;

create sequence  slgnc_order           start with 300000     maxvalue 550000;
create sequence  slgnc_order_number    start with 32000000   maxvalue 32999999;
create sequence  slgnc_load_order      start with 3020000000 maxvalue 3029999999;
create sequence  slgnc_load_order_item start with 3002000000 maxvalue 3002999999;

grant select on slgnc_order to rl_lgn_user;
grant select on slgnc_order_number to rl_lgn_user;
grant select on slgnc_load_order to rl_lgn_user;
grant select on slgnc_load_order_item to rl_lgn_user;

prompt *** END LOGON-8698

spool off
