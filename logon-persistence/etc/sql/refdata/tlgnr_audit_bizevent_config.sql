-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_audit_bizevent_config.sql ----------'

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
-- tlgnr_audit_bizevent_config_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_audit_bizevent_config_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_audit_bizevent_config_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_audit_bizevent_config_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.audit_bizevent_config_cd)
    into v_exists_flag
    from tlgnr_audit_bizevent_config_bs bs
    where bs.audit_bizevent_config_cd = p_audit_bizevent_config_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_audit_bizevent_config_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_audit_bizevent_config_bs set
/* audit_bizevent_config_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          audit_bizevent_config_cd = p_audit_bizevent_config_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_audit_bizevent_config_cd || ').');
      end if;
    else
      insert into tlgnr_audit_bizevent_config_bs (
        audit_bizevent_config_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_audit_bizevent_config_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_audit_bizevent_config_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_audit_bizevent_config_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_audit_bizevent_config_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_audit_bizevent_config_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_audit_bizevent_config_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.audit_bizevent_config_cd) into v_exists_bs_flag
    from tlgnr_audit_bizevent_config_bs bs
    where bs.audit_bizevent_config_cd = p_audit_bizevent_config_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.audit_bizevent_config_cd) into v_exists_flag
      from tlgnr_audit_bizevent_config_tx tx
      where tx.audit_bizevent_config_cd = p_audit_bizevent_config_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_audit_bizevent_config_tx set
/* p_audit_bizevent_config_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          audit_bizevent_config_cd = p_audit_bizevent_config_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_audit_bizevent_config_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_audit_bizevent_config_tx (
          audit_bizevent_config_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_audit_bizevent_config_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_audit_bizevent_config_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_audit_bizevent_config_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_BS
--prompt inserting audit table data (ADN67959)
execute upsert_bs(1000, 1);
execute upsert_bs(1001, 1);
execute upsert_bs(1002, 1);
execute upsert_bs(1003, 1);
execute upsert_bs(1004, 1);
execute upsert_bs(1005, 1);
execute upsert_bs(1006, 1);
execute upsert_bs(1007, 1);
execute upsert_bs(1008, 1);
execute upsert_bs(1009, 1);
execute upsert_bs(1010, 1);
execute upsert_bs(1011, 1);
execute upsert_bs(1012, 1);
execute upsert_bs(1019, 1);
execute upsert_bs(1020, 1);
execute upsert_bs(1021, 1);
execute upsert_bs(1022, 1);
execute upsert_bs(1023, 1);
execute upsert_bs(1024, 1);
execute upsert_bs(1025, 1);
execute upsert_bs(1026, 1);
execute upsert_bs(1027, 1);
execute upsert_bs(1028, 1);
execute upsert_bs(1029, 1);
execute upsert_bs(1030, 1);
execute upsert_bs(1031, 1);
execute upsert_bs(1032, 1);
execute upsert_bs(1033, 1);
execute upsert_bs(1034, 1);
execute upsert_bs(1035, 1);
execute upsert_bs(1036, 1);
execute upsert_bs(1037, 1);
execute upsert_bs(1038, 1);
execute upsert_bs(1039, 1);
execute upsert_bs(1040, 1);
execute upsert_bs(1041, 1);
execute upsert_bs(1042, 1);
execute upsert_bs(1043, 1);
execute upsert_bs(1044, 1);
execute upsert_bs(1045, 1);
execute upsert_bs(1046, 1);

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - EN
execute upsert_tx(1000, 'en', 'Order status');
execute upsert_tx(1001, 'en', 'ShipTo');
execute upsert_tx(1002, 'en', 'SoldTo');
execute upsert_tx(1003, 'en', 'BillTo');
execute upsert_tx(1004, 'en', 'Pool');
execute upsert_tx(1005, 'en', 'Commercial Carrier');
execute upsert_tx(1006, 'en', 'Shipping point');
execute upsert_tx(1007, 'en', 'Delivery type');
execute upsert_tx(1008, 'en', 'Delivery from date');
execute upsert_tx(1009, 'en', 'Delivery to date');
execute upsert_tx(1010, 'en', 'Flag "Release SAP"');
execute upsert_tx(1011, 'en', 'Flag "Free of Charge Customer"');
execute upsert_tx(1012, 'en', 'No Pda or Rail Event');
execute upsert_tx(1019, 'en', 'Remark for Order taking (Header)');
execute upsert_tx(1020, 'en', 'Delivery note text (Header)');
execute upsert_tx(1021, 'en', 'Remark for Scheduling (Header)');
execute upsert_tx(1022, 'en', 'Remark for Driver (Header)');
execute upsert_tx(1023, 'en', 'Remark for Shipping (Header)');
execute upsert_tx(1024, 'en', 'Carrier delivery note');
execute upsert_tx(1025, 'en', 'Carrier truck driver note');
execute upsert_tx(1026, 'en', 'Customer Delivery Note Comment');
execute upsert_tx(1027, 'en', 'Flag "On-Hold order"');
execute upsert_tx(1028, 'en', 'Production Plant');
execute upsert_tx(1029, 'en', 'Ordered Quantity UOM');
execute upsert_tx(1030, 'en', 'Ordered Quantity');
execute upsert_tx(1031, 'en', 'Loaded Quantity UOM');
execute upsert_tx(1032, 'en', 'Loaded Quantity');
execute upsert_tx(1033, 'en', 'Returned Quantity UOM');
execute upsert_tx(1034, 'en', 'Returned Quantity');
execute upsert_tx(1035, 'en', 'Delivered Quantity UOM');
execute upsert_tx(1036, 'en', 'Delivered Quantity');
execute upsert_tx(1037, 'en', 'Loading Point');
execute upsert_tx(1038, 'en', 'Customer Silo');
execute upsert_tx(1039, 'en', 'Remark for Order taking (Item)');
execute upsert_tx(1040, 'en', 'Remark for Scheduling (Item)');
execute upsert_tx(1041, 'en', 'Remark for Driver (Item)');
execute upsert_tx(1042, 'en', 'Remark for Shipping (Item)');
execute upsert_tx(1043, 'en', 'Executing Carrier');
execute upsert_tx(1044, 'en', 'Return reason code');
execute upsert_tx(1045, 'en', 'Special processing indicator');
execute upsert_tx(1046, 'en', 'iTrack status');

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - DE
execute upsert_tx(1000, 'de', 'Auftragstatus');
execute upsert_tx(1001, 'de', 'Warenempfänger');
execute upsert_tx(1002, 'de', 'Auftraggeber');
execute upsert_tx(1003, 'de', 'Rechnungsempfänger');
execute upsert_tx(1004, 'de', 'Pool');
execute upsert_tx(1005, 'de', 'Kommerziell Spediteur');
execute upsert_tx(1006, 'de', 'Versandstelle');
execute upsert_tx(1007, 'de', 'Lieferart');
execute upsert_tx(1008, 'de', 'Lieferung "Von-Datum"');
execute upsert_tx(1009, 'de', 'Lieferung "Bis-Datum"');
execute upsert_tx(1010, 'de', 'Flag "Freigabe SAP"');
execute upsert_tx(1011, 'de', 'Flag "Gratislieferung Kunde"');
execute upsert_tx(1012, 'de', 'No Pda or Rail Event');
execute upsert_tx(1019, 'de', 'Notiz für Auftragsannahme (Header)');
execute upsert_tx(1020, 'de', 'Lieferschein Zusatztext (Header)');
execute upsert_tx(1021, 'de', 'Notiz für Disposition (Header)');
execute upsert_tx(1022, 'de', 'Notiz für Fahrer (Header)');
execute upsert_tx(1023, 'de', 'Notiz für Spedition (Header)');
execute upsert_tx(1024, 'de', 'Lieferschein Spediteur');
execute upsert_tx(1025, 'de', 'Spediteur Bemerkungen für Fahrer');
execute upsert_tx(1026, 'de', 'Lieferschein Zusatztext Kunde');
execute upsert_tx(1027, 'de', 'Flag "Abrufauftrag"');
execute upsert_tx(1028, 'de', 'Produktionswerk');
execute upsert_tx(1029, 'de', 'Bestellmenge Einheit');
execute upsert_tx(1030, 'de', 'Bestellmenge');
execute upsert_tx(1031, 'de', 'Lademenge Einheit');
execute upsert_tx(1032, 'de', 'Lademenge');
execute upsert_tx(1033, 'de', 'Rückmenge Einheit');
execute upsert_tx(1034, 'de', 'Rückmenge');
execute upsert_tx(1035, 'de', 'Gelieferte Einheit');
execute upsert_tx(1036, 'de', 'Gelieferte Menge');
execute upsert_tx(1037, 'de', 'Ladestrasse');
execute upsert_tx(1038, 'de', 'Kunden Silo');
execute upsert_tx(1039, 'de', 'Notiz für Auftragsannahme (Item)');
execute upsert_tx(1040, 'de', 'Notiz für Disposition (Item)');
execute upsert_tx(1041, 'de', 'Notiz für Fahrer (Item)');
execute upsert_tx(1042, 'de', 'Notiz für Spedition (Item)');
execute upsert_tx(1043, 'de', 'Ausführung Spediteur');
execute upsert_tx(1044, 'de', 'Code für Rücklieferungsgrund');
execute upsert_tx(1045, 'de', 'Indikator für Spezialprozess');
execute upsert_tx(1046, 'de', 'iTrack status');

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - FR
execute upsert_tx(1000, 'fr', 'Statut de l''ordre');
execute upsert_tx(1001, 'fr', 'Destination');
execute upsert_tx(1002, 'fr', 'Mandant');
execute upsert_tx(1003, 'fr', 'Facturer à');
execute upsert_tx(1004, 'fr', 'Pool');
execute upsert_tx(1005, 'fr', 'Commercial Transporteur');
execute upsert_tx(1006, 'fr', 'Lieu d''expédition');
execute upsert_tx(1007, 'fr', 'Type de livraison');
execute upsert_tx(1008, 'fr', 'Date de livraison de');
execute upsert_tx(1009, 'fr', 'Date de livraison à');
execute upsert_tx(1010, 'fr', 'Flag "Autorisation SAP"');
execute upsert_tx(1011, 'fr', 'Flag "Client de livraison gratuite"');
execute upsert_tx(1012, 'fr', 'No Pda or Rail Event');
execute upsert_tx(1019, 'fr', 'Remarque pour la Prise de commande (Titre)');
execute upsert_tx(1020, 'fr', 'Texte bulletin de livraison (Titre)');
execute upsert_tx(1021, 'fr', 'Remarque pour Planification (Titre)');
execute upsert_tx(1022, 'fr', 'Remarque pour Chauffeur (Titre)');
execute upsert_tx(1023, 'fr', 'Remarque pour Expédition (Titre)');
execute upsert_tx(1024, 'fr', 'Bulletin de livraison du Transporteur');
execute upsert_tx(1025, 'fr', 'Notes pour le Chauffeur');
execute upsert_tx(1026, 'fr', 'Commentaire Bulletin de Livraison Client');
execute upsert_tx(1027, 'fr', 'Flag "Ordre en attente"');
execute upsert_tx(1028, 'fr', 'Usine de production');
execute upsert_tx(1029, 'fr', 'Quantité Commandée UdM');
execute upsert_tx(1030, 'fr', 'Quantité Commandée');
execute upsert_tx(1031, 'fr', 'Quantité Chargée UdM');
execute upsert_tx(1032, 'fr', 'Quantité Chargée');
execute upsert_tx(1033, 'fr', 'Quantité Retournée UdM');
execute upsert_tx(1034, 'fr', 'Quantité Retournée');
execute upsert_tx(1035, 'fr', 'Quantité Livrée UdM');
execute upsert_tx(1036, 'fr', 'Quantité Livrée');
execute upsert_tx(1037, 'fr', 'Pont de chargement');
execute upsert_tx(1038, 'fr', 'Silos Client');
execute upsert_tx(1039, 'fr', 'Remarque pour la Prise de commande (Élément)');
execute upsert_tx(1040, 'fr', 'Remarque pour Scheduling (Élément)');
execute upsert_tx(1041, 'fr', 'Remarque pour Chauffeur (Élément)');
execute upsert_tx(1042, 'fr', 'Remarque pour Expédition (Élément)');
execute upsert_tx(1043, 'fr', 'Executing Transporteur');
execute upsert_tx(1044, 'fr', 'Code de motif du retour');
execute upsert_tx(1045, 'fr', 'Indicateur de traitement spécial');
execute upsert_tx(1046, 'fr', 'iTrack status');

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - IT
execute upsert_tx(1000, 'it', 'Stato Ordine');
execute upsert_tx(1001, 'it', 'Destinatario');
execute upsert_tx(1002, 'it', 'Committente');
execute upsert_tx(1003, 'it', 'Destinatario Fattura');
execute upsert_tx(1004, 'it', 'Pool');
execute upsert_tx(1005, 'it', 'Vettore');
execute upsert_tx(1006, 'it', 'Punto di spedizione');
execute upsert_tx(1007, 'it', 'Tipo di consegna');
execute upsert_tx(1008, 'it', 'Data di consegna da');
execute upsert_tx(1009, 'it', 'Data di consegna fino a');
execute upsert_tx(1010, 'it', 'Flag "Rilasciato a SAP"');
execute upsert_tx(1011, 'it', 'Flag "Cliente consegna gratuita"');
execute upsert_tx(1012, 'it', 'No Pda or Rail Event');
execute upsert_tx(1019, 'it', 'Osservazione per la Ricezione ordine (Intestazione)');
execute upsert_tx(1020, 'it', 'Testo bollettino di consegna (Intestazione)');
execute upsert_tx(1021, 'it', 'Osservazione per Scheduling (Intestazione)');
execute upsert_tx(1022, 'it', 'Osservazione per Autista (Intestazione)');
execute upsert_tx(1023, 'it', 'Osservazione per Spedizione (Intestazione)');
execute upsert_tx(1024, 'it', 'Bollettino di consegna del Vettore');
execute upsert_tx(1025, 'it', 'Note per l''Autista (sub-)vettore');
execute upsert_tx(1026, 'it', 'Commento Bollettino di consegna Cliente');
execute upsert_tx(1027, 'it', 'Flag "Ordine in attesa"');
execute upsert_tx(1028, 'it', 'Impianto di produzione');
execute upsert_tx(1029, 'it', 'Quantità Ordinata UdM');
execute upsert_tx(1030, 'it', 'Quantità Ordinata');
execute upsert_tx(1031, 'it', 'Quantità Caricata UdM');
execute upsert_tx(1032, 'it', 'Quantità Caricata');
execute upsert_tx(1033, 'it', 'Quantità Ritornata UdM');
execute upsert_tx(1034, 'it', 'Quantità Ritornata');
execute upsert_tx(1035, 'it', 'Quantità Consegnata UdM');
execute upsert_tx(1036, 'it', 'Quantità Consegnata');
execute upsert_tx(1037, 'it', 'Punto di carico');
execute upsert_tx(1038, 'it', 'Clienti Silo');
execute upsert_tx(1039, 'it', 'Osservazione per la Ricezione ordine (Articolo)');
execute upsert_tx(1040, 'it', 'Osservazione per Scheduling (Articolo)');
execute upsert_tx(1041, 'it', 'Osservazione per Autista (Articolo)');
execute upsert_tx(1042, 'it', 'Osservazione per Spedizione (Articolo)');
execute upsert_tx(1043, 'it', 'Sub-vettore');
execute upsert_tx(1044, 'it', 'Codice della ragione di ritorno');
execute upsert_tx(1045, 'it', 'Indicatore di trattamento speciale');
execute upsert_tx(1046, 'it', 'iTrack status');

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - ES
execute upsert_tx(1000, 'es', 'Estado pedido');
execute upsert_tx(1001, 'es', 'Destinatario');
execute upsert_tx(1002, 'es', 'Cliente');
execute upsert_tx(1003, 'es', 'Destinatario factura');
execute upsert_tx(1004, 'es', 'Región');
execute upsert_tx(1005, 'es', 'Operador logístico');
execute upsert_tx(1006, 'es', 'Punto de expedición');
execute upsert_tx(1007, 'es', 'Tipo de entrega');
execute upsert_tx(1008, 'es', 'Fecha de entrega desde');
execute upsert_tx(1009, 'es', 'Fecha de entrega hasta');
execute upsert_tx(1010, 'es', 'Flag "Enviar a SAP"');
execute upsert_tx(1011, 'es', 'Flag "Cliente gratis"');
execute upsert_tx(1012, 'es', 'No Pda or Rail Event');
execute upsert_tx(1019, 'es', 'Observaciones para toma de pedidos (Cabecera)');
execute upsert_tx(1020, 'es', 'Texto albarán (Cabecera)');
execute upsert_tx(1021, 'es', 'Observaciones para planificación (Cabecera)');
execute upsert_tx(1022, 'es', 'Observaciones para el conductor (Cabecera)');
execute upsert_tx(1023, 'es', 'Observaciones para transporte (Cabecera)');
execute upsert_tx(1024, 'es', 'Albarán Transportista');
execute upsert_tx(1025, 'es', 'Notas para el conductor del transportista');
execute upsert_tx(1026, 'es', 'Comentario albarán cliente');
execute upsert_tx(1027, 'es', 'Flag "Suspender pedido"');
execute upsert_tx(1028, 'es', 'Planta de Producción');
execute upsert_tx(1029, 'es', 'UdM cantidad de pedido');
execute upsert_tx(1030, 'es', 'Cantidad de pedido');
execute upsert_tx(1031, 'es', 'UdM Cantidad cargada');
execute upsert_tx(1032, 'es', 'Cantidad cargada');
execute upsert_tx(1033, 'es', 'UdM cantidad de retorno');
execute upsert_tx(1034, 'es', 'Cantidad de retorno');
execute upsert_tx(1035, 'es', 'UdM cantidad entregada');
execute upsert_tx(1036, 'es', 'Cantidad entregada');
execute upsert_tx(1037, 'es', 'Punto de carga');
execute upsert_tx(1038, 'es', 'Silo del cliente');
execute upsert_tx(1039, 'es', 'Observaciones para toma de pedidos (Artículo)');
execute upsert_tx(1040, 'es', 'Observaciones para planificación (Item)');
execute upsert_tx(1041, 'es', 'Observaciones para el conductor (Item)');
execute upsert_tx(1042, 'es', 'Observaciones para transporte (Item)');
execute upsert_tx(1043, 'es', 'Transportista');
execute upsert_tx(1044, 'es', 'Código motivo de devolución');
execute upsert_tx(1045, 'es', 'Indicador procesado especial');
execute upsert_tx(1046, 'es', 'iTrack status');

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - CS
execute upsert_tx(1000, 'cs', 'Status objednávky');
execute upsert_tx(1001, 'cs', 'Příjemce materiálu');
execute upsert_tx(1002, 'cs', 'Odběratel');
execute upsert_tx(1003, 'cs', 'Plátce');
execute upsert_tx(1004, 'cs', 'Skupina');
execute upsert_tx(1005, 'cs', 'Komerční dopravce');
execute upsert_tx(1006, 'cs', 'Místo nakládky');
execute upsert_tx(1007, 'cs', 'Typ dodávky');
execute upsert_tx(1008, 'cs', 'Datum dodávky od');
execute upsert_tx(1009, 'cs', 'Datum dodávky do');
execute upsert_tx(1010, 'cs', 'Označení "Uvolnění do SAP"');
execute upsert_tx(1011, 'cs', 'Označení "Bezplatně zákazníkovi"');
execute upsert_tx(1012, 'cs', 'No Pda or Rail Event');
execute upsert_tx(1019, 'cs', 'Poznámka pro příjemce objednávky (Hlavička)');
execute upsert_tx(1020, 'cs', 'Text na Dodací list (Hlavička)');
execute upsert_tx(1021, 'cs', 'Poznámka pro plánování (Hlavička)');
execute upsert_tx(1022, 'cs', 'Poznámka pro řidiče (Hlavička)');
execute upsert_tx(1023, 'cs', 'Poznámka pro přepravu (Hlavička)');
execute upsert_tx(1024, 'cs', 'Dodací list - Dopravce');
execute upsert_tx(1025, 'cs', 'List řidiče dopravce');
execute upsert_tx(1026, 'cs', 'Komentář - Dodací list');
execute upsert_tx(1027, 'cs', 'Označení "Zastavená objednávka"');
execute upsert_tx(1028, 'cs', 'Výrobní závod');
execute upsert_tx(1029, 'cs', 'Objednané množství - MJ');
execute upsert_tx(1030, 'cs', 'Objednané množství');
execute upsert_tx(1031, 'cs', 'Naložené množství - MJ');
execute upsert_tx(1032, 'cs', 'Naložené množství');
execute upsert_tx(1033, 'cs', 'Vrácené množství - MJ');
execute upsert_tx(1034, 'cs', 'Vrácené množství');
execute upsert_tx(1035, 'cs', 'Dodané množství - MJ');
execute upsert_tx(1036, 'cs', 'Dodané množství');
execute upsert_tx(1037, 'cs', 'Místo nakládky');
execute upsert_tx(1038, 'cs', 'Silo zákazníka');
execute upsert_tx(1039, 'cs', 'Poznámka pro příjemce objednávky (Položka)');
execute upsert_tx(1040, 'cs', 'Poznámka pro plánování (Položka)');
execute upsert_tx(1041, 'cs', 'Poznámka pro řidiče (Položka)');
execute upsert_tx(1042, 'cs', 'Poznámka pro přepravu (Položka)');
execute upsert_tx(1043, 'cs', 'Prováděcí dopravce');
execute upsert_tx(1044, 'cs', 'Kód důvodu vrácení');
execute upsert_tx(1045, 'cs', 'Indikátor spec. procesu');
execute upsert_tx(1046, 'cs', 'iTrack status');

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - SK
execute upsert_tx(1000, 'sk', 'Status objednávky');
execute upsert_tx(1001, 'sk', 'Príjemca tovaru');
execute upsert_tx(1002, 'sk', 'Predané');
execute upsert_tx(1003, 'sk', 'Fakturovať');
execute upsert_tx(1004, 'sk', 'Región');
execute upsert_tx(1005, 'sk', 'Komerčný prepravca');
execute upsert_tx(1006, 'sk', 'Miesto nakládky');
execute upsert_tx(1007, 'sk', 'Typ dodávky');
execute upsert_tx(1008, 'sk', 'Dodávka z dátumu');
execute upsert_tx(1009, 'sk', 'Dodávka k dátumu');
execute upsert_tx(1010, 'sk', 'Zaškrtnutie "Uvoľnenie SAP"');
execute upsert_tx(1011, 'sk', 'Zaškrtnutie "Zákazník bez poplatkov"');
execute upsert_tx(1012, 'sk', 'No Pda or Rail Event');
execute upsert_tx(1019, 'sk', 'Poznámka pre objednanie (Záhlavie)');
execute upsert_tx(1020, 'sk', 'Dodací list text (Záhlavie)');
execute upsert_tx(1021, 'sk', 'Poznámka pre plánovanie (Záhlavie)');
execute upsert_tx(1022, 'sk', 'Poznámka pre vodiča (Záhlavie)');
execute upsert_tx(1023, 'sk', 'Poznámka pre prepravu (Záhlavie)');
execute upsert_tx(1024, 'sk', 'Dodací list prepravcu');
execute upsert_tx(1025, 'sk', 'Poznámka pre vodiča dopravcu');
execute upsert_tx(1026, 'sk', 'Poznámka pre dodací list zákazníka');
execute upsert_tx(1027, 'sk', 'Zaškrtnutie "Pozastavená objednávka"');
execute upsert_tx(1028, 'sk', 'Výrobný závod');
execute upsert_tx(1029, 'sk', 'Objednané množstvo MJ');
execute upsert_tx(1030, 'sk', 'Objednané množstvo');
execute upsert_tx(1031, 'sk', 'Naložené množstvo MJ');
execute upsert_tx(1032, 'sk', 'Naložené množstvo');
execute upsert_tx(1033, 'sk', 'Vrátené množstvo MJ');
execute upsert_tx(1034, 'sk', 'Vrátené množstvo');
execute upsert_tx(1035, 'sk', 'Dodacie množstvo MJ');
execute upsert_tx(1036, 'sk', 'Dodacie množstvo');
execute upsert_tx(1037, 'sk', 'Miesto nakládky');
execute upsert_tx(1038, 'sk', 'Zákaznícke silo');
execute upsert_tx(1039, 'sk', 'Poznámka pre objednanie (Položka)');
execute upsert_tx(1040, 'sk', 'Poznámka pre plánovanie (Položka)');
execute upsert_tx(1041, 'sk', 'Poznámka pre vodiča (Položka)');
execute upsert_tx(1042, 'sk', 'Poznámka pre prepravu (Položka)');
execute upsert_tx(1043, 'sk', 'Výkonný dopravca');
execute upsert_tx(1044, 'sk', 'Dôvod vrátenia kód');
execute upsert_tx(1045, 'sk', 'Indikátor špeciálneho spracovania');
execute upsert_tx(1046, 'sk', 'iTrack status');

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - HU
execute upsert_tx(1000, 'hu', 'Megredelés állapota');
execute upsert_tx(1001, 'hu', 'Ship to');
execute upsert_tx(1002, 'hu', 'Vevő');
execute upsert_tx(1003, 'hu', 'Fizető');
execute upsert_tx(1004, 'hu', 'Flotta');
execute upsert_tx(1005, 'hu', 'Kereskedelmi fuvarozó');
execute upsert_tx(1006, 'hu', 'Felrakó hely');
execute upsert_tx(1007, 'hu', 'Szállítás típusa');
execute upsert_tx(1008, 'hu', 'Szállítás indulópontja');
execute upsert_tx(1009, 'hu', 'Szállítás végpontja');
execute upsert_tx(1010, 'hu', 'Jelölés "SAP elismerés"');
execute upsert_tx(1011, 'hu', 'Jelölés "Térítésmentes ügyfél"');
execute upsert_tx(1012, 'hu', 'No Pda or Rail Event');
execute upsert_tx(1019, 'hu', 'Rendelési megjegyzés (fejléc)');
execute upsert_tx(1020, 'hu', 'Szállítási jegyet (fejléc)');
execute upsert_tx(1021, 'hu', 'Ütemezési megjegyzés (fejléc)');
execute upsert_tx(1022, 'hu', 'Vezetői megjegyzés (fejléc)');
execute upsert_tx(1023, 'hu', 'Szállítási megjegyzés (fejléc)');
execute upsert_tx(1024, 'hu', '(Fuvarozói) szállítólevél');
execute upsert_tx(1025, 'hu', 'Teherautó vezető megjegyzése');
execute upsert_tx(1026, 'hu', 'Ügyfél szállítólevél megjegyzése');
execute upsert_tx(1027, 'hu', 'Jelölés "tartott megrendelés"');
execute upsert_tx(1028, 'hu', 'Termelési üzem /gyár');
execute upsert_tx(1029, 'hu', 'Megrendelt mennyiség mértékegysége');
execute upsert_tx(1030, 'hu', 'Megrendelt mennyiség');
execute upsert_tx(1031, 'hu', 'Szállítási mennyiség mértékegysége');
execute upsert_tx(1032, 'hu', 'Szállítási mennyiség');
execute upsert_tx(1033, 'hu', 'Visszavitt mennyiség mértékegysége');
execute upsert_tx(1034, 'hu', 'Visszavitt mennyiség');
execute upsert_tx(1035, 'hu', 'Kiszállított mennyiség mértékegysége');
execute upsert_tx(1036, 'hu', 'Kiszállított mennyiség');
execute upsert_tx(1037, 'hu', 'Felrakó hely');
execute upsert_tx(1038, 'hu', 'Vevői siló');
execute upsert_tx(1039, 'hu', 'Megrendelés megjegyzése (tétel)');
execute upsert_tx(1040, 'hu', 'Ütemezési megyjegyzése (tétel)');
execute upsert_tx(1041, 'hu', 'Vezetői megjegyzés (tétel)');
execute upsert_tx(1042, 'hu', 'Szállítási megjegyzés (tétel)');
execute upsert_tx(1043, 'hu', 'Fuvarozói végrehajtása');
execute upsert_tx(1044, 'hu', 'Visszaszállítás okának a kódja');
execute upsert_tx(1045, 'hu', 'Speciális feldolgozás/gyártás oka');
execute upsert_tx(1046, 'en', 'iTrack státusz');

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - RO
execute upsert_tx(1000, 'ro', 'Status comanda');
execute upsert_tx(1001, 'ro', 'Destinatar');
execute upsert_tx(1002, 'ro', 'Destinatar');
execute upsert_tx(1003, 'ro', 'Factura catre ');
execute upsert_tx(1004, 'ro', 'Regiune');
execute upsert_tx(1005, 'ro', 'Operator Logistic');
execute upsert_tx(1006, 'ro', 'Punct de Expeditie');
execute upsert_tx(1007, 'ro', 'Tip de livrare');
execute upsert_tx(1008, 'ro', 'Livrare de la data');
execute upsert_tx(1009, 'ro', 'Livrare pana la data');
execute upsert_tx(1010, 'ro', 'Marcati "Transmitere SAP"');
execute upsert_tx(1011, 'ro', 'Marcati "Client Gratuitate"');
execute upsert_tx(1012, 'ro', 'No Pda or Rail Event');
execute upsert_tx(1019, 'ro', 'Observatii Primire comanda (Antet)');
execute upsert_tx(1020, 'ro', 'Text nota de livrare (Antet)');
execute upsert_tx(1021, 'ro', 'Observatii Planificare (Antet)');
execute upsert_tx(1022, 'ro', 'Observatii Sofer (Antet)');
execute upsert_tx(1023, 'ro', 'Observatii Expeditie (Antet)');
execute upsert_tx(1024, 'ro', 'Nota de livrare Transportator');
execute upsert_tx(1025, 'ro', 'Nota Sofer camion transportator');
execute upsert_tx(1026, 'ro', 'Observatii Nota de livrare Client');
execute upsert_tx(1027, 'ro', 'Marcati "Comanda in asteptare"');
execute upsert_tx(1028, 'ro', 'Fabrica de Productie');
execute upsert_tx(1029, 'ro', 'Cantitate comandata UOM');
execute upsert_tx(1030, 'ro', 'Cantitate comandata');
execute upsert_tx(1031, 'ro', 'Cantitate incarcata UOM');
execute upsert_tx(1032, 'ro', 'Cantitate incarcata');
execute upsert_tx(1033, 'ro', 'Cantitate returnata UOM');
execute upsert_tx(1034, 'ro', 'Cantitate returnata');
execute upsert_tx(1035, 'ro', 'Cantitate livrata UOM');
execute upsert_tx(1036, 'ro', 'Cantitate livrata');
execute upsert_tx(1037, 'ro', 'Punct de Incarcare');
execute upsert_tx(1038, 'ro', 'Siloz Client');
execute upsert_tx(1039, 'ro', 'Observatii Primire comanda (Articol)');
execute upsert_tx(1040, 'ro', 'Observatii Planificare (Articol)');
execute upsert_tx(1041, 'ro', 'Observatii Sofer (Articol)');
execute upsert_tx(1042, 'ro', 'Observatii Expeditie (Articol)');
execute upsert_tx(1043, 'ro', 'Transportator executant');
execute upsert_tx(1044, 'ro', 'Cod motiv returnare');
execute upsert_tx(1045, 'ro', 'Indicator procesare special');
execute upsert_tx(1046, 'ro', 'iTrack status');


prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - BG
execute upsert_tx(1000, 'bg', 'Статус на поръчка');
execute upsert_tx(1001, 'bg', 'Обект');
execute upsert_tx(1002, 'bg', 'Клиент');
execute upsert_tx(1003, 'bg', 'Получател на фактура');
execute upsert_tx(1004, 'bg', 'Pool');
execute upsert_tx(1005, 'bg', 'Търговски превозвач');
execute upsert_tx(1006, 'bg', 'Точка на доставка');
execute upsert_tx(1007, 'bg', 'Тип доставка');
execute upsert_tx(1008, 'bg', 'Доставка от дата');
execute upsert_tx(1009, 'bg', 'Доставка до дата');
execute upsert_tx(1010, 'bg', 'Флаг "Освободи SAP"');
execute upsert_tx(1011, 'bg', 'Флаг "Клиент, освободен от такса"');
execute upsert_tx(1012, 'bg', 'No Pda or Rail Event');
execute upsert_tx(1019, 'bg', 'Забележка към приемащия поръчка (Header)');
execute upsert_tx(1020, 'bg', 'Текст на Експедиционна бележка(Header)');
execute upsert_tx(1021, 'bg', 'Бележка за график (Header)');
execute upsert_tx(1022, 'bg', 'Бележка за шофьора (Header)');
execute upsert_tx(1023, 'bg', 'Бележка за доставяне (Header)');
execute upsert_tx(1024, 'bg', 'Експедиционна бележка за превозвач');
execute upsert_tx(1025, 'bg', 'Експедиционна бележка за превозвач на камион');
execute upsert_tx(1026, 'bg', 'Коментар за експедиционна бележка от клиент');
execute upsert_tx(1027, 'bg', 'Флаг "Поръчка в изчакване"');
execute upsert_tx(1028, 'bg', 'Производствен завод');
execute upsert_tx(1029, 'bg', 'Мерна единица на поръчано количество UOM');
execute upsert_tx(1030, 'bg', 'Поръчано количество');
execute upsert_tx(1031, 'bg', 'Мерна единица на натоварено количество UOM');
execute upsert_tx(1032, 'bg', 'Натоварено количество');
execute upsert_tx(1033, 'bg', 'Мерна единица на върнато количество UOM');
execute upsert_tx(1034, 'bg', 'Върнато количество');
execute upsert_tx(1035, 'bg', 'Мерна единица на доставено количество UOM');
execute upsert_tx(1036, 'bg', 'Доставено количество');
execute upsert_tx(1037, 'bg', 'Място на товарене');
execute upsert_tx(1038, 'bg', 'Клиентски силоз');
execute upsert_tx(1039, 'bg', 'Бележка за вземане на поръчка (Item)');
execute upsert_tx(1040, 'bg', 'Бележка за график (Item)');
execute upsert_tx(1041, 'bg', 'Бележка за шофьора (Item)');
execute upsert_tx(1042, 'bg', 'Бележка за доставяне (Item)');
execute upsert_tx(1043, 'bg', 'Изпълняващ превозвач');
execute upsert_tx(1044, 'bg', 'Код на причина за връщане');
execute upsert_tx(1045, 'bg', 'Специален изпълняващ показател');
execute upsert_tx(1046, 'bg', 'iTrack status');

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - RU (ENGLISH COPY)
execute upsert_tx(1000, 'ru', 'Статус заказа');
execute upsert_tx(1001, 'ru', 'Грузополучатель');
execute upsert_tx(1002, 'ru', 'Клиент');
execute upsert_tx(1003, 'ru', 'Плательщик');
execute upsert_tx(1004, 'ru', 'Список заказов');
execute upsert_tx(1005, 'ru', 'Коммерческий перевозчик');
execute upsert_tx(1006, 'ru', 'зона погрузки');
execute upsert_tx(1007, 'ru', 'тип доставки');
execute upsert_tx(1008, 'ru', 'доставка с момента');
execute upsert_tx(1009, 'ru', 'доставка на момент');
execute upsert_tx(1010, 'ru', 'Флаг "создать заказ в SAP"');
execute upsert_tx(1011, 'ru', 'Флаг "бесплатная поставка клиенту"');
execute upsert_tx(1012, 'ru', 'No Pda or Rail Event');
execute upsert_tx(1019, 'ru', 'Пометка для принятия заказа (заголовок)');
execute upsert_tx(1020, 'ru', 'текст накладной (заголовок)');
execute upsert_tx(1021, 'ru', 'Пометка для планирования (заголовок)');
execute upsert_tx(1022, 'ru', 'Пометка для водителя  (заголовок)');
execute upsert_tx(1023, 'ru', 'Пометка для доставки (заголовок)');
execute upsert_tx(1024, 'ru', 'Накладная для Перевозчика');
execute upsert_tx(1025, 'ru', 'Пометка для водителя  перевозчика (примечание)');
execute upsert_tx(1026, 'ru', 'Комментарий к Накладной для Заказчика');
execute upsert_tx(1027, 'ru', 'Флаг "Заказ на согласовании"');
execute upsert_tx(1028, 'ru', 'Поставщик');
execute upsert_tx(1029, 'ru', 'Заказанное количество, единицы измерения');
execute upsert_tx(1030, 'ru', 'Заказанное количество');
execute upsert_tx(1031, 'ru', 'Загруженное количество, единицы измерения');
execute upsert_tx(1032, 'ru', 'Загруженное количество');
execute upsert_tx(1033, 'ru', 'Возвращенное количество, единицы измерения');
execute upsert_tx(1034, 'ru', 'Возвращенное количество');
execute upsert_tx(1035, 'ru', 'Доставленное количество, единицы измерения');
execute upsert_tx(1036, 'ru', 'Доставленное количество');
execute upsert_tx(1037, 'ru', 'Пункт погрузки');
execute upsert_tx(1038, 'ru', 'Силос клиента');
execute upsert_tx(1039, 'ru', 'Пометка для принятия заказа (на продукт)');
execute upsert_tx(1040, 'ru', 'Пометка для планирования (на продукт)');
execute upsert_tx(1041, 'ru', 'Пометка для водителя  (на продукт)');
execute upsert_tx(1042, 'ru', 'Пометка для доставки (на продукт)');
execute upsert_tx(1043, 'ru', 'Перевозчик');
execute upsert_tx(1044, 'ru', 'Код причины возврата');
execute upsert_tx(1045, 'ru', 'особые условия поставки');
execute upsert_tx(1046, 'ru', 'iTrack status');

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - PL (ENGLISH COPY)
execute upsert_tx(1000, 'pl', 'Status zamówienia');
execute upsert_tx(1001, 'pl', 'Odbiorca towaru');
execute upsert_tx(1002, 'pl', 'Nabywca');
execute upsert_tx(1003, 'pl', 'Płatnik');
execute upsert_tx(1004, 'pl', 'Region');
execute upsert_tx(1005, 'pl', 'Lokalny przewoźnik');
execute upsert_tx(1006, 'pl', 'Punkt dostawy');
execute upsert_tx(1007, 'pl', 'Typ dostawy');
execute upsert_tx(1008, 'pl', 'Dostawa od daty');
execute upsert_tx(1009, 'pl', 'Dostawa do daty');
execute upsert_tx(1010, 'pl', 'Flaga "Zwolnij SAP"');
execute upsert_tx(1011, 'pl', 'Flaga "Klient zwolniony z opłat"');
execute upsert_tx(1012, 'pl', 'No Pda or Rail Event');
execute upsert_tx(1019, 'pl', 'Uwaga dla przyjmującego zamówienie (Nagłówek)');
execute upsert_tx(1020, 'pl', 'Treść listu przewozowego (Nagłówek)');
execute upsert_tx(1021, 'pl', 'Uwaga dla harmonogramu (Nagłówek)');
execute upsert_tx(1022, 'pl', 'Uwaga dla kierowcy (Nagłówek)');
execute upsert_tx(1023, 'pl', 'Uwaga dla Spedycji (Nagłówek)');
execute upsert_tx(1024, 'pl', 'Dowód dostawy przewoźnika');
execute upsert_tx(1025, 'pl', 'Nota kierowcy');
execute upsert_tx(1026, 'pl', 'Komentarz klienta dotyczący dostawy');
execute upsert_tx(1027, 'pl', 'Flaga "Zamówienie wstrzymane"');
execute upsert_tx(1028, 'pl', 'Zakład Produkcyjny');
execute upsert_tx(1029, 'pl', 'Zamówiona ilość UOM');
execute upsert_tx(1030, 'pl', 'Zamówiona ilość');
execute upsert_tx(1031, 'pl', 'Załadowana ilość UOM');
execute upsert_tx(1032, 'pl', 'Załadowana ilość');
execute upsert_tx(1033, 'pl', 'Zwrócona ilość UOM');
execute upsert_tx(1034, 'pl', 'Zwrócona ilość');
execute upsert_tx(1035, 'pl', 'Dostarczona ilość UOM');
execute upsert_tx(1036, 'pl', 'Dostarczona ilość');
execute upsert_tx(1037, 'pl', 'Punkt załadunkowy');
execute upsert_tx(1038, 'pl', 'Silos klienta');
execute upsert_tx(1039, 'pl', 'Uwaga dla przyjmującego zamówienie(Pozycja)');
execute upsert_tx(1040, 'pl', 'Uwaga dla planującego harmonogram (Pozycja)');
execute upsert_tx(1041, 'pl', 'Uwaga dla kierowcy (Pozycja)');
execute upsert_tx(1042, 'pl', 'Uwaga dla spedycji (Pozycja)');
execute upsert_tx(1043, 'pl', 'Przewoźnik wykonujący');
execute upsert_tx(1044, 'pl', 'Kod powodu zwrotu');
execute upsert_tx(1045, 'pl', 'Specjalny wskaźnik przetwarzania');
execute upsert_tx(1046, 'pl', 'iTrack status');

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - AZ (ENGLISH COPY)
execute upsert_tx(1000, 'az', 'Order status');
execute upsert_tx(1001, 'az', 'ShipTo');
execute upsert_tx(1002, 'az', 'SoldTo');
execute upsert_tx(1003, 'az', 'BillTo');
execute upsert_tx(1004, 'az', 'Pool');
execute upsert_tx(1005, 'az', 'Commercial Carrier');
execute upsert_tx(1006, 'az', 'Shipping point');
execute upsert_tx(1007, 'az', 'Delivery type');
execute upsert_tx(1008, 'az', 'Delivery from date');
execute upsert_tx(1009, 'az', 'Delivery to date');
execute upsert_tx(1010, 'az', 'Flag "Release SAP"');
execute upsert_tx(1011, 'az', 'Flag "Free of Charge Customer"');
execute upsert_tx(1012, 'az', 'No Pda or Rail Event');
execute upsert_tx(1019, 'az', 'Remark for Order taking (Header)');
execute upsert_tx(1020, 'az', 'Delivery note text (Header)');
execute upsert_tx(1021, 'az', 'Remark for Scheduling (Header)');
execute upsert_tx(1022, 'az', 'Remark for Driver (Header)');
execute upsert_tx(1023, 'az', 'Remark for Shipping (Header)');
execute upsert_tx(1024, 'az', 'Carrier delivery note');
execute upsert_tx(1025, 'az', 'Carrier truck driver note');
execute upsert_tx(1026, 'az', 'Customer Delivery Note Comment');
execute upsert_tx(1027, 'az', 'Flag "On-Hold order"');
execute upsert_tx(1028, 'az', 'Production Plant');
execute upsert_tx(1029, 'az', 'Ordered Quantity UOM');
execute upsert_tx(1030, 'az', 'Ordered Quantity');
execute upsert_tx(1031, 'az', 'Loaded Quantity UOM');
execute upsert_tx(1032, 'az', 'Loaded Quantity');
execute upsert_tx(1033, 'az', 'Returned Quantity UOM');
execute upsert_tx(1034, 'az', 'Returned Quantity');
execute upsert_tx(1035, 'az', 'Delivered Quantity UOM');
execute upsert_tx(1036, 'az', 'Delivered Quantity');
execute upsert_tx(1037, 'az', 'Loading Point');
execute upsert_tx(1038, 'az', 'Customer Silo');
execute upsert_tx(1039, 'az', 'Remark for Order taking (Item)');
execute upsert_tx(1040, 'az', 'Remark for Scheduling (Item)');
execute upsert_tx(1041, 'az', 'Remark for Driver (Item)');
execute upsert_tx(1042, 'az', 'Remark for Shipping (Item)');
execute upsert_tx(1043, 'az', 'Executing Carrier');
execute upsert_tx(1044, 'az', 'Return reason code');
execute upsert_tx(1045, 'az', 'Special processing indicator');
execute upsert_tx(1046, 'az', 'iTrack status');

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - HR (ENGLISH COPY)
execute upsert_tx(1000, 'hr', 'Status narudžbe');
execute upsert_tx(1001, 'hr', 'Odredište');
execute upsert_tx(1002, 'hr', 'Kupac');
execute upsert_tx(1003, 'hr', 'Primatelj računa');
execute upsert_tx(1004, 'hr', 'Segment');
execute upsert_tx(1005, 'hr', 'Organizator prijevoza');
execute upsert_tx(1006, 'hr', 'Odredište');
execute upsert_tx(1007, 'hr', 'Vrsta isporuke');
execute upsert_tx(1008, 'hr', 'Isporuka od dana');
execute upsert_tx(1009, 'hr', 'Isporuka na dan');
execute upsert_tx(1010, 'hr', 'Zastava "Izdanje SAP"');
execute upsert_tx(1011, 'hr', 'Zastava "gratis Kupac"');
execute upsert_tx(1012, 'hr', 'No Pda or Rail Event');
execute upsert_tx(1019, 'hr', 'Napomena za Preuzimanje narudžbe (Zaglavlje)');
execute upsert_tx(1020, 'hr', 'Tekst na Otpremnici (Zaglavlje)');
execute upsert_tx(1021, 'hr', 'Napomena za planiranje (Zaglavlje)');
execute upsert_tx(1022, 'hr', 'Napomena za Vozača (Zaglavlje)');
execute upsert_tx(1023, 'hr', 'Napomena za isporuku (Zaglavlje)');
execute upsert_tx(1024, 'hr', 'Izdatnica projevoznika');
execute upsert_tx(1025, 'hr', 'Izdatnica projevoznika');
execute upsert_tx(1026, 'hr', 'Kupčev komentar na izdatnici');
execute upsert_tx(1027, 'hr', 'Označi "na čekanju"');
execute upsert_tx(1028, 'hr', 'Proizvodni pogon');
execute upsert_tx(1029, 'hr', 'Naručena količina UOM');
execute upsert_tx(1030, 'hr', 'Naručena količina');
execute upsert_tx(1031, 'hr', 'Utovarena Količina UOM');
execute upsert_tx(1032, 'hr', 'Utovarena Količina');
execute upsert_tx(1033, 'hr', 'Vraćena Količina UOM');
execute upsert_tx(1034, 'hr', 'Vraćena Količina');
execute upsert_tx(1035, 'hr', 'Isporučena Količina UOM');
execute upsert_tx(1036, 'hr', 'Isporučena Količina');
execute upsert_tx(1037, 'hr', 'Utovarna stanica');
execute upsert_tx(1038, 'hr', 'Kupac Silos');
execute upsert_tx(1039, 'hr', 'Napomena za uzimanje reda (Stavka)');
execute upsert_tx(1040, 'hr', 'Napomena za zakazivanje (Stavka)');
execute upsert_tx(1041, 'hr', 'Napomena za Vozač (Stavka)');
execute upsert_tx(1042, 'hr', 'Napomena za utovar (Stavka)');
execute upsert_tx(1043, 'hr', 'Izvršavanje prijevoznik');
execute upsert_tx(1044, 'hr', 'Povratak razlog kod');
execute upsert_tx(1045, 'hr', 'Posebna obrada indikator');
execute upsert_tx(1046, 'hr', 'iTrack status');

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - SR (ENGLISH COPY)
execute upsert_tx(1000, 'sr', 'Status naloga');
execute upsert_tx(1001, 'sr', 'Mesto isporuke');
execute upsert_tx(1002, 'sr', 'Kupac');
execute upsert_tx(1003, 'sr', 'Uplatilac');
execute upsert_tx(1004, 'sr', 'Segment');
execute upsert_tx(1005, 'sr', 'Prevoznik');
execute upsert_tx(1006, 'sr', 'Odredište');
execute upsert_tx(1007, 'sr', 'Tip isporuke');
execute upsert_tx(1008, 'sr', 'Isporučeno od dana');
execute upsert_tx(1009, 'sr', 'Isporučeno do dana');
execute upsert_tx(1010, 'sr', 'Oznaka "Release SAP"');
execute upsert_tx(1011, 'sr', 'Oznaka "Free of charge customer" ');
execute upsert_tx(1012, 'sr', 'No Pda or Rail Event');
execute upsert_tx(1019, 'sr', 'Napomena za otvaranje naloga (Zaglavlje)');
execute upsert_tx(1020, 'sr', 'Tekst na otpremnici (Zaglavlje)');
execute upsert_tx(1021, 'sr', 'Napomena za raspoređivanje naloga (Zaglavlje)');
execute upsert_tx(1022, 'sr', 'Napomena za vozača (Zaglavlje)');
execute upsert_tx(1023, 'sr', 'Napomena za isporuku (Zaglavlje)');
execute upsert_tx(1024, 'sr', 'Otpremnica za prevoznika');
execute upsert_tx(1025, 'sr', 'Otpremnica za vozača');
execute upsert_tx(1026, 'sr', 'Napomena za kupčevu Otpremnicu ');
execute upsert_tx(1027, 'sr', 'Oznaka "nalog na čekanju"');
execute upsert_tx(1028, 'sr', 'Fabrika');
execute upsert_tx(1029, 'sr', 'Naručena količina JM');
execute upsert_tx(1030, 'sr', 'Naručena količina');
execute upsert_tx(1031, 'sr', 'Utovarena Količina JM');
execute upsert_tx(1032, 'sr', 'Utovarena Količina');
execute upsert_tx(1033, 'sr', 'Vraćena Količina JM');
execute upsert_tx(1034, 'sr', 'Vraćena Količina');
execute upsert_tx(1035, 'sr', 'Isporučena količina JM');
execute upsert_tx(1036, 'sr', 'Isporučena količina');
execute upsert_tx(1037, 'sr', 'Utovarna stanica');
execute upsert_tx(1038, 'sr', 'Kupčev silos');
execute upsert_tx(1039, 'sr', 'Napomena za otvaranje naloga (Stavka)');
execute upsert_tx(1040, 'sr', 'Napomena za respoređivanje naloga (Stavka)');
execute upsert_tx(1041, 'sr', 'Napomena za vozača (Stavka)');
execute upsert_tx(1042, 'sr', 'Napomena za isporuku (Stavka)');
execute upsert_tx(1043, 'sr', 'Prevoznik');
execute upsert_tx(1044, 'sr', 'Kod razloga za povraćaj robe');
execute upsert_tx(1045, 'sr', 'SPI');
execute upsert_tx(1046, 'sr', 'iTrack status');

prompt INSERTING into TLGNR_AUDIT_BIZEVENT_CONFIG_TX - TR
execute upsert_tx(1000, 'tr', 'Order status');
execute upsert_tx(1001, 'tr', 'ShipTo');
execute upsert_tx(1002, 'tr', 'SoldTo');
execute upsert_tx(1003, 'tr', 'BillTo');
execute upsert_tx(1004, 'tr', 'Pool');
execute upsert_tx(1005, 'tr', 'Commercial Carrier');
execute upsert_tx(1006, 'tr', 'Shipping point');
execute upsert_tx(1007, 'tr', 'Delivery type');
execute upsert_tx(1008, 'tr', 'Delivery from date');
execute upsert_tx(1009, 'tr', 'Delivery to date');
execute upsert_tx(1010, 'tr', 'Flag "Release SAP"');
execute upsert_tx(1011, 'tr', 'Flag "Free of Charge Customer"');
execute upsert_tx(1012, 'tr', 'No Pda or Rail Event');
execute upsert_tx(1019, 'tr', 'Remark for Order taking (Header)');
execute upsert_tx(1020, 'tr', 'Delivery note text (Header)');
execute upsert_tx(1021, 'tr', 'Remark for Scheduling (Header)');
execute upsert_tx(1022, 'tr', 'Remark for Driver (Header)');
execute upsert_tx(1023, 'tr', 'Remark for Shipping (Header)');
execute upsert_tx(1024, 'tr', 'Carrier delivery note');
execute upsert_tx(1025, 'tr', 'Carrier truck driver note');
execute upsert_tx(1026, 'tr', 'Customer Delivery Note Comment');
execute upsert_tx(1027, 'tr', 'Flag "On-Hold order"');
execute upsert_tx(1028, 'tr', 'Production Plant');
execute upsert_tx(1029, 'tr', 'Ordered Quantity UOM');
execute upsert_tx(1030, 'tr', 'Ordered Quantity');
execute upsert_tx(1031, 'tr', 'Loaded Quantity UOM');
execute upsert_tx(1032, 'tr', 'Loaded Quantity');
execute upsert_tx(1033, 'tr', 'Returned Quantity UOM');
execute upsert_tx(1034, 'tr', 'Returned Quantity');
execute upsert_tx(1035, 'tr', 'Delivered Quantity UOM');
execute upsert_tx(1036, 'tr', 'Delivered Quantity');
execute upsert_tx(1037, 'tr', 'Loading Point');
execute upsert_tx(1038, 'tr', 'Customer Silo');
execute upsert_tx(1039, 'tr', 'Remark for Order taking (Item)');
execute upsert_tx(1040, 'tr', 'Remark for Scheduling (Item)');
execute upsert_tx(1041, 'tr', 'Remark for Driver (Item)');
execute upsert_tx(1042, 'tr', 'Remark for Shipping (Item)');
execute upsert_tx(1043, 'tr', 'Executing Carrier');
execute upsert_tx(1044, 'tr', 'Return reason code');
execute upsert_tx(1045, 'tr', 'Special processing indicator');
execute upsert_tx(1046, 'tr', 'iTrack status');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

