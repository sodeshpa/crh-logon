-- $Revision$
-- $Date$
--
prompt '---------- refdata/tlgnr_weigh_system_lp.sql ----------'

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
-- tlgnr_weigh_system_lp_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_weigh_system_lp_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_weigh_system_lp_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_weigh_system_lp_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.weigh_system_lp_cd)
    into v_exists_flag
    from tlgnr_weigh_system_lp_bs bs
    where bs.weigh_system_lp_cd = p_weigh_system_lp_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_weigh_system_lp_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_weigh_system_lp_bs set
/* weigh_system_lp_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          weigh_system_lp_cd = p_weigh_system_lp_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_weigh_system_lp_cd || ').');
      end if;
    else
      insert into tlgnr_weigh_system_lp_bs (
        weigh_system_lp_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_weigh_system_lp_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_weigh_system_lp_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_weigh_system_lp_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_weigh_system_lp_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_weigh_system_lp_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_weigh_system_lp_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.weigh_system_lp_cd) into v_exists_bs_flag
    from tlgnr_weigh_system_lp_bs bs
    where bs.weigh_system_lp_cd = p_weigh_system_lp_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.weigh_system_lp_cd) into v_exists_flag
      from tlgnr_weigh_system_lp_tx tx
      where tx.weigh_system_lp_cd = p_weigh_system_lp_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_weigh_system_lp_tx set
/* p_weigh_system_lp_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          weigh_system_lp_cd = p_weigh_system_lp_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_weigh_system_lp_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_weigh_system_lp_tx (
          weigh_system_lp_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_weigh_system_lp_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_weigh_system_lp_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_weigh_system_lp_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_BS
execute upsert_bs( 0,  1);
execute upsert_bs( 2,  1);
execute upsert_bs( 3,  1);

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - EN
execute upsert_tx( 0, 'en', 'LDS from shipping point');
execute upsert_tx( 2, 'en', 'manual LOGON');
execute upsert_tx( 3, 'en', 'manual PDA');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - DE
execute upsert_tx( 0, 'de', 'LDS der Verladestrasse');
execute upsert_tx( 2, 'de', 'manuell LOGON');
execute upsert_tx( 3, 'de', 'manuell PDA');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - FR
execute upsert_tx( 0, 'fr', 'LDS from shipping point');
execute upsert_tx( 2, 'fr', 'manuel LOGON');
execute upsert_tx( 3, 'fr', 'manuel PDA');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - IT
execute upsert_tx( 0, 'it', 'LDS from shipping point');
execute upsert_tx( 2, 'it', 'manuale LOGON');
execute upsert_tx( 3, 'it', 'manuale PDA');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - ES
execute upsert_tx( 0, 'es', 'LDS from shipping point');
execute upsert_tx( 2, 'es', 'manual LOGON');
execute upsert_tx( 3, 'es', 'manual PDA');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - CS
execute upsert_tx( 0, 'cs', 'LDS v místě nakládky');
execute upsert_tx( 2, 'cs', 'manuální LOGON');
execute upsert_tx( 3, 'cs', 'manuální PDA');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - SK
execute upsert_tx( 0, 'sk', 'LDS z miesta nakládky');
execute upsert_tx( 2, 'sk', 'manuálny LOGON');
execute upsert_tx( 3, 'sk', 'manuálne PDA');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - HU
execute upsert_tx( 0, 'hu', 'LDS felrakó helyről');
execute upsert_tx( 2, 'hu', 'Manuális Logon');
execute upsert_tx( 3, 'hu', 'Manuális PDA');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - RO
execute upsert_tx( 0, 'ro', 'Sistem Local de Expeditie de la punct de expeditie');
execute upsert_tx( 2, 'ro', 'LOGON manual');
execute upsert_tx( 3, 'ro', 'PDA manual');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - BG
execute upsert_tx( 0, 'bg', 'LDS от мястото на товарене');
execute upsert_tx( 2, 'bg', 'ръчен LOGON');
execute upsert_tx( 3, 'bg', 'ръчен PDA');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - RU (ENGLISH COPY)
execute upsert_tx( 0, 'ru', 'LDS в зоне отгрузки');
execute upsert_tx( 2, 'ru', 'инструкция LOGON');
execute upsert_tx( 3, 'ru', 'инструкция PDA');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - PL
execute upsert_tx( 0, 'pl', 'LDS z żeglugi punktu');
execute upsert_tx( 2, 'pl', 'instrukcja LOGON');
execute upsert_tx( 3, 'pl', 'obsługi PDA');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - AZ (ENGLISH COPY)
execute upsert_tx( 0, 'az', 'LDS from shipping point');
execute upsert_tx( 2, 'az', 'manual LOGON');
execute upsert_tx( 3, 'az', 'manual PDA');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - HR
execute upsert_tx( 0, 'hr', 'LDS od utovarne točke');
execute upsert_tx( 2, 'hr', 'ručno LOGON');
execute upsert_tx( 3, 'hr', 'ručno PDA');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - SR
execute upsert_tx( 0, 'sr', 'LDS od utovarne tačke');
execute upsert_tx( 2, 'sr', 'ručno LOGON');
execute upsert_tx( 3, 'sr', 'ručno PDA');

prompt INSERTING into TLGNR_WEIGH_SYSTEM_LP_TX - TR
execute upsert_tx( 0, 'tr', 'LDS from shipping point');
execute upsert_tx( 2, 'tr', 'manual LOGON');
execute upsert_tx( 3, 'tr', 'manual PDA');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

