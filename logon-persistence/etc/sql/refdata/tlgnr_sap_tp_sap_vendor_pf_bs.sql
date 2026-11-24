-- $Revision$
-- $Date$
--
-- reference data from
-- /share/doc/crh/logon_2_2/spec/biz/latest/SDS_Logon_Multiple_PF_Freighters_in_one_delivery_to_be_sent_to_SAP_v1.docx,
-- section "5.3.1 Reference Data" 
--

prompt '---------- refdata/tlgnr_sap_tp_sap_vendor_pf_bs.sql ----------'

prompt INSERTING into tlgnr_sap_tp_sap_vendor_pf_bs

insert into tlgnr_sap_tp_sap_vendor_pf_bs ( sap_tp_sap_vendor_pf_cd, description, ctl_act_cd) values ('TF', 'Freight Service Agent 1', 1);
insert into tlgnr_sap_tp_sap_vendor_pf_bs ( sap_tp_sap_vendor_pf_cd, description, ctl_act_cd) values ('ZF', 'Freight Service Agent 2', 1);
insert into tlgnr_sap_tp_sap_vendor_pf_bs ( sap_tp_sap_vendor_pf_cd, description, ctl_act_cd) values ('LF', '3rd Party SAP Vendor', 1);

commit;