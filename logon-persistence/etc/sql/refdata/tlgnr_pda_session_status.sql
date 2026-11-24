-- $Revision$
-- $Date$
--
-- Logon internal refdata. Adding new values requires code changes, see tlgnc_pda_session.pda_session_status_cd.

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_pda_session_status.sql ----------'

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
-- tlgnr_pda_session_status_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_pda_session_status_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_pda_session_status_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_pda_session_status_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.pda_session_status_cd)
    into v_exists_flag
    from tlgnr_pda_session_status_bs bs
    where bs.pda_session_status_cd = p_pda_session_status_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_pda_session_status_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_pda_session_status_bs set
/* pda_session_status_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          pda_session_status_cd = p_pda_session_status_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_pda_session_status_cd || ').');
      end if;
    else
      insert into tlgnr_pda_session_status_bs (
        pda_session_status_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_pda_session_status_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_pda_session_status_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_pda_session_status_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_pda_session_status_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_pda_session_status_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_pda_session_status_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.pda_session_status_cd) into v_exists_bs_flag
    from tlgnr_pda_session_status_bs bs
    where bs.pda_session_status_cd = p_pda_session_status_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.pda_session_status_cd) into v_exists_flag
      from tlgnr_pda_session_status_tx tx
      where tx.pda_session_status_cd = p_pda_session_status_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_pda_session_status_tx set
/* p_pda_session_status_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          pda_session_status_cd = p_pda_session_status_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_pda_session_status_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_pda_session_status_tx (
          pda_session_status_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_pda_session_status_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_pda_session_status_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_pda_session_status_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_BS
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);
execute upsert_bs(3, 1);
execute upsert_bs(4, 1);
execute upsert_bs(5, 1);

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - EN
execute upsert_tx(1, 'en', 'Initializing');
execute upsert_tx(2, 'en', 'Ready');
execute upsert_tx(3, 'en', 'Terminated');
execute upsert_tx(4, 'en', 'Events pending');
execute upsert_tx(5, 'en', 'Timed out');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - DE
execute upsert_tx(1, 'de', 'Am initialisieren');
execute upsert_tx(2, 'de', 'Bereit');
execute upsert_tx(3, 'de', 'Beendet');
execute upsert_tx(4, 'de', 'Events pending');
execute upsert_tx(5, 'de', 'Timed out');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - FR
execute upsert_tx(1, 'fr', 'Initialisation');
execute upsert_tx(2, 'fr', 'Prêt');
execute upsert_tx(3, 'fr', 'Terminé');
execute upsert_tx(4, 'fr', 'Événements en attente');
execute upsert_tx(5, 'fr', 'Hors délais');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - IT
execute upsert_tx(1, 'it', 'Inizializzando');
execute upsert_tx(2, 'it', 'Pronto');
execute upsert_tx(3, 'it', 'Terminato');
execute upsert_tx(4, 'it', 'Eventi in sospeso');
execute upsert_tx(5, 'it', 'Scaduto');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - ES
execute upsert_tx(1, 'es', 'Inicializando');
execute upsert_tx(2, 'es', 'Preparado');
execute upsert_tx(3, 'es', 'Interrumpido');
execute upsert_tx(4, 'es', 'Eventos pendientes');
execute upsert_tx(5, 'es', 'Desconectado');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - CS
execute upsert_tx(1, 'cs', 'Startuji');
execute upsert_tx(2, 'cs', 'Připraven');
execute upsert_tx(3, 'cs', 'Přerušen');
execute upsert_tx(4, 'cs', 'Události čekají na vyřízení');
execute upsert_tx(5, 'cs', 'Čas vypršel');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - SK
execute upsert_tx(1, 'sk', 'Inicializovanie');
execute upsert_tx(2, 'sk', 'Hotovo');
execute upsert_tx(3, 'sk', 'Skončené');
execute upsert_tx(4, 'sk', 'Udalosti v priebehu');
execute upsert_tx(5, 'sk', 'Vypršal');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - HU
execute upsert_tx(1, 'hu', 'Inicializálás');
execute upsert_tx(2, 'hu', 'Kész');
execute upsert_tx(3, 'hu', 'Terminált');
execute upsert_tx(4, 'hu', 'Esemény függőben');
execute upsert_tx(5, 'hu', 'Időtúllépés');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - RO
execute upsert_tx(1, 'ro', 'Initializare');
execute upsert_tx(2, 'ro', 'Pregatit');
execute upsert_tx(3, 'ro', 'Oprit');
execute upsert_tx(4, 'ro', 'Evenimente in asteptare');
execute upsert_tx(5, 'ro', 'Timp expirat');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - BG
execute upsert_tx(1, 'bg', 'Инициализиране');
execute upsert_tx(2, 'bg', 'Готов');
execute upsert_tx(3, 'bg', 'Прекратен');
execute upsert_tx(4, 'bg', 'Очаквани събития');
execute upsert_tx(5, 'bg', 'С изтекъл срок');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - RU (ENGLISH COPY)
execute upsert_tx(1, 'ru', 'инициализация');
execute upsert_tx(2, 'ru', 'готово');
execute upsert_tx(3, 'ru', 'прекращено');
execute upsert_tx(4, 'ru', 'событие в ожидании');
execute upsert_tx(5, 'ru', 'время истекло');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - PL
execute upsert_tx(1, 'pl', 'Inicjowanie');
execute upsert_tx(2, 'pl', 'Gotowy');
execute upsert_tx(3, 'pl', 'Zakończony');
execute upsert_tx(4, 'pl', 'Spodziewane zdarzenia');
execute upsert_tx(5, 'pl', 'Przekroczono limit czasu');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - AZ (ENGLISH COPY)
execute upsert_tx(1, 'az', 'Initializing');
execute upsert_tx(2, 'az', 'Ready');
execute upsert_tx(3, 'az', 'Terminated');
execute upsert_tx(4, 'az', 'Events pending');
execute upsert_tx(5, 'az', 'Timed out');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - HR
execute upsert_tx(1, 'hr', 'Inicijalizacija');
execute upsert_tx(2, 'hr', 'Spreman');
execute upsert_tx(3, 'hr', 'Obustavljeno');
execute upsert_tx(4, 'hr', 'Događanja u tijeku');
execute upsert_tx(5, 'hr', 'Vrijeme isteklo');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - SR
execute upsert_tx(1, 'sr', 'Inicijalizacija');
execute upsert_tx(2, 'sr', 'Spreman');
execute upsert_tx(3, 'sr', 'Obustavljeno');
execute upsert_tx(4, 'sr', 'Događanja u toku');
execute upsert_tx(5, 'sr', 'Vreme isteklo');

prompt INSERTING into TLGNR_PDA_SESSION_STATUS_TX - TR
execute upsert_tx(1, 'tr', 'Initializing');
execute upsert_tx(2, 'tr', 'Ready');
execute upsert_tx(3, 'tr', 'Terminated');
execute upsert_tx(4, 'tr', 'Events pending');
execute upsert_tx(5, 'tr', 'Timed out');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

