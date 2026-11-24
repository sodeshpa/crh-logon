-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_fuel_type.sql ----------'

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
-- tlgnr_fuel_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_fuel_type_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_fuel_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_fuel_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.fuel_type_cd)
    into v_exists_flag
    from tlgnr_fuel_type_bs bs
    where bs.fuel_type_cd = p_fuel_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_fuel_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_fuel_type_bs set
/* fuel_type_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          fuel_type_cd = p_fuel_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_fuel_type_cd || ').');
      end if;
    else
      insert into tlgnr_fuel_type_bs (
        fuel_type_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_fuel_type_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_fuel_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_fuel_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_fuel_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_fuel_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_fuel_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.fuel_type_cd) into v_exists_bs_flag
    from tlgnr_fuel_type_bs bs
    where bs.fuel_type_cd = p_fuel_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.fuel_type_cd) into v_exists_flag
      from tlgnr_fuel_type_tx tx
      where tx.fuel_type_cd = p_fuel_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_fuel_type_tx set
/* p_fuel_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          fuel_type_cd = p_fuel_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_fuel_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_fuel_type_tx (
          fuel_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_fuel_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_fuel_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_fuel_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_FUEL_TYPE_BS
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);

prompt INSERTING into TLGNR_FUEL_TYPE_TX - EN
execute upsert_tx(1, 'en', 'Diesel');
execute upsert_tx(2, 'en', 'Gasoline');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - DE
execute upsert_tx(1, 'de', 'Diesel');
execute upsert_tx(2, 'de', 'Benzin');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - FR
execute upsert_tx(1, 'fr', 'Diesel');
execute upsert_tx(2, 'fr', 'Essence');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - IT
execute upsert_tx(1, 'it', 'Diesel');
execute upsert_tx(2, 'it', 'Benzina');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - ES
execute upsert_tx(1, 'es', 'Diésel');
execute upsert_tx(2, 'es', 'Gasolina');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - CS
execute upsert_tx(1, 'cs', 'Nafta');
execute upsert_tx(2, 'cs', 'Benzín');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - SK
execute upsert_tx(1, 'sk', 'Nafta');
execute upsert_tx(2, 'sk', 'Benzín');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - HU
execute upsert_tx(1, 'hu', 'Dízel');
execute upsert_tx(2, 'hu', 'Benzin');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - RO
execute upsert_tx(1, 'ro', 'Diesel');
execute upsert_tx(2, 'ro', 'Benzina');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - BG
execute upsert_tx(1, 'bg', 'Дизел');
execute upsert_tx(2, 'bg', 'Бензин');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - RU
execute upsert_tx(1, 'ru', 'дизельное топливо');
execute upsert_tx(2, 'ru', 'бензин');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - PL
execute upsert_tx(1, 'pl', 'Diesel');
execute upsert_tx(2, 'pl', 'Benzyna');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - AZ
execute upsert_tx(1, 'az', 'Diesel');
execute upsert_tx(2, 'az', 'Gasoline');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - HR
execute upsert_tx(1, 'hr', 'Dizel');
execute upsert_tx(2, 'hr', 'Benzin');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - SR
execute upsert_tx(1, 'sr', 'Dizel');
execute upsert_tx(2, 'sr', 'Benzin');

prompt INSERTING into TLGNR_FUEL_TYPE_TX - TR
execute upsert_tx(1, 'tr', 'Diesel');
execute upsert_tx(2, 'tr', 'Gasoline');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

