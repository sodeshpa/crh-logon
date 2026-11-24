-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_user_activity_action.sql ----------'

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
-- tlgnr_user_activity_action_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_user_activity_action_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_user_activity_action_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_user_activity_action_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.user_activity_action_cd)
    into v_exists_flag
    from tlgnr_user_activity_action_bs bs
    where bs.user_activity_action_cd = p_user_activity_action_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_user_activity_action_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_user_activity_action_bs set
/* user_activity_action_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          user_activity_action_cd = p_user_activity_action_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_user_activity_action_cd || ').');
      end if;
    else
      insert into tlgnr_user_activity_action_bs (
        user_activity_action_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_user_activity_action_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_user_activity_action_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_user_activity_action_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_user_activity_action_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_user_activity_action_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_user_activity_action_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.user_activity_action_cd) into v_exists_bs_flag
    from tlgnr_user_activity_action_bs bs
    where bs.user_activity_action_cd = p_user_activity_action_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.user_activity_action_cd) into v_exists_flag
      from tlgnr_user_activity_action_tx tx
      where tx.user_activity_action_cd = p_user_activity_action_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_user_activity_action_tx set
/* p_user_activity_action_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          user_activity_action_cd = p_user_activity_action_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_user_activity_action_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_user_activity_action_tx (
          user_activity_action_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_user_activity_action_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_user_activity_action_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_user_activity_action_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_BS
-- user related 0 - 19
execute upsert_bs( 0,  1);
execute upsert_bs( 1,  1);
execute upsert_bs( 2,  1);
execute upsert_bs( 3,  1);
execute upsert_bs( 4,  1);
execute upsert_bs( 5,  1);
execute upsert_bs( 6,  1);
-- external login related 20 - 39
execute upsert_bs( 20,  1);
execute upsert_bs( 21,  1);
execute upsert_bs( 22,  1);
execute upsert_bs( 23,  1);
execute upsert_bs( 24,  1);
execute upsert_bs( 25,  1);
execute upsert_bs( 26,  1);
execute upsert_bs( 27,  1);
execute upsert_bs( 28,  1);
execute upsert_bs( 29,  1);
execute upsert_bs( 30,  1);
-- terminal related 40 - 59
execute upsert_bs( 40,  1);
execute upsert_bs( 41,  1);
execute upsert_bs( 42,  1);
execute upsert_bs( 43,  1);
execute upsert_bs( 44,  1);
execute upsert_bs( 45,  1);
-- pda related 60 - 79
execute upsert_bs( 60,  1);
execute upsert_bs( 61,  1);
execute upsert_bs( 62,  1);
execute upsert_bs( 63,  1);
execute upsert_bs( 64,  1);
execute upsert_bs( 65,  1);

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - EN
execute upsert_tx(0, 'en', 'User created');
execute upsert_tx(1, 'en', 'User reactivated');
execute upsert_tx(2, 'en', 'User inactivated');
execute upsert_tx(3, 'en', 'User login successful');
execute upsert_tx(4, 'en', 'User login failed');
execute upsert_tx(5, 'en', 'User logged out');
execute upsert_tx(6, 'en', 'User logged out, session expired');
execute upsert_tx(20, 'en', 'External login activated');
execute upsert_tx(21, 'en', 'External login deactivated');
execute upsert_tx(22, 'en', 'External login successful');
execute upsert_tx(23, 'en', 'External login failed');
execute upsert_tx(24, 'en', 'External log out');
execute upsert_tx(25, 'en', 'External log out, session expired');
execute upsert_tx(26, 'en', 'External password unlocked');
execute upsert_tx(27, 'en', 'External password locked expired');
execute upsert_tx(28, 'en', 'External pwd locked max. login attempt');
execute upsert_tx(29, 'en', 'External password unlock attempt failed');
execute upsert_tx(30, 'en', 'External password changed');
execute upsert_tx(40, 'en', 'Terminal login activated');
execute upsert_tx(41, 'en', 'Terminal login deactivated');
execute upsert_tx(42, 'en', 'Terminal login successful');
execute upsert_tx(43, 'en', 'Terminal login failed');
execute upsert_tx(44, 'en', 'Terminal log out');
execute upsert_tx(45, 'en', 'Terminal log out, session expired');
execute upsert_tx(60, 'en', 'PDA login activated');
execute upsert_tx(61, 'en', 'PDA login deactivated');
execute upsert_tx(62, 'en', 'PDA login successful');
execute upsert_tx(63, 'en', 'PDA login failed');
execute upsert_tx(64, 'en', 'PDA log out');
execute upsert_tx(65, 'en', 'PDA log out, session expired');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - DE
execute upsert_tx(0, 'de', 'Benutzer kreiert');
execute upsert_tx(1, 'de', 'Benutzer reaktiviert');
execute upsert_tx(2, 'de', 'Benutzer deaktiviert');
execute upsert_tx(3, 'de', 'Benutzeranmeldung erfolgreich');
execute upsert_tx(4, 'de', 'Benutzeranmeldung fehlgeschlagen');
execute upsert_tx(5, 'de', 'Benutzer loged aus');
execute upsert_tx(6, 'de', 'Benutzer loged out abgelaufen');
execute upsert_tx(20, 'de', 'Externe Anmeldung aktiviert');
execute upsert_tx(21, 'de', 'Externe Login deaktiviert');
execute upsert_tx(22, 'de', 'Externe Anmeldung erfolgreich');
execute upsert_tx(23, 'de', 'Externe Anmeldung fehlgeschlagen');
execute upsert_tx(24, 'de', 'Externe loged aus');
execute upsert_tx(25, 'de', 'Externe loged aus abgelaufen');
execute upsert_tx(26, 'de', 'Externe Passwort entsperrt');
execute upsert_tx(27, 'de', 'Externe Passwort gesperrt abgelaufen');
execute upsert_tx(28, 'de', 'Externe Passw gesperrt max. Login-Vers');
execute upsert_tx(29, 'de', 'Ext Passwort Entsperrversuch scheiterte');
execute upsert_tx(30, 'de', 'Externe Passwort geändert');
execute upsert_tx(40, 'de', 'Terminal Anmeldung aktiviert');
execute upsert_tx(41, 'de', 'Terminal Login deaktiviert');
execute upsert_tx(42, 'de', 'Terminal Anmeldung erfolgreich');
execute upsert_tx(43, 'de', 'Terminal Anmeldung fehlgeschlagen');
execute upsert_tx(44, 'de', 'Terminal loged aus');
execute upsert_tx(45, 'de', 'Terminal loged aus abgelaufen');
execute upsert_tx(60, 'de', 'PDA Anmeldung aktiviert');
execute upsert_tx(61, 'de', 'PDA Login deaktiviert');
execute upsert_tx(62, 'de', 'PDA Anmeldung erfolgreich');
execute upsert_tx(63, 'de', 'PDA Anmeldung fehlgeschlagen');
execute upsert_tx(64, 'de', 'PDA loged aus');
execute upsert_tx(65, 'de', 'PDA loged aus abgelaufen');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - FR
execute upsert_tx(0, 'fr', 'Utilisateur créé');
execute upsert_tx(1, 'fr', 'Utilisateur réactivé');
execute upsert_tx(2, 'fr', 'Utilisateur inactivé');
execute upsert_tx(3, 'fr', 'Utilisateur connecté correctement');
execute upsert_tx(4, 'fr', 'Connexion d''utilisateur échouée');
execute upsert_tx(5, 'fr', 'Utilisateur déconnecté');
execute upsert_tx(6, 'fr', 'Utilisateur déconnecté, session expirée');
execute upsert_tx(20, 'fr', 'Connexion externe activée');
execute upsert_tx(21, 'fr', 'Connexion externe désactivée');
execute upsert_tx(22, 'fr', 'Connexion externe réussie');
execute upsert_tx(23, 'fr', 'Connexion externe échouée');
execute upsert_tx(24, 'fr', 'Connexion externe déconnectée');
execute upsert_tx(25, 'fr', 'Connexion externe déconnectée, session expirée');
execute upsert_tx(26, 'fr', 'Mot de passe externe déverrouillé');
execute upsert_tx(27, 'fr', 'Mot de passe externe verrouillé expiré');
execute upsert_tx(28, 'fr', 'Mot de passe externe verrouillé tentative de connexion maximale');
execute upsert_tx(29, 'fr', 'Mot de passe externe déverrouiller tentative a échoué');
execute upsert_tx(30, 'fr', 'Mot de passe externe a changé');
execute upsert_tx(40, 'fr', 'Connexion de terminal activée');
execute upsert_tx(41, 'fr', 'Connexion de terminal désactivée');
execute upsert_tx(42, 'fr', 'Connexion de terminal réussie');
execute upsert_tx(43, 'fr', 'Connexion de terminal échouée');
execute upsert_tx(44, 'fr', 'Déconnexion de terminal');
execute upsert_tx(45, 'fr', 'Déconnexion de terminal, session expirée');
execute upsert_tx(60, 'fr', 'Connexion de PDA activée');
execute upsert_tx(61, 'fr', 'Connexion de PDA désactivée');
execute upsert_tx(62, 'fr', 'Connexion de PDA réussie');
execute upsert_tx(63, 'fr', 'Connexion de PDA échouée');
execute upsert_tx(64, 'fr', 'Déconnexion de PDA');
execute upsert_tx(65, 'fr', 'Déconnexion de PDA, session expirée');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - IT
execute upsert_tx(0, 'it', 'Utente creato');
execute upsert_tx(1, 'it', 'Utente riattivato');
execute upsert_tx(2, 'it', 'Utente inattivato');
execute upsert_tx(3, 'it', 'Login utente successo');
execute upsert_tx(4, 'it', 'Login utente non riuscito');
execute upsert_tx(5, 'it', 'Utente disconnesso');
execute upsert_tx(6, 'it', 'Utente disconnesso scaduto');
execute upsert_tx(20, 'it', 'Login esterno attivato');
execute upsert_tx(21, 'it', 'Login esterno disattivato');
execute upsert_tx(22, 'it', 'Login esterno successo');
execute upsert_tx(23, 'it', 'Login esterno non riuscito');
execute upsert_tx(24, 'it', 'Esterno disconnesso');
execute upsert_tx(25, 'it', 'Esterno disconnesso scaduto');
execute upsert_tx(26, 'it', 'Parola d''ordine esterna sbloccata');
execute upsert_tx(27, 'it', 'Parola d''ordine esterna bloccata scaduta');
execute upsert_tx(28, 'it', 'Parola d''ordine esterna bloccata tentativo di accesso massimo');
execute upsert_tx(29, 'it', 'Parola d''ordine esterna sblocco tentativo non risciuto');
execute upsert_tx(30, 'it', 'Parola d''ordine esterna cambiata');
execute upsert_tx(40, 'it', 'Login terminal attivato');
execute upsert_tx(41, 'it', 'Login terminal disattivato');
execute upsert_tx(42, 'it', 'Login terminal successo');
execute upsert_tx(43, 'it', 'Login terminal non riuscito');
execute upsert_tx(44, 'it', 'Terminale disconnesso');
execute upsert_tx(45, 'it', 'Terminale disconnesso scaduto');
execute upsert_tx(60, 'it', 'Login PDA attivato');
execute upsert_tx(61, 'it', 'Login PDA disattivato');
execute upsert_tx(62, 'it', 'Login PDA successo');
execute upsert_tx(63, 'it', 'Login PDA non riuscito');
execute upsert_tx(64, 'it', 'PDA disconnesso');
execute upsert_tx(65, 'it', 'PDA disconnesso scaduto');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - ES
execute upsert_tx(0, 'es', 'Usuario creado');
execute upsert_tx(1, 'es', 'Usuario reactivado');
execute upsert_tx(2, 'es', 'Usuario desactivado');
execute upsert_tx(3, 'es', 'Acceso usuario satisfactoriamente');
execute upsert_tx(4, 'es', 'Acceso usuario fallido');
execute upsert_tx(5, 'es', 'Usuario desconectado');
execute upsert_tx(6, 'es', 'Desconexión usuario expirada');
execute upsert_tx(20, 'es', 'Acceso externo activado');
execute upsert_tx(21, 'es', 'Acceso externo desactivado');
execute upsert_tx(22, 'es', 'Acceso externo correcto');
execute upsert_tx(23, 'es', 'Acceso externo fallido');
execute upsert_tx(24, 'es', 'Desconexión externa');
execute upsert_tx(25, 'es', 'Desconexión externa expirada');
execute upsert_tx(26, 'es', 'Contraseña externa desbloqueada');
execute upsert_tx(27, 'es', 'Bloqueo contraseña externa expirado');
execute upsert_tx(28, 'es', 'Contraseña externa bloqueada número máx. intentos de acceso');
execute upsert_tx(29, 'es', 'Intento fallido desbloqueo contraseña externa');
execute upsert_tx(30, 'es', 'Contraseña externa cambiada');
execute upsert_tx(40, 'es', 'Acceso terminal activado');
execute upsert_tx(41, 'es', 'Acceso terminal desactivado');
execute upsert_tx(42, 'es', 'Acceso correcto terminal');
execute upsert_tx(43, 'es', 'Acceso fallido terminal');
execute upsert_tx(44, 'es', 'Terminal desconectado');
execute upsert_tx(45, 'es', 'Desconexión terminal expirada');
execute upsert_tx(60, 'es', 'Acceso PDA activado');
execute upsert_tx(61, 'es', 'Acceso PDA desactivado');
execute upsert_tx(62, 'es', 'Acceso PDA correcto');
execute upsert_tx(63, 'es', 'Acceso PDA fallido');
execute upsert_tx(64, 'es', 'PDA desconectado');
execute upsert_tx(65, 'es', 'Desconexión PDA expirada');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - CS
execute upsert_tx(0, 'cs', 'Uživatel založen');
execute upsert_tx(1, 'cs', 'Uživatel znovu aktivován');
execute upsert_tx(2, 'cs', 'Uživatel deaktivován');
execute upsert_tx(3, 'cs', 'Přihlášení uživatele bylo úspěšné');
execute upsert_tx(4, 'cs', 'Přihlášení uživatele selhalo');
execute upsert_tx(5, 'cs', 'Uživatel odhlášen');
execute upsert_tx(6, 'cs', 'Uživatel odhlášen, relace vypršela');
execute upsert_tx(20, 'cs', 'Externí přihlášení založeno');
execute upsert_tx(21, 'cs', 'Externí přihlášení deaktivováno');
execute upsert_tx(22, 'cs', 'Externí přihlášení úspěšné');
execute upsert_tx(23, 'cs', 'Externí přihlášení selhalo');
execute upsert_tx(24, 'cs', 'Externí odhlášeno');
execute upsert_tx(25, 'cs', 'Externí odhlášeno, relace vypršela');
execute upsert_tx(26, 'cs', 'Externí heslo zablokováno');
execute upsert_tx(27, 'cs', 'Externí heslo zablokování vypršelo');
execute upsert_tx(28, 'cs', 'Externí heslo zablokováno po dosažení max. počtu pokusů o přihlášení');
execute upsert_tx(29, 'cs', 'Externí heslo - selhal pokus o odblokování');
execute upsert_tx(30, 'cs', 'Externí heslo změněno');
execute upsert_tx(40, 'cs', 'Terminálové přihlášení založeno');
execute upsert_tx(41, 'cs', 'Terminálové přihlášení deaktivováno');
execute upsert_tx(42, 'cs', 'Terminálové příhlášení úspěšné');
execute upsert_tx(43, 'cs', 'Terminálové přihlášení selhalo');
execute upsert_tx(44, 'cs', 'Terminálové přihlášení ukončeno');
execute upsert_tx(45, 'cs', 'Terminálové přihlášení ukončeno, relace vypršela');
execute upsert_tx(60, 'cs', 'PDA příhlášení založeno');
execute upsert_tx(61, 'cs', 'PDA příhlášení deaktivováno');
execute upsert_tx(62, 'cs', 'Terminálové příhlášení úspěšné');
execute upsert_tx(63, 'cs', 'PDA příhlášení selhalo');
execute upsert_tx(64, 'cs', 'PDA příhlášení ukončeno');
execute upsert_tx(65, 'cs', 'PDA příhlášení ukončeno, relace vypršela');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - SK
execute upsert_tx(0, 'sk', 'Užívateľ vytvorený');
execute upsert_tx(1, 'sk', 'Užívateľ deaktivovaný');
execute upsert_tx(2, 'sk', 'Užívateľ aktivovaný');
execute upsert_tx(3, 'sk', 'Užívateľ úspešne prihlásený');
execute upsert_tx(4, 'sk', 'Prihlásenie užívateľa neúspešné');
execute upsert_tx(5, 'sk', 'Užívateľ odhlásený');
execute upsert_tx(6, 'sk', 'Užívateľ odhlásený, prihlásenie vypršalo');
execute upsert_tx(20, 'sk', 'Externé prihlásenie aktivované');
execute upsert_tx(21, 'sk', 'Externé prihlásenie deaktivované');
execute upsert_tx(22, 'sk', 'Externé prihlásenie úspešné');
execute upsert_tx(23, 'sk', 'Externé prihlásenie neúspešné');
execute upsert_tx(24, 'sk', 'Externé odhlásenie');
execute upsert_tx(25, 'sk', 'Externé odhlásenie, prihlásenie vypršalo');
execute upsert_tx(26, 'sk', 'Externé heslo odblokované');
execute upsert_tx(27, 'sk', 'Externé heslo blokované, vypršalo');
execute upsert_tx(28, 'sk', 'Externé heslo blokované max. prihlasovacích pokusov');
execute upsert_tx(29, 'sk', 'Pokus o odblokovanie externého hesla neúspešné');
execute upsert_tx(30, 'sk', 'Externé heslo zmenené');
execute upsert_tx(40, 'sk', 'Terminálové prihlásenie aktivované');
execute upsert_tx(41, 'sk', 'Terminálové prihlásenie deaktivované');
execute upsert_tx(42, 'sk', 'Terminálové prihlásenie úspešné');
execute upsert_tx(43, 'sk', 'Terminálové prihlásenie neúspešné');
execute upsert_tx(44, 'sk', 'Terminálové odhlásenie');
execute upsert_tx(45, 'sk', 'Terminálové odhlásenie, prihlásenie vypršalo');
execute upsert_tx(60, 'sk', 'PDA prihlásenie aktivované');
execute upsert_tx(61, 'sk', 'PDA prihlásenie deaktivované');
execute upsert_tx(62, 'sk', 'PDA prihlásenie úspešné');
execute upsert_tx(63, 'sk', 'PDA prihlásenie neúspešné');
execute upsert_tx(64, 'sk', 'PDA odhlásenie');
execute upsert_tx(65, 'sk', 'PDA odhlásenie, prihlásenie vypršalo');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - HU
execute upsert_tx(0, 'hu', 'Felhasználó létrehozva');
execute upsert_tx(1, 'hu', 'Felhasználó újra aktiválása');
execute upsert_tx(2, 'hu', 'Felhasználó inaktivitása');
execute upsert_tx(3, 'hu', 'Sikeres bejelentkezés');
execute upsert_tx(4, 'hu', 'Sikeres bejelentkezés');
execute upsert_tx(5, 'hu', 'Felhasználó kijelentkezett');
execute upsert_tx(6, 'hu', 'Felhasználó kijelentkezett, kapcsolat lejárt');
execute upsert_tx(20, 'hu', 'Külsö bejelentkezés aktív');
execute upsert_tx(21, 'hu', 'Külső bejelenetkezés kikapcsolva');
execute upsert_tx(22, 'hu', 'Sikeres külső bejelentkezés');
execute upsert_tx(23, 'hu', 'Sikertelen külső bejelentkezés');
execute upsert_tx(24, 'hu', 'Külső kijelentkezett');
execute upsert_tx(25, 'hu', 'Külső kijelentkezett, kapcsolat lejárt');
execute upsert_tx(26, 'hu', 'Külső jelszó nyitott');
execute upsert_tx(27, 'hu', 'Külső jelszó zárolása lejárt');
execute upsert_tx(28, 'hu', 'Külső jelszó zárolva, maximul jelenetkezési kísérlet');
execute upsert_tx(29, 'hu', 'Külső jelszó feloldási kísérlete nem sikerült');
execute upsert_tx(30, 'hu', 'Külső jelszó megváltoztatva');
execute upsert_tx(40, 'hu', 'Terminál bejelentkezés aktív');
execute upsert_tx(41, 'hu', 'Terminális bejelentkezés kikapcsolva');
execute upsert_tx(42, 'hu', 'Sikeres terminál bejelentkezés');
execute upsert_tx(43, 'hu', 'Sikertelen terminál bejelentkezés');
execute upsert_tx(44, 'hu', 'Terminál kijelentkezett');
execute upsert_tx(45, 'hu', 'Terminál kijelentkezett, kapcsolat lejárt');
execute upsert_tx(60, 'hu', 'PDA bejelentkezés aktív');
execute upsert_tx(61, 'hu', 'PDA bejelentkezés kikapcsolva');
execute upsert_tx(62, 'hu', 'Sikeres PDA bejelentkezés');
execute upsert_tx(63, 'hu', 'Sikertelen PDA bejelentkezés');
execute upsert_tx(64, 'hu', 'PDA kijelentkezett');
execute upsert_tx(65, 'hu', 'PDA kijelentkezett, kapcsolat lejárt');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - RO
execute upsert_tx(0, 'ro', 'Utilizator creat');
execute upsert_tx(1, 'ro', 'Utilizator reactivat');
execute upsert_tx(2, 'ro', 'Utilizator dezactivat');
execute upsert_tx(3, 'ro', 'Conectare utilizator reusita');
execute upsert_tx(4, 'ro', 'Conectare utilizator esuata');
execute upsert_tx(5, 'ro', 'Utilizator deconectat');
execute upsert_tx(6, 'ro', 'Utilizator deconectat, sesiune expirata');
execute upsert_tx(20, 'ro', 'Conectare externa activata');
execute upsert_tx(21, 'ro', 'Conectare externa dezactivata');
execute upsert_tx(22, 'ro', 'Conectare externa reusita');
execute upsert_tx(23, 'ro', 'Conectare externa esua');
execute upsert_tx(24, 'ro', 'Deconectare externa');
execute upsert_tx(25, 'ro', 'Deconectare externa, sesiune expirata');
execute upsert_tx(26, 'ro', 'Parola externa deblocata');
execute upsert_tx(27, 'ro', 'Parola externa blocata a expirat');
execute upsert_tx(28, 'ro', 'Parola externa blocata, numar maxim de incercari de conectare');
execute upsert_tx(29, 'ro', 'Incercare deblocare parola externa esuata');
execute upsert_tx(30, 'ro', 'Parola externa schimbata');
execute upsert_tx(40, 'ro', 'Conectare terminal activata');
execute upsert_tx(41, 'ro', 'Conectare terminal dezactivata');
execute upsert_tx(42, 'ro', 'Conectare terminal reusita');
execute upsert_tx(43, 'ro', 'Conectare terminal esuata');
execute upsert_tx(44, 'ro', 'Deconectare terminal');
execute upsert_tx(45, 'ro', 'Deconectare terminal, sesiune expirata');
execute upsert_tx(60, 'ro', 'Conectare PDA activata');
execute upsert_tx(61, 'ro', 'Conectare PDA dezactivata');
execute upsert_tx(62, 'ro', 'Conectare PDA reusita');
execute upsert_tx(63, 'ro', 'Conectare PDA esuata');
execute upsert_tx(64, 'ro', 'Deconectare PDA');
execute upsert_tx(65, 'ro', 'Deconectare PDA, sesiune expirata');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - BG
execute upsert_tx(0, 'bg', 'Създаване на потребител');
execute upsert_tx(1, 'bg', 'Активиране на потребител');
execute upsert_tx(2, 'bg', 'Деактивиране на потребител');
execute upsert_tx(3, 'bg', 'Успешно влизане на потребител');
execute upsert_tx(4, 'bg', 'Неуспешно влизане на потребител');
execute upsert_tx(5, 'bg', 'Излизане на потребител');
execute upsert_tx(6, 'bg', 'Излизане на потребител, изтичане на сесия');
execute upsert_tx(20, 'bg', 'Активиране на външно влизане');
execute upsert_tx(21, 'bg', 'Деактивиране на външно влизане');
execute upsert_tx(22, 'bg', 'Успешно външно влизане');
execute upsert_tx(23, 'bg', 'Неуспешно външно влизане');
execute upsert_tx(24, 'bg', 'Външно излизане');
execute upsert_tx(25, 'bg', 'Външно излизане, изтичане на сесия');
execute upsert_tx(26, 'bg', 'Външната парола е отключена');
execute upsert_tx(27, 'bg', 'Изтичане на заключена външна парола');
execute upsert_tx(28, 'bg', 'Заключена външна парола; максимално разрешени опити');
execute upsert_tx(29, 'bg', 'Проваляне на външен опит за отключване на парола');
execute upsert_tx(30, 'bg', 'Промяна на външна парола');
execute upsert_tx(40, 'bg', 'Активиране на вход на терминала');
execute upsert_tx(41, 'bg', 'Деактивиране на вход на терминала');
execute upsert_tx(42, 'bg', 'Успешно влизане на терминала');
execute upsert_tx(43, 'bg', 'Пропадна влизане на терминала');
execute upsert_tx(44, 'bg', 'Излизане от терминала');
execute upsert_tx(45, 'bg', 'Излитане от терминала, сесията изтече');
execute upsert_tx(60, 'bg', 'PDA активиране на вход');
execute upsert_tx(61, 'bg', 'PDA деактивиране на вход');
execute upsert_tx(62, 'bg', 'PDA успешно влизане');
execute upsert_tx(63, 'bg', 'PDA неуспешно влизане');
execute upsert_tx(64, 'bg', 'PDA излизане');
execute upsert_tx(65, 'bg', 'PDA излизане, сесията изтече');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - RU (ENGLISH COPY)
execute upsert_tx(0, 'ru', 'Пользователь создан');
execute upsert_tx(1, 'ru', 'Пользователь восстановлен');
execute upsert_tx(2, 'ru', 'пользователь заблокирован');
execute upsert_tx(3, 'ru', 'Вход прошел успешно');
execute upsert_tx(4, 'ru', 'Вход прошел неуспешно');
execute upsert_tx(5, 'ru', 'Пользователь вышел из системы');
execute upsert_tx(6, 'ru', 'Пользователь вышел из системы, сессия истекла');
execute upsert_tx(20, 'ru', 'Внешний вход активирован');
execute upsert_tx(21, 'ru', 'Внешний вход выключен');
execute upsert_tx(22, 'ru', 'Внешний вход успешен');
execute upsert_tx(23, 'ru', 'Внешний вход не успешен');
execute upsert_tx(24, 'ru', 'Внешний выход');
execute upsert_tx(25, 'ru', 'Внешний выход, сессия истекла\окончена');
execute upsert_tx(26, 'ru', 'Внешний пароль разблокирован');
execute upsert_tx(27, 'ru', 'Внешний пароль заблокирован истек');
execute upsert_tx(28, 'ru', 'Внешний пароль заблокирован: макс. кол-во неудачных попыток входа');
execute upsert_tx(29, 'ru', 'Внешняя попытка разблокировки не удалась');
execute upsert_tx(30, 'ru', 'Внешний пароль изменен');
execute upsert_tx(40, 'ru', 'вход в терминал активирован');
execute upsert_tx(41, 'ru', 'вход в теминал отключен');
execute upsert_tx(42, 'ru', 'вход в терминал прошел успешно');
execute upsert_tx(43, 'ru', 'вход в терминал прошел не успешно');
execute upsert_tx(44, 'ru', 'выход из терминала');
execute upsert_tx(45, 'ru', 'выход из терминала, истекла рабочая сессия');
execute upsert_tx(60, 'ru', 'вход в КПК активирован');
execute upsert_tx(61, 'ru', 'вход в КПК отключен');
execute upsert_tx(62, 'ru', 'вход в КПК прошел успешно');
execute upsert_tx(63, 'ru', 'вход в КПК прошел не успешноd');
execute upsert_tx(64, 'ru', 'выход из КПК');
execute upsert_tx(65, 'ru', 'выход из КПК, истекла рабочая сессия');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - PL
execute upsert_tx(0, 'pl', 'Użytkownik utworzony');
execute upsert_tx(1, 'pl', 'Użytkownik reaktywowany');
execute upsert_tx(2, 'pl', 'Użytkownik inaktywowany');
execute upsert_tx(3, 'pl', 'Nazwa użytkownika sukces');
execute upsert_tx(4, 'pl', 'Nazwa użytkownika nie powiodło się');
execute upsert_tx(5, 'pl', 'Użytkownik wylogowany');
execute upsert_tx(6, 'pl', 'Użytkownik wylogowany , sesja wygasła');
execute upsert_tx(20, 'pl', 'Zewnętrzne login aktywowany');
execute upsert_tx(21, 'pl', 'Zewnętrzna login deaktywowany');
execute upsert_tx(22, 'pl', 'Zewnętrzne logowanie sukces');
execute upsert_tx(23, 'pl', 'Zewnętrzne logowanie nie powiodło się');
execute upsert_tx(24, 'pl', 'Zewnętrzne wylogowanie się');
execute upsert_tx(25, 'pl', 'Zewnętrzne wylogowanie się, sesja wygasła');
execute upsert_tx(26, 'pl', 'Zewnętrzna hasło odblokowane');
execute upsert_tx(27, 'pl', 'Zewnętrzne zablokowane hasło wygasło');
execute upsert_tx(28, 'pl', 'Zewnętrzne hasło zablokowane (maksymalna próba logowania)');
execute upsert_tx(29, 'pl', 'Próba odblokowania zewnętrznego hasła nie powiodła się');
execute upsert_tx(30, 'pl', 'Zewnętrzne hasło zmienione');
execute upsert_tx(40, 'pl', 'Terminal logowania aktywowano');
execute upsert_tx(41, 'pl', 'Terminal logowania deaktywowano');
execute upsert_tx(42, 'pl', 'Logowanie przez terminal powiodło się');
execute upsert_tx(43, 'pl', 'Logowanie przez terminal nie powiodło się');
execute upsert_tx(44, 'pl', 'Wylogowanie się z terminalu');
execute upsert_tx(45, 'pl', 'Wylogowanie się z terminalu, sesja wygasła');
execute upsert_tx(60, 'pl', 'Logowanie PDA aktywowane');
execute upsert_tx(61, 'pl', 'Logowanie PDA deaktywowane');
execute upsert_tx(62, 'pl', 'Logowanie PDA zakończyło się sukcesem');
execute upsert_tx(63, 'pl', 'Logowanie PDA nie powiodło się');
execute upsert_tx(64, 'pl', 'Wylogowano z PDA');
execute upsert_tx(65, 'pl', 'Wylogowano z PDA, sesja wygasła');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - AZ (ENGLISH COPY)
execute upsert_tx(0, 'az', 'User created');
execute upsert_tx(1, 'az', 'User reactivated');
execute upsert_tx(2, 'az', 'User inactivated');
execute upsert_tx(3, 'az', 'User login successful');
execute upsert_tx(4, 'az', 'User login failed');
execute upsert_tx(5, 'az', 'User logged out');
execute upsert_tx(6, 'az', 'User logged out, session expired');
execute upsert_tx(20, 'az', 'External login activated');
execute upsert_tx(21, 'az', 'External login deactivated');
execute upsert_tx(22, 'az', 'External login successful');
execute upsert_tx(23, 'az', 'External login failed');
execute upsert_tx(24, 'az', 'External log out');
execute upsert_tx(25, 'az', 'External log out, session expired');
execute upsert_tx(26, 'az', 'External password unlocked');
execute upsert_tx(27, 'az', 'External password locked expired');
execute upsert_tx(28, 'az', 'External pwd locked max. login attempt');
execute upsert_tx(29, 'az', 'External password unlock attempt failed');
execute upsert_tx(30, 'az', 'External password changed');
execute upsert_tx(40, 'az', 'Terminal login activated');
execute upsert_tx(41, 'az', 'Terminal login deactivated');
execute upsert_tx(42, 'az', 'Terminal login successful');
execute upsert_tx(43, 'az', 'Terminal login failed');
execute upsert_tx(44, 'az', 'Terminal log out');
execute upsert_tx(45, 'az', 'Terminal log out, session expired');
execute upsert_tx(60, 'az', 'PDA login activated');
execute upsert_tx(61, 'az', 'PDA login deactivated');
execute upsert_tx(62, 'az', 'PDA login successful');
execute upsert_tx(63, 'az', 'PDA login failed');
execute upsert_tx(64, 'az', 'PDA log out');
execute upsert_tx(65, 'az', 'PDA log out, session expired');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - HR
execute upsert_tx(0, 'hr', 'Korisnik kreiran');
execute upsert_tx(1, 'hr', 'Korisnik ponovno aktiviran');
execute upsert_tx(2, 'hr', 'Korisnik inaktivirani');
execute upsert_tx(3, 'hr', 'Prijava korisnika uspješna');
execute upsert_tx(4, 'hr', 'Korisnik prijava nije uspjela');
execute upsert_tx(5, 'hr', 'Korisnik odjavljen');
execute upsert_tx(6, 'hr', 'Korisnik odjavljen, Sesije istekla');
execute upsert_tx(20, 'hr', 'Vanjska prijava aktivirana');
execute upsert_tx(21, 'hr', 'Vanjska prijava deaktivirana');
execute upsert_tx(22, 'hr', 'Vanjska prijava uspješna');
execute upsert_tx(23, 'hr', 'Vanjska prijava nije uspjela');
execute upsert_tx(24, 'hr', 'Vanjska odjava');
execute upsert_tx(25, 'hr', 'Vanjska odjava, Sesije istekla');
execute upsert_tx(26, 'hr', 'Vanjska lozinka otključana');
execute upsert_tx(27, 'hr', 'Vanjska lozinka zaključana istekla');
execute upsert_tx(28, 'hr', 'Vanjska lozinka zaključana maks. pokušaj prijava');
execute upsert_tx(29, 'hr', 'Vanjska lozinka za otključavanje pokušaj nije uspio');
execute upsert_tx(30, 'hr', 'Vanjska lozinka je promijenjena');
execute upsert_tx(40, 'hr', 'Terminal prijava aktivirana');
execute upsert_tx(41, 'hr', 'Terminal prijava deaktivirana');
execute upsert_tx(42, 'hr', 'Terminal prijava uspješna');
execute upsert_tx(43, 'hr', 'Terminal prijava nije uspjela');
execute upsert_tx(44, 'hr', 'Terminal odjava');
execute upsert_tx(45, 'hr', 'Terminal odjava, Sesije istekla');
execute upsert_tx(60, 'hr', 'PDA prijava aktivirana');
execute upsert_tx(61, 'hr', 'PDA prijava deaktivirana');
execute upsert_tx(62, 'hr', 'PDA prijava uspješna');
execute upsert_tx(63, 'hr', 'PDA prijava nije uspjela');
execute upsert_tx(64, 'hr', 'PDA odjava');
execute upsert_tx(65, 'hr', 'PDA odjava, Sesije istekla');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - SR
execute upsert_tx(0, 'sr', 'Korisnik kreiran');
execute upsert_tx(1, 'sr', 'Korisnik ponovno aktiviran');
execute upsert_tx(2, 'sr', 'Korisnik neaktivan');
execute upsert_tx(3, 'sr', 'Prijava korisnika uspešna');
execute upsert_tx(4, 'sr', 'Prijava korisnika nije uspjela');
execute upsert_tx(5, 'sr', 'Korisnik odjavljen');
execute upsert_tx(6, 'sr', 'Korisnik odjavljen, vreme prijave je isteklo');
execute upsert_tx(20, 'sr', 'Eksterna prijava aktivirana');
execute upsert_tx(21, 'sr', 'Eksterna prijava deaktivirana');
execute upsert_tx(22, 'sr', 'Eksterna prijava uspešna');
execute upsert_tx(23, 'sr', 'Eksterna prijava nije uspela');
execute upsert_tx(24, 'sr', 'Eksterna odjava');
execute upsert_tx(25, 'sr', 'Eksterna odjava, vreme je isteklo');
execute upsert_tx(26, 'sr', 'Eksterna lozinka otključana');
execute upsert_tx(27, 'sr', 'Eksterna lozinka zaključana, istekla');
execute upsert_tx(28, 'sr', 'Eksterna lozinka zaključana maks. pokušaj prijava');
execute upsert_tx(29, 'sr', 'Eksterna lozinka zaključana, pokušaj nije uspeo');
execute upsert_tx(30, 'sr', 'Eksterna lozinka je promenjena');
execute upsert_tx(40, 'sr', 'Aktivirana prijava za terminal');
execute upsert_tx(41, 'sr', 'Deaktivirana prijava za terminal');
execute upsert_tx(42, 'sr', 'Prijava za terminal uspešna');
execute upsert_tx(43, 'sr', 'Terminal prijava nije uspela');
execute upsert_tx(44, 'sr', 'Terminal odjava');
execute upsert_tx(45, 'sr', 'Terminal odjava,vreme prijave isteklo');
execute upsert_tx(60, 'sr', 'PDA prijava aktivirana');
execute upsert_tx(61, 'sr', 'PDA prijava deaktivirana');
execute upsert_tx(62, 'sr', 'PDA prijava uspešna');
execute upsert_tx(63, 'sr', 'PDA prijava nije uspela');
execute upsert_tx(64, 'sr', 'PDA odjava');
execute upsert_tx(65, 'sr', 'PDA odjava, vreme prijave isteklo');

prompt INSERTING into TLGNR_USER_ACTIVITY_ACTION_TX - TR
execute upsert_tx(0, 'tr', 'User created');
execute upsert_tx(1, 'tr', 'User reactivated');
execute upsert_tx(2, 'tr', 'User inactivated');
execute upsert_tx(3, 'tr', 'User login successful');
execute upsert_tx(4, 'tr', 'User login failed');
execute upsert_tx(5, 'tr', 'User logged out');
execute upsert_tx(6, 'tr', 'User logged out, session expired');
execute upsert_tx(20, 'tr', 'External login activated');
execute upsert_tx(21, 'tr', 'External login deactivated');
execute upsert_tx(22, 'tr', 'External login successful');
execute upsert_tx(23, 'tr', 'External login failed');
execute upsert_tx(24, 'tr', 'External log out');
execute upsert_tx(25, 'tr', 'External log out, session expired');
execute upsert_tx(26, 'tr', 'External password unlocked');
execute upsert_tx(27, 'tr', 'External password locked expired');
execute upsert_tx(28, 'tr', 'External pwd locked max. login attempt');
execute upsert_tx(29, 'tr', 'External password unlock attempt failed');
execute upsert_tx(30, 'tr', 'External password changed');
execute upsert_tx(40, 'tr', 'Terminal login activated');
execute upsert_tx(41, 'tr', 'Terminal login deactivated');
execute upsert_tx(42, 'tr', 'Terminal login successful');
execute upsert_tx(43, 'tr', 'Terminal login failed');
execute upsert_tx(44, 'tr', 'Terminal log out');
execute upsert_tx(45, 'tr', 'Terminal log out, session expired');
execute upsert_tx(60, 'tr', 'PDA login activated');
execute upsert_tx(61, 'tr', 'PDA login deactivated');
execute upsert_tx(62, 'tr', 'PDA login successful');
execute upsert_tx(63, 'tr', 'PDA login failed');
execute upsert_tx(64, 'tr', 'PDA log out');
execute upsert_tx(65, 'tr', 'PDA log out, session expired');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

