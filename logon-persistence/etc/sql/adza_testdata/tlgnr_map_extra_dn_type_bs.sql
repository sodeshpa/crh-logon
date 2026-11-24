prompt '---------- adza_testdata/tlgnr_map_extra_dn_type_bs.sql ----------'

-- This table helps to decide what kind of delivery note is needed to be generated. 
-- Right now the possible cases are DN only, DN + Cash Sales Invoice, DN + Export Invoice

--for every sales area of ADZA insert an entry:
--ZCSA (Cash Sales),
--Sales Area
--print Cash Sales

insert into tlgnr_map_extra_dn_type_bs (map_extra_dn_type_id, sap_contract_type_cd, sarea_id, print_cash_sales, print_export_invoice, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_act_cd)
  select slgnr_map_extra_dn_type_bs.nextval, 'ZCSA', sales_area.sarea_id, 1, 0, 1, systimestamp, 1, systimestamp, 1
  from tlgnc_sarea sales_area where sarea_display_longname like 'AD0%';

--for every sales area of ADZA insert an entry:
--ZCEX (Export Invoice)
--Sales Area
--print Export Invoice 

insert into tlgnr_map_extra_dn_type_bs (map_extra_dn_type_id, sap_contract_type_cd, sarea_id, print_cash_sales, print_export_invoice, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_act_cd)
  select slgnr_map_extra_dn_type_bs.nextval, 'ZCEX', sales_area.sarea_id, 0, 1, 1, systimestamp, 1, systimestamp, 1
  from tlgnc_sarea sales_area where sarea_display_longname like 'AD0%';

