-- $Revision$
-- $Date$
--
-- initial data from
-- https://aww.adnovum.ch/jira/browse/LOGON-6305
prompt '---------- refdata/tlgnr_incoterm_deliv_type.sql ----------'
prompt INSERTING into TLGNR_INCOTERM_DELIV_TYPE_BS

-- *************************************************************************** 
-- *** Procedure 'merge_incoterm_deliv_type_bs'
-- *************************************************************************** 
--
-- Allows given delivery type for given incoterm in all existing sarea.
-- This works without error even if some of the records are already inserted.
--
create or replace procedure merge_incoterm_deliv_type_bs(
    p_sap_incoterm_cd  in varchar2,
    p_delivery_type_cd in number) is
begin
  dbms_output.enable(1000000);
  merge into tlgnr_incoterm_deliv_type_bs old_lines
  using
  (select sarea_id, p_sap_incoterm_cd as sap_incoterm_cd, p_delivery_type_cd as delivery_type_cd 
    from    tlgnc_sarea
  ) new_lines
  on (new_lines.sarea_id = old_lines.sarea_id and 
    new_lines.sap_incoterm_cd = old_lines.sap_incoterm_cd and 
    new_lines.delivery_type_cd = old_lines.delivery_type_cd)
  when not matched then
    insert (sarea_id          , incoterm_deliv_type_cd              , sap_incoterm_cd  , delivery_type_cd  , ctl_act_cd, ctl_cre_uid, ctl_cre_ts  , ctl_mod_uid, ctl_mod_ts  ) values
           (new_lines.sarea_id, slgnr_incoterm_deliv_type_bs.nextval, p_sap_incoterm_cd, p_delivery_type_cd, 1         , 1          , systimestamp, 1          , systimestamp);
  dbms_output.put_line ('MERGED ' || SQL%ROWCOUNT ||' rows into tlgnr_incoterm_deliv_type_bs (' 
    || 'sap_incoterm_cd = "' || p_sap_incoterm_cd || '", ' 
    || 'delivery_type_cd = ' || p_delivery_type_cd || ');');
end;
/
-- EXW
execute merge_incoterm_deliv_type_bs('EXW', 5);
execute merge_incoterm_deliv_type_bs('EXW', 8);
-- FCA
execute merge_incoterm_deliv_type_bs('FCA', 1);
execute merge_incoterm_deliv_type_bs('FCA', 2);
execute merge_incoterm_deliv_type_bs('FCA', 3);
execute merge_incoterm_deliv_type_bs('FCA', 4);
execute merge_incoterm_deliv_type_bs('FCA', 6);
execute merge_incoterm_deliv_type_bs('FCA', 7);
execute merge_incoterm_deliv_type_bs('FCA', 8);
-- FAS
execute merge_incoterm_deliv_type_bs('FAS', 1);
execute merge_incoterm_deliv_type_bs('FAS', 2);
execute merge_incoterm_deliv_type_bs('FAS', 3);
execute merge_incoterm_deliv_type_bs('FAS', 4);
execute merge_incoterm_deliv_type_bs('FAS', 6);
execute merge_incoterm_deliv_type_bs('FAS', 7);
execute merge_incoterm_deliv_type_bs('FAS', 8);
-- FOB
execute merge_incoterm_deliv_type_bs('FOB', 5);
execute merge_incoterm_deliv_type_bs('FOB', 8);
-- CFR
execute merge_incoterm_deliv_type_bs('CFR', 1);
execute merge_incoterm_deliv_type_bs('CFR', 2);
execute merge_incoterm_deliv_type_bs('CFR', 3);
execute merge_incoterm_deliv_type_bs('CFR', 4);
execute merge_incoterm_deliv_type_bs('CFR', 6);
execute merge_incoterm_deliv_type_bs('CFR', 7);
execute merge_incoterm_deliv_type_bs('CFR', 8);
-- CIF
execute merge_incoterm_deliv_type_bs('CIF', 1);
execute merge_incoterm_deliv_type_bs('CIF', 2);
execute merge_incoterm_deliv_type_bs('CIF', 3);
execute merge_incoterm_deliv_type_bs('CIF', 4);
execute merge_incoterm_deliv_type_bs('CIF', 6);
execute merge_incoterm_deliv_type_bs('CIF', 7);
execute merge_incoterm_deliv_type_bs('CIF', 8);
-- CPT
execute merge_incoterm_deliv_type_bs('CPT', 1);
execute merge_incoterm_deliv_type_bs('CPT', 2);
execute merge_incoterm_deliv_type_bs('CPT', 3);
execute merge_incoterm_deliv_type_bs('CPT', 4);
execute merge_incoterm_deliv_type_bs('CPT', 6);
execute merge_incoterm_deliv_type_bs('CPT', 7);
execute merge_incoterm_deliv_type_bs('CPT', 8);
-- CIP
execute merge_incoterm_deliv_type_bs('CIP', 1);
execute merge_incoterm_deliv_type_bs('CIP', 2);
execute merge_incoterm_deliv_type_bs('CIP', 3);
execute merge_incoterm_deliv_type_bs('CIP', 4);
execute merge_incoterm_deliv_type_bs('CIP', 6);
execute merge_incoterm_deliv_type_bs('CIP', 7);
execute merge_incoterm_deliv_type_bs('CIP', 8);
-- DAF
execute merge_incoterm_deliv_type_bs('DAF', 8);
-- DES
execute merge_incoterm_deliv_type_bs('DES', 8);
-- DEQ
execute merge_incoterm_deliv_type_bs('DEQ', 8);
-- DDU
execute merge_incoterm_deliv_type_bs('DDU', 8);
-- DDP
execute merge_incoterm_deliv_type_bs('DDP', 1);
execute merge_incoterm_deliv_type_bs('DDP', 2);
execute merge_incoterm_deliv_type_bs('DDP', 3);
execute merge_incoterm_deliv_type_bs('DDP', 4);
execute merge_incoterm_deliv_type_bs('DDP', 6);
execute merge_incoterm_deliv_type_bs('DDP', 7);
execute merge_incoterm_deliv_type_bs('DDP', 8);
-- DAP
execute merge_incoterm_deliv_type_bs('DAP', 1);
execute merge_incoterm_deliv_type_bs('DAP', 2);
execute merge_incoterm_deliv_type_bs('DAP', 3);
execute merge_incoterm_deliv_type_bs('DAP', 4);
execute merge_incoterm_deliv_type_bs('DAP', 6);
execute merge_incoterm_deliv_type_bs('DAP', 7);
execute merge_incoterm_deliv_type_bs('DAP', 8);
-- DAT
execute merge_incoterm_deliv_type_bs('DAT', 1);
execute merge_incoterm_deliv_type_bs('DAT', 2);
execute merge_incoterm_deliv_type_bs('DAT', 3);
execute merge_incoterm_deliv_type_bs('DAT', 4);
execute merge_incoterm_deliv_type_bs('DAT', 6);
execute merge_incoterm_deliv_type_bs('DAT', 7);
execute merge_incoterm_deliv_type_bs('DAT', 8);
-- FCA
execute merge_incoterm_deliv_type_bs('FCA', 5);

commit;
