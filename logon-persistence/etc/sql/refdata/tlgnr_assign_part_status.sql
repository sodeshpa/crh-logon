-- $Revision$
-- $Date$
--
-- This is an internal, technical table:
-- - No translations needed
-- - Extending the possible values requires code changes

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_assign_part_status.sql ----------'

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
-- tlgnr_assign_part_status_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_assign_part_status_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_assign_part_status_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_assign_part_status_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.assign_part_status_cd)
    into v_exists_flag
    from tlgnr_assign_part_status_bs bs
    where bs.assign_part_status_cd = p_assign_part_status_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_assign_part_status_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_assign_part_status_bs set
          /* assign_part_status_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          assign_part_status_cd = p_assign_part_status_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_assign_part_status_cd || ').');
      end if;
    else
      insert into tlgnr_assign_part_status_bs (
        assign_part_status_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_assign_part_status_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_assign_part_status_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_assign_part_status_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_assign_part_status_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_assign_part_status_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_assign_part_status_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

    ----------------------------------------------------------------------------
    -- Lookup BS entry first
    ----------------------------------------------------------------------------
    select count(bs.assign_part_status_cd) into v_exists_bs_flag
    from tlgnr_assign_part_status_bs bs
    where bs.assign_part_status_cd = p_assign_part_status_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.assign_part_status_cd) into v_exists_flag
      from tlgnr_assign_part_status_tx tx
      where tx.assign_part_status_cd = p_assign_part_status_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_assign_part_status_tx set
          /* p_assign_part_status_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          assign_part_status_cd = p_assign_part_status_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_assign_part_status_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_assign_part_status_tx (
          assign_part_status_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_assign_part_status_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_assign_part_status_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_assign_part_status_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_BS
execute upsert_bs(0, 1);
execute upsert_bs(2, 1);

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - EN
execute upsert_tx(0, 'en', 'New');
execute upsert_tx(2, 'en', 'Confirmed by driver');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - DE
execute upsert_tx(0, 'de', 'Neu');
execute upsert_tx(2, 'de', 'Bestätigt durch den Fahrer');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - FR
execute upsert_tx(0, 'fr', 'Nouveau');
execute upsert_tx(2, 'fr', 'Confirmé par le chauffeur');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - IT
execute upsert_tx(0, 'it', 'Nuovo');
execute upsert_tx(2, 'it', 'Confermato dall''autista');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - ES
execute upsert_tx(0, 'es', 'Nuevo');
execute upsert_tx(2, 'es', 'Confirmado por el conductor');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - CS
execute upsert_tx(0, 'cs', 'Nový');
execute upsert_tx(2, 'cs', 'Potvrzeno řidičem');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - SK
execute upsert_tx(0, 'sk', 'Nový');
execute upsert_tx(2, 'sk', 'Potvrdené vodičom');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - HU
execute upsert_tx(0, 'hu', 'Új');
execute upsert_tx(2, 'hu', 'Vezetői megerősítés');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - RO
execute upsert_tx(0, 'ro', 'Nou');
execute upsert_tx(2, 'ro', 'Confirmat de catre sofer');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - BG
execute upsert_tx(0, 'bg', 'Нов');
execute upsert_tx(2, 'bg', 'Потвърден от шофьор');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - RU (ENGLISH COPY)
execute upsert_tx(0, 'ru', 'новый');
execute upsert_tx(2, 'ru', 'подтверждено водителем');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - PL (ENGLISH COPY)
execute upsert_tx(0, 'pl', 'Nowy');
execute upsert_tx(2, 'pl', 'Potwierdzono przez kierowcę');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - AZ (ENGLISH COPY)
execute upsert_tx(0, 'az', 'Yeni');
execute upsert_tx(2, 'az', 'Sürücü  təsdiq edib');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - HR (ENGLISH COPY)
execute upsert_tx(0, 'hr', 'Novi');
execute upsert_tx(2, 'hr', 'Potvrđeno od strane vozača');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - SR (ENGLISH COPY)
execute upsert_tx(0, 'sr', 'Novi');
execute upsert_tx(2, 'sr', 'Potvrdio vozač');

prompt INSERTING into TLGNR_ASSIGN_PART_STATUS_TX - TR
execute upsert_tx(0, 'tr', 'New');
execute upsert_tx(2, 'tr', 'Confirmed by driver');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

