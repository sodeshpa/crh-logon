-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_wagon_event_status.sql ----------'

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
-- tlgnr_wagon_event_status_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_wagon_event_status_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_wagon_event_status_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_wagon_event_status_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.wagon_event_status_cd)
    into v_exists_flag
    from tlgnr_wagon_event_status_bs bs
    where bs.wagon_event_status_cd = p_wagon_event_status_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_wagon_event_status_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_wagon_event_status_bs set
/* wagon_event_status_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          wagon_event_status_cd = p_wagon_event_status_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_wagon_event_status_cd || ').');
      end if;
    else
      insert into tlgnr_wagon_event_status_bs (
        wagon_event_status_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_wagon_event_status_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_wagon_event_status_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_wagon_event_status_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_wagon_event_status_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_wagon_event_status_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_wagon_event_status_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.wagon_event_status_cd) into v_exists_bs_flag
    from tlgnr_wagon_event_status_bs bs
    where bs.wagon_event_status_cd = p_wagon_event_status_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.wagon_event_status_cd) into v_exists_flag
      from tlgnr_wagon_event_status_tx tx
      where tx.wagon_event_status_cd = p_wagon_event_status_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_wagon_event_status_tx set
/* p_wagon_event_status_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          wagon_event_status_cd = p_wagon_event_status_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_wagon_event_status_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_wagon_event_status_tx (
          wagon_event_status_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_wagon_event_status_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_wagon_event_status_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_wagon_event_status_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_BS
execute upsert_bs( 1, 1);
execute upsert_bs( 2, 1);
execute upsert_bs( 3, 1);
execute upsert_bs( 4, 1);

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - EN
execute upsert_tx( 1, 'en', 'New');
execute upsert_tx( 2, 'en', 'Processed (success)');
execute upsert_tx( 3, 'en', 'Processed (failure)');
execute upsert_tx( 4, 'en', 'Ignored');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - DE
execute upsert_tx( 1, 'de', 'Neu');
execute upsert_tx( 2, 'de', 'Verarbeitet (Erfolg)');
execute upsert_tx( 3, 'de', 'Verarbeitet (Fehler)');
execute upsert_tx( 4, 'de', 'Ignoriert');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - FR
execute upsert_tx( 1, 'fr', 'Nouveau');
execute upsert_tx( 2, 'fr', 'Traité (réussie)');
execute upsert_tx( 3, 'fr', 'Traité (échouée)');
execute upsert_tx( 4, 'fr', 'Ignoré');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - IT
execute upsert_tx( 1, 'it', 'Nuovo');
execute upsert_tx( 2, 'it', 'Processato (riuscito)');
execute upsert_tx( 3, 'it', 'Processato (fallito)');
execute upsert_tx( 4, 'it', 'Ignorato');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - ES
execute upsert_tx( 1, 'es', 'Nuevo');
execute upsert_tx( 2, 'es', 'Procesado (correcto)');
execute upsert_tx( 3, 'es', 'Procesado (fallido)');
execute upsert_tx( 4, 'es', 'Ignorado');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - CS
execute upsert_tx( 1, 'cs', 'Nový');
execute upsert_tx( 2, 'cs', 'Zpracováno (úspešně)');
execute upsert_tx( 3, 'cs', 'Zpracováno (chyba)');
execute upsert_tx( 4, 'cs', 'Ignorováno');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - SK
execute upsert_tx( 1, 'sk', 'Nový');
execute upsert_tx( 2, 'sk', 'Spracované (úspešne)');
execute upsert_tx( 3, 'sk', 'Spracované (neúspešne)');
execute upsert_tx( 4, 'sk', 'Ignorované');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - HU
execute upsert_tx( 1, 'hu', 'Új');
execute upsert_tx( 2, 'hu', 'Feldolgozott(sikeres)');
execute upsert_tx( 3, 'hu', 'Feldolgozott (sikertelen)');
execute upsert_tx( 4, 'hu', 'Figyelmen kívül hagyott');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - RO
execute upsert_tx( 1, 'ro', 'Nou');
execute upsert_tx( 2, 'ro', 'Procesat (succes)');
execute upsert_tx( 3, 'ro', 'Procesat (esec)');
execute upsert_tx( 4, 'ro', 'Ignorat');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - BG
execute upsert_tx( 1, 'bg', 'Нов');
execute upsert_tx( 2, 'bg', 'Преработване (Успешно)');
execute upsert_tx( 3, 'bg', 'Преработване (Неуспешно)');
execute upsert_tx( 4, 'bg', 'Игнориран');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - RU (ENGLISH COPY)
execute upsert_tx( 1, 'ru', 'новый');
execute upsert_tx( 2, 'ru', 'обработано (успешно)');
execute upsert_tx( 3, 'ru', 'обработано (сбой)');
execute upsert_tx( 4, 'ru', 'игнорируется');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - PL
execute upsert_tx( 1, 'pl', 'Nowy');
execute upsert_tx( 2, 'pl', 'Przetworzone(sukces)');
execute upsert_tx( 3, 'pl', 'Przetworzone(brak powod.)');
execute upsert_tx( 4, 'pl', 'Ignorowany');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - AZ (ENGLISH COPY)
execute upsert_tx( 1, 'az', 'New');
execute upsert_tx( 2, 'az', 'Processed (success)');
execute upsert_tx( 3, 'az', 'Processed (failure)');
execute upsert_tx( 4, 'az', 'Ignored');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - HR
execute upsert_tx( 1, 'hr', 'Novi');
execute upsert_tx( 2, 'hr', 'Obrađeno (uspješno)');
execute upsert_tx( 3, 'hr', 'Obrađeno (neuspješno)');
execute upsert_tx( 4, 'hr', 'Zanemareno');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - SR
execute upsert_tx( 1, 'sr', 'Novi');
execute upsert_tx( 2, 'sr', 'Obrađeno (uspešno)');
execute upsert_tx( 3, 'sr', 'Obrađeno (neuspešno)');
execute upsert_tx( 4, 'sr', 'Zanemareno');

prompt INSERTING into TLGNR_WAGON_EVENT_STATUS_TX - TR
execute upsert_tx( 1, 'tr', 'New');
execute upsert_tx( 2, 'tr', 'Processed (success)');
execute upsert_tx( 3, 'tr', 'Processed (failure)');
execute upsert_tx( 4, 'tr', 'Ignored');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

