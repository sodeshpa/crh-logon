-- $Revision$
-- $Date$
--
-- This is an internal, technical table:
-- - Extending the possible values requires code changes

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_billto_ruletype.sql ----------'

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
-- tlgnr_billto_ruletype_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_billto_ruletype_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_billto_ruletype_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_billto_ruletype_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.billto_ruletype_cd)
    into v_exists_flag
    from tlgnr_billto_ruletype_bs bs
    where bs.billto_ruletype_cd = p_billto_ruletype_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_billto_ruletype_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_billto_ruletype_bs set
          /* billto_ruletype_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          billto_ruletype_cd = p_billto_ruletype_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_billto_ruletype_cd || ').');
      end if;
    else
      insert into tlgnr_billto_ruletype_bs (
        billto_ruletype_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_billto_ruletype_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_billto_ruletype_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_billto_ruletype_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_billto_ruletype_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_billto_ruletype_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_billto_ruletype_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

    ----------------------------------------------------------------------------
    -- Lookup BS entry first
    ----------------------------------------------------------------------------
    select count(bs.billto_ruletype_cd) into v_exists_bs_flag
    from tlgnr_billto_ruletype_bs bs
    where bs.billto_ruletype_cd = p_billto_ruletype_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.billto_ruletype_cd) into v_exists_flag
      from tlgnr_billto_ruletype_tx tx
      where tx.billto_ruletype_cd = p_billto_ruletype_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_billto_ruletype_tx set
          /* p_billto_ruletype_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          billto_ruletype_cd = p_billto_ruletype_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_billto_ruletype_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_billto_ruletype_tx (
          billto_ruletype_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_billto_ruletype_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_billto_ruletype_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_billto_ruletype_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_BILLTO_RULETYPE_BS
execute upsert_bs(0, 1);
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - EN
execute upsert_tx(0, 'en', 'Month');
execute upsert_tx(1, 'en', 'Day of week');
execute upsert_tx(2, 'en', 'Day of month');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - DE
execute upsert_tx(0, 'de', 'Monat');
execute upsert_tx(1, 'de', 'Wochentag');
execute upsert_tx(2, 'de', 'Monatstag');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - FR
execute upsert_tx(0, 'fr', 'Mois');
execute upsert_tx(1, 'fr', 'Jour de semaine');
execute upsert_tx(2, 'fr', 'Jour du mois');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - IT
execute upsert_tx(0, 'it', 'Mese');
execute upsert_tx(1, 'it', 'Giorno della settimana');
execute upsert_tx(2, 'it', 'Giorno del mese');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - ES
execute upsert_tx(0, 'es', 'Mes');
execute upsert_tx(1, 'es', 'Día de la semana');
execute upsert_tx(2, 'es', 'Día del mes');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - CS
execute upsert_tx(0, 'cs', 'Měsíc');
execute upsert_tx(1, 'cs', 'Den v týdnu');
execute upsert_tx(2, 'cs', 'Den v měsíci');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - SK
execute upsert_tx(0, 'sk', 'Mesiac');
execute upsert_tx(1, 'sk', 'Deň v týždni');
execute upsert_tx(2, 'sk', 'Deň v mesiaci');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - HU
execute upsert_tx(0, 'hu', 'Hónap');
execute upsert_tx(1, 'hu', 'A hét napja');
execute upsert_tx(2, 'hu', 'A hónap napja');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - RO
execute upsert_tx(0, 'ro', 'Luna');
execute upsert_tx(1, 'ro', 'Ziua saptamanii');
execute upsert_tx(2, 'ro', 'Ziua lunii');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - BG
execute upsert_tx(0, 'bg', 'Месец');
execute upsert_tx(1, 'bg', 'Ден от седмица');
execute upsert_tx(2, 'bg', 'Ден от месец');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - RU (ENGLISH COPY)
execute upsert_tx(0, 'ru', 'месяц');
execute upsert_tx(1, 'ru', 'день недели');
execute upsert_tx(2, 'ru', 'день месяца');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - PL (ENGLISH COPY)
execute upsert_tx(0, 'pl', 'Miesiąc');
execute upsert_tx(1, 'pl', 'Dzień tygodnia');
execute upsert_tx(2, 'pl', 'Dzień miesiąca');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - AZ (ENGLISH COPY)
execute upsert_tx(0, 'az', 'Month');
execute upsert_tx(1, 'az', 'Day of week');
execute upsert_tx(2, 'az', 'Day of month');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - HR (ENGLISH COPY)
execute upsert_tx(0, 'hr', 'Mjesec');
execute upsert_tx(1, 'hr', 'Dan u tjednu');
execute upsert_tx(2, 'hr', 'Dan u mjesecu');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - SR (ENGLISH COPY)
execute upsert_tx(0, 'sr', 'Mesec');
execute upsert_tx(1, 'sr', 'Dan u nedelji ');
execute upsert_tx(2, 'sr', 'Dan u mesecu');

prompt INSERTING into TLGNR_BILLTO_RULETYPE_TX - TR
execute upsert_tx(0, 'tr', 'Month');
execute upsert_tx(1, 'tr', 'Day of week');
execute upsert_tx(2, 'tr', 'Day of month');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

