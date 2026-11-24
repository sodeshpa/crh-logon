-- $Revision$
-- $Date$
--
-- LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_mop_status.sql ----------'

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
-- tlgnr_mop_status_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_mop_status_cd     in number,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_mop_status_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_mop_status_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.mop_status_cd)
    into v_exists_flag
    from tlgnr_mop_status_bs bs
    where bs.mop_status_cd = p_mop_status_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_mop_status_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_mop_status_bs set
/* mop_status_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          mop_status_cd = p_mop_status_cd
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_mop_status_cd || ').');
      end if;
    else
      insert into tlgnr_mop_status_bs (
        mop_status_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_mop_status_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_mop_status_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_mop_status_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_mop_status_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_mop_status_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_mop_status_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.mop_status_cd) into v_exists_bs_flag
    from tlgnr_mop_status_bs bs
    where bs.mop_status_cd = p_mop_status_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.mop_status_cd) into v_exists_flag
      from tlgnr_mop_status_tx tx
      where tx.mop_status_cd = p_mop_status_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_mop_status_tx set
/* p_mop_status_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          mop_status_cd = p_mop_status_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_mop_status_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_mop_status_tx (
          mop_status_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_mop_status_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_mop_status_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_mop_status_cd || ').');
    end if;

  end upsert_tx;
/
show errors;


-- Response codes to incoming Parallel events
prompt INSERTING into TLGNR_MOP_STATUS_BS
execute upsert_bs(10000,1);
execute upsert_bs(10101,1);
execute upsert_bs(10102,1);
execute upsert_bs(10201,1);
execute upsert_bs(10202,1);
execute upsert_bs(10401,1);
execute upsert_bs(10402,1);
execute upsert_bs(10601,1);
execute upsert_bs(10602,1);
execute upsert_bs(10603,1);
execute upsert_bs(10604,1);
execute upsert_bs(10610,1);
execute upsert_bs(10611,1);
execute upsert_bs(10612,1);
execute upsert_bs(10613,1);
execute upsert_bs(10614,1);
execute upsert_bs(10615,1);
execute upsert_bs(10616,1);
execute upsert_bs(10617,1);
execute upsert_bs(10618,1);
execute upsert_bs(10619,1);
execute upsert_bs(10620,1);
execute upsert_bs(10621,1);
execute upsert_bs(10701,1);
execute upsert_bs(10702,1);

-- Response codes to incoming PDA events
execute upsert_bs(30000,1);
execute upsert_bs(30001,1);
execute upsert_bs(30002,1);
execute upsert_bs(30101,1);
execute upsert_bs(30201,1);
execute upsert_bs(30202,1);
execute upsert_bs(30203,1);
execute upsert_bs(30401,1);
execute upsert_bs(30501,1);
execute upsert_bs(30502,1);
execute upsert_bs(30503,1);
execute upsert_bs(30504,1);
execute upsert_bs(30505,1);
execute upsert_bs(30506,1);
execute upsert_bs(30507,1);
execute upsert_bs(30601,1);
execute upsert_bs(30602,1);
execute upsert_bs(30603,1);
execute upsert_bs(30604,1);
-- Response codes to outgoing PDA events (referenced by tlgnc_mopevent_header_out)
execute upsert_bs(40000,1);
execute upsert_bs(40101,1);
execute upsert_bs(40102,1);
execute upsert_bs(40201,1);
execute upsert_bs(40202,1);
execute upsert_bs(40401,1);

prompt INSERTING into TLGNR_MOP_STATUS_TX - EN
execute upsert_tx(10000, 'en', 'OK');
execute upsert_tx(10101, 'en', 'Technical Error');
execute upsert_tx(10102, 'en', 'Retry');
execute upsert_tx(10201, 'en', 'Mandatory Field Missing: {0}');
execute upsert_tx(10202, 'en', 'Mandatory Field Error: {0}={1}');
execute upsert_tx(10401, 'en', 'Application Error: {0}');
execute upsert_tx(10402, 'en', 'Scheduling error: {0}');
execute upsert_tx(10601, 'en', 'Loading not possible: {0}');
execute upsert_tx(10602, 'en', 'No load on wagon: {0}');
execute upsert_tx(10603, 'en', 'Loading aborted: {0}');
execute upsert_tx(10604, 'en', 'Incompatible wagon: {0}');
execute upsert_tx(10610, 'en', 'Invalid ShipTo type');
execute upsert_tx(10611, 'en', 'ShipTo Sarea not found');
execute upsert_tx(10612, 'en', 'Multiple SareaRestr found');
execute upsert_tx(10613, 'en', 'ShipTo Sarea unknown');
execute upsert_tx(10614, 'en', 'Production plant unknown');
execute upsert_tx(10615, 'en', 'Material condition not found');
execute upsert_tx(10616, 'en', 'Pool not found');
execute upsert_tx(10617, 'en', 'Invalid delivery type');
execute upsert_tx(10618, 'en', 'SAP BP Sarea not found');
execute upsert_tx(10619, 'en', 'Route not found');
execute upsert_tx(10620, 'en', 'Shipping point not found');
execute upsert_tx(10621, 'en', 'Tare warn check failed');
execute upsert_tx(10701, 'en', 'Truck assignment not possible');
execute upsert_tx(10702, 'en', 'Invalid order validation code');

execute upsert_tx(30000, 'en', 'OK');
execute upsert_tx(30001, 'en', 'Login OK');
execute upsert_tx(30002, 'en', 'Session exists');
execute upsert_tx(30101, 'en', 'Technical Error');
execute upsert_tx(30201, 'en', 'Mandatory Field Missing: {0}');
execute upsert_tx(30202, 'en', 'Mandatory Field Error: {0}={1}');
execute upsert_tx(30203, 'en', 'Field Unknown: {0}={1}');
execute upsert_tx(30401, 'en', 'Application Error: {0}');
execute upsert_tx(30501, 'en', 'Wrong PIN');
execute upsert_tx(30502, 'en', 'Outdated PIN');
execute upsert_tx(30503, 'en', 'Unknown User');
execute upsert_tx(30504, 'en', 'Unknown Truck');
execute upsert_tx(30505, 'en', 'Unknown PDA');
execute upsert_tx(30506, 'en', 'Carriermismatch');
execute upsert_tx(30507, 'en', 'Invalid driver certificate.');
execute upsert_tx(30601, 'en', 'IncompatibleTruckComposition');
execute upsert_tx(30602, 'en', 'Material mismatch with container: {0}');
execute upsert_tx(30603, 'en', 'Unknown material in container: {0}');
execute upsert_tx(30604, 'en', 'Invalid measurement');

execute upsert_tx(40000, 'en', 'OK');
execute upsert_tx(40101, 'en', 'Technical Error');
execute upsert_tx(40102, 'en', 'Retry');
execute upsert_tx(40201, 'en', 'Mandatory Field Missing: <1>');
execute upsert_tx(40202, 'en', 'Mandatory Field Error: <1>');
execute upsert_tx(40401, 'en', 'Application Error: <1>');

prompt INSERTING into TLGNR_MOP_STATUS_TX - DE
execute upsert_tx(10000, 'de', 'OK');
execute upsert_tx(10101, 'de', 'Technischer Fehler');
execute upsert_tx(10102, 'de', 'Wiederholung');
execute upsert_tx(10201, 'de', 'Pflichtfeldeintrag fehlt: {0}');
execute upsert_tx(10202, 'de', 'Pflichtfeldeintrag fehlerhaft: {0}={1}');
execute upsert_tx(10401, 'de', 'Fehler in der Anwendung: {0}');
execute upsert_tx(10601, 'de', 'Beladung nicht möglich: {0}');
execute upsert_tx(10602, 'de', 'Keine Ladung auf Bahnwagen: {0}');
execute upsert_tx(10603, 'de', 'Beladung abgebrochen: {0}');
execute upsert_tx(10604, 'de', 'Bahnwagentyp nicht kompatibel: {0}');
execute upsert_tx(10610, 'de', 'Ungültiger ShipTo Typ');
execute upsert_tx(10611, 'de', 'ShipTo Sales Area nicht gefunden');
execute upsert_tx(10612, 'de', 'Mehrere Sales Area Restiktionen gefunden');
execute upsert_tx(10613, 'de', 'ShipTo Sales Area unbekannt');
execute upsert_tx(10614, 'de', 'Produktionswerk unbekannt');
execute upsert_tx(10615, 'de', 'Materialkondition nicht gefunden');
execute upsert_tx(10616, 'de', 'Pool nicht gefunden');
execute upsert_tx(10617, 'de', 'Ungültiger Liefertyp');
execute upsert_tx(10618, 'de', 'SAP BP Sales Area nicht gefunden');
execute upsert_tx(10619, 'de', 'Strecke nicht gefunden');
execute upsert_tx(10620, 'de', 'Shipping Point nicht gefunden');
execute upsert_tx(10621, 'de', 'Tarakontrolle fehlgeschlagen');
execute upsert_tx(10701, 'de', 'Fahrzeugzuordnung nicht möglich');
execute upsert_tx(10702, 'de', 'Ungültiger Auftragscode');

execute upsert_tx(30000, 'de', 'OK');
execute upsert_tx(30001, 'de', 'Login OK');
execute upsert_tx(30002, 'de', 'Session existiert bereits');
execute upsert_tx(30101, 'de', 'Technischer Fehler');
execute upsert_tx(30201, 'de', 'Pflichtfeldeintrag fehlt: {0}');
execute upsert_tx(30202, 'de', 'Pflichtfeldeintrag fehlerhaft: {0}={1}');
execute upsert_tx(30203, 'de', 'Feld unbekannt: {0}={1}');
execute upsert_tx(30401, 'de', 'Fehler in der Anwendung: {0}');
execute upsert_tx(30501, 'de', 'Falscher PIN');
execute upsert_tx(30502, 'de', 'PIN abgelaufen');
execute upsert_tx(30503, 'de', 'Unbekannter Anwender');
execute upsert_tx(30504, 'de', 'Unbekanntes Fahrzeug');
execute upsert_tx(30505, 'de', 'Unbekanntes PDA');
execute upsert_tx(30506, 'de', 'Transporteur passt nicht');
execute upsert_tx(30507, 'de', 'TODO Invalid driver certificate.');
execute upsert_tx(30601, 'de', 'Fahrzeugkomposition nicht möglich');
execute upsert_tx(30602, 'de', 'Material passt nicht zu Container: {0}');
execute upsert_tx(30603, 'de', 'Ubekanntes Material im Container: {0}');
execute upsert_tx(30604, 'de', 'Ungültige Messung');

execute upsert_tx(40000, 'de', 'OK');
execute upsert_tx(40101, 'de', 'Technischer Fehler');
execute upsert_tx(40102, 'de', 'Wiederholung');
execute upsert_tx(40201, 'de', 'Pflichtfeldeintrag fehlt: <1>');
execute upsert_tx(40202, 'de', 'Pflichtfeldeintrag fehlerhaft: <1>');
execute upsert_tx(40401, 'de', 'Fehler in der Anwendung: <1>');

prompt INSERTING into TLGNR_MOP_STATUS_TX - FR
execute upsert_tx(10000, 'fr', 'OK');
execute upsert_tx(10101, 'fr', 'Erreur technique');
execute upsert_tx(10102, 'fr', 'Réessayer');
execute upsert_tx(10201, 'fr', 'Manque un champ obligatoire: {0}');
execute upsert_tx(10202, 'fr', 'Erreur dans un champ obligatoire: {0}={1}');
execute upsert_tx(10401, 'fr', 'Erreur de l''application: {0}');
execute upsert_tx(10601, 'fr', 'Pas possible charger: {0}');
execute upsert_tx(10602, 'fr', 'Aucun chargement dans le wagon: {0}');
execute upsert_tx(10603, 'fr', 'Chargement avorté: {0}');
execute upsert_tx(10604, 'fr', 'Wagon incompatible: {0}');
execute upsert_tx(10610, 'fr', 'Type de destination non valide');
execute upsert_tx(10611, 'fr', 'Zones de Ventes de Destination manquante');
execute upsert_tx(10612, 'fr', 'Plusieurs restrictions de Zones de Ventes trouvées');
execute upsert_tx(10613, 'fr', 'Zone de Ventes de Destination non connue');
execute upsert_tx(10614, 'fr', 'Plante de Production non connue');
execute upsert_tx(10615, 'fr', 'Condition de matériel pas trouvée');
execute upsert_tx(10616, 'fr', 'Pool non trouvé');
execute upsert_tx(10617, 'fr', 'Type de livraison non valide');
execute upsert_tx(10618, 'fr', 'SAP BP Zone de ventes pas trouvé');
execute upsert_tx(10619, 'fr', 'Route pas trouvée');
execute upsert_tx(10620, 'fr', 'Lieu d''expédition pas trouvé');
execute upsert_tx(10621, 'fr', 'Avis de vérification de tare raté');
execute upsert_tx(10701, 'fr', 'Assignation de camion impossible');
execute upsert_tx(10702, 'fr', 'Code de validation de l''ordre non valide');

execute upsert_tx(30000, 'fr', 'OK');
execute upsert_tx(30001, 'fr', 'Connexion OK');
execute upsert_tx(30002, 'fr', 'Quitter session');
execute upsert_tx(30101, 'fr', 'Erreur technique');
execute upsert_tx(30201, 'fr', 'Manque un champ obligatoire: {0}');
execute upsert_tx(30202, 'fr', 'Erreur dans un champ obligatoire: {0}={1}');
execute upsert_tx(30203, 'fr', 'Champ non connu: {0}={1}');
execute upsert_tx(30401, 'fr', 'Erreur de l''application: {0}');
execute upsert_tx(30501, 'fr', 'Mot de passe invalide');
execute upsert_tx(30502, 'fr', 'Mot de passe expiré');
execute upsert_tx(30503, 'fr', 'Utilisateur inconnu');
execute upsert_tx(30504, 'fr', 'Camion inconnu');
execute upsert_tx(30505, 'fr', 'Mobile non reconnu');
execute upsert_tx(30506, 'fr', 'Le mob., chauff., cam. n''appartiennent pas au même ensemble');
execute upsert_tx(30507, 'fr', 'Certificat expiré');
execute upsert_tx(30601, 'fr', 'Commande non compatible avec le camion');
execute upsert_tx(30602, 'fr', 'Le matériel ne correspond pas à celui du container: {0}');
execute upsert_tx(30603, 'fr', 'Le matériel dans le container est inconnu: {0}');
execute upsert_tx(30604, 'fr', 'Mesures invalides');

execute upsert_tx(40000, 'fr', 'OK');
execute upsert_tx(40101, 'fr', 'Erreur technique');
execute upsert_tx(40102, 'fr', 'Réessayer');
execute upsert_tx(40201, 'fr', 'Manque un champ obligatoire: <1>');
execute upsert_tx(40202, 'fr', 'Erreur dans un champ obligatoire: <1>');
execute upsert_tx(40401, 'fr', 'Erreur de l''application: <1>');
prompt INSERTING into TLGNR_MOP_STATUS_TX - IT
execute upsert_tx(10000, 'it', 'OK');
execute upsert_tx(10101, 'it', 'Errore Tecnico');
execute upsert_tx(10102, 'it', 'Riprova');
execute upsert_tx(10201, 'it', 'Campo Obbligatorio Mancante: {0}');
execute upsert_tx(10202, 'it', 'Errore in Campo Obbligatorio: {0}={1}');
execute upsert_tx(10401, 'it', 'Errore di Applicazione: {0}');
execute upsert_tx(10601, 'it', 'Caricamento non possibili: {0}');
execute upsert_tx(10602, 'it', 'Nessun carico sul vagone: {0}');
execute upsert_tx(10603, 'it', 'Carico interrotto: {0}');
execute upsert_tx(10604, 'it', 'Vagone incompatibile: {0}');
execute upsert_tx(10610, 'it', 'Tipo Destinatario non valido');
execute upsert_tx(10611, 'it', 'Destinazione dell''area vendita non trovata');
execute upsert_tx(10612, 'it', 'Trovate restrizioni in multiple aree di vendita');
execute upsert_tx(10613, 'it', 'Area di Vendita Destinatario sconosciuta');
execute upsert_tx(10614, 'it', 'Impianto di Produzione sconosciuta');
execute upsert_tx(10615, 'it', 'Condizione del materiale non trovata');
execute upsert_tx(10616, 'it', 'Pool non trovato');
execute upsert_tx(10617, 'it', 'Tipo di consegna non valido');
execute upsert_tx(10618, 'it', 'SAP BP Area Vendita non trovata');
execute upsert_tx(10619, 'it', 'Itinerario non trovato');
execute upsert_tx(10620, 'it', 'Punto di Spedizione non trovato');
execute upsert_tx(10621, 'it', 'Avviso di controllo della tara fallito');
execute upsert_tx(10701, 'it', 'Assegnazione di camion non è possibile');
execute upsert_tx(10702, 'it', 'Codice di convalida di ordine non valido');

execute upsert_tx(30000, 'it', 'OK');
execute upsert_tx(30001, 'it', 'Login OK');
execute upsert_tx(30002, 'it', 'La sessione già esiste');
execute upsert_tx(30101, 'it', 'Errore Tecnico');
execute upsert_tx(30201, 'it', 'Campo Obbligatorio Mancante: {0}');
execute upsert_tx(30202, 'it', 'Errore in Campo Obbligatorio: {0}={1}');
execute upsert_tx(30203, 'it', 'Campo sconosciuto: {0}={1}');
execute upsert_tx(30401, 'it', 'Errore di Applicazione: {0}');
execute upsert_tx(30501, 'it', 'PIN errato');
execute upsert_tx(30502, 'it', 'PIN obsoleto');
execute upsert_tx(30503, 'it', 'Utente sconosciuto');
execute upsert_tx(30504, 'it', 'Camion sconosciuto');
execute upsert_tx(30505, 'it', 'PDA sconosciuto');
execute upsert_tx(30506, 'it', 'Trasportatore non corrispondente');
execute upsert_tx(30507, 'it', 'TODO Invalid driver certificate.');
execute upsert_tx(30601, 'it', 'Composizione di camion incompatibili');
execute upsert_tx(30602, 'it', 'Materiale non corrispondente con container: {0}');
execute upsert_tx(30603, 'it', 'Materiale sconosciuto in container: {0}');
execute upsert_tx(30604, 'it', 'Misura non valida');

execute upsert_tx(40000, 'it', 'OK');
execute upsert_tx(40101, 'it', 'Errore Tecnico');
execute upsert_tx(40102, 'it', 'Riprova');
execute upsert_tx(40201, 'it', 'Campo Obbligatorio Mancante: <1>');
execute upsert_tx(40202, 'it', 'Errore in Campo Obbligatorio: <1>');
execute upsert_tx(40401, 'it', 'Errore di Applicazione: <1>');

prompt INSERTING into TLGNR_MOP_STATUS_TX - ES
execute upsert_tx(10000, 'es', 'OK');
execute upsert_tx(10101, 'es', 'Error Técnico');
execute upsert_tx(10102, 'es', 'Reintento');
execute upsert_tx(10201, 'es', 'Falta campo obligatorio: {0}');
execute upsert_tx(10202, 'es', 'Error en campo obligatorio: {0}={1}');
execute upsert_tx(10401, 'es', 'Error de aplicación: {0}');
execute upsert_tx(10601, 'es', 'Carga no posible: {0}');
execute upsert_tx(10602, 'es', 'No hay cargas en el vagón: {0}');
execute upsert_tx(10603, 'es', 'Carga anulada: {0}');
execute upsert_tx(10604, 'es', 'Vagón incmpatible: {0}');
execute upsert_tx(10610, 'es', 'Tipo de destinatario incorrecto');
execute upsert_tx(10611, 'es', 'No existe área de ventas para destinatario');
execute upsert_tx(10612, 'es', 'Encontradas multiples restricciones de área de ventas');
execute upsert_tx(10613, 'es', 'Area de ventas de destinatario desconocida');
execute upsert_tx(10614, 'es', 'Planta de Producción desconocida');
execute upsert_tx(10615, 'es', 'Condición de materiales no encontrada');
execute upsert_tx(10616, 'es', 'Región no encontrada');
execute upsert_tx(10617, 'es', 'Tipo de entrega erróneo');
execute upsert_tx(10618, 'es', 'Area de ventas para SAP BP no encontrada');
execute upsert_tx(10619, 'es', 'Ruta no encontrada');
execute upsert_tx(10620, 'es', 'Punto de transporte no encontrado');
execute upsert_tx(10621, 'es', 'Chequeo de tara fallido');
execute upsert_tx(10701, 'es', 'Asignación camión no posible');
execute upsert_tx(10702, 'es', 'Código de validación de pedido erróneo');

execute upsert_tx(30000, 'es', 'OK');
execute upsert_tx(30001, 'es', 'Inicio de sesión OK');
execute upsert_tx(30002, 'es', 'La sesión ya existe');
execute upsert_tx(30101, 'es', 'Error Técnico');
execute upsert_tx(30201, 'es', 'Falta campo obligatorio: {0}');
execute upsert_tx(30202, 'es', 'Error en campo obligatorio: {0}={1}');
execute upsert_tx(30203, 'es', 'Campo desconocido: {0}={1}');
execute upsert_tx(30401, 'es', 'Error Aplicación: {0}');
execute upsert_tx(30501, 'es', 'PIN erróneo');
execute upsert_tx(30502, 'es', 'PIN obsoleto');
execute upsert_tx(30503, 'es', 'Usuario desconocido');
execute upsert_tx(30504, 'es', 'Camión desconocido');
execute upsert_tx(30505, 'es', 'PDA desconocida');
execute upsert_tx(30506, 'es', 'Transportista no emparejado');
execute upsert_tx(30507, 'es', 'TODO Invalid driver certificate.');
execute upsert_tx(30601, 'es', 'Composición camión incompatible');
execute upsert_tx(30602, 'es', 'El material no empareja con el contenedor: {0}');
execute upsert_tx(30603, 'es', 'Contenedor con material desconocido: {0}');
execute upsert_tx(30604, 'es', 'Medición errónea');

execute upsert_tx(40000, 'es', 'OK');
execute upsert_tx(40101, 'es', 'Error Técnico');
execute upsert_tx(40102, 'es', 'Reintento');
execute upsert_tx(40201, 'es', 'Falta campo obligatorio: <1>');
execute upsert_tx(40202, 'es', 'Error en campo obligatorio: <1>');
execute upsert_tx(40401, 'es', 'Error de aplicación: <1>');

prompt INSERTING into TLGNR_MOP_STATUS_TX - CS
execute upsert_tx(10000, 'cs', 'OK');
execute upsert_tx(10101, 'cs', 'Technická chyba');
execute upsert_tx(10102, 'cs', 'Opakovat');
execute upsert_tx(10201, 'cs', 'Chybí povinné pole: {0}');
execute upsert_tx(10202, 'cs', 'Povinné pole - chyba: {0}={1}');
execute upsert_tx(10401, 'cs', 'Chyba aplikace: {0}');
execute upsert_tx(10402, 'cs', 'Chyba plánování: {0}');
execute upsert_tx(10601, 'cs', 'Nakládka není možná: {0}');
execute upsert_tx(10602, 'cs', 'Není náklad ve vagónu: {0}');
execute upsert_tx(10603, 'cs', 'Nakládka přerušena: {0}');
execute upsert_tx(10604, 'cs', 'Nevhodný vagón: {0}');
execute upsert_tx(10610, 'cs', 'Chybný typ Příjemce materiálu');
execute upsert_tx(10611, 'cs', 'Nenalezena Prodejní oblast pro Příjemce materiálu');
execute upsert_tx(10612, 'cs', 'Nalezeno více Prodejních oblastí pro Příjemce materiálu');
execute upsert_tx(10613, 'cs', 'Neznámá Prodejní oblast');
execute upsert_tx(10614, 'cs', 'Neznámy výrobní závod');
execute upsert_tx(10615, 'cs', 'Podmínky materiiálu nenalezeny');
execute upsert_tx(10616, 'cs', 'Skupina nenalezena');
execute upsert_tx(10617, 'cs', 'Chybný typ dodávky');
execute upsert_tx(10618, 'cs', 'SAB Obchodní partner -  Prodejní oblast nenalezena');
execute upsert_tx(10619, 'cs', 'Cesta nenalezena');
execute upsert_tx(10620, 'cs', 'Místo nakládky nenalezeno');
execute upsert_tx(10621, 'cs', 'Prověření varování TARA selhalo');
execute upsert_tx(10701, 'cs', 'Přiřazení vozidl anení možné');
execute upsert_tx(10702, 'cs', 'Chybný potvrzovací kód objednávky');

execute upsert_tx(30000, 'cs', 'OK');
execute upsert_tx(30001, 'cs', 'Příhlášení je v pořádku');
execute upsert_tx(30002, 'cs', 'Relace již existuje');
execute upsert_tx(30101, 'cs', 'Technická chyba');
execute upsert_tx(30201, 'cs', 'Chybí povinné pole: {0}');
execute upsert_tx(30202, 'cs', 'Povinné pole - chyba: {0}={1}');
execute upsert_tx(30203, 'cs', 'Neznámé pole: {0}');
execute upsert_tx(30401, 'cs', 'Chyba aplikace: {0}');
execute upsert_tx(30501, 'cs', 'Chybný PIN');
execute upsert_tx(30502, 'cs', 'Neplatný PIN');
execute upsert_tx(30503, 'cs', 'Neznámý uživatel');
execute upsert_tx(30504, 'cs', 'Neznávé vozidlo');
execute upsert_tx(30505, 'cs', 'Neznámé PDA');
execute upsert_tx(30506, 'cs', 'Nevhodný dopravce');
execute upsert_tx(30507, 'cs', 'TODO Invalid driver certificate.');
execute upsert_tx(30601, 'cs', 'Nevhodná kombinace soupravy');
execute upsert_tx(30602, 'cs', 'Nevhodný materiál pro kontejner: {0}');
execute upsert_tx(30603, 'cs', 'Neznámý materiál v kontejneru: {0}');
execute upsert_tx(30604, 'cs', 'Chybné měření');

execute upsert_tx(40000, 'cs', 'OK');
execute upsert_tx(40101, 'cs', 'Technická chyba');
execute upsert_tx(40102, 'cs', 'Opakovat');
execute upsert_tx(40201, 'cs', 'Chybí povinné pole: <1>');
execute upsert_tx(40202, 'cs', 'Povinné pole - chyba: <1>');
execute upsert_tx(40401, 'cs', 'Chyba aplikace: <1>');

prompt INSERTING into TLGNR_MOP_STATUS_TX - SK
execute upsert_tx(10000, 'sk', 'OK');
execute upsert_tx(10101, 'sk', 'Technická chyba');
execute upsert_tx(10102, 'sk', 'Skúsiť znova');
execute upsert_tx(10201, 'sk', 'Povinné pole chýba: {0}');
execute upsert_tx(10202, 'sk', 'Chyba v povinnom poli: {0}={1}');
execute upsert_tx(10401, 'sk', 'Chyba aplikácie: {0}');
execute upsert_tx(10402, 'sk', 'Chyba plánovania: {0}');
execute upsert_tx(10601, 'sk', 'Nakládka nemožná: {0}');
execute upsert_tx(10602, 'sk', 'Žiaden náklad na vagóne: {0}');
execute upsert_tx(10603, 'sk', 'Nakládka prerušená: {0}');
execute upsert_tx(10604, 'sk', 'Nekompatibilný vagón: {0}');
execute upsert_tx(10610, 'sk', 'Neplatný typ príjemcu');
execute upsert_tx(10611, 'sk', 'Predajná oblasť príjemcu sa nenašla');
execute upsert_tx(10612, 'sk', 'Našlo sa viac obmedzení pre predajnú oblasť');
execute upsert_tx(10613, 'sk', 'Oblasť predaja príjemcu neznáma');
execute upsert_tx(10614, 'sk', 'Výrobný závod neznámy');
execute upsert_tx(10615, 'sk', 'Stav materiálu sa nenašiel');
execute upsert_tx(10616, 'sk', 'Región sa nenašiel');
execute upsert_tx(10617, 'sk', 'Neplatný typ dodávky');
execute upsert_tx(10618, 'sk', 'SAP miesto predaja obchodného partnera sa nenašlo');
execute upsert_tx(10619, 'sk', 'Trasa sa nenašla');
execute upsert_tx(10620, 'sk', 'Miesto nakládky sa nenašlo');
execute upsert_tx(10621, 'sk', 'Kontrola tara váhy neúspešná');
execute upsert_tx(10701, 'sk', 'Pridelenie ťahača nemožné');
execute upsert_tx(10702, 'sk', 'Neplatný kód potvrdenia objednávky');

execute upsert_tx(30000, 'sk', 'OK');
execute upsert_tx(30001, 'sk', 'Prihlásenie OK');
execute upsert_tx(30002, 'sk', 'Prihlásenie už existuje');
execute upsert_tx(30101, 'sk', 'Tachnická chyba');
execute upsert_tx(30201, 'sk', 'Povinné pole chýba: {0}');
execute upsert_tx(30202, 'sk', 'Chyba v povinnom poli: {0}={1}');
execute upsert_tx(30203, 'sk', 'Pole neznáme: {0}={1}');
execute upsert_tx(30401, 'sk', 'Chyba aplikácie: {0}');
execute upsert_tx(30501, 'sk', 'Nesprávny PIN');
execute upsert_tx(30502, 'sk', 'Starý PIN');
execute upsert_tx(30503, 'sk', 'Neznámy užívateľ');
execute upsert_tx(30504, 'sk', 'Neznámy ťahač');
execute upsert_tx(30505, 'sk', 'Neznáme PDA');
execute upsert_tx(30506, 'sk', 'Nezhodný dopravca');
execute upsert_tx(30507, 'sk', 'TODO Invalid driver certificate.');
execute upsert_tx(30601, 'sk', 'Nekompatibilné zloženie ťahača');
execute upsert_tx(30602, 'sk', 'Nezhodný materiál s kontajnerom: {0}');
execute upsert_tx(30603, 'sk', 'Neznámy materiál v kontajneri: {0}');
execute upsert_tx(30604, 'sk', 'Neplatná miera');

execute upsert_tx(40000, 'sk', 'OK');
execute upsert_tx(40101, 'sk', 'Technická chyba');
execute upsert_tx(40102, 'sk', 'Znova');
execute upsert_tx(40201, 'sk', 'Povinné pole chýba: <1>');
execute upsert_tx(40202, 'sk', 'Chyba v povinnom poli: <1>');
execute upsert_tx(40401, 'sk', 'Chyba aplikácie: <1>');

prompt INSERTING into TLGNR_MOP_STATUS_TX - HU
execute upsert_tx(10000, 'hu', 'OK');
execute upsert_tx(10101, 'hu', 'Technika probléma');
execute upsert_tx(10102, 'hu', 'Újra');
execute upsert_tx(10201, 'hu', 'Kötelező mező hiányzik: {0}');
execute upsert_tx(10202, 'hu', 'Kötelező mező hiba: {0}={1}');
execute upsert_tx(10401, 'hu', 'Alkalmazási hiba: {0}');
execute upsert_tx(10402, 'hu', 'Ütemezés hiba: {0}');
execute upsert_tx(10601, 'hu', 'Töltés nem lehetséges: {0}');
execute upsert_tx(10602, 'hu', 'Nem tölthető vagonról: {0}');
execute upsert_tx(10603, 'hu', 'Megszakított töltés: {0}');
execute upsert_tx(10604, 'hu', 'Inkompatibilis vagon: {0}');
execute upsert_tx(10610, 'hu', 'Érvénytelen felrakó típus');
execute upsert_tx(10611, 'hu', 'Felrakó értékesítési körzet nem található');
execute upsert_tx(10612, 'hu', 'Több értekesítési körzet korlázozottan található');
execute upsert_tx(10613, 'hu', 'Felrakó értékesítési körzet ismeretlen');
execute upsert_tx(10614, 'hu', 'Ismeretlen gyár');
execute upsert_tx(10615, 'hu', 'Anyag kondíció nem található');
execute upsert_tx(10616, 'hu', 'Készlet nem található');
execute upsert_tx(10617, 'hu', 'Érvénytelen szállítási típus');
execute upsert_tx(10618, 'hu', 'SAP üzleti értékesítési körzet nem található');
execute upsert_tx(10619, 'hu', 'Útvonal nem található');
execute upsert_tx(10620, 'hu', 'Felrakó hely nem található');
execute upsert_tx(10621, 'hu', 'Tára ellenőrzési hiba');
execute upsert_tx(10701, 'hu', 'Kamion hozzáadása nem lehetséges');
execute upsert_tx(10702, 'hu', 'Érvénytelen rendelés jóváhagyási kódja');

execute upsert_tx(30000, 'hu', 'OK');
execute upsert_tx(30001, 'hu', 'Sikeres bejelentkezés');
execute upsert_tx(30002, 'hu', 'Létező munkaszakasz');
execute upsert_tx(30101, 'hu', 'Technika probléma');
execute upsert_tx(30201, 'hu', 'Kötelező mező hiányzik: {0}');
execute upsert_tx(30202, 'hu', 'Kötelező mező hiba: {0}={1}');
execute upsert_tx(30203, 'hu', 'Ismeretlen mező: {0}={1}');
execute upsert_tx(30401, 'hu', 'Alkalmazási hiba: {0}');
execute upsert_tx(30501, 'hu', 'Helytelen PIN kód');
execute upsert_tx(30502, 'hu', 'Elavult PIN kód');
execute upsert_tx(30503, 'hu', 'Ismeretlen felhasználó');
execute upsert_tx(30504, 'hu', 'Ismeretlen kamion');
execute upsert_tx(30505, 'hu', 'Ismeretlen PDA');
execute upsert_tx(30506, 'hu', 'Fuvarozói rossz párosítás/nem megfelelő');
execute upsert_tx(30507, 'hu', 'TODO Invalid driver certificate.');
execute upsert_tx(30601, 'hu', 'Inkompatibilis kamion helyzet');
execute upsert_tx(30602, 'hu', 'Nem megfelelő anyag párosítás a konténerrel: {0}');
execute upsert_tx(30603, 'hu', 'Ismeretlen anyag a konténerrel: {0}');
execute upsert_tx(30604, 'hu', 'Mérési hiba');

execute upsert_tx(40000, 'hu', 'OK');
execute upsert_tx(40101, 'hu', 'Technika probléma');
execute upsert_tx(40102, 'hu', 'Újra');
execute upsert_tx(40201, 'hu', 'Kötelező mező hiányzik: <1>');
execute upsert_tx(40202, 'hu', 'Kötelező mező hiba: <1>');
execute upsert_tx(40401, 'hu', 'Alkalmazási hiba: <1>');

prompt INSERTING into TLGNR_MOP_STATUS_TX - RO
execute upsert_tx(10000, 'ro', 'OK');
execute upsert_tx(10101, 'ro', 'Eroare Tehnica');
execute upsert_tx(10102, 'ro', 'Incearca din nou');
execute upsert_tx(10201, 'ro', 'Lipseste camp obligatoriu: {0}');
execute upsert_tx(10202, 'ro', 'Eroare camp obligatoriu: {0}={1}');
execute upsert_tx(10401, 'ro', 'Eroare aplicatie: {0}');
execute upsert_tx(10402, 'ro', 'Eroare planificare: {0}');
execute upsert_tx(10601, 'ro', 'Incarcarea nu este posibila: {0}');
execute upsert_tx(10602, 'ro', 'Nu exista incarcatura in vagon: {0}');
execute upsert_tx(10603, 'ro', 'Incarcare oprita: {0}');
execute upsert_tx(10604, 'ro', 'Vagon incompatibil: {0}');
execute upsert_tx(10610, 'ro', 'Tip Destinatar invalid');
execute upsert_tx(10611, 'ro', 'Aria de vanzari  Destinatar nu a fost gasita');
execute upsert_tx(10612, 'ro', 'S-au gasit multiple restrictii Aria Vanzari');
execute upsert_tx(10613, 'ro', 'Aria Vanzari Destinatar necunoscuta');
execute upsert_tx(10614, 'ro', 'Fabrica de Productie necunoscuta');
execute upsert_tx(10615, 'ro', 'Conditie material nu a fost gasita');
execute upsert_tx(10616, 'ro', 'Regiunea nu a fost gasita');
execute upsert_tx(10617, 'ro', 'Tip livrare invalid');
execute upsert_tx(10618, 'ro', 'Aria Vanzari Partener nu a fost gasita in SAP');
execute upsert_tx(10619, 'ro', 'Itinerariu nu a fost gasit');
execute upsert_tx(10620, 'ro', 'Punct de Expeditie nu a fost gasit');
execute upsert_tx(10621, 'ro', 'Verificarea de avertizare tara a esuat');
execute upsert_tx(10701, 'ro', 'Atribuire camion nu este posibila');
execute upsert_tx(10702, 'ro', 'Cod validare comanda invalid');

execute upsert_tx(30000, 'ro', 'OK');
execute upsert_tx(30001, 'ro', 'Conectare OK');
execute upsert_tx(30002, 'ro', 'Sesiunea exista');
execute upsert_tx(30101, 'ro', 'Eroare Tehnica');
execute upsert_tx(30201, 'ro', 'Lipseste camp obligatoriu: {0}');
execute upsert_tx(30202, 'ro', 'Eroare camp obligatoriu: {0}={1}');
execute upsert_tx(30203, 'ro', 'Camp necunoscut: {0}={1}');
execute upsert_tx(30401, 'ro', 'Eroare Aplicatie: {0}');
execute upsert_tx(30501, 'ro', 'PIN gresit');
execute upsert_tx(30502, 'ro', 'PIN iesit din uz');
execute upsert_tx(30503, 'ro', 'Utilizator necunoscut');
execute upsert_tx(30504, 'ro', 'Camion necunoscut');
execute upsert_tx(30505, 'ro', 'PDA necunoscut');
execute upsert_tx(30506, 'ro', 'Transportator neadecvat');
execute upsert_tx(30507, 'ro', 'TODO Invalid driver certificate.');
execute upsert_tx(30601, 'ro', 'Componenta camion incompatibila');
execute upsert_tx(30602, 'ro', 'Material neadecvat containerului: {0}');
execute upsert_tx(30603, 'ro', 'Material necunoscut in container: {0}');
execute upsert_tx(30604, 'ro', 'Dimensiune invalida');

execute upsert_tx(40000, 'ro', 'OK');
execute upsert_tx(40101, 'ro', 'Eroare Tehnica');
execute upsert_tx(40102, 'ro', 'Incearca din nou');
execute upsert_tx(40201, 'ro', 'Lipseste camp obligatoriu: <1>');
execute upsert_tx(40202, 'ro', 'Eroare camp obligatoriu: <1>');
execute upsert_tx(40401, 'ro', 'Eroare Aplicatie: <1>');

prompt INSERTING into TLGNR_MOP_STATUS_TX - BG
execute upsert_tx(10000, 'bg', 'OK');
execute upsert_tx(10101, 'bg', 'Техническа грешка');
execute upsert_tx(10102, 'bg', 'Повторен опит');
execute upsert_tx(10201, 'bg', 'Задължителното поле липсва: {0}');
execute upsert_tx(10202, 'bg', 'Грешка в задължителното поле: {0}={1}');
execute upsert_tx(10401, 'bg', 'Грешка в приложението: {0}');
execute upsert_tx(10402, 'bg', 'Грешка в графика: {0}');
execute upsert_tx(10601, 'bg', 'Товаренето не е възможно: {0}');
execute upsert_tx(10602, 'bg', 'Без товарене на вагона: {0}');
execute upsert_tx(10603, 'bg', 'Товаренето претърпя неуспех: {0}');
execute upsert_tx(10604, 'bg', 'Несъвместим вагон: {0}');
execute upsert_tx(10610, 'bg', 'Невалиден обект');
execute upsert_tx(10611, 'bg', 'Не е намерен търговски регион на обекта');
execute upsert_tx(10612, 'bg', 'Намерени са ограничени търговски области');
execute upsert_tx(10613, 'bg', 'Непозната търговска област на обекта');
execute upsert_tx(10614, 'bg', 'Непознат производствен завод');
execute upsert_tx(10615, 'bg', 'Не е намерено състояние на материала');
execute upsert_tx(10616, 'bg', 'Не е намерен Pool');
execute upsert_tx(10617, 'bg', 'Невалиден вид доставка');
execute upsert_tx(10618, 'bg', 'SAP бизнес партньор търговска област не намерен');
execute upsert_tx(10619, 'bg', 'Не е намерен маршрут');
execute upsert_tx(10620, 'bg', 'Място на доставяне не е намерено');
execute upsert_tx(10621, 'bg', 'Проверката за предупреждение на тарата не е възможно');
execute upsert_tx(10701, 'bg', 'Не е възможно назначение на камион');
execute upsert_tx(10702, 'bg', 'Невалиден валидиран код на поръчка');

execute upsert_tx(30000, 'bg', 'OK');
execute upsert_tx(30001, 'bg', 'Вход ОК');
execute upsert_tx(30002, 'bg', 'Сесията съществува');
execute upsert_tx(30101, 'bg', 'Техническа грешка');
execute upsert_tx(30201, 'bg', 'Липсва задължителното поле: {0}');
execute upsert_tx(30202, 'bg', 'Грешка на задължителното поле: {0}={1}');
execute upsert_tx(30203, 'bg', 'Непознато поле: {0}={1}');
execute upsert_tx(30401, 'bg', 'Грешка в приложението: {0}');
execute upsert_tx(30501, 'bg', 'Грешен PIN');
execute upsert_tx(30502, 'bg', 'Остарял PIN');
execute upsert_tx(30503, 'bg', 'Непознат потребител');
execute upsert_tx(30504, 'bg', 'Непознат камион');
execute upsert_tx(30505, 'bg', 'Непознато PDA');
execute upsert_tx(30506, 'bg', 'Разминаване на превозвач');
execute upsert_tx(30507, 'bg', 'TODO Invalid driver certificate.');
execute upsert_tx(30601, 'bg', 'Несъвместима композиция на камион');
execute upsert_tx(30602, 'bg', 'Несъвместимост на материал и контейнер: {0}');
execute upsert_tx(30603, 'bg', 'Непознат материал в контейнер: {0}');
execute upsert_tx(30604, 'bg', 'Невалидно измерване');

execute upsert_tx(40000, 'bg', 'OK');
execute upsert_tx(40101, 'bg', 'Техническа грешка');
execute upsert_tx(40102, 'bg', 'Повторен опит');
execute upsert_tx(40201, 'bg', 'Задължителното поле липсва: <1>');
execute upsert_tx(40202, 'bg', 'Грешка при задължителното поле: <1>');
execute upsert_tx(40401, 'bg', 'Грешка при приложението: <1>');

prompt INSERTING into TLGNR_MOP_STATUS_TX - RU (ENGLISH COPY)
execute upsert_tx(10000, 'ru', 'OK');
execute upsert_tx(10101, 'ru', 'техническая ошибка');
execute upsert_tx(10102, 'ru', 'повторить');
execute upsert_tx(10201, 'ru', 'обязательное поле не заполнено: {0}');
execute upsert_tx(10202, 'ru', 'ошибка при заполнении обязательного поля: {0}={1}');
execute upsert_tx(10401, 'ru', 'ошибка приложения: {0}');
execute upsert_tx(10402, 'ru', 'ошибка планирования: {0}');
execute upsert_tx(10601, 'ru', 'загрузка не возможна: {0}');
execute upsert_tx(10602, 'ru', 'Нет нагрузки на вагон: {0}');
execute upsert_tx(10603, 'ru', 'Погрузка прервана: {0}');
execute upsert_tx(10604, 'ru', 'несовместимый тип вагона: {0}');
execute upsert_tx(10610, 'ru', 'неверный тип грузополучателя');
execute upsert_tx(10611, 'ru', 'ShipTo Sarea не найдена');
execute upsert_tx(10612, 'ru', 'Multiple SareaRestr найдена');
execute upsert_tx(10613, 'ru', 'ShipTo Sarea неизвестна');
execute upsert_tx(10614, 'ru', 'неизвестный поставщик');
execute upsert_tx(10615, 'ru', 'Состояние материала не найдено');
execute upsert_tx(10616, 'ru', 'Список заказов не найден');
execute upsert_tx(10617, 'ru', 'неверный тип поставки');
execute upsert_tx(10618, 'ru', 'SAP BP Sarea не найдена');
execute upsert_tx(10619, 'ru', 'маршрут не найден');
execute upsert_tx(10620, 'ru', 'Зона погрузки не найдена');
execute upsert_tx(10621, 'ru', 'вес пустой машины проверку не прошел');
execute upsert_tx(10701, 'ru', 'присвоение автомобиля не возможно');
execute upsert_tx(10702, 'ru', 'Неверный код проверки заказа');

execute upsert_tx(30000, 'ru', 'OK');
execute upsert_tx(30001, 'ru', 'Вход осуществлен успешно');
execute upsert_tx(30002, 'ru', 'Сессия открыта');
execute upsert_tx(30101, 'ru', 'техническая ошибка');
execute upsert_tx(30201, 'ru', 'обязательное поле не заполнено: {0}');
execute upsert_tx(30202, 'ru', 'ошибка при заполнении обязательного поля: {0}={1}');
execute upsert_tx(30203, 'ru', 'поле неизвестно: {0}={1}');
execute upsert_tx(30401, 'ru', 'ошибка приложения: {0}');
execute upsert_tx(30501, 'ru', 'неправильный PIN');
execute upsert_tx(30502, 'ru', 'срок действия PIN истек');
execute upsert_tx(30503, 'ru', 'неизвестный пользователь');
execute upsert_tx(30504, 'ru', 'неизвестный автомобиль');
execute upsert_tx(30505, 'ru', 'неизвестный КПК');
execute upsert_tx(30506, 'ru', 'перевозчик не совпадает');
execute upsert_tx(30507, 'ru', 'TODO Invalid driver certificate.');
execute upsert_tx(30601, 'ru', 'несовместимый состав автомобиля');
execute upsert_tx(30602, 'ru', 'Материал несовпадает с контейнером: {0}');
execute upsert_tx(30603, 'ru', 'неизвестный материал в контейнере: {0}');
execute upsert_tx(30604, 'ru', 'неверные изменения');

execute upsert_tx(40000, 'ru', 'OK');
execute upsert_tx(40101, 'ru', 'техническая ошибка');
execute upsert_tx(40102, 'ru', 'повторить');
execute upsert_tx(40201, 'ru', 'обязательное поле не заполнено: <1>');
execute upsert_tx(40202, 'ru', 'ошибка при заполнении обязательного поля: <1>');
execute upsert_tx(40401, 'ru', 'ошибка приложения: <1>');

prompt INSERTING into TLGNR_MOP_STATUS_TX - PL
execute upsert_tx(10000, 'pl', 'OK');
execute upsert_tx(10101, 'pl', 'Błąd techniczny');
execute upsert_tx(10102, 'pl', 'Ponów');
execute upsert_tx(10201, 'pl', 'Pole obowiązkowe niewypełnione: {0}');
execute upsert_tx(10202, 'pl', 'Błąd pola obowiązkowego: {0}={1}');
execute upsert_tx(10401, 'pl', 'Błąd aplikacji: {0}');
execute upsert_tx(10402, 'pl', 'Błąd harmonogramu: {0}');
execute upsert_tx(10601, 'pl', 'Ładowanie nie jest możliwe: {0}');
execute upsert_tx(10602, 'pl', 'Brak ładunku na wozie: {0}');
execute upsert_tx(10603, 'pl', 'Ładowanie przerwane: {0}');
execute upsert_tx(10604, 'pl', 'Niezgodny wagon: {0}');
execute upsert_tx(10610, 'pl', 'Nieprawidłowe miejsce przeznaczenia');
execute upsert_tx(10611, 'pl', 'Nie znaleziono odbiorcy towaru na terenie sprzedaży');
execute upsert_tx(10612, 'pl', 'Znaleziono ograniczenia terenu sprzedaży');
execute upsert_tx(10613, 'pl', 'Odbiorca towaru na terenie sprzedaży nie znany');
execute upsert_tx(10614, 'pl', 'Nieznany zakład produkcyjny');
execute upsert_tx(10615, 'pl', 'Stan materiału nie znaleziony');
execute upsert_tx(10616, 'pl', 'Nie znaleziono regionu');
execute upsert_tx(10617, 'pl', 'Nieprawidłowy typ dostawy');
execute upsert_tx(10618, 'pl', 'Nie znaleziono SAP BP w regionie sprzedaży');
execute upsert_tx(10619, 'pl', 'Nie znaleziono trasy');
execute upsert_tx(10620, 'pl', 'Nie znaleziono punktu przeznaczenia');
execute upsert_tx(10621, 'pl', 'Sprawdzenie tary nie powiodło się');
execute upsert_tx(10701, 'pl', 'Ustalenie ciężarówki nie jest możliwe');
execute upsert_tx(10702, 'pl', 'Błędny kod zamówienia');

execute upsert_tx(30000, 'pl', 'OK');
execute upsert_tx(30001, 'pl', 'Login OK');
execute upsert_tx(30002, 'pl', 'Sesja istnieje');
execute upsert_tx(30101, 'pl', 'Błąd techniczny');
execute upsert_tx(30201, 'pl', 'Pole obowiązkowe niewypełnione: {0}');
execute upsert_tx(30202, 'pl', 'Błąd pola obowiązkowego: {0}={1}');
execute upsert_tx(30203, 'pl', 'Pole nieznane: {0}={1}');
execute upsert_tx(30401, 'pl', 'Błąd aplikacji: {0}');
execute upsert_tx(30501, 'pl', 'Błędny kod PIN');
execute upsert_tx(30502, 'pl', 'Nieaktualny PIN');
execute upsert_tx(30503, 'pl', 'Nieznany użytkownik');
execute upsert_tx(30504, 'pl', 'Nieznana ciężarówka');
execute upsert_tx(30505, 'pl', 'Nieznany PDA');
execute upsert_tx(30506, 'pl', 'Nieprzypisano przewoźnika');
execute upsert_tx(30507, 'pl', 'TODO Invalid driver certificate.');
execute upsert_tx(30601, 'pl', 'Niekompatybilny skład ciężarówki');
execute upsert_tx(30602, 'pl', 'Niedopasowany materiał do pojemnika: {0}');
execute upsert_tx(30603, 'pl', 'Nieznany materiał w pojemniku: {0}');
execute upsert_tx(30604, 'pl', 'Nieprawidłowy pomiar');

execute upsert_tx(40000, 'pl', 'OK');
execute upsert_tx(40101, 'pl', 'Błąd techniczny');
execute upsert_tx(40102, 'pl', 'Ponów');
execute upsert_tx(40201, 'pl', 'Pole obowiązkowe niewypełnione: <1>');
execute upsert_tx(40202, 'pl', 'Błąd pola obowiązkowego: <1>');
execute upsert_tx(40401, 'pl', 'Błąd aplikacji: <1>');

prompt INSERTING into TLGNR_MOP_STATUS_TX - AZ (ENGLISH COPY)
execute upsert_tx(10000, 'az', 'OK');
execute upsert_tx(10101, 'az', 'Texniki səhv');
execute upsert_tx(10102, 'az', 'Bir daha sına');
execute upsert_tx(10201, 'az', 'Məcburi sahə buraxılışı: {0}');
execute upsert_tx(10202, 'az', 'Məcburi sahə səhvi: {0}={1}');
execute upsert_tx(10401, 'az', 'Tətbiq səhvi: {0}');
execute upsert_tx(10402, 'az', 'Səhvlərin planlaşdırılması: {0}');
execute upsert_tx(10601, 'az', 'Yükləmə mümkün deyil: {0}');
execute upsert_tx(10602, 'az', 'Vaqonda yük yoxdur: {0}');
execute upsert_tx(10603, 'az', 'Yükləmə dayandırılıb: {0}');
execute upsert_tx(10604, 'az', 'Qeyri-münasib vaqon: {0}');
execute upsert_tx(10610, 'az', 'Etibarsız “Hara göndərilir” tipi');
execute upsert_tx(10611, 'az', '“Satış sahəsinə göndərilir” tapılmadı');
execute upsert_tx(10612, 'az', 'Çoxdəfəli “Məhdud satış sahəsi” tapıldı');
execute upsert_tx(10613, 'az', '“Satış sahəsinə göndərilir” məlum deyil');
execute upsert_tx(10614, 'az', 'İstehsalat Zavodu məlum deyil');
execute upsert_tx(10615, 'az', 'Material vəziyyəti tapılmadı');
execute upsert_tx(10616, 'az', 'Park tapılmadı');
execute upsert_tx(10617, 'az', 'Etibarsız çatdırma növü');
execute upsert_tx(10618, 'az', '“SAP BP” satış sahəsi tapılmadı');
execute upsert_tx(10619, 'az', 'Marşrut tapılmadı');
execute upsert_tx(10620, 'az', 'Nəqletmə məntəqəsi tapılmadı');
execute upsert_tx(10621, 'az', 'Tara xəbərdarlıq yoxlaması baş tutmadı');
execute upsert_tx(10701, 'az', 'Yük maşını tapşırıq mümkün deyil');
execute upsert_tx(10702, 'az', 'Etibarsız sifarişi təsdiq kodu');

execute upsert_tx(30000, 'az', 'OK');
execute upsert_tx(30001, 'az', 'Qeydiyyat təsdiqi');
execute upsert_tx(30002, 'az', 'Sessiya mövcuddur');
execute upsert_tx(30101, 'az', 'Texniki səhv');
execute upsert_tx(30201, 'az', 'Məcburi Sahə buraxılışı: {0}');
execute upsert_tx(30202, 'az', 'Məcburi Sahə səhvi: {0}={1}');
execute upsert_tx(30203, 'az', 'Sahə Məlum deyil: {0}={1}');
execute upsert_tx(30401, 'az', 'Tətbiq səhvi: {0}');
execute upsert_tx(30501, 'az', 'Yanlış PIN');
execute upsert_tx(30502, 'az', 'Köhnəlmiş PIN');
execute upsert_tx(30503, 'az', 'Naməlum İstifadəçi');
execute upsert_tx(30504, 'az', 'Naməlum Yük maşını');
execute upsert_tx(30505, 'az', 'Naməlum PDA');
execute upsert_tx(30506, 'az', 'Daşıyıcı uyğunsuzluğu');
execute upsert_tx(30507, 'az', 'TODO Invalid driver certificate.');
execute upsert_tx(30601, 'az', 'Qeyri-münasib yük maşını tərkibi');
execute upsert_tx(30602, 'az', 'Materialın konteyner uyğunsuzluğu: {0}');
execute upsert_tx(30603, 'az', 'Konteynerdə naməlum material: {0}');
execute upsert_tx(30604, 'az', 'Etibarsız ölçü');

execute upsert_tx(40000, 'az', 'OK');
execute upsert_tx(40101, 'az', 'Texniki səhv');
execute upsert_tx(40102, 'az', 'Bir daha sına');
execute upsert_tx(40201, 'az', 'Məcburi Sahə buraxılışı: <1>');
execute upsert_tx(40202, 'az', 'Məcburi Sahə səhvi: <1>');
execute upsert_tx(40401, 'az', 'Tətbiq səhvi: <1>');

prompt INSERTING into TLGNR_MOP_STATUS_TX - HR
execute upsert_tx(10000, 'hr', 'OK');
execute upsert_tx(10101, 'hr', 'Tehnička pogreška');
execute upsert_tx(10102, 'hr', 'Ponoviti');
execute upsert_tx(10201, 'hr', 'Obvezno polje nedostaj: {0}');
execute upsert_tx(10202, 'hr', 'Obavezno polje Pogreška: {0}={1}');
execute upsert_tx(10401, 'hr', 'Pogreška aplikacije: {0}');
execute upsert_tx(10402, 'hr', 'Raspored pogreške: {0}');
execute upsert_tx(10601, 'hr', 'Utovar nije moguć: {0}');
execute upsert_tx(10602, 'hr', 'Nema tereta u vagonu: {0}');
execute upsert_tx(10603, 'hr', 'Utovar prekinu: {0}');
execute upsert_tx(10604, 'hr', 'Nekompatibilni vagon: {0}');
execute upsert_tx(10610, 'hr', 'Nevažeće odredište');
execute upsert_tx(10611, 'hr', 'Odredište nije pronađeno');
execute upsert_tx(10612, 'hr', 'Višestruki areaRestr pronađen');
execute upsert_tx(10613, 'hr', 'Odredište nepoznato');
execute upsert_tx(10614, 'hr', 'Proizvodni pogon nepoznat');
execute upsert_tx(10615, 'hr', 'Materijalni uvjeti nisu pronađeni');
execute upsert_tx(10616, 'hr', 'Segment nije pronađen');
execute upsert_tx(10617, 'hr', 'Neispravna vrsta isporuke');
execute upsert_tx(10618, 'hr', 'SAP BP područje nije pronađeno');
execute upsert_tx(10619, 'hr', 'Ruta nije pronađena');
execute upsert_tx(10620, 'hr', 'Dostava točka nije pronađena');
execute upsert_tx(10621, 'hr', 'Vaganje tare nije uspjelo');
execute upsert_tx(10701, 'hr', 'Raspored kamiona nije moguć');
execute upsert_tx(10702, 'hr', 'Nevažeći kod valjanosti narudžbe');

execute upsert_tx(30000, 'hr', 'OK');
execute upsert_tx(30001, 'hr', 'Prijava OK');
execute upsert_tx(30002, 'hr', 'Sesija postoji');
execute upsert_tx(30101, 'hr', 'Tehnička pogreška');
execute upsert_tx(30201, 'hr', 'Obvezno polje nedostaje: {0}');
execute upsert_tx(30202, 'hr', 'Obvezno Polje Pogreška: {0}={1}');
execute upsert_tx(30203, 'hr', 'Polje nepoznati: {0}={1}');
execute upsert_tx(30401, 'hr', 'Pogreška aplikacije: {0}');
execute upsert_tx(30501, 'hr', 'Pogrešan PIN');
execute upsert_tx(30502, 'hr', 'Zastarjeli PIN');
execute upsert_tx(30503, 'hr', 'Nepoznati korisnik');
execute upsert_tx(30504, 'hr', 'Nepoznati kamion');
execute upsert_tx(30505, 'hr', 'Nepoznati PDA');
execute upsert_tx(30506, 'hr', 'Neusklađenost prijevoznika');
execute upsert_tx(30507, 'hr', 'TODO Invalid driver certificate.');
execute upsert_tx(30601, 'hr', 'Nekompatibilni sustav kamiona');
execute upsert_tx(30602, 'hr', 'Materijal neusklađenost sa spremnika: {0}');
execute upsert_tx(30603, 'hr', 'Nepoznati materijal u kontejneru: {0}');
execute upsert_tx(30604, 'hr', 'Nevažeće mjerenje');

execute upsert_tx(40000, 'hr', 'OK');
execute upsert_tx(40101, 'hr', 'Tehnička pogreška');
execute upsert_tx(40102, 'hr', 'Ponoviti');
execute upsert_tx(40201, 'hr', 'Obvezno polje nedostaje: <1>');
execute upsert_tx(40202, 'hr', 'Obvezno Polje Pogreška: <1>');
execute upsert_tx(40401, 'hr', 'Pogreška aplikacije: <1>');

prompt INSERTING into TLGNR_MOP_STATUS_TX - SR
execute upsert_tx(10000, 'sr', 'OK');
execute upsert_tx(10101, 'sr', 'Tehnička greška');
execute upsert_tx(10102, 'sr', 'Ponoviti');
execute upsert_tx(10201, 'sr', 'Nedostaju obavezna polja: {0}');
execute upsert_tx(10202, 'sr', 'Greška u obaveznom polju: {0}={1}');
execute upsert_tx(10401, 'sr', 'Greška u aplikaciji: {0}');
execute upsert_tx(10402, 'sr', 'Greška u rasporedu: {0}');
execute upsert_tx(10601, 'sr', 'Utovar nije moguć: {0}');
execute upsert_tx(10602, 'sr', 'Ne tovariti u vagon: {0}');
execute upsert_tx(10603, 'sr', 'Utovar prekinut: {0}');
execute upsert_tx(10604, 'sr', 'Nekompatibilni vagon: {0}');
execute upsert_tx(10610, 'sr', 'Pogrešan ShipTo');
execute upsert_tx(10611, 'sr', 'Nije pronađena ShipTo SalesArea');
execute upsert_tx(10612, 'sr', 'Multiple SareaRestr found');
execute upsert_tx(10613, 'sr', 'ShipTo SalesArea nepoznata');
execute upsert_tx(10614, 'sr', 'Nepoznata fabrika');
execute upsert_tx(10615, 'sr', 'Material condition not found');
execute upsert_tx(10616, 'sr', 'Segment nije pronađen');
execute upsert_tx(10617, 'sr', 'Pogrešan tip isporuke');
execute upsert_tx(10618, 'sr', 'SAP Business partner nije pronađen');
execute upsert_tx(10619, 'sr', 'Ruta nije pronađena');
execute upsert_tx(10620, 'sr', 'Istovarno mesto nije nađeno');
execute upsert_tx(10621, 'sr', 'Provera tere nije uspela');
execute upsert_tx(10701, 'sr', 'Raspored vozila nije moguć');
execute upsert_tx(10702, 'sr', 'Pogrešan kod za validaciju naloga');

execute upsert_tx(30000, 'sr', 'OK');
execute upsert_tx(30001, 'sr', 'Login OK');
execute upsert_tx(30002, 'sr', 'Sesija postoji');
execute upsert_tx(30101, 'sr', 'Tehnička greška');
execute upsert_tx(30201, 'sr', 'Nedostaju obavezna polja: {0}');
execute upsert_tx(30202, 'sr', 'Greška u unosu obaveznih polja: {0}={1}');
execute upsert_tx(30203, 'sr', 'Nepoznato polje: {0}={1}');
execute upsert_tx(30401, 'sr', 'Greška u aplikaciji: {0}');
execute upsert_tx(30501, 'sr', 'Pogrešan PIN ');
execute upsert_tx(30502, 'sr', 'PIN je istekao');
execute upsert_tx(30503, 'sr', 'Nepoznat korisnik');
execute upsert_tx(30504, 'sr', 'Nepoznat kamion');
execute upsert_tx(30505, 'sr', 'Nepoznat PDA');
execute upsert_tx(30506, 'sr', 'Prevoznik ne odgovara');
execute upsert_tx(30507, 'sr', 'TODO Invalid driver certificate.');
execute upsert_tx(30601, 'sr', 'Nekompatibilna kompozicija');
execute upsert_tx(30602, 'sr', 'Neslaganje materijala sa kontejnerom : {0}');
execute upsert_tx(30603, 'sr', 'Nepoznat materijal u kontejneru: {0}');
execute upsert_tx(30604, 'sr', 'Nevažeće merenje');

execute upsert_tx(40000, 'sr', 'OK');
execute upsert_tx(40101, 'sr', 'Tehnička greška');
execute upsert_tx(40102, 'sr', 'Ponoviti');
execute upsert_tx(40201, 'sr', 'Nedostaju obavezna polja: <1>');
execute upsert_tx(40202, 'sr', 'Greška u unosu obaveznih polja: <1>');
execute upsert_tx(40401, 'sr', 'Greška u aplikaciji: <1>');

prompt INSERTING into TLGNR_MOP_STATUS_TX - TR
execute upsert_tx(10000, 'tr', 'OK');
execute upsert_tx(10101, 'tr', 'Technical Error');
execute upsert_tx(10102, 'tr', 'Retry');
execute upsert_tx(10201, 'tr', 'Mandatory Field Missing: {0}');
execute upsert_tx(10202, 'tr', 'Mandatory Field Error: {0}={1}');
execute upsert_tx(10401, 'tr', 'Application Error: {0}');
execute upsert_tx(10402, 'tr', 'Scheduling error: {0}');
execute upsert_tx(10601, 'tr', 'Loading not possible: {0}');
execute upsert_tx(10602, 'tr', 'No load on wagon: {0}');
execute upsert_tx(10603, 'tr', 'Loading aborted: {0}');
execute upsert_tx(10604, 'tr', 'Incompatible wagon: {0}');
execute upsert_tx(10610, 'tr', 'Invalid ShipTo type');
execute upsert_tx(10611, 'tr', 'ShipTo Sarea not found');
execute upsert_tx(10612, 'tr', 'Multiple SareaRestr found');
execute upsert_tx(10613, 'tr', 'ShipTo Sarea unknown');
execute upsert_tx(10614, 'tr', 'Production plant unknown');
execute upsert_tx(10615, 'tr', 'Material condition not found');
execute upsert_tx(10616, 'tr', 'Pool not found');
execute upsert_tx(10617, 'tr', 'Invalid delivery type');
execute upsert_tx(10618, 'tr', 'SAP BP Sarea not found');
execute upsert_tx(10619, 'tr', 'Route not found');
execute upsert_tx(10620, 'tr', 'Shipping point not found');
execute upsert_tx(10621, 'tr', 'Tare warn check failed');
execute upsert_tx(10701, 'tr', 'Truck assignment not possible');
execute upsert_tx(10702, 'tr', 'Invalid order validation code');

execute upsert_tx(30000, 'tr', 'OK');
execute upsert_tx(30001, 'tr', 'Login OK');
execute upsert_tx(30002, 'tr', 'Session exists');
execute upsert_tx(30101, 'tr', 'Technical Error');
execute upsert_tx(30201, 'tr', 'Mandatory Field Missing: {0}');
execute upsert_tx(30202, 'tr', 'Mandatory Field Error: {0}={1}');
execute upsert_tx(30203, 'tr', 'Field Unknown: {0}={1}');
execute upsert_tx(30401, 'tr', 'Application Error: {0}');
execute upsert_tx(30501, 'tr', 'Wrong PIN');
execute upsert_tx(30502, 'tr', 'Outdated PIN');
execute upsert_tx(30503, 'tr', 'Unknown User');
execute upsert_tx(30504, 'tr', 'Unknown Truck');
execute upsert_tx(30505, 'tr', 'Unknown PDA');
execute upsert_tx(30506, 'tr', 'Carriermismatch');
execute upsert_tx(30507, 'tr', 'TODO Invalid driver certificate.');
execute upsert_tx(30601, 'tr', 'IncompatibleTruckComposition');
execute upsert_tx(30602, 'tr', 'Material mismatch with container: {0}');
execute upsert_tx(30603, 'tr', 'Unknown material in container: {0}');
execute upsert_tx(30604, 'tr', 'Invalid measurement');

execute upsert_tx(40000, 'tr', 'OK');
execute upsert_tx(40101, 'tr', 'Technical Error');
execute upsert_tx(40102, 'tr', 'Retry');
execute upsert_tx(40201, 'tr', 'Mandatory Field Missing: <1>');
execute upsert_tx(40202, 'tr', 'Mandatory Field Error: <1>');
execute upsert_tx(40401, 'tr', 'Application Error: <1>');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;
