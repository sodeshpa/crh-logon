-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_audit_action.sql ----------'

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
-- tlgnr_audit_action_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_audit_action_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_audit_action_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_audit_action_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.audit_action_cd)
    into v_exists_flag
    from tlgnr_audit_action_bs bs
    where bs.audit_action_cd = p_audit_action_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_audit_action_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_audit_action_bs set
/* audit_action_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          audit_action_cd = p_audit_action_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_audit_action_cd || ').');
      end if;
    else
      insert into tlgnr_audit_action_bs (
        audit_action_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_audit_action_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_audit_action_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_audit_action_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_audit_action_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_audit_action_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_audit_action_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.audit_action_cd) into v_exists_bs_flag
    from tlgnr_audit_action_bs bs
    where bs.audit_action_cd = p_audit_action_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.audit_action_cd) into v_exists_flag
      from tlgnr_audit_action_tx tx
      where tx.audit_action_cd = p_audit_action_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_audit_action_tx set
/* p_audit_action_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          audit_action_cd = p_audit_action_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_audit_action_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_audit_action_tx (
          audit_action_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_audit_action_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_audit_action_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_audit_action_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_AUDIT_ACTION_BS
execute upsert_bs( 0, 1);
execute upsert_bs( 1, 1);
execute upsert_bs( 2, 1);
execute upsert_bs(10, 1);
execute upsert_bs(11, 1);
execute upsert_bs(12, 1);
execute upsert_bs(13, 1);

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - EN
execute upsert_tx(0, 'en', 'Tablebrowser Insert');
execute upsert_tx(1, 'en', 'Tablebrowser Update');
execute upsert_tx(2, 'en', 'Tablebrowser Delete');
execute upsert_tx(10, 'en', 'User created');
execute upsert_tx(11, 'en', 'Password changed');
execute upsert_tx(12, 'en', 'User inactivated');
execute upsert_tx(13, 'en', 'User reactivated');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - DE
execute upsert_tx(0, 'de', 'Tablebrowser Hinzufügen');
execute upsert_tx(1, 'de', 'Tablebrowser Updaten');
execute upsert_tx(2, 'de', 'Tablebrowser Löschen');
execute upsert_tx(10, 'de', 'Benutzer kreiert');
execute upsert_tx(11, 'de', 'Passwort geändert');
execute upsert_tx(12, 'de', 'Benutzer deaktiviert');
execute upsert_tx(13, 'de', 'Benutzer reaktiviert');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - FR
execute upsert_tx(0, 'fr', 'Tablerecherche Insérer');
execute upsert_tx(1, 'fr', 'Tablerecherche Mettre à jour');
execute upsert_tx(2, 'fr', 'Tablerecherche Annuler');
execute upsert_tx(10, 'fr', 'Utilisateur créé');
execute upsert_tx(11, 'fr', 'Mot de passe modifié');
execute upsert_tx(12, 'fr', 'Utilisateur inactivé');
execute upsert_tx(13, 'fr', 'Utilisateur réactivé');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - IT
execute upsert_tx(0, 'it', 'Tabellabrowser Inserire');
execute upsert_tx(1, 'it', 'Tabellabrowser Aggiornare');
execute upsert_tx(2, 'it', 'Tabellabrowser Eliminare');
execute upsert_tx(10, 'it', 'Utente creato');
execute upsert_tx(11, 'it', 'Parola d''ordine modificata');
execute upsert_tx(12, 'it', 'Utente inattivato');
execute upsert_tx(13, 'it', 'Utente riattivato');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - ES
execute upsert_tx(0, 'es', 'Añadido en el buscador de tablas');
execute upsert_tx(1, 'es', 'Actualizado en el buscador de tablas');
execute upsert_tx(2, 'es', 'Borrado en el buscador de tablas');
execute upsert_tx(10, 'es', 'Usuario creado');
execute upsert_tx(11, 'es', 'Contraseña cambiada');
execute upsert_tx(12, 'es', 'Usuario desactivado');
execute upsert_tx(13, 'es', 'Usuario reactivado');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - CS
execute upsert_tx(0, 'cs', 'Tabulkový prohlížeč - Vložit');
execute upsert_tx(1, 'cs', 'Tabulkový prohlížeč - Aktualizovat');
execute upsert_tx(2, 'cs', 'Tabulkový prohlížeč - smazat');
execute upsert_tx(10, 'cs', 'Založení uživatele');
execute upsert_tx(11, 'cs', 'Heslo bylo změněno');
execute upsert_tx(12, 'cs', 'Deaktivace uživatele');
execute upsert_tx(13, 'cs', 'Aktivace uživatele');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - SK
execute upsert_tx(0, 'sk', 'Tabuľkový prehliadač vložiť');
execute upsert_tx(1, 'sk', 'Tabuľkový prehliadač aktualizovať');
execute upsert_tx(2, 'sk', 'Tabuľkový prehliadač zmazať');
execute upsert_tx(10, 'sk', 'Užívateľ vytvorený');
execute upsert_tx(11, 'sk', 'Heslo zmenené');
execute upsert_tx(12, 'sk', 'Užívateľ deaktivovaný');
execute upsert_tx(13, 'sk', 'Užívateľ aktivovaný');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - HU
execute upsert_tx(0, 'hu', 'Táblázat beszúrása');
execute upsert_tx(1, 'hu', 'Táblázat frissítése ');
execute upsert_tx(2, 'hu', 'Táblázat törlése');
execute upsert_tx(10, 'hu', 'Felhasználó létrehozása');
execute upsert_tx(11, 'hu', 'Jelszó megváltoztatása');
execute upsert_tx(12, 'hu', 'Felhasználó inaktivitása');
execute upsert_tx(13, 'hu', 'Felhasználó újra aktiválása');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - RO
execute upsert_tx(0, 'ro', 'Inserare tablebrowser');
execute upsert_tx(1, 'ro', 'Actualizare tablebrowser');
execute upsert_tx(2, 'ro', 'Stergere tablebrowser');
execute upsert_tx(10, 'ro', 'Utilizator creat');
execute upsert_tx(11, 'ro', 'Parola schimbata');
execute upsert_tx(12, 'ro', 'Utilizator inactivat');
execute upsert_tx(13, 'ro', 'Utilizator reactivat');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - BG
execute upsert_tx(0, 'bg', 'Вмъкване на таблица браузер');
execute upsert_tx(1, 'bg', 'Обновяване на таблица браузер');
execute upsert_tx(2, 'bg', 'Изтриване на таблица браузер');
execute upsert_tx(10, 'bg', 'Създаване на потребител');
execute upsert_tx(11, 'bg', 'Паролата е променена');
execute upsert_tx(12, 'bg', 'Потребителят се инактивира');
execute upsert_tx(13, 'bg', 'Потребителят се активира');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - RU (ENGLISH COPY)
execute upsert_tx(0, 'ru', 'Вставить Tablebrowser');
execute upsert_tx(1, 'ru', 'Обновить Tablebrowser');
execute upsert_tx(2, 'ru', 'Удалить Tablebrowser');
execute upsert_tx(10, 'ru', 'пользователь создан');
execute upsert_tx(11, 'ru', 'пароль изменен');
execute upsert_tx(12, 'ru', 'пользователь заблокирован');
execute upsert_tx(13, 'ru', 'пользователь восстановлен');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - PL (ENGLISH COPY)
execute upsert_tx(0, 'pl', 'Wstawienie przeglądu tabeli');
execute upsert_tx(1, 'pl', 'Aktualizacja przegląd tabeli');
execute upsert_tx(2, 'pl', 'Usuń przegląd tabeli');
execute upsert_tx(10, 'pl', 'Użytkownik utworzony');
execute upsert_tx(11, 'pl', 'Hasło zostało zmienione');
execute upsert_tx(12, 'pl', 'Użytkownik inaktywowany');
execute upsert_tx(13, 'pl', 'Użytkownik reaktywowany');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - AZ (ENGLISH COPY)
execute upsert_tx(0, 'az', 'Tablebrowser Insert');
execute upsert_tx(1, 'az', 'Tablebrowser Update');
execute upsert_tx(2, 'az', 'Tablebrowser Delete');
execute upsert_tx(10, 'az', 'User created');
execute upsert_tx(11, 'az', 'Password changed');
execute upsert_tx(12, 'az', 'User inactivated');
execute upsert_tx(13, 'az', 'User reactivated');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - HR (ENGLISH COPY)
execute upsert_tx(0, 'hr', 'Tablica preglednik Umetni');
execute upsert_tx(1, 'hr', 'Tablica preglednik Ažurirati');
execute upsert_tx(2, 'hr', 'Tablica preglednik Obriši');
execute upsert_tx(10, 'hr', 'Korisnik kreiran');
execute upsert_tx(11, 'hr', 'Lozinka promijenjena');
execute upsert_tx(12, 'hr', 'Korisnik inaktiviran');
execute upsert_tx(13, 'hr', 'Korisnik aktiviran');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - SR (ENGLISH COPY)
execute upsert_tx(0, 'sr', 'Tablebrowser Insert');
execute upsert_tx(1, 'sr', 'Tablebrowser Update');
execute upsert_tx(2, 'sr', 'Tablebrowser Delete');
execute upsert_tx(10, 'sr', 'Kreiran korisnik');
execute upsert_tx(11, 'sr', 'Promenjena lozinka');
execute upsert_tx(12, 'sr', 'Korisnik neaktivan');
execute upsert_tx(13, 'sr', 'Korisnik ponovo aktivan');

prompt INSERTING into TLGNR_AUDIT_ACTION_TX - TR
execute upsert_tx(0, 'tr', 'Tablebrowser Insert');
execute upsert_tx(1, 'tr', 'Tablebrowser Update');
execute upsert_tx(2, 'tr', 'Tablebrowser Delete');
execute upsert_tx(10, 'tr', 'User created');
execute upsert_tx(11, 'tr', 'Password changed');
execute upsert_tx(12, 'tr', 'User inactivated');
execute upsert_tx(13, 'tr', 'User reactivated');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

