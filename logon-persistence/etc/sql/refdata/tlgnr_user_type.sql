-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_user_type.sql ----------'

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
-- tlgnr_user_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_user_type_cd   in varchar2,
  p_ctl_act_cd                in number,
  p_function_id in number)
is
  v_table_name    varchar2(48) := 'tlgnr_user_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_user_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.user_type_cd)
    into v_exists_flag
    from tlgnr_user_type_bs bs
    where bs.user_type_cd = p_user_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_user_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_user_type_bs set
/* user_type_cd: cannot update because this is the key */
          function_id					 = p_function_id,
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          user_type_cd = p_user_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_user_type_cd || ').');
      end if;
    else
      insert into tlgnr_user_type_bs (
        user_type_cd,
        function_id,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_user_type_cd,
        p_function_id,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_user_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_user_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_user_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_user_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_user_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.user_type_cd) into v_exists_bs_flag
    from tlgnr_user_type_bs bs
    where bs.user_type_cd = p_user_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.user_type_cd) into v_exists_flag
      from tlgnr_user_type_tx tx
      where tx.user_type_cd = p_user_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_user_type_tx set
/* p_user_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          user_type_cd = p_user_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_user_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_user_type_tx (
          user_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_user_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_user_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_user_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_USER_TYPE_BS
execute upsert_bs(0, 1, 9150);
execute upsert_bs(1, 1, 9151);
execute upsert_bs(2, 1, 9152);
execute upsert_bs(3, 1, 9153);
execute upsert_bs(4, 1, 9154);
-- Driver is NOT a user type according to
-- https://aww.adnovum.ch/twiki/bin/view/CrhPegasus/UserAdminExternalUsers#Clarification_about_User_Types_a

prompt INSERTING into TLGNR_USER_TYPE_TX - EN
execute upsert_tx(0, 'en', 'Crh');
execute upsert_tx(1, 'en', 'Customer');
execute upsert_tx(2, 'en', 'Vendor');
execute upsert_tx(3, 'en', 'Driver');
execute upsert_tx(4, 'en', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - DE
execute upsert_tx(0, 'de', 'Crh');
execute upsert_tx(1, 'de', 'Kunde');
execute upsert_tx(2, 'de', 'Transporteur');
execute upsert_tx(3, 'de', 'Driver');
execute upsert_tx(4, 'de', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - FR
execute upsert_tx(0, 'fr', 'Crh');
execute upsert_tx(1, 'fr', 'Client');
execute upsert_tx(2, 'fr', 'Vendeur');
execute upsert_tx(3, 'fr', 'Driver');
execute upsert_tx(4, 'fr', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - IT
execute upsert_tx(0, 'it', 'Crh');
execute upsert_tx(1, 'it', 'Cliente');
execute upsert_tx(2, 'it', 'Fornitore');
execute upsert_tx(3, 'it', 'Driver');
execute upsert_tx(4, 'it', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - ES
execute upsert_tx(0, 'es', 'Crh');
execute upsert_tx(1, 'es', 'Cliente');
execute upsert_tx(2, 'es', 'Transportista');
execute upsert_tx(3, 'es', 'Driver');
execute upsert_tx(4, 'es', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - CS
execute upsert_tx(0, 'cs', 'Crh');
execute upsert_tx(1, 'cs', 'Zákazník');
execute upsert_tx(2, 'cs', 'Dodavatel');
execute upsert_tx(3, 'cs', 'Driver');
execute upsert_tx(4, 'cs', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - SK
execute upsert_tx(0, 'sk', 'Crh');
execute upsert_tx(1, 'sk', 'Zákazník');
execute upsert_tx(2, 'sk', 'Obchodník');
execute upsert_tx(3, 'sk', 'Driver');
execute upsert_tx(4, 'sk', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - HU
execute upsert_tx(0, 'hu', 'Crh');
execute upsert_tx(1, 'hu', 'Vevők');
execute upsert_tx(2, 'hu', 'Eladó');
execute upsert_tx(3, 'hu', 'Driver');
execute upsert_tx(4, 'hu', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - RO
execute upsert_tx(0, 'ro', 'Crh');
execute upsert_tx(1, 'ro', 'Client');
execute upsert_tx(2, 'ro', 'Vanzator');
execute upsert_tx(3, 'ro', 'Driver');
execute upsert_tx(4, 'ro', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - BG
execute upsert_tx(0, 'bg', 'Crh');
execute upsert_tx(1, 'bg', 'Клиент');
execute upsert_tx(2, 'bg', 'Продавач');
execute upsert_tx(3, 'bg', 'Driver');
execute upsert_tx(4, 'bg', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - RU (ENGLISH COPY)
execute upsert_tx(0, 'ru', 'Crh');
execute upsert_tx(1, 'ru', 'Клиент');
execute upsert_tx(2, 'ru', 'Поставщик');
execute upsert_tx(3, 'ru', 'Driver');
execute upsert_tx(4, 'ru', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - PL
execute upsert_tx(0, 'pl', 'Crh');
execute upsert_tx(1, 'pl', 'Klient');
execute upsert_tx(2, 'pl', 'Dystrybutor');
execute upsert_tx(3, 'pl', 'Driver');
execute upsert_tx(4, 'pl', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - AZ (ENGLISH COPY)
execute upsert_tx(0, 'az', 'Crh');
execute upsert_tx(1, 'az', 'Customer');
execute upsert_tx(2, 'az', 'Vendor');
execute upsert_tx(3, 'az', 'Driver');
execute upsert_tx(4, 'az', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - HR
execute upsert_tx(0, 'hr', 'Crh');
execute upsert_tx(1, 'hr', 'Kupac');
execute upsert_tx(2, 'hr', 'Prodavač');
execute upsert_tx(3, 'hr', 'Driver');
execute upsert_tx(4, 'hr', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - SR
execute upsert_tx(0, 'sr', 'Crh');
execute upsert_tx(1, 'sr', 'Kupac');
execute upsert_tx(2, 'sr', 'Dobavljač');
execute upsert_tx(3, 'sr', 'Driver');
execute upsert_tx(4, 'sr', 'Admin');

prompt INSERTING into TLGNR_USER_TYPE_TX - TR
execute upsert_tx(0, 'tr', 'Crh');
execute upsert_tx(1, 'tr', 'Customer');
execute upsert_tx(2, 'tr', 'Vendor');
execute upsert_tx(3, 'tr', 'Driver');
execute upsert_tx(4, 'tr', 'Admin');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

