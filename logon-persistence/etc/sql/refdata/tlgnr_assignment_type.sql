-- $Revision$
-- $Date$
--
-- This is an internal, technical table:
-- - Extending the possible values requires code changes

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_assignment_type.sql ----------'

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
-- tlgnr_assignment_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_assignment_type_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_assignment_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_assignment_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.assignment_type_cd)
    into v_exists_flag
    from tlgnr_assignment_type_bs bs
    where bs.assignment_type_cd = p_assignment_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_assignment_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_assignment_type_bs set
          /* assignment_type_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          assignment_type_cd = p_assignment_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_assignment_type_cd || ').');
      end if;
    else
      insert into tlgnr_assignment_type_bs (
        assignment_type_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_assignment_type_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_assignment_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_assignment_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_assignment_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_assignment_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_assignment_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

    ----------------------------------------------------------------------------
    -- Lookup BS entry first
    ----------------------------------------------------------------------------
    select count(bs.assignment_type_cd) into v_exists_bs_flag
    from tlgnr_assignment_type_bs bs
    where bs.assignment_type_cd = p_assignment_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.assignment_type_cd) into v_exists_flag
      from tlgnr_assignment_type_tx tx
      where tx.assignment_type_cd = p_assignment_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_assignment_type_tx set
          /* p_assignment_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          assignment_type_cd = p_assignment_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_assignment_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_assignment_type_tx (
          assignment_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_assignment_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_assignment_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_assignment_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_BS
execute upsert_bs(0, 1);
execute upsert_bs(1, 1);

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - EN
execute upsert_tx(0, 'en', 'external');
execute upsert_tx(1, 'en', 'internal');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - DE
execute upsert_tx(0, 'de', 'extern');
execute upsert_tx(1, 'de', 'intern');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - FR
execute upsert_tx(0, 'fr', 'externe');
execute upsert_tx(1, 'fr', 'interne');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - IT
execute upsert_tx(0, 'it', 'esterno');
execute upsert_tx(1, 'it', 'interno');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - ES
execute upsert_tx(0, 'es', 'externo');
execute upsert_tx(1, 'es', 'interno');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - CS
execute upsert_tx(0, 'cs', 'externí');
execute upsert_tx(1, 'cs', 'interní');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - SK
execute upsert_tx(0, 'sk', 'externé');
execute upsert_tx(1, 'sk', 'interné');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - HU
execute upsert_tx(0, 'hu', 'Külső');
execute upsert_tx(1, 'hu', 'Belső');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - RO
execute upsert_tx(0, 'ro', 'extern');
execute upsert_tx(1, 'ro', 'intern');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - BG
execute upsert_tx(0, 'bg', 'Външен');
execute upsert_tx(1, 'bg', 'Вътрешен');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - RU (ENGLISH COPY)
execute upsert_tx(0, 'ru', 'внешний');
execute upsert_tx(1, 'ru', 'внутренний');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - PL (ENGLISH COPY)
execute upsert_tx(0, 'pl', 'zewnętrzny');
execute upsert_tx(1, 'pl', 'wewnętrzny');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - AZ (ENGLISH COPY)
execute upsert_tx(0, 'az', 'external');
execute upsert_tx(1, 'az', 'internal');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - HR (ENGLISH COPY)
execute upsert_tx(0, 'hr', 'vanjski');
execute upsert_tx(1, 'hr', 'interni');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - SR (ENGLISH COPY)
execute upsert_tx(0, 'sr', 'eksterni');
execute upsert_tx(1, 'sr', 'interni');

prompt INSERTING into TLGNR_ASSIGNMENT_TYPE_TX - TR
execute upsert_tx(0, 'tr', 'external');
execute upsert_tx(1, 'tr', 'internal');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

