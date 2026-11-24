-- $Revision$
-- $Date$
--
-- initial data from
-- file:///share/doc/crh/logon_2_2/spec/tech/latest/ifc_sap_receive/Interface_Foundation_And_Reference_Tables.pdf

prompt '---------- refdata/tlgnr_sap_incoterm.sql ----------'

set define off
set serveroutput on

-------------------------------------------------------------------------------
-- is_different_varchar2
-------------------------------------------------------------------------------
-- compares two varchars.
-- They are identical either if a) both are null, b) they are equal by "="
-- They are different if a) either is null and the other not, b) different by "="
create or replace function is_different_varchar2 (
  i_a in varchar2,
  i_b in varchar2)
  return number is
  begin
    if ((i_a != i_b)
        or (i_a is null and i_b is not null)
        or (i_a is not null and i_b is null)) then
      return 1;
    end if;
    return 0;
  end;
/
show errors;

-------------------------------------------------------------------------------
-- is_different_number
-------------------------------------------------------------------------------
-- compares two numbers.
-- They are identical either if a) both are null, b) they are equal by "="
-- They are different if a) either is null and the other not, b) different by "="
create or replace function is_different_number (
  i_a in number,
  i_b in number)
  return number is
  begin
    if ((i_a != i_b)
        or (i_a is null and i_b is not null)
        or (i_a is not null and i_b is null)) then
      return 1;
    end if;
    return 0;
  end;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_sap_incoterm_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_sap_incoterm_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_sap_incoterm_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_sap_incoterm_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.sap_incoterm_cd)
    into v_exists_flag
    from tlgnr_sap_incoterm_bs bs
    where bs.sap_incoterm_cd = p_sap_incoterm_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_sap_incoterm_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_sap_incoterm_bs set
/* sap_incoterm_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          sap_incoterm_cd = p_sap_incoterm_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_sap_incoterm_cd || ').');
      end if;
    else
      insert into tlgnr_sap_incoterm_bs (
        sap_incoterm_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_sap_incoterm_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_sap_incoterm_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_sap_incoterm_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_sap_incoterm_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_sap_incoterm_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_sap_incoterm_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.sap_incoterm_cd) into v_exists_bs_flag
    from tlgnr_sap_incoterm_bs bs
    where bs.sap_incoterm_cd = p_sap_incoterm_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.sap_incoterm_cd) into v_exists_flag
      from tlgnr_sap_incoterm_tx tx
      where tx.sap_incoterm_cd = p_sap_incoterm_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_sap_incoterm_tx set
/* p_sap_incoterm_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          sap_incoterm_cd = p_sap_incoterm_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_sap_incoterm_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_sap_incoterm_tx (
          sap_incoterm_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_sap_incoterm_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_sap_incoterm_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_sap_incoterm_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_SAP_INCOTERM_BS

execute upsert_bs('EXW', 1);
execute upsert_bs('FCA', 1);
execute upsert_bs('FAS', 1);
execute upsert_bs('FOB', 1);
execute upsert_bs('CFR', 1);
execute upsert_bs('CIF', 1);
execute upsert_bs('CPT', 1);
execute upsert_bs('CIP', 1);
execute upsert_bs('DAF', 1);
execute upsert_bs('DES', 1);
execute upsert_bs('DEQ', 1);
execute upsert_bs('DDU', 1);
execute upsert_bs('DDP', 1);
execute upsert_bs('DAP', 1);
execute upsert_bs('DAT', 1);

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - EN
execute upsert_tx('EXW', 'en', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'en', 'Free Carrier');
execute upsert_tx('FAS', 'en', 'Free Alongside Ship');
execute upsert_tx('FOB', 'en', 'Free on bord');
execute upsert_tx('CFR', 'en', 'Cost and Freight');
execute upsert_tx('CIF', 'en', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'en', 'Carriage Paid to');
execute upsert_tx('CIP', 'en', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'en', 'Delivered at frontier');
execute upsert_tx('DES', 'en', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'en', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'en', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'en', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'en', 'Delivered at Place');
execute upsert_tx('DAT', 'en', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - DE
execute upsert_tx('EXW', 'de', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'de', 'Free Carrier');
execute upsert_tx('FAS', 'de', 'Free Alongside Ship');
execute upsert_tx('FOB', 'de', 'Free on bord');
execute upsert_tx('CFR', 'de', 'Cost and Freight');
execute upsert_tx('CIF', 'de', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'de', 'Carriage Paid to');
execute upsert_tx('CIP', 'de', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'de', 'Delivered at frontier');
execute upsert_tx('DES', 'de', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'de', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'de', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'de', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'de', 'Delivered at Place');
execute upsert_tx('DAT', 'de', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - FR
execute upsert_tx('EXW', 'fr', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'fr', 'Free Carrier');
execute upsert_tx('FAS', 'fr', 'Free Alongside Ship');
execute upsert_tx('FOB', 'fr', 'Free on bord');
execute upsert_tx('CFR', 'fr', 'Cost and Freight');
execute upsert_tx('CIF', 'fr', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'fr', 'Carriage Paid to');
execute upsert_tx('CIP', 'fr', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'fr', 'Delivered at frontier');
execute upsert_tx('DES', 'fr', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'fr', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'fr', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'fr', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'fr', 'Delivered at Place');
execute upsert_tx('DAT', 'fr', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - IT
execute upsert_tx('EXW', 'it', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'it', 'Free Carrier');
execute upsert_tx('FAS', 'it', 'Free Alongside Ship');
execute upsert_tx('FOB', 'it', 'Free on bord');
execute upsert_tx('CFR', 'it', 'Cost and Freight');
execute upsert_tx('CIF', 'it', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'it', 'Carriage Paid to');
execute upsert_tx('CIP', 'it', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'it', 'Delivered at frontier');
execute upsert_tx('DES', 'it', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'it', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'it', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'it', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'it', 'Delivered at Place');
execute upsert_tx('DAT', 'it', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - ES
execute upsert_tx('EXW', 'es', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'es', 'Free Carrier');
execute upsert_tx('FAS', 'es', 'Free Alongside Ship');
execute upsert_tx('FOB', 'es', 'Free on bord');
execute upsert_tx('CFR', 'es', 'Cost and Freight');
execute upsert_tx('CIF', 'es', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'es', 'Carriage Paid to');
execute upsert_tx('CIP', 'es', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'es', 'Delivered at frontier');
execute upsert_tx('DES', 'es', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'es', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'es', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'es', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'es', 'Delivered at Place');
execute upsert_tx('DAT', 'es', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - CS
execute upsert_tx('EXW', 'cs', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'cs', 'Free Carrier');
execute upsert_tx('FAS', 'cs', 'Free Alongside Ship');
execute upsert_tx('FOB', 'cs', 'Free on bord');
execute upsert_tx('CFR', 'cs', 'Cost and Freight');
execute upsert_tx('CIF', 'cs', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'cs', 'Carriage Paid to');
execute upsert_tx('CIP', 'cs', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'cs', 'Delivered at frontier');
execute upsert_tx('DES', 'cs', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'cs', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'cs', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'cs', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'cs', 'Delivered at Place');
execute upsert_tx('DAT', 'cs', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - SK
execute upsert_tx('EXW', 'sk', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'sk', 'Free Carrier');
execute upsert_tx('FAS', 'sk', 'Free Alongside Ship');
execute upsert_tx('FOB', 'sk', 'Free on bord');
execute upsert_tx('CFR', 'sk', 'Cost and Freight');
execute upsert_tx('CIF', 'sk', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'sk', 'Carriage Paid to');
execute upsert_tx('CIP', 'sk', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'sk', 'Delivered at frontier');
execute upsert_tx('DES', 'sk', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'sk', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'sk', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'sk', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'sk', 'Delivered at Place');
execute upsert_tx('DAT', 'sk', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - HU
execute upsert_tx('EXW', 'hu', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'hu', 'Free Carrier');
execute upsert_tx('FAS', 'hu', 'Free Alongside Ship');
execute upsert_tx('FOB', 'hu', 'Free on bord');
execute upsert_tx('CFR', 'hu', 'Cost and Freight');
execute upsert_tx('CIF', 'hu', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'hu', 'Carriage Paid to');
execute upsert_tx('CIP', 'hu', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'hu', 'Delivered at frontier');
execute upsert_tx('DES', 'hu', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'hu', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'hu', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'hu', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'hu', 'Delivered at Place');
execute upsert_tx('DAT', 'hu', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - RO
execute upsert_tx('EXW', 'ro', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'ro', 'Free Carrier');
execute upsert_tx('FAS', 'ro', 'Free Alongside Ship');
execute upsert_tx('FOB', 'ro', 'Free on bord');
execute upsert_tx('CFR', 'ro', 'Cost and Freight');
execute upsert_tx('CIF', 'ro', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'ro', 'Carriage Paid to');
execute upsert_tx('CIP', 'ro', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'ro', 'Delivered at frontier');
execute upsert_tx('DES', 'ro', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'ro', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'ro', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'ro', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'ro', 'Delivered at Place');
execute upsert_tx('DAT', 'ro', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - BG
execute upsert_tx('EXW', 'bg', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'bg', 'Free Carrier');
execute upsert_tx('FAS', 'bg', 'Free Alongside Ship');
execute upsert_tx('FOB', 'bg', 'Free on bord');
execute upsert_tx('CFR', 'bg', 'Cost and Freight');
execute upsert_tx('CIF', 'bg', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'bg', 'Carriage Paid to');
execute upsert_tx('CIP', 'bg', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'bg', 'Delivered at frontier');
execute upsert_tx('DES', 'bg', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'bg', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'bg', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'bg', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'bg', 'Delivered at Place');
execute upsert_tx('DAT', 'bg', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - RU
execute upsert_tx('EXW', 'ru', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'ru', 'Free Carrier');
execute upsert_tx('FAS', 'ru', 'Free Alongside Ship');
execute upsert_tx('FOB', 'ru', 'Free on bord');
execute upsert_tx('CFR', 'ru', 'Cost and Freight');
execute upsert_tx('CIF', 'ru', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'ru', 'Carriage Paid to');
execute upsert_tx('CIP', 'ru', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'ru', 'Delivered at frontier');
execute upsert_tx('DES', 'ru', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'ru', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'ru', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'ru', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'ru', 'Delivered at Place');
execute upsert_tx('DAT', 'ru', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - PL
execute upsert_tx('EXW', 'pl', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'pl', 'Free Carrier');
execute upsert_tx('FAS', 'pl', 'Free Alongside Ship');
execute upsert_tx('FOB', 'pl', 'Free on bord');
execute upsert_tx('CFR', 'pl', 'Cost and Freight');
execute upsert_tx('CIF', 'pl', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'pl', 'Carriage Paid to');
execute upsert_tx('CIP', 'pl', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'pl', 'Delivered at frontier');
execute upsert_tx('DES', 'pl', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'pl', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'pl', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'pl', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'pl', 'Delivered at Place');
execute upsert_tx('DAT', 'pl', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - AZ
execute upsert_tx('EXW', 'az', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'az', 'Free Carrier');
execute upsert_tx('FAS', 'az', 'Free Alongside Ship');
execute upsert_tx('FOB', 'az', 'Free on bord');
execute upsert_tx('CFR', 'az', 'Cost and Freight');
execute upsert_tx('CIF', 'az', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'az', 'Carriage Paid to');
execute upsert_tx('CIP', 'az', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'az', 'Delivered at frontier');
execute upsert_tx('DES', 'az', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'az', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'az', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'az', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'az', 'Delivered at Place');
execute upsert_tx('DAT', 'az', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - HR
execute upsert_tx('EXW', 'hr', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'hr', 'Free Carrier');
execute upsert_tx('FAS', 'hr', 'Free Alongside Ship');
execute upsert_tx('FOB', 'hr', 'Free on bord');
execute upsert_tx('CFR', 'hr', 'Cost and Freight');
execute upsert_tx('CIF', 'hr', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'hr', 'Carriage Paid to');
execute upsert_tx('CIP', 'hr', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'hr', 'Delivered at frontier');
execute upsert_tx('DES', 'hr', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'hr', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'hr', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'hr', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'hr', 'Delivered at Place');
execute upsert_tx('DAT', 'hr', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - SR
execute upsert_tx('EXW', 'sr', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'sr', 'Free Carrier');
execute upsert_tx('FAS', 'sr', 'Free Alongside Ship');
execute upsert_tx('FOB', 'sr', 'Free on bord');
execute upsert_tx('CFR', 'sr', 'Cost and Freight');
execute upsert_tx('CIF', 'sr', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'sr', 'Carriage Paid to');
execute upsert_tx('CIP', 'sr', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'sr', 'Delivered at frontier');
execute upsert_tx('DES', 'sr', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'sr', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'sr', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'sr', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'sr', 'Delivered at Place');
execute upsert_tx('DAT', 'sr', 'Delivered at Terminal');

prompt INSERTING into TLGNR_SAP_INCOTERM_TX - TR
execute upsert_tx('EXW', 'tr', 'Ex Works (Pickup)');
execute upsert_tx('FCA', 'tr', 'Free Carrier');
execute upsert_tx('FAS', 'tr', 'Free Alongside Ship');
execute upsert_tx('FOB', 'tr', 'Free on bord');
execute upsert_tx('CFR', 'tr', 'Cost and Freight');
execute upsert_tx('CIF', 'tr', 'Cost, insurance and freight');
execute upsert_tx('CPT', 'tr', 'Carriage Paid to');
execute upsert_tx('CIP', 'tr', 'Carriage and insurance Paid to');
execute upsert_tx('DAF', 'tr', 'Delivered at frontier');
execute upsert_tx('DES', 'tr', 'Delivered Ex Ship');
execute upsert_tx('DEQ', 'tr', 'Delivered Ex Quay');
execute upsert_tx('DDU', 'tr', 'Delivered Duty Unpaid');
execute upsert_tx('DDP', 'tr', 'Delivered Duty Paid');
execute upsert_tx('DAP', 'tr', 'Delivered at Place');
execute upsert_tx('DAT', 'tr', 'Delivered at Terminal');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

