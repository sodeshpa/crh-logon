prompt ************************************************************************
prompt *** BEGIN CRHLOGON-1349: New rules for update_orders cron.
prompt ************************************************************************

prompt *** Add the force_to_sap_flag field to the tlgnc_sarea table
alter table tlgnc_sarea add force_to_sap_flag number(1,0) null;
update tlgnc_sarea set force_to_sap_flag = 0;
alter table tlgnc_sarea modify force_to_sap_flag number(1,0) not null;

prompt ************************************************************************
prompt *** END CRHLOGON-1349: New rules for update_orders cron.
prompt ************************************************************************